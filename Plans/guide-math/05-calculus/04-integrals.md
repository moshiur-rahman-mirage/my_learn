@@ 1 | MODULE | Phase 4 — Integrals and Integration Techniques (Core) | Riemann sums, definite integrals, antiderivatives, the fundamental theorem, substitution, parts, trig methods, partial fractions, improper integrals, numerical integration | MUST
# Phase 4: Integrals and integration techniques

The **integral** adds up infinitely many tiny pieces to find a total: distance from speed, area under a curve, total water that flowed, total energy used, probability from a density. The **fundamental theorem of calculus** reveals that integration is the reverse of differentiation, which turns hard sums into easy formulas.

@@ 2 | MODULE | The definite integral and the fundamental theorem | Area and accumulation, Riemann sums, properties, antiderivatives, FTC | MUST
Integration = accumulation. The fundamental theorem connects it to derivatives.

@@ 3 | TOPIC | Accumulation, area and Riemann sums | Distance from velocity, left/right/midpoint sums, trapezoidal rule, sigma notation | MUST
# Accumulation, area and Riemann sums

## 1. What it covers
- The **area problem**: total accumulation as area under a rate curve.
- **Riemann sums**: approximating with rectangles (left, right, midpoint).
- The **trapezoidal rule**.
- Over- and under-estimates.

## 2. Where it is used
- **Physics:** distance travelled = area under the velocity–time graph.
- **Energy:** kWh used = area under the power–time graph (your electricity bill).
- **Data:** total rainfall, total traffic or total sales from rate measurements taken at intervals.
- **Numerical computing:** computers integrate by summing small pieces (e.g. `numpy.trapz`).
- **Medicine:** total drug exposure (AUC: area under the concentration curve).

## 3. What it means
If you drive at a constant 60 km/h for 2 h, the distance is $60 \times 2 = 120$ km: the area of a rectangle under the velocity graph. If the speed **changes**, split the time into small intervals, assume the speed is roughly constant in each, and add up the little rectangles. Thinner rectangles give a better estimate; in the limit you get the exact area: the **definite integral**.

- **Left sum:** each rectangle's height is the value at its left edge. **Right sum:** at the right edge. **Midpoint:** at the middle (usually more accurate).
- **Trapezoidal rule:** use trapezoids instead of rectangles (the average of left and right sums).

For an increasing function, left sums underestimate and right sums overestimate.

## 4. Formula & worked example
$$
\int_a^b f(x)\,dx = \lim_{n \to \infty}\sum_{i=1}^{n} f(x_i^*)\,\Delta x, \quad \Delta x = \frac{b - a}{n}
$$
$$
T_n = \frac{\Delta x}{2}\left[f(x_0) + 2f(x_1) + \cdots + 2f(x_{n-1}) + f(x_n)\right]
$$

**Example 1.** Estimate $\int_0^2 x^2\,dx$ with 4 rectangles ($\Delta x = 0.5$).
- Left: $0.5(0 + 0.25 + 1 + 2.25) = 1.75$
- Right: $0.5(0.25 + 1 + 2.25 + 4) = 3.75$
- Midpoint: $0.5(0.0625 + 0.5625 + 1.5625 + 3.0625) = 2.625$
- Trapezoid: $\frac{1.75 + 3.75}{2} = 2.75$
- Exact: $\frac{8}{3} \approx 2.667$

**Example 2 (data).** Velocities (m/s) every 10 s: 0, 4, 9, 12, 14. Distance in 40 s (trapezoid)?
- $\frac{10}{2}[0 + 2(4) + 2(9) + 2(12) + 14] = 5(64) = 320$ m

**Example 3 (code).**
```python
def midpoint_sum(f, a, b, n):
    dx = (b - a) / n
    return sum(f(a + (i + 0.5) * dx) for i in range(n)) * dx
for n in (4, 100, 10_000):
    print(n, midpoint_sum(lambda x: x**2, 0, 2, n))
# 4 2.625 / 100 2.6666 / 10000 2.66666666  -> approaching 8/3
```
`a + (i + 0.5) * dx` is the midpoint of the $i$-th strip. As `n` grows, the sum converges to the exact integral $\frac{8}{3}$.

## 5. Common mistakes
- Using $n + 1$ rectangles or the wrong endpoints.
- Forgetting the factor $\Delta x$.
- In the trapezoidal rule, doubling the first and last values (only the interior values are doubled).

## 6. Practice
Q: What does the area under a velocity–time graph represent?
A: Distance travelled (displacement).

Q: $\Delta x$ for $[1, 5]$ with $n = 8$?
A: $0.5$.

Q: Left sum of $f(x) = x$ on $[0, 4]$ with $n = 4$?
A: $1(0 + 1 + 2 + 3) = 6$.

Q: For an increasing function, does a right sum over- or underestimate?
A: Overestimate.

Q: How is the trapezoidal rule related to left and right sums?
A: It's their average.

@@ 3 | TOPIC | The definite integral: notation and properties | Signed area, bounds, linearity, additivity, reversing limits, using geometry | MUST
# The definite integral: notation and properties

