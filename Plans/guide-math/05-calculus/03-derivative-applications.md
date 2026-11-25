@@ 1 | MODULE | Phase 3 — Applications of Derivatives (Core) | Linear approximation, motion, related rates, MVT, extrema, curve sketching, optimisation, L'Hôpital, Newton's method and gradient descent | MUST
# Phase 3: Applications of derivatives

Now the payoff: derivatives tell you where a function is increasing, where it peaks, how fast connected quantities change, and how to find the **best** design, price or path. This is the calculus used daily in engineering, economics and machine learning.

@@ 2 | MODULE | Rates and approximation | Tangent lines, linear approximation, motion, related rates, mean value theorem | MUST
The derivative is a local rate of change; these topics use it directly.

@@ 3 | TOPIC | Tangent lines and linear approximation | Equation of the tangent, local linearisation, differentials, estimating values | MUST
# Tangent lines and linear approximation

## 1. What it covers
- The tangent line $y = f(a) + f'(a)(x - a)$.
- **Linear approximation**: near $a$, $f(x) \approx f(a) + f'(a)(x - a)$.
- **Differentials** $dy = f'(x)\,dx$ for estimating small changes and errors.

## 2. Where it is used
- **Physics and engineering:** small-angle approximation $\sin\theta \approx \theta$; linearising nonlinear systems around an operating point (control engineering).
- **Error propagation:** how a small measurement error affects a computed result.
- **Numerical methods:** Newton's method and Euler's method are built on tangent lines.
- **Machine learning:** gradient descent assumes the loss is roughly linear in a small step.

## 3. What it means
Zoom in far enough on any smooth curve and it looks like a straight line: its tangent. So for $x$ close to $a$, the tangent line is a good, simple estimate of the function. Think of the Earth: it's round, but locally (your street) it looks flat.

**Differentials:** a small change $dx$ in the input causes a change of about $dy = f'(x)\,dx$ in the output.

## 4. Formula & worked example
$$
L(x) = f(a) + f'(a)(x - a) \qquad dy = f'(x)\,dx
$$

**Example 1.** Tangent to $f(x) = \sqrt{x}$ at $x = 9$.
- $f(9) = 3$, $f'(x) = \frac{1}{2\sqrt{x}}$, $f'(9) = \frac{1}{6}$ → $y = 3 + \frac{1}{6}(x - 9)$

**Example 2.** Estimate $\sqrt{9.4}$ with that line: $3 + \frac{0.4}{6} \approx 3.0667$ (true $3.0659$).

**Example 3.** Estimate $\sin(0.05)$: near 0, $\sin x \approx x$ → $0.05$ (true $0.04998$).

**Example 4 (error).** The radius of a ball is measured as 10 cm ± 0.1 cm. Error in its volume?
- $V = \frac{4}{3}\pi r^3$, $dV = 4\pi r^2\,dr = 4\pi(100)(0.1) \approx 125.7$ cm³ (about 3% of $V \approx 4189$).

**Example 5.** Estimate $e^{0.1}$: near 0, $e^x \approx 1 + x$ → $1.1$ (true $1.1052$).

## 5. Common mistakes
- Using the approximation far from $a$ (error grows quickly).
- Evaluating $f'$ at $x$ instead of $a$.
- Forgetting radians for trig approximations.

## 6. Practice
Q: Linear approximation formula?
A: $f(x) \approx f(a) + f'(a)(x - a)$.

Q: Estimate $\sqrt{25.5}$.
A: $5 + \frac{0.5}{10} = 5.05$.

Q: Tangent to $y = x^3$ at $x = 2$?
A: $y = 8 + 12(x - 2)$.

Q: Linear approximation of $\ln x$ near $x = 1$?
A: $\ln x \approx x - 1$.

Q: A square's side is 5 cm ± 0.02 cm. Approximate error in area?
A: $dA = 2s\,ds = 0.2$ cm².

@@ 3 | TOPIC | Motion: position, velocity and acceleration | s, v = s′, a = v′ = s″; speeding up vs slowing down; displacement vs distance | MUST
# Motion: position, velocity and acceleration

## 1. What it covers
- Velocity $v = s'(t)$ and acceleration $a = v'(t) = s''(t)$.
- **Speed** $= \lvert v\rvert$.
- When an object is moving left/right, at rest, **speeding up** or **slowing down**.
- Total distance vs displacement.

## 2. Where it is used
- **Physics and engineering:** vehicle dynamics, rockets, projectiles.
- **Game physics:** update loops compute position from velocity and velocity from acceleration each frame.
- **Sports science:** sprint analysis.
- **Robotics and drones:** motion planning with velocity and acceleration limits.

## 3. What it means
- $s(t)$: position (where it is).
- $v(t)$: velocity (how fast and which direction). $v > 0$ moving forward/right; $v < 0$ backward/left; $v = 0$ at rest (possibly turning around).
- $a(t)$: acceleration (how fast velocity changes).

**Speeding up vs slowing down:** compare signs. If $v$ and $a$ have the **same sign**, speed is increasing; **opposite signs**, it's decreasing. (Braking while going forward is negative acceleration with positive velocity.)

**Total distance:** add the distances of each leg between turning points (where $v = 0$).

## 4. Formula & worked example
$$
v(t) = s'(t) \qquad a(t) = v'(t) = s''(t) \qquad \text{speed} = \lvert v(t)\rvert
$$

**Example 1.** $s(t) = t^3 - 6t^2 + 9t$ (metres, $t \ge 0$).
- $v = 3t^2 - 12t + 9 = 3(t - 1)(t - 3)$; $a = 6t - 12$
- At rest at $t = 1$ and $t = 3$.
- Moving right on $[0, 1)$ and $(3, \infty)$; left on $(1, 3)$.

