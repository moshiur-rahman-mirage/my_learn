@@ 3 | TOPIC | Date and Time API (java.time) | LocalDate, Instant, ZonedDateTime, Duration vs Period, formatting and time zones | SHOULD
# Date and Time API (java.time)

**Level: Beginner | Phase 3 | Priority: SHOULD | Since: Java 8 (JSR 310)**

> **30-second interview answer:** `java.time` replaced the old mutable, confusing `Date` and `Calendar`. Its classes are immutable and thread-safe. `LocalDate`, `LocalTime` and `LocalDateTime` have no time zone (a birthday, an opening hour). `Instant` is a point on the UTC timeline (timestamps, logs, databases). `ZonedDateTime` is a date-time in a specific zone with daylight-saving rules. `Duration` measures time in seconds and nanos, `Period` in years, months and days. Store `Instant` (or UTC) and convert to the user's zone only for display.

## 1. Concept in Simple Words
### Analogy
Calendars, clocks and world clocks.

| Real world | java.time |
|---|---|
| A date circled on a paper calendar ("my birthday") | `LocalDate` |
| "The shop opens at 9:00" (whatever city) | `LocalTime` |
| The single moment a rocket launches, the same everywhere | `Instant` |
| The airport board showing the time in Tokyo | `ZonedDateTime` |
| "The flight takes 7 hours 30 minutes" | `Duration` |
| "The contract lasts 2 years and 3 months" | `Period` |

### What it is
| Class | Holds | Example |
|---|---|---|
| `LocalDate` | Date | 2026-11-24 |
| `LocalTime` | Time | 09:30 |
| `LocalDateTime` | Date and time, no zone | 2026-11-24T09:30 |
| `Instant` | Moment on the UTC timeline | 2026-11-24T03:30:00Z |
| `ZonedDateTime` | Date, time and zone rules | 2026-11-24T09:30+06:00[Asia/Dhaka] |
| `OffsetDateTime` | Date, time and fixed offset | 2026-11-24T09:30+06:00 |
| `Duration` | Seconds and nanos | PT7H30M |
| `Period` | Years, months, days | P2Y3M |
| `DateTimeFormatter` | Formatting and parsing, thread-safe | `ofPattern("dd/MM/yyyy")` |
| `Clock` | Source of "now", replaceable in tests | `Clock.fixed(...)` |

All are immutable: `date.plusDays(1)` returns a new object.

### Why it exists
`java.util.Date` was mutable (unsafe to share), had months starting at 0, years offset from 1900, and `SimpleDateFormat` was not thread-safe, causing famous production bugs. `java.time` fixed all of this.

## 2. How It Works
```text
Instant (UTC timeline)  2026-03-29T00:30:00Z
        |
        | atZone(ZoneId.of("Europe/London"))
        v
ZonedDateTime           2026-03-29T00:30Z[Europe/London]   (GMT, offset +00:00)
        | plusHours(1)
        v
                        2026-03-29T02:30+01:00[Europe/London]
                        (clocks jumped forward at 01:00: 01:30 does not exist that day)
```

**Duration vs Period across a daylight-saving change:**
```text
zdt.plus(Duration.ofDays(1))  -> exactly 24 hours later (wall time may shift by an hour)
zdt.plus(Period.ofDays(1))    -> same wall-clock time on the next day (may be 23 or 25 hours)
```

**Where each type belongs in a Spring Boot app**
| Need | Type | Database column |
|---|---|---|
| When an order was created | `Instant` (or `OffsetDateTime`) | `TIMESTAMP WITH TIME ZONE` |
| Date of birth, delivery date | `LocalDate` | `DATE` |
| Shop opening time | `LocalTime` | `TIME` |
| Meeting in a city | `ZonedDateTime` (store Instant plus ZoneId) | timestamp plus zone column |

Jackson (with the `jsr310` module that Spring Boot registers automatically) serializes these as ISO-8601 strings like `"2026-11-24T03:30:00Z"`.

