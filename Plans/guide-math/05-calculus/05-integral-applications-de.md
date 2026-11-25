@@ 1 | MODULE | Phase 5 — Applications of Integrals and Differential Equations (Applied) | Net change, average value, area between curves, volumes, arc length, parametric/polar calculus, slope fields, Euler's method, separable and logistic equations | SHOULD
# Phase 5: Applications of integrals and differential equations

Integrals compute totals of anything that can be sliced into small pieces: areas, volumes, lengths, work, mass, probability. This phase also introduces **differential equations**, equations involving derivatives, which describe how systems evolve: populations, cooling coffee, charging batteries, spreading diseases. (The Multivariable Calculus & Differential Equations roadmap continues with more methods.)

@@ 2 | MODULE | Applications of integration | Net change, average value, areas, volumes, arc length, parametric and polar curves | SHOULD
The pattern is always the same: slice, approximate one slice, add the slices with an integral.

@@ 3 | TOPIC | Net change, motion and average value | ∫ of a rate = net change, displacement vs distance, average value of a function | SHOULD
# Net change, motion and average value

## 1. What it covers
- **Net change theorem**: integrating a rate gives the total change.
- **Displacement** $\int v\,dt$ vs **total distance** $\int\lvert v\rvert\,dt$.
- **Average value** of a function: $\frac{1}{b - a}\int_a^b f(x)\,dx$.

## 2. Where it is used
- **Physics:** displacement and distance from velocity data.
- **Economics:** total cost from marginal cost; total revenue over a period.
- **Engineering:** average power, average temperature, average flow rate.
- **Data analysis:** the average of a continuous signal (e.g. average CPU load over an hour).

## 3. What it means
If $r(t)$ is the rate at which something changes, then $\int_a^b r(t)\,dt$ is the **total change** over that time. Water flowing in at $r(t)$ litres per minute: the integral is the total litres added.

**Displacement vs distance:** if velocity goes negative (moving backwards), the integral subtracts that motion. To get total distance, integrate the **absolute value** (split at the times where $v = 0$).

**Average value:** the height of the rectangle with the same area as the region under the curve, like levelling out a sand pile into a flat layer of the same volume.

## 4. Formula & worked example
$$
\int_a^b F'(t)\,dt = F(b) - F(a) \qquad f_{\text{avg}} = \frac{1}{b - a}\int_a^b f(x)\,dx
$$

**Example 1.** Water flows into a tank at $r(t) = 20 - 2t$ L/min for $0 \le t \le 10$. Total water added?
- $\int_0^{10}(20 - 2t)\,dt = [20t - t^2]_0^{10} = 100$ L

**Example 2 (displacement vs distance).** $v(t) = t^2 - 4t + 3$ m/s on $[0, 3]$.
- Displacement $= \left[\frac{t^3}{3} - 2t^2 + 3t\right]_0^3 = 0$ m
- $v < 0$ on $(1, 3)$: distance $= \int_0^1 v\,dt - \int_1^3 v\,dt = \frac{4}{3} + \frac{4}{3} = \frac{8}{3}$ m

**Example 3 (average value).** Average of $f(x) = x^2$ on $[0, 3]$: $\frac{1}{3}\cdot 9 = 3$.

**Example 4 (temperature).** $T(t) = 20 + 6\sin\left(\frac{\pi t}{12}\right)$ over a day ($0 \le t \le 24$): average $= 20$ °C (the sine part averages to zero over a full period).

## 5. Common mistakes
- Using displacement when total distance is asked (or vice versa).
- Forgetting to divide by $b - a$ for the average.
- Integrating a quantity instead of its rate.

## 6. Practice
Q: Integral of a rate of change over $[a, b]$ gives?
A: The net change in the quantity.

Q: Average value of $f(x) = 2x$ on $[0, 4]$?
A: $4$.

Q: $v(t) = 3t^2$ m/s. Displacement from $t = 0$ to $2$?
A: $8$ m.

Q: How do you get total distance from velocity?
A: Integrate $\lvert v(t)\rvert$ (split where $v$ changes sign).

Q: Marginal cost $C'(x) = 50 - 0.1x$. Cost of increasing production from 100 to 200 units?
A: $\int_{100}^{200}(50 - 0.1x)\,dx = 5000 - 1500 = 3500$.

@@ 3 | TOPIC | Area between curves | Top minus bottom, finding intersections, integrating with respect to y | SHOULD
# Area between curves

