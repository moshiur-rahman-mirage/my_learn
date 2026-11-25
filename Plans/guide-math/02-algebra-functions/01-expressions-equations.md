@@ 1 | MODULE | Phase 1 — Expressions, Equations and Inequalities (Foundation) | Variables, simplifying expressions, solving linear equations and inequalities, formulas, word problems | MUST
# Phase 1: Expressions, equations and inequalities

Algebra is arithmetic with **unknowns**. Instead of "what is $3 \times 4 + 2$?", it asks "which number $x$ makes $3x + 2 = 14$?". This roadmap takes you from writing your first expression to logarithms, polynomials and series.

**What you need first:** the Numbers & Arithmetic roadmap, especially fractions, negative numbers and the order of operations. Most algebra mistakes are really arithmetic mistakes.

Each topic has six parts: what it covers, where it is used, what it means, formulas with worked examples, common mistakes, and practice flashcards.

@@ 2 | MODULE | Algebraic expressions | Variables, writing and evaluating expressions, like terms, distributing and factoring | MUST
An **expression** is a recipe for a number, like $3x + 2$. It has no "=" sign. You can simplify or evaluate it, but not "solve" it.

@@ 3 | TOPIC | Variables and writing expressions | Variables as unknowns and as changing quantities; translating words into algebra | MUST
# Variables and writing expressions

## 1. What it covers
- What a **variable** is: a letter standing for a number.
- Variables as an **unknown** (one value to find) or a **changing quantity** (many values).
- Translating phrases like "three more than twice a number" into algebra.

## 2. Where it is used
- **Programming:** a variable `price` holds a value that can change; `total = price * qty + delivery` is an algebraic expression.
- **Spreadsheets:** `=B2*C2+D2` is an expression in which the cells act as variables.
- **Business formulas:** profit $= 120n - 5000$ for $n$ items sold.
- **Every later topic**, since all of algebra is written this way.

## 3. What it means
A variable is a **labelled box** for a number. Writing $n$ instead of a specific number lets you describe a rule that works for every number at once: "the cost of $n$ pens at 15 taka each is $15n$".

**Translation keywords:**
- **sum, more than, increased by, plus** → $+$
- **difference, less than, decreased by, minus** → $-$ (watch the order: "5 less than $x$" is $x - 5$)
- **product, times, of, twice** → $\times$
- **quotient, divided by, per, ratio** → $\div$

**Notation:** $3x$ means $3 \times x$; $\frac{x}{4}$ means $x \div 4$; $x^2$ means $x \times x$.

## 4. Formula & worked example
**Example 1.** "Three more than twice a number $n$" → $2n + 3$

**Example 2.** "Five less than a number $x$" → $x - 5$ (not $5 - x$)

**Example 3.** "The quotient of a number and 4, decreased by 7" → $\frac{y}{4} - 7$

**Example 4.** "The square of the sum of $a$ and $b$" → $(a + b)^2$. Compare "the sum of the squares": $a^2 + b^2$.

**Example 5.** A taxi charges 60 taka plus 25 taka per km. Write the fare for $d$ km.
- $60 + 25d$

**Example 6.** A phone plan costs 300 taka per month plus 2 taka per extra minute $m$. Cost for a year?
- $12(300 + 2m)$ if $m$ is extra minutes per month.

## 5. Common mistakes
- Reversing "less than": "7 less than $x$" is $x - 7$.
- Forgetting brackets: "twice the sum of $x$ and 3" is $2(x + 3)$, not $2x + 3$.
- Using the same letter for two different unknowns.

## 6. Practice
Q: Write "6 times a number, minus 4".
A: $6n - 4$.

Q: Write "the sum of $x$ and 9, divided by 2".
A: $\frac{x + 9}{2}$.

Q: Write "10 less than three times $p$".
A: $3p - 10$.

Q: Tickets cost 250 taka for adults ($a$) and 150 for children ($c$). Total cost?
A: $250a + 150c$.

Q: Write "half the square of $r$".
A: $\frac{r^2}{2}$.

@@ 3 | TOPIC | Evaluating expressions and formulas | Substituting values, order of operations, using real formulas | MUST
# Evaluating expressions and formulas

## 1. What it covers
- **Substituting** numbers for variables.
- Following the order of operations carefully, especially with negatives.
- Evaluating real formulas (area, speed, temperature, interest).

## 2. Where it is used
- **Programming:** every time code runs `area = 3.14159 * r ** 2` it evaluates an expression.
- **Spreadsheets:** formulas recalculate when cell values change.
- **Science and engineering:** plugging measurements into formulas.
- **Finance:** loan and interest calculators.

## 3. What it means
To evaluate, **replace each variable with its value in brackets**, then calculate with BODMAS. The brackets protect you from sign mistakes: $x^2$ at $x = -3$ becomes $(-3)^2 = 9$, not $-9$.

