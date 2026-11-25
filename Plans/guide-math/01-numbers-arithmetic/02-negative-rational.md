@@ 1 | MODULE | Phase 2 — Negative and Rational Numbers (Foundation) | Number line, absolute value, ordering, the four operations with integers, negative fractions and decimals | MUST
# Phase 2: Negative and rational numbers

Negative numbers cause more algebra errors than anything else. The goal of this phase is to make the sign rules automatic, and to understand **why** they work so you never have to guess.

Keep using the **number line** as your picture: positive to the right, negative to the left.

@@ 2 | MODULE | Signed numbers | Meaning, ordering and arithmetic with negative numbers | MUST
Every rule here can be checked on a number line, with a thermometer or with money (credit and debt).

@@ 3 | TOPIC | Negative numbers, the number line and opposites | Meaning of negatives in context, locating numbers, opposites | MUST
# Negative numbers, the number line and opposites

## 1. What it covers
- What negative numbers mean in real situations.
- Placing integers, fractions and decimals on a number line.
- **Opposites** ($5$ and $-5$), and the opposite of an opposite.

## 2. Where it is used
- **Temperature:** $-5^\circ$C is five degrees below freezing.
- **Money and accounting:** a balance of $-2000$ taka is a debt; spreadsheets show negatives in red or brackets.
- **Elevation:** the Dead Sea shore is about $-430$ m (below sea level).
- **Programming:** negative array indices in Python (`a[-1]` is the last item), and coordinates on the left of or above an origin.
- **Physics:** direction: velocity $-3$ m/s means moving backwards.

## 3. What it means
A negative number is a quantity **less than zero**, or a value in the **opposite direction** from positive. On a number line it lies to the left of 0.

**Opposites** are the same distance from 0 on opposite sides: $7$ and $-7$. The minus sign can be read as "the opposite of". So $-(-7)$ is "the opposite of the opposite of 7", which brings you back to $7$. Turning around twice faces you forward again.

## 4. Formula & worked example
$$
-(-a) = a \qquad a + (-a) = 0
$$

**Example 1.** Write as a signed number: a submarine 250 m below sea level.
- $-250$ m

**Example 2.** Place $-2.5$, $\frac{3}{4}$, $-\frac{1}{2}$ and $1.5$ on a number line (left to right).
- $-2.5$, $-\frac{1}{2}$, $\frac{3}{4}$, $1.5$

**Example 3.** Simplify $-(-(-4))$.
- $-(-4) = 4$, then $-(4) = -4$. An **odd** number of minus signs gives a negative.

**Example 4.** The temperature was $3^\circ$C and fell $8^\circ$. What is it now?
- $3 - 8 = -5^\circ$C

## 5. Common mistakes
- Thinking $-8 > -3$ because $8 > 3$. On the number line, $-8$ is further left, so it is **smaller**.
- Reading $-x$ as "a negative number". If $x = -3$, then $-x = 3$ is positive.
- Mixing up the subtraction sign with the negative sign.

## 6. Practice
Q: What is the opposite of $-12$?
A: $12$.

Q: Simplify $-(-(-(-6)))$.
A: $6$ (four minus signs, an even number).

Q: A diver is at $-18$ m and rises 7 m. New depth?
A: $-11$ m.

Q: If $x = -5$, what is $-x$?
A: $5$.

Q: Which is to the left on the number line: $-0.3$ or $-0.25$?
A: $-0.3$.

@@ 3 | TOPIC | Absolute value and distance | Distance from zero, absolute value in context, distance between two numbers | MUST
# Absolute value and distance

## 1. What it covers
- **Absolute value** $\lvert a\rvert$: the distance from 0.
- Absolute value in context (size without direction).
- **Distance between two numbers**: $\lvert a - b\rvert$.

## 2. Where it is used
- **Error measurement:** how far off a prediction is, ignoring direction. Machine learning's *mean absolute error* averages $\lvert \text{predicted} - \text{actual}\rvert$.
- **Programming:** `abs()` for tolerances: `abs(a - b) < 1e-9` is how floats are compared safely.
- **Finance:** the size of a loss or gain.
- **Geometry:** distances on a grid ("Manhattan distance" $\lvert x_1 - x_2\rvert + \lvert y_1 - y_2\rvert$, used in games and pathfinding).

## 3. What it means
$\lvert a\rvert$ is **how far** $a$ is from 0, whatever the direction. Distance is never negative: $\lvert 5\rvert = 5$ and $\lvert -5\rvert = 5$.

The distance between two numbers on the line is the absolute value of their difference. It doesn't matter which you subtract from which: $\lvert 3 - 8\rvert = \lvert 8 - 3\rvert = 5$.

