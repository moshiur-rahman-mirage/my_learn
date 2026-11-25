@@ 3 | TOPIC | Practice — Coding drill: Diagnose the JVM | Eleven tasks on class initialization, visibility, leaks, deadlocks, GC logs, thread dumps, flags and JFR | MUST
# Coding drill: Diagnose the JVM

Eleven tasks in the style of senior interviews: predict what the JVM does, read real diagnostic output, and fix the code. Several of them are exactly what you would do during a production incident on OrderHub. Try each before opening the solution.

## 1. Predict the output: initialization order
```java
public class InitOrder {
    static class Base {
        static { System.out.println("1 Base static"); }
        { System.out.println("3 Base instance block"); }
        Base() { System.out.println("4 Base constructor"); describe(); }
        void describe() { System.out.println("Base describe"); }
    }

    static class Order extends Base {
        static { System.out.println("2 Order static"); }
        private String id = "ORD-1";
        { System.out.println("5 Order instance block, id=" + id); }
        Order() { System.out.println("6 Order constructor"); }
        @Override void describe() { System.out.println("Order describe, id=" + id); }
    }

    public static void main(String[] args) {
        new Order();
        System.out.println("--- second instance");
        new Order();
    }
}
```
Hint: static initializers run once, parent before child; instance initializers and field assignments run as part of each constructor, after the super constructor returns.

<details><summary>Solution</summary>

```text
1 Base static
2 Order static
3 Base instance block
4 Base constructor
Order describe, id=null
5 Order instance block, id=ORD-1
6 Order constructor
--- second instance
3 Base instance block
4 Base constructor
Order describe, id=null
5 Order instance block, id=ORD-1
6 Order constructor
```
Class initialization (`<clinit>`) happens once per class, parent first. Each `new Order()` runs `Base`'s constructor first; `Order`'s field initializer (`id = "ORD-1"`) and instance block run only after `super()` returns. Calling an overridable method (`describe()`) from the `Base` constructor therefore sees `id == null`: the classic reason never to call overridable methods from constructors.
</details>

## 2. Fix the visibility bug
```java
class OrderPoller implements Runnable {
    private boolean running = true;
    public void run() { while (running) { pollOutbox(); } }
    public void stop() { running = false; }
    private void pollOutbox() { /* reads a table, no synchronization */ }
}
```
In production `stop()` sometimes never stops the poller. Why, and what are two fixes?

<details><summary>Solution</summary>

```java
class OrderPoller implements Runnable {
    private volatile boolean running = true;      // fix 1: volatile creates happens-before
    public void run() { while (running && !Thread.currentThread().isInterrupted()) { pollOutbox(); } }
    public void stop() { running = false; }
    private void pollOutbox() { }
}
```
Without `volatile` there is no happens-before between the write in `stop()` and the read in `run()`; once the JIT inlines `pollOutbox` and sees no synchronization, it may hoist the read of `running` out of the loop. Fix 1: `volatile`. Fix 2: use interruption (`thread.interrupt()` and check `isInterrupted()`), which is also how `ExecutorService.shutdownNow()` stops tasks. In Spring, prefer a `@Scheduled` method so the framework manages the thread.
</details>

## 3. Read the GC log
```text
[3600.101s][info][gc] GC(901) Pause Young (Normal) (G1 Evacuation Pause) 1890M->1720M(2048M) 48.211ms
[3601.322s][info][gc] GC(902) Pause Young (Concurrent Start) (G1 Humongous Allocation) 1950M->1801M(2048M) 61.004ms
[3603.010s][info][gc] GC(903) To-space exhausted
[3603.011s][info][gc] GC(903) Pause Young (Normal) (G1 Evacuation Pause) 2040M->2010M(2048M) 210.551ms
[3603.890s][info][gc] GC(904) Pause Full (G1 Compaction Pause) 2047M->1650M(2048M) 2841.330ms
```
What is happening, and what do you do?

<details><summary>Solution</summary>

```text
- After young GCs the heap stays at 1.7 to 1.8 GB of 2 GB: the live set is about 85% of the heap. G1 has no room to work.
- "G1 Humongous Allocation" shows large arrays (over half a region) being allocated.
- "To-space exhausted" means G1 could not find free regions to copy survivors into (evacuation failure).
- The result is a 2.8-second Full GC, freeing only about 400 MB.
```
Actions: check whether the live set is legitimately large (heap too small: raise it so the live set is a third to a quarter of the heap) or growing over time (leak: heap dump and MAT). Find the humongous allocations with JFR (`jdk.ObjectAllocationOutsideTLAB` events with large sizes) and either stream the data or raise `-XX:G1HeapRegionSize`. Alert on "Pause Full" in logs.
</details>

