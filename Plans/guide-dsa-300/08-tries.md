@@ 1 | MODULE | Phase 8 — Tries | prefix; dictionary; autocomplete; many words at once | MUST
# Phase 8: Tries

Words with the same prefix share a path from the root. A search costs the word's length, whatever the dictionary size.

**Signals:** prefix; dictionary; autocomplete; many words at once.

## The big analogy: a river delta of words

All words start at one spring, and the river splits only where two words differ. `car`, `card` and `care` share one channel for `car`. **Following a word costs its own length, however many words the river carries.** A flag on the bank says "a word may end here".

| Part | Picture | Java |
|---|---|---|
| Node | a fork in the river | `Trie[] child` |
| Edge | a letter-named channel | index `c - 'a'` |
| End of word | a flag on the bank | `boolean word` |
| Missing child | a dry bed, so stop searching | `null` |

## Template
```java
class Trie {
    Trie[] child = new Trie[26];
    boolean word;
}

Trie node = root;
for (char c : text.toCharArray()) {
    if (node.child[c - 'a'] == null) node.child[c - 'a'] = new Trie();
    node = node.child[c - 'a'];
}
node.word = true;
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Tries — Easy | 1 problems | MUST
# Tries: Easy

Patterns in this group: Prefix scan.

@@ 3 | TOPIC | 167 — 14. Longest Common Prefix | Easy · Prefix scan | NICE
# 167 — 14. Longest Common Prefix

**Problem 167 – Topic: Tries | Difficulty: Easy | Pattern: Prefix scan**

[Open on LeetCode](https://leetcode.com/problems/longest-common-prefix/)

## 1. Problem Statement
You get an array of strings. Return the longest string that is a prefix of every one of them, or an empty string if they share no prefix.

### Examples
```text
Input:  strs = ["flower","flow","flight"]
Output: "fl"

Input:  strs = ["dog","racecar","car"]
Output: ""
```

### Constraints
```text
1 <= strs.length <= 200
0 <= strs[i].length <= 200
strs[i] consists of only lowercase English letters if it is non-empty.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Friends walking until the first fork
Friends set out along one road. They share it while everyone's next step agrees. The first disagreement, or the first person whose journey ends, ends the shared stretch.

| In the story | In the solution |
|---|---|
| step | column |
| fork | mismatch |

### Pattern signal
**Prefix scan.** It is a trie's path before the first branch, and a column scan finds it without building one.

### Thought process
1. **First idea:** Test every prefix against every word, rechecking characters.
2. **The insight:** It is a trie's path before the first branch, and a column scan finds it without building one.

### Optimal approach
1. Use the first word as the guide.
2. Compare one column across all.
3. Stop at a short word or a difference.
4. Return the prefix.

### Dry run
```text
flower, flow, flight
column  letters  result
0       f f f    f
1       l l l    fl
2       o o i    stop
```

