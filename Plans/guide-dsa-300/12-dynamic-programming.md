@@ 1 | MODULE | Phase 12 — Dynamic Programming (1D → 2D → Advanced) | number of ways; minimum or maximum; repeated subproblems | MUST
# Phase 12: Dynamic Programming (1D → 2D → Advanced)

Write five things before code: state, transition, base case, order and answer. Start with memoized recursion, turn it into a table, then drop dimensions you no longer read.

**Signals:** number of ways; minimum or maximum; repeated subproblems.

## The big analogy: sticky notes on a staircase

Asked "how many ways to step 50?" you would recompute step 30 millions of times. Write each answer on a sticky note the first time and **never solve the same question twice**. That is all DP is. The skill is choosing what the note says.

| Five decisions | Picture | For climbing stairs |
|---|---|---|
| State | what a note says | ways to reach step i |
| Transition | which notes you read | i-1 and i-2 |
| Base case | notes written by hand | steps 0 and 1 have one way |
| Order | read only written notes | bottom to top |
| Answer | the note you hand in | step n |

## Template
```java
int[] dp = new int[amount + 1];
dp[0] = 1;                                      // base case
for (int coin : coins) {                        // order
    for (int s = coin; s <= amount; s++) dp[s] += dp[s - coin]; // transition
}
return dp[amount];                              // answer
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Dynamic Programming — 1D DP | 18 problems | MUST
# Dynamic Programming: 1D DP

Patterns in this group: Fibonacci DP, Min-cost DP, Take or skip, Circular take or skip, Expand centers, Two-choice count, Unbounded knapsack, Max and min state, Segmentation DP, Patience sorting, 0/1 knapsack, Bottom-up path, Value take or skip, Stack and prefix, Product of threes, Length and count.

@@ 3 | TOPIC | 229 — 70. Climbing Stairs | Easy · Fibonacci DP | MUST
# 229 — 70. Climbing Stairs

**Problem 229 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Easy | Pattern: Fibonacci DP**

[Open on LeetCode](https://leetcode.com/problems/climbing-stairs/)

## 1. Problem Statement
A staircase has n steps and each move climbs either 1 or 2 of them. Return how many distinct sequences of moves reach the top.

### Examples
```text
Input:  n = 2
Output: 2

Input:  n = 3
Output: 3
```

### Constraints
```text
1 <= n <= 45
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two notes on a staircase
Put a sticky note on every step saying how many ways lead there. You can only arrive from one or two steps below, so a note is the sum of those two. You never need more than the last two notes in your hand.

| In the story | In the solution |
|---|---|
| notes | two variables |
| arriving | last move |

### Pattern signal
**Fibonacci DP.** Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.

### Thought process
1. **First idea:** Recursively try both moves at every step, which repeats the same steps exponentially often.
2. **The insight:** Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.

### Optimal approach
1. Set the notes for steps 0 and 1 to one.
2. Add the last two notes.
3. Shift them along.
4. Return the newest.

### Dry run
```text
n = 5
step  twoBack  oneBack  ways here
2     1        1        2
3     1        2        3
4     2        3        5
5     3        5        8
```

### Why it works
The two last moves are different cases and cover every climb, so their counts add without overlap. By induction every note is exact.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int climbStairs(int n) {
        int twoBack = 1, oneBack = 1;
        for (int step = 2; step <= n; step++) {
            int now = oneBack + twoBack;
            twoBack = oneBack;
            oneBack = now;
        }
        return oneBack;
    }
}
```

### Code walkthrough
- `twoBack = 1`: Doing nothing is the one way to stand at step 0.
- `oneBack + twoBack`: The two possible arrivals are added.
- `twoBack = oneBack`: Shift the notes before overwriting the newer one.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Plain recursion repeats subproblems.** Fix: keep two values.
2. **Zero ways for zero steps breaks the base.** Fix: use one.
3. **Overwriting a value before shifting loses it.** Fix: use a temporary.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `1` |
| Two | `2` | `2` |
| Four | `4` | `5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[230 — 746. Min Cost Climbing Stairs]]: Minimum. The key idea there: How you reached a landing doesn't matter later, only the cheapest cost of reaching it.
- [[235 — 91. Decode Ways]]: Conditions. The key idea there: The final code uses one digit or two, and only valid choices pass their count on.

### In a real interview
Say it in this order:

1. **Brute force:** Recursively try both moves at every step, which repeats the same steps exponentially often.
2. **Insight:** Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.
3. **Approach in a few words:** The last move gives Fibonacci.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 70. Climbing Stairs: which pattern, and what is the insight?
A: Fibonacci DP. Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.

Q: 70. Climbing Stairs: what is the analogy, and what does it stand for?
A: Two notes on a staircase: notes → two variables; arriving → last move.

Q: 70. Climbing Stairs: What is the stair state?
A: Ways to reach step i.

Q: 70. Climbing Stairs: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 230 — 746. Min Cost Climbing Stairs | Easy · Min-cost DP | MUST
# 230 — 746. Min Cost Climbing Stairs

**Problem 230 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Easy | Pattern: Min-cost DP**

[Open on LeetCode](https://leetcode.com/problems/min-cost-climbing-stairs/)

## 1. Problem Statement
`cost[i]` is the price of leaving step i. After paying it you climb one or two steps. You may start at step 0 or 1. Return the least cost to reach the floor just past the last step.

### Examples
```text
Input:  cost = [10,15,20]
Output: 15

Input:  cost = [1,100,1,1,1,100,1,1,100,1]
Output: 6
```

### Constraints
```text
2 <= cost.length <= 1000
0 <= cost[i] <= 999
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A toll on each landing
Every landing charges a toll when you leave it. Write on each landing the cheapest total to get there: the cheaper of the two landings below, each plus its toll. The exit is one landing above the last.

| In the story | In the solution |
|---|---|
| toll | cost |
| landing | state |

### Pattern signal
**Min-cost DP.** How you reached a landing doesn't matter later, only the cheapest cost of reaching it.

### Thought process
1. **First idea:** Try every sequence of climbs, which is exponential.
2. **The insight:** How you reached a landing doesn't matter later, only the cheapest cost of reaching it.

### Optimal approach
1. Start landings 0 and 1 at zero.
2. Compute the cheaper of the two arrivals.
3. Shift.
4. Continue to position n.

### Dry run
```text
cost = [10,15,20]
landing  from one below   from two below  cheapest
0        -                -               0, free start
1        -                -               0, free start
2        0 + 15           0 + 10          10
3 (top)  10 + 20          0 + 15          15
```

### Why it works
The last move onto a landing comes from one of two landings, and an optimal route reaches that one optimally.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minCostClimbingStairs(int[] cost) {
        int twoBack = 0, oneBack = 0;
        for (int i = 2; i <= cost.length; i++) {
            int now = Math.min(oneBack + cost[i - 1], twoBack + cost[i - 2]);
            twoBack = oneBack;
            oneBack = now;
        }
        return oneBack;
    }
}
```

### Code walkthrough
- `oneBack = 0`: Both allowed starting landings are free.
- `cost[i - 1]`: The toll is paid for the landing you leave.
- `i <= cost.length`: The floor is one position past the last step.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **The top is not the last index.** Fix: compute position n.
2. **Choosing the cheaper next step ignores later costs.** Fix: use DP.
3. **The first two starts are free.** Fix: begin both states at 0.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two | `[10,15]` | `10` |
| Zeros | `[0,0]` | `0` |
| Middle | `[10,1,10]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[229 — 70. Climbing Stairs]]: Add. The key idea there: Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.
- [[254 — 64. Minimum Path Sum]]: Two directions. The key idea there: The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.

### In a real interview
Say it in this order:

1. **Brute force:** Try every sequence of climbs, which is exponential.
2. **Insight:** How you reached a landing doesn't matter later, only the cheapest cost of reaching it.
3. **Approach in a few words:** Minimum-cost linear DP.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 746. Min Cost Climbing Stairs: which pattern, and what is the insight?
A: Min-cost DP. How you reached a landing doesn't matter later, only the cheapest cost of reaching it.

Q: 746. Min Cost Climbing Stairs: what is the analogy, and what does it stand for?
A: A toll on each landing: toll → cost; landing → state.

Q: 746. Min Cost Climbing Stairs: What is the cost state?
A: Cheapest arrival at landing i.

Q: 746. Min Cost Climbing Stairs: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 231 — 198. House Robber | Medium · Take or skip | MUST
# 231 — 198. House Robber

**Problem 231 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Take or skip**

[Open on LeetCode](https://leetcode.com/problems/house-robber/)

## 1. Problem Statement
Houses along a street each hold some money. Robbing two adjacent houses on the same night triggers the alarm. Return the most money you can rob without triggering it.

### Examples
```text
Input:  nums = [1,2,3,1]
Output: 4

Input:  nums = [2,7,9,3,1]
Output: 12
```

### Constraints
```text
1 <= nums.length <= 100
0 <= nums[i] <= 400
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Dinner guests who dislike neighbors
You invite guests from a row where neighbors dislike each other. For each newcomer either don't invite them, keeping your best party so far, or invite them and add to the best party that excluded their neighbor.

| In the story | In the solution |
|---|---|
| don't | best one back |
| invite | value plus two back |

### Pattern signal
**Take or skip.** The last house is either skipped or taken, and each case leaves one smaller, solved problem.

### Thought process
1. **First idea:** Try every non-adjacent subset, which is exponential.
2. **The insight:** The last house is either skipped or taken, and each case leaves one smaller, solved problem.

### Optimal approach
1. Keep the best one and two houses back.
2. Compute the better of skipping and taking.
3. Shift.
4. Return the last.

### Dry run
```text
nums = [1,2,3,1]
value  twoBack  oneBack  skip  take     best
1      0        0        0     0+1      1
2      0        1        1     0+2      2
3      1        2        2     1+3      4
1      2        4        4     2+1      4
```

### Why it works
An optimal plan either omits the last house, making it optimal for the previous prefix, or includes it with an optimal plan two back.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int rob(int[] nums) {
        int twoBack = 0, oneBack = 0;
        for (int value : nums) {
            int now = Math.max(oneBack, twoBack + value);
            twoBack = oneBack;
            oneBack = now;
        }
        return oneBack;
    }
}
```

### Code walkthrough
- `twoBack + value`: Invite this guest along with the best party two back.
- `Math.max`: Keep whichever party is worth more.
- `oneBack = 0`: With no houses there is no money.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Alternate sums miss gaps of two houses.** Fix: decide at each house.
2. **Adding to the previous best robs neighbors.** Fix: add to two back.
3. **Taking the largest first can lose.** Fix: compare both choices.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5]` | `5` |
| Gap two | `[5,1,1,5]` | `10` |
| Zero | `[0]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[232 — 213. House Robber II]]: Twice. The key idea there: Any valid circular plan omits the first or the last house, so two straight-line problems cover everything.
- [[160 — 337. House Robber III]]: Pairs. The key idea there: The only thing linking parent and child is whether each is robbed, two states.

### In a real interview
Say it in this order:

1. **Brute force:** Try every non-adjacent subset, which is exponential.
2. **Insight:** The last house is either skipped or taken, and each case leaves one smaller, solved problem.
3. **Approach in a few words:** Take or skip.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 198. House Robber: which pattern, and what is the insight?
A: Take or skip. The last house is either skipped or taken, and each case leaves one smaller, solved problem.

Q: 198. House Robber: what is the analogy, and what does it stand for?
A: Dinner guests who dislike neighbors: don't → best one back; invite → value plus two back.

Q: 198. House Robber: What are the robber choices?
A: Best one back, or value plus best two back.

Q: 198. House Robber: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 232 — 213. House Robber II | Medium · Circular take or skip | MUST
# 232 — 213. House Robber II

**Problem 232 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Circular take or skip**

[Open on LeetCode](https://leetcode.com/problems/house-robber-ii/)

## 1. Problem Statement
Houses stand in a circle, so the first and last are neighbors. Robbing two adjacent houses triggers the alarm. Return the most money you can rob without triggering it.

### Examples
```text
Input:  nums = [2,3,2]
Output: 3

Input:  nums = [1,2,3,1]
Output: 4

Input:  nums = [1,2,3]
Output: 3
```

### Constraints
```text
1 <= nums.length <= 100
0 <= nums[i] <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A round table cut open
At a round table the first and last seats touch. So cut the table open twice: once with the last guest banned and once with the first. Each is an ordinary row you already know how to solve.

| In the story | In the solution |
|---|---|
| cut | range |
| line | problem 198 |

### Pattern signal
**Circular take or skip.** Any valid circular plan omits the first or the last house, so two straight-line problems cover everything.

### Thought process
1. **First idea:** Try all subsets that respect the circle.
2. **The insight:** Any valid circular plan omits the first or the last house, so two straight-line problems cover everything.

### Optimal approach
1. Return the only value for one house.
2. Solve the range without the last.
3. Solve the range without the first.
4. Take the larger.

### Dry run
```text
nums = [2,3,2]
row solved        first house  second house    best
without last      2            max(2, 0+3)     3
without first     3            max(3, 0+2)     3
answer = max(3, 3) = 3
```

### Why it works
No plan within either range can use both ends, so all are valid, and every valid circular plan lies in one of the two ranges.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(line(nums, 0, n - 2), line(nums, 1, n - 1));
    }

    private int line(int[] nums, int from, int to) {
        int twoBack = 0, oneBack = 0;
        for (int i = from; i <= to; i++) {
            int now = Math.max(oneBack, twoBack + nums[i]);
            twoBack = oneBack;
            oneBack = now;
        }
        return oneBack;
    }
}
```

### Code walkthrough
- `n == 1`: With one house both ranges would be empty.
- `line(nums, 0, n - 2)`: The first row: the last guest is banned.
- `line(nums, 1, n - 1)`: The second row: the first guest is banned.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **The plain solution robs both ends.** Fix: exclude one end.
2. **A single house makes both ranges empty.** Fix: return it directly.
3. **Running one range misses plans.** Fix: take the better of two.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[4]` | `4` |
| Two | `[1,2]` | `2` |
| Four | `[1,2,3,1]` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[231 — 198. House Robber]]: Piece. The key idea there: The last house is either skipped or taken, and each case leaves one smaller, solved problem.
- [[278 — 918. Maximum Sum Circular Subarray]]: Complement. The key idea there: The best wrapping sum equals the total minus the minimum subarray, which a second Kadane finds.

### In a real interview
Say it in this order:

1. **Brute force:** Try all subsets that respect the circle.
2. **Insight:** Any valid circular plan omits the first or the last house, so two straight-line problems cover everything.
3. **Approach in a few words:** Two linear runs.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 213. House Robber II: which pattern, and what is the insight?
A: Circular take or skip. Any valid circular plan omits the first or the last house, so two straight-line problems cover everything.

Q: 213. House Robber II: what is the analogy, and what does it stand for?
A: A round table cut open: cut → range; line → problem 198.

Q: 213. House Robber II: How is the circle broken?
A: Exclude first or exclude last.

Q: 213. House Robber II: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 233 — 5. Longest Palindromic Substring | Medium · Expand centers | MUST
# 233 — 5. Longest Palindromic Substring

**Problem 233 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Expand centers**

