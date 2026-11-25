@@ 1 | MODULE | Phase 4 — Stack | nesting; undo; most recent; next greater or smaller | MUST
# Phase 4: Stack

A stack holds work waiting for an answer, newest first. A monotonic stack also keeps that waiting work sorted, so one arrival can resolve many items.

**Signals:** nesting; undo; most recent; next greater or smaller.

## The big analogy: a pile of plates

You can only add or take the top plate, and that is exactly right whenever **the newest unfinished thing must be finished first**: the last bracket opened, the last folder entered, the last action undone. A **monotonic stack** is a pile with a bouncer: before a newcomer lands, everything it beats is removed, and is told "you just met your answer".

| What waits on the pile | Picture | Meet it in |
|---|---|---|
| Open brackets and nested work | boxes inside boxes | 20, 394 |
| History to undo | the undo button | 2390, 71, 682 |
| Operands | ingredients waiting for a recipe step | 150 |
| People waiting for someone bigger | a queue craning their necks | 496, 739, 901 |
| Bars still able to widen | buildings under one roof | 84 |

## Template
```java
Deque<Integer> waiting = new ArrayDeque<>(); // indices
for (int i = 0; i < a.length; i++) {
    while (!waiting.isEmpty() && a[waiting.peek()] < a[i]) answer[waiting.pop()] = a[i];
    waiting.push(i);
}
```

The template is a skeleton. Names that it does not declare, such as the input or a helper check, come from the problem you are solving. Write the invariant as a comment before filling it in.

@@ 2 | MODULE | Stack — Easy | 5 problems | MUST
# Stack: Easy

Patterns in this group: Monotonic stack, Matching stack, Record stack, Queue rotation, Two stacks.

@@ 3 | TOPIC | 067 — 496. Next Greater Element I | Easy · Monotonic stack | NICE
# 067 — 496. Next Greater Element I

**Problem 67 – Topic: Stack | Difficulty: Easy | Pattern: Monotonic stack**

[Open on LeetCode](https://leetcode.com/problems/next-greater-element-i/)

## 1. Problem Statement
`nums1` is a subset of `nums2`, all values unique. For each value of `nums1` return the first larger value to its right in `nums2`, or -1.

### Examples
```text
Input:  nums1 = [4,1,2], nums2 = [1,3,4,2]
Output: [-1,3,-1]

Input:  nums1 = [2,4], nums2 = [1,2,3,4]
Output: [3,-1]
```

### Constraints
```text
1 <= nums1.length <= nums2.length <= 1000
0 <= nums1[i], nums2[i] <= 10^4
All integers in nums1 and nums2 are unique.
All the integers of nums1 also appear in nums2.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A crowd waiting for someone taller
People join a line and wait to see someone taller. A tall arrival answers all the shorter people at the back at once, and they can go home with that name.

| In the story | In the solution |
|---|---|
| waiting people | a decreasing stack |
| the name they take home | the `next` map |

### Pattern signal
**Monotonic stack.** Values without an answer are always decreasing, so each arrival only checks the top.

### Thought process
1. **First idea:** Scan right from each queried value, in O(n·m).
2. **The insight:** Values without an answer are always decreasing, so each arrival only checks the top.

### Optimal approach
1. Walk the second array.
2. Pop and answer every smaller waiting value.
3. Push the arrival.
4. Look up each query.

### Dry run
```text
nums2 = [1,3,4,2]
x  stack before  answered  stack after
1  []            -         [1]
3  [1]           1 gets 3  [3]
4  [3]           3 gets 4  [4]
2  [4]           -         [4,2]
Queries [4,1,2] give [-1,3,-1]
```

### Why it works
A value leaves the stack at the first later value that exceeds it, which is the definition of next greater.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> next = new HashMap<>();
        Deque<Integer> waiting = new ArrayDeque<>();
        for (int x : nums2) {
            while (!waiting.isEmpty() && waiting.peek() < x) next.put(waiting.pop(), x);
            waiting.push(x);
        }
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) result[i] = next.getOrDefault(nums1[i], -1);
        return result;
    }
}
```

### Code walkthrough
- `waiting.peek() < x`: The arrival is taller.
- `next.put`: Values are unique, so they work as keys.
- `getOrDefault`: Still waiting means -1.

### Complexity
- **Time:** O(n + m), one push and pop each
- **Space:** O(m)

### Common mistakes
1. **Choosing the largest to the right.** The first larger is wanted.
2. **Searching leftward.** Only later values count.
3. **Using `if`.** One arrival can answer many.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `[2], [2,1]` | `[-1]` |
| Immediate | `[1], [1,2]` | `[2]` |
| Far | `[3], [3,1,4]` | `[4]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **503. Next Greater Element II**: Walk the array twice.
- [[078 — 739. Daily Temperatures]]: Return distances. The key idea there: Unresolved temperatures never increase along the stack, so a warm day resolves a suffix.

### In a real interview
Say this: *"Unresolved values form a decreasing stack. Each is pushed and popped once, so it is linear."*

## 5. Recall
Q: 496. Next Greater Element I: which pattern, and what is the insight?
A: Monotonic stack. Values without an answer are always decreasing, so each arrival only checks the top.

Q: 496. Next Greater Element I: what is the analogy, and what does it stand for?
A: A crowd waiting for someone taller: waiting people → a decreasing stack; the name they take home → the `next` map.

Q: 496. Next Greater Element I: What does the stack hold?
A: Values still waiting for a greater one, in decreasing order.

Q: 496. Next Greater Element I: what are the time and space costs?
A: Time O(n + m), one push and pop each. Space O(m).

@@ 3 | TOPIC | 068 — 20. Valid Parentheses | Easy · Matching stack | MUST
# 068 — 20. Valid Parentheses

**Problem 68 – Topic: Stack | Difficulty: Easy | Pattern: Matching stack**

[Open on LeetCode](https://leetcode.com/problems/valid-parentheses/)

## 1. Problem Statement
`s` consists of ()[]{}. Return true if every bracket is closed by the same type in the correct order.

### Examples
```text
Input:  s = "()"
Output: true

Input:  s = "()[]{}"
Output: true

Input:  s = "(]"
Output: false

Input:  s = "([])"
Output: true

Input:  s = "([)]"
Output: false
```

### Constraints
```text
1 <= s.length <= 10^4
s consists of parentheses only '()[]{}'.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Boxes inside boxes
The box opened last must be closed first. Each time you open one, put the lid you will need on a pile. Every closing must use the top lid.

| In the story | In the solution |
|---|---|
| lid needed | the expected closer |
| pile of lids | the stack |

### Pattern signal
**Matching stack.** Only the newest unclosed opener limits the next closer.

### Thought process
1. **First idea:** Repeatedly delete adjacent pairs, in O(n²).
2. **The insight:** Only the newest unclosed opener limits the next closer.

### Optimal approach
1. Push the matching closer for an opener.
2. Require a closer to equal the top.
3. Require an empty stack at the end.

### Dry run
```text
s = "{[]}"
char  action       stack
{     push }       }
[     push ]       } ]
]     matches top  }
}     matches top  empty, true
```

### Why it works
The stack is precisely the closers still owed, innermost on top. Valid means never paying the wrong one and owing nothing.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean isValid(String s) {
        Deque<Character> lids = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(') lids.push(')');
            else if (c == '[') lids.push(']');
            else if (c == '{') lids.push('}');
            else if (lids.isEmpty() || lids.pop() != c) return false;
        }
        return lids.isEmpty();
    }
}
```

### Code walkthrough
- `lids.push(')')`: Push what you will need.
- `lids.pop() != c`: The wrong lid.
- `return lids.isEmpty()`: No box is left open.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Three counters.** "([)]" passes wrongly.
2. **Popping an empty stack.** Check first.
3. **Returning true at the end.** "(" is invalid.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Closer first | `"]"` | `false` |
| Crossed | `"([)]"` | `false` |
| Open | `"(("` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[285 — 678. Valid Parenthesis String]]: Wildcards need more state. The key idea there: Save the flexible stars as long as possible and store positions so the order can be checked afterwards.
- [[082 — 394. Decode String]]: Work waits in each box. The key idea there: At a closer only the newest bookmark matters.

### In a real interview
Say this: *"Nesting is last in, first out. I push expected closers and compare."*

## 5. Recall
Q: 20. Valid Parentheses: which pattern, and what is the insight?
A: Matching stack. Only the newest unclosed opener limits the next closer.

Q: 20. Valid Parentheses: what is the analogy, and what does it stand for?
A: Boxes inside boxes: lid needed → the expected closer; pile of lids → the stack.

Q: 20. Valid Parentheses: What must be true at the end?
A: No opener is left unmatched.

Q: 20. Valid Parentheses: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 069 — 682. Baseball Game | Easy · Record stack | NICE
# 069 — 682. Baseball Game

**Problem 69 – Topic: Stack | Difficulty: Easy | Pattern: Record stack**

[Open on LeetCode](https://leetcode.com/problems/baseball-game/)

## 1. Problem Statement
Process score operations: an integer records it, "+" records the sum of the last two, "D" records double the last, "C" deletes the last. Return the total of the record.

### Examples
```text
Input:  ops = ["5","2","C","D","+"]
Output: 30

Input:  ops = ["5","-2","4","C","D","9","+","+"]
Output: 27

Input:  ops = ["1","C"]
Output: 0
```

### Constraints
```text
1 <= operations.length <= 1000
operations[i] is "C", "D", "+", or a string representing an integer in the range [-3 * 10^4, 3 * 10^4].
For operation "+", there will always be at least two previous scores on the record.
For operations "C" and "D", there will always be at least one previous score on the record.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A scorekeeper with sticky notes
Each score is a note on a pile. Every rule reads or removes only the top one or two notes.

| In the story | In the solution |
|---|---|
| notes | a stack |
| cancel | pop |

### Pattern signal
**Record stack.** All operations refer to the most recent valid scores, which is a stack by definition.

### Thought process
1. **First idea:** A list with searches or rebuilding works, but hides the fact that only the end changes.
2. **The insight:** All operations refer to the most recent valid scores, which is a stack by definition.

### Optimal approach
1. Pop on C.
2. Push double the top on D.
3. Push the top two's sum on plus without losing them.
4. Push numbers.
5. Add up.

### Dry run
```text
ops = [5,2,C,D,+]
op  stack
5   5
2   5 2
C   5
D   5 10
+   5 10 15, total 30
```