## 3. Code Example
### Code
```java
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class TimeDemo {
    public static void main(String[] args) {
        // A fixed clock makes "now" predictable (also how you test time-based code)
        Clock clock = Clock.fixed(Instant.parse("2026-11-24T03:30:00Z"), ZoneId.of("UTC"));

        // 1. Local types
        LocalDate today = LocalDate.now(clock);
        LocalDate birthday = LocalDate.of(1995, 6, 15);       // months are 1-based
        System.out.println("today       : " + today + " (" + today.getDayOfWeek() + ")");
        System.out.println("age         : " + Period.between(birthday, today).getYears());
        System.out.println("plus 1 month: " + LocalDate.of(2026, 1, 31).plusMonths(1));
        System.out.println("leap year?  : " + today.isLeapYear());
        System.out.println("next Monday : " + today.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
        System.out.println("month end   : " + today.with(TemporalAdjusters.lastDayOfMonth()));

        // 2. Immutability
        LocalDate d = LocalDate.of(2026, 1, 1);
        d.plusDays(10);                                        // result discarded
        System.out.println("unchanged   : " + d);

        // 3. Instant and zones
        Instant now = Instant.now(clock);
        ZonedDateTime dhaka = now.atZone(ZoneId.of("Asia/Dhaka"));
        ZonedDateTime ny = now.atZone(ZoneId.of("America/New_York"));
        System.out.println("instant     : " + now);
        System.out.println("Dhaka       : " + dhaka);
        System.out.println("New York    : " + ny);
        System.out.println("same moment : " + dhaka.toInstant().equals(ny.toInstant()));

        // 4. Daylight saving: Duration vs Period
        ZonedDateTime beforeDst = ZonedDateTime.of(2026, 3, 28, 12, 0, 0, 0, ZoneId.of("Europe/London"));
        System.out.println("+Duration 1d: " + beforeDst.plus(Duration.ofDays(1)));
        System.out.println("+Period 1d  : " + beforeDst.plus(Period.ofDays(1)));

        // 5. Duration and between
        LocalTime open = LocalTime.of(9, 0), close = LocalTime.of(17, 30);
        Duration shift = Duration.between(open, close);
        System.out.println("shift       : " + shift + " = " + shift.toMinutes() + " minutes");
        System.out.println("days until  : " + ChronoUnit.DAYS.between(today, LocalDate.of(2026, 12, 25)));

        // 6. Formatting and parsing (DateTimeFormatter is thread-safe)
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime meeting = LocalDateTime.of(2026, 12, 1, 14, 5);
        String text = meeting.format(fmt);
        System.out.println("formatted   : " + text);
        System.out.println("parsed back : " + LocalDateTime.parse(text, fmt));
        System.out.println("ISO         : " + meeting);
        try {
            LocalDate.parse("2026-02-30");
        } catch (DateTimeParseException e) {
            System.out.println("invalid date rejected: 2026-02-30");
        }

        // 7. Comparison
        System.out.println("isBefore    : " + birthday.isBefore(today));
    }
}
```

### Walkthrough
- A fixed `Clock` makes `now` reproducible. In Spring you can inject a `Clock` bean so tests control time.
- `LocalDate.of(1995, 6, 15)` uses real month numbers. `Period.between` gives the age in years. Adding one month to 31 January gives 28 February (clamped to the last valid day).
- `TemporalAdjusters` answer questions like "next Monday" and "last day of the month".
- Calling `plusDays` without assigning changes nothing: the classes are immutable.
- The same `Instant` shown in Dhaka (UTC+6) and New York (UTC-5 in November) is one moment.
- On 29 March 2026 UK clocks jump forward. `Duration.ofDays(1)` adds exactly 24 hours, landing on 13:00 local time; `Period.ofDays(1)` keeps 12:00 local time.
- `DateTimeFormatter` patterns: `dd` day, `MM` month, `yyyy` year, `HH` 24-hour, `mm` minutes. Parsing an invalid date throws.

### Output
```text
today       : 2026-11-24 (TUESDAY)
age         : 31
plus 1 month: 2026-02-28
leap year?  : false
next Monday : 2026-11-30
month end   : 2026-11-30
unchanged   : 2026-01-01
instant     : 2026-11-24T03:30:00Z
Dhaka       : 2026-11-24T09:30+06:00[Asia/Dhaka]
New York    : 2026-11-23T22:30-05:00[America/New_York]
same moment : true
+Duration 1d: 2026-03-29T13:00+01:00[Europe/London]
+Period 1d  : 2026-03-29T12:00+01:00[Europe/London]
shift       : PT8H30M = 510 minutes
days until  : 31
formatted   : 01/12/2026 14:05
parsed back : 2026-12-01T14:05
ISO         : 2026-12-01T14:05
invalid date rejected: 2026-02-30
isBefore    : true
```