## 1. What it covers
- Notation $\int_a^b f(x)\,dx$: integrand, limits, $dx$.
- **Signed area**: area below the axis counts as negative.
- Properties: linearity, additivity over intervals, reversing the limits, zero-width intervals.
- Evaluating with geometry (rectangles, triangles, semicircles).

## 2. Where it is used
- **Physics:** net displacement (forward minus backward).
- **Finance:** net cash flow (income minus spending).
- **Probability:** probabilities are integrals of densities over intervals; splitting intervals uses additivity.
- **Engineering:** net charge, net work.

## 3. What it means
$\int_a^b f(x)\,dx$ is the **net signed area** between the curve and the x-axis from $a$ to $b$: area above the axis is positive, area below is negative. The $\int$ sign is a stretched "S" for **sum**, and $dx$ is the width of each tiny strip.

**Properties:**
- Constants factor out; integrals of sums split.
- Additivity: $\int_a^b + \int_b^c = \int_a^c$.
- Reversing limits flips the sign.
- $\int_a^a f = 0$.

## 4. Formula & worked example
$$
\int_a^b [cf \pm g]\,dx = c\int_a^b f\,dx \pm \int_a^b g\,dx \qquad \int_a^c f = \int_a^b f + \int_b^c f \qquad \int_b^a f = -\int_a^b f
$$

**Example 1 (geometry).** $\int_0^4 (x + 1)\,dx$: a trapezoid with heights 1 and 5, width 4 → $12$.

**Example 2 (semicircle).** $\int_{-3}^{3}\sqrt{9 - x^2}\,dx$: upper half of a circle of radius 3 → $\frac{9\pi}{2}$.

**Example 3 (signed).** $\int_0^{2\pi}\sin x\,dx = 0$ (the hump above cancels the hump below).

**Example 4 (properties).** If $\int_0^5 f = 8$ and $\int_0^2 f = 3$, then $\int_2^5 f = 5$ and $\int_5^2 f = -5$.

**Example 5.** If $\int_1^4 f = 6$ and $\int_1^4 g = -2$, then $\int_1^4 (3f - 2g) = 18 + 4 = 22$.

## 5. Common mistakes
- Forgetting that area below the axis is negative in a definite integral.
- Reversing limits without changing the sign.
- Thinking $\int fg = \int f \cdot \int g$ (false).

## 6. Practice
Q: $\int_2^2 x^5\,dx$?
A: $0$.

Q: $\int_0^3 4\,dx$?
A: $12$.

Q: $\int_{-2}^{2} x^3\,dx$?
A: $0$ (odd function, symmetric interval).

Q: $\int_1^3 f = 7$. What is $\int_3^1 f$?
A: $-7$.

Q: $\int_0^2 \sqrt{4 - x^2}\,dx$ using geometry?
A: A quarter circle of radius 2: $\pi$.

@@ 3 | TOPIC | Antiderivatives and basic integration rules | Indefinite integrals, + C, power rule for integrals, trig, exponential and 1/x | MUST
# Antiderivatives and basic integration rules

## 1. What it covers
- **Antiderivatives**: functions whose derivative is the given function.
- **Indefinite integral** notation $\int f(x)\,dx = F(x) + C$.
- The basic rules: powers, $\frac{1}{x}$, $e^x$, $\sin$, $\cos$, $\sec^2$, and the inverse trig forms.
- Finding a specific antiderivative from an initial condition.

## 2. Where it is used
- **Physics:** position from velocity, velocity from acceleration ($v = \int a\,dt$).
- **Economics:** total cost from marginal cost.
- **Every definite integral** (via the fundamental theorem).
- **Differential equations:** the simplest ones are solved by antidifferentiation.

## 3. What it means
Antidifferentiation runs differentiation **backwards**: "which function has this derivative?" Since constants differentiate to 0, there's a whole family of answers differing by a constant, hence **$+ C$**. (Knowing only your speed, you can't tell where you started; that's the $C$.)

**Power rule for integrals:** add 1 to the exponent, then divide by the new exponent (except $n = -1$, which gives $\ln\lvert x\rvert$). Always check by differentiating your answer.

## 4. Formula & worked example
$$
\int x^n\,dx = \frac{x^{n+1}}{n + 1} + C\ (n \ne -1) \qquad \int\frac{1}{x}\,dx = \ln\lvert x\rvert + C \qquad \int e^x\,dx = e^x + C
$$
$$
\int\cos x\,dx = \sin x + C \quad \int\sin x\,dx = -\cos x + C \quad \int\sec^2 x\,dx = \tan x + C \quad \int\frac{dx}{1 + x^2} = \arctan x + C
$$

**Example 1.** $\int(6x^2 - 4x + 5)\,dx = 2x^3 - 2x^2 + 5x + C$

**Example 2.** $\int\frac{3}{x^4}\,dx = \int 3x^{-4}\,dx = -x^{-3} + C = -\frac{1}{x^3} + C$

**Example 3.** $\int\sqrt{x}\,dx = \frac{2}{3}x^{3/2} + C$

**Example 4.** $\int(2e^x - 3\sin x + \frac{1}{x})\,dx = 2e^x + 3\cos x + \ln\lvert x\rvert + C$

**Example 5 (initial condition).** $f'(x) = 4x - 1$, $f(2) = 5$.
- $f(x) = 2x^2 - x + C$; $8 - 2 + C = 5$ → $C = -1$ → $f(x) = 2x^2 - x - 1$