### Why it works
Every earlier column is common to all and the stopping column cannot belong to any common prefix.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        String first = strs[0];
        for (int i = 0; i < first.length(); i++) {
            for (String word : strs) {
                if (i == word.length() || word.charAt(i) != first.charAt(i)) {
                    return first.substring(0, i);
                }
            }
        }
        return first;
    }
}
```

### Code walkthrough
- `strs[0]`: Any word can serve as the guide.
- `i == word.length()`: Someone's journey has ended.
- `substring(0, i)`: The stretch shared by all.

### Complexity
- **Time:** O(total characters)
- **Space:** O(1) besides the result

### Common mistakes
1. **A short word crashes charAt.** Fix: test its length first.
2. **Only prefixes count.** Fix: stop at the first mismatch.
3. **Later columns are irrelevant.** Fix: return immediately.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `["a"]` | `"a"` |
| Empty word | `["","b"]` | `""` |
| None | `["a","b"]` | `""` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[169 — 208. Implement Trie (Prefix Tree)]]: The general tool. The key idea there: Shared prefixes can share one path, so a lookup costs only the key's length.
- [[168 — 1268. Search Suggestions System]]: Each prefix length. The key idea there: Sorting makes each prefix's matches contiguous and inside the previous block.

### In a real interview
Say it in this order:

1. **Brute force:** Test every prefix against every word, rechecking characters.
2. **Insight:** It is a trie's path before the first branch, and a column scan finds it without building one.
3. **Approach in a few words:** Vertical scanning stops at the first difference.
4. **Cost:** time O(total characters); space O(1) besides the result.

## 5. Recall
Q: 14. Longest Common Prefix: which pattern, and what is the insight?
A: Prefix scan. It is a trie's path before the first branch, and a column scan finds it without building one.

Q: 14. Longest Common Prefix: what is the analogy, and what does it stand for?
A: Friends walking until the first fork: step → column; fork → mismatch.

Q: 14. Longest Common Prefix: What ends the common prefix?
A: A mismatch or any word ending.

Q: 14. Longest Common Prefix: what are the time and space costs?
A: Time O(total characters). Space O(1) besides the result.

@@ 2 | MODULE | Tries — Medium | 3 problems | MUST
# Tries: Medium

Patterns in this group: Sorted prefix range, Prefix tree, Trie with wildcard.

@@ 3 | TOPIC | 168 — 1268. Search Suggestions System | Medium · Sorted prefix range | SHOULD
# 168 — 1268. Search Suggestions System

**Problem 168 – Topic: Tries | Difficulty: Medium | Pattern: Sorted prefix range**

[Open on LeetCode](https://leetcode.com/problems/search-suggestions-system/)

## 1. Problem Statement
After each typed letter suggest up to three lexically smallest products starting with the typed prefix.

### Examples
```text
Input:  products = ["mobile","mouse","moneypot","monitor","mousepad"], searchWord = "mouse"
Output: [["mobile","moneypot","monitor"],["mobile","moneypot","monitor"],["mouse","mousepad"],["mouse","mousepad"],["mouse","mousepad"]]

Input:  products = ["havana"], searchWord = "havana"
Output: [["havana"],["havana"],["havana"],["havana"],["havana"],["havana"]]
```

### Constraints
```text
1 <= products.length <= 1000
1 <= products[i].length <= 3000
1 <= sum(products[i].length) <= 2 * 10^4
All the strings of products are unique.
products[i] consists of lowercase English letters.
1 <= searchWord.length <= 1000
searchWord consists of lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two fingers in a phone book
In a phone book all names with one prefix form a block. Each typed letter moves your two fingers inward, and the first three names under the left finger are the suggestions.

| In the story | In the solution |
|---|---|
| fingers | low and high |
| block | matching range |

### Pattern signal
**Sorted prefix range.** Sorting makes each prefix's matches contiguous and inside the previous block.

### Thought process
1. **First idea:** Filter and sort again after each letter.
2. **The insight:** Sorting makes each prefix's matches contiguous and inside the previous block.

### Optimal approach
1. Sort.
2. Move both ends past non-matching words.
3. Copy up to three from the left.
4. Repeat per letter.

### Dry run
```text
sorted mobile, moneypot, monitor, mouse, mousepad
typed  block  suggestions
mo     all 5  mobile, moneypot, monitor
mou    last 2 mouse, mousepad
```

### Why it works
All words with a prefix are consecutive, so only ends need removing, and the first three are the smallest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<String>> suggestedProducts(String[] products, String searchWord) {
        Arrays.sort(products);
        List<List<String>> result = new ArrayList<>();
        int low = 0, high = products.length - 1;
        for (int i = 0; i < searchWord.length(); i++) {
            char c = searchWord.charAt(i);
            while (low <= high && (products[low].length() <= i || products[low].charAt(i) != c)) low++;
            while (low <= high && (products[high].length() <= i || products[high].charAt(i) != c)) high--;
            List<String> three = new ArrayList<>();
            for (int j = low; j <= high && j < low + 3; j++) three.add(products[j]);
            result.add(three);
        }
        return result;
    }
}
```

### Code walkthrough
- `Arrays.sort`: Build the phone book.
- `products[low].length() <= i`: A shorter word can't match this letter.
- `j < low + 3`: At most three suggestions.

### Complexity
- **Time:** O(sort + n + L), with L the typed length
- **Space:** O(log n) sort stack, besides the output

### Common mistakes
1. **A short product crashes.** Fix: test its length.
2. **Every letter needs a list.** Fix: continue when empty.
3. **Resetting the range repeats work.** Fix: only shrink it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `["a"], "b"` | `[ [] ]` |
| One | `["a"], "a"` | `[ ["a"] ]` |
| Longer | `["a"], "ab"` | `[ ["a"],[] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[169 — 208. Implement Trie (Prefix Tree)]]: Changing lists. The key idea there: Shared prefixes can share one path, so a lookup costs only the key's length.
- [[063 — 658. Find K Closest Elements]]: A shrinking range. The key idea there: The answer is contiguous, and "should move right" changes only once as the start grows.