### Compare
| Old API | java.time |
|---|---|
| `Date` (mutable, really an instant) | `Instant` (immutable) |
| `Calendar` (months from 0) | `LocalDate`, `ZonedDateTime` (months from 1) |
| `SimpleDateFormat` (not thread-safe) | `DateTimeFormatter` (thread-safe) |
| Converting: `date.toInstant()`, `Date.from(instant)` | Bridges for legacy code |

## 4. Interview Questions
**Basic**
1. **Why was java.time introduced?**
   The old `Date` and `Calendar` were mutable, not thread-safe, and confusing (0-based months, 1900-based years). java.time is immutable, thread-safe and clearly modelled.
2. **LocalDateTime vs ZonedDateTime vs Instant?**
   LocalDateTime has no zone (a wall-clock value); ZonedDateTime adds a zone with DST rules; Instant is an exact moment in UTC.
3. **Duration vs Period?**
   Duration is time-based (hours, minutes, seconds); Period is date-based (years, months, days).

**Follow-up**
4. **What should you store in the database for "created at"?**
   An `Instant` (UTC) in a timestamp-with-time-zone column, converting to the user's zone only for display.
5. **Is `DateTimeFormatter` thread-safe?**
   Yes, it is immutable and can be a shared constant, unlike `SimpleDateFormat`.
6. **How do you test code that uses the current time?**
   Inject a `java.time.Clock` and call `LocalDate.now(clock)`; tests pass `Clock.fixed(...)`.

**Tricky / scenario**
7. **What is 31 January plus one month?**
   28 February (or 29 in a leap year): java.time clamps to the last valid day of the month.
8. **Users in different countries see wrong order times. What went wrong?**
   Likely storing `LocalDateTime` (no zone) or using the server's default zone. Store `Instant` in UTC and convert with the user's `ZoneId` in the API or UI.

**What the interviewer is really testing:** whether you can handle time zones and DST correctly, a classic source of production bugs.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| Storing `LocalDateTime` for events | Ambiguous across zones | `Instant` or `OffsetDateTime` |
| Using `ZoneId.systemDefault()` on servers | Behaviour depends on server settings | Explicit zones; run servers in UTC |
| Ignoring return values of `plusDays` | Value unchanged | Reassign |
| Pattern `YYYY` instead of `yyyy` | Week-based year: wrong year around New Year | Use `yyyy` (or `uuuu`) |
| `mm` for months | `mm` means minutes | `MM` for months |

### Best practices
- Use ISO-8601 strings in APIs.
- Inject `Clock` into services that depend on "now".
- Keep zone conversion at the edges (UI and API), UTC inside.

### Compare with
| Question | Type |
|---|---|
| "What moment did it happen?" | `Instant` |
| "What day is the delivery?" | `LocalDate` |
| "What time does the meeting start in Paris?" | `ZonedDateTime` |
| "How long did it take?" | `Duration` |
| "How old is the user?" | `Period` |

## 6. Hands-on Exercise
Write `static LocalDate addBusinessDays(LocalDate start, int days)` that skips Saturdays and Sundays. Then compute the delivery date for an order placed on Friday 2026-11-27 with 3 business days.

<details><summary>Solution</summary>

```java
import java.time.DayOfWeek;
import java.time.LocalDate;

public class BusinessDays {
    static LocalDate addBusinessDays(LocalDate start, int days) {
        LocalDate date = start;
        int added = 0;
        while (added < days) {
            date = date.plusDays(1);
            DayOfWeek dow = date.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) added++;
        }
        return date;
    }

    public static void main(String[] args) {
        LocalDate placed = LocalDate.of(2026, 11, 27);           // Friday
        System.out.println(placed.getDayOfWeek());                // FRIDAY
        System.out.println(addBusinessDays(placed, 3));           // 2026-12-02 (Mon, Tue, Wed)
    }
}
```
</details>

## 7. Recall
Q: Which java.time type represents a moment on the UTC timeline?
A: `Instant`.

Q: What is the difference between `Duration` and `Period`?
A: Duration is time-based (seconds and nanos); Period is date-based (years, months, days).

Q: Is `DateTimeFormatter` thread-safe?
A: Yes.

Q: How do you make time-dependent code testable?
A: Inject a `Clock` and use `Clock.fixed` in tests.

Q: What does `LocalDate.of(2026, 1, 31).plusMonths(1)` return?
A: 2026-02-28.

**Related:** [[Immutable Class Design]] · [[Entity Mapping]] · [[Auditing]]

