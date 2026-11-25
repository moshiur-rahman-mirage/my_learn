@@ 1 | MODULE | Phase 11 — Graphs (BFS, DFS, Topological Sort) | grid; connected; fewest moves; dependencies; network | MUST
# Phase 11: Graphs (BFS, DFS, Topological Sort)

Name the nodes and edges first. Use DFS for connectivity, BFS for fewest moves, topological sort for dependencies, Union-Find for merging and Dijkstra or Bellman-Ford for weights.

**Signals:** grid; connected; fewest moves; dependencies; network.

## The big analogy: a city of rumours and roads

Places are nodes, roads are edges. A **DFS** is one explorer with a ball of string. A **BFS** is a rumour: everyone one street away hears first, then two, so the first time it reaches you is the fewest streets. **Topological sort** is getting dressed, socks before shoes. **Union-Find** is clubs merging under one president. **Dijkstra** is a rumour on roads of different lengths.

| Question | Picture | Tool | Meet it in |
|---|---|---|---|
| What is connected? | spilled paint | DFS | 200, 695, 130 |
| Fewest moves? | a rumour | BFS | 127, 752, 994 |
| What order? | getting dressed | topological sort | 207, 210 |
| Same group? | merging clubs | Union-Find | 684, 721 |
| Cheapest? | weighted roads | Prim, Dijkstra, Bellman-Ford | 1584, 778, 787 |

## Template
```java
Queue<Integer> queue = new ArrayDeque<>(List.of(start));
Set<Integer> seen = new HashSet<>(List.of(start));
while (!queue.isEmpty()) {
    for (int next : graph.get(queue.poll())) {
        if (seen.add(next)) queue.add(next); // mark when queued
    }
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Graphs — Easy | 3 problems | MUST
# Graphs: Easy

Patterns in this group: Edge counting DFS, Custom comparison, Flood fill.

@@ 3 | TOPIC | 200 — 463. Island Perimeter | Easy · Edge counting DFS | NICE
# 200 — 463. Island Perimeter

**Problem 200 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Easy | Pattern: Edge counting DFS**

[Open on LeetCode](https://leetcode.com/problems/island-perimeter/)

## 1. Problem Statement
A grid contains exactly one island: 1s connected horizontally or vertically, with no lakes inside. Each cell is a square of side 1. Return the island's perimeter.

### Examples
```text
Input:  grid = [[0,1,0,0],[1,1,1,0],[0,1,0,0],[1,1,0,0]]
Output: 16

Input:  grid = [[1]]
Output: 4

Input:  grid = [[1,0]]
Output: 4
```

### Constraints
```text
row == grid.length
col == grid[i].length
1 <= row, col <= 100
grid[i][j] is 0 or 1.
There is exactly one island in grid.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Counting fence panels on a walk
You walk over the whole island, and at each plot look in four directions. Every look that would step into water or off the map needs one fence panel. Looking at land you already walked needs none.

| In the story | In the solution |
|---|---|
| step off | base case 1 |
| walked plot | value 2 |

### Pattern signal
**Edge counting DFS.** The perimeter is just the number of land sides that face something other than land.

### Thought process
1. **First idea:** Trace the coastline in order, which is fiddly at corners.
2. **The insight:** The perimeter is just the number of land sides that face something other than land.

### Optimal approach
1. Find the first land cell.
2. Mark it walked.
3. Return one for each step off the land.
4. Return zero for walked land.
5. Add the four directions.

### Dry run
```text
two adjacent cells
cell   exits
left   3
right  3
total  6
```

### Why it works
Each land cell is expanded once and examines each side once, and exactly the outer sides return one.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int islandPerimeter(int[][] grid) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 1) return walk(grid, r, c);
            }
        }
        return 0;
    }

    private int walk(int[][] g, int r, int c) {
        if (r < 0 || c < 0 || r == g.length || c == g[0].length || g[r][c] == 0) return 1;
        if (g[r][c] == 2) return 0;
        g[r][c] = 2;
        return walk(g, r + 1, c) + walk(g, r - 1, c) + walk(g, r, c + 1) + walk(g, r, c - 1);
    }
}
```

### Code walkthrough
- `return 1`: A step off the land is one panel.
- `g[r][c] == 2`: A walked neighbor is land, so no panel.
- `g[r][c] = 2`: Mark it walked rather than turning it to water.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n) stack

### Common mistakes
1. **Treating a visited cell as water adds false edges.** Fix: return 0 for it.
2. **Counting land cells gives the area.** Fix: count sides.
3. **Diagonal cells share no side.** Fix: only check four directions.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [1] ]` | `4` |
| Pair | `[ [1,1] ]` | `6` |
| Corner | `[ [1,0] ]` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[205 — 695. Max Area of Island]]: Count cells. The key idea there: A component search can return the component's size directly.
- [[203 — 200. Number of Islands]]: Count starts. The key idea there: Each connected component produces exactly one unclaimed cell that starts a search.

### In a real interview
Say it in this order:

1. **Brute force:** Trace the coastline in order, which is fiddly at corners.
2. **Insight:** The perimeter is just the number of land sides that face something other than land.
3. **Approach in a few words:** Count non-land neighbors.
4. **Cost:** time O(m·n); space O(m·n) stack.

## 5. Recall
Q: 463. Island Perimeter: which pattern, and what is the insight?
A: Edge counting DFS. The perimeter is just the number of land sides that face something other than land.

Q: 463. Island Perimeter: what is the analogy, and what does it stand for?
A: Counting fence panels on a walk: step off → base case 1; walked plot → value 2.

Q: 463. Island Perimeter: What adds one to perimeter?
A: A land side facing water or outside.

Q: 463. Island Perimeter: what are the time and space costs?
A: Time O(m·n). Space O(m·n) stack.

@@ 3 | TOPIC | 201 — 953. Verifying an Alien Dictionary | Easy · Custom comparison | NICE
# 201 — 953. Verifying an Alien Dictionary

**Problem 201 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Easy | Pattern: Custom comparison**

[Open on LeetCode](https://leetcode.com/problems/verifying-an-alien-dictionary/)

## 1. Problem Statement
You get words and `order`, a permutation of the lowercase alphabet. Return true if the words are sorted lexicographically under that alphabet, where a word comes before any longer word it is a prefix of.

### Examples
```text
Input:  words = ["hello","leetcode"], order = "hlabcdefgijkmnopqrstuvwxyz"
Output: true

Input:  words = ["word","world","row"], order = "worldabcefghijkmnpqstuvxyz"
Output: false

Input:  words = ["apple","app"], order = "abcdefghijklmnopqrstuvwxyz"
Output: false
```

### Constraints
```text
1 <= words.length <= 100
1 <= words[i].length <= 20
order.length == 26
All characters in words[i] and order are English lowercase letters.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A librarian with another alphabet
A librarian receives a new alphabet on a rank card. To check a shelf only neighbors need comparing, at their first differing letter. If there is none the shorter word must stand first.

| In the story | In the solution |
|---|---|
| rank card | array |
| first difference | decider |

### Pattern signal
**Custom comparison.** A list is sorted exactly when every neighboring pair is in order.

### Thought process
1. **First idea:** Sort a copy with a custom comparator and compare the lists.
2. **The insight:** A list is sorted exactly when every neighboring pair is in order.

### Optimal approach
1. Build a rank for each letter.
2. Compare each adjacent pair.
3. Decide at the first difference.
4. Otherwise require the shorter first.

### Dry run
```text
words = hello, leetcode.  order starts h, l, a, b ...
position  letters  ranks     decision
0         h and l  0 and 1   first word is smaller, the pair is ordered
no more pairs, so return true
```

### Why it works
Lexicographic order is transitive, so ordered neighbors make the whole list ordered, and a single bad pair disproves it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isAlienSorted(String[] words, String order) {
        int[] rank = new int[26];
        for (int i = 0; i < 26; i++) rank[order.charAt(i) - 'a'] = i;
        for (int i = 1; i < words.length; i++) {
            if (!ordered(words[i - 1], words[i], rank)) return false;
        }
        return true;
    }

    private boolean ordered(String a, String b, int[] rank) {
        for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
            if (a.charAt(i) != b.charAt(i)) return rank[a.charAt(i) - 'a'] < rank[b.charAt(i) - 'a'];
        }
        return a.length() <= b.length();
    }
}
```

### Code walkthrough
- `rank[order.charAt(i) - 'a'] = i`: Write the rank card.
- `a.charAt(i) != b.charAt(i)`: The first difference alone decides.
- `a.length() <= b.length()`: A prefix must come before the longer word.

### Complexity
- **Time:** O(total characters)
- **Space:** O(1)

### Common mistakes
1. **Comparing past the first difference is wrong.** Fix: decide there and stop.
2. **Normal alphabetical comparison ignores the order.** Fix: compare ranks.
3. **Forgetting the prefix rule accepts "apple" before "app".** Fix: compare lengths last.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `["a"]` | `true` |
| Prefix | `["ab","a"], normal` | `false` |
| Equal | `["a","a"]` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **269. Alien Dictionary**: Infer with topological sort.
- [[028 — 179. Largest Number]]: Custom comparison. The key idea there: The best order of two neighbors does not depend on the others, so it can be a comparator.

### In a real interview
Say it in this order:

1. **Brute force:** Sort a copy with a custom comparator and compare the lists.
2. **Insight:** A list is sorted exactly when every neighboring pair is in order.
3. **Approach in a few words:** Check adjacent pairs by rank.
4. **Cost:** time O(total characters); space O(1).

## 5. Recall
Q: 953. Verifying an Alien Dictionary: which pattern, and what is the insight?
A: Custom comparison. A list is sorted exactly when every neighboring pair is in order.

Q: 953. Verifying an Alien Dictionary: what is the analogy, and what does it stand for?
A: A librarian with another alphabet: rank card → array; first difference → decider.

Q: 953. Verifying an Alien Dictionary: What decides two alien words?
A: The first difference, or shorter prefix.

Q: 953. Verifying an Alien Dictionary: what are the time and space costs?
A: Time O(total characters). Space O(1).

@@ 3 | TOPIC | 202 — 733. Flood Fill | Easy · Flood fill | NICE
# 202 — 733. Flood Fill

**Problem 202 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Easy | Pattern: Flood fill**

[Open on LeetCode](https://leetcode.com/problems/flood-fill/)

## 1. Problem Statement
You get an image, a start pixel and a new color. Recolor the start pixel and every pixel connected to it horizontally or vertically through its original color, then return the image.

### Examples
```text
Input:  image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
Output: [[2,2,2],[2,2,0],[2,0,1]]

Input:  image = [[0,0,0],[0,0,0]], sr = 0, sc = 0, color = 0
Output: [[0,0,0],[0,0,0]]
```

### Constraints
```text
m == image.length
n == image[i].length
1 <= m, n <= 50
0 <= image[i][j], color < 2^16
0 <= sr < m
0 <= sc < n
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A paint bucket
A paint bucket tool pours at one pixel. The paint creeps sideways and up and down, but only through pixels of the original color, and stops at every other color.

| In the story | In the solution |
|---|---|
| original | old |
| spreading | DFS |

### Pattern signal
**Flood fill.** The area to paint is the connected component of the start pixel.

### Thought process
1. **First idea:** Rescan the whole image until nothing more changes.
2. **The insight:** The area to paint is the connected component of the start pixel.

### Optimal approach
1. Save the original color.
2. Stop if the new color is the same.
3. Paint a matching pixel.
4. Recurse in four directions.

### Dry run
```text
1 1 0, paint 2 at first
visit  row
0      2 1 0
1      2 2 0
```

### Why it works
Recursion reaches exactly the pixels connected through the old color, and painting removes each from further matching, so it is visited once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int old = image[sr][sc];
        if (old != color) paint(image, sr, sc, old, color);
        return image;
    }

    private void paint(int[][] a, int r, int c, int old, int color) {
        if (r < 0 || c < 0 || r == a.length || c == a[0].length || a[r][c] != old) return;
        a[r][c] = color;
        paint(a, r + 1, c, old, color);
        paint(a, r - 1, c, old, color);
        paint(a, r, c + 1, old, color);
        paint(a, r, c - 1, old, color);
    }
}
```

### Code walkthrough
- `old != color`: With equal colors painted pixels would still match.
- `a[r][c] != old`: Other colors and the edge are walls.
- `a[r][c] = color`: Painting also marks it visited.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n) stack

### Common mistakes
1. **Painting with the same color recurses forever.** Fix: return early.
2. **Recoloring every matching pixel ignores connection.** Fix: spread from the start.
3. **Moving diagonally leaks through corners.** Fix: use four directions.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Same | `[ [1] ], color 1` | `[ [1] ]` |
| One | `[ [0] ], color 2` | `[ [2] ]` |
| Separate | `[ [1,0,1] ] first to 2` | `[ [2,0,1] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[203 — 200. Number of Islands]]: Every start. The key idea there: Each connected component produces exactly one unclaimed cell that starts a search.
- [[208 — 130. Surrounded Regions]]: From edges. The key idea there: It is simpler to find the regions that are safe than those captured.

### In a real interview
Say it in this order:

1. **Brute force:** Rescan the whole image until nothing more changes.
2. **Insight:** The area to paint is the connected component of the start pixel.
3. **Approach in a few words:** DFS over the old color.
4. **Cost:** time O(m·n); space O(m·n) stack.

## 5. Recall
Q: 733. Flood Fill: which pattern, and what is the insight?
A: Flood fill. The area to paint is the connected component of the start pixel.

Q: 733. Flood Fill: what is the analogy, and what does it stand for?
A: A paint bucket: original → old; spreading → DFS.

Q: 733. Flood Fill: Why check equal colors in flood fill?
A: Painted cells would still match.

Q: 733. Flood Fill: what are the time and space costs?
A: Time O(m·n). Space O(m·n) stack.

@@ 2 | MODULE | Graphs — Medium | 22 problems | MUST
# Graphs: Medium

Patterns in this group: Component count, Clone map, Component size, Component condition, Reverse reachability, Border flood fill, Directed tree DFS, Multi-source BFS, State BFS, Cycle-state DFS, Cycle detection, Topological order, DFS then BFS, Eight-way BFS, Union-Find, Two-coloring, Weighted graph, Prim MST, Bellman-Ford, Bounded Bellman-Ford.

@@ 3 | TOPIC | 203 — 200. Number of Islands | Medium · Component count | MUST
# 203 — 200. Number of Islands

**Problem 203 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Component count**

[Open on LeetCode](https://leetcode.com/problems/number-of-islands/)

## 1. Problem Statement
You get a grid of '1' (land) and '0' (water). An island is land connected horizontally or vertically and surrounded by water. Return the number of islands.

### Examples
```text
Input:  grid = [
          ["1","1","1","1","0"],
          ["1","1","0","1","0"],
          ["1","1","0","0","0"],
          ["0","0","0","0","0"]
        ]
Output: 1

Input:  grid = [
          ["1","1","0","0","0"],
          ["1","1","0","0","0"],
          ["0","0","1","0","0"],
          ["0","0","0","1","1"]
        ]
Output: 3
```

### Constraints
```text
m == grid.length
n == grid[i].length
1 <= m, n <= 300
grid[i][j] is '0' or '1'.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Claiming islands from a plane
From a plane you spot unclaimed land, plant one flag and send a crew that claims every plot of that island. The next unclaimed land must be a different island.

| In the story | In the solution |
|---|---|
| flag | counter |
| crew | DFS |

### Pattern signal
**Component count.** Each connected component produces exactly one unclaimed cell that starts a search.

### Thought process
1. **First idea:** Compare every land cell with every other for connection.
2. **The insight:** Each connected component produces exactly one unclaimed cell that starts a search.

### Optimal approach
1. Scan the grid.
2. At each '1' add one.
3. Sink its whole island using a stack.
4. Continue scanning.

### Dry run
```text
1 1 0 1
column  action
0       count 1, sink 0 and 1
3       count 2
```

### Why it works
A search claims all of one component and nothing else, so the number of searches equals the number of islands.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] != '1') continue;
                count++;
                sink(grid, r, c);
            }
        }
        return count;
    }

    private void sink(char[][] g, int row, int col) {
        Deque<int[]> work = new ArrayDeque<>();
        work.push(new int[] {row, col});
        while (!work.isEmpty()) {
            int[] cell = work.pop();
            int r = cell[0], c = cell[1];
            if (r < 0 || c < 0 || r == g.length || c == g[0].length || g[r][c] != '1') continue;
            g[r][c] = '0';
            work.push(new int[] {r + 1, c});
            work.push(new int[] {r - 1, c});
            work.push(new int[] {r, c + 1});
            work.push(new int[] {r, c - 1});
        }
    }
}
```

### Code walkthrough
- `count++`: One flag for a new island.
- `g[r][c] = '0'`: Sinking marks the plot claimed.
- `Deque<int[]>`: An explicit stack is safe for 90,000 cells.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Counting land cells gives the area.** Fix: count only unvisited starts.
2. **Comparing with the number 1 never matches.** Fix: compare with the character '1'.
3. **Connecting diagonals merges islands.** Fix: use four directions.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Water | `[ ["0"] ]` | `0` |
| One | `[ ["1"] ]` | `1` |
| Two | `[ ["1","0","1"] ]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[205 — 695. Max Area of Island]]: Size. The key idea there: A component search can return the component's size directly.
- [[218 — 684. Redundant Connection]]: Changing edges. The key idea there: Union-Find answers "already connected?" almost instantly.

