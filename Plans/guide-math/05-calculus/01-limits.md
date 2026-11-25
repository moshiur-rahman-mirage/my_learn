@@ 1 | MODULE | Phase 1 — Limits and Continuity (Foundation) | The idea of a limit, limit laws, algebraic techniques, squeeze theorem, continuity, limits at infinity, IVT | MUST
# Phase 1: Limits and continuity

**Calculus** is the mathematics of **change** and **accumulation**. Its two big tools are the **derivative** (how fast something is changing right now) and the **integral** (how much has built up). Both rest on one idea: the **limit**, which describes what a function approaches as its input gets closer and closer to some value.

Calculus is everywhere in modern technology: physics engines, optimisation, machine learning (training a neural network is calculus: gradient descent), economics, medicine and engineering.

**What you need first:** the Algebra & Functions roadmap (functions, graphs, factoring, rational functions, exponentials and logs) and the Trigonometry roadmap.

Each topic has six parts: what it covers, where it is used, what it means, formulas with worked examples, common mistakes, and practice flashcards.

@@ 2 | MODULE | Limits | Meaning, notation, laws, algebraic techniques, squeeze theorem | MUST
A limit asks "where is the function heading?", not "where is it?".

@@ 3 | TOPIC | The idea of a limit: graphs, tables and one-sided limits | Approaching a value, notation, left and right limits, when a limit does not exist | MUST
# The idea of a limit: graphs, tables and one-sided limits

## 1. What it covers
- What $\lim_{x \to a} f(x) = L$ means.
- Estimating limits from **tables** and **graphs**.
- **One-sided limits** ($x \to a^-$ and $x \to a^+$).
- When a limit **does not exist**.

