@@ 1 | MODULE | Phase 4 — Identities and Equations (Advanced) | Fundamental, Pythagorean, sum/difference, double/half-angle identities; proofs; solving trig equations | MUST
# Phase 4: Identities and equations

An **identity** is an equation that is true for **every** value of the variable, like $\sin^2 x + \cos^2 x = 1$. An **equation** is true only for **some** values, like $\sin x = \frac{1}{2}$, and solving it means finding them.

Identities are the "algebra rules" of trigonometry. You use them to **simplify** expressions, **prove** that two expressions are equal, and **rewrite** equations into a form you can solve. Calculus uses them heavily, especially for integrals.

**What you need first:** Phases 1–2 (unit circle, exact values, ASTC) and algebra: factoring, quadratics, fractions.

@@ 2 | MODULE | Trigonometric identities | The core identity families and how to prove identities | MUST
Learn each family by **deriving** it once. Then you only need to remember the start, not a long list.

@@ 3 | TOPIC | Reciprocal, quotient, even-odd and cofunction identities | The fundamental identities and simplifying expressions to sin and cos | MUST
# Reciprocal, quotient, even-odd and cofunction identities

## 1. What it covers
- **Reciprocal identities:** $\csc x = \frac{1}{\sin x}$, $\sec x = \frac{1}{\cos x}$, $\cot x = \frac{1}{\tan x}$.
- **Quotient identities:** $\tan x = \frac{\sin x}{\cos x}$, $\cot x = \frac{\cos x}{\sin x}$.
- **Even-odd identities:** what happens with $-x$.
- **Cofunction identities** in radians.
- The main simplification strategy: **rewrite everything in sin and cos**.

## 2. Where it is used
- **Calculus:** before differentiating or integrating, expressions are simplified with these identities.
- **Physics and engineering formulas:** expressions such as $\frac{\sin\theta}{\cos\theta}$ simplify to $\tan\theta$.
- **Computer algebra systems:** tools like SymPy and Wolfram apply exactly these rewrite rules.
- **Signal processing:** even and odd functions (cosine and sine) are the basis of the Fourier cosine and sine series.

## 3. What it means
These identities come directly from definitions, not from any clever proof:
- Reciprocal and quotient: from the definitions in Phase 1.
- Even-odd: reflect the angle across the x-axis on the unit circle. The point $(\cos x, \sin x)$ becomes $(\cos x, -\sin x)$. So cosine stays the same (**even**) and sine changes sign (**odd**).
- Cofunction: complementary angles from Phase 1.

**Strategy:** when an expression mixes many functions, convert all of them to **sin and cos**. Then use fraction algebra. This works almost every time.

**Analogy:** converting every currency to taka before adding up a bill. Once everything is in one "currency" (sin and cos), it's easy to combine.

## 4. Formula & worked example
$$
\tan x = \frac{\sin x}{\cos x} \quad \cot x = \frac{\cos x}{\sin x} \quad \csc x = \frac{1}{\sin x} \quad \sec x = \frac{1}{\cos x}
$$
$$
\sin(-x) = -\sin x \quad \cos(-x) = \cos x \quad \tan(-x) = -\tan x
$$
$$
\sin\!\left(\tfrac{\pi}{2} - x\right) = \cos x \qquad \cos\!\left(\tfrac{\pi}{2} - x\right) = \sin x \qquad \tan\!\left(\tfrac{\pi}{2} - x\right) = \cot x
$$

**Example 1.** Simplify $\tan x \cos x$.
- $= \frac{\sin x}{\cos x} \cdot \cos x = \sin x$

**Example 2.** Simplify $\frac{\sec x}{\csc x}$.
- $= \frac{1/\cos x}{1/\sin x} = \frac{1}{\cos x} \cdot \frac{\sin x}{1} = \frac{\sin x}{\cos x} = \tan x$

**Example 3.** Simplify $\sin(-x)\csc x + \cos(-x)\sec x$.
- $= -\sin x \cdot \frac{1}{\sin x} + \cos x \cdot \frac{1}{\cos x} = -1 + 1 = 0$

**Example 4.** Simplify $\cot x \sin x + \tan x \cos x$.
- $= \frac{\cos x}{\sin x}\sin x + \frac{\sin x}{\cos x}\cos x = \cos x + \sin x$

**Example 5.** Is $f(x) = x\sin x$ even or odd?
- $f(-x) = (-x)\sin(-x) = (-x)(-\sin x) = x\sin x = f(x)$ → even.

## 5. Common mistakes
- Cancelling across addition: $\frac{\sin x + 1}{\sin x} \ne 1 + 1$. You can only cancel common **factors**.
- Writing $\sin(-x) = \sin x$. Sine is **odd**: $\sin(-x) = -\sin x$.
- Forgetting that these identities fail where a denominator is zero (e.g. $\tan x$ is undefined when $\cos x = 0$).

## 6. Practice
Q: Simplify $\sin x \cot x$.
A: $\cos x$.

Q: Simplify $\frac{\csc x}{\cot x}$.
A: $\frac{1/\sin x}{\cos x/\sin x} = \frac{1}{\cos x} = \sec x$.

Q: $\cos(-x)$ equals?
A: $\cos x$ (cosine is even).

Q: $\tan(-\pi/4)$?
A: $-\tan(\pi/4) = -1$.

Q: $\cos\left(\frac{\pi}{2} - x\right)$ equals?
A: $\sin x$.

@@ 3 | TOPIC | Pythagorean identities | sin² + cos² = 1 and its tan/sec and cot/csc variants, simplifying and finding values | MUST
# Pythagorean identities

## 1. What it covers
- The main identity $\sin^2 x + \cos^2 x = 1$.
- Its two variants: $1 + \tan^2 x = \sec^2 x$ and $1 + \cot^2 x = \csc^2 x$.
- Rearranged forms ($1 - \sin^2 x = \cos^2 x$, etc.) used for simplifying and factoring.
- Finding all trig values from one.

