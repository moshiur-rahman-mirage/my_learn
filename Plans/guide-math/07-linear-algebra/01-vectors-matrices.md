@@ 1 | MODULE | Phase 1 — Vectors (Foundation) | Vectors in Rⁿ, addition and scaling, linear combinations, dot product, projections, cross product | MUST
# Phase 1: Vectors

**Linear algebra** is the mathematics of vectors and matrices: of many numbers handled at once. It's arguably the most useful branch of maths for programmers: computer graphics, machine learning (every neural network is matrix multiplication), search engines, recommendation systems, image processing, robotics and data compression all run on it.

This roadmap pairs each idea with **NumPy** code, so you see both the maths and how it's computed.

**What you need first:** the Algebra & Functions roadmap (especially systems of equations) and basic trigonometry.

Each topic has six parts: what it covers, where it is used, what it means, formulas with worked examples, common mistakes, and practice flashcards.

@@ 2 | MODULE | Vectors and the dot product | Vectors, operations, linear combinations, dot product, projection, cross product | MUST
A vector is an arrow, a list of numbers, and a point, all at once.

@@ 3 | TOPIC | Vectors in Rⁿ: components, magnitude and direction | Geometric and list views, notation, length (norm), unit vectors, vectors in higher dimensions | MUST
# Vectors in Rⁿ: components, magnitude and direction

## 1. What it covers
- A **vector** as an arrow (magnitude + direction) and as a list of numbers.
- Notation: $\vec{v} = \langle 3, 4\rangle$ or a column $\begin{pmatrix}3\\4\end{pmatrix}$; vectors in $\mathbb{R}^n$.
- **Magnitude (length, norm)** $\lVert\vec{v}\rVert$.
- **Unit vectors** and normalising.

## 2. Where it is used
- **Games and physics:** positions, velocities, forces and directions are vectors.
- **Machine learning:** every data point is a vector of features (a house = [area, rooms, age, ...]); word embeddings represent words as vectors with hundreds of dimensions.
- **Graphics:** surface normals (unit vectors) decide lighting.
- **Search and recommendations:** documents and users as vectors.

## 3. What it means
**Geometric view:** an arrow with a length and a direction. $\langle 3, 4\rangle$ means "3 right, 4 up". Arrows with the same length and direction are the same vector, wherever they're drawn.

**List view:** a vector is an ordered list of $n$ numbers, a point in $n$-dimensional space $\mathbb{R}^n$. We can't picture 300 dimensions, but the arithmetic works exactly the same as in 2D.

**Magnitude:** by Pythagoras, $\lVert\langle 3, 4\rangle\rVert = 5$. In $n$ dimensions: the square root of the sum of squares.

**Unit vector:** length 1; it represents a pure direction. **Normalise** by dividing a vector by its length.

## 4. Formula & worked example
$$
\lVert\vec{v}\rVert = \sqrt{v_1^2 + v_2^2 + \cdots + v_n^2} \qquad \hat{v} = \frac{\vec{v}}{\lVert\vec{v}\rVert}
$$

**Example 1.** $\lVert\langle 6, -8\rangle\rVert = \sqrt{36 + 64} = 10$; unit vector $\langle 0.6, -0.8\rangle$.

**Example 2 (3D).** $\lVert\langle 2, 3, 6\rangle\rVert = \sqrt{4 + 9 + 36} = 7$.

**Example 3 (from two points).** The vector from $A(1, 2)$ to $B(4, 6)$ is $\langle 3, 4\rangle$; distance 5.

**Example 4 (game).** An enemy at $(10, 2)$ moves towards a player at $(4, 10)$ at speed 3 units/frame.
- Direction $\langle -6, 8\rangle$, length 10, unit $\langle -0.6, 0.8\rangle$ → velocity $\langle -1.8, 2.4\rangle$ per frame.

**Example 5 (code).**
```python
import numpy as np
v = np.array([6, -8])
print(np.linalg.norm(v), v / np.linalg.norm(v))   # 10.0 [ 0.6 -0.8]
house = np.array([1450, 3, 12])                    # area, rooms, age: a point in R^3
```
`np.linalg.norm` computes the length; dividing by it gives the unit vector. Any row of a data table is a vector like `house`.

## 5. Common mistakes
- Forgetting to square negative components before adding.
- Normalising the zero vector (it has no direction; division by zero).
- Confusing a point's coordinates with a vector between two points (subtract end − start).

## 6. Practice
Q: Magnitude of $\langle 5, 12\rangle$?
A: $13$.

Q: Unit vector in the direction of $\langle 0, -7\rangle$?
A: $\langle 0, -1\rangle$.

Q: Vector from $(2, -1)$ to $(-1, 3)$?
A: $\langle -3, 4\rangle$.

Q: Magnitude of $\langle 1, 1, 1, 1\rangle$?
A: $2$.

Q: Why normalise a direction vector in a game?
A: So that speed is controlled separately (length 1), and diagonal motion isn't faster.

@@ 3 | TOPIC | Vector addition, scalar multiplication and linear combinations | Tip-to-tail addition, scaling, subtraction, linear combinations, forces and velocities | MUST
# Vector addition, scalar multiplication and linear combinations

## 1. What it covers
- **Adding** vectors (tip to tail, or component by component).
- **Scalar multiplication** (stretching, shrinking, reversing).
- Subtraction and the vector between two points.
- **Linear combinations** $a\vec{u} + b\vec{v}$.
- Applications: resultant forces, velocities with wind or current.

## 2. Where it is used
- **Physics:** combining forces; a plane's ground velocity = air velocity + wind.
- **Games:** position += velocity × dt every frame.
- **Graphics:** interpolating between points ($(1 - t)\vec{a} + t\vec{b}$).
- **ML:** word-vector arithmetic: vector("king") − vector("man") + vector("woman") ≈ vector("queen").
- **Colour mixing:** RGB colours are vectors; blending is a linear combination.

## 3. What it means
- **Addition:** place the second arrow's tail at the first arrow's tip; the sum goes from the start to the end. In components: add matching entries.
- **Scalar multiplication:** $2\vec{v}$ is twice as long, same direction; $-\vec{v}$ points the opposite way; $0.5\vec{v}$ is half as long.
- **Subtraction:** $\vec{b} - \vec{a}$ is the arrow from the tip of $\vec{a}$ to the tip of $\vec{b}$.
- **Linear combination:** scale some vectors and add them. This single idea underlies almost all of linear algebra: every vector in the plane is a combination of $\langle 1, 0\rangle$ and $\langle 0, 1\rangle$.

## 4. Formula & worked example
$$
\vec{u} + \vec{v} = \langle u_1 + v_1, u_2 + v_2\rangle \qquad c\vec{v} = \langle cv_1, cv_2\rangle \qquad a\vec{u} + b\vec{v}
$$

**Example 1.** $\langle 2, 5\rangle + \langle -3, 1\rangle = \langle -1, 6\rangle$; $3\langle 2, -1\rangle = \langle 6, -3\rangle$.

**Example 2 (combination).** $2\langle 1, 3\rangle - \langle 4, -2\rangle = \langle -2, 8\rangle$.

**Example 3 (write as a combination).** Express $\langle 7, 4\rangle$ as $a\langle 1, 1\rangle + b\langle 2, -1\rangle$.
- $a + 2b = 7$, $a - b = 4$ → $b = 1$, $a = 5$.

**Example 4 (plane and wind).** A plane flies at $\langle 0, 400\rangle$ km/h (north); wind blows $\langle 60, 0\rangle$ (east). Ground velocity $\langle 60, 400\rangle$; ground speed $\sqrt{3600 + 160\,000} \approx 404.5$ km/h.

**Example 5 (forces).** Forces $\langle 30, 0\rangle$ N and $\langle 0, 40\rangle$ N → resultant $\langle 30, 40\rangle$, magnitude 50 N.