Think of a taxi fare: it depends on how far you go, not which direction.

## 4. Formula & worked example
$$
\lvert a\rvert = \begin{cases} a & a \ge 0 \\ -a & a < 0 \end{cases} \qquad \text{distance}(a, b) = \lvert a - b\rvert
$$

**Example 1.** $\lvert -13\rvert = 13$, $\lvert 0\rvert = 0$, $\lvert 2.7\rvert = 2.7$

**Example 2.** $-\lvert -4\rvert$
- $\lvert -4\rvert = 4$, then the outside minus: $-4$

**Example 3.** Distance between $-6$ and $9$.
- $\lvert -6 - 9\rvert = \lvert -15\rvert = 15$

**Example 4.** A city's coldest temperature was $-12^\circ$C and its hottest $38^\circ$C. What is the range?
- $\lvert 38 - (-12)\rvert = 50^\circ$C

**Example 5.** Which numbers have $\lvert x\rvert = 7$?
- $x = 7$ or $x = -7$.

## 5. Common mistakes
- Thinking absolute value "removes the minus sign" from expressions: $\lvert 3 - 8\rvert = \lvert -5\rvert = 5$, not $\lvert 3 + 8\rvert$. Calculate inside first.
- Writing $-\lvert 4\rvert = 4$. The minus is outside: $-4$.
- Forgetting the second solution when solving $\lvert x\rvert = k$.

## 6. Practice
Q: $\lvert -0.75\rvert$?
A: $0.75$.

Q: Distance between $-3$ and $-11$?
A: $8$.

Q: $\lvert 5 - 12\rvert - \lvert -2\rvert$?
A: $7 - 2 = 5$.

Q: Solve $\lvert x\rvert = 4.5$.
A: $x = 4.5$ or $x = -4.5$.

Q: A forecast says 30°C; the actual is 27°C. Absolute error?
A: $\lvert 30 - 27\rvert = 3^\circ$C.

@@ 3 | TOPIC | Comparing and ordering rational numbers | Ordering negatives, fractions and decimals; inequality symbols | MUST
# Comparing and ordering rational numbers

## 1. What it covers
- Using $<$, $>$, $\le$, $\ge$.
- Comparing negative numbers.
- Ordering mixed lists of fractions, decimals and negatives.

## 2. Where it is used
- **Sorting data:** spreadsheets and databases sort numbers ascending or descending; `ORDER BY balance` puts the biggest debts first.
- **Finance:** comparing returns (a loss of $-2.5\%$ is worse than $-1.8\%$).
- **Programming:** every `if a < b` comparison; sorting algorithms.
- **Inequalities in algebra.**

## 3. What it means
On a number line, **further right means bigger**. So:
- Any positive number > 0 > any negative number.
- Among negatives, the one **closer to 0** is bigger: $-2 > -9$. Owing 200 taka is better than owing 900.

**To order a mixed list:** convert everything to decimals (or a common denominator), then place them on a number line.

Symbols: the open side of $<$ faces the bigger number (like a hungry mouth eating the bigger meal).

## 4. Formula & worked example
$$
a < b \iff b - a > 0 \qquad a < b \iff -a > -b
$$

**Example 1.** Compare $-\frac{3}{4}$ and $-\frac{2}{3}$.
- Decimals: $-0.75$ and $-0.667$. $-0.667$ is closer to 0 → $-\frac{2}{3} > -\frac{3}{4}$

**Example 2.** Order from least to greatest: $0.4, -1.2, -\frac{5}{4}, \frac{1}{3}, -0.5$
- Decimals: $0.4, -1.2, -1.25, 0.333, -0.5$
- Order: $-\frac{5}{4}, -1.2, -0.5, \frac{1}{3}, 0.4$

**Example 3.** True or false: $-7 \ge -7$?
- True ($\ge$ allows equality).

**Example 4.** Which statement is true: $-3 < -5$ or $-3 > -5$?
- $-3 > -5$ ($-3$ is to the right).

## 5. Common mistakes
- Ordering negatives by their digits ($-5 < -3$ is right; $-3 < -5$ is wrong).
- Comparing fractions by their denominators only.
- Mixing up $<$ and $>$.

## 6. Practice
Q: Which is greater: $-0.6$ or $-\frac{2}{3}$?
A: $-0.6$ (since $-\frac{2}{3} \approx -0.667$).

Q: Order: $-2, 1.5, -2.5, 0, -\frac{1}{2}$.
A: $-2.5, -2, -\frac{1}{2}, 0, 1.5$.