## 2. Where it is used
- **Calculus:** the integral $\int \sqrt{1 - x^2}\,dx$ is solved by substituting $x = \sin\theta$, which works because $1 - \sin^2\theta = \cos^2\theta$ ("trig substitution").
- **Derivatives:** $\frac{d}{dx}\tan x = \sec^2 x = 1 + \tan^2 x$.
- **Physics:** energy in simple harmonic motion: kinetic energy $\propto \cos^2$, potential $\propto \sin^2$, and the total is constant because they add up to 1.
- **Graphics:** checking that a rotation is valid (no stretching): its sin and cos must satisfy $s^2 + c^2 = 1$.

## 3. What it means
The point $(\cos x, \sin x)$ is on the unit circle, whose equation is $x^2 + y^2 = 1$. So $\cos^2 x + \sin^2 x = 1$. It's just **Pythagoras** for the triangle inside the unit circle with hypotenuse 1.

The other two come from dividing by $\cos^2 x$ or by $\sin^2 x$:
$$
\frac{\sin^2 x}{\cos^2 x} + \frac{\cos^2 x}{\cos^2 x} = \frac{1}{\cos^2 x} \;\Rightarrow\; \tan^2 x + 1 = \sec^2 x
$$
$$
\frac{\sin^2 x}{\sin^2 x} + \frac{\cos^2 x}{\sin^2 x} = \frac{1}{\sin^2 x} \;\Rightarrow\; 1 + \cot^2 x = \csc^2 x
$$
So you only need to remember **one** identity.

**Analogy:** a pie split between two people. However you split it ($\sin^2$ for one, $\cos^2$ for the other), the two shares always make one whole pie.

## 4. Formula & worked example
$$
\sin^2 x + \cos^2 x = 1 \qquad 1 + \tan^2 x = \sec^2 x \qquad 1 + \cot^2 x = \csc^2 x
$$
Useful rearrangements: $1 - \cos^2 x = \sin^2 x$, $1 - \sin^2 x = \cos^2 x$, $\sec^2 x - 1 = \tan^2 x$, $\csc^2 x - 1 = \cot^2 x$.

**Example 1.** Simplify $\frac{1 - \cos^2 x}{\sin x}$.
- $= \frac{\sin^2 x}{\sin x} = \sin x$

**Example 2.** Simplify $(1 - \sin x)(1 + \sin x)$.
- Difference of squares: $= 1 - \sin^2 x = \cos^2 x$

**Example 3.** Simplify $\frac{\tan^2 x}{\sec x - 1}$.
- $\tan^2 x = \sec^2 x - 1 = (\sec x - 1)(\sec x + 1)$
- $\frac{(\sec x - 1)(\sec x + 1)}{\sec x - 1} = \sec x + 1$

**Example 4.** $\sin x = -\frac{2}{3}$ with $x$ in quadrant IV. Find $\cos x$ and $\tan x$.
- $\cos^2 x = 1 - \frac{4}{9} = \frac{5}{9}$ → $\cos x = \pm\frac{\sqrt{5}}{3}$. In QIV cosine is positive: $\frac{\sqrt{5}}{3}$.
- $\tan x = \frac{-2/3}{\sqrt{5}/3} = -\frac{2}{\sqrt{5}} = -\frac{2\sqrt{5}}{5}$

**Example 5.** $\tan x = 3$ with $x$ in quadrant III. Find $\sec x$.
- $\sec^2 x = 1 + 9 = 10$ → $\sec x = \pm\sqrt{10}$. In QIII cosine is negative, so $\sec x = -\sqrt{10}$.

## 5. Common mistakes
- Writing $\sin^2 x + \cos^2 x = 1$ as $\sin x + \cos x = 1$. The squares matter: at $x = 45^\circ$, $\sin x + \cos x = \sqrt{2}$.
- Taking the square root and forgetting the $\pm$; then not using the quadrant to choose the sign.
- Confusing $\sin x^2$ (sine of $x^2$) with $\sin^2 x$ (sine of $x$, squared).

## 6. Practice
Q: Simplify $\sec^2 x - \tan^2 x$.
A: $1$.

Q: Simplify $\cos^2 x \tan^2 x$.
A: $\cos^2 x \cdot \frac{\sin^2 x}{\cos^2 x} = \sin^2 x$.

Q: $\cos x = \frac{1}{4}$, $x$ in QIV. Find $\sin x$.
A: $-\sqrt{1 - \frac{1}{16}} = -\frac{\sqrt{15}}{4}$.

Q: Simplify $\frac{1 - \sin^2 x}{\cos x}$.
A: $\cos x$.

Q: Derive $1 + \cot^2 x = \csc^2 x$.
A: Divide every term of $\sin^2 x + \cos^2 x = 1$ by $\sin^2 x$.

@@ 3 | TOPIC | Sum and difference identities | sin(a ± b), cos(a ± b), tan(a ± b), exact values like sin 15° and 75° | MUST
# Sum and difference identities

## 1. What it covers
- The formulas for $\sin(a \pm b)$, $\cos(a \pm b)$ and $\tan(a \pm b)$.
- Using them to get exact values such as $\sin 15^\circ$ and $\cos 75^\circ$.
- Simplifying and proving with them; the link to **rotation**.

## 2. Where it is used
- **Computer graphics and robotics:** rotating a point by angle $b$ uses exactly these formulas. The 2D rotation matrix is the sum identity written as a matrix.
- **Signal processing and radio:** mixing two signals (AM radio, heterodyne receivers) creates sum and difference frequencies.
- **Physics:** adding two waves gives "beats", the wobble you hear when two guitar strings are slightly out of tune.
- **Calculus:** the derivative of $\sin x$ is derived using $\sin(x + h)$.

## 3. What it means
A common mistake is to think $\sin(a + b) = \sin a + \sin b$. It isn't: $\sin(30^\circ + 60^\circ) = \sin 90^\circ = 1$, but $\sin 30^\circ + \sin 60^\circ \approx 1.37$. The correct formulas mix sines and cosines.

**Where they come from: rotation.** Rotating the point $(\cos a, \sin a)$ by a further angle $b$ lands on $(\cos(a + b), \sin(a + b))$. Working out that rotation geometrically gives the formulas. In matrix form:
$$
\begin{pmatrix}\cos(a+b) \\ \sin(a+b)\end{pmatrix} = \begin{pmatrix}\cos b & -\sin b \\ \sin b & \cos b\end{pmatrix}\begin{pmatrix}\cos a \\ \sin a\end{pmatrix}
$$
Multiplying out gives exactly the two formulas below.