## 4. Formula & worked example
**Example 1.** $4a - 3b$ at $a = 5$, $b = -2$.
- $4(5) - 3(-2) = 20 + 6 = 26$

**Example 2.** $\frac{x^2 - 1}{x + 3}$ at $x = -2$.
- $\frac{(-2)^2 - 1}{-2 + 3} = \frac{3}{1} = 3$

**Example 3 (temperature).** $F = \frac{9}{5}C + 32$ at $C = 30$.
- $F = 54 + 32 = 86^\circ$F

**Example 4 (falling object).** $h = 80 - 4.9t^2$ at $t = 3$.
- $h = 80 - 4.9(9) = 80 - 44.1 = 35.9$ m

**Example 5 (code).**
```python
def bmi(weight_kg, height_m):
    return weight_kg / height_m ** 2
print(round(bmi(70, 1.75), 1))   # 22.9
```
`height_m ** 2` is evaluated first (powers before division), exactly as BODMAS says: $\frac{70}{1.75^2} = \frac{70}{3.0625} \approx 22.9$.

## 5. Common mistakes
- Substituting without brackets: $-x^2$ at $x = -4$ is $-(16) = -16$.
- $3x$ at $x = 5$ written as $35$. It is $3 \times 5 = 15$.
- Doing operations left to right instead of by BODMAS.

## 6. Practice
Q: $2x^2 - 5x$ at $x = -1$?
A: $2(1) + 5 = 7$.

Q: $\frac{a + b}{a - b}$ at $a = 7$, $b = 3$?
A: $\frac{10}{4} = 2.5$.

Q: $A = \pi r^2$ with $r = 10$ (use $\pi \approx 3.14$)?
A: $314$.

Q: $C = \frac{5}{9}(F - 32)$ at $F = 212$?
A: $100$.

Q: $-3y^3$ at $y = -2$?
A: $-3(-8) = 24$.

@@ 3 | TOPIC | Terms, coefficients and combining like terms | Parts of an expression; simplifying by collecting like terms, with signed coefficients | MUST
# Terms, coefficients and combining like terms

## 1. What it covers
- The parts of an expression: **terms**, **coefficients**, **constants**, **factors**.
- **Like terms**: same variables with the same powers.
- Simplifying by combining like terms, including negative coefficients.

## 2. Where it is used
- **Simplifying formulas** before using them, which means fewer operations and fewer errors.
- **Computer algebra and compilers:** "constant folding" and simplification in compilers do exactly this.
- **Accounting:** totalling similar items: $3x + 5x$ is like 3 boxes + 5 boxes of the same product.
- **Every equation you solve.**

## 3. What it means
In $5x^2 - 3x + 7$:
- **Terms** (separated by + or −): $5x^2$, $-3x$, $7$. The sign belongs to the term after it.
- **Coefficients:** $5$ and $-3$ (the numbers multiplying variables).
- **Constant:** $7$.

**Like terms** have exactly the same variable part: $4xy$ and $-9xy$ are like; $x^2$ and $x$ are **not**. You can only add like terms, just as you can add 3 apples and 5 apples but not 3 apples and 5 oranges. Add the coefficients and keep the variable part.

## 4. Formula & worked example
$$
ax + bx = (a + b)x
$$

**Example 1.** $7x + 3 - 2x + 9 = 5x + 12$

**Example 2.** $4a^2 - 3a + a^2 + 8a = 5a^2 + 5a$

**Example 3.** $-6m + 2n - m - 5n = -7m - 3n$

**Example 4.** $3xy - 2x + 5yx = 8xy - 2x$ ($xy$ and $yx$ are the same term)

**Example 5.** $\frac{1}{2}p + \frac{3}{4}p = \frac{5}{4}p$

**Example 6.** $0.3x - 1.2 + 0.7x - 0.8 = x - 2$

## 5. Common mistakes
- Combining unlike terms: $3x + 2x^2 \ne 5x^3$.
- Losing the sign: in $5 - 3x$, the term is $-3x$.
- Thinking $x$ has coefficient 0. It has coefficient 1 ($x = 1x$), and $-x$ has coefficient $-1$.

## 6. Practice
Q: Simplify $9y - 4 - 3y + 10$.
A: $6y + 6$.

Q: Simplify $2x^2 + 5x - x^2 - 7x$.
A: $x^2 - 2x$.

Q: Coefficient of $x$ in $4 - x + 3x^2$?
A: $-1$.

Q: Simplify $-a + 4b - 3a - b$.
A: $-4a + 3b$.

Q: Are $5x^2y$ and $5xy^2$ like terms?
A: No, the powers of $x$ and $y$ differ.

@@ 3 | TOPIC | Distributive property and factoring expressions | Expanding brackets, negative multipliers, taking out common factors | MUST
# Distributive property and factoring expressions

