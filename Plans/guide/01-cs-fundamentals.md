@@ 1 | MODULE | Phase 1 — CS Fundamentals | Data structures, algorithms, OOP, OS, networks, DBMS | MUST
# Phase 1: CS Fundamentals

These topics show up in almost every written test as MCQs and handwritten code.
They are the **foundation of a house**: nobody sees them, but everything stands on them.

@@ 2 | MODULE | Data Structures | How data is arranged in memory | MUST
A **data structure** is a way of arranging data so that certain operations are fast.
Choosing one is like choosing a container: a **bookshelf** (array) for numbered access, a **chain** (linked list) for easy insertion, a **stack of plates** (stack) for last-in-first-out.

@@ 3 | TOPIC | Big-O Notation | How to measure speed of an algorithm | MUST
# Big-O notation

**Big-O** tells you how the running time (or memory) **grows** as the input size `n` grows. It ignores small details and constants.

**Analogy:** finding a friend's name.
- **O(1):** you know their seat number, so you go straight there.
- **O(log n):** a phone book. Open the middle and throw away half each time.
- **O(n):** check every person one by one.
- **O(n²):** compare every person with every other person (a handshake at a party).

| Big-O | Name | n = 1,000 → steps (roughly) |
|---|---|---|
| O(1) | constant | 1 |
| O(log n) | logarithmic | 10 |
| O(n) | linear | 1,000 |
| O(n log n) | linearithmic | 10,000 |
| O(n²) | quadratic | 1,000,000 |
| O(2ⁿ) | exponential | astronomically large |

```java
// O(n): one loop over the array
int sum(int[] a) {
    int total = 0;
    for (int x : a) total += x;
    return total;
}

// O(n²): a loop inside a loop
boolean hasDuplicate(int[] a) {
    for (int i = 0; i < a.length; i++)
        for (int j = i + 1; j < a.length; j++)
            if (a[i] == a[j]) return true;
    return false;
}
```

**Line by line**
- `sum`: the loop visits each element once. Double the array and you double the work, so it is **O(n)**.
- `hasDuplicate`: for each `i`, the inner loop visits the remaining elements. That is about n×n/2 comparisons, and we drop constants, so it is **O(n²)**.
- `j = i + 1`: we start after `i` so that we don't compare an element with itself or compare the same pair twice.

**Rules of thumb**
- Drop constants: O(2n) → O(n).
- Keep the biggest term: O(n² + n) → O(n²).
- Nested loops multiply. Loops one after another add.
- Halving each step gives log n.

Q: What is the time complexity of finding an item in a sorted array using binary search?
A: O(log n), because each step throws away half of the remaining items.

@@ 3 | TOPIC | Arrays vs Linked Lists | Complexity table and when to use which | MUST
# Arrays vs linked lists

| Structure | Access by index | Search | Insert | Delete | Notes |
|---|---|---|---|---|---|
| Array | O(1) | O(n) | O(n) | O(n) | Contiguous memory |
| Linked list | O(n) | O(n) | O(1) at head | O(1) with node ref | No random access |
| Stack / Queue | - | O(n) | O(1) | O(1) | LIFO / FIFO |
| Hash table | - | O(1) avg | O(1) avg | O(1) avg | O(n) worst case |
| Balanced BST | - | O(log n) | O(log n) | O(log n) | Unbalanced worst O(n) |
| Heap | O(1) top | O(n) | O(log n) | O(log n) | Priority queue |

**Array = a row of numbered houses on one street.** You can go straight to house #57 (O(1)). But to insert a new house in the middle, everyone after it must move one place (O(n)).

**Linked list = a treasure hunt.** Each clue (node) tells you where the next clue is. Adding a clue is easy (just change one pointer), but to reach clue #57 you must follow 56 clues first (O(n)).

```java
class ListNode {
    int val;          // the data stored in this node
    ListNode next;    // reference (pointer) to the next node, or null at the end
    ListNode(int val) { this.val = val; }
}

// Build 1 -> 2 -> 3
ListNode head = new ListNode(1);
head.next = new ListNode(2);
head.next.next = new ListNode(3);
```

**Line by line**
- `int val`: the value each node holds.
- `ListNode next`: the "arrow" to the next node. The last node has `next == null`.
- `head`: we only keep a reference to the **first** node. Everything else is reached by following `next`.

Q: Why does a linked list use more memory than an array?
A: Each node stores an extra reference (`next`) alongside the value, and nodes are separate objects with their own overhead.

@@ 3 | TOPIC | Linked List — Reverse and Detect Cycle | Two classic handwritten problems | MUST
# Linked list: reverse it, and detect a cycle

## 1. Reverse a linked list

**Idea:** walk through the list and turn every arrow around. **Analogy:** a line of people each pointing at the person in front. Ask each one, in turn, to point at the person behind instead.

```java
ListNode reverse(ListNode head) {
    ListNode prev = null;          // the reversed part (empty at start)
    ListNode cur = head;           // the node we are working on
    while (cur != null) {
        ListNode next = cur.next;  // 1. remember the rest of the list
        cur.next = prev;           // 2. turn the arrow backwards
        prev = cur;                // 3. move prev one step forward
        cur = next;                // 4. move cur one step forward
    }
    return prev;                   // prev is now the new head
}
```

**Line by line**
- `prev = null`: the new tail must point to `null`, so we start with null.
- `next = cur.next`: **save it first**. Once we change `cur.next`, we would otherwise lose the rest of the list.
- `cur.next = prev`: this one line does the actual reversal.
- `prev = cur; cur = next;`: both pointers slide one step to the right.
- When `cur` becomes null we have processed every node, and `prev` points to the old last node, which is the new head.

**Trace for 1→2→3:** prev=null,cur=1 → 1→null, prev=1,cur=2 → 2→1, prev=2,cur=3 → 3→2, prev=3,cur=null → return 3 (3→2→1).

Complexity: **O(n) time, O(1) extra space.**

## 2. Detect a cycle (Floyd's tortoise and hare)

**Idea:** two runners on a track. The hare runs 2 steps for every 1 step of the tortoise. If the track is a loop, the hare will eventually lap the tortoise and they meet. If the track has an end, the hare simply reaches it.

```java
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;          // tortoise: 1 step
        fast = fast.next.next;     // hare: 2 steps
        if (slow == fast) return true;   // they met, so there is a loop
    }
    return false;                  // hare reached the end, so no loop
}
```

**Line by line**
- `fast != null && fast.next != null`: we check both because the hare jumps two nodes. Skipping the check would throw a `NullPointerException`.
- `slow == fast`: compares **references** (the same node object), not values.
- O(n) time, O(1) space. No HashSet needed.

