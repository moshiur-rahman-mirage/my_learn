PKSF Full-stack Software Engineer — Study Material 

# **Software Engineer (Full-stack)** Study Material — PKSF Recruitment Preparation 

Java · Spring Boot · React · PostgreSQL · MongoDB · Redis OAuth2 / JWT / Keycloak · Microservices · Docker · Kubernetes · CI/CD 

_Companion to PKSF_Study_Plan.csv_ 

Page 1 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Contents** 

|How to Use This Material.............................................................................................................................................5<br>i|
|---|
|Phase 0 — Orientation & Strategy............................................................................................................................... 6|
|What the TOR Asks For............................................................................................................................................6<br>i|
|Microfinance Vocabulary......................................................................................................................................... 6<br>i|
|Selection Process.....................................................................................................................................................6|
|Phase 1 — CS Fundamentals........................................................................................................................................7|
|Data Structures........................................................................................................................................................7|
|Algorithms................................................................................................................................................................8<br>t|
|OOP and Software Design...................................................................................................................................... 10|
|Operating Systems................................................................................................................................................. 11|
|Computer Networks...............................................................................................................................................11|
|DBMS Theory.........................................................................................................................................................12<br>i|
|Phase 1 — Interview Questions.............................................................................................................................13|
|Phase 2 — Core Java.................................................................................................................................................. 14|
|Java Language Basics..............................................................................................................................................14|
|Collections Framework.......................................................................................................................................... 16|
|Functional Java and Streams..................................................................................................................................16<br>i|
|Concurrency and Multithreading........................................................................................................................... 17|
|JVM Internals.........................................................................................................................................................18|
|Modern Java (17/21)..............................................................................................................................................18|
|Build Tools — Maven.............................................................................................................................................18<br>i|
|Phase 2 — Interview Questions.............................................................................................................................18|
|Phase 3 — Spring and Spring Boot.............................................................................................................................20|
|Spring Core.............................................................................................................................................................20|
|Spring Boot Essentials............................................................................................................................................ 21|
|Building REST APIs..................................................................................................................................................22|
|Spring Data JPA and Hibernate..............................................................................................................................23|
|Other Spring Capabilities....................................................................................................................................... 25|
|Phase 3 — Interview Questions.............................................................................................................................25|
|Phase 4 — Databases.................................................................................................................................................27|
|SQL Mastery........................................................................................................................................................... 27|
|PostgreSQL Schema Design....................................................................................................................................28|
|PostgreSQL Indexing..............................................................................................................................................29|
|PostgreSQL Query Tuning......................................................................................................................................30|
|PostgreSQL Operations.......................................................................................................................................... 30|
|MongoDB............................................................................................................................................................... 31|
|Phase 4 — Interview Questions.............................................................................................................................31|
|Phase 5 — APIs, Integration and Microservices.........................................................................................................33|
|REST API Design..................................................................................................................................................... 33|



Page 2 of 63 

PKSF Full-stack Software Engineer — Study Material 

|Data Exchange Formats......................................................................................................................................... 33|
|---|
|Microservices Architecture.................................................................................................................................... 34|
|Messaging.............................................................................................................................................................. 36|
|Phase 5 — Interview Questions.............................................................................................................................36<br>ii|
|Phase 6 — Authentication and Security.....................................................................................................................37|
|Security Fundamentals...........................................................................................................................................37|
|Spring Security.......................................................................................................................................................37|
|JWT.........................................................................................................................................................................38|
|OAuth2 and OpenID Connect.................................................................................................................................38|
|Keycloak................................................................................................................................................................. 39|
|Data Protection and Auditing.................................................................................................................................39<br>i|
|Phase 6 — Interview Questions.............................................................................................................................40|
|Phase 7 — Redis Caching and Sessions......................................................................................................................41|
|Redis Fundamentals...............................................................................................................................................41|
|Caching Patterns....................................................................................................................................................41|
|Sessions and Distributed Use Cases....................................................................................................................... 42|
|Phase 7 — Interview Questions.............................................................................................................................42|
|Phase 8 — Frontend with React.................................................................................................................................44|
|HTML and CSS........................................................................................................................................................44|
|JavaScript............................................................................................................................................................... 44|
|TypeScript..............................................................................................................................................................45|
|React Core..............................................................................................................................................................45|
|React Ecosystem....................................................................................................................................................47|
|React Advanced..................................................................................................................................................... 48|
|Phase 8 — Interview Questions.............................................................................................................................48|
|Phase 9 — Testing and Code Quality.........................................................................................................................50|
|Backend Testing..................................................................................................................................................... 50<br>i|
|Frontend Testing.................................................................................................................................................... 51|
|Code Quality...........................................................................................................................................................51<br>i|
|Phase 9 — Interview Questions.............................................................................................................................51|
|Phase 10 — DevOps: Git, Docker, Kubernetes, CI/CD................................................................................................52|
|Git...........................................................................................................................................................................52|
|Linux and Servers................................................................................................................................................... 52|
|Docker.................................................................................................................................................................... 52|
|Kubernetes.............................................................................................................................................................54|
|CI/CD...................................................................................................................................................................... 55|
|Phase 10 — Interview Questions...........................................................................................................................56|
|Phase 11 — Cloud (Optional)..................................................................................................................................... 57|
|Phase 12 — System Design........................................................................................................................................ 58|
|Fundamentals........................................................................................................................................................58|
|Approach to Any Design Question......................................................................................................................... 58|



Page 3 of 63 

|PKSF Full-stack Software Engineer — Study Material|
|---|
|Case Study: PO Loan Management System........................................................................................................... 58|
|Phase 12 — Interview Questions...........................................................................................................................59|
|Phase 13 — Portfolio Project..................................................................................................................................... 60|
|Project: PO Microfinance Management System....................................................................................................60|
|Milestones..............................................................................................................................................................60|
|README Checklist.................................................................................................................................................. 60|
|Phase 14 — Exam and Interview Preparation............................................................................................................61|
|Written Test Strategy............................................................................................................................................. 61|
|Java Output Prediction — Practice........................................................................................................................ 61|
|Viva Preparation.................................................................................................................................................... 61|
|Rapid-Fire Revision List..........................................................................................................................................61|



Page 4 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **How to Use This Material** 

This guide follows PKSF_Study_Plan.csv phase by phase. Each section has short notes, code you should be able to write without looking, and likely written-test or viva questions with model answers. 

**Tip** Focus order: Java + Spring Boot + React + PostgreSQL first (mandatory). Then Security (JWT/OAuth2/Keycloak), Microservices, Redis, Docker/K8s/CI-CD (preferred). Cloud is optional. 

|**Week**|**Focus**|**Output**|
|---|---|---|
|1|CS fundamentals + Core Java|50 MCQs/day, 2 coding problems/day|
|2|Collections, Streams, Concurrency, JVM|Handwritten Java solutions|
|3|Spring Core, Boot, REST, JPA|CRUD API with validation and tests|
|4|SQL + PostgreSQL design, indexing, tuning|20 SQL problems, EXPLAIN practice|
|5|Spring Security, JWT, OAuth2, Keycloak|Secure API with Keycloak|
|6|React + TypeScript|Frontend for the API|
|7|Microservices, SOAP/XML, Redis, MongoDB|Split into services, add cache|
|8|Docker, Kubernetes, CI/CD, mock interviews|Deployed portfolio repo|



Page 5 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 0 — Orientation & Strategy** 

### **What the TOR Asks For** 

- **Mandatory:** Minimum 2 years full-stack experience; hands-on Java (Spring Boot) and React. 

- **Preferred:** Public GitHub with clean structure, tests, README and docker-compose; microservices; REST/JSON/XML/SOAP; OAuth2, JWT, Keycloak; PostgreSQL schema design, indexing and query tuning; MongoDB; Redis caching and sessions; Docker, Kubernetes, Git, CI/CD. 

- **Optional:** AWS/GCP/Azure familiarity or associate-level certification. 

- **Context:** PKSF works through 200+ Partner Organizations (POs). Data is financial and sensitive, so expect questions on security, data integrity, auditing and performance. 

### **Microfinance Vocabulary** 

- **PO (Partner Organization):** an NGO/MFI that borrows funds from PKSF and lends to members. 

- **Member/Borrower:** an individual in a PO group (samity). 

- **Loan disbursement:** money released to a borrower. **Installment:** a scheduled repayment (often weekly). 

- **Outstanding:** principal not yet repaid. **Overdue:** installments past due date. 

- **PAR (Portfolio at Risk):** outstanding of loans with overdue installments divided by total outstanding. 

- **Savings:** member deposits. **Service charge:** interest-like fee. 

**Tip** In design questions, model Loans, Installments and Transactions with NUMERIC money columns, an immutable ledger and an audit trail. 

### **Selection Process** 

- **Written:** MCQs (Java, OOP, DBMS, networking, OS, web), short coding by hand, SQL queries, short theory answers. 

- **Viva:** your past projects, Spring Boot internals, database design, security, why PKSF. 

- **Tip:** Practice writing Java on paper. Syntax errors cost marks when there is no IDE. 

Page 6 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 1 — CS Fundamentals** 

### **Data Structures** 

|**Structure**|**Access**|**Search**|**Insert**|**Delete**|**Notes**|
|---|---|---|---|---|---|
|Array|O(1)|O(n)|O(n)|O(n)|Contiguous memory|
|Linked list|O(n)|O(n)|O(1) at head|O(1) with node ref|No random access|
|Stack/Queue|-|O(n)|O(1)|O(1)|LIFO / FIFO|
|Hash table|-|O(1) avg|O(1) avg|O(1) avg|O(n) worst case|
|BST (balanced)|-|O(log n)|O(log n)|O(log n)|Unbalanced worst O(n)|
|Heap|O(1) top|O(n)|O(log n)|O(log n)|Priority queue|



#### **Linked List — Reverse and Detect Cycle** 

<mark>ListNode reverse(ListNode head) { ListNode prev = null, cur = head; while (cur != null) { ListNode next = cur.next; cur.next = prev; prev = cur; cur = next; } return prev; }</mark> 

<mark>boolean hasCycle(ListNode head) {   // Floyd's tortoise and hare ListNode slow = head, fast = head; while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; if (slow == fast) return true; } return false; }</mark> 

#### **Stack — Balanced Parentheses** 

<mark>boolean isBalanced(String s) { Deque<Character> st = new ArrayDeque<>(); for (char c : s.toCharArray()) { if (c == '(' || c == '{' || c == '[') st.push(c); else { if (st.isEmpty()) return false; char o = st.pop();</mark> 

<mark>if ((c == ')' && o != '(') || (c == '}' && o != '{') || (c == ']' && o != '[')) return false; } } return st.isEmpty(); }</mark> 

#### **Trees — Traversals** 

- **Inorder (L, Root, R):** gives sorted order for a BST. 

- **Preorder (Root, L, R):** copy/serialize a tree. **Postorder (L, R, Root):** delete/free a tree. 

- **Level order:** BFS using a queue. 

<mark>int height(TreeNode n) { return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right)); }</mark> 

Page 7 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>List<List<Integer>> levelOrder(TreeNode root) { List<List<Integer>> res = new ArrayList<>(); if (root == null) return res; Queue<TreeNode> q = new LinkedList<>(); q.add(root); while (!q.isEmpty()) { int size = q.size(); List<Integer> level = new ArrayList<>(); for (int i = 0; i < size; i++) { TreeNode n = q.poll(); level.add(n.val); if (n.left != null) q.add(n.left); if (n.right != null) q.add(n.right); } res.add(level); } return res; }</mark> 

#### **Graphs — BFS and DFS** 

<mark>void bfs(Map<Integer, List<Integer>> g, int start) { Set<Integer> seen = new HashSet<>(List.of(start)); Queue<Integer> q = new ArrayDeque<>(List.of(start)); while (!q.isEmpty()) { int u = q.poll(); System.out.println(u); for (int v : g.getOrDefault(u, List.of())) if (seen.add(v)) q.add(v); } }</mark> 

- **BFS:** shortest path in unweighted graphs, level-wise processing. 

- **DFS:** cycle detection, topological sort, connected components. 

- **Topological sort:** ordering of a DAG (e.g., task dependencies). Use Kahn's algorithm (in-degree + queue). 

### **Algorithms** 

#### **Complexity Cheat Sheet** 

|**Algorithm**|**Best**|**Average**|**Worst**|**Space**|**Stable**|
|---|---|---|---|---|---|
|Bubble sort|O(n)|O(n²)|O(n²)|O(1)|Yes|
|Insertion sort|O(n)|O(n²)|O(n²)|O(1)|Yes|
|Selection sort|O(n²)|O(n²)|O(n²)|O(1)|No|
|Merge sort|O(n log n)|O(n log n)|O(n log n)|O(n)|Yes|
|Quick sort|O(n log n)|O(n log n)|O(n²)|O(log n)|No|
|Heap sort|O(n log n)|O(n log n)|O(n log n)|O(1)|No|
|Binary search|O(1)|O(log n)|O(log n)|O(1)|-|



**Tip** Java's Arrays.sort uses dual-pivot Quicksort for primitives and TimSort (stable) for objects; Collections.sort uses TimSort. 

#### **Binary Search** 

<mark>int binarySearch(int[] a, int target) { int lo = 0, hi = a.length - 1; while (lo <= hi) { int mid = lo + (hi - lo) / 2;   // avoids int overfow</mark> l 

Page 8 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>if (a[mid] == target) return mid; if (a[mid] < target) lo = mid + 1; else hi = mid - 1; } return -1; }</mark> 

#### **Merge Sort** 

<mark>void mergeSort(int[] a, int l, int r) { if (l >= r) return; int m = (l + r) / 2; mergeSort(a, l, m); mergeSort(a, m + 1, r); int[] tmp = new int[r - l + 1]; int i = l, j = m + 1, k = 0; while (i <= m && j <= r) tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++]; while (i <= m) tmp[k++] = a[i++]; while (j <= r) tmp[k++] = a[j++]; System.arraycopy(tmp, 0, a, l, tmp.length); }</mark> 

#### **Two Pointers and Sliding Window** 

<mark>// Longest substring without repeating characters int lengthOfLongestSubstring(String s) { Map<Character, Integer> last = new HashMap<>(); int best = 0, left = 0; for (int right = 0; right < s.length(); right++) { char c = s.charAt(right); if (last.containsKey(c) && last.get(c) >= left) left = last.get(c) + 1; last.put(c, right); best = Math.max(best, right - left + 1); } return best; }</mark> 

<mark>// Two Sum with HashMap — O(n) int[] twoSum(int[] nums, int target) { Map<Integer, Integer> idx = new HashMap<>(); for (int i = 0; i < nums.length; i++) { Integer j = idx.get(target - nums[i]); if (j != null) return new int[]{j, i}; idx.put(nums[i], i); } return new int[0]; }</mark> 

#### **Dynamic Programming** 

- Use when a problem has **overlapping subproblems** and **optimal substructure** . 

- **Memoization:** top-down recursion with a cache. **Tabulation:** bottom-up loop filling a table. 

<mark>// Coin change: minimum coins to make amount int coinChange(int[] coins, int amount) { int[] dp = new int[amount + 1]; Arrays.f</mark> i <mark>ll(dp, amount + 1); dp[0] = 0; for (int a = 1; a <= amount; a++) for (int c : coins) if (c <= a) dp[a] = Math.min(dp[a], dp[a - c] + 1); return dp[amount] > amount ? -1 : dp[amount]; }</mark> 

#### **Common Handwritten Problems** 

<mark>boolean isPalindrome(String s) { int i = 0, j = s.length() - 1;</mark> 

Page 9 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>while (i < j) if (s.charAt(i++) != s.charAt(j--)) return false; return true; } boolean isPrime(int n) { if (n < 2) return false; for (int i = 2; (long) i * i <= n; i++) if (n % i == 0) return false; return true; } long factorial(int n) { return n <= 1 ? 1 : n * factorial(n - 1); } Map<Character, Long> charFrequency(String s) { return s.chars().mapToObj(c -> (char) c) .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting())); } boolean isAnagram(String a, String b) { if (a.length() != b.length()) return false; int[] cnt = new int[26]; for (int i = 0; i < a.length(); i++) { cnt[a.charAt(i) - 'a']++; cnt[b.charAt(i) - 'a']--; } for (int c : cnt) if (c != 0) return false; return true; } void fzzBuzz(int n) {</mark> i <mark>for (int i = 1; i <= n; i++) System.out.println(i % 15 == 0 ? "FizzBuzz" : i % 3 == 0 ? "Fizz" : i % 5 == 0 ? "Buzz" : String.valueOf(i)); }</mark> 

### **OOP and Software Design** 

#### **Four Pillars** 

- **Encapsulation:** hide state behind methods (private fields + getters/validated setters). 

- **Abstraction:** expose what, hide how (interfaces, abstract classes). 

- **Inheritance:** an IS-A relationship and code reuse (extends). 

- **Polymorphism:** one interface, many forms. Compile-time = overloading (same name, different params). Runtime = overriding (subclass redefines; resolved by dynamic dispatch). 

#### **SOLID** 

- **S — Single Responsibility:** a class has one reason to change. LoanService should not also generate PDFs. 

- **O — Open/Closed:** open for extension, closed for modification. Add a new InterestCalculator implementation instead of editing if/else. 

- **L — Liskov Substitution:** subtypes must work wherever the base type is used. Classic violation: Square extends Rectangle. 

- **I — Interface Segregation:** many small interfaces beat one fat interface. 

- **D — Dependency Inversion:** depend on abstractions. Spring DI is the practical form of this. 

<mark>// Open/Closed + Strategy pattern public interface InterestCalculator { BigDecimal calculate(BigDecimal principal, int months); }</mark> 

<mark>@Component("fat")</mark> l <mark>class FlatInterest implements InterestCalculator { public BigDecimal calculate(BigDecimal p, int m) {</mark> return p.multiply(new BigDecimal("0.10")).multiply(BigDecimal.valueOf(m)).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP); <mark>} }</mark> 

<mark>@Component("declining")</mark> 

Page 10 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>class DecliningInterest implements InterestCalculator { /* ... */ }</mark> 

