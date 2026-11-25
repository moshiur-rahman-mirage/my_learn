@@ 1 | MODULE | Phase 6 — Sequences and Series (Advanced) | Convergence of sequences and series, divergence/integral/comparison/ratio/root/alternating tests, power series, Taylor and Maclaurin series | SHOULD
# Phase 6: Sequences and series

Can you add infinitely many numbers and get a finite answer? Sometimes yes ($\frac{1}{2} + \frac{1}{4} + \frac{1}{8} + \cdots = 1$), sometimes no ($1 + \frac{1}{2} + \frac{1}{3} + \cdots = \infty$). This phase gives you tests to tell the difference, and then the big payoff: **Taylor series**, which write functions like $e^x$, $\sin x$ and $\ln(1 + x)$ as "infinite polynomials". That's how calculators and computer libraries actually compute these functions.

**Before starting:** review arithmetic and geometric series and sigma notation in the Algebra & Functions roadmap (Phase 6).

@@ 2 | MODULE | Convergence tests | Sequences, series, divergence test, integral and p-series, comparison, ratio and root, alternating series | SHOULD
Each test answers one question: does the infinite sum settle to a finite value?

@@ 3 | TOPIC | Sequences, series and the divergence test | Limits of sequences, partial sums, convergence of a series, geometric and telescoping series, nth-term test | SHOULD
# Sequences, series and the divergence test

## 1. What it covers
- **Sequences** and their limits.
- **Series** as limits of **partial sums**.
- Geometric and **telescoping** series (ones you can sum exactly).
- The **divergence (nth-term) test**.

## 2. Where it is used
- **Computing:** iterative algorithms produce sequences of approximations that should converge.
- **Finance:** perpetuities (payments forever) are infinite geometric series.
- **Physics and engineering:** series solutions of equations; signal decomposition.
- **Probability:** expected values of discrete distributions are series.

## 3. What it means
A **sequence** $a_1, a_2, a_3, \ldots$ **converges** if its terms approach a fixed number.

A **series** $\sum a_n$ is the sum of a sequence's terms. Add them one at a time to get **partial sums** $S_1, S_2, S_3, \ldots$. The series converges if the partial sums approach a finite limit.

