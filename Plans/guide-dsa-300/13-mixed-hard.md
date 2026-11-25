@@ 1 | MODULE | Phase 13 — Mixed / Hard and Contest-style | no topic given; greedy; intervals; bits; math; design | MUST
# Phase 13: Mixed / Hard and Contest-style

Nobody tells you the pattern here. Read the constraints, state a brute force, find the repeated work and choose the structure that removes it. For greedy, prove that the choice is safe.

**Signals:** no topic given; greedy; intervals; bits; math; design.

## The big analogy: a doctor with no referral note

Until now every problem arrived labelled "stack" or "graph". Here the patient just describes symptoms, as in an interview. **Read the symptoms (constraints and key words), name a brute force, find the repeated work, then prescribe.** Greedy is a medicine that needs proof: show that the early choice can never hurt.

| Symptom | Think of | Prescription | Meet it in |
|---|---|---|---|
| overlapping ranges | calendar bookings | sort intervals | 56, 57, 435 |
| best as you go | a hiker dropping dead weight | greedy | 53, 55, 134 |
| appears twice, powers of two | light switches | bits | 136, 338, 371 |
| digits | working on paper | carry and modulo | 66, 43, 7 |
| matrix movement | peeling an onion | layers | 48, 54 |
| O(1) design | an index beside an ordered shelf | map and list | 146, 460 |
| offline questions | sorting the visitors first | sweep and heap | 1851 |

## Template
```java
Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
List<int[]> merged = new ArrayList<>();
for (int[] x : intervals) {
    if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < x[0]) merged.add(x.clone());
    else merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], x[1]);
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Mixed / Hard and Contest-style — Easy | 12 problems | MUST
# Mixed / Hard and Contest-style: Easy

Patterns in this group: Base 26 without zero, GCD, Cycle detection, Carry, Digit reversal, Factor removal, XOR cancellation, Bit count, Bit DP, Bit reversal, Expected sum.

@@ 3 | TOPIC | 264 — 168. Excel Sheet Column Title | Easy · Base 26 without zero | NICE
# 264 — 168. Excel Sheet Column Title

**Problem 264 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Base 26 without zero**

[Open on LeetCode](https://leetcode.com/problems/excel-sheet-column-title/)

## 1. Problem Statement
You get a positive integer. Return its Excel column title, where 1 is A, 26 is Z, 27 is AA and 28 is AB.

### Examples
```text
Input:  columnNumber = 1
Output: "A"

Input:  columnNumber = 28
Output: "AB"

Input:  columnNumber = 701
Output: "ZY"
```

### Constraints
```text
1 <= columnNumber <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An odometer whose wheels have no zero
Imagine an odometer whose wheels show A to Z and have no zero position. Normal base conversion expects a zero, so before reading each wheel you step the number back by one. That makes A behave like 0.

| In the story | In the solution |
|---|---|
| step back | n-- |
| wheel | remainder |

### Pattern signal
**Base 26 without zero.** This is base 26 with digits 1 to 26. Subtracting one at each place turns it into ordinary base 26.

### Thought process
1. **First idea:** Counting from A up to the number takes linear time.
2. **The insight:** This is base 26 with digits 1 to 26. Subtracting one at each place turns it into ordinary base 26.

### Optimal approach
1. While the number is positive, subtract one.
2. Take the remainder and turn it into a letter.
3. Divide by 26.
4. Reverse the collected letters.

### Dry run
```text
columnNumber = 28
before  after decrement  letter = n % 26  next n = n / 26
28      27               1, B             1
1       0                0, A             0
collected BA, reversed AB
```

### Why it works
After the decrement the remainder is exactly the zero-based letter of the last place and the quotient is exactly the number formed by the places before it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String convertToTitle(int columnNumber) {
        StringBuilder title = new StringBuilder();
        while (columnNumber > 0) {
            columnNumber--;
            title.append((char) ('A' + columnNumber % 26));
            columnNumber /= 26;
        }
        return title.reverse().toString();
    }
}
```

### Code walkthrough
- `columnNumber--`: Shift this place from 1..26 to 0..25.
- `% 26`: The remainder picks the letter of the current last wheel.
- `reverse()`: Wheels were read right to left, so flip the text.

### Complexity
- **Time:** O(log n)
- **Space:** O(log n) for the output

### Common mistakes
1. **Without the decrement Z breaks.** Fix: subtract before each modulo.
2. **Letters arrive last first.** Fix: reverse them.
3. **One decrement is not enough.** Fix: repeat it for every place.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `"A"` |
| Z | `26` | `"Z"` |
| AA | `27` | `"AA"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **171. Reverse**: Multiply and add.
- [[294 — 12. Integer to Roman]]: Symbols. The key idea there: Treating subtractive pairs as symbols of their own makes plain greedy produce the canonical numeral.

### In a real interview
Say it in this order:

1. **Brute force:** Counting from A up to the number takes linear time.
2. **Insight:** This is base 26 with digits 1 to 26. Subtracting one at each place turns it into ordinary base 26.
3. **Approach in a few words:** Bijective base 26.
4. **Cost:** time O(log n); space O(log n) for the output.

## 5. Recall
Q: 168. Excel Sheet Column Title: which pattern, and what is the insight?
A: Base 26 without zero. This is base 26 with digits 1 to 26. Subtracting one at each place turns it into ordinary base 26.

Q: 168. Excel Sheet Column Title: what is the analogy, and what does it stand for?
A: An odometer whose wheels have no zero: step back → n--; wheel → remainder.

Q: 168. Excel Sheet Column Title: Why decrement before modulo?
A: There is no zero digit.

Q: 168. Excel Sheet Column Title: what are the time and space costs?
A: Time O(log n). Space O(log n) for the output.

@@ 3 | TOPIC | Practice — Review 44 | Re-solve without looking | MUST
# Review 44

Solve on a blank page. No notes, hints or solutions.

## 168. Excel Sheet Column Title
Convert a number to an Excel column title.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/excel-sheet-column-title/)

## 1220. Count Vowels Permutation
Count strings under vowel-following rules.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/count-vowels-permutation/)

## 1143. Longest Common Subsequence
Find the longest common subsequence.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/longest-common-subsequence/)

## 127. Word Ladder
Find the shortest one-letter transformation chain.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/word-ladder/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 168 | | | | |
| 1220 | | | | |
| 1143 | | | | |
| 127 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[264 — 168. Excel Sheet Column Title]]
- [[261 — 1220. Count Vowels Permutation]]
- [[250 — 1143. Longest Common Subsequence]]
- [[226 — 127. Word Ladder]]

@@ 3 | TOPIC | 265 — 1071. Greatest Common Divisor of Strings | Easy · GCD | NICE
# 265 — 1071. Greatest Common Divisor of Strings

**Problem 265 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: GCD**

[Open on LeetCode](https://leetcode.com/problems/greatest-common-divisor-of-strings/)

## 1. Problem Statement
A string t divides s if s is t repeated one or more times. Return the longest string that divides both str1 and str2, or an empty string.

### Examples
```text
Input:  str1 = "ABCABC", str2 = "ABC"
Output: "ABC"

Input:  str1 = "ABABAB", str2 = "ABAB"
Output: "AB"

Input:  str1 = "LEET", str2 = "CODE"
Output: ""

Input:  str1 = "AAAAAB", str2 = "AAA"
Output: ""​​​​​​​
```

### Constraints
```text
1 <= str1.length, str2.length <= 1000
str1 and str2 consist of English uppercase letters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The largest tile for two floors
Two floors are tiled with the same repeating tile. If the patterns really match, laying floor A then B looks like B then A. The biggest tile that fits both is as long as the greatest length dividing both floors.

| In the story | In the solution |
|---|---|
| same pattern | equal concatenations |
| largest tile | gcd |

### Pattern signal
**GCD.** Two strings are built from one unit exactly when ab equals ba, and the longest unit's length is the gcd of their lengths.

### Thought process
1. **First idea:** Test every prefix as a candidate tile against both strings.
2. **The insight:** Two strings are built from one unit exactly when ab equals ba, and the longest unit's length is the gcd of their lengths.

### Optimal approach
1. Compare both concatenations.
2. If they differ return empty.
3. Find the gcd of the lengths with Euclid.
4. Return that prefix.

### Dry run
```text
str1 = ABABAB, str2 = ABAB
check                 result
str1+str2, str2+str1  both ABABABABAB, a tile exists
6 % 4                 2
4 % 2                 0, so the gcd is 2
prefix of length 2    AB
```

### Why it works
Equal concatenations prove that both strings repeat a common unit. A common divisor's length must divide both lengths, and the gcd is the longest such length.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String gcdOfStrings(String str1, String str2) {
        if (!(str1 + str2).equals(str2 + str1)) return "";
        int a = str1.length(), b = str2.length();
        while (b != 0) {
            int rest = a % b;
            a = b;
            b = rest;
        }
        return str1.substring(0, a);
    }
}
```

### Code walkthrough
- `str1 + str2`: Proves that a common tile exists.
- `a % b`: One step of Euclid's algorithm.
- `substring(0, a)`: The prefix of gcd length is the tile itself.

### Complexity
- **Time:** O(m + n)
- **Space:** O(m + n)

### Common mistakes
1. **A common prefix need not repeat.** Fix: compare both concatenations.
2. **Skipping that check returns garbage.** Fix: test before the gcd.
3. **The shorter string may not divide.** Fix: use the gcd length.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `AB, CD` | `""` |
| Equal | `A, A` | `"A"` |
| Divides | `AA, A` | `"A"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[167 — 14. Longest Common Prefix]]: No repeat. The key idea there: It is a trie's path before the first branch, and a column scan finds it without building one.
- **459. Repeated**: One string.

### In a real interview
Say it in this order:

1. **Brute force:** Test every prefix as a candidate tile against both strings.
2. **Insight:** Two strings are built from one unit exactly when ab equals ba, and the longest unit's length is the gcd of their lengths.
3. **Approach in a few words:** Commutation then gcd.
4. **Cost:** time O(m + n); space O(m + n).

## 5. Recall
Q: 1071. Greatest Common Divisor of Strings: which pattern, and what is the insight?
A: GCD. Two strings are built from one unit exactly when ab equals ba, and the longest unit's length is the gcd of their lengths.

Q: 1071. Greatest Common Divisor of Strings: what is the analogy, and what does it stand for?
A: The largest tile for two floors: same pattern → equal concatenations; largest tile → gcd.

Q: 1071. Greatest Common Divisor of Strings: When is there a common string divisor?
A: When both concatenations are equal.

Q: 1071. Greatest Common Divisor of Strings: what are the time and space costs?
A: Time O(m + n). Space O(m + n).

@@ 3 | TOPIC | 266 — 202. Happy Number | Easy · Cycle detection | MUST
# 266 — 202. Happy Number

**Problem 266 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Cycle detection**

[Open on LeetCode](https://leetcode.com/problems/happy-number/)

## 1. Problem Statement
Repeatedly replace a positive integer by the sum of the squares of its digits. It is happy if this reaches 1, and not if it loops forever without 1. Return whether n is happy.

### Examples
```text
Input:  n = 19
Output: true

Input:  n = 2
Output: false
```

### Constraints
```text
1 <= n <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A circular bus line
You ride a bus whose next stop is decided by the current one. Either it reaches home, the number 1, or it passes a stop you have seen, and then it will circle forever. So write down each stop.

| In the story | In the solution |
|---|---|
| stops | set |
| home | 1 |

### Pattern signal
**Cycle detection.** The values quickly become small and bounded, so the sequence must repeat unless it reaches 1.

### Thought process
1. **First idea:** Keep iterating and hope to see 1, which can run forever.
2. **The insight:** The values quickly become small and bounded, so the sequence must repeat unless it reaches 1.

### Optimal approach
1. Until the value is 1, add it to a set.
2. Fail if it was already there.
3. Replace it with the sum of its squared digits.

### Dry run
```text
n = 19
value  squares        next  seen before?
19     1+81           82    no
82     64+4           68    no
68     36+64          100   no
100    1+0+0          1     reached home, true
```

### Why it works
Each value has one successor. A repeated value therefore repeats everything after it, and since 1 was not seen before the repeat it never will be.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isHappy(int n) {
        Set<Integer> stops = new HashSet<>();
        while (n != 1) {
            if (!stops.add(n)) return false;
            int next = 0;
            for (int x = n; x > 0; x /= 10) next += (x % 10) * (x % 10);
            n = next;
        }
        return true;
    }
}
```

### Code walkthrough
- `!stops.add(n)`: False means a stop seen before, so this is a loop.
- `x % 10`: Extracts the last digit so it can be squared.
- `n = next`: Move on to the next stop.

### Complexity
- **Time:** O(log n)
- **Space:** O(1), bounded states

### Common mistakes
1. **Looping until 1 may never end.** Fix: remember seen values.
2. **Summing digits is the wrong map.** Fix: square each one.
3. **A fixed iteration count proves nothing.** Fix: detect the repeat.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `true` |
| Two | `2` | `false` |
| Seven | `7` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[114 — 141. Linked List Cycle]]: Two speeds. The key idea there: A speed difference of exactly one closes any gap around a loop.
- [[269 — 263. Ugly Number]]: Reduction. The key idea there: You don't need to know the other factors, only whether any exist after removing the allowed ones.

### In a real interview
Say it in this order:

1. **Brute force:** Keep iterating and hope to see 1, which can run forever.
2. **Insight:** The values quickly become small and bounded, so the sequence must repeat unless it reaches 1.
3. **Approach in a few words:** Sequence cycle detection.
4. **Cost:** time O(log n); space O(1), bounded states.

## 5. Recall
Q: 202. Happy Number: which pattern, and what is the insight?
A: Cycle detection. The values quickly become small and bounded, so the sequence must repeat unless it reaches 1.

Q: 202. Happy Number: what is the analogy, and what does it stand for?
A: A circular bus line: stops → set; home → 1.

Q: 202. Happy Number: How is unhappy detected?
A: A repeated value.

Q: 202. Happy Number: what are the time and space costs?
A: Time O(log n). Space O(1), bounded states.

@@ 3 | TOPIC | 267 — 66. Plus One | Easy · Carry | MUST
# 267 — 66. Plus One

**Problem 267 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Carry**

[Open on LeetCode](https://leetcode.com/problems/plus-one/)

## 1. Problem Statement
A large integer is given as an array of digits, most significant first and without leading zeros. Add one and return the resulting array.

### Examples
```text
Input:  digits = [1,2,3]
Output: [1,2,4]

Input:  digits = [4,3,2,1]
Output: [4,3,2,2]

Input:  digits = [9]
Output: [1,0]
```

### Constraints
```text
1 <= digits.length <= 100
0 <= digits[i] <= 9
digits does not contain any leading 0's.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An odometer clicking
An odometer clicks up by one. Every 9 at the right rolls to 0, and the first wheel that is not 9 moves up and stops the chain. If all wheels were 9 a new wheel showing 1 appears on the left.

| In the story | In the solution |
|---|---|
| roll | 0 |
| grow | new array |

### Pattern signal
**Carry.** Adding one only changes the trailing 9s and one digit before them.

### Thought process
1. **First idea:** Parse the digits into an integer, which overflows after about 19 digits.
2. **The insight:** Adding one only changes the trailing 9s and one digit before them.

### Optimal approach
1. Walk from the last digit.
2. Increment and return if it is below 9.
3. Otherwise write zero and continue.
4. If all rolled over allocate a longer array.

### Dry run
```text
1,2,9
digit  array
9      1,2,0
2      1,3,0
```

### Why it works
The carry is exactly one until a digit below 9 absorbs it. No digit to the left of that one can change.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] plusOne(int[] digits) {
        for (int i = digits.length - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;
                return digits;
            }
            digits[i] = 0;
        }
        int[] longer = new int[digits.length + 1];
        longer[0] = 1;
        return longer;
    }
}
```

### Code walkthrough
- `digits[i] < 9`: This wheel can absorb the carry, so we are finished.
- `digits[i] = 0`: A 9 rolls over and passes the carry left.
- `longer[0] = 1`: All were 9, so the answer is 1 followed by zeros.

### Complexity
- **Time:** O(n)
- **Space:** O(1), O(n) if it grows

### Common mistakes
1. **Parsing overflows.** Fix: work on the digits.
2. **Continuing after the carry stops corrupts digits.** Fix: return.
3. **All nines need a longer array.** Fix: allocate one more.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Nine | `[9]` | `[1,0]` |
| Zero | `[0]` | `[1]` |
| Middle | `[1,9]` | `[2,0]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[275 — 67. Add Binary]]: Two inputs. The key idea there: Binary addition is column addition with base 2 instead of 10.
- [[121 — 2. Add Two Numbers]]: Links. The key idea there: With units first, walking forward is exactly the addition algorithm.

### In a real interview
Say it in this order:

1. **Brute force:** Parse the digits into an integer, which overflows after about 19 digits.
2. **Insight:** Adding one only changes the trailing 9s and one digit before them.
3. **Approach in a few words:** Carry from the end.
4. **Cost:** time O(n); space O(1), O(n) if it grows.

## 5. Recall
Q: 66. Plus One: which pattern, and what is the insight?
A: Carry. Adding one only changes the trailing 9s and one digit before them.

Q: 66. Plus One: what is the analogy, and what does it stand for?
A: An odometer clicking: roll → 0; grow → new array.

Q: 66. Plus One: When does the array grow?
A: All digits are nine.

Q: 66. Plus One: what are the time and space costs?
A: Time O(n). Space O(1), O(n) if it grows.

@@ 3 | TOPIC | 268 — 9. Palindrome Number | Easy · Digit reversal | NICE
# 268 — 9. Palindrome Number

**Problem 268 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Digit reversal**

[Open on LeetCode](https://leetcode.com/problems/palindrome-number/)

## 1. Problem Statement
You get an integer x. Return true if it reads the same from both directions, the minus sign included. Follow-up: don't convert it to a string.

### Examples
```text
Input:  x = 121
Output: true

Input:  x = -121
Output: false

Input:  x = 10
Output: false
```

### Constraints
```text
-2^31 <= x <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Folding a number
Fold the number in the middle like paper. Peel digits off the back onto a new number, which reverses them, until you hold half. Then lay it on what is left of the front.

| In the story | In the solution |
|---|---|
| peel | % 10 |
| fold | half |

### Pattern signal
**Digit reversal.** Only half needs reversing, and half of an int can never overflow.

### Thought process
1. **First idea:** Convert to text and compare, which uses extra memory.
2. **The insight:** Only half needs reversing, and half of an int can never overflow.

### Optimal approach
1. Reject negatives and non-zero numbers ending in zero.
2. Move last digits to a reversed number.
3. Stop when it is at least the front.
4. Compare, ignoring an odd middle digit.

### Dry run
```text
x = 1221
step   front  back  keep going?
start  1221   0     yes
1      122    1     yes
2      12     12    no, front is not bigger
front equals back, so true
```

### Why it works
When the loop stops the reversed part holds the back half. A palindrome's back half reversed equals its front half, with the middle digit irrelevant.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isPalindrome(int x) {
        if (x < 0 || (x % 10 == 0 && x != 0)) return false;
        int back = 0;
        while (x > back) {
            back = back * 10 + x % 10;
            x /= 10;
        }
        return x == back || x == back / 10;
    }
}
```

### Code walkthrough
- `x != 0`: Zero itself is a palindrome, other trailing zeros are not.
- `x > back`: Keep peeling until the back has at least half the digits.
- `back / 10`: Removes the middle digit of an odd-length number.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **A negative cannot be a palindrome.** Fix: reject it.
2. **A trailing zero fools the half method.** Fix: reject it except for 0.
3. **Reversing it all can overflow.** Fix: reverse half.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `0` | `true` |
| Negative | `-1` | `false` |
| Ten | `10` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[036 — 125. Valid Palindrome]]: Pointers. The key idea there: A palindrome is only a claim about mirrored pairs, and those can be compared in place.
- [[299 — 7. Reverse Integer]]: Whole. The key idea there: Overflow can be predicted before it happens by comparing with the limit divided by ten.

### In a real interview
Say it in this order:

1. **Brute force:** Convert to text and compare, which uses extra memory.
2. **Insight:** Only half needs reversing, and half of an int can never overflow.
3. **Approach in a few words:** Reverse half.
4. **Cost:** time O(log n); space O(1).

## 5. Recall
Q: 9. Palindrome Number: which pattern, and what is the insight?
A: Digit reversal. Only half needs reversing, and half of an int can never overflow.

