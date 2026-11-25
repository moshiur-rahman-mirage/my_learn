@@ 1 | MODULE | Phase 7 — Trees (DFS & BFS) | depth; path; ancestor; level; BST | MUST
# Phase 7: Trees (DFS & BFS)

Decide what each node needs from its parent and what it returns. Trust the call on each child. Use BFS for levels and inorder for sorted BST values.

**Signals:** depth; path; ancestor; level; BST.

## The big analogy: a company org chart

A manager who needs a fact about the whole company doesn't interview everyone. They ask each direct report "give me the answer for your department", **trust it**, combine, and report upward. That trust is recursion. Two questions solve most tree problems: what must I **tell** my children, and what must they **report** to me?

| Flow of information | Picture | Meet it in |
|---|---|---|
| Upward | reports to the boss | 104, 543, 124 |
| Downward | instructions to staff | 98, 112, 1448 |
| Two trees together | twins compared limb by limb | 100, 101, 617 |
| Floor by floor | a lift stopping at each floor | 102, 199 |
| Sorted tree | a signpost at every fork | 235, 230, 450 |

## Template
```java
int visit(TreeNode node) {
    if (node == null) return 0;
    int left = visit(node.left), right = visit(node.right);
    best = Math.max(best, left + right);  // answer through this node
    return 1 + Math.max(left, right);     // answer for the parent
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Trees — Easy | 14 problems | MUST
# Trees: Easy

Patterns in this group: Inorder DFS, Preorder DFS, Postorder DFS, Structural DFS, Bottom-up DFS, Height plus global best, Height check, Parallel DFS, Match at every node, Middle as root, Root-to-leaf state, Inorder previous, Mirror DFS.

@@ 3 | TOPIC | 131 — 94. Binary Tree Inorder Traversal | Easy · Inorder DFS | NICE
# 131 — 94. Binary Tree Inorder Traversal

**Problem 131 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Inorder DFS**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-inorder-traversal/)

## 1. Problem Statement
You get the root of a binary tree. Return its values in inorder: the left subtree, then the node, then the right subtree. Follow-up: solve it without recursion.

### Examples
```text
Input:  root = [1,null,2,3]
Output: [1,3,2]

Input:  root = [1,2,3,4,5,null,8,null,null,6,7,9]
Output: [4,2,6,5,7,1,3,9,8]

Input:  root = []
Output: []

Input:  root = [1]
Output: [1]
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 100].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Reading a family shelf left to right
You read a family shelf from left to right. Walk as far left as possible, leaving a bookmark at every fork. Return to the newest bookmark, read that book, then explore what is to its right.

| In the story | In the solution |
|---|---|
| bookmarks | a stack |
| reading | adding the value |

### Pattern signal
**Inorder DFS.** A node waits until its whole left side is finished, and the most recent waiter resumes first, which is a stack.

### Thought process
1. **First idea:** Use recursion, which hides the same bookmarks in the call stack.
2. **The insight:** A node waits until its whole left side is finished, and the most recent waiter resumes first, which is a stack.

### Optimal approach
1. Push the whole left chain.
2. Pop the newest.
3. Record it.
4. Move to its right child.
5. Repeat.

### Dry run
```text
root 1, right 2, whose left is 3
action        stack  output
push 1        1      -
pop, go right -      1
push 2, 3     2 3    1
pop 3, pop 2  -      1,3,2
```

### Why it works
A node is popped only after everything in its left subtree and before anything in its right one, which is the definition of inorder.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode node = root;
        while (node != null || !stack.isEmpty()) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
            node = stack.pop();
            result.add(node.val);
            node = node.right;
        }
        return result;
    }
}
```

### Code walkthrough
- `node != null || !stack.isEmpty()`: Work remains in hand or on a bookmark.
- `node = node.left`: Keep going to the smallest position first.
- `node = node.right`: After reading a node, its right side is next.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **The stack starts empty.** Fix: also loop while the node is non-null.
2. **Recording on push is preorder.** Fix: record on pop.
3. **Right subtrees are lost.** Fix: move right after each visit.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[1]` |
| Left only | `[1,2]` | `[2,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[156 — 230. Kth Smallest Element in a BST]]: Stop early. The key idea there: An iterative inorder walk produces values in order and can stop early.
- [[162 — 173. Binary Search Tree Iterator]]: Pause the loop. The key idea there: An inorder walk can be paused after each pop.

### In a real interview
Say this: *"An explicit stack delays each node until its left is done."*

## 5. Recall
Q: 94. Binary Tree Inorder Traversal: which pattern, and what is the insight?
A: Inorder DFS. A node waits until its whole left side is finished, and the most recent waiter resumes first, which is a stack.

Q: 94. Binary Tree Inorder Traversal: what is the analogy, and what does it stand for?
A: Reading a family shelf left to right: bookmarks → a stack; reading → adding the value.

Q: 94. Binary Tree Inorder Traversal: When is a node visited inorder?
A: After left, before right.

Q: 94. Binary Tree Inorder Traversal: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 132 — 144. Binary Tree Preorder Traversal | Easy · Preorder DFS | NICE
# 132 — 144. Binary Tree Preorder Traversal

**Problem 132 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Preorder DFS**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-preorder-traversal/)

## 1. Problem Statement
You get the root of a binary tree, possibly empty. Return its values in preorder: the node, then its left subtree, then its right subtree.

### Examples
```text
Input:  root = [1,null,2,3]
Output: [1,2,3]

Input:  root = [1,2,3,4,5,null,8,null,null,6,7,9]
Output: [1,2,4,5,6,7,3,8,9]

Input:  root = []
Output: []

Input:  root = [1]
Output: [1]
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 100].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A tour guide
A tour guide announces each room on entering it. Then they tour the whole left wing, and only after that the whole right wing, following the same rule inside each.

| In the story | In the solution |
|---|---|
| on entering | record first |
| wing | a subtree |

### Pattern signal
**Preorder DFS.** The definition is itself recursive: node, then both subtrees by the same rule.

### Thought process
1. **First idea:** Every node must be visited, so linear time is already the best possible.
2. **The insight:** The definition is itself recursive: node, then both subtrees by the same rule.

### Optimal approach
1. Return on null.
2. Record the node.
3. Traverse left.
4. Traverse right.

### Dry run
```text
root 1, children 2 and 3
call  output
1     1
2     1,2
3     1,2,3
```

### Why it works
By induction each call outputs its node and then the correct traversals of both subtrees, in order.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        visit(root, result);
        return result;
    }

    private void visit(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.val);
        visit(node.left, result);
        visit(node.right, result);
    }
}
```

### Code walkthrough
- `node == null`: An absent room has nothing to announce.
- `result.add(node.val)`: Announce on entering.
- `visit(node.left, result)`: Trust the call to tour the whole wing.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **A null child crashes.** Fix: return on null first.
2. **Recording between the children is inorder.** Fix: record first.
3. **A static list leaks between tests.** Fix: create one per call.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[1]` |
| Left | `[1,2]` | `[1,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[131 — 94. Binary Tree Inorder Traversal]]: Record between. The key idea there: A node waits until its whole left side is finished, and the most recent waiter resumes first, which is a stack.
- [[166 — 297. Serialize and Deserialize Binary Tree]]: Add nulls. The key idea there: One traversal that includes nulls determines the tree completely.

### In a real interview
Say it in this order:

1. **Brute force:** Every node must be visited, so linear time is already the best possible.
2. **Insight:** The definition is itself recursive: node, then both subtrees by the same rule.
3. **Approach in a few words:** Preorder announces before descending.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 144. Binary Tree Preorder Traversal: which pattern, and what is the insight?
A: Preorder DFS. The definition is itself recursive: node, then both subtrees by the same rule.

Q: 144. Binary Tree Preorder Traversal: what is the analogy, and what does it stand for?
A: A tour guide: on entering → record first; wing → a subtree.

Q: 144. Binary Tree Preorder Traversal: What is preorder's order?
A: Node, left, right.

Q: 144. Binary Tree Preorder Traversal: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | Practice — Review 22 | Re-solve without looking | MUST
# Review 22

Solve on a blank page. No notes, hints or solutions.

## 144. Binary Tree Preorder Traversal
Return the preorder traversal of a tree.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/binary-tree-preorder-traversal/)

## 23. Merge k Sorted Lists
Merge k sorted linked lists.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/merge-k-sorted-lists/)

## 138. Copy List with Random Pointer
Deep-copy a list with random pointers.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/copy-list-with-random-pointer/)

## 540. Single Element in a Sorted Array
Find the single value in a sorted array of pairs in O(log n).

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/single-element-in-a-sorted-array/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 144 | | | | |
| 23 | | | | |
| 138 | | | | |
| 540 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[132 — 144. Binary Tree Preorder Traversal]]
- [[129 — 23. Merge k Sorted Lists]]
- [[118 — 138. Copy List with Random Pointer]]
- [[094 — 540. Single Element in a Sorted Array]]

@@ 3 | TOPIC | 133 — 145. Binary Tree Postorder Traversal | Easy · Postorder DFS | NICE
# 133 — 145. Binary Tree Postorder Traversal

**Problem 133 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Postorder DFS**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-postorder-traversal/)

## 1. Problem Statement
You get the root of a binary tree, possibly empty. Return its values in postorder: the left subtree, then the right subtree, then the node.

### Examples
```text
Input:  root = [1,null,2,3]
Output: [3,2,1]

Input:  root = [1,2,3,4,5,null,8,null,null,6,7,9]
Output: [4,6,7,5,2,9,8,3,1]

Input:  root = []
Output: []

Input:  root = [1]
Output: [1]
```

### Constraints
```text
The number of the nodes in the tree is in the range [0, 100].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Demolishing a building safely
You demolish a building safely: nothing can be removed while something still hangs from it. So both children go before their parent, and the root is last of all.

| In the story | In the solution |
|---|---|
| hanging | children |
| remove | record |

### Pattern signal
**Postorder DFS.** A parent is processed after both of its children have finished.

### Thought process
1. **First idea:** Reverse a root-right-left walk, which works but hides the meaning.
2. **The insight:** A parent is processed after both of its children have finished.

### Optimal approach
1. Return on null.
2. Traverse left.
3. Traverse right.
4. Record the node.

### Dry run
```text
root 1, children 2 and 3
finished  output
2         2
3         2,3
1         2,3,1
```

### Why it works
Each call finishes two correct child traversals before it records itself, which is exactly postorder.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        visit(root, result);
        return result;
    }

    private void visit(TreeNode node, List<Integer> result) {
        if (node == null) return;
        visit(node.left, result);
        visit(node.right, result);
        result.add(node.val);
    }
}
```

### Code walkthrough
- `visit(node.left, result)`: Finish everything on the left first.
- `visit(node.right, result)`: Then everything on the right.
- `result.add(node.val)`: Only now is it safe to handle the parent.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Reversed preorder has the sides swapped.** Fix: visit left then right.
2. **The parent is recorded too early.** Fix: record it last.
3. **A null child crashes.** Fix: return on null.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[1]` |
| Right | `[1,null,2]` | `[2,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[135 — 104. Maximum Depth of Binary Tree]]: A value in postorder. The key idea there: A node's depth depends only on the depths of its two children.
- [[137 — 110. Balanced Binary Tree]]: A check in postorder. The key idea there: Height and validity can travel in the same report by using -1 for invalid.

### In a real interview
Say it in this order:

1. **Brute force:** Reverse a root-right-left walk, which works but hides the meaning.
2. **Insight:** A parent is processed after both of its children have finished.
3. **Approach in a few words:** Postorder suits any parent computation that needs the children.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 145. Binary Tree Postorder Traversal: which pattern, and what is the insight?
A: Postorder DFS. A parent is processed after both of its children have finished.

Q: 145. Binary Tree Postorder Traversal: what is the analogy, and what does it stand for?
A: Demolishing a building safely: hanging → children; remove → record.

Q: 145. Binary Tree Postorder Traversal: When is the parent visited in postorder?
A: After both children.

Q: 145. Binary Tree Postorder Traversal: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 134 — 226. Invert Binary Tree | Easy · Structural DFS | MUST
# 134 — 226. Invert Binary Tree

**Problem 134 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Structural DFS**

[Open on LeetCode](https://leetcode.com/problems/invert-binary-tree/)

## 1. Problem Statement
You get the root of a binary tree. Invert it, so that at every node the left and right subtrees change places, and return the root.

### Examples
```text
Input:  root = [4,2,7,1,3,6,9]
Output: [4,7,2,9,6,3,1]

Input:  root = [2,1,3]
Output: [2,3,1]

Input:  root = []
Output: []
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 100].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A hanging mobile in a mirror
A hanging mobile is seen in a mirror. You don't rebuild it: at every bar just swap the two strings, and the whole mobile is mirrored.

| In the story | In the solution |
|---|---|
| bar | node |
| strings | children |

### Pattern signal
**Structural DFS.** The mirror of a tree is its root with the mirrors of its children exchanged.

### Thought process
1. **First idea:** Build a mirrored copy with n new nodes.
2. **The insight:** The mirror of a tree is its root with the mirrors of its children exchanged.

### Optimal approach
1. Return null for null.
2. Save the left child.
3. Set left to the mirrored right.
4. Set right to the mirrored saved child.

### Dry run
```text
level order 4,2,7,1,3,6,9
node  children after
4     7,2
7     9,6
2     3,1
```

### Why it works
Assuming both recursive calls return correct mirrors, exchanging them gives exactly the mirror of this node's tree.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        TreeNode left = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(left);
        return root;
    }
}
```

### Code walkthrough
- `TreeNode left = root.left`: Save it before it is overwritten.
- `invertTree(root.right)`: Trust the call to mirror that side.
- `return root`: The same root, now mirrored.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **The first assignment loses a child.** Fix: save it.
2. **Swapping values keeps a wrong shape.** Fix: swap subtrees.
3. **Deeper levels must change too.** Fix: recurse.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[1]` |
| Left only | `[1,2]` | `[1,null,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[144 — 101. Symmetric Tree]]: Compare mirrors. The key idea there: Symmetry is tree equality with the children crossed.
- **951. Flip Equivalent**: Swaps optional.

### In a real interview
Say it in this order:

1. **Brute force:** Build a mirrored copy with n new nodes.
2. **Insight:** The mirror of a tree is its root with the mirrors of its children exchanged.
3. **Approach in a few words:** Swap recursively, saving one child.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 226. Invert Binary Tree: which pattern, and what is the insight?
A: Structural DFS. The mirror of a tree is its root with the mirrors of its children exchanged.

Q: 226. Invert Binary Tree: what is the analogy, and what does it stand for?
A: A hanging mobile in a mirror: bar → node; strings → children.

Q: 226. Invert Binary Tree: Why save the left child?
A: It is overwritten first.

Q: 226. Invert Binary Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 135 — 104. Maximum Depth of Binary Tree | Easy · Bottom-up DFS | MUST
# 135 — 104. Maximum Depth of Binary Tree

**Problem 135 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Bottom-up DFS**

[Open on LeetCode](https://leetcode.com/problems/maximum-depth-of-binary-tree/)

## 1. Problem Statement
You get the root of a binary tree. Return its maximum depth: the number of nodes on the longest path from the root down to a leaf. An empty tree has depth 0.

### Examples
```text
Input:  root = [3,9,20,null,null,15,7]
Output: 3

Input:  root = [1,null,2]
Output: 2
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 10^4].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Asking departments how deep they go
A manager who wants to know how deep the company goes asks each direct report for their department's depth, takes the larger answer and adds one for themself.

| In the story | In the solution |
|---|---|
| deeper answer | max |
| no department | zero |

### Pattern signal
**Bottom-up DFS.** A node's depth depends only on the depths of its two children.

### Thought process
1. **First idea:** List every root-to-leaf path and measure it.
2. **The insight:** A node's depth depends only on the depths of its two children.

### Optimal approach
1. Return zero for null.
2. Compute both children's depths.
3. Return the larger plus one.

### Dry run
```text
root 3, children 9 and 20, 20 has 15 and 7
node   reports
9,15,7 1
20     2
3      3
```

### Why it works
The longest downward path from a node consists of the node and the longest path from one of its children.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }
}
```

### Code walkthrough
- `return 0`: An empty department has depth zero.
- `Math.max`: Only the deeper side matters.
- `1 +`: Count the manager too.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **A path uses one child.** Fix: take the maximum rather than the sum.
2. **Depth counts nodes.** Fix: add one per node.
3. **Null is not a node.** Fix: return zero.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `0` |
| One | `[1]` | `1` |
| Chain | `[1,null,2]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[136 — 543. Diameter of Binary Tree]]: Use both reports. The key idea there: A single height traversal already has both arms at the moment it visits each node.
- [[137 — 110. Balanced Binary Tree]]: Compare them. The key idea there: Height and validity can travel in the same report by using -1 for invalid.

