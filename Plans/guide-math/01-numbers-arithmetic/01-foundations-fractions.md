@@ 1 | MODULE | Phase 1 — Number Foundations and Fractions (Foundation) | Number sets, properties, factors, GCF/LCM, exponents, order of operations, fractions and decimals | MUST
# Phase 1: Number foundations and fractions

This roadmap is the base of all the other math roadmaps. Algebra is arithmetic with letters, so any weakness here (fractions, negatives, percentages) shows up later as "I don't get algebra".

Every topic has six parts: **1. What it covers**, **2. Where it is used**, **3. What it means**, **4. Formula & worked example**, **5. Common mistakes**, **6. Practice** (`Q:`/`A:` lines become flashcards in the app).

## Set up your study system first (one evening)
- **Fixed time:** choose a daily slot (for example 1 hour after dinner). Consistency beats long weekend sessions.
- **Tools:** a notebook and a pencil. For Phases 1–2, **no calculator**: mental arithmetic is the skill being trained.
- **Error log:** a page (or spreadsheet) with columns *problem → my mistake → correct method → date to redo*. Redo each entry after 1, 7 and 30 days. The app's review feature does this for topics.
- **Diagnostic:** before starting, try the Practice section of a few topics cold. Skim the topics you ace and slow down on the ones you miss.

**How to study each topic (about 1 hour):** read sections 1–3, work through every example in section 4 on paper, read section 5, then answer section 6 without looking.

@@ 2 | MODULE | Number foundations | Number sets, properties of operations, factors and primes, GCF/LCM, exponents, order of operations | MUST
The building blocks: what kinds of numbers exist, the rules they follow, and how they break into factors.

@@ 3 | TOPIC | Number sets and the real number system | Natural numbers, whole numbers, integers, rationals, irrationals and reals | MUST
# Number sets and the real number system

## 1. What it covers
- The families of numbers: **natural** ($\mathbb{N}$), **whole**, **integers** ($\mathbb{Z}$), **rational** ($\mathbb{Q}$), **irrational** and **real** ($\mathbb{R}$).
- How the sets fit inside each other.
- Classifying any number.

