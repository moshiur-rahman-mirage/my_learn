@@ 3 | TOPIC | Practice — Coding drill: Classic concurrency problems | Twelve interview tasks: alternating printers, producer-consumer, dining philosophers, singletons, rate limiters, memoizers and a mini thread pool | MUST
# Coding drill: Classic concurrency problems

Twelve problems that appear again and again in Java interviews, from "print odd and even with two threads" to "build a thread pool". For each: read the task, try it for 10-20 minutes, then open the solution. Every solution is a complete Java 21 class you can run.

## 1. Print odd and even numbers alternately with two threads
Thread "odd" prints 1, 3, 5 and thread "even" prints 2, 4, 6, up to 10, strictly in order.

Hint: two semaphores, one starting with a permit, hand the turn back and forth.

<details><summary>Solution</summary>

```java
import java.util.concurrent.Semaphore;

public class OddEven {
    public static void main(String[] args) throws InterruptedException {
        Semaphore oddTurn = new Semaphore(1);    // odd goes first
        Semaphore evenTurn = new Semaphore(0);
        int max = 10;
        Thread odd = new Thread(() -> {
            for (int i = 1; i <= max; i += 2) {
                oddTurn.acquireUninterruptibly();
                System.out.println("odd:  " + i);
                evenTurn.release();               // hand the turn over
            }
        });
        Thread even = new Thread(() -> {
            for (int i = 2; i <= max; i += 2) {
                evenTurn.acquireUninterruptibly();
                System.out.println("even: " + i);
                oddTurn.release();
            }
        });
        odd.start(); even.start();
        odd.join(); even.join();
    }
}
```
Output:
```text
odd:  1
even: 2
odd:  3
even: 4
odd:  5
even: 6
odd:  7
even: 8
odd:  9
even: 10
```
Each semaphore is a baton: a thread can only print while holding its own permit and then gives the other thread a permit. The same pattern prints "foo bar" alternately or A, B, C in rotation with three semaphores.
</details>

## 2. Three threads print A, B, C in order, 3 rounds, using one lock
Output `ABCABCABC` using `ReentrantLock` and `Condition`s (no semaphores).

Hint: a shared `turn` variable and one condition per thread.