### In a real interview
Say it in this order:

1. **Brute force:** Compare every land cell with every other for connection.
2. **Insight:** Each connected component produces exactly one unclaimed cell that starts a search.
3. **Approach in a few words:** Component starts with flood fill.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 200. Number of Islands: which pattern, and what is the insight?
A: Component count. Each connected component produces exactly one unclaimed cell that starts a search.

Q: 200. Number of Islands: what is the analogy, and what does it stand for?
A: Claiming islands from a plane: flag → counter; crew → DFS.

Q: 200. Number of Islands: When is the island count increased?
A: On each unvisited land start.

Q: 200. Number of Islands: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 204 — 133. Clone Graph | Medium · Clone map | MUST
# 204 — 133. Clone Graph

**Problem 204 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Clone map**

[Open on LeetCode](https://leetcode.com/problems/clone-graph/)

## 1. Problem Statement
You get a node of a connected undirected graph. Each node has a value and a list of neighbors. Return a deep copy of the whole graph, as the copy of that node.

### Examples
```text
Input:  adjList = [[2,4],[1,3],[2,4],[1,3]]
Output: [[2,4],[1,3],[2,4],[1,3]]

Input:  adjList = [[]]
Output: [[]]

Input:  adjList = []
Output: []
```

### Constraints
```text
The number of nodes in the graph is in the range [0, 100].
1 <= Node.val <= 100
Node.val is unique for each node.
There are no repeated edges and no self-loops in the graph.
The Graph is connected and all nodes can be visited starting from the given node.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Photographing a friend network
You copy a friendship network by creating a double of each person and noting it in a register. Register the double before visiting the friends, or two friends will send you back and forth forever.

| In the story | In the solution |
|---|---|
| register | map |
| double | copy |

### Pattern signal
**Clone map.** One old-to-new map serves both as the visited set and as the translation of every edge.

### Thought process
1. **First idea:** Copy recursively with no record, which never ends on a cycle.
2. **The insight:** One old-to-new map serves both as the visited set and as the translation of every edge.

### Optimal approach
1. Return null for null.
2. Return an existing double.
3. Create and register a double.
4. Add the clone of each neighbor.

### Dry run
```text
1 and 2 connected
call     action
1        register 1'
2        register 2'
1 again  return 1'
```

### Why it works
Each node gets exactly one copy before its edges are followed, so cycles meet the register and every edge points to a copy.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private final Map<Node, Node> twin = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) return null;
        if (twin.containsKey(node)) return twin.get(node);
        Node copy = new Node(node.val);
        twin.put(node, copy);
        for (Node next : node.neighbors) copy.neighbors.add(cloneGraph(next));
        return copy;
    }
}
```

### Code walkthrough
- `twin.containsKey(node)`: This person already has a double.
- `twin.put(node, copy)`: Register before visiting any friends.
- `copy.neighbors.add`: Edges connect copies only.

### Complexity
- **Time:** O(V + E)
- **Space:** O(V)

### Common mistakes
1. **Registering after visiting neighbors loops forever.** Fix: register first.
2. **Adding original neighbors is a shallow copy.** Fix: add their clones.
3. **Creating a copy per edge duplicates nodes.** Fix: reuse the map's copy.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `null` | `null` |
| One | `node 1` | `a new node 1` |
| Pair | `1-2` | `two new nodes` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[118 — 138. Copy List with Random Pointer]]: Two edges. The key idea there: Separate creating nodes from connecting them, with a map for the correspondence.
- [[221 — 399. Evaluate Division]]: Search. The key idea there: a/c is the product of edge ratios along any path from a to c, because the middles cancel.

### In a real interview
Say it in this order:

1. **Brute force:** Copy recursively with no record, which never ends on a cycle.
2. **Insight:** One old-to-new map serves both as the visited set and as the translation of every edge.
3. **Approach in a few words:** Memoized DFS by identity.
4. **Cost:** time O(V + E); space O(V).

## 5. Recall
Q: 133. Clone Graph: which pattern, and what is the insight?
A: Clone map. One old-to-new map serves both as the visited set and as the translation of every edge.

Q: 133. Clone Graph: what is the analogy, and what does it stand for?
A: Photographing a friend network: register → map; double → copy.

Q: 133. Clone Graph: When is a clone put in the map?
A: Before cloning neighbors.

Q: 133. Clone Graph: what are the time and space costs?
A: Time O(V + E). Space O(V).

@@ 3 | TOPIC | Practice — Review 34 | Re-solve without looking | MUST
# Review 34

Solve on a blank page. No notes, hints or solutions.

## 133. Clone Graph
Deep-copy a connected graph.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/clone-graph/)

## 953. Verifying an Alien Dictionary
Check words are sorted in an alien alphabet.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/verifying-an-alien-dictionary/)

## 90. Subsets II
List unique subsets of values with duplicates.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/subsets-ii/)

## 297. Serialize and Deserialize Binary Tree
Serialize and restore any binary tree.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 133 | | | | |
| 953 | | | | |
| 90 | | | | |
| 297 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[204 — 133. Clone Graph]]
- [[201 — 953. Verifying an Alien Dictionary]]
- [[190 — 90. Subsets II]]
- [[166 — 297. Serialize and Deserialize Binary Tree]]

@@ 3 | TOPIC | 205 — 695. Max Area of Island | Medium · Component size | MUST
# 205 — 695. Max Area of Island

**Problem 205 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Component size**

[Open on LeetCode](https://leetcode.com/problems/max-area-of-island/)

## 1. Problem Statement
You get a grid of 0s and 1s. An island is a group of 1s connected horizontally or vertically and its area is its cell count. Return the largest area, or 0 without islands.

### Examples
```text
Input:  grid = [[0,0,1,0,0,0,0,1,0,0,0,0,0],[0,0,0,0,0,0,0,1,1,1,0,0,0],[0,1,1,0,1,0,0,0,0,0,0,0,0],[0,1,0,0,1,1,0,0,1,0,1,0,0],[0,1,0,0,1,1,0,0,1,1,1,0,0],[0,0,0,0,0,0,0,0,0,0,1,0,0],[0,0,0,0,0,0,0,1,1,1,0,0,0],[0,0,0,0,0,0,0,1,1,0,0,0,0]]
Output: 6

Input:  grid = [[0,0,0,0,0,0,0,0]]
Output: 0
```

### Constraints
```text
m == grid.length
n == grid[i].length
1 <= m, n <= 50
grid[i][j] is either 0 or 1.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Census boats
Census boats visit each island. Every plot is counted once and then struck out, and each boat adds up what the boats it sent onward report.

| In the story | In the solution |
|---|---|
| counted | sunk |
| census | DFS return |

### Pattern signal
**Component size.** A component search can return the component's size directly.

### Thought process
1. **First idea:** Recount the island from each of its cells.
2. **The insight:** A component search can return the component's size directly.

### Optimal approach
1. From each cell run the search.
2. Return zero off land.
3. Sink the cell.
4. Return one plus four results.
5. Keep the maximum.

### Dry run
```text
1 1 0 1
island  area
first   2
second  1, best 2
```

### Why it works
Every cell adds exactly one to the single search that first reaches its island, so each result is that island's area.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxAreaOfIsland(int[][] grid) {
        int best = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) best = Math.max(best, area(grid, r, c));
        }
        return best;
    }

    private int area(int[][] g, int r, int c) {
        if (r < 0 || c < 0 || r == g.length || c == g[0].length || g[r][c] == 0) return 0;
        g[r][c] = 0;
        return 1 + area(g, r + 1, c) + area(g, r - 1, c) + area(g, r, c + 1) + area(g, r, c - 1);
    }
}
```

### Code walkthrough
- `g[r][c] = 0`: Struck out, so never counted again.
- `1 + area`: This plot plus what lies beyond it.
- `Math.max`: Keep the largest island.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Summing all land mixes islands.** Fix: keep a size per search.
2. **Marking late counts cells twice.** Fix: sink before recursing.
3. **Taking the largest neighbor result is wrong.** Fix: add all four.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Water | `[ [0] ]` | `0` |
| One | `[ [1] ]` | `1` |
| Pair | `[ [1,1] ]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[203 — 200. Number of Islands]]: Starts. The key idea there: Each connected component produces exactly one unclaimed cell that starts a search.
- [[200 — 463. Island Perimeter]]: Sides. The key idea there: The perimeter is just the number of land sides that face something other than land.

### In a real interview
Say it in this order:

1. **Brute force:** Recount the island from each of its cells.
2. **Insight:** A component search can return the component's size directly.
3. **Approach in a few words:** DFS returning size.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 695. Max Area of Island: which pattern, and what is the insight?
A: Component size. A component search can return the component's size directly.

Q: 695. Max Area of Island: what is the analogy, and what does it stand for?
A: Census boats: counted → sunk; census → DFS return.

Q: 695. Max Area of Island: What does area DFS return?
A: Unseen cells in that component.

Q: 695. Max Area of Island: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 206 — 1905. Count Sub Islands | Medium · Component condition | SHOULD
# 206 — 1905. Count Sub Islands

**Problem 206 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Component condition**

[Open on LeetCode](https://leetcode.com/problems/count-sub-islands/)

## 1. Problem Statement
You get two binary grids of equal size. An island in `grid2` is a sub-island if every one of its cells is land in `grid1`. Islands connect horizontally or vertically. Return the number of sub-islands.

### Examples
```text
Input:  grid1 = [[1,1,1,0,0],[0,1,1,1,1],[0,0,0,0,0],[1,0,0,0,0],[1,1,0,1,1]], grid2 = [[1,1,1,0,0],[0,0,1,1,1],[0,1,0,0,0],[1,0,1,1,0],[0,1,0,1,0]]
Output: 3

Input:  grid1 = [[1,0,1,0,1],[1,1,1,1,1],[0,0,0,0,0],[1,1,1,1,1],[1,0,1,0,1]], grid2 = [[0,0,0,0,0],[1,1,1,1,1],[0,1,0,1,0],[0,1,0,1,0],[1,0,0,0,1]]
Output: 2
```

### Constraints
```text
m == grid1.length == grid2.length
n == grid1[i].length == grid2[i].length
1 <= m, n <= 500
grid1[i][j] and grid2[i][j] are either 0 or 1.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A tracing over an old map
Lay tracing paper with islands over an old map. One traced plot over old sea spoils that island, but you still finish tracing it, or its remaining plots would look like a new island.

| In the story | In the solution |
|---|---|
| spoiled | false |
| finish | full DFS |

### Pattern signal
**Component condition.** Being a sub-island is a logical AND over the cells of one component.

### Thought process
1. **First idea:** Compare every island of one grid with those of the other.
2. **The insight:** Being a sub-island is a logical AND over the cells of one component.

### Optimal approach
1. Start at each land cell of grid2.
2. Visit the whole island.
3. Clear a flag on uncovered cells.
4. Count islands whose flag survived.

### Dry run
```text
B = 1 1 0 1, A = 1 0 0 1
island  covered
0,1     no
3       yes, count 1
```

### Why it works
The search visits exactly one island and the flag stays true exactly when all its cells are covered.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int countSubIslands(int[][] grid1, int[][] grid2) {
        int count = 0;
        for (int r = 0; r < grid2.length; r++) {
            for (int c = 0; c < grid2[0].length; c++) {
                if (grid2[r][c] == 1 && covered(grid1, grid2, r, c)) count++;
            }
        }
        return count;
    }

    private boolean covered(int[][] a, int[][] b, int row, int col) {
        boolean good = true;
        Deque<int[]> work = new ArrayDeque<>();
        work.push(new int[] {row, col});
        while (!work.isEmpty()) {
            int[] cell = work.pop();
            int r = cell[0], c = cell[1];
            if (r < 0 || c < 0 || r == b.length || c == b[0].length || b[r][c] == 0) continue;
            b[r][c] = 0;
            if (a[r][c] == 0) good = false; // keep going
            work.push(new int[] {r + 1, c});
            work.push(new int[] {r - 1, c});
            work.push(new int[] {r, c + 1});
            work.push(new int[] {r, c - 1});
        }
        return good;
    }
}
```

### Code walkthrough
- `good = false`: Spoiled, but keep tracing.
- `b[r][c] = 0`: Mark this plot traced.
- `count++`: Every cell was covered.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Returning at the first bad cell leaves pieces to be recounted.** Fix: finish the search.
2. **Accepting any overlap is too weak.** Fix: require every cell covered.
3. **Searching grid1's islands answers another question.** Fix: start from grid2.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Covered | `[ [1] ], [ [1] ]` | `1` |
| Not | `[ [0] ], [ [1] ]` | `0` |
| Empty | `[ [1] ], [ [0] ]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[203 — 200. Number of Islands]]: No condition. The key idea there: Each connected component produces exactly one unclaimed cell that starts a search.
- [[208 — 130. Surrounded Regions]]: Border condition. The key idea there: It is simpler to find the regions that are safe than those captured.

### In a real interview
Say it in this order:

1. **Brute force:** Compare every island of one grid with those of the other.
2. **Insight:** Being a sub-island is a logical AND over the cells of one component.
3. **Approach in a few words:** Full component DFS with a flag.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 1905. Count Sub Islands: which pattern, and what is the insight?
A: Component condition. Being a sub-island is a logical AND over the cells of one component.

Q: 1905. Count Sub Islands: what is the analogy, and what does it stand for?
A: A tracing over an old map: spoiled → false; finish → full DFS.

Q: 1905. Count Sub Islands: Why not stop at an uncovered cell?
A: The rest would look like another island.

Q: 1905. Count Sub Islands: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 207 — 417. Pacific Atlantic Water Flow | Medium · Reverse reachability | MUST
# 207 — 417. Pacific Atlantic Water Flow

**Problem 207 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Reverse reachability**

