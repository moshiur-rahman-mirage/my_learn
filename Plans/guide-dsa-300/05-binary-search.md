@@ 1 | MODULE | Phase 5 — Binary Search | sorted; rotated; O(log n); smallest value that works | MUST
# Phase 5: Binary Search

All you need is a yes/no question whose answers switch once. Keep the switch inside the range and halve it.

**Signals:** sorted; rotated; O(log n); smallest value that works.

## The big analogy: guess my number

"I'm thinking of a number from 1 to 1,000." If every guess is answered "higher" or "lower", ten guesses are enough, because **each answer throws away half**. The array doesn't even need to be sorted. You only need a question whose answers look like `no no no yes yes yes`, and then you hunt for the switch.

| What you search | Picture | Meet it in |
|---|---|---|
| A position | opening a dictionary in the middle | 704, 35, 34 |
| An answer | trying shoe sizes | 875, 1011, 410 |
| A slope | climbing in fog | 162 |
| A broken order | a clock face, sorted except at one seam | 153, 33 |
| A cut | splitting two decks at once | 4 |

## Template
```java
int low = min, high = max;
while (low < high) {
    int mid = low + (high - low) / 2;
    if (works(mid)) high = mid; // mid may be the answer
    else low = mid + 1;
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Binary Search — Easy | 6 problems | MUST
# Binary Search: Easy

Patterns in this group: Classic search, Lower bound, Last feasible, Opposite pointers, Search on answer.

@@ 3 | TOPIC | 088 — 704. Binary Search | Easy · Classic search | MUST
# 088 — 704. Binary Search

**Problem 88 – Topic: Binary Search | Difficulty: Easy | Pattern: Classic search**

[Open on LeetCode](https://leetcode.com/problems/binary-search/)

## 1. Problem Statement
Return the index of `target` in a sorted array of distinct values, or -1, in O(log n).

### Examples
```text
Input:  nums = [-1,0,3,5,9,12], target = 9
Output: 4

Input:  nums = [-1,0,3,5,9,12], target = 2
Output: -1
```

### Constraints
```text
1 <= nums.length <= 10^4
-10^4 < nums[i], target < 10^4
All the integers in nums are unique.
nums is sorted in ascending order.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Finding a word in a dictionary
Open in the middle. If your word comes later, the whole first half is irrelevant. Keep halving the part still in your fingers.

| In the story | In the solution |
|---|---|
| part in your fingers | `low` to `high` |
| open page | `mid` |

### Pattern signal
**Classic search.** In sorted data one comparison rules out everything on one side.

### Thought process
1. **First idea:** Scan all values, in O(n).
2. **The insight:** In sorted data one comparison rules out everything on one side.

### Optimal approach
1. Cover the whole array.
2. Compare the middle.
3. Return on equality.
4. Keep the possible half.
5. Return minus one when empty.

### Dry run
```text
nums = [-1,0,3,5,9,12], target = 9
low  high  mid  value  action
0    5     2    3      go right
3    5     4    9      return 4
```

### Why it works
If the target exists it is always within low to high, since each discarded value is too small or too large.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int search(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }
}
```

### Code walkthrough
- `low <= high`: A single page still needs reading.
- `low + (high - low) / 2`: Safe from overflow.
- `mid + 1`: The open page was already rejected.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **`low < high` here.** The last candidate is skipped.
2. **`low = mid`.** It can loop forever.
3. **Returning low when absent.** That is the insert position.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5], 5` | `0` |
| Absent | `[1,3], 2` | `-1` |
| Last | `[1,2,3], 3` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[089 — 35. Search Insert Position]]: Return the boundary. The key idea there: "At least target" goes from false to true once. Find the first true.
- [[101 — 33. Search in Rotated Sorted Array]]: Find the sorted half first. The key idea there: At most one half holds the seam, so the other supports a range test.

### In a real interview
Say this: *"I keep the invariant that the target is inside the range, and each comparison halves it."*

## 5. Recall
Q: 704. Binary Search: which pattern, and what is the insight?
A: Classic search. In sorted data one comparison rules out everything on one side.

Q: 704. Binary Search: what is the analogy, and what does it stand for?
A: Finding a word in a dictionary: part in your fingers → `low` to `high`; open page → `mid`.

Q: 704. Binary Search: What does low to high represent?
A: Every position that could still hold the target.

Q: 704. Binary Search: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 089 — 35. Search Insert Position | Easy · Lower bound | NICE
# 089 — 35. Search Insert Position

**Problem 89 – Topic: Binary Search | Difficulty: Easy | Pattern: Lower bound**

[Open on LeetCode](https://leetcode.com/problems/search-insert-position/)

## 1. Problem Statement
Return the index of `target` in a sorted array of distinct values, or where it would be inserted, in O(log n).

### Examples
```text
Input:  nums = [1,3,5,6], target = 5
Output: 2

Input:  nums = [1,3,5,6], target = 2
Output: 1

Input:  nums = [1,3,5,6], target = 7
Output: 4
```

### Constraints
```text
1 <= nums.length <= 10^4
-10^4 <= nums[i] <= 10^4
nums contains distinct values sorted in ascending order.
-10^4 <= target <= 10^4
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Joining a line ordered by height
You stand in front of the first person who is at least your height. If nobody is, you go to the end, which is a valid place.

| In the story | In the solution |
|---|---|
| first at least your height | lower bound |
| the end | index n |

### Pattern signal
**Lower bound.** "At least target" goes from false to true once. Find the first true.

### Thought process
1. **First idea:** Scan for it, in O(n).
2. **The insight:** "At least target" goes from false to true once. Find the first true.

### Optimal approach
1. Search zero to n.
2. If the middle is smaller go right.
3. Otherwise keep it.
4. Return the meeting point.

### Dry run
```text
nums = [1,3,5,6], target = 2
low  high  mid  value  action
0    4     2    5      high = 2
0    2     1    3      high = 1
0    1     0    1      low = 1, return 1
```

### Why it works
Before low all values are smaller and from high all are at least target. Where they meet is the boundary.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int searchInsert(int[] nums, int target) {
        int low = 0, high = nums.length;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] < target) low = mid + 1;
            else high = mid;
        }
        return low;
    }
}
```

### Code walkthrough
- `high = nums.length`: The end of the line is a choice.
- `high = mid`: This person may be the first.
- `return low`: Works found or not.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **high = n - 1 with this template.** The end is unreachable.
2. **high = mid - 1.** The boundary can be lost.
3. **Returning -1.** A position is wanted.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Before | `[2,4], 1` | `0` |
| After | `[2,4], 5` | `2` |
| Present | `[2,4], 4` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[104 — 34. Find First and Last Position of Element in Sorted Array]]: Two lower bounds. The key idea there: Both edges are boundaries of monotonic questions.
- [[088 — 704. Binary Search]]: Exact only. The key idea there: In sorted data one comparison rules out everything on one side.

### In a real interview
Say this: *"It is a lower bound, the first value at least target, with n as a valid answer."*

## 5. Recall
Q: 35. Search Insert Position: which pattern, and what is the insight?
A: Lower bound. "At least target" goes from false to true once. Find the first true.

Q: 35. Search Insert Position: what is the analogy, and what does it stand for?
A: Joining a line ordered by height: first at least your height → lower bound; the end → index n.

Q: 35. Search Insert Position: What is a lower bound?
A: The first index whose value is at least the target.

Q: 35. Search Insert Position: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 090 — 441. Arranging Coins | Easy · Last feasible | NICE
# 090 — 441. Arranging Coins

**Problem 90 – Topic: Binary Search | Difficulty: Easy | Pattern: Last feasible**

[Open on LeetCode](https://leetcode.com/problems/arranging-coins/)

## 1. Problem Statement
Row i of a staircase needs i coins. With `n` coins return how many rows are complete.

### Examples
```text
Input:  n = 5
Output: 2

Input:  n = 8
Output: 3
```

### Constraints
```text
1 <= n <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The last step you can afford
Any number of steps has a known price. Guess a count: affordable means try higher, too dear means lower.

| In the story | In the solution |
|---|---|
| price of k steps | k(k+1)/2 |
| last affordable | last true |

### Pattern signal
**Last feasible.** The price only grows, so affordable is true and then false.

### Thought process
1. **First idea:** Subtract row by row, in O(√n).
2. **The insight:** The price only grows, so affordable is true and then false.

### Optimal approach
1. Search zero to n.
2. Take the upper middle.
3. Keep it if affordable.
4. Otherwise go below.

### Dry run
```text
n = 8
low  high  mid  price  action
0    8     4    10     high = 3
0    3     2    3      low = 2
2    3     3    6      low = 3, return 3
```

### Why it works
Low is always affordable and everything above high is not, so they meet at the last affordable count.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int arrangeCoins(int n) {
        int low = 0, high = n;
        while (low < high) {
            int mid = high - (high - low) / 2; // upper middle, no overflow
            if ((long) mid * (mid + 1L) / 2 <= n) low = mid;
            else high = mid - 1;
        }
        return low;
    }
}
```

### Code walkthrough
- `high - (high - low) / 2`: The upper middle, so low = mid progresses. Adding 1 to high - low would overflow at the int maximum.
- `(long)`: The price overflows an int.
- `low = mid`: Affordable may be the answer.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Lower middle.** It loops forever.
2. **Int product.** It overflows.
3. **Counting the partial row.** Only complete rows count.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `1` |
| Partial | `2` | `1` |
| Exact | `6` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[093 — 69. Sqrt(x)]]: The price is k squared. The key idea there: Fits is true up to the answer and false after it.
- [[098 — 875. Koko Eating Bananas]]: The first affordable instead. The key idea there: Hours never rise as speed rises, so feasibility switches once.

