@@ 3 | TOPIC | Practice — Coding drill: Stream interview problems (25 tasks) | Twenty-five classic stream questions on grouping, aggregation, flatMap, strings and numbers with solutions | MUST
# Coding drill: Stream interview problems (25 tasks)

These are the stream questions interviewers actually ask, from screening calls ("names of employees earning more than X") to senior live-coding ("second highest salary per department", "group anagrams"). Tasks 1 to 16 and 23 to 24 use the shared employee data below; tasks 17 to 22 and 25 work on strings and numbers. Write each answer on paper or in JShell first, then open the solution. Try to say aloud which collector or operation you use and why: interviewers grade the explanation as much as the code.

## Shared data
```java
import java.util.List;

public class EmployeeData {

    record Employee(int id, String name, String dept, int salary, int age, String city, List<String> skills) { }

    static final List<Employee> EMPLOYEES = List.of(
            new Employee(1, "Ana", "Engineering", 9500, 34, "Berlin", List.of("java", "spring", "sql")),
            new Employee(2, "Bob", "Engineering", 7200, 28, "Paris", List.of("java", "kotlin")),
            new Employee(3, "Cara", "Sales", 5100, 41, "Berlin", List.of("negotiation", "crm")),
            new Employee(4, "Dan", "Sales", 6300, 37, "Madrid", List.of("crm", "sql")),
            new Employee(5, "Eve", "HR", 4800, 25, "Paris", List.of("recruiting")),
            new Employee(6, "Finn", "Engineering", 11000, 45, "Berlin", List.of("java", "aws", "sql")),
            new Employee(7, "Gina", "HR", 5600, 31, "Madrid", List.of("recruiting", "payroll")),
            new Employee(8, "Hugo", "Marketing", 5100, 29, "Paris", List.of("seo", "crm")));

    public static void main(String[] args) {
        System.out.println(EMPLOYEES.size() + " employees loaded");
    }
}
```
All solutions assume `import java.util.*;`, `import java.util.function.Function;`, `import java.util.stream.*;` and `import static java.util.stream.Collectors.*;`, and a local variable `List<Employee> employees = EMPLOYEES;`. Results are put into `TreeMap` or `LinkedHashMap` where order matters so that output is deterministic.

## 1. Names of employees earning more than 6000, alphabetically
**Hint:** filter, map, sorted.

<details><summary>Solution</summary>

```java
List<String> wellPaid = employees.stream()
        .filter(e -> e.salary() > 6000)
        .map(Employee::name)
        .sorted()
        .toList();
```
```text
[Ana, Bob, Dan, Finn]
```
Filter first so fewer elements are mapped and sorted. `toList()` (Java 16) returns an unmodifiable list.
</details>

## 2. Group employee names by department
**Hint:** `groupingBy` with a map factory and a `mapping` downstream.

<details><summary>Solution</summary>

```java
Map<String, List<String>> namesByDept = employees.stream()
        .collect(groupingBy(Employee::dept, TreeMap::new, mapping(Employee::name, toList())));
```
```text
{Engineering=[Ana, Bob, Finn], HR=[Eve, Gina], Marketing=[Hugo], Sales=[Cara, Dan]}
```
Without `mapping` you would get whole `Employee` objects per key; without `TreeMap::new` the key order is unspecified.
</details>

## 3. Average salary per department
**Hint:** `averagingInt` returns a `Double`.

<details><summary>Solution</summary>

```java
Map<String, Double> avgByDept = employees.stream()
        .collect(groupingBy(Employee::dept, TreeMap::new, averagingInt(Employee::salary)));
avgByDept.forEach((dept, avg) -> System.out.printf("%s=%.2f%n", dept, avg));
```
```text
Engineering=9233.33
HR=5200.00
Marketing=5100.00
Sales=5700.00
```
This is the single most asked stream question. Mention that for money in production you would use `BigDecimal` or integer cents and round explicitly.
</details>

## 4. Highest paid employee in each department
**Hint:** `maxBy` gives `Optional<Employee>`; unwrap it with `collectingAndThen`.