**Example 6 (physics).** $a(t) = -9.8$, $v(0) = 15$, $s(0) = 2$.
- $v = -9.8t + 15$; $s = -4.9t^2 + 15t + 2$

## 5. Common mistakes
- Forgetting $+ C$.
- $\int\frac{1}{x}dx = \frac{x^0}{0}$ ✗; it's $\ln\lvert x\rvert$.
- Sign errors with $\sin$/$\cos$: $\int\sin x\,dx = -\cos x$.

## 6. Practice
Q: $\int x^5\,dx$?
A: $\frac{x^6}{6} + C$.

Q: $\int\frac{1}{\sqrt{x}}\,dx$?
A: $2\sqrt{x} + C$.

Q: $\int(\cos x + e^x)\,dx$?
A: $\sin x + e^x + C$.

Q: $\int\frac{5}{x}\,dx$?
A: $5\ln\lvert x\rvert + C$.

Q: $f'(x) = 3x^2$ and $f(1) = 4$. Find $f$.
A: $f(x) = x^3 + 3$.

@@ 3 | TOPIC | The fundamental theorem of calculus | Part 1: derivative of an accumulation function; Part 2: evaluating definite integrals with antiderivatives | MUST
# The fundamental theorem of calculus

## 1. What it covers
- **FTC part 1:** if $F(x) = \int_a^x f(t)\,dt$, then $F'(x) = f(x)$.
- **FTC part 2:** $\int_a^b f(x)\,dx = F(b) - F(a)$ for any antiderivative $F$.
- Accumulation functions, and the chain rule with variable limits.

## 2. Where it is used
- **Everywhere integrals are computed exactly:** physics, engineering, probability, economics.
- **Probability:** the cumulative distribution function (CDF) is the integral of the density (PDF), and the PDF is the derivative of the CDF.
- **Accumulation models:** total water in a tank, total distance, total revenue.

## 3. What it means
**Part 1:** think of $F(x)$ as "the area so far" under $f$ from $a$ up to $x$. If you move $x$ a tiny bit to the right, the area grows by a thin strip of height $f(x)$. So the rate at which the accumulated area grows is $f(x)$. **Accumulating and then differentiating gets you back to the original function.**

**Part 2:** to find the exact area from $a$ to $b$, you don't need to add rectangles at all. Find any antiderivative $F$ and subtract: $F(b) - F(a)$. The $+C$ cancels.

This theorem connects the two halves of calculus: differentiation and integration are inverse operations.

## 4. Formula & worked example
$$
\frac{d}{dx}\int_a^x f(t)\,dt = f(x) \qquad \int_a^b f(x)\,dx = F(b) - F(a) = \Big[F(x)\Big]_a^b
$$
With a variable upper limit $g(x)$: $\frac{d}{dx}\int_a^{g(x)} f(t)\,dt = f(g(x))\,g'(x)$.

**Example 1.** $\int_0^2 x^2\,dx = \left[\frac{x^3}{3}\right]_0^2 = \frac{8}{3}$ (matches the Riemann sums).

**Example 2.** $\int_1^4 (3\sqrt{x} - 2)\,dx = \left[2x^{3/2} - 2x\right]_1^4 = (16 - 8) - (2 - 2) = 8$

**Example 3.** $\int_0^{\pi}\sin x\,dx = [-\cos x]_0^{\pi} = 1 + 1 = 2$

**Example 4.** $\int_1^e\frac{1}{x}\,dx = [\ln x]_1^e = 1$

**Example 5 (part 1).** $\frac{d}{dx}\int_2^x \cos(t^2)\,dt = \cos(x^2)$

**Example 6 (variable limit).** $\frac{d}{dx}\int_0^{x^3} e^{t}\,dt = e^{x^3}\cdot 3x^2$

## 5. Common mistakes
- Subtracting in the wrong order ($F(a) - F(b)$).
- Forgetting the chain-rule factor with a variable limit like $x^3$.
- Applying part 2 across a discontinuity of $f$ (e.g. $\int_{-1}^{1}\frac{1}{x^2}dx$ is improper, not $-2$).

## 6. Practice
Q: $\int_0^3 2x\,dx$?
A: $9$.

Q: $\int_0^{\pi/2}\cos x\,dx$?
A: $1$.

Q: $\frac{d}{dx}\int_1^x \sqrt{1 + t^3}\,dt$?
A: $\sqrt{1 + x^3}$.

Q: $\int_0^1 e^x\,dx$?
A: $e - 1$.

Q: $\frac{d}{dx}\int_0^{2x}\sin t\,dt$?
A: $2\sin(2x)$.

@@ 2 | MODULE | Integration techniques | Substitution, parts, trig integrals and substitution, partial fractions, improper integrals, strategy and numerical integration | MUST
Integration has no single mechanical recipe like differentiation does; these techniques are your toolbox.

@@ 3 | TOPIC | u-substitution | Reversing the chain rule, choosing u, definite integrals and changing limits | MUST
# u-substitution

## 1. What it covers
- **Substitution** as the chain rule in reverse.
- Choosing $u$ (usually the "inside" function whose derivative also appears).
- Adjusting constants.
- Definite integrals: changing the limits.

