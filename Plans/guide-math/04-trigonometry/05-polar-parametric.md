@@ 1 | MODULE | Phase 5 — Polar, Parametric and Applied Trigonometry (Applied) | Polar coordinates and curves, parametric equations, simple harmonic motion | SHOULD
# Phase 5: Polar, parametric and applied trigonometry

Trigonometry also gives us **new ways to describe position and motion**:
- **Polar coordinates** describe a point by *distance and direction* instead of x and y.
- **Parametric equations** describe a path by giving x and y as functions of time.
- **Harmonic motion** uses sine waves to model anything that vibrates.

These lead directly into calculus, physics, vectors and complex numbers.

**What you need first:** Phases 1–2 (unit circle, graphs) and Phase 4's identities for the polar ⇄ Cartesian conversions.

@@ 2 | MODULE | Polar and parametric | Polar coordinates, polar curves, parametric equations, harmonic motion | SHOULD
Each topic here is a different way of using $(\cos\theta, \sin\theta)$.

@@ 3 | TOPIC | Polar coordinates | (r, θ) points, converting between polar and Cartesian coordinates and equations | SHOULD
# Polar coordinates

## 1. What it covers
- Describing a point as $(r, \theta)$: distance $r$ from the origin and angle $\theta$ from the positive x-axis.
- Converting points between polar and Cartesian $(x, y)$.
- Converting equations, e.g. $r = 4\cos\theta$ into a circle equation.
- Negative $r$ and the many names of a single point.

## 2. Where it is used
- **Radar and sonar:** a target is detected at a distance and a bearing, which is polar data.
- **Robotics and lidar:** a self-driving car's lidar returns (angle, distance) pairs; converting them to x/y builds the map.
- **Games:** aiming systems, radial menus, circular explosion patterns, orbiting objects.
- **Complex numbers and electronics:** a complex number in polar form $re^{i\theta}$ makes multiplication easy (multiply the lengths, add the angles).
- **Antenna design:** radiation patterns are drawn in polar plots.

## 3. What it means
Cartesian coordinates say "go $x$ right and $y$ up". Polar coordinates say "**face** angle $\theta$ and **walk** distance $r$".

From the unit circle idea: walking distance $r$ at angle $\theta$ puts you at $x = r\cos\theta$, $y = r\sin\theta$. Going the other way, $r$ is the distance (Pythagoras) and $\theta$ is the direction (arctangent, with the quadrant checked).

**One point, many names:** $(2, \frac{\pi}{6})$, $(2, \frac{13\pi}{6})$ and $(-2, \frac{7\pi}{6})$ are the same point. A **negative** $r$ means "face $\theta$, then walk **backwards**".

**Analogy:** giving directions in a field: "the tree is 50 m away, towards 2 o'clock" (polar) versus "30 m east and 40 m north" (Cartesian). Both find the same tree.

## 4. Formula & worked example
$$
x = r\cos\theta \qquad y = r\sin\theta \qquad r^2 = x^2 + y^2 \qquad \tan\theta = \frac{y}{x}
$$
For $\theta$, use `atan2(y, x)` or find the reference angle and place it in the right quadrant.

**Example 1: polar → Cartesian.** $\left(6, \frac{2\pi}{3}\right)$
- $x = 6\cos\frac{2\pi}{3} = 6\left(-\frac{1}{2}\right) = -3$
- $y = 6\sin\frac{2\pi}{3} = 6\cdot\frac{\sqrt{3}}{2} = 3\sqrt{3}$
- Point: $(-3, 3\sqrt{3})$

**Example 2: Cartesian → polar.** $(-2, -2)$
- $r = \sqrt{4 + 4} = 2\sqrt{2}$
- Reference angle $\tan^{-1}(1) = \frac{\pi}{4}$; the point is in QIII, so $\theta = \pi + \frac{\pi}{4} = \frac{5\pi}{4}$.
- Point: $\left(2\sqrt{2}, \frac{5\pi}{4}\right)$. (Plain $\tan^{-1}\frac{-2}{-2} = \frac{\pi}{4}$ would point the wrong way.)