[Open on LeetCode](https://leetcode.com/problems/longest-palindromic-substring/)

## 1. Problem Statement
You get a string `s`. Return its longest contiguous substring that reads the same forwards and backwards. Any one of equal longest answers is accepted.

### Examples
```text
Input:  s = "babad"
Output: "bab"

Input:  s = "cbbd"
Output: "bb"
```

### Constraints
```text
1 <= s.length <= 1000
s consist of only digits and English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Ripples from every stone
Drop a stone on every letter and in every gap between letters. The ripple spreads as long as the letters on both sides match. The widest ripple is the longest palindrome.

| In the story | In the solution |
|---|---|
| stone | center |
| ripple | pointers |

### Pattern signal
**Expand centers.** A palindrome is defined by its center, and there are only 2n-1 possible centers.

### Thought process
1. **First idea:** Check each of the n squared substrings, in O(n³) altogether.
2. **The insight:** A palindrome is defined by its center, and there are only 2n-1 possible centers.

### Optimal approach
1. Loop over both kinds of center.
2. Expand while both ends match.
3. Remember the widest start and length.
4. Take the substring at the end.

### Dry run
```text
babad
center  widest
1       bab
2       aba
```

### Why it works
Every palindrome contains all smaller ones with the same center, so expansion from that center reaches it, and stopping at a mismatch loses nothing.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String longestPalindrome(String s) {
        int start = 0, length = 0;
        for (int center = 0; center < 2 * s.length() - 1; center++) {
            int left = center / 2, right = left + center % 2;
            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                if (right - left + 1 > length) {
                    start = left;
                    length = right - left + 1;
                }
                left--;
                right++;
            }
        }
        return s.substring(start, start + length);
    }
}
```

### Code walkthrough
- `center % 2`: Even centers are letters and odd centers are gaps.
- `left--`: The ripple spreads one step outward.
- `substring`: Only the final answer is copied.

### Complexity
- **Time:** O(n²)
- **Space:** O(1)

### Common mistakes
1. **Only odd centers miss "bb".** Fix: also start in the gaps.
2. **Common substring with the reverse gives false matches.** Fix: verify symmetry.
3. **Copying at each expansion is cubic.** Fix: store indices.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `"a"` | `"a"` |
| Even | `"bb"` | `"bb"` |
| None | `"ab"` | `"a"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[234 — 647. Palindromic Substrings]]: Every step. The key idea there: Each palindrome corresponds to exactly one center and one radius.
- **516. Subsequence**: DP.

### In a real interview
Say it in this order:

1. **Brute force:** Check each of the n squared substrings, in O(n³) altogether.
2. **Insight:** A palindrome is defined by its center, and there are only 2n-1 possible centers.
3. **Approach in a few words:** Expand all centers.
4. **Cost:** time O(n²); space O(1).

## 5. Recall
Q: 5. Longest Palindromic Substring: which pattern, and what is the insight?
A: Expand centers. A palindrome is defined by its center, and there are only 2n-1 possible centers.

Q: 5. Longest Palindromic Substring: what is the analogy, and what does it stand for?
A: Ripples from every stone: stone → center; ripple → pointers.

Q: 5. Longest Palindromic Substring: How many palindrome centers?
A: 2n minus one.

Q: 5. Longest Palindromic Substring: what are the time and space costs?
A: Time O(n²). Space O(1).

@@ 3 | TOPIC | 234 — 647. Palindromic Substrings | Medium · Expand centers | MUST
# 234 — 647. Palindromic Substrings

**Problem 234 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Expand centers**

[Open on LeetCode](https://leetcode.com/problems/palindromic-substrings/)

## 1. Problem Statement
You get a string. Return the number of its substrings that are palindromes. Substrings at different positions count separately, even if their text is equal.

### Examples
```text
Input:  s = "abc"
Output: 3

Input:  s = "aaa"
Output: 6
```

### Constraints
```text
1 <= s.length <= 1000
s consists of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Counting rings of ripples
Use the same ripples, but count rings. Every successful step outward from a center is one more palindrome, with its own start and end.

| In the story | In the solution |
|---|---|
| ring | expansion |
| count | increment |

### Pattern signal
**Expand centers.** Each palindrome corresponds to exactly one center and one radius.

### Thought process
1. **First idea:** Test every substring separately, in O(n³).
2. **The insight:** Each palindrome corresponds to exactly one center and one radius.

### Optimal approach
1. Loop over all 2n-1 centers.
2. Expand while the ends match.
3. Add one at each successful step.

### Dry run
```text
aaa
center       found
letters      1,2,1
gaps         1,1
total        6
```

### Why it works
Expansion succeeds at exactly the radii that are palindromes, and no two center and radius pairs describe the same position.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int countSubstrings(String s) {
        int count = 0;
        for (int center = 0; center < 2 * s.length() - 1; center++) {
            int left = center / 2, right = left + center % 2;
            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                count++;
                left--;
                right++;
            }
        }
        return count;
    }
}
```

### Code walkthrough
- `count++`: One ring, one palindrome.
- `center / 2`: The stone's position among letters and gaps.
- `s.charAt(left) ==`: The ripple continues only while symmetric.

### Complexity
- **Time:** O(n²)
- **Space:** O(1)

### Common mistakes
1. **Counting distinct text is wrong.** Fix: count positions.
2. **Only odd centers miss even palindromes.** Fix: include gaps.
3. **Counting only the widest misses inner ones.** Fix: count every expansion.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `"a"` | `1` |
| Pair | `"aa"` | `3` |
| None | `"ab"` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[233 — 5. Longest Palindromic Substring]]: Maximum. The key idea there: A palindrome is defined by its center, and there are only 2n-1 possible centers.
- [[194 — 131. Palindrome Partitioning]]: Backtrack. The key idea there: An invalid first piece rules out every way of cutting what follows, so test before recursing.

### In a real interview
Say it in this order:

1. **Brute force:** Test every substring separately, in O(n³).
2. **Insight:** Each palindrome corresponds to exactly one center and one radius.
3. **Approach in a few words:** Count expansions.
4. **Cost:** time O(n²); space O(1).

## 5. Recall
Q: 647. Palindromic Substrings: which pattern, and what is the insight?
A: Expand centers. Each palindrome corresponds to exactly one center and one radius.

Q: 647. Palindromic Substrings: what is the analogy, and what does it stand for?
A: Counting rings of ripples: ring → expansion; count → increment.

Q: 647. Palindromic Substrings: What adds one to the count?
A: Each successful expansion.

Q: 647. Palindromic Substrings: what are the time and space costs?
A: Time O(n²). Space O(1).

@@ 3 | TOPIC | Practice — Review 39 | Re-solve without looking | MUST
# Review 39

Solve on a blank page. No notes, hints or solutions.

## 647. Palindromic Substrings
Count palindromic substrings.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/palindromic-substrings/)

## 198. House Robber
Maximize robbery with no adjacent houses.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/house-robber/)

## 785. Is Graph Bipartite
Decide whether a graph is bipartite.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/is-graph-bipartite/)

## 473. Matchsticks to Square
Decide whether sticks form a square.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/matchsticks-to-square/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 647 | | | | |
| 198 | | | | |
| 785 | | | | |
| 473 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[234 — 647. Palindromic Substrings]]
- [[231 — 198. House Robber]]
- [[220 — 785. Is Graph Bipartite]]
- [[196 — 473. Matchsticks to Square]]

@@ 3 | TOPIC | 235 — 91. Decode Ways | Medium · Two-choice count | MUST
# 235 — 91. Decode Ways

**Problem 235 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Two-choice count**

[Open on LeetCode](https://leetcode.com/problems/decode-ways/)

## 1. Problem Statement
Letters A to Z are encoded as "1" to "26". You get a string of digits. Return the number of ways to decode all of it. A code may not have a leading zero, so "06" is invalid.

### Examples
```text
Input:  s = "12"
Output: 2

Input:  s = "226"
Output: 3

Input:  s = "06"
Output: 0
```

### Constraints
```text
1 <= s.length <= 100
s contains only digits and may contain leading zero(s).
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Stepping stones with rotten planks
You cross on stepping stones one or two at a time, but some moves are rotten: a lone 0 can't hold you and a double only works from 10 to 26. Each stone's note adds up the moves that hold.

| In the story | In the solution |
|---|---|
| plank | two digits |
| ways | notes |

### Pattern signal
**Two-choice count.** The final code uses one digit or two, and only valid choices pass their count on.

### Thought process
1. **First idea:** Try every split of the digits, which is exponential.
2. **The insight:** The final code uses one digit or two, and only valid choices pass their count on.

### Optimal approach
1. Give the empty prefix one way.
2. Inherit one back for a non-zero digit.
3. Add two back for a pair 10..26.
4. Shift.

### Dry run
```text
s = "226"
digit  one-digit ways  two-digit code  add two back  ways
2      1               -               -             1
2      1               22, valid       1             2
6      2               26, valid       1             3
```

### Why it works
Decodings whose last code has one digit and those with two are separate and cover all, and each is valid exactly when its last code is.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numDecodings(String s) {
        int twoBack = 0, oneBack = 1;
        for (int i = 0; i < s.length(); i++) {
            int now = s.charAt(i) == '0' ? 0 : oneBack;
            if (i > 0) {
                int two = (s.charAt(i - 1) - '0') * 10 + s.charAt(i) - '0';
                if (two >= 10 && two <= 26) now += twoBack;
            }
            twoBack = oneBack;
            oneBack = now;
        }
        return oneBack;
    }
}
```

### Code walkthrough
- `? 0 : oneBack`: Zero has no letter of its own.
- `two >= 10`: A two-digit code cannot start with zero.
- `now += twoBack`: The second way of arriving at this position.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **A zero has no letter.** Fix: it adds nothing alone.
2. **"06" is not a code.** Fix: require at least 10.
3. **27 and above are not letters.** Fix: require at most 26.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `"0"` | `0` |
| Ten | `"10"` | `1` |
| Two | `"12"` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[229 — 70. Climbing Stairs]]: All valid. The key idea there: Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.
- [[238 — 139. Word Break]]: Dictionary. The key idea there: Whether a suffix can be split doesn't depend on how you reached it.

### In a real interview
Say it in this order:

1. **Brute force:** Try every split of the digits, which is exponential.
2. **Insight:** The final code uses one digit or two, and only valid choices pass their count on.
3. **Approach in a few words:** Conditional Fibonacci.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 91. Decode Ways: which pattern, and what is the insight?
A: Two-choice count. The final code uses one digit or two, and only valid choices pass their count on.

Q: 91. Decode Ways: what is the analogy, and what does it stand for?
A: Stepping stones with rotten planks: plank → two digits; ways → notes.

Q: 91. Decode Ways: When is two-back added?
A: When the last two digits form 10..26.

Q: 91. Decode Ways: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 236 — 322. Coin Change | Medium · Unbounded knapsack | MUST
# 236 — 322. Coin Change

**Problem 236 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Unbounded knapsack**

[Open on LeetCode](https://leetcode.com/problems/coin-change/)

## 1. Problem Statement
You get coin denominations and an amount, with unlimited coins of each kind. Return the fewest coins that make exactly that amount, or -1 if it cannot be made.

### Examples
```text
Input:  coins = [1,2,5], amount = 11
Output: 3

Input:  coins = [2], amount = 3
Output: -1

Input:  coins = [1], amount = 0
Output: 0
```

### Constraints
```text
1 <= coins.length <= 12
1 <= coins[i] <= 2^31 - 1
0 <= amount <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A price list for every amount
Write a price list: for every amount the fewest coins needed. To price an amount ask each coin "if you were the last one, what did the rest cost?" and take the cheapest answer plus one.

| In the story | In the solution |
|---|---|
| list | dp |
| last coin | transition |

### Pattern signal
**Unbounded knapsack.** Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.

### Thought process
1. **First idea:** Try every multiset of coins, which is exponential.
2. **The insight:** Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.

### Optimal approach
1. Set amount zero to 0 and others to infinity.
2. For each coin walk amounts upward.
3. Take the minimum with one more coin.
4. Translate infinity to -1.

### Dry run
```text
1,2,5
amount  fewest
1,2     1
3,4     2
5       1
10      2
11      3
```

### Why it works
Every optimal combination has some last coin and all are tried. Going upward means the smaller amount may already use the same coin, which is allowed.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] best = new int[amount + 1];
        Arrays.fill(best, amount + 1);
        best[0] = 0;
        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) best[a] = Math.min(best[a], best[a - coin] + 1);
        }
        return best[amount] > amount ? -1 : best[amount];
    }
}
```

### Code walkthrough
- `amount + 1`: More coins than any real answer, and safe to add to.
- `best[a - coin] + 1`: Use this coin as the last one.
- `int a = coin`: Amounts below the coin can't use it, so start there.

### Complexity
- **Time:** O(amount·coins)
- **Space:** O(amount)

### Common mistakes
1. **The largest coin first fails for 1,3,4 and 6.** Fix: try every coin.
2. **MAX_VALUE plus one overflows.** Fix: use amount+1 as infinity.
3. **Looping backwards forbids reuse.** Fix: loop amounts upward.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `[1], 0` | `0` |
| None | `[2], 3` | `-1` |
| Greedy fails | `[1,3,4], 6` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[251 — 518. Coin Change II]]: Add. The key idea there: Fixing the order of coin types gives every combination a single representation.
- [[243 — 279. Perfect Squares]]: Given coins. The key idea there: Removing the last square leaves a smaller total that is already solved.

### In a real interview
Say it in this order:

1. **Brute force:** Try every multiset of coins, which is exponential.
2. **Insight:** Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.
3. **Approach in a few words:** Unbounded knapsack.
4. **Cost:** time O(amount·coins); space O(amount).

## 5. Recall
Q: 322. Coin Change: which pattern, and what is the insight?
A: Unbounded knapsack. Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.

Q: 322. Coin Change: what is the analogy, and what does it stand for?
A: A price list for every amount: list → dp; last coin → transition.

Q: 322. Coin Change: What is coin-change state?
A: Fewest coins for amount a.

Q: 322. Coin Change: what are the time and space costs?
A: Time O(amount·coins). Space O(amount).

@@ 3 | TOPIC | 237 — 152. Maximum Product Subarray | Medium · Max and min state | MUST
# 237 — 152. Maximum Product Subarray

**Problem 237 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Max and min state**

[Open on LeetCode](https://leetcode.com/problems/maximum-product-subarray/)

## 1. Problem Statement
You get an integer array, possibly with negatives and zeros. Return the largest product of a contiguous non-empty subarray.

### Examples
```text
Input:  nums = [-2,0,-1]
Output: 0
```

### Constraints
```text
1 <= nums.length <= 2 * 10^4
-10 <= nums[i] <= 10
The product of any subarray of nums is guaranteed to fit in a 32-bit integer.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A bank where a stamp flips debt into wealth
A strange bank multiplies your balance. A negative stamp turns the deepest debt into the biggest fortune and the reverse. So you track both the richest and the poorest balance ending here.

| In the story | In the solution |
|---|---|
| richest | high |
| debt | low |

### Pattern signal
**Max and min state.** The next largest product can only come from the current largest, the current smallest or a fresh start.

### Thought process
1. **First idea:** Compute every range's product, in O(n²).
2. **The insight:** The next largest product can only come from the current largest, the current smallest or a fresh start.

### Optimal approach
1. Seed both extremes with the first value.
2. Swap them for a negative.
3. Extend or restart each.
4. Record the maximum.

### Dry run
```text
2,3,-2,4
x   high  low
2   2     2
3   6     3
-2  -2    -12
4   4     -48
best 6
```

### Why it works
Multiplying by a fixed number sends extremes to extremes, so nothing between them can ever become the best.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxProduct(int[] nums) {
        int high = nums[0], low = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            if (x < 0) {
                int temp = high;
                high = low;
                low = temp;
            }
            high = Math.max(x, high * x);
            low = Math.min(x, low * x);
            best = Math.max(best, high);
        }
        return best;
    }
}
```

### Code walkthrough
- `high = low`: The debt becomes the fortune.
- `Math.max(x, high * x)`: Continue the range or start again here.
- `low * x`: Keep the debt for a future negative stamp.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Keeping only the maximum loses sign flips.** Fix: keep the minimum too.
2. **Starting at 1 breaks a single negative.** Fix: seed with the first value.
3. **A zero ends every product.** Fix: allow restarting at each value.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[-2]` | `-2` |
| Pair | `[-2,-3]` | `6` |
| Zero | `[-2,0,-1]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[277 — 53. Maximum Subarray]]: One state. The key idea there: The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.
- [[019 — 238. Product of Array Except Self]]: All but one. The key idea there: Everything except me is simply everything left of me times everything right of me.

### In a real interview
Say it in this order:

1. **Brute force:** Compute every range's product, in O(n²).
2. **Insight:** The next largest product can only come from the current largest, the current smallest or a fresh start.
3. **Approach in a few words:** Two-state Kadane.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 152. Maximum Product Subarray: which pattern, and what is the insight?
A: Max and min state. The next largest product can only come from the current largest, the current smallest or a fresh start.

Q: 152. Maximum Product Subarray: what is the analogy, and what does it stand for?
A: A bank where a stamp flips debt into wealth: richest → high; debt → low.

Q: 152. Maximum Product Subarray: Why track minimum product?
A: A negative can make it the maximum.

Q: 152. Maximum Product Subarray: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 238 — 139. Word Break | Medium · Segmentation DP | MUST
# 238 — 139. Word Break

**Problem 238 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Segmentation DP**

[Open on LeetCode](https://leetcode.com/problems/word-break/)

## 1. Problem Statement
You get a string `s` and a dictionary. Return true if the whole of s can be cut into one or more dictionary words. A word may be reused.

### Examples
```text
Input:  s = "leetcode", wordDict = ["leet","code"]
Output: true

Input:  s = "applepenapple", wordDict = ["apple","pen"]
Output: true