<details><summary>Solution</summary>

```java
Map<String, String> topEarnerByDept = employees.stream()
        .collect(groupingBy(Employee::dept, TreeMap::new,
                collectingAndThen(maxBy(Comparator.comparingInt(Employee::salary)),
                        top -> top.orElseThrow().name())));
```
```text
{Engineering=Finn, HR=Gina, Marketing=Hugo, Sales=Dan}
```
`maxBy` returns an `Optional` because a downstream collector could in principle see no elements; inside `groupingBy` every group has at least one, so `orElseThrow()` is safe. Alternative: `toMap(Employee::dept, Function.identity(), BinaryOperator.maxBy(comparator))`.
</details>

## 5. Second highest salary
**Hint:** distinct, sort descending, skip one.

<details><summary>Solution</summary>

```java
Optional<Integer> secondHighest = employees.stream()
        .map(Employee::salary)
        .distinct()
        .sorted(Comparator.reverseOrder())
        .skip(1)
        .findFirst();
```
```text
9500
```
`distinct()` matters when two people share the top salary. Return `Optional` because there may be fewer than two distinct salaries. For the Nth highest, use `skip(n - 1)`. On huge data, a single pass with a bounded `PriorityQueue` avoids the full sort.
</details>

## 6. Count employees per city
**Hint:** `counting()` returns `Long`.

<details><summary>Solution</summary>

```java
Map<String, Long> perCity = employees.stream()
        .collect(groupingBy(Employee::city, TreeMap::new, counting()));
```
```text
{Berlin=3, Madrid=2, Paris=3}
```
</details>

## 7. Split employees into age 30 and over versus under 30
**Hint:** two buckets means `partitioningBy`.

<details><summary>Solution</summary>

```java
Map<Boolean, List<String>> byAge = employees.stream()
        .collect(partitioningBy(e -> e.age() >= 30, mapping(Employee::name, toList())));
```
```text
{false=[Bob, Eve, Hugo], true=[Ana, Cara, Dan, Finn, Gina]}
```
`partitioningBy` always contains both keys, even if a side is empty, which `groupingBy(e -> e.age() >= 30)` does not guarantee.
</details>

## 8. Total salary per department, sorted by total descending
**Hint:** aggregate first, then sort the entry set by value and collect into a `LinkedHashMap`.

<details><summary>Solution</summary>

```java
Map<String, Integer> billByDept = employees.stream()
        .collect(groupingBy(Employee::dept, summingInt(Employee::salary)))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
```
```text
{Engineering=27700, Sales=11400, HR=10400, Marketing=5100}
```
Two traps: the type witness `Map.Entry.<String, Integer>comparingByValue()` is needed because `.reversed()` breaks inference, and the final map must be a `LinkedHashMap` or the sort order is lost.
</details>

## 9. All distinct skills, sorted
**Hint:** every employee has a list of skills: flatten it.

<details><summary>Solution</summary>

```java
List<String> skills = employees.stream()
        .flatMap(e -> e.skills().stream())
        .distinct()
        .sorted()
        .toList();
```
```text
[aws, crm, java, kotlin, negotiation, payroll, recruiting, seo, spring, sql]
```
`map(Employee::skills)` would give a `Stream<List<String>>`; `flatMap` gives a `Stream<String>`.
</details>

## 10. Most common skill
**Hint:** flatten, count with `groupingBy(identity, counting())`, then take the max entry. Watch out for ties.

<details><summary>Solution</summary>

```java
Map.Entry<String, Long> mostCommon = employees.stream()
        .flatMap(e -> e.skills().stream())
        .collect(groupingBy(Function.identity(), counting()))
        .entrySet().stream()
        .max(Map.Entry.<String, Long>comparingByValue()
                .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
        .orElseThrow();
```
```text
crm=3
```
`crm`, `java` and `sql` all appear three times. Without a tie-breaker, the winner depends on `HashMap` iteration order. The secondary comparator (key in reverse order inside `max`) picks the alphabetically first key. Saying "there is a tie, so I add a deterministic tie-breaker" earns points.
</details>