## 1. What it covers
- **Expanding:** $a(b + c) = ab + ac$, including negative multipliers.
- Expanding and then simplifying.
- **Factoring** linear expressions by taking out the GCF.
- **Equivalent expressions**: checking them by substitution.

## 2. Where it is used
- **Solving equations:** removing brackets is often the first step.
- **Mental maths:** $7 \times 98 = 7(100 - 2)$.
- **Programming:** refactoring `a*b + a*c` into `a*(b + c)` saves a multiplication; database query optimisers use the same rules.
- **Factoring** is the key skill for quadratics later.

## 3. What it means
**Distributing:** the number outside multiplies **every** term inside. Buying 4 gift packs, each with 2 pens and 3 pencils, gives $4(2 + 3) = 8$ pens $+ 12$ pencils.

A minus sign in front of a bracket is a multiplier of $-1$: $-(x - 5) = -x + 5$. Every sign inside flips.

**Factoring** is distributing in reverse: find the GCF of all terms and write it outside the bracket. $6x + 15 = 3(2x + 5)$.

**Equivalent expressions** give the same value for **every** input. Quick test: substitute a couple of values into both.

## 4. Formula & worked example
$$
a(b + c) = ab + ac \qquad -(b - c) = -b + c \qquad ab + ac = a(b + c)
$$

**Example 1.** $5(2x - 3) = 10x - 15$

**Example 2.** $-4(3a - 2) = -12a + 8$

**Example 3.** $3(x + 4) - 2(x - 5) = 3x + 12 - 2x + 10 = x + 22$

**Example 4.** Factor $12y - 18$: GCF 6 → $6(2y - 3)$

**Example 5.** Factor $8x^2 + 20x$: GCF $4x$ → $4x(2x + 5)$

**Example 6.** Are $2(x + 3) + x$ and $3x + 6$ equivalent?
- Expand: $2x + 6 + x = 3x + 6$ ✓. (Check with $x = 1$: $9 = 9$ ✓.)

## 5. Common mistakes
- Multiplying only the first term: $3(x + 4) \ne 3x + 4$.
- Forgetting to flip all signs: $-(x - 5) \ne -x - 5$.
- Factoring out less than the greatest common factor: $8x + 12 = 2(4x + 6)$ is correct but not complete; $4(2x + 3)$ is.

## 6. Practice
Q: Expand $-2(5 - 3x)$.
A: $-10 + 6x$.

Q: Simplify $4(2a + 1) - 3(a - 2)$.
A: $5a + 10$.

Q: Factor $21m + 14$.
A: $7(3m + 2)$.

Q: Factor $15x^2 - 10x$.
A: $5x(3x - 2)$.

Q: Is $4(x - 1)$ equivalent to $4x - 1$?
A: No: $4(x - 1) = 4x - 4$.

@@ 2 | MODULE | Linear equations and inequalities | Solving one-step to multi-step equations, formulas, word problems, inequalities | MUST
An **equation** says two expressions are equal. Solving it means finding every value of the variable that makes it true.

@@ 3 | TOPIC | Equations as a balance; one-step equations | What a solution is, inverse operations, checking solutions | MUST
# Equations as a balance; one-step equations

## 1. What it covers
- What an equation and a **solution** are.
- Testing whether a value is a solution.
- Solving one-step equations with **inverse operations** (add ↔ subtract, multiply ↔ divide).

## 2. Where it is used
- **Everyday reasoning:** "I paid 450 taka for 3 kg; what's the price per kg?" is $3x = 450$.
- **Spreadsheets:** Excel's Goal Seek solves equations for you.
- **Programming:** working out loop counts, buffer sizes, timeouts.
- **All later algebra.**

## 3. What it means
An equation is like a **balanced scale**: the left side weighs the same as the right. You may do anything to one side as long as you do **exactly the same** to the other, and the scale stays balanced.

To find $x$, "undo" whatever is being done to it with the **inverse operation**:
- $x + 7 = 12$: 7 was added, so subtract 7 from both sides.
- $\frac{x}{4} = 9$: $x$ was divided by 4, so multiply both sides by 4.

**Always check** by putting your answer back into the original equation.

## 4. Formula & worked example
$$
x + a = b \Rightarrow x = b - a \qquad ax = b \Rightarrow x = \frac{b}{a} \ (a \ne 0)
$$

**Example 1.** Is $x = 4$ a solution of $3x - 2 = 10$?
- $3(4) - 2 = 10$ ✓ yes.

**Example 2.** $x - 8 = -3$ → $x = -3 + 8 = 5$

**Example 3.** $-6x = 42$ → $x = \frac{42}{-6} = -7$

**Example 4.** $\frac{x}{5} = -2.4$ → $x = -12$

**Example 5.** $\frac{2}{3}x = 10$ → multiply by the reciprocal $\frac{3}{2}$: $x = 15$

