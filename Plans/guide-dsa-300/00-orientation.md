@@ 1 | MODULE | Phase 0 — Orientation | How to use this plan | MUST
# Orientation
Read both topics before the first problem.

@@ 2 | MODULE | Getting started | Method, Big-O and outline | MUST
# Getting started
How to study, then the whole plan on one page.

@@ 3 | TOPIC | Practice — Start here: method and Big-O | Study method, complexity, Java collections | MUST
# Start here

This plan has **304 problems** in 13 topics. Go in order, at your own pace.

1. Read the phase note and its template.
2. Read only section 1 of a problem and try it.
3. Read section 2 and try again.
4. Compare with section 3, then close it and rewrite from memory.
5. Do each review without looking at solutions.

## What the constraints allow

| Input size | Target complexity |
|---|---|
| up to 12 | O(n!) |
| up to 20 | O(2^n) |
| up to 500 | O(n³) |
| up to 5,000 | O(n²) |
| up to 100,000 | O(n log n) |
| up to 10,000,000 | O(n) |
| larger | O(log n) or O(1) |

## Java collections

| Need | Use | Cost |
|---|---|---|
| Lookup | `HashMap`, `HashSet` | O(1) average |
| Stack or queue | `ArrayDeque` | O(1) |
| Smallest or largest | `PriorityQueue` | O(log n) add and poll |
| Sorted keys | `TreeMap` | O(log n) |
| Build text | `StringBuilder` | O(1) amortized append |

## About the solutions

All solutions were written for this plan. They assume `import java.util.*;` and LeetCode's `ListNode`, `TreeNode` and `Node` types, and Java 21. They compile but were never run, and dry runs and expected edge-case outputs were worked out by hand, so submit on LeetCode to confirm one.

Sample cases and constraints are from the public problem pages.

## How to use the analogies

1. Read the story before the code.
2. Cover the right column of its table and name what each part stands for.
3. Retell the story when you review. If you can tell the story, you can rebuild the code.

@@ 3 | TOPIC | Practice — Start here: full outline | Every problem in order | MUST
# Full outline

## 1. Arrays & Hashing

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 1 | [[001 — 217. Contains Duplicate]] | Easy | Hash set |
| 2 | [[002 — 242. Valid Anagram]] | Easy | Frequency count |
| 3 | [[003 — 58. Length of Last Word]] | Easy | Scan from the end |
| 4 | [[004 — 1. Two Sum]] | Easy | Complement map |
| 5 | [[005 — 27. Remove Element]] | Easy | In-place partition |
| 6 | [[006 — 205. Isomorphic Strings]] | Easy | Two-way mapping |
| 7 | [[007 — 605. Can Place Flowers]] | Easy | Greedy scan |
| 8 | [[008 — 169. Majority Element]] | Easy | Boyer-Moore voting |
| 9 | [[009 — 724. Find Pivot Index]] | Easy | Prefix sum |
| 10 | [[010 — 303. Range Sum Query - Immutable]] | Easy | Prefix sum |
| 11 | [[011 — 448. Find All Numbers Disappeared in an Array]] | Easy | Index as bucket |
| 12 | [[012 — 290. Word Pattern]] | Easy | Two-way mapping |
| 13 | [[013 — 705. Design HashSet]] | Easy | Direct addressing |
| 14 | [[014 — 706. Design HashMap]] | Easy | Separate chaining |
| 15 | [[015 — 28. Find the Index of the First Occurrence in a String]] | Easy | KMP prefix function |
| 16 | [[016 — 2215. Find the Difference of Two Arrays]] | Easy | Set difference |
| 17 | [[017 — 49. Group Anagrams]] | Medium | Canonical key |
| 18 | [[018 — 912. Sort an Array]] | Medium | Merge sort |
| 19 | [[019 — 238. Product of Array Except Self]] | Medium | Prefix and suffix product |
| 20 | [[020 — 36. Valid Sudoku]] | Medium | Seen sets |
| 21 | [[021 — 128. Longest Consecutive Sequence]] | Medium | Sequence starts |
| 22 | [[022 — 535. Encode and Decode TinyURL]] | Medium | ID map |
| 23 | [[023 — 554. Brick Wall]] | Medium | Prefix frequency |
| 24 | [[024 — 122. Best Time to Buy and Sell Stock II]] | Medium | Greedy gains |
| 25 | [[025 — 560. Subarray Sum Equals K]] | Medium | Prefix sum map |
| 26 | [[026 — 1930. Unique Length-3 Palindromic Subsequences]] | Medium | Middle character |
| 27 | [[027 — 2017. Grid Game]] | Medium | Prefix and minimax |
| 28 | [[028 — 179. Largest Number]] | Medium | Custom order |
| 29 | [[029 — 523. Continuous Subarray Sum]] | Medium | Prefix remainder |
| 30 | [[030 — 187. Repeated DNA Sequences]] | Medium | Substring set |
| 31 | [[031 — 380. Insert Delete GetRandom O(1)]] | Medium | Map plus list |
| 32 | [[032 — 304. Range Sum Query 2D - Immutable]] | Medium | 2D prefix sum |
| 33 | [[033 — 41. First Missing Positive]] | Hard | Index as bucket |
| 34 | [[034 — 68. Text Justification]] | Hard | Greedy line packing |