[Open on LeetCode](https://leetcode.com/problems/pacific-atlantic-water-flow/)

## 1. Problem Statement
Return cells from which water, flowing to equal or lower neighbors, reaches both Pacific (top, left) and Atlantic (bottom, right).

### Examples
```text
Input:  heights = [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]
Output: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]

Input:  heights = [[1]]
Output: [[0,0]]
```

### Constraints
```text
m == heights.length
n == heights[r].length
1 <= m, n <= 200
0 <= heights[r][c] <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Salmon swimming upstream
Release salmon from both oceans. They can climb steps that are level or higher. A cell both kinds reach is one from which water flows down to both oceans.

| In the story | In the solution |
|---|---|
| salmon | reverse DFS |
| both | intersection |

### Pattern signal
**Reverse reachability.** Reversing every flow edge turns many searches into two.

### Thought process
1. **First idea:** Search downhill from every cell, repeating work.
2. **The insight:** Reversing every flow edge turns many searches into two.

### Optimal approach
1. Start from both borders of each ocean.
2. Climb to equal or higher unseen cells.
3. Keep two tables.
4. Output their intersection.

### Dry run
```text
heights = [ [1,2,1] ], a single row of three cells
start from       cells marked
Pacific (top)    (0,0) (0,1) (0,2), all touch the top edge
Atlantic (bottom) (0,0) (0,1) (0,2), all touch the bottom edge
in both tables   all three cells are returned
```

### Why it works
A downhill path from a cell to an ocean, read backwards, is exactly a climb from that ocean to the cell.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        int m = heights.length, n = heights[0].length;
        boolean[][] pacific = new boolean[m][n], atlantic = new boolean[m][n];
        for (int r = 0; r < m; r++) {
            climb(heights, pacific, r, 0, 0);
            climb(heights, atlantic, r, n - 1, 0);
        }
        for (int c = 0; c < n; c++) {
            climb(heights, pacific, 0, c, 0);
            climb(heights, atlantic, m - 1, c, 0);
        }
        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (pacific[r][c] && atlantic[r][c]) result.add(List.of(r, c));
            }
        }
        return result;
    }

    private void climb(int[][] h, boolean[][] seen, int r, int c, int from) {
        if (r < 0 || c < 0 || r == h.length || c == h[0].length || seen[r][c] || h[r][c] < from) return;
        seen[r][c] = true;
        climb(h, seen, r + 1, c, h[r][c]);
        climb(h, seen, r - 1, c, h[r][c]);
        climb(h, seen, r, c + 1, h[r][c]);
        climb(h, seen, r, c - 1, h[r][c]);
    }
}
```

### Code walkthrough
- `h[r][c] < from`: Salmon never descend.
- `climb(heights, atlantic, r, n - 1, 0)`: Release them along the Atlantic's edge.
- `pacific[r][c] && atlantic[r][c]`: Reached from both oceans.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Searching downhill from the ocean is backwards.** Fix: climb.
2. **One visited table mixes the oceans.** Fix: keep two.
3. **A strict inequality loses plateaus.** Fix: allow equal heights.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [5] ]` | `[ [0,0] ]` |
| Row | `[ [1,2] ]` | `both` |
| Flat | `equal 2x2` | `all` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[208 — 130. Surrounded Regions]]: One border. The key idea there: It is simpler to find the regions that are safe than those captured.
- [[210 — 994. Rotting Oranges]]: Many sources. The key idea there: BFS started from all sources at once measures the distance to the nearest source.

### In a real interview
Say it in this order:

1. **Brute force:** Search downhill from every cell, repeating work.
2. **Insight:** Reversing every flow edge turns many searches into two.
3. **Approach in a few words:** Reverse multi-start reachability.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 417. Pacific Atlantic Water Flow: which pattern, and what is the insight?
A: Reverse reachability. Reversing every flow edge turns many searches into two.

Q: 417. Pacific Atlantic Water Flow: what is the analogy, and what does it stand for?
A: Salmon swimming upstream: salmon → reverse DFS; both → intersection.

Q: 417. Pacific Atlantic Water Flow: Which moves are allowed from oceans?
A: To equal or higher cells.

Q: 417. Pacific Atlantic Water Flow: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 208 — 130. Surrounded Regions | Medium · Border flood fill | MUST
# 208 — 130. Surrounded Regions

**Problem 208 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Border flood fill**

[Open on LeetCode](https://leetcode.com/problems/surrounded-regions/)

## 1. Problem Statement
You get a board of X and O. Capture every four-directionally connected region of O that has no cell on the border by changing it to X, in place.

### Examples
```text
Input:  board = [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]
Output: [["X","X","X","X"],["X","X","X","X"],["X","X","X","X"],["X","O","X","X"]]

Input:  board = [["X"]]
Output: [["X"]]
```

### Constraints
```text
m == board.length
n == board[i].length
1 <= m, n <= 200
board[i][j] is 'X' or 'O'.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A flood from the coast
The sea floods in from the board's edge through every O. Whatever it reaches can escape and is safe. The dry O regions left over are completely surrounded.

| In the story | In the solution |
|---|---|
| reached | S |
| captured | X |

### Pattern signal
**Border flood fill.** It is simpler to find the regions that are safe than those captured.

### Thought process
1. **First idea:** Search each region to see whether it touches the edge.
2. **The insight:** It is simpler to find the regions that are safe than those captured.

### Optimal approach
1. Start from all border cells.
2. Mark connected O as safe.
3. Flip remaining O to X.
4. Restore the safe marks.

### Dry run
```text
X O X / X O X / X X X
(0,1),(1,1)  safe
nothing is captured
```

### Why it works
A region cannot be captured exactly when some path connects it to a border O, which is what the flood marks.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void solve(char[][] board) {
        int m = board.length, n = board[0].length;
        Deque<int[]> sea = new ArrayDeque<>();
        for (int r = 0; r < m; r++) {
            sea.push(new int[] {r, 0});
            sea.push(new int[] {r, n - 1});
        }
        for (int c = 0; c < n; c++) {
            sea.push(new int[] {0, c});
            sea.push(new int[] {m - 1, c});
        }
        while (!sea.isEmpty()) {
            int[] cell = sea.pop();
            int r = cell[0], c = cell[1];
            if (r < 0 || c < 0 || r == m || c == n || board[r][c] != 'O') continue;
            board[r][c] = 'S';
            sea.push(new int[] {r + 1, c});
            sea.push(new int[] {r - 1, c});
            sea.push(new int[] {r, c + 1});
            sea.push(new int[] {r, c - 1});
        }
        for (char[] row : board) {
            for (int c = 0; c < n; c++) row[c] = row[c] == 'S' ? 'O' : 'X';
        }
    }
}
```

### Code walkthrough
- `sea.push(new int[] {r, 0})`: The coast is where the flood begins.
- `board[r][c] = 'S'`: A temporary mark for safe.
- `? 'O' : 'X'`: Safe returns to O, all else becomes X.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Starting inside cannot know whether a region escapes.** Fix: start on the border.
2. **Saving only border cells loses their regions.** Fix: flood from them.
3. **Leaving the temporary mark corrupts the board.** Fix: turn S back into O.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ ["O"] ]` | `O` |
| Centre | `3x3 X around O` | `all X` |
| All | `all O` | `unchanged` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[207 — 417. Pacific Atlantic Water Flow]]: Two. The key idea there: Reversing every flow edge turns many searches into two.
- [[202 — 733. Flood Fill]]: One start. The key idea there: The area to paint is the connected component of the start pixel.

### In a real interview
Say it in this order:

1. **Brute force:** Search each region to see whether it touches the edge.
2. **Insight:** It is simpler to find the regions that are safe than those captured.
3. **Approach in a few words:** Mark the complement from the border.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 130. Surrounded Regions: which pattern, and what is the insight?
A: Border flood fill. It is simpler to find the regions that are safe than those captured.

Q: 130. Surrounded Regions: what is the analogy, and what does it stand for?
A: A flood from the coast: reached → S; captured → X.

Q: 130. Surrounded Regions: Which regions are safe?
A: Those connected to a border O.

Q: 130. Surrounded Regions: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | 209 — 1466. Reorder Routes to Make All Paths Lead to the City Zero | Medium · Directed tree DFS | SHOULD
# 209 — 1466. Reorder Routes to Make All Paths Lead to the City Zero

**Problem 209 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Directed tree DFS**

[Open on LeetCode](https://leetcode.com/problems/reorder-routes-to-make-all-paths-lead-to-the-city-zero/)

## 1. Problem Statement
n cities form a tree of one-way roads. Return the fewest reversals so all reach city 0.

### Examples
```text
Input:  n = 6, connections = [[0,1],[1,3],[2,3],[4,0],[4,5]]
Output: 3

Input:  n = 5, connections = [[1,0],[1,2],[3,2],[3,4]]
Output: 2

Input:  n = 3, connections = [[1,0],[2,0]]
Output: 0
```

### Constraints
```text
2 <= n <= 5 * 10^4
connections.length == n - 1
connections[i].length == 2
0 <= ai, bi <= n - 1
ai != bi
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A walk out of the capital
Walk outward from the capital ignoring the arrows. Any arrow pointing the same way you walk leads away from the capital, so it has to be turned around.

| In the story | In the solution |
|---|---|
| with you | cost 1 |
| walk | DFS |

### Pattern signal
**Directed tree DFS.** In a tree each road lies on one path to city 0, so its required direction is fixed.

### Thought process
1. **First idea:** Try combinations of reversed roads.
2. **The insight:** In a tree each road lies on one path to city 0, so its required direction is fixed.

### Optimal approach
1. Store each road both ways with a cost of 1 along its arrow.
2. Traverse from 0.
3. Add each used direction's cost.

### Dry run
```text
0 to 1, 2 to 1
step   cost
0 to 1 1
1 to 2 0
total  1
```

### Why it works
Every edge is crossed once away from the root, and it must point toward the root, so its cost is exactly whether it must flip.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minReorder(int n, int[][] connections) {
        List<List<int[]>> roads = new ArrayList<>();
        for (int i = 0; i < n; i++) roads.add(new ArrayList<>());
        for (int[] e : connections) {
            roads.get(e[0]).add(new int[] {e[1], 1});
            roads.get(e[1]).add(new int[] {e[0], 0});
        }
        boolean[] seen = new boolean[n];
        seen[0] = true;
        int turns = 0;
        Deque<Integer> work = new ArrayDeque<>(List.of(0));
        while (!work.isEmpty()) {
            for (int[] road : roads.get(work.pop())) {
                if (seen[road[0]]) continue;
                seen[road[0]] = true;
                turns += road[1];
                work.push(road[0]);
            }
        }
        return turns;
    }
}
```

### Code walkthrough
- `{e[1], 1}`: Walking along the arrow needs a flip.
- `{e[0], 0}`: Against the arrow already points home.
- `turns += road[1]`: Count that road's flip.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Following arrows only cannot visit every city.** Fix: store both directions.
2. **Counting every stored outgoing edge is wrong.** Fix: count only traversed ones.
3. **Recursion can overflow on a chain.** Fix: use an explicit stack.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Away | `0 to 1` | `1` |
| Toward | `1 to 0` | `0` |
| Chain | `0 to 1 to 2` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[153 — 1376. Time Needed to Inform All Employees]]: Weights. The key idea there: Times add along a chain and combine by maximum across branches.
- [[214 — 207. Course Schedule]]: Cycles. The key idea there: All courses can be finished exactly when the prerequisite graph has no cycle.

### In a real interview
Say it in this order:

1. **Brute force:** Try combinations of reversed roads.
2. **Insight:** In a tree each road lies on one path to city 0, so its required direction is fixed.
3. **Approach in a few words:** Undirected traversal with direction costs.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 1466. Reorder Routes to Make All Paths Lead to the City Zero: which pattern, and what is the insight?
A: Directed tree DFS. In a tree each road lies on one path to city 0, so its required direction is fixed.

Q: 1466. Reorder Routes to Make All Paths Lead to the City Zero: what is the analogy, and what does it stand for?
A: A walk out of the capital: with you → cost 1; walk → DFS.

Q: 1466. Reorder Routes to Make All Paths Lead to the City Zero: Which traversed edges need reversal?
A: Those originally pointing away from zero.

Q: 1466. Reorder Routes to Make All Paths Lead to the City Zero: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 210 — 994. Rotting Oranges | Medium · Multi-source BFS | MUST
# 210 — 994. Rotting Oranges

**Problem 210 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Multi-source BFS**

[Open on LeetCode](https://leetcode.com/problems/rotting-oranges/)

## 1. Problem Statement
Each minute rot spreads to four-neighbor fresh oranges. Return minutes until none is fresh or -1.

### Examples
```text
Input:  grid = [[2,1,1],[1,1,0],[0,1,1]]
Output: 4

Input:  grid = [[2,1,1],[0,1,1],[1,0,1]]
Output: -1

Input:  grid = [[0,2]]
Output: 0
```

### Constraints
```text
m == grid.length
n == grid[i].length
1 <= m, n <= 10
grid[i][j] is 0, 1, or 2.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A rumour from several people
Several people start a rumour in the same minute. Every minute all who just heard tell their neighbors. The answer is the minute the last person hears, unless somebody can't be reached.

| In the story | In the solution |
|---|---|
| tellers | initial queue |
| round | level |

### Pattern signal
**Multi-source BFS.** BFS started from all sources at once measures the distance to the nearest source.

### Thought process
1. **First idea:** Simulate by rescanning the grid every minute.
2. **The insight:** BFS started from all sources at once measures the distance to the nearest source.

### Optimal approach
1. Queue every rotten orange.
2. Count the fresh.
3. Expand one layer per minute.
4. Return the minutes if none are fresh.

### Dry run
```text
2,1,1
minute  fresh
0       2
1       1
2       0
```

### Why it works
BFS layer t holds exactly the oranges whose nearest rotten one is t steps away, the minute each rots.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int orangesRotting(int[][] grid) {
        Queue<int[]> queue = new ArrayDeque<>();
        int fresh = 0, minutes = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 2) queue.add(new int[] {r, c});
                else if (grid[r][c] == 1) fresh++;
            }
        }
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (fresh > 0 && !queue.isEmpty()) {
            minutes++;
            for (int size = queue.size(); size > 0; size--) {
                int[] cell = queue.poll();
                for (int[] s : steps) {
                    int r = cell[0] + s[0], c = cell[1] + s[1];
                    if (r < 0 || c < 0 || r == grid.length || c == grid[0].length || grid[r][c] != 1) continue;
                    grid[r][c] = 2;
                    fresh--;
                    queue.add(new int[] {r, c});
                }
            }
        }
        return fresh == 0 ? minutes : -1;
    }
}
```

### Code walkthrough
- `grid[r][c] == 2`: All tellers start in the same minute.
- `fresh > 0 &&`: Stop as soon as everybody has heard.
- `minutes++`: One layer is one minute.

### Complexity
- **Time:** O(m·n)
- **Space:** O(m·n)

### Common mistakes
1. **Running one source at a time gives wrong times.** Fix: queue all at minute 0.
2. **An extra last round adds a minute.** Fix: loop only while fresh ones remain.
3. **Forgetting unreachable oranges is wrong.** Fix: return -1 if any remain.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `[ [0] ]` | `0` |
| Alone | `[ [1] ]` | `-1` |
| Pair | `[ [2,1] ]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **542. Distance**: Store layers.
- [[216 — 934. Shortest Bridge]]: An island source. The key idea there: One BFS whose sources are a whole island finds the nearest point of the other.

### In a real interview
Say it in this order:

1. **Brute force:** Simulate by rescanning the grid every minute.
2. **Insight:** BFS started from all sources at once measures the distance to the nearest source.
3. **Approach in a few words:** Multi-source level BFS.
4. **Cost:** time O(m·n); space O(m·n).

## 5. Recall
Q: 994. Rotting Oranges: which pattern, and what is the insight?
A: Multi-source BFS. BFS started from all sources at once measures the distance to the nearest source.

Q: 994. Rotting Oranges: what is the analogy, and what does it stand for?
A: A rumour from several people: tellers → initial queue; round → level.

Q: 994. Rotting Oranges: What starts the rotting queue?
A: Every rotten orange.

Q: 994. Rotting Oranges: what are the time and space costs?
A: Time O(m·n). Space O(m·n).

@@ 3 | TOPIC | Practice — Review 35 | Re-solve without looking | MUST
# Review 35

Solve on a blank page. No notes, hints or solutions.

## 994. Rotting Oranges
Find minutes until all oranges rot.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/rotting-oranges/)

## 417. Pacific Atlantic Water Flow
Find cells draining to both oceans.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/pacific-atlantic-water-flow/)

## 473. Matchsticks to Square
Decide whether sticks form a square.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/matchsticks-to-square/)

## 703. Kth Largest Element in a Stream
Return the kth largest after each stream insert.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/kth-largest-element-in-a-stream/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 994 | | | | |
| 417 | | | | |
| 473 | | | | |
| 703 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[210 — 994. Rotting Oranges]]
- [[207 — 417. Pacific Atlantic Water Flow]]
- [[196 — 473. Matchsticks to Square]]
- [[172 — 703. Kth Largest Element in a Stream]]

@@ 3 | TOPIC | 211 — 909. Snakes and Ladders | Medium · State BFS | SHOULD
# 211 — 909. Snakes and Ladders

**Problem 211 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: State BFS**

[Open on LeetCode](https://leetcode.com/problems/snakes-and-ladders/)

## 1. Problem Statement
A zigzag board numbered from bottom-left has jumps. Each roll moves 1..6, then takes at most one jump. Return fewest rolls to the last square or -1.

### Examples
```text
Input:  board = [[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,35,-1,-1,13,-1],[-1,-1,-1,-1,-1,-1],[-1,15,-1,-1,-1,-1]]
Output: 4

Input:  board = [[-1,-1],[-1,3]]
Output: 1
```

### Constraints
```text
n == board.length == board[i].length
2 <= n <= 20
board[i][j] is either -1 or in the range [1, n^2].
The squares labeled 1 and n^2 are not the starting points of any snake or ladder.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Rumour on a board game
On the game board a rumour spreads from each square to the six it can roll to, or to wherever their snake or ladder drops you. The first round it hears the last square is the fewest rolls.

| In the story | In the solution |
|---|---|
| six | edges |
| round | rolls |

### Pattern signal
**State BFS.** A square is a state and every roll costs one, so BFS gives the minimum.

### Thought process
1. **First idea:** Try every sequence of rolls.
2. **The insight:** A square is a state and every roll costs one, so BFS gives the minimum.

### Optimal approach
1. Set square 1 to zero rolls.
2. Poll a square.
3. Convert each roll to a cell.
4. Redirect once.
5. Record the landing if new.
6. Stop at the last.

### Dry run
```text
empty 3 x 3 board, squares 1 to 9
rolls  squares first reached
0      1
1      2, 3, 4, 5, 6, 7
2      8, 9
square 9 is the last, so return 2
```

### Why it works
All edges cost one roll, so the first time BFS labels a square it uses the fewest rolls.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int snakesAndLadders(int[][] board) {
        int n = board.length, last = n * n;
        int[] rolls = new int[last + 1];
        Arrays.fill(rolls, -1);
        rolls[1] = 0;
        Queue<Integer> queue = new ArrayDeque<>(List.of(1));
        while (!queue.isEmpty()) {
            int at = queue.poll();
            if (at == last) return rolls[at];
            for (int next = at + 1; next <= Math.min(at + 6, last); next++) {
                int row = (next - 1) / n, col = (next - 1) % n;
                if (row % 2 == 1) col = n - 1 - col;
                int jump = board[n - 1 - row][col];
                int landed = jump == -1 ? next : jump;
                if (rolls[landed] != -1) continue;
                rolls[landed] = rolls[at] + 1;
                queue.add(landed);
            }
        }
        return -1;
    }
}
```

### Code walkthrough
- `row % 2 == 1`: Alternate rows are numbered backwards.
- `n - 1 - row`: Labels start from the bottom row.
- `jump == -1 ? next : jump`: At most one jump per roll.

### Complexity
- **Time:** O(n²)
- **Space:** O(n²)

### Common mistakes
1. **Following a second jump breaks the rules.** Fix: redirect once.
2. **Marking the square before the jump is wrong.** Fix: mark where you land.
3. **Always taking ladders is not optimal.** Fix: use BFS.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Small | `empty 2x2` | `1` |
| Empty | `empty 3x3` | `2` |
| Snake | `landed back` | `continue from there` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[212 — 752. Open the Lock]]: Eight edges. The key idea there: There are only 10,000 equal-cost states, a perfect fit for BFS.
- [[217 — 1091. Shortest Path in Binary Matrix]]: Cells. The key idea there: Every move costs the same, so BFS over eight neighbors is shortest.

### In a real interview
Say it in this order:

1. **Brute force:** Try every sequence of rolls.
2. **Insight:** A square is a state and every roll costs one, so BFS gives the minimum.
3. **Approach in a few words:** BFS over labels.
4. **Cost:** time O(n²); space O(n²).

## 5. Recall
Q: 909. Snakes and Ladders: which pattern, and what is the insight?
A: State BFS. A square is a state and every roll costs one, so BFS gives the minimum.

Q: 909. Snakes and Ladders: what is the analogy, and what does it stand for?
A: Rumour on a board game: six → edges; round → rolls.

Q: 909. Snakes and Ladders: What is a node in Snakes and Ladders?
A: The square after any jump.

Q: 909. Snakes and Ladders: what are the time and space costs?
A: Time O(n²). Space O(n²).

@@ 3 | TOPIC | 212 — 752. Open the Lock | Medium · State BFS | SHOULD
# 212 — 752. Open the Lock

**Problem 212 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: State BFS**

[Open on LeetCode](https://leetcode.com/problems/open-the-lock/)

## 1. Problem Statement
Four wheels start at 0000, a move turns one by one step with wrap. Avoid deadends. Return fewest moves to target or -1.

### Examples
```text
Input:  deadends = ["0201","0101","0102","1212","2002"], target = "0202"
Output: 6

