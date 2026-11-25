@@ 1 | MODULE | Phase 3 — Non-Right Triangles (Core) | Law of sines, the ambiguous case, law of cosines, triangle area, real-world applications | MUST
# Phase 3: Non-right triangles

SOH-CAH-TOA only works when there's a $90^\circ$ angle. Real triangles (a plot of land, three cell towers, the forces on a crane) usually don't have one. Two laws handle **every** triangle: the **law of sines** and the **law of cosines**.

**Labelling (used in every topic):** angles $A$, $B$, $C$; the side opposite each is $a$, $b$, $c$.

**Which law?** It depends on what you know:
| You know | Name | Use |
|---|---|---|
| Two angles + any side | AAS / ASA | Law of sines |
| Two sides + a non-included angle | SSA | Law of sines (watch the ambiguous case) |
| Two sides + the included angle | SAS | Law of cosines |
| All three sides | SSS | Law of cosines |

@@ 2 | MODULE | Laws of sines and cosines | Solving any triangle, the ambiguous SSA case, area formulas, applications | MUST
These two laws, plus the angle sum $A + B + C = 180^\circ$, solve every triangle.

@@ 3 | TOPIC | Law of sines | a/sin A = b/sin B = c/sin C, solving AAS and ASA triangles | MUST
# Law of sines

## 1. What it covers
- The law of sines: $\frac{a}{\sin A} = \frac{b}{\sin B} = \frac{c}{\sin C}$.
- Why it's true (from the height of the triangle).
- Solving triangles when you know two angles and a side (AAS and ASA).

## 2. Where it is used
- **Surveying:** measuring the distance to a point across a river from two angles and a baseline (triangulation).
- **GPS and phone location:** triangulation from known points.
- **Astronomy:** the distance to nearby stars via parallax.
- **Navigation:** fixing a ship's position from bearings to two lighthouses.
- **Forestry:** the height of a tree on a slope.

## 3. What it means
In any triangle, **bigger angles face bigger sides**, and the law of sines makes this exact: each side divided by the sine of its opposite angle gives the same number.

**Why it is true.** Drop a height $h$ from $C$ to side $c$. From the two right triangles this creates:
$h = b\sin A$ and $h = a\sin B$. So $b\sin A = a\sin B$, which rearranges to $\frac{a}{\sin A} = \frac{b}{\sin B}$. The same trick with another height brings in $c$.

**Analogy:** in a family, a taller parent casts a longer shadow. Each "side : opposite-angle" pair keeps the same ratio, like a fixed exchange rate.

(That common ratio equals $2R$, the diameter of the circle through all three corners. You won't need that often, but it's a nice check.)

## 4. Formula & worked example
$$
\frac{a}{\sin A} = \frac{b}{\sin B} = \frac{c}{\sin C} \qquad \text{or flipped:} \qquad \frac{\sin A}{a} = \frac{\sin B}{b} = \frac{\sin C}{c}
$$
Use the first form to find a **side**, the flipped form to find an **angle**.

**Example 1 (AAS).** $A = 40^\circ$, $B = 75^\circ$, $a = 12$. Solve the triangle.
- $C = 180^\circ - 40^\circ - 75^\circ = 65^\circ$
- $b = \frac{a\sin B}{\sin A} = \frac{12\sin 75^\circ}{\sin 40^\circ} \approx \frac{12(0.9659)}{0.6428} \approx 18.03$
- $c = \frac{12\sin 65^\circ}{\sin 40^\circ} \approx \frac{12(0.9063)}{0.6428} \approx 16.92$
- Check: largest angle $B$ faces largest side $b$ ✓

**Example 2 (ASA): across a river.** Two points A and B are 200 m apart on one bank. A tree C is on the other bank. Angle $CAB = 52^\circ$, angle $CBA = 61^\circ$. How far is the tree from A?
- $C = 180^\circ - 52^\circ - 61^\circ = 67^\circ$, and the side opposite $C$ is $AB = 200$.
- $AC$ is opposite angle $B$: $AC = \frac{200\sin 61^\circ}{\sin 67^\circ} \approx \frac{200(0.8746)}{0.9205} \approx 190.0$ m
- Width of the river (perpendicular distance from C to AB): $190.0\sin 52^\circ \approx 149.7$ m

