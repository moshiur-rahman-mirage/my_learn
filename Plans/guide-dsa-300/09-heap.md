@@ 1 | MODULE | Phase 9 — Heap / Priority Queue | top k; closest; next best; median of a stream | MUST
# Phase 9: Heap / Priority Queue

A heap gives the best item without sorting the rest. For the k largest keep a min-heap of size k, so its top is the weakest survivor.

**Signals:** top k; closest; next best; median of a stream.

## The big analogy: an emergency room

Patients aren't seen in arrival order and nobody sorts the whole waiting room. The room only guarantees that **the most urgent person is next**, and fixes that cheaply when someone arrives or leaves. That is a heap. For "the k largest", picture a VIP room with k seats and a bouncer watching only the weakest VIP, which is why it is a **min**-heap.

| Need | Picture | Meet it in |
|---|---|---|
| k best | a small VIP room | 215, 703, 347, 973 |
| repeatedly the largest | the emergency room | 1046, 767, 1405 |
| available, then best | a gate before the waiting room | 502, 1834, 621 |
| middle | two rooms facing each other | 295 |

## Template
```java
PriorityQueue<Integer> heap = new PriorityQueue<>();
for (int x : a) {
    heap.add(x);
    if (heap.size() > k) heap.poll(); // evict the weakest
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Heap / Priority Queue — Easy | 2 problems | MUST
# Heap / Priority Queue: Easy

Patterns in this group: Min-heap of k, Max-heap simulation.

@@ 3 | TOPIC | 172 — 703. Kth Largest Element in a Stream | Easy · Min-heap of k | MUST
# 172 — 703. Kth Largest Element in a Stream

**Problem 172 – Topic: Heap / Priority Queue | Difficulty: Easy | Pattern: Min-heap of k**

[Open on LeetCode](https://leetcode.com/problems/kth-largest-element-in-a-stream/)

## 1. Problem Statement
After each inserted value return the kth largest so far, duplicates counted.

### Examples
```text
Input:  ["KthLargest", "add", "add", "add", "add", "add"]
        [[3, [4, 5, 8, 2]], [3], [5], [10], [9], [4]]
Output: [null, 4, 5, 5, 8, 8]

Input:  ["KthLargest", "add", "add", "add", "add"]
        [[4, [7, 7, 7, 7, 8, 3]], [2], [10], [9], [9]]
Output: [null, 7, 7, 7, 8]
```

### Constraints
```text
0 <= nums.length <= 10^4
1 <= k <= nums.length + 1
-10^4 <= nums[i] <= 10^4
-10^4 <= val <= 10^4
At most 10^4 calls will be made to add.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A VIP room with k chairs
A VIP room has k chairs and the weakest VIP stands by the door. A newcomer walks in, and if there are too many the weakest leaves. Whoever is at the door is always the kth best.

| In the story | In the solution |
|---|---|
| room | heap of k |
| door | minimum |

### Pattern signal
**Min-heap of k.** A value below the k largest can never return, since values are only added.

### Thought process
1. **First idea:** Re-sort all values at every call.
2. **The insight:** A value below the k largest can never return, since values are only added.

### Optimal approach
1. Feed every value through add.
2. Evict when the size exceeds k.
3. Return the smallest kept.

### Dry run
```text
k 3, start 4,5,8,2
add   room    answer
-     4,5,8   4
3     4,5,8   4
5     5,5,8   5
10    5,8,10  5
```

### Why it works
The heap holds exactly the k largest seen, and their minimum is by definition the kth largest.

## 3. Java Solution with Explanation
### Code
```java
class KthLargest {
    private final PriorityQueue<Integer> room = new PriorityQueue<>();
    private final int k;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        for (int x : nums) add(x);
    }

    public int add(int val) {
        room.add(val);
        if (room.size() > k) room.poll();
        return room.peek();
    }
}
```

### Code walkthrough
- `new PriorityQueue<>()`: A min-heap, so the weakest is on top.
- `room.poll()`: The weakest leaves.
- `room.peek()`: Whoever is at the door.

### Complexity
- **Time:** O(log k) per add
- **Space:** O(k)

### Common mistakes
1. **A max-heap shows the first.** Fix: use a min-heap.
2. **Keeping everything wastes memory.** Fix: cap it at k.
3. **The start may hold k-1 values.** Fix: push through add.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty start | `k 1, add 3` | `3` |
| Tie | `k 2, [5], add 5` | `5` |
| Negative | `k 1, [], add -1` | `-1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[176 — 215. Kth Largest Element in an Array]]: Once. The key idea there: You need one boundary, not the full order.
- [[183 — 295. Find Median from Data Stream]]: k moves. The key idea there: The median needs only the two middle borders, which two heaps expose.

### In a real interview
Say it in this order:

1. **Brute force:** Re-sort all values at every call.
2. **Insight:** A value below the k largest can never return, since values are only added.
3. **Approach in a few words:** A bounded min-heap.
4. **Cost:** time O(log k) per add; space O(k).

## 5. Recall
Q: 703. Kth Largest Element in a Stream: which pattern, and what is the insight?
A: Min-heap of k. A value below the k largest can never return, since values are only added.

Q: 703. Kth Largest Element in a Stream: what is the analogy, and what does it stand for?
A: A VIP room with k chairs: room → heap of k; door → minimum.

Q: 703. Kth Largest Element in a Stream: Why a min-heap for k largest?
A: Its top is the one to evict and the answer.

Q: 703. Kth Largest Element in a Stream: what are the time and space costs?
A: Time O(log k) per add. Space O(k).

@@ 3 | TOPIC | 173 — 1046. Last Stone Weight | Easy · Max-heap simulation | MUST
# 173 — 1046. Last Stone Weight

**Problem 173 – Topic: Heap / Priority Queue | Difficulty: Easy | Pattern: Max-heap simulation**

[Open on LeetCode](https://leetcode.com/problems/last-stone-weight/)

## 1. Problem Statement
You get stone weights. Each turn smash the two heaviest, x at most y: equal stones are both destroyed, otherwise a stone of weight y-x remains. Return the final stone's weight, or 0 if none is left.

### Examples
```text
Input:  stones = [2,7,4,1,8,1]
Output: 1

Input:  stones = [1]
Output: 1
```

### Constraints
```text
1 <= stones.length <= 30
1 <= stones[i] <= 1000
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An arena for the two strongest
An arena always calls up the two strongest. If one survives, weakened, they rejoin the waiting room and may be called again.

| In the story | In the solution |
|---|---|
| room | max-heap |
| survivor | difference |

### Pattern signal
**Max-heap simulation.** Each round only needs the two current maxima, and the set changes slightly.

### Thought process
1. **First idea:** Sort again before every round.
2. **The insight:** Each round only needs the two current maxima, and the set changes slightly.

### Optimal approach
1. Add all to a max-heap.
2. Poll two.
3. Add a non-zero difference.
4. Return the last or zero.

### Dry run
```text
2,7,4,1,8,1
smash  left
8,7    4,2,1,1,1
4,2    2,1,1,1
2,1    1,1,1
1,1    1
```