### In a real interview
Say this: *"Triangular numbers are monotonic, so I search the last one within n using long."*

## 5. Recall
Q: 441. Arranging Coins: which pattern, and what is the insight?
A: Last feasible. The price only grows, so affordable is true and then false.

Q: 441. Arranging Coins: what is the analogy, and what does it stand for?
A: The last step you can afford: price of k steps → k(k+1)/2; last affordable → last true.

Q: 441. Arranging Coins: Why the upper middle?
A: So low = mid always makes progress.

Q: 441. Arranging Coins: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | Practice — Review 15 | Re-solve without looking | MUST
# Review 15

Solve on a blank page. No notes, hints or solutions.

## 441. Arranging Coins
Count complete staircase rows from n coins.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/arranging-coins/)

## 84. Largest Rectangle in Histogram
Find the largest rectangle in a histogram.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/largest-rectangle-in-histogram/)

## 22. Generate Parentheses
Generate all valid strings of n bracket pairs.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/generate-parentheses/)

## 121. Best Time to Buy and Sell Stock
Maximize profit from one buy and a later sell.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 441 | | | | |
| 84 | | | | |
| 22 | | | | |
| 121 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[090 — 441. Arranging Coins]]
- [[087 — 84. Largest Rectangle in Histogram]]
- [[076 — 22. Generate Parentheses]]
- [[052 — 121. Best Time to Buy and Sell Stock]]

@@ 3 | TOPIC | 091 — 977. Squares of a Sorted Array | Easy · Opposite pointers | NICE
# 091 — 977. Squares of a Sorted Array

**Problem 91 – Topic: Binary Search | Difficulty: Easy | Pattern: Opposite pointers**

[Open on LeetCode](https://leetcode.com/problems/squares-of-a-sorted-array/)

## 1. Problem Statement
Return the squares of a sorted array in sorted order, in O(n).

### Examples
```text
Input:  nums = [-4,-1,0,3,10]
Output: [0,1,9,16,100]

Input:  nums = [-7,-3,2,3,11]
Output: [4,9,9,49,121]
```

### Constraints
```text
1 <= nums.length <= 10^4
-10^4 <= nums[i] <= 10^4
nums is sorted in non-decreasing order.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A valley between two hills
Squaring bends the line into a valley. The highest remaining point is always at one of the two rims, so take the taller rim and fill the result from the top.

| In the story | In the solution |
|---|---|
| rims | both ends |
| top | the back of the result |

### Pattern signal
**Opposite pointers.** Absolute values fall and then rise, so the biggest is at an end.

### Thought process
1. **First idea:** Square and sort, in O(n log n).
2. **The insight:** Absolute values fall and then rise, so the biggest is at an end.

### Optimal approach
1. Point at both ends.
2. Fill from the back.
3. Take the larger square.
4. Move that pointer.

### Dry run
```text
nums = [-4,-1,0,3,10]
left  right  chosen  result
-4    10     100     _ _ _ _ 100
-4    3      16      _ _ _ 16 100
-1    3      9       _ _ 9 16 100
-1    0      1       _ 1 9 16 100
0     0      0       0 1 9 16 100
```

### Why it works
The largest remaining square is always at an end, so each write is the correct next largest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] sortedSquares(int[] nums) {
        int left = 0, right = nums.length - 1;
        int[] result = new int[nums.length];
        for (int write = nums.length - 1; write >= 0; write--) {
            int a = nums[left] * nums[left], b = nums[right] * nums[right];
            if (a > b) {
                result[write] = a;
                left++;
            } else {
                result[write] = b;
                right--;
            }
        }
        return result;
    }
}
```

### Code walkthrough
- `write--`: Fill from the peak down.
- `a > b`: The taller rim.
- `new int[nums.length]`: Reading and writing would collide in place.

### Complexity
- **Time:** O(n)
- **Space:** O(n) for the output

### Common mistakes
1. **Filling from the front.** The smallest is inside.
2. **Comparing signed values.** -4 beats 3 after squaring.
3. **Writing over the input.** Unread values are lost.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[-2]` | `[4]` |
| Negatives | `[-3,-1]` | `[1,9]` |
| Equal | `[-2,2]` | `[4,4]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[040 — 88. Merge Sorted Array]]: Back-filling. The key idea there: The free space is at the back, and so are both maxima.
- [[063 — 658. Find K Closest Elements]]: Distance is a valley too. The key idea there: The answer is contiguous, and "should move right" changes only once as the start grows.

### In a real interview
Say this: *"The largest square is at an end, so two pointers fill backwards in linear time."*

## 5. Recall
Q: 977. Squares of a Sorted Array: which pattern, and what is the insight?
A: Opposite pointers. Absolute values fall and then rise, so the biggest is at an end.

Q: 977. Squares of a Sorted Array: what is the analogy, and what does it stand for?
A: A valley between two hills: rims → both ends; top → the back of the result.

Q: 977. Squares of a Sorted Array: Where is the largest remaining square?
A: At one of the two ends.

Q: 977. Squares of a Sorted Array: what are the time and space costs?
A: Time O(n). Space O(n) for the output.

@@ 3 | TOPIC | 092 — 367. Valid Perfect Square | Easy · Search on answer | NICE
# 092 — 367. Valid Perfect Square

**Problem 92 – Topic: Binary Search | Difficulty: Easy | Pattern: Search on answer**

[Open on LeetCode](https://leetcode.com/problems/valid-perfect-square/)

## 1. Problem Statement
Return true if `num` is the square of an integer, without a library square root.

### Examples
```text
Input:  num = 16
Output: true

Input:  num = 14
Output: false
```

### Constraints
```text
1 <= num <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Finding the side of a square carpet
You know the area. Guess a side, and the area it gives says larger or smaller.

| In the story | In the solution |
|---|---|
| side | the searched value |
| its area | mid × mid |

### Pattern signal
**Search on answer.** Squares of positive numbers only grow.

### Thought process
1. **First idea:** Try 1, 2, 3 and so on, in O(√n).
2. **The insight:** Squares of positive numbers only grow.

### Optimal approach
1. Search one to num.
2. Square in long.
3. Return on equality.
4. Otherwise discard a half.

### Dry run
```text
num = 16
low  high  mid  square  action
1    16    8    64      high = 7
1    7     4    16      true
```

### Why it works
A side with too large a square rules out all larger sides, and the reverse. An exact side is never discarded.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isPerfectSquare(int num) {
        int low = 1, high = num;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            long square = (long) mid * mid;
            if (square == num) return true;
            if (square < num) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }
}
```

### Code walkthrough
- `(long) mid * mid`: The cast comes before multiplying.
- `high = num`: A safe ceiling, including for 1.
- `return false`: No side fits.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **`(long)(mid * mid)`.** It already overflowed.
2. **high = num / 2.** It breaks for 1.
3. **Comparing doubles.** Rounding misleads.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `true` |
| Two | `2` | `false` |
| Maximum | `2147483647` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[093 — 69. Sqrt(x)]]: The floor. The key idea there: Fits is true up to the answer and false after it.
- [[090 — 441. Arranging Coins]]: Triangles. The key idea there: The price only grows, so affordable is true and then false.

### In a real interview
Say this: *"The square function is monotonic, so it is an exact binary search with long."*

## 5. Recall
Q: 367. Valid Perfect Square: which pattern, and what is the insight?
A: Search on answer. Squares of positive numbers only grow.

Q: 367. Valid Perfect Square: what is the analogy, and what does it stand for?
A: Finding the side of a square carpet: side → the searched value; its area → mid × mid.

Q: 367. Valid Perfect Square: Why multiply in long?
A: The middle squared can exceed an int.

Q: 367. Valid Perfect Square: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 093 — 69. Sqrt(x) | Easy · Last feasible | NICE
# 093 — 69. Sqrt(x)

**Problem 93 – Topic: Binary Search | Difficulty: Easy | Pattern: Last feasible**