Q: How could you detect a cycle with extra memory?
A: Put every visited node into a `HashSet`. If you see a node again, there is a cycle. That is O(n) space, while Floyd's uses O(1).

@@ 3 | TOPIC | Stack — Balanced Parentheses | LIFO structure and a classic problem | MUST
# Stack: balanced parentheses

A **stack** is **LIFO: Last In, First Out**. **Analogy:** a stack of plates. You put plates on top (`push`) and take from the top (`pop`).

A **queue** is **FIFO: First In, First Out**, like a line at a bank counter.

In Java, use `ArrayDeque` for both (the old `Stack` class is synchronized and slow).

## Problem: is "{[()]}" balanced?

**Idea:** every closing bracket must match the **most recent** unmatched opening bracket. "Most recent" is exactly what a stack gives you.

```java
boolean isBalanced(String s) {
    Deque<Character> st = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '{' || c == '[') {
            st.push(c);                       // opening: remember it
        } else {
            if (st.isEmpty()) return false;   // closing with nothing open
            char open = st.pop();             // the latest opening bracket
            if ((c == ')' && open != '(') ||
                (c == '}' && open != '{') ||
                (c == ']' && open != '[')) {
                return false;                 // wrong type of bracket
            }
        }
    }
    return st.isEmpty();                      // leftovers mean unclosed brackets
}
```

**Line by line**
- `Deque<Character> st = new ArrayDeque<>()`: our stack. `Deque` means "double-ended queue". Here we use only one end.
- `s.toCharArray()`: lets us loop over each character.
- `push(c)`: put an opening bracket on top.
- `st.isEmpty()` check: for `")("`, the first `)` has no partner.
- `pop()`: removes and returns the top bracket, which must match.
- `return st.isEmpty()`: for `"(("`, the loop ends with brackets still open, so the string is not balanced.

