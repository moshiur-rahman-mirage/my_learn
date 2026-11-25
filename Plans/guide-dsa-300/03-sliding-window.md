@@ 1 | MODULE | Phase 3 — Sliding Window | contiguous; substring; subarray; longest; shortest; at most k | MUST
# Phase 3: Sliding Window

Picture a frame over the array. The right edge expands to take values in, and the left edge contracts to restore the rule. Never recompute the inside of the frame.

**Signals:** contiguous; substring; subarray; longest; shortest; at most k.

## The big analogy: a train window

As the train moves, a little scenery enters on one side of the window and a little leaves on the other. You never repaint the whole view: you **update only what entered and what left**. Some windows have a fixed frame. Others are elastic, stretching to take in more and shrinking when a rule breaks.

| Kind of window | Picture | Question it answers | Meet it in |
|---|---|---|---|
| Fixed frame | a train window | best among all length-k ranges | 643, 1456, 567 |
| Elastic, shrink when broken | a bag with a weight limit | longest valid range | 3, 424, 904 |
| Elastic, shrink while valid | tightening a belt | shortest valid range | 209, 76 |
| Frame with a leaderboard | the tallest person in view | maximum of each window | 239 |

## Template
```java
int left = 0, best = 0;
for (int right = 0; right < a.length; right++) {
    add(a[right]);                      // expand
    while (invalid()) remove(a[left++]); // contract
    best = Math.max(best, right - left + 1);
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Sliding Window — Easy | 3 problems | MUST
# Sliding Window: Easy

Patterns in this group: Running minimum, Fixed window set, Fixed window sum.

@@ 3 | TOPIC | 052 — 121. Best Time to Buy and Sell Stock | Easy · Running minimum | MUST
# 052 — 121. Best Time to Buy and Sell Stock

**Problem 52 – Topic: Sliding Window | Difficulty: Easy | Pattern: Running minimum**

[Open on LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)

## 1. Problem Statement
`prices[i]` is a stock's price on day i. Choose one day to buy and a later day to sell. Return the largest profit, or 0 if none is possible.

### Examples
```text
Input:  prices = [7,1,5,3,6,4]
Output: 5

Input:  prices = [7,6,4,3,1]
Output: 0
```

### Constraints
```text
1 <= prices.length <= 10^5
0 <= prices[i] <= 10^4
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A time traveller with one regret
Walking through the days you remember one thing: the cheapest price you have passed. Each day you ask "if I sold now, having bought at that bottom, how much would I make?"

| In the story | In the solution |
|---|---|
| the remembered bottom | `cheapest` |
| selling now | the window's right edge |

### Pattern signal
**Running minimum.** For a fixed selling day the best buy is simply the minimum before it, which is one variable.

### Thought process
1. **First idea:** Try all buy and sell pairs, in O(n²).
2. **The insight:** For a fixed selling day the best buy is simply the minimum before it, which is one variable.

### Optimal approach
1. Start with the first price as cheapest.
2. Compute today's profit.
3. Keep the best.
4. Lower the cheapest if today is lower.

### Dry run
```text
prices = [7,1,5,3,6,4]
price  cheapest before  profit  best
7      7                0       0
1      7                -6      0
5      1                4       4
3      1                2       4
6      1                5       5
4      1                3       5
```

### Why it works
Every possible selling day is tried with its best possible buying day, so the optimal pair is among those checked.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxProfit(int[] prices) {
        int cheapest = prices[0], best = 0;
        for (int price : prices) {
            best = Math.max(best, price - cheapest);
            cheapest = Math.min(cheapest, price);
        }
        return best;
    }
}
```

### Code walkthrough
- `price - cheapest`: Sell today, bought at the bottom.
- `Math.min`: The bottom only moves down.
- `best = 0`: Not trading is allowed.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Global maximum minus global minimum.** The maximum may come first.
2. **Adding every rise.** That is for unlimited trades.
3. **Returning a loss.** Zero is the floor.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One day | `[5]` | `0` |
| Falling | `[3,2,1]` | `0` |
| Best last | `[2,1,4]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[024 — 122. Best Time to Buy and Sell Stock II]]: Unlimited trades, so add all rises.
- [[277 — 53. Maximum Subarray]]: The same answer on daily changes.

### In a real interview
Say this: *"For each selling day I only need the minimum so far. One pass and two variables are enough."*

## 5. Recall
Q: 121. Best Time to Buy and Sell Stock: which pattern, and what is the insight?
A: Running minimum. For a fixed selling day the best buy is simply the minimum before it, which is one variable.

Q: 121. Best Time to Buy and Sell Stock: what is the analogy, and what does it stand for?
A: A time traveller with one regret: the remembered bottom → `cheapest`; selling now → the window's right edge.

Q: 121. Best Time to Buy and Sell Stock: What is remembered during the scan?
A: The minimum price so far and the best profit.

Q: 121. Best Time to Buy and Sell Stock: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 053 — 219. Contains Duplicate II | Easy · Fixed window set | NICE
# 053 — 219. Contains Duplicate II

**Problem 53 – Topic: Sliding Window | Difficulty: Easy | Pattern: Fixed window set**

[Open on LeetCode](https://leetcode.com/problems/contains-duplicate-ii/)

## 1. Problem Statement
Return true if two equal values in `nums` are at indices at most `k` apart.

### Examples
```text
Input:  nums = [1,2,3,1], k = 3
Output: true

Input:  nums = [1,0,1,1], k = 1
Output: true

Input:  nums = [1,2,3,1,2,3], k = 2
Output: false
```

### Constraints
```text
1 <= nums.length <= 10^5
-10^9 <= nums[i] <= 10^9
0 <= k <= 10^5
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A doorman with a short memory
This doorman remembers only the last k guests. Each newcomer is compared with that memory, and the oldest name fades as a new one enters.

| In the story | In the solution |
|---|---|
| the memory | a set of the last k values |
| fading | removing `nums[i - k]` |

### Pattern signal
**Fixed window set.** Only the last k values matter, and a set answers membership at once.

### Thought process
1. **First idea:** Compare each value with the next k, in O(n·k).
2. **The insight:** Only the last k values matter, and a set answers membership at once.

### Optimal approach
1. Try adding the current value.
2. Succeed if already present.
3. Evict the value that will be too far for the next step.

### Dry run
```text
nums = [1,2,1], k = 1
i  x  set before  action
0  1  {}          add
1  2  {1}         add, evict 1
2  1  {2}         add, evict 2
Result: false, the 1s are two apart
```

### Why it works
Before step i the set holds exactly the values at the previous k indices, so membership equals a close duplicate.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> recent = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (!recent.add(nums[i])) return true;
            if (i >= k) recent.remove(nums[i - k]);
        }
        return false;
    }
}
```

### Code walkthrough
- `!recent.add(nums[i])`: The face is still remembered.
- `i >= k`: The memory is now one over capacity.
- `nums[i - k]`: The name that fades.

### Complexity
- **Time:** O(n) on average
- **Space:** O(min(n, k))

### Common mistakes
1. **Evicting before checking.** A distance of exactly k is missed.
2. **Keeping all values.** Distant duplicates match.
3. **Failing on k = 0.** No two different indices qualify.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Exactly k | `[1,2,1], 2` | `true` |
| Zero k | `[1,1], 0` | `false` |
| One | `[1], 3` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[001 — 217. Contains Duplicate]]: Unlimited memory. The key idea there: The only question at each value is "have I seen you?", so remember values in a structure that answers it instantly.
- **220. Contains Duplicate III**: Near values too, so use a tree set or buckets.

### In a real interview
Say this: *"The distance limit is a fixed window. A set holds its values with O(1) updates."*

