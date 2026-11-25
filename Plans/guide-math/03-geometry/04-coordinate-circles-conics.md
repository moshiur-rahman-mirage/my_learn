@@ 1 | MODULE | Phase 4 — Coordinate Geometry (Core) | Distance and midpoint, dividing a segment, coordinate proofs, shoelace area, equation of a circle | MUST
# Phase 4: Coordinate geometry

Coordinate (analytic) geometry, invented by Descartes, puts shapes on a grid so that geometry problems become algebra problems. It's how computers handle geometry: every game engine, CAD program, GPS and mapping app stores shapes as coordinates and uses these formulas.

@@ 2 | MODULE | Analytic geometry | Distance, midpoint, section formula, slopes in proofs, polygon area, circles | MUST
With coordinates, you can prove and measure using only arithmetic.

@@ 3 | TOPIC | Distance and midpoint formulas | Deriving the distance formula from Pythagoras, midpoint as an average, finding endpoints | MUST
# Distance and midpoint formulas

## 1. What it covers
- The **distance formula** between two points (from Pythagoras).
- The **midpoint formula** (average the coordinates).
- Finding a missing endpoint given the midpoint.
- Distance in 3D.

## 2. Where it is used
- **Games:** distance checks for collisions, aggro range, nearest target.
- **Machine learning:** Euclidean distance in k-nearest neighbours and k-means clustering.
- **Maps and GPS:** "nearest restaurant" (on small scales; on the globe the haversine formula is used).
- **Graphics:** midpoints for subdividing lines and meshes.

## 3. What it means
Between $(x_1, y_1)$ and $(x_2, y_2)$, draw a right triangle: horizontal leg $x_2 - x_1$, vertical leg $y_2 - y_1$. The distance is the hypotenuse, so Pythagoras gives the formula.

The **midpoint** is halfway in each direction: the **average** of the x-coordinates and the average of the y-coordinates.

## 4. Formula & worked example
$$
d = \sqrt{(x_2 - x_1)^2 + (y_2 - y_1)^2} \qquad M = \left(\frac{x_1 + x_2}{2},\ \frac{y_1 + y_2}{2}\right)
$$
In 3D: $d = \sqrt{\Delta x^2 + \Delta y^2 + \Delta z^2}$.

**Example 1.** Distance from $(-2, 1)$ to $(4, 9)$: $\sqrt{36 + 64} = 10$.

**Example 2.** Midpoint of $(3, -5)$ and $(9, 1)$: $(6, -2)$.

**Example 3 (missing endpoint).** $M(2, 3)$ is the midpoint of $A(-1, 7)$ and $B$. Find $B$.
- $\frac{-1 + x}{2} = 2$ → $x = 5$; $\frac{7 + y}{2} = 3$ → $y = -1$ → $B(5, -1)$

**Example 4 (classify).** Is $A(0, 0)$, $B(4, 0)$, $C(2, 2\sqrt{3})$ equilateral?
- $AB = 4$, $BC = \sqrt{4 + 12} = 4$, $AC = \sqrt{4 + 12} = 4$ → yes.

**Example 5 (3D).** Distance from $(1, 2, 3)$ to $(4, 6, 15)$: $\sqrt{9 + 16 + 144} = 13$.