<mark>@Service class LoanService { private f</mark> i <mark>nal Map<String, InterestCalculator> calculators; // Spring injects all beans by name LoanService(Map<String, InterestCalculator> calculators) { this.calculators = calculators; } BigDecimal interest(String method, BigDecimal p, int m) { return calculators.get(method).calculate(p, m); } }</mark> 

#### **Design Patterns You Must Know** 

|**Pattern**|**Type**|**Purpose**|**Spring/Java example**|
|---|---|---|---|
|Singleton|Creational|One instance|Spring beans (default scope)|
|Factory|Creational|Hide object creation|BeanFactory, Calendar.getInstance()|
|Builder|Creational|Build complex objects step by step|Lombok @Builder, StringBuilder|
|Adapter|Structural|Convert one interface to another|HandlerAdapter in Spring MVC|
|Decorator|Structural|Add behavior by wrapping|BufferedInputStream|
|Proxy|Structural|Control access|Spring AOP, @Transactional proxies|
|Facade|Structural|Simple interface over a subsystem|Service layer|
|Strategy|Behavioral|Swap algorithms at runtime|Comparator, InterestCalculator|
|Observer|Behavioral|Notify subscribers|ApplicationEventPublisher, @EventListener|
|Template Method|Behavioral|Fixed skeleton, variable steps|JdbcTemplate, RestTemplate|
|Chain of Responsibility|Behavioral|Pass request along handlers|Servlet filters, Spring Security filter chain|



<mark>// Thread-safe lazy singleton (holder idiom) public class Confg {</mark> i <mark>private Confg() {}</mark> i <mark>private static class Holder { static fnal Conf</mark> i i <mark>g INSTANCE = new Confg(); }</mark> i <mark>public static Confg getInstance() { return Holder.INSTANCE; }</mark> i <mark>}</mark> 

#### **SDLC and Agile** 

- **Waterfall:** sequential phases; fits fixed requirements. 

- **Agile/Scrum:** iterative sprints (1–4 weeks). Roles: Product Owner, Scrum Master, Dev Team. Ceremonies: sprint planning, daily stand-up, review, retrospective. Artifacts: product backlog, sprint backlog, increment. 

- **User story:** "As a PO officer, I want to record a repayment so that the outstanding balance is up to date." 

### **Operating Systems** 

- **Process:** an independent program with its own memory space. **Thread:** a lightweight unit inside a process that shares heap memory but has its own stack. 

- **Deadlock conditions (all four needed):** mutual exclusion, hold and wait, no preemption, circular wait. Prevent by breaking one, for example by always acquiring locks in a fixed order. 

- **Mutex vs semaphore:** a mutex allows one owner. A counting semaphore allows N concurrent holders. 

- **Race condition:** the outcome depends on thread timing. Fix with synchronization or atomic operations. 

- **Virtual memory/paging:** memory is split into fixed pages. A page fault loads a page from disk. Thrashing means too many page faults. 

- **Scheduling:** FCFS, SJF (optimal average wait), Round Robin (time quantum, fair), Priority (risk of starvation, fixed by aging). 

Page 11 of 63 

PKSF Full-stack Software Engineer — Study Material 

### **Computer Networks** 

|**OSI Layer**|**Example**|
|---|---|
|7 Application|HTTP, DNS, SMTP, FTP|
|6 Presentation|TLS/SSL, encoding|
|5 Session|Sessions, RPC|
|4 Transport|TCP, UDP|
|3 Network|IP, ICMP, routers|
|2 Data Link|Ethernet, MAC, switches|
|1 Physical|Cables, signals|



- **TCP:** connection-oriented (3-way handshake SYN, SYN-ACK, ACK), reliable, ordered, flow/congestion control. Used for HTTP, DB connections. 

- **UDP:** connectionless, no delivery guarantee, low latency. Used for DNS, video, VoIP. 

- **HTTPS:** HTTP over TLS. TLS handshake: the server sends its certificate, the client verifies it, and both agree on a symmetric session key. 

- **Safe methods:** GET, HEAD, OPTIONS. **Idempotent:** GET, PUT, DELETE, HEAD, OPTIONS. **POST** is neither. 

|**Code**|**Meaning**|
|---|---|
|200 OK / 201 Created / 204 No Content|Success|
|301 / 302 / 304 Not Modified|Redirects / caching|
|400 Bad Request|Validation error|
|401 Unauthorized|Not authenticated|
|403 Forbidden|Authenticated but not allowed|
|404 Not Found / 409 Conflict|Missing / state conflict (e.g. version mismatch)|
|422 Unprocessable Entity|Semantic validation failure|
|429 Too Many Requests|Rate limited|
|500 / 502 / 503 / 504|Server error / bad gateway / unavailable / gateway timeout|



- **What happens when you type a URL:** DNS lookup, then TCP connect, TLS handshake, HTTP request, server processes it, response, then the browser parses HTML, fetches CSS/JS and renders. 

- **CORS:** a browser rule. A page from origin A calling API origin B needs B to send Access-Control-Allow-Origin. Non-simple requests send an OPTIONS preflight first. 

- **Cookies:** HttpOnly (JavaScript can't read it), Secure (sent only over HTTPS), SameSite (Strict/Lax/None, which mitigates CSRF). 

### **DBMS Theory** 

#### **Normalization** 

- **1NF:** atomic values, no repeating groups. 

- **2NF:** 1NF + no partial dependency (non-key column depends on the whole composite key). 

- **3NF:** 2NF + no transitive dependency (non-key depends only on key). 

- **BCNF:** every determinant is a candidate key. 

- **Denormalize** for read-heavy reporting, with care. 

**Tip** Example: storing po_name in the loans table violates 3NF (loan_id → po_id → po_name). Move it to a pos table. 

Page 12 of 63 

PKSF Full-stack Software Engineer — Study Material 

#### **ACID and Isolation** 

- **Atomicity:** all or nothing. **Consistency:** constraints hold. **Isolation:** concurrent transactions don't interfere. **Durability:** committed data survives a crash (WAL). 

|**Isolation level**|**Dirty read**|**Non-repeatable read**|**Phantom**|
|---|---|---|---|
|Read Uncommitted|Possible|Possible|Possible|
|Read Committed (Postgres default)|No|Possible|Possible|
|Repeatable Read|No|No|Possible (Postgres prevents)|
|Serializable|No|No|No|



- **CAP:** in a network partition, a distributed system must choose Consistency or Availability. **BASE:** Basically Available, Soft state, Eventually consistent. 

### **Phase 1 — Interview Questions** 

##### **Q. Difference between an array and a linked list?** 

**A.** An array has contiguous memory, O(1) index access and costly inserts. A linked list has scattered nodes, O(n) access, O(1) insert/delete given the node, and extra memory for pointers. 

##### **Q. Why is HashMap lookup O(1) on average but O(n) in the worst case?** 

**A.** The hash maps a key to a bucket. If many keys collide in one bucket, lookup scans that bucket. Java 8+ turns large buckets (more than 8 entries) into red-black trees, so the worst case becomes O(log n). 

##### **Q. Explain 401 vs 403.** 

**A.** 401 means you are not authenticated (missing or invalid credentials). 403 means you are authenticated but lack permission. 

##### **Q. What is a deadlock and how do you prevent it in Java?** 

**A.** Two or more threads each wait for a lock held by another. Prevent it by acquiring locks in a consistent global order, using tryLock with a timeout, or reducing lock scope. 

##### **Q. What is the difference between process and thread?** 

**A.** Processes have separate address spaces. Threads in a process share heap and code but each has its own stack and registers. Threads are cheaper to create and switch. 

Page 13 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 2 — Core Java** 

### **Java Language Basics** 

- **JDK** = JRE + dev tools (javac, jar, jdb). **JRE** = JVM + libraries. **JVM** runs bytecode; the JIT compiles hot code to native code. 

- **Primitives:** byte, short, int, long, float, double, char, boolean. Wrappers allow null and are needed for generics. 

- **Integer cache:** Integer.valueOf caches -128 to 127, so Integer a=127,b=127; a==b is true but with 128 it is false. Always compare objects with equals(). 

- **Money:** never use double. Use BigDecimal with the String constructor and an explicit RoundingMode. 

<mark>BigDecimal a = new BigDecimal("0.1"), b = new BigDecimal("0.2"); System.out.println(a.add(b));          // 0.3 System.out.println(0.1 + 0.2);         // 0.30000000000000004 BigDecimal inst = new BigDecimal("10000").divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP); // 833.33</mark> 

#### **Strings** 

- String is **immutable** and final. That makes it thread-safe, hash-cacheable and safe in the pool. 

- Literals go to the **string pool** . new String("x") creates a new heap object. 

- **StringBuilder:** mutable, not synchronized, fast. **StringBuffer:** synchronized, legacy. 

- Use StringBuilder for concatenation in loops. 

<mark>String s1 = "pksf", s2 = "pksf", s3 = new String("pksf"); s1 == s2;        // true  (same pool reference) s1 == s3;        // false (diferent objects)</mark> f <mark>s1.equals(s3);   // true s3.intern() == s1; // true</mark> 

#### **equals() and hashCode()** 

- **Contract:** if a.equals(b) then a.hashCode() == b.hashCode(). The reverse is not required. 

- If you override equals without hashCode, HashMap/HashSet break (equal objects land in different buckets). 

<mark>public fnal class Member {</mark> i <mark>private f</mark> i <mark>nal String nid; private f</mark> i <mark>nal String name; public Member(String nid, String name) { this.nid = nid; this.name = name; } @Override public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof Member m)) return false; return nid.equals(m.nid); } @Override public int hashCode() { return Objects.hash(nid); } } // Or simply: public record Member(String nid, String name) {}  — equals/hashCode generated</mark> 

#### **Keywords and Access Modifiers** 

|**Modifier**|**Class**|**Package**|**Subclass**|**World**|
|---|---|---|---|---|
|private|Yes|No|No|No|
|default (none)|Yes|Yes|No|No|
|protected|Yes|Yes|Yes|No|
|public|Yes|Yes|Yes|Yes|



- **static:** belongs to the class rather than an instance. A static method cannot use this. 

- **final:** a final variable can't be reassigned, a final method can't be overridden, a final class can't be extended. 

Page 14 of 63 

PKSF Full-stack Software Engineer — Study Material 

- **finally:** a block that always runs (except System.exit). **finalize:** deprecated GC hook. 

#### **Exceptions** 

- **Checked** (extends Exception, not RuntimeException): the compiler forces handling, e.g. IOException, SQLException. 

- **Unchecked** (RuntimeException): programming errors, e.g. NullPointerException, IllegalArgumentException. 

- **Error:** JVM problems such as OutOfMemoryError and StackOverflowError. Don't catch these. 