### Why it works
After each operation the stack equals the official record, cancellations included, so its sum is the answer.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int calPoints(String[] operations) {
        List<Integer> record = new ArrayList<>(); // the end is the top
        for (String op : operations) {
            int n = record.size();
            switch (op) {
                case "C" -> record.remove(n - 1);
                case "D" -> record.add(record.get(n - 1) * 2);
                case "+" -> record.add(record.get(n - 1) + record.get(n - 2));
                default -> record.add(Integer.parseInt(op));
            }
        }
        int total = 0;
        for (int score : record) total += score;
        return total;
    }
}
```

### Code walkthrough
- `record.remove(n - 1)`: Peel the top note.
- `record.get(n - 2)`: A list lets us read two notes without popping.
- `parseInt`: Handles negatives.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Popping operands for plus.** They must remain.
2. **A running answer without history.** C cannot be undone.
3. **Testing for a digit first.** Negatives start with a minus.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Cancel all | `["1","C"]` | `0` |
| Negative | `["-2","D"]` | `-6` |
| One | `["5"]` | `5` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[073 — 150. Evaluate Reverse Polish Notation]]: Operands are consumed. The key idea there: An operator always applies to the two most recent unfinished values.
- [[072 — 155. Min Stack]]: A statistic survives undo. The key idea there: The minimum below a plate never changes while the plate is there, so it can be saved with it.

### In a real interview
Say this: *"Every rule touches the latest records, so a stack simulates it directly in linear time."*

## 5. Recall
Q: 682. Baseball Game: which pattern, and what is the insight?
A: Record stack. All operations refer to the most recent valid scores, which is a stack by definition.

Q: 682. Baseball Game: what is the analogy, and what does it stand for?
A: A scorekeeper with sticky notes: notes → a stack; cancel → pop.

Q: 682. Baseball Game: Are plus's operands removed?
A: No, their sum is added on top.

Q: 682. Baseball Game: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 070 — 225. Implement Stack using Queues | Easy · Queue rotation | NICE
# 070 — 225. Implement Stack using Queues

**Problem 70 – Topic: Stack | Difficulty: Easy | Pattern: Queue rotation**

[Open on LeetCode](https://leetcode.com/problems/implement-stack-using-queues/)

## 1. Problem Statement
Implement a stack (push, pop, top, empty) using only queue operations: add at the back, remove or read at the front, size and empty.

### Examples
```text
Input:  ["MyStack", "push", "push", "top", "pop", "empty"]
        [[], [1], [2], [], [], []]
Output: [null, null, null, 2, 2, false]
```

### Constraints
```text
1 <= x <= 9
At most 100 calls will be made to push, pop, top, and empty.
All the calls to pop and top are valid.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A carousel of suitcases
A new suitcase goes on at the back. Spin all the older ones past the pickup point and behind it, and the newest now stands at the front.

| In the story | In the solution |
|---|---|
| spinning | remove front, add back |
| pickup point | the queue's front |

### Pattern signal
**Queue rotation.** Rotating once at push keeps the queue permanently in stack order.

### Thought process
1. **First idea:** Move everything between two queues on every pop and top.
2. **The insight:** Rotating once at push keeps the queue permanently in stack order.

### Optimal approach
1. Add the value.
2. Move each older value from front to back.
3. Serve pop and top from the front.

### Dry run
```text
operation  after add   after rotation
push 1     1           1
push 2     1 2         2 1
push 3     2 1 3       3 2 1
```

### Why it works
If the queue was newest to oldest, moving all of it behind the new value keeps that order and puts the newest first.

## 3. Java Solution with Explanation
### Code
```java
class MyStack {
    private final Queue<Integer> queue = new ArrayDeque<>();

    public void push(int x) {
        queue.add(x);
        for (int i = 1; i < queue.size(); i++) queue.add(queue.poll());
    }

    public int pop() {
        return queue.poll();
    }

    public int top() {
        return queue.peek();
    }

    public boolean empty() {
        return queue.isEmpty();
    }
}
```

### Code walkthrough
- `int i = 1`: Spin size minus one, all but the newcomer.
- `queue.add(queue.poll())`: One turn of the carousel.
- `queue.peek()`: The front is the top.

### Complexity
- **Time:** O(n) per push, O(1) for the rest
- **Space:** O(n)

### Common mistakes
1. **Reading the back.** A queue doesn't allow it.
2. **Rotating size times.** Nothing changes.
3. **Rotating again on pop.** The order is already right.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `push(1), top` | `1` |
| Two | `push(1), push(2), pop` | `2` |
| Emptied | `push(1), pop, empty` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[071 — 232. Implement Queue using Stacks]]: The mirror, amortized O(1). The key idea there: One reversal fixes the order, and it stays fixed until that batch is used up.
- [[128 — 622. Design Circular Queue]]: A queue from an array. The key idea there: The front index and the size determine every position, using modulo.

### In a real interview
Say this: *"I make push expensive by rotating. Then the front is always the stack top."*

## 5. Recall
Q: 225. Implement Stack using Queues: which pattern, and what is the insight?
A: Queue rotation. Rotating once at push keeps the queue permanently in stack order.

Q: 225. Implement Stack using Queues: what is the analogy, and what does it stand for?
A: A carousel of suitcases: spinning → remove front, add back; pickup point → the queue's front.

Q: 225. Implement Stack using Queues: How many rotations after a push?
A: Size minus one.

Q: 225. Implement Stack using Queues: what are the time and space costs?
A: Time O(n) per push, O(1) for the rest. Space O(n).

@@ 3 | TOPIC | 071 — 232. Implement Queue using Stacks | Easy · Two stacks | NICE
# 071 — 232. Implement Queue using Stacks

**Problem 71 – Topic: Stack | Difficulty: Easy | Pattern: Two stacks**

[Open on LeetCode](https://leetcode.com/problems/implement-queue-using-stacks/)

## 1. Problem Statement
Implement a queue (push, pop, peek, empty) using stacks. Aim for amortized O(1).

### Examples
```text
Input:  ["MyQueue", "push", "push", "peek", "pop", "empty"]
        [[], [1], [2], [], [], []]
Output: [null, null, null, 1, 1, false]
```

### Constraints
```text
1 <= x <= 9
At most 100 calls will be made to push, pop, peek, and empty.
All the calls to pop and peek are valid.
```

**Stop here and try it for 15 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An in-tray and an out-tray
Papers pile on the in-tray, newest on top. When the out-tray is empty, flip the whole in-tray into it and the oldest is on top. Never flip onto unfinished work.

| In the story | In the solution |
|---|---|
| trays | two stacks |
| flipping | pop and push everything |

### Pattern signal
**Two stacks.** One reversal fixes the order, and it stays fixed until that batch is used up.

### Thought process
1. **First idea:** Flip there and back on each read, in O(n).
2. **The insight:** One reversal fixes the order, and it stays fixed until that batch is used up.

### Optimal approach
1. Push to in.
2. Before reading refill out only if empty.
3. Read from out.
4. Empty means both are empty.

### Dry run
```text
operation  in    out, top first
push 1,2   1 2   -
peek       -     1 2
push 3     3     1 2
pop gives 1  3   2
pop gives 2  3   -
```

### Why it works
Everything in out is older than everything in in, and out has its oldest on top.

## 3. Java Solution with Explanation
### Code
```java
class MyQueue {
    private final Deque<Integer> in = new ArrayDeque<>();
    private final Deque<Integer> out = new ArrayDeque<>();

    public void push(int x) {
        in.push(x);
    }

    public int pop() {
        peek(); // makes sure out is ready
        return out.pop();
    }

    public int peek() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) out.push(in.pop());
        }
        return out.peek();
    }

    public boolean empty() {
        return in.isEmpty() && out.isEmpty();
    }
}
```

### Code walkthrough
- `out.isEmpty()`: The only moment a flip is safe.
- `out.push(in.pop())`: Reverses the batch.
- `in.isEmpty() && out.isEmpty()`: Work can be in either tray.

### Complexity
- **Time:** O(1) amortized, each value moves once
- **Space:** O(n)

### Common mistakes
1. **Refilling a non-empty out.** Order breaks.
2. **Flipping back.** The amortized bound is lost.
3. **Checking one tray.** Values hide in the other.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `push(1), pop` | `1` |
| Mixed | `push(1), peek, push(2), pop` | `1` |
| New | `empty` | `true` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[070 — 225. Implement Stack using Queues]]: The mirror. The key idea there: Rotating once at push keeps the queue permanently in stack order.
- [[066 — 239. Sliding Window Maximum]]: Two min-max stacks can also make such a queue.

### In a real interview
Say this: *"Two reversals make FIFO. Refilling only when empty moves each element once."*

## 5. Recall
Q: 232. Implement Queue using Stacks: which pattern, and what is the insight?
A: Two stacks. One reversal fixes the order, and it stays fixed until that batch is used up.

Q: 232. Implement Queue using Stacks: what is the analogy, and what does it stand for?
A: An in-tray and an out-tray: trays → two stacks; flipping → pop and push everything.

Q: 232. Implement Queue using Stacks: When is the out stack refilled?
A: Only when it is empty.

Q: 232. Implement Queue using Stacks: what are the time and space costs?
A: Time O(1) amortized, each value moves once. Space O(n).

@@ 2 | MODULE | Stack — Medium | 14 problems | MUST
# Stack: Medium

Patterns in this group: Auxiliary minimum, Operand stack, Undo stack, Simulation stack, Open and close choices, Collision stack, Monotonic stack, Stack of spans, Arrival-time stack, Path stack, Nested decoding, Increasing stack, Run-length stack, Decreasing stack.

@@ 3 | TOPIC | 072 — 155. Min Stack | Medium · Auxiliary minimum | MUST
# 072 — 155. Min Stack

**Problem 72 – Topic: Stack | Difficulty: Medium | Pattern: Auxiliary minimum**