### In a real interview
Say it in this order:

1. **Brute force:** List every root-to-leaf path and measure it.
2. **Insight:** A node's depth depends only on the depths of its two children.
3. **Approach in a few words:** One plus the deeper child, postorder.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 104. Maximum Depth of Binary Tree: which pattern, and what is the insight?
A: Bottom-up DFS. A node's depth depends only on the depths of its two children.

Q: 104. Maximum Depth of Binary Tree: what is the analogy, and what does it stand for?
A: Asking departments how deep they go: deeper answer → max; no department → zero.

Q: 104. Maximum Depth of Binary Tree: What does null return for depth?
A: Zero.

Q: 104. Maximum Depth of Binary Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 136 — 543. Diameter of Binary Tree | Easy · Height plus global best | MUST
# 136 — 543. Diameter of Binary Tree

**Problem 136 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Height plus global best**

[Open on LeetCode](https://leetcode.com/problems/diameter-of-binary-tree/)

## 1. Problem Statement
You get the root of a binary tree. Return its diameter: the number of edges on the longest path between any two nodes. That path need not pass through the root.

### Examples
```text
Input:  root = [1,2,3,4,5]
Output: 3

Input:  root = [1,2]
Output: 1
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 10^4].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The longest walk over a junction
The longest walk over a junction uses the two deepest arms. But your boss can extend only one of them. So report one arm upward and privately record both.

| In the story | In the solution |
|---|---|
| report | height |
| record | left + right |

### Pattern signal
**Height plus global best.** A single height traversal already has both arms at the moment it visits each node.

### Thought process
1. **First idea:** Compute the heights separately at every node, in O(n²).
2. **The insight:** A single height traversal already has both arms at the moment it visits each node.

### Optimal approach
1. Return zero for null.
2. Get both heights.
3. Update the best with their sum.
4. Return the larger plus one.

### Dry run
```text
tree 1,2,3,4,5
node   left,right  best  reports
4,5,3  0,0         0     1
2      1,1         2     2
1      2,1         3     3
```

### Why it works
Every path has a highest node, and the best path over that node is exactly the two child heights. All nodes are tried.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private int best;

    public int diameterOfBinaryTree(TreeNode root) {
        best = 0;
        height(root);
        return best;
    }

    private int height(TreeNode node) {
        if (node == null) return 0;
        int left = height(node.left), right = height(node.right);
        best = Math.max(best, left + right);
        return 1 + Math.max(left, right);
    }
}
```

### Code walkthrough
- `left + right`: The edges of the path bending here.
- `1 + Math.max`: A parent can extend one arm only.
- `return best`: The best may be far from the root.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **The best may avoid the root.** Fix: update at every node.
2. **A parent can extend one arm.** Fix: return the larger only.
3. **The answer is in edges.** Fix: do not add one.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `0` |
| Two | `[1,2]` | `1` |
| Three | `[1,2,3]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[165 — 124. Binary Tree Maximum Path Sum]]: Weights. The key idea there: Every path has a highest node, so compute the best path bending at each.
- [[135 — 104. Maximum Depth of Binary Tree]]: The report alone. The key idea there: A node's depth depends only on the depths of its two children.

### In a real interview
Say it in this order:

1. **Brute force:** Compute the heights separately at every node, in O(n²).
2. **Insight:** A single height traversal already has both arms at the moment it visits each node.
3. **Approach in a few words:** I return height while updating a through-node answer.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 543. Diameter of Binary Tree: which pattern, and what is the insight?
A: Height plus global best. A single height traversal already has both arms at the moment it visits each node.

Q: 543. Diameter of Binary Tree: what is the analogy, and what does it stand for?
A: The longest walk over a junction: report → height; record → left + right.

Q: 543. Diameter of Binary Tree: What is returned versus recorded?
A: One arm is returned, both are recorded.

Q: 543. Diameter of Binary Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 137 — 110. Balanced Binary Tree | Easy · Height check | MUST
# 137 — 110. Balanced Binary Tree

**Problem 137 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Height check**

[Open on LeetCode](https://leetcode.com/problems/balanced-binary-tree/)

## 1. Problem Statement
You get the root of a binary tree. Return true if it is height-balanced: at every node the heights of the two subtrees differ by at most one.

### Examples
```text
Input:  root = [3,9,20,null,null,15,7]
Output: true

Input:  root = [1,2,2,3,3,null,null,4,4]
Output: false

Input:  root = []
Output: true
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 5000].
-10^4 <= Node.val <= 10^4
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An inspector with a red card
An inspector asks each section for its height. A tilted section answers with a red card instead, and anyone receiving a red card passes it straight up without measuring further.

| In the story | In the solution |
|---|---|
| red card | -1 |
| level | gap at most one |

### Pattern signal
**Height check.** Height and validity can travel in the same report by using -1 for invalid.

### Thought process
1. **First idea:** Call a height function at every node, in O(n²).
2. **The insight:** Height and validity can travel in the same report by using -1 for invalid.

### Optimal approach
1. Get the left report and pass on failure.
2. Get the right likewise.
3. Fail on a gap over one.
4. Otherwise return the height.

### Dry run
```text
chain 1,2,3 leftward
node  heights  report
3     0,0      1
2     1,0      2
1     2,0      red card
```

### Why it works
A red card is created exactly at a violation and passed to the root, and without one every node has been checked.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isBalanced(TreeNode root) {
        return height(root) != -1;
    }

    private int height(TreeNode node) {
        if (node == null) return 0;
        int left = height(node.left);
        if (left == -1) return -1;
        int right = height(node.right);
        if (right == -1 || Math.abs(left - right) > 1) return -1;
        return 1 + Math.max(left, right);
    }
}
```

### Code walkthrough
- `left == -1`: Stop as soon as one side failed.
- `Math.abs(left - right) > 1`: This node itself is tilted.
- `!= -1`: No red card reached the top.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Balance is needed everywhere.** Fix: check each node.
2. **The shallowest leaf is irrelevant.** Fix: compare heights.
3. **Arithmetic on -1 hides failures.** Fix: propagate it at once.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `true` |
| Two | `[1,2]` | `true` |
| Chain | `[1,2,null,3]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[136 — 543. Diameter of Binary Tree]]: The same pass. The key idea there: A single height traversal already has both arms at the moment it visits each node.
- [[140 — 108. Convert Sorted Array to Binary Search Tree]]: Construct it. The key idea there: The middle value as root gives both balance and search order at once.

### In a real interview
Say it in this order:

1. **Brute force:** Call a height function at every node, in O(n²).
2. **Insight:** Height and validity can travel in the same report by using -1 for invalid.
3. **Approach in a few words:** A sentinel joins height and validity.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 110. Balanced Binary Tree: which pattern, and what is the insight?
A: Height check. Height and validity can travel in the same report by using -1 for invalid.

Q: 110. Balanced Binary Tree: what is the analogy, and what does it stand for?
A: An inspector with a red card: red card → -1; level → gap at most one.

Q: 110. Balanced Binary Tree: What does -1 mean?
A: That subtree is unbalanced.

Q: 110. Balanced Binary Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 138 — 100. Same Tree | Easy · Parallel DFS | MUST
# 138 — 100. Same Tree

**Problem 138 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Parallel DFS**

[Open on LeetCode](https://leetcode.com/problems/same-tree/)

## 1. Problem Statement
You get the roots `p` and `q` of two binary trees. Return true if they have identical structure and equal values at every matching position.

### Examples
```text
Input:  p = [1,2,3], q = [1,2,3]
Output: true

Input:  p = [1,2], q = [1,null,2]
Output: false

Input:  p = [1,2,1], q = [1,1,2]
Output: false
```

### Constraints
```text
The number of nodes in both trees is in the range [0, 100].
-10^4 <= Node.val <= 10^4
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Comparing twins limb by limb
Two examiners inspect twins limb by limb in step. They stop as soon as only one finds a limb, or the marks on matching limbs differ.

| In the story | In the solution |
|---|---|
| in step | paired calls |
| only one | one null |

### Pattern signal
**Parallel DFS.** Two trees are equal when the roots match and both pairs of children are equal trees.

### Thought process
1. **First idea:** Serialize both trees, using extra memory.
2. **The insight:** Two trees are equal when the roots match and both pairs of children are equal trees.

### Optimal approach
1. Accept two nulls.
2. Reject exactly one.
3. Compare values.
4. Recurse on left with left and right with right.

### Dry run
```text
1,2 versus 1,null,2
pair        result
1 and 1     continue
2 and null  false
```

### Why it works
The recursion is literally the definition of equality, applied to every matching position.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null || q == null) return p == q;
        return p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
}
```

### Code walkthrough
- `return p == q`: True only when both are absent.
- `p.val == q.val`: The marks at this position agree.
- `isSameTree(p.left, q.left)`: Compare the same limb on both twins.

### Complexity
- **Time:** O(min(n,m))
- **Space:** O(h)

### Common mistakes
1. **Reading values before null checks crashes.** Fix: check nulls first.
2. **Traversals without nulls lose the shape.** Fix: recurse in parallel.
3. **Copies are different objects.** Fix: compare values.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], []` | `true` |
| Value | `[1], [2]` | `false` |
| Missing | `[1], []` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[139 — 572. Subtree of Another Tree]]: At every start. The key idea there: The only possible places for a match to start are the nodes of the main tree.
- [[144 — 101. Symmetric Tree]]: Crossed. The key idea there: Symmetry is tree equality with the children crossed.

### In a real interview
Say it in this order:

1. **Brute force:** Serialize both trees, using extra memory.
2. **Insight:** Two trees are equal when the roots match and both pairs of children are equal trees.
3. **Approach in a few words:** A parallel DFS with null cases first.
4. **Cost:** time O(min(n,m)); space O(h).

## 5. Recall
Q: 100. Same Tree: which pattern, and what is the insight?
A: Parallel DFS. Two trees are equal when the roots match and both pairs of children are equal trees.

Q: 100. Same Tree: what is the analogy, and what does it stand for?
A: Comparing twins limb by limb: in step → paired calls; only one → one null.

Q: 100. Same Tree: When are two null nodes equal?
A: Always.

Q: 100. Same Tree: what are the time and space costs?
A: Time O(min(n,m)). Space O(h).

@@ 3 | TOPIC | Practice — Review 23 | Re-solve without looking | MUST
# Review 23

Solve on a blank page. No notes, hints or solutions.

## 100. Same Tree
Decide whether two trees are identical.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/same-tree/)

## 104. Maximum Depth of Binary Tree
Return the height of a tree in nodes.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/maximum-depth-of-binary-tree/)

## 148. Sort List
Sort a linked list in O(n log n).

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/sort-list/)

## 153. Find Minimum in Rotated Sorted Array
Find the minimum of a rotated sorted array.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 100 | | | | |
| 104 | | | | |
| 148 | | | | |
| 153 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[138 — 100. Same Tree]]
- [[135 — 104. Maximum Depth of Binary Tree]]
- [[124 — 148. Sort List]]
- [[100 — 153. Find Minimum in Rotated Sorted Array]]

@@ 3 | TOPIC | 139 — 572. Subtree of Another Tree | Easy · Match at every node | MUST
# 139 — 572. Subtree of Another Tree

**Problem 139 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Match at every node**

[Open on LeetCode](https://leetcode.com/problems/subtree-of-another-tree/)

## 1. Problem Statement
You get `root` and `subRoot`. Return true if some node of `root`, together with all of its descendants, forms a tree identical to `subRoot`. A tree is a subtree of itself.

### Examples
```text
Input:  root = [3,4,5,1,2], subRoot = [4,1,2]
Output: true

Input:  root = [3,4,5,1,2,null,null,null,null,0], subRoot = [4,1,2]
Output: false
```

### Constraints
```text
The number of nodes in the root tree is in the range [1, 2000].
The number of nodes in the subRoot tree is in the range [1, 1000].
-10^4 <= root.val <= 10^4
-10^4 <= subRoot.val <= 10^4
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Fitting a stencil
Hold a stencil over every node of the big tree. At one of them it must fit exactly, all the way to the leaves, with nothing extra hanging below.

| In the story | In the solution |
|---|---|
| stencil | subRoot |
| fit | same tree |

### Pattern signal
**Match at every node.** The only possible places for a match to start are the nodes of the main tree.

### Thought process
1. **First idea:** Comparing at every node is the direct method and fine for these limits.
2. **The insight:** The only possible places for a match to start are the nodes of the main tree.

### Optimal approach
1. Fail when the main tree is empty.
2. Test equality here.
3. Otherwise search the left and the right.

### Dry run
```text
3,4,5,1,2 and 4,1,2
start  result
3      values differ
4      all match: true
```

### Why it works
Every candidate root is tested with an exact tree comparison, so a match is found exactly when one exists.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (root == null) return false;
        return same(root, subRoot) || isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    private boolean same(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && same(a.left, b.left) && same(a.right, b.right);
    }
}
```

### Code walkthrough
- `root == null`: A non-empty stencil can't fit on nothing.
- `same(root, subRoot)`: Try the stencil at this node.
- `return a == b`: Both must end at the same places.

### Complexity
- **Time:** O(n·m)
- **Space:** O(h)

### Common mistakes
1. **A match with extra children is not equal.** Fix: require both to end together.
2. **Equal values can repeat.** Fix: try every start.
3. **A serialization without delimiters confuses 2 with 12.** Fix: add markers.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Whole | `[1], [1]` | `true` |
| Extra | `[1,2], [1]` | `false` |
| Leaf | `[1,2], [2]` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[138 — 100. Same Tree]]: One place. The key idea there: Two trees are equal when the roots match and both pairs of children are equal trees.
- **652. Duplicate Subtrees**: A map of shapes.

### In a real interview
Say this: *"Run same-tree per node, or use KMP on delimited serials."*

## 5. Recall
Q: 572. Subtree of Another Tree: which pattern, and what is the insight?
A: Match at every node. The only possible places for a match to start are the nodes of the main tree.

Q: 572. Subtree of Another Tree: what is the analogy, and what does it stand for?
A: Fitting a stencil: stencil → subRoot; fit → same tree.

Q: 572. Subtree of Another Tree: Can a match have extra descendants?
A: No.

Q: 572. Subtree of Another Tree: what are the time and space costs?
A: Time O(n·m). Space O(h).

@@ 3 | TOPIC | 140 — 108. Convert Sorted Array to Binary Search Tree | Easy · Middle as root | NICE
# 140 — 108. Convert Sorted Array to Binary Search Tree

**Problem 140 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Middle as root**

[Open on LeetCode](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/)

## 1. Problem Statement
You get an array sorted in strictly increasing order. Build a height-balanced binary search tree from its values and return the root. Any valid balanced tree is accepted.

### Examples
```text
Input:  nums = [-10,-3,0,5,9]
Output: [0,-3,9,-10,null,5]

Input:  nums = [1,3]
Output: [3,1]
```

### Constraints
```text
1 <= nums.length <= 10^4
-10^4 <= nums[i] <= 10^4
nums is sorted in a strictly increasing order.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Choosing a captain from the middle
To form balanced teams from a line sorted by height, make the middle person captain. Both sides are equally large and correctly ordered, and each side chooses its captain the same way.

| In the story | In the solution |
|---|---|
| captain | root |
| teams | slices |

### Pattern signal
**Middle as root.** The middle value as root gives both balance and search order at once.

### Thought process
1. **First idea:** Insert in sorted order, which produces a chain.
2. **The insight:** The middle value as root gives both balance and search order at once.

### Optimal approach
1. Return null for an empty range.
2. Create the middle.
3. Build the left range.
4. Build the right range.

### Dry run
```text
nums = [-10,-3,0,5,9], lower middle
range  root
0..4   0
0..1   -10, right -3
3..4   5, right 9
```

