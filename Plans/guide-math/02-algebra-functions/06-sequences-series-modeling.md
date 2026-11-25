@@ 1 | MODULE | Phase 6 — Sequences, Series, Complex Polar Form and Modeling (Advanced) | Arithmetic and geometric sequences and series, sigma notation, induction, polar form of complex numbers, choosing and fitting models | SHOULD
# Phase 6: Sequences, series, complex polar form and modeling

A **sequence** is a list of numbers following a rule; a **series** is their sum. They describe anything that happens in steps: monthly loan payments, savings deposits, generations, loop iterations. Geometric series even let you add **infinitely** many numbers and get a finite answer, an idea that leads directly into calculus.

This phase also finishes complex numbers (polar form, where multiplication becomes rotation) and ends with the skill that ties the roadmap together: **choosing the right function to model real data**.

@@ 2 | MODULE | Sequences and series | Arithmetic and geometric sequences, recursion, sigma notation, sums, infinite series, induction | SHOULD
Sequences are functions whose inputs are the counting numbers $1, 2, 3, \ldots$

@@ 3 | TOPIC | Arithmetic sequences: explicit and recursive formulas | Common difference, aₙ = a₁ + (n − 1)d, recursive definitions, link to linear functions | SHOULD
# Arithmetic sequences: explicit and recursive formulas

## 1. What it covers
- **Arithmetic sequences:** each term adds a constant **common difference** $d$.
- The **explicit** formula $a_n = a_1 + (n - 1)d$.
- The **recursive** formula $a_n = a_{n-1} + d$.
- Converting between them, and the link to linear functions.

## 2. Where it is used
- **Finance:** simple interest balances, fixed monthly savings, straight-line depreciation.
- **Seating and stacking:** rows of a stadium with 2 more seats each row.
- **Programming:** `range(start, stop, step)` produces an arithmetic sequence; memory addresses of array elements are $\text{base} + i \times \text{size}$.
- **Scheduling:** events every $d$ days.

## 3. What it means
An arithmetic sequence goes up (or down) by the **same amount** each step: 5, 8, 11, 14, ... ($d = 3$).

- **Recursive** form describes how to get the **next** term from the previous one: "start at 5, add 3 each time". It's like giving directions one step at a time.
- **Explicit** form jumps straight to the $n$th term: to reach term $n$ from term 1 you take $n - 1$ steps of size $d$.

An arithmetic sequence is a **linear function** sampled at whole numbers: $a_n = dn + (a_1 - d)$, with slope $d$.

## 4. Formula & worked example
$$
a_n = a_1 + (n - 1)d \qquad a_1 \text{ given},\ a_n = a_{n-1} + d
$$

**Example 1.** Find $a_{20}$ for 7, 11, 15, ...
- $d = 4$: $a_{20} = 7 + 19(4) = 83$

**Example 2.** Which term of 3, 10, 17, ... equals 143?
- $3 + (n - 1)7 = 143$ → $n - 1 = 20$ → $n = 21$

**Example 3.** $a_4 = 20$ and $a_{10} = 50$. Find $a_1$ and $d$.
- 6 steps add 30 → $d = 5$; $a_1 = 20 - 3(5) = 5$

**Example 4 (convert).** Recursive form of $a_n = 12 - 3n$: $a_1 = 9$, $a_n = a_{n-1} - 3$.

**Example 5 (depreciation).** A machine bought for 900 000 taka loses 75 000 each year. Value at the start of year 7?
- $a_1 = 900\,000$, $d = -75\,000$: $a_7 = 900\,000 - 6(75\,000) = 450\,000$

## 5. Common mistakes
- Using $a_1 + nd$ instead of $a_1 + (n - 1)d$ (off-by-one, like a fencepost error in loops).
- Getting the sign of $d$ wrong for decreasing sequences.
- Thinking a recursive formula alone is complete. It needs a starting value.

## 6. Practice
Q: Common difference of 20, 14, 8, 2, ...?
A: $-6$.

Q: Explicit formula for 4, 9, 14, ...?
A: $a_n = 4 + 5(n - 1) = 5n - 1$.