[Open on LeetCode](https://leetcode.com/problems/min-stack/)

## 1. Problem Statement
Design a stack with push, pop, top and getMin, all O(1).

### Examples
```text
Input:  ["MinStack","push","push","push","getMin","pop","top","getMin"]
        [[],[-2],[0],[-3],[],[],[],[]]
Output: [null,null,null,null,-3,null,0,-2]
```

### Constraints
```text
-2^31 <= val <= 2^31 - 1
Methods pop, top and getMin operations will always be called on non-empty stacks.
At most 3 * 10^4 calls will be made to push, pop, top, and getMin.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A note on every plate
When a plate lands, write on it the smallest value in the pile so far. Removing it automatically uncovers the older note.

| In the story | In the solution |
|---|---|
| the note | the saved minimum |
| uncovering | pop |

### Pattern signal
**Auxiliary minimum.** The minimum below a plate never changes while the plate is there, so it can be saved with it.

### Thought process
1. **First idea:** Scan for the minimum, in O(n), or use one variable that pop cannot undo.
2. **The insight:** The minimum below a plate never changes while the plate is there, so it can be saved with it.

### Optimal approach
1. On push store the value and the minimum including it.
2. On pop remove both.
3. Read both from the top.

### Dry run
```text
operation  plates (value,min)      getMin
push -2    (-2,-2)                 -2
push 0     (-2,-2) (0,-2)          -2
push -3    (-2,-2) (0,-2) (-3,-3)  -3
pop        (-2,-2) (0,-2)          -2
```

### Why it works
Each note is the minimum of that plate and everything below, which is exactly the stack when it is on top.

## 3. Java Solution with Explanation
### Code
```java
class MinStack {
    private final Deque<int[]> plates = new ArrayDeque<>(); // value, minimum

    public void push(int val) {
        int min = plates.isEmpty() ? val : Math.min(val, getMin());
        plates.push(new int[] {val, min});
    }

    public void pop() {
        plates.pop();
    }

    public int top() {
        return plates.peek()[0];
    }

    public int getMin() {
        return plates.peek()[1];
    }
}
```

### Code walkthrough
- `Math.min(val, getMin())`: The note for the new plate.
- `new int[] {val, min}`: Value and note travel together.
- `plates.peek()[1]`: No search.

### Complexity
- **Time:** O(1) per operation
- **Space:** O(n)

### Common mistakes
1. **One variable.** It can't be restored.
2. **A heap.** Removal costs more.
3. **Dropping equal minima in a second stack.** A duplicate pop loses it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `push(5), getMin` | `5` |
| Equal | `push(1), push(1), pop, getMin` | `1` |
| Restore | `push(2), push(0), pop, getMin` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[086 — 895. Maximum Frequency Stack]]: A richer best. The key idea there: One stack per frequency keeps recency and frequency together, with no moving.
- [[079 — 901. Online Stock Span]]: Saved summaries. The key idea there: If yesterday absorbed a block, anything at least as large takes the whole block.

### In a real interview
Say this: *"I snapshot the minimum with each push, so pop restores it automatically."*

## 5. Recall
Q: 155. Min Stack: which pattern, and what is the insight?
A: Auxiliary minimum. The minimum below a plate never changes while the plate is there, so it can be saved with it.

Q: 155. Min Stack: what is the analogy, and what does it stand for?
A: A note on every plate: the note → the saved minimum; uncovering → pop.

Q: 155. Min Stack: What is stored with each value?
A: The minimum from the bottom up to it.

Q: 155. Min Stack: what are the time and space costs?
A: Time O(1) per operation. Space O(n).

@@ 3 | TOPIC | Practice — Review 12 | Re-solve without looking | MUST
# Review 12

Solve on a blank page. No notes, hints or solutions.

## 155. Min Stack
Design a stack with O(1) minimum.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/min-stack/)

## 682. Baseball Game
Evaluate baseball score operations.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/baseball-game/)

## 567. Permutation in String
Decide whether s2 contains a permutation of s1.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/permutation-in-string/)

## 68. Text Justification
Format words into fully justified lines of a given width.

Time limit: 40 minutes. [Problem on LeetCode](https://leetcode.com/problems/text-justification/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 155 | | | | |
| 682 | | | | |
| 567 | | | | |
| 68 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[072 — 155. Min Stack]]
- [[069 — 682. Baseball Game]]
- [[058 — 567. Permutation in String]]
- [[034 — 68. Text Justification]]

@@ 3 | TOPIC | 073 — 150. Evaluate Reverse Polish Notation | Medium · Operand stack | MUST
# 073 — 150. Evaluate Reverse Polish Notation

**Problem 73 – Topic: Stack | Difficulty: Medium | Pattern: Operand stack**

[Open on LeetCode](https://leetcode.com/problems/evaluate-reverse-polish-notation/)

## 1. Problem Statement
Evaluate an expression in Reverse Polish Notation with + - * and division truncating toward zero.

### Examples
```text
Input:  tokens = ["2","1","+","3","*"]
Output: 9

Input:  tokens = ["4","13","5","/","+"]
Output: 6

Input:  tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
Output: 22
```

### Constraints
```text
1 <= tokens.length <= 10^4
tokens[i] is either an operator: "+", "-", "*", or "/", or an integer in the range [-200, 200].
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A cook with prepared bowls
Ingredients are set on the counter. An instruction takes the last two bowls, combines them, and puts one bowl back.

| In the story | In the solution |
|---|---|
| bowls | the operand stack |
| instruction | an operator |

### Pattern signal
**Operand stack.** An operator always applies to the two most recent unfinished values.

### Thought process
1. **First idea:** Repeatedly find the first operator and shrink the list, in O(n²).
2. **The insight:** An operator always applies to the two most recent unfinished values.

### Optimal approach
1. Push numbers.
2. Pop right then left for an operator.
3. Push the result.
4. Return the last value.

### Dry run
```text
tokens = [2,1,+,3,*]
token  stack
2      2
1      2 1
+      3
3      3 3
*      9
```

### Why it works
Every stack entry is the value of a completed subexpression. An operator joins the newest two, as postfix defines.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> bowls = new ArrayDeque<>();
        for (String t : tokens) {
            if (t.length() == 1 && "+-*/".contains(t)) {
                int right = bowls.pop();
                int left = bowls.pop();
                bowls.push(switch (t) {
                    case "+" -> left + right;
                    case "-" -> left - right;
                    case "*" -> left * right;
                    default -> left / right;
                });
            } else {
                bowls.push(Integer.parseInt(t));
            }
        }
        return bowls.pop();
    }
}
```

### Code walkthrough
- `t.length() == 1`: So that -3 is a number.
- `int right = bowls.pop()`: The top is the right operand.
- `left / right`: Java truncates toward zero.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Swapping operands.** Subtraction and division fail.
2. **Testing the first character.** Negatives look like operators.
3. **Using floor division.** Negatives round wrongly.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `["7"]` | `7` |
| Order | `["5","2","-"]` | `3` |
| Truncation | `["-7","2","/"]` | `-3` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **224. Basic Calculator**: Infix brackets save context.
- [[069 — 682. Baseball Game]]: Operands stay. The key idea there: All operations refer to the most recent valid scores, which is a stack by definition.

### In a real interview
Say this: *"Postfix needs only a value stack. The first pop is the right operand."*

## 5. Recall
Q: 150. Evaluate Reverse Polish Notation: which pattern, and what is the insight?
A: Operand stack. An operator always applies to the two most recent unfinished values.

Q: 150. Evaluate Reverse Polish Notation: what is the analogy, and what does it stand for?
A: A cook with prepared bowls: bowls → the operand stack; instruction → an operator.

Q: 150. Evaluate Reverse Polish Notation: Which pop is the right operand?
A: The first.

Q: 150. Evaluate Reverse Polish Notation: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 074 — 2390. Removing Stars From a String | Medium · Undo stack | SHOULD
# 074 — 2390. Removing Stars From a String

**Problem 74 – Topic: Stack | Difficulty: Medium | Pattern: Undo stack**

[Open on LeetCode](https://leetcode.com/problems/removing-stars-from-a-string/)

## 1. Problem Statement
Each star in `s` removes itself and the closest non-star to its left. Return the final string.

### Examples
```text
Input:  s = "leet**cod*e"
Output: "lecoe"

Input:  s = "erase*****"
Output: ""
```

### Constraints
```text
1 <= s.length <= 10^5
s consists of lowercase English letters and stars *.
The operation above can be performed on s.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Backspace
Letters are typed, and a star is the backspace key. The screen is already a stack.

| In the story | In the solution |
|---|---|
| screen | a StringBuilder |
| backspace | shorten by one |

### Pattern signal
**Undo stack.** A star always removes the newest surviving letter.

### Thought process
1. **First idea:** Delete inside the string, in O(n²).
2. **The insight:** A star always removes the newest surviving letter.

### Optimal approach
1. Append letters.
2. On a star drop the last.
3. Return the buffer.

### Dry run
```text
s = "ab*c*d"
char  screen
a     a
b     ab
*     a
c     ac
*     a
d     ad
```

### Why it works
The screen always equals the processed prefix with all its stars applied, so its last letter is the closest survivor.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String removeStars(String s) {
        StringBuilder screen = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '*') screen.setLength(screen.length() - 1);
            else screen.append(c);
        }
        return screen.toString();
    }
}
```

### Code walkthrough
- `setLength`: Backspace in O(1).
- `screen.append(c)`: Type.
- `screen.toString()`: Already in order.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Deleting the previous original character.** It may be gone.
2. **`substring` each time.** It copies.
3. **Reversing at the end.** The buffer isn't reversed.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| All gone | `"a*"` | `""` |
| No star | `"ab"` | `"ab"` |
| Chain | `"ab**"` | `""` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **844. Backspace String Compare**: O(1) space from the back.
- [[084 — 1209. Remove All Adjacent Duplicates in String II]]: Deletion by count. The key idea there: A deletion can only join the next letters to the newest surviving run.

### In a real interview
Say this: *"Closest surviving to the left means a stack. A builder serves as one."*

## 5. Recall
Q: 2390. Removing Stars From a String: which pattern, and what is the insight?
A: Undo stack. A star always removes the newest surviving letter.

Q: 2390. Removing Stars From a String: what is the analogy, and what does it stand for?
A: Backspace: screen → a StringBuilder; backspace → shorten by one.

Q: 2390. Removing Stars From a String: What does a star remove?
A: The most recent surviving letter.

Q: 2390. Removing Stars From a String: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 075 — 946. Validate Stack Sequences | Medium · Simulation stack | SHOULD
# 075 — 946. Validate Stack Sequences

**Problem 75 – Topic: Stack | Difficulty: Medium | Pattern: Simulation stack**

[Open on LeetCode](https://leetcode.com/problems/validate-stack-sequences/)

## 1. Problem Statement
Return true if pushing `pushed` in order, with pops whenever you like, can produce the order `popped`. Values are unique.

### Examples
```text
Input:  pushed = [1,2,3,4,5], popped = [4,5,3,2,1]
Output: true

Input:  pushed = [1,2,3,4,5], popped = [4,3,5,1,2]
Output: false
```

### Constraints
```text
1 <= pushed.length <= 1000
0 <= pushed[i] <= 1000
All the elements of pushed are unique.
popped.length == pushed.length
popped is a permutation of pushed.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A dishwasher and a waiter
Plates arrive in fixed order. The waiter needs them in their own order and takes the top whenever it is the next one needed. If they get stuck, the order is impossible.