**Trace "{[()]}":** push { → push [ → push ( → `)` pops ( ✓ → `]` pops [ ✓ → `}` pops { ✓ → stack empty → **true**.

Q: Why not use `java.util.Stack`?
A: It extends `Vector`, so every method is synchronized (slow) and it exposes index access. `ArrayDeque` is faster and is the recommended stack in modern Java.

@@ 3 | TOPIC | Hash Tables | How HashMap gives O(1) lookup | MUST
# Hash tables

**Idea:** turn the key into a number (a **hash**), and use that number to pick a **bucket** (an array slot). Then lookup is a single jump, which is O(1).

**Analogy:** a library where the book's title decides its shelf. "Java Basics" → hash → shelf 7. To find it again you compute the shelf number directly instead of searching every shelf.

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("Rahim", 30);              // hash("Rahim") -> bucket -> store
ages.put("Karim", 25);
int a = ages.get("Rahim");          // hash again -> same bucket -> 30
boolean has = ages.containsKey("Jamal");   // false
ages.getOrDefault("Jamal", 0);      // 0 instead of null
```

**Line by line**
- `put`: computes `"Rahim".hashCode()`, maps it to a bucket index, and stores the key-value pair there.
- `get`: computes the same hash, goes to the same bucket, and uses `equals()` to find the exact key.
- `getOrDefault`: avoids a `NullPointerException` when the key is missing and you unbox to `int`.

## Collisions
Two different keys can land in the same bucket. Java keeps a small **linked list** in that bucket. Once the bucket has more than 8 entries (and the table has at least 64 buckets), the list turns into a **red-black tree**.

- Average case: **O(1)**.
- Worst case (all keys collide): O(n) with a list, and **O(log n)** with a tree (Java 8+).

Q: Why is HashMap lookup O(1) on average but slower in the worst case?
A: Normally each bucket holds very few items. If many keys collide into one bucket, lookup must scan that bucket. Java 8 converts big buckets into trees, so the worst case is O(log n).

@@ 3 | TOPIC | Trees — Traversals | Inorder, preorder, postorder, level order | MUST
# Trees and traversals

A **tree** is a structure of nodes in which each node has children, like a family tree or a folder structure.
A **binary tree** gives each node at most 2 children (left, right).
In a **BST (Binary Search Tree)**: left < node < right.

```java
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int v) { val = v; }
}
```

## The four traversals (for a tree with root 2, left 1, right 3)

| Traversal | Order | Result | Used for |
|---|---|---|---|
| Inorder | Left, Root, Right | 1 2 3 | Sorted output of a BST |
| Preorder | Root, Left, Right | 2 1 3 | Copy / serialize a tree |
| Postorder | Left, Right, Root | 1 3 2 | Delete a tree (children first) |
| Level order | Level by level | 2 1 3 | BFS, shortest path |

**Memory trick:** "pre / in / post" tells you **where the Root goes**: before, in the middle, or after.

```java
void inorder(TreeNode n) {
    if (n == null) return;      // base case: empty subtree
    inorder(n.left);            // 1. everything smaller
    System.out.print(n.val + " ");  // 2. this node
    inorder(n.right);           // 3. everything bigger
}

int height(TreeNode n) {
    return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
}
```

**Line by line**
- `if (n == null) return;`: **every recursive function needs a base case**, or it never stops.
- `inorder`: for a BST this prints values in sorted order, because left values are smaller and right values are bigger.
- `height`: an empty tree has height 0. Otherwise it is 1 (this node) plus the taller of the two subtrees.

## Level order (BFS) with a queue

```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new LinkedList<>();
    q.add(root);
    while (!q.isEmpty()) {
        int size = q.size();                 // nodes on the current level
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode n = q.poll();           // take from the front
            level.add(n.val);
            if (n.left != null) q.add(n.left);    // children wait for the next level
            if (n.right != null) q.add(n.right);
        }
        res.add(level);
    }
    return res;
}
```

**Line by line**
- A **queue** (FIFO) processes nodes in the order they were found, which naturally goes level by level.
- `int size = q.size()`: we freeze the count **before** the loop. The children we add during the loop belong to the next level and must not be counted now.
- `poll()`: removes the front node. `add()`: puts children at the back.

**Analogy:** a school assembly. Call the first row, and as each student comes up, their "children" join the back of the line.

Q: Which traversal gives sorted output for a BST?
A: Inorder (Left, Root, Right).

@@ 3 | TOPIC | Graphs — BFS and DFS | Graph traversal and topological sort | MUST
# Graphs: BFS and DFS

A **graph** is a set of nodes (vertices) connected by edges, like cities joined by roads.
We usually store it as an **adjacency list**: `Map<Integer, List<Integer>>` (each city → its neighbouring cities).

## BFS (Breadth-First Search)
**Analogy:** a ripple in a pond. First visit all neighbours 1 step away, then 2 steps away, and so on.

```java
void bfs(Map<Integer, List<Integer>> g, int start) {
    Set<Integer> seen = new HashSet<>(List.of(start));
    Queue<Integer> q = new ArrayDeque<>(List.of(start));
    while (!q.isEmpty()) {
        int u = q.poll();                        // next node to process
        System.out.println(u);
        for (int v : g.getOrDefault(u, List.of())) {
            if (seen.add(v)) q.add(v);           // add only if never seen
        }
    }
}
```

**Line by line**
- `seen`: stops us visiting a node twice. Without it, a cycle (A→B→A) would loop forever.
- `seen.add(v)` returns **true only if v was new**. That one call does "check and mark" together, which is a neat trick.
- `getOrDefault(u, List.of())`: a node with no edges gives an empty list instead of null.

## DFS (Depth-First Search)
**Analogy:** exploring a maze. Follow one path as deep as possible, and turn back only at a dead end.

```java
void dfs(Map<Integer, List<Integer>> g, int u, Set<Integer> seen) {
    if (!seen.add(u)) return;                    // already visited
    System.out.println(u);
    for (int v : g.getOrDefault(u, List.of())) dfs(g, v, seen);
}
```

**Line by line**
- Recursion uses the **call stack** as its stack, so DFS goes deep before wide.
- `if (!seen.add(u)) return;`: visit each node only once.

| | BFS | DFS |
|---|---|---|
| Uses | Queue | Stack / recursion |
| Good for | Shortest path (unweighted), levels | Cycle detection, topological sort, connected components |

**Topological sort:** orders tasks so that each task comes after the tasks it depends on (e.g. "install Java" before "run Maven"). Kahn's algorithm: repeatedly take the nodes with **in-degree 0** (no remaining dependencies) from a queue.

Q: Which traversal finds the shortest path in an unweighted graph?
A: BFS, because it explores nodes in order of their distance from the start.

@@ 2 | MODULE | Algorithms | Sorting, searching and problem-solving patterns | MUST
Algorithms are step-by-step recipes. In the written test you will mostly need **binary search, sorting, two pointers, sliding window, hashing and simple DP**.

@@ 3 | TOPIC | Sorting Complexity Cheat Sheet | Best, average and worst case of common sorts | MUST
# Sorting complexity cheat sheet

| Algorithm | Best | Average | Worst | Space | Stable |
|---|---|---|---|---|---|
| Bubble sort | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Insertion sort | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Selection sort | O(n²) | O(n²) | O(n²) | O(1) | No |
| Merge sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quick sort | O(n log n) | O(n log n) | O(n²) | O(log n) | No |
| Heap sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No |
| Binary search | O(1) | O(log n) | O(log n) | O(1) | - |

**Stable** means equal items keep their original order. Example: sort students by grade, and students with the same grade stay in alphabetical order if they were alphabetical before.

**Simple analogies**
- **Bubble sort:** the biggest bubbles float to the top one by one.
- **Insertion sort:** how you sort playing cards in your hand. Pick the next card and slide it into the right spot.
- **Selection sort:** repeatedly pick the smallest item and put it at the front.
- **Merge sort:** split the pile in half, sort each half, then merge the two sorted halves.
- **Quick sort:** pick a pivot, put smaller items on the left and bigger on the right, then repeat on each side.

```java
// Bubble sort: easy to write by hand
void bubbleSort(int[] a) {
    for (int i = 0; i < a.length - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < a.length - 1 - i; j++) {
            if (a[j] > a[j + 1]) {
                int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;   // swap
                swapped = true;
            }
        }
        if (!swapped) break;      // already sorted, so stop early (best case O(n))
    }
}
```

**Line by line**
- The outer loop `i` counts passes. After each pass the largest remaining item is at the end.
- `a.length - 1 - i`: the last `i` items are already in place, so we skip them.
- The swap uses a temporary variable `t`, otherwise one value would be overwritten.
- `swapped` flag: if a whole pass makes no swap, the array is sorted. That is why the best case is O(n).

**Java tip:** `Arrays.sort(int[])` uses dual-pivot Quicksort (primitives). `Arrays.sort(Object[])` and `Collections.sort` use **TimSort** (stable, a merge-sort hybrid).

Q: Which sort would you choose for guaranteed O(n log n) with stability?
A: Merge sort (TimSort in Java).

@@ 3 | TOPIC | Binary Search | Find an item in a sorted array in O(log n) | MUST
# Binary search

**Idea:** in a **sorted** array, look at the middle. If the target is bigger, throw away the left half. If it is smaller, throw away the right half. Repeat.

**Analogy:** the "guess my number between 1 and 100" game. Guess 50, they say "higher", guess 75... At most 7 guesses.

```java
int binarySearch(int[] a, int target) {
    int lo = 0, hi = a.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;      // avoids int overflow
        if (a[mid] == target) return mid;  // found it
        if (a[mid] < target) lo = mid + 1; // target is on the right
        else hi = mid - 1;                 // target is on the left
    }
    return -1;                             // not found
}
```

**Line by line**
- `lo` and `hi` mark the part of the array that could still contain the target.
- `lo <= hi`: use `<=`, not `<`. When `lo == hi` there is still one element left to check.
- `mid = lo + (hi - lo) / 2`: the same as `(lo + hi) / 2`, but **safe**. If lo and hi are both near `Integer.MAX_VALUE`, `lo + hi` would overflow into a negative number.
- `lo = mid + 1` / `hi = mid - 1`: we already checked `mid`, so we exclude it. Writing `lo = mid` can cause an infinite loop.
- `return -1`: the usual signal for "not found".

**Trace:** a = [1,3,5,7,9], target 7 → lo=0,hi=4,mid=2 (5<7) → lo=3,mid=3 (7) → return 3.

Q: What is the precondition for binary search?
A: The array must be sorted.

@@ 3 | TOPIC | Merge Sort | Divide and conquer sorting | SHOULD
# Merge sort

**Idea (divide and conquer):** split the array in half, sort each half (recursively), then **merge** the two sorted halves.

**Analogy:** two sorted piles of exam papers. To combine them, keep looking at the top paper of each pile and take the smaller one.

```java
void mergeSort(int[] a, int l, int r) {
    if (l >= r) return;                 // 0 or 1 element is already sorted
    int m = (l + r) / 2;
    mergeSort(a, l, m);                 // sort left half
    mergeSort(a, m + 1, r);             // sort right half

    int[] tmp = new int[r - l + 1];     // space for the merged result
    int i = l, j = m + 1, k = 0;
    while (i <= m && j <= r)            // both halves have items
        tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
    while (i <= m) tmp[k++] = a[i++];   // leftovers from the left
    while (j <= r) tmp[k++] = a[j++];   // leftovers from the right
    System.arraycopy(tmp, 0, a, l, tmp.length);   // copy back
}
// call: mergeSort(arr, 0, arr.length - 1);
```

**Line by line**
- `if (l >= r) return;`: the base case. A single element cannot be out of order.
- `i` walks the left half, `j` walks the right half, and `k` fills `tmp`.
- `a[i] <= a[j]`: using `<=` (not `<`) takes from the left first when values are equal. **That is what makes merge sort stable.**
- `a[i++]`: uses the value, **then** moves `i` forward.
- The two leftover loops: one half may run out first, so we copy whatever remains of the other.
- `System.arraycopy(src, srcPos, dest, destPos, length)`: copies the merged part back into the original array.

Complexity: **O(n log n)** always (log n levels of splitting, O(n) merging per level). Space **O(n)** for `tmp`.

@@ 3 | TOPIC | Two Pointers and Sliding Window | Patterns that turn O(n²) into O(n) | MUST
# Two pointers and sliding window

These patterns replace a slow "check every pair" O(n²) solution with a single pass, O(n).

## Sliding window: longest substring without repeating characters

**Idea:** keep a "window" `[left, right]` that holds no duplicates. Move `right` forward. When a repeated character appears, jump `left` past its previous position.

**Analogy:** a train window moving along a row of houses. You always look at a stretch with no repeated house colour.

```java
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> last = new HashMap<>(); // char -> last index seen
    int best = 0, left = 0;
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (last.containsKey(c) && last.get(c) >= left) {
            left = last.get(c) + 1;      // shrink: start after the old copy
        }
        last.put(c, right);              // remember the latest position
        best = Math.max(best, right - left + 1);
    }
    return best;
}
```

**Line by line**
- `last`: remembers where we last saw each character.
- `last.get(c) >= left`: the old copy only matters if it is **inside** the current window. If it is before `left`, it is already outside.
- `left = last.get(c) + 1`: move the window start just past the duplicate.
- `right - left + 1`: the window length (+1 because both ends are included).
- Example "abcabcbb" → 3 ("abc").

## Two Sum with a HashMap: O(n)

```java
int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> idx = new HashMap<>();   // value -> index
    for (int i = 0; i < nums.length; i++) {
        Integer j = idx.get(target - nums[i]);     // the partner we need
        if (j != null) return new int[]{j, i};
        idx.put(nums[i], i);
    }
    return new int[0];
}
```

**Line by line**
- For each number, ask: "Have I already seen the number that completes the target?" That partner is `target - nums[i]`.
- `Integer j` (not `int`): `get` returns `null` when the key is missing, and an `int` cannot be null.
- We `put` **after** checking so that a number is not paired with itself.

## Two pointers on a sorted array

```java
boolean pairWithSum(int[] sorted, int target) {
    int i = 0, j = sorted.length - 1;
    while (i < j) {
        int s = sorted[i] + sorted[j];
        if (s == target) return true;
        if (s < target) i++; else j--;   // need bigger -> move left pointer right
    }
    return false;
}
```
- Start at both ends. If the sum is too small, the only way to grow it is to move `i` right. If it is too big, move `j` left.

@@ 3 | TOPIC | Dynamic Programming | Solve overlapping subproblems once | SHOULD
# Dynamic programming (DP)

**Idea:** if a problem breaks into smaller problems that **repeat**, solve each small problem once and **save the answer**.

**Analogy:** someone asks you 1+1+1+1+1 = ? You count: 5. They add "+1" and you say 6 straight away. You didn't recount; you **remembered** the 5. That is DP.

DP needs two things:
- **Overlapping subproblems:** the same small problem is solved many times.
- **Optimal substructure:** the best answer is built from the best answers of the subproblems.

Two styles:
- **Memoization (top-down):** recursion + a cache.
- **Tabulation (bottom-up):** fill a table with a loop, from small to big.

## Fibonacci, both ways

```java
// Top-down: memoization
long fib(int n, long[] memo) {
    if (n <= 1) return n;
    if (memo[n] != 0) return memo[n];          // already solved
    return memo[n] = fib(n - 1, memo) + fib(n - 2, memo);
}

