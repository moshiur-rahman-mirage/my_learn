@@ 1 | MODULE | Phase 2 — Derivatives (Core) | Definition, derivative graphs, differentiability, power/sum/product/quotient/chain rules, implicit, inverse, exponential and log derivatives | MUST
# Phase 2: Derivatives

The **derivative** measures **instantaneous rate of change**: the speed shown on a speedometer at this exact moment, the slope of a curve at one point, the marginal cost of making one more item. Once you know the rules in this phase, you can differentiate almost any formula mechanically.

In machine learning, every training step computes derivatives (gradients) of a loss function with respect to millions of parameters; libraries like PyTorch do this with the **chain rule** automatically ("autograd").

@@ 2 | MODULE | Definition and basic rules | Rate of change, the limit definition, differentiability, power, sum, trig, exponential, product and quotient rules | MUST
Learn what a derivative **is** first, then the shortcuts.

@@ 3 | TOPIC | The derivative as a limit | From average to instantaneous rate, secant to tangent, the difference quotient, notation | MUST
# The derivative as a limit

## 1. What it covers
- From **average** rate of change (secant slope) to **instantaneous** rate (tangent slope).
- The definition $f'(x) = \lim_{h \to 0}\frac{f(x + h) - f(x)}{h}$.
- Computing derivatives from the definition.
- Notation: $f'(x)$, $\frac{dy}{dx}$, $\frac{d}{dx}f(x)$, $\dot{x}$.

## 2. Where it is used
- **Physics:** velocity is the derivative of position; acceleration is the derivative of velocity.
- **Economics:** marginal cost and marginal revenue.
- **Machine learning:** gradients drive training (gradient descent).
- **Numerical computing:** finite differences $\frac{f(x + h) - f(x)}{h}$ approximate derivatives in simulations.
- **Biology and medicine:** growth rates, rate of drug absorption.

## 3. What it means
The average rate of change between $x$ and $x + h$ is the slope of the **secant** line through two points on the curve. Now slide the second point towards the first ($h \to 0$). The secant turns into the **tangent** line, the line that just touches the curve at that point. Its slope is the derivative.

Think of a car journey: average speed over the last hour is a secant; the speedometer reading right now is the tangent.

**Notation:** $f'(x)$ (Lagrange) and $\frac{dy}{dx}$ (Leibniz: "a tiny change in $y$ per tiny change in $x$"). They mean the same thing.

## 4. Formula & worked example
$$
f'(x) = \lim_{h \to 0}\frac{f(x + h) - f(x)}{h}
$$

**Example 1.** $f(x) = x^2$.
$$
f'(x) = \lim_{h \to 0}\frac{(x + h)^2 - x^2}{h} = \lim_{h \to 0}\frac{2xh + h^2}{h} = \lim_{h \to 0}(2x + h) = 2x
$$
At $x = 3$ the tangent slope is 6.

**Example 2.** $f(x) = 3x - 7$: $\frac{3(x + h) - 7 - (3x - 7)}{h} = 3$ → $f'(x) = 3$ (a line's slope is constant).

**Example 3.** $f(x) = \frac{1}{x}$.
$$
\frac{\frac{1}{x + h} - \frac{1}{x}}{h} = \frac{x - (x + h)}{h\,x(x + h)} = \frac{-1}{x(x + h)} \to -\frac{1}{x^2}
$$

**Example 4.** $f(x) = \sqrt{x}$: rationalise: $\frac{\sqrt{x + h} - \sqrt{x}}{h} = \frac{1}{\sqrt{x + h} + \sqrt{x}} \to \frac{1}{2\sqrt{x}}$.

**Example 5 (numerical, code).**
```python
def derivative(f, x, h=1e-6):
    return (f(x + h) - f(x - h)) / (2 * h)   # central difference
print(derivative(lambda x: x**2, 3))          # 6.000000000...
```
This replaces the limit with a very small `h`. The **central** difference (using $x + h$ and $x - h$) is more accurate than the one-sided version. Making `h` too small causes floating-point rounding errors, so around $10^{-6}$ is a common choice.

## 5. Common mistakes
- Expanding $f(x + h)$ wrongly (e.g. $(x + h)^2 = x^2 + h^2$ ✗).
- Setting $h = 0$ before simplifying (gives $\frac{0}{0}$).
- Confusing the derivative function $f'(x)$ with its value at one point $f'(a)$.

## 6. Practice
Q: Write the limit definition of the derivative.
A: $f'(x) = \lim_{h \to 0}\frac{f(x + h) - f(x)}{h}$.

Q: Use the definition to differentiate $f(x) = 5x^2$.
A: $10x$.

Q: Geometric meaning of $f'(a)$?
A: The slope of the tangent line at $x = a$.

Q: $s(t)$ is position. What is $s'(t)$?
A: Velocity.

Q: Why is the central difference more accurate than $\frac{f(x + h) - f(x)}{h}$?
A: Its errors from the two sides cancel to first order.

@@ 3 | TOPIC | Derivative graphs and differentiability | Sketching f′ from f, where derivatives fail (corners, cusps, vertical tangents, discontinuities) | MUST
# Derivative graphs and differentiability