**Memory aids:**
- **Sine** keeps the sign and mixes: "sin cos, cos sin".
- **Cosine** keeps the pairs together ("cos cos, sin sin") and **flips** the sign.

**Analogy:** turning $30^\circ$ and then another $45^\circ$ is the same as turning $75^\circ$ in one go. These formulas compute the result of the two turns.

## 4. Formula & worked example
$$
\sin(a \pm b) = \sin a\cos b \pm \cos a\sin b
$$
$$
\cos(a \pm b) = \cos a\cos b \mp \sin a\sin b
$$
$$
\tan(a \pm b) = \frac{\tan a \pm \tan b}{1 \mp \tan a\tan b}
$$

**Example 1.** Exact value of $\sin 75^\circ$.
- $75^\circ = 45^\circ + 30^\circ$
- $\sin 75^\circ = \sin 45^\circ\cos 30^\circ + \cos 45^\circ\sin 30^\circ = \frac{\sqrt{2}}{2}\cdot\frac{\sqrt{3}}{2} + \frac{\sqrt{2}}{2}\cdot\frac{1}{2} = \frac{\sqrt{6} + \sqrt{2}}{4} \approx 0.966$

**Example 2.** Exact value of $\cos 15^\circ$.
- $15^\circ = 45^\circ - 30^\circ$
- $\cos 15^\circ = \cos 45^\circ\cos 30^\circ + \sin 45^\circ\sin 30^\circ = \frac{\sqrt{6} + \sqrt{2}}{4}$ (the same as $\sin 75^\circ$, as the cofunction rule predicts).

**Example 3.** Exact value of $\tan\frac{\pi}{12}$ ($15^\circ$).
- $\tan(45^\circ - 30^\circ) = \frac{1 - \frac{\sqrt{3}}{3}}{1 + \frac{\sqrt{3}}{3}} = \frac{3 - \sqrt{3}}{3 + \sqrt{3}}$
- Multiply top and bottom by $3 - \sqrt{3}$: $\frac{(3 - \sqrt{3})^2}{9 - 3} = \frac{12 - 6\sqrt{3}}{6} = 2 - \sqrt{3}$

**Example 4.** Simplify $\sin(x + \pi)$.
- $= \sin x\cos\pi + \cos x\sin\pi = \sin x(-1) + \cos x(0) = -\sin x$

**Example 5.** $\sin a = \frac{3}{5}$ ($a$ in QI) and $\cos b = -\frac{5}{13}$ ($b$ in QII). Find $\cos(a + b)$.
- $\cos a = \frac{4}{5}$; $\sin b = \frac{12}{13}$.
- $\cos(a + b) = \frac{4}{5}\cdot\left(-\frac{5}{13}\right) - \frac{3}{5}\cdot\frac{12}{13} = -\frac{20}{65} - \frac{36}{65} = -\frac{56}{65}$

**Example 6: rotate a point in code.** Rotate $(3, 1)$ by $90^\circ$ around the origin.
```python
import math
def rotate(x, y, deg):
    t = math.radians(deg)
    return (x * math.cos(t) - y * math.sin(t),
            x * math.sin(t) + y * math.cos(t))
print(rotate(3, 1, 90))   # (-1.0, 3.0) up to rounding
```
This is the rotation matrix above. If the point is written as $r(\cos a, \sin a)$, the new x is $r(\cos a\cos t - \sin a\sin t) = r\cos(a + t)$: the cosine sum identity.

## 5. Common mistakes
- Splitting the function: $\cos(a + b) \ne \cos a + \cos b$.
- Wrong sign in the cosine formula: for $\cos(a + b)$ the sign in the middle is **minus**.
- In $\tan(a + b)$, using $+$ in the denominator. It is $1 - \tan a\tan b$ for the **sum**.

## 6. Practice
Q: Formula for $\sin(a - b)$?
A: $\sin a\cos b - \cos a\sin b$.

Q: Formula for $\cos(a + b)$?
A: $\cos a\cos b - \sin a\sin b$.

Q: Exact value of $\sin 15^\circ$?
A: $\sin(45^\circ - 30^\circ) = \frac{\sqrt{6} - \sqrt{2}}{4}$.

Q: Simplify $\cos\left(x - \frac{\pi}{2}\right)$.
A: $\cos x \cdot 0 + \sin x \cdot 1 = \sin x$.

Q: Exact value of $\tan 75^\circ$?
A: $\frac{1 + \frac{\sqrt{3}}{3}}{1 - \frac{\sqrt{3}}{3}} = 2 + \sqrt{3}$.

@@ 3 | TOPIC | Double-angle and half-angle identities | sin 2x, cos 2x (three forms), tan 2x, power reduction, half-angle formulas | MUST
# Double-angle and half-angle identities

## 1. What it covers
- **Double-angle:** $\sin 2x$, $\cos 2x$ (three forms) and $\tan 2x$, derived from the sum identities.
- **Power reduction:** writing $\sin^2 x$ and $\cos^2 x$ without squares.
- **Half-angle:** $\sin\frac{x}{2}$, $\cos\frac{x}{2}$, $\tan\frac{x}{2}$, and choosing the sign.

## 2. Where it is used
- **Calculus:** $\int \sin^2 x\,dx$ is impossible to do directly; power reduction turns it into $\int \frac{1 - \cos 2x}{2}dx$, which is easy.
- **Physics:** the range of a projectile launched at speed $v$ and angle $\theta$ is $R = \frac{v^2\sin 2\theta}{g}$. It is largest when $2\theta = 90^\circ$, so the best launch angle is $45^\circ$.
- **Electrical engineering:** the power in an AC circuit uses $\sin^2(\omega t) = \frac{1 - \cos 2\omega t}{2}$, which is why its average is half the peak.
- **Computer graphics:** half-angle formulas appear in quaternion rotations (3D games and drones rotate using $\cos\frac{\theta}{2}$ and $\sin\frac{\theta}{2}$).

