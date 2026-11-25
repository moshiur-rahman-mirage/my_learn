@@ 1 | MODULE | Phase 4 — Polynomials, Quadratics and Complex Numbers (Core) | Polynomial operations, factoring, quadratic functions and equations, complex numbers, polynomial division, zeros and graphs, binomial theorem | MUST
# Phase 4: Polynomials, quadratics and complex numbers

**Polynomials** are expressions like $3x^4 - 2x + 7$: sums of powers of $x$ with number coefficients. They are the most flexible simple functions; computers use them to approximate almost everything (curves in fonts and CAD, sensor calibration, even $\sin x$ inside your calculator).

**Quadratics** ($ax^2 + bx + c$) are the star of this phase: projectile paths, profit curves, areas. Solving them sometimes needs a new kind of number, the **complex numbers**.

@@ 2 | MODULE | Polynomial operations and factoring | Adding, multiplying, special products, factoring techniques | MUST
Multiplying out and factoring are opposite directions of the same road. Practise both until they're automatic.

@@ 3 | TOPIC | Polynomials: vocabulary, adding and subtracting | Terms, degree, leading coefficient, standard form, combining polynomials | MUST
# Polynomials: vocabulary, adding and subtracting

## 1. What it covers
- What a polynomial is: **terms**, **degree**, **leading coefficient**, **standard form**.
- Names: monomial, binomial, trinomial; linear, quadratic, cubic.
- Adding and subtracting polynomials.

## 2. Where it is used
- **Computer graphics:** Bézier curves (used in fonts, SVG, CSS animations) are polynomials.
- **Numerical computing:** calculators approximate $\sin$, $e^x$ and $\ln$ with polynomials.
- **Data fitting:** polynomial regression (`numpy.polyfit`).
- **Engineering:** beam deflection and trajectory formulas.
- **Error-correcting codes and CRC checksums** use polynomial arithmetic.

## 3. What it means
A **polynomial** is a sum of terms of the form $ax^n$, where $n$ is a whole number ($0, 1, 2, \ldots$). So $5x^3 - x + 4$ is a polynomial, but $\frac{1}{x}$ and $\sqrt{x}$ are not (negative or fractional powers).

- **Degree:** the highest power (it decides the overall shape).
- **Leading coefficient:** the coefficient of that highest power.
- **Standard form:** terms written from the highest power down.

**Adding/subtracting** is just combining like terms. When subtracting, distribute the minus sign to **every** term of the second polynomial.

## 4. Formula & worked example
**Example 1.** Describe $4x - 7x^3 + 2 + x^2$.
- Standard form $-7x^3 + x^2 + 4x + 2$; degree 3 (cubic); leading coefficient $-7$; 4 terms.

**Example 2.** $(3x^2 - 5x + 1) + (x^2 + 2x - 6) = 4x^2 - 3x - 5$

**Example 3.** $(5x^3 - 2x + 4) - (2x^3 + x^2 - 3x + 9)$
- $= 5x^3 - 2x + 4 - 2x^3 - x^2 + 3x - 9 = 3x^3 - x^2 + x - 5$

**Example 4.** Is $3x^{-2} + x$ a polynomial? No (negative exponent).

**Example 5.** Perimeter of a triangle with sides $2x + 1$, $x^2 - 3$ and $4x$.
- $x^2 + 6x - 2$

## 5. Common mistakes
- Subtracting only the first term of the second polynomial.
- Combining unlike terms ($x^2 + x \ne x^3$).
- Giving the degree as the number of terms.

## 6. Practice
Q: Degree and leading coefficient of $6 - 2x^5 + x^2$?
A: Degree 5; leading coefficient $-2$.

Q: $(2a^2 + 3a - 1) + (5 - a - a^2)$?
A: $a^2 + 2a + 4$.

Q: $(x^3 - 4x) - (3x^3 + x^2 - 4x)$?
A: $-2x^3 - x^2$.

Q: Is $\sqrt{2}x^2 + 1$ a polynomial?
A: Yes (coefficients may be any real number; the powers must be whole numbers).

Q: Name a degree-2 polynomial with 3 terms.
A: A quadratic trinomial, e.g. $x^2 + 3x + 2$.

@@ 3 | TOPIC | Multiplying polynomials and special products | Distributing, FOIL and the area model, (a ± b)², (a + b)(a − b), cubes | MUST
# Multiplying polynomials and special products

## 1. What it covers
- Multiplying monomials and polynomials by distributing every term.
- Multiplying two binomials (FOIL, area/grid method).
- **Special products:** $(a + b)^2$, $(a - b)^2$, $(a + b)(a - b)$, and $(a + b)^3$.

## 2. Where it is used
- **Area and volume formulas:** a garden $(x + 3)$ by $(x + 5)$.
- **Mental maths:** $43 \times 37 = (40 + 3)(40 - 3) = 1600 - 9 = 1591$.
- **Algebra:** expanding is needed for solving equations and for calculus.
- **Statistics:** expanding $(x - \mu)^2$ gives the variance shortcut formula.

## 3. What it means
**Every term in the first bracket multiplies every term in the second.** The **area model** makes this visible: a rectangle with sides $(x + 3)$ and $(x + 5)$ splits into four smaller rectangles: $x^2$, $5x$, $3x$, $15$. Add them up: $x^2 + 8x + 15$.

**FOIL** (First, Outer, Inner, Last) is a checklist for two binomials only.

**Special products** appear so often they're worth memorising:
- $(a + b)^2 = a^2 + 2ab + b^2$. **Not** $a^2 + b^2$; the $2ab$ is the two middle rectangles.
- $(a - b)^2 = a^2 - 2ab + b^2$.
- $(a + b)(a - b) = a^2 - b^2$ (the middle terms cancel).

## 4. Formula & worked example
$$
(a + b)^2 = a^2 + 2ab + b^2 \qquad (a - b)^2 = a^2 - 2ab + b^2 \qquad (a + b)(a - b) = a^2 - b^2
$$
$$
(a + b)^3 = a^3 + 3a^2b + 3ab^2 + b^3
$$

**Example 1.** $-3x^2(4x^3 - 2x + 5) = -12x^5 + 6x^3 - 15x^2$

**Example 2.** $(2x + 3)(x - 4) = 2x^2 - 8x + 3x - 12 = 2x^2 - 5x - 12$

**Example 3.** $(3y - 5)^2 = 9y^2 - 30y + 25$

**Example 4.** $(4m + 7)(4m - 7) = 16m^2 - 49$

**Example 5.** $(x + 2)(x^2 - 3x + 1) = x^3 - 3x^2 + x + 2x^2 - 6x + 2 = x^3 - x^2 - 5x + 2$

**Example 6 (mental).** $102^2 = (100 + 2)^2 = 10\,000 + 400 + 4 = 10\,404$

## 5. Common mistakes
- $(x + 5)^2 = x^2 + 25$. Missing the middle term $10x$.
- Sign errors with negative terms: $(x - 3)(x - 4)$ has $+12$ at the end.
- Missing a product when multiplying a binomial by a trinomial (there should be $2 \times 3 = 6$ products before combining).

## 6. Practice
Q: Expand $(x + 6)(x - 2)$.
A: $x^2 + 4x - 12$.

Q: Expand $(2a - 1)^2$.
A: $4a^2 - 4a + 1$.

Q: Expand $(5x + 3)(5x - 3)$.
A: $25x^2 - 9$.

Q: Compute $59 \times 61$ mentally.
A: $(60 - 1)(60 + 1) = 3599$.

Q: Expand $(x - 1)^3$.
A: $x^3 - 3x^2 + 3x - 1$.

