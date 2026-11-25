@@ 1 | MODULE | Phase 2 — Two Pointers | sorted input; palindrome; pair; rewrite in place | MUST
# Phase 2: Two Pointers

Each pointer moves only one way, so there are at most 2n moves. Before moving one, prove that every pair you discard cannot be the answer.

**Signals:** sorted input; palindrome; pair; rewrite in place.

## The big analogy: two detectives searching one street

One detective checking every pair of houses needs forever. Two detectives who agree on a rule, such as "whoever has the less useful house walks on", clear the street in one walk, because **every step rules out a whole group of pairs**.

| How the detectives walk | Picture | Use it for | Meet it in |
|---|---|---|---|
| Toward each other | closing a pair of curtains | pairs, palindromes | 125, 167, 11 |
| Same direction, different speeds | a reader and an editor | rewriting in place | 283, 26, 80 |
| One on each of two streets | two zips closing | merging, matching | 392, 1768, 88 |
| Three at once | sorting laundry into three baskets | partitioning | 75 |

## Template
```java
int left = 0, right = a.length - 1;
while (left < right) {
    int sum = a[left] + a[right];
    if (sum == target) return true;
    if (sum < target) left++; // left is too small for every remaining right
    else right--;             // right is too large for every remaining left
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Two Pointers — Easy | 8 problems | MUST
# Two Pointers: Easy

Patterns in this group: Same-direction pointers, Opposite pointers, Pointers with one skip, Parallel pointers, Fill from the back, Slow and fast.

@@ 3 | TOPIC | 035 — 392. Is Subsequence | Easy · Same-direction pointers | NICE
# 035 — 392. Is Subsequence

**Problem 35 – Topic: Two Pointers | Difficulty: Easy | Pattern: Same-direction pointers**

[Open on LeetCode](https://leetcode.com/problems/is-subsequence/)

## 1. Problem Statement
Return true if `s` is a subsequence of `t`: all characters of `s` appear in `t` in the same order, not necessarily next to each other. Follow-up: handle billions of different `s` against one `t`.

### Examples
```text
Input:  s = "abc", t = "ahbgdc"
Output: true

Input:  s = "axc", t = "ahbgdc"
Output: false
```

### Constraints
```text
0 <= s.length <= 100
0 <= t.length <= 10^4
s and t consist only of lowercase English letters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A shopping list in a one-way shop
You walk the aisles once, looking only for the next item on your list. When you see it, tick it and look for the following one. You succeed if the list is finished by the exit.

| In the story | In the solution |
|---|---|
| the next unticked item | pointer `need` |
| walking the aisles | the loop over t |

### Pattern signal
**Same-direction pointers.** Taking the earliest possible match always leaves the most of t for the rest, so one forward walk is enough.

### Thought process
1. **First idea:** Try every way of deleting characters from t, which is exponential.
2. **The insight:** Taking the earliest possible match always leaves the most of t for the rest, so one forward walk is enough.

### Optimal approach
1. Point at the first needed character.
2. Walk t.
3. Advance the need on each match.
4. Succeed when every character was matched.

### Dry run
```text
s = "abc", t = "ahbgdc"
t char  needed  action
a       a       tick, need b
h       b       walk on
b       b       tick, need c
g       c       walk on
d       c       walk on
c       c       tick, list finished: true
```

### Why it works
If any valid matching exists, replacing each match with the earliest available one keeps it valid. So greedy finds one whenever one exists.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isSubsequence(String s, String t) {
        int need = 0;
        for (int i = 0; i < t.length() && need < s.length(); i++) {
            if (s.charAt(need) == t.charAt(i)) need++; // tick it
        }
        return need == s.length();
    }
}
```

### Code walkthrough
- `need < s.length()`: Leave the shop as soon as the list is done.
- `need++`: Only a match moves the list pointer.
- `need == s.length()`: Everything was ticked. This also handles an empty list.

### Complexity
- **Time:** O(t), each aisle once
- **Space:** O(1), two pointers

### Common mistakes
1. **Using `contains`.** That needs adjacent characters.
2. **Restarting in t for each character.** Order and reuse rules break.
3. **Failing an empty s.** It is a subsequence of everything.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty list | `"", "abc"` | `true` |
| Wrong order | `"ba", "ab"` | `false` |
| Empty shop | `"a", ""` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **792. Number of Matching Subsequences**: Store each letter's positions and binary-search the next one.
- [[250 — 1143. Longest Common Subsequence]]: Both strings may skip, so DP replaces greedy.

### In a real interview
Say this: *"I match each needed character at its earliest position. That never hurts the remaining matches, so one pass is enough."*

## 5. Recall
Q: 392. Is Subsequence: which pattern, and what is the insight?
A: Same-direction pointers. Taking the earliest possible match always leaves the most of t for the rest, so one forward walk is enough.

Q: 392. Is Subsequence: what is the analogy, and what does it stand for?
A: A shopping list in a one-way shop: the next unticked item → pointer `need`; walking the aisles → the loop over t.

Q: 392. Is Subsequence: Why is the earliest match safe?
A: It leaves the largest remainder of t for later characters.

Q: 392. Is Subsequence: what are the time and space costs?
A: Time O(t), each aisle once. Space O(1), two pointers.

@@ 3 | TOPIC | 036 — 125. Valid Palindrome | Easy · Opposite pointers | MUST
# 036 — 125. Valid Palindrome

**Problem 36 – Topic: Two Pointers | Difficulty: Easy | Pattern: Opposite pointers**

[Open on LeetCode](https://leetcode.com/problems/valid-palindrome/)

## 1. Problem Statement
A string is a palindrome if, after lowercasing it and removing everything except letters and digits, it reads the same both ways. Decide whether `s` is one.

### Examples
```text
Input:  s = "A man, a plan, a canal: Panama"
Output: true

Input:  s = "race a car"
Output: false

Input:  s = " "
Output: true
```

### Constraints
```text
1 <= s.length <= 2 * 10^5
s consists only of printable ASCII characters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two proofreaders and a banner
One starts at each end of a banner. Both step over decorations such as commas and spaces, then call out their letter. A single disagreement settles it, and if they meet, it is a palindrome.

| In the story | In the solution |
|---|---|
| proofreaders | `left` and `right` |
| decorations | characters that are not letters or digits |

### Pattern signal
**Opposite pointers.** A palindrome is only a claim about mirrored pairs, and those can be compared in place.

### Thought process
1. **First idea:** Build a cleaned copy and its reverse. It is O(n), but costs O(n) space.
2. **The insight:** A palindrome is only a claim about mirrored pairs, and those can be compared in place.

### Optimal approach
1. Start at both ends.
2. Skip junk on either side.
3. Compare lowercase characters.
4. Fail on a difference.
5. Move both inward.

### Dry run
```text
s = "Ab, ba"
left  right  chars       action
0     5      A and a     equal, move both
1     4      b and b     equal, move both
2     3      , and space skip comma
3     3      pointers met: true
```