<details><summary>Solution</summary>

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AbcPrinter {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition[] turnOf = { lock.newCondition(), lock.newCondition(), lock.newCondition() };
    private int turn = 0;                                   // guarded by lock
    private final StringBuilder out = new StringBuilder();  // guarded by lock

    void run(int me, char letter, int rounds) {
        for (int r = 0; r < rounds; r++) {
            lock.lock();
            try {
                while (turn != me) turnOf[me].awaitUninterruptibly();
                out.append(letter);
                turn = (turn + 1) % 3;
                turnOf[turn].signal();                       // wake exactly the next thread
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AbcPrinter p = new AbcPrinter();
        Thread a = new Thread(() -> p.run(0, 'A', 3));
        Thread b = new Thread(() -> p.run(1, 'B', 3));
        Thread c = new Thread(() -> p.run(2, 'C', 3));
        a.start(); b.start(); c.start();
        a.join(); b.join(); c.join();
        System.out.println(p.out);
    }
}
```
Output:
```text
ABCABCABC
```
The `while` loop re-checks `turn` after every wakeup (spurious wakeups are allowed). Separate conditions avoid waking threads whose turn it is not, which `notifyAll` on a monitor would do.
</details>

## 3. Producer-consumer with a BlockingQueue and a poison pill
Two producers each produce 1..50; three consumers sum everything. Shut consumers down cleanly.

Hint: `ArrayBlockingQueue`, and one "poison pill" value per consumer.

<details><summary>Solution</summary>

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ProducerConsumer {
    private static final int POISON = -1;

    public static void main(String[] args) throws Exception {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);   // bounded: back-pressure
        AtomicLong total = new AtomicLong();
        ExecutorService pool = Executors.newFixedThreadPool(5);

        Future<?> p1 = pool.submit(() -> { for (int i = 1; i <= 50; i++) queue.put(i); return null; });
        Future<?> p2 = pool.submit(() -> { for (int i = 1; i <= 50; i++) queue.put(i); return null; });
        for (int c = 0; c < 3; c++) {
            pool.submit(() -> {
                while (true) {
                    int item = queue.take();
                    if (item == POISON) return null;            // clean exit
                    total.addAndGet(item);
                }
            });
        }
        p1.get(); p2.get();                                     // wait until producers finish
        for (int c = 0; c < 3; c++) queue.put(POISON);          // one pill per consumer
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("total = " + total.get());
    }
}
```
Output:
```text
total = 2550
```
The bounded queue blocks producers when consumers fall behind (back-pressure). Pills are sent only after both producers finish, and one per consumer, because each consumer stops after taking one pill.
</details>

## 4. Dining philosophers without deadlock
Five philosophers, five forks (locks). Each eats 100 times. Prove there is no deadlock.

Hint: break the circular wait by always taking the lower-numbered fork first.

<details><summary>Solution</summary>

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    public static void main(String[] args) {
        int n = 5;
        ReentrantLock[] forks = new ReentrantLock[n];
        for (int i = 0; i < n; i++) forks[i] = new ReentrantLock();
        AtomicInteger meals = new AtomicInteger();

        try (ExecutorService pool = Executors.newFixedThreadPool(n)) {
            for (int p = 0; p < n; p++) {
                int left = p, right = (p + 1) % n;
                ReentrantLock first = forks[Math.min(left, right)];    // global lock order
                ReentrantLock second = forks[Math.max(left, right)];
                pool.submit(() -> {
                    for (int meal = 0; meal < 100; meal++) {
                        first.lock();
                        try {
                            second.lock();
                            try {
                                meals.incrementAndGet();              // eat
                            } finally {
                                second.unlock();
                            }
                        } finally {
                            first.unlock();
                        }
                    }
                });
            }
        }
        System.out.println("meals eaten = " + meals.get());
    }
}
```
Output:
```text
meals eaten = 500
```
Deadlock needs four conditions: mutual exclusion, hold and wait, no preemption, circular wait. Ordering lock acquisition removes circular wait: philosopher 4 takes fork 0 before fork 4, so the cycle cannot close. Alternatives: `tryLock` with back-off, or a `Semaphore(n - 1)` "waiter" so at most four philosophers sit at once.
</details>

## 5. Thread-safe lazy singleton: two correct ways
Implement a lazily created singleton with the holder idiom and with double-checked locking. Explain why `volatile` matters.

<details><summary>Solution</summary>

```java
public class Singletons {

    // 1. Initialization-on-demand holder: lazy, thread-safe, no synchronization cost
    static final class ConfigRegistry {
        private ConfigRegistry() { System.out.println("ConfigRegistry created"); }
        private static final class Holder {
            static final ConfigRegistry INSTANCE = new ConfigRegistry();   // class init is thread-safe
        }
        static ConfigRegistry getInstance() { return Holder.INSTANCE; }
    }

    // 2. Double-checked locking: volatile is mandatory
    static final class MetricsRegistry {
        private static volatile MetricsRegistry instance;
        private MetricsRegistry() { System.out.println("MetricsRegistry created"); }

        static MetricsRegistry getInstance() {
            MetricsRegistry local = instance;          // one volatile read on the fast path
            if (local == null) {
                synchronized (MetricsRegistry.class) {
                    local = instance;
                    if (local == null) {
                        instance = local = new MetricsRegistry();
                    }
                }
            }
            return local;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("before first use");
        Thread[] threads = new Thread[8];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> { ConfigRegistry.getInstance(); MetricsRegistry.getInstance(); });
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("same config instance: " + (ConfigRegistry.getInstance() == ConfigRegistry.getInstance()));
        System.out.println("same metrics instance: " + (MetricsRegistry.getInstance() == MetricsRegistry.getInstance()));
    }
}
```
Output (the two "created" lines can appear in either order):
```text
before first use
ConfigRegistry created
MetricsRegistry created
same config instance: true
same metrics instance: true
```
Without `volatile`, the JIT or CPU may publish the reference before the constructor's writes are visible, so another thread can see a non-null but half-initialized object. `volatile` gives the write-then-publish a happens-before edge. The holder idiom relies on the JVM's class initialization lock, which is simpler and just as lazy. An `enum` singleton is the third correct option (see [[Singleton Variants]]).
</details>

## 6. Fix the lost-update counter three ways
`count++` from 8 threads, 100,000 times each, prints less than 800,000. Fix it with `synchronized`, `AtomicLong` and `LongAdder`, and explain when each is best.

<details><summary>Solution</summary>

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class Counters {
    static long unsafeCount;
    static long syncCount;
    static final Object LOCK = new Object();
    static final AtomicLong atomic = new AtomicLong();
    static final LongAdder adder = new LongAdder();

    public static void main(String[] args) {
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            for (int t = 0; t < 8; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < 100_000; i++) {
                        unsafeCount++;                          // read, add, write: not atomic
                        synchronized (LOCK) { syncCount++; }
                        atomic.incrementAndGet();               // CAS loop
                        adder.increment();                      // striped cells, summed on read
                    }
                });
            }
        }
        System.out.println("unsafe below 800000: " + (unsafeCount < 800_000));
        System.out.println("synchronized = " + syncCount);
        System.out.println("AtomicLong   = " + atomic.get());
        System.out.println("LongAdder    = " + adder.sum());
    }
}
```
Output (the unsafe line is true on virtually every multi-core run):
```text
unsafe below 800000: true
synchronized = 800000
AtomicLong   = 800000
LongAdder    = 800000
```
`synchronized` is right when several fields change together. `AtomicLong` is best when you need the exact current value often (ids, sequence numbers) and contention is moderate. `LongAdder` spreads increments across cells to avoid CAS contention and is fastest for hot counters read rarely (metrics); its `sum()` is not an atomic snapshot.
</details>

## 7. Thread-safe token bucket rate limiter
Allow at most `capacity` requests in a burst and refill `ratePerSecond` tokens per second. `tryAcquire()` returns immediately. Make time injectable for testing.

Hint: compute refill lazily on each call from elapsed nanos.

<details><summary>Solution</summary>

```java
import java.util.function.LongSupplier;

public class TokenBucket {
    private final long capacity;
    private final double refillPerNano;
    private final LongSupplier nanoClock;
    private double tokens;           // guarded by this
    private long lastRefill;         // guarded by this

    public TokenBucket(long capacity, double ratePerSecond, LongSupplier nanoClock) {
        this.capacity = capacity;
        this.refillPerNano = ratePerSecond / 1_000_000_000.0;
        this.nanoClock = nanoClock;
        this.tokens = capacity;
        this.lastRefill = nanoClock.getAsLong();
    }

    public synchronized boolean tryAcquire() {
        long now = nanoClock.getAsLong();
        tokens = Math.min(capacity, tokens + (now - lastRefill) * refillPerNano);   // lazy refill
        lastRefill = now;
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        long[] fakeNow = {0};
        TokenBucket bucket = new TokenBucket(3, 2.0, () -> fakeNow[0]);   // burst 3, 2 per second
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(bucket.tryAcquire() ? "Y" : "N");
        System.out.println("burst of 5 at t=0s:  " + sb);
        fakeNow[0] = 1_000_000_000L;                                       // 1 second later
        sb.setLength(0);
        for (int i = 0; i < 3; i++) sb.append(bucket.tryAcquire() ? "Y" : "N");
        System.out.println("3 requests at t=1s:  " + sb);
    }
}
```
Output:
```text
burst of 5 at t=0s:  YYYNN
3 requests at t=1s:  YYN
```
`tokens` and `lastRefill` form one invariant, so both are guarded by the same lock. Refill is computed from elapsed time on demand, so there is no background thread. For a distributed limit across instances, the same algorithm runs in Redis (a Lua script) instead of memory.
</details>

## 8. Memoizer: compute each key exactly once under concurrency
Cache results of an expensive function so that concurrent callers asking for the same key wait for one computation instead of computing it twice.

Hint: store `Future`s in a `ConcurrentHashMap`, not values.

<details><summary>Solution</summary>

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Memoizer<K, V> {
    private final ConcurrentMap<K, CompletableFuture<V>> cache = new ConcurrentHashMap<>();
    private final Function<K, V> loader;

    public Memoizer(Function<K, V> loader) { this.loader = loader; }

    public V get(K key) {
        CompletableFuture<V> created = new CompletableFuture<>();
        CompletableFuture<V> existing = cache.putIfAbsent(key, created);   // atomic claim
        if (existing != null) return existing.join();                      // someone else computes
        try {
            V value = loader.apply(key);                                  // compute OUTSIDE map locks
            created.complete(value);
            return value;
        } catch (RuntimeException e) {
            cache.remove(key, created);                                   // allow a retry later
            created.completeExceptionally(e);
            throw e;
        }
    }

    public static void main(String[] args) {
        AtomicInteger computations = new AtomicInteger();
        Memoizer<String, Integer> lengths = new Memoizer<>(s -> {
            computations.incrementAndGet();
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return s.length();
        });
        AtomicInteger sum = new AtomicInteger();
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) pool.submit(() -> sum.addAndGet(lengths.get("concurrency")));
        }
        System.out.println("sum = " + sum.get() + ", computations = " + computations.get());
    }
}
```
Output:
```text
sum = 1100, computations = 1
```
`putIfAbsent` lets exactly one thread claim the key; the others get its future and wait. The slow computation runs outside the map, unlike `computeIfAbsent`, which holds the bin lock during the computation and blocks other keys in the same bin (and must not recurse into the same map). Failures are removed so the next caller can retry.
</details>

## 9. Find and fix the deadlock
This transfer method deadlocks when two threads transfer between the same accounts in opposite directions. Fix it without `tryLock`.

```java
void transfer(Account from, Account to, long amount) {
    synchronized (from) {
        synchronized (to) {
            from.balance -= amount;
            to.balance += amount;
        }
    }
}
```

<details><summary>Solution</summary>

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderedTransfer {
    static final class Account {
        final long id;
        long balance;               // guarded by this
        Account(long id, long balance) { this.id = id; this.balance = balance; }
    }

    static void transfer(Account from, Account to, long amount) {
        Account first = from.id < to.id ? from : to;    // global order by id
        Account second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    public static void main(String[] args) {
        Account a = new Account(1, 500), b = new Account(2, 500);
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            pool.submit(() -> { for (int i = 0; i < 100_000; i++) transfer(a, b, 1); });
            pool.submit(() -> { for (int i = 0; i < 100_000; i++) transfer(b, a, 1); });
        }
        synchronized (a) { synchronized (b) {
            System.out.println("a=" + a.balance + " b=" + b.balance);
        } }
    }
}
```
Output:
```text
a=500 b=500
```
Thread 1 locked A then waited for B while thread 2 locked B then waited for A: a circular wait. Always acquiring the lower id first makes every thread take locks in the same global order, so no cycle can form. If ids can be equal (the same account), handle that case first. To find deadlocks in production, `jstack` or `jcmd Thread.print` prints "Found one Java-level deadlock" with both stacks.
</details>