Input:  s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]
Output: false
```

### Constraints
```text
1 <= s.length <= 300
1 <= wordDict.length <= 1000
1 <= wordDict[i].length <= 20
s and wordDict[i] consist of only lowercase English letters.
All the strings of wordDict are unique.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Lily pads
The string is a pond and each dictionary word a jump. A lily pad is safe if some jump from it lands on a safe pad. Label pads starting from the far bank and read the label of the first.

| In the story | In the solution |
|---|---|
| safe | true |
| jump | word |

### Pattern signal
**Segmentation DP.** Whether a suffix can be split doesn't depend on how you reached it.

### Thought process
1. **First idea:** Try every split recursively, repeating suffixes exponentially often.
2. **The insight:** Whether a suffix can be split doesn't depend on how you reached it.

### Optimal approach
1. Mark the end safe.
2. Go backwards.
3. Mark a pad if a word matches and lands safely.
4. Return pad zero.

### Dry run
```text
s = "leetcode", words leet and code
pad i   word starting here  lands on  safe?
8       empty suffix        -         true
7,6,5   none                -         false
4       code                8         true
3,2,1   none                -         false
0       leet                4         true, return it
```

### Why it works
A valid split has a first word followed by a valid split of the rest. Going backwards every rest is already known.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        boolean[] safe = new boolean[s.length() + 1];
        safe[s.length()] = true;
        for (int i = s.length() - 1; i >= 0; i--) {
            for (String word : wordDict) {
                if (s.startsWith(word, i) && safe[i + word.length()]) {
                    safe[i] = true;
                    break;
                }
            }
        }
        return safe[0];
    }
}
```

### Code walkthrough
- `safe[s.length()] = true`: The empty suffix is already complete.
- `s.startsWith(word, i)`: The word really appears at this pad.
- `safe[i + word.length()]`: The jump must land on a safe pad.

### Complexity
- **Time:** O(n·W·L)
- **Space:** O(n)

### Common mistakes
1. **The longest word first can reach a dead end.** Fix: try every word.
2. **An unreachable base makes all false.** Fix: set the empty suffix true.
3. **Words may repeat.** Fix: never mark them used.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Reuse | `aa, [a]` | `true` |
| None | `ab, [a]` | `false` |
| Whole | `a, [a]` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[235 — 91. Decode Ways]]: Count. The key idea there: The final code uses one digit or two, and only valid choices pass their count on.
- [[194 — 131. Palindrome Partitioning]]: Output. The key idea there: An invalid first piece rules out every way of cutting what follows, so test before recursing.

### In a real interview
Say it in this order:

1. **Brute force:** Try every split recursively, repeating suffixes exponentially often.
2. **Insight:** Whether a suffix can be split doesn't depend on how you reached it.
3. **Approach in a few words:** Suffix feasibility DP.
4. **Cost:** time O(n·W·L); space O(n).

## 5. Recall
Q: 139. Word Break: which pattern, and what is the insight?
A: Segmentation DP. Whether a suffix can be split doesn't depend on how you reached it.

Q: 139. Word Break: what is the analogy, and what does it stand for?
A: Lily pads: safe → true; jump → word.

Q: 139. Word Break: What is word-break state?
A: Whether the suffix at i can be split.

Q: 139. Word Break: what are the time and space costs?
A: Time O(n·W·L). Space O(n).

@@ 3 | TOPIC | 239 — 300. Longest Increasing Subsequence | Medium · Patience sorting | MUST
# 239 — 300. Longest Increasing Subsequence

**Problem 239 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Patience sorting**

[Open on LeetCode](https://leetcode.com/problems/longest-increasing-subsequence/)

## 1. Problem Statement
You get an integer array. Return the length of its longest strictly increasing subsequence, which keeps order but may skip elements. Follow-up: O(n log n).

### Examples
```text
Input:  nums = [10,9,2,5,3,7,101,18]
Output: 4

Input:  nums = [0,1,0,3,2,3]
Output: 4

Input:  nums = [7,7,7,7,7,7,7]
Output: 1
```

### Constraints
```text
1 <= nums.length <= 2500
-10^4 <= nums[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Patience card piles
Deal cards onto piles, placing each on the first top at least as large, or a new pile. A smaller top is kinder to future cards. The number of piles is the answer, though the tops are not the sequence.

| In the story | In the solution |
|---|---|
| tops | tails |
| leftmost | lower bound |

### Pattern signal
**Patience sorting.** For each length only the smallest possible last value matters, and those values are sorted.

### Thought process
1. **First idea:** For each index scan all earlier ones, in O(n²).
2. **The insight:** For each length only the smallest possible last value matters, and those values are sorted.

### Optimal approach
1. Binary-search the first tail at least x.
2. Replace it.
3. Extend when none exists.
4. Return the count.

### Dry run
```text
10,9,2,5,3,7
x   tops
10  10
9   9
2   2
5   2,5
3   2,3
7   2,3,7
```

### Why it works
A tail at position k is always the smallest end of any increasing subsequence of length k+1, so a longer one exists exactly when a value exceeds the last tail.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int x : nums) {
            int low = 0, high = size;
            while (low < high) {
                int mid = (low + high) >>> 1;
                if (tails[mid] < x) low = mid + 1;
                else high = mid;
            }
            tails[low] = x;
            if (low == size) size++;
        }
        return size;
    }
}
```

### Code walkthrough
- `tails[mid] < x`: Find the first tail that is not smaller.
- `tails[low] = x`: A kinder tail for this length.
- `size++`: This value extends the longest so far.

### Complexity
- **Time:** O(n log n)
- **Space:** O(n)

### Common mistakes
1. **The tails array is not the subsequence.** Fix: return only its size.
2. **Upper bound allows equal values.** Fix: use lower bound.
3. **Requiring adjacency solves another problem.** Fix: allow skips.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Equal | `[7,7]` | `1` |
| Falling | `[3,2]` | `1` |
| Rising | `[1,2]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[246 — 673. Number of Longest Increasing Subsequence]]: More state. The key idea there: Grouping by predecessor counts each subsequence once, provided each state stores length and ways.
- [[259 — 329. Longest Increasing Path in a Matrix]]: DAG. The key idea there: Strict increase makes the grid a DAG, where longest paths can be memoized per cell.

### In a real interview
Say it in this order:

1. **Brute force:** For each index scan all earlier ones, in O(n²).
2. **Insight:** For each length only the smallest possible last value matters, and those values are sorted.
3. **Approach in a few words:** Smallest tail per length.
4. **Cost:** time O(n log n); space O(n).

## 5. Recall
Q: 300. Longest Increasing Subsequence: which pattern, and what is the insight?
A: Patience sorting. For each length only the smallest possible last value matters, and those values are sorted.

Q: 300. Longest Increasing Subsequence: what is the analogy, and what does it stand for?
A: Patience card piles: tops → tails; leftmost → lower bound.

Q: 300. Longest Increasing Subsequence: What do LIS tails store?
A: The smallest tail for each length.

Q: 300. Longest Increasing Subsequence: what are the time and space costs?
A: Time O(n log n). Space O(n).

@@ 3 | TOPIC | 240 — 416. Partition Equal Subset Sum | Medium · 0/1 knapsack | MUST
# 240 — 416. Partition Equal Subset Sum

**Problem 240 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: 0/1 knapsack**

[Open on LeetCode](https://leetcode.com/problems/partition-equal-subset-sum/)

## 1. Problem Statement
You get an array of positive integers. Return true if it can be divided into two subsets with equal sums, using each element exactly once.

### Examples
```text
Input:  nums = [1,5,11,5]
Output: true

Input:  nums = [1,2,3,5]
Output: false
```

### Constraints
```text
1 <= nums.length <= 200
1 <= nums[i] <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Filling one suitcase to exactly half
Fill a suitcase to exactly half the total weight. Keep which weights are reachable. When a new item arrives update the heavy weights first, or that one item gets packed twice.

| In the story | In the solution |
|---|---|
| reachable | boolean |
| heavy first | backwards |

### Pattern signal
**0/1 knapsack.** If one part reaches half the total the other does automatically, so this is a subset-sum question.

### Thought process
1. **First idea:** Try every subset.
2. **The insight:** If one part reaches half the total the other does automatically, so this is a subset-sum question.

### Optimal approach
1. Reject an odd total.
2. Mark zero reachable.
3. Per number update downward.
4. Read the half.

### Dry run
```text
nums = [1,5,11,5], half = 11
number  reachable sums afterwards
start   0
1       0,1
5       0,1,5,6
11      0,1,5,6,11
5       0,1,5,6,10,11
11 is reachable, so true
```

### Why it works
After each number exactly the subset sums of the numbers so far are marked, because a downward update reads only values from before this number.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % 2 != 0) return false;
        boolean[] can = new boolean[sum / 2 + 1];
        can[0] = true;
        for (int x : nums) {
            for (int s = can.length - 1; s >= x; s--) can[s] = can[s] || can[s - x];
        }
        return can[sum / 2];
    }
}
```

### Code walkthrough
- `sum % 2 != 0`: An odd total can't be halved.
- `s--`: Going down uses this item once.
- `can[s - x]`: Reachable by adding this item.

### Complexity
- **Time:** O(n·sum)
- **Space:** O(sum)

### Common mistakes
1. **Looping upward reuses numbers.** Fix: loop sums downward.
2. **A sorted split can miss answers.** Fix: search subset sums.
3. **Without a true base nothing is reachable.** Fix: set zero true.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `false` |
| Pair | `[2,2]` | `true` |
| No reuse | `[2,4]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[252 — 494. Target Sum]]: Count. The key idea there: The index and the running sum completely determine how many ways remain.
- [[197 — 698. Partition to K Equal Sum Subsets]]: Backtrack. The key idea there: Equal buckets are interchangeable and large values constrain earliest, so prune both.

### In a real interview
Say it in this order:

1. **Brute force:** Try every subset.
2. **Insight:** If one part reaches half the total the other does automatically, so this is a subset-sum question.
3. **Approach in a few words:** Backward 0/1 knapsack.
4. **Cost:** time O(n·sum); space O(sum).

## 5. Recall
Q: 416. Partition Equal Subset Sum: which pattern, and what is the insight?
A: 0/1 knapsack. If one part reaches half the total the other does automatically, so this is a subset-sum question.

Q: 416. Partition Equal Subset Sum: what is the analogy, and what does it stand for?
A: Filling one suitcase to exactly half: reachable → boolean; heavy first → backwards.

Q: 416. Partition Equal Subset Sum: Why loop sums downward?
A: To use each item once.

Q: 416. Partition Equal Subset Sum: what are the time and space costs?
A: Time O(n·sum). Space O(sum).

@@ 3 | TOPIC | Practice — Review 40 | Re-solve without looking | MUST
# Review 40

Solve on a blank page. No notes, hints or solutions.

## 416. Partition Equal Subset Sum
Split values into two equal sums.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/partition-equal-subset-sum/)

## 152. Maximum Product Subarray
Find the largest subarray product.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/maximum-product-subarray/)

## 127. Word Ladder
Find the shortest one-letter transformation chain.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/word-ladder/)

## 733. Flood Fill
Recolor a connected region.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/flood-fill/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 416 | | | | |
| 152 | | | | |
| 127 | | | | |
| 733 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[240 — 416. Partition Equal Subset Sum]]
- [[237 — 152. Maximum Product Subarray]]
- [[226 — 127. Word Ladder]]
- [[202 — 733. Flood Fill]]

@@ 3 | TOPIC | 241 — 120. Triangle | Medium · Bottom-up path | SHOULD
# 241 — 120. Triangle

**Problem 241 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Bottom-up path**

[Open on LeetCode](https://leetcode.com/problems/triangle/)

## 1. Problem Statement
You get a triangle of numbers. Moving from row to row you may step to the same index or the next one. Return the smallest path sum from top to bottom.

### Examples
```text
Input:  triangle = [[2],[3,4],[6,5,7],[4,1,8,3]]
Output: 11

Input:  triangle = [[-10]]
Output: -10
```

### Constraints
```text
1 <= triangle.length <= 200
triangle[0].length == 1
triangle[i].length == triangle[i - 1].length + 1
-10^4 <= triangle[i][j] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Scouts reporting from the valley
Scouts stand in the valley and report upward. At every fork the hiker takes the cheaper of the two reports below and adds their own toll, so the summit hears the cheapest total.

| In the story | In the solution |
|---|---|
| report | dp |
| fork | cell |

### Pattern signal
**Bottom-up path.** The best continuation from a cell is independent of the path taken to it.

### Thought process
1. **First idea:** Try all 2^n paths.
2. **The insight:** The best continuation from a cell is independent of the path taken to it.

### Optimal approach
1. Start with a row of zeros below.
2. Move upward.
3. Add each cell to the smaller child.
4. Return the top.

### Dry run
```text
rows 2 / 3,4 / 6,5,7 / 4,1,8,3
row  reports
3    4,1,8,3
2    7,6,10
1    9,10
0    11
```

### Why it works
A path from a cell is the cell plus a best path from one of its two children, both already computed.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[] best = new int[n + 1];
        for (int r = n - 1; r >= 0; r--) {
            for (int c = 0; c <= r; c++) {
                best[c] = triangle.get(r).get(c) + Math.min(best[c], best[c + 1]);
            }
        }
        return best[0];
    }
}
```

### Code walkthrough
- `n + 1`: An imaginary free floor removes the last-row special case.
- `best[c + 1]`: Still the lower row's value when read left to right.
- `best[0]`: The summit's report is the answer.

### Complexity
- **Time:** O(n²)
- **Space:** O(n)

### Common mistakes
1. **The smaller child can hide a costly path.** Fix: combine from the bottom.
2. **Updating columns backwards overwrites inputs.** Fix: go left to right.
3. **Negatives make partial pruning invalid.** Fix: compute every cell.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [-1] ]` | `-1` |
| Two | `[ [1],[2,3] ]` | `3` |
| Negative | `[ [1],[2,-3] ]` | `-2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[254 — 64. Minimum Path Sum]]: Square. The key idea there: The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.
- [[247 — 118. Pascal's Triangle]]: Down. The key idea there: Every inner cell is the sum of two cells that are already built.

### In a real interview
Say it in this order:

1. **Brute force:** Try all 2^n paths.
2. **Insight:** The best continuation from a cell is independent of the path taken to it.
3. **Approach in a few words:** Bottom-up minimum.
4. **Cost:** time O(n²); space O(n).

## 5. Recall
Q: 120. Triangle: which pattern, and what is the insight?
A: Bottom-up path. The best continuation from a cell is independent of the path taken to it.

Q: 120. Triangle: what is the analogy, and what does it stand for?
A: Scouts reporting from the valley: report → dp; fork → cell.

Q: 120. Triangle: Why bottom-up?
A: Each cell needs only finished children, without edge cases.

Q: 120. Triangle: what are the time and space costs?
A: Time O(n²). Space O(n).

@@ 3 | TOPIC | 242 — 740. Delete and Earn | Medium · Value take or skip | SHOULD
# 242 — 740. Delete and Earn

**Problem 242 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Value take or skip**

[Open on LeetCode](https://leetcode.com/problems/delete-and-earn/)

## 1. Problem Statement
Repeatedly pick a value v from the array to earn v points, deleting that element, after which every element equal to v-1 or v+1 must also be deleted without earning. Return the most points possible.

### Examples
```text
Input:  nums = [3,4,2]
Output: 6

Input:  nums = [2,2,3,3,3,4]
Output: 9
```

### Constraints
```text
1 <= nums.length <= 2 * 10^4
1 <= nums[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A street named by value
Build a street where house v holds all the points of value v. Taking house v deletes houses v-1 and v+1, so this is House Robber on that street.

| In the story | In the solution |
|---|---|
| house | bucket |
| street | value range |

### Pattern signal
**Value take or skip.** If you take one copy of v you may as well take them all, so decisions are per distinct value.

### Thought process
1. **First idea:** Try every deletion order.
2. **The insight:** If you take one copy of v you may as well take them all, so decisions are per distinct value.

### Optimal approach
1. Add each value into its bucket.
2. Walk buckets in value order.
3. Choose skip or take with two back.

### Dry run
```text
2,2,3,3,3,4
value  bucket  best
2      4       4
3      9       9
4      4       9
```

### Why it works
Valid choices are exactly sets of values without two consecutive ones, worth the bucket totals.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int deleteAndEarn(int[] nums) {
        int[] bucket = new int[10001];
        for (int x : nums) bucket[x] += x;
        int twoBack = 0, oneBack = 0;
        for (int total : bucket) {
            int now = Math.max(oneBack, twoBack + total);
            twoBack = oneBack;
            oneBack = now;
        }
        return oneBack;
    }
}
```

### Code walkthrough
- `bucket[x] += x`: Pour equal values into one house.
- `int total : bucket`: Missing values are houses holding nothing.
- `twoBack + total`: Take this house plus the best two back.

### Complexity
- **Time:** O(n + 10001)
- **Space:** O(10001)

### Common mistakes
1. **Adjacency is by value rather than index.** Fix: build value buckets.
2. **Taking one copy wastes points.** Fix: total equal values.
3. **The largest value first can lose.** Fix: take or skip.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[2]` | `2` |
| Gap | `[1,3]` | `4` |
| Near | `[3,4]` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[231 — 198. House Robber]]: Reduced to. The key idea there: The last house is either skipped or taken, and each case leaves one smaller, solved problem.
- [[240 — 416. Partition Equal Subset Sum]]: Value range. The key idea there: If one part reaches half the total the other does automatically, so this is a subset-sum question.