## 2. Where it is used
- **Physics and engineering:** integrals like $\int t e^{-t^2}dt$ and $\int\sin(\omega t)\,dt$.
- **Probability:** standardising the normal distribution is a substitution.
- **Almost every integral** that isn't a basic rule.

## 3. What it means
The chain rule says $\frac{d}{dx}F(g(x)) = f(g(x))g'(x)$. So when you see an integral of the form $\int f(g(x))\,g'(x)\,dx$, the answer is $F(g(x)) + C$.

**Method:**
1. Choose $u$ = an inner function whose derivative (up to a constant) is also in the integrand.
2. Compute $du = u'(x)\,dx$.
3. Rewrite everything in terms of $u$; there should be no $x$ left.
4. Integrate, then substitute back (or, for definite integrals, change the limits to $u$-values and don't substitute back).

## 4. Formula & worked example
$$
\int f(g(x))\,g'(x)\,dx = \int f(u)\,du \quad (u = g(x))
$$

**Example 1.** $\int 2x(x^2 + 1)^5\,dx$: $u = x^2 + 1$, $du = 2x\,dx$ → $\int u^5\,du = \frac{(x^2 + 1)^6}{6} + C$

**Example 2 (adjust a constant).** $\int x e^{x^2}\,dx$: $u = x^2$, $du = 2x\,dx$ → $\frac{1}{2}\int e^u\,du = \frac{1}{2}e^{x^2} + C$

**Example 3.** $\int\cos(3x)\,dx = \frac{1}{3}\sin(3x) + C$

**Example 4.** $\int\frac{x}{x^2 + 4}\,dx$: $u = x^2 + 4$ → $\frac{1}{2}\ln(x^2 + 4) + C$

**Example 5.** $\int\tan x\,dx = \int\frac{\sin x}{\cos x}dx$: $u = \cos x$, $du = -\sin x\,dx$ → $-\ln\lvert\cos x\rvert + C$

**Example 6 (definite, change limits).** $\int_0^2 x\sqrt{x^2 + 5}\,dx$: $u = x^2 + 5$; $x = 0 \to u = 5$, $x = 2 \to u = 9$
- $\frac{1}{2}\int_5^9 u^{1/2}\,du = \frac{1}{3}\left[u^{3/2}\right]_5^9 = \frac{1}{3}(27 - 5\sqrt{5}) \approx 5.27$

## 5. Common mistakes
- Leaving some $x$ in the integral after substituting.
- Forgetting to change the limits (or using $x$-limits with $u$).
- Choosing $u$ whose derivative isn't present (then substitution doesn't help).

## 6. Practice
Q: $\int 3x^2(x^3 - 2)^4\,dx$?
A: $\frac{(x^3 - 2)^5}{5} + C$.

Q: $\int e^{5x}\,dx$?
A: $\frac{1}{5}e^{5x} + C$.

Q: $\int\frac{2x}{x^2 + 1}\,dx$?
A: $\ln(x^2 + 1) + C$.

Q: $\int\sin x\cos^3 x\,dx$?
A: $-\frac{\cos^4 x}{4} + C$.

Q: $\int_0^1 2x e^{x^2}\,dx$?
A: $e - 1$.

@@ 3 | TOPIC | Integration by parts | ∫u dv = uv − ∫v du, choosing u (LIATE), repeated and cyclic cases | MUST
# Integration by parts

## 1. What it covers
- The formula $\int u\,dv = uv - \int v\,du$ (the product rule in reverse).
- Choosing $u$ with the **LIATE** guideline.
- Repeated parts and the **tabular** method.
- Cyclic integrals like $\int e^x\sin x\,dx$.
- $\int\ln x\,dx$.

## 2. Where it is used
- **Physics:** work and moments; expected values involving $xe^{-x}$.
- **Probability:** the mean and variance of the exponential distribution.
- **Signal processing:** Fourier coefficients ($\int x\sin(nx)\,dx$).
- **Laplace transforms** in engineering.

## 3. What it means
From the product rule $(uv)' = u'v + uv'$, integrate both sides and rearrange: $\int u\,dv = uv - \int v\,du$. It swaps a hard integral for (hopefully) an easier one.

**Choosing $u$ — LIATE:** pick $u$ from whichever comes first: **L**ogarithms, **I**nverse trig, **A**lgebraic (polynomials), **T**rig, **E**xponentials. The idea: $u$ should get simpler when differentiated, and $dv$ should be easy to integrate.

**Cyclic case:** after two rounds the original integral reappears; move it to the left side and solve for it.

## 4. Formula & worked example
$$
\int u\,dv = uv - \int v\,du
$$

**Example 1.** $\int xe^x\,dx$: $u = x$, $dv = e^x\,dx$ → $du = dx$, $v = e^x$
- $= xe^x - \int e^x\,dx = xe^x - e^x + C$

**Example 2.** $\int x\cos x\,dx$: $u = x$, $dv = \cos x\,dx$ → $x\sin x + \cos x + C$

**Example 3.** $\int\ln x\,dx$: $u = \ln x$, $dv = dx$ → $x\ln x - \int x\cdot\frac{1}{x}dx = x\ln x - x + C$