**Example 3: find an angle.** $a = 10$, $b = 7$, $A = 80^\circ$. Find $B$.
- $\sin B = \frac{7\sin 80^\circ}{10} \approx 0.6894$ → $B \approx 43.6^\circ$
- (Could $B$ be $180^\circ - 43.6^\circ = 136.4^\circ$? Then $A + B > 180^\circ$, so no. One triangle only. The next topic covers when two triangles are possible.)

## 5. Common mistakes
- Pairing a side with the wrong angle. Always pair a side with the angle **opposite** it.
- Using the law of sines for SAS or SSS. You need at least one complete side-angle opposite pair.
- Forgetting $\sin^{-1}$ gives only the acute answer; an obtuse angle with the same sine may also work (ambiguous case).

## 6. Practice
Q: State the law of sines.
A: $\frac{a}{\sin A} = \frac{b}{\sin B} = \frac{c}{\sin C}$.

Q: $A = 30^\circ$, $a = 5$, $B = 45^\circ$. Find $b$.
A: $b = \frac{5\sin 45^\circ}{\sin 30^\circ} = 5\sqrt{2} \approx 7.07$.

Q: Which triangle cases (AAS, ASA, SAS, SSS, SSA) use the law of sines?
A: AAS, ASA and SSA.

Q: $B = 50^\circ$, $C = 70^\circ$, $a = 9$. Find $b$.
A: $A = 60^\circ$; $b = \frac{9\sin 50^\circ}{\sin 60^\circ} \approx 7.96$.

Q: In a triangle, angle $A$ is the largest. Which side is longest?
A: Side $a$, opposite $A$.

@@ 3 | TOPIC | The ambiguous case (SSA) | When two sides and a non-included angle give 0, 1 or 2 triangles | MUST
# The ambiguous case (SSA)

## 1. What it covers
- Why knowing two sides and a **non-included** angle (SSA) can give **no triangle, one, or two**.
- A reliable test using the height $h = b\sin A$, or using $\sin^{-1}$ and checking the obtuse option.
- Solving both triangles when two exist.

## 2. Where it is used
- **Surveying and navigation:** a bearing and two distances can match two different positions; you must use extra information to pick the right one.
- **Engineering and CAD:** constraint solvers in design software meet exactly this ambiguity when a sketch is under-constrained.
- **Robotics:** a two-link arm can reach a point "elbow up" or "elbow down"; these are the two triangles.
- **Exams:** a classic trick question.

## 3. What it means
Imagine you know angle $A$, side $b$ (next to $A$) and side $a$ (opposite $A$). Fix side $b$ and the angle at $A$. Now side $a$ is like a **swinging arm** hinged at $C$, which must reach the line from $A$.
- If $a$ is too short to reach the line → **no triangle**.
- If $a$ just touches the line at a right angle → **one right triangle**.
- If $a$ is a bit longer, it can hit the line at **two** points → **two triangles**.
- If $a$ is longer than $b$, it hits once on the useful side → **one triangle**.

**Analogy:** a door hinge with a fixed-length chain to the floor: depending on the chain length, it may not reach, touch at one point, or reach at two points (one in front, one behind).

The "touching" height is $h = b\sin A$.

## 4. Formula & worked example
For acute $A$ (with $h = b\sin A$):
| Condition | Triangles |
|---|---|
| $a < h$ | 0 |
| $a = h$ | 1 (right triangle) |
| $h < a < b$ | **2** |
| $a \ge b$ | 1 |
If $A$ is obtuse: one triangle if $a > b$, otherwise none.

**Practical method:** compute $\sin B = \frac{b\sin A}{a}$.
- If $\sin B > 1$ → no triangle.
- Otherwise $B_1 = \sin^{-1}(\ldots)$ and $B_2 = 180^\circ - B_1$. Keep each one where $A + B < 180^\circ$.