Input:  deadends = ["8888"], target = "0009"
Output: 1

Input:  deadends = ["8887","8889","8878","8898","8788","8988","7888","9888"], target = "8888"
Output: -1
```

### Constraints
```text
1 <= deadends.length <= 500
deadends[i].length == 4
target.length == 4
target will not be in the list deadends.
target and deadends[i] consist of digits only.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Rumour through combinations
Every code is a person with eight neighbors, one wheel turn away. The rumour starts at 0000, and deadends are people who won't listen. The round it reaches the target is the answer.

| In the story | In the solution |
|---|---|
| neighbor | one turn |
| won't listen | seen already |

### Pattern signal
**State BFS.** There are only 10,000 equal-cost states, a perfect fit for BFS.

### Thought process
1. **First idea:** DFS finds some route, not the shortest.
2. **The insight:** There are only 10,000 equal-cost states, a perfect fit for BFS.

### Optimal approach
1. Put deadends in visited.
2. Reject a dead start.
3. Expand all eight turns by levels.
4. Return the level at the target.

### Dry run
```text
target 0002
moves  code
1      0001
2      0002
```

### Why it works
BFS visits codes in order of turns needed and blocked ones are never entered, so the first arrival is optimal.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int openLock(String[] deadends, String target) {
        Set<String> seen = new HashSet<>(Arrays.asList(deadends));
        if (!seen.add("0000")) return -1;
        Queue<String> queue = new ArrayDeque<>(List.of("0000"));
        for (int moves = 0; !queue.isEmpty(); moves++) {
            for (int size = queue.size(); size > 0; size--) {
                String code = queue.poll();
                if (code.equals(target)) return moves;
                for (int i = 0; i < 4; i++) {
                    for (int turn : new int[] {1, 9}) {
                        char[] next = code.toCharArray();
                        next[i] = (char) ('0' + (next[i] - '0' + turn) % 10);
                        String s = new String(next);
                        if (seen.add(s)) queue.add(s);
                    }
                }
            }
        }
        return -1;
    }
}
```

### Code walkthrough
- `!seen.add("0000")`: The start itself may be blocked.
- `{1, 9}`: Turning by nine is one step back.
- `seen.add(s)`: Mark it when it is queued.

### Complexity
- **Time:** O(10^4) states
- **Space:** O(10^4)

### Common mistakes
1. **A dead starting code must fail.** Fix: check 0000 first.
2. **Forgetting the wrap loses moves.** Fix: use modulo 10.
3. **Marking when polled queues duplicates.** Fix: mark when queued.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Start | `[], 0000` | `0` |
| Back | `[], 0009` | `1` |
| Dead | `[0000], 0001` | `-1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[226 — 127. Word Ladder]]: Letters. The key idea there: Neighbors can be generated with 26 tries per position, and equal steps mean BFS.
- [[211 — 909. Snakes and Ladders]]: Six. The key idea there: A square is a state and every roll costs one, so BFS gives the minimum.

### In a real interview
Say it in this order:

1. **Brute force:** DFS finds some route, not the shortest.
2. **Insight:** There are only 10,000 equal-cost states, a perfect fit for BFS.
3. **Approach in a few words:** BFS on the state graph.
4. **Cost:** time O(10^4) states; space O(10^4).

## 5. Recall
Q: 752. Open the Lock: which pattern, and what is the insight?
A: State BFS. There are only 10,000 equal-cost states, a perfect fit for BFS.

Q: 752. Open the Lock: what is the analogy, and what does it stand for?
A: Rumour through combinations: neighbor → one turn; won't listen → seen already.

Q: 752. Open the Lock: How many neighbors has a lock code?
A: Eight.

Q: 752. Open the Lock: what are the time and space costs?
A: Time O(10^4) states. Space O(10^4).

@@ 3 | TOPIC | 213 — 802. Find Eventual Safe States | Medium · Cycle-state DFS | SHOULD
# 213 — 802. Find Eventual Safe States

**Problem 213 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Cycle-state DFS**

[Open on LeetCode](https://leetcode.com/problems/find-eventual-safe-states/)

## 1. Problem Statement
You get a directed graph as adjacency lists. A terminal node has no outgoing edge. A node is safe if every path starting from it ends at a terminal node. Return all safe nodes in ascending order.

### Examples
```text
Input:  graph = [[1,2],[2,3],[5],[0],[5],[],[]]
Output: [2,4,5,6]

Input:  graph = [[1,2,3,4],[1,2],[3,4],[0,4],[]]
Output: [4]
```

### Constraints
```text
n == graph.length
1 <= n <= 10^4
0 <= graph[i].length <= n
0 <= graph[i][j] <= n - 1
graph[i] is sorted in a strictly increasing order.
The graph may contain self-loops.
The number of edges in the graph will be in the range [1, 4 * 10^4].
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Testing corridors with red paint
Paint a room red as you enter and green only when all its exits lead to green. Running into red means you have walked into a loop or something leading to one.

| In the story | In the solution |
|---|---|
| red | 1 |
| green | 2 |

### Pattern signal
**Cycle-state DFS.** Being unsafe means reaching a cycle, which one colored DFS decides for every node.

### Thought process
1. **First idea:** Look for a reachable cycle separately from every node.
2. **The insight:** Being unsafe means reaching a cycle, which one colored DFS decides for every node.

### Optimal approach
1. Return a known color.
2. Paint red.
3. Check every exit.
4. Paint green if all were safe.
5. List the green ones.

### Dry run
```text
0 to 1 to 0, 2 terminal
node  color
0,1   red
2     green
```

### Why it works
A node turns green only after all its paths are proved to end. Anything that reaches a cycle meets red and stays red.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int[] color = new int[graph.length];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            if (safe(graph, color, i)) result.add(i);
        }
        return result;
    }

    private boolean safe(int[][] graph, int[] color, int node) {
        if (color[node] != 0) return color[node] == 2;
        color[node] = 1;
        for (int next : graph[node]) {
            if (!safe(graph, color, next)) return false;
        }
        color[node] = 2;
        return true;
    }
}
```

### Code walkthrough
- `color[node] = 1`: Red until proved safe.
- `return false`: One bad exit leaves it red.
- `color[node] = 2`: Every path from here ends.

### Complexity
- **Time:** O(V + E)
- **Space:** O(V)

### Common mistakes
1. **One boolean cannot tell unsafe from safe.** Fix: use three states.
2. **One safe exit is not enough.** Fix: require every exit safe.
3. **Unmarking failures repeats work.** Fix: leave them red.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Terminal | `[ [] ]` | `[0]` |
| Self | `[ [0] ]` | `[]` |
| Chain | `[ [1],[] ]` | `[0,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[214 — 207. Course Schedule]]: Any. The key idea there: All courses can be finished exactly when the prerequisite graph has no cycle.
- [[215 — 210. Course Schedule II]]: Indegrees. The key idea there: Counting unmet prerequisites shows the moment each course becomes ready.

### In a real interview
Say it in this order:

1. **Brute force:** Look for a reachable cycle separately from every node.
2. **Insight:** Being unsafe means reaching a cycle, which one colored DFS decides for every node.
3. **Approach in a few words:** Three-color memoized DFS.
4. **Cost:** time O(V + E); space O(V).

## 5. Recall
Q: 802. Find Eventual Safe States: which pattern, and what is the insight?
A: Cycle-state DFS. Being unsafe means reaching a cycle, which one colored DFS decides for every node.

Q: 802. Find Eventual Safe States: what is the analogy, and what does it stand for?
A: Testing corridors with red paint: red → 1; green → 2.

Q: 802. Find Eventual Safe States: What does meeting red mean?
A: A cycle or a known unsafe node.

Q: 802. Find Eventual Safe States: what are the time and space costs?
A: Time O(V + E). Space O(V).

@@ 3 | TOPIC | 214 — 207. Course Schedule | Medium · Cycle detection | MUST
# 214 — 207. Course Schedule

**Problem 214 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Cycle detection**

[Open on LeetCode](https://leetcode.com/problems/course-schedule/)

## 1. Problem Statement
There are numCourses courses. A prerequisite pair [a, b] means b must be taken before a. Return true if it is possible to finish all the courses.

### Examples
```text
Input:  numCourses = 2, prerequisites = [[1,0]]
Output: true

Input:  numCourses = 2, prerequisites = [[1,0],[0,1]]
Output: false
```

### Constraints
```text
1 <= numCourses <= 2000
0 <= prerequisites.length <= 5000
prerequisites[i].length == 2
0 <= ai, bi < numCourses
All the pairs prerequisites[i] are unique.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Getting dressed
Getting dressed: each item may need others on first. It is impossible only if, while preparing for one item, you are led back to that same unfinished item.

| In the story | In the solution |
|---|---|
| being put on | 1 |
| on | 2 |

### Pattern signal
**Cycle detection.** All courses can be finished exactly when the prerequisite graph has no cycle.

### Thought process
1. **First idea:** Try possible course orders.
2. **The insight:** All courses can be finished exactly when the prerequisite graph has no cycle.

### Optimal approach
1. Build prerequisite lists.
2. Mark in progress on entry.
3. Fail on meeting in progress.
4. Mark done after all.
5. Start from every course.

### Dry run
```text
0 needs 1, 1 needs 0
visit  state
0      progress
1      progress
0      cycle, false
```

### Why it works
An in-progress node is on the current path, so meeting one is a cycle, and any cycle must eventually produce such a meeting.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> needs = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) needs.add(new ArrayList<>());
        for (int[] p : prerequisites) needs.get(p[0]).add(p[1]);
        int[] state = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (!dress(needs, state, i)) return false;
        }
        return true;
    }

    private boolean dress(List<List<Integer>> needs, int[] state, int at) {
        if (state[at] != 0) return state[at] == 2;
        state[at] = 1;
        for (int before : needs.get(at)) {
            if (!dress(needs, state, before)) return false;
        }
        state[at] = 2;
        return true;
    }
}
```

### Code walkthrough
- `needs.get(p[0]).add(p[1])`: What each course needs first.
- `state[at] == 2`: A finished course can be shared safely.
- `state[at] = 1`: On the current path.

### Complexity
- **Time:** O(V + E)
- **Space:** O(V + E)

### Common mistakes
1. **One visited flag calls a diamond a cycle.** Fix: separate in-progress from done.
2. **Searching from course 0 misses components.** Fix: start from every course.
3. **Forgetting finished results is exponential.** Fix: mark courses done.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `1, []` | `true` |
| Cycle | `2, both ways` | `false` |
| Diamond | `shared prerequisite` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[215 — 210. Course Schedule II]]: Output. The key idea there: Counting unmet prerequisites shows the moment each course becomes ready.
- [[213 — 802. Find Eventual Safe States]]: List. The key idea there: Being unsafe means reaching a cycle, which one colored DFS decides for every node.

### In a real interview
Say it in this order:

1. **Brute force:** Try possible course orders.
2. **Insight:** All courses can be finished exactly when the prerequisite graph has no cycle.
3. **Approach in a few words:** Three-state cycle detection.
4. **Cost:** time O(V + E); space O(V + E).

## 5. Recall
Q: 207. Course Schedule: which pattern, and what is the insight?
A: Cycle detection. All courses can be finished exactly when the prerequisite graph has no cycle.

Q: 207. Course Schedule: what is the analogy, and what does it stand for?
A: Getting dressed: being put on → 1; on → 2.

Q: 207. Course Schedule: What proves a course cycle?
A: An edge to an in-progress node.

Q: 207. Course Schedule: what are the time and space costs?
A: Time O(V + E). Space O(V + E).

@@ 3 | TOPIC | 215 — 210. Course Schedule II | Medium · Topological order | MUST
# 215 — 210. Course Schedule II

**Problem 215 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Topological order**

[Open on LeetCode](https://leetcode.com/problems/course-schedule-ii/)

## 1. Problem Statement
There are numCourses courses. A prerequisite pair [a, b] means b must be taken before a. Return any order in which all courses can be taken, or an empty array if none exists.

### Examples
```text
Input:  numCourses = 2, prerequisites = [[1,0]]
Output: [0,1]

Input:  numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
Output: [0,2,1,3]

Input:  numCourses = 1, prerequisites = []
Output: [0]
```

### Constraints
```text
1 <= numCourses <= 2000
0 <= prerequisites.length <= numCourses * (numCourses - 1)
prerequisites[i].length == 2
0 <= ai, bi < numCourses
ai != bi
All the pairs [ai, bi] are distinct.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A row of ready dishes
In a kitchen each dish waits on others. Cook any dish waiting on nothing, strike it from everyone's list, and new dishes become ready. If you get stuck there is a circle.

| In the story | In the solution |
|---|---|
| waiting | indegree |
| ready | queue |

