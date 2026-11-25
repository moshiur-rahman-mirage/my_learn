@@ 1 | MODULE | Phase 10 — Backtracking | all combinations; all arrangements; small n; constraints | MUST
# Phase 10: Backtracking

Draw a decision tree. At each node choose, explore, then undo exactly what you chose. Cut a branch as soon as it cannot succeed.

**Signals:** all combinations; all arrangements; small n; constraints.

## The big analogy: exploring a maze with chalk

At a fork choose a corridor and chalk it. Explore. At a dead end **walk back and wipe exactly your own chalk**, then try the next corridor. Choose, explore, undo. The skill is refusing corridors early: those that can't work, and those that mirror one already tried.

| Question at the fork | Picture | Meet it in |
|---|---|---|
| Take it or not? | packing a bag | 78, 90 |
| Which next, never looking back? | picking a team | 77, 39, 40 |
| Who takes this seat? | seating a row | 46, 47 |
| Where is the cut? | cutting ribbon | 131 |
| Which container? | loading vans | 473, 698 |
| Is this square legal? | placing rivals | 51, 79 |

## Template
```java
void explore(int start) {
    result.add(new ArrayList<>(path));
    for (int i = start; i < a.length; i++) {
        path.add(a[i]);              // choose
        explore(i + 1);              // explore
        path.remove(path.size() - 1); // undo
    }
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Backtracking — Easy | 1 problems | MUST
# Backtracking: Easy

Patterns in this group: Path and undo.

@@ 3 | TOPIC | 185 — 257. Binary Tree Paths | Easy · Path and undo | NICE
# 185 — 257. Binary Tree Paths

**Problem 185 – Topic: Backtracking | Difficulty: Easy | Pattern: Path and undo**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-paths/)

## 1. Problem Statement
You get the root of a binary tree. Return every path from the root down to a leaf, in any order, written as values joined by `->`.

### Examples
```text
Input:  root = [1,2,3,null,5]
Output: ["1->2->5","1->3"]

Input:  root = [1]
Output: ["1"]
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 100].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A trail of breadcrumbs
You explore branches dropping breadcrumbs. At a leaf you photograph the trail. Walking back you pick up exactly the crumbs you dropped at that node, so the next branch starts with a clean trail.

| In the story | In the solution |
|---|---|
| crumbs | builder |
| pick up | setLength |

### Pattern signal
**Path and undo.** Sibling paths share everything above them, so one builder with an undo step is enough.

### Thought process
1. **First idea:** Copy the path string at every node, which wastes work.
2. **The insight:** Sibling paths share everything above them, so one builder with an undo step is enough.

### Optimal approach
1. Note the builder's length.
2. Append the value.
3. Save a copy at a leaf.
4. Explore both children.
5. Restore the length.

### Dry run
```text
1 over 2,3. 2 has right 5
leaf  path
5     1->2->5
3     1->3
```

### Why it works
On entering a node the builder holds exactly its ancestors, and it is restored on leaving, so every saved string is one root-to-leaf path.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<String> binaryTreePaths(TreeNode root) {
        List<String> result = new ArrayList<>();
        walk(root, new StringBuilder(), result);
        return result;
    }

    private void walk(TreeNode node, StringBuilder path, List<String> result) {
        if (node == null) return;
        int mark = path.length();
        path.append(node.val);
        if (node.left == null && node.right == null) {
            result.add(path.toString());
        } else {
            path.append("->");
            walk(node.left, path, result);
            walk(node.right, path, result);
        }
        path.setLength(mark);
    }
}
```

### Code walkthrough
- `int mark`: Remember where your crumbs begin.
- `path.toString()`: Save a snapshot, not the changing builder.
- `path.setLength(mark)`: Pick up your crumbs, however many characters.

### Complexity
- **Time:** O(n·h)
- **Space:** O(h) besides the output

### Common mistakes
1. **Values have several characters.** Fix: restore the saved length.
2. **Saving at null duplicates paths.** Fix: save at leaves.
3. **Without undo branches leak.** Fix: restore on exit.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `["1"]` |
| Negative | `[-1,2]` | `["-1->2"]` |
| Two | `[1,2,3]` | `two paths` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[159 — 129. Sum Root to Leaf Numbers]]: An int. The key idea there: The whole prefix of a path is one integer.
- **113. Path Sum II**: A filter.

### In a real interview
Say it in this order:

1. **Brute force:** Copy the path string at every node, which wastes work.
2. **Insight:** Sibling paths share everything above them, so one builder with an undo step is enough.
3. **Approach in a few words:** Append, explore, restore.
4. **Cost:** time O(n·h); space O(h) besides the output.

## 5. Recall
Q: 257. Binary Tree Paths: which pattern, and what is the insight?
A: Path and undo. Sibling paths share everything above them, so one builder with an undo step is enough.

Q: 257. Binary Tree Paths: what is the analogy, and what does it stand for?
A: A trail of breadcrumbs: crumbs → builder; pick up → setLength.

Q: 257. Binary Tree Paths: How is the path restored?
A: To the length saved on entry.

Q: 257. Binary Tree Paths: what are the time and space costs?
A: Time O(n·h). Space O(h) besides the output.

@@ 2 | MODULE | Backtracking — Medium | 12 problems | MUST
# Backtracking: Medium

Patterns in this group: Include or exclude, Choice with reuse, Start index, Use each once, Skip duplicates, One use, unique, Unique permutations, Grid backtracking, Cut positions, Cartesian product, Equal buckets.

@@ 3 | TOPIC | 186 — 78. Subsets | Medium · Include or exclude | MUST
# 186 — 78. Subsets

**Problem 186 – Topic: Backtracking | Difficulty: Medium | Pattern: Include or exclude**

[Open on LeetCode](https://leetcode.com/problems/subsets/)

## 1. Problem Statement
You get an array of unique integers. Return all possible subsets, including the empty one, with no duplicate subsets and in any order.

### Examples
```text
Input:  nums = [1,2,3]
Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]

Input:  nums = [0]
Output: [[],[0]]
```

### Constraints
```text
1 <= nums.length <= 10
-10 <= nums[i] <= 10
All the numbers of nums are unique.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Packing a bag with yes or no
You pack a bag with every item passing in front of you once. To each you answer yes or no. Each complete list of answers is a different bag, and undoing a yes lets you try the no.

| In the story | In the solution |
|---|---|
| yes | add |
| undo | remove last |

### Pattern signal
**Include or exclude.** A subset is exactly one yes-or-no decision per element.

### Thought process
1. **First idea:** Count in binary from 0 to 2^n and build each subset, which also works.
2. **The insight:** A subset is exactly one yes-or-no decision per element.

### Optimal approach
1. Save a copy when all items are decided.
2. Explore without the item.
3. Add it.
4. Explore with it.
5. Remove it.

### Dry run
```text
1,2
choices   subset
no, no    []
no, yes   [2]
yes, no   [1]
yes, yes  [1,2]
```

### Why it works
Every subset matches exactly one sequence of n decisions, and the recursion visits each sequence once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        decide(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void decide(int[] nums, int i, List<Integer> bag, List<List<Integer>> result) {
        if (i == nums.length) {
            result.add(new ArrayList<>(bag));
            return;
        }
        decide(nums, i + 1, bag, result); // no
        bag.add(nums[i]);
        decide(nums, i + 1, bag, result); // yes
        bag.remove(bag.size() - 1);
    }
}
```

### Code walkthrough
- `new ArrayList<>(bag)`: A snapshot of the bag.
- `bag.add(nums[i])`: The yes branch.
- `bag.remove(bag.size() - 1)`: Unpack before returning to the previous decision.

### Complexity
- **Time:** O(n·2^n)
- **Space:** O(n) besides the output

### Common mistakes
1. **The live list changes later.** Fix: save a copy.
2. **The empty set is valid.** Fix: save it too.
3. **Not advancing never ends.** Fix: recurse on the next index.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `[ [],[1] ]` |
| Two | `[1,2]` | `4 subsets` |
| Negative | `[-1]` | `[ [],[-1] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[190 — 90. Subsets II]]: Skip twins. The key idea there: After sorting, duplicates come only from equal choices at the same depth, so skip just those.
- [[187 — 39. Combination Sum]]: A budget. The key idea there: Choosing with a non-decreasing index gives each combination exactly one order.