| In the story | In the solution |
|---|---|
| the waiter's next plate | pointer `next` |
| pile | the stack |

### Pattern signal
**Simulation stack.** When the top is the needed value it must be popped now, because anything pushed would bury it.

### Thought process
1. **First idea:** Try all interleavings, which is exponential.
2. **The insight:** When the top is the needed value it must be popped now, because anything pushed would bury it.

### Optimal approach
1. Push each value.
2. Pop while the top is the next needed.
3. Succeed if all were popped.

### Dry run
```text
pushed = [1,2,3,4,5], popped = [4,5,3,2,1]
push  popped now  stack
1     -           1
2     -           1 2
3     -           1 2 3
4     4           1 2 3
5     5,3,2,1     empty, true
```

### Why it works
Pops are forced when possible and pushes are forced otherwise, so the simulation is the only candidate.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Deque<Integer> pile = new ArrayDeque<>();
        int next = 0;
        for (int x : pushed) {
            pile.push(x);
            while (!pile.isEmpty() && pile.peek() == popped[next]) {
                pile.pop();
                next++;
            }
        }
        return pile.isEmpty();
    }
}
```

### Code walkthrough
- `pile.push(x)`: The plate arrives.
- `while`: Several can be ready.
- `return pile.isEmpty()`: The waiter got all of them.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Popping once.** Use while.
2. **Comparing the arrays.** Many orders are valid.
3. **Peeking empty.** Check first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Same | `[1,2], [1,2]` | `true` |
| Reverse | `[1,2], [2,1]` | `true` |
| Buried | `[1,2,3], [3,1,2]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[068 — 20. Valid Parentheses]]: Push and pop are brackets. The key idea there: Only the newest unclosed opener limits the next closer.
- [[074 — 2390. Removing Stars From a String]]: Pops are given. The key idea there: A star always removes the newest surviving letter.

### In a real interview
Say this: *"With unique values each pop is forced when possible. I simulate greedily."*

## 5. Recall
Q: 946. Validate Stack Sequences: which pattern, and what is the insight?
A: Simulation stack. When the top is the needed value it must be popped now, because anything pushed would bury it.

Q: 946. Validate Stack Sequences: what is the analogy, and what does it stand for?
A: A dishwasher and a waiter: the waiter's next plate → pointer `next`; pile → the stack.

Q: 946. Validate Stack Sequences: Why pop immediately on a match?
A: Another push would bury the needed unique value.

Q: 946. Validate Stack Sequences: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 076 — 22. Generate Parentheses | Medium · Open and close choices | MUST
# 076 — 22. Generate Parentheses

**Problem 76 – Topic: Stack | Difficulty: Medium | Pattern: Open and close choices**

