@@ 1 | MODULE | Phase 2 — Core Java | Language, collections, streams, concurrency, JVM | MUST
# Phase 2: Core Java

Java is the **main language of this job**. Expect output-prediction MCQs, "what is the difference between…" questions and handwritten code.

@@ 2 | MODULE | Java Language Basics | JDK/JRE/JVM, types, strings, keywords, exceptions | MUST
The building blocks: how Java runs, types, Strings, `equals`/`hashCode`, keywords, exceptions, generics, interfaces, enums, records.

@@ 3 | TOPIC | JDK, JRE, JVM and Data Types | How Java runs and primitive vs reference types | MUST
# JDK, JRE, JVM and data types

- **JVM (Java Virtual Machine):** runs bytecode (`.class` files). One JVM per OS, so the same bytecode runs everywhere: "write once, run anywhere".
- **JRE:** JVM + standard libraries, enough to **run** Java.
- **JDK:** JRE + tools (`javac` compiler, `jar`, debugger), needed to **develop** Java.

**Analogy:** the JDK is a full kitchen (tools + oven). The JRE is just the oven and ingredients needed to heat food. The JVM is the oven itself.

`Hello.java` → `javac` → `Hello.class` (bytecode) → `java Hello` → the JVM interprets it and the **JIT** compiles hot code to machine code.

## Primitive vs reference types

| Primitive | Size | Default | Wrapper |
|---|---|---|---|
| byte | 1 byte | 0 | Byte |
| short | 2 | 0 | Short |
| int | 4 | 0 | Integer |
| long | 8 | 0L | Long |
| float | 4 | 0.0f | Float |
| double | 8 | 0.0 | Double |
| char | 2 (UTF-16) | '\u0000' | Character |
| boolean | JVM-dependent | false | Boolean |

```java
int a = 10;                 // primitive: the value itself sits on the stack
Integer b = 10;             // autoboxing: int -> Integer object
int c = b;                  // unboxing: Integer -> int
Integer x = 127, y = 127;
System.out.println(x == y); // true  (values -128..127 are cached)
Integer p = 128, q = 128;
System.out.println(p == q); // false (different objects!)
System.out.println(p.equals(q)); // true (same value)
Integer n = null;
int boom = n;               // NullPointerException during unboxing
```

**Line by line**
- A primitive holds the **value**. A reference variable holds an **address** pointing to an object on the heap.
- **Autoboxing:** Java converts `int` ↔ `Integer` for you automatically.
- `x == y` for 127 is **true** because Java caches Integer objects from -128 to 127. For 128 you get two different objects, so `==` (which compares addresses) is false.
- **Rule: compare objects with `.equals()`, never `==`.**
- Unboxing `null` throws a `NullPointerException`. This is a common bug with `Map.get()` results.

## Java is always pass-by-value

```java
void change(int v, int[] arr) { v = 99; arr[0] = 99; }
int v = 1; int[] arr = {1};
change(v, arr);
// v is still 1, arr[0] is now 99
```
- Java copies the **value** of the variable. For an object, that value is the **reference** (the address). So the method can modify the object the reference points to, but it can't make your variable point to a different object.
- **Analogy:** I give you a **photocopy of my house address**. You can go and paint my house (modify the object), but scribbling a new address on your copy doesn't move my house.

@@ 3 | TOPIC | Strings | Immutability, String pool, StringBuilder | MUST
# Strings

`String` is **immutable**: once created it can never change. Any "change" creates a **new** String.

**Analogy:** a printed book. To "edit" it you print a new edition; the old one stays the same.

```java
String a = "hello";             // goes into the String pool
String b = "hello";             // reuses the same pooled object
String c = new String("hello"); // forces a new object on the heap
System.out.println(a == b);       // true  (same pooled object)
System.out.println(a == c);       // false (different objects)
System.out.println(a.equals(c));  // true  (same characters)
System.out.println(a == c.intern()); // true (intern returns the pooled copy)

String s = "Hi";
s.concat(" there");             // result thrown away!
System.out.println(s);          // "Hi"
s = s.concat(" there");         // must reassign
```

**Line by line**
- **String pool:** a special memory area where literal strings are shared. The same literal gives the same object, which saves memory.
- `new String("hello")` always creates a new object outside the pool.
- `concat` returns a **new** String. If you don't store it, the result is lost.

## Why is String immutable?
1. **Security:** file paths, URLs and DB usernames can't be changed after a check.
2. **Thread safety:** it can be shared between threads with no locks.
3. **Hash caching:** its hashCode never changes, which makes it perfect as a HashMap key.
4. **The pool works:** sharing is only safe if nobody can modify the string.

## StringBuilder for building strings in a loop

```java
// BAD: creates a new String object every iteration -> O(n²)
String s = "";
for (int i = 0; i < 1000; i++) s += i;

// GOOD: one mutable buffer -> O(n)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i).append(',');
String result = sb.toString();
```
- `StringBuilder` is a **mutable** character buffer. `append` adds to the same object.
- `StringBuffer` is the same but **synchronized** (thread-safe, slower). It is rarely needed today.

| | String | StringBuilder | StringBuffer |
|---|---|---|---|
| Mutable | No | Yes | Yes |
| Thread-safe | Yes (immutable) | No | Yes (synchronized) |
| Speed for many edits | Slow | Fastest | Slower |

**Useful methods:** `length()`, `charAt(i)`, `substring(a, b)` (b exclusive), `indexOf`, `split(",")`, `trim()`/`strip()`, `toUpperCase()`, `replace`, `isBlank()`, `String.join(",", list)`, `"%d items".formatted(5)`.

@@ 3 | TOPIC | equals() and hashCode() | The contract and why both must be overridden | MUST
# equals() and hashCode()

- `==` checks whether two references point to the **same object** (same address).
- `equals()` checks whether two objects are **logically equal** (same content), if you override it.
- `hashCode()` returns an int used by HashMap/HashSet to pick a bucket.

**The contract:** if `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` **must** be true. (The reverse is not required: two different objects may share a hash.)

**Analogy:** hashCode is the **shelf number** in a library, and equals is checking the **exact title** on that shelf. If two copies of the same book were put on different shelves, you would never find the second one.

```java
public class Member {
    private final String nationalId;
    private final String name;

    public Member(String nationalId, String name) {
        this.nationalId = nationalId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                       // same object
        if (!(o instanceof Member other)) return false;   // null or a different type
        return nationalId.equals(other.nationalId);       // business identity
    }

    @Override
    public int hashCode() {
        return Objects.hash(nationalId);                  // use the SAME fields as equals
    }
}
```

**Line by line**
- `this == o`: a quick shortcut. An object always equals itself.
- `o instanceof Member other`: (Java 16+ pattern matching) checks the type and casts in one step. `instanceof` is false for `null`, so null is handled too.
- We compare `nationalId` only, because two Member objects with the same national ID represent the same person.
- `Objects.hash(nationalId)`: builds the hash from **the same fields** used in `equals`.

## What breaks if you override only equals?

```java
Set<Member> set = new HashSet<>();
set.add(new Member("123", "Rahim"));
set.contains(new Member("123", "Rahim"));  // false if hashCode is NOT overridden!
```
- Without `hashCode`, each object gets a different default hash (based on identity), so the lookup goes to the wrong bucket and never even calls `equals`.