## 1. What it covers
- Area between two curves: $\int_a^b[\text{top} - \text{bottom}]\,dx$.
- Finding the limits from intersection points.
- Curves that cross (split the integral).
- Integrating with respect to $y$ (right minus left).

## 2. Where it is used
- **Economics:** consumer and producer surplus are areas between demand/supply curves and the price line.
- **Statistics:** the Lorenz curve and the **Gini coefficient** (income inequality) use the area between a curve and the line $y = x$.
- **Machine learning:** AUC (area under the ROC curve) to evaluate classifiers.
- **Engineering:** cross-sectional areas of irregular shapes.

## 3. What it means
Slice the region into thin vertical strips. Each strip has height (top curve − bottom curve) and width $dx$. Add them up with an integral.

**Steps:**
1. Sketch the curves.
2. Find where they intersect (the limits).
3. Decide which is on top in each interval (test a point).
4. Integrate top − bottom; if they cross, split the interval.

Sometimes horizontal strips are easier: $\int_c^d[\text{right} - \text{left}]\,dy$.

## 4. Formula & worked example
$$
A = \int_a^b\big[f(x) - g(x)\big]\,dx \quad (f \ge g \text{ on } [a, b])
$$

**Example 1.** Area between $y = x^2$ and $y = x + 2$.
- Intersections: $x^2 = x + 2$ → $x = -1, 2$. Top: $x + 2$.
- $\int_{-1}^{2}(x + 2 - x^2)\,dx = \left[\frac{x^2}{2} + 2x - \frac{x^3}{3}\right]_{-1}^{2} = \frac{10}{3} + \frac{7}{6} = \frac{9}{2}$

**Example 2 (crossing).** Area between $y = \sin x$ and $y = \cos x$ on $[0, \frac{\pi}{2}]$.
- They cross at $\frac{\pi}{4}$. $\int_0^{\pi/4}(\cos x - \sin x)\,dx + \int_{\pi/4}^{\pi/2}(\sin x - \cos x)\,dx = (\sqrt{2} - 1) + (\sqrt{2} - 1) = 2\sqrt{2} - 2 \approx 0.828$

**Example 3 (with respect to y).** Area between $x = y^2$ and $x = 4$.
- $\int_{-2}^{2}(4 - y^2)\,dy = \left[4y - \frac{y^3}{3}\right]_{-2}^{2} = \frac{32}{3}$

**Example 4 (consumer surplus).** Demand $p = 100 - 2q$, market price 60 (so $q = 20$).
- $\int_0^{20}(100 - 2q - 60)\,dq = [40q - q^2]_0^{20} = 400$

## 5. Common mistakes
- Subtracting in the wrong order (negative area).
- Not splitting when the curves cross (areas cancel).
- Using x-limits with a $dy$ integral.

## 6. Practice
Q: Area between $y = x$ and $y = x^2$ on $[0, 1]$?
A: $\int_0^1(x - x^2)dx = \frac{1}{6}$.

Q: Area between $y = 4 - x^2$ and the x-axis?
A: $\int_{-2}^{2}(4 - x^2)dx = \frac{32}{3}$.

Q: What do you integrate for vertical strips?
A: Top curve minus bottom curve.

Q: Area between $y = e^x$ and $y = 1$ on $[0, 1]$?
A: $e - 2$.

Q: Why split the integral when curves cross?
A: Otherwise positive and negative parts cancel.

@@ 3 | TOPIC | Volumes of solids of revolution: disks and washers | Rotating regions about an axis, disk method, washer method | SHOULD
# Volumes of solids of revolution: disks and washers

## 1. What it covers
- Volume by **disks**: $V = \pi\int R(x)^2\,dx$.
- Volume by **washers** (hollow solids): $V = \pi\int(R^2 - r^2)\,dx$.
- Rotating about the x-axis, the y-axis or another horizontal/vertical line.

## 2. Where it is used
- **Manufacturing:** volumes of lathe-turned parts, bottles, vases, rocket nose cones.
- **Engineering:** tank capacities with curved profiles.
- **3D printing and CAD:** material volume of rotational parts.
- **Medicine:** estimating organ volumes from cross-sectional profiles.

## 3. What it means
Rotating a region around an axis creates a solid. Slice it perpendicular to the axis: each slice is a thin **disk** (a coin) of radius $R(x)$ and thickness $dx$, with volume $\pi R^2\,dx$. Add them up.

If there's a hole (the region doesn't touch the axis), each slice is a **washer** (a coin with a hole): outer radius $R$, inner radius $r$, volume $\pi(R^2 - r^2)\,dx$.