Q: $a_{50}$ for 1, 4, 7, ...?
A: $1 + 49 \times 3 = 148$.

Q: Recursive formula for 100, 92, 84, ...?
A: $a_1 = 100$, $a_n = a_{n-1} - 8$.

Q: A theatre has 20 seats in row 1 and 2 more in each row. Seats in row 15?
A: $20 + 14 \times 2 = 48$.

@@ 3 | TOPIC | Geometric sequences | Common ratio, aₙ = a₁rⁿ⁻¹, recursive form, link to exponential functions | SHOULD
# Geometric sequences

## 1. What it covers
- **Geometric sequences:** each term is multiplied by a constant **common ratio** $r$.
- Explicit $a_n = a_1r^{n-1}$ and recursive $a_n = r \cdot a_{n-1}$ forms.
- The link to exponential functions.

## 2. Where it is used
- **Finance:** a balance with compound interest at the end of each year.
- **Biology:** generations of bacteria, viral spread (each person infects $r$ others).
- **Computing:** dynamic arrays (Python lists, Java ArrayList) grow capacity by a factor (e.g. ×1.5 or ×2); exponential backoff waits 1, 2, 4, 8 s between retries.
- **Music:** frequencies of notes in an octave form a geometric sequence (ratio $2^{1/12}$).

## 3. What it means
A geometric sequence multiplies by the **same factor** each step: 3, 6, 12, 24, ... ($r = 2$). Check by dividing consecutive terms: a constant ratio means geometric.

- $\lvert r\rvert > 1$: terms grow (in size).
- $0 < \lvert r\rvert < 1$: terms shrink towards 0.
- $r < 0$: signs alternate.

It's an **exponential function** sampled at whole numbers.

## 4. Formula & worked example
$$
a_n = a_1 r^{\,n-1} \qquad a_1 \text{ given},\ a_n = r \cdot a_{n-1}
$$

**Example 1.** $a_8$ for 2, 6, 18, ...: $r = 3$ → $a_8 = 2 \cdot 3^7 = 4374$

**Example 2.** $a_6$ for 64, 32, 16, ...: $r = \frac{1}{2}$ → $a_6 = 64 \cdot \left(\frac{1}{2}\right)^5 = 2$

**Example 3.** 5, $-10$, 20, ...: $r = -2$ → $a_n = 5(-2)^{n-1}$

**Example 4.** $a_2 = 12$, $a_5 = 324$. Find $r$ and $a_1$.
- $r^3 = \frac{324}{12} = 27$ → $r = 3$; $a_1 = 4$

**Example 5 (backoff).** A client retries after 1 s, doubling the wait each time. How long is the 8th wait?
- $a_8 = 1 \cdot 2^7 = 128$ s

## 5. Common mistakes
- Using $r^n$ instead of $r^{n-1}$.
- Finding $r$ by subtracting instead of dividing.
- Losing the sign pattern with negative $r$: $(-2)^{n-1}$, not $-2^{n-1}$.

## 6. Practice
Q: Common ratio of 81, 27, 9, ...?
A: $\frac{1}{3}$.

Q: Explicit formula for 4, 20, 100, ...?
A: $a_n = 4 \cdot 5^{n-1}$.

Q: $a_{10}$ of 1, 2, 4, ...?
A: $2^9 = 512$.

Q: Arithmetic or geometric: 2, 5, 12.5, 31.25?
A: Geometric ($r = 2.5$).

Q: A dynamic array starts with capacity 8 and doubles when full. Capacity after 5 doublings?
A: $8 \cdot 2^5 = 256$.

@@ 3 | TOPIC | Sigma notation and arithmetic series | Reading and writing Σ, sum of an arithmetic series, Gauss's trick | SHOULD
# Sigma notation and arithmetic series

## 1. What it covers
- **Sigma notation** $\sum_{k=1}^{n} f(k)$: reading, writing and evaluating.
- The sum of an **arithmetic series**.
- Gauss's pairing trick.