<mark>try (BuferedReader br = Files.newBuf</mark> f <mark>eredReader(Path.of("data.csv"))) {   // auto-closed</mark> f <mark>return br.lines().count(); } catch (IOException e) { throw new UncheckedIOException("Failed to read fle", e);   // wrap, keep cause</mark> i <mark>} public class LoanNotFoundException extends RuntimeException { public LoanNotFoundException(Long id) { super("Loan not found: " + id); } }</mark> 

**Tip** Best practice: don't swallow exceptions, don't catch Exception broadly, preserve the cause, and log once at the boundary. 

#### **Generics** 

- Generics give compile-time type safety. They are removed at runtime ( **type erasure** ), so new T() and List<int> are not allowed. 

- **Bounded:** <T extends Number>. **Wildcards:** List<?>, List<? extends Number> (read/producer), List<? super Integer> (write/consumer). Mnemonic **PECS** : Producer Extends, Consumer Super. 

<mark>public static <T extends Comparable<T>> T max(List<T> list) { T best = list.get(0); for (T t : list) if (t.compareTo(best) > 0) best = t; return best; }</mark> 

<mark>double sum(List<? extends Number> nums) { return nums.stream().mapToDouble(Number::doubleValue).sum(); }</mark> 

#### **Interfaces vs Abstract Classes** 

|**Interface**|**Abstract class**|
|---|---|
|Multiple implementation allowed|Single inheritance|
|No instance state (constants only)|Can have fields and constructors|
|default/static/private methods since Java 8/9|Any method type|
|Defines a capability (CAN-DO)|Shares a base (IS-A)|



#### **Enums and Records** 

<mark>public enum LoanStatus { PENDING("Pending approval"), ACTIVE("Disbursed"), CLOSED("Fully repaid"), WRITTEN_OFF("Written of");</mark> f <mark>private f</mark> i <mark>nal String label; LoanStatus(String label) { this.label = label; } public String label() { return label; } }</mark> 

<mark>public record LoanDto(Long id, String memberName, BigDecimal amount, LoanStatus status) { public LoanDto {   // compact constructor for validation if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive"); } }</mark> 

#### **Immutable Class Recipe** 

- Make the class final, all fields private final, and provide no setters. 

Page 15 of 63 

PKSF Full-stack Software Engineer — Study Material 

- Make defensive copies of mutable inputs and outputs (List.copyOf). 

- Or use a record (shallowly immutable). 

### **Collections Framework** 

|**Type**|**Implementation**|**Ordered**|**Sorted**|**Null**|**Get/Contains**|
|---|---|---|---|---|---|
|List|ArrayList|Insertion|No|Yes|O(1) index|
|List|LinkedList|Insertion|No|Yes|O(n) index|
|Set|HashSet|No|No|One null|O(1)|
|Set|LinkedHashSet|Insertion|No|One null|O(1)|
|Set|TreeSet|Sorted|Yes|No|O(log n)|
|Map|HashMap|No|No|One null key|O(1)|
|Map|LinkedHashMap|Insertion/access|No|One null key|O(1)|
|Map|TreeMap|Sorted keys|Yes|No null key|O(log n)|
|Map|ConcurrentHashMap|No|No|No nulls|O(1), thread-safe|



#### **HashMap Internals** 

- Backed by an array of buckets (Node[] table), default capacity 16 and load factor 0.75. 

- index = (n - 1) & (hash ^ (hash >>> 16)). 

- A collision creates a linked list in the bucket. A list longer than 8 (when the table is at least 64) becomes a red-black tree. 

- When size > capacity × load factor, the table doubles and entries are redistributed. 

- **Mutable keys are dangerous:** if a key's hashCode changes after insertion, the entry can't be found. 

#### **LRU Cache with LinkedHashMap** 

<mark>class LruCache<K, V> extends LinkedHashMap<K, V> { private f</mark> i <mark>nal int capacity; LruCache(int capacity) { super(16, 0.75f, true); this.capacity = capacity; } // accessOrder = true @Override protected boolean removeEldestEntry(Map.Entry<K, V> e) { return size() > capacity; } }</mark> 

#### **Comparable vs Comparator** 

<mark>record Loan(String member, BigDecimal amount, LocalDate date) implements Comparable<Loan> { public int compareTo(Loan o) { return date.compareTo(o.date); }   // natural order } loans.sort(Comparator.comparing(Loan::amount).reversed().thenComparing(Loan::member));</mark> 

- **Fail-fast iterators** (ArrayList, HashMap) throw ConcurrentModificationException if the collection is modified during iteration. Use iterator.remove() or removeIf(). 

- **Fail-safe** (ConcurrentHashMap, CopyOnWriteArrayList) iterate over a snapshot or weakly consistent view. 

### **Functional Java and Streams** 

- **Functional interface:** an interface with exactly one abstract method. Built-ins: Function<T,R>, Predicate<T>, Supplier<T>, Consumer<T>, BiFunction, UnaryOperator. 

- **Method references:** Integer::parseInt (static), String::toUpperCase (instance of arbitrary object), System.out::println (instance), ArrayList::new (constructor). 

- **Streams are lazy:** intermediate ops (filter, map) run only when a terminal op (collect, forEach, reduce) is called. A stream can be consumed only once. 

<mark>record Txn(String po, String type, BigDecimal amount, LocalDate date) {}</mark> 

Page 16 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>// Total disbursement per PO Map<String, BigDecimal> byPo = txns.stream() .flter(t -> t.type().equals("DISBURSE"))</mark> i <mark>.collect(Collectors.groupingBy(Txn::po, Collectors.reducing(BigDecimal.ZERO, Txn::amount, BigDecimal::add)));</mark> 

<mark>// Top 3 largest transactions List<Txn> top3 = txns.stream().sorted(Comparator.comparing(Txn::amount).reversed()).limit(3).toList(); // Partition: large vs small Map<Boolean, List<Txn>> parts = txns.stream() .collect(Collectors.partitioningBy(t -> t.amount().compareTo(new BigDecimal("50000")) > 0)); // fatMap: all words from lines</mark> l <mark>List<String> words = lines.stream().f</mark> l <mark>atMap(l -> Arrays.stream(l.split("\\s+"))).distinct().toList(); // Second highest number Optional<Integer> second = nums.stream().distinct().sorted(Comparator.reverseOrder()).skip(1).fndFirst();</mark> i <mark>// Duplicates in a list Set<Integer> seen = new HashSet<>(); Set<Integer> dups = nums.stream().flter(n -> !seen.add(n)).collect(Collectors.toSet());</mark> i 

#### **Optional** 

- Use Optional as a **return type** for "may be absent". Don't use it for fields or parameters. 

- Prefer orElseThrow, map, orElse and ifPresent over get(). 

<mark>Loan loan = loanRepository.fndById(id).orElseThrow(() -> new LoanNotFoundException(id));</mark> i <mark>String name = memberRepo.fndById(id).map(Member::name).orElse("Unknown");</mark> i 

### **Concurrency and Multithreading** 

- **Thread states:** NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED. 

- **Runnable** returns nothing. **Callable** returns a value and can throw a checked exception. 

- **synchronized:** mutual exclusion plus visibility. **volatile:** visibility and ordering only, not atomicity (count++ is still unsafe). 

- **happens-before:** unlocking a monitor happens-before a later lock of it, and a volatile write happens-before a later read. 

<mark>ExecutorService pool = Executors.newFixedThreadPool(4); try { List<Future<BigDecimal>> futures = poIds.stream() .map(id -> pool.submit(() -> reportService.calculateOutstanding(id))).toList(); for (Future<BigDecimal> f : futures) total = total.add(f.get()); } fnally {</mark> i <mark>pool.shutdown(); } CompletableFuture<Member> m = CompletableFuture.supplyAsync(() -> memberClient.get(id)); CompletableFuture<List<Loan>> l = CompletableFuture.supplyAsync(() -> loanClient.byMember(id)); MemberProfle prof</mark> i <mark>le = m.thenCombine(l, MemberProf</mark> i <mark>le::new).join();</mark> i 

<mark>AtomicLong counter = new AtomicLong();   // lock-free via CAS counter.incrementAndGet();</mark> 

- **Thread-safe counter options:** synchronized method, AtomicInteger/LongAdder, ReentrantLock. 

- **wait/notify vs sleep:** wait() releases the monitor and must be called in synchronized code. sleep() keeps the lock. 

- **Virtual threads (Java 21):** cheap, JVM-managed threads, good for blocking I/O. Enable in Spring Boot 3.2+ with spring.threads.virtual.enabled=true. 

Page 17 of 63 

PKSF Full-stack Software Engineer — Study Material 

#### **Producer–Consumer** 

<mark>BlockingQueue<String> q = new LinkedBlockingQueue<>(100);</mark> Runnable producer = () -> { for (int i = 0; i < 10; i++) { try { q.put("job-" + i); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } } }; Runnable consumer = () -> { while (true) { try { process(q.take()); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; } } }; 

### **JVM Internals** 

- **Heap:** objects (Young gen: Eden + Survivors; Old gen). **Stack:** one per thread; frames hold local variables and references. **Metaspace:** class metadata (native memory, replaced PermGen in Java 8). 

- **StackOverflowError:** recursion too deep. **OutOfMemoryError:** heap/metaspace exhausted or leak. 

- **GC:** objects unreachable from GC roots (stack refs, static fields) are collected. Minor GC cleans the young gen and major/full GC cleans the old gen. G1 is the default since Java 9; ZGC offers very low pauses. 

- **Class loading:** Bootstrap → Platform → Application, using the parent-delegation model. 

- **Tools:** jps, jstack (thread dump for deadlocks), jmap/heap dump, VisualVM, Java Flight Recorder. Tune with -Xms, -Xmx, -XX:+UseG1GC. 

### **Modern Java (17/21)** 

<mark>var list = new ArrayList<String>();                       // local type inference String json = """ {"po": "ABC", "amount": 5000} """;                                                  // text block String label = switch (status) {                          // switch expression case PENDING -> "Waiting"; case ACTIVE, CLOSED -> "Processed"; default -> "Other"; };</mark> 

<mark>if (obj instanceof Loan loan && loan.amount().signum() > 0) { /* use loan */ }   // pattern matching</mark> 

<mark>sealed interface Shape permits Circle, Square {} record Circle(double r) implements Shape {} record Square(double s) implements Shape {} double area(Shape s) { return switch (s) {                                   // record patterns, Java 21 case Circle(double r) -> Math.PI * r * r; case Square(double side) -> side * side; }; }</mark> 

### **Build Tools — Maven** 

- **Lifecycle phases:** validate → compile → test → package → verify → install → deploy. 

- **Scopes:** compile (default), provided (container supplies), runtime (e.g. JDBC driver), test. 

- **Commands:** mvn clean package, mvn test, mvn spring-boot:run, mvn dependency:tree, mvn -DskipTests package. 

- **Multi-module:** a parent POM with <modules> and <dependencyManagement> for consistent versions. 

### **Phase 2 — Interview Questions** 

##### **Q. Why is String immutable?** 

**A.** For security (paths, URLs, class names can't be changed), thread safety, safe use as HashMap keys with cached hashCode, and string-pool sharing. 

Page 18 of 63 

PKSF Full-stack Software Engineer — Study Material 

##### **Q. Difference between == and equals()?** 

- **A.** == compares references (or primitive values). equals() compares logical content as defined by the class. 

##### **Q. What happens if you put a key with an overridden equals but not hashCode into a HashMap?** 

**A.** Equal keys may produce different hash codes, land in different buckets and create duplicates. get() with an equal key may return null. 

##### **Q. ArrayList vs LinkedList?** 

**A.** ArrayList is backed by a dynamic array: O(1) random access and amortized O(1) append, with costly middle inserts. LinkedList is doubly linked: O(1) insert/remove at the ends but O(n) access and poor cache locality. ArrayList is preferred almost always. 

##### **Q. Checked vs unchecked exceptions — which should a Spring service throw?** 

**A.** Usually unchecked (custom RuntimeExceptions). They don't pollute signatures, and by default 

- @Transactional rolls back only on unchecked exceptions. 

##### **Q. What is the difference between map and flatMap?** 

**A.** map transforms each element one-to-one. flatMap maps each element to a stream and flattens the results into one stream. 

##### **Q. volatile vs synchronized?** 

**A.** volatile guarantees visibility and ordering of a single variable but not compound atomicity. synchronized gives mutual exclusion and visibility for a block. 

##### **Q. Explain the Java memory model areas.** 

**A.** Heap stores objects and is shared by threads. Each thread has its own stack of frames with locals. Metaspace stores class metadata. There are also the PC register and native method stack. 

##### **Q. What is a functional interface? Can it have default methods?** 

- **A.** An interface with exactly one abstract method. Yes, it can have any number of default and static methods. 

Page 19 of 63 

PKSF Full-stack Software Engineer — Study Material 

**Phase 3 — Spring and Spring Boot** 

### **Spring Core** 

- **IoC (Inversion of Control):** the container creates and wires objects instead of your code doing it. **DI** is how: dependencies are supplied through constructors, setters or fields. 

- **Prefer constructor injection:** it allows final fields, makes dependencies explicit, simplifies testing and fails fast. With a single constructor, @Autowired is optional. 

- **ApplicationContext** is the container (it extends BeanFactory and adds events, i18n and AOP integration). 

<mark>@Service</mark> 

<mark>public class RepaymentService { private f</mark> i <mark>nal LoanRepository loanRepo; private f</mark> i <mark>nal ApplicationEventPublisher events; public RepaymentService(LoanRepository loanRepo, ApplicationEventPublisher events) { this.loanRepo = loanRepo; this.events = events; } }</mark> 

#### **Bean Scopes and Lifecycle** 

|**Scope**|**Meaning**|
|---|---|
|singleton (default)|One instance per container|
|prototype|New instance per injection/request for the bean|
|request|One per HTTP request|
|session|One per HTTP session|
|application|One per ServletContext|



- **Lifecycle:** instantiate → inject dependencies → Aware callbacks → BeanPostProcessor before → @PostConstruct / afterPropertiesSet → BeanPostProcessor after (proxies created here) → ready → @PreDestroy on shutdown. 

- Singleton beans must be **stateless** or thread-safe because they are shared across requests. 

#### **Stereotypes and Configuration** 

- **@Component:** generic bean. **@Service:** business logic. **@Repository:** DAO; also translates persistence exceptions into DataAccessException. **@Controller/@RestController:** web layer. 

- **@Configuration + @Bean:** define beans for third-party classes. **@Qualifier/@Primary:** resolve multiple candidates. **@Profile, @ConditionalOnProperty:** conditional beans. 

<mark>@Confguration</mark> i <mark>public class AppConfg {</mark> i <mark>@Bean public RestClient smsClient(@Value("${sms.base-url}") String url) { return RestClient.builder().baseUrl(url).build(); } }</mark> 

#### **Spring AOP** 

- **Aspect:** cross-cutting module (logging, security, transactions). **Advice:** @Before, @After, @AfterReturning, 

   - @AfterThrowing, @Around. **Pointcut:** where to apply. **Join point:** a method execution. 

- Spring AOP is **proxy-based** : JDK dynamic proxies for interfaces, CGLIB subclasses otherwise. Internal calls (this.method()) bypass the proxy, which is why @Transactional on self-invoked methods does not work. 

<mark>@Aspect @Component public class TimingAspect {</mark> 

Page 20 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>private static fnal Logger log = LoggerFactory.getLogger(TimingAspect.class);</mark> i <mark>@Around("execution(* bd.org.pksf..service..*(..))") public Object time(ProceedingJoinPoint pjp) throws Throwable { long start = System.nanoTime(); try { return pjp.proceed(); } f</mark> i <mark>nally { log.info("{} took {} ms", pjp.getSignature(), (System.nanoTime() - start) / 1_000_000); } } }</mark> 

### **Spring Boot Essentials** 

- **@SpringBootApplication** = @Configuration + @EnableAutoConfiguration + @ComponentScan (current package and sub-packages). 

- **Auto-configuration:** classes listed in META-INF/spring/...AutoConfiguration.imports are applied conditionally (@ConditionalOnClass, @ConditionalOnMissingBean). Example: if a DataSource class and URL are present, a HikariCP DataSource is configured. Debug with --debug to see the conditions report. 

- **Starters:** curated dependency bundles (spring-boot-starter-web, -data-jpa, -security, -validation, -actuator, -data-redis, -oauth2-resource-server). 

- **Embedded server:** Tomcat by default, so the app runs as an executable fat jar. 

#### **Configuration and Profiles** 

<mark># application.yml spring: application: name: loan-service datasource: url: jdbc:postgresql://localhost:5432/pksf username: ${DB_USER:pksf} password: ${DB_PASSWORD} hikari: maximum-pool-size: 20 jpa: hibernate: ddl-auto: validate      # never 'update' in production; use Flyway open-in-view: false profles:</mark> i <mark>active: dev app: loan: max-amount: 500000 default-tenure-months: 12 --spring: conf</mark> i <mark>g: activate: on-profle: prod</mark> i <mark>logging: level: root: WARN</mark> 

@ConfigurationProperties(prefix = "app.loan") <mark>public record LoanProperties(BigDecimal maxAmount, int defaultTenureMonths) {} // enable with @EnableConfgurationProperties(LoanProperties.class) or @Conf</mark> i <mark>gurationPropertiesScan</mark> i 

- **Property precedence (high to low, simplified):** command-line args → environment variables → profilespecific files → application.yml → defaults. 

#### **Actuator and Logging** 

- Endpoints: /actuator/health (with liveness/readiness groups for K8s), /info, /metrics, /prometheus. Expose only what you need and secure the rest. 

Page 21 of 63 

PKSF Full-stack Software Engineer — Study Material 

- **Logging:** SLF4J API + Logback. Use parameterized logs: log.info("Loan {} approved", id). Add a correlation ID through MDC in a filter so you can trace requests across services. Never log passwords, tokens or full NID numbers. 

#### **Project Structure** 

<mark>src/main/java/bd/org/pksf/loan</mark> 

- <mark>├── LoanServiceApplication.java ├── conf</mark> i <mark>g/          (security, OpenAPI, cache confg)</mark> i <mark>├── loan/            (feature package) │ ├── LoanController.java</mark> 

- <mark>│ ├── LoanService.java</mark> 

- <mark>│ ├── LoanRepository.java</mark> 

- <mark>│ ├── Loan.java (entity)</mark> 

- <mark>│ └── dto/ (LoanRequest, LoanResponse) ├── member/ └── common/          (exceptions, audit, utils)</mark> 

<mark>src/main/resources/application.yml, db/migration/V1__init.sql</mark> 

### **Building REST APIs** 

<mark>@RestController @RequestMapping("/api/v1/loans") @RequiredArgsConstructor public class LoanController { private f</mark> i <mark>nal LoanService loanService;</mark> 

<mark>@GetMapping public Page<LoanResponse> list(@RequestParam(required = false) LoanStatus status, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { return loanService.search(status, pageable);</mark> 

<mark>} @GetMapping("/{id}") public LoanResponse get(@PathVariable Long id) { return loanService.get(id); }</mark> 

<mark>@PostMapping @PreAuthorize("hasRole('PO_OFFICER')") public ResponseEntity<LoanResponse> create(@Valid @RequestBody LoanRequest req, UriComponentsBuilder uri) { LoanResponse created = loanService.create(req);</mark> 

<mark>return ResponseEntity.created(uri.path("/api/v1/loans/{id}").build(created.id())).body(created);</mark> 

<mark>}</mark> 

<mark>@PostMapping("/{id}/repayments") public RepaymentResponse repay(@PathVariable Long id, @Valid @RequestBody RepaymentRequest req) { return loanService.repay(id, req);</mark> 

<mark>}</mark> 

<mark>@DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { loanService.delete(id); }</mark> 

<mark>}</mark> 

#### **DTOs and Validation** 

- **Never expose entities directly.** Doing so leaks internal fields, causes lazy-loading errors and couples the API to the schema. Map entities to DTOs manually or with MapStruct. 

<mark>public record LoanRequest(</mark> 

<mark>@NotNull Long memberId,</mark> 

- <mark>@NotNull @DecimalMin("1000.00") @DecimalMax("500000.00") @Digits(integer = 10, fraction = 2) BigDecimal amount, @Min(1) @Max(60) int tenureMonths,</mark> 

<mark>@NotBlank @Size(max = 200) String purpose) {}</mark> 

<mark>@Mapper(componentModel = "spring")</mark> 

Page 22 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>public interface LoanMapper { @Mapping(target = "memberName", source = "member.name") LoanResponse toResponse(Loan loan); }</mark> 

#### **Global Exception Handling** 

<mark>@RestControllerAdvice public class GlobalExceptionHandler { @ExceptionHandler(LoanNotFoundException.class) public ProblemDetail notFound(LoanNotFoundException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()); } @ExceptionHandler(MethodArgumentNotValidException.class) public ProblemDetail validation(MethodArgumentNotValidException ex) { ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed"); Map<String, String> errors = new HashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage())); pd.setProperty("errors", errors); return pd; } @ExceptionHandler(ObjectOptimisticLockingFailureException.class) public ProblemDetail confict() {</mark> l <mark>return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Record was modifed by another user. Reload and retry.");</mark> i <mark>} }</mark> 

#### **OpenAPI, Files and HTTP Clients** 

- Add springdoc-openapi-starter-webmvc-ui to get Swagger UI at /swagger-ui.html. Annotate with @Operation and @Tag. 

- **Upload:** @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE) upload(@RequestParam MultipartFile file). Validate size, type and filename. Configure spring.servlet.multipart.max-file-size. 

- **Download large files:** stream with StreamingResponseBody or Resource. Set Content-Disposition. 

- **RestClient (Boot 3.2+):** synchronous fluent client. **WebClient:** reactive/non-blocking. **OpenFeign:** declarative interface clients for microservices. 

<mark>MemberDto m = restClient.get().uri("/members/{id}", id) .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) .retrieve().body(MemberDto.class);</mark> 

### **Spring Data JPA and Hibernate** 

#### **Entity Mapping** 

<mark>@Entity @Table(name = "loans", indexes = @Index(name = "idx_loans_member", columnList = "member_id")) @EntityListeners(AuditingEntityListener.class) public class Loan { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "member_id") private Member member; @Column(nullable = false, precision = 14, scale = 2) private BigDecimal amount; @Enumerated(EnumType.STRING)          // never ORDINAL — reordering breaks data private LoanStatus status;</mark> 

Page 23 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true) private List<Installment> installments = new ArrayList<>();</mark> 

<mark>@Version private Long version;                 // optimistic locking</mark> 

<mark>@CreatedDate private Instant createdAt; @CreatedBy   private String createdBy; public void addInstallment(Installment i) { installments.add(i); i.setLoan(this); }  // keep both sides in sync }</mark> • **ID strategies:** IDENTITY (DB auto-increment; disables JDBC insert batching), SEQUENCE (best for Postgres; supports batching with allocationSize), UUID. 

- **Owning side:** the side with the foreign key (@ManyToOne / @JoinColumn). mappedBy marks the inverse side. 

- **Cascade:** propagates persist/merge/remove to children. **orphanRemoval:** deletes a child removed from the collection. 

#### **N+1 Problem and Fetching** 

- **N+1:** one query loads N loans, then N extra queries load each loan's member lazily. 

- **Fixes:** JOIN FETCH in JPQL, @EntityGraph, DTO projections, or @BatchSize / hibernate.default_batch_fetch_size. 

- **Default fetch:** @ManyToOne/@OneToOne are EAGER by default, so set them to LAZY. @OneToMany/@ManyToMany are LAZY. 

- **LazyInitializationException:** a lazy association is accessed after the session closes. Fetch what you need in the query instead of enabling open-in-view. 

<mark>public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecifcationExecutor<Loan> {</mark> i <mark>List<Loan> f</mark> i <mark>ndByStatusAndAmountGreaterThan(LoanStatus status, BigDecimal amount);   // derived query</mark> 

<mark>@Query("select l from Loan l join fetch l.member where l.status = :status") List<Loan> f</mark> i <mark>ndWithMember(@Param("status") LoanStatus status);                       // fxes N+1</mark> i 

<mark>@EntityGraph(attributePaths = {"member", "installments"}) Optional<Loan> fndDetailedById(Long id);</mark> i 

<mark>@Query("select new bd.org.pksf.loan.dto.PoSummary(m.po.id, sum(l.amount), count(l)) " + "from Loan l join l.member m where l.status = 'ACTIVE' group by m.po.id") List<PoSummary> activeSummaryByPo();                                                 // DTO projection</mark> 

<mark>@Query(value = "select * from loans where created_at >= now() - interval '7 days'", nativeQuery = true) List<Loan> lastWeek();</mark> 

<mark>@Modifying @Query("update Loan l set l.status = 'CLOSED' where l.id = :id") int close(@Param("id") Long id); }</mark> 

#### **Transactions** 

- **@Transactional** is applied through a proxy. It works only on public methods called from outside the bean. 

- **Default rollback:** RuntimeException and Error only. Use rollbackFor = Exception.class to include checked exceptions. 

- Use readOnly = true for queries; it skips dirty checking and can route to replicas. 

|**Propagation**|**Behavior**|
|---|---|
|REQUIRED (default)|Join existing or create new|



Page 24 of 63 

PKSF Full-stack Software Engineer — Study Material 

|**Propagation**|**Behavior**|
|---|---|
|REQUIRES_NEW|Suspend current, always start new (e.g. audit log that must persist)|
|SUPPORTS|Join if exists, else run non-transactionally|
|MANDATORY|Must have an existing transaction, else exception|
|NEVER / NOT_SUPPORTED|Must not run in a transaction / suspend it|
|NESTED|Savepoint within current transaction|



<mark>@Transactional public RepaymentResponse repay(Long loanId, RepaymentRequest req) { Loan loan = loanRepo.fndById(loanId).orElseThrow(() -> new LoanNotFoundException(loanId));</mark> i <mark>if (loan.getStatus() != LoanStatus.ACTIVE) throw new BusinessException("Loan is not active"); loan.applyPayment(req.amount());              // dirty checking saves changes at commit Repayment r = repaymentRepo.save(new Repayment(loan, req.amount(), LocalDate.now())); events.publishEvent(new RepaymentRecorded(loan.getId(), req.amount())); return mapper.toResponse(r); }</mark> 

#### **Locking** 

- **Optimistic (@Version):** no DB lock. An update checks the version, and a mismatch throws 

   - OptimisticLockException (return 409). Best when conflicts are rare. 

- **Pessimistic:** @Lock(LockModeType.PESSIMISTIC_WRITE) issues SELECT ... FOR UPDATE. Use it for hot rows such as balance updates. Keep transactions short. 

#### **Persistence Context** 

- **Entity states:** Transient (new), Managed (in the persistence context), Detached (context closed), Removed. 

- **First-level cache:** the same entity ID returns the same instance within a transaction. 

- **Dirty checking:** changes to managed entities are flushed automatically at commit, so no save() is needed. 

- **save() vs saveAndFlush():** flush forces SQL immediately. 

#### **Migrations and Batching** 

<mark>-- src/main/resources/db/migration/V1__create_loans.sql  (Flyway) CREATE TABLE loans ( id          BIGSERIAL PRIMARY KEY, member_id   BIGINT NOT NULL REFERENCES members(id), amount      NUMERIC(14,2) NOT NULL CHECK (amount > 0), status      VARCHAR(20) NOT NULL, version     BIGINT NOT NULL DEFAULT 0, created_at  TIMESTAMPTZ NOT NULL DEFAULT now(), created_by  VARCHAR(100) );</mark> 

<mark>CREATE INDEX idx_loans_member ON loans(member_id);</mark> 

- Flyway runs versioned scripts in order (V1__, V2__) and records them in flyway_schema_history. Never edit an applied migration; add a new one. 

- **Batch inserts:** set spring.jpa.properties.hibernate.jdbc.batch_size=50 and order_inserts=true, use SEQUENCE IDs, or use JdbcTemplate.batchUpdate for bulk imports from POs. 

### **Other Spring Capabilities** 

<mark>@EnableScheduling @EnableAsync @EnableCaching @Confguration class AsyncConf</mark> i <mark>g {}</mark> i 

<mark>@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Dhaka")       // 1 AM daily @SchedulerLock(name = "overdueJob")                          // ShedLock: run once in a cluster public void markOverdueInstallments() { installmentService.markOverdue(LocalDate.now()); }</mark> 

Page 25 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>@Async</mark> 

<mark>public CompletableFuture<byte[]> generateReport(Long poId) { return CompletableFuture.completedFuture(excel.build(poId)); }</mark> 

<mark>@Cacheable(value = "po", key = "#id") public PoDto getPo(Long id) { return mapper.toDto(poRepo.fndById(id).orElseThrow()); }</mark> i 

<mark>@CacheEvict(value = "po", key = "#id") public void updatePo(Long id, PoUpdate req) { /* ... */ }</mark> 

- **Reports:** Apache POI (XSSFWorkbook / SXSSFWorkbook for streaming large Excel files), JasperReports or OpenPDF for PDF. Generate heavy reports asynchronously and notify the user when ready. 

- **Spring Batch:** Job → Steps → chunk-oriented ItemReader/ItemProcessor/ItemWriter, with restartability. Good for monthly PO data imports. 

### **Phase 3 — Interview Questions** 

##### **Q. What does @SpringBootApplication do?** 

**A.** It combines @Configuration, @EnableAutoConfiguration and @ComponentScan. It scans the package of the main class and below and applies conditional auto-configurations. 

##### **Q. How does auto-configuration decide what to configure?** 

**A.** Through @Conditional annotations, such as @ConditionalOnClass (library on classpath), @ConditionalOnMissingBean (you haven't defined your own) and @ConditionalOnProperty. User-defined beans take precedence. 

##### **Q. Why prefer constructor injection?** 

**A.** It gives immutability (final fields), explicit required dependencies, easier unit tests without Spring, detection of circular dependencies at startup, and no nulls. 

##### **Q. Why might @Transactional not work?** 

**A.** The method is private or final, it is called from within the same class (self-invocation bypasses the proxy), a checked exception is thrown without rollbackFor, the exception is caught and swallowed, or the bean isn't Spring-managed. 

##### **Q. What is the N+1 problem and how do you fix it?** 

- **A.** Loading a list triggers one extra query per element for a lazy association. Fix with JOIN FETCH, 

@EntityGraph, DTO projections or batch fetching. 

##### **Q. Difference between @Component, @Service and @Repository?** 

- **A.** All register beans. @Service is semantic for business logic. @Repository adds persistence exception translation. 

##### **Q. What is the difference between PUT and PATCH?** 

- **A.** PUT replaces the whole resource and is idempotent. PATCH partially updates and is not necessarily idempotent. 

##### **Q. How do you handle concurrent updates to the same loan?** 

**A.** Use optimistic locking with @Version (return 409 Conflict) or pessimistic locking (SELECT FOR UPDATE) for high-contention rows, plus short transactions. 

##### **Q. How would you set the active profile in production?** 

**A.** Use the environment variable SPRING_PROFILES_ACTIVE=prod or the argument -- spring.profiles.active=prod. Secrets come from env vars or a secret store, never from committed files. 

##### **Q. What is open-in-view and why disable it?** 

**A.** It keeps the Hibernate session open during view rendering, which allows lazy loading in controllers. That hides N+1 issues and holds DB connections longer. Disable it and fetch explicitly. 

Page 26 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 4 — Databases** 

### **SQL Mastery** 

##### Sample schema used below: 

<mark>pos(id, name, district) members(id, po_id, name, nid, phone, joined_on) loans(id, member_id, amount, status, disbursed_on) installments(id, loan_id, due_date, amount_due, amount_paid, paid_on) employees(id, name, dept_id, salary, manager_id)</mark> 

#### **Joins and Aggregation** 

<mark>-- Members with their PO name (INNER JOIN) SELECT m.name, p.name AS po_name FROM members m JOIN pos p ON p.id = m.po_id;</mark> 

<mark>-- POs with zero members (LEFT JOIN + IS NULL) SELECT p.* FROM pos p LEFT JOIN members m ON m.po_id = p.id WHERE m.id IS NULL; -- Total active loan amount per PO, only POs above 1 crore SELECT p.name, SUM(l.amount) AS total, COUNT(*) AS loans FROM loans l JOIN members m ON m.id = l.member_id JOIN pos p ON p.id = m.po_id WHERE l.status = 'ACTIVE' GROUP BY p.name HAVING SUM(l.amount) > 10000000 ORDER BY total DESC;</mark> 

<mark>-- Conditional aggregation SELECT p.name, COUNT(*) FILTER (WHERE l.status = 'ACTIVE') AS active, COUNT(*) FILTER (WHERE l.status = 'CLOSED') AS closed FROM loans l JOIN members m ON m.id = l.member_id JOIN pos p ON p.id = m.po_id GROUP BY p.name;</mark> 

<mark>-- Employee and manager (self join) SELECT e.name AS employee, m.name AS manager FROM employees e LEFT JOIN employees m ON m.id = e.manager_id;</mark> 

- **WHERE vs HAVING:** WHERE filters rows before grouping, and HAVING filters groups after aggregation. 

- **Logical order:** FROM → JOIN → WHERE → GROUP BY → HAVING → SELECT → DISTINCT → ORDER BY → LIMIT. 

#### **Subqueries, CTEs and Window Functions** 

<mark>-- Second / Nth highest salary SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) FROM employees); SELECT DISTINCT salary FROM employees ORDER BY salary DESC OFFSET 2 LIMIT 1;   -- 3rd highest SELECT salary FROM (SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) r FROM employees) t WHERE r = 3;</mark> 

<mark>-- Highest paid employee per department (top-N per group) SELECT * FROM ( SELECT e.*, ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) rn FROM employees e ) t WHERE rn = 1;</mark> 

<mark>-- Employees earning above their department average (correlated subquery) SELECT * FROM employees e WHERE salary > (SELECT AVG(salary) FROM employees WHERE dept_id = e.dept_id);</mark> 

<mark>-- Find duplicate NIDs SELECT nid, COUNT(*) FROM members GROUP BY nid HAVING COUNT(*) > 1;</mark> 

<mark>-- Delete duplicates keeping lowest id DELETE FROM members a USING members b WHERE a.nid = b.nid AND a.id > b.id;</mark> 

Page 27 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>-- Monthly disbursement with running total and month-over-month change WITH monthly AS ( SELECT date_trunc('month', disbursed_on) AS month, SUM(amount) AS total FROM loans GROUP BY 1 ) SELECT month, total, SUM(total) OVER (ORDER BY month) AS running_total, total - LAG(total) OVER (ORDER BY month) AS change FROM monthly ORDER BY month;</mark> 

<mark>-- Overdue members: any installment due before today and not fully paid SELECT DISTINCT m.id, m.name FROM members m JOIN loans l ON l.member_id = m.id JOIN installments i ON i.loan_id = l.id WHERE i.due_date < CURRENT_DATE AND COALESCE(i.amount_paid, 0) < i.amount_due; -- Recursive CTE: org hierarchy under manager 1 WITH RECURSIVE tree AS ( SELECT id, name, manager_id, 1 AS lvl FROM employees WHERE id = 1 UNION ALL SELECT e.id, e.name, e.manager_id, t.lvl + 1 FROM employees e JOIN tree t ON e.manager_id = t.id )</mark> 

<mark>SELECT * FROM tree;</mark> 

- **ROW_NUMBER:** unique sequence (1,2,3,4). **RANK:** gaps on ties (1,2,2,4). **DENSE_RANK:** no gaps (1,2,2,3). 

- **UNION** removes duplicates and **UNION ALL** keeps them (faster). INTERSECT and EXCEPT are also available. 

- **DELETE vs TRUNCATE vs DROP:** DELETE removes rows (can use WHERE, fires triggers). TRUNCATE removes all rows quickly and resets storage. DROP removes the table. 

#### **DDL, Upsert, Views, Functions** 

<mark>INSERT INTO members (nid, name, po_id) VALUES ('1990123456789', 'Rahima', 5) ON CONFLICT (nid) DO UPDATE SET name = EXCLUDED.name;          -- upsert</mark> 

<mark>UPDATE loans l SET status = 'CLOSED' FROM (SELECT loan_id FROM installments GROUP BY loan_id HAVING SUM(amount_due - COALESCE(amount_paid,0)) = 0) p WHERE l.id = p.loan_id;</mark> 

<mark>CREATE MATERIALIZED VIEW po_portfolio AS</mark> SELECT m.po_id, SUM(l.amount) total, COUNT(*) loans FROM loans l JOIN members m ON m.id = l.member_id GROUP BY m.po_id; <mark>CREATE UNIQUE INDEX ON po_portfolio(po_id); REFRESH MATERIALIZED VIEW CONCURRENTLY po_portfolio;            -- needs unique index</mark> 

<mark>CREATE OR REPLACE FUNCTION audit_loan() RETURNS trigger AS $$</mark> 

<mark>BEGIN</mark> 

<mark>INSERT INTO loan_audit(loan_id, old_status, new_status, changed_at) VALUES (OLD.id, OLD.status, NEW.status, now()); RETURN NEW; END $$ LANGUAGE plpgsql;</mark> 

<mark>CREATE TRIGGER trg_loan_audit AFTER UPDATE OF status ON loans FOR EACH ROW EXECUTE FUNCTION audit_loan();</mark> 

### **PostgreSQL Schema Design** 

|**Need**|**Use**|**Avoid**|
|---|---|---|
|Money|NUMERIC(14,2)|float, double, money type|
|Timestamps|TIMESTAMPTZ (stored as UTC)|TIMESTAMP without tz for events|
|IDs|BIGINT identity / BIGSERIAL; UUID for<br>public/distributed IDs|INT for big tables|
|Flexible attributes|JSONB with GIN index|Many nullable columns or EAV|
|Status|VARCHAR + CHECK or lookup table|Magic integers|



Page 28 of 63 

PKSF Full-stack Software Engineer — Study Material 

|**Need**|**Use**|**Avoid**|
|---|---|---|
|Text|TEXT / VARCHAR(n)|CHAR(n)|



#### **Example Finance Schema** 

|CREATE TABLE pos (<br>id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,<br>code VARCHAR(20) NOT NULL UNIQUE,<br>name TEXT NOT NULL,|
|---|
|district VARCHAR(50) NOT NULL,<br>created_at TIMESTAMPTZ NOT NULL DEFAULT now()<br>|
|);|
|CREATE TABLE members (<br>id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,<br>po_id BIGINT NOT NULL REFERENCES pos(id),<br>nid VARCHAR(17) NOT NULL UNIQUE,<br>name TEXT NOT NULL,<br>phone VARCHAR(14),<br>deleted_at TIMESTAMPTZ                      -- soft delete|
|);|
|CREATE TABLE loans (|
|id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,<br>member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE RESTRICT,<br>principal NUMERIC(14,2) NOT NULL CHECK (principal > 0),<br>service_charge_rate NUMERIC(5,2) NOT NULL,<br>status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','ACTIVE','CLOSED','WRITTEN_OFF')),<br>disbursed_on DATE,<br>version BIGINT NOT NULL DEFAULT 0,<br>created_at TIMESTAMPTZ NOT NULL DEFAULT now(),<br>created_by VARCHAR(100) NOT NULL|
|);|
|-- Append-only ledger: never UPDATE/DELETE, correct with reversing entries<br>CREATE TABLE ledger_entries (<br>id BIGINT GENERATED ALWAYS AS IDENTITY,|
|loan_id BIGINT NOT NULL REFERENCES loans(id),<br>entry_type VARCHAR(20) NOT NULL,            -- DISBURSE, REPAY, REVERSAL<br>amount NUMERIC(14,2) NOT NULL,<br>txn_date DATE NOT NULL,<br>created_at TIMESTAMPTZ NOT NULL DEFAULT now(),<br>PRIMARY KEY (id, txn_date)|
|) PARTITION BY RANGE (txn_date);|
|CREATE TABLE ledger_2026 PARTITION OF ledger_entries FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');|



- **ON DELETE options:** RESTRICT/NO ACTION (block), CASCADE (delete children), SET NULL. For financial data, prefer RESTRICT and soft delete. 

- **Partitioning:** split huge tables by date range. Queries that filter by date touch only the relevant partitions (partition pruning), and old partitions are easy to archive. 

### **PostgreSQL Indexing** 

- **B-tree** (default) supports =, <, >, BETWEEN, IN, ORDER BY and prefix LIKE 'abc%'. 

- **Composite index (a, b, c):** usable for filters on a, (a,b) and (a,b,c), but not b alone (leftmost-prefix rule). Put equality columns first, then range/sort columns. 

- **Partial index:** indexes only the rows that matter. 

- **Expression index:** indexes a computed value. 

- **Covering index (INCLUDE):** enables index-only scans. 

Page 29 of 63 

PKSF Full-stack Software Engineer — Study Material 

- **GIN:** JSONB, arrays, full-text search. **GiST:** geometry and ranges. **BRIN:** very large, naturally ordered tables (time series) with a tiny index. **Hash:** equality only. 

<mark>CREATE INDEX idx_inst_loan_due ON installments (loan_id, due_date); CREATE INDEX idx_loans_active ON loans (member_id) WHERE status = 'ACTIVE';           -- partial CREATE INDEX idx_members_lower_name ON members (LOWER(name));                          -- expression CREATE INDEX idx_loans_member_cover ON loans (member_id) INCLUDE (principal, status);  -- covering CREATE INDEX idx_forms_data ON survey_forms USING GIN (data jsonb_path_ops);           -- JSONB CREATE INDEX idx_ledger_date_brin ON ledger_entries USING BRIN (txn_date); CREATE INDEX CONCURRENTLY idx_members_phone ON members(phone);                         -- no table lock</mark> 

#### **When an Index Is Not Used** 

- A function is applied to the column (WHERE LOWER(name) = 'x' without an expression index; WHERE date(created_at) = ...). 

- A leading wildcard: LIKE '%rahim' (use pg_trgm with a GIN index). 

- Implicit type casts or comparing mismatched types. 

- Low selectivity (the planner prefers a seq scan when most rows match) or a small table. 

- Stale statistics. Run ANALYZE. 

- OR across different columns (consider UNION or separate indexes so a bitmap OR can be used). 

**Tip** Indexes speed up reads but slow down writes and take space. Find unused ones in pg_stat_user_indexes where idx_scan = 0. 

### **PostgreSQL Query Tuning** 

<mark>EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM installments WHERE loan_id = 42 AND due_date < CURRENT_DATE;</mark> 

- <mark>-- Example output to read: -- Index Scan using idx_inst_loan_due on installments  (cost=0.43..8.45 rows=3 width=48)</mark> 

<mark>--      (actual time=0.021..0.025 rows=3 loops=1)</mark> 

<mark>--   Index Cond: ((loan_id = 42) AND (due_date < CURRENT_DATE)) --   Bufers: shared hit=4</mark> f 

<mark>-- Planning Time: 0.1 ms   Execution Time: 0.05 ms</mark> 

- **cost:** the planner estimate (startup..total). **rows:** estimated vs actual; big differences mean stale stats. **loops:** times the node ran. **Buffers:** shared hit = cache, read = disk. 

- **Seq Scan:** reads the whole table. **Index Scan:** traverses the index, then fetches heap rows. **Index Only Scan:** all columns come from the index. **Bitmap Heap Scan:** collects matching pages, then reads them, which suits medium selectivity. 

- **Join algorithms:** Nested Loop (small outer, indexed inner), Hash Join (large unsorted equi-joins), Merge Join (both sides sorted). 

#### **MVCC and VACUUM** 

- **MVCC:** an UPDATE writes a new row version and marks the old one dead. Readers never block writers. 

- **VACUUM** reclaims dead tuples, and **autovacuum** does this automatically. **ANALYZE** updates planner statistics. VACUUM FULL rewrites the table and locks it. 

- Long-running transactions prevent cleanup and cause bloat. 

#### **Practical Tuning Checklist** 

- Enable **pg_stat_statements** and sort by total_exec_time to find the worst queries. 

- Set log_min_duration_statement = 500ms to log slow queries. 

- Avoid SELECT *; fetch only the columns you need. 

- Use **keyset pagination** instead of a large OFFSET (OFFSET 100000 scans and discards 100k rows). 

- Use EXISTS instead of IN with a large subquery, and NOT EXISTS instead of NOT IN (NOT IN breaks with NULLs). 

Page 30 of 63 

PKSF Full-stack Software Engineer — Study Material 

- Batch inserts and use COPY for bulk loads. 

- Keep transactions short and fix N+1 queries in the ORM. 

- **Connection pooling:** HikariCP pool size is roughly (cores × 2) + effective spindles. Usually 10–30 is enough. Use PgBouncer when many app instances connect. 

<mark>-- Keyset pagination SELECT id, name FROM members WHERE po_id = 5 AND id > :last_seen_id ORDER BY id LIMIT 50;</mark> 

<mark>-- Top slow queries SELECT query, calls, mean_exec_time, total_exec_time FROM pg_stat_statements ORDER BY total_exec_time DESC LIMIT 10;</mark> 

### **PostgreSQL Operations** 

- **Backup:** pg_dump -Fc pksf > pksf.dump; restore with pg_restore -d pksf pksf.dump. **PITR** = base backup + WAL archiving. 

- **Replication:** streaming replication to read replicas (asynchronous by default). Send reports to replicas. 

- **Roles:** an app user with only the needed privileges (no superuser), and a separate read-only reporting user. 

- **Row-Level Security:** ALTER TABLE members ENABLE ROW LEVEL SECURITY; CREATE POLICY po_isolation ON members USING (po_id = current_setting('app.po_id')::bigint); 

### **MongoDB** 

- **Document model:** JSON-like BSON documents in collections. Schema is flexible, and each document has an _id (ObjectId by default). 

- **Good fits at PKSF:** audit logs, activity events, dynamic survey forms, notifications, document metadata. 

- **Keep in PostgreSQL:** loans, ledgers and anything needing strict relational integrity and multi-row transactions. 

<mark>db.audit_logs.insertOne({ entity: "Loan", entityId: 42, action: "APPROVE", user: "ofcer1",</mark> fi <mark>at: new Date(), changes: { status: { from: "PENDING", to: "ACTIVE" } } });</mark> 

<mark>db.audit_logs.f</mark> i <mark>nd({ entity: "Loan", entityId: 42 }).sort({ at: -1 }).limit(20);</mark> 

<mark>db.members.updateOne({ _id: id }, { $set: { phone: "017..." }, $push: { tags: "verifed" }, $inc: { visits: 1 } });</mark> i 

<mark>db.audit_logs.createIndex({ entity: 1, entityId: 1, at: -1 });              // compound db.sessions.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 });    // TTL</mark> 

<mark>// Aggregation: actions per user in last 30 days db.audit_logs.aggregate([ { $match: { at: { $gte: new Date(Date.now() - 30*24*3600*1000) } } }, { $group: { _id: "$user", actions: { $sum: 1 } } }, { $sort: { actions: -1 } }, { $limit: 10 } ]);</mark> 

<mark>// $lookup = left outer join db.loans.aggregate([{ $lookup: { from: "members", localField: "memberId", foreignField: "_id", as: "member" } }, { $unwind: "$member" }]);</mark> 

#### **Embedding vs Referencing** 

- **Embed** when the data is read together, owned by the parent, and bounded in size (an address in a member document). 

- **Reference** when the data is unbounded (thousands of transactions), shared, or updated independently. 

- A document can be at most 16 MB. Model schemas around query patterns. 

<mark>@Document(collection = "audit_logs")</mark> public record AuditLog(@Id String id, String entity, Long entityId, String action, String user, Instant at, Map<String, Object> changes) {} 

Page 31 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>public interface AuditLogRepository extends MongoRepository<AuditLog, String> { List<AuditLog> fndByEntityAndEntityIdOrderByAtDesc(String entity, Long entityId);</mark> i <mark>}</mark> 

|**PostgreSQL (SQL)**|**MongoDB (NoSQL)**|
|---|---|
|Fixed schema, relations, joins|Flexible schema, embedded docs|
|Strong ACID across tables|ACID on single document; multi-doc transactions on replica sets|
|Vertical scaling + replicas|Horizontal sharding built in|
|Finance, ledgers, reporting|Logs, events, catalogs, dynamic forms|



### **Phase 4 — Interview Questions** 

##### **Q. How would you speed up a slow query?** 

**A.** Run EXPLAIN (ANALYZE, BUFFERS) and look for seq scans on large tables, row-estimate mismatches and expensive sorts or joins. Add or adjust indexes (composite, partial, covering), rewrite the query (no functions on indexed columns, keyset pagination, EXISTS), refresh statistics with ANALYZE, and check pg_stat_statements for the worst queries overall. 

##### **Q. What is a composite index and how does column order matter?** 

- **A.** It is an index on multiple columns, sorted by the first, then the second, and so on. It helps queries that filter on a leftmost prefix. Put equality-filtered columns first, then range or ORDER BY columns. 

##### **Q. Why use NUMERIC instead of FLOAT for money?** 

**A.** FLOAT is binary floating point and can't represent decimals like 0.1 exactly, so rounding errors accumulate. NUMERIC is exact decimal. 

##### **Q. What is MVCC?** 

**A.** Multi-Version Concurrency Control. Each transaction sees a snapshot. Updates create new row versions, so readers and writers don't block each other. VACUUM cleans up old versions. 

##### **Q. Clustered vs non-clustered index?** 

**A.** A clustered index defines the physical row order (one per table, e.g. in MySQL InnoDB or SQL Server). PostgreSQL heap tables have no persistent clustered index; all indexes are secondary. The CLUSTER command reorders rows once. 

##### **Q. When would you choose MongoDB over PostgreSQL?** 

**A.** When the schema varies or evolves rapidly, the data is document-shaped and read as a whole, writes are high-volume and append-heavy (logs/events), or you need horizontal sharding. Financial transactions stay relational. 

##### **Q. What is the difference between a view and a materialized view?** 

**A.** A view is a stored query that runs each time. A materialized view stores the result physically and must be refreshed, which suits heavy reports. 

Page 32 of 63 

PKSF Full-stack Software Engineer — Study Material 

**Phase 5 — APIs, Integration and Microservices** 

### **REST API Design** 

- **Resources are nouns, plural:** /api/v1/pos/{poId}/members, /api/v1/loans/{id}/installments. 

- **Use HTTP verbs:** GET (read), POST (create / non-idempotent action), PUT (replace), PATCH (partial), DELETE. 

- **Actions that are not CRUD:** POST /loans/{id}/approve, or treat them as sub-resources (POST /loans/{id}/repayments). 

- **Filtering, sorting and paging:** GET /loans?status=ACTIVE&po=12&sort=createdAt,desc&page=0&size=20. 

- **Stateless:** each request carries its auth (token). No server session is required. 

- **Idempotency key:** for payment or disbursement POSTs, the client sends an Idempotency-Key header. The server stores the key with its result and returns the same response on retry, which prevents double disbursement. 

- **Versioning:** URI (/v1/), which is simplest and most common, header (API-Version: 2) or media type (application/vnd.pksf.v2+json). 

- **Rate limiting:** token bucket (Bucket4j + Redis) at the gateway. Return 429 with Retry-After. 

### **Data Exchange Formats** 

#### **JSON with Jackson** 

<mark>public record LoanResponse( Long id, @JsonProperty("member_name") String memberName, BigDecimal amount, @JsonFormat(pattern = "yyyy-MM-dd") LocalDate disbursedOn, @JsonInclude(JsonInclude.Include.NON_NULL) String remarks) {}</mark> 

<mark>ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule()) .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); String json = om.writeValueAsString(dto); LoanResponse back = om.readValue(json, LoanResponse.class); List<LoanResponse> list = om.readValue(jsonArray, new TypeReference<>() {});</mark> 

