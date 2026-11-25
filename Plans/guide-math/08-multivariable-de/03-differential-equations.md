@@ 1 | MODULE | Phase 6 — Differential Equations (Advanced) | Classification, linear and exact first-order equations, modelling and numerical methods, second-order linear equations, vibrations, systems, Laplace transforms | SHOULD
# Phase 6: Differential equations

A **differential equation** (DE) describes a system by how it **changes**: the rate of cooling depends on the temperature difference; a spring's acceleration depends on its stretch; current in a circuit depends on voltage and charge. Solving the DE predicts the system's future.

**Before starting:** the Calculus roadmap's DE introduction (slope fields, Euler's method, separable equations, exponential and logistic models) and the Linear Algebra roadmap's eigenvalues.

@@ 2 | MODULE | First-order equations | Classification, linear equations with integrating factors, exact equations, modelling and numerical methods | SHOULD
First-order equations involve only the first derivative: one rate, one rule.

@@ 3 | TOPIC | Classifying DEs and first-order linear equations | Order, linearity, ODE vs PDE, initial value problems, integrating factors for y′ + p(x)y = q(x) | SHOULD
# Classifying DEs and first-order linear equations

## 1. What it covers
- Classification: **order**, **linear vs non-linear**, **ODE vs PDE**, homogeneous vs non-homogeneous.
- **Initial value problems** (IVPs).
- Solving **first-order linear** equations $y' + p(x)y = q(x)$ with an **integrating factor**.

## 2. Where it is used
- **Electrical circuits:** an RC or RL circuit is a first-order linear DE.
- **Mixing problems:** salt concentration in a tank with inflow and outflow.
- **Pharmacology:** drug level with continuous infusion and elimination.
- **Finance:** a balance with interest and continuous deposits/withdrawals.

## 3. What it means
**Classification:**
- **Order:** the highest derivative. $y'' + y = 0$ is second order.
- **Linear:** $y$ and its derivatives appear only to the first power, not multiplied together or inside functions. $y' + xy = \sin x$ is linear; $y' = y^2$ is not.
- **ODE:** one independent variable; **PDE:** several (partial derivatives), like the heat equation.

**Integrating factor:** for $y' + p(x)y = q(x)$, multiply both sides by $\mu(x) = e^{\int p\,dx}$. This makes the left side the derivative of a product, $(\mu y)'$, by the product rule. Then just integrate both sides.

## 4. Formula & worked example
$$
y' + p(x)y = q(x) \qquad \mu(x) = e^{\int p(x)\,dx} \qquad (\mu y)' = \mu q \Rightarrow y = \frac{1}{\mu}\left(\int\mu q\,dx + C\right)
$$