## 10. Build a minimal fixed thread pool
Implement `SimplePool` with N worker threads, `submit(Runnable)`, and `shutdown()` that lets queued tasks finish. No `java.util.concurrent` executors allowed (queues are fine).

<details><summary>Solution</summary>

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimplePool {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final List<Thread> workers = new ArrayList<>();
    private volatile boolean shutdown;

    public SimplePool(int size) {
        for (int i = 0; i < size; i++) {
            Thread t = new Thread(this::workLoop, "simple-pool-" + i);
            workers.add(t);
            t.start();
        }
    }

    private void workLoop() {
        while (true) {
            Runnable task;
            try {
                task = queue.take();
            } catch (InterruptedException e) {
                if (shutdown && queue.isEmpty()) return;      // woken by shutdown, nothing left
                continue;
            }
            try {
                task.run();
            } catch (RuntimeException e) {
                System.err.println("task failed: " + e);       // a failing task must not kill the worker
            }
        }
    }

    public void submit(Runnable task) {
        if (shutdown) throw new IllegalStateException("pool is shut down");
        queue.add(task);
    }

    public void shutdownAndWait() throws InterruptedException {
        shutdown = true;
        while (!queue.isEmpty()) Thread.sleep(5);             // let queued work drain
        workers.forEach(Thread::interrupt);                   // wake idle workers blocked in take()
        for (Thread t : workers) t.join();
    }

    public static void main(String[] args) throws InterruptedException {
        SimplePool pool = new SimplePool(3);
        AtomicInteger done = new AtomicInteger();
        for (int i = 0; i < 20; i++) pool.submit(done::incrementAndGet);
        pool.shutdownAndWait();
        System.out.println("tasks completed = " + done.get());
    }
}
```
Output:
```text
tasks completed = 20
```
Workers block on `take()`, which is the core of every real pool. Shutdown stops new submissions, waits for the queue to drain, then interrupts workers that are idle in `take()`. A worker interrupted while the queue still has items keeps going. `ThreadPoolExecutor` adds core and max sizes, rejection policies, and a cleaner state machine for the same ideas.
</details>

## 11. Call a slow service with a timeout and cancel it
Run a call that may take 2 seconds; give up after 300 ms, cancel the work, and return a fallback.

<details><summary>Solution</summary>

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeoutCall {
    static final AtomicBoolean interrupted = new AtomicBoolean();

    static String slowPriceLookup() {
        try {
            Thread.sleep(2_000);
            return "price=42";
        } catch (InterruptedException e) {
            interrupted.set(true);                 // cooperative cancellation
            Thread.currentThread().interrupt();
            return "cancelled";
        }
    }

    public static void main(String[] args) throws Exception {
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = pool.submit(TimeoutCall::slowPriceLookup);
            String result;
            try {
                result = future.get(300, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);               // interrupt the worker
                result = "price=unknown (fallback)";
            } catch (ExecutionException e) {
                result = "price=error";
            }
            System.out.println(result);
        }
        System.out.println("worker was interrupted: " + interrupted.get());
    }
}
```
Output:
```text
price=unknown (fallback)
worker was interrupted: true
```
`Future.get(timeout)` stops *waiting*, but the task keeps running until `cancel(true)` interrupts it, and even then only if the task responds to interruption. With `CompletableFuture`, `orTimeout` would not interrupt the task at all.
</details>