@@ 3 | TOPIC | File I/O with java.nio.file | Path, Files, reading and writing text, walking directories and streams | SHOULD
# File I/O with java.nio.file

**Level: Beginner | Phase 3 | Priority: SHOULD | Since: Java 7 (NIO.2), Files.readString Java 11**

> **30-second interview answer:** Modern Java file I/O uses `java.nio.file`: a `Path` represents a location and the `Files` utility class does the work: `readString`, `writeString`, `readAllLines`, `lines` (lazy stream), `newBufferedReader`, `copy`, `move`, `delete`, `walk` and `list`. Always specify the charset (UTF-8 is the default since Java 18), close streams with try-with-resources, and use the streaming methods for large files instead of loading everything into memory.

## 1. Concept in Simple Words
### Analogy
An office filing system.

| Office | java.nio.file |
|---|---|
| The address of a folder or a document ("Cabinet 2 / Drawer 3 / invoice.txt") | `Path` |
| The clerk who reads, copies, moves and shreds documents | `Files` |
| Reading a short memo in one go | `Files.readString` |
| Reading a huge ledger page by page | `Files.lines` or a `BufferedReader` |
| Walking through every drawer of the cabinet | `Files.walk` |
| Returning the drawer key when finished | try-with-resources on streams |

### What it is
- **`Path`:** `Path.of("data", "orders.csv")`. Methods: `getFileName`, `getParent`, `resolve`, `relativize`, `normalize`, `toAbsolutePath`.
- **`Files`:** static methods for nearly everything.

| Task | Method |
|---|---|
| Small text file | `Files.readString(path)`, `Files.writeString(path, text)` |
| Lines as a list | `Files.readAllLines(path)` |
| Lines lazily | `Files.lines(path)` (must be closed) |
| Buffered streaming | `Files.newBufferedReader`, `Files.newBufferedWriter` |
| Bytes | `Files.readAllBytes`, `Files.write` |
| Existence and type | `Files.exists`, `Files.isDirectory`, `Files.size` |
| Create | `Files.createDirectories`, `Files.createTempFile` |
| Copy, move, delete | `Files.copy`, `Files.move`, `Files.delete`, `Files.deleteIfExists` |
| Directory listing | `Files.list` (one level), `Files.walk` (recursive), `Files.find` |

- **`StandardOpenOption`:** `CREATE`, `APPEND`, `TRUNCATE_EXISTING`, and others.

### Why it exists
The old `java.io.File` returned `false` instead of explaining failures, had poor support for symbolic links and metadata, and needed lots of boilerplate. NIO.2 gives clear exceptions and one-line operations.

## 2. How It Works
```text
Path.of("data").resolve("2026").resolve("orders.csv")   -> data/2026/orders.csv
Path.of("data/2026/../2025/x.csv").normalize()           -> data/2025/x.csv

Files.lines(path)  -> Stream<String> backed by an open file handle
   try (Stream<String> lines = Files.lines(path)) { ... }   // closes the handle
   Reads one line at a time: works for a 10 GB file with little memory.

Files.readAllLines(path) -> List<String> with EVERY line in memory: fine for small files only.
```

**Charsets:** text files are bytes; a charset maps bytes to characters. Since Java 18 the default is UTF-8 everywhere (JEP 400); before that it depended on the operating system, which caused "garbled characters" bugs on Windows. Being explicit (`StandardCharsets.UTF_8`) is still good practice.

**In Spring Boot** you usually read files through `Resource` (`classpath:data.csv`), handle uploads with `MultipartFile` (Phase 11), and store large files in object storage, but the same `Files` API is used underneath.