## 3. What it means
**Double-angle** formulas are just the sum formulas with $b = a$:
$$
\sin(x + x) = \sin x\cos x + \cos x\sin x = 2\sin x\cos x
$$
$$
\cos(x + x) = \cos^2 x - \sin^2 x
$$
Using $\sin^2 x + \cos^2 x = 1$, the cosine version can be written in two more ways, one with only cos and one with only sin.

**Power reduction** comes from solving those forms for $\cos^2 x$ or $\sin^2 x$.

**Half-angle** formulas are power reduction with $x$ replaced by $\frac{x}{2}$, then square-rooted. The $\pm$ sign is chosen by the quadrant of $\frac{x}{2}$.

**Analogy:** a recipe for one cake, scaled. Double-angle formulas "double the recipe"; half-angle formulas "halve it". Both are built from the same base recipe (the sum identity).

## 4. Formula & worked example
$$
\sin 2x = 2\sin x\cos x \qquad \tan 2x = \frac{2\tan x}{1 - \tan^2 x}
$$
$$
\cos 2x = \cos^2 x - \sin^2 x = 2\cos^2 x - 1 = 1 - 2\sin^2 x
$$
$$
\sin^2 x = \frac{1 - \cos 2x}{2} \qquad \cos^2 x = \frac{1 + \cos 2x}{2}
$$
$$
\sin\frac{x}{2} = \pm\sqrt{\frac{1 - \cos x}{2}} \qquad \cos\frac{x}{2} = \pm\sqrt{\frac{1 + \cos x}{2}} \qquad \tan\frac{x}{2} = \frac{1 - \cos x}{\sin x}
$$

**Example 1.** $\sin x = \frac{3}{5}$, $x$ in QII. Find $\sin 2x$ and $\cos 2x$.
- $\cos x = -\frac{4}{5}$
- $\sin 2x = 2\cdot\frac{3}{5}\cdot\left(-\frac{4}{5}\right) = -\frac{24}{25}$
- $\cos 2x = 1 - 2\sin^2 x = 1 - \frac{18}{25} = \frac{7}{25}$

**Example 2.** Exact value of $\cos 22.5^\circ$.
- $22.5^\circ = \frac{45^\circ}{2}$, in QI so positive.
- $\cos 22.5^\circ = \sqrt{\frac{1 + \cos 45^\circ}{2}} = \sqrt{\frac{1 + \frac{\sqrt{2}}{2}}{2}} = \frac{\sqrt{2 + \sqrt{2}}}{2} \approx 0.924$

**Example 3.** Simplify $\frac{\sin 2x}{1 + \cos 2x}$.
- $= \frac{2\sin x\cos x}{1 + (2\cos^2 x - 1)} = \frac{2\sin x\cos x}{2\cos^2 x} = \tan x$

