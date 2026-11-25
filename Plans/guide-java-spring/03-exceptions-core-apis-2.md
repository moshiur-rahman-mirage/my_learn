@@ 2 | MODULE | Core APIs | String, StringBuilder, BigDecimal, java.time and java.nio.file | MUST
# Core APIs

**Shared analogy: the tools on a shop counter.** `String` is a printed price tag: never edited, only replaced. `StringBuilder` is a whiteboard you write and erase while preparing a sign. `BigDecimal` is the cash register that counts every cent exactly. `java.time` is the wall clock and calendar that understands time zones. `java.nio.file` is the filing cabinet in the back room.

@@ 3 | TOPIC | String Immutability and the String Pool | Why strings never change, literal pooling, intern, == vs equals and compact strings | MUST
# String Immutability and the String Pool

**Level: Beginner | Phase 3 | Priority: MUST | Since: Java 1.0 (pool moved to heap Java 7, compact strings Java 9)**

> **30-second interview answer:** A `String` object never changes after creation; methods like `toUpperCase` return new strings. String literals are stored once in the String pool and shared, so `"hi" == "hi"` is true, while `new String("hi")` creates a separate object. Immutability makes strings safe to share between threads, safe as `HashMap` keys (their hash is cached), safe for security checks, and makes pooling possible. Always compare content with `equals`.

## 1. Concept in Simple Words
### Analogy
Printed price tags in a shop.

| Price tags | Java |
|---|---|
| A printed tag is never edited | A `String` never changes |
| To change the price you print a new tag | `s.toUpperCase()` returns a new String |
| The shop keeps one master tag for each common price and reuses it | The String pool shares literals |
| Making a photocopy of the master tag | `new String("hi")`: a separate object |
| Checking two tags show the same text | `equals` |
| Checking they are the very same piece of paper | `==` |

### What it is
- `String` is a `final` class holding characters in a private final array (`byte[]` since Java 9).
- **String pool (string constant pool):** a JVM table of unique string objects. Literals and compile-time constant expressions are pooled automatically.
- **`intern()`:** returns the pooled copy of a string, adding it if missing.
- **Compact strings (Java 9):** strings that only use Latin-1 characters store one byte per char instead of two, halving memory for most text.
- **Cached hash:** `String` computes its `hashCode` once and stores it.

### Why it exists
| Reason | Explanation |
|---|---|
| Security | File paths, URLs, class names and passwords checked once cannot be changed afterwards by another reference |
| Thread safety | Shared freely without locks |
| Hash keys | Hash code never changes, and it is cached for speed |
| Pooling | Sharing is only safe because nobody can modify a shared string |

## 2. How It Works
```text
String a = "java";             // literal: pooled
String b = "java";             // same literal: same pooled object
String c = new String("java"); // new object on the heap (its content comes from the pool literal)
String d = c.intern();         // returns the pooled object

HEAP
+------------- String pool --------------+
|  "java"  <---- a, b, d                  |
+-----------------------------------------+
   "java" (separate object)  <---- c

a == b  -> true     a == c -> false     a == d -> true     a.equals(c) -> true
```

**Compile-time constants are folded:**
```text
"ja" + "va"           -> compiler makes "java" -> pooled -> == "java" is true
String part = "ja";
part + "va"           -> computed at runtime -> new object -> == "java" is false
final String fp = "ja";
fp + "va"             -> fp is a compile-time constant -> folded -> == is true
```

**How many objects does `new String("hello")` create?** Up to two: the literal `"hello"` in the pool (if not already there, created when the class loads) and one new heap object.

**Concatenation in loops** creates a new String each time, copying all previous characters: O(n²) work for n appends. Use `StringBuilder` (next topic). Since Java 9, a single `a + b + c` expression is compiled to an efficient `invokedynamic` call, so simple concatenations are fine.