**Divergence test:** if the terms $a_n$ do **not** go to 0, the series **diverges** (you keep adding chunks that don't shrink). **But** if $a_n \to 0$, the test is inconclusive: the harmonic series $\sum\frac{1}{n}$ has terms going to 0 and still diverges.

**Telescoping:** terms cancel in pairs, like a collapsing telescope, leaving only the ends.

## 4. Formula & worked example
$$
\sum_{n=0}^{\infty} ar^n = \frac{a}{1 - r}\ (\lvert r\rvert < 1) \qquad \lim_{n \to \infty} a_n \ne 0 \Rightarrow \sum a_n \text{ diverges}
$$

**Example 1 (sequence).** $a_n = \frac{3n + 1}{n + 2} \to 3$. Converges.

**Example 2 (geometric).** $\sum_{n=1}^\infty 5\left(\frac{2}{3}\right)^{n-1} = \frac{5}{1 - 2/3} = 15$

**Example 3 (telescoping).** $\sum_{n=1}^\infty\frac{1}{n(n + 1)} = \sum\left(\frac{1}{n} - \frac{1}{n + 1}\right)$
- $S_N = 1 - \frac{1}{N + 1} \to 1$

**Example 4 (divergence test).** $\sum\frac{n}{2n + 1}$: terms → $\frac{1}{2} \ne 0$ → diverges.

**Example 5 (perpetuity).** A bond pays 1000 taka a year forever, discounted at 8%: present value $= \sum_{n=1}^\infty\frac{1000}{1.08^n} = \frac{1000/1.08}{1 - 1/1.08} = 12\,500$ taka.

## 5. Common mistakes
- Concluding a series converges because $a_n \to 0$.
- Confusing the sequence's limit with the series' sum.
- Using the geometric formula when $\lvert r\rvert \ge 1$.

## 6. Practice
Q: Difference between a sequence and a series?
A: A sequence is a list of terms; a series is their sum.

Q: Does $\sum\frac{n^2}{n^2 + 1}$ converge?
A: No, terms → 1 (divergence test).

Q: $\sum_{n=0}^\infty\left(\frac{1}{4}\right)^n$?
A: $\frac{4}{3}$.

Q: If $a_n \to 0$, does $\sum a_n$ converge?
A: Not necessarily (e.g. the harmonic series).

Q: $\lim_{n \to \infty}\frac{\ln n}{n}$?
A: $0$.

@@ 3 | TOPIC | The integral test and p-series | Comparing a series with an improper integral, p-series convergence, the harmonic series | SHOULD
# The integral test and p-series

## 1. What it covers
- The **integral test**: a positive decreasing series behaves like the matching improper integral.
- **p-series** $\sum\frac{1}{n^p}$: converges iff $p > 1$.
- Why the **harmonic series** $\sum\frac{1}{n}$ diverges.

## 2. Where it is used
- **Algorithm analysis:** $\sum_{k=1}^{n}\frac{1}{k} \approx \ln n$ appears in the average cost of quicksort and in hashing.
- **Physics:** sums of inverse powers (e.g. potential energy in lattices).
- **Number theory:** $\sum\frac{1}{n^2} = \frac{\pi^2}{6}$ (the Basel problem).
- **Reference tool** for comparison tests.

## 3. What it means
If $f(x)$ is positive and decreasing with $f(n) = a_n$, the terms $a_n$ are the heights of unit-width rectangles that hug the curve. So the sum and the area $\int_1^\infty f(x)\,dx$ either **both** are finite or **both** are infinite.

**p-series:** $\int_1^\infty\frac{dx}{x^p}$ converges iff $p > 1$, so $\sum\frac{1}{n^p}$ does too.
- $\sum\frac{1}{n}$ ($p = 1$) **diverges**, but incredibly slowly: the first million terms add up to only about 14.4.
- $\sum\frac{1}{n^2}$ ($p = 2$) converges (to $\frac{\pi^2}{6} \approx 1.645$).

## 4. Formula & worked example
$$
\sum_{n=1}^{\infty}\frac{1}{n^p} \text{ converges} \iff p > 1 \qquad \sum_{k=1}^{n}\frac{1}{k} \approx \ln n + 0.577
$$

**Example 1.** $\sum\frac{1}{n^3}$: $p = 3 > 1$ → converges.

**Example 2.** $\sum\frac{1}{\sqrt{n}}$: $p = \frac{1}{2}$ → diverges.

**Example 3 (integral test).** $\sum_{n=2}^\infty\frac{1}{n\ln n}$: $\int_2^\infty\frac{dx}{x\ln x} = \lim[\ln(\ln x)]_2^b = \infty$ → diverges.

**Example 4 (integral test).** $\sum_{n=1}^\infty ne^{-n^2}$: $\int_1^\infty xe^{-x^2}dx = \frac{1}{2e}$ (finite) → converges.

**Example 5 (code).**
```python
import math
s = sum(1 / k for k in range(1, 1_000_001))
print(s, math.log(1_000_000) + 0.5772)   # 14.3927...  14.3927...
```
After a million terms the harmonic sum is only about 14.39, matching $\ln n + 0.577$. It keeps growing without bound, just very slowly, like $\ln n$.

## 5. Common mistakes
- Applying the integral test to a function that isn't positive and decreasing.
- Thinking the integral's value equals the series' sum (only convergence matches).
- Mixing up the cut-off: $p = 1$ diverges.

## 6. Practice
Q: Does $\sum\frac{1}{n^{1.01}}$ converge?
A: Yes ($p > 1$).

Q: Does $\sum\frac{1}{n^{0.99}}$ converge?
A: No.

Q: Conditions for the integral test?
A: $f$ positive, continuous and decreasing with $f(n) = a_n$.

Q: Does the harmonic series converge?
A: No.

Q: Does $\sum\frac{1}{n^2 + 1}$ converge (integral test)?
A: Yes: $\int_1^\infty\frac{dx}{x^2 + 1} = \frac{\pi}{4}$.

@@ 3 | TOPIC | Comparison tests | Direct comparison and limit comparison with known series | SHOULD
# Comparison tests

## 1. What it covers
- **Direct comparison**: bound a series above by a convergent one, or below by a divergent one.
- **Limit comparison**: if $\frac{a_n}{b_n} \to$ a positive finite number, both behave the same.
- Choosing the comparison series (usually a p-series or geometric series).

## 2. Where it is used
- **Algorithm analysis:** bounding sums by simpler ones (big-O reasoning).
- **Error estimation:** bounding the tail of a series.
- **Quick convergence checks** for messy terms.

## 3. What it means
**Direct comparison** (positive terms):
- If $a_n \le b_n$ and $\sum b_n$ converges → $\sum a_n$ converges (smaller than something finite).
- If $a_n \ge b_n$ and $\sum b_n$ diverges → $\sum a_n$ diverges (bigger than something infinite).

**Limit comparison:** for large $n$, what matters is the **dominant** terms. $\frac{3n^2 + 1}{n^4 - n}$ behaves like $\frac{3}{n^2}$. If $\lim\frac{a_n}{b_n} = c$ with $0 < c < \infty$, both series do the same thing.

**Finding $b_n$:** keep the highest powers in the numerator and denominator.

## 4. Formula & worked example
$$
\lim_{n \to \infty}\frac{a_n}{b_n} = c,\ 0 < c < \infty \Rightarrow \sum a_n \text{ and } \sum b_n \text{ both converge or both diverge}
$$

**Example 1 (direct).** $\sum\frac{1}{n^2 + 5}$: $\frac{1}{n^2 + 5} < \frac{1}{n^2}$, which converges → converges.

**Example 2 (direct).** $\sum\frac{1}{\sqrt{n} - 0.5}$: $\frac{1}{\sqrt{n} - 0.5} > \frac{1}{\sqrt{n}}$, which diverges → diverges.

**Example 3 (limit).** $\sum\frac{3n^2 + 1}{n^4 - n}$: compare with $\frac{1}{n^2}$: ratio → 3 → converges.

**Example 4 (limit).** $\sum\frac{n + 4}{n^2 + 1}$: compare with $\frac{1}{n}$: ratio → 1 → diverges.

**Example 5 (geometric comparison).** $\sum\frac{1}{2^n + n}$: $< \frac{1}{2^n}$ → converges.

## 5. Common mistakes
- Comparing in the useless direction (bigger than a convergent series tells you nothing).
- Using comparison tests on series with negative terms.
- Picking $b_n$ without looking at the dominant terms.

## 6. Practice
Q: Does $\sum\frac{1}{n^3 + 2n}$ converge?
A: Yes (compare with $\frac{1}{n^3}$).

Q: Does $\sum\frac{2n}{n^2 - 3}$ converge?
A: No (behaves like $\frac{2}{n}$).

Q: Does $\sum\frac{\sin^2 n}{n^2}$ converge?
A: Yes: $0 \le \frac{\sin^2 n}{n^2} \le \frac{1}{n^2}$.

Q: What comparison series for $\frac{\sqrt{n}}{n^2 + 1}$?
A: $\frac{1}{n^{3/2}}$ → converges.

Q: Why does "bigger than a convergent series" tell you nothing?
A: A bigger series could still converge or diverge.

@@ 3 | TOPIC | Ratio and root tests | Limit of the ratio of consecutive terms and of the nth root; factorials and powers; when the tests are inconclusive | SHOULD
# Ratio and root tests

## 1. What it covers
- The **ratio test**: $L = \lim\left\lvert\frac{a_{n+1}}{a_n}\right\rvert$.
- The **root test**: $L = \lim\sqrt[n]{\lvert a_n\rvert}$.
- $L < 1$ converges, $L > 1$ diverges, $L = 1$ inconclusive.
- Best for **factorials** and **exponentials** (ratio) and $n$th powers (root).

## 2. Where it is used
- **Power series:** finding the radius of convergence (next module).
- **Probability:** sums like $\sum\frac{\lambda^k}{k!}$ (Poisson distribution).
- **Algorithm analysis:** comparing growth of $n!$, $2^n$ and $n^k$.

## 3. What it means
For large $n$, if each term is about $L$ times the previous one, the series behaves like a **geometric series with ratio $L$**:
- $L < 1$: the terms shrink geometrically → converges.
- $L > 1$: they grow → diverges.
- $L = 1$: no conclusion (e.g. both $\sum\frac{1}{n}$ and $\sum\frac{1}{n^2}$ give $L = 1$).

The root test measures the same thing differently: the $n$th root of the $n$th term.

## 4. Formula & worked example
$$
L = \lim_{n \to \infty}\left\lvert\frac{a_{n+1}}{a_n}\right\rvert \qquad L = \lim_{n \to \infty}\sqrt[n]{\lvert a_n\rvert}
$$

**Example 1.** $\sum\frac{2^n}{n!}$: $\frac{a_{n+1}}{a_n} = \frac{2^{n+1}}{(n + 1)!}\cdot\frac{n!}{2^n} = \frac{2}{n + 1} \to 0$ → converges.

**Example 2.** $\sum\frac{n!}{10^n}$: ratio $= \frac{n + 1}{10} \to \infty$ → diverges (factorials beat exponentials).

**Example 3.** $\sum\frac{n^3}{3^n}$: ratio $= \frac{(n + 1)^3}{n^3}\cdot\frac{1}{3} \to \frac{1}{3}$ → converges.

**Example 4 (root).** $\sum\left(\frac{2n + 1}{3n - 1}\right)^n$: root $= \frac{2n + 1}{3n - 1} \to \frac{2}{3}$ → converges.

**Example 5 (inconclusive).** $\sum\frac{1}{n^2}$: ratio $\to 1$ → test fails (use the p-series test: converges).

## 5. Common mistakes
- Simplifying factorials wrongly: $\frac{(n + 1)!}{n!} = n + 1$.
- Concluding anything when $L = 1$.
- Using the ratio test on p-series (it always gives 1).

## 6. Practice
Q: Ratio test result for $\sum\frac{5^n}{n!}$?
A: $L = 0$ → converges.

Q: Ratio test for $\sum\frac{3^n}{n^2}$?
A: $L = 3$ → diverges.

Q: What does $L = 1$ mean?
A: Inconclusive.

Q: $\frac{(n + 2)!}{n!}$?
A: $(n + 2)(n + 1)$.

Q: Root test for $\sum\left(\frac{1}{\ln n}\right)^n$?
A: $L = \lim\frac{1}{\ln n} = 0$ → converges.

@@ 3 | TOPIC | Alternating series and absolute convergence | Leibniz test, error bound, absolute vs conditional convergence | SHOULD
# Alternating series and absolute convergence

## 1. What it covers
- **Alternating series** $\sum(-1)^n b_n$.
- The **alternating series (Leibniz) test**.
- The **error bound**: the error is at most the first omitted term.
- **Absolute** vs **conditional** convergence.

## 2. Where it is used
- **Numerical computing:** Taylor series for $\sin x$ and $\cos x$ alternate, and the error bound tells libraries how many terms to use.
- **Physics:** alternating sums in electrostatics and wave analysis.
- **Mathematics:** $1 - \frac{1}{3} + \frac{1}{5} - \cdots = \frac{\pi}{4}$ (Leibniz's formula for $\pi$).

## 3. What it means
**Leibniz test:** if $b_n$ is positive, **decreasing**, and **$\to 0$**, then $\sum(-1)^n b_n$ converges. Each step goes back and forth, overshooting by less each time, homing in on the limit like a pendulum settling.

**Error bound:** stopping after $N$ terms, the error is less than the next term $b_{N+1}$.

**Absolute convergence:** $\sum\lvert a_n\rvert$ converges. Then $\sum a_n$ converges too.
**Conditional convergence:** $\sum a_n$ converges but $\sum\lvert a_n\rvert$ doesn't (e.g. the alternating harmonic series). Strangely, a conditionally convergent series can be rearranged to add up to **any** number.

## 4. Formula & worked example
$$
\sum(-1)^{n}b_n \text{ converges if } b_{n+1} \le b_n \text{ and } b_n \to 0 \qquad \lvert S - S_N\rvert \le b_{N+1}
$$

**Example 1.** $\sum\frac{(-1)^{n+1}}{n} = 1 - \frac{1}{2} + \frac{1}{3} - \cdots$: $\frac{1}{n}$ decreases to 0 → converges (to $\ln 2$). Since $\sum\frac{1}{n}$ diverges, it's **conditionally** convergent.

**Example 2.** $\sum\frac{(-1)^n}{n^2}$: $\sum\frac{1}{n^2}$ converges → **absolutely** convergent.

**Example 3 (error).** How many terms of $\sum\frac{(-1)^{n+1}}{n^3}$ are needed for error $< 0.001$?
- Need $\frac{1}{(N + 1)^3} < 0.001$ → $N + 1 > 10$ → $N = 10$ terms.

**Example 4 (Leibniz π).**
```python
s = sum((-1) ** k / (2 * k + 1) for k in range(100_000))
print(4 * s)    # 3.14158265...  (error about 1e-5, as the bound predicts)
```
The error after $N$ terms is at most the next term, $\frac{1}{2N + 1}$; times 4 that's about $2 \times 10^{-5}$ here. This series converges correctly but slowly, so real software uses faster formulas.

**Example 5.** $\sum\frac{(-1)^n n}{n + 1}$: terms don't go to 0 → diverges.

## 5. Common mistakes
- Forgetting to check that $b_n$ decreases.
- Confusing conditional and absolute convergence.
- Using the error bound for non-alternating series.

## 6. Practice
Q: Does $\sum\frac{(-1)^n}{\sqrt{n}}$ converge?
A: Yes, conditionally.

Q: Is $\sum\frac{(-1)^n}{n^3}$ absolutely convergent?
A: Yes.

Q: Error bound after 5 terms of $\sum\frac{(-1)^{n+1}}{n!}$ (starting at $n = 1$)?
A: Less than $\frac{1}{6!} = \frac{1}{720}$.

Q: Define conditional convergence.
A: The series converges, but the series of absolute values diverges.

Q: Three conditions of the Leibniz test?
A: Alternating signs, $b_n$ decreasing, $b_n \to 0$.

@@ 2 | MODULE | Power series and Taylor series | Radius and interval of convergence, building Taylor and Maclaurin series, error bounds | SHOULD
Power series are "infinite polynomials"; Taylor series find the one that matches a given function.

@@ 3 | TOPIC | Power series and radius of convergence | ∑cₙ(x − a)ⁿ, radius and interval of convergence, differentiating and integrating series | SHOULD
# Power series and radius of convergence

## 1. What it covers
- **Power series** $\sum c_n(x - a)^n$.
- **Radius** and **interval of convergence** (using the ratio test, then checking endpoints).
- Differentiating and integrating power series term by term.
- Building new series from $\frac{1}{1 - x} = \sum x^n$.

## 2. Where it is used
- **Numerical computing:** evaluating functions efficiently.
- **Differential equations:** series solutions when no formula exists.
- **Combinatorics:** generating functions encode sequences as power series.
- **Physics:** perturbation methods expand quantities as power series in a small parameter.

## 3. What it means
A power series is like a polynomial that never ends. For each $x$ it's a number series, which may or may not converge.

A power series centred at $a$ converges:
- for all $x$ within distance $R$ of $a$ (the **radius of convergence**),
- diverges for all $x$ farther than $R$,
- and must be checked separately at the two endpoints $a \pm R$.

$R$ can be 0, a positive number, or $\infty$.

Inside the interval, you can **differentiate and integrate term by term**, which lets you build series for $\ln(1 + x)$, $\arctan x$ and more from the geometric series.

## 4. Formula & worked example
$$
\frac{1}{1 - x} = \sum_{n=0}^{\infty} x^n = 1 + x + x^2 + \cdots \quad (\lvert x\rvert < 1)
$$

**Example 1.** $\sum\frac{x^n}{n}$: ratio $= \frac{n}{n + 1}\lvert x\rvert \to \lvert x\rvert$ → $R = 1$.
- $x = 1$: harmonic → diverges; $x = -1$: alternating harmonic → converges → interval $[-1, 1)$.

**Example 2.** $\sum\frac{x^n}{n!}$: ratio $= \frac{\lvert x\rvert}{n + 1} \to 0$ for every $x$ → $R = \infty$.

**Example 3.** $\sum\frac{(x - 2)^n}{3^n}$: geometric with ratio $\frac{x - 2}{3}$ → converges for $\lvert x - 2\rvert < 3$ → $(-1, 5)$ (endpoints diverge).

**Example 4 (build).** Replace $x$ by $-x$: $\frac{1}{1 + x} = 1 - x + x^2 - \cdots$. Integrate: $\ln(1 + x) = x - \frac{x^2}{2} + \frac{x^3}{3} - \cdots$ ($-1 < x \le 1$).

**Example 5 (build).** Replace $x$ by $-x^2$: $\frac{1}{1 + x^2} = 1 - x^2 + x^4 - \cdots$; integrate: $\arctan x = x - \frac{x^3}{3} + \frac{x^5}{5} - \cdots$. At $x = 1$: $\frac{\pi}{4} = 1 - \frac{1}{3} + \frac{1}{5} - \cdots$ (Leibniz).

## 5. Common mistakes
- Forgetting to test the endpoints.
- Giving the radius when the interval is asked (or vice versa).
- Differentiating or integrating outside the interval of convergence.

## 6. Practice
Q: Radius of convergence of $\sum n x^n$?
A: $R = 1$.

Q: Radius of $\sum\frac{x^n}{2^n}$?
A: $R = 2$.

Q: Series for $\frac{1}{1 - 2x}$?
A: $\sum(2x)^n$, for $\lvert x\rvert < \frac{1}{2}$.

Q: Radius of $\sum n!\,x^n$?
A: $R = 0$ (converges only at $x = 0$).

Q: Series for $\ln(1 + x)$?
A: $x - \frac{x^2}{2} + \frac{x^3}{3} - \frac{x^4}{4} + \cdots$.

@@ 3 | TOPIC | Taylor and Maclaurin series | Building series from derivatives, the standard series for eˣ, sin x, cos x, ln(1 + x), error bounds | SHOULD
# Taylor and Maclaurin series

## 1. What it covers
- The **Taylor series** of $f$ about $a$, and the **Maclaurin series** (about 0).
- The standard series for $e^x$, $\sin x$, $\cos x$, $\ln(1 + x)$, $\frac{1}{1 - x}$, $(1 + x)^k$.
- **Taylor polynomials** as approximations.
- The **Lagrange error bound**.

## 2. Where it is used
- **Calculators and math libraries:** $\sin$, $\cos$, $e^x$ are computed from Taylor-type polynomials (after reducing the argument to a small range).
- **Physics:** small-angle approximations ($\sin\theta \approx \theta$, $\cos\theta \approx 1 - \frac{\theta^2}{2}$); relativity's $\frac{1}{\sqrt{1 - v^2/c^2}} \approx 1 + \frac{v^2}{2c^2}$.
- **Numerical methods and optimisation:** Newton's method and many ML optimisers use first- and second-order Taylor approximations of the loss.
- **Euler's formula** $e^{i\theta} = \cos\theta + i\sin\theta$ follows from these series.

## 3. What it means
Build a polynomial that matches $f$ at $x = a$: same value, same slope, same curvature, same third derivative, and so on. Matching more derivatives makes the polynomial hug the curve over a wider range. The coefficient of $(x - a)^n$ must be $\frac{f^{(n)}(a)}{n!}$ to match the $n$th derivative.

It's like describing a car's future position: "it's here (value), moving this fast (1st derivative), speeding up this much (2nd derivative)..." Each extra term improves the prediction.

**Error bound:** stopping at degree $n$, the error is at most $\frac{M}{(n + 1)!}\lvert x - a\rvert^{n+1}$, where $M$ bounds $\lvert f^{(n+1)}\rvert$ between $a$ and $x$.

## 4. Formula & worked example
$$
f(x) = \sum_{n=0}^{\infty}\frac{f^{(n)}(a)}{n!}(x - a)^n
$$
$$
e^x = \sum\frac{x^n}{n!} \qquad \sin x = x - \frac{x^3}{3!} + \frac{x^5}{5!} - \cdots \qquad \cos x = 1 - \frac{x^2}{2!} + \frac{x^4}{4!} - \cdots \qquad (1 + x)^k \approx 1 + kx + \frac{k(k - 1)}{2}x^2
$$

**Example 1 (build $e^x$).** Every derivative of $e^x$ is $e^x$, equal to 1 at 0 → $e^x = 1 + x + \frac{x^2}{2!} + \frac{x^3}{3!} + \cdots$

**Example 2 (estimate).** $e^{0.1} \approx 1 + 0.1 + 0.005 + 0.000167 = 1.105167$ (true $1.1051709$).

**Example 3 (substitute).** $e^{-x^2} = 1 - x^2 + \frac{x^4}{2} - \frac{x^6}{6} + \cdots$. Then $\int_0^1 e^{-x^2}dx \approx 1 - \frac{1}{3} + \frac{1}{10} - \frac{1}{42} + \frac{1}{216} \approx 0.7475$ (true $0.7468$) — an integral with no elementary antiderivative.

**Example 4 (Taylor about $a \ne 0$).** $\ln x$ about $a = 1$: $f(1) = 0$, $f' = 1$, $f'' = -1$, $f''' = 2$ → $\ln x \approx (x - 1) - \frac{(x - 1)^2}{2} + \frac{(x - 1)^3}{3}$.

**Example 5 (error bound).** Using $\sin x \approx x - \frac{x^3}{6}$ for $x = 0.5$: the next derivative is bounded by 1, so the error $\le \frac{0.5^5}{5!} \approx 0.00026$. (Actual: $0.47917$ vs $0.47943$.)

**Example 6 (code).**
```python
import math
def sin_taylor(x, terms=6):
    return sum((-1) ** k * x ** (2 * k + 1) / math.factorial(2 * k + 1) for k in range(terms))
print(sin_taylor(1.0), math.sin(1.0))   # 0.8414709848 0.8414709848
```
Six terms ($x$ up to $x^{11}$) already match `math.sin` to 10 digits at $x = 1$. Libraries first reduce $x$ into a small range (using periodicity) so few terms are needed.

## 5. Common mistakes
- Forgetting the factorials.
- Using a Maclaurin series far from 0 and expecting accuracy.
- Mixing up the sine (odd powers) and cosine (even powers) series.

## 6. Practice
Q: First three nonzero terms of the Maclaurin series of $\cos x$?
A: $1 - \frac{x^2}{2} + \frac{x^4}{24}$.

Q: Maclaurin series of $e^{2x}$ up to $x^3$?
A: $1 + 2x + 2x^2 + \frac{4}{3}x^3$.

Q: Coefficient of $(x - a)^n$ in a Taylor series?
A: $\frac{f^{(n)}(a)}{n!}$.

Q: Approximate $\sqrt{1.1}$ with $(1 + x)^{1/2} \approx 1 + \frac{x}{2} - \frac{x^2}{8}$.
A: $1 + 0.05 - 0.00125 = 1.04875$.

Q: Maclaurin series of $x\sin x$ up to $x^4$?
A: $x^2 - \frac{x^4}{6}$.

@@ 3 | TOPIC | Practice & review: Series | Mixed problem set with worked answers | SHOULD
# Practice & review: Series

**How to use this:** 50 minutes. For each series, write which test you use and why.

## Problems
1. $\sum_{n=1}^\infty\frac{3}{4^n}$
2. $\sum\frac{n^2}{3n^2 + 1}$
3. $\sum\frac{1}{n^{3/2}}$
4. $\sum\frac{n}{n^3 + 4}$
5. $\sum\frac{3^n}{n!}$
6. $\sum\frac{(-1)^n}{\ln n}$ ($n \ge 2$): absolutely, conditionally, or divergent?
7. Interval of convergence of $\sum\frac{(x + 1)^n}{n\,2^n}$.
8. Maclaurin series of $\frac{x}{1 - x^2}$.
9. Use the series for $e^x$ to find $\lim_{x \to 0}\frac{e^x - 1 - x}{x^2}$.
10. Estimate $\cos(0.2)$ with two nonzero terms and bound the error.

## Answers
1. Geometric: $\frac{3/4}{1 - 1/4} = 1$.
2. Terms → $\frac{1}{3}$ → diverges.
3. p-series, $p = 1.5$ → converges.
4. Limit comparison with $\frac{1}{n^2}$ → converges.
5. Ratio test: $\frac{3}{n + 1} \to 0$ → converges.
6. Leibniz: $\frac{1}{\ln n}$ decreases to 0 → converges; $\frac{1}{\ln n} > \frac{1}{n}$ so $\sum\frac{1}{\ln n}$ diverges → conditionally.
7. Ratio → $\frac{\lvert x + 1\rvert}{2}$ → $R = 2$, $-3 < x < 1$; $x = 1$: harmonic (diverges); $x = -3$: alternating harmonic (converges) → $[-3, 1)$.
8. $x\sum(x^2)^n = x + x^3 + x^5 + \cdots$ ($\lvert x\rvert < 1$).
9. $\frac{x^2/2 + x^3/6 + \cdots}{x^2} \to \frac{1}{2}$.
10. $1 - \frac{0.04}{2} = 0.98$; error $\le \frac{0.2^4}{24} \approx 0.000067$ (true $0.980067$).

## Flashcards
Q: Divergence test?
A: If $a_n \not\to 0$, the series diverges.

Q: p-series convergence?
A: $\sum\frac{1}{n^p}$ converges iff $p > 1$.

Q: Ratio test outcome if $L < 1$?
A: Converges (absolutely).

Q: Maclaurin series of $e^x$?
A: $\sum\frac{x^n}{n!}$.

@@ 2 | MODULE | Final review | Two cumulative mixed sets covering the whole roadmap | MUST
Mix everything. After this, continue with Statistics & Probability, Linear Algebra, or Multivariable Calculus & Differential Equations.

@@ 3 | TOPIC | Final review 1: Limits and derivatives | Cumulative mixed problems from Phases 1–3 with answers | MUST
# Final review 1: Limits and derivatives

**How to use this:** 60 minutes; calculator for (C).

## Problems
1. $\lim_{x \to 2}\frac{x^3 - 8}{x^2 - 4}$
2. $\lim_{x \to \infty}\left(\sqrt{x^2 + 4x} - x\right)$
3. $\frac{d}{dx}\left[\frac{\sin(2x)}{x^2 + 1}\right]$
4. $\frac{d}{dx}\left[e^{x}\ln(x^2 + 1)\right]$
5. Find $\frac{dy}{dx}$ for $e^{xy} = x + y$.
6. Tangent line to $y = \arctan(2x)$ at $x = \frac{1}{2}$.
7. (C) A spherical snowball melts so its volume decreases at 10 cm³/min. How fast is the radius decreasing when $r = 5$ cm?
8. Find and classify the critical points of $f(x) = x^4 - 8x^2 + 3$.
9. Find the dimensions of the rectangle of largest area inscribed in the ellipse $\frac{x^2}{9} + \frac{y^2}{4} = 1$ (sides parallel to the axes).
10. $\lim_{x \to 0}\frac{e^{2x} - 1 - 2x}{x^2}$

## Answers
1. $\frac{(x - 2)(x^2 + 2x + 4)}{(x - 2)(x + 2)} \to \frac{12}{4} = 3$.
2. Multiply by the conjugate: $\frac{4x}{\sqrt{x^2 + 4x} + x} \to \frac{4}{2} = 2$.
3. $\frac{2\cos(2x)(x^2 + 1) - 2x\sin(2x)}{(x^2 + 1)^2}$.
4. $e^x\ln(x^2 + 1) + \frac{2xe^x}{x^2 + 1}$.
5. $e^{xy}(y + xy') = 1 + y'$ → $y' = \frac{1 - ye^{xy}}{xe^{xy} - 1}$.
6. $y' = \frac{2}{1 + 4x^2} = 1$ at $x = \frac{1}{2}$; point $\left(\frac{1}{2}, \frac{\pi}{4}\right)$ → $y = x - \frac{1}{2} + \frac{\pi}{4}$.
7. $-10 = 4\pi(25)r'$ → $r' = -\frac{1}{10\pi} \approx -0.0318$ cm/min.
8. $f' = 4x(x^2 - 4)$ → $x = 0, \pm 2$; $f'' = 12x^2 - 16$: $f''(0) < 0$ → local max $f(0) = 3$; $f''(\pm 2) > 0$ → local minima $f(\pm 2) = -13$.
9. Area $A = 4xy$ with $y = 2\sqrt{1 - x^2/9}$; maximise $A^2$ or use $x = \frac{3}{\sqrt{2}}$, $y = \sqrt{2}$ → dimensions $3\sqrt{2} \times 2\sqrt{2}$, area 12.
10. L'Hôpital twice (or series): $\frac{2e^{2x} - 2}{2x} \to \frac{4e^{2x}}{2} \to 2$.

@@ 3 | TOPIC | Final review 2: Integrals, applications and series | Cumulative mixed problems from Phases 4–6 with answers | MUST
# Final review 2: Integrals, applications and series

**How to use this:** 60 minutes; calculator for (C).

## Problems
1. $\int_0^1\frac{x}{(x^2 + 1)^2}dx$
2. $\int x^2e^{-x}\,dx$
3. $\int\frac{3x + 5}{x^2 + 2x - 3}dx$
4. $\int_0^{\pi}\sin^2 x\,dx$
5. Does $\int_1^\infty\frac{\ln x}{x^2}dx$ converge? Find its value.
6. Area enclosed by $y = x^3$ and $y = x$ (all regions).
7. Volume when the region under $y = \sin x$, $0 \le x \le \pi$, is rotated about the x-axis.
8. Solve $y' = y\cos x$, $y(0) = 2$.
9. Does $\sum\frac{n^2}{2^n}$ converge?
10. (C) Use the first three nonzero terms of the series for $\arctan x$ to estimate $\arctan(0.2)$.

## Answers
1. $u = x^2 + 1$: $\frac{1}{2}\int_1^2 u^{-2}du = \frac{1}{2}\left(1 - \frac{1}{2}\right) = \frac{1}{4}$.
2. $-e^{-x}(x^2 + 2x + 2) + C$.
3. $\frac{3x + 5}{(x + 3)(x - 1)} = \frac{1}{x + 3} + \frac{2}{x - 1}$ → $\ln\lvert x + 3\rvert + 2\ln\lvert x - 1\rvert + C$.
4. $\frac{\pi}{2}$.
5. Parts: $\left[-\frac{\ln x}{x} - \frac{1}{x}\right]_1^\infty = 0 - (0 - 1) = 1$ → converges to 1.
6. They meet at $x = -1, 0, 1$: $2\int_0^1(x - x^3)\,dx = 2 \cdot \frac{1}{4} = \frac{1}{2}$.
7. $\pi\int_0^\pi\sin^2 x\,dx = \frac{\pi^2}{2}$.
8. $\ln\lvert y\rvert = \sin x + C$ → $y = 2e^{\sin x}$.
9. Ratio test: $\frac{(n + 1)^2}{2n^2} \to \frac{1}{2}$ → converges.
10. $0.2 - \frac{0.008}{3} + \frac{0.00032}{5} \approx 0.197397$ (true $0.197396$).