## 12. Parallel word count with virtual threads and ConcurrentHashMap
Count word frequencies across several "documents" in parallel, one virtual thread per document, merging into one map without locks.

<details><summary>Solution</summary>

```java
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelWordCount {
    public static void main(String[] args) {
        List<String> documents = List.of(
                "the quick brown fox",
                "the lazy dog",
                "the fox jumps over the dog",
                "quick quick slow");
        Map<String, Integer> counts = new ConcurrentHashMap<>();
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String doc : documents) {
                pool.submit(() -> {
                    for (String word : doc.split("\\s+")) {
                        counts.merge(word, 1, Integer::sum);   // atomic per key, no external lock
                    }
                });
            }
        }
        System.out.println(new TreeMap<>(counts));             // sorted for stable output
    }
}
```
Output:
```text
{brown=1, dog=2, fox=2, jumps=1, lazy=1, over=1, quick=3, slow=1, the=4}
```
`merge` is atomic for its key, so concurrent increments of "the" never lose updates. `counts.put(word, counts.getOrDefault(word, 0) + 1)` would be a read-modify-write race. In a real job reading files, each virtual thread blocks on disk or network I/O cheaply; for pure in-memory CPU work, a parallel stream with `groupingByConcurrent` is the alternative.
</details>

@@ 3 | TOPIC | Practice — Phase 16 review: Advanced Concurrency | Cheat sheet, key code, top 10 questions and rapid-fire flashcards for Phase 16 | MUST
# Phase 16 review: Advanced Concurrency