@@ 3 | TOPIC | Factoring: GCF and grouping | Taking out the greatest common factor, factoring four-term polynomials by grouping | MUST
# Factoring: GCF and grouping

## 1. What it covers
- **Factoring out the GCF** (always the first step).
- Factoring out a binomial common factor.
- **Factoring by grouping** for four-term polynomials.

## 2. Where it is used
- **Solving equations:** factored form + the zero product property gives solutions immediately.
- **Simplifying fractions** with polynomials (rational expressions).
- **Efficient computation:** Horner's method rewrites $ax^3 + bx^2 + cx + d$ as $((ax + b)x + c)x + d$ to evaluate polynomials with fewer multiplications (used inside numeric libraries).

## 3. What it means
Factoring is "un-distributing": rewriting a sum as a **product**. Products are easier to work with: you can cancel factors and you can solve "product = 0".

**GCF:** the largest number and the highest power of each variable that divides every term.

**Grouping** (four terms): split into two pairs, factor each pair, and hope a common bracket appears.
$$
ax + ay + bx + by = a(x + y) + b(x + y) = (a + b)(x + y)
$$

## 4. Formula & worked example
**Example 1.** $12x^3 - 18x^2 + 6x = 6x(2x^2 - 3x + 1)$

**Example 2.** $15a^2b^3 + 10ab^2 = 5ab^2(3ab + 2)$

**Example 3 (binomial factor).** $x(x - 4) + 3(x - 4) = (x - 4)(x + 3)$

**Example 4 (grouping).** $x^3 + 2x^2 + 5x + 10$
- $x^2(x + 2) + 5(x + 2) = (x + 2)(x^2 + 5)$

**Example 5 (grouping with a minus).** $2x^3 - 6x^2 - x + 3$
- $2x^2(x - 3) - 1(x - 3) = (x - 3)(2x^2 - 1)$

**Example 6.** $6xy - 9x + 4y - 6$
- $3x(2y - 3) + 2(2y - 3) = (2y - 3)(3x + 2)$

## 5. Common mistakes
- Not taking out the **greatest** common factor.
- Leaving a term empty: $4x + 4 = 4(x + 1)$, not $4(x)$ (the "1" stays).
- Sign errors when factoring a negative out of the second pair: $-x + 3 = -1(x - 3)$.

## 6. Practice
Q: Factor $8x^4 - 20x^2$.
A: $4x^2(2x^2 - 5)$.

Q: Factor $y(2y + 1) - 5(2y + 1)$.
A: $(2y + 1)(y - 5)$.

Q: Factor $x^3 - 3x^2 + 4x - 12$.
A: $(x - 3)(x^2 + 4)$.

Q: Factor $ab + 3a - 2b - 6$.
A: $(b + 3)(a - 2)$.

Q: What should you always check first when factoring?
A: Whether there is a greatest common factor.

@@ 3 | TOPIC | Factoring trinomials | x² + bx + c by number pairs; ax² + bx + c by the AC (grouping) method | MUST
# Factoring trinomials

## 1. What it covers
- Factoring $x^2 + bx + c$: find two numbers that **multiply to $c$** and **add to $b$**.
- Factoring $ax^2 + bx + c$ with the **AC method**.
- Using signs to guide the search.

## 2. Where it is used
- **Solving quadratic equations** quickly.
- **Finding x-intercepts** of parabolas (where profit is zero, where a ball lands).
- **Simplifying rational expressions** and partial fractions (calculus).

## 3. What it means
Since $(x + p)(x + q) = x^2 + (p + q)x + pq$, factoring $x^2 + bx + c$ means finding $p$ and $q$ with $p + q = b$ and $pq = c$.

**Sign guide:**
- $c > 0$: both numbers have the **same sign** as $b$.
- $c < 0$: the numbers have **opposite signs**; the bigger one has the sign of $b$.

**AC method for $ax^2 + bx + c$:** find two numbers that multiply to $a \cdot c$ and add to $b$. Use them to **split the middle term**, then factor by grouping.

## 4. Formula & worked example
$$
x^2 + bx + c = (x + p)(x + q) \text{ where } p + q = b,\ pq = c
$$

**Example 1.** $x^2 + 7x + 12$: $3 \times 4 = 12$, $3 + 4 = 7$ → $(x + 3)(x + 4)$

**Example 2.** $x^2 - 9x + 20$: both negative: $-4, -5$ → $(x - 4)(x - 5)$

**Example 3.** $x^2 + 2x - 15$: opposite signs, product $-15$, sum $2$: $5, -3$ → $(x + 5)(x - 3)$

**Example 4 (AC).** $6x^2 + 11x - 10$
- $ac = -60$; numbers with product $-60$ and sum $11$: $15$ and $-4$.
- $6x^2 + 15x - 4x - 10 = 3x(2x + 5) - 2(2x + 5) = (2x + 5)(3x - 2)$

**Example 5 (GCF first).** $2x^2 - 8x - 24 = 2(x^2 - 4x - 12) = 2(x - 6)(x + 2)$