**Radius = distance from the curve to the axis of rotation.** For an axis like $y = -1$, the radius is $f(x) - (-1) = f(x) + 1$.

## 4. Formula & worked example
$$
V_{\text{disk}} = \pi\int_a^b [R(x)]^2\,dx \qquad V_{\text{washer}} = \pi\int_a^b\left([R(x)]^2 - [r(x)]^2\right)dx
$$

**Example 1 (cone check).** Rotate $y = \frac{r}{h}x$, $0 \le x \le h$, about the x-axis.
- $V = \pi\int_0^h\frac{r^2}{h^2}x^2\,dx = \pi\frac{r^2}{h^2}\cdot\frac{h^3}{3} = \frac{1}{3}\pi r^2h$ ✓ (the cone formula)

**Example 2 (sphere check).** Rotate $y = \sqrt{r^2 - x^2}$ about the x-axis.
- $V = \pi\int_{-r}^{r}(r^2 - x^2)\,dx = \pi\left[r^2x - \frac{x^3}{3}\right]_{-r}^{r} = \frac{4}{3}\pi r^3$ ✓

**Example 3.** Rotate $y = \sqrt{x}$, $0 \le x \le 4$, about the x-axis: $\pi\int_0^4 x\,dx = 8\pi$.

**Example 4 (washer).** Region between $y = x$ and $y = x^2$, rotated about the x-axis.
- $R = x$, $r = x^2$ on $[0, 1]$: $\pi\int_0^1(x^2 - x^4)\,dx = \pi\left(\frac{1}{3} - \frac{1}{5}\right) = \frac{2\pi}{15}$

**Example 5 (other axis).** Rotate $y = x^2$, $0 \le x \le 1$, about $y = -1$ (region between the curve and the x-axis).
- $R = x^2 + 1$, $r = 1$: $\pi\int_0^1\left((x^2 + 1)^2 - 1\right)dx = \pi\int_0^1(x^4 + 2x^2)\,dx = \pi\left(\frac{1}{5} + \frac{2}{3}\right) = \frac{13\pi}{15}$

## 5. Common mistakes
- Using $(R - r)^2$ instead of $R^2 - r^2$.
- Forgetting $\pi$.
- Measuring the radius from the x-axis when the axis of rotation is a different line.

## 6. Practice
Q: Disk method formula?
A: $V = \pi\int[R(x)]^2dx$.

Q: Rotate $y = x$, $0 \le x \le 3$, about the x-axis. Volume?
A: $\pi\int_0^3x^2dx = 9\pi$.

Q: Rotate $y = e^x$, $0 \le x \le 1$, about the x-axis.
A: $\pi\int_0^1e^{2x}dx = \frac{\pi}{2}(e^2 - 1)$.

Q: When do you use washers instead of disks?
A: When the region doesn't touch the axis, leaving a hole.

Q: Radius for rotating $y = f(x)$ about $y = 2$ (curve below the line)?
A: $2 - f(x)$.

@@ 3 | TOPIC | Volumes by cylindrical shells and known cross sections | Shell method, choosing disks vs shells, solids with square/triangular/semicircular cross sections | SHOULD
# Volumes by cylindrical shells and known cross sections

## 1. What it covers
- The **shell method**: $V = 2\pi\int(\text{radius})(\text{height})\,dx$.
- When shells are easier than washers.
- Volumes of solids with **known cross sections** (not rotational): $V = \int A(x)\,dx$.

## 2. Where it is used
- **Engineering:** volumes of pipes, tubes and rotated profiles where solving for $x$ in terms of $y$ is hard.
- **Architecture:** volumes of buildings and structures with varying cross sections.
- **Geology and civil engineering:** earthwork volumes from cross-section surveys (cut and fill for roads).
- **Medical imaging:** organ volume = sum of cross-sectional areas × slice thickness (exactly $\int A(x)\,dx$).

## 3. What it means
**Shells:** instead of slicing perpendicular to the axis, take thin strips **parallel** to the axis and spin them. Each becomes a thin cylindrical shell (like a toilet paper tube). Unroll it: a thin rectangular sheet with length $2\pi r$ (circumference), height $h$ and thickness $dx$. Volume $\approx 2\pi rh\,dx$.

**Choosing:** use whichever gives simpler integrals; rotating about the y-axis with $y = f(x)$ usually suits shells.

**Known cross sections:** if you know the area $A(x)$ of each slice (squares, triangles, semicircles built on a base region), the volume is $\int A(x)\,dx$, a generalisation of Cavalieri's principle.