## 5. Recall
Q: 219. Contains Duplicate II: which pattern, and what is the insight?
A: Fixed window set. Only the last k values matter, and a set answers membership at once.

Q: 219. Contains Duplicate II: what is the analogy, and what does it stand for?
A: A doorman with a short memory: the memory → a set of the last k values; fading → removing `nums[i - k]`.

Q: 219. Contains Duplicate II: What is in the set before index i?
A: The values of the previous k indices.

Q: 219. Contains Duplicate II: what are the time and space costs?
A: Time O(n) on average. Space O(min(n, k)).

@@ 3 | TOPIC | 054 — 643. Maximum Average Subarray I | Easy · Fixed window sum | NICE
# 054 — 643. Maximum Average Subarray I

**Problem 54 – Topic: Sliding Window | Difficulty: Easy | Pattern: Fixed window sum**

[Open on LeetCode](https://leetcode.com/problems/maximum-average-subarray-i/)

## 1. Problem Statement
Find the contiguous subarray of exactly `k` values with the largest average and return that average.

### Examples
```text
Input:  nums = [1,12,-5,-6,50,3], k = 4
Output: 12.75000

Input:  nums = [5], k = 1
Output: 5.00000
```

### Constraints
```text
n == nums.length
1 <= k <= n <= 10^5
-10^4 <= nums[i] <= 10^4
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The train window
Moving one seat along, you don't recount the view. Add what appeared on the right and subtract what vanished on the left.

| In the story | In the solution |
|---|---|
| the view | the window sum |
| appeared and vanished | `nums[right]` and `nums[right - k]` |

### Pattern signal
**Fixed window sum.** Neighboring windows share k-1 values. With fixed k the largest sum is the largest average.

### Thought process
1. **First idea:** Sum every window from scratch, in O(n·k).
2. **The insight:** Neighboring windows share k-1 values. With fixed k the largest sum is the largest average.

### Optimal approach
1. Sum the first k.
2. Slide by adding and subtracting.
3. Keep the largest.
4. Divide at the end.

### Dry run
```text
nums = [1,12,-5,-6,50,3], k = 4
window        enters  leaves  sum
1,12,-5,-6    -       -       2
12,-5,-6,50   50      1       51
-5,-6,50,3    3       12      42
Answer: 51/4 = 12.75
```

### Why it works
After each slide the sum is exactly that window's sum, and every window is visited.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public double findMaxAverage(int[] nums, int k) {
        int sum = 0;
        for (int i = 0; i < k; i++) sum += nums[i];
        int best = sum;
        for (int right = k; right < nums.length; right++) {
            sum += nums[right] - nums[right - k];
            best = Math.max(best, sum);
        }
        return (double) best / k;
    }
}
```

### Code walkthrough
- `i < k`: The first view.
- `nums[right] - nums[right - k]`: In on the right, out on the left.
- `(double) best / k`: Cast first to keep the fraction.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Integer division.** The fraction is lost.
2. **Starting best at 0.** All windows may be negative.
3. **Removing `right - k + 1`.** That value is still inside.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Whole array | `[1,2], 2` | `1.5` |
| One | `[5], 1` | `5.0` |
| Negative | `[-3,-1], 1` | `-1.0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[061 — 1456. Maximum Number of Vowels in a Substring of Given Length]]: Count instead of sum. The key idea there: Only the two edge letters change the count.
- **1343. Subarrays with Average at Least Threshold**: Count the qualifying windows.

### In a real interview
Say this: *"The windows overlap, so I update a sum in O(1). I divide once at the end."*

## 5. Recall
Q: 643. Maximum Average Subarray I: which pattern, and what is the insight?
A: Fixed window sum. Neighboring windows share k-1 values. With fixed k the largest sum is the largest average.

Q: 643. Maximum Average Subarray I: what is the analogy, and what does it stand for?
A: The train window: the view → the window sum; appeared and vanished → `nums[right]` and `nums[right - k]`.

Q: 643. Maximum Average Subarray I: How is the next fixed window's sum found?
A: Add the entering value and subtract the leaving one.

Q: 643. Maximum Average Subarray I: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Review 09 | Re-solve without looking | MUST
# Review 09

Solve on a blank page. No notes, hints or solutions.

## 643. Maximum Average Subarray I
Find the largest average of k consecutive values.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/maximum-average-subarray-i/)

## 42. Trapping Rain Water
Compute rain water trapped between bars.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/trapping-rain-water/)

## 88. Merge Sorted Array
Merge two sorted arrays into the first one's spare space.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/merge-sorted-array/)

## 2215. Find the Difference of Two Arrays
For two arrays, list the distinct values found only in each.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-the-difference-of-two-arrays/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 643 | | | | |
| 42 | | | | |
| 88 | | | | |
| 2215 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[054 — 643. Maximum Average Subarray I]]
- [[051 — 42. Trapping Rain Water]]
- [[040 — 88. Merge Sorted Array]]
- [[016 — 2215. Find the Difference of Two Arrays]]

@@ 2 | MODULE | Sliding Window — Medium | 10 problems | MUST
# Sliding Window: Medium

Patterns in this group: Fixed window counts, Window without repeats, Window plus budget, Sorted window cost, At most two kinds, Fixed window count, Shrink while valid, Search for window start, Complement window.

@@ 3 | TOPIC | 055 — 438. Find All Anagrams in a String | Medium · Fixed window counts | SHOULD
# 055 — 438. Find All Anagrams in a String

**Problem 55 – Topic: Sliding Window | Difficulty: Medium | Pattern: Fixed window counts**

[Open on LeetCode](https://leetcode.com/problems/find-all-anagrams-in-a-string/)

## 1. Problem Statement
Return every start index in `s` of a substring that is an anagram of `p`.

### Examples
```text
Input:  s = "cbaebabacd", p = "abc"
Output: [0,6]

Input:  s = "abab", p = "ab"
Output: [0,1,2]
```

### Constraints
```text
1 <= s.length, p.length <= 3 * 10^4
s and p consist of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A stencil over a strip of tiles
A stencil as wide as p slides along. You compare only the inventory visible through it with p's inventory, and each slide changes two tiles.

| In the story | In the solution |
|---|---|
| stencil | a window of p's length |
| inventory | 26 counts |

### Pattern signal
**Fixed window counts.** Anagram means equal counts, and a window's counts update in O(1).

### Thought process
1. **First idea:** Sort every window, in O(n·m log m).
2. **The insight:** Anagram means equal counts, and a window's counts update in O(1).

### Optimal approach
1. Count p.
2. Add each entering letter.
3. Remove the letter leaving a full window.
4. Record the start when counts match.

### Dry run
```text
s = "cbaeb", p = "abc"
right  window  matches?
2      cba     yes, record 0
3      bae     no
4      aeb     no
```

### Why it works
The counts always describe the last m letters. Equal counts are exactly the definition of an anagram.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        int m = p.length();
        int[] need = new int[26], have = new int[26];
        for (char c : p.toCharArray()) need[c - 'a']++;
        List<Integer> starts = new ArrayList<>();
        for (int right = 0; right < s.length(); right++) {
            have[s.charAt(right) - 'a']++;
            if (right >= m) have[s.charAt(right - m) - 'a']--;
            if (Arrays.equals(need, have)) starts.add(right - m + 1);
        }
        return starts;
    }
}
```

### Code walkthrough
- `right >= m`: A tile slides out of the stencil.
- `Arrays.equals`: Only 26 comparisons. It cannot match before the stencil is full.
- `right - m + 1`: The left edge.

### Complexity
- **Time:** O(26·n + m)
- **Space:** O(1), two fixed arrays

### Common mistakes
1. **Comparing arrays with `==`.** That compares references.
2. **Recording right.** Starts are requested.
3. **Jumping past a match.** Matches overlap.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Short s | `"a", "ab"` | `[]` |
| Overlap | `"aaa", "aa"` | `[0,1]` |
| Whole | `"ba", "ab"` | `[0]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[058 — 567. Permutation in String]]: Stop at the first match. The key idea there: A permutation is equal counts, so one balance array for a sliding window is enough.
- [[002 — 242. Valid Anagram]]: One stencil position. The key idea there: Order does not matter, only how many of each letter there are.

