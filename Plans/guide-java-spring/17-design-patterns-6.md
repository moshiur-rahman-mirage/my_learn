@@ 3 | TOPIC | Practice — Coding drill: Low-level design problems | Ten LLD interview problems: parking lot, vending machine, rate limiter, LRU cache, elevator, ATM, expense sharing, ticket booking, notifications and a TTL store | MUST
# Coding drill: Low-level design problems

Low-level design (LLD, also called machine coding or object-oriented design) rounds give you 45-60 minutes to turn a vague prompt into working classes. Interviewers score: clarifying requirements, identifying entities and responsibilities, choosing patterns for the parts that vary, handling concurrency and edge cases, and producing clean, runnable code. Each problem below follows the same rhythm you should use in the interview:

1. **Clarify** scope (what is in and out), with 3-5 questions.
2. **Entities** and relationships (nouns become classes, verbs become methods).
3. **Variation points** and the pattern for each.
4. **Code** the core path first, then edge cases and concurrency.
5. **Extend:** say how the design handles the follow-up the interviewer is about to ask.

Every solution is one runnable Java 21 class; the designs are deliberately compact enough to write in an interview.

## 1. Parking lot
Requirements: several levels; spots of size MOTORCYCLE, COMPACT and LARGE; a vehicle fits in a spot of its size or larger; `park` returns a ticket, `unpark` returns the fee (first hour 2.00, then 1.50 per started hour; motorcycles half price); concurrent entry gates must never assign one spot twice.

Hint: entities `ParkingLot`, `Level`, `Spot`, `Vehicle`, `Ticket`; pricing is a Strategy; time comes from an injected `Clock`; spot allocation is the critical section.

<details><summary>Solution</summary>

```java
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ParkingLotDesign {

    enum Size { MOTORCYCLE, COMPACT, LARGE }
    record Vehicle(String plate, Size size) { }
    record Ticket(long id, String plate, String spotId, Instant entry) { }

    static final class Spot {
        final String id;
        final Size size;
        private Vehicle parked;                                  // guarded by the lot's lock
        Spot(String id, Size size) { this.id = id; this.size = size; }
        boolean fits(Vehicle v) { return parked == null && size.ordinal() >= v.size().ordinal(); }
    }

    interface PricingStrategy { BigDecimal fee(Vehicle v, Duration stay); }

    static final class HourlyPricing implements PricingStrategy {
        public BigDecimal fee(Vehicle v, Duration stay) {
            long startedHours = Math.max(1, (stay.toMinutes() + 59) / 60);
            BigDecimal fee = new BigDecimal("2.00").add(new BigDecimal("1.50").multiply(BigDecimal.valueOf(startedHours - 1)));
            return v.size() == Size.MOTORCYCLE ? fee.divide(BigDecimal.valueOf(2)) : fee;
        }
    }

    static final class ParkingLot {
        private final List<List<Spot>> levels = new ArrayList<>();
        private final Map<Long, Ticket> active = new ConcurrentHashMap<>();
        private final Map<String, Spot> spotsById = new ConcurrentHashMap<>();
        private final AtomicLong ticketIds = new AtomicLong();
        private final PricingStrategy pricing;
        private final Clock clock;

        ParkingLot(int levelCount, Map<Size, Integer> spotsPerLevel, PricingStrategy pricing, Clock clock) {
            this.pricing = pricing;
            this.clock = clock;
            for (int l = 0; l < levelCount; l++) {
                List<Spot> level = new ArrayList<>();
                for (Size s : Size.values()) {
                    for (int i = 0; i < spotsPerLevel.getOrDefault(s, 0); i++) {
                        Spot spot = new Spot("L" + l + "-" + s.name().charAt(0) + i, s);
                        level.add(spot);
                        spotsById.put(spot.id, spot);
                    }
                }
                levels.add(level);
            }
        }

        // smallest fitting spot on the lowest level; synchronized so two gates never take one spot
        synchronized Optional<Ticket> park(Vehicle v) {
            for (List<Spot> level : levels) {
                for (Spot spot : level) {                          // spots are ordered small to large
                    if (spot.fits(v)) {
                        spot.parked = v;
                        Ticket t = new Ticket(ticketIds.incrementAndGet(), v.plate(), spot.id, clock.instant());
                        active.put(t.id(), t);
                        return Optional.of(t);
                    }
                }
            }
            return Optional.empty();                               // lot full for this size
        }

        synchronized BigDecimal unpark(long ticketId) {
            Ticket t = active.remove(ticketId);
            if (t == null) throw new IllegalArgumentException("unknown or used ticket " + ticketId);
            Spot spot = spotsById.get(t.spotId());
            Vehicle v = spot.parked;
            spot.parked = null;
            return pricing.fee(v, Duration.between(t.entry(), clock.instant()));
        }
    }

    // a controllable clock for the demo
    static final class MutableClock extends Clock {
        private Instant now;
        MutableClock(Instant start) { now = start; }
        void advance(Duration d) { now = now.plus(d); }
        public Instant instant() { return now; }
        public java.time.ZoneId getZone() { return ZoneOffset.UTC; }
        public Clock withZone(java.time.ZoneId zone) { return this; }
    }

    public static void main(String[] args) {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-24T08:00:00Z"));
        ParkingLot lot = new ParkingLot(1, Map.of(Size.MOTORCYCLE, 1, Size.COMPACT, 1, Size.LARGE, 1), new HourlyPricing(), clock);

        Ticket bike = lot.park(new Vehicle("B-1", Size.MOTORCYCLE)).orElseThrow();
        Ticket car = lot.park(new Vehicle("C-1", Size.COMPACT)).orElseThrow();
        Ticket car2 = lot.park(new Vehicle("C-2", Size.COMPACT)).orElseThrow();   // takes the LARGE spot
        System.out.println(bike.spotId() + " " + car.spotId() + " " + car2.spotId());
        System.out.println("truck parked: " + lot.park(new Vehicle("T-1", Size.LARGE)).isPresent());

        clock.advance(Duration.ofMinutes(150));                     // 2.5 h -> 3 started hours
        System.out.println("car fee: " + lot.unpark(car.id()));
        System.out.println("bike fee: " + lot.unpark(bike.id()));
        try {
            lot.unpark(car.id());
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}
```
Output:
```text
L0-M0 L0-C0 L0-L0
truck parked: false
car fee: 5.00
bike fee: 2.50
rejected: unknown or used ticket 2
```
Design notes: pricing is a Strategy (weekend or EV pricing is a new class); `Clock` injection makes fees testable; allocation and release are `synchronized` because "find free spot and mark it taken" is a check-then-act. Follow-ups: per-level locks or a free-spot queue per size (`ConcurrentLinkedQueue`) to reduce contention; a `SpotAssignmentStrategy` (nearest to exit, level fill order); displays as Observers of spot changes.
</details>