**Tip:** a Java `record` generates correct `equals`/`hashCode` automatically. Avoid using mutable fields in `hashCode`: if the field changes while the object is inside a HashSet, the object gets "lost".

@@ 3 | TOPIC | Keywords and Access Modifiers | static, final, this, super, access levels | MUST
# Keywords and access modifiers

## Access modifiers
| Modifier | Same class | Same package | Subclass | Everywhere |
|---|---|---|---|---|
| `private` | ✓ | | | |
| (default, no word) | ✓ | ✓ | | |
| `protected` | ✓ | ✓ | ✓ | |
| `public` | ✓ | ✓ | ✓ | ✓ |

**Analogy:** private = your diary, default = shared with your family (package), protected = family plus your children who moved away (subclasses), public = posted on a notice board.

## static
Belongs to the **class**, not to any one object. All objects share it.

```java
public class Counter {
    static int total = 0;      // shared by all Counter objects
    int mine = 0;              // each object has its own

    Counter() { total++; mine++; }

    static int getTotal() { return total; }   // can't use 'this' or 'mine' here
}
new Counter(); new Counter();
System.out.println(Counter.total);  // 2
```
- `total` exists once for the whole class. `mine` exists once per object.
- A static method has no `this`, so it can't touch instance fields directly.
- **Analogy:** `static` is the class whiteboard everyone shares. Instance fields are each student's own notebook.

## final
- `final` variable: can be assigned only **once**.
- `final` method: **can't be overridden**.
- `final` class: **can't be extended** (e.g. `String`).

```java
final List<String> list = new ArrayList<>();
list.add("ok");          // allowed: the object can change
// list = new ArrayList<>();  // compile error: the reference can't change
```
- `final` freezes the **reference**, not the object. For a truly unchangeable list use `List.of(...)`.

## this and super
- `this`: the current object. Used for `this.name = name` and for constructor chaining `this(...)`.
- `super`: the parent class. `super.method()`, `super(...)` calls the parent constructor and must be the first line.

## Others to know
- `transient`: skip this field during serialization (e.g. a password).
- `volatile`: always read/write from main memory, giving visibility between threads (see Concurrency).
- `synchronized`: only one thread at a time may run this method/block on the same lock.

**final vs finally vs finalize:** `final` = can't change; `finally` = a block that always runs after try/catch; `finalize()` = a deprecated GC hook. Never use it.

@@ 3 | TOPIC | Exceptions | Checked vs unchecked, try-with-resources, custom exceptions | MUST
# Exceptions

An **exception** is an event that breaks normal flow: a file is missing, the network is down, input is bad.

```
Throwable
├── Error            (JVM problems: OutOfMemoryError, StackOverflowError — don't catch)
└── Exception
    ├── IOException, SQLException ...   (CHECKED: must handle or declare)
    └── RuntimeException                 (UNCHECKED: programming bugs)
        ├── NullPointerException
        ├── IllegalArgumentException
        └── ArithmeticException ...
```

| | Checked | Unchecked |
|---|---|---|
| Checked at compile time | Yes, you must `catch` or `throws` | No |
| Typical cause | External problems (file, network) | Bugs, bad input |
| Example | `IOException` | `NullPointerException` |

**Analogy:** a checked exception is like the weather forecast saying "it may rain", and the compiler forces you to carry an umbrella (handle it). An unchecked exception is tripping over your own shoelace: a mistake you should fix, not plan for.

## try / catch / finally and try-with-resources

```java
// Old style
BufferedReader r = null;
try {
    r = new BufferedReader(new FileReader("data.txt"));
    System.out.println(r.readLine());
} catch (IOException e) {
    System.err.println("Could not read: " + e.getMessage());
} finally {
    if (r != null) try { r.close(); } catch (IOException ignored) {}
}

// Modern: try-with-resources (Java 7+)
try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    System.out.println(reader.readLine());
} catch (IOException e) {
    System.err.println("Could not read: " + e.getMessage());
}
```

**Line by line**
- `try`: the code that may fail.
- `catch (IOException e)`: runs only if that type of exception (or a subclass) is thrown.
- `finally`: **always** runs, whether or not there was an exception. It is for cleanup.
- `try (resource)`: anything that implements `AutoCloseable` is **closed automatically** at the end, even if an exception happens. Shorter and safer.

## Custom exception for business rules

```java
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountNo, BigDecimal needed) {
        super("Account " + accountNo + " needs " + needed + " more");
    }
}

void withdraw(BigDecimal amount) {
    if (balance.compareTo(amount) < 0)
        throw new InsufficientBalanceException(accountNo, amount.subtract(balance));
    balance = balance.subtract(amount);
}
```
- Extending `RuntimeException` (unchecked) is the common choice in Spring apps. A `@RestControllerAdvice` can then turn it into a clean HTTP error.
- `super(message)` passes the message to the parent, so `getMessage()` works.
- `compareTo(amount) < 0` means balance < amount (BigDecimal can't use `<`).

**Tricky facts**
- Order catch blocks from **specific to general**. Putting `catch (Exception)` first makes the later ones unreachable, which is a compile error.
- If `finally` has a `return`, it **overrides** the return in `try`. Avoid it.
- `finally` does not run only if the JVM exits (`System.exit`) or crashes.
- `throw` throws one exception. `throws` in a method signature **declares** what might be thrown.

@@ 3 | TOPIC | Generics | Type-safe containers, bounded types, wildcards | SHOULD
# Generics

**Generics** let a class or method work with **any type** while staying type-safe.

**Analogy:** a labelled storage box: "Box of Books". You can't accidentally put shoes in it, and when you take something out you *know* it's a book (no casting).

```java
// Without generics (old Java): unsafe
List list = new ArrayList();
list.add("hi"); list.add(42);
String s = (String) list.get(1);   // ClassCastException at runtime!

// With generics: the compiler stops mistakes
List<String> names = new ArrayList<>();
names.add("hi");
// names.add(42);                  // compile error
String first = names.get(0);       // no cast needed
```

## Writing a generic class and method

```java
public class Box<T> {                     // T = a type parameter
    private T value;
    public void set(T v) { value = v; }
    public T get() { return value; }
}
Box<Integer> b = new Box<>();             // T becomes Integer
b.set(5);

public static <T extends Comparable<T>> T max(List<T> list) {
    T best = list.get(0);
    for (T x : list) if (x.compareTo(best) > 0) best = x;
    return best;
}
max(List.of(3, 9, 2));      // 9
```

**Line by line**
- `<T>`: a placeholder. The user decides the real type: `Box<Integer>`, `Box<String>`.
- `<T extends Comparable<T>>`: a **bounded type**. T must be comparable, so we're allowed to call `compareTo`.
- `new Box<>()`: the diamond `<>` lets Java infer the type.

## Wildcards: PECS (Producer Extends, Consumer Super)

```java
double sum(List<? extends Number> nums) {      // READ numbers (producer)
    double s = 0; for (Number n : nums) s += n.doubleValue(); return s;
}
void addInts(List<? super Integer> out) {      // WRITE integers (consumer)
    out.add(1); out.add(2);
}
```
- `? extends Number`: accepts `List<Integer>`, `List<Double>`... You can **read** as Number, but you can't add (Java doesn't know the exact type).
- `? super Integer`: accepts `List<Integer>`, `List<Number>`, `List<Object>`. You can **add** Integers.