**Example 3: equation polar → Cartesian.** $r = 4\cos\theta$
- Multiply by $r$: $r^2 = 4r\cos\theta$ → $x^2 + y^2 = 4x$
- Complete the square: $(x - 2)^2 + y^2 = 4$, a circle of radius 2 centred at $(2, 0)$.

**Example 4: equation Cartesian → polar.** $y = x$
- $r\sin\theta = r\cos\theta$ → $\tan\theta = 1$ → $\theta = \frac{\pi}{4}$ (a line through the origin).

**Example 5: lidar in code.** Convert scan readings to map points.
```python
import math
scan = [(0, 2.0), (45, 1.5), (90, 3.0)]          # (angle in degrees, distance in m)
points = [(d * math.cos(math.radians(a)), d * math.sin(math.radians(a))) for a, d in scan]
print([(round(x, 2), round(y, 2)) for x, y in points])   # [(2.0, 0.0), (1.06, 1.06), (0.0, 3.0)]
```
Each reading is a polar point $(r, \theta)$, converted with $x = r\cos\theta$, $y = r\sin\theta$. This is literally how a robot vacuum turns its laser scan into a floor map.

## 5. Common mistakes
- Using plain $\tan^{-1}\frac{y}{x}$ without checking the quadrant.
- Forgetting that $r$ can be negative, or that the same point has infinitely many polar names.
- Calculator in degree mode when $\theta$ is in radians.

## 6. Practice
Q: Convert $\left(4, \frac{\pi}{2}\right)$ to Cartesian.
A: $(0, 4)$.

Q: Convert $(1, -\sqrt{3})$ to polar with $0 \le \theta < 2\pi$.
A: $r = 2$, $\theta = \frac{5\pi}{3}$.

Q: What curve is $r = 5$?
A: A circle of radius 5 centred at the origin.

Q: Convert $x^2 + y^2 = 9$ to polar.
A: $r = 3$.

Q: Give another polar name for $\left(3, \frac{\pi}{4}\right)$ with negative $r$.
A: $\left(-3, \frac{5\pi}{4}\right)$.

@@ 3 | TOPIC | Graphing polar curves | Circles, roses, cardioids and limaçons, spirals; symmetry and plotting from a table | SHOULD
# Graphing polar curves

## 1. What it covers
- Plotting $r = f(\theta)$ from a table of values.
- The standard families: **circles**, **roses** $r = a\cos(n\theta)$, **cardioids and limaçons** $r = a + b\cos\theta$, and **spirals** $r = a\theta$.
- Recognising a curve from its equation (number of petals, inner loop or not).

## 2. Where it is used
- **Microphones and antennas:** a "cardioid microphone" is named after its heart-shaped polar pickup pattern; it hears the front and rejects the back.
- **Art and design:** roses and spirals in logos, generative art and spirograph-style animations.
- **Nature and engineering:** spirals appear in shells, galaxies, springs and the grooves of a vinyl record.
- **Calculus:** areas of these shapes are found by polar integration.

## 3. What it means
A polar equation says, **for each direction $\theta$, how far out the curve is**. Sweep $\theta$ from $0$ to $2\pi$ like a lighthouse beam and mark the point at distance $r$ along the beam each time.

**Analogy:** a lighthouse beam with a dimmer that changes as it turns. The pattern of how far the light reaches, traced all the way round, is the polar graph.

**Families:**
- $r = a$: circle at the origin. $r = 2a\cos\theta$ or $r = 2a\sin\theta$: circle through the origin.
- **Rose** $r = a\cos(n\theta)$ or $a\sin(n\theta)$: if $n$ is **odd**, there are $n$ petals; if $n$ is **even**, $2n$ petals. Each petal has length $a$.
- **Limaçon** $r = a + b\cos\theta$: if $a = b$ it is a **cardioid** (heart); if $a < b$ it has an **inner loop**; if $a > b$ it is a dimpled or rounded oval.
- **Archimedean spiral** $r = a\theta$: the distance grows steadily as it turns.