### In a real interview
Say this: *"A fixed window of letter counts detects anagrams. Each slide updates two counts."*

## 5. Recall
Q: 438. Find All Anagrams in a String: which pattern, and what is the insight?
A: Fixed window counts. Anagram means equal counts, and a window's counts update in O(1).

Q: 438. Find All Anagrams in a String: what is the analogy, and what does it stand for?
A: A stencil over a strip of tiles: stencil → a window of p's length; inventory → 26 counts.

Q: 438. Find All Anagrams in a String: When does a window match?
A: When its 26 counts equal those of p.

Q: 438. Find All Anagrams in a String: what are the time and space costs?
A: Time O(26·n + m). Space O(1), two fixed arrays.

@@ 3 | TOPIC | 056 — 3. Longest Substring Without Repeating Characters | Medium · Window without repeats | MUST
# 056 — 3. Longest Substring Without Repeating Characters

**Problem 56 – Topic: Sliding Window | Difficulty: Medium | Pattern: Window without repeats**

[Open on LeetCode](https://leetcode.com/problems/longest-substring-without-repeating-characters/)

## 1. Problem Statement
Return the length of the longest substring of `s` with no repeated character.

### Examples
```text
Input:  s = "abcabcbb"
Output: 3

Input:  s = "bbbbb"
Output: 1

Input:  s = "pwwkew"
Output: 3
```

### Constraints
```text
0 <= s.length <= 10^5
s consists of English letters, digits, symbols and spaces.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A bus with no twins allowed
Passengers board at the front. When someone's twin is already aboard, everyone up to and including that twin leaves by the back door. You can jump straight there if you noted each person's seat.

| In the story | In the solution |
|---|---|
| back door | `left` |
| seat note | the last index of each character |

### Pattern signal
**Window without repeats.** A repeat dooms every start up to the earlier copy, so left can jump past it and never return.

### Thought process
1. **First idea:** Test every substring, up to O(n³).
2. **The insight:** A repeat dooms every start up to the earlier copy, so left can jump past it and never return.

### Optimal approach
1. Read each character.
2. If its last index is inside the window move left past it.
3. Save the new index.
4. Record the width.

### Dry run
```text
s = "abba"
right  char  left after  window  best
0      a     0           a       1
1      b     0           ab      2
2      b     2           b       2
3      a     2           ba      2
```

### Why it works
The window is the longest valid one ending at right. Every longer one contains both copies of some character.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeat = new HashMap<>();
        int left = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            Integer twin = lastSeat.put(s.charAt(right), right);
            if (twin != null && twin >= left) left = twin + 1;
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
```

### Code walkthrough
- `lastSeat.put`: Notes the new seat and returns the old one.
- `twin >= left`: A twin who already left doesn't count.
- `twin + 1`: The twin must leave too.

### Complexity
- **Time:** O(n)
- **Space:** O(min(n, alphabet))

### Common mistakes
1. **Moving left backwards.** "abba" would give 3.
2. **A 26-slot array.** Digits, symbols and spaces occur.
3. **Clearing everything on a repeat.** "dvdf" loses its answer.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `""` | `0` |
| All same | `"bbb"` | `1` |
| Space | `"a b"` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[060 — 904. Fruit Into Baskets]]: Two kinds allowed. The key idea there: Adding never reduces the kinds and removing from the left never increases them, so a window works.
- **340. At Most K Distinct Characters**: Counts with k kinds.

### In a real interview
Say this: *"I keep the longest valid window ending here. Last indices let left jump, and it only moves forward."*

## 5. Recall
Q: 3. Longest Substring Without Repeating Characters: which pattern, and what is the insight?
A: Window without repeats. A repeat dooms every start up to the earlier copy, so left can jump past it and never return.

Q: 3. Longest Substring Without Repeating Characters: what is the analogy, and what does it stand for?
A: A bus with no twins allowed: back door → `left`; seat note → the last index of each character.

Q: 3. Longest Substring Without Repeating Characters: Why check that the twin index is at least left?
A: An older copy outside the window must not move left backwards.

Q: 3. Longest Substring Without Repeating Characters: what are the time and space costs?
A: Time O(n). Space O(min(n, alphabet)).

@@ 3 | TOPIC | 057 — 424. Longest Repeating Character Replacement | Medium · Window plus budget | MUST
# 057 — 424. Longest Repeating Character Replacement

**Problem 57 – Topic: Sliding Window | Difficulty: Medium | Pattern: Window plus budget**

