@@ 1 | MODULE | Phase 5 — Random Variables and Sampling Distributions (Core) | Discrete random variables, expected value, variance, combining variables, binomial, geometric and Poisson, continuous variables, sampling distributions, central limit theorem | MUST
# Phase 5: Random variables and sampling distributions

A **random variable** puts a number on a chance outcome: the number of defective items, the waiting time for a bus, tomorrow's sales. Its **distribution** says which values are likely. The key bridge to inference is the **sampling distribution**: how a statistic like the sample mean varies from sample to sample. The **central limit theorem** says this is approximately normal, which is why normal-based methods work almost everywhere.

@@ 2 | MODULE | Random variables and distributions | Discrete and continuous random variables, expected value, variance, binomial, geometric, Poisson | MUST
A random variable turns uncertainty into numbers you can calculate with.

@@ 3 | TOPIC | Discrete random variables and probability distributions | Definition, probability tables, valid distributions, cumulative probabilities | MUST
# Discrete random variables and probability distributions

## 1. What it covers
- **Random variables** $X$: numerical outcomes of a chance process.
- **Discrete** vs **continuous** random variables.
- **Probability distributions** as tables or formulas; validity rules.
- Cumulative probabilities $P(X \le x)$.

## 2. Where it is used
- **Operations:** number of customers per hour, number of defects per batch.
- **Software reliability:** number of failures per day; number of retries until success.
- **Insurance:** number of claims per policy.
- **Games:** outcomes and payouts.

## 3. What it means
A random variable assigns a number to each outcome. "Number of heads in 3 flips" can be 0, 1, 2 or 3.
- **Discrete:** countable values (counts).
- **Continuous:** any value in an interval (times, weights); handled with density curves (later topic).

A **probability distribution** lists each value with its probability. It's valid if every probability is between 0 and 1 and they **add up to 1**.

## 4. Formula & worked example
$$
0 \le P(X = x) \le 1 \qquad \sum_x P(X = x) = 1 \qquad P(X \le a) = \sum_{x \le a} P(X = x)
$$

**Example 1.** $X$ = number of heads in 3 fair flips (8 equally likely outcomes):
| $x$ | 0 | 1 | 2 | 3 |
|---|---|---|---|---|
| $P(X = x)$ | $\frac{1}{8}$ | $\frac{3}{8}$ | $\frac{3}{8}$ | $\frac{1}{8}$ |

**Example 2.** From the table: $P(X \ge 2) = \frac{3}{8} + \frac{1}{8} = \frac{1}{2}$.

**Example 3 (valid?).** $P(0) = 0.2$, $P(1) = 0.5$, $P(2) = 0.4$ → sums to 1.1 → not valid.

**Example 4 (missing value).** Customers per hour: $P(0) = 0.1$, $P(1) = 0.3$, $P(2) = 0.35$, $P(3) = k$ → $k = 0.25$.

**Example 5 (simulation, code).**
```python
import numpy as np
flips = np.random.randint(0, 2, size=(100_000, 3))   # 100k trials of 3 flips
heads = flips.sum(axis=1)
print(np.bincount(heads) / len(heads))                # ≈ [0.125 0.375 0.375 0.125]
```
Each row is one trial of 3 flips; summing the row counts the heads. The observed frequencies match the theoretical distribution.

## 5. Common mistakes
- Probabilities that don't sum to 1.
- Confusing $P(X < 2)$ with $P(X \le 2)$ for discrete variables (they differ!).
- Treating a continuous variable like a discrete one.

## 6. Practice
Q: Is "time to load a web page" discrete or continuous?
A: Continuous.

Q: $P(X = 1) = 0.2$, $P(X = 2) = 0.5$, $P(X = 3) = ?$ (only these values)
A: $0.3$.

Q: Using Example 1, $P(X \le 1)$?
A: $\frac{1}{2}$.

Q: Two conditions for a valid discrete distribution?
A: Each probability is between 0 and 1, and they sum to 1.

Q: For a discrete $X$, are $P(X < 3)$ and $P(X \le 3)$ equal?
A: Not in general; the second includes $P(X = 3)$.