**Type erasure:** generics exist only at compile time. At runtime `List<String>` and `List<Integer>` are both just `List`. That's why you can't write `new T()` or `if (x instanceof List<String>)`.

@@ 3 | TOPIC | Interfaces vs Abstract Classes | When to use which | MUST
# Interfaces vs abstract classes

| | Interface | Abstract class |
|---|---|---|
| Purpose | A **capability / contract** ("can do") | A **partial base class** ("is a") |
| Multiple inheritance | A class can implement **many** | A class can extend only **one** |
| Fields | Only `public static final` constants | Any fields (with state) |
| Constructors | No | Yes |
| Methods | abstract, `default`, `static`, `private` (Java 9+) | abstract and concrete |

**Analogy:** an interface is a **driving licence**: it says you *can drive*, and anyone can get one. An abstract class is a **family surname**: you inherit traits, but you can only belong to one family.

```java
interface Payable {
    BigDecimal amountDue();                          // abstract (must implement)
    default boolean isOverdue(LocalDate due) {       // default: shared body
        return LocalDate.now().isAfter(due);
    }
}

abstract class Account {
    protected BigDecimal balance = BigDecimal.ZERO;  // state
    protected Account(BigDecimal opening) { balance = opening; }
    abstract BigDecimal monthlyFee();                // subclasses decide
    void applyFee() { balance = balance.subtract(monthlyFee()); }   // shared logic
}

class SavingsAccount extends Account implements Payable {
    SavingsAccount(BigDecimal opening) { super(opening); }
    BigDecimal monthlyFee() { return BigDecimal.ZERO; }
    public BigDecimal amountDue() { return BigDecimal.ZERO; }
}
```

**Line by line**
- `default` method: added in Java 8 so that interfaces can gain new methods **without breaking** existing implementations.
- `Account` has a field and a constructor, which an interface cannot have.
- `applyFee()` is a small **template method**: a fixed step that calls the variable step `monthlyFee()`.
- `SavingsAccount` extends **one** class but could implement **many** interfaces.

**Rule of thumb:** start with an interface. Use an abstract class when subclasses share **state or common code**.

@@ 3 | TOPIC | Enums and Records | Type-safe constants and immutable data carriers | MUST
# Enums and records

## Enum: a fixed set of named values

```java
public enum LoanStatus {
    PENDING("Waiting for approval"),
    ACTIVE("Disbursed and running"),
    CLOSED("Fully repaid");

    private final String label;
    LoanStatus(String label) { this.label = label; }
    public String label() { return label; }

    public boolean canRepay() { return this == ACTIVE; }
}

LoanStatus s = LoanStatus.valueOf("ACTIVE");   // String -> enum
switch (s) {
    case PENDING -> System.out.println("approve first");
    case ACTIVE  -> System.out.println("collect");
    case CLOSED  -> System.out.println("done");
}
```

**Line by line**
- Each constant (`PENDING`...) is a single object created once, so you can compare with `==`.
- Enums can have fields, constructors and methods, like a small class.
- The constructor is implicitly private: nobody can create new statuses at runtime.
- `valueOf("ACTIVE")` throws `IllegalArgumentException` for an unknown name.
- **Analogy:** traffic light colours. There are only three, and nobody can invent "purple".
- In JPA, store enums with `@Enumerated(EnumType.STRING)`, never ORDINAL: reordering the constants would silently corrupt the data.

## Record (Java 16+): an immutable data carrier

```java
public record MoneyDto(String currency, BigDecimal amount) {
    public MoneyDto {                                   // compact constructor: validation
        if (amount.signum() < 0) throw new IllegalArgumentException("negative");
    }
}
var m = new MoneyDto("BDT", new BigDecimal("500.00"));
m.amount();             // accessor (no "get" prefix)
System.out.println(m);  // MoneyDto[currency=BDT, amount=500.00]
```
- One line gives you: private final fields, a constructor, accessors, `equals`, `hashCode` and `toString`.
- The **compact constructor** runs your validation before the fields are assigned.
- Records are perfect for **DTOs**, API responses and events. They are not suitable as JPA entities (entities need a no-arg constructor and mutable fields).
- **Analogy:** a sealed envelope with a printed receipt. Once printed, the values can't change.

@@ 3 | TOPIC | Immutable Class Recipe | How to make a class that never changes | SHOULD
# Immutable class recipe

An **immutable** object can't change after creation (like `String`, `LocalDate`, `BigDecimal`). It is automatically thread-safe and safe to share or cache.

**Analogy:** a stamped, signed certificate. If anything needs to change, you issue a new certificate.

```java
public final class Installment {                          // 1. final class: no subclass can break it
    private final int number;                             // 2. private final fields
    private final BigDecimal amount;
    private final List<String> notes;

    public Installment(int number, BigDecimal amount, List<String> notes) {
        this.number = number;
        this.amount = amount;
        this.notes = List.copyOf(notes);                  // 3. defensive copy IN
    }

    public int getNumber() { return number; }             // 4. getters only, no setters
    public BigDecimal getAmount() { return amount; }
    public List<String> getNotes() { return notes; }      // List.copyOf is already unmodifiable

    public Installment withAmount(BigDecimal newAmount) { // 5. "change" returns a NEW object
        return new Installment(number, newAmount, notes);
    }
}
```

**Line by line**
1. `final class`: stops a subclass from adding mutable behaviour.
2. `private final`: fields are set once and hidden.
3. `List.copyOf(notes)`: if we stored the caller's list directly, the caller could still modify it from outside. The copy cuts that link. `List.copyOf` is also unmodifiable.
4. No setters.
5. `withAmount`: the "wither" pattern. You get a modified copy and the original stays unchanged.

`BigDecimal` and `String` fields are already immutable, so they need no copying. Mutable fields like `Date`, arrays and lists need defensive copies both in and out.

Shortcut: a `record` gives you points 1, 2 and 4. You still add `List.copyOf` in its compact constructor.

@@ 2 | MODULE | Collections Framework | List, Set, Map, Queue and their internals | MUST
The Collections Framework is a toolbox of ready-made data structures. Choosing the right one is a classic interview question.

@@ 3 | TOPIC | Collections Overview | List vs Set vs Map vs Queue and which implementation to choose | MUST
# Collections overview

```
Iterable
└── Collection
    ├── List   (ordered, allows duplicates)  : ArrayList, LinkedList
    ├── Set    (no duplicates)               : HashSet, LinkedHashSet, TreeSet
    └── Queue/Deque                          : ArrayDeque, PriorityQueue, LinkedList
Map (key -> value, not a Collection)        : HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap
```

