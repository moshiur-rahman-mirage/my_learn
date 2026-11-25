@@ 1 | MODULE | Phase 6 — Linked List | relink; middle; cycle; nth from the end | MUST
# Phase 6: Linked List

Draw boxes and arrows. Save an arrow before you overwrite it, use a dummy node when the head can change, and use fast and slow pointers for positions.

**Signals:** relink; middle; cycle; nth from the end.

## The big analogy: a treasure hunt

Each clue tells you only where the next clue is. You can't jump to clue 50, but you can insert a clue by changing one note. The danger is the same every time: **overwrite a note before copying it and the rest of the hunt is lost**.

| Tool | Picture | Meet it in |
|---|---|---|
| Save, then rewire | photograph the clue before editing it | 206, 24 |
| Dummy node | a "start here" sign before the first clue | 203, 19, 21 |
| Fast and slow | hare and tortoise | 876, 141, 287 |
| Cut, fix, rejoin | repairing a section of train | 143, 92, 25 |
| Map beside the list | an address book for copies | 138 |

## Template
```java
ListNode previous = null, current = head;
while (current != null) {
    ListNode next = current.next; // save
    current.next = previous;      // rewire
    previous = current;
    current = next;
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Linked List — Easy | 8 problems | MUST
# Linked List: Easy

Patterns in this group: Pointer reversal, Dummy and tail, Middle and reverse, Unlinking, Fast and slow, Path switching, Floyd cycle.

@@ 3 | TOPIC | 107 — 206. Reverse Linked List | Easy · Pointer reversal | MUST
# 107 — 206. Reverse Linked List

**Problem 107 – Topic: Linked List | Difficulty: Easy | Pattern: Pointer reversal**

[Open on LeetCode](https://leetcode.com/problems/reverse-linked-list/)

## 1. Problem Statement
You get the head of a singly linked list. Reverse the list by changing its links and return the new head. Follow-up: do it both iteratively and recursively.

### Examples
```text
Input:  head = [1,2,3,4,5]
Output: [5,4,3,2,1]

Input:  head = [1,2]
Output: [2,1]

Input:  head = []
Output: []
```

### Constraints
```text
The number of nodes in the list is the range [0, 5000].
-5000 <= Node.val <= 5000
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Turning one-way signs around
You walk a road turning every one-way sign to point behind you. Before turning a sign, look where it pointed, or you won't know where to walk next.

| In the story | In the solution |
|---|---|
| looking first | `next = current.next` |
| behind you | `previous` |

### Pattern signal
**Pointer reversal.** A reversal changes nothing but each arrow's direction.

### Thought process
1. **First idea:** Copy values into an array and write them back, using O(n) space.
2. **The insight:** A reversal changes nothing but each arrow's direction.

### Optimal approach
1. Start previous at null.
2. Save the next node.
3. Point the current back.
4. Move both forward.
5. Return previous.

### Dry run
```text
list = 1,2,3
current  saved  reversed part  rest
1        2      1              2,3
2        3      2,1            3
3        null   3,2,1          empty
```

### Why it works
The processed part is always completely reversed with previous as its head, while current heads the untouched rest.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode previous = null, current = head;
        while (current != null) {
            ListNode next = current.next; // look first
            current.next = previous;      // turn the sign
            previous = current;
            current = next;
        }
        return previous;
    }
}
```

### Code walkthrough
- `previous = null`: The old head becomes the tail, pointing to nothing.
- `current.next = previous`: One sign is turned.
- `return previous`: Current has walked off the end, so previous is the head.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Overwriting next first loses the rest of the list.** Fix: save it before rewiring.
2. **The old head is the tail at the end.** Fix: return previous.
3. **Stopping at current.next leaves one arrow unturned.** Fix: loop until current is null.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[1]` |
| Two | `[1,2]` | `[2,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[127 — 92. Reverse Linked List II]]: A section. The key idea there: Repeated insertion at the front reverses a range without disconnecting it.
- [[130 — 25. Reverse Nodes in k-Group]]: Repeated sections. The key idea there: Each group is an ordinary reversal whose previous starts at the node after the group.

### In a real interview
Say it in this order:

1. **Brute force:** Copy values into an array and write them back, using O(n) space.
2. **Insight:** A reversal changes nothing but each arrow's direction.
3. **Approach in a few words:** I redirect each link, saving the successor first.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 206. Reverse Linked List: which pattern, and what is the insight?
A: Pointer reversal. A reversal changes nothing but each arrow's direction.

Q: 206. Reverse Linked List: what is the analogy, and what does it stand for?
A: Turning one-way signs around: looking first → `next = current.next`; behind you → `previous`.

Q: 206. Reverse Linked List: What must be saved before rewiring?
A: The current node's original next.

Q: 206. Reverse Linked List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 108 — 21. Merge Two Sorted Lists | Easy · Dummy and tail | MUST
# 108 — 21. Merge Two Sorted Lists

**Problem 108 – Topic: Linked List | Difficulty: Easy | Pattern: Dummy and tail**

[Open on LeetCode](https://leetcode.com/problems/merge-two-sorted-lists/)

## 1. Problem Statement
You get the heads of two linked lists, each sorted in non-decreasing order. Splice their nodes into one sorted list and return its head.

### Examples
```text
Input:  list1 = [1,2,4], list2 = [1,3,4]
Output: [1,1,2,3,4,4]

Input:  list1 = [], list2 = []
Output: []

Input:  list1 = [], list2 = [0]
Output: [0]
```

### Constraints
```text
The number of nodes in both lists is in the range [0, 50].
-100 <= Node.val <= 100
Both list1 and list2 are sorted in non-decreasing order.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A zip with a starter tab
Two halves of a zip close: at each step the smaller front tooth joins. A starter tab, the dummy, gives the very first tooth something to attach to.

| In the story | In the solution |
|---|---|
| starter tab | dummy node |
| closed end | `tail` |

### Pattern signal
**Dummy and tail.** The smallest remaining node is always one of the two current heads.

### Thought process
1. **First idea:** Collect all values and sort them.
2. **The insight:** The smallest remaining node is always one of the two current heads.

### Optimal approach
1. Create a dummy.
2. Attach the smaller head.
3. Advance that list and the tail.
4. Attach the remainder.

### Dry run
```text
a = 1,2,4  b = 1,3,4
pick    merged
1 of a  1
1 of b  1,1
2       1,1,2
3       1,1,2,3
4 of a  1,1,2,3,4
rest    1,1,2,3,4,4
```

### Why it works
The tail always ends the smallest nodes in order, and the smaller of two sorted heads is the next smallest overall.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0), tail = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }
        tail.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }
}
```

### Code walkthrough
- `new ListNode(0)`: No special case for the first node.
- `list1.val <= list2.val`: Taking the first list on ties keeps it stable.
- `tail.next = (list1 != null)`: The remainder is already sorted and linked.

### Complexity
- **Time:** O(n + m)
- **Space:** O(1)

### Common mistakes
1. **The dummy is not part of the answer.** Fix: return its next.
2. **One list usually has nodes left.** Fix: attach the remainder.
3. **A tail that never moves overwrites links.** Fix: advance it each round.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Both empty | `[], []` | `[]` |
| One empty | `[], [0]` | `[0]` |
| Tie | `[1], [1]` | `[1,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[129 — 23. Merge k Sorted Lists]]: A heap of fronts. The key idea there: You repeatedly need the minimum of k items of which only one changes, which is a heap.
- [[040 — 88. Merge Sorted Array]]: From the back. The key idea there: The free space is at the back, and so are both maxima.

### In a real interview
Say this: *"A dummy and tail merge by relinking in constant space."*

## 5. Recall
Q: 21. Merge Two Sorted Lists: which pattern, and what is the insight?
A: Dummy and tail. The smallest remaining node is always one of the two current heads.

Q: 21. Merge Two Sorted Lists: what is the analogy, and what does it stand for?
A: A zip with a starter tab: starter tab → dummy node; closed end → `tail`.

Q: 21. Merge Two Sorted Lists: Why use a dummy?
A: The first node then attaches like every other.

Q: 21. Merge Two Sorted Lists: what are the time and space costs?
A: Time O(n + m). Space O(1).

@@ 3 | TOPIC | Practice — Review 18 | Re-solve without looking | MUST
# Review 18

Solve on a blank page. No notes, hints or solutions.

## 21. Merge Two Sorted Lists
Merge two sorted linked lists.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/merge-two-sorted-lists/)

## 410. Split Array Largest Sum
Split into k parts minimizing the largest sum.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/split-array-largest-sum/)

## 540. Single Element in a Sorted Array
Find the single value in a sorted array of pairs in O(log n).

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/single-element-in-a-sorted-array/)

## 225. Implement Stack using Queues
Implement a stack using a queue.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/implement-stack-using-queues/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 21 | | | | |
| 410 | | | | |
| 540 | | | | |
| 225 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[108 — 21. Merge Two Sorted Lists]]
- [[105 — 410. Split Array Largest Sum]]
- [[094 — 540. Single Element in a Sorted Array]]
- [[070 — 225. Implement Stack using Queues]]

@@ 3 | TOPIC | 109 — 234. Palindrome Linked List | Easy · Middle and reverse | NICE
# 109 — 234. Palindrome Linked List

**Problem 109 – Topic: Linked List | Difficulty: Easy | Pattern: Middle and reverse**

[Open on LeetCode](https://leetcode.com/problems/palindrome-linked-list/)

## 1. Problem Statement
You get the head of a singly linked list. Return true if its values read the same forwards and backwards. Follow-up: use O(n) time and O(1) space.

### Examples
```text
Input:  head = [1,2,2,1]
Output: true

Input:  head = [1,2]
Output: false
```

### Constraints
```text
The number of nodes in the list is in the range [1, 10^5].
0 <= Node.val <= 9
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Folding paper in half
Fold a strip of paper in half: find the fold, flip the back half over and hold it against the front. Every mark must land on an equal mark.

| In the story | In the solution |
|---|---|
| fold | the middle |
| flip | reversal |

### Pattern signal
**Middle and reverse.** You can't walk backwards, but you can reverse the half that needs reading backwards.

### Thought process
1. **First idea:** Copy to an array and use two pointers, with O(n) space.
2. **The insight:** You can't walk backwards, but you can reverse the half that needs reading backwards.

### Optimal approach
1. Find the middle at double speed.
2. Reverse from there.
3. Compare both halves.
4. Fail at a difference.

### Dry run
```text
list = 1,2,2,1
middle         second 2
reversed half  1,2
compare        1 and 1, 2 and 2: true
```

### Why it works
Reversal turns each mirrored pair into nodes at the same position of both halves. An odd middle is harmless.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isPalindrome(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode back = null;
        while (slow != null) {
            ListNode next = slow.next;
            slow.next = back;
            back = slow;
            slow = next;
        }
        while (back != null) {
            if (head.val != back.val) return false;
            head = head.next;
            back = back.next;
        }
        return true;
    }
}
```

### Code walkthrough
- `fast.next.next`: Slow stops at the fold.
- `slow.next = back`: Flip the back half.
- `while (back != null)`: The flipped half decides when to stop.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Equal values can be different nodes.** Fix: compare values.
2. **Reversing the whole list destroys the original.** Fix: reverse only the second half.
3. **The input is modified.** Fix: reverse again if the caller needs it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `true` |
| Two | `[1,2]` | `false` |
| Odd | `[1,2,1]` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[115 — 143. Reorder List]]: Weave the halves. The key idea there: The wanted order is the first half forwards woven with the second half backwards.
- [[036 — 125. Valid Palindrome]]: Arrays walk backwards. The key idea there: A palindrome is only a claim about mirrored pairs, and those can be compared in place.