## 4. Formula & worked example
$$
V_{\text{shell}} = 2\pi\int_a^b r(x)\,h(x)\,dx \qquad V = \int_a^b A(x)\,dx
$$

**Example 1 (shells).** Rotate the region under $y = x^2$, $0 \le x \le 2$, about the **y-axis**.
- Radius $x$, height $x^2$: $V = 2\pi\int_0^2 x^3\,dx = 2\pi\cdot 4 = 8\pi$

**Example 2 (shells, hard with washers).** Rotate $y = x - x^3$, $0 \le x \le 1$, about the y-axis.
- $V = 2\pi\int_0^1 x(x - x^3)\,dx = 2\pi\left(\frac{1}{3} - \frac{1}{5}\right) = \frac{4\pi}{15}$ (washers would need to solve a cubic for $x$)

**Example 3 (square cross sections).** The base is the region between $y = \sqrt{x}$ and the x-axis for $0 \le x \le 4$; cross sections perpendicular to the x-axis are squares.
- Side $\sqrt{x}$ → $A = x$ → $V = \int_0^4 x\,dx = 8$

**Example 4 (semicircle cross sections).** Base: the disk $x^2 + y^2 \le 4$; cross sections perpendicular to the x-axis are semicircles.
- Diameter $2\sqrt{4 - x^2}$ → radius $\sqrt{4 - x^2}$ → $A = \frac{\pi}{2}(4 - x^2)$
- $V = \frac{\pi}{2}\int_{-2}^{2}(4 - x^2)\,dx = \frac{\pi}{2}\cdot\frac{32}{3} = \frac{16\pi}{3}$