**Example 6 (code).**
```python
import numpy as np
u, v = np.array([1, 3]), np.array([4, -2])
print(2*u - v)                               # [-2  8]
a, b = np.array([0, 0]), np.array([10, 4])
for t in (0, 0.5, 1):
    print((1 - t) * a + t * b)               # interpolate from a to b
```
NumPy adds and scales arrays component by component, so vector arithmetic looks just like the maths. The loop is the "lerp" linear combination used in animation.

## 5. Common mistakes
- Adding magnitudes instead of vectors ($\lVert\vec{u} + \vec{v}\rVert \ne \lVert\vec{u}\rVert + \lVert\vec{v}\rVert$ unless they point the same way).
- Subtracting in the wrong order for "from A to B".
- Adding vectors of different dimensions.

## 6. Practice
Q: $\langle 3, -2\rangle + \langle -1, 5\rangle$?
A: $\langle 2, 3\rangle$.

Q: $-2\langle 4, 1\rangle$?
A: $\langle -8, -2\rangle$.

Q: Write $\langle 5, 5\rangle$ as $a\langle 1, 0\rangle + b\langle 1, 1\rangle$.
A: $b = 5$, $a = 0$.

Q: A boat goes $\langle 0, 8\rangle$ km/h in a current of $\langle 3, 0\rangle$. Speed over ground?
A: $\sqrt{73} \approx 8.54$ km/h.

Q: What is a linear combination?
A: A sum of scalar multiples of vectors, $a\vec{u} + b\vec{v} + \cdots$.

@@ 3 | TOPIC | The dot product, angles and cosine similarity | Algebraic and geometric definitions, angle between vectors, orthogonality, cosine similarity | MUST
# The dot product, angles and cosine similarity

## 1. What it covers
- The **dot product** $\vec{u} \cdot \vec{v}$, algebraically and geometrically.
- Finding the **angle** between vectors.
- **Orthogonal** (perpendicular) vectors: dot product 0.
- **Cosine similarity**.

## 2. Where it is used
- **Search and NLP:** cosine similarity measures how similar two documents or embeddings are (semantic search, recommendations, vector databases).
- **Graphics:** lighting brightness is $\vec{n} \cdot \vec{l}$ (surface normal · light direction); back-face culling checks the sign of a dot product.
- **Physics:** work $= \vec{F} \cdot \vec{d}$.
- **ML:** a neuron computes a dot product of weights and inputs; matrix multiplication is many dot products.

## 3. What it means
**Algebraically:** multiply matching components and add.

**Geometrically:** $\vec{u} \cdot \vec{v} = \lVert\vec{u}\rVert\lVert\vec{v}\rVert\cos\theta$. It measures how much two vectors point the **same way**:
- positive → angle less than 90° (roughly the same direction);
- zero → perpendicular;
- negative → angle greater than 90° (opposing).

**Cosine similarity** $= \cos\theta = \frac{\vec{u} \cdot \vec{v}}{\lVert\vec{u}\rVert\lVert\vec{v}\rVert}$: 1 means the same direction, 0 unrelated, $-1$ opposite. It ignores length, so a long and a short document about the same topic still score as similar.

## 4. Formula & worked example
$$
\vec{u} \cdot \vec{v} = \sum u_iv_i = \lVert\vec{u}\rVert\lVert\vec{v}\rVert\cos\theta \qquad \cos\theta = \frac{\vec{u} \cdot \vec{v}}{\lVert\vec{u}\rVert\lVert\vec{v}\rVert}
$$

**Example 1.** $\langle 2, 3\rangle \cdot \langle 4, -1\rangle = 8 - 3 = 5$.

**Example 2 (angle).** Angle between $\langle 1, 0\rangle$ and $\langle 1, 1\rangle$: $\cos\theta = \frac{1}{1 \cdot \sqrt{2}}$ → $45^\circ$.

**Example 3 (perpendicular?).** $\langle 3, -2, 1\rangle \cdot \langle 2, 4, 2\rangle = 6 - 8 + 2 = 0$ → orthogonal.

**Example 4 (work).** A force $\langle 10, 5\rangle$ N moves a box by $\langle 4, 0\rangle$ m. Work $= 40$ J.

**Example 5 (cosine similarity).** Word counts for three documents over the words [python, code, football]:
- $A = \langle 5, 3, 0\rangle$, $B = \langle 10, 7, 1\rangle$, $C = \langle 0, 1, 8\rangle$
- $\cos(A, B) = \frac{71}{\sqrt{34}\sqrt{150}} \approx 0.994$ (very similar); $\cos(A, C) = \frac{3}{\sqrt{34}\sqrt{65}} \approx 0.064$ (unrelated).

**Example 6 (code).**
```python
import numpy as np
A, B, C = np.array([5, 3, 0]), np.array([10, 7, 1]), np.array([0, 1, 8])
cos = lambda u, v: u @ v / (np.linalg.norm(u) * np.linalg.norm(v))
print(round(cos(A, B), 3), round(cos(A, C), 3))   # 0.994 0.064
```
`u @ v` is the dot product in NumPy. Dividing by both lengths gives cosine similarity, the core operation in semantic search and vector databases.