Q: 9. Palindrome Number: what is the analogy, and what does it stand for?
A: Folding a number: peel → % 10; fold → half.

Q: 9. Palindrome Number: Why only half?
A: It suffices and avoids overflow.

Q: 9. Palindrome Number: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 269 — 263. Ugly Number | Easy · Factor removal | NICE
# 269 — 263. Ugly Number

**Problem 269 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Factor removal**

[Open on LeetCode](https://leetcode.com/problems/ugly-number/)

## 1. Problem Statement
An ugly number is a positive integer whose only prime factors are among 2, 3 and 5, with 1 included. Return true if n is ugly.

### Examples
```text
Input:  n = 6
Output: true

Input:  n = 1
Output: true

Input:  n = 14
Output: false
```

### Constraints
```text
-2^31 <= n <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Unpacking only three box types
A warehouse may contain only boxes of type 2, 3 and 5. Carry out every such box. If anything other than the empty floor, the number 1, remains, a forbidden box was inside.

| In the story | In the solution |
|---|---|
| remove | divide |
| one | empty |

### Pattern signal
**Factor removal.** You don't need to know the other factors, only whether any exist after removing the allowed ones.

### Thought process
1. **First idea:** Find all prime factors of n, which is far more work.
2. **The insight:** You don't need to know the other factors, only whether any exist after removing the allowed ones.

### Optimal approach
1. Reject zero and negatives.
2. Divide by 2 while possible.
3. Do the same for 3 and 5.
4. Check that 1 remains.

### Dry run
```text
30
after 2  15
after 3  5
after 5  1
```

### Why it works
Prime factorization is unique, so after all 2s, 3s and 5s are removed the rest is the product of every forbidden factor. It is 1 exactly when there is none.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isUgly(int n) {
        if (n <= 0) return false;
        for (int p : new int[] {2, 3, 5}) {
            while (n % p == 0) n /= p;
        }
        return n == 1;
    }
}
```

### Code walkthrough
- `n <= 0`: Non-positive values are excluded, and zero would loop forever.
- `while`: A factor can appear many times, so remove all copies.
- `n == 1`: An empty warehouse means no forbidden factors.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Zero divides forever.** Fix: reject non-positives.
2. **One division leaves copies.** Fix: divide with while.
3. **1 has no bad factor.** Fix: return true for it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `true` |
| Seven | `14` | `false` |
| Zero | `0` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **264. nth**: Pointers.
- [[266 — 202. Happy Number]]: Cycle. The key idea there: The values quickly become small and bounded, so the sequence must repeat unless it reaches 1.

### In a real interview
Say it in this order:

1. **Brute force:** Find all prime factors of n, which is far more work.
2. **Insight:** You don't need to know the other factors, only whether any exist after removing the allowed ones.
3. **Approach in a few words:** Strip allowed factors.
4. **Cost:** time O(log n); space O(1).

## 5. Recall
Q: 263. Ugly Number: which pattern, and what is the insight?
A: Factor removal. You don't need to know the other factors, only whether any exist after removing the allowed ones.

Q: 263. Ugly Number: what is the analogy, and what does it stand for?
A: Unpacking only three box types: remove → divide; one → empty.

Q: 263. Ugly Number: What must remain of an ugly number?
A: One.

Q: 263. Ugly Number: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 270 — 136. Single Number | Easy · XOR cancellation | MUST
# 270 — 136. Single Number

**Problem 270 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: XOR cancellation**

[Open on LeetCode](https://leetcode.com/problems/single-number/)

## 1. Problem Statement
In a non-empty integer array every element appears exactly twice except one. Return that one, using linear time and constant extra space.

### Examples
```text
Input:  nums = [2,2,1]
Output: 1

Input:  nums = [4,1,2,1,2]
Output: 4

Input:  nums = [1]
Output: 1
```

### Constraints
```text
1 <= nums.length <= 3 * 10^4
-3 * 10^4 <= nums[i] <= 3 * 10^4
Each element in the array appears twice except for one element which appears only once.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Light switches pressed twice
Each number flips a particular set of light switches. Flipping the same set twice leaves everything as it was, whatever happened in between. After all numbers only the single one's switches are still on.

| In the story | In the solution |
|---|---|
| press | XOR |
| pattern | result |

### Pattern signal
**XOR cancellation.** XOR is that flip: x XOR x is 0, and order doesn't matter.

### Thought process
1. **First idea:** Count with a map or set, using linear memory.
2. **The insight:** XOR is that flip: x XOR x is 0, and order doesn't matter.

### Optimal approach
1. Start from zero.
2. XOR in every number.
3. Return what remains.

### Dry run
```text
2,2,1
press  pattern
2      2
2      0
1      1
```

### Why it works
XOR is commutative and associative, so the numbers can be regrouped into equal pairs, each giving 0, plus the single one.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int singleNumber(int[] nums) {
        int pattern = 0;
        for (int x : nums) pattern ^= x;
        return pattern;
    }
}
```

### Code walkthrough
- `pattern = 0`: All switches start off.
- `^= x`: Flip this number's switches.
- `return pattern`: The lights still on spell the single number.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Triples do not cancel.** Fix: count bits for that variation.
2. **Addition does not cancel pairs.** Fix: use XOR.
3. **Sorting breaks the linear target.** Fix: fold once.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5]` | `5` |
| Negative | `[-1,2,2]` | `-1` |
| Zero | `[0,1,1]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[274 — 268. Missing Number]]: XOR indices. The key idea there: The expected and actual sums differ only by the missing number.
- **137. Triples**: Bit counts.

### In a real interview
Say it in this order:

1. **Brute force:** Count with a map or set, using linear memory.
2. **Insight:** XOR is that flip: x XOR x is 0, and order doesn't matter.
3. **Approach in a few words:** XOR cancels.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 136. Single Number: which pattern, and what is the insight?
A: XOR cancellation. XOR is that flip: x XOR x is 0, and order doesn't matter.

Q: 136. Single Number: what is the analogy, and what does it stand for?
A: Light switches pressed twice: press → XOR; pattern → result.

Q: 136. Single Number: Why does XOR find the single?
A: Pairs become zero.

Q: 136. Single Number: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Review 45 | Re-solve without looking | MUST
# Review 45

Solve on a blank page. No notes, hints or solutions.

## 136. Single Number
Find the only value not appearing twice.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/single-number/)

## 66. Plus One
Add one to a digit array.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/plus-one/)

## 72. Edit Distance
Find minimum edit operations between strings.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/edit-distance/)

## 213. House Robber II
Rob houses arranged in a circle.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/house-robber-ii/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 136 | | | | |
| 66 | | | | |
| 72 | | | | |
| 213 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[270 — 136. Single Number]]
- [[267 — 66. Plus One]]
- [[256 — 72. Edit Distance]]
- [[232 — 213. House Robber II]]

@@ 3 | TOPIC | 271 — 191. Number of 1 Bits | Easy · Bit count | MUST
# 271 — 191. Number of 1 Bits

**Problem 271 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Bit count**

[Open on LeetCode](https://leetcode.com/problems/number-of-1-bits/)

## 1. Problem Statement
You get an integer. Return the number of 1 bits in its binary representation, also called its Hamming weight.

### Examples
```text
Input:  n = 11
Output: 3

Input:  n = 128
Output: 1

Input:  n = 2147483645
Output: 30
```

### Constraints
```text
1 <= n <= 2^31 - 1
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Blowing out candles
Candles stand in a row, some lit. One special puff, n AND n-1, blows out exactly the rightmost lit candle, however far away it is. The number of puffs is the number of lit candles.

| In the story | In the solution |
|---|---|
| puff | n &amp; (n-1) |
| lit | set bit |

### Pattern signal
**Bit count.** Subtracting one flips the lowest set bit and everything below it, so ANDing with n erases just that bit.

### Thought process
1. **First idea:** Inspect all 32 positions one at a time.
2. **The insight:** Subtracting one flips the lowest set bit and everything below it, so ANDing with n erases just that bit.

### Optimal approach
1. Start a counter.
2. While n is non-zero clear its lowest set bit.
3. Add one per clearing.

### Dry run
```text
11 = 1011
left  count
1010  1
1000  2
0     3
```

### Why it works
Every round removes exactly one set bit and never creates one, so the rounds equal the number of set bits.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;
            count++;
        }
        return count;
    }
}
```

### Code walkthrough
- `n != 0`: Non-zero also covers negative bit patterns.
- `n &= n - 1`: Blow out the rightmost lit candle.
- `count++`: One puff counts one candle.

### Complexity
- **Time:** O(set bits), at most 32
- **Space:** O(1)

### Common mistakes
1. **n greater than 0 skips negatives.** Fix: loop while non-zero.
2. **A signed shift may never end.** Fix: clear the lowest bit.
3. **Counting loop rounds over all bits counts zeros.** Fix: count only set bits.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `1` |
| Power | `8` | `1` |
| All | `-1` | `32` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[272 — 338. Counting Bits]]: DP. The key idea there: Clearing the lowest set bit of i gives a smaller number, whose answer is already in the array.
- [[273 — 190. Reverse Bits]]: Shift. The key idea there: Popping from the low end while pushing onto the low end reverses a sequence.

### In a real interview
Say it in this order:

1. **Brute force:** Inspect all 32 positions one at a time.
2. **Insight:** Subtracting one flips the lowest set bit and everything below it, so ANDing with n erases just that bit.
3. **Approach in a few words:** Clear the lowest bit.
4. **Cost:** time O(set bits), at most 32; space O(1).

## 5. Recall
Q: 191. Number of 1 Bits: which pattern, and what is the insight?
A: Bit count. Subtracting one flips the lowest set bit and everything below it, so ANDing with n erases just that bit.

Q: 191. Number of 1 Bits: what is the analogy, and what does it stand for?
A: Blowing out candles: puff → n &amp; (n-1); lit → set bit.

Q: 191. Number of 1 Bits: What does n AND n-1 do?
A: Clears the lowest set bit.

Q: 191. Number of 1 Bits: what are the time and space costs?
A: Time O(set bits), at most 32. Space O(1).

@@ 3 | TOPIC | 272 — 338. Counting Bits | Easy · Bit DP | MUST
# 272 — 338. Counting Bits

**Problem 272 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Bit DP**

[Open on LeetCode](https://leetcode.com/problems/counting-bits/)

## 1. Problem Statement
You get an integer n. Return an array in which position i holds the number of 1 bits of i, for every i from 0 to n. Follow-up: linear time without a built-in count.

### Examples
```text
Input:  n = 2
Output: [0,1,1]

Input:  n = 5
Output: [0,1,1,2,1,2]
```

### Constraints
```text
0 <= n <= 10^5
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Asking a solved smaller cake
For each cake you could count the candles, but blow out one and it becomes a smaller cake you already counted. So its answer is that cake's answer plus one.

| In the story | In the solution |
|---|---|
| smaller | i &amp; (i-1) |
| one | plus |

### Pattern signal
**Bit DP.** Clearing the lowest set bit of i gives a smaller number, whose answer is already in the array.

### Thought process
1. **First idea:** Count the bits of every number separately, up to 32 steps each.
2. **The insight:** Clearing the lowest set bit of i gives a smaller number, whose answer is already in the array.

### Optimal approach
1. Create an array of n+1 with zero at index 0.
2. For each i clear its lowest bit.
3. Copy that count and add one.

### Dry run
```text
5
i  from  count
1  0     1
2  0     1
3  2     2
4  0     1
5  4     2
```

### Why it works
i AND i-1 has exactly one set bit fewer and is smaller than i, so it is always computed already.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] countBits(int n) {
        int[] count = new int[n + 1];
        for (int i = 1; i <= n; i++) count[i] = count[i & (i - 1)] + 1;
        return count;
    }
}
```

### Code walkthrough
- `n + 1`: The result includes n itself.
- `i & (i - 1)`: The smaller, already counted cake.
- `+ 1`: Add back the candle that was removed.

### Complexity
- **Time:** O(n)
- **Space:** O(n) for the output

### Common mistakes
1. **Operator precedence misleads.** Fix: parenthesize i-1.
2. **Zero has no predecessor.** Fix: start the loop at 1.
3. **The result includes n.** Fix: allocate n+1.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `0` | `[0]` |
| One | `1` | `[0,1]` |
| Three | `3` | `[0,1,1,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[271 — 191. Number of 1 Bits]]: Loop. The key idea there: Subtracting one flips the lowest set bit and everything below it, so ANDing with n erases just that bit.
- [[229 — 70. Climbing Stairs]]: Earlier states. The key idea there: Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.

### In a real interview
Say it in this order:

1. **Brute force:** Count the bits of every number separately, up to 32 steps each.
2. **Insight:** Clearing the lowest set bit of i gives a smaller number, whose answer is already in the array.
3. **Approach in a few words:** Bit DP.
4. **Cost:** time O(n); space O(n) for the output.

## 5. Recall
Q: 338. Counting Bits: which pattern, and what is the insight?
A: Bit DP. Clearing the lowest set bit of i gives a smaller number, whose answer is already in the array.

Q: 338. Counting Bits: what is the analogy, and what does it stand for?
A: Asking a solved smaller cake: smaller → i &amp; (i-1); one → plus.

Q: 338. Counting Bits: What is count-bits recurrence?
A: One plus the value with lowest bit cleared.

Q: 338. Counting Bits: what are the time and space costs?
A: Time O(n). Space O(n) for the output.

@@ 3 | TOPIC | 273 — 190. Reverse Bits | Easy · Bit reversal | MUST
# 273 — 190. Reverse Bits

**Problem 273 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Bit reversal**

[Open on LeetCode](https://leetcode.com/problems/reverse-bits/)

## 1. Problem Statement
You get a 32-bit integer. Reverse the order of all 32 bits, leading zeros included, and return the resulting integer.

### Examples
```text
Input:  n = 43261596
Output: 964176192

Input:  n = 2147483644
Output: 1073741822
```

### Constraints
```text
0 <= n <= 2^31 - 2
n is even.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Moving beads between wires
Slide 32 beads off the end of one wire onto another. The bead that left first is pushed furthest along, so the order reverses. Blank beads must move too.

| In the story | In the solution |
|---|---|
| bead | n&amp;1 |
| wire | answer |

### Pattern signal
**Bit reversal.** Popping from the low end while pushing onto the low end reverses a sequence.

### Thought process
1. **First idea:** Build a binary string, pad it, reverse it and parse it.
2. **The insight:** Popping from the low end while pushing onto the low end reverses a sequence.

### Optimal approach
1. Repeat 32 times.
2. Shift the answer left.
3. Copy in the input's low bit.
4. Shift the input right with zero fill.

### Dry run
```text
4-bit 1011
pop  answer
1    1
1    11
0    110
1    1101
```

### Why it works
The bit originally at position i is copied in round i and then shifted 31-i more times, landing at its mirrored position.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int reverseBits(int n) {
        int answer = 0;
        for (int i = 0; i < 32; i++) {
            answer = (answer << 1) | (n & 1);
            n >>>= 1;
        }
        return answer;
    }
}
```

### Code walkthrough
- `i < 32`: All 32 positions move, including leading zeros.
- `(n & 1)`: Read the bead at the end of the input.
- `>>>=`: Remove it, filling with zero whatever the sign.

### Complexity
- **Time:** O(1), 32 rounds
- **Space:** O(1)

### Common mistakes
1. **Stopping at zero loses trailing zeros.** Fix: do all 32 rounds.
2. **31 rounds are one short.** Fix: loop below 32.
3. **A signed shift copies the sign.** Fix: use an unsigned one.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `0` | `0` |
| Two | `2` | `1073741824` |
| Four | `4` | `536870912` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[299 — 7. Reverse Integer]]: Overflow. The key idea there: Overflow can be predicted before it happens by comparing with the limit divided by ten.
- [[271 — 191. Number of 1 Bits]]: Positions free. The key idea there: Subtracting one flips the lowest set bit and everything below it, so ANDing with n erases just that bit.

### In a real interview
Say it in this order:

1. **Brute force:** Build a binary string, pad it, reverse it and parse it.
2. **Insight:** Popping from the low end while pushing onto the low end reverses a sequence.
3. **Approach in a few words:** Pop and append 32 times.
4. **Cost:** time O(1), 32 rounds; space O(1).

## 5. Recall
Q: 190. Reverse Bits: which pattern, and what is the insight?
A: Bit reversal. Popping from the low end while pushing onto the low end reverses a sequence.

Q: 190. Reverse Bits: what is the analogy, and what does it stand for?
A: Moving beads between wires: bead → n&amp;1; wire → answer.

Q: 190. Reverse Bits: Why exactly 32 rounds?
A: Leading zeros become trailing.

Q: 190. Reverse Bits: what are the time and space costs?
A: Time O(1), 32 rounds. Space O(1).

@@ 3 | TOPIC | 274 — 268. Missing Number | Easy · Expected sum | MUST
# 274 — 268. Missing Number

**Problem 274 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Expected sum**

[Open on LeetCode](https://leetcode.com/problems/missing-number/)

## 1. Problem Statement
An array holds n distinct numbers from the range 0 to n. Return the only number of that range that is missing. Follow-up: O(1) space and O(n) time.

### Examples
```text
Input:  nums = [3,0,1]
Output: 2

Input:  nums = [0,1]
Output: 2

Input:  nums = [9,6,4,2,3,5,7,0,1]
Output: 8
```

### Constraints
```text
n == nums.length
1 <= n <= 10^4
0 <= nums[i] <= n
All the numbers of nums are unique.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A till short by one note
A cashier knows exactly what the till should total. One note is missing, so without listing the notes the shortfall names it.

| In the story | In the solution |
|---|---|
| correct | formula |
| gap | difference |

### Pattern signal
**Expected sum.** The expected and actual sums differ only by the missing number.

### Thought process
1. **First idea:** Put everything in a set and test 0 to n, using linear space.
2. **The insight:** The expected and actual sums differ only by the missing number.

### Optimal approach
1. Compute n(n+1)/2.
2. Subtract every value present.
3. Return the difference.

### Dry run
```text
3,0,1
expected 6
actual   4
missing  2
```

### Why it works
Every number other than the missing one appears in both sums and cancels, so exactly that number is left.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int missingNumber(int[] nums) {
        int n = nums.length;
        int gap = n * (n + 1) / 2;
        for (int x : nums) gap -= x;
        return gap;
    }
}
```

### Code walkthrough
- `n * (n + 1) / 2`: The total of the full range 0 to n.
- `gap -= x`: Cross this value off the expected total.
- `return gap`: The shortfall is the missing value.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Summing to n-1 is off by one.** Fix: sum to n.
2. **One equation finds one value.** Fix: use marking for several.
3. **Large n overflows.** Fix: use long or XOR there.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `[1]` | `0` |
| End | `[0]` | `1` |
| Middle | `[0,2]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[270 — 136. Single Number]]: Safe. The key idea there: XOR is that flip: x XOR x is 0, and order doesn't matter.
- [[011 — 448. Find All Numbers Disappeared in an Array]]: Buckets. The key idea there: Values and indices cover the same range, so the array can be its own set.

### In a real interview
Say it in this order:

1. **Brute force:** Put everything in a set and test 0 to n, using linear space.
2. **Insight:** The expected and actual sums differ only by the missing number.
3. **Approach in a few words:** Expected minus actual.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 268. Missing Number: which pattern, and what is the insight?
A: Expected sum. The expected and actual sums differ only by the missing number.

Q: 268. Missing Number: what is the analogy, and what does it stand for?
A: A till short by one note: correct → formula; gap → difference.

Q: 268. Missing Number: What is the expected sum?
A: n(n+1)/2.

Q: 268. Missing Number: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 275 — 67. Add Binary | Easy · Carry | NICE
# 275 — 67. Add Binary

**Problem 275 – Topic: Mixed / Hard and Contest-style | Difficulty: Easy | Pattern: Carry**