### In a real interview
Say it in this order:

1. **Brute force:** Count in binary from 0 to 2^n and build each subset, which also works.
2. **Insight:** A subset is exactly one yes-or-no decision per element.
3. **Approach in a few words:** A binary decision tree.
4. **Cost:** time O(n·2^n); space O(n) besides the output.

## 5. Recall
Q: 78. Subsets: which pattern, and what is the insight?
A: Include or exclude. A subset is exactly one yes-or-no decision per element.

Q: 78. Subsets: what is the analogy, and what does it stand for?
A: Packing a bag with yes or no: yes → add; undo → remove last.

Q: 78. Subsets: How many subsets have n unique values?
A: 2^n.

Q: 78. Subsets: what are the time and space costs?
A: Time O(n·2^n). Space O(n) besides the output.

@@ 3 | TOPIC | Practice — Review 31 | Re-solve without looking | MUST
# Review 31

Solve on a blank page. No notes, hints or solutions.

## 78. Subsets
List all subsets.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/subsets/)

## 295. Find Median from Data Stream
Maintain a stream's median.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-median-from-data-stream/)

## 703. Kth Largest Element in a Stream
Return the kth largest after each stream insert.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/kth-largest-element-in-a-stream/)

## 450. Delete Node in a BST
Delete a key from a BST.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/delete-node-in-a-bst/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 78 | | | | |
| 295 | | | | |
| 703 | | | | |
| 450 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[186 — 78. Subsets]]
- [[183 — 295. Find Median from Data Stream]]
- [[172 — 703. Kth Largest Element in a Stream]]
- [[148 — 450. Delete Node in a BST]]

@@ 3 | TOPIC | 187 — 39. Combination Sum | Medium · Choice with reuse | MUST
# 187 — 39. Combination Sum

**Problem 187 – Topic: Backtracking | Difficulty: Medium | Pattern: Choice with reuse**

[Open on LeetCode](https://leetcode.com/problems/combination-sum/)

## 1. Problem Statement
You get distinct positive `candidates` and a `target`. Return all unique combinations that sum to the target. Each candidate may be used any number of times, and combinations differ only by how often each value occurs.

### Examples
```text
Input:  candidates = [2,3,6,7], target = 7
Output: [[2,2,3],[7]]

Input:  candidates = [2,3,5], target = 8
Output: [[2,2,2,2],[2,3,3],[3,5]]

Input:  candidates = [2], target = 1
Output: []
```

### Constraints
```text
1 <= candidates.length <= 30
2 <= candidates[i] <= 40
All elements of candidates are distinct.
1 <= target <= 40
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Paying with unlimited coins
You pay a bill with unlimited coins, pointing at one kind. Either pay one and keep pointing, or move on and never come back. Not going back is what stops 2,3 and 3,2 both appearing.

| In the story | In the solution |
|---|---|
| pointing | index |
| bill | remaining |

### Pattern signal
**Choice with reuse.** Choosing with a non-decreasing index gives each combination exactly one order.

### Thought process
1. **First idea:** Generate every sequence and remove reordered duplicates.
2. **The insight:** Choosing with a non-decreasing index gives each combination exactly one order.

### Optimal approach
1. Save at remainder zero.
2. Stop when negative or out of kinds.
3. Take the coin and stay.
4. Undo.
5. Move to the next kind.

### Dry run
```text
2,3,6,7 target 7
path   left  action
2,2,2  1     fail
2,2,3  0     save
7      0     save
```

### Why it works
Each valid set of counts corresponds to one path of "take" and "move on", and positive coins mean the remainder must shrink.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        pay(candidates, 0, target, new ArrayList<>(), result);
        return result;
    }

    private void pay(int[] coins, int i, int left, List<Integer> path, List<List<Integer>> result) {
        if (left == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        if (left < 0 || i == coins.length) return;
        path.add(coins[i]);
        pay(coins, i, left - coins[i], path, result); // same type again
        path.remove(path.size() - 1);
        pay(coins, i + 1, left, path, result);
    }
}
```

### Code walkthrough
- `pay(coins, i, left - coins[i]`: Staying on the index allows reuse.
- `pay(coins, i + 1, left`: Moving on abandons this kind for good.
- `left < 0`: Overpaying can never be repaired.

### Complexity
- **Time:** Exponential in target/min
- **Space:** O(target/min) besides the output

### Common mistakes
1. **Restarting at 0 gives permutations.** Fix: keep the index.
2. **Advancing after a take forbids reuse.** Fix: stay.
3. **Overshooting never ends.** Fix: stop below zero.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `[2], 3` | `[]` |
| Reuse | `[2], 4` | `[ [2,2] ]` |
| One | `[3], 3` | `[ [3] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[191 — 40. Combination Sum II]]: Advance. The key idea there: Sorting provides both the duplicate skip and an early stop.
- [[251 — 518. Coin Change II]]: DP. The key idea there: Fixing the order of coin types gives every combination a single representation.

### In a real interview
Say it in this order:

1. **Brute force:** Generate every sequence and remove reordered duplicates.
2. **Insight:** Choosing with a non-decreasing index gives each combination exactly one order.
3. **Approach in a few words:** Take and stay, or skip.
4. **Cost:** time Exponential in target/min; space O(target/min) besides the output.

## 5. Recall
Q: 39. Combination Sum: which pattern, and what is the insight?
A: Choice with reuse. Choosing with a non-decreasing index gives each combination exactly one order.

Q: 39. Combination Sum: what is the analogy, and what does it stand for?
A: Paying with unlimited coins: pointing → index; bill → remaining.

Q: 39. Combination Sum: What index follows taking a reusable item?
A: The same one.

Q: 39. Combination Sum: what are the time and space costs?
A: Time Exponential in target/min. Space O(target/min) besides the output.

@@ 3 | TOPIC | 188 — 77. Combinations | Medium · Start index | SHOULD
# 188 — 77. Combinations

**Problem 188 – Topic: Backtracking | Difficulty: Medium | Pattern: Start index**

[Open on LeetCode](https://leetcode.com/problems/combinations/)

## 1. Problem Statement
You get integers `n` and `k`. Return all possible combinations of k different numbers chosen from 1 to n. Order inside and between combinations does not matter.

### Examples
```text
Input:  n = 4, k = 2
Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]

Input:  n = 1, k = 1
Output: [[1]]
```

### Constraints
```text
1 <= n <= 20
1 <= k <= n
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A team from a numbered list
You pick a team from a numbered list. After choosing someone you only look below them, so a team can't be picked in two orders. Stop early when too few names remain to fill it.

| In the story | In the solution |
|---|---|
| below | start |
| too few | upper loop bound |

### Pattern signal
**Start index.** Requiring increasing choices gives each combination one unique order.

### Thought process
1. **First idea:** Generate all subsets and keep those of size k.
2. **The insight:** Requiring increasing choices gives each combination one unique order.

### Optimal approach
1. Save when none are needed.
2. Loop starts that leave enough numbers.
3. Add.
4. Recurse after it.
5. Remove.

### Dry run
```text
n4 k2
first  second
1      2,3,4
2      3,4
3      4
```