## 2. Where it is used
- **Statistics:** the mean is $\frac{1}{n}\sum x_i$; variance and regression formulas are written with $\sum$.
- **Programming:** a `for` loop that accumulates a total is a sigma sum. Analysing nested loops gives sums like $1 + 2 + \cdots + n = \frac{n(n+1)}{2}$, which is why some algorithms are $O(n^2)$.
- **Finance:** total of payments that grow by a fixed amount.
- **Physics:** total distance with constant acceleration.

## 3. What it means
$\sum_{k=1}^{5} k^2$ means "put $k = 1, 2, 3, 4, 5$ into $k^2$ and add the results": $1 + 4 + 9 + 16 + 25 = 55$. It's exactly a loop:
```python
total = sum(k**2 for k in range(1, 6))   # 55
```
The variable `k` runs from the lower limit to the upper limit, and each value of `k**2` is added to the total.

**Gauss's trick** for $1 + 2 + \cdots + 100$: pair the first and last (1 + 100), the second and second-last (2 + 99), ... Each pair sums to 101 and there are 50 pairs → 5050. In general, an arithmetic series = number of terms × the average of the first and last term.

## 4. Formula & worked example
$$
S_n = \frac{n}{2}(a_1 + a_n) = \frac{n}{2}\big(2a_1 + (n - 1)d\big) \qquad \sum_{k=1}^{n} k = \frac{n(n + 1)}{2}
$$

**Example 1.** $\sum_{k=1}^{4}(3k - 1) = 2 + 5 + 8 + 11 = 26$

**Example 2.** $1 + 2 + \cdots + 200 = \frac{200 \cdot 201}{2} = 20\,100$

**Example 3.** Sum of the first 30 terms of 5, 9, 13, ...
- $S_{30} = \frac{30}{2}(2 \cdot 5 + 29 \cdot 4) = 15(126) = 1890$

**Example 4.** Write $3 + 6 + 9 + \cdots + 60$ in sigma notation and sum it.
- $\sum_{k=1}^{20} 3k = 3 \cdot \frac{20 \cdot 21}{2} = 630$

**Example 5 (savings).** You save 500 taka in month 1 and 100 more each month after. Total after 2 years?
- $S_{24} = \frac{24}{2}(2 \cdot 500 + 23 \cdot 100) = 12(3300) = 39\,600$ taka

**Example 6 (loops).** How many times does the inner line run?
```python
for i in range(n):
    for j in range(i):
        work()
```
$0 + 1 + 2 + \cdots + (n - 1) = \frac{(n - 1)n}{2}$, roughly $\frac{n^2}{2}$, so the code is $O(n^2)$.

## 5. Common mistakes
- Miscounting terms: from $k = 3$ to $k = 10$ there are $8$ terms, not 7.
- Using the last term's index instead of its value in $\frac{n}{2}(a_1 + a_n)$.
- Forgetting that the constant in $\sum 3k$ can be factored out.

## 6. Practice
Q: $\sum_{k=1}^{5} 2k$?
A: $30$.

Q: Sum of $1 + 2 + \cdots + 50$?
A: $1275$.

Q: Sum of the first 10 terms of 2, 5, 8, ...?
A: $\frac{10}{2}(4 + 27) = 155$.

Q: Number of terms in $\sum_{k=4}^{15}$?
A: $12$.

Q: Sum of all even numbers from 2 to 100?
A: $2 \cdot \frac{50 \cdot 51}{2} = 2550$.

@@ 3 | TOPIC | Geometric series and infinite geometric series | Finite sum formula, convergence for ratios between -1 and 1, repeating decimals, loans | SHOULD
# Geometric series and infinite geometric series

## 1. What it covers
- The sum of a **finite geometric series**.
- **Infinite geometric series:** when they **converge**, and their sum $\frac{a_1}{1 - r}$.
- Applications: repeating decimals, savings plans, loan payments.

## 2. Where it is used
- **Finance:** future value of regular deposits; the loan/EMI formula comes from a geometric series.
- **Computer science:** the total cost of doubling a dynamic array is a geometric series, giving O(1) amortised append; the number of nodes in a full binary tree is $1 + 2 + 4 + \cdots + 2^h = 2^{h+1} - 1$.
- **Physics:** total distance of a bouncing ball.
- **Calculus:** power series start from the geometric series.