[Open on LeetCode](https://leetcode.com/problems/add-binary/)

## 1. Problem Statement
You get two binary numbers as strings without leading zeros. Return their sum as a binary string. They can be too long for any integer type.

### Examples
```text
Input:  a = "11", b = "1"
Output: "100"

Input:  a = "1010", b = "1011"
Output: "10101"
```

### Constraints
```text
1 <= a.length, b.length <= 10^4
a and b consist only of '0' or '1' characters.
Each string does not contain leading zeros except for the zero itself.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Paper addition with two symbols
Add on paper with only two digits. In each column from the right write whether the sum is odd and carry one for every two, continuing while either number or a carry remains.

| In the story | In the solution |
|---|---|
| write | %2 |
| carry | /2 |

### Pattern signal
**Carry.** Binary addition is column addition with base 2 instead of 10.

### Thought process
1. **First idea:** Parse both into integers, which overflows for long inputs.
2. **The insight:** Binary addition is column addition with base 2 instead of 10.

### Optimal approach
1. Point at both last characters.
2. Add the available digits and the carry.
3. Append the sum modulo 2.
4. Carry the sum divided by 2.
5. Reverse at the end.

### Dry run
```text
11+1
sum  write  carry
2    0      1
2    0      1
1    1      0
```

### Why it works
Each round writes the correct digit of that place and the carry holds all that affects higher places.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String addBinary(String a, String b) {
        StringBuilder result = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, carry = 0;
        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;
            if (i >= 0) sum += a.charAt(i--) - '0';
            if (j >= 0) sum += b.charAt(j--) - '0';
            result.append(sum % 2);
            carry = sum / 2;
        }
        return result.reverse().toString();
    }
}
```

### Code walkthrough
- `carry > 0`: A final carry needs a column of its own.
- `- '0'`: Turns the character 0 or 1 into its numeric value.
- `reverse()`: Digits were produced from the lowest place upward.

### Complexity
- **Time:** O(m + n)
- **Space:** O(m + n)

### Common mistakes
1. **Parsing overflows.** Fix: add by column.
2. **The last carry is easy to lose.** Fix: loop while it exists.
3. **Prepending is quadratic.** Fix: append and reverse.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `0,0` | `"0"` |
| One | `1,1` | `"10"` |
| Unequal | `100,1` | `"101"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[121 — 2. Add Two Numbers]]: Base 10. The key idea there: With units first, walking forward is exactly the addition algorithm.
- [[296 — 43. Multiply Strings]]: Columns. The key idea there: Place values depend only on the two indices, so products can be accumulated in one array.

### In a real interview
Say it in this order:

1. **Brute force:** Parse both into integers, which overflows for long inputs.
2. **Insight:** Binary addition is column addition with base 2 instead of 10.
3. **Approach in a few words:** Two pointers and carry.
4. **Cost:** time O(m + n); space O(m + n).

## 5. Recall
Q: 67. Add Binary: which pattern, and what is the insight?
A: Carry. Binary addition is column addition with base 2 instead of 10.

Q: 67. Add Binary: what is the analogy, and what does it stand for?
A: Paper addition with two symbols: write → %2; carry → /2.

Q: 67. Add Binary: Which digit is written?
A: Sum modulo two.

Q: 67. Add Binary: what are the time and space costs?
A: Time O(m + n). Space O(m + n).

@@ 2 | MODULE | Mixed / Hard and Contest-style — Medium | 24 problems | MUST
# Mixed / Hard and Contest-style: Medium

Patterns in this group: Map plus doubly list, Kadane, Max and min Kadane, Greedy reach, Implicit BFS, Reset start, Sorted counts, Safe elements, Last occurrence, Two index stacks, Sort by regret, Three zones, Sort and merge, Interval greedy, In-place rotation, Shrinking borders, Markers in matrix, Greedy symbols, Binary exponentiation, Schoolbook multiplication, Cycle indices, XOR and carry, Digit reversal.

@@ 3 | TOPIC | 276 — 146. LRU Cache | Medium · Map plus doubly list | MUST
# 276 — 146. LRU Cache

**Problem 276 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Map plus doubly list**

[Open on LeetCode](https://leetcode.com/problems/lru-cache/)

## 1. Problem Statement
Design an LRU cache of fixed positive capacity. `get(key)` returns the value or -1. `put(key, value)` inserts or updates, evicting the least recently used key when over capacity. Both count as use and must run in O(1) average.

### Examples
```text
Input:  ["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
        [[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
Output: [null, null, null, 1, null, -1, null, -1, 3, 4]
```

### Constraints
```text
1 <= capacity <= 3000
0 <= key <= 10^4
0 <= value <= 10^5
At most 2 * 10^5 calls will be made to get and put.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A desk pile with an index
Books lie in a pile on your desk and an index card tells you where each is. A book you touch moves to the top. When the desk is full the bottom book, untouched for longest, goes.

| In the story | In the solution |
|---|---|
| index | map |
| pile | doubly list |

### Pattern signal
**Map plus doubly list.** A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).

### Thought process
1. **First idea:** Store a timestamp per key and scan for the oldest at eviction, in linear time.
2. **The insight:** A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).

### Optimal approach
1. Find the key in the map.
2. Detach its node.
3. Attach it at the front.
4. On inserting into a full cache remove the node before the bottom sentinel from both structures.

### Dry run
```text
capacity 2
op     front to back
put1   1
put2   2,1
get1   1,2
put3   3,1
```

### Why it works
Every use moves a key to the front, so the list is always sorted by recency and its last node is exactly the one to evict.

## 3. Java Solution with Explanation
### Code
```java
class LRUCache {
    private static class Book {
        int key, value;
        Book prev, next;
    }

    private final Map<Integer, Book> index = new HashMap<>();
    private final Book top = new Book(), bottom = new Book();
    private final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        top.next = bottom;
        bottom.prev = top;
    }

    public int get(int key) {
        Book book = index.get(key);
        if (book == null) return -1;
        detach(book);
        onTop(book);
        return book.value;
    }

    public void put(int key, int value) {
        Book book = index.get(key);
        if (book != null) {
            detach(book);
        } else {
            if (index.size() == capacity) {
                Book oldest = bottom.prev;
                detach(oldest);
                index.remove(oldest.key);
            }
            book = new Book();
            book.key = key;
            index.put(key, book);
        }
        book.value = value;
        onTop(book);
    }

    private void detach(Book b) {
        b.prev.next = b.next;
        b.next.prev = b.prev;
    }

    private void onTop(Book b) {
        b.next = top.next;
        b.prev = top;
        top.next.prev = b;
        top.next = b;
    }
}
```

### Code walkthrough
- `top.next = bottom`: Two sentinels mean detaching never meets null.
- `index.remove(oldest.key)`: The node stores its key so the map can be cleaned on eviction.
- `onTop(book)`: Both reading and writing count as use.

### Complexity
- **Time:** O(1) per operation
- **Space:** O(capacity)

### Common mistakes
1. **An updating put is a use.** Fix: move it to the front.
2. **A forgotten map entry is a ghost.** Fix: remove key and node together.
3. **A singly linked node cannot detach itself.** Fix: link both ways.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Update | `put1,1 put1,2 get1` | `2` |
| One | `cap1 put1 put2 get1` | `-1` |
| Missing | `get9` | `-1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[300 — 460. LFU Cache]]: Shelves. The key idea there: A count only grows by one, so books move one shelf at a time and the lowest shelf can be tracked in O(1).
- [[031 — 380. Insert Delete GetRandom O(1)]]: Map and array. The key idea there: Combine them, and make deletion cheap by giving up order.

### In a real interview
Say it in this order:

1. **Brute force:** Store a timestamp per key and scan for the oldest at eviction, in linear time.
2. **Insight:** A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).
3. **Approach in a few words:** Map plus recency list.
4. **Cost:** time O(1) per operation; space O(capacity).

## 5. Recall
Q: 146. LRU Cache: which pattern, and what is the insight?
A: Map plus doubly list. A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).

Q: 146. LRU Cache: what is the analogy, and what does it stand for?
A: A desk pile with an index: index → map; pile → doubly list.

Q: 146. LRU Cache: Why doubly linked?
A: To detach a known node in O(1).

Q: 146. LRU Cache: what are the time and space costs?
A: Time O(1) per operation. Space O(capacity).

@@ 3 | TOPIC | Practice — Review 46 | Re-solve without looking | MUST
# Review 46

Solve on a blank page. No notes, hints or solutions.

## 146. LRU Cache
Design an O(1) least-recently-used cache.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/lru-cache/)

## 190. Reverse Bits
Reverse 32 bits.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/reverse-bits/)

## 312. Burst Balloons
Maximize coins from bursting balloons.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/burst-balloons/)

## 139. Word Break
Decide whether a string splits into dictionary words.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/word-break/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 146 | | | | |
| 190 | | | | |
| 312 | | | | |
| 139 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[276 — 146. LRU Cache]]
- [[273 — 190. Reverse Bits]]
- [[262 — 312. Burst Balloons]]
- [[238 — 139. Word Break]]

@@ 3 | TOPIC | 277 — 53. Maximum Subarray | Medium · Kadane | MUST
# 277 — 53. Maximum Subarray

**Problem 277 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Kadane**

[Open on LeetCode](https://leetcode.com/problems/maximum-subarray/)

## 1. Problem Statement
You get an integer array, possibly all negative. Return the largest sum of any contiguous non-empty subarray.

### Examples
```text
Input:  nums = [-2,1,-3,4,-1,2,1,-5,4]
Output: 6

Input:  nums = [1]
Output: 1

Input:  nums = [5,4,-1,7,8]
Output: 23
```

### Constraints
```text
1 <= nums.length <= 10^5
-10^4 <= nums[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Dropping a losing backpack
You hike collecting gains and losses in a backpack. While its total is positive it helps whatever comes next. Once the total is negative it is only dead weight, so drop it and start empty at the next step.

| In the story | In the solution |
|---|---|
| backpack | current |
| drop | restart |

### Pattern signal
**Kadane.** The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.

### Thought process
1. **First idea:** Sum every possible range with running totals, in O(n²).
2. **The insight:** The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.

### Optimal approach
1. Seed the best with the first value.
2. Before each value drop a negative running sum.
3. Add the value.
4. Record the larger total.

### Dry run
```text
-2,1,-3,4,-1,2,1
x    current
-2   -2
1    1
-3   -2
4    4
-1   3
2    5
1    6
```

### Why it works
The running sum is always the best sum of a range ending at the current index. The optimal range ends somewhere, so it is recorded.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxSubArray(int[] nums) {
        int current = 0, best = nums[0];
        for (int x : nums) {
            if (current < 0) current = 0;
            current += x;
            best = Math.max(best, current);
        }
        return best;
    }
}
```

### Code walkthrough
- `current < 0`: A negative backpack can only hurt the next range.
- `best = nums[0]`: Seeding with a real element keeps all-negative input correct.
- `current += x`: Carry the current value in the backpack.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **A best of 0 breaks all negatives.** Fix: seed with the first value.
2. **A negative value alone is no reason to restart.** Fix: test the running sum.
3. **Skipping elements is not allowed.** Fix: stay contiguous.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[-1]` | `-1` |
| All | `[1,2]` | `3` |
| Bridge | `[3,-1,3]` | `5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[278 — 918. Maximum Sum Circular Subarray]]: Minimum too. The key idea there: The best wrapping sum equals the total minus the minimum subarray, which a second Kadane finds.
- [[237 — 152. Maximum Product Subarray]]: Two. The key idea there: The next largest product can only come from the current largest, the current smallest or a fresh start.

### In a real interview
Say it in this order:

1. **Brute force:** Sum every possible range with running totals, in O(n²).
2. **Insight:** The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.
3. **Approach in a few words:** Kadane.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 53. Maximum Subarray: which pattern, and what is the insight?
A: Kadane. The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.

Q: 53. Maximum Subarray: what is the analogy, and what does it stand for?
A: Dropping a losing backpack: backpack → current; drop → restart.

Q: 53. Maximum Subarray: When does Kadane restart?
A: When the running sum is negative.

Q: 53. Maximum Subarray: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 278 — 918. Maximum Sum Circular Subarray | Medium · Max and min Kadane | SHOULD
# 278 — 918. Maximum Sum Circular Subarray

**Problem 278 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Max and min Kadane**

[Open on LeetCode](https://leetcode.com/problems/maximum-sum-circular-subarray/)

## 1. Problem Statement
The array is circular: the last element is followed by the first. Return the largest sum of a non-empty contiguous subarray that uses each position at most once.

### Examples
```text
Input:  nums = [1,-2,3,-2]
Output: 3

Input:  nums = [5,-3,5]
Output: 10

Input:  nums = [-3,-2,-3]
Output: -2
```

### Constraints
```text
n == nums.length
1 <= n <= 3 * 10^4
-3 * 10^4 <= nums[i] <= 3 * 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A necklace's best or worst section
On a circular necklace the best section is either a straight piece, or it wraps past the clasp. A wrapping piece is the whole necklace minus some straight middle, so to make it big cut out the worst middle.

| In the story | In the solution |
|---|---|
| straight | Kadane max |
| worst | Kadane min |

### Pattern signal
**Max and min Kadane.** The best wrapping sum equals the total minus the minimum subarray, which a second Kadane finds.

### Thought process
1. **First idea:** Run Kadane from every rotation, in O(n²).
2. **The insight:** The best wrapping sum equals the total minus the minimum subarray, which a second Kadane finds.

### Optimal approach
1. Track the maximum and minimum sums ending here.
2. Track their best values and the total.
3. Return the plain maximum if all are negative.
4. Otherwise compare it with total minus minimum.

### Dry run
```text
nums = [5,-3,5]
x   high  best  low  worst  total
5   5     5     5    5      5
-3  2     5     -3   -3     2
5   7     7     2    -3     7
wrap = 7 - (-3) = 10, larger than 7
```

### Why it works
Every circular range either avoids the boundary or is the complement of a range that does. Only when all values are negative would that complement be empty.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxSubarraySumCircular(int[] nums) {
        int high = 0, low = 0, total = 0;
        int best = nums[0], worst = nums[0];
        for (int x : nums) {
            high = Math.max(high + x, x);
            best = Math.max(best, high);
            low = Math.min(low + x, x);
            worst = Math.min(worst, low);
            total += x;
        }
        return best < 0 ? best : Math.max(best, total - worst);
    }
}
```

### Code walkthrough
- `high + x`: The best straight piece ending here.
- `low + x`: The worst straight piece ending here, the one to cut.
- `best < 0 ?`: All negative means the wrap would be empty, so it isn't allowed.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Kadane on a doubled array uses items twice.** Fix: use the complement.
2. **All negatives give an empty wrap.** Fix: return the maximum.
3. **The answer may not wrap.** Fix: compare both shapes.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Negative | `[-3,-2]` | `-2` |
| One | `[4]` | `4` |
| Wrap | `[5,-3,5]` | `10` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[277 — 53. Maximum Subarray]]: One. The key idea there: The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.
- [[232 — 213. House Robber II]]: Cases. The key idea there: Any valid circular plan omits the first or the last house, so two straight-line problems cover everything.

### In a real interview
Say it in this order:

1. **Brute force:** Run Kadane from every rotation, in O(n²).
2. **Insight:** The best wrapping sum equals the total minus the minimum subarray, which a second Kadane finds.
3. **Approach in a few words:** Max and min Kadane.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 918. Maximum Sum Circular Subarray: which pattern, and what is the insight?
A: Max and min Kadane. The best wrapping sum equals the total minus the minimum subarray, which a second Kadane finds.

Q: 918. Maximum Sum Circular Subarray: what is the analogy, and what does it stand for?
A: A necklace's best or worst section: straight → Kadane max; worst → Kadane min.

Q: 918. Maximum Sum Circular Subarray: How is a wrapped sum computed?
A: Total minus the minimum middle.

Q: 918. Maximum Sum Circular Subarray: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 279 — 55. Jump Game | Medium · Greedy reach | MUST
# 279 — 55. Jump Game

**Problem 279 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Greedy reach**

[Open on LeetCode](https://leetcode.com/problems/jump-game/)

## 1. Problem Statement
You start at index 0 and `nums[i]` is the furthest you may jump forward from i. Return true if you can reach the last index.

### Examples
```text
Input:  nums = [2,3,1,1,4]
Output: true

Input:  nums = [3,2,1,0,4]
Output: false
```

### Constraints
```text
1 <= nums.length <= 10^4
0 <= nums[i] <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A bridge built as you walk
You lay a bridge as you walk: standing on plank i you can extend it to i plus its power. As long as your next step is still on the bridge you go on. Step beyond it and you fall.

| In the story | In the solution |
|---|---|
| bridge | furthest |
| fall | i beyond |

### Pattern signal
**Greedy reach.** If an index is reachable so is everything before it, so one number, the furthest reach, describes all reachable positions.

### Thought process
1. **First idea:** Explore every jump length from every index, which is exponential without memory.
2. **The insight:** If an index is reachable so is everything before it, so one number, the furthest reach, describes all reachable positions.

### Optimal approach
1. Start the reach at zero.
2. Visit indices in order.
3. Fail at an index beyond the reach.
4. Extend the reach with index plus its value.

### Dry run
```text
3,2,1,0,4
i   furthest
0-3 3
4   beyond, false
```

### Why it works
All indices up to the reach can be landed on, and none beyond can until one of those extends it. An index beyond it is therefore a proof of failure.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean canJump(int[] nums) {
        int furthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > furthest) return false;
            furthest = Math.max(furthest, i + nums[i]);
        }
        return true;
    }
}
```

### Code walkthrough
- `i > furthest`: This index was never covered by any jump.
- `i + nums[i]`: The furthest landing possible from this plank.
- `return true`: Every index, including the last, was covered.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **The longest jump is not forced.** Fix: track reach only.
2. **A zero can be jumped over.** Fix: fail only beyond the reach.
3. **A single index is already the goal.** Fix: return true.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[0]` | `true` |
| Blocked | `[0,1]` | `false` |
| Over | `[2,0,0]` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[280 — 45. Jump Game II]]: Layers. The key idea there: The indices reachable in k jumps form one interval, so BFS layers need no queue.
- **1306. Both ways**: BFS.

### In a real interview
Say it in this order:

1. **Brute force:** Explore every jump length from every index, which is exponential without memory.
2. **Insight:** If an index is reachable so is everything before it, so one number, the furthest reach, describes all reachable positions.
3. **Approach in a few words:** Furthest reach.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 55. Jump Game: which pattern, and what is the insight?
A: Greedy reach. If an index is reachable so is everything before it, so one number, the furthest reach, describes all reachable positions.

Q: 55. Jump Game: what is the analogy, and what does it stand for?
A: A bridge built as you walk: bridge → furthest; fall → i beyond.

Q: 55. Jump Game: When is Jump Game false?
A: An index exceeds furthest reach.

Q: 55. Jump Game: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 280 — 45. Jump Game II | Medium · Implicit BFS | MUST
# 280 — 45. Jump Game II

**Problem 280 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Implicit BFS**

[Open on LeetCode](https://leetcode.com/problems/jump-game-ii/)

## 1. Problem Statement
You start at index 0 and `nums[i]` is the furthest you may jump forward from i. The last index is guaranteed reachable. Return the fewest jumps that reach it.

### Examples
```text
Input:  nums = [2,3,1,1,4]
Output: 2

Input:  nums = [2,3,0,1,4]
Output: 2
```

### Constraints
```text
1 <= nums.length <= 10^4
0 <= nums[i] <= 1000
It's guaranteed that you can reach nums[n - 1].
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Waves on a beach
Waves roll up a beach. Everything the current wave covers can launch the next one, and the next reaches as far as the strongest launch. You only count waves.

| In the story | In the solution |
|---|---|
| wave | end |
| next | furthest |

### Pattern signal
**Implicit BFS.** The indices reachable in k jumps form one interval, so BFS layers need no queue.

### Thought process
1. **First idea:** For each index compute the fewest jumps from all earlier ones, in O(n²).
2. **The insight:** The indices reachable in k jumps form one interval, so BFS layers need no queue.

### Optimal approach
1. Scan up to the second last index.
2. Maintain the furthest landing.
3. At the end of the current interval count a jump.
4. Make the furthest landing the new end.

### Dry run
```text
2,3,1,1,4
i  end  furthest
0  2    2, jump1
2  4    4, jump2
```

### Why it works
After k increments the end is the furthest index reachable with k jumps, exactly as in breadth-first search.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int jump(int[] nums) {
        int jumps = 0, end = 0, furthest = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            furthest = Math.max(furthest, i + nums[i]);
            if (i == end) {
                jumps++;
                end = furthest;
            }
        }
        return jumps;
    }
}
```

### Code walkthrough
- `nums.length - 1`: The last index never needs to launch a jump.
- `i == end`: The current wave has been fully examined.
- `end = furthest`: The next wave covers up to the furthest landing.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Processing the last index adds a jump.** Fix: stop before it.
2. **The longest jump is not always best.** Fix: track the next furthest.
3. **Counting every improvement overcounts.** Fix: count layer ends.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[0]` | `0` |
| Two | `[1,0]` | `1` |
| Three | `[2,1,0]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[279 — 55. Jump Game]]: Reach. The key idea there: If an index is reachable so is everything before it, so one number, the furthest reach, describes all reachable positions.
- [[210 — 994. Rotting Oranges]]: Queue. The key idea there: BFS started from all sources at once measures the distance to the nearest source.