### In a real interview
Say it in this order:

1. **Brute force:** Try every deletion order.
2. **Insight:** If you take one copy of v you may as well take them all, so decisions are per distinct value.
3. **Approach in a few words:** Reduce to robber.
4. **Cost:** time O(n + 10001); space O(10001).

## 5. Recall
Q: 740. Delete and Earn: which pattern, and what is the insight?
A: Value take or skip. If you take one copy of v you may as well take them all, so decisions are per distinct value.

Q: 740. Delete and Earn: what is the analogy, and what does it stand for?
A: A street named by value: house → bucket; street → value range.

Q: 740. Delete and Earn: What is a house in delete-and-earn?
A: The total of one value.

Q: 740. Delete and Earn: what are the time and space costs?
A: Time O(n + 10001). Space O(10001).

@@ 3 | TOPIC | 243 — 279. Perfect Squares | Medium · Unbounded knapsack | SHOULD
# 243 — 279. Perfect Squares

**Problem 243 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Unbounded knapsack**

[Open on LeetCode](https://leetcode.com/problems/perfect-squares/)

## 1. Problem Statement
You get an integer n. Return the fewest perfect squares, such as 1, 4, 9 and 16, with repetition allowed, whose sum is exactly n.

### Examples
```text
Input:  n = 12
Output: 3

Input:  n = 13
Output: 2
```

### Constraints
```text
1 <= n <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A shop with square coins
It is the coin shop again, except the coins are 1, 4, 9, 16 and so on. The fewest for each total comes from trying every square as the last coin.

| In the story | In the solution |
|---|---|
| coins | squares |
| list | dp |

### Pattern signal
**Unbounded knapsack.** Removing the last square leaves a smaller total that is already solved.

### Thought process
1. **First idea:** Try every combination of squares.
2. **The insight:** Removing the last square leaves a smaller total that is already solved.

### Optimal approach
1. For each total start from all ones.
2. Try each square that fits.
3. Take one plus the remainder's answer.

### Dry run
```text
12
n   fewest
4   1
8   2
12  3
```

### Why it works
Some square is last in every optimal sum and all candidates are compared, so each entry is optimal by induction.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numSquares(int n) {
        int[] best = new int[n + 1];
        for (int total = 1; total <= n; total++) {
            best[total] = total; // all ones
            for (int root = 1; root * root <= total; root++) {
                best[total] = Math.min(best[total], best[total - root * root] + 1);
            }
        }
        return best[n];
    }
}
```

### Code walkthrough
- `best[total] = total`: The all-ones answer is always possible.
- `root * root <= total`: Only squares that fit this total.
- `+ 1`: Add this square as the last coin.

### Complexity
- **Time:** O(n·√n)
- **Space:** O(n)

### Common mistakes
1. **The largest square first fails at 12.** Fix: try every square.
2. **Trying only near the root misses answers.** Fix: loop all roots.
3. **Squares may repeat.** Fix: this is unbounded.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `1` |
| Square | `16` | `1` |
| Thirteen | `13` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[236 — 322. Coin Change]]: Given. The key idea there: Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.
- [[245 — 343. Integer Break]]: Product. The key idea there: Products are maximized by threes, with a final 2 or 4 where needed.

### In a real interview
Say it in this order:

1. **Brute force:** Try every combination of squares.
2. **Insight:** Removing the last square leaves a smaller total that is already solved.
3. **Approach in a few words:** Coins that are squares.
4. **Cost:** time O(n·√n); space O(n).

## 5. Recall
Q: 279. Perfect Squares: which pattern, and what is the insight?
A: Unbounded knapsack. Removing the last square leaves a smaller total that is already solved.

Q: 279. Perfect Squares: what is the analogy, and what does it stand for?
A: A shop with square coins: coins → squares; list → dp.

Q: 279. Perfect Squares: What are the coins in Perfect Squares?
A: All squares up to n.

Q: 279. Perfect Squares: what are the time and space costs?
A: Time O(n·√n). Space O(n).

@@ 3 | TOPIC | 244 — 1856. Maximum Subarray Min-Product | Medium · Stack and prefix | SHOULD
# 244 — 1856. Maximum Subarray Min-Product

**Problem 244 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Stack and prefix**

[Open on LeetCode](https://leetcode.com/problems/maximum-subarray-min-product/)

## 1. Problem Statement
An array's min-product is its minimum times its sum. Return the largest min-product of any non-empty contiguous subarray. Maximize first, then return the result modulo 1,000,000,007.

### Examples
```text
Input:  nums = [1,2,3,2]
Output: 14

Input:  nums = [2,3,3,1,2]
Output: 18

Input:  nums = [3,1,5,6,4,2]
Output: 60
```

### Constraints
```text
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^7
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A shortest person holding a banner
Each person holds a banner as the shortest of their group. Their group can stretch until someone shorter blocks it. A stack finds the blockers and prefix sums give the group's total at once.

| In the story | In the solution |
|---|---|
| group | stack bounds |
| total | prefix |

### Pattern signal
**Stack and prefix.** All values are positive, so for a fixed minimum the widest range has the largest sum.

### Thought process
1. **First idea:** Evaluate every range while tracking minimum and sum, in O(n²).
2. **The insight:** All values are positive, so for a fixed minimum the widest range has the largest sum.

### Optimal approach
1. Build long prefix sums.
2. Keep an increasing index stack.
3. Evaluate popped minimums.
4. Flush at the end.
5. Take the modulo last.

### Dry run
```text
1,2,3,2
min  range    product
3    3        9
2    2,3,2    14
1    all      8
```

### Why it works
Every possible minimum is popped once, with exactly the range between its smaller blockers, which is its best range.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxSumMinProduct(int[] nums) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
        Deque<Integer> stack = new ArrayDeque<>();
        long best = 0;
        for (int i = 0; i <= n; i++) {
            while (!stack.isEmpty() && (i == n || nums[stack.peek()] > nums[i])) {
                int min = nums[stack.pop()];
                int from = stack.isEmpty() ? 0 : stack.peek() + 1;
                best = Math.max(best, min * (prefix[i] - prefix[from]));
            }
            if (i < n) stack.push(i);
        }
        return (int) (best % 1_000_000_007);
    }
}
```

### Code walkthrough
- `i == n`: The imaginary end removes everyone left.
- `stack.peek() + 1`: The group begins after the shorter one on the left.
- `prefix[i] - prefix[from]`: The group's total in O(1).

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Taking the modulo early breaks comparisons.** Fix: apply it at the end.
2. **Int products overflow.** Fix: use long.
3. **Width is not the multiplier.** Fix: use the range's sum.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[3]` | `9` |
| Equal | `[2,2]` | `8` |
| Rising | `[1,5]` | `25` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[087 — 84. Largest Rectangle in Histogram]]: Width. The key idea there: An increasing stack knows both first smaller neighbors at the moment of popping.
- [[010 — 303. Range Sum Query - Immutable]]: Sums. The key idea there: The array is frozen, so prepare the totals once.

### In a real interview
Say it in this order:

1. **Brute force:** Evaluate every range while tracking minimum and sum, in O(n²).
2. **Insight:** All values are positive, so for a fixed minimum the widest range has the largest sum.
3. **Approach in a few words:** Histogram stack with prefix.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 1856. Maximum Subarray Min-Product: which pattern, and what is the insight?
A: Stack and prefix. All values are positive, so for a fixed minimum the widest range has the largest sum.

Q: 1856. Maximum Subarray Min-Product: what is the analogy, and what does it stand for?
A: A shortest person holding a banner: group → stack bounds; total → prefix.

Q: 1856. Maximum Subarray Min-Product: What range is used per minimum?
A: The widest, between smaller values.

Q: 1856. Maximum Subarray Min-Product: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 245 — 343. Integer Break | Medium · Product of threes | SHOULD
# 245 — 343. Integer Break

**Problem 245 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Product of threes**

[Open on LeetCode](https://leetcode.com/problems/integer-break/)

## 1. Problem Statement
Break the integer n into a sum of at least two positive integers so that their product is as large as possible, and return that product.

### Examples
```text
Input:  n = 2
Output: 1

Input:  n = 10
Output: 36
```

### Constraints
```text
2 <= n <= 58
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Cutting rope into threes
Cut a rope to multiply piece lengths. Any piece of five or more improves by cutting a three off it. Just never end with a piece of one: a four is better kept as it is.

| In the story | In the solution |
|---|---|
| threes | factor |
| last | up to four |

### Pattern signal
**Product of threes.** Products are maximized by threes, with a final 2 or 4 where needed.

### Thought process
1. **First idea:** For each length try every first cut, which is an O(n²) DP.
2. **The insight:** Products are maximized by threes, with a final 2 or 4 where needed.

### Optimal approach
1. Return n-1 for 2 and 3.
2. Take threes while more than four remain.
3. Multiply by what is left.

### Dry run
```text
10
left  product
7     3
4     9
end   36
```

### Why it works
For x at least 5, 3(x-3) exceeds x, two threes beat three twos, and a factor of one is wasted, so no other multiset is better.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int integerBreak(int n) {
        if (n < 4) return n - 1; // forced cut
        int product = 1;
        while (n > 4) {
            product *= 3;
            n -= 3;
        }
        return product * n;
    }
}
```

### Code walkthrough
- `n - 1`: These must be cut even though it loses.
- `n > 4`: A remaining four is already best.
- `product * n`: Multiply in the final piece.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **2 and 3 must still be split.** Fix: return n-1 for them.
2. **A final 1 wastes a factor.** Fix: stop at 4.
3. **Halving is not optimal.** Fix: prefer threes.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two | `2` | `1` |
| Four | `4` | `4` |
| Six | `6` | `9` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[243 — 279. Perfect Squares]]: DP. The key idea there: Removing the last square leaves a smaller total that is already solved.
- [[158 — 96. Unique Binary Search Trees]]: Products. The key idea there: The number of shapes depends only on how many nodes a subtree has.

### In a real interview
Say it in this order:

1. **Brute force:** For each length try every first cut, which is an O(n²) DP.
2. **Insight:** Products are maximized by threes, with a final 2 or 4 where needed.
3. **Approach in a few words:** Give the DP, then the threes proof.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 343. Integer Break: which pattern, and what is the insight?
A: Product of threes. Products are maximized by threes, with a final 2 or 4 where needed.

Q: 343. Integer Break: what is the analogy, and what does it stand for?
A: Cutting rope into threes: threes → factor; last → up to four.

Q: 343. Integer Break: Why threes?
A: Larger pieces improve by splitting off a three.

Q: 343. Integer Break: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 246 — 673. Number of Longest Increasing Subsequence | Medium · Length and count | SHOULD
# 246 — 673. Number of Longest Increasing Subsequence

**Problem 246 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Length and count**

[Open on LeetCode](https://leetcode.com/problems/number-of-longest-increasing-subsequence/)

## 1. Problem Statement
You get an integer array. Return how many different strictly increasing subsequences have the maximum possible length. Selections of different indices count separately.

### Examples
```text
Input:  nums = [1,3,5,4,7]
Output: 2

Input:  nums = [2,2,2,2,2]
Output: 5
```

### Constraints
```text
1 <= nums.length <= 2000
-10^6 <= nums[i] <= 10^6
The answer is guaranteed to fit inside a 32-bit integer.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Record holders with a tally
Every record holder also keeps a tally of how many ways achieve the record. A longer record replaces the tally, an equal one adds the predecessor's tally to it.

| In the story | In the solution |
|---|---|
| record | length |
| tally | ways |

### Pattern signal
**Length and count.** Grouping by predecessor counts each subsequence once, provided each state stores length and ways.

### Thought process
1. **First idea:** List every increasing subsequence.
2. **The insight:** Grouping by predecessor counts each subsequence once, provided each state stores length and ways.

### Optimal approach
1. Start every index at length one and one way.
2. Scan smaller predecessors.
3. Reset on longer and add on equal.
4. Sum endings of maximum length.

### Dry run
```text
1,3,5,4,7
x  length  ways
1  1       1
3  2       1
5  3       1
4  3       1
7  4       2
```

### Why it works
Subsequences ending here have exactly one predecessor index, so adding over the predecessors achieving the best length counts each once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int findNumberOfLIS(int[] nums) {
        int n = nums.length;
        int[] length = new int[n], ways = new int[n];
        int longest = 0, total = 0;
        for (int i = 0; i < n; i++) {
            length[i] = ways[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] >= nums[i]) continue;
                if (length[j] + 1 > length[i]) {
                    length[i] = length[j] + 1;
                    ways[i] = ways[j];
                } else if (length[j] + 1 == length[i]) {
                    ways[i] += ways[j];
                }
            }
            if (length[i] > longest) {
                longest = length[i];
                total = ways[i];
            } else if (length[i] == longest) {
                total += ways[i];
            }
        }
        return total;
    }
}
```

### Code walkthrough
- `ways[i] = ways[j]`: A longer record discards old ways.
- `ways[i] += ways[j]`: Another route to the same record.
- `total +=`: Different endings of the longest length add up.

### Complexity
- **Time:** O(n²)
- **Space:** O(n)

### Common mistakes
1. **A tie adds all predecessor ways.** Fix: add ways rather than 1.
2. **Several endings can share the maximum.** Fix: sum them.
3. **Equal values are not increasing.** Fix: require strictly smaller.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Same | `[2,2]` | `2` |
| One | `[1]` | `1` |
| Two | `[1,3,2]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[239 — 300. Longest Increasing Subsequence]]: Faster. The key idea there: For each length only the smallest possible last value matters, and those values are sorted.
- [[260 — 115. Distinct Subsequences]]: Matching. The key idea there: Each character is either used for the current target position or not, and those counts add.

### In a real interview
Say it in this order:

1. **Brute force:** List every increasing subsequence.
2. **Insight:** Grouping by predecessor counts each subsequence once, provided each state stores length and ways.
3. **Approach in a few words:** Length and count together.
4. **Cost:** time O(n²); space O(n).

## 5. Recall
Q: 673. Number of Longest Increasing Subsequence: which pattern, and what is the insight?
A: Length and count. Grouping by predecessor counts each subsequence once, provided each state stores length and ways.

Q: 673. Number of Longest Increasing Subsequence: what is the analogy, and what does it stand for?
A: Record holders with a tally: record → length; tally → ways.

Q: 673. Number of Longest Increasing Subsequence: What happens on equal LIS length?
A: Add the predecessor's ways.

Q: 673. Number of Longest Increasing Subsequence: what are the time and space costs?
A: Time O(n²). Space O(n).

@@ 3 | TOPIC | Practice — Review 41 | Re-solve without looking | MUST
# Review 41

Solve on a blank page. No notes, hints or solutions.

## 673. Number of Longest Increasing Subsequence
Count longest increasing subsequences.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/number-of-longest-increasing-subsequence/)

## 279. Perfect Squares
Find fewest squares summing to n.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/perfect-squares/)

## 213. House Robber II
Rob houses arranged in a circle.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/house-robber-ii/)

## 130. Surrounded Regions
Capture regions surrounded by X.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/surrounded-regions/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 673 | | | | |
| 279 | | | | |
| 213 | | | | |
| 130 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[246 — 673. Number of Longest Increasing Subsequence]]
- [[243 — 279. Perfect Squares]]
- [[232 — 213. House Robber II]]
- [[208 — 130. Surrounded Regions]]

@@ 2 | MODULE | Dynamic Programming — 2D DP | 11 problems | MUST
# Dynamic Programming: 2D DP

Patterns in this group: Build from previous row, Grid count, Rolling grid count, Two-string DP, Combination knapsack, Index and sum, Two-prefix DP, Memoized path, Square DP, Minimax DP.

@@ 3 | TOPIC | 247 — 118. Pascal's Triangle | Easy · Build from previous row | NICE
# 247 — 118. Pascal's Triangle

**Problem 247 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Easy | Pattern: Build from previous row**

[Open on LeetCode](https://leetcode.com/problems/pascals-triangle/)

## 1. Problem Statement
Return the first `numRows` rows of Pascal's triangle, in which both edges are 1 and every other number is the sum of the two directly above it.

### Examples
```text
Input:  numRows = 5
Output: [[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]

Input:  numRows = 1
Output: [[1]]
```

### Constraints
```text
1 <= numRows <= 30
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A human pyramid
In a human pyramid each person carries the load of the two above them, and those on the edges carry one. You can build each row looking only at the previous one.

| In the story | In the solution |
|---|---|
| carry | sum |
| edge | 1 |

### Pattern signal
**Build from previous row.** Every inner cell is the sum of two cells that are already built.

### Thought process
1. **First idea:** Compute each cell with factorials, which overflow.
2. **The insight:** Every inner cell is the sum of two cells that are already built.

### Optimal approach
1. Create a new row.
2. Put one on the edges.
3. Add the two above for inner cells.
4. Store the row.

### Dry run
```text
4
row
1
1 1
1 2 1
1 3 3 1
```

### Why it works
The loop applies the triangle's definition row by row, and each row it reads is complete.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> rows = new ArrayList<>();
        for (int r = 0; r < numRows; r++) {
            List<Integer> row = new ArrayList<>();
            for (int c = 0; c <= r; c++) {
                if (c == 0 || c == r) row.add(1);
                else row.add(rows.get(r - 1).get(c - 1) + rows.get(r - 1).get(c));
            }
            rows.add(row);
        }
        return rows;
    }
}
```

### Code walkthrough
- `c == 0 || c == r`: The edges have only one parent.
- `rows.get(r - 1)`: Read the completed row above.
- `new ArrayList<>()`: Every row must be its own list.

### Complexity
- **Time:** O(n²)
- **Space:** O(n²) for the output

### Common mistakes
1. **Reusing one row object aliases all rows.** Fix: create a new one.
2. **Row r has r+1 values.** Fix: loop through r.
3. **The edges have one parent.** Fix: set them to 1 directly.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `[ [1] ]` |
| Two | `2` | `[ [1],[1,1] ]` |
| Three | `3` | `last 1,2,1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[248 — 62. Unique Paths]]: Rotated. The key idea there: The last move into a cell is from above or from the left, two separate groups of paths.
- [[241 — 120. Triangle]]: Minimum. The key idea there: The best continuation from a cell is independent of the path taken to it.