## 2. Two Pointers

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 35 | [[035 — 392. Is Subsequence]] | Easy | Same-direction pointers |
| 36 | [[036 — 125. Valid Palindrome]] | Easy | Opposite pointers |
| 37 | [[037 — 680. Valid Palindrome II]] | Easy | Pointers with one skip |
| 38 | [[038 — 1768. Merge Strings Alternately]] | Easy | Parallel pointers |
| 39 | [[039 — 344. Reverse String]] | Easy | Opposite pointers |
| 40 | [[040 — 88. Merge Sorted Array]] | Easy | Fill from the back |
| 41 | [[041 — 283. Move Zeroes]] | Easy | Slow and fast |
| 42 | [[042 — 26. Remove Duplicates from Sorted Array]] | Easy | Slow and fast |
| 43 | [[043 — 75. Sort Colors]] | Medium | Dutch national flag |
| 44 | [[044 — 80. Remove Duplicates from Sorted Array II]] | Medium | Slow and fast |
| 45 | [[045 — 167. Two Sum II - Input Array Is Sorted]] | Medium | Sorted pair |
| 46 | [[046 — 15. 3Sum]] | Medium | Sort and fix one |
| 47 | [[047 — 18. 4Sum]] | Medium | Sort and fix two |
| 48 | [[048 — 11. Container With Most Water]] | Medium | Move the limiting side |
| 49 | [[049 — 189. Rotate Array]] | Medium | Triple reversal |
| 50 | [[050 — 881. Boats to Save People]] | Medium | Sort and pair ends |
| 51 | [[051 — 42. Trapping Rain Water]] | Hard | Left and right maxima |

## 3. Sliding Window

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 52 | [[052 — 121. Best Time to Buy and Sell Stock]] | Easy | Running minimum |
| 53 | [[053 — 219. Contains Duplicate II]] | Easy | Fixed window set |
| 54 | [[054 — 643. Maximum Average Subarray I]] | Easy | Fixed window sum |
| 55 | [[055 — 438. Find All Anagrams in a String]] | Medium | Fixed window counts |
| 56 | [[056 — 3. Longest Substring Without Repeating Characters]] | Medium | Window without repeats |
| 57 | [[057 — 424. Longest Repeating Character Replacement]] | Medium | Window plus budget |
| 58 | [[058 — 567. Permutation in String]] | Medium | Fixed window counts |
| 59 | [[059 — 1838. Frequency of the Most Frequent Element]] | Medium | Sorted window cost |
| 60 | [[060 — 904. Fruit Into Baskets]] | Medium | At most two kinds |
| 61 | [[061 — 1456. Maximum Number of Vowels in a Substring of Given Length]] | Medium | Fixed window count |
| 62 | [[062 — 209. Minimum Size Subarray Sum]] | Medium | Shrink while valid |
| 63 | [[063 — 658. Find K Closest Elements]] | Medium | Search for window start |
| 64 | [[064 — 1658. Minimum Operations to Reduce X to Zero]] | Medium | Complement window |
| 65 | [[065 — 76. Minimum Window Substring]] | Hard | Need and have |
| 66 | [[066 — 239. Sliding Window Maximum]] | Hard | Monotonic deque |