| Need | Choose | Why |
|---|---|---|
| Ordered list, fast index access | `ArrayList` | Backed by an array, O(1) get |
| Unique items, order doesn't matter | `HashSet` | O(1) add/contains |
| Unique items, keep insertion order | `LinkedHashSet` | Hash + linked list |
| Unique items, sorted | `TreeSet` | Red-black tree, O(log n) |
| Key → value lookup | `HashMap` | O(1) average |
| Sorted keys | `TreeMap` | `firstKey()`, `headMap()`, ranges |
| Stack or queue | `ArrayDeque` | Fast at both ends |
| Always get the smallest/largest | `PriorityQueue` | Binary heap |
| Shared between threads | `ConcurrentHashMap` | Fine-grained locking |

## ArrayList vs LinkedList

| | ArrayList | LinkedList |
|---|---|---|
| get(i) | O(1) | O(n) |
| add at end | O(1) amortized | O(1) |
| add/remove in middle | O(n) (shift) | O(1) once you're there, but O(n) to get there |
| Memory | Compact | Extra node objects + 2 pointers each |

In practice **ArrayList wins almost always** (CPU caches like contiguous memory).

```java
List<String> names = new ArrayList<>(List.of("Rahim", "Karim"));
names.add("Jamal");
names.remove("Karim");

// Safe removal while iterating
names.removeIf(n -> n.startsWith("R"));

// WRONG: ConcurrentModificationException
// for (String n : names) if (n.isEmpty()) names.remove(n);

List<String> fixed = List.of("a", "b");   // immutable
// fixed.add("c");                         // UnsupportedOperationException
```

**Line by line**
- `new ArrayList<>(List.of(...))`: `List.of` is immutable, so we copy it into a mutable ArrayList.
- `removeIf`: the safe way to delete while looping.
- Removing inside a for-each loop changes the list behind the iterator's back, so Java throws `ConcurrentModificationException` (fail-fast).
- `List.of`/`Set.of`/`Map.of` create **unmodifiable** collections and don't allow nulls.

**ArrayList growth:** when full, it creates a new array about **1.5×** bigger and copies the elements. That is why add is "amortized" O(1).

@@ 3 | TOPIC | HashMap Internals | Buckets, hashing, load factor, treeification | MUST
# HashMap internals

```java
Map<String, Integer> m = new HashMap<>();
m.put("apple", 3);
```

**What happens inside `put("apple", 3)`:**
1. Compute `"apple".hashCode()`.
2. **Spread** the bits: `h ^ (h >>> 16)` (mixes the high bits in for better distribution).
3. Bucket index = `hash & (capacity - 1)` (the same as `% capacity`, because capacity is a power of 2).
4. If the bucket is empty, store a new node.
5. If not, walk the bucket. If a key there `equals("apple")`, **replace** its value. Otherwise append a node.
6. If a bucket grows beyond **8** nodes (and capacity ≥ 64), it turns into a **red-black tree**.
7. If size > capacity × **load factor (0.75)**, **resize** (double the capacity) and redistribute all entries.

**Analogy:** a post office with 16 pigeonholes. Your name's hash decides your pigeonhole. If one pigeonhole gets too crowded, the post office organises it as a sorted binder (a tree). If the whole office gets 75% full, it moves to a building with 32 pigeonholes.

| Setting | Default |
|---|---|
| Initial capacity | 16 |
| Load factor | 0.75 |
| Treeify threshold | 8 (back to a list at 6) |

```java
Map<String, Integer> counts = new HashMap<>();
for (String w : "a b a c b a".split(" ")) {
    counts.merge(w, 1, Integer::sum);           // add 1, or start at 1
}
// {a=3, b=2, c=1}

counts.computeIfAbsent("d", k -> 0);            // insert if missing
counts.getOrDefault("z", 0);                    // 0, no NPE
for (Map.Entry<String, Integer> e : counts.entrySet())
    System.out.println(e.getKey() + "=" + e.getValue());
```

**Line by line**
- `merge(key, 1, Integer::sum)`: if the key is missing, put 1. If it is present, combine the old value and 1 with `sum`. It is the cleanest way to count.
- `computeIfAbsent`: great for `Map<String, List<X>>`: `map.computeIfAbsent(k, x -> new ArrayList<>()).add(item)`.
- Iterate over `entrySet()` to get the key and value together (faster than `keySet()` + `get`).

**Facts to remember**
- HashMap allows **one null key** and many null values. `Hashtable` and `ConcurrentHashMap` allow **no** nulls.
- HashMap is **not thread-safe**. Use `ConcurrentHashMap` for shared access.
- Order is **not guaranteed**. Use `LinkedHashMap` for insertion order, `TreeMap` for sorted order.
- Mutable keys are dangerous: if the hash changes after insertion, the entry is lost.

@@ 3 | TOPIC | LRU Cache with LinkedHashMap | Least-recently-used eviction in a few lines | SHOULD
# LRU cache with LinkedHashMap

**LRU (Least Recently Used):** when the cache is full, throw out the item that hasn't been used for the longest time.

**Analogy:** a small desk that holds 3 books. When you need a 4th, you put back the book you haven't touched for the longest time.

```java
class LruCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    LruCache(int capacity) {
        super(16, 0.75f, true);            // true = ACCESS order (not insertion order)
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;          // remove the oldest when over capacity
    }
}

LruCache<String, String> c = new LruCache<>(2);
c.put("a", "1"); c.put("b", "2");
c.get("a");             // 'a' is now most recently used
c.put("c", "3");        // over capacity: evicts 'b' (least recently used)
System.out.println(c.keySet());   // [a, c]
```

**Line by line**
- `LinkedHashMap` = HashMap + a doubly linked list that remembers order.
- `super(16, 0.75f, true)`: initial capacity, load factor, and **accessOrder = true**. Each `get`/`put` moves the entry to the end (most recent).
- `removeEldestEntry`: a hook that LinkedHashMap calls after every `put`. Returning true removes the first (least recently used) entry.
- Both `get` and `put` are O(1).

Interview follow-up: "Build it without LinkedHashMap." Use a `HashMap<K, Node>` plus your own doubly linked list: move a node to the head on access and remove from the tail when full.

@@ 3 | TOPIC | Comparable vs Comparator | Natural order vs custom sort rules | MUST
# Comparable vs Comparator

- **Comparable:** the class defines its **own natural order** by implementing `compareTo`. One order only.
- **Comparator:** an **external** rule. You can have many (by name, by amount, by date...).

**Analogy:** Comparable is a student's roll number (the built-in order). Comparator is the teacher saying "today, line up by height".

`compare(a, b)` returns **negative** if a comes first, **0** if equal, **positive** if b comes first.

```java
public class Loan implements Comparable<Loan> {
    Long id; String member; BigDecimal amount; LocalDate date;

    @Override
    public int compareTo(Loan other) {
        return this.id.compareTo(other.id);           // natural order: by id
    }
}

List<Loan> loans = new ArrayList<>(...);
Collections.sort(loans);                              // uses compareTo (by id)

// Comparators: many custom orders
loans.sort(Comparator.comparing(Loan::getAmount));                    // by amount ascending
loans.sort(Comparator.comparing(Loan::getAmount).reversed());         // descending
loans.sort(Comparator.comparing(Loan::getMember)
                     .thenComparing(Loan::getDate, Comparator.reverseOrder()));  // name, then newest date
```