### Why it works
The sides differ in size by at most one at every node and contain only smaller or only larger values than the root.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode sortedArrayToBST(int[] nums) {
        return build(nums, 0, nums.length - 1);
    }

    private TreeNode build(int[] nums, int low, int high) {
        if (low > high) return null;
        int mid = low + (high - low) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = build(nums, low, mid - 1);
        root.right = build(nums, mid + 1, high);
        return root;
    }
}
```

### Code walkthrough
- `low > high`: An empty team.
- `nums[mid]`: The middle person is the captain.
- `mid - 1`: The captain is not in either team.

### Complexity
- **Time:** O(n)
- **Space:** O(log n)

### Common mistakes
1. **Copying slices costs extra.** Fix: pass indices.
2. **Keeping mid in a side never shrinks.** Fix: exclude it.
3. **Several shapes are valid.** Fix: accept either middle.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `[1]` |
| Two | `[1,3]` | `[1,null,3]` |
| Three | `[1,2,3]` | `[2,1,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **109. From a List**: Inorder building.
- [[157 — 105. Construct Binary Tree from Preorder and Inorder Traversal]]: Root named. The key idea there: A forward pointer and an index map give root and split in O(1).

### In a real interview
Say it in this order:

1. **Brute force:** Insert in sorted order, which produces a chain.
2. **Insight:** The middle value as root gives both balance and search order at once.
3. **Approach in a few words:** Recursive middle roots.
4. **Cost:** time O(n); space O(log n).

## 5. Recall
Q: 108. Convert Sorted Array to Binary Search Tree: which pattern, and what is the insight?
A: Middle as root. The middle value as root gives both balance and search order at once.

Q: 108. Convert Sorted Array to Binary Search Tree: what is the analogy, and what does it stand for?
A: Choosing a captain from the middle: captain → root; teams → slices.

Q: 108. Convert Sorted Array to Binary Search Tree: Why take the middle as root?
A: Equal sides and correct order.

Q: 108. Convert Sorted Array to Binary Search Tree: what are the time and space costs?
A: Time O(n). Space O(log n).

@@ 3 | TOPIC | 141 — 617. Merge Two Binary Trees | Easy · Parallel DFS | NICE
# 141 — 617. Merge Two Binary Trees

**Problem 141 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Parallel DFS**

[Open on LeetCode](https://leetcode.com/problems/merge-two-binary-trees/)

## 1. Problem Statement
You get two binary trees. Lay one over the other: where both have a node the new value is their sum, and where only one has a node that node is used. Return the merged tree.

### Examples
```text
Input:  root1 = [1,3,2,5], root2 = [2,1,3,null,4,null,7]
Output: [3,4,5,5,4,null,7]

Input:  root1 = [1], root2 = [1,2]
Output: [2,2]
```

### Constraints
```text
The number of nodes in both trees is in the range [0, 2000].
-10^4 <= Node.val <= 10^4
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Laying two transparencies together
Lay two transparencies on each other. Where both have a node add the ink. Where only one has, its whole branch shows through unchanged.

| In the story | In the solution |
|---|---|
| overlap | two non-null |
| shows through | return it |

### Pattern signal
**Parallel DFS.** One null settles an entire subtree, so only the overlap needs visiting.

### Thought process
1. **First idea:** Copy both trees first, which is needless.
2. **The insight:** One null settles an entire subtree, so only the overlap needs visiting.

### Optimal approach
1. Return the other when one is null.
2. Add the values.
3. Merge the left children.
4. Merge the right children.

### Dry run
```text
1,3 and 2,null,4
position  result
root      3
left      3
right     4
```

### Why it works
Every position follows the rule, and recursing on matching children keeps positions aligned.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (root1 == null) return root2;
        if (root2 == null) return root1;
        root1.val += root2.val;
        root1.left = mergeTrees(root1.left, root2.left);
        root1.right = mergeTrees(root1.right, root2.right);
        return root1;
    }
}
```

### Code walkthrough
- `return root2`: Only the second has a branch here.
- `root1.val +=`: Both have a node, so add.
- `root1.left =`: Attach whatever the merge returned.

### Complexity
- **Time:** O(overlap)
- **Space:** O(h)

### Common mistakes
1. **One side can be null.** Fix: return the other.
2. **Ignoring return values drops branches.** Fix: assign them.
3. **The first tree is modified.** Fix: mention it or copy.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], []` | `[]` |
| One | `[], [1]` | `[1]` |
| Both | `[1], [2]` | `[3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[138 — 100. Same Tree]]: Compare. The key idea there: Two trees are equal when the roots match and both pairs of children are equal trees.
- [[121 — 2. Add Two Numbers]]: On lists. The key idea there: With units first, walking forward is exactly the addition algorithm.

### In a real interview
Say it in this order:

1. **Brute force:** Copy both trees first, which is needless.
2. **Insight:** One null settles an entire subtree, so only the overlap needs visiting.
3. **Approach in a few words:** Parallel DFS reusing nodes.
4. **Cost:** time O(overlap); space O(h).

## 5. Recall
Q: 617. Merge Two Binary Trees: which pattern, and what is the insight?
A: Parallel DFS. One null settles an entire subtree, so only the overlap needs visiting.

Q: 617. Merge Two Binary Trees: what is the analogy, and what does it stand for?
A: Laying two transparencies together: overlap → two non-null; shows through → return it.

Q: 617. Merge Two Binary Trees: What if one merge node is null?
A: Return the other subtree.

Q: 617. Merge Two Binary Trees: what are the time and space costs?
A: Time O(overlap). Space O(h).

@@ 3 | TOPIC | 142 — 112. Path Sum | Easy · Root-to-leaf state | NICE
# 142 — 112. Path Sum

**Problem 142 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Root-to-leaf state**

[Open on LeetCode](https://leetcode.com/problems/path-sum/)

## 1. Problem Statement
You get the root of a binary tree and `targetSum`. Return true if some path from the root down to a leaf has values adding up exactly to `targetSum`. A leaf has no children.

### Examples
```text
Input:  root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
Output: true

Input:  root = [1,2,3], targetSum = 5
Output: false

Input:  root = [], targetSum = 0
Output: false
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 5000].
-1000 <= Node.val <= 1000
-1000 <= targetSum <= 1000
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A budget to spend exactly
You must spend a budget exactly. Each node on the way charges its value and only the leaves have exits, where you check that precisely zero is left.

| In the story | In the solution |
|---|---|
| budget | remaining |
| exit | leaf |

### Pattern signal
**Root-to-leaf state.** A branch only needs to know the amount still to be spent.

### Thought process
1. **First idea:** Collect every path and then add each.
2. **The insight:** A branch only needs to know the amount still to be spent.

### Optimal approach
1. Fail on null.
2. Subtract this value.
3. Compare at a leaf.
4. Otherwise try both children.

### Dry run
```text
path 5,4,11,2 target 22
node  left over
5     17
4     13
11    2
2     0, true
```

### Why it works
The remainder always equals the target minus the path so far, so zero at a leaf is exactly a matching path.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) return false;
        int left = targetSum - root.val;
        if (root.left == null && root.right == null) return left == 0;
        return hasPathSum(root.left, left) || hasPathSum(root.right, left);
    }
}
```

### Code walkthrough
- `return false`: Falling off the tree is not an exit.
- `root.left == null && root.right == null`: Only a leaf has an exit.
- `targetSum - root.val`: Pay this node's charge.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **An inner match is not a path to a leaf.** Fix: test only leaves.
2. **Accepting at null cheats on one-child nodes.** Fix: return false there.
3. **Negatives can recover.** Fix: never prune on overshoot.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], 0` | `false` |
| One | `[1], 1` | `true` |
| Inner | `[1,2], 1` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **113. Paths**: Backtrack.
- [[159 — 129. Sum Root to Leaf Numbers]]: Carry a number. The key idea there: The whole prefix of a path is one integer.

### In a real interview
Say it in this order:

1. **Brute force:** Collect every path and then add each.
2. **Insight:** A branch only needs to know the amount still to be spent.
3. **Approach in a few words:** Carry the remainder, test at leaves.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 112. Path Sum: which pattern, and what is the insight?
A: Root-to-leaf state. A branch only needs to know the amount still to be spent.

Q: 112. Path Sum: what is the analogy, and what does it stand for?
A: A budget to spend exactly: budget → remaining; exit → leaf.

Q: 112. Path Sum: Where is a path sum tested?
A: Only at leaves.

Q: 112. Path Sum: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 143 — 783. Minimum Distance Between BST Nodes | Easy · Inorder previous | NICE
# 143 — 783. Minimum Distance Between BST Nodes

**Problem 143 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Inorder previous**

[Open on LeetCode](https://leetcode.com/problems/minimum-distance-between-bst-nodes/)

## 1. Problem Statement
You get the root of a binary search tree with at least two nodes. Return the smallest difference between the values of any two different nodes.

### Examples
```text
Input:  root = [4,2,6,1,3]
Output: 1

Input:  root = [1,0,48,null,null,12,49]
Output: 1
```

### Constraints
```text
The number of nodes in the tree is in the range [2, 100].
0 <= Node.val <= 10^5
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Closest prices on a sorted shelf
Reading a search tree inorder is walking along a shelf sorted by price. The two closest prices have to stand next to each other, so compare each with the last one seen.

| In the story | In the solution |
|---|---|
| shelf | inorder |
| last price | previous |

### Pattern signal
**Inorder previous.** Inorder of a search tree is sorted, so only consecutive visits matter.

### Thought process
1. **First idea:** Compare every pair of values, in O(n²).
2. **The insight:** Inorder of a search tree is sorted, so only consecutive visits matter.

### Optimal approach
1. Visit left.
2. Compare with the previous value.
3. Save this value.
4. Visit right.

### Dry run
```text
inorder 1,2,3,4,6
value  gap
2      1
3      1
4      1
6      2
Answer 1
```

### Why it works
The minimum gap in a sorted sequence is always between neighbors, and all neighboring pairs are compared.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private Integer previous;
    private int best;

    public int minDiffInBST(TreeNode root) {
        previous = null;
        best = Integer.MAX_VALUE;
        visit(root);
        return best;
    }

    private void visit(TreeNode node) {
        if (node == null) return;
        visit(node.left);
        if (previous != null) best = Math.min(best, node.val - previous);
        previous = node.val;
        visit(node.right);
    }
}
```

### Code walkthrough
- `previous != null`: The first value has nothing before it.
- `node.val - previous`: The gap is never negative in sorted order.
- `previous = node.val`: Remember for the next visit.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **The closest may not be parent and child.** Fix: compare inorder neighbors.
2. **A previous of 0 invents a gap.** Fix: start it at null.
3. **Preorder is not sorted.** Fix: visit inorder.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two | `[2,1]` | `1` |
| Far | `[10,1]` | `9` |
| Zero | `[1,0]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[156 — 230. Kth Smallest Element in a BST]]: Count. The key idea there: An iterative inorder walk produces values in order and can stop early.
- [[155 — 98. Validate Binary Search Tree]]: Must rise. The key idea there: All ancestors' rules compress into one allowed interval.

### In a real interview
Say it in this order:

1. **Brute force:** Compare every pair of values, in O(n²).
2. **Insight:** Inorder of a search tree is sorted, so only consecutive visits matter.
3. **Approach in a few words:** Adjacent inorder values suffice.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 783. Minimum Distance Between BST Nodes: which pattern, and what is the insight?
A: Inorder previous. Inorder of a search tree is sorted, so only consecutive visits matter.

Q: 783. Minimum Distance Between BST Nodes: what is the analogy, and what does it stand for?
A: Closest prices on a sorted shelf: shelf → inorder; last price → previous.

Q: 783. Minimum Distance Between BST Nodes: Which BST values give the minimum gap?
A: Inorder neighbors.

Q: 783. Minimum Distance Between BST Nodes: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 144 — 101. Symmetric Tree | Easy · Mirror DFS | NICE
# 144 — 101. Symmetric Tree

**Problem 144 – Topic: Trees (DFS & BFS) | Difficulty: Easy | Pattern: Mirror DFS**

[Open on LeetCode](https://leetcode.com/problems/symmetric-tree/)

## 1. Problem Statement
You get the root of a binary tree. Return true if it is symmetric around its centre, meaning the left subtree is the mirror image of the right one.

### Examples
```text
Input:  root = [1,2,2,3,4,4,3]
Output: true

Input:  root = [1,2,2,null,3,null,3]
Output: false
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 1000].
-100 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Hands pressed together
Press your hands together: left thumb meets right thumb. So two fingers walk the two halves in opposite directions, outer with outer and inner with inner.

| In the story | In the solution |
|---|---|
| thumbs | outer children |
| pressed | paired calls |

### Pattern signal
**Mirror DFS.** Symmetry is tree equality with the children crossed.

### Thought process
1. **First idea:** Invert a copy and compare it with the original.
2. **The insight:** Symmetry is tree equality with the children crossed.

### Optimal approach
1. Compare the root's children.
2. Match nulls and values.
3. Recurse on the outer pair.
4. Recurse on the inner pair.

### Dry run
```text
1,2,2,3,4,4,3
pair   result
2,2    equal
3,3    equal
4,4    equal, true
```

### Why it works
A mirror maps every left-left to a right-right and left-right to a right-left, which are exactly the pairs compared.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isSymmetric(TreeNode root) {
        return mirror(root.left, root.right);
    }

    private boolean mirror(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && mirror(a.left, b.right) && mirror(a.right, b.left);
    }
}
```

### Code walkthrough
- `mirror(root.left, root.right)`: Start with the two hands.
- `a.left, b.right`: The outer fingers meet.
- `a.right, b.left`: The inner fingers meet.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Same-side children test equality.** Fix: cross them.
2. **Levels without nulls lose shape.** Fix: compare nodes.
3. **The root's children are not enough.** Fix: recurse.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `true` |
| Lone | `[1,2]` | `false` |
| Same side | `[1,2,2,null,3,null,3]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[138 — 100. Same Tree]]: Uncrossed. The key idea there: Two trees are equal when the roots match and both pairs of children are equal trees.
- [[134 — 226. Invert Binary Tree]]: Make a mirror. The key idea there: The mirror of a tree is its root with the mirrors of its children exchanged.

### In a real interview
Say it in this order:

1. **Brute force:** Invert a copy and compare it with the original.
2. **Insight:** Symmetry is tree equality with the children crossed.
3. **Approach in a few words:** Paired DFS crossing children.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 101. Symmetric Tree: which pattern, and what is the insight?
A: Mirror DFS. Symmetry is tree equality with the children crossed.

Q: 101. Symmetric Tree: what is the analogy, and what does it stand for?
A: Hands pressed together: thumbs → outer children; pressed → paired calls.

Q: 101. Symmetric Tree: Which children are paired for symmetry?
A: Left with opposite right.

Q: 101. Symmetric Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | Practice — Review 24 | Re-solve without looking | MUST
# Review 24

Solve on a blank page. No notes, hints or solutions.

## 101. Symmetric Tree
Decide whether a tree is symmetric.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/symmetric-tree/)

## 617. Merge Two Binary Trees
Merge two trees by adding overlapping nodes.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/merge-two-binary-trees/)

## 25. Reverse Nodes in k-Group
Reverse each full group of k nodes.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/reverse-nodes-in-k-group/)

## 4. Median of Two Sorted Arrays
Find the median of two sorted arrays in logarithmic time.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/median-of-two-sorted-arrays/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 101 | | | | |
| 617 | | | | |
| 25 | | | | |
| 4 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[144 — 101. Symmetric Tree]]
- [[141 — 617. Merge Two Binary Trees]]
- [[130 — 25. Reverse Nodes in k-Group]]
- [[106 — 4. Median of Two Sorted Arrays]]

@@ 2 | MODULE | Trees — Medium | 20 problems | MUST
# Trees: Medium

Patterns in this group: Use built next links, BST split, BST descent, BST deletion, Level BFS, Last in level, Level BFS with direction, Traversal split, Weighted tree walk, Maximum so far, Allowed range, Inorder count, Catalan DP, Path number, Two-state tree DP, BST pruning, Inorder iterator, Reverse inorder, Report matches upward.

@@ 3 | TOPIC | 145 — 116. Populating Next Right Pointers in Each Node | Medium · Use built next links | SHOULD
# 145 — 116. Populating Next Right Pointers in Each Node

**Problem 145 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Use built next links**

[Open on LeetCode](https://leetcode.com/problems/populating-next-right-pointers-in-each-node/)

## 1. Problem Statement
In a perfect binary tree every parent has two children and all leaves share a level. Each node has a `next` pointer. Point it at the node directly to the right on the same level, or null for the last. Follow-up: constant extra space, recursion stack excepted.

### Examples
```text
Input:  root = [1,2,3,4,5,6,7]
Output: [1,#,2,3,#,4,5,6,7,#]

Input:  root = []
Output: []
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 2^12 - 1].
-1000 <= Node.val <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Building rope bridges floor by floor
You build rope bridges floor by floor. Standing on a floor that already has bridges, you can walk across it and tie the floor below, both inside a family and between neighboring families.

| In the story | In the solution |
|---|---|
| bridged floor | next pointers |
| between families | node.next.left |

### Pattern signal
**Use built next links.** A linked level acts as the queue for the level below it.

### Thought process
1. **First idea:** BFS with a queue, using memory proportional to the width.
2. **The insight:** A linked level acts as the queue for the level below it.

### Optimal approach
1. Start at the leftmost.
2. Walk along next.
3. Join the two children.
4. Join to the neighbor's left child.
5. Move down.

### Dry run
```text
1 to 7
level  links
1      2 to 3
2,3    4 to 5, 5 to 6, 6 to 7
```

### Why it works
The current level is linked, so each adjacent pair below is either two children of one parent or of adjacent parents, and both are linked.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public Node connect(Node root) {
        Node first = root;
        while (first != null && first.left != null) {
            for (Node node = first; node != null; node = node.next) {
                node.left.next = node.right;
                if (node.next != null) node.right.next = node.next.left;
            }
            first = first.left;
        }
        return root;
    }
}
```

### Code walkthrough
- `first.left != null`: Continue while there is a level below.
- `node.left.next = node.right`: The bridge inside a family.
- `node.next.left`: The bridge to the neighboring family.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Sibling links alone miss cousins.** Fix: link across parents.
2. **A queue is not constant space.** Fix: use the level above.
3. **An imperfect tree lacks children.** Fix: use a dummy for that variation.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `next null` |
| Three | `[1,2,3]` | `2 to 3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **117. Any Tree**: A dummy per level.
- [[149 — 102. Binary Tree Level Order Traversal]]: A queue. The key idea there: At the start of each round the queue holds exactly one whole level.

