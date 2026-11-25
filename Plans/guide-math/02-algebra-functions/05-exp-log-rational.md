@@ 1 | MODULE | Phase 5 — Radicals, Exponentials, Logarithms and Rational Functions (Core) | Rational exponents, radical equations, exponential growth and decay, e, logarithms, rational expressions and functions, nonlinear systems | MUST
# Phase 5: Radicals, exponentials, logarithms and rational functions

**Exponential functions** describe anything that grows or shrinks by a fixed **percentage**: money with compound interest, populations, viruses, radioactive decay, the number of transistors on a chip. **Logarithms** are their inverse: they answer "how long until it doubles?" and compress huge ranges into readable scales (decibels, pH, earthquake magnitudes, and the $O(\log n)$ of binary search).

**Rational functions** (one polynomial divided by another) model averages, rates and concentrations, and have **asymptotes**: lines the graph approaches but never reaches.

@@ 2 | MODULE | Radicals and rational exponents | nth roots, fractional powers, simplifying radicals, radical equations | MUST
A fractional exponent is a root in disguise: $x^{1/2} = \sqrt{x}$.

@@ 3 | TOPIC | nth roots and rational exponents | Principal roots, a^(1/n), a^(m/n), using exponent rules with fractions | MUST
# nth roots and rational exponents

## 1. What it covers
- **nth roots** $\sqrt[n]{a}$ and the principal root.
- **Rational exponents:** $a^{1/n} = \sqrt[n]{a}$ and $a^{m/n} = \left(\sqrt[n]{a}\right)^m$.
- Simplifying with the exponent rules (which still work for fractions).

## 2. Where it is used
- **Finance:** the average annual growth rate over $n$ years is $\left(\frac{\text{end}}{\text{start}}\right)^{1/n} - 1$ (CAGR).
- **Science:** Kepler's law $T \propto a^{3/2}$; animal metabolism scales as mass$^{3/4}$.
- **Music:** each semitone multiplies frequency by $2^{1/12}$.
- **Graphics:** gamma correction raises brightness to the power $1/2.2$.

## 3. What it means
$\sqrt[n]{a}$ is the number whose $n$th power is $a$: $\sqrt[3]{8} = 2$, $\sqrt[4]{81} = 3$. For even $n$, $a$ must be $\ge 0$ (in the reals) and we take the non-negative root.

**Why $a^{1/n}$ is a root:** the power rule says $\left(a^{1/n}\right)^n = a^{1} = a$. So $a^{1/n}$ is exactly the number whose $n$th power is $a$.

**$a^{m/n}$:** the denominator is the **root**, the numerator is the **power**. Doing the root first keeps numbers small: $8^{2/3} = \left(\sqrt[3]{8}\right)^2 = 4$.

All the exponent rules from before still apply.

## 4. Formula & worked example
$$
a^{1/n} = \sqrt[n]{a} \qquad a^{m/n} = \left(\sqrt[n]{a}\right)^m = \sqrt[n]{a^m} \qquad a^{-m/n} = \frac{1}{a^{m/n}}
$$

**Example 1.** $27^{1/3} = 3$; $16^{3/4} = \left(\sqrt[4]{16}\right)^3 = 2^3 = 8$

**Example 2.** $32^{-2/5} = \frac{1}{\left(\sqrt[5]{32}\right)^2} = \frac{1}{4}$

**Example 3.** Simplify $x^{1/2} \cdot x^{1/3} = x^{5/6}$

**Example 4.** Simplify $\left(8x^6\right)^{2/3} = 8^{2/3}x^{4} = 4x^4$

**Example 5.** Write $\sqrt[5]{x^3}$ as a power: $x^{3/5}$

**Example 6 (CAGR).** Revenue grew from 2 crore to 3.38 crore in 3 years. Average yearly growth?
- $\left(\frac{3.38}{2}\right)^{1/3} = 1.69^{1/3} \approx 1.191$ → about 19.1% per year

## 5. Common mistakes
- Swapping numerator and denominator: $8^{2/3}$ means cube root, then square.
- Thinking $a^{1/2} = \frac{a}{2}$.
- Taking an even root of a negative number in the reals ($\sqrt[4]{-16}$ is not real).

## 6. Practice
Q: $25^{3/2}$?
A: $125$.

Q: $81^{-1/4}$?
A: $\frac{1}{3}$.

Q: Simplify $\frac{x^{3/4}}{x^{1/4}}$.
A: $x^{1/2}$.

Q: Write $\frac{1}{\sqrt[3]{x^2}}$ with a rational exponent.
A: $x^{-2/3}$.

Q: The frequency ratio of one semitone?
A: $2^{1/12} \approx 1.0595$.

@@ 3 | TOPIC | Operations with radicals | Simplifying, adding, multiplying radicals, rationalizing denominators | MUST
# Operations with radicals

## 1. What it covers
- Simplifying square roots and cube roots.
- Adding and subtracting **like radicals**.
- Multiplying radicals and expanding brackets with radicals.
- **Rationalising** denominators, including with conjugates.

## 2. Where it is used
- **Exact answers** in geometry and trigonometry ($\frac{\sqrt{3}}{2}$, $5\sqrt{2}$).
- **Physics and engineering formulas** with square roots.
- **Algebra:** the quadratic formula produces radicals.
- **Numerical stability:** rationalising can avoid subtracting nearly equal numbers on a computer.

## 3. What it means
- **Simplify** by pulling out perfect powers: $\sqrt{50} = \sqrt{25 \cdot 2} = 5\sqrt{2}$.
- **Add** only like radicals (same root, same inside), like combining like terms: $3\sqrt{2} + 5\sqrt{2} = 8\sqrt{2}$, but $\sqrt{2} + \sqrt{3}$ can't combine.
- **Multiply:** $\sqrt{a}\sqrt{b} = \sqrt{ab}$.
- **Rationalise:** remove roots from the denominator by multiplying top and bottom by a suitable factor. For $a + \sqrt{b}$, use the **conjugate** $a - \sqrt{b}$, since $(a + \sqrt{b})(a - \sqrt{b}) = a^2 - b$ has no root.

## 4. Formula & worked example
$$
\sqrt{a}\sqrt{b} = \sqrt{ab} \qquad \frac{1}{\sqrt{a}} = \frac{\sqrt{a}}{a} \qquad \frac{1}{a + \sqrt{b}} = \frac{a - \sqrt{b}}{a^2 - b}
$$

**Example 1.** $\sqrt{75} + \sqrt{12} = 5\sqrt{3} + 2\sqrt{3} = 7\sqrt{3}$

**Example 2.** $\sqrt[3]{54} = \sqrt[3]{27 \cdot 2} = 3\sqrt[3]{2}$

**Example 3.** $(2 + \sqrt{3})(4 - \sqrt{3}) = 8 - 2\sqrt{3} + 4\sqrt{3} - 3 = 5 + 2\sqrt{3}$