## 3. Code Example
### Code
```java
public class StringPoolDemo {
    public static void main(String[] args) {
        // 1. Pool and identity
        String a = "java";
        String b = "java";
        String c = new String("java");
        String d = c.intern();
        System.out.println("a == b      : " + (a == b));
        System.out.println("a == c      : " + (a == c));
        System.out.println("a.equals(c) : " + a.equals(c));
        System.out.println("a == d      : " + (a == d));

        // 2. Compile-time folding vs runtime concatenation
        String part = "ja";
        final String constPart = "ja";
        System.out.println("\"ja\"+\"va\" == a : " + (("ja" + "va") == a));
        System.out.println("part+\"va\" == a  : " + ((part + "va") == a));
        System.out.println("const+\"va\" == a : " + ((constPart + "va") == a));

        // 3. Immutability: methods return new strings
        String original = "  Hello World  ";
        String trimmed = original.trim();
        String upper = trimmed.toUpperCase();
        System.out.println("original    : [" + original + "]");
        System.out.println("upper       : [" + upper + "]");
        original.concat("!!!");                       // result ignored: nothing changes
        System.out.println("after concat: [" + original + "]");

        // 4. Common String API
        String csv = "pen,book,,lamp";
        System.out.println("split       : " + String.join(" | ", csv.split(",")).replace(" | ", "/"));
        System.out.println("parts       : " + csv.split(",").length);
        System.out.println("contains    : " + csv.contains("book"));
        System.out.println("indexOf     : " + csv.indexOf("book"));
        System.out.println("substring   : " + csv.substring(4, 8));
        System.out.println("charAt      : " + csv.charAt(0));
        System.out.println("replace     : " + "a-b-c".replace("-", "+"));
        System.out.println("isBlank     : " + "   ".isBlank() + ", isEmpty: " + "   ".isEmpty());
        System.out.println("strip       : [" + "  hi  ".strip() + "]");
        System.out.println("repeat      : " + "ab".repeat(3));
        System.out.println("lines       : " + "a\nb\nc".lines().count());
        System.out.println("format      : " + String.format("%-5s|%5.2f|%03d", "id", 3.14159, 7));
        System.out.println("formatted   : " + "%s has %d items".formatted("cart", 3));
        System.out.println("equalsIgnoreCase: " + "JAVA".equalsIgnoreCase(a));
        System.out.println("compareTo   : " + "apple".compareTo("banana"));

        // 5. Safe comparison with possible null
        String input = null;
        System.out.println("literal first: " + "yes".equals(input));   // no NPE
        System.out.println("Objects.equals: " + java.util.Objects.equals(input, "yes"));

        // 6. hashCode is cached and depends only on content
        System.out.println("same hash   : " + (a.hashCode() == c.hashCode()));
    }
}
```

### Walkthrough
- Part 1: two literals share one pooled object; `new String` creates a separate object; `intern()` returns the pooled one.
- Part 2: `"ja" + "va"` is folded by the compiler into the literal `"java"`. `part + "va"` happens at runtime and creates a new object. A `final` local initialized with a literal is a compile-time constant, so it is folded too.
- Part 3: `trim`, `toUpperCase` and `concat` all return new strings. Ignoring the return value of `concat` changes nothing, a common beginner bug.
- Part 4: the most used methods. `split(",")` on `"pen,book,,lamp"` gives 4 parts including an empty string. `strip` (Java 11) removes Unicode whitespace that `trim` does not. `isBlank`, `repeat`, `lines` are Java 11; `formatted` is Java 15.
- Part 5: `"yes".equals(input)` is null-safe because the literal is never null.

### Output
```text
a == b      : true
a == c      : false
a.equals(c) : true
a == d      : true
"ja"+"va" == a : true
part+"va" == a  : false
const+"va" == a : true
original    : [  Hello World  ]
upper       : [HELLO WORLD]
after concat: [  Hello World  ]
split       : pen/book//lamp
parts       : 4
contains    : true
indexOf     : 4
substring   : book
charAt      : p
replace     : a+b+c
isBlank     : true, isEmpty: false
strip       : [hi]
repeat      : ababab
lines       : 3
format      : id   | 3.14|007
formatted   : cart has 3 items
equalsIgnoreCase: true
compareTo   : -1
literal first: false
Objects.equals: false
same hash   : true
```

### Compare
| `==` | `equals` |
|---|---|
| Same object? | Same characters? |
| True for two identical literals (pooled) | True for any two strings with equal content |
| Unreliable for strings from input, concatenation or `new` | Always correct |

## 4. Interview Questions
**Basic**
1. **Why is String immutable?**
   For security, thread safety, safe use as hash keys with a cached hash code, and to make the String pool possible.
2. **What is the String pool?**
   A JVM-managed table of unique string instances. Literals are interned there so identical literals share one object. Since Java 7 it lives on the heap.
3. **Difference between `==` and `equals` for strings?**
   `==` compares references; `equals` compares characters. Always use `equals` for content.

**Follow-up**
4. **How many objects does `String s = new String("abc");` create?**
   Up to two: the pooled literal `"abc"` (if not already present) and a new heap object referenced by `s`.
5. **What does `intern()` do?**
   Returns the pooled instance with the same content, adding this string to the pool if needed.
