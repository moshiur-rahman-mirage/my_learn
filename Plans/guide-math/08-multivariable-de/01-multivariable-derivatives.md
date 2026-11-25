@@ 1 | MODULE | Phase 1 — Functions of Several Variables (Foundation) | Multivariable functions, graphs and contour maps, vector fields, lines, planes and parametric surfaces | SHOULD
# Phase 1: Functions of several variables

Real quantities usually depend on **many** inputs: temperature depends on position and time; a loss function in machine learning depends on millions of weights; profit depends on price and advertising. **Multivariable calculus** extends derivatives and integrals to such functions. **Differential equations** (Phase 6) describe how systems change over time.

**What you need first:** the Calculus roadmap (derivatives, integrals, the chain rule) and the Linear Algebra roadmap (vectors, matrices, eigenvalues).

Each topic has six parts: what it covers, where it is used, what it means, formulas with worked examples, common mistakes, and practice flashcards.

@@ 2 | MODULE | Multivariable functions and geometry | Functions of two or three variables, contour maps, vector fields, lines and planes, parametric surfaces | SHOULD
Before differentiating, learn to picture functions of several variables.

@@ 3 | TOPIC | Functions of several variables, graphs and contour maps | f(x, y), domains, surfaces z = f(x, y), level curves and level surfaces | SHOULD
# Functions of several variables, graphs and contour maps

## 1. What it covers
- Functions $f(x, y)$ and $f(x, y, z)$; their domains.
- Graphs $z = f(x, y)$ as **surfaces** in 3D.
- **Level curves** (contour lines) $f(x, y) = c$ and **contour maps**.
- Level surfaces $f(x, y, z) = c$.

## 2. Where it is used
- **Maps:** topographic maps draw contour lines of equal height; weather maps show isobars (equal pressure) and isotherms.
- **Machine learning:** loss landscapes are visualised as contour plots of the loss over two parameters.
- **Economics:** indifference curves and isoquants are level curves of utility and production functions.
- **Physics:** equipotential surfaces of electric fields.

## 3. What it means
A function of two variables assigns a number to each point $(x, y)$ in the plane, like the height of terrain at each map position. Its graph $z = f(x, y)$ is a surface (a landscape).

**Contour map:** slice the landscape horizontally at heights $c_1, c_2, \ldots$ and draw the slices on the flat map. Each curve connects points with the same value.
- Contours **close together** → the surface is **steep**.
- Contours **far apart** → gentle slope.
- Closed loops shrinking to a point → a peak or a valley.

