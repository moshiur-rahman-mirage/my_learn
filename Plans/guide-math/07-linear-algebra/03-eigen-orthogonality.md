@@ 1 | MODULE | Phase 5 — Eigenvalues and Eigenvectors (Advanced) | Eigenvalues, characteristic polynomial, eigenvectors and eigenspaces, diagonalisation, matrix powers, Markov chains and PageRank | MUST
# Phase 5: Eigenvalues and eigenvectors

Most vectors change direction when a matrix acts on them. **Eigenvectors** are the special ones that only get stretched or shrunk; the stretch factor is the **eigenvalue**. They reveal a transformation's "natural axes" and explain long-term behaviour: Google's original PageRank, population models, vibration modes of buildings, stability of systems and principal component analysis are all eigenvalue problems.

@@ 2 | MODULE | Eigen-analysis | Eigenvalues, eigenvectors, diagonalisation, applications | MUST
Find the directions a matrix leaves unrotated, and everything about it becomes simpler.

@@ 3 | TOPIC | Eigenvalues and the characteristic polynomial | Av = λv, det(A − λI) = 0, trace and determinant shortcuts, complex eigenvalues | MUST
# Eigenvalues and the characteristic polynomial

## 1. What it covers
- The eigen-equation $A\vec{v} = \lambda\vec{v}$.
- Finding eigenvalues from the **characteristic equation** $\det(A - \lambda I) = 0$.
- Shortcuts: sum of eigenvalues = **trace**, product = **determinant**.
- Triangular matrices; complex eigenvalues (rotations).

## 2. Where it is used
- **Engineering:** natural frequencies of bridges and buildings (eigenvalues of the stiffness/mass system) to avoid resonance.
- **Stability:** a system $\vec{x}_{k+1} = A\vec{x}_k$ settles down if all $\lvert\lambda\rvert < 1$.
- **Machine learning:** PCA uses eigenvalues of the covariance matrix; the Hessian's eigenvalues describe the shape of a loss surface.
- **Quantum mechanics:** measurable quantities are eigenvalues.

## 3. What it means
Apply $A$ to most vectors and they turn to point somewhere new. An **eigenvector** $\vec{v}$ (non-zero) stays on its own line: $A\vec{v}$ is just $\vec{v}$ scaled by $\lambda$.
- $\lambda > 1$: stretched; $0 < \lambda < 1$: shrunk; $\lambda < 0$: flipped; $\lambda = 0$: squashed to zero.

Imagine stretching a rubber sheet: most marks on it turn as well as move, but along certain directions the marks only slide outward. Those are the eigen-directions.

**Finding eigenvalues:** $A\vec{v} = \lambda\vec{v}$ means $(A - \lambda I)\vec{v} = \vec{0}$ has a non-zero solution, so $A - \lambda I$ must be **non-invertible**: $\det(A - \lambda I) = 0$. This polynomial equation in $\lambda$ is the characteristic equation.

For a $2 \times 2$ matrix: $\lambda^2 - (\text{trace})\lambda + \det = 0$.

## 4. Formula & worked example
$$
A\vec{v} = \lambda\vec{v} \qquad \det(A - \lambda I) = 0 \qquad \lambda_1 + \cdots + \lambda_n = \text{tr}(A) \qquad \lambda_1\cdots\lambda_n = \det A
$$

**Example 1.** $A = \begin{pmatrix} 4 & 1 \\ 2 & 3\end{pmatrix}$: trace 7, det 10 → $\lambda^2 - 7\lambda + 10 = 0$ → $\lambda = 5, 2$.

**Example 2 (triangular).** $\begin{pmatrix} 3 & 7 \\ 0 & -1\end{pmatrix}$: eigenvalues are the diagonal entries, $3$ and $-1$.

**Example 3 (rotation).** $R_{90} = \begin{pmatrix} 0 & -1 \\ 1 & 0\end{pmatrix}$: $\lambda^2 + 1 = 0$ → $\lambda = \pm i$. Complex eigenvalues: no real direction is left unrotated (makes sense for a rotation).

**Example 4 (check).** $A\begin{pmatrix} 1 \\ 1\end{pmatrix} = \begin{pmatrix} 5 \\ 5\end{pmatrix} = 5\begin{pmatrix} 1 \\ 1\end{pmatrix}$ ✓ for Example 1.

**Example 5 (code).**
```python
import numpy as np
A = np.array([[4, 1], [2, 3]], dtype=float)
vals, vecs = np.linalg.eig(A)
print(vals)                # [5. 2.]
print(vecs)                # columns are unit eigenvectors
```
`np.linalg.eig` returns the eigenvalues and a matrix whose **columns** are the corresponding eigenvectors (normalised to length 1). For symmetric matrices, use `np.linalg.eigh`, which is faster and more accurate.

