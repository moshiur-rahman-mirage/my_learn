@@ 1 | MODULE | Phase 1 — Lines, Angles, Triangles and Polygons (Foundation) | Basic terms, angle relationships, parallel lines, triangle facts, polygon angles, quadrilaterals, constructions | MUST
# Phase 1: Lines, angles, triangles and polygons

Geometry is the maths of **shape, size and position**. It's behind architecture, engineering drawings, maps, computer graphics, game engines, robotics and CAD software. This roadmap goes from basic angle facts to proofs, coordinate geometry, circles and conic sections.

**Related roadmaps:** Pythagoras, right-triangle trigonometry, the unit circle, arc length and sector area are in the **Trigonometry** roadmap; scale drawings and how area and volume scale are in **Numbers & Arithmetic** (Phase 3).

Each topic has six parts: what it covers, where it is used, what it means, formulas with worked examples, common mistakes, and practice flashcards.

@@ 2 | MODULE | Lines and angles | Basic terms, angle pairs, parallel lines and transversals | MUST
Every geometry fact is built on a handful of angle rules. Learn them well and proofs become easy.

@@ 3 | TOPIC | Points, lines, rays, segments and planes | Undefined terms, notation, collinear and coplanar points, intersections | MUST
# Points, lines, rays, segments and planes

## 1. What it covers
- The basic objects: **point**, **line**, **plane** (the undefined terms).
- **Segments** and **rays**, and how to name them.
- **Collinear** and **coplanar** points; how lines and planes intersect.

## 2. Where it is used
- **Computer graphics and games:** everything is built from points (vertices), segments (edges) and planes (faces); a ray is cast from the camera for each pixel in ray tracing.
- **CAD and architecture:** drawings use exact notation for segments and lines.
- **GIS and maps:** roads are segments, locations are points.
- **Physics:** light travels in rays.

## 3. What it means
- A **point** marks a location; it has no size. Named with a capital letter: $A$.
- A **line** is perfectly straight, has no thickness and goes on **forever** in both directions: $\overleftrightarrow{AB}$.
- A **segment** is the part of a line between two endpoints: $\overline{AB}$. Its length is written $AB$.
- A **ray** starts at one point and goes forever in one direction: $\overrightarrow{AB}$ starts at $A$ and passes through $B$. Like a torch beam.
- A **plane** is a flat surface extending forever (like an endless sheet of paper).

**Collinear** points lie on one line; **coplanar** points lie on one plane. Two different lines meet in at most one point; two planes meet in a line (like two walls meeting at a corner edge).

## 4. Formula & worked example
**Example 1.** Name all segments in the line $A$–$B$–$C$ (in that order).
- $\overline{AB}$, $\overline{BC}$, $\overline{AC}$

**Example 2.** Are $\overrightarrow{AB}$ and $\overrightarrow{BA}$ the same ray?
- No. One starts at $A$ going through $B$; the other starts at $B$ going through $A$.

**Example 3 (segment addition).** $B$ lies on $\overline{AC}$, $AB = 2x + 1$, $BC = x + 5$, $AC = 21$. Find $x$ and $AB$.
- $2x + 1 + x + 5 = 21$ → $3x = 15$ → $x = 5$, $AB = 11$

**Example 4 (midpoint).** $M$ is the midpoint of $\overline{PQ}$, $PM = 3x - 2$, $MQ = x + 8$.
- $3x - 2 = x + 8$ → $x = 5$; $PQ = 2 \times 13 = 26$

