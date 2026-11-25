@@ 1 | MODULE | Phase 1 — Arrays & Hashing | seen before; count; group; pair; many range sums | MUST
# Phase 1: Arrays & Hashing

Remember what you have scanned so that each lookup costs O(1). A map answers 'where or how many', a set answers 'is it there', and a prefix sum turns a range into one subtraction.

**Signals:** seen before; count; group; pair; many range sums.

## The big analogy: a shop with a good notebook

A shopkeeper with no notebook searches the whole store for every question. One with a notebook answers at a glance, and all they pay is a little paper. This whole topic is choosing **what to write in the notebook**.

| The shopkeeper's question | The notebook | Java | Meet it in |
|---|---|---|---|
| Has this customer been here? | A guest list | `HashSet` | 217, 128 |
| How many of each item? | A tally sheet | `int[]` or `HashMap` | 242, 554 |
| Where did I put it? | An index page | `HashMap` | 1, 380 |
| Which items belong together? | Labelled pigeonholes | map of lists | 49 |
| How much was sold between two days? | Running totals, like milestones | prefix array | 303, 560 |
| Is every numbered item in its place? | The numbered shelf itself | the input array | 448, 41 |

## Template
```java
Map<Integer, Integer> seen = new HashMap<>();
for (int i = 0; i < a.length; i++) {
    Integer j = seen.get(target - a[i]); // ask about the past
    if (j != null) return new int[]{j, i};
    seen.put(a[i], i);                   // then remember
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Arrays & Hashing — Easy | 16 problems | MUST
# Arrays & Hashing: Easy

Patterns in this group: Hash set, Frequency count, Scan from the end, Complement map, In-place partition, Two-way mapping, Greedy scan, Boyer-Moore voting, Prefix sum, Index as bucket, Direct addressing, Separate chaining, KMP prefix function, Set difference.

@@ 3 | TOPIC | 001 — 217. Contains Duplicate | Easy · Hash set | MUST
# 001 — 217. Contains Duplicate

**Problem 1 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Hash set**

[Open on LeetCode](https://leetcode.com/problems/contains-duplicate/)

## 1. Problem Statement
You get an integer array `nums`. Return true if any value appears at least twice, and false if every value is different.

### Examples
```text
Input:  nums = [1,2,3,1]
Output: true

Input:  nums = [1,2,3,4]
Output: false

Input:  nums = [1,1,1,3,3,4,3,2,4,2]
Output: true
```

### Constraints
```text
1 <= nums.length <= 10^5
-10^9 <= nums[i] <= 10^9
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The guest list at the door
A doorman writes down each guest's name as they arrive. Before writing, they glance at the list. If the name is already there, someone has come twice. They never question the other guests, they only check the list.

| In the story | In the solution |
|---|---|
| the list | the hash set |
| glancing at the list | an O(1) lookup |

### Pattern signal
**Hash set.** The only question at each value is "have I seen you?", so remember values in a structure that answers it instantly.

### Thought process
1. **First idea:** Compare every pair. With 100,000 values that is about 5 billion comparisons.
2. **The insight:** The only question at each value is "have I seen you?", so remember values in a structure that answers it instantly.

### Optimal approach
1. Start with an empty set.
2. Visit each value.
3. Stop with true if it is already in the set.
4. Otherwise add it.
5. Finish with false.

### Dry run
```text
nums = [1,2,3,1]
value  set before  already there?  action
1      {}          no              add 1
2      {1}         no              add 2
3      {1,2}       no              add 3
1      {1,2,3}     yes             return true
```

### Why it works
Before each step the set holds exactly the values to the left. The second copy of any duplicate must therefore find the first.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int x : nums) {
            // add returns false when x was already present
            if (!seen.add(x)) return true;
        }
        return false;
    }
}
```

### Code walkthrough
- `new HashSet<>()`: The empty guest list.
- `seen.add(x)`: Checks and writes in one call. False means the name was already there.
- `return false`: Reached only when nobody came twice.

### Complexity
- **Time:** O(n) on average, one set operation per value
- **Space:** O(n), the set may hold every value

### Common mistakes
1. **Using `List.contains`.** It scans the whole list, so use a set.
2. **Adding first and then calling `contains`.** That is always true, so check before adding.
3. **Sorting the caller's array.** It changes the input, so copy it first or use the set.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One value | `[5]` | `false` |
| Negative duplicate | `[-1,-1]` | `true` |
| Duplicate at both ends | `[7,1,2,7]` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[053 — 219. Contains Duplicate II]]: The guest only counts if seen among the last k, so evict older names from the set.
- [[122 — 287. Find the Duplicate Number]]: No extra space is allowed, so treat values as linked-list pointers.

### In a real interview
Say this: *"I trade O(n) memory for speed by remembering what I have seen. If memory were tight I would sort and compare neighbors."*

## 5. Recall
Q: 217. Contains Duplicate: which pattern, and what is the insight?
A: Hash set. The only question at each value is "have I seen you?", so remember values in a structure that answers it instantly.

Q: 217. Contains Duplicate: what is the analogy, and what does it stand for?
A: The guest list at the door: the list → the hash set; glancing at the list → an O(1) lookup.

Q: 217. Contains Duplicate: What does the set contain just before index i is processed?
A: Exactly the values at indices before i.

Q: 217. Contains Duplicate: what are the time and space costs?
A: Time O(n) on average, one set operation per value. Space O(n), the set may hold every value.

@@ 3 | TOPIC | 002 — 242. Valid Anagram | Easy · Frequency count | MUST
# 002 — 242. Valid Anagram

**Problem 2 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Frequency count**

[Open on LeetCode](https://leetcode.com/problems/valid-anagram/)

## 1. Problem Statement
You get two strings `s` and `t` of lowercase letters. Return true if `t` uses exactly the letters of `s`, each the same number of times, in any order. Follow-up: support Unicode.

### Examples
```text
Input:  s = "anagram", t = "nagaram"
Output: true

Input:  s = "rat", t = "car"
Output: false
```

### Constraints
```text
1 <= s.length, t.length <= 5 * 10^4
s and t consist of lowercase English letters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Scrabble tiles on a tray
Spell the first word by putting tiles on a tray. Spell the second by taking tiles off it. If the tray ends empty, with no debts, both words used the same tiles.

| In the story | In the solution |
|---|---|
| one pile per letter | one slot of a 26-long array |
| adding and removing a tile | `++` and `--` |

### Pattern signal
**Frequency count.** Order does not matter, only how many of each letter there are.

### Thought process
1. **First idea:** Sort both strings and compare them, in O(n log n). Generating permutations is hopeless.
2. **The insight:** Order does not matter, only how many of each letter there are.

### Optimal approach
1. Reject different lengths.
2. Add one for each letter of the first word.
3. Subtract one for each letter of the second.
4. Require every slot to be zero.

### Dry run
```text
s = "rat", t = "car"
i  s adds  t removes  non-zero slots
0  r       c          r=1, c=-1
1  a       a          r=1, c=-1
2  t       r          t=1, c=-1
End: t and c are not zero, so return false
```

### Why it works
A slot is always "times seen in s minus times seen in t". All zeros means identical counts, which is the definition of an anagram.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] balance = new int[26];
        for (int i = 0; i < s.length(); i++) {
            balance[s.charAt(i) - 'a']++; // tile onto the tray
            balance[t.charAt(i) - 'a']--; // tile off the tray
        }
        for (int count : balance) {
            if (count != 0) return false;
        }
        return true;
    }
}
```

### Code walkthrough
- `s.length() != t.length()`: Different tile totals can never match. It also makes the shared index safe.
- `s.charAt(i) - 'a'`: Turns a to z into 0 to 25, the pile number.
- `count != 0`: A leftover tile or a debt.

### Complexity
- **Time:** O(n), one pass plus 26 checks
- **Space:** O(1), always 26 slots

### Common mistakes
1. **Skipping the length check.** The shared loop then crashes or ignores letters.
2. **Comparing sets of letters.** "aab" and "abb" would match, so keep counts.
3. **Using 26 slots for Unicode.** Use a map keyed by code point.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One letter | `"a", "a"` | `true` |
| Different lengths | `"ab", "a"` | `false` |
| Same letters, different counts | `"aab", "abb"` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[017 — 49. Group Anagrams]]: Use the tile counts as a map key.
- [[055 — 438. Find All Anagrams in a String]]: Slide the tray along a longer string.

### In a real interview
Say this: *"Anagram means equal frequency counts, so I keep one balance array. For a large alphabet I would switch to a map."*

## 5. Recall
Q: 242. Valid Anagram: which pattern, and what is the insight?
A: Frequency count. Order does not matter, only how many of each letter there are.

Q: 242. Valid Anagram: what is the analogy, and what does it stand for?
A: Scrabble tiles on a tray: one pile per letter → one slot of a 26-long array; adding and removing a tile → `++` and `--`.

Q: 242. Valid Anagram: What does one slot of the balance array mean?
A: Occurrences in s so far minus occurrences in t so far.

Q: 242. Valid Anagram: what are the time and space costs?
A: Time O(n), one pass plus 26 checks. Space O(1), always 26 slots.

@@ 3 | TOPIC | 003 — 58. Length of Last Word | Easy · Scan from the end | NICE
# 003 — 58. Length of Last Word

**Problem 3 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Scan from the end**

[Open on LeetCode](https://leetcode.com/problems/length-of-last-word/)

## 1. Problem Statement
A string `s` contains words and spaces, possibly with extra spaces at either end, and at least one word. Return the length of the last word.

### Examples
```text
Input:  s = "Hello World"
Output: 5

Input:  s = "   fly me   to   the moon  "
Output: 4

Input:  s = "luffy is still joyboy"
Output: 6
```

### Constraints
```text
1 <= s.length <= 10^4
s consists of only English letters and spaces ' '.
There will be at least one word in s.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The last carriage of a train
To measure the last carriage you don't walk from the engine. Start at the tail, step over the empty track, and count until the next coupling.

| In the story | In the solution |
|---|---|
| empty track | trailing spaces |
| the coupling | the space before the last word |

### Pattern signal
**Scan from the end.** The answer sits at the end, so read backwards.

### Thought process
1. **First idea:** Split into words and take the last. It works, but allocates every word when one is needed.
2. **The insight:** The answer sits at the end, so read backwards.

### Optimal approach
1. Start at the last character.
2. Step left over spaces.
3. Count non-spaces.
4. Stop at a space or the start.

### Dry run
```text
s = "hi yo "
i  char   phase  length
5  space  skip   0
4  o      count  1
3  y      count  2
2  space  stop   return 2
```

### Why it works
After skipping, the pointer is on the final letter of the last word. The characters counted from there to the next space are exactly that word.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int lengthOfLastWord(String s) {
        int i = s.length() - 1;
        while (s.charAt(i) == ' ') i--; // a word is guaranteed
        int length = 0;
        while (i >= 0 && s.charAt(i) != ' ') {
            length++;
            i--;
        }
        return length;
    }
}
```

### Code walkthrough
- `s.length() - 1`: Stand at the tail of the train.
- `while (s.charAt(i) == ' ')`: Walk over empty track. The guaranteed word stops this loop.
- `i >= 0`: The word may begin the string, so test the bound before reading.

### Complexity
- **Time:** O(n) at worst
- **Space:** O(1), two integers

### Common mistakes
1. **Not skipping trailing spaces.** The answer becomes 0.
2. **Testing `i >= 0` after `charAt`.** It crashes on a single word, so test it first.
3. **Splitting on one space.** Repeated spaces create empty words.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One character | `"a"` | `1` |
| Trailing space | `"a "` | `1` |
| Leading space | `" abc"` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **151. Reverse Words in a String**: Repeat the backwards scan for every word.
- [[167 — 14. Longest Common Prefix]]: The same idea of scanning only as far as needed, from the front.

### In a real interview
Say this: *"Only the end matters, so I scan backwards and stop early. That needs no extra memory."*

## 5. Recall
Q: 58. Length of Last Word: which pattern, and what is the insight?
A: Scan from the end. The answer sits at the end, so read backwards.

Q: 58. Length of Last Word: what is the analogy, and what does it stand for?
A: The last carriage of a train: empty track → trailing spaces; the coupling → the space before the last word.

Q: 58. Length of Last Word: Why are two loops needed?
A: One skips trailing spaces, the other counts the word.

Q: 58. Length of Last Word: what are the time and space costs?
A: Time O(n) at worst. Space O(1), two integers.

@@ 3 | TOPIC | 004 — 1. Two Sum | Easy · Complement map | MUST
# 004 — 1. Two Sum

**Problem 4 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Complement map**

[Open on LeetCode](https://leetcode.com/problems/two-sum/)

## 1. Problem Statement
You get an array `nums` and an integer `target`. Return the indices of two different elements whose values add up to the target, in any order. Exactly one answer exists. Follow-up: beat O(n²).

### Examples
```text
Input:  nums = [2,7,11,15], target = 9
Output: [0,1]

Input:  nums = [3,2,4], target = 6
Output: [1,2]

Input:  nums = [3,3], target = 6
Output: [0,1]
```

### Constraints
```text
2 <= nums.length <= 10^4
-10^9 <= nums[i] <= 10^9
-10^9 <= target <= 10^9
Only one valid answer exists.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Finding a dance partner at the cloakroom
Everyone needs one particular partner. On arrival you work out who that is and ask the attendant "is this person here, and on which hook?" If not, you leave your own name and hook number for whoever needs you later.

| In the story | In the solution |
|---|---|
| the partner you need | `target - x` |
| the attendant's book | a map from value to index |

### Pattern signal
**Complement map.** The partner is not a mystery: it is exactly target minus x. A map finds it at once.

### Thought process
1. **First idea:** Try every pair, in O(n²). For each value you rescan the same numbers looking for one known value.
2. **The insight:** The partner is not a mystery: it is exactly target minus x. A map finds it at once.

### Optimal approach
1. Visit each value.
2. Compute the partner.
3. Return both indices if the map has it.
4. Otherwise store the value with its index.

### Dry run
```text
nums = [3,2,4], target = 6
i  x  partner  map before  result
0  3  3        {}          store 3:0
1  2  4        {3:0}       store 2:1
2  4  2        {3:0, 2:1}  found, return [1,2]
```

### Why it works
The map holds only earlier elements, so an element cannot pair with itself. When we reach the second element of the answer, the first is already stored.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> indexOf = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int partner = target - nums[i];
            Integer j = indexOf.get(partner); // ask first
            if (j != null) return new int[] {j, i};
            indexOf.put(nums[i], i);          // then register
        }
        return new int[0]; // not reached: an answer is guaranteed
    }
}
```

### Code walkthrough
- `target - nums[i]`: Names the only acceptable partner.
- `Integer j`: A boxed type, so that null can mean "not here". Index 0 is a real answer.
- `indexOf.put(nums[i], i)`: Register after asking, so you never meet yourself.

### Complexity
- **Time:** O(n) on average
- **Space:** O(n) for the map

### Common mistakes
1. **Storing before looking up.** A value of 3 with target 6 pairs with itself.
2. **Returning values.** The question wants indices.
3. **Sorting first.** The original indices are lost.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Equal values | `[3,3], 6` | `[0,1]` |
| Negatives | `[-1,-2], -3` | `[0,1]` |
| Zeros | `[0,4,0], 0` | `[0,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[045 — 167. Two Sum II - Input Array Is Sorted]]: Sorted input allows two pointers with no map.
- [[046 — 15. 3Sum]]: Fix one value and solve Two Sum on the rest.

### In a real interview
Say this: *"I turn searching for a pair into looking up a complement. One pass with a map gives O(n)."*

## 5. Recall
Q: 1. Two Sum: which pattern, and what is the insight?
A: Complement map. The partner is not a mystery: it is exactly target minus x. A map finds it at once.

Q: 1. Two Sum: what is the analogy, and what does it stand for?
A: Finding a dance partner at the cloakroom: the partner you need → `target - x`; the attendant's book → a map from value to index.

Q: 1. Two Sum: Why look up before storing?
A: So that an element cannot be its own partner.

Q: 1. Two Sum: what are the time and space costs?
A: Time O(n) on average. Space O(n) for the map.

@@ 3 | TOPIC | 005 — 27. Remove Element | Easy · In-place partition | NICE
# 005 — 27. Remove Element

**Problem 5 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: In-place partition**