[Open on LeetCode](https://leetcode.com/problems/generate-parentheses/)

## 1. Problem Statement
Return every valid arrangement of `n` pairs of parentheses.

### Examples
```text
Input:  n = 3
Output: ["((()))","(()())","(())()","()(())","()()()"]

Input:  n = 1
Output: ["()"]
```

### Constraints
```text
1 <= n <= 8
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Walking up and down n hills
An opener is a step up, a closer a step down. You have n ups, may never go underground, and must end at ground level. List every legal walk, stepping back to try the other choice at each fork.

| In the story | In the solution |
|---|---|
| underground | more closers than openers |
| stepping back | undo |

### Pattern signal
**Open and close choices.** Validity of a prefix depends on two counts, so invalid branches are never started.

### Thought process
1. **First idea:** Generate all 2^(2n) strings and filter.
2. **The insight:** Validity of a prefix depends on two counts, so invalid branches are never started.

### Optimal approach
1. Save at length 2n.
2. Open while fewer than n are open.
3. Close while closed is below open.
4. Undo each choice.

### Dry run
```text
n = 2
prefix  open,closed  choices
""      0,0          (
(       1,0          ( or )
((      2,0          ) then ) gives (())
()      1,1          ( then ) gives ()()
```

### Why it works
Both rules keep every prefix legal, and every legal string follows exactly one path of these choices.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        walk(n, 0, 0, new StringBuilder(), result);
        return result;
    }

    private void walk(int n, int open, int closed, StringBuilder path, List<String> result) {
        if (path.length() == 2 * n) {
            result.add(path.toString());
            return;
        }
        if (open < n) {
            walk(n, open + 1, closed, path.append('('), result);
            path.setLength(path.length() - 1);
        }
        if (closed < open) {
            walk(n, open, closed + 1, path.append(')'), result);
            path.setLength(path.length() - 1);
        }
    }
}
```

### Code walkthrough
- `open < n`: An up-step remains.
- `closed < open`: Not underground.
- `path.setLength`: Step back to the fork.

### Complexity
- **Time:** O(n · Catalan(n))
- **Space:** O(n) besides the output

### Common mistakes
1. **`closed < n`.** It allows ")(".
2. **No undo.** Branches contaminate each other.
3. **Filtering everything.** It wastes exponential work.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `1` | `["()"]` |
| Two | `2` | `["(())","()()"]` |
| Three | `3` | `five strings` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[195 — 17. Letter Combinations of a Phone Number]]: Forks without a rule. The key idea there: Recursion is simply a variable number of nested loops.
- [[068 — 20. Valid Parentheses]]: Checks one walk. The key idea there: Only the newest unclosed opener limits the next closer.

### In a real interview
Say this: *"I backtrack with two counts, so only valid prefixes exist. The output is Catalan sized."*

## 5. Recall
Q: 22. Generate Parentheses: which pattern, and what is the insight?
A: Open and close choices. Validity of a prefix depends on two counts, so invalid branches are never started.

Q: 22. Generate Parentheses: what is the analogy, and what does it stand for?
A: Walking up and down n hills: underground → more closers than openers; stepping back → undo.

Q: 22. Generate Parentheses: When may a closer be added?
A: Only when closed is less than open.

Q: 22. Generate Parentheses: what are the time and space costs?
A: Time O(n · Catalan(n)). Space O(n) besides the output.

@@ 3 | TOPIC | 077 — 735. Asteroid Collision | Medium · Collision stack | SHOULD
# 077 — 735. Asteroid Collision

**Problem 77 – Topic: Stack | Difficulty: Medium | Pattern: Collision stack**

[Open on LeetCode](https://leetcode.com/problems/asteroid-collision/)

## 1. Problem Statement
Each asteroid's size is the absolute value and its sign is the direction, all at equal speed. When two meet the smaller explodes, or both if equal. Return the survivors.

### Examples
```text
Input:  asteroids = [5,10,-5]
Output: [5,10]

Input:  asteroids = [8,-8]
Output: []

Input:  asteroids = [10,2,-5]
Output: [10]

Input:  asteroids = [3,5,-6,2,-1,4]​​​​​​​
Output: [-6,2,4]
```

### Constraints
```text
2 <= asteroids.length <= 10^4
-1000 <= asteroids[i] <= 1000
asteroids[i] != 0
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A one-lane corridor
People walking left meet only those walking right who entered earlier, nearest first. They keep knocking over smaller ones until stopped or out in the clear.

| In the story | In the solution |
|---|---|
| nearest opponent | the stack's top |
| in the clear | empty, or a left mover on top |

### Pattern signal
**Collision stack.** Only right-then-left collides, and always with the newest surviving right mover.

### Thought process
1. **First idea:** Rescan for collisions, in O(n²).
2. **The insight:** Only right-then-left collides, and always with the newest surviving right mover.

### Optimal approach
1. For a left mover pop smaller right movers.
2. On equality pop and destroy it.
3. Push if it survives.
4. Right movers are pushed.

### Dry run
```text
asteroids = [10,2,-5]
x   action                   stack
10  push                     10
2   push                     10 2
-5  destroys 2, loses to 10  10
```

### Why it works
The stack is the stable result of the prefix. A new left mover can disturb only its trailing right movers, in that order.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        int[] stack = new int[asteroids.length];
        int size = 0;
        for (int x : asteroids) {
            boolean alive = true;
            while (alive && x < 0 && size > 0 && stack[size - 1] > 0) {
                int top = stack[size - 1];
                if (top <= -x) size--;       // top explodes
                if (top >= -x) alive = false; // x explodes
            }
            if (alive) stack[size++] = x;
        }
        return Arrays.copyOf(stack, size);
    }
}
```

### Code walkthrough
- `x < 0 && size > 0 && stack[size - 1] > 0`: The only meeting case.
- `top <= -x`: Opponent falls.
- `top >= -x`: Newcomer falls. Equality triggers both.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Colliding left then right.** They move apart.
2. **One collision.** Chains happen.
3. **Equal sizes.** Both vanish.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Equal | `[8,-8]` | `[]` |
| Apart | `[-2,2]` | `[-2,2]` |
| Chain | `[1,2,-3]` | `[-3]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[080 — 853. Car Fleet]]: Meeting means merging. The key idea there: Only arrival times matter, and the latest one ahead is the blocking one.
- [[083 — 402. Remove K Digits]]: A newcomer removes weaker ones. The key idea there: For equal lengths the first difference decides, so remove a digit larger than its successor as early as possible.

### In a real interview
Say this: *"A stack holds the stable prefix. A left mover resolves collisions from the top."*

## 5. Recall
Q: 735. Asteroid Collision: which pattern, and what is the insight?
A: Collision stack. Only right-then-left collides, and always with the newest surviving right mover.

Q: 735. Asteroid Collision: what is the analogy, and what does it stand for?
A: A one-lane corridor: nearest opponent → the stack's top; in the clear → empty, or a left mover on top.

Q: 735. Asteroid Collision: Which neighbors collide?
A: A right mover followed by a left mover.

Q: 735. Asteroid Collision: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 078 — 739. Daily Temperatures | Medium · Monotonic stack | MUST
# 078 — 739. Daily Temperatures

**Problem 78 – Topic: Stack | Difficulty: Medium | Pattern: Monotonic stack**

[Open on LeetCode](https://leetcode.com/problems/daily-temperatures/)

## 1. Problem Statement
For each day return how many days until a strictly warmer one, or 0.

### Examples
```text
Input:  temperatures = [73,74,75,71,69,72,76,73]
Output: [1,1,4,2,1,1,0,0]

Input:  temperatures = [30,40,50,60]
Output: [1,1,1,0]

Input:  temperatures = [30,60,90]
Output: [1,1,0]
```

### Constraints
```text
1 <= temperatures.length <= 10^5
30 <= temperatures[i] <= 100
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: People waiting on benches for sunshine
Each day sits down holding its date. A warmer day walks by and releases everyone cooler from the end of the benches, telling each how long they waited.

| In the story | In the solution |
|---|---|
| seated days | a stack of indices |
| waiting time | difference of indices |

### Pattern signal
**Monotonic stack.** Unresolved temperatures never increase along the stack, so a warm day resolves a suffix.

### Thought process
1. **First idea:** Scan forward from every day, in O(n²).
2. **The insight:** Unresolved temperatures never increase along the stack, so a warm day resolves a suffix.

### Optimal approach
1. Visit days.
2. Pop while today is warmer.
3. Store the distance.
4. Push today.

### Dry run
```text
temperatures = [73,74,71,69,72]
day  temp  resolved          waiting
0    73    -                 0
1    74    day 0: 1          1
2    71    -                 1 2
3    69    -                 1 2 3
4    72    day 3: 1, day 2: 2  1 4
```

### Why it works
A day is popped at the first warmer day. Those never popped have none and keep 0.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int[] days = new int[temperatures.length];
        Deque<Integer> waiting = new ArrayDeque<>();
        for (int today = 0; today < temperatures.length; today++) {
            while (!waiting.isEmpty() && temperatures[waiting.peek()] < temperatures[today]) {
                int earlier = waiting.pop();
                days[earlier] = today - earlier;
            }
            waiting.push(today);
        }
        return days;
    }
}
```

### Code walkthrough
- `waiting.push(today)`: Store the date.
- `< temperatures[today]`: Strictly warmer.
- `today - earlier`: How long they waited.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Storing temperatures.** Distance is lost.
2. **Using `<=`.** Equal isn't warmer.
3. **Resetting leftovers to -1.** They need 0.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Equal | `[50,50]` | `[0,0]` |
| Rising | `[30,40]` | `[1,0]` |
| One | `[30]` | `[0]` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[067 — 496. Next Greater Element I]]: Values, not distances. The key idea there: Values without an answer are always decreasing, so each arrival only checks the top.
- [[079 — 901. Online Stock Span]]: Look backwards. The key idea there: If yesterday absorbed a block, anything at least as large takes the whole block.

### In a real interview
Say this: *"It is next greater with distance. A decreasing index stack is linear."*

## 5. Recall
Q: 739. Daily Temperatures: which pattern, and what is the insight?
A: Monotonic stack. Unresolved temperatures never increase along the stack, so a warm day resolves a suffix.

Q: 739. Daily Temperatures: what is the analogy, and what does it stand for?
A: People waiting on benches for sunshine: seated days → a stack of indices; waiting time → difference of indices.

Q: 739. Daily Temperatures: Why store indices?
A: The answer is a distance.

Q: 739. Daily Temperatures: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | Practice — Review 13 | Re-solve without looking | MUST
# Review 13

Solve on a blank page. No notes, hints or solutions.

## 739. Daily Temperatures
For each day count days until a warmer one.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/daily-temperatures/)

## 946. Validate Stack Sequences
Decide whether pop order is possible for a push order.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/validate-stack-sequences/)

## 1658. Minimum Operations to Reduce X to Zero
Remove fewest end values so they sum to x.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/minimum-operations-to-reduce-x-to-zero/)

## 88. Merge Sorted Array
Merge two sorted arrays into the first one's spare space.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/merge-sorted-array/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 739 | | | | |
| 946 | | | | |
| 1658 | | | | |
| 88 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[078 — 739. Daily Temperatures]]
- [[075 — 946. Validate Stack Sequences]]
- [[064 — 1658. Minimum Operations to Reduce X to Zero]]
- [[040 — 88. Merge Sorted Array]]

@@ 3 | TOPIC | 079 — 901. Online Stock Span | Medium · Stack of spans | SHOULD
# 079 — 901. Online Stock Span

**Problem 79 – Topic: Stack | Difficulty: Medium | Pattern: Stack of spans**

[Open on LeetCode](https://leetcode.com/problems/online-stock-span/)

## 1. Problem Statement
Design `StockSpanner`. `next(price)` returns how many consecutive days, ending today, had a price at most today's.

### Examples
```text
Input:  ["StockSpanner", "next", "next", "next", "next", "next", "next", "next"]
        [[], [100], [80], [60], [70], [60], [75], [85]]
Output: [null, 1, 1, 1, 2, 1, 4, 6]
```

### Constraints
```text
1 <= price <= 10^5
At most 10^4 calls will be made to next.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Companies buying companies
A new company absorbs every smaller or equal neighbor to its left, along with all the days those had absorbed. It stops at the first bigger one.

| In the story | In the solution |
|---|---|
| absorbed days | the saved span |
| bigger neighbor | the top that stays |

### Pattern signal
**Stack of spans.** If yesterday absorbed a block, anything at least as large takes the whole block.

### Thought process
1. **First idea:** Walk back through the history, in O(n) per call.
2. **The insight:** If yesterday absorbed a block, anything at least as large takes the whole block.

### Optimal approach
1. Start span at one.
2. Pop prices not above today's and add their spans.
3. Push price and span.

### Dry run
```text
price  absorbs     span  stack prices
100    -           1     100
80     -           1     100 80
60     -           1     100 80 60
70     60          2     100 80 70
60     -           1     100 80 70 60
75     60,70       4     100 80 75
```

### Why it works
Every entry summarizes a contiguous block no larger than itself. The first larger top is the boundary.

## 3. Java Solution with Explanation
### Code
```java
class StockSpanner {
    private final Deque<int[]> blocks = new ArrayDeque<>(); // price, span

    public int next(int price) {
        int span = 1;
        while (!blocks.isEmpty() && blocks.peek()[0] <= price) span += blocks.pop()[1];
        blocks.push(new int[] {price, span});
        return span;
    }
}
```

### Code walkthrough
- `int span = 1`: Today itself.
- `blocks.pop()[1]`: Take the neighbor's whole holding.
- `<= price`: Equal is included.

### Complexity
- **Time:** O(1) amortized
- **Space:** O(n)

### Common mistakes
1. **Stopping on equal.** At most includes it.
2. **Adding one per pop.** Add its span.
3. **Static state.** Instances would mix.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| First | `next(5)` | `1` |
| Equal | `next(5), next(5)` | `1, 2` |
| Falling | `next(5), next(4)` | `1, 1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[078 — 739. Daily Temperatures]]: Look forward. The key idea there: Unresolved temperatures never increase along the stack, so a warm day resolves a suffix.
- [[087 — 84. Largest Rectangle in Histogram]]: Widths absorbed likewise. The key idea there: An increasing stack knows both first smaller neighbors at the moment of popping.

### In a real interview
Say this: *"I store compressed dominated blocks on a decreasing stack. Each is absorbed once."*

## 5. Recall
Q: 901. Online Stock Span: which pattern, and what is the insight?
A: Stack of spans. If yesterday absorbed a block, anything at least as large takes the whole block.

Q: 901. Online Stock Span: what is the analogy, and what does it stand for?
A: Companies buying companies: absorbed days → the saved span; bigger neighbor → the top that stays.

Q: 901. Online Stock Span: What does a span on the stack mean?
A: The consecutive days covered by that price.

Q: 901. Online Stock Span: what are the time and space costs?
A: Time O(1) amortized. Space O(n).

@@ 3 | TOPIC | 080 — 853. Car Fleet | Medium · Arrival-time stack | MUST
# 080 — 853. Car Fleet

**Problem 80 – Topic: Stack | Difficulty: Medium | Pattern: Arrival-time stack**

[Open on LeetCode](https://leetcode.com/problems/car-fleet/)

## 1. Problem Statement
Cars at different positions drive to `target` at their speeds. A car cannot overtake: on catching up it joins that fleet's speed. Return the fleets arriving. Meeting at the target counts.

### Examples
```text
Input:  target = 12, position = [10,8,0,5,3], speed = [2,4,1,1,3]
Output: 3

Input:  target = 10, position = [3], speed = [3]
Output: 1

Input:  target = 100, position = [0,2,4], speed = [4,2,1]
Output: 1
```

### Constraints
```text
n == position.length == speed.length
1 <= n <= 10^5
0 < target <= 10^6
0 <= position[i] < target
All the values of position are unique.
0 < speed[i] <= 10^6
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Stuck behind a tractor
Ask each car, from the front, "when would you arrive on an empty road?" Anyone who would arrive no later than the vehicle ahead gets stuck behind it. Anyone slower becomes the next tractor.

| In the story | In the solution |
|---|---|
| empty-road arrival | distance / speed |
| current tractor | `slowest` |

### Pattern signal
**Arrival-time stack.** Only arrival times matter, and the latest one ahead is the blocking one.

### Thought process
1. **First idea:** Simulate the meetings, which is complicated and quadratic.
2. **The insight:** Only arrival times matter, and the latest one ahead is the blocking one.

### Optimal approach
1. Sort from nearest the target.
2. Compute arrival time.
3. Start a fleet only if strictly later than the one ahead.

### Dry run
```text
target = 12
position  speed  time  latest ahead  fleets
10        2      1     0             1
8         4      1     1             1
5         1      7     1             2
3         3      3     7             2
0         1      12    7             3
```

### Why it works
A car with a smaller or equal time must meet the fleet ahead by the target. A larger one never can.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int carFleet(int target, int[] position, int[] speed) {
        Integer[] order = new Integer[position.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> position[b] - position[a]);
        int fleets = 0;
        double slowest = 0; // the top of the stack of fleets
        for (int i : order) {
            double time = (double) (target - position[i]) / speed[i];
            if (time > slowest) {
                fleets++;
                slowest = time;
            }
        }
        return fleets;
    }
}
```

### Code walkthrough
- `position[b] - position[a]`: From the front.
- `(double)`: Arrival times are fractions.
- `time > slowest`: A new tractor. The stack shrinks to this one value.

### Complexity
- **Time:** O(n log n)
- **Space:** O(n) for the order

### Common mistakes
1. **Sorting by speed.** Position decides who blocks.
2. **Integer division.** Different times collapse.
3. **Splitting on equality.** They meet at the target.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `10, [3], [3]` | `1` |
| Meet at target | `10, [0,5], [2,1]` | `1` |
| Never | `10, [0,5], [1,1]` | `2` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[077 — 735. Asteroid Collision]]: Meeting destroys. The key idea there: Only right-then-left collides, and always with the newest surviving right mover.
- [[079 — 901. Online Stock Span]]: Absorb what is dominated. The key idea there: If yesterday absorbed a block, anything at least as large takes the whole block.

### In a real interview
Say this: *"I turn motion into arrival times ordered by position. Each new maximum is a fleet."*

## 5. Recall
Q: 853. Car Fleet: which pattern, and what is the insight?
A: Arrival-time stack. Only arrival times matter, and the latest one ahead is the blocking one.

Q: 853. Car Fleet: what is the analogy, and what does it stand for?
A: Stuck behind a tractor: empty-road arrival → distance / speed; current tractor → `slowest`.

Q: 853. Car Fleet: When does a new fleet start?
A: When a car's solo time is strictly greater than that of the fleet ahead.

Q: 853. Car Fleet: what are the time and space costs?
A: Time O(n log n). Space O(n) for the order.

@@ 3 | TOPIC | 081 — 71. Simplify Path | Medium · Path stack | SHOULD
# 081 — 71. Simplify Path

**Problem 81 – Topic: Stack | Difficulty: Medium | Pattern: Path stack**

[Open on LeetCode](https://leetcode.com/problems/simplify-path/)

## 1. Problem Statement
Convert an absolute Unix path to canonical form: "." stays, ".." goes up, repeated slashes count as one, anything else is a name. No trailing slash except for the root.

### Examples
```text
Input:  path = "/home/"
Output: "/home"