**Symmetry test:** if replacing $\theta$ by $-\theta$ gives the same equation (true for anything in $\cos\theta$), the curve is symmetric about the x-axis.

## 4. Formula & worked example
**Example 1: cardioid** $r = 2 + 2\cos\theta$. Make a table:
| $\theta$ | $0$ | $\frac{\pi}{2}$ | $\pi$ | $\frac{3\pi}{2}$ | $2\pi$ |
|---|---|---|---|---|---|
| $r$ | $4$ | $2$ | $0$ | $2$ | $4$ |
- It reaches furthest (4) to the right, passes through the origin at $\theta = \pi$ (the "dent" of the heart), and is symmetric about the x-axis (cosine).

**Example 2: rose** $r = 3\sin(2\theta)$.
- $n = 2$ is even → $4$ petals of length 3.
- The petals point along $\theta = \frac{\pi}{4}, \frac{3\pi}{4}, \frac{5\pi}{4}, \frac{7\pi}{4}$ (where $\lvert\sin 2\theta\rvert = 1$).

**Example 3: limaçon with a loop** $r = 1 + 2\cos\theta$.
- $a = 1 < b = 2$ → inner loop.
- $r = 0$ when $\cos\theta = -\frac{1}{2}$: $\theta = \frac{2\pi}{3}, \frac{4\pi}{3}$. Between these angles $r$ is negative, which draws the small inner loop.

**Example 4: rose** $r = 4\cos(3\theta)$.
- $n = 3$ is odd → 3 petals of length 4; one petal points along $\theta = 0$.

**In code:** draw any polar curve by converting to x/y.
```python
import math
pts = []
for i in range(361):
    t = math.radians(i)
    r = 3 * math.sin(2 * t)                          # 4-petal rose
    pts.append((r * math.cos(t), r * math.sin(t)))   # polar -> Cartesian
# pass pts to matplotlib's plot() or draw lines on a canvas
```
Changing one line (`r = ...`) draws any curve in this topic.

## 5. Common mistakes
- Petal count for even $n$: $r = \cos 2\theta$ has **4** petals, not 2.
- Ignoring negative $r$ values; they draw part of the curve (e.g. the inner loop).
- Plotting only $0$ to $\pi$. Many curves need the full $0$ to $2\pi$ (spirals need more).

## 6. Practice
Q: How many petals does $r = 5\cos(4\theta)$ have?
A: 8 ($n = 4$ is even, so $2n$).

Q: How many petals does $r = 2\sin(5\theta)$ have?
A: 5.

Q: What shape is $r = 3 + 3\sin\theta$?
A: A cardioid ($a = b$), symmetric about the y-axis.

Q: Does $r = 2 + 5\cos\theta$ have an inner loop?
A: Yes, because $a = 2 < b = 5$.

Q: What curve is $r = 6\sin\theta$?
A: A circle of radius 3 through the origin, centred at $(0, 3)$.

@@ 3 | TOPIC | Parametric equations | x = f(t), y = g(t); sketching with direction, eliminating the parameter, circles and projectiles | SHOULD
# Parametric equations

## 1. What it covers
- Describing a curve with a **parameter** $t$: $x = f(t)$, $y = g(t)$.
- Plotting with a table and showing the **direction** of motion.
- **Eliminating the parameter** to get an x-y equation.
- The two classics: circles and ellipses, and projectile motion.

## 2. Where it is used
- **Animation and games:** every moving object is parametric: its position at time $t$. Bézier curves in fonts, SVG and CSS easing are parametric.
- **Physics:** projectile paths, orbits, and the motion of any particle.
- **CNC machines and 3D printers:** the tool path is given as x(t), y(t), z(t).
- **Robotics:** planned trajectories for arms and drones.