## 4. Find and fix the ThreadLocal leak
```java
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantFilter extends OncePerRequestFilter {
    public static final ThreadLocal<TenantContext> CURRENT = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        CURRENT.set(loadTenant(req.getHeader("X-Tenant")));    // TenantContext holds a 2 MB config cache
        chain.doFilter(req, res);
    }
}
```
Hint: what happens to the value after the request finishes, and what happens on the next request on the same Tomcat thread if loading the tenant fails?

<details><summary>Solution</summary>

```java
@Override
protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {
    CURRENT.set(loadTenant(req.getHeader("X-Tenant")));
    try {
        chain.doFilter(req, res);
    } finally {
        CURRENT.remove();                                       // always clean up pooled threads
    }
}
```
Two bugs: memory (each of up to 200 Tomcat threads keeps a 2 MB context forever, 400 MB pinned) and correctness (if `loadTenant` throws or a later code path skips the set, the next request on that thread reads the previous tenant's context, a data leak between customers). `finally { remove(); }` fixes both. In a heap dump this shows up as large `TenantContext` objects retained via `Thread.threadLocals` of `http-nio-8080-exec-*` threads.
</details>

## 5. Replace an unbounded cache with an LRU cache
```java
static final Map<Long, ProductDto> CACHE = new HashMap<>();   // grows with every product ever viewed
```
Write a thread-safe, size-bounded LRU cache using only the JDK.

<details><summary>Solution</summary>

```java
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCacheDemo {

    static <K, V> Map<K, V> lruCache(int maxEntries) {
        return Collections.synchronizedMap(new LinkedHashMap<K, V>(16, 0.75f, true) {   // access order
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        });
    }

    public static void main(String[] args) {
        Map<Long, String> cache = lruCache(3);
        cache.put(1L, "Keyboard");
        cache.put(2L, "Mouse");
        cache.put(3L, "Monitor");
        cache.get(1L);                       // 1 becomes most recently used
        cache.put(4L, "Laptop");             // evicts 2, the least recently used
        System.out.println(cache.keySet());
    }
}
```
```text
[3, 1, 4]
```
`accessOrder = true` moves an entry to the end on every `get`, and `removeEldestEntry` evicts the head when the size limit is exceeded. `Collections.synchronizedMap` makes it thread-safe with a single lock; `get` also mutates the order, so a read lock would not be enough. In production use Caffeine, which adds expiry, statistics and much better concurrency.
</details>

## 6. Fix the deadlock
Two threads transfer money between the same two accounts in opposite directions, each locking `from` then `to`, and the service hangs. A thread dump shows "Found one Java-level deadlock". Fix it without a global lock.

<details><summary>Solution</summary>

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeadlockFix {

    static final class Account {
        final long id;
        long balance;
        Account(long id, long balance) { this.id = id; this.balance = balance; }
    }

    // Always lock the account with the smaller id first: a global lock order makes cycles impossible
    static void transfer(Account from, Account to, long amount) {
        Account first = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Account a = new Account(1, 1_000_000);
        Account b = new Account(2, 1_000_000);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> { for (int i = 0; i < 100_000; i++) transfer(a, b, 1); });
        pool.submit(() -> { for (int i = 0; i < 100_000; i++) transfer(b, a, 1); });
        pool.shutdown();
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("finished without deadlock: " + finished);
        System.out.println("total money: " + (a.balance + b.balance));
    }
}
```
```text
finished without deadlock: true
total money: 2000000
```
A deadlock needs a cycle in the wait-for graph. Imposing a global order on lock acquisition (smaller id first) makes a cycle impossible. Alternatives: `ReentrantLock.tryLock(timeout)` with retry and back-off, or a single-writer design (a queue per account). In databases the same idea applies: update rows in a consistent order to avoid deadlocks.
</details>

## 7. Choose JVM flags for a container
OrderHub runs in Kubernetes with `limits.memory: 2Gi`, `requests.cpu: 1`, a p99 target of 100 ms, and the team wants evidence after any crash. Write the JVM options.

<details><summary>Solution</summary>

```bash
JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=75 \
  -XX:+UseG1GC -XX:MaxGCPauseMillis=100 \
  -XX:ActiveProcessorCount=2 \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps -XX:+ExitOnOutOfMemoryError \
  -XX:MaxMetaspaceSize=256m \
  -Xlog:gc*:file=/logs/gc.log:time,uptime,level,tags:filecount=5,filesize=20m \
  -XX:StartFlightRecording=disk=true,maxage=30m,settings=default,dumponexit=true,filename=/dumps/exit.jfr"