[Open on LeetCode](https://leetcode.com/problems/remove-element/)

## 1. Problem Statement
Remove every copy of `val` from `nums` in place and return k, the number of values left. The first k positions must hold those values, in any order. Whatever follows does not matter.

### Examples
```text
Input:  nums = [3,2,2,3], val = 3
Output: 2, nums = [2,2,_,_]

Input:  nums = [0,1,2,2,3,0,4,2], val = 2
Output: 5, nums = [0,1,4,0,3,_,_,_]
```

### Constraints
```text
0 <= nums.length <= 100
0 <= nums[i] <= 50
0 <= val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Sorting a crate of fruit
Go through a crate from the left. When you find a rotten fruit, swap it with the one at the back and move the back wall inward. Inspect what you just received before moving on, because it may be rotten too.

| In the story | In the solution |
|---|---|
| the back wall | the `last` pointer |
| rotten fruit | an element equal to `val` |

### Pattern signal
**In-place partition.** Order is free, so a single swap can replace a whole shift.

### Thought process
1. **First idea:** Delete each match by shifting everything after it, in O(n²).
2. **The insight:** Order is free, so a single swap can replace a whole shift.

### Optimal approach
1. Put i at the front and last at the back.
2. On a match copy the last value into i and shrink last.
3. Otherwise advance i.
4. Return i.

### Dry run
```text
nums = [3,2,2,3], val = 3
i  last  nums[i]  action                 active part after
0  3     3        take 3 from back       [3,2,2]
0  2     3        take 2 from back       [2,2]
0  1     2        keep                   [2,2]
1  1     2        keep, i becomes 2      return 2
```

### Why it works
Everything before i is checked and good. Everything after last is discarded. The unknown middle shrinks by one each step.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int removeElement(int[] nums, int val) {
        int i = 0, last = nums.length - 1;
        while (i <= last) {
            if (nums[i] == val) {
                nums[i] = nums[last]; // replacement still needs a check
                last--;
            } else {
                i++;
            }
        }
        return i;
    }
}
```

### Code walkthrough
- `i <= last`: The element under both pointers is still unchecked.
- `nums[i] = nums[last]`: The discarded region is irrelevant, so a copy is enough.
- `return i`: The number of good fruits.

### Complexity
- **Time:** O(n), the middle shrinks every step
- **Space:** O(1), in place

### Common mistakes
1. **Advancing i after a replacement.** The new value may equal val.
2. **Looping with `i < last`.** The final element is never checked.
3. **Returning last.** It is one less than the count.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], 1` | `0` |
| All removed | `[4,4], 4` | `0` |
| None removed | `[1,2], 3` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[041 — 283. Move Zeroes]]: Order matters, so use slow and fast pointers.
- [[042 — 26. Remove Duplicates from Sorted Array]]: Keep a value when it differs from the previous one.

### In a real interview
Say this: *"Since order is irrelevant, I overwrite from the back instead of shifting. I recheck each replacement."*

## 5. Recall
Q: 27. Remove Element: which pattern, and what is the insight?
A: In-place partition. Order is free, so a single swap can replace a whole shift.

Q: 27. Remove Element: what is the analogy, and what does it stand for?
A: Sorting a crate of fruit: the back wall → the `last` pointer; rotten fruit → an element equal to `val`.

Q: 27. Remove Element: What is true of indices before i?
A: They are checked and contain no val.

Q: 27. Remove Element: what are the time and space costs?
A: Time O(n), the middle shrinks every step. Space O(1), in place.

@@ 3 | TOPIC | 006 — 205. Isomorphic Strings | Easy · Two-way mapping | NICE
# 006 — 205. Isomorphic Strings

**Problem 6 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Two-way mapping**

[Open on LeetCode](https://leetcode.com/problems/isomorphic-strings/)

## 1. Problem Statement
Strings `s` and `t` have equal length. Return true if the characters of `s` can be replaced to produce `t`. Every copy of a character gets the same replacement, and two characters cannot share one. A character may map to itself.

### Examples
```text
Input:  s = "egg", t = "add"
Output: true

Input:  s = "f11", t = "b23"
Output: false

Input:  s = "paper", t = "title"
Output: true
```

### Constraints
```text
1 <= s.length <= 5 * 10^4
t.length == s.length
s and t consist of any valid ascii character.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A secret code book
A spy's code book gives each letter one symbol. It must also decode without doubt, so two letters cannot share a symbol. You therefore check the encoding page and the decoding page.

| In the story | In the solution |
|---|---|
| the encoding page | map from s to t |
| the decoding page | map from t to s |

### Pattern signal
**Two-way mapping.** It is enough to remember the first promise in each direction and compare later pairs with it.

### Thought process
1. **First idea:** For every pair of positions check that equal in s means equal in t and vice versa, in O(n²).
2. **The insight:** It is enough to remember the first promise in each direction and compare later pairs with it.

### Optimal approach
1. Walk both strings together.
2. Write the pair on each page if absent.
3. Fail if either page already says something different.
4. Finish with true.

### Dry run
```text
s = "badc", t = "baba"
i  pair  encode says  decode says  result
0  b-b   new          new          write
1  a-a   new          new          write
2  d-b   new          b means b    conflict, return false
```

### Why it works
The encode page ensures one replacement per character and the decode page ensures no sharing. Together they are exactly the two rules.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isIsomorphic(String s, String t) {
        Map<Character, Character> encode = new HashMap<>();
        Map<Character, Character> decode = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char a = s.charAt(i), b = t.charAt(i);
            // putIfAbsent returns the earlier promise, or null
            Character oldB = encode.putIfAbsent(a, b);
            Character oldA = decode.putIfAbsent(b, a);
            if (oldB != null && oldB != b) return false;
            if (oldA != null && oldA != a) return false;
        }
        return true;
    }
}
```

### Code walkthrough
- `putIfAbsent`: Writes only on a blank line and returns what was there.
- `oldB != b`: A boxed value against a primitive compares by value.
- `oldA != a`: Catches two letters sharing a symbol.

### Complexity
- **Time:** O(n)
- **Space:** O(k) for k distinct characters, at most the alphabet

### Common mistakes
1. **One map only.** "ab" to "aa" wrongly passes.
2. **Comparing two boxed characters with `!=`.** That compares references, so keep one side primitive.
3. **Overwriting an old pair.** The conflict disappears.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Self mapping | `"a", "a"` | `true` |
| Shared symbol | `"ab", "aa"` | `false` |
| Changing symbol | `"aa", "ab"` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[012 — 290. Word Pattern]]: The symbols are whole words. The key idea there: Remember the first pairing in both directions.
- [[017 — 49. Group Anagrams]]: Another definition of "same shape", this time by counts.

### In a real interview
Say this: *"One-to-one means checking both directions. Two maps make each check O(1)."*

## 5. Recall
Q: 205. Isomorphic Strings: which pattern, and what is the insight?
A: Two-way mapping. It is enough to remember the first promise in each direction and compare later pairs with it.

Q: 205. Isomorphic Strings: what is the analogy, and what does it stand for?
A: A secret code book: the encoding page → map from s to t; the decoding page → map from t to s.

Q: 205. Isomorphic Strings: Why are two maps needed?
A: One prevents changing images, the other prevents shared images.

Q: 205. Isomorphic Strings: what are the time and space costs?
A: Time O(n). Space O(k) for k distinct characters, at most the alphabet.

@@ 3 | TOPIC | Practice — Review 01 | Re-solve without looking | MUST
# Review 01

Solve on a blank page. No notes, hints or solutions.

## 205. Isomorphic Strings
Decide whether characters of s map one-to-one onto t.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/isomorphic-strings/)

## 58. Length of Last Word
Return the length of the last word in a string.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/length-of-last-word/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 205 | | | | |
| 58 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[006 — 205. Isomorphic Strings]]
- [[003 — 58. Length of Last Word]]

@@ 3 | TOPIC | 007 — 605. Can Place Flowers | Easy · Greedy scan | NICE
# 007 — 605. Can Place Flowers

**Problem 7 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Greedy scan**

[Open on LeetCode](https://leetcode.com/problems/can-place-flowers/)

## 1. Problem Statement
`flowerbed` holds 0 for an empty plot and 1 for a planted one, with no two flowers adjacent. Return true if `n` more flowers can be planted without breaking that rule.

### Examples
```text
Input:  flowerbed = [1,0,0,0,1], n = 1
Output: true

Input:  flowerbed = [1,0,0,0,1], n = 2
Output: false
```

### Constraints
```text
1 <= flowerbed.length <= 2 * 10^4
flowerbed[i] is 0 or 1.
There are no two adjacent flowers in flowerbed.
0 <= n <= flowerbed.length
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Seats in a cinema
People want an empty seat on each side of them. An usher walks along the row and fills each seat as soon as it is legal. Waiting never helps, because a seat further right blocks more of the seats still to come.

| In the story | In the solution |
|---|---|
| the usher | one left-to-right loop |
| the wall at each end | a missing neighbor, treated as empty |

### Pattern signal
**Greedy scan.** The leftmost legal plot is always a safe choice, so there is nothing to search.

### Thought process
1. **First idea:** Try every subset of empty plots, which is exponential.
2. **The insight:** The leftmost legal plot is always a safe choice, so there is nothing to search.

### Optimal approach
1. Visit each plot.
2. Check it and both neighbors are empty, counting borders as empty.
3. Plant and reduce n.
4. Succeed when n is zero or less.

### Dry run
```text
flowerbed = [1,0,0,0,1], n = 1
i  bed          left empty  self empty  right empty  action
0  [1,0,0,0,1]  yes         no          yes          skip
1  [1,0,0,0,1]  no          yes         yes          skip
2  [1,0,0,0,1]  yes         yes         yes          plant, n=0, success
```

### Why it works
If an optimal plan skips the first legal plot, move its first later flower to that plot. It stays legal and loses nothing, so greedy is optimal.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        for (int i = 0; i < flowerbed.length && n > 0; i++) {
            boolean left = i == 0 || flowerbed[i - 1] == 0;
            boolean right = i == flowerbed.length - 1 || flowerbed[i + 1] == 0;
            if (left && right && flowerbed[i] == 0) {
                flowerbed[i] = 1; // the next plot must see this flower
                n--;
            }
        }
        return n <= 0;
    }
}
```

### Code walkthrough
- `i == 0`: The wall counts as an empty seat. Short-circuiting avoids an illegal read.
- `flowerbed[i] = 1`: The person is now seated and blocks the next seat.
- `n > 0`: Stop as soon as everyone is placed.

### Complexity
- **Time:** O(n) in the bed's length
- **Space:** O(1), it edits the input

### Common mistakes
1. **Not marking the new flower.** The next plot is planted too.
2. **Reading outside the bed.** Test the border first.
3. **Failing for n = 0.** Zero flowers always fit.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One empty plot | `[0], 1` | `true` |
| Both ends | `[0,0,0], 2` | `true` |
| Too many | `[0,0], 2` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[231 — 198. House Robber]]: Seats have different values, so greedy fails and DP is needed.
- [[289 — 435. Non-overlapping Intervals]]: Another earliest-safe-choice greedy. The key idea there: Minimizing removals is maximizing a compatible set, which earliest end solves.

### In a real interview
Say this: *"I plant at the first legal plot. An exchange argument shows that never reduces the total."*

## 5. Recall
Q: 605. Can Place Flowers: which pattern, and what is the insight?
A: Greedy scan. The leftmost legal plot is always a safe choice, so there is nothing to search.

Q: 605. Can Place Flowers: what is the analogy, and what does it stand for?
A: Seats in a cinema: the usher → one left-to-right loop; the wall at each end → a missing neighbor, treated as empty.

Q: 605. Can Place Flowers: Why is planting early safe?
A: Moving an optimal plan's first flower left keeps it legal and equally large.

Q: 605. Can Place Flowers: what are the time and space costs?
A: Time O(n) in the bed's length. Space O(1), it edits the input.

@@ 3 | TOPIC | 008 — 169. Majority Element | Easy · Boyer-Moore voting | NICE
# 008 — 169. Majority Element

**Problem 8 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Boyer-Moore voting**

[Open on LeetCode](https://leetcode.com/problems/majority-element/)

## 1. Problem Statement
An array of size n has a value that appears more than n/2 times. Return it. Follow-up: use linear time and O(1) space.

### Examples
```text
Input:  nums = [3,2,3]
Output: 3

Input:  nums = [2,2,1,1,1,2,2]
Output: 2
```

### Constraints
```text
n == nums.length
1 <= n <= 5 * 10^4
-10^9 <= nums[i] <= 10^9
The input is generated such that a majority element will exist in the array.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The last army standing
Soldiers from different armies walk onto a field. Whenever two from different armies meet, both leave. An army with more than half of all soldiers cannot be wiped out, even if all the others unite against it.

| In the story | In the solution |
|---|---|
| the army holding the field | the candidate |
| its soldiers still standing | the count |

### Pattern signal
**Boyer-Moore voting.** Removing two different values never destroys a majority, so pairs can cancel as you go.

### Thought process
1. **First idea:** Count every value with a map. It is O(n) time but also O(n) space.
2. **The insight:** Removing two different values never destroys a majority, so pairs can cancel as you go.

### Optimal approach
1. Start with count zero.
2. When it is zero adopt the current value.
3. Add one for a match.
4. Subtract one otherwise.
5. Return the candidate.

### Dry run
```text
nums = [2,2,1,1,1,2,2]
value  count before  candidate  count after
2      0             2          1
2      1             2          2
1      2             2          1
1      1             2          0
1      0             1          1
2      1             1          0
2      0             2          1
Answer: 2
```

### Why it works
Each cancellation removes at most one majority value together with at least one other value. The majority has more members than everyone else combined, so it survives.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int majorityElement(int[] nums) {
        int candidate = 0, count = 0;
        for (int x : nums) {
            if (count == 0) candidate = x; // the field is empty
            count += (x == candidate) ? 1 : -1;
        }
        return candidate;
    }
}
```

### Code walkthrough
- `count == 0`: The next soldier claims the empty field.
- `? 1 : -1`: A friend strengthens, an enemy cancels.
- `return candidate`: Needs no second count only because a majority is guaranteed.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Reading count as a frequency.** It is only the surplus after cancellations.
2. **Using it without a guarantee.** Add a verification pass.
3. **Changing candidate on every mismatch.** Change only at count zero.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One value | `[7]` | `7` |
| Negatives | `[-1,-1,2]` | `-1` |
| Majority at the end | `[1,2,2]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **229. Majority Element II**: Keep two armies and verify both.
- [[270 — 136. Single Number]]: Cancellation again, using XOR. The key idea there: XOR is that flip: x XOR x is 0, and order doesn't matter.

### In a real interview
Say this: *"I cancel unequal pairs, which a strict majority survives. Without the guarantee I would verify the winner."*

## 5. Recall
Q: 169. Majority Element: which pattern, and what is the insight?
A: Boyer-Moore voting. Removing two different values never destroys a majority, so pairs can cancel as you go.

Q: 169. Majority Element: what is the analogy, and what does it stand for?
A: The last army standing: the army holding the field → the candidate; its soldiers still standing → the count.

Q: 169. Majority Element: What does the count mean?
A: The candidate's surplus after cancellations, not its frequency.

Q: 169. Majority Element: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 009 — 724. Find Pivot Index | Easy · Prefix sum | NICE
# 009 — 724. Find Pivot Index

**Problem 9 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Prefix sum**

[Open on LeetCode](https://leetcode.com/problems/find-pivot-index/)

## 1. Problem Statement
Return the leftmost index where the sum of all values strictly to its left equals the sum strictly to its right. An empty side sums to 0. Return -1 if there is none.

### Examples
```text
Input:  nums = [1,7,3,6,5,6]
Output: 3

Input:  nums = [1,2,3]
Output: -1

Input:  nums = [2,1,-1]
Output: 0
```

### Constraints
```text
1 <= nums.length <= 10^4
-1000 <= nums[i] <= 1000
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The balance point of a plank
Weights stand in a line and you slide a support under them. You don't reweigh both sides at every stop. You know the total, so the right side is whatever is neither on the left nor on the support.

| In the story | In the solution |
|---|---|
| the support | index i |
| the weight already passed | `left` |

### Pattern signal
**Prefix sum.** One total and one running sum give both sides by subtraction.

### Thought process
1. **First idea:** Sum both sides for each index, in O(n²).
2. **The insight:** One total and one running sum give both sides by subtraction.

### Optimal approach
1. Compute the total.
2. Start left at zero.
3. Compute right as total minus left minus current.
4. Return on equality.
5. Otherwise add current to left.

### Dry run
```text
nums = [1,7,3,6,5,6], total = 28
i  value  left  right
0  1      0     27
1  7      1     20
2  3      8     17
3  6      11    11     return 3
```

### Why it works
At step i, left is exactly the sum before i. Every value is on the left, at i or on the right, so the subtraction is exact.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int pivotIndex(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;
        int left = 0;
        for (int i = 0; i < nums.length; i++) {
            int right = total - left - nums[i];
            if (left == right) return i; // the first is the leftmost
            left += nums[i];
        }
        return -1;
    }
}
```

### Code walkthrough
- `total += x`: Weigh the whole plank once.
- `total - left - nums[i]`: Excludes the weight on the support.
- `left += nums[i]`: Only after the check does it join the left.

### Complexity
- **Time:** O(n), two passes
- **Space:** O(1)

### Common mistakes
1. **Adding to left before checking.** The pivot gets counted.
2. **Stopping when left exceeds right.** Negatives can restore balance.
3. **Ignoring the ends.** An empty side is 0.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One value | `[4]` | `0` |
| Pivot at the end | `[-1,1,9]` | `2` |
| None | `[1,2]` | `-1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[019 — 238. Product of Array Except Self]]: Left and right products instead of sums.
- [[025 — 560. Subarray Sum Equals K]]: Compare the running sum with all earlier ones.

### In a real interview
Say this: *"A running left sum makes the right sum a subtraction. Negatives rule out early stopping."*

## 5. Recall
Q: 724. Find Pivot Index: which pattern, and what is the insight?
A: Prefix sum. One total and one running sum give both sides by subtraction.

Q: 724. Find Pivot Index: what is the analogy, and what does it stand for?
A: The balance point of a plank: the support → index i; the weight already passed → `left`.

Q: 724. Find Pivot Index: How is the right sum found in O(1)?
A: Total minus left minus the current value.

Q: 724. Find Pivot Index: what are the time and space costs?
A: Time O(n), two passes. Space O(1).

@@ 3 | TOPIC | 010 — 303. Range Sum Query - Immutable | Easy · Prefix sum | NICE
# 010 — 303. Range Sum Query - Immutable

**Problem 10 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Prefix sum**

[Open on LeetCode](https://leetcode.com/problems/range-sum-query-immutable/)

## 1. Problem Statement
Design `NumArray`. It is built from an integer array that never changes, and `sumRange(left, right)` returns the sum between those indices, both included. There can be many calls.

### Examples
```text
Input:  ["NumArray", "sumRange", "sumRange", "sumRange"]
        [[[-2, 0, 3, -5, 2, -1]], [0, 2], [2, 5], [0, 5]]
Output: [null, 1, -1, -3]
```

### Constraints
```text
1 <= nums.length <= 10^4
-10^5 <= nums[i] <= 10^5
0 <= left <= right < nums.length
At most 10^4 calls will be made to sumRange.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Kilometre stones on a highway
To know the distance between two towns you don't measure the road again. Each milestone shows the distance from the start, so you subtract two of them.

| In the story | In the solution |
|---|---|
| a milestone | a prefix sum |
| milestone zero | `prefix[0] = 0` |

### Pattern signal
**Prefix sum.** The array is frozen, so prepare the totals once.

### Thought process
1. **First idea:** Loop over the range on every call, about 100 million additions at the limits.
2. **The insight:** The array is frozen, so prepare the totals once.

### Optimal approach
1. Make an array one longer.
2. Store the sum of the first i values at position i.
3. Answer with prefix at right plus one minus prefix at left.

### Dry run
```text
nums = [-2,0,3,-5,2,-1]
position   0   1   2   3   4   5   6
prefix     0  -2  -2   1  -4  -2  -3
sum(0,2) = prefix[3] - prefix[0] = 1
sum(2,5) = prefix[6] - prefix[2] = -1
```

### Why it works
The prefix through right contains the range plus everything before left. Subtracting that earlier part leaves only the range.

## 3. Java Solution with Explanation
### Code
```java
class NumArray {
    private final int[] prefix; // prefix[i] = sum of the first i values

    public NumArray(int[] nums) {
        prefix = new int[nums.length + 1];
        for (int i = 0; i < nums.length; i++) prefix[i + 1] = prefix[i] + nums[i];
    }

    public int sumRange(int left, int right) {
        return prefix[right + 1] - prefix[left];
    }
}
```

### Code walkthrough
- `nums.length + 1`: Room for milestone zero, so left = 0 needs no special case.
- `prefix[i] + nums[i]`: The previous stone plus this stretch of road.
- `prefix[right + 1]`: Plus one because right is included.

### Complexity
- **Time:** O(n) to build, O(1) per query
- **Space:** O(n) for the stones

### Common mistakes
1. **Using `prefix[right]`.** It leaves out the last value.
2. **Overwriting the input.** The caller's array is damaged.
3. **Using this with updates.** One update costs O(n), so use a Fenwick tree.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Single value | `[5], sum(0,0)` | `5` |
| From the start | `[1,2], sum(0,1)` | `3` |
| Cancelling | `[3,-3], sum(0,1)` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[032 — 304. Range Sum Query 2D - Immutable]]: Milestones on a map, using four corners.
- **307. Range Sum Query Mutable**: Updates need a Fenwick or segment tree.