**Example 1 (two triangles).** $A = 35^\circ$, $a = 8$, $b = 12$.
- $h = 12\sin 35^\circ \approx 6.88$; $6.88 < 8 < 12$ → two triangles.
- $\sin B = \frac{12\sin 35^\circ}{8} \approx 0.8604$ → $B_1 \approx 59.4^\circ$, $B_2 \approx 120.6^\circ$
- Triangle 1: $C_1 = 180^\circ - 35^\circ - 59.4^\circ = 85.6^\circ$, $c_1 = \frac{8\sin 85.6^\circ}{\sin 35^\circ} \approx 13.91$
- Triangle 2: $C_2 = 180^\circ - 35^\circ - 120.6^\circ = 24.4^\circ$, $c_2 = \frac{8\sin 24.4^\circ}{\sin 35^\circ} \approx 5.76$

**Example 2 (no triangle).** $A = 50^\circ$, $a = 5$, $b = 10$.
- $\sin B = \frac{10\sin 50^\circ}{5} \approx 1.532 > 1$ → impossible.

**Example 3 (one triangle).** $A = 40^\circ$, $a = 15$, $b = 10$.
- $a \ge b$ → one triangle. $\sin B = \frac{10\sin 40^\circ}{15} \approx 0.4285$ → $B \approx 25.4^\circ$.
- $B_2 = 154.6^\circ$ would give $A + B_2 = 194.6^\circ > 180^\circ$ ✗.

## 5. Common mistakes
- Stopping after $\sin^{-1}$ and missing the second triangle.
- Keeping the obtuse $B_2$ without checking $A + B_2 < 180^\circ$.
- Thinking the ambiguity also happens with SAS or ASA. It only happens with SSA.

## 6. Practice
Q: Which given information can produce two different triangles?
A: SSA: two sides and an angle not between them.

Q: $A = 30^\circ$, $b = 10$, $a = 5$. How many triangles?
A: $h = 10\sin 30^\circ = 5 = a$, so exactly one (a right triangle).

Q: $A = 30^\circ$, $b = 10$, $a = 7$. How many triangles?
A: $h = 5 < 7 < 10$, so two.

Q: $A = 120^\circ$, $a = 6$, $b = 8$. How many triangles?
A: None: $A$ is obtuse and $a < b$.

Q: You get $B_1 = 48^\circ$ from $\sin^{-1}$. What is the other candidate?
A: $B_2 = 132^\circ$ (keep it only if $A + 132^\circ < 180^\circ$).

@@ 3 | TOPIC | Law of cosines | c² = a² + b² − 2ab cos C, solving SAS and SSS triangles | MUST
# Law of cosines

## 1. What it covers
- The law of cosines: $c^2 = a^2 + b^2 - 2ab\cos C$.
- Its link to Pythagoras.
- Solving triangles when you know two sides and the angle between them (SAS), or all three sides (SSS).

## 2. Where it is used
- **Navigation and aviation:** the distance between two ships or planes travelling in different directions.
- **Geometry in code:** the angle between two vectors; this law is the dot-product formula $a \cdot b = \lvert a\rvert\lvert b\rvert\cos\theta$ in disguise.
- **Robotics:** inverse kinematics of a two-link arm (shoulder–elbow–hand triangle).
- **Engineering:** the resultant of two forces that are not perpendicular.
- **Sports analytics:** distance between players from angles and positions.

## 3. What it means
The law of cosines is **Pythagoras with a correction term**.
- If $C = 90^\circ$: $\cos C = 0$, and the law becomes $c^2 = a^2 + b^2$.
- If $C$ is **smaller** than $90^\circ$: $\cos C > 0$, so we subtract: $c$ is shorter than the Pythagoras value (the sides close in).
- If $C$ is **larger** than $90^\circ$: $\cos C < 0$, so we add: $c$ is longer (the sides open out).

**Analogy:** a pair of scissors. With the blades at $90^\circ$, the gap between the tips is the Pythagoras distance. Close them and the gap shrinks; open them wide and it grows. The $-2ab\cos C$ term is exactly how much.

**Choose the right form:** the angle on the right always matches the side on the left.

## 4. Formula & worked example
$$
c^2 = a^2 + b^2 - 2ab\cos C \qquad a^2 = b^2 + c^2 - 2bc\cos A \qquad b^2 = a^2 + c^2 - 2ac\cos B
$$
To find an angle (SSS), rearrange:
$$
\cos C = \frac{a^2 + b^2 - c^2}{2ab}
$$