**Example 4 (repeated).** $\int x^2e^x\,dx = x^2e^x - \int 2xe^x\,dx = x^2e^x - 2(xe^x - e^x) + C = e^x(x^2 - 2x + 2) + C$

**Example 5 (cyclic).** $I = \int e^x\sin x\,dx$
- Parts ($u = \sin x$): $I = e^x\sin x - \int e^x\cos x\,dx$
- Parts again ($u = \cos x$): $\int e^x\cos x\,dx = e^x\cos x + I$
- $I = e^x\sin x - e^x\cos x - I$ → $I = \frac{e^x(\sin x - \cos x)}{2} + C$

**Example 6 (definite).** $\int_0^1 xe^{-x}\,dx = \left[-xe^{-x} - e^{-x}\right]_0^1 = (-2e^{-1}) - (-1) = 1 - \frac{2}{e} \approx 0.264$

## 5. Common mistakes
- Choosing $u$ and $dv$ the wrong way round (the new integral gets harder).
- Sign errors in repeated parts.
- Forgetting the $uv$ term or evaluating it at the limits in definite integrals.

## 6. Practice
Q: Formula for integration by parts?
A: $\int u\,dv = uv - \int v\,du$.

Q: $\int x\sin x\,dx$?
A: $-x\cos x + \sin x + C$.

Q: In $\int x^3\ln x\,dx$, what should $u$ be?
A: $\ln x$ (logarithm comes first in LIATE).

Q: $\int x^3\ln x\,dx$?
A: $\frac{x^4}{4}\ln x - \frac{x^4}{16} + C$.

Q: $\int\arctan x\,dx$?
A: $x\arctan x - \frac{1}{2}\ln(1 + x^2) + C$.

@@ 3 | TOPIC | Trigonometric integrals and trig substitution | Powers of sin and cos, power reduction, tan/sec; substituting x = a sin θ, a tan θ, a sec θ | MUST
# Trigonometric integrals and trig substitution

## 1. What it covers
- Integrals of powers of $\sin$ and $\cos$ (odd power: save one factor; even powers: power reduction).
- Integrals involving $\tan$ and $\sec$.
- **Trig substitution** for $\sqrt{a^2 - x^2}$, $\sqrt{a^2 + x^2}$, $\sqrt{x^2 - a^2}$.

## 2. Where it is used
- **Physics and electrical engineering:** average power in AC circuits uses $\int\sin^2(\omega t)\,dt$.
- **Geometry:** area of a circle or ellipse from $\int\sqrt{a^2 - x^2}\,dx$.
- **Signal processing:** orthogonality of sines and cosines in Fourier series.
- **Arc length and surface area** integrals.

## 3. What it means
**Powers of sin and cos:**
- If one power is **odd**, peel off one factor and convert the rest using $\sin^2 + \cos^2 = 1$, then substitute.
- If both are **even**, use power reduction: $\sin^2 x = \frac{1 - \cos 2x}{2}$, $\cos^2 x = \frac{1 + \cos 2x}{2}$.

**Trig substitution** turns square roots of quadratics into trig expressions, using Pythagorean identities to remove the root:
| Expression | Substitute | Identity used |
|---|---|---|
| $\sqrt{a^2 - x^2}$ | $x = a\sin\theta$ | $1 - \sin^2\theta = \cos^2\theta$ |
| $\sqrt{a^2 + x^2}$ | $x = a\tan\theta$ | $1 + \tan^2\theta = \sec^2\theta$ |
| $\sqrt{x^2 - a^2}$ | $x = a\sec\theta$ | $\sec^2\theta - 1 = \tan^2\theta$ |

Draw a right triangle from the substitution to convert the answer back to $x$.

## 4. Formula & worked example
**Example 1 (odd power).** $\int\sin^3 x\,dx = \int(1 - \cos^2 x)\sin x\,dx$; $u = \cos x$ → $-\cos x + \frac{\cos^3 x}{3} + C$

**Example 2 (even power).** $\int\cos^2 x\,dx = \int\frac{1 + \cos 2x}{2}dx = \frac{x}{2} + \frac{\sin 2x}{4} + C$

**Example 3 (AC power).** Average of $\sin^2(t)$ over one period $[0, 2\pi]$:
- $\frac{1}{2\pi}\int_0^{2\pi}\frac{1 - \cos 2t}{2}dt = \frac{1}{2\pi}\cdot\pi = \frac{1}{2}$ (why AC RMS voltage is peak ÷ $\sqrt{2}$)

**Example 4 (tan/sec).** $\int\tan^2 x\,dx = \int(\sec^2 x - 1)\,dx = \tan x - x + C$

**Example 5 (trig sub).** $\int_0^a\sqrt{a^2 - x^2}\,dx$ with $x = a\sin\theta$:
- $= \int_0^{\pi/2} a^2\cos^2\theta\,d\theta = a^2\cdot\frac{\pi}{4}$ → the quarter-circle area, as expected.

**Example 6.** $\int\frac{dx}{x^2\sqrt{9 - x^2}}$: $x = 3\sin\theta$, $dx = 3\cos\theta\,d\theta$
- $= \int\frac{3\cos\theta}{9\sin^2\theta\cdot 3\cos\theta}d\theta = \frac{1}{9}\int\csc^2\theta\,d\theta = -\frac{1}{9}\cot\theta + C = -\frac{\sqrt{9 - x^2}}{9x} + C$