### In a real interview
Say it in this order:

1. **Brute force:** For each index compute the fewest jumps from all earlier ones, in O(n²).
2. **Insight:** The indices reachable in k jumps form one interval, so BFS layers need no queue.
3. **Approach in a few words:** Implicit interval BFS.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 45. Jump Game II: which pattern, and what is the insight?
A: Implicit BFS. The indices reachable in k jumps form one interval, so BFS layers need no queue.

Q: 45. Jump Game II: what is the analogy, and what does it stand for?
A: Waves on a beach: wave → end; next → furthest.

Q: 45. Jump Game II: When is a jump counted?
A: At the current layer's end.

Q: 45. Jump Game II: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 281 — 134. Gas Station | Medium · Reset start | MUST
# 281 — 134. Gas Station

**Problem 281 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Reset start**

[Open on LeetCode](https://leetcode.com/problems/gas-station/)

## 1. Problem Statement
Stations form a circle. Station i gives `gas[i]` and travelling to the next costs `cost[i]`. Starting with an empty tank, return the unique index from which you can complete one clockwise lap, or -1.

### Examples
```text
Input:  gas = [1,2,3,4,5], cost = [3,4,5,1,2]
Output: 3

Input:  gas = [2,3,4], cost = [3,4,3]
Output: -1
```

### Constraints
```text
n == gas.length == cost.length
1 <= n <= 10^5
0 <= gas[i], cost[i] <= 10^4
The input is generated such that the answer is unique.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A road trip restarting after empty
On a road trip you run dry between stations. Anyone starting at a station after your start had even less fuel on arrival here, because you reached them with some to spare. So restart just past the failure.

| In the story | In the solution |
|---|---|
| dry | tank&lt;0 |
| restart | i+1 |

### Pattern signal
**Reset start.** A failed stretch rules out every start inside it, and a non-negative total guarantees some start works.

### Thought process
1. **First idea:** Simulate a lap from every station, in O(n²).
2. **The insight:** A failed stretch rules out every start inside it, and a non-negative total guarantees some start works.

### Optimal approach
1. Add each station's net gain to the total and the tank.
2. On a negative tank restart after this station and empty the tank.
3. Return the start only if the total is non-negative.

### Dry run
```text
gas = [1,2,3,4,5], cost = [3,4,5,1,2]
i  net  tank     total  start
0  -2   -2 to 0  -2     1
1  -2   -2 to 0  -4     2
2  -2   -2 to 0  -6     3
3  3    3        -3     3
4  3    6        0      3, return it
```

### Why it works
Each skipped start would have failed at the same station. With enough fuel overall, the stretch after the last failure has the surplus that covers everything before it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0, tank = 0, start = 0;
        for (int i = 0; i < gas.length; i++) {
            int net = gas[i] - cost[i];
            total += net;
            tank += net;
            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }
        return total < 0 ? -1 : start;
    }
}
```

### Code walkthrough
- `total += net`: Decides whether any lap is possible at all.
- `start = i + 1`: None of the stations up to here can be the start.
- `tank = 0`: The new trip begins with an empty tank.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **The richest station is not always the start.** Fix: reset on failure.
2. **A start without enough total gas is false.** Fix: check the total.
3. **Retrying the next station is quadratic.** Fix: skip the failed block.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5],[4]` | `0` |
| None | `[1],[2]` | `-1` |
| Last | `[1,3],[2,1]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[277 — 53. Maximum Subarray]]: Reset. The key idea there: The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.
- [[279 — 55. Jump Game]]: Fuel. The key idea there: If an index is reachable so is everything before it, so one number, the furthest reach, describes all reachable positions.

### In a real interview
Say it in this order:

1. **Brute force:** Simulate a lap from every station, in O(n²).
2. **Insight:** A failed stretch rules out every start inside it, and a non-negative total guarantees some start works.
3. **Approach in a few words:** Failed blocks plus total.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 134. Gas Station: which pattern, and what is the insight?
A: Reset start. A failed stretch rules out every start inside it, and a non-negative total guarantees some start works.

Q: 134. Gas Station: what is the analogy, and what does it stand for?
A: A road trip restarting after empty: dry → tank&lt;0; restart → i+1.

Q: 134. Gas Station: Where restart after empty?
A: The station after failure.

Q: 134. Gas Station: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 282 — 846. Hand of Straights | Medium · Sorted counts | MUST
# 282 — 846. Hand of Straights

**Problem 282 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Sorted counts**

[Open on LeetCode](https://leetcode.com/problems/hand-of-straights/)

## 1. Problem Statement
You get card values and `groupSize`. Return true if all cards can be rearranged into groups of exactly that size, each made of consecutive values.

### Examples
```text
Input:  hand = [1,2,3,6,2,3,4,7,8], groupSize = 3
Output: true

Input:  hand = [1,2,3,4,5], groupSize = 4
Output: false
```

### Constraints
```text
1 <= hand.length <= 10^4
0 <= hand[i] <= 10^9
1 <= groupSize <= hand.length
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The smallest card has no choice
In a hand of cards the smallest has no choice: nothing lower exists, so it must lead its straight. Pull out its followers, and whatever is smallest then is forced in the same way.

| In the story | In the solution |
|---|---|
| smallest | tree first key |
| pull | decrement |

### Pattern signal
**Sorted counts.** Always starting from the minimum turns a search into a series of forced moves.

### Thought process
1. **First idea:** Try every grouping of the cards.
2. **The insight:** Always starting from the minimum turns a search into a series of forced moves.

### Optimal approach
1. Check that the length divides.
2. Count cards in a sorted map.
3. Take the smallest key.
4. Consume one of each following value.
5. Fail if one is absent.

### Dry run
```text
hand = [1,2,2,3,3,4], groupSize = 3
smallest  straight  counts left
-         -         1:1  2:2  3:2  4:1
1         1,2,3     2:1  3:1  4:1
2         2,3,4     none, return true
```

### Why it works
The minimum can only be a group's first card, so its group is the same in every valid answer. Removing it leaves a smaller instance of the same problem.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isNStraightHand(int[] hand, int groupSize) {
        if (hand.length % groupSize != 0) return false;
        TreeMap<Integer, Integer> count = new TreeMap<>();
        for (int card : hand) count.merge(card, 1, Integer::sum);
        while (!count.isEmpty()) {
            int first = count.firstKey();
            for (int card = first; card < first + groupSize; card++) {
                Integer left = count.get(card);
                if (left == null) return false;
                if (left == 1) count.remove(card);
                else count.put(card, left - 1);
            }
        }
        return true;
    }
}
```

### Code walkthrough
- `% groupSize`: The cards must divide evenly into groups.
- `count.firstKey()`: The smallest card still in the hand.
- `left == null`: A needed follower is absent, so the straight is broken.

### Complexity
- **Time:** O(n log n)
- **Space:** O(n)

### Common mistakes
1. **Starting in the middle strands small cards.** Fix: use the minimum.
2. **Zero counts look present.** Fix: remove them.
3. **Chunks of the sorted list fail with duplicates.** Fix: consume counts.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Single | `[1,5],1` | `true` |
| Gap | `[1,3],2` | `false` |
| Size | `[1,2,3],2` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[021 — 128. Longest Consecutive Sequence]]: One. The key idea there: Counting from a middle repeats part of a longer count, so do it only at starts.
- **659. Variable**: More.

### In a real interview
Say it in this order:

1. **Brute force:** Try every grouping of the cards.
2. **Insight:** Always starting from the minimum turns a search into a series of forced moves.
3. **Approach in a few words:** Forced minimum.
4. **Cost:** time O(n log n); space O(n).

## 5. Recall
Q: 846. Hand of Straights: which pattern, and what is the insight?
A: Sorted counts. Always starting from the minimum turns a search into a series of forced moves.

Q: 846. Hand of Straights: what is the analogy, and what does it stand for?
A: The smallest card has no choice: smallest → tree first key; pull → decrement.

Q: 846. Hand of Straights: Which card starts each straight?
A: The smallest remaining.

Q: 846. Hand of Straights: what are the time and space costs?
A: Time O(n log n). Space O(n).

@@ 3 | TOPIC | Practice — Review 47 | Re-solve without looking | MUST
# Review 47

Solve on a blank page. No notes, hints or solutions.

## 846. Hand of Straights
Split cards into consecutive groups.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/hand-of-straights/)

## 55. Jump Game
Decide whether the last index is reachable.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/jump-game/)

## 9. Palindrome Number
Decide whether an integer is a palindrome.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/palindrome-number/)

## 1856. Maximum Subarray Min-Product
Maximize subarray minimum times sum.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/maximum-subarray-min-product/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 846 | | | | |
| 55 | | | | |
| 9 | | | | |
| 1856 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[282 — 846. Hand of Straights]]
- [[279 — 55. Jump Game]]
- [[268 — 9. Palindrome Number]]
- [[244 — 1856. Maximum Subarray Min-Product]]

@@ 3 | TOPIC | 283 — 1899. Merge Triplets to Form Target Triplet | Medium · Safe elements | MUST
# 283 — 1899. Merge Triplets to Form Target Triplet

**Problem 283 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Safe elements**

[Open on LeetCode](https://leetcode.com/problems/merge-triplets-to-form-target-triplet/)

## 1. Problem Statement
One operation picks two triplets and replaces the second by their componentwise maxima. Any number of operations is allowed. Return true if the target triplet can appear as an element.

### Examples
```text
Input:  triplets = [[2,5,3],[1,8,4],[1,7,5]], target = [2,7,5]
Output: true

Input:  triplets = [[3,4,5],[4,5,6]], target = [3,2,5]
Output: false

Input:  triplets = [[2,5,3],[2,3,4],[1,2,5],[5,2,3]], target = [5,5,5]
Output: true
```

### Constraints
```text
1 <= triplets.length <= 10^5
triplets[i].length == target.length == 3
1 <= ai, bi, ci, x, y, z <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Mixing paint that only darkens
Mixing paint here can only make each channel darker. A pot already darker than the target in any channel can never be used. Mix all the others, which can never overshoot, and see if each channel hit its shade.

| In the story | In the solution |
|---|---|
| poison | exceeds |
| found | match flags |

### Pattern signal
**Safe elements.** Maximum never decreases, so unsafe triplets are useless and all safe ones can be merged without harm.

### Thought process
1. **First idea:** Try different merge orders and subsets.
2. **The insight:** Maximum never decreases, so unsafe triplets are useless and all safe ones can be merged without harm.

### Optimal approach
1. Skip a triplet exceeding any target value.
2. Mark each coordinate where a safe one equals the target.
3. Require all three marks.

### Dry run
```text
target = [2,7,5]
triplet  safe?          matches target in
[2,5,3]  yes            first
[1,8,4]  no, 8 above 7  skip
[1,7,5]  yes            second and third
all three supplied, true
```

### Why it works
An unsafe triplet spoils its coordinate permanently. Merging every safe one gives the target exactly when each coordinate's value is supplied by some safe triplet.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean mergeTriplets(int[][] triplets, int[] target) {
        boolean[] found = new boolean[3];
        for (int[] t : triplets) {
            if (t[0] > target[0] || t[1] > target[1] || t[2] > target[2]) continue;
            for (int i = 0; i < 3; i++) found[i] |= t[i] == target[i];
        }
        return found[0] && found[1] && found[2];
    }
}
```

### Code walkthrough
- `continue`: This pot is too dark to ever use.
- `found[i] |=`: Record which channels this safe triplet supplies.
- `found[0] &&`: All three channels must be supplied.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **An unsafe triplet ruins a coordinate forever.** Fix: skip it.
2. **One triplet need not match all.** Fix: combine matches.
3. **Merge order is irrelevant.** Fix: do not search it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Exact | `[ [1,2,3] ], target [1,2,3]` | `true` |
| Too big | `[ [2,2,3] ], target [1,2,3]` | `false` |
| Combine | `[ [1,0,0],[0,1,1] ], target [1,1,1]` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[288 — 56. Merge Intervals]]: Intervals. The key idea there: After sorting by start only the last merged block can overlap the next interval.
- [[279 — 55. Jump Game]]: Reach. The key idea there: If an index is reachable so is everything before it, so one number, the furthest reach, describes all reachable positions.

### In a real interview
Say it in this order:

1. **Brute force:** Try different merge orders and subsets.
2. **Insight:** Maximum never decreases, so unsafe triplets are useless and all safe ones can be merged without harm.
3. **Approach in a few words:** Filter and cover.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 1899. Merge Triplets to Form Target Triplet: which pattern, and what is the insight?
A: Safe elements. Maximum never decreases, so unsafe triplets are useless and all safe ones can be merged without harm.

Q: 1899. Merge Triplets to Form Target Triplet: what is the analogy, and what does it stand for?
A: Mixing paint that only darkens: poison → exceeds; found → match flags.

Q: 1899. Merge Triplets to Form Target Triplet: When is a triplet unusable?
A: Any value exceeds target.

Q: 1899. Merge Triplets to Form Target Triplet: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 284 — 763. Partition Labels | Medium · Last occurrence | MUST
# 284 — 763. Partition Labels

**Problem 284 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Last occurrence**

[Open on LeetCode](https://leetcode.com/problems/partition-labels/)

## 1. Problem Statement
Split the string into as many contiguous parts as possible so that each letter appears in at most one part. Return the parts' lengths in order.

### Examples
```text
Input:  s = "ababcbacadefegdehijhklij"
Output: [9,7,8]

Input:  s = "eccbbbbdec"
Output: [10]
```

### Constraints
```text
1 <= s.length <= 500
s consists of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A tour waiting for every family's last member
A tour bus cannot leave until the last member of every family on board has arrived. A new family boarding may push the departure later. The moment you reach that departure, the bus leaves.

| In the story | In the solution |
|---|---|
| last | array |
| departure | end |

### Pattern signal
**Last occurrence.** A cut is safe exactly where every letter seen so far has had its last occurrence.

### Thought process
1. **First idea:** Test every set of cuts for shared letters.
2. **The insight:** A cut is safe exactly where every letter seen so far has had its last occurrence.

### Optimal approach
1. Record each letter's last index.
2. Scan while extending the part's end to that index.
3. When the scan reaches the end record the size.
4. Start the next part.

### Dry run
```text
s = "ababcc", last a:2 b:3 c:5
i  letter  end  action
0  a       2    wait
1  b       3    wait
2  a       3    wait
3  b       3    cut, size 4
4  c       5    wait
5  c       5    cut, size 2
```

### Why it works
No earlier cut is legal because some letter would cross it. Cutting at the first legal place each time gives the most parts.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;
        List<Integer> sizes = new ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']);
            if (i == end) {
                sizes.add(end - start + 1);
                start = i + 1;
            }
        }
        return sizes;
    }
}
```

### Code walkthrough
- `last[s.charAt(i) - 'a'] = i`: Overwriting leaves the last position of each letter.
- `Math.max`: A newly seen family can delay departure.
- `i == end`: Everyone on board is complete, so cut here.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **The first letter's last copy is not enough.** Fix: extend for every letter seen.
2. **The size is not the end index.** Fix: subtract the start.
3. **Repeats move the last index.** Fix: overwrite it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `"a"` | `[1]` |
| Distinct | `"ab"` | `[1,1]` |
| Same | `"aba"` | `[3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[288 — 56. Merge Intervals]]: Merge. The key idea there: After sorting by start only the last merged block can overlap the next interval.
- [[280 — 45. Jump Game II]]: Scan. The key idea there: The indices reachable in k jumps form one interval, so BFS layers need no queue.

### In a real interview
Say it in this order:

1. **Brute force:** Test every set of cuts for shared letters.
2. **Insight:** A cut is safe exactly where every letter seen so far has had its last occurrence.
3. **Approach in a few words:** Last occurrences.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 763. Partition Labels: which pattern, and what is the insight?
A: Last occurrence. A cut is safe exactly where every letter seen so far has had its last occurrence.

Q: 763. Partition Labels: what is the analogy, and what does it stand for?
A: A tour waiting for every family's last member: last → array; departure → end.

Q: 763. Partition Labels: When is a partition cut?
A: When i reaches the furthest last occurrence.

Q: 763. Partition Labels: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 285 — 678. Valid Parenthesis String | Medium · Two index stacks | MUST
# 285 — 678. Valid Parenthesis String

**Problem 285 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Two index stacks**

[Open on LeetCode](https://leetcode.com/problems/valid-parenthesis-string/)

## 1. Problem Statement
A string contains (, ) and *. A star may act as an opener, a closer or an empty string. Return true if it can be a correctly nested bracket string.

### Examples
```text
Input:  s = "()"
Output: true

Input:  s = "(*)"
Output: true

Input:  s = "(*))"
Output: true

Input:  s = "("
Output: false
```

### Constraints
```text
1 <= s.length <= 100
s[i] is '(', ')' or '*'.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Understudies standing by
Real openers are actors and stars are understudies who can play anything. A closer takes a real opener first, keeping understudies free. At the end an unmatched opener needs an understudy standing to its right to close it.

| In the story | In the solution |
|---|---|
| understudy | star stack |
| right | larger index |

### Pattern signal
**Two index stacks.** Save the flexible stars as long as possible and store positions so the order can be checked afterwards.

### Thought process
1. **First idea:** Try three meanings for each star, 3^n combinations.
2. **The insight:** Save the flexible stars as long as possible and store positions so the order can be checked afterwards.

### Optimal approach
1. Push indices of openers and stars.
2. Match a closer with an opener, else a star.
3. Fail if neither exists.
4. Pair leftover openers with later stars.

### Dry run
```text
(*))
char  open  star
(     0     -
*     0     1
)     -     1
)     -     -
```

### Why it works
Using a real opener never loses, because a star can do all it can and more. Pairing the latest opener with the latest star detects any star on the wrong side.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean checkValidString(String s) {
        Deque<Integer> open = new ArrayDeque<>(), star = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') open.push(i);
            else if (c == '*') star.push(i);
            else if (!open.isEmpty()) open.pop();
            else if (!star.isEmpty()) star.pop();
            else return false;
        }
        while (!open.isEmpty() && !star.isEmpty()) {
            if (open.pop() > star.pop()) return false;
        }
        return open.isEmpty();
    }
}
```

### Code walkthrough
- `open.push(i)`: The position is needed for the final order check.
- `else if (!star.isEmpty())`: Only when no real opener is available.
- `open.pop() > star.pop()`: The star is left of the opener and cannot close it.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Counts alone miss the order.** Fix: store indices.
2. **Spending stars first wastes them.** Fix: use real openers first.
3. **Leftover openers are not yet failures.** Fix: pair them with later stars.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Star | `"*"` | `true` |
| Wrong | `"*("` | `false` |
| Right | `"(*"` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[068 — 20. Valid Parentheses]]: One. The key idea there: Only the newest unclosed opener limits the next closer.
- [[076 — 22. Generate Parentheses]]: Rules. The key idea there: Validity of a prefix depends on two counts, so invalid branches are never started.

### In a real interview
Say it in this order:

1. **Brute force:** Try three meanings for each star, 3^n combinations.
2. **Insight:** Save the flexible stars as long as possible and store positions so the order can be checked afterwards.
3. **Approach in a few words:** Two index stacks.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 678. Valid Parenthesis String: which pattern, and what is the insight?
A: Two index stacks. Save the flexible stars as long as possible and store positions so the order can be checked afterwards.

Q: 678. Valid Parenthesis String: what is the analogy, and what does it stand for?
A: Understudies standing by: understudy → star stack; right → larger index.

Q: 678. Valid Parenthesis String: Where must a star closing an opener be?
A: To its right.

Q: 678. Valid Parenthesis String: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 286 — 1029. Two City Scheduling | Medium · Sort by regret | SHOULD
# 286 — 1029. Two City Scheduling

**Problem 286 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Sort by regret**

[Open on LeetCode](https://leetcode.com/problems/two-city-scheduling/)

## 1. Problem Statement
A company interviews 2n people. Person i costs a_i to fly to city A or b_i to city B. Exactly n must go to each city. Return the least total cost.

### Examples
```text
Input:  costs = [[10,20],[30,200],[400,50],[30,20]]
Output: 110

Input:  costs = [[259,770],[448,54],[926,667],[184,139],[840,118],[577,469]]
Output: 1859

