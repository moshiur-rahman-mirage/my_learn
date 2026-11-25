@@ 1 | MODULE | Phase 1 — Right-Triangle Trigonometry (Foundation) | Pythagoras, the six trig ratios, exact values, solving right triangles and real-world uses | MUST
# Phase 1: Right-triangle trigonometry

**Trigonometry** means "triangle measuring". It connects the **angles** of a triangle with the **lengths** of its sides. Once you know a few of these, you can calculate everything else without measuring it.

Every topic in this roadmap has the same six parts:

1. **What it covers:** the scope of the topic in a few lines.
2. **Where it is used:** real uses in programming, games, engineering, physics and everyday life.
3. **What it means:** a plain-words explanation with an analogy.
4. **Formula & worked example:** the formulas, then solved problems step by step.
5. **Common mistakes:** the traps that cost marks.
6. **Practice:** `Q:`/`A:` lines that become flashcards in the app.

**What you need first:** angles in degrees, the fact that a triangle's angles add up to $180^\circ$, square roots, and solving simple equations such as $\frac{x}{5} = 0.6$.

**How to study each topic (about 1 hour):** read sections 1–3 (10 min), work through every example in section 4 with pencil and paper (25 min), read section 5, then answer section 6 without looking (20 min). Redo anything you missed the next day.

@@ 2 | MODULE | Right triangles and the trig ratios | Pythagoras, special triangles, SOH-CAH-TOA, exact values, inverse trig, solving triangles | MUST
This module builds the core toolkit. A right triangle has one $90^\circ$ angle. Its longest side, opposite the right angle, is the **hypotenuse**.

@@ 3 | TOPIC | Angles and right-triangle vocabulary | Degrees, angle sum, hypotenuse, opposite and adjacent sides, labelling convention | MUST
# Angles and right-triangle vocabulary

## 1. What it covers
- How angles are measured in degrees, and the angle sum of a triangle.
- The names of a right triangle's sides: **hypotenuse**, **opposite** and **adjacent**.
- The standard labelling convention: capital letters for angles, small letters for the sides opposite them.

## 2. Where it is used
- **Everywhere else in this roadmap.** Every trig formula is written with these names, so mixing them up breaks every later topic.
- **Construction and carpentry:** "square" corners are $90^\circ$; roof pitches are angles.
- **Games and graphics:** a character's facing direction is an angle; the distance to a target is a hypotenuse.

## 3. What it means
An **angle** measures how far you turn. A full turn is $360^\circ$, a half turn (a straight line) is $180^\circ$, and a quarter turn (a corner) is $90^\circ$.

In a **right triangle**, one angle is exactly $90^\circ$. The other two angles share the remaining $90^\circ$, so they are both **acute** (less than $90^\circ$).

The side names depend on **which acute angle you are standing at**:
- **Hypotenuse:** the longest side, always opposite the right angle. It never changes.
- **Opposite:** the side across from your angle; it does not touch your angle.
- **Adjacent:** the side next to your angle that is *not* the hypotenuse.

**Analogy:** stand at a corner of a triangular garden. The fence you look straight across at is "opposite". The fence you are leaning on that runs to the square corner is "adjacent". The long diagonal fence is the hypotenuse.

```text
            B
            |\
            | \
       a    |  \   c  (hypotenuse)
 (opposite  |   \
   to A)    |    \
            |_____\
            C   b   A
           (90°) (adjacent to A)
```
The picture shows the labelling convention: the angle at A faces side $a$, B faces $b$, and C (the right angle) faces $c$. From angle A, $a$ is opposite and $b$ is adjacent. From angle B, they swap: $b$ is opposite and $a$ is adjacent.

## 4. Formula & worked example
**Angle sum of any triangle:**
$$
A + B + C = 180^\circ
$$
In a right triangle with $C = 90^\circ$:
$$
A + B = 90^\circ
$$

**Example 1.** A right triangle has $A = 35^\circ$. Find $B$.
- $A + B = 90^\circ$, so $B = 90^\circ - 35^\circ = 55^\circ$.