6. **Why is concatenation in a loop slow?**
   Each `+=` creates a new String and copies all previous characters, giving quadratic time. Use `StringBuilder`.

**Tricky / scenario**
7. **Why are passwords often stored in `char[]` instead of `String`?**
   A String cannot be wiped and may linger in memory (and the pool) until garbage collected; a `char[]` can be overwritten with zeros right after use. `JPasswordField.getPassword()` returns `char[]` for this reason.
8. **What does `"a" + 1 + 2` give, and `1 + 2 + "a"`?**
   `"a12"` and `"3a"`: evaluation is left to right, and once a String appears, `+` means concatenation.

**What the interviewer is really testing:** whether you know why `==` sometimes "works" for strings and why you still must not rely on it.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| Comparing strings with `==` | Works for literals, fails for input | `equals` |
| Ignoring the return value: `s.trim();` | String unchanged | `s = s.trim();` |
| `input.equals("yes")` with null input | `NullPointerException` | `"yes".equals(input)` |
| `+=` in big loops | Slow, lots of garbage | `StringBuilder` |
| `split(".")` | Regex: `.` matches everything, returns an empty array | `split("\\.")` or `Pattern.quote(".")` |

### Best practices
- Prefer `isBlank` and `strip` (Unicode-aware) over `trim().isEmpty()`.
- Use `String.join`, `String.format` or text blocks (Phase 6) for readable building.
- Use `equalsIgnoreCase` or `toLowerCase(Locale.ROOT)` for case-insensitive checks; avoid locale surprises.

### Compare with
| Type | Mutable? | Thread-safe? |
|---|---|---|
| `String` | No | Yes (immutable) |
| `StringBuilder` | Yes | No |
| `StringBuffer` | Yes | Yes (synchronized) |

## 6. Hands-on Exercise
Write `isPalindrome(String s)` that ignores case, spaces and punctuation (`"A man, a plan, a canal: Panama"` is true). Then write `countWords(String text)` that counts words separated by any whitespace, ignoring leading and trailing spaces.

<details><summary>Solution</summary>

```java
public class StringExercises {
    static boolean isPalindrome(String s) {
        String clean = s.toLowerCase().replaceAll("[^a-z0-9]", "");  // keep letters and digits
        int i = 0, j = clean.length() - 1;
        while (i < j) {
            if (clean.charAt(i++) != clean.charAt(j--)) return false;
        }
        return true;
    }

    static int countWords(String text) {
        String t = text.strip();
        return t.isEmpty() ? 0 : t.split("\\s+").length;   // \\s+ = one or more whitespace
    }

    public static void main(String[] args) {
        System.out.println(isPalindrome("A man, a plan, a canal: Panama")); // true
        System.out.println(isPalindrome("race a car"));                     // false
        System.out.println(countWords("  Java   is\tfun \n"));              // 3
        System.out.println(countWords("   "));                              // 0
    }
}
```
</details>

## 7. Recall
Q: What does `"java" == new String("java")` evaluate to?
A: false, because `new String` creates a separate object.

Q: Where has the String pool lived since Java 7?
A: On the heap.

Q: What does `String.intern()` return?
A: The pooled instance with the same content.

Q: Why is `"yes".equals(input)` safer than `input.equals("yes")`?
A: The literal is never null, so it cannot throw `NullPointerException`.

Q: Name three reasons `String` is immutable.
A: Security, thread safety, and safe hashing/pooling (cached hash code).

**Related:** [[StringBuilder and StringBuffer]] · [[Immutable Class Design]] · [[var, Switch Expressions and Text Blocks]]

@@ 3 | TOPIC | StringBuilder and StringBuffer | Mutable strings, capacity growth, chaining, synchronized StringBuffer and when to use each | MUST
# StringBuilder and StringBuffer

**Level: Beginner | Phase 3 | Priority: MUST | Since: StringBuffer Java 1.0, StringBuilder Java 5**

> **30-second interview answer:** `StringBuilder` is a mutable sequence of characters for building strings efficiently: `append`, `insert`, `delete`, `reverse` change it in place, and `toString()` produces the final `String`. `StringBuffer` has the same API but every method is synchronized, so it is thread-safe and slower. Use `StringBuilder` by default, especially in loops; `StringBuffer` is legacy, since sharing one builder across threads is rarely a good design.

## 1. Concept in Simple Words
### Analogy
A whiteboard versus printed posters.

| Whiteboard | Java |
|---|---|
| Printing a new poster for every word you add | `String +=` in a loop |
| Writing on a whiteboard, erasing and adding freely | `StringBuilder` |
| A whiteboard with a lock so only one person writes at a time | `StringBuffer` (synchronized) |
| Taking a photo of the finished whiteboard | `toString()` |
| Buying a bigger whiteboard when space runs out | Capacity growth |