#### **XML with JAXB / Jackson XML** 

<mark>@XmlRootElement(name = "loan") @XmlAccessorType(XmlAccessType.FIELD) public class LoanXml { @XmlAttribute private Long id; @XmlElement private String member; @XmlElement private BigDecimal amount; } JAXBContext ctx = JAXBContext.newInstance(LoanXml.class); Marshaller m = ctx.createMarshaller(); m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); m.marshal(loan, System.out); LoanXml parsed = (LoanXml) ctx.createUnmarshaller().unmarshal(new StringReader(xml));</mark> 

- Spring MVC can return both JSON and XML from one endpoint (content negotiation via the Accept header) if jackson-dataformat-xml is on the classpath. 

- **XSD** defines and validates XML structure. **XPath** queries XML (e.g. /loan/amount). 

- **Security:** disable external entities when parsing XML to prevent XXE attacks. 

#### **SOAP** 

- **SOAP** is an XML-based protocol with a strict contract. The message is an **Envelope** containing an optional **Header** (security, WS-Security tokens) and a **Body** (payload or **Fault** ). 

Page 33 of 63 

PKSF Full-stack Software Engineer — Study Material 

- The **WSDL** describes operations, messages, types (XSD), bindings and endpoint address. 

- It is common in government and bank integrations (payment gateways, NID verification, legacy systems). 