**Example 6.** Is $x^2 + x + 1$ factorable over the integers? Product 1, sum 1: no integer pair works → prime (it has no real roots, as you'll see with the discriminant).

## 5. Common mistakes
- Choosing numbers that multiply correctly but add to the wrong value.
- Forgetting the GCF first, making the numbers harder.
- Not checking by expanding the answer.

## 6. Practice
Q: Factor $x^2 + 10x + 21$.
A: $(x + 3)(x + 7)$.

Q: Factor $x^2 - x - 30$.
A: $(x - 6)(x + 5)$.

Q: Factor $3x^2 + 10x + 8$.
A: $(3x + 4)(x + 2)$.

Q: Factor $4x^2 - 4x - 3$.
A: $(2x - 3)(2x + 1)$.

Q: Factor $5x^2 - 20x + 15$.
A: $5(x - 1)(x - 3)$.

@@ 3 | TOPIC | Factoring special forms | Difference of squares, perfect square trinomials, sum and difference of cubes, quadratic form | MUST
# Factoring special forms

## 1. What it covers
- **Difference of squares:** $a^2 - b^2 = (a + b)(a - b)$.
- **Perfect square trinomials:** $a^2 \pm 2ab + b^2 = (a \pm b)^2$.
- **Sum and difference of cubes.**
- **Quadratic form:** factoring $x^4 + bx^2 + c$ by substitution.
- Factoring completely (repeating until nothing more factors).

## 2. Where it is used
- **Simplifying expressions** in algebra and calculus (e.g. limits like $\frac{x^2 - 9}{x - 3}$).
- **Mental arithmetic:** $97^2 - 3^2 = 100 \times 94$.
- **Rationalising denominators** uses $(a + b)(a - b) = a^2 - b^2$.
- **Solving higher-degree equations.**

## 3. What it means
These are the special products read backwards. Learn to **recognise the pattern**:
- Two perfect squares with a **minus** between them → difference of squares. (A **sum** of squares $a^2 + b^2$ doesn't factor over the reals.)
- First and last terms are squares, and the middle is $2 \times$ (their roots) → perfect square.
- Two cubes added or subtracted → the cube formulas. Memory aid for signs, **SOAP**: **S**ame, **O**pposite, **A**lways **P**ositive.

**Quadratic form:** $x^4 - 5x^2 + 4$ looks like a quadratic in $u = x^2$: $u^2 - 5u + 4$.

## 4. Formula & worked example
$$
a^2 - b^2 = (a + b)(a - b) \qquad a^2 \pm 2ab + b^2 = (a \pm b)^2
$$
$$
a^3 + b^3 = (a + b)(a^2 - ab + b^2) \qquad a^3 - b^3 = (a - b)(a^2 + ab + b^2)
$$

**Example 1.** $49x^2 - 16 = (7x + 4)(7x - 4)$

**Example 2.** $x^2 - 14x + 49 = (x - 7)^2$ (check: $2 \cdot x \cdot 7 = 14x$ ✓)

**Example 3.** $8x^3 + 27 = (2x)^3 + 3^3 = (2x + 3)(4x^2 - 6x + 9)$

**Example 4.** $x^3 - 64 = (x - 4)(x^2 + 4x + 16)$

**Example 5 (completely).** $x^4 - 81 = (x^2 + 9)(x^2 - 9) = (x^2 + 9)(x + 3)(x - 3)$

**Example 6 (quadratic form).** $x^4 - 5x^2 + 4$
- $u = x^2$: $u^2 - 5u + 4 = (u - 1)(u - 4)$ → $(x^2 - 1)(x^2 - 4) = (x + 1)(x - 1)(x + 2)(x - 2)$

## 5. Common mistakes
- Factoring $a^2 + b^2$ as $(a + b)^2$.
- Stopping too early (e.g. leaving $x^2 - 9$ unfactored).
- Sign errors in the cube formulas (use SOAP).

## 6. Practice
Q: Factor $25y^2 - 1$.
A: $(5y + 1)(5y - 1)$.

Q: Factor $4x^2 + 12x + 9$.
A: $(2x + 3)^2$.

Q: Factor $x^3 + 1$.
A: $(x + 1)(x^2 - x + 1)$.

Q: Factor $2x^3 - 50x$ completely.
A: $2x(x + 5)(x - 5)$.

Q: Factor $x^4 - 10x^2 + 9$.
A: $(x + 1)(x - 1)(x + 3)(x - 3)$.

@@ 2 | MODULE | Quadratic functions and equations | Parabolas, solving by factoring, square roots, completing the square, the quadratic formula, applications | MUST
A quadratic $ax^2 + bx + c$ graphs as a **parabola**, the path of a thrown ball and the shape of a satellite dish.

@@ 3 | TOPIC | Parabolas: features and graphing quadratics | Vertex, axis of symmetry, intercepts, direction, standard and factored forms | MUST
# Parabolas: features and graphing quadratics

## 1. What it covers
- The parabola $y = ax^2 + bx + c$: **direction** (up/down), **vertex**, **axis of symmetry**, **intercepts**.
- The vertex formula $x = -\frac{b}{2a}$.
- Graphing from standard form and from factored form $y = a(x - r)(x - s)$.

## 2. Where it is used
- **Projectile motion:** a thrown ball, a water fountain, a basketball shot.
- **Business:** revenue and profit often peak at a vertex (best price).
- **Engineering:** satellite dishes, car headlights and solar cookers use parabolic shapes (they focus parallel rays to one point).
- **Optimisation:** finding a maximum or minimum.

## 3. What it means
- **$a > 0$:** opens **up** (a smile), so the vertex is a minimum. **$a < 0$:** opens **down** (a frown), so the vertex is a maximum. Bigger $\lvert a\rvert$ = narrower.
- **Axis of symmetry:** the vertical line through the vertex; the parabola is a mirror image on either side.
- **Vertex:** at $x = -\frac{b}{2a}$ (exactly halfway between the two x-intercepts when they exist).
- **y-intercept:** $(0, c)$.
- **x-intercepts (zeros/roots):** solve $ax^2 + bx + c = 0$. In factored form $a(x - r)(x - s)$, they are simply $r$ and $s$.

## 4. Formula & worked example
$$
\text{vertex } x = -\frac{b}{2a}, \quad y = f\!\left(-\frac{b}{2a}\right)
$$

**Example 1.** $y = x^2 - 6x + 5$
- Opens up. Vertex $x = \frac{6}{2} = 3$, $y = 9 - 18 + 5 = -4$ → $(3, -4)$.
- y-intercept $(0, 5)$. Zeros: $(x - 1)(x - 5) = 0$ → $x = 1, 5$ (vertex is halfway ✓).

**Example 2 (factored form).** $y = -2(x + 1)(x - 3)$
- Opens down; zeros $-1$ and $3$; vertex at $x = 1$, $y = -2(2)(-2) = 8$ → $(1, 8)$.

**Example 3 (projectile).** $h(t) = -5t^2 + 20t + 1.5$
- Max at $t = \frac{-20}{-10} = 2$ s, $h = -20 + 40 + 1.5 = 21.5$ m.

**Example 4 (revenue).** Selling at price $p$ gives $R(p) = p(200 - 4p) = -4p^2 + 200p$.
- Best price: $p = \frac{-200}{-8} = 25$; max revenue $R(25) = 25 \times 100 = 2500$.

## 5. Common mistakes
- Forgetting the minus in $-\frac{b}{2a}$.
- Reading the zeros of $a(x - r)(x - s)$ with the wrong signs: $(x + 1)$ gives $x = -1$.
- Thinking every parabola has two x-intercepts (it can have one or none).

## 6. Practice
Q: Vertex of $y = x^2 + 4x - 1$?
A: $x = -2$, $y = -5$ → $(-2, -5)$.

Q: Does $y = -3x^2 + x$ open up or down?
A: Down.

Q: Zeros of $y = 2(x - 4)(x + 6)$?
A: $4$ and $-6$.

Q: Axis of symmetry of $y = 2x^2 - 12x + 7$?
A: $x = 3$.

Q: A ball's height is $h = -5t^2 + 30t$. Maximum height?
A: $45$ m at $t = 3$.

@@ 3 | TOPIC | Solving quadratics by factoring and square roots | Zero product property, x² = k, (x − h)² = k | MUST
# Solving quadratics by factoring and square roots

## 1. What it covers
- The **zero product property**: if $AB = 0$ then $A = 0$ or $B = 0$.
- Solving by factoring (after moving everything to one side).
- Solving $x^2 = k$ and $(x - h)^2 = k$ with square roots.

## 2. Where it is used
- **Physics:** when does a ball hit the ground ($h = 0$)?
- **Geometry:** a rectangle with area 60 whose length is 4 more than its width.
- **Business:** break-even points of a quadratic profit function.
- **Graphics:** ray–sphere intersection in ray tracing solves a quadratic for every pixel.

## 3. What it means
**Zero product property:** the only way a product of numbers can be 0 is if at least one factor is 0. So once a quadratic is factored and set **equal to zero**, each factor gives a solution.

**It only works with 0** on one side: $(x - 2)(x - 3) = 6$ does **not** mean $x - 2 = 6$.

**Square root method:** when there's no $x$ term, isolate the square and take $\pm$ the root.

## 4. Formula & worked example
$$
AB = 0 \Rightarrow A = 0 \text{ or } B = 0 \qquad x^2 = k \Rightarrow x = \pm\sqrt{k} \qquad (x - h)^2 = k \Rightarrow x = h \pm \sqrt{k}
$$

**Example 1.** $x^2 - 5x - 14 = 0$ → $(x - 7)(x + 2) = 0$ → $x = 7$ or $x = -2$

**Example 2.** $3x^2 = 12x$ → $3x^2 - 12x = 0$ → $3x(x - 4) = 0$ → $x = 0$ or $x = 4$ (don't divide by $x$, you'd lose $x = 0$)

**Example 3.** $2x^2 + 7x = 15$ → $2x^2 + 7x - 15 = 0$ → $(2x - 3)(x + 5) = 0$ → $x = \frac{3}{2}$, $-5$

**Example 4.** $4x^2 - 25 = 0$ → $x^2 = \frac{25}{4}$ → $x = \pm\frac{5}{2}$

**Example 5.** $(x - 3)^2 = 7$ → $x = 3 \pm \sqrt{7}$

**Example 6 (area).** A rectangle's length is 4 m more than its width; area 60 m².
- $w(w + 4) = 60$ → $w^2 + 4w - 60 = 0$ → $(w + 10)(w - 6) = 0$ → $w = 6$ (reject $-10$). Dimensions 6 × 10.

## 5. Common mistakes
- Using the zero product property when the right side isn't 0.
- Dividing both sides by $x$ and losing the solution $x = 0$.
- Forgetting $\pm$ with square roots.

## 6. Practice
Q: Solve $x^2 + 3x - 10 = 0$.
A: $x = 2$ or $x = -5$.

Q: Solve $x^2 = 7x$.
A: $x = 0$ or $x = 7$.

Q: Solve $(x + 1)^2 = 16$.
A: $x = 3$ or $x = -5$.

Q: Solve $5x^2 - 45 = 0$.
A: $x = \pm 3$.

Q: Two consecutive positive integers have product 132. Find them.
A: $n(n + 1) = 132$ → 11 and 12.

@@ 3 | TOPIC | Completing the square and vertex form | Turning ax² + bx + c into a(x − h)² + k; solving; reading the vertex | MUST
# Completing the square and vertex form

## 1. What it covers
- **Completing the square**: rewriting $x^2 + bx$ as $\left(x + \frac{b}{2}\right)^2 - \left(\frac{b}{2}\right)^2$.
- Converting to **vertex form** $y = a(x - h)^2 + k$.
- Solving any quadratic by completing the square.
- The link to transformations.

## 2. Where it is used
- **Deriving the quadratic formula** (next topic).
- **Finding max/min** without calculus.
- **Circles and conics:** $x^2 + y^2 - 6x + 4y = 3$ becomes $(x - 3)^2 + (y + 2)^2 = 16$ (Geometry roadmap).
- **Statistics:** the normal distribution's formula comes from completing the square; optimisation in ML too.

## 3. What it means
Picture $x^2 + 6x$ as an $x$-by-$x$ square plus a $6$-by-$x$ strip. Cut the strip in half (two $3$-by-$x$ pieces) and attach them to two sides of the square. You almost have a bigger square of side $x + 3$, missing only a $3 \times 3$ corner. So
$$
x^2 + 6x = (x + 3)^2 - 9
$$
That's "completing the square": add and subtract $\left(\frac{b}{2}\right)^2$.

**Vertex form** $a(x - h)^2 + k$ shows the vertex $(h, k)$ directly: it's $x^2$ stretched by $a$, moved right $h$ and up $k$.

**If $a \ne 1$:** factor $a$ out of the $x^2$ and $x$ terms first.

## 4. Formula & worked example
$$
x^2 + bx = \left(x + \frac{b}{2}\right)^2 - \frac{b^2}{4} \qquad y = a(x - h)^2 + k \text{ has vertex } (h, k)
$$

**Example 1.** Vertex form of $y = x^2 - 8x + 10$.
- $\left(\frac{-8}{2}\right)^2 = 16$: $y = (x^2 - 8x + 16) - 16 + 10 = (x - 4)^2 - 6$ → vertex $(4, -6)$

**Example 2 ($a \ne 1$).** $y = 2x^2 + 12x + 7$
- $2(x^2 + 6x) + 7 = 2[(x + 3)^2 - 9] + 7 = 2(x + 3)^2 - 11$ → vertex $(-3, -11)$

**Example 3 (solve).** $x^2 + 6x - 4 = 0$
- $(x + 3)^2 - 9 - 4 = 0$ → $(x + 3)^2 = 13$ → $x = -3 \pm \sqrt{13}$

**Example 4 (odd b).** $x^2 + 5x + 1 = 0$
- $\left(x + \frac{5}{2}\right)^2 - \frac{25}{4} + 1 = 0$ → $\left(x + \frac{5}{2}\right)^2 = \frac{21}{4}$ → $x = \frac{-5 \pm \sqrt{21}}{2}$

## 5. Common mistakes
- Adding $\left(\frac{b}{2}\right)^2$ without subtracting it (or without adding to both sides).
- Forgetting to multiply the subtracted number by $a$ when $a$ was factored out.
- Reading $(x + 3)^2$ as $h = 3$. It's $h = -3$.

## 6. Practice
Q: Complete the square: $x^2 + 10x$.
A: $(x + 5)^2 - 25$.

Q: Vertex form of $y = x^2 - 2x + 5$?
A: $(x - 1)^2 + 4$.

Q: Solve $x^2 - 4x - 1 = 0$ by completing the square.
A: $(x - 2)^2 = 5$ → $x = 2 \pm \sqrt{5}$.

Q: Vertex of $y = -3(x - 2)^2 + 7$?
A: $(2, 7)$, a maximum.

Q: Vertex form of $y = 3x^2 - 6x + 1$?
A: $3(x - 1)^2 - 2$.

@@ 3 | TOPIC | The quadratic formula and the discriminant | Deriving and using the formula; b² − 4ac and the number and type of solutions | MUST
# The quadratic formula and the discriminant

## 1. What it covers
- The **quadratic formula**, which solves **every** quadratic.
- Where it comes from (completing the square on $ax^2 + bx + c = 0$).
- The **discriminant** $\Delta = b^2 - 4ac$: two, one or no real solutions.
- Choosing the best solving method.

## 2. Where it is used
- **Physics:** time of flight, collisions, stopping distances.
- **Computer graphics:** ray tracing solves a quadratic per ray per object; the discriminant tells you instantly whether the ray hits the sphere.
- **Finance:** solving for an interest rate over two periods.
- **Engineering:** circuit and vibration analysis (complex roots → oscillation).

## 3. What it means
Completing the square on $ax^2 + bx + c = 0$ in general gives one formula that always works, even when factoring is impossible.

The **discriminant** is the part under the square root:
- $\Delta > 0$: two different real solutions (the parabola crosses the x-axis twice).
- $\Delta = 0$: one repeated solution (it touches the x-axis at its vertex).
- $\Delta < 0$: no real solutions (it misses the x-axis); there are two **complex** solutions (later topic).
- If $\Delta$ is a perfect square, the quadratic factors nicely over the integers.

## 4. Formula & worked example
$$
ax^2 + bx + c = 0 \ \Rightarrow\ x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}
$$

**Derivation (sketch):** divide by $a$: $x^2 + \frac{b}{a}x = -\frac{c}{a}$. Complete the square: $\left(x + \frac{b}{2a}\right)^2 = \frac{b^2}{4a^2} - \frac{c}{a} = \frac{b^2 - 4ac}{4a^2}$. Take roots: $x + \frac{b}{2a} = \pm\frac{\sqrt{b^2 - 4ac}}{2a}$.

**Example 1.** $2x^2 - 7x + 3 = 0$
- $\Delta = 49 - 24 = 25$ → $x = \frac{7 \pm 5}{4}$ → $x = 3$ or $x = \frac{1}{2}$

**Example 2.** $x^2 + 4x - 1 = 0$
- $\Delta = 16 + 4 = 20$ → $x = \frac{-4 \pm \sqrt{20}}{2} = -2 \pm \sqrt{5}$

**Example 3.** How many real roots: $3x^2 - 2x + 5 = 0$?
- $\Delta = 4 - 60 = -56 < 0$ → none.

**Example 4.** For which $k$ does $x^2 + kx + 9 = 0$ have exactly one solution?
- $\Delta = k^2 - 36 = 0$ → $k = \pm 6$

**Example 5 (code with a graphics flavour).**
```python
import math
def solve_quadratic(a, b, c):
    d = b * b - 4 * a * c          # discriminant
    if d < 0:
        return []                  # no real roots: the ray misses
    r = math.sqrt(d)
    return sorted({(-b - r) / (2 * a), (-b + r) / (2 * a)})
print(solve_quadratic(2, -7, 3))   # [0.5, 3.0]
```
The discriminant is checked first; a negative value means no real solution (in a ray tracer: no hit, so skip the expensive work). The set `{...}` removes the duplicate root when $d = 0$.

## 5. Common mistakes
- Writing the formula with only $-b$ over $2a$ (the whole numerator is over $2a$).
- Sign errors when $b$ or $c$ is negative: with $b = -7$, $-b = 7$ and $b^2 = 49$.
- Forgetting to rewrite the equation in the form $ax^2 + bx + c = 0$ first.

## 6. Practice
Q: Solve $x^2 - 2x - 8 = 0$ with the formula.
A: $x = \frac{2 \pm 6}{2}$ → $4$ or $-2$.

Q: Discriminant of $4x^2 + 4x + 1$, and what it means?
A: $0$: one repeated root ($x = -\frac{1}{2}$).

Q: Solve $3x^2 + x - 1 = 0$.
A: $x = \frac{-1 \pm \sqrt{13}}{6}$.

Q: How many real solutions does $x^2 + x + 3 = 0$ have?
A: None ($\Delta = -11$).

Q: For what $c$ does $x^2 - 6x + c = 0$ have two real roots?
A: $36 - 4c > 0$ → $c < 9$.

@@ 3 | TOPIC | Quadratic word problems | Projectiles, area, revenue and profit, number problems | MUST
# Quadratic word problems

## 1. What it covers
- Setting up quadratic models from descriptions.
- Answering "when", "how big", and "maximum/minimum" questions.
- Rejecting solutions that don't make sense in context.

## 2. Where it is used
- **Physics:** projectile height $h = -\frac{1}{2}gt^2 + v_0t + h_0$ (use $-4.9t^2$ in metres).
- **Business:** revenue = price × quantity, where quantity falls as price rises.
- **Design:** maximising area with a fixed amount of fencing.
- **Sports analytics:** shot trajectories.

## 3. What it means
Two kinds of questions:
- **"When/where is it equal to ...?"** → set the quadratic equal to the value and **solve** (formula or factoring).
- **"What is the maximum/minimum?"** → find the **vertex**.

Always check: negative times, negative lengths or prices usually get rejected.

## 4. Formula & worked example
**Example 1 (projectile).** A ball is thrown up from 2 m at 15 m/s: $h = -4.9t^2 + 15t + 2$.
- Max: $t = \frac{15}{9.8} \approx 1.53$ s; $h \approx -4.9(2.343) + 22.96 + 2 \approx 13.48$ m
- Lands ($h = 0$): $t = \frac{-15 - \sqrt{225 + 39.2}}{-9.8} = \frac{15 + 16.25}{9.8} \approx 3.19$ s (reject the negative root)

**Example 2 (fencing).** 100 m of fence for three sides of a rectangle along a wall. Maximise the area.
- Width $x$ (two sides), length $100 - 2x$: $A = x(100 - 2x) = -2x^2 + 100x$
- Vertex $x = 25$ → $A = 25 \times 50 = 1250$ m²

**Example 3 (revenue).** At 300 taka a café sells 200 cups a day. Each 10-taka increase loses 5 cups. Best price?
- $n$ increases: price $300 + 10n$, cups $200 - 5n$
- $R = (300 + 10n)(200 - 5n) = -50n^2 + 500n + 60\,000$
- Vertex $n = 5$ → price 350 taka, $R = 350 \times 175 = 61\,250$

**Example 4 (frame).** A 20 × 30 cm photo has a frame of uniform width $x$; the total area (photo + frame) is 1200 cm².
- $(20 + 2x)(30 + 2x) = 1200$ → $4x^2 + 100x + 600 = 1200$ → $x^2 + 25x - 150 = 0$
- $(x + 30)(x - 5) = 0$ → $x = 5$ cm (reject $-30$)

## 5. Common mistakes
- Keeping a negative time or length.
- Answering with the input at the vertex when the question asks for the maximum value (or vice versa).
- Setting up revenue as price + quantity instead of price × quantity.

## 6. Practice
Q: $h = -5t^2 + 40t$. When does the object land?
A: $t = 8$ s.

Q: 60 m of fencing encloses a rectangle (all four sides). Maximum area?
A: A 15 × 15 square: 225 m².

Q: $P(x) = -2x^2 + 120x - 1000$. Maximum profit?
A: At $x = 30$: $P = 800$.

Q: The product of a number and 3 more than it is 40. Positive number?
A: $x(x + 3) = 40$ → $x = 5$.

Q: When is $h = -5t^2 + 20t + 25$ equal to 40?
A: $-5t^2 + 20t - 15 = 0$ → $t = 1$ s and $t = 3$ s.

@@ 2 | MODULE | Complex numbers | The imaginary unit, arithmetic, conjugates, the complex plane, complex roots | MUST
When the discriminant is negative, the solutions live in a bigger number system: the complex numbers.

@@ 3 | TOPIC | The imaginary unit and complex arithmetic | i = √−1, powers of i, adding, subtracting and multiplying complex numbers | MUST
# The imaginary unit and complex arithmetic

## 1. What it covers
- The **imaginary unit** $i$ with $i^2 = -1$.
- Simplifying $\sqrt{-k}$.
- The cycle of powers of $i$.
- Complex numbers $a + bi$: adding, subtracting, multiplying.

## 2. Where it is used
- **Electrical engineering:** AC circuits use complex impedance ($Z = R + iX$; engineers write $j$ for $i$).
- **Signal processing:** the Fourier transform (audio, images, MP3, JPEG, 5G) is built on complex numbers.
- **Quantum computing:** quantum states have complex amplitudes.
- **Graphics:** complex multiplication rotates points in 2D; fractals like the Mandelbrot set.

## 3. What it means
No real number squares to $-1$. Mathematicians **invented** $i$ with the rule $i^2 = -1$, and it turned out to be enormously useful, just as negative numbers once seemed strange but became essential.

A **complex number** $a + bi$ has a **real part** $a$ and an **imaginary part** $b$. Treat $i$ like a variable when adding and multiplying, then replace $i^2$ with $-1$.

**Powers of $i$ repeat every 4:** $i, -1, -i, 1, i, -1, \ldots$ To find $i^n$, divide $n$ by 4 and use the remainder.

## 4. Formula & worked example
$$
i^2 = -1 \qquad \sqrt{-k} = i\sqrt{k}\ (k > 0) \qquad i^1 = i,\ i^2 = -1,\ i^3 = -i,\ i^4 = 1
$$

**Example 1.** $\sqrt{-49} = 7i$; $\sqrt{-12} = 2i\sqrt{3}$

**Example 2.** $i^{27}$: $27 = 4 \times 6 + 3$ → $i^3 = -i$

**Example 3.** $(3 + 5i) + (2 - 8i) = 5 - 3i$

**Example 4.** $(4 - 2i) - (-1 + 6i) = 5 - 8i$

**Example 5.** $(2 + 3i)(4 - i) = 8 - 2i + 12i - 3i^2 = 8 + 10i + 3 = 11 + 10i$

**Example 6.** $(1 + i)^2 = 1 + 2i + i^2 = 2i$

**In code:**
```python
z = complex(2, 3)        # 2 + 3j  (Python uses j for i)
w = 4 - 1j
print(z * w)             # (11+10j)
print(1j ** 2)           # (-1+0j)
```
Python has complex numbers built in; `1j` is $i$. The product matches Example 5.

## 5. Common mistakes
- Writing $i^2 = 1$.
- $\sqrt{-4} \cdot \sqrt{-9} = \sqrt{36} = 6$ ✗. Convert to $i$ first: $2i \cdot 3i = 6i^2 = -6$.
- Forgetting to simplify $i^2$ after multiplying.

## 6. Practice
Q: Simplify $\sqrt{-50}$.
A: $5i\sqrt{2}$.

Q: $i^{42}$?
A: $42 = 40 + 2$ → $i^2 = -1$.

Q: $(6 - 4i) + (-2 + 9i)$?
A: $4 + 5i$.

Q: $(3 - i)(3 + 2i)$?
A: $9 + 6i - 3i - 2i^2 = 11 + 3i$.

Q: $(2i)^3$?
A: $8i^3 = -8i$.

@@ 3 | TOPIC | Conjugates, division and the complex plane | Complex conjugates, dividing complex numbers, plotting, modulus, complex roots of quadratics | MUST
# Conjugates, division and the complex plane

## 1. What it covers
- The **conjugate** $\overline{a + bi} = a - bi$ and why $z\bar{z}$ is real.
- **Dividing** complex numbers (multiply by the conjugate).
- The **complex plane** and the **modulus** $\lvert z\rvert$.
- Quadratics with **complex solutions** (negative discriminant).

## 2. Where it is used
- **Electrical engineering:** dividing voltage by complex impedance to get current.
- **Signal processing:** the magnitude (modulus) of a Fourier coefficient is how strong a frequency is (audio spectrum analysers display it).
- **Control systems:** complex roots mean oscillation; their position in the plane decides stability.
- **Graphics:** points as complex numbers.

## 3. What it means
**Conjugate:** flip the sign of the imaginary part. Multiplying a number by its conjugate removes $i$:
$$
(a + bi)(a - bi) = a^2 - b^2i^2 = a^2 + b^2
$$
That's the trick for division: multiply top and bottom by the conjugate of the bottom, like rationalising a denominator.

**Complex plane:** plot $a + bi$ as the point $(a, b)$: real part across, imaginary part up. The **modulus** $\lvert a + bi\rvert = \sqrt{a^2 + b^2}$ is its distance from the origin (Pythagoras).

**Complex roots:** when $b^2 - 4ac < 0$, the quadratic formula gives $\sqrt{\text{negative}}$, which is now allowed. The two roots are conjugates of each other.

## 4. Formula & worked example
$$
\frac{a + bi}{c + di} = \frac{(a + bi)(c - di)}{c^2 + d^2} \qquad \lvert a + bi\rvert = \sqrt{a^2 + b^2}
$$

**Example 1.** $(3 + 4i)(3 - 4i) = 9 + 16 = 25$

**Example 2.** $\dfrac{5 + i}{2 - 3i}$
- $\times \frac{2 + 3i}{2 + 3i}$: top $= 10 + 15i + 2i + 3i^2 = 7 + 17i$; bottom $= 4 + 9 = 13$ → $\frac{7}{13} + \frac{17}{13}i$

**Example 3.** $\frac{4}{i}$: multiply by $\frac{-i}{-i}$: $\frac{-4i}{1} = -4i$

**Example 4.** Modulus of $-6 + 8i$: $\sqrt{36 + 64} = 10$

**Example 5 (complex roots).** $x^2 - 4x + 13 = 0$
- $\Delta = 16 - 52 = -36$ → $x = \frac{4 \pm 6i}{2} = 2 \pm 3i$

**Example 6.** Check: $(2 + 3i)^2 - 4(2 + 3i) + 13 = (4 + 12i - 9) - 8 - 12i + 13 = 0$ ✓

## 5. Common mistakes
- Changing the sign of the real part for the conjugate.
- Multiplying only the denominator by the conjugate.
- Writing the modulus as $a^2 + b^2$ without the square root.

## 6. Practice
Q: Conjugate of $-7 + 2i$?
A: $-7 - 2i$.

Q: $\frac{2 + i}{1 - i}$?
A: $\frac{(2 + i)(1 + i)}{2} = \frac{1 + 3i}{2}$.

Q: $\lvert 5 - 12i\rvert$?
A: $13$.

Q: Solve $x^2 + 2x + 5 = 0$.
A: $x = -1 \pm 2i$.

Q: Why is $z \cdot \bar{z}$ always real?
A: $(a + bi)(a - bi) = a^2 + b^2$; the imaginary parts cancel.

@@ 2 | MODULE | Higher-degree polynomials | Division, remainder and factor theorems, finding zeros, graphs, binomial theorem | SHOULD
Everything you learned for quadratics extends to polynomials of any degree.

@@ 3 | TOPIC | Polynomial division, remainder and factor theorems | Long division, synthetic division, the remainder and factor theorems | SHOULD
# Polynomial division, remainder and factor theorems

## 1. What it covers
- **Polynomial long division.**
- **Synthetic division** (fast division by $x - c$).
- The **remainder theorem**: the remainder of $p(x) \div (x - c)$ is $p(c)$.
- The **factor theorem**: $(x - c)$ is a factor exactly when $p(c) = 0$.

## 2. Where it is used
- **Error detection:** CRC checksums (used in Ethernet, ZIP files, USB) are remainders of polynomial division.
- **Factoring cubics and quartics** to solve equations.
- **Calculus:** simplifying rational functions and finding slant asymptotes.
- **Evaluating polynomials efficiently:** synthetic division is Horner's method.

## 3. What it means
Polynomial division works like long division of numbers: divide the leading terms, multiply, subtract, bring down, repeat. The result:
$$
p(x) = (\text{divisor}) \times q(x) + r(x)
$$

**Remainder theorem:** if you divide by $(x - c)$, then putting $x = c$ makes the divisor 0, so $p(c) = r$. Evaluating is the same as finding the remainder.

**Factor theorem:** if $p(c) = 0$, the remainder is 0, so $(x - c)$ divides evenly: it is a factor, and $c$ is a root.

**Synthetic division** is a shortcut that only writes the coefficients.

## 4. Formula & worked example
$$
p(x) = (x - c)\,q(x) + p(c)
$$

**Example 1 (long division).** $(2x^3 + 3x^2 - 5x + 6) \div (x + 2)$
- $2x^3 \div x = 2x^2$; $2x^2(x + 2) = 2x^3 + 4x^2$; subtract → $-x^2 - 5x$
- $-x^2 \div x = -x$; $-x(x + 2) = -x^2 - 2x$; subtract → $-3x + 6$
- $-3x \div x = -3$; $-3(x + 2) = -3x - 6$; subtract → $12$
- Result: $2x^2 - x - 3$ remainder $12$

**Example 2 (synthetic, same problem, $c = -2$).**
```text
-2 |  2    3   -5    6
   |      -4    2    6
   -------------------
      2   -1   -3 | 12
```
Bring down the 2; multiply by $-2$ and add to the next column, repeat. The last number (12) is the remainder; the others are the quotient's coefficients: $2x^2 - x - 3$.

**Example 3 (remainder theorem).** Remainder of $x^4 - 3x + 1$ divided by $x - 2$?
- $p(2) = 16 - 6 + 1 = 11$

**Example 4 (factor theorem).** Is $(x - 1)$ a factor of $x^3 - 6x^2 + 11x - 6$?
- $p(1) = 1 - 6 + 11 - 6 = 0$ ✓. Dividing gives $x^2 - 5x + 6 = (x - 2)(x - 3)$, so $p(x) = (x - 1)(x - 2)(x - 3)$.

## 5. Common mistakes
- Forgetting placeholder zeros for missing powers (e.g. $x^3 + 0x^2 - 4x + 1$).
- Using $+2$ instead of $-2$ in synthetic division for the divisor $x + 2$.
- Subtraction sign errors in long division.

## 6. Practice
Q: Remainder of $x^3 + 2x - 5$ divided by $x - 1$?
A: $p(1) = -2$.

Q: Is $(x + 3)$ a factor of $x^2 + x - 6$?
A: Yes, $p(-3) = 9 - 3 - 6 = 0$.

Q: Divide $x^2 + 5x + 6$ by $x + 2$.
A: $x + 3$, remainder 0.

Q: Divide $x^3 - 8$ by $x - 2$.
A: $x^2 + 2x + 4$.

Q: What does the factor theorem say?
A: $(x - c)$ is a factor of $p(x)$ if and only if $p(c) = 0$.

@@ 3 | TOPIC | Finding zeros: rational root theorem and higher-degree equations | Candidate roots p/q, factoring cubics and quartics, multiplicity, fundamental theorem of algebra | SHOULD
# Finding zeros: rational root theorem and higher-degree equations

## 1. What it covers
- The **rational root theorem**: listing possible rational roots $\frac{p}{q}$.
- A strategy for factoring cubics and quartics completely.
- **Multiplicity** of roots.
- The **fundamental theorem of algebra**: a degree-$n$ polynomial has exactly $n$ complex roots (counting multiplicity).

## 2. Where it is used
- **Engineering:** finding the natural frequencies of a system (roots of a characteristic polynomial).
- **Control theory:** stability depends on where the roots are.
- **Optimisation and calculus:** setting a derivative (a polynomial) to zero.
- **Numerical software:** `numpy.roots` finds all roots, including complex ones.

## 3. What it means
**Rational root theorem:** if a polynomial with integer coefficients has a rational root $\frac{p}{q}$ (in lowest terms), then $p$ divides the **constant term** and $q$ divides the **leading coefficient**. This gives a short list of candidates to test.

**Strategy:**
1. List candidates $\pm\frac{p}{q}$.
2. Test them ($p(c) = 0$?) until one works.
3. Divide by $(x - c)$ to get a lower-degree polynomial.
4. Repeat, or use the quadratic formula when you reach degree 2.

**Multiplicity:** in $(x - 2)^3(x + 1)$, the root 2 has multiplicity 3. Counting with multiplicity, a degree-$n$ polynomial has exactly $n$ roots in the complex numbers (some may be complex, coming in conjugate pairs).

## 4. Formula & worked example
$$
a_nx^n + \cdots + a_0:\ \text{rational roots} \in \left\{\pm\frac{p}{q} : p \mid a_0,\ q \mid a_n\right\}
$$

**Example 1.** Solve $x^3 - 2x^2 - 5x + 6 = 0$.
- Candidates: $\pm 1, \pm 2, \pm 3, \pm 6$. $p(1) = 1 - 2 - 5 + 6 = 0$ ✓
- Divide: $x^2 - x - 6 = (x - 3)(x + 2)$ → roots $1, 3, -2$

**Example 2.** Solve $2x^3 - 3x^2 - 11x + 6 = 0$.
- Candidates: $\pm 1, \pm 2, \pm 3, \pm 6, \pm\frac{1}{2}, \pm\frac{3}{2}$. $p(3) = 54 - 27 - 33 + 6 = 0$ ✓
- Divide: $2x^2 + 3x - 2 = (2x - 1)(x + 2)$ → roots $3, \frac{1}{2}, -2$

**Example 3 (complex roots).** $x^3 - 1 = 0$
- $(x - 1)(x^2 + x + 1) = 0$ → $x = 1$ or $x = \frac{-1 \pm i\sqrt{3}}{2}$ (3 roots, as the theorem promises)

**Example 4 (quartic, quadratic form).** $x^4 - 13x^2 + 36 = 0$
- $(x^2 - 4)(x^2 - 9) = 0$ → $x = \pm 2, \pm 3$

## 5. Common mistakes
- Forgetting negative candidates.
- Swapping $p$ and $q$ (numerator from the constant term, denominator from the leading coefficient).
- Expecting every polynomial to have rational roots; many don't (then use numerical methods).

## 6. Practice
Q: Possible rational roots of $3x^3 + x - 2$?
A: $\pm 1, \pm 2, \pm\frac{1}{3}, \pm\frac{2}{3}$.

Q: Solve $x^3 - 7x + 6 = 0$.
A: $p(1) = 0$ → $(x - 1)(x^2 + x - 6)$ → $1, 2, -3$.

Q: How many complex roots does $x^5 + 3x - 1$ have?
A: $5$ (counting multiplicity).

Q: Multiplicity of the root $-1$ in $(x + 1)^2(x - 4)$?
A: $2$.

Q: If $3 + 2i$ is a root of a real polynomial, what else is?
A: Its conjugate $3 - 2i$.

@@ 3 | TOPIC | Graphs of polynomial functions | End behavior, zeros and multiplicity, turning points, sketching | SHOULD
# Graphs of polynomial functions

## 1. What it covers
- **End behaviour** from the degree and leading coefficient.
- Behaviour at zeros: **cross** (odd multiplicity) or **touch/bounce** (even multiplicity).
- A degree-$n$ polynomial has at most $n - 1$ turning points.
- Sketching from factored form; positive and negative intervals.

## 2. Where it is used
- **Data fitting:** choosing a polynomial degree that fits data without wild ends (overfitting shows up as crazy end behaviour).
- **Engineering curves:** splines in CAD and animation are pieces of cubic polynomials.
- **Calculus preview:** where a function is positive or negative, increasing or decreasing.

## 3. What it means
**End behaviour** (what happens far left and far right) is controlled by the leading term $a_nx^n$, because for huge $x$ it dominates everything else.
- **Even degree:** both ends go the same way (up if $a_n > 0$, like $x^2$; down if $a_n < 0$).
- **Odd degree:** ends go opposite ways (down-left/up-right if $a_n > 0$, like $x^3$).

**At a zero** with multiplicity $m$:
- $m$ odd → the graph **crosses** the x-axis ($m = 1$ straight through; $m = 3$ flattens like $x^3$).
- $m$ even → the graph **touches** and turns back (bounces, like $x^2$).

**Sketching:** plot the zeros, decide cross/touch at each, use end behaviour, add the y-intercept.

## 4. Formula & worked example
**Example 1.** End behaviour of $f(x) = -2x^5 + 3x^2$: odd degree, negative leading coefficient → up on the left, down on the right.

**Example 2.** Sketch $f(x) = (x + 2)(x - 1)^2$.
- Degree 3, leading coefficient +1 → starts low (left), ends high (right).
- Zero $-2$: crosses; zero $1$ (multiplicity 2): touches.
- y-intercept $f(0) = 2 \cdot 1 = 2$.
- Path: up from the bottom left, cross at $-2$, rise to a local max, come down to touch at $x = 1$, then rise.

**Example 3.** Positive/negative intervals of $f(x) = x(x - 3)(x + 1)$.
- Zeros $-1, 0, 3$ (all cross). Degree 3, positive → negative on $(-\infty, -1)$, positive on $(-1, 0)$, negative on $(0, 3)$, positive on $(3, \infty)$.

**Example 4.** Maximum number of turning points of a degree-4 polynomial? → 3.

**Example 5 (write from a graph).** Zeros at $-3$ (cross), $2$ (bounce), degree 3, passing through $(0, -12)$.
- $f(x) = a(x + 3)(x - 2)^2$; $f(0) = a \cdot 3 \cdot 4 = 12a = -12$ → $a = -1$.

## 5. Common mistakes
- Using the first term written instead of the **highest-degree** term for end behaviour.
- Drawing a crossing at an even-multiplicity zero.
- Drawing more turning points than $n - 1$.

## 6. Practice
Q: End behaviour of $f(x) = 4x^6 - x$?
A: Both ends up.

Q: At $x = 5$, does $f(x) = (x - 5)^2(x + 1)$ cross or touch?
A: Touch.

Q: Maximum turning points of a degree-5 polynomial?
A: $4$.

Q: Where is $f(x) = (x - 1)(x - 4)$ negative?
A: $(1, 4)$.

Q: End behaviour of $f(x) = -x^3 + 2x$?
A: Up on the left, down on the right.

@@ 3 | TOPIC | Pascal's triangle and the binomial theorem | Expanding (a + b)ⁿ, binomial coefficients, finding a specific term | SHOULD
# Pascal's triangle and the binomial theorem

## 1. What it covers
- **Pascal's triangle** and how each row is built.
- The **binomial theorem** for expanding $(a + b)^n$.
- **Binomial coefficients** $\binom{n}{k} = \frac{n!}{k!(n - k)!}$.
- Finding one specific term without expanding everything.

## 2. Where it is used
- **Probability:** the binomial distribution (chance of $k$ successes in $n$ trials, e.g. 3 heads in 10 coin flips) uses $\binom{n}{k}$.
- **Combinatorics:** $\binom{n}{k}$ counts ways to choose $k$ items from $n$ (lottery odds, team selection).
- **Approximations:** $(1 + x)^n \approx 1 + nx$ for small $x$ (quick estimates in finance and physics).
- **Algorithms:** dynamic programming builds Pascal's triangle to count paths.

## 3. What it means
Expanding $(a + b)^n$ by hand gets painful fast. The pattern:
- The powers of $a$ go **down** from $n$ to 0, the powers of $b$ go **up** from 0 to $n$.
- The **coefficients** come from row $n$ of Pascal's triangle.

**Pascal's triangle:** start with 1; each number is the **sum of the two above it**.
```text
            1               row 0
          1   1             row 1
        1   2   1           row 2
      1   3   3   1         row 3
    1   4   6   4   1       row 4
  1   5  10  10   5   1     row 5
```
Each entry equals $\binom{n}{k}$, the number of ways to choose which $k$ of the $n$ brackets contribute a $b$.

## 4. Formula & worked example
$$
(a + b)^n = \sum_{k=0}^{n} \binom{n}{k} a^{n-k} b^k \qquad \binom{n}{k} = \frac{n!}{k!\,(n-k)!}
$$
The $(k + 1)$-th term is $\binom{n}{k}a^{n-k}b^k$.

**Example 1.** $(x + 2)^4$ with row 4 (1, 4, 6, 4, 1):
- $x^4 + 4x^3(2) + 6x^2(4) + 4x(8) + 16 = x^4 + 8x^3 + 24x^2 + 32x + 16$

**Example 2.** $(2x - 1)^3$ with row 3 (1, 3, 3, 1):
- $(2x)^3 + 3(2x)^2(-1) + 3(2x)(1) + (-1)^3 = 8x^3 - 12x^2 + 6x - 1$

**Example 3.** $\binom{7}{3} = \frac{7!}{3!\,4!} = \frac{7 \cdot 6 \cdot 5}{6} = 35$

**Example 4 (one term).** The $x^3$ term of $(x + 3)^8$.
- Need $a^{n-k} = x^3$ → $k = 5$: $\binom{8}{5}x^3 \cdot 3^5 = 56 \cdot 243\,x^3 = 13\,608x^3$

**Example 5 (approximation).** $1.02^{10} \approx 1 + 10(0.02) + \binom{10}{2}(0.02)^2 = 1 + 0.2 + 0.018 = 1.218$ (true $1.2190$).

## 5. Common mistakes
- Forgetting to raise the whole term to the power: $(2x)^3 = 8x^3$, not $2x^3$.
- Dropping negative signs: $(-1)^k$ alternates.
- Using row $n - 1$ instead of row $n$ (the top row is row 0).

## 6. Practice
Q: Row 5 of Pascal's triangle?
A: $1, 5, 10, 10, 5, 1$.

Q: Expand $(x + 1)^3$.
A: $x^3 + 3x^2 + 3x + 1$.

Q: $\binom{6}{2}$?
A: $15$.

Q: Coefficient of $x^2$ in $(x - 2)^5$?
A: $k = 3$: $\binom{5}{3}(-2)^3 = 10 \cdot (-8) = -80$.

Q: How many terms does $(a + b)^{12}$ have?
A: $13$.

@@ 3 | TOPIC | Practice & review: Polynomials, quadratics and complex numbers | Mixed problem set with worked answers | MUST
# Practice & review: Polynomials, quadratics and complex numbers

**How to use this:** 60 minutes, no calculator.

## Problems
1. Expand and simplify $(2x - 3)^2 - (x + 4)(x - 4)$.
2. Factor completely: (a) $3x^3 - 12x$, (b) $6x^2 - x - 15$, (c) $x^3 - 3x^2 - 4x + 12$.
3. Find the vertex, zeros and y-intercept of $y = -x^2 + 2x + 8$.
4. Write $y = 2x^2 - 8x + 3$ in vertex form.
5. Solve $3x^2 - 5x - 1 = 0$ and state the discriminant.
6. Solve $x^2 + 6x + 13 = 0$.
7. Simplify $\frac{3 - 2i}{1 + 4i}$ and $i^{103}$.
8. Find all roots of $x^3 + x^2 - 10x + 8 = 0$.
9. Describe the end behaviour and zeros (cross/touch) of $f(x) = -(x - 2)^2(x + 3)$.
10. Find the $x^4$ term in $(2x + 1)^6$.
11. A rectangular garden's length is 3 m more than twice its width; area 65 m². Dimensions?

## Answers
1. $4x^2 - 12x + 9 - x^2 + 16 = 3x^2 - 12x + 25$.
2. (a) $3x(x + 2)(x - 2)$; (b) $(3x - 5)(2x + 3)$; (c) $x^2(x - 3) - 4(x - 3) = (x - 3)(x + 2)(x - 2)$.
3. Vertex $(1, 9)$; zeros: $-(x - 4)(x + 2) = 0$ → $4, -2$; y-intercept $(0, 8)$.
4. $2(x - 2)^2 - 5$.
5. $\Delta = 25 + 12 = 37$; $x = \frac{5 \pm \sqrt{37}}{6}$.
6. $x = -3 \pm 2i$.
7. $\frac{(3 - 2i)(1 - 4i)}{17} = \frac{3 - 12i - 2i - 8}{17} = \frac{-5 - 14i}{17}$; $103 = 100 + 3$ → $i^3 = -i$.
8. $p(1) = 0$ → $(x - 1)(x^2 + 2x - 8) = (x - 1)(x + 4)(x - 2)$ → $1, -4, 2$.
9. Degree 3, leading coefficient $-1$ → up on the left, down on the right. Touches at 2, crosses at $-3$.
10. $k = 2$: $\binom{6}{2}(2x)^4(1)^2 = 15 \cdot 16x^4 = 240x^4$.
11. $w(2w + 3) = 65$ → $2w^2 + 3w - 65 = 0$ → $(2w + 13)(w - 5) = 0$ → $w = 5$, length 13 m.

## Flashcards
Q: Quadratic formula?
A: $x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}$.

Q: Vertex of $y = ax^2 + bx + c$?
A: $x = -\frac{b}{2a}$.

Q: Factor $a^2 - b^2$.
A: $(a + b)(a - b)$.

Q: Remainder theorem?
A: The remainder of $p(x) \div (x - c)$ is $p(c)$.

Q: Even multiplicity zero: cross or touch?
A: Touch.