## 11. Map of employee id to name
**Hint:** `toMap`; ids are unique, so no merge function is needed, but say what happens if they were not.

<details><summary>Solution</summary>

```java
Map<Integer, String> nameById = employees.stream()
        .collect(toMap(Employee::id, Employee::name));
System.out.println(nameById.get(6) + " " + nameById.size());
```
```text
Finn 8
```
With duplicate keys, the two-argument `toMap` throws `IllegalStateException: Duplicate key`; add a merge function such as `(first, second) -> first`.
</details>

## 12. Department with the most employees
**Hint:** count per department, then max by value.

<details><summary>Solution</summary>

```java
String largestDept = employees.stream()
        .collect(groupingBy(Employee::dept, counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow();
```
```text
Engineering
```
Engineering has 3 people, more than any other department. With ties, add a tie-breaker as in task 10.
</details>

## 13. Comma-separated names per department
**Hint:** `joining` as the downstream collector.

<details><summary>Solution</summary>

```java
Map<String, String> joined = employees.stream()
        .collect(groupingBy(Employee::dept, TreeMap::new, mapping(Employee::name, joining(", "))));
```
```text
{Engineering=Ana, Bob, Finn, HR=Eve, Gina, Marketing=Hugo, Sales=Cara, Dan}
```
</details>

## 14. Top 3 earners
**Hint:** sort by salary descending, then limit.

<details><summary>Solution</summary>

```java
List<String> top3 = employees.stream()
        .sorted(Comparator.comparingInt(Employee::salary).reversed())
        .limit(3)
        .map(Employee::name)
        .toList();
```
```text
[Finn, Ana, Bob]
```
`sorted` is a barrier, so all elements are sorted even though three are needed: O(n log n). For very large inputs, a min-heap of size 3 is O(n log 3).
</details>

## 15. Match questions
Is there anyone in HR earning more than 6000? Does everyone earn more than 4000? Is nobody under 18?

<details><summary>Solution</summary>

```java
boolean anyRichHr = employees.stream().anyMatch(e -> e.dept().equals("HR") && e.salary() > 6000);
boolean allAbove4000 = employees.stream().allMatch(e -> e.salary() > 4000);
boolean noMinors = employees.stream().noneMatch(e -> e.age() < 18);
System.out.println(anyRichHr + " " + allAbove4000 + " " + noMinors);
```
```text
false true true
```
All three short-circuit. On an empty list they return false, true, true respectively (vacuous truth for `allMatch` and `noneMatch`).
</details>

## 16. Salary statistics in one pass
**Hint:** `summarizingInt` or `mapToInt(...).summaryStatistics()`.

<details><summary>Solution</summary>

```java
IntSummaryStatistics stats = employees.stream().collect(summarizingInt(Employee::salary));
System.out.println(stats.getMin() + " " + stats.getMax() + " " + stats.getSum() + " "
        + stats.getAverage() + " " + stats.getCount());
```
```text
4800 11000 54600 6825.0 8
```
One traversal instead of four separate streams for min, max, sum and average.
</details>

## 17. Word frequency in a sentence
Count how often each word appears in `"the quick fox and the lazy dog and the cat"`.

<details><summary>Solution</summary>

```java
String text = "the quick fox and the lazy dog and the cat";
Map<String, Long> frequency = Arrays.stream(text.split("\\s+"))
        .collect(groupingBy(Function.identity(), TreeMap::new, counting()));
```
```text
{and=2, cat=1, dog=1, fox=1, lazy=1, quick=1, the=3}
```
For real text, normalize first: `text.toLowerCase().split("\\W+")` and filter out blanks.
</details>

## 18. First non-repeated character
Find the first character that appears exactly once in `"swiss"`.

<details><summary>Solution</summary>