[Open on LeetCode](https://leetcode.com/problems/sqrtx/)

## 1. Problem Statement
Return the square root of a non-negative `x` rounded down, without a built-in power or root.

### Examples
```text
Input:  x = 4
Output: 2

Input:  x = 8
Output: 2
```

### Constraints
```text
0 <= x <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The biggest square rug in a room
The rug's area cannot exceed x. Try a side: if it fits try bigger, else smaller. Keep the biggest that fitted.

| In the story | In the solution |
|---|---|
| fits | k × k ≤ x |
| biggest | last true |

### Pattern signal
**Last feasible.** Fits is true up to the answer and false after it.

### Thought process
1. **First idea:** Count up until the square exceeds x, in O(√x).
2. **The insight:** Fits is true up to the answer and false after it.

### Optimal approach
1. Search zero to x.
2. Take the upper middle.
3. Keep fitting values.
4. Discard others.

### Dry run
```text
x = 8
low  high  mid  square  action
0    8     4    16      high = 3
0    3     2    4       low = 2
2    3     3    9       high = 2, return 2
```

### Why it works
Low always fits and values above high never do.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int mySqrt(int x) {
        int low = 0, high = x;
        while (low < high) {
            int mid = low + (high - low) / 2 + (high - low) % 2; // upper middle
            if ((long) mid * mid <= x) low = mid;
            else high = mid - 1;
        }
        return low;
    }
}
```

### Code walkthrough
- `(high - low) % 2`: Rounds up without overflowing at the int maximum.
- `<= x`: The rug may touch the walls.
- `return low`: The largest fit.

### Complexity
- **Time:** O(log x)
- **Space:** O(1)

### Common mistakes
1. **`high - low + 1`.** It overflows at the maximum.
2. **Rounding up the answer.** The floor is wanted.
3. **Excluding zero.** sqrt(0) is 0.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `0` | `0` |
| Exact | `9` | `3` |
| Maximum | `2147483647` | `46340` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[092 — 367. Valid Perfect Square]]: Equality. The key idea there: Squares of positive numbers only grow.
- [[295 — 50. Pow(x, n)]]: Halving the exponent. The key idea there: Repeated squaring gives x to 1, 2, 4, 8, and any exponent is a sum of some of those.

### In a real interview
Say this: *"I search the last k whose square is at most x, with overflow-safe arithmetic."*

## 5. Recall
Q: 69. Sqrt(x): which pattern, and what is the insight?
A: Last feasible. Fits is true up to the answer and false after it.

Q: 69. Sqrt(x): what is the analogy, and what does it stand for?
A: The biggest square rug in a room: fits → k × k ≤ x; biggest → last true.

Q: 69. Sqrt(x): Which value is searched?
A: The largest integer whose square does not exceed x.

Q: 69. Sqrt(x): what are the time and space costs?
A: Time O(log x). Space O(1).

@@ 2 | MODULE | Binary Search — Medium | 11 problems | MUST
# Binary Search: Medium

Patterns in this group: Parity search, First feasible, Follow the slope, 2D search, Feasibility greedy, Rotation search, One sorted half, Upper bound by key, Two bounds.

@@ 3 | TOPIC | 094 — 540. Single Element in a Sorted Array | Medium · Parity search | SHOULD
# 094 — 540. Single Element in a Sorted Array

**Problem 94 – Topic: Binary Search | Difficulty: Medium | Pattern: Parity search**

[Open on LeetCode](https://leetcode.com/problems/single-element-in-a-sorted-array/)

## 1. Problem Statement
In a sorted array every value appears twice except one. Find it in O(log n) time and O(1) space.

### Examples
```text
Input:  nums = [1,1,2,3,3,4,4,8,8]
Output: 2

Input:  nums = [3,3,7,7,10,11,11]
Output: 10
```

### Constraints
```text
1 <= nums.length <= 10^5
0 <= nums[i] <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Couples in a cinema row and one single
Before the single, couples sit on seats even-odd. After, everyone is shifted. Check the couple starting at an even seat: intact means the single is further right.

| In the story | In the solution |
|---|---|
| couple's first seat | an even `mid` |
| intact | `nums[mid] == nums[mid + 1]` |

### Pattern signal
**Parity search.** The single flips the parity of every later pair, so "pair intact" switches once.

### Thought process
1. **First idea:** XOR everything, in O(n).
2. **The insight:** The single flips the parity of every later pair, so "pair intact" switches once.

### Optimal approach
1. Pick the middle and make it even.
2. If it equals the next skip both.
3. Otherwise keep it.
4. Return the meeting point.

### Dry run
```text
nums = [1,1,2,3,3,4,4,8,8]
low  high  even mid  pair  action
0    8     4         3,4   high = 4
0    4     2         2,3   high = 2
0    2     0         1,1   low = 2, return 2
```

### Why it works
Low and high stay even and always surround the single, since an intact pair proves it lies to the right.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int singleNonDuplicate(int[] nums) {
        int low = 0, high = nums.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (mid % 2 == 1) mid--;
            if (nums[mid] == nums[mid + 1]) low = mid + 2;
            else high = mid;
        }
        return nums[low];
    }
}
```

### Code walkthrough
- `mid--`: Stand at a couple's first seat.
- `low = mid + 2`: Skip the couple.
- `high = mid`: The single may be here.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Comparing with both neighbors only.** That gives no direction.
2. **low = mid + 1.** Parity is lost.
3. **XOR.** It isn't logarithmic.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5]` | `5` |
| First | `[1,2,2]` | `1` |
| Last | `[1,1,2]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[270 — 136. Single Number]]: Unsorted, so XOR. The key idea there: XOR is that flip: x XOR x is 0, and order doesn't matter.
- [[096 — 162. Find Peak Element]]: A neighbor gives direction. The key idea there: You don't need order, only a guarantee of which half contains some peak.

### In a real interview
Say this: *"Pair alignment flips at the single, so I search that flip."*

## 5. Recall
Q: 540. Single Element in a Sorted Array: which pattern, and what is the insight?
A: Parity search. The single flips the parity of every later pair, so "pair intact" switches once.

Q: 540. Single Element in a Sorted Array: what is the analogy, and what does it stand for?
A: Couples in a cinema row and one single: couple's first seat → an even `mid`; intact → `nums[mid] == nums[mid + 1]`.

Q: 540. Single Element in a Sorted Array: What does an intact even-starting pair prove?
A: The single is to its right.

Q: 540. Single Element in a Sorted Array: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 095 — 1011. Capacity To Ship Packages Within D Days | Medium · First feasible | SHOULD
# 095 — 1011. Capacity To Ship Packages Within D Days

**Problem 95 – Topic: Binary Search | Difficulty: Medium | Pattern: First feasible**

[Open on LeetCode](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/)

## 1. Problem Statement
Packages must ship in the given order within `days` days. Return the least ship capacity that achieves it.

### Examples
```text
Input:  weights = [1,2,3,4,5,6,7,8,9,10], days = 5
Output: 15

Input:  weights = [3,2,2,4,1,4], days = 3
Output: 6

Input:  weights = [1,2,3,1,1], days = 4
Output: 3
```

### Constraints
```text
1 <= days <= weights.length <= 5 * 10^4
1 <= weights[i] <= 500
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Trying shoe sizes
You can't compute the size, but you can test one: "can I walk in this?" If a ship is big enough, every bigger one is too. Try the middle size.

| In the story | In the solution |
|---|---|
| trying on | simulate the loading |
| size range | heaviest package to total |

### Pattern signal
**First feasible.** Feasibility is monotonic and testing one capacity is a linear greedy.

### Thought process
1. **First idea:** Try each capacity upward, O(n · sum).
2. **The insight:** Feasibility is monotonic and testing one capacity is a linear greedy.

### Optimal approach
1. Bound by the heaviest and the total.
2. Load greedily at the middle.
3. Shrink if it meets the days.
4. Otherwise grow.

### Dry run
```text
weights = [3,2,2,4,1,4], days = 3
low  high  mid  days needed  action
4    16    10   2            high = 10
4    10    7    3            high = 7
4    7     5    4            low = 6
6    7     6    3            high = 6, return 6
```

### Why it works
Filling each day as far as possible uses the fewest days for a capacity, and more capacity never needs more.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int shipWithinDays(int[] weights, int days) {
        int low = 0, high = 0;
        for (int w : weights) {
            low = Math.max(low, w);
            high += w;
        }
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (daysNeeded(weights, mid) <= days) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    private int daysNeeded(int[] weights, int capacity) {
        int days = 1, load = 0;
        for (int w : weights) {
            if (load + w > capacity) {
                days++;
                load = 0;
            }
            load += w;
        }
        return days;
    }
}
```

### Code walkthrough
- `Math.max(low, w)`: The heaviest must fit.
- `high = mid`: This size fits, try smaller.
- `load + w > capacity`: Sail and begin a new day.

### Complexity
- **Time:** O(n log sum)
- **Space:** O(1)

### Common mistakes
1. **Sorting the weights.** The order is fixed.
2. **low = 1.** A package would never fit.
3. **Total divided by days.** Packages can't split.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One day | `[1,2], 1` | `3` |
| Daily | `[1,2], 2` | `2` |
| One | `[5], 1` | `5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[105 — 410. Split Array Largest Sum]]: The same problem. The key idea there: Choosing cuts is hard but checking a limit is easy, and monotonic.
- [[098 — 875. Koko Eating Bananas]]: Another test. The key idea there: Hours never rise as speed rises, so feasibility switches once.

### In a real interview
Say this: *"I search the answer with a greedy monotonic feasibility check."*