**Example 2 (speeding up?).** Same motion at $t = 2$: $v = -3$, $a = 0$ → neither. At $t = 2.5$: $v = -2.25$, $a = 3$ → opposite signs → slowing down.

**Example 3 (distance).** Total distance from $t = 0$ to $t = 4$:
- $s(0) = 0$, $s(1) = 4$, $s(3) = 0$, $s(4) = 4$ → $4 + 4 + 4 = 12$ m (displacement only 4 m).

**Example 4 (projectile).** $h(t) = -4.9t^2 + 19.6t + 2$: $v = -9.8t + 19.6$ → top at $t = 2$ s; $a = -9.8$ m/s² (gravity).

**Example 5 (code, game loop).**
```python
dt = 1 / 60              # one frame at 60 fps
pos, vel, acc = 0.0, 10.0, -9.8
for frame in range(60):  # simulate one second
    vel += acc * dt      # a = dv/dt  ->  dv ≈ a·dt
    pos += vel * dt      # v = ds/dt  ->  ds ≈ v·dt
print(round(pos, 2), round(vel, 2))   # ≈ 5.02, 0.2
```
Each frame uses the derivative relationships backwards: a small change in velocity is acceleration × time step, and a small change in position is velocity × time step. (Exact answer: $s = 10 - 4.9 = 5.1$, $v = 0.2$; the small difference is the error of this simple method.)

## 5. Common mistakes
- Treating displacement as total distance when the object turns around.
- Saying "negative acceleration means slowing down" (only if velocity is positive).
- Mixing up velocity (signed) and speed (unsigned).

## 6. Practice
Q: $s(t) = 4t^2 - 8t$. When is the object at rest?
A: $v = 8t - 8 = 0$ → $t = 1$.

Q: $v = -5$, $a = -2$. Speeding up or slowing down?
A: Speeding up (same signs).

Q: $s(t) = \sin t$. Acceleration?
A: $-\sin t$.

Q: Difference between distance and displacement?
A: Distance counts every metre travelled; displacement is the net change in position.

Q: $h(t) = -5t^2 + 20t$. Velocity at $t = 1$?
A: $10$ m/s.

@@ 3 | TOPIC | Related rates | Differentiating an equation with respect to time; ladders, balloons, cones, shadows, distances | MUST
# Related rates

## 1. What it covers
- Finding how fast one quantity changes from how fast a **related** quantity changes.
- The method: draw, write an equation linking the quantities, differentiate with respect to $t$, substitute, solve.
- Classic problems: sliding ladder, inflating balloon, filling cone, moving shadow, separating cars.

## 2. Where it is used
- **Engineering:** how fast the water level rises as a tank fills.
- **Aviation and radar:** how fast the distance to a plane is changing.
- **Medicine:** rate of change of a tumour's volume from its radius growth.
- **Economics:** how fast revenue changes as price and demand both change over time.

## 3. What it means
When two quantities are linked by an equation and both change over time, their **rates** are linked too. Differentiate the equation with respect to time $t$ (using the chain rule: $\frac{d}{dt}(x^2) = 2x\frac{dx}{dt}$).

**Method:**
1. Draw a picture; label quantities that change with variables.
2. Write down the given rate and the rate you want (with units).
3. Write an equation linking the variables (Pythagoras, volume formula, similar triangles...).
4. Differentiate both sides with respect to $t$.
5. **Only now** substitute the numbers for that instant, and solve.

## 4. Formula & worked example
**Example 1 (ladder).** A 10 m ladder slides down a wall; the bottom moves away at 1.5 m/s. How fast does the top slide down when the bottom is 6 m from the wall?
- $x^2 + y^2 = 100$ → $2x\frac{dx}{dt} + 2y\frac{dy}{dt} = 0$
- At $x = 6$: $y = 8$ → $6(1.5) + 8\frac{dy}{dt} = 0$ → $\frac{dy}{dt} = -1.125$ m/s (sliding down)

**Example 2 (balloon).** Air is pumped into a spherical balloon at 100 cm³/s. How fast is the radius growing when $r = 5$ cm?
- $V = \frac{4}{3}\pi r^3$ → $\frac{dV}{dt} = 4\pi r^2\frac{dr}{dt}$ → $100 = 100\pi\frac{dr}{dt}$ → $\frac{dr}{dt} = \frac{1}{\pi} \approx 0.318$ cm/s

**Example 3 (cone).** Water flows into a cone (tip down, radius 3 m, height 6 m) at 2 m³/min. How fast is the depth rising at depth 4 m?
- Similar triangles: $r = \frac{h}{2}$ → $V = \frac{1}{3}\pi\frac{h^2}{4}h = \frac{\pi h^3}{12}$
- $\frac{dV}{dt} = \frac{\pi h^2}{4}\frac{dh}{dt}$ → $2 = 4\pi\frac{dh}{dt}$ → $\frac{dh}{dt} = \frac{1}{2\pi} \approx 0.159$ m/min

**Example 4 (cars).** Car A drives north at 60 km/h, car B east at 80 km/h, from the same point. How fast is the distance between them growing after 1 hour?
- $z^2 = x^2 + y^2$ → $z\frac{dz}{dt} = x\frac{dx}{dt} + y\frac{dy}{dt}$; at 1 h: $x = 80$, $y = 60$, $z = 100$ → $100z' = 80(80) + 60(60)$ → $z' = 100$ km/h

## 5. Common mistakes
- Substituting numbers before differentiating (constants differentiate to 0 and the rate disappears).
- Wrong sign for decreasing quantities (a shrinking length has a negative rate).
- Forgetting to remove an extra variable with similar triangles first.