## 2. Vending machine
Requirements: products in slots with price and stock; accepts coins (10, 20, 50, 100 cents); select a product, dispense it and return change; cancel returns inserted money; reject selection when funds are insufficient or the slot is empty.

Hint: the machine's behaviour depends on its state (idle, has money), which is the State pattern; keep money in integer cents.

<details><summary>Solution</summary>

```java
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VendingMachineDesign {

    enum Coin {
        TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100);
        final int cents;
        Coin(int cents) { this.cents = cents; }
    }

    record Slot(String product, int priceCents, int stock) {
        Slot withStock(int s) { return new Slot(product, priceCents, s); }
    }

    // State pattern: each state decides what the actions mean
    interface State {
        State insert(VendingMachine m, Coin c);
        State select(VendingMachine m, String code);
        State cancel(VendingMachine m);
    }

    static final class Idle implements State {
        public State insert(VendingMachine m, Coin c) { m.balance += c.cents; return new HasMoney(); }
        public State select(VendingMachine m, String code) { m.say("insert money first"); return this; }
        public State cancel(VendingMachine m) { m.say("nothing to return"); return this; }
    }

    static final class HasMoney implements State {
        public State insert(VendingMachine m, Coin c) { m.balance += c.cents; return this; }

        public State select(VendingMachine m, String code) {
            Slot slot = m.slots.get(code);
            if (slot == null || slot.stock() == 0) { m.say("sold out: " + code); return this; }
            if (m.balance < slot.priceCents()) {
                m.say("insufficient funds: need " + (slot.priceCents() - m.balance) + " more");
                return this;
            }
            m.slots.put(code, slot.withStock(slot.stock() - 1));
            int change = m.balance - slot.priceCents();
            m.balance = 0;
            m.say("dispensed " + slot.product() + ", change " + m.makeChange(change));
            return new Idle();
        }

        public State cancel(VendingMachine m) {
            m.say("refund " + m.makeChange(m.balance));
            m.balance = 0;
            return new Idle();
        }
    }

    static final class VendingMachine {
        final Map<String, Slot> slots = new LinkedHashMap<>();
        int balance;
        private State state = new Idle();

        synchronized void insert(Coin c) { state = state.insert(this, c); }
        synchronized void select(String code) { state = state.select(this, code); }
        synchronized void cancel() { state = state.cancel(this); }

        List<Coin> makeChange(int cents) {                // greedy works for this coin system
            List<Coin> coins = new ArrayList<>();
            Coin[] desc = { Coin.HUNDRED, Coin.FIFTY, Coin.TWENTY, Coin.TEN };
            for (Coin c : desc) {
                while (cents >= c.cents) { coins.add(c); cents -= c.cents; }
            }
            return coins;
        }

        void say(String msg) { System.out.println(msg); }
    }

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.slots.put("A1", new Slot("Water", 80, 1));
        vm.slots.put("B2", new Slot("Chips", 150, 5));

        vm.select("A1");
        vm.insert(Coin.HUNDRED);
        vm.select("B2");
        vm.insert(Coin.FIFTY);
        vm.select("A1");
        vm.insert(Coin.TWENTY);
        vm.select("A1");
        vm.insert(Coin.FIFTY);
        vm.insert(Coin.TEN);
        vm.cancel();
    }
}
```
Output:
```text
insert money first
insufficient funds: need 50 more
dispensed Water, change [FIFTY, TWENTY]
sold out: A1
refund [FIFTY, TWENTY, TEN]
```
Design notes: the State pattern replaces `if (state == ...)` checks in every method; adding a `Maintenance` state is a new class. Money is integer cents (never `double`). Follow-ups: a limited coin inventory makes change-making a bounded coin-change problem (dynamic programming, and refuse the sale if exact change is impossible); card payments as a Strategy; telemetry events as Observer.
</details>

## 3. Rate limiter per client
Requirements: allow at most N requests per client in any rolling window of W seconds (sliding window log); thread-safe for many clients; time injectable; choose the algorithm per API.

Hint: `RateLimiter` interface (Strategy) with a sliding-window-log implementation; a `ConcurrentHashMap` of per-client deques, locking per client, not globally.

<details><summary>Solution</summary>