## 5. Recall
Q: 1011. Capacity To Ship Packages Within D Days: which pattern, and what is the insight?
A: First feasible. Feasibility is monotonic and testing one capacity is a linear greedy.

Q: 1011. Capacity To Ship Packages Within D Days: what is the analogy, and what does it stand for?
A: Trying shoe sizes: trying on → simulate the loading; size range → heaviest package to total.

Q: 1011. Capacity To Ship Packages Within D Days: What are the capacity bounds?
A: The heaviest package and the total weight.

Q: 1011. Capacity To Ship Packages Within D Days: what are the time and space costs?
A: Time O(n log sum). Space O(1).

@@ 3 | TOPIC | 096 — 162. Find Peak Element | Medium · Follow the slope | SHOULD
# 096 — 162. Find Peak Element

**Problem 96 – Topic: Binary Search | Difficulty: Medium | Pattern: Follow the slope**

[Open on LeetCode](https://leetcode.com/problems/find-peak-element/)

## 1. Problem Statement
A peak is larger than its neighbors, with both outsides counting as minus infinity. Neighbors differ. Return any peak's index in O(log n).

### Examples
```text
Input:  nums = [1,2,3,1]
Output: 2

Input:  nums = [1,2,1,3,5,6,4]
Output: 5
```

### Constraints
```text
1 <= nums.length <= 1000
-2^31 <= nums[i] <= 2^31 - 1
nums[i] != nums[i + 1] for all valid i.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Climbing in thick fog
You see one step. If the ground rises to the right, a summit must lie there, since the world drops to nothing at the edge. Otherwise one is here or to the left.

| In the story | In the solution |
|---|---|
| one step | comparing mid with mid + 1 |
| the cliff | minus infinity |

### Pattern signal
**Follow the slope.** You don't need order, only a guarantee of which half contains some peak.

### Thought process
1. **First idea:** Scan for the first drop, in O(n).
2. **The insight:** You don't need order, only a guarantee of which half contains some peak.

### Optimal approach
1. Compare the middle with the next.
2. Go right when rising.
3. Keep the middle when falling.
4. Stop when one remains.

### Dry run
```text
nums = [1,2,3,1]
low  high  mid  slope  action
0    3     1    up     low = 2
2    3     2    down   high = 2, return 2
```

### Why it works
The range always has rising ground at its left edge and falling at its right, so it contains a peak.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int findPeakElement(int[] nums) {
        int low = 0, high = nums.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] < nums[mid + 1]) low = mid + 1;
            else high = mid;
        }
        return low;
    }
}
```

### Code walkthrough
- `nums[mid + 1]`: Safe because mid is below high.
- `low = mid + 1`: Uphill.
- `high = mid`: Maybe the summit.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Seeking the maximum.** Any peak is fine.
2. **high = mid - 1.** The peak can be lost.
3. **A fake Integer.MIN_VALUE border.** It can equal a real value.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `0` |
| Rising | `[1,2]` | `1` |
| Falling | `[2,1]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **852. Peak Index in a Mountain**: Only one summit.
- [[100 — 153. Find Minimum in Rotated Sorted Array]]: Find the valley. The key idea there: "At most the last value" is false before the seam and true from it.

### In a real interview
Say this: *"Moving uphill guarantees a peak, so slope direction replaces sorted order."*

## 5. Recall
Q: 162. Find Peak Element: which pattern, and what is the insight?
A: Follow the slope. You don't need order, only a guarantee of which half contains some peak.

Q: 162. Find Peak Element: what is the analogy, and what does it stand for?
A: Climbing in thick fog: one step → comparing mid with mid + 1; the cliff → minus infinity.

Q: 162. Find Peak Element: Why go right on a rising slope?
A: A peak must exist there before the edge.

Q: 162. Find Peak Element: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | Practice — Review 16 | Re-solve without looking | MUST
# Review 16

Solve on a blank page. No notes, hints or solutions.

## 162. Find Peak Element
Find any index greater than both neighbors.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-peak-element/)

## 69. Sqrt(x)
Compute the floor of a square root.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/sqrtx/)

## 394. Decode String
Expand strings like 3[a2[c]].

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/decode-string/)

## 567. Permutation in String
Decide whether s2 contains a permutation of s1.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/permutation-in-string/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 162 | | | | |
| 69 | | | | |
| 394 | | | | |
| 567 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[096 — 162. Find Peak Element]]
- [[093 — 69. Sqrt(x)]]
- [[082 — 394. Decode String]]
- [[058 — 567. Permutation in String]]

@@ 3 | TOPIC | 097 — 74. Search a 2D Matrix | Medium · 2D search | MUST
# 097 — 74. Search a 2D Matrix

**Problem 97 – Topic: Binary Search | Difficulty: Medium | Pattern: 2D search**

[Open on LeetCode](https://leetcode.com/problems/search-a-2d-matrix/)

## 1. Problem Statement
Each row is sorted and starts above the previous row's end. Search for `target` in O(log(mn)).

### Examples
```text
Input:  matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
Output: true

Input:  matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 13
Output: false
```

### Constraints
```text
m == matrix.length
n == matrix[i].length
1 <= m, n <= 100
-10^4 <= matrix[i][j], target <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A book printed in rows
The lines are just one long sentence wrapped. Number the words 0 to mn-1 and turn any number into line and column.

| In the story | In the solution |
|---|---|
| wrapped sentence | a virtual sorted array |
| line and column | mid / n and mid % n |

### Pattern signal
**2D search.** The row rule makes the whole matrix one sorted list, with no copying needed.

### Thought process
1. **First idea:** Scan every cell, in O(mn).
2. **The insight:** The row rule makes the whole matrix one sorted list, with no copying needed.

### Optimal approach
1. Search zero to mn minus one.
2. Convert the middle.
3. Compare.
4. Keep a half.

### Dry run
```text
matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
low  high  mid  cell   value  action
0    11    5    (1,1)  11     left
0    4     2    (0,2)  5      left
0    1     0    (0,0)  1      right
1    1     1    (0,1)  3      true
```

### Why it works
The mapping preserves sorted order, so ordinary binary search applies.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int cols = matrix[0].length;
        int low = 0, high = matrix.length * cols - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = matrix[mid / cols][mid % cols];
            if (value == target) return true;
            if (value < target) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }
}
```

### Code walkthrough
- `matrix.length * cols - 1`: The last word's number.
- `mid / cols`: Whole lines before it.
- `mid % cols`: The place on its line.

### Complexity
- **Time:** O(log(mn))
- **Space:** O(1)

### Common mistakes
1. **Dividing by rows.** Use columns.
2. **Flattening.** It costs O(mn).
3. **Using it for problem 240.** Its rows overlap.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [1] ], 1` | `true` |
| Between rows | `[ [1],[3] ], 2` | `false` |
| Last | `[ [1,2] ], 2` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **240. Search 2D Matrix II**: Walk from the top-right.
- [[088 — 704. Binary Search]]: The unwrapped sentence. The key idea there: In sorted data one comparison rules out everything on one side.

### In a real interview
Say this: *"I treat it as a flat sorted array and convert indices."*

## 5. Recall
Q: 74. Search a 2D Matrix: which pattern, and what is the insight?
A: 2D search. The row rule makes the whole matrix one sorted list, with no copying needed.

Q: 74. Search a 2D Matrix: what is the analogy, and what does it stand for?
A: A book printed in rows: wrapped sentence → a virtual sorted array; line and column → mid / n and mid % n.

Q: 74. Search a 2D Matrix: How does a flat index become a cell?
A: Divide by columns for the row, the remainder is the column.

Q: 74. Search a 2D Matrix: what are the time and space costs?
A: Time O(log(mn)). Space O(1).

@@ 3 | TOPIC | 098 — 875. Koko Eating Bananas | Medium · First feasible | MUST
# 098 — 875. Koko Eating Bananas

**Problem 98 – Topic: Binary Search | Difficulty: Medium | Pattern: First feasible**