## 3. Code Example
### Code
```java
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class FilesDemo {
    public static void main(String[] args) throws IOException {
        Path root = Files.createTempDirectory("orderhub");      // safe scratch area
        Path data = root.resolve("data");
        Files.createDirectories(data);

        // 1. Write and read a small text file
        Path orders = data.resolve("orders.csv");
        Files.writeString(orders, "id,item,qty\n1,Book,2\n2,Pen,10\n", StandardCharsets.UTF_8);
        System.out.println("size bytes : " + Files.size(orders));
        System.out.println("first line : " + Files.readAllLines(orders).get(0));

        // 2. Append with options
        Files.writeString(orders, "3,Lamp,1\n", StandardOpenOption.APPEND);

        // 3. Stream lines lazily and process them (must close the stream)
        try (Stream<String> lines = Files.lines(orders)) {
            int totalQty = lines.skip(1)                          // skip header
                    .map(line -> line.split(","))
                    .mapToInt(cols -> Integer.parseInt(cols[2]))
                    .sum();
            System.out.println("total qty  : " + totalQty);
        }

        // 4. Buffered writer for many lines
        Path report = data.resolve("report.txt");
        try (BufferedWriter w = Files.newBufferedWriter(report, StandardCharsets.UTF_8)) {
            for (int i = 1; i <= 3; i++) {
                w.write("line " + i);
                w.newLine();
            }
        }
        System.out.println("report     : " + Files.readAllLines(report));

        // 5. Copy, move, list
        Path backup = root.resolve("backup");
        Files.createDirectories(backup);
        Files.copy(orders, backup.resolve("orders.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.move(report, backup.resolve("report.txt"), StandardCopyOption.REPLACE_EXISTING);
        try (Stream<Path> files = Files.walk(root)) {
            List<String> names = files.filter(Files::isRegularFile)
                    .map(p -> root.relativize(p).toString().replace('\\', '/'))
                    .sorted()
                    .toList();
            System.out.println("all files  : " + names);
        }

        // 6. Path operations
        Path messy = Path.of("data", "2026", "..", "2025", "x.csv");
        System.out.println("normalize  : " + messy.normalize().toString().replace('\\', '/'));
        System.out.println("file name  : " + orders.getFileName());

        // 7. Clear exceptions
        try {
            Files.readString(root.resolve("missing.txt"));
        } catch (NoSuchFileException e) {
            System.out.println("missing    : NoSuchFileException for " + Path.of(e.getFile()).getFileName());
        }

        // 8. Clean up: delete deepest paths first
        try (Stream<Path> all = Files.walk(root)) {
            all.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
        System.out.println("cleaned up : " + !Files.exists(root));
    }
}
```

### Walkthrough
- `createTempDirectory` gives a unique folder, so the demo never touches your real files.
- `writeString` and `readAllLines` handle small files in one call. `APPEND` adds to the end instead of overwriting.
- `Files.lines` returns a lazy stream tied to an open file, so it is used in try-with-resources. The pipeline skips the header, splits columns and sums quantities: 2 + 10 + 1 = 13.
- `newBufferedWriter` writes many lines efficiently.
- `copy` and `move` take options; `walk` lists everything recursively (also needs closing). Paths are printed with `/` so the output looks the same on every OS.
- `normalize` removes `..` segments.
- A missing file throws `NoSuchFileException`, a specific `IOException` subclass that names the file.
- Deleting in reverse order removes files before their folders.

### Output
```text
size bytes : 30
first line : id,item,qty
total qty  : 13
report     : [line 1, line 2, line 3]
all files  : [backup/orders.csv, backup/report.txt, data/orders.csv]
normalize  : data/2025/x.csv
file name  : orders.csv
missing    : NoSuchFileException for missing.txt
cleaned up : true
```

### Compare
| `java.io.File` (old) | `java.nio.file` (modern) |
|---|---|
| `file.delete()` returns false on failure | `Files.delete` throws an exception explaining why |
| Manual streams and loops to read text | `Files.readString`, `Files.lines` |
| Weak symbolic link and attribute support | Full support |
| `new File("a/b")` | `Path.of("a", "b")` |

## 4. Interview Questions
**Basic**
1. **How do you read a whole text file in modern Java?**
   `Files.readString(path)` (Java 11) or `Files.readAllLines(path)` for a list of lines.
2. **What is the difference between `Path` and `Files`?**
   `Path` describes a location; `Files` is a utility class with static methods that act on paths.
3. **How do you append to a file?**
   `Files.writeString(path, text, StandardOpenOption.APPEND)` (add `CREATE` if it may not exist).

**Follow-up**
4. **How do you process a 10 GB log file?**
   Stream it line by line with `Files.lines` or a `BufferedReader` inside try-with-resources; never `readAllLines`.
5. **Why must the stream from `Files.lines` or `Files.walk` be closed?**
   It holds an open file or directory handle; not closing it leaks handles until the process hits OS limits.
6. **Why specify a charset?**
   Text is bytes; without an explicit charset older Java versions used the platform default, which differs between Windows and Linux. Java 18+ defaults to UTF-8.