### Why it works
The heap always holds exactly the current stones, so each round smashes the required two.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> room = new PriorityQueue<>(Collections.reverseOrder());
        for (int s : stones) room.add(s);
        while (room.size() > 1) {
            int first = room.poll(), second = room.poll();
            if (first != second) room.add(first - second);
        }
        return room.isEmpty() ? 0 : room.peek();
    }
}
```

### Code walkthrough
- `reverseOrder`: A max-heap of stones.
- `first - second`: The first polled is never smaller.
- `? 0 :`: Everything was destroyed.

### Complexity
- **Time:** O(n log n)
- **Space:** O(n)

### Common mistakes
1. **Java's default gives the lightest.** Fix: reverse the order.
2. **Survivors change the order.** Fix: use a heap.
3. **Polling empty can fail.** Fix: loop while two remain.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[3]` | `3` |
| Equal | `[2,2]` | `0` |
| Two | `[2,5]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[172 — 703. Kth Largest Element in a Stream]]: Bounded. The key idea there: A value below the k largest can never return, since values are only added.
- **1049. Stones II**: Choice means DP.

### In a real interview
Say it in this order:

1. **Brute force:** Sort again before every round.
2. **Insight:** Each round only needs the two current maxima, and the set changes slightly.
3. **Approach in a few words:** Max-heap simulation.
4. **Cost:** time O(n log n); space O(n).

## 5. Recall
Q: 1046. Last Stone Weight: which pattern, and what is the insight?
A: Max-heap simulation. Each round only needs the two current maxima, and the set changes slightly.

Q: 1046. Last Stone Weight: what is the analogy, and what does it stand for?
A: An arena for the two strongest: room → max-heap; survivor → difference.

Q: 1046. Last Stone Weight: What returns to the stone heap?
A: A non-zero difference.

Q: 1046. Last Stone Weight: what are the time and space costs?
A: Time O(n log n). Space O(n).

@@ 2 | MODULE | Heap / Priority Queue — Medium | 9 problems | MUST
# Heap / Priority Queue: Medium

Patterns in this group: Frequency top k, Distance top k, Min-heap of k, Heap and cooldown, Per-user lists, Two orders, Alternate most frequent, Greedy heap, Difference array.

@@ 3 | TOPIC | 174 — 347. Top K Frequent Elements | Medium · Frequency top k | MUST
# 174 — 347. Top K Frequent Elements

**Problem 174 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Frequency top k**

[Open on LeetCode](https://leetcode.com/problems/top-k-frequent-elements/)

## 1. Problem Statement
You get an integer array and `k`. Return the k values that occur most often, in any order. Follow-up: better than O(n log n).

### Examples
```text
Input:  nums = [1,1,1,2,2,3], k = 2
Output: [1,2]

Input:  nums = [1], k = 1
Output: [1]

Input:  nums = [1,2,1,2,1,2,3,1,3,2], k = 2
Output: [1,2]
```

### Constraints
```text
1 <= nums.length <= 10^5
-10^4 <= nums[i] <= 10^4
k is in the range [1, the number of unique elements in the array].
It is guaranteed that the answer is unique.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A popularity contest with a small stage
A popularity contest has a stage for k. Count the votes, then let each contestant step up, and whenever it is crowded the least popular steps down.

| In the story | In the solution |
|---|---|
| votes | map |
| stage | heap by count |

### Pattern signal
**Frequency top k.** Only a boundary of k is needed, so a small min-heap by count replaces sorting.

### Thought process
1. **First idea:** Sort every distinct value by its count.
2. **The insight:** Only a boundary of k is needed, so a small min-heap by count replaces sorting.

### Optimal approach
1. Count with a map.
2. Push each distinct value.
3. Evict the least frequent beyond k.
4. Return the heap.

### Dry run
```text
1,1,1,2,2,3  k 2
enter  stage
1:3    1
2:2    2,1
3:1    evict 3
```

### Why it works
After each value the heap holds the k most frequent seen, and anything evicted has k at least as frequent.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> votes = new HashMap<>();
        for (int x : nums) votes.merge(x, 1, Integer::sum);
        PriorityQueue<Integer> stage = new PriorityQueue<>((a, b) -> votes.get(a) - votes.get(b));
        for (int x : votes.keySet()) {
            stage.add(x);
            if (stage.size() > k) stage.poll();
        }
        int[] result = new int[k];
        for (int i = 0; i < k; i++) result[i] = stage.poll();
        return result;
    }
}
```

### Code walkthrough
- `votes.merge`: Count one vote.
- `votes.get(a) - votes.get(b)`: The least popular on top.
- `votes.keySet()`: Each distinct value once.

### Complexity
- **Time:** O(n + u log k) for u distinct values
- **Space:** O(u)

### Common mistakes
1. **The largest values are not the most frequent.** Fix: order by count.
2. **Pushing occurrences duplicates values.** Fix: push keys.
3. **Evicting from a max-heap removes winners.** Fix: use a min-heap.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1], 1` | `[1]` |
| All | `[1,2], 2` | `[1,2]` |
| Negative | `[-1,-1,2], 1` | `[-1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[176 — 215. Kth Largest Element in an Array]]: No counts. The key idea there: You need one boundary, not the full order.
- **451. Sort by Frequency**: Keep all.

### In a real interview
Say this: *"Count, then a small heap, or buckets for linear time."*

## 5. Recall
Q: 347. Top K Frequent Elements: which pattern, and what is the insight?
A: Frequency top k. Only a boundary of k is needed, so a small min-heap by count replaces sorting.

Q: 347. Top K Frequent Elements: what is the analogy, and what does it stand for?
A: A popularity contest with a small stage: votes → map; stage → heap by count.

Q: 347. Top K Frequent Elements: What orders the top-frequent heap?
A: The frequency, smallest first.

Q: 347. Top K Frequent Elements: what are the time and space costs?
A: Time O(n + u log k) for u distinct values. Space O(u).

@@ 3 | TOPIC | Practice — Review 29 | Re-solve without looking | MUST
# Review 29

Solve on a blank page. No notes, hints or solutions.

## 347. Top K Frequent Elements
Return the k most frequent values.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/top-k-frequent-elements/)

## 212. Word Search II
Find all dictionary words in a letter grid.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/word-search-ii/)

## 337. House Robber III
Maximize robbery on a tree with no adjacent nodes.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/house-robber-iii/)

## 543. Diameter of Binary Tree
Find the longest path in edges between any nodes.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/diameter-of-binary-tree/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 347 | | | | |
| 212 | | | | |
| 337 | | | | |
| 543 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[174 — 347. Top K Frequent Elements]]
- [[171 — 212. Word Search II]]
- [[160 — 337. House Robber III]]
- [[136 — 543. Diameter of Binary Tree]]

@@ 3 | TOPIC | 175 — 973. K Closest Points to Origin | Medium · Distance top k | MUST
# 175 — 973. K Closest Points to Origin

**Problem 175 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Distance top k**

[Open on LeetCode](https://leetcode.com/problems/k-closest-points-to-origin/)

## 1. Problem Statement
You get points on the X-Y plane and an integer `k`. Return the k points closest to the origin by Euclidean distance, in any order.

### Examples
```text
Input:  points = [[1,3],[-2,2]], k = 1
Output: [[-2,2]]