For three variables, $f(x, y, z) = c$ gives **level surfaces** (we can't graph a 4D surface, but we can see these).

## 4. Formula & worked example
**Example 1 (domain).** $f(x, y) = \sqrt{9 - x^2 - y^2}$: need $x^2 + y^2 \le 9$ → the disk of radius 3. The graph is the top half of a sphere.

**Example 2 (level curves).** $f(x, y) = x^2 + y^2$: level curves $x^2 + y^2 = c$ are circles of radius $\sqrt{c}$. The surface is a bowl (paraboloid), and the circles get closer together further out (steeper).

**Example 3 (saddle).** $f(x, y) = x^2 - y^2$: level curves are hyperbolas; the surface is a saddle (like a Pringle).

**Example 4 (economics).** Utility $U(x, y) = xy$: indifference curves $xy = c$ (hyperbolas): combinations of two goods giving equal satisfaction.

**Example 5 (code).**
```python
import numpy as np, matplotlib.pyplot as plt
x, y = np.meshgrid(np.linspace(-3, 3, 200), np.linspace(-3, 3, 200))
z = x**2 - y**2                      # saddle
plt.contour(x, y, z, levels=15); plt.colorbar(); plt.gca().set_aspect("equal"); plt.show()
```
`meshgrid` builds a grid of $(x, y)$ points; `contour` draws the level curves of $z$ on it, exactly like a topographic map.

## 5. Common mistakes
- Reading contour spacing backwards (close lines mean steep).
- Forgetting domain restrictions (square roots, logs, denominators).
- Confusing a level curve (in the xy-plane) with the surface itself.

## 6. Practice
Q: Domain of $f(x, y) = \ln(x - y)$?
A: All points with $x > y$.

Q: Level curves of $f(x, y) = 2x + y$?
A: Parallel lines $2x + y = c$.

Q: What do closely spaced contour lines indicate?
A: A steep slope.

Q: Shape of the surface $z = x^2 + y^2$?
A: A bowl (paraboloid).

Q: Level surfaces of $f(x, y, z) = x^2 + y^2 + z^2$?
A: Spheres centred at the origin.

@@ 3 | TOPIC | Lines, planes and parametric surfaces in 3D | Vector and parametric equations of lines, equations of planes from a normal, parametrising surfaces | SHOULD
# Lines, planes and parametric surfaces in 3D

## 1. What it covers
- Lines in 3D: $\vec{r}(t) = \vec{r}_0 + t\vec{v}$.
- Planes: $a(x - x_0) + b(y - y_0) + c(z - z_0) = 0$ from a **normal vector** $\langle a, b, c\rangle$.
- Finding a plane through three points (cross product).
- **Parametric surfaces** $\vec{r}(u, v)$ (spheres, cylinders, tori).

## 2. Where it is used
- **3D graphics and games:** rays (lines) and triangles (pieces of planes); ray–plane intersection for picking and collisions.
- **CAD and 3D printing:** parametric surfaces describe shapes (NURBS in car and aircraft design).
- **Robotics:** planning straight-line motions.
- **Computer vision:** fitting planes to point clouds (e.g. detecting floors from lidar).

## 3. What it means
**Line:** start at a point $\vec{r}_0$ and move along a direction $\vec{v}$; the parameter $t$ says how far.

**Plane:** a plane is determined by a point on it and a **normal vector** $\vec{n}$ perpendicular to it. A point $\vec{r}$ is on the plane exactly when $\vec{r} - \vec{r}_0$ is perpendicular to $\vec{n}$: $\vec{n} \cdot (\vec{r} - \vec{r}_0) = 0$.

**Parametric surface:** two parameters $(u, v)$, like longitude and latitude, map a flat rectangle onto a curved surface.

## 4. Formula & worked example
$$
\vec{r}(t) = \vec{r}_0 + t\vec{v} \qquad a(x - x_0) + b(y - y_0) + c(z - z_0) = 0 \qquad \text{sphere: } \vec{r}(\theta, \phi) = \langle R\sin\phi\cos\theta,\ R\sin\phi\sin\theta,\ R\cos\phi\rangle
$$

**Example 1 (line).** Through $(1, 2, 3)$ and $(4, 0, 5)$: $\vec{v} = \langle 3, -2, 2\rangle$ → $\vec{r}(t) = \langle 1 + 3t, 2 - 2t, 3 + 2t\rangle$.

**Example 2 (plane from 3 points).** $A(1, 0, 0)$, $B(0, 1, 0)$, $C(0, 0, 1)$: $\vec{n} = \vec{AB} \times \vec{AC} = \langle -1, 1, 0\rangle \times \langle -1, 0, 1\rangle = \langle 1, 1, 1\rangle$ → $x + y + z = 1$.

**Example 3 (ray hits plane).** Where does $\vec{r}(t) = \langle t, 2t, 3t\rangle$ meet $x + y + z = 12$? $6t = 12$ → $t = 2$ → $(2, 4, 6)$.

**Example 4 (distance from a point to a plane).** $P(1, 2, 3)$ to $x + y + z = 1$: $\frac{\lvert 1 + 2 + 3 - 1\rvert}{\sqrt{3}} = \frac{5}{\sqrt{3}} \approx 2.89$.

**Example 5 (code: ray–plane intersection).**
```python
import numpy as np
def ray_plane(origin, direction, n, d):          # plane: n . x = d
    denom = n @ direction
    if abs(denom) < 1e-9: return None            # ray parallel to the plane
    t = (d - n @ origin) / denom
    return origin + t * direction if t >= 0 else None
print(ray_plane(np.zeros(3), np.array([1., 2, 3]), np.array([1., 1, 1]), 12))   # [2. 4. 6.]
```
Substituting the line into the plane equation gives one equation for $t$; this is the core of mouse picking and ray tracing.

## 5. Common mistakes
- Using a direction vector of the plane instead of its normal in the plane equation.
- Forgetting that a line in 3D can't be written as a single equation $y = mx + b$.
- Not checking for parallel rays (division by zero).

## 6. Practice
Q: Parametric line through $(0, 1, 2)$ with direction $\langle 1, 0, -1\rangle$?
A: $\langle t, 1, 2 - t\rangle$.

Q: Plane through $(1, 1, 1)$ with normal $\langle 2, -1, 3\rangle$?
A: $2x - y + 3z = 4$.

Q: Normal vector of $3x - 2y + z = 7$?
A: $\langle 3, -2, 1\rangle$.

Q: How do you find a plane through three points?
A: Take the cross product of two edge vectors for the normal, then use one point.

Q: Are the planes $x + y + z = 1$ and $2x + 2y + 2z = 5$ parallel?
A: Yes (proportional normals).

@@ 3 | TOPIC | Vector fields | Assigning vectors to points, plotting fields, flow, gravitational and electric fields, gradient fields | SHOULD
# Vector fields

## 1. What it covers
- A **vector field** $\vec{F}(x, y) = \langle P, Q\rangle$ assigns a vector to every point.
- Sketching fields; **flow lines** (streamlines).
- Examples: wind, water flow, gravity, electric fields, **gradient fields**.

## 2. Where it is used
- **Weather and oceanography:** wind and current maps.
- **Physics:** gravitational, electric and magnetic fields.
- **Fluid simulation:** velocity fields in games and CGI (smoke, water).
- **Machine learning:** the negative gradient of a loss is a vector field that gradient descent follows.
- **Robotics:** potential-field path planning (attracted to the goal, repelled by obstacles).

## 3. What it means
At each point, draw an arrow showing a direction and strength: wind speed and direction at each location on a weather map. A particle released in the field follows the arrows; its path is a **flow line**.

**Gradient field:** for a function $f$, the field $\nabla f$ (next phase) points uphill everywhere. Fields that are gradients of some function are called **conservative** (Phase 5).

## 4. Formula & worked example
$$
\vec{F}(x, y) = \langle P(x, y), Q(x, y)\rangle \qquad \vec{F}(x, y, z) = \langle P, Q, R\rangle
$$

**Example 1.** $\vec{F} = \langle x, y\rangle$: arrows point straight away from the origin, longer further out (a source).

**Example 2.** $\vec{F} = \langle -y, x\rangle$: arrows circle anticlockwise around the origin (a rotation, like a whirlpool).

**Example 3 (gravity).** $\vec{F} = -\frac{GMm}{r^3}\langle x, y, z\rangle$: points towards the mass, stronger closer in (inverse square).

**Example 4 (gradient field).** $f(x, y) = x^2 + y^2$ → $\nabla f = \langle 2x, 2y\rangle$: points away from the minimum at the origin; $-\nabla f$ points towards it (the direction gradient descent moves).

**Example 5 (code).**
```python
import numpy as np, matplotlib.pyplot as plt
x, y = np.meshgrid(np.linspace(-2, 2, 15), np.linspace(-2, 2, 15))
plt.quiver(x, y, -y, x)              # rotational field <-y, x>
plt.streamplot(x, y, -y, x, color="gray")
plt.gca().set_aspect("equal"); plt.show()
```
`quiver` draws an arrow at each grid point; `streamplot` draws flow lines that follow the arrows (circles for this field).

## 5. Common mistakes
- Swapping components when sketching (first component is horizontal).
- Thinking arrow length is irrelevant (it shows the field's strength).
- Confusing a vector field with a scalar function.

## 6. Practice
Q: What does a vector field assign to each point?
A: A vector.

Q: Describe $\vec{F} = \langle 1, 0\rangle$.
A: A uniform field: equal arrows pointing right everywhere.

Q: Describe $\vec{F} = \langle -x, -y\rangle$.
A: Arrows point towards the origin (a sink).

Q: What is a flow line?
A: The path of a particle that follows the field's arrows.

Q: Gradient field of $f = xy$?
A: $\langle y, x\rangle$.

@@ 3 | TOPIC | Practice & review: Functions of several variables | Mixed problem set with worked answers | SHOULD
# Practice & review: Functions of several variables

**How to use this:** 30 minutes; sketch every answer.

## Problems
1. Find and sketch the domain of $f(x, y) = \frac{\sqrt{y - x^2}}{x - 1}$.
2. Describe the level curves of $f(x, y) = \frac{y}{x^2}$ for $c = 0, 1, 2$.
3. Find the parametric equation of the line through $(2, -1, 0)$ parallel to the line $\langle 1 + t, 3t, 2 - t\rangle$.
4. Find the plane through $(1, 2, 0)$, $(0, 1, 1)$ and $(2, 0, 1)$.
5. Where does the line $\langle 1 + t, 2 - t, 3t\rangle$ meet the plane $2x + y + z = 10$?
6. Sketch $\vec{F} = \langle y, -x\rangle$. Which way does it circulate?

## Answers
1. Need $y \ge x^2$ and $x \ne 1$: the region on or above the parabola, minus the line $x = 1$.
2. $y = cx^2$ ($x \ne 0$): the x-axis ($c = 0$), the parabola $y = x^2$, the narrower parabola $y = 2x^2$.
3. Direction $\langle 1, 3, -1\rangle$ → $\langle 2 + t, -1 + 3t, -t\rangle$.
4. $\vec{AB} = \langle -1, -1, 1\rangle$, $\vec{AC} = \langle 1, -2, 1\rangle$; $\vec{n} = \langle 1, 2, 3\rangle$ → $x + 2y + 3z = 5$.
5. $2 + 2t + 2 - t + 3t = 10$ → $t = 1.5$ → $(2.5, 0.5, 4.5)$.
6. Clockwise around the origin.

## Flashcards
Q: Level curve?
A: A curve $f(x, y) = c$ connecting points with the same value.

Q: Plane equation from a normal $\langle a, b, c\rangle$ and point?
A: $a(x - x_0) + b(y - y_0) + c(z - z_0) = 0$.

Q: Vector field?
A: A function assigning a vector to each point.

Q: Parametric line?
A: $\vec{r}(t) = \vec{r}_0 + t\vec{v}$.

@@ 1 | MODULE | Phase 2 — Partial Derivatives, Gradients and Jacobians (Core) | Partial derivatives, tangent planes, gradient and directional derivatives, multivariable chain rule, Jacobian matrix | MUST
# Phase 2: Partial derivatives, gradients and Jacobians

How does a function of many variables change? Change **one** input at a time (partial derivatives), collect all those rates into a vector (the **gradient**), and you get the direction of steepest increase: the single most important object in machine learning optimisation. The **Jacobian** generalises the derivative to vector-valued functions and is central to robotics, physics simulations and backpropagation.

@@ 2 | MODULE | Differentiating multivariable functions | Partial derivatives, linearisation, gradient, directional derivatives, chain rule, Jacobian | MUST
Freeze all variables but one, and it's ordinary calculus again.

@@ 3 | TOPIC | Partial derivatives | ∂f/∂x and ∂f/∂y, geometric meaning, higher-order and mixed partials, Clairaut's theorem | MUST
# Partial derivatives

## 1. What it covers
- **Partial derivatives** $\frac{\partial f}{\partial x}$ and $\frac{\partial f}{\partial y}$ (also $f_x$, $f_y$).
- Their meaning as slopes in the $x$ and $y$ directions.
- **Second-order** and **mixed** partials; **Clairaut's theorem** ($f_{xy} = f_{yx}$).

## 2. Where it is used
- **Machine learning:** the gradient of the loss is the vector of partial derivatives with respect to each weight.
- **Economics:** marginal products ($\frac{\partial Q}{\partial L}$: extra output per extra worker, holding capital fixed).
- **Physics and engineering:** partial differential equations (heat, wave, Navier–Stokes) are written with partial derivatives.
- **Sensitivity analysis:** how much an output changes when one input is nudged.

## 3. What it means
$\frac{\partial f}{\partial x}$: how fast $f$ changes as $x$ changes, **with $y$ held fixed**. Treat $y$ like a constant and differentiate normally.

Geometrically: slice the surface $z = f(x, y)$ with the plane $y = b$; you get a curve, and $f_x(a, b)$ is its slope at $x = a$. Walking east on a hillside, $f_x$ is how steep it is in that direction.

**Second partials:** $f_{xx}$ (curvature in $x$), $f_{yy}$, and mixed $f_{xy}$, $f_{yx}$. For smooth functions the mixed partials are equal (Clairaut).

## 4. Formula & worked example
$$
f_x = \frac{\partial f}{\partial x} = \lim_{h \to 0}\frac{f(x + h, y) - f(x, y)}{h} \qquad f_{xy} = f_{yx}
$$

**Example 1.** $f(x, y) = x^3y^2 + 4xy - 5y$
- $f_x = 3x^2y^2 + 4y$; $f_y = 2x^3y + 4x - 5$

**Example 2.** $f(x, y) = e^{xy}$: $f_x = ye^{xy}$, $f_y = xe^{xy}$.

**Example 3.** $f(x, y) = \sin(x^2y)$: $f_x = 2xy\cos(x^2y)$, $f_y = x^2\cos(x^2y)$.

**Example 4 (second order).** For Example 1: $f_{xx} = 6xy^2$, $f_{yy} = 2x^3$, $f_{xy} = 6x^2y + 4 = f_{yx}$ ✓

**Example 5 (economics).** Production $Q = 10L^{0.5}K^{0.5}$ with $L = 100$ workers, $K = 400$ machines.
- $Q_L = 5L^{-0.5}K^{0.5} = 5 \cdot 0.1 \cdot 20 = 10$ units per extra worker.

**Example 6 (numerical, code).**
```python
def partial(f, x, y, var, h=1e-6):
    if var == "x": return (f(x + h, y) - f(x - h, y)) / (2 * h)
    return (f(x, y + h) - f(x, y - h)) / (2 * h)
f = lambda x, y: x**3 * y**2 + 4*x*y - 5*y
print(partial(f, 1, 2, "x"), partial(f, 1, 2, "y"))   # 20.0  3.0
```
Nudging only one variable approximates a partial derivative: at $(1, 2)$, $f_x = 3(1)(4) + 8 = 20$ and $f_y = 2(1)(2) + 4 - 5 = 3$.

## 5. Common mistakes
- Differentiating the "constant" variable too (in $f_x$, $y$ is fixed: $\frac{\partial}{\partial x}(y^2) = 0$).
- Forgetting the chain rule inside functions like $e^{xy}$.
- Mixing up $f_{xy}$ notation order (it rarely matters for smooth functions).

## 6. Practice
Q: $f = x^2y + y^3$. Find $f_x$ and $f_y$.
A: $f_x = 2xy$; $f_y = x^2 + 3y^2$.

Q: $f = \ln(x^2 + y^2)$. Find $f_x$.
A: $\frac{2x}{x^2 + y^2}$.

Q: What does $f_y(a, b)$ measure?
A: The rate of change of $f$ in the $y$ direction at $(a, b)$, with $x$ fixed.

Q: $f = x^2y^3$. Find $f_{xy}$.
A: $6xy^2$.

Q: State Clairaut's theorem.
A: For smooth functions, $f_{xy} = f_{yx}$.

@@ 3 | TOPIC | Tangent planes and linear approximation | Equation of the tangent plane, linearisation, differentials and error estimates | MUST
# Tangent planes and linear approximation

## 1. What it covers
- The **tangent plane** to $z = f(x, y)$ at a point.
- **Linearisation** $L(x, y)$ for approximation.
- **Total differential** $dz = f_x\,dx + f_y\,dy$ for estimating changes and errors.

## 2. Where it is used
- **Error propagation:** how measurement errors in several inputs affect a computed result.
- **Engineering:** linearising non-linear systems around an operating point (control design).
- **Optimisation:** gradient descent relies on the function being approximately linear for small steps.
- **Graphics:** tangent planes for shading and bump mapping.

## 3. What it means
Zoom in on a smooth surface and it looks flat: that flat piece is the **tangent plane**. Near the point, the function is well approximated by
$$
L(x, y) = f(a, b) + f_x(a, b)(x - a) + f_y(a, b)(y - b)
$$
Each partial derivative contributes the slope in its own direction.

**Differentials:** small changes $dx$ and $dy$ cause a change of about $dz = f_x\,dx + f_y\,dy$.

## 4. Formula & worked example
$$
z = f(a, b) + f_x(a, b)(x - a) + f_y(a, b)(y - b) \qquad dz = f_x\,dx + f_y\,dy
$$

**Example 1.** Tangent plane to $f(x, y) = x^2 + y^2$ at $(1, 2)$.
- $f = 5$, $f_x = 2$, $f_y = 4$ → $z = 5 + 2(x - 1) + 4(y - 2)$

**Example 2 (approximate).** Estimate $\sqrt{3.02^2 + 3.97^2}$ using $f(x, y) = \sqrt{x^2 + y^2}$ at $(3, 4)$.
- $f = 5$, $f_x = \frac{3}{5}$, $f_y = \frac{4}{5}$ → $5 + 0.6(0.02) + 0.8(-0.03) = 4.988$ (true $4.98806$)

**Example 3 (error).** A cylinder's radius is $r = 5 \pm 0.1$ cm, height $h = 12 \pm 0.2$ cm. Volume error?
- $V = \pi r^2h$: $dV = 2\pi rh\,dr + \pi r^2\,dh = 2\pi(60)(0.1) + \pi(25)(0.2) = 12\pi + 5\pi = 17\pi \approx 53.4$ cm³ (out of $V = 300\pi \approx 942$, about 5.7%).

**Example 4 (relative error).** For $V = \pi r^2h$: $\frac{dV}{V} = 2\frac{dr}{r} + \frac{dh}{h} = 2(2\%) + 1.67\% \approx 5.7\%$ ✓ (radius errors count double because $r$ is squared).

## 5. Common mistakes
- Evaluating partials at the new point instead of the base point.
- Forgetting the $f(a, b)$ term.
- Using the approximation far from the base point.

## 6. Practice
Q: Linearisation formula?
A: $L = f(a, b) + f_x(a, b)(x - a) + f_y(a, b)(y - b)$.

Q: Tangent plane to $z = xy$ at $(2, 3)$?
A: $z = 6 + 3(x - 2) + 2(y - 3)$.

Q: Total differential of $z = x^2y$?
A: $dz = 2xy\,dx + x^2\,dy$.

Q: Area $A = lw$ with $l = 10 \pm 0.1$, $w = 5 \pm 0.1$. Approximate error?
A: $dA = 5(0.1) + 10(0.1) = 1.5$.

Q: Why do squared variables contribute double relative error?
A: $\frac{d(r^2)}{r^2} = 2\frac{dr}{r}$.

@@ 3 | TOPIC | The gradient and directional derivatives | ∇f, direction of steepest ascent, rate of change in any direction, gradient ⊥ level curves | MUST
# The gradient and directional derivatives

## 1. What it covers
- The **gradient** $\nabla f = \langle f_x, f_y\rangle$ (or with $f_z$).
- **Directional derivative** $D_{\vec{u}}f = \nabla f \cdot \vec{u}$.
- The gradient points in the direction of **steepest ascent**; its length is the maximum rate.
- The gradient is **perpendicular** to level curves.

## 2. Where it is used
- **Machine learning:** gradient descent updates every weight by moving against the gradient of the loss; backpropagation computes that gradient.
- **Physics:** forces are negative gradients of potential energy; heat flows against the temperature gradient.
- **Image processing:** edges are where the brightness gradient is large (Sobel and Canny edge detectors).
- **Graphics:** normals of implicit surfaces $f(x, y, z) = 0$ are $\nabla f$.

## 3. What it means
Standing on a hillside, you can walk in any direction. The **directional derivative** tells you how steep the path is in a chosen direction $\vec{u}$ (a unit vector). It equals $\nabla f \cdot \vec{u} = \lVert\nabla f\rVert\cos\theta$, so:
- it's **largest** when $\vec{u}$ points along $\nabla f$ ($\theta = 0$): the gradient points straight **uphill**, and $\lVert\nabla f\rVert$ is the steepest slope;
- it's **most negative** opposite the gradient (steepest descent);
- it's **zero** perpendicular to the gradient: walking along a contour line keeps your height constant.

So the gradient is always perpendicular to the level curves.

## 4. Formula & worked example
$$
\nabla f = \langle f_x, f_y, f_z\rangle \qquad D_{\vec{u}}f = \nabla f \cdot \vec{u}\ (\lVert\vec{u}\rVert = 1) \qquad \max D_{\vec{u}}f = \lVert\nabla f\rVert
$$

**Example 1.** $f(x, y) = x^2y + 3y$ at $(1, 2)$: $\nabla f = \langle 2xy, x^2 + 3\rangle = \langle 4, 4\rangle$.
- Steepest ascent direction $\frac{1}{\sqrt{2}}\langle 1, 1\rangle$; maximum rate $\lVert\langle 4, 4\rangle\rVert = 4\sqrt{2} \approx 5.66$.

**Example 2 (directional).** Same point, direction $\langle 3, 4\rangle$ (unit $\langle 0.6, 0.8\rangle$): $D_{\vec{u}}f = 4(0.6) + 4(0.8) = 5.6$.

**Example 3 (perpendicular to level curve).** $f = x^2 + y^2$ at $(3, 4)$: $\nabla f = \langle 6, 8\rangle$, pointing straight out from the circle $x^2 + y^2 = 25$ ✓

**Example 4 (heat).** Temperature $T(x, y) = 100 - x^2 - 2y^2$ at $(1, 1)$: $\nabla T = \langle -2, -4\rangle$. Heat flows in the direction $-\nabla T = \langle 2, 4\rangle$ (from hot to cold). A bug wanting to warm up fastest moves along $\nabla T = \langle -2, -4\rangle$, towards the origin, the warmest point.

**Example 5 (image edges, code).**
```python
import numpy as np
img = np.zeros((10, 10)); img[:, 5:] = 1.0      # dark left half, bright right half
gy, gx = np.gradient(img)                         # partial derivatives along rows and columns
mag = np.hypot(gx, gy)                            # gradient magnitude
print(mag[5].round(2))                            # large only at the edge between columns 4 and 5
```
The brightness gradient is zero in flat areas and large where brightness jumps, which is exactly how edge detectors find outlines in images.

## 5. Common mistakes
- Using a non-unit direction vector in the directional derivative.
- Thinking the gradient points along the level curves (it's perpendicular).
- Confusing steepest **ascent** ($\nabla f$) with steepest **descent** ($-\nabla f$).

## 6. Practice
Q: Gradient of $f = 3x^2 - xy + y^2$?
A: $\langle 6x - y, -x + 2y\rangle$.

Q: Direction of steepest increase?
A: The direction of $\nabla f$.

Q: Maximum rate of increase at a point?
A: $\lVert\nabla f\rVert$.

Q: $\nabla f = \langle 2, -1\rangle$. Directional derivative towards $\langle 1, 0\rangle$?
A: $2$.

Q: How is the gradient related to level curves?
A: It's perpendicular to them.

@@ 3 | TOPIC | The multivariable chain rule | Dependency (tree) diagrams, dz/dt for z = f(x(t), y(t)), partials through intermediate variables, implicit differentiation | MUST
# The multivariable chain rule

## 1. What it covers
- $\frac{dz}{dt} = \frac{\partial z}{\partial x}\frac{dx}{dt} + \frac{\partial z}{\partial y}\frac{dy}{dt}$ for $z = f(x(t), y(t))$.
- Chain rule with several intermediate and independent variables; **tree diagrams**.
- Implicit differentiation: $\frac{dy}{dx} = -\frac{F_x}{F_y}$.

## 2. Where it is used
- **Backpropagation:** neural networks are deep compositions; the chain rule (summing over all paths) gives the gradient of the loss with respect to every weight.
- **Physics:** rate of change of temperature felt by a moving object.
- **Economics:** how a policy affects profit through several intermediate quantities.
- **Related rates** with several variables.

## 3. What it means
If $z$ depends on $x$ and $y$, and both depend on $t$, then changing $t$ affects $z$ through **two paths**: via $x$ and via $y$. Each path contributes (rate of $z$ w.r.t. the intermediate) × (rate of the intermediate w.r.t. $t$), and the contributions **add**.

**Tree diagram:** draw $z$ at the top, branches to $x$ and $y$, then to $t$. Multiply along each path, add over paths.

This is exactly what backpropagation does: each weight affects the loss through many paths in the network, and the gradient sums them.

## 4. Formula & worked example
$$
\frac{dz}{dt} = \frac{\partial z}{\partial x}\frac{dx}{dt} + \frac{\partial z}{\partial y}\frac{dy}{dt} \qquad \frac{\partial z}{\partial s} = \frac{\partial z}{\partial x}\frac{\partial x}{\partial s} + \frac{\partial z}{\partial y}\frac{\partial y}{\partial s} \qquad \frac{dy}{dx} = -\frac{F_x}{F_y}
$$

**Example 1.** $z = x^2y$, $x = \cos t$, $y = \sin t$.
- $\frac{dz}{dt} = 2xy(-\sin t) + x^2(\cos t) = -2\cos t\sin^2 t + \cos^3 t$

**Example 2 (moving bug).** Temperature $T(x, y) = x^2 + 3y$; a bug moves with $x = 2t$, $y = t^2$. Rate of temperature change at $t = 1$?
- $T_x = 2x = 4$, $T_y = 3$; $x' = 2$, $y' = 2$ → $\frac{dT}{dt} = 4(2) + 3(2) = 14$

**Example 3 (two parameters).** $z = e^x\sin y$, $x = st$, $y = s + t$: $\frac{\partial z}{\partial s} = e^x\sin y \cdot t + e^x\cos y \cdot 1$.

**Example 4 (implicit).** $x^3 + y^3 = 6xy$: $F = x^3 + y^3 - 6xy$ → $\frac{dy}{dx} = -\frac{3x^2 - 6y}{3y^2 - 6x} = \frac{2y - x^2}{y^2 - 2x}$ (matches the Calculus roadmap).

**Example 5 (tiny backprop, code).**
```python
import math
# loss L = (y - target)^2, y = tanh(w1*x + w2*z)
x, z, w1, w2, target = 1.0, 2.0, 0.5, -0.3, 0.5
u = w1*x + w2*z; y = math.tanh(u); L = (y - target)**2
dL_dy = 2 * (y - target)
dy_du = 1 - y**2
dL_dw1 = dL_dy * dy_du * x          # path: L <- y <- u <- w1
dL_dw2 = dL_dy * dy_du * z          # path: L <- y <- u <- w2
print(round(dL_dw1, 4), round(dL_dw2, 4))
```
Each weight's gradient is a product of local derivatives along its path to the loss. Frameworks like PyTorch build this graph automatically and apply the chain rule backwards.

## 5. Common mistakes
- Forgetting a path (each intermediate variable contributes a term).
- Using ordinary $\frac{d}{dt}$ where partials are needed (or vice versa).
- Sign error in $\frac{dy}{dx} = -\frac{F_x}{F_y}$.

## 6. Practice
Q: $z = xy$, $x = t^2$, $y = t^3$. $\frac{dz}{dt}$ via the chain rule?
A: $y(2t) + x(3t^2) = 2t^4 + 3t^4 = 5t^4$.

Q: How many terms does $\frac{dz}{dt}$ have if $z$ depends on 3 intermediate variables of $t$?
A: $3$.

Q: $\frac{dy}{dx}$ for $x^2 + y^2 = 25$ using $-\frac{F_x}{F_y}$?
A: $-\frac{x}{y}$.

Q: Which ML algorithm is the multivariable chain rule?
A: Backpropagation.

Q: $T = xy$, $x = 3$, $y = 4$, $x' = 1$, $y' = -2$. $T'$?
A: $4(1) + 3(-2) = -2$.

@@ 3 | TOPIC | The Jacobian matrix | Derivative of a vector-valued function as a matrix, linear approximation, chain rule as matrix multiplication | MUST
# The Jacobian matrix

## 1. What it covers
- The **Jacobian matrix** $J$ of $\vec{F}: \mathbb{R}^n \to \mathbb{R}^m$: all first partial derivatives.
- The Jacobian as the **best linear approximation** of $\vec{F}$ near a point.
- The chain rule as **matrix multiplication** of Jacobians.
- The Jacobian **determinant** (used in Phase 4).

## 2. Where it is used
- **Robotics:** the Jacobian maps joint velocities to the hand's velocity; inverse kinematics uses its inverse or pseudo-inverse.
- **Deep learning:** automatic differentiation computes Jacobian–vector products layer by layer.
- **Newton's method for systems** of non-linear equations.
- **Change of variables** in integrals (polar, spherical) uses the Jacobian determinant.
- **Computer vision:** camera calibration and bundle adjustment.

## 3. What it means
For a function with several inputs and several outputs, the "derivative" is a **matrix**: row $i$ holds the partial derivatives of output $i$ with respect to each input. Near a point,
$$
\vec{F}(\vec{x} + \Delta\vec{x}) \approx \vec{F}(\vec{x}) + J\Delta\vec{x}
$$
so the Jacobian is the **linear transformation** that best approximates $\vec{F}$ locally (zoom in on any smooth map and it looks linear).

For a scalar function, the Jacobian is just the gradient (as a row).

**Chain rule:** composing functions multiplies their Jacobians: $J_{\vec{F} \circ \vec{G}} = J_{\vec{F}} J_{\vec{G}}$.

## 4. Formula & worked example
$$
J = \begin{pmatrix} \frac{\partial F_1}{\partial x_1} & \cdots & \frac{\partial F_1}{\partial x_n} \\ \vdots & & \vdots \\ \frac{\partial F_m}{\partial x_1} & \cdots & \frac{\partial F_m}{\partial x_n}\end{pmatrix}
$$

**Example 1.** $\vec{F}(x, y) = \langle x^2y,\ 5x + \sin y\rangle$: $J = \begin{pmatrix} 2xy & x^2 \\ 5 & \cos y\end{pmatrix}$.

**Example 2 (polar to Cartesian).** $x = r\cos\theta$, $y = r\sin\theta$: $J = \begin{pmatrix} \cos\theta & -r\sin\theta \\ \sin\theta & r\cos\theta\end{pmatrix}$, $\det J = r$ (this $r$ appears in polar integrals).

**Example 3 (robot arm).** Two links of length 1 with angles $\theta_1$, $\theta_2$: hand position $x = \cos\theta_1 + \cos(\theta_1 + \theta_2)$, $y = \sin\theta_1 + \sin(\theta_1 + \theta_2)$.
- $J = \begin{pmatrix} -\sin\theta_1 - \sin(\theta_1 + \theta_2) & -\sin(\theta_1 + \theta_2) \\ \cos\theta_1 + \cos(\theta_1 + \theta_2) & \cos(\theta_1 + \theta_2)\end{pmatrix}$; hand velocity $= J\langle\dot{\theta}_1, \dot{\theta}_2\rangle$. Where $\det J = 0$ (arm fully stretched), the arm is at a **singularity** and can't move in some directions.

**Example 4 (Newton's method for systems, code).**
```python
import numpy as np
F = lambda v: np.array([v[0]**2 + v[1]**2 - 4, v[0] - v[1]])      # circle meets line y = x
J = lambda v: np.array([[2*v[0], 2*v[1]], [1, -1]])
v = np.array([1.0, 0.5])
for _ in range(6):
    v = v - np.linalg.solve(J(v), F(v))          # Newton step using the Jacobian
print(v)                                         # [1.41421356 1.41421356]
```
Each step replaces $\vec{F}$ by its linear approximation $\vec{F}(\vec{v}) + J\Delta\vec{v}$, solves for where that's zero, and repeats; it converges to $(\sqrt{2}, \sqrt{2})$ very fast.

## 5. Common mistakes
- Transposing the Jacobian (rows = outputs, columns = inputs).
- Multiplying Jacobians in the wrong order in the chain rule.
- Forgetting the absolute value of the Jacobian determinant in change of variables.

## 6. Practice
Q: Size of the Jacobian of $\vec{F}: \mathbb{R}^3 \to \mathbb{R}^2$?
A: $2 \times 3$.

Q: Jacobian of $\vec{F}(x, y) = \langle x + y, xy\rangle$?
A: $\begin{pmatrix} 1 & 1 \\ y & x\end{pmatrix}$.

Q: Jacobian determinant for polar coordinates?
A: $r$.

Q: What does the Jacobian represent geometrically?
A: The best linear approximation of the function near a point.

Q: What happens where a robot arm's Jacobian determinant is 0?
A: It's at a singularity and loses the ability to move in some direction.

@@ 3 | TOPIC | Practice & review: Partial derivatives and gradients | Mixed problem set with worked answers | MUST
# Practice & review: Partial derivatives and gradients

**How to use this:** 45 minutes; check with SymPy (`sp.diff`).

## Problems
1. Find all first and second partial derivatives of $f(x, y) = x^2e^{3y}$.
2. Find the tangent plane to $z = \ln(x^2 + y)$ at $(1, 0)$.
3. Use differentials to estimate the change in $f(x, y) = x^2y$ from $(3, 2)$ to $(3.05, 1.98)$.
4. $f(x, y, z) = xy + yz^2$. Find $\nabla f$ at $(1, 2, 1)$ and the directional derivative towards $\langle 2, 2, 1\rangle$.
5. In which direction does $f(x, y) = 4 - x^2 - 2y^2$ decrease fastest at $(1, 1)$? At what rate?
6. $w = x^2 + y^2 + z^2$ with $x = t$, $y = \cos t$, $z = \sin t$. Find $\frac{dw}{dt}$ (simplify).
7. Find the Jacobian of $\vec{F}(u, v) = \langle u^2 - v^2, 2uv\rangle$ and its determinant.

## Answers
1. $f_x = 2xe^{3y}$, $f_y = 3x^2e^{3y}$; $f_{xx} = 2e^{3y}$, $f_{yy} = 9x^2e^{3y}$, $f_{xy} = f_{yx} = 6xe^{3y}$.
2. $f(1, 0) = 0$; $f_x = \frac{2x}{x^2 + y} = 2$, $f_y = \frac{1}{x^2 + y} = 1$ → $z = 2(x - 1) + y$.
3. $dz = 2xy\,dx + x^2\,dy = 12(0.05) + 9(-0.02) = 0.42$.
4. $\nabla f = \langle y, x + z^2, 2yz\rangle = \langle 2, 2, 4\rangle$; unit direction $\frac{1}{3}\langle 2, 2, 1\rangle$ → $\frac{4 + 4 + 4}{3} = 4$.
5. $\nabla f = \langle -2, -4\rangle$; fastest decrease along $-\nabla f = \langle 2, 4\rangle$, rate $\sqrt{20} = 2\sqrt{5}$.
6. $w = t^2 + 1$ → $\frac{dw}{dt} = 2t$ (chain rule gives $2t + 2\cos t(-\sin t) + 2\sin t\cos t = 2t$).
7. $J = \begin{pmatrix} 2u & -2v \\ 2v & 2u\end{pmatrix}$; $\det = 4u^2 + 4v^2$.

## Flashcards
Q: How do you compute $f_x$?
A: Differentiate with respect to $x$, treating other variables as constants.

Q: Directional derivative?
A: $D_{\vec{u}}f = \nabla f \cdot \vec{u}$ with $\vec{u}$ a unit vector.

Q: Direction of steepest descent?
A: $-\nabla f$.

Q: Jacobian rows and columns?
A: Rows = output functions; columns = input variables.

@@ 1 | MODULE | Phase 3 — Multivariable Optimisation (Core) | Critical points, the Hessian and second derivative test, Lagrange multipliers, gradient descent in several variables | SHOULD
# Phase 3: Multivariable optimisation

Finding the best combination of several inputs: the cheapest design, the most profitable prices, the model weights with the lowest error. This phase covers the exact methods (critical points, Hessians, Lagrange multipliers) and the numerical one that powers machine learning (gradient descent).

@@ 2 | MODULE | Optimisation in several variables | Critical points, Hessian, constrained optimisation, gradient descent | SHOULD
At a smooth peak or valley, every slope is zero: the gradient vanishes.

@@ 3 | TOPIC | Critical points, the Hessian and the second derivative test | ∇f = 0, local max/min/saddle, Hessian matrix, D = f_xx f_yy − f_xy², eigenvalue view, absolute extrema on regions | SHOULD
# Critical points, the Hessian and the second derivative test

## 1. What it covers
- **Critical points**: where $\nabla f = \vec{0}$ (or undefined).
- Local maxima, minima and **saddle points**.
- The **Hessian matrix** of second derivatives and the **second derivative test** $D = f_{xx}f_{yy} - f_{xy}^2$.
- The eigenvalue view of the Hessian; absolute extrema on closed regions.

## 2. Where it is used
- **Machine learning:** loss surfaces of neural networks have huge numbers of saddle points; the Hessian's eigenvalues describe curvature and guide second-order optimisers.
- **Economics:** profit maximisation with several products.
- **Engineering design:** minimising material or energy.
- **Statistics:** maximum likelihood estimates; the Hessian gives standard errors.

## 3. What it means
At a local max or min of a smooth function, the surface is flat in every direction: $f_x = f_y = 0$. But a flat point might also be a **saddle**: a maximum in one direction and a minimum in another, like the centre of a horse saddle or a mountain pass.

**Hessian:** $H = \begin{pmatrix} f_{xx} & f_{xy} \\ f_{xy} & f_{yy}\end{pmatrix}$ describes the curvature. Its determinant $D$ decides:
- $D > 0$ and $f_{xx} > 0$ → **local minimum** (curves up in all directions).
- $D > 0$ and $f_{xx} < 0$ → **local maximum**.
- $D < 0$ → **saddle point**.
- $D = 0$ → inconclusive.

**Eigenvalue view:** both eigenvalues of $H$ positive → min; both negative → max; mixed signs → saddle. This extends to any number of variables.

**Absolute extrema on a closed bounded region:** check critical points inside **and** the maximum/minimum on the boundary.

## 4. Formula & worked example
$$
H = \begin{pmatrix} f_{xx} & f_{xy} \\ f_{xy} & f_{yy}\end{pmatrix} \qquad D = f_{xx}f_{yy} - f_{xy}^2
$$

**Example 1.** $f(x, y) = x^2 + xy + y^2 - 3x$.
- $f_x = 2x + y - 3 = 0$, $f_y = x + 2y = 0$ → $x = 2$, $y = -1$.
- $f_{xx} = 2$, $f_{yy} = 2$, $f_{xy} = 1$ → $D = 3 > 0$, $f_{xx} > 0$ → local min $f(2, -1) = -3$.

**Example 2 (saddle).** $f(x, y) = x^2 - y^2$: critical point $(0, 0)$; $D = (2)(-2) - 0 = -4 < 0$ → saddle.

**Example 3 (several critical points).** $f(x, y) = x^3 - 3x + y^2$.
- $f_x = 3x^2 - 3 = 0$ → $x = \pm 1$; $f_y = 2y = 0$ → $y = 0$.
- $(1, 0)$: $D = 6x \cdot 2 = 12 > 0$, $f_{xx} = 6 > 0$ → local min. $(-1, 0)$: $D = -12 < 0$ → saddle.

**Example 4 (profit).** Selling two products: $P(x, y) = 50x + 60y - x^2 - xy - y^2$.
- $50 - 2x - y = 0$, $60 - x - 2y = 0$ → $x = \frac{40}{3}$, $y = \frac{70}{3}$; $D = 3 > 0$, $f_{xx} < 0$ → maximum profit.

**Example 5 (code).**
```python
import numpy as np
H = np.array([[2, 1], [1, 2]])          # Hessian from Example 1
print(np.linalg.eigvalsh(H))            # [1. 3.] both positive -> minimum
```
For symmetric Hessians `eigvalsh` returns real eigenvalues; their signs classify the critical point in any dimension.

## 5. Common mistakes
- Forgetting to solve $f_x = 0$ and $f_y = 0$ **simultaneously**.
- Concluding "minimum" from $f_{xx} > 0$ alone without checking $D$.
- Ignoring the boundary when looking for absolute extrema on a region.

## 6. Practice
Q: Condition for a critical point of $f(x, y)$?
A: $f_x = 0$ and $f_y = 0$ (or a partial is undefined).

Q: $D < 0$ at a critical point means?
A: A saddle point.

Q: Classify $(0, 0)$ for $f = x^2 + 3y^2$.
A: $D = 12 > 0$, $f_{xx} > 0$ → local minimum.

Q: Hessian eigenvalues 4 and $-1$. Classification?
A: Saddle point.

Q: Critical points of $f = xy$?
A: $(0, 0)$, a saddle ($D = -1$).

@@ 3 | TOPIC | Constrained optimisation and Lagrange multipliers | Optimising subject to g = c, ∇f = λ∇g, geometric meaning, economics and engineering examples | SHOULD
# Constrained optimisation and Lagrange multipliers

## 1. What it covers
- Optimising $f(x, y)$ **subject to a constraint** $g(x, y) = c$.
- The **Lagrange condition** $\nabla f = \lambda\nabla g$.
- Geometric meaning (tangent level curves) and the meaning of $\lambda$.
- Examples with budgets, materials and distances.

## 2. Where it is used
- **Economics:** maximise utility subject to a budget; minimise cost subject to a production target. $\lambda$ is the "shadow price".
- **Engineering:** maximise strength or volume with limited material.
- **Machine learning:** support vector machines and regularisation are constrained optimisation problems; entropy maximisation gives the softmax.
- **Physics:** equilibrium under constraints.

## 3. What it means
Imagine walking along a road (the constraint curve $g = c$) on a hilly landscape ($f$). The highest point along the road is where the road is **tangent** to a contour line of $f$: if it crossed the contour, you could keep going uphill.

Tangent curves have parallel normals, and normals are gradients. So at the optimum, $\nabla f$ is parallel to $\nabla g$:
$$
\nabla f = \lambda\nabla g
$$

**Method:** solve the system $f_x = \lambda g_x$, $f_y = \lambda g_y$, $g(x, y) = c$; evaluate $f$ at each solution and compare.

**Meaning of $\lambda$:** the rate at which the optimal value changes if the constraint is loosened: how much more utility one extra taka of budget buys.

## 4. Formula & worked example
$$
\nabla f(x, y) = \lambda\nabla g(x, y), \quad g(x, y) = c
$$

**Example 1 (fencing).** Maximise the area $A = xy$ of a rectangle with perimeter $2x + 2y = 40$.
- $\langle y, x\rangle = \lambda\langle 2, 2\rangle$ → $x = y$ → $x = y = 10$, $A = 100$ (a square, as expected).

**Example 2 (budget).** Utility $U = x^{0.5}y^{0.5}$; prices 10 and 20 taka; budget 400: $10x + 20y = 400$.
- $\frac{0.5y^{0.5}}{x^{0.5}} = 10\lambda$, $\frac{0.5x^{0.5}}{y^{0.5}} = 20\lambda$ → dividing: $\frac{y}{x} = \frac{1}{2}$ → $x = 2y$
- $20y + 20y = 400$ → $y = 10$, $x = 20$. Spend half the budget on each good.

**Example 3 (closest point).** Closest point on $x + 2y = 5$ to the origin: minimise $x^2 + y^2$.
- $\langle 2x, 2y\rangle = \lambda\langle 1, 2\rangle$ → $y = 2x$ → $5x = 5$ → $(1, 2)$, distance $\sqrt{5}$.

**Example 4 (box).** Maximise the volume $xyz$ of a box with surface area $2(xy + yz + xz) = 24$. By symmetry of the Lagrange equations, $x = y = z = 2$: a cube of volume 8.

**Example 5 (code).**
```python
from scipy.optimize import minimize
obj = lambda v: -(v[0]**0.5 * v[1]**0.5)                       # maximise utility = minimise -U
cons = {"type": "eq", "fun": lambda v: 10*v[0] + 20*v[1] - 400}
res = minimize(obj, x0=[10, 10], constraints=[cons], bounds=[(0, None)]*2)
print(res.x.round(3))                                          # [20. 10.]
```
`scipy.optimize.minimize` handles constraints numerically (internally using Lagrange-multiplier ideas), useful when the equations can't be solved by hand.

## 5. Common mistakes
- Forgetting to include the constraint equation itself in the system.
- Dividing by a variable that could be zero (losing solutions).
- Not comparing $f$ at all candidate points.

## 6. Practice
Q: Lagrange condition?
A: $\nabla f = \lambda\nabla g$ together with $g = c$.

Q: Geometric meaning at the optimum?
A: The level curve of $f$ is tangent to the constraint curve.

Q: Maximise $xy$ subject to $x + y = 10$.
A: $x = y = 5$, max 25.

Q: What does $\lambda$ represent in economics?
A: The shadow price: the change in the optimal value per unit relaxation of the constraint.

Q: Minimise $x^2 + y^2$ subject to $x + y = 4$.
A: $(2, 2)$, minimum 8.

@@ 3 | TOPIC | Gradient descent in several variables | Update rule, learning rate, contours and zig-zagging, momentum, stochastic gradient descent, code | SHOULD
# Gradient descent in several variables

## 1. What it covers
- The update $\vec{x}_{k+1} = \vec{x}_k - \eta\nabla f(\vec{x}_k)$ in many dimensions.
- Learning rate, convergence, and zig-zagging in narrow valleys.
- **Momentum** and the idea behind Adam.
- **Stochastic** (mini-batch) gradient descent.
- Plotting the descent path on a contour map.

## 2. Where it is used
- **Training neural networks:** every modern model (image classifiers, language models) is trained by stochastic gradient descent variants.
- **Linear and logistic regression** on big data.
- **Recommender systems, matrix factorisation.**
- **Engineering optimisation** when only gradients are available.

## 3. What it means
At each step, compute the gradient (the uphill direction) and take a small step **downhill**. Repeat until the gradient is nearly zero.

**Learning rate $\eta$:** too small → slow; too large → overshoot and diverge.

**Narrow valleys:** if the function is much steeper in one direction than another (elongated contours), the gradient points mostly across the valley, so plain gradient descent **zig-zags** slowly. Two fixes:
- **Momentum:** keep a running "velocity" that averages recent gradients, so oscillations cancel and progress along the valley accumulates, like a ball rolling downhill.
- **Adaptive methods** (Adam, RMSProp): scale each coordinate's step by its recent gradient size.

**Stochastic gradient descent (SGD):** with millions of training examples, estimate the gradient from a small random **mini-batch** each step. Noisier, but each step is far cheaper.

## 4. Formula & worked example
$$
\vec{x}_{k+1} = \vec{x}_k - \eta\nabla f(\vec{x}_k) \qquad \text{momentum: } \vec{v}_{k+1} = \beta\vec{v}_k + \nabla f(\vec{x}_k),\ \ \vec{x}_{k+1} = \vec{x}_k - \eta\vec{v}_{k+1}
$$

**Example 1 (by hand).** $f(x, y) = x^2 + 4y^2$, start $(4, 1)$, $\eta = 0.1$. $\nabla f = \langle 2x, 8y\rangle$.
- Step 1: $\nabla f = \langle 8, 8\rangle$ → $(4 - 0.8, 1 - 0.8) = (3.2, 0.2)$
- Step 2: $\nabla f = \langle 6.4, 1.6\rangle$ → $(2.56, 0.04)$ ... → $(0, 0)$. The $y$ coordinate (steep direction) shrinks much faster than $x$.

**Example 2 (code: plain vs momentum on a narrow valley).**
```python
import numpy as np
grad = lambda p: np.array([2 * p[0], 50 * p[1]])     # f = x^2 + 25 y^2 (narrow valley)

def gd(lr, steps=30, beta=0.0):
    p, v = np.array([10.0, 1.0]), np.zeros(2)
    for _ in range(steps):
        v = beta * v + grad(p)
        p = p - lr * v
    return p
print(gd(0.035))              # plain:    [1.13 0.0002]  x still far from 0 after 30 steps
print(gd(0.035, beta=0.5))    # momentum: [0.04 0.0000]  about 28x closer
```
With a learning rate small enough to be stable in the steep $y$ direction, plain gradient descent creeps along the shallow $x$ direction. Momentum accumulates speed along the valley floor and reaches the minimum much faster.

**Example 3 (linear regression with mini-batch SGD).**
```python
rng = np.random.default_rng(0)
X = np.column_stack([np.ones(10_000), rng.normal(size=10_000)])
y = X @ np.array([2.0, -3.0]) + rng.normal(0, 0.5, 10_000)
w = np.zeros(2)
for step in range(2000):
    idx = rng.integers(0, len(y), 64)                     # random mini-batch of 64 rows
    g = 2 / 64 * X[idx].T @ (X[idx] @ w - y[idx])
    w -= 0.05 * g
print(w.round(2))                                         # ≈ [ 2. -3.]
```
Each step uses only 64 of the 10 000 rows, yet the weights converge to the true values: the same principle trains large neural networks on huge datasets.

## 5. Common mistakes
- Learning rate too large (divergence) or too small (no progress).
- Forgetting to scale features, which creates narrow valleys and slow convergence.
- Expecting gradient descent to find the global minimum of a non-convex function.

## 6. Practice
Q: Gradient descent update rule?
A: $\vec{x} \leftarrow \vec{x} - \eta\nabla f(\vec{x})$.

Q: Why does gradient descent zig-zag in narrow valleys?
A: The gradient points mostly across the valley rather than along it.

Q: What does momentum do?
A: Averages recent gradients so oscillations cancel and progress along consistent directions builds up.

Q: What is a mini-batch?
A: A small random subset of the training data used to estimate the gradient at each step.

Q: Why scale features before gradient descent?
A: To make the loss contours rounder, so convergence is faster.

@@ 3 | TOPIC | Practice & review: Multivariable optimisation | Mixed problem set with worked answers | SHOULD
# Practice & review: Multivariable optimisation

**How to use this:** 40 minutes; check with SciPy.

## Problems
1. Find and classify the critical points of $f(x, y) = x^2 + y^3 - 3y$.
2. Find and classify the critical points of $f(x, y) = xy - x^2 - y^2 + 3x$.
3. A rectangular box without a lid must hold 32 m³. Find the dimensions that minimise the surface area (use the constraint to eliminate a variable, then critical points).
4. Use Lagrange multipliers to maximise $f(x, y) = x + 2y$ on the circle $x^2 + y^2 = 5$.
5. Minimise the cost $C = 4x + 9y$ subject to producing $xy = 100$ units.
6. Do two gradient descent steps on $f(x, y) = (x - 1)^2 + 2(y + 2)^2$ from $(0, 0)$ with $\eta = 0.2$.

## Answers
1. $f_x = 2x = 0$, $f_y = 3y^2 - 3 = 0$ → $(0, 1)$, $(0, -1)$. $D = 2 \cdot 6y$: at $(0, 1)$ $D = 12 > 0$, $f_{xx} > 0$ → min; at $(0, -1)$ $D < 0$ → saddle.
2. $y - 2x + 3 = 0$, $x - 2y = 0$ → $x = 2$, $y = 1$; $D = (-2)(-2) - 1 = 3 > 0$, $f_{xx} < 0$ → local max $f = 3$.
3. $S = xy + \frac{64}{y} + \frac{64}{x}$ (with $z = \frac{32}{xy}$); $S_x = y - \frac{64}{x^2} = 0$, $S_y = x - \frac{64}{y^2} = 0$ → $x = y = 4$, $z = 2$.
4. $\langle 1, 2\rangle = \lambda\langle 2x, 2y\rangle$ → $y = 2x$ → $5x^2 = 5$ → max at $(1, 2)$, value 5.
5. $\langle 4, 9\rangle = \lambda\langle y, x\rangle$ → $4x = 9y$ → $\frac{9}{4}y^2 = 100$ → $y = \frac{20}{3}$, $x = 15$, $C = 120$.
6. $\nabla f = \langle 2(x - 1), 4(y + 2)\rangle$. Step 1: $\langle -2, 8\rangle$ → $(0.4, -1.6)$. Step 2: $\langle -1.2, 1.6\rangle$ → $(0.64, -1.92)$.

## Flashcards
Q: Second derivative test discriminant?
A: $D = f_{xx}f_{yy} - f_{xy}^2$.

Q: Lagrange multiplier condition?
A: $\nabla f = \lambda\nabla g$.

Q: Saddle point?
A: A critical point that is a max in one direction and a min in another ($D < 0$).

Q: Why use momentum in gradient descent?
A: To speed up progress and reduce zig-zagging in narrow valleys.