### In a real interview
Say this: *"Sort and narrow, or a trie if updates are needed."*

## 5. Recall
Q: 1268. Search Suggestions System: which pattern, and what is the insight?
A: Sorted prefix range. Sorting makes each prefix's matches contiguous and inside the previous block.

Q: 1268. Search Suggestions System: what is the analogy, and what does it stand for?
A: Two fingers in a phone book: fingers → low and high; block → matching range.

Q: 1268. Search Suggestions System: Why is sorting enough for suggestions?
A: Words sharing a prefix become contiguous.

Q: 1268. Search Suggestions System: what are the time and space costs?
A: Time O(sort + n + L), with L the typed length. Space O(log n) sort stack, besides the output.

@@ 3 | TOPIC | Practice — Review 28 | Re-solve without looking | MUST
# Review 28

Solve on a blank page. No notes, hints or solutions.

## 1268. Search Suggestions System
Suggest three smallest products after each typed letter.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/search-suggestions-system/)

## 124. Binary Tree Maximum Path Sum
Find the maximum path sum in a tree.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/binary-tree-maximum-path-sum/)

## 1448. Count Good Nodes in Binary Tree
Count nodes with no larger ancestor.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/count-good-nodes-in-binary-tree/)

## 25. Reverse Nodes in k-Group
Reverse each full group of k nodes.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/reverse-nodes-in-k-group/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 1268 | | | | |
| 124 | | | | |
| 1448 | | | | |
| 25 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[168 — 1268. Search Suggestions System]]
- [[165 — 124. Binary Tree Maximum Path Sum]]
- [[154 — 1448. Count Good Nodes in Binary Tree]]
- [[130 — 25. Reverse Nodes in k-Group]]

@@ 3 | TOPIC | 169 — 208. Implement Trie (Prefix Tree) | Medium · Prefix tree | MUST
# 169 — 208. Implement Trie (Prefix Tree)

**Problem 169 – Topic: Tries | Difficulty: Medium | Pattern: Prefix tree**