### Pattern signal
**Topological order.** Counting unmet prerequisites shows the moment each course becomes ready.

### Thought process
1. **First idea:** Repeatedly rescan for a course whose needs are done.
2. **The insight:** Counting unmet prerequisites shows the moment each course becomes ready.

### Optimal approach
1. Build edges and indegrees.
2. Queue the zeros.
3. Output one.
4. Decrement its dependents.
5. Return empty unless all were output.

### Dry run
```text
numCourses = 4, pairs = [1,0] [2,0] [3,1] [3,2]
take  indegrees of 0,1,2,3 afterwards  queue
-     0,1,1,2                          0
0     0,0,0,2                          1,2
1     0,0,0,1                          2
2     0,0,0,0                          3
3     -                                order 0,1,2,3
```

### Why it works
A course enters the order only after all its prerequisites. Courses never reaching zero are on or behind a cycle.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> unlocks = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) unlocks.add(new ArrayList<>());
        int[] waiting = new int[numCourses];
        for (int[] p : prerequisites) {
            unlocks.get(p[1]).add(p[0]);
            waiting[p[0]]++;
        }
        int[] order = new int[numCourses];
        int size = 0;
        for (int i = 0; i < numCourses; i++) {
            if (waiting[i] == 0) order[size++] = i;
        }
        for (int read = 0; read < size; read++) { // order is the queue
            for (int next : unlocks.get(order[read])) {
                if (--waiting[next] == 0) order[size++] = next;
            }
        }
        return size == numCourses ? order : new int[0];
    }
}
```

### Code walkthrough
- `unlocks.get(p[1]).add(p[0])`: An arrow from prerequisite to course.
- `--waiting[next] == 0`: Its last prerequisite was just taken.
- `size == numCourses`: Anything missing proves a cycle.

### Complexity
- **Time:** O(V + E)
- **Space:** O(V + E)

### Common mistakes
1. **Reversed edges give a backwards order.** Fix: point prerequisite to course.
2. **Returning a partial order hides a cycle.** Fix: check the count.
3. **Seeding one root misses others.** Fix: queue every zero indegree.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1, []` | `[0]` |
| Cycle | `2 both ways` | `[]` |
| Free | `2, []` | `[0,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[214 — 207. Course Schedule]]: Boolean. The key idea there: All courses can be finished exactly when the prerequisite graph has no cycle.
- [[213 — 802. Find Eventual Safe States]]: Reverse. The key idea there: Being unsafe means reaching a cycle, which one colored DFS decides for every node.

### In a real interview
Say it in this order:

1. **Brute force:** Repeatedly rescan for a course whose needs are done.
2. **Insight:** Counting unmet prerequisites shows the moment each course becomes ready.
3. **Approach in a few words:** Kahn's algorithm.
4. **Cost:** time O(V + E); space O(V + E).

## 5. Recall
Q: 210. Course Schedule II: which pattern, and what is the insight?
A: Topological order. Counting unmet prerequisites shows the moment each course becomes ready.

Q: 210. Course Schedule II: what is the analogy, and what does it stand for?
A: A row of ready dishes: waiting → indegree; ready → queue.

Q: 210. Course Schedule II: When can a course be output?
A: When its indegree is zero.

Q: 210. Course Schedule II: what are the time and space costs?
A: Time O(V + E). Space O(V + E).

@@ 3 | TOPIC | 216 — 934. Shortest Bridge | Medium · DFS then BFS | SHOULD
# 216 — 934. Shortest Bridge

**Problem 216 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: DFS then BFS**

[Open on LeetCode](https://leetcode.com/problems/shortest-bridge/)

## 1. Problem Statement
A binary grid contains exactly two islands, connected horizontally or vertically. You may change 0s to 1s. Return the fewest changes needed to connect the islands.

### Examples
```text
Input:  grid = [[0,1],[1,0]]
Output: 1

Input:  grid = [[0,1,0],[0,0,0],[0,0,1]]
Output: 2

Input:  grid = [[1,1,1,1,1],[1,0,0,0,1],[1,0,1,0,1],[1,0,0,0,1],[1,1,1,1,1]]
Output: 1
```

### Constraints
```text
n == grid.length == grid[i].length
2 <= n <= 100
grid[i][j] is either 0 or 1.
There are exactly two islands in grid.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Throwing ripples from a whole island
Paint one whole island, then let its entire coastline throw a ripple across the water together. The number of rings of water before a ripple touches the other island is the bridge.

| In the story | In the solution |
|---|---|
| paint | DFS |
| ripple | BFS |

### Pattern signal
**DFS then BFS.** One BFS whose sources are a whole island finds the nearest point of the other.

### Thought process
1. **First idea:** Measure every pair of cells from the two islands.
2. **The insight:** One BFS whose sources are a whole island finds the nearest point of the other.

### Optimal approach
1. Find land.
2. Paint and queue its island.
3. Expand rings over water.
4. Return the ring count on unpainted land.

### Dry run
```text
grid = [ [0,1],[1,0] ]
step             state
paint island 1   (0,1) is marked and queued
ring flips = 0   mark waters (1,1) and (0,0), queue them
ring flips = 1   from a water, (1,0) is unpainted land
return 1
```

### Why it works
Ring d holds exactly cells with d waters before them, so the first contact crosses the fewest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private static final int[][] STEPS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public int shortestBridge(int[][] grid) {
        int n = grid.length;
        Queue<int[]> shore = new ArrayDeque<>();
        for (int r = 0; r < n && shore.isEmpty(); r++) {
            for (int c = 0; c < n && shore.isEmpty(); c++) {
                if (grid[r][c] == 1) paint(grid, r, c, shore);
            }
        }
        for (int flips = 0; !shore.isEmpty(); flips++) {
            for (int size = shore.size(); size > 0; size--) {
                int[] cell = shore.poll();
                for (int[] s : STEPS) {
                    int r = cell[0] + s[0], c = cell[1] + s[1];
                    if (r < 0 || c < 0 || r == n || c == n || grid[r][c] == 2) continue;
                    if (grid[r][c] == 1) return flips;
                    grid[r][c] = 2;
                    shore.add(new int[] {r, c});
                }
            }
        }
        return -1;
    }

    private void paint(int[][] g, int r, int c, Queue<int[]> shore) {
        if (r < 0 || c < 0 || r == g.length || c == g.length || g[r][c] != 1) return;
        g[r][c] = 2;
        shore.add(new int[] {r, c});
        for (int[] s : STEPS) paint(g, r + s[0], c + s[1], shore);
    }
}
```

### Code walkthrough
- `shore.isEmpty()`: Stop after painting one island.
- `g[r][c] = 2`: Painted cells belong to the start.
- `return flips`: Count only the water between.

### Complexity
- **Time:** O(n²)
- **Space:** O(n²)

### Common mistakes
1. **Starting from one cell may miss the nearest shore.** Fix: queue the whole island.
2. **Counting the landing cell adds one.** Fix: count only water.
3. **Painting both islands loses the target.** Fix: stop after the first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Diagonal | `[ [0,1],[1,0] ]` | `1` |
| Two waters | `separated by two` | `2` |
| Ring | `around one` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[210 — 994. Rotting Oranges]]: Given sources. The key idea there: BFS started from all sources at once measures the distance to the nearest source.
- [[203 — 200. Number of Islands]]: Only DFS. The key idea there: Each connected component produces exactly one unclaimed cell that starts a search.

### In a real interview
Say it in this order:

1. **Brute force:** Measure every pair of cells from the two islands.
2. **Insight:** One BFS whose sources are a whole island finds the nearest point of the other.
3. **Approach in a few words:** Mark, then multi-source BFS.
4. **Cost:** time O(n²); space O(n²).

## 5. Recall
Q: 934. Shortest Bridge: which pattern, and what is the insight?
A: DFS then BFS. One BFS whose sources are a whole island finds the nearest point of the other.

Q: 934. Shortest Bridge: what is the analogy, and what does it stand for?
A: Throwing ripples from a whole island: paint → DFS; ripple → BFS.

Q: 934. Shortest Bridge: What starts bridge BFS?
A: Every cell of one island.

Q: 934. Shortest Bridge: what are the time and space costs?
A: Time O(n²). Space O(n²).

@@ 3 | TOPIC | Practice — Review 36 | Re-solve without looking | MUST
# Review 36

Solve on a blank page. No notes, hints or solutions.

## 934. Shortest Bridge
Find the shortest bridge between two islands.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/shortest-bridge/)

## 802. Find Eventual Safe States
Find nodes whose every path ends at a terminal.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-eventual-safe-states/)

## 733. Flood Fill
Recolor a connected region.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/flood-fill/)

## 355. Design Twitter
Design posting, following and a ten-item news feed.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/design-twitter/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 934 | | | | |
| 802 | | | | |
| 733 | | | | |
| 355 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[216 — 934. Shortest Bridge]]
- [[213 — 802. Find Eventual Safe States]]
- [[202 — 733. Flood Fill]]
- [[178 — 355. Design Twitter]]

@@ 3 | TOPIC | 217 — 1091. Shortest Path in Binary Matrix | Medium · Eight-way BFS | SHOULD
# 217 — 1091. Shortest Path in Binary Matrix

**Problem 217 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Eight-way BFS**

[Open on LeetCode](https://leetcode.com/problems/shortest-path-in-binary-matrix/)

## 1. Problem Statement
A clear path in a binary grid runs from the top-left to the bottom-right through 0 cells, moving in any of eight directions. Return the number of cells on the shortest one, or -1.

### Examples
```text
Input:  grid = [[0,1],[1,0]]
Output: 2

Input:  grid = [[0,0,0],[1,1,0],[1,1,0]]
Output: 4

Input:  grid = [[1,0,0],[1,1,0],[1,1,0]]
Output: -1
```

### Constraints
```text
n == grid.length
n == grid[i].length
1 <= n <= 100
grid[i][j] is 0 or 1
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A king's shortest walk
A chess king walks to the far corner. A rumour spreading one king-move per round reaches each square by the shortest walk. The count includes the square you started on.

| In the story | In the solution |
|---|---|
| ring | level |
| king | eight |

### Pattern signal
**Eight-way BFS.** Every move costs the same, so BFS over eight neighbors is shortest.

### Thought process
1. **First idea:** Enumerate every clear path.
2. **The insight:** Every move costs the same, so BFS over eight neighbors is shortest.

### Optimal approach
1. Reject a blocked start.
2. Mark it with length 1.
3. Expand eight neighbors by level.
4. Return the level at the corner.

### Dry run
```text
grid = [ [0,1],[1,0] ]
length  queue    neighbors
1       (0,0)    diagonal (1,1) is clear: mark and queue it
2       (1,1)    it is the target corner, return 2
```

### Why it works
BFS reaches each cell first by a path with the fewest moves, and cells are one more than moves.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] != 0) return -1;
        grid[0][0] = 1;
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {0, 0});
        for (int length = 1; !queue.isEmpty(); length++) {
            for (int size = queue.size(); size > 0; size--) {
                int[] cell = queue.poll();
                if (cell[0] == n - 1 && cell[1] == n - 1) return length;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int r = cell[0] + dr, c = cell[1] + dc;
                        if (r < 0 || c < 0 || r == n || c == n || grid[r][c] != 0) continue;
                        grid[r][c] = 1;
                        queue.add(new int[] {r, c});
                    }
                }
            }
        }
        return -1;
    }
}
```

### Code walkthrough
- `length = 1`: The first square counts.
- `dr <= 1`: All eight directions.
- `grid[r][c] = 1`: Blocking it also marks it visited.

### Complexity
- **Time:** O(n²)
- **Space:** O(n²)

### Common mistakes
1. **Four directions miss diagonal paths.** Fix: check all eight.
2. **Starting the length at 0 is one short.** Fix: count the first cell.
3. **DFS finds some path rather than the shortest.** Fix: use BFS.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [0] ]` | `1` |
| Blocked | `[ [1] ]` | `-1` |
| Diagonal | `[ [0,1],[1,0] ]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[210 — 994. Rotting Oranges]]: Many starts. The key idea there: BFS started from all sources at once measures the distance to the nearest source.
- [[228 — 778. Swim in Rising Water]]: Weights. The key idea there: A path costs its highest cell, and Dijkstra works for that cost.

### In a real interview
Say it in this order:

1. **Brute force:** Enumerate every clear path.
2. **Insight:** Every move costs the same, so BFS over eight neighbors is shortest.
3. **Approach in a few words:** Eight-neighbor BFS.
4. **Cost:** time O(n²); space O(n²).

## 5. Recall
Q: 1091. Shortest Path in Binary Matrix: which pattern, and what is the insight?
A: Eight-way BFS. Every move costs the same, so BFS over eight neighbors is shortest.

Q: 1091. Shortest Path in Binary Matrix: what is the analogy, and what does it stand for?
A: A king's shortest walk: ring → level; king → eight.

Q: 1091. Shortest Path in Binary Matrix: What does this path length count?
A: Cells, both ends included.

Q: 1091. Shortest Path in Binary Matrix: what are the time and space costs?
A: Time O(n²). Space O(n²).

@@ 3 | TOPIC | 218 — 684. Redundant Connection | Medium · Union-Find | MUST
# 218 — 684. Redundant Connection

**Problem 218 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Union-Find**

[Open on LeetCode](https://leetcode.com/problems/redundant-connection/)

## 1. Problem Statement
A tree with nodes 1 to n had one extra edge added. Return an edge that can be removed to leave a tree. If several qualify, return the one appearing last in the input.

### Examples
```text
Input:  edges = [[1,2],[1,3],[2,3]]
Output: [2,3]

Input:  edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]
Output: [1,4]
```

### Constraints
```text
n == edges.length
3 <= n <= 1000
edges[i].length == 2
1 <= ai < bi <= edges.length
ai != bi
There are no repeated edges.
The given graph is connected.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Clubs shaking hands
Each handshake merges two clubs under one president. A handshake between two people who already share a president adds nothing: it is the road that closes the loop.

| In the story | In the solution |
|---|---|
| president | root |
| merge | parent link |

### Pattern signal
**Union-Find.** Union-Find answers "already connected?" almost instantly.

### Thought process
1. **First idea:** Search for an existing path before adding each edge.
2. **The insight:** Union-Find answers "already connected?" almost instantly.

### Optimal approach
1. Give every node itself as parent.
2. Find both roots.
3. Return the edge if equal.
4. Otherwise link the roots.

### Dry run
```text
edges = [1,2] [1,3] [2,3]
edge   root of a  root of b  action
[1,2]  1          2          parent of 1 becomes 2
[1,3]  2          3          parent of 2 becomes 3
[2,3]  3          3          same root, return [2,3]
```

### Why it works
Only one cycle exists and all of its other edges come earlier, so the edge that closes it is the last one in the input.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int[] parent = new int[edges.length + 1];
        for (int i = 0; i < parent.length; i++) parent[i] = i;
        for (int[] e : edges) {
            int a = find(parent, e[0]), b = find(parent, e[1]);
            if (a == b) return e;
            parent[a] = b;
        }
        return new int[0];
    }

    private int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
}
```

### Code walkthrough
- `parent[i] = i`: Everyone presides over their own club.
- `parent[a] = b`: Join the presidents, not the members.
- `parent[parent[x]]`: Shorten the path to the president.

### Complexity
- **Time:** O(n log n) amortized bound
- **Space:** O(n)

### Common mistakes
1. **Linking the given nodes breaks groups.** Fix: link their roots.
2. **The nodes start at 1.** Fix: allocate n+1 parents.
3. **Comparing direct parents misses longer chains.** Fix: compare roots.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Triangle | `[ [1,2],[1,3],[2,3] ]` | `[2,3]` |
| Square | `[ [1,2],[2,3],[3,4],[1,4] ]` | `[1,4]` |
| Tail | `[ [1,2],[2,3],[1,3],[1,4] ]` | `[1,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[219 — 721. Accounts Merge]]: Emails. The key idea there: Accounts are nodes joined by shared emails, so people are connected components.
- [[222 — 1584. Min Cost to Connect All Points]]: Kruskal. The key idea there: The cheapest edge leaving the connected set is always safe, and arrays are ideal for a dense graph.

### In a real interview
Say it in this order:

1. **Brute force:** Search for an existing path before adding each edge.
2. **Insight:** Union-Find answers "already connected?" almost instantly.
3. **Approach in a few words:** The first same-root edge.
4. **Cost:** time O(n log n) amortized bound; space O(n).