```java
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

public class RateLimiterDesign {

    interface RateLimiter { boolean tryAcquire(String clientId); }

    static final class SlidingWindowLogLimiter implements RateLimiter {
        private final int limit;
        private final long windowNanos;
        private final LongSupplier clock;
        private final Map<String, Deque<Long>> logs = new ConcurrentHashMap<>();

        SlidingWindowLogLimiter(int limit, Duration window, LongSupplier nanoClock) {
            this.limit = limit;
            this.windowNanos = window.toNanos();
            this.clock = nanoClock;
        }

        @Override
        public boolean tryAcquire(String clientId) {
            Deque<Long> log = logs.computeIfAbsent(clientId, id -> new ArrayDeque<>());
            synchronized (log) {                                  // lock per client: no global contention
                long now = clock.getAsLong();
                while (!log.isEmpty() && now - log.peekFirst() >= windowNanos) log.pollFirst();   // evict old
                if (log.size() >= limit) return false;
                log.addLast(now);
                return true;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        long[] now = {0};
        RateLimiter limiter = new SlidingWindowLogLimiter(3, Duration.ofSeconds(10), () -> now[0]);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) sb.append(limiter.tryAcquire("alice") ? "Y" : "N");
        System.out.println("alice t=0s:  " + sb);
        System.out.println("bob t=0s:    " + (limiter.tryAcquire("bob") ? "Y" : "N"));
        now[0] = Duration.ofSeconds(9).toNanos();
        System.out.println("alice t=9s:  " + (limiter.tryAcquire("alice") ? "Y" : "N"));
        now[0] = Duration.ofSeconds(10).toNanos();
        System.out.println("alice t=10s: " + (limiter.tryAcquire("alice") ? "Y" : "N"));

        // concurrency check: 100 threads race for 3 permits of client "carol"
        RateLimiter shared = new SlidingWindowLogLimiter(3, Duration.ofMinutes(1), System::nanoTime);
        AtomicInteger allowed = new AtomicInteger();
        try (ExecutorService pool = Executors.newFixedThreadPool(16)) {
            for (int i = 0; i < 100; i++) pool.submit(() -> { if (shared.tryAcquire("carol")) allowed.incrementAndGet(); });
        }
        System.out.println("carol allowed under contention: " + allowed.get());
    }
}
```
Output:
```text
alice t=0s:  YYYN
bob t=0s:    Y
alice t=9s:  N
alice t=10s: Y
carol allowed under contention: 3
```
Design notes: at t=10 s the three requests from t=0 leave the window, so a new one is allowed. The sliding log is exact but stores one timestamp per request (memory O(limit) per client); alternatives behind the same interface: fixed window counter (cheap, burst at window edges), sliding window counter (weighted two windows), token bucket (bursts plus steady rate). For many instances, keep state in Redis with an atomic Lua script. Evict idle clients periodically to bound memory.
</details>

## 4. LRU cache with O(1) get and put
Requirements: fixed capacity; `get` and `put` in O(1); evict the least recently used entry when full; `get` counts as use. Do not use `LinkedHashMap` (then show the one-liner).

Hint: `HashMap<K, Node>` for lookup plus a doubly linked list with sentinel head and tail for recency order.

<details><summary>Solution</summary>

```java
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class LruCacheDesign<K, V> {

    private final class Node {
        K key;
        V value;
        Node prev, next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<K, Node> index = new HashMap<>();
    private final Node head = new Node(null, null);     // most recent side
    private final Node tail = new Node(null, null);     // least recent side

    public LruCacheDesign(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public synchronized V get(K key) {
        Node n = index.get(key);
        if (n == null) return null;
        moveToFront(n);
        return n.value;
    }

    public synchronized void put(K key, V value) {
        Node n = index.get(key);
        if (n != null) {
            n.value = value;
            moveToFront(n);
            return;
        }
        if (index.size() == capacity) {
            Node lru = tail.prev;                        // evict least recently used
            unlink(lru);
            index.remove(lru.key);
        }
        Node fresh = new Node(key, value);
        index.put(key, fresh);
        linkFront(fresh);
    }

    private void moveToFront(Node n) { unlink(n); linkFront(n); }
    private void unlink(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
    private void linkFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }

    @Override public synchronized String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (Node n = head.next; n != tail; n = n.next) sj.add(n.key + "=" + n.value);
        return sj.toString();
    }

    // The library version: access-order LinkedHashMap with removeEldestEntry
    static <K, V> Map<K, V> linkedHashMapLru(int capacity) {
        return new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > capacity; }
        };
    }

    public static void main(String[] args) {
        LruCacheDesign<String, Integer> cache = new LruCacheDesign<>(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.get("a");                 // a becomes most recent
        cache.put("d", 4);              // evicts b
        System.out.println("custom: " + cache + " get(b)=" + cache.get("b"));

        Map<String, Integer> lhm = linkedHashMapLru(3);
        lhm.put("a", 1); lhm.put("b", 2); lhm.put("c", 3);
        lhm.get("a");
        lhm.put("d", 4);
        System.out.println("linkedHashMap (eldest first): " + lhm);
    }
}
```
Output:
```text
custom: [d=4, a=1, c=3] get(b)=null
linkedHashMap (eldest first): {c=3, a=1, d=4}
```
Design notes: sentinels remove null checks at the ends; each operation touches a constant number of pointers. The node stores its key so eviction can remove it from the map. `synchronized` makes it thread-safe but serializes access; for high concurrency use Caffeine (approximate LRU with W-TinyLFU and lock-free reads) or segment the cache. Follow-ups: TTL per entry, LFU eviction, cache statistics.
</details>