## Cheat sheet

| Idea | Remember |
|---|---|
| `ReentrantLock` | `tryLock`, timeout, interruptible, fair option, many `Condition`s; unlock in `finally` |
| Reentrancy | Owner can re-acquire; hold count must return to zero |
| Read-write lock | Many readers or one writer; downgrade allowed, upgrade deadlocks |
| `StampedLock` | Optimistic read + `validate`; not reentrant, no conditions |
| AQS | Volatile int state + FIFO wait queue; engine of most j.u.c synchronizers |
| `CountDownLatch` | One-shot; wait for N events; count down in `finally` |
| `CyclicBarrier` | Reusable; N threads wait for each other; breaks on interrupt or timeout |
| `Semaphore` | N permits; limits in-flight work; permits not owned |
| `Phaser` | Dynamic parties, numbered phases |
| Fork-join | Per-worker deques, LIFO own, FIFO steal; `fork`, `compute`, `join` |
| Common pool | Cores minus one; parallel streams and default async; never block in it |
| CF composition | `thenApply` map, `thenCompose` flatMap, `thenCombine` two, `allOf`/`anyOf` many |
| CF errors | `exceptionally` recover, `handle` both, `whenComplete` observe; unwrap `CompletionException` |
| CF timeouts | `orTimeout` fail, `completeOnTimeout` default; `cancel` does not interrupt |
| Virtual threads | Final Java 21; cheap, unmount on blocking; for I/O-bound; never pool |
| Pinning | `synchronized` (21-23) and native calls; fixed for `synchronized` in Java 24 |
| Spring Boot | `spring.threads.virtual.enabled=true` (Boot 3.2+) |
| Structured concurrency | Subtasks bounded by a scope; still preview in Java 25 (`StructuredTaskScope.open`, `Joiner`) |
| Scoped values | Immutable, bounded context; final in Java 25 |
| ThreadLocal | Value in the Thread's map; `remove()` in `finally`; propagate with `TaskDecorator` |
| Thread-safe design | Stateless, immutable, confined, delegated, then locked; one lock per invariant |

