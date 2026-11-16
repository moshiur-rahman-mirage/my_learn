import csv

rows = [
    {
        'level': '1',
        'type': 'MODULE',
        'name': 'PKSF Full-stack Software Engineer — Study Material',
        'description': 'Complete study guide and interview-ready prep plan',
        'priority': '',
        'completed': 'FALSE',
        'content': '# PKSF Full-stack Software Engineer\n\nThis study material is aligned with the PKSF TOR and the full-stack recruitment preparation path.\n\n## Role focus\n- Full-stack development with strong Java (Spring Boot) and React/Angular\n- Secure APIs, PostgreSQL, MongoDB, Redis, Docker/Kubernetes, Git, CI/CD\n- Data integrity, financial systems, microfinance domain understanding\n- Strong fundamentals in CS, Java, DBMS, security, and system design\n\n## TOR summary\n- Minimum 2 years of professional experience in full-stack software development\n- Degree requirement: Bachelor in CSE/IT or related discipline\n- Priorities: Java + Spring Boot, React, REST APIs, PostgreSQL, MongoDB, Redis, Docker/Kubernetes, Keycloak, OAuth2/JWT, CI/CD\n- Context: PKSF works with 200+ Partner Organizations and sensitive financial/project data\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 0 — Orientation & Strategy',
        'description': 'Understand the job, domain, and study process',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 0 — Orientation & Strategy\n\n## What the TOR asks for\n- Full-stack applications using Java and React/Angular\n- Backend with Spring Boot, REST, secure auth, PostgreSQL, MongoDB\n- Containerization, Git, CI/CD, cloud knowledge optional\n- PKSF context: development partner for NGOs/MFIs and financial data handling\n\n## Microfinance vocabulary\n- PO = Partner Organization\n- Member/Borrower = end user in a PO group\n- Loan disbursement = releasing funds\n- Installment = scheduled repayment\n- Outstanding = remaining principal\n- Overdue = missed installment\n- PAR = portfolio at risk\n- Savings and service charge are common financial concepts\n\n## Selection process\n- Written test: MCQ, short coding, SQL, theory\n- Viva: projects, Java internals, DB design, security, PKSF relevance\n- Study habit: daily blocks, spaced revision, mock practice, progress tracking\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'TOR and Core Expectations',
        'description': 'Recruitment requirements and domain context',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## TOR and core expectations\n\n- Minimum 2 years of professional experience\n- Strong hands-on Java (Spring Boot) and React\n- Prefer public code portfolio with tests, README, and deployment notes\n- Prefer microservices, OAuth2/JWT/Keycloak, PostgreSQL, MongoDB, Redis, Docker, Kubernetes, CI/CD\n- Data is financial and sensitive; expect questions on security, compliance, auditability, and performance\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Microfinance Domain Knowledge',
        'description': 'Core PKSF and financial-domain concepts',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Microfinance domain knowledge\n\n- Loans, savings, disbursement, installment cycles\n- Overdue recovery and portfolio-at-risk\n- POs operating as partner organizations\n- Design decisions should reflect financial integrity and audit trails\n\n> Tip: in design questions, model loans, installments, and transactions using NUMERIC money values, immutable ledgers, and audit tables.\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Study Method and Revision Routine',
        'description': 'How to prepare for written test and viva',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## Study method\n\n- Study in focused daily blocks\n- Do MCQ and short-answer revision every day\n- Solve coding questions by hand\n- Use weekly mock tests and revision cycles\n- Keep a tracker for Java, Spring Boot, React, database, security, and system design\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 1 — CS Fundamentals',
        'description': 'Theory base for written tests and viva',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 1 — CS Fundamentals\n\n## Data structures\n\n| Structure | Access | Search | Insert | Delete | Notes |\n|---|---|---|---|---|---|\n| Array | O(1) | O(n) | O(n) | O(n) | contiguous memory |\n| Linked list | O(n) | O(n) | O(1) at head | O(1) with node ref | no random access |\n| Stack/Queue | - | O(n) | O(1) | O(1) | LIFO/FIFO |\n| Hash table | - | O(1) avg | O(1) avg | O(1) avg | O(n) worst case |\n| BST (balanced) | - | O(log n) | O(log n) | O(log n) | worst O(n) if unbalanced |\n| Heap | O(1) top | O(n) | O(log n) | O(log n) | priority queue |\n\n### Linked list: reverse and detect cycle\n\n```java\nListNode reverse(ListNode head) {\n    ListNode prev = null, cur = head;\n    while (cur != null) {\n        ListNode next = cur.next;\n        cur.next = prev;\n        prev = cur;\n        cur = next;\n    }\n    return prev;\n}\n\nboolean hasCycle(ListNode head) {\n    ListNode slow = head, fast = head;\n    while (fast != null && fast.next != null) {\n        slow = slow.next;\n        fast = fast.next.next;\n        if (slow == fast) return true;\n    }\n    return false;\n}\n```\n\n### Stack: balanced parentheses\n\n```java\nboolean isBalanced(String s) {\n    Deque<Character> st = new ArrayDeque<>();\n    for (char c : s.toCharArray()) {\n        if (c == '(' || c == '{' || c == '[') {\n            st.push(c);\n        } else {\n            if (st.isEmpty()) return false;\n            char open = st.pop();\n            if ((c == ')' && open != '(') || (c == '}' && open != '{') || (c == ']' && open != '[')) {\n                return false;\n            }\n        }\n    }\n    return st.isEmpty();\n}\n```\n\n### Trees and traversals\n- Inorder = sorted order for BST\n- Preorder = serialize/copy tree\n- Postorder = delete/free tree\n- Level order = BFS using queue\n\n### Graphs\n- BFS = shortest path in unweighted graphs\n- DFS = cycle detection, connected components, topological sort\n- Topological sort = use Kahn\'s algorithm with in-degree and queue\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Algorithms and Complexity',
        'description': 'Sorting, searching, and algorithm thinking',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Algorithms and complexity\n\n| Algorithm | Best | Average | Worst | Space | Stable |\n|---|---|---|---|---|---|\n| Bubble sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes |\n| Insertion sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes |\n| Selection sort | O(n^2) | O(n^2) | O(n^2) | O(1) | No |\n| Merge sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |\n| Quick sort | O(n log n) | O(n log n) | O(n^2) | O(log n) | No |\n| Heap sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No |\n| Binary search | O(1) | O(log n) | O(log n) | O(1) | - |\n\n### Key patterns\n- Binary search\n- Two pointers\n- Sliding window\n- Merge sort / quick sort\n- Dynamic programming\n- Greedy logic\n\n```java\nint binarySearch(int[] a, int target) {\n    int lo = 0, hi = a.length - 1;\n    while (lo <= hi) {\n        int mid = lo + (hi - lo) / 2;\n        if (a[mid] == target) return mid;\n        if (a[mid] < target) lo = mid + 1;\n        else hi = mid - 1;\n    }\n    return -1;\n}\n```\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'OOP and Software Design',
        'description': 'Object-oriented design and clean architecture',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## OOP and software design\n\n### Four pillars\n- Encapsulation: hide internal state\n- Abstraction: expose behavior, hide implementation\n- Inheritance: is-a relationship\n- Polymorphism: override and overload behavior\n\n### SOLID\n- Single Responsibility\n- Open/Closed\n- Liskov Substitution\n- Interface Segregation\n- Dependency Inversion\n\n### Design principles\n- Prefer composition over inheritance\n- Keep classes small and focused\n- Separate service logic, validation, persistence, and controllers\n- Use DTOs and domain models carefully\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Operating Systems and Networks',
        'description': 'Fundamentals for backend and system discussions',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## Operating systems and networking\n\n### OS topics\n- Process and thread management\n- Scheduling and memory management\n- Concurrency, synchronization, deadlocks\n- I/O and filesystem basics\n\n### Networking topics\n- TCP/UDP\n- HTTP and HTTPS\n- Routing, DNS, load balancing\n- Sockets and distributed communication patterns\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'DBMS Fundamentals',
        'description': 'Transactions, normalization, indexing, and query design',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## DBMS fundamentals\n\n- ACID properties\n- Normalization and denormalization trade-offs\n- Primary key, foreign key, constraints\n- Indexing and query optimization\n- Transactions and isolation levels\n\n> WHERE filters before grouping; HAVING filters after grouping.\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 2 — Core Java',
        'description': 'Java fundamentals and JVM internals',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 2 — Core Java\n\n## Java language basics\n- JDK = JRE + development tools\n- JRE = JVM + libraries\n- JVM runs bytecode and JIT compiles hot code\n- Primitives: byte, short, int, long, float, double, char, boolean\n- Wrappers are needed for generics and null values\n\n### Money and BigDecimal\n```java\nBigDecimal a = new BigDecimal("0.1");\nBigDecimal b = new BigDecimal("0.2");\nSystem.out.println(a.add(b)); // 0.3\n\nBigDecimal inst = new BigDecimal("10000")\n    .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);\nSystem.out.println(inst); // 833.33\n```\n\n### String immutability\n- Strings are immutable and final\n- String literals go to the string pool\n- Use StringBuilder for concatenation inside loops\n\n```java\nString s1 = "pksf";\nString s2 = "pksf";\nString s3 = new String("pksf");\nSystem.out.println(s1 == s2); // true\nSystem.out.println(s1.equals(s3)); // true\n```\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Java Core Concepts',
        'description': 'Generics, access modifiers, exceptions, records',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Java core concepts\n\n### equals and hashCode\n- If a.equals(b), then hash codes must match\n- Override both together when using HashMap/HashSet\n\n### Access modifiers\n| Modifier | Class | Package | Subclass | World |\n|---|---|---|---|---|\n| private | Yes | No | No | No |\n| default | Yes | Yes | No | No |\n| protected | Yes | Yes | Yes | No |\n| public | Yes | Yes | Yes | Yes |\n\n### Exceptions\n- Checked: must be handled or declared\n- Unchecked: runtime exceptions\n- Errors: OOM, StackOverflowError, do not catch\n\n### Generics and PECS\n- Producer Extends\n- Consumer Super\n\n```java\npublic static <T extends Comparable<T>> T max(List<T> list) {\n    T best = list.get(0);\n    for (T t : list) {\n        if (t.compareTo(best) > 0) best = t;\n    }\n    return best;\n}\n```\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Collections Framework',
        'description': 'List, Set, Map, Queue, comparator, fail-fast behavior',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Collections framework\n\n| Type | Implementation | Ordered | Sorted | Nulls | Notes |\n|---|---|---|---|---|---|\n| List | ArrayList | Yes | No | Yes | O(1) index access |\n| List | LinkedList | Yes | No | Yes | O(n) index access |\n| Set | HashSet | No | No | One null | O(1) average |\n| Set | TreeSet | Sorted | Yes | No | O(log n) |\n| Map | HashMap | No | No | One null key | O(1) average |\n| Map | LinkedHashMap | Insertion/access | No | One null key | O(1) |\n| Map | TreeMap | Sorted | Yes | No | O(log n) |\n| Map | ConcurrentHashMap | No | No | No nulls | thread-safe |\n\n### HashMap internals\n- array of buckets\n- default capacity 16, load factor 0.75\n- collisions resolved by chains or red-black tree\n- rehash occurs when size exceeds threshold\n\n### LRU cache\n```java\nclass LruCache<K, V> extends LinkedHashMap<K, V> {\n    private final int capacity;\n\n    LruCache(int capacity) {\n        super(16, 0.75f, true);\n        this.capacity = capacity;\n    }\n\n    @Override\n    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {\n        return size() > capacity;\n    }\n}\n```\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Functional Java and Streams',
        'description': 'Lambda, stream pipelines, and functional patterns',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## Functional Java and streams\n\n- Functional interfaces contain one abstract method\n- Method references simplify code\n- Streams are lazy and one-time use\n- Use collect, reduce, filter, map, and sorted carefully\n\n```java\nList<Integer> nums = List.of(1, 2, 3, 4);\nint sum = nums.stream().filter(n -> n % 2 == 0).mapToInt(Integer::intValue).sum();\n```\n\n### Common stream use cases\n- group by\n- aggregate and reduce\n- flatMap for nested collections\n- partitioning based on conditions\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Concurrency and Multithreading',
        'description': 'Threads, synchronization, locks, executors',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Concurrency and multithreading\n\n- Thread safety and shared mutable state\n- synchronized, volatile, ReentrantLock\n- ExecutorService, ThreadPoolExecutor\n- race conditions and deadlocks\n- avoid unnecessary sharing across threads\n\n### Thread safety rule\n- Shared mutable state must be protected or immutable\n- Use thread-safe collections when multiple threads access data\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'JVM Internals and Modern Java',
        'description': 'Memory model, GC, records, sealed types',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## JVM internals and modern Java\n\n- Heap, stack, metaspace\n- JIT compilation and garbage collection\n- Class loading and object lifecycle\n- Java 17/21 features: records, sealed classes, switch expressions, pattern matching\n\n### Modern Java notes\n- Prefer records for DTOs\n- Use `final` and immutable objects when possible\n- Understand memory behavior and GC cost in production systems\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Build Tools and Maven',
        'description': 'Build lifecycle, dependency management',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## Maven and build tools\n\n- Manage project dependencies\n- Run lifecycle phases: clean, compile, test, package, install\n- Use Maven plugins and profiles\n- Keep build reproducible and consistent across environments\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 3 — Spring and Spring Boot',
        'description': 'Core backend development stack',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 3 — Spring and Spring Boot\n\n## Spring Core\n- Dependency injection\n- Bean lifecycle\n- ApplicationContext and BeanFactory\n- configuration via annotations and XML\n\n## Spring Boot Essentials\n- Auto-configuration\n- starters\n- application.properties / application.yml\n- actuator and production readiness\n\n## Building REST APIs\n- Controller, Service, Repository, DTO\n- validation with Bean Validation\n- request/response design\n- HTTP status codes and error handling\n\n```java\n@RestController\n@RequestMapping("/loans")\npublic class LoanController {\n    private final LoanService loanService;\n\n    public LoanController(LoanService loanService) {\n        this.loanService = loanService;\n    }\n\n    @GetMapping\n    public List<LoanDto> getAll() {\n        return loanService.getAll();\n    }\n}\n```\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Spring Core and Boot',
        'description': 'Beans, DI, and app startup',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Spring Core and Boot\n\n- Understand @Component, @Service, @Repository, @Configuration\n- Learn constructor injection and bean scopes\n- Understand Spring Boot auto-configuration and starter dependencies\n- Use application configuration for environment-aware behavior\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'REST API Design',
        'description': 'Designing clean APIs for business systems',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## REST API design\n\n- Use resource-oriented URLs\n- Use nouns not verbs\n- Keep API consistent across entities\n- Use pagination, sorting, filtering, and error contracts\n- Validate inputs and return meaningful status codes\n\n### Example\n```http\nGET /api/loans?status=ACTIVE&page=1&size=20\n```\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Spring Data JPA and Hibernate',
        'description': 'ORM, entity mapping, queries, transactions',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Spring Data JPA and Hibernate\n\n- Entities and relationships\n- Repository interfaces and query methods\n- lazy vs eager loading\n- transactions and isolation\n- @OneToMany, @ManyToOne, @ManyToMany\n\n### Common rules\n- Use DTOs for API responses\n- Keep entity mapping clear and explicit\n- Avoid N+1 query problems\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Other Spring Capabilities',
        'description': 'Security, scheduling, validation, etc.',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## Other Spring capabilities\n\n- Scheduling with @Scheduled\n- Validation and Bean Validation\n- Exception handling\n- Integration-based patterns and monitoring\n- Production-grade application configuration\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 4 — Databases',
        'description': 'Relational and NoSQL data systems',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 4 — Databases\n\n## SQL mastery\n\n### Sample schema\n```sql\npos(id, name, district)\nmembers(id, po_id, name, nid, phone, joined_on)\nloans(id, member_id, amount, status, disbursed_on)\ninstallments(id, loan_id, due_date, amount_due, amount_paid, paid_on)\nemployees(id, name, dept_id, salary, manager_id)\n```\n\n### SQL patterns\n```sql\nSELECT m.name, p.name AS po_name\nFROM members m\nJOIN pos p ON p.id = m.po_id;\n\nSELECT p.name, SUM(l.amount) AS total\nFROM loans l\nJOIN members m ON m.id = l.member_id\nJOIN pos p ON p.id = m.po_id\nWHERE l.status = 'ACTIVE'\nGROUP BY p.name\nHAVING SUM(l.amount) > 10000000;\n```\n\n### Key topics\n- JOIN types\n- GROUP BY and HAVING\n- subqueries and CTEs\n- window functions\n- row_number, rank, dense_rank\n- UPSERT and delete duplicates\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'PostgreSQL Schema Design',
        'description': 'Designing finance-friendly database models',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## PostgreSQL schema design\n\n| Need | Use | Avoid |\n|---|---|---|\n| Money | NUMERIC(14,2) | float, double, money type |\n| Timestamps | TIMESTAMPTZ | TIMESTAMP without timezone for event data |\n| IDs | BIGINT identity / UUID | INT for large tables |\n| Flexible attributes | JSONB with GIN index | EAV schema |\n| Status | VARCHAR + CHECK or lookup table | magic integers |\n\n### Example finance schema\n```sql\nCREATE TABLE loans (\n    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n    member_id BIGINT NOT NULL REFERENCES members(id),\n    principal NUMERIC(14,2) NOT NULL CHECK (principal > 0),\n    service_charge_rate NUMERIC(5,2) NOT NULL,\n    status VARCHAR(20) NOT NULL CHECK (status IN (\'PENDING\', \'ACTIVE\', \'CLOSED\', \'WRITTEN_OFF\')),\n    disbursed_on DATE,\n    version BIGINT NOT NULL DEFAULT 0\n);\n```\n\n### Design principles\n- Prefer RESTRICT and soft delete for financial data\n- Use append-only ledger for immutable transactions\n- Use partitioning for high-volume date-based tables\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'PostgreSQL Indexing and Query Tuning',
        'description': 'Indexes, EXPLAIN, and optimization techniques',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## PostgreSQL indexing and tuning\n\n- B-tree default index\n- composite index follows leftmost-prefix rule\n- partial and expression indexes\n- covering indexes and INCLUDE clause\n- GIN for JSONB and arrays\n\n### Query tuning\n- Use EXPLAIN ANALYZE\n- Prefer proper indexes over full scans\n- Keep equality columns first in composite indexes\n- Understand join order and filter selectivity\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'MongoDB and NoSQL',
        'description': 'Document-based storage and trade-offs',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## MongoDB and NoSQL\n\n- Good for flexible, document-oriented workloads\n- Useful when schema evolves quickly\n- Not a replacement for relational DBs in all financial workflows\n- Use indexes and understand read/write trade-offs\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 5 — APIs, Integration and Microservices',
        'description': 'Service design and distributed systems',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 5 — APIs, Integration and Microservices\n\n## REST and integration\n- API design with consistent resource names\n- JSON, XML, and SOAP patterns\n- pagination, filtering, validation, error handling\n- synchronous vs asynchronous integration\n\n## Microservices\n- Split services by bounded context\n- Understand trade-offs around network latency and eventual consistency\n- Use service discovery and graceful degradation\n\n## Messaging\n- producer/consumer patterns\n- retries and idempotency\n- async processing and event-driven architecture\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'REST API Design and Data Formats',
        'description': 'API contracts and payload design',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## REST API design and data exchange\n\n- Resource-oriented URLs\n- Accept JSON as the default format\n- XML/SOAP where required by legacy integrations\n- define clear error responses\n- version APIs if needed\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Microservices Architecture',
        'description': 'Bounded context and distributed system trade-offs',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Microservices architecture\n\n- service boundaries must reflect business capability\n- keep data ownership clear\n- design for failure and partial outages\n- choose between orchestration and choreography carefully\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Messaging and Async Patterns',
        'description': 'Queues and distributed communication',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## Messaging and async patterns\n\n- Queue-based communication for decoupling\n- retries, dead-letter queues, idempotency\n- event sourcing and eventual consistency\n- important for high-throughput or background workflows\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 6 — Authentication and Security',
        'description': 'Identity, JWT, OAuth2, and secure app design',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 6 — Authentication and Security\n\n## Security fundamentals\n- injection risks\n- broken authentication and authorization\n- secure coding and secrets management\n- data protection and auditing\n\n## Spring Security\n- authentication and authorization\n- request filtering\n- CSRF protection\n- role-based access control\n\n## JWT and OAuth2\n- JWT structure: header, payload, signature\n- token validation, expiry, refresh flow\n- OAuth2 authorization code and client credentials patterns\n- OIDC for identity integration\n\n## Keycloak\n- enterprise identity provider\n- centralizes authentication and user roles\n- integrates with Spring Security and API gateways\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'JWT, OAuth2, and Keycloak',
        'description': 'Modern identity and authorization patterns',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## JWT, OAuth2, and Keycloak\n\n### JWT\n```java\nString token = jwtService.generateToken(user);\nJwtClaims claims = jwtDecoder.decode(token);\n```\n\n### OAuth2/OIDC\n- delegated authorization\n- client/server trust model\n- external identity providers\n\n### Keycloak\n- user federation\n- realm and client configuration\n- RBAC support\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Data Protection and Auditing',
        'description': 'Sensitive financial data handling',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## Data protection and auditing\n\n- encryption in transit and at rest\n- protect secrets and credentials\n- maintain audit trails for transactions\n- log key actions without leaking sensitive payloads\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 7 — Redis Caching and Sessions',
        'description': 'Cache performance and distributed session handling',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 7 — Redis Caching and Sessions\n\n## Redis fundamentals\n- in-memory data structure store\n- low-latency access\n- common use cases: cache, session store, pub/sub\n\n## Caching patterns\n- cache-aside\n- write-through\n- invalidate on update\n- TTL and stale data handling\n\n## Distributed sessions\n- use Redis with Spring Session\n- handle sticky sessions carefully\n- avoid cache stampedes and stale invalidation issues\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Redis, Caching, and Session Design',
        'description': 'High-performance data access patterns',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Redis and session design\n\n### Redis interview points\n- Why is Redis fast? It keeps data in memory, uses efficient structures, and minimizes lock contention.\n- How keep cache consistent with database? Update DB first, then invalidate or refresh cache.\n- How prevent cache stampede? Add TTL jitter, lock single refresh, or serve stale while refreshing.\n\n### Spring Session with Redis\n- centralize session storage\n- works across multiple service instances\n- suitable when stateless JWT is not enough\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 8 — Frontend with React',
        'description': 'Frontend essentials for full-stack engineers',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 8 — Frontend with React\n\n## HTML and CSS\n- semantic HTML\n- accessibility\n- flexbox and grid\n- responsive design with media queries\n\n## JavaScript fundamentals\n- closure\n- async/await\n- promises\n- event loop\n- destructuring and spread\n\n## TypeScript\n- interface and type aliases\n- union types\n- generics\n- strict typing\n\n## React core\n- components, props, state\n- hooks: useState, useEffect, useMemo, useCallback\n- controlled forms\n- routing and protected routes\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'React Hooks and State Management',
        'description': 'Managing UI state cleanly',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## React hooks and state management\n\n```jsx\nfunction LoanRow({ loan, onApprove }) {\n  return (\n    <tr>\n      <td>{loan.memberName}</td>\n      <td>{loan.amount}</td>\n      <td>{loan.status}</td>\n      <td>\n        {loan.status === "PENDING" && (\n          <button onClick={() => onApprove(loan.id)}>Approve</button>\n        )}\n      </td>\n    </tr>\n  );\n}\n```\n\n### React best practices\n- derive data instead of storing redundant state\n- cleanup effects and abort requests\n- use `useMemo` and `useCallback` carefully\n- prefer query libraries for server state\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'React Ecosystem',
        'description': 'Routing, API calls, and UI architecture',
        'priority': 'SHOULD',
        'completed': 'FALSE',
        'content': '## React ecosystem\n\n- React Router for routes and protected screens\n- TanStack Query for server state\n- axios instance with interceptors\n- component folders by feature\n\n```javascript\nconst api = axios.create({\n  baseURL: import.meta.env.VITE_API_URL,\n  timeout: 15000\n});\n```\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 9 — Testing and Code Quality',
        'description': 'Maintain quality and confidence in delivery',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 9 — Testing and Code Quality\n\n## Testing fundamentals\n- unit tests\n- integration tests\n- frontend component tests\n- test setup and coverage\n\n## Code quality\n- naming conventions\n- readable functions\n- maintainable abstractions\n- review practices\n\n## Core principle\nWell-tested systems are easier to deploy, extend, and debug in production.\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Backend and Frontend Testing',
        'description': 'Validation of services and UI behavior',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Testing\n\n- Test service logic and controllers\n- Validate API flows and validation errors\n- Write component tests for UI states and events\n- Keep tests focused and deterministic\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 10 — DevOps: Git, Docker, Kubernetes, CI/CD',
        'description': 'Deployment and automation fundamentals',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 10 — DevOps\n\n## Git\n- branching strategy\n- merge vs rebase\n- conflict resolution\n- commit hygiene\n\n## Docker and Kubernetes\n- containerize app and dependencies\n- use Docker Compose for local services\n- understand Pods, Deployments, Services, secrets, config maps\n\n## CI/CD\n- build pipelines\n- run tests in pipeline\n- deploy to staging and production safely\n\n## Linux and servers\n- shell basics\n- process management\n- permissions\n- server operations\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'Containerization and CI/CD',
        'description': 'Deployable, repeatable delivery workflow',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## Containerization and CI/CD\n\n```yaml\nservices:\n  api:\n    build: .\n    ports:\n      - "8080:8080"\n```\n\n- Docker packages app and dependencies\n- Kubernetes orchestrates deployment at scale\n- CI/CD ensures build, test, and deployment automation\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 12 — System Design',
        'description': 'Architecture and trade-off discussions',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 12 — System Design\n\n## Design fundamentals\n- throughput\n- latency\n- availability\n- reliability\n- partitioning and consistency\n\n## Interview approach\n1. Clarify requirements\n2. Define constraints\n3. Estimate scale\n4. Choose components\n5. Draw architecture\n6. Explain trade-offs\n\n## Case study: PO loan management system\n- loan origination\n- approval and disbursement\n- repayment tracking\n- reporting\n- auditing\n- role-based access\n'
    },
    {
        'level': '3',
        'type': 'TOPIC',
        'name': 'System Design Case Study',
        'description': 'Designing a microfinance management system',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '## PO loan management system case study\n\nA robust system should include:\n- member and PO management\n- loan application and approval\n- repayment ledger with immutable audit trail\n- reporting for portfolio health\n- secure role-based access\n- distributed cache for frequent reads\n- queue for async notifications and background processing\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 13 — Portfolio Project',
        'description': 'Build an end-to-end project aligned with PKSF domain',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 13 — Portfolio Project\n\n## Project idea\nBuild a PO microfinance management system with:\n- Spring Boot backend\n- React frontend\n- PostgreSQL schema\n- authentication with JWT or Keycloak\n- Dockerized deployment\n- README and deployment guidance\n\n## Milestones\n- domain model\n- database design\n- backend APIs\n- frontend screens\n- tests and security\n- deploy and document\n'
    },
    {
        'level': '2',
        'type': 'MODULE',
        'name': 'Phase 14 — Final Exam and Interview Preparation',
        'description': 'Written test, viva, and revision prep',
        'priority': 'MUST',
        'completed': 'FALSE',
        'content': '# Phase 14 — Final exam and interview preparation\n\n## Written test strategy\n- practice MCQs under time pressure\n- solve Java output prediction and DB queries\n- write code by hand and explain logic clearly\n\n## Viva preparation\n- explain project decisions\n- justify architecture and trade-offs\n- answer PKSF-specific questions with domain awareness\n\n## Rapid-fire revision list\n- Java fundamentals\n- Spring Boot and REST\n- SQL and PostgreSQL\n- React and TypeScript\n- Security and JWT\n- Docker, Kubernetes, CI/CD\n- Redis and cache patterns\n- system design and microfinance domain vocabulary\n'
    }
]

output_path = r'D:\mylearn\Plans\plan.csv'
with open(output_path, 'w', newline='', encoding='utf-8-sig') as f:
    writer = csv.DictWriter(f, fieldnames=['level', 'type', 'name', 'description', 'priority', 'completed', 'content'])
    writer.writeheader()
    for row in rows:
        writer.writerow(row)

print(f'wrote {len(rows)} rows to {output_path}')