## 5. Common mistakes
- Solving $\det(A) - \lambda = 0$ instead of $\det(A - \lambda I) = 0$.
- Allowing $\vec{v} = \vec{0}$ as an eigenvector (it's excluded by definition).
- Reading eigenvectors from rows of NumPy's output instead of columns.

## 6. Practice
Q: Definition of an eigenvector?
A: A non-zero $\vec{v}$ with $A\vec{v} = \lambda\vec{v}$.

Q: Eigenvalues of $\begin{pmatrix} 2 & 0 \\ 0 & 7\end{pmatrix}$?
A: $2$ and $7$.

Q: Eigenvalues of $\begin{pmatrix} 1 & 2 \\ 2 & 1\end{pmatrix}$?
A: $\lambda^2 - 2\lambda - 3 = 0$ → $3$ and $-1$.

Q: Sum and product of the eigenvalues equal?
A: The trace and the determinant.

Q: What does an eigenvalue of 0 tell you?
A: The matrix is not invertible.

@@ 3 | TOPIC | Eigenvectors and eigenspaces | Solving (A − λI)v = 0, eigenspaces as null spaces, geometric meaning, symmetric matrices | MUST
# Eigenvectors and eigenspaces

## 1. What it covers
- Finding eigenvectors by solving $(A - \lambda I)\vec{v} = \vec{0}$.
- The **eigenspace** of $\lambda$: the null space of $A - \lambda I$.
- Geometric interpretation of eigenvectors for common transformations.
- Symmetric matrices: real eigenvalues and **orthogonal** eigenvectors.

## 2. Where it is used
- **PCA:** eigenvectors of the covariance matrix are the principal directions of the data.
- **Vibration analysis:** eigenvectors are the mode shapes (how a structure wobbles).
- **Graphs:** eigenvectors of a graph's Laplacian are used for spectral clustering and community detection.
- **Computer vision:** "eigenfaces" for face recognition.

## 3. What it means
Once you have an eigenvalue $\lambda$, its eigenvectors are the non-zero solutions of $(A - \lambda I)\vec{v} = \vec{0}$: a null-space problem. Any non-zero multiple of an eigenvector is also an eigenvector (same line), so we usually give one representative.

**Geometric examples:**
- Projection onto a line: vectors **on** the line have $\lambda = 1$; vectors **perpendicular** have $\lambda = 0$.
- Reflection across a line: on the line $\lambda = 1$; perpendicular $\lambda = -1$.
- Scaling by 3: every vector is an eigenvector with $\lambda = 3$.

**Symmetric matrices** ($A = A^T$) always have real eigenvalues, and eigenvectors for different eigenvalues are perpendicular. This is the basis of PCA.

## 4. Formula & worked example
$$
E_\lambda = \text{Nul}(A - \lambda I)
$$

**Example 1.** $A = \begin{pmatrix} 4 & 1 \\ 2 & 3\end{pmatrix}$ ($\lambda = 5, 2$).
- $\lambda = 5$: $A - 5I = \begin{pmatrix} -1 & 1 \\ 2 & -2\end{pmatrix}$ → $-v_1 + v_2 = 0$ → $\vec{v} = \langle 1, 1\rangle$.
- $\lambda = 2$: $A - 2I = \begin{pmatrix} 2 & 1 \\ 2 & 1\end{pmatrix}$ → $2v_1 + v_2 = 0$ → $\vec{v} = \langle 1, -2\rangle$.

**Example 2 (symmetric).** $\begin{pmatrix} 2 & 1 \\ 1 & 2\end{pmatrix}$: $\lambda = 3$ with $\langle 1, 1\rangle$; $\lambda = 1$ with $\langle 1, -1\rangle$. These are perpendicular ✓.

**Example 3 (reflection).** Reflection in $y = x$: $\begin{pmatrix} 0 & 1 \\ 1 & 0\end{pmatrix}$ has $\lambda = 1$ with $\langle 1, 1\rangle$ (on the mirror) and $\lambda = -1$ with $\langle 1, -1\rangle$ (across it).

**Example 4 (repeated eigenvalue).** $\begin{pmatrix} 2 & 1 \\ 0 & 2\end{pmatrix}$: $\lambda = 2$ (twice), but $A - 2I = \begin{pmatrix} 0 & 1 \\ 0 & 0\end{pmatrix}$ gives only one eigenvector direction $\langle 1, 0\rangle$ (a shear).

## 5. Common mistakes
- Arithmetic errors in $A - \lambda I$ (subtract $\lambda$ only on the diagonal).
- Expecting $n$ independent eigenvectors always (Example 4 has only one).
- Treating different scalings of the same eigenvector as different eigenvectors.

## 6. Practice
Q: How do you find eigenvectors for a known $\lambda$?
A: Solve $(A - \lambda I)\vec{v} = \vec{0}$.

Q: Eigenvector of $\begin{pmatrix} 3 & 0 \\ 0 & 5\end{pmatrix}$ for $\lambda = 5$?
A: $\langle 0, 1\rangle$.

Q: Eigenvalues of a projection matrix?
A: $0$ and $1$.

Q: What's special about eigenvectors of symmetric matrices?
A: Eigenvalues are real and eigenvectors of distinct eigenvalues are orthogonal.

Q: Eigenvector of $\begin{pmatrix} 1 & 2 \\ 2 & 1\end{pmatrix}$ for $\lambda = 3$?
A: $\langle 1, 1\rangle$.

@@ 3 | TOPIC | Diagonalisation and matrix powers | A = PDP⁻¹, when a matrix is diagonalisable, computing Aᵏ, Fibonacci, long-run behaviour | MUST
# Diagonalisation and matrix powers

## 1. What it covers
- **Diagonalisation** $A = PDP^{-1}$ ($P$ = eigenvectors as columns, $D$ = eigenvalues on the diagonal).
- When it's possible ($n$ independent eigenvectors).
- Fast **matrix powers**: $A^k = PD^kP^{-1}$.
- Long-run behaviour from the largest eigenvalue; closed form for Fibonacci numbers.

## 2. Where it is used
- **Dynamical systems:** population models, Markov chains, economic models $\vec{x}_{k+1} = A\vec{x}_k$.
- **Differential equations:** systems $\vec{x}' = A\vec{x}$ decouple in the eigenbasis (roadmap 08).
- **Algorithms:** computing the $n$th Fibonacci number or linear recurrences quickly.
- **PCA and spectral methods:** symmetric matrices diagonalise with orthogonal $P$.

## 3. What it means
In the basis of eigenvectors, $A$ acts by simply **scaling each axis** by its eigenvalue: it's a diagonal matrix there. $P$ converts eigen-coordinates to standard coordinates; $P^{-1}$ converts back:
$$
A = PDP^{-1}
$$

**Powers:** $A^k = PD^kP^{-1}$, and $D^k$ just raises each diagonal entry to the $k$th power. Applying $A$ a thousand times becomes trivial.

**Long-run behaviour:** after many steps, the component along the eigenvector with the **largest $\lvert\lambda\rvert$** dominates. If that $\lambda > 1$, things grow; if all $\lvert\lambda\rvert < 1$, everything decays to 0.

## 4. Formula & worked example
$$
A = PDP^{-1} \qquad A^k = PD^kP^{-1}
$$

**Example 1.** $A = \begin{pmatrix} 4 & 1 \\ 2 & 3\end{pmatrix}$: $P = \begin{pmatrix} 1 & 1 \\ 1 & -2\end{pmatrix}$, $D = \begin{pmatrix} 5 & 0 \\ 0 & 2\end{pmatrix}$.
- $A^{10} = P\begin{pmatrix} 5^{10} & 0 \\ 0 & 2^{10}\end{pmatrix}P^{-1}$: dominated by $5^{10}$; long-run vectors point along $\langle 1, 1\rangle$.

**Example 2 (population).** Juveniles and adults: $\vec{x}_{k+1} = \begin{pmatrix} 0 & 2 \\ 0.3 & 0.5\end{pmatrix}\vec{x}_k$ (adults have 2 young per year; 30% of juveniles survive to adulthood; 50% of adults survive).
- $\lambda^2 - 0.5\lambda - 0.6 = 0$ → $\lambda \approx 1.06$ and $-0.56$ → the population grows about 6% per year in the long run.

**Example 3 (Fibonacci).** $\begin{pmatrix} F_{n+1} \\ F_n\end{pmatrix} = \begin{pmatrix} 1 & 1 \\ 1 & 0\end{pmatrix}\begin{pmatrix} F_n \\ F_{n-1}\end{pmatrix}$; eigenvalues $\frac{1 \pm \sqrt{5}}{2}$ → $F_n = \frac{\varphi^n - \psi^n}{\sqrt{5}}$ with $\varphi \approx 1.618$ (the golden ratio). The ratio of consecutive Fibonacci numbers approaches $\varphi$.

**Example 4 (not diagonalisable).** $\begin{pmatrix} 2 & 1 \\ 0 & 2\end{pmatrix}$ has only one independent eigenvector → can't be diagonalised.

**Example 5 (code).**
```python
import numpy as np
A = np.array([[0, 2], [0.3, 0.5]])
vals, P = np.linalg.eig(A)
print(vals)                                    # [-0.56..  1.06..]
x = np.array([100, 100.])
for _ in range(50): x = A @ x
print(x / x.sum())                             # stable age distribution (along the dominant eigenvector)
```
After many steps the proportions of juveniles and adults stop changing: the vector has lined up with the eigenvector of the largest eigenvalue.

## 5. Common mistakes
- Putting eigenvectors in $P$ in a different order than their eigenvalues in $D$.
- Assuming every matrix is diagonalisable.
- Writing $A^k = P^kD^kP^{-k}$ (only $D$ is raised to the power).

## 6. Practice
Q: In $A = PDP^{-1}$, what are the columns of $P$ and the entries of $D$?
A: Eigenvectors of $A$; the matching eigenvalues.

Q: When is an $n \times n$ matrix diagonalisable?
A: When it has $n$ linearly independent eigenvectors.

Q: Why is $A^k$ easy after diagonalising?
A: $A^k = PD^kP^{-1}$ and $D^k$ just powers the diagonal entries.

Q: A system's largest eigenvalue is 0.9 (others smaller). Long-run behaviour?
A: It decays to zero.

Q: Is a matrix with distinct eigenvalues always diagonalisable?
A: Yes.

@@ 3 | TOPIC | Eigenvector applications: Markov chains and PageRank | Transition matrices, steady-state vectors as eigenvectors for λ = 1, Google PageRank | SHOULD
# Eigenvector applications: Markov chains and PageRank

## 1. What it covers
- **Markov chains**: systems that move between states with fixed probabilities.
- **Transition matrices** (columns sum to 1).
- The **steady state**: the eigenvector for $\lambda = 1$.
- **PageRank**: ranking web pages as a Markov chain.

## 2. Where it is used
- **Search engines:** Google's original PageRank algorithm.
- **Customer analytics:** churn and brand-switching models; customer lifetime value.
- **Natural language:** early text generators and autocomplete (n-gram Markov models).
- **Finance:** credit-rating transitions.
- **Queueing and reliability:** machine states (working, degraded, broken).

## 3. What it means
A **Markov chain** has a set of states, and each step it moves randomly according to fixed probabilities that depend only on the current state. Write them in a **transition matrix** $P$ where column $j$ holds the probabilities of moving **from** state $j$ (so each column sums to 1). Then
$$
\vec{x}_{k+1} = P\vec{x}_k
$$
where $\vec{x}_k$ is the probability distribution after $k$ steps.

**Steady state:** for many chains, the distribution settles to $\vec{q}$ with $P\vec{q} = \vec{q}$: an eigenvector with eigenvalue 1, scaled so its entries sum to 1.

**PageRank:** imagine a random surfer clicking links. A page's rank is the long-run fraction of time the surfer spends there: the steady state of the "web Markov chain". With a small chance of jumping to a random page (damping, usually 0.85 follow / 0.15 jump), the steady state always exists and is unique.

## 4. Formula & worked example
$$
P\vec{q} = \vec{q},\quad \sum q_i = 1 \qquad \text{PageRank: } \vec{r} = dM\vec{r} + \frac{1 - d}{n}\vec{1}
$$

**Example 1 (brand switching).** Each month 90% of brand A users stay and 10% switch to B; 80% of B users stay and 20% switch to A.
- $P = \begin{pmatrix} 0.9 & 0.2 \\ 0.1 & 0.8\end{pmatrix}$. Steady state: $(P - I)\vec{q} = 0$ → $-0.1q_A + 0.2q_B = 0$ → $q_A = 2q_B$ → $\vec{q} = \langle \frac{2}{3}, \frac{1}{3}\rangle$.
- Long-run market share: A 66.7%, B 33.3%, whatever the starting shares.

**Example 2 (one step).** Start $\langle 0.5, 0.5\rangle$ → $P\vec{x} = \langle 0.55, 0.45\rangle$ after one month.

**Example 3 (PageRank, code).**
```python
import numpy as np
# links: A->B, A->C, B->C, C->A, D->C   (column j = outgoing links of page j, split evenly)
M = np.array([[0, 0, 1, 0],
              [0.5, 0, 0, 0],
              [0.5, 1, 0, 1],
              [0, 0, 0, 0]])
d, n = 0.85, 4
r = np.ones(n) / n
for _ in range(100):
    r = d * M @ r + (1 - d) / n          # follow a link, or jump to a random page
print(dict(zip("ABCD", r.round(3))))      # C ranks highest (most incoming links)
```
Repeatedly applying the matrix (the **power method**) converges to the dominant eigenvector, which is how PageRank was computed for billions of pages. Page C, linked from three pages, gets the highest rank; page D, with no incoming links, gets only the random-jump share.

## 5. Common mistakes
- Mixing up row and column conventions (here columns sum to 1; some books use rows).
- Forgetting to normalise the steady-state vector to sum to 1.
- Assuming every chain has a unique steady state (damping fixes this for PageRank).

## 6. Practice
Q: What do the columns of a (column-stochastic) transition matrix sum to?
A: $1$.

Q: The steady state is an eigenvector for which eigenvalue?
A: $\lambda = 1$.

Q: Steady state of $P = \begin{pmatrix} 0.5 & 0.5 \\ 0.5 & 0.5\end{pmatrix}$?
A: $\langle 0.5, 0.5\rangle$.

Q: What does the damping factor in PageRank model?
A: The chance the surfer jumps to a random page instead of following a link.

Q: How is PageRank computed at scale?
A: With the power method: repeatedly multiply by the matrix until it converges.

@@ 3 | TOPIC | Practice & review: Eigenvalues and eigenvectors | Mixed problem set with worked answers | MUST
# Practice & review: Eigenvalues and eigenvectors

**How to use this:** 40 minutes; verify with `np.linalg.eig`.

## Problems
1. Find the eigenvalues and eigenvectors of $\begin{pmatrix} 5 & 2 \\ 2 & 2\end{pmatrix}$.
2. Eigenvalues of $\begin{pmatrix} 1 & 4 & 7 \\ 0 & -2 & 3 \\ 0 & 0 & 6\end{pmatrix}$?
3. A $3 \times 3$ matrix has eigenvalues 2, $-1$, 4. Find its trace and determinant. Is it invertible?
4. Diagonalise $\begin{pmatrix} 3 & 1 \\ 0 & 2\end{pmatrix}$ and use it to describe $A^{20}$.
5. Without calculating, give the eigenvalues and eigenvectors of the projection onto the x-axis in $\mathbb{R}^2$.
6. Weather: if sunny today, tomorrow is sunny with probability 0.8; if rainy, rainy with probability 0.6. Find the long-run fraction of sunny days.
7. Why do rotation matrices (other than 0° and 180°) have no real eigenvectors?

## Answers
1. trace 7, det 6 → $\lambda = 6, 1$; $\lambda = 6$: $\langle 2, 1\rangle$; $\lambda = 1$: $\langle 1, -2\rangle$ (perpendicular, since the matrix is symmetric).
2. $1, -2, 6$.
3. Trace 5; det $-8$; invertible (no zero eigenvalue).
4. $\lambda = 3$: $\langle 1, 0\rangle$; $\lambda = 2$: $\langle 1, -1\rangle$. $P = \begin{pmatrix} 1 & 1 \\ 0 & -1\end{pmatrix}$, $D = \text{diag}(3, 2)$; $A^{20} = PD^{20}P^{-1} = \begin{pmatrix} 3^{20} & 3^{20} - 2^{20} \\ 0 & 2^{20}\end{pmatrix}$, dominated by $3^{20}$.
5. $\lambda = 1$ with $\langle 1, 0\rangle$; $\lambda = 0$ with $\langle 0, 1\rangle$.
6. $P = \begin{pmatrix} 0.8 & 0.4 \\ 0.2 & 0.6\end{pmatrix}$; $-0.2q_S + 0.4q_R = 0$ → $q_S = 2q_R$ → $\frac{2}{3}$ sunny.
7. A rotation turns every non-zero vector off its own line, so no real direction is only scaled; the eigenvalues are complex.

## Flashcards
Q: Characteristic equation?
A: $\det(A - \lambda I) = 0$.

Q: Diagonalisation?
A: $A = PDP^{-1}$ with eigenvectors in $P$ and eigenvalues in $D$.

Q: Steady state of a Markov chain?
A: The eigenvector for $\lambda = 1$, normalised to sum to 1.

Q: Long-run behaviour of $\vec{x}_{k+1} = A\vec{x}_k$?
A: Dominated by the eigenvector of the largest $\lvert\lambda\rvert$.

@@ 1 | MODULE | Phase 6 — Orthogonality, Least Squares, SVD and PCA (Advanced) | Orthonormal bases, orthogonal matrices, projections onto subspaces, Gram–Schmidt and QR, least squares regression, SVD, PCA | SHOULD
# Phase 6: Orthogonality, least squares, SVD and PCA

The final phase connects linear algebra directly to data science:
- **Orthogonality** (perpendicularity) makes computations simple and numerically stable.
- **Least squares** fits models to noisy data when there's no exact solution: it's linear regression.
- **SVD** (singular value decomposition) is the "Swiss army knife" of matrices: compression, recommendation systems, noise removal.
- **PCA** finds the most informative directions in high-dimensional data.

@@ 2 | MODULE | Orthogonality and least squares | Orthogonal bases and matrices, projections, Gram–Schmidt, least squares | SHOULD
Perpendicular directions don't interfere with each other, which makes everything easier.

@@ 3 | TOPIC | Orthogonal and orthonormal bases, orthogonal matrices | Orthogonal sets, easy coordinates via dot products, orthogonal matrices Qᵀ = Q⁻¹ | SHOULD
# Orthogonal and orthonormal bases, orthogonal matrices

## 1. What it covers
- **Orthogonal** sets (all pairs perpendicular) and **orthonormal** sets (also unit length).
- Easy coordinates: $c_i = \vec{x} \cdot \vec{u}_i$ in an orthonormal basis.
- **Orthogonal matrices** $Q$: orthonormal columns, $Q^TQ = I$, $Q^{-1} = Q^T$.
- They preserve lengths and angles (rotations and reflections).

## 2. Where it is used
- **Numerical computing:** orthogonal matrices don't amplify rounding errors, so stable algorithms (QR, SVD) are built on them.
- **Graphics and robotics:** rotation matrices are orthogonal; their inverse is just the transpose (cheap).
- **Signal processing:** sine/cosine (Fourier) and wavelet bases are orthogonal, so coefficients are computed with simple dot products.
- **PCA:** principal components form an orthonormal basis.

## 3. What it means
In an orthonormal basis, finding coordinates is easy: the coordinate along $\vec{u}_i$ is just the dot product $\vec{x} \cdot \vec{u}_i$ (the projection onto that axis). No system of equations needed, because the axes don't interfere with each other.

An **orthogonal matrix** $Q$ has orthonormal columns. Then $Q^TQ = I$, so the inverse is simply the transpose. Geometrically it's a rigid motion: rotation, reflection, or a combination; lengths and angles are preserved ($\lVert Q\vec{x}\rVert = \lVert\vec{x}\rVert$).

## 4. Formula & worked example
$$
\vec{u}_i \cdot \vec{u}_j = 0\ (i \ne j),\ \lVert\vec{u}_i\rVert = 1 \qquad \vec{x} = \sum(\vec{x} \cdot \vec{u}_i)\,\vec{u}_i \qquad Q^TQ = I
$$

**Example 1.** $\vec{u}_1 = \frac{1}{\sqrt{2}}\langle 1, 1\rangle$, $\vec{u}_2 = \frac{1}{\sqrt{2}}\langle 1, -1\rangle$ is an orthonormal basis of $\mathbb{R}^2$. Coordinates of $\langle 3, 1\rangle$: $\frac{4}{\sqrt{2}} = 2\sqrt{2}$ and $\frac{2}{\sqrt{2}} = \sqrt{2}$.

**Example 2 (orthogonal matrix).** $Q = R_\theta$: $Q^TQ = I$ (check with $\cos^2 + \sin^2 = 1$), so $R_\theta^{-1} = R_\theta^T = R_{-\theta}$.

**Example 3 (length preserved).** $R_{90}\langle 3, 4\rangle = \langle -4, 3\rangle$, still length 5.

**Example 4 (code).**
```python
import numpy as np
Q, _ = np.linalg.qr(np.random.randn(3, 3))     # a random orthogonal matrix
print(np.allclose(Q.T @ Q, np.eye(3)))         # True
x = np.array([1., 2., 3.])
print(np.linalg.norm(x), np.linalg.norm(Q @ x))  # equal lengths
```
The QR decomposition (next-but-one topic) produces an orthogonal $Q$. Multiplying by it rotates/reflects without changing lengths.

## 5. Common mistakes
- Calling a matrix orthogonal when its columns are orthogonal but not unit length.
- Using the dot-product coordinate shortcut with a non-orthonormal basis.
- Confusing $Q^T = Q^{-1}$ (orthogonal) with $A^T = A$ (symmetric).

## 6. Practice
Q: Difference between orthogonal and orthonormal sets?
A: Orthonormal vectors are also unit length.

Q: Inverse of an orthogonal matrix?
A: Its transpose.

Q: Coordinate of $\vec{x}$ along an orthonormal basis vector $\vec{u}$?
A: $\vec{x} \cdot \vec{u}$.

Q: Do orthogonal matrices preserve length?
A: Yes.

Q: Is $\begin{pmatrix} 0 & 1 \\ 1 & 0\end{pmatrix}$ orthogonal?
A: Yes (a reflection).

@@ 3 | TOPIC | Orthogonal projections onto subspaces | Projecting onto a subspace, the projection matrix, closest point, orthogonal complements | SHOULD
# Orthogonal projections onto subspaces

## 1. What it covers
- Projecting a vector onto a **subspace** (not just a line).
- The formula using an orthonormal basis, and the projection matrix $A(A^TA)^{-1}A^T$.
- The projection as the **closest point** in the subspace.
- The **orthogonal complement** $W^\perp$.

## 2. Where it is used
- **Least squares regression** (next topics): fitted values are the projection of $\vec{y}$ onto the column space of $X$.
- **Signal denoising:** project a noisy signal onto a subspace of "clean" signals.
- **Graphics:** projecting 3D points onto a plane (shadows, orthographic views).
- **Recommender systems and embeddings:** removing a direction (e.g. debiasing word vectors by projecting out a bias direction).

## 3. What it means
The projection of $\vec{y}$ onto a subspace $W$ is the point $\hat{y}$ in $W$ **closest** to $\vec{y}$. The error $\vec{y} - \hat{y}$ is **perpendicular** to the whole subspace.

Think of a light shining straight down onto a tabletop (the subspace): the shadow of a point is its projection, and the line from point to shadow is perpendicular to the table.

**With an orthonormal basis** $\vec{u}_1, \ldots, \vec{u}_k$ of $W$: add up the projections onto each basis vector.

**With any basis** (columns of $A$): $\hat{y} = A(A^TA)^{-1}A^T\vec{y}$.

**Orthogonal complement** $W^\perp$: all vectors perpendicular to $W$. Every vector splits uniquely into a part in $W$ plus a part in $W^\perp$.

## 4. Formula & worked example
$$
\hat{y} = (\vec{y} \cdot \vec{u}_1)\vec{u}_1 + \cdots + (\vec{y} \cdot \vec{u}_k)\vec{u}_k \qquad P = A(A^TA)^{-1}A^T
$$

**Example 1 (onto a plane).** Project $\vec{y} = \langle 1, 2, 3\rangle$ onto the xy-plane: $\langle 1, 2, 0\rangle$; the error $\langle 0, 0, 3\rangle$ is perpendicular to the plane.

**Example 2 (orthonormal basis).** $W = \text{span}\{\vec{u}_1 = \frac{1}{\sqrt{2}}\langle 1, 1, 0\rangle, \vec{u}_2 = \langle 0, 0, 1\rangle\}$; $\vec{y} = \langle 3, 1, 2\rangle$.
- $\vec{y} \cdot \vec{u}_1 = \frac{4}{\sqrt{2}}$ → $2\langle 1, 1, 0\rangle = \langle 2, 2, 0\rangle$; $\vec{y} \cdot \vec{u}_2 = 2$ → $\langle 0, 0, 2\rangle$
- $\hat{y} = \langle 2, 2, 2\rangle$; error $\langle 1, -1, 0\rangle$ (perpendicular to both basis vectors ✓)

**Example 3 (distance).** The distance from $\vec{y}$ to $W$ is the length of the error: $\lVert\langle 1, -1, 0\rangle\rVert = \sqrt{2}$.

**Example 4 (code: removing a direction).**
```python
import numpy as np
v = np.array([0.8, 0.3, 0.5])        # an embedding vector
b = np.array([1.0, 0.0, 0.0])        # unwanted direction (unit vector)
v_clean = v - (v @ b) * b            # project out b: keeps the part perpendicular to b
print(v_clean, v_clean @ b)          # [0.  0.3 0.5]  0.0
```
Subtracting the projection onto `b` leaves only the component in the orthogonal complement of `b`, which is the idea behind some embedding debiasing methods.

## 5. Common mistakes
- Using the orthonormal-basis formula with a basis that isn't orthonormal.
- Forgetting that the error must be perpendicular to **every** vector in the subspace.
- Confusing the projection matrix $P$ with a change-of-basis matrix.

## 6. Practice
Q: What is the projection of $\vec{y}$ onto $W$?
A: The closest point to $\vec{y}$ in $W$.

Q: How is the error $\vec{y} - \hat{y}$ related to $W$?
A: It's perpendicular to $W$.

Q: Project $\langle 4, -2, 7\rangle$ onto the xz-plane.
A: $\langle 4, 0, 7\rangle$.

Q: Projection matrix onto the column space of $A$?
A: $A(A^TA)^{-1}A^T$.

Q: What is $P^2$ for a projection matrix?
A: $P$ (projecting twice changes nothing).

@@ 3 | TOPIC | Gram–Schmidt and the QR decomposition | Turning any basis into an orthonormal one, A = QR, uses in solving and least squares | SHOULD
# Gram–Schmidt and the QR decomposition

## 1. What it covers
- The **Gram–Schmidt process**: turning a basis into an orthonormal basis.
- The **QR decomposition** $A = QR$ ($Q$ orthonormal columns, $R$ upper triangular).
- Why QR is used to solve least squares problems stably.

## 2. Where it is used
- **Numerical linear algebra:** QR is the standard way to solve least squares (used inside `numpy.linalg.lstsq`-like solvers and statistics packages).
- **Eigenvalue algorithms:** the QR algorithm computes eigenvalues in practice.
- **Signal processing and communications:** orthogonalising signals (MIMO wireless).
- **Robotics and graphics:** re-orthonormalising rotation matrices that drift due to rounding errors.

## 3. What it means
**Gram–Schmidt:** take the vectors one at a time. From each new vector, **subtract its projections onto the orthonormal vectors already built**; what's left is perpendicular to them. Then normalise it to length 1. Repeat.

It's like building a set of axes: keep the first direction; from the second, remove whatever part points along the first; from the third, remove the parts along the first two; and so on.

**QR:** the orthonormal vectors form $Q$; the coefficients used along the way form the upper triangular $R$, so $A = QR$.

**Least squares with QR:** $A\vec{x} \approx \vec{b}$ becomes $R\vec{x} = Q^T\vec{b}$, a triangular system solved by back substitution, avoiding $A^TA$ (which can lose numerical accuracy).

## 4. Formula & worked example
$$
\vec{w}_k = \vec{v}_k - \sum_{j<k}(\vec{v}_k \cdot \vec{u}_j)\vec{u}_j, \qquad \vec{u}_k = \frac{\vec{w}_k}{\lVert\vec{w}_k\rVert} \qquad A = QR
$$

**Example.** Orthonormalise $\vec{v}_1 = \langle 1, 1, 0\rangle$, $\vec{v}_2 = \langle 1, 0, 1\rangle$.
- $\vec{u}_1 = \frac{1}{\sqrt{2}}\langle 1, 1, 0\rangle$
- $\vec{v}_2 \cdot \vec{u}_1 = \frac{1}{\sqrt{2}}$ → subtract $\frac{1}{\sqrt{2}}\vec{u}_1 = \langle 0.5, 0.5, 0\rangle$: $\vec{w}_2 = \langle 0.5, -0.5, 1\rangle$
- $\lVert\vec{w}_2\rVert = \sqrt{1.5}$ → $\vec{u}_2 = \frac{1}{\sqrt{6}}\langle 1, -1, 2\rangle$
- Check: $\vec{u}_1 \cdot \vec{u}_2 = \frac{1 - 1 + 0}{\sqrt{12}} = 0$ ✓

**Code.**
```python
import numpy as np
A = np.array([[1, 1], [1, 0], [0, 1]], dtype=float)
Q, R = np.linalg.qr(A)
print(Q.round(3)); print(R.round(3))
print(np.allclose(Q @ R, A), np.allclose(Q.T @ Q, np.eye(2)))   # True True
```
NumPy may flip the signs of some columns of $Q$ (and rows of $R$) compared with hand calculation; both are valid. In practice, Householder reflections are used instead of classical Gram–Schmidt for better numerical stability.

## 5. Common mistakes
- Subtracting projections onto the original vectors instead of the new orthonormal ones.
- Forgetting to normalise.
- Using classical Gram–Schmidt for large, nearly dependent sets in floating point (loses orthogonality; use modified Gram–Schmidt or Householder).

## 6. Practice
Q: Purpose of Gram–Schmidt?
A: To turn a basis into an orthonormal basis of the same space.

Q: In $A = QR$, what kind of matrices are $Q$ and $R$?
A: $Q$ has orthonormal columns; $R$ is upper triangular.

Q: First step of Gram–Schmidt?
A: Normalise the first vector.

Q: Orthonormalise $\langle 3, 0\rangle$, $\langle 1, 2\rangle$.
A: $\langle 1, 0\rangle$ and $\langle 0, 1\rangle$.

Q: Why use QR for least squares instead of $A^TA$?
A: It's more numerically stable.

@@ 3 | TOPIC | Least squares and linear regression from scratch | Normal equations AᵀAx = Aᵀb, best-fit lines, connection to projection, normal equation vs gradient descent | SHOULD
# Least squares and linear regression from scratch

## 1. What it covers
- Inconsistent systems $A\vec{x} = \vec{b}$ (more equations than unknowns).
- The **least-squares** solution minimising $\lVert A\vec{x} - \vec{b}\rVert$.
- The **normal equations** $A^TA\hat{x} = A^T\vec{b}$.
- Fitting lines and polynomials; normal equation vs **gradient descent**.

## 2. Where it is used
- **Linear regression** in statistics and ML (`sklearn.linear_model.LinearRegression` solves least squares).
- **Sensor fusion and GPS:** more measurements than unknowns, all noisy.
- **Curve fitting** in science and engineering (`numpy.polyfit`).
- **Computer vision:** camera calibration, image alignment.

## 3. What it means
With 100 data points and a line $y = c_0 + c_1x$ (2 unknowns), there's usually no line through all the points: the system $A\vec{x} = \vec{b}$ has no solution. So find the $\hat{x}$ that makes $A\hat{x}$ as **close** as possible to $\vec{b}$.

**Geometry:** $A\hat{x}$ must be the **projection** of $\vec{b}$ onto the column space of $A$. The error $\vec{b} - A\hat{x}$ is perpendicular to every column of $A$:
$$
A^T(\vec{b} - A\hat{x}) = \vec{0} \Rightarrow A^TA\hat{x} = A^T\vec{b}
$$
These are the **normal equations**. Minimising the sum of squared residuals (the statistics view) gives exactly the same equations.

**Normal equation vs gradient descent:** the normal equation gives the exact answer in one step but needs solving a $p \times p$ system (fine for up to thousands of features). Gradient descent takes many cheap steps and scales to huge data, which is why it's used for neural networks.

## 4. Formula & worked example
$$
A^TA\hat{x} = A^T\vec{b} \qquad \hat{x} = (A^TA)^{-1}A^T\vec{b}
$$

**Example (line through 4 points).** Points $(0, 1)$, $(1, 3)$, $(2, 4)$, $(3, 4)$; model $y = c_0 + c_1x$.
- $A = \begin{pmatrix} 1 & 0 \\ 1 & 1 \\ 1 & 2 \\ 1 & 3\end{pmatrix}$, $\vec{b} = \begin{pmatrix} 1 \\ 3 \\ 4 \\ 4\end{pmatrix}$
- $A^TA = \begin{pmatrix} 4 & 6 \\ 6 & 14\end{pmatrix}$, $A^T\vec{b} = \begin{pmatrix} 12 \\ 23\end{pmatrix}$
- Solve: $\det = 20$ → $\hat{x} = \frac{1}{20}\begin{pmatrix} 14 & -6 \\ -6 & 4\end{pmatrix}\begin{pmatrix} 12 \\ 23\end{pmatrix} = \frac{1}{20}\begin{pmatrix} 30 \\ 20\end{pmatrix} = \begin{pmatrix} 1.5 \\ 1\end{pmatrix}$
- Best line: $y = 1.5 + x$.

**Code: normal equation vs gradient descent.**
```python
import numpy as np
x = np.array([0, 1, 2, 3.]); y = np.array([1, 3, 4, 4.])
A = np.column_stack([np.ones_like(x), x])

theta = np.linalg.solve(A.T @ A, A.T @ y)        # normal equations
print(theta)                                     # [1.5 1. ]
print(np.linalg.lstsq(A, y, rcond=None)[0])      # same, more stable (uses SVD)

w = np.zeros(2); lr = 0.05
for _ in range(5000):
    grad = 2 / len(y) * A.T @ (A @ w - y)        # gradient of the mean squared error
    w -= lr * grad
print(w.round(4))                                # [1.5 1. ]  converges to the same answer
```
All three agree. The gradient is $\frac{2}{n}A^T(A\vec{w} - \vec{y})$, the derivative of the mean squared error; gradient descent steps against it until it reaches the least-squares solution.

## 5. Common mistakes
- Trying to solve $A\vec{x} = \vec{b}$ directly when it's inconsistent.
- Computing $(A^TA)^{-1}$ explicitly for ill-conditioned data (use `lstsq` or QR).
- Forgetting the column of ones for the intercept.

## 6. Practice
Q: Normal equations?
A: $A^TA\hat{x} = A^T\vec{b}$.

Q: What does least squares minimise?
A: The sum of squared residuals $\lVert A\vec{x} - \vec{b}\rVert^2$.

Q: Geometrically, what is $A\hat{x}$?
A: The projection of $\vec{b}$ onto the column space of $A$.

Q: Why add a column of ones to $A$?
A: To fit an intercept term.

Q: When is gradient descent preferred over the normal equation?
A: For very large numbers of features or data points.

@@ 2 | MODULE | Matrix decompositions for data | Singular value decomposition, low-rank approximation, principal component analysis | NICE
SVD and PCA turn huge data matrices into a few meaningful directions.

@@ 3 | TOPIC | Singular value decomposition (SVD) | A = UΣVᵀ, singular values, low-rank approximation, image compression, recommendations | NICE
# Singular value decomposition (SVD)

## 1. What it covers
- The **SVD** $A = U\Sigma V^T$ for **any** $m \times n$ matrix.
- **Singular values** (on the diagonal of $\Sigma$) and their meaning.
- **Low-rank approximation**: keeping the top $k$ singular values.
- Applications: compression, noise reduction, recommendation systems, pseudo-inverse.

## 2. Where it is used
- **Recommender systems:** the Netflix Prize popularised matrix factorisation (closely related to SVD) for predicting ratings.
- **Image compression:** keep a few singular values of an image matrix.
- **NLP:** latent semantic analysis (LSA) applies SVD to word–document matrices.
- **Numerical computing:** `lstsq`, rank and the pseudo-inverse are computed via SVD.
- **PCA** (next topic) is SVD of centred data.

## 3. What it means
Every matrix, square or not, can be written as **rotate → stretch → rotate**:
$$
A = U\Sigma V^T
$$
- $V^T$ rotates (or reflects) the input space so the important directions line up with the axes.
- $\Sigma$ stretches along those axes by the **singular values** $\sigma_1 \ge \sigma_2 \ge \cdots \ge 0$.
- $U$ rotates into the output space.

The singular values measure **how important** each direction is. Often a few are large and the rest tiny. Keeping only the top $k$ gives the **best possible rank-$k$ approximation** of $A$ (Eckart–Young theorem). That's compression and denoising in one idea: keep the strong patterns, drop the weak ones (usually noise).

Singular values are the square roots of the eigenvalues of $A^TA$.

## 4. Formula & worked example
$$
A = U\Sigma V^T = \sum_{i=1}^{r}\sigma_i\vec{u}_i\vec{v}_i^T \qquad A_k = \sum_{i=1}^{k}\sigma_i\vec{u}_i\vec{v}_i^T
$$

**Example 1.** $A = \begin{pmatrix} 3 & 0 \\ 0 & 2\end{pmatrix}$: already diagonal; singular values 3 and 2; $U = V = I$.

**Example 2 (rank).** The number of non-zero singular values is the rank of $A$.

**Example 3 (image compression, code).**
```python
import numpy as np
rng = np.random.default_rng(0)
x = np.linspace(0, 1, 200)
img = np.sin(6 * np.outer(x, x)) + 0.05 * rng.standard_normal((200, 200))   # smooth image + noise
U, s, Vt = np.linalg.svd(img, full_matrices=False)
k = 10
approx = U[:, :k] @ np.diag(s[:k]) @ Vt[:k]
print(s[:6].round(1))                                       # first few singular values dominate
print(np.linalg.norm(img - approx) / np.linalg.norm(img))   # small relative error
print("storage:", k * (200 + 200 + 1), "vs", 200 * 200)     # 4010 vs 40000 numbers
```
Keeping 10 of 200 singular values stores about 10% of the numbers while capturing almost all of the image (and removing much of the noise).

**Example 4 (recommendations).** A users × movies rating matrix is approximately low-rank because tastes are driven by a few factors (genre, mood, era). A rank-$k$ factorisation predicts missing ratings from those hidden factors.

## 5. Common mistakes
- Confusing singular values with eigenvalues (they coincide only for symmetric positive semi-definite matrices).
- Forgetting that NumPy returns $V^T$ (not $V$) and $\Sigma$ as a 1D array.
- Choosing $k$ arbitrarily; look at how fast the singular values drop (or the fraction of total energy kept).

## 6. Practice
Q: SVD form of a matrix?
A: $A = U\Sigma V^T$.

Q: Which matrices have an SVD?
A: All matrices (any size).

Q: What do large singular values indicate?
A: The most important directions (strongest patterns) in the matrix.

Q: Number of non-zero singular values equals?
A: The rank.

Q: How does SVD compress an image?
A: By keeping only the top $k$ singular values and vectors (a low-rank approximation).

@@ 3 | TOPIC | Principal component analysis (PCA) | Centring data, covariance matrix, principal components as eigenvectors, explained variance, dimensionality reduction | NICE
# Principal component analysis (PCA)

## 1. What it covers
- **PCA**: finding the directions of greatest variance in data.
- Steps: centre (and often scale) the data, compute the covariance matrix, find its eigenvectors (or use SVD).
- **Explained variance ratio** and choosing the number of components.
- Using PCA for visualisation, compression and preprocessing.

## 2. Where it is used
- **Data visualisation:** projecting high-dimensional data (e.g. 50 features) to 2D to see clusters.
- **Preprocessing for ML:** reducing features, removing correlations, speeding up training.
- **Image and face recognition:** eigenfaces.
- **Finance:** a few principal components explain most movements of interest rates or stock returns.
- **Genomics:** visualising population structure from thousands of genetic markers.

## 3. What it means
Imagine a cloud of data points shaped like a tilted, flattened ellipse. PCA finds:
- **PC1:** the direction along which the data spread out the most;
- **PC2:** the direction of most remaining spread, **perpendicular** to PC1;
- and so on.

These directions are the **eigenvectors of the covariance matrix**, and each eigenvalue is the **variance** along its direction. Because the covariance matrix is symmetric, the components are orthogonal.

If the first few components capture most of the variance, you can describe each point with just those coordinates and lose little information. Think of photographing a 3D object from the angle that shows the most detail.

**Scale first** if features use different units (otherwise "salary in taka" dominates "age in years").

## 4. Formula & worked example
$$
C = \frac{1}{n - 1}X_c^TX_c \qquad C\vec{v}_i = \lambda_i\vec{v}_i \qquad \text{explained variance ratio}_i = \frac{\lambda_i}{\sum_j\lambda_j}
$$

**Example 1 (2D).** Data with covariance matrix $C = \begin{pmatrix} 5 & 2 \\ 2 & 2\end{pmatrix}$: eigenvalues 6 and 1 (from the eigen practice), eigenvectors $\langle 2, 1\rangle$ and $\langle 1, -2\rangle$.
- PC1 along $\langle 2, 1\rangle$ explains $\frac{6}{7} \approx 86\%$ of the variance; projecting onto PC1 alone keeps 86% of the information in one number per point.

**Example 2 (code).**
```python
import numpy as np
from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler
rng = np.random.default_rng(0)
latent = rng.normal(size=(500, 2))                         # 2 hidden factors
X = latent @ rng.normal(size=(2, 6)) + 0.1 * rng.normal(size=(500, 6))   # 6 observed features
Xs = StandardScaler().fit_transform(X)
pca = PCA().fit(Xs)
print(pca.explained_variance_ratio_.round(3))              # first 2 components ≈ 98%+
X2 = PCA(n_components=2).fit_transform(Xs)                 # 500 x 2: ready to plot
```
The 6 features were generated from 2 hidden factors plus noise, and PCA discovers that: the first two components explain nearly all the variance. `fit_transform` gives the 2D coordinates for plotting or for a smaller model.

**Example 3 (from scratch with SVD).**
```python
Xc = Xs - Xs.mean(axis=0)
U, s, Vt = np.linalg.svd(Xc, full_matrices=False)
components = Vt[:2]                     # principal directions (rows)
scores = Xc @ components.T              # same as PCA coordinates (up to sign)
explained = s**2 / np.sum(s**2)         # same ratios as pca.explained_variance_ratio_
```
PCA is the SVD of the centred data matrix: the right singular vectors are the principal directions, and squared singular values are proportional to the variances.

## 5. Common mistakes
- Forgetting to centre (and usually scale) the data.
- Interpreting components as original features (each is a mix of features).
- Using PCA when the important structure is non-linear (consider t-SNE or UMAP for visualisation).

## 6. Practice
Q: What is the first principal component?
A: The direction of greatest variance in the data.

Q: Principal components are eigenvectors of which matrix?
A: The covariance matrix of the (centred) data.

Q: Why scale features before PCA?
A: Otherwise features with large units dominate the variance.

Q: Eigenvalues 8, 1.5, 0.5. Variance explained by PC1?
A: $\frac{8}{10} = 80\%$.

Q: How is PCA related to SVD?
A: PCA is the SVD of the centred data matrix.

@@ 3 | TOPIC | Practice & review: Orthogonality, least squares, SVD and PCA | Mixed problem set with worked answers | SHOULD
# Practice & review: Orthogonality, least squares, SVD and PCA

**How to use this:** 45 minutes; verify with NumPy.

## Problems
1. Is $\left\{\frac{1}{3}\langle 1, 2, 2\rangle, \frac{1}{3}\langle 2, 1, -2\rangle\right\}$ orthonormal? Find the coordinates of $\langle 3, 3, 0\rangle$ along these vectors.
2. Is $\begin{pmatrix} 0.6 & -0.8 \\ 0.8 & 0.6\end{pmatrix}$ orthogonal? What is its inverse?
3. Project $\langle 2, 4, 6\rangle$ onto the line spanned by $\langle 1, 1, 1\rangle$ and find the distance from the point to the line.
4. Apply Gram–Schmidt to $\langle 1, 0, 1\rangle$, $\langle 1, 1, 0\rangle$.
5. Fit $y = c_0 + c_1x$ to $(1, 2)$, $(2, 3)$, $(3, 5)$ by least squares.
6. A matrix has singular values 50, 20, 1, 0.5. What is its rank? What does a rank-2 approximation keep?
7. Covariance eigenvalues 4.5, 3, 1.5, 1. How many components keep at least 75% of the variance?

## Answers
1. Dot $= \frac{2 + 2 - 4}{9} = 0$; each has length 1 → yes. Coordinates: $\frac{1}{3}(3 + 6) = 3$ and $\frac{1}{3}(6 + 3) = 3$.
2. Columns are orthonormal ($0.36 + 0.64 = 1$, dot $= 0$) → yes; inverse $=$ transpose $\begin{pmatrix} 0.6 & 0.8 \\ -0.8 & 0.6\end{pmatrix}$.
3. $\frac{12}{3}\langle 1, 1, 1\rangle = \langle 4, 4, 4\rangle$; error $\langle -2, 0, 2\rangle$, distance $2\sqrt{2}$.
4. $\vec{u}_1 = \frac{1}{\sqrt{2}}\langle 1, 0, 1\rangle$; $\vec{w}_2 = \langle 1, 1, 0\rangle - \frac{1}{2}\langle 1, 0, 1\rangle = \langle 0.5, 1, -0.5\rangle$ → $\vec{u}_2 = \frac{1}{\sqrt{6}}\langle 1, 2, -1\rangle$.
5. $A^TA = \begin{pmatrix} 3 & 6 \\ 6 & 14\end{pmatrix}$, $A^T\vec{b} = \begin{pmatrix} 10 \\ 23\end{pmatrix}$ → $\det = 6$ → $\hat{x} = \frac{1}{6}\begin{pmatrix} 14 \cdot 10 - 6 \cdot 23 \\ -6 \cdot 10 + 3 \cdot 23\end{pmatrix} = \frac{1}{6}\begin{pmatrix} 2 \\ 9\end{pmatrix}$ → $y = \frac{1}{3} + 1.5x$.
6. Rank 4 (all non-zero); the rank-2 approximation keeps the two dominant patterns (σ = 50, 20) and drops the tiny ones.
7. Total 10: PC1 45%, PC1+PC2 75% → 2 components.

## Flashcards
Q: Inverse of an orthogonal matrix?
A: Its transpose.

Q: Normal equations for least squares?
A: $A^TA\hat{x} = A^T\vec{b}$.

Q: SVD?
A: $A = U\Sigma V^T$: rotate, stretch, rotate.

Q: Principal components?
A: Orthogonal directions of maximum variance (eigenvectors of the covariance matrix).

@@ 2 | MODULE | Final review | Cumulative mixed set covering the whole roadmap | MUST
Mix everything. After this, continue with Multivariable Calculus (gradients, Jacobians, Hessians use these tools) or apply them in ML projects.

@@ 3 | TOPIC | Final review: Linear algebra | Cumulative mixed problems from all phases with answers | MUST
# Final review: Linear algebra

**How to use this:** 75 minutes by hand, then check everything with NumPy.

## Problems
1. Find the angle between $\langle 1, 0, 1\rangle$ and $\langle 0, 1, 1\rangle$, and a vector perpendicular to both.
2. Solve $x + y + 2z = 9$, $2x + 4y - 3z = 1$, $3x + 6y - 5z = 0$.
3. Find the inverse of $\begin{pmatrix} 2 & 3 \\ 1 & 2\end{pmatrix}$ and the area scale factor of the transformation.
4. For $A = \begin{pmatrix} 1 & 2 & 3 \\ 2 & 4 & 6\end{pmatrix}$, find the rank, nullity, and a basis for the null space.
5. Give the $2 \times 2$ matrix that reflects across the x-axis and then rotates by $90^\circ$. What single transformation is it?
6. Find the eigenvalues and eigenvectors of $\begin{pmatrix} 2 & 3 \\ 3 & 2\end{pmatrix}$ and diagonalise it.
7. A Markov chain has $P = \begin{pmatrix} 0.7 & 0.4 \\ 0.3 & 0.6\end{pmatrix}$. Find the steady state.
8. Fit $y = c_0 + c_1x$ to $(0, 0)$, $(1, 1)$, $(2, 1)$, $(3, 2)$ by least squares.
9. Explain in two sentences how SVD and PCA are related.

## Answers
1. $\cos\theta = \frac{1}{2}$ → $60^\circ$; cross product $\langle 0 - 1, 0 - 1, 1 - 0\rangle = \langle -1, -1, 1\rangle$.
2. $x = 1$, $y = 2$, $z = 3$.
3. $\det = 1$ → inverse $\begin{pmatrix} 2 & -3 \\ -1 & 2\end{pmatrix}$; areas are preserved (factor 1).
4. Rank 1; nullity 2; null space basis $\langle -2, 1, 0\rangle$, $\langle -3, 0, 1\rangle$.
5. $R_{90}F = \begin{pmatrix} 0 & -1 \\ 1 & 0\end{pmatrix}\begin{pmatrix} 1 & 0 \\ 0 & -1\end{pmatrix} = \begin{pmatrix} 0 & 1 \\ 1 & 0\end{pmatrix}$: reflection across $y = x$.
6. $\lambda = 5$ with $\langle 1, 1\rangle$; $\lambda = -1$ with $\langle 1, -1\rangle$; $P = \begin{pmatrix} 1 & 1 \\ 1 & -1\end{pmatrix}$, $D = \text{diag}(5, -1)$.
7. $-0.3q_1 + 0.4q_2 = 0$ → $q_1 = \frac{4}{3}q_2$ → $\vec{q} = \langle \frac{4}{7}, \frac{3}{7}\rangle$.
8. $A^TA = \begin{pmatrix} 4 & 6 \\ 6 & 14\end{pmatrix}$, $A^T\vec{b} = \begin{pmatrix} 4 \\ 9\end{pmatrix}$ → $\hat{x} = \frac{1}{20}\begin{pmatrix} 56 - 54 \\ -24 + 36\end{pmatrix} = \begin{pmatrix} 0.1 \\ 0.6\end{pmatrix}$ → $y = 0.1 + 0.6x$.
9. PCA finds the directions of maximum variance of centred data. Computing the SVD of the centred data matrix gives exactly these directions (the right singular vectors), with variances proportional to the squared singular values.