### Key code snippets
```java
// Explicit lock with timeout
if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
    try {
        updateSharedState();
    } finally {
        lock.unlock();
    }
} else {
    throw new IllegalStateException("busy, try again");
}
```

```java
// Bulkhead with a semaphore
if (!permits.tryAcquire(200, TimeUnit.MILLISECONDS)) return fallback();
try {
    return client.call();
} finally {
    permits.release();
}
```

```java
// CompletableFuture fan-out with timeouts and fallback
CompletableFuture<User> user = CompletableFuture.supplyAsync(() -> users.find(id), io).orTimeout(800, TimeUnit.MILLISECONDS);
CompletableFuture<Integer> points = CompletableFuture.supplyAsync(() -> loyalty.points(id), io)
        .completeOnTimeout(0, 300, TimeUnit.MILLISECONDS)
        .exceptionally(ex -> 0);
Summary summary = user.thenCombine(points, Summary::new).join();
```

```java
// Virtual threads: one per task, structured by try-with-resources
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    List<Future<Price>> prices = suppliers.stream()
            .map(s -> executor.submit(() -> s.quote(sku)))
            .toList();
}   // close() waits for all tasks
```

```java
// Fork-join idiom
left.fork();
long rightResult = right.compute();
long leftResult = left.join();
return leftResult + rightResult;
```

