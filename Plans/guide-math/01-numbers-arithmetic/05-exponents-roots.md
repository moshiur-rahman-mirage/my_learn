@@ 1 | MODULE | Phase 5 — Exponents, Roots and Real Numbers (Core) | Exponent rules, zero and negative exponents, scientific notation, square and cube roots, irrational numbers | MUST
# Phase 5: Exponents, roots and real numbers

This phase finishes the number toolkit you need for algebra: the **laws of exponents**, **scientific notation** for very large and very small numbers, and **roots**, including the irrational numbers like $\sqrt{2}$ that fill the gaps in the number line.

@@ 2 | MODULE | Powers and roots | Exponent laws, scientific notation, roots, irrational numbers | MUST
Each exponent rule comes from the meaning "repeated multiplication". If you forget a rule, write out the multiplication and it comes back.

@@ 3 | TOPIC | Integer exponent rules | Product, quotient and power rules; powers of products and quotients | MUST
# Integer exponent rules

## 1. What it covers
- **Product rule:** $a^m \cdot a^n = a^{m+n}$.
- **Quotient rule:** $\frac{a^m}{a^n} = a^{m-n}$.
- **Power rule:** $(a^m)^n = a^{mn}$.
- Powers of products and quotients: $(ab)^n = a^nb^n$, $\left(\frac{a}{b}\right)^n = \frac{a^n}{b^n}$.

## 2. Where it is used
- **Algebra:** simplifying expressions like $\frac{6x^5y^2}{2x^3y}$.
- **Computing:** $2^{10} \times 2^{10} = 2^{20}$ (KB × 1024 = MB); bit shifts `1 << n` compute $2^n$.
- **Science:** combining quantities in scientific notation.
- **Algorithms:** complexity like $O(n^2)$ and $O(2^n)$; doubling the input of an $O(n^2)$ algorithm multiplies the time by $2^2 = 4$.

## 3. What it means
Write the powers out and the rules become obvious:
- $a^3 \cdot a^2 = (a \cdot a \cdot a)(a \cdot a) = a^5$. **Multiply → add exponents.**
- $\frac{a^5}{a^2} = \frac{a \cdot a \cdot a \cdot a \cdot a}{a \cdot a} = a^3$. **Divide → subtract exponents.**
- $(a^2)^3 = a^2 \cdot a^2 \cdot a^2 = a^6$. **Power of a power → multiply exponents.**

These rules work only when the **bases are the same**: $2^3 \cdot 3^2$ can't be combined into one power.

## 4. Formula & worked example
$$
a^m a^n = a^{m+n} \qquad \frac{a^m}{a^n} = a^{m-n} \qquad (a^m)^n = a^{mn} \qquad (ab)^n = a^nb^n \qquad \left(\frac{a}{b}\right)^n = \frac{a^n}{b^n}
$$

**Example 1.** $x^4 \cdot x^7 = x^{11}$

**Example 2.** $\frac{5^9}{5^6} = 5^3 = 125$

**Example 3.** $(y^3)^4 = y^{12}$

**Example 4.** $(2x^3)^4 = 2^4x^{12} = 16x^{12}$

**Example 5.** $\dfrac{12a^5b^3}{4a^2b} = 3a^3b^2$

**Example 6.** $\left(\frac{3m^2}{n}\right)^3 = \frac{27m^6}{n^3}$