## 5. Common mistakes
- Using the shell formula with radius and height swapped.
- Forgetting the $2\pi$.
- Using the full width as the radius of a semicircle (it's the diameter).

## 6. Practice
Q: Shell method formula?
A: $2\pi\int r\,h\,dx$.

Q: Rotate the region under $y = x$, $0 \le x \le 1$, about the y-axis (shells).
A: $2\pi\int_0^1x^2dx = \frac{2\pi}{3}$.

Q: Volume formula for known cross sections?
A: $\int A(x)\,dx$.

Q: Base $0 \le x \le 2$ under $y = x$, square cross sections. Volume?
A: $\int_0^2x^2dx = \frac{8}{3}$.

Q: When are shells usually easier?
A: When rotating about the y-axis a region given as $y = f(x)$ that is hard to solve for $x$.

@@ 3 | TOPIC | Arc length and surface area | Length of a curve, ∫√(1 + f′²) dx, surface area of revolution | SHOULD
# Arc length and surface area

## 1. What it covers
- **Arc length** of $y = f(x)$: $L = \int_a^b\sqrt{1 + [f'(x)]^2}\,dx$.
- Why it works (Pythagoras on tiny pieces).
- **Surface area** of a solid of revolution.
- Numerical arc length for curves without nice antiderivatives.

## 2. Where it is used
- **Engineering:** length of cables (power lines hang as catenaries), pipes, roads and roller-coaster tracks.
- **Computer graphics and fonts:** length of Bézier curves for animation timing (constant-speed motion along a path).
- **Manufacturing:** material needed for curved parts; area of curved surfaces to paint or plate.
- **Robotics:** path length for motion planning.

## 3. What it means
Zoom into a tiny piece of the curve: it's almost straight, with horizontal change $dx$ and vertical change $dy = f'(x)\,dx$. By Pythagoras its length is
$$
\sqrt{dx^2 + dy^2} = \sqrt{1 + (f'(x))^2}\,dx
$$
Adding all the pieces gives the arc length.

Most arc length integrals can't be done by hand, so they're computed numerically.

**Surface area of revolution:** each tiny piece sweeps out a thin band of circumference $2\pi f(x)$ and slant width $ds$.

## 4. Formula & worked example
$$
L = \int_a^b\sqrt{1 + [f'(x)]^2}\,dx \qquad S = 2\pi\int_a^b f(x)\sqrt{1 + [f'(x)]^2}\,dx
$$

**Example 1 (check with a line).** $y = 2x$, $0 \le x \le 3$: $L = \int_0^3\sqrt{1 + 4}\,dx = 3\sqrt{5}$ (matches the distance formula).

**Example 2.** $y = \frac{2}{3}x^{3/2}$, $0 \le x \le 3$: $f' = x^{1/2}$ → $L = \int_0^3\sqrt{1 + x}\,dx = \frac{2}{3}\left[(1 + x)^{3/2}\right]_0^3 = \frac{2}{3}(8 - 1) = \frac{14}{3}$

**Example 3 (numerical).** Length of $y = x^2$ from 0 to 1.
```python
import math
from scipy.integrate import quad
L, _ = quad(lambda x: math.sqrt(1 + (2 * x) ** 2), 0, 1)
print(L)    # 1.4789428575...
```
The integrand $\sqrt{1 + 4x^2}$ comes straight from the formula with $f'(x) = 2x$.

**Example 4 (surface area check).** Sphere: rotate $y = \sqrt{r^2 - x^2}$ about the x-axis.
- $f\sqrt{1 + f'^2} = \sqrt{r^2 - x^2}\cdot\frac{r}{\sqrt{r^2 - x^2}} = r$ → $S = 2\pi\int_{-r}^{r} r\,dx = 4\pi r^2$ ✓

## 5. Common mistakes
- Forgetting to square $f'(x)$ or to add 1.
- Integrating $\sqrt{1 + f'(x)}$.
- Expecting a closed-form answer for every curve.

## 6. Practice
Q: Arc length formula?
A: $\int_a^b\sqrt{1 + [f'(x)]^2}\,dx$.

Q: Which theorem is behind the formula?
A: Pythagoras (on tiny pieces of the curve).

Q: Arc length of $y = 5$ from $x = 0$ to $x = 7$?
A: $7$.

Q: Arc length of $y = \frac{3}{4}x$ from 0 to 4?
A: $4 \cdot \frac{5}{4} = 5$.

Q: Why are arc lengths often computed numerically?
A: $\sqrt{1 + f'^2}$ rarely has an elementary antiderivative.

@@ 3 | TOPIC | Calculus with parametric, polar and vector-valued functions | dy/dx for parametric curves, polar slopes and area, velocity/acceleration vectors, arc length in t | NICE
# Calculus with parametric, polar and vector-valued functions

## 1. What it covers
- **Parametric derivatives:** $\frac{dy}{dx} = \frac{dy/dt}{dx/dt}$; second derivatives.
- **Parametric arc length.**
- **Polar area:** $\frac{1}{2}\int r^2\,d\theta$.
- **Vector-valued functions:** position $\vec{r}(t)$, velocity $\vec{v} = \vec{r}'$, acceleration, speed.

## 2. Where it is used
- **Physics:** projectile and orbital motion in 2D/3D.
- **Animation and games:** objects moving along parametric paths need velocity (for facing direction) and speed (for timing).
- **Robotics and drones:** trajectories as vector functions.
- **Engineering:** cam profiles and polar antenna patterns.

## 3. What it means
**Parametric curves** $x(t)$, $y(t)$: the slope is "how fast $y$ changes" divided by "how fast $x$ changes", both per unit of time: $\frac{dy}{dx} = \frac{y'(t)}{x'(t)}$.

**Vector functions:** $\vec{r}(t) = \langle x(t), y(t)\rangle$ is the position. Differentiate each component:
- velocity $\vec{v}(t) = \langle x'(t), y'(t)\rangle$ (points along the path);
- **speed** $= \lvert\vec{v}\rvert = \sqrt{x'^2 + y'^2}$;
- acceleration $\vec{a} = \vec{v}'$.

**Arc length** in $t$: integrate the speed.

**Polar area:** a thin wedge of angle $d\theta$ and radius $r$ is almost a sector with area $\frac{1}{2}r^2\,d\theta$.

## 4. Formula & worked example
$$
\frac{dy}{dx} = \frac{dy/dt}{dx/dt} \qquad L = \int_a^b\sqrt{x'(t)^2 + y'(t)^2}\,dt \qquad A_{\text{polar}} = \frac{1}{2}\int_\alpha^\beta r^2\,d\theta
$$

**Example 1.** $x = t^2$, $y = t^3 - 3t$. Slope at $t = 2$?
- $\frac{dy}{dx} = \frac{3t^2 - 3}{2t} = \frac{9}{4}$

**Example 2 (circle).** $x = 3\cos t$, $y = 3\sin t$: speed $= \sqrt{9\sin^2 t + 9\cos^2 t} = 3$; length over $[0, 2\pi]$: $6\pi$ ✓

**Example 3 (projectile).** $\vec{r}(t) = \langle 20t,\ 15t - 4.9t^2\rangle$.
- $\vec{v} = \langle 20,\ 15 - 9.8t\rangle$; at $t = 1$: speed $= \sqrt{400 + 27.04} \approx 20.7$ m/s; $\vec{a} = \langle 0, -9.8\rangle$.

**Example 4 (polar area).** Area inside the cardioid $r = 1 + \cos\theta$.
- $\frac{1}{2}\int_0^{2\pi}(1 + \cos\theta)^2\,d\theta = \frac{1}{2}\int_0^{2\pi}\left(1 + 2\cos\theta + \frac{1 + \cos 2\theta}{2}\right)d\theta = \frac{1}{2}(2\pi + 0 + \pi) = \frac{3\pi}{2}$

**Example 5 (one rose petal).** $r = \cos 2\theta$ has a petal for $-\frac{\pi}{4} \le \theta \le \frac{\pi}{4}$: $\frac{1}{2}\int_{-\pi/4}^{\pi/4}\cos^2 2\theta\,d\theta = \frac{\pi}{8}$.

## 5. Common mistakes
- Computing $\frac{dx}{dy}$ instead of $\frac{dy}{dx}$.
- Forgetting the $\frac{1}{2}$ in polar area, or using $r$ instead of $r^2$.
- Confusing velocity (a vector) with speed (its length).

## 6. Practice
Q: Slope of $x = 2t$, $y = t^2$ at $t = 3$?
A: $\frac{2t}{2} = 3$.

Q: Speed for $\vec{r}(t) = \langle\cos t, \sin t\rangle$?
A: $1$.

Q: Polar area formula?
A: $\frac{1}{2}\int r^2\,d\theta$.

Q: Area inside $r = 2$?
A: $\frac{1}{2}\int_0^{2\pi}4\,d\theta = 4\pi$.

Q: Velocity of $\vec{r}(t) = \langle t^2, 3t\rangle$?
A: $\langle 2t, 3\rangle$.

@@ 2 | MODULE | Introduction to differential equations | Modelling, slope fields, Euler's method, separable equations, exponential and logistic growth | SHOULD
A differential equation describes a rule for change; solving it gives the function that follows the rule.

@@ 3 | TOPIC | Differential equations: modelling, slope fields and Euler's method | Writing DEs from words, verifying solutions, slope fields, numerical solutions | SHOULD
# Differential equations: modelling, slope fields and Euler's method

## 1. What it covers
- What a **differential equation** (DE) is; order; general vs particular solutions.
- Turning descriptions into DEs ("the rate is proportional to ...").
- Verifying a proposed solution.
- **Slope fields** for visualising solutions.
- **Euler's method** for approximate numerical solutions.

## 2. Where it is used
- **Physics and engineering:** Newton's second law $F = ma$ is a DE; circuits, vibrations, heat flow.
- **Biology and epidemiology:** population and disease models (SIR models).
- **Finance:** continuously compounded growth $\frac{dA}{dt} = rA$.
- **Simulation and games:** physics engines solve DEs step by step, exactly like Euler's method.

## 3. What it means
A DE relates a function to its derivatives. Instead of telling you **what** $y$ is, it tells you **how $y$ changes**. "The population grows at a rate proportional to its size" becomes $\frac{dP}{dt} = kP$.

**Solution:** a function that makes the equation true. There's usually a family of solutions (a constant $C$); an **initial condition** like $P(0) = 100$ picks one.

**Slope field:** at many points $(t, y)$, draw a short line with slope given by the DE. Solution curves follow the arrows, like leaves floating on a stream.

**Euler's method:** start at the initial point and take small steps along the slope: $y_{n+1} = y_n + h \cdot f(t_n, y_n)$. Smaller steps give better accuracy.

## 4. Formula & worked example
$$
\frac{dy}{dt} = f(t, y) \qquad \text{Euler: } y_{n+1} = y_n + h\,f(t_n, y_n),\quad t_{n+1} = t_n + h
$$

**Example 1 (modelling).** A cup of tea cools at a rate proportional to the difference between its temperature and the room's (22°C) → $\frac{dT}{dt} = -k(T - 22)$ (Newton's law of cooling).

**Example 2 (verify).** Is $y = Ce^{3t}$ a solution of $y' = 3y$? $y' = 3Ce^{3t} = 3y$ ✓

**Example 3 (Euler by hand).** $y' = y$, $y(0) = 1$, step $h = 0.5$; estimate $y(1)$.
- $y_1 = 1 + 0.5(1) = 1.5$; $y_2 = 1.5 + 0.5(1.5) = 2.25$ (true value $e \approx 2.718$; smaller steps get closer)

**Example 4 (code).**
```python
def euler(f, t, y, h, steps):
    for _ in range(steps):
        y = y + h * f(t, y)    # follow the slope for one small step
        t = t + h
    return y
for n in (2, 10, 1000):
    print(n, euler(lambda t, y: y, 0, 1, 1 / n, n))
# 2 2.25 / 10 2.5937 / 1000 2.7169  -> approaching e = 2.71828
```
Each step moves along the tangent line for a short time `h`. With 1000 steps the estimate of $y(1) = e$ is accurate to about 3 digits; this is the same loop a game uses to move objects every frame.

**Example 5 (slope field reading).** For $y' = y(2 - y)$: slopes are 0 on the lines $y = 0$ and $y = 2$; positive between them; so solutions starting between 0 and 2 rise towards 2.

## 5. Common mistakes
- Confusing the DE with its solution.
- Forgetting to update $t$ in Euler's method when $f$ depends on $t$.
- Using a large step size and trusting the result.

## 6. Practice
Q: Write a DE: the rate of growth of $N$ is proportional to $N$.
A: $\frac{dN}{dt} = kN$.

Q: Is $y = x^2$ a solution of $xy' = 2y$?
A: $x(2x) = 2x^2 = 2y$ ✓ yes.

Q: One Euler step for $y' = t + y$, $y(0) = 1$, $h = 0.1$?
A: $y_1 = 1 + 0.1(0 + 1) = 1.1$.

Q: What does a slope field show?
A: The slope of the solution through each point, so you can see the shape of solutions.

Q: How does Euler's method become more accurate?
A: By using a smaller step size.

@@ 3 | TOPIC | Separable equations; exponential and logistic models | Separating variables, initial value problems, exponential growth/decay, Newton's cooling, logistic growth | SHOULD
# Separable equations; exponential and logistic models

## 1. What it covers
- **Separable** DEs: $\frac{dy}{dx} = g(x)h(y)$.
- Solving initial value problems.
- **Exponential growth/decay** $y' = ky$; **Newton's law of cooling**.
- **Logistic growth** $P' = kP\left(1 - \frac{P}{K}\right)$ with carrying capacity $K$.

## 2. Where it is used
- **Biology and ecology:** populations limited by resources (logistic).
- **Epidemiology:** early spread is exponential; with saturation, logistic.
- **Marketing and technology:** product adoption curves (S-curves).
- **Physics:** radioactive decay, cooling, RC circuits.
- **Machine learning:** the logistic function $\frac{1}{1 + e^{-x}}$ comes from the logistic equation.

## 3. What it means
**Separable:** move all $y$ terms (with $dy$) to one side and all $x$ terms (with $dx$) to the other, then integrate both sides:
$$
\frac{dy}{h(y)} = g(x)\,dx \Rightarrow \int\frac{dy}{h(y)} = \int g(x)\,dx
$$

**Exponential:** $y' = ky$ gives $y = y_0e^{kt}$: growth ($k > 0$) or decay ($k < 0$).

**Logistic:** growth is nearly exponential when $P$ is small, but slows as $P$ approaches the **carrying capacity** $K$, giving an S-shaped curve. Growth is fastest at $P = \frac{K}{2}$ (the inflection point).

## 4. Formula & worked example
$$
y' = ky \Rightarrow y = y_0e^{kt} \qquad T' = -k(T - T_s) \Rightarrow T = T_s + (T_0 - T_s)e^{-kt} \qquad P = \frac{K}{1 + Ae^{-kt}},\ A = \frac{K - P_0}{P_0}
$$

**Example 1 (separable).** $\frac{dy}{dx} = \frac{x}{y}$, $y(0) = 2$.
- $y\,dy = x\,dx$ → $\frac{y^2}{2} = \frac{x^2}{2} + C$ → $y^2 = x^2 + 4$ → $y = \sqrt{x^2 + 4}$

**Example 2.** $\frac{dy}{dx} = 2xy$, $y(0) = 3$.
- $\frac{dy}{y} = 2x\,dx$ → $\ln\lvert y\rvert = x^2 + C$ → $y = 3e^{x^2}$

**Example 3 (decay).** A drug decays with $k = 0.2$ per hour from 50 mg: $A = 50e^{-0.2t}$; half-life $= \frac{\ln 2}{0.2} \approx 3.47$ h.

**Example 4 (cooling).** Tea at 90°C in a 22°C room cools to 70°C in 5 minutes. When does it reach 50°C?
- $70 = 22 + 68e^{-5k}$ → $e^{-5k} = \frac{48}{68}$ → $k \approx 0.0697$
- $50 = 22 + 68e^{-kt}$ → $t = \frac{\ln(68/28)}{0.0697} \approx 12.7$ min

**Example 5 (logistic).** A town's app has $K = 10\,000$ potential users, starts with 100, and $k = 0.5$ per week.
- $A = 99$ → $P(t) = \frac{10\,000}{1 + 99e^{-0.5t}}$; half the market ($P = 5000$) when $99e^{-0.5t} = 1$ → $t = 2\ln 99 \approx 9.2$ weeks.

## 5. Common mistakes
- Forgetting $+C$ (or adding it on both sides; one is enough).
- Losing the absolute value or sign when exponentiating.
- Applying the initial condition before integrating.

## 6. Practice
Q: Solve $y' = -3y$, $y(0) = 10$.
A: $y = 10e^{-3t}$.

Q: Solve $\frac{dy}{dx} = y^2$, $y(0) = 1$.
A: $-\frac{1}{y} = x - 1$ → $y = \frac{1}{1 - x}$.

Q: In logistic growth, when is growth fastest?
A: When $P = \frac{K}{2}$.

Q: Half-life for $y' = -ky$?
A: $\frac{\ln 2}{k}$.

Q: What does the carrying capacity $K$ represent?
A: The long-run maximum population the environment supports.

@@ 3 | TOPIC | Practice & review: Applications of integrals and DEs | Mixed problem set with worked answers | SHOULD
# Practice & review: Applications of integrals and DEs

**How to use this:** 60 minutes; calculator allowed.

## Problems
1. $v(t) = 6 - 2t$ m/s on $[0, 5]$. Find displacement and total distance.
2. Average value of $f(x) = \sin x$ on $[0, \pi]$.
3. Area between $y = x^2 - 4$ and $y = 2x - 1$.
4. Volume when the region under $y = \frac{1}{x}$, $1 \le x \le 3$, is rotated about the x-axis.
5. Volume when the region between $y = \sqrt{x}$ and $y = x$ is rotated about the y-axis (shells).
6. Arc length of $y = \ln(\cos x)$ for $0 \le x \le \frac{\pi}{4}$.
7. Slope of the parametric curve $x = e^t$, $y = t^2$ at $t = 1$.
8. Use Euler's method with $h = 0.5$ to estimate $y(1)$ for $y' = 2t - y$, $y(0) = 1$.
9. Solve $\frac{dy}{dx} = \frac{y}{x}$, $y(1) = 3$.
10. A population follows $P' = 0.3P(1 - P/500)$, $P(0) = 50$. Find $P(t)$ and $\lim_{t \to \infty} P(t)$.

## Answers
1. Displacement $[6t - t^2]_0^5 = 5$ m. $v = 0$ at $t = 3$: distance $= 9 + 4 = 13$ m.
2. $\frac{1}{\pi}[-\cos x]_0^\pi = \frac{2}{\pi}$.
3. Intersections $x^2 - 2x - 3 = 0$ → $x = -1, 3$; $\int_{-1}^{3}(2x - 1 - x^2 + 4)\,dx = \int_{-1}^{3}(-x^2 + 2x + 3)\,dx = \frac{32}{3}$.
4. $\pi\int_1^3\frac{1}{x^2}dx = \pi\left(1 - \frac{1}{3}\right) = \frac{2\pi}{3}$.
5. $2\pi\int_0^1 x(\sqrt{x} - x)\,dx = 2\pi\left(\frac{2}{5} - \frac{1}{3}\right) = \frac{2\pi}{15}$.
6. $f' = -\tan x$ → $\sqrt{1 + \tan^2 x} = \sec x$ → $\int_0^{\pi/4}\sec x\,dx = \ln(\sec x + \tan x)\Big|_0^{\pi/4} = \ln(\sqrt{2} + 1) \approx 0.881$.
7. $\frac{2t}{e^t} = \frac{2}{e}$.
8. $y_1 = 1 + 0.5(0 - 1) = 0.5$; $y_2 = 0.5 + 0.5(1 - 0.5) = 0.75$.
9. $\ln\lvert y\rvert = \ln\lvert x\rvert + C$ → $y = 3x$.
10. $A = 9$: $P(t) = \frac{500}{1 + 9e^{-0.3t}}$; limit 500.

## Flashcards
Q: Area between curves formula?
A: $\int_a^b(\text{top} - \text{bottom})\,dx$.

Q: Washer method?
A: $\pi\int(R^2 - r^2)\,dx$.

Q: Euler's method update?
A: $y_{n+1} = y_n + h\,f(t_n, y_n)$.

Q: Solution of $y' = ky$?
A: $y = y_0e^{kt}$.