### Why it works
Everything outside the pointers is either junk or a verified mirror pair. When they meet, every pair has been verified.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            char a = s.charAt(left), b = s.charAt(right);
            if (!Character.isLetterOrDigit(a)) left++;
            else if (!Character.isLetterOrDigit(b)) right--;
            else if (Character.toLowerCase(a) != Character.toLowerCase(b)) return false;
            else {
                left++;
                right--;
            }
        }
        return true;
    }
}
```

### Code walkthrough
- `left < right`: A middle character needs no partner.
- `isLetterOrDigit`: Digits count, decorations do not.
- `toLowerCase`: The proofreaders ignore capitals.

### Complexity
- **Time:** O(n), each step shrinks the gap
- **Space:** O(1)

### Common mistakes
1. **Removing digits.** "0P" must be false.
2. **An inner skip loop without a bound.** It runs off a string of only symbols.
3. **Moving both on junk.** A real character is skipped.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Only symbols | `".,"` | `true` |
| Digit and letter | `"0P"` | `false` |
| One character | `"a"` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[037 — 680. Valid Palindrome II]]: One mismatch may be forgiven. The key idea there: Pairs that already match need no deletion. At the first mismatch one of those two characters must go, so only two checks exist.
- [[233 — 5. Longest Palindromic Substring]]: The proofreaders start in the middle and walk outward.

### In a real interview
Say this: *"I compare mirrored valid characters with two pointers. It avoids building a cleaned copy."*

## 5. Recall
Q: 125. Valid Palindrome: which pattern, and what is the insight?
A: Opposite pointers. A palindrome is only a claim about mirrored pairs, and those can be compared in place.

Q: 125. Valid Palindrome: what is the analogy, and what does it stand for?
A: Two proofreaders and a banner: proofreaders → `left` and `right`; decorations → characters that are not letters or digits.

Q: 125. Valid Palindrome: What is true outside the two pointers?
A: Every valid character there has been matched with its mirror.

Q: 125. Valid Palindrome: what are the time and space costs?
A: Time O(n), each step shrinks the gap. Space O(1).

@@ 3 | TOPIC | Practice — Review 06 | Re-solve without looking | MUST
# Review 06

Solve on a blank page. No notes, hints or solutions.

## 125. Valid Palindrome
Check a palindrome ignoring case and non-alphanumeric characters.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/valid-palindrome/)

## 41. First Missing Positive
Find the smallest missing positive in O(n) time and O(1) space.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/first-missing-positive/)

## 535. Encode and Decode TinyURL
Design a URL shortener that can encode and decode.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/encode-and-decode-tinyurl/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 125 | | | | |
| 41 | | | | |
| 535 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[036 — 125. Valid Palindrome]]
- [[033 — 41. First Missing Positive]]
- [[022 — 535. Encode and Decode TinyURL]]

@@ 3 | TOPIC | 037 — 680. Valid Palindrome II | Easy · Pointers with one skip | NICE
# 037 — 680. Valid Palindrome II

**Problem 37 – Topic: Two Pointers | Difficulty: Easy | Pattern: Pointers with one skip**

[Open on LeetCode](https://leetcode.com/problems/valid-palindrome-ii/)

## 1. Problem Statement
Return true if `s` can become a palindrome by deleting at most one character.

### Examples
```text
Input:  s = "aba"
Output: true

Input:  s = "abca"
Output: true

Input:  s = "abc"
Output: false
```

### Constraints
```text
1 <= s.length <= 10^5
s consists of lowercase English letters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A zip with one bad tooth
You close a zip from both ends. At the first pair that won't lock you may snap off one tooth, left or right, and the rest must close perfectly. There is no second chance.

| In the story | In the solution |
|---|---|
| the jammed pair | the first mismatch |
| snapping a tooth | skipping left or skipping right |

### Pattern signal
**Pointers with one skip.** Pairs that already match need no deletion. At the first mismatch one of those two characters must go, so only two checks exist.

### Thought process
1. **First idea:** Delete each position in turn and check, in O(n²).
2. **The insight:** Pairs that already match need no deletion. At the first mismatch one of those two characters must go, so only two checks exist.

### Optimal approach
1. Move inward while equal.
2. On a mismatch test the range without the left character.
3. Test the range without the right one.
4. Succeed if either is a palindrome.

### Dry run
```text
s = "abca"
left  right  chars    action
0     3      a and a  equal
1     2      b and c  mismatch
skip left:  "c"  palindrome, so return true
```

### Why it works
Keeping both mismatched characters leaves them as unequal mirrors. So one of them is the deletion, and both options are tested.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean validPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return closes(s, left + 1, right) || closes(s, left, right - 1);
            }
            left++;
            right--;
        }
        return true;
    }

    private boolean closes(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) return false;
        }
        return true;
    }
}
```

### Code walkthrough
- `closes(s, left + 1, right)`: Snap the left tooth.
- `closes(s, left, right - 1)`: Or the right one.
- `return true`: No jam at all, and zero deletions are allowed.

### Complexity
- **Time:** O(n), at most two extra scans
- **Space:** O(1)

### Common mistakes
1. **Trying only one side.** "cbbcc" needs the right one removed.
2. **Continuing to allow skips.** Only one deletion exists.
3. **Building substrings per position.** That is O(n²).

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Already valid | `"aba"` | `true` |
| One deletion | `"ab"` | `true` |
| Two needed | `"abc"` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[036 — 125. Valid Palindrome]]: The same zip with junk to skip.
- **1216. Valid Palindrome III**: With k deletions, DP is needed.

### In a real interview
Say this: *"Only the first mismatch involves a decision. Both choices can be verified in linear time."*

## 5. Recall
Q: 680. Valid Palindrome II: which pattern, and what is the insight?
A: Pointers with one skip. Pairs that already match need no deletion. At the first mismatch one of those two characters must go, so only two checks exist.

Q: 680. Valid Palindrome II: what is the analogy, and what does it stand for?
A: A zip with one bad tooth: the jammed pair → the first mismatch; snapping a tooth → skipping left or skipping right.

Q: 680. Valid Palindrome II: Which characters can be deleted at a mismatch?
A: Only the left or the right one of that pair.

Q: 680. Valid Palindrome II: what are the time and space costs?
A: Time O(n), at most two extra scans. Space O(1).

@@ 3 | TOPIC | 038 — 1768. Merge Strings Alternately | Easy · Parallel pointers | NICE
# 038 — 1768. Merge Strings Alternately

**Problem 38 – Topic: Two Pointers | Difficulty: Easy | Pattern: Parallel pointers**

[Open on LeetCode](https://leetcode.com/problems/merge-strings-alternately/)

## 1. Problem Statement
Merge `word1` and `word2` by taking letters alternately, starting with `word1`. Append what remains of the longer word.

### Examples
```text
Input:  word1 = "abc", word2 = "pqr"
Output: "apbqcr"

Input:  word1 = "ab", word2 = "pqrs"
Output: "apbqrs"

Input:  word1 = "abcd", word2 = "pq"
Output: "apbqcd"
```

### Constraints
```text
1 <= word1.length, word2.length <= 100
word1 and word2 consist of lowercase English letters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two lanes merging into one
At a roadworks the rule is one car from the left, one from the right. When a lane empties, the other simply keeps driving.

| In the story | In the solution |
|---|---|
| lanes | the two words |
| the merged road | the string builder |

### Pattern signal
**Parallel pointers.** The order is forced, so the only work is walking both words with one index and a buffer.

### Thought process
1. **First idea:** Concatenate strings in a loop, which copies the result each time and costs O(n²).
2. **The insight:** The order is forced, so the only work is walking both words with one index and a buffer.

### Optimal approach
1. Loop to the longer length.
2. Append the first word's letter if it exists.
3. Append the second's if it exists.

### Dry run
```text
word1 = "ab", word2 = "pqr"
i  from word1  from word2  result
0  a           p           ap
1  b           q           apbq
2  -           r           apbqr
```

### Why it works
At step i every earlier letter of both words is placed in order. Missing letters are simply skipped.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String mergeAlternately(String word1, String word2) {
        StringBuilder road = new StringBuilder();
        int longest = Math.max(word1.length(), word2.length());
        for (int i = 0; i < longest; i++) {
            if (i < word1.length()) road.append(word1.charAt(i));
            if (i < word2.length()) road.append(word2.charAt(i));
        }
        return road.toString();
    }
}
```

### Code walkthrough
- `Math.max`: Keep going until both lanes are empty.
- `i < word1.length()`: An empty lane sends no car.
- `StringBuilder`: Appending does not recopy the road.

### Complexity
- **Time:** O(n + m)
- **Space:** O(n + m) for the result

### Common mistakes
1. **Stopping at the shorter word.** The tail is lost.
2. **Starting with word2.** The order is specified.
3. **Using `+=` on a string.** It is quadratic.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Equal | `"ab", "cd"` | `"acbd"` |
| First longer | `"abc", "d"` | `"adbc"` |
| Single letters | `"a", "b"` | `"ab"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[040 — 88. Merge Sorted Array]]: The next car is chosen by size.
- [[108 — 21. Merge Two Sorted Lists]]: The same merge on links. The key idea there: The smallest remaining node is always one of the two current heads.

### In a real interview
Say this: *"Two parallel reads and one buffer are linear. I handle the leftover in the same loop."*

## 5. Recall
Q: 1768. Merge Strings Alternately: which pattern, and what is the insight?
A: Parallel pointers. The order is forced, so the only work is walking both words with one index and a buffer.

Q: 1768. Merge Strings Alternately: what is the analogy, and what does it stand for?
A: Two lanes merging into one: lanes → the two words; the merged road → the string builder.

Q: 1768. Merge Strings Alternately: What happens when one word ends?
A: The other word's remaining letters are appended in order.

Q: 1768. Merge Strings Alternately: what are the time and space costs?
A: Time O(n + m). Space O(n + m) for the result.

@@ 3 | TOPIC | 039 — 344. Reverse String | Easy · Opposite pointers | NICE
# 039 — 344. Reverse String

**Problem 39 – Topic: Two Pointers | Difficulty: Easy | Pattern: Opposite pointers**