### Why it works
Every k-set has exactly one increasing order, which this loop generates, and the bound skips only starts that cannot be completed.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        pick(n, k, 1, new ArrayList<>(), result);
        return result;
    }

    private void pick(int n, int need, int start, List<Integer> team, List<List<Integer>> result) {
        if (need == 0) {
            result.add(new ArrayList<>(team));
            return;
        }
        for (int x = start; x <= n - need + 1; x++) {
            team.add(x);
            pick(n, need - 1, x + 1, team, result);
            team.remove(team.size() - 1);
        }
    }
}
```

### Code walkthrough
- `n - need + 1`: Leave enough numbers for the remaining places.
- `x + 1`: Only look below the person just picked.
- `team.remove`: Free the place for the next candidate.

### Complexity
- **Time:** O(k·C(n,k))
- **Space:** O(k) besides the output

### Common mistakes
1. **Restarting gives permutations.** Fix: start after the chosen number.
2. **start+1 is not the next after x.** Fix: pass x+1.
3. **The live list changes.** Fix: save a copy.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1,1` | `[ [1] ]` |
| All | `2,2` | `[ [1,2] ]` |
| Singles | `2,1` | `[ [1],[2] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[186 — 78. Subsets]]: No k. The key idea there: A subset is exactly one yes-or-no decision per element.
- [[187 — 39. Combination Sum]]: Reuse. The key idea there: Choosing with a non-decreasing index gives each combination exactly one order.

### In a real interview
Say it in this order:

1. **Brute force:** Generate all subsets and keep those of size k.
2. **Insight:** Requiring increasing choices gives each combination one unique order.
3. **Approach in a few words:** A start index and a remaining-slots prune.
4. **Cost:** time O(k·C(n,k)); space O(k) besides the output.

## 5. Recall
Q: 77. Combinations: which pattern, and what is the insight?
A: Start index. Requiring increasing choices gives each combination one unique order.

Q: 77. Combinations: what is the analogy, and what does it stand for?
A: A team from a numbered list: below → start; too few → upper loop bound.

Q: 77. Combinations: What prevents reordered combinations?
A: Increasing start indices.

Q: 77. Combinations: what are the time and space costs?
A: Time O(k·C(n,k)). Space O(k) besides the output.

@@ 3 | TOPIC | 189 — 46. Permutations | Medium · Use each once | MUST
# 189 — 46. Permutations

**Problem 189 – Topic: Backtracking | Difficulty: Medium | Pattern: Use each once**

[Open on LeetCode](https://leetcode.com/problems/permutations/)

## 1. Problem Statement
You get an array of distinct integers. Return all possible permutations, meaning every possible ordering of all of them, in any order.

### Examples
```text
Input:  nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]

Input:  nums = [0,1]
Output: [[0,1],[1,0]]

Input:  nums = [1]
Output: [[1]]
```

### Constraints
```text
1 <= nums.length <= 6
-10 <= nums[i] <= 10
All the integers of nums are unique.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Seating a row
You seat people in a row. For each seat try everybody still standing: sit them, fill the rest, then stand them up again so that someone else can try that seat.

| In the story | In the solution |
|---|---|
| standing | not used |
| seat | path position |

### Pattern signal
**Use each once.** A permutation is choosing one unused element for each position.

### Thought process
1. **First idea:** Generate n^n sequences and reject repeats.
2. **The insight:** A permutation is choosing one unused element for each position.

### Optimal approach
1. Save a copy when the row is full.
2. Loop the unused.
3. Mark and append.
4. Recurse.
5. Remove and unmark.

### Dry run
```text
1,2,3
first  rest
1      23, 32
2      13, 31
3      12, 21
```

### Why it works
Each ordering has exactly one choice at each position, and the used flags prevent anyone sitting twice.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        seat(nums, new boolean[nums.length], new ArrayList<>(), result);
        return result;
    }

    private void seat(int[] nums, boolean[] used, List<Integer> row, List<List<Integer>> result) {
        if (row.size() == nums.length) {
            result.add(new ArrayList<>(row));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            row.add(nums[i]);
            seat(nums, used, row, result);
            row.remove(row.size() - 1);
            used[i] = false;
        }
    }
}
```

### Code walkthrough
- `if (used[i])`: Already seated in this row.
- `used[i] = false`: Stand up for the other orders.
- `new ArrayList<>(row)`: A snapshot of the finished row.

### Complexity
- **Time:** O(n·n!)
- **Space:** O(n) besides the output

### Common mistakes
1. **A start index loses orders.** Fix: use a used array.
2. **A half undo corrupts siblings.** Fix: undo both.
3. **The live row changes.** Fix: save a copy.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `[ [1] ]` |
| Two | `[1,2]` | `2 orders` |
| Three | `[1,2,3]` | `6 orders` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[192 — 47. Permutations II]]: Skip. The key idea there: Fixing the relative order of equal copies removes the symmetry during generation.
- [[188 — 77. Combinations]]: No order. The key idea there: Requiring increasing choices gives each combination one unique order.

### In a real interview
Say it in this order:

1. **Brute force:** Generate n^n sequences and reject repeats.
2. **Insight:** A permutation is choosing one unused element for each position.
3. **Approach in a few words:** A used array per seat.
4. **Cost:** time O(n·n!); space O(n) besides the output.

## 5. Recall
Q: 46. Permutations: which pattern, and what is the insight?
A: Use each once. A permutation is choosing one unused element for each position.

Q: 46. Permutations: what is the analogy, and what does it stand for?
A: Seating a row: standing → not used; seat → path position.

Q: 46. Permutations: What must be undone in permutations?
A: The path and used flag.

Q: 46. Permutations: what are the time and space costs?
A: Time O(n·n!). Space O(n) besides the output.

@@ 3 | TOPIC | 190 — 90. Subsets II | Medium · Skip duplicates | MUST
# 190 — 90. Subsets II

**Problem 190 – Topic: Backtracking | Difficulty: Medium | Pattern: Skip duplicates**

[Open on LeetCode](https://leetcode.com/problems/subsets-ii/)

## 1. Problem Statement
You get an integer array that may contain duplicates. Return all possible subsets, the empty one included, without repeating any subset.

### Examples
```text
Input:  nums = [1,2,2]
Output: [[],[1],[1,2],[1,2,2],[2],[2,2]]

Input:  nums = [0]
Output: [[],[0]]
```

### Constraints
```text
1 <= nums.length <= 10
-10 <= nums[i] <= 10
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Identical socks
Two identical socks in a drawer: as the next thing to pack, either gives the same bag, so always take the first. But after packing it, its twin may still join one level deeper.

| In the story | In the solution |
|---|---|
| twins | equal sorted neighbors |
| same moment | one loop |

### Pattern signal
**Skip duplicates.** After sorting, duplicates come only from equal choices at the same depth, so skip just those.

### Thought process
1. **First idea:** Generate all subsets and deduplicate with a set.
2. **The insight:** After sorting, duplicates come only from equal choices at the same depth, so skip just those.

### Optimal approach
1. Sort.
2. Save every state.
3. Loop from the start.
4. Skip an equal sibling.
5. Add and recurse after it.
6. Undo.

### Dry run
```text
1,2,2
start  subsets
empty  [], [1], [2]
1      [1,2], [1,2,2]
2      [2,2]
```

### Why it works
Any subset with c copies of a value is generated only by the first c copies, so exactly once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        pack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void pack(int[] nums, int start, List<Integer> bag, List<List<Integer>> result) {
        result.add(new ArrayList<>(bag));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;
            bag.add(nums[i]);
            pack(nums, i + 1, bag, result);
            bag.remove(bag.size() - 1);
        }
    }
}
```

### Code walkthrough
- `Arrays.sort`: Puts the twins side by side.
- `i > start`: The first option at this depth is always allowed.
- `i + 1`: Deeper, the twin may join.

### Complexity
- **Time:** O(n·2^n)
- **Space:** O(n) besides the output

### Common mistakes
1. **i greater than 0 loses pairs.** Fix: use i greater than start.
2. **Unsorted twins are apart.** Fix: sort first.
3. **Subsets have every size.** Fix: save every state.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `2 subsets` |
| Twins | `[2,2]` | `3 subsets` |
| Distinct | `[1,2]` | `4 subsets` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[186 — 78. Subsets]]: No skip. The key idea there: A subset is exactly one yes-or-no decision per element.
- [[191 — 40. Combination Sum II]]: The same skip. The key idea there: Sorting provides both the duplicate skip and an early stop.

### In a real interview
Say it in this order:

1. **Brute force:** Generate all subsets and deduplicate with a set.
2. **Insight:** After sorting, duplicates come only from equal choices at the same depth, so skip just those.
3. **Approach in a few words:** Sort and skip same-depth duplicates.
4. **Cost:** time O(n·2^n); space O(n) besides the output.

## 5. Recall
Q: 90. Subsets II: which pattern, and what is the insight?
A: Skip duplicates. After sorting, duplicates come only from equal choices at the same depth, so skip just those.

Q: 90. Subsets II: what is the analogy, and what does it stand for?
A: Identical socks: twins → equal sorted neighbors; same moment → one loop.

Q: 90. Subsets II: When are duplicates skipped in subsets?
A: At the same depth after an equal sibling.

Q: 90. Subsets II: what are the time and space costs?
A: Time O(n·2^n). Space O(n) besides the output.

@@ 3 | TOPIC | 191 — 40. Combination Sum II | Medium · One use, unique | MUST
# 191 — 40. Combination Sum II

**Problem 191 – Topic: Backtracking | Difficulty: Medium | Pattern: One use, unique**

[Open on LeetCode](https://leetcode.com/problems/combination-sum-ii/)

## 1. Problem Statement
You get candidate numbers, possibly repeated, and a `target`. Return all unique combinations that sum to the target, using each array element at most once in a combination.

### Examples
```text
Input:  candidates = [10,1,2,7,6,1,5], target = 8
Output: [
        [1,1,6],
        [1,2,5],
        [1,7],
        [2,6]
        ]