**Example 4: power reduction for calculus.**
- $\int \cos^2 x\,dx = \int \frac{1 + \cos 2x}{2}dx = \frac{x}{2} + \frac{\sin 2x}{4} + C$
- (You'll learn integration in the Calculus roadmap; the identity is the key step.)

**Example 5: projectile.** A ball is kicked at 20 m/s. Range at $30^\circ$ vs $45^\circ$ ($g = 9.8$)?
- $30^\circ$: $R = \frac{400\sin 60^\circ}{9.8} \approx 35.3$ m
- $45^\circ$: $R = \frac{400\sin 90^\circ}{9.8} \approx 40.8$ m (the maximum)
- $60^\circ$: $\sin 120^\circ = \sin 60^\circ$, so the same 35.3 m as $30^\circ$. Complementary angles give equal range.

## 5. Common mistakes
- Writing $\sin 2x = 2\sin x$. Check with $x = 90^\circ$: $\sin 180^\circ = 0$, not $2$.
- Using the wrong $\cos 2x$ form. Pick the form that matches what you know (only $\sin$? use $1 - 2\sin^2 x$).
- In half-angle formulas, choosing the sign from the quadrant of $x$ instead of $\frac{x}{2}$.

## 6. Practice
Q: $\sin 2x$ in terms of $\sin x$ and $\cos x$?
A: $2\sin x\cos x$.

Q: The three forms of $\cos 2x$?
A: $\cos^2 x - \sin^2 x$, $2\cos^2 x - 1$, $1 - 2\sin^2 x$.

Q: Power-reduction formula for $\sin^2 x$?
A: $\frac{1 - \cos 2x}{2}$.

Q: Exact value of $\sin 15^\circ$ using the half-angle formula.
A: $\sqrt{\frac{1 - \cos 30^\circ}{2}} = \frac{\sqrt{2 - \sqrt{3}}}{2}$ (equal to $\frac{\sqrt{6} - \sqrt{2}}{4}$).

Q: $\cos x = \frac{1}{3}$. Find $\cos 2x$.
A: $2\cdot\frac{1}{9} - 1 = -\frac{7}{9}$.

@@ 3 | TOPIC | Product-to-sum and sum-to-product | Converting products of sines and cosines into sums and back; beats | NICE
# Product-to-sum and sum-to-product

## 1. What it covers
- **Product-to-sum:** rewriting $\sin a\cos b$, $\cos a\cos b$, $\sin a\sin b$ as sums.
- **Sum-to-product:** rewriting $\sin a + \sin b$ and $\cos a + \cos b$ as products.
- The physics of **beats**.

## 2. Where it is used
- **Radio and telecom:** AM modulation multiplies a message by a carrier wave; product-to-sum shows it creates two side frequencies. Every radio receiver relies on this ("mixing").
- **Music:** two notes close in pitch produce beats; musicians tune by listening for them to slow down.
- **Calculus:** $\int \sin 3x\cos 2x\,dx$ becomes easy after product-to-sum.
- **Fourier analysis:** proving that different sine waves are "orthogonal" uses these formulas.

## 3. What it means
Add the formulas for $\sin(a + b)$ and $\sin(a - b)$:
$$
\sin(a + b) + \sin(a - b) = 2\sin a\cos b
$$
The $\cos a\sin b$ terms cancel. Divide by 2 and you've turned a **product** into a **sum**. The other formulas come from the same trick with the cosine identities.

**Sum-to-product** is the same formula read backwards (with $a + b$ and $a - b$ renamed).

**Analogy:** two marching bands with slightly different tempos. Sometimes their beats line up (loud), sometimes they clash (quiet). The combined sound is a fast wave whose loudness rises and falls slowly: sum-to-product shows exactly that.

## 4. Formula & worked example
$$
\sin a\cos b = \tfrac{1}{2}\left[\sin(a + b) + \sin(a - b)\right]
$$
$$
\cos a\cos b = \tfrac{1}{2}\left[\cos(a - b) + \cos(a + b)\right]
$$
$$
\sin a\sin b = \tfrac{1}{2}\left[\cos(a - b) - \cos(a + b)\right]
$$
$$
\sin a + \sin b = 2\sin\frac{a + b}{2}\cos\frac{a - b}{2} \qquad \cos a + \cos b = 2\cos\frac{a + b}{2}\cos\frac{a - b}{2}
$$

**Example 1.** Write $\sin 5x\cos 3x$ as a sum.
- $= \frac{1}{2}\left[\sin 8x + \sin 2x\right]$

**Example 2.** Exact value of $\cos 75^\circ\cos 15^\circ$.
- $= \frac{1}{2}\left[\cos 60^\circ + \cos 90^\circ\right] = \frac{1}{2}\left[\frac{1}{2} + 0\right] = \frac{1}{4}$

**Example 3.** Write $\sin 7x + \sin 3x$ as a product.
- $= 2\sin 5x\cos 2x$

**Example 4: beats.** Two tuning forks at 440 Hz and 444 Hz play together: $\sin(2\pi\cdot 440t) + \sin(2\pi\cdot 444t)$.
- $= 2\sin(2\pi\cdot 442t)\cos(2\pi\cdot 2t)$
- You hear a 442 Hz tone whose loudness follows $\cos(2\pi \cdot 2t)$. The loudness peaks twice per cycle of that cosine, so **4 beats per second**: the difference of the two frequencies.

## 5. Common mistakes
- Forgetting the $\frac{1}{2}$ in product-to-sum, or the $2$ in sum-to-product.
- Getting the order wrong in $\sin a\sin b$: it is $\cos(a - b) - \cos(a + b)$, not the reverse.
- Trying to memorise all six. Derive them from the sum and difference identities when needed.

## 6. Practice
Q: Write $\cos 4x\cos x$ as a sum.
A: $\frac{1}{2}[\cos 3x + \cos 5x]$.

Q: Write $\cos 5x + \cos x$ as a product.
A: $2\cos 3x\cos 2x$.

Q: Two notes at 256 Hz and 260 Hz. How many beats per second?
A: 4.

Q: Exact value of $\sin 75^\circ\sin 15^\circ$.
A: $\frac{1}{2}[\cos 60^\circ - \cos 90^\circ] = \frac{1}{4}$.

Q: Where does product-to-sum come from?
A: Adding or subtracting two sum/difference identities so that some terms cancel.

@@ 3 | TOPIC | Proving identities | Strategy, worked proofs, and the rules for a valid proof | MUST
# Proving identities

## 1. What it covers
- What it means to **prove** an identity, and what you may and may not do.
- A step-by-step strategy with a toolbox of moves.
- Several fully worked proofs.

## 2. Where it is used
- **Exams:** "show that" and "prove" questions are standard.
- **Calculus and physics:** rewriting an expression into a friendlier form before integrating or solving is the same skill.
- **Programming:** simplifying a formula before coding it (fewer operations, better numerical accuracy). For example, $1 - \cos x$ loses precision for small $x$, but the equal form $2\sin^2\frac{x}{2}$ doesn't.
- **Logical thinking:** step-by-step justification is good practice for proofs in algorithms and computer science.

## 3. What it means
To prove $\text{LHS} = \text{RHS}$, start with **one side** and transform it, step by step, using known identities and algebra, until it becomes **exactly the other side**.

**Do not** treat it as an equation to solve: you can't add, multiply or square both sides, because that assumes what you're trying to prove.

**Analogy:** a road trip from city A to city B. You must drive from one to the other along known roads (identities). You can't start in both cities and "meet in the middle" by teleporting.

(Working on both sides separately until they reach the **same** expression is also acceptable, as long as each side is transformed on its own.)

**Strategy toolbox:**
1. Start with the **more complicated** side.
2. Convert to **sin and cos**.
3. **Combine fractions** with a common denominator.
4. **Factor** (common factors, difference of squares, quadratics in $\sin x$).
5. Use a **Pythagorean identity** to replace $1 - \sin^2 x$, etc.
6. **Multiply by a conjugate**, e.g. multiply $\frac{1}{1 - \sin x}$ by $\frac{1 + \sin x}{1 + \sin x}$.
7. Keep an eye on the target: if the RHS has only $\sec x$, aim for $\frac{1}{\cos x}$.

## 4. Formula & worked example
**Example 1.** Prove $\sec x - \cos x = \sin x\tan x$.
- LHS $= \frac{1}{\cos x} - \cos x = \frac{1 - \cos^2 x}{\cos x} = \frac{\sin^2 x}{\cos x} = \sin x \cdot \frac{\sin x}{\cos x} = \sin x\tan x$ = RHS ✓

**Example 2.** Prove $\frac{\cos x}{1 - \sin x} = \frac{1 + \sin x}{\cos x}$.
- LHS: multiply by the conjugate $\frac{1 + \sin x}{1 + \sin x}$:
- $= \frac{\cos x(1 + \sin x)}{1 - \sin^2 x} = \frac{\cos x(1 + \sin x)}{\cos^2 x} = \frac{1 + \sin x}{\cos x}$ = RHS ✓

**Example 3.** Prove $\frac{1}{1 + \cos x} + \frac{1}{1 - \cos x} = 2\csc^2 x$.
- LHS: common denominator $(1 + \cos x)(1 - \cos x) = 1 - \cos^2 x = \sin^2 x$.
- $= \frac{(1 - \cos x) + (1 + \cos x)}{\sin^2 x} = \frac{2}{\sin^2 x} = 2\csc^2 x$ = RHS ✓

**Example 4.** Prove $\sin^4 x - \cos^4 x = 1 - 2\cos^2 x$.
- LHS: difference of squares $= (\sin^2 x - \cos^2 x)(\sin^2 x + \cos^2 x) = (\sin^2 x - \cos^2 x)(1)$
- $= (1 - \cos^2 x) - \cos^2 x = 1 - 2\cos^2 x$ = RHS ✓

**Example 5.** Prove $\frac{\sin 2x}{\sin x} - \frac{\cos 2x}{\cos x} = \sec x$.
- LHS $= \frac{2\sin x\cos x}{\sin x} - \frac{2\cos^2 x - 1}{\cos x} = 2\cos x - 2\cos x + \frac{1}{\cos x} = \sec x$ = RHS ✓

**Testing before proving:** plug in a value, such as $x = 30^\circ$, into both sides. If they differ, it isn't an identity. (Matching at one value doesn't prove it, though.)

