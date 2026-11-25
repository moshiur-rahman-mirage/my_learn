@@ 1 | MODULE | Phase 4 — Multiple Integrals (Core) | Double and triple integrals, changing order, polar, cylindrical and spherical coordinates, change of variables, applications | SHOULD
# Phase 4: Multiple integrals

Single integrals add up thin strips under a curve. **Double integrals** add up tiny columns under a surface (volume, mass of a plate, total rainfall over a region, probability for two random variables). **Triple integrals** add up tiny boxes inside a solid. Choosing good coordinates (polar, cylindrical, spherical) often turns a hard integral into an easy one.

@@ 2 | MODULE | Integrating over regions | Double integrals, iterated integrals, polar coordinates, triple integrals, curvilinear coordinates, Jacobians, applications | SHOULD
Slice, integrate the slice, then add the slices: an integral inside an integral.

@@ 3 | TOPIC | Double integrals over rectangles | Volume under a surface, Riemann sums, iterated integrals, Fubini's theorem, average value | SHOULD
# Double integrals over rectangles

## 1. What it covers
- The double integral $\iint_R f(x, y)\,dA$ as volume under a surface.
- Double Riemann sums.
- **Iterated integrals** and **Fubini's theorem** (integrate in either order).
- Average value over a region.

## 2. Where it is used
- **Environmental science:** total rainfall over a region from a rainfall-density map.
- **Physics:** mass of a thin plate with varying density; total charge.
- **Probability:** $P(a \le X \le b, c \le Y \le d) = \iint f(x, y)\,dA$ for a joint density.
- **Image processing:** total brightness of a region (integrating pixel intensity).

## 3. What it means
Split the rectangle into tiny squares of area $\Delta A$. Over each, the surface height is roughly constant, giving a thin column of volume $f(x, y)\Delta A$. Add up all the columns and let them shrink: that's $\iint_R f\,dA$.