Q: Fill in $<$ or $>$: $-\frac{1}{8} \ ? \ -\frac{1}{10}$.
A: $<$ ($-0.125 < -0.1$).

Q: If $a < b$, how do $-a$ and $-b$ compare?
A: $-a > -b$ (taking opposites flips the order).

Q: Which is the bigger loss: $-3.2\%$ or $-2.9\%$?
A: $-3.2\%$ (it is the smaller number).

@@ 3 | TOPIC | Adding and subtracting integers | Number-line and money models, subtracting as adding the opposite, long chains | MUST
# Adding and subtracting integers

## 1. What it covers
- Adding positive and negative integers.
- Subtraction as **adding the opposite**.
- Chains like $-4 + 9 - (-6) - 12$.

## 2. Where it is used
- **Bank balances:** deposits (+) and withdrawals (−).
- **Temperature changes**, elevation changes, golf scores (relative to par).
- **Game scores** with penalties.
- **Algebra:** combining like terms with signed coefficients.

## 3. What it means
**Number-line model:** start at the first number. Adding a positive moves right; adding a negative moves left.

**Money model:** positive = cash in hand, negative = debt. $-5 + 3$: you owe 5, someone gives you 3, now you owe 2 → $-2$.

**Rules for adding:**
- **Same signs:** add the sizes, keep the sign. $-4 + (-6) = -10$.
- **Different signs:** subtract the sizes, take the sign of the bigger size. $-9 + 4 = -5$.

**Subtracting:** taking away a debt is the same as receiving money. So $a - b = a + (-b)$, and in particular **subtracting a negative = adding a positive**: $5 - (-3) = 5 + 3 = 8$.

## 4. Formula & worked example
$$
a - b = a + (-b) \qquad a - (-b) = a + b
$$

**Example 1.** $-7 + 12 = 5$ (different signs: $12 - 7 = 5$, the bigger size is positive)

**Example 2.** $-8 + (-5) = -13$

**Example 3.** $3 - 10 = 3 + (-10) = -7$

**Example 4.** $-6 - (-14) = -6 + 14 = 8$

**Example 5 (chain).** $-4 + 9 - (-6) - 12$
- Rewrite: $-4 + 9 + 6 - 12$
- Group positives and negatives: $(9 + 6) + (-4 - 12) = 15 - 16 = -1$

**Example 6.** A lift is at floor $-2$ (basement), goes up 7, then down 9. Where is it?
- $-2 + 7 - 9 = -4$

## 5. Common mistakes
- $-3 - 5 = 2$ or $-2$. It is $-8$ (moving further left).
- $5 - (-3) = 2$. Two minus signs side by side become plus: $8$.
- Taking the sign of the first number instead of the bigger one: $-3 + 10 = 7$, not $-7$.

## 6. Practice
Q: $-15 + 9$?
A: $-6$.

Q: $-4 - 11$?
A: $-15$.

Q: $7 - (-8)$?
A: $15$.

Q: $-12 - (-12)$?
A: $0$.

Q: $10 - 15 + (-3) - (-9)$?
A: $10 - 15 - 3 + 9 = 1$.

@@ 3 | TOPIC | Multiplying and dividing integers | Sign rules and why they work; checking division by multiplication | MUST
# Multiplying and dividing integers

## 1. What it covers
- The sign rules for $\times$ and $\div$.
- **Why** negative × negative = positive.
- Many factors: counting negative signs.
- Powers of negative numbers.

## 2. Where it is used
- **Algebra:** expanding $-3(x - 4) = -3x + 12$ and solving equations.
- **Finance:** losing 500 taka a day for 4 days is $4 \times (-500) = -2000$.
- **Physics:** directions and charges (opposite charges multiply to a negative force: attraction).
- **Programming:** flipping a direction with `velocity *= -1` (bouncing off a wall).

## 3. What it means
**Sign rules:**
- Same signs → **positive**. Different signs → **negative**.
- The same rules hold for division.

**Why negative × negative is positive:** look at the pattern
$3 \times (-2) = -6$, $2 \times (-2) = -4$, $1 \times (-2) = -2$, $0 \times (-2) = 0$. Each step the answer goes **up** by 2. Continue: $(-1) \times (-2) = 2$, $(-2)(-2) = 4$.

Another view: multiplying by $-1$ means "turn around on the number line". Turning around twice faces you forward again.

**Many factors:** count the negative signs. Even count → positive; odd count → negative.

## 4. Formula & worked example
$$
(+)(+) = + \quad (-)(-) = + \quad (+)(-) = - \quad (-)(+) = -
$$

**Example 1.** $-6 \times 7 = -42$

