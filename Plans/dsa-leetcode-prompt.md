# Prompt: 98-day DSA interview plan (Java)

> To run: open Claude Code in `D:\mylearn\Plans` and say `Read dsa-leetcode-prompt.md and follow it`.
> If a session stops early, say the same thing again. It resumes where it stopped.

## 1. Role and goal

You are a patient DSA interview coach and senior Java engineer. Build a progressive, pattern-based study plan that takes me from absolute beginner to solving Hard LeetCode-style interview problems.

- I know basic Java syntax. Assume no DSA and no Big-O knowledge.
- One new problem per day, 98 days in total: 84 problem days and 14 review days (every 7th day).
- The notes must be complete enough that I never need another website to understand a problem or its solution.
- The goal is recognizing patterns in unseen problems, not memorizing solutions.

The result is a CSV that I import into my Learning Tracker app, in the same format as `plan-example.csv`.

## 2. Deliverables

| Path | Purpose |
|---|---|
| `guide-dsa/00-orientation.md` | How to use the plan, Big-O primer, Java collections primer, full day-by-day outline |
| `guide-dsa/01-arrays-hashing.md` to `guide-dsa/13-mixed-hard.md` | One source file per phase |
| `guide-dsa/_sections.txt` | The required headings from section 6, one per line |
| `dsa-leetcode-plan.csv` | Built with `python build_plan_csv.py guide-dsa dsa-leetcode-plan.csv` |

Never write the CSV by hand. Do not change `build_plan_csv.py`, `plan-example.csv` or any other plan. Do not commit.

## 3. How to work

1. Read `plan-example.csv`, `build_plan_csv.py` and the first 60 lines of `guide-math/04-trigonometry/01-right-triangles.md` for the format and tone.
2. If `guide-dsa/` exists, find the last complete day and continue after it. Do not rewrite finished days.
3. Otherwise show me the outline (Day, Topic, Problem, Difficulty) from section 9, then start without waiting for approval.
4. Write one phase at a time, in order. After each phase run the checks in section 10, fix any failures and report one line of progress.
5. Keep Day 90 as detailed as Day 1. Never write "similar to above", "TODO" or "...". If you run low on room, finish the current day and stop cleanly.

## 4. Row format

Each row starts with a marker line. Everything up to the next marker is that row's Markdown note.

```text
@@ level | TYPE | name | description | PRIORITY
```

The builder produces these columns: `level,type,name,description,priority,completed,content`.

| Row | level | TYPE | name | PRIORITY |
|---|---|---|---|---|
| Phase | 1 | MODULE | `Phase N — Topic` | MUST |
| Pattern group | 2 | MODULE | the group name from section 9 | MUST |
| Problem day | 3 | TOPIC | `Day 001 — 217. Contains Duplicate` | as given in section 9 |
| Pattern summary | 3 | TOPIC | `Practice — Pattern summary: Topic` | MUST |
| Review day | 3 | TOPIC | `Practice — Day 007: Review` | MUST |
| Orientation | 3 | TOPIC | `Practice — Start here: ...` | MUST |

- Phase 0 is `Phase 0 — Orientation`, with one level-2 module and its topics below it.
- Keep rows in the order of section 9. A summary or review belongs to the group it is listed under.
- Names starting with `Practice` skip the builder's heading check, so use that prefix only for the rows above.
- `description` is one line: the pattern and the key insight. No `|` in a name or description.
- A TOPIC never has children. The builder fills in `completed`.

## 5. Module notes

**Phase note:** what the topic is, with a small picture; prerequisites from earlier phases; the signals in a problem that point to it; the Java API needed, with costs.

**Pattern group note:** the pattern in plain words, a generic commented Java template, and how to adapt it. Teach this before the first problem that uses it.

## 6. Problem day note

Use exactly this structure:

````markdown
# Day 001 — 217. Contains Duplicate

**Day 1 – Topic: Arrays & Hashing | Difficulty: Easy | Pattern: Hash set lookup**