**Example 5.** How many points determine a line? A plane?
- Two points determine a line; three non-collinear points determine a plane (that's why a three-legged stool never wobbles).

## 5. Common mistakes
- Treating a ray's name as reversible.
- Confusing $\overline{AB}$ (the segment) with $AB$ (its length).
- Thinking a line has endpoints.

## 6. Practice
Q: What is the difference between a segment and a ray?
A: A segment has two endpoints; a ray has one endpoint and goes on forever in one direction.

Q: $AB = 7$, $BC = 12$, and $B$ is between $A$ and $C$. Find $AC$.
A: $19$.

Q: Where do two distinct planes intersect (if they do)?
A: In a line.

Q: Why does a three-legged stool never wobble?
A: Any three non-collinear points lie on exactly one plane.

Q: $M$ is the midpoint of $\overline{AB}$ and $AB = 18$. Find $AM$.
A: $9$.

@@ 3 | TOPIC | Angle types and angle pairs | Acute, right, obtuse, straight, reflex; complementary, supplementary, vertical and adjacent angles | MUST
# Angle types and angle pairs

## 1. What it covers
- Measuring angles in degrees; types: **acute, right, obtuse, straight, reflex**.
- **Complementary** (sum $90^\circ$) and **supplementary** (sum $180^\circ$) angles.
- **Vertical angles** (equal) and **linear pairs** (sum $180^\circ$).
- Angle bisectors; angles around a point ($360^\circ$).

## 2. Where it is used
- **Construction and carpentry:** cutting mitre joints ($45^\circ$ each to make a $90^\circ$ corner).
- **Navigation:** headings and turns.
- **Graphics and game dev:** rotation angles, field of view, joint angles in animation rigs.
- **Proofs:** vertical and supplementary angle facts are used in almost every geometry proof.

## 3. What it means
An angle is formed by two rays sharing an endpoint (the **vertex**). It measures the amount of turn.
- **Acute** $< 90^\circ$, **right** $= 90^\circ$, **obtuse** between $90^\circ$ and $180^\circ$, **straight** $= 180^\circ$, **reflex** between $180^\circ$ and $360^\circ$.

**Key facts:**
- Angles on a **straight line** add to $180^\circ$ (a linear pair).
- Angles **around a point** add to $360^\circ$.
- When two lines cross, the **opposite (vertical) angles are equal**. Why: each is supplementary to the same neighbouring angle.

```text
        \  2  /
       1 \   / 3       ∠1 = ∠3 (vertical)
          \ /          ∠2 = ∠4 (vertical)
          / \          ∠1 + ∠2 = 180° (linear pair)
       4 /   \
```

## 4. Formula & worked example
$$
\text{complementary: } a + b = 90^\circ \qquad \text{supplementary: } a + b = 180^\circ \qquad \text{around a point: } 360^\circ
$$

**Example 1.** The complement of $37^\circ$ is $53^\circ$; its supplement is $143^\circ$.

**Example 2.** Two lines cross; one angle is $68^\circ$. Find the other three.
- Vertical angle $68^\circ$; the two neighbours $112^\circ$ each.

**Example 3 (algebra).** Vertical angles measure $3x + 10$ and $5x - 30$.
- $3x + 10 = 5x - 30$ → $x = 20$ → each angle $70^\circ$

**Example 4.** An angle is 4 times its supplement. Find it.
- $x = 4(180 - x)$ → $5x = 720$ → $x = 144^\circ$

**Example 5.** Three angles around a point are $x$, $2x$ and $150^\circ$.
- $3x + 150 = 360$ → $x = 70^\circ$

## 5. Common mistakes
- Mixing up complementary ($90^\circ$) and supplementary ($180^\circ$). Memory aid: **C** comes before **S**, and 90 comes before 180.
- Assuming angles that look equal are equal (diagrams are not always to scale).
- Treating adjacent angles as vertical angles.

## 6. Practice
Q: Supplement of $47^\circ$?
A: $133^\circ$.

Q: Two complementary angles are $2x$ and $3x$. Find them.
A: $x = 18$: $36^\circ$ and $54^\circ$.

Q: Why are vertical angles equal?
A: Each forms a linear pair with the same angle, so both equal $180^\circ$ minus it.

Q: Type of angle measuring $210^\circ$?
A: Reflex.

Q: A linear pair has angles $x + 40$ and $2x - 10$. Find $x$.
A: $3x + 30 = 180$ → $x = 50$.

@@ 3 | TOPIC | Parallel lines and transversals | Corresponding, alternate interior, alternate exterior and co-interior angles; proving lines parallel | MUST
# Parallel lines and transversals

## 1. What it covers
- The angle pairs formed when a **transversal** crosses two lines.
- When the lines are **parallel**: corresponding and alternate angles are equal; co-interior angles add to $180^\circ$.
- The **converses**: using angles to prove lines are parallel.

## 2. Where it is used
- **Architecture and engineering:** checking that beams, rails and floors are parallel.
- **Road and railway design:** crossings and intersections.
- **Proofs:** the triangle angle sum and many polygon facts come from parallel-line angles.
- **Optics:** light through a glass slab exits parallel to how it entered.

## 3. What it means
A **transversal** is a line crossing two other lines, creating 8 angles. If the two lines are **parallel**, the transversal crosses each at the same tilt, so the angle patterns at the two crossings are copies of each other.

- **Corresponding angles** (same position at each crossing, an "F" shape) are **equal**.
- **Alternate interior angles** (between the lines, on opposite sides, a "Z" shape) are **equal**.
- **Alternate exterior angles** (outside, opposite sides) are **equal**.
- **Co-interior (same-side interior) angles** (between the lines, same side, a "C" or "U" shape) **add to $180^\circ$**.

**Converses:** if corresponding (or alternate) angles are equal, or co-interior angles add to $180^\circ$, then the lines **are** parallel.

## 4. Formula & worked example
$$
\ell \parallel m \Rightarrow \text{corresponding} =, \ \text{alternate} =, \ \text{co-interior sum} = 180^\circ
$$

**Example 1.** Parallel lines; one angle at the first crossing is $65^\circ$. Its corresponding angle is $65^\circ$, its alternate interior angle is $65^\circ$, and its co-interior angle is $115^\circ$.

**Example 2 (algebra).** Alternate interior angles: $4x - 15$ and $2x + 25$.
- $4x - 15 = 2x + 25$ → $x = 20$ → $65^\circ$

**Example 3.** Co-interior angles: $3y + 12$ and $2y + 18$.
- $5y + 30 = 180$ → $y = 30$ → $102^\circ$ and $78^\circ$

**Example 4 (prove parallel).** Corresponding angles measure $72^\circ$ and $72^\circ$. Are the lines parallel? Yes (converse of corresponding angles).

**Example 5 (zig-zag).** Two parallel lines with a point between them; segments from the point make angles $30^\circ$ and $45^\circ$ with the lines. The angle at the point is $30^\circ + 45^\circ = 75^\circ$ (draw a third parallel line through the point and use alternate angles twice).

## 5. Common mistakes
- Using these equalities when the lines are **not** parallel.
- Setting co-interior angles equal instead of supplementary.
- Mislabelling which angles are "interior" (between the two lines).

## 6. Practice
Q: Parallel lines: corresponding angles are $5x$ and $3x + 40$. Find $x$.
A: $x = 20$.

Q: Co-interior angles are $110^\circ$ and $?$
A: $70^\circ$.

Q: Alternate interior angles measure $80^\circ$ and $82^\circ$. Are the lines parallel?
A: No.

Q: Which angle pair forms a "Z" shape?
A: Alternate interior angles.

Q: Parallel lines cut by a transversal; one angle is $128^\circ$. Smallest angle formed?
A: $52^\circ$.

@@ 2 | MODULE | Triangles, polygons and quadrilaterals | Triangle angle facts and inequality, polygon angle sums, quadrilateral properties, constructions | MUST
Triangles are the strongest and simplest polygon; every polygon can be split into triangles.

@@ 3 | TOPIC | Triangle angle sum, exterior angles and the triangle inequality | 180° sum and its proof, exterior angle theorem, which three lengths form a triangle | MUST
# Triangle angle sum, exterior angles and the triangle inequality

## 1. What it covers
- The **angle sum** of a triangle is $180^\circ$ (with proof).
- The **exterior angle theorem**: an exterior angle equals the sum of the two remote interior angles.
- Classifying triangles by sides and angles.
- The **triangle inequality**: which three lengths can form a triangle.
- The largest side is opposite the largest angle.

## 2. Where it is used
- **Engineering and construction:** triangles make rigid frames (trusses, bridges, roof frames, bicycle frames).
- **Computer graphics:** every 3D model is a mesh of triangles; GPUs draw billions of them per second.
- **Navigation and surveying:** triangulation.
- **Algorithms:** the triangle inequality ($d(a, c) \le d(a, b) + d(b, c)$) is a key property of distance used in nearest-neighbour search and routing.

## 3. What it means
**Why $180^\circ$:** draw a line through one vertex parallel to the opposite side. The three angles at that vertex form a straight line ($180^\circ$), and by alternate angles they are exactly the three angles of the triangle.

**Exterior angle:** extend one side. The outside angle plus its neighbouring interior angle make $180^\circ$, and so do all three interior angles. So the exterior angle equals the other two interior angles added together.

**Triangle inequality:** any side must be **shorter than the sum of the other two**. Otherwise the two short sides can't reach each other. (Going straight is always shortest, so a detour through a third point can't be shorter.)