**Example 1 (classify).** $y''' + 2y' = e^x$: third order, linear, non-homogeneous ODE. $y' = \sin y$: first order, non-linear.

**Example 2.** $y' + 2y = 4$, $y(0) = 0$.
- $\mu = e^{2x}$ → $(e^{2x}y)' = 4e^{2x}$ → $e^{2x}y = 2e^{2x} + C$ → $y = 2 + Ce^{-2x}$; $y(0) = 0$ → $C = -2$ → $y = 2 - 2e^{-2x}$ (rises to 2).

**Example 3.** $y' + \frac{1}{x}y = x$ ($x > 0$): $\mu = x$ → $(xy)' = x^2$ → $xy = \frac{x^3}{3} + C$ → $y = \frac{x^2}{3} + \frac{C}{x}$.

**Example 4 (mixing tank).** A 100 L tank of pure water; brine with 0.5 kg salt/L flows in at 4 L/min; mixed solution flows out at 4 L/min. Salt $S(t)$?
- $S' = 4(0.5) - 4\frac{S}{100} = 2 - 0.04S$ → $S' + 0.04S = 2$ → $S = 50(1 - e^{-0.04t})$ → approaches 50 kg.

**Example 5 (RC circuit).** $RC\,V' + V = V_s$ (charging capacitor): $V(t) = V_s(1 - e^{-t/RC})$; the time constant $RC$ is the time to reach about 63%.

## 5. Common mistakes
- Forgetting to put the equation in standard form (coefficient of $y'$ must be 1) before finding $\mu$.
- Forgetting $+C$ before dividing by $\mu$.
- Calling $y' = xy^2$ linear.

## 6. Practice
Q: Order and linearity of $x^2y'' + xy' + y = 0$?
A: Second order, linear.

Q: Integrating factor for $y' + 3y = e^x$?
A: $e^{3x}$.

Q: Solve $y' + y = 1$, $y(0) = 0$.
A: $y = 1 - e^{-x}$.

Q: Is $y' = \cos y$ linear?
A: No.

Q: Why does the integrating factor work?
A: It turns the left side into the derivative of $\mu y$ (product rule).

@@ 3 | TOPIC | Exact equations | M dx + N dy = 0, the exactness test M_y = N_x, finding the potential function, link to conservative fields | SHOULD
# Exact equations

## 1. What it covers
- Equations of the form $M(x, y)\,dx + N(x, y)\,dy = 0$.
- The **exactness test** $M_y = N_x$.
- Finding a potential $\psi(x, y)$ with $\psi = C$ as the solution.
- The link with conservative vector fields.

## 2. Where it is used
- **Thermodynamics:** exact differentials represent state functions (energy, entropy).
- **Physics:** conservative force fields and potentials.
- **Solving non-linear first-order equations** that aren't separable or linear.

## 3. What it means
If $M\,dx + N\,dy$ is the total differential of some function $\psi$ (that is, $\psi_x = M$ and $\psi_y = N$), then the equation says $d\psi = 0$, so $\psi(x, y) = C$: the solutions are the **level curves** of $\psi$.

This is exactly the conservative-field test from Phase 5: $\langle M, N\rangle$ is a gradient field iff $M_y = N_x$.

**Finding $\psi$:** integrate $M$ with respect to $x$ (adding $h(y)$), then choose $h$ so that $\psi_y = N$.

## 4. Formula & worked example
$$
M\,dx + N\,dy = 0 \text{ is exact} \iff M_y = N_x \qquad \psi_x = M,\ \psi_y = N,\ \psi(x, y) = C
$$

**Example 1.** $(2xy + 3)\,dx + (x^2 + 4y)\,dy = 0$.
- $M_y = 2x = N_x$ ✓ exact.
- $\psi = \int(2xy + 3)\,dx = x^2y + 3x + h(y)$; $\psi_y = x^2 + h'(y) = x^2 + 4y$ → $h = 2y^2$.
- Solution: $x^2y + 3x + 2y^2 = C$.

**Example 2.** $(y\cos x + 2x)\,dx + (\sin x - 1)\,dy = 0$, $y(0) = 3$.
- $M_y = \cos x = N_x$ ✓; $\psi = y\sin x + x^2 - y$ → $y\sin x + x^2 - y = C$; at $(0, 3)$: $C = -3$.

**Example 3 (not exact).** $y\,dx - x\,dy = 0$: $M_y = 1$, $N_x = -1$ → not exact (it can be made exact with an integrating factor, e.g. $\frac{1}{y^2}$, giving $\frac{x}{y} = C$).

## 5. Common mistakes
- Testing $M_x = N_y$ instead of $M_y = N_x$.
- Forgetting the function $h(y)$ when integrating with respect to $x$.
- Writing the answer as $\psi$ instead of $\psi = C$.

## 6. Practice
Q: Exactness test?
A: $M_y = N_x$.

Q: Is $2x\,dx + 2y\,dy = 0$ exact? Solution?
A: Yes; $x^2 + y^2 = C$ (circles).

Q: Is $(x + y)\,dx + (x - y)\,dy = 0$ exact?
A: Yes ($M_y = 1 = N_x$); $\frac{x^2}{2} + xy - \frac{y^2}{2} = C$.

Q: What are the solution curves of an exact equation?
A: Level curves $\psi(x, y) = C$ of the potential.

Q: How are exact equations related to vector calculus?
A: $\langle M, N\rangle$ is a conservative field with potential $\psi$.

@@ 3 | TOPIC | Modelling with first-order DEs and numerical solvers | Cooling, mixing, circuits, populations; Runge–Kutta and scipy's solve_ivp | SHOULD
# Modelling with first-order DEs and numerical solvers

## 1. What it covers
- Building DE models from words (rate in − rate out; proportional rates).
- Classic models: cooling, mixing, circuits, drug infusion, populations with harvesting.
- **Numerical solution**: why Euler's method is improved by **Runge–Kutta (RK4)**; using `scipy.integrate.solve_ivp`.

## 2. Where it is used
- **Engineering simulation:** most real DEs have no formula solution and are solved numerically.
- **Epidemiology:** SIR models of disease spread.
- **Games and physics engines:** integrating motion every frame.
- **Pharmacology, ecology, economics:** compartment models.

## 3. What it means
**Modelling recipe:**
1. Identify the quantity that changes, $y(t)$.
2. Write its rate of change as (rate in) − (rate out), or as proportional to something.
3. Add the initial condition.
4. Solve exactly if possible; otherwise numerically.

**Numerical methods:** Euler's method follows the slope at the start of each step, which drifts off curving solutions. **RK4** samples the slope four times per step (start, two midpoints, end) and averages them cleverly, giving far better accuracy for the same step size. Library solvers like `solve_ivp` also adapt the step size automatically.

## 4. Formula & worked example
$$
\text{RK4: } y_{n+1} = y_n + \frac{h}{6}(k_1 + 2k_2 + 2k_3 + k_4)
$$
with $k_1 = f(t_n, y_n)$, $k_2 = f(t_n + \frac{h}{2}, y_n + \frac{h}{2}k_1)$, $k_3 = f(t_n + \frac{h}{2}, y_n + \frac{h}{2}k_2)$, $k_4 = f(t_n + h, y_n + hk_3)$.

**Example 1 (harvesting).** Fish grow logistically with harvesting: $P' = 0.5P\left(1 - \frac{P}{1000}\right) - 100$. Equilibria where $P' = 0$: $0.5P - 0.0005P^2 = 100$ → $P \approx 276$ (unstable) and $P \approx 724$ (stable). Below 276 the population collapses: a real fishery-management insight.

**Example 2 (drug infusion).** IV drip at 10 mg/h, elimination rate 0.2 per hour: $A' = 10 - 0.2A$ → $A(t) = 50(1 - e^{-0.2t})$; steady level 50 mg.

**Example 3 (Euler vs RK4 vs solve_ivp, code).**
```python
import numpy as np
from scipy.integrate import solve_ivp
f = lambda t, y: y                       # y' = y, y(0) = 1, exact y(1) = e
def euler(h):
    y = 1.0
    for _ in range(int(1 / h)): y += h * f(0, y)
    return y
def rk4(h):
    y, t = 1.0, 0.0
    for _ in range(int(1 / h)):
        k1 = f(t, y); k2 = f(t + h/2, y + h/2*k1); k3 = f(t + h/2, y + h/2*k2); k4 = f(t + h, y + h*k3)
        y += h / 6 * (k1 + 2*k2 + 2*k3 + k4); t += h
    return y
print(abs(euler(0.1) - np.e), abs(rk4(0.1) - np.e))    # ~0.12 vs ~2e-6
sol = solve_ivp(f, (0, 1), [1.0], rtol=1e-9)
print(sol.y[0, -1])                                     # 2.718281828...
```
With the same 10 steps, RK4 is about 50 000 times more accurate than Euler. `solve_ivp` (default RK45) chooses step sizes automatically to meet the requested tolerance.

**Example 4 (SIR epidemic, code).**
```python
def sir(t, u, beta=0.3, gamma=0.1):
    S, I, R = u
    return [-beta*S*I, beta*S*I - gamma*I, gamma*I]
sol = solve_ivp(sir, (0, 160), [0.99, 0.01, 0.0], dense_output=True)
t = np.linspace(0, 160, 161); S, I, R = sol.sol(t)
print(t[I.argmax()], I.max().round(3))                  # peak day and peak infected fraction
```
Susceptible people become infected at rate $\beta SI$ and recover at rate $\gamma I$. This three-equation system has no simple formula, but a numerical solver gives the epidemic curve, its peak and the final size.

## 5. Common mistakes
- Mixing units (per hour vs per minute) in rates.
- Forgetting outflow terms in mixing problems.
- Trusting a numerical solution without checking step size or tolerance.

## 6. Practice
Q: Basic modelling principle for compartments?
A: Rate of change = rate in − rate out.

Q: $A' = 6 - 0.3A$. Long-run value of $A$?
A: $20$.

Q: Why is RK4 more accurate than Euler?
A: It averages several slope samples within each step.

Q: Which scipy function solves IVPs numerically?
A: `scipy.integrate.solve_ivp`.

Q: What do the three equations of the SIR model track?
A: Susceptible, infected and recovered fractions of the population.

@@ 2 | MODULE | Second-order linear equations and systems | Constant-coefficient homogeneous equations, vibrations, undetermined coefficients, variation of parameters, systems via eigenvalues | NICE
Second-order equations describe anything with inertia: springs, pendulums, circuits with inductors.

@@ 3 | TOPIC | Homogeneous second-order equations with constant coefficients | ay″ + by′ + cy = 0, characteristic equation, distinct real, repeated and complex roots | NICE
# Homogeneous second-order equations with constant coefficients

## 1. What it covers
- Equations $ay'' + by' + cy = 0$.
- The **characteristic equation** $ar^2 + br + c = 0$.
- Three cases: **distinct real roots**, **repeated root**, **complex roots**.
- Using initial conditions $y(0)$, $y'(0)$.

## 2. Where it is used
- **Mechanical vibrations:** springs, car suspensions, building sway.
- **Electrical circuits:** RLC circuits (radios, filters).
- **Control systems:** the response of a system to a disturbance (overshoot, settling).
- **Acoustics:** vibrating strings and membranes (after separating variables).

## 3. What it means
Guess $y = e^{rx}$ (exponentials keep their shape when differentiated). Substituting gives $e^{rx}(ar^2 + br + c) = 0$, so $r$ must solve the **characteristic equation**. Its roots decide the behaviour:
- **Two real roots** $r_1 \ne r_2$: $y = C_1e^{r_1x} + C_2e^{r_2x}$ (growth/decay, no oscillation).
- **Repeated root** $r$: $y = (C_1 + C_2x)e^{rx}$ (the extra $x$ is needed for a second independent solution).
- **Complex roots** $\alpha \pm \beta i$: $y = e^{\alpha x}(C_1\cos\beta x + C_2\sin\beta x)$: **oscillation** with frequency $\beta$, growing or decaying by $e^{\alpha x}$ (Euler's formula turns complex exponentials into sines and cosines).

Two initial conditions fix the two constants.

## 4. Formula & worked example
$$
ay'' + by' + cy = 0 \Rightarrow ar^2 + br + c = 0
$$

**Example 1 (distinct real).** $y'' - 5y' + 6y = 0$: $r^2 - 5r + 6 = 0$ → $r = 2, 3$ → $y = C_1e^{2x} + C_2e^{3x}$.

**Example 2 (with ICs).** $y'' - y = 0$, $y(0) = 2$, $y'(0) = 0$: $r = \pm 1$ → $y = C_1e^x + C_2e^{-x}$; $C_1 + C_2 = 2$, $C_1 - C_2 = 0$ → $y = e^x + e^{-x} = 2\cosh x$.

**Example 3 (repeated).** $y'' + 4y' + 4y = 0$: $(r + 2)^2 = 0$ → $y = (C_1 + C_2x)e^{-2x}$.

**Example 4 (complex).** $y'' + 2y' + 5y = 0$: $r = -1 \pm 2i$ → $y = e^{-x}(C_1\cos 2x + C_2\sin 2x)$: a decaying oscillation.

**Example 5 (pure oscillation).** $y'' + 9y = 0$, $y(0) = 1$, $y'(0) = 0$: $r = \pm 3i$ → $y = \cos 3x$.

## 5. Common mistakes
- Forgetting the extra $x$ for a repeated root.
- Writing $e^{\beta x}$ instead of $e^{\alpha x}$ in the complex case (the real part gives the exponential).
- Using one initial condition to find both constants.

## 6. Practice
Q: Characteristic equation of $y'' + 3y' - 4y = 0$ and solution?
A: $r^2 + 3r - 4 = 0$ → $r = 1, -4$ → $C_1e^x + C_2e^{-4x}$.

Q: Solution of $y'' - 6y' + 9y = 0$?
A: $(C_1 + C_2x)e^{3x}$.

Q: Solution of $y'' + 16y = 0$?
A: $C_1\cos 4x + C_2\sin 4x$.

Q: What kind of roots produce oscillation?
A: Complex roots.

Q: Solve $y'' + y = 0$, $y(0) = 0$, $y'(0) = 2$.
A: $y = 2\sin x$.

@@ 3 | TOPIC | Mechanical vibrations: springs, damping and resonance | m x″ + c x′ + k x = F(t), undamped, under-, critically and over-damped motion, resonance | NICE
# Mechanical vibrations: springs, damping and resonance

## 1. What it covers
- The spring–mass–damper equation $mx'' + cx' + kx = F(t)$.
- **Natural frequency** $\omega_0 = \sqrt{k/m}$.
- **Underdamped**, **critically damped** and **overdamped** motion.
- **Forced** vibrations and **resonance**.

## 2. Where it is used
- **Automotive engineering:** shock absorbers are tuned near critical damping for a smooth ride.
- **Civil engineering:** buildings and bridges must avoid resonance with wind and earthquakes (the Tacoma Narrows bridge collapse, 1940, and London's wobbly Millennium Bridge, 2000).
- **Electronics:** RLC circuits obey the same equation ($L$, $R$, $\frac{1}{C}$ play the roles of $m$, $c$, $k$); radios tune by resonance.
- **Consumer tech:** phone vibration motors, hard-drive and camera stabilisation; UI spring animations.

## 3. What it means
- $m$: mass (inertia), $c$: damping (friction, shock absorber), $k$: spring stiffness, $F(t)$: external force.
- **No damping, no force:** endless oscillation at the natural frequency $\omega_0 = \sqrt{k/m}$.
- **With damping**, compare $c^2$ with $4mk$ (the discriminant of the characteristic equation):
  - $c^2 < 4mk$: **underdamped**: oscillates while decaying (a bouncy car).
  - $c^2 = 4mk$: **critically damped**: returns to rest fastest without oscillating (ideal door closer or shock absorber).
  - $c^2 > 4mk$: **overdamped**: returns slowly without oscillating (moving through honey).
- **Resonance:** pushing a system at its natural frequency makes the amplitude grow (unboundedly without damping, very large with light damping), like pushing a child on a swing at just the right moments.

## 4. Formula & worked example
$$
mx'' + cx' + kx = F(t) \qquad \omega_0 = \sqrt{\frac{k}{m}} \qquad \text{critical damping: } c = 2\sqrt{mk}
$$

**Example 1 (undamped).** $m = 1$ kg, $k = 16$ N/m, pulled 0.1 m and released: $x'' + 16x = 0$ → $x = 0.1\cos 4t$; period $\frac{2\pi}{4} \approx 1.57$ s.

**Example 2 (damping types).** $m = 1$, $k = 16$: critical damping $c = 8$.
- $c = 2$: $r^2 + 2r + 16 = 0$ → $r = -1 \pm i\sqrt{15}$ → underdamped (decaying oscillation).
- $c = 8$: $r = -4$ (repeated) → critically damped.
- $c = 10$: $r = -2, -8$ → overdamped.

**Example 3 (resonance).** $x'' + 16x = \cos(\omega t)$. For $\omega \ne 4$, the response has amplitude $\frac{1}{\lvert 16 - \omega^2\rvert}$, which blows up as $\omega \to 4$. At $\omega = 4$: $x_p = \frac{t}{8}\sin 4t$, growing without bound.

**Example 4 (code: damping comparison).**
```python
import numpy as np
from scipy.integrate import solve_ivp
def spring(t, u, c):                  # x'' + c x' + 16 x = 0 as a first-order system
    x, v = u
    return [v, -c*v - 16*x]
for c in (2, 8, 10):
    sol = solve_ivp(spring, (0, 3), [0.1, 0], args=(c,), dense_output=True)
    print(c, sol.sol(np.array([0.5, 1.0, 2.0]))[0].round(4))
# c=2 oscillates (sign changes), c=8 returns fastest, c=10 returns slowly
```
A second-order equation is solved numerically by rewriting it as two first-order equations (position and velocity), the standard trick in every physics engine.

## 5. Common mistakes
- Mixing up which inequality means underdamped (oscillation needs **small** damping, $c^2 < 4mk$).
- Forgetting that frequency in rad/s ($\omega$) differs from Hz ($f = \frac{\omega}{2\pi}$).
- Ignoring units ($k$ in N/m, $m$ in kg).

## 6. Practice
Q: Natural frequency of $m = 4$ kg, $k = 100$ N/m?
A: $\omega_0 = 5$ rad/s.

Q: Critical damping coefficient for $m = 2$, $k = 50$?
A: $c = 2\sqrt{100} = 20$.

Q: Characteristic roots $-1 \pm 3i$. Damping type?
A: Underdamped.

Q: What is resonance?
A: Large amplitude growth when a system is forced at its natural frequency.

Q: How is a second-order ODE solved numerically?
A: Rewrite it as a system of two first-order equations (position and velocity).

@@ 3 | TOPIC | Non-homogeneous equations: undetermined coefficients and variation of parameters | General = homogeneous + particular, guessing particular solutions, the variation of parameters formula | NICE
# Non-homogeneous equations: undetermined coefficients and variation of parameters

## 1. What it covers
- Structure: **general solution = homogeneous solution + one particular solution**.
- **Undetermined coefficients**: guess the form of $y_p$ for polynomial, exponential, sine/cosine forcing.
- The modification rule when the guess duplicates a homogeneous solution.
- **Variation of parameters**: a general formula for any forcing.

## 2. Where it is used
- **Forced vibrations:** a motor shaking a structure, road bumps on a car.
- **AC circuits:** response of an RLC circuit to a sinusoidal voltage (steady-state + transient).
- **Control engineering:** system response to step and ramp inputs.

## 3. What it means
For $ay'' + by' + cy = g(x)$:
- The **homogeneous** part $y_h$ (from the characteristic equation) describes the system's natural behaviour; it often decays (the transient).
- A **particular** solution $y_p$ responds to the forcing $g(x)$; for periodic forcing it's the steady state.
- Every solution is $y = y_h + y_p$.

**Undetermined coefficients:** the forcing's "shape" survives differentiation, so guess the same shape with unknown coefficients:
| $g(x)$ | Guess for $y_p$ |
|---|---|
| polynomial of degree $n$ | general polynomial of degree $n$ |
| $e^{kx}$ | $Ae^{kx}$ |
| $\sin\omega x$ or $\cos\omega x$ | $A\cos\omega x + B\sin\omega x$ |
If the guess solves the homogeneous equation, multiply it by $x$ (or $x^2$).

**Variation of parameters:** works for any $g(x)$ (like $\tan x$ or $\frac{1}{x}$), using the homogeneous solutions $y_1$, $y_2$ and their Wronskian.

## 4. Formula & worked example
$$
y = y_h + y_p \qquad \text{VoP: } y_p = -y_1\int\frac{y_2g}{aW}\,dx + y_2\int\frac{y_1g}{aW}\,dx,\quad W = y_1y_2' - y_1'y_2
$$

**Example 1 (polynomial).** $y'' - 3y' + 2y = 4x$.
- $y_h = C_1e^x + C_2e^{2x}$. Guess $y_p = Ax + B$: $-3A + 2Ax + 2B = 4x$ → $A = 2$, $B = 3$.
- $y = C_1e^x + C_2e^{2x} + 2x + 3$.

**Example 2 (exponential).** $y'' + y = 3e^{2x}$: guess $Ae^{2x}$ → $5A = 3$ → $y_p = \frac{3}{5}e^{2x}$; $y = C_1\cos x + C_2\sin x + \frac{3}{5}e^{2x}$.

**Example 3 (sinusoidal, steady state).** $y'' + 2y' + 5y = 10\cos x$: guess $A\cos x + B\sin x$:
- $(-A + 2B + 5A)\cos x + (-B - 2A + 5B)\sin x = 10\cos x$ → $4A + 2B = 10$, $4B - 2A = 0$ → $A = 2$, $B = 1$.
- Steady state $y_p = 2\cos x + \sin x$; the homogeneous part $e^{-x}(\ldots)$ dies out.

**Example 4 (modification rule).** $y'' - y = e^x$: $e^x$ solves the homogeneous equation, so guess $Axe^x$ → $2Ae^x = e^x$ → $y_p = \frac{1}{2}xe^x$.

**Example 5 (variation of parameters).** $y'' + y = \sec x$: $y_1 = \cos x$, $y_2 = \sin x$, $W = 1$ → $y_p = -\cos x\int\sin x\sec x\,dx + \sin x\int\cos x\sec x\,dx = \cos x\ln\lvert\cos x\rvert + x\sin x$.

## 5. Common mistakes
- Guessing only $A\cos\omega x$ for cosine forcing (you need both sine and cosine).
- Forgetting the modification rule when the guess overlaps $y_h$.
- Applying initial conditions to $y_h$ alone instead of to $y_h + y_p$.

## 6. Practice
Q: Structure of the general solution of a linear non-homogeneous DE?
A: $y = y_h + y_p$.

Q: Guess for $y_p$ when $g(x) = 5e^{3x}$ (and 3 is not a characteristic root)?
A: $Ae^{3x}$.

Q: Guess for $g(x) = \sin 2x$?
A: $A\cos 2x + B\sin 2x$.

Q: $y'' - 4y = 8$. Particular solution?
A: $y_p = -2$.

Q: When do you use variation of parameters?
A: When the forcing isn't a polynomial, exponential or sine/cosine (or combinations).

@@ 3 | TOPIC | Systems of linear differential equations and eigenvalues | x′ = Ax, solutions from eigenvalues and eigenvectors, phase portraits, stability | NICE
# Systems of linear differential equations and eigenvalues

## 1. What it covers
- Systems $\vec{x}' = A\vec{x}$.
- Solutions $\vec{x} = c_1e^{\lambda_1t}\vec{v}_1 + c_2e^{\lambda_2t}\vec{v}_2$ from eigenvalues and eigenvectors.
- **Phase portraits**: nodes, saddles, spirals, centres.
- **Stability** from the signs of the eigenvalues' real parts.

## 2. Where it is used
- **Ecology:** predator–prey and competing species (near equilibria).
- **Engineering:** coupled oscillators, multi-loop circuits, control-system stability.
- **Economics:** interacting markets.
- **Pharmacokinetics:** drug moving between blood and tissue compartments.
- **Converting higher-order ODEs:** any $n$th-order linear ODE becomes an $n \times n$ system.

## 3. What it means
Several quantities change together, each rate depending linearly on all of them. Guess $\vec{x} = e^{\lambda t}\vec{v}$: substituting gives $A\vec{v} = \lambda\vec{v}$, an **eigenvalue problem** (Linear Algebra Phase 5). Along each eigenvector direction, the system just grows or decays exponentially at rate $\lambda$; the general solution combines them.

**Stability of the equilibrium $\vec{0}$:**
- All eigenvalues with **negative** real parts → **stable** (everything decays to 0).
- Any eigenvalue with a **positive** real part → **unstable**.
- Real eigenvalues of opposite signs → **saddle** (unstable).
- Complex eigenvalues → **spirals** (decaying if the real part is negative); purely imaginary → **centre** (closed orbits).

## 4. Formula & worked example
$$
\vec{x}' = A\vec{x} \Rightarrow \vec{x}(t) = c_1e^{\lambda_1 t}\vec{v}_1 + c_2e^{\lambda_2 t}\vec{v}_2
$$

**Example 1.** $A = \begin{pmatrix} 4 & 1 \\ 2 & 3\end{pmatrix}$ (eigenvalues 5 and 2 with eigenvectors $\langle 1, 1\rangle$ and $\langle 1, -2\rangle$ from Linear Algebra):
- $\vec{x} = c_1e^{5t}\langle 1, 1\rangle + c_2e^{2t}\langle 1, -2\rangle$; both positive → unstable node.

**Example 2 (saddle).** $A = \begin{pmatrix} 1 & 0 \\ 0 & -2\end{pmatrix}$: $\lambda = 1, -2$ → saddle: trajectories approach along the y-axis, then escape along the x-axis.

**Example 3 (spiral).** $A = \begin{pmatrix} -1 & -2 \\ 2 & -1\end{pmatrix}$: $\lambda = -1 \pm 2i$ → stable spiral (decaying rotation).

**Example 4 (spring as a system).** $x'' + 16x = 0$ with $v = x'$: $\begin{pmatrix} x \\ v\end{pmatrix}' = \begin{pmatrix} 0 & 1 \\ -16 & 0\end{pmatrix}\begin{pmatrix} x \\ v\end{pmatrix}$, eigenvalues $\pm 4i$ → a centre (endless oscillation), matching the earlier topic.

**Example 5 (code: matrix exponential).**
```python
import numpy as np
from scipy.linalg import expm
A = np.array([[-1, -2], [2, -1.]])
x0 = np.array([1.0, 0.0])
for t in (0, 1, 2, 4):
    print(t, (expm(A * t) @ x0).round(4))    # spirals in towards the origin
print(np.linalg.eigvals(A))                   # [-1.+2.j -1.-2.j]
```
The exact solution is $\vec{x}(t) = e^{At}\vec{x}_0$, using the **matrix exponential** (computed by `expm`). The eigenvalues $-1 \pm 2i$ predict the decaying spiral the numbers show.

## 5. Common mistakes
- Forgetting that each eigenvalue needs its own eigenvector in the solution.
- Judging stability by the eigenvalues' size instead of the sign of their real parts.
- Treating complex eigenvalues as "no solution" (they give oscillations).

## 6. Practice
Q: Solution form of $\vec{x}' = A\vec{x}$ with distinct real eigenvalues?
A: $c_1e^{\lambda_1t}\vec{v}_1 + c_2e^{\lambda_2t}\vec{v}_2$.

Q: Eigenvalues $-3$ and $-1$. Stability?
A: Stable node.

Q: Eigenvalues $2$ and $-5$. Type?
A: Saddle (unstable).

Q: Eigenvalues $\pm 3i$. Phase portrait?
A: A centre (closed orbits).

Q: Convert $y'' + 3y' + 2y = 0$ into a system.
A: With $v = y'$: $y' = v$, $v' = -2y - 3v$.

@@ 2 | MODULE | The Laplace transform | Definition and table, properties and inverse transforms, solving IVPs, step functions and impulses | NICE
The Laplace transform turns differential equations into algebra.

@@ 3 | TOPIC | The Laplace transform: definition, table and properties | L{f} = ∫ e^(−st) f(t) dt, common transforms, linearity, derivatives, shifting | NICE
# The Laplace transform: definition, table and properties

## 1. What it covers
- The definition $\mathcal{L}\{f(t)\} = F(s) = \int_0^\infty e^{-st}f(t)\,dt$.
- A table of common transforms.
- Properties: **linearity**, transforms of **derivatives**, **shifting** ($e^{at}f(t)$).

## 2. Where it is used
- **Control engineering:** transfer functions $H(s)$ describe systems (cruise control, autopilots, robot arms, audio filters); stability is read from the poles of $H(s)$.
- **Electrical engineering:** circuit analysis in the "s-domain".
- **Signal processing:** closely related to the Fourier and Z-transforms used in digital filters.
- **Solving linear ODEs** with discontinuous or impulsive forcing.

## 3. What it means
The Laplace transform converts a function of time $t$ into a function of a new variable $s$. Its magic property: **differentiation in $t$ becomes multiplication by $s$** (plus initial values). So a linear differential equation becomes an **algebraic** equation in $s$, which is solved by ordinary algebra, then transformed back.

Think of it like logarithms, which turn multiplication into addition: transform, do the easy operation, transform back.

## 4. Formula & worked example
| $f(t)$ | $F(s)$ |
|---|---|
| $1$ | $\frac{1}{s}$ |
| $t^n$ | $\frac{n!}{s^{n+1}}$ |
| $e^{at}$ | $\frac{1}{s - a}$ |
| $\sin bt$ | $\frac{b}{s^2 + b^2}$ |
| $\cos bt$ | $\frac{s}{s^2 + b^2}$ |
| $e^{at}f(t)$ | $F(s - a)$ |

$$
\mathcal{L}\{y'\} = sY(s) - y(0) \qquad \mathcal{L}\{y''\} = s^2Y(s) - sy(0) - y'(0)
$$

**Example 1 (from the definition).** $\mathcal{L}\{e^{at}\} = \int_0^\infty e^{-(s - a)t}dt = \frac{1}{s - a}$ for $s > a$.

**Example 2 (linearity).** $\mathcal{L}\{3 + 2t - 5e^{-t}\} = \frac{3}{s} + \frac{2}{s^2} - \frac{5}{s + 1}$.

**Example 3 (trig).** $\mathcal{L}\{4\sin 3t\} = \frac{12}{s^2 + 9}$.

**Example 4 (shifting).** $\mathcal{L}\{e^{2t}\cos t\} = \frac{s - 2}{(s - 2)^2 + 1}$.

**Example 5 (code).**
```python
import sympy as sp
t, s = sp.symbols("t s", positive=True)
print(sp.laplace_transform(sp.exp(2*t) * sp.cos(t), t, s, noconds=True))   # (s - 2)/((s - 2)**2 + 1)
```
SymPy computes Laplace transforms symbolically, a good way to check table work.

## 5. Common mistakes
- Mixing up the sine and cosine transforms ($\sin$ has $b$ on top; $\cos$ has $s$).
- Forgetting the initial-value terms in the derivative formulas.
- Using $F(s + a)$ instead of $F(s - a)$ for $e^{at}f(t)$.

## 6. Practice
Q: $\mathcal{L}\{t^2\}$?
A: $\frac{2}{s^3}$.

Q: $\mathcal{L}\{e^{-3t}\}$?
A: $\frac{1}{s + 3}$.

Q: $\mathcal{L}\{\cos 5t\}$?
A: $\frac{s}{s^2 + 25}$.

Q: $\mathcal{L}\{y'\}$?
A: $sY(s) - y(0)$.

Q: $\mathcal{L}\{te^{t}\}$?
A: $\frac{1}{(s - 1)^2}$.

@@ 3 | TOPIC | Inverse Laplace transforms and solving initial value problems | Partial fractions, completing the square, transform–solve–invert method | NICE
# Inverse Laplace transforms and solving initial value problems

## 1. What it covers
- Finding $f(t)$ from $F(s)$ using the table, **partial fractions** and **completing the square**.
- The full method: **transform the ODE → solve for $Y(s)$ → invert**.
- Initial conditions are built in automatically.

## 2. Where it is used
- **Control and circuit design:** step responses, impulse responses, transient analysis.
- **Mechanical systems:** response of suspensions and structures to loads.
- **Any linear ODE with initial conditions**, especially with piecewise forcing (next topic).

## 3. What it means
**Inverse transform:** break $F(s)$ into simple pieces that appear in the table. Partial fractions (Algebra and Calculus roadmaps) do most of the work; completing the square handles quadratic denominators (giving shifted sines and cosines).

**Solving an IVP:**
1. Take $\mathcal{L}$ of both sides, using the derivative rules (initial values enter here).
2. Solve the algebraic equation for $Y(s)$.
3. Invert to get $y(t)$.

## 4. Formula & worked example
**Example 1 (inverse).** $F(s) = \frac{5}{s^2 + 3s - 4} = \frac{5}{(s - 1)(s + 4)} = \frac{1}{s - 1} - \frac{1}{s + 4}$ → $f(t) = e^t - e^{-4t}$.

**Example 2 (complete the square).** $F(s) = \frac{s + 1}{s^2 + 2s + 5} = \frac{s + 1}{(s + 1)^2 + 4}$ → $f(t) = e^{-t}\cos 2t$.

**Example 3 (first order).** $y' + 2y = 4$, $y(0) = 1$.
- $sY - 1 + 2Y = \frac{4}{s}$ → $Y = \frac{1}{s + 2} + \frac{4}{s(s + 2)} = \frac{1}{s + 2} + \frac{2}{s} - \frac{2}{s + 2}$ → $y = 2 - e^{-2t}$.

**Example 4 (second order).** $y'' + 4y = 0$, $y(0) = 3$, $y'(0) = 2$.
- $s^2Y - 3s - 2 + 4Y = 0$ → $Y = \frac{3s + 2}{s^2 + 4} = 3\frac{s}{s^2 + 4} + \frac{2}{s^2 + 4}$ → $y = 3\cos 2t + \sin 2t$.

**Example 5 (forced).** $y'' - y = e^{2t}$, $y(0) = 0$, $y'(0) = 0$.
- $(s^2 - 1)Y = \frac{1}{s - 2}$ → $Y = \frac{1}{(s - 2)(s - 1)(s + 1)} = \frac{1/3}{s - 2} - \frac{1/2}{s - 1} + \frac{1/6}{s + 1}$
- $y = \frac{1}{3}e^{2t} - \frac{1}{2}e^t + \frac{1}{6}e^{-t}$.

**Code (check).**
```python
import sympy as sp
t, s = sp.symbols("t s", positive=True)
print(sp.inverse_laplace_transform(1 / ((s - 2)*(s - 1)*(s + 1)), s, t))
```

## 5. Common mistakes
- Partial fraction errors (check by recombining).
- Forgetting initial conditions when transforming derivatives.
- Missing the shift when inverting expressions like $\frac{s + 1}{(s + 1)^2 + 4}$.

## 6. Practice
Q: $\mathcal{L}^{-1}\left\{\frac{1}{s - 4}\right\}$?
A: $e^{4t}$.

Q: $\mathcal{L}^{-1}\left\{\frac{6}{s^4}\right\}$?
A: $t^3$.

Q: $\mathcal{L}^{-1}\left\{\frac{3}{s^2 + 9}\right\}$?
A: $\sin 3t$.

Q: Three steps of the Laplace method for IVPs?
A: Transform, solve for $Y(s)$, invert.

Q: Solve $y' - 3y = 0$, $y(0) = 2$ with Laplace.
A: $Y = \frac{2}{s - 3}$ → $y = 2e^{3t}$.

@@ 3 | TOPIC | Step functions, impulses and transfer functions | Unit step u(t − a), second shifting theorem, Dirac delta, transfer functions and poles | NICE
# Step functions, impulses and transfer functions

## 1. What it covers
- The **unit step** $u(t - a)$ for switching forces on and off.
- The **second shifting theorem**: $\mathcal{L}\{u(t - a)f(t - a)\} = e^{-as}F(s)$.
- The **Dirac delta** $\delta(t - a)$ for sudden impulses; $\mathcal{L}\{\delta(t - a)\} = e^{-as}$.
- **Transfer functions** $H(s)$ and stability from their **poles**.

## 2. Where it is used
- **Control engineering:** step responses (how a cruise control reacts when you set a new speed), impulse responses, stability analysis.
- **Electrical engineering:** switching a circuit on at $t = a$; voltage spikes.
- **Mechanical engineering:** hammer blows, collisions, sudden loads.
- **Signal processing:** a system's impulse response fully characterises it (used in audio reverb effects).

## 3. What it means
- **Unit step $u(t - a)$:** 0 before time $a$, 1 after: a switch turned on at $t = a$. Delays in time become factors $e^{-as}$ in the s-domain.
- **Dirac delta $\delta(t - a)$:** an idealised, instantaneous kick at $t = a$ with total "area" 1 (like a hammer strike).
- **Transfer function:** for a system starting at rest, $H(s) = \frac{Y(s)}{X(s)}$ (output over input in the s-domain). For $ay'' + by' + cy = x(t)$: $H(s) = \frac{1}{as^2 + bs + c}$. Its **poles** (roots of the denominator) are the characteristic roots: all poles with negative real parts → the system is stable.

## 4. Formula & worked example
$$
\mathcal{L}\{u(t - a)\} = \frac{e^{-as}}{s} \qquad \mathcal{L}\{u(t - a)f(t - a)\} = e^{-as}F(s) \qquad \mathcal{L}\{\delta(t - a)\} = e^{-as}
$$

**Example 1 (switched force).** A force of 5 turned on at $t = 2$: $5u(t - 2)$ → $\frac{5e^{-2s}}{s}$.

**Example 2 (step response).** $y' + y = u(t - 1)$, $y(0) = 0$.
- $(s + 1)Y = \frac{e^{-s}}{s}$ → $Y = e^{-s}\left(\frac{1}{s} - \frac{1}{s + 1}\right)$ → $y = u(t - 1)\left(1 - e^{-(t - 1)}\right)$: nothing happens until $t = 1$, then it rises towards 1.

**Example 3 (impulse).** A spring at rest is struck at $t = 0$: $y'' + 4y = \delta(t)$ → $(s^2 + 4)Y = 1$ → $y = \frac{1}{2}\sin 2t$ (the impulse response).

**Example 4 (transfer function and stability).** $y'' + 3y' + 2y = x(t)$: $H(s) = \frac{1}{s^2 + 3s + 2} = \frac{1}{(s + 1)(s + 2)}$; poles $-1$, $-2$ → stable.

**Example 5 (code: step response).**
```python
from scipy import signal
import numpy as np
sys = signal.TransferFunction([1], [1, 3, 2])      # H(s) = 1/(s^2 + 3s + 2)
t, y = signal.step(sys, T=np.linspace(0, 6, 7))
print(y.round(3))                                   # rises smoothly to 0.5 (= H(0))
print(np.roots([1, 3, 2]))                          # poles [-2, -1]: stable
```
`scipy.signal` works directly with transfer functions, as control engineers do; the step response settles at $H(0) = 0.5$.

## 5. Common mistakes
- Forgetting to shift the function itself: $\mathcal{L}\{u(t - a)f(t)\}$ needs $f$ written in terms of $t - a$ first.
- Treating $\delta$ like an ordinary function (it only makes sense inside integrals).
- Ignoring initial conditions when computing transfer functions (they assume the system starts at rest).

## 6. Practice
Q: Laplace transform of $u(t - 3)$?
A: $\frac{e^{-3s}}{s}$.

Q: Laplace transform of $\delta(t - 2)$?
A: $e^{-2s}$.

Q: What does the second shifting theorem say?
A: A time delay $a$ multiplies the transform by $e^{-as}$.

Q: Transfer function of $y'' + 5y' + 6y = x(t)$? Stable?
A: $\frac{1}{s^2 + 5s + 6}$; poles $-2$, $-3$ → stable.

Q: What is an impulse response?
A: The system's output when the input is a delta function; it characterises the system.

@@ 3 | TOPIC | Practice & review: Differential equations | Mixed problem set with worked answers | SHOULD
# Practice & review: Differential equations

**How to use this:** 60 minutes; check with SymPy (`dsolve`) or `solve_ivp`.

## Problems
1. Solve $y' + \frac{2}{x}y = x^2$, $y(1) = 1$.
2. Is $(3x^2 + y)\,dx + (x + 2y)\,dy = 0$ exact? Solve it.
3. A 200 L tank holds 10 kg of salt. Fresh water enters at 5 L/min and the mixture leaves at 5 L/min. When is 5 kg left?
4. Solve $y'' - 2y' - 3y = 0$, $y(0) = 0$, $y'(0) = 4$.
5. A mass of 2 kg on a spring with $k = 18$ N/m and damping $c = 8$ N·s/m. Classify the motion.
6. Find a particular solution of $y'' + 4y = 3\sin x$.
7. For $\vec{x}' = \begin{pmatrix} -2 & 1 \\ 1 & -2\end{pmatrix}\vec{x}$, find the eigenvalues and classify the equilibrium.
8. Solve $y'' + y = 0$, $y(0) = 1$, $y'(0) = 1$ with the Laplace transform.

## Answers
1. $\mu = x^2$: $(x^2y)' = x^4$ → $x^2y = \frac{x^5}{5} + C$; $C = \frac{4}{5}$ → $y = \frac{x^3}{5} + \frac{4}{5x^2}$.
2. $M_y = 1 = N_x$ ✓; $\psi = x^3 + xy + y^2$ → $x^3 + xy + y^2 = C$.
3. $S' = -\frac{5}{200}S$ → $S = 10e^{-t/40}$; $5 = 10e^{-t/40}$ → $t = 40\ln 2 \approx 27.7$ min.
4. $r = 3, -1$: $y = C_1e^{3t} + C_2e^{-t}$; $C_1 + C_2 = 0$, $3C_1 - C_2 = 4$ → $C_1 = 1$, $C_2 = -1$ → $y = e^{3t} - e^{-t}$.
5. $c^2 = 64 < 4mk = 144$ → underdamped (roots $-2 \pm i\sqrt{5}$).
6. Guess $A\sin x + B\cos x$: $3A\sin x + 3B\cos x = 3\sin x$ → $y_p = \sin x$.
7. $\lambda = -1, -3$ → stable node.
8. $(s^2 + 1)Y = s + 1$ → $Y = \frac{s}{s^2 + 1} + \frac{1}{s^2 + 1}$ → $y = \cos t + \sin t$.

## Flashcards
Q: Integrating factor for $y' + p(x)y = q(x)$?
A: $\mu = e^{\int p\,dx}$.

Q: Characteristic equation of $ay'' + by' + cy = 0$?
A: $ar^2 + br + c = 0$.

Q: Condition for critical damping?
A: $c^2 = 4mk$.

Q: $\mathcal{L}\{y''\}$?
A: $s^2Y - sy(0) - y'(0)$.

Q: Stability of $\vec{x}' = A\vec{x}$?
A: Stable if all eigenvalues have negative real parts.

@@ 2 | MODULE | Final review | Two cumulative mixed sets covering the whole roadmap | MUST
Mix everything. This completes the math roadmaps: from here, apply the tools in physics, machine learning, engineering or data science projects.

@@ 3 | TOPIC | Final review 1: Multivariable calculus | Cumulative mixed problems from Phases 1–5 with answers | MUST
# Final review 1: Multivariable calculus

**How to use this:** 75 minutes; check with SymPy and SciPy.

## Problems
1. Find the plane tangent to $z = x^2 + 3xy - y^2$ at $(1, 1)$.
2. $f(x, y) = x^2y - y^3$. Find $\nabla f$ at $(2, 1)$, the maximum rate of increase, and the directional derivative towards $\langle -1, 1\rangle$.
3. Find and classify the critical points of $f(x, y) = 2x^3 + 6xy + 3y^2$.
4. Use Lagrange multipliers to find the extreme values of $f(x, y) = xy$ on the ellipse $x^2 + 4y^2 = 8$.
5. $\int_0^1\int_x^1 \sin(y^2)\,dy\,dx$ (reverse the order).
6. Use polar coordinates: volume under $z = e^{-(x^2 + y^2)}$ above the disk of radius 1.
7. Compute the work done by $\vec{F} = \langle 2xy, x^2\rangle$ from $(0, 0)$ to $(3, 2)$ along any path.
8. Use Green's theorem for $\oint_C(-y^3\,dx + x^3\,dy)$ around the unit circle.

## Answers
1. $f(1, 1) = 3$; $f_x = 2x + 3y = 5$, $f_y = 3x - 2y = 1$ → $z = 3 + 5(x - 1) + (y - 1)$.
2. $\nabla f = \langle 2xy, x^2 - 3y^2\rangle = \langle 4, 1\rangle$; max rate $\sqrt{17}$; $\frac{1}{\sqrt{2}}(-4 + 1) = -\frac{3}{\sqrt{2}} \approx -2.12$.
3. $f_x = 6x^2 + 6y = 0$, $f_y = 6x + 6y = 0$ → $y = -x$, $6x^2 - 6x = 0$ → $(0, 0)$, $(1, -1)$. $D = 12x \cdot 6 - 36$: at $(0, 0)$ $D = -36$ → saddle; at $(1, -1)$ $D = 36 > 0$, $f_{xx} = 12 > 0$ → local min ($f = -1$).
4. $\langle y, x\rangle = \lambda\langle 2x, 8y\rangle$ → $x^2 = 4y^2$ → $8y^2 = 8$ → $y = \pm 1$, $x = \pm 2$; max $2$ at $(2, 1)$, $(-2, -1)$; min $-2$ at $(2, -1)$, $(-2, 1)$.
5. $\int_0^1\int_0^y\sin(y^2)\,dx\,dy = \int_0^1 y\sin(y^2)\,dy = \frac{1 - \cos 1}{2} \approx 0.230$.
6. $2\pi\int_0^1 re^{-r^2}dr = \pi(1 - e^{-1}) \approx 1.986$.
7. Conservative with $f = x^2y$ → work $= 18$.
8. $Q_x - P_y = 3x^2 + 3y^2$ → $\int_0^{2\pi}\int_0^1 3r^3\,dr\,d\theta = \frac{3\pi}{2}$.

@@ 3 | TOPIC | Final review 2: Differential equations | Cumulative mixed problems from Phase 6 with answers | MUST
# Final review 2: Differential equations

**How to use this:** 60 minutes; verify with `sympy.dsolve` or `solve_ivp`.

## Problems
1. Classify each: (a) $y' = xy^2$; (b) $y'' + \sin(x)y = 0$; (c) $u_t = u_{xx}$.
2. Solve $y' - y = e^{2x}$, $y(0) = 3$.
3. Newton's law of cooling: an object at 90°C in a 20°C room cools to 60°C in 10 min. Write and solve the DE; find the temperature at 30 min.
4. Solve $y'' + 6y' + 9y = 0$, $y(0) = 2$, $y'(0) = -4$.
5. Solve $y'' + 4y = 8x$.
6. An RLC circuit obeys $q'' + 4q' + 13q = 0$. Describe the charge's behaviour.
7. Solve $\vec{x}' = \begin{pmatrix} 1 & 2 \\ 2 & 1\end{pmatrix}\vec{x}$ and classify the equilibrium.
8. Use Laplace transforms: $y'' + 3y' + 2y = 0$, $y(0) = 1$, $y'(0) = 0$.

## Answers
1. (a) first-order non-linear ODE (separable); (b) second-order linear homogeneous ODE; (c) PDE (heat equation).
2. $\mu = e^{-x}$: $(e^{-x}y)' = e^x$ → $y = e^{2x} + Ce^x$; $C = 2$ → $y = e^{2x} + 2e^x$.
3. $T' = -k(T - 20)$ → $T = 20 + 70e^{-kt}$; $40 = 70e^{-10k}$ → $k = \frac{\ln(7/4)}{10} \approx 0.056$; $T(30) = 20 + 70\left(\frac{4}{7}\right)^3 \approx 33.1$°C.
4. $r = -3$ (repeated): $y = (C_1 + C_2t)e^{-3t}$; $C_1 = 2$; $y' = (C_2 - 3C_1 - 3C_2t)e^{-3t}$ → $C_2 - 6 = -4$ → $C_2 = 2$ → $y = (2 + 2t)e^{-3t}$.
5. $y_h = C_1\cos 2x + C_2\sin 2x$; $y_p = 2x$ → $y = C_1\cos 2x + C_2\sin 2x + 2x$.
6. $r = -2 \pm 3i$ → $q = e^{-2t}(C_1\cos 3t + C_2\sin 3t)$: an underdamped, decaying oscillation.
7. $\lambda = 3$ ($\langle 1, 1\rangle$), $\lambda = -1$ ($\langle 1, -1\rangle$) → $\vec{x} = c_1e^{3t}\langle 1, 1\rangle + c_2e^{-t}\langle 1, -1\rangle$; saddle (unstable).
8. $s^2Y - s + 3(sY - 1) + 2Y = 0$ → $Y = \frac{s + 3}{(s + 1)(s + 2)} = \frac{2}{s + 1} - \frac{1}{s + 2}$ → $y = 2e^{-t} - e^{-2t}$.