### In a real interview
Say this: *"Repeated queries on fixed data justify preprocessing. Prefix sums turn each range into one subtraction."*

## 5. Recall
Q: 303. Range Sum Query - Immutable: which pattern, and what is the insight?
A: Prefix sum. The array is frozen, so prepare the totals once.

Q: 303. Range Sum Query - Immutable: what is the analogy, and what does it stand for?
A: Kilometre stones on a highway: a milestone → a prefix sum; milestone zero → `prefix[0] = 0`.

Q: 303. Range Sum Query - Immutable: What does prefix at position i store?
A: The sum of the first i values.

Q: 303. Range Sum Query - Immutable: what are the time and space costs?
A: Time O(n) to build, O(1) per query. Space O(n) for the stones.

@@ 3 | TOPIC | 011 — 448. Find All Numbers Disappeared in an Array | Easy · Index as bucket | NICE
# 011 — 448. Find All Numbers Disappeared in an Array

**Problem 11 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Index as bucket**

[Open on LeetCode](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/)

## 1. Problem Statement
An array of length n holds values from 1 to n, some repeated. Return every number in 1 to n that does not appear. Follow-up: O(n) time and no extra space beyond the result.

### Examples
```text
Input:  nums = [4,3,2,7,8,2,3,1]
Output: [5,6]

Input:  nums = [1,1]
Output: [2]
```

### Constraints
```text
n == nums.length
1 <= n <= 10^5
1 <= nums[i] <= n
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Numbered coat hooks
Coats and hooks are both numbered 1 to n. Carry each coat to its hook, swapping with the coat already there. At the end, a hook with the wrong coat belongs to a missing number.

| In the story | In the solution |
|---|---|
| hook number v | index v - 1 |
| coat already home | `nums[v - 1] == v` |

### Pattern signal
**Index as bucket.** Values and indices cover the same range, so the array can be its own set.

### Thought process
1. **First idea:** A set of seen values works, but uses O(n) extra space.
2. **The insight:** Values and indices cover the same range, so the array can be its own set.

### Optimal approach
1. At each index keep swapping its value home.
2. Stop if that home already holds the same value.
3. Scan again.
4. Report mismatched indices plus one.

### Dry run
```text
nums = [3,1,3]
i  action                         array
0  send 3 home, where 3 already is  [3,1,3]   stop
1  send 1 to index 0                [1,3,3]
1  send 3 home, where 3 already is  [1,3,3]   stop
2  already home                     [1,3,3]
Scan: index 1 holds 3, so 2 is missing
```

### Why it works
Each value that exists ends at its home and is never moved away. A mismatched home therefore proves its number is absent.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> findDisappearedNumbers(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            while (nums[nums[i] - 1] != nums[i]) {
                int v = nums[i];
                nums[i] = nums[v - 1];
                nums[v - 1] = v; // v is now home
            }
        }
        List<Integer> missing = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) missing.add(i + 1);
        }
        return missing;
    }
}
```

### Code walkthrough
- `while`: The coat received in a swap may also need carrying.
- `int v = nums[i]`: Save it, because it decides the destination.
- `i + 1`: The number printed on that hook.

### Complexity
- **Time:** O(n), each swap settles a value for good
- **Space:** O(1) besides the result

### Common mistakes
1. **Using `if` for `while`.** The received value stays misplaced.
2. **Testing `nums[i] != i + 1` to continue swapping.** Duplicates loop forever.
3. **Recomputing the target after changing `nums[i]`.** Save v first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One value | `[1]` | `[]` |
| All the same | `[2,2]` | `[1]` |
| Reversed | `[3,2,1]` | `[]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[033 — 41. First Missing Positive]]: The same hooks, ignoring coats out of range.
- [[274 — 268. Missing Number]]: Exactly one is missing, so a sum is enough.

### In a real interview
Say this: *"The range 1 to n lets indices act as buckets. Every swap puts a value home, so the nested loop is linear."*

## 5. Recall
Q: 448. Find All Numbers Disappeared in an Array: which pattern, and what is the insight?
A: Index as bucket. Values and indices cover the same range, so the array can be its own set.

Q: 448. Find All Numbers Disappeared in an Array: what is the analogy, and what does it stand for?
A: Numbered coat hooks: hook number v → index v - 1; coat already home → `nums[v - 1] == v`.

Q: 448. Find All Numbers Disappeared in an Array: Why is the nested loop linear?
A: Each swap settles one value permanently, so there are at most n swaps.

Q: 448. Find All Numbers Disappeared in an Array: what are the time and space costs?
A: Time O(n), each swap settles a value for good. Space O(1) besides the result.

@@ 3 | TOPIC | 012 — 290. Word Pattern | Easy · Two-way mapping | NICE
# 012 — 290. Word Pattern

**Problem 12 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Two-way mapping**

[Open on LeetCode](https://leetcode.com/problems/word-pattern/)

## 1. Problem Statement
You get a `pattern` of letters and a string `s` of words separated by single spaces. Return true if letters and words correspond one-to-one, in order.

### Examples
```text
Input:  pattern = "abba", s = "dog cat cat dog"
Output: true

Input:  pattern = "abba", s = "dog cat cat fish"
Output: false

Input:  pattern = "aaaa", s = "dog cat cat dog"
Output: false
```

### Constraints
```text
1 <= pattern.length <= 300
pattern contains only lower-case English letters.
1 <= s.length <= 3000
s contains only lowercase English letters and spaces ' '.
s does not contain any leading or trailing spaces.
All the words in s are separated by a single space.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Nicknames in a class
Each nickname belongs to one student and each student has one nickname. The teacher reads both lists side by side and objects if either side changes partner.

| In the story | In the solution |
|---|---|
| nickname | a pattern letter |
| student | a word |

### Pattern signal
**Two-way mapping.** Remember the first pairing in both directions.

### Thought process
1. **First idea:** Compare every pair of positions for matching equality, in O(n²) word comparisons.
2. **The insight:** Remember the first pairing in both directions.

### Optimal approach
1. Split into words.
2. Compare the two counts.
3. Record each pair in both maps.
4. Fail on any disagreement.

### Dry run
```text
pattern = "abba", words = dog dog dog dog
i  letter  word  letter's old word  word's old letter  result
0  a       dog   none               none               record
1  b       dog   none               a                  conflict, return false
```

### Why it works
One map gives each letter a single word and the other gives each word a single letter. With equal lengths, nothing is left unpaired.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean wordPattern(String pattern, String s) {
        String[] words = s.split(" ");
        if (words.length != pattern.length()) return false;
        Map<Character, String> wordOf = new HashMap<>();
        Map<String, Character> letterOf = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            char c = pattern.charAt(i);
            String oldWord = wordOf.putIfAbsent(c, words[i]);
            Character oldLetter = letterOf.putIfAbsent(words[i], c);
            if (oldWord != null && !oldWord.equals(words[i])) return false;
            if (oldLetter != null && oldLetter != c) return false;
        }
        return true;
    }
}
```

### Code walkthrough
- `s.split(" ")`: Safe because the separators are single spaces.
- `words.length != pattern.length()`: Both class lists must be equally long.
- `oldWord.equals`: Compares text, where `!=` would compare objects.

### Complexity
- **Time:** O(L) for L total characters
- **Space:** O(L) for words and maps

### Common mistakes
1. **One map.** Two letters can share a word.
2. **Skipping the count check.** Extra words are ignored or the loop crashes.
3. **Comparing strings with `==`.** Use equals.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One pair | `"a", "dog"` | `true` |
| Count differs | `"aa", "dog"` | `false` |
| Shared word | `"ab", "dog dog"` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[006 — 205. Isomorphic Strings]]: Both sides are characters. The key idea there: It is enough to remember the first promise in each direction and compare later pairs with it.
- **291. Word Pattern II**: No spaces are given, so backtracking chooses the splits.

### In a real interview
Say this: *"This is a bijection between tokens. I check the lengths and then both directions."*

## 5. Recall
Q: 290. Word Pattern: which pattern, and what is the insight?
A: Two-way mapping. Remember the first pairing in both directions.

Q: 290. Word Pattern: what is the analogy, and what does it stand for?
A: Nicknames in a class: nickname → a pattern letter; student → a word.

Q: 290. Word Pattern: What must be checked before pairing?
A: That the number of words equals the pattern length.

Q: 290. Word Pattern: what are the time and space costs?
A: Time O(L) for L total characters. Space O(L) for words and maps.

@@ 3 | TOPIC | Practice — Review 02 | Re-solve without looking | MUST
# Review 02

Solve on a blank page. No notes, hints or solutions.

## 290. Word Pattern
Decide whether words follow a letter pattern.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/word-pattern/)

## 724. Find Pivot Index
Find the first index whose left sum equals its right sum.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-pivot-index/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 290 | | | | |
| 724 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[012 — 290. Word Pattern]]
- [[009 — 724. Find Pivot Index]]

@@ 3 | TOPIC | 013 — 705. Design HashSet | Easy · Direct addressing | NICE
# 013 — 705. Design HashSet

**Problem 13 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Direct addressing**

[Open on LeetCode](https://leetcode.com/problems/design-hashset/)

## 1. Problem Statement
Design `MyHashSet` without built-in hash tables, with `add(key)`, `remove(key)` and `contains(key)`. Keys lie between 0 and 1,000,000.

### Examples
```text
Input:  ["MyHashSet", "add", "add", "contains", "contains", "add", "contains", "remove", "contains"]
        [[], [1], [2], [1], [3], [2], [2], [2], [2]]
Output: [null, null, null, true, false, null, true, null, false]
```

### Constraints
```text
0 <= key <= 10^6
At most 10^4 calls will be made to add, remove, and contains.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A wall of light switches
Give every possible key its own switch. Adding turns it on, removing turns it off, checking is a glance. No searching, but you pay for switches nobody uses.

| In the story | In the solution |
|---|---|
| switch number | the key as an index |
| on | true |

### Pattern signal
**Direct addressing.** The key range is small and known, so the key can be the address.

### Thought process
1. **First idea:** Keep a list and scan it for each call, in O(n) per operation.
2. **The insight:** The key range is small and known, so the key can be the address.

### Optimal approach
1. Allocate one boolean for every possible key.
2. Set it on add.
3. Clear it on remove.
4. Read it on contains.

### Dry run
```text
operation      switch 1  switch 2  returns
add(1)         on        off       -
add(2)         on        on        -
contains(1)    on        on        true
remove(2)      on        off       -
contains(2)    on        off       false
```

### Why it works
Each key owns one slot that no other key can touch. It therefore always reflects the last add or remove of that key.

## 3. Java Solution with Explanation
### Code
```java
class MyHashSet {
    private static final int MAX_KEY = 1_000_000;
    private final boolean[] present = new boolean[MAX_KEY + 1];

    public void add(int key) {
        present[key] = true; // repeating this is harmless
    }

    public void remove(int key) {
        present[key] = false;
    }

    public boolean contains(int key) {
        return present[key];
    }
}
```

### Code walkthrough
- `MAX_KEY + 1`: Both 0 and the maximum need a switch.
- `present[key] = true`: A set ignores repeats, and so does a switch.
- `return present[key]`: The address is computed, never searched.

### Complexity
- **Time:** O(1) per operation after allocation
- **Space:** O(U) for a key range of U

### Common mistakes
1. **Allocating exactly MAX_KEY.** The largest key crashes.
2. **A static array.** Different sets would share data.
3. **Using it for any int.** Four billion switches is too many, so hash into buckets.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Smallest key | `add(0), contains(0)` | `true` |
| Largest key | `add(1000000), contains(1000000)` | `true` |
| Remove absent | `remove(5), contains(5)` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[014 — 706. Design HashMap]]: Values and collisions, handled with buckets.
- [[001 — 217. Contains Duplicate]]: The set used as memory. The key idea there: The only question at each value is "have I seen you?", so remember values in a structure that answers it instantly.

### In a real interview
Say this: *"A bounded range allows direct addressing. For unbounded keys I would use hashing with chains and resizing."*

## 5. Recall
Q: 705. Design HashSet: which pattern, and what is the insight?
A: Direct addressing. The key range is small and known, so the key can be the address.

Q: 705. Design HashSet: what is the analogy, and what does it stand for?
A: A wall of light switches: switch number → the key as an index; on → true.

Q: 705. Design HashSet: When is direct addressing unsuitable?
A: When the key range is huge or unbounded.

Q: 705. Design HashSet: what are the time and space costs?
A: Time O(1) per operation after allocation. Space O(U) for a key range of U.

@@ 3 | TOPIC | 014 — 706. Design HashMap | Easy · Separate chaining | NICE
# 014 — 706. Design HashMap

**Problem 14 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Separate chaining**