**Example 1 (SAS).** $a = 7$, $b = 10$, $C = 50^\circ$. Find $c$.
- $c^2 = 49 + 100 - 2(7)(10)\cos 50^\circ = 149 - 140(0.6428) \approx 149 - 89.99 = 59.01$
- $c \approx 7.68$

**Example 2 (SSS).** $a = 5$, $b = 7$, $c = 9$. Find the largest angle.
- The largest angle is opposite the longest side, $c$.
- $\cos C = \frac{25 + 49 - 81}{2(5)(7)} = \frac{-7}{70} = -0.1$ → $C \approx 95.7^\circ$ (obtuse)
- Then use the law of sines (or cosines again) for the others.

**Example 3: two ships.** Two ships leave port at the same time. One sails 30 km on a bearing of $040^\circ$, the other 45 km on a bearing of $160^\circ$. How far apart are they?
- The angle between their paths is $160^\circ - 40^\circ = 120^\circ$.
- $d^2 = 30^2 + 45^2 - 2(30)(45)\cos 120^\circ = 900 + 2025 + 1350 = 4275$
- $d \approx 65.4$ km

**Example 4: robot arm.** Upper arm 30 cm, forearm 25 cm. The hand must reach a point 40 cm from the shoulder. What is the elbow angle $E$?
- $\cos E = \frac{30^2 + 25^2 - 40^2}{2(30)(25)} = \frac{900 + 625 - 1600}{1500} = -0.05$ → $E \approx 92.9^\circ$

## 5. Common mistakes
- Calculating $(a^2 + b^2 - 2ab)\cos C$. The order of operations is $a^2 + b^2 - (2ab\cos C)$.
- Pairing the wrong angle with the side on the left. In $c^2 = \ldots\cos C$, $C$ must be opposite $c$.
- With SSS, finding a smaller angle first using the law of sines and then missing that the largest angle is obtuse. **Find the largest angle first with the law of cosines** (it handles obtuse angles correctly, since cosine is negative there).

## 6. Practice
Q: State the law of cosines for side $a$.
A: $a^2 = b^2 + c^2 - 2bc\cos A$.

Q: What does the law of cosines become when the angle is $90^\circ$?
A: Pythagoras' theorem: $c^2 = a^2 + b^2$.

Q: $a = 8$, $b = 5$, $C = 60^\circ$. Find $c$.
A: $c^2 = 64 + 25 - 40 = 49$, so $c = 7$.

Q: Sides 4, 5, 6. Find the angle opposite 6.
A: $\cos C = \frac{16 + 25 - 36}{40} = 0.125$, so $C \approx 82.8^\circ$.

Q: Which law do you use for SAS?
A: The law of cosines.

@@ 3 | TOPIC | Triangle area: ½ab sin C and Heron's formula | Area from two sides and the included angle, or from three sides | MUST
# Triangle area: ½ab sin C and Heron's formula

## 1. What it covers
- The area formula $\text{Area} = \frac{1}{2}ab\sin C$ (two sides and the angle between them).
- **Heron's formula**, which uses only the three sides.
- Choosing between them, and using area to find a missing angle or side.

## 2. Where it is used
- **Land surveying and property:** a plot's area from measured boundaries (any polygon can be split into triangles).
- **Computer graphics:** meshes are made of triangles; areas are used in shading, collision and mesh simplification. In code, the 2D cross product gives $2 \times$ the area of a triangle.
- **Construction:** the amount of material for triangular roof sections or sails.
- **GIS and maps:** the area of a region from its corner coordinates.

## 3. What it means
The basic area formula is $\frac{1}{2} \times \text{base} \times \text{height}$. But the height is often unknown. If you know sides $a$ and $b$ and the angle $C$ between them, the height from the end of $a$ down onto $b$ is $a\sin C$ (SOH-CAH-TOA). So:
$$
\text{Area} = \tfrac{1}{2} \cdot b \cdot (a\sin C) = \tfrac{1}{2}ab\sin C
$$

**Heron's formula** needs only the three sides, with no angles at all. It uses the **semi-perimeter** $s$ (half the perimeter).

**Analogy:** two sticks joined by a hinge. Opening the hinge to $90^\circ$ gives the most area ($\sin 90^\circ = 1$); closing it flat gives zero area ($\sin 0^\circ = 0$).