### In a real interview
Say it in this order:

1. **Brute force:** Compute each cell with factorials, which overflow.
2. **Insight:** Every inner cell is the sum of two cells that are already built.
3. **Approach in a few words:** Build from the previous row.
4. **Cost:** time O(n²); space O(n²) for the output.

## 5. Recall
Q: 118. Pascal's Triangle: which pattern, and what is the insight?
A: Build from previous row. Every inner cell is the sum of two cells that are already built.

Q: 118. Pascal's Triangle: what is the analogy, and what does it stand for?
A: A human pyramid: carry → sum; edge → 1.

Q: 118. Pascal's Triangle: How is an inner Pascal value made?
A: The sum of two above.

Q: 118. Pascal's Triangle: what are the time and space costs?
A: Time O(n²). Space O(n²) for the output.

@@ 3 | TOPIC | 248 — 62. Unique Paths | Medium · Grid count | MUST
# 248 — 62. Unique Paths

**Problem 248 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Grid count**

[Open on LeetCode](https://leetcode.com/problems/unique-paths/)

## 1. Problem Statement
A robot starts at the top-left of an m by n grid and can move only right or down. Return the number of different paths to the bottom-right.

### Examples
```text
Input:  m = 3, n = 7
Output: 28

Input:  m = 3, n = 2
Output: 3
```

### Constraints
```text
1 <= m, n <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Water through pipes
Water enters at the top-left and flows only right and down. At each junction the streams from above and from the left join. What reaches the last junction is the number of paths.

| In the story | In the solution |
|---|---|
| pour | add |
| tap | 1 |

### Pattern signal
**Grid count.** The last move into a cell is from above or from the left, two separate groups of paths.

### Thought process
1. **First idea:** Recursively explore every path.
2. **The insight:** The last move into a cell is from above or from the left, two separate groups of paths.

### Optimal approach
1. Fill the first row with ones.
2. For each later row add the left value.
3. Return the last.

### Dry run
```text
3 by 7
row  last cells
1    1
2    7
3    28
```

### Why it works
Every path into a cell has one final move, so the two counts add exactly, and one row holds both values needed.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int uniquePaths(int m, int n) {
        int[] ways = new int[n];
        Arrays.fill(ways, 1);
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) ways[c] += ways[c - 1];
        }
        return ways[n - 1];
    }
}
```

### Code walkthrough
- `Arrays.fill(ways, 1)`: Only one way along the top edge.
- `ways[c] += ways[c - 1]`: The old value is above, the new neighbor is left.
- `ways[n - 1]`: What flows out of the final junction.

### Complexity
- **Time:** O(m·n)
- **Space:** O(n)

### Common mistakes
1. **Zero borders make every count zero.** Fix: start them at 1.
2. **This is a count.** Fix: add rather than take a maximum.
3. **A backwards column loop reads old values.** Fix: go left to right.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1,1` | `1` |
| Line | `1,5` | `1` |
| Square | `2,2` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[249 — 63. Unique Paths II]]: Zero. The key idea there: Obstacles are simply cells with zero ways in the same recurrence.
- [[254 — 64. Minimum Path Sum]]: Minimum. The key idea there: The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.

### In a real interview
Say it in this order:

1. **Brute force:** Recursively explore every path.
2. **Insight:** The last move into a cell is from above or from the left, two separate groups of paths.
3. **Approach in a few words:** Rolling grid count.
4. **Cost:** time O(m·n); space O(n).

## 5. Recall
Q: 62. Unique Paths: which pattern, and what is the insight?
A: Grid count. The last move into a cell is from above or from the left, two separate groups of paths.

Q: 62. Unique Paths: what is the analogy, and what does it stand for?
A: Water through pipes: pour → add; tap → 1.

Q: 62. Unique Paths: What adds at a grid cell?
A: Ways from above and left.

Q: 62. Unique Paths: what are the time and space costs?
A: Time O(m·n). Space O(n).

@@ 3 | TOPIC | 249 — 63. Unique Paths II | Medium · Rolling grid count | SHOULD
# 249 — 63. Unique Paths II

**Problem 249 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Rolling grid count**

[Open on LeetCode](https://leetcode.com/problems/unique-paths-ii/)

## 1. Problem Statement
A robot moves only right or down from the top-left to the bottom-right of a grid in which 1 marks an obstacle. Return the number of paths that avoid every obstacle.

### Examples
```text
Input:  obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
Output: 2

Input:  obstacleGrid = [[0,1],[0,0]]
Output: 1
```

### Constraints
```text
m == obstacleGrid.length
n == obstacleGrid[i].length
1 <= m, n <= 100
obstacleGrid[i][j] is 0 or 1.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Pipes with closed valves
It is the same pipework with some valves shut. A shut valve holds no water and passes none on, so everything that depended on it alone dries up.

| In the story | In the solution |
|---|---|
| valve | obstacle |
| tap | ways zero |

### Pattern signal
**Rolling grid count.** Obstacles are simply cells with zero ways in the same recurrence.

### Thought process
1. **First idea:** Explore every path while avoiding obstacles.
2. **The insight:** Obstacles are simply cells with zero ways in the same recurrence.

### Optimal approach
1. Put one way at the start.
2. Set blocked cells to zero.
3. Add from the left elsewhere.
4. Return the last.

### Dry run
```text
centre blocked 3x3
row  ways
0    1,1,1
1    1,0,1
2    1,1,2
```

### Why it works
No path ends in an obstacle, and every other cell's paths arrive from exactly above or left.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int n = obstacleGrid[0].length;
        int[] ways = new int[n];
        ways[0] = 1;
        for (int[] row : obstacleGrid) {
            for (int c = 0; c < n; c++) {
                if (row[c] == 1) ways[c] = 0;
                else if (c > 0) ways[c] += ways[c - 1];
            }
        }
        return ways[n - 1];
    }
}
```

### Code walkthrough
- `ways[0] = 1`: The source, before checking the first cell.
- `ways[c] = 0`: A shut valve erases what came from above.
- `ways[c - 1]`: Water arriving from the left.

### Complexity
- **Time:** O(m·n)
- **Space:** O(n)

### Common mistakes
1. **Filling the first row with ones crosses walls.** Fix: propagate from the start.
2. **A blocked start has no paths.** Fix: let it become 0.
3. **Zero as "unknown" in memo is exponential.** Fix: use a separate sentinel.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Start | `[ [1] ]` | `0` |
| Open | `[ [0] ]` | `1` |
| Wall | `[ [0,1,0] ]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[248 — 62. Unique Paths]]: No valve. The key idea there: The last move into a cell is from above or from the left, two separate groups of paths.
- [[254 — 64. Minimum Path Sum]]: Minimum. The key idea there: The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.

### In a real interview
Say it in this order:

1. **Brute force:** Explore every path while avoiding obstacles.
2. **Insight:** Obstacles are simply cells with zero ways in the same recurrence.
3. **Approach in a few words:** One row with zeros.
4. **Cost:** time O(m·n); space O(n).

## 5. Recall
Q: 63. Unique Paths II: which pattern, and what is the insight?
A: Rolling grid count. Obstacles are simply cells with zero ways in the same recurrence.

Q: 63. Unique Paths II: what is the analogy, and what does it stand for?
A: Pipes with closed valves: valve → obstacle; tap → ways zero.

Q: 63. Unique Paths II: What is an obstacle's count?
A: Zero.

Q: 63. Unique Paths II: what are the time and space costs?
A: Time O(m·n). Space O(n).

@@ 3 | TOPIC | 250 — 1143. Longest Common Subsequence | Medium · Two-string DP | MUST
# 250 — 1143. Longest Common Subsequence

**Problem 250 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Two-string DP**

[Open on LeetCode](https://leetcode.com/problems/longest-common-subsequence/)

## 1. Problem Statement
You get two strings. Return the length of their longest common subsequence: characters in the same relative order in both, not necessarily adjacent. Return 0 if there is none.

### Examples
```text
Input:  text1 = "abcde", text2 = "ace"
Output: 3

Input:  text1 = "abc", text2 = "abc"
Output: 3

Input:  text1 = "abc", text2 = "def"
Output: 0
```

### Constraints
```text
1 <= text1.length, text2.length <= 1000
text1 and text2 consist of only lowercase English characters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two bookmarks
Two readers keep bookmarks in two books. If both point at the same word they take it and both move. If not, one of those words must be skipped, so try both and keep the better.

| In the story | In the solution |
|---|---|
| bookmarks | indices |
| take | diagonal |

### Pattern signal
**Two-string DP.** Only the two prefix lengths matter to the rest of the decision.

### Thought process
1. **First idea:** Test all subsequences of one string in the other.
2. **The insight:** Only the two prefix lengths matter to the rest of the decision.

### Optimal approach
1. Add an empty row and column.
2. Extend the diagonal on a match.
3. Otherwise take the larger neighbor.
4. Return the corner.

### Dry run
```text
abcde, ace
matched  length
a        1
c        2
e        3
```

### Why it works
When the last characters match some optimum pairs them. When they differ at least one is unused, and both cases are compared.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] best = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) best[i][j] = best[i - 1][j - 1] + 1;
                else best[i][j] = Math.max(best[i - 1][j], best[i][j - 1]);
            }
        }
        return best[m][n];
    }
}
```

### Code walkthrough
- `m + 1`: The row and column for an empty prefix.
- `best[i - 1][j - 1] + 1`: Take the shared character.
- `Math.max`: Skip one of the two characters.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Substring logic forbids gaps.** Fix: carry the best on a mismatch.
2. **String indices trail the table by one.** Fix: read i-1.
3. **Resetting on a mismatch is wrong.** Fix: take the larger neighbor.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `a, b` | `0` |
| Same | `ab, ab` | `2` |
| Order | `ab, ba` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[256 — 72. Edit Distance]]: Costs. The key idea there: The last edit is one of three kinds, each reducing to a smaller pair of prefixes.
- [[253 — 97. Interleaving String]]: Third. The key idea there: The position in s3 is always i+j, so the state is just two prefix lengths.

### In a real interview
Say it in this order:

1. **Brute force:** Test all subsequences of one string in the other.
2. **Insight:** Only the two prefix lengths matter to the rest of the decision.
3. **Approach in a few words:** Two-prefix DP.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 1143. Longest Common Subsequence: which pattern, and what is the insight?
A: Two-string DP. Only the two prefix lengths matter to the rest of the decision.

Q: 1143. Longest Common Subsequence: what is the analogy, and what does it stand for?
A: Two bookmarks: bookmarks → indices; take → diagonal.

Q: 1143. Longest Common Subsequence: What happens on an LCS mismatch?
A: Best after dropping either character.

Q: 1143. Longest Common Subsequence: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 251 — 518. Coin Change II | Medium · Combination knapsack | MUST
# 251 — 518. Coin Change II

**Problem 251 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Combination knapsack**