**Example 2.** $-8 \times (-5) = 40$

**Example 3.** $-56 \div 8 = -7$; check: $8 \times (-7) = -56$ ✓

**Example 4.** $(-2)(3)(-4)(-1)$
- Three negatives (odd) → negative. Sizes: $2 \cdot 3 \cdot 4 \cdot 1 = 24$ → $-24$

**Example 5.** $(-1)^{100} = 1$ and $(-1)^{101} = -1$

**Example 6.** $\frac{-36}{-9} = 4$

## 5. Common mistakes
- Using the addition rule for multiplication ("take the sign of the bigger number").
- $-2^4 = 16$. Without brackets it's $-(2^4) = -16$; $(-2)^4 = 16$.
- Thinking $0 \div (-5)$ is undefined. It is $0$. Only division **by** zero is undefined.

## 6. Practice
Q: $-9 \times (-4)$?
A: $36$.

Q: $72 \div (-8)$?
A: $-9$.

Q: $(-3)^3$?
A: $-27$.

Q: $(-1)(-2)(-3)(-4)$?
A: $24$ (four negatives, even).

Q: Why is $(-1) \times (-1) = 1$?
A: Multiplying by $-1$ reverses direction; reversing twice returns to the original direction.

@@ 3 | TOPIC | Operations with negative fractions and decimals | All four operations on signed fractions and decimals | MUST
# Operations with negative fractions and decimals

## 1. What it covers
- Applying the integer sign rules to fractions and decimals.
- Where to put the minus sign in a fraction: $-\frac{a}{b} = \frac{-a}{b} = \frac{a}{-b}$.
- Multi-step calculations with signed rational numbers.

## 2. Where it is used
- **Finance:** daily percentage changes in stock prices ($-1.25\%$, $+0.8\%$).
- **Science:** temperature changes like $-2.5^\circ$C per hour.
- **Statistics:** deviations from the mean are negative and positive decimals.
- **Algebra:** solving equations with fractional coefficients.

## 3. What it means
Nothing new! The sign rules from integers apply exactly, and the fraction and decimal methods from Phase 1 apply exactly. Do two separate jobs:
1. Decide the **sign** with the integer rules.
2. Work out the **size** with the fraction or decimal method.

A negative fraction can show its minus sign in three equivalent places; the usual convention is in front: $-\frac{3}{4}$.

## 4. Formula & worked example
$$
-\frac{a}{b} = \frac{-a}{b} = \frac{a}{-b} \qquad \frac{-a}{-b} = \frac{a}{b}
$$

**Example 1.** $-\frac{2}{3} + \frac{1}{4}$
- $-\frac{8}{12} + \frac{3}{12} = -\frac{5}{12}$

**Example 2.** $-\frac{5}{6} - \frac{1}{4}$
- $-\frac{10}{12} - \frac{3}{12} = -\frac{13}{12}$

**Example 3.** $-\frac{3}{5} \times \left(-\frac{10}{9}\right)$
- Sign: positive. Size: $\frac{3}{5} \cdot \frac{10}{9} = \frac{2}{3}$ → $\frac{2}{3}$

**Example 4.** $\frac{7}{8} \div \left(-\frac{7}{4}\right)$
- Sign: negative. Size: $\frac{7}{8} \cdot \frac{4}{7} = \frac{1}{2}$ → $-\frac{1}{2}$

**Example 5.** $-3.6 + 1.25 - (-0.4)$
- $-3.6 + 1.25 + 0.4 = -1.95$

**Example 6.** $-0.25 \times 1.6 = -0.4$

## 5. Common mistakes
- Putting minus signs on both top and bottom by accident and then forgetting they cancel.
- Losing the sign during the "size" work. Decide the sign first and write it down.
- Treating $-\frac{1}{2} + \frac{1}{3}$ as $-\left(\frac{1}{2} + \frac{1}{3}\right)$.

## 6. Practice
Q: $-\frac{1}{2} + \frac{1}{3}$?
A: $-\frac{1}{6}$.

Q: $\frac{3}{4} - \left(-\frac{1}{8}\right)$?
A: $\frac{7}{8}$.

Q: $-\frac{4}{9} \times \frac{3}{8}$?
A: $-\frac{1}{6}$.

Q: $-2.4 \div (-0.6)$?
A: $4$.

Q: $-1.5 - 2.75$?
A: $-4.25$.

@@ 3 | TOPIC | Order of operations with negatives | Exponents and signs, brackets, mixed expressions with signed numbers | MUST
# Order of operations with negatives

## 1. What it covers
- BODMAS with negative numbers.
- The difference between $-a^2$ and $(-a)^2$.
- Substituting negative values into expressions.