## 3. What it means
A normal equation $y = f(x)$ tells you **where** the path is. A parametric pair also tells you **when** you're at each point, and in which **direction** you move.

**Analogy:** a map with a dotted route (the curve) versus a GPS track with timestamps (parametric). The GPS track also shows your speed and direction.

**Circle:** $x = r\cos t$, $y = r\sin t$ traces a circle anticlockwise as $t$ goes from $0$ to $2\pi$, since $x^2 + y^2 = r^2(\cos^2 t + \sin^2 t) = r^2$.

**Eliminating the parameter:**
- If one equation is easy to solve for $t$, solve it and substitute into the other.
- If both use sin and cos, isolate $\cos t$ and $\sin t$ and use $\cos^2 t + \sin^2 t = 1$.

## 4. Formula & worked example
$$
\text{circle: } x = h + r\cos t,\ y = k + r\sin t \qquad \text{ellipse: } x = a\cos t,\ y = b\sin t
$$
$$
\text{projectile: } x = (v\cos\theta)t,\quad y = (v\sin\theta)t - \tfrac{1}{2}gt^2
$$

**Example 1: line.** $x = 2t + 1$, $y = t - 3$. Eliminate $t$.
- From the second: $t = y + 3$. Substitute: $x = 2(y + 3) + 1 = 2y + 7$ → $y = \frac{x - 7}{2}$

**Example 2: ellipse.** $x = 4\cos t$, $y = 3\sin t$.
- $\cos t = \frac{x}{4}$, $\sin t = \frac{y}{3}$ → $\frac{x^2}{16} + \frac{y^2}{9} = 1$
- An ellipse 8 wide and 6 tall, traced anticlockwise starting at $(4, 0)$.

**Example 3: direction.** $x = \cos t$, $y = -\sin t$ for $0 \le t \le 2\pi$.
- $t = 0$: $(1, 0)$; $t = \frac{\pi}{2}$: $(0, -1)$. It goes **clockwise**, unlike the standard circle.

**Example 4: projectile.** A ball is thrown at 20 m/s at $30^\circ$ ($g = 9.8$).
- $x = 20\cos 30^\circ\, t \approx 17.32t$; $y = 20\sin 30^\circ\, t - 4.9t^2 = 10t - 4.9t^2$
- Lands when $y = 0$: $t(10 - 4.9t) = 0$ → $t \approx 2.04$ s
- Range: $x \approx 17.32 \times 2.04 \approx 35.3$ m (matching $\frac{v^2\sin 2\theta}{g}$ from Phase 4 ✓)
- Highest point at half the flight time, $t \approx 1.02$: $y \approx 10(1.02) - 4.9(1.02)^2 \approx 5.1$ m

**Example 5: game loop.** Move a coin in a circle around $(400, 300)$ once every 3 seconds.
```python
import math
def coin_pos(t):
    w = 2 * math.pi / 3                    # angular speed: one turn per 3 s
    return 400 + 50 * math.cos(w * t), 300 + 50 * math.sin(w * t)
```
This is the circle parametrisation $x = h + r\cos(\omega t)$, $y = k + r\sin(\omega t)$, with radius 50 and $\omega = \frac{2\pi}{3}$ rad/s.

## 5. Common mistakes
- Forgetting to show direction arrows on the sketch.
- Losing the domain restriction: $x = \cos t$ can only be in $[-1, 1]$, even after eliminating $t$.
- Putting sin on x and cos on y and then expecting the standard start point and direction.

## 6. Practice
Q: Eliminate $t$: $x = t^2$, $y = t + 1$.
A: $t = y - 1$, so $x = (y - 1)^2$.

Q: What curve is $x = 2 + 3\cos t$, $y = -1 + 3\sin t$?
A: A circle of radius 3 centred at $(2, -1)$.