## 5. Common mistakes
- Cross-multiplying or moving terms across the "=" sign. That assumes the identity is true.
- Skipping steps and making a sign error. Write one change per line.
- Stopping at a form that is "obviously equal" but not identical to the target. Finish the job.

## 6. Practice
Q: Prove $\cos x\tan x = \sin x$.
A: $\cos x \cdot \frac{\sin x}{\cos x} = \sin x$ ✓

Q: Prove $(1 + \tan^2 x)\cos^2 x = 1$.
A: $\sec^2 x \cos^2 x = \frac{1}{\cos^2 x}\cos^2 x = 1$ ✓

Q: Prove $\frac{1 - \cos^2 x}{\sin x \cos x} = \tan x$.
A: $\frac{\sin^2 x}{\sin x\cos x} = \frac{\sin x}{\cos x} = \tan x$ ✓

Q: Why can't you multiply both sides by the same thing when proving an identity?
A: It assumes the two sides are already equal, which is what you are trying to show.

Q: Prove $(\sin x + \cos x)^2 = 1 + \sin 2x$.
A: $\sin^2 x + 2\sin x\cos x + \cos^2 x = 1 + 2\sin x\cos x = 1 + \sin 2x$ ✓

@@ 2 | MODULE | Trigonometric equations | Solving for angles: general solutions, intervals, factoring and identities | MUST
Equations ask: **which angles** make this true? Because trig functions repeat, there are usually infinitely many answers, so you either list them all with a formula (general solution) or give the ones inside a given interval.

@@ 3 | TOPIC | Solving basic trig equations | sin x = k, cos x = k, tan x = k; all solutions in an interval and the general solution | MUST
# Solving basic trig equations

## 1. What it covers
- Solving $\sin x = k$, $\cos x = k$ and $\tan x = k$.
- Finding **all solutions** in an interval, e.g. $[0, 2\pi)$.
- Writing the **general solution** with $+ 2k\pi$ or $+ k\pi$.
- Equations with a multiple angle, such as $\sin 2x = \frac{1}{2}$.

## 2. Where it is used
- **Engineering:** when does an AC voltage reach a given value? When does a vibrating part pass a position?
- **Astronomy and navigation:** when is the sun at a given elevation (sunrise and sunset times)?
- **Modelling:** "when is the tide 4 m deep?", "when is the Ferris wheel car 30 m high?"
- **Games:** when will an oscillating platform be at the player's height?

## 3. What it means
$\sin x = \frac{1}{2}$ has **many** solutions: $30^\circ$, $150^\circ$, $390^\circ$, $510^\circ$, and so on. The calculator ($\sin^{-1}$) gives only the first.

**Method:**
1. **Isolate** the trig function: get it alone, like $\sin x = k$.
2. Find the **reference angle** from $\lvert k\rvert$.
3. Use **ASTC** (the sign of $k$) to find which quadrants the solutions are in. There are usually **two** solutions per full turn for sin and cos.
4. **Add full periods** to get all solutions: $+2k\pi$ for sin and cos, $+k\pi$ for tan.
5. Keep only those inside the required interval.

**Analogy:** asking "when is the minute hand pointing at 3?" The answer isn't one time; it's every hour: 1:15, 2:15, 3:15, ... The general solution is that "every hour" rule.

## 4. Formula & worked example
General solutions ($k$ is any integer):
$$
\sin x = s:\ x = \alpha + 2k\pi \ \text{ or } \ x = \pi - \alpha + 2k\pi \qquad (\alpha = \sin^{-1}s)
$$
$$
\cos x = c:\ x = \pm\alpha + 2k\pi \qquad (\alpha = \cos^{-1}c)
$$
$$
\tan x = t:\ x = \alpha + k\pi \qquad (\alpha = \tan^{-1}t)
$$

**Example 1.** Solve $2\sin x - 1 = 0$ on $[0, 2\pi)$.
- $\sin x = \frac{1}{2}$; reference $\frac{\pi}{6}$; sine positive in QI and QII.
- $x = \frac{\pi}{6}, \frac{5\pi}{6}$

**Example 2.** Solve $\sqrt{2}\cos x + 1 = 0$ (general solution).
- $\cos x = -\frac{\sqrt{2}}{2}$; reference $\frac{\pi}{4}$; cosine negative in QII and QIII.
- $x = \frac{3\pi}{4} + 2k\pi$ or $x = \frac{5\pi}{4} + 2k\pi$

**Example 3.** Solve $\tan x = -\sqrt{3}$ on $[0, 2\pi)$.
- Reference $\frac{\pi}{3}$; tangent negative in QII and QIV.
- $x = \frac{2\pi}{3}, \frac{5\pi}{3}$ (they are $\pi$ apart, as expected for tangent)

**Example 4: multiple angle.** Solve $\sin 2x = \frac{\sqrt{3}}{2}$ on $[0, 2\pi)$.
- Let $u = 2x$. If $x$ is in $[0, 2\pi)$, then $u$ is in $[0, 4\pi)$: **two** full turns.
- $u = \frac{\pi}{3}, \frac{2\pi}{3}, \frac{\pi}{3} + 2\pi, \frac{2\pi}{3} + 2\pi = \frac{\pi}{3}, \frac{2\pi}{3}, \frac{7\pi}{3}, \frac{8\pi}{3}$
- $x = \frac{u}{2} = \frac{\pi}{6}, \frac{\pi}{3}, \frac{7\pi}{6}, \frac{4\pi}{3}$ (four solutions)