## 5. Common mistakes
- Forgetting to convert $dx$ in trig substitution.
- Leaving the answer in terms of $\theta$.
- Using power reduction when an odd power makes substitution easier.

## 6. Practice
Q: $\int\sin^2 x\,dx$?
A: $\frac{x}{2} - \frac{\sin 2x}{4} + C$.

Q: $\int\cos^3 x\,dx$?
A: $\sin x - \frac{\sin^3 x}{3} + C$.

Q: Substitution for $\sqrt{16 + x^2}$?
A: $x = 4\tan\theta$.

Q: $\int\sec^2 x\tan x\,dx$?
A: $\frac{\tan^2 x}{2} + C$ (with $u = \tan x$).

Q: Average value of $\cos^2 t$ over a full period?
A: $\frac{1}{2}$.

@@ 3 | TOPIC | Integrating rational functions with partial fractions | Decomposing into simpler fractions, distinct, repeated and irreducible quadratic factors | MUST
# Integrating rational functions with partial fractions

## 1. What it covers
- Splitting $\frac{P(x)}{Q(x)}$ into simpler fractions.
- Cases: distinct linear factors, repeated linear factors, irreducible quadratic factors.
- Polynomial division first when the degree of the top is at least the degree of the bottom.

## 2. Where it is used
- **Control engineering:** inverse Laplace transforms of system responses.
- **Population biology:** solving the logistic equation uses $\frac{1}{P(K - P)}$ = partial fractions.
- **Chemistry:** reaction-rate integrals.
- **Probability:** some generating-function calculations.

## 3. What it means
Adding fractions combines them; partial fractions **un-adds** them into pieces you can integrate easily (each piece gives a log or an arctan).

**Forms:**
- Distinct linear factors: $\frac{A}{x - a} + \frac{B}{x - b}$.
- Repeated factor $(x - a)^2$: $\frac{A}{x - a} + \frac{B}{(x - a)^2}$.
- Irreducible quadratic $x^2 + c$: $\frac{Ax + B}{x^2 + c}$.

Find the constants by multiplying out and substituting convenient $x$ values (or by comparing coefficients).

## 4. Formula & worked example
$$
\int\frac{dx}{x - a} = \ln\lvert x - a\rvert + C \qquad \int\frac{dx}{(x - a)^2} = -\frac{1}{x - a} + C \qquad \int\frac{dx}{x^2 + a^2} = \frac{1}{a}\arctan\frac{x}{a} + C
$$

**Example 1.** $\int\frac{5x - 1}{(x - 1)(x + 1)}\,dx$
- $5x - 1 = A(x + 1) + B(x - 1)$; $x = 1$: $4 = 2A$ → $A = 2$; $x = -1$: $-6 = -2B$ → $B = 3$
- $= 2\ln\lvert x - 1\rvert + 3\ln\lvert x + 1\rvert + C$

**Example 2 (repeated).** $\int\frac{x + 3}{(x - 1)^2}\,dx$
- $x + 3 = A(x - 1) + B$ → $A = 1$, $B = 4$
- $= \ln\lvert x - 1\rvert - \frac{4}{x - 1} + C$

**Example 3 (quadratic).** $\int\frac{2}{x(x^2 + 1)}\,dx$
- $\frac{2}{x(x^2 + 1)} = \frac{2}{x} - \frac{2x}{x^2 + 1}$ → $2\ln\lvert x\rvert - \ln(x^2 + 1) + C$

**Example 4 (divide first).** $\int\frac{x^2}{x - 1}\,dx = \int\left(x + 1 + \frac{1}{x - 1}\right)dx = \frac{x^2}{2} + x + \ln\lvert x - 1\rvert + C$

## 5. Common mistakes
- Skipping the division step when the top's degree is too high.
- Using a constant (not $Ax + B$) over an irreducible quadratic.
- Missing the $\frac{B}{(x - a)^2}$ term for a repeated factor.

## 6. Practice
Q: Partial fraction form of $\frac{1}{(x - 2)(x + 3)}$?
A: $\frac{A}{x - 2} + \frac{B}{x + 3}$ with $A = \frac{1}{5}$, $B = -\frac{1}{5}$.

Q: $\int\frac{1}{x^2 - 1}\,dx$?
A: $\frac{1}{2}\ln\left\lvert\frac{x - 1}{x + 1}\right\rvert + C$.

Q: Form for $\frac{3x + 1}{(x + 2)^2}$?
A: $\frac{A}{x + 2} + \frac{B}{(x + 2)^2}$.

Q: $\int\frac{1}{x^2 + 9}\,dx$?
A: $\frac{1}{3}\arctan\frac{x}{3} + C$.

Q: When must you divide before using partial fractions?
A: When the top's degree is at least the bottom's.

@@ 3 | TOPIC | Improper integrals | Infinite limits of integration, infinite discontinuities, convergence and divergence, p-integrals | MUST
# Improper integrals