## 2. Where it is used
- **Derivatives and integrals:** both are defined as limits.
- **Numerical computing:** algorithms that converge (Newton's method, iterative solvers, training loss) are limits in practice.
- **Physics:** instantaneous speed is the limit of average speeds over shorter and shorter intervals.
- **Finance:** continuous compounding is the limit of compounding more and more often.

## 3. What it means
$\lim_{x \to a} f(x) = L$ means: **as $x$ gets closer and closer to $a$ (from both sides), $f(x)$ gets closer and closer to $L$**. What happens exactly **at** $a$ doesn't matter; $f(a)$ might even be undefined.

Think of walking towards a door: the limit is the door you're heading for, even if you never step through it.

**One-sided limits:** $x \to a^-$ means approaching from the **left** (values less than $a$), $x \to a^+$ from the **right**. The two-sided limit exists **only if both one-sided limits exist and are equal**.

**A limit does not exist (DNE) when:**
- the left and right limits differ (a jump);
- the function grows without bound (it goes to $\pm\infty$);
- the function oscillates forever (like $\sin\frac{1}{x}$ near 0).

## 4. Formula & worked example
$$
\lim_{x \to a} f(x) = L \iff \lim_{x \to a^-} f(x) = \lim_{x \to a^+} f(x) = L
$$

**Example 1 (table).** Estimate $\lim_{x \to 2} \frac{x^2 - 4}{x - 2}$.
| $x$ | 1.9 | 1.99 | 1.999 | 2.001 | 2.01 | 2.1 |
|---|---|---|---|---|---|---|
| $f(x)$ | 3.9 | 3.99 | 3.999 | 4.001 | 4.01 | 4.1 |
- The values approach 4 from both sides → limit 4, even though $f(2)$ is undefined ($\frac{0}{0}$).

**Example 2 (jump).** $f(x) = 1$ for $x < 0$ and $f(x) = 3$ for $x \ge 0$.
- $\lim_{x \to 0^-} f(x) = 1$, $\lim_{x \to 0^+} f(x) = 3$ → two-sided limit DNE.

**Example 3 (unbounded).** $\lim_{x \to 0} \frac{1}{x^2}$: values grow without bound → the limit is $\infty$ (it DNE as a number).

**Example 4 (hole).** $g(x) = x + 1$ for $x \ne 3$ and $g(3) = 10$. $\lim_{x \to 3} g(x) = 4$ (the value at 3 is irrelevant).

**Example 5 (code).** Watching a limit numerically.
```python
import math
for h in (0.1, 0.01, 0.001, 0.0001):
    print(h, math.sin(h) / h)
# 0.998334..., 0.999983..., 0.9999998..., 0.99999999...  -> approaching 1
```
As `h` shrinks, $\frac{\sin h}{h}$ gets closer to 1, so $\lim_{h \to 0}\frac{\sin h}{h} = 1$ (proved later with the squeeze theorem).

## 5. Common mistakes
- Thinking the limit must equal $f(a)$ (only true for continuous functions).
- Checking only one side.
- Trusting a table with too few or badly chosen values (calculators can also round misleadingly for very small $h$).

## 6. Practice
Q: What does $\lim_{x \to 5} f(x) = 2$ mean?
A: As $x$ approaches 5 from both sides, $f(x)$ approaches 2.

Q: Left limit 4, right limit 4, $f(a) = 7$. Two-sided limit?
A: $4$.

Q: Left limit 2, right limit $-1$. Two-sided limit?
A: Does not exist.

Q: $\lim_{x \to 0^+} \frac{1}{x}$?
A: $+\infty$ (unbounded).

Q: Name three ways a limit can fail to exist.
A: Different one-sided limits, unbounded growth, endless oscillation.

@@ 3 | TOPIC | Limit laws and direct substitution | Sum, product, quotient and power laws; substitution for continuous functions | MUST
# Limit laws and direct substitution

## 1. What it covers
- The **limit laws**: limits of sums, differences, products, quotients, powers and roots.
- **Direct substitution** for polynomials, rational functions (when the denominator isn't 0) and other continuous functions.
- What to do when substitution gives $\frac{0}{0}$ (next topic) or $\frac{k}{0}$.

## 2. Where it is used
- **Computing limits quickly** in every later topic.
- **Proving derivative rules** (sum rule, product rule) uses the limit laws.
- **Error analysis:** if each input converges, combinations converge too.

## 3. What it means
Limits behave nicely with arithmetic: the limit of a sum is the sum of the limits, and so on (as long as each limit exists and you don't divide by 0).

For **continuous** functions (polynomials, $\sin$, $\cos$, $e^x$, rational functions away from zeros of the denominator), the limit is simply the value: **plug in**.

**Substitution results:**
- A number → that's the limit.
- $\frac{k}{0}$ with $k \ne 0$ → the limit is infinite or DNE (check signs on each side).
- $\frac{0}{0}$ → **indeterminate**: more work needed (factor, rationalise, etc.).

## 4. Formula & worked example
$$
\lim(f \pm g) = \lim f \pm \lim g \qquad \lim(fg) = \lim f \cdot \lim g \qquad \lim\frac{f}{g} = \frac{\lim f}{\lim g}\ (\lim g \ne 0) \qquad \lim f^n = (\lim f)^n
$$

**Example 1.** $\lim_{x \to 3}(2x^2 - 5x + 1) = 18 - 15 + 1 = 4$

**Example 2.** $\lim_{x \to -1} \frac{x^2 + 3}{x - 2} = \frac{4}{-3} = -\frac{4}{3}$

**Example 3.** $\lim_{x \to \pi} (x\cos x) = \pi \cdot (-1) = -\pi$

**Example 4.** $\lim_{x \to 4} \sqrt{x^2 + 9} = \sqrt{25} = 5$

**Example 5 ($k/0$).** $\lim_{x \to 2} \frac{3}{x - 2}$: from the right $+\infty$, from the left $-\infty$ → DNE.

**Example 6 (laws).** If $\lim_{x \to a} f(x) = 6$ and $\lim_{x \to a} g(x) = -2$, then $\lim_{x \to a}\frac{f(x) + g(x)}{f(x)g(x)} = \frac{4}{-12} = -\frac{1}{3}$.

## 5. Common mistakes
- Calling $\frac{0}{0}$ "0" or "undefined, so DNE". It's indeterminate; the limit may well exist.
- Using the quotient law when the denominator's limit is 0.
- Forgetting to check both sides for $\frac{k}{0}$.

## 6. Practice
Q: $\lim_{x \to 2}(x^3 - 4x)$?
A: $0$.

Q: $\lim_{x \to 1}\frac{x + 4}{x^2 + 1}$?
A: $\frac{5}{2}$.

Q: Substitution gives $\frac{0}{0}$. What does that tell you?
A: The form is indeterminate; simplify before concluding.

Q: $\lim_{x \to 0} e^x\cos x$?
A: $1$.

Q: $\lim f = 3$, $\lim g = 5$. $\lim(2f - g^2)$?
A: $6 - 25 = -19$.

@@ 3 | TOPIC | Algebraic limit techniques for 0/0 | Factoring and cancelling, rationalising, simplifying complex fractions | MUST
# Algebraic limit techniques for 0/0

## 1. What it covers
- Resolving the indeterminate form $\frac{0}{0}$:
  - **factor and cancel**;
  - **rationalise** with a conjugate;
  - **simplify** compound fractions;
  - expand and simplify.

## 2. Where it is used
- **Derivatives from first principles:** the derivative definition always starts as $\frac{0}{0}$.
- **Removable discontinuities** (holes) in graphs.
- **Numerical stability:** rewriting $\sqrt{x + h} - \sqrt{x}$ as $\frac{h}{\sqrt{x + h} + \sqrt{x}}$ avoids catastrophic cancellation in floating-point code.

## 3. What it means
$\frac{0}{0}$ means a factor that is 0 at $x = a$ appears in both the top and bottom. The limit only cares about values **near** $a$, not at $a$, so you're allowed to cancel that factor (for $x \ne a$). After cancelling, substitute again.

**Strategy:**
1. Substitute. If you get a number, done.
2. If $\frac{0}{0}$: polynomials → **factor**; square roots → **multiply by the conjugate**; fractions inside fractions → **common denominator**.
3. Cancel the common factor, then substitute.

## 4. Formula & worked example
**Example 1 (factor).** $\lim_{x \to 3}\frac{x^2 - 9}{x - 3} = \lim_{x \to 3}\frac{(x - 3)(x + 3)}{x - 3} = \lim_{x \to 3}(x + 3) = 6$

**Example 2 (factor).** $\lim_{x \to -2}\frac{x^2 + 5x + 6}{x^2 - 4} = \lim\frac{(x + 2)(x + 3)}{(x + 2)(x - 2)} = \frac{1}{-4} = -\frac{1}{4}$

**Example 3 (rationalise).** $\lim_{x \to 0}\frac{\sqrt{x + 9} - 3}{x}$
- $\times \frac{\sqrt{x + 9} + 3}{\sqrt{x + 9} + 3}$: $\frac{x + 9 - 9}{x(\sqrt{x + 9} + 3)} = \frac{1}{\sqrt{x + 9} + 3} \to \frac{1}{6}$

**Example 4 (compound fraction).** $\lim_{x \to 2}\frac{\frac{1}{x} - \frac{1}{2}}{x - 2}$
- Top: $\frac{2 - x}{2x}$ → $\frac{2 - x}{2x(x - 2)} = \frac{-1}{2x} \to -\frac{1}{4}$

**Example 5 (expand).** $\lim_{h \to 0}\frac{(3 + h)^2 - 9}{h} = \lim\frac{6h + h^2}{h} = \lim(6 + h) = 6$ (this is the derivative of $x^2$ at 3).

**Example 6 (cubes).** $\lim_{x \to 1}\frac{x^3 - 1}{x - 1} = \lim(x^2 + x + 1) = 3$

## 5. Common mistakes
- Cancelling terms instead of factors.
- Concluding "DNE" as soon as you see $\frac{0}{0}$.
- Rationalising and forgetting to multiply the other part of the fraction too.

## 6. Practice
Q: $\lim_{x \to 4}\frac{x^2 - 16}{x - 4}$?
A: $8$.

Q: $\lim_{x \to 1}\frac{x^2 - 3x + 2}{x - 1}$?
A: $\lim(x - 2) = -1$.

Q: $\lim_{x \to 0}\frac{\sqrt{4 + x} - 2}{x}$?
A: $\frac{1}{4}$.

Q: $\lim_{h \to 0}\frac{(2 + h)^3 - 8}{h}$?
A: $12$.

Q: $\lim_{x \to 5}\frac{x - 5}{x^2 - 25}$?
A: $\frac{1}{10}$.

@@ 3 | TOPIC | Squeeze theorem and trig limits | Sandwiching a function, lim sin x / x = 1, lim (1 − cos x)/x = 0 | MUST
# Squeeze theorem and trig limits

## 1. What it covers
- The **squeeze (sandwich) theorem**.
- The key trig limits $\lim_{x \to 0}\frac{\sin x}{x} = 1$ and $\lim_{x \to 0}\frac{1 - \cos x}{x} = 0$.
- Using them to evaluate related limits.

## 2. Where it is used
- **Derivatives of sin and cos** are proved with these two limits.
- **Physics and engineering:** the small-angle approximation $\sin\theta \approx \theta$ (pendulums, optics, structural analysis) comes from $\frac{\sin x}{x} \to 1$.
- **Signal processing:** the sinc function $\frac{\sin x}{x}$ appears in sampling and filter design.
- **Proving limits** of oscillating functions.

## 3. What it means
**Squeeze theorem:** if $g(x) \le f(x) \le h(x)$ near $a$, and both $g$ and $h$ approach the same limit $L$, then $f$ is trapped and must approach $L$ too. Like a person walking between two police officers who both go to the station: the person ends up there as well.

**Example of use:** $x^2\sin\frac{1}{x}$ oscillates wildly near 0, but $-x^2 \le x^2\sin\frac{1}{x} \le x^2$, and both bounds go to 0, so the limit is 0.

**Why $\frac{\sin x}{x} \to 1$:** on the unit circle, for small positive $x$, the areas give $\cos x \le \frac{\sin x}{x} \le 1$. Both bounds approach 1, so the squeeze theorem gives 1. Numerically: $\sin(0.01) = 0.0099998$, almost exactly $0.01$.

## 4. Formula & worked example
$$
g(x) \le f(x) \le h(x),\ \lim g = \lim h = L \Rightarrow \lim f = L \qquad \lim_{x \to 0}\frac{\sin x}{x} = 1 \qquad \lim_{x \to 0}\frac{1 - \cos x}{x} = 0
$$
($x$ in radians.)

**Example 1.** $\lim_{x \to 0}\frac{\sin 5x}{x} = \lim 5 \cdot \frac{\sin 5x}{5x} = 5$

**Example 2.** $\lim_{x \to 0}\frac{\sin 3x}{\sin 7x} = \lim\frac{3}{7} \cdot \frac{\sin 3x}{3x} \cdot \frac{7x}{\sin 7x} = \frac{3}{7}$

**Example 3.** $\lim_{x \to 0}\frac{\tan x}{x} = \lim\frac{\sin x}{x} \cdot \frac{1}{\cos x} = 1$

**Example 4 (squeeze).** $\lim_{x \to 0} x\cos\frac{1}{x}$
- $-\lvert x\rvert \le x\cos\frac{1}{x} \le \lvert x\rvert$, both bounds → 0 → limit 0

**Example 5.** $\lim_{x \to 0}\frac{1 - \cos x}{x^2}$
- $\times\frac{1 + \cos x}{1 + \cos x}$: $\frac{\sin^2 x}{x^2(1 + \cos x)} \to 1 \cdot \frac{1}{2} = \frac{1}{2}$

## 5. Common mistakes
- Using degrees (these limits need radians).
- Writing $\frac{\sin 5x}{x} = \sin 5$.
- Using the squeeze theorem with bounds that approach different limits.

## 6. Practice
Q: $\lim_{x \to 0}\frac{\sin 4x}{x}$?
A: $4$.

Q: $\lim_{x \to 0}\frac{\sin x}{3x}$?
A: $\frac{1}{3}$.

Q: $\lim_{x \to 0} x^2\sin\frac{1}{x}$?
A: $0$ (squeeze).

Q: $\lim_{x \to 0}\frac{x}{\sin x}$?
A: $1$.

Q: State the squeeze theorem.
A: If $g \le f \le h$ near $a$ and $g, h \to L$, then $f \to L$.

@@ 2 | MODULE | Continuity and behaviour at infinity | Continuity, discontinuities, infinite limits, limits at infinity, IVT | MUST
A continuous function can be drawn without lifting your pen.

@@ 3 | TOPIC | Continuity and types of discontinuity | Three-part definition, removable, jump and infinite discontinuities, continuity on intervals | MUST
# Continuity and types of discontinuity

## 1. What it covers
- The definition of **continuity at a point** (three conditions).
- Types of discontinuity: **removable** (hole), **jump**, **infinite** (vertical asymptote).
- Which functions are continuous; continuity on an interval.
- Making a piecewise function continuous.

## 2. Where it is used
- **Physics and engineering:** physical quantities (temperature, position) change continuously; jumps signal events (a switch, a collision).
- **Numerical methods:** root-finding (bisection) and many theorems require continuity.
- **Signal processing:** discontinuities cause ringing artefacts.
- **Pricing:** tax brackets and shipping tiers are jump discontinuities.

## 3. What it means
$f$ is **continuous at $a$** if:
1. $f(a)$ is defined;
2. $\lim_{x \to a} f(x)$ exists;
3. they're equal: $\lim_{x \to a} f(x) = f(a)$.

Informally: no breaks, jumps or holes; you can draw it without lifting the pen.

**Discontinuity types:**
- **Removable:** the limit exists but $f(a)$ is missing or different (a hole). It can be "fixed" by redefining one point.
- **Jump:** the one-sided limits are different.
- **Infinite:** the function blows up (vertical asymptote).

Polynomials, $\sin$, $\cos$, $e^x$ are continuous everywhere; rational functions, $\ln x$, $\sqrt{x}$, $\tan x$ are continuous on their domains.

## 4. Formula & worked example
$$
f \text{ continuous at } a \iff \lim_{x \to a} f(x) = f(a)
$$

**Example 1.** $f(x) = \frac{x^2 - 1}{x - 1}$ at $x = 1$: undefined, but the limit is 2 → removable. Define $f(1) = 2$ to fix it.

**Example 2.** $f(x) = \frac{1}{x - 3}$ at $x = 3$: infinite discontinuity.

**Example 3.** $f(x) = \lfloor x\rfloor$ (floor function) at $x = 2$: left limit 1, right limit 2 → jump.

**Example 4 (make it continuous).** Find $k$ so that $f(x) = x^2 + k$ for $x < 2$ and $f(x) = 3x - 1$ for $x \ge 2$ is continuous.
- Left: $4 + k$; right: $5$ → $k = 1$

**Example 5.** Where is $f(x) = \frac{x + 2}{x^2 - 9}$ continuous? Everywhere except $x = \pm 3$.

## 5. Common mistakes
- Checking only whether $f(a)$ exists.
- Calling a hole a vertical asymptote.
- Forgetting to compare both one-sided pieces when making a piecewise function continuous.

## 6. Practice
Q: Three conditions for continuity at $a$?
A: $f(a)$ defined, the limit exists, and they are equal.

Q: Type of discontinuity of $\frac{x^2 - 4}{x - 2}$ at 2?
A: Removable.

Q: Type of discontinuity of $\frac{1}{(x + 1)^2}$ at $-1$?
A: Infinite.

Q: Find $a$ so $f(x) = ax + 1$ ($x < 1$), $f(x) = x^2 + 3$ ($x \ge 1$) is continuous.
A: $a + 1 = 4$ → $a = 3$.

Q: Is $f(x) = \lvert x\rvert$ continuous at 0?
A: Yes.

@@ 3 | TOPIC | Infinite limits and limits at infinity | Vertical asymptotes, end behaviour, comparing degrees, dominant terms | MUST
# Infinite limits and limits at infinity

## 1. What it covers
- **Infinite limits**: $f(x) \to \pm\infty$ as $x \to a$ (vertical asymptotes).
- **Limits at infinity**: $\lim_{x \to \pm\infty} f(x)$ (horizontal asymptotes, end behaviour).
- Rational functions: divide by the highest power.
- Growth rates: logs < powers < exponentials.

## 2. Where it is used
- **Algorithm analysis:** comparing $n^2$ vs $2^n$ vs $n\log n$ as $n \to \infty$ is exactly big-O thinking.
- **Long-run behaviour:** average cost, drug concentration, population limits.
- **Physics:** terminal velocity; charge on a capacitor approaching its maximum.
- **Machine learning:** the sigmoid $\frac{1}{1 + e^{-x}}$ approaches 0 and 1 at $\mp\infty$.

## 3. What it means
**Infinite limit:** near $a$, $f(x)$ grows without bound. Check the sign of the function on each side of $a$.

**Limit at infinity:** what $f(x)$ settles towards as $x$ becomes huge. For a rational function, only the **highest-power terms** matter for huge $x$ (they dominate). Divide top and bottom by the highest power of $x$ in the denominator:
- degree top < degree bottom → 0;
- equal degrees → ratio of leading coefficients;
- degree top > degree bottom → $\pm\infty$.

**Growth hierarchy** (as $x \to \infty$): $\ln x \ll x^p \ll e^x \ll x!$. Any exponential eventually beats any power.

## 4. Formula & worked example
$$
\lim_{x \to \infty}\frac{1}{x^p} = 0\ (p > 0) \qquad \lim_{x \to \infty} e^{-x} = 0 \qquad \lim_{x \to \infty}\frac{x^n}{e^x} = 0
$$

**Example 1.** $\lim_{x \to \infty}\frac{3x^2 - x}{5x^2 + 2} = \lim\frac{3 - 1/x}{5 + 2/x^2} = \frac{3}{5}$

**Example 2.** $\lim_{x \to \infty}\frac{4x + 1}{x^3} = 0$

**Example 3.** $\lim_{x \to -\infty}\frac{x^3}{x^2 + 1} = -\infty$

**Example 4 (infinite limit).** $\lim_{x \to 2^+}\frac{x}{x - 2}$: top → 2, bottom → $0^+$ → $+\infty$. From the left: $-\infty$.

**Example 5 (roots).** $\lim_{x \to \infty}\frac{\sqrt{9x^2 + 1}}{2x - 3} = \lim\frac{3x\sqrt{1 + 1/(9x^2)}}{2x - 3} = \frac{3}{2}$

**Example 6 (algorithms).** Is $n^{10}$ or $1.01^n$ bigger for very large $n$? Eventually $1.01^n$ (exponential beats any power), even though $n^{10}$ is far bigger for moderate $n$.

## 5. Common mistakes
- Dividing by the wrong power of $x$.
- Ignoring signs when $x \to -\infty$ (e.g. $\sqrt{x^2} = \lvert x\rvert = -x$ for negative $x$).
- Saying $\frac{\infty}{\infty} = 1$. It's indeterminate.

## 6. Practice
Q: $\lim_{x \to \infty}\frac{7x^3 + x}{2x^3 - 5}$?
A: $\frac{7}{2}$.

Q: $\lim_{x \to \infty}\frac{x^2}{x + 1}$?
A: $\infty$.

Q: $\lim_{x \to 3^-}\frac{1}{x - 3}$?
A: $-\infty$.

Q: $\lim_{x \to \infty} \frac{\ln x}{x}$?
A: $0$ (powers beat logs).

Q: Horizontal asymptote of $\frac{1}{1 + e^{-x}}$ as $x \to \infty$?
A: $y = 1$.

@@ 3 | TOPIC | Intermediate value theorem | Statement, guaranteeing roots, the bisection method | MUST
# Intermediate value theorem

## 1. What it covers
- The **intermediate value theorem (IVT)**.
- Using it to prove an equation has a solution in an interval.
- The **bisection method** for finding roots numerically.

## 2. Where it is used
- **Numerical root finding:** the bisection method (used in solvers, financial software for interest rates, and `scipy.optimize.bisect`).
- **Debugging:** `git bisect` uses the same halving idea to find the commit that introduced a bug.
- **Physics:** if temperature goes from 10°C to 30°C, at some moment it was exactly 20°C.
- **Proofs** of existence in analysis.

## 3. What it means
If $f$ is **continuous** on $[a, b]$, it takes **every value** between $f(a)$ and $f(b)$. You can't get from one height to another on a continuous path without passing through every height in between, like climbing a hill from 100 m to 500 m: you must pass 300 m at some point.

**Root version:** if $f(a)$ and $f(b)$ have **opposite signs**, there is at least one root ($f(c) = 0$) between $a$ and $b$.

The IVT says a value is reached; it doesn't say where or how many times.

**Bisection method:** check the sign at the midpoint and keep the half where the sign changes. Each step halves the interval.

## 4. Formula & worked example
$$
f \text{ continuous on } [a, b],\ N \text{ between } f(a), f(b) \Rightarrow \exists\, c \in (a, b): f(c) = N
$$

**Example 1.** Show $x^3 + x - 1 = 0$ has a root in $[0, 1]$.
- $f(0) = -1 < 0$, $f(1) = 1 > 0$, $f$ is continuous → a root exists.

**Example 2.** Show $\cos x = x$ has a solution in $[0, \frac{\pi}{2}]$.
- Let $g(x) = \cos x - x$: $g(0) = 1 > 0$, $g(\frac{\pi}{2}) = -\frac{\pi}{2} < 0$ → yes.

**Example 3 (bisection by hand).** Root of $f(x) = x^2 - 2$ in $[1, 2]$:
- $f(1.5) = 0.25 > 0$ → root in $[1, 1.5]$; $f(1.25) = -0.4375 < 0$ → $[1.25, 1.5]$; $f(1.375) \approx -0.109$ → $[1.375, 1.5]$; ... → approaches $\sqrt{2} \approx 1.414$.

**Example 4 (code).**
```python
def bisect(f, a, b, tol=1e-6):
    while b - a > tol:
        m = (a + b) / 2
        if f(a) * f(m) <= 0:   # sign change in the left half
            b = m
        else:
            a = m
    return (a + b) / 2
print(bisect(lambda x: x**3 + x - 1, 0, 1))   # 0.6823...
```
`f(a) * f(m) <= 0` means the values have opposite signs (or one is 0), so by the IVT a root lies in $[a, m]$. Each loop halves the interval, so 20 loops shrink it by a factor of about a million.

**Example 5 (IVT needs continuity).** $f(x) = \frac{1}{x}$ on $[-1, 1]$: $f(-1) = -1$, $f(1) = 1$, but there's no root, because $f$ isn't continuous at 0.

## 5. Common mistakes
- Applying the IVT to a discontinuous function.
- Concluding there's exactly one root (there could be several).
- Thinking the IVT tells you where the root is.

## 6. Practice
Q: State the IVT root version.
A: If $f$ is continuous on $[a, b]$ and $f(a)$, $f(b)$ have opposite signs, then $f$ has a root in $(a, b)$.

Q: $f(2) = 5$, $f(4) = -3$, $f$ continuous. Must $f(c) = 0$ for some $c$ in $(2, 4)$?
A: Yes.

Q: Does the IVT apply to $\tan x$ on $[0, \pi]$?
A: No, $\tan x$ is discontinuous at $\frac{\pi}{2}$.

Q: After 10 bisection steps on an interval of length 1, how wide is the interval?
A: $2^{-10} \approx 0.001$.

Q: Show $x^5 = 3$ has a solution between 1 and 2.
A: $f(x) = x^5 - 3$: $f(1) = -2$, $f(2) = 29$; continuous → yes.

@@ 3 | TOPIC | Practice & review: Limits and continuity | Mixed problem set with worked answers | MUST
# Practice & review: Limits and continuity

**How to use this:** 45 minutes, no calculator.

## Problems
1. $\lim_{x \to -3}\frac{x^2 + 2x - 3}{x + 3}$
2. $\lim_{x \to 0}\frac{\sqrt{1 + x} - 1}{x}$
3. $\lim_{x \to 0}\frac{\sin 6x}{2x}$
4. $\lim_{x \to 0}\frac{\tan 3x}{\sin 2x}$
5. $\lim_{x \to \infty}\frac{5x^4 - 3x}{2x^4 + x^2}$ and $\lim_{x \to -\infty}\frac{2x + 1}{\sqrt{x^2 + 4}}$
6. $\lim_{x \to 1^-}\frac{x}{x - 1}$
7. Find $c$ so that $f(x) = cx^2 - 1$ for $x \le 2$ and $f(x) = 4x + c$ for $x > 2$ is continuous.
8. Classify the discontinuities of $f(x) = \frac{x - 1}{x^2 - 1}$.
9. Use the IVT to show $e^x = 3 - x$ has a solution in $[0, 1]$.

## Answers
1. $\frac{(x + 3)(x - 1)}{x + 3} \to -4$.
2. $\frac{1}{\sqrt{1 + x} + 1} \to \frac{1}{2}$.
3. $3$.
4. $\frac{3x \cdot \frac{\tan 3x}{3x}}{2x \cdot \frac{\sin 2x}{2x}} \to \frac{3}{2}$.
5. $\frac{5}{2}$; for $x < 0$, $\sqrt{x^2 + 4} \approx \lvert x\rvert = -x$ → $\frac{2x}{-x} \to -2$.
6. $-\infty$.
7. $4c - 1 = 8 + c$ → $c = 3$.
8. $\frac{x - 1}{(x - 1)(x + 1)}$: removable at $x = 1$ (limit $\frac{1}{2}$), infinite at $x = -1$.
9. $g(x) = e^x + x - 3$: $g(0) = -2$, $g(1) = e - 2 \approx 0.72$; continuous → a root exists.

## Flashcards
Q: When does a two-sided limit exist?
A: When both one-sided limits exist and are equal.

Q: First thing to try for a limit?
A: Direct substitution.

Q: $\lim_{x \to 0}\frac{\sin x}{x}$?
A: $1$ (radians).

Q: Limit at infinity when top and bottom degrees are equal?
A: The ratio of the leading coefficients.