[Open on LeetCode](https://leetcode.com/problems/reverse-string/)

## 1. Problem Statement
Reverse the character array `s` in place with O(1) extra memory.

### Examples
```text
Input:  s = ["h","e","l","l","o"]
Output: ["o","l","l","e","h"]

Input:  s = ["H","a","n","n","a","h"]
Output: ["h","a","n","n","a","H"]
```

### Constraints
```text
1 <= s.length <= 10^5
s[i] is a printable ascii character.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A row of people changing seats
The people at the two ends swap seats, then the next two inward, until the middle is reached. Nobody needs a spare row of chairs, only a moment of standing.

| In the story | In the solution |
|---|---|
| standing for a moment | the `temp` variable |
| the two ends | `left` and `right` |

### Pattern signal
**Opposite pointers.** Reversal sends i to n-1-i and back, so the moves are independent swaps.

### Thought process
1. **First idea:** Copy into a second array backwards, using O(n) space.
2. **The insight:** Reversal sends i to n-1-i and back, so the moves are independent swaps.

### Optimal approach
1. Start at both ends.
2. Swap.
3. Move inward.
4. Stop when they meet.

### Dry run
```text
s = [h,e,l,l,o]
left  right  after
0     4      [o,e,l,l,h]
1     3      [o,l,l,e,h]
2     2      stop
```

### Why it works
Everything outside the pointers is in its final place, and each swap finishes two more positions.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void reverseString(char[] s) {
        int left = 0, right = s.length - 1;
        while (left < right) {
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            left++;
            right--;
        }
    }
}
```

### Code walkthrough
- `left < right`: The middle person stays seated.
- `char temp`: Someone must stand so a seat is free.
- `right--`: The finished end is never touched again.

### Complexity
- **Time:** O(n), n/2 swaps
- **Space:** O(1)

### Common mistakes
1. **Looping over all n.** Everything is swapped back.
2. **Returning a new array.** The input must change.
3. **No temporary.** One value is overwritten.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `["a"]` | `["a"]` |
| Two | `["a","b"]` | `["b","a"]` |
| Equal | `["x","x"]` | `["x","x"]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[049 — 189. Rotate Array]]: Three reversals make a rotation. The key idea there: A rotation is just two blocks trading places, which three reversals achieve.
- [[107 — 206. Reverse Linked List]]: No right pointer exists, so turn the links.

### In a real interview
Say this: *"Mirror positions are swapped in place. It takes n over two swaps and constant memory."*

## 5. Recall
Q: 344. Reverse String: which pattern, and what is the insight?
A: Opposite pointers. Reversal sends i to n-1-i and back, so the moves are independent swaps.

Q: 344. Reverse String: what is the analogy, and what does it stand for?
A: A row of people changing seats: standing for a moment → the `temp` variable; the two ends → `left` and `right`.

Q: 344. Reverse String: When does reversal stop?
A: When left is no longer smaller than right.

Q: 344. Reverse String: what are the time and space costs?
A: Time O(n), n/2 swaps. Space O(1).

@@ 3 | TOPIC | 040 — 88. Merge Sorted Array | Easy · Fill from the back | NICE
# 040 — 88. Merge Sorted Array

**Problem 40 – Topic: Two Pointers | Difficulty: Easy | Pattern: Fill from the back**

[Open on LeetCode](https://leetcode.com/problems/merge-sorted-array/)

## 1. Problem Statement
`nums1` has m sorted values followed by n free slots, and `nums2` has n sorted values. Merge everything into `nums1` in sorted order.

### Examples
```text
Input:  nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
Output: [1,2,2,3,5,6]

Input:  nums1 = [1], m = 1, nums2 = [], n = 0
Output: [1]

Input:  nums1 = [0], m = 0, nums2 = [1], n = 1
Output: [1]
```

### Constraints
```text
nums1.length == m + n
nums2.length == n
0 <= m, n <= 200
1 <= m + n <= 200
-10^9 <= nums1[i], nums2[j] <= 10^9
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Packing a shelf from the empty end
The free space is on the right. If you insert at the left you must shove books along. Instead place the tallest remaining book in the rightmost gap, and you never cover a book you still need.

| In the story | In the solution |
|---|---|
| rightmost gap | `write` |
| tallest remaining | the larger of the two tails |

### Pattern signal
**Fill from the back.** The free space is at the back, and so are both maxima.

### Thought process
1. **First idea:** Copy and sort in O(total log total), or shift on every insertion in O(m·n).
2. **The insight:** The free space is at the back, and so are both maxima.

### Optimal approach
1. Point at both last values and the last slot.
2. Write the larger.
3. Move that pointer and the slot left.
4. Stop when the second array is empty.

### Dry run
```text
nums1 = [1,2,3,0,0,0], nums2 = [2,5,6]
i  j  write  chosen  nums1
2  2  5      6       [1,2,3,0,0,6]
2  1  4      5       [1,2,3,0,5,6]
2  0  3      3       [1,2,3,3,5,6]
1  0  2      2       [1,2,2,3,5,6], second empty
```

### Why it works
The slot right of i's unread values always exists while j has values. The part written is always the largest values in order.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1, j = n - 1, write = m + n - 1;
        while (j >= 0) {
            if (i >= 0 && nums1[i] > nums2[j]) nums1[write--] = nums1[i--];
            else nums1[write--] = nums2[j--];
        }
    }
}
```

### Code walkthrough
- `write = m + n - 1`: The far end of the shelf.
- `j >= 0`: Books left in the first array are already in place.
- `i >= 0`: The first array may run out early.

### Complexity
- **Time:** O(m + n)
- **Space:** O(1)

### Common mistakes
1. **Merging from the front.** Unread values are overwritten.
2. **Stopping when i ends.** Remaining second values are lost.
3. **Using nums1.length as m.** The free slots are not values.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| First empty | `[0], 0, [1], 1` | `[1]` |
| Second empty | `[1], 1, [], 0` | `[1]` |
| All smaller | `[4,0], 1, [2], 1` | `[2,4]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[091 — 977. Squares of a Sorted Array]]: Also fills the largest from the back.
- [[129 — 23. Merge k Sorted Lists]]: Many fronts, chosen with a heap.

### In a real interview
Say this: *"The spare space is at the back, so I merge backwards. That protects the unread values."*

## 5. Recall
Q: 88. Merge Sorted Array: which pattern, and what is the insight?
A: Fill from the back. The free space is at the back, and so are both maxima.

Q: 88. Merge Sorted Array: what is the analogy, and what does it stand for?
A: Packing a shelf from the empty end: rightmost gap → `write`; tallest remaining → the larger of the two tails.

Q: 88. Merge Sorted Array: Why merge from the back?
A: The free slots are there, so nothing unread is overwritten.

Q: 88. Merge Sorted Array: what are the time and space costs?
A: Time O(m + n). Space O(1).

@@ 3 | TOPIC | 041 — 283. Move Zeroes | Easy · Slow and fast | NICE
# 041 — 283. Move Zeroes

**Problem 41 – Topic: Two Pointers | Difficulty: Easy | Pattern: Slow and fast**