### In a real interview
Say this: *"I find the middle, reverse the second half and compare in constant space."*

## 5. Recall
Q: 234. Palindrome Linked List: which pattern, and what is the insight?
A: Middle and reverse. You can't walk backwards, but you can reverse the half that needs reading backwards.

Q: 234. Palindrome Linked List: what is the analogy, and what does it stand for?
A: Folding paper in half: fold → the middle; flip → reversal.

Q: 234. Palindrome Linked List: Why reverse the second half?
A: To read it from the end toward the middle.

Q: 234. Palindrome Linked List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 110 — 203. Remove Linked List Elements | Easy · Unlinking | NICE
# 110 — 203. Remove Linked List Elements

**Problem 110 – Topic: Linked List | Difficulty: Easy | Pattern: Unlinking**

[Open on LeetCode](https://leetcode.com/problems/remove-linked-list-elements/)

## 1. Problem Statement
You get the head of a linked list and an integer `val`. Remove every node whose value equals `val` and return the new head, which may be empty.

### Examples
```text
Input:  head = [1,2,6,3,4,5,6], val = 6
Output: [1,2,3,4,5]

Input:  head = [], val = 1
Output: []

Input:  head = [7,7,7,7], val = 7
Output: []
```

### Constraints
```text
The number of nodes in the list is in the range [0, 10^4].
1 <= Node.val <= 50
0 <= val <= 50
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A guide for a chain of climbers
Climbers are roped in a line and a guide who is not one of them holds the front. To remove a climber the one before clips to the one after, then checks the new neighbor before stepping on.

| In the story | In the solution |
|---|---|
| guide | dummy |
| person before | `previous` |

### Pattern signal
**Unlinking.** Unlinking needs a predecessor, and a dummy gives even the head one.

### Thought process
1. **First idea:** Build a new list of the wanted values.
2. **The insight:** Unlinking needs a predecessor, and a dummy gives even the head one.

### Optimal approach
1. Put a dummy before the head.
2. Inspect the node after previous.
3. Bypass a match.
4. Otherwise advance.
5. Return the dummy's next.

### Dry run
```text
list = 1,6,6,2  val = 6
previous  next  action
dummy     1     advance
1         6     bypass
1         6     bypass
1         2     advance, result 1,2
```

### Why it works
Everything up to previous is clean, and previous only moves onto a node that has been checked.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(0, head), previous = dummy;
        while (previous.next != null) {
            if (previous.next.val == val) previous.next = previous.next.next;
            else previous = previous.next;
        }
        return dummy.next;
    }
}
```

### Code walkthrough
- `new ListNode(0, head)`: The guide in front of everyone.
- `previous.next = previous.next.next`: Clip past the removed climber.
- `else previous`: Step only onto a climber who stays.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Advancing after a removal skips consecutive matches.** Fix: stay in place.
2. **The head may be removed.** Fix: return the dummy's next.
3. **A lone current pointer has no predecessor.** Fix: inspect previous.next.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| All | `[7,7], 7` | `[]` |
| Empty | `[], 1` | `[]` |
| Head | `[1,2], 1` | `[2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[111 — 83. Remove Duplicates from Sorted List]]: The head stays. The key idea there: Sorting puts all duplicates next to each other, so one comparison finds each.
- [[005 — 27. Remove Element]]: Arrays overwrite. The key idea there: Order is free, so a single swap can replace a whole shift.

### In a real interview
Say it in this order:

1. **Brute force:** Build a new list of the wanted values.
2. **Insight:** Unlinking needs a predecessor, and a dummy gives even the head one.
3. **Approach in a few words:** A dummy gives every node a predecessor for unlinking.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 203. Remove Linked List Elements: which pattern, and what is the insight?
A: Unlinking. Unlinking needs a predecessor, and a dummy gives even the head one.

Q: 203. Remove Linked List Elements: what is the analogy, and what does it stand for?
A: A guide for a chain of climbers: guide → dummy; person before → `previous`.

Q: 203. Remove Linked List Elements: When does previous advance?
A: Only when its successor is kept.

Q: 203. Remove Linked List Elements: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 111 — 83. Remove Duplicates from Sorted List | Easy · Unlinking | NICE
# 111 — 83. Remove Duplicates from Sorted List

**Problem 111 – Topic: Linked List | Difficulty: Easy | Pattern: Unlinking**

[Open on LeetCode](https://leetcode.com/problems/remove-duplicates-from-sorted-list/)

## 1. Problem Statement
You get the head of a sorted linked list. Delete nodes so that each value appears only once, and return the list, still sorted.

### Examples
```text
Input:  head = [1,1,2]
Output: [1,2]

Input:  head = [1,1,2,3,3]
Output: [1,2,3]
```

### Constraints
```text
The number of nodes in the list is in the range [0, 300].
-100 <= Node.val <= 100
The list is guaranteed to be sorted in ascending order.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A host removing echoes
In a sorted line identical twins stand directly behind one another. Stand beside a person and keep sending away the one behind until it is someone different.

| In the story | In the solution |
|---|---|
| echo | an equal next |
| stay | don't advance |

### Pattern signal
**Unlinking.** Sorting puts all duplicates next to each other, so one comparison finds each.

### Thought process
1. **First idea:** Remember seen values in a set, using extra space.
2. **The insight:** Sorting puts all duplicates next to each other, so one comparison finds each.

### Optimal approach
1. Compare the current with its next.
2. Bypass when equal.
3. Otherwise advance.

### Dry run
```text
list = 1,1,1,2
current  next  result
1        1     1,1,2
1        1     1,2
1        2     advance
```

### Why it works
The list through the current node has no repeats, and a later repeat of it would have to be adjacent.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode current = head;
        while (current != null && current.next != null) {
            if (current.val == current.next.val) current.next = current.next.next;
            else current = current.next;
        }
        return head;
    }
}
```

### Code walkthrough
- `current.next != null`: There is still someone behind.
- `current.next = current.next.next`: Send the twin away.
- `return head`: The first node always stays.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Advancing after a removal leaves triples.** Fix: stay in place.
2. **All copies of a value must not vanish.** Fix: keep the first.
3. **Unsorted duplicates are not adjacent.** Fix: use a set for that case.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| All | `[1,1,1]` | `[1]` |
| None | `[1,2]` | `[1,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[110 — 203. Remove Linked List Elements]]: The head can go. The key idea there: Unlinking needs a predecessor, and a dummy gives even the head one.
- [[042 — 26. Remove Duplicates from Sorted Array]]: Slow and fast. The key idea there: In sorted data a repeat can only equal the last kept value.

### In a real interview
Say this: *"Sorted duplicates are adjacent, so I relink in one pass."*

## 5. Recall
Q: 83. Remove Duplicates from Sorted List: which pattern, and what is the insight?
A: Unlinking. Sorting puts all duplicates next to each other, so one comparison finds each.

Q: 83. Remove Duplicates from Sorted List: what is the analogy, and what does it stand for?
A: A host removing echoes: echo → an equal next; stay → don't advance.

Q: 83. Remove Duplicates from Sorted List: Why is one neighbor check enough?
A: Sorting puts equal values together.

Q: 83. Remove Duplicates from Sorted List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 112 — 876. Middle of the Linked List | Easy · Fast and slow | NICE
# 112 — 876. Middle of the Linked List

**Problem 112 – Topic: Linked List | Difficulty: Easy | Pattern: Fast and slow**

[Open on LeetCode](https://leetcode.com/problems/middle-of-the-linked-list/)

## 1. Problem Statement
You get the head of a non-empty linked list. Return its middle node. If there are two middle nodes, return the second one.

### Examples
```text
Input:  head = [1,2,3,4,5]
Output: [3,4,5]

Input:  head = [1,2,3,4,5,6]
Output: [4,5,6]
```

### Constraints
```text
The number of nodes in the list is in the range [1, 100].
1 <= Node.val <= 100
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Hare and tortoise
A hare runs twice as fast as a tortoise from the same start. When the hare finishes, the tortoise is halfway, with nobody measuring the road.

| In the story | In the solution |
|---|---|
| hare | `fast` |
| tortoise | `slow` |

### Pattern signal
**Fast and slow.** A speed ratio of two measures half without knowing the length.

### Thought process
1. **First idea:** Count the nodes, then walk half of them.
2. **The insight:** A speed ratio of two measures half without knowing the length.

### Optimal approach
1. Start both at the head.
2. Move one by one and the other by two.
3. Stop when fast can't.
4. Return slow.

### Dry run
```text
list = 1,2,3,4
step  slow  fast
0     1     1
1     2     3
2     3     null, return 3
```

### Why it works
After t steps slow is at t and fast at 2t, so slow is at half when fast reaches the end.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode middleNode(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }
}
```

### Code walkthrough
- `fast != null`: An even length ends on this check.
- `fast.next != null`: An odd length ends on this check.
- `return slow`: For two middles it is the second.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Testing fast.next first can crash.** Fix: test fast first.
2. **Starting fast ahead returns the first middle.** Fix: start both at the head.
3. **The answer is a node.** Fix: do not return its value.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `[1]` |
| Two | `[1,2]` | `[2]` |
| Odd | `[1,2,3]` | `[2,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[114 — 141. Linked List Cycle]]: The hare laps. The key idea there: A speed difference of exactly one closes any gap around a loop.
- [[117 — 19. Remove Nth Node From End of List]]: A fixed gap. The key idea there: A constant gap converts "from the end" into a position found in one pass.

### In a real interview
Say it in this order:

1. **Brute force:** Count the nodes, then walk half of them.
2. **Insight:** A speed ratio of two measures half without knowing the length.
3. **Approach in a few words:** Double speed finds half in one pass.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 876. Middle of the Linked List: which pattern, and what is the insight?
A: Fast and slow. A speed ratio of two measures half without knowing the length.

Q: 876. Middle of the Linked List: what is the analogy, and what does it stand for?
A: Hare and tortoise: hare → `fast`; tortoise → `slow`.

Q: 876. Middle of the Linked List: Which middle results when both start at head?
A: The second on even length.

Q: 876. Middle of the Linked List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 113 — 160. Intersection of Two Linked Lists | Easy · Path switching | NICE
# 113 — 160. Intersection of Two Linked Lists

**Problem 113 – Topic: Linked List | Difficulty: Easy | Pattern: Path switching**

[Open on LeetCode](https://leetcode.com/problems/intersection-of-two-linked-lists/)

## 1. Problem Statement
You get the heads of two singly linked lists without cycles. Return the node at which they join, compared by identity and not by value, or null if they never do. The lists must keep their structure. Follow-up: O(m + n) time and O(1) space.

### Examples
```text
Input:  intersectVal = 8, listA = [4,1,8,4,5], listB = [5,6,1,8,4,5], skipA = 2, skipB = 3
Output: Intersected at '8'

Input:  intersectVal = 2, listA = [1,9,1,2,4], listB = [3,2,4], skipA = 3, skipB = 1
Output: Intersected at '2'

Input:  intersectVal = 0, listA = [2,6,4], listB = [1,5], skipA = 3, skipB = 2
Output: No intersection
```

### Constraints
```text
The number of nodes of listA is in the m.
The number of nodes of listB is in the n.
1 <= m, n <= 3 * 10^4
1 <= Node.val <= 10^5
0 <= skipA <= m
0 <= skipB <= n
intersectVal is 0 if listA and listB do not intersect.
intersectVal == listA[skipA] == listB[skipB] if listA and listB intersect.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two walkers swapping routes
Two walkers each walk their own road and then the other's. Both totals are equal, so they step onto the shared stretch together, or finish together at null if there is none.

| In the story | In the solution |
|---|---|
| swapping | jump to the other head |
| meet | the same reference |

### Pattern signal
**Path switching.** A+B equals B+A, which cancels the different lengths before the junction.

### Thought process
1. **First idea:** Store one list's nodes in a set.
2. **The insight:** A+B equals B+A, which cancels the different lengths before the junction.

### Optimal approach
1. Start at both heads.
2. Advance together.
3. Switch to the other head after null.
4. Stop when identical.

### Dry run
```text
A = 1,2,C  B = 3,C
step  a     b
0     1     3
1     2     C
2     C     null
3     null  1
4     3     2
5     C     C, meet
```

### Why it works
After switching both have the same distance left to the junction or to the end, so they arrive simultaneously.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) {
            a = (a == null) ? headB : a.next;
            b = (b == null) ? headA : b.next;
        }
        return a;
    }
}
```

### Code walkthrough
- `a != b`: Compare nodes, not values.
- `? headB : a.next`: Swap roads after the end.
- `return a`: The junction, or null.

### Complexity
- **Time:** O(m + n)
- **Space:** O(1)

### Common mistakes
1. **Equal values do not mean a junction.** Fix: compare references.
2. **Switching at next == null never ends without a junction.** Fix: switch at null.
3. **The lists must keep their shape.** Fix: never edit links.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `[1], [2]` | `null` |
| Same head | `one list twice` | `its head` |
| Tail only | `A and B share the last node` | `that node` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[114 — 141. Linked List Cycle]]: Another meeting. The key idea there: A speed difference of exactly one closes any gap around a loop.
- **1650. LCA with Parents**: The same swap.

### In a real interview
Say it in this order:

1. **Brute force:** Store one list's nodes in a set.
2. **Insight:** A+B equals B+A, which cancels the different lengths before the junction.
3. **Approach in a few words:** Switching heads equalizes path lengths.
4. **Cost:** time O(m + n); space O(1).

## 5. Recall
Q: 160. Intersection of Two Linked Lists: which pattern, and what is the insight?
A: Path switching. A+B equals B+A, which cancels the different lengths before the junction.

Q: 160. Intersection of Two Linked Lists: what is the analogy, and what does it stand for?
A: Two walkers swapping routes: swapping → jump to the other head; meet → the same reference.

Q: 160. Intersection of Two Linked Lists: Why switch heads?
A: Both pointers then travel equal distances to the junction.

Q: 160. Intersection of Two Linked Lists: what are the time and space costs?
A: Time O(m + n). Space O(1).

@@ 3 | TOPIC | 114 — 141. Linked List Cycle | Easy · Floyd cycle | MUST
# 114 — 141. Linked List Cycle

**Problem 114 – Topic: Linked List | Difficulty: Easy | Pattern: Floyd cycle**

[Open on LeetCode](https://leetcode.com/problems/linked-list-cycle/)

## 1. Problem Statement
You get the head of a linked list. Return true if some node can be reached again by following `next`, meaning there is a cycle. The position `pos` in the examples only describes the test and is not given to you. Follow-up: O(1) space.

### Examples
```text
Input:  head = [3,2,0,-4], pos = 1
Output: true