## 5. Recall
Q: 684. Redundant Connection: which pattern, and what is the insight?
A: Union-Find. Union-Find answers "already connected?" almost instantly.

Q: 684. Redundant Connection: what is the analogy, and what does it stand for?
A: Clubs shaking hands: president → root; merge → parent link.

Q: 684. Redundant Connection: When is an edge redundant?
A: When endpoints share a root.

Q: 684. Redundant Connection: what are the time and space costs?
A: Time O(n log n) amortized bound. Space O(n).

@@ 3 | TOPIC | 219 — 721. Accounts Merge | Medium · Union-Find | SHOULD
# 219 — 721. Accounts Merge

**Problem 219 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Union-Find**

[Open on LeetCode](https://leetcode.com/problems/accounts-merge/)

## 1. Problem Statement
Merge accounts sharing any email, transitively. Return name then sorted emails.

### Examples
```text
Input:  accounts = [["John","johnsmith@mail.com","john_newyork@mail.com"],["John","johnsmith@mail.com","john00@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]
Output: [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]

Input:  accounts = [["Gabe","Gabe0@m.co","Gabe3@m.co","Gabe1@m.co"],["Kevin","Kevin3@m.co","Kevin5@m.co","Kevin0@m.co"],["Ethan","Ethan5@m.co","Ethan4@m.co","Ethan0@m.co"],["Hanzo","Hanzo3@m.co","Hanzo1@m.co","Hanzo0@m.co"],["Fern","Fern5@m.co","Fern1@m.co","Fern0@m.co"]]
Output: [["Ethan","Ethan0@m.co","Ethan4@m.co","Ethan5@m.co"],["Gabe","Gabe0@m.co","Gabe1@m.co","Gabe3@m.co"],["Hanzo","Hanzo0@m.co","Hanzo1@m.co","Hanzo3@m.co"],["Kevin","Kevin0@m.co","Kevin3@m.co","Kevin5@m.co"],["Fern","Fern0@m.co","Fern1@m.co","Fern5@m.co"]]
```

### Constraints
```text
1 <= accounts.length <= 1000
2 <= accounts[i].length <= 10
1 <= accounts[i][j].length <= 30
accounts[i][0] consists of English letters.
accounts[i][j] (for j > 0) is a valid email.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Address cards on one key ring
Every account is a key ring of addresses. Finding the same address on two rings clips them together, and clips can chain. At the end each bunch is one person.

| In the story | In the solution |
|---|---|
| clip | union |
| ring | root |

### Pattern signal
**Union-Find.** Accounts are nodes joined by shared emails, so people are connected components.

### Thought process
1. **First idea:** Intersect accounts pairwise until nothing changes.
2. **The insight:** Accounts are nodes joined by shared emails, so people are connected components.

### Optimal approach
1. Map each email to its first account.
2. Union repeats.
3. Group emails by final root.
4. Sort.
5. Add the name.

### Dry run
```text
A x,y / A y,z / A q
email  action
y      union 0 and 1
groups x,y,z and q
```

### Why it works
Union-Find's groups are the transitive closure of sharing an email, which is the merge rule.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        int n = accounts.size();
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        Map<String, Integer> owner = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < accounts.get(i).size(); j++) {
                Integer first = owner.putIfAbsent(accounts.get(i).get(j), i);
                if (first != null) parent[find(parent, i)] = find(parent, first);
            }
        }
        Map<Integer, TreeSet<String>> rings = new HashMap<>();
        owner.forEach((email, i) ->
                rings.computeIfAbsent(find(parent, i), k -> new TreeSet<>()).add(email));
        List<List<String>> result = new ArrayList<>();
        rings.forEach((root, emails) -> {
            List<String> merged = new ArrayList<>();
            merged.add(accounts.get(root).get(0));
            merged.addAll(emails);
            result.add(merged);
        });
        return result;
    }

    private int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
}
```

### Code walkthrough
- `putIfAbsent`: The first ring holding this address.
- `parent[find(parent, i)]`: Clip the two bunches.
- `TreeSet`: Emails come out sorted.

### Complexity
- **Time:** O(N log N)
- **Space:** O(N)

### Common mistakes
1. **Merging by name joins strangers.** Fix: merge only by email.
2. **One pass of intersections misses chains.** Fix: use Union-Find.
3. **Grouping by an old parent splits groups.** Fix: find the root again at the end.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `one account` | `sorted` |
| Same name | `no shared email` | `two` |
| Chain | `A-B-C` | `one` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[218 — 684. Redundant Connection]]: Simple. The key idea there: Union-Find answers "already connected?" almost instantly.
- [[203 — 200. Number of Islands]]: DFS. The key idea there: Each connected component produces exactly one unclaimed cell that starts a search.

### In a real interview
Say it in this order:

1. **Brute force:** Intersect accounts pairwise until nothing changes.
2. **Insight:** Accounts are nodes joined by shared emails, so people are connected components.
3. **Approach in a few words:** Union accounts through emails.
4. **Cost:** time O(N log N); space O(N).

## 5. Recall
Q: 721. Accounts Merge: which pattern, and what is the insight?
A: Union-Find. Accounts are nodes joined by shared emails, so people are connected components.

Q: 721. Accounts Merge: what is the analogy, and what does it stand for?
A: Address cards on one key ring: clip → union; ring → root.

Q: 721. Accounts Merge: What triggers account union?
A: A previously seen email.

Q: 721. Accounts Merge: what are the time and space costs?
A: Time O(N log N). Space O(N).

@@ 3 | TOPIC | 220 — 785. Is Graph Bipartite | Medium · Two-coloring | SHOULD
# 220 — 785. Is Graph Bipartite

**Problem 220 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Two-coloring**

[Open on LeetCode](https://leetcode.com/problems/is-graph-bipartite/)

## 1. Problem Statement
An undirected graph is given as adjacency lists and may be disconnected. Return true if its nodes can be split into two sets so that every edge joins a node of one set to a node of the other.

### Examples
```text
Input:  graph = [[1,2,3],[0,2],[0,1,3],[0,2]]
Output: false

Input:  graph = [[1,3],[0,2],[1,3],[0,2]]
Output: true
```

### Constraints
```text
graph.length == n
1 <= n <= 100
0 <= graph[u].length < n
0 <= graph[u][i] <= n - 1
graph[u] does not contain u.
All the values of graph[u] are unique.
If graph[u] contains v, then graph[v] contains u.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Alternate shirts
Give one person a red shirt, all their opponents blue, their opponents red again. If someone is ever required to match an opponent, two teams are impossible.

| In the story | In the solution |
|---|---|
| shirt | 1 or -1 |
| opponent | neighbor |

### Pattern signal
**Two-coloring.** Choosing one node's color forces every other in its component.

### Thought process
1. **First idea:** Try all 2^n team assignments.
2. **The insight:** Choosing one node's color forces every other in its component.

### Optimal approach
1. Seed each uncolored component.
2. Give neighbors the opposite color.
3. Push them.
4. Fail when an edge has equal colors.

### Dry run
```text
graph is a triangle 0-1, 0-2, 1-2
visit  action                        colors of 0,1,2
0      seed red                      red, ?, ?
0      give both neighbors blue      red, blue, blue
2      neighbor 1 is blue, like 2    conflict, return false
```

### Why it works
Colors are forced, so a conflict proves no split exists, and without a conflict the coloring itself is a valid split.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isBipartite(int[][] graph) {
        int[] shirt = new int[graph.length];
        for (int start = 0; start < graph.length; start++) {
            if (shirt[start] != 0) continue;
            shirt[start] = 1;
            Deque<Integer> work = new ArrayDeque<>(List.of(start));
            while (!work.isEmpty()) {
                int at = work.pop();
                for (int next : graph[at]) {
                    if (shirt[next] == shirt[at]) return false;
                    if (shirt[next] == 0) {
                        shirt[next] = -shirt[at];
                        work.push(next);
                    }
                }
            }
        }
        return true;
    }
}
```

### Code walkthrough
- `shirt[start] = 1`: A new component may start either color.
- `-shirt[at]`: Opponents wear the other shirt.
- `shirt[next] == shirt[at]`: Opponents in the same shirt, so impossible.

### Complexity
- **Time:** O(V + E)
- **Space:** O(V)

### Common mistakes
1. **Checking one component misses conflicts.** Fix: seed every node.
2. **Rejecting every cycle is wrong.** Fix: reject only equal-colored neighbors.
3. **The two sets need not be equal in size.** Fix: never compare counts.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Isolated | `[ [] ]` | `true` |
| Triangle | `three all connected` | `false` |
| Square | `4-cycle` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[214 — 207. Course Schedule]]: States. The key idea there: All courses can be finished exactly when the prerequisite graph has no cycle.
- **886. Dislikes**: Same.

### In a real interview
Say it in this order:

1. **Brute force:** Try all 2^n team assignments.
2. **Insight:** Choosing one node's color forces every other in its component.
3. **Approach in a few words:** Two-color each component.
4. **Cost:** time O(V + E); space O(V).

## 5. Recall
Q: 785. Is Graph Bipartite: which pattern, and what is the insight?
A: Two-coloring. Choosing one node's color forces every other in its component.

Q: 785. Is Graph Bipartite: what is the analogy, and what does it stand for?
A: Alternate shirts: shirt → 1 or -1; opponent → neighbor.

Q: 785. Is Graph Bipartite: When is a graph not bipartite?
A: An odd cycle, seen as equal neighbor colors.

Q: 785. Is Graph Bipartite: what are the time and space costs?
A: Time O(V + E). Space O(V).

@@ 3 | TOPIC | 221 — 399. Evaluate Division | Medium · Weighted graph | SHOULD
# 221 — 399. Evaluate Division

**Problem 221 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Weighted graph**

[Open on LeetCode](https://leetcode.com/problems/evaluate-division/)

## 1. Problem Statement
You get equations a / b = value over variable names, and queries c / d. Return each query's answer, or -1.0 when it cannot be determined. A variable that appears in no equation is unknown, even divided by itself.

### Examples
```text
Input:  equations = [["a","b"],["b","c"]], values = [2.0,3.0], queries = [["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]
Output: [6.00000,0.50000,-1.00000,1.00000,-1.00000]

Input:  equations = [["a","b"],["b","c"],["bc","cd"]], values = [1.5,2.5,5.0], queries = [["a","c"],["c","b"],["bc","cd"],["cd","bc"]]
Output: [3.75000,0.40000,5.00000,0.20000]

Input:  equations = [["a","b"]], values = [0.5], queries = [["a","b"],["b","a"],["a","c"],["x","y"]]
Output: [0.50000,2.00000,-1.00000,-1.00000]
```

### Constraints
```text
1 <= equations.length <= 20
equations[i].length == 2
1 <= Ai.length, Bi.length <= 5
values.length == equations.length
0.0 < values[i] <= 20.0
1 <= queries.length <= 20
queries[i].length == 2
1 <= Cj.length, Dj.length <= 5
Ai, Bi, Cj, Dj consist of lower case English letters and digits.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Currency exchange
Every equation is an exchange booth working both ways. To price a against c walk through any chain of booths multiplying the rates.

| In the story | In the solution |
|---|---|
| booth | weighted edge |
| product | path weight |

### Pattern signal
**Weighted graph.** a/c is the product of edge ratios along any path from a to c, because the middles cancel.

### Thought process
1. **First idea:** Substitute equations repeatedly.
2. **The insight:** a/c is the product of edge ratios along any path from a to c, because the middles cancel.

### Optimal approach
1. Add each ratio and its inverse.
2. For a query start fresh.
3. Reject unknown variables.
4. Search.
5. Multiply while returning.

### Dry run
```text
a/b = 2, b/c = 3, query a/c
call            edge taken  returns
exchange(c, c)  -           1
exchange(b, c)  b to c, 3   3 * 1 = 3
exchange(a, c)  a to b, 2   2 * 3 = 6
```

### Why it works
Along a path a/b times b/c is a/c, and consistent data gives every path the same product.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Map<String, Double>> booths = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            String a = equations.get(i).get(0), b = equations.get(i).get(1);
            booths.computeIfAbsent(a, k -> new HashMap<>()).put(b, values[i]);
            booths.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1 / values[i]);
        }
        double[] result = new double[queries.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = exchange(booths, queries.get(i).get(0), queries.get(i).get(1), new HashSet<>());
        }
        return result;
    }

    private double exchange(Map<String, Map<String, Double>> booths, String from, String to, Set<String> seen) {
        if (!booths.containsKey(from) || !seen.add(from)) return -1;
        if (from.equals(to)) return 1;
        for (Map.Entry<String, Double> e : booths.get(from).entrySet()) {
            double rest = exchange(booths, e.getKey(), to, seen);
            if (rest != -1) return e.getValue() * rest;
        }
        return -1;
    }
}
```

### Code walkthrough
- `1 / values[i]`: The booth also works backwards.
- `!booths.containsKey(from)`: Unknown is checked before equality.
- `e.getValue() * rest`: Multiply the rate by the rest.

### Complexity
- **Time:** O(Q·(V + E))
- **Space:** O(V + E)

### Common mistakes
1. **An unknown variable over itself is not 1.** Fix: test existence first.
2. **A shared visited set blocks later queries.** Fix: make one per query.
3. **Adding edge weights is wrong.** Fix: multiply them.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Self | `a/a known` | `1` |
| Unknown | `x/x` | `-1` |
| Reverse | `b/a` | `0.5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[204 — 133. Clone Graph]]: No weights. The key idea there: One old-to-new map serves both as the visited set and as the translation of every edge.
- [[219 — 721. Accounts Merge]]: Weighted variant. The key idea there: Accounts are nodes joined by shared emails, so people are connected components.

### In a real interview
Say it in this order:

1. **Brute force:** Substitute equations repeatedly.
2. **Insight:** a/c is the product of edge ratios along any path from a to c, because the middles cancel.
3. **Approach in a few words:** A weighted reachability search.
4. **Cost:** time O(Q·(V + E)); space O(V + E).

## 5. Recall
Q: 399. Evaluate Division: which pattern, and what is the insight?
A: Weighted graph. a/c is the product of edge ratios along any path from a to c, because the middles cancel.

Q: 399. Evaluate Division: what is the analogy, and what does it stand for?
A: Currency exchange: booth → weighted edge; product → path weight.

Q: 399. Evaluate Division: How are division edges combined?
A: Multiply along the path.

Q: 399. Evaluate Division: what are the time and space costs?
A: Time O(Q·(V + E)). Space O(V + E).

@@ 3 | TOPIC | 222 — 1584. Min Cost to Connect All Points | Medium · Prim MST | MUST
# 222 — 1584. Min Cost to Connect All Points

**Problem 222 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Prim MST**

[Open on LeetCode](https://leetcode.com/problems/min-cost-to-connect-all-points/)

## 1. Problem Statement
You get points on a plane. Connecting two costs their Manhattan distance. Return the least total cost making all points connected, with exactly one simple path between any two.

### Examples
```text
Input:  points = [[0,0],[2,2],[3,10],[5,2],[7,0]]
Output: 20

Input:  points = [[3,12],[-2,5],[-4,1]]
Output: 18
```

### Constraints
```text
1 <= points.length <= 1000
-10^6 <= xi, yi <= 10^6
All pairs (xi, yi) are distinct.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Growing one power grid
Grow a power grid from one station. Every time lay the cheapest cable from a powered point to an unpowered one, then see whether the new point offers anyone a shorter cable.

| In the story | In the solution |
|---|---|
| cheapest cable | cost array |
| powered | boolean |

### Pattern signal
**Prim MST.** The cheapest edge leaving the connected set is always safe, and arrays are ideal for a dense graph.

### Thought process
1. **First idea:** Try spanning trees, of which there are exponentially many.
2. **The insight:** The cheapest edge leaving the connected set is always safe, and arrays are ideal for a dense graph.

### Optimal approach
1. Set the first cost to zero.
2. Pick the cheapest unpowered point.
3. Power it and add.
4. Update the others' cable costs.

### Dry run
```text
0,0 2,2 3,10 5,2 7,0
add   cost
0,0   0
2,2   4
5,2   3
7,0   4
3,10  9, total 20
```

### Why it works
By the cut property the cheapest edge crossing from powered to unpowered belongs to some minimum spanning tree.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        int[] cable = new int[n];
        Arrays.fill(cable, Integer.MAX_VALUE);
        cable[0] = 0;
        boolean[] powered = new boolean[n];
        int total = 0;
        for (int round = 0; round < n; round++) {
            int at = -1;
            for (int i = 0; i < n; i++) {
                if (!powered[i] && (at < 0 || cable[i] < cable[at])) at = i;
            }
            powered[at] = true;
            total += cable[at];
            for (int i = 0; i < n; i++) {
                int d = Math.abs(points[at][0] - points[i][0]) + Math.abs(points[at][1] - points[i][1]);
                cable[i] = Math.min(cable[i], d);
            }
        }
        return total;
    }
}
```

### Code walkthrough
- `cable[0] = 0`: The power station costs nothing.
- `cable[i] < cable[at]`: The cheapest cable available.
- `Math.min(cable[i], d)`: A nearer connection through the new point.

### Complexity
- **Time:** O(n²)
- **Space:** O(n)

### Common mistakes
1. **Joining points in input order is not minimal.** Fix: choose by distance.
2. **Adding path lengths gives shortest paths.** Fix: store single edge costs.
3. **Euclidean distance is the wrong metric.** Fix: use Manhattan.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `one point` | `0` |
| Two | `0,0 and 1,1` | `2` |
| Line | `0,1,2` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[223 — 743. Network Delay Time]]: Accumulated. The key idea there: A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.
- [[218 — 684. Redundant Connection]]: Union. The key idea there: Union-Find answers "already connected?" almost instantly.

### In a real interview
Say it in this order:

1. **Brute force:** Try spanning trees, of which there are exponentially many.
2. **Insight:** The cheapest edge leaving the connected set is always safe, and arrays are ideal for a dense graph.
3. **Approach in a few words:** Array Prim for a dense graph.
4. **Cost:** time O(n²); space O(n).

## 5. Recall
Q: 1584. Min Cost to Connect All Points: which pattern, and what is the insight?
A: Prim MST. The cheapest edge leaving the connected set is always safe, and arrays are ideal for a dense graph.

Q: 1584. Min Cost to Connect All Points: what is the analogy, and what does it stand for?
A: Growing one power grid: cheapest cable → cost array; powered → boolean.

Q: 1584. Min Cost to Connect All Points: What does Prim choose?
A: The cheapest edge to an unconnected node.

Q: 1584. Min Cost to Connect All Points: what are the time and space costs?
A: Time O(n²). Space O(n).

@@ 3 | TOPIC | Practice — Review 37 | Re-solve without looking | MUST
# Review 37

Solve on a blank page. No notes, hints or solutions.

## 1584. Min Cost to Connect All Points
Connect all points at minimum Manhattan cost.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/min-cost-to-connect-all-points/)

## 721. Accounts Merge
Merge accounts sharing an email.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/accounts-merge/)

## 130. Surrounded Regions
Capture regions surrounded by X.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/surrounded-regions/)

## 502. IPO
Choose up to k projects to maximize capital.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/ipo/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 1584 | | | | |
| 721 | | | | |
| 130 | | | | |
| 502 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[222 — 1584. Min Cost to Connect All Points]]
- [[219 — 721. Accounts Merge]]
- [[208 — 130. Surrounded Regions]]
- [[184 — 502. IPO]]

@@ 3 | TOPIC | 223 — 743. Network Delay Time | Medium · Bellman-Ford | MUST
# 223 — 743. Network Delay Time

**Problem 223 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Bellman-Ford**

[Open on LeetCode](https://leetcode.com/problems/network-delay-time/)

## 1. Problem Statement
A network has nodes 1 to n and directed edges with travel times. A signal leaves node `k`. Return the time until every node has received it, or -1 if some never do.

### Examples
```text
Input:  times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
Output: 2