## 2. Where it is used
- **Programming:** `-x**2` in Python is $-(x^2)$; Excel's `=-2^2` gives $4$ (Excel applies the negation first!). Knowing the convention prevents bugs.
- **Algebra:** evaluating $x^2 - 3x$ at $x = -2$ is a classic exam trap.
- **Physics formulas** with negative values (deceleration, charges).

## 3. What it means
The order of operations doesn't change, but there's one trap: the **negative sign is not part of the number unless brackets say so**.
- $-3^2$ means "the opposite of $3^2$" $= -9$.
- $(-3)^2$ means "$-3$ times $-3$" $= 9$.

**When substituting a negative value, always put it in brackets.** For $x = -2$: $x^2 \to (-2)^2 = 4$.

## 4. Formula & worked example
$$
-a^2 = -(a^2) \qquad (-a)^2 = a^2 \qquad (-a)^3 = -a^3
$$

**Example 1.** $-4^2 + (-4)^2 = -16 + 16 = 0$

**Example 2.** Evaluate $x^2 - 3x$ at $x = -2$.
- $(-2)^2 - 3(-2) = 4 + 6 = 10$

**Example 3.** $-2(3 - 7)^2 \div 4$
- Brackets: $-4$ → power: $16$ → $-2 \times 16 = -32$ → $\div 4 = -8$

**Example 4.** $\dfrac{-6 - (-2)^3}{-1 - 1}$
- Top: $-6 - (-8) = 2$; bottom: $-2$ → $-1$

**Example 5.** $5 - 3 \times (-2)^2$
- $5 - 3 \times 4 = 5 - 12 = -7$

## 5. Common mistakes
- Substituting without brackets: $x^2$ at $x = -3$ written as $-3^2 = -9$.
- Treating $-3x$ at $x = -2$ as $-3 - 2$. It is $(-3)(-2) = 6$.
- Doing the subtraction before multiplication in $5 - 3 \times (-2)^2$.

## 6. Practice
Q: $-5^2$?
A: $-25$.

Q: $(-5)^2$?
A: $25$.

Q: Evaluate $2x^2 + x$ at $x = -3$.
A: $2(9) + (-3) = 15$.

Q: $-3(-2)^3 + 4$?
A: $-3(-8) + 4 = 28$.

Q: Evaluate $-x^2$ at $x = -4$.
A: $-(16) = -16$.

@@ 3 | TOPIC | Practice & review: Negative and rational numbers | Mixed problem set with worked answers | MUST
# Practice & review: Negative and rational numbers

**How to use this:** 40 minutes, no calculator.

## Problems
1. Order from least to greatest: $-1.5, \frac{2}{5}, -\frac{7}{4}, 0, -0.9$.
2. $-14 + 6 - (-9) - 20$.
3. $(-3)(-4)(-5) \div (-6)$.
4. $-\frac{3}{4} + \frac{5}{6} - \left(-\frac{1}{3}\right)$.
5. $-\frac{9}{10} \div \frac{3}{5} \times \left(-\frac{2}{3}\right)$.
6. Evaluate $3x^2 - 2x - 5$ at $x = -2$.
7. $\lvert -8 + 3\rvert - \lvert 4 - 10\rvert$.
8. At 6 am it was $-7^\circ$C. By noon it rose $15^\circ$, then fell $11^\circ$ by midnight. Midnight temperature?
9. A company's monthly profits were $+120$, $-45$, $-80$, $+30$ (thousand taka). Find the total and the mean.

## Answers
1. $-\frac{7}{4}, -1.5, -0.9, 0, \frac{2}{5}$.
2. $-14 + 6 + 9 - 20 = -19$.
3. $-60 \div (-6) = 10$.
4. $-\frac{9}{12} + \frac{10}{12} + \frac{4}{12} = \frac{5}{12}$.
5. $-\frac{9}{10} \cdot \frac{5}{3} = -\frac{3}{2}$; then $\times \left(-\frac{2}{3}\right) = 1$.
6. $3(4) + 4 - 5 = 11$.
7. $5 - 6 = -1$.
8. $-7 + 15 - 11 = -3^\circ$C.
9. Total $= 25$ thousand; mean $= 6.25$ thousand taka.

## Flashcards
Q: Sign rule for multiplying two numbers?
A: Same signs → positive; different signs → negative.

Q: $a - (-b) = ?$
A: $a + b$.

Q: $-3^2$ vs $(-3)^2$?
A: $-9$ vs $9$.

Q: What should you do when substituting a negative value?
A: Put it in brackets.