[Open on LeetCode](https://leetcode.com/problems/koko-eating-bananas/)

## 1. Problem Statement
Koko eats up to k bananas an hour, from only one pile in that hour. Return the least k that finishes all piles within `h` hours.

### Examples
```text
Input:  piles = [3,6,7,11], h = 8
Output: 4

Input:  piles = [30,11,23,4,20], h = 5
Output: 30

Input:  piles = [30,11,23,4,20], h = 6
Output: 23
```

### Constraints
```text
1 <= piles.length <= 10^4
piles.length <= h <= 10^9
1 <= piles[i] <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Setting a treadmill
Too slow misses the deadline and anything faster than a working speed works. Don't try every notch, test the middle.

| In the story | In the solution |
|---|---|
| notch | candidate k |
| test run | summed rounded-up hours |

### Pattern signal
**First feasible.** Hours never rise as speed rises, so feasibility switches once.

### Thought process
1. **First idea:** Try each speed, O(n · max).
2. **The insight:** Hours never rise as speed rises, so feasibility switches once.

### Optimal approach
1. Search one to the largest pile.
2. Add hours rounded up.
3. Keep a working speed.
4. Otherwise go faster.

### Dry run
```text
piles = [3,6,7,11], h = 8
low  high  speed  hours  action
1    11    6      6      high = 6
1    6     3      10     low = 4
4    6     5      8      high = 5
4    5     4      8      high = 4, return 4
```

### Why it works
Everything below low fails and high always works, so they meet at the first working speed.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        int low = 1, high = 1;
        for (int pile : piles) high = Math.max(high, pile);
        while (low < high) {
            int speed = low + (high - low) / 2;
            long hours = 0;
            for (int pile : piles) hours += (pile - 1) / speed + 1;
            if (hours <= h) high = speed;
            else low = speed + 1;
        }
        return low;
    }
}
```

### Code walkthrough
- `(pile - 1) / speed + 1`: Rounds up without overflow.
- `long hours`: Speed 1 can need 10^13 hours.
- `high = speed`: Keep what works.

### Complexity
- **Time:** O(n log max)
- **Space:** O(1)

### Common mistakes
1. **Rounding down.** Part hours cost whole ones.
2. **Int hours.** It overflows.
3. **Dividing the total.** Piles can't share an hour.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[10], 5` | `2` |
| Tight | `[3,7], 2` | `7` |
| Plenty | `[3], 9` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[095 — 1011. Capacity To Ship Packages Within D Days]]: Greedy test. The key idea there: Feasibility is monotonic and testing one capacity is a linear greedy.
- [[105 — 410. Split Array Largest Sum]]: Again. The key idea there: Choosing cuts is hard but checking a limit is easy, and monotonic.

### In a real interview
Say this: *"Hours are monotonic in speed, so I search the first feasible speed."*

## 5. Recall
Q: 875. Koko Eating Bananas: which pattern, and what is the insight?
A: First feasible. Hours never rise as speed rises, so feasibility switches once.

Q: 875. Koko Eating Bananas: what is the analogy, and what does it stand for?
A: Setting a treadmill: notch → candidate k; test run → summed rounded-up hours.

Q: 875. Koko Eating Bananas: How many hours does a pile take?
A: Pile divided by speed, rounded up.

Q: 875. Koko Eating Bananas: what are the time and space costs?
A: Time O(n log max). Space O(1).

@@ 3 | TOPIC | 099 — 2616. Minimize the Maximum Difference of Pairs | Medium · Feasibility greedy | SHOULD
# 099 — 2616. Minimize the Maximum Difference of Pairs

**Problem 99 – Topic: Binary Search | Difficulty: Medium | Pattern: Feasibility greedy**

[Open on LeetCode](https://leetcode.com/problems/minimize-the-maximum-difference-of-pairs/)

## 1. Problem Statement
Form `p` pairs using each index at most once so the largest absolute difference is as small as possible. Return it, or 0 for no pairs.

### Examples
```text
Input:  nums = [10,1,2,7,1,3], p = 2
Output: 1

Input:  nums = [4,2,1,2], p = 1
Output: 0
```

### Constraints
```text
1 <= nums.length <= 10^5
0 <= nums[i] <= 10^9
0 <= p <= (nums.length)/2
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A dance teacher's height rule
Line people up by height. For a limit D walk along: two neighbors within D pair up and leave, else the first sits out. Then shrink D while p couples still form.

| In the story | In the solution |
|---|---|
| limit | the searched answer |
| walk | the greedy count |

### Pattern signal
**Feasibility greedy.** Sorted neighbors are the only useful pairs, and for a given limit pairing the earliest is safe.

### Thought process
1. **First idea:** Try all pairings.
2. **The insight:** Sorted neighbors are the only useful pairs, and for a given limit pairing the earliest is safe.

### Optimal approach
1. Sort.
2. Search the limit.
3. Greedily count disjoint neighbors.
4. Keep a limit reaching p.

### Dry run
```text
sorted = [1,1,2,3,7,10], p = 2
low  high  limit  pairs           action
0    9     4      (1,1),(2,3),(7,10)  high = 4
0    4     2      2               high = 2
0    2     1      2               high = 1
0    1     0      1               low = 1, return 1
```

### Why it works
If the first neighbors qualify some optimal plan can use them. Higher limits never allow fewer pairs.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minimizeMax(int[] nums, int p) {
        Arrays.sort(nums);
        int low = 0, high = nums[nums.length - 1] - nums[0];
        while (low < high) {
            int limit = low + (high - low) / 2;
            if (pairs(nums, limit) >= p) high = limit;
            else low = limit + 1;
        }
        return low;
    }

    private int pairs(int[] nums, int limit) {
        int count = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] - nums[i - 1] <= limit) {
                count++;
                i++; // both have left
            }
        }
        return count;
    }
}
```

### Code walkthrough
- `Arrays.sort`: The height line.
- `i++;`: Nobody dances twice.
- `>= p`: More couples are fine.

### Complexity
- **Time:** O(n log n + n log range)
- **Space:** O(log n) sort stack

### Common mistakes
1. **Taking the p smallest differences.** They overlap.
2. **Not skipping.** An index is reused.
3. **Searching for exactly p.** At least is the test.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `[1,5], 0` | `0` |
| One | `[1,5], 1` | `4` |
| Shared middle | `[1,2,3,4], 2` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[059 — 1838. Frequency of the Most Frequent Element]]: Sorting makes groups adjacent. The key idea there: The best values to raise are the closest smaller ones, which are contiguous after sorting.
- [[105 — 410. Split Array Largest Sum]]: Minimize a maximum. The key idea there: Choosing cuts is hard but checking a limit is easy, and monotonic.

### In a real interview
Say this: *"Minimize the maximum means search the answer. The check is greedy on sorted neighbors."*

## 5. Recall
Q: 2616. Minimize the Maximum Difference of Pairs: which pattern, and what is the insight?
A: Feasibility greedy. Sorted neighbors are the only useful pairs, and for a given limit pairing the earliest is safe.

Q: 2616. Minimize the Maximum Difference of Pairs: what is the analogy, and what does it stand for?
A: A dance teacher's height rule: limit → the searched answer; walk → the greedy count.

Q: 2616. Minimize the Maximum Difference of Pairs: How is a limit tested?
A: Greedily count non-overlapping sorted neighbors within it.

Q: 2616. Minimize the Maximum Difference of Pairs: what are the time and space costs?
A: Time O(n log n + n log range). Space O(log n) sort stack.

@@ 3 | TOPIC | 100 — 153. Find Minimum in Rotated Sorted Array | Medium · Rotation search | MUST
# 100 — 153. Find Minimum in Rotated Sorted Array

**Problem 100 – Topic: Binary Search | Difficulty: Medium | Pattern: Rotation search**

[Open on LeetCode](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)

## 1. Problem Statement
A sorted array of unique values was rotated. Return its minimum in O(log n).

### Examples
```text
Input:  nums = [3,4,5,1,2]
Output: 1

Input:  nums = [4,5,6,7,0,1,2]
Output: 0

Input:  nums = [11,13,15,17]
Output: 11
```

### Constraints
```text
n == nums.length
1 <= n <= 5000
-5000 <= nums[i] <= 5000
All the integers of nums are unique.
nums is sorted and rotated between 1 and n times.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A clock face and its seam
Around a clock numbers rise except from 12 to 1. Stand anywhere: if you are bigger than the last value, the seam is ahead.

| In the story | In the solution |
|---|---|
| seam | the minimum's position |
| last value | `nums[high]` |

### Pattern signal
**Rotation search.** "At most the last value" is false before the seam and true from it.

### Thought process
1. **First idea:** Scan, in O(n).
2. **The insight:** "At most the last value" is false before the seam and true from it.

### Optimal approach
1. Compare the middle with the right end.
2. Go right if larger.
3. Otherwise keep it.
4. Return the meeting value.

### Dry run
```text
nums = [3,4,5,1,2]
low  high  mid  value  right end  action
0    4     2    5      2          low = 3
3    4     3    1      2          high = 3, return 1
```

### Why it works
A middle above the right end cannot be the minimum and the drop follows it. Otherwise that side is sorted, minimum at or before the middle.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int findMin(int[] nums) {
        int low = 0, high = nums.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] > nums[high]) low = mid + 1;
            else high = mid;
        }
        return nums[low];
    }
}
```

### Code walkthrough
- `nums[high]`: The right end works even unrotated.
- `low = mid + 1`: The seam is ahead.
- `high = mid`: This may be it.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Comparing with the left.** The unrotated case fails.
2. **high = mid - 1.** The minimum is lost.
3. **Using it with duplicates.** Equality is ambiguous.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `1` |
| Unrotated | `[1,2,3]` | `1` |
| Two | `[2,1]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[101 — 33. Search in Rotated Sorted Array]]: Any value. The key idea there: At most one half holds the seam, so the other supports a range test.
- **154. With Duplicates**: Shrink on equality.

### In a real interview
Say this: *"The comparison with the right end shows the side of the pivot."*

## 5. Recall
Q: 153. Find Minimum in Rotated Sorted Array: which pattern, and what is the insight?
A: Rotation search. "At most the last value" is false before the seam and true from it.

Q: 153. Find Minimum in Rotated Sorted Array: what is the analogy, and what does it stand for?
A: A clock face and its seam: seam → the minimum's position; last value → `nums[high]`.

Q: 153. Find Minimum in Rotated Sorted Array: When is the minimum right of mid?
A: When the middle exceeds the right end.

Q: 153. Find Minimum in Rotated Sorted Array: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 101 — 33. Search in Rotated Sorted Array | Medium · One sorted half | MUST
# 101 — 33. Search in Rotated Sorted Array

**Problem 101 – Topic: Binary Search | Difficulty: Medium | Pattern: One sorted half**

[Open on LeetCode](https://leetcode.com/problems/search-in-rotated-sorted-array/)

## 1. Problem Statement
Find `target`'s index in a rotated sorted array of unique values, or -1, in O(log n).

### Examples
```text
Input:  nums = [4,5,6,7,0,1,2], target = 0
Output: 4