## 6. Practice
Q: A circle's radius grows at 2 cm/s. How fast is the area growing when $r = 10$?
A: $\frac{dA}{dt} = 2\pi r\frac{dr}{dt} = 40\pi$ cm²/s.

Q: A cube's edge grows at 0.5 cm/s. Rate of volume change when the edge is 4 cm?
A: $3s^2\frac{ds}{dt} = 24$ cm³/s.

Q: Ladder of 13 m, bottom moving out at 2 m/s. Rate of the top when the bottom is 5 m out?
A: $5(2) + 12y' = 0$ → $y' = -\frac{5}{6}$ m/s.

Q: Why must you differentiate before substituting the instant's values?
A: Substituting first turns changing quantities into constants, losing their rates.

Q: $V = \pi r^2h$ with $r$ fixed at 2. Water enters at 8 m³/min. Rate of rise?
A: $\frac{dh}{dt} = \frac{8}{4\pi} = \frac{2}{\pi}$ m/min.

@@ 3 | TOPIC | The mean value theorem | Statement, geometric meaning, Rolle's theorem, applications | MUST
# The mean value theorem

## 1. What it covers
- **Rolle's theorem** and the **mean value theorem (MVT)**.
- Geometric meaning: a tangent parallel to the secant.
- Finding the value $c$; applications (speed cameras, bounding functions).

## 2. Where it is used
- **Speed enforcement:** average-speed cameras over a stretch of motorway: if your average speed was 110 km/h, the MVT guarantees you were going exactly 110 km/h at some moment.
- **Analysis:** proving that a function with zero derivative is constant, and that $f' > 0$ means increasing.
- **Numerical analysis:** error bounds for approximations (Taylor remainder).

## 3. What it means
**MVT:** if $f$ is continuous on $[a, b]$ and differentiable on $(a, b)$, there is at least one point $c$ in between where the **instantaneous** rate equals the **average** rate:
$$
f'(c) = \frac{f(b) - f(a)}{b - a}
$$
Geometrically: somewhere the tangent is parallel to the secant joining the endpoints.

**Rolle's theorem** (special case): if $f(a) = f(b)$, there's a point with $f'(c) = 0$ (a flat tangent). If you leave home and come back, at some moment your velocity was zero (or you turned around).

## 4. Formula & worked example
$$
f'(c) = \frac{f(b) - f(a)}{b - a} \text{ for some } c \in (a, b)
$$

**Example 1.** $f(x) = x^2$ on $[1, 3]$.
- Average rate $\frac{9 - 1}{2} = 4$; $f'(c) = 2c = 4$ → $c = 2$.

**Example 2.** $f(x) = x^3 - 3x$ on $[0, 2]$.
- Average $\frac{2 - 0}{2} = 1$; $3c^2 - 3 = 1$ → $c = \frac{2}{\sqrt{3}} \approx 1.155$ (in the interval ✓).

**Example 3 (Rolle).** $f(x) = x^2 - 4x$ on $[0, 4]$: $f(0) = f(4) = 0$; $f'(c) = 2c - 4 = 0$ → $c = 2$.

**Example 4 (speeding).** A car passes two cameras 30 km apart in 15 minutes. Must it have exceeded 100 km/h?
- Average speed $= 120$ km/h → by the MVT, at some instant it was exactly 120 km/h → yes.

**Example 5 (MVT fails).** $f(x) = \lvert x\rvert$ on $[-1, 1]$: average rate 0, but $f'$ is never 0 (not differentiable at 0, so the theorem doesn't apply).

## 5. Common mistakes
- Forgetting to check the conditions (continuity and differentiability).
- Giving a $c$ outside the open interval.
- Thinking the MVT tells you how to find $c$ in practice (it guarantees existence only).

## 6. Practice
Q: State the MVT.
A: If $f$ is continuous on $[a, b]$ and differentiable on $(a, b)$, some $c$ has $f'(c) = \frac{f(b) - f(a)}{b - a}$.

Q: Find $c$ for $f(x) = x^2 + 2x$ on $[0, 2]$.
A: Average 4; $2c + 2 = 4$ → $c = 1$.

Q: What is Rolle's theorem?
A: If $f(a) = f(b)$ (plus the conditions), some $c$ has $f'(c) = 0$.

Q: A runner covers 10 km in 50 minutes. What does the MVT guarantee?
A: At some instant they ran exactly 12 km/h.

Q: Does the MVT apply to $\frac{1}{x}$ on $[-1, 1]$?
A: No, it's not continuous at 0.

@@ 2 | MODULE | Analysing functions and optimisation | Extrema, increasing/decreasing, concavity, curve sketching, optimisation, L'Hôpital, numerical methods | MUST
Derivatives tell you the shape of a graph without plotting hundreds of points.

@@ 3 | TOPIC | Critical points and absolute extrema | Critical numbers, extreme value theorem, closed interval method | MUST
# Critical points and absolute extrema

## 1. What it covers
- **Critical points:** where $f'(x) = 0$ or $f'(x)$ is undefined.
- **Absolute** (global) vs **relative** (local) maxima and minima.
- The **extreme value theorem**.
- The **closed interval method** for absolute extrema.

## 2. Where it is used
- **Engineering:** maximum stress in a beam; peak load.
- **Business:** maximum profit and minimum cost within production limits.
- **Physics:** highest point of a projectile.
- **Machine learning:** training seeks the minimum of a loss function; critical points include minima, maxima and saddle points.