// Bottom-up: tabulation with O(1) space
long fib(int n) {
    if (n <= 1) return n;
    long a = 0, b = 1;
    for (int i = 2; i <= n; i++) { long c = a + b; a = b; b = c; }
    return b;
}
```
- Plain recursion recomputes fib(3) many times, which is O(2ⁿ). With memo, each value is computed once: O(n).

## Coin change: the fewest coins to make an amount

```java
int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];        // dp[a] = min coins for amount a
    Arrays.fill(dp, amount + 1);           // "infinity" (impossible) marker
    dp[0] = 0;                             // 0 coins make amount 0
    for (int a = 1; a <= amount; a++)
        for (int c : coins)
            if (c <= a)
                dp[a] = Math.min(dp[a], dp[a - c] + 1);
    return dp[amount] > amount ? -1 : dp[amount];
}
```

**Line by line**
- `dp[a]`: the answer for the smaller amount `a`. We build up from 0 to `amount`.
- `amount + 1` acts as infinity, because you can never need more than `amount` coins (using coins of 1).
- `dp[a - c] + 1`: "use one coin `c`, then solve the rest (a − c), which we already know".
- At the end, if the value is still "infinity", the amount is impossible, so return -1.
- Example: coins [1,2,5], amount 11 → 3 (5+5+1).

@@ 3 | TOPIC | Common Handwritten Problems | Palindrome, prime, factorial, anagram, FizzBuzz | MUST
# Common handwritten problems

These are the most common "write a function" questions. Practise each one **on paper**.

```java
boolean isPalindrome(String s) {
    int i = 0, j = s.length() - 1;
    while (i < j)
        if (s.charAt(i++) != s.charAt(j--)) return false;
    return true;
}
```
- Two pointers move from both ends toward the middle. "madam" → true. `i++` / `j--` move the pointers after the comparison.

```java
boolean isPrime(int n) {
    if (n < 2) return false;
    for (int i = 2; (long) i * i <= n; i++)
        if (n % i == 0) return false;
    return true;
}
```
- Only check up to √n: if n = a × b, one of a or b must be ≤ √n.
- `(long) i * i`: prevents overflow when i is large.

```java
long factorial(int n) {
    return n <= 1 ? 1 : n * factorial(n - 1);
}
```
- Recursion: 5! = 5 × 4!. The base case `n <= 1` returns 1. Use `long` because 13! already overflows `int`.

```java
boolean isAnagram(String a, String b) {
    if (a.length() != b.length()) return false;
    int[] cnt = new int[26];                  // one counter per letter a-z
    for (int i = 0; i < a.length(); i++) {
        cnt[a.charAt(i) - 'a']++;             // count up for a
        cnt[b.charAt(i) - 'a']--;             // count down for b
    }
    for (int c : cnt) if (c != 0) return false;
    return true;
}
```
- `charAt(i) - 'a'`: turns 'a'..'z' into 0..25 (a char is really a number).
- If both words use exactly the same letters, every counter ends at 0. "listen"/"silent" → true.

```java
Map<Character, Long> charFrequency(String s) {
    return s.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting()));
}
```
- `s.chars()` gives an `IntStream` of character codes, and `(char) c` turns each back into a Character.
- `groupingBy(key, mapFactory, counting())`: groups equal characters and counts them. `LinkedHashMap` keeps first-seen order.

```java
void fizzBuzz(int n) {
    for (int i = 1; i <= n; i++)
        System.out.println(i % 15 == 0 ? "FizzBuzz"
                         : i % 3 == 0 ? "Fizz"
                         : i % 5 == 0 ? "Buzz"
                         : String.valueOf(i));
}
```
- Check **15 first**: a multiple of 15 is also a multiple of 3, so testing 3 first would print "Fizz" by mistake.

```java
String reverse(String s) {
    return new StringBuilder(s).reverse().toString();
}
int secondLargest(int[] a) {
    int first = Integer.MIN_VALUE, second = Integer.MIN_VALUE;
    for (int x : a) {
        if (x > first) { second = first; first = x; }
        else if (x > second && x != first) second = x;
    }
    return second;
}
```
- `secondLargest`: one pass. When a new maximum appears, the old maximum becomes second. `x != first` skips duplicates of the maximum.

@@ 2 | MODULE | OOP and Software Design | Object-oriented principles and patterns | MUST
**Object-Oriented Programming (OOP)** models software as **objects**, each holding data (fields) and behaviour (methods), like real-world things.
A **class** is the blueprint (a house plan), and an **object** is the actual house built from it.

@@ 3 | TOPIC | Four Pillars of OOP | Encapsulation, abstraction, inheritance, polymorphism | MUST
# The four pillars of OOP

| Pillar | Simple meaning | Analogy |
|---|---|---|
| **Encapsulation** | Hide data, expose safe methods | An ATM: you can't touch the cash, only use the buttons |
| **Abstraction** | Show *what*, hide *how* | A car's steering wheel: you don't need to know the engine |
| **Inheritance** | A child class reuses a parent class (IS-A) | A dog IS-AN animal |
| **Polymorphism** | One interface, many forms | "Speak" makes a dog bark and a cat meow |

```java
// Encapsulation: balance is private and can only change through rules
public class Account {
    private BigDecimal balance = BigDecimal.ZERO;