[Open on LeetCode](https://leetcode.com/problems/design-hashmap/)

## 1. Problem Statement
Design `MyHashMap` without built-in hash tables. `put(key, value)` inserts or updates, `get(key)` returns the value or -1, and `remove(key)` deletes the key if present.

### Examples
```text
Input:  ["MyHashMap", "put", "put", "get", "get", "put", "get", "remove", "get"]
        [[], [1, 1], [2, 2], [1], [3], [2, 1], [2], [2], [2]]
Output: [null, null, null, 1, -1, null, 1, null, -1]
```

### Constraints
```text
0 <= key, value <= 10^6
At most 10^4 calls will be made to put, get, and remove.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Apartment mailboxes
A formula sends each surname to one of about a thousand mailboxes. Neighbors can share a box, so the letters inside are clipped in a short chain, each with a name on it. You open one box and read a few names, not the whole building's mail.

| In the story | In the solution |
|---|---|
| choosing the box | the hash |
| clipped letters | a linked chain |
| the empty clip at the front | a dummy head |

### Pattern signal
**Separate chaining.** Split that list into many short ones and compute which to open.

### Thought process
1. **First idea:** One long list of pairs costs O(n) per operation.
2. **The insight:** Split that list into many short ones and compute which to open.

### Optimal approach
1. Hash the key to a bucket.
2. Find the node before the key's node.
3. Update or append for put.
4. Read for get.
5. Unlink for remove.

### Dry run
```text
1009 buckets, so keys 1 and 1010 share bucket 1
operation        bucket 1            returns
put(1,10)        head -> 1:10        -
put(1010,20)     head -> 1:10 -> 1010:20  -
get(1010)        unchanged           20
remove(1)        head -> 1010:20     -
```

### Why it works
A key always hashes to the same bucket, and put updates before appending. So each key has at most one node, exactly where lookups search.

## 3. Java Solution with Explanation
### Code
```java
class MyHashMap {
    private static class Entry {
        int key, value;
        Entry next;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final Entry[] heads = new Entry[1009];

    // The node before key, or the tail when key is absent.
    private Entry before(int key) {
        int b = key % heads.length;
        if (heads[b] == null) heads[b] = new Entry(-1, -1); // dummy
        Entry node = heads[b];
        while (node.next != null && node.next.key != key) node = node.next;
        return node;
    }

    public void put(int key, int value) {
        Entry p = before(key);
        if (p.next == null) p.next = new Entry(key, value);
        else p.next.value = value;
    }

    public int get(int key) {
        Entry p = before(key);
        return p.next == null ? -1 : p.next.value;
    }

    public void remove(int key) {
        Entry p = before(key);
        if (p.next != null) p.next = p.next.next;
    }
}
```

### Code walkthrough
- `key % heads.length`: Picks the mailbox. Keys are non-negative here.
- `new Entry(-1, -1)`: The empty clip, so the first letter is not a special case.
- `before(key)`: One search serves all three operations.
- `p.next = p.next.next`: Unclips one letter.

### Complexity
- **Time:** O(1 + n/1009) on average, O(n) if all collide
- **Space:** O(1009 + n)

### Common mistakes
1. **Treating equal hashes as equal keys.** Always compare the key.
2. **Appending on every put.** An update must replace the value.
3. **Plain `%` with negative keys.** The index becomes negative, so use `Math.floorMod`.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Absent | `get(5)` | `-1` |
| Update | `put(1,1), put(1,2), get(1)` | `2` |
| Zero | `put(0,0), get(0)` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[013 — 705. Design HashSet]]: Chains without values. The key idea there: The key range is small and known, so the key can be the address.
- [[276 — 146. LRU Cache]]: Adds a second linked list for recency.

### In a real interview
Say this: *"Hashing selects a short chain and key comparison resolves collisions. In production I would resize by load factor."*

## 5. Recall
Q: 706. Design HashMap: which pattern, and what is the insight?
A: Separate chaining. Split that list into many short ones and compute which to open.

Q: 706. Design HashMap: what is the analogy, and what does it stand for?
A: Apartment mailboxes: choosing the box → the hash; clipped letters → a linked chain; the empty clip at the front → a dummy head.

Q: 706. Design HashMap: How are collisions handled?
A: Colliding entries share a chain and are told apart by key.

Q: 706. Design HashMap: what are the time and space costs?
A: Time O(1 + n/1009) on average, O(n) if all collide. Space O(1009 + n).

@@ 3 | TOPIC | 015 — 28. Find the Index of the First Occurrence in a String | Easy · KMP prefix function | NICE
# 015 — 28. Find the Index of the First Occurrence in a String

**Problem 15 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: KMP prefix function**

[Open on LeetCode](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/)

## 1. Problem Statement
Return the index of the first occurrence of `needle` in `haystack`, or -1 if it does not occur.

### Examples
```text
Input:  haystack = "sadbutsad", needle = "sad"
Output: 0

Input:  haystack = "leetcode", needle = "leeto"
Output: -1
```

### Constraints
```text
1 <= haystack.length, needle.length <= 10^4
haystack and needle consist of only lowercase English characters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Singing along to a half-remembered song
You have matched "la-li-la-li" and the next word is wrong. You don't restart: the last "la-li" you heard is also how the song begins, so you carry on from there. KMP prepares that "where can I carry on?" note for every position.

| In the story | In the solution |
|---|---|
| the song | the needle |
| the note | the prefix table `fallback` |

### Pattern signal
**KMP prefix function.** The matched text equals a needle prefix, so the needle alone tells how much of it can be reused.

### Thought process
1. **First idea:** Compare the needle from every start, in O(n·m). After a mismatch it rereads known characters.
2. **The insight:** The matched text equals a needle prefix, so the needle alone tells how much of it can be reused.

### Optimal approach
1. For each needle prefix store its longest proper prefix that is also a suffix.
2. Scan the haystack.
3. On mismatch fall back in the needle.
4. On a full match return the start.

### Dry run
```text
haystack = "abababd", needle = "ababd", fallback = [0,0,1,2,0]
i  char  j before  action                        j after
0  a     0         match                         1
1  b     1         match                         2
2  a     2         match                         3
3  b     3         match                         4
4  a     4         d differs, j=2, then match    3
5  b     3         match                         4
6  d     4         match                         5, return 6-5+1 = 2
```

### Why it works
After each character, j is the longest needle prefix ending there. Falling back tries the next longest possible prefix, so no occurrence is skipped.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int strStr(String haystack, String needle) {
        int m = needle.length();
        int[] fallback = new int[m];
        // Match the needle against itself.
        for (int i = 1, j = 0; i < m; i++) {
            while (j > 0 && needle.charAt(i) != needle.charAt(j)) j = fallback[j - 1];
            if (needle.charAt(i) == needle.charAt(j)) j++;
            fallback[i] = j;
        }
        // Then match it against the haystack.
        for (int i = 0, j = 0; i < haystack.length(); i++) {
            while (j > 0 && haystack.charAt(i) != needle.charAt(j)) j = fallback[j - 1];
            if (haystack.charAt(i) == needle.charAt(j)) j++;
            if (j == m) return i - m + 1;
        }
        return -1;
    }
}
```

### Code walkthrough
- `fallback[i] = j`: Writes the note: "this much of the opening is already sung".
- `j = fallback[j - 1]`: Uses the note for the matched part, which ends at j - 1.
- `i - m + 1`: Converts the match's end to its start.

### Complexity
- **Time:** O(n + m), j can fall only as often as it rose
- **Space:** O(m) for the table

### Common mistakes
1. **Resetting j to zero.** Overlapping matches are missed.
2. **Moving i backwards.** The linear bound is lost.
3. **Reading `fallback[j]` on mismatch.** The matched part ends at j - 1.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Equal strings | `"a", "a"` | `0` |
| Longer needle | `"a", "ab"` | `-1` |
| Overlap | `"aaab", "aab"` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **459. Repeated Substring Pattern**: The last table value reveals the repeated block.
- **1392. Longest Happy Prefix**: The answer is the last table value.

### In a real interview
Say this: *"I would state the simple O(n·m) scan first. KMP avoids rereading by reusing the needle's self-overlap."*

## 5. Recall
Q: 28. Find the Index of the First Occurrence in a String: which pattern, and what is the insight?
A: KMP prefix function. The matched text equals a needle prefix, so the needle alone tells how much of it can be reused.

Q: 28. Find the Index of the First Occurrence in a String: what is the analogy, and what does it stand for?
A: Singing along to a half-remembered song: the song → the needle; the note → the prefix table `fallback`.

Q: 28. Find the Index of the First Occurrence in a String: What does the fallback table store?
A: The longest proper prefix that is also a suffix, for each needle prefix.

Q: 28. Find the Index of the First Occurrence in a String: what are the time and space costs?
A: Time O(n + m), j can fall only as often as it rose. Space O(m) for the table.

@@ 3 | TOPIC | 016 — 2215. Find the Difference of Two Arrays | Easy · Set difference | NICE
# 016 — 2215. Find the Difference of Two Arrays

**Problem 16 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Set difference**

[Open on LeetCode](https://leetcode.com/problems/find-the-difference-of-two-arrays/)

## 1. Problem Statement
You get `nums1` and `nums2`. Return two lists: the distinct values of the first that are absent from the second, and the distinct values of the second absent from the first. Any order is fine.

### Examples
```text
Input:  nums1 = [1,2,3], nums2 = [2,4,6]
Output: [[1,3],[4,6]]

Input:  nums1 = [1,2,3,3], nums2 = [1,1,2,2]
Output: [[3],[]]
```

### Constraints
```text
1 <= nums1.length, nums2.length <= 1000
-1000 <= nums1[i], nums2[i] <= 1000
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two friends comparing stamp albums
Each first throws out their own doubles. Then each crosses off every stamp the other also has. What is left is unique to that album.

| In the story | In the solution |
|---|---|
| throwing out doubles | building a set |
| crossing off | `removeAll` |

### Pattern signal
**Set difference.** A set solves both problems: distinct values and instant membership.

### Thought process
1. **First idea:** Search the other array for each value, in O(n·m), and remove repeats afterwards.
2. **The insight:** A set solves both problems: distinct values and instant membership.

### Optimal approach
1. Build a set for each array.
2. Copy the first and remove the second.
3. Remove the original first from the second.
4. Return both.

### Dry run
```text
nums1 = [1,2,3,3], nums2 = [1,1,2,2]
step               first    second   only in first
build sets         {1,2,3}  {1,2}    -
copy minus second  {1,2,3}  {1,2}    {3}
second minus first {1,2,3}  {}       {3}
Answer: [[3],[]]
```

### Why it works
A value stays only if it was in its own array and not in the other, which is the definition. Sets keep one copy.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> findDifference(int[] nums1, int[] nums2) {
        Set<Integer> first = new HashSet<>(), second = new HashSet<>();
        for (int x : nums1) first.add(x);
        for (int x : nums2) second.add(x);
        Set<Integer> onlyFirst = new HashSet<>(first);
        onlyFirst.removeAll(second);
        second.removeAll(first); // first is still complete
        return List.of(new ArrayList<>(onlyFirst), new ArrayList<>(second));
    }
}
```

### Code walkthrough
- `new HashSet<>(first)`: A copy, so the first album is still whole for the second comparison.
- `removeAll`: Crosses off everything shared.
- `List.of`: Keeps the required order of the two answers.

### Complexity
- **Time:** O(n + m) on average
- **Space:** O(n + m)

### Common mistakes
1. **Shrinking first before using it.** Shared values survive in second.
2. **Membership in a list.** Each check becomes O(n).
3. **Keeping repeats.** Build sets first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Identical | `[1], [1]` | `[[],[]]` |
| Disjoint | `[1], [2]` | `[[1],[2]]` |
| Repeats | `[1,1], [2]` | `[[1],[2]]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **349. Intersection of Two Arrays**: Keep what is shared, with retainAll.
- [[001 — 217. Contains Duplicate]]: Membership within one array. The key idea there: The only question at each value is "have I seen you?", so remember values in a structure that answers it instantly.

### In a real interview
Say this: *"Distinctness and fast membership both point to sets. I copy one before subtracting."*

## 5. Recall
Q: 2215. Find the Difference of Two Arrays: which pattern, and what is the insight?
A: Set difference. A set solves both problems: distinct values and instant membership.

Q: 2215. Find the Difference of Two Arrays: what is the analogy, and what does it stand for?
A: Two friends comparing stamp albums: throwing out doubles → building a set; crossing off → `removeAll`.

Q: 2215. Find the Difference of Two Arrays: Why copy the first set?
A: The complete original is needed to filter the second.

Q: 2215. Find the Difference of Two Arrays: what are the time and space costs?
A: Time O(n + m) on average. Space O(n + m).

@@ 2 | MODULE | Arrays & Hashing — Medium | 16 problems | MUST
# Arrays & Hashing: Medium

Patterns in this group: Canonical key, Merge sort, Prefix and suffix product, Seen sets, Sequence starts, ID map, Prefix frequency, Greedy gains, Prefix sum map, Middle character, Prefix and minimax, Custom order, Prefix remainder, Substring set, Map plus list, 2D prefix sum.

@@ 3 | TOPIC | 017 — 49. Group Anagrams | Medium · Canonical key | MUST
# 017 — 49. Group Anagrams

**Problem 17 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Canonical key**

[Open on LeetCode](https://leetcode.com/problems/group-anagrams/)

## 1. Problem Statement
Group the strings of `strs` that are anagrams of each other. Groups and their contents may be in any order.

### Examples
```text
Input:  strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["bat"],["nat","tan"],["ate","eat","tea"]]

Input:  strs = [""]
Output: [[""]]

Input:  strs = ["a"]
Output: [["a"]]
```

### Constraints
```text
1 <= strs.length <= 10^4
0 <= strs[i].length <= 100
strs[i] consists of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The post office pigeonholes
A clerk doesn't compare each letter with every other. They work out a code from it and drop it into that pigeonhole. Here the code is the word's tile inventory, which all its anagrams share.

| In the story | In the solution |
|---|---|
| the code | the letter counts as text |
| pigeonhole | the list stored under that key |

### Pattern signal
**Canonical key.** Compute a label that is equal exactly when two words are anagrams.

### Thought process
1. **First idea:** Compare each word with every group's representative, up to O(n²·k).
2. **The insight:** Compute a label that is equal exactly when two words are anagrams.

### Optimal approach
1. Count each word's 26 letters.
2. Turn the counts into a key.
3. Append the word under that key.
4. Return the lists.

### Dry run
```text
strs = [eat, tea, tan, ate, nat, bat], keys abbreviated to non-zero counts
word  key       that group after
eat   a1 e1 t1  [eat]
tea   a1 e1 t1  [eat,tea]
tan   a1 n1 t1  [tan]
ate   a1 e1 t1  [eat,tea,ate]
nat   a1 n1 t1  [tan,nat]
bat   a1 b1 t1  [bat]
```

### Why it works
Anagrams have equal counts, so equal keys. Different words that are not anagrams differ in some count, so their keys differ.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : strs) {
            int[] count = new int[26];
            for (char c : word.toCharArray()) count[c - 'a']++;
            String key = Arrays.toString(count); // equal content, equal key
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groups.values());
    }
}
```

### Code walkthrough
- `new int[26]`: A fresh inventory for each word.
- `Arrays.toString(count)`: Arrays hash by identity, strings by content.
- `computeIfAbsent`: Opens the pigeonhole on first use.

### Complexity
- **Time:** O(n·k) for n words of length k
- **Space:** O(n·k)

### Common mistakes
1. **An array as the key.** Equal arrays do not match.
2. **Counts joined without separators.** 1,11 and 11,1 collide.
3. **Reusing one count array.** The letters accumulate.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty word | `[""]` | `[ [""] ]` |
| Repeated word | `["a","a"]` | `[ ["a","a"] ]` |
| Different counts | `["ab","abb"]` | `[["ab"],["abb"]]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[002 — 242. Valid Anagram]]: Compare two inventories. The key idea there: Order does not matter, only how many of each letter there are.
- **249. Group Shifted Strings**: The label is the gaps between letters.

### In a real interview
Say this: *"Grouping by equivalence calls for a canonical key. Counts avoid sorting each word."*

## 5. Recall
Q: 49. Group Anagrams: which pattern, and what is the insight?
A: Canonical key. Compute a label that is equal exactly when two words are anagrams.

Q: 49. Group Anagrams: what is the analogy, and what does it stand for?
A: The post office pigeonholes: the code → the letter counts as text; pigeonhole → the list stored under that key.

Q: 49. Group Anagrams: What makes a good grouping key?
A: It is equal exactly when two items belong together.

Q: 49. Group Anagrams: what are the time and space costs?
A: Time O(n·k) for n words of length k. Space O(n·k).

@@ 3 | TOPIC | 018 — 912. Sort an Array | Medium · Merge sort | SHOULD
# 018 — 912. Sort an Array

**Problem 18 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Merge sort**

[Open on LeetCode](https://leetcode.com/problems/sort-an-array/)

## 1. Problem Statement
Sort `nums` in ascending order without a built-in sort, in O(n log n) time and with as little extra space as practical.

### Examples
```text
Input:  nums = [5,2,3,1]
Output: [1,2,3,5]

Input:  nums = [5,1,1,2,0,0]
Output: [0,0,1,1,2,5]
```

### Constraints
```text
1 <= nums.length <= 5 * 10^4
-5 * 10^4 <= nums[i] <= 5 * 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Teachers merging exam piles
One teacher cannot order 1,000 papers quickly, so halves go to assistants, who split again until each holds one paper. Merging two ordered piles is easy: repeatedly take the smaller top paper.

| In the story | In the solution |
|---|---|
| handing out halves | recursion |
| the table for merging | a temporary array |

### Pattern signal
**Merge sort.** One paper is already sorted, and two sorted piles merge in linear time.

### Thought process
1. **First idea:** Insertion or bubble sort takes O(n²), about 2.5 billion steps at the limit.
2. **The insight:** One paper is already sorted, and two sorted piles merge in linear time.

### Optimal approach
1. Stop at one element.
2. Split at the middle.
3. Sort both halves.
4. Repeatedly copy the smaller front value.
5. Copy the leftovers.
6. Write back.

### Dry run
```text
nums = [5,2,3,1]
split:          [5,2]        [3,1]
split again:    [5] [2]      [3] [1]
merge pairs:    [2,5]        [1,3]
final merge:    take 1, take 2, take 3, leftover 5
result:         [1,2,3,5]
```

### Why it works
Single elements are sorted. If both halves are sorted, the smaller front is the smallest value remaining, so every merge is sorted too.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] sortArray(int[] nums) {
        sort(nums, new int[nums.length], 0, nums.length);
        return nums;
    }

    // Sorts [from, to): 'to' is excluded.
    private void sort(int[] a, int[] temp, int from, int to) {
        if (to - from <= 1) return;
        int mid = from + (to - from) / 2;
        sort(a, temp, from, mid);
        sort(a, temp, mid, to);
        int i = from, j = mid, w = from;
        while (i < mid && j < to) temp[w++] = a[i] <= a[j] ? a[i++] : a[j++];
        while (i < mid) temp[w++] = a[i++];
        while (j < to) temp[w++] = a[j++];
        System.arraycopy(temp, from, a, from, to - from);
    }
}
```

### Code walkthrough
- `new int[nums.length]`: One shared table instead of one per merge.
- `to - from <= 1`: A single paper needs no work.
- `a[i] <= a[j]`: Taking left on ties keeps the sort stable.
- `System.arraycopy`: Puts the ordered pile back.

### Complexity
- **Time:** O(n log n): log n levels with n work each
- **Space:** O(n) temporary, plus O(log n) stack

### Common mistakes
1. **Forgetting leftovers.** The tail of one pile is lost.
2. **Mixing inclusive and exclusive ends.** The recursion loops or skips a value.
3. **Not copying back.** The parent merges unsorted halves.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One value | `[1]` | `[1]` |
| Duplicates | `[2,1,2]` | `[1,2,2]` |
| Negatives | `[0,-3,-1]` | `[-3,-1,0]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[124 — 148. Sort List]]: The same split and merge on links.
- [[040 — 88. Merge Sorted Array]]: Just the merge, filled from the back.

### In a real interview
Say this: *"Merge sort guarantees O(n log n). If O(1) space mattered more I would use heap sort."*

## 5. Recall
Q: 912. Sort an Array: which pattern, and what is the insight?
A: Merge sort. One paper is already sorted, and two sorted piles merge in linear time.

Q: 912. Sort an Array: what is the analogy, and what does it stand for?
A: Teachers merging exam piles: handing out halves → recursion; the table for merging → a temporary array.

Q: 912. Sort an Array: Why is merge sort O(n log n)?
A: It has log n levels, each merging n values.

Q: 912. Sort an Array: what are the time and space costs?
A: Time O(n log n): log n levels with n work each. Space O(n) temporary, plus O(log n) stack.

@@ 3 | TOPIC | Practice — Review 03 | Re-solve without looking | MUST
# Review 03

Solve on a blank page. No notes, hints or solutions.

## 912. Sort an Array
Sort an array in O(n log n) without built-in sort.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/sort-an-array/)

## 28. Find the Index of the First Occurrence in a String
Find the first position of needle in haystack.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/)