[Open on LeetCode](https://leetcode.com/problems/move-zeroes/)

## 1. Problem Statement
Move every 0 in `nums` to the end in place, keeping the order of the other values.

### Examples
```text
Input:  nums = [0,1,0,3,12]
Output: [1,3,12,0,0]

Input:  nums = [0]
Output: [0]
```

### Constraints
```text
1 <= nums.length <= 10^4
-2^31 <= nums[i] <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A reader and a seating host
The reader walks along a queue. Whenever they find a real guest, the host gives that guest the first seat not yet settled, swapping out whatever was there. Empty coats, the zeros, drift to the back.

| In the story | In the solution |
|---|---|
| reader | `read` |
| first unsettled seat | `write` |

### Pattern signal
**Slow and fast.** Only the non-zero order matters, so write those compactly and the zeros take care of themselves.

### Thought process
1. **First idea:** Remove each zero by shifting, in O(n²).
2. **The insight:** Only the non-zero order matters, so write those compactly and the zeros take care of themselves.

### Optimal approach
1. Keep a write position.
2. Read every value.
3. Swap each non-zero into write.
4. Advance write.

### Dry run
```text
nums = [0,1,0,3,12]
read  value  write before  array after
0     0      0             [0,1,0,3,12]
1     1      0             [1,0,0,3,12]
2     0      1             [1,0,0,3,12]
3     3      1             [1,3,0,0,12]
4     12     2             [1,3,12,0,0]
```

### Why it works
Before write are the non-zeros seen, in order. From write to read there are only zeros, so a swap never reorders non-zeros.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void moveZeroes(int[] nums) {
        int write = 0;
        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != 0) {
                int temp = nums[write];
                nums[write] = nums[read];
                nums[read] = temp;
                write++;
            }
        }
    }
}
```

### Code walkthrough
- `nums[read] != 0`: A real guest needs a seat.
- `nums[read] = temp`: The zero moves to the reader's place.
- `write++`: One more seat is settled.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Swapping with the last value.** The order breaks.
2. **Advancing write on zeros.** Holes stay in front.
3. **Testing `> 0`.** Negatives are non-zero too.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| No zero | `[1,2]` | `[1,2]` |
| All zero | `[0,0]` | `[0,0]` |
| Negative | `[0,-1]` | `[-1,0]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[005 — 27. Remove Element]]: Order is free, so swap from the back.
- [[043 — 75. Sort Colors]]: Three regions. The key idea there: With three values, placing the 0s and 2s at the ends also places the 1s.

### In a real interview
Say this: *"A slow pointer marks where the next non-zero belongs. Swapping keeps their order and pushes zeros back."*

## 5. Recall
Q: 283. Move Zeroes: which pattern, and what is the insight?
A: Slow and fast. Only the non-zero order matters, so write those compactly and the zeros take care of themselves.

Q: 283. Move Zeroes: what is the analogy, and what does it stand for?
A: A reader and a seating host: reader → `read`; first unsettled seat → `write`.

Q: 283. Move Zeroes: What lies between write and read?
A: Only zeros.

Q: 283. Move Zeroes: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 042 — 26. Remove Duplicates from Sorted Array | Easy · Slow and fast | NICE
# 042 — 26. Remove Duplicates from Sorted Array

**Problem 42 – Topic: Two Pointers | Difficulty: Easy | Pattern: Slow and fast**

[Open on LeetCode](https://leetcode.com/problems/remove-duplicates-from-sorted-array/)

## 1. Problem Statement
A sorted array contains repeats. Rewrite it in place so its first k positions hold each distinct value once, in order, and return k.

### Examples
```text
Input:  nums = [1,1,2]
Output: 2, nums = [1,2,_]

Input:  nums = [0,0,1,1,1,2,2,3,3,4]
Output: 5, nums = [0,1,2,3,4,_,_,_,_,_]
```

### Constraints
```text
1 <= nums.length <= 3 * 10^4
-100 <= nums[i] <= 100
nums is sorted in non-decreasing order.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An editor copying a class register
The register is alphabetical, so repeated names sit together. A reader calls out every name and the editor writes one only if it differs from the last name written.

| In the story | In the solution |
|---|---|
| editor's pen | `write` |
| last name written | `nums[write - 1]` |

### Pattern signal
**Slow and fast.** In sorted data a repeat can only equal the last kept value.

### Thought process
1. **First idea:** A set removes repeats but needs memory, and ignores that the input is sorted.
2. **The insight:** In sorted data a repeat can only equal the last kept value.

### Optimal approach
1. Keep the first value.
2. Read the rest.
3. Write a value differing from the last kept one.
4. Return the write position.

### Dry run
```text
nums = [1,1,2,2,3]
read  value  last kept  action   kept part
1     1      1          skip     [1]
2     2      1          write    [1,2]
3     2      2          skip     [1,2]
4     3      2          write    [1,2,3], k=3
```

### Why it works
The part before write holds each distinct value read so far once. A new value is always larger than the last, so order holds.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int removeDuplicates(int[] nums) {
        int write = 1;
        for (int read = 1; read < nums.length; read++) {
            if (nums[read] != nums[write - 1]) {
                nums[write] = nums[read];
                write++;
            }
        }
        return write;
    }
}
```

### Code walkthrough
- `write = 1`: The first name is always kept.
- `nums[write - 1]`: The last name in ink.
- `return write`: The pen's position equals the count.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Returning write - 1.** That is an index, not the count.
2. **Deleting by shifting.** It is quadratic.
3. **Using it on unsorted data.** Repeats are no longer adjacent.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[4]` | `1` |
| All equal | `[2,2,2]` | `1` |
| All different | `[1,2,3]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[044 — 80. Remove Duplicates from Sorted Array II]]: Compare two positions back. The key idea there: Sorted means two kept copies are adjacent, so one look two back detects a third.
- [[111 — 83. Remove Duplicates from Sorted List]]: Skip by relinking. The key idea there: Sorting puts all duplicates next to each other, so one comparison finds each.

### In a real interview
Say this: *"Sorting makes duplicates adjacent. A reader and a writer compact them in one pass."*

## 5. Recall
Q: 26. Remove Duplicates from Sorted Array: which pattern, and what is the insight?
A: Slow and fast. In sorted data a repeat can only equal the last kept value.

Q: 26. Remove Duplicates from Sorted Array: what is the analogy, and what does it stand for?
A: An editor copying a class register: editor's pen → `write`; last name written → `nums[write - 1]`.

Q: 26. Remove Duplicates from Sorted Array: What does write mean?
A: The next free slot, and so the number of values kept.

Q: 26. Remove Duplicates from Sorted Array: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Review 07 | Re-solve without looking | MUST
# Review 07

Solve on a blank page. No notes, hints or solutions.

## 26. Remove Duplicates from Sorted Array
Keep one copy of each value of a sorted array in place.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/remove-duplicates-from-sorted-array/)

## 344. Reverse String
Reverse a character array in place.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/reverse-string/)

## 179. Largest Number
Arrange numbers to form the largest concatenation.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/largest-number/)

## 1. Two Sum
Return indices of the two numbers that add up to target.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/two-sum/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 26 | | | | |
| 344 | | | | |
| 179 | | | | |
| 1 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[042 — 26. Remove Duplicates from Sorted Array]]
- [[039 — 344. Reverse String]]
- [[028 — 179. Largest Number]]
- [[004 — 1. Two Sum]]

@@ 2 | MODULE | Two Pointers — Medium | 8 problems | MUST
# Two Pointers: Medium

Patterns in this group: Dutch national flag, Slow and fast, Sorted pair, Sort and fix one, Sort and fix two, Move the limiting side, Triple reversal, Sort and pair ends.

@@ 3 | TOPIC | 043 — 75. Sort Colors | Medium · Dutch national flag | SHOULD
# 043 — 75. Sort Colors

**Problem 43 – Topic: Two Pointers | Difficulty: Medium | Pattern: Dutch national flag**

[Open on LeetCode](https://leetcode.com/problems/sort-colors/)

## 1. Problem Statement
`nums` contains only 0, 1 and 2, standing for red, white and blue. Sort it in place without a library sort, ideally in one pass with constant space.

### Examples
```text
Input:  nums = [2,0,2,1,1,0]
Output: [0,0,1,1,2,2]

Input:  nums = [2,0,1]
Output: [0,1,2]
```

### Constraints
```text
n == nums.length
1 <= n <= 300
nums[i] is either 0, 1, or 2.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Laundry and three baskets
Reds go in the left basket, blues in the right, whites stay in the middle. You pick from an unsorted pile between them. Something swapped in from the right is still unknown, so look at it before moving on.

| In the story | In the solution |
|---|---|
| edges of the baskets | `low` and `high` |
| item in your hand | `mid` |

### Pattern signal
**Dutch national flag.** With three values, placing the 0s and 2s at the ends also places the 1s.

### Thought process
1. **First idea:** Count the three values and rewrite, which takes two passes.
2. **The insight:** With three values, placing the 0s and 2s at the ends also places the 1s.

### Optimal approach
1. Keep low, mid and high.
2. Swap a zero to low and advance both.
3. Leave a one.
4. Swap a two to high and recheck.
5. Stop when mid passes high.

### Dry run
```text
nums = [2,0,2,1,1,0]
low  mid  high  value  array after
0    0    5     2      [0,0,2,1,1,2]
0    0    4     0      unchanged
1    1    4     0      unchanged
2    2    4     2      [0,0,1,1,2,2]
2    2    3     1      unchanged
2    3    3     1      unchanged, finished
```

### Why it works
Before low are 0s, low to mid are 1s, after high are 2s, and the rest is unknown. Each step shrinks the unknown part.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void sortColors(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;
        while (mid <= high) {
            if (nums[mid] == 0) swap(nums, low++, mid++);
            else if (nums[mid] == 1) mid++;
            else swap(nums, mid, high--); // recheck what arrived
        }
    }

    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