## 4. Stack

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 67 | [[067 — 496. Next Greater Element I]] | Easy | Monotonic stack |
| 68 | [[068 — 20. Valid Parentheses]] | Easy | Matching stack |
| 69 | [[069 — 682. Baseball Game]] | Easy | Record stack |
| 70 | [[070 — 225. Implement Stack using Queues]] | Easy | Queue rotation |
| 71 | [[071 — 232. Implement Queue using Stacks]] | Easy | Two stacks |
| 72 | [[072 — 155. Min Stack]] | Medium | Auxiliary minimum |
| 73 | [[073 — 150. Evaluate Reverse Polish Notation]] | Medium | Operand stack |
| 74 | [[074 — 2390. Removing Stars From a String]] | Medium | Undo stack |
| 75 | [[075 — 946. Validate Stack Sequences]] | Medium | Simulation stack |
| 76 | [[076 — 22. Generate Parentheses]] | Medium | Open and close choices |
| 77 | [[077 — 735. Asteroid Collision]] | Medium | Collision stack |
| 78 | [[078 — 739. Daily Temperatures]] | Medium | Monotonic stack |
| 79 | [[079 — 901. Online Stock Span]] | Medium | Stack of spans |
| 80 | [[080 — 853. Car Fleet]] | Medium | Arrival-time stack |
| 81 | [[081 — 71. Simplify Path]] | Medium | Path stack |
| 82 | [[082 — 394. Decode String]] | Medium | Nested decoding |
| 83 | [[083 — 402. Remove K Digits]] | Medium | Increasing stack |
| 84 | [[084 — 1209. Remove All Adjacent Duplicates in String II]] | Medium | Run-length stack |
| 85 | [[085 — 456. 132 Pattern]] | Medium | Decreasing stack |
| 86 | [[086 — 895. Maximum Frequency Stack]] | Hard | Stacks by frequency |
| 87 | [[087 — 84. Largest Rectangle in Histogram]] | Hard | Increasing stack |

## 5. Binary Search

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 88 | [[088 — 704. Binary Search]] | Easy | Classic search |
| 89 | [[089 — 35. Search Insert Position]] | Easy | Lower bound |
| 90 | [[090 — 441. Arranging Coins]] | Easy | Last feasible |
| 91 | [[091 — 977. Squares of a Sorted Array]] | Easy | Opposite pointers |
| 92 | [[092 — 367. Valid Perfect Square]] | Easy | Search on answer |
| 93 | [[093 — 69. Sqrt(x)]] | Easy | Last feasible |
| 94 | [[094 — 540. Single Element in a Sorted Array]] | Medium | Parity search |
| 95 | [[095 — 1011. Capacity To Ship Packages Within D Days]] | Medium | First feasible |
| 96 | [[096 — 162. Find Peak Element]] | Medium | Follow the slope |
| 97 | [[097 — 74. Search a 2D Matrix]] | Medium | 2D search |
| 98 | [[098 — 875. Koko Eating Bananas]] | Medium | First feasible |
| 99 | [[099 — 2616. Minimize the Maximum Difference of Pairs]] | Medium | Feasibility greedy |
| 100 | [[100 — 153. Find Minimum in Rotated Sorted Array]] | Medium | Rotation search |
| 101 | [[101 — 33. Search in Rotated Sorted Array]] | Medium | One sorted half |
| 102 | [[102 — 81. Search in Rotated Sorted Array II]] | Medium | One sorted half |
| 103 | [[103 — 981. Time Based Key-Value Store]] | Medium | Upper bound by key |
| 104 | [[104 — 34. Find First and Last Position of Element in Sorted Array]] | Medium | Two bounds |
| 105 | [[105 — 410. Split Array Largest Sum]] | Hard | First feasible |
| 106 | [[106 — 4. Median of Two Sorted Arrays]] | Hard | Partition search |

