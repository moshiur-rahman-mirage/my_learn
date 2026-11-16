@@ 1 | MODULE | Phase 4 — Databases | SQL, PostgreSQL design, indexing, tuning, MongoDB | MUST
# Phase 4: Databases

The written test almost always includes **SQL queries written by hand**. The viva will ask about **schema design, indexes and query tuning** for financial data.

Practice tables used in this phase:
```sql
po(id, name, district)
member(id, po_id, name, phone, joined_on)
loan(id, member_id, principal, outstanding, status, disbursed_on)
installment(id, loan_id, due_date, amount, paid_amount, paid_on)
employee(id, name, dept_id, salary, manager_id)
```

@@ 2 | MODULE | SQL Mastery | SELECT, joins, grouping, subqueries, CTEs, window functions | MUST
SQL is a **declarative** language: you describe *what* you want, and the database figures out *how* to get it.
**Analogy:** you tell a taxi driver the destination, not every turn.

@@ 3 | TOPIC | SELECT Basics and Query Order | WHERE, ORDER BY, LIMIT, NULL handling, logical execution order | MUST
# SELECT basics

```sql
SELECT name, phone
FROM member
WHERE po_id = 3
  AND joined_on >= DATE '2024-01-01'
  AND phone IS NOT NULL
ORDER BY name ASC
LIMIT 20 OFFSET 40;          -- page 3 when the page size is 20
```

**Line by line**
- `SELECT name, phone`: which columns to return. Avoid `SELECT *` in application code (extra data, and it breaks when columns change).
- `FROM member`: which table.
- `WHERE ...`: keep only the matching rows.
- `IS NOT NULL`: **never write `= NULL`**. NULL means "unknown", and `x = NULL` is never true.
- `ORDER BY name ASC`: sort (DESC for reverse).
- `LIMIT 20 OFFSET 40`: skip 40 rows and return 20 (pagination).

## The logical order SQL runs in (very important)

```
FROM → WHERE → GROUP BY → HAVING → SELECT → DISTINCT → ORDER BY → LIMIT
```
- This is why you **can't use a SELECT alias in WHERE** (WHERE runs before SELECT), but you **can use it in ORDER BY**.
- **Analogy:** cooking: get the ingredients (FROM), throw away the bad ones (WHERE), group them into bowls (GROUP BY), throw away the small bowls (HAVING), plate them (SELECT), arrange them (ORDER BY), serve the first few (LIMIT).

## Useful operators

```sql
SELECT * FROM loan WHERE status IN ('ACTIVE', 'OVERDUE');
SELECT * FROM loan WHERE principal BETWEEN 10000 AND 50000;       -- inclusive
SELECT * FROM member WHERE name ILIKE 'rah%';                      -- case-insensitive, starts with "rah"
SELECT COALESCE(paid_amount, 0) FROM installment;                  -- NULL -> 0
SELECT name, CASE WHEN outstanding = 0 THEN 'Closed'
                  WHEN outstanding < 5000 THEN 'Small'
                  ELSE 'Large' END AS bucket
FROM loan l JOIN member m ON m.id = l.member_id;
SELECT DISTINCT district FROM po;
```
- `IN (...)`: shorthand for several ORs.
- `BETWEEN a AND b`: includes both ends.
- `LIKE 'rah%'`: `%` = any characters, `_` = exactly one character. `ILIKE` is Postgres-only and case-insensitive.
- `COALESCE(a, b)`: returns the first non-NULL value. Great for turning NULLs into 0.
- `CASE WHEN ... THEN ... ELSE ... END`: an if/else inside SQL.
- `DISTINCT`: removes duplicate rows.

**NULL traps:** `NULL + 5 = NULL`, `COUNT(column)` skips NULLs (but `COUNT(*)` counts all rows), and `NOT IN (subquery with a NULL)` returns no rows. Use `NOT EXISTS` instead.

@@ 3 | TOPIC | Joins and Aggregation | INNER, LEFT, RIGHT, FULL, SELF joins; GROUP BY and HAVING | MUST
# Joins and aggregation

## Joins

**Analogy:** two class lists, "students" and "library cards".
- **INNER JOIN:** students who **have** a card.
- **LEFT JOIN:** **all** students, with card info if they have one (NULL otherwise).
- **RIGHT JOIN:** all cards, with student info if there is one.
- **FULL JOIN:** everyone from both lists.

```sql
-- INNER: only members that have loans
SELECT m.name, l.principal
FROM member m
JOIN loan l ON l.member_id = m.id;

-- LEFT: all members, even those with no loan
SELECT m.name, l.id AS loan_id
FROM member m
LEFT JOIN loan l ON l.member_id = m.id;

-- Members with NO loans (anti-join)
SELECT m.name
FROM member m
LEFT JOIN loan l ON l.member_id = m.id
WHERE l.id IS NULL;

-- SELF join: each employee with their manager's name
SELECT e.name AS employee, mgr.name AS manager
FROM employee e
LEFT JOIN employee mgr ON mgr.id = e.manager_id;
```