Input:  nums = [4,5,6,7,0,1,2], target = 3
Output: -1

Input:  nums = [1], target = 0
Output: -1
```

### Constraints
```text
1 <= nums.length <= 5000
-10^4 <= nums[i] <= 10^4
All values of nums are unique.
nums is an ascending array that is possibly rotated.
-10^4 <= target <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A torn phone book taped in the wrong order
Open anywhere and one side is guaranteed to be in clean order. It can answer "would my name be here?" with certainty. If not, search the messy side.

| In the story | In the solution |
|---|---|
| clean side | the sorted half |
| certain answer | a range test |

### Pattern signal
**One sorted half.** At most one half holds the seam, so the other supports a range test.

### Thought process
1. **First idea:** Scan, in O(n).
2. **The insight:** At most one half holds the seam, so the other supports a range test.

### Optimal approach
1. Compare the middle.
2. Find the sorted half.
3. Keep it if the target is in its range.
4. Otherwise keep the other.

### Dry run
```text
nums = [4,5,6,7,0,1,2], target = 0
low  high  mid  sorted side     action
0    6     3    4..7, lacks 0   low = 4
4    6     5    0..1, holds 0   high = 4
4    4     4    found           return 4
```

### Why it works
The sorted half's range test is exact, so the target is never discarded.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int search(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) return mid;
            if (nums[low] <= nums[mid]) { // left is clean
                if (nums[low] <= target && target < nums[mid]) high = mid - 1;
                else low = mid + 1;
            } else { // right is clean
                if (nums[mid] < target && target <= nums[high]) low = mid + 1;
                else high = mid - 1;
            }
        }
        return -1;
    }
}
```

### Code walkthrough
- `nums[low] <= nums[mid]`: Equality covers low equal to mid.
- `target < nums[mid]`: Inside the clean range.
- `target <= nums[high]`: The end counts.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **`<` for the sorted test.** Two values break it.
2. **Comparing only with mid.** Direction is unknown.
3. **Excluding endpoints.** Targets there vanish.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two | `[3,1], 1` | `1` |
| Absent | `[1], 0` | `-1` |
| Unrotated | `[1,2,3], 3` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[102 — 81. Search in Rotated Sorted Array II]]: Shrink ties. The key idea there: Duplicates only break the sorted-side test when all three probes are equal, and then those ends are known not to be the target.
- [[100 — 153. Find Minimum in Rotated Sorted Array]]: Find the tear. The key idea there: "At most the last value" is false before the seam and true from it.

### In a real interview
Say this: *"One half is always sorted, and its range test decides the side."*

## 5. Recall
Q: 33. Search in Rotated Sorted Array: which pattern, and what is the insight?
A: One sorted half. At most one half holds the seam, so the other supports a range test.

Q: 33. Search in Rotated Sorted Array: what is the analogy, and what does it stand for?
A: A torn phone book taped in the wrong order: clean side → the sorted half; certain answer → a range test.

Q: 33. Search in Rotated Sorted Array: Which half gets the range test?
A: The sorted one.

Q: 33. Search in Rotated Sorted Array: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 102 — 81. Search in Rotated Sorted Array II | Medium · One sorted half | SHOULD
# 102 — 81. Search in Rotated Sorted Array II

**Problem 102 – Topic: Binary Search | Difficulty: Medium | Pattern: One sorted half**

[Open on LeetCode](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/)

## 1. Problem Statement
Decide whether `target` is in a rotated sorted array that may contain duplicates.

### Examples
```text
Input:  nums = [2,5,6,0,0,1,2], target = 0
Output: true

Input:  nums = [2,5,6,0,0,1,2], target = 3
Output: false
```

### Constraints
```text
1 <= nums.length <= 5000
-10^4 <= nums[i] <= 10^4
nums is guaranteed to be rotated at some pivot.
-10^4 <= target <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A torn book with identical pages
Usually one side is visibly in order. If the first, middle and last page look the same you can't tell, so peel one useless page off each end and look again.

| In the story | In the solution |
|---|---|
| identical pages | equal low, mid, high |
| peel | shrink both bounds |

### Pattern signal
**One sorted half.** Duplicates only break the sorted-side test when all three probes are equal, and then those ends are known not to be the target.

### Thought process
1. **First idea:** Scan, in O(n).
2. **The insight:** Duplicates only break the sorted-side test when all three probes are equal, and then those ends are known not to be the target.

### Optimal approach
1. Compare the middle.
2. Shrink both ends on a triple tie.
3. Otherwise range-test the sorted half.

### Dry run
```text
nums = [1,0,1,1,1], target = 0
low  high  mid  case            action
0    4     2    1,1,1 tie       low 1, high 3
1    3     2    left 0..1       high = 1
1    1     1    found           true
```

### Why it works
Peeled values equal a middle already known to differ from the target. Other steps are as in problem 33.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean search(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) return true;
            if (nums[low] == nums[mid] && nums[mid] == nums[high]) {
                low++;
                high--;
            } else if (nums[low] <= nums[mid]) {
                if (nums[low] <= target && target < nums[mid]) high = mid - 1;
                else low = mid + 1;
            } else {
                if (nums[mid] < target && target <= nums[high]) low = mid + 1;
                else high = mid - 1;
            }
        }
        return false;
    }
}
```

### Code walkthrough
- `nums[mid] == nums[high]`: The unreadable case.
- `low++`: Safe to peel.
- `return true`: Only existence is asked.

### Complexity
- **Time:** O(log n) typically, O(n) with many ties
- **Space:** O(1)

### Common mistakes
1. **Promising O(log n).** The worst case is linear.
2. **Reusing 33 unchanged.** The hidden value is missed.
3. **Peeling before the match test.** The target could be removed.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Hidden | `[1,1,0,1], 0` | `true` |
| Same | `[1,1], 2` | `false` |
| One | `[1], 1` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[101 — 33. Search in Rotated Sorted Array]]: No peeling. The key idea there: At most one half holds the seam, so the other supports a range test.
- **154. Minimum with Duplicates**: Peel the right.

### In a real interview
Say this: *"It is problem 33 plus a tie case, which costs linear worst time."*

## 5. Recall
Q: 81. Search in Rotated Sorted Array II: which pattern, and what is the insight?
A: One sorted half. Duplicates only break the sorted-side test when all three probes are equal, and then those ends are known not to be the target.

Q: 81. Search in Rotated Sorted Array II: what is the analogy, and what does it stand for?
A: A torn book with identical pages: identical pages → equal low, mid, high; peel → shrink both bounds.

Q: 81. Search in Rotated Sorted Array II: When is the sorted side unknown?
A: When low, middle and high values are equal.

Q: 81. Search in Rotated Sorted Array II: what are the time and space costs?
A: Time O(log n) typically, O(n) with many ties. Space O(1).

@@ 3 | TOPIC | Practice — Review 17 | Re-solve without looking | MUST
# Review 17

Solve on a blank page. No notes, hints or solutions.

## 81. Search in Rotated Sorted Array II
Search a rotated sorted array with duplicates.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/)

## 2616. Minimize the Maximum Difference of Pairs
Form p pairs minimizing the largest difference.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/minimize-the-maximum-difference-of-pairs/)

## 704. Binary Search
Find a target in a sorted array.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/binary-search/)

## 1658. Minimum Operations to Reduce X to Zero
Remove fewest end values so they sum to x.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/minimum-operations-to-reduce-x-to-zero/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 81 | | | | |
| 2616 | | | | |
| 704 | | | | |
| 1658 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[102 — 81. Search in Rotated Sorted Array II]]
- [[099 — 2616. Minimize the Maximum Difference of Pairs]]
- [[088 — 704. Binary Search]]
- [[064 — 1658. Minimum Operations to Reduce X to Zero]]

@@ 3 | TOPIC | 103 — 981. Time Based Key-Value Store | Medium · Upper bound by key | MUST
# 103 — 981. Time Based Key-Value Store

**Problem 103 – Topic: Binary Search | Difficulty: Medium | Pattern: Upper bound by key**

[Open on LeetCode](https://leetcode.com/problems/time-based-key-value-store/)

## 1. Problem Statement
`set(key, value, timestamp)` stores a value with increasing timestamps. `get(key, timestamp)` returns the one with the latest time at or before the query, or "".

### Examples
```text
Input:  ["TimeMap", "set", "get", "get", "set", "get", "get"]
        [[], ["foo", "bar", 1], ["foo", 1], ["foo", 3], ["foo", "bar2", 4], ["foo", 4], ["foo", 5]]