Input:  points = [[3,3],[5,-1],[-2,4]], k = 2
Output: [[3,3],[-2,4]]
```

### Constraints
```text
1 <= k <= points.length <= 10^4
-10^4 <= xi, yi <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Seats around a campfire
There are k seats around a campfire. When someone closer arrives, the farthest seated person is the one to give up a seat, so that is who the heap must show.

| In the story | In the solution |
|---|---|
| farthest | max-heap top |
| distance | x² + y² |

### Pattern signal
**Distance top k.** To keep the k nearest you only need to know the farthest of those kept.

### Thought process
1. **First idea:** Sort all points by distance.
2. **The insight:** To keep the k nearest you only need to know the farthest of those kept.

### Optimal approach
1. Push each point ordered by descending squared distance.
2. Evict beyond k.
3. Return those left.

### Dry run
```text
3,3  5,-1  -2,4  k 2
point  squared  kept
3,3    18       18
5,-1   26       18,26
-2,4   20       18,20
```

### Why it works
An evicted point has k others at least as near, so it isn't needed.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> seats = new PriorityQueue<>((a, b) -> squared(b) - squared(a));
        for (int[] p : points) {
            seats.add(p);
            if (seats.size() > k) seats.poll();
        }
        return seats.toArray(new int[0][]);
    }

    private int squared(int[] p) {
        return p[0] * p[0] + p[1] * p[1];
    }
}
```

### Code walkthrough
- `squared(b) - squared(a)`: The farthest is on top.
- `seats.poll()`: The farthest loses the seat.
- `p[0] * p[0]`: Comparing squares avoids roots.

### Complexity
- **Time:** O(n log k)
- **Space:** O(k)

### Common mistakes
1. **A min-heap evicts the nearest.** Fix: use a max-heap.
2. **Roots add rounding.** Fix: compare squares.
3. **Manhattan is another distance.** Fix: square both coordinates.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Origin | `[ [0,0] ], 1` | `[ [0,0] ]` |
| Negative | `[ [-1,0],[2,0] ], 1` | `[ [-1,0] ]` |
| All | `k = n` | `every point` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[176 — 215. Kth Largest Element in an Array]]: Values. The key idea there: You need one boundary, not the full order.
- [[063 — 658. Find K Closest Elements]]: Sorted line. The key idea there: The answer is contiguous, and "should move right" changes only once as the start grows.

### In a real interview
Say it in this order:

1. **Brute force:** Sort all points by distance.
2. **Insight:** To keep the k nearest you only need to know the farthest of those kept.
3. **Approach in a few words:** A bounded max-heap on squared distance.
4. **Cost:** time O(n log k); space O(k).

## 5. Recall
Q: 973. K Closest Points to Origin: which pattern, and what is the insight?
A: Distance top k. To keep the k nearest you only need to know the farthest of those kept.

Q: 973. K Closest Points to Origin: what is the analogy, and what does it stand for?
A: Seats around a campfire: farthest → max-heap top; distance → x² + y².

Q: 973. K Closest Points to Origin: Who is evicted among closest points?
A: The farthest kept.

Q: 973. K Closest Points to Origin: what are the time and space costs?
A: Time O(n log k). Space O(k).

@@ 3 | TOPIC | 176 — 215. Kth Largest Element in an Array | Medium · Min-heap of k | MUST
# 176 — 215. Kth Largest Element in an Array

**Problem 176 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Min-heap of k**

[Open on LeetCode](https://leetcode.com/problems/kth-largest-element-in-an-array/)

## 1. Problem Statement
You get an integer array and `k`. Return the kth largest element in sorted order, so duplicates count separately. Can you do it without sorting?

### Examples
```text
Input:  nums = [3,2,1,5,6,4], k = 2
Output: 5

Input:  nums = [3,2,3,1,2,4,5,5,6], k = 4
Output: 4
```

### Constraints
```text
1 <= k <= nums.length <= 10^5
-10^4 <= nums[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The last podium place
A podium has k places and a challenger only has to beat the lowest. When all have challenged, whoever holds the lowest place is kth.

| In the story | In the solution |
|---|---|
| podium | min-heap |
| lowest | top |

### Pattern signal
**Min-heap of k.** You need one boundary, not the full order.

### Thought process
1. **First idea:** Sort the whole array.
2. **The insight:** You need one boundary, not the full order.

### Optimal approach
1. Push each value into a min-heap.
2. Evict beyond k.
3. Return the top.

### Dry run
```text
3,2,1,5,6,4  k 2
value  podium
3      3
2      2,3
1      2,3
5      3,5
6      5,6
4      5,6
```

### Why it works
The heap holds the k largest processed so far, and their minimum is the kth largest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> podium = new PriorityQueue<>();
        for (int x : nums) {
            podium.add(x);
            if (podium.size() > k) podium.poll();
        }
        return podium.peek();
    }
}
```

### Code walkthrough
- `podium.add(x)`: Every value challenges.
- `podium.poll()`: The lowest falls off.
- `podium.peek()`: The kth largest.

### Complexity
- **Time:** O(n log k)
- **Space:** O(k)

### Common mistakes
1. **Duplicates count.** Fix: do not deduplicate.
2. **kth smallest is the mirror.** Fix: keep the k largest.
3. **A full heap wastes work.** Fix: cap it at k.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1], 1` | `1` |
| Tie | `[2,2], 2` | `2` |
| Last | `[3,1], 2` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[172 — 703. Kth Largest Element in a Stream]]: Keep it. The key idea there: A value below the k largest can never return, since values are only added.
- [[174 — 347. Top K Frequent Elements]]: A key. The key idea there: Only a boundary of k is needed, so a small min-heap by count replaces sorting.

### In a real interview
Say it in this order:

1. **Brute force:** Sort the whole array.
2. **Insight:** You need one boundary, not the full order.
3. **Approach in a few words:** A heap, or quickselect for average linear.
4. **Cost:** time O(n log k); space O(k).

## 5. Recall
Q: 215. Kth Largest Element in an Array: which pattern, and what is the insight?
A: Min-heap of k. You need one boundary, not the full order.

Q: 215. Kth Largest Element in an Array: what is the analogy, and what does it stand for?
A: The last podium place: podium → min-heap; lowest → top.

Q: 215. Kth Largest Element in an Array: What is the top of the size-k min-heap?
A: The kth largest.

Q: 215. Kth Largest Element in an Array: what are the time and space costs?
A: Time O(n log k). Space O(k).

@@ 3 | TOPIC | 177 — 621. Task Scheduler | Medium · Heap and cooldown | MUST
# 177 — 621. Task Scheduler

**Problem 177 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Heap and cooldown**