## 3. What it means
**Finite sum:** take $S = a + ar + ar^2 + \cdots + ar^{n-1}$. Multiply by $r$ and subtract; almost everything cancels: $S - rS = a - ar^n$, so $S = \frac{a(1 - r^n)}{1 - r}$.

**Infinite sum:** if $\lvert r\rvert < 1$, the terms shrink so fast that $r^n \to 0$, and the sum approaches $\frac{a}{1 - r}$. Walk halfway to a wall, then half the remaining distance, and so on: $\frac{1}{2} + \frac{1}{4} + \frac{1}{8} + \cdots = 1$. You never "pass" the wall; the total is exactly 1.

If $\lvert r\rvert \ge 1$, the series **diverges** (no finite sum).

## 4. Formula & worked example
$$
S_n = \frac{a_1(1 - r^n)}{1 - r}\ (r \ne 1) \qquad S_\infty = \frac{a_1}{1 - r}\ (\lvert r\rvert < 1)
$$

**Example 1.** $3 + 6 + 12 + \cdots$ (8 terms): $S_8 = \frac{3(1 - 2^8)}{1 - 2} = 3(255) = 765$

**Example 2.** $8 + 4 + 2 + 1 + \cdots$ (forever): $S_\infty = \frac{8}{1 - 1/2} = 16$

**Example 3.** $0.\overline{7} = 0.7 + 0.07 + 0.007 + \cdots = \frac{0.7}{1 - 0.1} = \frac{7}{9}$

**Example 4 (bouncing ball).** Dropped from 10 m, each bounce reaches 60% of the previous height. Total distance travelled?
- Down: $10 + 6 + 3.6 + \cdots = \frac{10}{0.4} = 25$; up: $6 + 3.6 + \cdots = \frac{6}{0.4} = 15$ → total 40 m

**Example 5 (savings).** 5000 taka deposited at the end of each year at 8% for 10 years. Final value?
- $5000(1 + 1.08 + 1.08^2 + \cdots + 1.08^9) = 5000 \cdot \frac{1.08^{10} - 1}{0.08} \approx 72\,433$ taka

**Example 6 (does it converge?).** $1 - 1.5 + 2.25 - \cdots$: $r = -1.5$, $\lvert r\rvert > 1$ → diverges.

## 5. Common mistakes
- Using $\frac{a}{1 - r}$ when $\lvert r\rvert \ge 1$.
- Using $r^{n-1}$ in the sum formula instead of $r^n$.
- Forgetting the first term in bouncing problems is counted once (down only).

## 6. Practice
Q: Sum of $1 + 3 + 9 + \cdots$ (6 terms)?
A: $\frac{3^6 - 1}{2} = 364$.

Q: $\sum_{k=0}^{\infty} \left(\frac{1}{3}\right)^k$?
A: $\frac{1}{1 - 1/3} = \frac{3}{2}$.

Q: Does $5 + 5.5 + 6.05 + \cdots$ converge?
A: No, $r = 1.1 > 1$.

Q: Write $0.\overline{45}$ using a geometric series.
A: $\frac{0.45}{1 - 0.01} = \frac{45}{99} = \frac{5}{11}$.

Q: Nodes in a full binary tree of height 3 (levels 0–3)?
A: $1 + 2 + 4 + 8 = 15$.

@@ 3 | TOPIC | Mathematical induction | Base case and inductive step; proving sum formulas and divisibility | NICE
# Mathematical induction

## 1. What it covers
- The structure of a proof by **induction**: base case + inductive step.
- Proving sum formulas such as $1 + 2 + \cdots + n = \frac{n(n+1)}{2}$.
- Proving divisibility and inequality statements.

## 2. Where it is used
- **Computer science:** proving algorithms correct (loop invariants are induction), proving recursive functions terminate, analysing recurrences.
- **Discrete maths and exams.**
- **Formal verification** of software and hardware.