Input:  candidates = [2,5,2,1,2], target = 5
Output: [
        [1,2,2],
        [5]
        ]
```

### Constraints
```text
1 <= candidates.length <= 100
1 <= candidates[i] <= 50
1 <= target <= 30
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Coins in a pocket
You pay from a pocket of actual coins, each usable once. Equal coins are interchangeable as the next choice, so try one. With coins sorted, one too large means every later one is too.

| In the story | In the solution |
|---|---|
| once | i + 1 |
| too big | break |

### Pattern signal
**One use, unique.** Sorting provides both the duplicate skip and an early stop.

### Thought process
1. **First idea:** Generate every subset and deduplicate the matches.
2. **The insight:** Sorting provides both the duplicate skip and an early stop.

### Optimal approach
1. Sort.
2. Save at zero.
3. Stop when a coin exceeds the remainder.
4. Skip equal siblings.
5. Recurse from the next.
6. Undo.

### Dry run
```text
sorted candidates = [1,1,2,5,6,7,10], target = 8
path      left  next step
1,1       6     can add 6: save [1,1,6]
1,2       5     can add 5: save [1,2,5]
1         7     can add 7: save [1,7]
second 1  -     same as its sibling at the first level, skip it
2         6     can add 6: save [2,6]
5         3     next coin 6 is too big, stop
```

### Why it works
Each multiset uses the earliest copies of its values, so appears once. Pruned branches overshoot and can never match.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        pay(candidates, 0, target, new ArrayList<>(), result);
        return result;
    }

    private void pay(int[] coins, int start, int left, List<Integer> path, List<List<Integer>> result) {
        if (left == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < coins.length && coins[i] <= left; i++) {
            if (i > start && coins[i] == coins[i - 1]) continue;
            path.add(coins[i]);
            pay(coins, i + 1, left - coins[i], path, result);
            path.remove(path.size() - 1);
        }
    }
}
```

### Code walkthrough
- `coins[i] <= left`: Sorted, so all later coins are too big too.
- `i > start`: An equal coin was already tried at this depth.
- `i + 1`: This coin is spent.

### Complexity
- **Time:** O(n·2^n)
- **Space:** O(n) besides the output

### Common mistakes
1. **Each element has one use.** Fix: recurse on i+1.
2. **i greater than 0 loses pairs.** Fix: use i greater than start.
3. **Unsorted input breaks both prunes.** Fix: sort.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Twins | `[1,1], 2` | `[ [1,1] ]` |
| Unique | `[1,1], 1` | `[ [1] ]` |
| None | `[2], 1` | `[]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[187 — 39. Combination Sum]]: Stay. The key idea there: Choosing with a non-decreasing index gives each combination exactly one order.
- [[190 — 90. Subsets II]]: No budget. The key idea there: After sorting, duplicates come only from equal choices at the same depth, so skip just those.

### In a real interview
Say it in this order:

1. **Brute force:** Generate every subset and deduplicate the matches.
2. **Insight:** Sorting provides both the duplicate skip and an early stop.
3. **Approach in a few words:** One use, skip siblings, break.
4. **Cost:** time O(n·2^n); space O(n) besides the output.

## 5. Recall
Q: 40. Combination Sum II: which pattern, and what is the insight?
A: One use, unique. Sorting provides both the duplicate skip and an early stop.

Q: 40. Combination Sum II: what is the analogy, and what does it stand for?
A: Coins in a pocket: once → i + 1; too big → break.

Q: 40. Combination Sum II: How does one-use change recursion?
A: The next start is i plus one.

Q: 40. Combination Sum II: what are the time and space costs?
A: Time O(n·2^n). Space O(n) besides the output.

@@ 3 | TOPIC | 192 — 47. Permutations II | Medium · Unique permutations | SHOULD
# 192 — 47. Permutations II

**Problem 192 – Topic: Backtracking | Difficulty: Medium | Pattern: Unique permutations**

[Open on LeetCode](https://leetcode.com/problems/permutations-ii/)

## 1. Problem Statement
You get a collection of numbers that may contain duplicates. Return every unique ordering of all of them, in any order.

### Examples
```text
Input:  nums = [1,1,2]
Output: [[1,1,2],
         [1,2,1],
         [2,1,1]]

Input:  nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```

### Constraints
```text
1 <= nums.length <= 8
-10 <= nums[i] <= 10
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Twins queue in birth order
Identical twins join a seating, and nobody can tell them apart. So make a rule: the younger may sit only after the elder is seated. That picks one labelling of each visible order.

| In the story | In the solution |
|---|---|
| elder | previous equal index |
| standing | unused |

### Pattern signal
**Unique permutations.** Fixing the relative order of equal copies removes the symmetry during generation.

### Thought process
1. **First idea:** Permute everything and deduplicate at the end.
2. **The insight:** Fixing the relative order of equal copies removes the symmetry during generation.

### Optimal approach
1. Sort.
2. Try unused values.
3. Skip one whose equal predecessor is unused.
4. Mark and recurse.
5. Undo.

### Dry run
```text
1a,1b,2
orders kept
1a 1b 2
1a 2 1b
2 1a 1b
```

### Why it works
Each distinct ordering has exactly one labelling in which equal copies appear in index order, and that one is never skipped.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        seat(nums, new boolean[nums.length], new ArrayList<>(), result);
        return result;
    }

    private void seat(int[] nums, boolean[] used, List<Integer> row, List<List<Integer>> result) {
        if (row.size() == nums.length) {
            result.add(new ArrayList<>(row));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
            used[i] = true;
            row.add(nums[i]);
            seat(nums, used, row, result);
            row.remove(row.size() - 1);
            used[i] = false;
        }
    }
}
```

### Code walkthrough
- `Arrays.sort`: Equal values become neighbors.
- `!used[i - 1]`: The elder twin is still standing, so wait.
- `used[i] = true`: Seat this person.

### Complexity
- **Time:** O(n·n!)
- **Space:** O(n) besides the output

### Common mistakes
1. **Skipping every twin leaves no full row.** Fix: skip only if the elder is unused.
2. **Unsorted twins are apart.** Fix: sort.
3. **A start index loses orders.** Fix: use a used array.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `[ [1] ]` |
| Twins | `[1,1]` | `[ [1,1] ]` |
| Mixed | `[1,1,2]` | `3 orders` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[189 — 46. Permutations]]: No rule. The key idea there: A permutation is choosing one unused element for each position.
- [[190 — 90. Subsets II]]: Sibling rule. The key idea there: After sorting, duplicates come only from equal choices at the same depth, so skip just those.

### In a real interview
Say it in this order:

1. **Brute force:** Permute everything and deduplicate at the end.
2. **Insight:** Fixing the relative order of equal copies removes the symmetry during generation.
3. **Approach in a few words:** Order equal copies to remove symmetry.
4. **Cost:** time O(n·n!); space O(n) besides the output.

## 5. Recall
Q: 47. Permutations II: which pattern, and what is the insight?
A: Unique permutations. Fixing the relative order of equal copies removes the symmetry during generation.

Q: 47. Permutations II: what is the analogy, and what does it stand for?
A: Twins queue in birth order: elder → previous equal index; standing → unused.

Q: 47. Permutations II: When may a duplicate be seated?
A: After its earlier equal copy.

Q: 47. Permutations II: what are the time and space costs?
A: Time O(n·n!). Space O(n) besides the output.

@@ 3 | TOPIC | Practice — Review 32 | Re-solve without looking | MUST
# Review 32