Input:  costs = [[515,563],[451,713],[537,709],[343,819],[855,779],[457,60],[650,359],[631,42]]
Output: 3086
```

### Constraints
```text
2 * n == costs.length
2 <= costs.length <= 100
costs.length is even.
1 <= aCosti, bCosti <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Who would regret A least
Pretend everyone flies to B. Moving a person to A changes the bill by a minus b, their regret. You must move exactly n, so move the n with the smallest values.

| In the story | In the solution |
|---|---|
| regret | a-b |
| refund | first half |

### Pattern signal
**Sort by regret.** The total is a constant plus the sum of a minus b over those sent to A, so only that difference matters.

### Thought process
1. **First idea:** Try all ways of choosing who flies to A.
2. **The insight:** The total is a constant plus the sum of a minus b over those sent to A, so only that difference matters.

### Optimal approach
1. Sort by cost A minus cost B.
2. Send the first half to A.
3. Send the rest to B.
4. Add up.

### Dry run
```text
costs = [10,20] [30,200] [400,50] [30,20]
sorted by a - b  difference  city  pays  total
[30,200]         -170        A     30    30
[10,20]          -10         A     10    40
[30,20]          10          B     20    60
[400,50]         350         B     50    110
```

### Why it works
Choosing the n smallest differences minimizes their sum, and everything else in the total does not depend on the choice.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int twoCitySchedCost(int[][] costs) {
        Arrays.sort(costs, (x, y) -> (x[0] - x[1]) - (y[0] - y[1]));
        int total = 0, half = costs.length / 2;
        for (int i = 0; i < costs.length; i++) total += i < half ? costs[i][0] : costs[i][1];
        return total;
    }
}
```

### Code walkthrough
- `x[0] - x[1]`: How much more A costs than B for this person.
- `i < half`: The half who gain most from A.
- `costs[i][1]`: Everyone else keeps city B.

### Complexity
- **Time:** O(n log n)
- **Space:** O(log n) sort stack

### Common mistakes
1. **Each person's cheaper city breaks the balance.** Fix: select n.
2. **Sorting by one city ignores the other.** Fix: sort by difference.
3. **An absolute difference loses direction.** Fix: keep the sign.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Opposite | `[ [1,9],[9,1] ]` | `2` |
| Both prefer A | `[ [1,5],[1,9] ]` | `6` |
| Equal | `[ [2,2],[2,2] ]` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[028 — 179. Largest Number]]: Exchange. The key idea there: The best order of two neighbors does not depend on the others, so it can be a comparator.
- [[050 — 881. Boats to Save People]]: Sort. The key idea there: The lightest is the best possible companion for the heaviest, and giving that seat away never hurts.

### In a real interview
Say it in this order:

1. **Brute force:** Try all ways of choosing who flies to A.
2. **Insight:** The total is a constant plus the sum of a minus b over those sent to A, so only that difference matters.
3. **Approach in a few words:** Sort by difference.
4. **Cost:** time O(n log n); space O(log n) sort stack.

## 5. Recall
Q: 1029. Two City Scheduling: which pattern, and what is the insight?
A: Sort by regret. The total is a constant plus the sum of a minus b over those sent to A, so only that difference matters.

Q: 1029. Two City Scheduling: what is the analogy, and what does it stand for?
A: Who would regret A least: regret → a-b; refund → first half.

Q: 1029. Two City Scheduling: What key sorts people?
A: Cost A minus cost B.

Q: 1029. Two City Scheduling: what are the time and space costs?
A: Time O(n log n). Space O(log n) sort stack.

@@ 3 | TOPIC | 287 — 57. Insert Interval | Medium · Three zones | MUST
# 287 — 57. Insert Interval

**Problem 287 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Three zones**

[Open on LeetCode](https://leetcode.com/problems/insert-interval/)

## 1. Problem Statement
You get non-overlapping intervals sorted by start and one new interval. Insert it, merging every overlap, touching endpoints included, so that the result is still sorted and non-overlapping.

### Examples
```text
Input:  intervals = [[1,3],[6,9]], newInterval = [2,5]
Output: [[1,5],[6,9]]

Input:  intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
Output: [[1,2],[3,10],[12,16]]
```

### Constraints
```text
0 <= intervals.length <= 10^4
intervals[i].length == 2
0 <= starti <= endi <= 10^5
intervals is sorted by starti in ascending order.
newInterval.length == 2
0 <= start <= end <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A booking in a calendar
A new booking lands in a tidy calendar. Copy what finishes before it, melt everything it touches into one long booking, then copy the rest unchanged.

| In the story | In the solution |
|---|---|
| melt | min and max |
| zones | loops |

### Pattern signal
**Three zones.** Because the list is sorted, the overlapping intervals are one consecutive block.

### Thought process
1. **First idea:** Append it, sort and merge everything, in O(n log n).
2. **The insight:** Because the list is sorted, the overlapping intervals are one consecutive block.

### Optimal approach
1. Copy intervals ending before the new start.
2. While starts are within the new end widen it.
3. Append the merged one.
4. Copy the rest.

### Dry run
```text
intervals = [1,3] [6,9], insert [2,5]
interval  zone         merged block  result
[1,3]     overlaps     [1,5]         -
end zone  -            append        [1,5]
[6,9]     after        -             [1,5] [6,9]
```

### Why it works
The three loops split the sorted list into before, overlapping and after. Widening covers exactly the union of the middle block.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0, n = intervals.length;
        int start = newInterval[0], end = newInterval[1];
        while (i < n && intervals[i][1] < start) result.add(intervals[i++]);
        while (i < n && intervals[i][0] <= end) {
            start = Math.min(start, intervals[i][0]);
            end = Math.max(end, intervals[i][1]);
            i++;
        }
        result.add(new int[] {start, end});
        while (i < n) result.add(intervals[i++]);
        return result.toArray(new int[0][]);
    }
}
```

### Code walkthrough
- `intervals[i][1] < start`: This booking finishes before the new one starts.
- `<= end`: Touching counts as overlapping here.
- `new int[] {start, end}`: The melted block is added exactly once.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Touching intervals merge.** Fix: compare with at most.
2. **The merged end grows.** Fix: compare with the updated one.
3. **An empty list is valid.** Fix: always add the new interval.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], new [1,2]` | `[ [1,2] ]` |
| Touch | `[ [1,2] ], new [2,3]` | `[ [1,3] ]` |
| Inside | `[ [1,9] ], new [2,3]` | `[ [1,9] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[288 — 56. Merge Intervals]]: Sort. The key idea there: After sorting by start only the last merged block can overlap the next interval.
- [[289 — 435. Non-overlapping Intervals]]: Greedy. The key idea there: Minimizing removals is maximizing a compatible set, which earliest end solves.

### In a real interview
Say it in this order:

1. **Brute force:** Append it, sort and merge everything, in O(n log n).
2. **Insight:** Because the list is sorted, the overlapping intervals are one consecutive block.
3. **Approach in a few words:** Three zones.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 57. Insert Interval: which pattern, and what is the insight?
A: Three zones. Because the list is sorted, the overlapping intervals are one consecutive block.

Q: 57. Insert Interval: what is the analogy, and what does it stand for?
A: A booking in a calendar: melt → min and max; zones → loops.

Q: 57. Insert Interval: What are insert's zones?
A: Before, overlapping, after.

Q: 57. Insert Interval: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 288 — 56. Merge Intervals | Medium · Sort and merge | MUST
# 288 — 56. Merge Intervals

**Problem 288 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Sort and merge**

[Open on LeetCode](https://leetcode.com/problems/merge-intervals/)

## 1. Problem Statement
You get intervals in any order. Merge all that overlap, touching endpoints included, and return non-overlapping intervals covering the same points.

### Examples
```text
Input:  intervals = [[1,3],[2,6],[8,10],[15,18]]
Output: [[1,6],[8,10],[15,18]]

Input:  intervals = [[1,4],[4,5]]
Output: [[1,5]]

Input:  intervals = [[4,7],[1,4]]
Output: [[1,7]]
```

### Constraints
```text
1 <= intervals.length <= 10^4
intervals[i].length == 2
0 <= starti <= endi <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Painting bookings on a timeline
Paint bookings on a timeline in order of start. Each stroke either touches the block you painted last and lengthens it, or leaves a gap and starts a new block.

| In the story | In the solution |
|---|---|
| stroke | interval |
| last | list end |

### Pattern signal
**Sort and merge.** After sorting by start only the last merged block can overlap the next interval.

### Thought process
1. **First idea:** Compare every pair repeatedly until nothing merges.
2. **The insight:** After sorting by start only the last merged block can overlap the next interval.

### Optimal approach
1. Sort by start.
2. Append an interval after a gap.
3. Otherwise set the last end to the larger end.

### Dry run
```text
intervals = [1,3] [2,6] [8,10]
next    last block  action      result
[1,3]   none        append      [1,3]
[2,6]   [1,3]       2 <= 3, extend  [1,6]
[8,10]  [1,6]       8 > 6, append   [1,6] [8,10]
```

### Why it works
Earlier blocks end before the last one starts, and the next start is at least that, so none of them can overlap it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> result = new ArrayList<>();
        for (int[] x : intervals) {
            if (result.isEmpty() || result.get(result.size() - 1)[1] < x[0]) {
                result.add(x);
            } else {
                int[] last = result.get(result.size() - 1);
                last[1] = Math.max(last[1], x[1]);
            }
        }
        return result.toArray(new int[0][]);
    }
}
```

### Code walkthrough
- `a[0] - b[0]`: Guarantees that starts only move right.
- `< x[0]`: A strict gap starts a new block.
- `Math.max`: A nested interval must not shorten the block.

### Complexity
- **Time:** O(n log n)
- **Space:** O(n)

### Common mistakes
1. **A nested interval shrinks the end.** Fix: take the maximum.
2. **Start order is required.** Fix: sort by start.
3. **Touching endpoints merge.** Fix: append only on a strict gap.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [1,2] ]` | `[ [1,2] ]` |
| Touch | `[ [1,2],[2,3] ]` | `[ [1,3] ]` |
| Nested | `[ [1,9],[2,3] ]` | `[ [1,9] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[287 — 57. Insert Interval]]: Sorted. The key idea there: Because the list is sorted, the overlapping intervals are one consecutive block.
- **253. Rooms**: Count.

### In a real interview
Say it in this order:

1. **Brute force:** Compare every pair repeatedly until nothing merges.
2. **Insight:** After sorting by start only the last merged block can overlap the next interval.
3. **Approach in a few words:** Sort and extend.
4. **Cost:** time O(n log n); space O(n).

## 5. Recall
Q: 56. Merge Intervals: which pattern, and what is the insight?
A: Sort and merge. After sorting by start only the last merged block can overlap the next interval.

Q: 56. Merge Intervals: what is the analogy, and what does it stand for?
A: Painting bookings on a timeline: stroke → interval; last → list end.

Q: 56. Merge Intervals: What does sorting guarantee?
A: Only the last merged block can overlap.

Q: 56. Merge Intervals: what are the time and space costs?
A: Time O(n log n). Space O(n).

@@ 3 | TOPIC | Practice — Review 48 | Re-solve without looking | MUST
# Review 48

Solve on a blank page. No notes, hints or solutions.

## 56. Merge Intervals
Merge overlapping intervals.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/merge-intervals/)

## 678. Valid Parenthesis String
Validate parentheses with star wildcards.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/valid-parenthesis-string/)

## 268. Missing Number
Find the missing value in 0..n.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/missing-number/)

## 1143. Longest Common Subsequence
Find the longest common subsequence.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/longest-common-subsequence/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 56 | | | | |
| 678 | | | | |
| 268 | | | | |
| 1143 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[288 — 56. Merge Intervals]]
- [[285 — 678. Valid Parenthesis String]]
- [[274 — 268. Missing Number]]
- [[250 — 1143. Longest Common Subsequence]]

@@ 3 | TOPIC | 289 — 435. Non-overlapping Intervals | Medium · Interval greedy | MUST
# 289 — 435. Non-overlapping Intervals

**Problem 289 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Interval greedy**

[Open on LeetCode](https://leetcode.com/problems/non-overlapping-intervals/)

## 1. Problem Statement
You get intervals. Return the fewest to remove so that the rest do not overlap. Intervals that only touch at an endpoint do not overlap.

### Examples
```text
Input:  intervals = [[1,2],[2,3],[3,4],[1,3]]
Output: 1

Input:  intervals = [[1,2],[1,2],[1,2]]
Output: 2

Input:  intervals = [[1,2],[2,3]]
Output: 0
```

### Constraints
```text
1 <= intervals.length <= 10^5
intervals[i].length == 2
-5 * 10^4 <= starti < endi <= 5 * 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Most films in one day
To see the most films in a day always pick the next one that finishes first, since it leaves the most evening for the others. The films you skip are the removals.

| In the story | In the solution |
|---|---|
| earliest end | sort |
| skipped | removals |

### Pattern signal
**Interval greedy.** Minimizing removals is maximizing a compatible set, which earliest end solves.

### Thought process
1. **First idea:** Try every subset of intervals.
2. **The insight:** Minimizing removals is maximizing a compatible set, which earliest end solves.

### Optimal approach
1. Sort by end.
2. Keep an interval starting at or after the last kept end.
3. Otherwise count a removal.

### Dry run
```text
sorted by end = [1,2] [2,3] [1,3] [3,4]
interval  previous end  action   removed
[1,2]     none          keep     0
[2,3]     2             keep     0
[1,3]     3             remove   1
[3,4]     3             keep     1
```

### Why it works
Any optimal set's first interval can be exchanged for the earliest-ending one without conflict, and the argument repeats on the rest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
        int end = Integer.MIN_VALUE, removed = 0;
        for (int[] x : intervals) {
            if (x[0] >= end) end = x[1];
            else removed++;
        }
        return removed;
    }
}
```

### Code walkthrough
- `a[1], b[1]`: Earliest finish comes first.
- `x[0] >= end`: It starts after the previous one has finished.
- `removed++`: An overlapping film is skipped.

### Complexity
- **Time:** O(n log n)
- **Space:** O(log n) sort stack

### Common mistakes
1. **The shortest first fails.** Fix: choose the earliest end.
2. **Touching is allowed.** Fix: use at least.
3. **Merging is not removing.** Fix: keep one and drop the other.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [1,2] ]` | `0` |
| Touch | `[ [1,2],[2,3] ]` | `0` |
| Identical | `[ [1,2],[1,2] ]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[288 — 56. Merge Intervals]]: Start. The key idea there: After sorting by start only the last merged block can overlap the next interval.
- **452. Arrows**: Same.

### In a real interview
Say it in this order:

1. **Brute force:** Try every subset of intervals.
2. **Insight:** Minimizing removals is maximizing a compatible set, which earliest end solves.
3. **Approach in a few words:** Interval scheduling.
4. **Cost:** time O(n log n); space O(log n) sort stack.

## 5. Recall
Q: 435. Non-overlapping Intervals: which pattern, and what is the insight?
A: Interval greedy. Minimizing removals is maximizing a compatible set, which earliest end solves.

Q: 435. Non-overlapping Intervals: what is the analogy, and what does it stand for?
A: Most films in one day: earliest end → sort; skipped → removals.

Q: 435. Non-overlapping Intervals: Which interval is kept?
A: The compatible one ending first.

Q: 435. Non-overlapping Intervals: what are the time and space costs?
A: Time O(n log n). Space O(log n) sort stack.

@@ 3 | TOPIC | 290 — 48. Rotate Image | Medium · In-place rotation | MUST
# 290 — 48. Rotate Image

**Problem 290 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: In-place rotation**

[Open on LeetCode](https://leetcode.com/problems/rotate-image/)

## 1. Problem Statement
You get an n by n matrix representing an image. Rotate it 90 degrees clockwise in place, without allocating another matrix.

### Examples
```text
Input:  matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [[7,4,1],[8,5,2],[9,6,3]]

Input:  matrix = [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]
Output: [[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]
```

### Constraints
```text
n == matrix.length == matrix[i].length
1 <= n <= 20
-1000 <= matrix[i][j] <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Turning tiles with two flips
Turn a square of tiles using two flips instead of lifting it: fold it over its main diagonal, then look at it in a side mirror. Together they make one clockwise quarter turn.

| In the story | In the solution |
|---|---|
| diagonal | transpose |
| mirror | reverse rows |

### Pattern signal
**In-place rotation.** A clockwise rotation equals a transpose followed by reversing each row, and both work in place.

### Thought process
1. **First idea:** Copy each cell to its new place in another matrix.
2. **The insight:** A clockwise rotation equals a transpose followed by reversing each row, and both work in place.

### Optimal approach
1. Swap each cell above the diagonal with its mirror.
2. Reverse every row with two pointers.

### Dry run
```text
start      transposed  rows reversed
1 2        1 3         3 1
3 4        2 4         4 2
```

### Why it works
Transposing sends (r,c) to (c,r) and reversing the row sends that to (c,n-1-r), the rotated position.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        for (int r = 0; r < n; r++) {
            for (int c = r + 1; c < n; c++) {
                int temp = matrix[r][c];
                matrix[r][c] = matrix[c][r];
                matrix[c][r] = temp;
            }
        }
        for (int[] row : matrix) {
            for (int left = 0, right = n - 1; left < right; left++, right--) {
                int temp = row[left];
                row[left] = row[right];
                row[right] = temp;
            }
        }
    }
}
```

### Code walkthrough
- `c = r + 1`: Visit each pair once, above the diagonal only.
- `matrix[c][r] = temp`: Completes the swap across the diagonal.
- `left < right`: The mirror step, reversing each row.

### Complexity
- **Time:** O(n²)
- **Space:** O(1)

### Common mistakes
1. **Transposing both triangles undoes it.** Fix: start above the diagonal.
2. **Reversing columns turns the other way.** Fix: reverse rows.
3. **A new matrix breaks the space rule.** Fix: swap in place.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One cell | `[ [1] ]` | `[ [1] ]` |
| Two by two | `[ [1,2],[3,4] ]` | `[ [3,1],[4,2] ]` |
| Four equal | `[ [5,5],[5,5] ]` | `[ [5,5],[5,5] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[291 — 54. Spiral Matrix]]: Read. The key idea there: Four border numbers fully describe the part not yet read.
- [[049 — 189. Rotate Array]]: Reversals. The key idea there: A rotation is just two blocks trading places, which three reversals achieve.

### In a real interview
Say it in this order:

1. **Brute force:** Copy each cell to its new place in another matrix.
2. **Insight:** A clockwise rotation equals a transpose followed by reversing each row, and both work in place.
3. **Approach in a few words:** Transpose and reverse.
4. **Cost:** time O(n²); space O(1).

## 5. Recall
Q: 48. Rotate Image: which pattern, and what is the insight?
A: In-place rotation. A clockwise rotation equals a transpose followed by reversing each row, and both work in place.

Q: 48. Rotate Image: what is the analogy, and what does it stand for?
A: Turning tiles with two flips: diagonal → transpose; mirror → reverse rows.

Q: 48. Rotate Image: Which two flips rotate?
A: Transpose then reverse each row.

Q: 48. Rotate Image: what are the time and space costs?
A: Time O(n²). Space O(1).

@@ 3 | TOPIC | 291 — 54. Spiral Matrix | Medium · Shrinking borders | MUST
# 291 — 54. Spiral Matrix

**Problem 291 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Shrinking borders**