## 2. Where it is used
- **Programming data types:** `int` stores integers, `float`/`double` approximates real numbers, and some languages have exact fractions (Python's `fractions.Fraction`). Choosing the wrong type causes bugs, e.g. money stored as a float drifts by tiny amounts ($0.1 + 0.2 = 0.30000000000000004$).
- **Databases:** `INTEGER` vs `DECIMAL(10,2)` columns.
- **Every later topic:** solving an equation "over the integers" or "over the reals" changes the answer.

## 3. What it means
Number sets grew as people needed them:
- **Natural numbers** $1, 2, 3, \ldots$: for counting things.
- **Whole numbers** $0, 1, 2, 3, \ldots$: add zero for "none".
- **Integers** $\ldots, -2, -1, 0, 1, 2, \ldots$: add negatives for debt, temperatures below zero, floors below ground.
- **Rational numbers**: anything that can be written as a fraction $\frac{p}{q}$ of integers ($q \ne 0$): $\frac{3}{4}$, $-2$, $0.5$, $0.333\ldots$. Their decimals either **stop** or **repeat**.
- **Irrational numbers**: real numbers that can't be written as a fraction, so their decimals go on forever **without repeating**: $\sqrt{2} = 1.41421356\ldots$, $\pi = 3.14159265\ldots$.
- **Real numbers**: all rationals and irrationals together, i.e. every point on the number line.

Each set sits inside the next, like Russian nesting dolls: natural ⊂ whole ⊂ integer ⊂ rational ⊂ real. Irrationals are the part of the reals outside the rationals.

## 4. Formula & worked example
$$
\mathbb{N} \subset \mathbb{W} \subset \mathbb{Z} \subset \mathbb{Q} \subset \mathbb{R} \qquad \mathbb{Q} = \left\{ \tfrac{p}{q} : p, q \in \mathbb{Z},\ q \ne 0 \right\}
$$

**Example 1.** Classify $-7$.
- It is an integer, so also rational ($-\frac{7}{1}$) and real. Not natural or whole.

**Example 2.** Classify $0.\overline{36}$ ($0.363636\ldots$).
- The decimal repeats, so it's rational. (It equals $\frac{36}{99} = \frac{4}{11}$.)

**Example 3.** Classify $\sqrt{16}$ and $\sqrt{17}$.
- $\sqrt{16} = 4$: natural, whole, integer, rational, real.
- $\sqrt{17}$: 17 isn't a perfect square, so it is irrational (and real).

**Example 4.** Classify $\frac{22}{7}$.
- Rational. It is close to $\pi$ but **not equal** to it; $\pi$ is irrational.

## 5. Common mistakes
- Thinking every square root is irrational. $\sqrt{25} = 5$ is rational.
- Thinking $\pi = \frac{22}{7}$ or $3.14$. Those are approximations.
- Forgetting that integers are also rational ($5 = \frac{5}{1}$).

## 6. Practice
Q: Is $0$ a natural number in this course's convention?
A: No. $0$ is a whole number (and integer, rational, real).

Q: Classify $-\frac{3}{8}$.
A: Rational and real.

Q: Classify $\sqrt{50}$.
A: Irrational (and real), because 50 is not a perfect square.

Q: How can you tell from its decimal that a number is rational?
A: The decimal either terminates or repeats.

Q: Why is $0.1 + 0.2$ not exactly $0.3$ in most programming languages?
A: Floats store binary approximations of real numbers; $0.1$ has no exact binary form.

@@ 3 | TOPIC | Properties of operations | Commutative, associative, distributive, identity and inverse properties | MUST
# Properties of operations

## 1. What it covers
- **Commutative:** order doesn't matter ($a + b = b + a$, $ab = ba$).
- **Associative:** grouping doesn't matter ($(a + b) + c = a + (b + c)$).
- **Distributive:** $a(b + c) = ab + ac$.
- **Identity** ($0$ for adding, $1$ for multiplying) and **inverse** (opposite and reciprocal).

## 2. Where it is used
- **Mental arithmetic:** $25 \times 37 \times 4 = (25 \times 4) \times 37 = 3700$.
- **Algebra:** every simplification step (expanding brackets, collecting terms) is one of these properties.
- **Programming:** parallel computing (MapReduce, GPU sums) relies on addition being associative and commutative, so a sum can be split across many machines. Floating-point addition is only *approximately* associative, which is why parallel sums can differ in the last digits.
- **Databases:** SQL's `SUM` can add rows in any order because of these properties.

## 3. What it means
These are the "rules of the game" that let you rearrange calculations without changing the answer.
- **Commutative**: swap the order. True for $+$ and $\times$, **false** for $-$ and $\div$ ($5 - 2 \ne 2 - 5$).
- **Associative**: regroup. True for $+$ and $\times$, false for $-$ and $\div$.
- **Distributive**: multiplication "spreads over" addition. Buying 3 bags each containing 4 apples and 2 oranges = 12 apples + 6 oranges.
- **Identity**: the number that changes nothing: $a + 0 = a$, $a \times 1 = a$.
- **Inverse**: the number that brings you back to the identity: $a + (-a) = 0$, $a \times \frac{1}{a} = 1$.

## 4. Formula & worked example
$$
a + b = b + a \qquad (ab)c = a(bc) \qquad a(b + c) = ab + ac \qquad a + (-a) = 0 \qquad a \cdot \tfrac{1}{a} = 1
$$

**Example 1 (mental math).** $17 + 46 + 3 + 4$
- Rearrange: $(17 + 3) + (46 + 4) = 20 + 50 = 70$

**Example 2 (distributive).** $8 \times 103$
- $8(100 + 3) = 800 + 24 = 824$

**Example 3 (distributive backwards, i.e. factoring).** $7 \times 46 + 7 \times 54$
- $7(46 + 54) = 7 \times 100 = 700$

**Example 4.** Name the property: $4(x + 5) = 4x + 20$.
- Distributive.

**Example 5.** Show subtraction is not associative.
- $(10 - 4) - 3 = 3$ but $10 - (4 - 3) = 9$.

## 5. Common mistakes
- Using commutative or associative rules with subtraction or division.
- Distributing to only the first term: $3(x + 4) \ne 3x + 4$.
- Mixing up identity and inverse. The identity for multiplication is $1$; the inverse of $5$ is $\frac{1}{5}$.

## 6. Practice
Q: Compute $4 \times 19 \times 25$ mentally.
A: $(4 \times 25) \times 19 = 1900$.

Q: Compute $6 \times 98$ using the distributive property.
A: $6(100 - 2) = 600 - 12 = 588$.

Q: What is the multiplicative inverse of $\frac{2}{3}$?
A: $\frac{3}{2}$.

Q: Which property is $(2 + 3) + 4 = 2 + (3 + 4)$?
A: Associative property of addition.

Q: Is division commutative?
A: No: $8 \div 2 = 4$ but $2 \div 8 = 0.25$.

@@ 3 | TOPIC | Factors, multiples and prime factorization | Divisibility rules, primes and composites, factor trees | MUST
# Factors, multiples and prime factorization

## 1. What it covers
- **Factors** (numbers that divide exactly) and **multiples** (results of multiplying).
- **Prime** and **composite** numbers.
- **Divisibility rules** for 2, 3, 4, 5, 6, 9, 10.
- **Prime factorization** with a factor tree.

## 2. Where it is used
- **Cryptography:** RSA encryption (used in HTTPS) is secure because multiplying two huge primes is easy, but factoring the product back is practically impossible.
- **Hashing:** hash tables often use a prime number of buckets to spread keys evenly.
- **Fractions:** simplifying, GCF and LCM all use factorization.
- **Scheduling:** "every 4 days" and "every 6 days" line up on common multiples.

## 3. What it means
A **factor** of $n$ divides $n$ with no remainder: the factors of 12 are $1, 2, 3, 4, 6, 12$. A **multiple** of $n$ is $n \times$ something: the multiples of 12 are $12, 24, 36, \ldots$

A **prime** has exactly two factors, 1 and itself: $2, 3, 5, 7, 11, 13, \ldots$ ($1$ is **not** prime). A **composite** has more than two factors.

**Fundamental theorem of arithmetic:** every whole number above 1 is a product of primes in exactly one way. Primes are the "atoms" and every number is a "molecule" built from them.

**Divisibility rules** (quick tests):
- 2: last digit even. 5: last digit 0 or 5. 10: last digit 0.
- 3: digit sum divisible by 3. 9: digit sum divisible by 9.
- 4: last two digits divisible by 4. 6: divisible by both 2 and 3.

## 4. Formula & worked example
**Example 1: factor tree for 360.**
- $360 = 36 \times 10 = (6 \times 6)(2 \times 5) = (2 \cdot 3)(2 \cdot 3)(2 \cdot 5)$
- $360 = 2^3 \times 3^2 \times 5$

**Example 2.** Is 2 718 divisible by 3? By 9?
- Digit sum $2 + 7 + 1 + 8 = 18$: divisible by 3 **and** 9.

**Example 3.** Is 91 prime?
- Test primes up to $\sqrt{91} \approx 9.5$: 2 no, 3 no ($9 + 1 = 10$), 5 no, 7: $91 = 7 \times 13$. **Composite.**
- You only need to test primes up to the square root, because factors come in pairs and one of each pair is at most $\sqrt{n}$.

**Example 4: in code.**
```python
def is_prime(n):
    if n < 2:
        return False
    i = 2
    while i * i <= n:          # only test up to the square root
        if n % i == 0:
            return False
        i += 1
    return True
print([p for p in range(2, 30) if is_prime(p)])   # [2, 3, 5, 7, 11, 13, 17, 19, 23, 29]
```
`n % i == 0` means "$i$ divides $n$ exactly". The loop stops at $\sqrt{n}$, which is the same shortcut as Example 3.

## 5. Common mistakes
- Calling 1 a prime. It has only one factor.
- Calling 2 composite because it's even. 2 is the only even prime.
- Stopping a factor tree early with a composite leaf (e.g. leaving a 9 instead of $3 \times 3$).

## 6. Practice
Q: Prime factorization of 84?
A: $2^2 \times 3 \times 7$.

Q: List all factors of 36.
A: $1, 2, 3, 4, 6, 9, 12, 18, 36$.

Q: Is 4 536 divisible by 4?
A: Yes, the last two digits 36 are divisible by 4.

Q: Is 97 prime?
A: Yes. No prime up to $\sqrt{97} \approx 9.8$ (2, 3, 5, 7) divides it.

Q: Why does RSA encryption rely on primes?
A: Multiplying two large primes is easy, but factoring their product is infeasible.

@@ 3 | TOPIC | GCF and LCM | Greatest common factor and least common multiple by listing and by prime factors | MUST
# GCF and LCM

## 1. What it covers
- The **greatest common factor** (GCF, also HCF or GCD): the biggest number that divides two numbers.
- The **least common multiple** (LCM): the smallest number both divide into.
- Methods: listing, prime factorization, and Euclid's algorithm.

## 2. Where it is used
- **Fractions:** simplify with the GCF, and add with the LCM (common denominator).
- **Scheduling:** two buses every 12 and 18 minutes leave together again after LCM $= 36$ minutes. The same idea applies to cron jobs and repeating events.
- **Tiling and packing:** the largest square tile that fits a 48 × 60 floor exactly is GCF $= 12$.
- **Programming:** `math.gcd`, and Euclid's algorithm, one of the oldest algorithms still in use.

## 3. What it means
- **GCF** = what both numbers **share**. Using prime factors, take the common primes with the **lowest** powers.
- **LCM** = the smallest number **containing** both. Take **every** prime that appears, with the **highest** power.

**Euclid's algorithm:** the GCF of $a$ and $b$ equals the GCF of $b$ and the remainder of $a \div b$. Repeat until the remainder is 0.

**Shortcut:** $\text{GCF}(a, b) \times \text{LCM}(a, b) = a \times b$.

## 4. Formula & worked example
**Example 1.** GCF and LCM of 24 and 36 by primes.
- $24 = 2^3 \times 3$, $36 = 2^2 \times 3^2$
- GCF $= 2^2 \times 3 = 12$ (lowest powers of the shared primes)
- LCM $= 2^3 \times 3^2 = 72$ (highest powers of all primes)
- Check: $12 \times 72 = 864 = 24 \times 36$ ✓

**Example 2: Euclid.** GCF of 252 and 105.
- $252 = 2 \times 105 + 42$
- $105 = 2 \times 42 + 21$
- $42 = 2 \times 21 + 0$ → GCF $= 21$

**Example 3: buses.** Bus A every 12 min, bus B every 18 min, both leave at 8:00. When next together?
- LCM$(12, 18)$: $12 = 2^2 \cdot 3$, $18 = 2 \cdot 3^2$ → $2^2 \cdot 3^2 = 36$ → at 8:36.

**Example 4: code.**
```python
def gcd(a, b):
    while b:
        a, b = b, a % b       # Euclid: replace (a, b) with (b, remainder)
    return a
def lcm(a, b):
    return a * b // gcd(a, b)
print(gcd(252, 105), lcm(12, 18))   # 21 36
```
The `while` loop is Euclid's algorithm from Example 2. `lcm` uses the shortcut $\text{LCM} = \frac{ab}{\text{GCF}}$.

## 5. Common mistakes
- Swapping the rules: GCF takes the **lowest** powers of **shared** primes; LCM takes the **highest** powers of **all** primes.
- Thinking the LCM is always $a \times b$. That is only true when the GCF is 1.
- Answering a "largest" question with the LCM, or a "next time together" question with the GCF.

## 6. Practice
Q: GCF of 18 and 30?
A: 6.

Q: LCM of 8 and 12?
A: 24.

Q: GCF of 7 and 20?
A: 1 (they are coprime).

Q: Two lights flash every 15 s and 25 s. After how long do they flash together?
A: LCM $= 75$ s.

Q: GCF$(a, b) = 4$ and $ab = 240$. What is the LCM?
A: $\frac{240}{4} = 60$.

@@ 3 | TOPIC | Exponents: squares, cubes and powers of ten | Meaning of powers, perfect squares and cubes, powers of ten and place value | MUST
# Exponents: squares, cubes and powers of ten

## 1. What it covers
- What $a^n$ means: repeated multiplication.
- Perfect squares up to $15^2$ and cubes up to $10^3$.
- Powers of ten and their link to place value.
- Evaluating expressions with exponents.

## 2. Where it is used
- **Computing:** memory sizes are powers of 2: $2^{10} = 1024$ bytes (1 KiB), $2^{30}$ ≈ 1 GB. A 32-bit integer can hold $2^{32}$ values.
- **Science:** very large and small quantities (scientific notation, Phase 5).
- **Area and volume:** $\text{m}^2$ and $\text{m}^3$ are literally squares and cubes.
- **Growth:** interest, population and viral spread grow by repeated multiplication.

## 3. What it means
$a^n$ means "multiply $a$ by itself $n$ times". $a$ is the **base**, $n$ the **exponent** (or power).
- $5^3 = 5 \times 5 \times 5 = 125$. **Not** $5 \times 3$.
- $a^2$ is "a squared" because it's the area of a square with side $a$; $a^3$ is "a cubed", the volume of a cube.

**Powers of ten** just move the decimal point: $10^3 = 1000$ (three zeros), and multiplying by $10^3$ shifts digits three places left.

Exponents grow **very** fast. A legend says a king agreed to pay rice doubling on each chessboard square: 1, 2, 4, 8, ... The last square alone needs $2^{63} \approx 9.2 \times 10^{18}$ grains, more than all the rice on Earth.

## 4. Formula & worked example
$$
a^n = \underbrace{a \times a \times \cdots \times a}_{n \text{ times}} \qquad a^1 = a
$$
Squares to memorise: $1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225$.
Cubes: $1, 8, 27, 64, 125, 216, 343, 512, 729, 1000$.
Powers of 2: $2, 4, 8, 16, 32, 64, 128, 256, 512, 1024$.

**Example 1.** $2^5 = 2 \cdot 2 \cdot 2 \cdot 2 \cdot 2 = 32$

**Example 2.** $(-3)^2$ vs $-3^2$
- $(-3)^2 = (-3)(-3) = 9$
- $-3^2 = -(3 \cdot 3) = -9$: the exponent applies only to the 3.

**Example 3.** $\left(\frac{2}{3}\right)^3 = \frac{2^3}{3^3} = \frac{8}{27}$

**Example 4.** Evaluate $2x^2 + 3$ for $x = 4$.
- $2(16) + 3 = 35$. (Square first, then multiply by 2.)

**Example 5.** $4.7 \times 10^3 = 4700$

## 5. Common mistakes
- $3^4 = 12$. Wrong: it's $81$; exponents mean repeated multiplication, not multiplication by the exponent.
- $-5^2 = 25$. Wrong: without brackets it's $-25$.
- $2x^2$ read as $(2x)^2$. The exponent applies only to $x$.

## 6. Practice
Q: $3^4$?
A: $81$.

Q: $(-2)^5$?
A: $-32$.

Q: $13^2$?
A: $169$.

Q: $\left(\frac{1}{2}\right)^4$?
A: $\frac{1}{16}$.

Q: How many values can an 8-bit number hold?
A: $2^8 = 256$.

@@ 3 | TOPIC | Order of operations | BODMAS/PEMDAS with whole numbers, fractions and decimals | MUST
# Order of operations

## 1. What it covers
- The agreed order: **B**rackets, **O**rders (powers and roots), **D**ivision/**M**ultiplication, **A**ddition/**S**ubtraction (BODMAS = PEMDAS).
- Left-to-right for operations of equal rank.
- Fraction bars and nested brackets.

## 2. Where it is used
- **Programming:** every language has operator precedence. `2 + 3 * 4` is `14`, not `20`. In Python, `-2 ** 2` is `-4`.
- **Spreadsheets:** `=A1+B1/2` divides only B1. Many formula bugs are missing brackets.
- **Calculators:** scientific calculators follow these rules; simple ones may not.
- **All algebra:** evaluating any formula.

## 3. What it means
Without a shared rule, $2 + 3 \times 4$ could be $20$ or $14$. Mathematicians agreed on an order so everyone gets the same answer:
1. **Brackets** first (innermost first).
2. **Orders**: exponents and roots.
3. **Multiplication and division**, from **left to right**. They have the same rank: neither goes first by default.
4. **Addition and subtraction**, from **left to right**.

A **fraction bar** acts like brackets around the top and the bottom.

## 4. Formula & worked example
**Example 1.** $8 + 2 \times 5^2$
- Power: $5^2 = 25$ → $8 + 2 \times 25$ → multiply: $8 + 50 = 58$

**Example 2.** $24 \div 4 \times 3$
- Left to right: $6 \times 3 = 18$ (not $24 \div 12 = 2$)

**Example 3.** $10 - 4 + 3$
- Left to right: $6 + 3 = 9$ (not $10 - 7 = 3$)

**Example 4.** $\dfrac{3 + 9}{2 \times 3} + 1$
- Top $= 12$, bottom $= 6$ → $2 + 1 = 3$

**Example 5.** $2[5 + (6 - 2)^2] \div 7$
- Inner: $6 - 2 = 4$ → $4^2 = 16$ → $5 + 16 = 21$ → $2 \times 21 = 42$ → $42 \div 7 = 6$

**Example 6 (fractions).** $\frac{1}{2} + \frac{3}{4} \times \frac{2}{3}$
- Multiply first: $\frac{3}{4} \cdot \frac{2}{3} = \frac{1}{2}$ → $\frac{1}{2} + \frac{1}{2} = 1$

## 5. Common mistakes
- Always doing multiplication before division (or addition before subtraction). They are equal rank: go **left to right**.
- Ignoring the hidden brackets of a fraction bar.
- Typing $\frac{3 + 9}{6}$ into code as `3 + 9 / 6` instead of `(3 + 9) / 6`.

## 6. Practice
Q: $6 + 18 \div 3 \times 2$?
A: $6 + 6 \times 2 = 6 + 12 = 18$.

Q: $(4 + 2)^2 \div 9$?
A: $36 \div 9 = 4$.

Q: $20 - 2 \times 3^2$?
A: $20 - 18 = 2$.

Q: $\frac{15 - 3}{2^2} - 1$?
A: $3 - 1 = 2$.

Q: What does Python print for `10 - 4 - 3`?
A: $3$ (left to right: $6 - 3$).

@@ 2 | MODULE | Fractions and decimals | Equivalent fractions, the four operations with fractions and decimals, long division, repeating decimals | MUST
Fractions are the topic most adults are shaky on, and the one algebra depends on most. Take your time here.

@@ 3 | TOPIC | Fraction basics: equivalent, simplifying, comparing | What a fraction means, equivalent fractions, lowest terms, mixed numbers, comparing | MUST
# Fraction basics: equivalent, simplifying, comparing

## 1. What it covers
- What $\frac{a}{b}$ means (part of a whole, and division $a \div b$).
- **Equivalent fractions** and **simplifying** to lowest terms.
- **Mixed numbers** and **improper fractions**.
- **Comparing** fractions.

## 2. Where it is used
- **Recipes and measurement:** $\frac{3}{4}$ cup, $\frac{1}{2}$ inch.
- **Probability:** chances are fractions ($\frac{1}{6}$ for a die).
- **Screen design:** aspect ratios ($\frac{16}{9}$), CSS grid `fr` units, "two-thirds width" layouts.
- **Music:** note lengths are fractions (half note, quarter note).
- **Algebra:** rational expressions are fractions with letters.

## 3. What it means
$\frac{3}{4}$: cut a whole into **4** equal parts (the **denominator**) and take **3** of them (the **numerator**). It also means $3 \div 4 = 0.75$.

**Equivalent fractions** describe the same amount: $\frac{1}{2} = \frac{2}{4} = \frac{4}{8}$. Cutting a pizza into more slices doesn't change how much you eat if you take proportionally more slices. You get them by multiplying (or dividing) top and bottom by the same number.

**Simplest form:** divide top and bottom by their GCF.

**Comparing:** give both the same denominator, or cross-multiply.

## 4. Formula & worked example
$$
\frac{a}{b} = \frac{a \times k}{b \times k} \qquad \frac{a}{b} < \frac{c}{d} \iff ad < bc \ \ (b, d > 0)
$$

**Example 1.** Simplify $\frac{36}{48}$.
- GCF $= 12$ → $\frac{3}{4}$

**Example 2.** Mixed ↔ improper.
- $3\frac{2}{5} = \frac{3 \times 5 + 2}{5} = \frac{17}{5}$
- $\frac{23}{4} = 5$ remainder $3$ → $5\frac{3}{4}$

**Example 3.** Compare $\frac{5}{8}$ and $\frac{7}{12}$.
- Common denominator 24: $\frac{15}{24}$ vs $\frac{14}{24}$ → $\frac{5}{8}$ is bigger.
- Or cross-multiply: $5 \times 12 = 60$ vs $8 \times 7 = 56$ → $\frac{5}{8} > \frac{7}{12}$.

**Example 4.** Find the missing number: $\frac{3}{7} = \frac{?}{42}$.
- $7 \times 6 = 42$, so $3 \times 6 = 18$.

## 5. Common mistakes
- Adding to top and bottom: $\frac{1}{2} \ne \frac{1 + 1}{2 + 1}$. Only **multiply or divide** both.
- Thinking a bigger denominator means a bigger fraction: $\frac{1}{8} < \frac{1}{4}$.
- Not fully simplifying: $\frac{12}{18} \to \frac{6}{9}$ still simplifies to $\frac{2}{3}$.

## 6. Practice
Q: Simplify $\frac{45}{60}$.
A: $\frac{3}{4}$.

Q: Write $4\frac{1}{3}$ as an improper fraction.
A: $\frac{13}{3}$.

Q: Which is bigger: $\frac{4}{9}$ or $\frac{5}{11}$?
A: $\frac{5}{11}$ ($4 \times 11 = 44 < 9 \times 5 = 45$).

Q: $\frac{5}{6} = \frac{?}{48}$
A: $40$.

Q: Write $\frac{29}{6}$ as a mixed number.
A: $4\frac{5}{6}$.

@@ 3 | TOPIC | Adding and subtracting fractions and mixed numbers | Common denominators with the LCM, borrowing with mixed numbers | MUST
# Adding and subtracting fractions and mixed numbers

## 1. What it covers
- Adding and subtracting fractions with the same and with different denominators.
- Using the LCM as the common denominator.
- Mixed numbers, including "borrowing".

## 2. Where it is used
- **Cooking and building:** $\frac{3}{4}$ cup + $\frac{2}{3}$ cup; a $2\frac{1}{2}$ m board minus $\frac{7}{8}$ m.
- **Time:** $\frac{1}{4}$ hour + $\frac{1}{3}$ hour = 35 minutes.
- **Algebra:** adding rational expressions like $\frac{1}{x} + \frac{1}{x + 1}$ uses exactly the same method.
- **Electronics:** parallel resistors: $\frac{1}{R} = \frac{1}{R_1} + \frac{1}{R_2}$.

## 3. What it means
You can only add things of the **same kind**: 2 apples + 3 apples = 5 apples, but 1 half + 1 third isn't "2 fifths". First cut both into the **same size pieces** (a common denominator), then add the pieces.

The best common denominator is the **LCM** of the denominators (smallest numbers to work with).

**Steps:** 1. find the LCM, 2. rewrite each fraction, 3. add or subtract the numerators (denominator stays), 4. simplify.

## 4. Formula & worked example
$$
\frac{a}{c} + \frac{b}{c} = \frac{a + b}{c} \qquad \frac{a}{b} + \frac{c}{d} = \frac{ad + bc}{bd}
$$

**Example 1.** $\frac{2}{9} + \frac{4}{9} = \frac{6}{9} = \frac{2}{3}$

**Example 2.** $\frac{3}{4} + \frac{5}{6}$
- LCM$(4, 6) = 12$: $\frac{9}{12} + \frac{10}{12} = \frac{19}{12} = 1\frac{7}{12}$

**Example 3.** $\frac{7}{10} - \frac{4}{15}$
- LCM $= 30$: $\frac{21}{30} - \frac{8}{30} = \frac{13}{30}$

**Example 4 (mixed, borrowing).** $5\frac{1}{4} - 2\frac{2}{3}$
- Common denominator 12: $5\frac{3}{12} - 2\frac{8}{12}$
- $\frac{3}{12} < \frac{8}{12}$, so borrow 1 from the 5: $4\frac{15}{12} - 2\frac{8}{12} = 2\frac{7}{12}$
- (Or convert: $\frac{21}{4} - \frac{8}{3} = \frac{63}{12} - \frac{32}{12} = \frac{31}{12} = 2\frac{7}{12}$ ✓)

**Example 5.** Two resistors of 6 Ω and 3 Ω in parallel.
- $\frac{1}{R} = \frac{1}{6} + \frac{1}{3} = \frac{1}{6} + \frac{2}{6} = \frac{3}{6} = \frac{1}{2}$ → $R = 2$ Ω

## 5. Common mistakes
- Adding denominators: $\frac{1}{2} + \frac{1}{3} \ne \frac{2}{5}$.
- Forgetting to change the numerator when changing the denominator.
- When borrowing in mixed numbers, adding 10 instead of one whole (e.g. $\frac{12}{12}$).

## 6. Practice
Q: $\frac{1}{2} + \frac{1}{3}$?
A: $\frac{5}{6}$.

Q: $\frac{5}{8} - \frac{1}{6}$?
A: $\frac{15}{24} - \frac{4}{24} = \frac{11}{24}$.

Q: $2\frac{3}{5} + 1\frac{4}{5}$?
A: $3\frac{7}{5} = 4\frac{2}{5}$.

Q: $6 - 2\frac{3}{8}$?
A: $3\frac{5}{8}$.

Q: Two 4 Ω resistors in parallel?
A: $\frac{1}{R} = \frac{1}{4} + \frac{1}{4} = \frac{1}{2}$, so $R = 2$ Ω.

@@ 3 | TOPIC | Multiplying fractions and mixed numbers | Multiply tops and bottoms, cancel first, "of" means multiply, area model | MUST
# Multiplying fractions and mixed numbers

## 1. What it covers
- Multiplying fractions: tops times tops, bottoms times bottoms.
- **Cancelling** before multiplying.
- Mixed numbers (convert first) and "a fraction **of** a quantity".

## 2. Where it is used
- **Scaling recipes:** $\frac{2}{3}$ of a recipe that needs $\frac{3}{4}$ kg flour.
- **Probability:** the chance of two independent events is a product: $\frac{1}{2} \times \frac{1}{6}$.
- **Discounts:** $\frac{1}{4}$ off means paying $\frac{3}{4}$ of the price.
- **Area:** a field $\frac{1}{2}$ km by $\frac{3}{5}$ km.

## 3. What it means
"$\frac{1}{2} \times \frac{1}{3}$" means "half **of** a third". Take a third of a chocolate bar, then half of that piece: you get $\frac{1}{6}$ of the bar. So multiplying by a fraction less than 1 makes things **smaller**.

**Area model:** a $1 \times 1$ square split into 2 columns and 3 rows has 6 cells; shading half the columns and a third of the rows overlaps in 1 cell = $\frac{1}{6}$.

**Cancelling:** any top number and any bottom number can be divided by a common factor before multiplying. This keeps numbers small.

## 4. Formula & worked example
$$
\frac{a}{b} \times \frac{c}{d} = \frac{ac}{bd}
$$

**Example 1.** $\frac{2}{3} \times \frac{4}{5} = \frac{8}{15}$

**Example 2 (cancel first).** $\frac{6}{35} \times \frac{14}{9}$
- 6 and 9 share 3 → $\frac{2}{35} \times \frac{14}{3}$; 14 and 35 share 7 → $\frac{2}{5} \times \frac{2}{3} = \frac{4}{15}$

**Example 3 (of).** $\frac{3}{8}$ of 640 m
- $\frac{3}{8} \times 640 = 3 \times 80 = 240$ m

**Example 4 (mixed).** $2\frac{1}{4} \times 1\frac{1}{3}$
- $\frac{9}{4} \times \frac{4}{3} = \frac{36}{12} = 3$ (cancel the 4s and $9 \div 3$ → $3 \times 1 = 3$)

**Example 5 (probability).** Chance of flipping heads **and** rolling a 6.
- $\frac{1}{2} \times \frac{1}{6} = \frac{1}{12}$

## 5. Common mistakes
- Multiplying mixed numbers part by part: $2\frac{1}{2} \times 3\frac{1}{2} \ne 6\frac{1}{4}$. Convert first: $\frac{5}{2} \times \frac{7}{2} = \frac{35}{4} = 8\frac{3}{4}$.
- Finding a common denominator first. That's only needed for adding and subtracting.
- Cancelling two numbers that are both on top (or both on the bottom). Cancel **top with bottom**.

## 6. Practice
Q: $\frac{3}{4} \times \frac{2}{9}$?
A: $\frac{6}{36} = \frac{1}{6}$.

Q: $\frac{5}{6}$ of 72?
A: $60$.

Q: $1\frac{1}{2} \times 2\frac{2}{3}$?
A: $\frac{3}{2} \times \frac{8}{3} = 4$.

Q: $\frac{10}{21} \times \frac{7}{15}$?
A: $\frac{2}{9}$.

Q: A shirt costs 1200 taka and is $\frac{1}{4}$ off. What do you pay?
A: $\frac{3}{4} \times 1200 = 900$ taka.

@@ 3 | TOPIC | Dividing fractions | Reciprocals, keep-change-flip, why it works, dividing by whole numbers | MUST
# Dividing fractions

## 1. What it covers
- The **reciprocal** of a fraction.
- Dividing by a fraction = multiplying by its reciprocal.
- Why this works, and dividing mixed numbers and whole numbers.

## 2. Where it is used
- **"How many fit?" questions:** how many $\frac{3}{4}$ m pieces can be cut from 6 m of wire?
- **Rates:** walking $\frac{3}{4}$ km in $\frac{1}{4}$ hour → speed $= \frac{3}{4} \div \frac{1}{4} = 3$ km/h.
- **Unit conversion and recipes.**
- **Algebra:** dividing rational expressions (complex fractions).

## 3. What it means
$6 \div \frac{1}{2}$ asks: **how many halves fit into 6?** Each whole has 2 halves, so 12. Notice $6 \div \frac{1}{2} = 6 \times 2$. Dividing by a fraction is the same as multiplying by its flip.

**Why it works:** division undoes multiplication, and multiplying by $\frac{b}{a}$ undoes multiplying by $\frac{a}{b}$, because $\frac{a}{b} \times \frac{b}{a} = 1$.

Memory aid: **keep** the first fraction, **change** $\div$ to $\times$, **flip** the second (keep–change–flip).

## 4. Formula & worked example
$$
\frac{a}{b} \div \frac{c}{d} = \frac{a}{b} \times \frac{d}{c} = \frac{ad}{bc}
$$

**Example 1.** $\frac{2}{3} \div \frac{4}{5} = \frac{2}{3} \times \frac{5}{4} = \frac{10}{12} = \frac{5}{6}$

**Example 2.** $6 \div \frac{3}{4}$
- $6 \times \frac{4}{3} = 8$: eight $\frac{3}{4}$ m pieces fit in 6 m.

**Example 3.** $\frac{5}{8} \div 10$
- $\frac{5}{8} \times \frac{1}{10} = \frac{5}{80} = \frac{1}{16}$

**Example 4 (mixed).** $3\frac{1}{3} \div 1\frac{1}{9}$
- $\frac{10}{3} \div \frac{10}{9} = \frac{10}{3} \times \frac{9}{10} = 3$

**Example 5 (complex fraction).** $\dfrac{\frac{1}{2}}{\frac{3}{8}}$
- $= \frac{1}{2} \times \frac{8}{3} = \frac{8}{6} = \frac{4}{3}$

## 5. Common mistakes
- Flipping the **first** fraction instead of the second.
- Flipping before converting mixed numbers.
- Expecting division to always make things smaller. Dividing by a number less than 1 makes the answer **bigger**.

## 6. Practice
Q: Reciprocal of $\frac{7}{3}$? Of $5$?
A: $\frac{3}{7}$; $\frac{1}{5}$.

Q: $\frac{3}{5} \div \frac{9}{10}$?
A: $\frac{3}{5} \times \frac{10}{9} = \frac{2}{3}$.

Q: $4 \div \frac{2}{7}$?
A: $14$.

Q: $2\frac{1}{2} \div \frac{5}{6}$?
A: $\frac{5}{2} \times \frac{6}{5} = 3$.

Q: How many $\frac{1}{4}$ kg packets can be filled from $3\frac{1}{2}$ kg of rice?
A: $\frac{7}{2} \div \frac{1}{4} = 14$.

@@ 3 | TOPIC | Decimal operations | Place value, adding, subtracting and multiplying decimals, multiplying by powers of ten | MUST
# Decimal operations

## 1. What it covers
- Decimal **place value** (tenths, hundredths, thousandths).
- Adding and subtracting by lining up the decimal point.
- Multiplying decimals and placing the point.
- Multiplying and dividing by 10, 100, 1000.

## 2. Where it is used
- **Money:** prices, bills and bank balances (and why software stores money as integer *paisa/cents* or a `Decimal` type, not float).
- **Measurements:** 1.75 m, 2.5 kg.
- **Data and statistics:** averages and percentages are usually decimals.
- **Programming:** formatting numbers (`round(x, 2)`, `f"{x:.2f}"`).

## 3. What it means
In $36.582$: 3 tens, 6 ones, then **5 tenths**, **8 hundredths**, **2 thousandths**. Each place is 10 times smaller than the one to its left.

- **Adding/subtracting:** line up the decimal points so that tenths add to tenths, like adding taka to taka and paisa to paisa. Fill gaps with zeros.
- **Multiplying:** ignore the points, multiply as whole numbers, then the answer has as many decimal places as the two numbers **together**.
- **× or ÷ by $10^n$:** shift the decimal point $n$ places (right for ×, left for ÷).

## 4. Formula & worked example
**Example 1.** $12.4 + 3.075$
```text
  12.400
+  3.075
--------
  15.475
```
Line up the points and pad with zeros so every column has the same place value.

**Example 2.** $20 - 7.36$
- $20.00 - 7.36 = 12.64$

**Example 3.** $3.4 \times 0.25$
- $34 \times 25 = 850$; decimal places $1 + 2 = 3$ → $0.850 = 0.85$

**Example 4.** $0.06 \times 0.3$
- $6 \times 3 = 18$; places $2 + 1 = 3$ → $0.018$

**Example 5.** $4.73 \times 100 = 473$; $58.2 \div 1000 = 0.0582$

**Estimate to check:** $3.4 \times 0.25$ is about $3 \times \frac{1}{4} \approx 0.8$ ✓

## 5. Common mistakes
- Lining up the last digits instead of the decimal points.
- Counting decimal places wrongly when multiplying (e.g. $0.2 \times 0.3 = 0.6$, but it's $0.06$).
- Thinking $0.5 \times 0.5$ is bigger than 0.5. Multiplying by a number less than 1 makes things smaller: $0.25$.

## 6. Practice
Q: $5.8 + 0.47$?
A: $6.27$.

Q: $10 - 3.25$?
A: $6.75$.

Q: $1.2 \times 0.4$?
A: $0.48$.

Q: $0.05 \times 0.04$?
A: $0.002$.

Q: $7.5 \div 100$?
A: $0.075$.

@@ 3 | TOPIC | Long division and decimal division | Dividing multi-digit numbers, dividing by decimals, remainders as decimals | MUST
# Long division and decimal division

## 1. What it covers
- The long-division method.
- Writing remainders as decimals.
- Dividing **by** a decimal (shift both numbers).

## 2. Where it is used
- **Splitting costs:** 2 350 taka shared by 6 people.
- **Unit prices:** cost per kg or per litre.
- **Averages:** total ÷ count.
- **Programming:** integer division `//` and remainder `%` are the quotient and remainder of long division; used for pagination ("page 3 of 12"), time formatting (seconds → minutes:seconds) and wrapping around arrays.

## 3. What it means
Long division breaks a big division into small steps, one digit at a time: **divide, multiply, subtract, bring down**, repeat.

To divide **by** a decimal, multiply both numbers by the same power of 10 to make the divisor whole. This doesn't change the answer (it's like an equivalent fraction): $\frac{4.5}{0.15} = \frac{450}{15}$.

## 4. Formula & worked example
$$
\text{dividend} = \text{divisor} \times \text{quotient} + \text{remainder}
$$

**Example 1.** $864 \div 6$
- $8 \div 6 = 1$ r 2 → bring down 6: $26 \div 6 = 4$ r 2 → bring down 4: $24 \div 6 = 4$ r 0 → **144**

**Example 2.** $47 \div 8$ as a decimal.
- $47 \div 8 = 5$ r 7 → add a decimal point and zeros: $70 \div 8 = 8$ r 6, $60 \div 8 = 7$ r 4, $40 \div 8 = 5$ r 0 → **5.875**

**Example 3.** $4.5 \div 0.15$
- Multiply both by 100: $450 \div 15 = 30$

**Example 4.** $7.82 \div 2.3$
- Multiply both by 10: $78.2 \div 23 = 3.4$

**Example 5 (code).** Convert 7 384 seconds to h:m:s.
```python
total = 7384
h, rest = divmod(total, 3600)    # 7384 = 2*3600 + 184
m, s = divmod(rest, 60)          # 184 = 3*60 + 4
print(f"{h}:{m:02}:{s:02}")      # 2:03:04
```
`divmod(a, b)` returns the quotient and the remainder of long division in one step.

## 5. Common mistakes
- Forgetting a 0 in the quotient when a step doesn't divide: $612 \div 6 = 102$, not $12$.
- Moving the point in only one of the two numbers when dividing by a decimal.
- Writing the remainder as a decimal digit directly ($47 \div 8 = 5.7$ ✗).

## 6. Practice
Q: $952 \div 7$?
A: $136$.

Q: $23 \div 4$ as a decimal?
A: $5.75$.

Q: $3.6 \div 0.12$?
A: $30$.

Q: $9.45 \div 0.5$?
A: $18.9$.

Q: 1000 items on pages of 30. How many full pages, and how many on the last page?
A: $1000 = 33 \times 30 + 10$: 33 full pages, and 10 items on the 34th page.

@@ 3 | TOPIC | Fractions to decimals: terminating and repeating | Converting both ways, which fractions terminate, repeating decimals to fractions | MUST
# Fractions to decimals: terminating and repeating

## 1. What it covers
- Converting fractions to decimals by division.
- **Terminating** vs **repeating** decimals, and how to predict which.
- Converting decimals (including repeating ones) back to fractions.

## 2. Where it is used
- **Computing:** $\frac{1}{3}$ and $\frac{1}{10}$ can't be stored exactly in binary floating point, which is the source of rounding bugs in finance software.
- **Measurement:** converting inches like $\frac{5}{8}$" to $0.625$".
- **Statistics:** probabilities shown as decimals or percentages.
- **Number sense:** recognising rational numbers.

## 3. What it means
$\frac{a}{b}$ **is** $a \div b$. Divide, and either the division ends (terminating) or the remainders start repeating, which makes the digits repeat.

**Which fractions terminate?** Simplify the fraction. If the denominator's only prime factors are **2 and 5**, it terminates (because $10 = 2 \times 5$). Otherwise it repeats.

**Repeating → fraction trick:** for $x = 0.\overline{27}$, multiply by 100 (two repeating digits): $100x = 27.\overline{27}$. Subtract: $99x = 27$ → $x = \frac{27}{99} = \frac{3}{11}$. The repeating tails cancel out.

## 4. Formula & worked example
$$
0.\overline{d} = \frac{d}{9} \qquad 0.\overline{d_1d_2} = \frac{d_1d_2}{99}
$$

**Example 1.** $\frac{3}{8} = 3 \div 8 = 0.375$ (terminates: $8 = 2^3$)

**Example 2.** $\frac{5}{12} = 0.41666\ldots = 0.41\overline{6}$ (repeats: $12$ has a factor 3)

**Example 3.** $\frac{7}{20}$: terminating? $20 = 2^2 \cdot 5$ → yes, $0.35$.

**Example 4.** $0.\overline{4}$ to a fraction.
- $x = 0.444\ldots$, $10x = 4.444\ldots$ → $9x = 4$ → $x = \frac{4}{9}$

**Example 5.** $0.1\overline{6}$ (only the 6 repeats).
- $10x = 1.666\ldots$, $100x = 16.666\ldots$ → $90x = 15$ → $x = \frac{15}{90} = \frac{1}{6}$

**Example 6.** $0.625$ to a fraction: $\frac{625}{1000} = \frac{5}{8}$

## 5. Common mistakes
- Writing $\frac{1}{3} = 0.3$ or $0.33$. It is $0.\overline{3}$; $0.33$ is only an approximation.
- Checking the denominator before simplifying. $\frac{3}{15} = \frac{1}{5}$ terminates even though 15 has a factor 3.
- Multiplying by the wrong power of 10 (use $10^n$ where $n$ is the length of the repeating block).

## 6. Practice
Q: $\frac{7}{16}$ as a decimal?
A: $0.4375$.

Q: Does $\frac{11}{30}$ terminate?
A: No: $30 = 2 \cdot 3 \cdot 5$ has a factor 3.

Q: $0.\overline{6}$ as a fraction?
A: $\frac{2}{3}$.

Q: $0.\overline{18}$ as a fraction?
A: $\frac{18}{99} = \frac{2}{11}$.

Q: Is $0.\overline{9}$ equal to 1?
A: Yes: $x = 0.\overline{9}$, $10x - x = 9$, so $x = 1$.

@@ 3 | TOPIC | Practice & review: Number foundations and fractions | Mixed problem set with worked answers | MUST
# Practice & review: Number foundations and fractions

**How to use this:** 45 minutes, no calculator. Log every miss in your error log.

## Problems
1. Classify $\sqrt{49}$, $-\frac{2}{5}$, $\sqrt{8}$, $0.\overline{3}$.
2. Prime factorization of 540, then GCF and LCM of 540 and 168.
3. Evaluate $36 \div (2 + 1)^2 \times 4 - 5$.
4. $\frac{5}{6} - \frac{3}{8} + \frac{1}{4}$.
5. $3\frac{1}{3} \times 2\frac{1}{4} \div 1\frac{1}{2}$.
6. $2.4 \times 0.15 + 7.2 \div 0.08$.
7. Write $\frac{13}{40}$ and $\frac{2}{7}$ as decimals (say which repeats).
8. Write $0.2\overline{7}$ as a fraction.
9. A rope is $12\frac{1}{2}$ m long. How many $\frac{5}{8}$ m pieces can be cut from it?
10. Two alarms ring every 45 and 60 minutes, both at 6:00. When do they next ring together?

## Answers
1. $\sqrt{49} = 7$: natural/whole/integer/rational/real; $-\frac{2}{5}$: rational, real; $\sqrt{8}$: irrational, real; $0.\overline{3} = \frac{1}{3}$: rational, real.
2. $540 = 2^2 \cdot 3^3 \cdot 5$; $168 = 2^3 \cdot 3 \cdot 7$. GCF $= 2^2 \cdot 3 = 12$; LCM $= 2^3 \cdot 3^3 \cdot 5 \cdot 7 = 7560$.
3. $36 \div 9 \times 4 - 5 = 4 \times 4 - 5 = 11$.
4. LCM 24: $\frac{20}{24} - \frac{9}{24} + \frac{6}{24} = \frac{17}{24}$.
5. $\frac{10}{3} \times \frac{9}{4} \times \frac{2}{3} = \frac{180}{36} = 5$.
6. $0.36 + 90 = 90.36$.
7. $0.325$ (terminates); $0.\overline{285714}$ (repeats).
8. $10x = 2.\overline{7}$, $100x = 27.\overline{7}$ → $90x = 25$ → $x = \frac{25}{90} = \frac{5}{18}$.
9. $\frac{25}{2} \div \frac{5}{8} = \frac{25}{2} \times \frac{8}{5} = 20$ pieces.
10. LCM$(45, 60) = 180$ min → 9:00.

## Flashcards
Q: Rule for adding fractions?
A: Common denominator (LCM), add numerators, keep the denominator, simplify.

Q: Rule for dividing fractions?
A: Multiply by the reciprocal of the second fraction.

Q: When does a simplified fraction give a terminating decimal?
A: When the denominator has no prime factors other than 2 and 5.

Q: GCF vs LCM with prime factors?
A: GCF: shared primes, lowest powers. LCM: all primes, highest powers.