Input:  head = [1,2], pos = 0
Output: true

Input:  head = [1], pos = -1
Output: false
```

### Constraints
```text
The number of the nodes in the list is in the range [0, 10^4].
-10^5 <= Node.val <= 10^5
pos is -1 or a valid index in the linked-list.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Runners on a track
Two runners share a track. On a straight road the faster just finishes. On a loop they come up behind the slower, closing one step per turn, so they can't jump past.

| In the story | In the solution |
|---|---|
| loop | cycle |
| lapping | meeting |

### Pattern signal
**Floyd cycle.** A speed difference of exactly one closes any gap around a loop.

### Thought process
1. **First idea:** Store every visited node in a set.
2. **The insight:** A speed difference of exactly one closes any gap around a loop.

### Optimal approach
1. Move fast by two and slow by one.
2. Succeed when they meet.
3. Fail when fast reaches null.

### Dry run
```text
list = 3,2,0,-4 back to 2
step  slow  fast
1     2     0
2     0     2
3     -4    -4, true
```

### Why it works
Inside a cycle the gap shrinks by one per move until zero. Without a cycle fast reaches the end.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }
}
```

### Code walkthrough
- `fast.next.next`: Double speed.
- `slow == fast`: They met, so there is a loop.
- `return false`: The road ended.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Both start equal.** Fix: compare only after moving.
2. **Values can repeat without a cycle.** Fix: compare references.
3. **An iteration limit proves nothing.** Fix: use two speeds.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `false` |
| Self | `[1], pos 0` | `true` |
| Straight | `[1,2]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[122 — 287. Find the Duplicate Number]]: An array as a list. The key idea there: Values are valid next pointers and nothing points at 0, so the duplicate is a cycle's entrance.
- **142. Cycle II**: Find the entrance.

### In a real interview
Say this: *"Floyd closes the gap by one per step, so it detects a cycle in constant space."*

## 5. Recall
Q: 141. Linked List Cycle: which pattern, and what is the insight?
A: Floyd cycle. A speed difference of exactly one closes any gap around a loop.

Q: 141. Linked List Cycle: what is the analogy, and what does it stand for?
A: Runners on a track: loop → cycle; lapping → meeting.

Q: 141. Linked List Cycle: Why must fast meet slow in a cycle?
A: The gap shrinks by one each step.

Q: 141. Linked List Cycle: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Review 19 | Re-solve without looking | MUST
# Review 19

Solve on a blank page. No notes, hints or solutions.

## 141. Linked List Cycle
Detect a cycle in a linked list.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/linked-list-cycle/)

## 83. Remove Duplicates from Sorted List
Remove duplicates from a sorted list.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/remove-duplicates-from-sorted-list/)

## 153. Find Minimum in Rotated Sorted Array
Find the minimum of a rotated sorted array.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)

## 22. Generate Parentheses
Generate all valid strings of n bracket pairs.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/generate-parentheses/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 141 | | | | |
| 83 | | | | |
| 153 | | | | |
| 22 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[114 — 141. Linked List Cycle]]
- [[111 — 83. Remove Duplicates from Sorted List]]
- [[100 — 153. Find Minimum in Rotated Sorted Array]]
- [[076 — 22. Generate Parentheses]]

@@ 2 | MODULE | Linked List — Medium | 14 problems | MUST
# Linked List: Medium

Patterns in this group: Middle, reverse, merge, Middle and reverse, Fixed gap, Old-to-new map, Node manipulation, History with cursor, Digit addition, Floyd on values, Pair rewiring, List merge sort, Two lists, Ring and cut, Sublist reversal, Bounded queue.

@@ 3 | TOPIC | 115 — 143. Reorder List | Medium · Middle, reverse, merge | MUST
# 115 — 143. Reorder List

**Problem 115 – Topic: Linked List | Difficulty: Medium | Pattern: Middle, reverse, merge**

