@@ 1 | MODULE | Phase 2 — Linear Functions and Systems (Foundation) | Slope, forms of a line, parallel and perpendicular lines, linear models, systems of equations and inequalities | MUST
# Phase 2: Linear functions and systems

A **linear** relationship changes at a **constant rate**: every extra kilometre adds the same fare, every hour adds the same pay. Its graph is a straight line. Lines are the most-used model in science, business and machine learning (linear regression is "find the best line").

A **system** asks where two lines (or conditions) agree, which is how you compare plans, find break-even points and solve mixture problems.

@@ 2 | MODULE | Lines and linear models | Slope, intercepts, the three forms of a line, special lines, modelling | MUST
Every non-vertical line is $y = mx + b$: slope $m$, starting value $b$.

@@ 3 | TOPIC | Relations, tables and graphs of two variables | Independent and dependent variables, plotting points, reading tables and graphs | MUST
# Relations, tables and graphs of two variables

## 1. What it covers
- **Independent** (input) and **dependent** (output) variables.
- Representing a relationship as a table, a graph and an equation.
- Plotting points in all four quadrants.

## 2. Where it is used
- **Data analysis:** every chart has an input axis (time, size) and an output axis (sales, price).
- **Science experiments:** you change one variable and measure another.
- **Programming:** a function's parameters are inputs and its return value is the output.
- **Dashboards:** time series plots (time is independent).

## 3. What it means
If the cost of an Uber ride depends on distance, then distance is the **independent** variable (you choose it, usually $x$, horizontal axis) and cost is the **dependent** variable (it responds, usually $y$, vertical axis).

The same relationship can be shown three ways:
- **Equation:** $y = 25x + 60$.
- **Table:** pairs of values $(x, y)$.
- **Graph:** each pair is a point $(x, y)$ on the coordinate plane.

**Coordinates:** $(x, y)$ means "go $x$ along (right if positive, left if negative), then $y$ up or down". The axes split the plane into four quadrants: I $(+, +)$, II $(-, +)$, III $(-, -)$, IV $(+, -)$.

## 4. Formula & worked example
**Example 1.** Make a table for $y = 25x + 60$ (taxi fare, $x$ in km).
| $x$ | 0 | 2 | 4 | 6 |
|---|---|---|---|---|
| $y$ | 60 | 110 | 160 | 210 |
- Plotting these points gives a straight line starting at $(0, 60)$.

**Example 2.** Which is independent: hours studied or exam score?
- Hours studied (input); the score depends on it.

**Example 3.** In which quadrant is $(-3, 5)$? $(4, -2)$?
- II; IV.

**Example 4.** From the table $x$: 1, 2, 3, 4 and $y$: 3, 5, 7, 9, find the rule.
- $y$ goes up 2 each time $x$ goes up 1, and $y = 3$ at $x = 1$ → $y = 2x + 1$.

## 5. Common mistakes
- Plotting $(x, y)$ as (up, across). It's **across first**, then up.
- Putting the dependent variable on the horizontal axis.
- Assuming a relationship is linear from just two points.

## 6. Practice
Q: Independent variable in "the electricity bill depends on units used"?
A: Units used.

Q: Quadrant of $(-1, -7)$?
A: III.

Q: Rule for the table $x$: 0, 1, 2, 3 and $y$: 5, 8, 11, 14?
A: $y = 3x + 5$.

Q: Which axis shows the dependent variable?
A: The vertical ($y$) axis.

Q: Where do all points with $x = 0$ lie?
A: On the y-axis.

@@ 3 | TOPIC | Slope and rate of change | Rise over run, slope from two points, positive/negative/zero/undefined slope | MUST
# Slope and rate of change

## 1. What it covers
- **Slope** $m$: how steep a line is, as rise over run.
- The slope formula from two points.
- Positive, negative, zero and undefined slopes.
- Slope as a **rate of change** with units.

## 2. Where it is used
- **Business:** cost per item, revenue growth per month.
- **Physics:** on a distance–time graph, slope = speed.
- **Roads and ramps:** a 5% grade means a rise of 5 m per 100 m.
- **Machine learning:** the weights of a linear model are slopes; gradient descent follows slopes downhill.
- **Data analysis:** trend lines ("sales grow 120 units per month").

## 3. What it means
Slope measures **how much $y$ changes for each 1-unit increase in $x$**. Between two points, divide the vertical change (**rise**) by the horizontal change (**run**).