<mark><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:mem="http://pksf.org.bd/member"> <soapenv:Header/> <soapenv:Body> <mem:GetMemberRequest> <mem:nid>1990123456789</mem:nid> </mem:GetMemberRequest> </soapenv:Body> </soapenv:Envelope></mark> // Spring WS client: generate classes from WSDL (jaxb2-maven-plugin / wsimport), then: <mark>public class MemberSoapClient extends WebServiceGatewaySupport { public GetMemberResponse getMember(String nid) { GetMemberRequest req = new GetMemberRequest(); req.setNid(nid); return (GetMemberResponse) getWebServiceTemplate().marshalSendAndReceive(req); } }</mark> // Spring WS server: @Endpoint class with @PayloadRoot(namespace = NS, localPart = "GetMemberRequest") @ResponsePayload method 

|**Aspect**|**REST**|**SOAP**|**gRPC**|
|---|---|---|---|
|Format|JSON (usually), any|XML only|Protobuf (binary)|
|Contract|OpenAPI (optional)|WSDL (mandatory)|.proto (mandatory)|
|Transport|HTTP|HTTP, SMTP, others|HTTP/2|
|Standard<br>s|Lightweight|WS-Security, WS-ReliableMessaging, ACID-<br>ish|Streaming, codegen|
|Use|Web/mobile APIs|Enterprise/legacy/bank|Internal high-performance service calls|



### **Microservices Architecture** 

|**Monolith**|**Microservices**|
|---|---|
|One deployable, simple to develop and test|Many independently deployable services|
|Scale everything together|Scale services independently|
|One tech stack, shared DB|Polyglot, database per service|
|Easier transactions|Distributed transactions, network failures, ops complexity|



**Tip** A well-structured **modular monolith** is often the right first step. Split services when team size, scaling or deployment independence demands it. 

#### **Decomposition** 

- Split services by **business capability / DDD bounded context** : PO Service, Member Service, Loan Service, Repayment Service, Reporting Service, Notification Service, Auth (Keycloak). 

- Each service **owns its data** . Other services never query its tables directly; they call its API or consume its events. 

#### **Core Infrastructure** 

<mark>React SPA ──► API Gateway (Spring Cloud Gateway: routing, JWT validation, rate limit, CORS) │ ┌───────────┼──────────────┬────────────────┐ PO Service  Loan Service  Member Service  Report Service │ │ │ │</mark> 

Page 34 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>Postgres     Postgres       Postgres     Read replica / MongoDB │ RabbitMQ / Kafka  ──► Notifcation Service (SMS/Email)</mark> i <mark>Keycloak (auth)   Redis (cache/session)   Confg Server   Prometheus/Grafana   Zipkin   ELK</mark> i # Spring Cloud Gateway routes <mark>spring: cloud: gateway: routes: - id: loan-service uri: lb://loan-service          # via discovery; in K8s use http://loan-service:8080 predicates: - Path=/api/v1/loans/** f</mark> i <mark>lters: - name: RequestRateLimiter args: redis-rate-limiter.replenishRate: 20 redis-rate-limiter.burstCapacity: 40</mark> 

- **Service discovery:** Eureka (Spring Cloud Netflix) or, in Kubernetes, built-in Service DNS (preferred). 

- **Config:** Spring Cloud Config Server (Git-backed), or K8s ConfigMaps/Secrets. 

#### **Communication** 

- **Synchronous** (REST/Feign/gRPC): simple, but creates temporal coupling. If the callee is down, the caller fails. 

- **Asynchronous** (events via RabbitMQ/Kafka): loose coupling and resilience, with eventual consistency. Example: LoanDisbursed event → Notification sends SMS, Reporting updates totals. 

#### **Resilience with Resilience4j** 

<mark>@CircuitBreaker(name = "memberService", fallbackMethod = "memberFallback") @Retry(name = "memberService") @TimeLimiter(name = "memberService") public CompletableFuture<MemberDto> getMember(Long id) { return CompletableFuture.supplyAsync(() -> memberClient.get(id)); } private CompletableFuture<MemberDto> memberFallback(Long id, Throwable t) { return CompletableFuture.completedFuture(MemberDto.unavailable(id)); }</mark> 

- **Circuit breaker states:** CLOSED (normal) → OPEN when the failure rate exceeds the threshold (fail fast) → HALF_OPEN (trial calls) → CLOSED or OPEN. 

- **Retry:** with exponential backoff, and only for idempotent operations. **Timeout:** always set one. **Bulkhead:** limit concurrent calls so one slow dependency can't exhaust all threads. 

#### **Distributed Transactions — Saga and Outbox** 

- **Two-phase commit** does not fit microservices (blocking, tight coupling). 

- **Saga:** a sequence of local transactions, each publishing an event. On failure, run **compensating transactions** (e.g. cancel disbursement). Choreography uses events with no central coordinator. Orchestration uses a central saga orchestrator that tells each service what to do. 

- **Transactional outbox:** write the business change and an outbox row in the same DB transaction. A relay (poller or Debezium CDC) publishes outbox rows to the broker. This avoids the dual-write problem where the DB commit succeeds but the message is lost. 

#### **Observability** 

- **Logs:** structured JSON logs with a traceId, shipped to ELK/EFK or Loki. 

- **Metrics:** Micrometer → /actuator/prometheus → Prometheus → Grafana dashboards (latency, error rate, JVM, DB pool). 

- **Tracing:** Micrometer Tracing / OpenTelemetry → Zipkin/Jaeger, to follow one request across services. 

Page 35 of 63 

PKSF Full-stack Software Engineer — Study Material 

- **Twelve-factor:** one codebase, explicit dependencies, config in the environment, backing services as attached resources, build/release/run stages, stateless processes, port binding, concurrency, disposability, dev/prod parity, logs as streams, admin processes. 

### **Messaging** 

- **Concepts:** producer, broker, consumer, acknowledgment, at-least-once delivery (so consumers must be **idempotent** ), dead-letter queue (DLQ) for poison messages, ordering guarantees. 

- **RabbitMQ:** exchanges (direct, topic, fanout) route to queues through bindings. Good for task queues and routing. 

- **Kafka:** a distributed log. Topics have partitions, and order is guaranteed only within a partition. Consumer groups share partitions and track offsets. Data is retained and can be replayed. Good for event streaming and high throughput. 

<mark>@Bean Queue smsQueue() { return QueueBuilder.durable("sms.queue").deadLetterExchange("dlx").build(); }</mark> 

<mark>rabbitTemplate.convertAndSend("loan.events", "loan.disbursed", new LoanDisbursed(loanId, memberPhone, amount));</mark> 

<mark>@RabbitListener(queues = "sms.queue") public void onDisbursed(LoanDisbursed e) { smsService.send(e.phone(), "Loan of Tk " + e.amount() + " disbursed."); }</mark> 

### **Phase 5 — Interview Questions** 

##### **Q. How do you keep data consistent across microservices?** 

**A.** Each service owns its database. Use the Saga pattern with compensating actions, publish events through a transactional outbox, and design for eventual consistency and idempotent consumers. 

##### **Q. What is a circuit breaker and why use it?** 

**A.** It stops calling a failing dependency after a failure threshold and fails fast or uses a fallback. This prevents cascading failures and resource exhaustion, and it periodically tests recovery in the half-open state. 

##### **Q. What does an API Gateway do?** 

**A.** It is a single entry point. It handles routing, authentication/JWT validation, rate limiting, CORS, request logging and response aggregation, and it hides internal service topology. 

##### **Q. SOAP vs REST — when would you still use SOAP?** 

**A.** When integrating with existing enterprise, bank or government systems that expose WSDL, or when WSSecurity, formal contracts and reliable messaging standards are required. 

##### **Q. How do you make a message consumer idempotent?** 

**A.** Store processed message IDs (or a business key) in a table with a unique constraint and skip duplicates, or design operations so that repeating them has no extra effect (upsert, set status). 

##### **Q. Monolith or microservices for a new PKSF system?** 

**A.** Start with a modular monolith with clear module boundaries and separate schemas, unless there is a clear need for independent scaling or deployment. Extract services later along those boundaries. This keeps operational cost low. 

Page 36 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 6 — Authentication and Security** 

### **Security Fundamentals** 

|**OWASP Top 10 (2021)**|**Mitigation in our stack**|
|---|---|
|A01 Broken Access Control|@PreAuthorize, check record ownership (PO scoping), deny<br>by default|
|A02 Cryptographic Failures|TLS everywhere, BCrypt passwords, encrypt sensitive columns|
|A03 Injection|Parameterized queries/JPA, validate input, no string-<br>concatenated SQL|
|A04 Insecure Design|Threat modeling, rate limits, business rule checks|
|A05 Security Misconfiguration|Disable unused actuator endpoints, no default creds, secure<br>headers|
|A06 Vulnerable Components|Dependabot/OWASP dependency-check, image scanning|
|A07 Identification & Auth Failures|Keycloak, MFA, lockout, short-lived tokens|
|A08 Software & Data Integrity Failures|Signed artifacts, CI pipeline protection|
|A09 Logging & Monitoring Failures|Audit logs, alerting|
|A10 SSRF|Validate/allow-list outbound URLs|



<mark>// SQL injection: BAD vs GOOD String bad = "SELECT * FROM members WHERE nid = '" + nid + "'";       // injectable jdbcTemplate.query("SELECT * FROM members WHERE nid = ?", mapper, nid); // parameterized</mark> 

- **XSS:** React escapes values by default. Never use dangerouslySetInnerHTML with user input, or sanitize with DOMPurify. Add a Content-Security-Policy header. 

- **CSRF:** relevant when auth uses cookies. Use CSRF tokens and SameSite cookies. APIs using Bearer tokens in headers are not vulnerable to classic CSRF. 

- **Hashing** is one-way (passwords: BCrypt/Argon2 with salt). **Encryption** is reversible with a key (AES for data, RSA for key exchange). **Encoding** (Base64) is not security. 

- **Secrets:** never commit them. Use env vars, K8s Secrets (with encryption at rest) or HashiCorp Vault. Rotate them regularly. 

### **Spring Security** 

- Requests pass through the **SecurityFilterChain** (a chain of servlet filters) before reaching the DispatcherServlet. Filters authenticate (create an Authentication in the SecurityContext) and then authorize. 

- **Authentication** answers "who are you?" **Authorization** answers "what can you do?" 

- **Role vs authority:** hasRole('ADMIN') checks the authority ROLE_ADMIN. 

<mark>@Confguration</mark> i <mark>@EnableMethodSecurity public class SecurityConfg {</mark> i <mark>@Bean SecurityFilterChain api(HttpSecurity http) throws Exception { http .csrf(csrf -> csrf.disable())                         // stateless JWT API .cors(Customizer.withDefaults()) .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) .authorizeHttpRequests(auth -> auth .requestMatchers("/actuator/health/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll() .requestMatchers(HttpMethod.GET, "/api/v1/reports/**").hasAnyRole("ADMIN", "AUDITOR")</mark> 

Page 37 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>.requestMatchers("/api/v1/admin/**").hasRole("ADMIN") .anyRequest().authenticated()) .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(keycloakConverter()))); return http.build(); }</mark> 