```
A 1.5 GiB heap leaves about 500 MB for Metaspace, code cache, threads and buffers. G1 is set explicitly because with 1 CPU and 2 GiB the JVM's ergonomics would pick Serial GC; `ActiveProcessorCount=2` keeps G1 and the common pool from being single-threaded (useful when the CPU limit allows bursting). Heap dumps and the JFR rolling buffer go to a mounted volume; `ExitOnOutOfMemoryError` lets Kubernetes restart a clean pod.
</details>

## 8. Read the thread dump
Three dumps, 5 seconds apart, all show 200 of 200 Tomcat threads like this, and CPU is at 3%:
```text
"http-nio-8080-exec-88" #131 daemon prio=5 os_prio=0 tid=0x00007f nid=0x4f21 waiting on condition
   java.lang.Thread.State: TIMED_WAITING (parking)
	at jdk.internal.misc.Unsafe.park(java.base@21/Native Method)
	at java.util.concurrent.locks.LockSupport.parkNanos(java.base@21/LockSupport.java:269)
	at com.zaxxer.hikari.util.ConcurrentBag.borrow(ConcurrentBag.java:151)
	at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:181)
	at org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.getConnection
	at com.orderhub.report.ReportService.dailyRevenue(ReportService.java:41)
```
and 10 threads named `report-exec-*` are `RUNNABLE` in `sun.nio.ch.NioSocketImpl.read` under `ReportService.exportToS3`. What happened?

<details><summary>Solution</summary>

```text
- The Hikari pool size is 10; all 10 connections are held by report-exec threads.
- Those threads are inside a transaction (connection held) while uploading to S3 over the network.
- Every request thread now waits for a connection (TIMED_WAITING in HikariPool.getConnection); CPU is idle.
- RUNNABLE in a socket read means waiting for network I/O, not using CPU.
```
Fix: do not hold a database transaction during remote I/O. Read the data in a short `@Transactional(readOnly = true)` method, release the connection, then upload. Add timeouts on the S3 client, use a separate bounded pool for reports, and set `spring.datasource.hikari.leak-detection-threshold=20000` to log where long-held connections were borrowed.
</details>

## 9. Turn deep recursion into iteration
An import job computes the depth of a category tree recursively and crashes with `StackOverflowError` on one customer's data. Fix it without raising `-Xss`.

<details><summary>Solution</summary>

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class IterativeDepth {

    static final class Category {
        final String name;
        final List<Category> children = new ArrayList<>();
        Category(String name) { this.name = name; }
    }

    static int depthRecursive(Category c) {
        int max = 0;
        for (Category child : c.children) max = Math.max(max, depthRecursive(child));
        return max + 1;
    }

    record Frame(Category node, int depth) { }

    static int depthIterative(Category root) {
        Deque<Frame> stack = new ArrayDeque<>();          // explicit stack on the heap
        stack.push(new Frame(root, 1));
        int max = 0;
        while (!stack.isEmpty()) {
            Frame f = stack.pop();
            max = Math.max(max, f.depth());
            for (Category child : f.node().children) stack.push(new Frame(child, f.depth() + 1));
        }
        return max;
    }

    public static void main(String[] args) {
        Category root = new Category("root");
        Category current = root;
        for (int i = 0; i < 200_000; i++) {               // a degenerate, very deep tree (bad import data)
            Category next = new Category("c" + i);
            current.children.add(next);
            current = next;
        }
        try {
            System.out.println("recursive: " + depthRecursive(root));
        } catch (StackOverflowError e) {
            System.out.println("recursive: StackOverflowError");
        }
        System.out.println("iterative: " + depthIterative(root));
    }
}
```
```text
recursive: StackOverflowError
iterative: 200001
```
Each recursive call uses a stack frame, and thread stacks are small (1 MB by default), so pathological input depth overflows. An explicit `Deque` on the heap is limited only by heap size. Raising `-Xss` would affect every thread and only move the limit. Also validate input: a category tree 200,000 levels deep is almost certainly bad data.
</details>

## 10. Add a custom JFR event to OrderHub
Record how long order placement takes as a custom JFR event, so it appears next to GC and lock events in JDK Mission Control.

<details><summary>Solution</summary>