**Example 6.** $1.25 + y = 4$ → $y = 2.75$

## 5. Common mistakes
- Doing an operation to only one side.
- Using the wrong inverse: for $-6x = 42$, dividing by 6 instead of $-6$.
- With $\frac{2}{3}x = 10$, multiplying by $\frac{2}{3}$ instead of its reciprocal.

## 6. Practice
Q: Solve $x + 13 = 4$.
A: $x = -9$.

Q: Solve $7x = -56$.
A: $x = -8$.

Q: Solve $\frac{x}{-3} = 6$.
A: $x = -18$.

Q: Solve $\frac{3}{4}x = 9$.
A: $x = 12$.

Q: Is $x = -2$ a solution of $5 - x = 3$?
A: No: $5 - (-2) = 7 \ne 3$.

@@ 3 | TOPIC | Two-step and multi-step equations | Undoing operations in reverse order, equations with brackets | MUST
# Two-step and multi-step equations

## 1. What it covers
- Two-step equations like $3x + 5 = 20$.
- Multi-step equations with brackets and like terms to collect.
- The order in which to undo operations.

## 2. Where it is used
- **Pricing:** a 60 taka base fare plus 25 per km came to 310: how many km? ($60 + 25d = 310$.)
- **Physics:** solving $v = u + at$ for $t$ with numbers.
- **Budgets:** "how many months until I've saved enough?"
- **Programming:** calculating an index or offset from a formula.

## 3. What it means
Think of how $x$ was "wrapped": in $3x + 5$, $x$ was first multiplied by 3, then 5 was added. To unwrap it, undo in **reverse order**: subtract 5 first, then divide by 3. It's like taking off shoes before socks.

**Multi-step recipe:**
1. Expand any brackets.
2. Combine like terms on each side.
3. Undo addition/subtraction.
4. Undo multiplication/division.
5. Check.

## 4. Formula & worked example
$$
ax + b = c \Rightarrow x = \frac{c - b}{a}
$$

**Example 1.** $3x + 5 = 20$
- $3x = 15$ → $x = 5$

**Example 2.** $\frac{x}{4} - 7 = -2$
- $\frac{x}{4} = 5$ → $x = 20$

**Example 3.** $5 - 2x = 17$
- $-2x = 12$ → $x = -6$

**Example 4.** $4(x - 3) + 2 = 18$
- $4x - 12 + 2 = 18$ → $4x - 10 = 18$ → $4x = 28$ → $x = 7$

**Example 5.** $3(2y + 1) - 4y = 15$
- $6y + 3 - 4y = 15$ → $2y = 12$ → $y = 6$

**Example 6 (taxi).** $60 + 25d = 310$ → $25d = 250$ → $d = 10$ km

## 5. Common mistakes
- Dividing first: $3x + 5 = 20$ → $x + 5 = \frac{20}{3}$ ✗ (you must divide **every** term).
- Losing a negative: $5 - 2x = 17$ gives $-2x = 12$, not $2x = 12$.
- Not distributing the number in front of the brackets to every term.

## 6. Practice
Q: Solve $7x - 4 = 31$.
A: $x = 5$.

Q: Solve $\frac{x}{3} + 8 = 2$.
A: $x = -18$.

Q: Solve $2(3x + 4) = 26$.
A: $x = 3$.

Q: Solve $10 - 3x = -5$.
A: $x = 5$.

Q: Solve $5(x - 1) - 2(x + 3) = 4$.
A: $3x - 11 = 4$ → $x = 5$.

@@ 3 | TOPIC | Variables on both sides and number of solutions | Collecting variables on one side; equations with no solution or infinitely many | MUST
# Variables on both sides and number of solutions

## 1. What it covers
- Moving all variable terms to one side and constants to the other.
- Recognising equations with **one**, **no** or **infinitely many** solutions.

## 2. Where it is used
- **Comparing plans:** Plan A costs $500 + 20m$, plan B costs $200 + 35m$. When are they equal? $500 + 20m = 200 + 35m$.
- **Break-even analysis:** cost = revenue.
- **Catch-up problems:** when does one runner catch another?
- **Software testing:** recognising "always true" or "never true" conditions in code (dead code).

## 3. What it means
Collect the variable terms on one side by adding or subtracting the same term on both sides (tip: move the smaller variable term to keep coefficients positive).

Sometimes the variable **disappears**:
- You get a **false** statement like $3 = 7$ → **no solution**. The two sides can never be equal (two parallel lines that never meet).
- You get a **true** statement like $5 = 5$ → **infinitely many solutions**. The two sides are the same expression (an identity).

## 4. Formula & worked example
**Example 1.** $5x + 3 = 2x + 18$
- Subtract $2x$: $3x + 3 = 18$ → $3x = 15$ → $x = 5$