## 5. Elevator (single car, SCAN scheduling)
Requirements: floors 0-10; requests from inside or outside; the car keeps moving in its current direction while there are requests ahead, then reverses (SCAN, like a disk arm); report the stop order.

Hint: two `TreeSet`s (stops above and below), a direction enum; `step()` moves one floor.

<details><summary>Solution</summary>

```java
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ElevatorDesign {

    enum Direction { UP, DOWN, IDLE }

    static final class Elevator {
        private final int minFloor, maxFloor;
        private int floor;
        private Direction direction = Direction.IDLE;
        private final TreeSet<Integer> up = new TreeSet<>();
        private final TreeSet<Integer> down = new TreeSet<>();
        final List<Integer> stops = new ArrayList<>();

        Elevator(int minFloor, int maxFloor, int start) { this.minFloor = minFloor; this.maxFloor = maxFloor; this.floor = start; }

        synchronized void request(int target) {
            if (target < minFloor || target > maxFloor) throw new IllegalArgumentException("no floor " + target);
            if (target > floor) up.add(target);
            else if (target < floor) down.add(target);
            if (direction == Direction.IDLE) direction = target > floor ? Direction.UP : Direction.DOWN;
        }

        synchronized boolean step() {
            if (up.isEmpty() && down.isEmpty()) { direction = Direction.IDLE; return false; }
            if (direction == Direction.UP && up.isEmpty()) direction = Direction.DOWN;      // reverse at the end
            if (direction == Direction.DOWN && down.isEmpty()) direction = Direction.UP;
            floor += direction == Direction.UP ? 1 : -1;
            TreeSet<Integer> pending = direction == Direction.UP ? up : down;
            if (pending.remove(floor)) stops.add(floor);                                   // open doors
            return true;
        }

        void runUntilIdle() { while (step()) { } }
        int floor() { return floor; }
    }

    public static void main(String[] args) {
        Elevator e = new Elevator(0, 10, 4);
        for (int f : new int[]{7, 2, 9, 5}) e.request(f);    // car at 4 goes up first (first request is 7)
        e.runUntilIdle();
        System.out.println("stop order: " + e.stops + ", now at " + e.floor());

        Elevator e2 = new Elevator(0, 10, 6);
        e2.request(1);
        e2.step();                    // moving down: now at 5
        e2.request(8);                // above: served after the downward sweep
        e2.request(3);                // below and ahead: served on the way down
        e2.runUntilIdle();
        System.out.println("stop order: " + e2.stops + ", now at " + e2.floor());
    }
}
```
Output:
```text
stop order: [5, 7, 9, 2], now at 2
stop order: [3, 1, 8], now at 8
```
Design notes: a request below the car while moving up is postponed to the downward sweep, which avoids starvation of far floors compared with "nearest first". Follow-ups: multiple cars with a dispatcher Strategy (nearest car moving towards the caller), direction of hall calls, door and overload states (State pattern), and a controller thread driving `step()` on a schedule.
</details>

## 6. ATM cash dispenser
Requirements: notes of 100, 50 and 20 with limited counts; dispense an amount using the largest notes first; refuse if the exact amount cannot be paid with available notes, without changing counts.

Hint: Chain of Responsibility, one handler per denomination; compute a plan first, then commit (all or nothing).

<details><summary>Solution</summary>

```java
import java.util.LinkedHashMap;
import java.util.Map;

public class AtmDesign {

    static final class NoteHandler {
        final int value;
        int count;
        NoteHandler next;
        NoteHandler(int value, int count) { this.value = value; this.count = count; }

        // returns true if the remaining amount can be covered by this handler and the rest of the chain
        boolean plan(int amount, Map<Integer, Integer> plan) {
            if (amount == 0) return true;
            int use = Math.min(amount / value, count);
            for (int n = use; n >= 0; n--) {               // back off if the rest cannot be paid (e.g. 60 = 3 x 20)
                plan.put(value, n);
                if (next != null && next.plan(amount - n * value, plan)) return true;
                if (next == null && amount - n * value == 0) return true;
            }
            plan.remove(value);
            return false;
        }
    }

    static final class Atm {
        private final NoteHandler chain;

        Atm(int hundreds, int fifties, int twenties) {
            NoteHandler h100 = new NoteHandler(100, hundreds);
            NoteHandler h50 = new NoteHandler(50, fifties);
            NoteHandler h20 = new NoteHandler(20, twenties);
            h100.next = h50;
            h50.next = h20;
            chain = h100;
        }

        synchronized Map<Integer, Integer> withdraw(int amount) {
            if (amount <= 0 || amount % 10 != 0) throw new IllegalArgumentException("invalid amount " + amount);
            Map<Integer, Integer> plan = new LinkedHashMap<>();
            if (!chain.plan(amount, plan)) throw new IllegalStateException("cannot dispense " + amount);
            for (NoteHandler h = chain; h != null; h = h.next) h.count -= plan.getOrDefault(h.value, 0);   // commit
            plan.values().removeIf(n -> n == 0);
            return plan;
        }

        String stock() {
            StringBuilder sb = new StringBuilder();
            for (NoteHandler h = chain; h != null; h = h.next) sb.append(h.value).append("x").append(h.count).append(' ');
            return sb.toString().trim();
        }
    }

    public static void main(String[] args) {
        Atm atm = new Atm(1, 1, 5);
        System.out.println("withdraw 160 -> " + atm.withdraw(160) + " | stock " + atm.stock());
        System.out.println("withdraw 90  -> " + atm.withdraw(90) + " | stock " + atm.stock());
        try {
            atm.withdraw(30);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage() + " | stock unchanged " + atm.stock());
        }
    }
}
```
Output:
```text
withdraw 160 -> {100=1, 20=3} | stock 100x0 50x1 20x2
withdraw 90  -> {50=1, 20=2} | stock 100x0 50x0 20x0
cannot dispense 30 | stock unchanged 100x0 50x0 20x0
```
Design notes: each handler owns one denomination and passes the remainder down the chain. Pure greedy fails for 160 (100 + 50 leaves 10), so the 50 handler backs off and the 20s pay 60 instead. Planning before committing makes a failed withdrawal leave the stock untouched. Follow-ups: ATM states (card inserted, PIN verified) with the State pattern; per-account limits; persistence and auditing.
</details>