### What it is
- Internally holds a resizable array plus a `count` of used characters.
- Default capacity is 16; `new StringBuilder(n)` presets capacity.
- Methods return `this`, so calls chain: `sb.append("a").append(1).append('c')`.
- Key methods: `append`, `insert(index, x)`, `delete(start, end)`, `deleteCharAt`, `replace`, `reverse`, `setCharAt`, `setLength`, `length`, `charAt`, `indexOf`, `toString`.
- `StringBuilder` does not override `equals`: compare with `sb1.toString().equals(sb2.toString())` or `sb1.compareTo(sb2) == 0` (Java 11).

### Why it exists
Strings are immutable, so building text piece by piece with `+` creates many temporary objects and copies. A builder appends into one buffer.

## 2. How It Works
```text
StringBuilder sb = new StringBuilder();     capacity 16, length 0
sb.append("Hello");                         [H e l l o _ _ _ _ _ _ _ _ _ _ _]  length 5
sb.append(", World!!!!");                   length 16, capacity 16: full
sb.append("?");                             needs 17 > 16 -> grow:
                                            new capacity = old * 2 + 2 = 34
                                            copy old chars into the new array
```
Growth doubles capacity, so n appends cost O(n) in total (amortized O(1) each).

**Loop cost comparison** for building a string of n pieces:
```text
String +=      : copy 1 + 2 + 3 + ... + n chars  -> O(n^2)
StringBuilder  : amortized O(1) per append        -> O(n)
```

**StringBuffer** wraps each method in `synchronized`. The JIT can sometimes remove uncontended locks (lock elision), but the semantics are still "one thread at a time per call", and a sequence of calls is still not atomic as a whole.

## 3. Code Example
### Code
```java
public class BuilderDemo {
    public static void main(String[] args) {
        // 1. Chaining and editing in place
        StringBuilder sb = new StringBuilder("Order");
        sb.append(" #").append(42).append(':').append(' ').append(19.99);
        System.out.println("built      : " + sb);
        sb.insert(0, "[NEW] ");
        System.out.println("insert     : " + sb);
        sb.replace(0, 6, "");
        System.out.println("replace    : " + sb);
        sb.delete(sb.indexOf(":"), sb.length());
        System.out.println("delete     : " + sb);
        System.out.println("reverse    : " + new StringBuilder("stressed").reverse());

        // 2. Capacity growth
        StringBuilder cap = new StringBuilder();
        System.out.println("capacity   : " + cap.capacity());
        cap.append("1234567890123456");      // 16 chars: still fits
        System.out.println("capacity   : " + cap.capacity());
        cap.append("7");                     // 17th char: grows
        System.out.println("capacity   : " + cap.capacity());

        // 3. Performance: String += vs StringBuilder
        int n = 20_000;
        long t1 = System.nanoTime();
        String s = "";
        for (int i = 0; i < n; i++) s += "x";
        long stringTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        StringBuilder fast = new StringBuilder(n);
        for (int i = 0; i < n; i++) fast.append("x");
        String result = fast.toString();
        long builderTime = System.nanoTime() - t2;

        System.out.println("same length: " + (s.length() == result.length()));
        System.out.println("builder faster: " + (builderTime < stringTime));

        // 4. Joining with a separator, avoiding a trailing comma
        String[] items = {"pen", "book", "lamp"};
        StringBuilder csv = new StringBuilder();
        for (String item : items) {
            if (csv.length() > 0) csv.append(", ");
            csv.append(item);
        }
        System.out.println("joined     : " + csv);
        System.out.println("String.join: " + String.join(", ", items));

        // 5. equals is NOT overridden
        StringBuilder x = new StringBuilder("hi"), y = new StringBuilder("hi");
        System.out.println("sb.equals  : " + x.equals(y));
        System.out.println("compareTo  : " + (x.compareTo(y) == 0));
        System.out.println("toString eq: " + x.toString().equals(y.toString()));

        // 6. StringBuffer has the same API, synchronized
        StringBuffer buffer = new StringBuffer();
        buffer.append("thread").append("-safe");
        System.out.println("buffer     : " + buffer);
    }
}
```