Solve on a blank page. No notes, hints or solutions.

## 47. Permutations II
List unique permutations with duplicates.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/permutations-ii/)

## 46. Permutations
List all permutations.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/permutations/)

## 355. Design Twitter
Design posting, following and a ten-item news feed.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/design-twitter/)

## 1448. Count Good Nodes in Binary Tree
Count nodes with no larger ancestor.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/count-good-nodes-in-binary-tree/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 47 | | | | |
| 46 | | | | |
| 355 | | | | |
| 1448 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[192 — 47. Permutations II]]
- [[189 — 46. Permutations]]
- [[178 — 355. Design Twitter]]
- [[154 — 1448. Count Good Nodes in Binary Tree]]

@@ 3 | TOPIC | 193 — 79. Word Search | Medium · Grid backtracking | MUST
# 193 — 79. Word Search

**Problem 193 – Topic: Backtracking | Difficulty: Medium | Pattern: Grid backtracking**

[Open on LeetCode](https://leetcode.com/problems/word-search/)

## 1. Problem Statement
You get a grid of letters and a `word`. Return true if the word can be spelled by moving between horizontally or vertically adjacent cells, using each cell at most once.

### Examples
```text
Input:  board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
Output: true

Input:  board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "SEE"
Output: true

Input:  board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCB"
Output: false
```

### Constraints
```text
m == board.length
n = board[i].length
1 <= m, n <= 6
1 <= word.length <= 15
board and word consists of only lowercase and uppercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Chalk in a maze
You search a maze with chalk, writing on your current cell so you can't loop into it. When a direction fails you wipe the chalk, because a different route may need that cell.

| In the story | In the solution |
|---|---|
| chalk | # |
| wipe | restore |

### Pattern signal
**Grid backtracking.** Continue only while the path spells a prefix, marking only the current path.

### Thought process
1. **First idea:** Generate every path of the word's length.
2. **The insight:** Continue only while the path spells a prefix, marking only the current path.

### Optimal approach
1. Start at every cell.
2. Reject a wrong or marked one.
3. Succeed on the last letter.
4. Mark.
5. Try four neighbors.
6. Restore.

### Dry run
```text
board row 0 = A B C E,  row 1 = S F C S,  row 2 = A D E E,  word = ABCCED
need  cell   what happens
A     (0,0)  matches, chalk it
B     (0,1)  right of A, chalk it
C     (0,2)  right of B, chalk it
C     (1,2)  below, chalk it
E     (2,2)  below, chalk it
D     (2,1)  left of E, last letter, return true
```

### Why it works
Every simple path that spells the prefix is tried, and the marks are exactly the cells on the path being tried.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean exist(char[][] board, String word) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (trace(board, word, r, c, 0)) return true;
            }
        }
        return false;
    }

    private boolean trace(char[][] b, String word, int r, int c, int i) {
        if (r < 0 || c < 0 || r == b.length || c == b[0].length || b[r][c] != word.charAt(i)) return false;
        if (i == word.length() - 1) return true;
        b[r][c] = '#';
        boolean found = trace(b, word, r + 1, c, i + 1) || trace(b, word, r - 1, c, i + 1)
                || trace(b, word, r, c + 1, i + 1) || trace(b, word, r, c - 1, i + 1);
        b[r][c] = word.charAt(i);
        return found;
    }
}
```

### Code walkthrough
- `b[r][c] != word.charAt(i)`: A wrong letter or a cell already on this path.
- `b[r][c] = '#'`: Chalk the cell.
- `b[r][c] = word.charAt(i)`: Wipe it for other routes.

### Complexity
- **Time:** O(m·n·4·3^(L-1))
- **Space:** O(L)

### Common mistakes
1. **Permanent marks block other routes.** Fix: restore them.
2. **Diagonals are not adjacent.** Fix: use four directions.
3. **A flood fill is not a path search.** Fix: unmark on return.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `A, "A"` | `true` |
| Reuse | `A, "AA"` | `false` |
| Case | `a, "A"` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[171 — 212. Word Search II]]: A trie. The key idea there: One path in the grid can test the prefixes of all words at once.
- [[203 — 200. Number of Islands]]: No unmark. The key idea there: Each connected component produces exactly one unclaimed cell that starts a search.

### In a real interview
Say it in this order:

1. **Brute force:** Generate every path of the word's length.
2. **Insight:** Continue only while the path spells a prefix, marking only the current path.
3. **Approach in a few words:** DFS, marking only the current path.
4. **Cost:** time O(m·n·4·3^(L-1)); space O(L).

## 5. Recall
Q: 79. Word Search: which pattern, and what is the insight?
A: Grid backtracking. Continue only while the path spells a prefix, marking only the current path.

Q: 79. Word Search: what is the analogy, and what does it stand for?
A: Chalk in a maze: chalk → #; wipe → restore.

Q: 79. Word Search: Why unmark in word search?
A: Another path may need the cell.

Q: 79. Word Search: what are the time and space costs?
A: Time O(m·n·4·3^(L-1)). Space O(L).

@@ 3 | TOPIC | 194 — 131. Palindrome Partitioning | Medium · Cut positions | MUST
# 194 — 131. Palindrome Partitioning

**Problem 194 – Topic: Backtracking | Difficulty: Medium | Pattern: Cut positions**

[Open on LeetCode](https://leetcode.com/problems/palindrome-partitioning/)

## 1. Problem Statement
You get a string `s`. Cut it into contiguous pieces so that every piece is a palindrome. Return every possible way of doing so.

### Examples
```text
Input:  s = "aab"
Output: [["a","a","b"],["aa","b"]]

Input:  s = "a"
Output: [["a"]]
```

### Constraints
```text
1 <= s.length <= 16
s contains only lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Cutting a ribbon
You cut a ribbon from the left, allowed to cut only when the piece reads the same both ways. Cut the rest the same way, then tape the piece back and try a longer first cut.

| In the story | In the solution |
|---|---|
| cut | end index |
| tape | remove piece |

### Pattern signal
**Cut positions.** An invalid first piece rules out every way of cutting what follows, so test before recursing.

### Thought process
1. **First idea:** Generate all 2^(n-1) ways to cut and validate each.
2. **The insight:** An invalid first piece rules out every way of cutting what follows, so test before recursing.

### Optimal approach
1. Save at the end of the string.
2. Try each end.
3. Skip non-palindromes.
4. Add the piece.
5. Recurse after it.
6. Remove.

### Dry run
```text
s = "aab"
start  piece tried  palindrome?  pieces so far
0      a            yes          [a]
1      a            yes          [a, a]
2      b            yes          [a, a, b]   save
1      ab           no           skip
0      aa           yes          [aa]
2      b            yes          [aa, b]     save
0      aab          no           skip
```

### Why it works
Every valid partition has one first piece, which is tried, followed by a valid partition of the rest, found recursively.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        cut(s, 0, new ArrayList<>(), result);
        return result;
    }

    private void cut(String s, int start, List<String> pieces, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(pieces));
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (!palindrome(s, start, end)) continue;
            pieces.add(s.substring(start, end + 1));
            cut(s, end + 1, pieces, result);
            pieces.remove(pieces.size() - 1);
        }
    }

    private boolean palindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) return false;
        }
        return true;
    }
}
```

### Code walkthrough
- `end < s.length()`: Try every possible cut.
- `continue`: A longer piece may still be a palindrome.
- `end + 1`: Cut the remaining ribbon.

### Complexity
- **Time:** O(n·2^n)
- **Space:** O(n) besides the output

### Common mistakes
1. **A longer piece may be a palindrome.** Fix: continue rather than break.
2. **Pieces must stay contiguous.** Fix: cut only.
3. **The end is included.** Fix: substring to end+1.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `"a"` | `[ ["a"] ]` |
| Twins | `"aa"` | `2 splits` |
| Distinct | `"ab"` | `[ ["a","b"] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[238 — 139. Word Break]]: Feasibility DP. The key idea there: Whether a suffix can be split doesn't depend on how you reached it.
- **93. IP**: Four pieces.

### In a real interview
Say it in this order:

1. **Brute force:** Generate all 2^(n-1) ways to cut and validate each.
2. **Insight:** An invalid first piece rules out every way of cutting what follows, so test before recursing.
3. **Approach in a few words:** Enumerate the next valid cut.
4. **Cost:** time O(n·2^n); space O(n) besides the output.

## 5. Recall
Q: 131. Palindrome Partitioning: which pattern, and what is the insight?
A: Cut positions. An invalid first piece rules out every way of cutting what follows, so test before recursing.

Q: 131. Palindrome Partitioning: what is the analogy, and what does it stand for?
A: Cutting a ribbon: cut → end index; tape → remove piece.

Q: 131. Palindrome Partitioning: What is a choice in palindrome partitioning?
A: The end of the next piece.

Q: 131. Palindrome Partitioning: what are the time and space costs?
A: Time O(n·2^n). Space O(n) besides the output.

@@ 3 | TOPIC | 195 — 17. Letter Combinations of a Phone Number | Medium · Cartesian product | MUST
# 195 — 17. Letter Combinations of a Phone Number

**Problem 195 – Topic: Backtracking | Difficulty: Medium | Pattern: Cartesian product**

[Open on LeetCode](https://leetcode.com/problems/letter-combinations-of-a-phone-number/)

## 1. Problem Statement
You get a string of digits 2 to 9. Using a phone keypad's letters, return every letter string the digits could represent, in any order, or an empty list for empty input.

### Examples
```text
Input:  digits = "23"
Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]