[Open on LeetCode](https://leetcode.com/problems/implement-trie-prefix-tree/)

## 1. Problem Statement
Implement a Trie: `insert(word)` stores a word, `search(word)` returns true only if exactly that word was inserted, and `startsWith(prefix)` returns true if any inserted word begins with the prefix.

### Examples
```text
Input:  ["Trie", "insert", "search", "search", "startsWith", "insert", "search"]
        [[], ["apple"], ["apple"], ["app"], ["app"], ["app"], ["app"]]
Output: [null, null, true, false, true, null, true]
```

### Constraints
```text
1 <= word.length, prefix.length <= 2000
word and prefix consist only of lowercase English letters.
At most 3 * 10^4 calls in total will be made to insert, search, and startsWith.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A river with flags
Words are channels of a river delta. Dig one only where it is missing and plant a flag where a word ends. An exact search needs the flag, a prefix search only the water.

| In the story | In the solution |
|---|---|
| channel | child |
| flag | word |

### Pattern signal
**Prefix tree.** Shared prefixes can share one path, so a lookup costs only the key's length.

### Thought process
1. **First idea:** Scan a list of all words for each query.
2. **The insight:** Shared prefixes can share one path, so a lookup costs only the key's length.

### Optimal approach
1. Create missing children on insert.
2. Flag the last node.
3. Follow children on lookup.
4. Require the flag only for search.

### Dry run
```text
insert apple
query       path  flag  answer
search app  yes   no    false
prefix app  yes   -     true
```

### Why it works
A path exists exactly for inserted prefixes and a flag exactly at inserted words.

## 3. Java Solution with Explanation
### Code
```java
class Trie {
    private final Trie[] child = new Trie[26];
    private boolean word;

    public void insert(String text) {
        Trie node = this;
        for (char c : text.toCharArray()) {
            if (node.child[c - 'a'] == null) node.child[c - 'a'] = new Trie();
            node = node.child[c - 'a'];
        }
        node.word = true;
    }

    public boolean search(String text) {
        Trie end = follow(text);
        return end != null && end.word;
    }

    public boolean startsWith(String prefix) {
        return follow(prefix) != null;
    }

    private Trie follow(String text) {
        Trie node = this;
        for (char c : text.toCharArray()) {
            node = node.child[c - 'a'];
            if (node == null) return null;
        }
        return node;
    }
}
```

### Code walkthrough
- `node.child[c - 'a'] = new Trie()`: Dig only a missing channel.
- `node.word = true`: A word ends here.
- `end.word`: Only an exact search needs the flag.

### Complexity
- **Time:** O(L) per operation
- **Space:** O(26 · total characters)

### Common mistakes
1. **Without flags every prefix is a word.** Fix: mark endings.
2. **Replacing a child erases words.** Fix: create only if absent.
3. **A list of children needs scanning.** Fix: use an array.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Prefix | `insert ab, search a` | `false` |
| Absent | `search a` | `false` |
| Nested | `insert ab and a, search ab` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[170 — 211. Design Add and Search Words Data Structure]]: Branching lookup. The key idea there: Only a dot causes branching, and the trie limits it to real prefixes.
- **648. Replace Words**: Stop at a flag.

### In a real interview
Say it in this order:

1. **Brute force:** Scan a list of all words for each query.
2. **Insight:** Shared prefixes can share one path, so a lookup costs only the key's length.
3. **Approach in a few words:** Cost depends on key length, not dictionary size.
4. **Cost:** time O(L) per operation; space O(26 · total characters).

## 5. Recall
Q: 208. Implement Trie (Prefix Tree): which pattern, and what is the insight?
A: Prefix tree. Shared prefixes can share one path, so a lookup costs only the key's length.

Q: 208. Implement Trie (Prefix Tree): what is the analogy, and what does it stand for?
A: A river with flags: channel → child; flag → word.

Q: 208. Implement Trie (Prefix Tree): How do search and startsWith differ?
A: Search also needs an end marker.

Q: 208. Implement Trie (Prefix Tree): what are the time and space costs?
A: Time O(L) per operation. Space O(26 · total characters).

@@ 3 | TOPIC | 170 — 211. Design Add and Search Words Data Structure | Medium · Trie with wildcard | MUST
# 170 — 211. Design Add and Search Words Data Structure

**Problem 170 – Topic: Tries | Difficulty: Medium | Pattern: Trie with wildcard**

[Open on LeetCode](https://leetcode.com/problems/design-add-and-search-words-data-structure/)

## 1. Problem Statement
Design `WordDictionary`. `addWord(word)` stores a word and `search(word)` returns true if any stored word matches it completely. A dot in the searched word matches any single letter.

### Examples
```text
Input:  ["WordDictionary","addWord","addWord","addWord","search","search","search","search"]
        [[],["bad"],["dad"],["mad"],["pad"],["bad"],[".ad"],["b.."]]
Output: [null,null,null,null,false,true,true,true]
```

### Constraints
```text
1 <= word.length <= 25
word in addWord consists of lowercase English letters.
word in search consist of '.' or lowercase English letters.
There will be at most 2 dots in word for search queries.
At most 10^4 calls will be made to addWord and search.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A river guide with a smudged map
A river guide holds a smudged map. A readable letter names one channel. At a smudge they try every channel and succeed if any route ends at a flag.

| In the story | In the solution |
|---|---|
| smudge | dot |
| try all | DFS |

### Pattern signal
**Trie with wildcard.** Only a dot causes branching, and the trie limits it to real prefixes.

### Thought process
1. **First idea:** Compare the pattern with every stored word.
2. **The insight:** Only a dot causes branching, and the trie limits it to real prefixes.

### Optimal approach
1. Insert as in a trie.
2. At the pattern's end check the flag.
3. Follow a letter.
4. Try every child at a dot.

### Dry run
```text
words bad, dad, mad. query .ad
position  nodes tried
.         b, d, m
a         from b exists
d         flag, true
```

### Why it works
Each branch is one legal replacement of the dot and all possible ones are tried.

## 3. Java Solution with Explanation
### Code
```java
class WordDictionary {
    private final WordDictionary[] child = new WordDictionary[26];
    private boolean word;

    public void addWord(String text) {
        WordDictionary node = this;
        for (char c : text.toCharArray()) {
            if (node.child[c - 'a'] == null) node.child[c - 'a'] = new WordDictionary();
            node = node.child[c - 'a'];
        }
        node.word = true;
    }

    public boolean search(String text) {
        return match(text, 0);
    }

    private boolean match(String text, int i) {
        if (i == text.length()) return word;
        char c = text.charAt(i);
        if (c != '.') return child[c - 'a'] != null && child[c - 'a'].match(text, i + 1);
        for (WordDictionary next : child) {
            if (next != null && next.match(text, i + 1)) return true;
        }
        return false;
    }
}
```

### Code walkthrough
- `return word`: The end needs a flag, not just water.
- `c != '.'`: A letter has one channel.
- `next.match(text, i + 1)`: Any successful route is enough.

### Complexity
- **Time:** O(L), up to O(26^d · L) with d dots
- **Space:** O(26 · total characters)

### Common mistakes
1. **The first branch may fail.** Fix: try every child.
2. **A dot is exactly one letter.** Fix: always advance.
3. **A prefix is not a word.** Fix: check the flag.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Dot | `add a, search .` | `true` |
| Length | `add a, search ..` | `false` |
| Empty | `search a` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[169 — 208. Implement Trie (Prefix Tree)]]: No smudge. The key idea there: Shared prefixes can share one path, so a lookup costs only the key's length.
- [[263 — 10. Regular Expression Matching]]: Star needs DP. The key idea there: The two indices alone decide whether the rest matches.