[Open on LeetCode](https://leetcode.com/problems/task-scheduler/)

## 1. Problem Statement
Each task takes one interval. Equal letters need n intervals between them. Return the least total, idling allowed.

### Examples
```text
Input:  tasks = ["A","A","A","B","B","B"], n = 2
Output: 8

Input:  tasks = ["A","C","A","B","D","B"], n = 1
Output: 6

Input:  tasks = ["A","A","A", "B","B","B"], n = 3
Output: 10
```

### Constraints
```text
1 <= tasks.length <= 10^4
tasks[i] is an uppercase English letter.
0 <= n <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A chef with cooling pans
A chef's pans must cool. Each minute cook the ready dish with most orders left. Its pan returns at a known minute, so a queue keeps the cooling pans in return order.

| In the story | In the solution |
|---|---|
| ready | max-heap |
| cooling | queue |

### Pattern signal
**Heap and cooldown.** The most frequent task is the most constrained and cool-downs finish in the order they began.

### Thought process
1. **First idea:** Search every schedule.
2. **The insight:** The most frequent task is the most constrained and cool-downs finish in the order they began.

### Optimal approach
1. Count letters.
2. Tick the clock.
3. Run the most frequent ready.
4. Queue it with its return time.
5. Release it when due.

### Dry run
```text
AAA BBB n 2
time  action
1     A
2     B
3     idle
4     A
5     B
6     idle
7     A
8     B, return 8
```

### Why it works
Running a most frequent ready task first never worsens the gaps left for its remaining copies, which are what force idling.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] count = new int[26];
        for (char t : tasks) count[t - 'A']++;
        PriorityQueue<Integer> ready = new PriorityQueue<>(Collections.reverseOrder());
        for (int c : count) {
            if (c > 0) ready.add(c);
        }
        Queue<int[]> cooling = new ArrayDeque<>(); // remaining, ready time
        int time = 0;
        while (!ready.isEmpty() || !cooling.isEmpty()) {
            time++;
            if (!ready.isEmpty()) {
                int left = ready.poll() - 1;
                if (left > 0) cooling.add(new int[] {left, time + n});
            }
            if (!cooling.isEmpty() && cooling.peek()[1] == time) ready.add(cooling.poll()[0]);
        }
        return time;
    }
}
```

### Code walkthrough
- `ready.poll() - 1`: One copy has been done.
- `time + n`: When it may be used again.
- `cooling.peek()[1] == time`: The pan has cooled.

### Complexity
- **Time:** O(total intervals)
- **Space:** O(1), 26 letters

### Common mistakes
1. **Idle intervals count.** Fix: keep ticking.
2. **Releasing too soon breaks cooldown.** Fix: compare the ready time.
3. **Letter order is irrelevant.** Fix: choose by count.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| n 0 | `AA, 0` | `2` |
| One | `A, 9` | `1` |
| Gap | `AA, 2` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[180 — 767. Reorganize String]]: Cooldown one. The key idea there: The commonest letter is the most at risk of being stranded, so use it whenever legal.
- [[179 — 1834. Single-Threaded CPU]]: Other priority. The key idea there: Arriving and being chosen follow two different orders, a sort and a heap.

### In a real interview
Say this: *"A ready heap and a cooling queue, or the gap formula."*

## 5. Recall
Q: 621. Task Scheduler: which pattern, and what is the insight?
A: Heap and cooldown. The most frequent task is the most constrained and cool-downs finish in the order they began.

Q: 621. Task Scheduler: what is the analogy, and what does it stand for?
A: A chef with cooling pans: ready → max-heap; cooling → queue.

Q: 621. Task Scheduler: Which task runs each tick?
A: The ready one with most copies left.

Q: 621. Task Scheduler: what are the time and space costs?
A: Time O(total intervals). Space O(1), 26 letters.

@@ 3 | TOPIC | 178 — 355. Design Twitter | Medium · Per-user lists | MUST
# 178 — 355. Design Twitter

**Problem 178 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Per-user lists**