**Tricky / scenario**
7. **A user uploads a file named `../../etc/passwd`. What is the risk and the fix?**
   Path traversal: resolving it could write outside the upload folder. Normalize the resolved path and check it still `startsWith` the upload directory, or generate your own file names.
8. **How do you write a file so readers never see a half-written version?**
   Write to a temporary file in the same directory, then `Files.move(temp, target, ATOMIC_MOVE)`.

**What the interviewer is really testing:** practical, safe file handling: streaming, resource closing and security.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| `readAllLines` on huge files | `OutOfMemoryError` | Stream lines |
| Not closing `Files.lines` or `Files.walk` | Leaked file handles | try-with-resources |
| Building paths with string concatenation and `/` | Breaks across OSes, traversal bugs | `Path.resolve` |
| Trusting user-supplied file names | Path traversal | Normalize and check, or rename |

### Best practices
- Use `Path` and `Files`, not `java.io.File`, in new code.
- Be explicit about UTF-8 when interoperating with other systems.
- In Spring, load bundled files with `ClassPathResource` and keep uploaded files outside the app's working directory (or in object storage).

### Compare with
| Size of data | Approach |
|---|---|
| Small config or template | `Files.readString` |
| Medium list of lines | `Files.readAllLines` |
| Large or unknown size | `Files.lines` or `BufferedReader` |
| Binary | `Files.newInputStream` with a buffer |

## 6. Hands-on Exercise
Write a program that creates a temporary CSV with lines `sku,price` (`BOOK,12.50`, `PEN,1.20`, `LAMP,30.00`), reads it with `Files.lines`, and writes a new file `expensive.csv` containing only rows with a price above 10, then prints that file's content.

<details><summary>Solution</summary>

```java
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FilterCsv {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("csv");
        Path in = dir.resolve("prices.csv");
        Files.write(in, List.of("sku,price", "BOOK,12.50", "PEN,1.20", "LAMP,30.00"));

        List<String> expensive;
        try (Stream<String> lines = Files.lines(in)) {
            expensive = lines.skip(1)
                    .filter(l -> new BigDecimal(l.split(",")[1]).compareTo(BigDecimal.TEN) > 0)
                    .toList();
        }
        Path out = dir.resolve("expensive.csv");
        Files.write(out, expensive);
        System.out.println(Files.readString(out).strip());
    }
}
```
Output:
```text
BOOK,12.50
LAMP,30.00
```
</details>

## 7. Recall
Q: Which method reads an entire small text file into a String (Java 11+)?
A: `Files.readString(path)`.

Q: Why must `Files.lines` be used in try-with-resources?
A: The stream holds an open file handle that must be closed.

Q: Which exception does `Files.readString` throw for a missing file?
A: `NoSuchFileException` (a subclass of `IOException`).

Q: How do you join path segments safely?
A: `Path.of("a", "b")` or `path.resolve("child")`.

Q: Since which Java version is UTF-8 the default charset?
A: Java 18.

**Related:** [[try, catch, finally and try-with-resources]] · [[File Upload and Download]] · [[Stream Pipeline: Lazy Evaluation]]

@@ 3 | TOPIC | Practice — Coding drill: Strings and exceptions | Ten interview tasks on string manipulation, exception flow and core APIs | MUST
# Coding drill: Strings and exceptions

Ten tasks mixing string algorithms (very common in screening rounds) and exception-flow puzzles. Try each before opening the solution.

## 1. Reverse words in a sentence
`"  the sky   is blue "` → `"blue is sky the"` (single spaces, no leading or trailing spaces).

<details><summary>Solution</summary>

```java
static String reverseWords(String s) {
    String[] words = s.strip().split("\\s+");
    StringBuilder sb = new StringBuilder();
    for (int i = words.length - 1; i >= 0; i--) {
        sb.append(words[i]);
        if (i > 0) sb.append(' ');
    }
    return sb.toString();
}
```
</details>

## 2. Are two strings anagrams?
`"listen"` and `"silent"` → true. Lower-case letters only.

<details><summary>Solution</summary>

```java
static boolean isAnagram(String a, String b) {
    if (a.length() != b.length()) return false;
    int[] counts = new int[26];
    for (int i = 0; i < a.length(); i++) {
        counts[a.charAt(i) - 'a']++;
        counts[b.charAt(i) - 'a']--;
    }
    for (int c : counts) if (c != 0) return false;
    return true;
}
```
O(n) time, O(1) extra space (26 counters).
</details>