```

### Code walkthrough
- `mid <= high`: The item at high is still unknown.
- `low++, mid++`: What comes from low is already known.
- `high--`: Mid stays, because the arriving item is unknown.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Advancing mid after a swap with high.** A 0 can be stranded.
2. **Looping with `mid < high`.** The last item is skipped.
3. **Treating it as two groups.** The middle region is needed.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `[1]` |
| Reversed | `[2,1,0]` | `[0,1,2]` |
| Missing colour | `[2,0]` | `[0,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[041 — 283. Move Zeroes]]: Two regions with stable order. The key idea there: Only the non-zero order matters, so write those compactly and the zeros take care of themselves.
- [[176 — 215. Kth Largest Element in an Array]]: Quickselect uses this partition. The key idea there: You need one boundary, not the full order.

### In a real interview
Say this: *"I keep three known regions and shrink the unknown one. That is the Dutch national flag partition."*

## 5. Recall
Q: 75. Sort Colors: which pattern, and what is the insight?
A: Dutch national flag. With three values, placing the 0s and 2s at the ends also places the 1s.

Q: 75. Sort Colors: what is the analogy, and what does it stand for?
A: Laundry and three baskets: edges of the baskets → `low` and `high`; item in your hand → `mid`.

Q: 75. Sort Colors: Why does mid wait after swapping with high?
A: The value that arrived has not been examined.

Q: 75. Sort Colors: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 044 — 80. Remove Duplicates from Sorted Array II | Medium · Slow and fast | SHOULD
# 044 — 80. Remove Duplicates from Sorted Array II

**Problem 44 – Topic: Two Pointers | Difficulty: Medium | Pattern: Slow and fast**

[Open on LeetCode](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/)

## 1. Problem Statement
Rewrite a sorted array in place so each value appears at most twice, in order, and return the kept length k.

### Examples
```text
Input:  nums = [1,1,1,2,2,3]
Output: 5, nums = [1,1,2,2,3,_]

Input:  nums = [0,0,1,1,1,1,2,3,3]
Output: 7, nums = [0,0,1,1,2,3,3,_,_]
```

### Constraints
```text
1 <= nums.length <= 3 * 10^4
-10^4 <= nums[i] <= 10^4
nums is sorted in non-decreasing order.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A photographer allowing two per family
Families queue together. Before adding someone to the photo, look two places back in it. If that is the same family, they already have two, so this person waits out.

| In the story | In the solution |
|---|---|
| the photo | the part before `write` |
| two places back | `nums[write - 2]` |

### Pattern signal
**Slow and fast.** Sorted means two kept copies are adjacent, so one look two back detects a third.

### Thought process
1. **First idea:** Count each value and shift the extras out, in O(n²).
2. **The insight:** Sorted means two kept copies are adjacent, so one look two back detects a third.

### Optimal approach
1. Keep each of the first two.
2. For later values compare with the value two back in the kept part.
3. Write if different.
4. Return write.

### Dry run
```text
nums = [1,1,1,2,2,3]
value  write  two back  action
1      0      -         keep
1      1      -         keep
1      2      1         skip
2      2      1         keep
2      3      1         keep
3      4      2         keep, k=5
```

### Why it works
If the value equals the one two back, sorting means both last kept values equal it. Otherwise it has fewer than two copies kept.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int removeDuplicates(int[] nums) {
        int write = 0;
        for (int value : nums) {
            if (write < 2 || value != nums[write - 2]) {
                nums[write++] = value;
            }
        }
        return write;
    }
}
```

### Code walkthrough
- `write < 2`: Any two people fit.
- `nums[write - 2]`: Look back in the photo, not in the queue.
- `write++`: The photo grows by one.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Comparing `nums[read - 2]`.** It may have been overwritten.
2. **Comparing one back.** That allows only one copy.
3. **Counting without resetting.** A new value inherits the count.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `1` |
| Four copies | `[2,2,2,2]` | `2` |
| Pairs | `[1,1,2,2]` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[042 — 26. Remove Duplicates from Sorted Array]]: The same with one back. The key idea there: In sorted data a repeat can only equal the last kept value.
- [[084 — 1209. Remove All Adjacent Duplicates in String II]]: Whole runs vanish, so use a stack of counts.

### In a real interview
Say this: *"Because the input is sorted, checking two back in the output enforces the limit. It generalizes to any k."*

## 5. Recall
Q: 80. Remove Duplicates from Sorted Array II: which pattern, and what is the insight?
A: Slow and fast. Sorted means two kept copies are adjacent, so one look two back detects a third.

Q: 80. Remove Duplicates from Sorted Array II: what is the analogy, and what does it stand for?
A: A photographer allowing two per family: the photo → the part before `write`; two places back → `nums[write - 2]`.

Q: 80. Remove Duplicates from Sorted Array II: What detects a third copy?
A: It equals the kept value two positions back.

Q: 80. Remove Duplicates from Sorted Array II: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 045 — 167. Two Sum II - Input Array Is Sorted | Medium · Sorted pair | MUST
# 045 — 167. Two Sum II - Input Array Is Sorted

**Problem 45 – Topic: Two Pointers | Difficulty: Medium | Pattern: Sorted pair**

[Open on LeetCode](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/)

## 1. Problem Statement
`numbers` is sorted. Find the two different positions whose values sum to `target` and return them 1-indexed. One answer exists and extra space must be constant.

### Examples
```text
Input:  numbers = [2,7,11,15], target = 9
Output: [1,2]

Input:  numbers = [2,3,4], target = 6
Output: [1,3]

Input:  numbers = [-1,0], target = -1
Output: [1,2]
```

### Constraints
```text
2 <= numbers.length <= 3 * 10^4
-1000 <= numbers[i] <= 1000
numbers is sorted in non-decreasing order.
-1000 <= target <= 1000
The tests are generated such that there is exactly one solution.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Hot and cold taps
You want an exact temperature. Too cold? Only turning up the cold end, moving left rightward, can help. Too hot? Turn the hot end down. Each turn rules out a setting for good.

| In the story | In the solution |
|---|---|
| cold tap | the small left value |
| hot tap | the large right value |

### Pattern signal
**Sorted pair.** Sorted order tells which pointer is hopeless: a sum too small means left is too small with every remaining partner.

### Thought process
1. **First idea:** Try all pairs, or use a map and spend O(n) space.
2. **The insight:** Sorted order tells which pointer is hopeless: a sum too small means left is too small with every remaining partner.

### Optimal approach
1. Start at both ends.
2. Add.
3. Move left up if too small.
4. Move right down if too large.
5. Return both plus one on a match.

### Dry run
```text
numbers = [2,7,11,15], target = 9
left  right  sum  action
0     3      17   too large
0     2      13   too large
0     1      9    return [1,2]
```

### Why it works
A discarded left was too small even with the largest remaining partner, and symmetrically for right. The answer is never discarded.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) return new int[] {left + 1, right + 1};
            if (sum < target) left++;
            else right--;
        }
        return new int[0]; // not reached
    }
}
```

### Code walkthrough
- `sum < target`: Too cold, raise the small side.
- `right--`: Too hot, lower the large side.
- `left + 1`: Positions start at 1 here.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Returning 0-based indices.** Add one.
2. **Using `<=` in the loop.** An element pairs with itself.
3. **Using this on unsorted data.** The proof needs order.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two values | `[-1,0], -1` | `[1,2]` |
| Equal | `[3,3], 6` | `[1,2]` |
| At the end | `[1,2,4,5], 9` | `[3,4]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[004 — 1. Two Sum]]: Unsorted, so use a map. The key idea there: The partner is not a mystery: it is exactly target minus x. A map finds it at once.
- [[046 — 15. 3Sum]]: A loop around this. The key idea there: Fixing one value turns the rest into sorted Two Sum, and sorting puts duplicates side by side.

### In a real interview
Say this: *"Sorted order lets each comparison eliminate one element with all its pairs. That is linear with no memory."*

## 5. Recall
Q: 167. Two Sum II - Input Array Is Sorted: which pattern, and what is the insight?
A: Sorted pair. Sorted order tells which pointer is hopeless: a sum too small means left is too small with every remaining partner.

Q: 167. Two Sum II - Input Array Is Sorted: what is the analogy, and what does it stand for?
A: Hot and cold taps: cold tap → the small left value; hot tap → the large right value.

Q: 167. Two Sum II - Input Array Is Sorted: Why can left be dropped when the sum is small?
A: It is too small even with the largest remaining partner.

Q: 167. Two Sum II - Input Array Is Sorted: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 046 — 15. 3Sum | Medium · Sort and fix one | MUST
# 046 — 15. 3Sum

**Problem 46 – Topic: Two Pointers | Difficulty: Medium | Pattern: Sort and fix one**

[Open on LeetCode](https://leetcode.com/problems/3sum/)

## 1. Problem Statement
Return every unique triple of values, taken from three different positions of `nums`, that sums to 0. Order does not matter.

### Examples
```text
Input:  nums = [-1,0,1,2,-1,-4]
Output: [[-1,-1,2],[-1,0,1]]

