@@ 3 | TOPIC | Practice — Coding drill: JPA scenarios | Ten interview tasks: fix N+1, map relationships, write queries, repair transactions and prevent lost updates in OrderHub | MUST
# Coding drill: JPA scenarios

Ten tasks in the style of live-coding and code-review rounds. Each uses the OrderHub entities from this phase (`Product`, `Category`, `Customer`, `Order`, `OrderLine`, `Payment`). Try each one on paper or in your IDE first, then open the solution. For every task, say out loud which SQL Hibernate will run: interviewers care about that more than about the annotations.

## 1. Fix this N+1
The endpoint below runs 1 + 2N queries for N orders. Make it run at most 2 statements without changing the JSON.

```java
@Transactional(readOnly = true)
public List<OrderView> recentOrders() {
    return orderRepository.findTop50ByOrderByCreatedAtDesc().stream()
            .map(o -> new OrderView(o.getId(), o.getCustomer().getEmail(), o.getLines().size()))
            .toList();
}
```
Hint: the view needs only three values. Do you need entities at all?

<details><summary>Solution</summary>

```java
public record OrderView(Long id, String email, long lineCount) { }

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
           select new com.orderhub.order.OrderView(o.id, c.email, count(l))
           from Order o join o.customer c left join o.lines l
           group by o.id, c.email, o.createdAt
           order by o.createdAt desc
           limit 50
           """)
    List<OrderView> recentOrderViews();
}
```
One statement. `limit` is supported in HQL since Hibernate 6. If entities were required (for example to call domain methods), use `@EntityGraph(attributePaths = {"customer", "lines"})` on the derived method, or rely on `default_batch_fetch_size`: 1 query for orders, 1 for customers, 1 for lines. Changing the mapping to EAGER would not fix it.
</details>

## 2. Map a bidirectional one-to-many correctly
Write `Order` and `OrderLine` so that `orderRepository.save(order)` inserts the order and its lines, removing a line from the order deletes the row, and the foreign key is written exactly once per line.

Hint: owning side, `mappedBy`, cascade, orphan removal and a helper method.

<details><summary>Solution</summary>

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    public void addLine(Product product, int qty) {
        lines.add(new OrderLine(this, product, qty, product.getPrice()));   // sets both sides
    }
    public void removeLine(OrderLine line) {
        lines.remove(line);                                                // orphanRemoval -> DELETE
    }
    public List<OrderLine> getLines() { return Collections.unmodifiableList(lines); }
}

@Entity
@Table(name = "order_line")
public class OrderLine {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")          // owning side: writes order_id in the INSERT
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private BigDecimal unitPrice;

    protected OrderLine() { }
    OrderLine(Order order, Product product, int quantity, BigDecimal unitPrice) {
        this.order = order; this.product = product; this.quantity = quantity; this.unitPrice = unitPrice;
    }
}
```
The child owns the foreign key, so each line is one INSERT with `order_id` included and no follow-up UPDATE. `CascadeType.ALL` saves new lines with the order; orphan removal deletes removed lines. There is no cascade from `OrderLine` to `Product`.
</details>

## 3. Why is nothing saved?
```java
@Service
public class ProductService {
    public void rename(Long id, String name) {
        Product p = productRepository.findById(id).orElseThrow();
        p.rename(name);
    }
}
```
The name never changes in the database. Explain and fix with two different approaches.

<details><summary>Solution</summary>

`findById` runs in its own repository transaction, which ends when it returns, so `p` is detached; the change is never dirty-checked.

```java
// Fix A: one service-level transaction, dirty checking does the UPDATE
@Transactional
public void rename(Long id, String name) {
    productRepository.findById(id).orElseThrow().rename(name);
}