@@ 3 | TOPIC | Expected value | Mean of a random variable, long-run average, games and insurance, linearity of expectation | MUST
# Expected value

## 1. What it covers
- **Expected value** $E(X) = \mu_X$: the long-run average of a random variable.
- Computing it from a distribution.
- Fair games, insurance premiums and decision making.
- **Linearity:** $E(aX + b) = aE(X) + b$ and $E(X + Y) = E(X) + E(Y)$.

## 2. Where it is used
- **Insurance and finance:** premiums are set above expected payouts; expected return of investments.
- **Business decisions:** expected profit of launching a product.
- **Gambling:** casinos rely on negative expected value for players.
- **Machine learning:** the loss being minimised is an expected value; reinforcement learning maximises expected reward.
- **Algorithms:** average-case running time (e.g. quicksort's expected $O(n\log n)$).

## 3. What it means
$E(X)$ weights each value by its probability. It's the average you'd get if you repeated the process **many** times: the balance point of the distribution. It doesn't have to be a possible value (the expected number of children per family can be 2.3).

**Linearity** is powerful: the expected value of a sum is the sum of the expected values, **even if the variables are dependent**.

## 4. Formula & worked example
$$
E(X) = \sum x\,P(X = x) \qquad E(aX + b) = aE(X) + b \qquad E(X + Y) = E(X) + E(Y)
$$

**Example 1 (die).** $E(X) = \frac{1 + 2 + 3 + 4 + 5 + 6}{6} = 3.5$

**Example 2 (game).** Pay 100 taka to play. Win 500 with probability 0.1, win 100 with probability 0.3, otherwise nothing. Expected net gain?
- $E(\text{winnings}) = 50 + 30 = 80$ → net $= 80 - 100 = -20$ taka per play (a losing game).

**Example 3 (insurance).** A phone costs 60 000; the chance of loss this year is 2%. Expected loss $= 1200$ taka. An insurer charging 2000 expects 800 profit per policy (to cover costs and risk).

**Example 4 (product decision).** Launching costs 5 lakh; 30% chance of 20 lakh profit, 50% chance of 5 lakh, 20% chance of nothing. $E = 6 + 2.5 + 0 - 5 = 3.5$ lakh → launch (if you can afford the risk).

**Example 5 (linearity).** Expected number of heads in 100 flips $= 100 \times 0.5 = 50$.

## 5. Common mistakes
- Forgetting to subtract the cost of playing.
- Thinking $E(X)$ is the most likely value (that's the mode).
- Assuming $E(XY) = E(X)E(Y)$ without independence.

## 6. Practice
Q: $X$ takes 0, 1, 2 with probabilities 0.5, 0.3, 0.2. $E(X)$?
A: $0.7$.

Q: $E(X) = 10$. $E(3X - 4)$?
A: $26$.

Q: What does $E(X)$ represent?
A: The long-run average value over many repetitions.

Q: A raffle: 1000 tickets at 50 taka, one prize of 20 000. Expected net value per ticket?
A: $20 - 50 = -30$ taka.

Q: Expected sum of two dice?
A: $7$.

@@ 3 | TOPIC | Variance of random variables and combining random variables | Var(X), SD, rules for aX + b, sums and differences of independent variables | MUST
# Variance of random variables and combining random variables

## 1. What it covers
- **Variance** $\text{Var}(X) = E[(X - \mu)^2]$ and SD of a random variable.
- Rules: $\text{Var}(aX + b) = a^2\text{Var}(X)$.
- For **independent** $X$ and $Y$: $\text{Var}(X \pm Y) = \text{Var}(X) + \text{Var}(Y)$.
- Why variances add even for differences.

## 2. Where it is used
- **Finance:** portfolio risk: combining assets (diversification reduces risk when they aren't perfectly correlated).
- **Manufacturing:** tolerance stacking: the total length of assembled parts.
- **Project management:** uncertainty in total project time.
- **Statistics:** the standard error of a mean or a difference (Phase 6) comes from these rules.

## 3. What it means
**Variance** measures how spread out a random variable is around its mean: the expected squared distance from $\mu$.

**Linear transformations:** adding a constant shifts values but doesn't change spread; multiplying by $a$ multiplies the SD by $\lvert a\rvert$ and the variance by $a^2$.

**Sums and differences of independent variables:** uncertainties **add up** in variance, for differences too. If two independent measurements each have some error, their difference is **more** uncertain, not less: errors don't cancel on average.

**Add variances, not SDs:** $\sigma_{X + Y} = \sqrt{\sigma_X^2 + \sigma_Y^2}$ (like Pythagoras).

## 4. Formula & worked example
$$
\text{Var}(X) = \sum(x - \mu)^2P(x) = E(X^2) - \mu^2 \qquad \text{Var}(aX + b) = a^2\text{Var}(X) \qquad \text{Var}(X \pm Y) = \text{Var}(X) + \text{Var}(Y)\ (\text{indep.})
$$

**Example 1.** $X$: 0, 1, 2 with probabilities 0.5, 0.3, 0.2. $\mu = 0.7$; $E(X^2) = 0.3 + 0.8 = 1.1$ → $\text{Var} = 1.1 - 0.49 = 0.61$, SD $\approx 0.78$.

**Example 2 (transform).** Daily sales $X$ have mean 200 units, SD 30. Revenue $= 50X - 1000$ → mean $9000$, SD $1500$.

**Example 3 (assembly).** Part A length: mean 40 mm, SD 0.3; part B: mean 25 mm, SD 0.4; independent. Total length: mean 65 mm, SD $= \sqrt{0.09 + 0.16} = 0.5$ mm.

**Example 4 (difference).** Same parts: difference $A - B$ has mean 15 mm and SD 0.5 mm (variances still add).

**Example 5 (diversification).** Two independent investments, each SD 20%. Put half in each: SD of the average $= \sqrt{0.25(400) + 0.25(400)} = \sqrt{200} \approx 14.1\%$, less risk with the same expected return.

## 5. Common mistakes
- Adding SDs instead of variances.
- Subtracting variances for a difference.
- Using the addition rule for dependent variables (then a covariance term is needed).

## 6. Practice
Q: $\text{Var}(X) = 4$. $\text{Var}(3X + 7)$?
A: $36$.

Q: Independent $X$ (SD 6) and $Y$ (SD 8). SD of $X + Y$?
A: $10$.

Q: SD of $X - Y$ for the same variables?
A: $10$.

Q: Why does adding a constant not change variance?
A: It shifts all values equally; distances from the mean stay the same.

Q: Shortcut formula for variance?
A: $E(X^2) - [E(X)]^2$.

@@ 3 | TOPIC | The binomial distribution | n independent trials with success probability p; formula, mean np, SD √(np(1 − p)), conditions | MUST
# The binomial distribution

## 1. What it covers
- The **binomial setting** (BINS): Binary outcomes, Independent trials, fixed Number $n$, Same probability $p$.
- The formula $P(X = k) = \binom{n}{k}p^k(1 - p)^{n-k}$.
- Mean $np$ and SD $\sqrt{np(1 - p)}$.
- Cumulative probabilities with software.

## 2. Where it is used
- **Quality control:** number of defective items in a batch.
- **A/B testing:** number of conversions out of $n$ visitors.
- **Medicine:** number of patients who respond to a treatment.
- **Networking:** number of lost packets out of $n$ sent.
- **Elections and polls:** number of supporters in a sample.

## 3. What it means
Repeat the same yes/no trial $n$ times independently, each with success probability $p$. $X$ counts the successes.

**Why the formula:** any particular sequence with $k$ successes has probability $p^k(1 - p)^{n-k}$, and there are $\binom{n}{k}$ ways to choose which trials are the successes.

**Mean** $np$: 100 visitors with a 5% conversion rate → expect 5 conversions. **SD** $\sqrt{np(1 - p)}$ tells you how much that count typically varies.

## 4. Formula & worked example
$$
P(X = k) = \binom{n}{k}p^k(1 - p)^{n - k} \qquad \mu = np \qquad \sigma = \sqrt{np(1 - p)}
$$

**Example 1.** A fair coin flipped 5 times. $P(\text{exactly 3 heads}) = \binom{5}{3}(0.5)^5 = \frac{10}{32} = 0.3125$.

**Example 2 (defects).** 4% of chips are defective; a batch of 20. $P(\text{no defects}) = 0.96^{20} \approx 0.442$; $P(\text{at least one}) \approx 0.558$.

**Example 3.** $P(\text{exactly 2 defective})$ in that batch: $\binom{20}{2}(0.04)^2(0.96)^{18} = 190 \times 0.0016 \times 0.4796 \approx 0.146$.

**Example 4 (mean and SD).** 400 website visitors, conversion rate 5%: $\mu = 20$, $\sigma = \sqrt{400 \times 0.05 \times 0.95} \approx 4.36$. So 20 ± about 9 conversions (two SDs) is normal variation; 35 would be surprising.

**Example 5 (code).**
```python
from scipy.stats import binom
print(binom.pmf(3, 5, 0.5))         # 0.3125   P(X = 3)
print(binom.cdf(2, 20, 0.04))       # 0.9561   P(X <= 2)
print(binom.sf(29, 400, 0.05))      # P(X >= 30) ≈ 0.019
```
`pmf` gives $P(X = k)$, `cdf` gives $P(X \le k)$, and `sf(k)` gives $P(X > k)$, so $P(X \ge 30)$ is `sf(29)`.

## 5. Common mistakes
- Using the binomial when trials aren't independent or $p$ changes (e.g. sampling without replacement from a small population).
- Forgetting the $\binom{n}{k}$ factor.
- Off-by-one errors with "at least" and "more than".

## 6. Practice
Q: What does BINS stand for?
A: Binary, Independent, Number fixed, Same probability.

Q: $n = 10$, $p = 0.3$. Mean and SD?
A: $3$ and $\sqrt{2.1} \approx 1.45$.

Q: $P(\text{exactly 2 sixes in 4 rolls})$?
A: $\binom{4}{2}\left(\frac{1}{6}\right)^2\left(\frac{5}{6}\right)^2 \approx 0.116$.

Q: Is "number of aces in 5 cards dealt from a deck" binomial?
A: Not exactly: draws are without replacement, so $p$ changes.

Q: How do you get $P(X \ge 5)$ with scipy?
A: `binom.sf(4, n, p)`.

@@ 3 | TOPIC | Geometric and Poisson distributions | Waiting for the first success; counts of rare events in an interval | SHOULD
# Geometric and Poisson distributions

## 1. What it covers
- The **geometric** distribution: number of trials until the **first** success.
- Its mean $\frac{1}{p}$.
- The **Poisson** distribution: number of events in a fixed interval when events happen independently at a constant average rate $\lambda$.
- Its mean and variance both equal $\lambda$.

## 2. Where it is used
- **Geometric:** number of login attempts until success; number of sales calls until the first sale; retries until a packet gets through.
- **Poisson:** customers arriving per hour, server requests per second, typos per page, website errors per day, calls to a help desk. It's the basis of **queueing theory** used to size servers and staff call centres.

## 3. What it means
**Geometric:** repeat independent trials with success probability $p$; $X$ is the trial on which the first success occurs. $P(X = k) = (1 - p)^{k-1}p$: $k - 1$ failures then a success. On average you wait $\frac{1}{p}$ trials (a 1-in-6 chance → 6 rolls on average to get a six).

**Poisson:** counts rare, independent events over time or space, with average rate $\lambda$ per interval. It's what the binomial becomes when $n$ is large and $p$ is small ($\lambda = np$).

## 4. Formula & worked example
$$
\text{Geometric: } P(X = k) = (1 - p)^{k - 1}p,\ E(X) = \frac{1}{p} \qquad \text{Poisson: } P(X = k) = \frac{e^{-\lambda}\lambda^k}{k!},\ E(X) = \text{Var}(X) = \lambda
$$

**Example 1 (geometric).** A salesperson closes 20% of calls. $P(\text{first sale on the 3rd call}) = 0.8^2 \times 0.2 = 0.128$. Expected calls until the first sale: 5.

**Example 2 (geometric, at most).** $P(\text{first sale within 5 calls}) = 1 - 0.8^5 \approx 0.672$.

**Example 3 (Poisson).** A help desk gets 4 calls per hour on average. $P(\text{exactly 2 calls in an hour}) = \frac{e^{-4}4^2}{2!} \approx 0.147$.

**Example 4 (Poisson, capacity).** A server gets 3 errors per day on average. $P(\text{more than 6 errors in a day})$?
- $1 - P(X \le 6) \approx 1 - 0.966 = 0.034$ → about once a month; use it to set alert thresholds.

**Example 5 (code).**
```python
from scipy.stats import geom, poisson
print(geom.pmf(3, 0.2))          # 0.128
print(poisson.pmf(2, 4))         # 0.1465
print(poisson.sf(6, 3))          # 0.0335  P(X > 6)
```
scipy's `geom` counts the trial number of the first success (starting at 1), matching the definition here.

## 5. Common mistakes
- Using the binomial (fixed $n$) when the number of trials isn't fixed (that's geometric).
- Using a Poisson model when events cluster or the rate changes over time (e.g. traffic at lunch vs night).
- Using the wrong interval for $\lambda$ (3 per day → 0.125 per hour).

## 6. Practice
Q: Mean of a geometric distribution with $p = 0.25$?
A: $4$.

Q: $P(\text{first head on the 4th flip})$?
A: $0.5^4 = 0.0625$.

Q: Poisson with $\lambda = 2$. $P(X = 0)$?
A: $e^{-2} \approx 0.135$.

Q: Mean and variance of a Poisson($\lambda$) distribution?
A: Both $\lambda$.

Q: 6 requests per minute on average. $\lambda$ for a 10-second window?
A: $1$.

@@ 3 | TOPIC | Continuous random variables | Density functions, uniform, exponential and normal as random variables, P(a < X < b) as area | SHOULD
# Continuous random variables

## 1. What it covers
- Continuous random variables and their **probability density functions** (PDFs).
- $P(a < X < b)$ as the area under the PDF (an integral).
- Mean $E(X) = \int x f(x)\,dx$.
- Key models: **uniform**, **exponential**, **normal**.

## 2. Where it is used
- **Reliability:** the time until a component fails (exponential, Weibull).
- **Queueing:** time between arrivals is exponential when arrivals are Poisson.
- **Measurement and natural variation:** normal.
- **Simulation:** generating random times and quantities.
- **ML:** probabilistic models with continuous outputs.

## 3. What it means
For continuous variables, single values have probability 0; only **intervals** have positive probability, given by the **area** under the density curve. (Phase 2 introduced density curves; here they're treated as random variables, with calculus where helpful.)

- **Uniform** $[a, b]$: every value equally likely; mean $\frac{a + b}{2}$.
- **Exponential** with rate $\lambda$: waiting time until the next event in a Poisson process; mean $\frac{1}{\lambda}$. It's **memoryless**: having waited 10 minutes doesn't change the distribution of the remaining wait.
- **Normal** $N(\mu, \sigma)$.

## 4. Formula & worked example
$$
P(a < X < b) = \int_a^b f(x)\,dx \qquad \text{Exponential: } f(x) = \lambda e^{-\lambda x},\ P(X > t) = e^{-\lambda t},\ E(X) = \frac{1}{\lambda}
$$

**Example 1 (uniform).** A delivery arrives uniformly between 2 pm and 5 pm. $P(\text{before 3 pm}) = \frac{1}{3}$; expected time 3:30 pm.

**Example 2 (exponential).** Customers arrive at 12 per hour (Poisson). Time between arrivals is exponential with $\lambda = 12$ per hour (mean 5 minutes). $P(\text{gap more than 10 min}) = e^{-12 \times (1/6)} = e^{-2} \approx 0.135$.

**Example 3 (reliability).** A hard drive's lifetime is exponential with mean 5 years. $P(\text{lasts more than 8 years}) = e^{-8/5} \approx 0.202$.

**Example 4 (memoryless).** Given the drive has lasted 3 years, $P(\text{lasts 8 more})$ is still $0.202$.

**Example 5 (code).**
```python
from scipy.stats import expon, uniform
print(expon.sf(10, scale=5))           # 0.1353  P(gap > 10 min), mean 5 min
print(uniform.cdf(3, loc=2, scale=3))  # 0.3333  P(arrival before 3) on [2, 5]
```
scipy parametrises the exponential by its mean (`scale = 1/λ`) and the uniform by its start (`loc`) and width (`scale`).

## 5. Common mistakes
- Giving positive probability to a single value of a continuous variable.
- Confusing the rate $\lambda$ with the mean $\frac{1}{\lambda}$ (especially in scipy's `scale`).
- Using an exponential model for things that wear out (old components fail more often, so they aren't memoryless).

## 6. Practice
Q: Uniform on $[0, 4]$. $P(1 < X < 2)$?
A: $0.25$.

Q: Exponential with mean 2 hours. $P(X > 2)$?
A: $e^{-1} \approx 0.368$.

Q: Mean of an exponential with rate $\lambda = 0.5$ per minute?
A: $2$ minutes.

Q: What does "memoryless" mean?
A: The remaining waiting time doesn't depend on how long you've already waited.

Q: How is $P(a < X < b)$ found for a continuous variable?
A: As the area under the density curve between $a$ and $b$.

@@ 2 | MODULE | Sampling distributions | Sampling variability, distributions of proportions and means, standard error, central limit theorem | MUST
A statistic (like a sample mean) is itself a random variable: it changes from sample to sample.

@@ 3 | TOPIC | Sampling distributions and the sample proportion | Parameter vs statistic, sampling variability, mean and standard error of p̂, normal approximation | MUST
# Sampling distributions and the sample proportion

## 1. What it covers
- **Parameter** (population value, fixed but unknown) vs **statistic** (from a sample, varies).
- The **sampling distribution**: the distribution of a statistic over all possible samples.
- The sample proportion $\hat{p}$: mean $p$, standard error $\sqrt{\frac{p(1 - p)}{n}}$.
- When $\hat{p}$ is approximately normal ($np \ge 10$ and $n(1 - p) \ge 10$).

## 2. Where it is used
- **Polling:** the "margin of error" comes from the SD of $\hat{p}$.
- **A/B testing:** whether a difference in conversion rates is bigger than random variation.
- **Quality control:** control charts for defect proportions.
- **ML evaluation:** the uncertainty of a model's accuracy measured on a finite test set.

## 3. What it means
If 54% of a sample supports a candidate, the true population proportion is probably not exactly 54%. Take another sample and you'd get, say, 51%. The **sampling distribution** describes this sample-to-sample variation.

For sample proportions:
- **Centre:** $p$ (unbiased: on average, samples get it right).
- **Spread:** the **standard error** $\sqrt{\frac{p(1 - p)}{n}}$, which shrinks as $n$ grows. Quadrupling the sample size halves the error.
- **Shape:** approximately normal when there are at least 10 expected successes and failures.

Notice the population size doesn't appear (as long as the sample is less than about 10% of it): a sample of 1000 is as precise for Bangladesh as for a single city.

## 4. Formula & worked example
$$
\mu_{\hat{p}} = p \qquad \sigma_{\hat{p}} = \sqrt{\frac{p(1 - p)}{n}}
$$

**Example 1.** True support $p = 0.5$, sample $n = 1000$: $\sigma_{\hat{p}} = \sqrt{\frac{0.25}{1000}} \approx 0.0158$. About 95% of samples give $\hat{p}$ within $0.5 \pm 0.032$ (the familiar "± 3%" margin).

**Example 2.** $p = 0.1$, $n = 50$: $np = 5 < 10$ → the normal approximation isn't reliable (use binomial calculations instead).

**Example 3 (probability).** A website's true conversion rate is 8%. With 2000 visitors, how likely is an observed rate above 9.5%?
- $\sigma = \sqrt{\frac{0.08 \times 0.92}{2000}} \approx 0.00607$; $z = \frac{0.015}{0.00607} \approx 2.47$ → $P \approx 0.007$. Seeing 9.5% would be unusual if nothing had changed.

**Example 4 (simulation).**
```python
import numpy as np
p, n = 0.08, 2000
phats = np.random.binomial(n, p, size=100_000) / n   # 100k simulated samples
print(phats.mean(), phats.std())                       # ≈ 0.080  0.00607
```
Each simulated sample gives one $\hat{p}$; the collection of 100 000 values **is** the sampling distribution, and its SD matches the formula.

## 5. Common mistakes
- Confusing the population SD with the standard error of a statistic.
- Thinking a bigger population needs a bigger sample (it barely matters).
- Using the normal approximation when expected counts are below 10.

## 6. Practice
Q: Parameter or statistic: "62% of 500 surveyed users like the feature"?
A: Statistic.

Q: Standard error of $\hat{p}$ for $p = 0.3$, $n = 100$?
A: $\sqrt{0.0021} \approx 0.046$.

Q: To halve the standard error, multiply $n$ by?
A: 4.

Q: Conditions for $\hat{p}$ to be approximately normal?
A: $np \ge 10$ and $n(1 - p) \ge 10$.

Q: Is $\hat{p}$ an unbiased estimator of $p$?
A: Yes, its mean is $p$.

@@ 3 | TOPIC | The sample mean and the central limit theorem | Mean and standard error of x̄, σ/√n, the central limit theorem, why normality appears | MUST
# The sample mean and the central limit theorem

## 1. What it covers
- The sampling distribution of $\bar{x}$: mean $\mu$, standard error $\frac{\sigma}{\sqrt{n}}$.
- The **central limit theorem (CLT)**: for large $n$, $\bar{x}$ is approximately normal **whatever** the population's shape.
- Rules of thumb ($n \ge 30$) and probability calculations for $\bar{x}$.

## 2. Where it is used
- **Every confidence interval and t-test** for means relies on it.
- **A/B testing:** comparing average order values, even though individual order values are very skewed.
- **Performance testing:** averaging many latency measurements.
- **Quality control:** $\bar{x}$ control charts.
- **Measurement:** averaging repeated readings reduces error by $\sqrt{n}$.

## 3. What it means
Averages are **less variable** than individual values: one person's height varies a lot, but the average height of 100 people barely changes from group to group. The SD of $\bar{x}$ is $\frac{\sigma}{\sqrt{n}}$.

**Central limit theorem:** take many samples of size $n$ from **any** population (skewed, lumpy, whatever) with finite SD. The distribution of the sample means becomes approximately **normal** as $n$ grows. That's why the bell curve appears everywhere: many outcomes are averages or sums of lots of small effects.

Rule of thumb: $n \ge 30$ is usually enough; more for very skewed populations; any $n$ if the population itself is normal.

## 4. Formula & worked example
$$
\mu_{\bar{x}} = \mu \qquad \sigma_{\bar{x}} = \frac{\sigma}{\sqrt{n}} \qquad \bar{x} \approx N\!\left(\mu, \frac{\sigma}{\sqrt{n}}\right) \text{ for large } n
$$

**Example 1.** Order values: mean 1200 taka, SD 900 (right-skewed). Average of 100 orders: mean 1200, SE $= \frac{900}{10} = 90$, approximately normal.

**Example 2.** $P(\bar{x} > 1350)$ for those 100 orders? $z = \frac{150}{90} \approx 1.67$ → $P \approx 0.048$.

**Example 3 (individual vs average).** $P(\text{one order} > 1350)$ can't be found with the normal table because individual orders are skewed; but for the **average** of 100, the CLT makes it possible.

**Example 4 (measurement).** A scale has SD 2 g per reading. Averaging 16 readings gives SD $\frac{2}{4} = 0.5$ g.

**Example 5 (simulation: CLT in action).**
```python
import numpy as np
pop = np.random.exponential(scale=1200, size=1_000_000)   # strongly right-skewed
means = [np.random.choice(pop, 100).mean() for _ in range(10_000)]
print(np.mean(means), np.std(means))                        # ≈ 1200  ≈ 120 (= 1200/√100)
# a histogram of `means` looks like a bell curve, even though `pop` is very skewed
```
The exponential population has mean = SD = 1200 and is very lopsided, yet the 10 000 sample means are bell-shaped, centred at 1200, with SD $\frac{1200}{\sqrt{100}} = 120$.

## 5. Common mistakes
- Thinking the CLT makes the **data** normal (it's about the distribution of **means**).
- Using $\sigma$ instead of $\frac{\sigma}{\sqrt{n}}$ for probabilities about $\bar{x}$.
- Applying it to tiny samples from very skewed populations.

## 6. Practice
Q: Standard error of $\bar{x}$ with $\sigma = 20$, $n = 25$?
A: $4$.

Q: State the central limit theorem.
A: For large $n$, the sampling distribution of $\bar{x}$ is approximately normal, whatever the population's shape.

Q: Population mean 50, SD 10. $P(\bar{x} > 52)$ for $n = 100$?
A: SE $= 1$, $z = 2$ → $\approx 0.023$.

Q: Does the CLT make individual observations normal?
A: No, only the distribution of sample means.

Q: How many readings must you average to cut the error to a third?
A: 9.

@@ 3 | TOPIC | Practice & review: Random variables and sampling distributions | Mixed problem set with worked answers | MUST
# Practice & review: Random variables and sampling distributions

**How to use this:** 50 minutes; calculator or Python allowed.

## Problems
1. $X$ = number of items a customer buys: $P(1) = 0.4$, $P(2) = 0.3$, $P(3) = 0.2$, $P(4) = 0.1$. Find $E(X)$ and $\text{SD}(X)$.
2. Each item gives 150 taka profit, minus a 50 taka fixed cost per customer. Mean and SD of profit per customer?
3. A test has 10 multiple-choice questions with 4 options. Guessing randomly, $P(\text{at least 5 correct})$?
4. 2% of emails bounce. In 300 emails, what are the mean and SD of the number of bounces? Is 15 bounces unusual?
5. Typos occur at 1.5 per page. $P(\text{a page has no typos})$? $P(\text{3 or more})$?
6. A bus comes every 15 minutes (uniform wait). $P(\text{wait} > 10 \text{ min})$? Mean wait?
7. True defect rate 5%; you inspect 400 items. Mean and SE of $\hat{p}$; $P(\hat{p} > 0.07)$?
8. Delivery times have mean 40 min, SD 15 min (skewed). For a random sample of 36 deliveries, $P(\bar{x} < 36)$?

## Answers
1. $E = 0.4 + 0.6 + 0.6 + 0.4 = 2$; $E(X^2) = 0.4 + 1.2 + 1.8 + 1.6 = 5$ → $\text{Var} = 1$, SD $= 1$.
2. Profit $= 150X - 50$: mean 250, SD 150.
3. Binomial $n = 10$, $p = 0.25$: $P(X \ge 5) \approx 0.078$.
4. Mean 6, SD $\sqrt{5.88} \approx 2.42$; 15 is $z \approx 3.7$ → very unusual.
5. $e^{-1.5} \approx 0.223$; $1 - P(0) - P(1) - P(2) = 1 - 0.223 - 0.335 - 0.251 \approx 0.191$.
6. $\frac{5}{15} = \frac{1}{3}$; 7.5 min.
7. Mean 0.05; SE $= \sqrt{\frac{0.0475}{400}} \approx 0.0109$; $z \approx 1.84$ → $P \approx 0.033$.
8. SE $= \frac{15}{6} = 2.5$; $z = -1.6$ → $P \approx 0.055$.

## Flashcards
Q: Expected value formula?
A: $E(X) = \sum xP(x)$.

Q: Binomial mean and SD?
A: $np$ and $\sqrt{np(1 - p)}$.

Q: Standard error of $\bar{x}$?
A: $\frac{\sigma}{\sqrt{n}}$.

Q: Variance of the sum of independent variables?
A: The sum of their variances.