Input:  digits = "2"
Output: ["a","b","c"]
```

### Constraints
```text
1 <= digits.length <= 4
digits[i] is a digit in the range ['2', '9'].
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An outfit from several drawers
You dress from several drawers, one per digit. Take an item from the first and combine it with every choice from the rest, then swap it for the next item.

| In the story | In the solution |
|---|---|
| drawer | a digit's letters |
| outfit | a string |

### Pattern signal
**Cartesian product.** Recursion is simply a variable number of nested loops.

### Thought process
1. **First idea:** Nested loops, which can't handle a variable number of digits.
2. **The insight:** Recursion is simply a variable number of nested loops.

### Optimal approach
1. Return empty for no digits.
2. Save at full length.
3. Loop this digit's letters.
4. Append.
5. Recurse.
6. Remove.

### Dry run
```text
23
first  strings
a      ad ae af
b      bd be bf
c      cd ce cf
```

### Why it works
Each output string is one independent choice per position and every such choice is visited exactly once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private static final String[] KEYS = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (!digits.isEmpty()) choose(digits, new StringBuilder(), result);
        return result;
    }

    private void choose(String digits, StringBuilder path, List<String> result) {
        if (path.length() == digits.length()) {
            result.add(path.toString());
            return;
        }
        for (char c : KEYS[digits.charAt(path.length()) - '0'].toCharArray()) {
            path.append(c);
            choose(digits, path, result);
            path.setLength(path.length() - 1);
        }
    }
}
```

### Code walkthrough
- `KEYS`: The letters in each drawer.
- `!digits.isEmpty()`: No digits means no combinations.
- `- '0'`: Convert the digit to the drawer's index.

### Complexity
- **Time:** O(n·4^n)
- **Space:** O(n) besides the output

### Common mistakes
1. **Empty input has no combinations.** Fix: return an empty list.
2. **7 and 9 have four letters.** Fix: use a table.
3. **Without undo the path grows.** Fix: remove the last letter.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `""` | `[]` |
| One | `"2"` | `3 strings` |
| Four | `"7"` | `4 strings` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[076 — 22. Generate Parentheses]]: A rule. The key idea there: Validity of a prefix depends on two counts, so invalid branches are never started.
- [[186 — 78. Subsets]]: Two per drawer. The key idea there: A subset is exactly one yes-or-no decision per element.

### In a real interview
Say it in this order:

1. **Brute force:** Nested loops, which can't handle a variable number of digits.
2. **Insight:** Recursion is simply a variable number of nested loops.
3. **Approach in a few words:** A depth-first Cartesian product.
4. **Cost:** time O(n·4^n); space O(n) besides the output.

## 5. Recall
Q: 17. Letter Combinations of a Phone Number: which pattern, and what is the insight?
A: Cartesian product. Recursion is simply a variable number of nested loops.

Q: 17. Letter Combinations of a Phone Number: what is the analogy, and what does it stand for?
A: An outfit from several drawers: drawer → a digit's letters; outfit → a string.

Q: 17. Letter Combinations of a Phone Number: What does recursion depth represent here?
A: The digit being translated.

Q: 17. Letter Combinations of a Phone Number: what are the time and space costs?
A: Time O(n·4^n). Space O(n) besides the output.

@@ 3 | TOPIC | 196 — 473. Matchsticks to Square | Medium · Equal buckets | SHOULD
# 196 — 473. Matchsticks to Square

**Problem 196 – Topic: Backtracking | Difficulty: Medium | Pattern: Equal buckets**

[Open on LeetCode](https://leetcode.com/problems/matchsticks-to-square/)

## 1. Problem Statement
You get matchstick lengths. Use every stick exactly once, without breaking any, linking them into the four equal sides of a square. Return true if that is possible.

### Examples
```text
Input:  matchsticks = [1,1,2,2,2]
Output: true

Input:  matchsticks = [3,3,3,3,4]
Output: false
```

### Constraints
```text
1 <= matchsticks.length <= 15
1 <= matchsticks[i] <= 10^8
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Loading four identical vans
Load four identical vans to exactly a quarter of the total each. Put the biggest boxes in first, as they have the fewest options, and never try two vans that are equally full: those are the same choice.

| In the story | In the solution |
|---|---|
| vans | sides |
| same | equal sums |

### Pattern signal
**Equal buckets.** A known target, largest-first order and skipping equal sides remove nearly all branches.

### Thought process
1. **First idea:** Assign each stick to one of four sides, 4^n ways.
2. **The insight:** A known target, largest-first order and skipping equal sides remove nearly all branches.

### Optimal approach
1. Check divisibility.
2. Sort.
3. Place the largest.
4. Skip overfull and equal sides.
5. Undo on failure.

### Dry run
```text
1,1,2,2,2 target 2
stick  sides
2      2,0,0,0
2      2,2,0,0
2      2,2,2,0
1,1    2,2,2,2
```

### Why it works
Sides with equal sums are interchangeable and overfull ones are impossible, so no distinct solution is pruned.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean makesquare(int[] matchsticks) {
        long sum = 0;
        for (int x : matchsticks) sum += x;
        if (sum % 4 != 0) return false;
        Arrays.sort(matchsticks);
        return place(matchsticks, matchsticks.length - 1, new long[4], sum / 4);
    }

    private boolean place(int[] sticks, int i, long[] sides, long target) {
        if (i < 0) return true;
        for (int s = 0; s < 4; s++) {
            if (sides[s] + sticks[i] > target) continue;
            boolean same = false;
            for (int p = 0; p < s; p++) same |= sides[p] == sides[s];
            if (same) continue;
            sides[s] += sticks[i];
            if (place(sticks, i - 1, sides, target)) return true;
            sides[s] -= sticks[i];
        }
        return false;
    }
}
```

### Code walkthrough
- `matchsticks.length - 1`: The biggest box has the fewest options.
- `sides[p] == sides[s]`: A van identical to one already tried.
- `sides[s] -=`: Unload before trying another van.

### Complexity
- **Time:** O(4^n) worst
- **Space:** O(n)

### Common mistakes
1. **Divisibility is not sufficient.** Fix: search.
2. **Small sticks first branch widely.** Fix: start with the largest.
3. **The sum is large.** Fix: use long.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Square | `[1,1,1,1]` | `true` |
| Few | `[4]` | `false` |
| Indivisible | `[1,1,1,2]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[197 — 698. Partition to K Equal Sum Subsets]]: k vans. The key idea there: Equal buckets are interchangeable and large values constrain earliest, so prune both.
- [[240 — 416. Partition Equal Subset Sum]]: DP. The key idea there: If one part reaches half the total the other does automatically, so this is a subset-sum question.

### In a real interview
Say it in this order:

1. **Brute force:** Assign each stick to one of four sides, 4^n ways.
2. **Insight:** A known target, largest-first order and skipping equal sides remove nearly all branches.
3. **Approach in a few words:** Bucket backtracking with symmetry pruning.
4. **Cost:** time O(4^n) worst; space O(n).

