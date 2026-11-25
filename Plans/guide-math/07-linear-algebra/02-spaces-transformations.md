@@ 1 | MODULE | Phase 3 — Vector Spaces (Core) | Span, linear independence, subspaces, basis and dimension, null space and column space, rank and rank–nullity | MUST
# Phase 3: Vector spaces

This phase gives you the vocabulary to describe **what a set of vectors can reach** (span), **whether any of them are redundant** (independence), and **how many truly different directions exist** (dimension). These ideas explain when systems have solutions, why some data features are redundant, and how dimensionality reduction works.

@@ 2 | MODULE | Span, independence and subspaces | Span, linear independence, subspaces, basis, dimension, null and column spaces, rank | MUST
Think of vectors as directions you're allowed to travel in; span is everywhere you can get.

@@ 3 | TOPIC | Linear combinations and span | What a set of vectors can reach; span as a line, plane or all of space; testing membership | MUST
# Linear combinations and span

## 1. What it covers
- The **span** of a set of vectors: all their linear combinations.
- Geometric pictures: a line, a plane, or all of $\mathbb{R}^n$.
- Testing whether a vector is in a span (solving a system).
- The link: $A\vec{x} = \vec{b}$ has a solution ⇔ $\vec{b}$ is in the span of $A$'s columns.

## 2. Where it is used
- **Solvability of systems:** whether a target can be reached by combining available "ingredients".
- **Colour mixing:** which colours a set of primary lights can make.
- **Signal processing:** which signals can be built from a set of basis waves.
- **ML:** the outputs a linear model can produce lie in the span of its features.

## 3. What it means
Imagine you can walk any amount (forwards or backwards) along a set of direction vectors and combine the walks. The **span** is every point you can reach.
- Span of one non-zero vector: a **line** through the origin.
- Span of two non-parallel vectors in 3D: a **plane** through the origin.
- Span of three vectors in 3D not in one plane: **all of 3D space**.

If one vector is a combination of the others, it adds nothing new to the span.

**Membership test:** $\vec{b}$ is in $\text{span}\{\vec{v}_1, \ldots, \vec{v}_k\}$ if the system $c_1\vec{v}_1 + \cdots + c_k\vec{v}_k = \vec{b}$ has a solution.

## 4. Formula & worked example
$$
\text{span}\{\vec{v}_1, \ldots, \vec{v}_k\} = \{c_1\vec{v}_1 + \cdots + c_k\vec{v}_k : c_i \in \mathbb{R}\}
$$

**Example 1.** $\text{span}\{\langle 1, 2\rangle\}$ is the line $y = 2x$.

**Example 2.** $\text{span}\{\langle 1, 0\rangle, \langle 0, 1\rangle\} = \mathbb{R}^2$; so is $\text{span}\{\langle 1, 1\rangle, \langle 1, -1\rangle\}$.

**Example 3.** $\text{span}\{\langle 1, 2\rangle, \langle 2, 4\rangle\}$ is just the line $y = 2x$ (the second vector is redundant).

**Example 4 (membership).** Is $\langle 4, 1, 7\rangle$ in $\text{span}\{\langle 1, 0, 1\rangle, \langle 1, 1, 3\rangle\}$?
- $c_1 + c_2 = 4$, $c_2 = 1$, $c_1 + 3c_2 = 7$ → $c_1 = 3$, $c_2 = 1$ → check the third: $3 + 3 = 6 \ne 7$ → **no**.

**Example 5 (code).**
```python
import numpy as np
V = np.array([[1, 1], [0, 1], [1, 3]], dtype=float)   # columns are the spanning vectors
b = np.array([4, 1, 7.])
c, res, rank, _ = np.linalg.lstsq(V, b, rcond=None)
print(c, np.allclose(V @ c, b))     # best combination, False -> b is not in the span
```
`lstsq` finds the best combination of the columns; if even the best one doesn't reproduce `b` exactly, `b` is outside the span. (Least squares is explained in Phase 6.)