**Classifying:**
- By sides: **equilateral** (3 equal), **isosceles** (2 equal), **scalene** (none equal).
- By angles: **acute** (all $< 90^\circ$), **right** (one $90^\circ$), **obtuse** (one $> 90^\circ$).

## 4. Formula & worked example
$$
A + B + C = 180^\circ \qquad \text{exterior angle} = \text{sum of remote interior angles} \qquad a < b + c
$$

**Example 1.** Two angles are $47^\circ$ and $78^\circ$. Third angle? → $55^\circ$.

**Example 2.** Angles $x$, $2x$ and $3x$. → $6x = 180$ → $30^\circ, 60^\circ, 90^\circ$ (a right triangle).

**Example 3 (exterior).** Remote interior angles $52^\circ$ and $64^\circ$. Exterior angle? → $116^\circ$.

**Example 4.** Can 4, 7 and 12 form a triangle? $4 + 7 = 11 < 12$ → **no**. Can 5, 8, 12? $5 + 8 = 13 > 12$ → yes.

**Example 5 (range).** Two sides are 6 and 10. Range for the third side $x$? → $4 < x < 16$.

**Example 6.** In $\triangle ABC$, $A = 40^\circ$, $B = 75^\circ$. Which side is longest? $C = 65^\circ$, so the largest angle is $B$ → side $b$ (opposite $B$) is longest.