## 1. Two Sum
Return indices of the two numbers that add up to target.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/two-sum/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 912 | | | | |
| 28 | | | | |
| 1 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[018 — 912. Sort an Array]]
- [[015 — 28. Find the Index of the First Occurrence in a String]]
- [[004 — 1. Two Sum]]

@@ 3 | TOPIC | 019 — 238. Product of Array Except Self | Medium · Prefix and suffix product | MUST
# 019 — 238. Product of Array Except Self

**Problem 19 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Prefix and suffix product**

[Open on LeetCode](https://leetcode.com/problems/product-of-array-except-self/)

## 1. Problem Statement
Return an array where position i holds the product of every element of `nums` except `nums[i]`. Use O(n) time and no division. Follow-up: O(1) space besides the output.

### Examples
```text
Input:  nums = [1,2,3,4]
Output: [24,12,8,6]

Input:  nums = [-1,1,0,-3,3]
Output: [0,0,9,0,0]
```

### Constraints
```text
2 <= nums.length <= 10^5
-30 <= nums[i] <= 30
The input is generated such that answer[i] is guaranteed to fit in a 32-bit integer.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two messengers along a row of houses
One messenger walks from the left and leaves at each door the product of the houses already passed. Another walks from the right and multiplies that note by what they have passed. Neither has included the house they are standing at.

| In the story | In the solution |
|---|---|
| first messenger | `left` |
| second messenger | `right` |

### Pattern signal
**Prefix and suffix product.** Everything except me is simply everything left of me times everything right of me.

### Thought process
1. **First idea:** Multiply the others for each index, in O(n²).
2. **The insight:** Everything except me is simply everything left of me times everything right of me.

### Optimal approach
1. Sweep right, writing the product so far before including the value.
2. Sweep left, multiplying by that side's product before including the value.

### Dry run
```text
nums = [1,2,3,4]
after left sweep:  [1,1,2,6]
i  right before  answer[i]   right after
3  1             6*1 = 6     4
2  4             2*4 = 8     12
1  12            1*12 = 12   24
0  24            1*24 = 24   24
Answer: [24,12,8,6]
```

### Why it works
Each index receives every earlier value from the first sweep and every later value from the second, and itself from neither.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        int left = 1;
        for (int i = 0; i < n; i++) {
            answer[i] = left; // excludes nums[i]
            left *= nums[i];
        }
        int right = 1;
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= right; // excludes nums[i]
            right *= nums[i];
        }
        return answer;
    }
}
```

### Code walkthrough
- `left = 1`: An empty product is 1. Starting at 0 would erase everything.
- `answer[i] = left`: Leave the note before collecting this house.
- `answer[i] *= right`: The second messenger completes the note.

### Complexity
- **Time:** O(n), two sweeps
- **Space:** O(1) besides the output

### Common mistakes
1. **Dividing the total.** It is forbidden and breaks on zero.
2. **Updating before writing.** The value includes itself.
3. **Starting at 0.** All answers become 0.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One zero | `[0,2,3]` | `[6,0,0]` |
| Two zeros | `[0,0]` | `[0,0]` |
| Negatives | `[-1,2]` | `[2,-1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[009 — 724. Find Pivot Index]]: Both sides, with sums. The key idea there: One total and one running sum give both sides by subtraction.
- [[051 — 42. Trapping Rain Water]]: Both sides, with maxima. The key idea there: You don't need both exact maxima, only the limiting one, and the lower side reveals it.

### In a real interview
Say this: *"I split "except me" into a prefix and a suffix. The output stores one and a variable carries the other."*

## 5. Recall
Q: 238. Product of Array Except Self: which pattern, and what is the insight?
A: Prefix and suffix product. Everything except me is simply everything left of me times everything right of me.

Q: 238. Product of Array Except Self: what is the analogy, and what does it stand for?
A: Two messengers along a row of houses: first messenger → `left`; second messenger → `right`.

Q: 238. Product of Array Except Self: How is the current value excluded?
A: Each sweep writes before multiplying the current value in.

Q: 238. Product of Array Except Self: what are the time and space costs?
A: Time O(n), two sweeps. Space O(1) besides the output.

@@ 3 | TOPIC | 020 — 36. Valid Sudoku | Medium · Seen sets | MUST
# 020 — 36. Valid Sudoku

**Problem 20 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Seen sets**

[Open on LeetCode](https://leetcode.com/problems/valid-sudoku/)

## 1. Problem Statement
Decide whether a partly filled 9 x 9 Sudoku board is valid so far: no digit repeats in a row, a column or any of the nine 3 x 3 boxes. Dots are empty. The board need not be solvable.

### Examples
```text
Input:  board =
        [["5","3",".",".","7",".",".",".","."]
        ,["6",".",".","1","9","5",".",".","."]
        ,[".","9","8",".",".",".",".","6","."]
        ,["8",".",".",".","6",".",".",".","3"]
        ,["4",".",".","8",".","3",".",".","1"]
        ,["7",".",".",".","2",".",".",".","6"]
        ,[".","6",".",".",".",".","2","8","."]
        ,[".",".",".","4","1","9",".",".","5"]
        ,[".",".",".",".","8",".",".","7","9"]]
Output: true

Input:  board =
        [["8","3",".",".","7",".",".",".","."]
        ,["6",".",".","1","9","5",".",".","."]
        ,[".","9","8",".",".",".",".","6","."]
        ,["8",".",".",".","6",".",".",".","3"]
        ,["4",".",".","8",".","3",".",".","1"]
        ,["7",".",".",".","2",".",".",".","6"]
        ,[".","6",".",".",".",".","2","8","."]
        ,[".",".",".","4","1","9",".",".","5"]
        ,[".",".",".",".","8",".",".","7","9"]]
Output: false
```

### Constraints
```text
board.length == 9
board[i].length == 9
board[i][j] is a digit 1-9 or '.'.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A citizen with three club cards
Each digit on the board must sign three registers: its row's, its column's and its box's. If any register already shows that digit, it is an impostor.

| In the story | In the solution |
|---|---|
| register | a boolean row in a table |
| the box's number | `(r / 3) * 3 + c / 3` |

### Pattern signal
**Seen sets.** One visit can check all three rules if all three registers are kept.

### Thought process
1. **First idea:** For each filled cell rescan its row, column and box. It is repeated work, even if small here.
2. **The insight:** One visit can check all three rules if all three registers are kept.

### Optimal approach
1. Create registers for rows, columns and boxes.
2. Skip dots.
3. Compute the box.
4. Fail if any register has the digit.
5. Otherwise sign all three.

### Dry run
```text
the top-left box of the second example
cell   digit  box  box register before  result
(0,0)  8      0    {}                   sign
(0,1)  3      0    {8}                  sign
(1,0)  6      0    {8,3}                sign
(2,1)  9      0    {8,3,6}              sign
(2,2)  8      0    {8,3,6,9}            repeat, return false
```

### Why it works
Each register holds exactly the digits already seen in that unit, so any repeat is caught at its second copy.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isValidSudoku(char[][] board) {
        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == '.') continue;
                int d = board[r][c] - '1'; // digit 1..9 to index 0..8
                int box = (r / 3) * 3 + c / 3;
                if (rows[r][d] || cols[c][d] || boxes[box][d]) return false;
                rows[r][d] = cols[c][d] = boxes[box][d] = true;
            }
        }
        return true;
    }
}
```

### Code walkthrough
- `board[r][c] - '1'`: The digit's line in a register.
- `(r / 3) * 3 + c / 3`: The band of rows picks 0, 3 or 6, the band of columns adds 0 to 2.
- `continue`: Empty cells sign nothing.

### Complexity
- **Time:** O(1), always 81 cells, or O(n²) for an n x n board
- **Space:** O(1), 243 booleans

### Common mistakes
1. **Trying to solve the puzzle.** Only current conflicts matter.
2. **`r / 3 + c / 3` as the box.** Different boxes collide.
3. **Registering dots.** The index becomes invalid.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty board | `all dots` | `true` |
| Row repeat | `5 at (0,0) and (0,8)` | `false` |
| Box repeat only | `1 at (0,0) and (1,1)` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **37. Sudoku Solver**: The same registers plus backtracking.
- [[198 — 51. N-Queens]]: Registers for columns and diagonals. The key idea there: Each diagonal has a name, r+c or r-c, so conflicts are tested in O(1).

### In a real interview
Say this: *"Each cell belongs to three constraint sets, so one pass is enough. A formula maps it to its box."*

## 5. Recall
Q: 36. Valid Sudoku: which pattern, and what is the insight?
A: Seen sets. One visit can check all three rules if all three registers are kept.

Q: 36. Valid Sudoku: what is the analogy, and what does it stand for?
A: A citizen with three club cards: register → a boolean row in a table; the box's number → `(r / 3) * 3 + c / 3`.

Q: 36. Valid Sudoku: How is a cell's box computed?
A: (r / 3) * 3 + c / 3.

Q: 36. Valid Sudoku: what are the time and space costs?
A: Time O(1), always 81 cells, or O(n²) for an n x n board. Space O(1), 243 booleans.

@@ 3 | TOPIC | 021 — 128. Longest Consecutive Sequence | Medium · Sequence starts | MUST
# 021 — 128. Longest Consecutive Sequence

**Problem 21 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Sequence starts**

[Open on LeetCode](https://leetcode.com/problems/longest-consecutive-sequence/)

## 1. Problem Statement
Return the length of the longest run of consecutive integers, such as 4, 5, 6, that can be formed from the values of an unsorted array. Positions don't matter. Use O(n) time.

### Examples
```text
Input:  nums = [100,4,200,1,3,2]
Output: 4

Input:  nums = [0,3,7,2,5,8,4,6,0,1]
Output: 9

Input:  nums = [1,0,1,2]
Output: 3
```

### Constraints
```text
0 <= nums.length <= 10^5
-10^9 <= nums[i] <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Counting a train only from its engine
Carriages are scattered around a yard, each numbered. If carriage x-1 exists, x is in the middle of someone's train, so leave it. Start counting only at an engine, a number with nothing before it.

| In the story | In the solution |
|---|---|
| engine | x with no x - 1 in the set |
| following the couplings | checking x + 1, x + 2 and so on |

### Pattern signal
**Sequence starts.** Counting from a middle repeats part of a longer count, so do it only at starts.

### Thought process
1. **First idea:** Sort and scan in O(n log n), or count from every value in up to O(n²).
2. **The insight:** Counting from a middle repeats part of a longer count, so do it only at starts.

### Optimal approach
1. Put all values in a set.
2. Loop over the set.
3. Skip values with a predecessor.
4. Count successors from each start.
5. Keep the longest.

### Dry run
```text
nums = [100,4,200,1,3,2]
value  predecessor present?  run        best
100    no                    100        1
4      yes, 3                skip       1
200    no                    200        1
1      no                    1,2,3,4    4
3      yes, 2                skip       4
2      yes, 1                skip       4
```

### Why it works
Every run has exactly one start, which is examined. Every value is walked only within its own run, so the total is linear.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> values = new HashSet<>();
        for (int x : nums) values.add(x);
        int best = 0;
        for (int x : values) { // the set, so each start is used once
            if (values.contains(x - 1)) continue;
            int end = x;
            while (values.contains(end + 1)) end++;
            best = Math.max(best, end - x + 1);
        }
        return best;
    }
}
```

### Code walkthrough
- `for (int x : values)`: Looping over nums would recount a repeated engine again and again.
- `values.contains(x - 1)`: Not an engine, so skip.
- `end - x + 1`: Both ends of the train count.

### Complexity
- **Time:** O(n) on average
- **Space:** O(n) for the set

### Common mistakes
1. **Looping over the array.** Repeated starts make it O(n²).
2. **Omitting the start check.** One long run becomes O(n²).
3. **Starting best at 1.** An empty array must return 0.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `0` |
| Duplicates | `[1,1,2]` | `2` |
| Across zero | `[-1,0,1]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **298. Binary Tree Longest Consecutive Sequence**: The run follows tree edges.
- **674. Longest Continuous Increasing Subsequence**: Positions matter, so scan directly.

### In a real interview
Say this: *"A set replaces sorting. Starting only where x minus one is absent makes the nested loop linear."*

## 5. Recall
Q: 128. Longest Consecutive Sequence: which pattern, and what is the insight?
A: Sequence starts. Counting from a middle repeats part of a longer count, so do it only at starts.

Q: 128. Longest Consecutive Sequence: what is the analogy, and what does it stand for?
A: Counting a train only from its engine: engine → x with no x - 1 in the set; following the couplings → checking x + 1, x + 2 and so on.

Q: 128. Longest Consecutive Sequence: Which values may start a count?
A: Only those whose predecessor is absent.

Q: 128. Longest Consecutive Sequence: what are the time and space costs?
A: Time O(n) on average. Space O(n) for the set.

@@ 3 | TOPIC | 022 — 535. Encode and Decode TinyURL | Medium · ID map | SHOULD
# 022 — 535. Encode and Decode TinyURL

**Problem 22 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: ID map**

[Open on LeetCode](https://leetcode.com/problems/encode-and-decode-tinyurl/)

## 1. Problem Statement
Design `Codec`. `encode(longUrl)` returns a short URL and `decode(shortUrl)` returns the original. Any scheme works if decoding is exact.

### Examples
```text
Input:  url = "https://leetcode.com/problems/design-tinyurl"
Output: "https://leetcode.com/problems/design-tinyurl"
```

### Constraints
```text
1 <= url.length <= 10^4
url is guranteed to be a valid URL.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A coat check
The ticket doesn't describe your coat. It is just a number, and the attendant's records lead from it to the coat. Issue a new number each time and no two coats get confused.

| In the story | In the solution |
|---|---|
| ticket | the short URL |
| attendant's records | a list indexed by ID |

### Pattern signal
**ID map.** The short URL only has to identify a stored value, not contain it.

### Thought process
1. **First idea:** Compressing the text gives no guarantee that the result is short.
2. **The insight:** The short URL only has to identify a stored value, not contain it.

### Optimal approach
1. Use the list's size as a new ID.
2. Append the URL.
3. Return a prefix plus the ID.
4. Decode by reading the ID and indexing the list.

### Dry run
```text
operation          ID  stored after  returns
encode(url A)      0   [A]           http://tinyurl.com/0
encode(url B)      1   [A,B]         http://tinyurl.com/1
decode(.../0)      0   [A,B]         url A
```

### Why it works
The list only grows, so an ID is never reused and always points to the URL that created it.

## 3. Java Solution with Explanation
### Code
```java
class Codec {
    private static final String PREFIX = "http://tinyurl.com/";
    private final List<String> urls = new ArrayList<>();

    public String encode(String longUrl) {
        int id = urls.size(); // never used before
        urls.add(longUrl);
        return PREFIX + id;
    }

    public String decode(String shortUrl) {
        int id = Integer.parseInt(shortUrl.substring(PREFIX.length()));
        return urls.get(id);
    }
}
```

### Code walkthrough
- `urls.size()`: The next free ticket number.
- `PREFIX + id`: The ticket handed to the user.
- `substring(PREFIX.length())`: Reads the number back off the ticket.

### Complexity
- **Time:** O(1) lookup and amortized append, plus the short URL's length
- **Space:** O(total stored characters)

### Common mistakes
1. **A constant key.** Every URL overwrites the last.
2. **Using hashCode as the ID.** Different URLs can collide.
3. **A counter in production.** IDs can be guessed and servers need coordination.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two URLs | `encode A, encode B, decode the first` | `A` |
| Same URL twice | `encode A twice, decode both` | `A and A` |
| Query string | `a URL with ?x=1` | `unchanged` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **271. Encode and Decode Strings**: No storage, so the text must describe itself.
- [[014 — 706. Design HashMap]]: How a lookup table works inside.

### In a real interview
Say this: *"The short URL is a handle to stored data. At scale I would discuss base 62, distributed IDs and persistence."*

## 5. Recall
Q: 535. Encode and Decode TinyURL: which pattern, and what is the insight?
A: ID map. The short URL only has to identify a stored value, not contain it.

Q: 535. Encode and Decode TinyURL: what is the analogy, and what does it stand for?
A: A coat check: ticket → the short URL; attendant's records → a list indexed by ID.

Q: 535. Encode and Decode TinyURL: Why are collisions impossible here?
A: Every encode takes the next unused list index.

Q: 535. Encode and Decode TinyURL: what are the time and space costs?
A: Time O(1) lookup and amortized append, plus the short URL's length. Space O(total stored characters).

@@ 3 | TOPIC | 023 — 554. Brick Wall | Medium · Prefix frequency | SHOULD
# 023 — 554. Brick Wall

**Problem 23 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Prefix frequency**

[Open on LeetCode](https://leetcode.com/problems/brick-wall/)

## 1. Problem Statement
A wall has rows of bricks with the same total width. Draw a vertical line from top to bottom crossing as few bricks as possible. A line along a joint doesn't cross that brick. The two outer edges are not allowed. Return the bricks crossed.

### Examples
```text
Input:  wall = [[1,2,2,1],[3,1,2],[1,3,2],[2,4],[3,1,2],[1,3,1,1]]
Output: 2

Input:  wall = [[1],[1],[1]]
Output: 3
```

### Constraints
```text
n == wall.length
1 <= n <= 10^4
1 <= wall[i].length <= 10^4
1 <= sum(wall[i].length) <= 2 * 10^4
sum(wall[i]) is the same for each row i.
1 <= wall[i][j] <= 2^31 - 1
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Sliding a ruler through the mortar
You want a straight ruler to fall through as many mortar joints as possible. So don't study bricks, study joints: each row votes for the positions where it has one, and the ruler goes to the winner.

| In the story | In the solution |
|---|---|
| a vote | one count for a joint position |
| the winner | the highest count |

### Pattern signal
**Prefix frequency.** Crossed bricks equal rows minus rows with a joint there. Only joints matter.

### Thought process
1. **First idea:** Test every x-position, but widths go up to about two billion.
2. **The insight:** Crossed bricks equal rows minus rows with a joint there. Only joints matter.

### Optimal approach
1. Walk each row adding widths.
2. Stop before the last brick.
3. Count each position.
4. Return rows minus the highest count.

### Dry run
```text
wall = [[1,2,2,1],[3,1,2],[1,3,2],[2,4],[3,1,2],[1,3,1,1]]
row        joints   counts after
1,2,2,1    1,3,5    1:1 3:1 5:1
3,1,2      3,4      1:1 3:2 4:1 5:1
1,3,2      1,4      1:2 3:2 4:2 5:1
2,4        2        1:2 2:1 3:2 4:2 5:1
3,1,2      3,4      1:2 2:1 3:3 4:3 5:1
1,3,1,1    1,4,5    1:3 2:1 3:3 4:4 5:2
Best is 4 joints, so cross 6-4 = 2
```

### Why it works
A vertical line meets each row once, at a joint or a brick. Maximizing joints minimizes bricks.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int leastBricks(List<List<Integer>> wall) {
        Map<Integer, Integer> joints = new HashMap<>();
        int most = 0;
        for (List<Integer> row : wall) {
            int x = 0;
            for (int i = 0; i < row.size() - 1; i++) { // no outer edge
                x += row.get(i);
                int votes = joints.merge(x, 1, Integer::sum);
                most = Math.max(most, votes);
            }
        }
        return wall.size() - most;
    }
}
```

### Code walkthrough
- `row.size() - 1`: The right edge would win every vote illegally.
- `x += row.get(i)`: A prefix sum is the joint's position.
- `merge`: Adds a vote and returns the new count.

### Complexity
- **Time:** O(b) for b bricks
- **Space:** O(j) for j distinct joints

### Common mistakes
1. **Counting the right edge.** The answer becomes 0.
2. **Comparing brick indices.** Align by position, not by order.
3. **An array as wide as the wall.** The width can be huge, so use a map.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| No joints | `[[1],[1]]` | `2` |
| Aligned | `[[1,1],[1,1]]` | `0` |
| Shifted | `[[1,2],[2,1]]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[025 — 560. Subarray Sum Equals K]]: Prefix positions counted in a map.
- [[182 — 1094. Car Pooling]]: Events at positions, then a sweep.