### In a real interview
Say it in this order:

1. **Brute force:** Compare the pattern with every stored word.
2. **Insight:** Only a dot causes branching, and the trie limits it to real prefixes.
3. **Approach in a few words:** Trie DFS branching at wildcards.
4. **Cost:** time O(L), up to O(26^d · L) with d dots; space O(26 · total characters).

## 5. Recall
Q: 211. Design Add and Search Words Data Structure: which pattern, and what is the insight?
A: Trie with wildcard. Only a dot causes branching, and the trie limits it to real prefixes.

Q: 211. Design Add and Search Words Data Structure: what is the analogy, and what does it stand for?
A: A river guide with a smudged map: smudge → dot; try all → DFS.

Q: 211. Design Add and Search Words Data Structure: What happens at a dot?
A: Search every existing child.

Q: 211. Design Add and Search Words Data Structure: what are the time and space costs?
A: Time O(L), up to O(26^d · L) with d dots. Space O(26 · total characters).

@@ 2 | MODULE | Tries — Hard | 1 problems | MUST
# Tries: Hard

Patterns in this group: Trie-pruned DFS.

@@ 3 | TOPIC | 171 — 212. Word Search II | Hard · Trie-pruned DFS | MUST
# 171 — 212. Word Search II

**Problem 171 – Topic: Tries | Difficulty: Hard | Pattern: Trie-pruned DFS**