Input:  path = "/home//foo/"
Output: "/home/foo"

Input:  path = "/home/user/Documents/../Pictures"
Output: "/home/user/Pictures"

Input:  path = "/../"
Output: "/"

Input:  path = "/.../a/../b/c/../d/./"
Output: "/.../b/d"
```

### Constraints
```text
1 <= path.length <= 3000
path consists of English letters, digits, period '.', slash '/' or '_'.
path is a valid absolute Unix path.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Walking through rooms with a ball of string
Each door you pass is added to your trail. Two dots means walk back through the last door. At the front entrance you can go back no further.

| In the story | In the solution |
|---|---|
| trail | a deque of names |
| front entrance | the root |

### Pattern signal
**Path stack.** ".." cancels the newest surviving folder.

### Thought process
1. **First idea:** Rewrite the text repeatedly, which is fragile and quadratic.
2. **The insight:** ".." cancels the newest surviving folder.

### Optimal approach
1. Split on slash.
2. Ignore empty and dot.
3. Pop on two dots if possible.
4. Push names.
5. Join from the bottom.

### Dry run
```text
path = "/a/./b/../c"
token  trail
a      a
.      a
b      a b
..     a
c      a c, giving /a/c
```

### Why it works
The trail is always the canonical location of the prefix processed.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String simplifyPath(String path) {
        Deque<String> trail = new ArrayDeque<>();
        for (String part : path.split("/")) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) trail.pollLast();
            else trail.addLast(part);
        }
        return "/" + String.join("/", trail);
    }
}
```

### Code walkthrough
- `part.isEmpty()`: Double slashes create blanks.
- `pollLast`: Safe at the root, where it returns null.
- `String.join`: Walks front to back.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Treating "..." as special.** It is a name.
2. **Failing at the root.** Stay there.
3. **push plus join.** The order comes out reversed.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Root | `"/../"` | `"/"` |
| Slashes | `"/a//b/"` | `"/a/b"` |
| Dots | `"/..."` | `"/..."` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[120 — 1472. Design Browser History]]: Forward is remembered. The key idea there: Moving is index arithmetic and erasing the future is moving an end marker.
- [[074 — 2390. Removing Stars From a String]]: Undo on characters. The key idea there: A star always removes the newest surviving letter.

### In a real interview
Say this: *"Path parts form a stack and two dots pops it. Splitting removes slash handling."*

## 5. Recall
Q: 71. Simplify Path: which pattern, and what is the insight?
A: Path stack. ".." cancels the newest surviving folder.

Q: 71. Simplify Path: what is the analogy, and what does it stand for?
A: Walking through rooms with a ball of string: trail → a deque of names; front entrance → the root.

Q: 71. Simplify Path: What does two dots do at the root?
A: Nothing.

Q: 71. Simplify Path: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 082 — 394. Decode String | Medium · Nested decoding | SHOULD
# 082 — 394. Decode String

**Problem 82 – Topic: Stack | Difficulty: Medium | Pattern: Nested decoding**

[Open on LeetCode](https://leetcode.com/problems/decode-string/)

## 1. Problem Statement
Decode strings where `k[text]` means text repeated k times, possibly nested.

### Examples
```text
Input:  s = "3[a]2[bc]"
Output: "aaabcbc"

Input:  s = "3[a2[c]]"
Output: "accaccacc"

Input:  s = "2[abc]3[cd]ef"
Output: "abcabccdcdcdef"
```

### Constraints
```text
1 <= s.length <= 30
s consists of lowercase English letters, digits, and square brackets '[]'.
s is guaranteed to be a valid input.
All the integers in s are in the range [1, 300].
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Pausing a recipe for a sub-recipe
At "make 3 of:" you put a bookmark holding what you made so far and the 3, then start fresh. When the sub-recipe ends, take the bookmark and attach three copies.

| In the story | In the solution |
|---|---|
| bookmark | saved text and count |
| fresh start | an empty builder |

### Pattern signal
**Nested decoding.** At a closer only the newest bookmark matters.

### Thought process
1. **First idea:** Expand the innermost bracket repeatedly, rescanning each time.
2. **The insight:** At a closer only the newest bookmark matters.

### Optimal approach
1. Build numbers digit by digit.
2. On an opener save text and count.
3. On a closer pop and append the repeats.
4. Append letters.

### Dry run
```text
s = "3[a2[c]]"
read  saved               current
3[    ("",3)              ""
a     ("",3)              a
2[    ("",3) ("a",2)      ""
c     ("",3) ("a",2)      c
]     ("",3)              acc
]     -                   accaccacc
```

### Why it works
Current is the decoded part of the innermost open bracket and each bookmark is exactly what surrounds it.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String decodeString(String s) {
        Deque<StringBuilder> texts = new ArrayDeque<>();
        Deque<Integer> counts = new ArrayDeque<>();
        StringBuilder current = new StringBuilder();
        int number = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            } else if (c == '[') {
                texts.push(current);
                counts.push(number);
                current = new StringBuilder();
                number = 0;
            } else if (c == ']') {
                StringBuilder outer = texts.pop();
                outer.append(current.toString().repeat(counts.pop()));
                current = outer;
            } else {
                current.append(c);
            }
        }
        return current.toString();
    }
}
```

### Code walkthrough
- `number * 10`: Counts have several digits.
- `texts.push(current)`: Place the bookmark.
- `repeat(counts.pop())`: Attach the copies.

### Complexity
- **Time:** O(d · output) at worst for depth d
- **Space:** O(input + output)

### Common mistakes
1. **One-digit counts.** 12[a] fails.
2. **Not resetting the number.** Nested counts merge.
3. **Prepending instead of appending.** The outer text comes first.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Plain | `"ab"` | `"ab"` |
| Two digits | `"10[a]"` | `ten a's` |
| Nested | `"2[2[b]]"` | `"bbbb"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **224. Basic Calculator**: Bookmarks hold a sum and sign.
- [[068 — 20. Valid Parentheses]]: Only the nesting. The key idea there: Only the newest unclosed opener limits the next closer.

### In a real interview
Say this: *"Each bracket saves context on a stack and its closer restores it."*

## 5. Recall
Q: 394. Decode String: which pattern, and what is the insight?
A: Nested decoding. At a closer only the newest bookmark matters.

Q: 394. Decode String: what is the analogy, and what does it stand for?
A: Pausing a recipe for a sub-recipe: bookmark → saved text and count; fresh start → an empty builder.

Q: 394. Decode String: What is saved at an opening bracket?
A: The text so far and the repeat count.

Q: 394. Decode String: what are the time and space costs?
A: Time O(d · output) at worst for depth d. Space O(input + output).

@@ 3 | TOPIC | 083 — 402. Remove K Digits | Medium · Increasing stack | SHOULD
# 083 — 402. Remove K Digits

**Problem 83 – Topic: Stack | Difficulty: Medium | Pattern: Increasing stack**

[Open on LeetCode](https://leetcode.com/problems/remove-k-digits/)

## 1. Problem Statement
Delete exactly `k` digits from `num` to make the smallest possible number, without leading zeros. An empty result is "0".

### Examples
```text
Input:  num = "1432219", k = 3
Output: "1219"

Input:  num = "10200", k = 1
Output: "200"

Input:  num = "10", k = 2
Output: "0"
```

### Constraints
```text
1 <= k <= num.length <= 10^5
num consists of only digits.
num does not have any leading zeros except for the zero itself.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A bouncer who likes small digits in front
Places at the front are worth most. A small newcomer may throw out larger digits just ahead, as long as throw-outs remain.

| In the story | In the solution |
|---|---|
| throw-outs | k |
| queue kept | an increasing stack |

### Pattern signal
**Increasing stack.** For equal lengths the first difference decides, so remove a digit larger than its successor as early as possible.

### Thought process
1. **First idea:** Try all deletions, C(n,k) of them.
2. **The insight:** For equal lengths the first difference decides, so remove a digit larger than its successor as early as possible.

### Optimal approach
1. Pop larger tops while k remains.
2. Push the digit.
3. Cut leftover k from the end.
4. Skip leading zeros.

### Dry run
```text
num = 1432219, k = 3
digit  popped  stack  k
1      -       1      3
4      -       14     3
3      4       13     2
2      3       12     1
2      -       122    1
1      2       121    0
9      -       1219   0
```

### Why it works
Each pop puts a smaller digit at the most significant changeable place. If none apply the digits increase, so the end is best to cut.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String removeKdigits(String num, int k) {
        StringBuilder kept = new StringBuilder();
        for (char c : num.toCharArray()) {
            while (k > 0 && kept.length() > 0 && kept.charAt(kept.length() - 1) > c) {
                kept.setLength(kept.length() - 1);
                k--;
            }
            kept.append(c);
        }
        kept.setLength(kept.length() - k);
        int start = 0;
        while (start < kept.length() && kept.charAt(start) == '0') start++;
        return start == kept.length() ? "0" : kept.substring(start);
    }
}
```

### Code walkthrough
- `> c`: A larger digit blocks a better front.
- `kept.length() - k`: Spend leftovers on the tail.
- `start++`: Zeros vanish from the front.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Removing the largest digits.** Position matters more.
2. **Forgetting leftovers.** 1234 still needs cuts.
3. **Parsing the number.** It is too long.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| All | `"10", 2` | `"0"` |
| Zeros | `"10200", 1` | `"200"` |
| Rising | `"123", 1` | `"12"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **316. Remove Duplicate Letters**: Pop only if the letter returns.
- **1673. Most Competitive Subsequence**: The same for arrays.