Output: [null, null, "bar", "bar", null, "bar2", "bar2"]
```

### Constraints
```text
1 <= key.length, value.length <= 100
key and value consist of lowercase English letters and digits.
1 <= timestamp <= 10^7
All the timestamps timestamp of set are strictly increasing.
At most 2 * 10^5 calls will be made to set and get.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A diary per person
Each person has a diary in date order. "What was true on the 12th?" Open that diary and find the last entry not after the 12th.

| In the story | In the solution |
|---|---|
| diary | a list per key |
| last entry not after | upper bound minus one |

### Pattern signal
**Upper bound by key.** The history arrives sorted, so append and binary-search.

### Thought process
1. **First idea:** Scan all entries on each get.
2. **The insight:** The history arrives sorted, so append and binary-search.

### Optimal approach
1. Append on set.
2. Take the key's list.
3. Find the first later entry.
4. Return the one before it.

### Dry run
```text
entries = [(1,bar),(4,bar2)], get 3
low  high  mid  time  action
0    2     1    4     high = 1
0    1     0    1     low = 1, return bar
```

### Why it works
Before low all times qualify and from high none do, so the predecessor of the boundary is latest.

## 3. Java Solution with Explanation
### Code
```java
class TimeMap {
    private static class Entry {
        int time;
        String value;

        Entry(int time, String value) {
            this.time = time;
            this.value = value;
        }
    }

    private final Map<String, List<Entry>> diaries = new HashMap<>();

    public void set(String key, String value, int timestamp) {
        diaries.computeIfAbsent(key, k -> new ArrayList<>()).add(new Entry(timestamp, value));
    }

    public String get(String key, int timestamp) {
        List<Entry> diary = diaries.getOrDefault(key, List.of());
        int low = 0, high = diary.size();
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (diary.get(mid).time <= timestamp) low = mid + 1;
            else high = mid;
        }
        return low == 0 ? "" : diary.get(low - 1).value;
    }
}
```

### Code walkthrough
- `computeIfAbsent`: A new diary on demand.
- `time <= timestamp`: Exact dates qualify.
- `low - 1`: The last one not after.

### Complexity
- **Time:** O(1) set, O(log n) get
- **Space:** O(n)

### Common mistakes
1. **Requiring the exact time.** Earlier is allowed.
2. **Sorting on get.** It is already sorted.
3. **Returning the next.** It is in the future.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Unknown | `get(x,1)` | `""` |
| Too early | `set at 5, get 4` | `""` |
| Exact | `set at 5, get 5` | `that value` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[089 — 35. Search Insert Position]]: The boundary. The key idea there: "At least target" goes from false to true once. Find the first true.
- **1146. Snapshot Array**: A diary per index.

### In a real interview
Say this: *"A map leads to a sorted history and an upper bound finds the version."*

## 5. Recall
Q: 981. Time Based Key-Value Store: which pattern, and what is the insight?
A: Upper bound by key. The history arrives sorted, so append and binary-search.

Q: 981. Time Based Key-Value Store: what is the analogy, and what does it stand for?
A: A diary per person: diary → a list per key; last entry not after → upper bound minus one.

Q: 981. Time Based Key-Value Store: Which entry does get return?
A: The last with time at most the query.

Q: 981. Time Based Key-Value Store: what are the time and space costs?
A: Time O(1) set, O(log n) get. Space O(n).

@@ 3 | TOPIC | 104 — 34. Find First and Last Position of Element in Sorted Array | Medium · Two bounds | SHOULD
# 104 — 34. Find First and Last Position of Element in Sorted Array

**Problem 104 – Topic: Binary Search | Difficulty: Medium | Pattern: Two bounds**

[Open on LeetCode](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/)

## 1. Problem Statement
Return the first and last index of `target` in a sorted array, or [-1,-1], in O(log n).

### Examples
```text
Input:  nums = [5,7,7,8,8,10], target = 8
Output: [3,4]

Input:  nums = [5,7,7,8,8,10], target = 6
Output: [-1,-1]

Input:  nums = [], target = 0
Output: [-1,-1]
```

### Constraints
```text
0 <= nums.length <= 10^5
-10^9 <= nums[i] <= 10^9
nums is a non-decreasing array.
-10^9 <= target <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The edges of a crowd in red shirts
Don't walk through the crowd. Find where red begins, and where the next colour begins, then step back one.

| In the story | In the solution |
|---|---|
| red begins | lower bound of target |
| next colour | first value above target |

### Pattern signal
**Two bounds.** Both edges are boundaries of monotonic questions.

### Thought process
1. **First idea:** Find one and expand, O(n) when all are equal.
2. **The insight:** Both edges are boundaries of monotonic questions.

### Optimal approach
1. Find the first value at least target.
2. Verify it.
3. Find the first value above target.
4. Subtract one.

### Dry run
```text
nums = [5,7,7,8,8,10], target = 8
search          boundary  meaning
first >= 8      3         start
first > 8       5         end is 4
```

### Why it works
The occurrences are exactly the positions between those boundaries.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int start = bound(nums, target, false);
        if (start == nums.length || nums[start] != target) return new int[] {-1, -1};
        return new int[] {start, bound(nums, target, true) - 1};
    }

    private int bound(int[] nums, int target, boolean above) {
        int low = 0, high = nums.length;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] < target || (above && nums[mid] == target)) low = mid + 1;
            else high = mid;
        }
        return low;
    }
}
```

### Code walkthrough
- `nums[start] != target`: Nobody in red.
- `above &&`: Equals are skipped for the second edge.
- `- 1`: Step back into the crowd.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Linear expansion.** The bound fails.
2. **Reading at n.** Check length first.
3. **Searching target + 1 blindly.** It can overflow in general.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], 1` | `[-1,-1]` |
| One | `[1], 1` | `[0,0]` |
| All | `[2,2], 2` | `[0,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[089 — 35. Search Insert Position]]: One edge. The key idea there: "At least target" goes from false to true once. Find the first true.
- [[088 — 704. Binary Search]]: Any occurrence. The key idea there: In sorted data one comparison rules out everything on one side.

### In a real interview
Say this: *"I reuse a boundary search for at least and for above."*

## 5. Recall
Q: 34. Find First and Last Position of Element in Sorted Array: which pattern, and what is the insight?
A: Two bounds. Both edges are boundaries of monotonic questions.

Q: 34. Find First and Last Position of Element in Sorted Array: what is the analogy, and what does it stand for?
A: The edges of a crowd in red shirts: red begins → lower bound of target; next colour → first value above target.

Q: 34. Find First and Last Position of Element in Sorted Array: Which two boundaries are found?
A: The first value at least target and the first above it.

Q: 34. Find First and Last Position of Element in Sorted Array: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 2 | MODULE | Binary Search — Hard | 2 problems | MUST
# Binary Search: Hard

Patterns in this group: First feasible, Partition search.

@@ 3 | TOPIC | 105 — 410. Split Array Largest Sum | Hard · First feasible | SHOULD
# 105 — 410. Split Array Largest Sum

**Problem 105 – Topic: Binary Search | Difficulty: Hard | Pattern: First feasible**

[Open on LeetCode](https://leetcode.com/problems/split-array-largest-sum/)

## 1. Problem Statement
Split `nums` into `k` non-empty contiguous parts so the largest part sum is as small as possible. Return that sum.

### Examples
```text
Input:  nums = [7,2,5,10,8], k = 2
Output: 18

Input:  nums = [1,2,3,4,5], k = 2
Output: 9
```

### Constraints
```text
1 <= nums.length <= 1000
0 <= nums[i] <= 10^6
1 <= k <= min(50, nums.length)
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Copyists sharing chapters
Chapters go to k copyists in order. Pick a page limit and hand out chapters until someone would exceed it. If k people are enough, try a lower limit.

| In the story | In the solution |
|---|---|
| page limit | the searched answer |
| people used | the greedy part count |

### Pattern signal
**First feasible.** Choosing cuts is hard but checking a limit is easy, and monotonic.

### Thought process
1. **First idea:** Try all cuts.
2. **The insight:** Choosing cuts is hard but checking a limit is easy, and monotonic.

### Optimal approach
1. Bound by the maximum and total.
2. Count parts greedily.
3. Shrink if at most k.
4. Otherwise grow.

### Dry run
```text
nums = [7,2,5,10,8], k = 2
low  high  limit  parts            action
10   32    21     14,18            high = 21
10   21    15     14,10,8          low = 16
16   21    18     14,18            high = 18
16   18    17     14,10,8          low = 18, return 18
```

### Why it works
Greedy uses the fewest parts. Fewer than k can be cut further without raising the maximum.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int splitArray(int[] nums, int k) {
        int low = 0, high = 0;
        for (int x : nums) {
            low = Math.max(low, x);
            high += x;
        }
        while (low < high) {
            int limit = low + (high - low) / 2;
            if (parts(nums, limit) <= k) high = limit;
            else low = limit + 1;
        }
        return low;
    }

    private int parts(int[] nums, int limit) {
        int count = 1, pages = 0;
        for (int x : nums) {
            if (pages + x > limit) {
                count++;
                pages = 0;
            }
            pages += x;
        }
        return count;
    }
}
```

### Code walkthrough
- `<= k`: Spare copyists only help.
- `Math.max(low, x)`: A chapter can't split.
- `pages + x > limit`: The next person starts.

### Complexity
- **Time:** O(n log sum)
- **Space:** O(1)

### Common mistakes
1. **Requiring exactly k.** Feasible limits get rejected.
2. **Sorting.** The parts are contiguous.
3. **low = 0 in search.** The check breaks.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One part | `[1,2], 1` | `3` |
| Singles | `[1,2], 2` | `2` |
| Zeros | `[0,0], 1` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[095 — 1011. Capacity To Ship Packages Within D Days]]: The same. The key idea there: Feasibility is monotonic and testing one capacity is a linear greedy.
- [[099 — 2616. Minimize the Maximum Difference of Pairs]]: Another check. The key idea there: Sorted neighbors are the only useful pairs, and for a given limit pairing the earliest is safe.