    public void deposit(BigDecimal amount) {
        if (amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance = balance.add(amount);
    }
    public BigDecimal getBalance() { return balance; }
}

// Abstraction + Inheritance + Polymorphism
abstract class Animal {
    abstract String sound();                 // WHAT (no HOW here)
    void speak() { System.out.println(sound()); }
}
class Dog extends Animal { String sound() { return "Woof"; } }
class Cat extends Animal { String sound() { return "Meow"; } }

Animal a = new Dog();
a.speak();       // "Woof": decided at runtime (dynamic dispatch)
```

**Line by line**
- `private BigDecimal balance`: nobody outside can write `account.balance = -500`. That is encapsulation.
- `deposit` validates first: the class **protects its own rules** (invariants).
- `abstract String sound()`: every animal must make a sound, but the parent doesn't say which. That is abstraction.
- `Dog extends Animal`: Dog inherits `speak()` for free. That is inheritance.
- `Animal a = new Dog()`: the variable type is Animal, but the object is a Dog. Java calls `Dog.sound()` at runtime. That is **runtime polymorphism (overriding)**.

## Two kinds of polymorphism
- **Compile-time (overloading):** same method name, different parameters. `print(int)`, `print(String)`.
- **Runtime (overriding):** a subclass redefines a parent method. Chosen at runtime by the actual object type.

Q: Overloading vs overriding?
A: Overloading means the same name with different parameters in the same class, resolved at compile time. Overriding means a subclass gives a new body to an inherited method with the same signature, resolved at runtime.

@@ 3 | TOPIC | SOLID Principles | Five rules for maintainable classes | MUST
# SOLID principles

| Letter | Principle | Simple meaning |
|---|---|---|
| **S** | Single Responsibility | One class, one job, one reason to change |
| **O** | Open/Closed | Add new behaviour by **adding** code, not editing old code |
| **L** | Liskov Substitution | A child must work wherever the parent is expected |
| **I** | Interface Segregation | Many small interfaces beat one fat interface |
| **D** | Dependency Inversion | Depend on interfaces, not concrete classes |

**Analogies**
- **S:** a chef cooks and a cashier takes money. If the chef also does accounts, a tax rule change would affect the kitchen.
- **O:** a power strip. To add a new device you plug it in; you don't rewire the strip.
- **L:** if a "Square extends Rectangle" breaks `setWidth()` (because the height changes too), it is not a true substitute.
- **I:** a printer interface shouldn't force a simple printer to implement `fax()` and `scan()`.
- **D:** a lamp depends on "a socket" (an interface), not on one specific power plant.

## Open/Closed + Strategy pattern: interest calculation

```java
public interface InterestCalculator {
    BigDecimal calculate(BigDecimal principal, int months);
}

@Component("flat")
class FlatInterest implements InterestCalculator {
    public BigDecimal calculate(BigDecimal p, int m) {
        return p.multiply(new BigDecimal("0.10"))          // 10% per year
                .multiply(BigDecimal.valueOf(m))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }
}

@Component("declining")
class DecliningInterest implements InterestCalculator {
    public BigDecimal calculate(BigDecimal p, int m) { /* different formula */ return BigDecimal.ZERO; }
}

@Service
class LoanService {
    private final Map<String, InterestCalculator> calculators;