[Open on LeetCode](https://leetcode.com/problems/reorder-list/)

## 1. Problem Statement
A list is L0, L1, up to Ln. Reorder it in place to L0, Ln, L1, Ln-1, L2, Ln-2 and so on. Only links may change, not node values.

### Examples
```text
Input:  head = [1,2,3,4]
Output: [1,4,2,3]

Input:  head = [1,2,3,4,5]
Output: [1,5,2,4,3]
```

### Constraints
```text
The number of nodes in the list is in the range [1, 5 * 10^4].
1 <= Node.val <= 1000
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Shuffling a folded deck
Shuffle a folded deck: cut it in half, turn the second pile over so the last card is on top, then take one card from each pile in turn.

| In the story | In the solution |
|---|---|
| turn over | reverse |
| alternate | weave |

### Pattern signal
**Middle, reverse, merge.** The wanted order is the first half forwards woven with the second half backwards.

### Thought process
1. **First idea:** Store nodes in an array and rewire from both ends.
2. **The insight:** The wanted order is the first half forwards woven with the second half backwards.

### Optimal approach
1. Find the middle.
2. Cut there.
3. Reverse the second half.
4. Weave the two.

### Dry run
```text
list = 1,2,3,4,5
middle cut  1,2,3 and 4,5
reverse     5,4
weave       1,5,2,4,3
```

### Why it works
The halves are read in exactly the two directions required, and the cut keeps them separate so no cycle forms.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public void reorderList(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode current = slow.next, back = null;
        slow.next = null; // cut
        while (current != null) {
            ListNode next = current.next;
            current.next = back;
            back = current;
            current = next;
        }
        ListNode front = head;
        while (back != null) {
            ListNode frontNext = front.next, backNext = back.next;
            front.next = back;
            back.next = frontNext;
            front = frontNext;
            back = backNext;
        }
    }
}
```

### Code walkthrough
- `fast.next.next != null`: The first half gets the extra card.
- `slow.next = null`: Really separate the piles.
- `frontNext`: Save both tops before linking.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Without a cut the list becomes a cycle.** Fix: set slow.next to null.
2. **Saving one successor loses a pile.** Fix: save both.
3. **Changing values is forbidden.** Fix: change links.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[1]` | `[1]` |
| Two | `[1,2]` | `[1,2]` |
| Odd | `[1,2,3]` | `[1,3,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[109 — 234. Palindrome Linked List]]: Compare. The key idea there: You can't walk backwards, but you can reverse the half that needs reading backwards.
- [[108 — 21. Merge Two Sorted Lists]]: Choose by value. The key idea there: The smallest remaining node is always one of the two current heads.

### In a real interview
Say it in this order:

1. **Brute force:** Store nodes in an array and rewire from both ends.
2. **Insight:** The wanted order is the first half forwards woven with the second half backwards.
3. **Approach in a few words:** It is three primitives: middle, reverse, merge.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 143. Reorder List: which pattern, and what is the insight?
A: Middle, reverse, merge. The wanted order is the first half forwards woven with the second half backwards.

Q: 143. Reorder List: what is the analogy, and what does it stand for?
A: Shuffling a folded deck: turn over → reverse; alternate → weave.

Q: 143. Reorder List: What three steps reorder the list?
A: Middle and cut, reverse, weave.

Q: 143. Reorder List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 116 — 2130. Maximum Twin Sum of a Linked List | Medium · Middle and reverse | SHOULD
# 116 — 2130. Maximum Twin Sum of a Linked List

**Problem 116 – Topic: Linked List | Difficulty: Medium | Pattern: Middle and reverse**

[Open on LeetCode](https://leetcode.com/problems/maximum-twin-sum-of-a-linked-list/)

## 1. Problem Statement
A linked list has even length n. Node i, counted from 0, is the twin of node n-1-i. A twin sum is the sum of both values. Return the largest twin sum.

### Examples
```text
Input:  head = [5,4,2,1]
Output: 6

Input:  head = [4,2,2,3]
Output: 7

Input:  head = [1,100000]
Output: 100001
```

### Constraints
```text
The number of nodes in the list is an even integer in the range [2, 10^5].
1 <= Node.val <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A folded banquet table
A long banquet table is folded in the middle. Guests who now face each other are twins, so you can add each facing pair walking down once.

| In the story | In the solution |
|---|---|
| fold | middle |
| facing | the reversed half |

### Pattern signal
**Middle and reverse.** Reversing the second half puts every mirrored pair at the same index.

### Thought process
1. **First idea:** Copy to an array and pair from both ends.
2. **The insight:** Reversing the second half puts every mirrored pair at the same index.

### Optimal approach
1. Find the middle.
2. Reverse from it.
3. Walk both halves.
4. Keep the largest sum.

### Dry run
```text
list = 5,4,2,1
reversed half  1,2
sums           5+1 = 6, 4+2 = 6
```

### Why it works
Position i of the reversed half held node n-1-i, so every twin pair is added exactly once.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int pairSum(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode back = null;
        while (slow != null) {
            ListNode next = slow.next;
            slow.next = back;
            back = slow;
            slow = next;
        }
        int best = 0;
        while (back != null) {
            best = Math.max(best, head.val + back.val);
            head = head.next;
            back = back.next;
        }
        return best;
    }
}
```

### Code walkthrough
- `slow.next = back`: Fold the second half over.
- `head.val + back.val`: A facing pair of guests.
- `best = 0`: All values are positive.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Twins are mirrored rather than adjacent.** Fix: reverse the second half.
2. **Sorting changes who is paired.** Fix: keep positions.
3. **The first middle misaligns the halves.** Fix: start both pointers at the head.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Two | `[1,100]` | `101` |
| Equal | `[2,2,2,2]` | `4` |
| Uneven | `[1,9,1,1]` | `10` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[109 — 234. Palindrome Linked List]]: Equality. The key idea there: You can't walk backwards, but you can reverse the half that needs reading backwards.
- [[048 — 11. Container With Most Water]]: Outside-in on arrays. The key idea there: Area is width times the shorter line. The shorter line already has its best possible partner width, so it can be retired.

### In a real interview
Say it in this order:

1. **Brute force:** Copy to an array and pair from both ends.
2. **Insight:** Reversing the second half puts every mirrored pair at the same index.
3. **Approach in a few words:** I reverse the second half and scan both.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 2130. Maximum Twin Sum of a Linked List: which pattern, and what is the insight?
A: Middle and reverse. Reversing the second half puts every mirrored pair at the same index.

Q: 2130. Maximum Twin Sum of a Linked List: what is the analogy, and what does it stand for?
A: A folded banquet table: fold → middle; facing → the reversed half.

Q: 2130. Maximum Twin Sum of a Linked List: How are twins aligned?
A: By reversing the second half.

Q: 2130. Maximum Twin Sum of a Linked List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 117 — 19. Remove Nth Node From End of List | Medium · Fixed gap | MUST
# 117 — 19. Remove Nth Node From End of List

**Problem 117 – Topic: Linked List | Difficulty: Medium | Pattern: Fixed gap**

[Open on LeetCode](https://leetcode.com/problems/remove-nth-node-from-end-of-list/)

## 1. Problem Statement
You get the head of a linked list and an integer `n`. Remove the nth node counted from the end and return the head. Follow-up: use one pass.

### Examples
```text
Input:  head = [1,2,3,4,5], n = 2
Output: [1,2,3,5]

Input:  head = [1], n = 1
Output: []

Input:  head = [1,2], n = 1
Output: [1]
```

### Constraints
```text
The number of nodes in the list is sz.
1 <= sz <= 30
0 <= Node.val <= 100
1 <= n <= sz
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two hikers on a rope
Two hikers are tied by a rope n nodes long. When the leader stands at the cliff, the follower is exactly one before the node to remove.

| In the story | In the solution |
|---|---|
| rope | fixed gap |
| base camp | dummy |

### Pattern signal
**Fixed gap.** A constant gap converts "from the end" into a position found in one pass.

### Thought process
1. **First idea:** Count the length and walk again, in two passes.
2. **The insight:** A constant gap converts "from the end" into a position found in one pass.

### Optimal approach
1. Start both at a dummy.
2. Move lead n ahead.
3. Move together until lead is last.
4. Bypass.

### Dry run
```text
list = 1,2,3,4,5  n = 2
lead  follow
2     dummy
3     1
4     2
5     3, bypass 4
```

### Why it works
Follow is always n behind, so when lead is the final node follow precedes the nth from the end.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode lead = dummy, follow = dummy;
        for (int i = 0; i < n; i++) lead = lead.next;
        while (lead.next != null) {
            lead = lead.next;
            follow = follow.next;
        }
        follow.next = follow.next.next;
        return dummy.next;
    }
}
```

### Code walkthrough
- `new ListNode(0, head)`: Even the head now has a predecessor.
- `i < n`: Stretch the rope to n.
- `lead.next != null`: Stop on the last node itself.

### Complexity
- **Time:** O(L)
- **Space:** O(1)

### Common mistakes
1. **Removing the head has no predecessor.** Fix: start at a dummy.
2. **A wrong gap removes a neighbor.** Fix: advance lead exactly n from the dummy.
3. **Stopping at null is one too far.** Fix: stop at the last node.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Only | `[1], 1` | `[]` |
| Head | `[1,2], 2` | `[2]` |
| Tail | `[1,2], 1` | `[1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[112 — 876. Middle of the Linked List]]: A ratio. The key idea there: A speed ratio of two measures half without knowing the length.
- [[126 — 61. Rotate List]]: Cut k from the end. The key idea there: Nodes keep their circular order, so only the cut position changes.

### In a real interview
Say this: *"A fixed gap plus a dummy solves it in one pass."*

## 5. Recall
Q: 19. Remove Nth Node From End of List: which pattern, and what is the insight?
A: Fixed gap. A constant gap converts "from the end" into a position found in one pass.

Q: 19. Remove Nth Node From End of List: what is the analogy, and what does it stand for?
A: Two hikers on a rope: rope → fixed gap; base camp → dummy.

Q: 19. Remove Nth Node From End of List: Where must follow stop?
A: On the node before the one to delete.

Q: 19. Remove Nth Node From End of List: what are the time and space costs?
A: Time O(L). Space O(1).

@@ 3 | TOPIC | 118 — 138. Copy List with Random Pointer | Medium · Old-to-new map | MUST
# 118 — 138. Copy List with Random Pointer

**Problem 118 – Topic: Linked List | Difficulty: Medium | Pattern: Old-to-new map**

[Open on LeetCode](https://leetcode.com/problems/copy-list-with-random-pointer/)

## 1. Problem Statement
Each node has next and a random pointer to any node or null. Return a deep copy.

### Examples
```text
Input:  head = [[7,null],[13,0],[11,4],[10,2],[1,0]]
Output: [[7,null],[13,0],[11,4],[10,2],[1,0]]

Input:  head = [[1,1],[2,1]]
Output: [[1,1],[2,1]]

Input:  head = [[3,null],[3,0],[3,null]]
Output: [[3,null],[3,0],[3,null]]
```

### Constraints
```text
0 <= n <= 1000
-10^4 <= Node.val <= 10^4
Node.random is null or is pointing to some node in the linked list.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Cloning a town with an address book
You clone a town whose friendship arrows may point at houses not yet built. First build every twin house and write it in an address book. Then translate every arrow through the book.

| In the story | In the solution |
|---|---|
| address book | identity map |
| translate | `twin.get` |

### Pattern signal
**Old-to-new map.** Separate creating nodes from connecting them, with a map for the correspondence.

### Thought process
1. **First idea:** Find each random target by its index, in O(n²).
2. **The insight:** Separate creating nodes from connecting them, with a map for the correspondence.

### Optimal approach
1. Create and map a twin for each node.
2. Walk again.
3. Set next through the map.
4. Set random through the map.

### Dry run
```text
A.random = B, B.random = B
pass  action
1     A to A', B to B'
2     A'.next = B', A'.random = B', B'.random = B'
```

### Why it works
Every original has its twin before any lookup, so every pointer gets the right image and none point into the original.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public Node copyRandomList(Node head) {
        Map<Node, Node> twin = new HashMap<>();
        for (Node n = head; n != null; n = n.next) twin.put(n, new Node(n.val));
        for (Node n = head; n != null; n = n.next) {
            Node copy = twin.get(n);
            copy.next = twin.get(n.next);
            copy.random = twin.get(n.random);
        }
        return twin.get(head);
    }
}
```

### Code walkthrough
- `new Node(n.val)`: Only build in the first pass.
- `twin.get(n.next)`: A null key safely returns null.
- `twin.get(n.random)`: Translate the arrow, never share it.

### Complexity
- **Time:** O(n)
- **Space:** O(n), or O(1) by interleaving twins

### Common mistakes
1. **Copying random directly points into the original.** Fix: translate it.
2. **Values repeat.** Fix: key the map by node.
3. **Forward targets may not exist yet.** Fix: create every twin first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| Self | `one node, random itself` | `the copy points to itself` |
| Equal values | `two 1s` | `two distinct copies` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[204 — 133. Clone Graph]]: Discover by search. The key idea there: One old-to-new map serves both as the visited set and as the translation of every edge.
- [[276 — 146. LRU Cache]]: A map to nodes. The key idea there: A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).

### In a real interview
Say it in this order:

1. **Brute force:** Find each random target by its index, in O(n²).
2. **Insight:** Separate creating nodes from connecting them, with a map for the correspondence.
3. **Approach in a few words:** An old-to-new map handles forward and cyclic references.
4. **Cost:** time O(n); space O(n), or O(1) by interleaving twins.

## 5. Recall
Q: 138. Copy List with Random Pointer: which pattern, and what is the insight?
A: Old-to-new map. Separate creating nodes from connecting them, with a map for the correspondence.

Q: 138. Copy List with Random Pointer: what is the analogy, and what does it stand for?
A: Cloning a town with an address book: address book → identity map; translate → `twin.get`.

Q: 138. Copy List with Random Pointer: Why create all copies first?
A: A random target may lie ahead.

Q: 138. Copy List with Random Pointer: what are the time and space costs?
A: Time O(n). Space O(n), or O(1) by interleaving twins.

@@ 3 | TOPIC | 119 — 707. Design Linked List | Medium · Node manipulation | SHOULD
# 119 — 707. Design Linked List

**Problem 119 – Topic: Linked List | Difficulty: Medium | Pattern: Node manipulation**

[Open on LeetCode](https://leetcode.com/problems/design-linked-list/)

## 1. Problem Statement
Design a 0-indexed linked list: get (or -1), addAtHead, addAtTail, addAtIndex (index may equal length) and deleteAtIndex. Invalid edits do nothing.

### Examples
```text
Input:  ["MyLinkedList", "addAtHead", "addAtTail", "addAtIndex", "get", "deleteAtIndex", "get"]
        [[], [1], [3], [1, 2], [1], [1], [1]]
Output: [null, null, null, null, 2, null, 3]
```

### Constraints
```text
0 <= index, val <= 1000
Please do not use the built-in LinkedList library.
At most 2000 calls will be made to get, addAtHead, addAtTail, addAtIndex and deleteAtIndex.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Railway staff and a buffer stop
A permanent buffer stands before the first carriage. So every coupling job is the same: walk to the carriage before the place and change one link.

| In the story | In the solution |
|---|---|
| buffer | dummy |
| carriage count | `size` |

### Pattern signal
**Node manipulation.** Every edit reduces to changing what follows position index-1.

### Thought process
1. **First idea:** Handle head, middle and tail separately, which invites bugs.
2. **The insight:** Every edit reduces to changing what follows position index-1.

### Optimal approach
1. Keep a dummy and a size.
2. Validate the index.
3. Walk index steps.
4. Relink.
5. Update the size.

### Dry run
```text
operation         list    size
addAtHead 1       1       1
addAtTail 3       1,3     2
addAtIndex 1,2    1,2,3   3
deleteAtIndex 1   1,3     2
```

### Why it works
Walking index steps from the dummy lands on the predecessor for every valid index, the first and last included.

## 3. Java Solution with Explanation
### Code
```java
class MyLinkedList {
    private static class Link {
        int val;
        Link next;

        Link(int val, Link next) {
            this.val = val;
            this.next = next;
        }
    }

    private final Link dummy = new Link(0, null);
    private int size;

    private Link before(int index) {
        Link node = dummy;
        for (int i = 0; i < index; i++) node = node.next;
        return node;
    }

    public int get(int index) {
        return index >= size ? -1 : before(index).next.val;
    }

    public void addAtHead(int val) {
        addAtIndex(0, val);
    }

    public void addAtTail(int val) {
        addAtIndex(size, val);
    }

    public void addAtIndex(int index, int val) {
        if (index > size) return;
        Link p = before(index);
        p.next = new Link(val, p.next);
        size++;
    }

    public void deleteAtIndex(int index) {
        if (index >= size) return;
        Link p = before(index);
        p.next = p.next.next;
        size--;
    }
}
```

### Code walkthrough
- `before(int index)`: One walk for every operation.
- `index > size`: An index equal to the size appends.
- `index >= size`: Reading or deleting there is invalid.
- `new Link(val, p.next)`: Couple the new carriage in.

### Complexity
- **Time:** O(index) per operation
- **Space:** O(n)

### Common mistakes
1. **Insert and delete have different limits.** Fix: allow index == size only for insert.
2. **A forgotten size breaks validation.** Fix: update it on every edit.
3. **Invalid edits must not crash.** Fix: return without changes.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `get(0)` | `-1` |
| Append | `addAtIndex(0,5), get(0)` | `5` |
| Invalid | `addAtIndex(2,5) on empty` | `no change` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[120 — 1472. Design Browser History]]: A cursor. The key idea there: Moving is index arithmetic and erasing the future is moving an end marker.
- [[276 — 146. LRU Cache]]: Doubly linked. The key idea there: A map finds a node at once and a doubly linked list moves it at once, so together they keep recency order in O(1).