[Open on LeetCode](https://leetcode.com/problems/longest-repeating-character-replacement/)

## 1. Problem Statement
You may change at most `k` characters of an uppercase string. Return the longest substring that can become a single repeated letter.

### Examples
```text
Input:  s = "ABAB", k = 2
Output: 4

Input:  s = "AABABBA", k = 1
Output: 4
```

### Constraints
```text
1 <= s.length <= 10^5
s consists of only uppercase English letters.
0 <= k <= s.length
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Repainting a fence
A stretch of fence should be one colour and you have k tins of paint. Keep the colour it already has most of and repaint the others. When that needs more than k tins, shorten the stretch.

| In the story | In the solution |
|---|---|
| commonest colour | `top` |
| tins needed | width minus `top` |

### Pattern signal
**Window plus budget.** A window's cost is its width minus its top count, and it can slide.

### Thought process
1. **First idea:** Check every substring's majority, in O(26·n²).
2. **The insight:** A window's cost is its width minus its top count, and it can slide.

### Optimal approach
1. Add the right letter.
2. Update the top frequency.
3. Move left once if the cost exceeds k.
4. Record the width.

### Dry run
```text
s = "AABABBA", k = 1
right  letter  left  top  width
0      A       0     1    1
1      A       0     2    2
2      B       0     2    3
3      A       0     3    4
4      B       1     3    4
5      B       2     3    4
6      A       3     3    4
Answer: 4
```

### Why it works
Top may be stale after a shrink, but it was real once. The window only grows past the best when a larger real top pays for it, so the best is never overstated.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int left = 0, top = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            top = Math.max(top, ++count[s.charAt(right) - 'A']);
            if (right - left + 1 - top > k) count[s.charAt(left++) - 'A']--;
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
```

### Code walkthrough
- `++count`: A plank joins the stretch.
- `right - left + 1 - top`: Planks to repaint.
- `left++`: One step is enough, we only seek wider windows.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Counting colour changes.** The cost is the non-majority letters.
2. **Only trying the first letter's colour.** Any may be the majority.
3. **Using 'a' as the base.** The letters are uppercase.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| No paint | `"AAB", 0` | `2` |
| Enough paint | `"ABC", 2` | `3` |
| One | `"A", 0` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **1004. Max Consecutive Ones III**: The colour is fixed as 1.
- [[059 — 1838. Frequency of the Most Frequent Element]]: The cost is increments. The key idea there: The best values to raise are the closest smaller ones, which are contiguous after sorting.

### In a real interview
Say this: *"Width minus majority count is the replacement cost. I slide while it stays within k."*

## 5. Recall
Q: 424. Longest Repeating Character Replacement: which pattern, and what is the insight?
A: Window plus budget. A window's cost is its width minus its top count, and it can slide.

Q: 424. Longest Repeating Character Replacement: what is the analogy, and what does it stand for?
A: Repainting a fence: commonest colour → `top`; tins needed → width minus `top`.

Q: 424. Longest Repeating Character Replacement: What does a window cost?
A: Its width minus the count of its commonest letter.

Q: 424. Longest Repeating Character Replacement: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 058 — 567. Permutation in String | Medium · Fixed window counts | MUST
# 058 — 567. Permutation in String

**Problem 58 – Topic: Sliding Window | Difficulty: Medium | Pattern: Fixed window counts**

[Open on LeetCode](https://leetcode.com/problems/permutation-in-string/)

## 1. Problem Statement
Return true if `s2` contains some permutation of `s1` as a substring.

### Examples
```text
Input:  s1 = "ab", s2 = "eidbaooo"
Output: true

Input:  s1 = "ab", s2 = "eidboaoo"
Output: false
```

### Constraints
```text
1 <= s1.length, s2.length <= 10^4
s1 and s2 consist of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A locksmith's tumbler
The lock cares which pins are present and how many, not their order. Slide a holder of that size along s2 until its pins balance exactly.

| In the story | In the solution |
|---|---|
| pins still needed | positive balance |
| holder | the fixed window |

### Pattern signal
**Fixed window counts.** A permutation is equal counts, so one balance array for a sliding window is enough.

### Thought process
1. **First idea:** Generate the permutations, which is m! of them.
2. **The insight:** A permutation is equal counts, so one balance array for a sliding window is enough.

### Optimal approach
1. Add s1's counts.
2. Subtract each entering letter.
3. Add back each leaving letter.
4. Succeed when all are zero.

### Dry run
```text
s1 = "ab", s2 = "eba"
right  in  out  non-zero balance
start  -   -    a1 b1
0      e   -    a1 b1 e-1
1      b   -    a1 e-1
2      a   e    none, return true
```

### Why it works
The balance is need minus window. All zero means identical counts, and so a permutation.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean checkInclusion(String s1, String s2) {
        int m = s1.length();
        int[] balance = new int[26], zero = new int[26];
        for (char c : s1.toCharArray()) balance[c - 'a']++;
        for (int right = 0; right < s2.length(); right++) {
            balance[s2.charAt(right) - 'a']--;
            if (right >= m) balance[s2.charAt(right - m) - 'a']++;
            if (Arrays.equals(balance, zero)) return true;
        }
        return false;
    }
}
```

### Code walkthrough
- `balance[c - 'a']++`: The pins the lock needs.
- `right >= m`: A pin falls out of the holder.
- `Arrays.equals(balance, zero)`: Nothing owed, nothing spare.

### Complexity
- **Time:** O(26·n + m)
- **Space:** O(1)

### Common mistakes
1. **Allowing gaps.** It must be a substring.
2. **Reversing the arguments.** s1 is the pattern.
3. **Checking presence only.** Counts matter.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Longer pattern | `"ab", "a"` | `false` |
| Duplicates | `"aab", "aba"` | `true` |
| One | `"a", "a"` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[055 — 438. Find All Anagrams in a String]]: Record every match. The key idea there: Anagram means equal counts, and a window's counts update in O(1).
- [[065 — 76. Minimum Window Substring]]: Extras allowed, so the frame is elastic.

### In a real interview
Say this: *"A permutation means equal frequencies. I slide a fixed window and track their difference."*

## 5. Recall
Q: 567. Permutation in String: which pattern, and what is the insight?
A: Fixed window counts. A permutation is equal counts, so one balance array for a sliding window is enough.

Q: 567. Permutation in String: what is the analogy, and what does it stand for?
A: A locksmith's tumbler: pins still needed → positive balance; holder → the fixed window.

Q: 567. Permutation in String: What does a zero balance mean?
A: The window has exactly the pattern's counts.

Q: 567. Permutation in String: what are the time and space costs?
A: Time O(26·n + m). Space O(1).

@@ 3 | TOPIC | 059 — 1838. Frequency of the Most Frequent Element | Medium · Sorted window cost | SHOULD
# 059 — 1838. Frequency of the Most Frequent Element

**Problem 59 – Topic: Sliding Window | Difficulty: Medium | Pattern: Sorted window cost**

[Open on LeetCode](https://leetcode.com/problems/frequency-of-the-most-frequent-element/)

## 1. Problem Statement
One operation adds 1 to one element. With at most `k` operations, return how many elements can be made equal at most.

### Examples
```text
Input:  nums = [1,2,4], k = 5
Output: 3

Input:  nums = [1,4,8,13], k = 5
Output: 2

Input:  nums = [3,9,6], k = 2
Output: 1
```

### Constraints
```text
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^5
1 <= k <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Levelling a staircase with k bricks
You can only build up. Sort the steps. Any stretch can be levelled to its tallest, rightmost step, and the bricks needed are the empty space above the others.

| In the story | In the solution |
|---|---|
| tallest step | `nums[right]` |
| bricks needed | width × right value − window sum |

### Pattern signal
**Sorted window cost.** The best values to raise are the closest smaller ones, which are contiguous after sorting.

### Thought process
1. **First idea:** For each target raise the nearest smaller values one by one, in O(n²).
2. **The insight:** The best values to raise are the closest smaller ones, which are contiguous after sorting.

### Optimal approach
1. Sort.
2. Add the right value.
3. Compute the bricks.
4. Shrink while above k.
5. Record the width.

### Dry run
```text
nums = [1,2,4], k = 5
right  window  sum  bricks      best
0      1       1    1-1 = 0     1
1      1,2     3    4-3 = 1     2
2      1,2,4   7    12-7 = 5    3
```

### Why it works
For a fixed target the cheapest helpers are contiguous. When the target rises an invalid left never becomes valid again.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxFrequency(int[] nums, int k) {
        Arrays.sort(nums);
        long sum = 0;
        int left = 0, best = 0;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while ((long) (right - left + 1) * nums[right] - sum > k) sum -= nums[left++];
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
```

### Code walkthrough
- `Arrays.sort`: Makes the staircase.
- `(long)`: The area can reach ten billion.
- `- sum`: Subtract the bricks already there.

### Complexity
- **Time:** O(n log n)
- **Space:** O(log n) sort stack

### Common mistakes
1. **Int multiplication.** It overflows.
2. **Choosing a target above the largest in the window.** That wastes bricks.
3. **Forgetting to sort.** The formula is wrong.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5], 1` | `1` |
| No pair | `[1,9], 2` | `1` |
| Equal | `[4,4], 1` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[057 — 424. Longest Repeating Character Replacement]]: Cost is a count. The key idea there: A window's cost is its width minus its top count, and it can slide.
- [[099 — 2616. Minimize the Maximum Difference of Pairs]]: Sorting also makes good groups adjacent.

### In a real interview
Say this: *"After sorting, a window is raised to its right value. The cost uses the running sum."*

## 5. Recall
Q: 1838. Frequency of the Most Frequent Element: which pattern, and what is the insight?
A: Sorted window cost. The best values to raise are the closest smaller ones, which are contiguous after sorting.

Q: 1838. Frequency of the Most Frequent Element: what is the analogy, and what does it stand for?
A: Levelling a staircase with k bricks: tallest step → `nums[right]`; bricks needed → width × right value − window sum.

Q: 1838. Frequency of the Most Frequent Element: What does levelling a sorted window cost?
A: Width times the right value, minus the sum.

Q: 1838. Frequency of the Most Frequent Element: what are the time and space costs?
A: Time O(n log n). Space O(log n) sort stack.

@@ 3 | TOPIC | 060 — 904. Fruit Into Baskets | Medium · At most two kinds | SHOULD
# 060 — 904. Fruit Into Baskets

**Problem 60 – Topic: Sliding Window | Difficulty: Medium | Pattern: At most two kinds**

[Open on LeetCode](https://leetcode.com/problems/fruit-into-baskets/)

## 1. Problem Statement
Each tree bears one fruit type. From any start you pick one fruit per tree moving right, with two baskets that each hold one type. Return the most fruit you can pick: the longest subarray with at most two distinct values.

### Examples
```text
Input:  fruits = [1,2,1]
Output: 3

Input:  fruits = [0,1,2,2]
Output: 3

Input:  fruits = [1,2,3,2,2]
Output: 4
```

### Constraints
```text
1 <= fruits.length <= 10^5
0 <= fruits[i] < fruits.length
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two baskets and a third fruit
A third kind appears, so you tip fruit out from the oldest end until one kind is completely gone. Then the new one has a basket.

| In the story | In the solution |
|---|---|
| baskets | the count map |
| completely gone | a count of zero |

### Pattern signal
**At most two kinds.** Adding never reduces the kinds and removing from the left never increases them, so a window works.

### Thought process
1. **First idea:** Walk from every start, in O(n²).
2. **The insight:** Adding never reduces the kinds and removing from the left never increases them, so a window works.

### Optimal approach
1. Count the right fruit.
2. While three kinds exist remove the left one.
3. Delete zero counts.
4. Record the width.

### Dry run
```text
fruits = [1,2,3,2,2]
right  kinds before shrink  left  best
0      1                    0     1
1      1,2                  0     2
2      1,2,3                1     2
3      2,3                  1     3
4      2,3                  1     4
```

### Why it works
Shrinking stops at the earliest valid left for this right, so each recorded window is the longest ending there.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int totalFruit(int[] fruits) {
        Map<Integer, Integer> baskets = new HashMap<>();
        int left = 0, best = 0;
        for (int right = 0; right < fruits.length; right++) {
            baskets.merge(fruits[right], 1, Integer::sum);
            while (baskets.size() > 2) {
                int out = fruits[left++];
                if (baskets.merge(out, -1, Integer::sum) == 0) baskets.remove(out);
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
```

### Code walkthrough
- `baskets.size() > 2`: A fruit without a basket.
- `baskets.remove(out)`: An empty basket must lose its label.
- `while`: One tip-out may not empty a kind.

### Complexity
- **Time:** O(n)
- **Space:** O(1), three kinds at most

### Common mistakes
1. **Leaving zero entries.** The size stays three.
2. **Taking the two commonest types.** The picks must be contiguous.
3. **Restarting at the third kind.** The last run still counts.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[0]` | `1` |
| One kind | `[1,1,1]` | `3` |
| All different | `[0,1,2]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **340. At Most K Distinct**: Replace 2 by k.
- [[056 — 3. Longest Substring Without Repeating Characters]]: Each kind at most once. The key idea there: A repeat dooms every start up to the earlier copy, so left can jump past it and never return.

### In a real interview
Say this: *"This is the longest subarray with two distinct values. Counts tell me when a kind has left."*

## 5. Recall
Q: 904. Fruit Into Baskets: which pattern, and what is the insight?
A: At most two kinds. Adding never reduces the kinds and removing from the left never increases them, so a window works.

Q: 904. Fruit Into Baskets: what is the analogy, and what does it stand for?
A: Two baskets and a third fruit: baskets → the count map; completely gone → a count of zero.

Q: 904. Fruit Into Baskets: When is a kind removed from the map?
A: When its count in the window reaches zero.

Q: 904. Fruit Into Baskets: what are the time and space costs?
A: Time O(n). Space O(1), three kinds at most.

@@ 3 | TOPIC | Practice — Review 10 | Re-solve without looking | MUST
# Review 10

Solve on a blank page. No notes, hints or solutions.

## 904. Fruit Into Baskets
Find the longest subarray with at most two distinct values.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/fruit-into-baskets/)

## 424. Longest Repeating Character Replacement
Find the longest one-letter substring after at most k replacements.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/longest-repeating-character-replacement/)

## 15. 3Sum
List unique triplets that sum to zero.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/3sum/)

## 535. Encode and Decode TinyURL
Design a URL shortener that can encode and decode.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/encode-and-decode-tinyurl/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 904 | | | | |
| 424 | | | | |
| 15 | | | | |
| 535 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[060 — 904. Fruit Into Baskets]]
- [[057 — 424. Longest Repeating Character Replacement]]
- [[046 — 15. 3Sum]]
- [[022 — 535. Encode and Decode TinyURL]]

@@ 3 | TOPIC | 061 — 1456. Maximum Number of Vowels in a Substring of Given Length | Medium · Fixed window count | SHOULD
# 061 — 1456. Maximum Number of Vowels in a Substring of Given Length

**Problem 61 – Topic: Sliding Window | Difficulty: Medium | Pattern: Fixed window count**

[Open on LeetCode](https://leetcode.com/problems/maximum-number-of-vowels-in-a-substring-of-given-length/)

## 1. Problem Statement
Return the most vowels (a, e, i, o, u) in any substring of length `k`.

### Examples
```text
Input:  s = "abciiidef", k = 3
Output: 3

Input:  s = "aeiou", k = 2
Output: 2

Input:  s = "leetcode", k = 3
Output: 2
```

### Constraints
```text
1 <= s.length <= 10^5
s consists of lowercase English letters.
1 <= k <= s.length
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Counting red cars through a tunnel
Exactly k cars fit in the tunnel. Add one when a red car enters, subtract one when a red car exits, and note the highest reading.

| In the story | In the solution |
|---|---|
| red car | a vowel |
| reading | `count` |

### Pattern signal
**Fixed window count.** Only the two edge letters change the count.

### Thought process
1. **First idea:** Count each window, in O(n·k).
2. **The insight:** Only the two edge letters change the count.

### Optimal approach
1. Add an entering vowel.
2. Subtract a leaving vowel.
3. Keep the maximum.

### Dry run
```text
s = "abci", k = 2
right  in  out  count  best
0      a   -    1      1
1      b   -    1      1
2      c   a    0      1
3      i   b    1      1
```

### Why it works
After each step the count equals the vowels in the last k letters. Partial first windows can't exceed the first full one.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxVowels(String s, int k) {
        int count = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            if (isVowel(s.charAt(right))) count++;
            if (right >= k && isVowel(s.charAt(right - k))) count--;
            best = Math.max(best, count);
        }
        return best;
    }

    private boolean isVowel(char c) {
        return "aeiou".indexOf(c) >= 0;
    }
}
```

### Code walkthrough
- `isVowel(s.charAt(right))`: A car enters.
- `right - k`: The car now outside.
- `indexOf(c) >= 0`: A five-letter lookup is constant.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Counting y.** It isn't in the list.
2. **Removing `right - k + 1`.** It is still inside.
3. **Recounting.** Use the two changes.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `"xyz", 2` | `0` |
| All | `"aei", 3` | `3` |
| One | `"a", 1` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[054 — 643. Maximum Average Subarray I]]: A sum. The key idea there: Neighboring windows share k-1 values. With fixed k the largest sum is the largest average.
- [[055 — 438. Find All Anagrams in a String]]: 26 readings. The key idea there: Anagram means equal counts, and a window's counts update in O(1).

### In a real interview
Say this: *"It is a fixed window with one counter. Each slide is constant time."*

## 5. Recall
Q: 1456. Maximum Number of Vowels in a Substring of Given Length: which pattern, and what is the insight?
A: Fixed window count. Only the two edge letters change the count.

Q: 1456. Maximum Number of Vowels in a Substring of Given Length: what is the analogy, and what does it stand for?
A: Counting red cars through a tunnel: red car → a vowel; reading → `count`.

Q: 1456. Maximum Number of Vowels in a Substring of Given Length: Which letters change the count on a slide?
A: Only the entering and the leaving one.

Q: 1456. Maximum Number of Vowels in a Substring of Given Length: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 062 — 209. Minimum Size Subarray Sum | Medium · Shrink while valid | SHOULD
# 062 — 209. Minimum Size Subarray Sum

**Problem 62 – Topic: Sliding Window | Difficulty: Medium | Pattern: Shrink while valid**

[Open on LeetCode](https://leetcode.com/problems/minimum-size-subarray-sum/)

## 1. Problem Statement
All values are positive. Return the shortest length of a contiguous subarray with sum at least `target`, or 0 if none.

### Examples
```text
Input:  target = 7, nums = [2,3,1,2,4,3]
Output: 2

Input:  target = 4, nums = [1,4,4]
Output: 1

Input:  target = 11, nums = [1,1,1,1,1,1,1,1]
Output: 0
```

### Constraints
```text
1 <= target <= 10^9
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Tightening a belt
Let the belt out until it holds enough. Then pull it one hole tighter at a time while it still holds, noting the tightest hole. When it fails, let it out again.

| In the story | In the solution |
|---|---|
| holds enough | sum at least target |
| one hole tighter | removing the left value |

### Pattern signal
**Shrink while valid.** With positive values growing always adds and shrinking always subtracts, so neither edge backtracks.

### Thought process
1. **First idea:** Extend from each start, in O(n²).
2. **The insight:** With positive values growing always adds and shrinking always subtracts, so neither edge backtracks.

### Optimal approach
1. Add the right value.
2. While enough record and remove the left.
3. Return the best or zero.

### Dry run
```text
target = 7, nums = [2,3,1,2,4,3]
right  sum  windows recorded      left after  sum left
3      8    [2,3,1,2] 4           1           6
4      10   [3,1,2,4] 4, [1,2,4] 3  3         6
5      9    [2,4,3] 3, [4,3] 2    5           3
```

### Why it works
For each left the first sufficient right is its shortest answer, so after recording it that left can go.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                best = Math.min(best, right - left + 1);
                sum -= nums[left++];
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }
}
```

### Code walkthrough
- `sum >= target`: Shrink while valid, unlike the longest-window problems.
- `Math.min`: Note the hole before tightening.
- `? 0 : best`: The belt never held.

### Complexity
- **Time:** O(n), each edge moves at most n times
- **Space:** O(1)

### Common mistakes
1. **Recording after the shrink.** The window is then invalid.
2. **Using it with negatives.** The proof fails.
3. **Requiring equality.** At least is enough.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Impossible | `9, [1,2]` | `0` |
| One value | `3, [1,5]` | `1` |
| Whole | `3, [1,1,1]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[065 — 76. Minimum Window Substring]]: Enough means all letters. The key idea there: Validity only needs counts and one number saying how many required copies are missing.
- **862. Shortest Subarray with Sum at Least K**: Negatives need prefix sums and a deque.