## 5. Recall
Q: 473. Matchsticks to Square: which pattern, and what is the insight?
A: Equal buckets. A known target, largest-first order and skipping equal sides remove nearly all branches.

Q: 473. Matchsticks to Square: what is the analogy, and what does it stand for?
A: Loading four identical vans: vans → sides; same → equal sums.

Q: 473. Matchsticks to Square: Why skip equal side sums?
A: They lead to equivalent states.

Q: 473. Matchsticks to Square: what are the time and space costs?
A: Time O(4^n) worst. Space O(n).

@@ 3 | TOPIC | 197 — 698. Partition to K Equal Sum Subsets | Medium · Equal buckets | SHOULD
# 197 — 698. Partition to K Equal Sum Subsets

**Problem 197 – Topic: Backtracking | Difficulty: Medium | Pattern: Equal buckets**

[Open on LeetCode](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/)

## 1. Problem Statement
You get an integer array and `k`. Return true if it can be divided into k non-empty subsets with equal sums, using every element exactly once.

### Examples
```text
Input:  nums = [4,3,2,3,5,2,1], k = 4
Output: true

Input:  nums = [1,2,3,4], k = 3
Output: false
```

### Constraints
```text
1 <= k <= nums.length <= 16
1 <= nums[i] <= 10^4
The frequency of each element is in the range [1, 4].
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Loading k identical lorries
Load k identical lorries, biggest box first. If a box fails in an empty lorry it would fail in every other empty one as well, so you give up at once.

| In the story | In the solution |
|---|---|
| lorry | bucket |
| give up | break on empty |

### Pattern signal
**Equal buckets.** Equal buckets are interchangeable and large values constrain earliest, so prune both.

### Thought process
1. **First idea:** Assign every number to one of k buckets, k^n ways.
2. **The insight:** Equal buckets are interchangeable and large values constrain earliest, so prune both.

### Optimal approach
1. Check divisibility and the largest.
2. Sort.
3. Place from the largest.
4. Skip equal or overfull buckets.
5. Undo.
6. Stop after a failed empty bucket.

### Dry run
```text
5,4,3,3,2,2,1 target 5
after          buckets
5,4,3,3        5,4,3,3
2,2            5,4,5,5
1              5,5,5,5
```

### Why it works
Every skipped branch either overflows or is a relabelling of one explored, so if an assignment exists it is found.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean canPartitionKSubsets(int[] nums, int k) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % k != 0) return false;
        Arrays.sort(nums);
        if (nums[nums.length - 1] > sum / k) return false;
        return place(nums, nums.length - 1, new int[k], sum / k);
    }

    private boolean place(int[] nums, int i, int[] buckets, int target) {
        if (i < 0) return true;
        for (int b = 0; b < buckets.length; b++) {
            if (b > 0 && buckets[b] == buckets[b - 1]) continue;
            if (buckets[b] + nums[i] > target) continue;
            buckets[b] += nums[i];
            if (place(nums, i - 1, buckets, target)) return true;
            buckets[b] -= nums[i];
            if (buckets[b] == 0) break;
        }
        return false;
    }
}
```

### Code walkthrough
- `nums[nums.length - 1] > sum / k`: One box that can never fit.
- `buckets[b] == buckets[b - 1]`: This lorry is in the same state as the previous.
- `break`: All other empty lorries would fail too.

### Complexity
- **Time:** O(k^n) worst
- **Space:** O(n + k)

### Common mistakes
1. **Symmetric buckets repeat work.** Fix: skip equals.
2. **Greedy filling can fail.** Fix: backtrack.
3. **Subsets need not be contiguous.** Fix: assign freely.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1,2], 1` | `true` |
| Equal | `[2,2], 2` | `true` |
| Too big | `[1,3], 2` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[196 — 473. Matchsticks to Square]]: k 4. The key idea there: A known target, largest-first order and skipping equal sides remove nearly all branches.
- [[240 — 416. Partition Equal Subset Sum]]: DP. The key idea there: If one part reaches half the total the other does automatically, so this is a subset-sum question.

### In a real interview
Say it in this order:

1. **Brute force:** Assign every number to one of k buckets, k^n ways.
2. **Insight:** Equal buckets are interchangeable and large values constrain earliest, so prune both.
3. **Approach in a few words:** Descending placement with pruning, or bitmask DP.
4. **Cost:** time O(k^n) worst; space O(n + k).

## 5. Recall
Q: 698. Partition to K Equal Sum Subsets: which pattern, and what is the insight?
A: Equal buckets. Equal buckets are interchangeable and large values constrain earliest, so prune both.

Q: 698. Partition to K Equal Sum Subsets: what is the analogy, and what does it stand for?
A: Loading k identical lorries: lorry → bucket; give up → break on empty.

Q: 698. Partition to K Equal Sum Subsets: What follows a failed placement in an empty bucket?
A: Stop, other empty buckets are equivalent.

Q: 698. Partition to K Equal Sum Subsets: what are the time and space costs?
A: Time O(k^n) worst. Space O(n + k).

@@ 2 | MODULE | Backtracking — Hard | 2 problems | MUST
# Backtracking: Hard

Patterns in this group: Column and diagonals, Constraint sets.

@@ 3 | TOPIC | 198 — 51. N-Queens | Hard · Column and diagonals | MUST
# 198 — 51. N-Queens

**Problem 198 – Topic: Backtracking | Difficulty: Hard | Pattern: Column and diagonals**

[Open on LeetCode](https://leetcode.com/problems/n-queens/)

## 1. Problem Statement
Place n queens on an n by n board so that no two share a row, column or diagonal. Return every distinct board as strings, with `Q` for a queen and `.` for an empty square.

### Examples
```text
Input:  n = 4
Output: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]

Input:  n = 1
Output: [["Q"]]
```

### Constraints
```text
1 <= n <= 9
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Rival queens claiming roads
Rival queens each claim three roads: a column and two diagonals. Place one per row, only where all three are free. When you retreat, release all three.

| In the story | In the solution |
|---|---|
| roads | three arrays |
| floor | row |

### Pattern signal
**Column and diagonals.** Each diagonal has a name, r+c or r-c, so conflicts are tested in O(1).

### Thought process
1. **First idea:** Place queens in all n^n ways and check each board.
2. **The insight:** Each diagonal has a name, r+c or r-c, so conflicts are tested in O(1).

### Optimal approach
1. Go row by row.
2. Skip occupied columns and diagonals.
3. Claim and place.
4. Recurse.
5. Release.
6. Copy the board at the end.

### Dry run
```text
n = 4
row  columns tried               placed
0    0 (leads to a dead end), 1  column 1
1    0, 1, 2 attacked            column 3
2    0 is free                   column 0
3    2 is free                   column 2
board: .Q..  ...Q  Q...  ..Q.
```