## 7. Expense sharing (Splitwise)
Requirements: users add expenses split equally or by exact amounts; show each user's net balance; simplify debts into the minimum practical list of "who pays whom".

Hint: split types as a Strategy (or a sealed interface); keep a net balance per user in cents; settle by matching the largest creditor with the largest debtor.

<details><summary>Solution</summary>

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class ExpenseSharingDesign {

    sealed interface Split permits Equal, Exact { }
    record Equal(List<String> participants) implements Split { }
    record Exact(Map<String, Long> shares) implements Split { }

    static final class Ledger {
        private final Map<String, Long> net = new TreeMap<>();   // positive: others owe them

        void addExpense(String payer, long amountCents, Split split) {
            Map<String, Long> owed = switch (split) {
                case Equal e -> {
                    Map<String, Long> m = new TreeMap<>();
                    long base = amountCents / e.participants().size();
                    long remainder = amountCents % e.participants().size();
                    for (int i = 0; i < e.participants().size(); i++) {
                        m.put(e.participants().get(i), base + (i < remainder ? 1 : 0));   // spread leftover cents
                    }
                    yield m;
                }
                case Exact x -> {
                    long sum = x.shares().values().stream().mapToLong(Long::longValue).sum();
                    if (sum != amountCents) throw new IllegalArgumentException("shares " + sum + " != amount " + amountCents);
                    yield x.shares();
                }
            };
            net.merge(payer, amountCents, Long::sum);
            owed.forEach((user, share) -> net.merge(user, -share, Long::sum));
        }

        Map<String, Long> balances() { return net; }

        List<String> settle() {
            PriorityQueue<Map.Entry<String, Long>> creditors = new PriorityQueue<>((a, b) -> Long.compare(b.getValue(), a.getValue()));
            PriorityQueue<Map.Entry<String, Long>> debtors = new PriorityQueue<>((a, b) -> Long.compare(a.getValue(), b.getValue()));
            net.forEach((u, v) -> {
                if (v > 0) creditors.add(Map.entry(u, v));
                if (v < 0) debtors.add(Map.entry(u, v));
            });
            List<String> payments = new ArrayList<>();
            while (!creditors.isEmpty() && !debtors.isEmpty()) {
                var c = creditors.poll();
                var d = debtors.poll();
                long amount = Math.min(c.getValue(), -d.getValue());
                payments.add(d.getKey() + " pays " + c.getKey() + " " + amount / 100 + "." + String.format("%02d", amount % 100));
                if (c.getValue() - amount > 0) creditors.add(Map.entry(c.getKey(), c.getValue() - amount));
                if (d.getValue() + amount < 0) debtors.add(Map.entry(d.getKey(), d.getValue() + amount));
            }
            return payments;
        }
    }

    public static void main(String[] args) {
        Ledger ledger = new Ledger();
        ledger.addExpense("alice", 9000, new Equal(List.of("alice", "bob", "carol")));        // dinner 90.00
        ledger.addExpense("bob", 3000, new Exact(Map.of("carol", 2000L, "dave", 1000L)));     // taxi 30.00
        ledger.addExpense("dave", 1000, new Equal(List.of("alice", "bob", "carol")));         // coffee 10.00
        System.out.println("balances (cents): " + ledger.balances());
        ledger.settle().forEach(System.out::println);
    }
}
```
Output:
```text
balances (cents): {alice=5666, bob=-333, carol=-5333, dave=0}
carol pays alice 53.33
bob pays alice 3.33
```
Design notes: integer cents avoid rounding drift, and leftover cents from equal splits are assigned deterministically so balances always sum to zero. The sealed `Split` with an exhaustive `switch` makes adding a `Percent` split a compile-checked change. The greedy settlement produces at most n-1 payments; the true minimum is NP-hard in general, which is worth saying in the interview.
</details>

## 8. Movie ticket booking with seat holds
Requirements: users select seats for a show; selected seats are held for 5 minutes; holds expire; confirming turns a hold into a booking; two users can never book the same seat, even if they click at the same moment.

Hint: `ConcurrentHashMap<SeatId, Hold>` with atomic `compute` per seat; all-or-nothing for multi-seat holds; injected clock for expiry.

<details><summary>Solution</summary>

```java
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

public class TicketBookingDesign {

    enum Status { HELD, BOOKED }
    record SeatState(String user, Status status, long expiresAtNanos) { }

    static final class Show {
        private final Map<String, SeatState> seats = new ConcurrentHashMap<>();
        private final LongSupplier clock;
        private final long holdNanos;

        Show(LongSupplier clock, Duration hold) { this.clock = clock; this.holdNanos = hold.toNanos(); }