[Open on LeetCode](https://leetcode.com/problems/coin-change-ii/)

## 1. Problem Statement
You get distinct coin denominations and an amount, with unlimited coins of each. Return how many combinations make the amount. Different orders of the same coins count once.

### Examples
```text
Input:  amount = 5, coins = [1,2,5]
Output: 4

Input:  amount = 3, coins = [2]
Output: 0

Input:  amount = 10, coins = [10]
Output: 1
```

### Constraints
```text
1 <= coins.length <= 300
1 <= coins[i] <= 5000
All the values of coins are unique.
0 <= amount <= 5000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Opening jars one at a time
Open coin jars one at a time and never return to a closed one. That way 1+2 and 2+1 can't both be counted, while an open jar can still be used again and again.

| In the story | In the solution |
|---|---|
| jar | outer loop |
| refills | forward |

### Pattern signal
**Combination knapsack.** Fixing the order of coin types gives every combination a single representation.

### Thought process
1. **First idea:** Recursively list all combinations.
2. **The insight:** Fixing the order of coin types gives every combination a single representation.

### Optimal approach
1. Give amount zero one way.
2. Loop over coins outside.
3. Loop amounts upward.
4. Add the ways without this coin.

### Dry run
```text
amount = 5, coins = [1,2,5]
after coin  ways for amounts 0,1,2,3,4,5
start       1,0,0,0,0,0
1           1,1,1,1,1,1
2           1,1,2,2,3,3
5           1,1,2,2,3,4
```

### Why it works
After processing a coin the table counts combinations of the coins so far, each grouped by how often it uses that coin.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int change(int amount, int[] coins) {
        int[] ways = new int[amount + 1];
        ways[0] = 1;
        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) ways[a] += ways[a - coin];
        }
        return ways[amount];
    }
}
```

### Code walkthrough
- `ways[0] = 1`: The empty combination makes zero.
- `int coin : coins`: The outer loop fixes the jar order.
- `a++`: Upward means this coin may repeat.

### Complexity
- **Time:** O(amount·coins)
- **Space:** O(amount)

### Common mistakes
1. **Amounts outside count orders.** Fix: put coins outside.
2. **Looping backwards forbids reuse.** Fix: loop upward.
3. **A zero base kills every count.** Fix: one way makes zero.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `0, [2]` | `1` |
| None | `3, [2]` | `0` |
| Two | `3, [1,2]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[236 — 322. Coin Change]]: Min. The key idea there: Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.
- **377. Ordered**: Swap.

### In a real interview
Say it in this order:

1. **Brute force:** Recursively list all combinations.
2. **Insight:** Fixing the order of coin types gives every combination a single representation.
3. **Approach in a few words:** Coins outside.
4. **Cost:** time O(amount·coins); space O(amount).

## 5. Recall
Q: 518. Coin Change II: which pattern, and what is the insight?
A: Combination knapsack. Fixing the order of coin types gives every combination a single representation.

Q: 518. Coin Change II: what is the analogy, and what does it stand for?
A: Opening jars one at a time: jar → outer loop; refills → forward.

Q: 518. Coin Change II: Why coins in the outer loop?
A: To count combinations once.

Q: 518. Coin Change II: what are the time and space costs?
A: Time O(amount·coins). Space O(amount).

@@ 3 | TOPIC | 252 — 494. Target Sum | Medium · Index and sum | MUST
# 252 — 494. Target Sum

**Problem 252 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Index and sum**

[Open on LeetCode](https://leetcode.com/problems/target-sum/)

## 1. Problem Statement
Put a plus or a minus in front of every number in the array and evaluate. Return how many different sign assignments give exactly `target`.

### Examples
```text
Input:  nums = [1,1,1,1,1], target = 3
Output: 5

Input:  nums = [1], target = 1
Output: 1
```

### Constraints
```text
1 <= nums.length <= 20
0 <= nums[i] <= 1000
0 <= sum(nums[i]) <= 1000
-1000 <= target <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Walking left or right with notes
You walk along a line, one step per number, left or right. Two walkers on the same spot after the same step have identical futures, so the second can read the first one's note.

| In the story | In the solution |
|---|---|
| signpost | sum |
| step | index |

### Pattern signal
**Index and sum.** The index and the running sum completely determine how many ways remain.

### Thought process
1. **First idea:** Try all 2^n assignments.
2. **The insight:** The index and the running sum completely determine how many ways remain.

### Optimal approach
1. At the end compare with the target.
2. Otherwise read the memo.
3. Add the plus and minus branches.
4. Store it.

### Dry run
```text
nums = [1,1,1,1,1], target = 3: ways counted by running sum
after  sum:ways
1      -1:1  1:1
2      -2:1  0:2  2:1
3      -3:1  -1:3  1:3  3:1
4      -4:1  -2:4  0:6  2:4  4:1
5      -5:1  -3:5  -1:10  1:10  3:5  5:1
answer = 5
```

### Why it works
The two signs are disjoint cases, and nothing before the current state affects the remaining choices.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private static final int OFFSET = 1000;

    public int findTargetSumWays(int[] nums, int target) {
        return count(nums, 0, 0, target, new Integer[nums.length][2 * OFFSET + 1]);
    }

    private int count(int[] nums, int i, int sum, int target, Integer[][] memo) {
        if (i == nums.length) return sum == target ? 1 : 0;
        if (memo[i][sum + OFFSET] != null) return memo[i][sum + OFFSET];
        int ways = count(nums, i + 1, sum + nums[i], target, memo)
                + count(nums, i + 1, sum - nums[i], target, memo);
        memo[i][sum + OFFSET] = ways;
        return ways;
    }
}
```

### Code walkthrough
- `OFFSET = 1000`: Shifts negative sums to valid indices.
- `sum == target ? 1 : 0`: One way if the walk ended on the target.
- `memo[i][sum + OFFSET] = ways`: Leave a note for later walkers.

### Complexity
- **Time:** O(n·sum)
- **Space:** O(n·sum)

### Common mistakes
1. **Caching the sum alone mixes positions.** Fix: include the index.
2. **Every number needs a sign.** Fix: never skip one.
3. **A zero still has two signs.** Fix: count both branches.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1], 1` | `1` |
| Zero | `[0], 0` | `2` |
| None | `[1], 2` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[240 — 416. Partition Equal Subset Sum]]: Boolean. The key idea there: If one part reaches half the total the other does automatically, so this is a subset-sum question.
- [[251 — 518. Coin Change II]]: Reuse. The key idea there: Fixing the order of coin types gives every combination a single representation.

### In a real interview
Say it in this order:

1. **Brute force:** Try all 2^n assignments.
2. **Insight:** The index and the running sum completely determine how many ways remain.
3. **Approach in a few words:** Memoize index and sum.
4. **Cost:** time O(n·sum); space O(n·sum).

## 5. Recall
Q: 494. Target Sum: which pattern, and what is the insight?
A: Index and sum. The index and the running sum completely determine how many ways remain.

Q: 494. Target Sum: what is the analogy, and what does it stand for?
A: Walking left or right with notes: signpost → sum; step → index.

Q: 494. Target Sum: What is target-sum state?
A: Index and current sum.

Q: 494. Target Sum: what are the time and space costs?
A: Time O(n·sum). Space O(n·sum).

@@ 3 | TOPIC | Practice — Review 42 | Re-solve without looking | MUST
# Review 42

Solve on a blank page. No notes, hints or solutions.

## 494. Target Sum
Count sign assignments reaching a target.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/target-sum/)

## 63. Unique Paths II
Count grid paths avoiding obstacles.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/unique-paths-ii/)

## 139. Word Break
Decide whether a string splits into dictionary words.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/word-break/)

## 207. Course Schedule
Decide whether all courses can be finished.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/course-schedule/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 494 | | | | |
| 63 | | | | |
| 139 | | | | |
| 207 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[252 — 494. Target Sum]]
- [[249 — 63. Unique Paths II]]
- [[238 — 139. Word Break]]
- [[214 — 207. Course Schedule]]

@@ 3 | TOPIC | 253 — 97. Interleaving String | Medium · Two-prefix DP | MUST
# 253 — 97. Interleaving String

**Problem 253 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Two-prefix DP**

[Open on LeetCode](https://leetcode.com/problems/interleaving-string/)

## 1. Problem Statement
You get strings s1, s2 and s3. Return true if s3 can be formed by interleaving all characters of s1 and s2 while keeping the original order within each of them.

### Examples
```text
Input:  s1 = "aabcc", s2 = "dbbca", s3 = "aadbbcbcac"
Output: true

Input:  s1 = "aabcc", s2 = "dbbca", s3 = "aadbbbaccc"
Output: false

Input:  s1 = "", s2 = "", s3 = ""
Output: true
```

### Constraints
```text
0 <= s1.length, s2.length <= 100
0 <= s3.length <= 200
s1, s2, and s3 consist of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Shuffling two decks
You shuffle two decks into a third without reordering either. Knowing how many cards you took from each also tells you the position in the third, so two numbers describe everything.

| In the story | In the solution |
|---|---|
| taken | i and j |
| position | i+j |

### Pattern signal
**Two-prefix DP.** The position in s3 is always i+j, so the state is just two prefix lengths.

### Thought process
1. **First idea:** Branch whenever both decks show the needed card, which is exponential.
2. **The insight:** The position in s3 is always i+j, so the state is just two prefix lengths.

### Optimal approach
1. Check the lengths.
2. Mark the empty state true.
3. Extend from s1 if its card matches.
4. Extend from s2 likewise.

### Dry run
```text
s1 = "a", s2 = "b", s3 = "ab"
state (i,j)  last card needed  comes from    ok
(0,0)        -                 base          true
(0,1)        a                 s2 has b      false
(1,0)        a                 s1 has a      true
(1,1)        b                 s2, from (1,0) true
```

### Why it works
The last card of a valid shuffle came from one of the decks, and the rest must be a valid shuffle of the smaller prefixes.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length()) return false;
        boolean[][] ok = new boolean[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 && j == 0) ok[i][j] = true;
                if (i > 0 && s1.charAt(i - 1) == s3.charAt(i + j - 1) && ok[i - 1][j]) ok[i][j] = true;
                if (j > 0 && s2.charAt(j - 1) == s3.charAt(i + j - 1) && ok[i][j - 1]) ok[i][j] = true;
            }
        }
        return ok[m][n];
    }
}
```

### Code walkthrough
- `m + n != s3.length()`: All cards must be used.
- `i + j - 1`: The current position in the shuffled deck.
- `ok[i - 1][j]`: The last card came from the first deck.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Greedy fails when both next letters match.** Fix: keep both states.
2. **Equal counts ignore order.** Fix: check positions.
3. **Unequal total length cannot match.** Fix: check it first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `three empty` | `true` |
| One | `a, empty, a` | `true` |
| Order | `ab, empty, ba` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[250 — 1143. Longest Common Subsequence]]: Max. The key idea there: Only the two prefix lengths matter to the rest of the decision.
- [[263 — 10. Regular Expression Matching]]: Rules. The key idea there: The two indices alone decide whether the rest matches.

### In a real interview
Say it in this order:

1. **Brute force:** Branch whenever both decks show the needed card, which is exponential.
2. **Insight:** The position in s3 is always i+j, so the state is just two prefix lengths.
3. **Approach in a few words:** Two-prefix boolean.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 97. Interleaving String: which pattern, and what is the insight?
A: Two-prefix DP. The position in s3 is always i+j, so the state is just two prefix lengths.

Q: 97. Interleaving String: what is the analogy, and what does it stand for?
A: Shuffling two decks: taken → i and j; position → i+j.

Q: 97. Interleaving String: How is s3's index known?
A: i plus j.

Q: 97. Interleaving String: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 254 — 64. Minimum Path Sum | Medium · Memoized path | SHOULD
# 254 — 64. Minimum Path Sum

**Problem 254 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Memoized path**

[Open on LeetCode](https://leetcode.com/problems/minimum-path-sum/)

## 1. Problem Statement
You get a grid of non-negative numbers. Moving only right or down, return the smallest possible sum of values on a path from the top-left to the bottom-right, both included.

### Examples
```text
Input:  grid = [[1,3,1],[1,5,1],[4,2,1]]
Output: 7

Input:  grid = [[1,2,3],[4,5,6]]
Output: 12
```

### Constraints
```text
m == grid.length
n == grid[i].length
1 <= m, n <= 200
0 <= grid[i][j] <= 200
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Asking neighbors the cheapest route home
At each street corner you ask two neighbors "what is the cheapest route from the start to you?", add your own toll and write it down, so nobody asks that corner twice.

| In the story | In the solution |
|---|---|
| written | memo |
| home | start |

### Pattern signal
**Memoized path.** The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.

### Thought process
1. **First idea:** Enumerate every path.
2. **The insight:** The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.

### Optimal approach
1. Return infinity off the map and the value at the start.
2. Reuse a saved answer.
3. Otherwise save the cell plus the minimum.

### Dry run
```text
grid rows = 1 3 1 / 1 5 1 / 4 2 1
best total to each cell
row 0:  1  4  5
row 1:  2  7  6
row 2:  6  8  7
the corner holds 7
```

### Why it works
Every path's last move comes from above or left, and an optimal one uses an optimal path to that predecessor.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        return best(grid, m - 1, n - 1, new Integer[m][n]);
    }

    private int best(int[][] grid, int r, int c, Integer[][] memo) {
        if (r < 0 || c < 0) return Integer.MAX_VALUE;
        if (r == 0 && c == 0) return grid[0][0];
        if (memo[r][c] == null) {
            memo[r][c] = grid[r][c] + Math.min(best(grid, r - 1, c, memo), best(grid, r, c - 1, memo));
        }
        return memo[r][c];
    }
}
```

### Code walkthrough
- `return Integer.MAX_VALUE`: A route from off the map never wins.
- `memo[r][c] == null`: Null means unknown, since zero is a real cost.
- `Math.min`: Choose the cheaper neighbor.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **The cheaper next cell can lead to a worse path.** Fix: use DP.
2. **Zero is a valid cost.** Fix: use null as unknown.
3. **Off-map moves must lose.** Fix: return infinity.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [5] ]` | `5` |
| Row | `[ [1,2] ]` | `3` |
| Zero | `[ [0] ]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[248 — 62. Unique Paths]]: Add. The key idea there: The last move into a cell is from above or from the left, two separate groups of paths.
- [[241 — 120. Triangle]]: Shape. The key idea there: The best continuation from a cell is independent of the path taken to it.

### In a real interview
Say it in this order:

1. **Brute force:** Enumerate every path.
2. **Insight:** The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.
3. **Approach in a few words:** Memo or rolling table.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 64. Minimum Path Sum: which pattern, and what is the insight?
A: Memoized path. The cheapest path to a cell is the cell plus the cheaper path to one of two predecessors.

Q: 64. Minimum Path Sum: what is the analogy, and what does it stand for?
A: Asking neighbors the cheapest route home: written → memo; home → start.

Q: 64. Minimum Path Sum: What is min-path recurrence?
A: Cell plus minimum of above and left.

Q: 64. Minimum Path Sum: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 255 — 221. Maximal Square | Medium · Square DP | SHOULD
# 255 — 221. Maximal Square

**Problem 255 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Square DP**

[Open on LeetCode](https://leetcode.com/problems/maximal-square/)

## 1. Problem Statement
You get a matrix of '0' and '1'. Find the largest square containing only 1s and return its area.

### Examples
```text
Input:  matrix = [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
Output: 4

Input:  matrix = [["0","1"],["1","0"]]
Output: 1

Input:  matrix = [["0"]]
Output: 0
```

### Constraints
```text
m == matrix.length
n == matrix[i].length
1 <= m, n <= 300
matrix[i][j] is '0' or '1'.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Laying a square floor
You are tiling square floors. A new corner tile can finish a square only one larger than the weakest of the three squares supporting it from above, left and diagonal.

| In the story | In the solution |
|---|---|
| corner | bottom-right |
| weakest | minimum |

### Pattern signal
**Square DP.** Three neighboring squares of side k-1 plus one tile certify a square of side k.

### Thought process
1. **First idea:** Check every possible square cell by cell.
2. **The insight:** Three neighboring squares of side k-1 plus one tile certify a square of side k.

### Optimal approach
1. Pad the table.
2. For a 1 take one plus the smallest of three neighbors.
3. Track the largest side.
4. Square it.

### Dry run
```text
matrix = [ [1,1],[1,1] ]
cell   up  left  diagonal  side
(0,0)  0   0     0         1
(0,1)  0   1     0         1
(1,0)  1   0     0         1
(1,1)  1   1     1         2, so the area is 4
```

### Why it works
A side-k square ending here contains those three k-1 squares, and they together with this tile cover every cell of it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maximalSquare(char[][] matrix) {
        int m = matrix.length, n = matrix[0].length, best = 0;
        int[][] side = new int[m + 1][n + 1];
        for (int r = 1; r <= m; r++) {
            for (int c = 1; c <= n; c++) {
                if (matrix[r - 1][c - 1] == '0') continue;
                side[r][c] = 1 + Math.min(side[r - 1][c - 1], Math.min(side[r - 1][c], side[r][c - 1]));
                best = Math.max(best, side[r][c]);
            }
        }
        return best * best;
    }
}
```

### Code walkthrough
- `== '0'`: A hole supports no square.
- `1 + Math.min`: The weakest support limits it.
- `best * best`: The question asks for area.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **The answer is an area.** Fix: square the side.
2. **The maximum neighbor overstates it.** Fix: take the minimum.
3. **Cells are characters.** Fix: compare with '0'.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `[ ["0"] ]` | `0` |
| One | `[ ["1"] ]` | `1` |
| Row | `[ ["1","1"] ]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **85. Rectangle**: Histogram.
- **1277. Count**: Sum.

### In a real interview
Say it in this order:

1. **Brute force:** Check every possible square cell by cell.
2. **Insight:** Three neighboring squares of side k-1 plus one tile certify a square of side k.
3. **Approach in a few words:** Side ending here.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 221. Maximal Square: which pattern, and what is the insight?
A: Square DP. Three neighboring squares of side k-1 plus one tile certify a square of side k.

Q: 221. Maximal Square: what is the analogy, and what does it stand for?
A: Laying a square floor: corner → bottom-right; weakest → minimum.

Q: 221. Maximal Square: What limits a square?
A: The minimum of three neighboring squares.

Q: 221. Maximal Square: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 256 — 72. Edit Distance | Medium · Two-string DP | MUST
# 256 — 72. Edit Distance

**Problem 256 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Two-string DP**

[Open on LeetCode](https://leetcode.com/problems/edit-distance/)

## 1. Problem Statement
You get two words. One operation inserts, deletes or replaces a single character. Return the fewest operations that turn word1 into word2.

### Examples
```text
Input:  word1 = "horse", word2 = "ros"
Output: 3

Input:  word1 = "intention", word2 = "execution"
Output: 5
```

### Constraints
```text
0 <= word1.length, word2.length <= 500
word1 and word2 consist of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An editor with two bookmarks
An editor has a bookmark in each word. Matching letters cost nothing. Otherwise the editor replaces, deletes or inserts, whichever leaves the cheapest remaining job.

| In the story | In the solution |
|---|---|
| bookmarks | prefixes |
| edits | neighbors |

### Pattern signal
**Two-string DP.** The last edit is one of three kinds, each reducing to a smaller pair of prefixes.

### Thought process
1. **First idea:** Search all sequences of edits.
2. **The insight:** The last edit is one of three kinds, each reducing to a smaller pair of prefixes.

### Optimal approach
1. Fill borders with lengths.
2. Copy the diagonal on equality.
3. Otherwise add one to the smallest of three.
4. Return the corner.

### Dry run
```text
horse to ros
edit       word
replace h  rorse
delete r   rose
delete e   ros
```

### Why it works
Some optimal edit script ends in a match, replacement, deletion or insertion, and each of those leaves an optimally solved smaller problem.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] edits = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) edits[i][0] = i;
        for (int j = 0; j <= n; j++) edits[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) edits[i][j] = edits[i - 1][j - 1];
                else edits[i][j] = 1 + Math.min(edits[i - 1][j - 1], Math.min(edits[i - 1][j], edits[i][j - 1]));
            }
        }
        return edits[m][n];
    }
}
```

### Code walkthrough
- `edits[i][0] = i`: Delete every character to reach empty.
- `edits[0][j] = j`: Insert every character from empty.
- `1 + Math.min`: Replace, delete or insert, whichever is cheapest.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Borders cost their length.** Fix: do not leave them zero.
2. **Three edits exist.** Fix: check three neighbors.
3. **Equal letters are free.** Fix: copy the diagonal.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `"", "ab"` | `2` |
| Same | `a, a` | `0` |
| Replace | `a, b` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[250 — 1143. Longest Common Subsequence]]: Maximum. The key idea there: Only the two prefix lengths matter to the rest of the decision.
- **583. Delete Only**: Two.

### In a real interview
Say it in this order:

1. **Brute force:** Search all sequences of edits.
2. **Insight:** The last edit is one of three kinds, each reducing to a smaller pair of prefixes.
3. **Approach in a few words:** Two prefixes, three edits.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 72. Edit Distance: which pattern, and what is the insight?
A: Two-string DP. The last edit is one of three kinds, each reducing to a smaller pair of prefixes.

Q: 72. Edit Distance: what is the analogy, and what does it stand for?
A: An editor with two bookmarks: bookmarks → prefixes; edits → neighbors.

Q: 72. Edit Distance: What are edit transitions?
A: Diagonal replace, up delete, left insert.

Q: 72. Edit Distance: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 257 — 1140. Stone Game II | Medium · Minimax DP | SHOULD
# 257 — 1140. Stone Game II

**Problem 257 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: Minimax DP**

[Open on LeetCode](https://leetcode.com/problems/stone-game-ii/)

## 1. Problem Statement
Alice and Bob alternate, Alice first, M starting at 1. On a turn a player takes all stones in the first X remaining piles, for any X from 1 to 2M, and M becomes max(M, X). Both play optimally. Return Alice's most stones.

### Examples
```text
Input:  piles = [2,7,9,4,4]
Output: 10