### In a real interview
Say it in this order:

1. **Brute force:** BFS with a queue, using memory proportional to the width.
2. **Insight:** A linked level acts as the queue for the level below it.
3. **Approach in a few words:** The level above replaces the queue.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 116. Populating Next Right Pointers in Each Node: which pattern, and what is the insight?
A: Use built next links. A linked level acts as the queue for the level below it.

Q: 116. Populating Next Right Pointers in Each Node: what is the analogy, and what does it stand for?
A: Building rope bridges floor by floor: bridged floor → next pointers; between families → node.next.left.

Q: 116. Populating Next Right Pointers in Each Node: How are cousins linked?
A: The right child points to the parent's next node's left.

Q: 116. Populating Next Right Pointers in Each Node: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 146 — 235. Lowest Common Ancestor of a Binary Search Tree | Medium · BST split | MUST
# 146 — 235. Lowest Common Ancestor of a Binary Search Tree

**Problem 146 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: BST split**

[Open on LeetCode](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/)

## 1. Problem Statement
You get a binary search tree and two of its nodes `p` and `q`. Return their lowest common ancestor: the deepest node having both as descendants, where a node counts as its own descendant.

### Examples
```text
Input:  root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
Output: 6

Input:  root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
Output: 2

Input:  root = [2,1], p = 2, q = 1
Output: 2
```

### Constraints
```text
The number of nodes in the tree is in the range [2, 10^5].
-10^9 <= Node.val <= 10^9
All Node.val are unique.
p != q
p and q will exist in the BST.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Where two travellers part
Two travellers follow the same signposts. Their routes share every step until one is sent the other way or has already arrived. That junction is their lowest common ancestor.

| In the story | In the solution |
|---|---|
| signpost | value comparison |
| parting | answer |

### Pattern signal
**BST split.** The search order reveals which side each target is on without searching.

### Thought process
1. **First idea:** Store both root paths and compare them.
2. **The insight:** The search order reveals which side each target is on without searching.

### Optimal approach
1. Go left when both are smaller.
2. Go right when both are larger.
3. Otherwise return the node.

### Dry run
```text
root 6, p 2, q 8
node  action
6     split, return 6
```

### Why it works
Until the split both targets are inside one child's subtree. At the split no single child contains both, so it is the lowest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode node = root;
        while (true) {
            if (p.val < node.val && q.val < node.val) node = node.left;
            else if (p.val > node.val && q.val > node.val) node = node.right;
            else return node;
        }
    }
}
```

### Code walkthrough
- `p.val < node.val && q.val < node.val`: Both travellers turn left.
- `node = node.right`: Both travellers turn right.
- `else return node`: They part here or one has arrived.

### Complexity
- **Time:** O(h)
- **Space:** O(1)

### Common mistakes
1. **p may be the larger.** Fix: compare both with the node.
2. **A target can be the ancestor.** Fix: stop on arrival.
3. **Products of differences overflow.** Fix: compare directly.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Root | `[2,1], 2, 1` | `2` |
| Split | `[2,1,3], 1, 3` | `2` |
| Deep | `[3,2,null,1], 2, 1` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[164 — 236. Lowest Common Ancestor of a Binary Tree]]: Search both. The key idea there: Postorder reveals the first place where the two reports join.
- [[147 — 701. Insert into a Binary Search Tree]]: One traveller. The key idea there: A failed search ends at a position that is always legal for the value.

### In a real interview
Say it in this order:

1. **Brute force:** Store both root paths and compare them.
2. **Insight:** The search order reveals which side each target is on without searching.
3. **Approach in a few words:** The first split is the ancestor.
4. **Cost:** time O(h); space O(1).

## 5. Recall
Q: 235. Lowest Common Ancestor of a Binary Search Tree: which pattern, and what is the insight?
A: BST split. The search order reveals which side each target is on without searching.

Q: 235. Lowest Common Ancestor of a Binary Search Tree: what is the analogy, and what does it stand for?
A: Where two travellers part: signpost → value comparison; parting → answer.

Q: 235. Lowest Common Ancestor of a Binary Search Tree: Where is a BST's LCA?
A: The first node not having both targets on one side.

Q: 235. Lowest Common Ancestor of a Binary Search Tree: what are the time and space costs?
A: Time O(h). Space O(1).

@@ 3 | TOPIC | 147 — 701. Insert into a Binary Search Tree | Medium · BST descent | SHOULD
# 147 — 701. Insert into a Binary Search Tree

**Problem 147 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: BST descent**

[Open on LeetCode](https://leetcode.com/problems/insert-into-a-binary-search-tree/)

## 1. Problem Statement
You get the root of a binary search tree and a value not yet in it. Insert the value so that the tree remains a valid search tree and return the root. Any valid result is accepted.

### Examples
```text
Input:  root = [4,2,7,1,3], val = 5
Output: [4,2,7,1,3,5]

Input:  root = [40,20,60,10,30,50,70], val = 25
Output: [40,20,60,10,30,50,70,null,null,25]

Input:  root = [4,2,7,1,3,null,null,null,null,null,null], val = 5
Output: [4,2,7,1,3,5]
```

### Constraints
```text
The number of nodes in the tree will be in the range [0, 10^4].
-10^8 <= Node.val <= 10^8
All the values Node.val are unique.
-10^8 <= val <= 10^8
It's guaranteed that val does not exist in the original BST.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Following signs to an empty plot
Follow the signposts as if looking up the new value: smaller left, larger right. The first empty plot on that route is where its house belongs.

| In the story | In the solution |
|---|---|
| sign | comparison |
| plot | null child |

### Pattern signal
**BST descent.** A failed search ends at a position that is always legal for the value.

### Thought process
1. **First idea:** Rebuild the tree from all the values.
2. **The insight:** A failed search ends at a position that is always legal for the value.

### Optimal approach
1. Return the new node for an empty tree.
2. Descend by comparison.
3. Attach at the first null.
4. Return the root.

### Dry run
```text
4,2,7,1,3 insert 5
node  action
4     right
7     attach left
```

### Why it works
Every comparison on the route is satisfied by the new leaf and no other node's relations change.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode insertIntoBST(TreeNode root, int val) {
        TreeNode added = new TreeNode(val);
        if (root == null) return added;
        TreeNode node = root;
        while (true) {
            if (val < node.val) {
                if (node.left == null) {
                    node.left = added;
                    return root;
                }
                node = node.left;
            } else {
                if (node.right == null) {
                    node.right = added;
                    return root;
                }
                node = node.right;
            }
        }
    }
}
```

### Code walkthrough
- `return added`: The first house becomes the root.
- `val < node.val`: Read the signpost.
- `node.left = added`: Link it to its parent.

### Complexity
- **Time:** O(h)
- **Space:** O(1)

### Common mistakes
1. **Assigning a null local attaches nothing.** Fix: set the parent's field.
2. **Balance is not required.** Fix: do not rotate.
3. **The caller needs the tree.** Fix: return the root.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], 5` | `[5]` |
| Left | `[2], 1` | `[2,1]` |
| Right | `[2], 3` | `[2,null,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[148 — 450. Delete Node in a BST]]: Harder. The key idea there: Only the next larger value can replace a two-child node without breaking the order.
- [[089 — 35. Search Insert Position]]: In an array. The key idea there: "At least target" goes from false to true once. Find the first true.

### In a real interview
Say it in this order:

1. **Brute force:** Rebuild the tree from all the values.
2. **Insight:** A failed search ends at a position that is always legal for the value.
3. **Approach in a few words:** Attach where search fails.
4. **Cost:** time O(h); space O(1).

## 5. Recall
Q: 701. Insert into a Binary Search Tree: which pattern, and what is the insight?
A: BST descent. A failed search ends at a position that is always legal for the value.

Q: 701. Insert into a Binary Search Tree: what is the analogy, and what does it stand for?
A: Following signs to an empty plot: sign → comparison; plot → null child.

Q: 701. Insert into a Binary Search Tree: Where does a new BST value go?
A: Where its search reaches null.

Q: 701. Insert into a Binary Search Tree: what are the time and space costs?
A: Time O(h). Space O(1).

@@ 3 | TOPIC | 148 — 450. Delete Node in a BST | Medium · BST deletion | SHOULD
# 148 — 450. Delete Node in a BST

**Problem 148 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: BST deletion**

[Open on LeetCode](https://leetcode.com/problems/delete-node-in-a-bst/)

## 1. Problem Statement
You get the root of a binary search tree and a `key`. Delete the node with that key if it exists, keep a valid search tree, and return the possibly changed root.

### Examples
```text
Input:  root = [5,3,6,2,4,null,7], key = 3
Output: [5,4,6,2,null,null,7]

Input:  root = [5,3,6,2,4,null,7], key = 0
Output: [5,3,6,2,4,null,7]

Input:  root = [], key = 0
Output: []
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 10^4].
-10^5 <= Node.val <= 10^5
Each node has a unique value.
root is a valid binary search tree.
-10^5 <= key <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Replacing a manager
A manager leaves. With one team, that team moves up. With two, the successor is the smallest person of the right team: they take the title and their old post is closed.

| In the story | In the solution |
|---|---|
| successor | right's minimum |
| old post | a second delete |

### Pattern signal
**BST deletion.** Only the next larger value can replace a two-child node without breaking the order.

### Thought process
1. **First idea:** Rebuild the tree without the key, in O(n).
2. **The insight:** Only the next larger value can replace a two-child node without breaking the order.

### Optimal approach
1. Search by comparison.
2. Splice a node missing a child.
3. Otherwise copy the right minimum.
4. Delete that from the right.

### Dry run
```text
5,3,6,2,4,null,7 key 3
step     result
found 3  two children, successor 4
copy     5,4,6,...
remove   5,4,6,2,null,null,7
```

### Why it works
The successor is larger than everything on the left and no larger than anything on the right, so order holds.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) return null;
        if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNode(root.right, key);
        } else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            TreeNode next = root.right;
            while (next.left != null) next = next.left;
            root.val = next.val;
            root.right = deleteNode(root.right, next.val);
        }
        return root;
    }
}
```

### Code walkthrough
- `return root.right`: Only a right team, so move it up.
- `next = next.left`: Walk to the smallest on the right.
- `deleteNode(root.right, next.val)`: Close the successor's old post.

### Complexity
- **Time:** O(h)
- **Space:** O(h)

### Common mistakes
1. **Ignoring return values changes nothing.** Fix: assign them.
2. **Lifting a child can break order.** Fix: use the successor.
3. **The successor would remain twice.** Fix: delete it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Absent | `[1], 2` | `[1]` |
| Only | `[1], 1` | `[]` |
| Two children | `[2,1,3], 2` | `[3,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[147 — 701. Insert into a Binary Search Tree]]: Easy. The key idea there: A failed search ends at a position that is always legal for the value.
- [[161 — 669. Trim a Binary Search Tree]]: Many. The key idea there: Order lets one comparison reject a whole side.

### In a real interview
Say it in this order:

1. **Brute force:** Rebuild the tree without the key, in O(n).
2. **Insight:** Only the next larger value can replace a two-child node without breaking the order.
3. **Approach in a few words:** Three cases, the successor for two children.
4. **Cost:** time O(h); space O(h).

## 5. Recall
Q: 450. Delete Node in a BST: which pattern, and what is the insight?
A: BST deletion. Only the next larger value can replace a two-child node without breaking the order.

Q: 450. Delete Node in a BST: what is the analogy, and what does it stand for?
A: Replacing a manager: successor → right's minimum; old post → a second delete.

Q: 450. Delete Node in a BST: What replaces a node with two children?
A: The minimum of its right subtree.

Q: 450. Delete Node in a BST: what are the time and space costs?
A: Time O(h). Space O(h).

@@ 3 | TOPIC | 149 — 102. Binary Tree Level Order Traversal | Medium · Level BFS | MUST
# 149 — 102. Binary Tree Level Order Traversal

**Problem 149 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Level BFS**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-level-order-traversal/)

## 1. Problem Statement
You get the root of a binary tree. Return its values grouped by level from top to bottom, each level ordered from left to right.

### Examples
```text
Input:  root = [3,9,20,null,null,15,7]
Output: [[3],[9,20],[15,7]]

Input:  root = [1]
Output: [[1]]

Input:  root = []
Output: []
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 2000].
-1000 <= Node.val <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A lift emptying by floor
A lift empties by floors. Count who is waiting before the doors open: exactly those are this floor. Anyone who lines up meanwhile belongs to the next.

| In the story | In the solution |
|---|---|
| count first | saved size |
| arrivals | children |

### Pattern signal
**Level BFS.** At the start of each round the queue holds exactly one whole level.

### Thought process
1. **First idea:** Run a traversal per depth, in O(n·h).
2. **The insight:** At the start of each round the queue holds exactly one whole level.

### Optimal approach
1. Enqueue the root.
2. Save the size.
3. Poll that many.
4. Enqueue their children.
5. Store the level.

### Dry run
```text
3 over 9,20 over 15,7
queue  level
3      [3]
9,20   [9,20]
15,7   [15,7]
```

### Why it works
Removing exactly one level while adding all its children leaves exactly the next level, in left-to-right order.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        if (root != null) queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            for (int size = queue.size(); size > 0; size--) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            result.add(level);
        }
        return result;
    }
}
```

### Code walkthrough
- `int size = queue.size()`: Freeze the count before adding children.
- `level.add(node.val)`: Everyone polled now is on this floor.
- `queue.add(node.left)`: Left first, to keep the order.

### Complexity
- **Time:** O(n)
- **Space:** O(width)

### Common mistakes
1. **A live size mixes levels.** Fix: freeze it first.
2. **ArrayDeque rejects null.** Fix: check children first.
3. **A stack gives depth order.** Fix: use a queue.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[ [1] ]` |
| Chain | `[1,2]` | `[ [1],[2] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[150 — 199. Binary Tree Right Side View]]: The last. The key idea there: Visible simply means last in its level.
- [[151 — 103. Binary Tree Zigzag Level Order Traversal]]: Direction. The key idea there: Never change the order of discovery, only where each value is inserted.

### In a real interview
Say it in this order:

1. **Brute force:** Run a traversal per depth, in O(n·h).
2. **Insight:** At the start of each round the queue holds exactly one whole level.
3. **Approach in a few words:** BFS with a frozen size.
4. **Cost:** time O(n); space O(width).

## 5. Recall
Q: 102. Binary Tree Level Order Traversal: which pattern, and what is the insight?
A: Level BFS. At the start of each round the queue holds exactly one whole level.

Q: 102. Binary Tree Level Order Traversal: what is the analogy, and what does it stand for?
A: A lift emptying by floor: count first → saved size; arrivals → children.

Q: 102. Binary Tree Level Order Traversal: How are levels separated?
A: By saving the queue size first.

Q: 102. Binary Tree Level Order Traversal: what are the time and space costs?
A: Time O(n). Space O(width).

@@ 3 | TOPIC | 150 — 199. Binary Tree Right Side View | Medium · Last in level | MUST
# 150 — 199. Binary Tree Right Side View

**Problem 150 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Last in level**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-right-side-view/)

## 1. Problem Statement
Imagine standing on the right side of a binary tree. Return the values of the nodes you can see, one per level, from top to bottom.

### Examples
```text
Input:  root = [1,2,3,null,5,null,4]
Output: [1,3,4]