**Example 2.** In the triangle above, $a = 3$, $b = 4$, $c = 5$. Name the sides from angle B.
- The hypotenuse is always $c = 5$.
- Opposite B is $b = 4$ (it doesn't touch B).
- Adjacent to B is $a = 3$.

**Example 3.** A triangle has angles $40^\circ$ and $50^\circ$. Is it a right triangle?
- The third angle is $180^\circ - 40^\circ - 50^\circ = 90^\circ$. Yes.

## 5. Common mistakes
- Calling the hypotenuse "adjacent" because it touches your angle. The hypotenuse is **never** called adjacent.
- Forgetting that opposite and adjacent **swap** when you switch to the other acute angle.
- Labelling the hypotenuse from the wrong angle: it is always opposite the $90^\circ$ angle, whichever acute angle you are using.

## 6. Practice
Q: In a right triangle, one acute angle is $62^\circ$. What is the other?
A: $90^\circ - 62^\circ = 28^\circ$.

Q: Which side of a right triangle is always the longest?
A: The hypotenuse, the side opposite the right angle.

Q: From angle A, side $a$ is opposite. What is side $a$ from angle B?
A: Adjacent. Opposite and adjacent swap between the two acute angles.

Q: Can a triangle have two right angles? Why?
A: No. Two $90^\circ$ angles already add up to $180^\circ$, leaving $0^\circ$ for the third angle.

@@ 3 | TOPIC | Pythagorean theorem and triples | a² + b² = c², finding missing sides, converse, common triples, distance formula | MUST
# Pythagorean theorem and triples

## 1. What it covers
- The Pythagorean theorem $a^2 + b^2 = c^2$, and using it to find a missing side.
- The **converse**: testing whether a triangle is right-angled.
- **Pythagorean triples** such as 3-4-5 and 5-12-13.
- The **distance formula** between two points, which is Pythagoras on a grid.

## 2. Where it is used
- **Programming and games:** the distance between two points, $\sqrt{(x_2-x_1)^2 + (y_2-y_1)^2}$, is used in collision detection, "nearest enemy" and k-nearest-neighbours in machine learning (Euclidean distance).
- **Screens:** a "55-inch TV" is measured along the diagonal, the hypotenuse of the screen.
- **Construction:** builders mark 3 m and 4 m along two walls; if the diagonal is exactly 5 m, the corner is square (the 3-4-5 rule).
- **Navigation:** walking 3 km east then 4 km north puts you 5 km from the start.

## 3. What it means
In a right triangle, if you build a square on each side, **the two small squares together have exactly the same area as the big square** on the hypotenuse.

**Analogy:** cutting diagonally across a rectangular field is shorter than walking along two edges. Pythagoras tells you exactly how much shorter.

The theorem only works for **right** triangles. The **converse** says: if $a^2 + b^2 = c^2$ is true for a triangle's three sides (with $c$ the longest), then it *is* a right triangle.

A **Pythagorean triple** is three whole numbers that fit the theorem. Multiplying a triple by any number gives another triple: 3-4-5 → 6-8-10 → 9-12-15.

## 4. Formula & worked example
$$
a^2 + b^2 = c^2 \qquad c = \sqrt{a^2 + b^2} \qquad a = \sqrt{c^2 - b^2}
$$
**Distance between** $(x_1, y_1)$ and $(x_2, y_2)$:
$$
d = \sqrt{(x_2 - x_1)^2 + (y_2 - y_1)^2}
$$
Common triples: **3-4-5, 5-12-13, 8-15-17, 7-24-25** (and their multiples).

**Example 1: find the hypotenuse.** Legs $6$ and $8$.
- $c^2 = 6^2 + 8^2 = 36 + 64 = 100$
- $c = \sqrt{100} = 10$ (a 3-4-5 triple, doubled)

**Example 2: find a leg.** A 13 m ladder reaches 12 m up a wall. How far from the wall is its foot?
- $a^2 + 12^2 = 13^2$
- $a^2 = 169 - 144 = 25$, so $a = 5$ m

**Example 3: converse.** Is a triangle with sides 7, 9 and 12 right-angled?
- Longest side is 12: $7^2 + 9^2 = 49 + 81 = 130$, but $12^2 = 144$.
- $130 \ne 144$, so it is **not** a right triangle. (Since $130 < 144$, the angle opposite 12 is obtuse.)

**Example 4: distance.** From $(1, 2)$ to $(7, 10)$:
- $d = \sqrt{(7-1)^2 + (10-2)^2} = \sqrt{36 + 64} = \sqrt{100} = 10$

**In code** (this is how games measure distance):
```python
import math
def dist(x1, y1, x2, y2):
    return math.hypot(x2 - x1, y2 - y1)   # same as sqrt(dx*dx + dy*dy)
print(dist(1, 2, 7, 10))   # 10.0
```
`math.hypot` computes $\sqrt{dx^2 + dy^2}$ without overflow problems. Games often skip the square root and compare *squared* distances, because if $d_1^2 < d_2^2$ then $d_1 < d_2$, and square roots are slow.

## 5. Common mistakes
- Adding when finding a **leg**. For a leg you subtract: $a^2 = c^2 - b^2$.
- Writing $\sqrt{a^2 + b^2} = a + b$. It is not: $\sqrt{9 + 16} = 5$, not $7$.
- Using the theorem on a triangle that is not right-angled.
- Putting the wrong side as $c$. The hypotenuse (longest side) must be $c$.

## 6. Practice
Q: Legs are 5 and 12. Find the hypotenuse.
A: $\sqrt{25 + 144} = \sqrt{169} = 13$.

Q: Hypotenuse 17, one leg 8. Find the other leg.
A: $\sqrt{289 - 64} = \sqrt{225} = 15$.

Q: Is 9, 40, 41 a right triangle?
A: Yes: $81 + 1600 = 1681 = 41^2$.

Q: Distance from $(-2, 1)$ to $(4, 9)$?
A: $\sqrt{6^2 + 8^2} = \sqrt{100} = 10$.

Q: A TV screen is 48 inches wide and 27 inches tall. What is its diagonal size?
A: $\sqrt{48^2 + 27^2} = \sqrt{2304 + 729} = \sqrt{3033} \approx 55.1$ inches, a "55-inch" TV.

@@ 3 | TOPIC | Special right triangles | 45-45-90 and 30-60-90 side ratios, derived from a square and an equilateral triangle | MUST
# Special right triangles

## 1. What it covers
- The **45-45-90** triangle: sides in the ratio $1 : 1 : \sqrt{2}$.
- The **30-60-90** triangle: sides in the ratio $1 : \sqrt{3} : 2$.
- Where these ratios come from, so you can rebuild them instead of memorising.

## 2. Where it is used
- **Exact trig values:** $\sin 30^\circ$, $\cos 45^\circ$, $\tan 60^\circ$ and so on are read straight off these two triangles (next topics).
- **Engineering drawings:** set squares come in exactly these two shapes.
- **Graphics:** isometric game art uses 30° angles; diagonal movement on a grid uses 45° (moving one step diagonally covers $\sqrt{2} \approx 1.414$ units, which is why diagonal movement must be normalised in games).
- **Exams:** questions without calculators expect you to use these exact values.

## 3. What it means
These two triangles appear so often that their side ratios are worth knowing by heart.

**45-45-90:** cut a square along its diagonal. You get two triangles, each with two equal legs. If each leg is $1$, the diagonal is $\sqrt{1^2 + 1^2} = \sqrt{2}$.

**30-60-90:** cut an equilateral triangle (all sides $2$, all angles $60^\circ$) down the middle. You get a triangle with hypotenuse $2$, a short side $1$ (half the base), and a height of $\sqrt{2^2 - 1^2} = \sqrt{3}$.

**Analogy:** these are the "times tables" of trigonometry. You could work them out each time, but knowing them makes everything faster.

```text
 45-45-90                30-60-90
     /|                      /|
 √2 / | 1             2     / |  √3
   /45|                    /30|
  /___|                   /___|
    1                    60  1
```
In the 30-60-90 triangle, the **short side (1) is opposite 30°**, the **middle side ($\sqrt{3}$) is opposite 60°**, and the hypotenuse is twice the short side.

## 4. Formula & worked example
$$
45\text{-}45\text{-}90:\quad \text{leg} : \text{leg} : \text{hyp} = 1 : 1 : \sqrt{2}
$$
$$
30\text{-}60\text{-}90:\quad \text{short} : \text{long} : \text{hyp} = 1 : \sqrt{3} : 2
$$

**Example 1.** A 45-45-90 triangle has legs of 7. Find the hypotenuse.
- hyp $= 7\sqrt{2} \approx 9.90$

**Example 2.** A 45-45-90 triangle has hypotenuse 10. Find each leg.
- leg $= \dfrac{10}{\sqrt{2}} = \dfrac{10\sqrt{2}}{2} = 5\sqrt{2} \approx 7.07$

**Example 3.** A 30-60-90 triangle has hypotenuse 12. Find the other sides.
- short side (opposite 30°) $= 12 \div 2 = 6$
- long side (opposite 60°) $= 6\sqrt{3} \approx 10.39$

**Example 4.** A 30-60-90 triangle has long side $9$. Find the short side and hypotenuse.
- short $= \dfrac{9}{\sqrt{3}} = \dfrac{9\sqrt{3}}{3} = 3\sqrt{3}$
- hyp $= 2 \times 3\sqrt{3} = 6\sqrt{3}$

## 5. Common mistakes
- Swapping $\sqrt{3}$ and $2$. The hypotenuse is always the longest side, and $2 > \sqrt{3} \approx 1.73$.
- Putting the short side opposite 60°. The shortest side is always opposite the smallest angle (30°).
- Leaving $\sqrt{2}$ in the denominator when an exact answer is asked for. Rationalise it: $\frac{1}{\sqrt{2}} = \frac{\sqrt{2}}{2}$.

## 6. Practice
Q: 45-45-90 with legs 4. Hypotenuse?
A: $4\sqrt{2}$.

Q: 30-60-90 with short side 5. Other two sides?
A: Long side $5\sqrt{3}$, hypotenuse $10$.

Q: 30-60-90 with hypotenuse 8. Side opposite 60°?
A: Short side is 4, so the side opposite 60° is $4\sqrt{3}$.

Q: How is the 45-45-90 triangle derived?
A: Cut a unit square along its diagonal; the diagonal is $\sqrt{1^2 + 1^2} = \sqrt{2}$.

Q: A square has a diagonal of 20 cm. What is its side length?
A: $\frac{20}{\sqrt{2}} = 10\sqrt{2} \approx 14.14$ cm.

@@ 3 | TOPIC | The six trig ratios (SOH-CAH-TOA) | sin, cos, tan and their reciprocals csc, sec, cot from a right triangle | MUST
# The six trig ratios (SOH-CAH-TOA)

## 1. What it covers
- The three main ratios: **sine**, **cosine** and **tangent**.
- The three reciprocal ratios: **cosecant**, **secant** and **cotangent**.
- The memory aid **SOH-CAH-TOA**, and why the ratios depend only on the angle, not the triangle's size.

## 2. Where it is used
- **Games and graphics:** moving a sprite at speed $v$ in direction $\theta$ means $dx = v\cos\theta$, $dy = v\sin\theta$. Rotating any image or 3D model uses sin and cos.
- **Physics:** splitting a force or velocity into horizontal and vertical parts.
- **Construction:** a ramp or roof slope is a tangent: rise ÷ run.
- **Surveying:** measuring a building's height from an angle and a distance.
- **Signals and audio:** sound waves and AC electricity are sine waves (Phase 2).

## 3. What it means
Take any two right triangles with the same acute angle $\theta$, one small and one big. They are **similar**: the big one is a scaled-up copy. So the **ratio** of any two sides is the same in both. That fixed ratio depends only on $\theta$, and we give it a name.

**Analogy:** a ramp with a 1-in-12 slope is equally steep whether it is 1 m long or 100 m long. The steepness depends on the angle, not the size. Trig ratios are exact "steepness numbers" for an angle.

- **sine** $\theta$: how much of the hypotenuse is "going up" (opposite ÷ hypotenuse).
- **cosine** $\theta$: how much is "going across" (adjacent ÷ hypotenuse).
- **tangent** $\theta$: the slope, rise ÷ run (opposite ÷ adjacent).

The reciprocals are just the three main ratios flipped upside down.

## 4. Formula & worked example
$$
\sin\theta = \frac{\text{opp}}{\text{hyp}} \qquad \cos\theta = \frac{\text{adj}}{\text{hyp}} \qquad \tan\theta = \frac{\text{opp}}{\text{adj}}
$$
**SOH-CAH-TOA:** **S**in = **O**pp/**H**yp, **C**os = **A**dj/**H**yp, **T**an = **O**pp/**A**dj.

$$
\csc\theta = \frac{1}{\sin\theta} = \frac{\text{hyp}}{\text{opp}} \qquad \sec\theta = \frac{1}{\cos\theta} = \frac{\text{hyp}}{\text{adj}} \qquad \cot\theta = \frac{1}{\tan\theta} = \frac{\text{adj}}{\text{opp}}
$$
A useful link: $\tan\theta = \dfrac{\sin\theta}{\cos\theta}$.

**Example 1.** A right triangle has opp $= 3$, adj $= 4$, hyp $= 5$ (from angle $\theta$). Find all six ratios.
- $\sin\theta = \frac{3}{5}$, $\cos\theta = \frac{4}{5}$, $\tan\theta = \frac{3}{4}$
- $\csc\theta = \frac{5}{3}$, $\sec\theta = \frac{5}{4}$, $\cot\theta = \frac{4}{3}$

**Example 2.** $\sin\theta = \frac{5}{13}$ and $\theta$ is acute. Find $\cos\theta$ and $\tan\theta$.
- Draw it: opp $= 5$, hyp $= 13$. Pythagoras: adj $= \sqrt{169 - 25} = 12$.
- $\cos\theta = \frac{12}{13}$, $\tan\theta = \frac{5}{12}$

**Example 3: a game.** A bullet travels at 10 units per frame at $30^\circ$ above the x-axis. How far does it move in x and y each frame?
- $dx = 10\cos 30^\circ = 10 \times 0.866 = 8.66$
- $dy = 10\sin 30^\circ = 10 \times 0.5 = 5$
```python
import math
speed, angle = 10, math.radians(30)   # Python's trig functions use radians
dx, dy = speed * math.cos(angle), speed * math.sin(angle)
print(round(dx, 2), round(dy, 2))     # 8.66 5.0
```
Notice `math.radians(30)`: programming languages expect angles in **radians**, not degrees (Phase 2 explains radians). Forgetting this conversion is the most common trig bug in code.

## 5. Common mistakes
- Using the side names from the wrong angle. Always relabel opposite and adjacent for the angle you are working with.
- Thinking $\sin\theta$ means $\sin \times \theta$. It is a function of $\theta$, like $f(\theta)$.
- Calculator in the wrong mode (radians instead of degrees). Check: $\sin 30$ should give $0.5$.
- Mixing up $\csc$ and $\sec$: **csc goes with sin**, **sec goes with cos** (each pair has exactly one "co").

## 6. Practice
Q: What does SOH-CAH-TOA stand for?
A: $\sin = \frac{\text{opp}}{\text{hyp}}$, $\cos = \frac{\text{adj}}{\text{hyp}}$, $\tan = \frac{\text{opp}}{\text{adj}}$.

Q: opp $= 8$, adj $= 15$. Find $\sin\theta$.
A: hyp $= 17$, so $\sin\theta = \frac{8}{17}$.

Q: $\cos\theta = \frac{3}{5}$ ($\theta$ acute). Find $\tan\theta$ and $\csc\theta$.
A: opp $= 4$, so $\tan\theta = \frac{4}{3}$ and $\csc\theta = \frac{5}{4}$.

Q: What is the reciprocal of $\cos\theta$?
A: $\sec\theta = \frac{1}{\cos\theta}$.

Q: Why does $\sin 40^\circ$ have the same value in every right triangle?
A: All right triangles with a $40^\circ$ angle are similar, so the ratio opp/hyp is the same in each.

@@ 3 | TOPIC | Exact values at 30°, 45° and 60° | sin, cos, tan of special angles without a calculator, the hand trick and table | MUST
# Exact values at 30°, 45° and 60°

## 1. What it covers
- The exact sin, cos and tan of $30^\circ$, $45^\circ$ and $60^\circ$ (plus $0^\circ$ and $90^\circ$).
- How to read them off the special triangles.
- A memory pattern so you never need to memorise a table blindly.

## 2. Where it is used
- **Exams without calculators:** answers are expected as exact values such as $\frac{\sqrt{3}}{2}$, not $0.866$.
- **Later topics:** the unit circle, identities and equations all reuse these values.
- **Graphics:** rotation matrices for $90^\circ$, $45^\circ$ and $30^\circ$ use these exact numbers, and hexagonal grids in games are built on $60^\circ$.
- **Engineering:** three-phase electrical power is $120^\circ$ apart, which uses $\frac{\sqrt{3}}{2}$.

## 3. What it means
These are not new facts, just SOH-CAH-TOA applied to the two special triangles:
- 45-45-90 with sides $1, 1, \sqrt{2}$.
- 30-60-90 with sides $1, \sqrt{3}, 2$.

**Memory pattern for sine:** write $\frac{\sqrt{0}}{2}, \frac{\sqrt{1}}{2}, \frac{\sqrt{2}}{2}, \frac{\sqrt{3}}{2}, \frac{\sqrt{4}}{2}$ for $0^\circ, 30^\circ, 45^\circ, 60^\circ, 90^\circ$. Simplify: $0, \frac{1}{2}, \frac{\sqrt{2}}{2}, \frac{\sqrt{3}}{2}, 1$. **Cosine is the same list backwards.** Tangent is sine ÷ cosine.

**Analogy:** like knowing $7 \times 8 = 56$ instantly instead of adding 7 eight times.

## 4. Formula & worked example
| angle | $0^\circ$ | $30^\circ$ | $45^\circ$ | $60^\circ$ | $90^\circ$ |
|---|---|---|---|---|---|
| $\sin$ | $0$ | $\frac{1}{2}$ | $\frac{\sqrt{2}}{2}$ | $\frac{\sqrt{3}}{2}$ | $1$ |
| $\cos$ | $1$ | $\frac{\sqrt{3}}{2}$ | $\frac{\sqrt{2}}{2}$ | $\frac{1}{2}$ | $0$ |
| $\tan$ | $0$ | $\frac{\sqrt{3}}{3}$ | $1$ | $\sqrt{3}$ | undefined |

**Example 1: derive $\sin 60^\circ$.** In the 30-60-90 triangle, the side opposite $60^\circ$ is $\sqrt{3}$ and the hypotenuse is $2$.
- $\sin 60^\circ = \frac{\sqrt{3}}{2}$

**Example 2: derive $\tan 30^\circ$.** Opposite $30^\circ$ is $1$, adjacent is $\sqrt{3}$.
- $\tan 30^\circ = \frac{1}{\sqrt{3}} = \frac{\sqrt{3}}{3} \approx 0.577$

**Example 3: evaluate exactly.** $2\sin 30^\circ + \cos^2 45^\circ$
- $= 2 \cdot \frac{1}{2} + \left(\frac{\sqrt{2}}{2}\right)^2 = 1 + \frac{2}{4} = \frac{3}{2}$

**Example 4.** Why is $\tan 90^\circ$ undefined?
- $\tan 90^\circ = \frac{\sin 90^\circ}{\cos 90^\circ} = \frac{1}{0}$, and you can't divide by zero. Geometrically, a line at $90^\circ$ is vertical, and a vertical line has no defined slope.

## 5. Common mistakes
- Swapping $\sin 30^\circ$ and $\sin 60^\circ$. Check with size: a bigger angle (up to $90^\circ$) has a bigger sine, so $\sin 60^\circ > \sin 30^\circ$.
- Writing $\cos^2 45^\circ$ as $\cos(45^2)$. $\cos^2\theta$ means $(\cos\theta)^2$.
- Thinking $\sin 60^\circ = 2\sin 30^\circ$. It isn't: $\frac{\sqrt{3}}{2} \ne 1$. Sine is not linear.

## 6. Practice
Q: Exact value of $\cos 30^\circ$?
A: $\frac{\sqrt{3}}{2}$.

Q: Exact value of $\tan 60^\circ$?
A: $\sqrt{3}$.

Q: Exact value of $\sin 45^\circ$?
A: $\frac{\sqrt{2}}{2}$.

Q: Evaluate $\sin^2 60^\circ + \cos^2 60^\circ$.
A: $\frac{3}{4} + \frac{1}{4} = 1$.

Q: Evaluate $4\cos 60^\circ \tan 45^\circ$.
A: $4 \cdot \frac{1}{2} \cdot 1 = 2$.

@@ 3 | TOPIC | Inverse trig: finding angles | sin⁻¹, cos⁻¹, tan⁻¹ on a calculator to find an angle from a ratio | MUST
# Inverse trig: finding angles

## 1. What it covers
- Using $\sin^{-1}$, $\cos^{-1}$ and $\tan^{-1}$ (also written arcsin, arccos, arctan) to find an **angle** when you know a **ratio**.
- Choosing which inverse to use from the two sides you know.
- Calculator use, and `atan2` in programming.

## 2. Where it is used
- **Games:** to make an enemy face the player, compute the angle with `atan2(dy, dx)`. Every top-down shooter does this.
- **Robotics:** inverse kinematics finds the joint angles that put a robot arm's hand at a target point.
- **Construction:** a ramp rises 1 m over 12 m. What angle is that? ($\tan^{-1}\frac{1}{12} \approx 4.8^\circ$.)
- **Navigation:** converting a position change into a heading.

## 3. What it means
Normal trig goes **angle → ratio**: $\sin 30^\circ = 0.5$.
Inverse trig goes **ratio → angle**: $\sin^{-1}(0.5) = 30^\circ$. It answers "which angle has this sine?"

**Analogy:** a trig function is like a dictionary from English to French. The inverse is the French-to-English dictionary.

**Notation warning:** $\sin^{-1} x$ means the inverse function, **not** $\frac{1}{\sin x}$. The reciprocal $\frac{1}{\sin x}$ is $\csc x$.

For right triangles the angle is always between $0^\circ$ and $90^\circ$, and the calculator gives that angle directly. (For other angles there are more solutions; see Phase 4.)

## 4. Formula & worked example
$$
\theta = \sin^{-1}\!\left(\frac{\text{opp}}{\text{hyp}}\right) \qquad \theta = \cos^{-1}\!\left(\frac{\text{adj}}{\text{hyp}}\right) \qquad \theta = \tan^{-1}\!\left(\frac{\text{opp}}{\text{adj}}\right)
$$
**Choosing:** look at the two sides you **know** relative to the angle, and pick the ratio that uses exactly those two.

**Example 1.** opp $= 7$, hyp $= 10$. Find $\theta$.
- Known: opp and hyp → sine.
- $\theta = \sin^{-1}(0.7) \approx 44.4^\circ$

**Example 2.** A ladder 6 m long has its foot 2 m from the wall. What angle does it make with the ground?
- From the ground angle: adj $= 2$ (along the ground), hyp $= 6$ → cosine.
- $\theta = \cos^{-1}\left(\frac{2}{6}\right) \approx 70.5^\circ$

**Example 3.** A road rises 15 m over a horizontal distance of 200 m. Angle of the road?
- opp $= 15$, adj $= 200$ → tangent.
- $\theta = \tan^{-1}(0.075) \approx 4.3^\circ$

**Example 4: in a game.** The player is at $(2, 3)$ and an enemy at $(7, 8)$. Which way should the enemy face?
```python
import math
dx, dy = 2 - 7, 3 - 8                       # vector from enemy to player: (-5, -5)
angle = math.degrees(math.atan2(dy, dx))
print(angle)                                # -135.0  (down-left)
```
`atan2(dy, dx)` is a smarter $\tan^{-1}$: it looks at the signs of both $dx$ and $dy$, so it returns the right direction in all four quadrants (from $-180^\circ$ to $180^\circ$). Plain `atan(dy/dx)` can't tell $(-5,-5)$ from $(5,5)$, and crashes when $dx = 0$.

## 5. Common mistakes
- Reading $\sin^{-1}x$ as $\frac{1}{\sin x}$.
- Taking $\sin^{-1}$ of a number bigger than 1 (an error: no angle has sine $1.5$). If you get that, a side is probably in the wrong place, for example opp > hyp.
- Calculator in radian mode: $\sin^{-1}(0.5)$ shows $0.5236$ instead of $30$.

## 6. Practice
Q: $\tan\theta = 1$. Find the acute angle $\theta$.
A: $\theta = \tan^{-1}(1) = 45^\circ$.

Q: adj $= 9$, hyp $= 15$. Find $\theta$.
A: $\cos^{-1}(0.6) \approx 53.1^\circ$.

Q: What is the difference between $\sin^{-1}x$ and $(\sin x)^{-1}$?
A: $\sin^{-1}x$ is the angle whose sine is $x$; $(\sin x)^{-1} = \frac{1}{\sin x} = \csc x$.

Q: Why use `atan2(dy, dx)` instead of `atan(dy/dx)` in code?
A: `atan2` uses the signs of both inputs to give the correct quadrant, and works when $dx = 0$.

Q: A wheelchair ramp must not be steeper than 1:12. What is the maximum angle?
A: $\tan^{-1}\left(\frac{1}{12}\right) \approx 4.76^\circ$.

@@ 3 | TOPIC | Solving right triangles | Find every missing side and angle from two known parts | MUST
# Solving right triangles

## 1. What it covers
- "Solving" a triangle means finding **all** its unknown sides and angles.
- Two cases: you know **one side and one acute angle**, or you know **two sides**.
- A step-by-step method that always works.

## 2. Where it is used
- **Surveying and mapping:** measuring land and heights that can't be reached directly.
- **Engineering:** working out the lengths of beams and braces in trusses, roofs and bridges.
- **Physics:** finding the magnitude and direction of a resultant force or velocity.
- **Game development:** computing a camera's view distance, or how far a projectile lands.

## 3. What it means
A right triangle has 3 sides and 2 unknown angles (the third angle is $90^\circ$). **If you know any two of them (with at least one being a side), you can find all the rest.**

Your three tools:
1. **Angle sum:** the two acute angles add up to $90^\circ$.
2. **Pythagoras:** links the three sides.
3. **SOH-CAH-TOA:** links two sides with an angle.

**Analogy:** a sudoku. Once a few cells are filled in, the rules force every other cell.

**The method:**
1. Sketch the triangle and label what you know.
2. Find the missing angle with $90^\circ - \theta$.
3. For a missing side: choose the trig ratio that uses the side you **know** and the side you **want**.
4. Set up the equation and solve it (multiply or divide).
5. Check: the longest side should be opposite the largest angle.

## 4. Formula & worked example
If $\sin\theta = \frac{x}{h}$, then $x = h\sin\theta$ (unknown on top: multiply).
If $\sin\theta = \frac{5}{h}$, then $h = \frac{5}{\sin\theta}$ (unknown on the bottom: divide).

**Example 1: one side and one angle.** $A = 32^\circ$, $C = 90^\circ$, hypotenuse $c = 20$. Solve the triangle.
- $B = 90^\circ - 32^\circ = 58^\circ$
- Side $a$ (opposite $A$): $\sin 32^\circ = \frac{a}{20}$ → $a = 20\sin 32^\circ \approx 20(0.5299) \approx 10.60$
- Side $b$ (adjacent to $A$): $\cos 32^\circ = \frac{b}{20}$ → $b = 20\cos 32^\circ \approx 16.96$
- Check: $10.60^2 + 16.96^2 \approx 112.4 + 287.6 = 400 = 20^2$ ✓

**Example 2: unknown on the bottom.** $A = 40^\circ$, opposite side $a = 9$. Find the hypotenuse.
- $\sin 40^\circ = \frac{9}{c}$ → $c = \frac{9}{\sin 40^\circ} \approx \frac{9}{0.6428} \approx 14.00$

**Example 3: two sides.** Legs $a = 5$ and $b = 8$. Solve the triangle.
- $c = \sqrt{25 + 64} = \sqrt{89} \approx 9.43$
- $A = \tan^{-1}\left(\frac{5}{8}\right) \approx 32.0^\circ$
- $B = 90^\circ - 32.0^\circ = 58.0^\circ$

## 5. Common mistakes
- Rounding too early. Keep full calculator values until the final answer, or the errors add up.
- Multiplying when the unknown is in the denominator. If $\cos\theta = \frac{6}{x}$, then $x = \frac{6}{\cos\theta}$, not $6\cos\theta$.
- Using a side you calculated (and rounded) when an original given side would do. Use given values where you can.

## 6. Practice
Q: $A = 25^\circ$, hypotenuse $= 12$. Find side $a$ (opposite $A$).
A: $a = 12\sin 25^\circ \approx 5.07$.

Q: $B = 60^\circ$, adjacent side to B $= 7$. Find the hypotenuse.
A: $c = \frac{7}{\cos 60^\circ} = 14$.

Q: Legs are 6 and 6. Find both acute angles.
A: $\tan^{-1}(1) = 45^\circ$ each.

Q: Hypotenuse 10, one leg 6. Solve the triangle.
A: Other leg $8$; angle opposite 6 is $\sin^{-1}(0.6) \approx 36.9^\circ$; other angle $\approx 53.1^\circ$.

Q: $A = 50^\circ$, adjacent side $b = 4$. Find the opposite side $a$.
A: $a = 4\tan 50^\circ \approx 4.77$.

@@ 3 | TOPIC | Complementary angles and cofunctions | sin θ = cos(90° − θ), tan θ = cot(90° − θ), and why "co" means complement | MUST
# Complementary angles and cofunctions

## 1. What it covers
- **Complementary angles:** two angles that add up to $90^\circ$.
- **Cofunction identities:** $\sin\theta = \cos(90^\circ - \theta)$, and similar pairs.
- Why the "co" in cosine, cotangent and cosecant means "complement".

## 2. Where it is used
- **Simplifying expressions** in identities and calculus (Phase 4).
- **Graphs:** the cosine wave is just the sine wave shifted by $90^\circ$ (Phase 2).
- **Solving problems quickly:** knowing $\cos 70^\circ = \sin 20^\circ$ lets you switch to whichever is easier.
- **Physics:** components of a force along a slope swap sin and cos depending on which angle is measured.

## 3. What it means
In a right triangle, the two acute angles $A$ and $B$ are complementary: $A + B = 90^\circ$.

The side that is **opposite** $A$ is **adjacent** to $B$, and the other way round. So "opp/hyp from A" is exactly the same fraction as "adj/hyp from B". In other words, the sine of one angle is the cosine of the other.

**Analogy:** two people standing at opposite corners of the same triangle describe the same fence differently: one calls it "across from me", the other "next to me". Same fence, different viewpoint.

That's where the names come from: **co**sine is the sine of the **co**mplement. The same goes for **co**tangent and **co**secant.

## 4. Formula & worked example
$$
\sin\theta = \cos(90^\circ - \theta) \qquad \cos\theta = \sin(90^\circ - \theta)
$$
$$
\tan\theta = \cot(90^\circ - \theta) \qquad \sec\theta = \csc(90^\circ - \theta)
$$

**Example 1.** Write $\cos 72^\circ$ as a sine.
- $\cos 72^\circ = \sin(90^\circ - 72^\circ) = \sin 18^\circ$

**Example 2.** Find $\theta$ if $\sin(2\theta) = \cos(\theta + 30^\circ)$, with both angles acute.
- Sine of one equals cosine of the other, so the angles are complementary:
- $2\theta + \theta + 30^\circ = 90^\circ$ → $3\theta = 60^\circ$ → $\theta = 20^\circ$
- Check: $\sin 40^\circ = \cos 50^\circ$ ✓

**Example 3.** Simplify $\dfrac{\sin 35^\circ}{\cos 55^\circ}$.
- $\cos 55^\circ = \sin 35^\circ$, so the fraction is $1$.

**Example 4.** Check with exact values: $\sin 30^\circ = \frac{1}{2}$ and $\cos 60^\circ = \frac{1}{2}$. Since $30^\circ + 60^\circ = 90^\circ$, they match ✓

## 5. Common mistakes
- Thinking $\sin\theta = \cos\theta$ in general. They are equal only at $45^\circ$ (in the first quadrant).
- Using $180^\circ$ instead of $90^\circ$. Complementary means adding to $90^\circ$; **supplementary** means adding to $180^\circ$.
- Pairing $\tan$ with $\sec$. The cofunction pairs are sin-cos, tan-cot and sec-csc.

## 6. Practice
Q: Write $\sin 25^\circ$ as a cosine.
A: $\cos 65^\circ$.

Q: What is the complement of $38^\circ$?
A: $52^\circ$.

Q: Simplify $\tan 20^\circ \cdot \tan 70^\circ$.
A: $\tan 70^\circ = \cot 20^\circ = \frac{1}{\tan 20^\circ}$, so the product is $1$.

Q: Solve $\cos(x + 10^\circ) = \sin(2x + 20^\circ)$ for acute angles.
A: $3x + 30^\circ = 90^\circ$, so $x = 20^\circ$.

Q: What does the "co" in cosine mean?
A: Complement: $\cos\theta$ is the sine of the complementary angle $90^\circ - \theta$.

@@ 2 | MODULE | Right-triangle applications | Elevation and depression, bearings and navigation, mixed practice | MUST
Now use the toolkit on real situations. The hardest part is usually turning words into a correct sketch, so always draw first.

@@ 3 | TOPIC | Angles of elevation and depression | Measuring heights and distances by looking up or down from a horizontal line | MUST
# Angles of elevation and depression

## 1. What it covers
- The **angle of elevation** (looking up) and the **angle of depression** (looking down).
- Turning a word problem into a right triangle.
- Two-step problems with two angles from different positions.

## 2. Where it is used
- **Surveying:** the height of a building, tree or mountain without climbing it.
- **Aviation:** a pilot's glide path is usually a $3^\circ$ angle of descent.
- **Military and sports:** range finders, golf rangefinders, artillery.
- **Games:** the camera pitch angle; whether a turret can aim up at a flying target.
- **Lighthouses and ships:** the distance of a boat from the angle down to it.

## 3. What it means
Both angles are measured from a **horizontal** line, never from the vertical.
- **Elevation:** you look **up** from the horizontal to an object above you.
- **Depression:** you look **down** from the horizontal to an object below you.

**Key fact:** the angle of depression from A down to B **equals** the angle of elevation from B up to A. The two horizontal lines are parallel, so these are alternate angles.

**Analogy:** standing on a cliff looking down at a boat, and the person in the boat looking up at you: both of you see the same tilt of the line between you.

```text
  A  ________ horizontal
     \ ) depression angle
      \
       \  line of sight
        \
         \( elevation angle
  ________\ B
        horizontal
```

## 4. Formula & worked example
Most problems use **tangent**, because you usually know or want the horizontal distance (adjacent) and the height (opposite):
$$
\text{height} = \text{distance} \times \tan(\text{angle})
$$

**Example 1.** From 50 m away, the angle of elevation to the top of a tree is $35^\circ$. How tall is the tree?
- $h = 50\tan 35^\circ \approx 50(0.7002) \approx 35.0$ m

**Example 2.** From a 80 m cliff, the angle of depression to a boat is $12^\circ$. How far is the boat from the foot of the cliff?
- The angle of elevation from the boat to the cliff top is also $12^\circ$.
- $\tan 12^\circ = \frac{80}{d}$ → $d = \frac{80}{\tan 12^\circ} \approx \frac{80}{0.2126} \approx 376.4$ m

**Example 3: two angles.** From point P, the angle of elevation to a tower's top is $30^\circ$. Walking 40 m closer, it is $45^\circ$. Find the tower's height $h$.
- Let $x$ be the distance from the closer point to the tower.
- From the closer point: $\tan 45^\circ = \frac{h}{x}$ → $h = x$.
- From P: $\tan 30^\circ = \frac{h}{x + 40}$ → $\frac{\sqrt{3}}{3}(x + 40) = h = x$.
- $\sqrt{3}(x + 40) = 3x$ → $x(3 - \sqrt{3}) = 40\sqrt{3}$ → $x = \frac{40\sqrt{3}}{3 - \sqrt{3}} \approx 54.6$ m
- So $h \approx 54.6$ m.

**Example 4: add the eye height.** A person whose eyes are 1.6 m above the ground sees the top of a pole at $40^\circ$ from 10 m away.
- Height above eye level $= 10\tan 40^\circ \approx 8.39$ m; pole height $\approx 8.39 + 1.6 = 9.99$ m.

## 5. Common mistakes
- Measuring the angle of depression from the **vertical** (the cliff face). It is measured from the horizontal.
- Forgetting the observer's eye height when the problem gives it.
- Putting the angle of depression at the top of the cliff inside the triangle. Use the equal elevation angle at the bottom instead.

## 6. Practice
Q: From 30 m away, the elevation to a building's top is $60^\circ$. Height?
A: $30\tan 60^\circ = 30\sqrt{3} \approx 52.0$ m.

Q: From a 100 m tower, the angle of depression to a car is $25^\circ$. Horizontal distance?
A: $\frac{100}{\tan 25^\circ} \approx 214.5$ m.

Q: Is the angle of depression from A to B equal to the angle of elevation from B to A?
A: Yes. They are alternate angles between parallel horizontal lines.

Q: A kite string is 60 m long at an elevation of $50^\circ$. How high is the kite?
A: $60\sin 50^\circ \approx 46.0$ m (the string is the hypotenuse).

Q: A plane descends at $3^\circ$ from 900 m altitude. How far horizontally to touchdown?
A: $\frac{900}{\tan 3^\circ} \approx 17\,173$ m, about 17.2 km.

@@ 3 | TOPIC | Bearings and navigation | Compass and three-figure bearings, turning journeys into right triangles | MUST
# Bearings and navigation

## 1. What it covers
- **Three-figure bearings** (such as $045^\circ$) and **compass bearings** (such as N45°E).
- Converting a journey into north/south and east/west parts.
- Finding the distance and bearing back to the start.

## 2. Where it is used
- **Ships and aircraft:** courses and headings are given as bearings.
- **GPS and maps apps:** route directions, "head north-east".
- **Drones and robotics:** turning a target position into a heading.
- **Hiking and orienteering:** compass navigation.
- **Game maps:** converting a heading angle into x/y movement (note that maths angles start from east and go anticlockwise, while bearings start from north and go clockwise).

## 3. What it means
A **three-figure bearing** is the angle measured **clockwise from north**, always written with three digits: east is $090^\circ$, south $180^\circ$, west $270^\circ$.

A **compass bearing** like N30°E means "face north, then turn $30^\circ$ towards east".

To do maths with a journey, split each leg into **east** and **north** parts with sin and cos. For a bearing $\beta$ and distance $d$:
- east part $= d\sin\beta$
- north part $= d\cos\beta$

(This is the opposite of the usual maths x/y, because bearings are measured from north, not from the x-axis.)

**Analogy:** a treasure map that says "walk 10 paces at bearing 060". You can replace that with "walk about 8.7 paces east and 5 paces north".

## 4. Formula & worked example
$$
\Delta E = d\sin\beta \qquad \Delta N = d\cos\beta
$$
Negative $\Delta E$ means west; negative $\Delta N$ means south.
**Back bearing:** the bearing to return is $\beta \pm 180^\circ$.

**Example 1.** A ship sails 20 km on a bearing of $060^\circ$. How far east and north is it?
- $\Delta E = 20\sin 60^\circ \approx 17.32$ km east
- $\Delta N = 20\cos 60^\circ = 10$ km north

**Example 2.** A plane flies 300 km due east, then 400 km due north. How far is it from the start, and on what bearing?
- Distance $= \sqrt{300^2 + 400^2} = 500$ km.
- The angle east of north: $\tan\theta = \frac{300}{400}$ → $\theta \approx 36.9^\circ$.
- Bearing $\approx 036.9^\circ$.

**Example 3.** The bearing from A to B is $070^\circ$. What is the bearing from B to A?
- $070^\circ + 180^\circ = 250^\circ$.

**Example 4: two legs.** A hiker walks 5 km on $030^\circ$, then 8 km on $120^\circ$. Distance from the start?
- Leg 1: $\Delta E = 5\sin 30^\circ = 2.5$, $\Delta N = 5\cos 30^\circ \approx 4.33$
- Leg 2: $\Delta E = 8\sin 120^\circ \approx 6.93$, $\Delta N = 8\cos 120^\circ = -4$
- Total: $E \approx 9.43$, $N \approx 0.33$
- Distance $\approx \sqrt{9.43^2 + 0.33^2} \approx 9.44$ km
- (The two legs are exactly $90^\circ$ apart, so you could also use $\sqrt{5^2 + 8^2} = \sqrt{89} \approx 9.43$ km ✓.)

## 5. Common mistakes
- Measuring anticlockwise, or from east. Bearings go **clockwise from north**.
- Writing $45^\circ$ instead of $045^\circ$ for a three-figure bearing.
- Using cos for east and sin for north (the maths-axis habit). For bearings it's **sin → east, cos → north**.

## 6. Practice
Q: What three-figure bearing is due west?
A: $270^\circ$.

Q: Convert the compass bearing S40°E to a three-figure bearing.
A: Facing south ($180^\circ$), turning $40^\circ$ towards east (anticlockwise): $180^\circ - 40^\circ = 140^\circ$.

Q: A boat sails 12 km on $150^\circ$. How far south does it go?
A: $\Delta N = 12\cos 150^\circ \approx -10.39$, so 10.39 km south.

Q: The bearing of B from A is $215^\circ$. What is the bearing of A from B?
A: $215^\circ - 180^\circ = 035^\circ$.

Q: Walk 6 km north then 6 km east. Bearing back to the start?
A: Bearing from start is $045^\circ$, so back is $225^\circ$.

@@ 3 | TOPIC | Practice & review: Right-triangle trigonometry | Mixed problem set with worked answers covering all of Phase 1 | MUST
# Practice & review: Right-triangle trigonometry

**How to use this:** set a 45-minute timer. Do every problem on paper before you look at the answers. For each miss, write in your error log *what* went wrong (wrong ratio? calculator mode? sketch?) and redo it tomorrow. Aim for at least 80%.

## Problems
1. Legs 9 and 12. Find the hypotenuse.
2. A 30-60-90 triangle has short side 7. Find the other two sides exactly.
3. $\tan\theta = \frac{7}{24}$ ($\theta$ acute). Find $\sin\theta$ and $\sec\theta$.
4. Evaluate exactly: $\sin 60^\circ \cos 30^\circ - \sin 30^\circ \cos 60^\circ$.
5. Hypotenuse 25, angle $A = 38^\circ$. Find both legs.
6. opp $= 11$, adj $= 4$. Find the angle.
7. Solve for $x$ (acute): $\sin(3x) = \cos(x + 10^\circ)$.
8. From 45 m away, the elevation to the top of a statue is $28^\circ$. The observer's eyes are 1.5 m high. How tall is the statue?
9. From a 60 m lighthouse, the angles of depression to two boats in a straight line with it are $30^\circ$ and $20^\circ$. How far apart are the boats?
10. A ship sails 15 km on $040^\circ$, then 10 km on $130^\circ$. How far is it from port, and on what bearing?

## Answers
1. $\sqrt{81 + 144} = 15$.
2. Long side $7\sqrt{3}$, hypotenuse $14$.
3. hyp $= 25$; $\sin\theta = \frac{7}{25}$, $\sec\theta = \frac{25}{24}$.
4. $\frac{\sqrt{3}}{2}\cdot\frac{\sqrt{3}}{2} - \frac{1}{2}\cdot\frac{1}{2} = \frac{3}{4} - \frac{1}{4} = \frac{1}{2}$. (This is $\sin(60^\circ - 30^\circ)$, as you'll see in Phase 4.)
5. $a = 25\sin 38^\circ \approx 15.39$, $b = 25\cos 38^\circ \approx 19.70$.
6. $\tan^{-1}(2.75) \approx 70.0^\circ$.
7. $4x + 10^\circ = 90^\circ$ → $x = 20^\circ$.
8. $45\tan 28^\circ + 1.5 \approx 23.93 + 1.5 \approx 25.4$ m.
9. Near boat: $\frac{60}{\tan 30^\circ} \approx 103.9$ m. Far boat: $\frac{60}{\tan 20^\circ} \approx 164.8$ m. Apart $\approx 60.9$ m.
10. The legs are $90^\circ$ apart, so distance $= \sqrt{15^2 + 10^2} \approx 18.03$ km. At the port, the angle between the first leg and the straight line to the ship is $\tan^{-1}\frac{10}{15} \approx 33.7^\circ$ (turning clockwise), so bearing $\approx 040^\circ + 33.7^\circ = 073.7^\circ$. (Check with components: $E = 15\sin 40^\circ + 10\sin 130^\circ \approx 9.64 + 7.66 = 17.30$; $N = 15\cos 40^\circ + 10\cos 130^\circ \approx 11.49 - 6.43 = 5.06$; bearing $= \tan^{-1}\frac{17.30}{5.06} \approx 73.7^\circ$ ✓.)

## Flashcards
Q: Three tools for solving a right triangle?
A: Angle sum ($A + B = 90^\circ$), Pythagoras, and SOH-CAH-TOA.

Q: Which ratio do you use when you know the opposite side and want the adjacent?
A: Tangent.

Q: Exact $\sin 30^\circ$, $\cos 45^\circ$, $\tan 60^\circ$?
A: $\frac{1}{2}$, $\frac{\sqrt{2}}{2}$, $\sqrt{3}$.

Q: The unknown is in the denominator: $\cos 50^\circ = \frac{8}{x}$. What is $x$?
A: $x = \frac{8}{\cos 50^\circ} \approx 12.45$.

Q: Bearing: which trig function gives the east component?
A: Sine: $\Delta E = d\sin\beta$.