## 6. Linked List

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 107 | [[107 — 206. Reverse Linked List]] | Easy | Pointer reversal |
| 108 | [[108 — 21. Merge Two Sorted Lists]] | Easy | Dummy and tail |
| 109 | [[109 — 234. Palindrome Linked List]] | Easy | Middle and reverse |
| 110 | [[110 — 203. Remove Linked List Elements]] | Easy | Unlinking |
| 111 | [[111 — 83. Remove Duplicates from Sorted List]] | Easy | Unlinking |
| 112 | [[112 — 876. Middle of the Linked List]] | Easy | Fast and slow |
| 113 | [[113 — 160. Intersection of Two Linked Lists]] | Easy | Path switching |
| 114 | [[114 — 141. Linked List Cycle]] | Easy | Floyd cycle |
| 115 | [[115 — 143. Reorder List]] | Medium | Middle, reverse, merge |
| 116 | [[116 — 2130. Maximum Twin Sum of a Linked List]] | Medium | Middle and reverse |
| 117 | [[117 — 19. Remove Nth Node From End of List]] | Medium | Fixed gap |
| 118 | [[118 — 138. Copy List with Random Pointer]] | Medium | Old-to-new map |
| 119 | [[119 — 707. Design Linked List]] | Medium | Node manipulation |
| 120 | [[120 — 1472. Design Browser History]] | Medium | History with cursor |
| 121 | [[121 — 2. Add Two Numbers]] | Medium | Digit addition |
| 122 | [[122 — 287. Find the Duplicate Number]] | Medium | Floyd on values |
| 123 | [[123 — 24. Swap Nodes in Pairs]] | Medium | Pair rewiring |
| 124 | [[124 — 148. Sort List]] | Medium | List merge sort |
| 125 | [[125 — 86. Partition List]] | Medium | Two lists |
| 126 | [[126 — 61. Rotate List]] | Medium | Ring and cut |
| 127 | [[127 — 92. Reverse Linked List II]] | Medium | Sublist reversal |
| 128 | [[128 — 622. Design Circular Queue]] | Medium | Bounded queue |
| 129 | [[129 — 23. Merge k Sorted Lists]] | Hard | Merge many |
| 130 | [[130 — 25. Reverse Nodes in k-Group]] | Hard | Group reversal |