Input:  root = [1,2,3,4,null,null,null,5]
Output: [1,3,4,5]

Input:  root = [1,null,3]
Output: [1,3]

Input:  root = []
Output: []
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 100].
-100 <= Node.val <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A photo from the right
A photo taken from the right shows one person per floor, the last one, even if they stand in the left wing because the right wing is shorter.

| In the story | In the solution |
|---|---|
| floor | level |
| last | size reaches one |

### Pattern signal
**Last in level.** Visible simply means last in its level.

### Thought process
1. **First idea:** Follow right children, which fails when the left side is deeper.
2. **The insight:** Visible simply means last in its level.

### Optimal approach
1. Run BFS by levels.
2. Record the node when it is the last of the size.
3. Enqueue children left then right.

### Dry run
```text
1,2,3,null,5,null,4
level  seen
1      1
2,3    3
5,4    4
```

### Why it works
BFS orders each level left to right, so the final node of a level is its rightmost, the visible one.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> seen = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        if (root != null) queue.add(root);
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                TreeNode node = queue.poll();
                if (size == 1) seen.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
        }
        return seen;
    }
}
```

### Code walkthrough
- `size == 1`: The last one on this floor.
- `node.left != null`: The left wing can be visible lower down.
- `root != null`: An empty tree shows nobody.

### Complexity
- **Time:** O(n)
- **Space:** O(width)

### Common mistakes
1. **The right child chain is too short.** Fix: use levels.
2. **The visible node is not the maximum.** Fix: use the position.
3. **Enqueueing right first flips the view.** Fix: keep left first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| Left | `[1,2]` | `[1,2]` |
| Both | `[1,2,3]` | `[1,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[149 — 102. Binary Tree Level Order Traversal]]: All. The key idea there: At the start of each round the queue holds exactly one whole level.
- **513. Bottom Left**: The first.

### In a real interview
Say it in this order:

1. **Brute force:** Follow right children, which fails when the left side is deeper.
2. **Insight:** Visible simply means last in its level.
3. **Approach in a few words:** The last of each BFS level.
4. **Cost:** time O(n); space O(width).

## 5. Recall
Q: 199. Binary Tree Right Side View: which pattern, and what is the insight?
A: Last in level. Visible simply means last in its level.

Q: 199. Binary Tree Right Side View: what is the analogy, and what does it stand for?
A: A photo from the right: floor → level; last → size reaches one.

Q: 199. Binary Tree Right Side View: Which node is visible from the right?
A: The last in each level.

Q: 199. Binary Tree Right Side View: what are the time and space costs?
A: Time O(n). Space O(width).

@@ 3 | TOPIC | Practice — Review 25 | Re-solve without looking | MUST
# Review 25

Solve on a blank page. No notes, hints or solutions.

## 199. Binary Tree Right Side View
Return what is visible from the right.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/binary-tree-right-side-view/)

## 701. Insert into a Binary Search Tree
Insert a value into a BST.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/insert-into-a-binary-search-tree/)

## 543. Diameter of Binary Tree
Find the longest path in edges between any nodes.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/diameter-of-binary-tree/)

## 876. Middle of the Linked List
Return the middle node, second on ties.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/middle-of-the-linked-list/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 199 | | | | |
| 701 | | | | |
| 543 | | | | |
| 876 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[150 — 199. Binary Tree Right Side View]]
- [[147 — 701. Insert into a Binary Search Tree]]
- [[136 — 543. Diameter of Binary Tree]]
- [[112 — 876. Middle of the Linked List]]

@@ 3 | TOPIC | 151 — 103. Binary Tree Zigzag Level Order Traversal | Medium · Level BFS with direction | SHOULD
# 151 — 103. Binary Tree Zigzag Level Order Traversal

**Problem 151 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Level BFS with direction**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/)

## 1. Problem Statement
You get the root of a binary tree. Return its values level by level in zigzag order: the first level left to right, the next right to left, alternating from there.

### Examples
```text
Input:  root = [3,9,20,null,null,15,7]
Output: [[3],[20,9],[15,7]]

Input:  root = [1]
Output: [[1]]

Input:  root = []
Output: []
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 2000].
-100 <= Node.val <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A printer head going both ways
A printer head writes one line to the right and the next to the left. The text arrives in normal order. Only the direction of writing changes.

| In the story | In the solution |
|---|---|
| arrive | normal BFS |
| direction | add first or last |

### Pattern signal
**Level BFS with direction.** Never change the order of discovery, only where each value is inserted.

### Thought process
1. **First idea:** Collect levels normally and reverse every second one afterwards.
2. **The insight:** Never change the order of discovery, only where each value is inserted.

### Optimal approach
1. Run BFS.
2. Add to the back or front by direction.
3. Enqueue normally.
4. Flip after each level.

### Dry run
```text
3 over 9,20 over 15,7
level  direction  output
3      right      [3]
9,20   left       [20,9]
15,7   right      [15,7]
```

### Why it works
The queue stays left to right, and inserting at the front reverses exactly the levels that need it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        if (root != null) queue.add(root);
        boolean forward = true;
        while (!queue.isEmpty()) {
            LinkedList<Integer> level = new LinkedList<>();
            for (int size = queue.size(); size > 0; size--) {
                TreeNode node = queue.poll();
                if (forward) level.addLast(node.val);
                else level.addFirst(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            result.add(level);
            forward = !forward;
        }
        return result;
    }
}
```

### Code walkthrough
- `addFirst`: Writing right to left.
- `forward = !forward`: Flip once per line.
- `LinkedList`: Constant-time insertion at the front.

### Complexity
- **Time:** O(n)
- **Space:** O(width)

### Common mistakes
1. **Reversing discovery corrupts later levels.** Fix: reverse only output.
2. **Flipping per node is wrong.** Fix: flip per level.
3. **Inserting at an ArrayList's front is quadratic.** Fix: use a linked list.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[ [1] ]` |
| Three | `[1,2,3]` | `[ [1],[3,2] ]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[149 — 102. Binary Tree Level Order Traversal]]: No flip. The key idea there: At the start of each round the queue holds exactly one whole level.
- [[297 — 6. Zigzag Conversion]]: Text. The key idea there: The pattern repeats every 2(rows-1) characters, so each row's indices follow from arithmetic.

### In a real interview
Say it in this order:

1. **Brute force:** Collect levels normally and reverse every second one afterwards.
2. **Insight:** Never change the order of discovery, only where each value is inserted.
3. **Approach in a few words:** Ordinary BFS, alternate writing.
4. **Cost:** time O(n); space O(width).

## 5. Recall
Q: 103. Binary Tree Zigzag Level Order Traversal: which pattern, and what is the insight?
A: Level BFS with direction. Never change the order of discovery, only where each value is inserted.

Q: 103. Binary Tree Zigzag Level Order Traversal: what is the analogy, and what does it stand for?
A: A printer head going both ways: arrive → normal BFS; direction → add first or last.

Q: 103. Binary Tree Zigzag Level Order Traversal: What alternates in zigzag BFS?
A: Only where values are inserted.

Q: 103. Binary Tree Zigzag Level Order Traversal: what are the time and space costs?
A: Time O(n). Space O(width).

@@ 3 | TOPIC | 152 — 106. Construct Binary Tree from Inorder and Postorder Traversal | Medium · Traversal split | SHOULD
# 152 — 106. Construct Binary Tree from Inorder and Postorder Traversal

**Problem 152 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Traversal split**

[Open on LeetCode](https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/)

## 1. Problem Statement
You get `inorder` and `postorder`, the two traversals of the same binary tree whose values are all unique. Construct that tree and return its root.

### Examples
```text
Input:  inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
Output: [3,9,20,null,null,15,7]

Input:  inorder = [-1], postorder = [-1]
Output: [-1]
```

### Constraints
```text
1 <= inorder.length <= 3000
postorder.length == inorder.length
-3000 <= inorder[i], postorder[i] <= 3000
inorder and postorder consist of unique values.
Each value of postorder also appears in inorder.
inorder is guaranteed to be the inorder traversal of the tree.
postorder is guaranteed to be the postorder traversal of the tree.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The boss signs last
The boss signs last, so postorder's final value is the root. The seating row, inorder, shows who is in each wing. Reading the signatures backwards, the right wing's boss comes next.

| In the story | In the solution |
|---|---|
| last signature | root |
| seating | inorder index |

### Pattern signal
**Traversal split.** A backwards pointer gives every root and a map gives every split in O(1).

### Thought process
1. **First idea:** Search inorder for each root, in O(n²).
2. **The insight:** A backwards pointer gives every root and a map gives every split in O(1).

### Optimal approach
1. Map inorder positions.
2. Take the next value from the back.
3. Split.
4. Build right.
5. Build left.

### Dry run
```text
in 9,3,15,20,7  post 9,15,7,20,3
root  left wing  right wing
3     9          15,20,7
20    15         7
```

### Why it works
Reversed postorder is root, right, left, which is exactly the order in which these calls consume values.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private final Map<Integer, Integer> seat = new HashMap<>();
    private int next;

    public TreeNode buildTree(int[] inorder, int[] postorder) {
        seat.clear();
        for (int i = 0; i < inorder.length; i++) seat.put(inorder[i], i);
        next = postorder.length - 1;
        return build(postorder, 0, inorder.length - 1);
    }

    private TreeNode build(int[] postorder, int low, int high) {
        if (low > high) return null;
        TreeNode root = new TreeNode(postorder[next--]);
        int split = seat.get(root.val);
        root.right = build(postorder, split + 1, high);
        root.left = build(postorder, low, split - 1);
        return root;
    }
}
```

### Code walkthrough
- `next--`: The next signature from the end.
- `root.right =`: Right must be built first.
- `seat.get(root.val)`: The split in constant time.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Building left first takes wrong roots.** Fix: build right first.
2. **Scanning for each root is quadratic.** Fix: map indices.
3. **An empty range must consume nothing.** Fix: test it first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1], [1]` | `[1]` |
| Left | `[2,1], [2,1]` | `[1,2]` |
| Right | `[1,2], [2,1]` | `[1,null,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[157 — 105. Construct Binary Tree from Preorder and Inorder Traversal]]: Forward. The key idea there: A forward pointer and an index map give root and split in O(1).
- [[166 — 297. Serialize and Deserialize Binary Tree]]: One traversal. The key idea there: One traversal that includes nulls determines the tree completely.

### In a real interview
Say it in this order:

1. **Brute force:** Search inorder for each root, in O(n²).
2. **Insight:** A backwards pointer gives every root and a map gives every split in O(1).
3. **Approach in a few words:** Backwards roots with indexed splits.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 106. Construct Binary Tree from Inorder and Postorder Traversal: which pattern, and what is the insight?
A: Traversal split. A backwards pointer gives every root and a map gives every split in O(1).

Q: 106. Construct Binary Tree from Inorder and Postorder Traversal: what is the analogy, and what does it stand for?
A: The boss signs last: last signature → root; seating → inorder index.

Q: 106. Construct Binary Tree from Inorder and Postorder Traversal: Why build right first?
A: Reversed postorder is root, right, left.

Q: 106. Construct Binary Tree from Inorder and Postorder Traversal: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 153 — 1376. Time Needed to Inform All Employees | Medium · Weighted tree walk | SHOULD
# 153 — 1376. Time Needed to Inform All Employees

**Problem 153 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Weighted tree walk**

[Open on LeetCode](https://leetcode.com/problems/time-needed-to-inform-all-employees/)

## 1. Problem Statement
A company has n employees forming a tree under `headID`, with `manager[i]` the direct manager of i. Employee i needs `informTime[i]` minutes to inform all direct reports at once, who can then inform theirs. Return the minutes until everyone knows.

### Examples
```text
Input:  n = 1, headID = 0, manager = [-1], informTime = [0]
Output: 0

Input:  n = 6, headID = 2, manager = [2,2,-1,2,2,2], informTime = [0,0,1,0,0,0]
Output: 1
```

### Constraints
```text
1 <= n <= 10^5
0 <= headID < n
manager.length == n
0 <= manager[i] < n
manager[headID] == -1
informTime.length == n
0 <= informTime[i] <= 1000
informTime[i] == 0 if employee i has no subordinates.
It is guaranteed that all the employees can be informed.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A phone tree
The news spreads through a phone tree. A manager's branches talk at the same time, so the company finishes when the slowest chain of calls finishes.

| In the story | In the solution |
|---|---|
| chain | root-to-leaf |
| call length | informTime |

### Pattern signal
**Weighted tree walk.** Times add along a chain and combine by maximum across branches.

### Thought process
1. **First idea:** For every employee climb to the head, repeating work.
2. **The insight:** Times add along a chain and combine by maximum across branches.

### Optimal approach
1. Build lists of reports.
2. Start the head at zero.
3. Add the manager's delay for each report.
4. Keep the latest.

### Dry run
```text
0 takes 2 to tell 1,2. 1 takes 3 to tell 3
person  hears at
1,2     2
3       5
```

### Why it works
Each employee's only path from the head gives the time they hear, and everyone knows at the maximum of those.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numOfMinutes(int n, int headID, int[] manager, int[] informTime) {
        List<List<Integer>> reports = new ArrayList<>();
        for (int i = 0; i < n; i++) reports.add(new ArrayList<>());
        for (int i = 0; i < n; i++) {
            if (manager[i] != -1) reports.get(manager[i]).add(i);
        }
        int[] heard = new int[n];
        int last = 0;
        Deque<Integer> work = new ArrayDeque<>(List.of(headID));
        while (!work.isEmpty()) {
            int boss = work.pop();
            last = Math.max(last, heard[boss]);
            for (int person : reports.get(boss)) {
                heard[person] = heard[boss] + informTime[boss];
                work.push(person);
            }
        }
        return last;
    }
}
```

### Code walkthrough
- `reports.get(manager[i]).add(i)`: Turn the manager links downward.
- `informTime[boss]`: The speaker's time, not the listener's.
- `Math.max`: The slowest chain decides.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Branches run in parallel.** Fix: take the maximum.
2. **The delay belongs to the speaker.** Fix: add the manager's.
3. **A chain of 100,000 overflows recursion.** Fix: use a stack.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `head only` | `0` |
| Star | `boss 1, two reports` | `1` |
| Chain | `2 then 3` | `5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[135 — 104. Maximum Depth of Binary Tree]]: Every delay 1. The key idea there: A node's depth depends only on the depths of its two children.
- [[223 — 743. Network Delay Time]]: A graph. The key idea there: A shortest path uses at most n-1 edges, so that many relaxation rounds suffice.

### In a real interview
Say it in this order:

1. **Brute force:** For every employee climb to the head, repeating work.
2. **Insight:** Times add along a chain and combine by maximum across branches.
3. **Approach in a few words:** The maximum weighted depth.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 1376. Time Needed to Inform All Employees: which pattern, and what is the insight?
A: Weighted tree walk. Times add along a chain and combine by maximum across branches.

Q: 1376. Time Needed to Inform All Employees: what is the analogy, and what does it stand for?
A: A phone tree: chain → root-to-leaf; call length → informTime.

Q: 1376. Time Needed to Inform All Employees: How do parallel branches combine?
A: By maximum.

Q: 1376. Time Needed to Inform All Employees: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 154 — 1448. Count Good Nodes in Binary Tree | Medium · Maximum so far | MUST
# 154 — 1448. Count Good Nodes in Binary Tree

**Problem 154 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Maximum so far**