**Line by line**
- `member m`: `m` is an **alias**, a short nickname for the table.
- `ON l.member_id = m.id`: **how** rows match (FK = PK).
- `JOIN` alone means `INNER JOIN`.
- Anti-join: after a LEFT JOIN, members with no loan have NULLs in every loan column, so `WHERE l.id IS NULL` finds them.
- Self join: the same table twice with different aliases. `LEFT` keeps the boss, who has no manager.
- **Trap:** putting a condition on the right table in `WHERE` turns a LEFT JOIN into an INNER JOIN. Put it in `ON` instead: `LEFT JOIN loan l ON l.member_id = m.id AND l.status = 'ACTIVE'`.

## Aggregation: GROUP BY and HAVING

```sql
-- Number of loans and total outstanding per PO, only POs above 1 million
SELECT p.name,
       COUNT(l.id)          AS loan_count,
       SUM(l.outstanding)   AS total_outstanding,
       AVG(l.principal)     AS avg_loan,
       MAX(l.disbursed_on)  AS last_disbursed
FROM po p
JOIN member m ON m.po_id = p.id
JOIN loan l   ON l.member_id = m.id
WHERE l.status = 'ACTIVE'
GROUP BY p.id, p.name
HAVING SUM(l.outstanding) > 1000000
ORDER BY total_outstanding DESC;
```

**Line by line**
- Aggregate functions: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`. They squeeze many rows into one value per group.
- `GROUP BY p.id, p.name`: one output row per PO. **Every non-aggregated column in SELECT must appear in GROUP BY.**
- `WHERE` filters **rows before grouping**. `HAVING` filters **groups after aggregation**.
- `ORDER BY total_outstanding`: an alias is allowed here, because ORDER BY runs after SELECT.

**WHERE vs HAVING in one line:** WHERE picks which students enter the exam hall, and HAVING picks which classes passed on average.

@@ 3 | TOPIC | Subqueries, CTEs and Window Functions | Nested queries, WITH, ROW_NUMBER, RANK, running totals | MUST
# Subqueries, CTEs and window functions

## Subqueries

```sql
-- Employees earning more than the company average (scalar subquery)
SELECT name, salary FROM employee
WHERE salary > (SELECT AVG(salary) FROM employee);

-- Members who have at least one overdue installment (EXISTS)
SELECT m.name FROM member m
WHERE EXISTS (
    SELECT 1 FROM loan l
    JOIN installment i ON i.loan_id = l.id
    WHERE l.member_id = m.id AND i.due_date < CURRENT_DATE AND i.paid_on IS NULL
);

-- Second-highest salary (a classic)
SELECT MAX(salary) FROM employee
WHERE salary < (SELECT MAX(salary) FROM employee);
```
- A scalar subquery returns one value, so it can be compared with `>`.
- `EXISTS` stops at the first matching row, which makes it efficient. `SELECT 1` because the selected value doesn't matter.
- **Correlated subquery:** the inner query uses `m.id` from the outer query, so it is (logically) evaluated per outer row.

## CTE (Common Table Expression): a named temporary result

```sql
WITH overdue AS (
    SELECT l.member_id, SUM(i.amount - COALESCE(i.paid_amount, 0)) AS overdue_amount
    FROM installment i
    JOIN loan l ON l.id = i.loan_id
    WHERE i.due_date < CURRENT_DATE AND COALESCE(i.paid_amount, 0) < i.amount
    GROUP BY l.member_id
)
SELECT m.name, o.overdue_amount
FROM overdue o
JOIN member m ON m.id = o.member_id
ORDER BY o.overdue_amount DESC;
```
- `WITH name AS (...)`: like declaring a variable for a sub-result. It makes long queries **readable**, step by step.
- **Analogy:** writing rough work on the side of the exam paper before the final answer.
- `WITH RECURSIVE` walks hierarchies (an employee → manager chain, a category tree).

## Window functions: calculations across rows **without collapsing them**

```sql
SELECT name, dept_id, salary,
       ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rn,
       RANK()       OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk,
       DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS drnk,
       AVG(salary)  OVER (PARTITION BY dept_id)                      AS dept_avg
FROM employee;

-- Top 2 earners per department
SELECT * FROM (
    SELECT name, dept_id, salary,
           DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS r
    FROM employee
) t WHERE r <= 2;

-- Running total of repayments per loan
SELECT loan_id, paid_on, paid_amount,
       SUM(paid_amount) OVER (PARTITION BY loan_id ORDER BY paid_on) AS running_paid
FROM installment WHERE paid_on IS NOT NULL;

-- Compare with the previous installment
SELECT loan_id, due_date, amount,
       LAG(amount) OVER (PARTITION BY loan_id ORDER BY due_date) AS prev_amount