## 1. What it covers
- Integrals over **infinite intervals**: $\int_a^\infty f(x)\,dx$.
- Integrals of functions with **infinite discontinuities** (vertical asymptotes).
- **Convergence** vs **divergence**.
- The p-integral test: $\int_1^\infty\frac{dx}{x^p}$.

## 2. Where it is used
- **Probability:** total probability $\int_{-\infty}^{\infty} f(x)\,dx = 1$; expected values of continuous distributions (exponential, normal).
- **Physics:** escape velocity: the work to move a rocket from Earth to "infinity" is finite.
- **Engineering:** Laplace transforms $\int_0^\infty e^{-st}f(t)\,dt$.
- **Series convergence** (the integral test, Phase 6).

## 3. What it means
You can't plug in $\infty$, so use a limit: integrate to a finite $b$, then let $b \to \infty$. If the limit is a finite number, the integral **converges**; otherwise it **diverges**.

Surprisingly, an infinitely long region can have **finite area** if the curve drops fast enough: $\frac{1}{x^2}$ does, $\frac{1}{x}$ doesn't.

**p-integrals:** $\int_1^\infty\frac{1}{x^p}dx$ converges if $p > 1$ and diverges if $p \le 1$.

For a vertical asymptote at an endpoint (or inside the interval), approach it with a limit as well.

## 4. Formula & worked example
$$
\int_a^\infty f(x)\,dx = \lim_{b \to \infty}\int_a^b f(x)\,dx \qquad \int_1^\infty\frac{dx}{x^p} = \frac{1}{p - 1}\ (p > 1)
$$

**Example 1.** $\int_1^\infty\frac{1}{x^2}dx = \lim_{b \to \infty}\left[-\frac{1}{x}\right]_1^b = \lim\left(1 - \frac{1}{b}\right) = 1$ (converges)

**Example 2.** $\int_1^\infty\frac{1}{x}dx = \lim[\ln b - 0] = \infty$ (diverges)

**Example 3 (probability).** $\int_0^\infty 2e^{-2x}\,dx = \lim[-e^{-2x}]_0^b = 1$ (a valid probability density)

**Example 4 (asymptote).** $\int_0^1\frac{1}{\sqrt{x}}dx = \lim_{a \to 0^+}[2\sqrt{x}]_a^1 = 2$ (converges)

**Example 5 (asymptote inside).** $\int_{-1}^{1}\frac{1}{x^2}dx$: split at 0; $\int_0^1\frac{1}{x^2}dx = \infty$ → diverges. (Blindly applying the FTC gives $-2$, which is wrong.)

**Example 6 (expected value).** Mean of the exponential distribution with rate $\lambda$: $\int_0^\infty x\lambda e^{-\lambda x}\,dx = \frac{1}{\lambda}$ (by parts).

## 5. Common mistakes
- Treating $\infty$ like a number in $F(\infty)$.
- Missing an asymptote inside the interval.
- Thinking "infinite region" always means infinite area.

## 6. Practice
Q: Does $\int_1^\infty\frac{1}{x^3}dx$ converge? Value?
A: Yes, $\frac{1}{2}$.

Q: Does $\int_1^\infty\frac{1}{\sqrt{x}}dx$ converge?
A: No ($p = \frac{1}{2} \le 1$).

Q: $\int_0^\infty e^{-x}dx$?
A: $1$.

Q: $\int_0^4\frac{1}{\sqrt{x}}dx$?
A: $4$.

Q: Why is $\int_{-1}^{1}\frac{dx}{x^2} \ne -2$?
A: The integrand has an infinite discontinuity at 0; the integral diverges.

@@ 3 | TOPIC | Integration strategy and numerical integration | Choosing a method, integrals with no elementary antiderivative, Simpson's rule and code | MUST
# Integration strategy and numerical integration

## 1. What it covers
- A checklist for choosing an integration technique.
- Integrals with **no elementary antiderivative** (like $\int e^{-x^2}dx$).
- **Simpson's rule** and comparing numerical methods.
- Integrating numerically in code.

## 2. Where it is used
- **Statistics:** the normal distribution's CDF has no closed form; software computes it numerically.
- **Engineering simulations:** integrals of measured data or complicated functions.
- **Science:** computing areas, volumes and totals from sensor data.
- **Libraries:** `scipy.integrate.quad`, `numpy.trapz`.

## 3. What it means
**Strategy checklist:**
1. **Simplify** (algebra, trig identities, split fractions).
2. Is it a **basic rule**?
3. Is an inner function's derivative present? → **substitution**.
4. Product of different types (polynomial × exponential/trig, or a log)? → **parts**.
5. Powers of trig functions? → **trig identities**.
6. $\sqrt{a^2 \pm x^2}$ or $\sqrt{x^2 - a^2}$? → **trig substitution**.
7. Rational function? → **partial fractions** (divide first if needed).
8. None works? → it may have **no elementary antiderivative**: integrate **numerically**.

**Simpson's rule** fits parabolas through groups of three points; it's much more accurate than the trapezoidal rule for smooth functions (error shrinks like $\frac{1}{n^4}$ instead of $\frac{1}{n^2}$).

## 4. Formula & worked example
$$
S_n = \frac{\Delta x}{3}\left[f(x_0) + 4f(x_1) + 2f(x_2) + 4f(x_3) + \cdots + 4f(x_{n-1}) + f(x_n)\right] \quad (n \text{ even})
$$