- **Positive** slope: the line goes up from left to right.
- **Negative**: goes down.
- **Zero**: horizontal (no change).
- **Undefined**: vertical (the run is 0, and you can't divide by 0).

A slope always has units: $\frac{\text{taka}}{\text{km}}$, $\frac{\text{km}}{\text{h}}$. That's why it's called a **rate** of change.

## 4. Formula & worked example
$$
m = \frac{\text{rise}}{\text{run}} = \frac{y_2 - y_1}{x_2 - x_1}
$$

**Example 1.** Slope through $(1, 2)$ and $(4, 11)$.
- $m = \frac{11 - 2}{4 - 1} = \frac{9}{3} = 3$

**Example 2.** Through $(-2, 5)$ and $(3, -5)$.
- $m = \frac{-5 - 5}{3 - (-2)} = \frac{-10}{5} = -2$

**Example 3.** Through $(2, 7)$ and $(6, 7)$: $m = 0$ (horizontal).

**Example 4.** Through $(3, 1)$ and $(3, 8)$: run $= 0$ → undefined (vertical).

**Example 5 (rate).** A water tank has 800 L at 2 pm and 560 L at 6 pm.
- $m = \frac{560 - 800}{6 - 2} = -60$ L per hour (draining 60 L/h)

## 5. Common mistakes
- Mixing the order: $\frac{y_2 - y_1}{x_1 - x_2}$ gives the wrong sign. Subtract in the same order top and bottom.
- Putting $x$ on top ("run over rise").
- Calling a vertical line's slope 0. Horizontal is 0; vertical is undefined.

## 6. Practice
Q: Slope through $(0, -3)$ and $(2, 5)$?
A: $4$.

Q: Slope through $(-1, 4)$ and $(5, 1)$?
A: $-\frac{1}{2}$.

Q: Slope of a horizontal line?
A: $0$.

Q: A car's odometer reads 12 400 km at 9 am and 12 610 km at noon. Average speed?
A: $70$ km/h.

Q: What does a slope of $-2.5$ mean in "temperature vs hour"?
A: The temperature falls 2.5 degrees each hour.

@@ 3 | TOPIC | Slope-intercept form and graphing lines | y = mx + b, graphing from the intercept and slope, intercepts | MUST
# Slope-intercept form and graphing lines

## 1. What it covers
- The form $y = mx + b$: $m$ is the slope, $b$ the y-intercept.
- Graphing a line from $b$ and $m$.
- Finding x- and y-intercepts.

## 2. Where it is used
- **Pricing models:** fixed fee + per-unit cost.
- **Linear regression:** the output of a fitted model is $y = mx + b$ (in ML often written $y = wx + b$, with $w$ = weight and $b$ = bias).
- **Spreadsheets:** Excel's `SLOPE` and `INTERCEPT` functions.
- **Physics:** $v = u + at$ is a line with slope $a$ and intercept $u$.

## 3. What it means
In $y = mx + b$:
- $b$ is where the line **crosses the y-axis** (the value when $x = 0$, the starting amount).
- $m$ tells you how to move from there: for each 1 step right, go $m$ up (or down if negative).

**Graphing recipe:** plot $(0, b)$, then use the slope as rise/run to find a second point, and draw the line through both.

**Intercepts:**
- **y-intercept:** set $x = 0$.
- **x-intercept:** set $y = 0$ and solve. It's where the line crosses the x-axis (e.g. "when does the tank become empty?").

## 4. Formula & worked example
$$
y = mx + b \qquad \text{y-intercept } (0, b) \qquad \text{x-intercept } \left(-\tfrac{b}{m}, 0\right)
$$

**Example 1.** Graph $y = \frac{2}{3}x - 1$.
- Start at $(0, -1)$. Slope $\frac{2}{3}$: right 3, up 2 → $(3, 1)$. Draw the line.

**Example 2.** Slope and intercept of $y = -4x + 7$: $m = -4$, $b = 7$.

**Example 3.** Rewrite $3x + 2y = 8$ in slope-intercept form.
- $2y = -3x + 8$ → $y = -\frac{3}{2}x + 4$

**Example 4.** Intercepts of $y = 2x - 6$.
- y-intercept: $(0, -6)$. x-intercept: $0 = 2x - 6$ → $(3, 0)$.

**Example 5 (context).** A tank holds 800 L and drains 60 L/h: $V = -60t + 800$. When is it empty?
- $0 = -60t + 800$ → $t = 13.\overline{3}$ h (13 h 20 min)

## 5. Common mistakes
- Reading the slope of $y = 7 - 4x$ as 7. Rearrange first: $y = -4x + 7$, so $m = -4$.
- Using run/rise when plotting.
- Forgetting to divide **every** term when isolating $y$.

## 6. Practice
Q: Slope and y-intercept of $y = 5 - \frac{1}{2}x$?
A: $m = -\frac{1}{2}$, $b = 5$.

Q: Write $4x - 2y = 10$ in slope-intercept form.
A: $y = 2x - 5$.

Q: x-intercept of $y = -3x + 9$?
A: $(3, 0)$.

Q: A line has slope 3 and passes through $(0, -2)$. Equation?
A: $y = 3x - 2$.

Q: A phone plan is 200 taka + 1.5 taka/min. Equation?
A: $C = 1.5m + 200$.

@@ 3 | TOPIC | Point-slope and standard form | Writing the equation of a line from a point and slope or from two points; Ax + By = C | MUST
# Point-slope and standard form

## 1. What it covers
- **Point-slope form** $y - y_1 = m(x - x_1)$.
- Writing a line's equation from a point and slope, or from two points.
- **Standard form** $Ax + By = C$ and graphing with intercepts.
- Converting between the three forms.

## 2. Where it is used
- **Data:** writing the trend line through two measured points.
- **Graphics:** a line between two pixels or vertices.
- **Economics:** standard form fits budget constraints: $50x + 80y = 4000$ (spending on two goods).
- **Linear programming and systems.**

## 3. What it means
**Point-slope** says: the slope between any point $(x, y)$ on the line and the fixed point $(x_1, y_1)$ is $m$. That is $\frac{y - y_1}{x - x_1} = m$; multiply out to get the form. It's the fastest way to write a line when you know one point and the slope.

**Standard form** $Ax + By = C$ (usually with whole numbers and $A \ge 0$) is natural for "total" situations. Its intercepts are easy: set $x = 0$ to get $y = \frac{C}{B}$, and $y = 0$ to get $x = \frac{C}{A}$.

## 4. Formula & worked example
$$
y - y_1 = m(x - x_1) \qquad Ax + By = C \ \Rightarrow\ m = -\frac{A}{B}
$$

**Example 1 (point + slope).** Slope 4 through $(2, -3)$.
- $y + 3 = 4(x - 2)$ → $y = 4x - 11$

**Example 2 (two points).** Through $(1, 5)$ and $(4, -1)$.
- $m = \frac{-1 - 5}{4 - 1} = -2$ → $y - 5 = -2(x - 1)$ → $y = -2x + 7$

**Example 3 (to standard form).** $y = \frac{2}{3}x - 4$
- ×3: $3y = 2x - 12$ → $2x - 3y = 12$

**Example 4 (graph via intercepts).** $3x + 4y = 24$
- $x = 0$ → $y = 6$; $y = 0$ → $x = 8$. Plot $(0, 6)$ and $(8, 0)$, join.

**Example 5 (budget).** 4000 taka for notebooks (50 each) and pens (20 each): $50x + 20y = 4000$. If you buy 40 notebooks, how many pens?
- $2000 + 20y = 4000$ → $y = 100$

## 5. Common mistakes
- Sign errors with negative coordinates: through $(-2, 3)$ gives $y - 3 = m(x + 2)$.
- Reading the slope of $Ax + By = C$ as $A$. It's $-\frac{A}{B}$.
- Forgetting to distribute $m$ to both terms in the bracket.

## 6. Practice
Q: Line with slope $-3$ through $(-1, 4)$?
A: $y = -3x + 1$.

Q: Line through $(2, 1)$ and $(6, 9)$?
A: $m = 2$; $y = 2x - 3$.

Q: Slope of $5x - 2y = 7$?
A: $\frac{5}{2}$.

Q: Intercepts of $2x + 5y = 20$?
A: $(10, 0)$ and $(0, 4)$.

Q: Write $y = -\frac{1}{4}x + 3$ in standard form.
A: $x + 4y = 12$.

@@ 3 | TOPIC | Horizontal, vertical, parallel and perpendicular lines | y = k and x = k; equal slopes; negative reciprocal slopes | MUST
# Horizontal, vertical, parallel and perpendicular lines

## 1. What it covers
- Equations of **horizontal** ($y = k$) and **vertical** ($x = k$) lines.
- **Parallel** lines have equal slopes.
- **Perpendicular** lines have slopes that are negative reciprocals ($m_1m_2 = -1$).
- Writing lines parallel or perpendicular to a given line through a point.

## 2. Where it is used
- **Graphics and UI layout:** grid lines, guides and snapping.
- **Geometry proofs on coordinates:** showing a shape is a rectangle (perpendicular sides) or parallelogram (parallel sides).
- **Engineering drawings:** perpendicular cross-sections.
- **Shortest distance:** the shortest path from a point to a line is perpendicular to it (used in regression and collision detection).

## 3. What it means
- **Horizontal:** $y$ never changes → $y = k$, slope 0.
- **Vertical:** $x$ never changes → $x = k$, slope undefined.
- **Parallel lines** have the same steepness and never meet, so their slopes are equal (with different intercepts).
- **Perpendicular lines** meet at $90^\circ$. Rotating a line by $90^\circ$ swaps rise and run and flips one sign, so the new slope is $-\frac{1}{m}$. Example: slope $\frac{2}{3}$ → perpendicular slope $-\frac{3}{2}$.

## 4. Formula & worked example
$$
\text{parallel: } m_1 = m_2 \qquad \text{perpendicular: } m_1 \cdot m_2 = -1 \ \left(m_2 = -\frac{1}{m_1}\right)
$$

**Example 1.** Line through $(4, -2)$ parallel to $y = 3x + 1$.
- $m = 3$: $y + 2 = 3(x - 4)$ → $y = 3x - 14$

**Example 2.** Line through $(6, 1)$ perpendicular to $y = 2x - 5$.
- $m = -\frac{1}{2}$: $y - 1 = -\frac{1}{2}(x - 6)$ → $y = -\frac{1}{2}x + 4$

**Example 3.** Are $2x + 3y = 6$ and $3x - 2y = 4$ perpendicular?
- Slopes $-\frac{2}{3}$ and $\frac{3}{2}$; product $= -1$ → yes.

**Example 4.** Horizontal and vertical lines through $(-3, 7)$.
- $y = 7$ and $x = -3$.

**Example 5.** Is the quadrilateral $A(0,0)$, $B(4,1)$, $C(3,5)$, $D(-1,4)$ a rectangle?
- $AB$: $\frac{1}{4}$; $BC$: $\frac{4}{-1} = -4$; product $-1$ → perpendicular. $CD$: $\frac{-1}{-4} = \frac{1}{4}$ (parallel to AB); $DA$: $\frac{4}{-1} = -4$ (parallel to BC). Opposite sides parallel, adjacent sides perpendicular → rectangle (in fact a square: every side has length $\sqrt{17}$).

## 5. Common mistakes
- Using the reciprocal without changing the sign (perpendicular to slope 2 is $-\frac{1}{2}$, not $\frac{1}{2}$).
- Writing a vertical line as $y = k$.
- Calling lines with equal slopes **and** equal intercepts "parallel". They're the same line.

## 6. Practice
Q: Slope perpendicular to $-\frac{4}{5}$?
A: $\frac{5}{4}$.

Q: Line through $(2, 3)$ parallel to $y = -x + 8$?
A: $y = -x + 5$.

Q: Equation of the vertical line through $(5, -1)$?
A: $x = 5$.

Q: Are $y = 3x + 2$ and $y = -3x + 2$ perpendicular?
A: No, product $= -9$.

Q: Line through the origin perpendicular to $y = \frac{1}{3}x$?
A: $y = -3x$.

@@ 3 | TOPIC | Linear modeling and interpreting slope and intercept | Building linear models from words and data; proportional vs linear; meaning in context | MUST
# Linear modeling and interpreting slope and intercept

## 1. What it covers
- Building a linear model from a description or from data.
- Interpreting slope and intercept **in context**, with units.
- Proportional ($y = kx$) vs linear ($y = mx + b$).
- Using a model to predict, and knowing its limits.

## 2. Where it is used
- **Business forecasting:** cost, revenue and profit lines; break-even.
- **Science:** calibration lines (sensor reading → real value).
- **Machine learning:** linear regression is the foundation of many models.
- **Personal finance:** savings plans.

## 3. What it means
A linear model has a **starting value** (intercept $b$) and a **constant rate** (slope $m$).

**Interpreting in context** (always with units):
- Slope: "for each extra [x-unit], [y] changes by $m$ [y-units]".
- Intercept: "when [x] is 0, [y] is $b$".

**Proportional vs linear:** proportional is the special case with $b = 0$ (the line passes through the origin). A taxi fare with a starting charge is linear but not proportional.

**Limits:** models work only within sensible ranges. A model that predicts a negative price or a height of 4 m for an adult is being used outside its range (**extrapolation**).

## 4. Formula & worked example
**Example 1 (from words).** A plumber charges 500 taka call-out plus 300 taka per hour.
- $C = 300h + 500$. Slope: 300 taka per extra hour. Intercept: 500 taka even for 0 hours.

**Example 2 (from two data points).** A candle is 24 cm after 2 h and 15 cm after 5 h.
- $m = \frac{15 - 24}{5 - 2} = -3$ cm/h; $24 = -3(2) + b$ → $b = 30$
- $L = -3t + 30$. Original height 30 cm; burns 3 cm per hour; burns out at $t = 10$ h.

**Example 3 (profit and break-even).** Selling T-shirts: cost 20 000 setup + 250 each; price 450.
- Profit $P = 450n - (250n + 20\,000) = 200n - 20\,000$
- Break-even: $P = 0$ → $n = 100$ shirts

**Example 4 (interpretation).** A model of salary vs years of experience is $S = 4500y + 35\,000$.
- Each extra year adds 4500 taka/month; a new graduate (0 years) is predicted to earn 35 000.

**Example 5 (proportional?).** Which is proportional: (a) cost of mangoes at 120 taka/kg, (b) rickshaw fare 30 + 15/km?
- (a) only ($y = 120x$).

## 5. Common mistakes
- Describing the slope without units or without "per".
- Interpreting the intercept when $x = 0$ makes no sense (e.g. a person of height 0).
- Trusting predictions far outside the data range.

## 6. Practice
Q: A gym costs $C = 1500m + 2000$. Meaning of 2000?
A: The one-time joining fee (cost at 0 months).

Q: A plant is 4 cm at week 1 and 10 cm at week 4. Model?
A: $m = 2$ cm/week, $h = 2w + 2$.

Q: Revenue $R = 80n$, cost $C = 30n + 1500$. Break-even?
A: $50n = 1500$ → $n = 30$.

Q: Is $y = 7x$ proportional?
A: Yes, it passes through the origin.

Q: Why is predicting a 50-year-old's height from a child growth line wrong?
A: It extrapolates far outside the data; growth isn't linear over a lifetime.

@@ 2 | MODULE | Systems of equations and inequalities | Graphing, substitution, elimination, special cases, word problems, feasible regions | MUST
Two unknowns need two equations. The solution is the point that satisfies both: where the two lines cross.

@@ 3 | TOPIC | Systems of equations: meaning and solving by graphing | Solutions as intersection points, estimating and checking | MUST
# Systems of equations: meaning and solving by graphing

## 1. What it covers
- What a **system** of two linear equations is.
- Its solution as the **intersection point** of two lines.
- Solving by graphing and verifying algebraically.

## 2. Where it is used
- **Comparing options:** when do two phone plans cost the same?
- **Break-even:** where the cost line meets the revenue line.
- **Economics:** supply and demand curves meet at the market price.
- **Navigation:** two position lines cross at your location.

## 3. What it means
Each equation describes a line: all the points that satisfy it. A point that satisfies **both** equations must be on **both** lines, which means it's where they cross.

Graphing gives a quick picture and estimate. It's exact only when the crossing is at a clear grid point, so always **check** by substituting into both equations.

## 4. Formula & worked example
**Example 1.** Solve by graphing: $y = 2x - 1$ and $y = -x + 5$.
- Line 1 through $(0, -1)$, slope 2; line 2 through $(0, 5)$, slope $-1$.
- They cross at $(2, 3)$. Check: $2(2) - 1 = 3$ ✓, $-2 + 5 = 3$ ✓.

**Example 2.** Is $(1, 4)$ a solution of $x + y = 5$ and $3x - y = 1$?
- $1 + 4 = 5$ ✓; $3 - 4 = -1 \ne 1$ ✗ → no.

**Example 3 (plans).** Plan A: $C = 20m + 300$. Plan B: $C = 35m$. Graph both; they cross at $m = 20$, $C = 700$. For fewer than 20 units B is cheaper; for more, A is.

**Example 4 (code).** Solving numerically.
```python
import numpy as np
A = np.array([[2, -1], [1, 1]])    # 2x - y = 1 ;  x + y = 5
b = np.array([1, 5])
print(np.linalg.solve(A, b))       # [2. 3.]
```
The coefficients go in a matrix `A` and the constants in `b`; NumPy returns $x = 2$, $y = 3$. (The Linear Algebra roadmap explains how this works.)

## 5. Common mistakes
- Reading the intersection inaccurately and not checking.
- Checking the point in only one equation.
- Drawing lines with wrong slopes because the equations weren't in $y = mx + b$ form first.

## 6. Practice
Q: What does the solution of a system of two lines represent?
A: The point where the two lines intersect.

Q: Is $(3, -1)$ a solution of $x + y = 2$ and $x - y = 4$?
A: Yes, both are true.

Q: Lines $y = x + 1$ and $y = -x + 7$ cross where?
A: $(3, 4)$.

Q: Why should you check a graphed solution algebraically?
A: Graph readings can be imprecise.

Q: In a cost/revenue graph, what is the intersection called?
A: The break-even point.

@@ 3 | TOPIC | Solving systems by substitution | Isolating a variable and substituting into the other equation | MUST
# Solving systems by substitution

## 1. What it covers
- The substitution method, step by step.
- When it is the best method (a variable already isolated or with coefficient 1).

## 2. Where it is used
- **Word problems** where one quantity is described in terms of another ("twice as many", "300 more than").
- **Nonlinear systems** later (line meets curve).
- **Programming:** symbolic solvers (like SymPy's `solve`) work this way.

## 3. What it means
If one equation tells you what $y$ **is** in terms of $x$, you can **replace** $y$ in the other equation with that expression. Now you have one equation with one unknown, which you already know how to solve.

**Steps:**
1. Solve one equation for one variable (pick the easiest).
2. Substitute that expression into the **other** equation.
3. Solve for the remaining variable.
4. Substitute back to find the first variable.
5. Check in both original equations.

## 4. Formula & worked example
**Example 1.** $y = 3x - 2$ and $2x + y = 13$.
- $2x + (3x - 2) = 13$ → $5x = 15$ → $x = 3$ → $y = 7$. Solution $(3, 7)$.

**Example 2.** $x + 2y = 7$ and $3x - y = 7$.
- From the first: $x = 7 - 2y$. Substitute: $3(7 - 2y) - y = 7$ → $21 - 7y = 7$ → $y = 2$ → $x = 3$.

**Example 3 (word problem).** A rectangle's length is 4 more than its width; perimeter 40.
- $l = w + 4$, $2l + 2w = 40$ → $2(w + 4) + 2w = 40$ → $w = 8$, $l = 12$

**Example 4 (fractions).** $y = \frac{1}{2}x + 1$ and $3x - 4y = 2$.
- $3x - 4\left(\frac{1}{2}x + 1\right) = 2$ → $3x - 2x - 4 = 2$ → $x = 6$, $y = 4$

## 5. Common mistakes
- Substituting back into the same equation you rearranged (it only gives $0 = 0$).
- Forgetting brackets around the substituted expression.
- Stopping after finding one variable.

## 6. Practice
Q: Solve $y = x + 4$ and $x + y = 10$.
A: $(3, 7)$.

Q: Solve $x = 2y$ and $3x - 4y = 10$.
A: $6y - 4y = 10$ → $y = 5$, $x = 10$.

Q: Solve $2x + y = 1$ and $x - 3y = 11$.
A: $y = 1 - 2x$ → $x - 3 + 6x = 11$ → $x = 2$, $y = -3$.

Q: When is substitution most convenient?
A: When one variable is already isolated or has coefficient 1 or $-1$.

Q: Adult tickets cost twice child tickets. 3 adults + 4 children = 2000 taka. Child price?
A: $3(2c) + 4c = 2000$ → $c = 200$ taka.

@@ 3 | TOPIC | Solving systems by elimination | Adding or subtracting equations, scaling first | MUST
# Solving systems by elimination

## 1. What it covers
- Adding or subtracting equations so one variable cancels.
- Multiplying one or both equations first to line up coefficients.
- Choosing which variable to eliminate.

## 2. Where it is used
- **Systems in standard form** ($ax + by = c$), common in money and mixture problems.
- **Large systems:** Gaussian elimination, the algorithm behind solving thousands of equations in engineering simulations and in `numpy.linalg.solve`, is this idea done systematically.
- **Circuit analysis** (Kirchhoff's laws).

## 3. What it means
If two equations are both true, then their **sum** is also true (adding equal things to equal things). Choose a combination that makes one variable's coefficients **opposite**, so it disappears when you add.

**Steps:**
1. Line up the variables in columns.
2. Multiply one or both equations so that one variable has opposite (or equal) coefficients.
3. Add (or subtract) the equations to eliminate it.
4. Solve, substitute back, and check.

## 4. Formula & worked example
**Example 1.** $3x + 2y = 16$ and $5x - 2y = 0$.
- Add: $8x = 16$ → $x = 2$ → $3(2) + 2y = 16$ → $y = 5$

**Example 2.** $2x + 3y = 12$ and $2x + y = 8$.
- Subtract: $2y = 4$ → $y = 2$ → $x = 3$

**Example 3 (scale one).** $x + 3y = 7$ and $2x - y = 0$.
- ×3 the second: $6x - 3y = 0$. Add to the first: $7x = 7$ → $x = 1$, $y = 2$

**Example 4 (scale both).** $3x + 4y = 10$ and $2x - 3y = 1$.
- ×3 first, ×4 second: $9x + 12y = 30$, $8x - 12y = 4$ → add: $17x = 34$ → $x = 2$ → $y = 1$

**Example 5 (money).** 4 pens + 3 notebooks = 330 taka; 2 pens + 5 notebooks = 410 taka.
- ×2 the second: $4p + 10n = 820$. Subtract the first: $7n = 490$ → $n = 70$, $p = 30$

## 5. Common mistakes
- Multiplying only one side of an equation (or only some terms).
- Subtracting with sign errors. Adding after multiplying by $-1$ is safer.
- Eliminating and then forgetting to find the other variable.

## 6. Practice
Q: Solve $x + y = 9$ and $x - y = 3$.
A: $(6, 3)$.

Q: Solve $4x + y = 14$ and $2x + y = 8$.
A: Subtract: $2x = 6$ → $(3, 2)$.

Q: Solve $2x + 5y = 1$ and $3x - 2y = 11$.
A: ×3 and ×2: $15y + 4y = 3 - 22$ → $y = -1$, $x = 3$.

Q: What makes elimination work?
A: Adding two true equations gives another true equation; choose multiples so one variable cancels.

Q: 3 burgers + 2 drinks = 790 taka; 2 burgers + 3 drinks = 660 taka. Burger price?
A: ×3/×2 and subtract: $5b = 1050$ → $b = 210$ taka.

@@ 3 | TOPIC | Special systems and choosing a method | No solution, infinitely many solutions, and when to graph, substitute or eliminate | MUST
# Special systems and choosing a method

## 1. What it covers
- Systems with **no solution** (parallel lines) and **infinitely many** (the same line).
- Recognising them from slopes and intercepts, or during solving.
- Choosing the best method for a given system.

## 2. Where it is used
- **Consistency checks:** an inconsistent system in a business model means the requirements contradict each other.
- **Data and engineering:** "redundant" equations (infinitely many solutions) mean you need more information.
- **Linear algebra:** the same three cases for any number of equations (determinant zero → not exactly one solution).

## 3. What it means
Two lines in a plane can:
- **Cross once** → exactly one solution (different slopes).
- **Be parallel** → no solution (same slope, different intercepts). While solving you get a false statement like $0 = 5$.
- **Coincide** → infinitely many solutions (same slope and intercept). You get a true statement like $0 = 0$.

**Choosing a method:**
- A variable already isolated (or coefficient ±1) → **substitution**.
- Both in standard form → **elimination**.
- A picture, or a rough estimate → **graphing**.

## 4. Formula & worked example
For $y = m_1x + b_1$ and $y = m_2x + b_2$:
- $m_1 \ne m_2$: one solution
- $m_1 = m_2$, $b_1 \ne b_2$: none
- $m_1 = m_2$, $b_1 = b_2$: infinitely many

**Example 1.** $2x - y = 3$ and $4x - 2y = 10$.
- ×2 the first: $4x - 2y = 6$. Subtract from the second: $0 = 4$ ✗ → no solution (parallel).

**Example 2.** $x + 3y = 6$ and $2x + 6y = 12$.
- The second is 2 × the first → infinitely many solutions: all points with $y = -\frac{1}{3}x + 2$.

**Example 3 (by slopes).** $y = 5x - 2$ and $y = 5x + 1$: same slope, different intercepts → none.

**Example 4 (choose).** $y = 4x - 7$ and $3x + 2y = 8$ → substitution (y is isolated).
- $3x + 2(4x - 7) = 8$ → $11x = 22$ → $x = 2$, $y = 1$

## 5. Common mistakes
- Writing "$x = 0$" when the variables cancel. Look at the leftover statement.
- Saying "no solution" for $0 = 0$. That's infinitely many.
- Comparing slopes without first writing both lines in the same form.

## 6. Practice
Q: How many solutions: $y = -2x + 4$ and $y = -2x - 1$?
A: None (parallel).

Q: How many solutions: $3x - y = 2$ and $6x - 2y = 4$?
A: Infinitely many (same line).

Q: Solving gives $0 = -3$. Meaning?
A: No solution.

Q: Best method for $5x + 2y = 9$ and $3x - 2y = 7$?
A: Elimination (add: $8x = 16$).

Q: For what $k$ does $y = kx + 1$, $y = 3x - 2$ have no solution?
A: $k = 3$.

@@ 3 | TOPIC | Systems word problems | Mixtures, costs, ages, rates and interest with two variables | MUST
# Systems word problems

## 1. What it covers
- Setting up two equations from a word problem.
- Standard types: **total and value** (tickets, coins), **mixtures**, **speed with current or wind**, **investment**.

## 2. Where it is used
- **Business:** product mix, pricing.
- **Chemistry and pharmacy:** mixing solutions to a target concentration.
- **Finance:** splitting money between investments.
- **Travel:** speed of a boat in a river, plane in the wind.

## 3. What it means
Most problems give **two facts**: usually a **count** and a **value** (or two totals). Each fact becomes one equation.

**Method:** define two variables, write one equation per fact, solve (usually by elimination), and check in the story.

## 4. Formula & worked example
**Example 1 (tickets).** 250 tickets sold; adults 300 taka, children 150 taka; total 60 000 taka.
- $a + c = 250$; $300a + 150c = 60\,000$
- From the first, $c = 250 - a$: $300a + 37\,500 - 150a = 60\,000$ → $a = 150$, $c = 100$

**Example 2 (mixture).** Mix a 10% salt solution with a 30% one to make 40 L of 25%.
- $x + y = 40$; $0.10x + 0.30y = 0.25(40) = 10$
- ×10: $x + 3y = 100$; subtract the first: $2y = 60$ → $y = 30$ L of 30%, $x = 10$ L of 10%

**Example 3 (current).** A boat goes 36 km downstream in 2 h and back upstream in 3 h. Boat speed and current?
- $b + c = 18$; $b - c = 12$ → $b = 15$ km/h, $c = 3$ km/h

**Example 4 (investment).** 200 000 taka split between 6% and 9% accounts earns 14 700 a year.
- $x + y = 200\,000$; $0.06x + 0.09y = 14\,700$
- ×100: $6x + 9y = 1\,470\,000$; $6x + 6y = 1\,200\,000$ → $3y = 270\,000$ → $y = 90\,000$, $x = 110\,000$

## 5. Common mistakes
- Using percentages as whole numbers (10 instead of 0.10).
- In current problems, adding the current upstream (upstream speed is $b - c$).
- Answering with one value when the question asks for both.

## 6. Practice
Q: Two numbers sum to 50 and differ by 14. Find them.
A: 32 and 18.

Q: 30 coins of 2 and 5 taka total 99 taka. How many of each?
A: $x + y = 30$, $2x + 5y = 99$ → 17 two-taka, 13 five-taka.

Q: A plane flies 900 km with the wind in 2 h and against it in 2.5 h. Wind speed?
A: $p + w = 450$, $p - w = 360$ → $w = 45$ km/h.

Q: How much 20% and 50% juice make 30 L of 40%?
A: 10 L of 20%, 20 L of 50%.

Q: 2 kg apples + 3 kg oranges = 690 taka; 3 kg apples + 1 kg orange = 615 taka. Price per kg of apples?
A: Apples 165, oranges 120.

@@ 3 | TOPIC | Linear inequalities in two variables and feasible regions | Boundary lines, shading, systems of inequalities, constraint problems | SHOULD
# Linear inequalities in two variables and feasible regions

## 1. What it covers
- Graphing $y > mx + b$ (and $\ge$, $<$, $\le$): boundary line and shading.
- **Systems** of inequalities: the **feasible region** where all are true.
- Testing points, and real constraint problems.
- A first look at **linear programming** (best value at a corner).

## 2. Where it is used
- **Operations research:** maximising profit given limited materials, labour and machine hours.
- **Diet and nutrition planning:** meeting minimum nutrients at minimum cost.
- **Cloud resource planning:** CPU and memory limits.
- **Logistics:** truck capacity by weight and volume.

## 3. What it means
A line splits the plane into two halves. An inequality like $y > 2x + 1$ is true on **one side**.

**Steps:**
1. Draw the boundary line: **dashed** for $<$ or $>$ (not included), **solid** for $\le$ or $\ge$.
2. Pick a **test point** not on the line (often $(0, 0)$). If it satisfies the inequality, shade its side; otherwise the other side.

For a system, the **feasible region** is where all the shadings overlap. In linear programming, the best (max or min) value of a linear objective always occurs at a **corner** of this region.

## 4. Formula & worked example
**Example 1.** Graph $y \le -x + 4$.
- Solid line through $(0, 4)$ and $(4, 0)$. Test $(0, 0)$: $0 \le 4$ ✓ → shade the side containing the origin (below).

**Example 2.** Graph the system $y > x - 2$ and $y \le 3$.
- Dashed line $y = x - 2$, shade above; solid line $y = 3$, shade below. The feasible region is between them.

**Example 3 (constraints).** A baker makes cakes ($x$) and pies ($y$). Each cake needs 2 h of oven time, each pie 1 h; 16 h available. At most 6 cakes. Profit: 500 per cake, 300 per pie. Maximise profit.
- Constraints: $2x + y \le 16$, $x \le 6$, $x \ge 0$, $y \ge 0$.
- Corners: $(0, 0)$, $(6, 0)$, $(6, 4)$, $(0, 16)$.
- Profit $500x + 300y$: $0$, $3000$, $4200$, $4800$ → best is $(0, 16)$: 16 pies, 4800 taka.

## 5. Common mistakes
- A solid line for a strict inequality (or dashed for $\le$).
- Shading the wrong side; always test a point.
- Forgetting the non-negativity constraints $x \ge 0$, $y \ge 0$ in real problems.

## 6. Practice
Q: Dashed or solid boundary for $y < 3x$?
A: Dashed.

Q: Does $(1, 1)$ satisfy $2x + 3y > 6$?
A: No: $5 > 6$ is false.

Q: Where does the maximum of a linear objective occur in linear programming?
A: At a corner (vertex) of the feasible region.

Q: Write constraints: at most 40 hours; product A takes 2 h, B takes 5 h.
A: $2a + 5b \le 40$, $a \ge 0$, $b \ge 0$.

Q: Which side of $y \ge x$ contains $(0, 3)$?
A: $3 \ge 0$ is true, so the side above the line.

@@ 3 | TOPIC | Practice & review: Linear functions and systems | Mixed problem set with worked answers | MUST
# Practice & review: Linear functions and systems

**How to use this:** 50 minutes, no calculator.

## Problems
1. Slope and equation of the line through $(-2, 7)$ and $(4, -2)$.
2. Write $5x - 3y = 15$ in slope-intercept form and give both intercepts.
3. Line through $(3, -1)$ perpendicular to $2x + 5y = 10$.
4. A printer has 2000 sheets and uses 150 per day. Model and interpret; when does it run out?
5. Solve by substitution: $y = 2x - 3$, $4x - 3y = 1$.
6. Solve by elimination: $5x + 3y = 1$, $3x + 2y = 0$.
7. How many solutions: $6x - 4y = 8$ and $y = 1.5x - 2$?
8. 40 L of 15% acid must become 25% by adding pure acid. How much?
9. Find the corners of the region $x \ge 0$, $y \ge 0$, $x + y \le 8$, $x + 3y \le 12$, and the maximum of $P = 3x + 5y$.

## Answers
1. $m = \frac{-9}{6} = -\frac{3}{2}$; $y - 7 = -\frac{3}{2}(x + 2)$ → $y = -\frac{3}{2}x + 4$.
2. $y = \frac{5}{3}x - 5$; intercepts $(3, 0)$, $(0, -5)$.
3. Given slope $-\frac{2}{5}$ → perpendicular $\frac{5}{2}$: $y = \frac{5}{2}x - \frac{17}{2}$.
4. $S = 2000 - 150d$; starts with 2000, uses 150 per day; runs out after $13.\overline{3}$ days (during day 14).
5. $4x - 3(2x - 3) = 1$ → $-2x + 9 = 1$ → $x = 4$, $y = 5$.
6. ×2 / ×3: $10x + 6y = 2$, $9x + 6y = 0$ → $x = 2$, $y = -3$.
7. $y = 1.5x - 2$ is the same line as $6x - 4y = 8$ → infinitely many.
8. $0.15(40) + x = 0.25(40 + x)$ → $6 + x = 10 + 0.25x$ → $x = \frac{16}{3} \approx 5.33$ L.
9. Corners $(0,0)$, $(8,0)$, $(6,2)$, $(0,4)$. $P$: 0, 24, 28, 20 → max 28 at $(6, 2)$.

## Flashcards
Q: Slope formula?
A: $m = \frac{y_2 - y_1}{x_2 - x_1}$.

Q: Point-slope form?
A: $y - y_1 = m(x - x_1)$.

Q: Perpendicular slopes multiply to?
A: $-1$.

Q: Three possible numbers of solutions of a 2×2 linear system?
A: One, none, or infinitely many.