FROM installment;
```

**Line by line**
- `OVER (...)`: makes it a window function. Every row is kept, and a value computed over its "window" is added.
- `PARTITION BY dept_id`: restart the calculation for each department (like GROUP BY, but without merging rows).
- `ORDER BY salary DESC` inside OVER: the order used for ranking or running totals.
- For salaries 100, 100, 90: `ROW_NUMBER` = 1, 2, 3 (always unique); `RANK` = 1, 1, **3** (leaves a gap); `DENSE_RANK` = 1, 1, **2** (no gap).
- You can't use a window function in WHERE (it runs after WHERE), so wrap it in a subquery and filter outside.
- `SUM(...) OVER (... ORDER BY paid_on)`: a cumulative sum up to the current row.
- `LAG` / `LEAD`: the value from the previous / next row.

**Analogy:** GROUP BY is making a class summary sheet (one line per class). A window function writes "your rank in your class" next to each student's name, keeping every student.

@@ 3 | TOPIC | DDL, Constraints, Upsert, Views and Functions | CREATE/ALTER, ON CONFLICT, views, materialized views, triggers | MUST
# DDL, constraints, upsert, views

## DDL with constraints

```sql
CREATE TABLE installment (
    id           BIGSERIAL PRIMARY KEY,
    loan_id      BIGINT NOT NULL REFERENCES loan(id) ON DELETE RESTRICT,
    seq_no       INT    NOT NULL,
    due_date     DATE   NOT NULL,
    amount       NUMERIC(14,2) NOT NULL CHECK (amount > 0),
    paid_amount  NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (paid_amount >= 0),
    paid_on      DATE,
    UNIQUE (loan_id, seq_no)
);

ALTER TABLE loan ADD COLUMN next_due_date DATE;
ALTER TABLE loan ALTER COLUMN status SET DEFAULT 'PENDING';
DROP TABLE IF EXISTS temp_import;
TRUNCATE TABLE staging_installment;    -- delete all rows fast, no WHERE
```

**Line by line**
- `REFERENCES loan(id) ON DELETE RESTRICT`: you can't delete a loan that has installments (protects financial history). Other options: `CASCADE` (delete the children too), `SET NULL`.
- `CHECK (amount > 0)`: the DB rejects bad data.
- `DEFAULT 0`: the value used when the insert doesn't provide one.
- `UNIQUE (loan_id, seq_no)`: no two "installment #3" rows for the same loan.
- DDL = Data **Definition** (CREATE, ALTER, DROP, TRUNCATE). DML = Data **Manipulation** (INSERT, UPDATE, DELETE). DCL = GRANT/REVOKE. TCL = COMMIT/ROLLBACK.
- `DELETE` vs `TRUNCATE`: DELETE removes rows one by one (WHERE allowed, fires triggers). TRUNCATE empties the table at once (much faster).

## Upsert: insert, or update if it already exists

```sql
INSERT INTO member_balance (member_id, balance)
VALUES (42, 500.00)
ON CONFLICT (member_id)
DO UPDATE SET balance = member_balance.balance + EXCLUDED.balance;
```
- `ON CONFLICT (member_id)`: if a row with this unique key already exists...
- `EXCLUDED.balance`: the value we **tried** to insert (500.00).
- Atomic, with no "check then insert" race condition. `DO NOTHING` simply skips duplicates.

## Views and materialized views

```sql
CREATE VIEW v_active_loans AS
SELECT l.id, m.name, l.outstanding FROM loan l JOIN member m ON m.id = l.member_id
WHERE l.status = 'ACTIVE';

CREATE MATERIALIZED VIEW mv_po_portfolio AS
SELECT m.po_id, SUM(l.outstanding) AS outstanding, COUNT(*) AS loans
FROM loan l JOIN member m ON m.id = l.member_id GROUP BY m.po_id;

REFRESH MATERIALIZED VIEW CONCURRENTLY mv_po_portfolio;   -- needs a unique index
```
- **View:** a saved query. It runs fresh every time (no stored data). **Analogy:** a shortcut on your desktop.
- **Materialized view:** stores the result like a table. Reads are fast, but the data is only as fresh as the last `REFRESH`. Great for dashboards.
- `CONCURRENTLY`: readers aren't blocked during the refresh.

## Function and trigger (an audit example)

```sql
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_loan_updated
BEFORE UPDATE ON loan
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
```
- `NEW`: the row as it will be saved. We set `updated_at` before the update happens.
- `BEFORE UPDATE ... FOR EACH ROW`: runs for every updated row.
- Triggers are powerful but "hidden". Keep business logic in the application, and use triggers for audit/timestamps.

@@ 3 | TOPIC | SQL Practice Problems | Classic written-test queries with solutions | MUST
# SQL practice problems

Write each one on paper first, then check.

**1. Find duplicate phone numbers among members.**
```sql
SELECT phone, COUNT(*) AS cnt
FROM member
GROUP BY phone
HAVING COUNT(*) > 1;
```
- Group by the value that should be unique. Any group with more than 1 row is a duplicate.

**2. Delete duplicates, keeping the lowest id.**
```sql
DELETE FROM member a
USING member b
WHERE a.phone = b.phone AND a.id > b.id;
```
- Join the table with itself. For each pair with the same phone, delete the one with the higher id.

**3. Nth highest salary (N = 3).**
```sql
SELECT DISTINCT salary FROM employee
ORDER BY salary DESC
OFFSET 2 LIMIT 1;
-- or: SELECT salary FROM (SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) r FROM employee) t WHERE r = 3;
```
- `DISTINCT` so that tied salaries count once. `OFFSET N-1` skips the top N-1.

**4. Employees who earn more than their manager.**
```sql
SELECT e.name
FROM employee e
JOIN employee m ON m.id = e.manager_id
WHERE e.salary > m.salary;
```

**5. Department with the highest total salary.**
```sql
SELECT dept_id, SUM(salary) AS total
FROM employee GROUP BY dept_id
ORDER BY total DESC LIMIT 1;
```

**6. Monthly disbursement totals for 2025.**
```sql
SELECT date_trunc('month', disbursed_on) AS month, SUM(principal) AS total
FROM loan
WHERE disbursed_on >= '2025-01-01' AND disbursed_on < '2026-01-01'
GROUP BY 1 ORDER BY 1;
```
- `date_trunc('month', d)` rounds a date down to the first day of its month.
- A half-open range (`>= start AND < next start`) is safer than BETWEEN for dates/timestamps, and it can use an index (unlike `EXTRACT(YEAR FROM ...) = 2025`).

**7. PAR (Portfolio at Risk) per PO.**
```sql
WITH loan_risk AS (
    SELECT l.id, l.outstanding, m.po_id,
           EXISTS (SELECT 1 FROM installment i
                   WHERE i.loan_id = l.id AND i.due_date < CURRENT_DATE
                     AND i.paid_amount < i.amount) AS at_risk
    FROM loan l JOIN member m ON m.id = l.member_id
    WHERE l.status = 'ACTIVE'
)
SELECT po_id,
       SUM(outstanding) FILTER (WHERE at_risk) AS risky,
       SUM(outstanding) AS total,
       ROUND(100.0 * SUM(outstanding) FILTER (WHERE at_risk) / NULLIF(SUM(outstanding), 0), 2) AS par_pct