### Why it works
One queen per row rules out row conflicts and the arrays rule out the rest, while every non-conflicting option is tried.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<String>> solveNQueens(int n) {
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        List<List<String>> result = new ArrayList<>();
        place(board, 0, new boolean[n], new boolean[2 * n], new boolean[2 * n], result);
        return result;
    }

    private void place(char[][] board, int r, boolean[] col, boolean[] down, boolean[] up, List<List<String>> result) {
        int n = board.length;
        if (r == n) {
            List<String> copy = new ArrayList<>();
            for (char[] row : board) copy.add(new String(row));
            result.add(copy);
            return;
        }
        for (int c = 0; c < n; c++) {
            if (col[c] || down[r - c + n] || up[r + c]) continue;
            col[c] = down[r - c + n] = up[r + c] = true;
            board[r][c] = 'Q';
            place(board, r + 1, col, down, up, result);
            board[r][c] = '.';
            col[c] = down[r - c + n] = up[r + c] = false;
        }
    }
}
```

### Code walkthrough
- `r - c + n`: One diagonal family, shifted to be non-negative.
- `up[r + c]`: The other diagonal family.
- `new String(row)`: Snapshot each row of the board.

### Complexity
- **Time:** O(n!) search plus O(n²) per board
- **Space:** O(n²)

### Common mistakes
1. **There are two diagonal families.** Fix: track both.
2. **A partial release blocks siblings.** Fix: clear all three.
3. **The live board changes.** Fix: copy its rows.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `[ ["Q"] ]` |
| Two | `2` | `[]` |
| Four | `4` | `2 boards` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[199 — 52. N-Queens II]]: No board. The key idea there: Legality depends only on the constraints, and counts of different branches add.
- [[020 — 36. Valid Sudoku]]: Registers. The key idea there: One visit can check all three rules if all three registers are kept.

### In a real interview
Say it in this order:

1. **Brute force:** Place queens in all n^n ways and check each board.
2. **Insight:** Each diagonal has a name, r+c or r-c, so conflicts are tested in O(1).
3. **Approach in a few words:** Row backtracking with O(1) constraints.
4. **Cost:** time O(n!) search plus O(n²) per board; space O(n²).

## 5. Recall
Q: 51. N-Queens: which pattern, and what is the insight?
A: Column and diagonals. Each diagonal has a name, r+c or r-c, so conflicts are tested in O(1).

Q: 51. N-Queens: what is the analogy, and what does it stand for?
A: Rival queens claiming roads: roads → three arrays; floor → row.

Q: 51. N-Queens: How are queen diagonals identified?
A: r plus c and r minus c.

Q: 51. N-Queens: what are the time and space costs?
A: Time O(n!) search plus O(n²) per board. Space O(n²).

@@ 3 | TOPIC | Practice — Review 33 | Re-solve without looking | MUST
# Review 33

Solve on a blank page. No notes, hints or solutions.

## 51. N-Queens
List all N-Queens boards.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/n-queens/)

## 17. Letter Combinations of a Phone Number
List letter combinations for phone digits.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/letter-combinations-of-a-phone-number/)

## 502. IPO
Choose up to k projects to maximize capital.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/ipo/)

## 337. House Robber III
Maximize robbery on a tree with no adjacent nodes.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/house-robber-iii/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 51 | | | | |
| 17 | | | | |
| 502 | | | | |
| 337 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[198 — 51. N-Queens]]
- [[195 — 17. Letter Combinations of a Phone Number]]
- [[184 — 502. IPO]]
- [[160 — 337. House Robber III]]

@@ 3 | TOPIC | 199 — 52. N-Queens II | Hard · Constraint sets | SHOULD
# 199 — 52. N-Queens II

**Problem 199 – Topic: Backtracking | Difficulty: Hard | Pattern: Constraint sets**

[Open on LeetCode](https://leetcode.com/problems/n-queens-ii/)

## 1. Problem Statement
Place n queens on an n by n board so that no two share a row, column or diagonal. Return the number of distinct solutions.

### Examples
```text
Input:  n = 4
Output: 2

Input:  n = 1
Output: 1
```

### Constraints
```text
1 <= n <= 9
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Counting routes without photos
It is the same claiming of roads, but instead of photographing each finished board you click a counter. The board itself is never needed, only the three claims.

| In the story | In the solution |
|---|---|
| click | return 1 |
| claims | three arrays |

### Pattern signal
**Constraint sets.** Legality depends only on the constraints, and counts of different branches add.

### Thought process
1. **First idea:** Build every board and then count them.
2. **The insight:** Legality depends only on the constraints, and counts of different branches add.

### Optimal approach
1. Return one after the last row.
2. Try free columns.
3. Claim.
4. Add the result.
5. Release.

### Dry run
```text
n = 4, count completions for each choice in row 0
row 0 column  what follows        returns
0             every branch dies   0
1             columns 3, 0, 2     1
2             columns 0, 3, 1     1
3             every branch dies   0
total = 2
```

### Why it works
Different choices in a row lead to disjoint sets of solutions, so their counts add to exactly the total.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int totalNQueens(int n) {
        return place(n, 0, new boolean[n], new boolean[2 * n], new boolean[2 * n]);
    }

    private int place(int n, int r, boolean[] col, boolean[] down, boolean[] up) {
        if (r == n) return 1;
        int total = 0;
        for (int c = 0; c < n; c++) {
            if (col[c] || down[r - c + n] || up[r + c]) continue;
            col[c] = down[r - c + n] = up[r + c] = true;
            total += place(n, r + 1, col, down, up);
            col[c] = down[r - c + n] = up[r + c] = false;
        }
        return total;
    }
}
```

### Code walkthrough
- `return 1`: One completed placement.
- `total +=`: Add up all the branches.
- `= false`: Release the three roads.

### Complexity
- **Time:** O(n!)
- **Space:** O(n)

### Common mistakes
1. **All solutions count.** Fix: do not return at the first.
2. **Boards are wasted work.** Fix: track constraints only.
3. **A static counter leaks between tests.** Fix: return sums.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `1` |
| Three | `3` | `0` |
| Five | `5` | `10` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[198 — 51. N-Queens]]: Photos. The key idea there: Each diagonal has a name, r+c or r-c, so conflicts are tested in O(1).
- [[252 — 494. Target Sum]]: Repeated states, DP. The key idea there: The index and the running sum completely determine how many ways remain.

### In a real interview
Say it in this order:

1. **Brute force:** Build every board and then count them.
2. **Insight:** Legality depends only on the constraints, and counts of different branches add.
3. **Approach in a few words:** Counting backtracking, optionally bitmasks.
4. **Cost:** time O(n!); space O(n).

## 5. Recall
Q: 52. N-Queens II: which pattern, and what is the insight?
A: Constraint sets. Legality depends only on the constraints, and counts of different branches add.

Q: 52. N-Queens II: what is the analogy, and what does it stand for?
A: Counting routes without photos: click → return 1; claims → three arrays.

Q: 52. N-Queens II: What does a complete placement return?
A: One.

Q: 52. N-Queens II: what are the time and space costs?
A: Time O(n!). Space O(n).

@@ 3 | TOPIC | Practice — Pattern summary: Backtracking | Key patterns of the topic | MUST
# Pattern summary: Backtracking

Draw a decision tree. At each node choose, explore, then undo exactly what you chose. Cut a branch as soon as it cannot succeed.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Path and undo | List all root-to-leaf paths. (257) | Not undoing |
| Include or exclude | List all subsets. (78) | Storing the mutable list |
| Choice with reuse | List combinations with target sum, reuse allowed. (39) | Returning permutations |
| Start index | List all k-size combinations of 1..n. (77) | Restarting from 1 |
| Use each once | List all permutations. (46) | Forgetting to restore |
| Skip duplicates | List unique subsets of values with duplicates. (90) | Skipping valid repeated use across depths |
| One use, unique | List unique target-sum combinations using each item once. (40) | Reusing the same index |
| Unique permutations | List unique permutations with duplicates. (47) | Deduplicating only at the end |
| Grid backtracking | Decide whether a word follows an adjacent path of unused cells. (79) | Leaving cells marked |
| Cut positions | List all palindrome partitions. (131) | Not handling the empty remainder |
| Cartesian product | List letter combinations for phone digits. (17) | Empty input returns an empty list |
| Equal buckets | Decide whether sticks form a square. (473, 698) | Skipping the divisibility check |
| Column and diagonals | List all N-Queens boards. (51) | Using one diagonal family |
| Constraint sets | Count N-Queens solutions. (52) | Not removing constraints |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| all subsets | packing a bag | include or exclude | 78, 90 |
| combinations | picking a team from a list | start index | 77, 39, 40 |
| arrangements | seats in a row | used array | 46, 47 |
| all splits | cutting ribbon | cut positions | 131 |
| equal groups | vans, biggest box first | buckets | 473, 698 |
| no conflicts | claimed lines | constraint sets | 51, 52 |
| path in a grid | chalk in a maze | mark and unmark | 79 |

## Which template do I pick?

1. **Order doesn't matter?** Use a start index.
2. **Order matters?** Use a used array.
3. **Reuse allowed?** Recurse on `i`, otherwise on `i + 1`.
4. **Duplicate values?** Sort and skip an equal sibling at the same fork.
5. **Saving an answer?** Save a copy.

Q: Which signals point to Backtracking?
A: all combinations; all arrangements; small n; constraints.

Q: What is the core idea of Backtracking?
A: Draw a decision tree. At each node choose, explore, then undo exactly what you chose. Cut a branch as soon as it cannot succeed.