## 3. What it means
To prove a statement $P(n)$ is true for **every** natural number $n$:
1. **Base case:** show $P(1)$ is true.
2. **Inductive step:** show that **if** $P(k)$ is true (the inductive hypothesis), **then** $P(k + 1)$ is true.

Then it's true for all $n$: $P(1)$ is true, so $P(2)$ is, so $P(3)$ is, forever.

**Analogy:** an infinite row of dominoes. Knock over the first (base case), and make sure each domino knocks over the next (inductive step). Then they all fall.

It mirrors **recursion**: a recursive function handles a base case and reduces case $n$ to case $n - 1$.

## 4. Formula & worked example
**Example 1.** Prove $1 + 2 + \cdots + n = \frac{n(n + 1)}{2}$.
- **Base** $n = 1$: LHS $= 1$, RHS $= \frac{1 \cdot 2}{2} = 1$ ✓
- **Step:** assume $1 + \cdots + k = \frac{k(k + 1)}{2}$. Then
$$
1 + \cdots + k + (k + 1) = \frac{k(k + 1)}{2} + (k + 1) = \frac{(k + 1)(k + 2)}{2}
$$
which is the formula with $n = k + 1$ ✓

**Example 2.** Prove $1 + 3 + 5 + \cdots + (2n - 1) = n^2$.
- Base: $1 = 1^2$ ✓. Step: $k^2 + (2k + 1) = (k + 1)^2$ ✓

**Example 3 (divisibility).** Prove $4^n - 1$ is divisible by 3.
- Base: $4 - 1 = 3$ ✓
- Step: assume $4^k - 1 = 3m$. Then $4^{k+1} - 1 = 4 \cdot 4^k - 1 = 4(3m + 1) - 1 = 12m + 3 = 3(4m + 1)$ ✓

## 5. Common mistakes
- Skipping the base case (the "dominoes" never start).
- Assuming what you're trying to prove for $k + 1$ instead of using the hypothesis for $k$.
- Checking a few values and calling it a proof.

## 6. Practice
Q: Two parts of an induction proof?
A: Base case and inductive step.

Q: In the inductive step, what do you assume?
A: That the statement holds for $n = k$.

Q: Prove $2 + 4 + \cdots + 2n = n(n + 1)$: inductive step?
A: $k(k + 1) + 2(k + 1) = (k + 1)(k + 2)$ ✓

Q: Base case for $5^n - 1$ divisible by 4?
A: $5 - 1 = 4$ ✓

Q: How does induction relate to recursion?
A: Both handle a base case and build case $n$ from case $n - 1$.

@@ 2 | MODULE | Complex numbers in polar form | Modulus-argument form, multiplication as rotation, De Moivre's theorem, roots | SHOULD
In polar form, multiplying complex numbers means multiplying lengths and adding angles.

@@ 3 | TOPIC | Polar form of complex numbers and De Moivre's theorem | r(cos θ + i sin θ), multiplying and dividing, powers and nth roots, Euler's formula | SHOULD
# Polar form of complex numbers and De Moivre's theorem

## 1. What it covers
- Writing $a + bi$ as $r(\cos\theta + i\sin\theta)$ (modulus $r$, argument $\theta$).
- Multiplying and dividing in polar form.
- **De Moivre's theorem** for powers.
- Finding **nth roots** of complex numbers.
- **Euler's formula** $e^{i\theta} = \cos\theta + i\sin\theta$ (preview).

## 2. Where it is used
- **Electrical engineering:** phasors: AC voltages as rotating complex numbers $Ve^{i\omega t}$.
- **Signal processing:** the FFT uses the $n$th roots of unity $e^{2\pi ik/n}$; it runs inside audio codecs, image compression and Wi-Fi.
- **Graphics:** multiplying by $e^{i\theta}$ rotates a 2D point by $\theta$.
- **Quantum computing:** qubit phases.