Input:  nums = [0,1,1]
Output: []

Input:  nums = [0,0,0]
Output: [[0,0,0]]
```

### Constraints
```text
3 <= nums.length <= 3000
-10^5 <= nums[i] <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A table for three
Seat one guest, then send the two-detective search down the rest of the sorted street for the pair that balances them. Never seat a lookalike in the same chair twice, or you print the same party again.

| In the story | In the solution |
|---|---|
| seated guest | index i |
| lookalike | an equal neighbor |

### Pattern signal
**Sort and fix one.** Fixing one value turns the rest into sorted Two Sum, and sorting puts duplicates side by side.

### Thought process
1. **First idea:** Try all triples in O(n³), then deduplicate with a set.
2. **The insight:** Fixing one value turns the rest into sorted Two Sum, and sorting puts duplicates side by side.

### Optimal approach
1. Sort.
2. Fix each distinct first value.
3. Close two pointers on the rest.
4. Record a match.
5. Skip equal left values.

### Dry run
```text
sorted = [-4,-1,-1,0,1,2]
fixed  left,right values  sum  action
-4     -1,2 and onward    <0   no match
-1     -1,2               0    record
-1     0,1                0    record
-1     duplicate first         skip
0      1,2                3    no match
```

### Why it works
For each first value the pointers eliminate only impossible pairs. Skipping an equal value in the same chair loses no new triple.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum < 0) left++;
                else if (sum > 0) right--;
                else {
                    result.add(List.of(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) left++;
                }
            }
        }
        return result;
    }
}
```

### Code walkthrough
- `Arrays.sort`: Enables both the search and the skipping.
- `nums[i] == nums[i - 1]`: Not the same guest in the first chair again.
- `left = i + 1`: Only look to the right, so positions differ.
- `nums[left] == nums[left - 1]`: Not the same second guest again.

### Complexity
- **Time:** O(n²)
- **Space:** O(log n) sort stack, besides the result

### Common mistakes
1. **Skipping `nums[i] == nums[i + 1]`.** It loses [-1,-1,2].
2. **Skipping only the first chair.** Duplicates still appear.
3. **Stopping at the first match.** One guest can have many pairs.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zeros | `[0,0,0,0]` | `one triple 0,0,0` |
| None | `[1,2,3]` | `[]` |
| Repeats | `[-2,1,1,1]` | `one triple -2,1,1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[047 — 18. 4Sum]]: Seat two guests first. The key idea there: Any k-sum becomes a pair search after fixing k-2 values.
- **16. 3Sum Closest**: Track the nearest sum.

### In a real interview
Say this: *"I sort, fix one and solve sorted Two Sum. Duplicate skipping happens at each level."*

## 5. Recall
Q: 15. 3Sum: which pattern, and what is the insight?
A: Sort and fix one. Fixing one value turns the rest into sorted Two Sum, and sorting puts duplicates side by side.

Q: 15. 3Sum: what is the analogy, and what does it stand for?
A: A table for three: seated guest → index i; lookalike → an equal neighbor.

Q: 15. 3Sum: How are duplicate triples prevented?
A: Skip equal first values, and equal left values after a match.

Q: 15. 3Sum: what are the time and space costs?
A: Time O(n²). Space O(log n) sort stack, besides the result.

@@ 3 | TOPIC | 047 — 18. 4Sum | Medium · Sort and fix two | SHOULD
# 047 — 18. 4Sum

**Problem 47 – Topic: Two Pointers | Difficulty: Medium | Pattern: Sort and fix two**

[Open on LeetCode](https://leetcode.com/problems/4sum/)

## 1. Problem Statement
Return every unique quadruple of values from four different positions whose sum is `target`.

### Examples
```text
Input:  nums = [1,0,-1,0,-2,2], target = 0
Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]

Input:  nums = [2,2,2,2,2], target = 8
Output: [[2,2,2,2]]
```

### Constraints
```text
1 <= nums.length <= 200
-10^9 <= nums[i] <= 10^9
-10^9 <= target <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A table for four
The host seats two guests with nested loops and lets the detectives find the other pair. Each chair refuses a lookalike of the guest who just vacated it.

| In the story | In the solution |
|---|---|
| seated guests | loops a and b |
| detectives | `left` and `right` |

### Pattern signal
**Sort and fix two.** Any k-sum becomes a pair search after fixing k-2 values.

### Thought process
1. **First idea:** Four loops, in O(n⁴).
2. **The insight:** Any k-sum becomes a pair search after fixing k-2 values.

### Optimal approach
1. Sort.
2. Fix a distinct first.
3. Fix a distinct second.
4. Close two pointers using a long sum.
5. Skip repeats after matches.

### Dry run
```text
sorted = [-2,-1,0,0,1,2], target = 0
fixed   pair found  quadruple
-2,-1   1,2         -2,-1,1,2
-2,0    0,2         -2,0,0,2
-1,0    0,1         -1,0,0,1
```

### Why it works
Every sorted quadruple has one distinct pair of first values, and the two-pointer step finds all endings of it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();
        for (int a = 0; a < n - 3; a++) {
            if (a > 0 && nums[a] == nums[a - 1]) continue;
            for (int b = a + 1; b < n - 2; b++) {
                if (b > a + 1 && nums[b] == nums[b - 1]) continue;
                int left = b + 1, right = n - 1;
                while (left < right) {
                    long sum = (long) nums[a] + nums[b] + nums[left] + nums[right];
                    if (sum < target) left++;
                    else if (sum > target) right--;
                    else {
                        result.add(List.of(nums[a], nums[b], nums[left], nums[right]));
                        left++;
                        right--;
                        while (left < right && nums[left] == nums[left - 1]) left++;
                    }
                }
            }
        }
        return result;
    }
}
```

### Code walkthrough
- `b > a + 1`: The second guest may equal the first, just not the previous second.
- `(long)`: Four values near a billion overflow an int.
- `left = b + 1`: The detectives start after both seats.

### Complexity
- **Time:** O(n³)
- **Space:** O(log n) sort stack, besides the result

### Common mistakes
1. **Summing as int.** It overflows.
2. **Using `b > 0`.** Valid equal pairs are lost.
3. **Breaking when a exceeds target.** Negatives can follow a negative target.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Too few | `[1,2,3], 6` | `[]` |
| All equal | `[2,2,2,2,2], 8` | `one quadruple of 2s` |
| Overflow | `four times 1000000000, -294967296` | `[]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[046 — 15. 3Sum]]: One loop fewer. The key idea there: Fixing one value turns the rest into sorted Two Sum, and sorting puts duplicates side by side.
- **454. 4Sum II**: Separate arrays, so store pair sums in a map.

### In a real interview
Say this: *"K-sum is sorting plus k minus two loops plus two pointers. I use long for the sum."*

## 5. Recall
Q: 18. 4Sum: which pattern, and what is the insight?
A: Sort and fix two. Any k-sum becomes a pair search after fixing k-2 values.

Q: 18. 4Sum: what is the analogy, and what does it stand for?
A: A table for four: seated guests → loops a and b; detectives → `left` and `right`.

Q: 18. 4Sum: Why use long for the sum?
A: Four values near a billion exceed the int range.

Q: 18. 4Sum: what are the time and space costs?
A: Time O(n³). Space O(log n) sort stack, besides the result.

@@ 3 | TOPIC | 048 — 11. Container With Most Water | Medium · Move the limiting side | MUST
# 048 — 11. Container With Most Water

**Problem 48 – Topic: Two Pointers | Difficulty: Medium | Pattern: Move the limiting side**

[Open on LeetCode](https://leetcode.com/problems/container-with-most-water/)

## 1. Problem Statement
`height[i]` is a vertical line at x = i. Choose two lines that, with the x-axis, hold the most water, and return that amount. The container cannot tilt.

### Examples
```text
Input:  height = [1,8,6,2,5,4,8,3,7]
Output: 49

Input:  height = [1,1]
Output: 1
```

### Constraints
```text
n == height.length
2 <= n <= 10^5
0 <= height[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two friends holding a banner
The banner can only be as high as the shorter friend. Walking closer loses width, so it is worth it only if the shorter friend is replaced. Replacing the taller one can never raise the banner.

| In the story | In the solution |
|---|---|
| shorter friend | the smaller height |
| distance between friends | the width |

### Pattern signal
**Move the limiting side.** Area is width times the shorter line. The shorter line already has its best possible partner width, so it can be retired.

### Thought process
1. **First idea:** Try every pair, in O(n²).
2. **The insight:** Area is width times the shorter line. The shorter line already has its best possible partner width, so it can be retired.

### Optimal approach
1. Start at both ends.
2. Record the area.
3. Move the shorter side inward.
4. Repeat until they meet.

### Dry run
```text
height = [1,8,6,2,5,4,8,3,7]
left  right  heights  area  best
0     8      1,7      8     8
1     8      8,7      49    49
1     7      8,3      18    49
1     6      8,8      40    49
remaining pairs are narrower and reach at most 16
```

### Why it works
Every unchecked pair using the retired line is narrower and no taller, so none can beat the area just recorded.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1, best = 0;
        while (left < right) {
            int area = (right - left) * Math.min(height[left], height[right]);
            best = Math.max(best, area);
            if (height[left] < height[right]) left++;
            else right--;
        }
        return best;
    }
}
```

### Code walkthrough
- `Math.min`: The shorter friend sets the height.
- `right - left`: Distance, with no plus one.
- `height[left] < height[right]`: Retire the shorter. On a tie either is safe.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Moving the taller.** The winner can be skipped.
2. **Adding one to the width.** Lines have no thickness.
3. **Subtracting lines between.** They don't displace water here.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two | `[1,1]` | `1` |
| Zero | `[0,5]` | `0` |
| Rising | `[1,2,3]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[051 — 42. Trapping Rain Water]]: Every bar holds its own water.
- [[087 — 84. Largest Rectangle in Histogram]]: Bars between do limit, so use a stack.