### In a real interview
Say this: *"I reverse the goal to maximizing aligned joints. Prefix widths name each joint and a map counts them."*

## 5. Recall
Q: 554. Brick Wall: which pattern, and what is the insight?
A: Prefix frequency. Crossed bricks equal rows minus rows with a joint there. Only joints matter.

Q: 554. Brick Wall: what is the analogy, and what does it stand for?
A: Sliding a ruler through the mortar: a vote → one count for a joint position; the winner → the highest count.

Q: 554. Brick Wall: What is maximized to minimize crossings?
A: The rows with a joint at the same position.

Q: 554. Brick Wall: what are the time and space costs?
A: Time O(b) for b bricks. Space O(j) for j distinct joints.

@@ 3 | TOPIC | 024 — 122. Best Time to Buy and Sell Stock II | Medium · Greedy gains | SHOULD
# 024 — 122. Best Time to Buy and Sell Stock II

**Problem 24 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Greedy gains**

[Open on LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/)

## 1. Problem Statement
`prices[i]` is a stock's price on day i. Hold at most one share, buy and sell as often as you like, even on the same day. Return the maximum profit.

### Examples
```text
Input:  prices = [7,1,5,3,6,4]
Output: 7

Input:  prices = [1,2,3,4,5]
Output: 4

Input:  prices = [7,6,4,3,1]
Output: 0
```

### Constraints
```text
1 <= prices.length <= 3 * 10^4
0 <= prices[i] <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Paid only for climbing
On a mountain trail you earn a coin for every metre uphill and can take a free cable car on every downhill. You don't need to find the peaks: say yes to every uphill step.

| In the story | In the solution |
|---|---|
| uphill step | a rise from yesterday |
| cable car | holding nothing during a fall |

### Pattern signal
**Greedy gains.** A long climb equals the sum of its daily climbs, so each day can be decided alone.

### Thought process
1. **First idea:** Try buy, sell or wait each day, which is exponential without DP.
2. **The insight:** A long climb equals the sum of its daily climbs, so each day can be decided alone.

### Optimal approach
1. Compare each day with the previous one.
2. Add a positive difference.
3. Ignore the rest.

### Dry run
```text
prices = [7,1,5,3,6,4]
from  to  change  profit
7     1   -6      0
1     5   +4      4
5     3   -2      4
3     6   +3      7
6     4   -2      7
```

### Why it works
Any trade's profit is the sum of its daily changes, so no plan can exceed the sum of positive changes. One-day trades achieve exactly that.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxProfit(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            int change = prices[i] - prices[i - 1];
            if (change > 0) profit += change; // walk uphill only
        }
        return profit;
    }
}
```

### Code walkthrough
- `i = 1`: The first day has no step leading to it.
- `prices[i] - prices[i - 1]`: One step of the trail.
- `change > 0`: Falls are skipped by holding nothing.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Finding only the best pair.** That is problem 121.
2. **Adding negative changes.** You can choose not to hold.
3. **Using this with a fee.** Fees need DP or an adjusted greedy.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One day | `[5]` | `0` |
| Falling | `[5,4,3]` | `0` |
| Flat | `[2,2,2]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[052 — 121. Best Time to Buy and Sell Stock]]: One trade, so track the cheapest day.
- [[258 — 309. Best Time to Buy and Sell Stock with Cooldown]]: Steps are dependent, so use states.

### In a real interview
Say this: *"With unlimited free trades every interval splits into daily changes. I take all the positive ones."*

## 5. Recall
Q: 122. Best Time to Buy and Sell Stock II: which pattern, and what is the insight?
A: Greedy gains. A long climb equals the sum of its daily climbs, so each day can be decided alone.

Q: 122. Best Time to Buy and Sell Stock II: what is the analogy, and what does it stand for?
A: Paid only for climbing: uphill step → a rise from yesterday; cable car → holding nothing during a fall.

Q: 122. Best Time to Buy and Sell Stock II: Why is summing rises optimal?
A: It is an upper bound on any plan and one-day trades reach it.

Q: 122. Best Time to Buy and Sell Stock II: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Review 04 | Re-solve without looking | MUST
# Review 04

Solve on a blank page. No notes, hints or solutions.

## 122. Best Time to Buy and Sell Stock II
Maximize profit with unlimited buy and sell trades.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/)

## 128. Longest Consecutive Sequence
Find the longest run of consecutive values in O(n).

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/longest-consecutive-sequence/)

## 303. Range Sum Query - Immutable
Answer many range-sum queries on a fixed array.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/range-sum-query-immutable/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 122 | | | | |
| 128 | | | | |
| 303 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[024 — 122. Best Time to Buy and Sell Stock II]]
- [[021 — 128. Longest Consecutive Sequence]]
- [[010 — 303. Range Sum Query - Immutable]]

@@ 3 | TOPIC | 025 — 560. Subarray Sum Equals K | Medium · Prefix sum map | SHOULD
# 025 — 560. Subarray Sum Equals K

**Problem 25 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Prefix sum map**

[Open on LeetCode](https://leetcode.com/problems/subarray-sum-equals-k/)

## 1. Problem Statement
Return how many contiguous, non-empty subarrays of `nums` sum to exactly `k`. Values may be negative.

### Examples
```text
Input:  nums = [1,1,1], k = 2
Output: 2

Input:  nums = [1,2,3], k = 3
Output: 2
```

### Constraints
```text
1 <= nums.length <= 2 * 10^4
-1000 <= nums[i] <= 1000
-10^7 <= k <= 10^7
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A hiker's altitude diary
A hiker writes down their altitude after every step. "Which stretches climbed exactly k?" becomes "how many earlier diary entries are exactly k below where I stand now?" The route may go up and down, the subtraction still works.

| In the story | In the solution |
|---|---|
| altitude | the running sum |
| diary tally | a map from sum to how often it occurred |
| trailhead | sum 0, seen once |

### Pattern signal
**Prefix sum map.** A range is now minus earlier. Both now and k are known, so earlier must be now minus k.

### Thought process
1. **First idea:** Try every start and end with a running total, in O(n²).
2. **The insight:** A range is now minus earlier. Both now and k are known, so earlier must be now minus k.

### Optimal approach
1. Record sum zero once.
2. Add each value to the sum.
3. Add the count of sum minus k to the answer.
4. Then record the sum.

### Dry run
```text
nums = [1,2,3], k = 3
x  sum  need  map before         answer
1  1    -2    {0:1}              0
2  3    0     {0:1,1:1}          1, range [1,2]
3  6    3     {0:1,1:1,3:1}      2, range [3]
```

### Why it works
The map counts every possible start before the current end. Each matching earlier sum gives one different subarray ending here.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> times = new HashMap<>();
        times.put(0, 1); // the trailhead
        int sum = 0, answer = 0;
        for (int x : nums) {
            sum += x;
            answer += times.getOrDefault(sum - k, 0);
            times.merge(sum, 1, Integer::sum);
        }
        return answer;
    }
}
```

### Code walkthrough
- `times.put(0, 1)`: Lets a range start at index 0.
- `sum - k`: The altitude the stretch must have started at.
- `merge`: Record afterwards, so no range is empty.

### Complexity
- **Time:** O(n) on average
- **Space:** O(n) distinct sums

### Common mistakes
1. **A sliding window.** Negatives break its monotonic rule.
2. **Storing presence only.** One altitude can occur many times.
3. **Recording before looking up.** k = 0 counts empty ranges.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One match | `[5], 5` | `1` |
| Zeros | `[0,0], 0` | `3` |
| Negative | `[1,-1], 0` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[029 — 523. Continuous Subarray Sum]]: Equal remainders, storing the first index.
- **974. Subarray Sums Divisible by K**: Count equal remainders.

### In a real interview
Say this: *"A range sum is two prefixes subtracted. I count earlier prefixes equal to sum minus k."*

## 5. Recall
Q: 560. Subarray Sum Equals K: which pattern, and what is the insight?
A: Prefix sum map. A range is now minus earlier. Both now and k are known, so earlier must be now minus k.

Q: 560. Subarray Sum Equals K: what is the analogy, and what does it stand for?
A: A hiker's altitude diary: altitude → the running sum; diary tally → a map from sum to how often it occurred; trailhead → sum 0, seen once.

Q: 560. Subarray Sum Equals K: Why seed the map with 0 seen once?
A: For subarrays starting at index 0.

Q: 560. Subarray Sum Equals K: what are the time and space costs?
A: Time O(n) on average. Space O(n) distinct sums.

@@ 3 | TOPIC | 026 — 1930. Unique Length-3 Palindromic Subsequences | Medium · Middle character | SHOULD
# 026 — 1930. Unique Length-3 Palindromic Subsequences

**Problem 26 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Middle character**

[Open on LeetCode](https://leetcode.com/problems/unique-length-3-palindromic-subsequences/)

## 1. Problem Statement
Count the different palindromes of length 3 that are subsequences of `s`. A subsequence keeps order but may skip characters. The same palindrome counts once.

### Examples
```text
Input:  s = "aabca"
Output: 3

Input:  s = "adc"
Output: 0

Input:  s = "bbcbaba"
Output: 4
```

### Constraints
```text
3 <= s.length <= 10^5
s consists of only lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Bookends on a shelf
A length-3 palindrome is a middle book between two identical bookends. For each bookend letter put the pair as far apart as possible. Every choice of middle that could ever work is between them, so count the different books there.

| In the story | In the solution |
|---|---|
| bookends | first and last copy of a letter |
| different books | a 26-slot seen array |

### Pattern signal
**Middle character.** There are only 26 bookend letters, and the widest pair covers every narrower one.

### Thought process
1. **First idea:** Try all triples, in O(n³).
2. **The insight:** There are only 26 bookend letters, and the widest pair covers every narrower one.

### Optimal approach
1. Record first and last position of each letter.
2. Visit letters occurring twice or more.
3. Count distinct characters strictly between.
4. Add up.

### Dry run
```text
s = "aabca"
outer  first  last  between  distinct  total
a      0      4     a,b,c    3         3
b      2      2     none     0         3
c      3      3     none     0         3
Palindromes: aaa, aba, aca
```

### Why it works
Any valid middle for a letter lies between some pair of its copies, so between the widest pair. Palindromes with different outer or middle letters are different.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int countPalindromicSubsequence(String s) {
        int[] first = new int[26], last = new int[26];
        Arrays.fill(first, -1);
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i) - 'a';
            if (first[c] == -1) first[c] = i;
            last[c] = i;
        }
        int total = 0;
        for (int c = 0; c < 26; c++) {
            if (first[c] == -1) continue;
            boolean[] seen = new boolean[26];
            for (int i = first[c] + 1; i < last[c]; i++) {
                int middle = s.charAt(i) - 'a';
                if (!seen[middle]) {
                    seen[middle] = true;
                    total++;
                }
            }
        }
        return total;
    }
}
```

### Code walkthrough
- `first[c] == -1`: Keep only the earliest copy.
- `last[c] = i`: Overwriting leaves the latest.
- `new boolean[26]`: Middles are distinct per bookend, so start fresh.
- `i < last[c]`: A bookend is not its own middle.

### Complexity
- **Time:** O(26·n)
- **Space:** O(1), fixed arrays

### Common mistakes
1. **Counting occurrences.** Count different strings.
2. **One seen array for all letters.** aba would hide cbc.
3. **Requiring adjacency.** Subsequences may skip.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Triple | `"aaa"` | `1` |
| None | `"abc"` | `0` |
| Repeated middle | `"abba"` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[284 — 763. Partition Labels]]: Last occurrences define boundaries. The key idea there: A cut is safe exactly where every letter seen so far has had its last occurrence.
- [[035 — 392. Is Subsequence]]: The basic meaning of subsequence. The key idea there: Taking the earliest possible match always leaves the most of t for the rest, so one forward walk is enough.

### In a real interview
Say this: *"The pattern is fixed, so I enumerate the outer letter. Its first and last copies contain every possible middle."*

## 5. Recall
Q: 1930. Unique Length-3 Palindromic Subsequences: which pattern, and what is the insight?
A: Middle character. There are only 26 bookend letters, and the widest pair covers every narrower one.

Q: 1930. Unique Length-3 Palindromic Subsequences: what is the analogy, and what does it stand for?
A: Bookends on a shelf: bookends → first and last copy of a letter; different books → a 26-slot seen array.

Q: 1930. Unique Length-3 Palindromic Subsequences: Why use first and last occurrence?
A: The widest pair contains every possible middle.

Q: 1930. Unique Length-3 Palindromic Subsequences: what are the time and space costs?
A: Time O(26·n). Space O(1), fixed arrays.

@@ 3 | TOPIC | 027 — 2017. Grid Game | Medium · Prefix and minimax | SHOULD
# 027 — 2017. Grid Game

**Problem 27 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Prefix and minimax**

[Open on LeetCode](https://leetcode.com/problems/grid-game/)

## 1. Problem Statement
Two robots cross a 2 x n grid from top-left to bottom-right, moving only right or down. The first collects its cells and sets them to 0, then the second takes its best path. The first minimizes the second's total and the second maximizes it. Return the second's total.

### Examples
```text
Input:  grid = [[2,5,4],[1,5,1]]
Output: 4

Input:  grid = [[3,3,1],[8,5,2]]
Output: 4

Input:  grid = [[1,3,1,15],[1,3,3,1]]
Output: 7
```

### Constraints
```text
grid.length == 2
n == grid[r].length
1 <= n <= 5 * 10^4
1 <= grid[r][c] <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Snowploughs and a rival collector
The first plough clears the top road up to one crossing, drops down, and clears the rest of the bottom. It leaves two piles that no single later route can join: the top-right and the bottom-left. Your rival takes the bigger, so choose the crossing that makes the bigger pile small.

| In the story | In the solution |
|---|---|
| crossing | column i |
| the piles | `top` and `bottom` |

### Pattern signal
**Prefix and minimax.** After the first path only two sums can be non-zero for the rival, and both update in O(1).

### Thought process
1. **First idea:** Try n crossings and n replies, in O(n²).
2. **The insight:** After the first path only two sums can be non-zero for the rival, and both update in O(1).

### Optimal approach
1. Sum the top row.
2. At each crossing remove that top cell.
3. Take the larger pile.
4. Minimize it.
5. Add that bottom cell to the left pile.

### Dry run
```text
grid = [[2,5,4],[1,5,1]]
crossing  top-right  bottom-left  rival  best
0         9          0            9      9
1         4          1            4      4
2         0          6            6      4
```

### Why it works
Each first path has exactly one crossing, so all are tested. The rival cannot reach both piles, so its best is their maximum.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public long gridGame(int[][] grid) {
        long top = 0, bottom = 0;
        for (int x : grid[0]) top += x;
        long best = Long.MAX_VALUE;
        for (int i = 0; i < grid[0].length; i++) {
            top -= grid[0][i]; // only the cells right of i remain
            best = Math.min(best, Math.max(top, bottom));
            bottom += grid[1][i]; // left of the next crossing
        }
        return best;
    }
}
```

### Code walkthrough
- `long`: The sum can reach five billion.
- `Math.max(top, bottom)`: The rival's choice.
- `Math.min`: Our choice of crossing.
- `bottom += grid[1][i]`: After scoring, because the first robot takes this cell now.

### Complexity
- **Time:** O(n)
- **Space:** O(1), two sums

### Common mistakes
1. **Maximizing the first robot.** Its goal is to starve the second.
2. **Adding the two piles.** One path cannot take both.
3. **Using int.** It overflows.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One column | `[[1],[2]]` | `0` |
| Two columns | `[[1,2],[3,4]]` | `2` |
| Uniform | `[[1,1,1],[1,1,1]]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[009 — 724. Find Pivot Index]]: A moving split with running sums.
- [[257 — 1140. Stone Game II]]: Min against max over many turns, needing DP.

### In a real interview
Say this: *"The first path is one crossing, which leaves two piles. I minimize the larger using running sums."*