## 4. Formula & worked example
$$
\text{Area} = \tfrac{1}{2}ab\sin C = \tfrac{1}{2}bc\sin A = \tfrac{1}{2}ac\sin B
$$
$$
\text{Heron:} \quad s = \frac{a + b + c}{2}, \qquad \text{Area} = \sqrt{s(s - a)(s - b)(s - c)}
$$

**Example 1.** Two sides 8 and 11 with $40^\circ$ between them.
- Area $= \frac{1}{2}(8)(11)\sin 40^\circ \approx 44(0.6428) \approx 28.28$

**Example 2 (Heron).** Sides 13, 14, 15.
- $s = 21$; Area $= \sqrt{21 \cdot 8 \cdot 7 \cdot 6} = \sqrt{7056} = 84$

**Example 3: find an angle from area.** A triangle has sides 10 and 12 and area 30. Find the angle between those sides.
- $30 = \frac{1}{2}(10)(12)\sin C = 60\sin C$ → $\sin C = 0.5$ → $C = 30^\circ$ **or** $150^\circ$ (both give the same area).

**Example 4: a plot of land.** A four-sided plot $ABCD$ has $AB = 40$ m, $BC = 30$ m, angle $B = 90^\circ$, $CD = 35$ m and $DA = 45$ m. Find its area.
- Split along diagonal $AC$: $AC = \sqrt{40^2 + 30^2} = 50$.
- Triangle $ABC$: $\frac{1}{2}(40)(30) = 600$ m².
- Triangle $ACD$ (sides 50, 35, 45): $s = 65$, area $= \sqrt{65 \cdot 15 \cdot 30 \cdot 20} = \sqrt{585\,000} \approx 764.9$ m².
- Total $\approx 1364.9$ m².

**In code (cross product):** area of a triangle from its corner coordinates.
```python
def tri_area(ax, ay, bx, by, cx, cy):
    return abs((bx - ax) * (cy - ay) - (cx - ax) * (by - ay)) / 2
print(tri_area(0, 0, 4, 0, 0, 3))   # 6.0
```
The expression inside `abs` is the 2D cross product of vectors AB and AC, which equals $\lvert AB\rvert\lvert AC\rvert\sin A$. Halving it is exactly $\frac{1}{2}bc\sin A$. Graphics code uses its sign too: positive means the corners go anticlockwise.

## 5. Common mistakes
- Using an angle that is **not between** the two sides. The angle must be the one formed by those two sides.
- Forgetting the $\frac{1}{2}$.
- In Heron's formula, using the full perimeter instead of the semi-perimeter.

## 6. Practice
Q: Area of a triangle with sides 6 and 9 and an included angle of $30^\circ$?
A: $\frac{1}{2}(6)(9)(0.5) = 13.5$.

Q: Heron's formula?
A: $\sqrt{s(s-a)(s-b)(s-c)}$ with $s = \frac{a+b+c}{2}$.

Q: Area of a triangle with sides 5, 5, 6?
A: $s = 8$; $\sqrt{8 \cdot 3 \cdot 3 \cdot 2} = \sqrt{144} = 12$.

Q: Two sides 10 and 10. Which included angle gives the largest area?
A: $90^\circ$ (area 50), because $\sin C$ is largest at $90^\circ$.

Q: Area of an equilateral triangle with side 4?
A: $\frac{1}{2}(4)(4)\sin 60^\circ = 4\sqrt{3} \approx 6.93$.

@@ 3 | TOPIC | Applications: navigation, surveying and forces | Choosing the right law in multi-step real-world problems | MUST
# Applications: navigation, surveying and forces

## 1. What it covers
- Multi-step problems that combine bearings, the law of sines, the law of cosines and area.
- Adding two forces or velocities at an angle (the resultant).
- A checklist for turning a story into a triangle.

## 2. Where it is used
- **Aviation:** a plane's real path when wind blows across it (ground speed and drift angle).
- **Shipping and rescue:** finding a vessel's position from two shore stations.
- **Structural engineering:** forces in cables and beams that meet at angles.
- **Telecom:** locating a phone from signal angles at two or three towers.
- **Game physics:** combining velocity vectors.