## 5. Common mistakes
- Thinking two vectors always span a plane (not if they're parallel).
- Forgetting that every span contains the zero vector.
- Checking only some equations in the membership test.

## 6. Practice
Q: What is the span of $\langle 0, 0\rangle$?
A: Just the zero vector.

Q: Do $\langle 1, 2\rangle$ and $\langle 3, 1\rangle$ span $\mathbb{R}^2$?
A: Yes (not parallel).

Q: Is $\langle 3, 6\rangle$ in $\text{span}\{\langle 1, 2\rangle\}$?
A: Yes, $3\langle 1, 2\rangle$.

Q: Can two vectors span $\mathbb{R}^3$?
A: No, at most a plane.

Q: $A\vec{x} = \vec{b}$ has a solution exactly when ...?
A: $\vec{b}$ is in the span of the columns of $A$.

@@ 3 | TOPIC | Linear independence | Definition, testing with row reduction or determinants, redundancy, geometric meaning | MUST
# Linear independence

## 1. What it covers
- **Linearly independent** vs **dependent** sets.
- Testing: solve $c_1\vec{v}_1 + \cdots + c_k\vec{v}_k = \vec{0}$; use row reduction or the determinant.
- Facts: more than $n$ vectors in $\mathbb{R}^n$ are always dependent; a set containing $\vec{0}$ is dependent.

## 2. Where it is used
- **Data science:** perfectly correlated (dependent) features cause **multicollinearity**: regression coefficients become unstable or undefined. Dropping redundant columns fixes it (e.g. dummy-variable trap in one-hot encoding).
- **Engineering:** independent equations are needed to determine all unknowns.
- **Signal processing and coding theory:** independent signals/codewords carry separate information.

## 3. What it means
A set of vectors is **independent** if none of them can be built from the others: each adds a genuinely new direction. It's **dependent** if at least one is redundant (a combination of the others).

**Test:** the only way to combine them into $\vec{0}$ should be with all coefficients zero. If some non-zero combination gives $\vec{0}$, one vector can be solved for in terms of the others → dependent.

For $n$ vectors in $\mathbb{R}^n$: put them as columns of a square matrix; independent ⇔ $\det \ne 0$.

## 4. Formula & worked example
$$
c_1\vec{v}_1 + \cdots + c_k\vec{v}_k = \vec{0} \text{ only for } c_1 = \cdots = c_k = 0 \iff \text{independent}
$$

**Example 1.** $\langle 1, 2\rangle$ and $\langle 2, 4\rangle$: $2\vec{v}_1 - \vec{v}_2 = \vec{0}$ → dependent.

**Example 2.** $\langle 1, 0, 0\rangle$, $\langle 1, 1, 0\rangle$, $\langle 1, 1, 1\rangle$: $\det = 1 \ne 0$ → independent.

**Example 3.** $\langle 1, 2, 3\rangle$, $\langle 4, 5, 6\rangle$, $\langle 7, 8, 9\rangle$: $\det = 0$ → dependent (indeed $\vec{v}_3 = 2\vec{v}_2 - \vec{v}_1$).

**Example 4.** Any 4 vectors in $\mathbb{R}^3$ are dependent (only 3 independent directions exist).

**Example 5 (dummy-variable trap, code).**
```python
import numpy as np
# columns: intercept, is_dhaka, is_khulna, is_other (every row has exactly one city = 1)
X = np.array([[1, 1, 0, 0], [1, 0, 1, 0], [1, 0, 0, 1], [1, 1, 0, 0]])
print(np.linalg.matrix_rank(X))     # 3, not 4 -> columns are dependent
```
The three city columns always add up to the intercept column, so one is redundant. That's why `pd.get_dummies(..., drop_first=True)` drops one category.

## 5. Common mistakes
- Thinking "not parallel" is enough for three or more vectors (three vectors can be pairwise non-parallel yet lie in one plane).
- Using the determinant test on a non-square set.
- Forgetting that a set containing $\vec{0}$ is automatically dependent.

## 6. Practice
Q: Define linear independence.
A: No vector in the set is a linear combination of the others.

Q: Are $\langle 1, 3\rangle$ and $\langle -2, -6\rangle$ independent?
A: No.

Q: Are 5 vectors in $\mathbb{R}^4$ independent?
A: No, they must be dependent.

Q: Test for 3 vectors in $\mathbb{R}^3$?
A: The determinant of the matrix with them as columns is non-zero.

Q: Why do dependent features cause problems in regression?
A: The coefficients aren't uniquely determined ($X^TX$ isn't invertible).

@@ 3 | TOPIC | Subspaces | Definition (contains 0, closed under addition and scaling), examples and non-examples | MUST
# Subspaces

## 1. What it covers
- A **subspace** of $\mathbb{R}^n$: a set that contains $\vec{0}$ and is closed under addition and scalar multiplication.
- Examples: lines and planes through the origin, spans, solution sets of $A\vec{x} = \vec{0}$.
- Non-examples: lines not through the origin, the first quadrant.

## 2. Where it is used
- **Solutions of homogeneous systems** (null spaces) are subspaces.
- **Dimensionality reduction:** PCA finds a low-dimensional subspace that captures data well.
- **Signal processing:** filters keep signals in certain subspaces (frequency bands).
- **Error-correcting codes:** linear codes are subspaces.

## 3. What it means
A subspace is a "flat" piece of space through the origin that you can't escape by adding or scaling: a self-contained world for vectors.

**Three checks:**
1. Contains the zero vector.
2. Closed under addition: if $\vec{u}$ and $\vec{v}$ are in it, so is $\vec{u} + \vec{v}$.
3. Closed under scaling: if $\vec{u}$ is in it, so is $c\vec{u}$.

In $\mathbb{R}^3$, the subspaces are: $\{\vec{0}\}$, lines through the origin, planes through the origin, and $\mathbb{R}^3$ itself.

**Any span is a subspace**, and every subspace is the span of some vectors.

## 4. Formula & worked example
**Example 1.** The plane $x + 2y - z = 0$ in $\mathbb{R}^3$: contains $\vec{0}$ ✓; if two vectors satisfy it, so does their sum and any multiple ✓ → subspace.

**Example 2 (not).** The plane $x + 2y - z = 5$: doesn't contain $\vec{0}$ → not a subspace.

**Example 3 (not).** The first quadrant $\{(x, y) : x \ge 0, y \ge 0\}$: $(1, 1)$ is in it but $-1 \cdot (1, 1)$ isn't → not closed under scaling.

**Example 4 (not).** The union of the x-axis and y-axis: $(1, 0) + (0, 1) = (1, 1)$ is in neither axis → not closed under addition.

**Example 5.** The solution set of $A\vec{x} = \vec{0}$ is always a subspace (the **null space**, next topics).

## 5. Common mistakes
- Forgetting the zero-vector check (fastest way to rule a set out).
- Thinking any line or plane is a subspace (only those through the origin).
- Checking closure with one example instead of in general.

## 6. Practice
Q: Three conditions for a subspace?
A: Contains $\vec{0}$; closed under addition; closed under scalar multiplication.

Q: Is the line $y = 3x + 1$ a subspace of $\mathbb{R}^2$?
A: No, it doesn't contain the origin.

Q: Is $\{(x, y, z) : z = 0\}$ a subspace of $\mathbb{R}^3$?
A: Yes (the xy-plane).

Q: Is the set of vectors with integer entries a subspace?
A: No, scaling by $0.5$ can leave it.

Q: List all types of subspaces of $\mathbb{R}^2$.
A: $\{\vec{0}\}$, lines through the origin, and $\mathbb{R}^2$.

@@ 3 | TOPIC | Basis and dimension | Minimal spanning sets, standard basis, coordinates, dimension of a subspace | MUST
# Basis and dimension