FROM loan_risk GROUP BY po_id;
```
- The CTE marks each active loan as at risk when it has any overdue, under-paid installment.
- `SUM(...) FILTER (WHERE at_risk)`: sums only the risky loans (Postgres syntax; elsewhere use `SUM(CASE WHEN at_risk THEN outstanding END)`).
- `NULLIF(x, 0)`: turns 0 into NULL so that we never divide by zero.
- `100.0 *`: forces decimal division instead of integer division.

**8. Members who joined but never took a loan.**
```sql
SELECT m.* FROM member m
WHERE NOT EXISTS (SELECT 1 FROM loan l WHERE l.member_id = m.id);
```

**9. Latest loan of each member.**
```sql
SELECT DISTINCT ON (member_id) member_id, id, disbursed_on
FROM loan ORDER BY member_id, disbursed_on DESC;
```
- `DISTINCT ON` (Postgres): keeps the **first** row per member, according to ORDER BY. The portable alternative is `ROW_NUMBER() ... WHERE rn = 1`.

@@ 2 | MODULE | PostgreSQL Design, Indexing and Tuning | Schema design, data types, indexes, EXPLAIN, MVCC | MUST
PostgreSQL is the main database in the TOR. Know **how to design tables for money**, **which index to create** and **how to read EXPLAIN**.

@@ 3 | TOPIC | Schema Design for a Finance System | Data types, ledger, constraints, example schema | MUST
# Schema design for a finance system

## Choosing data types

| Data | Type | Why |
|---|---|---|
| Money | `NUMERIC(14,2)` | Exact decimal. **Never** `float`/`real`/`money` |
| IDs | `BIGINT` / `BIGSERIAL` (or `UUID`) | INT runs out at 2.1 billion |
| Date only | `DATE` | Due dates |
| Date + time | `TIMESTAMPTZ` | Stored in UTC, shown in the user's zone |
| Short text | `VARCHAR(n)` / `TEXT` | In Postgres they perform the same |
| Status | `VARCHAR` + `CHECK` (or an enum type) | Readable, constrained |
| Flexible extra data | `JSONB` | Indexed, queryable JSON |

## Example schema

```sql
CREATE TABLE po (
    id        BIGSERIAL PRIMARY KEY,
    code      VARCHAR(20) NOT NULL UNIQUE,
    name      VARCHAR(150) NOT NULL
);

CREATE TABLE member (
    id          BIGSERIAL PRIMARY KEY,
    po_id       BIGINT NOT NULL REFERENCES po(id),
    national_id VARCHAR(17) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    phone       VARCHAR(11)
);

CREATE TABLE loan (
    id           BIGSERIAL PRIMARY KEY,
    member_id    BIGINT NOT NULL REFERENCES member(id),
    principal    NUMERIC(14,2) NOT NULL CHECK (principal > 0),
    outstanding  NUMERIC(14,2) NOT NULL CHECK (outstanding >= 0),
    status       VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','ACTIVE','CLOSED','WRITTEN_OFF')),
    disbursed_on DATE,
    version      BIGINT NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by   VARCHAR(50) NOT NULL
);