    LoanService(Map<String, InterestCalculator> calculators) {   // Spring injects ALL implementations
        this.calculators = calculators;
    }
    BigDecimal interest(String method, BigDecimal p, int m) {
        return calculators.get(method).calculate(p, m);
    }
}
```

**Line by line**
- `InterestCalculator`: the **abstraction** that LoanService depends on (D).
- `@Component("flat")`: registers this class as a Spring bean named "flat".
- `new BigDecimal("0.10")`: **use the String constructor**. `new BigDecimal(0.1)` carries the binary float error.
- `divide(..., 2, RoundingMode.HALF_UP)`: money needs 2 decimals and an explicit rounding rule. Without a scale, `divide` can throw `ArithmeticException` for non-terminating decimals.
- `Map<String, InterestCalculator>`: Spring fills the map with **bean name → bean**.
- To add a "compound" method, you **add a new class**. LoanService never changes. That is Open/Closed (O).

Q: Give a real Spring example of Dependency Inversion.
A: Constructor injection of an interface. The service depends on `PaymentGateway`, and Spring decides which implementation to plug in.

@@ 3 | TOPIC | Design Patterns You Must Know | Singleton, Factory, Builder, Proxy, Strategy, Observer and more | MUST
# Design patterns

A **design pattern** is a proven solution to a common design problem, like a standard recipe every chef knows.

| Pattern | Type | Purpose | Java / Spring example |
|---|---|---|---|
| Singleton | Creational | Exactly one instance | Spring beans (default scope) |
| Factory | Creational | Hide object creation | `BeanFactory`, `Calendar.getInstance()` |
| Builder | Creational | Build complex objects step by step | Lombok `@Builder`, `StringBuilder` |
| Adapter | Structural | Convert one interface to another | `HandlerAdapter` in Spring MVC |
| Decorator | Structural | Add behaviour by wrapping | `BufferedInputStream` |
| Proxy | Structural | Control access / add behaviour | Spring AOP, `@Transactional` |
| Facade | Structural | Simple front for a complex system | The service layer |
| Strategy | Behavioral | Swap algorithms at runtime | `Comparator`, `InterestCalculator` |
| Observer | Behavioral | Notify subscribers | `ApplicationEventPublisher`, `@EventListener` |
| Template Method | Behavioral | Fixed skeleton, variable steps | `JdbcTemplate` |
| Chain of Responsibility | Behavioral | Pass a request along handlers | Servlet filters, the Spring Security filter chain |

## Singleton (thread-safe, lazy: the holder idiom)

```java
public class Config {
    private Config() {}                                  // nobody else can call new
    private static class Holder {
        static final Config INSTANCE = new Config();     // created on first use
    }
    public static Config getInstance() { return Holder.INSTANCE; }
}
```

**Line by line**
- `private Config()`: a private constructor blocks `new Config()` from outside.
- `Holder` is a nested class. The JVM loads it **only when `getInstance()` is first called**, so creation is lazy.
- Class loading in Java is thread-safe, so we get one instance with no `synchronized` needed.
- **Analogy:** a country has one president. Everyone asks "who is the president?" and gets the same person.

## Builder

```java
public class Loan {
    private final String memberId;
    private final BigDecimal amount;
    private final int months;

    private Loan(Builder b) { memberId = b.memberId; amount = b.amount; months = b.months; }

