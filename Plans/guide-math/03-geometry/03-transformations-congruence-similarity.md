@@ 1 | MODULE | Phase 3 — Transformations, Congruence and Similarity (Core) | Translations, reflections, rotations, dilations, symmetry, congruence criteria, proofs, similarity, proportional segments | MUST
# Phase 3: Transformations, congruence and similarity

**Transformations** move, flip, turn and resize shapes. They're the mathematics behind every graphics engine: when a game character walks, turns or zooms, the computer applies these transformations to thousands of points per frame.

**Congruent** shapes are exact copies (same shape and size); **similar** shapes have the same shape but may differ in size. These ideas let you prove facts and measure things you can't reach directly.

@@ 2 | MODULE | Transformations | Translations, reflections, rotations, dilations, compositions and symmetry | SHOULD
A transformation is a rule that moves every point of a shape. Coordinate rules make them easy to compute.

@@ 3 | TOPIC | Translations and reflections | Sliding by a vector, reflecting across axes and the lines y = x and y = −x | SHOULD
# Translations and reflections

## 1. What it covers
- **Translations:** sliding every point by the same vector $\langle a, b\rangle$.
- **Reflections:** flipping across the x-axis, y-axis, $y = x$, $y = -x$, or a vertical/horizontal line.
- Coordinate rules for each.

## 2. Where it is used
- **Games and UI:** moving sprites and windows (translation); mirrored sprites for facing left/right (reflection).
- **CSS:** `transform: translate(20px, 10px)` and `scaleX(-1)` (a horizontal reflection).
- **Physics and optics:** mirror images; angle of incidence = angle of reflection.
- **Design:** symmetric logos and patterns.

## 3. What it means
**Translation:** every point moves the same distance in the same direction, like sliding a book across a table. Shape, size and orientation stay the same.

**Reflection:** every point jumps to the other side of a mirror line, the same distance away, so the line is the perpendicular bisector of each point and its image. Shape and size stay the same, but **orientation reverses** (left becomes right, like a mirror).

**Notation:** the image of $A$ is written $A'$ ("A prime").

## 4. Formula & worked example
| Transformation | Rule |
|---|---|
| Translate by $\langle a, b\rangle$ | $(x, y) \to (x + a,\ y + b)$ |
| Reflect in x-axis | $(x, y) \to (x, -y)$ |
| Reflect in y-axis | $(x, y) \to (-x, y)$ |
| Reflect in $y = x$ | $(x, y) \to (y, x)$ |
| Reflect in $y = -x$ | $(x, y) \to (-y, -x)$ |
| Reflect in $x = k$ | $(x, y) \to (2k - x,\ y)$ |

**Example 1.** Translate triangle $A(1, 2)$, $B(4, 2)$, $C(2, 5)$ by $\langle -3, 1\rangle$.
- $A'(-2, 3)$, $B'(1, 3)$, $C'(-1, 6)$

**Example 2.** Reflect $P(3, -4)$ in the x-axis → $(3, 4)$; in the y-axis → $(-3, -4)$; in $y = x$ → $(-4, 3)$.

**Example 3.** Reflect $(5, 2)$ in the line $x = 1$.
- Distance from the line is 4 → image at $x = 1 - 4 = -3$ → $(-3, 2)$ (rule: $2(1) - 5 = -3$ ✓)

**Example 4.** Which translation maps $(2, 7)$ to $(-1, 3)$? → $\langle -3, -4\rangle$.

## 5. Common mistakes
- Changing the wrong coordinate (reflection in the **x-axis** changes **y**).
- For $y = x$, forgetting to swap.
- Measuring the distance to a line like $x = k$ from the origin instead of from the line.

## 6. Practice
Q: Translate $(4, -1)$ by $\langle 2, 5\rangle$.
A: $(6, 4)$.

Q: Reflect $(-2, 6)$ in the y-axis.
A: $(2, 6)$.

Q: Reflect $(3, 8)$ in $y = x$.
A: $(8, 3)$.

Q: Reflect $(1, 4)$ in the line $y = 2$.
A: $(1, 0)$.

Q: What property does a reflection change that a translation doesn't?
A: Orientation (it creates a mirror image).

@@ 3 | TOPIC | Rotations | Rotating about the origin by 90°, 180°, 270°; rotation about any point; the rotation formula | SHOULD
# Rotations

## 1. What it covers
- Rotation by an angle about a **centre**, anticlockwise for positive angles.
- Coordinate rules for $90^\circ$, $180^\circ$, $270^\circ$ about the origin.
- Rotating about a point other than the origin.
- The general rotation formula (link to trigonometry).