-- Immutable ledger: rows are only ever INSERTed, never updated or deleted
CREATE TABLE ledger_entry (
    id          BIGSERIAL PRIMARY KEY,
    loan_id     BIGINT NOT NULL REFERENCES loan(id),
    entry_type  VARCHAR(20) NOT NULL CHECK (entry_type IN ('DISBURSE','REPAY','REVERSAL','FEE')),
    amount      NUMERIC(14,2) NOT NULL,
    reference   VARCHAR(40) NOT NULL UNIQUE,      -- idempotency: the same receipt can't post twice
    reverses_id BIGINT REFERENCES ledger_entry(id),
    posted_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    posted_by   VARCHAR(50) NOT NULL
);
CREATE INDEX ix_ledger_loan ON ledger_entry(loan_id, posted_at);
```

**Line by line**
- Every FK (`po_id`, `member_id`, `loan_id`) has `REFERENCES`, so there are no orphan rows.
- `CHECK (status IN ...)`: the DB refuses typos like 'ACTVE'.
- `version`: for optimistic locking from JPA.
- `created_by` / `posted_by`: who did it (audit).
- **Ledger:** to correct a wrong payment you insert a `REVERSAL` row that points to the original (`reverses_id`). History is never rewritten. **Analogy:** an accountant never uses an eraser. They write a correcting entry in ink.
- `reference UNIQUE`: if the mobile app retries the same payment, the second insert fails instead of double-counting.
- The balance can always be recalculated as `SUM(ledger)`. `loan.outstanding` is a fast cached copy, kept in the same transaction.
- **Postgres does not auto-create indexes on foreign keys.** Add them yourself (`ix_ledger_loan`) or joins and deletes will be slow.

@@ 3 | TOPIC | PostgreSQL Indexing | B-tree, composite, partial, expression, GIN indexes | MUST
# PostgreSQL indexing

An **index** is a separate sorted structure that lets the DB find rows without scanning the whole table.
**Analogy:** the index at the back of a textbook. Instead of reading 500 pages to find "Hibernate", you look it up and jump to page 212.

```sql
-- 1. B-tree (default): =, <, >, BETWEEN, ORDER BY, LIKE 'abc%'
CREATE INDEX ix_member_phone ON member(phone);

-- 2. Composite: column order matters!
CREATE INDEX ix_loan_status_date ON loan(status, disbursed_on);

-- 3. Partial: index only the rows you query often
CREATE INDEX ix_installment_unpaid ON installment(due_date)
WHERE paid_on IS NULL;

-- 4. Expression: index a computed value
CREATE INDEX ix_member_lower_name ON member(lower(name));
-- used by: WHERE lower(name) = 'rahim'

-- 5. Unique: also enforces a rule
CREATE UNIQUE INDEX ux_member_nid ON member(national_id);

-- 6. Covering (INCLUDE): answer the query from the index alone
CREATE INDEX ix_loan_member_cov ON loan(member_id) INCLUDE (outstanding, status);

-- 7. GIN: JSONB, arrays, full-text search
CREATE INDEX ix_member_extra ON member USING GIN (extra_data);

-- Build without locking writes on a live table
CREATE INDEX CONCURRENTLY ix_loan_created ON loan(created_at);
```

**Line by line**
1. A **B-tree** is a balanced tree, giving O(log n) lookups. It is right for 95% of indexes.
2. **Composite `(status, disbursed_on)`** works like a phone book sorted by (surname, first name). It helps `WHERE status = ?` and `WHERE status = ? AND disbursed_on > ?`, but **not** `WHERE disbursed_on > ?` alone (the **leftmost prefix rule**). Put equality columns first and range columns last.
3. **Partial index:** only unpaid installments (a small fraction) are indexed. It is smaller and faster, and perfect for "overdue" queries.
4. **Expression index:** `WHERE lower(name) = ...` can't use a plain index on `name`, but can use one on `lower(name)`.
5. A **unique index** both speeds up lookups and prevents duplicates.
6. **INCLUDE** stores extra columns in the index, giving an **Index Only Scan** (no table visit).
7. **GIN** (Generalized Inverted Index): for "contains" queries on JSONB/arrays/text.
- `CONCURRENTLY`: slower to build, but production traffic isn't blocked.

## The cost of indexes
- Every INSERT/UPDATE/DELETE must update **every index**, so writes get slower.
- They use disk and memory.
- So: **index the columns used in WHERE, JOIN and ORDER BY of frequent queries**, and drop unused indexes (check `pg_stat_user_indexes`).

@@ 3 | TOPIC | When an Index Is Not Used | Common reasons Postgres chooses a sequential scan | MUST
# When an index is not used

You created an index, but EXPLAIN shows `Seq Scan`. The common reasons:

```sql
-- 1. A function on the column
SELECT * FROM member WHERE lower(name) = 'rahim';       -- index on name is not used
-- fix: an expression index on lower(name)

-- 2. A leading wildcard
SELECT * FROM member WHERE name LIKE '%rahim';           -- B-tree can't help
-- fix: a pg_trgm GIN index, or full-text search

-- 3. Type mismatch / implicit cast
SELECT * FROM member WHERE phone = 1712345678;           -- phone is VARCHAR, compared to a number
-- fix: WHERE phone = '01712345678'

-- 4. Wrong column order in a composite index
-- index (status, disbursed_on), but the query only filters on disbursed_on

-- 5. Low selectivity: the query returns a big part of the table
SELECT * FROM loan WHERE status = 'ACTIVE';              -- 90% of rows are ACTIVE
-- a seq scan really is faster here

-- 6. Arithmetic on the column
SELECT * FROM loan WHERE disbursed_on + 30 > CURRENT_DATE;
-- fix: WHERE disbursed_on > CURRENT_DATE - 30