### In a real interview
Say this: *"Positive values make the sum monotonic in both edges. I expand until valid and shrink while valid."*

## 5. Recall
Q: 209. Minimum Size Subarray Sum: which pattern, and what is the insight?
A: Shrink while valid. With positive values growing always adds and shrinking always subtracts, so neither edge backtracks.

Q: 209. Minimum Size Subarray Sum: what is the analogy, and what does it stand for?
A: Tightening a belt: holds enough → sum at least target; one hole tighter → removing the left value.

Q: 209. Minimum Size Subarray Sum: When is a shortest-window answer recorded?
A: While valid, just before each shrink.

Q: 209. Minimum Size Subarray Sum: what are the time and space costs?
A: Time O(n), each edge moves at most n times. Space O(1).

@@ 3 | TOPIC | 063 — 658. Find K Closest Elements | Medium · Search for window start | SHOULD
# 063 — 658. Find K Closest Elements

**Problem 63 – Topic: Sliding Window | Difficulty: Medium | Pattern: Search for window start**

[Open on LeetCode](https://leetcode.com/problems/find-k-closest-elements/)

## 1. Problem Statement
From a sorted array return, in ascending order, the k values closest to `x`. On equal distance the smaller value wins.

### Examples
```text
Input:  arr = [1,2,3,4,5], k = 4, x = 3
Output: [1,2,3,4]

Input:  arr = [1,1,2,3,4,5], k = 4, x = -1
Output: [1,1,2,3]
```

### Constraints
```text
1 <= k <= arr.length
1 <= arr.length <= 10^4
arr is sorted in ascending order.
-10^4 <= arr[i], x <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Placing a picture frame
A frame k wide must be centred on x. At any placement compare the picture just inside on the left with the one just outside on the right. If the outside one is closer, the frame and everything left of it are wrong.

| In the story | In the solution |
|---|---|
| placement | the start index |
| the two pictures | `arr[mid]` and `arr[mid + k]` |

### Pattern signal
**Search for window start.** The answer is contiguous, and "should move right" changes only once as the start grows.

### Thought process
1. **First idea:** Sort everything by distance, in O(n log n).
2. **The insight:** The answer is contiguous, and "should move right" changes only once as the start grows.

### Optimal approach
1. Search starts from zero to n minus k.
2. Compare signed gaps.
3. Move right if the outside value is closer.
4. Otherwise keep mid.
5. Copy k values.

### Dry run
```text
arr = [1,2,3,4,5], k = 4, x = 3
low  high  mid  x-left  right-x  action
0    1     0    2       2        tie keeps left, high = 0
Answer: [1,2,3,4]
```

### Why it works
If the outside value is closer, this and every earlier window are improvable. Otherwise no later window is better.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        int low = 0, high = arr.length - k;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (x - arr[mid] > arr[mid + k] - x) low = mid + 1;
            else high = mid;
        }
        List<Integer> result = new ArrayList<>();
        for (int i = low; i < low + k; i++) result.add(arr[i]);
        return result;
    }
}
```

### Code walkthrough
- `arr.length - k`: The last placement that fits.
- `x - arr[mid]`: Signed on purpose, for duplicates.
- `high = mid`: Ties go left.

### Complexity
- **Time:** O(log(n-k) + k)
- **Space:** O(1) besides the output

### Common mistakes
1. **Absolute gaps.** Equal values can send the search the wrong way.
2. **Comparing with `mid + k - 1`.** The challenger is outside.
3. **Moving right on a tie.** The smaller wins.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| All | `[1,2], 2, 9` | `[1,2]` |
| Below | `[1,2,3], 1, -5` | `[1]` |
| Above | `[1,2,3], 1, 9` | `[3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[098 — 875. Koko Eating Bananas]]: Also searches for an answer. The key idea there: Hours never rise as speed rises, so feasibility switches once.
- [[175 — 973. K Closest Points to Origin]]: Unsorted, so use a heap. The key idea there: To keep the k nearest you only need to know the farthest of those kept.

### In a real interview
Say this: *"The result is a fixed window. I binary-search its start with one boundary comparison."*

## 5. Recall
Q: 658. Find K Closest Elements: which pattern, and what is the insight?
A: Search for window start. The answer is contiguous, and "should move right" changes only once as the start grows.

Q: 658. Find K Closest Elements: what is the analogy, and what does it stand for?
A: Placing a picture frame: placement → the start index; the two pictures → `arr[mid]` and `arr[mid + k]`.

Q: 658. Find K Closest Elements: Which values are compared at a candidate start?
A: Its first value and the first one after the window.

Q: 658. Find K Closest Elements: what are the time and space costs?
A: Time O(log(n-k) + k). Space O(1) besides the output.

@@ 3 | TOPIC | 064 — 1658. Minimum Operations to Reduce X to Zero | Medium · Complement window | SHOULD
# 064 — 1658. Minimum Operations to Reduce X to Zero

**Problem 64 – Topic: Sliding Window | Difficulty: Medium | Pattern: Complement window**

[Open on LeetCode](https://leetcode.com/problems/minimum-operations-to-reduce-x-to-zero/)

## 1. Problem Statement
Each operation removes the leftmost or rightmost value and subtracts it from `x`. All values are positive. Return the fewest operations to make x exactly 0, or -1.

### Examples
```text
Input:  nums = [1,1,4,2,3], x = 5
Output: 2

Input:  nums = [5,6,7,8,9], x = 4
Output: -1

Input:  nums = [3,2,20,1,1,3], x = 10
Output: 5
```

### Constraints
```text
1 <= nums.length <= 10^5
1 <= nums[i] <= 10^4
1 <= x <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Eating a loaf from both ends
Deciding which end to slice is confusing. Look at the bread you leave instead: one piece in the middle whose weight is known. The longer it is, the fewer slices you ate.

| In the story | In the solution |
|---|---|
| bread left | a window summing to total − x |
| slices eaten | n − window length |

### Pattern signal
**Complement window.** The kept part is contiguous, so the problem becomes the longest window with a known sum.

### Thought process
1. **First idea:** Try every choice of ends, which is 2^n.
2. **The insight:** The kept part is contiguous, so the problem becomes the longest window with a known sum.

### Optimal approach
1. Compute the kept target.
2. Slide over the positive values.
3. Record the longest exact window.
4. Subtract from n.

### Dry run
```text
nums = [1,1,4,2,3], x = 5, keep 6
right  sum after shrink  left  exact width
0      1                 0     -
1      2                 0     -
2      6                 0     3
3      6                 2     2
4      5                 3     -
Answer: 5-3 = 2
```

### Why it works
Every valid removal corresponds to exactly one such middle. Minimizing one maximizes the other.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minOperations(int[] nums, int x) {
        int keep = -x;
        for (int value : nums) keep += value;
        if (keep < 0) return -1;
        int left = 0, sum = 0, longest = -1;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum > keep) sum -= nums[left++];
            if (sum == keep) longest = Math.max(longest, right - left + 1);
        }
        return longest < 0 ? -1 : nums.length - longest;
    }
}
```

### Code walkthrough
- `keep = -x`: Becomes total minus x.
- `keep < 0`: The whole loaf is too small.
- `sum > keep`: A keep of zero shrinks to an empty piece, which is valid.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Taking the bigger end.** Greedy fails.
2. **Treating keep 0 as impossible.** It means eat everything.
3. **Finding the shortest middle.** Longest means fewest slices.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Eat all | `[1,2], 3` | `2` |
| Too much | `[1], 2` | `-1` |
| One end | `[5,1], 1` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **1423. Maximum Points from Cards**: Keep the smallest fixed middle.
- [[025 — 560. Subarray Sum Equals K]]: Negatives need a map. The key idea there: A range is now minus earlier. Both now and k are known, so earlier must be now minus k.

### In a real interview
Say this: *"I invert the two-ended removal into a middle window. Positive values make it linear."*

## 5. Recall
Q: 1658. Minimum Operations to Reduce X to Zero: which pattern, and what is the insight?
A: Complement window. The kept part is contiguous, so the problem becomes the longest window with a known sum.

Q: 1658. Minimum Operations to Reduce X to Zero: what is the analogy, and what does it stand for?
A: Eating a loaf from both ends: bread left → a window summing to total − x; slices eaten → n − window length.

Q: 1658. Minimum Operations to Reduce X to Zero: What is the kept middle's target sum?
A: The total minus x.

Q: 1658. Minimum Operations to Reduce X to Zero: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 2 | MODULE | Sliding Window — Hard | 2 problems | MUST
# Sliding Window: Hard

Patterns in this group: Need and have, Monotonic deque.

@@ 3 | TOPIC | 065 — 76. Minimum Window Substring | Hard · Need and have | MUST
# 065 — 76. Minimum Window Substring

**Problem 65 – Topic: Sliding Window | Difficulty: Hard | Pattern: Need and have**

[Open on LeetCode](https://leetcode.com/problems/minimum-window-substring/)

## 1. Problem Statement
Return the shortest substring of `s` containing every character of `t` with at least its count, or "" if none. The answer is unique.

### Examples
```text
Input:  s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"