### In a real interview
Say it in this order:

1. **Brute force:** Handle head, middle and tail separately, which invites bugs.
2. **Insight:** Every edit reduces to changing what follows position index-1.
3. **Approach in a few words:** A dummy and size make every edit uniform.
4. **Cost:** time O(index) per operation; space O(n).

## 5. Recall
Q: 707. Design Linked List: which pattern, and what is the insight?
A: Node manipulation. Every edit reduces to changing what follows position index-1.

Q: 707. Design Linked List: what is the analogy, and what does it stand for?
A: Railway staff and a buffer stop: buffer → dummy; carriage count → `size`.

Q: 707. Design Linked List: Which insert indices are valid?
A: Zero through size inclusive.

Q: 707. Design Linked List: what are the time and space costs?
A: Time O(index) per operation. Space O(n).

@@ 3 | TOPIC | 120 — 1472. Design Browser History | Medium · History with cursor | SHOULD
# 120 — 1472. Design Browser History

**Problem 120 – Topic: Linked List | Difficulty: Medium | Pattern: History with cursor**

[Open on LeetCode](https://leetcode.com/problems/design-browser-history/)

## 1. Problem Statement
Start at a homepage. `visit` erases forward history. `back(steps)` and `forward(steps)` move as far as possible up to steps and return the page.

### Examples
```text
Input:  ["BrowserHistory","visit","visit","visit","back","back","forward","visit","forward","back","back"]
        [["leetcode.com"],["google.com"],["facebook.com"],["youtube.com"],[1],[1],[1],["linkedin.com"],[2],[2],[7]]
Output: [null,null,null,null,"facebook.com","google.com","facebook.com",null,"linkedin.com","google.com","leetcode.com"]
```

### Constraints
```text
1 <= homepage.length <= 20
1 <= url.length <= 20
1 <= steps <= 100
homepage and url consist of '.' or lower case English letters.
At most 5000 calls will be made to visit, back, and forward.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A bookmark in a notebook
Your history is lines in a notebook with a bookmark. Going back only moves the bookmark. Writing at the bookmark makes every line after it invalid, even if still visible.

| In the story | In the solution |
|---|---|
| bookmark | `current` |
| last valid line | `end` |

### Pattern signal
**History with cursor.** Moving is index arithmetic and erasing the future is moving an end marker.

### Thought process
1. **First idea:** Delete forward entries one at a time on every visit.
2. **The insight:** Moving is index arithmetic and erasing the future is moving an end marker.

### Optimal approach
1. Write after the cursor.
2. Set the end there.
3. Clamp back at zero.
4. Clamp forward at the end.

### Dry run
```text
operation    pages valid  current
start h      h            0
visit a, b   h a b        2
back 1       h a b        1
visit c      h a c        2
```

### Why it works
The valid range is always exactly the history, so clamping to it implements "as far as possible".

## 3. Java Solution with Explanation
### Code
```java
class BrowserHistory {
    private final List<String> pages = new ArrayList<>();
    private int current, end;

    public BrowserHistory(String homepage) {
        pages.add(homepage);
    }

    public void visit(String url) {
        current++;
        if (current < pages.size()) pages.set(current, url);
        else pages.add(url);
        end = current;
    }

    public String back(int steps) {
        current = Math.max(0, current - steps);
        return pages.get(current);
    }

    public String forward(int steps) {
        current = Math.min(end, current + steps);
        return pages.get(current);
    }
}
```

### Code walkthrough
- `pages.set(current, url)`: Overwrite the old future.
- `end = current`: Later lines are now invalid.
- `Math.max(0`: You can't go before the homepage.

### Complexity
- **Time:** O(1) per operation
- **Space:** O(visits)

### Common mistakes
1. **The list can hold stale pages.** Fix: clamp to the logical end.
2. **Going back must keep the future.** Fix: only move the cursor.
3. **Unclamped steps go out of range.** Fix: use min and max.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Far back | `back(9)` | `homepage` |
| No future | `forward(1)` | `current page` |
| Branch | `visit a, back 1, visit b, forward 1` | `b` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[081 — 71. Simplify Path]]: No forward. The key idea there: ".." cancels the newest surviving folder.
- [[119 — 707. Design Linked List]]: The links version. The key idea there: Every edit reduces to changing what follows position index-1.

### In a real interview
Say this: *"A list, cursor and logical end make all operations constant."*

## 5. Recall
Q: 1472. Design Browser History: which pattern, and what is the insight?
A: History with cursor. Moving is index arithmetic and erasing the future is moving an end marker.

Q: 1472. Design Browser History: what is the analogy, and what does it stand for?
A: A bookmark in a notebook: bookmark → `current`; last valid line → `end`.

Q: 1472. Design Browser History: What does visit do to forward history?
A: Invalidates it by moving the logical end.

Q: 1472. Design Browser History: what are the time and space costs?
A: Time O(1) per operation. Space O(visits).

@@ 3 | TOPIC | Practice — Review 20 | Re-solve without looking | MUST
# Review 20

Solve on a blank page. No notes, hints or solutions.

## 1472. Design Browser History
Implement visit, back and forward of a browser.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/design-browser-history/)

## 19. Remove Nth Node From End of List
Remove the nth node from the end.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/remove-nth-node-from-end-of-list/)

## 4. Median of Two Sorted Arrays
Find the median of two sorted arrays in logarithmic time.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/median-of-two-sorted-arrays/)

## 394. Decode String
Expand strings like 3[a2[c]].

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/decode-string/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 1472 | | | | |
| 19 | | | | |
| 4 | | | | |
| 394 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[120 — 1472. Design Browser History]]
- [[117 — 19. Remove Nth Node From End of List]]
- [[106 — 4. Median of Two Sorted Arrays]]
- [[082 — 394. Decode String]]

@@ 3 | TOPIC | 121 — 2. Add Two Numbers | Medium · Digit addition | MUST
# 121 — 2. Add Two Numbers

**Problem 121 – Topic: Linked List | Difficulty: Medium | Pattern: Digit addition**

[Open on LeetCode](https://leetcode.com/problems/add-two-numbers/)

## 1. Problem Statement
Two non-empty lists hold numbers with digits in reverse order. Return their sum as such a list.

### Examples
```text
Input:  l1 = [2,4,3], l2 = [5,6,4]
Output: [7,0,8]

Input:  l1 = [0], l2 = [0]
Output: [0]

Input:  l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
Output: [8,9,9,9,0,0,0,1]
```

### Constraints
```text
The number of nodes in each linked list is in the range [1, 100].
0 <= Node.val <= 9
It is guaranteed that the list represents a number that does not have leading zeros.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Column addition with the units first
It is addition on paper and the lists already start at the units column. A missing column is a zero and a final carry gets a column of its own.

| In the story | In the solution |
|---|---|
| column | a node |
| small digit on top | `carry` |

### Pattern signal
**Digit addition.** With units first, walking forward is exactly the addition algorithm.

### Thought process
1. **First idea:** Convert to integers, which overflow for 100 digits.
2. **The insight:** With units first, walking forward is exactly the addition algorithm.

### Optimal approach
1. Continue while a digit or carry remains.
2. Add them.
3. Append the sum modulo ten.
4. Carry the rest.

### Dry run
```text
342 + 465
column  sum     digit  carry
1       2+5     7      0
2       4+6     0      1
3       3+4+1   8      0
```

### Why it works
The result holds the correct lower digits, and the carry is all that affects higher ones.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), tail = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            tail.next = new ListNode(sum % 10);
            tail = tail.next;
            carry = sum / 10;
        }
        return dummy.next;
    }
}
```

### Code walkthrough
- `carry != 0`: The last carry needs a column.
- `sum % 10`: The digit to write.
- `sum / 10`: What moves to the next column.

### Complexity
- **Time:** O(max(m,n))
- **Space:** O(max(m,n)) for the output

### Common mistakes
1. **100 digits overflow any integer.** Fix: add node by node.
2. **The final carry is easy to lose.** Fix: loop while it exists.
3. **The order is already units first.** Fix: do not reverse.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Zeros | `[0], [0]` | `[0]` |
| Carry | `[5], [5]` | `[0,1]` |
| Unequal | `[9,9], [1]` | `[0,0,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **445. Add Two Numbers II**: Stacks.
- [[275 — 67. Add Binary]]: From string ends. The key idea there: Binary addition is column addition with base 2 instead of 10.

### In a real interview
Say this: *"It is digit addition with one loop for leftovers and carry."*

## 5. Recall
Q: 2. Add Two Numbers: which pattern, and what is the insight?
A: Digit addition. With units first, walking forward is exactly the addition algorithm.

Q: 2. Add Two Numbers: what is the analogy, and what does it stand for?
A: Column addition with the units first: column → a node; small digit on top → `carry`.

Q: 2. Add Two Numbers: When does addition stop?
A: When both lists and the carry are empty.

Q: 2. Add Two Numbers: what are the time and space costs?
A: Time O(max(m,n)). Space O(max(m,n)) for the output.

@@ 3 | TOPIC | 122 — 287. Find the Duplicate Number | Medium · Floyd on values | MUST
# 122 — 287. Find the Duplicate Number

**Problem 122 – Topic: Linked List | Difficulty: Medium | Pattern: Floyd on values**

[Open on LeetCode](https://leetcode.com/problems/find-the-duplicate-number/)

## 1. Problem Statement
n+1 values lie in 1..n with one repeated. Find it without modifying the array, using O(1) space.

### Examples
```text
Input:  nums = [1,3,4,2,2]
Output: 2

Input:  nums = [3,1,3,4,2]
Output: 3

Input:  nums = [3,3,3,3,3]
Output: 3
```

### Constraints
```text
1 <= n <= 10^5
nums.length == n + 1
1 <= nums[i] <= n
All the integers in nums appear only once except for precisely one integer which appears two or more times.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Rooms with signs to the next room
Each room has a sign to the next room. Two signs to one room make it the entrance of a loop. Find a meeting by two speeds, then equal steps from the start and the meeting reach the entrance.

| In the story | In the solution |
|---|---|
| sign | `nums[i]` |
| entrance | the duplicate |

### Pattern signal
**Floyd on values.** Values are valid next pointers and nothing points at 0, so the duplicate is a cycle's entrance.

### Thought process
1. **First idea:** A set or sorting, each breaking a rule.
2. **The insight:** Values are valid next pointers and nothing points at 0, so the duplicate is a cycle's entrance.

### Optimal approach
1. Move at two speeds until meeting.
2. Put a pointer at 0.
3. Move both one step at a time.
4. Return their meeting.

### Dry run
```text
nums = [1,3,4,2,2]
phase 1  slow: 1,3,2,4  fast: 3,4,4,4  meet at 4
phase 2  from 0: 1,3,2  from 4: 2,4,2  meet at 2
```

### Why it works
At the meeting the extra distance is whole laps, so from 0 and from the meeting the entrance is the same number of steps modulo the loop.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int findDuplicate(int[] nums) {
        int slow = 0, fast = 0;
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);
        int start = 0;
        while (start != slow) {
            start = nums[start];
            slow = nums[slow];
        }
        return start;
    }
}
```

### Code walkthrough
- `do {`: Both start equal, so move first.
- `nums[nums[fast]]`: The hare moves two rooms.
- `int start = 0`: Begin phase two outside the loop.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **The sum formula fails with several copies.** Fix: use the cycle.
2. **The first meeting is not the answer.** Fix: run the second phase.
3. **Marking modifies the input.** Fix: use only pointers.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Smallest | `[1,1]` | `1` |
| Many | `[2,2,2,2]` | `2` |
| End | `[1,2,3,3]` | `3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[114 — 141. Linked List Cycle]]: Phase one. The key idea there: A speed difference of exactly one closes any gap around a loop.
- [[011 — 448. Find All Numbers Disappeared in an Array]]: Edits allowed. The key idea there: Values and indices cover the same range, so the array can be its own set.

### In a real interview
Say this: *"Values as next pointers make the duplicate a cycle entry."*

## 5. Recall
Q: 287. Find the Duplicate Number: which pattern, and what is the insight?
A: Floyd on values. Values are valid next pointers and nothing points at 0, so the duplicate is a cycle's entrance.

Q: 287. Find the Duplicate Number: what is the analogy, and what does it stand for?
A: Rooms with signs to the next room: sign → `nums[i]`; entrance → the duplicate.

Q: 287. Find the Duplicate Number: What is the duplicate in the linked-list view?
A: The cycle's entrance.

Q: 287. Find the Duplicate Number: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 123 — 24. Swap Nodes in Pairs | Medium · Pair rewiring | SHOULD
# 123 — 24. Swap Nodes in Pairs

**Problem 123 – Topic: Linked List | Difficulty: Medium | Pattern: Pair rewiring**

[Open on LeetCode](https://leetcode.com/problems/swap-nodes-in-pairs/)

## 1. Problem Statement
Swap every two adjacent nodes without changing values.

### Examples
```text
Input:  head = [1,2,3,4]
Output: [2,1,4,3]

Input:  head = []
Output: []

Input:  head = [1]
Output: [1]

Input:  head = [1,2,3]
Output: [2,1,3]
```

### Constraints
```text
The number of nodes in the list is in the range [0, 100].
0 <= Node.val <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Dance partners changing sides
Dancers change sides couple by couple. A holder before each couple keeps the line. The second steps forward, the first behind, and then holds for the next couple.

| In the story | In the solution |
|---|---|
| holder | `previous` |
| couple | first and second |

### Pattern signal
**Pair rewiring.** One swap always involves the same three links.

### Thought process
1. **First idea:** Swap values, which is forbidden.
2. **The insight:** One swap always involves the same three links.

### Optimal approach
1. Use a dummy.
2. Require a full pair.
3. Connect the three links.
4. Move previous to the pair's new end.

### Dry run
```text
list = 1,2,3,4
previous  pair  result
dummy     1,2   2,1,3,4
1         3,4   2,1,4,3
```

### Why it works
Everything through previous is already swapped and the rest untouched, and each round fixes one more pair.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0, head), previous = dummy;
        while (previous.next != null && previous.next.next != null) {
            ListNode first = previous.next, second = first.next;
            first.next = second.next;
            second.next = first;
            previous.next = second;
            previous = first;
        }
        return dummy.next;
    }
}
```

### Code walkthrough
- `first.next = second.next`: Keep hold of the rest first.
- `second.next = first`: The second now leads.
- `previous = first`: The first is the new holder.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Linking second to first too early loses the rest.** Fix: link first onward first.
2. **Moving previous to second overlaps pairs.** Fix: move it to first.
3. **An odd node must stay.** Fix: require a full pair.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| One | `[1]` | `[1]` |
| Odd | `[1,2,3]` | `[2,1,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[130 — 25. Reverse Nodes in k-Group]]: Any size. The key idea there: Each group is an ordinary reversal whose previous starts at the node after the group.
- [[107 — 206. Reverse Linked List]]: All. The key idea there: A reversal changes nothing but each arrow's direction.