**Example 2 (phone plans).** $500 + 20m = 200 + 35m$
- $300 = 15m$ → $m = 20$ minutes. Below 20 minutes plan B is cheaper; above, plan A.

**Example 3 (no solution).** $3(x + 2) = 3x + 10$
- $3x + 6 = 3x + 10$ → $6 = 10$ ✗ → no solution

**Example 4 (infinitely many).** $2(2x - 1) + 3 = 4x + 1$
- $4x - 2 + 3 = 4x + 1$ → $4x + 1 = 4x + 1$ → $1 = 1$ ✓ → all real numbers

**Example 5.** $0.5(6 - 4x) = 3x - 7$
- $3 - 2x = 3x - 7$ → $10 = 5x$ → $x = 2$

## 5. Common mistakes
- Subtracting a term from one side and adding it to the other.
- Writing "$x = 0$" when the variable cancels. Check whether the result is true (infinitely many) or false (none).
- Sign errors when moving negative terms.

## 6. Practice
Q: Solve $7x - 4 = 3x + 12$.
A: $x = 4$.

Q: Solve $2(x - 3) = 2x - 6$.
A: Infinitely many solutions (identity).

Q: Solve $4x + 1 = 4x - 5$.
A: No solution.

Q: Solve $9 - 2y = y + 3$.
A: $y = 2$.

Q: Gym A: 1000 joining fee + 800/month. Gym B: 2000/month. Same cost after how many months?
A: $1000 + 800m = 2000m$ → $m = \frac{5}{6}$, i.e. gym A is cheaper from month 1 onward.

@@ 3 | TOPIC | Equations with fractions and decimals | Clearing denominators with the LCD, scaling decimals | MUST
# Equations with fractions and decimals

## 1. What it covers
- **Clearing fractions** by multiplying every term by the least common denominator (LCD).
- Clearing decimals by multiplying by a power of 10.
- Fractions with expressions on top, like $\frac{x + 1}{3}$.

## 2. Where it is used
- **Mixtures and averages:** "the average of $x$, 70 and 85 is 80" → $\frac{x + 70 + 85}{3} = 80$.
- **Finance:** equations with percentages like $0.05x + 0.08(1000 - x) = 65$.
- **Science:** formulas with fractions (lens equation, parallel resistors).
- **Rates of work:** "A does a job in 4 hours, B in 6".

## 3. What it means
Fractions make equations messy. Multiplying **every term on both sides** by the LCD removes all denominators at once, and the scale stays balanced.

Decimals: multiply every term by 10, 100, ... until all the numbers are whole.

**Important:** a numerator like $x + 1$ acts as a group; keep it in brackets when you multiply.

## 4. Formula & worked example
**Example 1.** $\frac{x}{3} + \frac{x}{4} = 7$
- LCD 12: $4x + 3x = 84$ → $7x = 84$ → $x = 12$

**Example 2.** $\frac{x + 1}{3} - \frac{x - 2}{5} = 2$
- ×15: $5(x + 1) - 3(x - 2) = 30$ → $5x + 5 - 3x + 6 = 30$ → $2x = 19$ → $x = 9.5$

**Example 3 (average).** $\frac{x + 70 + 85}{3} = 80$ → $x + 155 = 240$ → $x = 85$

**Example 4 (decimals).** $0.05x + 0.08(1000 - x) = 65$
- ×100: $5x + 8(1000 - x) = 6500$ → $5x + 8000 - 8x = 6500$ → $-3x = -1500$ → $x = 500$

**Example 5 (work).** A fills a tank in 4 h, B in 6 h. Together?
- $\frac{t}{4} + \frac{t}{6} = 1$ → ×12: $3t + 2t = 12$ → $t = 2.4$ h

## 5. Common mistakes
- Multiplying only the fractions and forgetting the whole-number terms.
- Dropping the brackets: $-\frac{x - 2}{5}$ times 15 is $-3(x - 2) = -3x + 6$, not $-3x - 6$.
- Choosing a common denominator that is not shared by all terms.

## 6. Practice
Q: Solve $\frac{x}{2} - \frac{x}{5} = 3$.
A: $5x - 2x = 30$ → $x = 10$.

Q: Solve $\frac{2x - 1}{3} = \frac{x + 4}{2}$.
A: $4x - 2 = 3x + 12$ → $x = 14$.

Q: Solve $0.3x + 1.2 = 0.5x - 0.4$.
A: $3x + 12 = 5x - 4$ → $x = 8$.

Q: Two pipes fill a tank in 3 h and 6 h. Together?
A: $\frac{t}{3} + \frac{t}{6} = 1$ → $t = 2$ h.

Q: Your marks are 65, 78 and $x$; you need an average of 75. Find $x$.
A: $x = 82$.