```java
// ThreadLocal hygiene
CONTEXT.set(value);
try {
    handle();
} finally {
    CONTEXT.remove();
}
```

```java
// Multi-field invariant: immutable snapshot + CAS
state.updateAndGet(s -> s.date().equals(today) ? new State(today, s.seq() + 1) : new State(today, 1));
```

```yaml
# Spring Boot 3.2+: virtual threads for request handling, @Async and scheduling
spring:
  threads:
    virtual:
      enabled: true
```

## The 10 most likely interview questions

1. **`ReentrantLock` vs `synchronized`?** Same memory semantics; the lock adds `tryLock`, timeouts, interruptible waits, fairness and multiple conditions, at the cost of manual `unlock` in `finally`.
2. **How do you avoid deadlock?** Consistent global lock order, `tryLock` with timeout and back-off, holding one lock at a time, and never calling foreign code while holding a lock.
3. **CountDownLatch vs CyclicBarrier vs Semaphore?** One-shot wait for N events; reusable meeting point for N threads; limit of N concurrent holders.
4. **What is work stealing and why not block in the common pool?** Idle workers steal from others' deques; the common pool is tiny and shared, so blocking there stalls parallel streams and default async tasks JVM-wide.
5. **`thenApply` vs `thenCompose`, and how are errors handled?** Map vs flatMap; errors skip normal stages until `exceptionally`, `handle` or `whenComplete`, arriving wrapped in `CompletionException`.
6. **What are virtual threads and when do they not help?** JVM-scheduled cheap threads that unmount on blocking; no help for CPU-bound work, and limits must move to downstream resources.
7. **What is pinning?** A blocked virtual thread holding its carrier: `synchronized` on Java 21-23, native calls always; detect with JFR `jdk.VirtualThreadPinned`.
8. **What are structured concurrency and scoped values, and their status?** Scope-bounded subtasks with automatic cancellation (preview through Java 25) and immutable bounded context replacing `ThreadLocal` (final in Java 25).
9. **How do ThreadLocals leak and how does Spring propagate context to `@Async`?** Values stay in long-lived pool threads without `remove()`; use a `TaskDecorator` to capture, install and restore.
10. **How do you design a thread-safe class?** Stateless, immutable, confined, delegated; for invariants across fields, one lock or an immutable snapshot swapped by CAS; safe publication; document the policy.

## Rapid-fire flashcards

Q: What does `lock.getHoldCount()` return inside a nested acquisition by the same thread?
A: The number of times the current thread holds the lock, for example 2.

Q: Which lock offers an optimistic read mode?
A: `StampedLock`, via `tryOptimisticRead()` and `validate(stamp)`.

Q: What exception do waiting threads get when a `CyclicBarrier` breaks?
A: `BrokenBarrierException`.

Q: Which synchronizer is best for limiting concurrent calls to a partner API?
A: `Semaphore`.

Q: What is the default parallelism of the virtual thread scheduler?
A: The number of available processors.

Q: Does `CompletableFuture.cancel(true)` interrupt the running task?
A: No.

Q: Which Java version made `ScopedValue` final?
A: Java 25.

Q: What method creates a structured scope in the Java 25 preview API?
A: `StructuredTaskScope.open()`, optionally with a `Joiner` and a configuration function.

Q: Why is `InheritableThreadLocal` useless for executor tasks?
A: It copies values only when a thread is created, and pool threads already exist.

Q: Why are immutable objects thread-safe without locks?
A: Their state never changes after construction and `final` fields are safely published by the JMM.