## 5. Common mistakes
- Adding the adjacent interior angle in the exterior angle theorem (use the two **remote** ones).
- Checking the triangle inequality with only one pair. Test the two smaller sides against the largest.
- Allowing "equal to" in the triangle inequality ($3 + 4 = 7$ gives a flat line, not a triangle).

## 6. Practice
Q: Angles of a triangle are $x + 10$, $2x$, $3x - 40$. Find $x$.
A: $6x - 30 = 180$ → $x = 35$.

Q: Exterior angle $130^\circ$, one remote interior angle $70^\circ$. Other remote angle?
A: $60^\circ$.

Q: Can 3, 3, 7 form a triangle?
A: No ($3 + 3 < 7$).

Q: Two sides are 9 and 4. Possible third sides?
A: Between 5 and 13 (exclusive).

Q: Why are triangles used in bridges and roofs?
A: A triangle is rigid: its shape can't change without changing a side length.

@@ 3 | TOPIC | Polygons: interior and exterior angle sums | (n − 2)·180°, regular polygons, exterior angles sum to 360°, tessellations | MUST
# Polygons: interior and exterior angle sums

## 1. What it covers
- Naming polygons; **regular** polygons (all sides and angles equal).
- The interior angle sum $(n - 2) \times 180^\circ$.
- Each interior angle of a regular polygon.
- The exterior angle sum is always $360^\circ$.
- Which regular polygons **tessellate** (tile a floor).

## 2. Where it is used
- **Tiling and design:** floor tiles, honeycombs (hexagons), Islamic geometric art.
- **Game development:** hex grids for strategy games; polygon meshes.
- **Engineering:** nuts and bolts are hexagonal; stop signs are octagons.
- **Robotics and drones:** a robot driving around a polygon turns a total of $360^\circ$.