## 3. What it means
Real problems hide the triangle in a story. The work is mostly **drawing it correctly**; the maths is just one of the two laws.

**Checklist:**
1. Draw a sketch. Mark north lines for bearings.
2. Find the triangle and label its three corners.
3. Use angle facts (parallel north lines, straight lines = $180^\circ$) to get the **interior** angles.
4. Decide which case it is (SAS, SSS, ASA, AAS, SSA) and pick the law.
5. Solve, then check that the answer makes sense (largest side opposite largest angle).

**Adding two forces:** put them "tip to tail". They form two sides of a triangle; the resultant is the third side. If the forces meet at angle $\theta$ (tail to tail), the angle **inside** the triangle is $180^\circ - \theta$.

**Analogy:** two people pulling a box with ropes at an angle. The box moves as if one stronger person pulled along the diagonal.

## 4. Formula & worked example
Resultant of forces $F_1$ and $F_2$ with angle $\theta$ between them (tail to tail):
$$
R^2 = F_1^2 + F_2^2 + 2F_1F_2\cos\theta
$$
(This is the law of cosines with $\cos(180^\circ - \theta) = -\cos\theta$.)

**Example 1: forces.** Two ropes pull a box with 60 N and 80 N, with $50^\circ$ between them. Find the resultant.
- $R^2 = 3600 + 6400 + 2(60)(80)\cos 50^\circ \approx 10\,000 + 6171 = 16\,171$ → $R \approx 127.2$ N
- Direction from the 60 N rope: $\frac{\sin\alpha}{80} = \frac{\sin 130^\circ}{127.2}$ → $\sin\alpha \approx 0.4818$ → $\alpha \approx 28.8^\circ$

**Example 2: wind.** A plane heads due north at 400 km/h. A wind blows from the west (towards the east) at 60 km/h. Find the ground speed and the actual track.
- The two velocities are at $90^\circ$, so: speed $= \sqrt{400^2 + 60^2} \approx 404.5$ km/h
- Drift: $\tan^{-1}\frac{60}{400} \approx 8.5^\circ$ → track $\approx 008.5^\circ$

**Example 3: two lookout stations.** Stations A and B are 10 km apart, with B due east of A. A fire is seen from A on a bearing of $050^\circ$ and from B on a bearing of $320^\circ$. How far is the fire from A?
- At A, the angle between AB (east, $090^\circ$) and AF ($050^\circ$) is $40^\circ$.
- At B, the direction to A is west ($270^\circ$); BF is $320^\circ$, so the angle is $50^\circ$.
- Angle at F $= 180^\circ - 40^\circ - 50^\circ = 90^\circ$.
- $AF = 10\sin 50^\circ \approx 7.66$ km (it happens to be a right triangle here; in general use the law of sines: $AF = \frac{10\sin 50^\circ}{\sin 90^\circ}$).

**Example 4: two ships.** From port, ship P sails 20 km on $070^\circ$ and ship Q sails 35 km on $150^\circ$. Find the distance PQ and the bearing of Q from P.
- Angle at port $= 150^\circ - 70^\circ = 80^\circ$.
- $PQ^2 = 400 + 1225 - 2(20)(35)\cos 80^\circ \approx 1625 - 243.1 = 1381.9$ → $PQ \approx 37.17$ km
- Angle at P: $\frac{\sin P}{35} = \frac{\sin 80^\circ}{37.17}$ → $\sin P \approx 0.9273$ → $P \approx 68.0^\circ$. Take the acute value: PQ is the longest side, so the largest angle is the $80^\circ$ at the port, and P must be smaller than that.
- At P, the back bearing to port is $070^\circ + 180^\circ = 250^\circ$. Q lies $68.0^\circ$ anticlockwise from that direction (towards the south): bearing $\approx 250^\circ - 68.0^\circ = 182.0^\circ$.

## 5. Common mistakes
- Using a bearing directly as an interior angle. Work out the angle **inside** the triangle first.
- For forces, using the angle between the forces in the law of cosines without changing it to $180^\circ - \theta$ (or using the $+2F_1F_2\cos\theta$ form).
- Not drawing a sketch. Nearly every error in these problems starts with a missing or wrong sketch.