<mark>@Bean CorsConf</mark> i <mark>gurationSource corsConfgurationSource() {</mark> i <mark>CorsConfguration c = new CorsConf</mark> i <mark>guration();</mark> i <mark>c.setAllowedOrigins(List.of("https://app.pksf.org.bd", "http://localhost:5173")); c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE")); c.setAllowedHeaders(List.of("Authorization", "Content-Type")); UrlBasedCorsConfgurationSource src = new UrlBasedCorsConf</mark> i <mark>gurationSource();</mark> i <mark>src.registerCorsConfguration("/**", c);</mark> i <mark>return src; }</mark> 

<mark>@Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); } }</mark> 

<mark>// Method-level, including ownership checks @PreAuthorize("hasRole('PO_OFFICER') and @poAccess.canAccess(authentication, #poId)") public List<MemberDto> members(Long poId) { ... }</mark> 

### **JWT** 

- **Structure:** base64url(header).base64url(payload).signature 

- **Header:** {"alg":"RS256","typ":"JWT","kid":"..."}. **Payload claims:** iss, sub, aud, exp, iat, plus custom claims (roles, po_id). 

- The payload is **encoded, not encrypted** , so anyone can read it. Never put secrets in it. 

- **HS256:** shared secret HMAC. Every verifier must know the secret. **RS256/ES256:** a private key signs and a public key verifies. Public keys are published at a JWKS endpoint, which is what Keycloak does. 

- **Always validate:** signature, exp, iss, aud and alg (reject "none"). 

- **Access token:** short-lived (5–15 minutes). **Refresh token:** longer-lived, used to get new access tokens, and rotated on each use. 

- **Revocation:** JWTs are stateless and can't be revoked by default. Use short expiry, a denylist in Redis keyed by jti, or token introspection. 

|**Storage**|**XSS risk**|**CSRF risk**|**Notes**|
|---|---|---|---|
|localStorage|High (JS can read)|None|Simple but risky|
|Memory (JS variable)|Lower|None|Lost on refresh; use silent refresh|
|HttpOnly Secure cookie|JS can't read|Needs SameSite/CSRF token|Recommended with BFF pattern|



### **OAuth2 and OpenID Connect** 

- **OAuth2** is an authorization delegation framework. **OIDC** is an identity layer on top that adds an **ID token** (a JWT about the user), the userinfo endpoint and discovery (/.well-known/openid-configuration). 

- **Roles:** Resource Owner (user), Client (React app / service), Authorization Server (Keycloak), Resource Server (Spring Boot API). 

- **Authorization Code + PKCE** for SPAs and mobile apps: 

<mark>1. React generates code_verifer (random) and code_challenge = SHA256(verif</mark> i <mark>er)</mark> i 

<mark>2. Redirect user to Keycloak /auth?response_type=code&client_id=pksf-web&code_challenge=...&redirect_uri=... 3. User logs in at Keycloak → redirected back with ?code=XYZ 4. React POSTs code + code_verifer to /token</mark> i <mark>→ receives access_token, refresh_token, id_token</mark> 

<mark>5. React calls API with Authorization: Bearer <access_token></mark> 

<mark>6. Spring Boot validates the JWT signature using Keycloak's JWKS public keys</mark> 

Page 38 of 63 

PKSF Full-stack Software Engineer — Study Material 

- **Client Credentials:** machine-to-machine (service A calls service B) with no user. 

- **Deprecated:** the Implicit and Resource Owner Password grants. Don't use them. 

- **Scopes:** coarse permissions requested by the client (read:loans). **Roles:** user permissions assigned in the IdP. 

### **Keycloak** 

- **Realm:** an isolated tenant (users, clients, roles), for example the realm "pksf". 

- **Client:** an application. pksf-web (public client, PKCE) and loan-service (confidential/bearer-only). 

- **Roles:** realm roles (ADMIN, PO_OFFICER, AUDITOR) or client roles. **Groups** bundle roles. Custom attributes such as po_id can be mapped into the token with protocol mappers. 

<mark># docker-compose excerpt keycloak: image: quay.io/keycloak/keycloak:25.0 command: start-dev --import-realm environment: KC_BOOTSTRAP_ADMIN_USERNAME: admin KC_BOOTSTRAP_ADMIN_PASSWORD: admin ports: ["8081:8080"] volumes: ["./keycloak/realm-export.json:/opt/keycloak/data/import/realm.json"]</mark> # Spring Boot application.yml <mark>spring: security: oauth2: resourceserver: jwt: issuer-uri: http://localhost:8081/realms/pksf</mark> // Map Keycloak realm roles (realm_access.roles) to Spring authorities <mark>@Bean JwtAuthenticationConverter keycloakConverter() { JwtAuthenticationConverter conv = new JwtAuthenticationConverter(); conv.setJwtGrantedAuthoritiesConverter(jwt -> { Map<String, Object> realm = jwt.getClaim("realm_access"); if (realm == null) return List.of(); @SuppressWarnings("unchecked") List<String> roles = (List<String>) realm.getOrDefault("roles", List.of()); return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList()); }); return conv; }</mark> // React with keycloak-js <mark>import Keycloak from "keycloak-js"; export const keycloak = new Keycloak({ url: "http://localhost:8081", realm: "pksf", clientId: "pksf-web" }); await keycloak.init({ onLoad: "login-required", pkceMethod: "S256", checkLoginIframe: false });</mark> 

<mark>api.interceptors.request.use(async (conf</mark> i <mark>g) => { await keycloak.updateToken(30);                 // refresh if expiring within 30s conf</mark> i <mark>g.headers.Authorization = `Bearer ${keycloak.token}`; return confg;</mark> i <mark>}); const isAdmin = keycloak.hasRealmRole("ADMIN"); const logout = () => keycloak.logout({ redirectUri: window.location.origin });</mark> 

- **Service-to-service:** a confidential client with a service account obtains a token via client_credentials, using spring-boot-starter-oauth2-client with an OAuth2AuthorizedClientManager. 

- **Advanced:** user federation (LDAP/AD), identity brokering (Google/Microsoft login), MFA (OTP), custom login themes, fine-grained authorization services. 

Page 39 of 63 

PKSF Full-stack Software Engineer — Study Material 

### **Data Protection and Auditing** 

- **Audit trail:** record who, what, when, from where and the old/new values for every create, update, delete and approval. Store it append-only (MongoDB collection or Postgres table with no UPDATE/DELETE grant). Use Hibernate Envers or a custom @EntityListener / AOP. 

- **Encryption:** TLS for every hop including DB connections (sslmode=require). Encrypt sensitive columns with pgcrypto or app-level AES-GCM, and disk encryption at rest. 

- **Masking:** show NID as ********6789 in the UI and logs. Restrict full view by role. 

- **Least privilege** for users, DB accounts and K8s service accounts. Review access periodically. 

### **Phase 6 — Interview Questions** 

##### **Q. Authentication vs authorization?** 

**A.** Authentication verifies identity (login, token validation). Authorization decides what an authenticated identity may access (roles, permissions, ownership). 

##### **Q. How does a Spring Boot resource server validate a Keycloak JWT?** 

**A.** On startup it reads issuer-uri/.well-known/openid-configuration to find the JWKS URI. For each request it verifies the signature with the matching public key (by kid) and checks exp, iss and optionally aud. It then maps claims to authorities. 

##### **Q. Why use Authorization Code with PKCE for a React app?** 

**A.** A SPA can't keep a client secret. PKCE binds the authorization code to a one-time verifier, so an intercepted code is useless. The implicit flow is deprecated because it exposes tokens in URLs. 

##### **Q. How do you log out or revoke a JWT?** 

**A.** Keep access tokens short-lived, revoke the refresh token at Keycloak (logout endpoint), and if immediate revocation is needed, keep a Redis denylist of jti values until they expire, or use introspection. 

##### **Q. HS256 vs RS256?** 

**A.** HS256 uses one shared secret for signing and verifying, so every service that verifies can also forge tokens. RS256 signs with a private key and verifies with a public key, which suits distributed systems. 

##### **Q. How do you prevent SQL injection in Spring Boot?** 

**A.** Use JPA/JPQL parameters or JdbcTemplate placeholders and never concatenate user input into SQL. Validate input and run with least-privilege DB users. 

Page 40 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 7 — Redis Caching and Sessions** 

### **Redis Fundamentals** 

- Redis is an in-memory key–value store. It is single-threaded for command execution, so each command is atomic, and it typically answers in under a millisecond. 

|**Type**|**Commands**|**Use case**|
|---|---|---|
|String|SET, GET, INCR, SETEX|Cache values, counters, rate limiting|
|Hash|HSET, HGET, HGETALL|Object fields (session data, user profile)|
|List|LPUSH, RPOP, LRANGE|Queues, recent activity|
|Set|SADD, SISMEMBER, SINTER|Unique items, tags, online users|
|Sorted Set|ZADD, ZRANGE, ZREVRANK|Leaderboards, sliding-window rate limit, delayed jobs|
|Stream|XADD, XREADGROUP|Durable event log with consumer groups|



<mark>SET po:12 '{"id":12,"name":"ABC"}' EX 3600     # set with 1-hour TTL GET po:12 TTL po:12 INCR rate:user:42                              # atomic counter EXPIRE rate:user:42 60 SET lock:loan:42 uuid NX PX 10000              # acquire lock if absent, 10s expiry</mark> 

- **Eviction (when maxmemory is reached):** noeviction, allkeys-lru (common for caches), allkeys-lfu, volatilelru (only keys with TTL), volatile-ttl. 

- **Persistence:** RDB takes periodic snapshots (fast restart, may lose recent data). AOF logs every write (more durable, larger). Both can be combined. 

### **Caching Patterns** 

- **Cache-aside (lazy loading):** the app checks the cache. On a miss it loads from the DB and writes to the cache. On update it writes the DB and evicts the cache key. This is the most common pattern and what @Cacheable does. 

- **Write-through:** write to the cache and DB synchronously. The cache is always fresh, but writes are slower. 

- **Write-behind:** write to the cache and flush to the DB asynchronously. Writes are fast but data can be lost. 

- **What to cache:** read-heavy, rarely changing data (PO list, districts, loan products, user permissions, dashboard aggregates). Don't cache rapidly changing balances without careful invalidation. 

- **Invalidation:** TTL as a safety net, explicit evict on update, or event-driven eviction across services. 

- **Cache stampede** (many misses at once when a hot key expires): use a mutex/lock or early refresh. **Penetration** (queries for non-existent keys): cache nulls briefly or use a Bloom filter. **Avalanche** (many keys expire together): add random jitter to TTLs. 

<mark>spring: data: redis: host: localhost port: 6379 cache: type: redis redis: time-to-live: 10m</mark> 

@Configuration <mark>@EnableCaching public class CacheConf</mark> i <mark>g { @Bean RedisCacheConfguration cacheConf</mark> i <mark>guration() {</mark> i 

Page 41 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>return RedisCacheConf</mark> i <mark>guration.defaultCacheConf</mark> i <mark>g() .entryTtl(Duration.ofMinutes(10)) .disableCachingNullValues() .serializeValuesWith(RedisSerializationContext.SerializationPair .fromSerializer(new GenericJackson2JsonRedisSerializer())); } } @Service public class PoService { @Cacheable(cacheNames = "po", key = "#id", unless = "#result == null") public PoDto get(Long id) { ... } @CachePut(cacheNames = "po", key = "#id") public PoDto update(Long id, PoUpdate req) { ... } @CacheEvict(cacheNames = "po", allEntries = true) public void reloadAll() { ... } } // Direct access @Autowired StringRedisTemplate redis; redis.opsForValue().set("otp:" + phone, otp, Duration.ofMinutes(5)); String saved = redis.opsForValue().get("otp:" + phone);</mark> 

### **Sessions and Distributed Use Cases** 

- **Spring Session + Redis:** stores HttpSession in Redis, so any app instance behind a load balancer can serve the user. This removes the need for sticky sessions. Add spring-session-data-redis and set spring.session.store-type=redis (auto-detected in Boot 3). 

- **Distributed lock:** SET key value NX PX ttl. Release only if the value matches, using a Lua script. Redisson's RLock handles this, plus watchdog renewal. 

- **Rate limiting:** a fixed window with INCR + EXPIRE, or a sliding window with a sorted set of timestamps. 

- **Pub/Sub:** fire-and-forget broadcast, for example cache invalidation across instances. **Streams:** durable, with consumer groups. 

- **High availability:** Sentinel provides automatic failover for primary/replica. Cluster shards data across nodes (16384 hash slots). 

<mark>RLock lock = redisson.getLock("lock:loan:" + loanId); if (lock.tryLock(2, 10, TimeUnit.SECONDS)) { try { disburse(loanId); } fnally { lock.unlock(); }</mark> i <mark>}</mark> 

### **Phase 7 — Interview Questions** 

##### **Q. Why is Redis fast?** 

**A.** Data lives in memory, it uses efficient data structures, and a single-threaded event loop avoids lock overhead. It uses I/O multiplexing and a simple protocol. 

##### **Q. How do you keep the cache consistent with the database?** 

**A.** Cache-aside: update the DB first, then delete the cache key (not update it), with a TTL as a safety net. For multi-instance setups, publish invalidation events. Accept brief staleness for non-critical data and never cache authoritative balances blindly. 

##### **Q. How do you share sessions across multiple Spring Boot instances?** 

**A.** Use Spring Session with Redis as the store (a stateless JWT also avoids sessions). The load balancer then needs no sticky sessions. 

Page 42 of 63 

PKSF Full-stack Software Engineer — Study Material 

##### **Q. What is a cache stampede and how do you prevent it?** 

- **A.** When a hot key expires, many requests hit the DB at once. Prevent it with a lock so only one request rebuilds the value, serve stale data while refreshing, or add TTL jitter. 

##### **Q. RDB vs AOF?** 

**A.** RDB takes point-in-time snapshots: compact and fast to restore, but writes since the last snapshot can be 

lost. AOF appends every write: more durable, with larger files and slower restarts. 

Page 43 of 63 

PKSF Full-stack Software Engineer — Study Material 

**Phase 8 — Frontend with React** 

### **HTML and CSS** 

- **Semantic HTML:** header, nav, main, section, article, footer, button (not a clickable div). Give forms label for= and proper input types. 

- **Accessibility:** alt text, keyboard navigation, focus states, color contrast, aria-label where needed. 

- **Box model:** content + padding + border + margin. Use box-sizing: border-box. 

- **Flexbox** for one-dimensional layout (rows or columns). **Grid** for two-dimensional layout. 

- **Responsive design:** mobile-first CSS with @media (min-width: 768px), relative units (rem, %), and flexible images. 

- **Position:** static, relative, absolute (relative to the nearest positioned ancestor), fixed, sticky. 

<mark>.layout { display: grid; grid-template-columns: 240px 1fr; min-height: 100vh; } .toolbar { display: fex; justify-content: space-between; align-items: center; gap: 1rem; }</mark> l <mark>@media (max-width: 768px) { .layout { grid-template-columns: 1fr; } }</mark> 

### **JavaScript** 

<mark>const { name, po: { district } = {} } = member;          // destructuring with default const updated = { ...member, phone: "017..." };           // spread (immutable update) const sum = (...nums) => nums.reduce((a, b) => a + b, 0);  // rest + arrow + reduce const label = `Loan #${id} for ${name}`;                   // template literal const city = member?.address?.city ?? "N/A";              // optional chaining + nullish coalescing</mark> 

- **var** is function-scoped and hoisted (initialized as undefined). **let/const** are block-scoped and sit in the temporal dead zone before their declaration. 

- **Closure:** a function remembers variables from the scope where it was created. 

- **this:** depends on how a function is called. Arrow functions don't have their own this; they use the enclosing scope's. 

- **== vs ===:** == coerces types ("5" == 5 is true). === is strict. Always use ===. 

- **Falsy values:** false, 0, "", null, undefined, NaN. Everything else is truthy. 

<mark>function counter() { let c = 0; return () => ++c; }      // closure const next = counter(); next(); next();                   // 1, 2 // Classic closure-in-loop for (var i = 0; i < 3; i++) setTimeout(() => console.log(i));  // 3 3 3 for (let i = 0; i < 3; i++) setTimeout(() => console.log(i));  // 0 1 2</mark> 

#### **Async JavaScript and the Event Loop** 

<mark>async function loadDashboard() { try { const [pos, loans] = await Promise.all([api.get("/pos"), api.get("/loans?status=ACTIVE")]); return { pos: pos.data, loans: loans.data }; } catch (err) { console.error("Failed", err); throw err; } } console.log(1); setTimeout(() => console.log(2), 0);        // macrotask Promise.resolve().then(() => console.log(3)); // microtask console.log(4); // Output: 1 4 3 2  — sync f</mark> i <mark>rst, then all microtasks, then macrotasks</mark> 

- **Event loop:** the call stack runs synchronous code. When it is empty, all **microtasks** run (promise callbacks, queueMicrotask), then one **macrotask** (setTimeout, I/O, events), then rendering. 

Page 44 of 63 

PKSF Full-stack Software Engineer — Study Material 

- **Promise.all** fails fast on the first rejection. **Promise.allSettled** waits for all. **Promise.race** settles with the first. **Promise.any** resolves with the first fulfilled promise. 

- **Array methods:** map (transform), filter (subset), reduce (accumulate), find/findIndex, some/every, flatMap, sort (mutates; use toSorted for a copy). 

#### **Axios Instance with Interceptors** 

<mark>import axios from "axios"; export const api = axios.create({ baseURL: import.meta.env.VITE_API_URL, timeout: 15000 }); api.interceptors.response.use( (res) => res, (error) => { if (error.response?.status === 401) keycloak.login(); return Promise.reject(error); } );</mark> 

### **TypeScript** 

<mark>type LoanStatus = "PENDING" | "ACTIVE" | "CLOSED" | "WRITTEN_OFF";   // literal union</mark> 

<mark>interface Loan { id: number; memberName: string; amount: number; status: LoanStatus; disbursedOn?: string;          // optional readonly createdAt: string; }</mark> 

<mark>interface Page<T> { content: T[]; totalElements: number; totalPages: number; number: number; size: number; }</mark> 

<mark>async function fetchPage<T>(url: string, params?: Record<string, unknown>): Promise<Page<T>> { const { data } = await api.get<Page<T>>(url, { params }); return data; }</mark> 

<mark>type LoanCreate = Omit<Loan, "id" | "createdAt" | "status">; type LoanPatch = Partial<Pick<Loan, "amount" | "status">>; type StatusCount = Record<LoanStatus, number>;</mark> 

- **type vs interface:** both describe object shapes. Interfaces can be extended and merged. Types support unions, intersections and mapped types. 

- **unknown vs any:** any disables checking. unknown forces you to narrow the type before use. 

- **Narrowing:** typeof, instanceof, in, discriminated unions. 

### **React Core** 

#### **Components, Props and State** 

<mark>type Props = { loan: Loan; onApprove: (id: number) => void };</mark> 

<mark>function LoanRow({ loan, onApprove }: Props) { return ( <tr> <td>{loan.memberName}</td> <td>{loan.amount.toLocaleString("en-BD")}</td> <td>{loan.status}</td></mark> 

<mark><td>{loan.status === "PENDING" && <button onClick={() => onApprove(loan.id)}>Approve</button>}</td> </tr> );</mark> 

Page 45 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>}</mark> 

<mark>function LoanTable({ loans }: { loans: Loan[] }) { const [flter, setFilter] = useState<LoanStatus | "ALL">("ALL");</mark> i <mark>const visible = f</mark> i <mark>lter === "ALL" ? loans : loans.flter((l) => l.status === f</mark> i <mark>lter);  // derived, not state</mark> i <mark>return ( <> <select value={flter} onChange={(e) => setFilter(e.target.value as LoanStatus | "ALL")}></mark> i <mark><option value="ALL">All</option><option value="PENDING">Pending</option><option value="ACTIVE">Active</option> </select> <table><tbody>{visible.map((l) => <LoanRow key={l.id} loan={l} onApprove={approve} />)}</tbody></table> </> ); }</mark> 

- **Props** are read-only inputs from the parent. **State** is local data owned by the component; changing it triggers a re-render. 

- **Update state immutably:** setItems([...items, x]), setUser({ ...user, name }). Use the functional form when the new value depends on the old one: setCount(c => c + 1). 

- **Don't store derived data in state.** Compute it during render (or with useMemo if it is expensive). 

- **Keys:** must be stable and unique among siblings (a DB id). Using the array index breaks state when items are reordered or removed. 

- **Re-renders happen when** state changes, the parent re-renders, or a consumed context changes. 

#### **useEffect** 

<mark>useEfect(() => {</mark> f <mark>const controller = new AbortController(); api.get(`/members/${id}`, { signal: controller.signal }).then((r) => setMember(r.data)); return () => controller.abort();              // cleanup on unmount or id change }, [id]);                                       // re-run when id changes</mark> 

- **No dependency array:** runs after every render. **[]:** once on mount. **[a, b]:** when a or b changes. 

- Cleanup runs before the next effect and on unmount (timers, subscriptions, aborting fetches). 

- In React 18 StrictMode (dev), effects run twice on mount to surface missing cleanup. 

- Prefer TanStack Query for data fetching instead of hand-written effects. 

#### **Forms, Refs, Memoization, Context, Reducers** 

<mark>// Controlled input const [name, setName] = useState(""); <input value={name} onChange={(e) => setName(e.target.value)} /> // useRef: DOM access or mutable value that doesn't trigger render const inputRef = useRef<HTMLInputElement>(null); useEfect(() => inputRef.current?.focus(), []);</mark> f <mark>// useMemo / useCallback: avoid expensive recalculation / keep stable function identity const total = useMemo(() => loans.reduce((s, l) => s + l.amount, 0), [loans]); const handleApprove = useCallback((id: number) => approveMutation.mutate(id), [approveMutation]);</mark> 

<mark>// useReducer for complex state transitions type Action = { type: "add"; item: Item } | { type: "remove"; id: number }; function reducer(state: Item[], a: Action): Item[] { switch (a.type) { case "add": return [...state, a.item]; case "remove": return state.flter((i) => i.id !== a.id);</mark> i <mark>} } const [items, dispatch] = useReducer(reducer, []); // Context for app-wide values (auth user, theme, language) const AuthContext = createContext<{ user?: User; logout: () => void } | null>(null);</mark> 

Page 46 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>export const useAuth = () => { const ctx = useContext(AuthContext); if (!ctx) throw new Error("useAuth must be inside AuthProvider"); return ctx; };</mark> 

<mark>// Custom hook function useDebounce<T>(value: T, delay = 400) { const [v, setV] = useState(value); useEfect(() => { const t = setTimeout(() => setV(value), delay); return () => clearTimeout(t); }, [value, delay]);</mark> f <mark>return v; }</mark> 

- **Rules of hooks:** call them only at the top level (not in loops or conditions) and only from components or custom hooks. 

- **Virtual DOM and reconciliation:** React renders a new tree, diffs it with the previous one (comparing element type and key), and applies minimal DOM updates. 

- **Error boundaries:** class components (or the react-error-boundary library) that catch render errors in children and show a fallback UI. 

### **React Ecosystem** 

<mark>src/</mark> 

- <mark>├── api/            (axios instance, endpoint functions) ├── auth/           (keycloak, AuthProvider, ProtectedRoute) ├── components/     (shared UI) ├── features/ │ ├── loans/     (LoanListPage, LoanForm, hooks/useLoans.ts, types.ts) │ └── members/ ├── hooks/ ├── i18n/           (en.json, bn.json) ├── routes.tsx └── main.tsx</mark> 

#### **Routing and Protected Routes** 

<mark>const router = createBrowserRouter([ { path: "/", element: <Layout />, errorElement: <ErrorPage />, children: [ { index: true, element: <Dashboard /> }, { path: "loans", element: <LoanListPage /> }, { path: "loans/:id", element: <LoanDetailPage /> }, { path: "admin", element: <RequireRole role="ADMIN"><AdminPage /></RequireRole> }, ]}, ]);</mark> 

<mark>function RequireRole({ role, children }: { role: string; children: JSX.Element }) { return keycloak.hasRealmRole(role) ? children : <Navigate to="/" replace />; } const { id } = useParams();                 // read route param const navigate = useNavigate();             // navigate("/loans")</mark> 

#### **Server State with TanStack Query** 

<mark>export function useLoans(page: number, status?: LoanStatus) { return useQuery({ queryKey: ["loans", page, status], queryFn: () => fetchPage<Loan>("/loans", { page, size: 20, status }), placeholderData: keepPreviousData, }); } export function useApproveLoan() { const qc = useQueryClient(); return useMutation({</mark> 

Page 47 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>mutationFn: (id: number) => api.post(`/loans/${id}/approve`), onSuccess: () => qc.invalidateQueries({ queryKey: ["loans"] }), }); }</mark> 

<mark>const { data, isLoading, isError } = useLoans(page, status);</mark> 

- **Server state** (from the API) belongs in TanStack Query, which handles caching, refetching, retries and stale time. **Client/UI state** (modals, filters) belongs in useState, Context, Zustand or Redux Toolkit. 

#### **Forms with React Hook Form + Zod** 

<mark>const schema = z.object({ memberId: z.number({ required_error: "Member is required" }), amount: z.number().min(1000).max(500000), tenureMonths: z.number().int().min(1).max(60), purpose: z.string().min(3).max(200), }); type FormValues = z.infer<typeof schema>; function LoanForm() { const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormValues>({ resolver: zodResolver(schema) }); const create = useMutation({ mutationFn: (v: FormValues) => api.post("/loans", v) }); return ( <form onSubmit={handleSubmit((v) => create.mutateAsync(v))}> <input type="number" {...register("amount", { valueAsNumber: true })} /> {errors.amount && <span role="alert">{errors.amount.message}</span>} <button disabled={isSubmitting}>Save</button> </form> ); }</mark> 

#### **UI Libraries, Tables and Charts** 

- **MUI or Ant Design** provide ready components (DataGrid/Table, forms, dialogs, date pickers). Theme them to match branding. 

- **Server-side tables:** send page, size, sort and filters to the API (Spring Pageable) and show totalElements for pagination. 

- **Charts:** Recharts or Chart.js for dashboards (disbursement by month, PAR by PO). 

- **Global state:** Zustand for something minimal, or Redux Toolkit (createSlice, configureStore, RTK Query) for larger apps. 

### **React Advanced** 

- **Performance:** React.memo for pure components with stable props; useMemo/useCallback where profiling shows a benefit; code splitting with React.lazy(() => import('./ReportsPage')) inside Suspense; virtualize long lists (react-window); avoid creating new objects in props unnecessarily. 

- **SPA auth flow:** initialize Keycloak before rendering, attach the token in an interceptor, refresh silently with updateToken, guard routes by role, handle 401 by re-login and 403 with a "no access" page, and clear state on logout. 

- **i18n:** react-i18next with en.json and bn.json, plus a language switcher. Format numbers and dates with Intl (bn-BD). 

- **Next.js:** a React framework with SSR/SSG/ISR and file-based routing. Mostly not needed for internal admin SPAs. 

- **Build:** Vite (npm run build creates static files in dist/) served by Nginx. Environment variables use the VITE_ prefix and are baked in at build time. 

Page 48 of 63 

PKSF Full-stack Software Engineer — Study Material 

### **Phase 8 — Interview Questions** 

##### **Q. What is the virtual DOM?** 

**A.** It is an in-memory representation of the UI. On a state change, React creates a new tree, diffs it against the previous one and applies only the minimal changes to the real DOM. 

##### **Q. Why are keys important in lists?** 

**A.** React uses them to match elements between renders. Stable keys preserve component state and avoid unnecessary re-creation. Index keys cause bugs when items are reordered, inserted or deleted. 

##### **Q. useMemo vs useCallback?** 

**A.** useMemo caches a computed value. useCallback caches a function reference. Both recompute only when dependencies change. 

##### **Q. Controlled vs uncontrolled components?** 

**A.** Controlled inputs keep their value in React state (value + onChange). Uncontrolled inputs keep it in the DOM and you read it with a ref. React Hook Form uses uncontrolled inputs for performance. 

##### **Q. How do you share state between sibling components?** 

**A.** Lift the state up to the common parent and pass it down via props, or use Context or a store (Zustand/Redux) for widely shared state. 

##### **Q. What is the output order of console.log, setTimeout and Promise.then?** 

**A.** Synchronous logs first, then promise callbacks (microtasks), then setTimeout callbacks (macrotasks). 

##### **Q. How do you protect routes in React?** 

**A.** Use a wrapper component that checks authentication and roles (from Keycloak or the auth context) and redirects with Navigate if access is denied. The backend must still enforce authorization, because frontend checks are only UX. 

Page 49 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 9 — Testing and Code Quality** 

### **Backend Testing** 

- **Test pyramid:** many fast unit tests, fewer integration tests, few end-to-end tests. 

- **Unit:** one class, with dependencies mocked. **Integration:** real Spring context and real DB (Testcontainers). **E2E:** the full system through its UI or API. 

<mark>@ExtendWith(MockitoExtension.class) class LoanServiceTest { @Mock LoanRepository loanRepo; @Mock ApplicationEventPublisher events; @InjectMocks LoanService service; @Test void repay_reducesOutstanding() { Loan loan = Loan.active(1L, new BigDecimal("10000")); when(loanRepo.fndById(1L)).thenReturn(Optional.of(loan));</mark> i <mark>service.repay(1L, new RepaymentRequest(new BigDecimal("2500"))); assertThat(loan.getOutstanding()).isEqualByComparingTo("7500"); verify(events).publishEvent(any(RepaymentRecorded.class)); } @Test void repay_unknownLoan_throws() { when(loanRepo.fndById(99L)).thenReturn(Optional.empty());</mark> i <mark>assertThatThrownBy(() -> service.repay(99L, new RepaymentRequest(BigDecimal.TEN))) .isInstanceOf(LoanNotFoundException.class); } @ParameterizedTest @CsvSource({"10000,12,833.33", "5000,6,833.33"}) void installment(String principal, int months, String expected) { assertThat(service.installment(new BigDecimal(principal), months)).isEqualByComparingTo(expected); } }</mark> @WebMvcTest(LoanController.class) <mark>class LoanControllerTest { @Autowired MockMvc mvc; @MockitoBean LoanService loanService;      // Boot 3.4+; older: @MockBean @Test @WithMockUser(roles = "PO_OFFICER") void create_invalidAmount_returns400() throws Exception { mvc.perform(post("/api/v1/loans").with(csrf()) .contentType(MediaType.APPLICATION_JSON) .content("{\"memberId\":1,\"amount\":10,\"tenureMonths\":12,\"purpose\":\"cow\"}")) .andExpect(status().isBadRequest()) .andExpect(jsonPath("$.errors.amount").exists()); } @Test void unauthenticated_returns401() throws Exception { mvc.perform(get("/api/v1/loans")).andExpect(status().isUnauthorized()); } }</mark> @SpringBootTest <mark>@Testcontainers class LoanRepositoryIT { @Container @ServiceConnection</mark> 

Page 50 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");</mark> 

<mark>@Autowired LoanRepository repo;</mark> 

<mark>@Test void f</mark> i <mark>ndWithMember_fetchesInOneQuery() { List<Loan> loans = repo.f</mark> i <mark>ndWithMember(LoanStatus.ACTIVE); assertThat(loans).allSatisfy(l -> assertThat(l.getMember().getName()).isNotBlank()); } }</mark> 

- **Slices:** @WebMvcTest (controllers, MockMvc, security), @DataJpaTest (JPA repositories; embedded DB by default, so use @AutoConfigureTestDatabase(replace = NONE) with Testcontainers), @JsonTest, @SpringBootTest (full context). 

- **@ServiceConnection** (Boot 3.1+) wires Testcontainers connection details automatically. 

- **TDD cycle:** Red (write a failing test) → Green (minimal code to pass) → Refactor. 

- **JaCoCo:** coverage reports in CI. Aim for meaningful coverage of business logic, not 100% of everything. 

### **Frontend Testing** 

<mark>import { render, screen } from "@testing-library/react"; import userEvent from "@testing-library/user-event";</mark> 

<mark>test("shows validation error for small amount", async () => { render(<LoanForm />); await userEvent.type(screen.getByLabelText(/amount/i), "10"); await userEvent.click(screen.getByRole("button", { name: /save/i })); expect(await screen.f</mark> i <mark>ndByRole("alert")).toHaveTextContent(/1000/);</mark> 

<mark>});</mark> 

- Test behavior the way a user sees it: query by role, label or text, not by CSS classes. 

- **MSW (Mock Service Worker)** mocks API calls at the network level. **Playwright** runs end-to-end browser tests (login → create loan → see it in the list). 

### **Code Quality** 

- **Clean code:** intention-revealing names, small focused methods, no duplicated logic (DRY), no magic numbers (use constants/enums), fail fast with clear exceptions, and comments that explain why rather than what. 

- **Static analysis:** SonarQube (bugs, code smells, coverage gates), Checkstyle/Spotless (formatting), SpotBugs, ESLint + Prettier (frontend), OWASP dependency-check. 

- **Code review:** small PRs, a clear description, review for correctness, security and readability, and constructive comments. 

### **Phase 9 — Interview Questions** 

##### **Q. Difference between @Mock and @MockitoBean/@MockBean?** 

**A.** @Mock is a plain Mockito mock with no Spring involved. @MockitoBean (formerly @MockBean) replaces a bean inside the Spring test context. 

##### **Q. Why use Testcontainers instead of H2?** 

**A.** H2 behaves differently from PostgreSQL (SQL dialect, JSONB, indexes, functions). Testcontainers runs the real database in Docker, so tests reflect production behavior. 

##### **Q. What should a unit test not do?** 

**A.** Hit the network, a real database or the filesystem, depend on test order, or test framework internals. It should be fast, isolated and deterministic. 

Page 51 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 10 — DevOps: Git, Docker, Kubernetes, CI/CD** 

### **Git** 

<mark>git clone <url> && cd repo git checkout -b feature/loan-repayment         # create branch git add -p && git commit -m "feat(loan): add repayment endpoint" git fetch origin && git rebase origin/main      # update branch with latest main git push -u origin feature/loan-repayment       # then open a Pull Request git stash / git stash pop                       # shelve work in progress git log --oneline --graph --all git dif main...HEAD</mark> f <mark>git revert <sha>                                # new commit that undoes <sha> (safe on shared branches) git reset --soft HEAD~1                         # undo last commit, keep changes staged (local only) git restore fle.txt                            # discard working changes to f</mark> i i <mark>le git cherry-pick <sha>                           # apply one commit onto current branch git tag -a v1.2.0 -m "Release 1.2.0" && git push --tags</mark> 

- **Merge** preserves history with a merge commit. **Rebase** rewrites your commits on top of the target for a linear history. Never rebase shared/public branches. **Squash merge** turns a PR into one commit. 

- **GitFlow:** main, develop, feature/*, release/*, hotfix/*. **Trunk-based:** short-lived branches merged to main often, using feature flags. It is simpler and CI-friendly. 

- **Conflicts:** git shows <<<<<<< ======= >>>>>>> markers. Edit, then git add and continue the merge or rebase. 

- **Conventional commits:** feat:, fix:, docs:, refactor:, test:, chore:. **SemVer:** MAJOR.MINOR.PATCH. 

### **Linux and Servers** 

<mark>ls -la; cd; pwd; mkdir -p; cp -r; mv; rm -rf cat / less / head -n 50 / tail -f app.log          # view and follow logs grep -rn "ERROR" /var/log/app/                     # search fnd / -name "*.jar" 2>/dev/null</mark> i <mark>ps aux | grep java; top; htop; kill -15 <pid>      # processes df -h; du -sh *; free -m                           # disk and memory chmod 640 fle; chown app:app f</mark> i i <mark>le                 # permissions (rwx for user/group/others) ss -tulpn | grep 8080                              # who listens on a port ssh -i key.pem user@server; scp app.jar user@server:/opt/app/ systemctl status|start|restart loan-service; journalctl -u loan-service -f curl -i http://localhost:8080/actuator/health</mark> server { <mark>listen 443 ssl; server_name app.pksf.org.bd; ssl_certifcate     /etc/ssl/certs/pksf.crt;</mark> i <mark>ssl_certifcate_key /etc/ssl/private/pksf.key;</mark> i <mark>root /usr/share/nginx/html;                         # React build location / { try_fles $uri /index.html; }          # SPA fallback for client routing</mark> i <mark>location /api/ { proxy_pass http://localhost:8080; proxy_set_header Host $host; proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; proxy_set_header X-Forwarded-Proto $scheme; } }</mark> 

### **Docker** 

- **Image:** a read-only template made of layers. **Container:** a running instance of an image with a writable layer. 

Page 52 of 63 

PKSF Full-stack Software Engineer — Study Material 

- **Docker vs VM:** containers share the host kernel, so they are lightweight and start in seconds. VMs include a full guest OS. 

- **Layer caching:** order Dockerfile instructions from least to most frequently changing (dependencies before source code). 

<mark># Spring Boot — multi-stage build FROM maven:3.9-eclipse-temurin-21 AS build WORKDIR /app COPY pom.xml . RUN mvn -q dependency:go-ofine          # cached layer unless pom changes</mark> fl <mark>COPY src ./src RUN mvn -q package -DskipTests FROM eclipse-temurin:21-jre-alpine WORKDIR /app RUN addgroup -S app && adduser -S app -G app COPY --from=build /app/target/*.jar app.jar USER app EXPOSE 8080 HEALTHCHECK CMD wget -qO- http://localhost:8080/actuator/health || exit 1 ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]</mark> # React — build then serve with Nginx <mark>FROM node:20-alpine AS build WORKDIR /app COPY package*.json ./ RUN npm ci COPY . . RUN npm run build FROM nginx:1.27-alpine COPY nginx.conf /etc/nginx/conf.d/default.conf COPY --from=build /app/dist /usr/share/nginx/html EXPOSE 80</mark> # docker-compose.yml — full local stack <mark>services: postgres: image: postgres:16-alpine environment: { POSTGRES_DB: pksf, POSTGRES_USER: pksf, POSTGRES_PASSWORD: pksf } volumes: [pgdata:/var/lib/postgresql/data] healthcheck: { test: ["CMD-SHELL", "pg_isready -U pksf"], interval: 5s, retries: 10 } redis: image: redis:7-alpine mongo: image: mongo:7 keycloak: image: quay.io/keycloak/keycloak:25.0 command: start-dev --import-realm environment: { KC_BOOTSTRAP_ADMIN_USERNAME: admin, KC_BOOTSTRAP_ADMIN_PASSWORD: admin } volumes: [./keycloak:/opt/keycloak/data/import] ports: ["8081:8080"] backend: build: ./backend environment: SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/pksf SPRING_DATASOURCE_USERNAME: pksf SPRING_DATASOURCE_PASSWORD: pksf SPRING_DATA_REDIS_HOST: redis SPRING_DATA_MONGODB_URI: mongodb://mongo:27017/audit SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI: http://keycloak:8080/realms/pksf depends_on: postgres: { condition: service_healthy } redis: { condition: service_started } ports: ["8080:8080"] frontend:</mark> 

Page 53 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>build: ./frontend ports: ["3000:80"] depends_on: [backend] volumes: pgdata:</mark> 

docker build -t pksf/loan-service:1.0 . <mark>docker run -d -p 8080:8080 --name loan pksf/loan-service:1.0 docker ps; docker logs -f loan; docker exec -it loan sh; docker stop loan; docker rm loan docker images; docker rmi <img>; docker system prune docker compose up -d --build; docker compose logs -f backend; docker compose down -v</mark> 

- **Volumes:** persist data outside the container lifecycle. **Bind mounts** map host paths. **Networks:** containers on the same compose network reach each other by service name (postgres:5432). 

- **Best practices:** small base images (alpine/distroless/JRE-only), a .dockerignore (target, node_modules, .git), a non-root USER, pinned versions, one process per container, no secrets in images, and image scanning (Trivy). 

- **CMD vs ENTRYPOINT:** ENTRYPOINT is the fixed executable. CMD supplies default arguments that can be overridden. **COPY vs ADD:** prefer COPY; ADD also extracts archives and fetches URLs. 

### **Kubernetes** 

- **Control plane:** kube-apiserver (front door), etcd (cluster state store), scheduler (places Pods on nodes), controller-manager (reconciliation loops). 

- **Worker node:** kubelet (runs Pods), kube-proxy (Service networking), container runtime (containerd). 

- **Pod:** the smallest deployable unit (one or more containers sharing network and storage). **ReplicaSet** keeps N Pods running. **Deployment** manages ReplicaSets for rolling updates and rollbacks. 

- **Service:** a stable virtual IP/DNS name for a set of Pods. **ClusterIP** is internal, **NodePort** exposes a port on each node, and **LoadBalancer** creates a cloud LB. **Ingress** does HTTP routing by host/path (with an ingress controller such as NGINX). 

- **ConfigMap** holds non-secret config. **Secret** holds sensitive data (base64-encoded, not encrypted by default; enable encryption at rest). 

- **StatefulSet:** stable identity and storage for databases. **PersistentVolume/PersistentVolumeClaim:** storage abstraction. **DaemonSet:** one Pod per node (log agents). **Job/CronJob:** batch tasks. 

<mark>apiVersion: apps/v1 kind: Deployment metadata: { name: loan-service, labels: { app: loan-service } } spec: replicas: 3 selector: { matchLabels: { app: loan-service } } strategy: { type: RollingUpdate, rollingUpdate: { maxUnavailable: 0, maxSurge: 1 } } template: metadata: { labels: { app: loan-service } } spec: containers: - name: app image: registry.pksf.org.bd/loan-service:1.4.2 ports: [{ containerPort: 8080 }] envFrom: - conf</mark> i <mark>gMapRef: { name: loan-confg }</mark> i <mark>- secretRef: { name: loan-secrets } resources: requests: { cpu: "250m", memory: "512Mi" } limits: { memory: "1Gi" } readinessProbe: httpGet: { path: /actuator/health/readiness, port: 8080 } initialDelaySeconds: 20 livenessProbe: httpGet: { path: /actuator/health/liveness, port: 8080 } initialDelaySeconds: 40</mark> 

Page 54 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>apiVersion: v1 kind: Service metadata: { name: loan-service } spec: selector: { app: loan-service } ports: [{ port: 80, targetPort: 8080 }] --apiVersion: networking.k8s.io/v1 kind: Ingress metadata: { name: pksf-ingress } spec: ingressClassName: nginx rules: - host: app.pksf.org.bd http: paths: - { path: /api/v1/loans, pathType: Prefx, backend: { service: { name: loan-service, port: { number: 80 } } } }</mark> i <mark>- { path: /, pathType: Prefx, backend: { service: { name: frontend, port: { number: 80 } } } }</mark> i <mark>--apiVersion: autoscaling/v2 kind: HorizontalPodAutoscaler metadata: { name: loan-service } spec: scaleTargetRef: { apiVersion: apps/v1, kind: Deployment, name: loan-service } minReplicas: 2 maxReplicas: 10 metrics: [{ type: Resource, resource: { name: cpu, target: { type: Utilization, averageUtilization: 70 } } }]</mark> 

- **Probes:** a failed **readiness** check removes the Pod from Service endpoints (no traffic). A failed **liveness** check restarts the container. A **startup** probe protects slow-starting apps. 

- **Requests** are used for scheduling. **Limits** are the maximum: exceeding the memory limit kills the container (OOMKilled), and exceeding the CPU limit throttles it. 

<mark>kubectl get pods -o wide; kubectl get deploy,svc,ingress kubectl describe pod <pod>                      # events: ImagePullBackOf, CrashLoopBackOf</mark> f <mark>, OOMKilled</mark> f <mark>kubectl logs -f <pod> [-c container] [--previous] kubectl exec -it <pod> -- sh kubectl apply -f k8s/; kubectl delete -f k8s/ kubectl rollout status deploy/loan-service; kubectl rollout undo deploy/loan-service kubectl scale deploy/loan-service --replicas=5 kubectl port-forward svc/loan-service 8080:80 kubectl create secret generic loan-secrets --from-literal=DB_PASSWORD=...</mark> 

- **Troubleshooting:** CrashLoopBackOff (app crashes: check logs --previous), ImagePullBackOff (wrong image or registry credentials), Pending (insufficient resources or unbound PVC), OOMKilled (raise the memory limit or fix a leak). 

- **Practice locally** with minikube, kind or k3d. **Helm** is a package manager that templates manifests (charts, values.yaml). 

### **CI/CD** 

- **CI (Continuous Integration):** every push is built and tested automatically. **CD:** continuous delivery means always deployable with a manual approval; continuous deployment means automatic deployment to production. 

- **Typical stages:** checkout → build → unit tests → integration tests → static analysis/security scan → build Docker image → push to registry → deploy to dev/staging → smoke tests → approval → production. 

<mark># .github/workfows/ci.yml</mark> l <mark>name: CI on: push: { branches: [main] } pull_request: jobs: backend:</mark> 

Page 55 of 63 

PKSF Full-stack Software Engineer — Study Material 

<mark>runs-on: ubuntu-latest defaults: { run: { working-directory: backend } } steps: - uses: actions/checkout@v4 - uses: actions/setup-java@v4 with: { distribution: temurin, java-version: "21", cache: maven } - run: mvn -B verify                       # unit + Testcontainers integration tests - uses: actions/upload-artifact@v4 with: { name: jacoco, path: backend/target/site/jacoco } frontend: runs-on: ubuntu-latest defaults: { run: { working-directory: frontend } } steps: - uses: actions/checkout@v4 - uses: actions/setup-node@v4 with: { node-version: "20", cache: npm, cache-dependency-path: frontend/package-lock.json } - run: npm ci && npm run lint && npm test -- --run && npm run build docker: if: github.ref == 'refs/heads/main' needs: [backend, frontend] runs-on: ubuntu-latest permissions: { contents: read, packages: write } steps: - uses: actions/checkout@v4 - uses: docker/login-action@v3 with: { registry: ghcr.io, username: "${{ github.actor }}", password: "${{ secrets.GITHUB_TOKEN }}" } - uses: docker/build-push-action@v6 with: { context: backend, push: true, tags: "ghcr.io/${{ github.repository }}/backend:${{ github.sha }}" }</mark> // Jenkinsfile (declarative) — common in on-premise government setups <mark>pipeline { agent any stages { stage('Build & Test') { steps { sh 'mvn -B clean verify' } } stage('Sonar')        { steps { withSonarQubeEnv('sonar') { sh 'mvn sonar:sonar' } } }</mark> stage('Docker')       { steps { sh 'docker build -t registry/loan:$BUILD_NUMBER . && docker push registry/loan: $BUILD_NUMBER' } } stage('Deploy')       { when { branch 'main' } steps { sh 'kubectl set image deploy/loan-service app=registry/loan: $BUILD_NUMBER' } } <mark>} post { failure { echo 'Build failed' } } }</mark> 

- **Deployment strategies: Rolling** replaces Pods gradually (the K8s default). **Blue-green** runs two environments and switches traffic, with instant rollback. **Canary** sends a small percentage of traffic to the new version first. **Recreate** stops old then starts new (downtime). 

- **Environment promotion:** build the image once and promote the same image through dev → staging → prod. Inject config per environment via ConfigMaps, Secrets or profiles. 

### **Phase 10 — Interview Questions** 

##### **Q. Merge vs rebase?** 

**A.** Merge combines histories with a merge commit and preserves context. Rebase replays your commits on top of another branch for linear history, but rewrites commits, so avoid it on shared branches. 

##### **Q. How do you reduce Docker image size?** 

**A.** Use multi-stage builds, a JRE-only or alpine/distroless base, a .dockerignore, fewer layers, clearing package caches, and copying only the final artifact. 

##### **Q. Liveness vs readiness probe?** 

**A.** Readiness decides whether the Pod should receive traffic (e.g. still warming up or the DB is unreachable). Liveness decides whether it should be restarted (e.g. deadlocked). Spring Boot Actuator exposes both. 

Page 56 of 63 

PKSF Full-stack Software Engineer — Study Material 

##### **Q. Deployment vs StatefulSet?** 

**A.** A Deployment is for stateless, interchangeable Pods. A StatefulSet gives stable network IDs, ordered rollout and per-Pod persistent volumes, which suits databases and brokers. 

##### **Q. How do you roll back a bad release in Kubernetes?** 

**A.** kubectl rollout undo deployment/name (or redeploy the previous image tag via CI). With blue-green, switch traffic back. 

##### **Q. What is the difference between CI, continuous delivery and continuous deployment?** 

**A.** CI automatically builds and tests every change. Continuous delivery keeps every change releasable, with a manual production approval. Continuous deployment releases every passing change to production automatically. 

Page 57 of 63 

PKSF Full-stack Software Engineer — Study Material 

**Phase 11 — Cloud (Optional)** 

- **IaaS** (VMs: EC2), **PaaS** (managed runtime: Elastic Beanstalk, App Service), **SaaS** (finished software). Regions contain multiple Availability Zones; deploy across AZs for high availability. 

- **AWS core:** EC2 (VMs), S3 (object storage for documents and backups), RDS (managed PostgreSQL), IAM (users, roles, least-privilege policies), VPC (private networking, subnets, security groups), ECR (container registry), EKS (managed Kubernetes), CloudWatch (logs and metrics). 

- **Azure equivalents:** VM, Blob Storage, Azure Database for PostgreSQL, Entra ID, VNet, ACR, AKS. **GCP:** Compute Engine, Cloud Storage, Cloud SQL, IAM, GKE. 

- **Simple deploy path:** push the image to ECR, then run it on ECS Fargate or EKS with RDS PostgreSQL, ElastiCache Redis, an ALB and HTTPS via ACM. Store secrets in Secrets Manager. 

- **Certifications:** AWS Certified Developer – Associate, AWS Solutions Architect – Associate, Azure Developer Associate (AZ-204). 

Page 58 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 12 — System Design** 

### **Fundamentals** 

- **Vertical scaling** adds a bigger machine. **Horizontal scaling** adds more instances, which requires stateless services (sessions in Redis or JWT). 

- **Load balancers:** round robin, least connections, IP hash. Health checks remove bad instances. 

- **Database scaling:** indexes and query tuning first, then caching, read replicas for reports, partitioning big tables, and finally sharding. 

- **Caching layers:** browser/CDN (static assets), API gateway, application (Redis), database buffer cache. 

- **Asynchronous processing:** queues for heavy work (report generation, SMS, bulk imports). 

### **Approach to Any Design Question** 

- **1. Clarify requirements:** functional (features) and non-functional (users, data volume, latency, availability, security). 

- **2. Estimate:** e.g. 200 POs × 20,000 members = 4M members; weekly installments mean about 4M payment rows/week, or about 200M/year, so partition by date. 

- **3. High-level design:** clients, gateway, services, databases, cache, queue. 

- **4. Data model and APIs.** 

- **5. Deep dive:** bottlenecks, consistency, security, failure handling. 

- **6. Trade-offs** and future improvements. 

### **Case Study: PO Loan Management System** 

- **Functional requirements:** onboard POs and members, loan application → approval → disbursement, installment schedules, repayment collection, overdue tracking, reports (PO portfolio, PAR), audit trail, rolebased access (PKSF admin, PO officer, auditor). 

- **Non-functional requirements:** accurate money handling, no double disbursement, high availability during office hours, sub-second responses for common screens, PO data isolation, full auditability, data retention. 

- **Architecture:** React SPA → Nginx/Ingress → API Gateway (JWT via Keycloak) → modular Spring Boot services (PO, Member, Loan, Repayment, Reporting, Notification) → PostgreSQL (primary + read replica) + Redis (cache, rate limit) + MongoDB (audit/events) + RabbitMQ (async events) → Prometheus/Grafana + ELK. 

- **Data model:** pos, members, loan_products, loans, installments, repayments, ledger_entries (append-only, partitioned by date), users/roles in Keycloak with a po_id attribute. 

<mark>POST /api/v1/loans                      create application POST /api/v1/loans/{id}/approve         approve (role: PKSF/PO manager) POST /api/v1/loans/{id}/disburse        disburse (Idempotency-Key header) GET  /api/v1/loans/{id}/installments    schedule POST /api/v1/loans/{id}/repayments      record repayment GET  /api/v1/reports/par?poId=&asOf=    portfolio at risk</mark> 

- **Correctness:** use a DB transaction for repayment + ledger entry + installment update; @Version optimistic locking on the loan; an Idempotency-Key with a unique constraint on disbursement/repayment requests; NUMERIC money; reversal entries instead of edits. 

- **Performance:** composite indexes (loan_id, due_date) and (po_id, status); nightly materialized views for PAR and portfolio reports, refreshed concurrently; reports served from a replica; Redis caching of products and PO metadata; async Excel/PDF generation. 

- **Security:** Keycloak RBAC + PO scoping on every query (also enforceable with Postgres RLS), TLS, masking of NID/phone, audit of every state change, rate limiting, OWASP checks in CI. 

- **Reliability:** multiple replicas behind K8s, readiness/liveness probes, circuit breakers for external SMS/NID services, an outbox for events, daily backups + PITR, and DR documentation. 

Page 59 of 63 

PKSF Full-stack Software Engineer — Study Material 

### **Phase 12 — Interview Questions** 

##### **Q. How would you prevent double disbursement if the user clicks twice or the network retries?** 

- **A.** The client sends an Idempotency-Key. The server stores it with a unique constraint in the same transaction as the disbursement and returns the stored result for repeats. Also check the loan status transition (only APPROVED → DISBURSED) under optimistic locking. 

##### **Q. Reports are slowing down the transactional database. What do you do?** 

- **A.** Move reports to a read replica, precompute aggregates in materialized views or summary tables refreshed on a schedule, add suitable indexes, generate large reports asynchronously, and cache frequent results. 

Page 60 of 63 

PKSF Full-stack Software Engineer — Study Material 

## **Phase 13 — Portfolio Project** 

### **Project: PO Microfinance Management System** 

**Tip** The TOR explicitly prefers a public repo with clean modular full-stack code, unit/integration tests, a README and docker-compose. This project demonstrates every mandatory and preferred skill. 

<mark>pksf-microfnance/</mark> i 

- <mark>├── backend/                 Spring Boot 3, Java 21, Maven │ ├── src/main/java/...   po, member, loan, repayment, report, audit, confg, common</mark> i 

- <mark>│ ├── src/main/resources/db/migration (Flyway) │ ├── src/test/java       unit + Testcontainers ITs │ └── Dockerfle</mark> i <mark>├── frontend/                React 18 + TypeScript + Vite + MUI + TanStack Query │ └── Dockerfle</mark> i <mark>├── keycloak/realm-export.json ├── k8s/                     deployment, service, ingress, conf</mark> i <mark>gmap, hpa ├── docker-compose.yml ├── .github/workfows/ci.yml</mark> l <mark>└── README.md                architecture diagram, setup, screenshots, API docs link</mark> 

### **Milestones** 

|**#**|**Milestone**|**Skills shown**|
|---|---|---|
|1|Requirements, ER diagram, OpenAPI contract|Design, documentation|
|2|PO and Member CRUD with validation, pagination,<br>error handling|Spring Boot REST, JPA, DTOs|
|3|Loan lifecycle (apply, approve, disburse) + installment<br>schedule generation|Business logic, transactions, BigDecimal|
|4|Repayments, overdue job, ledger|@Scheduled, locking, idempotency|
|5|Keycloak login, roles ADMIN/PO_OFFICER/AUDITOR, PO<br>scoping|OAuth2, JWT, Spring Security|
|6|Redis caching for products/POs; Mongo audit log|Redis, MongoDB|
|7|React UI: login, dashboard with charts, tables, forms|React, TS, RHF+Zod, TanStack Query|
|8|Reports: PAR, portfolio by PO, Excel export; EXPLAIN-<br>tuned queries|SQL, indexing, Apache POI|
|9|Tests: JUnit/Mockito, MockMvc, Testcontainers, RTL|Testing|
|10|docker-compose, K8s manifests, GitHub Actions CI|Docker, K8s, CI/CD|
|11|README with architecture diagram, setup steps,<br>screenshots|Communication|



### **README Checklist** 

- A one-paragraph overview and a feature list. 

- An architecture diagram and tech stack badges. 

- Quick start: docker compose up -d, plus default login users for each role. 

- API docs link (Swagger UI) and sample requests. 

- How to run tests, and a coverage report. 

- Key design decisions: why NUMERIC, why the outbox, the indexing strategy with EXPLAIN before/after. 

- Screenshots of the UI. 

Page 61 of 63 

PKSF Full-stack Software Engineer — Study Material 

**Phase 14 — Exam and Interview Preparation** 

### **Written Test Strategy** 

- **MCQ:** expect fast recall of Java output prediction, OOP concepts, SQL clauses, normalization, OSI layers, HTTP codes, OS deadlock/scheduling and Git commands. Practice timed sets of 50. 

- **Handwritten code:** write complete, compiling Java: class, method signature, edge cases (null, empty). Narrate complexity in a comment. 

- **SQL:** joins, GROUP BY/HAVING, Nth highest, top-N per group, duplicates, running totals. 

- **Short answers:** structure each one as definition → how it works → example → trade-off. 

### **Java Output Prediction — Practice** 

<mark>String s = "a" + 1 + 2;  String t = 1 + 2 + "a"; System.out.println(s + " " + t);              // a12 3a</mark> 

<mark>int i = 5; i = i++ + ++i; System.out.println(i);   // 5 + 7 = 12 System.out.println(10 / 3 + " " + 10 % 3 + " " + 10.0 / 3);   // 3 1 3.3333333333333335 List<Integer> list = new ArrayList<>(List.of(1, 2, 3)); list.remove(Integer.valueOf(1));   // removes value 1 -> [2, 3] list.remove(0);                    // removes index 0 -> [3] static int f() { try { return 1; } fnally { System.out.println("f</mark> i <mark>nally"); } }   // prints f</mark> i <mark>nally, returns 1</mark> i <mark>Integer a = 1000, b = 1000; System.out.println(a == b);     // false (outside cache) System.out.println("hello".substring(1, 3));                 // el char c = 'A'; c += 1; System.out.println(c);                 // B</mark> 

### **Viva Preparation** 

- **Self-introduction (60–90 seconds):** education, years of experience, core stack (Java/Spring Boot/React/PostgreSQL), one key achievement with numbers, and why PKSF. 

- **Project walkthrough (3 minutes):** the problem, your role, architecture, tech choices and why, one hard problem you solved (performance, concurrency, security), and the result. 

- **Why PKSF?** Mission impact on poverty alleviation at national scale, building secure systems used by 200+ POs, and the chance to work across the full stack on meaningful, data-intensive government projects. 

- **Behavioral (STAR: Situation, Task, Action, Result):** a tight deadline, a production bug, disagreement with a teammate, learning a new technology quickly, mentoring someone. 

- **Questions to ask them:** current tech stack and deployment environment (on-prem or cloud), team size and process, the major upcoming systems, and code review/CI practices. 

### **Rapid-Fire Revision List** 

- Java: OOP pillars, SOLID, String immutability, equals/hashCode, HashMap internals, ArrayList vs LinkedList, checked vs unchecked, streams map/flatMap/groupingBy, volatile vs synchronized, JVM memory, GC. 

- Spring: IoC/DI, bean scopes, @SpringBootApplication, auto-config, profiles, @Transactional pitfalls and propagation, N+1, lazy vs eager, optimistic locking, @ControllerAdvice, validation, Actuator. 

- Security: authN vs authZ, filter chain, JWT structure, HS256 vs RS256, OAuth2 auth code + PKCE, client credentials, Keycloak realm/client/roles, CORS, CSRF, XSS, SQL injection, BCrypt. 

- DB: normalization, ACID, isolation levels, indexes (B-tree, composite, partial, GIN, BRIN), EXPLAIN ANALYZE, MVCC/VACUUM, keyset pagination, window functions, CTEs, MongoDB embedding vs referencing, aggregation. 

- Microservices: gateway, discovery, circuit breaker, saga, outbox, idempotency, observability, REST vs SOAP. 

Page 62 of 63 

PKSF Full-stack Software Engineer — Study Material 

- Redis: data types, cache-aside, invalidation, stampede, TTL/eviction, Spring Session, distributed lock. 

- React: virtual DOM, hooks rules, useEffect deps and cleanup, keys, controlled inputs, Context, TanStack Query, protected routes, event loop. 

- DevOps: Git merge vs rebase, Dockerfile multi-stage, compose, K8s 

Pod/Deployment/Service/Ingress/ConfigMap/Secret/probes/HPA, CI stages, deployment strategies. 

**Tip** Final tip: for every answer, connect the concept to PKSF's context — sensitive financial data, 200+ POs, reliability and auditability. It shows you understand the job, not just the technology. 

Page 63 of 63 