## 3. What it means
**Interior sum:** from one vertex of an $n$-sided polygon, draw diagonals to all others. They split it into $n - 2$ triangles, each with $180^\circ$. So the interior angles add to $(n - 2) \times 180^\circ$.

**Exterior sum:** walk around any convex polygon. At each corner you turn by the exterior angle. After going all the way round you face the original direction, having turned exactly one full circle: $360^\circ$, whatever the number of sides.

**Regular polygon:** divide by $n$ to get each angle.

**Tessellation:** regular polygons tile the plane only if their interior angle divides $360^\circ$: triangles ($60^\circ$), squares ($90^\circ$), hexagons ($120^\circ$). Bees use hexagons: they tile with no gaps and use the least wax for the area enclosed.

## 4. Formula & worked example
$$
\text{interior sum} = (n - 2) \times 180^\circ \qquad \text{each interior (regular)} = \frac{(n - 2) \times 180^\circ}{n} \qquad \text{each exterior (regular)} = \frac{360^\circ}{n}
$$

**Example 1.** Interior angle sum of a hexagon: $4 \times 180^\circ = 720^\circ$.

**Example 2.** Each interior angle of a regular octagon: $\frac{6 \times 180^\circ}{8} = 135^\circ$.

**Example 3.** A regular polygon has exterior angles of $24^\circ$. How many sides? → $\frac{360}{24} = 15$.

**Example 4.** A regular polygon has interior angles of $156^\circ$. Sides?
- Exterior $= 24^\circ$ → $n = 15$

**Example 5.** A pentagon's angles are $100^\circ$, $110^\circ$, $95^\circ$, $120^\circ$ and $x$. Find $x$.
- Sum $= 540^\circ$ → $x = 115^\circ$

**Example 6.** Can regular pentagons tile a floor? $108^\circ$ doesn't divide $360^\circ$ → no.

## 5. Common mistakes
- Using $n \times 180^\circ$ instead of $(n - 2) \times 180^\circ$.
- Thinking the exterior angle sum grows with $n$. It's always $360^\circ$.
- Applying "each angle" formulas to irregular polygons.

## 6. Practice
Q: Interior angle sum of a decagon (10 sides)?
A: $1440^\circ$.

Q: Each interior angle of a regular hexagon?
A: $120^\circ$.

Q: Each exterior angle of a regular 12-gon?
A: $30^\circ$.

Q: A regular polygon has interior angles of $140^\circ$. How many sides?
A: Exterior $40^\circ$ → $9$.

Q: Which regular polygons tessellate?
A: Equilateral triangles, squares and regular hexagons.

@@ 3 | TOPIC | Quadrilaterals and their properties | Parallelograms, rectangles, rhombi, squares, trapezoids, kites; diagonal properties | MUST
# Quadrilaterals and their properties

## 1. What it covers
- The family of quadrilaterals: **parallelogram, rectangle, rhombus, square, trapezoid (trapezium), kite**.
- Their side, angle and **diagonal** properties.
- How to prove which type a quadrilateral is.

## 2. Where it is used
- **Architecture and furniture:** rectangles everywhere; checking a frame is rectangular by measuring its diagonals (equal diagonals → rectangle).
- **Mechanisms:** parallelogram linkages keep things level (desk lamps, car jacks, scissor lifts).
- **UI design:** most layout boxes are rectangles.
- **Kites and bridges:** kite and trapezoid shapes in trusses.

## 3. What it means
A quadrilateral has 4 sides; its angles add to $360^\circ$. The special types form a family tree, where each child has all its parent's properties plus more:
- **Parallelogram:** both pairs of opposite sides parallel. → Opposite sides equal, opposite angles equal, consecutive angles add to $180^\circ$, **diagonals bisect each other**.
- **Rectangle:** a parallelogram with 4 right angles. → Plus **diagonals are equal**.
- **Rhombus:** a parallelogram with 4 equal sides. → Plus **diagonals are perpendicular** and bisect the angles.
- **Square:** both a rectangle and a rhombus: all properties.
- **Trapezoid:** exactly one pair of parallel sides (the bases). An **isosceles** trapezoid has equal legs and equal base angles.
- **Kite:** two pairs of equal adjacent sides; diagonals perpendicular.