### In a real interview
Say it in this order:

1. **Brute force:** Swap values, which is forbidden.
2. **Insight:** One swap always involves the same three links.
3. **Approach in a few words:** Three links per pair, with a dummy.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 24. Swap Nodes in Pairs: which pattern, and what is the insight?
A: Pair rewiring. One swap always involves the same three links.

Q: 24. Swap Nodes in Pairs: what is the analogy, and what does it stand for?
A: Dance partners changing sides: holder → `previous`; couple → first and second.

Q: 24. Swap Nodes in Pairs: Where does previous move after a swap?
A: To the first original node, now last of the pair.

Q: 24. Swap Nodes in Pairs: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 124 — 148. Sort List | Medium · List merge sort | SHOULD
# 124 — 148. Sort List

**Problem 124 – Topic: Linked List | Difficulty: Medium | Pattern: List merge sort**

[Open on LeetCode](https://leetcode.com/problems/sort-list/)

## 1. Problem Statement
Sort a linked list in O(n log n).

### Examples
```text
Input:  head = [4,2,1,3]
Output: [1,2,3,4]

Input:  head = [-1,5,3,4,0]
Output: [-1,0,3,4,5]

Input:  head = []
Output: []
```

### Constraints
```text
The number of nodes in the list is in the range [0, 5 * 10^4].
-10^5 <= Node.val <= 10^5
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Sorting a chain by unclipping
You can't jump into the middle of a chain, but you can walk to it and unclip. Sort both pieces the same way and close them like a zip.

| In the story | In the solution |
|---|---|
| unclip | cut at the middle |
| zip | merge |

### Pattern signal
**List merge sort.** Merge sort needs only sequential access, exactly what a list offers.

### Thought process
1. **First idea:** Insertion sort, in O(n²).
2. **The insight:** Merge sort needs only sequential access, exactly what a list offers.

### Optimal approach
1. Return a tiny list.
2. Find the end of the first half.
3. Cut.
4. Sort both.
5. Merge.

### Dry run
```text
list = 4,2,1,3
split  4,2 and 1,3
sort   2,4 and 1,3
merge  1,2,3,4
```

### Why it works
Both halves are strictly smaller so recursion ends, and merging two sorted halves gives a sorted whole.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode second = slow.next;
        slow.next = null;
        return merge(sortList(head), sortList(second));
    }

    private ListNode merge(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) {
                tail.next = a;
                a = a.next;
            } else {
                tail.next = b;
                b = b.next;
            }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b;
        return dummy.next;
    }
}
```

### Code walkthrough
- `fast = head.next`: So two nodes split one and one.
- `slow.next = null`: Unclip the halves.
- `merge(sortList(head)`: Trust both sorted halves and zip them.

### Complexity
- **Time:** O(n log n)
- **Space:** O(log n) stack

### Common mistakes
1. **Starting fast at head never splits two nodes.** Fix: start at head.next.
2. **Without a cut nothing shrinks.** Fix: null the middle link.
3. **Index access costs linear time.** Fix: use pointers.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[]` | `[]` |
| Two | `[2,1]` | `[1,2]` |
| Equal | `[1,1]` | `[1,1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[018 — 912. Sort an Array]]: With indices. The key idea there: One paper is already sorted, and two sorted piles merge in linear time.
- [[108 — 21. Merge Two Sorted Lists]]: The zip. The key idea there: The smallest remaining node is always one of the two current heads.

### In a real interview
Say this: *"Merge sort suits lists, with bottom-up as the constant-space follow-up."*

## 5. Recall
Q: 148. Sort List: which pattern, and what is the insight?
A: List merge sort. Merge sort needs only sequential access, exactly what a list offers.

Q: 148. Sort List: what is the analogy, and what does it stand for?
A: Sorting a chain by unclipping: unclip → cut at the middle; zip → merge.

Q: 148. Sort List: Why start fast at head.next?
A: So two nodes split into one and one.

Q: 148. Sort List: what are the time and space costs?
A: Time O(n log n). Space O(log n) stack.

@@ 3 | TOPIC | 125 — 86. Partition List | Medium · Two lists | SHOULD
# 125 — 86. Partition List

**Problem 125 – Topic: Linked List | Difficulty: Medium | Pattern: Two lists**

[Open on LeetCode](https://leetcode.com/problems/partition-list/)

## 1. Problem Statement
You get the head of a linked list and a value `x`. Move every node smaller than `x` before every node at least `x`, keeping the original relative order inside both groups, and return the head.

### Examples
```text
Input:  head = [1,4,3,2,5,2], x = 3
Output: [1,2,2,4,3,5]

Input:  head = [2,1], x = 2
Output: [1,2]
```

### Constraints
```text
The number of nodes in the list is in the range [0, 200].
-100 <= Node.val <= 100
-200 <= x <= 200
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Two checkout queues
Send each arrival to one of two queues without reordering, then join the queues. The last person must let go of whoever followed them in the original line.

| In the story | In the solution |
|---|---|
| queues | two dummy lists |
| let go | end with null |

### Pattern signal
**Two lists.** A stable partition is simply appending to two separate lists.

### Thought process
1. **First idea:** Copy values into two lists and rebuild.
2. **The insight:** A stable partition is simply appending to two separate lists.

### Optimal approach
1. Create two dummies.
2. Append each node to one.
3. End the high list with null.
4. Join low to high.

### Dry run
```text
list = 1,4,3,2,5,2  x = 3
low   1,2,2
high  4,3,5
join  1,2,2,4,3,5
```

### Why it works
Each node joins the right queue once, in original order, so both the split and the order are correct.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode partition(ListNode head, int x) {
        ListNode low = new ListNode(0), high = new ListNode(0);
        ListNode lowTail = low, highTail = high;
        for (ListNode node = head; node != null; node = node.next) {
            if (node.val < x) {
                lowTail.next = node;
                lowTail = node;
            } else {
                highTail.next = node;
                highTail = node;
            }
        }
        highTail.next = null;
        lowTail.next = high.next;
        return low.next;
    }
}
```

### Code walkthrough
- `node.val < x`: An equal value goes to the high queue.
- `highTail.next = null`: Let go, or a cycle forms.
- `lowTail.next = high.next`: Join, skipping the dummy.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **An old link makes a cycle.** Fix: end the high list with null.
2. **Linking to the dummy adds a fake node.** Fix: join to high.next.
3. **Sorting breaks the relative order.** Fix: only partition.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], 0` | `[]` |
| All high | `[3,4], 1` | `[3,4]` |
| Equal | `[2,1], 2` | `[1,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[043 — 75. Sort Colors]]: Three array regions. The key idea there: With three values, placing the 0s and 2s at the ends also places the 1s.
- **328. Odd Even List**: By position.

### In a real interview
Say it in this order:

1. **Brute force:** Copy values into two lists and rebuild.
2. **Insight:** A stable partition is simply appending to two separate lists.
3. **Approach in a few words:** Two tails give a stable partition.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 86. Partition List: which pattern, and what is the insight?
A: Two lists. A stable partition is simply appending to two separate lists.

Q: 86. Partition List: what is the analogy, and what does it stand for?
A: Two checkout queues: queues → two dummy lists; let go → end with null.

Q: 86. Partition List: Why terminate the high list?
A: Its last node may still point into the low list.

Q: 86. Partition List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 126 — 61. Rotate List | Medium · Ring and cut | SHOULD
# 126 — 61. Rotate List

**Problem 126 – Topic: Linked List | Difficulty: Medium | Pattern: Ring and cut**

[Open on LeetCode](https://leetcode.com/problems/rotate-list/)

## 1. Problem Statement
You get the head of a linked list and an integer `k`, which may exceed the length. Rotate the list right by k places and return the new head.

### Examples
```text
Input:  head = [1,2,3,4,5], k = 2
Output: [4,5,1,2,3]

Input:  head = [0,1,2], k = 4
Output: [2,0,1]
```

### Constraints
```text
The number of nodes in the list is in the range [0, 500].
-100 <= Node.val <= 100
0 <= k <= 2 * 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A bracelet
Close the necklace into a ring. Nothing is moved: a rotation is just choosing another place to open the clasp.

| In the story | In the solution |
|---|---|
| ring | tail.next = head |
| opening | the new tail |

### Pattern signal
**Ring and cut.** Nodes keep their circular order, so only the cut position changes.

### Thought process
1. **First idea:** Move the tail to the front k times, in O(nk).
2. **The insight:** Nodes keep their circular order, so only the cut position changes.

### Optimal approach
1. Find the length and tail.
2. Close the ring.
3. Walk length minus k modulo length.
4. Cut.

### Dry run
```text
list = 1,2,3,4,5  k = 2
ring, 3 steps from 5  new tail 3
result                4,5,1,2,3
```

### Why it works
Cutting there puts exactly the last k nodes first and preserves every other neighbor.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null) return null;
        ListNode tail = head;
        int length = 1;
        while (tail.next != null) {
            tail = tail.next;
            length++;
        }
        tail.next = head;
        for (int i = 0; i < length - k % length; i++) tail = tail.next;
        ListNode newHead = tail.next;
        tail.next = null;
        return newHead;
    }
}
```

### Code walkthrough
- `tail.next = head`: Close the clasp.
- `k % length`: Whole laps change nothing.
- `tail.next = null`: Open the ring.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **k can be two billion.** Fix: reduce it modulo the length.
2. **An empty list divides by zero.** Fix: return early.
3. **Leaving the ring creates a cycle.** Fix: cut it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Empty | `[], 1` | `[]` |
| Lap | `[1,2], 2` | `[1,2]` |
| One | `[1], 9` | `[1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[049 — 189. Rotate Array]]: Reversals. The key idea there: A rotation is just two blocks trading places, which three reversals achieve.
- [[117 — 19. Remove Nth Node From End of List]]: The cut. The key idea there: A constant gap converts "from the end" into a position found in one pass.

### In a real interview
Say it in this order:

1. **Brute force:** Move the tail to the front k times, in O(nk).
2. **Insight:** Nodes keep their circular order, so only the cut position changes.
3. **Approach in a few words:** Close a ring and cut at length minus k.
4. **Cost:** time O(n); space O(1).

## 5. Recall
Q: 61. Rotate List: which pattern, and what is the insight?
A: Ring and cut. Nodes keep their circular order, so only the cut position changes.

Q: 61. Rotate List: what is the analogy, and what does it stand for?
A: A bracelet: ring → tail.next = head; opening → the new tail.

Q: 61. Rotate List: How is a rotation done with links?
A: Make a ring and cut at the new tail.

Q: 61. Rotate List: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Review 21 | Re-solve without looking | MUST
# Review 21

Solve on a blank page. No notes, hints or solutions.

## 61. Rotate List
Rotate a list right by k.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/rotate-list/)

## 24. Swap Nodes in Pairs
Swap adjacent nodes by changing links.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/swap-nodes-in-pairs/)

## 876. Middle of the Linked List
Return the middle node, second on ties.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/middle-of-the-linked-list/)

## 704. Binary Search
Find a target in a sorted array.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/binary-search/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 61 | | | | |
| 24 | | | | |
| 876 | | | | |
| 704 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[126 — 61. Rotate List]]
- [[123 — 24. Swap Nodes in Pairs]]
- [[112 — 876. Middle of the Linked List]]
- [[088 — 704. Binary Search]]

@@ 3 | TOPIC | 127 — 92. Reverse Linked List II | Medium · Sublist reversal | SHOULD
# 127 — 92. Reverse Linked List II

**Problem 127 – Topic: Linked List | Difficulty: Medium | Pattern: Sublist reversal**

[Open on LeetCode](https://leetcode.com/problems/reverse-linked-list-ii/)

## 1. Problem Statement
You get the head of a list and positions `left` and `right`, 1-indexed with left at most right. Reverse only the nodes in that range and return the head. Follow-up: use one pass.

### Examples
```text
Input:  head = [1,2,3,4,5], left = 2, right = 4
Output: [1,4,3,2,5]

Input:  head = [5], left = 1, right = 1
Output: [5]
```

### Constraints
```text
The number of nodes in the list is n.
1 <= n <= 500
-500 <= Node.val <= 500
1 <= left <= right <= n
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Pulling books to the front of a section
A bookend stands before a shelf section. Repeatedly pull out the book after the section's original first one and put it directly behind the bookend. The section reverses.

| In the story | In the solution |
|---|---|
| bookend | `before` |
| original first | `first`, sliding back |

### Pattern signal
**Sublist reversal.** Repeated insertion at the front reverses a range without disconnecting it.

### Thought process
1. **First idea:** Copy the range's values and write them back.
2. **The insight:** Repeated insertion at the front reverses a range without disconnecting it.

### Optimal approach
1. Reach the node before the range.
2. Fix the original first.
3. Move its successor behind the bookend.
4. Repeat.

### Dry run
```text
list = 1,2,3,4,5  left 2, right 4
move  result
3     1,3,2,4,5
4     1,4,3,2,5
```

### Why it works
After j moves the first j+1 nodes of the range are reversed and still connected on both sides.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head), before = dummy;
        for (int i = 1; i < left; i++) before = before.next;
        ListNode first = before.next;
        for (int i = left; i < right; i++) {
            ListNode moved = first.next;
            first.next = moved.next;
            moved.next = before.next;
            before.next = moved;
        }
        return dummy.next;
    }
}
```

### Code walkthrough
- `i < left`: Walk to the bookend.
- `first.next = moved.next`: Unclip the book being moved.
- `moved.next = before.next`: It goes before the current front.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Linking the moved node to first undoes earlier moves.** Fix: link it to the current front.
2. **first slides back by itself.** Fix: never advance it.
3. **left = 1 changes the head.** Fix: use a dummy.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[5], 1, 1` | `[5]` |
| Whole | `[1,2], 1, 2` | `[2,1]` |
| Tail | `[1,2,3], 2, 3` | `[1,3,2]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[107 — 206. Reverse Linked List]]: All. The key idea there: A reversal changes nothing but each arrow's direction.
- [[130 — 25. Reverse Nodes in k-Group]]: Repeat. The key idea there: Each group is an ordinary reversal whose previous starts at the node after the group.

### In a real interview
Say this: *"Head insertion keeps the list connected while reversing a range."*

## 5. Recall
Q: 92. Reverse Linked List II: which pattern, and what is the insight?
A: Sublist reversal. Repeated insertion at the front reverses a range without disconnecting it.

Q: 92. Reverse Linked List II: what is the analogy, and what does it stand for?
A: Pulling books to the front of a section: bookend → `before`; original first → `first`, sliding back.

Q: 92. Reverse Linked List II: Where is each moved node inserted?
A: Immediately after the node before the range.

Q: 92. Reverse Linked List II: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | 128 — 622. Design Circular Queue | Medium · Bounded queue | SHOULD
# 128 — 622. Design Circular Queue

**Problem 128 – Topic: Linked List | Difficulty: Medium | Pattern: Bounded queue**

[Open on LeetCode](https://leetcode.com/problems/design-circular-queue/)

## 1. Problem Statement
Design a queue of capacity k reusing freed slots: enQueue and deQueue report success, Front and Rear give -1 if empty, plus isEmpty and isFull.

### Examples
```text
Input:  ["MyCircularQueue", "enQueue", "enQueue", "enQueue", "enQueue", "Rear", "isFull", "deQueue", "enQueue", "Rear"]
        [[3], [1], [2], [3], [4], [], [], [], [4], []]