Q: Eliminate $t$: $x = 5\cos t$, $y = 2\sin t$.
A: $\frac{x^2}{25} + \frac{y^2}{4} = 1$ (an ellipse).

Q: A projectile has $y = 15t - 4.9t^2$. When does it land?
A: $t = \frac{15}{4.9} \approx 3.06$ s.

Q: Which way does $x = \sin t$, $y = \cos t$ go around the circle?
A: Clockwise, starting at $(0, 1)$.

@@ 3 | TOPIC | Simple harmonic motion | Springs, pendulums and waves: x = A cos(ωt), frequency, period and damping | SHOULD
# Simple harmonic motion

## 1. What it covers
- **Simple harmonic motion (SHM):** motion described by $x(t) = A\cos(\omega t)$ or $A\sin(\omega t)$.
- **Angular frequency** $\omega$, **period** $T$ and **frequency** $f$, and how they connect.
- Springs and pendulums.
- **Damped** motion $x = Ae^{-kt}\cos(\omega t)$ (a preview).

## 2. Where it is used
- **Mechanical engineering:** car suspension, building sway in earthquakes, machine vibration.
- **Electronics:** LC circuits and quartz crystals in watches and computers oscillate harmonically (a quartz watch crystal vibrates at 32 768 Hz).
- **Music:** guitar strings, tuning forks and speaker cones.
- **Game and UI animation:** "spring" animations (iOS, Framer Motion, React Spring) are damped harmonic motion.
- **Seismology:** seismographs measure ground vibration.

## 3. What it means
Pull a mass on a spring and let go. It moves back and forth around its rest position, and its position over time is a perfect sine wave. The same thing happens to a pendulum (for small swings), a vibrating string and an electrical circuit.

Why sine? Look at a point moving round a circle at a steady speed, **from the side**: its shadow moves back and forth in exactly this way. SHM is "circular motion viewed edge-on".

**Analogy:** a child on a swing. They're fastest at the bottom (rest position) and stop for a moment at each end before swinging back. A cosine wave behaves exactly like this.

- $A$: amplitude (the furthest distance from rest).
- $\omega$: angular frequency (rad/s), i.e. how fast the "hidden circle" turns.
- $T = \frac{2\pi}{\omega}$: the time for one full back-and-forth.
- $f = \frac{1}{T}$: oscillations per second (Hz).

**Damping:** real springs lose energy, so the amplitude shrinks: multiply by $e^{-kt}$, which decays towards 0.

## 4. Formula & worked example
$$
x(t) = A\cos(\omega t) \qquad T = \frac{2\pi}{\omega} \qquad f = \frac{1}{T} = \frac{\omega}{2\pi}
$$
$$
\text{spring: } \omega = \sqrt{\frac{k}{m}} \qquad \text{pendulum (small swings): } T = 2\pi\sqrt{\frac{L}{g}}
$$
$$
\text{damped: } x(t) = Ae^{-ct}\cos(\omega t)
$$

**Example 1.** A mass on a spring is pulled 5 cm and released. It oscillates 2 times per second. Write its motion.
- $A = 5$, $f = 2$ Hz → $\omega = 2\pi f = 4\pi$
- Released from the end (maximum) → cosine: $x(t) = 5\cos(4\pi t)$

**Example 2.** Where is it at $t = 0.1$ s?
- $x = 5\cos(0.4\pi) \approx 5(0.309) \approx 1.55$ cm

**Example 3: spring constant.** A 0.5 kg mass on a spring with $k = 200$ N/m.
- $\omega = \sqrt{\frac{200}{0.5}} = 20$ rad/s → $T = \frac{2\pi}{20} \approx 0.314$ s, $f \approx 3.18$ Hz

**Example 4: pendulum clock.** How long must a pendulum be to have a period of 2 s (one tick per second each way)?
- $2 = 2\pi\sqrt{\frac{L}{9.8}}$ → $\sqrt{\frac{L}{9.8}} = \frac{1}{\pi}$ → $L = \frac{9.8}{\pi^2} \approx 0.993$ m