## 2. Where it is used
- **Games and graphics:** turning characters, spinning wheels, rotating the camera; every 2D/3D engine uses the rotation formula.
- **Robotics:** robot arm joints rotate; robot heading changes.
- **Image editing:** rotating photos.
- **Mechanical engineering:** gears and turbines.

## 3. What it means
A rotation turns every point around a fixed centre by the same angle, like the hands of a clock (but positive angles go **anticlockwise**). Distances to the centre stay the same; shape and size are kept.

**About the origin:**
- $90^\circ$: $(x, y) \to (-y, x)$
- $180^\circ$: $(x, y) \to (-x, -y)$
- $270^\circ$ (or $-90^\circ$): $(x, y) \to (y, -x)$

**About another point $C$:** translate so that $C$ moves to the origin, rotate, then translate back.

**Any angle:** the general rule uses trig (see the sum identities in the Trigonometry roadmap):
$$
(x, y) \to (x\cos\theta - y\sin\theta,\ x\sin\theta + y\cos\theta)
$$

## 4. Formula & worked example
**Example 1.** Rotate $A(3, 1)$ by $90^\circ$ about the origin → $(-1, 3)$.

**Example 2.** Rotate $(2, -5)$ by $180^\circ$ → $(-2, 5)$.

**Example 3.** Rotate $(4, 2)$ by $270^\circ$ → $(2, -4)$.

**Example 4 (about a point).** Rotate $P(5, 3)$ by $90^\circ$ about $C(2, 1)$.
- Shift: $P - C = (3, 2)$ → rotate: $(-2, 3)$ → shift back: $(0, 4)$

**Example 5 (any angle, code).**
```python
import math
def rotate(x, y, deg, cx=0, cy=0):
    t = math.radians(deg)
    x, y = x - cx, y - cy                      # move centre to origin
    xr = x * math.cos(t) - y * math.sin(t)
    yr = x * math.sin(t) + y * math.cos(t)
    return round(xr + cx, 6), round(yr + cy, 6)  # move back
print(rotate(5, 3, 90, 2, 1))   # (0.0, 4.0)
```
This follows the three steps of Example 4: translate the centre to the origin, apply the rotation formula, translate back. It works for any angle, not just multiples of 90°.

## 5. Common mistakes
- Rotating clockwise for a positive angle.
- Mixing up the $90^\circ$ and $270^\circ$ rules.
- Rotating about the origin when the question gives another centre.

## 6. Practice
Q: Rotate $(1, 6)$ by $90^\circ$ about the origin.
A: $(-6, 1)$.

Q: Rotate $(-3, 4)$ by $180^\circ$.
A: $(3, -4)$.

Q: Rotate $(2, 7)$ by $270^\circ$.
A: $(7, -2)$.

Q: What stays the same under a rotation?
A: Distances, angles, shape and size (and the distance of each point from the centre).

Q: Rotate $(4, 0)$ by $60^\circ$ about the origin.
A: $(2, 2\sqrt{3})$.

@@ 3 | TOPIC | Dilations | Scale factor and centre, enlargements and reductions, coordinates, effect on length and area | SHOULD
# Dilations

## 1. What it covers
- A **dilation** with centre $C$ and **scale factor** $k$.
- Enlargement ($k > 1$) and reduction ($0 < k < 1$).
- Coordinate rule about the origin and about another centre.
- What changes (lengths) and what doesn't (angles, parallel lines).

## 2. Where it is used
- **Zooming:** maps, images, and pinch-to-zoom on phones.
- **Projectors and cameras:** the image is a dilation of the object.
- **Responsive design:** scaling layouts and vector icons (SVG) without distortion.
- **Similar triangles and models:** scale models, architectural drawings.

## 3. What it means
A dilation stretches or shrinks a shape **from a fixed centre**. Each point moves along the line from the centre, ending $k$ times as far away. It's like a projector: move it back and the image grows, but keeps its shape.

- Lengths multiply by $k$; areas by $k^2$.
- Angles stay the same; parallel lines stay parallel.
- The result is **similar** to the original (not congruent, unless $k = 1$).

## 4. Formula & worked example
$$
\text{about the origin: } (x, y) \to (kx, ky) \qquad \text{about } C(a, b): (x, y) \to \big(a + k(x - a),\ b + k(y - b)\big)
$$

**Example 1.** Dilate $A(2, 3)$, $B(4, -1)$ by $k = 3$ about the origin → $A'(6, 9)$, $B'(12, -3)$.

**Example 2.** Dilate $(8, -4)$ by $k = \frac{1}{4}$ → $(2, -1)$.