## 1. What it covers
- A **basis**: a set that spans the space and is linearly independent.
- The **standard basis** $\vec{e}_1, \ldots, \vec{e}_n$.
- Every basis of a space has the same number of vectors: the **dimension**.
- **Coordinates** relative to a basis.

## 2. Where it is used
- **Data compression:** JPEG represents image blocks in a cosine basis and keeps only the important coordinates.
- **Embeddings:** word vectors have a fixed dimension (e.g. 768).
- **Physics and graphics:** choosing convenient coordinate axes.
- **Feature engineering:** the number of truly independent features is the dimension of their span.

## 3. What it means
A **basis** is a minimal set of building blocks: enough vectors to reach everything (spanning) and no redundant ones (independent). Every vector in the space can then be written in **exactly one** way as a combination of basis vectors; those weights are its **coordinates**.

A space can have many bases (like describing a location with different sets of directions), but they always have the same number of vectors: the **dimension**. $\mathbb{R}^n$ has dimension $n$; a plane through the origin has dimension 2.

**Shortcut:** in an $n$-dimensional space, any $n$ independent vectors automatically form a basis.

## 4. Formula & worked example
$$
\vec{v} = c_1\vec{b}_1 + \cdots + c_n\vec{b}_n \quad (\text{unique } c_i) \qquad \dim\mathbb{R}^n = n
$$

**Example 1.** Standard basis of $\mathbb{R}^3$: $\langle 1, 0, 0\rangle$, $\langle 0, 1, 0\rangle$, $\langle 0, 0, 1\rangle$. The coordinates of $\langle 4, -2, 7\rangle$ are just its entries.

**Example 2.** Is $\{\langle 1, 1\rangle, \langle 1, -1\rangle\}$ a basis of $\mathbb{R}^2$? Two independent vectors in a 2-dimensional space → yes.

**Example 3 (coordinates).** Write $\langle 5, 1\rangle$ in that basis: $c_1 + c_2 = 5$, $c_1 - c_2 = 1$ → $c_1 = 3$, $c_2 = 2$.

**Example 4 (basis of a plane).** The plane $x + y + z = 0$: $z = -x - y$, so vectors are $x\langle 1, 0, -1\rangle + y\langle 0, 1, -1\rangle$ → basis $\{\langle 1, 0, -1\rangle, \langle 0, 1, -1\rangle\}$, dimension 2.

**Example 5.** Can $\{\langle 1, 2, 3\rangle, \langle 4, 5, 6\rangle\}$ be a basis of $\mathbb{R}^3$? No: only 2 vectors (they span a plane at most).

## 5. Common mistakes
- Calling a spanning set with redundant vectors a basis.
- Thinking a space has only one basis.
- Confusing the dimension of a subspace with the number of components of its vectors (a plane in $\mathbb{R}^3$ has dimension 2).

## 6. Practice
Q: Two properties of a basis?
A: It spans the space and is linearly independent.

Q: Dimension of a line through the origin in $\mathbb{R}^5$?
A: $1$.

Q: Do $\langle 2, 0\rangle$ and $\langle 3, 1\rangle$ form a basis of $\mathbb{R}^2$?
A: Yes.

Q: Coordinates of $\langle 2, 4\rangle$ in the basis $\{\langle 1, 1\rangle, \langle 0, 1\rangle\}$?
A: $c_1 = 2$, $c_2 = 2$.

Q: How many vectors are in every basis of $\mathbb{R}^4$?
A: $4$.

@@ 3 | TOPIC | Null space and column space | The column space (reachable outputs) and null space (inputs sent to 0), finding bases for each | MUST
# Null space and column space

## 1. What it covers
- The **column space** $\text{Col}(A)$: all vectors $A\vec{x}$ (the span of the columns).
- The **null space** $\text{Nul}(A)$: all solutions of $A\vec{x} = \vec{0}$.
- Finding bases for both from the RREF.
- What they say about solving $A\vec{x} = \vec{b}$.