**Example 4.** $\frac{6}{\sqrt{3}} = \frac{6\sqrt{3}}{3} = 2\sqrt{3}$

**Example 5.** $\frac{4}{3 - \sqrt{5}} = \frac{4(3 + \sqrt{5})}{9 - 5} = 3 + \sqrt{5}$

**Example 6.** $\left(\sqrt{x} + 2\right)^2 = x + 4\sqrt{x} + 4$

## 5. Common mistakes
- $\sqrt{9 + 16} = 3 + 4$. Roots don't split over addition.
- Adding unlike radicals: $\sqrt{2} + \sqrt{3} \ne \sqrt{5}$.
- Multiplying only the denominator when rationalising.

## 6. Practice
Q: Simplify $\sqrt{98}$.
A: $7\sqrt{2}$.

Q: $3\sqrt{8} - \sqrt{18}$?
A: $6\sqrt{2} - 3\sqrt{2} = 3\sqrt{2}$.

Q: $\sqrt{6} \cdot \sqrt{15}$?
A: $\sqrt{90} = 3\sqrt{10}$.

Q: Rationalise $\frac{5}{\sqrt{10}}$.
A: $\frac{\sqrt{10}}{2}$.

Q: Rationalise $\frac{1}{\sqrt{2} + 1}$.
A: $\sqrt{2} - 1$.

@@ 3 | TOPIC | Radical equations and extraneous solutions | Isolate the radical, raise to a power, check every answer | MUST
# Radical equations and extraneous solutions

## 1. What it covers
- Solving equations with square roots (and other roots).
- Equations with two radicals.
- **Extraneous solutions**: fake answers created by squaring, and why you must check.

## 2. Where it is used
- **Physics:** a pendulum's period $T = 2\pi\sqrt{L/g}$, solved for $L$.
- **Geometry:** distance equations $\sqrt{(x - a)^2 + (y - b)^2} = d$.
- **Engineering:** stopping distance and speed formulas with roots.

## 3. What it means
To remove a square root, **isolate it** and then **square both sides**. But squaring can turn a false statement into a true one: $-3 \ne 3$, yet $(-3)^2 = 3^2$. So squaring may introduce solutions that don't satisfy the original equation. These are **extraneous** and must be rejected. **Always check every answer in the original equation.**

**Steps:** 1. isolate the radical, 2. square (or cube), 3. solve, 4. check.

## 4. Formula & worked example
**Example 1.** $\sqrt{2x + 3} = 5$ → $2x + 3 = 25$ → $x = 11$. Check: $\sqrt{25} = 5$ ✓

**Example 2.** $\sqrt{x + 6} = x$
- Square: $x + 6 = x^2$ → $x^2 - x - 6 = 0$ → $x = 3$ or $x = -2$
- Check: $\sqrt{9} = 3$ ✓; $\sqrt{4} = 2 \ne -2$ ✗ → only $x = 3$

**Example 3 (isolate first).** $\sqrt{x - 1} + 3 = x$
- $\sqrt{x - 1} = x - 3$ → $x - 1 = x^2 - 6x + 9$ → $x^2 - 7x + 10 = 0$ → $x = 5$ or $2$
- Check: $x = 5$: $2 + 3 = 5$ ✓; $x = 2$: $1 + 3 = 4 \ne 2$ ✗ → $x = 5$