[Open on LeetCode](https://leetcode.com/problems/count-good-nodes-in-binary-tree/)

## 1. Problem Statement
A node X in a binary tree is good if no node on the path from the root to X has a value greater than X's. Return the number of good nodes.

### Examples
```text
Input:  root = [3,1,4,3,null,1,5]
Output: 4

Input:  root = [3,3,null,4,2]
Output: 3

Input:  root = [1]
Output: 1
```

### Constraints
```text
The number of nodes in the binary tree is in the range [1, 10^5].
Each node's value is between [-10^4, 10^4].
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A hiker's high-point record
A hiker carries the record altitude of the trail so far. A spot is good if it ties or beats the record, and each fork carries its own copy of it downward.

| In the story | In the solution |
|---|---|
| record | high |
| trail | root path |

### Pattern signal
**Maximum so far.** All that matters about the ancestors is their maximum.

### Thought process
1. **First idea:** Scan all ancestors of every node, in O(n·h).
2. **The insight:** All that matters about the ancestors is their maximum.

### Optimal approach
1. Start with the root's value.
2. Count a node at least the record.
3. Pass the larger to both children.
4. Add.

### Dry run
```text
3,1,4,3,null,1,5
node     high  good
3        3     yes
1        3     no
3 below  3     yes
4        3     yes
1 under4 4     no
5        4     yes
Answer 4
```

### Why it works
The value carried is exactly the maximum on the path, so the comparison is the definition of good.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int goodNodes(TreeNode root) {
        return count(root, root.val);
    }

    private int count(TreeNode node, int high) {
        if (node == null) return 0;
        int good = node.val >= high ? 1 : 0;
        int next = Math.max(high, node.val);
        return good + count(node.left, next) + count(node.right, next);
    }
}
```

### Code walkthrough
- `root.val`: The root always qualifies.
- `>= high`: A tie is still good.
- `count(node.left, next)`: Every trail keeps its own record.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Equal values are good.** Fix: compare with at least.
2. **A global record mixes branches.** Fix: pass it down.
3. **Values can be negative.** Fix: start at the root's value.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[-1]` | `1` |
| Equal | `[2,2]` | `2` |
| Smaller | `[3,1]` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[155 — 98. Validate Binary Search Tree]]: Two records. The key idea there: All ancestors' rules compress into one allowed interval.
- [[142 — 112. Path Sum]]: A sum. The key idea there: A branch only needs to know the amount still to be spent.

### In a real interview
Say it in this order:

1. **Brute force:** Scan all ancestors of every node, in O(n·h).
2. **Insight:** All that matters about the ancestors is their maximum.
3. **Approach in a few words:** Pass the path maximum down.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 1448. Count Good Nodes in Binary Tree: which pattern, and what is the insight?
A: Maximum so far. All that matters about the ancestors is their maximum.

Q: 1448. Count Good Nodes in Binary Tree: what is the analogy, and what does it stand for?
A: A hiker's high-point record: record → high; trail → root path.

Q: 1448. Count Good Nodes in Binary Tree: What is passed down for good nodes?
A: The path's maximum.

Q: 1448. Count Good Nodes in Binary Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 155 — 98. Validate Binary Search Tree | Medium · Allowed range | MUST
# 155 — 98. Validate Binary Search Tree

**Problem 155 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Allowed range**

[Open on LeetCode](https://leetcode.com/problems/validate-binary-search-tree/)

## 1. Problem Statement
You get the root of a binary tree. Return true if it is a valid search tree: at every node all values in the left subtree are strictly smaller and all in the right strictly greater.

### Examples
```text
Input:  root = [2,1,3]
Output: true

Input:  root = [5,1,4,null,null,3,6]
Output: false
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 10^4].
-2^31 <= Node.val <= 2^31 - 1
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Fences narrowing a field
Each turn on the way down adds a fence. A node must stand inside all fences set by its ancestors, not only the one its parent set.

| In the story | In the solution |
|---|---|
| fences | low and high |
| turn | child |

### Pattern signal
**Allowed range.** All ancestors' rules compress into one allowed interval.

### Thought process
1. **First idea:** Scan each node's whole subtrees, in O(n²).
2. **The insight:** All ancestors' rules compress into one allowed interval.

### Optimal approach
1. Start with unlimited long bounds.
2. Require strictly inside.
3. Tighten the upper for left.
4. Tighten the lower for right.

### Dry run
```text
5,1,4 with 4 having 3,6
node  range          result
5     open           ok
1     below 5        ok
4     above 5        false
```

### Why it works
The interval at a node is exactly the intersection of every ancestor's rule, so checking it checks the whole definition.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isValidBST(TreeNode root) {
        return inside(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean inside(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;
        return inside(node.left, low, node.val) && inside(node.right, node.val, high);
    }
}
```

### Code walkthrough
- `Long.MIN_VALUE`: Outside the range of any int.
- `node.val <= low`: Equality is not allowed.
- `inside(node.left, low, node.val)`: The parent becomes a new fence.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Children alone miss grandchildren.** Fix: pass bounds.
2. **Equal values are invalid.** Fix: compare strictly.
3. **Int sentinels collide with values.** Fix: use long.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Maximum | `[2147483647]` | `true` |
| Equal | `[1,1]` | `false` |
| Deep | `[5,4,6,null,null,3,7]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[156 — 230. Kth Smallest Element in a BST]]: Inorder rises. The key idea there: An iterative inorder walk produces values in order and can stop early.
- [[161 — 669. Trim a Binary Search Tree]]: Given fences. The key idea there: Order lets one comparison reject a whole side.

### In a real interview
Say it in this order:

1. **Brute force:** Scan each node's whole subtrees, in O(n²).
2. **Insight:** All ancestors' rules compress into one allowed interval.
3. **Approach in a few words:** Pass strict bounds in long.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 98. Validate Binary Search Tree: which pattern, and what is the insight?
A: Allowed range. All ancestors' rules compress into one allowed interval.

Q: 98. Validate Binary Search Tree: what is the analogy, and what does it stand for?
A: Fences narrowing a field: fences → low and high; turn → child.

Q: 98. Validate Binary Search Tree: What constrains a BST node?
A: The interval from all ancestors.

Q: 98. Validate Binary Search Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 156 — 230. Kth Smallest Element in a BST | Medium · Inorder count | MUST
# 156 — 230. Kth Smallest Element in a BST

**Problem 156 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Inorder count**

[Open on LeetCode](https://leetcode.com/problems/kth-smallest-element-in-a-bst/)

## 1. Problem Statement
You get the root of a binary search tree and an integer `k`. Return the kth smallest of all its values, counting from 1.

### Examples
```text
Input:  root = [3,1,4,null,2], k = 1
Output: 1

Input:  root = [5,3,6,2,4,null,null,1], k = 3
Output: 3
```

### Constraints
```text
The number of nodes in the tree is n.
1 <= k <= n <= 10^4
0 <= Node.val <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The kth book on a sorted shelf
The books of a search tree stand sorted when read inorder. Read from the left and stop at the kth, never looking at the others.

| In the story | In the solution |
|---|---|
| reading | inorder |
| stop | k reaches zero |

### Pattern signal
**Inorder count.** An iterative inorder walk produces values in order and can stop early.

### Thought process
1. **First idea:** Collect all values and sort them.
2. **The insight:** An iterative inorder walk produces values in order and can stop early.

### Optimal approach
1. Push the left chain.
2. Pop.
3. Decrease k.
4. Return at zero.
5. Move right.

### Dry run
```text
3,1,4,null,2 k 2
pop  k left
1    1
2    0, return 2
```

### Why it works
Inorder visits values ascending, so the kth pop is the kth smallest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode node = root;
        while (true) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
            node = stack.pop();
            if (--k == 0) return node.val;
            node = node.right;
        }
    }
}
```

### Code walkthrough
- `node = node.left`: The smallest remaining comes first.
- `--k == 0`: This is the kth book.
- `node = node.right`: Continue with the next larger books.

### Complexity
- **Time:** O(h + k)
- **Space:** O(h)

### Common mistakes
1. **Preorder is not sorted.** Fix: use inorder.
2. **k starts at 1.** Fix: decrement before testing.
3. **A pushed node is not visited.** Fix: count on pop.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1], 1` | `1` |
| Last | `[2,1], 2` | `2` |
| Right | `[1,null,2], 2` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[162 — 173. Binary Search Tree Iterator]]: Pausable. The key idea there: An inorder walk can be paused after each pop.
- [[176 — 215. Kth Largest Element in an Array]]: Unordered. The key idea there: You need one boundary, not the full order.

### In a real interview
Say it in this order:

1. **Brute force:** Collect all values and sort them.
2. **Insight:** An iterative inorder walk produces values in order and can stop early.
3. **Approach in a few words:** Iterative inorder with early stop.
4. **Cost:** time O(h + k); space O(h).

## 5. Recall
Q: 230. Kth Smallest Element in a BST: which pattern, and what is the insight?
A: Inorder count. An iterative inorder walk produces values in order and can stop early.

Q: 230. Kth Smallest Element in a BST: what is the analogy, and what does it stand for?
A: The kth book on a sorted shelf: reading → inorder; stop → k reaches zero.

Q: 230. Kth Smallest Element in a BST: Why does inorder find the kth smallest?
A: It is sorted in a BST.

Q: 230. Kth Smallest Element in a BST: what are the time and space costs?
A: Time O(h + k). Space O(h).

@@ 3 | TOPIC | Practice — Review 26 | Re-solve without looking | MUST
# Review 26

Solve on a blank page. No notes, hints or solutions.

## 230. Kth Smallest Element in a BST
Find the kth smallest BST value.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/kth-smallest-element-in-a-bst/)

## 1376. Time Needed to Inform All Employees
Find the time to inform every employee.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/time-needed-to-inform-all-employees/)

## 112. Path Sum
Decide whether a root-to-leaf path has a target sum.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/path-sum/)

## 138. Copy List with Random Pointer
Deep-copy a list with random pointers.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/copy-list-with-random-pointer/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 230 | | | | |
| 1376 | | | | |
| 112 | | | | |
| 138 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[156 — 230. Kth Smallest Element in a BST]]
- [[153 — 1376. Time Needed to Inform All Employees]]
- [[142 — 112. Path Sum]]
- [[118 — 138. Copy List with Random Pointer]]

@@ 3 | TOPIC | 157 — 105. Construct Binary Tree from Preorder and Inorder Traversal | Medium · Traversal split | MUST
# 157 — 105. Construct Binary Tree from Preorder and Inorder Traversal

**Problem 157 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Traversal split**

[Open on LeetCode](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/)

## 1. Problem Statement
You get `preorder` and `inorder`, the two traversals of the same binary tree whose values are all unique. Construct that tree and return its root.

### Examples
```text
Input:  preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
Output: [3,9,20,null,null,15,7]

Input:  preorder = [-1], inorder = [-1]
Output: [-1]
```

### Constraints
```text
1 <= preorder.length <= 3000
inorder.length == preorder.length
-3000 <= preorder[i], inorder[i] <= 3000
preorder and inorder consist of unique values.
Each value of inorder also appears in preorder.
preorder is guaranteed to be the preorder traversal of the tree.
inorder is guaranteed to be the inorder traversal of the tree.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: The boss introduces themself first
The boss introduces themself first, so preorder names the root. The seating row shows who is in each wing, and the next person to speak leads the left wing.

| In the story | In the solution |
|---|---|
| speaker | next preorder value |
| seating | index map |

### Pattern signal
**Traversal split.** A forward pointer and an index map give root and split in O(1).

### Thought process
1. **First idea:** Copy arrays and search at every node, in O(n²).
2. **The insight:** A forward pointer and an index map give root and split in O(1).

### Optimal approach
1. Map inorder positions.
2. Take the next preorder value.
3. Split.
4. Build left.
5. Build right.

### Dry run
```text
pre 3,9,20,15,7  in 9,3,15,20,7
root  left  right
3     9     15,20,7
20    15    7
```

### Why it works
Preorder is root, left, right, precisely the order in which the recursive calls consume values.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private final Map<Integer, Integer> seat = new HashMap<>();
    private int next;

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        seat.clear();
        next = 0;
        for (int i = 0; i < inorder.length; i++) seat.put(inorder[i], i);
        return build(preorder, 0, inorder.length - 1);
    }

    private TreeNode build(int[] preorder, int low, int high) {
        if (low > high) return null;
        TreeNode root = new TreeNode(preorder[next++]);
        int split = seat.get(root.val);
        root.left = build(preorder, low, split - 1);
        root.right = build(preorder, split + 1, high);
        return root;
    }
}
```

### Code walkthrough
- `next++`: The next person to speak.
- `seat.get(root.val)`: Who is left and who is right.
- `root.left =`: Left is listed first.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **An empty range must consume nothing.** Fix: test it before advancing.
2. **An inorder index is not a preorder index.** Fix: keep a separate pointer.
3. **Copying slices is quadratic.** Fix: pass bounds.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1], [1]` | `[1]` |
| Left | `[1,2], [2,1]` | `[1,2]` |
| Right | `[1,2], [1,2]` | `[1,null,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[152 — 106. Construct Binary Tree from Inorder and Postorder Traversal]]: Backwards. The key idea there: A backwards pointer gives every root and a map gives every split in O(1).
- [[140 — 108. Convert Sorted Array to Binary Search Tree]]: Choose the root. The key idea there: The middle value as root gives both balance and search order at once.

### In a real interview
Say it in this order:

1. **Brute force:** Copy arrays and search at every node, in O(n²).
2. **Insight:** A forward pointer and an index map give root and split in O(1).
3. **Approach in a few words:** Preorder names roots, inorder splits.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 105. Construct Binary Tree from Preorder and Inorder Traversal: which pattern, and what is the insight?
A: Traversal split. A forward pointer and an index map give root and split in O(1).

Q: 105. Construct Binary Tree from Preorder and Inorder Traversal: what is the analogy, and what does it stand for?
A: The boss introduces themself first: speaker → next preorder value; seating → index map.

Q: 105. Construct Binary Tree from Preorder and Inorder Traversal: What does each traversal give in reconstruction?
A: Preorder the root, inorder the sides.

Q: 105. Construct Binary Tree from Preorder and Inorder Traversal: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 158 — 96. Unique Binary Search Trees | Medium · Catalan DP | SHOULD
# 158 — 96. Unique Binary Search Trees

**Problem 158 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Catalan DP**