```java
import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import java.nio.file.Files;
import java.nio.file.Path;

public class OrderPlacedEvent {

    @Name("com.orderhub.OrderPlaced")
    @Label("Order Placed")
    @Category("OrderHub")
    static class OrderPlaced extends Event {
        @Label("Order Id") long orderId;
        @Label("Lines") int lines;
    }

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("orderhub", ".jfr");
        try (Recording recording = new Recording()) {
            recording.enable("com.orderhub.OrderPlaced");
            recording.start();
            for (long id = 1; id <= 3; id++) {
                OrderPlaced event = new OrderPlaced();
                event.begin();                       // start timing
                event.orderId = id;
                event.lines = (int) id * 2;
                Thread.sleep(10);                    // the work being measured
                event.commit();                      // end timing and record (no-op if not enabled)
            }
            recording.stop();
            recording.dump(file);
        }
        for (RecordedEvent e : RecordingFile.readAllEvents(file)) {
            System.out.println(e.getEventType().getLabel() + ": order " + e.getLong("orderId")
                    + ", lines " + e.getInt("lines") + ", took at least 10 ms: " + (e.getDuration().toMillis() >= 10));
        }
        Files.delete(file);
    }
}
```
```text
Order Placed: order 1, lines 2, took at least 10 ms: true
Order Placed: order 2, lines 4, took at least 10 ms: true
Order Placed: order 3, lines 6, took at least 10 ms: true
```
Custom events extend `jdk.jfr.Event`; `begin()` and `commit()` measure duration, and `commit()` is nearly free when no recording has the event enabled. In OrderHub, create the event in `OrderService.place` and run with `-XX:StartFlightRecording`; in JMC you can then correlate slow orders with GC pauses or lock contention at the same moment. The program uses the `Recording` API only to make the demo self-contained.
</details>

## 11. Match the error to the cause
For each message, name the memory area and the most likely cause in a Spring Boot service:
```text
a) java.lang.OutOfMemoryError: Java heap space
b) java.lang.OutOfMemoryError: Metaspace
c) java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
d) java.lang.OutOfMemoryError: Direct buffer memory
e) Pod status OOMKilled, exit code 137, no Java error in the logs
f) java.lang.StackOverflowError
```

<details><summary>Solution</summary>

```text
a) Heap: a leak (unbounded cache, ThreadLocal, listener) or a heap too small for the live set; also loading huge result sets into memory.
b) Metaspace: too many classes, for example proxies or scripts generated per request, or class loader leaks after redeploys.
c) Native threads: unbounded thread creation (new Thread per task, newCachedThreadPool under load) or a low pids or ulimit setting.
d) Direct memory: Netty or NIO buffers not released, or MaxDirectMemorySize too small for the workload.
e) Container: total process memory (heap plus non-heap) exceeded the limit; heap set too close to the limit.
f) Thread stack: unbounded or very deep recursion (bad data, or bidirectional JPA relations in toString or JSON serialization).
```
Item f in Spring apps is very often Jackson or Lombok `toString` recursing through a bidirectional `@OneToMany` and `@ManyToOne` pair: fix with DTOs, `@JsonManagedReference` and `@JsonBackReference`, or by excluding the back reference from `toString`.
</details>

@@ 3 | TOPIC | Practice — Phase 15 review: JVM Internals | Cheat sheet, key snippets and commands, top 10 questions and rapid-fire flashcards for JVM internals and performance | MUST
# Phase 15 review: JVM Internals

## Cheat sheet

| Idea | Remember |
|---|---|
| Class loading phases | Load, link (verify, prepare, resolve), initialize on first active use |
| Loaders | Bootstrap (null), platform, app; parent delegation |
| Class identity | Name plus defining loader; unloaded only with its loader |
| `NoClassDefFoundError` | Missing at runtime, or a static initializer failed earlier |
| Heap | Objects, shared, GC-managed; young (Eden, S0, S1) and old |
| Stack | Per thread, frames with locals; `StackOverflowError`; `-Xss` |
| Metaspace | Class metadata in native memory, replaced PermGen in Java 8 |
| Container heap default | 25% of the limit; set `MaxRAMPercentage` to about 75 |
| OOM-killed (137) | Total RSS over the limit, not a Java exception |
| JIT | Interpreter, C1 (profiling), C2 (optimized); deoptimization; warm-up |
| Escape analysis | Non-escaping objects scalar replaced, locks elided |
| JMM | Happens-before: lock, volatile, start, join, final fields, transitivity |
| `volatile` | Visibility and ordering, not an atomic `++` |
| GC roots | Stacks, statics, JNI references, monitors; cycles are collected |
| Generational | Most objects die young; young GC cost depends on survivors |
| Collectors | G1 default; Parallel for throughput; ZGC and Shenandoah for sub-ms pauses; CMS removed in 14 |
| Generational ZGC | Java 21 opt-in, Java 23 default, Java 24 only mode |
| Key flags | `MaxRAMPercentage`, `UseG1GC` or `UseZGC`, `MaxGCPauseMillis`, `-Xlog:gc*`, heap dump and exit on OOM |
| Leak | Reachable but unneeded; old gen after GC keeps rising |
| Tools | `jcmd` Thread.print, GC.heap_dump, GC.class_histogram, VM.flags, JFR.start; MAT; JMC |