### Walkthrough
- Part 1: every method edits the same object and returns it, allowing chains. `append` accepts any type; numbers and chars are converted to text.
- Part 2: the default capacity is 16. The 17th character triggers growth to 16 × 2 + 2 = 34.
- Part 3: `s += "x"` copies the growing string every time; with 20,000 iterations the builder is far faster.
- Part 4: the classic "no trailing comma" loop, and the simpler `String.join`.
- Part 5: `StringBuilder.equals` is identity-based. Compare contents with `compareTo` (Java 11) or `toString`.
- Part 6: `StringBuffer` is used exactly the same way.

### Output
```text
built      : Order #42: 19.99
insert     : [NEW] Order #42: 19.99
replace    : Order #42: 19.99
delete     : Order #42
reverse    : desserts
capacity   : 16
capacity   : 16
capacity   : 34
same length: true
builder faster: true
joined     : pen, book, lamp
String.join: pen, book, lamp
sb.equals  : false
compareTo  : true
toString eq: true
buffer     : thread-safe
```

### Compare
| | String | StringBuilder | StringBuffer |
|---|---|---|---|
| Mutable | No | Yes | Yes |
| Thread-safe | Yes (immutable) | No | Yes (synchronized) |
| Speed for building | Slow in loops | Fastest | Slower than builder |
| `equals` by content | Yes | No | No |
| Use when | Values, keys, constants | Building text, single thread | Legacy code |

## 4. Interview Questions
**Basic**
1. **String vs StringBuilder vs StringBuffer?**
   String is immutable; StringBuilder is mutable and not synchronized; StringBuffer is mutable and synchronized. Use StringBuilder for building text.
2. **Why is StringBuilder faster than String concatenation in loops?**
   It appends into one growing buffer instead of creating and copying a new String each iteration.
3. **What is the default capacity of a StringBuilder?**
   16 characters; it grows to old × 2 + 2 when full.

**Follow-up**
4. **Does the compiler optimize `+`?**
   Yes, a single expression like `a + b + c` is compiled efficiently (StringBuilder before Java 9, `invokedynamic` `StringConcatFactory` since). It cannot optimize across loop iterations.
5. **Why is StringBuffer rarely used today?**
   Sharing a mutable builder between threads is unusual; when needed, higher-level synchronization is better. Unneeded locking only costs performance.
6. **How do you compare two StringBuilders?**
   `sb1.compareTo(sb2) == 0` (Java 11) or compare their `toString()` values; `equals` compares references.

**Tricky / scenario**
7. **Is a sequence of StringBuffer calls atomic?**
   No. Each method is synchronized, but between `append("a")` and `append("b")` another thread can append. Compound operations need external locking.
8. **You log `"User " + user + " did " + action` at debug level millions of times while debug is off. Problem?**
   The string is built even if never logged. Use parameterized logging: `log.debug("User {} did {}", user, action)`.

**What the interviewer is really testing:** performance awareness and correct choice of tool.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| `String +=` in loops | Quadratic time | StringBuilder |
| Using `sb.equals(other)` | Always false for different objects | `compareTo` or `toString().equals` |
| Sharing a StringBuilder across threads | Corrupted text | Use local builders per thread |
| Building log messages eagerly | Wasted CPU | Parameterized logging |

### Best practices
- Create the builder with an estimated capacity for large outputs.
- Prefer `String.join`, `Collectors.joining` or `StringJoiner` for separated lists.
- Keep builders local to a method: then thread safety is not an issue.

### Compare with
| Tool | Best for |
|---|---|
| `StringBuilder` | General text building |
| `StringJoiner` / `String.join` | Lists with separators, prefix and suffix |
| `Collectors.joining` | Building from streams |
| Text blocks and `formatted` | Fixed templates |

## 6. Hands-on Exercise
Write `compress(String s)` that turns `"aaabccdddd"` into `"a3b1c2d4"` using a `StringBuilder`, and returns the original if the compressed string is not shorter. Test with `"aaabccdddd"`, `"abc"` and `""`.

<details><summary>Solution</summary>

```java
public class Compress {
    static String compress(String s) {
        if (s.isEmpty()) return s;
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (int i = 1; i <= s.length(); i++) {
            if (i < s.length() && s.charAt(i) == s.charAt(i - 1)) {
                count++;
            } else {
                sb.append(s.charAt(i - 1)).append(count);  // close the current run
                count = 1;
            }
        }
        return sb.length() < s.length() ? sb.toString() : s;
    }

    public static void main(String[] args) {
        System.out.println(compress("aaabccdddd")); // a3b1c2d4
        System.out.println(compress("abc"));        // abc (a1b1c1 is longer)
        System.out.println("[" + compress("") + "]"); // []
    }
}
```
</details>

## 7. Recall
Q: What is the difference between StringBuilder and StringBuffer?
A: StringBuffer's methods are synchronized (thread-safe, slower); StringBuilder's are not.

