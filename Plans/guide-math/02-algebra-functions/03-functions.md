@@ 1 | MODULE | Phase 3 — Functions (Core) | Function definition and notation, domain and range, reading graphs, rate of change, transformations, absolute value, piecewise, composition, inverses | MUST
# Phase 3: Functions

A **function** is a rule that turns each input into exactly one output. It's the single most important idea in mathematics after numbers: calculus studies how functions change, statistics fits functions to data, and programming is built from functions.

This phase gives you the general toolkit (notation, domain, graphs, transformations, combining and inverting functions) that you'll reuse for every family: polynomials, exponentials, logarithms, rational and trig functions.

@@ 2 | MODULE | Function fundamentals | Definition, notation, domain and range, graph features, rate of change | MUST
Think of a function as a machine: input goes in, one output comes out.

@@ 3 | TOPIC | What is a function: definition, notation and the vertical line test | Relations vs functions, f(x) notation, evaluating, mapping diagrams | MUST
# What is a function: definition, notation and the vertical line test

## 1. What it covers
- **Relations** vs **functions**: each input has exactly **one** output.
- Function notation $f(x)$ and evaluating it.
- Recognising functions in tables, mappings, equations and graphs (the **vertical line test**).

## 2. Where it is used
- **Programming:** `def f(x): return 2*x + 3` is a function; a pure function always gives the same output for the same input.
- **Databases:** a primary key determines one row (a functional dependency).
- **Spreadsheets:** each formula cell is a function of other cells.
- **Science:** "temperature as a function of time", $T(t)$.