// Fix B: no transaction on the service, explicitly merge the detached entity
public void renameDetached(Long id, String name) {
    Product p = productRepository.findById(id).orElseThrow();
    p.rename(name);
    productRepository.save(p);          // merge: SELECT + UPDATE in a new transaction
}
```
Fix A is preferred: one transaction, two statements (SELECT, UPDATE), and the read and write are consistent. Fix B costs an extra SELECT and leaves a window where another transaction can change the row between the two transactions.
</details>

## 4. Write the derived queries
Declare `ProductRepository` methods for: products by category name ordered by price, the 5 cheapest active products, whether a SKU exists, active products with stock below a threshold as a `Slice`, and the count of discontinued products.

<details><summary>Solution</summary>

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoriesNameOrderByPriceAsc(String categoryName);
    List<Product> findTop5ByStatusOrderByPriceAsc(Product.Status status);
    boolean existsBySku(String sku);
    Slice<Product> findByStatusAndStockLessThan(Product.Status status, int threshold, Pageable pageable);
    long countByStatus(Product.Status status);
}
```
`findByCategoriesName...` joins through the many-to-many. `existsBySku` selects only an id with a limit of 1. `Slice` avoids the count query by fetching one extra row. All names are validated at startup.
</details>

## 5. Bulk update without stale data
Write a repository method that applies a percentage price increase to all products in a category and a service method that calls it and then returns the new price of one product from the same category. The returned price must be the new one.

Hint: bulk updates bypass the persistence context.