**Example 5: non-special value.** Solve $\cos x = 0.3$ on $[0^\circ, 360^\circ)$.
- $\cos^{-1}(0.3) \approx 72.5^\circ$ (QI). Cosine also positive in QIV: $360^\circ - 72.5^\circ = 287.5^\circ$.

## 5. Common mistakes
- Giving only the calculator answer and missing the second solution.
- With $\sin 2x$, solving only on $[0, 2\pi)$ for $u = 2x$ instead of $[0, 4\pi)$, which loses half the solutions.
- Using $+2k\pi$ for tangent (it repeats every $\pi$, so $+k\pi$ already covers everything).
- Trying to solve $\sin x = 1.2$. No solution: sine is always between $-1$ and $1$.

## 6. Practice
Q: Solve $\cos x = \frac{1}{2}$ on $[0, 2\pi)$.
A: $x = \frac{\pi}{3}, \frac{5\pi}{3}$.

Q: Solve $\sin x = -1$ on $[0, 2\pi)$.
A: $x = \frac{3\pi}{2}$.

Q: General solution of $\tan x = 1$?
A: $x = \frac{\pi}{4} + k\pi$.

Q: Solve $2\cos x + \sqrt{3} = 0$ on $[0^\circ, 360^\circ)$.
A: $\cos x = -\frac{\sqrt{3}}{2}$, so $x = 150^\circ, 210^\circ$.

Q: How many solutions does $\sin 3x = 0.5$ have on $[0, 2\pi)$?
A: Six (two per turn, and $3x$ makes three turns).

@@ 3 | TOPIC | Solving harder trig equations | Factoring, quadratic-type equations, using identities, and avoiding lost or extra solutions | MUST
# Solving harder trig equations

## 1. What it covers
- Equations that **factor**, such as $2\sin x\cos x = \sin x$.
- **Quadratic-type** equations, such as $2\sin^2 x - \sin x - 1 = 0$.
- Using identities to get **one function** of **one angle**.
- Avoiding the two big traps: **dividing by a variable** and **squaring both sides**.

## 2. Where it is used
- **Physics:** finding the angles at which a projectile hits a target: $R = \frac{v^2\sin 2\theta}{g}$ usually gives two angles, a low and a high one.
- **Optimisation:** setting a derivative to zero often gives a trig equation, e.g. $\cos x - \sin x = 0$.
- **Electrical engineering:** finding when two signals are equal.
- **Robotics:** inverse kinematics equations mix sin and cos of joint angles.

## 3. What it means
Harder equations become basic ones after some algebra. The **goal** is to reach one or more equations of the form $\sin x = k$ (or cos, tan), each of which you already know how to solve.

**Tactics:**
1. **Everything to one side**, then **factor**: if $AB = 0$, then $A = 0$ or $B = 0$.
2. **Quadratic in disguise:** treat $\sin x$ like $y$: $2y^2 - y - 1 = 0$.
3. **Mixed functions:** use Pythagorean identities to convert ($\cos^2 x = 1 - \sin^2 x$).
4. **Mixed angles** ($x$ and $2x$): use double-angle identities to bring everything to $x$.

**Never divide both sides by $\sin x$** (or any expression that could be zero): you'll lose the solutions where $\sin x = 0$. Factor it out instead.

**If you square both sides**, check every answer in the original equation, because squaring can create fake ("extraneous") solutions.

**Analogy:** a locked box inside another box. Each tactic opens one layer, until you reach the simple $\sin x = k$ in the middle.

## 4. Formula & worked example
**Example 1: factor, don't divide.** Solve $2\sin x\cos x = \sin x$ on $[0, 2\pi)$.
- $2\sin x\cos x - \sin x = 0$ → $\sin x(2\cos x - 1) = 0$
- $\sin x = 0$: $x = 0, \pi$
- $\cos x = \frac{1}{2}$: $x = \frac{\pi}{3}, \frac{5\pi}{3}$
- All solutions: $0, \frac{\pi}{3}, \pi, \frac{5\pi}{3}$. (Dividing by $\sin x$ would have lost $0$ and $\pi$.)

**Example 2: quadratic type.** Solve $2\sin^2 x - \sin x - 1 = 0$ on $[0, 2\pi)$.
- Let $y = \sin x$: $2y^2 - y - 1 = (2y + 1)(y - 1) = 0$
- $\sin x = -\frac{1}{2}$: $x = \frac{7\pi}{6}, \frac{11\pi}{6}$
- $\sin x = 1$: $x = \frac{\pi}{2}$

**Example 3: use a Pythagorean identity.** Solve $2\cos^2 x + 3\sin x - 3 = 0$ on $[0, 2\pi)$.
- $2(1 - \sin^2 x) + 3\sin x - 3 = 0$ → $-2\sin^2 x + 3\sin x - 1 = 0$ → $2\sin^2 x - 3\sin x + 1 = 0$
- $(2\sin x - 1)(\sin x - 1) = 0$
- $\sin x = \frac{1}{2}$: $\frac{\pi}{6}, \frac{5\pi}{6}$; $\sin x = 1$: $\frac{\pi}{2}$

**Example 4: double angle.** Solve $\cos 2x + \cos x = 0$ on $[0, 2\pi)$.
- $2\cos^2 x - 1 + \cos x = 0$ → $(2\cos x - 1)(\cos x + 1) = 0$
- $\cos x = \frac{1}{2}$: $\frac{\pi}{3}, \frac{5\pi}{3}$; $\cos x = -1$: $\pi$

**Example 5: squaring (check!).** Solve $\sin x + \cos x = 1$ on $[0, 2\pi)$.
- Square: $\sin^2 x + 2\sin x\cos x + \cos^2 x = 1$ → $1 + \sin 2x = 1$ → $\sin 2x = 0$
- $2x = 0, \pi, 2\pi, 3\pi$ → $x = 0, \frac{\pi}{2}, \pi, \frac{3\pi}{2}$
- Check in the original: $x = 0$: $0 + 1 = 1$ ✓; $\frac{\pi}{2}$: $1 + 0 = 1$ ✓; $\pi$: $0 - 1 = -1$ ✗; $\frac{3\pi}{2}$: $-1 + 0 = -1$ ✗
- Solutions: $x = 0, \frac{\pi}{2}$