[Open on LeetCode](https://leetcode.com/problems/spiral-matrix/)

## 1. Problem Statement
You get an m by n matrix. Return all its elements in clockwise spiral order, starting from the top-left corner.

### Examples
```text
Input:  matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,2,3,6,9,8,7,4,5]

Input:  matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
Output: [1,2,3,4,8,12,11,10,9,5,6,7]
```

### Constraints
```text
m == matrix.length
n == matrix[i].length
1 <= m, n <= 10
-100 <= matrix[i][j] <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Peeling an onion
Peel an onion one skin at a time: top, right, bottom, left. After each strip that border moves inward. The last skin may be a single line with no opposite side, so check before peeling back.

| In the story | In the solution |
|---|---|
| skin | borders |
| thin | guards |

### Pattern signal
**Shrinking borders.** Four border numbers fully describe the part not yet read.

### Thought process
1. **First idea:** Walk with a visited matrix and turn when blocked, using O(mn) space.
2. **The insight:** Four border numbers fully describe the part not yet read.

### Optimal approach
1. Read the top and lower it.
2. Read the right and move it in.
3. Read the bottom if a row remains.
4. Read the left if a column remains.

### Dry run
```text
matrix rows = 1 2 3 / 4 5 6 / 7 8 9
side     reads  border afterwards
top      1,2,3  top = 1
right    6,9    right = 1
bottom   8,7    bottom = 1
left     4      left = 1
top      5      top = 2, finished
```

### Why it works
Each strip is read once and immediately excluded by moving its border. The guards stop a single remaining line being read again.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {
        int top = 0, bottom = matrix.length - 1, left = 0, right = matrix[0].length - 1;
        List<Integer> result = new ArrayList<>();
        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) result.add(matrix[top][c]);
            top++;
            for (int r = top; r <= bottom; r++) result.add(matrix[r][right]);
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) result.add(matrix[bottom][c]);
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) result.add(matrix[r][left]);
                left++;
            }
        }
        return result;
    }
}
```

### Code walkthrough
- `top++`: This strip is finished.
- `if (top <= bottom)`: Only go back along a bottom row if one is left.
- `if (left <= right)`: Only go up a left column if one is left.

### Complexity
- **Time:** O(m·n)
- **Space:** O(1) besides the output

### Common mistakes
1. **No guards reads a line twice.** Fix: test before the bottom and left.
2. **It may not be square.** Fix: track four borders.
3. **A corner repeats.** Fix: shrink after each side.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [1] ]` | `[1]` |
| Row | `[ [1,2] ]` | `[1,2]` |
| Column | `[ [1],[2] ]` | `[1,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[292 — 59. Spiral Matrix II]]: Same. The key idea there: A square's spiral is four shrinking borders, and both directions run out together.
- [[290 — 48. Rotate Image]]: Layers. The key idea there: A clockwise rotation equals a transpose followed by reversing each row, and both work in place.

### In a real interview
Say it in this order:

1. **Brute force:** Walk with a visited matrix and turn when blocked, using O(mn) space.
2. **Insight:** Four border numbers fully describe the part not yet read.
3. **Approach in a few words:** Four borders with guards.
4. **Cost:** time O(m·n); space O(1) besides the output.

## 5. Recall
Q: 54. Spiral Matrix: which pattern, and what is the insight?
A: Shrinking borders. Four border numbers fully describe the part not yet read.

Q: 54. Spiral Matrix: what is the analogy, and what does it stand for?
A: Peeling an onion: skin → borders; thin → guards.

Q: 54. Spiral Matrix: Why guards?
A: A single row or column would be read twice.

Q: 54. Spiral Matrix: what are the time and space costs?
A: Time O(m·n). Space O(1) besides the output.

@@ 3 | TOPIC | 292 — 59. Spiral Matrix II | Medium · Shrinking borders | SHOULD
# 292 — 59. Spiral Matrix II

**Problem 292 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Shrinking borders**

[Open on LeetCode](https://leetcode.com/problems/spiral-matrix-ii/)

## 1. Problem Statement
You get a positive integer n. Build an n by n matrix filled with the numbers 1 to n squared in clockwise spiral order from the top-left.

### Examples
```text
Input:  n = 3
Output: [[1,2,3],[8,9,4],[7,6,5]]

Input:  n = 1
Output: [[1]]
```

### Constraints
```text
1 <= n <= 20
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Painting the onion
It is the same onion, but you paint numbers instead of reading them. One counter keeps the next number while the four borders close in.

| In the story | In the solution |
|---|---|
| counter | next |
| skin | borders |

### Pattern signal
**Shrinking borders.** A square's spiral is four shrinking borders, and both directions run out together.

### Thought process
1. **First idea:** Simulate a walker that checks cells and turns.
2. **The insight:** A square's spiral is four shrinking borders, and both directions run out together.

### Optimal approach
1. Start the counter at 1.
2. Fill the top.
3. Fill the right.
4. Fill the bottom backwards.
5. Fill the left upwards.
6. Shrink after each.

### Dry run
```text
n = 3
side     writes  cells
top      1,2,3   row 0
right    4,5     column 2
bottom   6,7     row 2
left     8       column 0
top      9       centre
final rows = 1 2 3 / 8 9 4 / 7 6 5
```

### Why it works
Every loop writes only cells inside the borders, which are fresh, in spiral order, until all n squared are filled.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[][] generateMatrix(int n) {
        int[][] a = new int[n][n];
        int top = 0, bottom = n - 1, left = 0, right = n - 1, next = 1;
        while (top <= bottom) {
            for (int c = left; c <= right; c++) a[top][c] = next++;
            top++;
            for (int r = top; r <= bottom; r++) a[r][right] = next++;
            right--;
            for (int c = right; c >= left; c--) a[bottom][c] = next++;
            bottom--;
            for (int r = bottom; r >= top; r--) a[r][left] = next++;
            left++;
        }
        return a;
    }
}
```

### Code walkthrough
- `next++`: The next number to paint.
- `top <= bottom`: In a square this also implies a column remains.
- `left++`: The last border of this skin moves inward.

### Complexity
- **Time:** O(n²)
- **Space:** O(n²) for the output

### Common mistakes
1. **Corners get overwritten.** Fix: shrink after each side.
2. **The values start at 1.** Fix: seed the counter there.
3. **The centre needs a value.** Fix: loop while top is at most bottom.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `[ [1] ]` |
| Two | `2` | `[ [1,2],[4,3] ]` |
| Three | `3` | `[ [1,2,3],[8,9,4],[7,6,5] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[291 — 54. Spiral Matrix]]: Guards. The key idea there: Four border numbers fully describe the part not yet read.
- [[290 — 48. Rotate Image]]: Square. The key idea there: A clockwise rotation equals a transpose followed by reversing each row, and both work in place.

### In a real interview
Say it in this order:

1. **Brute force:** Simulate a walker that checks cells and turns.
2. **Insight:** A square's spiral is four shrinking borders, and both directions run out together.
3. **Approach in a few words:** Shrinking borders.
4. **Cost:** time O(n²); space O(n²) for the output.

## 5. Recall
Q: 59. Spiral Matrix II: which pattern, and what is the insight?
A: Shrinking borders. A square's spiral is four shrinking borders, and both directions run out together.

Q: 59. Spiral Matrix II: what is the analogy, and what does it stand for?
A: Painting the onion: counter → next; skin → borders.

Q: 59. Spiral Matrix II: Why no guards in the square?
A: Both dimensions shrink together, leaving empty loops.

Q: 59. Spiral Matrix II: what are the time and space costs?
A: Time O(n²). Space O(n²) for the output.

@@ 3 | TOPIC | 293 — 73. Set Matrix Zeroes | Medium · Markers in matrix | MUST
# 293 — 73. Set Matrix Zeroes

**Problem 293 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Markers in matrix**

[Open on LeetCode](https://leetcode.com/problems/set-matrix-zeroes/)

## 1. Problem Statement
If any element of an m by n matrix is 0, set its whole row and column to 0, in place. Follow-up: constant extra space.

### Examples
```text
Input:  matrix = [[1,1,1],[1,0,1],[1,1,1]]
Output: [[1,0,1],[0,0,0],[1,0,1]]

Input:  matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
Output: [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
```

### Constraints
```text
m == matrix.length
n == matrix[0].length
1 <= m, n <= 200
-2^31 <= matrix[i][j] <= 2^31 - 1
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Notes in the margins
You may not use a notebook, so write notes in the page's margins: the first row and column. A single extra note remembers whether the top margin itself had a zero. Settle the margins last or you erase your notes.

| In the story | In the solution |
|---|---|
| margins | first row and column |
| extra | boolean |

### Pattern signal
**Markers in matrix.** The first row and column have exactly one cell for every column and row, enough to hold all the markers.

### Thought process
1. **First idea:** Record bad rows and columns in two extra arrays.
2. **The insight:** The first row and column have exactly one cell for every column and row, enough to hold all the markers.

### Optimal approach
1. Mark the margins for each zero, with a flag for row 0.
2. Zero inner cells from the marks.
3. Settle the first column.
4. Settle the first row.

### Dry run
```text
matrix rows = 1 1 1 / 1 0 1 / 1 1 1
step              rows
mark (1,1)        1 0 1 / 0 0 1 / 1 1 1
zero inner cells  1 0 1 / 0 0 0 / 1 0 1
first column      unchanged, the corner is 1
first row         unchanged, the flag is false
```

### Why it works
Inner cells are changed only from markers of original zeros. The margins are changed only after every marker has been used.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstRow = false;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (matrix[r][c] != 0) continue;
                matrix[0][c] = 0;
                if (r == 0) firstRow = true;
                else matrix[r][0] = 0;
            }
        }
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                if (matrix[r][0] == 0 || matrix[0][c] == 0) matrix[r][c] = 0;
            }
        }
        if (matrix[0][0] == 0) {
            for (int r = 0; r < m; r++) matrix[r][0] = 0;
        }
        if (firstRow) Arrays.fill(matrix[0], 0);
    }
}
```

### Code walkthrough
- `firstRow = true`: Row 0 can't share the corner with column 0.
- `r = 1`: Inner cells first, while the notes are intact.
- `Arrays.fill`: The top margin is settled last.

### Complexity
- **Time:** O(m·n)
- **Space:** O(1)

### Common mistakes
1. **Zeroing at once cascades.** Fix: mark first.
2. **The shared corner is ambiguous.** Fix: add a boolean.
3. **Zeroing margins first erases notes.** Fix: do them last.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [0] ]` | `[ [0] ]` |
| None | `[ [1] ]` | `[ [1] ]` |
| Row | `[ [1,0] ]` | `[ [0,0] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **289. Life**: Encoded.
- [[033 — 41. First Missing Positive]]: Markers. The key idea there: With n slots the answer must be within 1 to n+1, so nothing else needs storing.

### In a real interview
Say it in this order:

1. **Brute force:** Record bad rows and columns in two extra arrays.
2. **Insight:** The first row and column have exactly one cell for every column and row, enough to hold all the markers.
3. **Approach in a few words:** Border markers.
4. **Cost:** time O(m·n); space O(1).

## 5. Recall
Q: 73. Set Matrix Zeroes: which pattern, and what is the insight?
A: Markers in matrix. The first row and column have exactly one cell for every column and row, enough to hold all the markers.

Q: 73. Set Matrix Zeroes: what is the analogy, and what does it stand for?
A: Notes in the margins: margins → first row and column; extra → boolean.

Q: 73. Set Matrix Zeroes: Why an extra boolean?
A: The corner can't represent row and column both.

Q: 73. Set Matrix Zeroes: what are the time and space costs?
A: Time O(m·n). Space O(1).

@@ 3 | TOPIC | 294 — 12. Integer to Roman | Medium · Greedy symbols | SHOULD
# 294 — 12. Integer to Roman

**Problem 294 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Greedy symbols**

[Open on LeetCode](https://leetcode.com/problems/integer-to-roman/)

## 1. Problem Statement
Convert an integer from 1 to 3999 into a Roman numeral with I, V, X, L, C, D, M, using the subtractive forms IV, IX, XL, XC, CD and CM for fours and nines.

### Examples
```text
Input:  num = 3749
Output: "MMMDCCXLIX"

Input:  num = 58
Output: "LVIII"

Input:  num = 1994
Output: "MCMXCIV"
```

### Constraints
```text
1 <= num <= 3999
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A cashier with odd notes
A cashier pays with the largest note that fits, and the drawer has special notes for 900, 400, 90, 40, 9 and 4. With those in the drawer no special rules are needed.

| In the story | In the solution |
|---|---|
| note | value |
| receipt | builder |

### Pattern signal
**Greedy symbols.** Treating subtractive pairs as symbols of their own makes plain greedy produce the canonical numeral.

### Thought process
1. **First idea:** Write separate rules for every decimal place.
2. **The insight:** Treating subtractive pairs as symbols of their own makes plain greedy produce the canonical numeral.

### Optimal approach
1. List the thirteen values in descending order.
2. Append the symbol while its value fits.
3. Subtract it each time.

### Dry run
```text
1994
note  left
M     994
CM    94
XC    4
IV    0
```

### Why it works
At every place the largest fitting symbol is exactly the canonical one, and less than that place is left afterwards.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String intToRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder receipt = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                receipt.append(symbols[i]);
                num -= values[i];
            }
        }
        return receipt.toString();
    }
}
```

### Code walkthrough
- `900`: A subtractive pair is an ordinary note.
- `num >= values[i]`: A note such as M or I can repeat.
- `num -= values[i]`: That amount has been paid.

### Complexity
- **Time:** O(1)
- **Space:** O(1)

### Common mistakes
1. **IIII is not canonical.** Fix: include the subtractive symbols.
2. **Symbols can repeat.** Fix: use while.
3. **A wrong order breaks greedy.** Fix: list values descending.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `"I"` |
| Four | `4` | `"IV"` |
| Max | `3999` | `"MMMCMXCIX"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **13. Back**: Subtract.
- [[264 — 168. Excel Sheet Column Title]]: Base. The key idea there: This is base 26 with digits 1 to 26. Subtracting one at each place turns it into ordinary base 26.

### In a real interview
Say it in this order:

1. **Brute force:** Write separate rules for every decimal place.
2. **Insight:** Treating subtractive pairs as symbols of their own makes plain greedy produce the canonical numeral.
3. **Approach in a few words:** Greedy thirteen symbols.
4. **Cost:** time O(1); space O(1).

## 5. Recall
Q: 12. Integer to Roman: which pattern, and what is the insight?
A: Greedy symbols. Treating subtractive pairs as symbols of their own makes plain greedy produce the canonical numeral.

Q: 12. Integer to Roman: what is the analogy, and what does it stand for?
A: A cashier with odd notes: note → value; receipt → builder.

Q: 12. Integer to Roman: How is subtractive handled?
A: As its own symbol.

Q: 12. Integer to Roman: what are the time and space costs?
A: Time O(1). Space O(1).

@@ 3 | TOPIC | Practice — Review 49 | Re-solve without looking | MUST
# Review 49

Solve on a blank page. No notes, hints or solutions.

## 12. Integer to Roman
Convert an integer to Roman.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/integer-to-roman/)

## 54. Spiral Matrix
Return a matrix in spiral order.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/spiral-matrix/)

## 45. Jump Game II
Find fewest jumps to the end.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/jump-game-ii/)

## 72. Edit Distance
Find minimum edit operations between strings.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/edit-distance/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 12 | | | | |
| 54 | | | | |
| 45 | | | | |
| 72 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[294 — 12. Integer to Roman]]
- [[291 — 54. Spiral Matrix]]
- [[280 — 45. Jump Game II]]
- [[256 — 72. Edit Distance]]

@@ 3 | TOPIC | 295 — 50. Pow(x, n) | Medium · Binary exponentiation | MUST
# 295 — 50. Pow(x, n)

**Problem 295 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Binary exponentiation**

[Open on LeetCode](https://leetcode.com/problems/powx-n/)

## 1. Problem Statement
Implement pow(x, n): x raised to the integer power n, which may be zero, negative or the minimum int.

### Examples
```text
Input:  x = 2.00000, n = 10
Output: 1024.00000

Input:  x = 2.10000, n = 3
Output: 9.26100

Input:  x = 2.00000, n = -2
Output: 0.25000
```

### Constraints
```text
-100.0 < x < 100.0
-2^31 <= n <= 2^31-1
n is an integer.
Either x is not zero or n > 0.
-10^4 <= x^n <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Folding paper
Folding paper doubles its thickness, so a few folds give huge powers. Write the exponent in binary: keep folding, and multiply into the answer only the folds whose bit is 1.

| In the story | In the solution |
|---|---|
| fold | square |
| on | odd |

### Pattern signal
**Binary exponentiation.** Repeated squaring gives x to 1, 2, 4, 8, and any exponent is a sum of some of those.

### Thought process
1. **First idea:** Multiply x n times, about two billion steps at worst.
2. **The insight:** Repeated squaring gives x to 1, 2, 4, 8, and any exponent is a sum of some of those.

### Optimal approach
1. Widen n to long.
2. Invert x for a negative exponent.
3. Multiply the result when the power is odd.
4. Square the base.
5. Halve the power.

### Dry run
```text
2^10
power  base  result
10     2     1
5      4     4
2      16    4
1      256   1024
```

### Why it works
The result times base to the remaining power always equals the original answer. When the power reaches zero the result is that answer.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public double myPow(double x, int n) {
        long power = n;
        if (power < 0) {
            x = 1 / x;
            power = -power;
        }
        double result = 1;
        while (power > 0) {
            if (power % 2 == 1) result *= x;
            x *= x;
            power /= 2;
        }
        return result;
    }
}
```

### Code walkthrough
- `long power`: Negating the minimum int would overflow.
- `power % 2 == 1`: This bit of the exponent is used.
- `x *= x`: The next fold squares the base.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Negating MIN_VALUE overflows.** Fix: widen to long.
2. **n multiplications are too many.** Fix: square.
3. **Power zero is 1.** Fix: start the result there.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `5,0` | `1.0` |
| Negative | `2,-2` | `0.25` |
| Odd | `3,3` | `27.0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[093 — 69. Sqrt(x)]]: Inverse. The key idea there: Fits is true up to the answer and false after it.
- [[271 — 191. Number of 1 Bits]]: Walk. The key idea there: Subtracting one flips the lowest set bit and everything below it, so ANDing with n erases just that bit.

### In a real interview
Say it in this order:

1. **Brute force:** Multiply x n times, about two billion steps at worst.
2. **Insight:** Repeated squaring gives x to 1, 2, 4, 8, and any exponent is a sum of some of those.
3. **Approach in a few words:** Binary exponentiation.
4. **Cost:** time O(log n); space O(1).

## 5. Recall
Q: 50. Pow(x, n): which pattern, and what is the insight?
A: Binary exponentiation. Repeated squaring gives x to 1, 2, 4, 8, and any exponent is a sum of some of those.

Q: 50. Pow(x, n): what is the analogy, and what does it stand for?
A: Folding paper: fold → square; on → odd.

Q: 50. Pow(x, n): What is pow's invariant?
A: result times base to the remaining power equals the answer.

Q: 50. Pow(x, n): what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 3 | TOPIC | 296 — 43. Multiply Strings | Medium · Schoolbook multiplication | MUST
# 296 — 43. Multiply Strings

**Problem 296 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Schoolbook multiplication**

[Open on LeetCode](https://leetcode.com/problems/multiply-strings/)

## 1. Problem Statement
Two non-negative integers are given as strings. Return their product as a string, without converting the inputs to integers or using a big-integer library.

### Examples
```text
Input:  num1 = "2", num2 = "3"
Output: "6"

Input:  num1 = "123", num2 = "456"
Output: "56088"
```

### Constraints
```text
1 <= num1.length, num2.length <= 200
num1 and num2 consist of digits only.
Both num1 and num2 do not contain any leading zero, except the number 0 itself.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The school grid
It is the school multiplication grid. Digit i times digit j always lands in columns i+j and i+j+1, so there is no need to write separate rows: pour every product into those columns.

| In the story | In the solution |
|---|---|
| columns | array |
| meet | product |

### Pattern signal
**Schoolbook multiplication.** Place values depend only on the two indices, so products can be accumulated in one array.

### Thought process
1. **First idea:** Add the first number repeatedly, which is impossibly slow.
2. **The insight:** Place values depend only on the two indices, so products can be accumulated in one array.

### Optimal approach
1. Return zero if either is zero.
2. Loop both numbers backwards.
3. Add the product to its column.
4. Keep the units and carry left.
5. Skip a leading zero.

### Dry run
```text
12*34
i,j   cells
1,1   0,0,0,8
1,0   0,0,6,8
0,1   0,1,0,8
0,0   0,4,0,8
```

### Why it works
The product is the sum of all digit products at their place values. Going right to left normalizes each column as it is finished.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String multiply(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) return "0";
        int[] columns = new int[num1.length() + num2.length()];
        for (int i = num1.length() - 1; i >= 0; i--) {
            for (int j = num2.length() - 1; j >= 0; j--) {
                int sum = (num1.charAt(i) - '0') * (num2.charAt(j) - '0') + columns[i + j + 1];
                columns[i + j + 1] = sum % 10;
                columns[i + j] += sum / 10;
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = columns[0] == 0 ? 1 : 0; i < columns.length; i++) result.append(columns[i]);
        return result.toString();
    }
}
```

### Code walkthrough
- `i + j + 1`: The units column for this pair of digits.
- `sum / 10`: The carry joins the column to the left.
- `columns[0] == 0 ?`: Only the first column can be empty.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m + n)

### Common mistakes
1. **Parsing overflows.** Fix: use an array of columns.
2. **Zero would print many zeros.** Fix: return "0" early.
3. **Columns receive many products.** Fix: add to them.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `"0", "12"` | `"0"` |
| One digit | `"2", "3"` | `"6"` |
| Carry | `"99", "99"` | `"9801"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[275 — 67. Add Binary]]: One row. The key idea there: Binary addition is column addition with base 2 instead of 10.
- [[267 — 66. Plus One]]: One. The key idea there: Adding one only changes the trailing 9s and one digit before them.