**Example 1 (choose).** $\int x\sqrt{x + 1}\,dx$ → substitution $u = x + 1$ ($x = u - 1$): $\int(u - 1)u^{1/2}du = \frac{2}{5}(x + 1)^{5/2} - \frac{2}{3}(x + 1)^{3/2} + C$

**Example 2 (choose).** $\int x^2\sin x\,dx$ → parts twice.

**Example 3 (choose).** $\int\frac{dx}{x^2 - 4x + 3}$ → partial fractions: $\frac{1}{(x - 1)(x - 3)}$.

**Example 4 (Simpson by hand).** $\int_0^2 x^3\,dx$ with $n = 2$ ($\Delta x = 1$): $\frac{1}{3}[0 + 4(1) + 8] = 4$, exactly right (Simpson's rule is exact for cubics).

**Example 5 (code).**
```python
import math
from scipy.integrate import quad
f = lambda x: math.exp(-x**2)
value, err = quad(f, 0, 1)
print(value)                      # 0.746824132812427

def simpson(f, a, b, n=10):       # n must be even
    h = (b - a) / n
    s = f(a) + f(b) + sum((4 if i % 2 else 2) * f(a + i * h) for i in range(1, n))
    return s * h / 3
print(simpson(f, 0, 1))           # 0.7468249482...  (already 6 correct digits with n = 10)
```
$\int_0^1 e^{-x^2}dx$ has no formula in elementary functions, but numerical methods give it to high accuracy. `quad` is an adaptive library routine; the hand-written `simpson` applies the weights 1, 4, 2, 4, ..., 4, 1.

## 5. Common mistakes
- Trying a technique for a long time without simplifying first.
- Using Simpson's rule with an odd number of intervals.
- Assuming every integral has a formula.

## 6. Practice
Q: Best method for $\int x e^{3x}dx$?
A: Integration by parts.

Q: Best method for $\int\frac{\ln x}{x}dx$?
A: Substitution $u = \ln x$ → $\frac{(\ln x)^2}{2} + C$.

Q: Best method for $\int\frac{x + 5}{x^2 + x - 2}dx$?
A: Partial fractions.

Q: Name an integral with no elementary antiderivative.
A: $\int e^{-x^2}dx$ (also $\int\frac{\sin x}{x}dx$).

Q: Why is Simpson's rule more accurate than the trapezoidal rule?
A: It fits parabolas instead of straight lines, so its error shrinks much faster.

@@ 3 | TOPIC | Practice & review: Integrals and techniques | Mixed problem set with worked answers | MUST
# Practice & review: Integrals and techniques

**How to use this:** 60 minutes, no calculator. Check indefinite answers by differentiating.

## Problems
1. Estimate $\int_1^3\frac{1}{x}dx$ with the trapezoidal rule, $n = 4$.
2. $\int(4x^3 - \frac{2}{\sqrt{x}} + e^x)\,dx$
3. $\int_1^2\left(x^2 + \frac{1}{x^2}\right)dx$
4. $\frac{d}{dx}\int_x^{5}\sqrt{t^2 + 1}\,dt$
5. $\int\frac{\cos x}{1 + \sin x}\,dx$
6. $\int_0^{\pi/2}\sin^3 x\cos x\,dx$
7. $\int x^2\ln x\,dx$
8. $\int\frac{x + 7}{x^2 - x - 6}\,dx$
9. $\int_0^\infty xe^{-x}\,dx$
10. $\int\frac{dx}{\sqrt{4 - x^2}}$

## Answers
1. $\Delta x = 0.5$: $\frac{0.5}{2}[1 + 2(0.6667) + 2(0.5) + 2(0.4) + 0.3333] = 0.25(4.4667) \approx 1.1167$ (exact $\ln 3 \approx 1.0986$).
2. $x^4 - 4\sqrt{x} + e^x + C$.
3. $\left[\frac{x^3}{3} - \frac{1}{x}\right]_1^2 = \left(\frac{8}{3} - \frac{1}{2}\right) - \left(\frac{1}{3} - 1\right) = \frac{17}{6}$.
4. $-\sqrt{x^2 + 1}$ (the variable is the lower limit).
5. $\ln\lvert 1 + \sin x\rvert + C$.
6. $u = \sin x$: $\int_0^1 u^3du = \frac{1}{4}$.
7. $\frac{x^3}{3}\ln x - \frac{x^3}{9} + C$.
8. $\frac{x + 7}{(x - 3)(x + 2)} = \frac{2}{x - 3} - \frac{1}{x + 2}$ → $2\ln\lvert x - 3\rvert - \ln\lvert x + 2\rvert + C$.
9. $1$.
10. $\arcsin\frac{x}{2} + C$.

## Flashcards
Q: FTC part 2?
A: $\int_a^b f(x)\,dx = F(b) - F(a)$.

Q: Integration by parts formula?
A: $\int u\,dv = uv - \int v\,du$.

Q: When does $\int_1^\infty\frac{dx}{x^p}$ converge?
A: When $p > 1$.

Q: LIATE order?
A: Logarithmic, inverse trig, algebraic, trig, exponential.