## 7. Trees (DFS & BFS)

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 131 | [[131 — 94. Binary Tree Inorder Traversal]] | Easy | Inorder DFS |
| 132 | [[132 — 144. Binary Tree Preorder Traversal]] | Easy | Preorder DFS |
| 133 | [[133 — 145. Binary Tree Postorder Traversal]] | Easy | Postorder DFS |
| 134 | [[134 — 226. Invert Binary Tree]] | Easy | Structural DFS |
| 135 | [[135 — 104. Maximum Depth of Binary Tree]] | Easy | Bottom-up DFS |
| 136 | [[136 — 543. Diameter of Binary Tree]] | Easy | Height plus global best |
| 137 | [[137 — 110. Balanced Binary Tree]] | Easy | Height check |
| 138 | [[138 — 100. Same Tree]] | Easy | Parallel DFS |
| 139 | [[139 — 572. Subtree of Another Tree]] | Easy | Match at every node |
| 140 | [[140 — 108. Convert Sorted Array to Binary Search Tree]] | Easy | Middle as root |
| 141 | [[141 — 617. Merge Two Binary Trees]] | Easy | Parallel DFS |
| 142 | [[142 — 112. Path Sum]] | Easy | Root-to-leaf state |
| 143 | [[143 — 783. Minimum Distance Between BST Nodes]] | Easy | Inorder previous |
| 144 | [[144 — 101. Symmetric Tree]] | Easy | Mirror DFS |
| 145 | [[145 — 116. Populating Next Right Pointers in Each Node]] | Medium | Use built next links |
| 146 | [[146 — 235. Lowest Common Ancestor of a Binary Search Tree]] | Medium | BST split |
| 147 | [[147 — 701. Insert into a Binary Search Tree]] | Medium | BST descent |
| 148 | [[148 — 450. Delete Node in a BST]] | Medium | BST deletion |
| 149 | [[149 — 102. Binary Tree Level Order Traversal]] | Medium | Level BFS |
| 150 | [[150 — 199. Binary Tree Right Side View]] | Medium | Last in level |
| 151 | [[151 — 103. Binary Tree Zigzag Level Order Traversal]] | Medium | Level BFS with direction |
| 152 | [[152 — 106. Construct Binary Tree from Inorder and Postorder Traversal]] | Medium | Traversal split |
| 153 | [[153 — 1376. Time Needed to Inform All Employees]] | Medium | Weighted tree walk |
| 154 | [[154 — 1448. Count Good Nodes in Binary Tree]] | Medium | Maximum so far |
| 155 | [[155 — 98. Validate Binary Search Tree]] | Medium | Allowed range |
| 156 | [[156 — 230. Kth Smallest Element in a BST]] | Medium | Inorder count |
| 157 | [[157 — 105. Construct Binary Tree from Preorder and Inorder Traversal]] | Medium | Traversal split |
| 158 | [[158 — 96. Unique Binary Search Trees]] | Medium | Catalan DP |
| 159 | [[159 — 129. Sum Root to Leaf Numbers]] | Medium | Path number |
| 160 | [[160 — 337. House Robber III]] | Medium | Two-state tree DP |
| 161 | [[161 — 669. Trim a Binary Search Tree]] | Medium | BST pruning |
| 162 | [[162 — 173. Binary Search Tree Iterator]] | Medium | Inorder iterator |
| 163 | [[163 — 538. Convert BST to Greater Tree]] | Medium | Reverse inorder |
| 164 | [[164 — 236. Lowest Common Ancestor of a Binary Tree]] | Medium | Report matches upward |
| 165 | [[165 — 124. Binary Tree Maximum Path Sum]] | Hard | Gain plus global best |
| 166 | [[166 — 297. Serialize and Deserialize Binary Tree]] | Hard | DFS with null markers |

## 8. Tries

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 167 | [[167 — 14. Longest Common Prefix]] | Easy | Prefix scan |
| 168 | [[168 — 1268. Search Suggestions System]] | Medium | Sorted prefix range |
| 169 | [[169 — 208. Implement Trie (Prefix Tree)]] | Medium | Prefix tree |
| 170 | [[170 — 211. Design Add and Search Words Data Structure]] | Medium | Trie with wildcard |
| 171 | [[171 — 212. Word Search II]] | Hard | Trie-pruned DFS |

## 9. Heap / Priority Queue

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 172 | [[172 — 703. Kth Largest Element in a Stream]] | Easy | Min-heap of k |
| 173 | [[173 — 1046. Last Stone Weight]] | Easy | Max-heap simulation |
| 174 | [[174 — 347. Top K Frequent Elements]] | Medium | Frequency top k |
| 175 | [[175 — 973. K Closest Points to Origin]] | Medium | Distance top k |
| 176 | [[176 — 215. Kth Largest Element in an Array]] | Medium | Min-heap of k |
| 177 | [[177 — 621. Task Scheduler]] | Medium | Heap and cooldown |
| 178 | [[178 — 355. Design Twitter]] | Medium | Per-user lists |
| 179 | [[179 — 1834. Single-Threaded CPU]] | Medium | Two orders |
| 180 | [[180 — 767. Reorganize String]] | Medium | Alternate most frequent |
| 181 | [[181 — 1405. Longest Happy String]] | Medium | Greedy heap |
| 182 | [[182 — 1094. Car Pooling]] | Medium | Difference array |
| 183 | [[183 — 295. Find Median from Data Stream]] | Hard | Two heaps |
| 184 | [[184 — 502. IPO]] | Hard | Unlock then choose |