Input:  s = "a", t = "a"
Output: "a"

Input:  s = "a", t = "aa"
Output: ""
```

### Constraints
```text
m == s.length
n == t.length
1 <= m, n <= 10^5
s and t consist of uppercase and lowercase English letters.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Packing the lightest suitcase
Throw in everything in order until all items on the packing list are inside. Then pull things out from the oldest end until a required item is about to be lost. Note the size, let it go, and pack on.

| In the story | In the solution |
|---|---|
| the list with quantities | `need` |
| items still missing | `missing` |

### Pattern signal
**Need and have.** Validity only needs counts and one number saying how many required copies are missing.

### Thought process
1. **First idea:** Check every substring, at least O(n²).
2. **The insight:** Validity only needs counts and one number saying how many required copies are missing.

### Optimal approach
1. Count t.
2. Take the right character, paying a need if positive.
3. While nothing is missing record and remove the left.
4. A need turning positive makes the window invalid.

### Dry run
```text
s = "ADOBECODEBANC", t = "ABC"
right  becomes valid with  shrinks to  best
5      ADOBEC              ADOBEC      6
10     DOBECODEBA          CODEBA      6
12     ODEBANC             BANC        4
```

### Why it works
For each right the inner loop finds the latest valid left. The optimal window ends somewhere and is recorded there.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String minWindow(String s, String t) {
        int[] need = new int[128];
        for (char c : t.toCharArray()) need[c]++;
        int missing = t.length(), left = 0, start = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < s.length(); right++) {
            if (need[s.charAt(right)]-- > 0) missing--;
            while (missing == 0) {
                if (right - left + 1 < best) {
                    best = right - left + 1;
                    start = left;
                }
                if (++need[s.charAt(left++)] > 0) missing++;
            }
        }
        return best == Integer.MAX_VALUE ? "" : s.substring(start, start + best);
    }
}
```

### Code walkthrough
- `missing = t.length()`: Copies, not distinct letters.
- `need[s.charAt(right)]-- > 0`: Only a needed item reduces missing. Extras go negative.
- `++need[s.charAt(left++)] > 0`: A required item just left the case.
- `s.substring`: Built once, at the end.

### Complexity
- **Time:** O(n + m)
- **Space:** O(1), 128 counts

### Common mistakes
1. **Using a set for t.** "AA" needs two.
2. **Copying substrings while sliding.** That is quadratic.
3. **26 slots.** Both cases occur.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Impossible | `"a", "aa"` | `""` |
| Exact | `"a", "a"` | `"a"` |
| Case | `"a", "A"` | `""` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[058 — 567. Permutation in String]]: No extras. The key idea there: A permutation is equal counts, so one balance array for a sliding window is enough.
- [[062 — 209. Minimum Size Subarray Sum]]: Enough is a sum. The key idea there: With positive values growing always adds and shrinking always subtracts, so neither edge backtracks.

### In a real interview
Say this: *"I expand to valid and shrink while valid. A missing counter makes validity O(1)."*

## 5. Recall
Q: 76. Minimum Window Substring: which pattern, and what is the insight?
A: Need and have. Validity only needs counts and one number saying how many required copies are missing.

Q: 76. Minimum Window Substring: what is the analogy, and what does it stand for?
A: Packing the lightest suitcase: the list with quantities → `need`; items still missing → `missing`.

Q: 76. Minimum Window Substring: What does a negative need mean?
A: The window holds spare copies of that character.

Q: 76. Minimum Window Substring: what are the time and space costs?
A: Time O(n + m). Space O(1), 128 counts.

@@ 3 | TOPIC | 066 — 239. Sliding Window Maximum | Hard · Monotonic deque | MUST
# 066 — 239. Sliding Window Maximum

**Problem 66 – Topic: Sliding Window | Difficulty: Hard | Pattern: Monotonic deque**

[Open on LeetCode](https://leetcode.com/problems/sliding-window-maximum/)

## 1. Problem Statement
A window of size `k` slides over `nums`. Return the maximum of every window.

### Examples
```text
Input:  nums = [1,3,-1,-3,5,3,6,7], k = 3
Output: [3,3,5,5,6,7]