    public static class Builder {
        private String memberId; private BigDecimal amount; private int months = 12;
        public Builder memberId(String v) { memberId = v; return this; }
        public Builder amount(BigDecimal v) { amount = v; return this; }
        public Builder months(int v) { months = v; return this; }
        public Loan build() { return new Loan(this); }
    }
}
Loan loan = new Loan.Builder().memberId("M1").amount(new BigDecimal("50000")).build();
```
- Each setter `return this`, so calls can be **chained**.
- It avoids a constructor with 10 parameters where you mix up the order. Fields can stay `final` (immutable).
- `months = 12`: a default value when the caller doesn't set it.
- **Analogy:** ordering a burger: "bun, then patty, then cheese, then build".

## Observer with Spring events

```java
public record LoanApproved(Long loanId) {}

@Service
class LoanService {
    private final ApplicationEventPublisher events;
    LoanService(ApplicationEventPublisher events) { this.events = events; }
    void approve(Long id) { /* ... */ events.publishEvent(new LoanApproved(id)); }
}

@Component
class SmsNotifier {
    @EventListener
    void on(LoanApproved e) { System.out.println("Send SMS for loan " + e.loanId()); }
}
```
- LoanService doesn't know who is listening. New listeners (email, audit) can be added without touching it.
- **Analogy:** a YouTube channel. The creator uploads once, and all subscribers are notified.

@@ 3 | TOPIC | SDLC and Agile | Waterfall, Scrum, user stories | SHOULD
# SDLC and Agile

**SDLC (Software Development Life Cycle):** requirements → design → implementation → testing → deployment → maintenance.

- **Waterfall:** do each phase once, in order. Good when requirements are fixed (like building a bridge).
- **Agile:** build in short cycles and adjust often (like cooking while tasting).

## Scrum (the most popular Agile framework)
- **Sprint:** a fixed time box of 1–4 weeks that ends with a working increment.
- **Roles:** Product Owner (decides *what*), Scrum Master (removes blockers, guards the process), Development Team (builds it).
- **Ceremonies:** sprint planning, daily stand-up (15 min: yesterday / today / blockers), sprint review (demo), retrospective (improve the process).
- **Artifacts:** product backlog (all wanted work), sprint backlog (work for this sprint), increment (the working product).

## User story format
> As a **PO field officer**, I want to **record a repayment** so that **the outstanding balance is up to date**.

Add **acceptance criteria**: "Given a loan with 5,000 outstanding, when I record 500, then outstanding becomes 4,500 and a receipt number is shown."

Q: Difference between Scrum Master and Project Manager?
A: A Scrum Master is a facilitator and coach who removes blockers. They don't assign tasks. The team self-organizes.

@@ 2 | TOPIC | Operating Systems | Processes, threads, deadlock, memory, scheduling | MUST
# Operating systems essentials

## Process vs thread
- **Process:** a running program with its **own memory**. **Analogy:** a separate house.
- **Thread:** a worker inside a process. Threads **share the heap** (the house) but each has its **own stack** (its own notebook).
- Threads are cheaper to create and switch, but shared memory means they can interfere with each other.

## Race condition
Two threads update shared data at the same time, and the result depends on timing.
**Analogy:** two people withdraw from the same account at the same second. Both see 1,000, both withdraw 800, and the bank loses money.
Fix: synchronization (locks) or atomic operations.

## Deadlock
Two threads each hold a lock and wait for the other's lock, so both wait forever.
**Analogy:** two cars on a narrow bridge from opposite sides, neither backing up.

All **four** conditions are needed (Coffman conditions):
1. **Mutual exclusion:** a resource can be held by only one thread.
2. **Hold and wait:** a thread holds one lock while waiting for another.
3. **No preemption:** locks can't be taken away by force.
4. **Circular wait:** A waits for B and B waits for A.

Break any one to prevent deadlock. The usual fix: **always acquire locks in the same order**.

```java
// Deadlock-prone: thread 1 locks a then b, thread 2 locks b then a
void transfer(Account from, Account to, BigDecimal amt) {
    Account first  = from.getId() < to.getId() ? from : to;   // fixed global order
    Account second = from.getId() < to.getId() ? to : from;
    synchronized (first) {
        synchronized (second) {
            from.withdraw(amt);
            to.deposit(amt);
        }
    }
}
```
- Whatever the direction of the transfer, both threads lock the **smaller id first**, so a circular wait can't happen.

## Mutex vs semaphore
- **Mutex:** one key for one room. Only one thread at a time.
- **Semaphore:** N keys. Up to N threads at a time (e.g. a pool of 10 DB connections).

## Memory
- **Virtual memory:** each process thinks it has a large private memory. The OS maps it to real RAM in fixed-size **pages**.
- **Page fault:** the page is not in RAM, so the OS loads it from disk.
- **Thrashing:** too many page faults, so the system spends its time swapping instead of working.

## CPU scheduling
- **FCFS:** first come, first served (simple, but short jobs can get stuck behind a long one).
- **SJF:** shortest job first (the best average wait, but needs to know job lengths).
- **Round Robin:** each process gets a time slice (quantum) in turn. Fair, and used in time-sharing.
- **Priority:** higher priority first. Low-priority jobs can **starve**. The fix is **aging** (raise priority over time).

@@ 2 | TOPIC | Computer Networks | OSI, TCP/UDP, HTTP methods, status codes, DNS, CORS | MUST
# Computer networks

## OSI model (7 layers)
Memory trick (top → bottom): **"All People Seem To Need Data Processing"**.

| Layer | Example | Analogy (sending a letter) |
|---|---|---|
| 7 Application | HTTP, DNS, SMTP, FTP | What you write in the letter |
| 6 Presentation | TLS/SSL, encoding | Translating / sealing the envelope |
| 5 Session | Sessions, RPC | Keeping the conversation going |
| 4 Transport | TCP, UDP | Registered post vs a normal postcard |
| 3 Network | IP, ICMP, routers | The address and the route between cities |
| 2 Data Link | Ethernet, MAC, switches | The local postman on your street |
| 1 Physical | Cables, signals | The road itself |

## TCP vs UDP
| TCP | UDP |
|---|---|
| Connection first (3-way handshake: SYN, SYN-ACK, ACK) | No connection |
| Reliable, ordered, retransmits lost packets | May lose or reorder packets |
| Slower | Faster, low latency |
| HTTP, database connections, email | DNS, video calls, games |

**Analogy:** TCP is a phone call ("Hello?" "Hello!" "OK, let's talk"). UDP is shouting announcements through a loudspeaker.

## HTTP methods
- **Safe** (doesn't change data): GET, HEAD, OPTIONS.
- **Idempotent** (repeating it gives the same result): GET, PUT, DELETE, HEAD, OPTIONS.
- **POST** is neither: posting twice creates two records.
- **PATCH** is a partial update and is not guaranteed to be idempotent.

## Status codes
| Code | Meaning |
|---|---|
| 200 OK / 201 Created / 204 No Content | Success |
| 301 / 302 / 304 Not Modified | Redirect / caching |
| 400 Bad Request | Invalid input |
| 401 Unauthorized | **Not logged in** (who are you?) |
| 403 Forbidden | **Logged in but not allowed** (I know you, but no) |
| 404 Not Found / 409 Conflict | Missing / state conflict (e.g. version mismatch) |
| 422 Unprocessable Entity | Valid format, but breaks a business rule |
| 429 Too Many Requests | Rate limited |
| 500 / 502 / 503 / 504 | Server error / bad gateway / unavailable / gateway timeout |

**Analogy for 401 vs 403:** at an office gate, 401 = "Show me your ID card". 403 = "I see your ID, but you can't enter the server room."

## What happens when you type a URL?
1. **DNS lookup:** turn `pksf.org.bd` into an IP address (the phone book of the internet).
2. **TCP connect** (3-way handshake).
3. **TLS handshake** (for HTTPS): the server sends its certificate, the browser verifies it, and both agree a symmetric session key.
4. **HTTP request** is sent, the server processes it and sends a **response**.
5. The browser parses HTML, fetches CSS/JS/images, builds the DOM and **renders** the page.

## CORS
A browser safety rule. A page from origin A (`app.com`) calling an API on origin B (`api.com`) is blocked unless B replies with `Access-Control-Allow-Origin: https://app.com`. Non-simple requests (e.g. with an `Authorization` header or a PUT) first send an **OPTIONS preflight**.
**Analogy:** a guard asking the building you're visiting, "Is this visitor expected?"

## Cookie flags
- **HttpOnly:** JavaScript can't read it (protects against XSS stealing it).
- **Secure:** only sent over HTTPS.
- **SameSite** (Strict/Lax/None): limits cross-site sending (protects against CSRF).

@@ 2 | MODULE | DBMS Theory | Keys, normalization, ACID, isolation | MUST
Database theory comes up in every written test: keys, normal forms, ACID and isolation levels.

@@ 3 | TOPIC | Keys and Relationships | Primary, foreign, candidate, composite keys | MUST
# Keys and relationships

| Key | Meaning | Example |
|---|---|---|
| **Primary key (PK)** | Uniquely identifies a row, cannot be NULL | `member.id` |
| **Candidate key** | Any column(s) that *could* be the PK | `id`, `national_id` |
| **Alternate key** | A candidate key not chosen as PK | `national_id` (UNIQUE) |
| **Foreign key (FK)** | Points to a PK in another table | `loan.member_id → member.id` |
| **Composite key** | A PK made of 2+ columns | `(loan_id, installment_no)` |
| **Surrogate key** | An artificial id with no meaning | `BIGSERIAL id` |
| **Natural key** | A real-world identifier | National ID |

**Analogy:** your student ID is a primary key. Your national ID is also unique (a candidate key). The library card that refers to your student ID is a foreign key.

```sql
CREATE TABLE member (
    id          BIGSERIAL PRIMARY KEY,
    national_id VARCHAR(20) UNIQUE NOT NULL,
    name        VARCHAR(100) NOT NULL
);
CREATE TABLE loan (
    id        BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES member(id),
    amount    NUMERIC(14,2) NOT NULL CHECK (amount > 0)
);
```

**Line by line**
- `BIGSERIAL PRIMARY KEY`: an auto-increasing 64-bit id that must be unique and not null.
- `UNIQUE NOT NULL` on `national_id`: an alternate key. No two members can share it.
- `REFERENCES member(id)`: the foreign key. You can't create a loan for a member who doesn't exist (**referential integrity**).
- `CHECK (amount > 0)`: the database itself refuses bad data, even if the application has a bug.

**Relationships**
- One-to-one: member ↔ member_profile.
- One-to-many: member → loans (FK on the "many" side).
- Many-to-many: students ↔ courses. Needs a **join table** `enrollment(student_id, course_id)`.

@@ 3 | TOPIC | Normalization | 1NF, 2NF, 3NF, BCNF with examples | MUST
# Normalization

**Goal:** remove duplicated data so that each fact is stored **in one place only**. Otherwise you update it in one place, forget another, and the data contradicts itself (an **update anomaly**).

**Analogy:** keep your phone number in one contact card, not written on 20 sticky notes.

| Form | Rule | Simple test |
|---|---|---|
| **1NF** | Atomic values, no repeating groups | No "phone1, phone2, phone3" columns; no comma lists in a cell |
| **2NF** | 1NF + no **partial** dependency | Every non-key column depends on the **whole** composite key |
| **3NF** | 2NF + no **transitive** dependency | Non-key columns depend **only on the key**, not on other non-key columns |
| **BCNF** | Every determinant is a candidate key | A stricter 3NF |

**Memory line:** "The key, the whole key, and nothing but the key."

## Example

Bad table `loan(loan_id, po_id, po_name, amount)`:
- `po_name` depends on `po_id`, not on `loan_id`. That is a transitive dependency (loan_id → po_id → po_name), so it **violates 3NF**.
- If a PO changes its name, you must update thousands of loan rows.

Fix:
```sql
CREATE TABLE po   (id BIGINT PRIMARY KEY, name VARCHAR(150) NOT NULL);
CREATE TABLE loan (id BIGINT PRIMARY KEY, po_id BIGINT REFERENCES po(id), amount NUMERIC(14,2));
```
- Now the PO name lives in **one row**. Loans just point to it by `po_id`.

**2NF example:** `installment(loan_id, installment_no, member_name, amount)` with PK (loan_id, installment_no). `member_name` depends only on `loan_id` (part of the key), which is a partial dependency. Move it out.

**Denormalization:** sometimes we deliberately duplicate data for fast read-heavy reports (e.g. a summary table). Do it on purpose and keep it in sync.

@@ 3 | TOPIC | ACID and Isolation Levels | Transactions, anomalies, CAP and BASE | MUST
# ACID and isolation levels

A **transaction** is a group of operations that succeed or fail **together**.

**Analogy:** a bank transfer: take 500 from A **and** add 500 to B. If the power fails in the middle, neither must happen.

| Letter | Meaning | Simple words |
|---|---|---|
| **A**tomicity | All or nothing | Either the whole transfer happens, or none of it |
| **C**onsistency | Rules always hold | Constraints (balance ≥ 0, FKs) are never broken |
| **I**solation | Transactions don't disturb each other | Others don't see your half-done work |
| **D**urability | Committed means saved | Survives a crash (the write-ahead log, WAL) |

```sql
BEGIN;
UPDATE account SET balance = balance - 500 WHERE id = 1;
UPDATE account SET balance = balance + 500 WHERE id = 2;
COMMIT;      -- or ROLLBACK; to undo both
```
- Between `BEGIN` and `COMMIT`, the changes are invisible to others (isolation) and can be undone (atomicity).

## Read anomalies
- **Dirty read:** you read data another transaction has not committed yet (and it may roll back).
- **Non-repeatable read:** you read the same row twice and get different values (someone updated and committed in between).
- **Phantom read:** you run the same query twice and **new rows** appear.

| Isolation level | Dirty read | Non-repeatable | Phantom |
|---|---|---|---|
| Read Uncommitted | Possible | Possible | Possible |
| Read Committed (**Postgres default**) | No | Possible | Possible |
| Repeatable Read | No | No | Possible (Postgres prevents it) |
| Serializable | No | No | No |

Higher isolation is safer but slower (more waiting or more retries).

## CAP theorem (distributed systems)
When the network is split (**P**artition), a system must choose between:
- **C**onsistency: every read sees the latest write (it may refuse to answer).
- **A**vailability: always answer (the answer may be stale).

**BASE** (the opposite of ACID for NoSQL): **B**asically **A**vailable, **S**oft state, **E**ventually consistent.

Q: Which isolation level does PostgreSQL use by default?
A: Read Committed.

@@ 2 | TOPIC | Phase 1 — Interview Questions | Quick Q&A revision for CS fundamentals | MUST
# Phase 1 interview questions

**Q. Difference between an array and a linked list?**
A. An array uses contiguous memory, with O(1) index access but costly inserts in the middle. A linked list has scattered nodes, O(n) access, O(1) insert/delete when you have the node, and extra memory for pointers.

**Q. Why is HashMap O(1) on average but slower in the worst case?**
A. The hash picks a bucket directly. If many keys collide into one bucket, lookup scans it. Java 8+ turns big buckets (more than 8 entries) into red-black trees, so the worst case is O(log n).

**Q. 401 vs 403?**
A. 401 means not authenticated (missing or invalid credentials). 403 means authenticated but not allowed.

**Q. What is a deadlock, and how do you prevent it in Java?**
A. Threads each wait for a lock another thread holds, forever. Prevent it by acquiring locks in a fixed global order, using `tryLock` with a timeout, or keeping lock scopes small.

**Q. Process vs thread?**
A. A process has its own memory space. Threads live inside a process and share its heap, but each has its own stack.

**Q. TCP vs UDP?**
A. TCP is connection-oriented, reliable and ordered (HTTP, databases). UDP is connectionless and fast but unreliable (DNS, video).

**Q. Explain 3NF with an example.**
A. Non-key columns must depend only on the key. Storing `po_name` in `loan` breaks 3NF because it depends on `po_id`. Move it to a `po` table.

**Q. What does ACID mean?**
A. Atomicity (all or nothing), Consistency (rules hold), Isolation (no interference), Durability (committed data survives a crash).

**Q. Stack vs queue?**
A. A stack is LIFO (undo, recursion, bracket matching). A queue is FIFO (BFS, task scheduling).

**Q. What is the time complexity of merge sort, and is it stable?**
A. O(n log n) in all cases, O(n) extra space, and it is stable.

**Q. Composition vs inheritance: which do you prefer?**
A. Usually composition ("has-a"). It is more flexible, avoids fragile base-class problems and allows behaviour to change at runtime. Use inheritance only for a true "is-a" relationship.

**Q. What is idempotency?**
A. Doing an operation once or many times has the same effect. PUT and DELETE are idempotent, POST is not. For payments we use an idempotency key so that a retried request doesn't charge twice.