## 6. Practice
Q: Two forces of 5 N and 12 N act at $90^\circ$. Resultant?
A: $13$ N.

Q: Two forces of 10 N each act at $60^\circ$ to each other. Resultant?
A: $R^2 = 100 + 100 + 200(0.5) = 300$, so $R = 10\sqrt{3} \approx 17.3$ N.

Q: Two ships leave port with $100^\circ$ between their courses, after travelling 8 km and 15 km. Distance apart?
A: $\sqrt{64 + 225 - 240\cos 100^\circ} \approx \sqrt{330.7} \approx 18.2$ km.

Q: First step in any applied trigonometry problem?
A: Draw and label a sketch, then find the interior angles.

Q: In SAS, which law gives the third side?
A: The law of cosines.

@@ 3 | TOPIC | Practice & review: Non-right triangles | Mixed problem set with worked answers | MUST
# Practice & review: Non-right triangles

**How to use this:** 50 minutes with a calculator. For each problem, first write which case it is (AAS, ASA, SSA, SAS or SSS) and which law you'll use.

## Problems
1. $A = 42^\circ$, $C = 71^\circ$, $c = 15$. Find $a$.
2. $a = 9$, $b = 14$, $C = 105^\circ$. Find $c$.
3. $a = 6$, $b = 8$, $c = 11$. Find all three angles.
4. $A = 28^\circ$, $a = 9$, $b = 15$. How many triangles? Solve them.
5. Area of a triangle with $a = 12$, $b = 9$, $C = 65^\circ$.
6. A triangular field has sides 120 m, 150 m and 200 m. Find its area.
7. Two forces of 30 N and 45 N act at $70^\circ$ to each other. Find the resultant.
8. From a point on level ground, the angle of elevation to the top of a hill is $25^\circ$. After walking 300 m directly towards it, the angle is $40^\circ$. Find the hill's height.

## Answers
1. AAS: $a = \frac{15\sin 42^\circ}{\sin 71^\circ} \approx 10.62$.
2. SAS: $c^2 = 81 + 196 - 252\cos 105^\circ \approx 277 + 65.22 = 342.2$, so $c \approx 18.50$.
3. SSS: $\cos C = \frac{36 + 64 - 121}{96} \approx -0.2188$ → $C \approx 102.6^\circ$; $\sin A = \frac{6\sin C}{11} \approx 0.5323$ → $A \approx 32.2^\circ$; $B \approx 45.2^\circ$.
4. SSA: $h = 15\sin 28^\circ \approx 7.04 < 9 < 15$ → two triangles. $\sin B = \frac{15\sin 28^\circ}{9} \approx 0.7825$ → $B_1 \approx 51.5^\circ$, $C_1 \approx 100.5^\circ$, $c_1 = \frac{9\sin 100.5^\circ}{\sin 28^\circ} \approx 18.85$. $B_2 \approx 128.5^\circ$, $C_2 \approx 23.5^\circ$, $c_2 = \frac{9\sin 23.5^\circ}{\sin 28^\circ} \approx 7.64$.
5. $\frac{1}{2}(12)(9)\sin 65^\circ \approx 48.94$.
6. $s = 235$; area $= \sqrt{235 \cdot 115 \cdot 85 \cdot 35} \approx \sqrt{80\,399\,375} \approx 8966.6$ m².
7. $R^2 = 900 + 2025 + 2700\cos 70^\circ \approx 2925 + 923.4 = 3848.4$ → $R \approx 62.0$ N.
8. In the triangle (first point, second point, hilltop): the angle at the second point is $180^\circ - 40^\circ = 140^\circ$, at the top $40^\circ - 25^\circ = 15^\circ$. The distance from the second point to the top $= \frac{300\sin 25^\circ}{\sin 15^\circ} \approx 489.9$ m. Height $= 489.9\sin 40^\circ \approx 314.9$ m.

## Flashcards
Q: Which law for SSS and SAS?
A: Law of cosines.

Q: Which law for AAS and ASA?
A: Law of sines.

Q: Area of a triangle from two sides and the included angle?
A: $\frac{1}{2}ab\sin C$.

Q: When can SSA give two triangles?
A: When $A$ is acute and $b\sin A < a < b$.