[Open on LeetCode](https://leetcode.com/problems/design-twitter/)

## 1. Problem Statement
Design posting, following, unfollowing and a feed of the ten newest tweets by the user and followees.

### Examples
```text
Input:  ["Twitter", "postTweet", "getNewsFeed", "follow", "postTweet", "getNewsFeed", "unfollow", "getNewsFeed"]
        [[], [1, 5], [1], [1, 2], [2, 6], [1], [1, 2], [1]]
Output: [null, null, [5], null, null, [6, 5], null, [5]]
```

### Constraints
```text
1 <= userId, followerId, followeeId <= 500
0 <= tweetId <= 10^4
All the tweets have unique IDs.
At most 3 * 10^4 calls will be made to postTweet, getNewsFeed, follow, and unfollow.
A user cannot follow himself.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Newspapers on a table
Each author's tweets form a newest-first pile. Put just the top of each followed pile on a table, take the newest, uncover the one below it, and stop after ten.

| In the story | In the solution |
|---|---|
| pile | author's list |
| table | heap of tops |

### Pattern signal
**Per-user lists.** A feed merges sorted lists and stops after ten items.

### Thought process
1. **First idea:** Collect and sort every relevant tweet.
2. **The insight:** A feed merges sorted lists and stops after ten items.

### Optimal approach
1. Stamp tweets.
2. Store follows in sets.
3. Heap each author's newest.
4. Poll up to ten and push the next older.

### Dry run
```text
1 follows 2. posts 1:5 then 2:6
take  feed
6     6
5     6,5
```

### Why it works
Every unshown tweet is behind its author's entry, so the heap's newest is the globally newest unshown one.

## 3. Java Solution with Explanation
### Code
```java
class Twitter {
    private static class Tweet {
        int id, time;
        Tweet older;

        Tweet(int id, int time, Tweet older) {
            this.id = id;
            this.time = time;
            this.older = older;
        }
    }

    private final Map<Integer, Tweet> latest = new HashMap<>();
    private final Map<Integer, Set<Integer>> following = new HashMap<>();
    private int clock;

    public void postTweet(int userId, int tweetId) {
        latest.put(userId, new Tweet(tweetId, clock++, latest.get(userId)));
    }

    public List<Integer> getNewsFeed(int userId) {
        Set<Integer> authors = new HashSet<>(following.getOrDefault(userId, Set.of()));
        authors.add(userId);
        PriorityQueue<Tweet> table = new PriorityQueue<>((a, b) -> b.time - a.time);
        for (int author : authors) {
            if (latest.containsKey(author)) table.add(latest.get(author));
        }
        List<Integer> feed = new ArrayList<>();
        while (!table.isEmpty() && feed.size() < 10) {
            Tweet t = table.poll();
            feed.add(t.id);
            if (t.older != null) table.add(t.older);
        }
        return feed;
    }

    public void follow(int followerId, int followeeId) {
        following.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
    }

    public void unfollow(int followerId, int followeeId) {
        Set<Integer> set = following.get(followerId);
        if (set != null) set.remove(followeeId);
    }
}
```

### Code walkthrough
- `clock++`: IDs don't show time, so stamp them.
- `authors.add(userId)`: You always see your own tweets.
- `table.add(t.older)`: Uncover that author's next tweet.

### Complexity
- **Time:** O(1) updates, O(F log F) per feed
- **Space:** O(tweets + follows)

### Common mistakes
1. **IDs are not times.** Fix: stamp each post.
2. **Users see themselves.** Fix: add the user as author.
3. **Adding self to the follow set corrupts it.** Fix: copy it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `feed 1` | `[]` |
| Self | `post 1,5 then feed` | `[5]` |
| Unfollow absent | `unfollow 1,2` | `no change` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[129 — 23. Merge k Sorted Lists]]: The same merge. The key idea there: You repeatedly need the minimum of k items of which only one changes, which is a heap.
- [[276 — 146. LRU Cache]]: Recency. The key idea there: A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).

### In a real interview
Say it in this order:

1. **Brute force:** Collect and sort every relevant tweet.
2. **Insight:** A feed merges sorted lists and stops after ten items.
3. **Approach in a few words:** Per-user lists merged lazily.
4. **Cost:** time O(1) updates, O(F log F) per feed; space O(tweets + follows).

## 5. Recall
Q: 355. Design Twitter: which pattern, and what is the insight?
A: Per-user lists. A feed merges sorted lists and stops after ten items.

Q: 355. Design Twitter: what is the analogy, and what does it stand for?
A: Newspapers on a table: pile → author's list; table → heap of tops.

Q: 355. Design Twitter: What is in the feed heap?
A: The newest unshown tweet per author.

Q: 355. Design Twitter: what are the time and space costs?
A: Time O(1) updates, O(F log F) per feed. Space O(tweets + follows).

@@ 3 | TOPIC | 179 — 1834. Single-Threaded CPU | Medium · Two orders | SHOULD
# 179 — 1834. Single-Threaded CPU

**Problem 179 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Two orders**

[Open on LeetCode](https://leetcode.com/problems/single-threaded-cpu/)

## 1. Problem Statement
Tasks become available at a time. An idle CPU runs the shortest available, by index on ties, to completion. Return the order.

### Examples
```text
Input:  tasks = [[1,2],[2,4],[3,2],[4,1]]
Output: [0,2,3,1]

Input:  tasks = [[7,10],[7,12],[7,5],[7,4],[7,2]]
Output: [4,3,2,0,1]
```

### Constraints
```text
1 <= tasks.length <= 10^5
tasks[i] = [enqueueTimei, processingTimei]
1 <= enqueueTimei, processingTimei <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A single barber
A single barber has a list of arrival times at the door and a waiting room that always offers the quickest haircut. If the room is empty they sleep until the next arrival.

| In the story | In the solution |
|---|---|
| door list | sorted indices |
| room | min-heap |

### Pattern signal
**Two orders.** Arriving and being chosen follow two different orders, a sort and a heap.

### Thought process
1. **First idea:** Scan all tasks at each decision, in O(n²).
2. **The insight:** Arriving and being chosen follow two different orders, a sort and a heap.

### Optimal approach
1. Sort indices by arrival.
2. Jump time when idle.
3. Admit arrived tasks.
4. Run the smallest.
5. Advance time.

### Dry run
```text
(1,2),(2,4),(3,2),(4,1)
time  room  run  finish
1     0     0    3
3     1,2   2    5
5     1,3   3    6
6     1     1    10
```

### Why it works
At each decision the heap holds exactly the available tasks, ordered by the required duration and index rule.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] getOrder(int[][] tasks) {
        int n = tasks.length;
        Integer[] door = new Integer[n];
        for (int i = 0; i < n; i++) door[i] = i;
        Arrays.sort(door, (a, b) -> Integer.compare(tasks[a][0], tasks[b][0]));
        PriorityQueue<Integer> room = new PriorityQueue<>((a, b) ->
                tasks[a][1] != tasks[b][1] ? Integer.compare(tasks[a][1], tasks[b][1]) : Integer.compare(a, b));
        int[] order = new int[n];
        long time = 0;
        int next = 0;
        for (int done = 0; done < n; done++) {
            if (room.isEmpty()) time = Math.max(time, tasks[door[next]][0]);
            while (next < n && tasks[door[next]][0] <= time) room.add(door[next++]);
            int task = room.poll();
            order[done] = task;
            time += tasks[task][1];
        }
        return order;
    }
}
```

### Code walkthrough
- `Integer.compare(a, b)`: The index breaks ties.
- `Math.max(time`: Sleep until work exists.
- `long time`: The total time overflows an int.

### Complexity
- **Time:** O(n log n)
- **Space:** O(n)

### Common mistakes
1. **The time overflows an int.** Fix: use long.
2. **Sorting loses indices.** Fix: sort indices.
3. **The shortest may not have arrived.** Fix: admit by time.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [5,1] ]` | `[0]` |
| Tie | `two (1,1)` | `[0,1]` |
| Gap | `(1,1),(9,1)` | `[0,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[184 — 502. IPO]]: Unlock by money. The key idea there: Among affordable projects only the profit matters, so unlock by capital and choose by profit.
- [[177 — 621. Task Scheduler]]: Cooling. The key idea there: The most frequent task is the most constrained and cool-downs finish in the order they began.

### In a real interview
Say it in this order:

1. **Brute force:** Scan all tasks at each decision, in O(n²).
2. **Insight:** Arriving and being chosen follow two different orders, a sort and a heap.
3. **Approach in a few words:** Sort availability, heap priority.
4. **Cost:** time O(n log n); space O(n).

## 5. Recall
Q: 1834. Single-Threaded CPU: which pattern, and what is the insight?
A: Two orders. Arriving and being chosen follow two different orders, a sort and a heap.

Q: 1834. Single-Threaded CPU: what is the analogy, and what does it stand for?
A: A single barber: door list → sorted indices; room → min-heap.

Q: 1834. Single-Threaded CPU: Which two orders does the CPU use?
A: Arrival, then duration with index.

Q: 1834. Single-Threaded CPU: what are the time and space costs?
A: Time O(n log n). Space O(n).

@@ 3 | TOPIC | 180 — 767. Reorganize String | Medium · Alternate most frequent | SHOULD
# 180 — 767. Reorganize String

**Problem 180 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Alternate most frequent**

[Open on LeetCode](https://leetcode.com/problems/reorganize-string/)

## 1. Problem Statement
You get a string of lowercase letters. Rearrange it so that no two adjacent characters are equal and return any such arrangement, or an empty string if it is impossible.

### Examples
```text
Input:  s = "aab"
Output: "aba"

Input:  s = "aaab"
Output: ""
```

### Constraints
```text
1 <= s.length <= 500
s consists of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The star player rests a turn
Always field the star player, but whoever just played sits out one turn. After that they return to the bench pool and may be picked again.

| In the story | In the solution |
|---|---|
| bench | held letter |
| most left | max-heap |

### Pattern signal
**Alternate most frequent.** The commonest letter is the most at risk of being stranded, so use it whenever legal.

### Thought process
1. **First idea:** Try permutations of the letters.
2. **The insight:** The commonest letter is the most at risk of being stranded, so use it whenever legal.

### Optimal approach
1. Count.
2. Poll the most frequent.
3. Append it.
4. Return the held letter.
5. Hold this one.
6. Check the final length.

### Dry run
```text
aab
pick  bench  result
a     a:1    a
b     -      ab
a     -      aba
```

### Why it works
The hold makes neighbors differ, and choosing the maximum fails only when one letter exceeds half the length rounded up.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String reorganizeString(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;
        PriorityQueue<Integer> pool = new PriorityQueue<>((a, b) -> count[b] - count[a]);
        for (int i = 0; i < 26; i++) {
            if (count[i] > 0) pool.add(i);
        }
        StringBuilder result = new StringBuilder();
        int bench = -1;
        while (!pool.isEmpty()) {
            int letter = pool.poll();
            result.append((char) ('a' + letter));
            count[letter]--;
            if (bench >= 0 && count[bench] > 0) pool.add(bench);
            bench = letter;
        }
        return result.length() == s.length() ? result.toString() : "";
    }
}
```

### Code walkthrough
- `count[b] - count[a]`: The most remaining first.
- `bench = letter`: This letter rests one turn.
- `result.length() == s.length()`: A shorter result means impossible.

### Complexity
- **Time:** O(n log 26)
- **Space:** O(n) for the output

### Common mistakes
1. **Immediate reinsertion repeats a letter.** Fix: hold it a turn.
2. **A partial result is invalid.** Fix: check the length.
3. **Changing a queued count corrupts the heap.** Fix: change only polled letters.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `"a"` | `"a"` |
| Impossible | `"aa"` | `""` |
| Balanced | `"ab"` | `"ab"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[177 — 621. Task Scheduler]]: Longer rest. The key idea there: The most frequent task is the most constrained and cool-downs finish in the order they began.
- [[181 — 1405. Longest Happy String]]: Two allowed. The key idea there: Reducing the largest pile prevents stranding it, and one separator is the cheapest break.