## 10. Backtracking

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 185 | [[185 — 257. Binary Tree Paths]] | Easy | Path and undo |
| 186 | [[186 — 78. Subsets]] | Medium | Include or exclude |
| 187 | [[187 — 39. Combination Sum]] | Medium | Choice with reuse |
| 188 | [[188 — 77. Combinations]] | Medium | Start index |
| 189 | [[189 — 46. Permutations]] | Medium | Use each once |
| 190 | [[190 — 90. Subsets II]] | Medium | Skip duplicates |
| 191 | [[191 — 40. Combination Sum II]] | Medium | One use, unique |
| 192 | [[192 — 47. Permutations II]] | Medium | Unique permutations |
| 193 | [[193 — 79. Word Search]] | Medium | Grid backtracking |
| 194 | [[194 — 131. Palindrome Partitioning]] | Medium | Cut positions |
| 195 | [[195 — 17. Letter Combinations of a Phone Number]] | Medium | Cartesian product |
| 196 | [[196 — 473. Matchsticks to Square]] | Medium | Equal buckets |
| 197 | [[197 — 698. Partition to K Equal Sum Subsets]] | Medium | Equal buckets |
| 198 | [[198 — 51. N-Queens]] | Hard | Column and diagonals |
| 199 | [[199 — 52. N-Queens II]] | Hard | Constraint sets |

## 11. Graphs (BFS, DFS, Topological Sort)

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 200 | [[200 — 463. Island Perimeter]] | Easy | Edge counting DFS |
| 201 | [[201 — 953. Verifying an Alien Dictionary]] | Easy | Custom comparison |
| 202 | [[202 — 733. Flood Fill]] | Easy | Flood fill |
| 203 | [[203 — 200. Number of Islands]] | Medium | Component count |
| 204 | [[204 — 133. Clone Graph]] | Medium | Clone map |
| 205 | [[205 — 695. Max Area of Island]] | Medium | Component size |
| 206 | [[206 — 1905. Count Sub Islands]] | Medium | Component condition |
| 207 | [[207 — 417. Pacific Atlantic Water Flow]] | Medium | Reverse reachability |
| 208 | [[208 — 130. Surrounded Regions]] | Medium | Border flood fill |
| 209 | [[209 — 1466. Reorder Routes to Make All Paths Lead to the City Zero]] | Medium | Directed tree DFS |
| 210 | [[210 — 994. Rotting Oranges]] | Medium | Multi-source BFS |
| 211 | [[211 — 909. Snakes and Ladders]] | Medium | State BFS |
| 212 | [[212 — 752. Open the Lock]] | Medium | State BFS |
| 213 | [[213 — 802. Find Eventual Safe States]] | Medium | Cycle-state DFS |
| 214 | [[214 — 207. Course Schedule]] | Medium | Cycle detection |
| 215 | [[215 — 210. Course Schedule II]] | Medium | Topological order |
| 216 | [[216 — 934. Shortest Bridge]] | Medium | DFS then BFS |
| 217 | [[217 — 1091. Shortest Path in Binary Matrix]] | Medium | Eight-way BFS |
| 218 | [[218 — 684. Redundant Connection]] | Medium | Union-Find |
| 219 | [[219 — 721. Accounts Merge]] | Medium | Union-Find |
| 220 | [[220 — 785. Is Graph Bipartite]] | Medium | Two-coloring |
| 221 | [[221 — 399. Evaluate Division]] | Medium | Weighted graph |
| 222 | [[222 — 1584. Min Cost to Connect All Points]] | Medium | Prim MST |
| 223 | [[223 — 743. Network Delay Time]] | Medium | Bellman-Ford |
| 224 | [[224 — 787. Cheapest Flights Within K Stops]] | Medium | Bounded Bellman-Ford |
| 225 | [[225 — 1553. Minimum Number of Days to Eat N Oranges]] | Hard | Shrink with memo |
| 226 | [[226 — 127. Word Ladder]] | Hard | Word BFS |
| 227 | [[227 — 332. Reconstruct Itinerary]] | Hard | Eulerian path |
| 228 | [[228 — 778. Swim in Rising Water]] | Hard | Minimum bottleneck |