```java
String word = "swiss";
Optional<Character> firstUnique = word.chars()
        .mapToObj(c -> (char) c)
        .collect(groupingBy(Function.identity(), LinkedHashMap::new, counting()))
        .entrySet().stream()
        .filter(entry -> entry.getValue() == 1)
        .map(Map.Entry::getKey)
        .findFirst();
```
```text
w
```
`String.chars()` gives an `IntStream`; `mapToObj(c -> (char) c)` boxes to `Character`. `LinkedHashMap::new` keeps first-seen order, which is what makes "first" correct; a `HashMap` would give a random answer.
</details>

## 19. Find duplicates in a list
Return the values that occur more than once in `[4, 7, 1, 4, 9, 7, 7, 2]`.

<details><summary>Solution</summary>

```java
List<Integer> numbers = List.of(4, 7, 1, 4, 9, 7, 7, 2);

// Pure functional: count, then keep counts above 1
Set<Integer> duplicates = numbers.stream()
        .collect(groupingBy(Function.identity(), counting()))
        .entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(Map.Entry::getKey)
        .collect(toCollection(TreeSet::new));

// Common interview shortcut: Set.add returns false for a repeat
Set<Integer> seen = new HashSet<>();
List<Integer> duplicatesInOrder = numbers.stream().filter(n -> !seen.add(n)).distinct().toList();
```
```text
[4, 7]
[4, 7]
```
The shortcut uses a stateful lambda (it mutates `seen`), which is fine sequentially but wrong in a parallel stream; mention that trade-off. The counting version is safe and also tells you how many times each value occurs.
</details>

## 20. Sum of squares of even numbers from 1 to 10
**Hint:** stay in `IntStream` to avoid boxing.

<details><summary>Solution</summary>

```java
int sum = IntStream.rangeClosed(1, 10)
        .filter(n -> n % 2 == 0)
        .map(n -> n * n)
        .sum();
```
```text
220
```
4 + 16 + 36 + 64 + 100 = 220.
</details>

## 21. Palindrome check
Check whether `"level"`, `"Racecar"` and `"stream"` are palindromes, ignoring case, using a stream.

<details><summary>Solution</summary>

```java
static boolean isPalindrome(String word) {
    String s = word.toLowerCase();
    return IntStream.range(0, s.length() / 2)
            .allMatch(i -> s.charAt(i) == s.charAt(s.length() - 1 - i));
}
// level=true Racecar=true stream=false
```
```text
level=true Racecar=true stream=false
```
Only half the indices are compared, and `allMatch` stops at the first mismatch. `new StringBuilder(s).reverse().toString().equals(s)` is shorter but allocates a copy.
</details>

## 22. Group anagrams
Group `["listen", "silent", "enlist", "google", "gogole", "cat", "act", "dog"]` into anagram groups, keeping first-seen order.

<details><summary>Solution</summary>

```java
List<String> words = List.of("listen", "silent", "enlist", "google", "gogole", "cat", "act", "dog");
Collection<List<String>> groups = words.stream()
        .collect(groupingBy(word -> {
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            return new String(chars);          // canonical key: sorted letters
        }, LinkedHashMap::new, toList()))
        .values();
```
```text
[[listen, silent, enlist], [google, gogole], [cat, act], [dog]]
```
The classifier computes a canonical key (the sorted letters), so all anagrams land in the same group. Complexity is O(n times k log k) for n words of length k.
</details>

## 23. Nested grouping: department, then city, then count
**Hint:** a `groupingBy` inside a `groupingBy`.

<details><summary>Solution</summary>

```java
Map<String, Map<String, Long>> deptCity = employees.stream()
        .collect(groupingBy(Employee::dept, TreeMap::new,
                groupingBy(Employee::city, TreeMap::new, counting())));
```
```text
{Engineering={Berlin=2, Paris=1}, HR={Madrid=1, Paris=1}, Marketing={Paris=1}, Sales={Berlin=1, Madrid=1}}
```
</details>

## 24. Distinct skills per department
**Hint:** `flatMapping` (Java 9) flattens inside each group.

<details><summary>Solution</summary>