## 3. First non-repeating character
`"swiss"` → `'w'`. Return `'_'` if none.

<details><summary>Solution</summary>

```java
import java.util.LinkedHashMap;
import java.util.Map;

static char firstUnique(String s) {
    Map<Character, Integer> counts = new LinkedHashMap<>();   // keeps insertion order
    for (char c : s.toCharArray()) counts.merge(c, 1, Integer::sum);
    for (Map.Entry<Character, Integer> e : counts.entrySet()) {
        if (e.getValue() == 1) return e.getKey();
    }
    return '_';
}
```
</details>

## 4. Count character frequency, sorted by character
`"banana"` → `{a=3, b=1, n=2}`.

<details><summary>Solution</summary>

```java
import java.util.Map;
import java.util.TreeMap;

static Map<Character, Integer> frequency(String s) {
    Map<Character, Integer> freq = new TreeMap<>();
    for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
    return freq;
}
```
</details>

## 5. Predict the output: finally and return
```java
static int test() {
    int x = 10;
    try {
        x = 20;
        throw new RuntimeException();
    } catch (RuntimeException e) {
        x = 30;
        return x;
    } finally {
        x = 40;
        System.out.println("finally x=" + x);
    }
}
System.out.println(test());
```

<details><summary>Solution</summary>

```text
finally x=40
30
```
The catch block's `return x` saves 30 before finally runs; finally changes the variable, not the saved return value.
</details>

## 6. Predict the output: which catch runs?
```java
try {
    Object o = "text";
    Integer i = (Integer) o;
} catch (NumberFormatException e) {
    System.out.println("NFE");
} catch (RuntimeException e) {
    System.out.println("RTE " + e.getClass().getSimpleName());
} finally {
    System.out.println("done");
}
```

<details><summary>Solution</summary>

```text
RTE ClassCastException
done
```
Casting a String to Integer throws `ClassCastException`, which is not an NFE, so the `RuntimeException` block catches it.
</details>

## 7. Valid parentheses
`"([]{})"` → true, `"(]"` → false.

<details><summary>Solution</summary>

```java
import java.util.ArrayDeque;
import java.util.Deque;

static boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        switch (c) {
            case '(' -> stack.push(')');
            case '[' -> stack.push(']');
            case '{' -> stack.push('}');
            default -> {
                if (stack.isEmpty() || stack.pop() != c) return false;
            }
        }
    }
    return stack.isEmpty();
}
```
Push the expected closing bracket; each closing bracket must match the top.
</details>

## 8. Parse key=value pairs safely
Parse `"a=1;b=two;c=3"` into a map of integers, skipping invalid values and reporting them.

<details><summary>Solution</summary>

```java
import java.util.LinkedHashMap;
import java.util.Map;

static Map<String, Integer> parse(String input) {
    Map<String, Integer> result = new LinkedHashMap<>();
    for (String pair : input.split(";")) {
        String[] kv = pair.split("=", 2);              // limit 2: values may contain '='
        if (kv.length != 2) continue;
        try {
            result.put(kv[0].strip(), Integer.parseInt(kv[1].strip()));
        } catch (NumberFormatException e) {
            System.out.println("skipping " + kv[0] + ": not a number");
        }
    }
    return result;
}
// parse("a=1;b=two;c=3") prints "skipping b: not a number" and returns {a=1, c=3}
```
</details>

## 9. Retry with a custom exception
Write `retry(Supplier<T> action, int attempts)` that retries on `RuntimeException` and finally throws `RetryExhaustedException` with the last error as the cause.

<details><summary>Solution</summary>

```java
import java.util.function.Supplier;

static class RetryExhaustedException extends RuntimeException {
    RetryExhaustedException(int attempts, Throwable cause) {
        super("Failed after " + attempts + " attempts", cause);
    }
}

static <T> T retry(Supplier<T> action, int attempts) {
    RuntimeException last = null;
    for (int i = 1; i <= attempts; i++) {
        try {
            return action.get();
        } catch (RuntimeException e) {
            last = e;
            System.out.println("attempt " + i + " failed: " + e.getMessage());
        }
    }
    throw new RetryExhaustedException(attempts, last);   // keep the cause
}
```
In Phase 20 you will use Resilience4j `@Retry`, which does the same with backoff.
</details>

## 10. Money formatting
Format `BigDecimal("1234567.5")` as `"1,234,567.50"`.

<details><summary>Solution</summary>

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