[Open on LeetCode](https://leetcode.com/problems/contains-duplicate/)

## 1. Problem Statement
The complete problem, restated in your own words: task, input, output, every rule and follow-up.
At least two examples with input, output and the reason, then all constraints.
End with: **Stop here and try it for 20–40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### In simple words
### Pattern signals
Which words and constraints point to the pattern, and what complexity the constraints allow.
### Thought process
Brute force, why it is too slow, the repeated work, then the insight that removes it.
### Optimal approach
Numbered steps in plain English. No code or pseudocode.
### Dry run
A step-by-step table or `text` diagram on a real example, showing how the state changes
(pointers moving, window expanding and contracting, stack, queue, DP table).
### Why it works
The intuition and the invariant, plus an analogy only where it really helps.

## 3. Java Solution with Explanation
### Code
One complete, commented Java solution with the LeetCode signature.
### Code walkthrough
Explain it section by section: what each part does and why.
### Complexity
Time and space, each with its reason. Count recursion stack space.
### Common mistakes
At least three specific mistakes, each with its fix.
### Edge cases
A table: case, input, expected output.

## 4. Bonus
### Variations
One or two related problems and exactly what changes in the approach.
### In a real interview
When to use this pattern, when not to, and a two-sentence way to explain it aloud.

## 5. Recall
Q: Which signal in this problem points to the pattern?
A: ...

Q: What is the invariant?
A: ...
````

`_sections.txt` contains exactly:

```text
## 1. Problem Statement
## 2. Understanding & Approach (No Code)
## 3. Java Solution with Explanation
## 4. Bonus
## 5. Recall
```

Write 3–5 Q/A pairs per day (signal, insight, invariant, complexity, trap). They become flashcards, so each question must make sense on its own and name the problem.

Deepen the explanations as the plan progresses:

- **Days 1–28:** define every term and trace every step. Explain each Java API on first use.
- **Days 29–70:** shorter basics, more on choosing between patterns and on why the approach is correct.
- **Days 71–98:** derive the solution as in an interview: state or invariant first, proof sketch, alternatives and trade-offs. For DP always give state, transition, base case, order and space optimization.

## 7. Summary and review notes

**Pattern summary** (after each topic): a table of pattern, signal, template idea, complexity and problems solved; a "which pattern do I pick?" decision guide; the top mistakes; what carries into the next topic; 3–5 Q/A pairs.

**Review day:** use the problems listed in section 9.

- For each, give a short restatement, one example, the constraints and a time limit (Easy 15, Medium 25, Hard 40 minutes).
- Give no pattern name, hint, code or solution, and no Q/A pairs.
- Add a self-score table (solved alone, time, bug, redo date) and the rule for failures: redo the next day and again on the next review day.
- Put links to the original notes at the bottom, under "Check only after solving", like `[[Day 001 — 217. Contains Duplicate]]`.
- Day 98 is a timed mock interview: add a script (clarify, brute force, optimize, code, test) and a scoring rubric.

## 8. Rendering rules for my app

- Each note starts with a `#` heading. No line inside a note starts with `@@`.
- Tag every code fence (`java`, `text`, `mermaid`) and close it.
- A single newline renders as a line break, so do not hard-wrap paragraphs.
- Put arrays and generics in backticks: `[[7]]` in plain text becomes a note link, and `List<Integer>` loses its type as HTML.
- No `|` inside table cells, even in backticks. Write "or" instead.
- `$...$` is KaTeX, so use it only for math.
- Use Mermaid only for trees and graphs, with quoted labels. Use `text` diagrams for pointers, windows and tables.
- `Q:` and `A:` start a line only for flashcards. Leave a blank line after each answer, and none inside it.

## 9. Fixed outline

Follow it exactly. Do not add, drop, swap or reorder problems. Difficulty never decreases inside a topic.

Line format: name · difficulty · priority · LeetCode slug. Bold lines are the level-2 group names. In review lines, `217 (D1)` means problem 217 from Day 1.

### Phase 1 — Arrays & Hashing

**Hash set and map**

- Day 001 — 217. Contains Duplicate · Easy · MUST · `contains-duplicate`
- Day 002 — 242. Valid Anagram · Easy · MUST · `valid-anagram`
- Day 003 — 1. Two Sum · Easy · MUST · `two-sum`
- Day 004 — 49. Group Anagrams · Medium · MUST · `group-anagrams`

**Prefix and in-place indexing**

- Day 005 — 238. Product of Array Except Self · Medium · MUST · `product-of-array-except-self`
- Day 006 — 560. Subarray Sum Equals K · Medium · MUST · `subarray-sum-equals-k`
- Practice — Day 007: Review · re-solve 238 (D5), 560 (D6)
- Day 008 — 41. First Missing Positive · Hard · SHOULD · `first-missing-positive`
- Practice — Pattern summary: Arrays & Hashing

### Phase 2 — Two Pointers

**Opposite and same-direction pointers**

- Day 009 — 125. Valid Palindrome · Easy · MUST · `valid-palindrome`
- Day 010 — 283. Move Zeroes · Easy · NICE · `move-zeroes`
- Day 011 — 167. Two Sum II - Input Array Is Sorted · Medium · MUST · `two-sum-ii-input-array-is-sorted`

**Narrowing the search space**

- Day 012 — 15. 3Sum · Medium · MUST · `3sum`
- Day 013 — 11. Container With Most Water · Medium · MUST · `container-with-most-water`
- Practice — Day 014: Review · re-solve 41 (D8), 11 (D13)
- Day 015 — 42. Trapping Rain Water · Hard · MUST · `trapping-rain-water`
- Practice — Pattern summary: Two Pointers

### Phase 3 — Sliding Window

**Fixed window and running best**

- Day 016 — 643. Maximum Average Subarray I · Easy · NICE · `maximum-average-subarray-i`
- Day 017 — 121. Best Time to Buy and Sell Stock · Easy · MUST · `best-time-to-buy-and-sell-stock`

**Variable window**

- Day 018 — 3. Longest Substring Without Repeating Characters · Medium · MUST · `longest-substring-without-repeating-characters`
- Day 019 — 424. Longest Repeating Character Replacement · Medium · MUST · `longest-repeating-character-replacement`
- Day 020 — 209. Minimum Size Subarray Sum · Medium · MUST · `minimum-size-subarray-sum`
- Practice — Day 021: Review · re-solve 560 (D6), 42 (D15), 209 (D20)
- Day 022 — 76. Minimum Window Substring · Hard · MUST · `minimum-window-substring`
- Practice — Pattern summary: Sliding Window

### Phase 4 — Stack

**Stack basics and design**

- Day 023 — 20. Valid Parentheses · Easy · MUST · `valid-parentheses`
- Day 024 — 232. Implement Queue using Stacks · Easy · NICE · `implement-queue-using-stacks`
- Day 025 — 155. Min Stack · Medium · MUST · `min-stack`
- Day 026 — 150. Evaluate Reverse Polish Notation · Medium · MUST · `evaluate-reverse-polish-notation`

**Monotonic stack**

- Day 027 — 739. Daily Temperatures · Medium · MUST · `daily-temperatures`
- Practice — Day 028: Review · re-solve 41 (D8), 76 (D22), 739 (D27)
- Day 029 — 84. Largest Rectangle in Histogram · Hard · MUST · `largest-rectangle-in-histogram`
- Practice — Pattern summary: Stack

### Phase 5 — Binary Search

**Search on an index**

- Day 030 — 704. Binary Search · Easy · MUST · `binary-search`
- Day 031 — 35. Search Insert Position · Easy · NICE · `search-insert-position`

**Search on answers, rotations and partitions**

- Day 032 — 875. Koko Eating Bananas · Medium · MUST · `koko-eating-bananas`
- Day 033 — 153. Find Minimum in Rotated Sorted Array · Medium · MUST · `find-minimum-in-rotated-sorted-array`
- Day 034 — 33. Search in Rotated Sorted Array · Medium · MUST · `search-in-rotated-sorted-array`
- Practice — Day 035: Review · re-solve 49 (D4), 643 (D16), 84 (D29), 33 (D34)
- Day 036 — 4. Median of Two Sorted Arrays · Hard · MUST · `median-of-two-sorted-arrays`
- Practice — Pattern summary: Binary Search

### Phase 6 — Linked List

**Pointer rewiring and fast-slow**

- Day 037 — 206. Reverse Linked List · Easy · MUST · `reverse-linked-list`
- Day 038 — 21. Merge Two Sorted Lists · Easy · MUST · `merge-two-sorted-lists`
- Day 039 — 141. Linked List Cycle · Easy · MUST · `linked-list-cycle`
- Day 040 — 19. Remove Nth Node From End of List · Medium · MUST · `remove-nth-node-from-end-of-list`

**Combining list techniques**

- Day 041 — 143. Reorder List · Medium · MUST · `reorder-list`
- Practice — Day 042: Review · re-solve 15 (D12), 232 (D24), 4 (D36), 143 (D41)
- Day 043 — 138. Copy List with Random Pointer · Medium · SHOULD · `copy-list-with-random-pointer`
- Day 044 — 25. Reverse Nodes in k-Group · Hard · SHOULD · `reverse-nodes-in-k-group`
- Practice — Pattern summary: Linked List

### Phase 7 — Trees (DFS & BFS)

**Basic DFS and BFS**

- Day 045 — 104. Maximum Depth of Binary Tree · Easy · MUST · `maximum-depth-of-binary-tree`
- Day 046 — 226. Invert Binary Tree · Easy · MUST · `invert-binary-tree`
- Day 047 — 543. Diameter of Binary Tree · Easy · MUST · `diameter-of-binary-tree`
- Day 048 — 102. Binary Tree Level Order Traversal · Medium · MUST · `binary-tree-level-order-traversal`
- Practice — Day 049: Review · re-solve 209 (D20), 875 (D32), 25 (D44), 102 (D48)

**BST, ancestors and construction**

- Day 050 — 98. Validate Binary Search Tree · Medium · MUST · `validate-binary-search-tree`
- Day 051 — 236. Lowest Common Ancestor of a Binary Tree · Medium · MUST · `lowest-common-ancestor-of-a-binary-tree`
- Day 052 — 105. Construct Binary Tree from Preorder and Inorder Traversal · Medium · MUST · `construct-binary-tree-from-preorder-and-inorder-traversal`

**Advanced tree DFS**

- Day 053 — 297. Serialize and Deserialize Binary Tree · Hard · MUST · `serialize-and-deserialize-binary-tree`
- Day 054 — 124. Binary Tree Maximum Path Sum · Hard · MUST · `binary-tree-maximum-path-sum`
- Practice — Pattern summary: Trees (DFS & BFS)

### Phase 8 — Tries

**Prefix trees**

- Day 055 — 14. Longest Common Prefix · Easy · NICE · `longest-common-prefix`
- Practice — Day 056: Review · re-solve 76 (D22), 19 (D40), 297 (D53), 124 (D54)
- Day 057 — 208. Implement Trie (Prefix Tree) · Medium · MUST · `implement-trie-prefix-tree`
- Day 058 — 212. Word Search II · Hard · MUST · `word-search-ii`
- Practice — Pattern summary: Tries

### Phase 9 — Heap / Priority Queue

**Top K**

- Day 059 — 703. Kth Largest Element in a Stream · Easy · MUST · `kth-largest-element-in-a-stream`
- Day 060 — 1046. Last Stone Weight · Easy · NICE · `last-stone-weight`
- Day 061 — 215. Kth Largest Element in an Array · Medium · MUST · `kth-largest-element-in-an-array`
- Day 062 — 347. Top K Frequent Elements · Medium · MUST · `top-k-frequent-elements`
- Practice — Day 063: Review · re-solve 704 (D30), 102 (D48), 212 (D58), 347 (D62)

**Two heaps**

- Day 064 — 295. Find Median from Data Stream · Hard · MUST · `find-median-from-data-stream`
- Practice — Pattern summary: Heap / Priority Queue

### Phase 10 — Backtracking

**Choose, explore, undo**

- Day 065 — 257. Binary Tree Paths · Easy · NICE · `binary-tree-paths`
- Day 066 — 78. Subsets · Medium · MUST · `subsets`
- Day 067 — 46. Permutations · Medium · MUST · `permutations`
- Day 068 — 39. Combination Sum · Medium · MUST · `combination-sum`

**Constraint search**

- Day 069 — 79. Word Search · Medium · MUST · `word-search`
- Practice — Day 070: Review · re-solve 21 (D38), 98 (D50), 295 (D64), 79 (D69)
- Day 071 — 51. N-Queens · Hard · SHOULD · `n-queens`
- Practice — Pattern summary: Backtracking

### Phase 11 — Graphs (BFS, DFS, Topological Sort)

**Flood fill and traversal**

- Day 072 — 733. Flood Fill · Easy · NICE · `flood-fill`
- Day 073 — 200. Number of Islands · Medium · MUST · `number-of-islands`
- Day 074 — 133. Clone Graph · Medium · MUST · `clone-graph`
- Day 075 — 994. Rotting Oranges · Medium · MUST · `rotting-oranges`

**Topological sort and Union-Find**

- Day 076 — 207. Course Schedule · Medium · MUST · `course-schedule`
- Practice — Day 077: Review · re-solve 226 (D46), 212 (D58), 51 (D71), 207 (D76)
- Day 078 — 210. Course Schedule II · Medium · MUST · `course-schedule-ii`
- Day 079 — 684. Redundant Connection · Medium · SHOULD · `redundant-connection`

**Implicit graphs**

- Day 080 — 127. Word Ladder · Hard · MUST · `word-ladder`
- Practice — Pattern summary: Graphs (BFS, DFS, Topological Sort)

### Phase 12 — Dynamic Programming (1D → 2D → Advanced)

**1D DP**

- Day 081 — 70. Climbing Stairs · Easy · MUST · `climbing-stairs`
- Day 082 — 198. House Robber · Medium · MUST · `house-robber`
- Day 083 — 322. Coin Change · Medium · MUST · `coin-change`
- Practice — Day 084: Review · re-solve 124 (D54), 78 (D66), 127 (D80), 322 (D83)
- Day 085 — 300. Longest Increasing Subsequence · Medium · MUST · `longest-increasing-subsequence`

**2D DP**

- Day 086 — 62. Unique Paths · Medium · MUST · `unique-paths`
- Day 087 — 1143. Longest Common Subsequence · Medium · MUST · `longest-common-subsequence`
- Day 088 — 416. Partition Equal Subset Sum · Medium · MUST · `partition-equal-subset-sum`
- Day 089 — 72. Edit Distance · Medium · MUST · `edit-distance`

**Advanced DP**

- Day 090 — 309. Best Time to Buy and Sell Stock with Cooldown · Medium · SHOULD · `best-time-to-buy-and-sell-stock-with-cooldown`
- Practice — Day 091: Review · re-solve 347 (D62), 133 (D74), 72 (D89), 309 (D90)
- Day 092 — 10. Regular Expression Matching · Hard · SHOULD · `regular-expression-matching`
- Day 093 — 312. Burst Balloons · Hard · SHOULD · `burst-balloons`
- Practice — Pattern summary: Dynamic Programming (1D → 2D → Advanced)

### Phase 13 — Mixed / Hard and Contest-style

**Intervals and design**

- Day 094 — 56. Merge Intervals · Medium · MUST · `merge-intervals`
- Day 095 — 146. LRU Cache · Medium · MUST · `lru-cache`

**Hard mixed practice**

- Day 096 — 135. Candy · Hard · SHOULD · `candy`
- Day 097 — 815. Bus Routes · Hard · SHOULD · `bus-routes`
- Practice — Pattern summary: Mixed / Hard and Contest-style
- Practice — Day 098: Review · re-solve 295 (D64), 198 (D82), 135 (D96), 815 (D97)

## 10. Checks

Put check scripts and compiled files in a temporary folder, not in `Plans`.

1. **Build:** `python build_plan_csv.py guide-dsa dsa-leetcode-plan.csv` exits without problems.
2. **Outline:** days, names, numbers, difficulties, order and priorities match section 9 for every phase written so far.
3. **Code:** extract the Java from the Markdown, compile it and run it on every example and edge case in that note. Supply `ListNode`, `TreeNode` and `Node` in the test harness. Every expected output in the notes must match a real run.
4. **Content:** every day has all subheadings from section 6, a dry run and 3–5 Q/A pairs. Section 2 has no Java. Review days contain no solutions. Every `[[link]]` matches a topic name exactly.
5. **Final:** the CSV has 84 problem days, 14 reviews and 13 pattern summaries, plus the orientation topics.

At the end, report the row counts, what was tested and anything you could not verify.