Input:  times = [[1,2,1]], n = 2, k = 1
Output: 1

Input:  times = [[1,2,1]], n = 2, k = 2
Output: -1
```

### Constraints
```text
1 <= k <= n <= 100
1 <= times.length <= 6000
times[i].length == 3
1 <= ui, vi <= n
ui != vi
0 <= wi <= 100
All the pairs (ui, vi) are unique. (i.e., no multiple edges.)
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: News improving by rounds
News improves in rounds. In each, every road offers "through me you would hear at time x" and better news replaces worse. After n-1 rounds no better news exists.

| In the story | In the solution |
|---|---|
| offer | relax |
| day | round |

### Pattern signal
**Bellman-Ford.** A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.

### Thought process
1. **First idea:** Enumerate every path.
2. **The insight:** A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.

### Optimal approach
1. Fill with a safe infinity.
2. Set k to zero.
3. Relax all edges n-1 times.
4. Return the maximum or -1.

### Dry run
```text
times = [2,1,1] [2,3,1] [3,4,1], n = 4, k = 2
edge relaxed  heard at 1,2,3,4
start         inf, 0, inf, inf
2 to 1        1, 0, inf, inf
2 to 3        1, 0, 1, inf
3 to 4        1, 0, 1, 2
later rounds change nothing, the maximum is 2
```

### Why it works
After i rounds every shortest path with at most i edges is found, and with non-negative weights some shortest path has no repeated node.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        final int FAR = 1_000_000;
        int[] heard = new int[n + 1];
        Arrays.fill(heard, FAR);
        heard[k] = 0;
        for (int round = 1; round < n; round++) {
            for (int[] road : times) {
                heard[road[1]] = Math.min(heard[road[1]], heard[road[0]] + road[2]);
            }
        }
        int last = 0;
        for (int i = 1; i <= n; i++) last = Math.max(last, heard[i]);
        return last >= FAR ? -1 : last;
    }
}
```

### Code walkthrough
- `FAR = 1_000_000`: An infinity that survives an addition.
- `heard[road[0]] + road[2]`: The offer made by this road.
- `Math.max`: The last person to hear.

### Complexity
- **Time:** O(n·E)
- **Space:** O(n)

### Common mistakes
1. **Adding to Integer.MAX_VALUE overflows.** Fix: use a smaller infinity.
2. **Summing the times is wrong because delivery is parallel.** Fix: take the maximum.
3. **Plain BFS ignores weights.** Fix: relax or use Dijkstra.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `n 1` | `0` |
| Unreached | `n 2, none` | `-1` |
| Zero | `weight 0` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[224 — 787. Cheapest Flights Within K Stops]]: Snapshot. The key idea there: Relaxing from a snapshot adds at most one flight per round, enforcing the limit.
- [[228 — 778. Swim in Rising Water]]: Heap. The key idea there: A path costs its highest cell, and Dijkstra works for that cost.

### In a real interview
Say it in this order:

1. **Brute force:** Enumerate every path.
2. **Insight:** A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.
3. **Approach in a few words:** Bellman-Ford, Dijkstra for speed.
4. **Cost:** time O(n·E); space O(n).

## 5. Recall
Q: 743. Network Delay Time: which pattern, and what is the insight?
A: Bellman-Ford. A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.

Q: 743. Network Delay Time: what is the analogy, and what does it stand for?
A: News improving by rounds: offer → relax; day → round.

Q: 743. Network Delay Time: How many relaxation rounds suffice?
A: n minus one.

Q: 743. Network Delay Time: what are the time and space costs?
A: Time O(n·E). Space O(n).

@@ 3 | TOPIC | 224 — 787. Cheapest Flights Within K Stops | Medium · Bounded Bellman-Ford | MUST
# 224 — 787. Cheapest Flights Within K Stops

**Problem 224 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Medium | Pattern: Bounded Bellman-Ford**

[Open on LeetCode](https://leetcode.com/problems/cheapest-flights-within-k-stops/)

## 1. Problem Statement
You get flights with prices between n cities. Return the cheapest price from `src` to `dst` with at most `k` intermediate stops, which means k+1 flights, or -1 if there is no such route.

### Examples
```text
Input:  n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]], src = 0, dst = 3, k = 1
Output: 700

Input:  n = 3, flights = [[0,1,100],[1,2,100],[0,2,500]], src = 0, dst = 2, k = 1
Output: 200

Input:  n = 3, flights = [[0,1,100],[1,2,100],[0,2,500]], src = 0, dst = 2, k = 0
Output: 500
```

### Constraints
```text
2 <= n <= 100
0 <= flights.length <= (n * (n - 1) / 2)
flights[i].length == 3
0 <= fromi, toi < n
fromi != toi
1 <= pricei <= 10^4
There will not be any multiple flights between two cities.
0 <= src, dst, k < n
src != dst
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: One flight per day
A traveller may take one flight per day. So today's prices may only build on yesterday's board. Reading today's board would let someone take two flights in a day.

| In the story | In the solution |
|---|---|
| board | previous array |
| day | an edge |

### Pattern signal
**Bounded Bellman-Ford.** Relaxing from a snapshot adds at most one flight per round, enforcing the limit.

### Thought process
1. **First idea:** Enumerate all routes within the limit.
2. **The insight:** Relaxing from a snapshot adds at most one flight per round, enforcing the limit.

### Optimal approach
1. Set the source to zero.
2. Repeat k+1 times.
3. Copy the board.
4. Offer each flight from the old board.
5. Return the destination.

### Dry run
```text
01 100, 12 100, 23 200, 13 600. k 1
round  prices
1      node1 100
2      node2 200, node3 700
```

### Why it works
After round i each price is the best using at most i flights, because every update extends a price of at most i-1.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        final int FAR = 1_000_000_000;
        int[] price = new int[n];
        Arrays.fill(price, FAR);
        price[src] = 0;
        for (int day = 0; day <= k; day++) {
            int[] today = price.clone();
            for (int[] f : flights) today[f[1]] = Math.min(today[f[1]], price[f[0]] + f[2]);
            price = today;
        }
        return price[dst] == FAR ? -1 : price[dst];
    }
}
```

### Code walkthrough
- `day <= k`: k stops allow k+1 flights.
- `price.clone()`: Freeze yesterday's board.
- `price[f[0]]`: Build only on yesterday's price.

### Complexity
- **Time:** O(k·E)
- **Space:** O(n)

### Common mistakes
1. **Updating in place chains flights in one round.** Fix: use a snapshot.
2. **Running k rounds allows one flight too few.** Fix: run k+1.
3. **Plain Dijkstra discards routes with fewer stops.** Fix: track stops.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Direct | `k 0, edge` | `its price` |
| None | `no edge` | `-1` |
| Limit | `two edges, k 0` | `-1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[223 — 743. Network Delay Time]]: In-place. The key idea there: A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.
- [[236 — 322. Coin Change]]: Counts. The key idea there: Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.

### In a real interview
Say it in this order:

1. **Brute force:** Enumerate all routes within the limit.
2. **Insight:** Relaxing from a snapshot adds at most one flight per round, enforcing the limit.
3. **Approach in a few words:** Bounded relaxation with snapshots.
4. **Cost:** time O(k·E); space O(n).

## 5. Recall
Q: 787. Cheapest Flights Within K Stops: which pattern, and what is the insight?
A: Bounded Bellman-Ford. Relaxing from a snapshot adds at most one flight per round, enforcing the limit.

Q: 787. Cheapest Flights Within K Stops: what is the analogy, and what does it stand for?
A: One flight per day: board → previous array; day → an edge.

Q: 787. Cheapest Flights Within K Stops: Why copy prices each round?
A: To add at most one flight.

Q: 787. Cheapest Flights Within K Stops: what are the time and space costs?
A: Time O(k·E). Space O(n).

@@ 2 | MODULE | Graphs — Hard | 4 problems | MUST
# Graphs: Hard

Patterns in this group: Shrink with memo, Word BFS, Eulerian path, Minimum bottleneck.

@@ 3 | TOPIC | 225 — 1553. Minimum Number of Days to Eat N Oranges | Hard · Shrink with memo | SHOULD
# 225 — 1553. Minimum Number of Days to Eat N Oranges

**Problem 225 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Hard | Pattern: Shrink with memo**

[Open on LeetCode](https://leetcode.com/problems/minimum-number-of-days-to-eat-n-oranges/)

## 1. Problem Statement
Daily eat one orange, or n/2 if even, or 2n/3 if divisible by 3. Return fewest days for n.

### Examples
```text
Input:  n = 10
Output: 4

Input:  n = 6
Output: 3
```

### Constraints
```text
1 <= n <= 2 * 10^9
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Walking to the nearest express lift
You descend a tower that has express lifts at multiples of two and three. Single steps are only worth walking to reach the nearest lift, so from any floor there are just two sensible plans.

| In the story | In the solution |
|---|---|
| steps | remainder |
| lifts | divide |

### Pattern signal
**Shrink with memo.** It is only useful to eat singles up to the next multiple, which leaves very few states.

### Thought process
1. **First idea:** Compute an answer for every value up to two billion.
2. **The insight:** It is only useful to eat singles up to the next multiple, which leaves very few states.

### Optimal approach
1. Return n for 0 and 1.
2. Price walking to and halving.
3. Price walking to and thirding.
4. Memoize the smaller.

### Dry run
```text
10
route  days
to 9   1
to 3   1
to 1   1, f(3)=2
to 0   1, total 4
```

### Why it works
Eating extra singles before a shrink can be replaced by at most as many after it, so the two compressed moves are enough.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private final Map<Integer, Integer> memo = new HashMap<>();

    public int minDays(int n) {
        if (n <= 1) return n;
        Integer known = memo.get(n);
        if (known != null) return known;
        int half = n % 2 + 1 + minDays(n / 2);
        int third = n % 3 + 1 + minDays(n / 3);
        int best = Math.min(half, third);
        memo.put(n, best);
        return best;
    }
}
```

### Code walkthrough
- `n % 2 + 1`: The walk plus one lift.
- `minDays(n / 3)`: Two thirds are eaten and one third remains.
- `memo.put`: Only a few states repeat, so save them.

### Complexity
- **Time:** O(log² n)
- **Space:** O(log² n)

### Common mistakes
1. **A state for every value is too large.** Fix: jump to the next multiple.
2. **Always dividing by three is not optimal.** Fix: try both shrinks.
3. **After eating two thirds n/3 remain.** Fix: recurse on n/3.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `1` |
| Two | `2` | `2` |
| Six | `6` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[236 — 322. Coin Change]]: Full DP. The key idea there: Removing the last coin from an optimal answer leaves an optimal answer for a smaller amount.
- [[229 — 70. Climbing Stairs]]: Dense. The key idea there: Every climb ends with a 1-step or a 2-step, which splits the count into two smaller, known counts.

### In a real interview
Say it in this order:

1. **Brute force:** Compute an answer for every value up to two billion.
2. **Insight:** It is only useful to eat singles up to the next multiple, which leaves very few states.
3. **Approach in a few words:** Skip to useful states and memoize.
4. **Cost:** time O(log² n); space O(log² n).

## 5. Recall
Q: 1553. Minimum Number of Days to Eat N Oranges: which pattern, and what is the insight?
A: Shrink with memo. It is only useful to eat singles up to the next multiple, which leaves very few states.

Q: 1553. Minimum Number of Days to Eat N Oranges: what is the analogy, and what does it stand for?
A: Walking to the nearest express lift: steps → remainder; lifts → divide.

Q: 1553. Minimum Number of Days to Eat N Oranges: What are the two compressed choices?
A: Shrink by 2 or by 3 after removing the remainder.

Q: 1553. Minimum Number of Days to Eat N Oranges: what are the time and space costs?
A: Time O(log² n). Space O(log² n).

@@ 3 | TOPIC | 226 — 127. Word Ladder | Hard · Word BFS | MUST
# 226 — 127. Word Ladder

**Problem 226 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Hard | Pattern: Word BFS**