## 3. What it means
A function is like a **vending machine**: press a button (input) and you get exactly one item (output). The same button always gives the same item. Two buttons may give the same item (that's fine), but one button can never give two different items.

**Notation:** $f(x)$ is read "f of x" and means "the output of $f$ when the input is $x$". It is **not** $f \times x$. $f(3)$ means "put 3 in".

**Vertical line test:** a graph is a function if no vertical line crosses it more than once. Two crossings would mean one $x$ with two $y$ values.

## 4. Formula & worked example
**Example 1.** $f(x) = 3x^2 - 2x$. Find $f(2)$, $f(-1)$ and $f(a + 1)$.
- $f(2) = 12 - 4 = 8$
- $f(-1) = 3 + 2 = 5$
- $f(a + 1) = 3(a + 1)^2 - 2(a + 1) = 3a^2 + 6a + 3 - 2a - 2 = 3a^2 + 4a + 1$

**Example 2.** Is $\{(1, 4), (2, 5), (3, 4)\}$ a function? Yes: each input appears once (repeat outputs are fine).

**Example 3.** Is $\{(2, 1), (2, 3), (4, 5)\}$ a function? No: the input 2 gives two outputs.

**Example 4.** Is $x^2 + y^2 = 25$ (a circle) a function of $x$? No: at $x = 0$, $y = 5$ and $y = -5$. A vertical line crosses it twice.

**Example 5.** If $f(x) = 2x + 3$, solve $f(x) = 11$.
- $2x + 3 = 11$ → $x = 4$. (Here the output is given and you find the input.)

## 5. Common mistakes
- Reading $f(x)$ as multiplication.
- Thinking repeated outputs break the rule. Only repeated **inputs with different outputs** do.
- Confusing "find $f(4)$" (input 4) with "solve $f(x) = 4$" (output 4).

## 6. Practice
Q: $g(x) = 5 - x^2$. Find $g(-3)$.
A: $5 - 9 = -4$.

Q: Is $y = x^2$ a function? Is $x = y^2$?
A: $y = x^2$ yes; $x = y^2$ no (e.g. $x = 4$ gives $y = \pm 2$).

Q: $h(t) = 4t - 1$. Solve $h(t) = 19$.
A: $t = 5$.

Q: What does the vertical line test check?
A: That no input has more than one output.

Q: $f(x) = x^2 + 1$. Find $f(x + h)$.
A: $x^2 + 2xh + h^2 + 1$.

@@ 3 | TOPIC | Domain and range | Allowed inputs and possible outputs from equations, graphs, tables and context | MUST
# Domain and range

## 1. What it covers
- **Domain:** all allowed inputs. **Range:** all possible outputs.
- Finding the domain from an equation: avoid division by zero and square roots of negatives.
- Reading domain and range from a graph.
- Domain in real contexts.

## 2. Where it is used
- **Programming:** input validation; `math.sqrt(-1)` raises an error and `1/0` crashes, because the input is outside the domain.
- **APIs and databases:** allowed values for a parameter or column (a `CHECK` constraint).
- **Modelling:** time can't be negative; a number of people must be a whole number.
- **Calculus:** functions are only analysed on their domain.

## 3. What it means
The **domain** is the set of inputs the function can accept: the buttons that work on the vending machine. The **range** is the set of outputs that actually come out.

**Finding the domain of a formula:** start with all real numbers and remove:
- values that make a **denominator zero**;
- values that make the expression **under an even root negative**;
- (later) values that make the input of a **logarithm** zero or negative.

**From a graph:** domain = how far the graph extends left–right; range = how far it extends up–down.

## 4. Formula & worked example
**Example 1.** $f(x) = \frac{5}{x - 3}$
- Denominator $\ne 0$ → $x \ne 3$. Domain: $(-\infty, 3) \cup (3, \infty)$.

**Example 2.** $g(x) = \sqrt{2x - 8}$
- $2x - 8 \ge 0$ → $x \ge 4$. Domain $[4, \infty)$; range $[0, \infty)$.

**Example 3.** $h(x) = x^2 + 1$
- Domain all reals; since $x^2 \ge 0$, range $[1, \infty)$.

**Example 4.** $k(x) = \frac{\sqrt{x}}{x - 4}$
- Need $x \ge 0$ and $x \ne 4$ → $[0, 4) \cup (4, \infty)$.

**Example 5 (context).** A ticket booth sells at most 300 tickets at 200 taka: $R(n) = 200n$.
- Domain: whole numbers $0, 1, \ldots, 300$. Range: $0, 200, \ldots, 60\,000$.

## 5. Common mistakes
- Forgetting the root condition includes 0 ($\sqrt{0}$ is fine; $\ge$, not $>$).
- Giving the range as the set of $x$ values.
- Ignoring real-world limits (negative time, fractional people).

## 6. Practice
Q: Domain of $f(x) = \frac{1}{x + 7}$?
A: All reals except $-7$.

Q: Domain of $\sqrt{5 - x}$?
A: $(-\infty, 5]$.

Q: Range of $f(x) = -x^2$?
A: $(-\infty, 0]$.

Q: Domain of $\frac{x}{x^2 - 9}$?
A: All reals except $\pm 3$.

Q: Domain of $A(r) = \pi r^2$ for the area of a real circle?
A: $r > 0$ (or $r \ge 0$).

@@ 3 | TOPIC | Reading and interpreting function graphs | Intercepts, increasing/decreasing intervals, maximum and minimum, positive/negative regions, context | MUST
# Reading and interpreting function graphs

## 1. What it covers
- **Key features:** intercepts, where a function **increases** or **decreases**, **maximum** and **minimum** points, where it is positive or negative.
- Describing these in interval notation.
- Interpreting the features in a real situation.

## 2. Where it is used
- **Dashboards and analytics:** "traffic peaked at 8 pm", "sales fell from March to May".
- **Engineering:** reading sensor graphs.
- **Finance:** stock charts (highs, lows, trends).
- **Calculus:** derivatives make these features precise.

## 3. What it means
Read a graph from **left to right**, like text:
- **Increasing** on an interval: the graph goes up as $x$ increases.
- **Decreasing:** it goes down.
- **Relative (local) maximum:** a peak, higher than nearby points. **Relative minimum:** a valley.
- **Absolute max/min:** the highest or lowest point overall.
- **Zeros (x-intercepts):** where the output is 0.
- **Positive** where the graph is above the x-axis; **negative** below.

Intervals of increase/decrease are given in terms of $x$ (the inputs), not $y$.

## 4. Formula & worked example
Consider $f(x) = x^3 - 3x$ (sketch it with a few points: $f(-2) = -2$, $f(-1) = 2$, $f(0) = 0$, $f(1) = -2$, $f(2) = 2$).

**Example 1.** Zeros: $x^3 - 3x = x(x^2 - 3) = 0$ → $x = 0$, $\pm\sqrt{3} \approx \pm 1.73$.

**Example 2.** Increasing on $(-\infty, -1)$ and $(1, \infty)$; decreasing on $(-1, 1)$.

**Example 3.** Relative maximum $2$ at $x = -1$; relative minimum $-2$ at $x = 1$. No absolute max or min (the graph goes to $\pm\infty$).

**Example 4.** Positive on $(-\sqrt{3}, 0) \cup (\sqrt{3}, \infty)$.

**Example 5 (context).** A ball's height is $h(t) = -5t^2 + 20t$ (metres, seconds).
- y-intercept 0 (thrown from ground level); zeros $t = 0$ and $t = 4$ (lands after 4 s); maximum at $t = 2$, $h = 20$ m; increasing on $(0, 2)$ (rising), decreasing on $(2, 4)$ (falling).

## 5. Common mistakes
- Giving increasing intervals in $y$ values.
- Including the turning point inside an "increasing" interval with square brackets carelessly (use open intervals around turning points).
- Confusing a local maximum with the absolute maximum.

## 6. Practice
Q: Where is $f(x) = (x - 2)^2$ decreasing?
A: $(-\infty, 2)$.

Q: What is a zero of a function?
A: An input where the output is 0 (an x-intercept).

Q: $h(t) = -5t^2 + 30t$. Maximum height and when?
A: At $t = 3$, $h = 45$ m.

Q: Where is $f(x) = x^2 - 4$ negative?
A: $(-2, 2)$.

Q: A graph rises until $x = 5$ and then falls. What is at $x = 5$?
A: A (relative) maximum.

@@ 3 | TOPIC | Average rate of change and comparing functions | Slope between two points on any function; comparing functions given in different forms | MUST
# Average rate of change and comparing functions

## 1. What it covers
- The **average rate of change** of any function over an interval.
- Its meaning as the slope of a **secant line**.
- Comparing functions given as a table, a graph and an equation.

## 2. Where it is used
- **Business:** average monthly growth between January and June.
- **Physics:** average velocity = change in position ÷ change in time.
- **Calculus:** the derivative is the limit of the average rate of change as the interval shrinks.
- **Performance analysis:** throughput between two timestamps.

## 3. What it means
For a straight line the rate of change is constant (the slope). For a curve it varies, but we can still ask for the **average** over an interval $[a, b]$: draw the straight line (the **secant**) joining $(a, f(a))$ and $(b, f(b))$ and take its slope.

It's like the average speed of a trip: you may have sped up and slowed down, but the total distance ÷ total time gives one summary number.

**Comparing functions:** convert the information to the same feature (intercept, rate, value at a point) and compare.

## 4. Formula & worked example
$$
\text{average rate of change on } [a, b] = \frac{f(b) - f(a)}{b - a}
$$

**Example 1.** $f(x) = x^2$ on $[1, 4]$.
- $\frac{16 - 1}{4 - 1} = 5$

**Example 2.** Same function on $[1, 2]$: $\frac{4 - 1}{1} = 3$. On $[3, 4]$: $\frac{16 - 9}{1} = 7$. The curve gets steeper.

**Example 3 (data).** Users: 12 000 in January, 20 400 in July.
- $\frac{20\,400 - 12\,000}{6} = 1400$ new users per month on average.

**Example 4 (position).** $s(t) = 4.9t^2$ metres. Average velocity from $t = 1$ to $t = 3$?
- $\frac{44.1 - 4.9}{2} = 19.6$ m/s

**Example 5 (compare).** $f(x) = 3x + 2$ vs $g$ given by the table $x$: 0, 2, 4 and $g$: 1, 8, 15. Which grows faster?
- $f$ has rate 3; $g$ has rate $\frac{15 - 1}{4} = 3.5$ → $g$ grows faster (and starts lower: 1 vs 2).

## 5. Common mistakes
- Putting the $x$ difference on top.
- Computing $f(b) - f(a)$ with the values in the wrong order.
- Thinking the average rate tells you the rate at every point in the interval.

## 6. Practice
Q: Average rate of change of $f(x) = x^3$ on $[0, 2]$?
A: $\frac{8 - 0}{2} = 4$.

Q: $f(x) = \frac{1}{x}$ on $[1, 4]$?
A: $\frac{0.25 - 1}{3} = -0.25$.

Q: Population 50 000 in 2010, 62 000 in 2020. Average yearly change?
A: $1200$ per year.

Q: What line has slope equal to the average rate of change?
A: The secant line through the two endpoints.

Q: Which has a bigger y-intercept: $f(x) = 2x - 3$ or the line through $(1, 1)$ and $(3, 7)$?
A: The line is $y = 3x - 2$; intercept $-2 > -3$, so the line.

@@ 2 | MODULE | Transforming and combining functions | Shifts, reflections, stretches, symmetry, absolute value, piecewise, composition, inverses | MUST
Learn the graph of a few parent functions and you can draw thousands of others by moving, flipping and stretching them.

@@ 3 | TOPIC | Transformations: shifts | Vertical shifts f(x) + k and horizontal shifts f(x − h) | SHOULD
# Transformations: shifts

## 1. What it covers
- **Vertical shifts:** $f(x) + k$ moves the graph up by $k$ (down if $k < 0$).
- **Horizontal shifts:** $f(x - h)$ moves the graph **right** by $h$.
- Parent functions to shift: $x^2$, $\lvert x\rvert$, $\sqrt{x}$, $x^3$.

## 2. Where it is used
- **Graphics and animation:** translating an object by $(h, k)$ means $x \to x - h$, $y \to y + k$.
- **Signal processing:** a time delay is a horizontal shift $f(t - d)$.
- **Modelling:** adjusting a curve to match data (e.g. a parabola with its peak at a given time).
- **Trigonometry:** phase shifts of waves are horizontal shifts.

## 3. What it means
- **Outside change** ($f(x) + k$): changes outputs → moves **vertically**, in the direction you expect.
- **Inside change** ($f(x - h)$): changes inputs → moves **horizontally**, in the direction that seems "backwards".

Why backwards? $g(x) = f(x - 3)$ reaches the value $f(0)$ when $x - 3 = 0$, i.e. at $x = 3$. Everything happens 3 units **later** (to the right). It's like a TV show broadcast with a 3-hour delay: each scene appears 3 hours later than the original.

## 4. Formula & worked example
$$
y = f(x - h) + k \quad \text{moves } f \text{ right } h \text{ and up } k
$$

**Example 1.** $y = x^2 + 3$: the parabola moves up 3; vertex $(0, 3)$.

**Example 2.** $y = (x - 4)^2$: moves right 4; vertex $(4, 0)$.

**Example 3.** $y = (x + 2)^2 - 5$: left 2, down 5; vertex $(-2, -5)$.

**Example 4.** $y = \sqrt{x - 1} + 2$: starting point moves from $(0, 0)$ to $(1, 2)$.

**Example 5.** Write the equation of $y = \lvert x\rvert$ shifted right 3 and down 1.
- $y = \lvert x - 3\rvert - 1$

## 5. Common mistakes
- Moving $f(x + 2)$ to the right. It moves **left**.
- Applying a vertical shift inside the function: $f(x) + 3 \ne f(x + 3)$.
- Forgetting that the domain shifts too (e.g. $\sqrt{x - 1}$ starts at $x = 1$).

## 6. Practice
Q: Vertex of $y = (x - 5)^2 + 1$?
A: $(5, 1)$.

Q: Describe $y = \lvert x + 4\rvert$ compared with $\lvert x\rvert$.
A: Shifted left 4.

Q: Write $x^3$ shifted up 2 and right 1.
A: $y = (x - 1)^3 + 2$.

Q: Domain of $y = \sqrt{x + 3}$?
A: $[-3, \infty)$.

Q: Which way does $f(t - 10)$ shift a signal?
A: Right (a delay of 10).

@@ 3 | TOPIC | Reflections, stretches and combined transformations | −f(x), f(−x), a·f(x), f(bx), and the order of transformations | SHOULD
# Reflections, stretches and combined transformations

## 1. What it covers
- **Reflections:** $-f(x)$ (across the x-axis) and $f(-x)$ (across the y-axis).
- **Vertical stretch/compression:** $a \cdot f(x)$.
- **Horizontal stretch/compression:** $f(bx)$.
- Combining all transformations in the right order.

## 2. Where it is used
- **Graphics:** scaling and mirroring images (CSS `transform: scale(-1, 1)` flips horizontally).
- **Audio:** amplifying a signal (vertical stretch) or speeding it up (horizontal compression).
- **Modelling:** fitting a parent shape to data by stretching and moving it.
- **Every function family** (quadratic vertex form, sinusoid form) uses this framework.

## 3. What it means
- $-f(x)$: every output changes sign → **flip upside down** (across the x-axis).
- $f(-x)$: inputs are mirrored → **flip left–right** (across the y-axis).
- $a \cdot f(x)$ with $\lvert a\rvert > 1$: outputs get bigger → **taller** (vertical stretch). $0 < \lvert a\rvert < 1$: shorter.
- $f(bx)$ with $\lvert b\rvert > 1$: the graph happens **faster**, so it's **squeezed** horizontally by a factor $\frac{1}{b}$. With $0 < \lvert b\rvert < 1$ it's stretched. (Inside changes act "backwards" again.)

**Order for $y = a\,f(b(x - h)) + k$:** horizontal changes (stretch by $\frac{1}{b}$, then shift $h$), then vertical changes (stretch by $a$ and reflect, then shift $k$). A safe way: transform a few key points: $(x, y) \to \left(\frac{x}{b} + h,\ ay + k\right)$.

## 4. Formula & worked example
$$
y = a\,f\big(b(x - h)\big) + k: \qquad (x, y) \mapsto \left(\frac{x}{b} + h,\ ay + k\right)
$$

**Example 1.** $y = -x^2$: the parabola opens downward.

**Example 2.** $y = \sqrt{-x}$: the square-root curve goes to the left; domain $x \le 0$.

**Example 3.** $y = 3\lvert x\rvert$: a narrower V (steeper sides, slope ±3). $y = \frac{1}{2}\lvert x\rvert$: wider.

**Example 4.** $f(x) = x^2$ and $g(x) = f(2x) = 4x^2$. Point $(2, 4)$ on $f$ moves to $(1, 4)$ on $g$: squeezed horizontally by half.

**Example 5 (combined).** $y = -2(x - 1)^2 + 3$ from $y = x^2$.
- Right 1, stretched vertically ×2, reflected (opens down), up 3. Vertex $(1, 3)$. Point $(1, 1)$ on $x^2$ → $(2, -2 + 3) = (2, 1)$.

## 5. Common mistakes
- Mixing up $-f(x)$ and $f(-x)$.
- Thinking $f(2x)$ stretches the graph wider. It **squeezes** it.
- Doing the shift before the stretch for vertical transformations (stretch first, then shift).

## 6. Practice
Q: Effect of $y = -f(x)$?
A: Reflection across the x-axis.

Q: Effect of $y = f(3x)$?
A: Horizontal compression by a factor of $\frac{1}{3}$.

Q: Describe $y = \frac{1}{2}(x + 4)^2 - 1$.
A: Left 4, vertical compression by $\frac{1}{2}$, down 1; vertex $(-4, -1)$.

Q: The point $(3, 5)$ is on $f$. Where is it on $y = 2f(x) - 1$?
A: $(3, 9)$.

Q: Equation: $\sqrt{x}$ reflected across the y-axis and shifted up 2?
A: $y = \sqrt{-x} + 2$.

@@ 3 | TOPIC | Even and odd functions | Symmetry about the y-axis and the origin; algebraic tests | SHOULD
# Even and odd functions

## 1. What it covers
- **Even** functions: $f(-x) = f(x)$, symmetric about the y-axis.
- **Odd** functions: $f(-x) = -f(x)$, symmetric about the origin.
- Testing algebraically and from graphs; most functions are neither.

## 2. Where it is used
- **Signal processing:** any signal splits into even and odd parts; the Fourier cosine series uses even functions, the sine series odd ones.
- **Calculus:** the integral of an odd function over $[-a, a]$ is 0, which saves a lot of work.
- **Physics:** symmetry arguments simplify problems.
- **Graphing faster:** draw half and mirror it.

## 3. What it means
- **Even:** the graph is a **mirror image** across the y-axis, like a butterfly. Plugging in $-x$ gives the same output. Examples: $x^2$, $x^4$, $\lvert x\rvert$, $\cos x$.
- **Odd:** turning the graph **180°** around the origin leaves it unchanged. Plugging in $-x$ flips the sign of the output. Examples: $x$, $x^3$, $\frac{1}{x}$, $\sin x$.

The names come from powers: even powers of $x$ give even functions; odd powers give odd functions.

**Test:** compute $f(-x)$ and simplify. If it equals $f(x)$ → even; if it equals $-f(x)$ → odd; otherwise neither.

## 4. Formula & worked example
$$
\text{even: } f(-x) = f(x) \qquad \text{odd: } f(-x) = -f(x)
$$

**Example 1.** $f(x) = x^4 - 3x^2 + 1$
- $f(-x) = x^4 - 3x^2 + 1 = f(x)$ → even.

**Example 2.** $g(x) = 2x^3 - x$
- $g(-x) = -2x^3 + x = -(2x^3 - x)$ → odd.

**Example 3.** $h(x) = x^2 + x$
- $h(-x) = x^2 - x$: neither $h(x)$ nor $-h(x)$ → neither.

**Example 4.** $k(x) = \frac{x}{x^2 + 1}$
- $k(-x) = \frac{-x}{x^2 + 1} = -k(x)$ → odd.

**Example 5.** An odd function has $f(3) = 7$. Find $f(-3)$. → $-7$.

## 5. Common mistakes
- Assuming every function is either even or odd.
- Testing with a single number and concluding (one example can disprove, but not prove).
- Thinking a constant term keeps a function odd: $x^3 + 1$ is **neither**.

## 6. Practice
Q: Is $f(x) = 5x^2 - 7$ even, odd or neither?
A: Even.

Q: Is $f(x) = x^5 + 2x$ even, odd or neither?
A: Odd.

Q: Is $f(x) = (x - 1)^2$ even, odd or neither?
A: Neither.

Q: An even function has $f(-4) = 10$. Find $f(4)$.
A: $10$.

Q: What symmetry does an odd function's graph have?
A: Rotational symmetry of 180° about the origin.

@@ 3 | TOPIC | Absolute value equations, inequalities and graphs | Two-case equations, compound inequalities, V-shaped graphs | SHOULD
# Absolute value equations, inequalities and graphs

## 1. What it covers
- Solving $\lvert ax + b\rvert = c$ (two cases).
- Solving $\lvert ax + b\rvert < c$ ("and", between) and $\lvert ax + b\rvert > c$ ("or", outside).
- Graphing $y = a\lvert x - h\rvert + k$: vertex and slopes.

## 2. Where it is used
- **Tolerances:** a bolt must be within 0.05 mm of 10 mm: $\lvert d - 10\rvert \le 0.05$.
- **Error and distance:** $\lvert \text{predicted} - \text{actual}\rvert$ (mean absolute error in ML).
- **Polling:** "45% ± 3%" means $\lvert p - 45\rvert \le 3$.
- **Code:** `abs(a - b) < epsilon` for comparing floats.

## 3. What it means
$\lvert x\rvert$ is the **distance** from 0. So:
- $\lvert x - 5\rvert = 2$: the distance from $x$ to 5 is 2 → $x = 3$ or $x = 7$.
- $\lvert x - 5\rvert < 2$: within 2 of 5 → $3 < x < 7$ (between).
- $\lvert x - 5\rvert > 2$: more than 2 away → $x < 3$ or $x > 7$ (outside).

Memory aid: **less than → "less" = between (and)**; **greater than → "great" = outside (or)**.

**Isolate the absolute value first**, then split. If it equals a negative number, there's no solution.

**Graph:** $y = \lvert x\rvert$ is a V with vertex $(0, 0)$ and slopes ±1. $y = a\lvert x - h\rvert + k$ has vertex $(h, k)$ and slopes $\pm a$.

## 4. Formula & worked example
$$
\lvert u\rvert = c \Rightarrow u = \pm c \qquad \lvert u\rvert < c \Rightarrow -c < u < c \qquad \lvert u\rvert > c \Rightarrow u < -c \text{ or } u > c
$$

**Example 1.** $\lvert 2x - 3\rvert = 7$
- $2x - 3 = 7$ → $x = 5$; $2x - 3 = -7$ → $x = -2$

**Example 2.** $3\lvert x + 1\rvert - 4 = 8$
- Isolate: $\lvert x + 1\rvert = 4$ → $x = 3$ or $x = -5$

**Example 3.** $\lvert x - 4\rvert \le 3$ → $-3 \le x - 4 \le 3$ → $1 \le x \le 7$

**Example 4.** $\lvert 2x + 1\rvert > 5$ → $2x + 1 < -5$ or $2x + 1 > 5$ → $x < -3$ or $x > 2$

**Example 5.** $\lvert x + 2\rvert = -3$ → no solution (distance can't be negative).

**Example 6 (graph).** $y = -2\lvert x - 1\rvert + 4$: vertex $(1, 4)$, opens down, slopes ∓2; x-intercepts where $\lvert x - 1\rvert = 2$: $x = -1, 3$.

## 5. Common mistakes
- Splitting before isolating: $3\lvert x + 1\rvert - 4 = 8$ → $3(x + 1) - 4 = \pm 8$ ✗.
- Using "and" for $>$ problems.
- Giving solutions when $\lvert u\rvert$ is set equal to a negative number.

## 6. Practice
Q: Solve $\lvert x + 6\rvert = 2$.
A: $x = -4$ or $x = -8$.

Q: Solve $\lvert 3x\rvert < 12$.
A: $-4 < x < 4$.

Q: Solve $\lvert x - 1\rvert \ge 5$.
A: $x \le -4$ or $x \ge 6$.

Q: Vertex of $y = \lvert x + 3\rvert - 2$?
A: $(-3, -2)$.

Q: A part must be $25 \pm 0.1$ mm. Write as an absolute value inequality.
A: $\lvert L - 25\rvert \le 0.1$.

@@ 3 | TOPIC | Piecewise functions | Evaluating, graphing and writing functions defined in pieces | SHOULD
# Piecewise functions

## 1. What it covers
- Functions defined by **different formulas on different intervals**.
- Evaluating (choosing the right piece).
- Graphing with open and closed endpoints.
- Writing piecewise models (taxes, shipping, tariffs).

## 2. Where it is used
- **Taxes:** income tax slabs (0% up to a threshold, then 5%, 10%, ...).
- **Utility bills:** electricity tariffs rise per unit in blocks.
- **Shipping costs** by weight bracket.
- **Programming:** `if / elif / else` chains are piecewise functions.
- **Machine learning:** the ReLU activation $\max(0, x)$ is piecewise linear.

## 3. What it means
A piecewise function is like a set of rules with conditions: "if the input is in this range, use this formula; otherwise use that one". To evaluate, **first find which interval** the input belongs to, then use only that piece.

On a graph, each piece is drawn only over its interval. A **closed dot** marks an included endpoint ($\le$, $\ge$); an **open dot** an excluded one ($<$, $>$).

## 4. Formula & worked example
$$
f(x) = \begin{cases} x^2 & x < 0 \\ 2x + 1 & 0 \le x \le 3 \\ 10 & x > 3 \end{cases}
$$

**Example 1.** Evaluate $f(-2)$, $f(0)$, $f(3)$ and $f(5)$.
- $f(-2) = 4$ (first piece); $f(0) = 1$; $f(3) = 7$; $f(5) = 10$

**Example 2.** $\lvert x\rvert$ as a piecewise function:
$$
\lvert x\rvert = \begin{cases} -x & x < 0 \\ x & x \ge 0 \end{cases}
$$

**Example 3 (electricity, simplified).** First 75 units at 5 taka each; units above 75 at 7 taka each. Write $C(u)$ and find $C(120)$.
$$
C(u) = \begin{cases} 5u & 0 \le u \le 75 \\ 375 + 7(u - 75) & u > 75 \end{cases}
$$
- $C(120) = 375 + 7(45) = 690$ taka

**Example 4 (code).**
```python
def shipping(kg):
    if kg <= 1:
        return 60
    elif kg <= 5:
        return 60 + 20 * (kg - 1)
    else:
        return 140 + 15 * (kg - 5)
print(shipping(3), shipping(8))   # 100 185
```
Each `if` branch is one piece. The conditions are checked top to bottom, so a 3 kg parcel uses the second piece: $60 + 20(2) = 100$.

## 5. Common mistakes
- Using the wrong piece at a boundary (check $\le$ vs $<$).
- Applying the higher rate to **all** units in a block tariff instead of only those above the threshold.
- Drawing each piece across the whole graph.

## 6. Practice
Q: Using $f$ above, find $f(-0.5)$.
A: $0.25$.

Q: Write ReLU as a piecewise function.
A: $f(x) = 0$ for $x < 0$; $f(x) = x$ for $x \ge 0$.

Q: Using $C(u)$ above, what is $C(50)$?
A: $250$ taka.

Q: Open or closed dot at $x = 3$ for the piece "$x > 3$"?
A: Open.

Q: Using the shipping function, cost for 5 kg?
A: $60 + 20 \times 4 = 140$.

@@ 3 | TOPIC | Composition of functions | f(g(x)), evaluating and simplifying compositions, decomposing functions | MUST
# Composition of functions

## 1. What it covers
- **Composition** $(f \circ g)(x) = f(g(x))$: the output of $g$ becomes the input of $f$.
- Evaluating and writing compositions.
- Order matters: $f(g(x)) \ne g(f(x))$ in general.
- **Decomposing** a function into simpler ones.
- The domain of a composition.

## 2. Where it is used
- **Programming:** chaining functions `f(g(x))`, Unix pipes `cat | grep | sort`, data pipelines, middleware.
- **Neural networks:** a deep network is a composition of many layers $f_n(\ldots f_2(f_1(x)))$; training uses the **chain rule**, the calculus of composition.
- **Business:** price after a discount, then tax: $T(D(p))$.
- **Unit conversions in sequence.**

## 3. What it means
Composition is an **assembly line**: the first machine processes the input, and its output goes straight into the second machine. In $f(g(x))$, **$g$ acts first** (inside), then $f$.

**Decomposing:** to see $h(x) = (3x + 1)^5$ as a composition, ask "what is done first?" → $g(x) = 3x + 1$, then "what happens to that?" → $f(u) = u^5$. So $h = f \circ g$. This skill is essential for the chain rule in calculus.

**Domain:** $x$ must be in the domain of $g$, **and** $g(x)$ must be in the domain of $f$.

## 4. Formula & worked example
$$
(f \circ g)(x) = f\big(g(x)\big)
$$

Let $f(x) = x^2 + 1$ and $g(x) = 2x - 3$.

**Example 1.** $f(g(2))$
- $g(2) = 1$ → $f(1) = 2$

**Example 2.** $f(g(x)) = (2x - 3)^2 + 1 = 4x^2 - 12x + 10$

**Example 3.** $g(f(x)) = 2(x^2 + 1) - 3 = 2x^2 - 1$ (different from Example 2)

**Example 4 (shopping).** Discount $D(p) = 0.8p$, then VAT $T(p) = 1.15p$. Final price $T(D(p)) = 0.92p$.

**Example 5 (decompose).** $h(x) = \sqrt{x^2 - 4}$ → inner $g(x) = x^2 - 4$, outer $f(u) = \sqrt{u}$.

**Example 6 (domain).** $f(x) = \sqrt{x}$, $g(x) = x - 5$: $f(g(x)) = \sqrt{x - 5}$, domain $x \ge 5$.

## 5. Common mistakes
- Applying the outer function first.
- Multiplying instead of composing: $f(g(x)) \ne f(x) \cdot g(x)$.
- Assuming $f \circ g = g \circ f$.

## 6. Practice
Q: $f(x) = 3x$, $g(x) = x + 4$. Find $f(g(x))$ and $g(f(x))$.
A: $3x + 12$ and $3x + 4$.

Q: $f(x) = x^2$, $g(x) = x - 1$. Find $g(f(3))$.
A: $g(9) = 8$.

Q: Decompose $h(x) = (5x - 2)^3$.
A: $g(x) = 5x - 2$, $f(u) = u^3$.

Q: $f(x) = \frac{1}{x}$, $g(x) = x - 2$. Domain of $f(g(x))$?
A: All reals except 2.

Q: Which function acts first in $f(g(x))$?
A: $g$.

@@ 3 | TOPIC | Inverse functions | One-to-one functions, finding inverses algebraically, reflection in y = x, restricting domains | MUST
# Inverse functions

## 1. What it covers
- What an **inverse** $f^{-1}$ does: it undoes $f$.
- **One-to-one** functions and the **horizontal line test**.
- Finding an inverse algebraically (swap and solve).
- The graph of $f^{-1}$ is the reflection of $f$ in $y = x$.
- Restricting a domain to make a function invertible.

## 2. Where it is used
- **Cryptography:** encryption and decryption are inverse functions.
- **Unit and currency conversion:** °C → °F and back.
- **Data compression/encoding:** encode and decode (Base64, UTF-8).
- **Logarithms** are the inverses of exponentials; arcsin is the inverse of sin.
- **Machine learning:** invertible transformations like standardising data and undoing it for predictions.

## 3. What it means
If $f$ takes $3 \to 11$, then $f^{-1}$ takes $11 \to 3$. Doing one then the other gets you back where you started: $f^{-1}(f(x)) = x$. It's like putting on and taking off shoes.

A function can only be inverted if **no two inputs share an output** (one-to-one). Otherwise the inverse wouldn't know which input to return. **Horizontal line test:** no horizontal line crosses the graph more than once.

**Finding the inverse:**
1. Write $y = f(x)$.
2. **Swap** $x$ and $y$.
3. Solve for $y$. That's $f^{-1}(x)$.

**Graph:** swapping $x$ and $y$ reflects the graph in the line $y = x$.

**Notation warning:** $f^{-1}(x)$ is **not** $\frac{1}{f(x)}$.

## 4. Formula & worked example
$$
f^{-1}(f(x)) = x \qquad f(f^{-1}(x)) = x
$$

**Example 1.** $f(x) = 3x + 2$.
- $y = 3x + 2$ → swap: $x = 3y + 2$ → $y = \frac{x - 2}{3}$ → $f^{-1}(x) = \frac{x - 2}{3}$
- Check: $f^{-1}(f(4)) = f^{-1}(14) = 4$ ✓

**Example 2 (temperature).** $F = \frac{9}{5}C + 32$ → inverse $C = \frac{5}{9}(F - 32)$.

**Example 3.** $f(x) = \frac{x + 1}{x - 2}$.
- Swap: $x = \frac{y + 1}{y - 2}$ → $xy - 2x = y + 1$ → $xy - y = 2x + 1$ → $y(x - 1) = 2x + 1$ → $f^{-1}(x) = \frac{2x + 1}{x - 1}$

**Example 4 (restrict).** $f(x) = x^2$ fails the horizontal line test. Restrict to $x \ge 0$: then $f^{-1}(x) = \sqrt{x}$.

**Example 5.** $f(x) = x^3 - 5$ → $f^{-1}(x) = \sqrt[3]{x + 5}$

## 5. Common mistakes
- Thinking $f^{-1}(x) = \frac{1}{f(x)}$.
- Forgetting to swap $x$ and $y$ (just rearranging gives the same function).
- Trying to invert a function that isn't one-to-one without restricting its domain.

## 6. Practice
Q: Inverse of $f(x) = 5x - 7$?
A: $f^{-1}(x) = \frac{x + 7}{5}$.

Q: Inverse of $f(x) = \frac{x}{4} + 1$?
A: $f^{-1}(x) = 4(x - 1)$.

Q: Is $f(x) = \lvert x\rvert$ one-to-one?
A: No, e.g. $f(-2) = f(2)$.

Q: $f(2) = 9$. What is $f^{-1}(9)$?
A: $2$.

Q: How are the graphs of $f$ and $f^{-1}$ related?
A: They are reflections of each other in the line $y = x$.

@@ 3 | TOPIC | Practice & review: Functions | Mixed problem set with worked answers | MUST
# Practice & review: Functions

**How to use this:** 50 minutes. Sketch graphs for 5–7.

## Problems
1. $f(x) = 2x^2 - x + 3$. Find $f(-2)$ and $f(x + 1)$.
2. Domain of $g(x) = \frac{\sqrt{x + 2}}{x - 1}$.
3. Average rate of change of $h(x) = x^2 - 4x$ on $[1, 5]$.
4. Is $f(x) = x^3 - x^2$ even, odd or neither?
5. Describe the transformations in $y = -\frac{1}{2}\lvert x + 3\rvert + 4$ and give the vertex.
6. Solve $\lvert 4 - 3x\rvert \ge 10$.
7. Graph and evaluate at $x = -1, 2, 4$: $f(x) = 3 - x$ for $x < 2$; $f(x) = x^2 - 3$ for $x \ge 2$.
8. $f(x) = \sqrt{x}$, $g(x) = 4x - 3$. Find $f(g(x))$, its domain, and $g(f(9))$.
9. Find the inverse of $f(x) = \frac{2x - 1}{x + 3}$.
10. The point $(2, -1)$ is on $f$. Where is it on $y = 3f(x - 4) + 2$?

## Answers
1. $f(-2) = 8 + 2 + 3 = 13$; $f(x + 1) = 2x^2 + 3x + 4$.
2. $x \ge -2$ and $x \ne 1$ → $[-2, 1) \cup (1, \infty)$.
3. $h(1) = -3$, $h(5) = 5$ → $\frac{8}{4} = 2$.
4. $f(-x) = -x^3 - x^2$: neither.
5. Left 3, reflected across the x-axis, vertical compression by $\frac{1}{2}$, up 4; vertex $(-3, 4)$.
6. $4 - 3x \le -10$ or $4 - 3x \ge 10$ → $x \ge \frac{14}{3}$ or $x \le -2$.
7. $f(-1) = 4$, $f(2) = 1$, $f(4) = 13$. The left piece ends with an open dot at $(2, 1)$ and the right piece starts with a closed dot at $(2, 1)$, so the graph has no gap.
8. $f(g(x)) = \sqrt{4x - 3}$, domain $x \ge \frac{3}{4}$; $g(f(9)) = g(3) = 9$.
9. Swap: $x(y + 3) = 2y - 1$ → $xy - 2y = -1 - 3x$ → $y = \frac{-1 - 3x}{x - 2} = \frac{3x + 1}{2 - x}$.
10. $(6, -1)$: $x \to 2 + 4 = 6$, $y \to 3(-1) + 2 = -1$.

## Flashcards
Q: Definition of a function?
A: A rule giving each input exactly one output.

Q: Which way does $f(x - 3)$ move the graph?
A: Right 3.

Q: Steps to find an inverse?
A: Write $y = f(x)$, swap $x$ and $y$, solve for $y$.

Q: $\lvert u\rvert < c$ becomes?
A: $-c < u < c$.