### In a real interview
Say it in this order:

1. **Brute force:** Try permutations of the letters.
2. **Insight:** The commonest letter is the most at risk of being stranded, so use it whenever legal.
3. **Approach in a few words:** Max-count with a hold.
4. **Cost:** time O(n log 26); space O(n) for the output.

## 5. Recall
Q: 767. Reorganize String: which pattern, and what is the insight?
A: Alternate most frequent. The commonest letter is the most at risk of being stranded, so use it whenever legal.

Q: 767. Reorganize String: what is the analogy, and what does it stand for?
A: The star player rests a turn: bench → held letter; most left → max-heap.

Q: 767. Reorganize String: Why hold the last letter?
A: To prevent using it immediately again.

Q: 767. Reorganize String: what are the time and space costs?
A: Time O(n log 26). Space O(n) for the output.

@@ 3 | TOPIC | Practice — Review 30 | Re-solve without looking | MUST
# Review 30

Solve on a blank page. No notes, hints or solutions.

## 767. Reorganize String
Rearrange so equal letters are never adjacent.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/reorganize-string/)

## 621. Task Scheduler
Find minimum intervals to run tasks with cooldown.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/task-scheduler/)

## 297. Serialize and Deserialize Binary Tree
Serialize and restore any binary tree.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/)

## 112. Path Sum
Decide whether a root-to-leaf path has a target sum.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/path-sum/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 767 | | | | |
| 621 | | | | |
| 297 | | | | |
| 112 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[180 — 767. Reorganize String]]
- [[177 — 621. Task Scheduler]]
- [[166 — 297. Serialize and Deserialize Binary Tree]]
- [[142 — 112. Path Sum]]

@@ 3 | TOPIC | 181 — 1405. Longest Happy String | Medium · Greedy heap | SHOULD
# 181 — 1405. Longest Happy String

**Problem 181 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Greedy heap**