Input:  piles = [1,2,3,4,5,100]
Output: 104
```

### Constraints
```text
1 <= piles.length <= 100
1 <= piles[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Leaving your rival the smallest feast
At a feast the food left is shared between you and your rival. So instead of grabbing, make the move that leaves your rival's best meal as small as possible. Yours is the rest.

| In the story | In the solution |
|---|---|
| remains | suffix |
| rival | recursive call |

### Pattern signal
**Minimax DP.** Both players solve the same problem, and the fixed suffix total turns your gain into total minus theirs.

### Thought process
1. **First idea:** Search the whole game tree.
2. **The insight:** Both players solve the same problem, and the fixed suffix total turns your gain into total minus theirs.

### Optimal approach
1. Compute suffix sums.
2. Take all if allowed.
3. Otherwise try each X.
4. Minimize the rival's value.
5. Subtract and memoize.

### Dry run
```text
2,7,9,4,4
Alice takes  rival best
1 pile       16
2 piles      17
Alice 26-16 = 10
```

### Why it works
The rival plays optimally by the same function. With a fixed total, minimizing their score is exactly maximizing yours.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int stoneGameII(int[] piles) {
        int n = piles.length;
        int[] suffix = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) suffix[i] = suffix[i + 1] + piles[i];
        return best(suffix, 0, 1, new int[n][n + 1]);
    }

    private int best(int[] suffix, int i, int m, int[][] memo) {
        int n = suffix.length - 1;
        if (i + 2 * m >= n) return suffix[i];
        if (memo[i][m] != 0) return memo[i][m];
        int rival = Integer.MAX_VALUE;
        for (int x = 1; x <= 2 * m; x++) rival = Math.min(rival, best(suffix, i + x, Math.max(m, x), memo));
        memo[i][m] = suffix[i] - rival;
        return memo[i][m];
    }
}
```

### Code walkthrough
- `i + 2 * m >= n`: You may take everything left.
- `Math.min(rival`: Leave the rival as little as possible.
- `suffix[i] - rival`: Your share is everything they don't get.

### Complexity
- **Time:** O(n³)
- **Space:** O(n²)

### Common mistakes
1. **Grabbing the most piles helps the rival.** Fix: minimize their result.
2. **The rival is optimal.** Fix: never average their moves.
3. **M changes the options.** Fix: cache index and M together.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5]` | `5` |
| Two | `[1,2]` | `3` |
| Three | `[1,1,1]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[231 — 198. House Robber]]: Max. The key idea there: The last house is either skipped or taken, and each case leaves one smaller, solved problem.
- **877. Ends**: Intervals.

### In a real interview
Say it in this order:

1. **Brute force:** Search the whole game tree.
2. **Insight:** Both players solve the same problem, and the fixed suffix total turns your gain into total minus theirs.
3. **Approach in a few words:** Suffix minimax.
4. **Cost:** time O(n³); space O(n²).

## 5. Recall
Q: 1140. Stone Game II: which pattern, and what is the insight?
A: Minimax DP. Both players solve the same problem, and the fixed suffix total turns your gain into total minus theirs.

Q: 1140. Stone Game II: what is the analogy, and what does it stand for?
A: Leaving your rival the smallest feast: remains → suffix; rival → recursive call.

Q: 1140. Stone Game II: What is stone-game value?
A: Suffix minus opponent's best.

Q: 1140. Stone Game II: what are the time and space costs?
A: Time O(n³). Space O(n²).

@@ 2 | MODULE | Dynamic Programming — Advanced DP | 6 problems | MUST
# Dynamic Programming: Advanced DP

Patterns in this group: State machine, DAG memo, Matching count, Interval DP, Two-index matching.

@@ 3 | TOPIC | 258 — 309. Best Time to Buy and Sell Stock with Cooldown | Medium · State machine | MUST
# 258 — 309. Best Time to Buy and Sell Stock with Cooldown

**Problem 258 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Medium | Pattern: State machine**

[Open on LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/)

## 1. Problem Statement
`prices[i]` is a stock's price on day i. You may trade many times but hold at most one share, and after selling you cannot buy on the next day. Return the largest profit.

### Examples
```text
Input:  prices = [1,2,3,0,2]
Output: 3

Input:  prices = [1]
Output: 0
```

### Constraints
```text
1 <= prices.length <= 5000
0 <= prices[i] <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A traffic light
A trading light has modes: free to buy, or holding. Selling turns it red for one day, which you model by jumping straight to the day after tomorrow.

| In the story | In the solution |
|---|---|
| lights | buy and sell states |
| red | day+2 |

### Pattern signal
**State machine.** The day and whether you hold a share are all that the future depends on.

### Thought process
1. **First idea:** Try buy, sell or wait on every day.
2. **The insight:** The day and whether you hold a share are all that the future depends on.

### Optimal approach
1. Pad two zero days.
2. Go backwards.
3. For can-buy choose wait or buy.
4. For holding choose keep or sell with a jump.
5. Return can-buy at day zero.

### Dry run
```text
1,2,3,0,2
action  profit
buy 1, sell 2  1
rest, buy 0, sell 2  3
```

### Why it works
Every legal first action is considered, and the jump enforces exactly one rest day, leaving a smaller solved state.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int[] canBuy = new int[n + 2], holding = new int[n + 2];
        for (int day = n - 1; day >= 0; day--) {
            canBuy[day] = Math.max(canBuy[day + 1], holding[day + 1] - prices[day]);
            holding[day] = Math.max(holding[day + 1], canBuy[day + 2] + prices[day]);
        }
        return canBuy[0];
    }
}
```

### Code walkthrough
- `n + 2`: Room to jump past the last day.
- `holding[day + 1] - prices[day]`: Buy today and start holding.
- `canBuy[day + 2]`: After selling, the next chance is two days later.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Adding every rise ignores the rest day.** Fix: use states.
2. **The rest follows selling only.** Fix: jump two days there.
3. **The day alone is not enough.** Fix: also store whether you hold.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `0` |
| Rise | `[1,2]` | `1` |
| Fall | `[2,1]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[024 — 122. Best Time to Buy and Sell Stock II]]: Greedy. The key idea there: A long climb equals the sum of its daily climbs, so each day can be decided alone.
- [[261 — 1220. Count Vowels Permutation]]: States. The key idea there: Only the final vowel limits the next one, so five counts are the whole state.

### In a real interview
Say it in this order:

1. **Brute force:** Try buy, sell or wait on every day.
2. **Insight:** The day and whether you hold a share are all that the future depends on.
3. **Approach in a few words:** A two-state machine.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 309. Best Time to Buy and Sell Stock with Cooldown: which pattern, and what is the insight?
A: State machine. The day and whether you hold a share are all that the future depends on.

Q: 309. Best Time to Buy and Sell Stock with Cooldown: what is the analogy, and what does it stand for?
A: A traffic light: lights → buy and sell states; red → day+2.

Q: 309. Best Time to Buy and Sell Stock with Cooldown: How is cooldown encoded?
A: Selling moves to can-buy at day plus two.

Q: 309. Best Time to Buy and Sell Stock with Cooldown: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | Practice — Review 43 | Re-solve without looking | MUST
# Review 43

Solve on a blank page. No notes, hints or solutions.

## 309. Best Time to Buy and Sell Stock with Cooldown
Maximize stock profit with a one-day cooldown.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/)

## 221. Maximal Square
Find the largest square of ones.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/maximal-square/)

## 1856. Maximum Subarray Min-Product
Maximize subarray minimum times sum.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/maximum-subarray-min-product/)

## 785. Is Graph Bipartite
Decide whether a graph is bipartite.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/is-graph-bipartite/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 309 | | | | |
| 221 | | | | |
| 1856 | | | | |
| 785 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[258 — 309. Best Time to Buy and Sell Stock with Cooldown]]
- [[255 — 221. Maximal Square]]
- [[244 — 1856. Maximum Subarray Min-Product]]
- [[220 — 785. Is Graph Bipartite]]

@@ 3 | TOPIC | 259 — 329. Longest Increasing Path in a Matrix | Hard · DAG memo | MUST
# 259 — 329. Longest Increasing Path in a Matrix

**Problem 259 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Hard | Pattern: DAG memo**

[Open on LeetCode](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/)

## 1. Problem Statement
You get an integer matrix. Return the length of the longest strictly increasing path, moving only up, down, left or right.

### Examples
```text
Input:  matrix = [[9,9,4],[6,6,8],[2,1,1]]
Output: 4

Input:  matrix = [[3,4,5],[3,2,6],[2,2,1]]
Output: 4

Input:  matrix = [[1]]
Output: 1
```

### Constraints
```text
m == matrix.length
n == matrix[i].length
1 <= m, n <= 200
0 <= matrix[i][j] <= 2^31 - 1
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Climbers leaving notes
Climbers leave a note on every rock: "the longest climb from here is L". Because they only go upward nobody can walk in a circle, so a note stays true however you arrived.

| In the story | In the solution |
|---|---|
| note | memo |
| upward | DAG edge |

### Pattern signal
**DAG memo.** Strict increase makes the grid a DAG, where longest paths can be memoized per cell.

### Thought process
1. **First idea:** Run a full DFS from every cell without memory, which is exponential.
2. **The insight:** Strict increase makes the grid a DAG, where longest paths can be memoized per cell.

### Optimal approach
1. Start a search at each cell.
2. Return a saved length.
3. Try larger neighbors.
4. Save one plus the best.

### Dry run
```text
path 1,2,6,9
cell  note
9     1
6     2
2     3
1     4
```

### Why it works
Every increasing path from a cell continues with a larger neighbor's path, and no cycle can make the recursion return to itself.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int longestIncreasingPath(int[][] matrix) {
        int[][] memo = new int[matrix.length][matrix[0].length];
        int best = 0;
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) best = Math.max(best, climb(matrix, memo, r, c));
        }
        return best;
    }

    private int climb(int[][] a, int[][] memo, int r, int c) {
        if (memo[r][c] != 0) return memo[r][c];
        int longest = 1;
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] s : steps) {
            int nr = r + s[0], nc = c + s[1];
            if (nr < 0 || nc < 0 || nr == a.length || nc == a[0].length || a[nr][nc] <= a[r][c]) continue;
            longest = Math.max(longest, 1 + climb(a, memo, nr, nc));
        }
        memo[r][c] = longest;
        return longest;
    }
}
```

### Code walkthrough
- `memo[r][c] != 0`: A note is already here.
- `<= a[r][c]`: Only strictly uphill moves.
- `memo[r][c] = longest`: Leave the note for everyone else.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Equal moves create cycles.** Fix: require strictly larger.
2. **DFS without memory is exponential.** Fix: cache each cell.
3. **Length counts cells.** Fix: a single cell returns 1.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [1] ]` | `1` |
| Flat | `[ [2,2] ]` | `1` |
| Row | `[ [1,2,3] ]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[239 — 300. Longest Increasing Subsequence]]: 1D. The key idea there: For each length only the smallest possible last value matters, and those values are sorted.
- [[207 — 417. Pacific Atlantic Water Flow]]: Reachability. The key idea there: Reversing every flow edge turns many searches into two.

### In a real interview
Say it in this order:

1. **Brute force:** Run a full DFS from every cell without memory, which is exponential.
2. **Insight:** Strict increase makes the grid a DAG, where longest paths can be memoized per cell.
3. **Approach in a few words:** Memoized DAG DFS.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 329. Longest Increasing Path in a Matrix: which pattern, and what is the insight?
A: DAG memo. Strict increase makes the grid a DAG, where longest paths can be memoized per cell.

Q: 329. Longest Increasing Path in a Matrix: what is the analogy, and what does it stand for?
A: Climbers leaving notes: note → memo; upward → DAG edge.

Q: 329. Longest Increasing Path in a Matrix: Why no visited set?
A: Strict increase has no cycles.

Q: 329. Longest Increasing Path in a Matrix: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 260 — 115. Distinct Subsequences | Hard · Matching count | MUST
# 260 — 115. Distinct Subsequences

**Problem 260 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Hard | Pattern: Matching count**

[Open on LeetCode](https://leetcode.com/problems/distinct-subsequences/)

## 1. Problem Statement
You get strings s and t. Return how many different ways there are to delete characters of s, possibly none, so that exactly t remains in order.

### Examples
```text
Input:  s = "rabbbit", t = "rabbit"
Output: 3

Input:  s = "babgbag", t = "bag"
Output: 5
```

### Constraints
```text
1 <= s.length, t.length <= 1000
s and t consist of English letters.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Stamping a word from a tape
A tape passes a stamping machine that must print t. A matching letter may be stamped or ignored. Update the counts from the end so one letter can't be stamped in two places.

| In the story | In the solution |
|---|---|
| stamped | prefix of t |
| once | backwards |

### Pattern signal
**Matching count.** Each character is either used for the current target position or not, and those counts add.

### Thought process
1. **First idea:** Enumerate all subsequences of s.
2. **The insight:** Each character is either used for the current target position or not, and those counts add.

### Optimal approach
1. Give the empty target one way.
2. Read s.
3. Loop t backwards.
4. On a match add the ways of the shorter prefix.

### Dry run
```text
bbb to bb
read  one b  two b
1     1      0
2     2      1
3     3      3
```

### Why it works
The selections using this character and those skipping it are disjoint. The backwards loop reads values from before this character.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numDistinct(String s, String t) {
        int[] ways = new int[t.length() + 1];
        ways[0] = 1;
        for (char c : s.toCharArray()) {
            for (int j = t.length(); j > 0; j--) {
                if (t.charAt(j - 1) == c) ways[j] += ways[j - 1];
            }
        }
        return ways[t.length()];
    }
}
```

### Code walkthrough
- `ways[0] = 1`: There is one way to make nothing.
- `j--`: Backwards, so a letter is used once.
- `ways[j - 1]`: Use the letter at this target position.

### Complexity
- **Time:** O(m·n)
- **Space:** O(n)

### Common mistakes
1. **A forward update reuses a letter.** Fix: loop t backwards.
2. **Different indices count separately.** Fix: do not deduplicate text.
3. **A match may also be skipped.** Fix: add both cases.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Same | `a, a` | `1` |
| Twice | `aa, a` | `2` |
| None | `a, b` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[250 — 1143. Longest Common Subsequence]]: Max. The key idea there: Only the two prefix lengths matter to the rest of the decision.
- [[252 — 494. Target Sum]]: Signs. The key idea there: The index and the running sum completely determine how many ways remain.