[Open on LeetCode](https://leetcode.com/problems/word-ladder/)

## 1. Problem Statement
Change one letter at a time through dictionary words. Return words in the shortest chain, or 0.

### Examples
```text
Input:  beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
Output: 5

Input:  beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log"]
Output: 0
```

### Constraints
```text
1 <= beginWord.length <= 10
endWord.length == beginWord.length
1 <= wordList.length <= 5000
wordList[i].length == beginWord.length
beginWord, endWord, and wordList[i] consist of lowercase English letters.
beginWord != endWord
All the words in wordList are unique.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A rumour among near-namesakes
Words differing by one letter are neighbors who gossip. The rumour starts at the first word, and the round it reaches the last word is the shortest chain, counted in words.

| In the story | In the solution |
|---|---|
| talk | 25L tries |
| unheard | set |

### Pattern signal
**Word BFS.** Neighbors can be generated with 26 tries per position, and equal steps mean BFS.

### Thought process
1. **First idea:** Compare all pairs of words or try every chain.
2. **The insight:** Neighbors can be generated with 26 tries per position, and equal steps mean BFS.

### Optimal approach
1. Put words in a set.
2. Reject a missing end.
3. Expand by level.
4. Try every letter.
5. Remove when queued.
6. Return the level.

### Dry run
```text
hit to cog
length  words
1       hit
2       hot
3       dot,lot
4       dog,log
5       cog
```

### Why it works
BFS explores by number of changes, so the first arrival of the end word is a shortest chain.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> unheard = new HashSet<>(wordList);
        if (!unheard.contains(endWord)) return 0;
        unheard.remove(beginWord);
        Queue<String> queue = new ArrayDeque<>(List.of(beginWord));
        for (int length = 1; !queue.isEmpty(); length++) {
            for (int size = queue.size(); size > 0; size--) {
                String word = queue.poll();
                if (word.equals(endWord)) return length;
                char[] letters = word.toCharArray();
                for (int i = 0; i < letters.length; i++) {
                    char old = letters[i];
                    for (char c = 'a'; c <= 'z'; c++) {
                        letters[i] = c;
                        String next = new String(letters);
                        if (unheard.remove(next)) queue.add(next);
                    }
                    letters[i] = old;
                }
            }
        }
        return 0;
    }
}
```

### Code walkthrough
- `length = 1`: The first word counts.
- `unheard.remove(next)`: Valid and not yet heard.
- `letters[i] = old`: Only one letter may differ.

### Complexity
- **Time:** O(26·N·L²)
- **Space:** O(N·L)

### Common mistakes
1. **Counting changes is one short.** Fix: count words.
2. **Not restoring the letter changes two positions.** Fix: undo it.
3. **Removing a word when polled queues duplicates.** Fix: remove it when queued.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Absent | `end missing` | `0` |
| Adjacent | `a,b` | `2` |
| Cut | `hit,cog, [cog]` | `0` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[212 — 752. Open the Lock]]: Digits. The key idea there: There are only 10,000 equal-cost states, a perfect fit for BFS.
- **126. All**: Parents.

### In a real interview
Say it in this order:

1. **Brute force:** Compare all pairs of words or try every chain.
2. **Insight:** Neighbors can be generated with 26 tries per position, and equal steps mean BFS.
3. **Approach in a few words:** Implicit BFS.
4. **Cost:** time O(26·N·L²); space O(N·L).

## 5. Recall
Q: 127. Word Ladder: which pattern, and what is the insight?
A: Word BFS. Neighbors can be generated with 26 tries per position, and equal steps mean BFS.

Q: 127. Word Ladder: what is the analogy, and what does it stand for?
A: A rumour among near-namesakes: talk → 25L tries; unheard → set.

Q: 127. Word Ladder: How are word neighbors generated?
A: Replace each position with 26 letters and check the set.

Q: 127. Word Ladder: what are the time and space costs?
A: Time O(26·N·L²). Space O(N·L).

@@ 3 | TOPIC | 227 — 332. Reconstruct Itinerary | Hard · Eulerian path | MUST
# 227 — 332. Reconstruct Itinerary

**Problem 227 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Hard | Pattern: Eulerian path**

[Open on LeetCode](https://leetcode.com/problems/reconstruct-itinerary/)

## 1. Problem Statement
Use every ticket once from JFK, the smallest lexical itinerary. One exists.

### Examples
```text
Input:  tickets = [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]
Output: ["JFK","MUC","LHR","SFO","SJC"]

Input:  tickets = [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]
Output: ["JFK","ATL","JFK","SFO","ATL","SFO"]
```

### Constraints
```text
1 <= tickets.length <= 300
tickets[i].length == 2
fromi.length == 3
toi.length == 3
fromi and toi consist of uppercase English letters.
fromi != toi
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Unwinding from dead ends
Fly on the smallest ticket until you are stuck. That airport has to be the end of the trip, so write it at the back, retreat, and let any unused tickets become side trips in front of it.

| In the story | In the solution |
|---|---|
| ticket stub | heap entry |
| write stuck | postorder |

### Pattern signal
**Eulerian path.** Using every edge once is an Eulerian path, and recording airports when stuck needs no guessing.

### Thought process
1. **First idea:** Backtrack over ticket orders.
2. **The insight:** Using every edge once is an Eulerian path, and recording airports when stuck needs no guessing.

### Optimal approach
1. Heap destinations per airport.
2. Follow and consume the smallest.
3. When none remain prepend the airport.

### Dry run
```text
JFK to KUL, NRT. NRT to JFK
finish  itinerary
KUL     KUL
JFK     JFK,KUL
NRT     NRT,JFK,KUL
JFK     JFK,NRT,JFK,KUL
```

### Why it works
An airport with no tickets left closes the part still to be printed. Side trips get inserted before it and smallest-first keeps the result lexical.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> from = new HashMap<>();
        for (List<String> t : tickets) from.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).add(t.get(1));
        LinkedList<String> route = new LinkedList<>();
        fly("JFK", from, route);
        return route;
    }

    private void fly(String at, Map<String, PriorityQueue<String>> from, LinkedList<String> route) {
        PriorityQueue<String> options = from.get(at);
        while (options != null && !options.isEmpty()) fly(options.poll(), from, route);
        route.addFirst(at);
    }
}
```

### Code walkthrough
- `options.poll()`: Consume this ticket.
- `while`: Return here for side trips.
- `route.addFirst(at)`: Written only when stuck.

### Complexity
- **Time:** O(E log E)
- **Space:** O(E)

### Common mistakes
1. **Recording on arrival fails at dead ends.** Fix: record when stuck.
2. **A set loses duplicate tickets.** Fix: use a heap.
3. **Marking airports visited is wrong.** Fix: consume tickets.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `JFK A` | `JFK,A` |
| Loop | `JFK A, A JFK` | `JFK,A,JFK` |
| Copies | `two round trips` | `all four used` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[215 — 210. Course Schedule II]]: Nodes. The key idea there: Counting unmet prerequisites shows the moment each course becomes ready.
- [[204 — 133. Clone Graph]]: Nodes once. The key idea there: One old-to-new map serves both as the visited set and as the translation of every edge.

### In a real interview
Say it in this order:

1. **Brute force:** Backtrack over ticket orders.
2. **Insight:** Using every edge once is an Eulerian path, and recording airports when stuck needs no guessing.
3. **Approach in a few words:** Hierholzer with heaps.
4. **Cost:** time O(E log E); space O(E).

## 5. Recall
Q: 332. Reconstruct Itinerary: which pattern, and what is the insight?
A: Eulerian path. Using every edge once is an Eulerian path, and recording airports when stuck needs no guessing.

Q: 332. Reconstruct Itinerary: what is the analogy, and what does it stand for?
A: Unwinding from dead ends: ticket stub → heap entry; write stuck → postorder.

Q: 332. Reconstruct Itinerary: When is an airport added?
A: After all its outgoing tickets are used.

Q: 332. Reconstruct Itinerary: what are the time and space costs?
A: Time O(E log E). Space O(E).

@@ 3 | TOPIC | 228 — 778. Swim in Rising Water | Hard · Minimum bottleneck | MUST
# 228 — 778. Swim in Rising Water

**Problem 228 – Topic: Graphs (BFS, DFS, Topological Sort) | Difficulty: Hard | Pattern: Minimum bottleneck**

[Open on LeetCode](https://leetcode.com/problems/swim-in-rising-water/)

## 1. Problem Statement
`grid[r][c]` is an elevation. At time t the water depth is t everywhere and you may swim instantly between four-adjacent cells whose elevations are both at most t. Return the least time to reach the bottom-right from the top-left.

### Examples
```text
Input:  grid = [[0,2],[1,3]]
Output: 3

Input:  grid = [[0,1,2,3,4],[24,23,22,21,5],[12,13,14,15,16],[11,17,18,19,20],[10,9,8,7,6]]
Output: 16
```

### Constraints
```text
n == grid.length
n == grid[i].length
1 <= n <= 50
0 <= grid[i][j] < n^2
Each value grid[i][j] is unique.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Rising flood choosing the lowest wall
Rising water always spills over the lowest wall on the flooded border. Keep that border in a heap. The highest wall you had to cross when the far corner floods is the time.

| In the story | In the solution |
|---|---|
| border | heap |
| highest | time |

### Pattern signal
**Minimum bottleneck.** A path costs its highest cell, and Dijkstra works for that cost.

### Thought process
1. **First idea:** Run a connectivity search for every possible time.
2. **The insight:** A path costs its highest cell, and Dijkstra works for that cost.

### Optimal approach
1. Push the start.
2. Take the lowest border cell.
3. Update the time with the maximum.
4. Stop at the corner.
5. Push unseen neighbors.

### Dry run
```text
grid = [ [0,2],[1,3] ]
remove     time  border afterwards
0 at (0,0)  0    1, 2
1 at (1,0)  1    2, 3
2 at (0,1)  2    3
3 at (1,1)  3    target reached, return 3
```

### Why it works
Any route onward must cross the current border, whose minimum is therefore unavoidable, so taking it never overestimates.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int swimInWater(int[][] grid) {
        int n = grid.length;
        boolean[][] seen = new boolean[n][n];
        seen[0][0] = true;
        PriorityQueue<int[]> border = new PriorityQueue<>((a, b) -> grid[a[0]][a[1]] - grid[b[0]][b[1]]);
        border.add(new int[] {0, 0});
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int time = 0;
        while (!border.isEmpty()) {
            int[] cell = border.poll();
            time = Math.max(time, grid[cell[0]][cell[1]]);
            if (cell[0] == n - 1 && cell[1] == n - 1) return time;
            for (int[] s : steps) {
                int r = cell[0] + s[0], c = cell[1] + s[1];
                if (r < 0 || c < 0 || r == n || c == n || seen[r][c]) continue;
                seen[r][c] = true;
                border.add(new int[] {r, c});
            }
        }
        return time;
    }
}
```

### Code walkthrough
- `grid[a[0]][a[1]] -`: The lowest wall comes first.
- `Math.max(time`: The highest wall crossed so far.
- `return time`: Only when the corner is removed.

### Complexity
- **Time:** O(n² log n)
- **Space:** O(n²)

### Common mistakes
1. **Summing elevations is the wrong cost.** Fix: track the maximum.
2. **Plain BFS ignores elevation.** Fix: expand the lowest cell.
3. **Returning when pushed can hide a higher wall.** Fix: return when removed.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[ [0] ]` | `0` |
| Two | `[ [0,2],[1,3] ]` | `3` |
| High start | `[ [3,2],[1,0] ]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **1631. Effort**: Differences.
- [[223 — 743. Network Delay Time]]: Sums. The key idea there: A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.

### In a real interview
Say it in this order:

1. **Brute force:** Run a connectivity search for every possible time.
2. **Insight:** A path costs its highest cell, and Dijkstra works for that cost.
3. **Approach in a few words:** Minimax Dijkstra.
4. **Cost:** time O(n² log n); space O(n²).

## 5. Recall
Q: 778. Swim in Rising Water: which pattern, and what is the insight?
A: Minimum bottleneck. A path costs its highest cell, and Dijkstra works for that cost.

Q: 778. Swim in Rising Water: what is the analogy, and what does it stand for?
A: Rising flood choosing the lowest wall: border → heap; highest → time.

Q: 778. Swim in Rising Water: What is a swim path's cost?
A: Its maximum elevation.

Q: 778. Swim in Rising Water: what are the time and space costs?
A: Time O(n² log n). Space O(n²).

@@ 3 | TOPIC | Practice — Review 38 | Re-solve without looking | MUST
# Review 38

Solve on a blank page. No notes, hints or solutions.

## 778. Swim in Rising Water
Find the earliest time to swim across rising water.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/swim-in-rising-water/)

## 1553. Minimum Number of Days to Eat N Oranges
Find minimum days to eat n oranges.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/minimum-number-of-days-to-eat-n-oranges/)

## 207. Course Schedule
Decide whether all courses can be finished.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/course-schedule/)

## 90. Subsets II
List unique subsets of values with duplicates.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/subsets-ii/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 778 | | | | |
| 1553 | | | | |
| 207 | | | | |
| 90 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[228 — 778. Swim in Rising Water]]
- [[225 — 1553. Minimum Number of Days to Eat N Oranges]]
- [[214 — 207. Course Schedule]]
- [[190 — 90. Subsets II]]

@@ 3 | TOPIC | Practice — Pattern summary: Graphs (BFS, DFS, Topological Sort) | Key patterns of the topic | MUST
# Pattern summary: Graphs (BFS, DFS, Topological Sort)

Name the nodes and edges first. Use DFS for connectivity, BFS for fewest moves, topological sort for dependencies, Union-Find for merging and Dijkstra or Bellman-Ford for weights.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Edge counting DFS | Compute an island's perimeter. (463) | Counting shared edges |
| Custom comparison | Check words are sorted in an alien alphabet. (953) | A longer word before its prefix |
| Flood fill | Recolor a connected region. (733) | Endless recursion on equal colors |
| Component count | Count islands in a grid. (200) | Including diagonals |
| Clone map | Deep-copy a connected graph. (133) | An endless cycle |
| Component size | Find the largest island area. (695) | Counting a cell twice |
| Component condition | Count islands of grid 2 fully covered by grid 1. (1905) | Short-circuiting the traversal |
| Reverse reachability | Find cells draining to both oceans. (417) | Wrong inequality when reversed |
| Border flood fill | Capture regions surrounded by X. (130) | Capturing border regions |
| Directed tree DFS | Count road reversals so all cities reach 0. (1466) | Walking only original directions |
| Multi-source BFS | Find minutes until all oranges rot. (994) | No fresh oranges means 0 |
| State BFS | Find minimum rolls on a Snakes and Ladders board. (909, 752) | Chaining jumps |
| Cycle-state DFS | Find nodes whose every path ends at a terminal. (802) | Returning unsorted |
| Cycle detection | Decide whether all courses can be finished. (207) | A plain visited set is not enough in DFS |
| Topological order | Return a valid course order. (210) | Reversed edge direction |
| DFS then BFS | Find the shortest bridge between two islands. (934) | BFS from only one cell |
| Eight-way BFS | Find the shortest clear path across a binary grid. (1091) | A blocked start or end |
| Union-Find | Find the edge that makes a tree cyclic. (684, 721) | Nodes are 1-indexed |
| Two-coloring | Decide whether a graph is bipartite. (785) | Checking one component |
| Weighted graph | Evaluate divisions from known ratios. (399) | Unknown variable divided by itself is -1 |
| Prim MST | Connect all points at minimum Manhattan cost. (1584) | Adding a visited point's cost |
| Bellman-Ford | Find time for a signal to reach all nodes. (743) | An unreachable node means -1 |
| Bounded Bellman-Ford | Find the cheapest flight with at most k stops. (787) | Chaining flights in one round |
| Shrink with memo | Find minimum days to eat n oranges. (1553) | Trying eat-one at every state |
| Word BFS | Find the shortest one-letter transformation chain. (127) | Counting edges instead of words |
| Eulerian path | Use every ticket once in lexical-smallest order from JFK. (332) | Greedy forward hits dead ends |
| Minimum bottleneck | Find the earliest time to swim across rising water. (778) | Summing elevations |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| islands, region | spilled paint | flood fill | 733, 200, 695, 1905 |
| reaches the border | pour inward from the edge | reverse search | 417, 130 |
| minutes, fewest steps | a rumour | BFS | 994, 909, 752, 1091, 127 |
| prerequisites, cycle | getting dressed | three states or indegrees | 207, 210, 802 |
| merge groups | clubs and presidents | Union-Find | 684, 721 |
| two teams | alternate shirts | two-coloring | 785 |
| connect all cheaply | grow one power grid | Prim | 1584 |
| at most k edges | news spreading k rounds | Bellman-Ford | 787 |
| use every edge | unwind when stuck | Hierholzer | 332 |

## Which algorithm do I pick?

1. **All moves cost one?** BFS for shortest, DFS for reachability.
2. **Several sources?** Queue them all at the start.
3. **Different positive costs?** Dijkstra. **An edge limit?** Bellman-Ford rounds.
4. **Dependencies?** Topological sort.
5. **Always:** say what a node is, and mark it when you queue it.

Q: Which signals point to Graphs (BFS, DFS, Topological Sort)?
A: grid; connected; fewest moves; dependencies; network.

Q: What is the core idea of Graphs (BFS, DFS, Topological Sort)?
A: Name the nodes and edges first. Use DFS for connectivity, BFS for fewest moves, topological sort for dependencies, Union-Find for merging and Dijkstra or Bellman-Ford for weights.