## 3. What it means
Plot $z = a + bi$ in the complex plane. Instead of "across $a$, up $b$", describe it by **distance** $r = \sqrt{a^2 + b^2}$ and **angle** $\theta$ from the positive real axis (like polar coordinates). Then $a = r\cos\theta$, $b = r\sin\theta$.

**Multiplying:** the lengths **multiply** and the angles **add**. So multiplying by $i$ (length 1, angle 90°) rotates any point by 90°. That's why $i^2 = -1$: two quarter turns face you the opposite way.

**Powers (De Moivre):** $z^n$ has length $r^n$ and angle $n\theta$.

**Roots:** $z^{1/n}$ has length $\sqrt[n]{r}$ and angles $\frac{\theta + 2\pi k}{n}$ for $k = 0, \ldots, n - 1$: $n$ roots equally spaced around a circle.

## 4. Formula & worked example
$$
z = r(\cos\theta + i\sin\theta) = re^{i\theta} \qquad z_1z_2 = r_1r_2\big(\cos(\theta_1 + \theta_2) + i\sin(\theta_1 + \theta_2)\big)
$$
$$
z^n = r^n(\cos n\theta + i\sin n\theta) \qquad \sqrt[n]{z} = \sqrt[n]{r}\left(\cos\frac{\theta + 2\pi k}{n} + i\sin\frac{\theta + 2\pi k}{n}\right)
$$

**Example 1.** Write $1 + i$ in polar form.
- $r = \sqrt{2}$, $\theta = \frac{\pi}{4}$ → $\sqrt{2}\left(\cos\frac{\pi}{4} + i\sin\frac{\pi}{4}\right)$

**Example 2.** Write $-2\sqrt{3} + 2i$ in polar form.
- $r = \sqrt{12 + 4} = 4$; QII, reference angle $\tan^{-1}\frac{2}{2\sqrt{3}} = \frac{\pi}{6}$ → $\theta = \frac{5\pi}{6}$

**Example 3 (multiply).** $2(\cos 20^\circ + i\sin 20^\circ) \times 3(\cos 40^\circ + i\sin 40^\circ) = 6(\cos 60^\circ + i\sin 60^\circ) = 3 + 3\sqrt{3}i$

**Example 4 (De Moivre).** $(1 + i)^8 = (\sqrt{2})^8\left(\cos 2\pi + i\sin 2\pi\right) = 16$

**Example 5 (roots).** Cube roots of 8.
- $8 = 8(\cos 0 + i\sin 0)$. Roots: length 2, angles $0, \frac{2\pi}{3}, \frac{4\pi}{3}$ → $2$, $-1 + i\sqrt{3}$, $-1 - i\sqrt{3}$

**Example 6 (Euler).** $e^{i\pi} = \cos\pi + i\sin\pi = -1$, so $e^{i\pi} + 1 = 0$.

## 5. Common mistakes
- Getting the angle in the wrong quadrant (use the signs of $a$ and $b$, or `atan2`).
- Multiplying the angles instead of adding them.
- Listing only one of the $n$ roots.

## 6. Practice
Q: Modulus and argument of $-3i$?
A: $r = 3$, $\theta = \frac{3\pi}{2}$ (or $-\frac{\pi}{2}$).

Q: $\left[2(\cos 15^\circ + i\sin 15^\circ)\right]^3$?
A: $8(\cos 45^\circ + i\sin 45^\circ) = 4\sqrt{2} + 4\sqrt{2}i$.

Q: Effect of multiplying a complex number by $i$?
A: Rotates it 90° anticlockwise about the origin.

Q: How many fourth roots does 16 have, and what are they?
A: Four: $2, 2i, -2, -2i$.

Q: Polar form of $-1$?
A: $1(\cos\pi + i\sin\pi) = e^{i\pi}$.

@@ 2 | MODULE | Modeling with functions | Choosing a function family, fitting and checking models | SHOULD
The final skill: look at data or a situation and decide which function describes it.

@@ 3 | TOPIC | Choosing and fitting models | Linear, quadratic, exponential, logarithmic, periodic; residuals; fitting with technology | SHOULD
# Choosing and fitting models