**Example 6: projectile angles.** A ball kicked at 25 m/s must land 50 m away ($g = 9.8$). At what angles?
- $50 = \frac{625\sin 2\theta}{9.8}$ → $\sin 2\theta = 0.784$
- $2\theta \approx 51.6^\circ$ or $128.4^\circ$ → $\theta \approx 25.8^\circ$ or $64.2^\circ$ (a low and a high kick)

## 5. Common mistakes
- Dividing by $\sin x$ or $\cos x$ and losing solutions.
- Not checking answers after squaring.
- Solving $\sin x = 2$ from a factor and listing an answer. Reject impossible factors ($\lvert\sin x\rvert > 1$).

## 6. Practice
Q: Solve $\sin^2 x = \frac{1}{4}$ on $[0, 2\pi)$.
A: $\sin x = \pm\frac{1}{2}$, so $x = \frac{\pi}{6}, \frac{5\pi}{6}, \frac{7\pi}{6}, \frac{11\pi}{6}$.

Q: Solve $\tan x\sin x = \sin x$ on $[0, 2\pi)$.
A: $\sin x(\tan x - 1) = 0$, so $x = 0, \pi$ and $x = \frac{\pi}{4}, \frac{5\pi}{4}$.

Q: Solve $2\cos^2 x - \cos x - 1 = 0$ on $[0, 2\pi)$.
A: $(2\cos x + 1)(\cos x - 1) = 0$, so $x = 0, \frac{2\pi}{3}, \frac{4\pi}{3}$.

Q: Why should you never divide both sides by $\cos x$?
A: Any solution where $\cos x = 0$ gets lost. Factor it out instead.

Q: Solve $\sin 2x = \cos x$ on $[0, 2\pi)$.
A: $\cos x(2\sin x - 1) = 0$, so $x = \frac{\pi}{2}, \frac{3\pi}{2}, \frac{\pi}{6}, \frac{5\pi}{6}$.

@@ 3 | TOPIC | Practice & review: Identities and equations | Mixed problem set with worked answers | MUST
# Practice & review: Identities and equations

**How to use this:** 60 minutes, no calculator. For proofs, write one step per line and name the identity you used.

## Problems
1. Simplify $\frac{\sin x}{1 + \cos x} + \frac{1 + \cos x}{\sin x}$.
2. Prove $\tan x + \cot x = \sec x\csc x$.
3. Exact value of $\cos 105^\circ$.
4. $\cos x = -\frac{12}{13}$, $x$ in QIII. Find $\sin 2x$, $\cos 2x$ and $\tan 2x$.
5. Prove $\frac{1 - \cos 2x}{\sin 2x} = \tan x$.
6. Solve $4\sin^2 x - 3 = 0$ on $[0, 2\pi)$.
7. Solve $\cos 2x = \sin x$ on $[0, 2\pi)$.
8. Solve $\tan 2x = 1$ on $[0, 2\pi)$.
9. Solve $\sec^2 x - 2\tan x = 0$ on $[0, 2\pi)$.

## Answers
1. Common denominator $\sin x(1 + \cos x)$: numerator $\sin^2 x + 1 + 2\cos x + \cos^2 x = 2 + 2\cos x = 2(1 + \cos x)$. Result $\frac{2}{\sin x} = 2\csc x$.
2. LHS $= \frac{\sin x}{\cos x} + \frac{\cos x}{\sin x} = \frac{\sin^2 x + \cos^2 x}{\sin x\cos x} = \frac{1}{\sin x\cos x} = \sec x\csc x$ ✓
3. $\cos(60^\circ + 45^\circ) = \frac{1}{2}\cdot\frac{\sqrt{2}}{2} - \frac{\sqrt{3}}{2}\cdot\frac{\sqrt{2}}{2} = \frac{\sqrt{2} - \sqrt{6}}{4}$.
4. $\sin x = -\frac{5}{13}$. $\sin 2x = 2\cdot\left(-\frac{5}{13}\right)\left(-\frac{12}{13}\right) = \frac{120}{169}$; $\cos 2x = 2\cdot\frac{144}{169} - 1 = \frac{119}{169}$; $\tan 2x = \frac{120}{119}$.
5. LHS $= \frac{1 - (1 - 2\sin^2 x)}{2\sin x\cos x} = \frac{2\sin^2 x}{2\sin x\cos x} = \frac{\sin x}{\cos x} = \tan x$ ✓
6. $\sin x = \pm\frac{\sqrt{3}}{2}$: $x = \frac{\pi}{3}, \frac{2\pi}{3}, \frac{4\pi}{3}, \frac{5\pi}{3}$.
7. $1 - 2\sin^2 x = \sin x$ → $2\sin^2 x + \sin x - 1 = 0$ → $(2\sin x - 1)(\sin x + 1) = 0$ → $x = \frac{\pi}{6}, \frac{5\pi}{6}, \frac{3\pi}{2}$.
8. $2x = \frac{\pi}{4} + k\pi$ → $x = \frac{\pi}{8} + \frac{k\pi}{2}$: $\frac{\pi}{8}, \frac{5\pi}{8}, \frac{9\pi}{8}, \frac{13\pi}{8}$.
9. $1 + \tan^2 x - 2\tan x = 0$ → $(\tan x - 1)^2 = 0$ → $x = \frac{\pi}{4}, \frac{5\pi}{4}$.

## Flashcards
Q: The one Pythagorean identity you need to remember?
A: $\sin^2 x + \cos^2 x = 1$ (divide by $\cos^2 x$ or $\sin^2 x$ for the others).

Q: $\cos(a - b) = ?$
A: $\cos a\cos b + \sin a\sin b$.

Q: Golden rule when solving trig equations?
A: Factor, don't divide by a trig function; check answers if you squared.

Q: General solution of $\cos x = c$?
A: $x = \pm\cos^{-1}c + 2k\pi$.