[Open on LeetCode](https://leetcode.com/problems/longest-happy-string/)

## 1. Problem Statement
With at most a, b, c of those letters build a longest string lacking aaa, bbb and ccc.

### Examples
```text
Input:  a = 1, b = 1, c = 7
Output: "ccaccbcc"

Input:  a = 7, b = 1, c = 0
Output: "aabaa"
```

### Constraints
```text
0 <= a, b, c <= 100
a + b + c > 0
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The favourite with a limit of two
Serve the favourite dish with a limit of two in a row. At the limit serve a single spoon of the runner-up, which resets the limit as cheaply as possible.

| In the story | In the solution |
|---|---|
| limit | last two |
| runner-up | next ordered |

### Pattern signal
**Greedy heap.** Reducing the largest pile prevents stranding it, and one separator is the cheapest break.

### Thought process
1. **First idea:** Search all valid strings.
2. **The insight:** Reducing the largest pile prevents stranding it, and one separator is the cheapest break.

### Optimal approach
1. Order letters by remaining.
2. Choose the first that makes no triple.
3. Append.
4. Stop when none is possible.

### Dry run
```text
a1 b1 c7
picks  result
cc     cc
a      cca
cc     ccacc
b      ccaccb
cc     ccaccbcc
```

### Why it works
The result can only be limited by too few separators for the largest letter, and they are spent singly.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String longestDiverseString(int a, int b, int c) {
        int[] left = {a, b, c};
        Integer[] order = {0, 1, 2};
        StringBuilder result = new StringBuilder();
        while (true) {
            Arrays.sort(order, (x, y) -> left[y] - left[x]); // a three-item heap
            boolean placed = false;
            for (int i : order) {
                char letter = (char) ('a' + i);
                int n = result.length();
                boolean triple = n >= 2 && result.charAt(n - 1) == letter && result.charAt(n - 2) == letter;
                if (left[i] > 0 && !triple) {
                    result.append(letter);
                    left[i]--;
                    placed = true;
                    break;
                }
            }
            if (!placed) return result.toString();
        }
    }
}
```

### Code walkthrough
- `left[y] - left[x]`: Most plentiful first, three items need no real heap.
- `!triple`: The limit of two in a row.
- `break`: Re-order after every letter.

### Complexity
- **Time:** O(a + b + c)
- **Space:** O(a + b + c) for the output

### Common mistakes
1. **The top may be blocked.** Fix: try the runner-up.
2. **Two separators waste letters.** Fix: add one.
3. **Not every letter must be used.** Fix: stop when stuck.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One kind | `0,0,3` | `"cc"` |
| One | `1,0,0` | `"a"` |
| Equal | `1,1,1` | `"abc"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[180 — 767. Reorganize String]]: Limit one. The key idea there: The commonest letter is the most at risk of being stranded, so use it whenever legal.
- [[177 — 621. Task Scheduler]]: Idles. The key idea there: The most frequent task is the most constrained and cool-downs finish in the order they began.

### In a real interview
Say it in this order:

1. **Brute force:** Search all valid strings.
2. **Insight:** Reducing the largest pile prevents stranding it, and one separator is the cheapest break.
3. **Approach in a few words:** Largest legal letter greedily.
4. **Cost:** time O(a + b + c); space O(a + b + c) for the output.

## 5. Recall
Q: 1405. Longest Happy String: which pattern, and what is the insight?
A: Greedy heap. Reducing the largest pile prevents stranding it, and one separator is the cheapest break.

Q: 1405. Longest Happy String: what is the analogy, and what does it stand for?
A: The favourite with a limit of two: limit → last two; runner-up → next ordered.

Q: 1405. Longest Happy String: When is the runner-up used?
A: When the largest would make a triple.

Q: 1405. Longest Happy String: what are the time and space costs?
A: Time O(a + b + c). Space O(a + b + c) for the output.

@@ 3 | TOPIC | 182 — 1094. Car Pooling | Medium · Difference array | SHOULD
# 182 — 1094. Car Pooling

**Problem 182 – Topic: Heap / Priority Queue | Difficulty: Medium | Pattern: Difference array**

[Open on LeetCode](https://leetcode.com/problems/car-pooling/)

## 1. Problem Statement
Trips give passengers, pickup, drop. Drop frees seats at that point. Decide if capacity suffices.

### Examples
```text
Input:  trips = [[2,1,5],[3,3,7]], capacity = 4
Output: false

Input:  trips = [[2,1,5],[3,3,7]], capacity = 5
Output: true
```

### Constraints
```text
1 <= trips.length <= 1000
trips[i].length == 3
1 <= numPassengersi <= 100
0 <= fromi < toi <= 1000
1 <= capacity <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Plus and minus signs at bus stops
At each bus stop write plus for those boarding and minus for those leaving. Drive once along the route reading the signs to know the load everywhere.

| In the story | In the solution |
|---|---|
| signs | difference array |
| load | prefix sum |

### Pattern signal
**Difference array.** Only the endpoints change the load and positions are at most 1,000.

### Thought process
1. **First idea:** Add each trip to every stop it covers.
2. **The insight:** Only the endpoints change the load and positions are at most 1,000.

### Optimal approach
1. Add at each pickup.
2. Subtract at each drop.
3. Sweep with a running sum.
4. Fail above the capacity.

### Dry run
```text
(2,1,5),(3,3,7) capacity 4
stop  change  load
1     +2      2
3     +3      5, false
```

### Why it works
The running sum at a point counts exactly the trips started and not yet ended.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean carPooling(int[][] trips, int capacity) {
        int[] change = new int[1001];
        for (int[] trip : trips) {
            change[trip[1]] += trip[0];
            change[trip[2]] -= trip[0];
        }
        int load = 0;
        for (int c : change) {
            load += c;
            if (load > capacity) return false;
        }
        return true;
    }
}
```

### Code walkthrough
- `new int[1001]`: One slot for every possible stop.
- `-= trip[0]`: Seats are free at the drop itself.
- `load += c`: Drive, reading the signs.

### Complexity
- **Time:** O(n + 1001)
- **Space:** O(1001)

### Common mistakes
1. **Seats free at the drop.** Fix: subtract there.
2. **Trips do not all overlap.** Fix: sweep.
3. **Stop 1000 needs a slot.** Fix: allocate 1001.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Touching | `(2,0,5),(2,5,9), 2` | `true` |
| Over | `(3,0,1), 2` | `false` |
| Last stop | `(1,0,1000), 1` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **253. Rooms**: Large times, a heap.
- [[010 — 303. Range Sum Query - Immutable]]: The inverse. The key idea there: The array is frozen, so prepare the totals once.

### In a real interview
Say it in this order:

1. **Brute force:** Add each trip to every stop it covers.
2. **Insight:** Only the endpoints change the load and positions are at most 1,000.
3. **Approach in a few words:** A difference array, as coordinates are bounded.
4. **Cost:** time O(n + 1001); space O(1001).

## 5. Recall
Q: 1094. Car Pooling: which pattern, and what is the insight?
A: Difference array. Only the endpoints change the load and positions are at most 1,000.

Q: 1094. Car Pooling: what is the analogy, and what does it stand for?
A: Plus and minus signs at bus stops: signs → difference array; load → prefix sum.

Q: 1094. Car Pooling: What is written at trip ends?
A: Plus at pickup and minus at drop.

Q: 1094. Car Pooling: what are the time and space costs?
A: Time O(n + 1001). Space O(1001).

@@ 2 | MODULE | Heap / Priority Queue — Hard | 2 problems | MUST
# Heap / Priority Queue: Hard

Patterns in this group: Two heaps, Unlock then choose.

@@ 3 | TOPIC | 183 — 295. Find Median from Data Stream | Hard · Two heaps | MUST
# 183 — 295. Find Median from Data Stream

**Problem 183 – Topic: Heap / Priority Queue | Difficulty: Hard | Pattern: Two heaps**

[Open on LeetCode](https://leetcode.com/problems/find-median-from-data-stream/)

## 1. Problem Statement
Design `MedianFinder`. `addNum(num)` adds a number from a stream and `findMedian()` returns the median so far: the middle value, or the mean of the two middle values for an even count.

### Examples
```text
Input:  ["MedianFinder", "addNum", "addNum", "findMedian", "addNum", "findMedian"]
        [[], [1], [2], [], [3], []]
Output: [null, null, null, 1.5, null, 2.0]
```

### Constraints
```text
-10^5 <= num <= 10^5
There will be at least one element in the data structure before calling findMedian.
At most 5 * 10^4 calls will be made to addNum and findMedian.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two rooms facing a corridor
Two rooms face a corridor: the shorter half's tallest and the taller half's shortest stand in the doorways. With sizes within one, the median is always someone in a doorway.

| In the story | In the solution |
|---|---|
| short room | max-heap low |
| tall room | min-heap high |

### Pattern signal
**Two heaps.** The median needs only the two middle borders, which two heaps expose.

### Thought process
1. **First idea:** Insert into a sorted list, in linear time each.
2. **The insight:** The median needs only the two middle borders, which two heaps expose.

### Optimal approach
1. Add to low.
2. Move low's top to high.
3. Move one back if high is larger.
4. Read one top or the average.

### Dry run
```text
add  low  high  median
1    1    -     1
2    1    2     1.5
3    2,1  3     2
```

### Why it works
The crossing keeps every low at most every high and the rebalancing keeps the middle on the tops.

## 3. Java Solution with Explanation
### Code
```java
class MedianFinder {
    private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
    private final PriorityQueue<Integer> high = new PriorityQueue<>();

    public void addNum(int num) {
        low.add(num);
        high.add(low.poll());
        if (high.size() > low.size()) low.add(high.poll());
    }

    public double findMedian() {
        if (low.size() > high.size()) return low.peek();
        return (low.peek() + high.peek()) / 2.0;
    }
}
```

### Code walkthrough
- `high.add(low.poll())`: The largest low crosses over.
- `low.add(high.poll())`: Keep the sizes within one.
- `/ 2.0`: Keep the half.

### Complexity
- **Time:** O(log n) add, O(1) read
- **Space:** O(n)