## 1. What it covers
- Sketching the graph of $f'$ from the graph of $f$.
- Reading information about $f$ from $f'$.
- **Differentiability:** where the derivative fails to exist (corners, cusps, vertical tangents, discontinuities).
- Differentiable ⇒ continuous (but not the reverse).

## 2. Where it is used
- **Data analysis:** a velocity graph from a position graph (GPS speed from location data).
- **Machine learning:** ReLU has a corner at 0, so it isn't differentiable there; frameworks choose a value by convention.
- **Optimisation:** algorithms like gradient descent need differentiable functions (or special handling).
- **Engineering:** sharp corners in a path cause infinite acceleration, so roads and roller coasters are designed smooth.

## 3. What it means
**Sketching $f'$:** read the slope of $f$ at each point.
- Where $f$ is **increasing**, $f' > 0$ (above the axis).
- Where $f$ is **decreasing**, $f' < 0$.
- Where $f$ has a **flat tangent** (peak or valley), $f' = 0$ (crosses the axis).
- Where $f$ is steepest, $\lvert f'\rvert$ is largest.

**Not differentiable at $a$ if:**
- **Corner:** the slopes from the left and right differ ($\lvert x\rvert$ at 0).
- **Cusp:** slopes go to $+\infty$ on one side and $-\infty$ on the other ($x^{2/3}$ at 0).
- **Vertical tangent:** slope infinite ($\sqrt[3]{x}$ at 0).
- **Discontinuity:** a jump or hole.

If $f$ is differentiable at $a$, it is continuous there. The reverse fails: $\lvert x\rvert$ is continuous at 0 but not differentiable.

## 4. Formula & worked example
**Example 1.** $f(x) = x^2$ has slope negative for $x < 0$, zero at 0, positive for $x > 0$ → $f'(x) = 2x$ is a line through the origin.

**Example 2.** $f(x) = x^3 - 3x$ has peaks/valleys at $x = \pm 1$ → $f'(\pm 1) = 0$. Indeed $f'(x) = 3x^2 - 3$, a parabola crossing zero at $\pm 1$.

**Example 3 (corner).** $f(x) = \lvert x - 2\rvert$: slope $-1$ on the left, $+1$ on the right → not differentiable at 2.

**Example 4 (piecewise).** Is $f(x) = x^2$ ($x \le 1$), $2x - 1$ ($x > 1$) differentiable at 1?
- Continuous: $1 = 1$ ✓. Left slope $2x = 2$, right slope $2$ ✓ → yes.