**Iterated integral:** integrate one variable at a time, treating the other as constant. First slice the solid (e.g. for fixed $x$, integrate over $y$ to get the slice's area), then add up the slices over $x$. **Fubini:** for nice functions on a rectangle, the order doesn't matter.

## 4. Formula & worked example
$$
\iint_R f(x, y)\,dA = \int_a^b\!\!\int_c^d f(x, y)\,dy\,dx = \int_c^d\!\!\int_a^b f(x, y)\,dx\,dy \qquad f_{\text{avg}} = \frac{1}{\text{Area}(R)}\iint_R f\,dA
$$

**Example 1.** $\int_0^2\int_0^3 (x + 2y)\,dy\,dx$
- Inner: $\int_0^3(x + 2y)\,dy = [xy + y^2]_0^3 = 3x + 9$
- Outer: $\int_0^2(3x + 9)\,dx = [1.5x^2 + 9x]_0^2 = 6 + 18 = 24$

**Example 2 (other order).** $\int_0^3\int_0^2(x + 2y)\,dx\,dy = \int_0^3(2 + 4y)\,dy = 6 + 18 = 24$ ✓

**Example 3 (product).** $\int_0^1\int_0^{\pi}y\sin x\,dx\,dy = \left(\int_0^\pi\sin x\,dx\right)\left(\int_0^1 y\,dy\right) = 2 \times \frac{1}{2} = 1$

**Example 4 (average temperature).** $T(x, y) = 20 + x + y$ over $[0, 4] \times [0, 2]$: $\iint T = \int_0^4\int_0^2(20 + x + y)\,dy\,dx = \int_0^4(42 + 2x)\,dx = 184$; average $= \frac{184}{8} = 23$°C.

**Example 5 (code).**
```python
from scipy.integrate import dblquad
val, err = dblquad(lambda y, x: x + 2*y, 0, 2, 0, 3)   # x from 0 to 2, y from 0 to 3
print(val)                                               # 24.0
```
`dblquad` takes the integrand with the **inner** variable first (`y, x`), then the outer limits, then the inner limits. Mixing up this order is a common bug.

## 5. Common mistakes
- Integrating the inner integral with respect to the wrong variable.
- Using the outer variable's limits for the inner integral.
- Forgetting to divide by the area for the average value.

## 6. Practice
Q: $\int_0^1\int_0^2 xy\,dy\,dx$?
A: $\int_0^1 2x\,dx = 1$.

Q: $\int_0^2\int_0^1 (x^2 + y)\,dx\,dy$?
A: $\int_0^2\left(\frac{1}{3} + y\right)dy = \frac{2}{3} + 2 = \frac{8}{3}$.

Q: What does $\iint_R 1\,dA$ give?
A: The area of $R$.

Q: State Fubini's theorem (informally).
A: On a rectangle, a nice function can be integrated in either order with the same result.

Q: Average of $f = xy$ over $[0, 2] \times [0, 2]$?
A: $\frac{4}{4} = 1$.

@@ 3 | TOPIC | Double integrals over general regions and changing the order | Type I and Type II regions, setting limits, reversing the order of integration | SHOULD
# Double integrals over general regions and changing the order

## 1. What it covers
- Regions bounded by curves: **Type I** ($y$ between two functions of $x$) and **Type II** ($x$ between two functions of $y$).
- Setting up the limits from a sketch.
- **Reversing the order** of integration (sometimes necessary to make an integral solvable).
- Area and volume over curved regions.

## 2. Where it is used
- **Engineering:** mass, centroid and moment of inertia of irregular plates (beam cross-sections).
- **Probability:** $P(X < Y)$ for a joint density integrates over a triangular region.
- **Environmental modelling:** quantities over irregular land areas.

## 3. What it means
The inner integral's limits can be **functions** of the outer variable. For a region between $y = g_1(x)$ (bottom) and $y = g_2(x)$ (top) for $a \le x \le b$: for each fixed $x$, $y$ runs from the bottom curve to the top curve.

**Always sketch the region.** Draw a typical vertical line (for $dy\,dx$) or horizontal line (for $dx\,dy$) through it and read off where it enters and leaves.

**Reversing order:** describe the same region the other way. This can turn an impossible inner integral (like $\int e^{x^2}dx$) into an easy one.

## 4. Formula & worked example
$$
\iint_D f\,dA = \int_a^b\!\!\int_{g_1(x)}^{g_2(x)} f(x, y)\,dy\,dx = \int_c^d\!\!\int_{h_1(y)}^{h_2(y)} f(x, y)\,dx\,dy
$$

**Example 1.** Region between $y = x^2$ and $y = x$ for $0 \le x \le 1$; integrate $f = xy$.
- $\int_0^1\int_{x^2}^{x}xy\,dy\,dx = \int_0^1\frac{x(x^2 - x^4)}{2}dx = \frac{1}{2}\left(\frac{1}{4} - \frac{1}{6}\right) = \frac{1}{24}$

**Example 2 (area).** Area between $y = x^2$ and $y = 2x$: $\int_0^2\int_{x^2}^{2x}1\,dy\,dx = \int_0^2(2x - x^2)\,dx = \frac{4}{3}$.

**Example 3 (reverse the order).** $\int_0^1\int_y^1 e^{x^2}\,dx\,dy$ can't be done as written ($e^{x^2}$ has no elementary antiderivative).
- Region: $0 \le y \le x \le 1$. Reverse: $\int_0^1\int_0^x e^{x^2}\,dy\,dx = \int_0^1 xe^{x^2}\,dx = \frac{e - 1}{2}$.

**Example 4 (probability).** $X$, $Y$ independent and uniform on $[0, 1]$. $P(Y < X^2)$?
- $\int_0^1\int_0^{x^2}1\,dy\,dx = \frac{1}{3}$

## 5. Common mistakes
- Putting variables in the outer limits (outer limits must be constants).
- Not redrawing the region when reversing the order.
- Swapping the top and bottom curves.

## 6. Practice
Q: Set up the area of the region under $y = \sqrt{x}$ for $0 \le x \le 4$ as a double integral.
A: $\int_0^4\int_0^{\sqrt{x}}1\,dy\,dx$.

Q: Reverse $\int_0^2\int_0^{x}f\,dy\,dx$.
A: $\int_0^2\int_y^2 f\,dx\,dy$.

Q: $\int_0^1\int_0^x 2\,dy\,dx$?
A: $1$.

Q: Why reverse the order of integration?
A: To make the inner integral solvable or simpler.

Q: What must be true of the outer limits?
A: They must be constants.

@@ 3 | TOPIC | Double integrals in polar coordinates | dA = r dr dθ, disks, rings and sectors, the Gaussian integral | SHOULD
# Double integrals in polar coordinates

## 1. What it covers
- Converting $\iint f\,dA$ to polar: $dA = r\,dr\,d\theta$.
- Regions that are disks, rings (annuli), sectors and polar curves.
- The famous **Gaussian integral** $\int_{-\infty}^{\infty}e^{-x^2}dx = \sqrt{\pi}$.

## 2. Where it is used
- **Statistics:** proving the normal distribution's total probability is 1 (the Gaussian integral).
- **Physics and engineering:** circular plates, pipes, antennas, radially symmetric fields.
- **Image processing:** circular filters and apertures (camera lenses, telescopes).

## 3. What it means
For circular regions, rectangles fit badly. Polar coordinates fit naturally: $x = r\cos\theta$, $y = r\sin\theta$.

The tiny area element is **not** $dr\,d\theta$: a small polar "rectangle" has sides $dr$ and $r\,d\theta$ (an arc), so $dA = r\,dr\,d\theta$. The extra $r$ (the Jacobian determinant from Phase 2) accounts for the pieces getting bigger as you move outward, like slices of a pizza being wider at the crust.

## 4. Formula & worked example
$$
\iint_D f(x, y)\,dA = \int_\alpha^\beta\!\!\int_{r_1}^{r_2} f(r\cos\theta, r\sin\theta)\,r\,dr\,d\theta
$$

**Example 1 (area of a disk).** $\int_0^{2\pi}\int_0^R r\,dr\,d\theta = 2\pi \cdot \frac{R^2}{2} = \pi R^2$ ✓

**Example 2.** $\iint_D (x^2 + y^2)\,dA$ over the disk of radius 2: $\int_0^{2\pi}\int_0^2 r^2 \cdot r\,dr\,d\theta = 2\pi \cdot 4 = 8\pi$.

**Example 3 (ring).** $\iint e^{-(x^2 + y^2)}\,dA$ over $1 \le r \le 2$: $2\pi\int_1^2 re^{-r^2}dr = \pi(e^{-1} - e^{-4}) \approx 1.10$.

**Example 4 (Gaussian integral).** Let $I = \int_{-\infty}^\infty e^{-x^2}dx$. Then
$$
I^2 = \iint_{\mathbb{R}^2}e^{-(x^2 + y^2)}\,dA = \int_0^{2\pi}\!\!\int_0^\infty e^{-r^2}r\,dr\,d\theta = 2\pi \cdot \frac{1}{2} = \pi
$$
so $I = \sqrt{\pi}$. This is why the normal density has the factor $\frac{1}{\sqrt{2\pi}}$.

**Example 5 (volume of a sphere).** Twice the volume under $z = \sqrt{R^2 - r^2}$: $2\int_0^{2\pi}\int_0^R\sqrt{R^2 - r^2}\,r\,dr\,d\theta = \frac{4}{3}\pi R^3$ ✓

## 5. Common mistakes
- Forgetting the extra factor $r$.
- Wrong $\theta$ limits (a half-disk above the x-axis is $0 \le \theta \le \pi$).
- Converting $f$ incorrectly ($x^2 + y^2 = r^2$, not $r$).

## 6. Practice
Q: Polar area element?
A: $dA = r\,dr\,d\theta$.

Q: $\iint_D 1\,dA$ over the disk of radius 3 in polar?
A: $\int_0^{2\pi}\int_0^3 r\,dr\,d\theta = 9\pi$.

Q: Limits for the quarter disk of radius 1 in the first quadrant?
A: $0 \le r \le 1$, $0 \le \theta \le \frac{\pi}{2}$.

Q: Value of $\int_{-\infty}^{\infty}e^{-x^2}dx$?
A: $\sqrt{\pi}$.

Q: $\iint\sqrt{x^2 + y^2}\,dA$ over the unit disk?
A: $\int_0^{2\pi}\int_0^1 r^2\,dr\,d\theta = \frac{2\pi}{3}$.

@@ 3 | TOPIC | Triple integrals | Volume, mass and density in 3D, setting up limits over solid regions | SHOULD
# Triple integrals

## 1. What it covers
- $\iiint_E f(x, y, z)\,dV$ over a solid region.
- Iterated triple integrals and setting up limits.
- Volume ($f = 1$), mass ($f$ = density), average value.

## 2. Where it is used
- **Engineering:** mass, centre of mass and moments of inertia of 3D parts (CAD software computes these).
- **Physics:** total charge in a volume, gravitational potential of a solid.
- **Medical imaging:** total tracer amount in an organ from 3D scan data.
- **Probability:** three random variables (joint density in 3D).

## 3. What it means
Exactly like double integrals, one dimension higher: fill the solid with tiny boxes of volume $dV$, weight each by $f$ (e.g. density), and add up.

**Setting up:** usually integrate $z$ first between a bottom surface and a top surface, then handle the remaining 2D region (the "shadow" of the solid on the xy-plane) as a double integral.

## 4. Formula & worked example
$$
\iiint_E f\,dV = \iint_D\left[\int_{u_1(x, y)}^{u_2(x, y)} f(x, y, z)\,dz\right]dA
$$

**Example 1 (box).** $\int_0^1\int_0^2\int_0^3 xyz\,dz\,dy\,dx = \frac{1}{2} \cdot 2 \cdot \frac{9}{2} = 4.5$

**Example 2 (tetrahedron volume).** Region under $x + y + z = 1$ in the first octant:
- $\int_0^1\int_0^{1-x}\int_0^{1-x-y}1\,dz\,dy\,dx = \int_0^1\int_0^{1-x}(1 - x - y)\,dy\,dx = \int_0^1\frac{(1 - x)^2}{2}dx = \frac{1}{6}$

**Example 3 (mass).** A cube $[0, 1]^3$ with density $\rho = x + y + z$: mass $= \iiint(x + y + z)\,dV = \frac{3}{2}$.

**Example 4 (code: Monte Carlo volume).**
```python
import numpy as np
rng = np.random.default_rng(0)
p = rng.random((1_000_000, 3))                 # random points in the unit cube
inside = (p.sum(axis=1) <= 1).mean()           # fraction under x + y + z = 1
print(inside)                                   # ≈ 0.1667 = 1/6
```
For complicated 3D shapes, Monte Carlo integration (random points) is often easier than setting up limits, and it scales to high dimensions where grid methods fail.

## 5. Common mistakes
- Inner limits that depend on the wrong variables.
- Losing track of the order $dz\,dy\,dx$.
- Forgetting the density when computing mass.

## 6. Practice
Q: $\iiint_E 1\,dV$ gives?
A: The volume of $E$.

Q: $\int_0^2\int_0^2\int_0^2 1\,dz\,dy\,dx$?
A: $8$.

Q: Volume of the tetrahedron under $x + y + z = 2$ in the first octant?
A: $\frac{2^3}{6} = \frac{4}{3}$.

Q: How do you compute mass from density?
A: Integrate the density over the volume.

Q: Why is Monte Carlo useful for high-dimensional integrals?
A: Its error doesn't grow with the number of dimensions like grid methods' does.

@@ 3 | TOPIC | Cylindrical and spherical coordinates | Conversions, volume elements r dz dr dθ and ρ² sin φ dρ dφ dθ, integrating over cylinders, cones and spheres | SHOULD
# Cylindrical and spherical coordinates

## 1. What it covers
- **Cylindrical** $(r, \theta, z)$: polar in the plane plus height; $dV = r\,dz\,dr\,d\theta$.
- **Spherical** $(\rho, \theta, \phi)$: distance from origin, angle around, angle down from the z-axis; $dV = \rho^2\sin\phi\,d\rho\,d\phi\,d\theta$.
- Choosing the right system and setting up integrals.

## 2. Where it is used
- **Engineering:** pipes, tanks and shafts (cylindrical).
- **Physics:** gravity and electric fields of spheres, atoms (spherical).
- **Geography and GPS:** latitude and longitude are spherical coordinates.
- **3D graphics:** orbit cameras use spherical coordinates (distance, yaw, pitch).

## 3. What it means
- **Cylindrical:** use polar coordinates for $(x, y)$ and keep $z$. Natural for anything with an axis of symmetry (cylinders, cones, paraboloids).
- **Spherical:** describe a point by its distance $\rho$ from the origin, its compass angle $\theta$ (as in polar), and its angle $\phi$ from the north pole ($0 \le \phi \le \pi$). Natural for spheres and cones from the origin.

The volume elements include stretch factors (Jacobians): $r$ for cylindrical, $\rho^2\sin\phi$ for spherical. A small spherical "box" has sides $d\rho$, $\rho\,d\phi$ and $\rho\sin\phi\,d\theta$.

## 4. Formula & worked example
$$
x = r\cos\theta,\ y = r\sin\theta,\ z = z;\ dV = r\,dz\,dr\,d\theta \qquad x = \rho\sin\phi\cos\theta,\ y = \rho\sin\phi\sin\theta,\ z = \rho\cos\phi;\ dV = \rho^2\sin\phi\,d\rho\,d\phi\,d\theta
$$

**Example 1 (cylinder volume).** $\int_0^{2\pi}\int_0^R\int_0^h r\,dz\,dr\,d\theta = \pi R^2h$ ✓

**Example 2 (sphere volume).** $\int_0^{2\pi}\int_0^{\pi}\int_0^R\rho^2\sin\phi\,d\rho\,d\phi\,d\theta = \frac{R^3}{3} \cdot 2 \cdot 2\pi = \frac{4}{3}\pi R^3$ ✓

**Example 3 (paraboloid).** Volume under $z = 4 - x^2 - y^2$ above $z = 0$: $\int_0^{2\pi}\int_0^2(4 - r^2)r\,dr\,d\theta = 2\pi(8 - 4) = 8\pi$.

**Example 4 (ice-cream cone).** Region inside the sphere $\rho = 1$ and the cone $\phi = \frac{\pi}{4}$: volume $= \int_0^{2\pi}\int_0^{\pi/4}\int_0^1\rho^2\sin\phi\,d\rho\,d\phi\,d\theta = \frac{2\pi}{3}\left(1 - \frac{\sqrt{2}}{2}\right) \approx 0.613$.

**Example 5 (orbit camera, code).**
```python
import numpy as np
def camera_pos(dist, yaw_deg, pitch_deg):          # pitch measured from the horizon
    th, ph = np.radians(yaw_deg), np.radians(90 - pitch_deg)   # phi from the vertical axis
    return dist * np.array([np.sin(ph)*np.cos(th), np.sin(ph)*np.sin(th), np.cos(ph)])
print(camera_pos(10, 45, 30).round(2))   # [6.12 6.12 5.  ]
```
3D viewers let you orbit around an object by changing yaw and pitch at a fixed distance: that's moving on a sphere in spherical coordinates and converting to $(x, y, z)$.

## 5. Common mistakes
- Forgetting the Jacobian factor ($r$ or $\rho^2\sin\phi$).
- Using $0$ to $2\pi$ for $\phi$ (it runs from $0$ to $\pi$).
- Mixing up conventions (physics books swap $\theta$ and $\phi$).

## 6. Practice
Q: Cylindrical volume element?
A: $r\,dz\,dr\,d\theta$.

Q: Spherical volume element?
A: $\rho^2\sin\phi\,d\rho\,d\phi\,d\theta$.

Q: Range of $\phi$ in spherical coordinates?
A: $0$ to $\pi$.

Q: Convert $x^2 + y^2 + z^2 = 9$ to spherical.
A: $\rho = 3$.

Q: Which system suits a pipe?
A: Cylindrical.

@@ 3 | TOPIC | Change of variables and the Jacobian determinant | General substitutions in double/triple integrals, area scaling by the absolute Jacobian determinant, linear maps and ellipses | SHOULD
# Change of variables and the Jacobian determinant

## 1. What it covers
- General substitutions $x = x(u, v)$, $y = y(u, v)$ in double integrals.
- $dA = \lvert\det J\rvert\,du\,dv$ (the area scale factor).
- Linear substitutions (parallelograms, ellipses); polar and spherical as special cases.
- The probability version (densities of transformed random variables).

## 2. Where it is used
- **Probability and ML:** transforming random variables; **normalising flows** (a type of generative model) rely on the Jacobian determinant of invertible transformations.
- **Physics:** integrating in coordinates adapted to the problem.
- **Computer graphics:** texture mapping and area distortion.
- **Engineering:** integrating over skewed or curved regions by mapping them to rectangles.

## 3. What it means
A substitution maps a simple region in the $uv$-plane (often a rectangle) onto the real region in the $xy$-plane. Tiny squares in $uv$ become tiny parallelograms in $xy$, scaled in area by $\lvert\det J\rvert$ (Phase 2 / Linear Algebra: the determinant is the area scale factor). So
$$
\iint_D f(x, y)\,dx\,dy = \iint_S f(x(u, v), y(u, v))\,\lvert\det J\rvert\,du\,dv
$$
This is the multivariable version of $dx = g'(u)\,du$ in u-substitution.

## 4. Formula & worked example
$$
J = \begin{pmatrix} x_u & x_v \\ y_u & y_v\end{pmatrix} \qquad dA = \lvert\det J\rvert\,du\,dv
$$

**Example 1 (polar check).** $x = r\cos\theta$, $y = r\sin\theta$: $\det J = r$ → $dA = r\,dr\,d\theta$ ✓

**Example 2 (ellipse area).** $\frac{x^2}{a^2} + \frac{y^2}{b^2} \le 1$ with $x = au$, $y = bv$: the region becomes the unit disk; $\det J = ab$ → area $= ab \cdot \pi$.

**Example 3 (parallelogram).** Region bounded by $x + y = 0$, $x + y = 2$, $x - y = 0$, $x - y = 1$; integrate $(x + y)^2$.
- $u = x + y$, $v = x - y$ → $x = \frac{u + v}{2}$, $y = \frac{u - v}{2}$, $\det J = -\frac{1}{2}$ → $\lvert\det J\rvert = \frac{1}{2}$
- $\int_0^1\int_0^2 u^2 \cdot \frac{1}{2}\,du\,dv = \frac{1}{2} \cdot \frac{8}{3} = \frac{4}{3}$

**Example 4 (probability).** If $X$ has density $f_X$ and $Y = g(X)$ is invertible, $f_Y(y) = f_X(g^{-1}(y))\left\lvert\frac{d}{dy}g^{-1}(y)\right\rvert$. In several dimensions, the derivative becomes the Jacobian determinant.

## 5. Common mistakes
- Forgetting the absolute value of the determinant.
- Using the Jacobian of the inverse map by mistake (or inverting it when needed: $\det J_{uv \to xy} = \frac{1}{\det J_{xy \to uv}}$).
- Not transforming the region's boundaries.

## 6. Practice
Q: Area element under a change of variables?
A: $dA = \lvert\det J\rvert\,du\,dv$.

Q: Jacobian determinant of $x = 2u$, $y = 3v$?
A: $6$.

Q: Area of the ellipse $\frac{x^2}{9} + \frac{y^2}{4} \le 1$?
A: $6\pi$.

Q: Why does the Jacobian determinant appear?
A: It's the local area (or volume) scale factor of the transformation.

Q: Name an ML model that uses Jacobian determinants.
A: Normalising flows.

@@ 3 | TOPIC | Applications: mass, centre of mass and joint probability | Density, mass, centroids, moments of inertia, joint densities and expected values | SHOULD
# Applications: mass, centre of mass and joint probability

## 1. What it covers
- **Mass** from density: $m = \iint\rho\,dA$ (or $\iiint\rho\,dV$).
- **Centre of mass** $(\bar{x}, \bar{y})$ and centroids.
- **Moment of inertia** (resistance to rotation).
- **Joint probability densities**: probabilities and expected values as double integrals.

## 2. Where it is used
- **Mechanical and civil engineering:** centroids of beam cross-sections; balancing rotating parts; vehicle stability.
- **Robotics and drones:** centre of mass for balance and control.
- **Game physics:** rigid bodies need mass, centre of mass and inertia.
- **Statistics and ML:** joint distributions, covariance, expected values of functions of several variables.

## 3. What it means
- **Mass:** add up density × tiny area.
- **Centre of mass:** the balance point: the density-weighted average position. For uniform density it's the **centroid** (geometric centre).
- **Moment of inertia** $I = \iint r^2\rho\,dA$: mass far from the axis counts more (squared distance). That's why a figure skater spins faster when pulling their arms in.
- **Joint density** $f(x, y)$: probabilities are volumes under it; total volume 1; $E[g(X, Y)] = \iint g\,f\,dA$.

## 4. Formula & worked example
$$
m = \iint_D\rho\,dA \qquad \bar{x} = \frac{1}{m}\iint_D x\rho\,dA \qquad \bar{y} = \frac{1}{m}\iint_D y\rho\,dA \qquad I_z = \iint_D(x^2 + y^2)\rho\,dA
$$

**Example 1 (centroid of a triangle).** Uniform triangle with vertices $(0, 0)$, $(1, 0)$, $(0, 1)$: area $\frac{1}{2}$; $\bar{x} = 2\int_0^1\int_0^{1-x}x\,dy\,dx = 2 \cdot \frac{1}{6} = \frac{1}{3}$; similarly $\bar{y} = \frac{1}{3}$ (the average of the vertices, as in the Geometry roadmap).

**Example 2 (non-uniform plate).** Square $[0, 1]^2$ with density $\rho = x$: $m = \frac{1}{2}$; $\bar{x} = 2\int_0^1\int_0^1 x^2\,dy\,dx = \frac{2}{3}$; $\bar{y} = \frac{1}{2}$ (shifted towards the heavier side).

**Example 3 (moment of inertia).** Uniform disk of radius $R$, mass $M$ (density $\frac{M}{\pi R^2}$): $I = \frac{M}{\pi R^2}\int_0^{2\pi}\int_0^R r^3\,dr\,d\theta = \frac{1}{2}MR^2$.

**Example 4 (joint probability).** $f(x, y) = x + y$ on $[0, 1]^2$ (valid: total 1).
- $P(X + Y \le 1) = \int_0^1\int_0^{1-x}(x + y)\,dy\,dx = \frac{1}{3}$
- $E[XY] = \int_0^1\int_0^1 xy(x + y)\,dy\,dx = \frac{1}{3}$

## 5. Common mistakes
- Forgetting to divide by the mass for the centre of mass.
- Using distance instead of squared distance for the moment of inertia.
- Using a joint "density" that doesn't integrate to 1.

## 6. Practice
Q: Formula for mass of a plate with density $\rho$?
A: $\iint\rho\,dA$.

Q: Centroid of the rectangle $[0, 4] \times [0, 2]$?
A: $(2, 1)$.

Q: Why does mass far from the axis increase the moment of inertia more?
A: It's weighted by the squared distance.

Q: Is $f(x, y) = 4xy$ on $[0, 1]^2$ a valid joint density?
A: Yes, it integrates to 1.

Q: Moment of inertia of a uniform disk about its centre?
A: $\frac{1}{2}MR^2$.

@@ 3 | TOPIC | Practice & review: Multiple integrals | Mixed problem set with worked answers | SHOULD
# Practice & review: Multiple integrals

**How to use this:** 50 minutes; sketch every region; check with SciPy.

## Problems
1. $\int_1^2\int_0^1(2x + 3y^2)\,dy\,dx$.
2. Integrate $f = x$ over the region between $y = x^2$ and $y = 4$.
3. Reverse the order and evaluate $\int_0^2\int_{y/2}^{1}e^{x^2}\,dx\,dy$.
4. Use polar coordinates: $\iint\frac{1}{1 + x^2 + y^2}\,dA$ over the disk of radius 2.
5. Volume of the solid under $z = 6 - x - 2y$ in the first octant.
6. Use spherical coordinates for the volume between spheres of radius 1 and 2.
7. Centroid of the semicircular region $x^2 + y^2 \le 4$, $y \ge 0$.

## Answers
1. Inner: $2x + 1$ → $\int_1^2(2x + 1)\,dx = 3 + 1 = 4$.
2. $\int_{-2}^{2}\int_{x^2}^{4}x\,dy\,dx = \int_{-2}^{2}x(4 - x^2)\,dx = 0$ (odd function, symmetric region).
3. Region $0 \le y \le 2x$, $0 \le x \le 1$ → $\int_0^1\int_0^{2x}e^{x^2}dy\,dx = \int_0^1 2xe^{x^2}dx = e - 1$.
4. $2\pi\int_0^2\frac{r}{1 + r^2}dr = \pi\ln 5 \approx 5.06$.
5. $\int_0^6\int_0^{(6-x)/2}(6 - x - 2y)\,dy\,dx = \int_0^6\frac{(6 - x)^2}{4}dx = 18$.
6. $\frac{4}{3}\pi(8 - 1) = \frac{28\pi}{3}$.
7. By symmetry $\bar{x} = 0$; $\bar{y} = \frac{1}{2\pi}\int_0^\pi\int_0^2 r\sin\theta\cdot r\,dr\,d\theta = \frac{1}{2\pi}\cdot\frac{8}{3}\cdot 2 = \frac{8}{3\pi} \approx 0.85$.

## Flashcards
Q: Polar area element?
A: $r\,dr\,d\theta$.

Q: Spherical volume element?
A: $\rho^2\sin\phi\,d\rho\,d\phi\,d\theta$.

Q: Change of variables area factor?
A: $\lvert\det J\rvert$.

Q: Centre of mass x-coordinate?
A: $\bar{x} = \frac{1}{m}\iint x\rho\,dA$.

@@ 1 | MODULE | Phase 5 — Vector Calculus (Advanced) | Line integrals, conservative fields, Green's theorem, curl and divergence, surface integrals, Stokes' and divergence theorems | NICE
# Phase 5: Vector calculus

Vector calculus integrates **along curves** and **over surfaces** in vector fields: the work done moving through a force field, the circulation of a fluid, the flux of water or electricity through a surface. Its big theorems (Green, Stokes, divergence) all say the same thing in different dimensions: **what happens inside a region is determined by what happens on its boundary**. They are the mathematical language of electromagnetism (Maxwell's equations) and fluid dynamics.

@@ 2 | MODULE | Line and surface integrals and the big theorems | Line integrals, conservative fields, Green's theorem, curl, divergence, flux, Stokes, divergence theorem | NICE
Every theorem here generalises the fundamental theorem of calculus: integrate a derivative inside = evaluate on the boundary.

@@ 3 | TOPIC | Line integrals of scalar fields | Integrating along a curve, arc length element ds, mass of a wire | NICE
# Line integrals of scalar fields

## 1. What it covers
- $\int_C f\,ds$: integrating a function along a curve.
- Parametrising the curve and using $ds = \lVert\vec{r}'(t)\rVert\,dt$.
- Applications: mass of a wire, average value along a path.

## 2. Where it is used
- **Engineering:** mass and centre of mass of cables, wires and curved beams.
- **Geography and logistics:** total cost or exposure along a route (e.g. pollution encountered along a path).
- **Graphics:** length-parametrised curves for animation.

## 3. What it means
Walk along a curve $C$; at each point there's a value $f$ (like the density of a wire, or the height of a fence). Add up $f$ times tiny lengths $ds$. If $f = 1$, you get the curve's length. Think of the area of a curtain hanging from the curve, with height $f$ at each point.

**Method:** parametrise $C$ by $\vec{r}(t)$, $a \le t \le b$; replace $ds$ by the speed times $dt$.

## 4. Formula & worked example
$$
\int_C f\,ds = \int_a^b f(\vec{r}(t))\,\lVert\vec{r}'(t)\rVert\,dt
$$

**Example 1 (length).** Circle of radius 3: $\vec{r} = \langle 3\cos t, 3\sin t\rangle$, $\lVert\vec{r}'\rVert = 3$ → $\int_0^{2\pi}3\,dt = 6\pi$ ✓

**Example 2.** $\int_C (x + y)\,ds$ along the segment from $(0, 0)$ to $(3, 4)$: $\vec{r} = \langle 3t, 4t\rangle$, $\lVert\vec{r}'\rVert = 5$ → $\int_0^1 7t \cdot 5\,dt = 17.5$.

**Example 3 (mass of a wire).** A semicircular wire $x^2 + y^2 = 1$, $y \ge 0$, with density $\rho = y$: $\int_0^\pi\sin t \cdot 1\,dt = 2$.

## 5. Common mistakes
- Forgetting the speed factor $\lVert\vec{r}'(t)\rVert$.
- Using a parametrisation that traces the curve more than once.
- Mixing up $ds$ (length) with $dx$.

## 6. Practice
Q: What does $\int_C 1\,ds$ give?
A: The length of $C$.

Q: $ds$ in terms of a parametrisation?
A: $\lVert\vec{r}'(t)\rVert\,dt$.

Q: $\int_C x\,ds$ along the segment from $(0, 0)$ to $(1, 0)$?
A: $\frac{1}{2}$.

Q: Mass of a wire with constant density 2 along a circle of radius 1?
A: $4\pi$.

Q: Does reversing the direction of $C$ change $\int_C f\,ds$?
A: No.

@@ 3 | TOPIC | Line integrals of vector fields: work and circulation | ∫F·dr, work done by a force, dependence on path and direction | NICE
# Line integrals of vector fields: work and circulation

## 1. What it covers
- $\int_C\vec{F} \cdot d\vec{r}$: the **work** done by a force field along a path.
- Computing with a parametrisation: $\int_a^b\vec{F}(\vec{r}(t)) \cdot \vec{r}'(t)\,dt$.
- **Circulation** around a closed curve: $\oint_C\vec{F} \cdot d\vec{r}$.
- Direction matters (reversing the path flips the sign).

## 2. Where it is used
- **Physics:** work done by gravity, electric fields, friction.
- **Electrical engineering:** voltage is a line integral of the electric field; Ampère's law uses circulation of the magnetic field.
- **Fluid dynamics:** circulation measures swirling (lift on aircraft wings is linked to circulation).

## 3. What it means
As you move along a path through a force field, at each step only the part of the force **along** your direction does work: $\vec{F} \cdot d\vec{r}$. Add these up along the path.
- Walking with the wind at your back: positive work (the wind helps).
- Against the wind: negative work.
- Across the wind: zero.

**Circulation:** for a closed loop, the total tendency of the field to push you around the loop.

## 4. Formula & worked example
$$
\int_C\vec{F} \cdot d\vec{r} = \int_a^b\vec{F}(\vec{r}(t)) \cdot \vec{r}'(t)\,dt = \int_C P\,dx + Q\,dy
$$

**Example 1.** $\vec{F} = \langle y, x\rangle$ along $\vec{r} = \langle t, t^2\rangle$, $0 \le t \le 1$.
- $\vec{F}(\vec{r}) = \langle t^2, t\rangle$, $\vec{r}' = \langle 1, 2t\rangle$ → $\int_0^1(t^2 + 2t^2)\,dt = 1$

**Example 2 (circulation).** $\vec{F} = \langle -y, x\rangle$ around the unit circle (anticlockwise): $\vec{F} \cdot \vec{r}' = \sin^2 t + \cos^2 t = 1$ → circulation $2\pi$ (the field swirls the same way you go).

**Example 3 (gravity).** Lifting a 2 kg mass straight up 5 m: gravity $\langle 0, -19.6\rangle$ N does $-98$ J of work (you do $+98$ J).

**Example 4 (path dependence).** $\vec{F} = \langle -y, x\rangle$ from $(1, 0)$ to $(-1, 0)$: along the upper semicircle the work is $\pi$; along the straight segment through the origin it's $0$. Different paths, different work: this field is **not** conservative.

## 5. Common mistakes
- Forgetting to substitute the parametrisation into $\vec{F}$.
- Using $\lVert\vec{r}'\rVert$ (that's for scalar line integrals) instead of $\vec{r}'$.
- Ignoring the direction of travel.

## 6. Practice
Q: Formula for work along a path?
A: $\int_C\vec{F} \cdot d\vec{r} = \int\vec{F}(\vec{r}(t)) \cdot \vec{r}'(t)\,dt$.

Q: What happens to the work if you reverse the path?
A: It changes sign.

Q: Work done by $\vec{F} = \langle 2, 0\rangle$ along the segment from $(0, 0)$ to $(3, 4)$?
A: $\int_0^1\langle 2, 0\rangle \cdot \langle 3, 4\rangle\,dt = 6$.

Q: Work done by a force always perpendicular to the motion?
A: Zero.

Q: What is circulation?
A: The line integral of a field around a closed curve.

@@ 3 | TOPIC | Conservative fields and potential functions | Path independence, F = ∇f, the fundamental theorem for line integrals, testing for conservativeness | NICE
# Conservative fields and potential functions

## 1. What it covers
- **Conservative** fields: $\vec{F} = \nabla f$ for some **potential** $f$.
- The **fundamental theorem for line integrals**: $\int_C\nabla f \cdot d\vec{r} = f(B) - f(A)$.
- Path independence; zero circulation around closed loops.
- Testing: $P_y = Q_x$ (in 2D, simply connected regions); finding $f$.

## 2. Where it is used
- **Physics:** gravity and electrostatic forces are conservative, so they have potential energy; work depends only on start and end height or voltage (energy conservation).
- **Engineering:** voltage as a potential in circuits.
- **Optimisation and ML:** gradient fields are conservative by construction; following $-\nabla f$ decreases $f$ steadily.
- **Thermodynamics:** state functions are potentials.

## 3. What it means
For a conservative field, the work from $A$ to $B$ is the same along **every** path; only the endpoints matter, like the change in height when hiking: however winding the trail, the elevation gain is the end height minus the start height. Consequently, the work around any closed loop is 0.

**Test (2D):** $\vec{F} = \langle P, Q\rangle$ is conservative (on a region without holes) if $\frac{\partial P}{\partial y} = \frac{\partial Q}{\partial x}$.

**Finding the potential:** integrate $P$ with respect to $x$ (adding a function of $y$), then match the $y$-derivative to $Q$.

## 4. Formula & worked example
$$
\vec{F} = \nabla f \Rightarrow \int_C\vec{F} \cdot d\vec{r} = f(B) - f(A) \qquad \text{test: } P_y = Q_x
$$

**Example 1 (test).** $\vec{F} = \langle 2xy, x^2 + 3y^2\rangle$: $P_y = 2x = Q_x$ → conservative.
- $f = \int 2xy\,dx = x^2y + g(y)$; $f_y = x^2 + g'(y) = x^2 + 3y^2$ → $g = y^3$ → $f = x^2y + y^3$.

**Example 2 (use the potential).** Work from $(0, 0)$ to $(1, 2)$: $f(1, 2) - f(0, 0) = 2 + 8 = 10$, along any path.

**Example 3 (not conservative).** $\vec{F} = \langle -y, x\rangle$: $P_y = -1 \ne Q_x = 1$ → not conservative (matches the path-dependence example).

**Example 4 (gravity).** Near the Earth, $\vec{F} = \langle 0, 0, -mg\rangle = \nabla(-mgz)$: work by gravity $= mg(z_A - z_B)$, depending only on the height change.

## 5. Common mistakes
- Applying the potential shortcut to non-conservative fields.
- Forgetting the "function of $y$" constant when integrating with respect to $x$.
- Using the 2D test on regions with holes without care (e.g. around the origin for $\frac{\langle -y, x\rangle}{x^2 + y^2}$).

## 6. Practice
Q: Define a conservative field.
A: A field that is the gradient of a potential function.

Q: Work done by a conservative field around a closed loop?
A: Zero.

Q: Is $\langle y, x\rangle$ conservative? Potential?
A: Yes; $f = xy$.

Q: Is $\langle x^2, xy\rangle$ conservative?
A: $P_y = 0$, $Q_x = y$ → no.

Q: State the fundamental theorem for line integrals.
A: $\int_C\nabla f \cdot d\vec{r} = f(\text{end}) - f(\text{start})$.

@@ 3 | TOPIC | Green's theorem | Circulation around a closed curve = double integral of Q_x − P_y; area by line integrals | NICE
# Green's theorem

## 1. What it covers
- **Green's theorem**: $\oint_C P\,dx + Q\,dy = \iint_D(Q_x - P_y)\,dA$.
- Orientation (anticlockwise, region on the left).
- Using it to compute circulation or area.
- The planimeter and the shoelace formula connection.

## 2. Where it is used
- **Surveying:** the planimeter (a mechanical device that measures area by tracing a boundary) works by Green's theorem.
- **Computational geometry:** the shoelace formula for polygon area is Green's theorem.
- **Fluid dynamics:** relating circulation around a wing to the vorticity inside.
- **Physics:** 2D versions of Stokes' theorem in electromagnetism.

## 3. What it means
$Q_x - P_y$ measures the tiny **rotation** (curl) of the field at each point. Green's theorem says: add up all the tiny rotations inside a region, and you get the total circulation around its boundary. Interior swirls cancel between neighbours (like gears turning against each other), leaving only the edge.

**Area from the boundary:** choosing $P = -\frac{y}{2}$, $Q = \frac{x}{2}$ gives $Q_x - P_y = 1$, so area $= \frac{1}{2}\oint(x\,dy - y\,dx)$. Applied to a polygon's edges, this **is** the shoelace formula.

## 4. Formula & worked example
$$
\oint_C P\,dx + Q\,dy = \iint_D\left(\frac{\partial Q}{\partial x} - \frac{\partial P}{\partial y}\right)dA \qquad \text{Area} = \frac{1}{2}\oint_C(x\,dy - y\,dx)
$$

**Example 1.** $\oint_C(-y\,dx + x\,dy)$ around the unit circle: $Q_x - P_y = 2$ → $2 \times \pi = 2\pi$ (matches the direct calculation earlier).

**Example 2.** $\oint_C(x^2y\,dx + xy^2\,dy)$ around the square $[0, 1]^2$: $\iint(y^2 - x^2)\,dA = \frac{1}{3} - \frac{1}{3} = 0$.

**Example 3 (area of an ellipse).** $x = a\cos t$, $y = b\sin t$: $\frac{1}{2}\int_0^{2\pi}(ab\cos^2 t + ab\sin^2 t)\,dt = \pi ab$ ✓

**Example 4 (shoelace link).** For a polygon, the area integral over each straight edge from $(x_i, y_i)$ to $(x_{i+1}, y_{i+1})$ gives $\frac{1}{2}(x_iy_{i+1} - x_{i+1}y_i)$; summing gives the shoelace formula from the Geometry roadmap.

## 5. Common mistakes
- Wrong orientation (clockwise gives a sign flip).
- Computing $P_y - Q_x$ instead of $Q_x - P_y$.
- Applying it to curves that aren't closed.

## 6. Practice
Q: State Green's theorem.
A: $\oint_C P\,dx + Q\,dy = \iint_D(Q_x - P_y)\,dA$.

Q: Required orientation of $C$?
A: Anticlockwise (region on the left).

Q: Area formula from Green's theorem?
A: $\frac{1}{2}\oint(x\,dy - y\,dx)$.

Q: Circulation of a conservative field around any closed curve, via Green?
A: $0$, since $Q_x - P_y = 0$.

Q: Which polygon-area formula comes from Green's theorem?
A: The shoelace formula.

@@ 3 | TOPIC | Curl and divergence | ∇·F as spreading out (sources/sinks), ∇×F as rotation, physical meaning, identities | NICE
# Curl and divergence

## 1. What it covers
- **Divergence** $\nabla \cdot \vec{F}$: how much a field spreads out from a point (sources and sinks).
- **Curl** $\nabla \times \vec{F}$: how much it rotates around a point.
- Computing both; key identities ($\nabla \times \nabla f = \vec{0}$, $\nabla \cdot (\nabla \times \vec{F}) = 0$).
- The Laplacian $\nabla^2 f = \nabla \cdot \nabla f$.

## 2. Where it is used
- **Electromagnetism:** Maxwell's equations are written with divergence and curl.
- **Fluid simulation (games, CGI, weather):** incompressible fluids have zero divergence; vorticity is the curl of velocity.
- **Image processing:** the Laplacian detects edges and blobs.
- **Heat and diffusion:** the heat equation $u_t = k\nabla^2 u$.

## 3. What it means
- **Divergence:** put a tiny box at a point. If more fluid flows out than in, the point is a **source** (positive divergence); if more flows in, a **sink** (negative). Zero divergence means "incompressible": what comes in goes out.
- **Curl:** put a tiny paddle wheel at the point. If the field makes it spin, the curl is non-zero; its direction is the wheel's axis (right-hand rule), its size the spin rate.

A gradient field never has curl (no swirl in "always uphill"), and a curl field never has divergence.

## 4. Formula & worked example
$$
\nabla \cdot \vec{F} = P_x + Q_y + R_z \qquad \nabla \times \vec{F} = \langle R_y - Q_z,\ P_z - R_x,\ Q_x - P_y\rangle \qquad \nabla^2 f = f_{xx} + f_{yy} + f_{zz}
$$

**Example 1.** $\vec{F} = \langle x, y, z\rangle$: divergence 3 (spreading out from everywhere), curl $\vec{0}$.

**Example 2.** $\vec{F} = \langle -y, x, 0\rangle$: divergence 0; curl $\langle 0, 0, 2\rangle$ (rotation about the z-axis).

**Example 3.** $\vec{F} = \langle x^2y, yz, xz^2\rangle$: $\nabla \cdot \vec{F} = 2xy + z + 2xz$; $\nabla \times \vec{F} = \langle 0 - y,\ 0 - z^2,\ 0 - x^2\rangle = \langle -y, -z^2, -x^2\rangle$.

**Example 4 (identity check).** $f = x^2y$: $\nabla f = \langle 2xy, x^2, 0\rangle$; curl $= \langle 0, 0, 2x - 2x\rangle = \vec{0}$ ✓

**Example 5 (Laplacian edges, code).**
```python
import numpy as np
from scipy.ndimage import laplace
img = np.zeros((7, 7)); img[2:5, 2:5] = 1.0     # a bright square
print(laplace(img))                              # non-zero only around the square's border
```
The discrete Laplacian sums second differences in x and y; it's zero in flat regions and large at edges, which is used in edge detection and image sharpening.

## 5. Common mistakes
- Treating divergence as a vector (it's a scalar) or curl as a scalar (in 3D it's a vector).
- Sign errors in the curl components (use the determinant pattern carefully).
- Confusing the Laplacian with the gradient.

## 6. Practice
Q: Divergence of $\langle 3x, -y, 2z\rangle$?
A: $3 - 1 + 2 = 4$.

Q: What does zero divergence mean physically?
A: The flow is incompressible (no net sources or sinks).

Q: Curl of $\langle y, -x, 0\rangle$?
A: $\langle 0, 0, -2\rangle$.

Q: Curl of any gradient field?
A: $\vec{0}$.

Q: Laplacian of $f = x^2 + y^2 + z^2$?
A: $6$.

@@ 3 | TOPIC | Surface integrals and flux | Parametrised surfaces, surface area, flux ∬F·n dS through a surface | NICE
# Surface integrals and flux

## 1. What it covers
- Surface area of parametrised surfaces: $\iint\lVert\vec{r}_u \times \vec{r}_v\rVert\,du\,dv$.
- Scalar surface integrals $\iint_S f\,dS$.
- **Flux** $\iint_S\vec{F} \cdot \vec{n}\,dS$: how much of a field flows through a surface.
- Orientation (choosing the normal direction).

## 2. Where it is used
- **Physics:** electric flux (Gauss's law), magnetic flux, heat flux through walls.
- **Fluid dynamics:** flow rate of water through a pipe cross-section or a net.
- **Engineering:** total pressure force on a dam or ship hull.
- **Graphics:** surface area of meshes; light flux in rendering (radiometry).

## 3. What it means
**Surface area:** a parametrised surface $\vec{r}(u, v)$ maps tiny rectangles in $(u, v)$ onto tiny parallelograms on the surface, with sides $\vec{r}_u\,du$ and $\vec{r}_v\,dv$. Their area is $\lVert\vec{r}_u \times \vec{r}_v\rVert\,du\,dv$ (the cross product from Linear Algebra).

**Flux:** imagine a net in a river. Only the part of the current **perpendicular** to the net passes through it: $\vec{F} \cdot \vec{n}$. Flux adds this up over the whole net. Turning the net edge-on to the flow makes the flux zero.

## 4. Formula & worked example
$$
dS = \lVert\vec{r}_u \times \vec{r}_v\rVert\,du\,dv \qquad \iint_S\vec{F} \cdot d\vec{S} = \iint_D\vec{F}(\vec{r}(u, v)) \cdot (\vec{r}_u \times \vec{r}_v)\,du\,dv
$$

**Example 1 (sphere area).** $\vec{r}(\phi, \theta)$ for radius $R$: $\lVert\vec{r}_\phi \times \vec{r}_\theta\rVert = R^2\sin\phi$ → $\int_0^{2\pi}\int_0^\pi R^2\sin\phi\,d\phi\,d\theta = 4\pi R^2$ ✓

**Example 2 (flux through a square).** $\vec{F} = \langle 0, 0, 3\rangle$ through the unit square in the plane $z = 1$, normal $\langle 0, 0, 1\rangle$: flux $= 3 \times 1 = 3$.

**Example 3 (flux out of a sphere).** $\vec{F} = \langle x, y, z\rangle$ through the sphere of radius $R$ (outward normal $\frac{\vec{r}}{R}$): $\vec{F} \cdot \vec{n} = R$ → flux $= R \cdot 4\pi R^2 = 4\pi R^3$.

**Example 4 (graph surface).** For $z = g(x, y)$ with upward normal: $d\vec{S} = \langle -g_x, -g_y, 1\rangle\,dA$.

## 5. Common mistakes
- Forgetting the $\lVert\vec{r}_u \times \vec{r}_v\rVert$ factor.
- Using an inward normal when outward is required (sign flip).
- Confusing flux (field through a surface) with surface area.

## 6. Practice
Q: Surface area element for a parametrised surface?
A: $\lVert\vec{r}_u \times \vec{r}_v\rVert\,du\,dv$.

Q: What does flux measure?
A: How much of a vector field passes through a surface.

Q: Flux of a field parallel to a flat surface?
A: Zero.

Q: Flux of $\langle 0, 0, 5\rangle$ through a disk of radius 2 in the xy-plane (upward)?
A: $5 \times 4\pi = 20\pi$.

Q: Surface area of a sphere of radius 3?
A: $36\pi$.

@@ 3 | TOPIC | Stokes' theorem and the divergence theorem | Circulation = flux of curl; outward flux = integral of divergence; Maxwell's equations | NICE
# Stokes' theorem and the divergence theorem

## 1. What it covers
- **Stokes' theorem**: $\oint_C\vec{F} \cdot d\vec{r} = \iint_S(\nabla \times \vec{F}) \cdot d\vec{S}$.
- **Divergence (Gauss's) theorem**: $\oiint_S\vec{F} \cdot d\vec{S} = \iiint_E\nabla \cdot \vec{F}\,dV$.
- Using them to simplify calculations.
- The big picture: all are forms of the fundamental theorem of calculus.

## 2. Where it is used
- **Electromagnetism:** Gauss's law (flux of the electric field = enclosed charge) and Faraday's and Ampère's laws are these theorems applied to Maxwell's equations; they explain how motors, generators and antennas work.
- **Fluid dynamics:** conservation of mass (what flows out of a region equals what's produced inside).
- **Heat transfer:** total heat flowing out of a body.
- **Numerical simulation:** finite-volume methods (used in weather, aerospace and game fluids) are built on the divergence theorem.

## 3. What it means
**Divergence theorem:** the total amount of fluid flowing **out** through a closed surface equals the total amount **produced** inside (the sum of all the little sources and sinks). Like a crowded room: the net number of people leaving through all the doors equals the net number created inside (which is zero unless people appear from nowhere).

**Stokes' theorem:** the circulation around a loop equals the total swirl (curl) through **any** surface bounded by that loop. Green's theorem is the flat special case.

**The pattern:**
| Theorem | Inside | Boundary |
|---|---|---|
| FTC | $\int_a^b f'\,dx$ | $f(b) - f(a)$ |
| Line integrals | $\int_C\nabla f \cdot d\vec{r}$ | $f(B) - f(A)$ |
| Green / Stokes | $\iint\text{curl}$ | $\oint$ circulation |
| Divergence | $\iiint\text{div}$ | $\oiint$ flux |

## 4. Formula & worked example
$$
\oint_C\vec{F} \cdot d\vec{r} = \iint_S(\nabla \times \vec{F}) \cdot d\vec{S} \qquad \oiint_S\vec{F} \cdot d\vec{S} = \iiint_E\nabla \cdot \vec{F}\,dV
$$

**Example 1 (divergence theorem check).** $\vec{F} = \langle x, y, z\rangle$, sphere of radius $R$: $\nabla \cdot \vec{F} = 3$ → $\iiint 3\,dV = 3 \cdot \frac{4}{3}\pi R^3 = 4\pi R^3$ ✓ (matches the direct flux calculation).

**Example 2 (cube).** $\vec{F} = \langle x^2, y^2, z^2\rangle$ out of the cube $[0, 1]^3$: $\nabla \cdot \vec{F} = 2x + 2y + 2z$ → $\iiint = 3$ (much easier than six face integrals).

**Example 3 (Stokes).** $\vec{F} = \langle -y, x, 0\rangle$ around the unit circle in the xy-plane: curl $= \langle 0, 0, 2\rangle$; flux through the unit disk $= 2\pi$ ✓ (same as the line integral).

**Example 4 (Gauss's law).** A point charge $q$ at the centre of any closed surface: the electric flux through it is $\frac{q}{\varepsilon_0}$, whatever the surface's shape, because the field's divergence is zero everywhere except at the charge.

## 5. Common mistakes
- Using the divergence theorem on a surface that isn't closed.
- Wrong orientation (outward normal for the divergence theorem; right-hand rule linking $C$ and $S$ for Stokes).
- Forgetting that Stokes' surface can be any surface with the given boundary (choose the easiest).

## 6. Practice
Q: State the divergence theorem.
A: Outward flux through a closed surface = triple integral of the divergence inside.

Q: State Stokes' theorem.
A: Circulation around a loop = flux of the curl through a surface bounded by the loop.

Q: Flux of a divergence-free field out of any closed surface?
A: Zero.

Q: Which theorem is the 2D version of Stokes'?
A: Green's theorem.

Q: Common idea behind all these theorems?
A: The integral of a derivative over a region equals a boundary integral (generalised FTC).

@@ 3 | TOPIC | Practice & review: Vector calculus | Mixed problem set with worked answers | NICE
# Practice & review: Vector calculus

**How to use this:** 50 minutes. Draw each curve or surface and its orientation.

## Problems
1. $\int_C xy\,ds$ along the quarter circle $x = 2\cos t$, $y = 2\sin t$, $0 \le t \le \frac{\pi}{2}$.
2. Work done by $\vec{F} = \langle x, -y\rangle$ along $\vec{r} = \langle t, t^3\rangle$, $0 \le t \le 1$.
3. Show $\vec{F} = \langle e^y, xe^y + 2y\rangle$ is conservative, find a potential and the work from $(0, 0)$ to $(2, 1)$.
4. Use Green's theorem: $\oint_C(y^2\,dx + 3xy\,dy)$ around the boundary of $[0, 2] \times [0, 1]$ (anticlockwise).
5. Compute the divergence and curl of $\vec{F} = \langle yz, xz, xy\rangle$. Is it conservative?
6. Use the divergence theorem for the outward flux of $\vec{F} = \langle x^3, y^3, z^3\rangle$ through the unit sphere.

## Answers
1. $\int_0^{\pi/2}4\cos t\sin t \cdot 2\,dt = 4$.
2. $\int_0^1(t \cdot 1 - t^3 \cdot 3t^2)\,dt = \frac{1}{2} - \frac{1}{2} = 0$.
3. $P_y = e^y = Q_x$ ✓; $f = xe^y + y^2$; work $= f(2, 1) - f(0, 0) = 2e + 1 \approx 6.44$.
4. $Q_x - P_y = 3y - 2y = y$ → $\int_0^2\int_0^1 y\,dy\,dx = 1$.
5. Divergence $0$; curl $\langle x - x, y - y, z - z\rangle = \vec{0}$ → conservative ($f = xyz$).
6. $\nabla \cdot \vec{F} = 3(x^2 + y^2 + z^2) = 3\rho^2$ → $\int_0^{2\pi}\int_0^\pi\int_0^1 3\rho^4\sin\phi\,d\rho\,d\phi\,d\theta = \frac{3}{5} \cdot 2 \cdot 2\pi = \frac{12\pi}{5}$.

## Flashcards
Q: Fundamental theorem for line integrals?
A: $\int_C\nabla f \cdot d\vec{r} = f(B) - f(A)$.

Q: Green's theorem?
A: $\oint P\,dx + Q\,dy = \iint(Q_x - P_y)\,dA$.

Q: Divergence vs curl?
A: Divergence measures spreading (sources/sinks); curl measures rotation.

Q: Divergence theorem?
A: $\oiint\vec{F} \cdot d\vec{S} = \iiint\nabla \cdot \vec{F}\,dV$.