@@ 3 | TOPIC | Literal equations and formulas | Rearranging a formula to make another variable the subject | MUST
# Literal equations and formulas

## 1. What it covers
- Rearranging a formula to solve for a **chosen variable**.
- Using the same inverse-operation steps as with numbers.
- Formulas where the variable appears twice (factor it out).

## 2. Where it is used
- **Science and engineering:** from $v = u + at$ get $t = \frac{v - u}{a}$; from Ohm's law $V = IR$ get $R = \frac{V}{I}$.
- **Programming:** writing a function that computes the missing quantity.
- **Finance:** solving $A = P(1 + rt)$ for $r$.
- **Conversions:** $F = \frac{9}{5}C + 32$ → $C = \frac{5}{9}(F - 32)$.

## 3. What it means
A literal equation has several letters. "Solve for $t$" means treat $t$ as the unknown and **every other letter as a known number**. Use exactly the same steps as before: undo operations in reverse order.

If the target variable appears in two terms, collect them on one side and **factor it out**.

## 4. Formula & worked example
**Example 1.** Solve $v = u + at$ for $t$.
- $v - u = at$ → $t = \frac{v - u}{a}$

**Example 2.** Solve $A = \frac{1}{2}bh$ for $h$.
- $2A = bh$ → $h = \frac{2A}{b}$

**Example 3.** Solve $F = \frac{9}{5}C + 32$ for $C$.
- $F - 32 = \frac{9}{5}C$ → $C = \frac{5}{9}(F - 32)$

**Example 4.** Solve $A = P(1 + rt)$ for $r$.
- $\frac{A}{P} = 1 + rt$ → $\frac{A}{P} - 1 = rt$ → $r = \frac{A - P}{Pt}$

**Example 5 (variable twice).** Solve $ax + b = cx + d$ for $x$.
- $ax - cx = d - b$ → $x(a - c) = d - b$ → $x = \frac{d - b}{a - c}$

**Example 6.** Solve $y = \frac{x + 2}{x}$ for $x$.
- $xy = x + 2$ → $xy - x = 2$ → $x(y - 1) = 2$ → $x = \frac{2}{y - 1}$

## 5. Common mistakes
- Cancelling across addition: in $t = \frac{v - u}{a}$ you can't "cancel" letters.
- Leaving the target variable on both sides.
- Dividing by an expression that could be zero (note the condition, e.g. $a \ne c$).

## 6. Practice
Q: Solve $P = 2l + 2w$ for $w$.
A: $w = \frac{P - 2l}{2}$.

Q: Solve $V = \pi r^2 h$ for $h$.
A: $h = \frac{V}{\pi r^2}$.

Q: Solve $y = mx + c$ for $x$.
A: $x = \frac{y - c}{m}$.

Q: Solve $E = \frac{1}{2}mv^2$ for $v$ ($v > 0$).
A: $v = \sqrt{\frac{2E}{m}}$.

Q: Solve $3x + k = kx - 5$ for $x$.
A: $x(3 - k) = -5 - k$ → $x = \frac{k + 5}{k - 3}$.

@@ 3 | TOPIC | Writing equations from word problems | Defining a variable, translating, solving and checking in context | MUST
# Writing equations from word problems

## 1. What it covers
- A step-by-step method for turning a story into an equation.
- Common types: consecutive numbers, ages, perimeter, cost and money, percentages.
- Checking that the answer makes sense.

## 2. Where it is used
- **Every real use of algebra**: real problems arrive as words, not equations.
- **Business:** pricing, break-even, budgeting.
- **Interviews and exams:** aptitude tests are mostly word problems.
- **Software requirements:** turning "users get 10% off orders above 2000 taka" into code.

## 3. What it means
**Method:**
1. **Read** and find what is asked.
2. **Define** a variable: "let $x$ = ..." (with units).
3. **Express** other quantities in terms of $x$.
4. **Write** the equation from the sentence that gives a total or equality.
5. **Solve.**
6. **Check** in the original story (not just in your equation) and answer in words.

## 4. Formula & worked example
**Example 1 (consecutive).** Three consecutive integers add up to 87.
- Let $n$, $n + 1$, $n + 2$: $3n + 3 = 87$ → $n = 28$ → 28, 29, 30.

**Example 2 (ages).** A mother is 4 times as old as her son. In 6 years she'll be 3 times as old. Ages now?
- Son $s$, mother $4s$: $4s + 6 = 3(s + 6)$ → $4s + 6 = 3s + 18$ → $s = 12$. Son 12, mother 48.

**Example 3 (perimeter).** A rectangle's length is 5 m more than twice its width. Perimeter 46 m.
- $2w + 2(2w + 5) = 46$ → $6w + 10 = 46$ → $w = 6$, length $17$ m.