## 2. Where it is used
- **Solving systems:** $A\vec{x} = \vec{b}$ is solvable iff $\vec{b} \in \text{Col}(A)$; solutions are unique iff $\text{Nul}(A) = \{\vec{0}\}$.
- **Data science:** null-space directions are combinations of features that don't affect predictions (redundancy).
- **Robotics:** null-space motions move a redundant robot arm's joints without moving its hand.
- **Network flows and circuits:** null spaces describe balanced flows (Kirchhoff's laws).

## 3. What it means
Think of $A$ as a machine that turns inputs $\vec{x}$ into outputs $A\vec{x}$.
- **Column space:** the set of all possible **outputs**. If $\vec{b}$ isn't in it, $A\vec{x} = \vec{b}$ has no solution.
- **Null space:** the set of inputs the machine sends to **zero**. If it contains non-zero vectors, different inputs give the same output (adding a null-space vector to a solution gives another solution).

**Finding bases:**
- Column space: the **pivot columns of the original $A$** (as found from the RREF).
- Null space: solve $A\vec{x} = \vec{0}$ in parametric form; the vectors multiplying the free variables form a basis.

## 4. Formula & worked example
$$
\text{Col}(A) = \{A\vec{x}\} \qquad \text{Nul}(A) = \{\vec{x} : A\vec{x} = \vec{0}\}
$$

**Example.** $A = \begin{pmatrix} 1 & 2 & 3 \\ 2 & 4 & 7\end{pmatrix}$, RREF $\begin{pmatrix} 1 & 2 & 0 \\ 0 & 0 & 1\end{pmatrix}$.
- Pivots in columns 1 and 3 → basis of $\text{Col}(A)$: $\langle 1, 2\rangle$, $\langle 3, 7\rangle$ (from the **original** $A$). These span $\mathbb{R}^2$, so every $\vec{b}$ is reachable.
- Null space: $x_1 = -2x_2$, $x_3 = 0$, $x_2$ free → basis $\langle -2, 1, 0\rangle$ (dimension 1).
- Meaning: $A\langle -2, 1, 0\rangle = \vec{0}$, so if $\vec{x}$ solves $A\vec{x} = \vec{b}$, so does $\vec{x} + t\langle -2, 1, 0\rangle$.

**Code.**
```python
import numpy as np, sympy as sp
A = sp.Matrix([[1, 2, 3], [2, 4, 7]])
print(A.columnspace())     # [Matrix([1, 2]), Matrix([3, 7])]
print(A.nullspace())       # [Matrix([-2, 1, 0])]
from scipy.linalg import null_space
print(null_space(np.array([[1, 2, 3], [2, 4, 7]], dtype=float)))   # unit-length version
```
SymPy gives exact bases; SciPy's `null_space` returns an orthonormal basis numerically (a scaled version of $\langle -2, 1, 0\rangle$).

## 5. Common mistakes
- Using the pivot columns of the RREF instead of the original matrix for the column space.
- Mixing up which space lives where: $\text{Col}(A) \subseteq \mathbb{R}^m$ (outputs), $\text{Nul}(A) \subseteq \mathbb{R}^n$ (inputs) for an $m \times n$ matrix.
- Forgetting that $\vec{0}$ is always in the null space.

## 6. Practice
Q: What is the column space of $A$?
A: The span of its columns: all possible outputs $A\vec{x}$.

Q: What is the null space of $A$?
A: All $\vec{x}$ with $A\vec{x} = \vec{0}$.

Q: If $\text{Nul}(A) = \{\vec{0}\}$, what about solutions of $A\vec{x} = \vec{b}$?
A: There's at most one solution.

Q: Null space of $\begin{pmatrix} 1 & 1\end{pmatrix}$?
A: Span of $\langle -1, 1\rangle$.

Q: For a $3 \times 5$ matrix, where do the null space and column space live?
A: Null space in $\mathbb{R}^5$; column space in $\mathbb{R}^3$.

@@ 3 | TOPIC | Rank and the rank–nullity theorem | Rank as number of pivots, rank + nullity = number of columns, full rank, invertible matrix theorem | MUST
# Rank and the rank–nullity theorem

## 1. What it covers
- **Rank** = number of pivots = dimension of the column space.
- **Nullity** = dimension of the null space.
- **Rank–nullity theorem**: rank + nullity = number of columns.
- Full rank, and the **invertible matrix theorem** (many equivalent conditions).

## 2. Where it is used
- **ML and statistics:** a data matrix with rank less than its number of columns has redundant features.
- **Low-rank approximation:** recommender systems (Netflix-style) model the huge user × movie rating matrix as approximately low-rank.
- **Image compression:** keep a low-rank approximation of an image (SVD, Phase 6).
- **Control engineering:** rank tests for controllability.

## 3. What it means
**Rank** counts the truly independent directions among the columns (equivalently, among the rows): how many dimensions the outputs really fill.

**Rank–nullity:** every column (input dimension) either contributes a new output direction (a pivot) or becomes a free variable (a null-space direction). So
$$
\text{rank} + \text{nullity} = n \ (\text{number of columns})
$$
Input dimensions are either "kept" or "squashed".

**Invertible matrix theorem** (for square $n \times n$ $A$): all of these are equivalent:
$A$ is invertible ⇔ $\det A \ne 0$ ⇔ rank $= n$ ⇔ nullity $= 0$ ⇔ columns are independent ⇔ columns span $\mathbb{R}^n$ ⇔ $A\vec{x} = \vec{b}$ has exactly one solution for every $\vec{b}$.

## 4. Formula & worked example
$$
\text{rank}(A) + \text{nullity}(A) = n \qquad \text{rank}(A) = \text{rank}(A^T)
$$

**Example 1.** The previous topic's $A$ ($2 \times 3$): 2 pivots → rank 2, nullity $3 - 2 = 1$ ✓

**Example 2.** A $5 \times 8$ matrix with rank 5: nullity $= 3$; columns span $\mathbb{R}^5$, so every $A\vec{x} = \vec{b}$ is solvable, with infinitely many solutions.

**Example 3.** A $4 \times 4$ matrix with rank 3: not invertible; nullity 1; $\det = 0$.

**Example 4 (low-rank image, code).**
```python
import numpy as np
img = np.outer(np.linspace(0, 1, 100), np.linspace(1, 2, 80))   # a smooth 100x80 "image"
print(np.linalg.matrix_rank(img))    # 1: every row is a multiple of one row
```
An outer product of two vectors has rank 1: the whole 8000-pixel image is described by 180 numbers. Real images aren't exactly low-rank, but they're close, which is why SVD compression works.

## 5. Common mistakes
- Thinking rank can exceed the number of rows or columns (rank ≤ min(m, n)).
- Applying rank–nullity with the number of rows instead of columns.
- Calling a non-square matrix invertible.

## 6. Practice
Q: Define the rank of a matrix.
A: The number of pivots: the dimension of its column space.

Q: A $3 \times 7$ matrix has rank 3. Nullity?
A: $4$.

Q: Maximum possible rank of a $6 \times 4$ matrix?
A: $4$.

Q: A square matrix has nullity 0. Is it invertible?
A: Yes.

Q: Is $\text{rank}(A) = \text{rank}(A^T)$?
A: Yes (row rank equals column rank).

@@ 3 | TOPIC | Practice & review: Vector spaces | Mixed problem set with worked answers | MUST
# Practice & review: Vector spaces

**How to use this:** 40 minutes. Check with SymPy (`rref`, `nullspace`, `columnspace`, `rank`).

## Problems
1. Is $\langle 1, 5, 3\rangle$ in the span of $\langle 1, 1, 1\rangle$ and $\langle 0, 2, 1\rangle$?
2. Are $\langle 1, 2, 0\rangle$, $\langle 0, 1, 1\rangle$, $\langle 1, 0, -2\rangle$ independent?
3. Is $\{(x, y) : xy = 0\}$ a subspace of $\mathbb{R}^2$?
4. Find a basis and the dimension of the plane $2x - y + 3z = 0$.
5. For $A = \begin{pmatrix} 1 & 2 & 1 & 0 \\ 2 & 4 & 3 & 1 \\ 1 & 2 & 2 & 1\end{pmatrix}$ (RREF $\begin{pmatrix} 1 & 2 & 0 & -1 \\ 0 & 0 & 1 & 1 \\ 0 & 0 & 0 & 0\end{pmatrix}$), find rank, nullity, a basis for $\text{Col}(A)$ and $\text{Nul}(A)$.
6. A $7 \times 10$ matrix has a null space of dimension 4. What is its rank? Do its columns span $\mathbb{R}^7$?
7. Give three conditions equivalent to "the $3 \times 3$ matrix $A$ is invertible".

## Answers
1. $c_1 = 1$; $c_1 + 2c_2 = 5$ → $c_2 = 2$; check: $c_1 + c_2 = 3$ ✓ → yes.
2. $\det\begin{pmatrix} 1 & 0 & 1 \\ 2 & 1 & 0 \\ 0 & 1 & -2\end{pmatrix} = 1(-2 - 0) - 0 + 1(2 - 0) = 0$ → dependent (indeed $\vec{v}_3 = \vec{v}_1 - 2\vec{v}_2$).
3. No: $(1, 0) + (0, 1) = (1, 1)$ has $xy = 1 \ne 0$.
4. $y = 2x + 3z$: $x\langle 1, 2, 0\rangle + z\langle 0, 3, 1\rangle$ → basis $\{\langle 1, 2, 0\rangle, \langle 0, 3, 1\rangle\}$, dimension 2.
5. Rank 2, nullity 2. Col basis: columns 1 and 3 of $A$: $\langle 1, 2, 1\rangle$, $\langle 1, 3, 2\rangle$. Null: $x_1 = -2x_2 + x_4$, $x_3 = -x_4$ → basis $\langle -2, 1, 0, 0\rangle$, $\langle 1, 0, -1, 1\rangle$.
6. Rank $= 10 - 4 = 6 < 7$ → the columns don't span $\mathbb{R}^7$.
7. Any three of: $\det A \ne 0$; rank 3; null space $\{\vec{0}\}$; columns independent; columns span $\mathbb{R}^3$; $A\vec{x} = \vec{b}$ always has a unique solution.

## Flashcards
Q: Span?
A: The set of all linear combinations of the vectors.

Q: Basis?
A: An independent spanning set.

Q: Rank–nullity theorem?
A: rank + nullity = number of columns.

Q: Column space basis from RREF?
A: The pivot columns of the original matrix.

@@ 1 | MODULE | Phase 4 — Linear Transformations (Core) | Linear maps, matrices of transformations, 2D/3D geometric transformations, composition and inverses, change of basis | MUST
# Phase 4: Linear transformations

A matrix is not just a table: it's a **function that moves vectors**, stretching, rotating, reflecting and shearing space while keeping lines straight and the origin fixed. This viewpoint explains matrix multiplication, determinants and eigenvectors, and it's exactly how graphics engines and game engines position everything on screen.

@@ 2 | MODULE | Transformations and coordinates | Linear transformations, standard matrices, geometric transformations, composition, change of basis | MUST
Every linear transformation is a matrix, and every matrix is a linear transformation.

@@ 3 | TOPIC | Linear transformations and their matrices | Definition, linearity properties, the standard matrix from images of basis vectors | MUST
# Linear transformations and their matrices

## 1. What it covers
- A **linear transformation** $T$: $T(\vec{u} + \vec{v}) = T(\vec{u}) + T(\vec{v})$ and $T(c\vec{u}) = cT(\vec{u})$.
- Every linear transformation $\mathbb{R}^n \to \mathbb{R}^m$ is $T(\vec{x}) = A\vec{x}$.
- Finding $A$: its columns are where the basis vectors go.
- Examples and non-examples.

## 2. Where it is used
- **Graphics:** every rotation, scale and shear of a model is a linear transformation (translation is handled with a trick; next topic).
- **ML:** each dense neural-network layer applies a linear transformation (followed by a non-linear activation).
- **Signal processing:** filters and the Fourier transform are linear.
- **Physics:** many laws are linear (Hooke's law, linear circuits).

## 3. What it means
A transformation is **linear** if it respects addition and scaling. Geometrically: grid lines stay straight, parallel and evenly spaced, and the origin stays put.

**Key insight:** a linear transformation is completely determined by what it does to the basis vectors $\vec{e}_1 = \langle 1, 0\rangle$ and $\vec{e}_2 = \langle 0, 1\rangle$. Every vector is $x\vec{e}_1 + y\vec{e}_2$, so by linearity $T(\vec{x}) = xT(\vec{e}_1) + yT(\vec{e}_2)$. Therefore:

**The columns of $A$ are $T(\vec{e}_1), T(\vec{e}_2), \ldots$**

## 4. Formula & worked example
$$
A = \big[\,T(\vec{e}_1)\ \ T(\vec{e}_2)\ \cdots\ T(\vec{e}_n)\,\big]
$$

**Example 1.** $T$ doubles x and triples y: $T(\vec{e}_1) = \langle 2, 0\rangle$, $T(\vec{e}_2) = \langle 0, 3\rangle$ → $A = \begin{pmatrix} 2 & 0 \\ 0 & 3\end{pmatrix}$.

**Example 2.** $T$ sends $\vec{e}_1 \to \langle 1, 1\rangle$ and $\vec{e}_2 \to \langle -1, 2\rangle$. $T\langle 3, 2\rangle = 3\langle 1, 1\rangle + 2\langle -1, 2\rangle = \langle 1, 7\rangle$.

**Example 3 (not linear).** $T(x, y) = (x + 1, y)$: $T(\vec{0}) = (1, 0) \ne \vec{0}$ → not linear (a translation).

**Example 4 (not linear).** $T(x, y) = (x^2, y)$: $T(2\vec{e}_1) = (4, 0) \ne 2T(\vec{e}_1) = (2, 0)$ → not linear.

**Example 5 (projection is linear).** Projection onto the x-axis: $\vec{e}_1 \to \langle 1, 0\rangle$, $\vec{e}_2 \to \langle 0, 0\rangle$ → $\begin{pmatrix} 1 & 0 \\ 0 & 0\end{pmatrix}$.

## 5. Common mistakes
- Calling translations linear (they move the origin).
- Writing the images of basis vectors as **rows** instead of columns.
- Assuming any formula with $x$ and $y$ is linear (powers, products and constants break linearity).

## 6. Practice
Q: Two properties of a linear transformation?
A: $T(\vec{u} + \vec{v}) = T\vec{u} + T\vec{v}$ and $T(c\vec{u}) = cT\vec{u}$.

Q: How do you find the matrix of a linear transformation?
A: Its columns are the images of the standard basis vectors.

Q: Is $T(x, y) = (2x - y, 3y)$ linear? Matrix?
A: Yes; $\begin{pmatrix} 2 & -1 \\ 0 & 3\end{pmatrix}$.

Q: Is $T(x, y) = (xy, x)$ linear?
A: No.

Q: Must a linear transformation send $\vec{0}$ to $\vec{0}$?
A: Yes.

@@ 3 | TOPIC | Geometric transformations in 2D and 3D | Rotation, reflection, scaling, shear, projection matrices; homogeneous coordinates for translation | MUST
# Geometric transformations in 2D and 3D

## 1. What it covers
- Standard matrices for **rotation**, **reflection**, **scaling**, **shear** and **projection**.
- What each does to areas (determinant).
- **Homogeneous coordinates**: handling translation with a $3 \times 3$ (2D) or $4 \times 4$ (3D) matrix.
- 3D rotations about the axes.

## 2. Where it is used
- **Game engines and graphics APIs** (OpenGL, WebGL, Unity, three.js): model, view and projection matrices are $4 \times 4$ matrices in homogeneous coordinates.
- **CSS:** `transform: matrix(a, b, c, d, e, f)` is a 2D affine matrix.
- **Image processing:** rotating, scaling and deskewing images.
- **Robotics:** transforming between coordinate frames.

## 3. What it means
Using "columns = images of basis vectors":
- **Rotation by $\theta$:** $\vec{e}_1 \to (\cos\theta, \sin\theta)$, $\vec{e}_2 \to (-\sin\theta, \cos\theta)$. Determinant 1 (areas preserved).
- **Scaling** by $s_x, s_y$: diagonal matrix; determinant $s_xs_y$.
- **Reflection** in the x-axis: $\vec{e}_2 \to -\vec{e}_2$; determinant $-1$.
- **Shear** (horizontal): $\vec{e}_2 \to (k, 1)$; slants shapes, determinant 1.

**Translation isn't linear**, but there's a trick: add a third coordinate equal to 1, writing $(x, y)$ as $(x, y, 1)$. Then translation by $(t_x, t_y)$ becomes a matrix multiplication, so rotations, scales and translations can all be combined into **one** matrix.

## 4. Formula & worked example
$$
R_\theta = \begin{pmatrix} \cos\theta & -\sin\theta \\ \sin\theta & \cos\theta\end{pmatrix} \quad S = \begin{pmatrix} s_x & 0 \\ 0 & s_y\end{pmatrix} \quad H = \begin{pmatrix} 1 & k \\ 0 & 1\end{pmatrix} \quad T = \begin{pmatrix} 1 & 0 & t_x \\ 0 & 1 & t_y \\ 0 & 0 & 1\end{pmatrix}
$$

**Example 1.** Rotate $\langle 3, 1\rangle$ by $90^\circ$: $R_{90} = \begin{pmatrix} 0 & -1 \\ 1 & 0\end{pmatrix}$ → $\langle -1, 3\rangle$.

**Example 2.** Reflection in $y = x$: $\vec{e}_1 \to \vec{e}_2$, $\vec{e}_2 \to \vec{e}_1$ → $\begin{pmatrix} 0 & 1 \\ 1 & 0\end{pmatrix}$.

**Example 3 (shear).** $H$ with $k = 0.5$ sends the unit square's corner $(1, 1)$ to $(1.5, 1)$: the square becomes a parallelogram of the same area.

**Example 4 (homogeneous).** Translate $(2, 3)$ by $(5, -1)$: $\begin{pmatrix} 1 & 0 & 5 \\ 0 & 1 & -1 \\ 0 & 0 & 1\end{pmatrix}\begin{pmatrix} 2 \\ 3 \\ 1\end{pmatrix} = \begin{pmatrix} 7 \\ 2 \\ 1\end{pmatrix}$ → $(7, 2)$.

**Example 5 (code: rotate a sprite about its centre).**
```python
import numpy as np
def T(tx, ty): return np.array([[1, 0, tx], [0, 1, ty], [0, 0, 1]])
def R(deg):
    t = np.radians(deg); c, s = np.cos(t), np.sin(t)
    return np.array([[c, -s, 0], [s, c, 0], [0, 0, 1]])
center = (5, 5)
M = T(*center) @ R(90) @ T(-center[0], -center[1])   # move to origin, rotate, move back
corner = np.array([6, 5, 1])
print((M @ corner).round(6))                          # [5. 6. 1.]
```
Reading right to left: translate the centre to the origin, rotate, translate back. Multiplying the three matrices gives one matrix `M` that does everything; a game applies such a matrix to every vertex.

## 5. Common mistakes
- Rotating clockwise with the standard matrix (positive $\theta$ is anticlockwise; screen coordinates with y pointing down flip this).
- Forgetting the 1 in homogeneous coordinates.
- Reading a matrix product in the wrong order (the rightmost matrix acts first).

## 6. Practice
Q: Matrix for rotation by $180^\circ$?
A: $\begin{pmatrix} -1 & 0 \\ 0 & -1\end{pmatrix}$.

Q: Matrix for reflection in the y-axis?
A: $\begin{pmatrix} -1 & 0 \\ 0 & 1\end{pmatrix}$.

Q: Determinant of any rotation matrix?
A: $1$.

Q: Why are homogeneous coordinates used in graphics?
A: They let translations be written as matrix multiplications, so all transformations combine into one matrix.

Q: In $M = ABC$ applied to $\vec{x}$, which acts first?
A: $C$.

@@ 3 | TOPIC | Composition, inverses and matrix products as transformations | Composing transformations, why order matters, inverse transformations, determinants of products | MUST
# Composition, inverses and matrix products as transformations

## 1. What it covers
- **Composing** transformations = multiplying their matrices.
- Why $AB \ne BA$ geometrically.
- **Inverse** transformations = inverse matrices.
- Determinants multiply under composition.

## 2. Where it is used
- **Graphics pipelines:** a vertex is transformed by projection × view × model matrices.
- **Robotics:** forward kinematics multiplies joint transformation matrices along the arm.
- **Animation rigs:** parent–child bone transforms.
- **Neural networks:** stacking linear layers without activations collapses into a single matrix, which is why non-linear activations are needed.

## 3. What it means
Applying $B$ then $A$ to $\vec{x}$ gives $A(B\vec{x}) = (AB)\vec{x}$: the product $AB$ is the combined transformation, with the **rightmost acting first**.

**Order matters:** rotate 90° then reflect in the x-axis gives a different result from reflecting then rotating. Matrix multiplication mirrors this.

**Inverse:** $A^{-1}$ undoes $A$. A rotation's inverse rotates back ($R_\theta^{-1} = R_{-\theta} = R_\theta^T$); a scale by 2 is undone by a scale by $\frac{1}{2}$. Squashing transformations (det 0) can't be undone.

**Determinants:** areas scale by $\det B$, then by $\det A$ → $\det(AB) = \det A\det B$.

**Stacked linear layers:** $W_2(W_1\vec{x}) = (W_2W_1)\vec{x}$, just one linear map. Activations like ReLU between layers are what give deep networks their power.

## 4. Formula & worked example
$$
(A \circ B)(\vec{x}) = A(B\vec{x}) = (AB)\vec{x} \qquad (AB)^{-1} = B^{-1}A^{-1}
$$

**Example 1.** Rotate 90° ($R$), then reflect in the x-axis ($F$):
- $FR = \begin{pmatrix} 1 & 0 \\ 0 & -1\end{pmatrix}\begin{pmatrix} 0 & -1 \\ 1 & 0\end{pmatrix} = \begin{pmatrix} 0 & -1 \\ -1 & 0\end{pmatrix}$ (reflection in $y = -x$)
- Reflect first, then rotate: $RF = \begin{pmatrix} 0 & 1 \\ 1 & 0\end{pmatrix}$ (reflection in $y = x$). Different!

**Example 2.** Two rotations: $R_{30}R_{60} = R_{90}$ (angles add), and rotations about the origin commute.

**Example 3 (inverse).** Undo "scale by 2 then rotate 45°": $(R_{45}S_2)^{-1} = S_{1/2}R_{-45}$ (undo the rotation first).

**Example 4 (determinant).** Scale by 3 (det 9) then shear (det 1): areas scale by 9.

**Example 5 (collapse, code).**
```python
import numpy as np
W1, W2 = np.random.randn(5, 3), np.random.randn(2, 5)
x = np.random.randn(3)
print(np.allclose(W2 @ (W1 @ x), (W2 @ W1) @ x))   # True: two linear layers = one 2x3 matrix
```
Without a non-linearity between them, any stack of linear layers equals a single matrix, so it can only learn linear functions.

## 5. Common mistakes
- Multiplying matrices in the order the transformations are described (it's the reverse).
- Forgetting to reverse the order for inverses.
- Assuming all transformations commute.

## 6. Practice
Q: Matrix for "do $B$, then $A$"?
A: $AB$.

Q: Inverse of rotation by $\theta$?
A: Rotation by $-\theta$ (also $R_\theta^T$).

Q: Do reflections and rotations commute in general?
A: No.

Q: $\det A = 2$, $\det B = 5$. Area scale of $AB$?
A: $10$.

Q: Why do neural networks need non-linear activations?
A: Without them, stacked linear layers collapse into one linear transformation.

@@ 3 | TOPIC | Change of basis | Coordinates in a new basis, change-of-basis matrices, the same transformation in different coordinates | SHOULD
# Change of basis

## 1. What it covers
- **Coordinates** of a vector relative to a basis $B$: $[\vec{x}]_B$.
- The **change-of-basis matrix** $P$ (columns = new basis vectors): $\vec{x} = P[\vec{x}]_B$.
- Converting a transformation's matrix to a new basis: $P^{-1}AP$.

## 2. Where it is used
- **Graphics and robotics:** world, camera and object coordinate frames.
- **Data science:** PCA re-expresses data in a basis of principal components.
- **Signal processing:** the Fourier transform changes to a basis of sine waves; JPEG uses a cosine basis.
- **Physics:** choosing axes aligned with symmetry to simplify problems.
- **Diagonalisation** (Phase 5) is a change of basis.

## 3. What it means
The same arrow can be described with different "rulers". In the standard basis, $\langle 3, 1\rangle$ means 3 steps along $\vec{e}_1$ and 1 along $\vec{e}_2$. In another basis $\{\vec{b}_1, \vec{b}_2\}$, the same arrow has different coordinates.

Like giving directions: "3 km east, 1 km north" vs "2 blocks along Main Street, 1 block along Park Road". Same place, different description.

- **New → standard:** multiply by $P$ (whose columns are the new basis vectors in standard coordinates).
- **Standard → new:** multiply by $P^{-1}$.

A transformation $A$ (in standard coordinates) looks like $P^{-1}AP$ in the new basis: convert to standard, apply $A$, convert back. A good basis can make the matrix much simpler (even diagonal).

## 4. Formula & worked example
$$
\vec{x} = P[\vec{x}]_B \qquad [\vec{x}]_B = P^{-1}\vec{x} \qquad [T]_B = P^{-1}AP
$$

**Example 1.** Basis $\vec{b}_1 = \langle 1, 1\rangle$, $\vec{b}_2 = \langle 1, -1\rangle$; $P = \begin{pmatrix} 1 & 1 \\ 1 & -1\end{pmatrix}$. Coordinates of $\vec{x} = \langle 3, 1\rangle$?
- $P^{-1} = \frac{1}{2}\begin{pmatrix} 1 & 1 \\ 1 & -1\end{pmatrix}$ → $[\vec{x}]_B = \frac{1}{2}\langle 4, 2\rangle = \langle 2, 1\rangle$. Check: $2\langle 1, 1\rangle + \langle 1, -1\rangle = \langle 3, 1\rangle$ ✓

**Example 2.** $[\vec{x}]_B = \langle -1, 4\rangle$ → $\vec{x} = -\langle 1, 1\rangle + 4\langle 1, -1\rangle = \langle 3, -5\rangle$.

**Example 3 (simpler matrix).** Reflection in $y = x$ has standard matrix $A = \begin{pmatrix} 0 & 1 \\ 1 & 0\end{pmatrix}$. In the basis above (along and across the mirror line):
- $P^{-1}AP = \begin{pmatrix} 1 & 0 \\ 0 & -1\end{pmatrix}$: "keep the component along the mirror, flip the one across it". Much clearer.

**Example 4 (code).**
```python
import numpy as np
P = np.array([[1, 1], [1, -1]], dtype=float)
A = np.array([[0, 1], [1, 0]], dtype=float)
print(np.linalg.solve(P, [3, 1]))        # [2. 1.]  coordinates in the new basis
print(np.linalg.inv(P) @ A @ P)          # [[1, 0], [0, -1]]
```

## 5. Common mistakes
- Using $P$ where $P^{-1}$ is needed (and vice versa).
- Putting basis vectors in rows instead of columns.
- Writing $PAP^{-1}$ instead of $P^{-1}AP$ (it depends on the convention; check with a test vector).

## 6. Practice
Q: Columns of the change-of-basis matrix $P$?
A: The new basis vectors written in standard coordinates.

Q: Basis $\{\langle 2, 0\rangle, \langle 0, 3\rangle\}$. Coordinates of $\langle 4, 9\rangle$?
A: $\langle 2, 3\rangle$.

Q: Formula for a transformation's matrix in basis $B$?
A: $P^{-1}AP$.

Q: Why change basis?
A: To make vectors or transformations simpler to describe.

Q: Name an everyday technology that relies on a change of basis.
A: JPEG compression (cosine basis); also the Fourier transform in audio.

@@ 3 | TOPIC | Practice & review: Linear transformations | Mixed problem set with worked answers | MUST
# Practice & review: Linear transformations

**How to use this:** 40 minutes; verify with NumPy.

## Problems
1. Is $T(x, y) = (3x - y, x + 2y)$ linear? Give its matrix and compute $T(2, -1)$.
2. $T$ sends $\vec{e}_1 \to \langle 2, 1, 0\rangle$ and $\vec{e}_2 \to \langle -1, 0, 3\rangle$. Find its matrix and $T\langle 1, 2\rangle$.
3. Write the matrix that rotates by $60^\circ$ and apply it to $\langle 2, 0\rangle$.
4. Find the matrix that first scales x by 2, then rotates by $90^\circ$. Does the order matter here?
5. Write a $3 \times 3$ homogeneous matrix that rotates by $90^\circ$ and then translates by $(4, 1)$. Apply it to $(1, 0)$.
6. Basis $B = \{\langle 2, 1\rangle, \langle 1, 1\rangle\}$. Find $[\langle 5, 3\rangle]_B$.
7. A transformation has determinant 0. What happens to the unit square? Can it be undone?

## Answers
1. Yes; $\begin{pmatrix} 3 & -1 \\ 1 & 2\end{pmatrix}$; $T(2, -1) = (7, 0)$.
2. $\begin{pmatrix} 2 & -1 \\ 1 & 0 \\ 0 & 3\end{pmatrix}$; $\langle 0, 1, 6\rangle$.
3. $\begin{pmatrix} 0.5 & -\frac{\sqrt{3}}{2} \\ \frac{\sqrt{3}}{2} & 0.5\end{pmatrix}$; $\langle 1, \sqrt{3}\rangle$.
4. $R_{90}S = \begin{pmatrix} 0 & -1 \\ 1 & 0\end{pmatrix}\begin{pmatrix} 2 & 0 \\ 0 & 1\end{pmatrix} = \begin{pmatrix} 0 & -1 \\ 2 & 0\end{pmatrix}$; $SR_{90} = \begin{pmatrix} 0 & -2 \\ 1 & 0\end{pmatrix}$ → yes, order matters.
5. $T \cdot R = \begin{pmatrix} 0 & -1 & 4 \\ 1 & 0 & 1 \\ 0 & 0 & 1\end{pmatrix}$; $(1, 0) \to (4, 2)$.
6. $2c_1 + c_2 = 5$, $c_1 + c_2 = 3$ → $c_1 = 2$, $c_2 = 1$ → $\langle 2, 1\rangle$.
7. It's squashed onto a line or a point (zero area); it can't be undone (not invertible).

## Flashcards
Q: Columns of a transformation's matrix?
A: The images of the standard basis vectors.

Q: 2D rotation matrix?
A: $\begin{pmatrix} \cos\theta & -\sin\theta \\ \sin\theta & \cos\theta\end{pmatrix}$.

Q: Matrix of "$B$ then $A$"?
A: $AB$.

Q: Why use homogeneous coordinates?
A: To express translation as a matrix multiplication.