**Builder's trick:** to check a frame is rectangular, measure both diagonals. Equal diagonals (plus opposite sides equal) means it's a rectangle.

## 4. Formula & worked example
**Example 1.** In parallelogram $ABCD$, $\angle A = 70^\circ$. Find the other angles.
- $\angle C = 70^\circ$; $\angle B = \angle D = 110^\circ$

**Example 2.** In a rectangle, one diagonal is $3x + 4$ and the other $5x - 6$.
- $3x + 4 = 5x - 6$ → $x = 5$ → diagonals 19

**Example 3.** A rhombus has diagonals 12 and 16. Side length?
- Diagonals bisect each other at right angles: halves 6 and 8 → side $= \sqrt{36 + 64} = 10$

**Example 4.** In an isosceles trapezoid one base angle is $65^\circ$. Find all angles.
- $65^\circ, 65^\circ, 115^\circ, 115^\circ$

**Example 5.** True or false: every square is a rhombus; every rhombus is a square.
- True; false.

## 5. Common mistakes
- Assuming a parallelogram's diagonals are equal (only in rectangles).
- Assuming a rhombus has right angles.
- Treating the family tree backwards (a rectangle is a parallelogram, not the other way round).

## 6. Practice
Q: Which quadrilaterals have perpendicular diagonals?
A: Rhombus, square and kite.

Q: Consecutive angles of a parallelogram are $2x$ and $3x$. Find them.
A: $72^\circ$ and $108^\circ$.

Q: Is every rectangle a parallelogram?
A: Yes.

Q: A rhombus has diagonals 10 and 24. Side length?
A: $13$.

Q: How do builders check a frame is rectangular?
A: Measure both diagonals; they must be equal.

@@ 3 | TOPIC | Compass and straightedge constructions | Copying segments and angles, bisectors, perpendiculars, parallels, equilateral triangles | SHOULD
# Compass and straightedge constructions

## 1. What it covers
- Copying a segment and an angle.
- Constructing the **perpendicular bisector** of a segment and the **angle bisector**.
- Constructing a perpendicular through a point and a parallel line.
- Constructing an equilateral triangle and a regular hexagon.

## 2. Where it is used
- **Technical drawing and drafting:** these are the basic operations of engineering drawing.
- **CAD software:** "perpendicular", "bisect" and "parallel" snaps implement these constructions.
- **Computational geometry:** the perpendicular bisector defines **Voronoi diagrams** (used for nearest-facility maps, cell-tower coverage and game AI).
- **Understanding proofs:** each construction is justified by congruent triangles.

## 3. What it means
With only a compass (to draw circles and copy distances) and an unmarked straightedge (to draw lines), you can build exact shapes without measuring.

**Perpendicular bisector of $\overline{AB}$:** with the compass more than half of $AB$, draw arcs from $A$ and from $B$ on both sides. The line through the two crossing points is perpendicular to $\overline{AB}$ and passes through its midpoint. Every point on it is **equally far from $A$ and $B$**.

**Angle bisector:** from the vertex, draw an arc cutting both rays; from those two points, draw equal arcs that cross; join the vertex to the crossing. Every point on it is equally far from the two sides.

**Equilateral triangle:** from each end of a segment, draw a circle with radius equal to the segment; join the ends to a crossing point.

**Regular hexagon:** step the radius around a circle 6 times (the side of a regular hexagon equals its circumradius).

## 4. Formula & worked example
**Example 1.** Why does the perpendicular bisector construction work?
- The two crossing points $P$ and $Q$ are each equally far from $A$ and $B$ (same compass width). Triangles $APQ$ and $BPQ$ are congruent (SSS), so $PQ$ splits $AB$ into equal halves at right angles.