**Example 4 (money).** You have 50-taka and 100-taka notes, 23 notes in all, worth 1650 taka.
- $x$ fifties, $23 - x$ hundreds: $50x + 100(23 - x) = 1650$ → $-50x = -650$ → $x = 13$. So 13 fifties, 10 hundreds.

**Example 5 (percent).** After a 20% raise, a salary is 42 000. Original?
- $1.2x = 42\,000$ → $x = 35\,000$

## 5. Common mistakes
- Not defining the variable clearly (is $x$ the son's age now or in 6 years?).
- Applying a future change ("in 6 years") to only one person.
- Giving the value of $x$ when the question asks for something else (e.g. the length, not the width).

## 6. Practice
Q: Two consecutive even numbers sum to 74. Find them.
A: $n + (n + 2) = 74$ → 36 and 38.

Q: A number tripled, minus 8, equals 40. The number?
A: $3n - 8 = 40$ → $n = 16$.

Q: A shirt and trousers cost 3200 taka. The trousers cost 800 more than the shirt. Shirt price?
A: $s + s + 800 = 3200$ → $s = 1200$.

Q: Square perimeter 52 cm. Side?
A: $13$ cm.

Q: Ali is 5 years older than Bina; their ages sum to 41. Ages?
A: $b + b + 5 = 41$ → Bina 18, Ali 23.

@@ 3 | TOPIC | Linear inequalities | Solving, the flip rule for negatives, graphing on a number line | MUST
# Linear inequalities

## 1. What it covers
- The symbols $<$, $>$, $\le$, $\ge$ and what a solution **set** is.
- Solving one-step and multi-step inequalities.
- The **flip rule**: multiplying or dividing by a negative reverses the sign.
- Graphing on a number line (open vs closed circles).

## 2. Where it is used
- **Constraints:** "spend at most 5000 taka", "at least 18 years old".
- **Programming:** `if score >= 40` conditions; input validation.
- **Engineering tolerances** and safety limits.
- **Optimisation and linear programming** (Phase 2).

## 3. What it means
An inequality has **many** solutions, usually a whole interval of numbers. $x > 3$ includes 3.1, 4, 100, ... but not 3.

Solve exactly like an equation, **except**: when you multiply or divide both sides by a **negative**, the inequality sign flips. Why? $2 < 5$, but multiply by $-1$: $-2 > -5$. Multiplying by a negative mirrors the number line, which reverses the order.

**Graphing:** an **open circle** at the boundary for $<$ or $>$ (the number isn't included), a **closed circle** for $\le$ or $\ge$, and an arrow showing the direction.

## 4. Formula & worked example
$$
a < b \Rightarrow a + c < b + c \qquad a < b,\ c > 0 \Rightarrow ac < bc \qquad a < b,\ c < 0 \Rightarrow ac > bc
$$

**Example 1.** $3x - 4 < 11$ → $3x < 15$ → $x < 5$ (open circle at 5, arrow left)

**Example 2.** $-2x + 7 \ge 1$
- $-2x \ge -6$ → divide by $-2$ and **flip**: $x \le 3$

**Example 3.** $\frac{x}{-4} > 2$ → multiply by $-4$, flip: $x < -8$

**Example 4.** $5(x - 1) \le 3x + 9$
- $5x - 5 \le 3x + 9$ → $2x \le 14$ → $x \le 7$

**Example 5 (budget).** You have 5000 taka. A phone cover costs 800 and screen protectors cost 350 each. How many protectors can you buy?
- $800 + 350n \le 5000$ → $350n \le 4200$ → $n \le 12$ → at most 12.

## 5. Common mistakes
- Forgetting to flip when dividing by a negative.
- Flipping when the **number** is negative but you're not multiplying or dividing by it (e.g. $x + 3 > -2$ → $x > -5$, no flip).
- Using a closed circle for a strict inequality.

## 6. Practice
Q: Solve $4x + 3 > 19$.
A: $x > 4$.

Q: Solve $-3x \le 12$.
A: $x \ge -4$.

Q: Solve $7 - x < 2$.
A: $-x < -5$ → $x > 5$.

Q: Open or closed circle for $x \ge -2$?
A: Closed.

Q: You need an average of at least 60 over three tests. You scored 52 and 58. What do you need on the third?
A: $\frac{110 + x}{3} \ge 60$ → $x \ge 70$.

@@ 3 | TOPIC | Compound inequalities and interval notation | And/or inequalities, double inequalities, interval notation | MUST
# Compound inequalities and interval notation

## 1. What it covers
- **"And"** inequalities (both true, e.g. $-2 < x \le 5$).
- **"Or"** inequalities (at least one true, e.g. $x < -1$ or $x > 4$).
- Solving double inequalities in one go.
- **Interval notation**: $(a, b)$, $[a, b]$, $(-\infty, a)$, and $\cup$.

## 2. Where it is used
- **Validation:** an age field must be between 18 and 65; a password length from 8 to 64.
- **Programming:** `if 0 <= i < len(arr)` (Python allows chaining like this).
- **Quality control:** a part is acceptable if $9.95 \le d \le 10.05$ mm.
- **Domains of functions** (Phase 3), and absolute-value inequalities.

## 3. What it means
- **And:** the solution is the **overlap** of both conditions, usually one segment.
- **Or:** the solution is **everything** that satisfies either one, often two separate rays.

**Double inequality** $a < bx + c < d$: do the same operation to all **three** parts.

**Interval notation:** round bracket = endpoint not included; square bracket = included. Infinity always gets a round bracket.
- $-2 < x \le 5$ → $(-2, 5]$
- $x \ge 3$ → $[3, \infty)$
- $x < -1$ or $x > 4$ → $(-\infty, -1) \cup (4, \infty)$

## 4. Formula & worked example
**Example 1.** $-3 \le 2x + 1 < 9$
- Subtract 1: $-4 \le 2x < 8$ → divide by 2: $-2 \le x < 4$ → $[-2, 4)$

**Example 2.** $-5 < 1 - 3x \le 10$
- Subtract 1: $-6 < -3x \le 9$ → divide by $-3$ and flip both signs: $2 > x \ge -3$ → $-3 \le x < 2$ → $[-3, 2)$

**Example 3 (or).** $2x - 1 < -5$ or $x + 3 > 7$
- $x < -2$ or $x > 4$ → $(-\infty, -2) \cup (4, \infty)$

**Example 4 (and, no solution).** $x > 5$ and $x < 1$ → no overlap → no solution ($\varnothing$).

**Example 5 (tolerance).** A rod must be $50 \pm 0.2$ mm.
- $49.8 \le L \le 50.2$ → $[49.8, 50.2]$

## 5. Common mistakes
- Operating on only two of the three parts of a double inequality.
- Writing $[3, \infty]$. Infinity never gets a square bracket.
- Confusing "and" (overlap) with "or" (combine).

## 6. Practice
Q: Solve $1 < x + 4 \le 6$.
A: $-3 < x \le 2$, i.e. $(-3, 2]$.

Q: Write $x \le -1$ in interval notation.
A: $(-\infty, -1]$.

Q: Solve $-6 \le -2x < 4$.
A: $-2 < x \le 3$.

Q: Solve $x - 2 < 0$ or $x - 2 > 5$.
A: $x < 2$ or $x > 7$.

Q: Write $[0, 10)$ as an inequality.
A: $0 \le x < 10$.

@@ 3 | TOPIC | Practice & review: Expressions, equations and inequalities | Mixed problem set with worked answers | MUST
# Practice & review: Expressions, equations and inequalities

**How to use this:** 50 minutes, no calculator. Check every equation answer by substitution.

## Problems
1. Write "four less than the product of 7 and a number, all divided by 3".
2. Simplify $3(2x - 5) - 4(x - 2) + x$.
3. Factor $18a^2 - 12a$.
4. Solve $6 - 4(x + 1) = 2x - 10$.
5. Solve $\frac{3x}{4} - \frac{x - 2}{6} = 5$.
6. Solve $2(3x - 1) = 6x + 5$ and $4(x + 2) = 4x + 8$.
7. Solve $S = \frac{n}{2}(a + l)$ for $l$.
8. A rectangle's length is 3 less than 4 times its width; perimeter 74 cm. Find its dimensions.
9. Solve $5 - 2x > 13$ and graph it.
10. Solve $-7 < 3x - 4 \le 11$ in interval notation.

## Answers
1. $\frac{7n - 4}{3}$.
2. $6x - 15 - 4x + 8 + x = 3x - 7$.
3. $6a(3a - 2)$.
4. $6 - 4x - 4 = 2x - 10$ → $2 - 4x = 2x - 10$ → $12 = 6x$ → $x = 2$.
5. ×12: $9x - 2(x - 2) = 60$ → $7x + 4 = 60$ → $x = 8$.
6. First: $-2 = 5$ → no solution. Second: identity → all real numbers.
7. $\frac{2S}{n} = a + l$ → $l = \frac{2S}{n} - a$.
8. $2w + 2(4w - 3) = 74$ → $10w = 80$ → $w = 8$, $l = 29$ cm.
9. $-2x > 8$ → $x < -4$ (open circle at $-4$, arrow left).
10. $-3 < 3x \le 15$ → $-1 < x \le 5$ → $(-1, 5]$.

## Flashcards
Q: Golden rule of solving equations?
A: Do the same operation to both sides.

Q: When does an inequality sign flip?
A: When you multiply or divide both sides by a negative number.

Q: Variable cancels and leaves $4 = 4$. Meaning?
A: Infinitely many solutions (an identity).

Q: Round vs square bracket in interval notation?
A: Round = endpoint excluded; square = included.