**Example 4 (cube root).** $\sqrt[3]{x - 4} = -2$ → $x - 4 = -8$ → $x = -4$ (cube roots don't create extraneous solutions)

**Example 5 (pendulum).** For $T = 2$ s, find $L$: $2 = 2\pi\sqrt{L/9.8}$ → $\sqrt{L/9.8} = \frac{1}{\pi}$ → $L = \frac{9.8}{\pi^2} \approx 0.993$ m

## 5. Common mistakes
- Squaring before isolating: $(\sqrt{x - 1} + 3)^2 \ne x - 1 + 9$.
- Skipping the check and keeping extraneous answers.
- Forgetting that $\sqrt{\ }$ is never negative, so $\sqrt{x} = -4$ has no solution.

## 6. Practice
Q: Solve $\sqrt{3x - 2} = 4$.
A: $x = 6$.

Q: Solve $\sqrt{x} = -3$.
A: No solution.

Q: Solve $\sqrt{x + 2} = x$.
A: $x = 2$ ($x = -1$ is extraneous).

Q: Solve $\sqrt{5x + 1} = x + 1$.
A: $x^2 - 3x = 0$ → $x = 0$ or $3$; both check.

Q: Why must you check answers to radical equations?
A: Squaring can introduce extraneous solutions.

@@ 2 | MODULE | Exponential functions and models | Growth and decay, compound interest, the number e, modelling | MUST
Linear grows by adding the same amount; exponential grows by multiplying by the same factor.

@@ 3 | TOPIC | Exponential functions: growth and decay | f(x) = a·bˣ, growth/decay factor, graphs, constant ratios in tables | MUST
# Exponential functions: growth and decay

## 1. What it covers
- The form $f(x) = a \cdot b^x$: initial value $a$, growth/decay **factor** $b$.
- Growth ($b > 1$) vs decay ($0 < b < 1$).
- Converting a percent rate to a factor: $b = 1 + r$ or $1 - r$.
- Recognising exponential data (constant **ratio**) and the graph's features.

## 2. Where it is used
- **Finance:** savings, inflation, loan balances.
- **Biology:** bacteria doubling, epidemics in the early stage.
- **Technology:** Moore's law (transistor counts doubled about every 2 years).
- **Physics:** radioactive decay, cooling, capacitor discharge.
- **Computing:** brute-forcing an $n$-bit key takes up to $2^n$ tries; exponential backoff in network retries.

## 3. What it means
In $f(x) = a \cdot b^x$:
- $a$ is the starting value ($f(0) = a$).
- Each time $x$ increases by 1, the output is **multiplied** by $b$.

**Percent rate → factor:** growing 8% per year means ×1.08; decaying 15% means ×0.85.

**Recognising it in a table:** linear data has a constant **difference**; exponential data has a constant **ratio** $\frac{y_{n+1}}{y_n}$.

**Graph:** passes through $(0, a)$; growth curves rise ever faster, decay curves fall towards 0. The x-axis ($y = 0$) is a **horizontal asymptote**: the graph gets closer and closer to it but never touches it.

## 4. Formula & worked example
$$
f(x) = a \cdot b^x \qquad b = 1 + r \text{ (growth)} \qquad b = 1 - r \text{ (decay)}
$$

**Example 1.** A town of 50 000 grows 3% per year. Model and population in 10 years.
- $P(t) = 50\,000(1.03)^t$; $P(10) \approx 50\,000(1.3439) \approx 67\,196$

**Example 2.** A car worth 20 lakh loses 12% of its value each year. Value after 5 years?
- $V = 20(0.88)^5 \approx 20(0.5277) \approx 10.55$ lakh

**Example 3 (table).** $x$: 0, 1, 2, 3; $y$: 5, 15, 45, 135. Ratios are all 3 → $y = 5 \cdot 3^x$.

**Example 4.** Is $y = 3(0.7)^x$ growth or decay? Rate? → decay, 30% per step.

**Example 5 (doubling).** Bacteria double every 20 minutes, starting with 100. How many after 2 hours?
- 6 doublings: $100 \cdot 2^6 = 6400$. As a function of minutes: $N = 100 \cdot 2^{t/20}$.

## 5. Common mistakes
- Using $b = 0.12$ for 12% decay. It's $1 - 0.12 = 0.88$.
- Confusing $a \cdot b^x$ with $(ab)^x$: the exponent applies only to $b$.
- Calling a constant-difference table exponential.

## 6. Practice
Q: Factor for 6.5% growth?
A: $1.065$.

Q: Initial value and rate of $y = 800(0.95)^t$?
A: 800; decreasing 5% per period.

Q: A 1500 taka item increases 10% per year. Price after 3 years?
A: $1500(1.1)^3 = 1996.50$.

Q: Is the table $y$: 4, 12, 36, 108 linear or exponential?
A: Exponential (ratio 3).

Q: Horizontal asymptote of $y = 2 \cdot 3^x$?
A: $y = 0$.

@@ 3 | TOPIC | Compound interest and the number e | A = P(1 + r/n)^(nt), continuous compounding, A = Pe^(rt) | MUST
# Compound interest and the number e

## 1. What it covers
- Compound interest with $n$ compounding periods per year.
- How more frequent compounding increases the final amount, up to a limit.
- The number $e \approx 2.71828$ and **continuous compounding** $A = Pe^{rt}$.
- Continuous growth and decay models $y = ae^{kt}$.

## 2. Where it is used
- **Banking:** monthly vs quarterly compounding; comparing APR and effective annual rate.
- **Science:** natural growth and decay are written with $e$ (populations, radioactivity, drug elimination).
- **Calculus:** $e^x$ is the unique function equal to its own derivative, which is why it appears everywhere.
- **Machine learning:** the sigmoid $\frac{1}{1 + e^{-x}}$ and softmax functions use $e$.

## 3. What it means
If a yearly rate $r$ is compounded $n$ times a year, each period earns $\frac{r}{n}$, and there are $nt$ periods in $t$ years.

Compounding more often helps, but less and less. With 100% interest on 1 taka for one year:
- yearly: $2$; monthly: $(1 + \frac{1}{12})^{12} \approx 2.613$; daily: $\approx 2.7146$; every second: $\approx 2.71828$.

The limit as $n \to \infty$ is the number $e$. So **continuous** compounding gives $A = Pe^{rt}$.

## 4. Formula & worked example
$$
A = P\left(1 + \frac{r}{n}\right)^{nt} \qquad A = Pe^{rt} \qquad e = \lim_{n \to \infty}\left(1 + \frac{1}{n}\right)^n \approx 2.71828
$$
Effective annual rate: $\left(1 + \frac{r}{n}\right)^n - 1$.

**Example 1.** 1 lakh at 9% compounded monthly for 5 years.
- $A = 100\,000\left(1 + \frac{0.09}{12}\right)^{60} = 100\,000(1.0075)^{60} \approx 156\,568$

**Example 2.** Same, compounded continuously.
- $A = 100\,000e^{0.45} \approx 156\,831$

**Example 3 (effective rate).** 12% compounded monthly → $(1.01)^{12} - 1 \approx 12.68\%$ per year.

**Example 4 (continuous decay).** A drug leaves the body with $C = 50e^{-0.2t}$ mg (t in hours). Amount after 6 hours?
- $50e^{-1.2} \approx 50(0.3012) \approx 15.06$ mg

**Example 5 (code).**
```python
import math
P, r, t = 100_000, 0.09, 5
for n in (1, 4, 12, 365):
    print(n, round(P * (1 + r / n) ** (n * t)))
print("continuous", round(P * math.exp(r * t)))
# 1 153862 / 4 156051 / 12 156568 / 365 156823 / continuous 156831
```
As $n$ grows, the amount creeps up towards the continuous limit $Pe^{rt}$, computed with `math.exp`.

## 5. Common mistakes
- Using the yearly rate per period ($1 + 0.09$ instead of $1 + \frac{0.09}{12}$).
- Using $t$ instead of $nt$ as the exponent.
- Thinking continuous compounding gives unlimited money. It approaches a finite limit.

## 6. Practice
Q: 50 000 at 8% compounded quarterly for 2 years?
A: $50\,000(1.02)^8 \approx 58\,583$.

Q: 50 000 at 8% continuously for 2 years?
A: $50\,000e^{0.16} \approx 58\,676$.

Q: Effective rate of 6% compounded monthly?
A: $(1.005)^{12} - 1 \approx 6.17\%$.

Q: Value of $e$ to 3 decimal places?
A: $2.718$.

Q: In $y = 200e^{-0.05t}$, growth or decay?
A: Decay (negative exponent coefficient).

@@ 3 | TOPIC | Exponential modeling: half-life, doubling time, exponential vs linear | Building models from data, half-life and doubling formulas, comparing long-run growth | MUST
# Exponential modeling: half-life, doubling time, exponential vs linear

## 1. What it covers
- Writing models from **two data points** or a description.
- **Doubling time** and **half-life** models: $A = A_0 \cdot 2^{t/d}$ and $A = A_0\left(\frac{1}{2}\right)^{t/h}$.
- Why exponential growth always overtakes linear growth eventually.
- When to choose a linear vs an exponential model.

## 2. Where it is used
- **Medicine:** drug half-lives decide dosing schedules.
- **Archaeology:** carbon-14 dating (half-life about 5730 years).
- **Epidemiology:** early outbreak doubling times.
- **Startups and SaaS:** month-over-month growth projections.
- **Algorithms:** an $O(2^n)$ algorithm becomes unusable fast compared to $O(n)$.

## 3. What it means
**Doubling time $d$:** every $d$ time units the amount doubles. After $t$ units there have been $\frac{t}{d}$ doublings.
**Half-life $h$:** every $h$ units half remains.

**Linear vs exponential:** linear adds a fixed amount each step; exponential multiplies. Even a small percentage growth eventually beats a large fixed addition. A rice grain doubling on each square of a chessboard beats "add 1000 grains per square" by square 15.

**Choosing a model:** check differences (constant → linear) vs ratios (constant → exponential), or plot the data.

## 4. Formula & worked example
$$
A = A_0 \cdot 2^{t/d} \qquad A = A_0 \left(\tfrac{1}{2}\right)^{t/h} \qquad \text{from two points: } b = \left(\frac{y_2}{y_1}\right)^{1/(x_2 - x_1)}
$$

**Example 1 (half-life).** 80 mg of a drug with a half-life of 6 hours. Amount after 24 hours?
- $80\left(\frac{1}{2}\right)^{24/6} = 80 \cdot \frac{1}{16} = 5$ mg

**Example 2 (doubling).** Users double every 3 months from 2000. After 1.5 years?
- 18 months = 6 doublings: $2000 \cdot 2^6 = 128\,000$

**Example 3 (two points).** A culture has 500 cells at hour 2 and 4500 at hour 6.
- $b = \left(\frac{4500}{500}\right)^{1/4} = 9^{1/4} = \sqrt{3} \approx 1.732$; $a = \frac{500}{b^2} = \frac{500}{3} \approx 166.7$
- $N(t) \approx 166.7(1.732)^t$

**Example 4 (linear vs exponential).** Job A: 50 000 taka/month + 5000 raise per year. Job B: 40 000 + 10% raise per year. When does B overtake A?
- Year $n$: A $= 50\,000 + 5000n$, B $= 40\,000(1.1)^n$. Try values: $n = 6$: A 80 000, B 70 862; $n = 8$: A 90 000, B 85 744; $n = 9$: A 95 000, B 94 318; $n = 10$: A 100 000, B 103 750 → B overtakes in year 10.

## 5. Common mistakes
- Using $\frac{1}{2}t$ instead of $\left(\frac{1}{2}\right)^{t/h}$.
- Forgetting to convert time units (months vs years).
- Fitting a straight line to clearly curving (constant-ratio) data.

## 6. Practice
Q: A substance has a half-life of 10 days. What fraction remains after 30 days?
A: $\frac{1}{8}$.

Q: An investment doubles every 8 years. Growth factor over 24 years?
A: $2^3 = 8$.

Q: Exponential model through $(0, 3)$ and $(2, 12)$?
A: $y = 3 \cdot 2^x$.

Q: Differences 5, 5, 5. Linear or exponential?
A: Linear.

Q: Carbon-14: what fraction remains after 11 460 years?
A: Two half-lives: $\frac{1}{4}$.

@@ 2 | MODULE | Logarithms | Definition, evaluating, properties, change of base, solving equations, log scales | MUST
A logarithm answers "what power?" — $\log_2 8 = 3$ because $2^3 = 8$.

@@ 3 | TOPIC | Logarithms: definition and evaluating | log_b(x) = y ⇔ bʸ = x, common and natural logs, graphs and domain | MUST
# Logarithms: definition and evaluating

## 1. What it covers
- The definition: $\log_b x = y$ means $b^y = x$.
- Converting between exponential and logarithmic form.
- Evaluating logs without a calculator.
- **Common log** $\log x$ (base 10) and **natural log** $\ln x$ (base $e$).
- The graph of $y = \log_b x$ and its domain.

## 2. Where it is used
- **Computer science:** binary search takes about $\log_2 n$ steps; a balanced tree of a million items has depth about $\log_2 10^6 \approx 20$. You need $\lceil\log_2 n\rceil$ bits to represent $n$ values.
- **Information theory:** entropy and cross-entropy loss in machine learning use $\log$.
- **Science scales:** pH, decibels, earthquake magnitude, star brightness.
- **Finance:** time to reach a target with compound interest.

## 3. What it means
A logarithm is an **exponent**. $\log_b x$ asks: "**to what power must I raise $b$ to get $x$?**"
- $\log_2 32 = 5$ because $2^5 = 32$.
- $\log_{10} 1000 = 3$ because $10^3 = 1000$: the log of a power of 10 counts its zeros.

Logs are the **inverse** of exponentials: $\log_b(b^x) = x$ and $b^{\log_b x} = x$. So the graph of $\log_b x$ is $b^x$ reflected in $y = x$.

**Domain:** you can only take the log of a **positive** number ($b^y$ is never 0 or negative). The graph has a vertical asymptote at $x = 0$ and passes through $(1, 0)$ since $b^0 = 1$.

**Analogy:** if exponentials are "how big does it get after $n$ doublings?", logs are "how many doublings to get this big?".

## 4. Formula & worked example
$$
\log_b x = y \iff b^y = x \qquad \log_b 1 = 0 \qquad \log_b b = 1 \qquad \log x = \log_{10} x \qquad \ln x = \log_e x
$$

**Example 1.** Write $5^3 = 125$ in log form: $\log_5 125 = 3$.

**Example 2.** Write $\log_4 \frac{1}{16} = -2$ in exponential form: $4^{-2} = \frac{1}{16}$.

**Example 3.** Evaluate $\log_3 81$: $3^? = 81$ → $4$.

**Example 4.** Evaluate $\log_8 2$: $8^? = 2$ → $8^{1/3} = 2$ → $\frac{1}{3}$.

**Example 5.** $\ln e^7 = 7$; $\log 0.001 = -3$; $e^{\ln 5} = 5$.

**Example 6.** Domain of $f(x) = \log(x - 4)$: $x > 4$.

**Example 7 (binary search).** How many steps to search 1 000 000 sorted items?
- $\log_2 10^6 \approx 19.9$ → about 20 steps (versus up to 1 000 000 for a linear scan).

## 5. Common mistakes
- Thinking $\log_b x$ means $\log \times b \times x$.
- Taking logs of zero or negative numbers.
- Confusing $\log 100 = 2$ (base 10) with $\ln 100 \approx 4.6$ (base $e$).

## 6. Practice
Q: $\log_2 64$?
A: $6$.

Q: $\log_{10} 0.01$?
A: $-2$.

Q: Write $\log_7 49 = 2$ in exponential form.
A: $7^2 = 49$.

Q: $\log_9 3$?
A: $\frac{1}{2}$.

Q: Domain of $\ln(2x + 6)$?
A: $x > -3$.

@@ 3 | TOPIC | Properties of logarithms and change of base | Product, quotient and power rules; expanding and condensing; change of base | MUST
# Properties of logarithms and change of base

## 1. What it covers
- **Product rule:** $\log_b(MN) = \log_b M + \log_b N$.
- **Quotient rule:** $\log_b\frac{M}{N} = \log_b M - \log_b N$.
- **Power rule:** $\log_b(M^k) = k\log_b M$.
- Expanding and condensing log expressions.
- **Change of base:** $\log_b x = \frac{\ln x}{\ln b}$.

## 2. Where it is used
- **History:** before calculators, engineers multiplied big numbers by **adding** their logs (slide rules and log tables).
- **Machine learning:** multiplying many small probabilities underflows to 0 on a computer, so models add **log-probabilities** instead (log-likelihood).
- **Solving exponential equations** (the power rule brings exponents down).
- **Programming:** `math.log(x, 2)` or `math.log2(x)` use change of base.

## 3. What it means
Logs are exponents, so they follow the exponent rules in disguise:
- Multiplying powers **adds** exponents → log of a product = **sum** of logs.
- Dividing → **subtract**.
- Raising to a power → **multiply** by it.

**Change of base:** calculators have only $\log$ (base 10) and $\ln$. To get $\log_b x$, divide: $\frac{\ln x}{\ln b}$. (If $b^y = x$, take $\ln$ of both sides: $y\ln b = \ln x$.)

## 4. Formula & worked example
$$
\log_b(MN) = \log_b M + \log_b N \qquad \log_b\frac{M}{N} = \log_b M - \log_b N \qquad \log_b M^k = k\log_b M \qquad \log_b x = \frac{\log_c x}{\log_c b}
$$

**Example 1 (expand).** $\log_3\left(\frac{9x^2}{y}\right) = \log_3 9 + 2\log_3 x - \log_3 y = 2 + 2\log_3 x - \log_3 y$

**Example 2 (condense).** $2\ln x + \ln 5 - \ln(x + 1) = \ln\frac{5x^2}{x + 1}$

**Example 3.** $\log_2 40 - \log_2 5 = \log_2 8 = 3$

**Example 4.** Given $\log 2 \approx 0.301$ and $\log 3 \approx 0.477$, find $\log 12$.
- $\log(4 \cdot 3) = 2\log 2 + \log 3 \approx 1.079$

**Example 5 (change of base).** $\log_5 200 = \frac{\ln 200}{\ln 5} \approx \frac{5.298}{1.609} \approx 3.292$

**Example 6 (code: log-probabilities).**
```python
import math
probs = [0.01] * 400
print(math.prod(probs))                 # 0.0  (underflow: true value is 1e-800)
print(sum(math.log(p) for p in probs))  # -1842.07  (the log of that tiny number, no underflow)
```
The product of 400 small probabilities is too small for a float and becomes 0. Adding logs (product rule) keeps the information.

## 5. Common mistakes
- $\log(M + N) = \log M + \log N$. **False.** The rule is for products, not sums.
- $\frac{\log M}{\log N} = \log M - \log N$. **False.** That's the change-of-base form, not the quotient rule.
- $(\log x)^2 = 2\log x$. **False.** The power rule needs the power **inside** the log.

## 6. Practice
Q: Expand $\log(100x^3)$.
A: $2 + 3\log x$.

Q: Condense $\log_2 x + \log_2 (x - 3)$.
A: $\log_2[x(x - 3)]$.

Q: $\log_6 9 + \log_6 4$?
A: $\log_6 36 = 2$.

Q: $\log_3 50$ using change of base?
A: $\frac{\ln 50}{\ln 3} \approx 3.561$.

Q: Is $\ln(a + b) = \ln a + \ln b$?
A: No.

@@ 3 | TOPIC | Solving exponential and logarithmic equations | Taking logs of both sides, rewriting logs as exponentials, checking the domain | MUST
# Solving exponential and logarithmic equations

## 1. What it covers
- **Exponential equations:** match bases, or take logs of both sides.
- **Logarithmic equations:** condense, then rewrite in exponential form.
- Checking for answers outside the domain of the log.
- Applied "how long until ...?" problems.

## 2. Where it is used
- **Finance:** how many years until savings reach a goal?
- **Medicine:** when does a drug fall below an effective level?
- **Population and epidemics:** when will cases reach a threshold?
- **Computing:** how many bits or tree levels are needed?

## 3. What it means
**Exponential equations** ($x$ in the exponent):
- If both sides can be written with the **same base**, set the exponents equal: $2^{x+1} = 2^5$ → $x + 1 = 5$.
- Otherwise **take $\ln$ (or $\log$) of both sides** and use the power rule to bring $x$ down.

**Log equations:**
- Condense to a single log, then convert: $\log_b(\ldots) = c$ → $\ldots = b^c$.
- If there's a log on each side with the same base, set the insides equal.
- **Check** that every answer makes all log arguments positive.

## 4. Formula & worked example
**Example 1 (same base).** $9^x = 27$ → $3^{2x} = 3^3$ → $x = \frac{3}{2}$

**Example 2 (take logs).** $5^x = 40$ → $x\ln 5 = \ln 40$ → $x = \frac{\ln 40}{\ln 5} \approx 2.292$

**Example 3.** $3e^{2x} - 4 = 20$ → $e^{2x} = 8$ → $2x = \ln 8$ → $x = \frac{\ln 8}{2} \approx 1.040$

**Example 4 (time to double).** 7% interest compounded yearly. Years to double?
- $1.07^t = 2$ → $t = \frac{\ln 2}{\ln 1.07} \approx 10.24$ years. (Rule of 72: $72 \div 7 \approx 10.3$.)

**Example 5 (log equation).** $\log_2(x + 3) = 5$ → $x + 3 = 32$ → $x = 29$

**Example 6 (check the domain).** $\log x + \log(x - 3) = 1$
- $\log[x(x - 3)] = 1$ → $x^2 - 3x = 10$ → $(x - 5)(x + 2) = 0$
- $x = -2$ makes $\log x$ undefined → reject. Answer $x = 5$.

## 5. Common mistakes
- $\ln(5^x) = \ln 5 \cdot \ln x$. It's $x\ln 5$.
- Dividing logs incorrectly: $\frac{\ln 40}{\ln 5} \ne \ln 8$.
- Not checking log equations for negative arguments.

## 6. Practice
Q: Solve $2^{3x} = 64$.
A: $x = 2$.

Q: Solve $e^x = 12$.
A: $x = \ln 12 \approx 2.485$.

Q: Solve $\log_3(2x - 1) = 2$.
A: $x = 5$.

Q: How long for 10 000 to grow to 15 000 at 5% compounded yearly?
A: $t = \frac{\ln 1.5}{\ln 1.05} \approx 8.31$ years.

Q: Solve $\ln x + \ln 4 = \ln 20$.
A: $4x = 20$ → $x = 5$.

@@ 3 | TOPIC | Logarithmic scales and applications | pH, decibels, Richter/moment magnitude, log plots | MUST
# Logarithmic scales and applications

## 1. What it covers
- Why log scales are used for quantities spanning many orders of magnitude.
- **pH**, **decibels (dB)**, **earthquake magnitude**.
- Reading **log-scale charts**.

## 2. Where it is used
- **Chemistry:** pH of acids, bases, blood and soil.
- **Audio engineering:** volume in dB; sound levels and hearing safety.
- **Seismology:** earthquake magnitudes.
- **Data visualisation:** stock charts and COVID case charts on log axes show percentage growth as straight lines.
- **Computing:** latency charts (1 ms to 10 s) and algorithm complexity plots.

## 3. What it means
Some quantities range from tiny to enormous: the loudest sound you can hear is about $10^{12}$ times more intense than the quietest. A log scale counts **powers of 10** instead, turning $10^{12}$ into 12 steps.

The consequence: **adding 1 on a log scale means multiplying by 10** (or by another base).
- pH 3 is **10 times** more acidic than pH 4.
- +10 dB = **10 times** the sound intensity (and we perceive roughly double the loudness).
- Magnitude 7 releases about **32 times** the energy of magnitude 6 (energy grows by $10^{1.5}$ per step).

## 4. Formula & worked example
$$
\text{pH} = -\log[\text{H}^+] \qquad L = 10\log\frac{I}{I_0}\ \text{dB} \qquad \frac{E_2}{E_1} = 10^{1.5(M_2 - M_1)}
$$

**Example 1.** Lemon juice has $[\text{H}^+] = 10^{-2.3}$. pH? → $2.3$

**Example 2.** pH 4 vs pH 7: how many times more acidic? → $10^{7 - 4} = 1000$ times

**Example 3 (dB).** A sound is 1000 times more intense than another. Difference in dB?
- $10\log 1000 = 30$ dB

**Example 4.** A 70 dB vacuum cleaner and a 100 dB rock concert. Intensity ratio?
- $10^{(100 - 70)/10} = 10^3 = 1000$ times

**Example 5 (earthquakes).** Magnitude 8 vs 6 energy?
- $10^{1.5 \times 2} = 10^3 = 1000$ times

**Example 6 (log chart).** On a log y-axis, a line that rises steadily means **constant percentage growth**: equal vertical steps are equal multiplications.

## 5. Common mistakes
- Treating a log scale as linear ("pH 2 is twice as acidic as pH 4" ✗; it's 100 times).
- Forgetting the negative sign in pH.
- Reading a log-axis chart as if the gridlines were equally spaced values.

## 6. Practice
Q: pH of a solution with $[\text{H}^+] = 10^{-9}$?
A: $9$ (basic).

Q: How many times more acidic is pH 2 than pH 5?
A: $1000$.

Q: A sound goes from 60 dB to 80 dB. Intensity factor?
A: $100$.

Q: Energy ratio of magnitude 7 vs 5?
A: $10^3 = 1000$.

Q: What does a straight rising line on a log-y chart mean?
A: Constant percentage (exponential) growth.

@@ 2 | MODULE | Rational expressions, functions and nonlinear systems | Simplifying and combining rational expressions, asymptotes, rational equations, nonlinear systems | SHOULD
A rational function is a fraction with polynomials on top and bottom. Everything you know about fractions still applies.

@@ 3 | TOPIC | Rational expressions: simplify, multiply, divide, add | Factoring and cancelling, excluded values, combining with a common denominator | SHOULD
# Rational expressions: simplify, multiply, divide, add

## 1. What it covers
- **Simplifying** by factoring and cancelling common **factors**.
- **Excluded values** (where the denominator is 0).
- Multiplying and dividing rational expressions.
- Adding and subtracting with a **least common denominator**.
- Complex fractions.

## 2. Where it is used
- **Physics and electronics:** combined resistance $\frac{R_1R_2}{R_1 + R_2}$, lens formulas.
- **Rates of work and average speed.**
- **Calculus:** limits and partial fractions.
- **Control systems:** transfer functions are rational expressions.

## 3. What it means
Rational expressions behave exactly like numeric fractions:
- **Simplify** by cancelling factors common to top and bottom (just as $\frac{6}{8} = \frac{3}{4}$). You must **factor first**; you can never cancel terms that are added.
- **Multiply:** factor, cancel, multiply. **Divide:** multiply by the reciprocal.
- **Add/subtract:** find the LCD (from the factored denominators), rewrite each fraction, combine numerators.

**Excluded values:** any $x$ that makes an original denominator zero is excluded, even if the factor later cancels.

## 4. Formula & worked example
**Example 1.** $\dfrac{x^2 - 9}{x^2 + 5x + 6} = \dfrac{(x + 3)(x - 3)}{(x + 3)(x + 2)} = \dfrac{x - 3}{x + 2}$, with $x \ne -3, -2$

**Example 2.** $\dfrac{2x}{x^2 - 4} \cdot \dfrac{x + 2}{4x^2} = \dfrac{2x(x + 2)}{(x + 2)(x - 2) \cdot 4x^2} = \dfrac{1}{2x(x - 2)}$

**Example 3.** $\dfrac{x + 1}{x} \div \dfrac{x^2 - 1}{x^2} = \dfrac{x + 1}{x} \cdot \dfrac{x^2}{(x + 1)(x - 1)} = \dfrac{x}{x - 1}$

**Example 4.** $\dfrac{3}{x} + \dfrac{2}{x + 1} = \dfrac{3(x + 1) + 2x}{x(x + 1)} = \dfrac{5x + 3}{x(x + 1)}$

**Example 5.** $\dfrac{x}{x - 2} - \dfrac{8}{x^2 - 4} = \dfrac{x(x + 2) - 8}{(x - 2)(x + 2)} = \dfrac{x^2 + 2x - 8}{(x - 2)(x + 2)} = \dfrac{(x + 4)(x - 2)}{(x - 2)(x + 2)} = \dfrac{x + 4}{x + 2}$

**Example 6 (resistors).** $\dfrac{1}{\frac{1}{R_1} + \frac{1}{R_2}} = \dfrac{1}{\frac{R_2 + R_1}{R_1R_2}} = \dfrac{R_1R_2}{R_1 + R_2}$

## 5. Common mistakes
- Cancelling terms: $\frac{x + 3}{x} \ne 3$.
- Forgetting excluded values from cancelled factors.
- Subtracting only the first term of a numerator: $-\frac{x - 4}{\ldots}$ distributes to both terms.

## 6. Practice
Q: Simplify $\frac{x^2 - 25}{x + 5}$.
A: $x - 5$, $x \ne -5$.

Q: Simplify $\frac{x^2 + 3x}{x^2 - 9}$.
A: $\frac{x}{x - 3}$, $x \ne \pm 3$.

Q: $\frac{1}{x} + \frac{1}{2x}$?
A: $\frac{3}{2x}$.

Q: $\frac{x}{x + 1} \cdot \frac{x^2 - 1}{x^2}$?
A: $\frac{x - 1}{x}$.

Q: $\frac{2}{x - 1} - \frac{1}{x + 1}$?
A: $\frac{x + 3}{(x - 1)(x + 1)}$.

@@ 3 | TOPIC | Rational functions: asymptotes, holes and graphs | Vertical asymptotes, holes, horizontal and slant asymptotes, intercepts, sketching | SHOULD
# Rational functions: asymptotes, holes and graphs

## 1. What it covers
- **Vertical asymptotes** (denominator zero after cancelling) and **holes** (cancelled factors).
- **Horizontal asymptotes** from comparing degrees.
- **Slant (oblique) asymptotes** when the top's degree is one more.
- Intercepts and sketching.

## 2. Where it is used
- **Economics:** average cost $\bar{C}(x) = \frac{5000 + 20x}{x}$ approaches 20 as production grows (the horizontal asymptote).
- **Biology and medicine:** drug concentration $C(t) = \frac{at}{t^2 + b}$; enzyme kinetics (Michaelis–Menten $\frac{V_{\max}S}{K + S}$).
- **Physics:** inverse-square laws (gravity, light intensity $\propto \frac{1}{d^2}$).
- **Computing:** Amdahl's law for parallel speed-up, $\frac{1}{(1 - p) + p/n}$, approaches $\frac{1}{1 - p}$.

## 3. What it means
For $f(x) = \frac{p(x)}{q(x)}$:
- **Vertical asymptote:** a value of $x$ that makes the denominator 0 (and not the numerator). The function shoots to $\pm\infty$ there, because dividing by numbers close to 0 gives huge results.
- **Hole:** if a factor cancels from top and bottom, the graph has a single missing point there instead.
- **Horizontal asymptote** (long-run level), comparing degree of top ($n$) and bottom ($m$):
  - $n < m$: $y = 0$.
  - $n = m$: $y = \frac{\text{leading coefficient of top}}{\text{leading coefficient of bottom}}$.
  - $n > m$: none (if $n = m + 1$ there is a **slant** asymptote, found by polynomial division).

## 4. Formula & worked example
**Example 1.** $f(x) = \dfrac{2x + 1}{x - 3}$
- VA $x = 3$; HA $y = \frac{2}{1} = 2$; x-intercept $-\frac{1}{2}$; y-intercept $-\frac{1}{3}$.

**Example 2 (hole).** $g(x) = \dfrac{x^2 - 4}{x^2 - x - 2} = \dfrac{(x - 2)(x + 2)}{(x - 2)(x + 1)}$
- Hole at $x = 2$ (the point $\left(2, \frac{4}{3}\right)$ is missing); VA $x = -1$; HA $y = 1$.

**Example 3.** $h(x) = \dfrac{3x}{x^2 + 1}$: no VA (denominator never 0); HA $y = 0$.

**Example 4 (slant).** $k(x) = \dfrac{x^2 + 1}{x - 1}$: divide: $x + 1 + \frac{2}{x - 1}$ → slant asymptote $y = x + 1$; VA $x = 1$.

**Example 5 (average cost).** $\bar{C}(x) = \dfrac{5000 + 20x}{x}$: HA $y = 20$. As production grows, the average cost per item approaches the 20-taka material cost, since the fixed 5000 is spread thin.

## 5. Common mistakes
- Calling every zero of the denominator a vertical asymptote. Factor first: cancelled factors give holes.
- Comparing leading **terms'** coefficients when the degrees differ.
- Thinking a graph can never cross a horizontal asymptote. It can, in the middle; the asymptote describes the ends.

## 6. Practice
Q: Vertical and horizontal asymptotes of $\frac{5}{x + 2}$?
A: VA $x = -2$; HA $y = 0$.

Q: Horizontal asymptote of $\frac{4x^2 - 1}{2x^2 + x}$?
A: $y = 2$.

Q: Where is the hole in $\frac{x^2 - 1}{x - 1}$?
A: At $x = 1$ (point $(1, 2)$ missing).

Q: Slant asymptote of $\frac{x^2 + 3x}{x + 1}$?
A: Division gives $x + 2 - \frac{2}{x + 1}$ → $y = x + 2$.

Q: Long-run average cost of $\frac{20\,000 + 150x}{x}$?
A: Approaches 150.

@@ 3 | TOPIC | Rational equations and inequalities | Clearing denominators, checking restrictions, sign charts, partial fractions intro | SHOULD
# Rational equations and inequalities

## 1. What it covers
- Solving rational equations by multiplying by the LCD.
- Rejecting solutions that make a denominator zero.
- Solving rational inequalities with a **sign chart**.
- A first look at **partial fractions** (splitting a fraction into simpler ones).

## 2. Where it is used
- **Work and rate problems:** "two printers together".
- **Optics:** the thin lens equation $\frac{1}{f} = \frac{1}{d_o} + \frac{1}{d_i}$.
- **Average speed** problems with unknown speeds.
- **Calculus:** partial fractions make many integrals possible; Laplace transforms in engineering.

## 3. What it means
**Equations:** multiply every term by the LCD to clear denominators, solve the resulting polynomial equation, and then **check** each answer against the excluded values (which make an original denominator zero).

**Inequalities:** don't multiply by an expression whose sign is unknown (it might flip the inequality). Instead:
1. Move everything to one side and combine into one fraction.
2. Find the **critical points** (zeros of top and bottom).
3. Test the sign in each interval (sign chart).
4. Include zeros of the numerator if $\le$/$\ge$; never include zeros of the denominator.

**Partial fractions:** reverse of adding fractions: $\frac{5x + 3}{x(x + 1)} = \frac{3}{x} + \frac{2}{x + 1}$.

## 4. Formula & worked example
**Example 1.** $\dfrac{3}{x} + \dfrac{1}{2} = \dfrac{5}{x}$
- ×$2x$: $6 + x = 10$ → $x = 4$ ✓

**Example 2 (extraneous).** $\dfrac{x}{x - 2} = \dfrac{2}{x - 2} + 3$
- ×$(x - 2)$: $x = 2 + 3(x - 2)$ → $x = 3x - 4$ → $x = 2$. But $x = 2$ is excluded → **no solution**.

**Example 3 (work).** Printer A prints a job in 6 h, B in 3 h. Together?
- $\frac{1}{6} + \frac{1}{3} = \frac{1}{t}$ → ×$6t$: $t + 2t = 6$ → $t = 2$ h

**Example 4 (inequality).** $\dfrac{x - 3}{x + 1} \le 0$
- Critical points $-1$ (bottom) and $3$ (top).
- Signs: $x < -1$: $\frac{-}{-} = +$; $-1 < x < 3$: $\frac{-}{+} = -$; $x > 3$: $+$.
- Solution: $(-1, 3]$ (include 3, exclude $-1$).

**Example 5 (partial fractions).** Split $\dfrac{7x - 1}{(x - 1)(x + 2)}$.
- $\frac{A}{x - 1} + \frac{B}{x + 2}$ → $7x - 1 = A(x + 2) + B(x - 1)$
- $x = 1$: $6 = 3A$ → $A = 2$; $x = -2$: $-15 = -3B$ → $B = 5$ → $\frac{2}{x - 1} + \frac{5}{x + 2}$

## 5. Common mistakes
- Not checking against excluded values.
- Multiplying an inequality by an expression with unknown sign.
- Including a denominator zero in an inequality's solution.

## 6. Practice
Q: Solve $\frac{4}{x} = \frac{2}{x - 3}$.
A: $4(x - 3) = 2x$ → $x = 6$.

Q: Solve $\frac{1}{x} + \frac{1}{3} = \frac{1}{2}$.
A: $\frac{1}{x} = \frac{1}{6}$ → $x = 6$.

Q: Solve $\frac{x + 2}{x - 4} > 0$.
A: $x < -2$ or $x > 4$.

Q: Two pipes fill a tank in 4 h and 12 h. Together?
A: $3$ h.

Q: Split $\frac{2}{x(x + 2)}$ into partial fractions.
A: $\frac{1}{x} - \frac{1}{x + 2}$.

@@ 3 | TOPIC | Nonlinear systems of equations | Line–parabola, line–circle and curve–curve intersections | SHOULD
# Nonlinear systems of equations

## 1. What it covers
- Systems where at least one equation is not linear (parabola, circle, hyperbola).
- Solving by substitution or elimination.
- The number of intersection points (0, 1, 2, ...).

## 2. Where it is used
- **Computer graphics and games:** collision detection between a ray (line) and a circle/sphere.
- **GPS:** your position is where distance circles (spheres) from satellites intersect.
- **Economics:** supply and demand curves that aren't straight lines.
- **Engineering:** where a projectile's path meets a slope.

## 3. What it means
Same idea as linear systems: the solutions are the points on **both** curves. A line and a parabola can meet at 0, 1 or 2 points; a line and a circle likewise; two circles at 0, 1 or 2.

**Method:** usually **substitution**: solve the linear equation for one variable and plug it into the other, giving a quadratic. Its discriminant tells you how many intersections there are.

## 4. Formula & worked example
**Example 1 (line–parabola).** $y = x^2 - 4$ and $y = x + 2$.
- $x^2 - 4 = x + 2$ → $x^2 - x - 6 = 0$ → $x = 3$ or $x = -2$ → points $(3, 5)$ and $(-2, 0)$.

**Example 2 (line–circle).** $x^2 + y^2 = 25$ and $y = x + 1$.
- $x^2 + (x + 1)^2 = 25$ → $2x^2 + 2x - 24 = 0$ → $x^2 + x - 12 = 0$ → $x = 3$ or $-4$
- Points $(3, 4)$ and $(-4, -3)$.

**Example 3 (tangent line).** For which $k$ does $y = 2x + k$ touch $y = x^2$ exactly once?
- $x^2 - 2x - k = 0$; one solution when $\Delta = 4 + 4k = 0$ → $k = -1$.

**Example 4 (elimination).** $x^2 + y^2 = 13$ and $x^2 - y^2 = 5$.
- Add: $2x^2 = 18$ → $x = \pm 3$; then $y^2 = 4$ → $y = \pm 2$ → four points $(\pm 3, \pm 2)$.

**Example 5 (ray hits circle?).** Does the line $y = 10$ meet the circle $x^2 + y^2 = 64$?
- $x^2 = 64 - 100 < 0$ → no (the line passes above the circle).

## 5. Common mistakes
- Finding the $x$-values but not the matching $y$-values.
- Pairing $x$ and $y$ values incorrectly (substitute each $x$ into the **linear** equation).
- Missing solutions when taking square roots ($\pm$).

## 6. Practice
Q: Solve $y = x^2$ and $y = 4$.
A: $(2, 4)$ and $(-2, 4)$.

Q: Solve $y = x^2 + 1$ and $y = 2x$.
A: $x^2 - 2x + 1 = 0$ → $(1, 2)$ (tangent).

Q: Solve $x^2 + y^2 = 10$ and $y = 3x$.
A: $10x^2 = 10$ → $(1, 3)$ and $(-1, -3)$.

Q: Maximum number of intersections of a line and a circle?
A: $2$.

Q: Does $y = x - 10$ meet $y = x^2$?
A: $x^2 - x + 10 = 0$, $\Delta = -39 < 0$ → no.

@@ 3 | TOPIC | Practice & review: Radicals, exponentials, logs and rational functions | Mixed problem set with worked answers | MUST
# Practice & review: Radicals, exponentials, logs and rational functions

**How to use this:** 60 minutes; calculator for 5, 7, 8.

## Problems
1. Evaluate $64^{-2/3}$ and simplify $\sqrt{48} + \sqrt{27} - \sqrt{12}$.
2. Rationalise $\frac{6}{\sqrt{5} - \sqrt{2}}$.
3. Solve $\sqrt{2x + 7} = x + 2$.
4. Solve $4^{x - 1} = 8^{x}$.
5. A 12 lakh car loses 15% of its value per year. When is it worth less than 5 lakh?
6. Expand $\ln\frac{x^3\sqrt{y}}{e^2}$ and condense $3\log 2 + \log x - \log 4$.
7. Solve $2e^{0.3t} = 50$.
8. Solve $\log_2 x + \log_2(x + 2) = 3$.
9. Find all asymptotes and holes of $f(x) = \frac{x^2 - x - 6}{x^2 - 9}$.
10. Solve $\frac{2}{x + 1} + \frac{1}{x - 1} = \frac{4}{x^2 - 1}$.

## Answers
1. $\frac{1}{16}$; $4\sqrt{3} + 3\sqrt{3} - 2\sqrt{3} = 5\sqrt{3}$.
2. $\frac{6(\sqrt{5} + \sqrt{2})}{3} = 2(\sqrt{5} + \sqrt{2})$.
3. $2x + 7 = x^2 + 4x + 4$ → $x^2 + 2x - 3 = 0$ → $x = 1$ ✓ or $x = -3$ (gives $1 = -1$ ✗) → $x = 1$.
4. $2^{2x - 2} = 2^{3x}$ → $x = -2$.
5. $12(0.85)^t < 5$ → $t > \frac{\ln(5/12)}{\ln 0.85} \approx 5.39$ → during year 6.
6. $3\ln x + \frac{1}{2}\ln y - 2$; $\log\frac{8x}{4} = \log 2x$.
7. $e^{0.3t} = 25$ → $t = \frac{\ln 25}{0.3} \approx 10.73$.
8. $x(x + 2) = 8$ → $x = 2$ (reject $-4$).
9. $\frac{(x - 3)(x + 2)}{(x - 3)(x + 3)}$: hole at $x = 3$ (point $(3, \frac{5}{6})$); VA $x = -3$; HA $y = 1$.
10. ×$(x^2 - 1)$: $2(x - 1) + (x + 1) = 4$ → $3x - 1 = 4$ → $x = \frac{5}{3}$ ✓.

## Flashcards
Q: $a^{m/n}$ in radical form?
A: $\left(\sqrt[n]{a}\right)^m$.

Q: Definition of $\log_b x = y$?
A: $b^y = x$.

Q: Product, quotient and power rules of logs?
A: $\log MN = \log M + \log N$; $\log\frac{M}{N} = \log M - \log N$; $\log M^k = k\log M$.

Q: Horizontal asymptote when top and bottom have equal degree?
A: The ratio of leading coefficients.

Q: Continuous compounding formula?
A: $A = Pe^{rt}$.