### Common mistakes
1. **Balanced sizes alone break order.** Fix: cross the top first.
2. **Integer division drops the half.** Fix: divide by 2.0.
3. **Reversed heaps expose the wrong ends.** Fix: make low a max-heap.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `add 5` | `5.0` |
| Descending | `add 2,1` | `1.5` |
| Equal | `add 1,1` | `1.0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **480. Window Median**: Removal.
- [[106 — 4. Median of Two Sorted Arrays]]: A cut. The key idea there: The median needs a valid partition, not merged order.

### In a real interview
Say it in this order:

1. **Brute force:** Insert into a sorted list, in linear time each.
2. **Insight:** The median needs only the two middle borders, which two heaps expose.
3. **Approach in a few words:** Two heaps with order and size invariants.
4. **Cost:** time O(log n) add, O(1) read; space O(n).

## 5. Recall
Q: 295. Find Median from Data Stream: which pattern, and what is the insight?
A: Two heaps. The median needs only the two middle borders, which two heaps expose.

Q: 295. Find Median from Data Stream: what is the analogy, and what does it stand for?
A: Two rooms facing a corridor: short room → max-heap low; tall room → min-heap high.

Q: 295. Find Median from Data Stream: What are the two median invariants?
A: Low is at most high, sizes differ by at most one.

Q: 295. Find Median from Data Stream: what are the time and space costs?
A: Time O(log n) add, O(1) read. Space O(n).

@@ 3 | TOPIC | 184 — 502. IPO | Hard · Unlock then choose | SHOULD
# 184 — 502. IPO

**Problem 184 – Topic: Heap / Priority Queue | Difficulty: Hard | Pattern: Unlock then choose**

[Open on LeetCode](https://leetcode.com/problems/ipo/)

## 1. Problem Statement
Choose up to k distinct projects. Capital requirement is only a threshold and profit adds. Maximize capital from w.

### Examples
```text
Input:  k = 2, w = 0, profits = [1,2,3], capital = [0,1,1]
Output: 4

Input:  k = 3, w = 0, profits = [1,2,3], capital = [0,1,2]
Output: 6
```

### Constraints
```text
1 <= k <= 10^5
0 <= w <= 10^9
n == profits.length
n == capital.length
1 <= n <= 10^5
0 <= profits[i] <= 10^4
0 <= capital[i] <= 10^9
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A gate and a prize table
Your wealth opens gates in price order and each puts a prize on the table. Take the biggest prize. It costs nothing, so it can only open more gates.

| In the story | In the solution |
|---|---|
| gate | sorted requirements |
| table | max-heap |

### Pattern signal
**Unlock then choose.** Among affordable projects only the profit matters, so unlock by capital and choose by profit.

### Thought process
1. **First idea:** Try every sequence of projects.
2. **The insight:** Among affordable projects only the profit matters, so unlock by capital and choose by profit.

### Optimal approach
1. Sort by capital.
2. Add the affordable to a max-heap.
3. Take the largest.
4. Repeat up to k times.

### Dry run
```text
w 0, k 2
wealth  unlocked    take
0       profit 1    1
1       profits 2,3 3
end 4
```

### Why it works
Replacing any choice with a larger affordable profit leaves at least as much capital and options.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        Integer[] gate = new Integer[profits.length];
        for (int i = 0; i < gate.length; i++) gate[i] = i;
        Arrays.sort(gate, (a, b) -> Integer.compare(capital[a], capital[b]));
        PriorityQueue<Integer> table = new PriorityQueue<>(Collections.reverseOrder());
        int next = 0;
        while (k-- > 0) {
            while (next < gate.length && capital[gate[next]] <= w) table.add(profits[gate[next++]]);
            if (table.isEmpty()) break;
            w += table.poll();
        }
        return w;
    }
}
```

### Code walkthrough
- `capital[gate[next]] <= w`: This gate is now affordable.
- `table.poll()`: Take the biggest prize.
- `break`: Nothing more can be unlocked.

### Complexity
- **Time:** O(n log n)
- **Space:** O(n)

### Common mistakes
1. **Capital is not spent.** Fix: only add profit.
2. **The best may be locked.** Fix: unlock first.
3. **Polling empty fails.** Fix: stop then.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Locked | `k1 w0 p[5] c[1]` | `0` |
| Few | `k9 w0 p[2] c[0]` | `2` |
| Exact | `k1 w1 p[2] c[1]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[179 — 1834. Single-Threaded CPU]]: Time unlocks. The key idea there: Arriving and being chosen follow two different orders, a sort and a heap.
- **871. Refueling**: Distance unlocks.

### In a real interview
Say it in this order:

1. **Brute force:** Try every sequence of projects.
2. **Insight:** Among affordable projects only the profit matters, so unlock by capital and choose by profit.
3. **Approach in a few words:** Sort by unlock, heap by profit.
4. **Cost:** time O(n log n); space O(n).

## 5. Recall
Q: 502. IPO: which pattern, and what is the insight?
A: Unlock then choose. Among affordable projects only the profit matters, so unlock by capital and choose by profit.

Q: 502. IPO: what is the analogy, and what does it stand for?
A: A gate and a prize table: gate → sorted requirements; table → max-heap.

Q: 502. IPO: Why take maximum affordable profit?
A: It only expands future options.

Q: 502. IPO: what are the time and space costs?
A: Time O(n log n). Space O(n).

@@ 3 | TOPIC | Practice — Pattern summary: Heap / Priority Queue | Key patterns of the topic | MUST
# Pattern summary: Heap / Priority Queue

A heap gives the best item without sorting the rest. For the k largest keep a min-heap of size k, so its top is the weakest survivor.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Min-heap of k | Return the kth largest after each stream insert. (703, 215) | Starting with fewer than k values |
| Max-heap simulation | Smash the two heaviest stones until one remains. (1046) | Java heaps are min-heaps by default |
| Frequency top k | Return the k most frequent values. (347) | Ordering by value |
| Distance top k | Return the k points nearest the origin. (973) | A square root is unnecessary |
| Heap and cooldown | Find minimum intervals to run tasks with cooldown. (621) | Not counting idle time |
| Per-user lists | Design posting, following and a ten-item news feed. (355) | Forgetting the user's own tweets |
| Two orders | Simulate shortest-available-task CPU scheduling. (1834) | Time overflow |
| Alternate most frequent | Rearrange so equal letters are never adjacent. (767) | Not detecting impossibility |
| Greedy heap | Build the longest string with no three equal letters in a row. (1405) | Stopping when only the top is blocked |
| Difference array | Decide whether a car can serve all trips within capacity. (1094) | Drop-off happens before pickup |
| Two heaps | Maintain a stream's median. (295) | Balancing sizes but not order |
| Unlock then choose | Choose up to k projects to maximize capital. (502) | Taking the cheapest project |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| k largest | VIP room, weakest at the door | min-heap of k | 215, 703, 347 |
| k closest | keep out the farthest | max-heap of k | 973 |
| repeatedly take the biggest | emergency room | max-heap | 1046 |
| no neighbors alike | the star player rests a turn | heap and hold | 767, 1405, 621 |
| unlock, then choose | a gate and a prize table | two orders | 502, 1834 |
| running median | two facing rooms | two heaps | 295 |
| many ranges on a line | people boarding a bus | difference array | 1094 |

## Which heap do I pick?

1. **k largest:** min-heap. **k smallest:** max-heap. The top is who you evict.
2. **Choices unlock over time:** sort by unlock, heap by quality.
3. **Need the middle:** two heaps.
4. **Small bounded positions and no "best" to extract:** you may not need a heap (1094).

Q: Which signals point to Heap / Priority Queue?
A: top k; closest; next best; median of a stream.

Q: What is the core idea of Heap / Priority Queue?
A: A heap gives the best item without sorting the rest. For the k largest keep a min-heap of size k, so its top is the weakest survivor.