Q: What is the default capacity of a StringBuilder and how does it grow?
A: 16; it grows to old capacity × 2 + 2.

Q: Does `StringBuilder.equals` compare contents?
A: No, it compares references.

Q: Why is `+=` on a String inside a loop O(n²)?
A: Each iteration creates a new String and copies all previous characters.

Q: Which should you use by default for building text in one thread?
A: `StringBuilder`.

**Related:** [[String Immutability and the String Pool]] · [[Logging with SLF4J, Logback and MDC]] · [[Collectors: groupingBy, partitioningBy, toMap]]

@@ 3 | TOPIC | BigDecimal and Money | Exact decimal math, scale, rounding modes, comparing and money patterns | SHOULD
# BigDecimal and Money

**Level: Beginner | Phase 3 | Priority: SHOULD | Since: Java 1.1 (BigDecimal), RoundingMode enum Java 5**

> **30-second interview answer:** `double` stores binary fractions, so values like 0.1 cannot be represented exactly and money calculations drift. `BigDecimal` stores an exact decimal: an unscaled integer plus a scale (digits after the point). Create it from a String or `BigDecimal.valueOf(double)`, never `new BigDecimal(0.1)`. It is immutable, so `add` returns a new object. Division needs a scale and `RoundingMode`, and comparisons should use `compareTo`, because `equals` also compares scale (2.0 is not equal to 2.00).

## 1. Concept in Simple Words
### Analogy
Counting coins vs estimating a pile.