**Example 5: damped spring animation.**
```python
import math
def spring(t, start=100, damping=3.0, freq=2.0):
    return start * math.exp(-damping * t) * math.cos(2 * math.pi * freq * t)
# an element offset by 100 px wobbles back to 0 over ~1.5 seconds
```
`math.exp(-damping * t)` shrinks the swing each cycle, and `cos(2*pi*freq*t)` makes it oscillate twice per second. This is the "springy" effect used in UI libraries.

## 5. Common mistakes
- Confusing $f$ (Hz, cycles per second) with $\omega$ (rad/s). $\omega = 2\pi f$.
- Using sine when the motion starts at the maximum. A release from rest at the end is **cosine**.
- Using the pendulum formula for large swings. It is accurate only for small angles (under about $15^\circ$).

## 6. Practice
Q: $x(t) = 3\cos(10t)$. Amplitude, period and frequency?
A: $A = 3$, $T = \frac{2\pi}{10} \approx 0.628$ s, $f \approx 1.59$ Hz.

Q: Convert 50 Hz to angular frequency.
A: $\omega = 100\pi \approx 314$ rad/s.

Q: A spring has $k = 100$ N/m and a mass of 1 kg. What is $\omega$?
A: $\omega = \sqrt{100} = 10$ rad/s.

Q: A pendulum 2.45 m long. Period ($g = 9.8$)?
A: $2\pi\sqrt{0.25} = \pi \approx 3.14$ s.

Q: Why does SHM follow a sine wave?
A: It is the side view (shadow) of steady circular motion.

@@ 3 | TOPIC | Practice & review: Polar, parametric and harmonic motion | Mixed problem set with worked answers | SHOULD
# Practice & review: Polar, parametric and harmonic motion

**How to use this:** 40 minutes. Sketch the curves by hand before checking with a graphing tool such as Desmos.

## Problems
1. Convert $\left(-4, \frac{\pi}{3}\right)$ to Cartesian, and $(-3, 3)$ to polar.
2. Convert $r = 6\sin\theta$ to Cartesian form and describe the curve.
3. Identify each curve: (a) $r = 2\cos 3\theta$, (b) $r = 4 - 4\cos\theta$, (c) $r = 3 + \cos\theta$.
4. Eliminate the parameter: $x = 3\sec t$, $y = 2\tan t$.
5. A ball is kicked at 18 m/s at $50^\circ$. Write parametric equations and find its flight time and range ($g = 9.8$).
6. A mass on a spring moves with amplitude 8 cm and period 0.5 s, released from the top. Write $x(t)$ and find its position at $t = 0.125$ s.

## Answers
1. $x = -4\cos\frac{\pi}{3} = -2$, $y = -4\sin\frac{\pi}{3} = -2\sqrt{3}$ → $(-2, -2\sqrt{3})$. And $(-3, 3)$: $r = 3\sqrt{2}$, $\theta = \frac{3\pi}{4}$.
2. $r^2 = 6r\sin\theta$ → $x^2 + y^2 = 6y$ → $x^2 + (y - 3)^2 = 9$: a circle of radius 3 centred at $(0, 3)$.
3. (a) 3-petal rose, petals of length 2. (b) Cardioid pointing left (furthest point $r = 8$ at $\theta = \pi$). (c) Limaçon without a loop ($a > b$); rounded, with no dimple, since $a \ge 2b$.
4. $\sec t = \frac{x}{3}$, $\tan t = \frac{y}{2}$; with $\sec^2 t - \tan^2 t = 1$: $\frac{x^2}{9} - \frac{y^2}{4} = 1$ (a hyperbola).
5. $x = 18\cos 50^\circ\, t \approx 11.57t$, $y = 18\sin 50^\circ\, t - 4.9t^2 \approx 13.79t - 4.9t^2$. Flight time $\approx \frac{13.79}{4.9} \approx 2.81$ s; range $\approx 11.57 \times 2.81 \approx 32.5$ m.
6. $\omega = \frac{2\pi}{0.5} = 4\pi$; $x(t) = 8\cos(4\pi t)$. At $t = 0.125$: $8\cos\frac{\pi}{2} = 0$ (passing through rest position).