[Open on LeetCode](https://leetcode.com/problems/word-search-ii/)

## 1. Problem Statement
Return the given words that can be traced through adjacent cells, each cell once per word.

### Examples
```text
Input:  board = [["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]], words = ["oath","pea","eat","rain"]
Output: ["eat","oath"]

Input:  board = [["a","b"],["c","d"]], words = ["abcb"]
Output: []
```

### Constraints
```text
m == board.length
n == board[i].length
1 <= m, n <= 12
board[i][j] is a lowercase English letter.
1 <= words.length <= 3 * 10^4
1 <= words[i].length <= 10
words[i] consists of lowercase English letters.
All the strings of words are unique.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Hiking with a map of valid trails
Rather than hiking once for each destination you carry one map of all valid trails. Turn back the moment the map has no such trail and cross off each destination found.

| In the story | In the solution |
|---|---|
| map | trie |
| cross off | word = null |

### Pattern signal
**Trie-pruned DFS.** One path in the grid can test the prefixes of all words at once.

### Thought process
1. **First idea:** Run a separate grid search for each of 30,000 words.
2. **The insight:** One path in the grid can test the prefixes of all words at once.

### Optimal approach
1. Build a trie holding words at ends.
2. Start at every cell.
3. Stop without a child.
4. Record and clear a word.
5. Mark and restore.

### Dry run
```text
oath
cell   prefix  action
(0,0)  o       child
(0,1)  oa      child
(1,1)  oat     child
(2,1)  oath    record
```

### Why it works
Every grid path spelling some word's prefix is explored and no other path could lead to an answer.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private static class Node26 {
        Node26[] child = new Node26[26];
        String word;
    }

    public List<String> findWords(char[][] board, String[] words) {
        Node26 root = new Node26();
        for (String word : words) {
            Node26 node = root;
            for (char c : word.toCharArray()) {
                if (node.child[c - 'a'] == null) node.child[c - 'a'] = new Node26();
                node = node.child[c - 'a'];
            }
            node.word = word;
        }
        List<String> found = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) explore(board, r, c, root, found);
        }
        return found;
    }

    private void explore(char[][] board, int r, int c, Node26 node, List<String> found) {
        if (r < 0 || c < 0 || r == board.length || c == board[0].length || board[r][c] == '#') return;
        char letter = board[r][c];
        Node26 next = node.child[letter - 'a'];
        if (next == null) return;
        if (next.word != null) {
            found.add(next.word);
            next.word = null;
        }
        board[r][c] = '#';
        explore(board, r + 1, c, next, found);
        explore(board, r - 1, c, next, found);
        explore(board, r, c + 1, next, found);
        explore(board, r, c - 1, next, found);
        board[r][c] = letter;
    }
}
```

### Code walkthrough
- `node.word = word`: The end stores the word itself.
- `next == null`: The map has no such trail.
- `next.word = null`: Cross off, so it's reported once.
- `board[r][c] = letter`: Wipe the mark for other paths.

### Complexity
- **Time:** O(m·n·4·3^(L-1) + total characters)
- **Space:** O(total characters) plus O(L) stack

### Common mistakes
1. **One search per word is too slow.** Fix: share a trie.
2. **Longer words continue past a match.** Fix: keep exploring.
3. **An unrestored cell blocks paths.** Fix: undo it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `board a, word a` | `[a]` |
| Reuse | `board a, word aa` | `[]` |
| Nested | `board ab, words a, ab` | `[a,ab]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[193 — 79. Word Search]]: No trie. The key idea there: Continue only while the path spells a prefix, marking only the current path.
- [[170 — 211. Design Add and Search Words Data Structure]]: DFS on a trie. The key idea there: Only a dot causes branching, and the trie limits it to real prefixes.

### In a real interview
Say it in this order:

1. **Brute force:** Run a separate grid search for each of 30,000 words.
2. **Insight:** One path in the grid can test the prefixes of all words at once.
3. **Approach in a few words:** A trie prunes one shared backtracking.
4. **Cost:** time O(m·n·4·3^(L-1) + total characters); space O(total characters) plus O(L) stack.

## 5. Recall
Q: 212. Word Search II: which pattern, and what is the insight?
A: Trie-pruned DFS. One path in the grid can test the prefixes of all words at once.

Q: 212. Word Search II: what is the analogy, and what does it stand for?
A: Hiking with a map of valid trails: map → trie; cross off → word = null.

Q: 212. Word Search II: When does Word Search II stop a path?
A: When no word has that prefix.

Q: 212. Word Search II: what are the time and space costs?
A: Time O(m·n·4·3^(L-1) + total characters). Space O(total characters) plus O(L) stack.

@@ 3 | TOPIC | Practice — Pattern summary: Tries | Key patterns of the topic | MUST
# Pattern summary: Tries

Words with the same prefix share a path from the root. A search costs the word's length, whatever the dictionary size.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Prefix scan | Find the longest prefix shared by all strings. (14) | An exhausted shorter string |
| Sorted prefix range | Suggest three smallest products after each typed letter. (1268) | Scanning all products for each prefix |
| Prefix tree | Implement insert, search and prefix check. (208) | Treating a prefix as a word |
| Trie with wildcard | Design a dictionary where dot matches any letter. (211) | Returning after the first child fails |
| Trie-pruned DFS | Find all dictionary words in a letter grid. (212) | Separate searches per word |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| prefix of all | a river before its first fork | prefix scan | 14 |
| autocomplete | fingers narrowing a phone book | sorted range | 1268 |
| insert, search by prefix | the delta | trie | 208 |
| wildcard | try every channel at that fork | trie and DFS | 211 |
| many words in a grid | hiking with a map of valid trails | trie pruning | 212 |

## Trie, set or sorting?

1. **Exact lookups only?** A hash set is simpler.
2. **Prefixes on a fixed list?** Sorting may be enough.
3. **Prefixes with inserts, wildcards, or pruning a search?** Use a trie.

Q: Which signals point to Tries?
A: prefix; dictionary; autocomplete; many words at once.

Q: What is the core idea of Tries?
A: Words with the same prefix share a path from the root. A search costs the word's length, whatever the dictionary size.