### In a real interview
Say it in this order:

1. **Brute force:** Enumerate all subsequences of s.
2. **Insight:** Each character is either used for the current target position or not, and those counts add.
3. **Approach in a few words:** Backward matching count.
4. **Cost:** time O(m·n); space O(n).

## 5. Recall
Q: 115. Distinct Subsequences: which pattern, and what is the insight?
A: Matching count. Each character is either used for the current target position or not, and those counts add.

Q: 115. Distinct Subsequences: what is the analogy, and what does it stand for?
A: Stamping a word from a tape: stamped → prefix of t; once → backwards.

Q: 115. Distinct Subsequences: What happens on a subsequence match?
A: Add using to skipping.

Q: 115. Distinct Subsequences: what are the time and space costs?
A: Time O(m·n). Space O(n).

@@ 3 | TOPIC | 261 — 1220. Count Vowels Permutation | Hard · State machine | SHOULD
# 261 — 1220. Count Vowels Permutation

**Problem 261 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Hard | Pattern: State machine**

[Open on LeetCode](https://leetcode.com/problems/count-vowels-permutation/)

## 1. Problem Statement
Count strings of length n made of a, e, i, o, u where: a is followed only by e; e only by a or i; i never by i; o only by i or u; u only by a. Return the count modulo 1,000,000,007.

### Examples
```text
Input:  n = 1
Output: 5

Input:  n = 2
Output: 10

Input:  n = 5
Output: 68
```

### Constraints
```text
1 <= n <= 2 * 10^4
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Crowds moving between five stations
Five stations are joined by one-way lines given by the rules. Each round everyone rides one line. You don't track passengers, just the crowd at each station.

| In the story | In the solution |
|---|---|
| stations | five variables |
| ride | add predecessors |

### Pattern signal
**State machine.** Only the final vowel limits the next one, so five counts are the whole state.

### Thought process
1. **First idea:** Generate all strings.
2. **The insight:** Only the final vowel limits the next one, so five counts are the whole state.

### Optimal approach
1. Start each vowel at one.
2. Add the counts of allowed predecessors.
3. Reduce modulo.
4. Replace all five.
5. Sum at the end.

### Dry run
```text
n2
end  count
a    3
e    2
i    2
o    1
u    2
total 10
```

### Why it works
Every valid string has exactly one shorter prefix ending in an allowed predecessor, so each is counted once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int countVowelPermutation(int n) {
        final long MOD = 1_000_000_007L;
        long a = 1, e = 1, i = 1, o = 1, u = 1;
        for (int length = 2; length <= n; length++) {
            long nextA = (e + i + u) % MOD;
            long nextE = (a + i) % MOD;
            long nextI = (e + o) % MOD;
            long nextO = i;
            long nextU = (i + o) % MOD;
            a = nextA;
            e = nextE;
            i = nextI;
            o = nextO;
            u = nextU;
        }
        return (int) ((a + e + i + o + u) % MOD);
    }
}
```

### Code walkthrough
- `e + i + u`: The vowels allowed before a.
- `nextO = i`: Only i may come before o.
- `% MOD`: Keep the numbers small every round.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Followers are not predecessors.** Fix: invert the rules.
2. **Updating in place mixes lengths.** Fix: use temporaries.
3. **A late modulo overflows.** Fix: reduce every round.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `5` |
| Two | `2` | `10` |
| Five | `5` | `68` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[258 — 309. Best Time to Buy and Sell Stock with Cooldown]]: Max. The key idea there: The day and whether you hold a share are all that the future depends on.
- [[235 — 91. Decode Ways]]: Two back. The key idea there: The final code uses one digit or two, and only valid choices pass their count on.

### In a real interview
Say it in this order:

1. **Brute force:** Generate all strings.
2. **Insight:** Only the final vowel limits the next one, so five counts are the whole state.
3. **Approach in a few words:** Five-state count.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 1220. Count Vowels Permutation: which pattern, and what is the insight?
A: State machine. Only the final vowel limits the next one, so five counts are the whole state.

Q: 1220. Count Vowels Permutation: what is the analogy, and what does it stand for?
A: Crowds moving between five stations: stations → five variables; ride → add predecessors.

Q: 1220. Count Vowels Permutation: What is vowel state?
A: The final vowel.

Q: 1220. Count Vowels Permutation: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 262 — 312. Burst Balloons | Hard · Interval DP | MUST
# 262 — 312. Burst Balloons

**Problem 262 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Hard | Pattern: Interval DP**

[Open on LeetCode](https://leetcode.com/problems/burst-balloons/)

## 1. Problem Statement
You get numbered balloons. Bursting balloon i earns the product of it and its current left and right neighbors, a missing neighbor counting as 1, and the neighbors then become adjacent. Return the most coins from bursting all.

### Examples
```text
Input:  nums = [3,1,5,8]
Output: 167

Input:  nums = [1,5]
Output: 10
```

### Constraints
```text
n == nums.length
1 <= n <= 300
0 <= nums[i] <= 100
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Who leaves the party last
At a party ask who leaves a room last. That guest stands between the two doormen when leaving, and while they were there the guests on either side never met, so the sides are independent.

| In the story | In the solution |
|---|---|
| doormen | excluded borders |
| last | split |

### Pattern signal
**Interval DP.** Choosing the last balloon of an interval fixes its neighbors and separates the two sides.

### Thought process
1. **First idea:** Try all n factorial orders.
2. **The insight:** Choosing the last balloon of an interval fixes its neighbors and separates the two sides.

### Optimal approach
1. Pad with ones.
2. Grow intervals between excluded borders.
3. Try each last balloon.
4. Add both sides and its burst.

### Dry run
```text
3,1,5,8
burst  gain
1      15
5      120
3      24
8      8
total  167
```

### Why it works
In every order each interval has one last balloon, whose neighbors are the borders then, and the orders on its two sides don't affect each other.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length + 2;
        int[] a = new int[n];
        a[0] = a[n - 1] = 1;
        for (int i = 1; i < n - 1; i++) a[i] = nums[i - 1];
        int[][] best = new int[n][n]; // between excluded borders
        for (int left = n - 3; left >= 0; left--) {
            for (int right = left + 2; right < n; right++) {
                for (int last = left + 1; last < right; last++) {
                    int coins = best[left][last] + best[last][right] + a[left] * a[last] * a[right];
                    best[left][right] = Math.max(best[left][right], coins);
                }
            }
        }
        return best[0][n - 1];
    }
}
```

### Code walkthrough
- `a[n - 1] = 1`: The imaginary borders worth one.
- `a[left] * a[last] * a[right]`: The reward for leaving last.
- `left--`: Both inner intervals are already computed.

### Complexity
- **Time:** O(n³)
- **Space:** O(n²)

### Common mistakes
1. **Choosing the first burst couples both sides.** Fix: choose the last.
2. **The borders are never burst.** Fix: exclude them.
3. **Greedy by size fails.** Fix: try every last balloon.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5]` | `5` |
| Two | `[1,5]` | `10` |
| Zero | `[0]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[158 — 96. Unique Binary Search Trees]]: Split. The key idea there: The number of shapes depends only on how many nodes a subtree has.
- **1547. Cuts**: Minimum.

### In a real interview
Say it in this order:

1. **Brute force:** Try all n factorial orders.
2. **Insight:** Choosing the last balloon of an interval fixes its neighbors and separates the two sides.
3. **Approach in a few words:** Interval DP choosing last.
4. **Cost:** time O(n³); space O(n²).

## 5. Recall
Q: 312. Burst Balloons: which pattern, and what is the insight?
A: Interval DP. Choosing the last balloon of an interval fixes its neighbors and separates the two sides.

Q: 312. Burst Balloons: what is the analogy, and what does it stand for?
A: Who leaves the party last: doormen → excluded borders; last → split.

Q: 312. Burst Balloons: Why choose the last balloon?
A: Its neighbors are fixed and sides independent.

Q: 312. Burst Balloons: what are the time and space costs?
A: Time O(n³). Space O(n²).

@@ 3 | TOPIC | 263 — 10. Regular Expression Matching | Hard · Two-index matching | MUST
# 263 — 10. Regular Expression Matching

**Problem 263 – Topic: Dynamic Programming (1D → 2D → Advanced) | Difficulty: Hard | Pattern: Two-index matching**

[Open on LeetCode](https://leetcode.com/problems/regular-expression-matching/)

## 1. Problem Statement
Implement matching of the whole string s against pattern p, where `.` matches any single character and `*` matches zero or more of the element before it.

### Examples
```text
Input:  s = "aa", p = "a"
Output: false

Input:  s = "aa", p = "a*"
Output: true

Input:  s = "ab", p = ".*"
Output: true
```

### Constraints
```text
1 <= s.length <= 20
1 <= p.length <= 20
s contains only lowercase English letters.
p contains only lowercase English letters, '.', and '*'.
It is guaranteed for each appearance of the character '*', there will be a previous valid character to match.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A stencil with stretchy pieces
The pattern is a stencil where a starred piece is stretchy. It may vanish entirely, or swallow one matching character and stay to swallow more. Every other piece matches exactly one.

| In the story | In the solution |
|---|---|
| vanish | j+2 |
| swallow | i+1 |

### Pattern signal
**Two-index matching.** The two indices alone decide whether the rest matches.

### Thought process
1. **First idea:** Backtrack over every star length, repeating states.
2. **The insight:** The two indices alone decide whether the rest matches.

### Optimal approach
1. At the pattern's end require the text's end.
2. Test the first character.
3. Branch vanish or swallow at a star.
4. Otherwise advance both.
5. Cache.

### Dry run
```text
s = "aa", p = "a*"
call   vanish            swallow
(0,0)  (0,2) is false    go to (1,0)
(1,0)  (1,2) is false    go to (2,0)
(2,0)  (2,2) is true     -
so every call returns true
```

### Why it works
Any star count is either zero or one plus a smaller count, so the two branches cover all matches.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isMatch(String s, String p) {
        return match(s, p, 0, 0, new Boolean[s.length() + 1][p.length() + 1]);
    }

    private boolean match(String s, String p, int i, int j, Boolean[][] memo) {
        if (j == p.length()) return i == s.length();
        if (memo[i][j] != null) return memo[i][j];
        boolean first = i < s.length() && (p.charAt(j) == '.' || p.charAt(j) == s.charAt(i));
        boolean result;
        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            result = match(s, p, i, j + 2, memo) || (first && match(s, p, i + 1, j, memo));
        } else {
            result = first && match(s, p, i + 1, j + 1, memo);
        }
        memo[i][j] = result;
        return result;
    }
}
```

### Code walkthrough
- `return i == s.length()`: The whole text must be consumed.
- `j + 2`: The starred piece vanishes.
- `match(s, p, i + 1, j, memo)`: Swallow one character and stay.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **A star is not a wildcard.** Fix: bind it to the previous element.
2. **Greedy consumption fails.** Fix: try both branches.
3. **An empty text can still match stars.** Fix: test the pattern's end.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Short | `aa, a` | `false` |
| Any | `ab, .*` | `true` |
| Zero | `a, ab*` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **44. Wildcard**: Star alone.
- [[253 — 97. Interleaving String]]: Two indices. The key idea there: The position in s3 is always i+j, so the state is just two prefix lengths.

### In a real interview
Say it in this order:

1. **Brute force:** Backtrack over every star length, repeating states.
2. **Insight:** The two indices alone decide whether the rest matches.
3. **Approach in a few words:** Memoized index pair.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 10. Regular Expression Matching: which pattern, and what is the insight?
A: Two-index matching. The two indices alone decide whether the rest matches.

Q: 10. Regular Expression Matching: what is the analogy, and what does it stand for?
A: A stencil with stretchy pieces: vanish → j+2; swallow → i+1.

Q: 10. Regular Expression Matching: What are star's choices?
A: Use zero, or consume one and stay.

Q: 10. Regular Expression Matching: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | Practice — Pattern summary: Dynamic Programming (1D → 2D → Advanced) | Key patterns of the topic | MUST
# Pattern summary: Dynamic Programming (1D → 2D → Advanced)

Write five things before code: state, transition, base case, order and answer. Start with memoized recursion, turn it into a table, then drop dimensions you no longer read.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Fibonacci DP | Count ways to climb using 1 or 2 steps. (70) | Wrong base cases |
| Min-cost DP | Reach the top paying each step used. (746) | The top is beyond the last index |
| Take or skip | Maximize robbery with no adjacent houses. (198) | Assuming alternate houses |
| Circular take or skip | Rob houses arranged in a circle. (213) | One house |
| Expand centers | Find the longest palindromic substring. (5, 647) | Missing even lengths |
| Two-choice count | Count decodings of a digit string. (91) | A zero cannot stand alone |
| Unbounded knapsack | Find fewest coins for an amount. (322, 279) | Choosing the largest coin |
| Max and min state | Find the largest subarray product. (152) | Keeping only the maximum |
| Segmentation DP | Decide whether a string splits into dictionary words. (139) | Greedy longest word |
| Patience sorting | Find the longest strictly increasing subsequence. (300) | The tails are not the actual subsequence |
| 0/1 knapsack | Split values into two equal sums. (416) | Upward iteration reuses values |
| Bottom-up path | Find the minimum top-to-bottom triangle sum. (120) | Greedy smaller child from the top |
| Value take or skip | Earn values while deleting neighbors v-1 and v+1. (740) | Treating non-adjacent values as conflicting |
| Stack and prefix | Maximize subarray minimum times sum. (1856) | Taking modulo before comparing |
| Product of threes | Break n into integers with maximum product. (343) | Leaving a final 1 |
| Length and count | Count longest increasing subsequences. (673) | Counting one way per endpoint |
| Build from previous row | Generate Pascal's triangle. (118) | Reusing a mutated row |
| Grid count | Count right/down paths in a grid. (62) | Wrong border base |
| Rolling grid count | Count grid paths avoiding obstacles. (63) | A blocked start |
| Two-string DP | Find the longest common subsequence. (1143, 72) | Confusing it with substring |
| Combination knapsack | Count coin combinations for an amount. (518) | Swapped loops count permutations |
| Index and sum | Count sign assignments reaching a target. (494) | Memoizing the sum alone |
| Two-prefix DP | Decide whether a string interleaves two others. (97) | A greedy merge |
| Memoized path | Find the minimum right/down path sum. (64) | Wrong first row or column |
| Square DP | Find the largest square of ones. (221) | Returning side instead of area |
| Minimax DP | Maximize Alice's stones with an adaptable take limit. (1140) | Treating the opponent as random |
| State machine | Maximize stock profit with a one-day cooldown. (309, 1220) | Cooldown after buying |
| DAG memo | Find the longest increasing grid path. (329) | Allowing equal moves |
| Matching count | Count subsequences of s equal to t. (115) | An empty target has one way |
| Interval DP | Maximize coins from bursting balloons. (312) | Choosing the first breaks independence |
| Two-index matching | Match a full string with dot and star. (10) | Star modifies the preceding character |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| ways from a few earlier steps | sticky notes on stairs | linear DP | 70, 746, 91 |
| can't take neighbors | alternate dinner invitations | take or skip | 198, 213, 740 |
| reuse items | a vending machine restocked | forward knapsack | 322, 279, 518 |
| use once | one copy on the shelf | backward knapsack | 416, 494 |
| moves in a grid | water collecting downhill | grid DP | 62, 63, 64, 221 |
| two strings | two bookmarks | two-prefix DP | 1143, 72, 97, 115, 10 |
| modes | a traffic light | state machine | 309, 1220 |
| opponent | what they leave you | minimax | 1140 |
| interval, order of removal | who leaves last | interval DP | 312 |

## How do I build a DP?

1. Write the brute-force recursion.
2. Its changing arguments are the **state**.
3. Memoize it.
4. Reverse the dependencies into loops.
5. Keep only the rows you still read.

Q: Which signals point to Dynamic Programming (1D → 2D → Advanced)?
A: number of ways; minimum or maximum; repeated subproblems.

Q: What is the core idea of Dynamic Programming (1D → 2D → Advanced)?
A: Write five things before code: state, transition, base case, order and answer. Start with memoized recursion, turn it into a table, then drop dimensions you no longer read.