## 1. What it covers
- Recognising which family fits a situation or data set: **linear, quadratic, exponential, logarithmic, periodic**.
- Fitting a model with technology (least squares).
- Checking a fit with **residuals**.
- Composing models and knowing their limits.

## 2. Where it is used
- **Data science and machine learning:** model selection is a core job; residual plots diagnose bad models.
- **Business forecasting:** sales trends with seasonality.
- **Science and engineering:** calibration, growth curves, decay curves.
- **Product analytics:** user growth (exponential early, then levelling off).

## 3. What it means
**Clues for choosing:**
| Pattern | Family |
|---|---|
| Constant **differences** | Linear $y = mx + b$ |
| Constant **second differences**, one peak/valley | Quadratic |
| Constant **ratios**, percentage growth/decay | Exponential $y = ab^x$ |
| Fast rise that keeps slowing (diminishing returns) | Logarithmic $y = a + b\ln x$ |
| Repeats regularly | Periodic (sine/cosine) |

**Fitting:** software finds the parameters that minimise the sum of squared errors (least squares).

**Residual** = actual − predicted. A good model's residuals look like random scatter around 0. A clear pattern (a curve, a wave) in the residuals means the model is missing something.

## 4. Formula & worked example
**Example 1 (differences).** $y$: 3, 7, 13, 21, 31. First differences 4, 6, 8, 10; second differences 2, 2, 2 → quadratic.

**Example 2 (ratios).** $y$: 200, 240, 288, 345.6. Ratios all 1.2 → exponential $y = 200(1.2)^x$.

**Example 3 (fitting in code).**
```python
import numpy as np
x = np.array([0, 1, 2, 3, 4, 5])
y = np.array([2.1, 3.9, 8.2, 15.8, 32.5, 63.0])
b, log_a = np.polyfit(x, np.log(y), 1)     # fit ln y = ln a + x ln b
print(np.exp(log_a), np.exp(b))             # about 2.0 and 2.0 -> y ≈ 2·2^x
```
Taking $\ln$ of both sides of $y = ab^x$ gives $\ln y = \ln a + x\ln b$, which is a straight line in $x$. `polyfit(..., 1)` fits that line; exponentiating the slope and intercept recovers $b$ and $a$. This "linearise then fit" trick is widely used.

**Example 4 (residuals).** A line fitted to curved data leaves residuals that are positive at both ends and negative in the middle (a U-pattern) → try a quadratic or exponential.

**Example 5 (composition).** Revenue depends on users $R(u) = 120u$ and users depend on months $u(t) = 500 \cdot 1.1^t$. Revenue over time: $R(u(t)) = 60\,000 \cdot 1.1^t$.

## 5. Common mistakes
- Choosing a model by eye from a few points.
- Ignoring residual patterns because $R^2$ looks high.
- Extrapolating an exponential model forever (real growth eventually slows).

## 6. Practice
Q: $y$: 5, 8, 11, 14. Which family?
A: Linear.

Q: $y$: 1, 4, 9, 16, 25. Which family?
A: Quadratic (second differences constant).

Q: Data that rises quickly then levels off with diminishing returns?
A: Logarithmic (or another saturating model).

Q: What does a pattern in residuals indicate?
A: The model is missing structure; try a different family.

Q: How do you fit $y = ab^x$ with a linear fitting tool?
A: Fit a line to $(x, \ln y)$, then $a = e^{\text{intercept}}$, $b = e^{\text{slope}}$.

@@ 2 | MODULE | Final review | Two cumulative mixed sets covering the whole roadmap | MUST
Mixed practice across the whole roadmap. After this you're ready for Calculus.

@@ 3 | TOPIC | Final review 1: Equations, lines, functions and quadratics | Cumulative mixed problems from Phases 1–4 with answers | MUST
# Final review 1: Equations, lines, functions and quadratics

**How to use this:** 60 minutes, no calculator. Re-study the topic for every miss.