```java
Map<String, Set<String>> skillsByDept = employees.stream()
        .collect(groupingBy(Employee::dept, TreeMap::new,
                flatMapping(e -> e.skills().stream(), toCollection(TreeSet::new))));
```
```text
{Engineering=[aws, java, kotlin, spring, sql], HR=[payroll, recruiting], Marketing=[crm, seo], Sales=[crm, negotiation, sql]}
```
On Java 8 you would write `mapping(Employee::skills, ...)` and then flatten a set of lists by hand; `flatMapping` does it in the collector.
</details>

## 25. First 10 Fibonacci numbers with a stream
**Hint:** `Stream.iterate` with a pair as state.

<details><summary>Solution</summary>

```java
List<Long> fibonacci = Stream.iterate(new long[]{0, 1}, pair -> new long[]{pair[1], pair[0] + pair[1]})
        .limit(10)
        .map(pair -> pair[0])
        .toList();
```
```text
[0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
```
The state is a two-element array `[current, next]`; each step creates a new array, so no shared mutation happens. `limit` makes the infinite stream finite. A record `Pair(long a, long b)` reads better than an array in real code.
</details>

@@ 3 | TOPIC | Practice — Phase 7 review: Functional Java and Streams | Cheat sheet, key snippets, top 10 questions and rapid-fire flashcards for Phase 7 | MUST
# Phase 7 review: Functional Java and Streams

## Cheat sheet

| Idea | Remember |
|---|---|
| Lambda | Anonymous function implementing a functional interface's single abstract method |
| Functional interface | Exactly one abstract method; `@FunctionalInterface` is optional but checked |
| Capture rule | Locals must be effectively final; `this` means the enclosing instance |
| Implementation | Synthetic method + `invokedynamic` + `LambdaMetafactory`; non-capturing lambdas cached |
| Core interfaces | `Function` apply, `Predicate` test, `Consumer` accept, `Supplier` get |
| Variants | `Bi*`, `UnaryOperator`, `BinaryOperator`, primitive `IntPredicate`, `ToIntFunction` |
| Composition | `andThen`, `compose`, `and`, `or`, `negate`, `Predicate.not` (11) |
| Method refs | Static, bound (`obj::m`), unbound (`Type::m`, receiver = first param), constructor (`Type::new`) |
| Pipeline | Source, lazy intermediate ops, one terminal op |
| Laziness | Nothing runs until the terminal op; elements flow vertically |
| Short-circuit | `limit`, `takeWhile`, `findFirst`, `findAny`, `anyMatch`, `allMatch`, `noneMatch` |
| Stateful ops | `sorted` (barrier), `distinct`, `limit`, `skip` |
| Single use | Reuse throws `IllegalStateException` |
| `toList()` | Java 16, unmodifiable, allows nulls |
| `groupingBy` | `Map<K, List<T>>`, downstream collectors, map factory for order |
| `partitioningBy` | Always both `true` and `false` keys |
| `toMap` | Duplicate key throws; add merge function; null values throw |
| `flatMap` | One to many, flattened |
| `reduce` | Neutral identity, associative op; `Optional` without identity |
| `Optional` | Return types only; `orElseGet` lazy, `orElse` eager; `orElseThrow` for not-found |
| Parallel | Common ForkJoinPool; CPU-bound, big, splittable data only; no shared state, no blocking |

### Key code snippets
```java
// Lambda, functional interface, composition
@FunctionalInterface
interface DiscountRule { BigDecimal apply(Order order); }

Predicate<Order> paid = o -> o.status() == Status.PAID;
Predicate<Order> big = o -> o.total().compareTo(LIMIT) > 0;
Function<Order, BigDecimal> gross = ((Function<Order, BigDecimal>) Order::total).andThen(t -> t.multiply(VAT));
List<Order> vip = orders.stream().filter(paid.and(big)).toList();
```

```java
// Method references of all four kinds
Function<String, Integer> parse = Integer::parseInt;          // static
Consumer<String> print = System.out::println;                  // bound
Function<String, String> upper = String::toUpperCase;          // unbound
Supplier<List<String>> newList = ArrayList::new;               // constructor
String[] names = people.stream().map(Person::name).toArray(String[]::new);
```