### In a real interview
Say it in this order:

1. **Brute force:** Add the first number repeatedly, which is impossibly slow.
2. **Insight:** Place values depend only on the two indices, so products can be accumulated in one array.
3. **Approach in a few words:** m+n columns.
4. **Cost:** time O(m·n); space O(m + n).

## 5. Recall
Q: 43. Multiply Strings: which pattern, and what is the insight?
A: Schoolbook multiplication. Place values depend only on the two indices, so products can be accumulated in one array.

Q: 43. Multiply Strings: what is the analogy, and what does it stand for?
A: The school grid: columns → array; meet → product.

Q: 43. Multiply Strings: Where do digits i,j land?
A: i+j and i+j+1.

Q: 43. Multiply Strings: what are the time and space costs?
A: Time O(m·n). Space O(m + n).

@@ 3 | TOPIC | 297 — 6. Zigzag Conversion | Medium · Cycle indices | SHOULD
# 297 — 6. Zigzag Conversion

**Problem 297 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Cycle indices**

[Open on LeetCode](https://leetcode.com/problems/zigzag-conversion/)

## 1. Problem Statement
Write the string in a zigzag over `numRows` rows, going straight down and then diagonally up, repeatedly. Return the text obtained by reading the rows one after another.

### Examples
```text
Input:  s = "PAYPALISHIRING", numRows = 3
Output: "PAHNAPLSIIGYIR"

Input:  s = "PAYPALISHIRING", numRows = 4
Output: "PINALSIGYAHRPI"

Input:  s = "A", numRows = 1
Output: "A"
```

### Constraints
```text
1 <= s.length <= 1000
s consists of English letters (lower-case and upper-case), ',' and '.'.
1 <= numRows <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A bouncing ball's floors
A ball bounces between floor 0 and the last one. It passes the two end floors once per cycle and each middle floor twice, going down and coming up, so you can calculate when it visits each.

| In the story | In the solution |
|---|---|
| cycle | 2(rows-1) |
| twice | diagonal |

### Pattern signal
**Cycle indices.** The pattern repeats every 2(rows-1) characters, so each row's indices follow from arithmetic.

### Thought process
1. **First idea:** Build the zigzag grid, wasting space.
2. **The insight:** The pattern repeats every 2(rows-1) characters, so each row's indices follow from arithmetic.

### Optimal approach
1. Return the string for one row.
2. For each row jump by the cycle.
3. Append that letter.
4. For middle rows also append the diagonal one.

### Dry run
```text
s = PAYPAL, numRows = 3, cycle = 4
row  jump indices  diagonal indices  letters
0    0, 4          none              P A
1    1, 5          3                 A P L
2    2             none              Y
result = PAAPLY
```

### Why it works
Every index has exactly one position within its cycle, and that position determines one row, so each is appended once in reading order.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String convert(String s, int numRows) {
        if (numRows == 1) return s;
        int cycle = 2 * (numRows - 1);
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < numRows; row++) {
            for (int i = row; i < s.length(); i += cycle) {
                result.append(s.charAt(i));
                int second = i + cycle - 2 * row;
                if (row > 0 && row < numRows - 1 && second < s.length()) result.append(s.charAt(second));
            }
        }
        return result.toString();
    }
}
```

### Code walkthrough
- `numRows == 1`: A single row would give a cycle of zero.
- `i += cycle`: The same floor one bounce later.
- `i + cycle - 2 * row`: The visit on the way back up.

### Complexity
- **Time:** O(n)
- **Space:** O(n) for the output

### Common mistakes
1. **One row makes a zero step.** Fix: return the string.
2. **Edge rows have one letter per cycle.** Fix: add the diagonal only in the middle.
3. **The diagonal index can overrun.** Fix: check the length.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `ab,1` | `"ab"` |
| Two | `abcd,2` | `"acbd"` |
| Tall | `ab,5` | `"ab"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[291 — 54. Spiral Matrix]]: 2D. The key idea there: Four border numbers fully describe the part not yet read.
- [[151 — 103. Binary Tree Zigzag Level Order Traversal]]: Direction. The key idea there: Never change the order of discovery, only where each value is inserted.

### In a real interview
Say it in this order:

1. **Brute force:** Build the zigzag grid, wasting space.
2. **Insight:** The pattern repeats every 2(rows-1) characters, so each row's indices follow from arithmetic.
3. **Approach in a few words:** Period arithmetic.
4. **Cost:** time O(n); space O(n) for the output.

## 5. Recall
Q: 6. Zigzag Conversion: which pattern, and what is the insight?
A: Cycle indices. The pattern repeats every 2(rows-1) characters, so each row's indices follow from arithmetic.

Q: 6. Zigzag Conversion: what is the analogy, and what does it stand for?
A: A bouncing ball's floors: cycle → 2(rows-1); twice → diagonal.

Q: 6. Zigzag Conversion: What is the zigzag period?
A: Two times rows minus one.

Q: 6. Zigzag Conversion: what are the time and space costs?
A: Time O(n). Space O(n) for the output.

@@ 3 | TOPIC | 298 — 371. Sum of Two Integers | Medium · XOR and carry | MUST
# 298 — 371. Sum of Two Integers

**Problem 298 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: XOR and carry**

[Open on LeetCode](https://leetcode.com/problems/sum-of-two-integers/)

## 1. Problem Statement
You get two integers a and b. Return their sum without using the plus or minus operators.

### Examples
```text
Input:  a = 1, b = 2
Output: 3

Input:  a = 2, b = 3
Output: 5
```

### Constraints
```text
-1000 <= a, b <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two sheets of paper
Use two sheets of paper. On one write each column's sum ignoring carries, on the other only the carries, shifted to their column. Add the sheets in the same way until the carry sheet is blank.

| In the story | In the solution |
|---|---|
| sums | XOR |
| carries | AND shifted |

### Pattern signal
**XOR and carry.** XOR is addition without carry, and AND shifted left is exactly the carry.

### Thought process
1. **First idea:** Count up one at a time, which still needs addition.
2. **The insight:** XOR is addition without carry, and AND shifted left is exactly the carry.

### Optimal approach
1. Compute the shifted carry.
2. XOR the carry-free sum.
3. Repeat with the carry until it is zero.

### Dry run
```text
5+3
a  b
5  3
6  2
4  4
0  8
8  0
```

### Why it works
Each round preserves the total modulo 2^32, and the carry's lowest bit moves left, so it disappears within 32 rounds.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int getSum(int a, int b) {
        while (b != 0) {
            int carry = (a & b) << 1;
            a ^= b;
            b = carry;
        }
        return a;
    }
}
```

### Code walkthrough
- `(a & b) << 1`: The carries, moved to the column where they count.
- `a ^= b`: The sum of each column ignoring carries.
- `b = carry`: Now add the carry sheet.

### Complexity
- **Time:** O(1), at most 32 rounds
- **Space:** O(1)

### Common mistakes
1. **An unshifted carry lands in the wrong column.** Fix: shift left.
2. **Changing a first loses the carry.** Fix: compute carry first.
3. **Negative carries are normal.** Fix: loop while non-zero.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `0,0` | `0` |
| Negative | `-1,1` | `0` |
| Both | `-2,-3` | `-5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[275 — 67. Add Binary]]: Text. The key idea there: Binary addition is column addition with base 2 instead of 10.
- [[270 — 136. Single Number]]: Cancel. The key idea there: XOR is that flip: x XOR x is 0, and order doesn't matter.

### In a real interview
Say it in this order:

1. **Brute force:** Count up one at a time, which still needs addition.
2. **Insight:** XOR is addition without carry, and AND shifted left is exactly the carry.
3. **Approach in a few words:** XOR and carry.
4. **Cost:** time O(1), at most 32 rounds; space O(1).

## 5. Recall
Q: 371. Sum of Two Integers: which pattern, and what is the insight?
A: XOR and carry. XOR is addition without carry, and AND shifted left is exactly the carry.

Q: 371. Sum of Two Integers: what is the analogy, and what does it stand for?
A: Two sheets of paper: sums → XOR; carries → AND shifted.

Q: 371. Sum of Two Integers: What computes carry?
A: AND shifted left.

Q: 371. Sum of Two Integers: what are the time and space costs?
A: Time O(1), at most 32 rounds. Space O(1).

@@ 3 | TOPIC | 299 — 7. Reverse Integer | Medium · Digit reversal | MUST
# 299 — 7. Reverse Integer

**Problem 299 – Topic: Mixed / Hard and Contest-style | Difficulty: Medium | Pattern: Digit reversal**

[Open on LeetCode](https://leetcode.com/problems/reverse-integer/)

## 1. Problem Statement
You get a signed 32-bit integer x. Return it with its digits reversed, or 0 if that leaves the 32-bit range. Assume you cannot store 64-bit integers.

### Examples
```text
Input:  x = 123
Output: 321

Input:  x = -123
Output: -321

Input:  x = 120
Output: 21
```

### Constraints
```text
-2^31 <= x <= 2^31 - 1
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Plates on a limited shelf
You move plates to a shelf one at a time, shifting the others left to make room. The shelf has a fixed length, so before each shift check there is room. Afterwards it is too late, the plate has already broken.

| In the story | In the solution |
|---|---|
| check | divide limits |
| plate | digit |

### Pattern signal
**Digit reversal.** Overflow can be predicted before it happens by comparing with the limit divided by ten.

### Thought process
1. **First idea:** Use a long and check at the end, which the problem forbids.
2. **The insight:** Overflow can be predicted before it happens by comparing with the limit divided by ten.

### Optimal approach
1. Take the signed last digit.
2. Check the result against both divided limits.
3. Shift and add.
4. Drop the digit from x.

### Dry run
```text
-123
pop  result
-3   -3
-2   -32
-1   -321
```

### Why it works
The check catches every shift that cannot fit. An equal prefix is safe, because a ten-digit int starts with 1 or 2, which is then the last digit added.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int reverse(int x) {
        int result = 0;
        while (x != 0) {
            int digit = x % 10;
            if (result > Integer.MAX_VALUE / 10 || result < Integer.MIN_VALUE / 10) return 0;
            result = result * 10 + digit;
            x /= 10;
        }
        return result;
    }
}
```

### Code walkthrough
- `x % 10`: Java keeps the sign in the remainder.
- `MAX_VALUE / 10`: No room to shift, so it would overflow.
- `result * 10 + digit`: Shift left and place the digit.

### Complexity
- **Time:** O(log n)
- **Space:** O(1)

### Common mistakes
1. **Checking afterwards sees a wrapped value.** Fix: check before multiplying.
2. **abs of MIN_VALUE stays negative.** Fix: use signed remainders.
3. **A positive-only loop skips negatives.** Fix: loop while non-zero.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zeros | `120` | `21` |
| Negative | `-5` | `-5` |
| Overflow | `1534236469` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[268 — 9. Palindrome Number]]: Safe. The key idea there: Only half needs reversing, and half of an int can never overflow.
- [[273 — 190. Reverse Bits]]: Fixed. The key idea there: Popping from the low end while pushing onto the low end reverses a sequence.

### In a real interview
Say it in this order:

1. **Brute force:** Use a long and check at the end, which the problem forbids.
2. **Insight:** Overflow can be predicted before it happens by comparing with the limit divided by ten.
3. **Approach in a few words:** Pre-checked shift.
4. **Cost:** time O(log n); space O(1).

## 5. Recall
Q: 7. Reverse Integer: which pattern, and what is the insight?
A: Digit reversal. Overflow can be predicted before it happens by comparing with the limit divided by ten.

Q: 7. Reverse Integer: what is the analogy, and what does it stand for?
A: Plates on a limited shelf: check → divide limits; plate → digit.

Q: 7. Reverse Integer: When check overflow?
A: Before multiplying.

Q: 7. Reverse Integer: what are the time and space costs?
A: Time O(log n). Space O(1).

@@ 2 | MODULE | Mixed / Hard and Contest-style — Hard | 5 problems | MUST
# Mixed / Hard and Contest-style: Hard

Patterns in this group: Frequency lists, Two passes, Offline sweep and heap, Slope map, BFS on routes.

@@ 3 | TOPIC | 300 — 460. LFU Cache | Hard · Frequency lists | SHOULD
# 300 — 460. LFU Cache

**Problem 300 – Topic: Mixed / Hard and Contest-style | Difficulty: Hard | Pattern: Frequency lists**

[Open on LeetCode](https://leetcode.com/problems/lfu-cache/)

## 1. Problem Statement
Design an LFU cache with O(1) average `get` and `put`. When full, evict the key used least often, breaking ties by least recently used. A get or an updating put counts as a use, and a new key starts at one.

### Examples
```text
Input:  ["LFUCache", "put", "put", "get", "put", "get", "get", "put", "get", "get", "get"]
        [[2], [1, 1], [2, 2], [1], [3, 3], [2], [3], [4, 4], [1], [3], [4]]
Output: [null, null, null, 1, null, -1, 3, null, -1, 3, 4]
```

### Constraints
```text
1 <= capacity <= 10^4
0 <= key <= 10^5
0 <= value <= 10^9
At most 2 * 10^5 calls will be made to get and put.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A library with shelves by loan count
A library keeps shelves by number of loans. A borrowed book moves to the right end of the next shelf. To make space remove the left-most book of the lowest shelf: least used, and oldest among those.

| In the story | In the solution |
|---|---|
| shelves | linked sets |
| lowest | pointer |

### Pattern signal
**Frequency lists.** A count only grows by one, so books move one shelf at a time and the lowest shelf can be tracked in O(1).

### Thought process
1. **First idea:** Scan every key for the smallest count and time, in linear time.
2. **The insight:** A count only grows by one, so books move one shelf at a time and the lowest shelf can be tracked in O(1).

### Optimal approach
1. On use move the key to the next shelf.
2. Raise the minimum if that shelf emptied.
3. When full evict the first of the minimum shelf.
4. Put a new key on shelf one.

### Dry run
```text
capacity 2
operation  shelf 1  shelf 2  lowest
put 1      1        -        1
put 2      1,2      -        1
get 1      2        1        1
put 3      3        1        1, key 2 was evicted
```

### Why it works
Each shelf keeps the order in which keys reached that count, which is recency. The minimum changes only when its shelf empties or a new key arrives.

## 3. Java Solution with Explanation
### Code
```java
class LFUCache {
    private final Map<Integer, Integer> value = new HashMap<>(), uses = new HashMap<>();
    private final Map<Integer, LinkedHashSet<Integer>> shelf = new HashMap<>();
    private final int capacity;
    private int lowest;

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        if (!value.containsKey(key)) return -1;
        borrow(key);
        return value.get(key);
    }

    public void put(int key, int v) {
        if (value.containsKey(key)) {
            value.put(key, v);
            borrow(key);
            return;
        }
        if (value.size() == capacity) {
            int old = shelf.get(lowest).iterator().next();
            shelf.get(lowest).remove(old);
            value.remove(old);
            uses.remove(old);
        }
        value.put(key, v);
        uses.put(key, 1);
        shelf.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        lowest = 1;
    }

    private void borrow(int key) {
        int f = uses.get(key);
        shelf.get(f).remove(key);
        if (f == lowest && shelf.get(f).isEmpty()) lowest++;
        uses.put(key, f + 1);
        shelf.computeIfAbsent(f + 1, k -> new LinkedHashSet<>()).add(key);
    }
}
```

### Code walkthrough
- `LinkedHashSet`: Keeps arrival order with O(1) removal.
- `lowest++`: The lowest shelf just emptied into the next.
- `lowest = 1`: A new key always has the fewest uses.

### Complexity
- **Time:** O(1) per operation
- **Space:** O(capacity)

### Common mistakes
1. **An updating put is a use.** Fix: raise its count.
2. **Inserting before evicting may evict the newcomer.** Fix: evict first.
3. **A stale minimum evicts wrongly.** Fix: reset it on every insert.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Missing | `get` | `-1` |
| One | `cap1 two puts get first` | `-1` |
| Update | `put1,1 put1,2 get` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[276 — 146. LRU Cache]]: One. The key idea there: A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).
- [[086 — 895. Maximum Frequency Stack]]: Stacks. The key idea there: One stack per frequency keeps recency and frequency together, with no moving.

### In a real interview
Say it in this order:

1. **Brute force:** Scan every key for the smallest count and time, in linear time.
2. **Insight:** A count only grows by one, so books move one shelf at a time and the lowest shelf can be tracked in O(1).
3. **Approach in a few words:** Frequency lists and minimum.
4. **Cost:** time O(1) per operation; space O(capacity).

## 5. Recall
Q: 460. LFU Cache: which pattern, and what is the insight?
A: Frequency lists. A count only grows by one, so books move one shelf at a time and the lowest shelf can be tracked in O(1).

Q: 460. LFU Cache: what is the analogy, and what does it stand for?
A: A library with shelves by loan count: shelves → linked sets; lowest → pointer.

Q: 460. LFU Cache: When does minimum frequency rise?
A: When its last key is used.

Q: 460. LFU Cache: what are the time and space costs?
A: Time O(1) per operation. Space O(capacity).

@@ 3 | TOPIC | Practice — Review 50 | Re-solve without looking | MUST
# Review 50

Solve on a blank page. No notes, hints or solutions.

## 460. LFU Cache
Design an O(1) least-frequently-used cache.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/lfu-cache/)

## 6. Zigzag Conversion
Read a zigzag arrangement by rows.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/zigzag-conversion/)

## 1029. Two City Scheduling
Send half the people to each city cheaply.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/two-city-scheduling/)

## 312. Burst Balloons
Maximize coins from bursting balloons.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/burst-balloons/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 460 | | | | |
| 6 | | | | |
| 1029 | | | | |
| 312 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[300 — 460. LFU Cache]]
- [[297 — 6. Zigzag Conversion]]
- [[286 — 1029. Two City Scheduling]]
- [[262 — 312. Burst Balloons]]

@@ 3 | TOPIC | 301 — 135. Candy | Hard · Two passes | SHOULD
# 301 — 135. Candy

**Problem 301 – Topic: Mixed / Hard and Contest-style | Difficulty: Hard | Pattern: Two passes**

[Open on LeetCode](https://leetcode.com/problems/candy/)

## 1. Problem Statement
Children stand in a line with ratings. Each gets at least one candy, and a child rated higher than an immediate neighbor gets more than that neighbor. Return the fewest candies needed.

### Examples
```text
Input:  ratings = [1,0,2]
Output: 5

Input:  ratings = [1,2,2]
Output: 4
```

### Constraints
```text
1 <= n == ratings.length <= 5 * 10^4
0 <= ratings[i] <= 5 * 10^4
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two inspections
Two inspectors walk the line. The first, going right, makes each child beat a lower-rated left neighbor. The second, going left, does the same for right neighbors but never takes candy away.

| In the story | In the solution |
|---|---|
| inspections | passes |
| not back | max |

### Pattern signal
**Two passes.** The two neighbor rules are independent lower bounds, so the larger of them satisfies both.

### Thought process
1. **First idea:** Repeatedly fix violations until stable, in O(n²).
2. **The insight:** The two neighbor rules are independent lower bounds, so the larger of them satisfies both.

### Optimal approach
1. Give everyone one.
2. Scan right for rises.
3. Scan left taking the maximum.
4. Sum.

### Dry run
```text
1,0,2
pass   candies
left   1,1,2
right  2,1,2
total  5
```

### Why it works
Each pass's value is the least that rule allows. Their maximum is necessary and still satisfies both rules, so the total is minimal.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int candy(int[] ratings) {
        int n = ratings.length;
        int[] candies = new int[n];
        Arrays.fill(candies, 1);
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) candies[i] = candies[i - 1] + 1;
        }
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) candies[i] = Math.max(candies[i], candies[i + 1] + 1);
        }
        int total = 0;
        for (int c : candies) total += c;
        return total;
    }
}
```

### Code walkthrough
- `Arrays.fill`: Everyone receives at least one.
- `candies[i - 1] + 1`: Beat the lower-rated child on the left.
- `Math.max`: Satisfy the right without breaking the left.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **One pass sees one neighbor.** Fix: pass both ways.
2. **The second pass can break the first.** Fix: take the maximum.
3. **Equal ratings impose no rule.** Fix: compare strictly.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5]` | `1` |
| Equal | `[2,2]` | `2` |
| Fall | `[3,2,1]` | `6` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[051 — 42. Trapping Rain Water]]: Maxima. The key idea there: You don't need both exact maxima, only the limiting one, and the lower side reveals it.
- [[019 — 238. Product of Array Except Self]]: Products. The key idea there: Everything except me is simply everything left of me times everything right of me.