## 5. Recall
Q: 2017. Grid Game: which pattern, and what is the insight?
A: Prefix and minimax. After the first path only two sums can be non-zero for the rival, and both update in O(1).

Q: 2017. Grid Game: what is the analogy, and what does it stand for?
A: Snowploughs and a rival collector: crossing → column i; the piles → `top` and `bottom`.

Q: 2017. Grid Game: What can the second robot collect?
A: The larger of top-right and bottom-left.

Q: 2017. Grid Game: what are the time and space costs?
A: Time O(n). Space O(1), two sums.

@@ 3 | TOPIC | 028 — 179. Largest Number | Medium · Custom order | SHOULD
# 028 — 179. Largest Number

**Problem 28 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Custom order**

[Open on LeetCode](https://leetcode.com/problems/largest-number/)

## 1. Problem Statement
Arrange the non-negative integers of `nums` so that joined together they form the largest number. Return it as a string, with "0" rather than several zeros.

### Examples
```text
Input:  nums = [10,2]
Output: "210"

Input:  nums = [3,30,34,5,9]
Output: "9534330"
```

### Constraints
```text
1 <= nums.length <= 100
0 <= nums[i] <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two people queueing for a photo
Number cards must line up to show the biggest number. For any two neighbors don't ask who is taller alone, let them stand both ways, "3 30" and "30 3", and keep the bigger picture. Sorting does this for the whole line.

| In the story | In the solution |
|---|---|
| trying both orders | comparing `a + b` with `b + a` |
| the final photo | the joined string |

### Pattern signal
**Custom order.** The best order of two neighbors does not depend on the others, so it can be a comparator.

### Thought process
1. **First idea:** Try every permutation, n! of them.
2. **The insight:** The best order of two neighbors does not depend on the others, so it can be a comparator.

### Optimal approach
1. Convert to strings.
2. Sort so that the larger concatenation comes first.
3. Return zero if the first is zero.
4. Join.

### Dry run
```text
nums = [3,30,34,5,9]
a   b   a+b   b+a   first
3   30  330   303   3
34  3   343   334   34
5   34  534   345   5
9   5   95    59    9
Order: 9, 5, 34, 3, 30 gives 9534330
```

### Why it works
The relation is transitive. If any neighbors were in the other order, swapping them would enlarge the number, so the sorted order is optimal.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String largestNumber(int[] nums) {
        String[] parts = new String[nums.length];
        for (int i = 0; i < nums.length; i++) parts[i] = String.valueOf(nums[i]);
        // a goes first when ab is larger than ba
        Arrays.sort(parts, (a, b) -> (b + a).compareTo(a + b));
        if (parts[0].equals("0")) return "0";
        return String.join("", parts);
    }
}
```

### Code walkthrough
- `String.valueOf`: The cards are placed as text.
- `(b + a).compareTo(a + b)`: Equal lengths, so text order is numeric order. Reversed for largest first.
- `equals("0")`: If the best card is 0, all are.

### Complexity
- **Time:** O(d·n log n) for d digits
- **Space:** O(d·n)

### Common mistakes
1. **Descending numeric sort.** 30 lands before 3.
2. **Descending text sort.** It also puts 30 before 3.
3. **Parsing the concatenation as int.** It overflows.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[7]` | `"7"` |
| Zeros | `[0,0]` | `"0"` |
| Prefix | `[12,121]` | `"12121"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[286 — 1029. Two City Scheduling]]: A custom key proved by swapping.
- [[288 — 56. Merge Intervals]]: Sort so a single pass works.

### In a real interview
Say this: *"I derive the order from swapping two neighbors. Comparing both concatenations handles prefixes."*

## 5. Recall
Q: 179. Largest Number: which pattern, and what is the insight?
A: Custom order. The best order of two neighbors does not depend on the others, so it can be a comparator.

Q: 179. Largest Number: what is the analogy, and what does it stand for?
A: Two people queueing for a photo: trying both orders → comparing `a + b` with `b + a`; the final photo → the joined string.

Q: 179. Largest Number: When does a precede b?
A: When ab is larger than ba.

Q: 179. Largest Number: what are the time and space costs?
A: Time O(d·n log n) for d digits. Space O(d·n).

@@ 3 | TOPIC | 029 — 523. Continuous Subarray Sum | Medium · Prefix remainder | SHOULD
# 029 — 523. Continuous Subarray Sum

**Problem 29 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Prefix remainder**

[Open on LeetCode](https://leetcode.com/problems/continuous-subarray-sum/)

## 1. Problem Statement
Return true if some contiguous subarray of length at least 2 has a sum that is a multiple of `k`. Zero is a multiple too.

### Examples
```text
Input:  nums = [23,2,4,6,7], k = 6
Output: true

Input:  nums = [23,2,6,4,7], k = 6
Output: true

Input:  nums = [23,2,6,4,7], k = 13
Output: false
```

### Constraints
```text
1 <= nums.length <= 10^5
0 <= nums[i] <= 10^9
0 <= sum(nums[i]) <= 2^31 - 1
1 <= k <= 2^31 - 1
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The hour hand
Walk forward by each value on a clock with k hours. If the hand shows the same hour twice, you travelled whole laps in between, so that stretch is a multiple of k. Note only the first time you saw each hour, to make the stretch as long as possible.

| In the story | In the solution |
|---|---|
| hour shown | running sum modulo k |
| first sighting | the stored index |

### Pattern signal
**Prefix remainder.** Two prefixes differ by a multiple of k exactly when their remainders are equal.

### Thought process
1. **First idea:** Test every range with a running sum, in O(n²).
2. **The insight:** Two prefixes differ by a multiple of k exactly when their remainders are equal.

### Optimal approach
1. Record remainder zero at index minus one.
2. Update the remainder.
3. If seen, test the distance.
4. Otherwise store the index.

### Dry run
```text
nums = [23,2,4,6,7], k = 6
i  value  sum  remainder  first seen  result
0  23     23   5          none        store 0
1  2      25   1          none        store 1
2  4      29   5          0           distance 2, true
```

### Why it works
The earliest equal remainder gives the longest range ending here. If that is too short, any later one is too.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean checkSubarraySum(int[] nums, int k) {
        Map<Integer, Integer> first = new HashMap<>();
        first.put(0, -1); // before the array
        long sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            int remainder = (int) (sum % k);
            Integer j = first.putIfAbsent(remainder, i);
            if (j != null && i - j >= 2) return true;
        }
        return false;
    }
}
```

### Code walkthrough
- `first.put(0, -1)`: The hand's start, one step before index 0.
- `sum % k`: The hour now shown.
- `putIfAbsent`: Never replaces the first sighting.
- `i - j >= 2`: The values after j through i, at least two.

### Complexity
- **Time:** O(n) on average
- **Space:** O(min(n, k))

### Common mistakes
1. **Seeding index 0.** Ranges from the start look one too short.
2. **Overwriting the index.** Long enough ranges disappear.
3. **Accepting length 1.** [6] with k = 6 is false.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Too short | `[6], 6` | `false` |
| Two zeros | `[0,0], 5` | `true` |
| Single zero | `[5,0], 7` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[025 — 560. Subarray Sum Equals K]]: Exact sums, storing counts. The key idea there: A range is now minus earlier. Both now and k are known, so earlier must be now minus k.
- **525. Contiguous Array**: Equal balances, storing first index.

### In a real interview
Say this: *"Equal prefix remainders mean a divisible range. I keep the earliest index to meet the length rule."*

## 5. Recall
Q: 523. Continuous Subarray Sum: which pattern, and what is the insight?
A: Prefix remainder. Two prefixes differ by a multiple of k exactly when their remainders are equal.

Q: 523. Continuous Subarray Sum: what is the analogy, and what does it stand for?
A: The hour hand: hour shown → running sum modulo k; first sighting → the stored index.

Q: 523. Continuous Subarray Sum: Why keep the first index?
A: It gives the longest possible range.

Q: 523. Continuous Subarray Sum: what are the time and space costs?
A: Time O(n) on average. Space O(min(n, k)).

@@ 3 | TOPIC | 030 — 187. Repeated DNA Sequences | Medium · Substring set | SHOULD
# 030 — 187. Repeated DNA Sequences

**Problem 30 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Substring set**

[Open on LeetCode](https://leetcode.com/problems/repeated-dna-sequences/)

## 1. Problem Statement
A DNA string uses A, C, G and T. Return every 10-letter substring that occurs more than once, each only once.

### Examples
```text
Input:  s = "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT"
Output: ["AAAAACCCCC","CCCCCAAAAA"]

Input:  s = "AAAAAAAAAAAAA"
Output: ["AAAAAAAAAA"]
```

### Constraints
```text
1 <= s.length <= 10^5
s[i] is either 'A', 'C', 'G', or 'T'.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A stamp and two ink pads
Slide a 10-letter viewing frame along the strip. The first time a picture appears, file it in the album. When it appears again, put it on the repeats page, which ignores further copies.

| In the story | In the solution |
|---|---|
| album | the `seen` set |
| repeats page | the `repeated` set |

### Pattern signal
**Substring set.** Each window only needs to know whether it was seen before.

### Thought process
1. **First idea:** Compare every window with every other, in O(n²).
2. **The insight:** Each window only needs to know whether it was seen before.

### Optimal approach
1. Visit each start with ten characters left.
2. Cut the window.
3. Add it to seen.
4. If already there add it to repeated.

### Dry run
```text
s = "AAAAAAAAAAAA", length 12
start  window      seen before  action
0      AAAAAAAAAA  no           file it
1      AAAAAAAAAA  yes          add to repeats
2      AAAAAAAAAA  yes          repeats unchanged
```

### Why it works
Every substring of length 10 is visited once per start. It reaches the repeats exactly when a second start exists.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<String> findRepeatedDnaSequences(String s) {
        Set<String> seen = new HashSet<>(), repeated = new HashSet<>();
        for (int start = 0; start + 10 <= s.length(); start++) {
            String window = s.substring(start, start + 10);
            if (!seen.add(window)) repeated.add(window);
        }
        return new ArrayList<>(repeated);
    }
}
```

### Code walkthrough
- `start + 10 <= s.length()`: Includes the last full frame.
- `!seen.add(window)`: False means the album has it.
- `repeated`: A set, so the third copy adds nothing.

### Complexity
- **Time:** O(10·n), which is O(n)
- **Space:** O(n) windows

### Common mistakes
1. **Using `<`.** The last window is missed.
2. **A list for repeats.** Triples appear twice.
3. **Jumping by 10.** Occurrences overlap.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Short | `"ACGT"` | `[]` |
| One window | `"AAAAAAAAAA"` | `[]` |
| Overlap | `"AAAAAAAAAAA"` | `["AAAAAAAAAA"]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[055 — 438. Find All Anagrams in a String]]: Fixed windows compared by counts. The key idea there: Anagram means equal counts, and a window's counts update in O(1).
- **1044. Longest Duplicate Substring**: The length is unknown, so add binary search and rolling hash.

### In a real interview
Say this: *"A fixed-length window plus a seen set is linear. To save memory I could pack each window into 20 bits."*

## 5. Recall
Q: 187. Repeated DNA Sequences: which pattern, and what is the insight?
A: Substring set. Each window only needs to know whether it was seen before.

Q: 187. Repeated DNA Sequences: what is the analogy, and what does it stand for?
A: A stamp and two ink pads: album → the `seen` set; repeats page → the `repeated` set.

Q: 187. Repeated DNA Sequences: Why two sets?
A: One detects repeats, the other reports each once.

Q: 187. Repeated DNA Sequences: what are the time and space costs?
A: Time O(10·n), which is O(n). Space O(n) windows.

@@ 3 | TOPIC | Practice — Review 05 | Re-solve without looking | MUST
# Review 05

Solve on a blank page. No notes, hints or solutions.

## 187. Repeated DNA Sequences
List 10-letter DNA sequences occurring more than once.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/repeated-dna-sequences/)

## 2017. Grid Game
Minimize what the second robot can collect on a 2-row grid.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/grid-game/)

## 2215. Find the Difference of Two Arrays
For two arrays, list the distinct values found only in each.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-the-difference-of-two-arrays/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 187 | | | | |
| 2017 | | | | |
| 2215 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[030 — 187. Repeated DNA Sequences]]
- [[027 — 2017. Grid Game]]
- [[016 — 2215. Find the Difference of Two Arrays]]

@@ 3 | TOPIC | 031 — 380. Insert Delete GetRandom O(1) | Medium · Map plus list | SHOULD
# 031 — 380. Insert Delete GetRandom O(1)

**Problem 31 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: Map plus list**

[Open on LeetCode](https://leetcode.com/problems/insert-delete-getrandom-o1/)

## 1. Problem Statement
Design `RandomizedSet`. `insert(val)` and `remove(val)` report whether the set changed, and `getRandom()` returns a present value with equal probability. All average O(1).

### Examples
```text
Input:  ["RandomizedSet", "insert", "remove", "insert", "getRandom", "remove", "insert", "getRandom"]
        [[], [1], [2], [2], [], [1], [2], []]
Output: [null, true, false, true, 2, true, false, 2]
```

### Constraints
```text
-2^31 <= val <= 2^31 - 1
At most 2 * 10^5 calls will be made to insert, remove, and getRandom.
There will be at least one element in the data structure when getRandom is called.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Numbered chairs for a raffle
Guests sit on chairs 0 to n-1 with no gaps, so drawing a number picks a winner fairly. A register tells you each guest's chair. When someone leaves, the guest from the last chair moves into the gap and you correct the register.

| In the story | In the solution |
|---|---|
| chairs | the array list |
| register | map from value to index |

### Pattern signal
**Map plus list.** Combine them, and make deletion cheap by giving up order.

### Thought process
1. **First idea:** A set cannot jump to a random element and a list cannot find or remove in O(1).
2. **The insight:** Combine them, and make deletion cheap by giving up order.

### Optimal approach
1. Append and register on insert.
2. On remove move the last value into the hole.
3. Update its index.
4. Delete the end and the key.
5. Draw a random index.

### Dry run
```text
operation   chairs      register
insert 10   [10]        10:0
insert 20   [10,20]     10:0 20:1
insert 30   [10,20,30]  10:0 20:1 30:2
remove 10   [30,20]     20:1 30:0
```

### Why it works
After each operation the chairs have no gaps and the register matches them. So every lookup is direct and every index equally likely.

## 3. Java Solution with Explanation
### Code
```java
class RandomizedSet {
    private final List<Integer> values = new ArrayList<>();
    private final Map<Integer, Integer> indexOf = new HashMap<>();
    private final Random random = new Random();

    public boolean insert(int val) {
        if (indexOf.containsKey(val)) return false;
        indexOf.put(val, values.size());
        values.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!indexOf.containsKey(val)) return false;
        int hole = indexOf.get(val);
        int last = values.get(values.size() - 1);
        values.set(hole, last);
        indexOf.put(last, hole);
        values.remove(values.size() - 1); // removes by index
        indexOf.remove(val);              // last, in case val == last
        return true;
    }

    public int getRandom() {
        return values.get(random.nextInt(values.size()));
    }
}
```

### Code walkthrough
- `values.size()`: The next free chair.
- `values.set(hole, last)`: The last guest fills the gap.
- `indexOf.remove(val)`: After the update, so removing the last guest works.
- `nextInt`: Every occupied chair has the same chance.

### Complexity
- **Time:** O(1) average per operation
- **Space:** O(n)

### Common mistakes
1. **Removing from the middle.** Shifting is O(n).
2. **Forgetting the moved index.** Later removals hit the wrong value.
3. **Deleting the key first.** Removing the last element puts it back.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Repeat insert | `insert(1), insert(1)` | `true, false` |
| Absent | `remove(4)` | `false` |
| Remove only value | `insert(1), remove(1), insert(1)` | `true, true, true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **381. Duplicates allowed**: The register holds a set of indices per value.
- [[276 — 146. LRU Cache]]: Two structures again, for recency. The key idea there: A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).

### In a real interview
Say this: *"A list gives random access and a map gives lookup. Swapping with the last makes deletion O(1)."*

## 5. Recall
Q: 380. Insert Delete GetRandom O(1): which pattern, and what is the insight?
A: Map plus list. Combine them, and make deletion cheap by giving up order.

Q: 380. Insert Delete GetRandom O(1): what is the analogy, and what does it stand for?
A: Numbered chairs for a raffle: chairs → the array list; register → map from value to index.

Q: 380. Insert Delete GetRandom O(1): How is middle deletion O(1)?
A: The last value fills the hole and its index is updated.

Q: 380. Insert Delete GetRandom O(1): what are the time and space costs?
A: Time O(1) average per operation. Space O(n).

@@ 3 | TOPIC | 032 — 304. Range Sum Query 2D - Immutable | Medium · 2D prefix sum | SHOULD
# 032 — 304. Range Sum Query 2D - Immutable

**Problem 32 – Topic: Arrays & Hashing | Difficulty: Medium | Pattern: 2D prefix sum**

[Open on LeetCode](https://leetcode.com/problems/range-sum-query-2d-immutable/)

## 1. Problem Statement
Design `NumMatrix` for a fixed matrix. `sumRegion(row1, col1, row2, col2)` returns the sum of that inclusive rectangle in O(1).

### Examples
```text
Input:  ["NumMatrix", "sumRegion", "sumRegion", "sumRegion"]
        [[[[3, 0, 1, 4, 2], [5, 6, 3, 2, 1], [1, 2, 0, 1, 5], [4, 1, 0, 1, 7], [1, 0, 3, 0, 5]]], [2, 1, 4, 3], [1, 1, 2, 2], [1, 2, 2, 4]]
Output: [null, 8, 11, 12]
```

### Constraints
```text
m == matrix.length
n == matrix[i].length
1 <= m, n <= 200
-10^4 <= matrix[i][j] <= 10^4
0 <= row1 <= row2 < m
0 <= col1 <= col2 < n
At most 10^4 calls will be made to sumRegion.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Cutting a piece from a sheet cake
You know the weight of every piece measured from the top-left corner. For an inner piece take the big one, cut off the strip above and the strip on the left. Their shared corner has been cut twice, so give it back once.

| In the story | In the solution |
|---|---|
| big piece | prefix at the bottom-right |
| corner given back | the final plus |

### Pattern signal
**2D prefix sum.** Every rectangle is made of four rectangles anchored at the top-left.

### Thought process
1. **First idea:** Add up the rectangle on each call, up to 40,000 cells each.
2. **The insight:** Every rectangle is made of four rectangles anchored at the top-left.

### Optimal approach
1. Allocate an extra zero row and column.
2. Build each prefix from above, left and diagonal.
3. Answer with big minus top minus left plus corner.

### Dry run
```text
prefix table of the example
     c0  c1  c2  c3  c4  c5
r0    0   0   0   0   0   0
r1    0   3   3   4   8  10
r2    0   8  14  18  24  27
r3    0   9  17  21  28  36
r4    0  13  22  26  34  49
r5    0  14  23  30  38  58
sumRegion(2,1,4,3) = 38 - 24 - 14 + 8 = 8
```

### Why it works
A cell inside is counted once. A cell only above or only left is removed once. A corner cell is added, removed twice and added back: zero.

## 3. Java Solution with Explanation
### Code
```java
class NumMatrix {
    private final int[][] p; // p[r][c] = first r rows, first c columns

    public NumMatrix(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        p = new int[m + 1][n + 1];
        for (int r = 1; r <= m; r++) {
            for (int c = 1; c <= n; c++) {
                p[r][c] = matrix[r - 1][c - 1] + p[r - 1][c] + p[r][c - 1] - p[r - 1][c - 1];
            }
        }
    }

    public int sumRegion(int row1, int col1, int row2, int col2) {
        int big = p[row2 + 1][col2 + 1];
        int top = p[row1][col2 + 1];
        int left = p[row2 + 1][col1];
        int corner = p[row1][col1];
        return big - top - left + corner;
    }
}
```

### Code walkthrough
- `m + 1`: A crust of zeros removes every border case.
- `- p[r - 1][c - 1]`: Above and left overlap, so count it once.
- `row2 + 1`: The far edges are included.
- `+ corner`: Give back what was cut twice.

### Complexity
- **Time:** O(m·n) to build, O(1) per query
- **Space:** O(m·n)

### Common mistakes
1. **Forgetting the corner.** It is removed twice.
2. **Missing plus one.** The last row or column is left out.
3. **Swapping rows and columns.** Name the four pieces first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One cell | `[ [5] ], (0,0,0,0)` | `5` |
| Whole | `[[1,2],[3,4]], (0,0,1,1)` | `10` |
| Inner cell | `[[1,2],[3,4]], (1,1,1,1)` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[010 — 303. Range Sum Query - Immutable]]: One dimension. The key idea there: The array is frozen, so prepare the totals once.
- **1314. Matrix Block Sum**: Clamp each rectangle and reuse this.