## 5. Common mistakes
- Subtracting in different orders for $x$ and $y$ (it doesn't matter after squaring, but be consistent).
- Forgetting to square negative differences correctly: $(-3)^2 = 9$.
- Subtracting instead of averaging for the midpoint.

## 6. Practice
Q: Distance between $(1, 1)$ and $(4, 5)$?
A: $5$.

Q: Midpoint of $(-6, 2)$ and $(4, 8)$?
A: $(-1, 5)$.

Q: Midpoint $(0, 0)$, one endpoint $(3, -7)$. Other endpoint?
A: $(-3, 7)$.

Q: Distance from the origin to $(5, 12)$?
A: $13$.

Q: Which theorem gives the distance formula?
A: Pythagoras.

@@ 3 | TOPIC | Dividing a segment in a given ratio | Section formula, points a fraction of the way along, linear interpolation | MUST
# Dividing a segment in a given ratio

## 1. What it covers
- Finding the point that divides $\overline{AB}$ in the ratio $m : n$ (the **section formula**).
- Finding a point a fraction $t$ of the way from $A$ to $B$.
- The link to **linear interpolation** ("lerp").
- The centroid of a triangle.

## 2. Where it is used
- **Animation and games:** `lerp(a, b, t)` moves an object smoothly from $A$ to $B$ as $t$ goes from 0 to 1; colour gradients blend the same way.
- **Graphics:** Bézier curves are built from repeated lerps.
- **Physics:** the centre of mass of two objects divides the segment between them in the inverse ratio of their masses.
- **Data:** interpolating missing values between two readings.

## 3. What it means
A point a fraction $t$ of the way from $A$ to $B$ is $A + t(B - A)$: start at $A$ and move $t$ of the full journey. For $t = \frac{1}{2}$ you get the midpoint.

**Ratio $m : n$** means the point is $\frac{m}{m + n}$ of the way from $A$ to $B$. Written as a weighted average, $B$'s weight is $m$ and $A$'s weight is $n$ (each endpoint is weighted by the **other** part of the ratio).

**Centroid** (balance point) of a triangle: the average of the three vertices. It lies $\frac{2}{3}$ of the way along each median.

## 4. Formula & worked example
$$
P = A + t(B - A) \qquad P = \left(\frac{nx_1 + mx_2}{m + n},\ \frac{ny_1 + my_2}{m + n}\right) \qquad G = \left(\frac{x_1 + x_2 + x_3}{3},\ \frac{y_1 + y_2 + y_3}{3}\right)
$$

**Example 1.** Divide $A(1, 2)$ to $B(10, 14)$ in the ratio $1 : 2$.
- $t = \frac{1}{3}$: $P = (1 + 3,\ 2 + 4) = (4, 6)$

**Example 2.** Divide $A(-2, 5)$ to $B(6, -3)$ in the ratio $3 : 1$.
- $t = \frac{3}{4}$: $P = (-2 + 6,\ 5 - 6) = (4, -1)$

**Example 3 (centroid).** Triangle $(0, 0)$, $(6, 0)$, $(3, 9)$: $G = (3, 3)$.

**Example 4 (lerp in code).**
```python
def lerp(a, b, t):
    return a + (b - a) * t
start, end = (100, 400), (500, 100)
for t in (0, 0.25, 0.5, 1):
    print(t, (lerp(start[0], end[0], t), lerp(start[1], end[1], t)))
# 0 (100, 400) / 0.25 (200.0, 325.0) / 0.5 (300.0, 250.0) / 1 (500, 100)
```
Each frame of an animation increases `t` a little, and `lerp` returns the point that fraction of the way along. At $t = 0.5$ it returns the midpoint $(300, 250)$; at $t = 1$, the end point $(500, 100)$.

**Example 5 (centre of mass).** Masses of 2 kg at $x = 0$ and 6 kg at $x = 8$: the balance point divides the segment $6 : 2 = 3 : 1$ from the lighter mass → $x = 6$.

## 5. Common mistakes
- Using $\frac{m}{n}$ instead of $\frac{m}{m + n}$ as the fraction.
- Swapping the weights in the section formula.
- Confusing "from A" with "from B".

## 6. Practice
Q: Point $\frac{1}{4}$ of the way from $(0, 0)$ to $(8, 12)$?
A: $(2, 3)$.

Q: Divide $(2, 1)$ to $(11, 7)$ in the ratio $2 : 1$.
A: $(8, 5)$.

Q: Centroid of $(1, 2)$, $(5, 2)$, $(3, 8)$?
A: $(3, 4)$.

Q: What does `lerp(a, b, 0.5)` return?
A: The midpoint of $a$ and $b$.

Q: Ratio $2 : 3$. What fraction of the way from $A$ is the point?
A: $\frac{2}{5}$.

@@ 3 | TOPIC | Coordinate proofs with slopes and distances | Proving parallel, perpendicular, parallelograms, right triangles and other properties with coordinates | MUST
# Coordinate proofs with slopes and distances

## 1. What it covers
- Using **slopes** (parallel: equal; perpendicular: product $-1$) and **distances** to prove properties.
- Classifying triangles and quadrilaterals from coordinates.
- Choosing convenient coordinates for general proofs.

## 2. Where it is used
- **Computer graphics and CAD:** checking that walls are perpendicular, that a shape is a rectangle, that lines are parallel.
- **Robotics:** verifying path geometry.
- **Geometry proofs:** coordinate proofs are often easier than classical ones.
- **GIS:** validating plot boundaries.

## 3. What it means
**Toolbox:**
| To show... | Use |
|---|---|
| Segments are parallel | Equal slopes |
| Segments are perpendicular | Slopes multiply to $-1$ (or one horizontal, one vertical) |
| Segments are equal | Distance formula |
| Segments bisect each other | Same midpoint |

**General proofs:** place the figure cleverly, e.g. a right angle at the origin with sides along the axes, using letters like $(a, 0)$ and $(0, b)$. Then the proof covers **every** such figure.

## 4. Formula & worked example
**Example 1.** Is $A(1, 1)$, $B(5, 3)$, $C(7, 7)$, $D(3, 5)$ a parallelogram?
- Slopes: $AB = \frac{2}{4} = \frac{1}{2}$, $DC = \frac{2}{4} = \frac{1}{2}$; $BC = \frac{4}{2} = 2$, $AD = \frac{4}{2} = 2$ → both pairs parallel → yes.
- Is it a rhombus? $AB = \sqrt{20}$, $BC = \sqrt{20}$ → all sides $\sqrt{20}$ → yes, a rhombus (slopes $\frac{1}{2}$ and $2$ aren't perpendicular, so not a square).

**Example 2.** Is $P(-1, 2)$, $Q(3, 4)$, $R(5, 0)$ a right triangle?
- $PQ$: $\frac{2}{4} = \frac{1}{2}$; $QR$: $\frac{-4}{2} = -2$; product $-1$ → right angle at $Q$.

**Example 3 (general proof).** Prove the midpoint of the hypotenuse of a right triangle is equally far from all three vertices.
- Place $O(0, 0)$, $A(2a, 0)$, $B(0, 2b)$. Midpoint of $AB$: $M(a, b)$.
- $MO = \sqrt{a^2 + b^2}$, $MA = \sqrt{a^2 + b^2}$, $MB = \sqrt{a^2 + b^2}$ ✓ (using $2a$, $2b$ avoids fractions).

**Example 4 (diagonals).** Show the diagonals of the parallelogram in Example 1 bisect each other.
- Midpoint of $AC$: $(4, 4)$; midpoint of $BD$: $(4, 4)$ ✓

## 5. Common mistakes
- Checking only one pair of sides for a parallelogram.
- Concluding "rectangle" from equal opposite sides alone (that's any parallelogram).
- Placing a general figure with too many specific numbers, so the proof only covers one case.

## 6. Practice
Q: How do you show two segments are perpendicular with coordinates?
A: Show their slopes multiply to $-1$.

Q: $A(0, 0)$, $B(4, 0)$, $C(4, 3)$, $D(0, 3)$. Rectangle?
A: Yes: opposite sides parallel, adjacent sides horizontal/vertical.

Q: Show $(0, 0)$, $(3, 4)$, $(6, 0)$ is isosceles.
A: Two sides have length 5.

Q: How do you show diagonals bisect each other?
A: Show they have the same midpoint.

Q: Why use $(2a, 0)$ instead of $(a, 0)$ in some proofs?
A: Midpoints come out without fractions.

@@ 3 | TOPIC | Area on the coordinate plane and the shoelace formula | Areas of polygons from vertices, the shoelace formula, signed area | MUST
# Area on the coordinate plane and the shoelace formula

## 1. What it covers
- Finding polygon areas from coordinates by **decomposition** (boxing in and subtracting).
- The **shoelace formula** for any simple polygon.
- Perimeter from coordinates.
- What the sign of the shoelace result means (orientation).

## 2. Where it is used
- **GIS and land records:** computing plot areas from surveyed corner coordinates.
- **Computer graphics:** polygon area and winding order (clockwise vs anticlockwise decides which side of a triangle faces the camera, used for back-face culling).
- **Robotics:** area covered by a cleaning robot.
- **Computational geometry libraries** (Shapely, PostGIS) use this formula.

## 3. What it means
**Boxing method:** draw the smallest rectangle around the polygon, then subtract the right triangles in the corners.

**Shoelace formula:** list the vertices in order (around the shape) and repeat the first at the end. Multiply diagonally down-right and add; multiply diagonally up-right and subtract. Half the absolute value is the area. The criss-cross pattern looks like shoelaces.

The result before taking the absolute value is **positive if the vertices go anticlockwise**, negative if clockwise.

## 4. Formula & worked example
$$
A = \frac{1}{2}\left\lvert \sum_{i=1}^{n} (x_iy_{i+1} - x_{i+1}y_i) \right\rvert \quad (\text{with } x_{n+1} = x_1,\ y_{n+1} = y_1)
$$

**Example 1 (triangle).** $A(1, 1)$, $B(5, 2)$, $C(3, 6)$.
- Sum: $(1 \cdot 2 - 5 \cdot 1) + (5 \cdot 6 - 3 \cdot 2) + (3 \cdot 1 - 1 \cdot 6) = -3 + 24 - 3 = 18$
- Area $= 9$

**Example 2 (quadrilateral).** $(0, 0)$, $(4, 0)$, $(5, 3)$, $(1, 4)$.
- $(0 - 0) + (12 - 0) + (20 - 3) + (0 - 0) = 29$ → area $14.5$

**Example 3 (boxing).** Triangle $(0, 0)$, $(6, 2)$, $(2, 5)$.
- Box $6 \times 5 = 30$; corner triangles: $\frac{1}{2}(6)(2) = 6$, $\frac{1}{2}(4)(3) = 6$, $\frac{1}{2}(2)(5) = 5$ → $30 - 17 = 13$
- Shoelace check: $(0 - 0) + (30 - 4) + (0 - 0) = 26$ → $13$ ✓

**Example 4 (code).**
```python
def shoelace(pts):
    s = 0
    for i in range(len(pts)):
        x1, y1 = pts[i]
        x2, y2 = pts[(i + 1) % len(pts)]   # wrap around to the first point
        s += x1 * y2 - x2 * y1
    return abs(s) / 2
print(shoelace([(0, 0), (4, 0), (5, 3), (1, 4)]))   # 14.5
```
`(i + 1) % len(pts)` makes the last vertex pair up with the first, closing the polygon. Remove `abs` to get the signed area (positive = anticlockwise).

## 5. Common mistakes
- Listing vertices out of order (crossing the shape).
- Forgetting to close the loop (pair the last vertex with the first).
- Forgetting the $\frac{1}{2}$ or the absolute value.

## 6. Practice
Q: Shoelace area of $(0, 0)$, $(4, 0)$, $(0, 3)$?
A: $6$.

Q: Area of the square $(1, 1)$, $(4, 1)$, $(4, 4)$, $(1, 4)$?
A: $9$.

Q: What does a negative shoelace sum (before absolute value) mean?
A: The vertices are listed clockwise.

Q: Area of triangle $(2, 3)$, $(8, 3)$, $(5, 9)$?
A: Base 6, height 6 → $18$.

Q: Why must vertices be listed in order around the polygon?
A: Otherwise the edges cross and the formula gives a wrong area.

@@ 3 | TOPIC | Equation of a circle | (x − h)² + (y − k)² = r², completing the square, points inside/outside, tangents | MUST
# Equation of a circle

## 1. What it covers
- The standard form $(x - h)^2 + (y - k)^2 = r^2$ (centre $(h, k)$, radius $r$).
- Converting the general form $x^2 + y^2 + Dx + Ey + F = 0$ by **completing the square**.
- Testing whether a point is inside, on or outside a circle.
- The tangent line at a point.

## 2. Where it is used
- **Games:** circular collision detection (`dx*dx + dy*dy <= r*r`).
- **GPS and cell towers:** all points at a given distance from a tower form a circle; intersecting circles locates a phone.
- **CAD and CNC:** circular arcs and holes.
- **UI:** detecting a click inside a circular button.

## 3. What it means
A circle is every point at distance $r$ from the centre $(h, k)$. Write that with the distance formula and square both sides:
$$
\sqrt{(x - h)^2 + (y - k)^2} = r \ \Rightarrow\ (x - h)^2 + (y - k)^2 = r^2
$$

**Inside/outside test:** compute $(x - h)^2 + (y - k)^2$ and compare with $r^2$: less → inside, equal → on, greater → outside. No square root needed, which is why games use squared distances.

**Tangent at a point:** perpendicular to the radius at that point.

## 4. Formula & worked example
$$
(x - h)^2 + (y - k)^2 = r^2
$$

**Example 1.** Centre $(3, -2)$, radius 5: $(x - 3)^2 + (y + 2)^2 = 25$.

**Example 2.** Centre and radius of $(x + 1)^2 + (y - 4)^2 = 18$: $(-1, 4)$, $r = 3\sqrt{2}$.

**Example 3 (complete the square).** $x^2 + y^2 - 6x + 8y - 11 = 0$
- $(x^2 - 6x + 9) + (y^2 + 8y + 16) = 11 + 9 + 16 = 36$ → $(x - 3)^2 + (y + 4)^2 = 36$ → centre $(3, -4)$, $r = 6$

**Example 4 (inside?).** Is $(1, 2)$ inside $(x - 3)^2 + (y + 1)^2 = 16$?
- $4 + 9 = 13 < 16$ → inside.

**Example 5 (tangent).** Tangent to $x^2 + y^2 = 25$ at $(3, 4)$.
- Radius slope $\frac{4}{3}$ → tangent slope $-\frac{3}{4}$: $y - 4 = -\frac{3}{4}(x - 3)$ → $3x + 4y = 25$

**Example 6 (circle from a diameter).** Endpoints $(-2, 1)$ and $(6, 7)$.
- Centre $(2, 4)$; $r = \frac{1}{2}\sqrt{64 + 36} = 5$ → $(x - 2)^2 + (y - 4)^2 = 25$

## 5. Common mistakes
- Sign of the centre: $(x + 1)^2$ means $h = -1$.
- Forgetting to take the square root for $r$ (if the right side is 18, $r = \sqrt{18}$, not 18).
- Not adding the same numbers to both sides when completing the square.

## 6. Practice
Q: Equation of the circle with centre $(0, 0)$ and radius 7?
A: $x^2 + y^2 = 49$.

Q: Centre and radius of $(x - 5)^2 + (y + 2)^2 = 9$?
A: $(5, -2)$, $r = 3$.

Q: Convert $x^2 + y^2 + 4x - 2y - 4 = 0$.
A: $(x + 2)^2 + (y - 1)^2 = 9$: centre $(-2, 1)$, $r = 3$.

Q: Is $(5, 5)$ inside $x^2 + y^2 = 49$?
A: $50 > 49$ → just outside.

Q: Tangent to a circle at a point is ... to the radius at that point?
A: Perpendicular.

@@ 3 | TOPIC | Practice & review: Coordinate geometry | Mixed problem set with worked answers | MUST
# Practice & review: Coordinate geometry

**How to use this:** 40 minutes, no calculator.

## Problems
1. Find the distance and midpoint between $(-3, 7)$ and $(5, 1)$.
2. $M(4, -1)$ is the midpoint of $\overline{PQ}$ with $P(10, 3)$. Find $Q$.
3. Find the point dividing $(-4, 2)$ to $(8, 11)$ in the ratio $1 : 2$.
4. Show $A(-2, -1)$, $B(2, 1)$, $C(1, 3)$, $D(-3, 1)$ is a rectangle.
5. Shoelace area of $(1, 0)$, $(5, 1)$, $(4, 5)$, $(0, 3)$.
6. Write $x^2 + y^2 - 10x + 2y + 1 = 0$ in standard form.
7. Tangent to $(x - 1)^2 + (y - 2)^2 = 25$ at $(4, 6)$.
8. Centroid of $(-1, 4)$, $(5, 2)$, $(2, -3)$.

## Answers
1. $d = \sqrt{64 + 36} = 10$; $M(1, 4)$.
2. $Q(-2, -5)$.
3. $t = \frac{1}{3}$: $(-4 + 4,\ 2 + 3) = (0, 5)$.
4. Slopes: $AB = \frac{1}{2}$, $BC = -2$, $CD = \frac{1}{2}$, $DA = -2$; opposite sides parallel and $\frac{1}{2} \times (-2) = -1$ → right angles → rectangle.
5. $(1 - 0) + (25 - 4) + (12 - 0) + (0 - 3) = 31$ → $15.5$.
6. $(x - 5)^2 + (y + 1)^2 = 25$: centre $(5, -1)$, $r = 5$.
7. Radius slope $\frac{4}{3}$ → tangent slope $-\frac{3}{4}$: $y - 6 = -\frac{3}{4}(x - 4)$ → $3x + 4y = 36$.
8. $(2, 1)$.

## Flashcards
Q: Distance formula?
A: $\sqrt{(x_2 - x_1)^2 + (y_2 - y_1)^2}$.

Q: Standard form of a circle?
A: $(x - h)^2 + (y - k)^2 = r^2$.

Q: Shoelace formula idea?
A: Half the absolute value of $\sum(x_iy_{i+1} - x_{i+1}y_i)$ around the polygon.

Q: Centroid of a triangle?
A: The average of the three vertices.

@@ 1 | MODULE | Phase 5 — Circle Theorems (Core) | Chords, tangents, secants, arcs; central and inscribed angles; cyclic quadrilaterals; segment lengths | SHOULD
# Phase 5: Circle theorems

Circles have surprisingly many exact angle and length relationships. These theorems appear in exams, in engineering (gears, arches, wheels) and in computer graphics (fitting circles through points). For arc length and sector area, see the **Trigonometry** roadmap (Phase 2).

@@ 2 | MODULE | Angles and lengths in circles | Vocabulary, inscribed angles, cyclic quadrilaterals, tangents, chord and secant lengths | SHOULD
Most circle problems are solved by spotting one of a few key patterns.

@@ 3 | TOPIC | Circle vocabulary: chords, tangents, secants and arcs | Radius, diameter, chord, secant, tangent, minor and major arcs, perpendicular from centre to chord | SHOULD
# Circle vocabulary: chords, tangents, secants and arcs

## 1. What it covers
- **Radius, diameter, chord, secant, tangent**.
- **Arcs** (minor, major, semicircle) and their measure in degrees.
- The perpendicular from the centre to a chord **bisects** the chord.
- Equal chords are equally far from the centre.

## 2. Where it is used
- **Engineering:** finding the radius of a pipe or arch from a measured chord and its height (sagitta).
- **Archaeology:** reconstructing the full size of a broken plate from a fragment.
- **Road design:** curves as circular arcs.
- **Computer graphics:** fitting a circle through three points.

## 3. What it means
- **Chord:** a segment joining two points on the circle. The longest chord is the **diameter**.
- **Secant:** a line that cuts the circle at two points.
- **Tangent:** a line that touches the circle at exactly one point.
- **Arc:** part of the circle. A **minor arc** is less than $180^\circ$, a **major arc** more. An arc's measure equals the central angle that cuts it off.

**Key fact:** the line from the centre perpendicular to a chord cuts it exactly in half. (The centre is equally far from both ends, so it lies on the chord's perpendicular bisector.) This gives right triangles: $r^2 = d^2 + \left(\frac{c}{2}\right)^2$, where $d$ is the distance from the centre to the chord.

## 4. Formula & worked example
$$
r^2 = d^2 + \left(\frac{c}{2}\right)^2
$$

**Example 1.** A chord of length 16 is 6 from the centre. Radius? → $\sqrt{36 + 64} = 10$.

**Example 2.** A circle of radius 13 has a chord 5 from the centre. Chord length? → half $= \sqrt{169 - 25} = 12$ → $24$.

**Example 3 (broken plate).** A fragment's chord is 20 cm and the arc's height above the chord (sagitta) is 4 cm. Radius?
- $d = r - 4$: $r^2 = (r - 4)^2 + 100$ → $0 = -8r + 16 + 100$ → $r = 14.5$ cm

**Example 4.** An arc has a central angle of $110^\circ$. Its major arc measures $250^\circ$.

## 5. Common mistakes
- Using the full chord instead of half in the right triangle.
- Confusing a secant (line) with a chord (segment).
- Assuming a line that "looks" tangent is tangent.

## 6. Practice
Q: Radius 10, chord 12. Distance from the centre to the chord?
A: $8$.

Q: What does the perpendicular from the centre do to a chord?
A: It bisects it.

Q: A minor arc is $75^\circ$. Major arc?
A: $285^\circ$.

Q: Longest chord in a circle?
A: The diameter.

Q: Chord 10 cm, sagitta 1 cm. Radius?
A: $r^2 = (r - 1)^2 + 25$ → $r = 13$ cm.

@@ 3 | TOPIC | Central and inscribed angles | Inscribed angle = half the central angle, angles in the same segment, angle in a semicircle (Thales) | SHOULD
# Central and inscribed angles

## 1. What it covers
- **Central angle** (vertex at the centre) = arc measure.
- **Inscribed angle** (vertex on the circle) = half the arc (half the central angle on the same arc).
- Inscribed angles on the same arc are equal.
- **Thales' theorem:** an angle inscribed in a semicircle is $90^\circ$.

## 2. Where it is used
- **Carpentry:** finding the centre of a circular board with a set square (Thales).
- **Photography and theatres:** every seat on a circle through the stage edges sees the stage at the same angle (inscribed angle theorem), used in designing viewing positions.
- **Football analytics:** shooting angles to the goal.
- **Navigation:** fixing position from the angle between two landmarks.

## 3. What it means
The **inscribed angle theorem**: an angle drawn from a point on the circle to the ends of an arc is **half** the central angle on that arc.

Consequences:
- All inscribed angles standing on the same arc are **equal** (wherever the vertex sits on the remaining arc).
- If the arc is a **semicircle** (the chord is a diameter), the central angle is $180^\circ$, so the inscribed angle is $90^\circ$ (Thales).

**Carpenter's trick:** put the corner of a set square on the edge of a circular board; where its two edges cross the circle are the ends of a diameter. Do it twice; the diameters cross at the centre.

## 4. Formula & worked example
$$
\text{inscribed angle} = \tfrac{1}{2} \times \text{central angle} = \tfrac{1}{2} \times \text{arc}
$$

**Example 1.** Central angle $100^\circ$ → inscribed angle on the same arc $50^\circ$.

**Example 2.** An inscribed angle is $35^\circ$. Its arc? → $70^\circ$.

**Example 3.** $AB$ is a diameter and $C$ is on the circle; $\angle CAB = 28^\circ$. Find $\angle CBA$.
- $\angle ACB = 90^\circ$ (Thales) → $\angle CBA = 62^\circ$

**Example 4 (same arc).** Points $C$ and $D$ on the same side of chord $AB$; $\angle ACB = 40^\circ$. Then $\angle ADB = 40^\circ$.

**Example 5 (algebra).** Inscribed angle $3x + 5$ and central angle $8x - 10$ on the same arc.
- $8x - 10 = 2(3x + 5)$ → $x = 10$ → inscribed $35^\circ$, central $70^\circ$

## 5. Common mistakes
- Making the inscribed angle equal to the central angle (it's half).
- Using inscribed angles that stand on different arcs.
- Missing a diameter (and the free right angle) in a diagram.

## 6. Practice
Q: Central angle $140^\circ$. Inscribed angle on the same arc?
A: $70^\circ$.

Q: An angle inscribed in a semicircle measures?
A: $90^\circ$.

Q: Two inscribed angles stand on the same arc. One is $48^\circ$. The other?
A: $48^\circ$.

Q: Inscribed angle $55^\circ$. Arc measure?
A: $110^\circ$.

Q: How can a set square find the centre of a circle?
A: Its corner on the circle marks a diameter between where its edges cross the circle; two diameters cross at the centre.

@@ 3 | TOPIC | Cyclic quadrilaterals | Opposite angles supplementary, exterior angle equals interior opposite angle, testing for cyclic | SHOULD
# Cyclic quadrilaterals

## 1. What it covers
- A **cyclic quadrilateral** has all four vertices on a circle.
- **Opposite angles add to $180^\circ$** (and its converse).
- An exterior angle equals the interior opposite angle.
- Which quadrilaterals are always cyclic.

## 2. Where it is used
- **Geometry problems and olympiads:** a powerful tool for finding angles.
- **Engineering linkages:** four-bar linkage analysis.
- **Computer vision and graphics:** checking whether four points lie on one circle (used in Delaunay triangulation, which builds good triangle meshes for terrain and finite-element analysis).

## 3. What it means
Each angle of a cyclic quadrilateral is an inscribed angle. Two opposite angles stand on arcs that together make the whole circle ($360^\circ$). Half of $360^\circ$ is $180^\circ$, so **opposite angles are supplementary**.

**Converse:** if a quadrilateral's opposite angles add to $180^\circ$, its vertices lie on a circle.

Rectangles and squares are always cyclic; a non-rectangular parallelogram never is; isosceles trapezoids always are.

## 4. Formula & worked example
$$
\angle A + \angle C = 180^\circ \qquad \angle B + \angle D = 180^\circ
$$

**Example 1.** Cyclic $ABCD$ with $\angle A = 85^\circ$, $\angle B = 110^\circ$. Find $\angle C$ and $\angle D$. → $95^\circ$ and $70^\circ$.

**Example 2.** Is a quadrilateral with angles $70^\circ$, $100^\circ$, $110^\circ$, $80^\circ$ (in order) cyclic?
- $70 + 110 = 180$ ✓ and $100 + 80 = 180$ ✓ → yes.

**Example 3 (exterior angle).** Side $DC$ of cyclic $ABCD$ is extended past $C$; $\angle A = 75^\circ$. Exterior angle at $C$? → $75^\circ$ (it's supplementary to $\angle C$, which is supplementary to $\angle A$).

**Example 4 (algebra).** Opposite angles $2x + 30$ and $4x - 30$. → $6x = 180$ → $x = 30$ → $90^\circ$ and $90^\circ$.

## 5. Common mistakes
- Adding **adjacent** angles to $180^\circ$ instead of opposite ones.
- Assuming every quadrilateral is cyclic.
- Forgetting the converse can prove four points are concyclic.

## 6. Practice
Q: In a cyclic quadrilateral, $\angle B = 64^\circ$. Find $\angle D$.
A: $116^\circ$.

Q: Is every rectangle cyclic?
A: Yes (opposite angles $90^\circ + 90^\circ$).

Q: Angles $95^\circ$, $85^\circ$, $85^\circ$, $95^\circ$ in order. Cyclic?
A: Yes, $95 + 85 = 180$ for both opposite pairs.

Q: Can a non-rectangular parallelogram be cyclic?
A: No (opposite angles are equal, so they'd both have to be $90^\circ$).

Q: Exterior angle of a cyclic quadrilateral equals which angle?
A: The interior opposite angle.

@@ 3 | TOPIC | Tangents to circles | Tangent ⊥ radius, two tangents from an external point, tangent length, tangent–chord angle | SHOULD
# Tangents to circles

## 1. What it covers
- A tangent is **perpendicular** to the radius at the point of contact.
- Two tangents from an external point are **equal in length**.
- Tangent length with Pythagoras.
- The **tangent–chord angle** equals half the intercepted arc (alternate segment theorem).

## 2. Where it is used
- **Engineering:** belts around pulleys (the straight parts are tangents), gear teeth design.
- **Roads and railways:** straight track joins a circular curve along a tangent so there's no sudden turn.
- **Astronomy and line of sight:** how far you can see to the horizon (your line of sight is tangent to the Earth).
- **Graphics:** smooth joins between lines and arcs in fonts and CAD.

## 3. What it means
A tangent just touches the circle. The radius to the touching point is the **shortest** path from the centre to the tangent line, and the shortest path to a line is always perpendicular. So **tangent ⊥ radius**.

**Two tangents from one point** to a circle have equal lengths, like two equal ropes from a point to a ball (the two right triangles are congruent by HL).

**Horizon distance:** from height $h$ above a sphere of radius $R$, the tangent length is $\sqrt{(R + h)^2 - R^2} = \sqrt{2Rh + h^2} \approx \sqrt{2Rh}$.

## 4. Formula & worked example
$$
\text{tangent length from } P = \sqrt{OP^2 - r^2} \qquad \text{tangent–chord angle} = \tfrac{1}{2}\,\text{arc}
$$

**Example 1.** $P$ is 13 cm from the centre of a circle with radius 5. Tangent length? → $\sqrt{169 - 25} = 12$ cm.

**Example 2.** Two tangents from $P$ touch the circle at $A$ and $B$; $PA = 3x + 2$, $PB = 5x - 6$. → $x = 4$, both 14.

**Example 3.** Tangents from $P$ meet at angle $\angle APB = 50^\circ$. Find $\angle AOB$ (centre $O$).
- Quadrilateral $PAOB$ has two right angles: $\angle AOB = 360 - 90 - 90 - 50 = 130^\circ$

**Example 4 (horizon).** How far can you see from a 100 m tower? ($R \approx 6371$ km $= 6\,371\,000$ m)
- $d \approx \sqrt{2 \times 6\,371\,000 \times 100} \approx 35\,700$ m $\approx 36$ km

**Example 5 (tangent–chord).** A tangent and a chord meet at the point of contact, cutting off an arc of $120^\circ$. Angle between them? → $60^\circ$.

## 5. Common mistakes
- Forgetting the right angle between the tangent and the radius.
- Using the distance to the circle's edge instead of to the centre in the tangent length formula.
- Confusing a tangent with a secant that's nearly tangent.

## 6. Practice
Q: Angle between a tangent and the radius at the point of contact?
A: $90^\circ$.

Q: Radius 8, $P$ is 17 from the centre. Tangent length?
A: $15$.

Q: Two tangents from $P$; one is 9 cm. The other?
A: $9$ cm.

Q: Tangents from $P$ make $\angle AOB = 110^\circ$. Find $\angle APB$.
A: $70^\circ$.

Q: Approximate horizon distance from a height of 2 m?
A: $\sqrt{2 \times 6\,371\,000 \times 2} \approx 5$ km.

@@ 3 | TOPIC | Chords, secants and segment lengths | Intersecting chords, secant–secant and tangent–secant products (power of a point) | SHOULD
# Chords, secants and segment lengths

## 1. What it covers
- **Intersecting chords:** $a \cdot b = c \cdot d$.
- **Two secants** from an external point: whole × outside part are equal.
- **Tangent and secant:** $t^2 = \text{whole} \times \text{outside}$.
- The unifying idea: **power of a point**.

## 2. Where it is used
- **Engineering:** finding a pipe's or tunnel's radius from accessible measurements.
- **Optics:** lens and mirror geometry.
- **Geometry problems:** finding lengths quickly without trig.
- **Computational geometry:** the power of a point is used in circle and sphere intersection tests.

## 3. What it means
All three rules come from **similar triangles** created by inscribed angles. They share one idea: from a point $P$, every line through $P$ meeting the circle at two points $A$ and $B$ gives the same product $PA \cdot PB$. This constant is the **power of the point**.
- Inside the circle: the two pieces of each chord.
- Outside: the outside part × the whole secant.
- A tangent is a secant whose two points have merged, so its product is $t \cdot t = t^2$.

## 4. Formula & worked example
$$
\text{chords: } a \cdot b = c \cdot d \qquad \text{secants: } PA \cdot PB = PC \cdot PD \qquad \text{tangent: } PT^2 = PA \cdot PB
$$

**Example 1 (chords).** Two chords cross; one is split into 4 and 9, the other into 6 and $x$. → $36 = 6x$ → $x = 6$.

**Example 2 (secants).** From $P$, a secant has outside part 5 and whole length 12; another has outside part 4 and whole $x$. → $60 = 4x$ → $x = 15$.

**Example 3 (tangent–secant).** A tangent from $P$ has length $t$; a secant from $P$ has outside part 4 and whole 16. → $t^2 = 64$ → $t = 8$.

**Example 4 (tunnel).** An arched tunnel: the floor chord is 8 m wide and the arch's highest point is 6 m above the floor's midpoint. Radius of the arch circle?
- The vertical diameter through the midpoint is split into 6 and $2r - 6$; the chord into 4 and 4: $6(2r - 6) = 16$ → $2r - 6 = \frac{8}{3}$ → $r \approx 4.33$ m

## 5. Common mistakes
- Using "outside × inside" instead of "outside × **whole**" for secants.
- Mixing segment pieces from different chords.
- Forgetting the tangent is squared.

## 6. Practice
Q: Chords cross with pieces 3 and 8, and 4 and $x$. Find $x$.
A: $6$.

Q: Tangent length 6; a secant's outside part is 4. Whole secant?
A: $36 = 4 \cdot w$ → $9$.

Q: Secants: outside 2, whole 10; outside 4, whole $x$. Find $x$.
A: $5$.

Q: What is the "power of a point"?
A: The constant product $PA \cdot PB$ for any line through $P$ meeting the circle at $A$ and $B$.

Q: Which earlier idea proves these rules?
A: Similar triangles (from equal inscribed angles).

@@ 3 | TOPIC | Practice & review: Circle theorems | Mixed problem set with worked answers | SHOULD
# Practice & review: Circle theorems

**How to use this:** 40 minutes. Mark every right angle and equal length on your diagram first.

## Problems
1. A circle of radius 17 has a chord 15 from the centre. Chord length?
2. Inscribed angle $\angle ACB = 38^\circ$. Central angle $\angle AOB$?
3. $AB$ is a diameter, $C$ on the circle, $AC = 7$, $BC = 24$. Find the radius.
4. Cyclic $PQRS$: $\angle P = (5x)^\circ$, $\angle R = (3x + 20)^\circ$. Find $\angle P$.
5. Tangents $PA$ and $PB$ with $\angle APB = 64^\circ$. Find $\angle OAB$ ($O$ = centre).
6. Chords cross with pieces $x$ and $x + 2$, and 4 and 6. Find $x$.
7. A tangent of length 10 and a secant from the same point with outside part 5. Find the inside part of the secant.

## Answers
1. Half-chord $= \sqrt{289 - 225} = 8$ → $16$.
2. $76^\circ$.
3. $\angle C = 90^\circ$ → $AB = 25$ → $r = 12.5$.
4. $8x + 20 = 180$ → $x = 20$ → $100^\circ$.
5. $\angle AOB = 116^\circ$; $\triangle OAB$ is isosceles → $\angle OAB = 32^\circ$.
6. $x(x + 2) = 24$ → $x = 4$.
7. $100 = 5 \cdot w$ → whole $= 20$ → inside part $15$.

## Flashcards
Q: Inscribed angle vs central angle on the same arc?
A: Inscribed = half the central angle.

Q: Angle in a semicircle?
A: $90^\circ$ (Thales).

Q: Opposite angles of a cyclic quadrilateral?
A: Add to $180^\circ$.

Q: Tangent–secant rule?
A: $t^2 = \text{outside} \times \text{whole}$.

@@ 1 | MODULE | Phase 6 — Conic Sections (Advanced) | Parabola, ellipse, hyperbola from focus definitions; standard forms; identifying conics; applications | NICE
# Phase 6: Conic sections

Slice a double cone with a plane and you get a **circle**, **ellipse**, **parabola** or **hyperbola**, depending on the angle of the cut. These curves describe planetary orbits, satellite dishes, headlights, bridge cables, GPS-like navigation and the paths of thrown objects.

@@ 2 | MODULE | The conic sections | Parabolas, ellipses, hyperbolas, identification and applications | NICE
Each conic has a simple distance definition, and each definition leads to its equation.

@@ 3 | TOPIC | Parabolas from focus and directrix | Definition, y² = 4px and x² = 4py, vertex form, reflective property | NICE
# Parabolas from focus and directrix

## 1. What it covers
- The **definition:** all points equally far from a **focus** (point) and a **directrix** (line).
- Standard forms $x^2 = 4py$ and $y^2 = 4px$; shifted forms.
- Finding the focus, directrix and axis.
- The **reflective property**.

## 2. Where it is used
- **Satellite dishes and radio telescopes:** incoming parallel signals reflect to the focus, where the receiver sits.
- **Car headlights and torches:** a bulb at the focus sends light out in a parallel beam.
- **Solar cookers and solar power towers.**
- **Projectile motion:** thrown objects follow parabolas (ignoring air resistance).
- **Suspension bridges:** the cables carrying a uniform deck hang in a parabola.

## 3. What it means
A parabola is the set of points whose distance to the focus equals their distance to the directrix. The vertex sits halfway between them; $p$ is the distance from the vertex to the focus.

For a vertex at the origin, focus $(0, p)$ and directrix $y = -p$: setting the two distances equal and squaring gives $x^2 = 4py$.
- $p > 0$: opens up; $p < 0$: opens down. $y^2 = 4px$ opens right or left.

**Reflective property:** any ray coming in parallel to the axis bounces off the parabola straight through the focus, which is why dishes collect signals at one point.

## 4. Formula & worked example
$$
x^2 = 4py:\ \text{focus } (0, p),\ \text{directrix } y = -p \qquad (x - h)^2 = 4p(y - k):\ \text{vertex } (h, k)
$$

**Example 1.** $x^2 = 12y$: $4p = 12$ → $p = 3$; focus $(0, 3)$, directrix $y = -3$, opens up.

**Example 2.** $y^2 = -8x$: $p = -2$; focus $(-2, 0)$, directrix $x = 2$, opens left.

**Example 3.** Equation with focus $(0, -5)$ and directrix $y = 5$: $p = -5$ → $x^2 = -20y$.

**Example 4 (satellite dish).** A dish is 60 cm across and 10 cm deep. Where should the receiver go?
- Vertex at the origin: the rim point $(30, 10)$ is on $x^2 = 4py$ → $900 = 40p$ → $p = 22.5$ cm above the vertex.

**Example 5 (shifted).** $(x - 2)^2 = 8(y + 1)$: vertex $(2, -1)$, $p = 2$, focus $(2, 1)$, directrix $y = -3$.

## 5. Common mistakes
- Using $4p$ as the focal distance (it's $p$).
- Mixing up which form opens vertically ($x^2 = \ldots$) vs horizontally ($y^2 = \ldots$).
- Putting the directrix on the same side as the focus.

## 6. Practice
Q: Focus of $x^2 = 4y$?
A: $(0, 1)$.

Q: Directrix of $y^2 = 16x$?
A: $x = -4$.

Q: Parabola with vertex at the origin and focus $(3, 0)$?
A: $y^2 = 12x$.

Q: Why do satellite dishes have a parabolic shape?
A: Parallel incoming signals reflect to the focus, where the receiver is.

Q: A headlight reflector is 20 cm wide and 5 cm deep. Bulb distance from the vertex?
A: $100 = 20p$ → $p = 5$ cm.

@@ 3 | TOPIC | Ellipses | Two-focus definition, standard form, vertices, foci c² = a² − b², eccentricity, orbits | NICE
# Ellipses

## 1. What it covers
- The **definition:** the sum of the distances to two **foci** is constant ($2a$).
- Standard form $\frac{x^2}{a^2} + \frac{y^2}{b^2} = 1$ and shifted forms.
- **Vertices**, **co-vertices**, foci with $c^2 = a^2 - b^2$.
- **Eccentricity** $e = \frac{c}{a}$.

## 2. Where it is used
- **Astronomy:** planets orbit the Sun in ellipses with the Sun at one focus (Kepler's first law); satellite orbits.
- **Medicine:** lithotripsy machines shatter kidney stones by focusing shock waves from one focus of an elliptical reflector to the other.
- **Architecture:** whispering galleries (St Paul's Cathedral): a whisper at one focus is heard clearly at the other.
- **Graphics:** drawing ellipses and ovals (SVG `<ellipse>`).

## 3. What it means
**String construction:** pin two tacks (the foci), loop a string around them and a pencil, and draw while keeping the string tight. The total distance from the pencil to the two pins is always the string length: that's an ellipse.

- $a$: half the long axis (major axis); vertices at $(\pm a, 0)$ when horizontal.
- $b$: half the short axis (minor axis).
- $c$: distance from the centre to each focus, with $c^2 = a^2 - b^2$.
- **Eccentricity** $e = \frac{c}{a}$, from 0 (a circle) to nearly 1 (very stretched). Earth's orbit has $e \approx 0.017$, almost a circle.

The larger denominator tells you the direction of the major axis.

## 4. Formula & worked example
$$
\frac{(x - h)^2}{a^2} + \frac{(y - k)^2}{b^2} = 1 \ (a > b,\ \text{horizontal}) \qquad c^2 = a^2 - b^2 \qquad e = \frac{c}{a}
$$

**Example 1.** $\frac{x^2}{25} + \frac{y^2}{9} = 1$: $a = 5$, $b = 3$, $c = 4$. Vertices $(\pm 5, 0)$, co-vertices $(0, \pm 3)$, foci $(\pm 4, 0)$, $e = 0.8$.

**Example 2.** $\frac{x^2}{16} + \frac{y^2}{36} = 1$: vertical major axis, $a = 6$, $b = 4$, $c = \sqrt{20} = 2\sqrt{5}$; foci $(0, \pm 2\sqrt{5})$.

**Example 3 (write).** Centre $(0, 0)$, vertex $(7, 0)$, focus $(5, 0)$: $b^2 = 49 - 25 = 24$ → $\frac{x^2}{49} + \frac{y^2}{24} = 1$.

**Example 4 (whispering gallery).** An elliptical room is 40 m long and 30 m wide. Where are the foci?
- $a = 20$, $b = 15$ → $c = \sqrt{400 - 225} \approx 13.2$ m from the centre along the long axis.

## 5. Common mistakes
- Using $c^2 = a^2 + b^2$ (that's for hyperbolas).
- Taking $a$ from the $x^2$ term even when the $y^2$ denominator is bigger.
- Forgetting that $a$ and $b$ are square roots of the denominators.

## 6. Practice
Q: Vertices and foci of $\frac{x^2}{100} + \frac{y^2}{64} = 1$?
A: Vertices $(\pm 10, 0)$; $c = 6$, foci $(\pm 6, 0)$.

Q: Eccentricity of that ellipse?
A: $0.6$.

Q: What is the eccentricity of a circle?
A: $0$.

Q: Ellipse with vertices $(0, \pm 5)$ and co-vertices $(\pm 2, 0)$?
A: $\frac{x^2}{4} + \frac{y^2}{25} = 1$.

Q: Where is the Sun in a planet's elliptical orbit?
A: At one focus.

@@ 3 | TOPIC | Hyperbolas | Difference-of-distances definition, standard form, vertices, foci c² = a² + b², asymptotes, navigation | NICE
# Hyperbolas

## 1. What it covers
- The **definition:** the **difference** of distances to two foci is constant ($2a$).
- Standard forms $\frac{x^2}{a^2} - \frac{y^2}{b^2} = 1$ (opens left/right) and $\frac{y^2}{a^2} - \frac{x^2}{b^2} = 1$ (opens up/down).
- Vertices, foci ($c^2 = a^2 + b^2$) and **asymptotes** $y = \pm\frac{b}{a}x$.

## 2. Where it is used
- **Navigation:** LORAN and some GPS-like systems locate a ship from the **difference** in arrival times of signals from two stations, which puts it on a hyperbola. Two pairs of stations give two hyperbolas; the ship is where they cross. Mobile networks locate phones the same way (TDOA).
- **Astronomy:** comets and spacecraft moving fast enough to escape follow hyperbolic paths (slingshot manoeuvres).
- **Architecture:** cooling towers of power plants are hyperboloids (strong and use less material).
- **Physics:** $xy = k$ (a rotated hyperbola) describes Boyle's law ($PV$ = constant).

## 3. What it means
For an ellipse, the **sum** of the distances to the foci is constant; for a hyperbola, the **difference** is constant. This gives two separate branches, opening away from each other.

- $a$: centre to vertex. $c$: centre to focus, now with $c^2 = a^2 + b^2$ (foci are **outside** the vertices).
- **Asymptotes:** far from the centre, the branches approach the lines $y = \pm\frac{b}{a}x$. To sketch, draw the box $2a$ by $2b$ around the centre; its diagonals are the asymptotes.
- The **positive** term tells you the direction it opens.

## 4. Formula & worked example
$$
\frac{x^2}{a^2} - \frac{y^2}{b^2} = 1:\ \text{vertices } (\pm a, 0),\ \text{foci } (\pm c, 0),\ c^2 = a^2 + b^2,\ \text{asymptotes } y = \pm\frac{b}{a}x
$$

**Example 1.** $\frac{x^2}{9} - \frac{y^2}{16} = 1$: $a = 3$, $b = 4$, $c = 5$. Vertices $(\pm 3, 0)$, foci $(\pm 5, 0)$, asymptotes $y = \pm\frac{4}{3}x$.

**Example 2.** $\frac{y^2}{4} - \frac{x^2}{12} = 1$: opens up/down; $a = 2$, $b = 2\sqrt{3}$, $c = 4$; vertices $(0, \pm 2)$, foci $(0, \pm 4)$, asymptotes $y = \pm\frac{2}{2\sqrt{3}}x = \pm\frac{1}{\sqrt{3}}x$.

**Example 3 (write).** Vertices $(\pm 4, 0)$, foci $(\pm 5, 0)$: $b^2 = 25 - 16 = 9$ → $\frac{x^2}{16} - \frac{y^2}{9} = 1$.

**Example 4 (navigation idea).** Two stations 100 km apart send signals at the same moment; a ship receives one signal earlier, and the difference in distance works out as 60 km. The ship lies on a hyperbola with foci at the stations and $2a = 60$ → $a = 30$, $c = 50$, $b = 40$.

## 5. Common mistakes
- Using $c^2 = a^2 - b^2$ (that's the ellipse).
- Getting the opening direction wrong (follow the positive term).
- Swapping $a$ and $b$ in the asymptote slope (for horizontal hyperbolas it's $\frac{b}{a}$).

## 6. Practice
Q: Foci of $\frac{x^2}{16} - \frac{y^2}{9} = 1$?
A: $c = 5$ → $(\pm 5, 0)$.

Q: Asymptotes of $\frac{x^2}{25} - \frac{y^2}{4} = 1$?
A: $y = \pm\frac{2}{5}x$.

Q: Which way does $\frac{y^2}{9} - x^2 = 1$ open?
A: Up and down.

Q: What is constant for every point on a hyperbola?
A: The difference of its distances to the two foci.

Q: Relationship between $a$, $b$, $c$ for a hyperbola?
A: $c^2 = a^2 + b^2$.

@@ 3 | TOPIC | Identifying conics and applications | Recognising a conic from its general equation, completing the square, real-world uses | NICE
# Identifying conics and applications

## 1. What it covers
- Recognising circles, ellipses, parabolas and hyperbolas from $Ax^2 + Cy^2 + Dx + Ey + F = 0$.
- Completing the square to reach standard form.
- Choosing the right conic for a real situation.

## 2. Where it is used
- **Physics and engineering:** recognising orbit types from energy (circle/ellipse = bound, parabola = just escaping, hyperbola = escaping).
- **Computer vision:** fitting ellipses to detected edges (e.g. pupils in eye tracking, coins).
- **Architecture and design:** arches, domes, reflectors.

## 3. What it means
For $Ax^2 + Cy^2 + Dx + Ey + F = 0$ (no $xy$ term), look at $A$ and $C$:
| Condition | Conic |
|---|---|
| $A = C$ | Circle |
| $A$ and $C$ same sign, $A \ne C$ | Ellipse |
| One of $A$, $C$ is 0 | Parabola |
| $A$ and $C$ opposite signs | Hyperbola |

Then complete the square in $x$ and $y$ to find the centre (or vertex) and the other features. (Degenerate cases like a single point or two lines can occur; you'll notice them when the right side becomes 0 or negative.)

## 4. Formula & worked example
**Example 1.** $4x^2 + 9y^2 - 16x + 18y - 11 = 0$
- Same sign, different → ellipse.
- $4(x^2 - 4x + 4) + 9(y^2 + 2y + 1) = 11 + 16 + 9 = 36$ → $\frac{(x - 2)^2}{9} + \frac{(y + 1)^2}{4} = 1$
- Centre $(2, -1)$, $a = 3$ (horizontal), $b = 2$.

**Example 2.** $y^2 - 4y - 8x + 20 = 0$
- No $x^2$ term → parabola. $(y - 2)^2 = 8x - 16 = 8(x - 2)$ → vertex $(2, 2)$, opens right, $p = 2$.

**Example 3.** $x^2 - y^2 + 6x + 4y - 3 = 0$
- Opposite signs → hyperbola. $(x + 3)^2 - (y - 2)^2 = 3 + 9 - 4 = 8$ → $\frac{(x + 3)^2}{8} - \frac{(y - 2)^2}{8} = 1$; centre $(-3, 2)$.

**Example 4.** $2x^2 + 2y^2 - 8x + 12y + 6 = 0$ → circle: $(x - 2)^2 + (y + 3)^2 = 10$.

**Example 5 (choose the conic).** A comet passes the Sun once and never returns → hyperbola (or parabola). Earth's orbit → ellipse. A fountain's water jet → parabola.

## 5. Common mistakes
- Forgetting to factor out the coefficient of $x^2$ or $y^2$ before completing the square.
- Not adding the right amounts to the other side (multiply by the factored coefficient).
- Misclassifying when $A$ and $C$ are equal but negative (still a circle, if anything).

## 6. Practice
Q: Classify $3x^2 + 3y^2 - 12 = 0$.
A: Circle ($x^2 + y^2 = 4$).

Q: Classify $x^2 + 4y^2 - 4 = 0$.
A: Ellipse.

Q: Classify $y = 2x^2 - 3x + 1$.
A: Parabola.

Q: Classify $9x^2 - 4y^2 = 36$.
A: Hyperbola.

Q: Which conic describes a bound planetary orbit?
A: An ellipse.

@@ 3 | TOPIC | Practice & review: Conic sections | Mixed problem set with worked answers | NICE
# Practice & review: Conic sections

**How to use this:** 40 minutes, no calculator. Sketch each conic.

## Problems
1. Find the focus and directrix of $x^2 = -6y$.
2. Write the parabola with vertex $(1, 3)$ and focus $(1, 5)$.
3. Find the vertices, foci and eccentricity of $\frac{x^2}{169} + \frac{y^2}{144} = 1$.
4. Write the ellipse with foci $(0, \pm 3)$ and vertices $(0, \pm 5)$.
5. Find the vertices, foci and asymptotes of $\frac{y^2}{36} - \frac{x^2}{64} = 1$.
6. Classify and write in standard form: $x^2 + 4y^2 + 6x - 8y + 9 = 0$.
7. A searchlight reflector is 40 cm across and 10 cm deep. Where should the bulb go?

## Answers
1. $4p = -6$ → $p = -1.5$; focus $(0, -1.5)$, directrix $y = 1.5$.
2. $p = 2$: $(x - 1)^2 = 8(y - 3)$.
3. $a = 13$, $b = 12$, $c = 5$; vertices $(\pm 13, 0)$, foci $(\pm 5, 0)$, $e = \frac{5}{13}$.
4. $a = 5$, $c = 3$, $b = 4$: $\frac{x^2}{16} + \frac{y^2}{25} = 1$.
5. $a = 6$, $b = 8$, $c = 10$; vertices $(0, \pm 6)$, foci $(0, \pm 10)$, asymptotes $y = \pm\frac{3}{4}x$.
6. Ellipse: $(x + 3)^2 + 4(y - 1)^2 = -9 + 9 + 4 = 4$ → $\frac{(x + 3)^2}{4} + (y - 1)^2 = 1$.
7. $(20, 10)$ on $x^2 = 4py$: $400 = 40p$ → $p = 10$ cm from the vertex.

## Flashcards
Q: Focus definition of a parabola?
A: Points equidistant from the focus and the directrix.

Q: Ellipse: $c^2 = ?$
A: $a^2 - b^2$.

Q: Hyperbola: $c^2 = ?$
A: $a^2 + b^2$.

Q: Asymptotes of $\frac{x^2}{a^2} - \frac{y^2}{b^2} = 1$?
A: $y = \pm\frac{b}{a}x$.

@@ 2 | MODULE | Final review | Cumulative mixed set covering the whole roadmap | MUST
Mix everything. After this, continue with Calculus or Linear Algebra.

@@ 3 | TOPIC | Final review: Geometry | Cumulative mixed problems from all phases with answers | MUST
# Final review: Geometry

**How to use this:** 60 minutes; calculator allowed. Re-study the topic for every miss.

## Problems
1. Parallel lines are cut by a transversal; alternate interior angles are $(7x - 12)^\circ$ and $(4x + 18)^\circ$. Find the angle.
2. Each interior angle of a regular polygon is $162^\circ$. How many sides?
3. A trapezoid has bases 9 and 15 and legs 5 and 5 (isosceles). Find its height and area.
4. A cone has volume $96\pi$ cm³ and radius 6 cm. Find its height and slant height.
5. Rotate $(4, -3)$ by $270^\circ$ about the origin, then reflect in $y = x$.
6. $\triangle ABC \sim \triangle DEF$ with $AB = 8$, $DE = 12$, and the area of $\triangle ABC$ is 32. Area of $\triangle DEF$?
7. Show that $(1, 2)$, $(4, 6)$, $(8, 3)$ form a right isosceles triangle.
8. Write $x^2 + y^2 + 8x - 6y = 0$ in standard form; give the centre and radius.
9. Chord $AB$ subtends a $96^\circ$ central angle. Find the inscribed angle on the major arc and on the minor arc.
10. Identify $25x^2 - 4y^2 = 100$ and give its asymptotes.

## Answers
1. $7x - 12 = 4x + 18$ → $x = 10$ → $58^\circ$.
2. Exterior $18^\circ$ → $20$ sides.
3. Half the difference of bases $= 3$ → $h = \sqrt{25 - 9} = 4$; area $= \frac{24}{2} \times 4 = 48$.
4. $\frac{1}{3}\pi(36)h = 96\pi$ → $h = 8$; $\ell = 10$.
5. $(4, -3) \to (-3, -4)$ → reflect: $(-4, -3)$.
6. $k = 1.5$ → $32 \times 2.25 = 72$.
7. Sides: $\sqrt{9 + 16} = 5$, $\sqrt{16 + 9} = 5$, $\sqrt{49 + 1} = \sqrt{50}$; $25 + 25 = 50$ → right angle at $(4, 6)$, two equal sides → right isosceles.
8. $(x + 4)^2 + (y - 3)^2 = 25$; centre $(-4, 3)$, $r = 5$.
9. On the major arc: $48^\circ$; on the minor arc: $180 - 48 = 132^\circ$ (cyclic quadrilateral).
10. $\frac{x^2}{4} - \frac{y^2}{25} = 1$: hyperbola opening left/right; asymptotes $y = \pm\frac{5}{2}x$.