<details><summary>Solution</summary>

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update Product p set p.price = p.price * (1 + :pct / 100.0), p.version = p.version + 1
           where p.id in (select p2.id from Product p2 join p2.categories c where c.name = :category)
           """)
    int raisePrices(@Param("category") String category, @Param("pct") BigDecimal pct);
}

@Service
class PricingService {
    private final ProductRepository products;
    PricingService(ProductRepository products) { this.products = products; }

    @Transactional
    public BigDecimal raiseAndRead(String category, BigDecimal pct, Long sampleId) {
        products.findById(sampleId);                       // loaded before: would be stale without clearing
        products.raisePrices(category, pct);
        return products.findById(sampleId).orElseThrow().getPrice();   // reloaded after clear
    }
}
```
`clearAutomatically` empties the persistence context after the update so the second `findById` runs a fresh SELECT. The version is bumped manually because bulk updates do not do it, keeping optimistic locking correct.
</details>

## 6. Self-invocation bug
```java
@Service
public class ImportService {
    public void importFile(List<Row> rows) {
        for (Row r : rows) {
            try { importRow(r); } catch (Exception e) { log.warn("skip {}", r); }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importRow(Row r) { /* saves product and stock */ }
}
```
The team expects each row in its own transaction. What actually happens, and how do you fix it?

<details><summary>Solution</summary>

`importRow` is called through `this`, not the proxy, so `@Transactional` is ignored. Each repository call inside `importRow` runs in its own tiny transaction, so a failure in the middle of a row leaves half the row saved.

```java
@Service
class RowImporter {
    private final ProductRepository products;
    RowImporter(ProductRepository products) { this.products = products; }

    @Transactional                     // REQUIRED is enough: the caller has no transaction
    public void importRow(Row r) {
        Product p = products.findBySku(r.sku()).orElseGet(() -> new Product(r.sku(), r.name(), r.price(), 0));
        p.changePrice(r.price());
        products.save(p);
    }
}

@Service
class ImportService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImportService.class);
    private final RowImporter importer;
    ImportService(RowImporter importer) { this.importer = importer; }

    public void importFile(List<Row> rows) {
        for (Row r : rows) {
            try { importer.importRow(r); }       // crosses the proxy: one transaction per row
            catch (RuntimeException e) { log.warn("skip {}", r, e); }
        }
    }
}
```
Moving the method to another bean makes the call go through the proxy. Alternatives: `TransactionTemplate` inside the loop, or self-injection with `@Lazy`.
</details>

## 7. Prevent overselling under concurrency
Two hundred users try to buy the last 10 units of a product at the same moment. Write two correct implementations of `reserve(productId, qty)`: one pessimistic, one without any explicit lock.

<details><summary>Solution</summary>

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000"))
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findForUpdate(@Param("id") Long id);

    @Modifying
    @Query("update Product p set p.stock = p.stock - :qty, p.version = p.version + 1 where p.id = :id and p.stock >= :qty")
    int decrementIfAvailable(@Param("id") Long id, @Param("qty") int qty);
}

@Service
class ReservationService {
    private final ProductRepository products;
    ReservationService(ProductRepository products) { this.products = products; }

    // Pessimistic: SELECT ... FOR UPDATE serialises buyers on this row
    @Transactional
    public boolean reservePessimistic(Long id, int qty) {
        Product p = products.findForUpdate(id).orElseThrow();
        if (p.getStock() < qty) return false;
        p.decreaseStock(qty);
        return true;
    }

    // Atomic conditional update: the database checks and decrements in one statement
    @Transactional
    public boolean reserveAtomic(Long id, int qty) {
        return products.decrementIfAvailable(id, qty) == 1;
    }
}
```
Both guarantee exactly 10 successful single-unit reservations. The pessimistic version holds a row lock for the transaction's duration (keep it short, set a timeout). The atomic version needs no Java-side read at all. A third valid answer is `@Version` with a retry loop, which is fine at low contention but wastes work when 200 buyers collide.
</details>

## 8. Paginate orders with lines
`@Query("select o from Order o left join fetch o.lines")` with a `Pageable` logs `HHH90003004` and is slow on large data. Rewrite it so the database does the paging and each page still has its lines loaded.

<details><summary>Solution</summary>

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "select o.id from Order o", countQuery = "select count(o) from Order o")
    Page<Long> findPageOfIds(Pageable pageable);

    @EntityGraph(attributePaths = "lines")
    List<Order> findByIdIn(Collection<Long> ids, Sort sort);
}

@Service
@Transactional(readOnly = true)
class OrderPageService {
    private final OrderRepository orders;
    OrderPageService(OrderRepository orders) { this.orders = orders; }

    public Page<Order> page(Pageable pageable) {
        Page<Long> ids = orders.findPageOfIds(pageable);
        List<Order> content = orders.findByIdIn(ids.getContent(), pageable.getSort());
        return new PageImpl<>(content, pageable, ids.getTotalElements());
    }
}
```
Query 1 pages ids with `offset/fetch first` in SQL, query 2 counts, query 3 fetches exactly those orders with their lines in one join. The original version could not apply LIMIT to orders because the join multiplies rows per line, so Hibernate loaded everything and paged in memory. Setting `default_batch_fetch_size` and paging plain orders is another valid fix.
</details>

## 9. Optimistic lock with retry
Write a `LoyaltyService.addPoints(customerId, points)` that may be called concurrently by several Kafka consumers. `Customer` has `@Version`. No update may be lost, and transient conflicts should be retried up to 3 times.

<details><summary>Solution</summary>

```java
@Service
class LoyaltyService {
    private final CustomerRepository customers;
    private final TransactionTemplate tx;

    LoyaltyService(CustomerRepository customers, PlatformTransactionManager tm) {
        this.customers = customers;
        this.tx = new TransactionTemplate(tm);
    }

    public void addPoints(Long customerId, int points) {
        for (int attempt = 1; ; attempt++) {
            try {
                tx.executeWithoutResult(s -> customers.findById(customerId).orElseThrow().addPoints(points));
                return;                                   // committed: version checked in the UPDATE
            } catch (OptimisticLockingFailureException e) {
                if (attempt >= 3) throw e;
                try { Thread.sleep(20L * attempt); } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); throw e;
                }
            }
        }
    }
}
```
Each attempt runs in a brand-new transaction, so it reloads the latest row and version. The UPDATE includes `where id=? and version=?`; a concurrent change makes it match 0 rows, Spring throws `ObjectOptimisticLockingFailureException` (a subclass of `OptimisticLockingFailureException`) at commit, and we retry with backoff. Spring Retry's `@Retryable` on a method of another bean is an equivalent declarative solution.
</details>

## 10. Projection for a dashboard
The admin dashboard needs, per customer tier, the number of customers, the number of paid orders and the revenue. Write one query returning a list of records.

<details><summary>Solution</summary>

```java
public record TierStats(Customer.Tier tier, long customers, long paidOrders, BigDecimal revenue) { }

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("""
           select new com.orderhub.customer.TierStats(
                  c.tier,
                  count(distinct c.id),
                  count(o.id),
                  coalesce(sum(o.total), 0))
           from Customer c
           left join Order o on o.customer = c and o.status = 'PAID'
           group by c.tier
           order by c.tier
           """)
    List<TierStats> tierStats();
}
```
The entity join with an `on` condition (`left join Order o on o.customer = c`) works even though `Customer` has no `orders` collection, which keeps the mapping unidirectional. Putting `o.status = 'PAID'` in the join condition (not in `where`) keeps tiers with no paid orders in the result. `count(distinct c.id)` avoids counting customers once per order.
</details>

@@ 3 | TOPIC | Practice — Phase 12 review: Spring Data JPA | Cheat sheet, key snippets, the 10 most likely questions and rapid-fire flashcards for JPA and transactions | MUST
# Phase 12 review: Spring Data JPA

## Cheat sheet

| Idea | Remember |
|---|---|
| Layers | JPA = spec, Hibernate = implementation, Spring Data JPA = repositories on top |
| JDBC tools | `JdbcClient` (6.1) and `JdbcTemplate` for plain SQL, reports and bulk work |
| Entity rules | `@Entity`, `@Id`, protected no-arg constructor, not final |
| Ids | `SEQUENCE` with `allocationSize` equal to the sequence increment allows batching; `IDENTITY` disables batch inserts |
| Enums | Always `EnumType.STRING` |
| equals and hashCode | Business key, or id-based with null ids unequal and a constant hashCode; never Lombok `@Data` |
| Defaults | To-one EAGER, to-many LAZY; set every relation to LAZY |
| Owning side | The side with `@JoinColumn` or `@JoinTable`; only it writes the FK |
| Cascade | Only parent to owned children (`ALL` plus `orphanRemoval` for OrderLine) |
| N+1 | Fix with `JOIN FETCH`, `@EntityGraph`, batch fetching, DTO projections; never with EAGER |
| OSIV | `spring.jpa.open-in-view: false` |
| Pagination with collection fetch | HHH90003004 in-memory paging: page ids first |
| Derived queries | Parsed and validated at startup; keep them short |
| `@Modifying` | Bulk update or delete; bypasses persistence context; use `clearAutomatically` |
| Projections | Closed interface or record DTO; commands load entities, queries return projections |
| `@Transactional` | Proxy plus `TransactionInterceptor`; rollback on unchecked only |
| Traps | Self-invocation, private methods, swallowed exceptions, checked exceptions, other threads |
| Propagation | `REQUIRED` joins; `REQUIRES_NEW` suspends and runs independently |
| Isolation | PostgreSQL default READ COMMITTED; MySQL REPEATABLE READ |
| Lost update | Atomic UPDATE, `@Version`, or `PESSIMISTIC_WRITE` |
| Entity states | Transient, managed, detached, removed; `merge` returns a managed copy |
| Flush vs commit | Flush sends SQL; commit makes it durable and visible |
| Locking | Optimistic: version check and 409; pessimistic: `FOR UPDATE` with timeout and ordered locking |
| Flyway | `V1__name.sql`, never edit applied scripts, `ddl-auto: validate` |
| Auditing | `@EnableJpaAuditing`, `AuditingEntityListener`, `AuditorAware`; bulk updates skip it |

### Key code snippets
```java
// Entity basics
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_gen")
    @SequenceGenerator(name = "orders_gen", sequenceName = "orders_seq", allocationSize = 50)
    private Long id;

    @Version private long version;

    @Enumerated(EnumType.STRING) private Status status = Status.NEW;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() { }
}
```

```java
// Repository: derived, fetch, projection, bulk, lock
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByCreatedAtDesc(Order.Status status);

    @EntityGraph(attributePaths = {"customer", "lines"})
    Optional<Order> findWithDetailsById(Long id);

    @Query("select new com.orderhub.order.OrderSummary(o.id, c.email, o.total) from Order o join o.customer c")
    List<OrderSummary> summaries();

    @Modifying(clearAutomatically = true)
    @Query("update Order o set o.status = 'CANCELLED' where o.status = 'NEW' and o.createdAt < :before")
    int cancelStale(@Param("before") Instant before);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findForUpdate(@Param("id") Long id);
}
```

```java
// Transactions
@Service
@Transactional(readOnly = true)
class OrderService {
    @Transactional                                                  // read-write, REQUIRED
    public Long place(Long customerId, Long productId, int qty) { /* load, change, save */ return 1L; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)          // survives outer rollback
    public void audit(String msg) { }

    @Transactional(rollbackFor = PaymentDeclinedException.class)    // checked exception rolls back
    public void pay(Long orderId) throws PaymentDeclinedException { }
}
```

```yaml
# application.yml for JPA in OrderHub
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orderhub
    username: orderhub
    password: orderhub
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        default_batch_fetch_size: 50
        jdbc.batch_size: 50
        order_inserts: true
        order_updates: true
  flyway:
    clean-disabled: true
logging:
  level:
    org.hibernate.SQL: DEBUG
```

```sql
-- V1__init.sql pattern
create sequence orders_seq start with 1 increment by 50;
create table orders (
    id bigint primary key,
    customer_id bigint not null references customer(id),
    status varchar(20) not null,
    total numeric(12,2) not null,
    version bigint not null default 0,
    created_at timestamp with time zone not null
);
create index idx_orders_customer on orders(customer_id);
```

## The 10 most likely interview questions

1. **What is the N+1 problem and how do you fix it?** One query for N parents plus one per parent for a lazy relation; fix per use case with `JOIN FETCH`, `@EntityGraph`, batch fetching or DTO projections, and detect it by reading the SQL log or counting statements in tests.
2. **JPA vs Hibernate vs Spring Data JPA?** Specification, implementation, and repository abstraction that generates implementations and derived queries.
3. **How does `@Transactional` work and when does it fail?** A proxy's interceptor begins, commits or rolls back via the transaction manager; it fails on self-invocation, private methods, swallowed exceptions, checked exceptions without `rollbackFor`, and work on other threads.
4. **`REQUIRED` vs `REQUIRES_NEW`?** Join or create vs always a new independent transaction with the outer one suspended; `REQUIRES_NEW` for audit logs that must survive.
5. **Explain isolation levels and read phenomena.** Dirty, non-repeatable and phantom reads prevented progressively by READ COMMITTED, REPEATABLE READ and SERIALIZABLE; PostgreSQL defaults to READ COMMITTED.
6. **Optimistic vs pessimistic locking?** Version check that fails on conflict vs `SELECT FOR UPDATE` that makes others wait; optimistic for rare conflicts and edits across requests, pessimistic for hot rows.
7. **What is the persistence context and dirty checking?** The transaction-scoped set of managed entities with one instance per row; at flush Hibernate compares fields with snapshots and writes UPDATEs.
8. **LAZY vs EAGER and `LazyInitializationException`?** EAGER always loads, LAZY on access; the exception means a lazy relation was touched after the session closed; fetch inside the transaction and return DTOs.
9. **Owning side, cascade and orphanRemoval?** The FK-holding side writes; cascade propagates operations to owned children; orphanRemoval deletes children removed from the collection.
10. **Why Flyway instead of `ddl-auto: update`?** Versioned, reviewed, repeatable SQL with checksums and data migrations, versus unreviewed, incomplete automatic changes.

## Rapid-fire flashcards

Q: What is the default fetch type of @ManyToOne in JPA?
A: EAGER; set it to LAZY explicitly.

Q: Which Spring Boot property should be false to avoid lazy loading in controllers?
A: `spring.jpa.open-in-view`.

Q: What does Hibernate 6 log when a collection fetch is combined with pagination?
A: HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory.

Q: Which exception does Spring throw when an @Version check fails?
A: `ObjectOptimisticLockingFailureException`.

Q: What SQL does @Lock(PESSIMISTIC_WRITE) produce?
A: `SELECT ... FOR UPDATE` (or `for no key update` on PostgreSQL with Hibernate 6).

Q: What does @Modifying(clearAutomatically = true) prevent?
A: Stale managed entities after a bulk update in the same persistence context.

Q: Which id generation strategy disables JDBC batch inserts in Hibernate?
A: `GenerationType.IDENTITY`.

Q: What is SQLSTATE 40001 in PostgreSQL?
A: A serialization failure; the transaction must be retried.

Q: Which Flyway table records applied migrations?
A: `flyway_schema_history`.

Q: Which bean supplies the current user for @CreatedBy?
A: An `AuditorAware` bean.