**Line by line**
- `implements Comparable<Loan>`: now `Collections.sort` and `TreeSet` know how to order loans.
- `this.id.compareTo(other.id)`: reuse Long's own comparison. **Don't** write `return a - b` with ints, because it can overflow. Use `Integer.compare(a, b)`.
- `Comparator.comparing(Loan::getAmount)`: builds a comparator from a "key extractor" (a method reference).
- `.reversed()`: flips the order.
- `.thenComparing(...)`: a tie-breaker when the first key is equal.

@@ 2 | MODULE | Functional Java and Streams | Lambdas, functional interfaces, streams, Optional | MUST
Java 8 added **lambdas** and **streams**, letting you write data processing as a pipeline: "filter these, map them, collect the result".

@@ 3 | TOPIC | Lambdas and Functional Interfaces | Function, Predicate, Supplier, Consumer, method references | MUST
# Lambdas and functional interfaces

A **lambda** is a short anonymous function: `(parameters) -> body`.
A **functional interface** has exactly **one abstract method**, and a lambda provides that method's body.

**Analogy:** a lambda is a sticky note with instructions ("double this number") that you hand to someone else to run when needed.

```java
// Old style: anonymous class
Comparator<String> c1 = new Comparator<String>() {
    public int compare(String a, String b) { return a.length() - b.length(); }
};
// Lambda: same thing
Comparator<String> c2 = (a, b) -> a.length() - b.length();
```

## The four core functional interfaces (java.util.function)

| Interface | Method | Takes → Returns | Example |
|---|---|---|---|
| `Function<T,R>` | `apply` | T → R | `s -> s.length()` |
| `Predicate<T>` | `test` | T → boolean | `n -> n > 0` |
| `Supplier<T>` | `get` | nothing → T | `() -> LocalDate.now()` |
| `Consumer<T>` | `accept` | T → nothing | `s -> System.out.println(s)` |

```java
Function<String, Integer> len = s -> s.length();
Predicate<Integer> isPositive = n -> n > 0;
Supplier<List<String>> newList = ArrayList::new;
Consumer<String> printer = System.out::println;
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
UnaryOperator<String> upper = String::toUpperCase;

len.apply("hello");              // 5
isPositive.test(-3);             // false
isPositive.and(n -> n < 100);    // combine predicates
len.andThen(n -> n * 2).apply("hi");   // 4

@FunctionalInterface
interface Validator { boolean validate(String input); }   // your own
Validator notBlank = s -> !s.isBlank();
```

**Line by line**
- `s -> s.length()`: one parameter, so no parentheses are needed. A single expression has an implicit `return`.
- `ArrayList::new`, `System.out::println`, `String::toUpperCase`: **method references**, a shorter way to write a lambda that just calls one method.
- `@FunctionalInterface`: optional, but the compiler then makes sure there's only one abstract method.

**Effectively final:** a lambda can use local variables from outside only if they never change after assignment.
```java
int base = 10;
Function<Integer, Integer> f = x -> x + base;   // OK
// base = 20;   // would make the lambda above a compile error
```

@@ 3 | TOPIC | Streams API | filter, map, reduce, collect, groupingBy | MUST
# Streams API

A **stream** is a pipeline that processes a collection step by step, without changing the original.

**Analogy:** a factory conveyor belt. Items enter, pass through stations (filter, transform), and come out at the end packed into a box (collect).

Stream = **source → intermediate operations (lazy) → one terminal operation**.

```java
record Loan(String po, String member, BigDecimal amount, String status) {}

List<Loan> loans = List.of(
    new Loan("PO1", "Rahim", new BigDecimal("50000"), "ACTIVE"),
    new Loan("PO1", "Karim", new BigDecimal("30000"), "CLOSED"),
    new Loan("PO2", "Jamal", new BigDecimal("70000"), "ACTIVE"));

// 1. Names of active borrowers, sorted
List<String> active = loans.stream()
        .filter(l -> l.status().equals("ACTIVE"))   // keep only active
        .map(Loan::member)                          // Loan -> String
        .sorted()
        .toList();                                  // [Jamal, Rahim]

// 2. Total of all amounts
BigDecimal total = loans.stream()
        .map(Loan::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);  // 150000

// 3. Total per PO
Map<String, BigDecimal> perPo = loans.stream()
        .collect(Collectors.groupingBy(Loan::po,
                 Collectors.reducing(BigDecimal.ZERO, Loan::amount, BigDecimal::add)));
// {PO1=80000, PO2=70000}

// 4. Count per status
Map<String, Long> byStatus = loans.stream()
        .collect(Collectors.groupingBy(Loan::status, Collectors.counting()));

// 5. Split into two groups
Map<Boolean, List<Loan>> bigOrSmall = loans.stream()
        .collect(Collectors.partitioningBy(l -> l.amount().compareTo(new BigDecimal("40000")) > 0));

// 6. Primitive streams for numbers
int sumOfSquares = IntStream.rangeClosed(1, 5).map(x -> x * x).sum();   // 55
OptionalDouble avg = List.of(1, 2, 3).stream().mapToInt(Integer::intValue).average();
```

**Line by line**
- `filter(predicate)`: keeps the elements where the test is true.
- `map(function)`: transforms each element (here a Loan into its member name).
- `sorted()`: natural order; `sorted(Comparator)` for a custom one.
- `toList()` (Java 16+): returns an unmodifiable list. In older Java use `collect(Collectors.toList())`.
- `reduce(identity, accumulator)`: combines everything into one value. `ZERO` is the starting value, and `BigDecimal::add` adds each amount to the running total.
- `groupingBy(key, downstream)`: like SQL `GROUP BY`. The downstream collector says what to do with each group (count, sum, list).
- `partitioningBy`: a special groupingBy with only true/false keys.
- `IntStream` avoids boxing, so it is faster for plain numbers.

**Key facts**
- Intermediate operations are **lazy**: nothing runs until a terminal operation (`toList`, `collect`, `count`, `forEach`, `reduce`, `findFirst`, `anyMatch`).
- A stream can be used **only once**.
- `map` gives one output per input. `flatMap` flattens nested lists: `List<List<X>> → Stream<X>`.
- Avoid side effects (modifying outside variables) inside streams.
- `parallelStream()` splits work across CPU cores. Use it only for big, CPU-heavy, independent work.

@@ 3 | TOPIC | Optional | Avoiding null and NullPointerException | MUST
# Optional

`Optional<T>` is a box that **may or may not** contain a value. It makes "this might be missing" explicit in the method signature.

**Analogy:** a gift box that might be empty. You must open it carefully instead of assuming there's a gift inside.

```java
Optional<Member> findById(Long id) { ... }        // repository style

// Good ways to use it
Member m = repo.findById(5L)
        .orElseThrow(() -> new NotFoundException("Member 5 not found"));

String name = repo.findById(5L)
        .map(Member::getName)                     // transform if present
        .orElse("Unknown");                       // fallback value

repo.findById(5L).ifPresent(mem -> sendSms(mem)); // run only if present

Optional<String> maybe = Optional.ofNullable(possiblyNull);  // wrap a nullable value
Optional<String> empty = Optional.empty();
```