**Example 5.** Where is $f(x) = x^{1/3}$ not differentiable? At 0 (vertical tangent: $f'(x) = \frac{1}{3}x^{-2/3}$ blows up).

## 5. Common mistakes
- Plotting the values of $f$ instead of its slopes.
- Thinking continuous means differentiable.
- Forgetting to check continuity before matching slopes for a piecewise function.

## 6. Practice
Q: $f$ is increasing on $(0, 3)$. Sign of $f'$ there?
A: Positive.

Q: Where is $f' = 0$ on a smooth graph?
A: At horizontal tangents (peaks, valleys, flat points).

Q: Is $\lvert x\rvert$ differentiable at 0?
A: No (corner).

Q: Does differentiability imply continuity?
A: Yes.

Q: Name four ways a function can fail to be differentiable at a point.
A: Corner, cusp, vertical tangent, discontinuity.

@@ 3 | TOPIC | Power, constant multiple and sum rules | d/dx xⁿ = nxⁿ⁻¹, differentiating polynomials and powers with negative and fractional exponents | MUST
# Power, constant multiple and sum rules

## 1. What it covers
- The **power rule** $\frac{d}{dx}x^n = nx^{n-1}$ for any real $n$.
- **Constant rule**, **constant multiple rule**, **sum/difference rule**.
- Rewriting roots and fractions as powers first.

## 2. Where it is used
- **Physics:** $s = 4.9t^2$ → $v = 9.8t$.
- **Economics:** marginal cost of a polynomial cost function.
- **Geometry:** $\frac{d}{dr}(\pi r^2) = 2\pi r$ (the circumference: adding a thin ring to a circle).
- **Machine learning:** the derivative of squared error $(y - \hat{y})^2$.

## 3. What it means
**Power rule:** bring the exponent down in front, then subtract 1 from the exponent. It works for negative and fractional exponents too, so rewrite $\frac{1}{x^3} = x^{-3}$ and $\sqrt{x} = x^{1/2}$ first.

**Constants:** the derivative of a constant is 0 (a flat line has slope 0). A constant multiplier stays: $\frac{d}{dx}(5x^3) = 5 \cdot 3x^2$.

**Sums:** differentiate term by term.

## 4. Formula & worked example
$$
\frac{d}{dx}c = 0 \qquad \frac{d}{dx}x^n = nx^{n-1} \qquad \frac{d}{dx}[cf] = cf' \qquad \frac{d}{dx}[f \pm g] = f' \pm g'
$$

**Example 1.** $\frac{d}{dx}(4x^5 - 3x^2 + 7x - 9) = 20x^4 - 6x + 7$

**Example 2.** $\frac{d}{dx}\left(\frac{2}{x^3}\right) = \frac{d}{dx}(2x^{-3}) = -6x^{-4} = -\frac{6}{x^4}$

**Example 3.** $\frac{d}{dx}(6\sqrt{x}) = 6 \cdot \frac{1}{2}x^{-1/2} = \frac{3}{\sqrt{x}}$

**Example 4.** $\frac{d}{dx}\left(\frac{x^3 + 2x}{x}\right) = \frac{d}{dx}(x^2 + 2) = 2x$ (simplify first)

**Example 5 (tangent line).** Tangent to $y = x^3 - 2x$ at $x = 1$.
- $y' = 3x^2 - 2$ → slope 1; point $(1, -1)$ → $y = x - 2$

**Example 6 (physics).** $s(t) = 4.9t^2 + 2t$ m. Velocity at $t = 3$ s? → $s' = 9.8t + 2$ → $31.4$ m/s.

## 5. Common mistakes
- Forgetting to subtract 1 from the exponent, or subtracting from a negative exponent wrongly ($x^{-3} \to -3x^{-4}$, not $x^{-2}$).
- Differentiating a constant to 1.
- Applying the power rule to $2^x$ (that's an exponential, not a power).

## 6. Practice
Q: $\frac{d}{dx}x^7$?
A: $7x^6$.

Q: $\frac{d}{dx}(3x^4 - 5x + 2)$?
A: $12x^3 - 5$.

Q: $\frac{d}{dx}\frac{1}{x}$?
A: $-\frac{1}{x^2}$.

Q: $\frac{d}{dx}x^{3/2}$?
A: $\frac{3}{2}x^{1/2}$.

Q: $\frac{d}{dx}(\pi^2)$?
A: $0$ (it's a constant).

@@ 3 | TOPIC | Derivatives of sin, cos, eˣ and ln x | The basic table and why each holds | MUST
# Derivatives of sin, cos, eˣ and ln x

## 1. What it covers
- $\frac{d}{dx}\sin x = \cos x$, $\frac{d}{dx}\cos x = -\sin x$.
- $\frac{d}{dx}e^x = e^x$ and $\frac{d}{dx}\ln x = \frac{1}{x}$.
- The other trig derivatives: $\tan x$, $\sec x$, $\csc x$, $\cot x$.
- Why each is true (sketch).

## 2. Where it is used
- **Oscillations:** springs, AC circuits, sound waves; the derivative of a sine wave is another (shifted) sine wave.
- **Growth and decay:** $e^x$ is the function whose rate of change equals itself, which is why it models natural growth.
- **Machine learning:** derivatives of sigmoid, softmax and log-loss use $e^x$ and $\ln x$.
- **Information theory:** derivatives of entropy involve $\ln$.

## 3. What it means
- **Sine:** the slope of $\sin x$ is greatest (1) at $x = 0$, zero at its peaks, and $-1$ at $\pi$, exactly the values of $\cos x$. (Proof: use the sum identity and the limits $\frac{\sin h}{h} \to 1$, $\frac{1 - \cos h}{h} \to 0$.)
- **Cosine:** its slope is $-\sin x$ (it goes down where sine is positive).
- **$e^x$:** the number $e$ is exactly the base for which the slope of $b^x$ at 0 equals 1. That makes $\frac{d}{dx}e^x = e^x$: at every point, the height equals the slope.
- **$\ln x$:** it's the inverse of $e^x$, and inverse functions have reciprocal slopes, giving $\frac{1}{x}$.

All of these need **radians** for trig.

## 4. Formula & worked example
$$
\frac{d}{dx}\sin x = \cos x \quad \frac{d}{dx}\cos x = -\sin x \quad \frac{d}{dx}\tan x = \sec^2 x \quad \frac{d}{dx}e^x = e^x \quad \frac{d}{dx}\ln x = \frac{1}{x}
$$
$$
\frac{d}{dx}\sec x = \sec x\tan x \qquad \frac{d}{dx}\csc x = -\csc x\cot x \qquad \frac{d}{dx}\cot x = -\csc^2 x
$$

**Example 1.** $\frac{d}{dx}(3\sin x - 2\cos x) = 3\cos x + 2\sin x$

**Example 2.** $\frac{d}{dx}(5e^x + 4\ln x) = 5e^x + \frac{4}{x}$

**Example 3.** Slope of $y = \sin x$ at $x = \frac{\pi}{3}$? → $\cos\frac{\pi}{3} = \frac{1}{2}$.

**Example 4.** $\frac{d}{dx}(x^2 - e^x + \tan x) = 2x - e^x + \sec^2 x$

**Example 5 (oscillation).** $x(t) = 4\cos t$ (a spring). Velocity $= -4\sin t$; acceleration $= -4\cos t = -x(t)$ (acceleration is opposite to displacement, which is what makes it oscillate).

## 5. Common mistakes
- Sign error: $\frac{d}{dx}\cos x = -\sin x$, not $\sin x$.
- Using the power rule on $e^x$ ($xe^{x-1}$ ✗).
- Forgetting radian mode when evaluating.

## 6. Practice
Q: $\frac{d}{dx}\cos x$?
A: $-\sin x$.

Q: $\frac{d}{dx}(e^x + x^e)$?
A: $e^x + ex^{e-1}$.

Q: $\frac{d}{dx}\ln x$?
A: $\frac{1}{x}$.

Q: $\frac{d}{dx}\tan x$?
A: $\sec^2 x$.

Q: What is special about $e^x$?
A: It is its own derivative.

@@ 3 | TOPIC | Product and quotient rules | (fg)′ = f′g + fg′, (f/g)′ = (f′g − fg′)/g² | MUST
# Product and quotient rules

## 1. What it covers
- The **product rule** for $f(x)g(x)$.
- The **quotient rule** for $\frac{f(x)}{g(x)}$.
- When to simplify first instead.

## 2. Where it is used
- **Economics:** revenue $R = p \cdot q$ where both price and quantity change.
- **Physics:** power $P = Fv$; momentum with changing mass.
- **Biology:** total biomass = number of organisms × average mass.
- **Machine learning:** derivatives of expressions like $x\sigma(x)$ (the SiLU/Swish activation).

## 3. What it means
The derivative of a product is **not** the product of the derivatives. Picture a rectangle with sides $f$ and $g$ that are both growing. The area $fg$ grows by two strips: $f' \cdot g$ (one side growing) plus $f \cdot g'$ (the other side growing). The tiny corner piece vanishes in the limit.

**Quotient rule** memory aid: "**low d-high minus high d-low, over low squared**", where "high" is the numerator and "low" the denominator.

Sometimes simplifying first is easier: $\frac{x^3 + x}{x} = x^2 + 1$ needs no quotient rule.

## 4. Formula & worked example
$$
(fg)' = f'g + fg' \qquad \left(\frac{f}{g}\right)' = \frac{f'g - fg'}{g^2}
$$

**Example 1.** $\frac{d}{dx}(x^2\sin x) = 2x\sin x + x^2\cos x$

**Example 2.** $\frac{d}{dx}(xe^x) = e^x + xe^x = e^x(1 + x)$

**Example 3.** $\frac{d}{dx}\frac{x^2}{x + 1} = \frac{2x(x + 1) - x^2(1)}{(x + 1)^2} = \frac{x^2 + 2x}{(x + 1)^2}$

**Example 4 (derive tan).** $\frac{d}{dx}\frac{\sin x}{\cos x} = \frac{\cos x\cos x - \sin x(-\sin x)}{\cos^2 x} = \frac{1}{\cos^2 x} = \sec^2 x$

**Example 5.** $\frac{d}{dx}\frac{\ln x}{x} = \frac{\frac{1}{x} \cdot x - \ln x}{x^2} = \frac{1 - \ln x}{x^2}$

**Example 6 (revenue).** Price $p(t) = 50 + 2t$, units sold $q(t) = 1000 - 10t$. Rate of change of revenue at $t = 5$?
- $R' = p'q + pq' = 2(950) + 60(-10) = 1900 - 600 = 1300$ per unit time

## 5. Common mistakes
- $(fg)' = f'g'$ ✗.
- Quotient rule in the wrong order ($fg' - f'g$ gives the wrong sign).
- Forgetting to square the denominator.

## 6. Practice
Q: $\frac{d}{dx}(x^3e^x)$?
A: $3x^2e^x + x^3e^x$.

Q: $\frac{d}{dx}(x\ln x)$?
A: $\ln x + 1$.

Q: $\frac{d}{dx}\frac{1}{x^2 + 1}$?
A: $\frac{-2x}{(x^2 + 1)^2}$.

Q: $\frac{d}{dx}\frac{x}{e^x}$?
A: $\frac{e^x - xe^x}{e^{2x}} = \frac{1 - x}{e^x}$.

Q: $\frac{d}{dx}(\sin x\cos x)$?
A: $\cos^2 x - \sin^2 x = \cos 2x$.

@@ 2 | MODULE | The chain rule and advanced differentiation | Chain rule, implicit differentiation, inverse functions, general exponentials and logs, higher derivatives | MUST
The chain rule is the most important rule in calculus, and the engine of backpropagation in neural networks.

@@ 3 | TOPIC | The chain rule | Derivatives of composite functions, outer × inner, Leibniz form, nested functions | MUST
# The chain rule

## 1. What it covers
- Differentiating **composite** functions $f(g(x))$.
- "Derivative of the outer (inner left alone) × derivative of the inner."
- Leibniz form $\frac{dy}{dx} = \frac{dy}{du}\cdot\frac{du}{dx}$.
- Nested compositions (chain rule applied several times).

## 2. Where it is used
- **Neural networks:** **backpropagation** is the chain rule applied layer by layer; PyTorch and TensorFlow implement it automatically (automatic differentiation).
- **Physics:** if temperature depends on height and height depends on time, $\frac{dT}{dt} = \frac{dT}{dh}\cdot\frac{dh}{dt}$.
- **Related rates** problems (next phase).
- **Every** non-trivial derivative: $\sin(3x)$, $e^{-x^2}$, $(2x + 1)^{10}$.

## 3. What it means
Rates multiply through a chain. If a gear A turns 3 times as fast as gear B, and B turns 2 times as fast as C, then A turns $3 \times 2 = 6$ times as fast as C.

For $y = f(g(x))$, let $u = g(x)$ be the **inner** function:
1. Differentiate the **outer** function, keeping the inner one unchanged: $f'(g(x))$.
2. Multiply by the derivative of the **inner** function: $g'(x)$.

For deeper nesting, keep peeling layers from the outside in, multiplying each derivative.

## 4. Formula & worked example
$$
\frac{d}{dx}f(g(x)) = f'(g(x))\cdot g'(x) \qquad \frac{dy}{dx} = \frac{dy}{du}\cdot\frac{du}{dx}
$$

**Example 1.** $\frac{d}{dx}(3x^2 + 1)^5 = 5(3x^2 + 1)^4 \cdot 6x = 30x(3x^2 + 1)^4$

**Example 2.** $\frac{d}{dx}\sin(4x) = 4\cos(4x)$

**Example 3.** $\frac{d}{dx}e^{-x^2} = e^{-x^2}\cdot(-2x) = -2xe^{-x^2}$

**Example 4.** $\frac{d}{dx}\ln(x^2 + 1) = \frac{2x}{x^2 + 1}$

**Example 5.** $\frac{d}{dx}\sqrt{1 - x^2} = \frac{1}{2}(1 - x^2)^{-1/2}(-2x) = \frac{-x}{\sqrt{1 - x^2}}$

**Example 6 (nested).** $\frac{d}{dx}\cos^3(2x) = 3\cos^2(2x)\cdot(-\sin 2x)\cdot 2 = -6\cos^2(2x)\sin(2x)$

**Example 7 (chain + product).** $\frac{d}{dx}\left(x^2e^{3x}\right) = 2xe^{3x} + 3x^2e^{3x}$

**Example 8 (backprop idea, code).**
```python
import math
x = 0.5
u = 3 * x            # inner layer
y = math.tanh(u)     # outer layer
dy_du = 1 - y**2     # derivative of tanh
du_dx = 3
print(dy_du * du_dx) # dy/dx by the chain rule = 3(1 - tanh²(1.5)) ≈ 0.5421
```
Each layer contributes its local derivative, and the chain rule multiplies them. A neural network does exactly this, from the output layer back to the input, for every weight.

## 5. Common mistakes
- Forgetting to multiply by the inner derivative: $\frac{d}{dx}\sin 4x \ne \cos 4x$.
- Changing the inner function when differentiating the outer: $\frac{d}{dx}(3x^2 + 1)^5 \ne 5(6x)^4$.
- Missing a layer in nested functions.

## 6. Practice
Q: $\frac{d}{dx}(5x - 2)^3$?
A: $15(5x - 2)^2$.

Q: $\frac{d}{dx}e^{4x}$?
A: $4e^{4x}$.

Q: $\frac{d}{dx}\cos(x^2)$?
A: $-2x\sin(x^2)$.

Q: $\frac{d}{dx}\ln(\sin x)$?
A: $\frac{\cos x}{\sin x} = \cot x$.

Q: $\frac{d}{dx}\sqrt{x^3 + 1}$?
A: $\frac{3x^2}{2\sqrt{x^3 + 1}}$.

@@ 3 | TOPIC | Implicit differentiation | Differentiating equations in x and y, dy/dx for circles and curves, tangent lines | MUST
# Implicit differentiation

## 1. What it covers
- Finding $\frac{dy}{dx}$ when $y$ isn't isolated (e.g. $x^2 + y^2 = 25$).
- Treating $y$ as a function of $x$ and using the chain rule on every $y$ term.
- Tangent lines to implicit curves.

## 2. Where it is used
- **Geometry and graphics:** slopes of circles, ellipses and other curves defined by equations.
- **Economics:** indifference curves and isoquants ($f(x, y) = c$).
- **Physics:** thermodynamic relations like $PV = nRT$.
- **Related rates** problems.
- **Derivatives of inverse functions** (next topic).

## 3. What it means
Many curves are given by an equation linking $x$ and $y$ without $y$ alone on one side. You can still find the slope: differentiate **both sides** with respect to $x$, remembering that $y$ depends on $x$. So whenever you differentiate something with $y$ in it, the chain rule attaches a factor $\frac{dy}{dx}$:
$$
\frac{d}{dx}(y^2) = 2y\frac{dy}{dx}
$$
Then solve the resulting equation for $\frac{dy}{dx}$.

## 4. Formula & worked example
**Example 1.** $x^2 + y^2 = 25$.
- $2x + 2y\frac{dy}{dx} = 0$ → $\frac{dy}{dx} = -\frac{x}{y}$
- At $(3, 4)$: slope $-\frac{3}{4}$, matching the tangent from the Geometry roadmap.

**Example 2.** $x^3 + y^3 = 6xy$.
- $3x^2 + 3y^2y' = 6y + 6xy'$ (product rule on $xy$)
- $y'(3y^2 - 6x) = 6y - 3x^2$ → $y' = \frac{2y - x^2}{y^2 - 2x}$

**Example 3.** $\sin(xy) = x$.
- $\cos(xy)\cdot(y + xy') = 1$ → $y' = \frac{1 - y\cos(xy)}{x\cos(xy)}$

**Example 4 (tangent).** Tangent to $x^2 + xy + y^2 = 7$ at $(1, 2)$.
- $2x + y + xy' + 2yy' = 0$ → at $(1, 2)$: $2 + 2 + y' + 4y' = 0$ → $y' = -\frac{4}{5}$
- $y - 2 = -\frac{4}{5}(x - 1)$

**Example 5.** $e^y = x$ → $e^y y' = 1$ → $y' = \frac{1}{e^y} = \frac{1}{x}$ (this proves $\frac{d}{dx}\ln x = \frac{1}{x}$, since $y = \ln x$).

## 5. Common mistakes
- Forgetting the $\frac{dy}{dx}$ factor on $y$ terms.
- Forgetting the product rule on terms like $xy$.
- Differentiating the constant on the right as anything other than 0.

## 6. Practice
Q: $\frac{dy}{dx}$ for $x^2 + y^2 = 16$?
A: $-\frac{x}{y}$.

Q: $\frac{d}{dx}(y^3)$?
A: $3y^2\frac{dy}{dx}$.

Q: $\frac{dy}{dx}$ for $xy = 10$?
A: $y + xy' = 0$ → $y' = -\frac{y}{x}$.

Q: $\frac{dy}{dx}$ for $y^2 = 4x$?
A: $\frac{2}{y}$.

Q: Slope of $x^2 + y^2 = 25$ at $(-3, 4)$?
A: $\frac{3}{4}$.

@@ 3 | TOPIC | Derivatives of inverse functions and inverse trig | (f⁻¹)′ = 1/f′(f⁻¹(x)), derivatives of arcsin, arccos, arctan | MUST
# Derivatives of inverse functions and inverse trig

## 1. What it covers
- The inverse function derivative rule.
- Derivatives of $\arcsin x$, $\arccos x$, $\arctan x$.
- Combining with the chain rule.

## 2. Where it is used
- **Integration:** these derivatives explain integrals like $\int\frac{dx}{1 + x^2} = \arctan x$, which appear in physics and probability (the Cauchy distribution).
- **Robotics and graphics:** angles computed with `atan2` and `acos`, and their sensitivities.
- **Machine learning:** derivatives of activation-function inverses.

## 3. What it means
The graph of $f^{-1}$ is the graph of $f$ reflected in $y = x$. Reflection swaps rise and run, so slopes become **reciprocals**: if $f$ has slope 3 at a point, $f^{-1}$ has slope $\frac{1}{3}$ at the reflected point.

For inverse trig, differentiate implicitly. For $y = \arcsin x$: $\sin y = x$ → $\cos y \cdot y' = 1$ → $y' = \frac{1}{\cos y} = \frac{1}{\sqrt{1 - x^2}}$ (using $\cos y = \sqrt{1 - \sin^2 y}$, positive on arcsin's range).

## 4. Formula & worked example
$$
(f^{-1})'(x) = \frac{1}{f'(f^{-1}(x))} \qquad \frac{d}{dx}\arcsin x = \frac{1}{\sqrt{1 - x^2}} \qquad \frac{d}{dx}\arccos x = -\frac{1}{\sqrt{1 - x^2}} \qquad \frac{d}{dx}\arctan x = \frac{1}{1 + x^2}
$$

**Example 1.** $f(x) = x^3 + x$; find $(f^{-1})'(2)$.
- $f(1) = 2$, so $f^{-1}(2) = 1$. $f'(x) = 3x^2 + 1$, $f'(1) = 4$ → $(f^{-1})'(2) = \frac{1}{4}$

**Example 2.** $\frac{d}{dx}\arctan(3x) = \frac{3}{1 + 9x^2}$

**Example 3.** $\frac{d}{dx}\arcsin(x^2) = \frac{2x}{\sqrt{1 - x^4}}$

**Example 4.** $\frac{d}{dx}\left(x\arctan x\right) = \arctan x + \frac{x}{1 + x^2}$

**Example 5.** Slope of $y = \arctan x$ at $x = 1$? → $\frac{1}{2}$.

## 5. Common mistakes
- Evaluating $f'$ at $x$ instead of at $f^{-1}(x)$.
- Forgetting the minus sign for $\arccos$.
- Forgetting the chain rule factor for $\arctan(3x)$.

## 6. Practice
Q: $\frac{d}{dx}\arctan x$?
A: $\frac{1}{1 + x^2}$.

Q: $\frac{d}{dx}\arcsin x$?
A: $\frac{1}{\sqrt{1 - x^2}}$.

Q: $\frac{d}{dx}\arctan(x^2)$?
A: $\frac{2x}{1 + x^4}$.

Q: $f(2) = 5$ and $f'(2) = 4$. Find $(f^{-1})'(5)$.
A: $\frac{1}{4}$.

Q: Why are inverse slopes reciprocals?
A: Reflecting in $y = x$ swaps rise and run.

@@ 3 | TOPIC | General exponentials, logarithms and logarithmic differentiation | d/dx aˣ = aˣ ln a, d/dx log_a x, differentiating products, quotients and variable exponents with logs | MUST
# General exponentials, logarithms and logarithmic differentiation

## 1. What it covers
- $\frac{d}{dx}a^x = a^x\ln a$ and $\frac{d}{dx}\log_a x = \frac{1}{x\ln a}$.
- $\frac{d}{dx}\ln\lvert x\rvert = \frac{1}{x}$.
- **Logarithmic differentiation** for messy products/quotients and **variable exponents** like $x^x$.

## 2. Where it is used
- **Computing:** growth of $2^n$ (algorithm complexity, doubling processes).
- **Finance:** sensitivity of $P(1 + r)^t$ to time.
- **Statistics and ML:** maximum likelihood estimation takes the **log** of a product of probabilities before differentiating (log-likelihood): exactly logarithmic differentiation.
- **Information theory:** derivatives of $\log_2$.

## 3. What it means
Write $a^x = e^{x\ln a}$; the chain rule gives $e^{x\ln a}\cdot\ln a = a^x\ln a$. So for base $e$ the extra factor is $\ln e = 1$, which is why $e$ is the "natural" base.

**Logarithmic differentiation:** for $y$ = a complicated product, quotient or power:
1. Take $\ln$ of both sides.
2. Use log rules to turn products into sums and powers into multiples.
3. Differentiate implicitly: $\frac{y'}{y} = \ldots$
4. Multiply by $y$.

It's the only way (at this level) to differentiate $x^x$, where both base and exponent vary.

## 4. Formula & worked example
$$
\frac{d}{dx}a^x = a^x\ln a \qquad \frac{d}{dx}\log_a x = \frac{1}{x\ln a} \qquad \frac{d}{dx}\ln\lvert x\rvert = \frac{1}{x}
$$

**Example 1.** $\frac{d}{dx}2^x = 2^x\ln 2$

**Example 2.** $\frac{d}{dx}10^{3x} = 3\ln 10 \cdot 10^{3x}$

**Example 3.** $\frac{d}{dx}\log_2 x = \frac{1}{x\ln 2}$

**Example 4 ($x^x$).** $y = x^x$ → $\ln y = x\ln x$ → $\frac{y'}{y} = \ln x + 1$ → $y' = x^x(\ln x + 1)$

**Example 5 (messy quotient).** $y = \frac{x^2\sqrt{x + 1}}{(x - 2)^3}$
- $\ln y = 2\ln x + \frac{1}{2}\ln(x + 1) - 3\ln(x - 2)$
- $y' = y\left(\frac{2}{x} + \frac{1}{2(x + 1)} - \frac{3}{x - 2}\right)$

**Example 6.** $\frac{d}{dx}(\sin x)^x$: $\ln y = x\ln\sin x$ → $y' = (\sin x)^x\left(\ln\sin x + x\cot x\right)$

## 5. Common mistakes
- Treating $x^x$ with the power rule ($x \cdot x^{x-1}$ ✗) or the exponential rule ($x^x\ln x$ ✗). It needs both terms.
- Forgetting $\ln a$ in $\frac{d}{dx}a^x$.
- Forgetting to multiply by $y$ at the end of logarithmic differentiation.

## 6. Practice
Q: $\frac{d}{dx}5^x$?
A: $5^x\ln 5$.

Q: $\frac{d}{dx}\log_{10} x$?
A: $\frac{1}{x\ln 10}$.

Q: $\frac{d}{dx}x^{\sin x}$?
A: $x^{\sin x}\left(\cos x\ln x + \frac{\sin x}{x}\right)$.

Q: $\frac{d}{dx}\ln(3x)$?
A: $\frac{1}{x}$.

Q: Why is $e$ called the natural base?
A: $\frac{d}{dx}e^x = e^x$ with no extra factor ($\ln e = 1$).

@@ 3 | TOPIC | Higher-order derivatives and choosing a strategy | Second and third derivatives, notation, meaning; picking the right rule | MUST
# Higher-order derivatives and choosing a strategy

## 1. What it covers
- Second, third and $n$th derivatives: $f''$, $f'''$, $f^{(n)}$, $\frac{d^2y}{dx^2}$.
- Meaning: acceleration, jerk, concavity.
- Patterns ($\sin x$ derivatives cycle; $e^x$ stays the same).
- A checklist for choosing which rule(s) to use.

## 2. Where it is used
- **Physics:** acceleration is the second derivative of position; **jerk** (third derivative) is minimised in elevator and vehicle design for comfort.
- **Curve analysis:** concavity and inflection points (next phase).
- **Taylor series:** built from all the higher derivatives (Phase 6).
- **Optimisation:** Newton's method and the second derivative test use $f''$.

## 3. What it means
Differentiate again: $f''$ is the rate of change of the rate of change. If position is $s(t)$, then $s'$ is velocity and $s''$ is acceleration: pressing the accelerator changes how fast your speed changes.

**Strategy checklist:**
1. **Simplify first** (expand, split fractions, use log rules).
2. Sum/difference → term by term.
3. Product → product rule; quotient → quotient rule (or rewrite as a product with a negative power).
4. Something inside something → chain rule.
5. Variable in both base and exponent, or a huge product → logarithmic differentiation.
6. $y$ not isolated → implicit differentiation.

## 4. Formula & worked example
$$
f''(x) = \frac{d}{dx}f'(x) = \frac{d^2y}{dx^2}
$$

**Example 1.** $f(x) = x^4 - 3x^2$: $f' = 4x^3 - 6x$, $f'' = 12x^2 - 6$, $f''' = 24x$, $f^{(4)} = 24$, $f^{(5)} = 0$.

**Example 2 (cycle).** $\frac{d}{dx}\sin x = \cos x$, then $-\sin x$, $-\cos x$, $\sin x$ (repeats every 4). So $\frac{d^{10}}{dx^{10}}\sin x = -\sin x$.

**Example 3.** $\frac{d^n}{dx^n}e^{2x} = 2^ne^{2x}$.

**Example 4 (motion).** $s(t) = t^3 - 6t^2 + 9t$: $v = 3t^2 - 12t + 9$, $a = 6t - 12$. At $t = 1$: $v = 0$ (momentarily stopped), $a = -6$ (slowing/turning back).

**Example 5 (strategy).** $\frac{d}{dx}\frac{e^{2x}\sin x}{x}$: simplest as a product $e^{2x}\sin x \cdot x^{-1}$ or with log differentiation.

## 5. Common mistakes
- Writing $f''$ as $(f')^2$.
- Not simplifying $f'$ before differentiating again.
- Starting with the quotient rule when simplifying would avoid it.

## 6. Practice
Q: $f(x) = 3x^5$. Find $f''(x)$.
A: $60x^3$.

Q: $\frac{d^2}{dx^2}\ln x$?
A: $-\frac{1}{x^2}$.

Q: $\frac{d^4}{dx^4}\cos x$?
A: $\cos x$.

Q: Position $s = 5t^2 - 2t$. Acceleration?
A: $10$.

Q: Best method for $y = (x^2 + 1)^{\cos x}$?
A: Logarithmic differentiation.

@@ 3 | TOPIC | Practice & review: Derivatives | Mixed problem set with worked answers | MUST
# Practice & review: Derivatives

**How to use this:** 60 minutes, no calculator. Simplify each answer.

## Problems
1. Use the definition to differentiate $f(x) = 2x^2 - x$.
2. $\frac{d}{dx}\left(4x^3 - \frac{2}{x^2} + 3\sqrt{x}\right)$
3. $\frac{d}{dx}(x^2\cos x)$
4. $\frac{d}{dx}\frac{e^x}{x^2 + 1}$
5. $\frac{d}{dx}\sin^2(3x)$
6. $\frac{d}{dx}\ln\left(\frac{x^2}{\sqrt{x + 4}}\right)$
7. $\frac{dy}{dx}$ for $x^2y + y^3 = 10$
8. $\frac{d}{dx}\arctan(e^x)$
9. $\frac{d}{dx}x^{\ln x}$
10. Tangent line to $y = xe^{x}$ at $x = 0$.
11. $s(t) = 2t^3 - 9t^2 + 12t$. When is the velocity zero? Acceleration then?

## Answers
1. $\frac{2(x + h)^2 - (x + h) - 2x^2 + x}{h} = 4x + 2h - 1 \to 4x - 1$.
2. $12x^2 + \frac{4}{x^3} + \frac{3}{2\sqrt{x}}$.
3. $2x\cos x - x^2\sin x$.
4. $\frac{e^x(x^2 + 1) - 2xe^x}{(x^2 + 1)^2} = \frac{e^x(x - 1)^2}{(x^2 + 1)^2}$.
5. $2\sin(3x)\cos(3x)\cdot 3 = 3\sin(6x)$.
6. $2\ln x - \frac{1}{2}\ln(x + 4)$ → $\frac{2}{x} - \frac{1}{2(x + 4)}$.
7. $2xy + x^2y' + 3y^2y' = 0$ → $y' = \frac{-2xy}{x^2 + 3y^2}$.
8. $\frac{e^x}{1 + e^{2x}}$.
9. $\ln y = (\ln x)^2$ → $y' = x^{\ln x}\cdot\frac{2\ln x}{x}$.
10. $y' = e^x(1 + x)$ → slope 1 at $(0, 0)$ → $y = x$.
11. $v = 6t^2 - 18t + 12 = 6(t - 1)(t - 2) = 0$ → $t = 1, 2$; $a = 12t - 18$: $a(1) = -6$, $a(2) = 6$.

## Flashcards
Q: Product rule?
A: $(fg)' = f'g + fg'$.

Q: Quotient rule?
A: $\left(\frac{f}{g}\right)' = \frac{f'g - fg'}{g^2}$.

Q: Chain rule?
A: $\frac{d}{dx}f(g(x)) = f'(g(x))g'(x)$.

Q: $\frac{d}{dx}a^x$?
A: $a^x\ln a$.

Q: $\frac{d}{dx}\arctan x$?
A: $\frac{1}{1 + x^2}$.