**Example 3 (centre not origin).** Dilate $P(5, 4)$ by $k = 2$ about $C(1, 2)$.
- $(1 + 2(4),\ 2 + 2(2)) = (9, 6)$

**Example 4.** A triangle with area 12 is dilated by $k = 2.5$. New area? → $12 \times 6.25 = 75$.

**Example 5.** $A(1, 1)$ maps to $A'(4, 4)$ under a dilation about the origin. Scale factor? → $4$.

## 5. Common mistakes
- Multiplying areas by $k$ instead of $k^2$.
- Dilating about the origin when the centre is elsewhere.
- Thinking a dilation preserves lengths (it doesn't; it preserves angles).

## 6. Practice
Q: Dilate $(-3, 6)$ by $k = \frac{2}{3}$ about the origin.
A: $(-2, 4)$.

Q: A segment of length 5 is dilated by $k = 4$. New length?
A: $20$.

Q: Is a dilated figure congruent or similar to the original?
A: Similar.

Q: Dilate $(3, 3)$ by $k = 3$ about $(1, 1)$.
A: $(7, 7)$.

Q: A square's area goes from 9 to 81. Scale factor?
A: $3$.

@@ 3 | TOPIC | Compositions, symmetry and rigid motions | Sequences of transformations, line and rotational symmetry, what rigid motions preserve | SHOULD
# Compositions, symmetry and rigid motions

## 1. What it covers
- **Composing** transformations (doing one after another); order matters.
- **Rigid motions** (translations, reflections, rotations) preserve distance and angle.
- **Line symmetry** and **rotational symmetry** (order of symmetry).
- Two reflections = a translation or a rotation.

## 2. Where it is used
- **Computer graphics:** transformations are combined into one matrix (the "model-view" matrix); the order of multiplication matters, which is a classic bug.
- **Animation rigs:** a hand's position is the composition of shoulder, elbow and wrist rotations.
- **Design and nature:** logos, snowflakes (6-fold symmetry), flowers, tiles.
- **Chemistry and crystallography:** molecular and crystal symmetry.

## 3. What it means
**Composition:** apply one transformation, then another to the result. Order usually matters: rotating then translating gives a different result from translating then rotating.

**Rigid motions** (also called **isometries**) keep every distance and angle the same, so the image is **congruent** to the original. Dilations are not rigid (they change size).

**Symmetry:**
- **Line symmetry:** a reflection maps the shape onto itself (a butterfly).
- **Rotational symmetry of order $n$:** a rotation by $\frac{360^\circ}{n}$ maps it onto itself (a regular hexagon has order 6).

## 4. Formula & worked example
**Example 1.** Reflect $(2, 5)$ in the y-axis, then translate by $\langle 3, -1\rangle$.
- $(-2, 5)$ → $(1, 4)$

**Example 2 (order matters).** Translate $(2, 5)$ by $\langle 3, -1\rangle$ first, then reflect in the y-axis.
- $(5, 4)$ → $(-5, 4)$ (different from Example 1)

**Example 3.** Reflect in the x-axis, then in the y-axis. Combined effect?
- $(x, y) \to (x, -y) \to (-x, -y)$: a $180^\circ$ rotation about the origin.

**Example 4.** Lines of symmetry and rotational order of a square? → 4 lines; order 4.

**Example 5.** A regular pentagon: 5 lines of symmetry, rotational order 5 (smallest angle $72^\circ$).

**Example 6.** The letter "S": no line symmetry; rotational symmetry of order 2.

## 5. Common mistakes
- Assuming compositions can be done in any order.
- Counting the identity (no turn) as extra rotational symmetry. Order 1 means no rotational symmetry.
- Calling a dilation a rigid motion.

## 6. Practice
Q: Rotate $(1, 3)$ by $90^\circ$, then reflect in the x-axis.
A: $(-3, 1)$ → $(-3, -1)$.

Q: Which transformations are rigid motions?
A: Translations, reflections and rotations.

Q: Rotational symmetry order of an equilateral triangle?
A: 3.

Q: Lines of symmetry of a rectangle (non-square)?
A: 2.

Q: Reflecting across two parallel lines gives what single transformation?
A: A translation.

@@ 2 | MODULE | Congruence and proof | Congruence criteria, proofs, CPCTC, isosceles and equilateral triangles | MUST
Proofs explain **why** something is always true, not just in one drawing.

@@ 3 | TOPIC | Triangle congruence: SSS, SAS, ASA, AAS and HL | What congruence means, the five criteria, and the two that don't work | MUST
# Triangle congruence: SSS, SAS, ASA, AAS and HL

## 1. What it covers
- **Congruent** triangles: all corresponding sides and angles equal.
- The shortcuts: **SSS, SAS, ASA, AAS**, and **HL** for right triangles.
- Why **SSA** and **AAA** don't prove congruence.
- Writing congruence statements with matching vertex order.

## 2. Where it is used
- **Engineering and manufacturing:** identical parts must be congruent; triangular frames are rigid because SSS fixes the shape.
- **Surveying:** measuring an inaccessible distance by building a congruent triangle on accessible ground.
- **Proofs:** most geometry proofs go through congruent triangles.
- **Computer vision:** checking whether two shapes match.

## 3. What it means
Two triangles are **congruent** ($\cong$) if one can be moved by rigid motions to sit exactly on the other. You don't need to check all six parts; some combinations of three force the rest:
- **SSS:** three sides equal. (That's why triangles are rigid: three fixed lengths allow only one shape.)
- **SAS:** two sides and the angle **between** them.
- **ASA:** two angles and the side between them.
- **AAS:** two angles and a non-included side (the third angle is then fixed anyway).
- **HL:** in right triangles, hypotenuse and one leg.

**Don't work:**
- **AAA:** same angles, but sizes can differ (similar, not congruent).
- **SSA:** can give two different triangles (the ambiguous case from the Trigonometry roadmap).

**Order matters:** $\triangle ABC \cong \triangle DEF$ means $A \leftrightarrow D$, $B \leftrightarrow E$, $C \leftrightarrow F$.

## 4. Formula & worked example
**Example 1.** $AB = DE$, $BC = EF$, $\angle B = \angle E$. Congruent? → Yes, SAS ($\angle B$ is between $AB$ and $BC$).

**Example 2.** $\angle A = \angle D$, $\angle B = \angle E$, $BC = EF$. → Yes, AAS.

**Example 3.** All three angles equal. → Not enough (AAA).

**Example 4 (river width).** To measure across a river from $A$ to a tree $T$: walk along the bank from $A$ to $B$, continue the same distance to $C$, then walk away from the river at right angles until $D$, where $D$, $B$ and $T$ line up. Then $\triangle ABT \cong \triangle CBD$ (ASA: right angles, $AB = CB$, vertical angles at $B$), so $CD = AT$, which you can measure.

**Example 5.** $\triangle PQR \cong \triangle XYZ$, $PQ = 7$, $\angle R = 40^\circ$. Then $XY = 7$ and $\angle Z = 40^\circ$.

## 5. Common mistakes
- Using SSA or AAA as a congruence criterion.
- Using SAS when the angle isn't **between** the two sides.
- Writing the congruence statement with vertices in the wrong order.

## 6. Practice
Q: Two sides and the included angle are equal. Which criterion?
A: SAS.

Q: Why doesn't AAA prove congruence?
A: Triangles with the same angles can have different sizes.

Q: Right triangles with equal hypotenuses and one equal leg. Criterion?
A: HL.

Q: $\triangle ABC \cong \triangle KLM$. Which angle equals $\angle B$?
A: $\angle L$.

Q: Why are triangles used in rigid frames?
A: SSS: three fixed side lengths allow only one shape.

@@ 3 | TOPIC | Writing geometric proofs and CPCTC | Two-column and paragraph proofs, reasons, using congruent triangles to prove parts equal | MUST
# Writing geometric proofs and CPCTC

## 1. What it covers
- The structure of a proof: **given**, **prove**, statements and reasons.
- Two-column and paragraph formats.
- **CPCTC:** Corresponding Parts of Congruent Triangles are Congruent.
- Common reasons: definitions, vertical angles, reflexive property, parallel-line angles, congruence criteria.

## 2. Where it is used
- **Logical thinking:** step-by-step justification is the same skill as debugging and code review ("why is this true?").
- **Computer science:** proving algorithms correct; formal verification.
- **Law and argument:** building a case from accepted facts.
- **All higher maths.**

## 3. What it means
A proof is a chain of statements, each justified by something already accepted: a given fact, a definition, a postulate or a theorem proved earlier. Like a court case, every claim needs evidence.

**Typical plan for proving two segments or angles equal:**
1. Find two triangles that contain them.
2. Prove the triangles congruent (SSS, SAS, ASA, AAS, HL).
3. Conclude the parts are equal by **CPCTC**.

**Useful reasons:**
- **Reflexive property:** a shared side equals itself ($AC = AC$).
- **Vertical angles are equal.**
- **Definition of midpoint / bisector / perpendicular.**
- **Alternate angles** (parallel lines).

## 4. Formula & worked example
**Example 1.** Given: $AB = CB$ and $BD$ bisects $\angle ABC$ ($D$ on $AC$). Prove: $AD = CD$.

| Statement | Reason |
|---|---|
| $AB = CB$ | Given |
| $\angle ABD = \angle CBD$ | Definition of angle bisector |
| $BD = BD$ | Reflexive property |
| $\triangle ABD \cong \triangle CBD$ | SAS |
| $AD = CD$ | CPCTC |

**Example 2 (paragraph).** Given: $M$ is the midpoint of $\overline{AB}$ and of $\overline{CD}$. Prove: $AC = BD$.
- Since $M$ is the midpoint of both, $AM = BM$ and $CM = DM$. The angles $\angle AMC$ and $\angle BMD$ are vertical angles, so they're equal. So $\triangle AMC \cong \triangle BMD$ by SAS, and $AC = BD$ by CPCTC.

**Example 3 (parallel lines).** Given: $AB \parallel CD$ and $AB = CD$, with $AD$ and $BC$ meeting at $E$. Prove: $E$ is the midpoint of $AD$.
- Alternate angles: $\angle BAE = \angle CDE$ and $\angle ABE = \angle DCE$; with $AB = CD$, $\triangle ABE \cong \triangle DCE$ (ASA). By CPCTC, $AE = DE$.

## 5. Common mistakes
- Using what you want to prove as a reason.
- Using CPCTC **before** proving the triangles congruent.
- Missing the reflexive property for a shared side.

## 6. Practice
Q: What does CPCTC stand for?
A: Corresponding parts of congruent triangles are congruent.

Q: Two triangles share a side. Which property justifies that the side is equal to itself?
A: The reflexive property.

Q: What is the usual three-step plan to prove two segments equal?
A: Find triangles containing them, prove the triangles congruent, then use CPCTC.

Q: Two lines cross at $M$. Why are the opposite angles equal?
A: Vertical angles are equal.

Q: Can you use CPCTC to prove two triangles are congruent?
A: No, CPCTC is used after congruence is established.

@@ 3 | TOPIC | Isosceles and equilateral triangles | Base angles theorem and converse, properties of the altitude/median in isosceles triangles, equilateral facts | MUST
# Isosceles and equilateral triangles

## 1. What it covers
- **Isosceles triangle theorem:** the angles opposite equal sides (base angles) are equal, and its converse.
- In an isosceles triangle, the line from the apex to the midpoint of the base is also the altitude and the angle bisector.
- **Equilateral triangles:** all angles $60^\circ$.

## 2. Where it is used
- **Architecture:** gable roofs and A-frames are isosceles; the vertical centre post is the altitude.
- **Engineering:** symmetric trusses spread loads evenly.
- **Design:** equilateral triangles in logos, warning signs, hex grids (6 equilateral triangles).
- **Proofs and problem solving:** spotting equal sides gives equal angles for free.

## 3. What it means
Fold an isosceles triangle along its line of symmetry: the two equal sides land on each other, and so do the two **base angles**. So base angles are equal. The fold line goes from the apex to the midpoint of the base, at right angles to it, and splits the apex angle in half. It's the median, altitude and angle bisector all at once.

**Converse:** if two angles are equal, the sides opposite them are equal.

**Equilateral:** all sides equal → all angles equal → each $\frac{180^\circ}{3} = 60^\circ$.

## 4. Formula & worked example
**Example 1.** Isosceles triangle with apex angle $40^\circ$. Base angles? → $\frac{180 - 40}{2} = 70^\circ$ each.

**Example 2.** A base angle is $52^\circ$. Apex? → $180 - 104 = 76^\circ$.

**Example 3 (algebra).** In $\triangle ABC$, $AB = AC$, $\angle B = 3x + 5$, $\angle C = 5x - 25$. Find $\angle A$.
- $3x + 5 = 5x - 25$ → $x = 15$ → $\angle B = \angle C = 50^\circ$ → $\angle A = 80^\circ$

**Example 4 (roof).** An A-frame roof has equal rafters 5 m long and a span of 8 m. Height of the ridge?
- The altitude bisects the base: half-span 4 → height $= \sqrt{25 - 16} = 3$ m

**Example 5 (converse).** A triangle has two $65^\circ$ angles. By the converse, the sides opposite those angles are equal, so the triangle is isosceles (with apex angle $50^\circ$).

## 5. Common mistakes
- Making the apex angle equal to a base angle.
- Assuming a triangle is isosceles because it looks it.
- Forgetting that the altitude of an isosceles triangle bisects the **base** (only from the apex).

## 6. Practice
Q: Base angles of an isosceles triangle with apex $100^\circ$?
A: $40^\circ$ each.

Q: Each angle of an equilateral triangle?
A: $60^\circ$.

Q: A triangle has angles $70^\circ$, $70^\circ$, $40^\circ$. Which sides are equal?
A: The two sides opposite the $70^\circ$ angles.

Q: Isosceles triangle with equal sides 13 and base 10. Height?
A: $\sqrt{169 - 25} = 12$.

Q: What three lines coincide from the apex of an isosceles triangle?
A: The median, the altitude and the angle bisector.

@@ 2 | MODULE | Similarity | Similar figures, AA/SAS/SSS similarity, proportional segments, indirect measurement | MUST
Similar figures are scaled copies: same angles, proportional sides.

@@ 3 | TOPIC | Similar triangles and similarity criteria | Definition, scale factor, AA, SAS and SSS similarity, finding missing sides | MUST
# Similar triangles and similarity criteria

## 1. What it covers
- **Similar** figures: equal angles and proportional sides.
- The **scale factor**.
- Similarity shortcuts for triangles: **AA**, **SAS~**, **SSS~**.
- Finding missing lengths with proportions.

## 2. Where it is used
- **Photography and optics:** a camera's image is a similar triangle to the scene (pinhole camera).
- **Maps and models:** scale drawings are similar to reality.
- **Surveying and astronomy:** measuring heights and distances indirectly.
- **Computer vision:** estimating an object's distance from its size in a photo.
- **Trigonometry:** trig ratios exist **because** all right triangles with the same angle are similar.

## 3. What it means
Similar triangles ($\sim$) have the **same shape**: corresponding angles equal and corresponding sides in the same ratio $k$ (the scale factor).

**Shortcuts:**
- **AA:** two pairs of equal angles are enough (the third must match too). This is the most used one.
- **SSS~:** all three pairs of sides in the same ratio.
- **SAS~:** two pairs of sides in the same ratio and the included angles equal.

**Finding sides:** set up a proportion with **matching** sides in the same order: $\frac{\text{small}}{\text{big}} = \frac{\text{small}}{\text{big}}$.

## 4. Formula & worked example
$$
\triangle ABC \sim \triangle DEF \Rightarrow \frac{AB}{DE} = \frac{BC}{EF} = \frac{CA}{FD} = k
$$

**Example 1.** $\triangle ABC \sim \triangle DEF$, $AB = 6$, $DE = 9$, $BC = 8$. Find $EF$.
- $k = \frac{9}{6} = 1.5$ → $EF = 12$

**Example 2 (AA).** Two triangles have angles $50^\circ, 60^\circ$ and $60^\circ, 70^\circ$. Similar?
- Third angles: $70^\circ$ and $50^\circ$ → both are $50^\circ, 60^\circ, 70^\circ$ → yes.

**Example 3 (SSS~).** Sides 4, 6, 8 and 6, 9, 12: ratios $\frac{6}{4} = \frac{9}{6} = \frac{12}{8} = 1.5$ → similar.

**Example 4 (nested triangles).** In $\triangle ABC$, $DE \parallel BC$ with $D$ on $AB$, $E$ on $AC$. $AD = 4$, $DB = 6$, $DE = 5$. Find $BC$.
- $\triangle ADE \sim \triangle ABC$ (AA, corresponding angles) → $\frac{BC}{5} = \frac{10}{4}$ → $BC = 12.5$

## 5. Common mistakes
- Matching the wrong sides (use the angle correspondence, not position on the page).
- In nested triangles, using $DB$ instead of the whole side $AB$.
- Thinking similar means congruent.

## 6. Practice
Q: Minimum information for AA similarity?
A: Two pairs of equal angles.

Q: $\triangle PQR \sim \triangle STU$ with $k = 3$ and $PQ = 4$. Find $ST$.
A: $12$.

Q: Are triangles with sides 3, 4, 5 and 9, 12, 15 similar?
A: Yes, ratio 3.

Q: Similar triangles: $\frac{x}{6} = \frac{10}{4}$. Find $x$.
A: $15$.

Q: Why are all equilateral triangles similar?
A: All their angles are $60^\circ$ (AA).

@@ 3 | TOPIC | Proportional segments: triangle proportionality, midsegment and angle bisector theorems | Side-splitter theorem, midsegment theorem, angle bisector theorem | MUST
# Proportional segments: triangle proportionality, midsegment and angle bisector theorems

## 1. What it covers
- **Triangle proportionality (side-splitter) theorem:** a line parallel to one side divides the other two sides proportionally.
- **Midsegment theorem:** the segment joining two midpoints is parallel to the third side and half its length.
- **Angle bisector theorem:** an angle bisector divides the opposite side in the ratio of the adjacent sides.

## 2. Where it is used
- **Construction:** dividing a beam or board into equal parts using parallel lines.
- **Design and drafting:** scaling drawings; finding midpoints quickly.
- **Computer graphics:** linear interpolation along edges uses these proportions.
- **Problem solving:** quick ways to find lengths without trig.

## 3. What it means
- **Side-splitter:** if $DE \parallel BC$ in $\triangle ABC$, then $\frac{AD}{DB} = \frac{AE}{EC}$. The parallel line makes a smaller similar triangle, so the pieces are in proportion.
- **Midsegment:** joining the midpoints of two sides gives a segment parallel to the third side and **half** as long (the smaller triangle has scale factor $\frac{1}{2}$).
- **Angle bisector:** the bisector of $\angle A$ meets $BC$ at $D$ with $\frac{BD}{DC} = \frac{AB}{AC}$. The longer neighbouring side gets the longer piece.

## 4. Formula & worked example
$$
DE \parallel BC \Rightarrow \frac{AD}{DB} = \frac{AE}{EC} \qquad \text{midsegment} = \tfrac{1}{2}\,\text{third side} \qquad \frac{BD}{DC} = \frac{AB}{AC}
$$

**Example 1 (side-splitter).** $AD = 6$, $DB = 4$, $AE = 9$. Find $EC$.
- $\frac{6}{4} = \frac{9}{EC}$ → $EC = 6$

**Example 2 (midsegment).** The midsegment of a triangle is $3x - 1$ and the parallel side is $4x + 8$.
- $2(3x - 1) = 4x + 8$ → $x = 5$ → midsegment 14, side 28

**Example 3 (angle bisector).** $AB = 10$, $AC = 15$, $BC = 20$. The bisector of $\angle A$ meets $BC$ at $D$. Find $BD$.
- $\frac{BD}{DC} = \frac{10}{15} = \frac{2}{3}$ → $BD = \frac{2}{5}(20) = 8$

**Example 4 (dividing a board).** To split a board into 3 equal parts without measuring it, lay a ruler at an angle with marks at 0, 10, 20, 30 cm touching the two edges; parallel lines through the marks divide the board equally (side-splitter theorem).

## 5. Common mistakes
- Using $\frac{AD}{AB}$ on one side and $\frac{AE}{EC}$ on the other (mix of piece and whole).
- Making the midsegment equal to the third side instead of half.
- In the angle bisector theorem, matching each piece with the wrong side (each piece goes with the side **next to it**).

## 6. Practice
Q: Midsegment parallel to a side of length 18. Its length?
A: $9$.

Q: $DE \parallel BC$, $AD = 5$, $DB = 10$, $AE = 4$. Find $EC$.
A: $8$.

Q: Angle bisector: $AB = 6$, $AC = 9$, $BC = 10$. Find $BD$.
A: $\frac{6}{15}(10) = 4$.

Q: What can you say about the four small triangles formed by the three midsegments of a triangle?
A: They are congruent, each similar to the original with scale factor $\frac{1}{2}$.

Q: State the side-splitter theorem.
A: A line parallel to one side of a triangle divides the other two sides proportionally.

@@ 3 | TOPIC | Indirect measurement with similar triangles | Shadows, mirrors, pinhole cameras, measuring heights and widths | MUST
# Indirect measurement with similar triangles

## 1. What it covers
- Measuring heights and distances you can't reach, using similar triangles.
- The **shadow method**, the **mirror method** and the pinhole camera.
- Estimating distances from apparent size.

## 2. Where it is used
- **Surveying and forestry:** tree and building heights.
- **History:** Thales measured the height of the Great Pyramid from its shadow around 600 BCE.
- **Photography and computer vision:** estimating distance from the size of an object in an image (used in self-driving car perception).
- **Astronomy:** the size of the Sun and Moon from eclipses and shadows.

## 3. What it means
**Shadows:** at a given moment, the sun's rays are parallel, so a person and a tree make similar triangles with their shadows:
$$
\frac{\text{tree height}}{\text{tree shadow}} = \frac{\text{person height}}{\text{person shadow}}
$$

**Mirror:** put a mirror on the ground and step back until you see the top of the tree in it. The angles of incidence and reflection are equal, so your triangle and the tree's triangle are similar.

**Pinhole camera / camera:** the object and its image form similar triangles through the hole (or lens).

## 4. Formula & worked example
**Example 1 (shadow).** A 1.6 m person casts a 2 m shadow; a tree casts a 15 m shadow.
- $\frac{h}{15} = \frac{1.6}{2}$ → $h = 12$ m

**Example 2 (mirror).** A mirror lies 8 m from a building. A person whose eyes are 1.5 m high stands 1.2 m from the mirror and sees the top. Building height?
- $\frac{h}{8} = \frac{1.5}{1.2}$ → $h = 10$ m

**Example 3 (pinhole camera).** A 2 m tall person is 5 m from a pinhole; the screen is 0.1 m behind the hole. Image height?
- $\frac{x}{0.1} = \frac{2}{5}$ → $x = 0.04$ m $= 4$ cm (upside down)

**Example 4 (thumb trick).** Holding a 1.5 cm wide thumb at arm's length (60 cm), it just covers a car about 4.5 m long. Distance to the car?
- $\frac{d}{4.5} = \frac{60}{1.5}$ → $d = 180$ m

## 5. Common mistakes
- Mixing up which measurements belong to which triangle.
- Using shadows measured at different times (the sun's angle changes).
- Forgetting eye height when it matters.

## 6. Practice
Q: A 2 m pole casts a 3 m shadow; a building casts a 45 m shadow. Height?
A: $30$ m.

Q: Why are the shadow triangles similar?
A: The sun's rays are parallel, so the angles match (AA).

Q: A mirror is 10 m from a tree; you stand 2 m from the mirror with eyes at 1.6 m. Tree height?
A: $8$ m.

Q: In a pinhole camera, the image is 3 cm tall at 6 cm from the hole; the object is 10 m away. Object height?
A: $5$ m.

Q: Who first measured the Great Pyramid with shadows?
A: Thales of Miletus.

@@ 3 | TOPIC | Practice & review: Transformations, congruence and similarity | Mixed problem set with worked answers | MUST
# Practice & review: Transformations, congruence and similarity

**How to use this:** 45 minutes. Draw diagrams.

## Problems
1. Reflect $A(3, -2)$ in $y = x$, then translate by $\langle -1, 4\rangle$.
2. Rotate $(6, 1)$ by $90^\circ$ about $(2, 3)$.
3. Dilate the triangle $(0, 0)$, $(4, 0)$, $(0, 2)$ by $k = 1.5$ about the origin. Find the new area.
4. Which criterion (if any) proves congruence? (a) $AB = DE$, $AC = DF$, $\angle B = \angle E$. (b) $\angle A = \angle D$, $AB = DE$, $\angle B = \angle E$.
5. Given: $AB \parallel DC$, $AB = DC$. Prove $\triangle ABC \cong \triangle CDA$ (quadrilateral $ABCD$ with diagonal $AC$).
6. Isosceles $\triangle PQR$ with $PQ = PR$ and $\angle Q = 2x + 10$, $\angle P = 3x$. Find all angles.
7. $DE \parallel BC$ in $\triangle ABC$; $AD = x$, $DB = x + 3$, $AE = 4$, $EC = 6$. Find $x$.
8. A 1.8 m person casts a 2.4 m shadow when a flagpole casts a 16 m shadow. Flagpole height?
9. Two similar cylinders have heights 6 and 9. The small one holds 400 mL. How much does the large one hold?

## Answers
1. $(-2, 3)$ → $(-3, 7)$.
2. $(6, 1) - (2, 3) = (4, -2)$ → rotate: $(2, 4)$ → add back: $(4, 7)$.
3. New vertices $(0, 0)$, $(6, 0)$, $(0, 3)$; area $9$ (original 4, × 2.25).
4. (a) SSA → not a valid criterion. (b) ASA ✓.
5. $AB = CD$ (given); $\angle BAC = \angle DCA$ (alternate angles, $AB \parallel DC$); $AC = CA$ (reflexive) → SAS.
6. $\angle Q = \angle R = 2x + 10$; $3x + 2(2x + 10) = 180$ → $x = \frac{160}{7} \approx 22.86$ → $\angle P \approx 68.6^\circ$, $\angle Q = \angle R \approx 55.7^\circ$.
7. $\frac{x}{x + 3} = \frac{4}{6}$ → $6x = 4x + 12$ → $x = 6$.
8. $\frac{h}{16} = \frac{1.8}{2.4}$ → $h = 12$ m.
9. $k = 1.5$ → volume × $3.375$ → $1350$ mL.

## Flashcards
Q: Rule for rotating $90^\circ$ about the origin?
A: $(x, y) \to (-y, x)$.

Q: Five triangle congruence criteria?
A: SSS, SAS, ASA, AAS, HL.

Q: Most common triangle similarity shortcut?
A: AA (two equal angles).

Q: Midsegment theorem?
A: It is parallel to the third side and half its length.