**Line by line**
- `orElseThrow(supplier)`: get the value, or throw your own exception. This is the most common use in Spring services.
- `map(Member::getName)`: if the box has a member, turn it into the member's name. If it is empty, it stays empty.
- `orElse("Unknown")`: the default if empty. **Note:** the argument is always evaluated. Use `orElseGet(() -> expensive())` for costly defaults.
- `ifPresent(consumer)`: runs the code only when a value exists.
- `ofNullable` accepts null. `Optional.of(null)` throws an NPE.

**Do / don't**
- ✓ Use as a **return type** when a result may be missing.
- ✗ Don't use for fields, method parameters or in collections.
- ✗ Don't call `get()` without checking: it throws `NoSuchElementException` when empty. Use `orElseThrow()` instead.
- ✗ Don't return `null` from a method that returns Optional.

@@ 2 | MODULE | Concurrency and Multithreading | Threads, locks, executors, CompletableFuture | MUST
**Concurrency** means doing several things at overlapping times. Servers handle many users at once, so you must understand threads and the bugs they cause.

@@ 3 | TOPIC | Threads Basics | Creating threads, lifecycle, Runnable vs Callable | MUST
# Thread basics

A **thread** is an independent path of execution inside a program.
**Analogy:** a restaurant kitchen with several cooks (threads) sharing the same fridge (heap memory). Each cook has their own recipe card (stack).

```java
// 1. Runnable with a lambda (preferred)
Runnable task = () -> System.out.println("Running in " + Thread.currentThread().getName());
Thread t = new Thread(task, "worker-1");
t.start();          // starts a NEW thread that runs task.run()
t.join();           // the main thread waits until t finishes

// 2. Callable returns a value and can throw checked exceptions
Callable<Integer> job = () -> 6 * 7;

// 3. Virtual threads (Java 21): very cheap, millions possible
Thread.startVirtualThread(() -> System.out.println("virtual!"));
```

**Line by line**
- `new Thread(task, "worker-1")`: creates the thread object (state: NEW). Naming threads helps when reading logs.
- `start()`: asks the JVM to create a real thread and call `run()` on it.
- **Calling `run()` directly does NOT start a new thread.** It just runs the code on the current thread. This is a classic MCQ.
- `join()`: waits for the thread to finish.
- `Callable` vs `Runnable`: Callable **returns a result** and can throw checked exceptions. Runnable returns void.

## Thread lifecycle
NEW → (start) → RUNNABLE → (waiting for a lock) BLOCKED / (wait, join, sleep) WAITING / TIMED_WAITING → back to RUNNABLE → TERMINATED.

## sleep vs wait
| | `Thread.sleep(ms)` | `obj.wait()` |
|---|---|---|
| Belongs to | Thread (static) | Object |
| Releases the lock? | **No** | **Yes** |
| Woken by | Time passing | `notify()` / `notifyAll()` |
| Must hold the lock? | No | Yes (inside `synchronized`) |

@@ 3 | TOPIC | synchronized, volatile and Atomic | Fixing race conditions and visibility problems | MUST
# synchronized, volatile and Atomic

## The problem: a race condition

```java
class Counter {
    private int count = 0;
    void increment() { count++; }   // NOT atomic: read -> add 1 -> write
}
```
`count++` is really three steps. Two threads can both read 5, both write 6, and one increment is lost.
**Analogy:** two clerks update the same paper ledger. Both read "5", both write "6". One deposit disappears.

## Fix 1: synchronized (a lock)

```java
class Counter {
    private int count = 0;
    synchronized void increment() { count++; }     // only one thread at a time
    synchronized int get() { return count; }
}
```
- `synchronized` on a method locks on `this`. Other threads calling any synchronized method of the **same object** wait.
- It also guarantees **visibility**: changes are flushed to main memory when the lock is released.
- **Analogy:** a single bathroom key. Whoever holds the key goes in; everyone else waits.

## Fix 2: AtomicInteger (lock-free)

```java
class Counter {
    private final AtomicInteger count = new AtomicInteger();
    void increment() { count.incrementAndGet(); }  // atomic, uses CPU CAS
    int get() { return count.get(); }
}
```
- Uses **CAS (Compare-And-Swap)**: "set to 6 only if it is still 5, otherwise retry". There is no blocking, so it is fast for simple counters.

## volatile: visibility only, NOT atomicity

```java
class Worker implements Runnable {
    private volatile boolean running = true;   // every thread sees the latest value
    public void run() { while (running) { /* work */ } }
    public void stop() { running = false; }
}
```
- Without `volatile`, a thread may keep a cached copy of `running` and **never see** the change, so it loops forever.
- `volatile` makes every read go to main memory. It does **not** make `count++` safe.
- **Analogy:** a notice board everyone must check, instead of each person relying on their memory of yesterday's notice.

| Tool | Visibility | Atomicity | Use for |
|---|---|---|---|
| `volatile` | ✓ | ✗ | Flags (stop signals) |
| `AtomicInteger/Long` | ✓ | ✓ (single variable) | Counters |
| `synchronized` / `ReentrantLock` | ✓ | ✓ (whole block) | Several variables that change together |

`ReentrantLock` adds `tryLock(timeout)` (avoids waiting forever), fairness and multiple conditions. Always unlock in `finally`:
```java
lock.lock();
try { /* critical section */ } finally { lock.unlock(); }
```

@@ 3 | TOPIC | ExecutorService and CompletableFuture | Thread pools and async pipelines | MUST
# ExecutorService and CompletableFuture

Creating a new thread for every task is expensive. A **thread pool** keeps a few threads alive and reuses them.
**Analogy:** a taxi stand. Instead of buying a new car for every trip, a fixed set of taxis takes passengers in turn.

```java
ExecutorService pool = Executors.newFixedThreadPool(4);   // 4 reusable threads

Future<Integer> f = pool.submit(() -> {                   // Callable
    Thread.sleep(500);
    return 42;
});
Integer result = f.get();          // blocks until the result is ready
pool.shutdown();                   // stop accepting tasks, finish the running ones
pool.awaitTermination(10, TimeUnit.SECONDS);
```

**Line by line**
- `newFixedThreadPool(4)`: at most 4 tasks run at once. The others wait in a queue.
- `submit(callable)` returns a **Future**, a receipt you can exchange for the result later.
- `f.get()`: waits (blocks) for the result.
- `shutdown()`: **always shut pools down**, or the JVM may never exit.
- Java 21: `Executors.newVirtualThreadPerTaskExecutor()` is great for I/O-heavy work.

## CompletableFuture: non-blocking pipelines

```java
CompletableFuture<Member> memberF = CompletableFuture.supplyAsync(() -> memberApi.get(id));
CompletableFuture<List<Loan>> loansF = CompletableFuture.supplyAsync(() -> loanApi.byMember(id));

CompletableFuture<String> summary = memberF
        .thenCombine(loansF, (m, loans) -> m.getName() + " has " + loans.size() + " loans")
        .exceptionally(ex -> "Unavailable: " + ex.getMessage());

System.out.println(summary.join());
```