Output: [null, true, true, true, false, 3, true, true, true, 4]
```

### Constraints
```text
1 <= k <= 1000
0 <= value <= 1000
At most 3000 calls will be made to enQueue, deQueue, Front, Rear, isEmpty, and isFull.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Seats on a Ferris wheel
Seats on a Ferris wheel never move in memory. A marker says where the oldest rider sits and a count says how many follow, wrapping past the last seat.

| In the story | In the solution |
|---|---|
| marker | `front` |
| wrapping | modulo |

### Pattern signal
**Bounded queue.** The front index and the size determine every position, using modulo.

### Thought process
1. **First idea:** Shift everything left on each removal.
2. **The insight:** The front index and the size determine every position, using modulo.

### Optimal approach
1. Write at front plus size modulo capacity.
2. Advance the front to remove.
3. Compute the rear from both.

### Dry run
```text
capacity 3
operation  front  size  slots
add 1,2,3  0      3     1 2 3
remove     1      2     _ 2 3
add 4      1      3     4 2 3
```

### Why it works
The size cells starting at front, with wrapping, are always the queue's values in order.

## 3. Java Solution with Explanation
### Code
```java
class MyCircularQueue {
    private final int[] seats;
    private int front, size;

    public MyCircularQueue(int k) {
        seats = new int[k];
    }

    public boolean enQueue(int value) {
        if (isFull()) return false;
        seats[(front + size) % seats.length] = value;
        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) return false;
        front = (front + 1) % seats.length;
        size--;
        return true;
    }

    public int Front() {
        return isEmpty() ? -1 : seats[front];
    }

    public int Rear() {
        return isEmpty() ? -1 : seats[(front + size - 1) % seats.length];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == seats.length;
    }
}
```

### Code walkthrough
- `(front + size) % seats.length`: The next free seat.
- `front + size - 1`: The newest rider.
- `size == seats.length`: No confusion of full with empty.

### Complexity
- **Time:** O(1) per operation
- **Space:** O(k)