```java
// Grouping, partitioning, toMap
Map<String, Double> avgByDept = employees.stream()
        .collect(groupingBy(Employee::dept, TreeMap::new, averagingInt(Employee::salary)));
Map<Boolean, Long> seniorCount = employees.stream()
        .collect(partitioningBy(e -> e.age() >= 30, counting()));
Map<String, Integer> totalByCustomer = orders.stream()
        .collect(toMap(Order::customer, Order::total, Integer::sum, LinkedHashMap::new));
```

```java
// flatMap and reduce
List<String> skus = orders.stream().flatMap(o -> o.lines().stream()).map(Line::sku).distinct().toList();
BigDecimal revenue = orders.stream().flatMap(o -> o.lines().stream())
        .map(Line::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
```

```java
// Optional in a Spring service
public OrderDto getOrder(long id) {
    return orderRepository.findById(id)
            .map(OrderDto::from)
            .orElseThrow(() -> new OrderNotFoundException(id));
}
User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(new User(email)));
```

```java
// Parallel stream done safely: CPU-bound, primitive, no shared state
long primes = IntStream.rangeClosed(1, 2_000_000).parallel().filter(Primes::isPrime).count();
```

## The 10 most likely interview questions

1. **What is a functional interface?** An interface with one abstract method, the target type for lambdas and method references; examples `Runnable`, `Comparator`, `Function`.
2. **Why must captured variables be effectively final?** The lambda holds a copy of the value; forbidding changes keeps the copy correct and avoids concurrency surprises.
3. **Name the four kinds of method references.** Static, bound instance, unbound instance (receiver is the first parameter), and constructor.
4. **Intermediate vs terminal operations?** Intermediate ops are lazy and return streams; the terminal op triggers processing and produces a result or side effect.
5. **What is lazy evaluation and why does it matter?** Nothing runs until a terminal op; elements flow one at a time, enabling short-circuiting, infinite streams and no intermediate collections.
6. **`map` vs `flatMap`?** One-to-one transform vs one-to-many with flattening (lists of lists into one stream).
7. **How do you group and aggregate with streams?** `groupingBy(classifier, downstream)` with `counting`, `summingInt`, `averagingDouble`, `mapping`, `maxBy`; use a map factory for ordering.
8. **What happens with duplicate keys in `toMap`?** `IllegalStateException`; pass a merge function and optionally a map supplier.
9. **`orElse` vs `orElseGet`, and when not to use `Optional`?** `orElse` is always evaluated, `orElseGet` is lazy; do not use `Optional` for fields, parameters or collections.
10. **When should you use parallel streams?** Rarely: large, CPU-bound, splittable data with stateless operations, after measuring; never for blocking I/O in a server because they share the common ForkJoinPool.

## Rapid-fire flashcards

Q: Which annotation makes the compiler verify an interface has exactly one abstract method?
A: `@FunctionalInterface`.

Q: Which functional interface takes nothing and returns a value without checked exceptions?
A: `Supplier<T>`.

Q: What does `String::length` become as a functional interface?
A: An unbound reference such as `Function<String, Integer>` or `ToIntFunction<String>`, where the string is the parameter.

Q: What does a stream pipeline with no terminal operation do?
A: Nothing; intermediate operations are lazy.

Q: Which stream operation is a full barrier that must see every element first?
A: `sorted()`.

Q: What does `IntStream.rangeClosed(1, 5).sum()` return?
A: 15.

Q: Which collector counts elements per group?
A: `Collectors.counting()`, used as `groupingBy(key, counting())`.

Q: How do you remove empty Optionals from a stream?
A: `flatMap(Optional::stream)`.

Q: What identity should you use to sum `BigDecimal` values with `reduce`?
A: `BigDecimal.ZERO`, with `BigDecimal::add` as the accumulator.

Q: What is the default parallelism of the common ForkJoinPool?
A: Number of available processors minus one (the calling thread also helps).