## Problems
1. Solve $\frac{x - 2}{3} - \frac{2x + 1}{4} = 1$.
2. Solve for $r$: $S = \frac{a}{1 - r}$.
3. Solve $\lvert 2x - 5\rvert < 7$ and write it in interval notation.
4. Line through $(-1, 6)$ parallel to $3x + y = 4$.
5. Solve: $3x - 2y = 12$, $5x + 4y = 20$.
6. $f(x) = x^2 - 1$, $g(x) = \frac{1}{x + 2}$. Find $g(f(x))$ and its domain.
7. Inverse of $f(x) = \frac{3x - 2}{5}$.
8. Factor completely: $2x^4 - 32$.
9. Vertex form and vertex of $y = -2x^2 + 8x - 3$.
10. Solve $x^2 - 6x + 10 = 0$.

## Answers
1. ×12: $4(x - 2) - 3(2x + 1) = 12$ → $-2x - 11 = 12$ → $x = -11.5$.
2. $S(1 - r) = a$ → $1 - r = \frac{a}{S}$ → $r = 1 - \frac{a}{S}$.
3. $-7 < 2x - 5 < 7$ → $-1 < x < 6$ → $(-1, 6)$.
4. Slope $-3$: $y - 6 = -3(x + 1)$ → $y = -3x + 3$.
5. ×2 first: $6x - 4y = 24$; add: $11x = 44$ → $x = 4$, $y = 0$.
6. $g(f(x)) = \frac{1}{x^2 + 1}$; domain all reals.
7. $f^{-1}(x) = \frac{5x + 2}{3}$.
8. $2(x^4 - 16) = 2(x^2 + 4)(x + 2)(x - 2)$.
9. $-2(x - 2)^2 + 5$; vertex $(2, 5)$ (a maximum).
10. $x = 3 \pm i$.

@@ 3 | TOPIC | Final review 2: Exponentials, logs, rational functions and series | Cumulative mixed problems from Phases 5–6 with answers | MUST
# Final review 2: Exponentials, logs, rational functions and series

**How to use this:** 60 minutes; calculator for (C) problems.

## Problems
1. Simplify $\left(\frac{27x^6}{y^{-3}}\right)^{2/3}$.
2. Solve $\sqrt{x + 7} - x = 1$.
3. (C) 25 000 taka at 6% compounded monthly. How long to reach 40 000?
4. Solve $\log_3(x + 1) + \log_3(x - 1) = 1$.
5. Solve $e^{2x} - 5e^x + 6 = 0$.
6. Find the asymptotes of $f(x) = \frac{2x^2 + 3}{x^2 - 4}$.
7. Sum of the first 25 terms of 50, 46, 42, ...
8. $\sum_{k=1}^{\infty} 6\left(\frac{2}{3}\right)^{k-1}$.
9. (C) A geometric sequence has $a_3 = 18$ and $a_6 = 486$. Find $a_1$ and $S_6$.
10. Write $(1 - i)^6$ in the form $a + bi$ using De Moivre.

## Answers
1. $27^{2/3}x^4y^2 = 9x^4y^2$.
2. $\sqrt{x + 7} = x + 1$ → $x^2 + x - 6 = 0$ → $x = 2$ ✓ ($x = -3$: $2 \ne -2$ ✗).
3. $1.005^{12t} = 1.6$ → $t = \frac{\ln 1.6}{12\ln 1.005} \approx 7.85$ years.
4. $x^2 - 1 = 3$ → $x = 2$ (reject $-2$).
5. $(e^x - 2)(e^x - 3) = 0$ → $x = \ln 2$ or $\ln 3$.
6. VA $x = \pm 2$; HA $y = 2$.
7. $S_{25} = \frac{25}{2}(100 + 24(-4)) = \frac{25}{2}(4) = 50$.
8. $\frac{6}{1 - 2/3} = 18$.
9. $r^3 = 27$ → $r = 3$, $a_1 = 2$; $S_6 = \frac{2(3^6 - 1)}{2} = 728$.
10. $1 - i = \sqrt{2}e^{-i\pi/4}$ → $(\sqrt{2})^6 e^{-i3\pi/2} = 8(\cos\frac{3\pi}{2} - i\sin\frac{3\pi}{2}) = 8i$.