-- 7. OR across different columns (sometimes)
-- fix: UNION of two indexed queries, or let Postgres use a BitmapOr

-- 8. Stale statistics
ANALYZE loan;
```

**Why each happens**
1. The index stores `name`, not `lower(name)`. The DB would have to compute lower() for every row anyway.
2. A B-tree is sorted from the **start** of the string. "Ends with rahim" is like finding every word that ends in "-tion" in a dictionary: the ordering doesn't help.
3. Casting the column for every row makes the index useless.
5. If most rows match, jumping through the index row by row is slower than reading the table in one go. **Analogy:** if you need 450 of 500 pages, just read the book instead of using the index.
6. Keep the column "bare" on one side of the comparison so that the index can be used (a *sargable* condition).
8. The planner chooses using **statistics**. After big data changes, run `ANALYZE` (autovacuum usually does it).

@@ 3 | TOPIC | Query Tuning with EXPLAIN ANALYZE | Reading plans, scan and join types, tuning checklist | MUST
# Query tuning with EXPLAIN ANALYZE

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT m.name, l.outstanding
FROM loan l JOIN member m ON m.id = l.member_id
WHERE l.status = 'OVERDUE' AND m.po_id = 7;
```
Example output (simplified):
```
Hash Join  (cost=120.5..890.2 rows=150 width=40) (actual time=2.1..9.8 rows=162 loops=1)
  Hash Cond: (l.member_id = m.id)
  ->  Index Scan using ix_loan_status on loan l  (actual rows=1200)
        Index Cond: (status = 'OVERDUE')
  ->  Hash  (actual rows=800)
        ->  Seq Scan on member m  (actual rows=800)
              Filter: (po_id = 7)
              Rows Removed by Filter: 99200
Planning Time: 0.3 ms
Execution Time: 10.1 ms
```

**How to read it**
- `EXPLAIN` alone shows the **estimated** plan. `ANALYZE` actually **runs** the query and shows the real times and row counts. (Careful with UPDATE/DELETE: wrap them in BEGIN ... ROLLBACK.)
- `BUFFERS`: how many pages came from the cache vs disk.
- Read **from the innermost (most indented) node outward**.
- `cost=start..total`: the planner's estimate in arbitrary units.
- `rows=150` estimated vs `actual rows=162`: if these differ hugely (10× or more), the statistics are stale, so run `ANALYZE`.
- `Seq Scan on member ... Rows Removed by Filter: 99200`: it read 100k rows to keep 800. **That's the problem**, so add an index on `member(po_id)`.

## Scan types
| Scan | Meaning |
|---|---|
| Seq Scan | Read the whole table |
| Index Scan | Walk the index, then fetch the matching rows from the table |
| Index Only Scan | Everything is in the index, no table visit (best) |
| Bitmap Heap Scan | Collect matches from the index, then read the table pages in order (good for medium result sizes) |

## Join types
| Join | Good when |
|---|---|
| Nested Loop | Small outer side + indexed inner side |
| Hash Join | Large unsorted inputs, equality joins |
| Merge Join | Both inputs already sorted on the join key |

## Practical tuning checklist
1. Find slow queries: `pg_stat_statements` (top total time) and the slow query log (`log_min_duration_statement = 500ms`).
2. `EXPLAIN (ANALYZE, BUFFERS)` the worst ones.
3. Add or fix **indexes** for WHERE/JOIN/ORDER BY columns. Check composite column order.
4. Rewrite non-sargable conditions (functions on columns, leading `%`).
5. Select only the needed columns. Avoid `SELECT *`.
6. Use **keyset pagination** for deep pages instead of a big OFFSET:
   ```sql
   SELECT * FROM loan WHERE id > :lastSeenId ORDER BY id LIMIT 50;
   ```
   OFFSET 100000 still reads and throws away 100k rows. Keyset jumps straight there through the index.
7. Fix N+1 queries in the application.
8. Keep statistics fresh (`ANALYZE`) and make sure autovacuum keeps up.
9. Cache heavy reports (materialized views, Redis).
10. Use connection pooling (HikariCP / PgBouncer).

**Analogy:** EXPLAIN is the GPS route preview. ANALYZE is actually driving it and timing each road.

@@ 3 | TOPIC | MVCC and VACUUM | How Postgres handles concurrent reads and writes | SHOULD
# MVCC and VACUUM

**MVCC (Multi-Version Concurrency Control):** an UPDATE doesn't overwrite a row. It creates a **new version** and marks the old one as dead. Each transaction sees the versions that were committed when its snapshot was taken.

**Result:** **readers don't block writers, and writers don't block readers.**

**Analogy:** a Wikipedia page history. Editing creates a new revision, and someone reading the old revision isn't disturbed. Later, old revisions nobody can see any more are cleaned up.

```sql
-- Session A
BEGIN;
UPDATE loan SET outstanding = 4000 WHERE id = 1;   -- creates a new row version (not committed yet)

-- Session B (at the same time)
SELECT outstanding FROM loan WHERE id = 1;        -- still sees 5000, no waiting

-- Session A
COMMIT;                                            -- now new queries see 4000
```
- Each row version has hidden columns `xmin` (the transaction that created it) and `xmax` (the transaction that deleted or replaced it). Visibility is decided by comparing these with your snapshot.