### In a real interview
Say this: *"I search the maximum allowed sum and count parts greedily."*

## 5. Recall
Q: 410. Split Array Largest Sum: which pattern, and what is the insight?
A: First feasible. Choosing cuts is hard but checking a limit is easy, and monotonic.

Q: 410. Split Array Largest Sum: what is the analogy, and what does it stand for?
A: Copyists sharing chapters: page limit → the searched answer; people used → the greedy part count.

Q: 410. Split Array Largest Sum: When is a sum limit feasible?
A: When greedy needs at most k parts.

Q: 410. Split Array Largest Sum: what are the time and space costs?
A: Time O(n log sum). Space O(1).

@@ 3 | TOPIC | 106 — 4. Median of Two Sorted Arrays | Hard · Partition search | MUST
# 106 — 4. Median of Two Sorted Arrays

**Problem 106 – Topic: Binary Search | Difficulty: Hard | Pattern: Partition search**

[Open on LeetCode](https://leetcode.com/problems/median-of-two-sorted-arrays/)

## 1. Problem Statement
Return the median of two sorted arrays in logarithmic time.

### Examples
```text
Input:  nums1 = [1,3], nums2 = [2]
Output: 2.00000

Input:  nums1 = [1,2], nums2 = [3,4]
Output: 2.50000
```

### Constraints
```text
nums1.length == m
nums2.length == n
0 <= m <= 1000
0 <= n <= 1000
1 <= m + n <= 2000
-10^6 <= nums1[i], nums2[i] <= 10^6
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Cutting two decks at once
You want the left table to hold half of all cards, every one no bigger than any on the right. Cut the small deck somewhere and the big deck's cut is forced. Only the four cards touching the cuts can object, and they say which way to slide.

| In the story | In the solution |
|---|---|
| forced cut | j = half − i |
| four cards | the border values |

### Pattern signal
**Partition search.** The median needs a valid partition, not merged order.

### Thought process
1. **First idea:** Merge up to the middle, in O(m + n).
2. **The insight:** The median needs a valid partition, not merged order.

### Optimal approach
1. Search cuts of the shorter array.
2. Derive the other cut.
3. Check the cross borders.
4. Slide toward validity.
5. Compute from the borders.

### Dry run
```text
a = [1,3], b = [2,4,5,6], half = 3
i  j  a borders  b borders  action
1  2  1,3        4,5        4 > 3, i higher
2  1  3,inf      2,4        valid
median = (3 + 4) / 2 = 3.5
```

### Why it works
Valid borders mean every left card is at most every right one. A violated border shows monotonically which way the cut must move.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) return findMedianSortedArrays(nums2, nums1);
        int m = nums1.length, n = nums2.length, half = (m + n + 1) / 2;
        int low = 0, high = m;
        while (true) {
            int i = low + (high - low) / 2, j = half - i;
            int aLeft = i == 0 ? Integer.MIN_VALUE : nums1[i - 1];
            int aRight = i == m ? Integer.MAX_VALUE : nums1[i];
            int bLeft = j == 0 ? Integer.MIN_VALUE : nums2[j - 1];
            int bRight = j == n ? Integer.MAX_VALUE : nums2[j];
            if (aLeft > bRight) high = i - 1;
            else if (bLeft > aRight) low = i + 1;
            else {
                int left = Math.max(aLeft, bLeft);
                if ((m + n) % 2 == 1) return left;
                return (left + Math.min(aRight, bRight)) / 2.0;
            }
        }
    }
}
```

### Code walkthrough
- `nums1.length > nums2.length`: Search the smaller deck, keeping j valid.
- `half - i`: The forced cut.
- `Integer.MIN_VALUE`: An empty side never objects.
- `aLeft > bRight`: Too much of a on the left.

### Complexity
- **Time:** O(log min(m,n))
- **Space:** O(1)

### Common mistakes
1. **Searching the longer.** j goes out of range.
2. **Comparing within one array.** Only cross pairs matter.
3. **Integer division.** Use 2.0.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One empty | `[], [1]` | `1.0` |
| Even | `[1], [2]` | `1.5` |
| Apart | `[1,2], [9,10]` | `5.5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[183 — 295. Find Median from Data Stream]]: Two heaps hold the halves. The key idea there: The median needs only the two middle borders, which two heaps expose.
- [[063 — 658. Find K Closest Elements]]: Search a cut. The key idea there: The answer is contiguous, and "should move right" changes only once as the start grows.

### In a real interview
Say this: *"I binary-search a partition of the shorter array and test two cross inequalities."*

## 5. Recall
Q: 4. Median of Two Sorted Arrays: which pattern, and what is the insight?
A: Partition search. The median needs a valid partition, not merged order.

Q: 4. Median of Two Sorted Arrays: what is the analogy, and what does it stand for?
A: Cutting two decks at once: forced cut → j = half − i; four cards → the border values.

Q: 4. Median of Two Sorted Arrays: When is the partition valid?
A: When each left border is at most the other array's right border.

Q: 4. Median of Two Sorted Arrays: what are the time and space costs?
A: Time O(log min(m,n)). Space O(1).

@@ 3 | TOPIC | Practice — Pattern summary: Binary Search | Key patterns of the topic | MUST
# Pattern summary: Binary Search

All you need is a yes/no question whose answers switch once. Keep the switch inside the range and halve it.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Classic search | Find a target in a sorted array. (704) | A loop that never shrinks |
| Lower bound | Find a target's index or insertion position. (35) | Returning right instead of left |
| Last feasible | Count complete staircase rows from n coins. (441, 69) | Overflow in the product |
| Opposite pointers | Return sorted squares of a sorted array. (977) | Negatives break the original order |
| Search on answer | Decide whether a number is a perfect square without sqrt. (367) | Overflow when squaring |
| Parity search | Find the single value in a sorted array of pairs in O(log n). (540) | Reading past an end |
| First feasible | Find the smallest ship capacity finishing within days. (1011, 875, 410) | A capacity below one package |
| Follow the slope | Find any index greater than both neighbors. (162) | Assuming a sorted array |
| 2D search | Search a matrix whose rows continue in sorted order. (74) | Confusing it with row-only and column-only sorting |
| Feasibility greedy | Form p pairs minimizing the largest difference. (2616) | Reusing an index |
| Rotation search | Find the minimum of a rotated sorted array. (153) | Discarding the minimum at mid |
| One sorted half | Search a rotated sorted array. (33, 81) | Strict comparison at range ends |
| Upper bound by key | Store timestamped values and read the latest at or before a time. (981) | Returning a later timestamp |
| Two bounds | Find first and last positions of a target. (34) | Expanding linearly from one match |
| Partition search | Find the median of two sorted arrays in logarithmic time. (4) | Searching the longer array |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| find in sorted | the dictionary | classic | 704, 74 |
| first, last, insert at | the edge of a crowd | bounds | 35, 34, 981 |
| largest that still fits | the last step you can afford | last feasible | 441, 69 |
| minimum speed or capacity | trying shoe sizes | first feasible | 875, 1011, 410, 2616 |
| rotated | a clock with one seam | one sorted half | 153, 33, 81 |
| pairs, except one | couples in a cinema row | parity | 540 |
| median of two | cutting two decks | partition | 4 |

## Which template do I pick?

1. **Exact match:** `low <= high`, then `mid + 1` and `mid - 1`.
2. **First true:** `low < high`, lower middle, then `high = mid` or `low = mid + 1`.
3. **Last true:** `low < high`, upper middle, then `low = mid` or `high = mid - 1`.
4. **Searching an answer:** write `works(x)` first, and prove that once true it stays true.

Q: Which signals point to Binary Search?
A: sorted; rotated; O(log n); smallest value that works.

Q: What is the core idea of Binary Search?
A: All you need is a yes/no question whose answers switch once. Keep the switch inside the range and halve it.