        private boolean free(SeatState s, long now) {
            return s == null || (s.status() == Status.HELD && s.expiresAtNanos() <= now);
        }

        // all-or-nothing hold; per-seat atomic compute, rollback on partial failure
        boolean hold(String user, List<String> seatIds) {
            long now = clock.getAsLong();
            List<String> acquired = new ArrayList<>();
            for (String seat : seatIds) {
                SeatState after = seats.compute(seat, (k, s) ->
                        free(s, now) ? new SeatState(user, Status.HELD, now + holdNanos) : s);
                if (!after.user().equals(user) || after.status() != Status.HELD) {
                    acquired.forEach(a -> seats.computeIfPresent(a, (k, s) -> s.user().equals(user) ? null : s));
                    return false;
                }
                acquired.add(seat);
            }
            return true;
        }

        boolean confirm(String user, List<String> seatIds) {
            long now = clock.getAsLong();
            for (String seat : seatIds) {
                SeatState s = seats.get(seat);
                if (s == null || !s.user().equals(user) || s.status() != Status.HELD || s.expiresAtNanos() <= now) return false;
            }
            seatIds.forEach(seat -> seats.computeIfPresent(seat, (k, s) -> new SeatState(user, Status.BOOKED, Long.MAX_VALUE)));
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        long[] now = {0};
        Show show = new Show(() -> now[0], Duration.ofMinutes(5));

        System.out.println("alice holds A1,A2: " + show.hold("alice", List.of("A1", "A2")));
        System.out.println("bob holds A2,A3:   " + show.hold("bob", List.of("A2", "A3")));
        System.out.println("bob holds A3:      " + show.hold("bob", List.of("A3")) + " (A3 was rolled back earlier, so free)");
        now[0] = Duration.ofMinutes(6).toNanos();
        System.out.println("alice confirms after expiry: " + show.confirm("alice", List.of("A1", "A2")));
        System.out.println("bob holds A2,A3 after expiry: " + show.hold("bob", List.of("A2", "A3")));
        System.out.println("bob confirms A2,A3:           " + show.confirm("bob", List.of("A2", "A3")));

        // race: 50 users click seat B7 at the same instant
        Show premiere = new Show(System::nanoTime, Duration.ofMinutes(5));
        AtomicInteger winners = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 50; i++) {
                String user = "user" + i;
                pool.submit(() -> {
                    start.await();
                    if (premiere.hold(user, List.of("B7"))) winners.incrementAndGet();
                    return null;
                });
            }
            start.countDown();
        }
        System.out.println("winners for B7: " + winners.get());
    }
}
```
Output:
```text
alice holds A1,A2: true
bob holds A2,A3:   false
bob holds A3:      true (A3 was rolled back earlier, so free)
alice confirms after expiry: false
bob holds A2,A3 after expiry: true
bob confirms A2,A3:           true
winners for B7: 1
```
Design notes: `ConcurrentHashMap.compute` is atomic per key, so the check "is the seat free?" and the update happen together; there is no global lock. Bob's first request fails on A2 and rolls back his A3 hold; after alice's hold expires (and bob's own A3 hold too), he holds and confirms both seats. Expired holds are treated as free lazily, so no cleanup thread is needed for correctness. In production the same idea lives in the database: a unique constraint on (show, seat) for bookings, or `SELECT ... FOR UPDATE SKIP LOCKED`, or Redis `SET seat user NX PX 300000` for holds.
</details>

## 9. Notification service
Requirements: when an order event happens, notify the customer through their preferred channels (email, SMS, push); channels can fail and should be retried twice; adding a channel must not change the dispatcher; one failing channel must not block the others.

Hint: Observer for event subscription, Strategy for channels, Decorator for retry.

<details><summary>Solution</summary>

```java
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationDesign {

    enum ChannelType { EMAIL, SMS, PUSH }
    record Customer(String id, Set<ChannelType> preferences) { }
    record OrderEvent(String orderId, Customer customer, String message) { }

    interface Channel { void send(Customer c, String message); }

    static final class EmailChannel implements Channel {
        public void send(Customer c, String m) { System.out.println("  email -> " + c.id() + ": " + m); }
    }

    static final class FlakySmsChannel implements Channel {
        private int calls;
        public void send(Customer c, String m) {
            if (++calls == 1) throw new IllegalStateException("SMS gateway timeout");
            System.out.println("  sms   -> " + c.id() + ": " + m);
        }
    }

    static final class BrokenPushChannel implements Channel {
        public void send(Customer c, String m) { throw new IllegalStateException("push service down"); }
    }

    // Decorator: retry any channel
    static final class Retrying implements Channel {
        private final Channel delegate;
        private final int attempts;
        Retrying(Channel delegate, int attempts) { this.delegate = delegate; this.attempts = attempts; }

        public void send(Customer c, String m) {
            for (int i = 1; ; i++) {
                try {
                    delegate.send(c, m);
                    return;
                } catch (RuntimeException e) {
                    if (i == attempts) throw e;
                    System.out.println("  retry " + i + " after: " + e.getMessage());
                }
            }
        }
    }

    // Observer subject + strategy registry
    static final class NotificationDispatcher {
        private final Map<ChannelType, Channel> channels = new EnumMap<>(ChannelType.class);
        final List<String> deadLetters = new ArrayList<>();

        void register(ChannelType type, Channel channel) { channels.put(type, new Retrying(channel, 3)); }

        void onEvent(OrderEvent event) {
            for (ChannelType type : event.customer().preferences()) {
                Channel channel = channels.get(type);
                if (channel == null) continue;
                try {
                    channel.send(event.customer(), event.message());
                } catch (RuntimeException e) {                // isolate failures per channel
                    deadLetters.add(type + ":" + event.orderId() + ":" + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        NotificationDispatcher dispatcher = new NotificationDispatcher();
        dispatcher.register(ChannelType.EMAIL, new EmailChannel());
        dispatcher.register(ChannelType.SMS, new FlakySmsChannel());
        dispatcher.register(ChannelType.PUSH, new BrokenPushChannel());

        Customer alice = new Customer("alice", java.util.EnumSet.of(ChannelType.EMAIL, ChannelType.SMS, ChannelType.PUSH));
        System.out.println("order o-1 shipped:");
        dispatcher.onEvent(new OrderEvent("o-1", alice, "Your order o-1 has shipped"));
        System.out.println("dead letters: " + dispatcher.deadLetters);
    }
}
```
Output:
```text
order o-1 shipped:
  email -> alice: Your order o-1 has shipped
  retry 1 after: SMS gateway timeout
  sms   -> alice: Your order o-1 has shipped
  retry 1 after: push service down
  retry 2 after: push service down
dead letters: [PUSH:o-1:push service down]
```
Design notes: channels are Strategies chosen by customer preference (`EnumSet` keeps a stable order), retry is a Decorator applied uniformly at registration, and failures go to a dead-letter list for later reprocessing instead of breaking the loop. In OrderHub this becomes a Kafka consumer (Phase 20) with per-channel retry topics; templates per event type and locale would be another Strategy.
</details>

## 10. In-memory key-value store with TTL
Requirements: `put(key, value, ttl)`, `get(key)` returns empty after expiry, `delete`, size counts only live keys; thread-safe; a background sweeper removes expired keys so memory does not grow; testable without sleeping.

Hint: `ConcurrentHashMap<K, Entry>` with an expiry timestamp; lazy expiry on read plus a periodic sweep; injected clock.

<details><summary>Solution</summary>

```java
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public class TtlStoreDesign<K, V> {

    private record Entry<V>(V value, long expiresAt) { }

    private final Map<K, Entry<V>> data = new ConcurrentHashMap<>();
    private final LongSupplier clock;

    public TtlStoreDesign(LongSupplier nanoClock) { this.clock = nanoClock; }

    public void put(K key, V value, Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) throw new IllegalArgumentException("ttl must be positive");
        data.put(key, new Entry<>(value, clock.getAsLong() + ttl.toNanos()));
    }

    public Optional<V> get(K key) {
        Entry<V> e = data.get(key);
        if (e == null) return Optional.empty();
        if (e.expiresAt() <= clock.getAsLong()) {
            data.remove(key, e);                     // remove only this exact entry: a concurrent put wins
            return Optional.empty();
        }
        return Optional.of(e.value());
    }

    public boolean delete(K key) { return data.remove(key) != null; }

    // called by a ScheduledExecutorService every few seconds in production
    public int sweep() {
        long now = clock.getAsLong();
        int before = data.size();
        data.entrySet().removeIf(en -> en.getValue().expiresAt() <= now);
        return before - data.size();
    }

    public long liveSize() {
        long now = clock.getAsLong();
        return data.values().stream().filter(e -> e.expiresAt() > now).count();
    }

    public static void main(String[] args) {
        long[] now = {0};
        TtlStoreDesign<String, String> store = new TtlStoreDesign<>(() -> now[0]);
        store.put("session:1", "alice", Duration.ofSeconds(30));
        store.put("session:2", "bob", Duration.ofSeconds(90));
        store.put("otp:alice", "482913", Duration.ofSeconds(60));

        now[0] = Duration.ofSeconds(45).toNanos();
        System.out.println("t=45s session:1 " + store.get("session:1") + ", otp " + store.get("otp:alice"));
        System.out.println("t=45s live keys: " + store.liveSize());

        now[0] = Duration.ofSeconds(61).toNanos();
        System.out.println("t=61s sweep removed: " + store.sweep() + ", live keys: " + store.liveSize());

        store.put("session:2", "bob-renewed", Duration.ofSeconds(90));    // renew = overwrite with new expiry
        now[0] = Duration.ofSeconds(120).toNanos();
        System.out.println("t=120s session:2 " + store.get("session:2"));
        System.out.println("deleted session:2: " + store.delete("session:2") + ", get after delete: " + store.get("session:2"));
    }
}
```
Output:
```text
t=45s session:1 Optional.empty, otp Optional[482913]
t=45s live keys: 2
t=61s sweep removed: 1, live keys: 1
t=120s session:2 Optional[bob-renewed]
deleted session:2: true, get after delete: Optional.empty
```
Design notes: at t=45 s the read of `session:1` removes it lazily, so the sweep at t=61 s only removes the OTP. `remove(key, e)` deletes only the exact expired entry, so a concurrent renewal is never lost. The injected clock makes the test instant. Follow-ups: memory bounds with LRU eviction (combine with problem 4), a `DelayQueue` or timing wheel for precise expiry, persistence (append-only log), and this is the core of how Redis implements `EXPIRE` (lazy plus sampled active expiry).
</details>

@@ 3 | TOPIC | Practice — Phase 17 review: Design Patterns | Cheat sheet, key code, top 10 questions and rapid-fire flashcards for Phase 17 | MUST
# Phase 17 review: Design Patterns

## Cheat sheet

| Idea | Remember |
|---|---|
| SRP | One reason to change (one actor) |
| OCP | Add new classes, do not edit working ones; interface + registry |
| LSP | Subtypes keep the contract; `Square extends Rectangle` breaks it |
| ISP | Small role interfaces |
| DIP | Depend on abstractions you own; DI supplies implementations |
| DRY | One source of each piece of knowledge, not of each similar line |
| KISS and YAGNI | Simplest working design; no speculative features; rule of three |
| Singleton | Holder idiom, enum, or DCL with `volatile`; prefer Spring beans |
| Factory | Static factory, simple factory, Factory Method (subclass), Abstract Factory (families) |
| Builder | Fluent, defaults, validate in `build()`, immutable result, copy collections |
| Adapter | Converts an interface; isolates vendors |
| Decorator | Same interface, adds behaviour, stackable (`java.io`) |
| Facade | Simple front over a subsystem |
| Proxy | Controls access; JDK (interfaces) vs CGLIB (classes); self-invocation bypasses it |
| Strategy | Interchangeable algorithms by composition; `Comparator` |
| Template Method | Fixed skeleton, overridable steps; `JdbcTemplate` uses callbacks |
| Observer | Publish-subscribe; Spring events are synchronous by default |
| Chain of Responsibility | Handlers pass along; filters and Spring Security |
| Spring | DI, Factory, Singleton, Proxy, Template, Strategy, Observer, Adapter, Front Controller, Chain |
| LLD rhythm | Clarify, entities, variation points and patterns, core code, concurrency, extensions |

### Key code snippets
```java
// OCP + Strategy: registry from injected implementations
public PaymentRouter(List<PaymentMethod> methods) {
    this.byType = methods.stream().collect(Collectors.toUnmodifiableMap(PaymentMethod::type, Function.identity()));
}
```

```java
// Singleton: holder idiom and enum
final class Registry {
    private Registry() { }
    private static final class Holder { static final Registry INSTANCE = new Registry(); }
    static Registry getInstance() { return Holder.INSTANCE; }
}
enum FeatureFlags { INSTANCE; }
```

```java
// Builder: validate once, immutable result
Order order = Order.builder("cust-7")
        .line("BOOK", 2, new BigDecimal("12.50"))
        .express(true)
        .build();       // throws if there are no lines
```

```java
// Decorator stack (assembled in one place)
SmsSender sms = new LoggingSmsSender(new RetryingSmsSender(new LegacySmsAdapter(vendor), 3));
```

```java
// JDK dynamic proxy
ReportService proxy = (ReportService) Proxy.newProxyInstance(
        ReportService.class.getClassLoader(), new Class<?>[]{ReportService.class},
        (p, method, args) -> method.invoke(target, args));
```

```java
// Observer in Spring: react only after commit
@Async
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void sendConfirmation(OrderPlaced event) { email.send(event.customerEmail(), "Order confirmed"); }
```

```java
// Chain of Responsibility in Spring Security
http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

```java
// LRU in one line of JDK
Map<K, V> lru = new LinkedHashMap<>(16, 0.75f, true) {
    protected boolean removeEldestEntry(Map.Entry<K, V> e) { return size() > capacity; }
};
```

## The 10 most likely interview questions

1. **Explain SOLID with examples.** SRP: split validator, repository, notifier. OCP: payment methods as beans in a registry. LSP: subtypes keep contracts. ISP: role interfaces. DIP: services depend on interfaces they own.
2. **How do you implement a thread-safe singleton and why is enum best?** Holder idiom, enum, or DCL with `volatile`; enum is safe against reflection and serialization.
3. **Factory Method vs Abstract Factory?** One product via subclass override vs families of products via a factory object.
4. **When do you use a Builder?** Many or optional parameters, validation across fields, immutable result.
5. **Adapter vs Decorator vs Proxy vs Facade?** Convert interface; add behaviour with same interface; control access with same interface; simplify a subsystem.
6. **What is the self-invocation problem?** Calls on `this` bypass Spring proxies, so `@Transactional` and `@Cacheable` do nothing; call through another bean.
7. **Strategy vs Template Method?** Composition and runtime choice vs inheritance with fixed skeleton.
8. **How do Spring events work and what are the pitfalls?** Synchronous Observer in the same transaction by default; use `@Async` and `@TransactionalEventListener` for side effects.
9. **Which patterns does Spring use?** DI and IoC, Factory, Singleton, Proxy, Template Method, Strategy, Observer, Adapter, Front Controller, Chain of Responsibility, Builder, Facade.
10. **Design a parking lot (or LRU cache, rate limiter).** Clarify, entities, Strategy for pricing or algorithm, synchronized or per-key atomic allocation, injected clock, extensions.

## Rapid-fire flashcards

Q: Which SOLID principle does a growing `switch` over types violate?
A: Open/Closed.

Q: Which keyword makes double-checked locking correct?
A: `volatile`.

Q: Which JDK class is the classic Adapter in java.io?
A: `InputStreamReader`.

Q: Which proxy type can only proxy interfaces?
A: JDK dynamic proxy (`java.lang.reflect.Proxy`).

Q: What does `getBean("&myFactory")` return?
A: The `FactoryBean` itself.

Q: Which pattern is `DispatcherServlet`?
A: Front Controller.

Q: Which pattern lets you stack logging and retry around a client?
A: Decorator.

Q: Which pattern is `Comparator`?
A: Strategy.

Q: Which data structures give an O(1) LRU cache?
A: A hash map plus a doubly linked list.

Q: What is the first step in every low-level design interview?
A: Clarify requirements and scope before naming classes.