[Open on LeetCode](https://leetcode.com/problems/unique-binary-search-trees/)

## 1. Problem Statement
You get an integer `n`. Return how many structurally different binary search trees have exactly n nodes holding the unique values 1 to n.

### Examples
```text
Input:  n = 3
Output: 5

Input:  n = 1
Output: 1
```

### Constraints
```text
1 <= n <= 19
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Seating a family around each possible head
Choose who sits at the head. Those smaller form the left group and the larger the right, arranging independently, so multiply. Different heads are alternatives, so add.

| In the story | In the solution |
|---|---|
| head | root |
| groups | sizes |

### Pattern signal
**Catalan DP.** The number of shapes depends only on how many nodes a subtree has.

### Thought process
1. **First idea:** Generate every tree, which is exponential.
2. **The insight:** The number of shapes depends only on how many nodes a subtree has.

### Optimal approach
1. Give size zero one way.
2. For each size try every left size.
3. Multiply by the right's ways.
4. Add.

### Dry run
```text
size  sum                  ways
1     1*1                  1
2     1*1 + 1*1            2
3     1*2 + 1*1 + 2*1      5
```

### Why it works
Root choices are disjoint and cover all trees, and every left shape combines with every right shape.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int numTrees(int n) {
        int[] ways = new int[n + 1];
        ways[0] = 1;
        for (int size = 1; size <= n; size++) {
            for (int left = 0; left < size; left++) ways[size] += ways[left] * ways[size - 1 - left];
        }
        return ways[n];
    }
}
```

### Code walkthrough
- `ways[0] = 1`: The empty side has one shape.
- `size - 1 - left`: The root uses one node.
- `+=`: Different roots are alternatives.

### Complexity
- **Time:** O(n²)
- **Space:** O(n)

### Common mistakes
1. **The sides are independent.** Fix: multiply them.
2. **An empty side of zero kills all products.** Fix: use one.
3. **Permutations can share a shape.** Fix: do not use n!.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `1` |
| Two | `2` | `2` |
| Four | `4` | `14` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **95. Generate**: Build them.
- [[076 — 22. Generate Parentheses]]: Also Catalan. The key idea there: Validity of a prefix depends on two counts, so invalid branches are never started.

### In a real interview
Say it in this order:

1. **Brute force:** Generate every tree, which is exponential.
2. **Insight:** The number of shapes depends only on how many nodes a subtree has.
3. **Approach in a few words:** Catalan DP over the root.
4. **Cost:** time O(n²); space O(n).

## 5. Recall
Q: 96. Unique Binary Search Trees: which pattern, and what is the insight?
A: Catalan DP. The number of shapes depends only on how many nodes a subtree has.

Q: 96. Unique Binary Search Trees: what is the analogy, and what does it stand for?
A: Seating a family around each possible head: head → root; groups → sizes.

Q: 96. Unique Binary Search Trees: How do left and right counts combine?
A: Multiply, then add over roots.

Q: 96. Unique Binary Search Trees: what are the time and space costs?
A: Time O(n²). Space O(n).

@@ 3 | TOPIC | 159 — 129. Sum Root to Leaf Numbers | Medium · Path number | SHOULD
# 159 — 129. Sum Root to Leaf Numbers

**Problem 159 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Path number**

[Open on LeetCode](https://leetcode.com/problems/sum-root-to-leaf-numbers/)

## 1. Problem Statement
Every node holds a digit from 0 to 9. Each root-to-leaf path spells a number, so 1, 2, 3 spells 123. Return the sum of all those numbers.

### Examples
```text
Input:  root = [1,2,3]
Output: 25

Input:  root = [4,9,0,5,1]
Output: 1026
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 1000].
0 <= Node.val <= 9
The depth of the tree will not exceed 10.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An odometer being typed
An odometer is being typed as you walk down: each step shifts what you have left and types a digit. Only a leaf prints the number.

| In the story | In the solution |
|---|---|
| shift | times ten |
| exit | leaf |

### Pattern signal
**Path number.** The whole prefix of a path is one integer.

### Thought process
1. **First idea:** Build each path as a string and parse it.
2. **The insight:** The whole prefix of a path is one integer.

### Optimal approach
1. Return zero on null.
2. Extend with times ten plus the digit.
3. Return it at a leaf.
4. Add both children.

### Dry run
```text
1 over 2,3
path  number
1,2   12
1,3   13
sum   25
```

### Why it works
The carried number is the decimal value of the path, and every leaf contributes exactly its number once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int sumNumbers(TreeNode root) {
        return walk(root, 0);
    }

    private int walk(TreeNode node, int number) {
        if (node == null) return 0;
        number = number * 10 + node.val;
        if (node.left == null && node.right == null) return number;
        return walk(node.left, number) + walk(node.right, number);
    }
}
```

### Code walkthrough
- `number * 10 + node.val`: Shift and type.
- `return number`: A leaf prints.
- `return 0`: An absent child adds nothing.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Adding at null counts leaves twice.** Fix: add at leaves.
2. **Digits have place value.** Fix: multiply by ten.
3. **A global number mixes branches.** Fix: pass it down.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zero | `[0]` | `0` |
| Chain | `[1,0]` | `10` |
| One | `[9]` | `9` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[142 — 112. Path Sum]]: A budget. The key idea there: A branch only needs to know the amount still to be spent.
- [[185 — 257. Binary Tree Paths]]: Text. The key idea there: Sibling paths share everything above them, so one builder with an undo step is enough.

### In a real interview
Say it in this order:

1. **Brute force:** Build each path as a string and parse it.
2. **Insight:** The whole prefix of a path is one integer.
3. **Approach in a few words:** Carry times ten plus digit.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 129. Sum Root to Leaf Numbers: which pattern, and what is the insight?
A: Path number. The whole prefix of a path is one integer.

Q: 129. Sum Root to Leaf Numbers: what is the analogy, and what does it stand for?
A: An odometer being typed: shift → times ten; exit → leaf.

Q: 129. Sum Root to Leaf Numbers: When is the number added?
A: At a leaf.

Q: 129. Sum Root to Leaf Numbers: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 160 — 337. House Robber III | Medium · Two-state tree DP | SHOULD
# 160 — 337. House Robber III

**Problem 160 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Two-state tree DP**

[Open on LeetCode](https://leetcode.com/problems/house-robber-iii/)

## 1. Problem Statement
Houses form a binary tree with money in each node. Robbing two directly connected houses on the same night triggers the alarm. Return the most money you can rob without triggering it.

### Examples
```text
Input:  root = [3,2,3,null,3,null,1]
Output: 7

Input:  root = [3,4,5,1,3,null,1]
Output: 9
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 10^4].
0 <= Node.val <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two sealed offers
Each branch hands its parent two sealed offers: the best if you rob my top house and the best if you don't. The parent combines them without looking inside.

| In the story | In the solution |
|---|---|
| offers | take and skip |
| combining | two formulas |

### Pattern signal
**Two-state tree DP.** The only thing linking parent and child is whether each is robbed, two states.

### Thought process
1. **First idea:** Recursively look at children and grandchildren, repeating exponentially.
2. **The insight:** The only thing linking parent and child is whether each is robbed, two states.

### Optimal approach
1. Return zero offers for null.
2. Take adds both children's skips.
3. Skip adds both children's bests.
4. Choose at the root.

### Dry run
```text
3,2,3,null,3,null,1
node     take  skip
leaf 3   3     0
leaf 1   1     0
node 2   2     3
right 3  3     1
root     7     6
```

### Why it works
For each state the children are independent, and using their optimal offers gives this node's optimum.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int rob(TreeNode root) {
        int[] offer = offers(root);
        return Math.max(offer[0], offer[1]);
    }

    // [take this node, skip this node]
    private int[] offers(TreeNode node) {
        if (node == null) return new int[2];
        int[] left = offers(node.left), right = offers(node.right);
        int take = node.val + left[1] + right[1];
        int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        return new int[] {take, skip};
    }
}
```

### Code walkthrough
- `left[1] + right[1]`: A robbed node's children must be skipped.
- `Math.max(left[0], left[1])`: A skipped node's children are free.
- `new int[2]`: Nothing to rob.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Two levels may be skipped.** Fix: do not just alternate.
2. **A skipped node frees its children.** Fix: take their best.
3. **One number loses the state.** Fix: return two.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[4]` | `4` |
| Parent | `[5,1,1]` | `5` |
| Children | `[1,2,2]` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[231 — 198. House Robber]]: A line. The key idea there: The last house is either skipped or taken, and each case leaves one smaller, solved problem.
- [[165 — 124. Binary Tree Maximum Path Sum]]: Report and record. The key idea there: Every path has a highest node, so compute the best path bending at each.

### In a real interview
Say it in this order:

1. **Brute force:** Recursively look at children and grandchildren, repeating exponentially.
2. **Insight:** The only thing linking parent and child is whether each is robbed, two states.
3. **Approach in a few words:** A two-state tree DP.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 337. House Robber III: which pattern, and what is the insight?
A: Two-state tree DP. The only thing linking parent and child is whether each is robbed, two states.

Q: 337. House Robber III: what is the analogy, and what does it stand for?
A: Two sealed offers: offers → take and skip; combining → two formulas.

Q: 337. House Robber III: What does each node return in robbery?
A: Best taking it and best skipping it.

Q: 337. House Robber III: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 161 — 669. Trim a Binary Search Tree | Medium · BST pruning | SHOULD
# 161 — 669. Trim a Binary Search Tree

**Problem 161 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: BST pruning**

[Open on LeetCode](https://leetcode.com/problems/trim-a-binary-search-tree/)

## 1. Problem Statement
You get a binary search tree and bounds `low` and `high`. Remove every node outside the inclusive range without changing the relative structure of the remaining nodes, and return the possibly new root.

### Examples
```text
Input:  root = [1,0,2], low = 1, high = 2
Output: [1,null,2]

Input:  root = [3,0,4,null,2,null,null,1], low = 1, high = 3
Output: [3,2,null,1]
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 10^4].
0 <= Node.val <= 10^4
The value of each node in the tree is unique.
root is guaranteed to be a valid binary search tree.
0 <= low <= high <= 10^4
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Pruning a sorted hedge
You prune a sorted hedge. A branch that is too low has only lower growth on its left, so both go in one cut and only its right side, pruned, remains.

| In the story | In the solution |
|---|---|
| cut | discard a side |
| keep | returned root |

### Pattern signal
**BST pruning.** Order lets one comparison reject a whole side.

### Thought process
1. **First idea:** Delete invalid values one at a time.
2. **The insight:** Order lets one comparison reject a whole side.

### Optimal approach
1. Return null.
2. Replace a too-small node by its trimmed right.
3. A too-large one by its trimmed left.
4. Otherwise trim both.

### Dry run
```text
1,0,2 range 1..2
node  action
1     keep
0     below, null
2     keep
```

### Why it works
Every discarded side is entirely out of range and the kept nodes keep their ancestry.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null) return null;
        if (root.val < low) return trimBST(root.right, low, high);
        if (root.val > high) return trimBST(root.left, low, high);
        root.left = trimBST(root.left, low, high);
        root.right = trimBST(root.right, low, high);
        return root;
    }
}
```

### Code walkthrough
- `trimBST(root.right, low, high)`: Everything to the left is too small as well.
- `root.val > high`: Everything to the right is too large.
- `root.left =`: Attach the trimmed result.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **A bad node can have good descendants.** Fix: return the trimmed side.
2. **Ignoring return values changes nothing.** Fix: assign them.
3. **The bounds are valid values.** Fix: keep equality.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1], 1..1` | `[1]` |
| All | `[1], 2..3` | `[]` |
| Root cut | `[1,null,2], 2..2` | `[2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[148 — 450. Delete Node in a BST]]: One. The key idea there: Only the next larger value can replace a two-child node without breaking the order.
- [[155 — 98. Validate Binary Search Tree]]: Bounds checked. The key idea there: All ancestors' rules compress into one allowed interval.

### In a real interview
Say it in this order:

1. **Brute force:** Delete invalid values one at a time.
2. **Insight:** Order lets one comparison reject a whole side.
3. **Approach in a few words:** Skip impossible sides.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 669. Trim a Binary Search Tree: which pattern, and what is the insight?
A: BST pruning. Order lets one comparison reject a whole side.

Q: 669. Trim a Binary Search Tree: what is the analogy, and what does it stand for?
A: Pruning a sorted hedge: cut → discard a side; keep → returned root.

Q: 669. Trim a Binary Search Tree: What replaces a too-small node?
A: Its trimmed right subtree.

Q: 669. Trim a Binary Search Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 162 — 173. Binary Search Tree Iterator | Medium · Inorder iterator | SHOULD
# 162 — 173. Binary Search Tree Iterator

**Problem 162 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Inorder iterator**

[Open on LeetCode](https://leetcode.com/problems/binary-search-tree-iterator/)

## 1. Problem Statement
Design an iterator over a BST in increasing order with average O(1) next and O(h) memory.

### Examples
```text
Input:  ["BSTIterator", "next", "next", "hasNext", "next", "hasNext", "next", "hasNext", "next", "hasNext"]
        [[[7, 3, 15, null, null, 9, 20]], [], [], [], [], [], [], [], [], []]
Output: [null, 3, 7, true, 9, true, 15, true, 20, false]
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 10^5].
0 <= Node.val <= 10^6
At most 10^5 calls will be made to hasNext, and next.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A paused tour
A tour guide pauses between visitors' requests, holding bookmarks of forks still to read. On next they read the newest and bookmark the left chain of its right side.

| In the story | In the solution |
|---|---|
| pause | between calls |
| bookmarks | stack |

### Pattern signal
**Inorder iterator.** An inorder walk can be paused after each pop.

### Thought process
1. **First idea:** Store all values up front, using O(n) memory.
2. **The insight:** An inorder walk can be paused after each pop.

### Optimal approach
1. Push the root's left chain.
2. On next pop.
3. Push the left chain of its right child.
4. HasNext checks the stack.

### Dry run
```text
7 with 3,15. 15 with 9,20
call  returns  stack top first
init  -        3,7
next  3        7
next  7        9,15
next  9        15
```

### Why it works
The top is always the smallest unvisited node, and every node is pushed and popped once, so next is amortized O(1).

## 3. Java Solution with Explanation
### Code
```java
class BSTIterator {
    private final Deque<TreeNode> marks = new ArrayDeque<>();

    public BSTIterator(TreeNode root) {
        pushLefts(root);
    }

    private void pushLefts(TreeNode node) {
        while (node != null) {
            marks.push(node);
            node = node.left;
        }
    }

    public int next() {
        TreeNode node = marks.pop();
        pushLefts(node.right);
        return node.val;
    }

    public boolean hasNext() {
        return !marks.isEmpty();
    }
}
```

### Code walkthrough
- `pushLefts(root)`: Ready at the smallest value.
- `pushLefts(node.right)`: Prepare the following values.
- `!marks.isEmpty()`: Something is left to read.

### Complexity
- **Time:** O(1) amortized per call
- **Space:** O(h)

### Common mistakes
1. **Storing all nodes breaks the memory follow-up.** Fix: keep one chain.
2. **The next is the leftmost on the right.** Fix: push that whole chain.
3. **hasNext must not advance.** Fix: only inspect the stack.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `1, then false` |
| Left | `[2,1]` | `1,2` |
| Right | `[1,null,2]` | `1,2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[131 — 94. Binary Tree Inorder Traversal]]: Unpaused. The key idea there: A node waits until its whole left side is finished, and the most recent waiter resumes first, which is a stack.
- [[156 — 230. Kth Smallest Element in a BST]]: k calls. The key idea there: An iterative inorder walk produces values in order and can stop early.

### In a real interview
Say it in this order:

1. **Brute force:** Store all values up front, using O(n) memory.
2. **Insight:** An inorder walk can be paused after each pop.
3. **Approach in a few words:** A lazy inorder stack.
4. **Cost:** time O(1) amortized per call; space O(h).

## 5. Recall
Q: 173. Binary Search Tree Iterator: which pattern, and what is the insight?
A: Inorder iterator. An inorder walk can be paused after each pop.

Q: 173. Binary Search Tree Iterator: what is the analogy, and what does it stand for?
A: A paused tour: pause → between calls; bookmarks → stack.

Q: 173. Binary Search Tree Iterator: What is the iterator's stack?
A: The left chain of unvisited ancestors.

Q: 173. Binary Search Tree Iterator: what are the time and space costs?
A: Time O(1) amortized per call. Space O(h).

@@ 3 | TOPIC | Practice — Review 27 | Re-solve without looking | MUST
# Review 27

Solve on a blank page. No notes, hints or solutions.

## 173. Binary Search Tree Iterator
Iterate a BST in sorted order.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/binary-search-tree-iterator/)

## 129. Sum Root to Leaf Numbers
Sum the numbers formed by root-to-leaf paths.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/sum-root-to-leaf-numbers/)

## 450. Delete Node in a BST
Delete a key from a BST.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/delete-node-in-a-bst/)

## 148. Sort List
Sort a linked list in O(n log n).

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/sort-list/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 173 | | | | |
| 129 | | | | |
| 450 | | | | |
| 148 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[162 — 173. Binary Search Tree Iterator]]
- [[159 — 129. Sum Root to Leaf Numbers]]
- [[148 — 450. Delete Node in a BST]]
- [[124 — 148. Sort List]]

@@ 3 | TOPIC | 163 — 538. Convert BST to Greater Tree | Medium · Reverse inorder | SHOULD
# 163 — 538. Convert BST to Greater Tree

**Problem 163 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Reverse inorder**

[Open on LeetCode](https://leetcode.com/problems/convert-bst-to-greater-tree/)

## 1. Problem Statement
You get the root of a binary search tree. Replace each value with the sum of all original values greater than or equal to it, and return the root.

### Examples
```text
Input:  root = [4,1,6,0,2,5,7,null,null,null,3,null,null,null,8]
Output: [30,36,21,36,35,26,15,null,null,null,33,null,null,null,8]

Input:  root = [0,null,1]
Output: [1,null,1]
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 10^4].
-10^4 <= Node.val <= 10^4
All the values in the tree are unique.
root is guaranteed to be a valid binary search tree.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A till counted from the largest note
A cashier adds from the largest note downward and writes the running total on each note. Everything counted before is at least as large.

| In the story | In the solution |
|---|---|
| largest first | right, node, left |
| total | sum |

### Pattern signal
**Reverse inorder.** Inorder backwards visits a search tree in descending order.

### Thought process
1. **First idea:** For every node sum the values at least as large, in O(n²).
2. **The insight:** Inorder backwards visits a search tree in descending order.

### Optimal approach
1. Visit right.
2. Add the value to the sum.
3. Write the sum.
4. Visit left.

### Dry run
```text
values 1,2,3
visit  sum
3      3
2      5
1      6
```