## 5. Common mistakes
- Multiplying exponents in the product rule: $x^2 \cdot x^3 \ne x^6$ (it's $x^5$).
- Raising only one factor: $(2x)^3 \ne 2x^3$ (it's $8x^3$).
- Using the rules for sums: $(a + b)^2 \ne a^2 + b^2$.

## 6. Practice
Q: $a^3 \cdot a^5 \cdot a$?
A: $a^9$.

Q: $\frac{10^8}{10^3}$?
A: $10^5$.

Q: $(3x^2)^3$?
A: $27x^6$.

Q: $\frac{18x^7y^4}{6x^2y^4}$?
A: $3x^5$.

Q: Can $2^4 \cdot 3^4$ be simplified to a single power?
A: Yes, $(2 \cdot 3)^4 = 6^4$ (same exponent, so the power-of-a-product rule applies).

@@ 3 | TOPIC | Zero and negative exponents | a⁰ = 1, a⁻ⁿ = 1/aⁿ, simplifying with negative exponents | MUST
# Zero and negative exponents

## 1. What it covers
- Why $a^0 = 1$.
- Why $a^{-n} = \frac{1}{a^n}$.
- Simplifying expressions and writing answers with positive exponents.

## 2. Where it is used
- **Science and engineering:** units like $\text{m}\cdot\text{s}^{-2}$ (metres per second squared); prefixes milli $= 10^{-3}$, micro $= 10^{-6}$, nano $= 10^{-9}$.
- **Computing:** time in nanoseconds; floating-point numbers store a (possibly negative) exponent.
- **Scientific notation** for small numbers.
- **Algebra and calculus:** the power rule for derivatives works smoothly with negative exponents ($\frac{1}{x^2} = x^{-2}$).

## 3. What it means
Look at the pattern of powers of 2, dividing by 2 each step:
$2^3 = 8$, $2^2 = 4$, $2^1 = 2$, $2^0 = 1$, $2^{-1} = \frac{1}{2}$, $2^{-2} = \frac{1}{4}$.

So $a^0 = 1$ and a negative exponent means **one over** the positive power. The quotient rule agrees: $\frac{a^3}{a^3} = a^{3-3} = a^0$, and anything divided by itself is 1.

A negative exponent **does not make the number negative**: $2^{-3} = \frac{1}{8}$, a small positive number. It means "divide by $a$ that many times" instead of multiply.

## 4. Formula & worked example
$$
a^0 = 1 \ (a \ne 0) \qquad a^{-n} = \frac{1}{a^n} \qquad \frac{1}{a^{-n}} = a^n \qquad \left(\frac{a}{b}\right)^{-n} = \left(\frac{b}{a}\right)^n
$$

**Example 1.** $5^{-2} = \frac{1}{25}$

**Example 2.** $(-7)^0 = 1$, but $-7^0 = -1$.

**Example 3.** $\left(\frac{2}{3}\right)^{-3} = \left(\frac{3}{2}\right)^3 = \frac{27}{8}$

**Example 4.** Simplify $x^5 \cdot x^{-8}$ with positive exponents.
- $x^{-3} = \frac{1}{x^3}$

**Example 5.** Simplify $\dfrac{4a^{-2}b^3}{2a^3b^{-1}}$.
- $2a^{-2-3}b^{3-(-1)} = 2a^{-5}b^4 = \frac{2b^4}{a^5}$

**Example 6.** $3 \times 10^{-4} = \frac{3}{10\,000} = 0.0003$

## 5. Common mistakes
- $2^{-3} = -8$. No: $\frac{1}{8}$.
- $0^0$: it's undefined in basic algebra (many programs return 1 by convention).
- Moving a coefficient with the variable: $3x^{-2} = \frac{3}{x^2}$, not $\frac{1}{3x^2}$.

## 6. Practice
Q: $4^{-1}$?
A: $\frac{1}{4}$.

Q: $10^{-3}$ as a decimal?
A: $0.001$.

Q: $\left(\frac{1}{5}\right)^{-2}$?
A: $25$.

Q: Simplify $\frac{x^{-3}}{x^{-7}}$.
A: $x^4$.

Q: Write $\frac{5}{y^4}$ with a negative exponent.
A: $5y^{-4}$.

@@ 3 | TOPIC | Scientific notation | Writing, comparing and computing with very large and very small numbers | MUST
# Scientific notation

## 1. What it covers
- Writing numbers as $a \times 10^n$ with $1 \le a < 10$.
- Converting to and from ordinary notation.
- Comparing numbers in scientific notation.
- Multiplying, dividing, adding and subtracting them.

## 2. Where it is used
- **Science:** the speed of light $3 \times 10^8$ m/s; an atom's size $\approx 10^{-10}$ m.
- **Computing:** programming languages print `1e9` or `2.5e-6` (that's $2.5 \times 10^{-6}$); a CPU at 3 GHz does $3 \times 10^9$ cycles per second.
- **Data:** world data volume in zettabytes ($10^{21}$ bytes).
- **Economics:** national budgets in trillions.

## 3. What it means
Scientific notation writes any number as **(a number from 1 to just under 10) × (a power of 10)**. The power of 10 tells you how many places to move the decimal point: positive for big numbers, negative for small ones.
- $45\,000\,000 = 4.5 \times 10^7$ (point moved 7 left).
- $0.00062 = 6.2 \times 10^{-4}$ (point moved 4 right).

It avoids long strings of zeros and makes size comparisons instant: compare exponents first.

**Calculating:**
- **× and ÷:** do the front numbers and the powers of 10 separately (exponent rules), then adjust to $1 \le a < 10$.
- **+ and −:** first make the powers of 10 the same.

## 4. Formula & worked example
$$
N = a \times 10^n,\ 1 \le a < 10 \qquad (a \times 10^m)(b \times 10^n) = ab \times 10^{m+n}
$$

**Example 1.** $380\,000 = 3.8 \times 10^5$; $0.0000071 = 7.1 \times 10^{-6}$

**Example 2.** $(3 \times 10^4)(5 \times 10^6) = 15 \times 10^{10} = 1.5 \times 10^{11}$

**Example 3.** $\dfrac{8.4 \times 10^{-3}}{2.1 \times 10^{5}} = 4 \times 10^{-8}$

**Example 4.** $6.2 \times 10^5 + 3.5 \times 10^4$
- $= 6.2 \times 10^5 + 0.35 \times 10^5 = 6.55 \times 10^5$

**Example 5.** How long does light take to travel from the Sun ($1.5 \times 10^{11}$ m) at $3 \times 10^8$ m/s?
- $\frac{1.5 \times 10^{11}}{3 \times 10^8} = 0.5 \times 10^3 = 500$ s $\approx 8.3$ minutes

**Example 6 (code).**
```python
print(3e8 * 500)        # 150000000000.0  (that's 1.5e11)
print(f"{0.00062:.2e}") # 6.20e-04
```
`3e8` is how code writes $3 \times 10^8$. The format `:.2e` prints a number in scientific notation with two decimals.

## 5. Common mistakes
- Leaving the front number outside $[1, 10)$: $15 \times 10^{10}$ must become $1.5 \times 10^{11}$.
- Wrong sign of exponent: small numbers (less than 1) have **negative** exponents.
- Adding without first matching the exponents.

## 6. Practice
Q: $52\,000\,000$ in scientific notation?
A: $5.2 \times 10^7$.

Q: $4.06 \times 10^{-3}$ as a decimal?
A: $0.00406$.

Q: $(2 \times 10^3)(4.5 \times 10^{-7})$?
A: $9 \times 10^{-4}$.

Q: Which is bigger: $3 \times 10^{-5}$ or $9 \times 10^{-6}$?
A: $3 \times 10^{-5}$ (larger exponent).

Q: A 3 GHz CPU: how many cycles in 2 ms?
A: $3 \times 10^9 \times 2 \times 10^{-3} = 6 \times 10^6$.

@@ 3 | TOPIC | Square roots and cube roots | Evaluating roots, solving x² = p and x³ = p, simplifying square roots | MUST
# Square roots and cube roots

## 1. What it covers
- The square root $\sqrt{a}$ and cube root $\sqrt[3]{a}$.
- Solving $x^2 = p$ (two answers) and $x^3 = p$ (one answer).
- Simplifying square roots like $\sqrt{72} = 6\sqrt{2}$.

## 2. Where it is used
- **Geometry:** Pythagoras and distances ($\sqrt{x^2 + y^2}$).
- **Statistics:** the standard deviation is a square root.
- **Physics:** the time to fall a height $h$ is $\sqrt{2h/g}$.
- **Graphics and ML:** normalising vectors divides by their length $\sqrt{x^2 + y^2 + z^2}$ (games compute this millions of times per second).
- **Volume:** the side of a cube holding 1000 L is $\sqrt[3]{1 \text{ m}^3} = 1$ m.

## 3. What it means
$\sqrt{a}$ asks "**what number times itself gives $a$**?" — like finding the side of a square from its area. $\sqrt{49} = 7$ because $7^2 = 49$. The symbol $\sqrt{\ }$ means the **non-negative** root.

But the **equation** $x^2 = 49$ has two solutions, $7$ and $-7$, since $(-7)^2 = 49$ too.

$\sqrt[3]{a}$ asks "what number cubed gives $a$?" — the side of a cube from its volume. Cube roots of negatives exist: $\sqrt[3]{-8} = -2$. So $x^3 = p$ has exactly one real solution.

**Simplifying:** pull out perfect-square factors: $\sqrt{ab} = \sqrt{a}\sqrt{b}$.

## 4. Formula & worked example
$$
\sqrt{a}\sqrt{b} = \sqrt{ab} \qquad \sqrt{\frac{a}{b}} = \frac{\sqrt{a}}{\sqrt{b}} \qquad x^2 = p \Rightarrow x = \pm\sqrt{p} \qquad x^3 = p \Rightarrow x = \sqrt[3]{p}
$$

**Example 1.** $\sqrt{144} = 12$, $\sqrt[3]{125} = 5$, $\sqrt[3]{-27} = -3$

**Example 2.** $\sqrt{\frac{9}{64}} = \frac{3}{8}$

**Example 3.** Simplify $\sqrt{72}$.
- $72 = 36 \times 2$ → $\sqrt{36}\sqrt{2} = 6\sqrt{2}$

**Example 4.** Solve $x^2 = 81$: $x = \pm 9$. Solve $x^3 = -64$: $x = -4$.

**Example 5.** A square garden has area 200 m². Side length?
- $\sqrt{200} = 10\sqrt{2} \approx 14.14$ m (only the positive root makes sense here)

**Example 6.** $\sqrt{12} + \sqrt{27}$
- $2\sqrt{3} + 3\sqrt{3} = 5\sqrt{3}$

## 5. Common mistakes
- $\sqrt{a + b} = \sqrt{a} + \sqrt{b}$. False: $\sqrt{9 + 16} = 5 \ne 3 + 4$.
- Forgetting the $\pm$ when solving $x^2 = p$.
- Thinking $\sqrt{-9}$ is $-3$. It has no real value ($-3 \times -3 = 9$).

## 6. Practice
Q: $\sqrt{0.25}$?
A: $0.5$.

Q: $\sqrt[3]{1000}$?
A: $10$.

Q: Simplify $\sqrt{50}$.
A: $5\sqrt{2}$.

Q: Solve $x^2 = 121$.
A: $x = \pm 11$.

Q: A cube has volume 343 cm³. Edge length?
A: $7$ cm.

@@ 3 | TOPIC | Irrational numbers, estimating roots and ordering reals | Locating √2 and √5 on a number line, why √2 is irrational, ordering mixed real numbers | MUST
# Irrational numbers, estimating roots and ordering reals

## 1. What it covers
- Estimating square roots between two whole numbers (and to one decimal place).
- Placing irrational numbers like $\sqrt{2}$, $\sqrt{5}$ and $\pi$ on a number line.
- Why $\sqrt{2}$ is irrational (a short proof).
- Ordering a mix of roots, fractions, decimals and $\pi$.

## 2. Where it is used
- **Estimation and sanity checks:** "$\sqrt{50}$ should be a bit above 7".
- **Engineering:** paper sizes (A4) use the ratio $\sqrt{2}$ so that halving a sheet keeps its shape.
- **Computing:** irrational numbers can't be stored exactly, so code works with approximations; numerical methods (like Newton's method, used inside `sqrt()`) refine estimates.
- **Mathematics:** understanding that the real number line has no gaps.

## 3. What it means
**Estimating:** find the two perfect squares around the number. $\sqrt{30}$: $25 < 30 < 36$, so $5 < \sqrt{30} < 6$. 30 is 5 above 25 and 6 below 36, so it sits a little before the halfway point: $\sqrt{30} \approx 5 + \frac{5}{11} \approx 5.45$. (True value $5.477$.)

**Why $\sqrt{2}$ is irrational (proof by contradiction):** suppose $\sqrt{2} = \frac{p}{q}$ in lowest terms. Then $p^2 = 2q^2$, so $p^2$ is even, so $p$ is even: $p = 2k$. Then $4k^2 = 2q^2$ → $q^2 = 2k^2$, so $q$ is even too. But then $\frac{p}{q}$ wasn't in lowest terms, a contradiction. So no such fraction exists.

**Ordering reals:** convert everything to decimals (to 2–3 places) and compare.

## 4. Formula & worked example
**Example 1.** Estimate $\sqrt{70}$.
- $64 < 70 < 81$ → between 8 and 9. 70 is 6 of the 17-unit gap → about $8 + \frac{6}{17} \approx 8.35$. (True: $8.367$.)

**Example 2.** Between which two integers is $-\sqrt{20}$?
- $4 < \sqrt{20} < 5$ → $-5 < -\sqrt{20} < -4$

**Example 3.** Order: $\sqrt{10}$, $\pi$, $3.2$, $\frac{22}{7}$, $3\frac{1}{8}$.
- Decimals: $3.162$, $3.142$, $3.2$, $3.143$, $3.125$
- Order: $3\frac{1}{8} < \pi < \frac{22}{7} < \sqrt{10} < 3.2$

**Example 4.** Estimate $\sqrt{2}$ with a calculator-free method (Newton's method): start with $1.5$; next guess $= \frac{1}{2}\left(1.5 + \frac{2}{1.5}\right) = \frac{1}{2}(1.5 + 1.333) = 1.4167$. Once more: $\frac{1}{2}\left(1.4167 + \frac{2}{1.4167}\right) \approx 1.41422$. (True: $1.414214$.)
```python
x = 1.5
for _ in range(4):
    x = (x + 2 / x) / 2     # average x and 2/x: the true root lies between them
print(x)                    # 1.414213562373095
```
If $x$ is too big, $\frac{2}{x}$ is too small (and vice versa), so their average is a better estimate. Each step roughly doubles the number of correct digits, which is why computers can compute square roots so quickly.

## 5. Common mistakes
- Guessing from the difference alone ($\sqrt{30} \approx 5.3$ because 30 is "5 more" than 25). Use where the number sits between the two squares.
- Treating $\frac{22}{7}$ or $3.14$ as exactly $\pi$.
- Forgetting that for negatives the order flips: $-\sqrt{10} < -3$.

## 6. Practice
Q: Between which integers is $\sqrt{55}$?
A: $7$ and $8$.

Q: Is $\sqrt{0.09}$ rational?
A: Yes, it equals $0.3$.

Q: Order: $\sqrt{3}$, $1.7$, $\frac{7}{4}$.
A: $1.7 < \sqrt{3} \approx 1.732 < 1.75$.

Q: Estimate $\sqrt{40}$ to one decimal place.
A: About $6.3$ (true $6.325$).

Q: In the proof that $\sqrt{2}$ is irrational, what contradiction is reached?
A: Both $p$ and $q$ turn out even, so $\frac{p}{q}$ wasn't in lowest terms.

@@ 3 | TOPIC | Practice & review: Exponents, roots and real numbers | Mixed problem set with worked answers | MUST
# Practice & review: Exponents, roots and real numbers

**How to use this:** 40 minutes, no calculator except problem 8.

## Problems
1. Simplify $\dfrac{(2x^3)^2 \cdot x^{-4}}{8x^{-1}}$.
2. Evaluate $2^{-3} + 4^0 + \left(\frac{1}{3}\right)^{-2}$.
3. Write $0.000\,048$ and $7\,030\,000$ in scientific notation.
4. $(6 \times 10^{-4})(5 \times 10^{9})$ in scientific notation.
5. $\dfrac{9 \times 10^{3}}{3 \times 10^{-2}}$.
6. Simplify $\sqrt{98} - \sqrt{18}$.
7. Solve $x^2 = 196$ and $x^3 = -216$.
8. A hard disk holds $2 \times 10^{12}$ bytes. How many 4 MB ($4 \times 10^6$ byte) photos fit?
9. Order: $\sqrt{8}$, $2.8$, $\frac{17}{6}$, $2\frac{3}{4}$.
10. Classify $\sqrt{121}$, $\sqrt{12}$, $-\frac{9}{3}$, $0.1010010001\ldots$ (the pattern of zeros keeps growing).

## Answers
1. $\frac{4x^6 \cdot x^{-4}}{8x^{-1}} = \frac{4x^2}{8x^{-1}} = \frac{x^3}{2}$.
2. $\frac{1}{8} + 1 + 9 = 10\frac{1}{8}$.
3. $4.8 \times 10^{-5}$; $7.03 \times 10^6$.
4. $30 \times 10^5 = 3 \times 10^6$.
5. $3 \times 10^5$.
6. $7\sqrt{2} - 3\sqrt{2} = 4\sqrt{2}$.
7. $x = \pm 14$; $x = -6$.
8. $\frac{2 \times 10^{12}}{4 \times 10^6} = 5 \times 10^5 = 500\,000$ photos.
9. $2\frac{3}{4} = 2.75 < 2.8 < \sqrt{8} \approx 2.828 < \frac{17}{6} \approx 2.833$.
10. $11$: natural/whole/integer/rational/real. $\sqrt{12}$: irrational. $-3$: integer, rational, real. The last: irrational (never terminates or repeats).

## Flashcards
Q: Product, quotient and power rules?
A: $a^ma^n = a^{m+n}$, $\frac{a^m}{a^n} = a^{m-n}$, $(a^m)^n = a^{mn}$.

Q: $a^{-n} = ?$
A: $\frac{1}{a^n}$.

Q: Form of scientific notation?
A: $a \times 10^n$ with $1 \le a < 10$.

Q: Solutions of $x^2 = p$ ($p > 0$)?
A: $x = \pm\sqrt{p}$.

@@ 2 | MODULE | Final review | Cumulative mixed set covering the whole roadmap | MUST
Mix everything to finish. After this you're ready for the Algebra & Functions roadmap.

@@ 3 | TOPIC | Final review: Numbers and arithmetic | Cumulative mixed problems from all phases with answers | MUST
# Final review: Numbers and arithmetic

**How to use this:** 60 minutes; calculator only for problems marked (C). Re-study the topic for every miss.

## Problems
1. LCM and GCF of 84 and 126.
2. $\frac{2}{3} \div \left(\frac{5}{6} - \frac{1}{4}\right) \times 1\frac{3}{4}$.
3. $-3^2 + (-2)^3 - (-4)(-5)$.
4. Write $0.\overline{45}$ as a fraction in lowest terms.
5. Share 3600 taka in the ratio $\frac{1}{2} : \frac{1}{3} : \frac{1}{4}$.
6. A car uses 7.5 L per 100 km. Fuel for a 340 km trip?
7. (C) A price rises from 1250 to 1450 taka. Percent change? Then it falls 10%. New price?
8. (C) A coat costs 4370 taka including 15% VAT, after a 5% discount. What was the original pre-VAT, pre-discount price?
9. Simplify $\dfrac{(3a^{-2}b)^2}{9a^{-5}b^3}$.
10. (C) The Earth's mass is $5.97 \times 10^{24}$ kg and the Moon's is $7.35 \times 10^{22}$ kg. How many times heavier is the Earth?

## Answers
1. $84 = 2^2 \cdot 3 \cdot 7$, $126 = 2 \cdot 3^2 \cdot 7$ → GCF $= 42$, LCM $= 252$.
2. $\frac{5}{6} - \frac{1}{4} = \frac{7}{12}$; $\frac{2}{3} \times \frac{12}{7} = \frac{8}{7}$; $\times \frac{7}{4} = 2$.
3. $-9 - 8 - 20 = -37$.
4. $\frac{45}{99} = \frac{5}{11}$.
5. Multiply by 12: $6 : 4 : 3$ (13 parts) → $1661.54$, $1107.69$, $830.77$ taka (rounded).
6. $7.5 \times 3.4 = 25.5$ L.
7. $+16\%$; $1450 \times 0.9 = 1305$ taka.
8. $4370 = P \times 0.95 \times 1.15$ → $P = \frac{4370}{1.0925} = 4000$ taka.
9. $\frac{9a^{-4}b^2}{9a^{-5}b^3} = \frac{a}{b}$.
10. $\frac{5.97 \times 10^{24}}{7.35 \times 10^{22}} \approx 0.812 \times 10^2 \approx 81$ times.