## Flashcards
Q: Polar to Cartesian formulas?
A: $x = r\cos\theta$, $y = r\sin\theta$.

Q: Number of petals of $r = a\cos(n\theta)$?
A: $n$ if $n$ is odd, $2n$ if $n$ is even.

Q: Standard parametric circle of radius $r$?
A: $x = r\cos t$, $y = r\sin t$.

Q: Period of $x = A\cos(\omega t)$?
A: $T = \frac{2\pi}{\omega}$.

@@ 2 | MODULE | Final review | Two cumulative mixed sets covering the whole roadmap | MUST
Finish the roadmap by mixing everything. Mixed practice (not grouped by topic) is what makes knowledge stick, because you have to decide which tool to use.

@@ 3 | TOPIC | Final review 1: Triangles and the unit circle | Cumulative mixed problems from Phases 1–3 with answers | MUST
# Final review 1: Triangles and the unit circle

**How to use this:** 60 minutes, calculator allowed only where marked (C). Revisit the topic notes for every miss.

## Problems
1. A 30-60-90 triangle has a long side of 12. Find the other sides exactly.
2. $\sec\theta = \frac{13}{5}$, $\theta$ in QIV. Find $\sin\theta$ and $\cot\theta$.
3. Evaluate exactly: $\cos\frac{5\pi}{4} \cdot \sin\frac{7\pi}{4} + \tan\frac{5\pi}{6}\cdot\sqrt{3}$.
4. (C) A 15 m ladder makes a $68^\circ$ angle with the ground. How high up the wall does it reach?
5. (C) An arc of length 18 cm subtends $2.4$ rad. Find the radius and the sector area.
6. Write the equation of a cosine wave with max 9, min 1, period 4 and a maximum at $x = 1$.
7. Evaluate exactly: $\arccos\left(-\frac{\sqrt{3}}{2}\right)$ and $\sin\left(\arctan\frac{4}{3}\right)$.
8. (C) $a = 11$, $b = 7$, $C = 38^\circ$. Find $c$ and the area.
9. (C) $A = 25^\circ$, $a = 6$, $b = 12$. How many triangles exist? Find $B$ for each.
10. (C) From a 40 m tower, the angles of depression to the near and far banks of a river (in line with the tower) are $52^\circ$ and $28^\circ$. How wide is the river?

## Answers
1. Short side $\frac{12}{\sqrt{3}} = 4\sqrt{3}$; hypotenuse $8\sqrt{3}$.
2. $\cos\theta = \frac{5}{13}$, $\sin\theta = -\frac{12}{13}$, $\cot\theta = -\frac{5}{12}$.
3. $\left(-\frac{\sqrt{2}}{2}\right)\left(-\frac{\sqrt{2}}{2}\right) + \left(-\frac{\sqrt{3}}{3}\right)\sqrt{3} = \frac{1}{2} - 1 = -\frac{1}{2}$.
4. $15\sin 68^\circ \approx 13.91$ m.
5. $r = \frac{18}{2.4} = 7.5$ cm; area $= \frac{1}{2}(7.5)^2(2.4) = 67.5$ cm².
6. $y = 4\cos\left(\frac{\pi}{2}(x - 1)\right) + 5$.
7. $\frac{5\pi}{6}$; $\frac{4}{5}$.
8. $c^2 = 121 + 49 - 154\cos 38^\circ \approx 170 - 121.35 = 48.65$ → $c \approx 6.97$; area $= \frac{1}{2}(11)(7)\sin 38^\circ \approx 23.70$.
9. $h = 12\sin 25^\circ \approx 5.07 < 6 < 12$ → two. $\sin B = \frac{12\sin 25^\circ}{6} \approx 0.8452$ → $B \approx 57.7^\circ$ or $122.3^\circ$.
10. Near bank $\frac{40}{\tan 52^\circ} \approx 31.25$ m; far bank $\frac{40}{\tan 28^\circ} \approx 75.23$ m; width $\approx 43.98$ m.