### Why it works
At each visit the sum holds exactly the values greater than or equal to the current one.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private int sum;

    public TreeNode convertBST(TreeNode root) {
        sum = 0;
        visit(root);
        return root;
    }

    private void visit(TreeNode node) {
        if (node == null) return;
        visit(node.right);
        sum += node.val;
        node.val = sum;
        visit(node.left);
    }
}
```

### Code walkthrough
- `visit(node.right)`: The larger values first.
- `node.val = sum`: The total includes this node.
- `sum = 0`: A fresh total per tree.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **Normal inorder sums the smaller values.** Fix: go right first.
2. **A subtree sum misses ancestors.** Fix: keep a running total.
3. **Writing first excludes the node.** Fix: add before writing.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[1]` |
| Two | `[2,1]` | `[2,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[143 — 783. Minimum Distance Between BST Nodes]]: Forward. The key idea there: Inorder of a search tree is sorted, so only consecutive visits matter.
- [[010 — 303. Range Sum Query - Immutable]]: In arrays. The key idea there: The array is frozen, so prepare the totals once.

### In a real interview
Say it in this order:

1. **Brute force:** For every node sum the values at least as large, in O(n²).
2. **Insight:** Inorder backwards visits a search tree in descending order.
3. **Approach in a few words:** A suffix sum over reverse inorder.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 538. Convert BST to Greater Tree: which pattern, and what is the insight?
A: Reverse inorder. Inorder backwards visits a search tree in descending order.

Q: 538. Convert BST to Greater Tree: what is the analogy, and what does it stand for?
A: A till counted from the largest note: largest first → right, node, left; total → sum.

Q: 538. Convert BST to Greater Tree: Which order builds a greater tree?
A: Right, node, left.

Q: 538. Convert BST to Greater Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 164 — 236. Lowest Common Ancestor of a Binary Tree | Medium · Report matches upward | SHOULD
# 164 — 236. Lowest Common Ancestor of a Binary Tree

**Problem 164 – Topic: Trees (DFS & BFS) | Difficulty: Medium | Pattern: Report matches upward**

[Open on LeetCode](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/)

## 1. Problem Statement
You get a binary tree and two of its nodes `p` and `q`. Return their lowest common ancestor: the deepest node having both as descendants, where a node counts as its own descendant.

### Examples
```text
Input:  root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
Output: 3

Input:  root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4
Output: 5

Input:  root = [1,2], p = 1, q = 2
Output: 1
```

### Constraints
```text
The number of nodes in the tree is in the range [2, 10^5].
-10^9 <= Node.val <= 10^9
All Node.val are unique.
p != q
p and q will exist in the tree.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Search parties reporting up
Search parties report up to their manager. A manager hearing "found" from both sides is the meeting point. With one report they pass it up unchanged.

| In the story | In the solution |
|---|---|
| found | non-null |
| both | the answer |

### Pattern signal
**Report matches upward.** Postorder reveals the first place where the two reports join.

### Thought process
1. **First idea:** Find and store both paths from the root.
2. **The insight:** Postorder reveals the first place where the two reports join.

### Optimal approach
1. Return null or a matching node.
2. Search both sides.
3. Return this node if both report.
4. Otherwise pass the one report.

### Dry run
```text
root 3, p 5, q 1
call  returns
5     5
1     1
3     both, 3
```

### Why it works
The first node receiving both is the deepest containing both. If one target is above the other it reports itself, which is the answer.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }
}
```

### Code walkthrough
- `root == p`: A target reports itself.
- `left != null && right != null`: Both sides found someone.
- `? left : right`: Pass the single report up.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **There is no search order.** Fix: search both sides.
2. **Values are not needed.** Fix: compare references.
3. **A single report must not be lost.** Fix: pass it up.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Ancestor | `[1,2], 1, 2` | `1` |
| Siblings | `[1,2,3], 2, 3` | `1` |
| Deep | `[1,2,null,3], 2, 3` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[146 — 235. Lowest Common Ancestor of a Binary Search Tree]]: Signposts. The key idea there: The search order reveals which side each target is on without searching.
- [[113 — 160. Intersection of Two Linked Lists]]: With parents. The key idea there: A+B equals B+A, which cancels the different lengths before the junction.

### In a real interview
Say it in this order:

1. **Brute force:** Find and store both paths from the root.
2. **Insight:** Postorder reveals the first place where the two reports join.
3. **Approach in a few words:** Postorder returns found nodes upward.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 236. Lowest Common Ancestor of a Binary Tree: which pattern, and what is the insight?
A: Report matches upward. Postorder reveals the first place where the two reports join.

Q: 236. Lowest Common Ancestor of a Binary Tree: what is the analogy, and what does it stand for?
A: Search parties reporting up: found → non-null; both → the answer.

Q: 236. Lowest Common Ancestor of a Binary Tree: When is a node the LCA?
A: When both sides report, or it is a target above the other.

Q: 236. Lowest Common Ancestor of a Binary Tree: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 2 | MODULE | Trees — Hard | 2 problems | MUST
# Trees: Hard

Patterns in this group: Gain plus global best, DFS with null markers.

@@ 3 | TOPIC | 165 — 124. Binary Tree Maximum Path Sum | Hard · Gain plus global best | MUST
# 165 — 124. Binary Tree Maximum Path Sum

**Problem 165 – Topic: Trees (DFS & BFS) | Difficulty: Hard | Pattern: Gain plus global best**

[Open on LeetCode](https://leetcode.com/problems/binary-tree-maximum-path-sum/)

## 1. Problem Statement
A path is a sequence of connected nodes using each node at most once. It need not include the root and has at least one node. Return the largest sum of values on any path.

### Examples
```text
Input:  root = [1,2,3]
Output: 6

Input:  root = [-10,9,20,null,null,15,7]
Output: 42
```

### Constraints
```text
The number of nodes in the tree is in the range [1, 3 * 10^4].
-1000 <= Node.val <= 1000
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Profit reports with the option to close a branch
Each branch reports its profit and a loss-making branch is closed, counted as zero. Here you may combine both branches, but to your boss you can offer only one chain.

| In the story | In the solution |
|---|---|
| close | max with 0 |
| offer | one side |

### Pattern signal
**Gain plus global best.** Every path has a highest node, so compute the best path bending at each.

### Thought process
1. **First idea:** Examine paths between all pairs of nodes.
2. **The insight:** Every path has a highest node, so compute the best path bending at each.

### Optimal approach
1. Clamp both children's gains at zero.
2. Record them plus the node.
3. Return the node plus the larger.

### Dry run
```text
-10 with 9,20. 20 with 15,7
node  gains   record  return
9     0,0     9       9
15    0,0     15      15
7     0,0     7       7
20    15,7    42      35
-10   9,35    34      25
Answer 42
```

### Why it works
At each top the best path takes each side's best non-negative chain, and every node is tried as the top.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    private int best;

    public int maxPathSum(TreeNode root) {
        best = Integer.MIN_VALUE;
        gain(root);
        return best;
    }

    private int gain(TreeNode node) {
        if (node == null) return 0;
        int left = Math.max(0, gain(node.left));
        int right = Math.max(0, gain(node.right));
        best = Math.max(best, node.val + left + right);
        return node.val + Math.max(left, right);
    }
}
```

### Code walkthrough
- `Math.max(0,`: Close a losing branch.
- `node.val + left + right`: The path bending here.
- `node.val + Math.max`: Only one chain can be extended.

### Complexity
- **Time:** O(n)
- **Space:** O(h)

### Common mistakes
1. **A fork can't be extended.** Fix: return one side.
2. **All values may be negative.** Fix: start at MIN_VALUE.
3. **The root may be off the best path.** Fix: update everywhere.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Negative | `[-3]` | `-3` |
| Bad child | `[2,-1]` | `2` |
| Both | `[1,2,3]` | `6` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[136 — 543. Diameter of Binary Tree]]: Unweighted. The key idea there: A single height traversal already has both arms at the moment it visits each node.
- [[277 — 53. Maximum Subarray]]: A line. The key idea there: The best range ending here either extends the best one ending just before or starts here, and extending helps only if that earlier sum is non-negative.

### In a real interview
Say it in this order:

1. **Brute force:** Examine paths between all pairs of nodes.
2. **Insight:** Every path has a highest node, so compute the best path bending at each.
3. **Approach in a few words:** Return one arm, record two, clamp.
4. **Cost:** time O(n); space O(h).

## 5. Recall
Q: 124. Binary Tree Maximum Path Sum: which pattern, and what is the insight?
A: Gain plus global best. Every path has a highest node, so compute the best path bending at each.

Q: 124. Binary Tree Maximum Path Sum: what is the analogy, and what does it stand for?
A: Profit reports with the option to close a branch: close → max with 0; offer → one side.

Q: 124. Binary Tree Maximum Path Sum: Why clamp child gains?
A: A negative branch is better omitted.

Q: 124. Binary Tree Maximum Path Sum: what are the time and space costs?
A: Time O(n). Space O(h).

@@ 3 | TOPIC | 166 — 297. Serialize and Deserialize Binary Tree | Hard · DFS with null markers | MUST
# 166 — 297. Serialize and Deserialize Binary Tree

**Problem 166 – Topic: Trees (DFS & BFS) | Difficulty: Hard | Pattern: DFS with null markers**

[Open on LeetCode](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/)

## 1. Problem Statement
Design an algorithm that serializes a binary tree into a string and deserializes that string into exactly the same tree. The format is your choice.

### Examples
```text
Input:  root = [1,2,3,null,null,4,5]
Output: [1,2,3,null,null,4,5]

Input:  root = []
Output: []
```

### Constraints
```text
The number of nodes in the tree is in the range [0, 10^4].
-1000 <= Node.val <= 1000
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Assembly instructions with "nothing here"
Assembly instructions list parts in visiting order and write "nothing here" for each empty socket. The builder follows with a finger and never has to guess the shape.

| In the story | In the solution |
|---|---|
| marker | # |
| reader's finger | token index |

### Pattern signal
**DFS with null markers.** One traversal that includes nulls determines the tree completely.

### Thought process
1. **First idea:** Store two traversals, which needs unique values.
2. **The insight:** One traversal that includes nulls determines the tree completely.

### Optimal approach
1. Write node, left, right with null markers.
2. Split the text.
3. Read in the same order.
4. Return null at a marker.

### Dry run
```text
1 with 2,3. 3 with 4,5
text: 1,2,#,#,3,4,#,#,5,#,#
read  builds
1     root
2,#,# left leaf
3...  right with 4 and 5
```

### Why it works
Each reading call consumes exactly the tokens its writing call produced, with markers ending every subtree unambiguously.

## 3. Java Solution with Explanation
### Code
```java
class Codec {
    private int next;

    public String serialize(TreeNode root) {
        StringBuilder text = new StringBuilder();
        write(root, text);
        return text.toString();
    }

    private void write(TreeNode node, StringBuilder text) {
        if (node == null) {
            text.append("#,");
            return;
        }
        text.append(node.val).append(',');
        write(node.left, text);
        write(node.right, text);
    }

    public TreeNode deserialize(String data) {
        next = 0;
        return read(data.split(","));
    }

    private TreeNode read(String[] tokens) {
        String token = tokens[next++];
        if (token.equals("#")) return null;
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = read(tokens);
        node.right = read(tokens);
        return node;
    }
}
```

### Code walkthrough
- `append("#,")`: An empty socket is written down.
- `split(",")`: Separators keep multi-digit and negative values apart.
- `next++`: One finger shared by all calls.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Without null markers shapes are ambiguous.** Fix: write them.
2. **Without separators 1,2 reads as 12.** Fix: add commas.
3. **A copied index rereads tokens.** Fix: share one.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| Negative | `[-1]` | `[-1]` |
| Equal | `[1,1]` | `[1,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[157 — 105. Construct Binary Tree from Preorder and Inorder Traversal]]: No markers. The key idea there: A forward pointer and an index map give root and split in O(1).
- **271. Encode Strings**: Self-describing text.

### In a real interview
Say it in this order:

1. **Brute force:** Store two traversals, which needs unique values.
2. **Insight:** One traversal that includes nulls determines the tree completely.
3. **Approach in a few words:** Preorder with null markers is self-delimiting.
4. **Cost:** time O(n); space O(n).

## 5. Recall
Q: 297. Serialize and Deserialize Binary Tree: which pattern, and what is the insight?
A: DFS with null markers. One traversal that includes nulls determines the tree completely.

Q: 297. Serialize and Deserialize Binary Tree: what is the analogy, and what does it stand for?
A: Assembly instructions with "nothing here": marker → #; reader's finger → token index.

Q: 297. Serialize and Deserialize Binary Tree: Why serialize nulls?
A: They fix where every subtree ends.

Q: 297. Serialize and Deserialize Binary Tree: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | Practice — Pattern summary: Trees (DFS & BFS) | Key patterns of the topic | MUST
# Pattern summary: Trees (DFS & BFS)

Decide what each node needs from its parent and what it returns. Trust the call on each child. Use BFS for levels and inorder for sorted BST values.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Inorder DFS | Return the inorder traversal of a tree. (94) | Visiting before the left subtree |
| Preorder DFS | Return the preorder traversal of a tree. (144) | Missing the null base case |
| Postorder DFS | Return the postorder traversal of a tree. (145) | Recording a parent early |
| Structural DFS | Mirror a binary tree. (226) | Overwriting a child before saving it |
| Bottom-up DFS | Return the height of a tree in nodes. (104) | Counting edges |
| Height plus global best | Find the longest path in edges between any nodes. (543) | Assuming it crosses the root |
| Height check | Decide whether every node is height-balanced. (110) | Recomputing heights makes it O(n²) |
| Parallel DFS | Decide whether two trees are identical. (100, 617) | Comparing values only |
| Match at every node | Decide whether one tree is a subtree of another. (572) | Accepting a match that stops before the leaves |
| Middle as root | Build a balanced BST from a sorted array. (108) | Off-by-one ranges |
| Root-to-leaf state | Decide whether a root-to-leaf path has a target sum. (112) | Stopping at an inner node |
| Inorder previous | Find the minimum difference between BST values. (783) | Comparing only parent and child |
| Mirror DFS | Decide whether a tree is symmetric. (101) | Comparing the same sides |
| Use built next links | Connect each node to its right neighbor in a perfect tree. (116) | Applying it to imperfect trees |
| BST split | Find the lowest common ancestor in a BST. (235) | Ignoring the ordering |
| BST descent | Insert a value into a BST. (701) | An empty tree |
| BST deletion | Delete a key from a BST. (450) | Not removing the replacement node |
| Level BFS | Return tree values level by level. (102) | Reading a changing queue size |
| Last in level | Return what is visible from the right. (199) | Following only right children |
| Level BFS with direction | Return levels in alternating directions. (103) | Reversing child discovery too |
| Traversal split | Build a tree from inorder and postorder. (106, 105) | Wrong subtree order |
| Weighted tree walk | Find the time to inform every employee. (1376) | Summing parallel branches |
| Maximum so far | Count nodes with no larger ancestor. (1448) | Excluding equality |
| Allowed range | Validate a binary search tree. (98) | Checking only direct children |
| Inorder count | Find the kth smallest BST value. (230) | Visiting preorder |
| Catalan DP | Count structurally unique BSTs with n values. (96) | Adding instead of multiplying |
| Path number | Sum the numbers formed by root-to-leaf paths. (129) | Adding at a null child counts leaves twice |
| Two-state tree DP | Maximize robbery on a tree with no adjacent nodes. (337) | Assuming alternate levels are best |
| BST pruning | Keep only BST values in a range. (669) | Dropping a whole subtree with valid values |
| Inorder iterator | Iterate a BST in sorted order. (173) | Visiting out of order |
| Reverse inorder | Replace each BST value by the sum of values at least it. (538) | Normal inorder gives the opposite sum |
| Report matches upward | Find the lowest common ancestor in a binary tree. (236) | Using BST ordering |
| Gain plus global best | Find the maximum path sum in a tree. (124) | Returning both sides to the parent |
| DFS with null markers | Serialize and restore any binary tree. (297) | Omitting nulls loses the shape |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| height, balance | departments reporting up | postorder | 104, 110 |
| best path anywhere | report one arm, record both | global best | 543, 124 |
| root to leaf | luggage carried down | downward state | 112, 129, 1448 |
| level, view | a lift by floors | BFS | 102, 103, 199 |
| BST, kth, gap | reading a bookshelf left to right | inorder | 230, 783, 538 |
| BST, locate | signposts | descent | 235, 701, 450, 669 |
| rebuild | the boss is named first or last | split | 105, 106, 297 |
| take or skip on a tree | two offers from each branch | tree DP | 337 |

## Which traversal do I pick?

1. **Children needed before the parent?** Postorder.
2. **Parent's information needed first?** Preorder, with parameters.
3. **Sorted BST values?** Inorder.
4. **Depth by depth?** BFS, saving the queue size.
5. **Always decide first** what null returns.

Q: Which signals point to Trees (DFS & BFS)?
A: depth; path; ancestor; level; BST.

Q: What is the core idea of Trees (DFS & BFS)?
A: Decide what each node needs from its parent and what it returns. Trust the call on each child. Use BFS for levels and inorder for sorted BST values.