### In a real interview
Say this: *"The shorter line can never do better with a narrower width, so I discard it. That gives one pass."*

## 5. Recall
Q: 11. Container With Most Water: which pattern, and what is the insight?
A: Move the limiting side. Area is width times the shorter line. The shorter line already has its best possible partner width, so it can be retired.

Q: 11. Container With Most Water: what is the analogy, and what does it stand for?
A: Two friends holding a banner: shorter friend → the smaller height; distance between friends → the width.

Q: 11. Container With Most Water: Why move the shorter line?
A: All its remaining pairs are narrower and no taller.

Q: 11. Container With Most Water: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Review 08 | Re-solve without looking | MUST
# Review 08

Solve on a blank page. No notes, hints or solutions.

## 11. Container With Most Water
Choose two lines holding the most water.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/container-with-most-water/)

## 167. Two Sum II - Input Array Is Sorted
Find two numbers with a target sum in a sorted array using O(1) space.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/)

## 68. Text Justification
Format words into fully justified lines of a given width.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/text-justification/)

## 303. Range Sum Query - Immutable
Answer many range-sum queries on a fixed array.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/range-sum-query-immutable/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 11 | | | | |
| 167 | | | | |
| 68 | | | | |
| 303 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[048 — 11. Container With Most Water]]
- [[045 — 167. Two Sum II - Input Array Is Sorted]]
- [[034 — 68. Text Justification]]
- [[010 — 303. Range Sum Query - Immutable]]

@@ 3 | TOPIC | 049 — 189. Rotate Array | Medium · Triple reversal | SHOULD
# 049 — 189. Rotate Array

**Problem 49 – Topic: Two Pointers | Difficulty: Medium | Pattern: Triple reversal**

[Open on LeetCode](https://leetcode.com/problems/rotate-array/)

## 1. Problem Statement
Rotate `nums` to the right by k steps, where k can exceed the length. Aim for O(1) extra space.

### Examples
```text
Input:  nums = [1,2,3,4,5,6,7], k = 3
Output: [5,6,7,1,2,3,4]

Input:  nums = [-1,-100,3,99], k = 2
Output: [3,99,-1,-100]
```

### Constraints
```text
1 <= nums.length <= 10^5
-2^31 <= nums[i] <= 2^31 - 1
0 <= k <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Flipping two hands of cards
Cards A then B must become B then A. Flip the whole row, and the groups have swapped but each is backwards. Flip each group on its own and it is right again.

| In the story | In the solution |
|---|---|
| group B | the last k values |
| a flip | an in-place reversal |

### Pattern signal
**Triple reversal.** A rotation is just two blocks trading places, which three reversals achieve.

### Thought process
1. **First idea:** Rotate one step k times in O(n·k), or use a second array.
2. **The insight:** A rotation is just two blocks trading places, which three reversals achieve.

### Optimal approach
1. Reduce k modulo n.
2. Reverse everything.
3. Reverse the first k.
4. Reverse the rest.

### Dry run
```text
nums = [1,2,3,4,5], k = 2
reverse all      [5,4,3,2,1]
reverse first 2  [4,5,3,2,1]
reverse rest     [4,5,1,2,3]
```

### Why it works
Reversing AB gives reversed B then reversed A. Reversing each part again restores its internal order, leaving BA.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k %= n; // full turns change nothing
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    private void reverse(int[] a, int left, int right) {
        while (left < right) {
            int temp = a[left];
            a[left++] = a[right];
            a[right--] = temp;
        }
    }
}
```

### Code walkthrough
- `k %= n`: Seven steps on seven seats is standing still.
- `reverse(nums, 0, n - 1)`: The groups trade places.
- `reverse(nums, k, n - 1)`: The second group gets its order back.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Forgetting the modulo.** The index runs out of range.
2. **Assigning a new array to nums.** The caller never sees it.
3. **Splitting at n - k after the first flip.** The split is now at k.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| No move | `[1,2], 0` | `[1,2]` |
| Large k | `[1,2], 3` | `[2,1]` |
| One | `[1], 9` | `[1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[039 — 344. Reverse String]]: The building block. The key idea there: Reversal sends i to n-1-i and back, so the moves are independent swaps.
- [[126 — 61. Rotate List]]: Close a ring and cut. The key idea there: Nodes keep their circular order, so only the cut position changes.

### In a real interview
Say this: *"A rotation swaps two blocks. Three reversals do it in linear time and constant space."*

## 5. Recall
Q: 189. Rotate Array: which pattern, and what is the insight?
A: Triple reversal. A rotation is just two blocks trading places, which three reversals achieve.

Q: 189. Rotate Array: what is the analogy, and what does it stand for?
A: Flipping two hands of cards: group B → the last k values; a flip → an in-place reversal.

Q: 189. Rotate Array: What three reversals rotate right by k?
A: All, then the first k, then the rest.

Q: 189. Rotate Array: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 050 — 881. Boats to Save People | Medium · Sort and pair ends | SHOULD
# 050 — 881. Boats to Save People

**Problem 50 – Topic: Two Pointers | Difficulty: Medium | Pattern: Sort and pair ends**

[Open on LeetCode](https://leetcode.com/problems/boats-to-save-people/)

## 1. Problem Statement
Each boat carries at most two people with total weight up to `limit`. Return the fewest boats for everyone.

### Examples
```text
Input:  people = [1,2], limit = 3
Output: 1

Input:  people = [3,2,2,1], limit = 3
Output: 3

Input:  people = [3,5,3,4], limit = 5
Output: 4
```

### Constraints
```text
1 <= people.length <= 5 * 10^4
1 <= people[i] <= limit <= 3 * 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The ferry's seating clerk
The heaviest passenger must sail anyway. Ask the lightest to join. If even they don't fit, nobody does, so the heavy one sails alone.

| In the story | In the solution |
|---|---|
| heaviest waiting | `right` |
| lightest waiting | `left` |

### Pattern signal
**Sort and pair ends.** The lightest is the best possible companion for the heaviest, and giving that seat away never hurts.

### Thought process
1. **First idea:** Try all pairings, which is exponential.
2. **The insight:** The lightest is the best possible companion for the heaviest, and giving that seat away never hurts.

### Optimal approach
1. Sort.
2. Load the heaviest.
3. Add the lightest if they fit.
4. Count a boat.
5. Repeat.

### Dry run
```text
sorted = [1,2,2,3], limit = 3
lightest  heaviest  fit?  boats
1         3         no    1
1         2         yes   2
2         2         alone 3
```

### Why it works
If the heaviest fits with no one, it is alone in every plan. If it fits with the lightest, any optimal plan can swap partners to this pairing without harm.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numRescueBoats(int[] people, int limit) {
        Arrays.sort(people);
        int left = 0, right = people.length - 1, boats = 0;
        while (left <= right) {
            if (people[left] + people[right] <= limit) left++;
            right--; // the heaviest always sails
            boats++;
        }
        return boats;
    }
}
```

### Code walkthrough
- `Arrays.sort`: Puts both extremes at the ends.
- `left <= right`: One last person still needs a boat.
- `right--`: With or without company.

### Complexity
- **Time:** O(n log n) for the sort
- **Space:** O(log n) sort stack

### Common mistakes
1. **Dividing total weight by the limit.** The two-person rule is ignored.
2. **Using `<` in the loop.** The last person is left behind.
3. **Seating three.** Two is the maximum.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[3], 3` | `1` |
| All heavy | `[3,3], 3` | `2` |
| Two-person cap | `[1,1,1], 5` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[045 — 167. Two Sum II - Input Array Is Sorted]]: The same end comparisons. The key idea there: Sorted order tells which pointer is hopeless: a sum too small means left is too small with every remaining partner.
- [[286 — 1029. Two City Scheduling]]: Another greedy after sorting. The key idea there: The total is a constant plus the sum of a minus b over those sent to A, so only that difference matters.