### In a real interview
Say this: *"A smaller later digit should displace larger earlier ones. An increasing stack does it."*

## 5. Recall
Q: 402. Remove K Digits: which pattern, and what is the insight?
A: Increasing stack. For equal lengths the first difference decides, so remove a digit larger than its successor as early as possible.

Q: 402. Remove K Digits: what is the analogy, and what does it stand for?
A: A bouncer who likes small digits in front: throw-outs → k; queue kept → an increasing stack.

Q: 402. Remove K Digits: Where are leftover deletions spent?
A: At the end.

Q: 402. Remove K Digits: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | 084 — 1209. Remove All Adjacent Duplicates in String II | Medium · Run-length stack | SHOULD
# 084 — 1209. Remove All Adjacent Duplicates in String II

**Problem 84 – Topic: Stack | Difficulty: Medium | Pattern: Run-length stack**

[Open on LeetCode](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string-ii/)

## 1. Problem Statement
Repeatedly delete `k` equal adjacent letters until none remain. Return the unique result.

### Examples
```text
Input:  s = "abcd", k = 2
Output: "abcd"

Input:  s = "deeedbbcccbdaa", k = 3
Output: "aa"

Input:  s = "pbbcggttciiippooaais", k = 2
Output: "ps"
```

### Constraints
```text
1 <= s.length <= 10^5
2 <= k <= 10^4
s only contains lowercase English letters.
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A tile game where k in a row vanish
Keep piles with a label and height. A tile on the same label raises the top pile. At height k it vanishes, and the pile below can keep growing.

| In the story | In the solution |
|---|---|
| pile height | the run count |
| vanish | pop |

### Pattern signal
**Run-length stack.** A deletion can only join the next letters to the newest surviving run.

### Thought process
1. **First idea:** Rescan after each deletion, in O(n²).
2. **The insight:** A deletion can only join the next letters to the newest surviving run.

### Optimal approach
1. Increase a matching top or push a new run.
2. Pop at k.
3. Expand the runs.

### Dry run
```text
s = "abbbaa", k = 3
char  runs after
a     a1
b     a1 b1
b     a1 b2
b     a1
a     a2
a     empty
```

### Why it works
The runs are the fully reduced prefix, adjacent ones differing, so a new letter can change only the last.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public String removeDuplicates(String s, int k) {
        char[] letters = new char[s.length()];
        int[] heights = new int[s.length()];
        int size = 0;
        for (char c : s.toCharArray()) {
            if (size == 0 || letters[size - 1] != c) {
                letters[size] = c;
                heights[size++] = 0;
            }
            if (++heights[size - 1] == k) size--;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) result.append(String.valueOf(letters[i]).repeat(heights[i]));
        return result.toString();
    }
}
```

### Code walkthrough
- `letters[size - 1] != c`: A new pile.
- `== k`: It vanishes.
- `repeat(heights[i])`: Unpack survivors.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **One pass over original runs.** New runs form.
2. **Global counts.** Adjacency matters.
3. **Resetting an uncovered run.** Its height must survive.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| None | `"abc", 2` | `"abc"` |
| Chain | `"abba", 2` | `""` |
| Extra | `"aaaa", 3` | `"a"` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[074 — 2390. Removing Stars From a String]]: Simple undo. The key idea there: A star always removes the newest surviving letter.
- [[044 — 80. Remove Duplicates from Sorted Array II]]: Cap runs instead of deleting. The key idea there: Sorted means two kept copies are adjacent, so one look two back detects a third.

### In a real interview
Say this: *"I run-length encode on a stack. Deletion exposes the previous run with its count."*

## 5. Recall
Q: 1209. Remove All Adjacent Duplicates in String II: which pattern, and what is the insight?
A: Run-length stack. A deletion can only join the next letters to the newest surviving run.

Q: 1209. Remove All Adjacent Duplicates in String II: what is the analogy, and what does it stand for?
A: A tile game where k in a row vanish: pile height → the run count; vanish → pop.

Q: 1209. Remove All Adjacent Duplicates in String II: Why store counts?
A: An uncovered run must continue from its earlier length.

Q: 1209. Remove All Adjacent Duplicates in String II: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | Practice — Review 14 | Re-solve without looking | MUST
# Review 14

Solve on a blank page. No notes, hints or solutions.

## 1209. Remove All Adjacent Duplicates in String II
Repeatedly delete k equal adjacent letters.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string-ii/)

## 71. Simplify Path
Simplify an absolute Unix path.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/simplify-path/)

## 225. Implement Stack using Queues
Implement a stack using a queue.

Time limit: 15 minutes. [Problem on LeetCode](https://leetcode.com/problems/implement-stack-using-queues/)

## 15. 3Sum
List unique triplets that sum to zero.

Time limit: 25 minutes. [Problem on LeetCode](https://leetcode.com/problems/3sum/)

| Problem | Solved alone | Minutes | Bug | Redo on |
|---|---|---|---|---|
| 1209 | | | | |
| 71 | | | | |
| 225 | | | | |
| 15 | | | | |

Redo a failure the next day and again at the next review.

### Check only after solving

- [[084 — 1209. Remove All Adjacent Duplicates in String II]]
- [[081 — 71. Simplify Path]]
- [[070 — 225. Implement Stack using Queues]]
- [[046 — 15. 3Sum]]

@@ 3 | TOPIC | 085 — 456. 132 Pattern | Medium · Decreasing stack | SHOULD
# 085 — 456. 132 Pattern

**Problem 85 – Topic: Stack | Difficulty: Medium | Pattern: Decreasing stack**

[Open on LeetCode](https://leetcode.com/problems/132-pattern/)

## 1. Problem Statement
Return true if indices i &lt; j &lt; k exist with nums[i] &lt; nums[k] &lt; nums[j].

### Examples
```text
Input:  nums = [1,2,3,4]
Output: false

Input:  nums = [3,1,4,2]
Output: true

Input:  nums = [-1,3,2,0]
Output: true
```

### Constraints
```text
n == nums.length
1 <= n <= 2 * 10^5
-10^9 <= nums[i] <= 10^9
```

**Stop here and try it for 25 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: An auction watched backwards
Read from the right. Whenever a big bidder beats someone, the beaten one becomes a runner-up who provably had a bigger bidder before them. Keep the best runner-up. Anyone further left who is lower completes the pattern.

| In the story | In the solution |
|---|---|
| big bidder | the 3 |
| best runner-up | `two` |

### Pattern signal
**Decreasing stack.** Make the 2 as large as possible while guaranteeing a 3, leaving only a smaller 1 to find.

### Thought process
1. **First idea:** Try triples in O(n³), or pairs with a running minimum in O(n²).
2. **The insight:** Make the 2 as large as possible while guaranteeing a 3, leaving only a smaller 1 to find.

### Optimal approach
1. Scan from the right.
2. Succeed if below the saved two.
3. Pop smaller values into two.
4. Push.

### Dry run
```text
nums = [3,1,4,2]
x  two before  action           stack
2  none        push             2
4  none        pop 2, two = 2   4
1  2           1 < 2, true
```

### Why it works
A value becomes two only when a larger value lies to its left. The largest such two gives any future 1 its best chance.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public boolean find132pattern(int[] nums) {
        Deque<Integer> stack = new ArrayDeque<>();
        int two = Integer.MIN_VALUE;
        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums[i] < two) return true;
            while (!stack.isEmpty() && stack.peek() < nums[i]) two = stack.pop();
            stack.push(nums[i]);
        }
        return false;
    }
}
```

### Code walkthrough
- `i--`: Backwards, so the 1 comes last.
- `two = stack.pop()`: Beaten, with proof of a 3.
- `nums[i] < two`: The 1 arrives.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Requiring adjacency.** It is a subsequence.
2. **Popping equals.** The middle must be strictly larger.
3. **Only the global minimum.** It may come too late.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| Short | `[1,3]` | `false` |
| Found | `[1,3,2]` | `true` |
| Equal | `[1,2,2]` | `false` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[067 — 496. Next Greater Element I]]: The same eviction. The key idea there: Values without an answer are always decreasing, so each arrival only checks the top.
- **334. Increasing Triplet**: Two running minima suffice.

### In a real interview
Say this: *"Scanning backwards, a decreasing stack certifies the best possible 2. Then I need one smaller value."*

## 5. Recall
Q: 456. 132 Pattern: which pattern, and what is the insight?
A: Decreasing stack. Make the 2 as large as possible while guaranteeing a 3, leaving only a smaller 1 to find.

Q: 456. 132 Pattern: what is the analogy, and what does it stand for?
A: An auction watched backwards: big bidder → the 3; best runner-up → `two`.

Q: 456. 132 Pattern: What does the saved two guarantee?
A: A larger value lies to its left, after the current position.

Q: 456. 132 Pattern: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 2 | MODULE | Stack — Hard | 2 problems | MUST
# Stack: Hard

Patterns in this group: Stacks by frequency, Increasing stack.

@@ 3 | TOPIC | 086 — 895. Maximum Frequency Stack | Hard · Stacks by frequency | SHOULD
# 086 — 895. Maximum Frequency Stack

**Problem 86 – Topic: Stack | Difficulty: Hard | Pattern: Stacks by frequency**

[Open on LeetCode](https://leetcode.com/problems/maximum-frequency-stack/)

## 1. Problem Statement
Design a stack whose pop removes the most frequent value, choosing the most recently pushed on ties.

### Examples
```text
Input:  ["FreqStack", "push", "push", "push", "push", "push", "push", "pop", "pop", "pop", "pop"]
        [[], [5], [7], [5], [7], [4], [5], [], [], [], []]
Output: [null, null, null, null, null, null, null, 5, 7, 5, 4]
```

### Constraints
```text
0 <= val <= 10^9
At most 2 * 10^4 calls will be made to push and pop.
It is guaranteed that there will be at least one element in the stack before calling pop.
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: A building where copies go upstairs
A value's first copy goes on floor 1, its second on floor 2, and so on. Always take from the top floor's newest end. A value removed there still has its older copy a floor below.

| In the story | In the solution |
|---|---|
| floor f | a stack of f-th copies |
| top floor | `highest` |