Input:  nums = [1], k = 1
Output: [1]
```

### Constraints
```text
1 <= nums.length <= 10^5
-10^4 <= nums[i] <= 10^4
1 <= k <= nums.length
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A queue for team captain
A newcomer who is at least as strong sends every weaker earlier player home: they are older and weaker, so will never be captain. The front is captain until too old to play.

| In the story | In the solution |
|---|---|
| the queue | indices with decreasing values |
| too old | an index outside the window |

### Pattern signal
**Monotonic deque.** A value with a later value at least as large can never win again, so delete it.

### Thought process
1. **First idea:** Scan each window, in O(n·k).
2. **The insight:** A value with a later value at least as large can never win again, so delete it.

### Optimal approach
1. Drop an expired front.
2. Pop weaker backs.
3. Append the index.
4. Record the front once full.

### Dry run
```text
nums = [1,3,-1,-3,5,3], k = 3
i  value  deque values  output
0  1      1             -
1  3      3             -
2  -1     3,-1          3
3  -3     3,-1,-3       3
4  5      5             5
5  3      5,3           5
```

### Why it works
The deque keeps every value that could still be a maximum, in decreasing order and within the window. The front is the maximum.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> queue = new ArrayDeque<>(); // indices
        int[] best = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            if (!queue.isEmpty() && queue.peekFirst() <= i - k) queue.pollFirst();
            while (!queue.isEmpty() && nums[queue.peekLast()] <= nums[i]) queue.pollLast();
            queue.addLast(i);
            if (i >= k - 1) best[i - k + 1] = nums[queue.peekFirst()];
        }
        return best;
    }
}
```

### Code walkthrough
- `queue.peekFirst() <= i - k`: The captain retires.
- `nums[queue.peekLast()] <= nums[i]`: Weaker and older, so out.
- `queue.addLast(i)`: The index tells age and value.
- `i >= k - 1`: The first full team.

### Complexity
- **Time:** O(n), each index enters and leaves once
- **Space:** O(k)

### Common mistakes
1. **Storing values.** Age is unknown.
2. **Popping the front for weakness.** Weakness is tested at the back.
3. **Re-sorting windows.** It is slower.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| k one | `[2,1], 1` | `[2,1]` |
| Whole | `[1,2], 2` | `[2]` |
| Equal | `[4,4,4], 2` | `[4,4]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **1438. Longest Subarray with Limit**: Two deques, for maximum and minimum.
- [[078 — 739. Daily Temperatures]]: The same evictions on a stack.