**Line by line**
- `supplyAsync`: runs the lambda on another thread (the common ForkJoinPool by default).
- The two calls run **in parallel**: total time ≈ the slower call, not the sum of both.
- `thenCombine`: when **both** are done, combine their results.
- `thenApply` (transform), `thenCompose` (chain another async call), `allOf` (wait for many).
- `exceptionally`: a fallback if anything fails.
- `join()`: like `get()`, but throws an unchecked exception.

**Analogy:** you order tea and a sandwich at two counters at once and then sit down when both are ready, instead of waiting at one counter and then queueing at the next.

@@ 3 | TOPIC | Producer–Consumer and Concurrent Collections | BlockingQueue and ConcurrentHashMap | SHOULD
# Producer–consumer and concurrent collections

**Producer–consumer:** one thread produces work, and another consumes it, through a shared queue.
**Analogy:** a bakery. The baker (producer) puts bread on a shelf (the queue), and the cashier (consumer) takes it. If the shelf is full the baker waits; if it is empty the cashier waits.

```java
BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);   // capacity 10

Thread producer = new Thread(() -> {
    try {
        for (int i = 1; i <= 20; i++) {
            queue.put("payment-" + i);      // waits if the queue is full
        }
        queue.put("DONE");                  // poison pill: tells the consumer to stop
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
});

Thread consumer = new Thread(() -> {
    try {
        while (true) {
            String item = queue.take();     // waits if the queue is empty
            if (item.equals("DONE")) break;
            System.out.println("Processing " + item);
        }
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
});

producer.start(); consumer.start();
```