### In a real interview
Say this: *"A fixed matrix with many queries calls for 2D prefix sums. Inclusion and exclusion gives four lookups."*

## 5. Recall
Q: 304. Range Sum Query 2D - Immutable: which pattern, and what is the insight?
A: 2D prefix sum. Every rectangle is made of four rectangles anchored at the top-left.

Q: 304. Range Sum Query 2D - Immutable: what is the analogy, and what does it stand for?
A: Cutting a piece from a sheet cake: big piece → prefix at the bottom-right; corner given back → the final plus.

Q: 304. Range Sum Query 2D - Immutable: Why add the corner back?
A: The top and left pieces both removed it.

Q: 304. Range Sum Query 2D - Immutable: what are the time and space costs?
A: Time O(m·n) to build, O(1) per query. Space O(m·n).

@@ 2 | MODULE | Arrays & Hashing — Hard | 2 problems | MUST
# Arrays & Hashing: Hard

Patterns in this group: Index as bucket, Greedy line packing.

@@ 3 | TOPIC | 033 — 41. First Missing Positive | Hard · Index as bucket | SHOULD
# 033 — 41. First Missing Positive

**Problem 33 – Topic: Arrays & Hashing | Difficulty: Hard | Pattern: Index as bucket**

[Open on LeetCode](https://leetcode.com/problems/first-missing-positive/)

## 1. Problem Statement
Return the smallest positive integer missing from an unsorted array, in O(n) time and O(1) extra space.

### Examples
```text
Input:  nums = [1,2,0]
Output: 3

Input:  nums = [3,4,-1,1]
Output: 2

Input:  nums = [7,8,9,11,12]
Output: 1
```

### Constraints
```text
1 <= nums.length <= 10^5
-2^31 <= nums[i] <= 2^31 - 1
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A hotel with rooms 1 to n
Guests carry tickets. Only tickets 1 to n match a room, so ignore the others. Walk each valid guest to their room, then handle whoever was evicted. Finally walk the corridor: the first wrong door is the answer, and if none is wrong it is n+1.

| In the story | In the solution |
|---|---|
| room v | index v - 1 |
| invalid ticket | a value below 1 or above n |

### Pattern signal
**Index as bucket.** With n slots the answer must be within 1 to n+1, so nothing else needs storing.

### Thought process
1. **First idea:** A set plus counting up from 1 is O(n) but uses O(n) space.
2. **The insight:** With n slots the answer must be within 1 to n+1, so nothing else needs storing.

### Optimal approach
1. Swap each in-range value home unless its twin is there.
2. Scan for the first mismatch.
3. Return its room number.
4. Otherwise return n plus one.

### Dry run
```text
nums = [3,4,-1,1]
i  action           array
0  3 to index 2     [-1,4,3,1]
1  4 to index 3     [-1,1,3,4]
1  1 to index 0     [1,-1,3,4]
2  already home     [1,-1,3,4]
3  already home     [1,-1,3,4]
Scan: index 1 is wrong, return 2
```

### Why it works
Every present value in range ends at home and stays. So the first wrong room is absent and all smaller numbers are present.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            // valid ticket, and its room lacks that number
            while (nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
                int v = nums[i];
                nums[i] = nums[v - 1];
                nums[v - 1] = v;
            }
        }
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) return i + 1;
        }
        return n + 1;
    }
}
```

### Code walkthrough
- `nums[i] >= 1`: Range checks come first so the index is safe.
- `nums[nums[i] - 1] != nums[i]`: Stops duplicates swapping forever.
- `int v = nums[i]`: Save the destination before overwriting.
- `return n + 1`: Every room was right.

### Complexity
- **Time:** O(n), at most n settling swaps
- **Space:** O(1), in place

### Common mistakes
1. **Using `if`.** The evicted value is never placed.
2. **No duplicate check.** [1,1] loops forever.
3. **Forgetting n + 1.** A full hotel needs it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Full | `[1,2,3]` | `4` |
| Duplicates | `[1,1]` | `2` |
| None valid | `[-1,9]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[011 — 448. Find All Numbers Disappeared in an Array]]: Report every wrong room. The key idea there: Values and indices cover the same range, so the array can be its own set.
- [[122 — 287. Find the Duplicate Number]]: No edits allowed, so use cycle detection.

### In a real interview
Say this: *"The answer lies in 1 to n plus one, so the array is its own hash table. Each swap settles a value, so it is linear."*

## 5. Recall
Q: 41. First Missing Positive: which pattern, and what is the insight?
A: Index as bucket. With n slots the answer must be within 1 to n+1, so nothing else needs storing.

Q: 41. First Missing Positive: what is the analogy, and what does it stand for?
A: A hotel with rooms 1 to n: room v → index v - 1; invalid ticket → a value below 1 or above n.

Q: 41. First Missing Positive: What range holds the answer?
A: 1 to n+1.

Q: 41. First Missing Positive: what are the time and space costs?
A: Time O(n), at most n settling swaps. Space O(1), in place.

@@ 3 | TOPIC | 034 — 68. Text Justification | Hard · Greedy line packing | SHOULD
# 034 — 68. Text Justification

**Problem 34 – Topic: Arrays & Hashing | Difficulty: Hard | Pattern: Greedy line packing**

[Open on LeetCode](https://leetcode.com/problems/text-justification/)

## 1. Problem Statement
Arrange `words`, in order, into lines of exactly `maxWidth` characters. Put as many words as fit on each line. Spread spaces between words, extra ones going to the leftmost gaps. The last line and single-word lines are left-aligned and padded on the right.

### Examples
```text
Input:  words = ["This", "is", "an", "example", "of", "text", "justification."], maxWidth = 16
Output: [
           "This    is    an",
           "example  of text",
           "justification.  "
        ]

Input:  words = ["What","must","be","acknowledgment","shall","be"], maxWidth = 16
Output: [
          "What   must   be",
          "acknowledgment  ",
          "shall be        "
        ]

Input:  words = ["Science","is","what","we","understand","well","enough","to","explain","to","a","computer.","Art","is","everything","else","we","do"], maxWidth = 20
Output: [
          "Science  is  what we",
          "understand      well",
          "enough to explain to",
          "a  computer.  Art is",
          "everything  else  we",
          "do                  "
        ]
```

### Constraints
```text
1 <= words.length <= 300
1 <= words[i].length <= 20
words[i] consists of only English letters and symbols.
1 <= maxWidth <= 100
words[i].length <= maxWidth
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Loading a bookshelf
Fill a shelf with books, allowing one finger of space between them, until the next won't fit. Then share the spare space between the gaps like dealing cards from the left. The top shelf, the last one, is just pushed left.

| In the story | In the solution |
|---|---|
| books that fit | words `start` to `end - 1` |
| dealing spare space | quotient and remainder |

### Pattern signal
**Greedy line packing.** Separate them: first decide which words form a line, then format that line.

### Thought process
1. **First idea:** There is little to search, since the packing is forced. The difficulty is doing the two jobs at once.
2. **The insight:** Separate them: first decide which words form a line, then format that line.

### Optimal approach
1. Extend the line while letters plus minimum gaps fit.
2. Left-align a last or single-word line.
3. Otherwise give each gap the quotient and the first gaps one more.
4. Continue.

### Dry run
```text
This is an example of text justification., width 16
words               letters  spaces  gaps  shared
This is an          8        8       2     4,4
example of text     13       3       2     2,1
justification.      14       2       0     pad right
Lines, with dots for spaces:
This....is....an
example..of.text
justification...  <- the final dot is the word's own, then 2 spaces
```

### Why it works
The next word is rejected only if it cannot fit, so packing is maximal. Quotient and remainder use all spaces, with gaps differing by at most one.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        while (start < words.length) {
            int end = start, letters = 0;
            // end - start = gaps needed if words[end] joins
            while (end < words.length
                    && letters + words[end].length() + (end - start) <= maxWidth) {
                letters += words[end].length();
                end++;
            }
            lines.add(format(words, start, end, letters, maxWidth));
            start = end;
        }
        return lines;
    }

    private String format(String[] words, int start, int end, int letters, int width) {
        int gaps = end - start - 1;
        boolean leftAlign = gaps == 0 || end == words.length;
        StringBuilder line = new StringBuilder(words[start]);
        for (int g = 0; g < gaps; g++) {
            int spaces = 1;
            if (!leftAlign) {
                spaces = (width - letters) / gaps;
                if (g < (width - letters) % gaps) spaces++; // deal from the left
            }
            line.append(" ".repeat(spaces)).append(words[start + 1 + g]);
        }
        line.append(" ".repeat(width - line.length()));
        return line.toString();
    }
}
```

### Code walkthrough
- `(end - start)`: The finger gaps needed if this book joins.
- `gaps == 0`: One book, pushed left. It also avoids dividing by zero.
- `end == words.length`: The last shelf.
- `g < (width - letters) % gaps`: The first few gaps get a spare space.
- `width - line.length()`: Right padding, zero on a justified line.

### Complexity
- **Time:** O(T + L·W) for T letters and L lines of width W
- **Space:** O(W) besides the output

### Common mistakes
1. **Justifying the last line.** It must be left-aligned.
2. **One word.** Division by zero without the check.
3. **Forgetting minimum gaps.** Lines overflow.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Exact fit | `["abc"], 3` | `["abc"]` |
| Last line | `["a","b"], 5` | `["a b  "]` |
| Single on a line | `["ab","c"], 3` | `["ab ","c  "]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **1592. Rearrange Spaces Between Words**: Only the formatting half.
- **418. Sentence Screen Fitting**: Only the packing half, repeated.

### In a real interview
Say this: *"I separate greedy packing from formatting. I state the single-word and last-line rules before coding."*

## 5. Recall
Q: 68. Text Justification: which pattern, and what is the insight?
A: Greedy line packing. Separate them: first decide which words form a line, then format that line.

Q: 68. Text Justification: what is the analogy, and what does it stand for?
A: Loading a bookshelf: books that fit → words `start` to `end - 1`; dealing spare space → quotient and remainder.

Q: 68. Text Justification: How are extra spaces shared?
A: Every gap gets the quotient, the first remainder gaps one more.

Q: 68. Text Justification: what are the time and space costs?
A: Time O(T + L·W) for T letters and L lines of width W. Space O(W) besides the output.

@@ 3 | TOPIC | Practice — Pattern summary: Arrays & Hashing | Key patterns of the topic | MUST
# Pattern summary: Arrays & Hashing

Remember what you have scanned so that each lookup costs O(1). A map answers 'where or how many', a set answers 'is it there', and a prefix sum turns a range into one subtraction.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Hash set | Report whether any value occurs twice. (217) | Comparing every pair is O(n²) |
| Frequency count | Decide whether t is a rearrangement of s. (242) | The 26-slot array does not work for Unicode |
| Scan from the end | Return the length of the last word in a string. (58) | Forgetting trailing spaces |
| Complement map | Return indices of the two numbers that add up to target. (1) | Storing before the lookup lets an element pair with itself |
| In-place partition | Remove all copies of val in place and return the new length. (27) | Advancing past a value that was just swapped in |
| Two-way mapping | Decide whether characters of s map one-to-one onto t. (205, 290) | A one-way map lets two letters share an image |
| Greedy scan | Decide whether n flowers fit with no two adjacent. (605) | Reading outside the array at either end |
| Boyer-Moore voting | Find the value occurring more than n/2 times. (169) | Voting only works when a majority is guaranteed |
| Prefix sum | Find the first index whose left sum equals its right sum. (724, 303) | Counting the pivot on either side |
| Index as bucket | List the values from 1 to n missing from the array. (448, 41) | Swapping equal values forever |
| Direct addressing | Design a hash set without library hash tables. (705) | This is impossible when the key range is unbounded |
| Separate chaining | Design a hash map without library hash tables. (706) | Not updating a key that already exists |
| KMP prefix function | Find the first position of needle in haystack. (28) | Resetting the needle pointer to zero on every mismatch |
| Set difference | For two arrays, list the distinct values found only in each. (2215) | Returning duplicates |
| Canonical key | Group words that are anagrams of each other. (49) | An array used as a map key compares by identity |
| Merge sort | Sort an array in O(n log n) without built-in sort. (912) | Forgetting the leftovers of one half |
| Prefix and suffix product | For each index return the product of all other values, without division. (238) | Division breaks on zeros |
| Seen sets | Check that a 9x9 board has no repeat in any row, column or box. (36) | Validity does not require solvability |
| Sequence starts | Find the longest run of consecutive values in O(n). (128) | Counting from every value is O(n²) |
| ID map | Design a URL shortener that can encode and decode. (535) | Keys that collide |
| Prefix frequency | Draw a vertical line through a wall crossing the fewest bricks. (554) | Counting the wall's right edge as a gap |
| Greedy gains | Maximize profit with unlimited buy and sell trades. (122) | Looking for only one best trade |
| Prefix sum map | Count subarrays whose sum is k. (560) | A sliding window fails with negatives; seed the map with 0 |
| Middle character | Count distinct palindromic subsequences of length 3. (1930) | Counting the same palindrome twice |
| Prefix and minimax | Minimize what the second robot can collect on a 2-row grid. (2017) | Maximizing robot one's own score |
| Custom order | Arrange numbers to form the largest concatenation. (179) | Plain descending order puts 30 before 3 |
| Prefix remainder | Find a subarray of length 2 or more whose sum is a multiple of k. (523) | Overwriting the first index |
| Substring set | List 10-letter DNA sequences occurring more than once. (187) | Adding a repeat several times |
| Map plus list | Insert, delete and pick a random element in O(1). (380) | Not updating the moved element's index |
| 2D prefix sum | Answer rectangle-sum queries on a fixed matrix. (304) | Subtracting the overlap twice |
| Greedy line packing | Format words into fully justified lines of a given width. (68) | Treating the last line like the others |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| seen before, distinct | the doorman's guest list | set | 217, 2215, 187 |
| same letters, how many | tiles on a tray | frequency count | 242, 49 |
| find the partner | the cloakroom book | complement map | 1 |
| one-to-one | the spy's two code pages | two maps | 205, 290 |
| sum of a range | kilometre stones | prefix sum | 303, 304 |
| subarray with an exact sum | the altitude diary | prefix map | 560 |
| divisible by k | the hour hand | prefix remainder | 523 |
| everything except me | two messengers | prefix and suffix | 238 |
| values from 1 to n, no space | numbered hotel rooms | index as bucket | 448, 41 |
| longest run in any order | count only from the engine | sequence starts | 128 |

## Which pattern do I pick?

1. Is it about a **contiguous range**? Use prefix sums, and add a map if you must find or count ranges.
2. Are the values **1 to n** with O(1) space required? Use the array as rooms.
3. Do you need only **yes or no**? Use a set.
4. Do you need **how many or where**? Use a map, or an array when the key range is small.
5. Do you need **groups**? Invent a label that is equal exactly for group members.

Q: Which signals point to Arrays & Hashing?
A: seen before; count; group; pair; many range sums.

Q: What is the core idea of Arrays & Hashing?
A: Remember what you have scanned so that each lookup costs O(1). A map answers 'where or how many', a set answers 'is it there', and a prefix sum turns a range into one subtraction.