BigDecimal amount = new BigDecimal("1234567.5").setScale(2, RoundingMode.HALF_EVEN);
String text = new DecimalFormat("#,##0.00").format(amount);   // 1,234,567.50
// or: String.format("%,.2f", amount)
```
`DecimalFormat` is not thread-safe: create one per use or per thread.
</details>

@@ 3 | TOPIC | Practice — Phase 3 review: Exceptions and Core APIs | Cheat sheet, top 10 questions and rapid-fire flashcards for Phase 3 | MUST
# Phase 3 review: Exceptions and Core APIs

## Cheat sheet

| Idea | Remember |
|---|---|
| Hierarchy | Throwable → Error and Exception; RuntimeException = unchecked |
| Checked | Must catch or declare (`IOException`, `SQLException`, `InterruptedException`) |
| Unchecked | Bugs and invalid input (`NPE`, `IllegalArgumentException`) |
| Catch order | Specific before general |
| finally | Always runs except JVM halt; never return from it |
| try-with-resources | `AutoCloseable`, reverse-order close, suppressed exceptions |
| Custom exceptions | Extend RuntimeException, keep the cause, add context |
| Spring rollback | Unchecked by default; checked needs `rollbackFor` |
| String | Immutable, pooled literals, `equals` not `==` |
| `new String("x")` | Up to two objects |
| StringBuilder | Mutable, fast, not synchronized; StringBuffer is synchronized |
| BigDecimal | From String, assign results, divide with scale and rounding, compare with `compareTo` |
| java.time | Immutable; Instant for moments, LocalDate for dates, ZonedDateTime for zones |
| Duration vs Period | Time-based vs date-based |
| Files | `readString`, `lines` in try-with-resources, `walk`, explicit UTF-8 |

### Key code snippets
```java
// try-with-resources plus exception translation
try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    return reader.readLine();
} catch (IOException e) {
    throw new ImportFailedException("Cannot read " + path, e);   // keep the cause
}
```

```java
// Custom unchecked exception
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(long id) { super("Order " + id + " not found"); }
}
```

```java
// Strings
boolean same = "yes".equals(input);                    // null-safe
String joined = String.join(", ", items);
StringBuilder sb = new StringBuilder(); sb.append("a").append(1);
```

```java
// Money and time
BigDecimal total = price.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_EVEN);
Instant createdAt = Instant.now(clock);
LocalDate delivery = LocalDate.now(clock).plusDays(3);
```

## The 10 most likely interview questions

1. **Checked vs unchecked exceptions?** Compiler-enforced vs not; recoverable conditions vs bugs.
2. **Error vs Exception?** JVM-level failures you should not catch vs conditions you may handle.
3. **Does finally always run?** Yes, except when the JVM stops; returning from finally swallows exceptions.
4. **What is try-with-resources?** Automatic closing of `AutoCloseable` resources, with suppressed exceptions.
5. **How do you design custom exceptions?** Unchecked, specific names, context in messages, keep causes, map centrally.
6. **Why is String immutable?** Security, thread safety, hashing, pooling.
7. **String pool and `new String`?** Literals are shared; `new` creates a separate object.
8. **String vs StringBuilder vs StringBuffer?** Immutable vs mutable vs mutable and synchronized.
9. **Why BigDecimal for money?** Exact decimal arithmetic; double is binary and inexact.
10. **LocalDateTime vs Instant?** No zone vs exact UTC moment; store Instant.

## Rapid-fire flashcards

Q: Is `IllegalStateException` checked or unchecked?
A: Unchecked.

Q: What does multi-catch syntax look like?
A: `catch (IOException | SQLException e)` (types written with a vertical bar between them).

Q: What does `e.getSuppressed()` return?
A: Exceptions from closing resources that were suppressed in favour of the primary exception.

Q: What does `"ja" + "va" == "java"` evaluate to?
A: true, because constant expressions are folded and pooled at compile time.

Q: What is the default capacity of `StringBuilder`?
A: 16.

Q: What does `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` return?
A: false, because the scales differ.

Q: Which rounding mode is banker's rounding?
A: `RoundingMode.HALF_EVEN`.

Q: Which java.time class should a "created at" timestamp use?
A: `Instant` (or `OffsetDateTime`).

Q: Which `Files` method streams a file lazily line by line?
A: `Files.lines(path)`.

Q: Why prefer `Path.resolve` over string concatenation for paths?
A: It handles separators correctly across operating systems and avoids traversal mistakes.