## VACUUM
Dead row versions take up space (**bloat**).
- `VACUUM`: marks the dead rows' space as reusable (it doesn't shrink the file).
- `VACUUM FULL`: rewrites the table to reclaim disk space. It **locks the table**, so avoid it on busy tables.
- `ANALYZE`: refreshes planner statistics.
- **Autovacuum** does both automatically. Tune it for heavily updated tables, and don't disable it.
- Long-running transactions stop VACUUM from cleaning up (old snapshots still "need" old versions). Keep transactions short.

**Transaction ID wraparound:** transaction IDs are 32-bit. VACUUM "freezes" old rows to prevent wraparound. Another reason never to disable autovacuum.

@@ 3 | TOPIC | PostgreSQL Operations | Connection pooling, backup, replication, partitioning, roles | SHOULD
# PostgreSQL operations

## Connection pooling (HikariCP)
Opening a DB connection is slow (TCP + authentication + a process on the server). A **pool** keeps connections open and lends them out.
**Analogy:** a library lending books instead of printing a new copy for every reader.

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10        # not 100! More connections ≠ faster
      minimum-idle: 2
      connection-timeout: 30000    # ms to wait for a free connection
      max-lifetime: 1800000
```
- Each Postgres connection is a server **process** using memory. A small pool (≈ CPU cores × 2) usually gives the best throughput.
- Many app instances? Put **PgBouncer** in front of Postgres.

## Backup and restore
```bash
pg_dump -Fc -h localhost -U app loans > loans_2025-11-15.dump   # logical backup (custom format)
pg_restore -d loans_restore loans_2025-11-15.dump
```
- `pg_dump`: a consistent snapshot of one database (good for small/medium sizes).
- For large production systems: **physical backups + WAL archiving** (pgBackRest, Barman), which allows **PITR (Point-In-Time Recovery)**, e.g. "restore to 10:42, just before the bad delete".
- **A backup you haven't test-restored is not a backup.**

## Replication
- **Streaming replication:** the primary sends WAL to **read replicas**. Use the replicas for reports and failover.
- Synchronous (no data loss, slower) vs asynchronous (fast, may lose the last few transactions on failover).

## Partitioning
Split a huge table into smaller pieces by range (e.g. installments per year).
```sql
CREATE TABLE ledger (id BIGINT, posted_at DATE NOT NULL, amount NUMERIC(14,2))
PARTITION BY RANGE (posted_at);
CREATE TABLE ledger_2025 PARTITION OF ledger FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
```
- Queries with `WHERE posted_at ...` only touch the matching partitions (**partition pruning**). Old years can be archived or dropped quickly.

## Roles and least privilege
```sql
CREATE ROLE loan_app LOGIN PASSWORD '...';
GRANT SELECT, INSERT, UPDATE ON loan, member TO loan_app;
REVOKE DELETE ON ledger_entry FROM loan_app;     -- the app can never delete ledger rows
```
- The app user should **not** be a superuser or own the schema. Give it only what it needs.

@@ 2 | MODULE | MongoDB | Document database basics, modelling and aggregation | SHOULD
MongoDB stores **JSON-like documents** instead of rows. It is good for flexible or nested data such as logs, survey forms and activity feeds.

@@ 3 | TOPIC | MongoDB Basics and CRUD | Documents, collections, queries, Spring Data MongoDB | SHOULD
# MongoDB basics

| SQL | MongoDB |
|---|---|
| Database | Database |
| Table | Collection |
| Row | Document (BSON: binary JSON) |
| Column | Field |
| JOIN | Embedding or `$lookup` |
| Primary key | `_id` (ObjectId by default) |

**Analogy:** SQL is an Excel sheet with fixed columns. MongoDB is a folder of filled-in forms, where each form can have slightly different sections.

```javascript
// Insert
db.surveys.insertOne({
  memberId: 42,
  poCode: "PO-07",
  date: ISODate("2025-11-01"),
  answers: { income: 12000, householdSize: 5 },
  tags: ["rural", "agri"]
});

// Find: members with income > 10000, only some fields, newest first
db.surveys.find(
  { "answers.income": { $gt: 10000 }, tags: "agri" },
  { memberId: 1, "answers.income": 1, _id: 0 }
).sort({ date: -1 }).limit(10);

// Update
db.surveys.updateOne({ memberId: 42 }, { $set: { "answers.income": 15000 }, $push: { tags: "verified" } });

// Index
db.surveys.createIndex({ poCode: 1, date: -1 });
```

**Line by line**
- `insertOne`: no table definition is needed first. The document **is** the schema.
- `"answers.income"`: dot notation reaches into nested fields.
- `$gt`: greater than (others: `$lt`, `$in`, `$ne`, `$exists`).
- `tags: "agri"`: matches if the array **contains** "agri".
- The second argument of `find` is a **projection**: 1 = include, 0 = exclude.
- `$set` changes fields, `$push` appends to an array.
- `createIndex({poCode: 1, date: -1})`: a compound index (1 = ascending, -1 = descending).

## Spring Data MongoDB
```java
@Document("surveys")
public record Survey(@Id String id, Long memberId, String poCode, LocalDate date, Map<String, Object> answers) {}