## 3. What it means
At a smooth peak or valley, the tangent is flat, so $f' = 0$. At a sharp corner, $f'$ is undefined. These **critical points** are the only interior places where a max or min can occur (but not every critical point is one: $x^3$ has $f'(0) = 0$ and no extremum).

**Extreme value theorem:** a continuous function on a **closed** interval $[a, b]$ always has an absolute max and an absolute min.

**Closed interval method:**
1. Find the critical points inside $(a, b)$.
2. Evaluate $f$ at those points **and at the endpoints**.
3. The largest value is the absolute max; the smallest is the absolute min.

## 4. Formula & worked example
**Example 1.** Critical points of $f(x) = x^3 - 12x$: $f'(x) = 3x^2 - 12 = 0$ → $x = \pm 2$.

**Example 2 (closed interval).** Absolute extrema of $f(x) = x^3 - 12x$ on $[-3, 5]$.
- Candidates: $f(-3) = 9$, $f(-2) = 16$, $f(2) = -16$, $f(5) = 65$
- Absolute max 65 at $x = 5$; absolute min $-16$ at $x = 2$.

**Example 3 (undefined derivative).** $f(x) = x^{2/3}$ on $[-1, 8]$: $f'(x) = \frac{2}{3}x^{-1/3}$, undefined at 0.
- $f(-1) = 1$, $f(0) = 0$, $f(8) = 4$ → max 4, min 0.

**Example 4.** $f(x) = xe^{-x}$ on $[0, 3]$: $f' = e^{-x}(1 - x) = 0$ → $x = 1$.
- $f(0) = 0$, $f(1) = e^{-1} \approx 0.368$, $f(3) = 3e^{-3} \approx 0.149$ → max $\frac{1}{e}$ at $x = 1$, min 0 at $x = 0$.

## 5. Common mistakes
- Forgetting to check the endpoints.
- Missing critical points where $f'$ is undefined.
- Assuming every critical point is a max or min.

## 6. Practice
Q: Define a critical point.
A: An interior point where $f'(x) = 0$ or $f'(x)$ is undefined.

Q: Critical points of $f(x) = x^2 - 6x + 1$?
A: $x = 3$.

Q: Absolute max and min of $f(x) = x^2$ on $[-1, 3]$?
A: Max 9 at $x = 3$; min 0 at $x = 0$.

Q: What does the extreme value theorem guarantee?
A: A continuous function on a closed interval has an absolute max and min.

Q: Is $x = 0$ an extremum of $f(x) = x^3$?
A: No, though it is a critical point.

@@ 3 | TOPIC | Increasing, decreasing and the first derivative test | Sign charts of f′, classifying critical points as local max, min or neither | MUST
# Increasing, decreasing and the first derivative test

## 1. What it covers
- $f' > 0$ → increasing; $f' < 0$ → decreasing.
- **Sign charts** for $f'$.
- The **first derivative test**: classifying critical points by how the sign of $f'$ changes.

## 2. Where it is used
- **Business analytics:** where profit is rising or falling with price.
- **Biology:** when a population is growing or shrinking.
- **Engineering:** detecting peaks in signals.
- **Optimisation algorithms:** they move in the direction where the function decreases (gradient descent).

## 3. What it means
If the slope is positive, the graph goes up; if negative, down.

**Sign chart:** mark the critical points on a number line; test one value in each interval to find the sign of $f'$.

**First derivative test** at a critical point $c$:
- $f'$ changes from $+$ to $-$ → **local maximum** (up, then down: a hilltop).
- $f'$ changes from $-$ to $+$ → **local minimum** (a valley).
- No sign change → neither (a flat spot, like $x^3$ at 0).

## 4. Formula & worked example
**Example 1.** $f(x) = x^3 - 3x^2 - 9x + 5$.
- $f'(x) = 3x^2 - 6x - 9 = 3(x - 3)(x + 1)$ → critical points $-1, 3$.
- Signs: $x < -1$: $+$; $-1 < x < 3$: $-$; $x > 3$: $+$.
- Increasing on $(-\infty, -1)$ and $(3, \infty)$; decreasing on $(-1, 3)$.
- Local max at $x = -1$: $f(-1) = 10$; local min at $x = 3$: $f(3) = -22$.

**Example 2.** $f(x) = x^4 - 4x^3$: $f' = 4x^2(x - 3)$; critical points 0 and 3.
- Signs: $x < 0$: $-$; $0 < x < 3$: $-$; $x > 3$: $+$ → $x = 0$ neither; $x = 3$ local min ($f(3) = -27$).

**Example 3.** $f(x) = \frac{x}{x^2 + 1}$: $f' = \frac{1 - x^2}{(x^2 + 1)^2}$ → critical points $\pm 1$; $-$ then $+$ then $-$ → local min at $-1$ ($-\frac{1}{2}$), local max at 1 ($\frac{1}{2}$).

**Example 4 (profit).** $P(x) = -2x^2 + 120x - 1000$: $P' = -4x + 120$, $+$ before 30, $-$ after → maximum profit at 30 units.

## 5. Common mistakes
- Using the sign of $f$ instead of $f'$.
- Forgetting points where $f'$ is undefined (or where $f$ itself is undefined) when making the sign chart.
- Concluding "max" or "min" without checking the sign change.

## 6. Practice
Q: $f'(x) > 0$ on an interval means?
A: $f$ is increasing there.

Q: $f'$ changes from $-$ to $+$ at $c$. What is at $c$?
A: A local minimum.

Q: Intervals of increase for $f(x) = x^2 - 4x$?
A: $(2, \infty)$.

Q: Local extrema of $f(x) = x^3 - 3x$?
A: Max $2$ at $x = -1$; min $-2$ at $x = 1$.

Q: $f'$ doesn't change sign at a critical point. Conclusion?
A: No local extremum there.

@@ 3 | TOPIC | Concavity, inflection points and the second derivative test | f″ > 0 concave up, f″ < 0 concave down, inflection points, classifying with f″ | MUST
# Concavity, inflection points and the second derivative test

## 1. What it covers
- **Concave up** ($f'' > 0$, like a cup) and **concave down** ($f'' < 0$, like a cap).
- **Inflection points**: where concavity changes.
- The **second derivative test** for local extrema.

## 2. Where it is used
- **Economics:** diminishing returns: output still rises but more slowly (concave down); the inflection point is where returns start diminishing.
- **Epidemiology:** the inflection point of an outbreak curve is where new cases per day peak; after it, growth slows.
- **Finance:** convexity of bond prices.
- **Optimisation:** a convex (concave up) loss function has one minimum, which makes training reliable.

## 3. What it means
$f''$ tells you how the slope is changing:
- $f'' > 0$: the slope is increasing → the graph bends **upward** (cup-shaped, holds water).
- $f'' < 0$: the slope is decreasing → it bends **downward** (cap-shaped).
- An **inflection point** is where the bending switches direction ($f''$ changes sign).

**Second derivative test** at a critical point $c$ with $f'(c) = 0$:
- $f''(c) > 0$ → local **min** (at the bottom of a cup).
- $f''(c) < 0$ → local **max** (at the top of a cap).
- $f''(c) = 0$ → inconclusive (use the first derivative test).

## 4. Formula & worked example
**Example 1.** $f(x) = x^3 - 6x^2 + 9x$.
- $f' = 3x^2 - 12x + 9$; $f'' = 6x - 12$
- $f'' < 0$ for $x < 2$ (concave down), $> 0$ for $x > 2$ (concave up) → inflection at $(2, 2)$.

**Example 2 (second derivative test).** Same function: critical points $x = 1, 3$.
- $f''(1) = -6 < 0$ → local max $f(1) = 4$; $f''(3) = 6 > 0$ → local min $f(3) = 0$.

**Example 3 (inconclusive).** $f(x) = x^4$: $f'(0) = 0$, $f''(0) = 0$ → inconclusive; the first derivative test shows a minimum.

**Example 4.** $f(x) = xe^{-x}$: $f'' = e^{-x}(x - 2)$ → inflection at $x = 2$.

**Example 5 (epidemic).** Cumulative cases $C(t)$: while $C'' > 0$, daily new cases are rising; after the inflection ($C'' < 0$), daily cases fall, even though the total keeps growing.

## 5. Common mistakes
- Calling every point with $f'' = 0$ an inflection point ($x^4$ at 0 isn't one; check the sign change).
- Mixing up the test: $f'' > 0$ means **minimum**.
- Using the second derivative test when $f'(c) \ne 0$.

## 6. Practice
Q: $f''(x) < 0$ on an interval means?
A: Concave down.

Q: Inflection point of $f(x) = x^3$?
A: $(0, 0)$.

Q: $f'(2) = 0$ and $f''(2) = 5$. Conclusion?
A: Local minimum at $x = 2$.

Q: Concavity of $e^x$?
A: Concave up everywhere ($f'' = e^x > 0$).

Q: What does the inflection point of a cumulative-cases curve represent?
A: The moment daily new cases peak.

@@ 3 | TOPIC | Curve sketching | Combining domain, intercepts, symmetry, asymptotes, f′ and f″ into a sketch | MUST
# Curve sketching

## 1. What it covers
- A full checklist for sketching $y = f(x)$ by hand.
- Combining domain, intercepts, symmetry, asymptotes, increasing/decreasing, extrema, concavity and inflection points.

## 2. Where it is used
- **Checking computer output:** knowing the expected shape lets you catch bugs in plots and models.
- **Exams and interviews.**
- **Modelling:** understanding a function's behaviour before using it (e.g. an activation function, a cost curve).

## 3. What it means
**Checklist:**
1. **Domain** (and any excluded points).
2. **Intercepts** ($x = 0$ and $f(x) = 0$).
3. **Symmetry** (even/odd).
4. **Asymptotes** (vertical, horizontal or slant).
5. **$f'$:** critical points, increasing/decreasing, local extrema.
6. **$f''$:** concavity, inflection points.
7. Plot the key points and join them with the right shape.

## 4. Formula & worked example
**Example.** Sketch $f(x) = \frac{x^2}{x^2 - 1}$.
1. Domain: $x \ne \pm 1$.
2. Intercept: $(0, 0)$.
3. Even ($f(-x) = f(x)$): symmetric about the y-axis.
4. VA $x = \pm 1$; HA $y = 1$.
5. $f'(x) = \frac{2x(x^2 - 1) - x^2(2x)}{(x^2 - 1)^2} = \frac{-2x}{(x^2 - 1)^2}$: positive for $x < 0$, negative for $x > 0$ → increasing on $(-\infty, -1)$ and $(-1, 0)$, decreasing on $(0, 1)$ and $(1, \infty)$; local max at $(0, 0)$.
6. $f''(x) = \frac{6x^2 + 2}{(x^2 - 1)^3}$: sign follows $(x^2 - 1)^3$ → concave up for $\lvert x\rvert > 1$, concave down for $\lvert x\rvert < 1$; no inflection points (the change happens at asymptotes).
7. Shape: a cap between $-1$ and 1 peaking at the origin and diving to $-\infty$ at the asymptotes; outside, two branches coming down from $+\infty$ and approaching $y = 1$ from above.

```mermaid
xychart-beta
    title "y = x²/(x² − 1), right half (x > 1 branch)"
    x-axis "x" [1.1, 1.25, 1.5, 2, 3, 4, 6]
    y-axis "y" 0 --> 6
    line [5.76, 2.78, 1.8, 1.33, 1.13, 1.07, 1.03]
```

## 5. Common mistakes
- Skipping asymptotes and drawing the curve across them.
- Getting concavity signs wrong because of the denominator's sign.
- Calling a concavity change at an asymptote an inflection point (the function isn't defined there).

## 6. Practice
Q: First three items on the curve-sketching checklist?
A: Domain, intercepts, symmetry.

Q: How do you find intervals of increase?
A: Where $f'(x) > 0$.

Q: $f(x) = x^3 - 3x$: local max, min, inflection?
A: Max $(-1, 2)$, min $(1, -2)$, inflection $(0, 0)$.

Q: Horizontal asymptote of $\frac{2x^2}{x^2 + 3}$?
A: $y = 2$.

Q: Can an inflection point occur at a vertical asymptote?
A: No, the function must be defined there.

@@ 3 | TOPIC | Optimisation | Setting up and solving max/min problems: geometry, cost, revenue, distance | MUST
# Optimisation

## 1. What it covers
- Turning a word problem into a function of **one variable** to maximise or minimise.
- Using a **constraint** to remove extra variables.
- Finding and justifying the optimum (closed interval method or derivative tests).

## 2. Where it is used
- **Packaging:** least material for a given volume (cans, boxes).
- **Business:** profit-maximising price, cost-minimising order quantity (the EOQ model in inventory management).
- **Engineering:** strongest beam, fastest route, minimum energy.
- **Machine learning:** every model is trained by minimising a loss function; this topic is the single-variable version.
- **Physics:** light takes the path of least time (Snell's law follows from optimisation).

## 3. What it means
**Method:**
1. Draw and label. Identify the quantity to optimise (the **objective**).
2. Write the objective as a formula.
3. Use the **constraint** (fixed perimeter, volume, budget...) to write it in terms of **one** variable.
4. Find the domain (physically sensible values).
5. Differentiate, set equal to 0, solve.
6. Justify max/min (endpoints, first or second derivative test).
7. Answer the question asked, with units.

## 4. Formula & worked example
**Example 1 (fencing).** 1200 m of fence encloses a rectangle along a river (no fence on the river side). Maximise the area.
- $2x + y = 1200$ → $A = x(1200 - 2x) = 1200x - 2x^2$
- $A' = 1200 - 4x = 0$ → $x = 300$, $y = 600$; $A'' = -4 < 0$ → max $= 180\,000$ m²

**Example 2 (open box).** Cut equal squares from the corners of a 30 × 30 cm sheet and fold up the sides. Maximise the volume.
- $V = x(30 - 2x)^2$, $0 < x < 15$
- $V' = (30 - 2x)^2 - 4x(30 - 2x) = (30 - 2x)(30 - 6x) = 0$ → $x = 5$ (or 15, which gives zero volume)
- $V(5) = 5 \times 400 = 2000$ cm³

**Example 3 (can).** A can must hold 500 cm³. Minimise the metal (surface area).
- $\pi r^2h = 500$ → $h = \frac{500}{\pi r^2}$; $S = 2\pi r^2 + 2\pi rh = 2\pi r^2 + \frac{1000}{r}$
- $S' = 4\pi r - \frac{1000}{r^2} = 0$ → $r^3 = \frac{250}{\pi}$ → $r \approx 4.30$ cm, $h \approx 8.60$ cm ($h = 2r$: height equals diameter)

**Example 4 (closest point).** Point on $y = \sqrt{x}$ closest to $(3, 0)$.
- Minimise $D^2 = (x - 3)^2 + x$: derivative $2(x - 3) + 1 = 0$ → $x = 2.5$ → point $(2.5, \sqrt{2.5})$

**Example 5 (pricing).** Demand $q = 400 - 20p$. Revenue-maximising price?
- $R = p(400 - 20p)$, $R' = 400 - 40p = 0$ → $p = 10$

## 5. Common mistakes
- Not using the constraint, leaving two variables.
- Ignoring the domain (e.g. negative lengths, or endpoints that give the true optimum).
- Answering with $x$ when the question asks for the area or the cost.

## 6. Practice
Q: Two positive numbers sum to 20. Maximise their product.
A: $10$ and $10$ (product 100).

Q: A rectangle has area 100. Minimise its perimeter.
A: A $10 \times 10$ square (perimeter 40).

Q: $C(x) = x^2 - 40x + 500$. Minimum cost?
A: At $x = 20$: $C = 100$.

Q: Why must you check endpoints in optimisation?
A: The optimum may occur at the boundary of the allowed values.

Q: Profit $P(x) = -x^3 + 12x^2 + 60x$ for $0 \le x \le 15$. Best $x$?
A: $P' = -3x^2 + 24x + 60 = 0$ → $x = 10$ ($P = 1600$); endpoints $P(0) = 0$, $P(15) = 225$ → $x = 10$.

@@ 3 | TOPIC | L'Hôpital's rule | Indeterminate forms 0/0 and ∞/∞; rewriting 0·∞, ∞ − ∞, 1^∞, 0⁰, ∞⁰ | MUST
# L'Hôpital's rule

## 1. What it covers
- **L'Hôpital's rule** for $\frac{0}{0}$ and $\frac{\infty}{\infty}$.
- Rewriting other indeterminate forms ($0 \cdot \infty$, $\infty - \infty$, $1^\infty$, $0^0$, $\infty^0$) into a fraction.
- Using logs for exponential forms.

## 2. Where it is used
- **Comparing growth rates:** proving $\frac{x^n}{e^x} \to 0$ and $\frac{\ln x}{x} \to 0$ (used in algorithm analysis).
- **Physics and engineering limits:** e.g. behaviour of formulas at resonance.
- **Finance:** deriving continuous compounding $\left(1 + \frac{r}{n}\right)^n \to e^r$.
- **Statistics:** limits in probability distributions.

## 3. What it means
When $\frac{f}{g}$ gives $\frac{0}{0}$ or $\frac{\infty}{\infty}$, compare **how fast** $f$ and $g$ change instead: the limit of $\frac{f}{g}$ equals the limit of $\frac{f'}{g'}$ (if that exists). Near the point, both functions behave like their tangent lines, so the ratio of values approaches the ratio of slopes.

**Important:** differentiate the top and bottom **separately**; this is not the quotient rule.

**Other forms:**
- $0 \cdot \infty$: rewrite $fg = \frac{f}{1/g}$.
- $\infty - \infty$: combine into one fraction.
- $1^\infty$, $0^0$, $\infty^0$: let $y = f^g$, take $\ln y = g\ln f$, find its limit $L$, then the answer is $e^L$.

## 4. Formula & worked example
$$
\lim\frac{f(x)}{g(x)} = \lim\frac{f'(x)}{g'(x)} \quad \text{for } \frac{0}{0} \text{ or } \frac{\infty}{\infty}
$$

**Example 1.** $\lim_{x \to 0}\frac{e^x - 1}{x}$ ($\frac{0}{0}$) $= \lim\frac{e^x}{1} = 1$

**Example 2.** $\lim_{x \to \infty}\frac{x^2}{e^x}$ ($\frac{\infty}{\infty}$) $= \lim\frac{2x}{e^x} = \lim\frac{2}{e^x} = 0$ (applied twice)

**Example 3.** $\lim_{x \to 0}\frac{1 - \cos x}{x^2} = \lim\frac{\sin x}{2x} = \frac{1}{2}$

**Example 4 ($0 \cdot \infty$).** $\lim_{x \to 0^+} x\ln x = \lim\frac{\ln x}{1/x} = \lim\frac{1/x}{-1/x^2} = \lim(-x) = 0$

**Example 5 ($1^\infty$).** $\lim_{n \to \infty}\left(1 + \frac{r}{n}\right)^n$
- $\ln y = n\ln\left(1 + \frac{r}{n}\right) = \frac{\ln(1 + r/n)}{1/n}$ → L'Hôpital → $\frac{\frac{-r/n^2}{1 + r/n}}{-1/n^2} = \frac{r}{1 + r/n} \to r$ → $y \to e^r$

**Example 6 ($0^0$).** $\lim_{x \to 0^+} x^x$: $\ln y = x\ln x \to 0$ → $y \to e^0 = 1$

## 5. Common mistakes
- Using the rule when the form isn't $\frac{0}{0}$ or $\frac{\infty}{\infty}$ (e.g. $\frac{1}{0}$).
- Applying the quotient rule instead of differentiating top and bottom separately.
- Forgetting to exponentiate at the end for $1^\infty$-type limits.

## 6. Practice
Q: $\lim_{x \to 0}\frac{\sin 3x}{x}$ with L'Hôpital?
A: $\lim\frac{3\cos 3x}{1} = 3$.

Q: $\lim_{x \to \infty}\frac{\ln x}{\sqrt{x}}$?
A: $\lim\frac{1/x}{1/(2\sqrt{x})} = \lim\frac{2}{\sqrt{x}} = 0$.

Q: $\lim_{x \to 1}\frac{\ln x}{x - 1}$?
A: $1$.

Q: Which forms does L'Hôpital's rule handle directly?
A: $\frac{0}{0}$ and $\frac{\infty}{\infty}$.

Q: $\lim_{x \to 0}(1 + x)^{1/x}$?
A: $e$.

@@ 3 | TOPIC | Newton's method and gradient descent | Root finding with tangent lines; minimising functions by stepping downhill; code | SHOULD
# Newton's method and gradient descent

## 1. What it covers
- **Newton's method**: finding roots with tangent lines, $x_{n+1} = x_n - \frac{f(x_n)}{f'(x_n)}$.
- **Gradient descent** (1D): minimising a function by stepping against the slope, $x_{n+1} = x_n - \eta f'(x_n)$.
- Convergence behaviour, learning rate, and when the methods fail.

## 2. Where it is used
- **Computers:** square roots and division in hardware and libraries use Newton-type iterations.
- **Machine learning:** **gradient descent** (and variants like Adam) trains almost every neural network.
- **Engineering and finance:** solving equations with no algebraic solution (e.g. the internal rate of return).
- **Optimisation solvers:** Newton's method on $f'$ finds minima quickly.

## 3. What it means
**Newton's method:** start with a guess. Draw the tangent line there and see where it crosses the x-axis; that's a better guess. Repeat. Near a root it converges extremely fast (the number of correct digits roughly doubles each step).

**Gradient descent:** imagine standing on a hillside in fog, wanting to reach the valley. Feel the slope under your feet and take a step downhill; repeat. The step size is the **learning rate** $\eta$:
- too small → very slow;
- too big → you overshoot and may bounce around or diverge.

Gradient descent finds a **local** minimum; with several valleys, where you end up depends on where you start.

## 4. Formula & worked example
$$
\text{Newton: } x_{n+1} = x_n - \frac{f(x_n)}{f'(x_n)} \qquad \text{Gradient descent: } x_{n+1} = x_n - \eta\,f'(x_n)
$$

**Example 1 (Newton for $\sqrt{2}$).** $f(x) = x^2 - 2$, $f'(x) = 2x$, start $x_0 = 1$.
- $x_1 = 1 - \frac{-1}{2} = 1.5$; $x_2 = 1.5 - \frac{0.25}{3} = 1.41667$; $x_3 \approx 1.414216$ (true $1.414214$)

**Example 2 (code: Newton).**
```python
def newton(f, df, x, steps=6):
    for _ in range(steps):
        x = x - f(x) / df(x)       # jump to where the tangent hits zero
    return x
print(newton(lambda x: x**3 - 2*x - 5, lambda x: 3*x**2 - 2, 2.0))   # 2.0945514815...
```
Each step uses the tangent line at the current guess. For this cubic, six steps give about 15 correct digits.

**Example 3 (gradient descent by hand).** Minimise $f(x) = (x - 3)^2$, $f'(x) = 2(x - 3)$, $\eta = 0.25$, start $x_0 = 0$.
- $x_1 = 0 - 0.25(-6) = 1.5$; $x_2 = 1.5 - 0.25(-3) = 2.25$; $x_3 = 2.625$; ... → 3 (each step halves the distance).

**Example 4 (code: gradient descent).**
```python
def grad_descent(df, x, lr=0.01, steps=100):
    for _ in range(steps):
        x = x - lr * df(x)         # step against the slope
    return x
print(grad_descent(lambda x: 4*x**3 - 9*x**2, x=4.0))   # ≈ 2.25 (minimum of x⁴ − 3x³)
```
The derivative of $x^4 - 3x^3$ is $4x^3 - 9x^2$, which is zero at $x = 2.25$ (a minimum). Try `lr=0.1` to see it blow up (an overflow error after a few steps): a learning rate that's too large makes the steps overshoot.

## 5. Common mistakes
- Newton: starting where $f'(x_0) = 0$ (division by zero) or far from the root (may wander off).
- Gradient descent: adding the gradient instead of subtracting it (that climbs uphill).
- Picking a learning rate that is too large.

## 6. Practice
Q: Newton's method formula?
A: $x_{n+1} = x_n - \frac{f(x_n)}{f'(x_n)}$.

Q: One Newton step for $f(x) = x^2 - 5$ from $x_0 = 2$?
A: $2 - \frac{-1}{4} = 2.25$.

Q: Gradient descent update?
A: $x_{n+1} = x_n - \eta f'(x_n)$.

Q: What happens if the learning rate is too large?
A: Steps overshoot the minimum and may diverge.

Q: Why might gradient descent end in different places from different starts?
A: It finds a local minimum, and there may be several.

@@ 3 | TOPIC | Practice & review: Applications of derivatives | Mixed problem set with worked answers | MUST
# Practice & review: Applications of derivatives

**How to use this:** 60 minutes; calculator allowed.

## Problems
1. Use linear approximation to estimate $\sqrt[3]{27.6}$.
2. $s(t) = t^3 - 9t^2 + 24t$ ($t \ge 0$). When is the particle at rest? When is it speeding up?
3. A 5 m ladder slides; the top falls at 0.6 m/s. How fast does the bottom move when the top is 3 m high?
4. Find $c$ from the MVT for $f(x) = \sqrt{x}$ on $[1, 9]$.
5. Absolute extrema of $f(x) = 2x^3 - 3x^2 - 12x + 4$ on $[-2, 3]$.
6. For $f(x) = x^4 - 4x^3$, find intervals of concavity and inflection points.
7. A farmer has 600 m of fencing to make a rectangular pen divided into two equal parts by a fence parallel to one side. Maximise the area.
8. $\lim_{x \to 0}\frac{x - \sin x}{x^3}$
9. Do two Newton steps for $f(x) = x^3 - 10$ starting from $x_0 = 2$.

## Answers
1. $f(x) = x^{1/3}$, $f'(27) = \frac{1}{27}$ → $3 + \frac{0.6}{27} \approx 3.0222$.
2. $v = 3t^2 - 18t + 24 = 3(t - 2)(t - 4)$ → rest at $t = 2, 4$. $a = 6t - 18$. Speeding up when $v$ and $a$ share a sign: $(2, 3)$ (both negative) and $(4, \infty)$ (both positive).
3. $x^2 + y^2 = 25$, $y = 3$ → $x = 4$; $4x' + 3(-0.6) = 0$ → $x' = 0.45$ m/s.
4. Average rate $\frac{3 - 1}{8} = \frac{1}{4}$; $\frac{1}{2\sqrt{c}} = \frac{1}{4}$ → $c = 4$.
5. $f' = 6(x - 2)(x + 1)$; $f(-2) = 0$, $f(-1) = 11$, $f(2) = -16$, $f(3) = -5$ → max 11, min $-16$.
6. $f'' = 12x^2 - 24x = 12x(x - 2)$ → up on $(-\infty, 0)$ and $(2, \infty)$, down on $(0, 2)$; inflection at $(0, 0)$ and $(2, -16)$.
7. $3x + 2y = 600$ (3 fences of length $x$) → $A = x\cdot\frac{600 - 3x}{2}$ → $A' = 300 - 3x = 0$ → $x = 100$, $y = 150$, $A = 15\,000$ m².
8. L'Hôpital three times: $\frac{1 - \cos x}{3x^2} \to \frac{\sin x}{6x} \to \frac{1}{6}$.
9. $x_1 = 2 - \frac{-2}{12} \approx 2.1667$; $x_2 = 2.1667 - \frac{0.1713}{14.083} \approx 2.1545$ (true $\sqrt[3]{10} \approx 2.1544$).

## Flashcards
Q: Steps for related rates?
A: Draw, write an equation linking the variables, differentiate with respect to time, then substitute and solve.

Q: Second derivative test: $f'(c) = 0$, $f''(c) < 0$?
A: Local maximum.

Q: Closed interval method?
A: Evaluate $f$ at the critical points and the endpoints; pick the largest and smallest.

Q: L'Hôpital's rule?
A: For $\frac{0}{0}$ or $\frac{\infty}{\infty}$: $\lim\frac{f}{g} = \lim\frac{f'}{g'}$.