## 5. Common mistakes
- Thinking the dot product is a vector (it's a single number).
- Forgetting to divide by both magnitudes for the angle.
- Using degrees vs radians inconsistently with `arccos`.

## 6. Practice
Q: $\langle 1, 2, 3\rangle \cdot \langle 4, 5, 6\rangle$?
A: $32$.

Q: What does a dot product of 0 mean?
A: The vectors are perpendicular (orthogonal).

Q: Angle between $\langle 1, 0\rangle$ and $\langle -1, 0\rangle$?
A: $180^\circ$ (cosine $-1$).

Q: Find $k$ so $\langle 2, k\rangle$ is perpendicular to $\langle 3, -6\rangle$.
A: $6 - 6k = 0$ → $k = 1$.

Q: Why does cosine similarity ignore vector length?
A: It divides by both magnitudes, leaving only the angle.

@@ 3 | TOPIC | Projections | Scalar and vector projection, decomposing a vector into parallel and perpendicular parts | MUST
# Projections

## 1. What it covers
- The **scalar projection** (component) of $\vec{v}$ onto $\vec{u}$.
- The **vector projection** $\text{proj}_{\vec{u}}\vec{v}$.
- Splitting a vector into parts **parallel** and **perpendicular** to another.
- Distance from a point to a line.

## 2. Where it is used
- **Physics:** the component of gravity along a slope; the force that actually moves an object.
- **Games:** sliding along walls (remove the velocity component into the wall); shadows.
- **Least squares and regression:** fitting a line is projecting the data vector onto a subspace (Phase 6).
- **PCA:** projecting data onto the most important directions.

## 3. What it means
Shine a light straight down onto the line of $\vec{u}$: the shadow that $\vec{v}$ casts is its **projection**. It's the part of $\vec{v}$ that points along $\vec{u}$.

- **Scalar projection:** the signed length of the shadow, $\frac{\vec{v} \cdot \vec{u}}{\lVert\vec{u}\rVert}$.
- **Vector projection:** that length times the unit vector of $\vec{u}$.
- The leftover $\vec{v} - \text{proj}_{\vec{u}}\vec{v}$ is **perpendicular** to $\vec{u}$.

## 4. Formula & worked example
$$
\text{comp}_{\vec{u}}\vec{v} = \frac{\vec{v} \cdot \vec{u}}{\lVert\vec{u}\rVert} \qquad \text{proj}_{\vec{u}}\vec{v} = \frac{\vec{v} \cdot \vec{u}}{\vec{u} \cdot \vec{u}}\,\vec{u}
$$

**Example 1.** Project $\vec{v} = \langle 4, 3\rangle$ onto $\vec{u} = \langle 1, 0\rangle$: $\langle 4, 0\rangle$ (the x-part).

**Example 2.** Project $\vec{v} = \langle 3, 4\rangle$ onto $\vec{u} = \langle 1, 1\rangle$.
- $\frac{7}{2}\langle 1, 1\rangle = \langle 3.5, 3.5\rangle$; perpendicular part $\langle -0.5, 0.5\rangle$ (check: $\langle -0.5, 0.5\rangle \cdot \langle 1, 1\rangle = 0$ ✓)

**Example 3 (slope).** A 10 kg box on a ramp at 30°; gravity 98 N downward. Component along the ramp $= 98\sin 30^\circ = 49$ N.

**Example 4 (wall sliding, code).**
```python
import numpy as np
vel = np.array([5.0, -3.0])          # moving right and down
n = np.array([0.0, 1.0])             # floor normal (unit vector, pointing up)
vel_slide = vel - (vel @ n) * n      # remove the part going into the floor
print(vel_slide)                     # [5. 0.]  -> slides along the floor
```
`(vel @ n) * n` is the projection of the velocity onto the normal (the part pushing into the floor). Subtracting it leaves only the motion along the surface, which is how games make characters slide along walls instead of stopping dead.

**Example 5 (distance to a line).** Distance from $P(3, 4)$ to the line through the origin with direction $\langle 1, 1\rangle$: the perpendicular part from Example 2 has length $\sqrt{0.5} \approx 0.707$.

## 5. Common mistakes
- Dividing by $\lVert\vec{u}\rVert$ instead of $\lVert\vec{u}\rVert^2$ for the vector projection.
- Projecting $\vec{u}$ onto $\vec{v}$ when asked for the reverse.
- Forgetting that the scalar projection can be negative.

## 6. Practice
Q: Vector projection of $\langle 2, 6\rangle$ onto $\langle 0, 1\rangle$?
A: $\langle 0, 6\rangle$.

Q: Projection of $\langle 1, 2\rangle$ onto $\langle 3, 4\rangle$?
A: $\frac{11}{25}\langle 3, 4\rangle = \langle 1.32, 1.76\rangle$.

Q: What can you say about $\vec{v} - \text{proj}_{\vec{u}}\vec{v}$?
A: It's perpendicular to $\vec{u}$.

Q: Scalar projection of $\langle 4, 3\rangle$ onto $\langle 0, -1\rangle$?
A: $-3$.

Q: How do games make a character slide along a wall?
A: Subtract the velocity's projection onto the wall's normal.

@@ 3 | TOPIC | The cross product | 3D cross product, right-hand rule, area of parallelograms, normal vectors | SHOULD
# The cross product

## 1. What it covers
- The **cross product** $\vec{u} \times \vec{v}$ of two 3D vectors.
- The **right-hand rule** for its direction.
- Its magnitude = area of the parallelogram.
- Finding **normal vectors** to planes and triangles.

## 2. Where it is used
- **3D graphics:** computing surface normals of triangles (needed for lighting and back-face culling).
- **Physics:** torque $\vec{\tau} = \vec{r} \times \vec{F}$, angular momentum, magnetic force $q\vec{v} \times \vec{B}$.
- **Robotics and drones:** rotational motion.
- **Computational geometry:** orientation tests (is a point left or right of a line?).

## 3. What it means
The cross product of two 3D vectors is a **new vector**:
- **perpendicular** to both;
- with length $\lVert\vec{u}\rVert\lVert\vec{v}\rVert\sin\theta$, the area of the parallelogram they span;
- pointing in the direction given by the **right-hand rule**: curl the fingers of your right hand from $\vec{u}$ towards $\vec{v}$; your thumb points along $\vec{u} \times \vec{v}$.

Order matters: $\vec{v} \times \vec{u} = -(\vec{u} \times \vec{v})$. Parallel vectors have cross product $\vec{0}$.

## 4. Formula & worked example
$$
\vec{u} \times \vec{v} = \langle u_2v_3 - u_3v_2,\ u_3v_1 - u_1v_3,\ u_1v_2 - u_2v_1\rangle \qquad \lVert\vec{u} \times \vec{v}\rVert = \lVert\vec{u}\rVert\lVert\vec{v}\rVert\sin\theta
$$

**Example 1.** $\langle 1, 0, 0\rangle \times \langle 0, 1, 0\rangle = \langle 0, 0, 1\rangle$ ($\hat{i} \times \hat{j} = \hat{k}$).

**Example 2.** $\langle 2, 1, -1\rangle \times \langle 1, 3, 2\rangle = \langle 1 \cdot 2 - (-1)3,\ (-1)1 - 2 \cdot 2,\ 2 \cdot 3 - 1 \cdot 1\rangle = \langle 5, -5, 5\rangle$.
- Check: $\langle 5, -5, 5\rangle \cdot \langle 2, 1, -1\rangle = 10 - 5 - 5 = 0$ ✓

**Example 3 (triangle area).** Triangle $A(0,0,0)$, $B(3,0,0)$, $C(0,4,0)$: $\vec{AB} \times \vec{AC} = \langle 0, 0, 12\rangle$ → area $= \frac{12}{2} = 6$.

**Example 4 (normal, code).**
```python
import numpy as np
A, B, C = np.array([0, 0, 0]), np.array([1, 0, 0]), np.array([0, 1, 1])
n = np.cross(B - A, C - A)
print(n, n / np.linalg.norm(n))     # [ 0 -1  1]  unit normal of the triangle
```
A 3D mesh stores triangles as three vertices. The cross product of two edge vectors gives the triangle's normal, which the renderer uses for lighting.

**Example 5 (torque).** A 0.3 m wrench, force 50 N perpendicular to it: torque magnitude $= 0.3 \times 50 = 15$ N·m.

## 5. Common mistakes
- Swapping the order (the result flips direction).
- Sign error in the middle component.
- Trying to take a cross product of 2D vectors (it's defined in 3D; for 2D, use the scalar $u_1v_2 - u_2v_1$).

## 6. Practice
Q: $\hat{j} \times \hat{i}$?
A: $-\hat{k}$.

Q: $\langle 1, 2, 3\rangle \times \langle 1, 2, 3\rangle$?
A: $\vec{0}$ (parallel vectors).

Q: What does $\lVert\vec{u} \times \vec{v}\rVert$ represent?
A: The area of the parallelogram spanned by $\vec{u}$ and $\vec{v}$.

Q: $\langle 1, 0, 0\rangle \times \langle 0, 0, 1\rangle$?
A: $\langle 0, -1, 0\rangle$.

Q: Why do 3D renderers use cross products?
A: To compute triangle normals for lighting and culling.

@@ 3 | TOPIC | Practice & review: Vectors | Mixed problem set with worked answers | MUST
# Practice & review: Vectors

**How to use this:** 40 minutes; check answers with NumPy afterwards.

## Problems
1. $\vec{u} = \langle 2, -1, 3\rangle$, $\vec{v} = \langle 1, 4, -2\rangle$. Find $3\vec{u} - 2\vec{v}$ and $\lVert\vec{u}\rVert$.
2. Find the unit vector in the direction from $(1, 5)$ to $(7, -3)$.
3. Write $\langle 1, 9\rangle$ as a combination of $\langle 1, 2\rangle$ and $\langle -1, 1\rangle$.
4. Angle between $\langle 1, 2, 2\rangle$ and $\langle 2, -1, 2\rangle$.
5. Cosine similarity of $\langle 1, 0, 1\rangle$ and $\langle 1, 1, 0\rangle$.
6. Project $\langle 5, 1\rangle$ onto $\langle 2, 1\rangle$ and find the perpendicular part.
7. Compute $\langle 1, 2, 0\rangle \times \langle 0, 1, 3\rangle$ and the area of the parallelogram they span.
8. A force of $\langle 20, 15\rangle$ N pulls a cart along $\langle 5, 0\rangle$ m. Work done?

## Answers
1. $\langle 4, -11, 13\rangle$; $\sqrt{14}$.
2. Vector $\langle 6, -8\rangle$ → $\langle 0.6, -0.8\rangle$.
3. $a - b = 1$, $2a + b = 9$ → $a = \frac{10}{3}$, $b = \frac{7}{3}$.
4. Dot $= 2 - 2 + 4 = 4$; norms 3 and 3 → $\cos\theta = \frac{4}{9}$ → $\theta \approx 63.6^\circ$.
5. $\frac{1}{\sqrt{2}\sqrt{2}} = 0.5$.
6. $\frac{11}{5}\langle 2, 1\rangle = \langle 4.4, 2.2\rangle$; perpendicular $\langle 0.6, -1.2\rangle$.
7. $\langle 6, -3, 1\rangle$; area $\sqrt{46} \approx 6.78$.
8. $100$ J.

## Flashcards
Q: Dot product formula?
A: $\sum u_iv_i = \lVert\vec{u}\rVert\lVert\vec{v}\rVert\cos\theta$.

Q: Vector projection formula?
A: $\frac{\vec{v} \cdot \vec{u}}{\vec{u} \cdot \vec{u}}\vec{u}$.

Q: Cross product direction?
A: Perpendicular to both vectors, by the right-hand rule.

Q: How do you normalise a vector?
A: Divide it by its length.

@@ 1 | MODULE | Phase 2 — Matrices and Linear Systems (Core) | Matrix operations, matrix multiplication, Gaussian elimination, solution sets, inverses, determinants, transpose, NumPy | MUST
# Phase 2: Matrices and linear systems

A **matrix** is a rectangular table of numbers. It can store data (rows = examples, columns = features), represent a system of equations, or represent a **transformation** that moves vectors around. Solving systems of linear equations efficiently is one of the most important computations in science and engineering.

@@ 2 | MODULE | Matrix algebra and systems | Operations, multiplication, row reduction, solution sets, inverses, determinants, transpose | MUST
Matrices let you handle hundreds of equations and millions of numbers with one compact notation.

@@ 3 | TOPIC | Matrix basics and operations | Notation, dimensions, entries, addition, scalar multiplication, special matrices | MUST
# Matrix basics and operations

## 1. What it covers
- Matrix notation: size $m \times n$ ($m$ rows, $n$ columns), entry $a_{ij}$.
- **Addition** and **scalar multiplication**.
- Special matrices: zero, **identity** $I$, diagonal, square, row and column vectors.

## 2. Where it is used
- **Data science:** a dataset is a matrix (pandas DataFrames, NumPy arrays): rows are samples, columns are features.
- **Images:** a greyscale image is a matrix of pixel brightnesses; a colour image is three matrices.
- **Spreadsheets** are matrices.
- **Graphs and networks:** adjacency matrices store who connects to whom.

## 3. What it means
A matrix is a grid of numbers. Its **size** is rows × columns. $a_{ij}$ is the entry in row $i$, column $j$.

- **Addition:** only for matrices of the same size; add matching entries.
- **Scalar multiplication:** multiply every entry.
- **Identity matrix** $I$: 1s on the diagonal, 0s elsewhere. It acts like the number 1 ($AI = A$).

## 4. Formula & worked example
$$
A = \begin{pmatrix} 1 & 2 & 3 \\ 4 & 5 & 6\end{pmatrix}\ (2 \times 3) \qquad I_3 = \begin{pmatrix} 1 & 0 & 0 \\ 0 & 1 & 0 \\ 0 & 0 & 1\end{pmatrix}
$$

**Example 1.** Size of $A$ above: $2 \times 3$; $a_{23} = 6$.

**Example 2.** $\begin{pmatrix} 1 & 2 \\ 3 & 4\end{pmatrix} + \begin{pmatrix} 5 & -1 \\ 0 & 2\end{pmatrix} = \begin{pmatrix} 6 & 1 \\ 3 & 6\end{pmatrix}$

**Example 3.** $3\begin{pmatrix} 2 & -1 \\ 0 & 4\end{pmatrix} = \begin{pmatrix} 6 & -3 \\ 0 & 12\end{pmatrix}$

**Example 4 (sales data).** Units sold of 3 products in 2 shops in January ($J$) and February ($F$): total over both months is $J + F$; a 10% increase forecast is $1.1F$.

**Example 5 (code).**
```python
import numpy as np
A = np.array([[1, 2, 3], [4, 5, 6]])
print(A.shape, A[1, 2])          # (2, 3) 6   (NumPy indexes from 0)
print(A + 10, 2 * A)             # element-wise
print(np.eye(3))                 # 3x3 identity
img = np.random.randint(0, 256, (480, 640))   # a greyscale image is a 480x640 matrix
```
NumPy's `shape` gives (rows, columns). Note that `A[1, 2]` is the mathematical $a_{23}$ because Python counts from 0.

## 5. Common mistakes
- Writing the size as columns × rows.
- Adding matrices of different sizes.
- Off-by-one indexing between maths ($a_{11}$ is first) and code (`A[0, 0]`).

## 6. Practice
Q: Size of a matrix with 4 rows and 2 columns?
A: $4 \times 2$.

Q: What is the identity matrix?
A: A square matrix with 1s on the diagonal and 0s elsewhere.

Q: $\begin{pmatrix} 1 & 0 \\ 2 & 3\end{pmatrix} - \begin{pmatrix} 0 & 1 \\ 1 & 1\end{pmatrix}$?
A: $\begin{pmatrix} 1 & -1 \\ 1 & 2\end{pmatrix}$.

Q: Can you add a $2 \times 3$ and a $3 \times 2$ matrix?
A: No.

Q: In a dataset matrix, what do rows and columns usually represent?
A: Rows are samples (examples); columns are features (variables).

@@ 3 | TOPIC | Matrix multiplication | Row-by-column rule, dimension compatibility, matrix-vector products, non-commutativity | MUST
# Matrix multiplication

## 1. What it covers
- The **row-by-column** rule.
- When a product is defined: $(m \times n)(n \times p) = m \times p$.
- **Matrix–vector** products $A\vec{x}$ as combinations of columns.
- Properties: associative, distributive, but **not commutative**.

## 2. Where it is used
- **Neural networks:** each layer computes $W\vec{x} + \vec{b}$; GPUs are built to multiply matrices fast.
- **Graphics:** transforming every vertex of a 3D model by a matrix.
- **Business:** total cost = quantity matrix × price vector.
- **Markov chains and graphs:** powers of a matrix count paths and evolve probabilities.

## 3. What it means
Entry $(i, j)$ of $AB$ is the **dot product** of row $i$ of $A$ with column $j$ of $B$. So the number of columns of $A$ must equal the number of rows of $B$.

**Matrix–vector product:** $A\vec{x}$ is a **linear combination of the columns of $A$**, with weights from $\vec{x}$. This view explains most of linear algebra.

**Order matters:** in general $AB \ne BA$ (rotating then scaling differs from scaling then rotating; and $BA$ may not even be defined).

## 4. Formula & worked example
$$
(AB)_{ij} = \sum_k a_{ik}b_{kj} \qquad A\vec{x} = x_1\vec{a}_1 + x_2\vec{a}_2 + \cdots + x_n\vec{a}_n
$$

**Example 1.** $\begin{pmatrix} 1 & 2 \\ 3 & 4\end{pmatrix}\begin{pmatrix} 5 & 6 \\ 7 & 8\end{pmatrix} = \begin{pmatrix} 1 \cdot 5 + 2 \cdot 7 & 1 \cdot 6 + 2 \cdot 8 \\ 3 \cdot 5 + 4 \cdot 7 & 3 \cdot 6 + 4 \cdot 8\end{pmatrix} = \begin{pmatrix} 19 & 22 \\ 43 & 50\end{pmatrix}$

**Example 2 (not commutative).** $\begin{pmatrix} 5 & 6 \\ 7 & 8\end{pmatrix}\begin{pmatrix} 1 & 2 \\ 3 & 4\end{pmatrix} = \begin{pmatrix} 23 & 34 \\ 31 & 46\end{pmatrix} \ne$ Example 1.

**Example 3 (columns view).** $\begin{pmatrix} 1 & 2 \\ 3 & 4\end{pmatrix}\begin{pmatrix} 2 \\ -1\end{pmatrix} = 2\begin{pmatrix} 1 \\ 3\end{pmatrix} - 1\begin{pmatrix} 2 \\ 4\end{pmatrix} = \begin{pmatrix} 0 \\ 2\end{pmatrix}$

**Example 4 (cost).** Two orders of [pens, notebooks, bags] = $\begin{pmatrix} 10 & 5 & 1 \\ 4 & 2 & 2\end{pmatrix}$; prices $\begin{pmatrix} 15 \\ 60 \\ 900\end{pmatrix}$ → totals $\begin{pmatrix} 1350 \\ 1980\end{pmatrix}$ taka.

**Example 5 (dimensions).** $(3 \times 2)(2 \times 4) = 3 \times 4$ ✓; $(3 \times 2)(3 \times 2)$ is undefined.

**Example 6 (neural network layer, code).**
```python
import numpy as np
x = np.array([0.5, -1.0, 2.0])          # input with 3 features
W = np.random.randn(4, 3)               # layer with 4 neurons
b = np.zeros(4)
h = np.maximum(0, W @ x + b)            # ReLU(Wx + b): shape (4,)
print(h.shape)
```
`W @ x` multiplies a $4 \times 3$ matrix by a 3-vector, giving 4 outputs: each is one neuron's dot product of its weights with the input. A whole network is a chain of these products.

## 5. Common mistakes
- Multiplying entry by entry (that's `A * B` in NumPy, not matrix multiplication; use `A @ B`).
- Assuming $AB = BA$.
- Multiplying incompatible sizes.

## 6. Practice
Q: Size of $AB$ if $A$ is $2 \times 5$ and $B$ is $5 \times 3$?
A: $2 \times 3$.

Q: $\begin{pmatrix} 2 & 0 \\ 1 & 3\end{pmatrix}\begin{pmatrix} 4 \\ 5\end{pmatrix}$?
A: $\begin{pmatrix} 8 \\ 19\end{pmatrix}$.

Q: Is matrix multiplication commutative?
A: No, in general $AB \ne BA$.

Q: In NumPy, what's the difference between `A * B` and `A @ B`?
A: `*` multiplies entry by entry; `@` is matrix multiplication.

Q: $A\vec{x}$ is a combination of what?
A: The columns of $A$, weighted by the entries of $\vec{x}$.

@@ 3 | TOPIC | Solving systems with Gaussian elimination | Augmented matrices, row operations, row echelon and reduced row echelon form, back substitution | MUST
# Solving systems with Gaussian elimination

## 1. What it covers
- Writing a system as an **augmented matrix** $[A \mid \vec{b}]$.
- The three **row operations**.
- **Row echelon form** (REF) and **reduced row echelon form** (RREF).
- Back substitution.

## 2. Where it is used
- **Engineering:** circuit analysis, structural analysis, finite-element simulations solve huge linear systems.
- **Computer graphics:** solving for intersection points and transformations.
- **Economics:** input–output models (Leontief).
- **Numerical libraries:** `numpy.linalg.solve` uses LU decomposition, an organised version of Gaussian elimination.

## 3. What it means
Elimination is the systematic version of the method you used for 2 equations: combine equations to remove variables one by one.

**Row operations** (they don't change the solutions):
1. Swap two rows.
2. Multiply a row by a non-zero number.
3. Add a multiple of one row to another.

**Goal:** a staircase shape (**row echelon form**) with zeros below each leading entry (**pivot**). Then solve from the bottom up (back substitution). Continuing until each pivot is 1 with zeros above and below gives **RREF**, where you can read off the solution directly.

## 4. Formula & worked example
**Example.** Solve:
$$
\begin{aligned} x + y + z &= 6 \\ 2x + 3y + z &= 14 \\ x - y + 2z &= 5 \end{aligned}
$$
Augmented matrix and elimination:
$$
\left(\begin{array}{ccc|c} 1 & 1 & 1 & 6 \\ 2 & 3 & 1 & 14 \\ 1 & -1 & 2 & 5 \end{array}\right) \xrightarrow[R_3 - R_1]{R_2 - 2R_1} \left(\begin{array}{ccc|c} 1 & 1 & 1 & 6 \\ 0 & 1 & -1 & 2 \\ 0 & -2 & 1 & -1 \end{array}\right) \xrightarrow{R_3 + 2R_2} \left(\begin{array}{ccc|c} 1 & 1 & 1 & 6 \\ 0 & 1 & -1 & 2 \\ 0 & 0 & -1 & 3 \end{array}\right)
$$
- Back substitution: $-z = 3$ → $z = -3$; $y - z = 2$ → $y = -1$; $x + y + z = 6$ → $x = 10$.
- Check in equation 2: $20 - 3 - 3 = 14$ ✓

**Code.**
```python
import numpy as np
A = np.array([[1, 1, 1], [2, 3, 1], [1, -1, 2]], dtype=float)
b = np.array([6, 14, 5], dtype=float)
print(np.linalg.solve(A, b))       # [10. -1. -3.]
import sympy as sp
print(sp.Matrix(np.column_stack([A, b])).rref()[0])   # reduced row echelon form
```
`np.linalg.solve` performs (a stable form of) elimination internally. SymPy's `rref()` shows the reduced row echelon form exactly, which is handy for checking hand work.

## 5. Common mistakes
- Applying an operation to only part of a row (including forgetting the augmented column).
- Arithmetic slips with negatives (check by substituting the answer).
- Computing an inverse to solve a system (slower and less accurate than elimination).

## 6. Practice
Q: Three allowed row operations?
A: Swap rows, scale a row by a non-zero number, add a multiple of one row to another.

Q: Solve $x + 2y = 5$, $3x + 4y = 11$ by elimination.
A: $R_2 - 3R_1$: $-2y = -4$ → $y = 2$, $x = 1$.

Q: What is a pivot?
A: The first non-zero entry in a row of an echelon form.

Q: What does RREF let you do?
A: Read the solution directly (each pivot 1, zeros above and below).

Q: Which NumPy function solves $A\vec{x} = \vec{b}$?
A: `np.linalg.solve(A, b)`.

@@ 3 | TOPIC | Solution sets of linear systems | Consistent and inconsistent systems, pivots and free variables, parametric solutions, homogeneous systems | MUST
# Solution sets of linear systems

## 1. What it covers
- Recognising **no solution**, **exactly one** or **infinitely many** from the echelon form.
- **Pivot** and **free** variables.
- Writing infinite solution sets in **parametric vector form**.
- **Homogeneous** systems $A\vec{x} = \vec{0}$.

## 2. Where it is used
- **Engineering design:** underdetermined systems (more unknowns than equations) have many solutions; you then choose the best one (e.g. least energy).
- **Chemistry:** balancing chemical equations is a homogeneous system.
- **Computer graphics and robotics:** robot arms with extra joints have infinitely many configurations reaching the same point.
- **Data science:** detecting redundant features.

## 3. What it means
After row reduction:
- A row like $[0\ 0\ 0 \mid 5]$ means $0 = 5$ → **no solution** (inconsistent).
- Otherwise, each column with a pivot gives a **pivot (basic) variable**; columns without a pivot give **free variables**, which can be anything.
  - No free variables → **exactly one** solution.
  - At least one free variable → **infinitely many** solutions.

**Parametric form:** write the pivot variables in terms of the free ones. The solution set is a point, a line, a plane, ... in space.

**Homogeneous** $A\vec{x} = \vec{0}$ always has the solution $\vec{x} = \vec{0}$; it has others exactly when there's a free variable.

## 4. Formula & worked example
**Example 1 (no solution).**
$$
\left(\begin{array}{cc|c} 1 & 2 & 3 \\ 0 & 0 & 4 \end{array}\right) \Rightarrow 0 = 4 \Rightarrow \text{no solution}
$$

**Example 2 (infinitely many).** RREF:
$$
\left(\begin{array}{ccc|c} 1 & 0 & 2 & 5 \\ 0 & 1 & -1 & 3 \end{array}\right)
$$
- $z$ is free: $x = 5 - 2z$, $y = 3 + z$ →
$$
\vec{x} = \begin{pmatrix} 5 \\ 3 \\ 0\end{pmatrix} + z\begin{pmatrix} -2 \\ 1 \\ 1\end{pmatrix}
$$
a line in 3D.

**Example 3 (balancing a reaction).** $a\,\text{H}_2 + b\,\text{O}_2 \to c\,\text{H}_2\text{O}$: H: $2a = 2c$; O: $2b = c$ → free variable $c$; take $c = 2$: $a = 2$, $b = 1$ → $2\text{H}_2 + \text{O}_2 \to 2\text{H}_2\text{O}$.

**Example 4 (count).** A system of 3 equations in 5 unknowns (consistent) has at least $5 - 3 = 2$ free variables → infinitely many solutions.

## 5. Common mistakes
- Concluding "no solution" from a row of all zeros (that's $0 = 0$, which is fine).
- Treating a free variable as 0 and reporting one solution.
- Forgetting that homogeneous systems are always consistent.

## 6. Practice
Q: What row signals an inconsistent system?
A: $[0\ 0\ \cdots\ 0 \mid c]$ with $c \ne 0$.

Q: 4 unknowns, 2 pivots, consistent. How many solutions?
A: Infinitely many (2 free variables).

Q: Does $A\vec{x} = \vec{0}$ always have a solution?
A: Yes, $\vec{x} = \vec{0}$.

Q: RREF rows $[1\ 0 \mid 2]$, $[0\ 1 \mid -1]$. Solution?
A: $x = 2$, $y = -1$.

Q: What is a free variable?
A: A variable whose column has no pivot; it can take any value.

@@ 3 | TOPIC | Inverse matrices | Definition, 2×2 formula, finding inverses by row reduction, invertibility, solving with inverses | MUST
# Inverse matrices

## 1. What it covers
- The **inverse** $A^{-1}$ with $AA^{-1} = A^{-1}A = I$.
- The $2 \times 2$ formula.
- Finding $A^{-1}$ by row reducing $[A \mid I]$.
- When an inverse exists (**invertible**/non-singular matrices).
- Solving $A\vec{x} = \vec{b}$ as $\vec{x} = A^{-1}\vec{b}$, and properties like $(AB)^{-1} = B^{-1}A^{-1}$.

## 2. Where it is used
- **Graphics:** undoing transformations (camera view matrix = inverse of the camera's placement).
- **Cryptography:** the Hill cipher encrypts with a matrix and decrypts with its inverse.
- **Statistics:** the normal equation $\hat{\beta} = (X^TX)^{-1}X^T\vec{y}$ in regression (computed more stably in practice).
- **Robotics:** inverse transformations between coordinate frames.

## 3. What it means
If $A$ transforms vectors, $A^{-1}$ **undoes** it: applying $A$ then $A^{-1}$ returns every vector to where it started. Only **square** matrices can have inverses, and only if they don't squash space flat (determinant ≠ 0).

**2×2 formula:** swap the diagonal entries, negate the off-diagonal ones, divide by the determinant $ad - bc$.

**Socks and shoes:** $(AB)^{-1} = B^{-1}A^{-1}$: to undo "put on socks, then shoes", take off shoes first.

## 4. Formula & worked example
$$
\begin{pmatrix} a & b \\ c & d\end{pmatrix}^{-1} = \frac{1}{ad - bc}\begin{pmatrix} d & -b \\ -c & a\end{pmatrix} \qquad (AB)^{-1} = B^{-1}A^{-1}
$$

**Example 1.** $A = \begin{pmatrix} 4 & 7 \\ 2 & 6\end{pmatrix}$: $\det = 24 - 14 = 10$ → $A^{-1} = \frac{1}{10}\begin{pmatrix} 6 & -7 \\ -2 & 4\end{pmatrix}$.
- Check: $AA^{-1} = \frac{1}{10}\begin{pmatrix} 24 - 14 & -28 + 28 \\ 12 - 12 & -14 + 24\end{pmatrix} = I$ ✓

**Example 2 (not invertible).** $\begin{pmatrix} 2 & 4 \\ 1 & 2\end{pmatrix}$: $\det = 0$ → no inverse (the second column is twice the first).

**Example 3 (solve).** $4x + 7y = 1$, $2x + 6y = 2$: $\vec{x} = A^{-1}\begin{pmatrix} 1 \\ 2\end{pmatrix} = \frac{1}{10}\begin{pmatrix} -8 \\ 6\end{pmatrix} = \begin{pmatrix} -0.8 \\ 0.6\end{pmatrix}$.

**Example 4 (row reduction).** Reduce $[A \mid I]$ until the left side is $I$; the right side is then $A^{-1}$.

**Example 5 (code).**
```python
import numpy as np
A = np.array([[4, 7], [2, 6]], dtype=float)
Ainv = np.linalg.inv(A)
print(Ainv, A @ Ainv)                 # inverse, and ~identity
print(np.linalg.solve(A, [1, 2]))     # [-0.8  0.6]  preferred over inv(A) @ b
```
`np.linalg.inv` computes the inverse, but to solve a system use `solve`: it's faster and more accurate than forming the inverse explicitly.

## 5. Common mistakes
- Writing $(AB)^{-1} = A^{-1}B^{-1}$.
- Forgetting to divide by the determinant.
- Computing $A^{-1}$ to solve one system (use elimination/`solve`).

## 6. Practice
Q: Inverse of $\begin{pmatrix} 2 & 1 \\ 5 & 3\end{pmatrix}$?
A: $\det = 1$ → $\begin{pmatrix} 3 & -1 \\ -5 & 2\end{pmatrix}$.

Q: When is a square matrix invertible?
A: When its determinant is non-zero.

Q: $(AB)^{-1}$?
A: $B^{-1}A^{-1}$.

Q: Inverse of $I$?
A: $I$.

Q: Inverse of $\begin{pmatrix} 3 & 0 \\ 0 & 5\end{pmatrix}$?
A: $\begin{pmatrix} 1/3 & 0 \\ 0 & 1/5\end{pmatrix}$.

@@ 3 | TOPIC | Determinants | 2×2 and 3×3 determinants, cofactor expansion, geometric meaning as area/volume scaling | MUST
# Determinants

## 1. What it covers
- The **determinant** of $2 \times 2$ and $3 \times 3$ matrices.
- **Cofactor expansion**.
- Geometric meaning: how much a matrix scales **area** (2D) or **volume** (3D), and whether it flips orientation.
- $\det A = 0$ ⇔ not invertible.

## 2. Where it is used
- **Invertibility checks** in solvers.
- **Graphics and physics:** checking if a transformation flips a model inside out (negative determinant).
- **Multivariable calculus:** the Jacobian determinant in change of variables.
- **Geometry:** area of triangles and parallelograms from coordinates; volume of a parallelepiped.

## 3. What it means
A $2 \times 2$ matrix sends the unit square to a parallelogram. The determinant is the **area** of that parallelogram (with a sign):
- $\lvert\det A\rvert$: how much areas are scaled.
- $\det A < 0$: orientation is flipped (like a mirror image).
- $\det A = 0$: space is squashed onto a line or a point, so information is lost and the transformation can't be undone.

In 3D, it's the volume scaling factor.

**3×3 by cofactor expansion along the first row:** each entry times the determinant of the $2 \times 2$ matrix left after deleting its row and column, with signs $+, -, +$.

## 4. Formula & worked example
$$
\det\begin{pmatrix} a & b \\ c & d\end{pmatrix} = ad - bc \qquad \det\begin{pmatrix} a & b & c \\ d & e & f \\ g & h & i\end{pmatrix} = a(ei - fh) - b(di - fg) + c(dh - eg)
$$

**Example 1.** $\det\begin{pmatrix} 3 & 1 \\ 2 & 4\end{pmatrix} = 12 - 2 = 10$: areas are multiplied by 10.

**Example 2 (flip).** $\det\begin{pmatrix} 0 & 1 \\ 1 & 0\end{pmatrix} = -1$: a reflection (swaps x and y), area unchanged, orientation flipped.

**Example 3 (3×3).** $\det\begin{pmatrix} 2 & 0 & 1 \\ 1 & 3 & 2 \\ 1 & 1 & 1\end{pmatrix} = 2(3 - 2) - 0 + 1(1 - 3) = 2 - 2 = 0$ → not invertible (the columns are dependent).

**Example 4 (triangle area).** Triangle with vertices $(0, 0)$, $(4, 1)$, $(1, 3)$: area $= \frac{1}{2}\left\lvert\det\begin{pmatrix} 4 & 1 \\ 1 & 3\end{pmatrix}\right\rvert = \frac{11}{2}$ (the shoelace formula from Geometry in matrix form).

**Example 5 (code).**
```python
import numpy as np
print(np.linalg.det(np.array([[3, 1], [2, 4]])))                  # 10.000000000000002
print(np.linalg.det(np.array([[2, 0, 1], [1, 3, 2], [1, 1, 1]]))) # ~0 (tiny rounding error)
```
Floating point gives results like `1e-16` instead of exactly 0, so test invertibility with a tolerance (or better, with `np.linalg.cond` or the rank).

## 5. Common mistakes
- Sign errors in cofactor expansion (the middle term is subtracted).
- Taking the determinant of a non-square matrix (undefined).
- Testing `det == 0` exactly in floating-point code.

## 6. Practice
Q: $\det\begin{pmatrix} 5 & 2 \\ 3 & 1\end{pmatrix}$?
A: $-1$.

Q: What does $\det A = 0$ mean geometrically?
A: The transformation squashes space into a lower dimension (not invertible).

Q: A matrix has determinant 3. What happens to a shape of area 4?
A: Its area becomes 12.

Q: $\det(I_3)$?
A: $1$.

Q: What does a negative determinant indicate?
A: Orientation is reversed (a reflection is involved).

@@ 3 | TOPIC | Properties of determinants | Effect of row operations, det(AB) = det A det B, det(Aᵀ), det(A⁻¹), triangular matrices | MUST
# Properties of determinants

## 1. What it covers
- How row operations change the determinant.
- Triangular matrices: determinant = product of the diagonal.
- $\det(AB) = \det(A)\det(B)$, $\det(A^T) = \det(A)$, $\det(A^{-1}) = \frac{1}{\det A}$, $\det(cA) = c^n\det A$.

## 2. Where it is used
- **Efficient computation:** computers find determinants by row reducing to triangular form (cofactor expansion is far too slow for large matrices: $n!$ terms).
- **Theory:** proving invertibility, eigenvalue calculations.
- **Graphics:** composing transformations multiplies their area/volume scale factors.

## 3. What it means
**Row operations:**
- Swapping two rows → determinant changes **sign**.
- Multiplying a row by $c$ → determinant is multiplied by $c$.
- Adding a multiple of one row to another → determinant **unchanged**.

So: reduce to triangular form, keep track of swaps and scalings, and multiply the diagonal.

**Product rule:** doing transformation $B$ then $A$ scales area by $\det B$ then by $\det A$, so $\det(AB) = \det A \det B$.

## 4. Formula & worked example
$$
\det(AB) = \det A\det B \qquad \det A^T = \det A \qquad \det A^{-1} = \frac{1}{\det A} \qquad \det(cA) = c^n\det A\ (n \times n)
$$

**Example 1 (triangular).** $\det\begin{pmatrix} 2 & 5 & 1 \\ 0 & 3 & 7 \\ 0 & 0 & 4\end{pmatrix} = 2 \times 3 \times 4 = 24$

**Example 2 (row reduce).** $\det\begin{pmatrix} 1 & 2 & 3 \\ 2 & 5 & 7 \\ 1 & 3 & 5\end{pmatrix}$: $R_2 - 2R_1$, $R_3 - R_1$ → $\begin{pmatrix} 1 & 2 & 3 \\ 0 & 1 & 1 \\ 0 & 1 & 2\end{pmatrix}$; $R_3 - R_2$ → diagonal 1, 1, 1 → $\det = 1$.

**Example 3.** $\det A = 3$, $\det B = -2$ ($3 \times 3$): $\det(AB) = -6$; $\det(A^{-1}) = \frac{1}{3}$; $\det(2A) = 8 \times 3 = 24$; $\det(A^T) = 3$.

**Example 4 (swap).** Swapping two rows of a matrix with determinant 5 gives $-5$.

## 5. Common mistakes
- $\det(A + B) = \det A + \det B$ (false).
- $\det(cA) = c\det A$ (it's $c^n$).
- Forgetting sign changes from row swaps.

## 6. Practice
Q: Determinant of a triangular matrix?
A: The product of its diagonal entries.

Q: Effect of adding $3R_1$ to $R_2$ on the determinant?
A: None.

Q: $\det A = 4$ ($2 \times 2$). $\det(3A)$?
A: $36$.

Q: $\det A = 5$. $\det(A^{-1})$?
A: $\frac{1}{5}$.

Q: Why do computers row reduce to find determinants?
A: Cofactor expansion takes about $n!$ operations; row reduction about $n^3$.

@@ 3 | TOPIC | Transpose and symmetric matrices | Aᵀ, (AB)ᵀ = BᵀAᵀ, symmetric matrices, AᵀA, dot products as matrix products | MUST
# Transpose and symmetric matrices

## 1. What it covers
- The **transpose** $A^T$ (rows become columns).
- Rules: $(A^T)^T = A$, $(A + B)^T = A^T + B^T$, $(AB)^T = B^TA^T$.
- **Symmetric** matrices ($A^T = A$).
- The dot product as $\vec{u}^T\vec{v}$; the matrix $A^TA$.

## 2. Where it is used
- **Statistics and ML:** covariance matrices $\frac{1}{n}X^TX$ (for centred data) are symmetric; the normal equations use $X^TX$.
- **Graphs:** undirected networks have symmetric adjacency matrices.
- **Physics:** stress and inertia tensors are symmetric.
- **Data wrangling:** `df.T` transposes a table.

## 3. What it means
**Transpose:** flip the matrix over its main diagonal, so row $i$ becomes column $i$. An $m \times n$ matrix becomes $n \times m$.

**$(AB)^T = B^TA^T$:** the order reverses (like the inverse rule).

**Symmetric:** equal to its own transpose; entries mirror across the diagonal ($a_{ij} = a_{ji}$). Symmetric matrices have especially nice properties: real eigenvalues and perpendicular eigenvectors (Phase 5–6).

**$A^TA$ is always symmetric** and square; it appears in least squares and PCA. Its entries are the dot products between the columns of $A$.

## 4. Formula & worked example
$$
(A^T)_{ij} = a_{ji} \qquad (AB)^T = B^TA^T \qquad \vec{u} \cdot \vec{v} = \vec{u}^T\vec{v}
$$

**Example 1.** $\begin{pmatrix} 1 & 2 & 3 \\ 4 & 5 & 6\end{pmatrix}^T = \begin{pmatrix} 1 & 4 \\ 2 & 5 \\ 3 & 6\end{pmatrix}$

**Example 2 (symmetric?).** $\begin{pmatrix} 2 & 7 \\ 7 & 3\end{pmatrix}$ yes; $\begin{pmatrix} 2 & 7 \\ 1 & 3\end{pmatrix}$ no.

**Example 3 ($A^TA$).** $A = \begin{pmatrix} 1 & 2 \\ 3 & 4 \\ 5 & 6\end{pmatrix}$: $A^TA = \begin{pmatrix} 35 & 44 \\ 44 & 56\end{pmatrix}$ (symmetric; $35 = 1 + 9 + 25$ is column 1 · column 1).

**Example 4 (covariance, code).**
```python
import numpy as np
X = np.array([[1.0, 2.0], [2.0, 3.9], [3.0, 6.1], [4.0, 8.0]])   # 4 samples, 2 features
Xc = X - X.mean(axis=0)                  # centre each column
cov = Xc.T @ Xc / (len(X) - 1)
print(cov, np.allclose(cov, np.cov(X, rowvar=False)))   # symmetric, matches np.cov
```
Centring and computing $\frac{1}{n-1}X_c^TX_c$ gives the covariance matrix: the diagonal holds each feature's variance, and the off-diagonals the covariances between features. It's symmetric by construction.

## 5. Common mistakes
- $(AB)^T = A^TB^T$ (order must reverse).
- Thinking only square matrices have transposes.
- Confusing $A^TA$ ($n \times n$) with $AA^T$ ($m \times m$).

## 6. Practice
Q: Transpose of a $3 \times 5$ matrix has what size?
A: $5 \times 3$.

Q: $(AB)^T$?
A: $B^TA^T$.

Q: Is $\begin{pmatrix} 1 & 0 \\ 0 & 4\end{pmatrix}$ symmetric?
A: Yes.

Q: Why is $A^TA$ always symmetric?
A: $(A^TA)^T = A^T(A^T)^T = A^TA$.

Q: Write $\vec{u} \cdot \vec{v}$ as a matrix product.
A: $\vec{u}^T\vec{v}$.

@@ 3 | TOPIC | Matrix computations with NumPy | Creating arrays, products, solving, inverse, determinant, rank, eigenvalues, broadcasting, performance | SHOULD
# Matrix computations with NumPy

## 1. What it covers
- Creating vectors and matrices; shapes and reshaping.
- Products (`@`), element-wise operations, broadcasting.
- `np.linalg`: `solve`, `inv`, `det`, `matrix_rank`, `eig`, `norm`, `lstsq`, `svd`.
- Why vectorised code is much faster than Python loops.

## 2. Where it is used
- **All of data science and ML:** pandas, scikit-learn, PyTorch and TensorFlow are built on these array operations.
- **Scientific computing and engineering simulations.**
- **Image processing:** images as arrays.

## 3. What it means
NumPy stores numbers in compact arrays and runs operations in optimised C/Fortran (BLAS/LAPACK), often using multiple CPU cores. One `A @ B` on large matrices can be thousands of times faster than nested Python loops.

**Broadcasting:** NumPy stretches smaller arrays to match bigger ones when shapes are compatible, e.g. subtracting a row of column means from every row of a matrix, without writing a loop.

## 4. Formula & worked example
**Example 1 (core operations).**
```python
import numpy as np
A = np.array([[2., 1.], [1., 3.]])
b = np.array([3., 5.])
print(A @ b)                       # matrix-vector product
print(np.linalg.solve(A, b))       # solve Ax = b  -> [0.8 1.4]
print(np.linalg.det(A))            # 5.0
print(np.linalg.inv(A))            # inverse
print(np.linalg.matrix_rank(A))    # 2
vals, vecs = np.linalg.eig(A)      # eigenvalues and eigenvectors (Phase 5)
print(vals)
```
Each line maps one-to-one to a concept in this roadmap; keep this as a cheat sheet.

**Example 2 (broadcasting: standardise features).**
```python
X = np.random.rand(1000, 3) * [100, 5, 50]     # 1000 samples, 3 features on different scales
Z = (X - X.mean(axis=0)) / X.std(axis=0)       # subtract column means, divide by column SDs
print(Z.mean(axis=0).round(6), Z.std(axis=0))  # ~[0 0 0] and [1 1 1]
```
`X.mean(axis=0)` has shape (3,), and NumPy broadcasts it across all 1000 rows: no loop needed. This is standard preprocessing before many ML models.

**Example 3 (speed).**
```python
import time
n = 200
A = np.random.rand(n, n); B = np.random.rand(n, n)
t = time.time(); C = A @ B; print("numpy", time.time() - t)
t = time.time()
C2 = [[sum(A[i, k] * B[k, j] for k in range(n)) for j in range(n)] for i in range(n)]
print("python loops", time.time() - t)          # typically 1000x+ slower
```
The triple loop does the same $n^3 = 8$ million multiplications as `A @ B`, but in slow Python; NumPy hands the work to optimised libraries.

## 5. Common mistakes
- Using `*` for matrix multiplication.
- Shape mismatches: a 1D array of shape `(3,)` vs a column `(3, 1)`.
- Using `inv` to solve systems instead of `solve`.

## 6. Practice
Q: NumPy operator for matrix multiplication?
A: `@` (or `np.matmul`).

Q: Function to solve $A\vec{x} = \vec{b}$?
A: `np.linalg.solve`.

Q: What does `X.mean(axis=0)` compute for a data matrix?
A: The mean of each column (feature).

Q: Why is NumPy faster than Python loops?
A: It runs operations in compiled, optimised libraries on whole arrays at once.

Q: Function for the rank of a matrix?
A: `np.linalg.matrix_rank`.

@@ 3 | TOPIC | Practice & review: Matrices and linear systems | Mixed problem set with worked answers | MUST
# Practice & review: Matrices and linear systems

**How to use this:** 50 minutes by hand, then check with NumPy.

## Problems
1. $A = \begin{pmatrix} 1 & -1 \\ 2 & 0\end{pmatrix}$, $B = \begin{pmatrix} 3 & 1 \\ 0 & 2\end{pmatrix}$. Compute $AB$, $BA$, $A^T$.
2. Solve by elimination: $x + 2y - z = 1$, $2x + y + z = 5$, $x - y + 3z = 5$.
3. Describe the solution set of the system with RREF $\left(\begin{array}{ccc|c} 1 & -2 & 0 & 4 \\ 0 & 0 & 1 & -1 \end{array}\right)$.
4. Find the inverse of $\begin{pmatrix} 3 & 5 \\ 1 & 2\end{pmatrix}$ and use it to solve $3x + 5y = 2$, $x + 2y = 1$.
5. $\det\begin{pmatrix} 1 & 2 & 0 \\ 3 & 1 & 4 \\ 0 & 2 & 1\end{pmatrix}$.
6. $\det A = -2$ for a $3 \times 3$ matrix $A$. Find $\det(A^T)$, $\det(A^2)$, $\det(-A)$, $\det(A^{-1})$.
7. For which $k$ is $\begin{pmatrix} 2 & k \\ 3 & 6\end{pmatrix}$ not invertible?
8. Explain why `np.linalg.solve(A, b)` is preferred to `np.linalg.inv(A) @ b`.

## Answers
1. $AB = \begin{pmatrix} 3 & -1 \\ 6 & 2\end{pmatrix}$; $BA = \begin{pmatrix} 5 & -3 \\ 4 & 0\end{pmatrix}$; $A^T = \begin{pmatrix} 1 & 2 \\ -1 & 0\end{pmatrix}$.
2. $x = 2$, $y = 0$, $z = 1$.
3. $y$ free: $x = 4 + 2y$, $z = -1$ → $\vec{x} = (4, 0, -1) + y(2, 1, 0)$, a line.
4. $\det = 1$; inverse $\begin{pmatrix} 2 & -5 \\ -1 & 3\end{pmatrix}$; $\vec{x} = (2 \cdot 2 - 5,\ -2 + 3) = (-1, 1)$.
5. $1(1 - 8) - 2(3 - 0) + 0 = -13$.
6. $-2$; $4$; $(-1)^3(-2) = 2$; $-\frac{1}{2}$.
7. $12 - 3k = 0$ → $k = 4$.
8. It's faster (no full inverse needed) and numerically more accurate.

## Flashcards
Q: Condition for $AB$ to be defined?
A: Columns of $A$ = rows of $B$.

Q: 2×2 inverse formula?
A: $\frac{1}{ad - bc}\begin{pmatrix} d & -b \\ -c & a\end{pmatrix}$.

Q: Geometric meaning of the determinant?
A: The factor by which the transformation scales area/volume (sign = orientation).

Q: How do you spot infinitely many solutions in RREF?
A: A consistent system with at least one free variable.