**Example 2.** Construct a $60^\circ$ angle: build an equilateral triangle and use one corner. Bisect it for $30^\circ$; bisect again for $15^\circ$.

**Example 3.** Construct a $90^\circ$ angle at point $P$ on a line: mark two points equally far from $P$ on the line and draw their perpendicular bisector. Bisect it for $45^\circ$.

**Example 4 (Voronoi idea).** Three hospitals $A$, $B$, $C$. The perpendicular bisectors of $AB$, $BC$, $CA$ split the map into regions closest to each hospital. (They meet at one point, the **circumcentre**.)

## 5. Common mistakes
- Changing the compass width between paired arcs.
- Using a compass width less than half the segment (the arcs don't meet).
- Measuring with a ruler; constructions must be exact.

## 6. Practice
Q: What property do points on a perpendicular bisector have?
A: They are equidistant from the segment's endpoints.

Q: How do you construct a $30^\circ$ angle?
A: Construct $60^\circ$ (equilateral triangle) and bisect it.

Q: Which triangle congruence rule justifies the angle bisector construction?
A: SSS.

Q: Where do the perpendicular bisectors of a triangle's sides meet?
A: At the circumcentre (centre of the circle through all three vertices).

Q: Why can you step the radius 6 times around a circle?
A: A regular hexagon's side equals the circle's radius (it's 6 equilateral triangles).

@@ 3 | TOPIC | Practice & review: Lines, angles and polygons | Mixed problem set with worked answers | MUST
# Practice & review: Lines, angles and polygons

**How to use this:** 40 minutes, no calculator. Draw a diagram for every problem.

## Problems
1. $B$ is between $A$ and $C$; $AB = 3x - 1$, $BC = 2x + 4$, $AC = 28$. Find $AB$.
2. An angle is $30^\circ$ more than twice its complement. Find it.
3. Two parallel lines are cut by a transversal; co-interior angles are $(4x + 6)^\circ$ and $(2x + 18)^\circ$. Find both.
4. A triangle's angles are in the ratio $2 : 3 : 7$. Find them and classify the triangle.
5. Two sides of a triangle are 8 and 15. What range is possible for the third side?
6. The exterior angle at $C$ is $125^\circ$ and $\angle A = 48^\circ$. Find $\angle B$.
7. Interior angle sum of a 14-gon? Each angle if regular?
8. A regular polygon's interior angle is 5 times its exterior angle. How many sides?
9. A rhombus has side 13 and one diagonal 10. Find the other diagonal.
10. In parallelogram $PQRS$, $\angle P = (3y + 15)^\circ$ and $\angle Q = (5y - 3)^\circ$. Find all angles.

## Answers
1. $5x + 3 = 28$ → $x = 5$ → $AB = 14$.
2. $x = 2(90 - x) + 30$ → $3x = 210$ → $70^\circ$.
3. $6x + 24 = 180$ → $x = 26$ → $110^\circ$ and $70^\circ$.
4. $12$ parts $= 180$ → $30^\circ, 45^\circ, 105^\circ$; obtuse scalene.
5. $7 < x < 23$.
6. $125 - 48 = 77^\circ$.
7. $12 \times 180 = 2160^\circ$; $\frac{2160}{14} \approx 154.3^\circ$.
8. $e + 5e = 180$ → $e = 30^\circ$ → $12$ sides.
9. Half-diagonals: 5 and $\sqrt{169 - 25} = 12$ → other diagonal $24$.
10. Consecutive angles: $8y + 12 = 180$ → $y = 21$ → $\angle P = \angle R = 78^\circ$, $\angle Q = \angle S = 102^\circ$.

## Flashcards
Q: Interior angle sum of an $n$-gon?
A: $(n - 2) \times 180^\circ$.

Q: Exterior angle sum of any convex polygon?
A: $360^\circ$.

Q: Exterior angle theorem?
A: An exterior angle equals the sum of the two remote interior angles.

Q: Parallel lines: co-interior angles?
A: Add to $180^\circ$.