### In a real interview
Say this: *"A later larger value dominates earlier ones. A decreasing deque leaves the maximum in front."*

## 5. Recall
Q: 239. Sliding Window Maximum: which pattern, and what is the insight?
A: Monotonic deque. A value with a later value at least as large can never win again, so delete it.

Q: 239. Sliding Window Maximum: what is the analogy, and what does it stand for?
A: A queue for team captain: the queue → indices with decreasing values; too old → an index outside the window.

Q: 239. Sliding Window Maximum: Why can smaller back values be discarded?
A: The new value is larger and expires later.

Q: 239. Sliding Window Maximum: what are the time and space costs?
A: Time O(n), each index enters and leaves once. Space O(k).

@@ 3 | TOPIC | Practice — Review 11 | Re-solve without looking | MUST
# Review 11

Solve on a blank page. No notes, hints or solutions.

## 239. Sliding Window Maximum
Return the maximum of every length-k window.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/sliding-window-maximum/)

## 658. Find K Closest Elements
Return the k values closest to x from a sorted array.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-k-closest-elements/)

## 121. Best Time to Buy and Sell Stock
Maximize profit from one buy and a later sell.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)

## 179. Largest Number
Arrange numbers to form the largest concatenation.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/largest-number/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 239 | | | | |
| 658 | | | | |
| 121 | | | | |
| 179 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[066 — 239. Sliding Window Maximum]]
- [[063 — 658. Find K Closest Elements]]
- [[052 — 121. Best Time to Buy and Sell Stock]]
- [[028 — 179. Largest Number]]

@@ 3 | TOPIC | Practice — Pattern summary: Sliding Window | Key patterns of the topic | MUST
# Pattern summary: Sliding Window

Picture a frame over the array. The right edge expands to take values in, and the left edge contracts to restore the rule. Never recompute the inside of the frame.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Running minimum | Maximize profit from one buy and a later sell. (121) | Selling before buying |
| Fixed window set | Find equal values at indices at most k apart. (219) | A window one too wide |
| Fixed window sum | Find the largest average of k consecutive values. (643) | Integer division |
| Fixed window counts | List starts of anagrams of p inside s. (438, 567) | Leaving zero-count keys in the map |
| Window without repeats | Find the longest substring with no repeated character. (3) | Moving left backwards |
| Window plus budget | Find the longest one-letter substring after at most k replacements. (424) | Replacing the majority letter |
| Sorted window cost | Maximize equal values using k increments. (1838) | Int overflow in width times value |
| At most two kinds | Find the longest subarray with at most two distinct values. (904) | Not removing zero-count keys |
| Fixed window count | Find the most vowels in any length-k substring. (1456) | Recounting each window |
| Shrink while valid | Find the shortest subarray with sum at least target, all values positive. (209) | Returning infinity when no window works |
| Search for window start | Return the k values closest to x from a sorted array. (658) | Absolute differences fail with duplicates here |
| Complement window | Remove fewest end values so they sum to x. (1658) | Choosing the larger end greedily |
| Need and have | Find the shortest substring of s covering all counts of t. (76) | Ignoring duplicate letters in t |
| Monotonic deque | Return the maximum of every length-k window. (239) | Storing values makes expiry ambiguous |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| length exactly k | the train window | fixed window | 643, 1456, 438, 567 |
| at most k apart | a short memory | window set | 219 |
| longest, under a rule | a bag you empty from the back | shrink when invalid | 3, 424, 904, 1838 |
| shortest, reaching a goal | tightening a belt | shrink while valid | 209, 76 |
| remove from both ends | keep the middle of the loaf | complement window | 1658 |
| maximum of every window | a queue where the weak give up | monotonic deque | 239 |

## Which pattern do I pick?

1. **Is it contiguous?** If not, this is probably not a window, unless sorting makes it one as in 1838.
2. **Is the length given?** Use a fixed frame.
3. **Longest?** Record after the window is valid again. **Shortest?** Record just before each shrink.
4. **Can growing turn an invalid window valid and invalid again at random**, for example a sum with negatives? Use prefix sums with a map instead.

Q: Which signals point to Sliding Window?
A: contiguous; substring; subarray; longest; shortest; at most k.

Q: What is the core idea of Sliding Window?
A: Picture a frame over the array. The right edge expands to take values in, and the left edge contracts to restore the rule. Never recompute the inside of the frame.