## 12. Dynamic Programming (1D → 2D → Advanced)

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 229 | [[229 — 70. Climbing Stairs]] | Easy | Fibonacci DP |
| 230 | [[230 — 746. Min Cost Climbing Stairs]] | Easy | Min-cost DP |
| 231 | [[231 — 198. House Robber]] | Medium | Take or skip |
| 232 | [[232 — 213. House Robber II]] | Medium | Circular take or skip |
| 233 | [[233 — 5. Longest Palindromic Substring]] | Medium | Expand centers |
| 234 | [[234 — 647. Palindromic Substrings]] | Medium | Expand centers |
| 235 | [[235 — 91. Decode Ways]] | Medium | Two-choice count |
| 236 | [[236 — 322. Coin Change]] | Medium | Unbounded knapsack |
| 237 | [[237 — 152. Maximum Product Subarray]] | Medium | Max and min state |
| 238 | [[238 — 139. Word Break]] | Medium | Segmentation DP |
| 239 | [[239 — 300. Longest Increasing Subsequence]] | Medium | Patience sorting |
| 240 | [[240 — 416. Partition Equal Subset Sum]] | Medium | 0/1 knapsack |
| 241 | [[241 — 120. Triangle]] | Medium | Bottom-up path |
| 242 | [[242 — 740. Delete and Earn]] | Medium | Value take or skip |
| 243 | [[243 — 279. Perfect Squares]] | Medium | Unbounded knapsack |
| 244 | [[244 — 1856. Maximum Subarray Min-Product]] | Medium | Stack and prefix |
| 245 | [[245 — 343. Integer Break]] | Medium | Product of threes |
| 246 | [[246 — 673. Number of Longest Increasing Subsequence]] | Medium | Length and count |
| 247 | [[247 — 118. Pascal's Triangle]] | Easy | Build from previous row |
| 248 | [[248 — 62. Unique Paths]] | Medium | Grid count |
| 249 | [[249 — 63. Unique Paths II]] | Medium | Rolling grid count |
| 250 | [[250 — 1143. Longest Common Subsequence]] | Medium | Two-string DP |
| 251 | [[251 — 518. Coin Change II]] | Medium | Combination knapsack |
| 252 | [[252 — 494. Target Sum]] | Medium | Index and sum |
| 253 | [[253 — 97. Interleaving String]] | Medium | Two-prefix DP |
| 254 | [[254 — 64. Minimum Path Sum]] | Medium | Memoized path |
| 255 | [[255 — 221. Maximal Square]] | Medium | Square DP |
| 256 | [[256 — 72. Edit Distance]] | Medium | Two-string DP |
| 257 | [[257 — 1140. Stone Game II]] | Medium | Minimax DP |
| 258 | [[258 — 309. Best Time to Buy and Sell Stock with Cooldown]] | Medium | State machine |
| 259 | [[259 — 329. Longest Increasing Path in a Matrix]] | Hard | DAG memo |
| 260 | [[260 — 115. Distinct Subsequences]] | Hard | Matching count |
| 261 | [[261 — 1220. Count Vowels Permutation]] | Hard | State machine |
| 262 | [[262 — 312. Burst Balloons]] | Hard | Interval DP |
| 263 | [[263 — 10. Regular Expression Matching]] | Hard | Two-index matching |

## 13. Mixed / Hard and Contest-style

| No. | Problem | Difficulty | Pattern |
|---|---|---|---|
| 264 | [[264 — 168. Excel Sheet Column Title]] | Easy | Base 26 without zero |
| 265 | [[265 — 1071. Greatest Common Divisor of Strings]] | Easy | GCD |
| 266 | [[266 — 202. Happy Number]] | Easy | Cycle detection |
| 267 | [[267 — 66. Plus One]] | Easy | Carry |
| 268 | [[268 — 9. Palindrome Number]] | Easy | Digit reversal |
| 269 | [[269 — 263. Ugly Number]] | Easy | Factor removal |
| 270 | [[270 — 136. Single Number]] | Easy | XOR cancellation |
| 271 | [[271 — 191. Number of 1 Bits]] | Easy | Bit count |
| 272 | [[272 — 338. Counting Bits]] | Easy | Bit DP |
| 273 | [[273 — 190. Reverse Bits]] | Easy | Bit reversal |
| 274 | [[274 — 268. Missing Number]] | Easy | Expected sum |
| 275 | [[275 — 67. Add Binary]] | Easy | Carry |
| 276 | [[276 — 146. LRU Cache]] | Medium | Map plus doubly list |
| 277 | [[277 — 53. Maximum Subarray]] | Medium | Kadane |
| 278 | [[278 — 918. Maximum Sum Circular Subarray]] | Medium | Max and min Kadane |
| 279 | [[279 — 55. Jump Game]] | Medium | Greedy reach |
| 280 | [[280 — 45. Jump Game II]] | Medium | Implicit BFS |
| 281 | [[281 — 134. Gas Station]] | Medium | Reset start |
| 282 | [[282 — 846. Hand of Straights]] | Medium | Sorted counts |
| 283 | [[283 — 1899. Merge Triplets to Form Target Triplet]] | Medium | Safe elements |
| 284 | [[284 — 763. Partition Labels]] | Medium | Last occurrence |
| 285 | [[285 — 678. Valid Parenthesis String]] | Medium | Two index stacks |
| 286 | [[286 — 1029. Two City Scheduling]] | Medium | Sort by regret |
| 287 | [[287 — 57. Insert Interval]] | Medium | Three zones |
| 288 | [[288 — 56. Merge Intervals]] | Medium | Sort and merge |
| 289 | [[289 — 435. Non-overlapping Intervals]] | Medium | Interval greedy |
| 290 | [[290 — 48. Rotate Image]] | Medium | In-place rotation |
| 291 | [[291 — 54. Spiral Matrix]] | Medium | Shrinking borders |
| 292 | [[292 — 59. Spiral Matrix II]] | Medium | Shrinking borders |
| 293 | [[293 — 73. Set Matrix Zeroes]] | Medium | Markers in matrix |
| 294 | [[294 — 12. Integer to Roman]] | Medium | Greedy symbols |
| 295 | [[295 — 50. Pow(x, n)]] | Medium | Binary exponentiation |
| 296 | [[296 — 43. Multiply Strings]] | Medium | Schoolbook multiplication |
| 297 | [[297 — 6. Zigzag Conversion]] | Medium | Cycle indices |
| 298 | [[298 — 371. Sum of Two Integers]] | Medium | XOR and carry |
| 299 | [[299 — 7. Reverse Integer]] | Medium | Digit reversal |
| 300 | [[300 — 460. LFU Cache]] | Hard | Frequency lists |
| 301 | [[301 — 135. Candy]] | Hard | Two passes |
| 302 | [[302 — 1851. Minimum Interval to Include Each Query]] | Hard | Offline sweep and heap |
| 303 | [[303 — 149. Max Points on a Line]] | Hard | Slope map |
| 304 | [[304 — 815. Bus Routes]] | Hard | BFS on routes |