| Money | Java |
|---|---|
| Estimating a pile of coins by its weight | `double`: fast, approximately right |
| Counting every coin exactly | `BigDecimal`: exact |
| Agreeing to always show 2 decimal places | Scale 2 |
| The rule for half cents (round half up, banker's rounding) | `RoundingMode` |
| "1.0 dollars" and "1.00 dollars" are worth the same but written differently | `compareTo` equal, `equals` not equal |

### What it is
- `BigDecimal` = **unscaled value** (a `BigInteger`) × 10^(-scale). `12.345` is unscaled 12345 with scale 3.
- Immutable: every operation returns a new instance.
- Create: `new BigDecimal("19.99")`, `BigDecimal.valueOf(19.99)` (uses the double's shortest string form), `BigDecimal.valueOf(1999, 2)` (unscaled and scale).
- Operations: `add`, `subtract`, `multiply`, `divide(divisor, scale, RoundingMode)`, `setScale(scale, RoundingMode)`, `negate`, `abs`, `max`, `min`.
- Common rounding modes: `HALF_UP` (school rounding), `HALF_EVEN` (banker's rounding, reduces bias), `DOWN` (truncate), `CEILING`, `FLOOR`.

### Why it exists
Financial calculations must be exact and auditable. Rounding errors of a fraction of a cent, repeated over millions of transactions, become real money and legal problems.

## 2. How It Works
```text
0.1 in binary = 0.0001100110011001100110011... (repeats forever)
double keeps 52 bits -> stores 0.1000000000000000055511151231257827...

new BigDecimal(0.1)        -> 0.1000000000000000055511151231257827021181583404541015625
BigDecimal.valueOf(0.1)    -> 0.1        (uses Double.toString -> "0.1")
new BigDecimal("0.1")      -> 0.1        (exact)
```

**Scale rules:**
```text
add/subtract: scale = max(scale1, scale2)        1.5 + 2.25 = 3.75 (scale 2)
multiply:     scale = scale1 + scale2            1.5 x 2.25 = 3.375 (scale 3)
divide:       exact result or ArithmeticException if non-terminating (1/3)
              -> always pass a scale and RoundingMode
```

**Rounding 2.345 and 2.355 to 2 places:**
| Mode | 2.345 | 2.355 | -2.345 |
|---|---|---|---|
| HALF_UP | 2.35 | 2.36 | -2.35 |
| HALF_EVEN | 2.34 | 2.36 | -2.34 |
| DOWN | 2.34 | 2.35 | -2.34 |
| FLOOR | 2.34 | 2.35 | -2.35 |

## 3. Code Example
### Code
```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MoneyDemo {
    public static void main(String[] args) {
        // 1. double drifts
        double total = 0;
        for (int i = 0; i < 10; i++) total += 0.1;
        System.out.println("double 10 x 0.1  : " + total);

        BigDecimal exact = BigDecimal.ZERO;
        for (int i = 0; i < 10; i++) exact = exact.add(new BigDecimal("0.1")); // reassign!
        System.out.println("BigDecimal       : " + exact);

        // 2. Constructor trap
        System.out.println("new BigDecimal(0.1): " + new BigDecimal(0.1).toString().substring(0, 22) + "...");
        System.out.println("valueOf(0.1)       : " + BigDecimal.valueOf(0.1));

        // 3. Immutability: forgetting to reassign
        BigDecimal price = new BigDecimal("10.00");
        price.add(new BigDecimal("5.00"));       // result discarded
        System.out.println("price unchanged  : " + price);

        // 4. Division needs scale and rounding
        BigDecimal hundred = new BigDecimal("100");
        try {
            hundred.divide(new BigDecimal("3"));
        } catch (ArithmeticException e) {
            System.out.println("divide 100/3     : " + e.getMessage());
        }
        System.out.println("100/3 (2, HALF_UP): " + hundred.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP));

        // 5. Rounding modes
        BigDecimal v = new BigDecimal("2.345");
        System.out.println("HALF_UP   : " + v.setScale(2, RoundingMode.HALF_UP));
        System.out.println("HALF_EVEN : " + v.setScale(2, RoundingMode.HALF_EVEN));
        System.out.println("DOWN      : " + v.setScale(2, RoundingMode.DOWN));

        // 6. equals vs compareTo
        BigDecimal a = new BigDecimal("2.0"), b = new BigDecimal("2.00");
        System.out.println("equals    : " + a.equals(b));
        System.out.println("compareTo : " + (a.compareTo(b) == 0));
        System.out.println("stripped  : " + b.stripTrailingZeros().toPlainString());

        // 7. A realistic invoice: line totals, tax, final rounding
        record Line(String item, BigDecimal unitPrice, int qty) {
            BigDecimal total() { return unitPrice.multiply(BigDecimal.valueOf(qty)); }
        }
        List<Line> lines = List.of(
                new Line("Book", new BigDecimal("12.99"), 3),
                new Line("Pen", new BigDecimal("0.35"), 7));
        BigDecimal subtotal = lines.stream().map(Line::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.075")).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal grand = subtotal.add(tax);
        System.out.println("subtotal  : " + subtotal);
        System.out.println("tax 7.5%  : " + tax);
        System.out.println("total     : " + grand);

        // 8. Splitting a bill without losing a cent
        BigDecimal bill = new BigDecimal("100.00");
        int people = 3;
        BigDecimal share = bill.divide(BigDecimal.valueOf(people), 2, RoundingMode.DOWN);
        BigDecimal remainder = bill.subtract(share.multiply(BigDecimal.valueOf(people)));
        System.out.println("share     : " + share + ", first person pays extra " + remainder);
    }
}
```

### Walkthrough
- Part 1: adding 0.1 ten times as `double` gives 0.9999999999999999; with `BigDecimal` it is exactly 1.0. Note `exact = exact.add(...)`: the result must be reassigned.
- Part 2: `new BigDecimal(0.1)` faithfully copies the inexact binary value; `valueOf` gives 0.1.
- Part 3: calling `add` without assigning changes nothing, because BigDecimal is immutable.
- Part 4: 100 / 3 has no exact decimal result, so `divide` without rounding throws. With scale 2 and `HALF_UP` it gives 33.33.
- Part 5: 2.345 rounds to 2.35 with HALF_UP, 2.34 with HALF_EVEN (the 4 is even), 2.34 with DOWN.
- Part 6: `equals` compares value and scale; `compareTo` compares only value.
- Part 7: 12.99 × 3 = 38.97, 0.35 × 7 = 2.45, subtotal 41.42, tax 41.42 × 0.075 = 3.1065 → 3.11, total 44.53.
- Part 8: each person pays 33.33 and the leftover 0.01 is assigned explicitly, so no cent disappears.

### Output
```text
double 10 x 0.1  : 0.9999999999999999
BigDecimal       : 1.0
new BigDecimal(0.1): 0.10000000000000000555...
valueOf(0.1)       : 0.1
price unchanged  : 10.00
divide 100/3     : Non-terminating decimal expansion; no exact representable decimal result.
100/3 (2, HALF_UP): 33.33
HALF_UP   : 2.35
HALF_EVEN : 2.34
DOWN      : 2.34
equals    : false
compareTo : true
stripped  : 2
subtotal  : 41.42
tax 7.5%  : 3.11
total     : 44.53
share     : 33.33, first person pays extra 0.01
```

### Compare
| `double` | `BigDecimal` | `long` cents |
|---|---|---|
| Fast, inexact | Exact, slower, more memory | Exact and fast |
| Fine for science and graphics | Standard for money with rates and rounding | Good for simple amounts in one currency |
| `==` and `<` operators | Methods only | Operators work |

## 4. Interview Questions
**Basic**
1. **Why should you not use `double` for money?**
   Doubles are binary fractions; most decimal amounts cannot be represented exactly, so sums and products accumulate rounding errors.
2. **How do you create a BigDecimal correctly?**
   From a String (`new BigDecimal("19.99")`) or `BigDecimal.valueOf(19.99)`, never `new BigDecimal(19.99)`.
3. **Is BigDecimal mutable?**
   No. Operations return new instances, so you must assign the result.

**Follow-up**
4. **Why can `divide` throw an exception?**
   If the exact result has infinitely many digits (1/3), BigDecimal cannot represent it; you must provide a scale and rounding mode.
5. **Why does `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` return false?**
   `equals` compares both value and scale. Use `compareTo(...) == 0` to compare numerically. This also affects `HashSet` and `HashMap` keys.
6. **What is HALF_EVEN rounding and why do banks use it?**
   Ties round to the nearest even digit (2.345 → 2.34, 2.355 → 2.36). Over many operations, rounding up and down balance out, reducing bias.

**Tricky / scenario**
7. **How do you store money in a database with JPA?**
   Use a `DECIMAL(19, 4)` or `NUMERIC` column mapped to `BigDecimal` with `@Column(precision = 19, scale = 4)`, and store the currency separately (or use a Money value object).
8. **You split 100.00 among 3 people with rounding. How do you avoid losing a cent?**
   Compute each share with rounding down, then assign the remainder (0.01) to one participant explicitly, so the parts always sum to the total.

**What the interviewer is really testing:** whether you have built real financial code without rounding bugs.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| `new BigDecimal(0.1)` | Inexact value | `new BigDecimal("0.1")` or `valueOf` |
| Not assigning results | Value never changes | `total = total.add(x)` |
| `divide` without rounding | `ArithmeticException` | Pass scale and `RoundingMode` |
| `equals` for comparison | 2.0 vs 2.00 not equal | `compareTo` |
| Rounding at every step | Accumulated rounding differences | Round once at defined points (line total or final total) |

### Best practices
- Wrap amounts in a `Money` value object (amount plus currency) so you never add USD to EUR.
- Agree on scale and rounding rules with the business, and write them in one place.
- Use `toPlainString()` to avoid scientific notation like `1E+3` in output.

### Compare with
| Need | Use |
|---|---|
| Money with tax and interest | `BigDecimal` |
| Simple counters of cents | `long` |
| Currency-aware library | JSR 354 Money API (Moneta) |

## 6. Hands-on Exercise
Write `static BigDecimal compound(BigDecimal principal, BigDecimal annualRate, int years)` that applies yearly compound interest, rounding to 2 decimals with HALF_EVEN after each year. Print the result for 1000.00 at 5% for 3 years.

<details><summary>Solution</summary>

```java
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Compound {
    static BigDecimal compound(BigDecimal principal, BigDecimal annualRate, int years) {
        BigDecimal amount = principal;
        BigDecimal factor = BigDecimal.ONE.add(annualRate);          // 1.05
        for (int y = 1; y <= years; y++) {
            amount = amount.multiply(factor).setScale(2, RoundingMode.HALF_EVEN);
            System.out.println("year " + y + ": " + amount);
        }
        return amount;
    }

    public static void main(String[] args) {
        compound(new BigDecimal("1000.00"), new BigDecimal("0.05"), 3);
    }
}
```
Output:
```text
year 1: 1050.00
year 2: 1102.50
year 3: 1157.62
```
Year 3: 1102.50 × 1.05 = 1157.625; HALF_EVEN rounds the tie to the even digit, 1157.62 (HALF_UP would give 1157.63).
</details>

## 7. Recall
Q: Why is `new BigDecimal(0.1)` a bug?
A: It copies the inexact binary double value; use `new BigDecimal("0.1")` or `BigDecimal.valueOf(0.1)`.

Q: What must you pass to `BigDecimal.divide` to avoid `ArithmeticException`?
A: A scale and a `RoundingMode`.

Q: How do you compare two BigDecimals numerically?
A: `a.compareTo(b) == 0`, because `equals` also compares scale.

Q: What is HALF_EVEN rounding?
A: Ties round to the nearest even digit (banker's rounding).

Q: Is BigDecimal immutable?
A: Yes; operations return new objects.

**Related:** [[Primitive Types and Variables]] · [[Immutable Class Design]] · [[Entity Mapping]]