### In a real interview
Say it in this order:

1. **Brute force:** Repeatedly fix violations until stable, in O(n²).
2. **Insight:** The two neighbor rules are independent lower bounds, so the larger of them satisfies both.
3. **Approach in a few words:** Two lower bounds.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 135. Candy: which pattern, and what is the insight?
A: Two passes. The two neighbor rules are independent lower bounds, so the larger of them satisfies both.

Q: 135. Candy: what is the analogy, and what does it stand for?
A: Two inspections: inspections → passes; not back → max.

Q: 135. Candy: Why maximum in the second pass?
A: To preserve the left rule.

Q: 135. Candy: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 302 — 1851. Minimum Interval to Include Each Query | Hard · Offline sweep and heap | MUST
# 302 — 1851. Minimum Interval to Include Each Query

**Problem 302 – Topic: Mixed / Hard and Contest-style | Difficulty: Hard | Pattern: Offline sweep and heap**

[Open on LeetCode](https://leetcode.com/problems/minimum-interval-to-include-each-query/)

## 1. Problem Statement
An interval [left, right] has size right-left+1. For each query value return the size of the smallest interval containing it, or -1 if none does, in the queries' original order.

### Examples
```text
Input:  intervals = [[1,4],[2,4],[3,6],[4,4]], queries = [2,3,4,5]
Output: [3,3,1,4]

Input:  intervals = [[2,3],[2,5],[1,8],[20,25]], queries = [2,19,5,22]
Output: [2,-1,4,6]
```

### Constraints
```text
1 <= intervals.length <= 10^5
1 <= queries.length <= 10^5
intervals[i].length == 2
1 <= lefti <= righti <= 10^7
1 <= queries[j] <= 10^7
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A museum admitting visitors by time
A museum answers "what is the shortest show on at time q?" Sort the questions by time. Shows go on a board when they open, ordered by length, and a finished one is taken down when it surfaces at the top.

| In the story | In the solution |
|---|---|
| board | heap |
| time order | sorted queries |

### Pattern signal
**Offline sweep and heap.** Offline, sorting queries makes intervals only ever arrive and expire as you sweep.

### Thought process
1. **First idea:** Scan every interval for every query, in O(nq).
2. **The insight:** Offline, sorting queries makes intervals only ever arrive and expire as you sweep.

### Optimal approach
1. Sort intervals by start and query indices by value.
2. Add started intervals to a size heap.
3. Drop expired tops.
4. Write the top's size at the original index.

### Dry run
```text
intervals = [1,4] [2,4] [3,6] [4,4], queries = 2,3,4,5
q  admitted      expired removed  smallest  answer
2  [1,4] [2,4]   none             [2,4]     3
3  [3,6]         none             [2,4]     3
4  [4,4]         none             [4,4]     1
5  none          those ending 4   [3,6]     4
```

### Why it works
After both steps the top has started and not ended, and nothing smaller on the board is valid. An expired interval can't serve a later query.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] minInterval(int[][] intervals, int[] queries) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        Integer[] order = new Integer[queries.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> queries[a] - queries[b]);
        PriorityQueue<int[]> board = new PriorityQueue<>((a, b) -> (a[1] - a[0]) - (b[1] - b[0]));
        int[] result = new int[queries.length];
        int next = 0;
        for (int i : order) {
            int q = queries[i];
            while (next < intervals.length && intervals[next][0] <= q) board.add(intervals[next++]);
            while (!board.isEmpty() && board.peek()[1] < q) board.poll();
            result[i] = board.isEmpty() ? -1 : board.peek()[1] - board.peek()[0] + 1;
        }
        return result;
    }
}
```

### Code walkthrough
- `queries[a] - queries[b]`: Sweep the questions in time order.
- `board.peek()[1] < q`: This show has finished, so it can't help any later question.
- `result[i]`: Answers go back to the question's original place.

### Complexity
- **Time:** O(n log n + q log q)
- **Space:** O(n + q)

### Common mistakes
1. **Answers in sorted order are misplaced.** Fix: keep original indices.
2. **A heap by end finds the wrong interval.** Fix: order by size.
3. **Sizes are inclusive.** Fix: add one.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `intervals [ [1,2] ], queries [3]` | `[-1]` |
| Single point | `intervals [ [2,2] ], queries [2]` | `[1]` |
| Nested | `intervals [ [1,9],[2,3] ], queries [2]` | `[2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[184 — 502. IPO]]: Profit. The key idea there: Among affordable projects only the profit matters, so unlock by capital and choose by profit.
- [[288 — 56. Merge Intervals]]: Merge. The key idea there: After sorting by start only the last merged block can overlap the next interval.

### In a real interview
Say it in this order:

1. **Brute force:** Scan every interval for every query, in O(nq).
2. **Insight:** Offline, sorting queries makes intervals only ever arrive and expire as you sweep.
3. **Approach in a few words:** Offline sweep and heap.
4. **Cost:** time O(n log n + q log q); space O(n + q).

## 5. Recall
Q: 1851. Minimum Interval to Include Each Query: which pattern, and what is the insight?
A: Offline sweep and heap. Offline, sorting queries makes intervals only ever arrive and expire as you sweep.

Q: 1851. Minimum Interval to Include Each Query: what is the analogy, and what does it stand for?
A: A museum admitting visitors by time: board → heap; time order → sorted queries.

Q: 1851. Minimum Interval to Include Each Query: Why sort queries?
A: Admission and expiry become monotonic.

Q: 1851. Minimum Interval to Include Each Query: what are the time and space costs?
A: Time O(n log n + q log q). Space O(n + q).

@@ 3 | TOPIC | 303 — 149. Max Points on a Line | Hard · Slope map | SHOULD
# 303 — 149. Max Points on a Line

**Problem 303 – Topic: Mixed / Hard and Contest-style | Difficulty: Hard | Pattern: Slope map**

[Open on LeetCode](https://leetcode.com/problems/max-points-on-a-line/)

## 1. Problem Statement
You get unique points on a plane. Return the largest number of them that lie on one straight line.

### Examples
```text
Input:  points = [[1,1],[2,2],[3,3]]
Output: 3

Input:  points = [[1,1],[3,2],[5,3],[4,1],[2,3],[1,4]]
Output: 4
```

### Constraints
```text
1 <= points.length <= 300
points[i].length == 2
-10^4 <= xi, yi <= 10^4
All the points are unique.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A lighthouse beam
Stand in a lighthouse at one point and swing the beam. All points lit at the same angle are on one line through you. Name each angle by its smallest whole-number step and count per name.

| In the story | In the solution |
|---|---|
| direction | gcd-reduced |
| beam | map key |

### Pattern signal
**Slope map.** With an anchor fixed a line is determined by direction alone, which a reduced dx, dy names exactly.

### Thought process
1. **First idea:** Test every triple of points, in O(n³).
2. **The insight:** With an anchor fixed a line is determined by direction alone, which a reduced dx, dy names exactly.

### Optimal approach
1. Fix an anchor with a fresh map.
2. Reduce each later point's offset by its gcd.
3. Normalize the sign.
4. Count it.
5. Add one for the anchor.

### Dry run
```text
points = [1,1] [2,2] [3,3]
anchor  other  dx,dy  reduced key  count  line size
[1,1]   [2,2]  1,1    1,1          1      2
[1,1]   [3,3]  2,2    1,1          2      3
```

### Why it works
For the best line, when its first point is the anchor every other point on it gets the same key and is counted.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxPoints(int[][] points) {
        int best = 1;
        for (int i = 0; i < points.length; i++) {
            Map<String, Integer> beam = new HashMap<>();
            for (int j = i + 1; j < points.length; j++) {
                int dx = points[j][0] - points[i][0], dy = points[j][1] - points[i][1];
                int g = gcd(Math.abs(dx), Math.abs(dy));
                dx /= g;
                dy /= g;
                if (dx < 0 || (dx == 0 && dy < 0)) {
                    dx = -dx;
                    dy = -dy;
                }
                best = Math.max(best, beam.merge(dx + "," + dy, 1, Integer::sum) + 1);
            }
        }
        return best;
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
```

### Code walkthrough
- `dx /= g`: Reduce to the smallest whole step, with no rounding.
- `dx == 0 && dy < 0`: Opposite directions are the same line.
- `+ 1`: The anchor is on the line too.

### Complexity
- **Time:** O(n² log C) for coordinates up to C
- **Space:** O(n)

### Common mistakes
1. **Floating slopes round.** Fix: reduce with gcd.
2. **Vertical and opposite directions differ.** Fix: normalize the sign.
3. **A global map merges parallel lines.** Fix: reset it per anchor.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [0,0] ]` | `1` |
| Vertical | `[ [0,0],[0,1],[0,2] ]` | `3` |
| Opposite | `[ [1,1],[0,0],[2,2] ]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[004 — 1. Two Sum]]: Complement. The key idea there: The partner is not a mystery: it is exactly target minus x. A map finds it at once.
- [[265 — 1071. Greatest Common Divisor of Strings]]: Strings. The key idea there: Two strings are built from one unit exactly when ab equals ba, and the longest unit's length is the gcd of their lengths.

### In a real interview
Say it in this order:

1. **Brute force:** Test every triple of points, in O(n³).
2. **Insight:** With an anchor fixed a line is determined by direction alone, which a reduced dx, dy names exactly.
3. **Approach in a few words:** Reduced slopes per anchor.
4. **Cost:** time O(n² log C) for coordinates up to C; space O(n).

## 5. Recall
Q: 149. Max Points on a Line: which pattern, and what is the insight?
A: Slope map. With an anchor fixed a line is determined by direction alone, which a reduced dx, dy names exactly.

Q: 149. Max Points on a Line: what is the analogy, and what does it stand for?
A: A lighthouse beam: direction → gcd-reduced; beam → map key.

Q: 149. Max Points on a Line: What key identifies a line per anchor?
A: The sign-normalized reduced direction.

Q: 149. Max Points on a Line: what are the time and space costs?
A: Time O(n² log C) for coordinates up to C. Space O(n).

@@ 3 | TOPIC | 304 — 815. Bus Routes | Hard · BFS on routes | SHOULD
# 304 — 815. Bus Routes

**Problem 304 – Topic: Mixed / Hard and Contest-style | Difficulty: Hard | Pattern: BFS on routes**

[Open on LeetCode](https://leetcode.com/problems/bus-routes/)

## 1. Problem Statement
`routes[i]` lists the stops that bus i repeats forever. Starting on foot at `source` and travelling only by bus, return the fewest buses to reach `target`, or -1 if impossible.

### Examples
```text
Input:  routes = [[1,2,7],[3,6,7]], source = 1, target = 6
Output: 2

Input:  routes = [[7,12],[4,5,15],[6],[15,19],[9,12,13]], source = 15, target = 12
Output: -1
```

### Constraints
```text
1 <= routes.length <= 500.
1 <= routes[i].length <= 10^5
All the values of routes[i] are unique.
sum(routes[i].length) <= 10^5
0 <= routes[i][j] < 10^6
0 <= source, target < 10^6
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A rumour riding buses
A rumour rides buses. Boarding costs one, and then every stop on that route hears it at once. Stops reached by one bus spread it to their buses in round two. Nobody boards the same bus twice.

| In the story | In the solution |
|---|---|
| bus | expanded once |
| round | buses |

### Pattern signal
**BFS on routes.** The cost is per bus, so one BFS step must be boarding a whole route.

### Thought process
1. **First idea:** Link adjacent stops and search, which minimizes stops instead of buses.
2. **The insight:** The cost is per bus, so one BFS step must be boarding a whole route.

### Optimal approach
1. Return zero for an equal start.
2. Map stops to buses.
3. BFS by rounds from the source.
4. Expand each new bus's whole route.
5. Return the round on reaching the target.

### Dry run
```text
routes = [1,2,7] [3,6,7], source = 1, target = 6
buses  expand from  board  stops heard
1      1            bus 0  1,2,7
2      2,7          bus 1  3, then 6 is the target, return 2
```

### Why it works
Round k reaches exactly the stops needing k buses, so the first time the target appears is optimal.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numBusesToDestination(int[][] routes, int source, int target) {
        if (source == target) return 0;
        Map<Integer, List<Integer>> busesAt = new HashMap<>();
        for (int bus = 0; bus < routes.length; bus++) {
            for (int stop : routes[bus]) busesAt.computeIfAbsent(stop, k -> new ArrayList<>()).add(bus);
        }
        boolean[] taken = new boolean[routes.length];
        Set<Integer> seen = new HashSet<>(List.of(source));
        Queue<Integer> queue = new ArrayDeque<>(List.of(source));
        for (int buses = 1; !queue.isEmpty(); buses++) {
            for (int size = queue.size(); size > 0; size--) {
                for (int bus : busesAt.getOrDefault(queue.poll(), List.of())) {
                    if (taken[bus]) continue;
                    taken[bus] = true;
                    for (int stop : routes[bus]) {
                        if (stop == target) return buses;
                        if (seen.add(stop)) queue.add(stop);
                    }
                }
            }
        }
        return -1;
    }
}
```

### Code walkthrough
- `source == target`: Already there, so no bus is needed.
- `taken[bus] = true`: Each route is expanded only once.
- `buses++`: One round of BFS is one more bus.

### Complexity
- **Time:** O(total stops)
- **Space:** O(total stops)

### Common mistakes
1. **Counting stops is the wrong cost.** Fix: count buses.
2. **Rescanning a route is quadratic.** Fix: take each bus once.
3. **An equal source and target needs no bus.** Fix: return 0 first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Same | `1 to 1` | `0` |
| Direct | `[ [1,2] ], 1, 2` | `1` |
| None | `[ [1],[2] ], 1, 2` | `-1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[226 — 127. Word Ladder]]: Edges. The key idea there: Neighbors can be generated with 26 tries per position, and equal steps mean BFS.
- [[211 — 909. Snakes and Ladders]]: Rolls. The key idea there: A square is a state and every roll costs one, so BFS gives the minimum.

### In a real interview
Say it in this order:

1. **Brute force:** Link adjacent stops and search, which minimizes stops instead of buses.
2. **Insight:** The cost is per bus, so one BFS step must be boarding a whole route.
3. **Approach in a few words:** BFS with buses as cost.
4. **Cost:** time O(total stops); space O(total stops).

## 5. Recall
Q: 815. Bus Routes: which pattern, and what is the insight?
A: BFS on routes. The cost is per bus, so one BFS step must be boarding a whole route.

Q: 815. Bus Routes: what is the analogy, and what does it stand for?
A: A rumour riding buses: bus → expanded once; round → buses.

Q: 815. Bus Routes: What costs one in Bus Routes?
A: Boarding a bus.

Q: 815. Bus Routes: what are the time and space costs?
A: Time O(total stops). Space O(total stops).

@@ 3 | TOPIC | Practice — Review 51 | Re-solve without looking | MUST
# Review 51

Solve on a blank page. No notes, hints or solutions.

## 815. Bus Routes
Find fewest buses between stops.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/bus-routes/)

## 135. Candy
Give fewest candies respecting rating neighbors.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/candy/)

## 48. Rotate Image
Rotate a square matrix clockwise.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/rotate-image/)

## 202. Happy Number
Decide whether squared-digit sums reach 1.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/happy-number/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 815 | | | | |
| 135 | | | | |
| 48 | | | | |
| 202 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[304 — 815. Bus Routes]]
- [[301 — 135. Candy]]
- [[290 — 48. Rotate Image]]
- [[266 — 202. Happy Number]]

@@ 3 | TOPIC | Practice — Pattern summary: Mixed / Hard and Contest-style | Key patterns of the topic | MUST
# Pattern summary: Mixed / Hard and Contest-style

Nobody tells you the pattern here. Read the constraints, state a brute force, find the repeated work and choose the structure that removes it. For greedy, prove that the choice is safe.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Base 26 without zero | Convert a number to an Excel column title. (168) | Z becomes zero without the shift |
| GCD | Find the largest string dividing both strings. (1071) | Returning any common prefix |
| Cycle detection | Decide whether squared-digit sums reach 1. (202) | An endless loop |
| Carry | Add one to a digit array. (66, 67) | Converting to int |
| Digit reversal | Decide whether an integer is a palindrome. (9, 7) | Negatives |
| Factor removal | Decide whether only prime factors 2, 3, 5 exist. (263) | Zero loops forever |
| XOR cancellation | Find the only value not appearing twice. (136) | Using it when repeats occur three times |
| Bit count | Count set bits. (191) | Signed shifts of negatives |
| Bit DP | Count set bits for 0..n. (338) | Missing base zero |
| Bit reversal | Reverse 32 bits. (190) | Stopping when the input is zero |
| Expected sum | Find the missing value in 0..n. (268) | Off by one in n |
| Map plus doubly list | Design an O(1) least-recently-used cache. (146) | Not refreshing on put |
| Kadane | Find the maximum subarray sum. (53) | All-negative input |
| Max and min Kadane | Find the maximum circular subarray sum. (918) | An empty wrapped result |
| Greedy reach | Decide whether the last index is reachable. (55) | Trying every jump |
| Implicit BFS | Find fewest jumps to the end. (45) | Counting a jump after the end |
| Reset start | Find the valid circular fuel start. (134) | Simulating every start |
| Sorted counts | Split cards into consecutive groups. (846) | An indivisible length |
| Safe elements | Decide whether max-merges can form a target triplet. (1899) | Using an oversized triplet |
| Last occurrence | Split so each letter stays in one part, maximizing parts. (763) | Cutting at the first letter's end |
| Two index stacks | Validate parentheses with star wildcards. (678) | A star left of an unmatched opener |
| Sort by regret | Send half the people to each city cheaply. (1029) | Choosing each person independently |
| Three zones | Insert and merge into sorted intervals. (57) | Touching endpoints overlap |
| Sort and merge | Merge overlapping intervals. (56) | A nested interval shrinking the end |
| Interval greedy | Remove fewest intervals to avoid overlap. (435) | Touching endpoints do not overlap |
| In-place rotation | Rotate a square matrix clockwise. (48) | Swapping pairs twice |
| Shrinking borders | Return a matrix in spiral order. (54, 59) | Reading the last row twice |
| Markers in matrix | Zero rows and columns containing zero in place. (73) | Destroying markers early |
| Greedy symbols | Convert an integer to Roman. (12) | Missing 4, 9, 40, 90, 400, 900 |
| Binary exponentiation | Compute x to the power n. (50) | Negating the minimum int |
| Schoolbook multiplication | Multiply numeric strings. (43) | Multiplication by zero |
| Cycle indices | Read a zigzag arrangement by rows. (6) | One row |
| XOR and carry | Add integers without plus or minus. (371) | Forgetting to shift the carry |
| Frequency lists | Design an O(1) least-frequently-used cache. (460) | A stale minimum frequency |
| Two passes | Give fewest candies respecting rating neighbors. (135) | The second pass overwriting the first |
| Offline sweep and heap | For each query find the smallest containing interval. (1851) | Answering in sorted query order |
| Slope map | Find the most collinear points. (149) | Vertical lines and negative zero |
| BFS on routes | Find fewest buses between stops. (815) | Counting stops instead of buses |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| maximum subarray | drop a losing backpack | Kadane | 53, 918 |
| can reach, fewest jumps | stretching a bridge | greedy reach | 55, 45 |
| circular route | restart after running dry | reset | 134 |
| merge or remove ranges | calendar | interval sort | 56, 57, 435 |
| choose A or B for all | who regrets most | sort by difference | 1029 |
| both neighbors | two inspections | two passes | 135 |
| without arithmetic | sum and carry sheets | bits | 371 |
| least recent or frequent | shelf with an index | linked structures | 146, 460 |

## In the interview, every time

1. Repeat the problem and ask about edge cases.
2. Read the target complexity off the constraints.
3. Say the brute force and its cost.
4. Name the repeated work and the pattern that removes it.
5. Code, trace an example, then state time and space.

Q: Which signals point to Mixed / Hard and Contest-style?
A: no topic given; greedy; intervals; bits; math; design.

Q: What is the core idea of Mixed / Hard and Contest-style?
A: Nobody tells you the pattern here. Read the constraints, state a brute force, find the repeated work and choose the structure that removes it. For greedy, prove that the choice is safe.