### Common mistakes
1. **Two indices alone confuse full and empty.** Fix: keep a size.
2. **The index runs off the array.** Fix: wrap with modulo.
3. **front+size is the free slot.** Fix: read the rear at one less.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `k=1, add 1, add 2` | `true, false` |
| Empty | `Front` | `-1` |
| Wrap | `k=1, add, remove, add` | `all true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **641. Circular Deque**: Both ends.
- [[071 — 232. Implement Queue using Stacks]]: Unbounded. The key idea there: One reversal fixes the order, and it stays fixed until that batch is used up.

### In a real interview
Say it in this order:

1. **Brute force:** Shift everything left on each removal.
2. **Insight:** The front index and the size determine every position, using modulo.
3. **Approach in a few words:** A ring with front and size avoids shifting.
4. **Cost:** time O(1) per operation; space O(k).

## 5. Recall
Q: 622. Design Circular Queue: which pattern, and what is the insight?
A: Bounded queue. The front index and the size determine every position, using modulo.

Q: 622. Design Circular Queue: what is the analogy, and what does it stand for?
A: Seats on a Ferris wheel: marker → `front`; wrapping → modulo.

Q: 622. Design Circular Queue: Where is the next insertion?
A: At front plus size, modulo capacity.

Q: 622. Design Circular Queue: what are the time and space costs?
A: Time O(1) per operation. Space O(k).

@@ 2 | MODULE | Linked List — Hard | 2 problems | MUST
# Linked List: Hard

Patterns in this group: Merge many, Group reversal.

@@ 3 | TOPIC | 129 — 23. Merge k Sorted Lists | Hard · Merge many | MUST
# 129 — 23. Merge k Sorted Lists

**Problem 129 – Topic: Linked List | Difficulty: Hard | Pattern: Merge many**

[Open on LeetCode](https://leetcode.com/problems/merge-k-sorted-lists/)

## 1. Problem Statement
You get an array of k linked lists, each sorted in ascending order. Any of them may be empty. Merge all of them into one sorted linked list and return its head.

### Examples
```text
Input:  lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]

Input:  lists = []
Output: []

Input:  lists = [[]]
Output: []
```

### Constraints
```text
k == lists.length
0 <= k <= 10^4
0 <= lists[i].length <= 500
-10^4 <= lists[i][j] <= 10^4
lists[i] is sorted in ascending order.
The sum of lists[i].length will not exceed 10^4.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: One exit for k checkout lanes
A shop has k checkout lanes feeding one exit. Only the front of each lane matters. A board shows the smallest, and when they leave the one behind them goes up.

| In the story | In the solution |
|---|---|
| board | min-heap |
| lane front | the node in the heap |

### Pattern signal
**Merge many.** You repeatedly need the minimum of k items of which only one changes, which is a heap.

### Thought process
1. **First idea:** Scan all k fronts at each step, in O(Nk).
2. **The insight:** You repeatedly need the minimum of k items of which only one changes, which is a heap.

### Optimal approach
1. Push each non-null head.
2. Poll the smallest.
3. Append it.
4. Push its successor.

### Dry run
```text
lists = 1,4  1,3  2
board  taken  result
1,1,2  1      1
1,2,4  1      1,1
2,3,4  2      1,1,2
3,4    3      1,1,2,3
4      4      1,1,2,3,4
```

### Why it works
Every unmerged node is behind some front, so the smallest front is the global next.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> board = new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));
        for (ListNode head : lists) {
            if (head != null) board.add(head);
        }
        ListNode dummy = new ListNode(0), tail = dummy;
        while (!board.isEmpty()) {
            tail.next = board.poll();
            tail = tail.next;
            if (tail.next != null) board.add(tail.next);
        }
        return dummy.next;
    }
}
```

### Code walkthrough
- `Integer.compare`: Order nodes by value safely.
- `head != null`: An empty lane has no front.
- `board.add(tail.next)`: The person behind moves up.

### Complexity
- **Time:** O(N log k)
- **Space:** O(k)

### Common mistakes
1. **A heap rejects null.** Fix: skip empty lists.
2. **Merging into one growing list is O(Nk).** Fix: use a heap.
3. **Pushing every node wastes memory.** Fix: push only fronts.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `[]` | `[]` |
| Empty lists | `[ [] ]` | `[]` |
| Single | `[ [1] ]` | `[1]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[108 — 21. Merge Two Sorted Lists]]: No heap. The key idea there: The smallest remaining node is always one of the two current heads.
- **378. Kth in Sorted Matrix**: Stop after k.

### In a real interview
Say it in this order:

1. **Brute force:** Scan all k fronts at each step, in O(Nk).
2. **Insight:** You repeatedly need the minimum of k items of which only one changes, which is a heap.
3. **Approach in a few words:** A heap of the heads gives N log k.
4. **Cost:** time O(N log k); space O(k).

## 5. Recall
Q: 23. Merge k Sorted Lists: which pattern, and what is the insight?
A: Merge many. You repeatedly need the minimum of k items of which only one changes, which is a heap.

Q: 23. Merge k Sorted Lists: what is the analogy, and what does it stand for?
A: One exit for k checkout lanes: board → min-heap; lane front → the node in the heap.

Q: 23. Merge k Sorted Lists: What is in the heap?
A: At most one front node per list.

Q: 23. Merge k Sorted Lists: what are the time and space costs?
A: Time O(N log k). Space O(k).

@@ 3 | TOPIC | 130 — 25. Reverse Nodes in k-Group | Hard · Group reversal | MUST
# 130 — 25. Reverse Nodes in k-Group

**Problem 130 – Topic: Linked List | Difficulty: Hard | Pattern: Group reversal**

[Open on LeetCode](https://leetcode.com/problems/reverse-nodes-in-k-group/)

## 1. Problem Statement
Reverse each consecutive group of `k` nodes, leaving a shorter remainder, with O(1) space.

### Examples
```text
Input:  head = [1,2,3,4,5], k = 2
Output: [2,1,4,3,5]

Input:  head = [1,2,3,4,5], k = 3
Output: [3,2,1,4,5]
```

### Constraints
```text
The number of nodes in the list is n.
1 <= k <= n <= 5000
0 <= Node.val <= 1000
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Turning train sections on a turntable
A turntable turns train sections of k carriages. Count first: too few are left alone. Otherwise turn them so the new tail is already coupled to the rest, then couple the previous section to the new front.

| In the story | In the solution |
|---|---|
| count first | find `last` |
| coupling | `before` |

### Pattern signal
**Group reversal.** Each group is an ordinary reversal whose previous starts at the node after the group.

### Thought process
1. **First idea:** Store nodes in an array, using O(n) space.
2. **The insight:** Each group is an ordinary reversal whose previous starts at the node after the group.

### Optimal approach
1. Find the kth node.
2. Stop if absent.
3. Reverse to the next group.
4. Reconnect.
5. Move before to the group's new tail.

### Dry run
```text
list = 1,2,3,4,5  k = 2
group  result
1,2    2,1,3,4,5
3,4    2,1,4,3,5
5      short, unchanged
```

### Why it works
Everything through before is finished and connected, and each round finishes exactly one complete group.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode(0, head), before = dummy;
        while (true) {
            ListNode last = before;
            for (int i = 0; i < k && last != null; i++) last = last.next;
            if (last == null) return dummy.next;
            ListNode first = before.next, after = last.next;
            ListNode previous = after, current = first;
            while (current != after) {
                ListNode next = current.next;
                current.next = previous;
                previous = current;
                current = next;
            }
            before.next = last;
            before = first;
        }
    }
}
```

### Code walkthrough
- `last == null`: Too few carriages, so leave them.
- `previous = after`: The new tail is coupled in advance.
- `before.next = last`: Couple to the new front.
- `before = first`: The old first is the new tail.

### Complexity
- **Time:** O(n)
- **Space:** O(1)

### Common mistakes
1. **Reversing before counting changes the remainder.** Fix: find the kth first.
2. **A null previous loses the rest.** Fix: start it at the next group.
3. **last is the front afterwards.** Fix: move before to first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| k one | `[1,2], 1` | `[1,2]` |
| Whole | `[1,2], 2` | `[2,1]` |
| Short tail | `[1,2,3], 2` | `[2,1,3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[123 — 24. Swap Nodes in Pairs]]: k = 2. The key idea there: One swap always involves the same three links.
- [[127 — 92. Reverse Linked List II]]: One group. The key idea there: Repeated insertion at the front reverses a range without disconnecting it.

### In a real interview
Say this: *"I verify a group, reverse to its boundary and reconnect."*

## 5. Recall
Q: 25. Reverse Nodes in k-Group: which pattern, and what is the insight?
A: Group reversal. Each group is an ordinary reversal whose previous starts at the node after the group.

Q: 25. Reverse Nodes in k-Group: what is the analogy, and what does it stand for?
A: Turning train sections on a turntable: count first → find `last`; coupling → `before`.

Q: 25. Reverse Nodes in k-Group: What should previous start as in a group reversal?
A: The node after the group.

Q: 25. Reverse Nodes in k-Group: what are the time and space costs?
A: Time O(n). Space O(1).

@@ 3 | TOPIC | Practice — Pattern summary: Linked List | Key patterns of the topic | MUST
# Pattern summary: Linked List

Draw boxes and arrows. Save an arrow before you overwrite it, use a dummy node when the head can change, and use fast and slow pointers for positions.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Pointer reversal | Reverse a singly linked list. (206) | Losing the rest of the list |
| Dummy and tail | Merge two sorted linked lists. (21) | Forgetting the remainder |
| Middle and reverse | Decide whether a linked list is a palindrome. (234, 2130) | Mishandling odd length |
| Unlinking | Remove every node with a given value. (203, 83) | Consecutive matches |
| Fast and slow | Return the middle node, second on ties. (876) | Returning the first middle |
| Path switching | Find the node where two lists intersect. (160) | Comparing values instead of nodes |
| Floyd cycle | Detect a cycle in a linked list. (141) | Dereferencing a null next |
| Middle, reverse, merge | Reorder to first, last, second, second last and so on. (143) | Not cutting the first half |
| Fixed gap | Remove the nth node from the end. (19) | Removing the head |
| Old-to-new map | Deep-copy a list with random pointers. (138) | Pointing into the original |
| Node manipulation | Design a linked list with indexed insert and delete. (707) | Inserting exactly at the end |
| History with cursor | Implement visit, back and forward of a browser. (1472) | Keeping forward history after a visit |
| Digit addition | Add numbers stored as reversed digit lists. (2) | The final carry |
| Floyd on values | Find the repeated value without modifying the array, O(1) space. (287) | Comparing before the first move |
| Pair rewiring | Swap adjacent nodes by changing links. (24) | Swapping values |
| List merge sort | Sort a linked list in O(n log n). (148) | Not cutting at the middle |
| Two lists | Place nodes below x before the rest, keeping order. (86) | A cycle from the old last link |
| Ring and cut | Rotate a list right by k. (61) | Walking k times for huge k |
| Sublist reversal | Reverse positions left to right of a list. (92) | A reversal starting at the head |
| Bounded queue | Design a fixed-capacity circular queue. (622) | Confusing full with empty |
| Merge many | Merge k sorted linked lists. (23) | Appending one by one is O(N·k) |
| Group reversal | Reverse each full group of k nodes. (25) | Reversing a short final group |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| reverse | turning one-way signs around | save and rewire | 206, 92, 25 |
| the head may change | a "start here" sign | dummy node | 203, 19, 24, 86 |
| middle, cycle | hare and tortoise | fast and slow | 876, 141, 287 |
| first with last | fold the paper in half | middle and reverse | 234, 143, 2130 |
| nth from the end | two hikers on a rope | fixed gap | 19 |
| do two lists meet | swap starting lines | path switching | 160 |
| merge many | several checkout lanes | heap | 23 |

## Which tool do I pick?

1. **Could the head change?** Start with a dummy.
2. **Need a position without a length?** Use two pointers with a speed or gap difference.
3. **Need to walk backwards?** Reverse that part.
4. **Before every `.next =`, ask:** "who still remembers the node I am disconnecting?"

Q: Which signals point to Linked List?
A: relink; middle; cycle; nth from the end.

Q: What is the core idea of Linked List?
A: Draw boxes and arrows. Save an arrow before you overwrite it, use a dummy node when the head can change, and use fast and slow pointers for positions.