### Pattern signal
**Stacks by frequency.** One stack per frequency keeps recency and frequency together, with no moving.

### Thought process
1. **First idea:** Scan the history on pop, in O(n).
2. **The insight:** One stack per frequency keeps recency and frequency together, with no moving.

### Optimal approach
1. Increase the value's count.
2. Push it on that floor.
3. Pop from the highest.
4. Lower its count.
5. Drop an empty floor.

### Dry run
```text
push 5,7,5,7,4,5
floor 1: 5 7 4
floor 2: 5 7
floor 3: 5
pops: 5 from floor 3, 7 from floor 2, 5 from floor 2, 4 from floor 1
```

### Why it works
Each floor holds, in arrival order, the values that reached that count. The top floor's top is the required value.

## 3. Java Solution with Explanation
### Code
```java
class FreqStack {
    private final Map<Integer, Integer> count = new HashMap<>();
    private final List<Deque<Integer>> floors = new ArrayList<>();

    public void push(int val) {
        int floor = count.merge(val, 1, Integer::sum);
        if (floors.size() < floor) floors.add(new ArrayDeque<>());
        floors.get(floor - 1).push(val);
    }

    public int pop() {
        int highest = floors.size() - 1;
        int val = floors.get(highest).pop();
        if (floors.get(highest).isEmpty()) floors.remove(highest);
        count.merge(val, -1, Integer::sum);
        return val;
    }
}
```

### Code walkthrough
- `count.merge(val, 1`: Which floor this copy takes.
- `floors.get(floor - 1).push(val)`: Lower copies stay.
- `floors.remove(highest)`: An empty top floor closes.

### Complexity
- **Time:** O(1) per operation
- **Space:** O(n)

### Common mistakes
1. **Moving values between floors.** Older recency is lost.
2. **A heap by frequency only.** Ties are wrong.
3. **Not lowering the count.** The next push lands too high.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `push(1), pop` | `1` |
| Tie | `push(1), push(2), pop` | `2` |
| Frequency | `push(1), push(1), push(2), pop` | `1` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- [[300 — 460. LFU Cache]]: The least frequent instead. The key idea there: A count only grows by one, so books move one shelf at a time and the lowest shelf can be tracked in O(1).
- [[072 — 155. Min Stack]]: A simpler saved best. The key idea there: The minimum below a plate never changes while the plate is there, so it can be saved with it.

### In a real interview
Say this: *"I keep one stack per copy number. That preserves recency inside each frequency."*

## 5. Recall
Q: 895. Maximum Frequency Stack: which pattern, and what is the insight?
A: Stacks by frequency. One stack per frequency keeps recency and frequency together, with no moving.

Q: 895. Maximum Frequency Stack: what is the analogy, and what does it stand for?
A: A building where copies go upstairs: floor f → a stack of f-th copies; top floor → `highest`.

Q: 895. Maximum Frequency Stack: Where does the third copy of a value go?
A: On stack 3, while copies 1 and 2 stay below.

Q: 895. Maximum Frequency Stack: what are the time and space costs?
A: Time O(1) per operation. Space O(n).

@@ 3 | TOPIC | 087 — 84. Largest Rectangle in Histogram | Hard · Increasing stack | MUST
# 087 — 84. Largest Rectangle in Histogram

**Problem 87 – Topic: Stack | Difficulty: Hard | Pattern: Increasing stack**

[Open on LeetCode](https://leetcode.com/problems/largest-rectangle-in-histogram/)

## 1. Problem Statement
Bars of width 1 have the given heights. Return the area of the largest rectangle within the histogram.

### Examples
```text
Input:  heights = [2,1,5,6,2,3]
Output: 10

Input:  heights = [2,4]
Output: 4
```

### Constraints
```text
1 <= heights.length <= 10^5
0 <= heights[i] <= 10^4
```

**Stop here and try it for 40 minutes before reading on.**

## 2. Understanding & Approach (No Code)
### Analogy: Stretching a banner along buildings
A banner at a building's roof height stretches both ways until it meets a lower building. Buildings wait on a stack. A lower one tells each taller one "you stop here", and the one beneath marks where it began.

| In the story | In the solution |
|---|---|
| you stop here | the popping index |
| where it began | after the new stack top |

### Pattern signal
**Increasing stack.** An increasing stack knows both first smaller neighbors at the moment of popping.

### Thought process
1. **First idea:** Expand from every bar, in O(n²).
2. **The insight:** An increasing stack knows both first smaller neighbors at the moment of popping.

### Optimal approach
1. Add an imaginary zero at the end.
2. Pop taller bars.
3. Width lies between the two smaller bars.
4. Record.
5. Push.

### Dry run
```text
heights = [2,1,5,6,2,3]
i, height  popped  left smaller  width  area
1, 1       2       -1            1      2
4, 2       6       2             1      6
4, 2       5       1             2      10
6, 0       3       4             1      3
6, 0       2       1             4      8
6, 0       1       -1            6      6
```

### Why it works
The optimal rectangle's height is its shortest bar. That bar's widest range is measured when it is popped.

## 3. Java Solution with Explanation
### Code
```java
class Solution {
    public int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int best = 0;
        for (int i = 0; i <= heights.length; i++) {
            int h = (i == heights.length) ? 0 : heights[i];
            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                int height = heights[stack.pop()];
                int left = stack.isEmpty() ? -1 : stack.peek();
                best = Math.max(best, height * (i - left - 1));
            }
            stack.push(i);
        }
        return best;
    }
}
```

### Code walkthrough
- `i <= heights.length`: One imaginary building flushes all.
- `stack.isEmpty() ? -1`: Nothing lower on the left.
- `i - left - 1`: Both blockers are excluded.

### Complexity
- **Time:** O(n)
- **Space:** O(n)

### Common mistakes
1. **Not flushing.** A rising histogram gives 0.
2. **Width from the popped index.** It extends further left.
3. **Tallest times n.** The lowest limits it.

### Edge cases
| Case | Input | Expected |
|---|---|---|
| One | `[4]` | `4` |
| Rising | `[1,2,3]` | `4` |
| Equal | `[2,2]` | `4` |

*Solution written for this plan. Compiled, not run.*

## 4. Bonus
### Variations
- **85. Maximal Rectangle**: A histogram per row.
- [[244 — 1856. Maximum Subarray Min-Product]]: Multiply by a range sum. The key idea there: All values are positive, so for a fixed minimum the widest range has the largest sum.

### In a real interview
Say this: *"Every bar is tried as the limiting height. An increasing stack gives both boundaries in linear time."*

## 5. Recall
Q: 84. Largest Rectangle in Histogram: which pattern, and what is the insight?
A: Increasing stack. An increasing stack knows both first smaller neighbors at the moment of popping.

Q: 84. Largest Rectangle in Histogram: what is the analogy, and what does it stand for?
A: Stretching a banner along buildings: you stop here → the popping index; where it began → after the new stack top.

Q: 84. Largest Rectangle in Histogram: What are a popped bar's boundaries?
A: The current index and the new stack top, both excluded.

Q: 84. Largest Rectangle in Histogram: what are the time and space costs?
A: Time O(n). Space O(n).

@@ 3 | TOPIC | Practice — Pattern summary: Stack | Key patterns of the topic | MUST
# Pattern summary: Stack

A stack holds work waiting for an answer, newest first. A monotonic stack also keeps that waiting work sorted, so one arrival can resolve many items.

| Pattern | Reach for it in problems like | Watch out for |
|---|---|---|
| Monotonic stack | For queried values find the next greater value in another array. (496, 739) | Searching to the left |
| Matching stack | Check that brackets close in the correct order. (20) | Popping an empty stack |
| Record stack | Evaluate baseball score operations. (682) | Losing the operands while computing plus |
| Queue rotation | Implement a stack using a queue. (225) | Forgetting to restore after top |
| Two stacks | Implement a queue using stacks. (232) | Refilling a non-empty out stack |
| Auxiliary minimum | Design a stack with O(1) minimum. (155) | Losing duplicate minima |
| Operand stack | Evaluate a postfix expression. (150) | Reversing subtraction and division |
| Undo stack | Remove each star with the closest character to its left. (2390) | Reversing the final string |
| Simulation stack | Decide whether pop order is possible for a push order. (946) | Popping only once per push |
| Open and close choices | Generate all valid strings of n bracket pairs. (22) | Filtering all 2^(2n) strings |
| Collision stack | Find the asteroids left after collisions. (735) | Left then right movers never collide |
| Stack of spans | Return how many consecutive days up to today have price at most today's. (901) | Stopping at equal prices |
| Arrival-time stack | Count car fleets reaching the target. (853) | Integer division for time |
| Path stack | Simplify an absolute Unix path. (71) | Three dots are a normal name |
| Nested decoding | Expand strings like 3[a2[c]]. (394) | Multi-digit counts |
| Increasing stack | Remove k digits to make the smallest number. (402, 84) | An increasing input still needs k removals |
| Run-length stack | Repeatedly delete k equal adjacent letters. (1209) | Missing runs formed after a deletion |
| Decreasing stack | Find i, j, k in order with values forming a 1-3-2 pattern. (456) | Allowing equal values |
| Stacks by frequency | Pop the most frequent value, newest on ties. (895) | Losing recency on ties |

## Remember each pattern by its story

| When the problem says | Think of | Pattern | Problems |
|---|---|---|---|
| properly nested | boxes inside boxes | matching stack | 20, 394 |
| remove the previous | the undo button | undo stack | 2390, 71, 682, 1209 |
| remember the best after pops | a note on every plate | auxiliary state | 155, 895 |
| next greater, days until | necks craning for someone taller | decreasing stack | 496, 739, 901, 456 |
| smallest after deletions | let small digits push ahead | increasing stack | 402 |
| largest range with this as minimum | a bar widening until blocked | increasing stack | 84 |
| things colliding | a corridor fight | simulation | 735, 946, 853 |

## Which pattern do I pick?

1. **Newest first?** Use a plain stack.
2. **Next or previous bigger or smaller?** Use a monotonic stack. To find a bigger one keep it decreasing, to find a smaller one keep it increasing.
3. **Need distance?** Store indices or spans, not only values.
4. **Anything left at the end?** Decide what it means: no answer (739), still open (20), or extends to the end (84).

Q: Which signals point to Stack?
A: nesting; undo; most recent; next greater or smaller.

Q: What is the core idea of Stack?
A: A stack holds work waiting for an answer, newest first. A monotonic stack also keeps that waiting work sorted, so one arrival can resolve many items.