### Key code snippets
```java
// Safe lazy singleton without locks: class initialization is thread-safe
final class Registry {
    private Registry() { }
    private static final class Holder { static final Registry INSTANCE = new Registry(); }
    static Registry getInstance() { return Holder.INSTANCE; }
}
```

```java
// Double-checked locking done right
private static volatile Config instance;
static Config config() {
    Config c = instance;
    if (c == null) {
        synchronized (Config.class) {
            c = instance;
            if (c == null) instance = c = new Config();
        }
    }
    return c;
}
```

```java
// ThreadLocal hygiene on pooled threads
CONTEXT.set(value);
try {
    chain.doFilter(request, response);
} finally {
    CONTEXT.remove();
}
```

```java
// Bounded cache instead of a static HashMap
Cache<Long, ProductDto> products = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build();
```

```bash
# Production JVM options and diagnostics
JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Xlog:gc*:file=/logs/gc.log:time,uptime,level,tags \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps -XX:+ExitOnOutOfMemoryError"
jcmd <pid> Thread.print
jcmd <pid> GC.class_histogram
jcmd <pid> GC.heap_dump /dumps/now.hprof
jcmd <pid> JFR.start name=incident settings=profile maxage=10m
jcmd <pid> JFR.dump name=incident filename=/dumps/incident.jfr
```

## The 10 most likely interview questions

1. **Explain the JVM memory areas.** Heap (objects, young and old generations), per-thread stacks (frames, locals), Metaspace (class metadata), code cache and direct memory; the container limit must cover all of them.
2. **How does garbage collection work?** Trace reachability from GC roots; copy survivors in the young generation, mark and compact the old; stop-the-world phases vary by collector.
3. **Which collector would you choose?** G1 by default; Parallel for batch throughput; generational ZGC for strict tail latency or huge heaps.
4. **What are class loaders and parent delegation?** Bootstrap, platform and app loaders; ask the parent first to protect core classes; class identity includes the loader.
5. **What is the Java Memory Model?** Rules for cross-thread visibility and ordering, based on happens-before.
6. **Why is `volatile` needed in double-checked locking?** To prevent publishing a reference before the object is fully constructed.
7. **What is the JIT and why do services need warm-up?** Profile-guided compilation of hot code (C1 then C2); code starts interpreted.
8. **How do you find a memory leak?** Confirm old-gen-after-GC growth, take heap dumps, use the MAT dominator tree and paths to GC roots, fix the retainer.
9. **Why was a pod OOM-killed with a healthy heap?** Non-heap memory pushed RSS over the limit; size the heap at about 75% and check Native Memory Tracking.
10. **How do you debug a hung service?** Several thread dumps: look for deadlocks, BLOCKED threads, pool exhaustion and threads stuck on I/O without timeouts.

## Rapid-fire flashcards

Q: Which class loader loads java.lang.String, and how does it appear in Java code?
A: The bootstrap loader; `String.class.getClassLoader()` returns null.

Q: Does reading a static final compile-time constant initialize its class?
A: No, the constant is inlined by the compiler.

Q: What is the default thread stack size (-Xss) on 64-bit Linux?
A: 1 MB.

Q: What is a TLAB in the JVM?
A: A thread-local allocation buffer in Eden that makes allocation a lock-free pointer bump.

Q: What does "made not entrant" mean in -XX:+PrintCompilation output?
A: That compiled version was invalidated (replaced or deoptimized) and new calls no longer enter it.

Q: Is count++ on a volatile int atomic?
A: No, it is a read followed by a write.

Q: What is G1's default pause time goal?
A: 200 ms (`-XX:MaxGCPauseMillis=200`).

Q: What is a humongous object in G1?
A: An object larger than half a region, allocated directly in contiguous old-gen regions.

Q: Which GC log message signals a G1 evacuation failure?
A: "To-space exhausted", often followed by a Full GC.

Q: Which jcmd command shows instance counts per class?
A: `jcmd <pid> GC.class_histogram`.

Q: What overhead does Java Flight Recorder have with default settings?
A: About 1%.

Q: Which exit code indicates a container OOM kill?
A: 137 (SIGKILL).