**Line by line**
- `ArrayBlockingQueue<>(10)`: a thread-safe queue with a fixed size.
- `put`: blocks when full, which gives **back-pressure** (the producer can't flood memory).
- `take`: blocks when empty. No busy-waiting loop is needed.
- `"DONE"` is a **poison pill**: a special message meaning "no more work".
- `Thread.currentThread().interrupt()`: restore the interrupt flag. Never silently swallow `InterruptedException`.

## ConcurrentHashMap
- A thread-safe Map without locking the whole map: it locks per bucket and uses CAS.
- Use atomic compound methods, not check-then-act:
```java
ConcurrentHashMap<String, Integer> hits = new ConcurrentHashMap<>();
hits.merge("home", 1, Integer::sum);          // atomic increment
// WRONG (race): if (!m.containsKey(k)) m.put(k, v);
hits.putIfAbsent("about", 0);                 // atomic
```
- Other thread-safe collections: `CopyOnWriteArrayList` (many reads, few writes), `ConcurrentLinkedQueue`.
- `Collections.synchronizedMap` locks the whole map, which is slower.

@@ 2 | TOPIC | JVM Internals | Memory areas, garbage collection, class loading | MUST
# JVM internals

## Memory areas

| Area | What lives there | Shared? |
|---|---|---|
| **Heap** | All objects and arrays | Shared by all threads |
| **Stack** | Method frames: local variables, references, return addresses | One per thread |
| **Metaspace** | Class metadata (replaced PermGen in Java 8) | Shared |
| **PC register** | The current instruction of each thread | Per thread |

**Analogy:** the heap is a warehouse where all the goods (objects) are stored. Each worker (thread) has a clipboard (stack) listing which shelves they are using right now.

```java
void process() {
    int count = 5;                   // primitive: on the STACK
    Member m = new Member("Rahim");  // reference 'm' on the stack, object on the HEAP
}                                     // frame removed; the object becomes garbage if nothing else refers to it
```
- Local primitives and references live in the method's **stack frame**, and the frame disappears when the method returns.
- `new` always creates the object on the **heap**.
- Deep or infinite recursion → **StackOverflowError**. Too many live objects → **OutOfMemoryError: Java heap space**.

## Garbage collection (GC)
The GC automatically frees objects that are no longer **reachable** from "GC roots" (local variables, static fields, active threads).
**Analogy:** a cleaner removes items in the warehouse that no clipboard refers to any more.

**Generational heap**, because most objects die young:
- **Young generation** (Eden + 2 Survivor spaces): new objects. Frequent, fast **minor GC**.
- **Old generation**: objects that survived many minor GCs. Less frequent **major/full GC**.

| Collector | Notes |
|---|---|
| G1 (default since Java 9) | Splits the heap into regions, predictable pauses |
| ZGC / Shenandoah | Very low pauses (< 1 ms), for large heaps |
| Parallel | Maximum throughput, longer pauses |

**Common JVM flags:** `-Xms512m` (initial heap), `-Xmx2g` (max heap), `-XX:+UseG1GC`, `-XX:+HeapDumpOnOutOfMemoryError`.

## Memory leaks in Java
Yes, they can happen: objects that are still **referenced** but never used again. Examples: static collections that keep growing, unclosed resources, listeners that are never removed, ThreadLocal values in pools.

## Class loading
Bootstrap loader (core Java) → Platform loader → Application loader (your classpath). **Parent delegation:** a loader first asks its parent, so nobody can replace `java.lang.String` with a fake.

## JIT
The JVM starts by interpreting bytecode, then the **JIT (Just-In-Time) compiler** compiles "hot" (frequently used) methods into fast native code.

@@ 2 | TOPIC | Modern Java (17/21) | var, switch expressions, text blocks, records, sealed, pattern matching, virtual threads | MUST
# Modern Java (17 / 21)

Spring Boot 3 requires Java 17+, so interviewers expect you to know these features.

```java
// 1. var (Java 10): the local type is inferred
var members = new ArrayList<Member>();     // type is ArrayList<Member>

// 2. Switch expressions (Java 14): return a value, no fall-through
String label = switch (status) {
    case PENDING -> "Waiting";
    case ACTIVE, OVERDUE -> "Running";
    case CLOSED -> {
        log.info("closed loan");
        yield "Done";                      // yield returns from a block
    }
};

// 3. Text blocks (Java 15): multi-line strings
String sql = """
        SELECT id, amount
        FROM loan
        WHERE status = 'ACTIVE'
        """;

// 4. Records (Java 16)
record Point(int x, int y) {}

// 5. Pattern matching for instanceof (Java 16)
if (obj instanceof String s && !s.isBlank()) {
    System.out.println(s.length());        // s is already cast
}

// 6. Sealed classes (Java 17): only listed subclasses are allowed
sealed interface Payment permits Cash, Bkash, BankTransfer {}
record Cash(BigDecimal amount) implements Payment {}
record Bkash(String phone, BigDecimal amount) implements Payment {}
record BankTransfer(String iban, BigDecimal amount) implements Payment {}

// 7. Pattern matching for switch + record patterns (Java 21)
String describe(Payment p) {
    return switch (p) {
        case Cash c -> "Cash " + c.amount();
        case Bkash(String phone, BigDecimal amt) -> "bKash from " + phone;
        case BankTransfer b when b.amount().compareTo(new BigDecimal("100000")) > 0 -> "Large transfer";
        case BankTransfer b -> "Transfer";
    };                                     // no default needed: the compiler knows all cases
}

// 8. Virtual threads (Java 21)
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) exec.submit(() -> callSlowApi());
}
```

**Line by line**
1. `var`: less typing. It is still statically typed (the type can't change), and it only works for local variables.
2. A switch **expression** returns a value. `->` means no `break` is needed and there is no accidental fall-through. `yield` returns a value from a `{ }` block.
3. A text block keeps line breaks and removes common indentation. Great for SQL and JSON.
4. Records: see "Enums and Records".
5. `instanceof String s`: test and cast in one go. `s` is only in scope where the test is true.
6. `sealed ... permits`: a closed family of types. **Analogy:** a club with a fixed membership list.
7. The switch can **deconstruct** a record (`Bkash(String phone, ...)`) and add a `when` guard. Because `Payment` is sealed, the compiler checks that every case is covered.
8. Virtual threads are lightweight threads managed by the JVM. Blocking I/O no longer wastes an OS thread, so 10,000 concurrent calls are fine. In Spring Boot 3.2+: `spring.threads.virtual.enabled=true`.

@@ 2 | TOPIC | Build Tools — Maven | pom.xml, lifecycle, dependency scopes | MUST
# Maven

Maven **builds** your project: it downloads libraries (dependencies), compiles, runs tests and packages a JAR.
**Analogy:** a recipe card plus a grocery delivery. You list the ingredients (dependencies), and Maven fetches them and follows the cooking steps (lifecycle).

```xml
<project>
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
    </parent>

    <groupId>bd.org.pksf</groupId>
    <artifactId>loan-service</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**Line by line**
- `<parent>`: inherits default settings and **managed versions** of Spring libraries, which is why the dependencies below have no `<version>`.
- `groupId:artifactId:version` (**GAV**): the unique "address" of a project or library.
- `<scope>runtime</scope>`: the PostgreSQL driver is needed only when the app runs, not to compile.
- `<scope>test</scope>`: JUnit/Mockito are only on the test classpath and are not packaged.
- `spring-boot-maven-plugin`: builds an executable "fat JAR" that bundles all dependencies.

## Lifecycle phases (each runs all the earlier ones)
`validate → compile → test → package → verify → install → deploy`

```bash
mvn clean package          # delete target/, compile, test, build the JAR
mvn clean package -DskipTests
mvn test                   # just run the tests
mvn dependency:tree        # see where each library came from (resolve conflicts)
mvn spring-boot:run        # run the app
```
- `install` copies the JAR into your local repository (`~/.m2`). `deploy` uploads it to a remote repository (Nexus/Artifactory).

**Scopes:** `compile` (default, everywhere), `provided` (the server supplies it, e.g. servlet-api), `runtime`, `test`.
**Gradle** is the alternative: `build.gradle` in Groovy/Kotlin, usually faster builds.

@@ 2 | TOPIC | BigDecimal for Money | Why never use double for money | MUST
# BigDecimal for money

```java
System.out.println(0.1 + 0.2);         // 0.30000000000000004  (!)
```
`double` stores numbers in **binary**, and 0.1 can't be represented exactly in binary, just as 1/3 can't be written exactly in decimal (0.3333...). Over millions of transactions the tiny errors add up.

**Analogy:** measuring cloth with a ruler whose markings are slightly blurred. One measurement looks fine, but after a thousand measurements you've lost a metre.

```java
BigDecimal a = new BigDecimal("0.10");                 // String constructor: exact
BigDecimal b = new BigDecimal("0.20");
BigDecimal sum = a.add(b);                             // 0.30 exactly

BigDecimal principal = new BigDecimal("10000.00");
BigDecimal rate = new BigDecimal("0.12");              // 12%
BigDecimal interest = principal.multiply(rate)
        .setScale(2, RoundingMode.HALF_UP);            // 1200.00

BigDecimal weekly = principal.divide(BigDecimal.valueOf(46), 2, RoundingMode.HALF_UP);  // 217.39

new BigDecimal("2.0").equals(new BigDecimal("2.00"));     // false (scale differs)
new BigDecimal("2.0").compareTo(new BigDecimal("2.00"));  // 0     (same value)
```

**Line by line**
- `new BigDecimal("0.10")`: **always use the String constructor** (or `BigDecimal.valueOf(double)`). `new BigDecimal(0.1)` copies the binary error: 0.1000000000000000055...
- BigDecimal is **immutable**, so `a.add(b)` returns a new object. You must assign it.
- `setScale(2, HALF_UP)`: keep 2 decimal places and round .5 up.
- `divide` **must** have a scale and a rounding mode. 10000/46 never terminates, so without them it throws `ArithmeticException`.
- `equals` compares the value **and** the scale. `compareTo` compares the value only, so **use `compareTo` for money comparisons**.

In the database, use `NUMERIC(14,2)`. In JPA: `@Column(precision = 14, scale = 2) private BigDecimal amount;`

@@ 2 | TOPIC | Phase 2 — Interview Questions | Quick Q&A revision for Core Java | MUST
# Phase 2 interview questions

**Q. Why is String immutable?**
A. For security (it can't be changed after validation), thread safety, cached hashCode (a good HashMap key) and to allow the String pool to share instances.

**Q. == vs equals()?**
A. `==` compares references (the same object). `equals()` compares logical content when it is overridden.

**Q. What happens if you override equals but not hashCode?**
A. Equal objects may land in different buckets, so HashSet/HashMap lookups fail and duplicates appear.

**Q. ArrayList vs LinkedList?**
A. ArrayList is array-backed with O(1) random access and cache-friendly. LinkedList has O(n) access but cheap insertion at the ends. ArrayList is preferred almost always.

**Q. HashMap vs Hashtable vs ConcurrentHashMap?**
A. HashMap is not thread-safe and allows one null key. Hashtable is legacy, fully synchronized and allows no nulls. ConcurrentHashMap is thread-safe with fine-grained locking/CAS, allows no nulls and scales well.

**Q. Checked vs unchecked exceptions?**
A. Checked exceptions must be caught or declared (IOException). Unchecked ones extend RuntimeException and usually mean bugs (NPE).

**Q. What does volatile guarantee?**
A. Visibility of changes across threads and no reordering around it. It does **not** make compound actions like `count++` atomic.

**Q. Difference between start() and run()?**
A. `start()` creates a new thread that calls `run()`. Calling `run()` directly executes on the current thread.

**Q. What is a functional interface?**
A. An interface with exactly one abstract method, such as Runnable, Comparator or Function. Lambdas implement it.

**Q. map vs flatMap in streams?**
A. `map` transforms each element into one element. `flatMap` transforms each element into a stream and flattens them into one stream.

**Q. Explain heap vs stack.**
A. The heap stores all objects and is shared and garbage-collected. Each thread's stack stores method frames with local primitives and references, and is freed when the method returns.

**Q. What is the String pool?**
A. A memory area where string literals are stored once and shared. `intern()` adds or returns a pooled copy.

**Q. Why use BigDecimal for money?**
A. double is binary floating point and can't represent 0.1 exactly, so rounding errors accumulate. BigDecimal is exact decimal arithmetic with controlled rounding.

**Q. final, finally, finalize?**
A. `final`: can't reassign / override / extend. `finally`: a block that always runs after try. `finalize`: a deprecated GC callback.

**Q. Can we override a static method?**
A. No. Static methods are hidden, not overridden. They are resolved by the reference type at compile time.

**Q. What is a memory leak in Java?**
A. Objects that are no longer needed but are still referenced (for example from a static map), so the GC can't free them.