@@ 3 | TOPIC | Final review 2: Identities, equations and applications | Cumulative mixed problems from Phases 4–5 with answers | MUST
# Final review 2: Identities, equations and applications

**How to use this:** 60 minutes, no calculator except (C). After this set, you're ready for the Calculus roadmap.

## Problems
1. Prove $\frac{\cos x}{1 + \sin x} + \frac{1 + \sin x}{\cos x} = 2\sec x$.
2. Prove $\cos^4 x - \sin^4 x = \cos 2x$.
3. Exact value of $\sin\frac{5\pi}{12}$ (two ways: sum identity and half-angle).
4. $\tan x = -\frac{3}{4}$, $x$ in QII. Find $\sin 2x$ and $\cos\frac{x}{2}$.
5. Solve $2\sin^2 x + 3\cos x = 0$ on $[0, 2\pi)$.
6. Solve $\sin 2x + \sin x = 0$ on $[0, 2\pi)$.
7. Solve $\cos 3x = -\frac{1}{2}$ on $[0, \pi]$.
8. Convert $r = 2\sec\theta$ to Cartesian form.
9. (C) A pendulum's angle is $\theta(t) = 0.2\cos(3.1t)$ radians. Find its period and its angle at $t = 1$ s.

## Answers
1. Common denominator $\cos x(1 + \sin x)$: numerator $\cos^2 x + 1 + 2\sin x + \sin^2 x = 2(1 + \sin x)$, so the result is $\frac{2}{\cos x} = 2\sec x$ ✓
2. $(\cos^2 x - \sin^2 x)(\cos^2 x + \sin^2 x) = \cos^2 x - \sin^2 x = \cos 2x$ ✓
3. $\sin(45^\circ + 30^\circ) = \frac{\sqrt{6} + \sqrt{2}}{4}$; half-angle: $\sqrt{\frac{1 - \cos 150^\circ}{2}} = \sqrt{\frac{1 + \frac{\sqrt{3}}{2}}{2}} = \frac{\sqrt{2 + \sqrt{3}}}{2}$ (the same number, $\approx 0.966$).
4. $\sin x = \frac{3}{5}$, $\cos x = -\frac{4}{5}$. $\sin 2x = -\frac{24}{25}$. $x$ in QII → $\frac{x}{2}$ in QI: $\cos\frac{x}{2} = \sqrt{\frac{1 - \frac{4}{5}}{2}} = \sqrt{\frac{1}{10}} = \frac{\sqrt{10}}{10}$.
5. $2(1 - \cos^2 x) + 3\cos x = 0$ → $2\cos^2 x - 3\cos x - 2 = 0$ → $(2\cos x + 1)(\cos x - 2) = 0$. $\cos x = 2$ is impossible; $\cos x = -\frac{1}{2}$ → $x = \frac{2\pi}{3}, \frac{4\pi}{3}$.
6. $\sin x(2\cos x + 1) = 0$ → $x = 0, \pi, \frac{2\pi}{3}, \frac{4\pi}{3}$.
7. $3x$ in $[0, 3\pi]$: $3x = \frac{2\pi}{3}, \frac{4\pi}{3}, \frac{8\pi}{3}$ → $x = \frac{2\pi}{9}, \frac{4\pi}{9}, \frac{8\pi}{9}$.
8. $r\cos\theta = 2$ → $x = 2$ (a vertical line).
9. $T = \frac{2\pi}{3.1} \approx 2.03$ s; $\theta(1) = 0.2\cos 3.1 \approx 0.2(-0.9991) \approx -0.200$ rad (almost at the far end of the swing).