public interface SurveyRepository extends MongoRepository<Survey, String> {
    List<Survey> findByPoCodeOrderByDateDesc(String poCode);
}
```
- It works just like JPA repositories: derived queries from method names.

@@ 3 | TOPIC | Embedding vs Referencing and Aggregation | Modelling choices and the aggregation pipeline | SHOULD
# Embedding vs referencing, and aggregation

## Embedding: put related data inside the document
```json
{ "_id": 1, "name": "Rahim",
  "addresses": [ { "type": "home", "village": "Kashipur" } ] }
```
## Referencing: store the id, like a foreign key
```json
{ "_id": 101, "memberId": 1, "amount": 50000 }
```

| Embed when | Reference when |
|---|---|
| Data is read together | Data is used independently |
| One-to-few (addresses, phones) | One-to-many or many-to-many (thousands of transactions) |
| The child doesn't change often | The child grows without limit (the document limit is **16 MB**) |

**Analogy:** your wallet holds your ID card (embedded, always together). Your bank statements stay at the bank (referenced, because they grow forever).

## Aggregation pipeline (like SQL GROUP BY, built in stages)

```javascript
db.loans.aggregate([
  { $match: { status: "ACTIVE" } },                                  // WHERE
  { $group: { _id: "$poCode",                                        // GROUP BY poCode
              total: { $sum: "$outstanding" },
              count: { $sum: 1 } } },
  { $sort: { total: -1 } },                                          // ORDER BY total DESC
  { $limit: 5 },
  { $lookup: { from: "pos", localField: "_id",                       // a LEFT JOIN
               foreignField: "code", as: "po" } }
]);
```

**Line by line**
- Each stage takes documents in, transforms them and passes them on, like an assembly line.
- `$match` first: filter early so that the later stages process fewer documents (and it can use indexes).
- `$group` with `_id: "$poCode"`: the `$` means "the value of this field". `$sum: 1` counts.
- `$lookup`: joins another collection. Fine occasionally, but if you need it everywhere, maybe the data belongs in SQL.

**MongoDB vs PostgreSQL for PKSF:** core money data (loans, ledger) needs strong ACID transactions and relations, so use **PostgreSQL**. Flexible data (survey forms, logs, audit snapshots, notifications) can use **MongoDB**. (Postgres `JSONB` can also cover many "flexible" needs.)

@@ 2 | TOPIC | Phase 4 — Interview Questions | Quick Q&A revision for databases | MUST
# Phase 4 interview questions

**Q. WHERE vs HAVING?**
A. WHERE filters rows before grouping. HAVING filters groups after aggregation (it can use SUM, COUNT...).

**Q. INNER vs LEFT JOIN?**
A. INNER returns only the matching rows from both tables. LEFT returns all rows from the left table, with NULLs where the right side has no match.

**Q. RANK vs DENSE_RANK vs ROW_NUMBER?**
A. For ties 100, 100, 90: ROW_NUMBER = 1, 2, 3; RANK = 1, 1, 3; DENSE_RANK = 1, 1, 2.

**Q. How does an index speed up queries? What does it cost?**
A. A B-tree lets the DB find rows in O(log n) instead of scanning the table. The cost is slower writes, extra storage and maintenance.

**Q. What is a composite index and the leftmost prefix rule?**
A. An index on several columns, sorted by the first column, then the second... It helps queries that filter on a leftmost prefix of those columns.

**Q. Why might Postgres ignore your index?**
A. A function on the column, a leading wildcard, a type mismatch, low selectivity, the wrong composite order, or stale statistics.

**Q. How do you find and fix a slow query?**
A. Find it (pg_stat_statements / slow log), EXPLAIN (ANALYZE, BUFFERS), look for seq scans with many rows removed and bad row estimates, then add the right index, rewrite the query, ANALYZE, or cache.

**Q. What is MVCC?**
A. Postgres keeps multiple row versions so that readers see a consistent snapshot without blocking writers. VACUUM later cleans up the dead versions.

**Q. Why NUMERIC for money?**
A. It is exact decimal arithmetic. Floating-point types introduce rounding errors.

**Q. DELETE vs TRUNCATE vs DROP?**
A. DELETE removes chosen rows (WHERE, triggers, can roll back). TRUNCATE quickly removes all rows. DROP removes the table itself.

**Q. What is a materialized view?**
A. A stored query result that is fast to read and must be refreshed. It is good for dashboards and reports.

**Q. OFFSET pagination problem?**
A. A large OFFSET still scans and discards rows, so it gets slower with every page. Keyset pagination (`WHERE id > last ORDER BY id LIMIT n`) stays fast.

**Q. SQL vs NoSQL: when do you choose MongoDB?**
A. MongoDB suits flexible, nested, rapidly changing schemas that are read as whole documents. SQL suits relational, transactional, consistent data such as money.

**Q. How do you prevent double-posting a payment?**
A. A unique constraint on a payment reference / idempotency key, in a transaction, plus optimistic or pessimistic locking on the balance row.

**Q. What is a deadlock in a database?**
A. Two transactions each hold a row lock the other needs. Postgres detects it and aborts one. Prevent it by locking rows in a consistent order and keeping transactions short.