### In a real interview
Say this: *"The heaviest pairs with the lightest or with nobody. An exchange argument shows that is optimal."*

## 5. Recall
Q: 881. Boats to Save People: which pattern, and what is the insight?
A: Sort and pair ends. The lightest is the best possible companion for the heaviest, and giving that seat away never hurts.

Q: 881. Boats to Save People: what is the analogy, and what does it stand for?
A: The ferry's seating clerk: heaviest waiting → `right`; lightest waiting → `left`.

Q: 881. Boats to Save People: Who is tried as the heaviest person's partner?
A: Only the lightest remaining person.

Q: 881. Boats to Save People: what are the time and space costs?
A: Time O(n log n) for the sort. Space O(log n) sort stack.

@@ 2 | MODULE | Two Pointers — Hard | 1 problems | MUST
# Two Pointers: Hard

Patterns in this group: Left and right maxima.

@@ 3 | TOPIC | 051 — 42. Trapping Rain Water | Hard · Left and right maxima | MUST
# 051 — 42. Trapping Rain Water

**Problem 51 – Topic: Two Pointers | Difficulty: Hard | Pattern: Left and right maxima**

[Open on LeetCode](https://leetcode.com/problems/trapping-rain-water/)

## 1. Problem Statement
`height` gives the heights of bars of width 1. Return how much rain water is trapped between them.

### Examples
```text
Input:  height = [0,1,0,2,1,0,1,3,2,1,2,1]
Output: 6

Input:  height = [4,2,0,3,2,5]
Output: 9
```

### Constraints
```text
n == height.length
1 <= n <= 2 * 10^4
0 <= height[i] <= 10^5
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Water in a street of walls
Above any spot water rises until it spills over the lower of the tallest wall on each side. Two inspectors walk inward. The one at the lower wall can settle their spot at once, knowing a wall at least as tall exists on the other side.

| In the story | In the solution |
|---|---|
| tallest wall behind an inspector | `leftMax` and `rightMax` |
| spill level | the smaller maximum |

### Pattern signal
**Left and right maxima.** You don't need both exact maxima, only the limiting one, and the lower side reveals it.

### Thought process
1. **First idea:** Scan both sides for every bar in O(n²), or precompute two arrays in O(n) space.
2. **The insight:** You don't need both exact maxima, only the limiting one, and the lower side reveals it.

### Optimal approach
1. Start at both ends.
2. Pick the lower bar.
3. Update that side's maximum.
4. Add the maximum minus the bar.
5. Move that side.

### Dry run
```text
height = [4,2,0,3,2,5]
left  right  bar  leftMax  added  total
0     5      4    4        0      0
1     5      2    4        2      2
2     5      0    4        4      6
3     5      3    4        1      7
4     5      2    4        2      9
```

### Why it works
When the left bar is the lower one, some right bar is at least as tall as leftMax, so leftMax alone fixes the level. The right case mirrors it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0, water = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                leftMax = Math.max(leftMax, height[left]);
                water += leftMax - height[left];
                left++;
            } else {
                rightMax = Math.max(rightMax, height[right]);
                water += rightMax - height[right];
                right--;
            }
        }
        return water;
    }
}
```

### Code walkthrough
- `height[left] < height[right]`: The left spill level is now certain.
- `leftMax - height[left]`: Water standing on this bar, never negative.
- `left < right`: The meeting bar is the tallest and holds none.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Using immediate neighbors.** The bounding wall can be far away.
2. **Updating the maximum after adding.** Water becomes negative.
3. **Confusing it with problem 11.** Here every bar takes up space.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[3]` | `0` |
| Slope | `[1,2,3]` | `0` |
| Bowl | `[3,0,3]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[048 — 11. Container With Most Water]]: One pair only. The key idea there: Area is width times the shorter line. The shorter line already has its best possible partner width, so it can be retired.
- **407. Trapping Rain Water II**: The boundary is a heap.

### In a real interview
Say this: *"Each bar depends on the lower of two maxima. Processing the lower end makes that maximum known without arrays."*

## 5. Recall
Q: 42. Trapping Rain Water: which pattern, and what is the insight?
A: Left and right maxima. You don't need both exact maxima, only the limiting one, and the lower side reveals it.

Q: 42. Trapping Rain Water: what is the analogy, and what does it stand for?
A: Water in a street of walls: tallest wall behind an inspector → `leftMax` and `rightMax`; spill level → the smaller maximum.

Q: 42. Trapping Rain Water: When can the left bar's water be settled?
A: When it is lower than the right bar, because leftMax is then the limit.

Q: 42. Trapping Rain Water: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Pattern summary: Two Pointers | Key patterns of the topic | MUST
# Pattern summary: Two Pointers

Each pointer moves only one way, so there are at most 2n moves. Before moving one, prove that every pair you discard cannot be the answer.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Same-direction pointers | Decide whether s is a subsequence of t. (392) | Requiring contiguous characters |
| Opposite pointers | Check a palindrome ignoring case and non-alphanumeric characters. (125, 344) | Skipping past the other pointer |
| Pointers with one skip | Check whether deleting at most one character gives a palindrome. (680) | Trying only one of the two skips |
| Parallel pointers | Interleave two strings, appending the leftover. (1768) | Stopping at the shorter string |
| Fill from the back | Merge two sorted arrays into the first one's spare space. (88) | Filling from the front overwrites values |
| Slow and fast | Move zeros to the end keeping the order of other values. (283, 26, 80) | Changing the relative order |
| Dutch national flag | Sort an array of 0, 1 and 2 in one pass. (75) | Advancing mid after swapping with high |
| Sorted pair | Find two numbers with a target sum in a sorted array using O(1) space. (167) | The answer is 1-indexed |
| Sort and fix one | List unique triplets that sum to zero. (15) | Skipping duplicates at only one position |
| Sort and fix two | List unique quadruplets with a target sum. (18) | Int overflow in the sum |
| Move the limiting side | Choose two lines holding the most water. (11) | Moving the taller side cannot improve the height |
| Triple reversal | Rotate an array right by k in place. (189) | Forgetting that k can exceed n |
| Sort and pair ends | Use the fewest boats with two people and a weight limit each. (881) | Putting three people in one boat |
| Left and right maxima | Compute rain water trapped between bars. (42) | Taking only the immediate neighbors |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| palindrome | reading a banner from both ends | opposite pointers | 125, 680 |
| appears in order | ticking off a shopping list in one walk | same direction | 392 |
| sorted, find a pair | hot and cold taps | sorted pair | 167 |
| triples, quadruples | book some seats, then solve for a pair | sort and fix | 15, 18 |
| keep some, in place | a reader and an editor | slow and fast | 283, 26, 80 |
| merge into spare space | fill the shelf from the far end | back to front | 88 |
| best width and height | the shorter friend limits the banner | move the limit | 11, 42 |
| rotate in place | flip the whole, then flip the parts | reversal | 189 |

## Which pattern do I pick?

1. **Rewriting an array in place?** Use a slow writer and a fast reader. If writing would destroy unread data, work from the back.
2. **Sorted, or allowed to sort, and looking for a pair?** Start at both ends.
3. **More than two values?** Fix all but two with loops.
4. **Before moving a pointer, ask:** "can any pair I am throwing away still win?" If you can't answer no, this is not a two-pointer problem.

Q: Which signals point to Two Pointers?
A: sorted input; palindrome; pair; rewrite in place.

Q: What is the core idea of Two Pointers?
A: Each pointer moves only one way, so there are at most 2n moves. Before moving one, prove that every pair you discard cannot be the answer.
