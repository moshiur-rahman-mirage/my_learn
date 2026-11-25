@@ 1 | MODULE | Phase 6 — Statistical Inference (Advanced) | Confidence intervals, hypothesis tests, errors and power, p-hacking, two-sample methods, chi-square tests, regression inference, multiple regression, ANOVA, tests in Python | MUST
# Phase 6: Statistical inference

**Inference** means using a sample to draw conclusions about a whole population, with an honest statement of uncertainty. Two main tools:
- **Confidence intervals:** a range of plausible values for an unknown parameter ("conversion rate 6.2% ± 0.8%").
- **Hypothesis tests:** is an observed effect real, or could it be random chance? ("Is version B really better?")

These are the methods behind A/B tests, clinical trials, polls, scientific papers and data-driven business decisions.

@@ 2 | MODULE | Confidence intervals | Point estimates, margin of error, intervals for proportions and means, interpretation, sample size | MUST
An estimate without a margin of error is only half an answer.

@@ 3 | TOPIC | Confidence intervals for a proportion | Point estimate ± margin of error, critical values, conditions, construction | MUST
# Confidence intervals for a proportion

## 1. What it covers
- **Point estimate** and **margin of error**.
- The structure: estimate ± (critical value) × (standard error).
- The interval $\hat{p} \pm z^*\sqrt{\frac{\hat{p}(1 - \hat{p})}{n}}$.
- Conditions: random sample, $n\hat{p} \ge 10$ and $n(1 - \hat{p}) \ge 10$, independence.

## 2. Where it is used
- **Polls:** "45% ± 3% at 95% confidence".
- **Product analytics:** conversion rate, churn rate, click-through rate with uncertainty.
- **Quality control:** estimated defect rate.
- **ML evaluation:** a confidence interval for a model's accuracy on a test set.

## 3. What it means
A sample proportion $\hat{p}$ is our best single guess (point estimate), but it varies from sample to sample. A **confidence interval** gives a range likely to contain the true $p$.

The SE measures typical sampling error. Going about **2 SEs** each way (exactly $z^* = 1.96$) captures the truth in 95% of samples, since $\hat{p}$ is approximately normal.

Common critical values: 90% → 1.645; 95% → 1.96; 99% → 2.576. Higher confidence needs a wider interval: it's like casting a wider net to be more sure of catching the fish.

## 4. Formula & worked example
$$
\hat{p} \pm z^*\sqrt{\frac{\hat{p}(1 - \hat{p})}{n}} \qquad \text{margin of error } ME = z^*\,SE
$$

**Example 1 (poll).** 540 of 1200 voters support a candidate. 95% CI?
- $\hat{p} = 0.45$; $SE = \sqrt{\frac{0.45 \times 0.55}{1200}} \approx 0.01436$; $ME = 1.96 \times 0.01436 \approx 0.028$
- CI: $0.45 \pm 0.028$ → $(0.422, 0.478)$, i.e. 42.2% to 47.8%.

**Example 2 (conversion).** 96 purchases from 1500 visitors: $\hat{p} = 0.064$; $SE \approx 0.00632$; 95% CI: $0.064 \pm 0.0124$ → $(5.2\%, 7.6\%)$.

**Example 3 (99%).** Same data at 99%: $ME = 2.576 \times 0.00632 \approx 0.0163$ → $(4.8\%, 8.0\%)$ (wider).

**Example 4 (conditions).** 4 defects in 80 items: $n\hat{p} = 4 < 10$ → the normal-based interval is unreliable (use an exact or Wilson interval).

**Example 5 (code).**
```python
from statsmodels.stats.proportion import proportion_confint
print(proportion_confint(540, 1200, alpha=0.05, method="normal"))   # (0.4219, 0.4781)
print(proportion_confint(4, 80, alpha=0.05, method="wilson"))       # better for small counts
```
`method="normal"` is the formula above; the Wilson method behaves much better when counts are small or $\hat{p}$ is near 0 or 1.

## 5. Common mistakes
- Using $p$ (unknown) instead of $\hat{p}$ in the SE.
- Forgetting to check the success/failure conditions.
- Thinking a 95% CI contains 95% of the data (it's about the parameter).

## 6. Practice
Q: Critical value $z^*$ for 95% confidence?
A: $1.96$.

Q: 200 of 500 say yes. 95% margin of error?
A: $\hat{p} = 0.4$, $SE \approx 0.0219$, $ME \approx 0.043$.

Q: What happens to the interval if confidence goes from 95% to 99%?
A: It gets wider.

Q: What happens to the margin of error if $n$ is quadrupled?
A: It halves.

Q: Conditions for the proportion interval?
A: Random sample, independence, at least 10 successes and 10 failures.

@@ 3 | TOPIC | Confidence intervals for a mean (t-intervals) | Unknown σ, the t-distribution, degrees of freedom, constructing and checking conditions | MUST
# Confidence intervals for a mean (t-intervals)

## 1. What it covers
- Why we use the **t-distribution** when $\sigma$ is unknown.
- **Degrees of freedom** $df = n - 1$.
- The interval $\bar{x} \pm t^*\frac{s}{\sqrt{n}}$.
- Conditions: random sample, independence, approximately normal or $n \ge 30$.

## 2. Where it is used
- **Product metrics:** average order value, average session time, average latency with uncertainty.
- **Science and medicine:** average effect of a treatment.
- **Manufacturing:** average fill volume of bottles.
- **Benchmarking:** average run time of code over repeated runs.

## 3. What it means
We rarely know the population SD $\sigma$, so we use the sample SD $s$. That adds extra uncertainty, especially for small samples. The **t-distribution** accounts for it: it looks like the normal curve but with **heavier tails** (so $t^* > z^*$). As $n$ grows, $t$ approaches the normal.

The interval has the same shape as before: estimate ± critical value × SE, with $SE = \frac{s}{\sqrt{n}}$.

## 4. Formula & worked example
$$
\bar{x} \pm t^*_{n-1}\frac{s}{\sqrt{n}}
$$
($t^*$ for 95%: $df = 9$ → 2.262; $df = 24$ → 2.064; $df = 99$ → 1.984.)

**Example 1.** 10 bottles: mean fill 498.2 mL, $s = 2.66$ mL. 95% CI?
- $SE = \frac{2.66}{\sqrt{10}} \approx 0.841$; $t^* = 2.262$ → $498.2 \pm 1.90$ → $(496.3, 500.1)$ mL
- 500 mL is (just) inside the interval, so the data are consistent with the target.

**Example 2 (larger sample).** 100 orders: $\bar{x} = 1240$ taka, $s = 850$. 95% CI: $1240 \pm 1.984 \times 85 \approx 1240 \pm 169$ → $(1071, 1409)$.

**Example 3 (benchmark).** 25 runs of a function: mean 12.4 ms, $s = 1.5$ ms → $12.4 \pm 2.064 \times 0.3 = 12.4 \pm 0.62$ ms.

**Example 4 (code).**
```python
import numpy as np
from scipy import stats
x = np.array([497, 501, 495, 499, 502, 496, 498, 500, 494, 500])
ci = stats.t.interval(0.95, df=len(x)-1, loc=x.mean(), scale=stats.sem(x))
print(x.mean(), ci)      # 498.2 (496.30, 500.10)
```
`stats.sem` computes $\frac{s}{\sqrt{n}}$ (with the sample SD), and `t.interval` applies the t critical value with $n - 1$ degrees of freedom.

## 5. Common mistakes
- Using $z^*$ instead of $t^*$ for small samples with unknown $\sigma$.
- Using $\sigma$ formulas with $s$ but forgetting $\sqrt{n}$.
- Applying t-intervals to small, strongly skewed samples or with outliers.

## 6. Practice
Q: Why use $t$ instead of $z$?
A: Because $\sigma$ is estimated by $s$, adding uncertainty.

Q: Degrees of freedom for a sample of 16?
A: $15$.

Q: $n = 16$, $\bar{x} = 50$, $s = 8$, $t^* = 2.131$. 95% CI?
A: $50 \pm 2.131 \times 2 = (45.74, 54.26)$.

Q: How does the t-distribution compare with the normal?
A: Heavier tails; it approaches the normal as $df$ grows.

Q: Condition for small samples?
A: The population should be roughly normal (no strong skew or outliers).

@@ 3 | TOPIC | Interpreting confidence intervals and choosing sample size | Correct interpretation, common misreadings, what affects width, sample size formulas | MUST
# Interpreting confidence intervals and choosing sample size

## 1. What it covers
- The correct interpretation of a confidence interval and of the confidence **level**.
- Common misinterpretations.
- What makes intervals wider or narrower.
- Choosing a **sample size** for a desired margin of error.

## 2. Where it is used
- **Survey planning:** how many people to interview for ± 3%.
- **A/B test planning:** how many users are needed.
- **Reporting results** honestly to stakeholders.
- **Clinical trial design.**

## 3. What it means
**Correct:** "We are 95% confident that the true proportion is between 42.2% and 47.8%."

**What "95% confident" means:** the **method** captures the true value in 95% of samples. If 100 different teams each took a sample and built a 95% CI, about 95 of the intervals would contain the truth and about 5 would miss. For any one interval, the truth is either in it or not; we just don't know which.

**Wrong interpretations:**
- "There's a 95% probability the true value is in this interval" (the parameter isn't random).
- "95% of the data are in this interval."
- "95% of sample means fall in this interval."

**Width** depends on: confidence level (higher → wider), sample size (larger → narrower, by $\sqrt{n}$), variability (more → wider).

**Sample size:** solve the margin-of-error formula for $n$. If you have no estimate of $p$, use $p = 0.5$ (the worst case, giving the largest $n$).

## 4. Formula & worked example
$$
n = \left(\frac{z^*}{ME}\right)^2 p(1 - p) \qquad n = \left(\frac{z^*\sigma}{ME}\right)^2
$$
(Always round **up**.)

**Example 1 (poll).** Margin of error 3% at 95%, no prior estimate:
- $n = \left(\frac{1.96}{0.03}\right)^2(0.25) \approx 1067.1$ → 1068 people (why national polls often survey about 1000).

**Example 2 (±1%).** $n = \left(\frac{1.96}{0.01}\right)^2(0.25) = 9604$: to cut the margin to a third, you need 9 times the sample.

**Example 3 (mean).** Estimate average delivery time within ± 2 minutes at 95%, $\sigma \approx 12$:
- $n = \left(\frac{1.96 \times 12}{2}\right)^2 \approx 138.3$ → 139 deliveries.

**Example 4 (interpretation).** A 95% CI for mean session length is (4.1, 4.9) minutes. Correct statement: "We're 95% confident the true average session length is between 4.1 and 4.9 minutes."

## 5. Common mistakes
- Saying "95% probability the parameter is in this interval".
- Rounding the sample size down.
- Thinking a larger population requires a much larger sample.

## 6. Practice
Q: What does a 95% confidence level mean?
A: In repeated sampling, 95% of intervals built this way contain the true parameter.

Q: Sample size for ±5% at 95% with $p = 0.5$?
A: $\left(\frac{1.96}{0.05}\right)^2(0.25) = 384.2$ → 385.

Q: Three things that make a CI narrower?
A: Larger sample, lower confidence level, less variability.

Q: Is "95% of the data lie in the CI" correct?
A: No.

Q: Why use $p = 0.5$ when planning?
A: It maximises $p(1 - p)$, giving a safe (large enough) sample size.

@@ 2 | MODULE | Hypothesis testing | Hypotheses, p-values, one-sample tests, errors and power, pitfalls | MUST
A test asks: if nothing interesting were going on, how surprising would these data be?

@@ 3 | TOPIC | Hypothesis tests and p-values | Null and alternative hypotheses, test statistics, p-values, significance level, conclusions in context | MUST
# Hypothesis tests and p-values

## 1. What it covers
- **Null hypothesis** $H_0$ (no effect / no difference) and **alternative** $H_a$.
- One-sided vs two-sided alternatives.
- The **test statistic** and the **p-value**.
- **Significance level** $\alpha$ and writing conclusions.

## 2. Where it is used
- **A/B testing:** is the new design's conversion rate really higher?
- **Medicine:** does the drug work better than placebo?
- **Manufacturing:** has the process mean shifted?
- **Science:** most published findings report p-values.

## 3. What it means
A hypothesis test works like a court trial:
- $H_0$ is "innocent" (no effect); we assume it unless the evidence is strong.
- The data are the evidence.
- The **p-value** is the probability of getting results **at least as extreme as ours if $H_0$ were true**. A small p-value means our data would be very surprising under $H_0$, which is evidence against it.
- If $p \le \alpha$ (commonly 0.05), we **reject $H_0$**: the result is **statistically significant**. Otherwise we **fail to reject $H_0$** (not enough evidence). We never "prove" $H_0$ ("not guilty" isn't "innocent").

**Steps:**
1. State $H_0$ and $H_a$ (about the **parameter**), and $\alpha$.
2. Check conditions.
3. Compute the test statistic: $\frac{\text{statistic} - \text{null value}}{SE}$.
4. Find the p-value.
5. Conclude **in context**.

## 4. Formula & worked example
$$
\text{test statistic} = \frac{\text{observed} - \text{hypothesised}}{SE} \qquad \text{reject } H_0 \text{ if } p\text{-value} \le \alpha
$$

**Example 1 (hypotheses).** A website's conversion rate has been 5%. After a redesign you want to know if it increased.
- $H_0: p = 0.05$; $H_a: p > 0.05$ (one-sided).

**Example 2 (two-sided).** Is a coin fair? $H_0: p = 0.5$; $H_a: p \ne 0.5$.

**Example 3 (interpret a p-value).** $p = 0.03$: "If the true rate were still 5%, there'd be only a 3% chance of seeing a sample rate this high or higher. At $\alpha = 0.05$, we reject $H_0$: there's convincing evidence the conversion rate increased."

**Example 4 (not significant).** $p = 0.21$: "We fail to reject $H_0$; the data don't provide convincing evidence of an increase." (Not: "the redesign has no effect.")

**Example 5 (simulation view).**
```python
import numpy as np
# Observed 62 heads in 100 flips. How surprising if the coin is fair?
sims = np.random.binomial(100, 0.5, 100_000)
print(np.mean(np.abs(sims - 50) >= 12))   # ≈ 0.02 (two-sided p-value)
```
Simulating many fair-coin experiments shows that a result at least 12 away from 50 happens only about 2% of the time, which is the p-value.

## 5. Common mistakes
- Writing hypotheses about the sample statistic ($\hat{p}$) instead of the parameter ($p$).
- Interpreting the p-value as "the probability $H_0$ is true".
- "Accepting" $H_0$ instead of "failing to reject" it.

## 6. Practice
Q: What does the null hypothesis usually state?
A: No effect, no difference, or the status quo value.

Q: Define a p-value.
A: The probability of results at least as extreme as observed, assuming $H_0$ is true.

Q: $p = 0.004$, $\alpha = 0.05$. Decision?
A: Reject $H_0$.

Q: Is "fail to reject $H_0$" the same as "$H_0$ is true"?
A: No, it means there isn't enough evidence against it.

Q: Write hypotheses: is the mean delivery time different from 30 minutes?
A: $H_0: \mu = 30$; $H_a: \mu \ne 30$.

@@ 3 | TOPIC | One-sample z-test for a proportion | Full procedure: hypotheses, conditions, z statistic, p-value, conclusion | MUST
# One-sample z-test for a proportion

## 1. What it covers
- Testing a claim about a population proportion $p$.
- The test statistic $z = \frac{\hat{p} - p_0}{\sqrt{p_0(1 - p_0)/n}}$ (using $p_0$ in the SE).
- One- and two-sided p-values; the link with confidence intervals.

## 2. Where it is used
- **Product:** has the sign-up rate changed from its historical 12%?
- **Quality:** is the defect rate above the 2% contract limit?
- **Elections:** does a candidate have majority support?
- **Fairness checks:** is a random process (shuffle, coin, RNG) biased?

## 3. What it means
Under $H_0$ the true proportion is $p_0$, so the sampling distribution of $\hat{p}$ is approximately normal with mean $p_0$ and SE $\sqrt{\frac{p_0(1 - p_0)}{n}}$. The z-statistic counts how many SEs the observed $\hat{p}$ is from $p_0$.

**Conditions:** random sample; $np_0 \ge 10$ and $n(1 - p_0) \ge 10$; sample less than 10% of the population.

**Link with CIs:** a two-sided test at $\alpha = 0.05$ rejects $H_0$ exactly when $p_0$ is outside the 95% CI (approximately, since the SEs differ slightly).

## 4. Formula & worked example
$$
z = \frac{\hat{p} - p_0}{\sqrt{\dfrac{p_0(1 - p_0)}{n}}}
$$

**Example 1.** Historically 12% of visitors sign up. After a change, 150 of 1000 sign up. Did it increase? ($\alpha = 0.05$)
- $H_0: p = 0.12$, $H_a: p > 0.12$. Conditions: $120 \ge 10$, $880 \ge 10$ ✓
- $\hat{p} = 0.15$; $SE = \sqrt{\frac{0.12 \times 0.88}{1000}} \approx 0.01028$; $z = \frac{0.03}{0.01028} \approx 2.92$
- p-value $= P(Z > 2.92) \approx 0.0018$ → reject $H_0$. Strong evidence the sign-up rate increased.

**Example 2 (two-sided).** A coin gives 58 heads in 100 flips. Fair?
- $z = \frac{0.58 - 0.5}{0.05} = 1.6$; p-value $= 2 \times 0.0548 \approx 0.11$ → fail to reject; not convincing evidence of bias.

**Example 3 (defects).** Contract limit 2%. A sample of 800 has 24 defects (3%). Is the rate above 2%?
- $z = \frac{0.01}{\sqrt{0.02 \times 0.98/800}} = \frac{0.01}{0.00495} \approx 2.02$ → p-value $\approx 0.022$ → reject at 5%.

**Example 4 (code).**
```python
from statsmodels.stats.proportion import proportions_ztest
z, p = proportions_ztest(count=150, nobs=1000, value=0.12, alternative="larger", prop_var=0.12)
print(round(z, 2), round(p, 4))     # 2.92 0.0018
```
`prop_var=0.12` tells statsmodels to use $p_0$ in the standard error, matching the textbook test.

## 5. Common mistakes
- Using $\hat{p}$ instead of $p_0$ in the SE for the test.
- Forgetting to double the one-tail area for a two-sided test.
- Checking conditions with $\hat{p}$ instead of $p_0$.

## 6. Practice
Q: Test statistic for a proportion?
A: $z = \frac{\hat{p} - p_0}{\sqrt{p_0(1 - p_0)/n}}$.

Q: $\hat{p} = 0.55$, $p_0 = 0.5$, $n = 400$. $z$?
A: $SE = 0.025$ → $z = 2$.

Q: Two-sided p-value for $z = 2$?
A: $\approx 0.046$.

Q: Why use $p_0$ in the SE?
A: The test assumes $H_0$ is true, so $p = p_0$.

Q: $H_a: p < 0.3$ and $z = -1.2$. p-value?
A: $P(Z < -1.2) \approx 0.115$.

@@ 3 | TOPIC | One-sample t-test for a mean | Testing a population mean with unknown σ; procedure, conditions and interpretation | MUST
# One-sample t-test for a mean

## 1. What it covers
- Testing $H_0: \mu = \mu_0$ when $\sigma$ is unknown.
- The test statistic $t = \frac{\bar{x} - \mu_0}{s/\sqrt{n}}$ with $df = n - 1$.
- Conditions and conclusions.

## 2. Where it is used
- **Quality control:** is the mean fill volume really 500 mL?
- **Service levels:** is average response time above the 200 ms target?
- **Product claims:** does a battery last the advertised 10 hours on average?
- **Benchmarking:** did a code change shift the mean run time?

## 3. What it means
Same logic as the proportion test, but for means: how many standard errors is the sample mean from the hypothesised mean? Because $s$ estimates $\sigma$, the statistic follows a t-distribution with $n - 1$ degrees of freedom.

**Conditions:** random sample; independence; population roughly normal or $n \ge 30$ (check a histogram for strong skew or outliers when $n$ is small).

## 4. Formula & worked example
$$
t = \frac{\bar{x} - \mu_0}{s / \sqrt{n}}, \quad df = n - 1
$$

**Example 1 (bottles).** Target 500 mL. A sample of 10: $\bar{x} = 498.2$, $s = 2.66$. Is the mean different from 500? ($\alpha = 0.05$)
- $t = \frac{-1.8}{0.841} \approx -2.14$, $df = 9$ → two-sided p-value $\approx 0.061$ → fail to reject at 5% (it's borderline). Not convincing evidence that the mean differs from 500 mL; a larger sample would settle it.

**Example 2 (latency).** Target: average below 200 ms. 40 requests: $\bar{x} = 212$ ms, $s = 35$. Is the mean above 200?
- $t = \frac{12}{35/\sqrt{40}} = \frac{12}{5.534} \approx 2.17$, $df = 39$ → one-sided p-value $\approx 0.018$ → reject; the mean latency exceeds the target.

**Example 3 (battery).** Claimed 10 hours; 20 batteries: $\bar{x} = 9.6$, $s = 0.8$. Less than claimed?
- $t = \frac{-0.4}{0.179} \approx -2.24$, $df = 19$ → p-value $\approx 0.019$ → reject at 5%.

**Example 4 (code).**
```python
import numpy as np
from scipy import stats
x = np.array([497, 501, 495, 499, 502, 496, 498, 500, 494, 500])
t, p = stats.ttest_1samp(x, popmean=500)
print(round(t, 2), round(p, 3))       # -2.14 0.061  (two-sided)
t, p = stats.ttest_1samp(x, 500, alternative="less")   # one-sided version
```
scipy returns the t-statistic and the p-value; use `alternative=` for one-sided tests.

## 5. Common mistakes
- Using the normal table instead of the t-distribution for small samples.
- Choosing a one-sided test **after** seeing the data (decide before collecting).
- Ignoring outliers in small samples.

## 6. Practice
Q: Test statistic for a mean with unknown $\sigma$?
A: $t = \frac{\bar{x} - \mu_0}{s/\sqrt{n}}$.

Q: $n = 25$, $\bar{x} = 52$, $s = 10$, $\mu_0 = 50$. $t$?
A: $1$.

Q: Degrees of freedom for $n = 25$?
A: $24$.

Q: When should a one-sided alternative be chosen?
A: Before seeing the data, when only one direction matters.

Q: Large $\lvert t\rvert$ means?
A: The sample mean is many standard errors from $\mu_0$: strong evidence against $H_0$.

@@ 3 | TOPIC | Type I and Type II errors and power | False positives and false negatives, α and β, power, what increases power | MUST
# Type I and Type II errors and power

## 1. What it covers
- **Type I error:** rejecting a true $H_0$ (false positive), probability $\alpha$.
- **Type II error:** failing to reject a false $H_0$ (false negative), probability $\beta$.
- **Power** $= 1 - \beta$: the chance of detecting a real effect.
- What increases power, and the trade-offs.

## 2. Where it is used
- **A/B testing:** an underpowered test misses real improvements; too many tests produce false wins.
- **Medicine:** false positives (unnecessary treatment) vs false negatives (missed disease).
- **Security and fraud detection:** false alarms vs missed attacks.
- **ML classification:** the same trade-off as precision vs recall.
- **Planning experiments:** power analysis decides sample size.

## 3. What it means
| | $H_0$ true | $H_0$ false |
|---|---|---|
| Reject $H_0$ | **Type I error** ($\alpha$) | Correct (power) |
| Fail to reject | Correct | **Type II error** ($\beta$) |

Like a smoke alarm: a Type I error is the alarm going off with no fire; a Type II error is no alarm during a real fire.

**Trade-off:** lowering $\alpha$ (being stricter) makes Type I errors rarer but Type II errors more common.

**Power increases with:**
- a larger sample size;
- a larger true effect;
- less variability in the data;
- a larger $\alpha$ (at the cost of more false positives).

A common target is 80% power.

## 4. Formula & worked example
$$
\alpha = P(\text{reject } H_0 \mid H_0 \text{ true}) \qquad \beta = P(\text{fail to reject} \mid H_0 \text{ false}) \qquad \text{power} = 1 - \beta
$$

**Example 1 (consequences).** Testing a new drug ($H_0$: no better than placebo).
- Type I: approving a useless drug. Type II: rejecting a drug that works.

**Example 2 (choose α).** A spam filter: $H_0$ = "email is legitimate". A Type I error sends a real email to spam (costly), so use a small $\alpha$.

**Example 3 (power, A/B).** Baseline conversion 5%; you want to detect a lift to 6% with 80% power at $\alpha = 0.05$ (two-sided). Required sample size per group ≈ 8140 (see code). To detect 5% → 5.5% you'd need about four times as many.

**Example 4 (code).**
```python
from statsmodels.stats.power import NormalIndPower
from statsmodels.stats.proportion import proportion_effectsize
es = proportion_effectsize(0.06, 0.05)                 # Cohen's h effect size
n = NormalIndPower().solve_power(effect_size=es, alpha=0.05, power=0.8)
print(round(n))                                        # ≈ 8140 users per group
```
Power analysis turns "how small an effect do I care about?" into a sample size. Small effects on low baseline rates need surprisingly large samples, which is why many A/B tests are underpowered.

## 5. Common mistakes
- Thinking $\alpha$ is the probability that a significant result is a false positive (that depends on how many true effects exist; see the next topic).
- Running tests with too little data and concluding "no effect".
- Lowering $\alpha$ without increasing $n$, which reduces power.

## 6. Practice
Q: Define a Type I error.
A: Rejecting $H_0$ when it is actually true (a false positive).

Q: Define power.
A: The probability of correctly rejecting a false $H_0$.

Q: Name two ways to increase power.
A: Increase the sample size; reduce variability (also: larger effect, larger $\alpha$).

Q: In a fire alarm analogy, what is a Type II error?
A: No alarm during a real fire.

Q: What happens to $\beta$ if you lower $\alpha$ (same $n$)?
A: $\beta$ increases (less power).

@@ 3 | TOPIC | Pitfalls: multiple testing, p-hacking and practical significance | Multiple comparisons, Bonferroni correction, peeking, practical vs statistical significance, effect sizes | MUST
# Pitfalls: multiple testing, p-hacking and practical significance

## 1. What it covers
- The **multiple comparisons** problem and the Bonferroni correction.
- **p-hacking**: trying many analyses until something is "significant".
- **Peeking** at A/B tests and stopping early.
- **Statistical vs practical significance**; reporting effect sizes and confidence intervals.

## 2. Where it is used
- **Tech:** companies that run hundreds of A/B tests must control false discoveries.
- **Science:** the "replication crisis": many published findings don't hold up.
- **Data science:** testing many features or segments ("it works for left-handed users on Tuesdays!").
- **Genomics:** testing thousands of genes at once needs strict corrections.

## 3. What it means
**Multiple testing:** at $\alpha = 0.05$, each test of a true $H_0$ has a 5% false-positive chance. Run 20 such tests and you expect about one "significant" result **by pure chance**. The chance of at least one false positive is $1 - 0.95^{20} \approx 64\%$.

**Bonferroni correction:** with $m$ tests, use $\frac{\alpha}{m}$ for each (simple but conservative). Other methods (e.g. Benjamini–Hochberg) control the false discovery rate.

**p-hacking:** trying different subsets, outcome measures, outlier rules or stopping times until $p < 0.05$, then reporting only that. The p-value is then meaningless.

**Peeking:** checking an A/B test every day and stopping the first time $p < 0.05$ hugely inflates false positives. Fix the sample size in advance (or use sequential methods designed for peeking).

**Practical significance:** with huge samples, tiny, useless effects become "statistically significant". A 0.01% increase in conversion may be real but not worth anything. Always report the **effect size** with a **confidence interval**.

## 4. Formula & worked example
$$
P(\text{at least one false positive in } m \text{ tests}) = 1 - (1 - \alpha)^m \qquad \alpha_{\text{Bonferroni}} = \frac{\alpha}{m}
$$

**Example 1.** Testing 10 page variants against control at $\alpha = 0.05$: chance of at least one false winner $= 1 - 0.95^{10} \approx 40\%$. Bonferroni: use $\alpha = 0.005$ per test.

**Example 2 (p-hacking).** An analyst tests a campaign's effect on 15 customer segments; one shows $p = 0.03$. Reported alone, it looks like a discovery; with Bonferroni ($0.05/15 \approx 0.0033$) it's not significant.

**Example 3 (practical significance).** With 10 million users, the conversion rate rises from 4.00% to 4.03%, $p = 0.001$. Statistically significant, but is a 0.03 percentage-point lift worth the engineering cost? Decide using the effect size and business value.

**Example 4 (peeking simulation).**
```python
import numpy as np
rng = np.random.default_rng(0)
false_wins = 0
for _ in range(2000):                      # 2000 A/A tests: no real difference
    a = rng.binomial(1, 0.05, 20_000); b = rng.binomial(1, 0.05, 20_000)
    for n in range(1000, 20_001, 1000):    # peek after every 1000 users
        pa, pb = a[:n].mean(), b[:n].mean(); p = (pa + pb) / 2
        z = (pb - pa) / np.sqrt(2 * p * (1 - p) / n)
        if abs(z) > 1.96:
            false_wins += 1; break
print(false_wins / 2000)                   # ≈ 0.2+ instead of the promised 0.05
```
Both groups have the same true rate, so every "win" is false. Stopping at the first significant peek produces false positives far more often than 5%.

## 5. Common mistakes
- Reporting only the significant result out of many tried.
- Stopping an experiment as soon as it looks significant.
- Equating "statistically significant" with "important".

## 6. Practice
Q: 20 independent tests of true nulls at $\alpha = 0.05$. Expected false positives?
A: About 1.

Q: Bonferroni-corrected $\alpha$ for 8 tests at overall 0.05?
A: $0.00625$.

Q: What is p-hacking?
A: Trying many analyses and reporting only those with small p-values.

Q: Why is peeking at an A/B test dangerous?
A: Repeated looks give many chances for a false positive.

Q: What should you report besides a p-value?
A: The effect size with a confidence interval.

@@ 2 | MODULE | Comparing groups and relationships | Two-sample proportions and means, paired data, chi-square tests, regression inference, multiple regression, ANOVA | SHOULD
Most real questions compare two or more groups, or ask whether variables are related.

@@ 3 | TOPIC | Comparing two proportions | Two-sample z interval and pooled z-test for p₁ − p₂; A/B test analysis | SHOULD
# Comparing two proportions

## 1. What it covers
- Confidence interval for $p_1 - p_2$.
- **Two-sample z-test** with a **pooled** proportion.
- Analysing a full A/B test.

## 2. Where it is used
- **A/B testing:** conversion rate of version B vs A.
- **Medicine:** recovery rate on treatment vs placebo.
- **Marketing:** response rates of two email campaigns.
- **Social science:** comparing opinions of two groups.

## 3. What it means
Each sample proportion varies; their **difference** varies more (variances add). The SE of the difference is $\sqrt{\frac{\hat{p}_1(1 - \hat{p}_1)}{n_1} + \frac{\hat{p}_2(1 - \hat{p}_2)}{n_2}}$.

For a **test** of $H_0: p_1 = p_2$, assume the null is true: both groups share one proportion, estimated by pooling all the data: $\hat{p} = \frac{x_1 + x_2}{n_1 + n_2}$.

**Conditions:** independent random samples/assignment; at least 10 successes and failures in each group.

## 4. Formula & worked example
$$
\text{CI: } (\hat{p}_1 - \hat{p}_2) \pm z^*\sqrt{\frac{\hat{p}_1(1 - \hat{p}_1)}{n_1} + \frac{\hat{p}_2(1 - \hat{p}_2)}{n_2}} \qquad z = \frac{\hat{p}_1 - \hat{p}_2}{\sqrt{\hat{p}(1 - \hat{p})\left(\frac{1}{n_1} + \frac{1}{n_2}\right)}}
$$

**Example (A/B test).** A: 400 conversions from 8000 visitors (5.0%). B: 480 from 8000 (6.0%).
- $H_0: p_B = p_A$, $H_a: p_B \ne p_A$.
- Pooled $\hat{p} = \frac{880}{16\,000} = 0.055$; $SE = \sqrt{0.055 \times 0.945 \times \frac{2}{8000}} \approx 0.003605$
- $z = \frac{0.01}{0.003605} \approx 2.77$ → p-value $\approx 0.0056$ → reject $H_0$.
- 95% CI for $p_B - p_A$: $SE = \sqrt{\frac{0.05 \times 0.95}{8000} + \frac{0.06 \times 0.94}{8000}} \approx 0.003602$ → $0.01 \pm 0.00706$ → $(0.3\%, 1.7\%)$.
- Conclusion: B's conversion rate is higher, by about 0.3 to 1.7 percentage points.

**Code.**
```python
from statsmodels.stats.proportion import proportions_ztest, confint_proportions_2indep
z, p = proportions_ztest([480, 400], [8000, 8000])
print(round(z, 2), round(p, 4))                                   # 2.77 0.0056
print(confint_proportions_2indep(480, 8000, 400, 8000, method="wald"))
```
`proportions_ztest` with two counts runs the pooled two-sample test; the second function gives the interval for the difference.

## 5. Common mistakes
- Using the pooled SE for the confidence interval (pool only for the test).
- Treating paired data as two independent samples.
- Reporting only "significant" without the size of the difference.

## 6. Practice
Q: Why pool the proportions in the test?
A: Because $H_0$ assumes $p_1 = p_2$, so one common proportion is estimated.

Q: 30/200 vs 45/200. Difference in sample proportions?
A: $0.225 - 0.15 = 0.075$.

Q: Pooled proportion for 30/200 and 45/200?
A: $\frac{75}{400} = 0.1875$.

Q: A 95% CI for $p_1 - p_2$ is $(-0.02, 0.05)$. Significant difference at 5%?
A: No, 0 is inside the interval.

Q: Conditions for a two-proportion test?
A: Independent random groups and at least 10 successes and failures in each.

@@ 3 | TOPIC | Comparing two means and paired data | Two-sample t-test (Welch), paired t-test on differences, choosing between them | SHOULD
# Comparing two means and paired data

## 1. What it covers
- **Two-sample t** interval and test for $\mu_1 - \mu_2$ (Welch's version, not assuming equal variances).
- **Paired** data: analyse the differences with a one-sample t procedure.
- Choosing between independent and paired designs.

## 2. Where it is used
- **A/B testing:** average order value or session time in two groups.
- **Before/after studies:** performance before and after an optimisation on the same machines (paired).
- **Medicine:** blood pressure before and after treatment (paired) or treatment vs control groups (independent).
- **Benchmarking:** comparing two algorithms on the same test inputs (paired).

## 3. What it means
**Independent samples:** two separate groups. The SE of $\bar{x}_1 - \bar{x}_2$ is $\sqrt{\frac{s_1^2}{n_1} + \frac{s_2^2}{n_2}}$ (variances add). Welch's t-test handles unequal spreads and is the safe default.

**Paired data:** each observation in one group is linked to one in the other (same person before/after, same input run on two algorithms). Compute the **differences** and do a one-sample t-test on them. Pairing removes variation between individuals, which often makes the test much more powerful.

## 4. Formula & worked example
$$
t = \frac{\bar{x}_1 - \bar{x}_2}{\sqrt{\dfrac{s_1^2}{n_1} + \dfrac{s_2^2}{n_2}}} \qquad \text{paired: } t = \frac{\bar{d}}{s_d / \sqrt{n}}
$$

**Example 1 (independent).** Average order value: A $n = 120$, $\bar{x} = 1150$, $s = 400$; B $n = 130$, $\bar{x} = 1260$, $s = 450$.
- $SE = \sqrt{\frac{160\,000}{120} + \frac{202\,500}{130}} \approx \sqrt{1333.3 + 1557.7} \approx 53.8$
- $t = \frac{110}{53.8} \approx 2.05$ → p-value ≈ 0.042 (two-sided) → significant at 5%.

**Example 2 (paired).** Run time (ms) of 6 test inputs before and after an optimisation:
| Input | 1 | 2 | 3 | 4 | 5 | 6 |
|---|---|---|---|---|---|---|
| Before | 120 | 85 | 200 | 150 | 95 | 170 |
| After | 112 | 80 | 188 | 141 | 92 | 160 |
| Difference | 8 | 5 | 12 | 9 | 3 | 10 |
- $\bar{d} = 7.83$, $s_d \approx 3.31$, $t = \frac{7.83}{3.31/\sqrt{6}} \approx 5.80$, $df = 5$ → p ≈ 0.002 → a clear speed-up.
- Treated (wrongly) as independent samples, the large differences **between inputs** would hide the effect (p ≈ 0.8).

**Code.**
```python
from scipy import stats
before = [120, 85, 200, 150, 95, 170]
after  = [112, 80, 188, 141, 92, 160]
print(stats.ttest_rel(before, after))                    # paired: t ≈ 5.80, p ≈ 0.002
print(stats.ttest_ind(before, after, equal_var=False))   # wrong here: p ≈ 0.8
```
`ttest_rel` is the paired test (it works on the differences); `ttest_ind(..., equal_var=False)` is Welch's two-sample test for independent groups.

## 5. Common mistakes
- Using a two-sample test on paired data (loses power).
- Pairing data that aren't naturally paired.
- Assuming equal variances without reason (prefer Welch).

## 6. Practice
Q: When are data paired?
A: When each observation in one group is naturally linked to one in the other.

Q: Paired test on differences: $\bar{d} = 4$, $s_d = 6$, $n = 36$. $t$?
A: $4$.

Q: Why can pairing increase power?
A: It removes variation between individuals.

Q: SE for independent means with $s_1 = 10$, $n_1 = 25$, $s_2 = 12$, $n_2 = 36$?
A: $\sqrt{4 + 4} \approx 2.83$.

Q: Which scipy function runs a paired t-test?
A: `stats.ttest_rel`.

@@ 3 | TOPIC | Chi-square tests: goodness of fit, independence and homogeneity | Expected counts, the χ² statistic, degrees of freedom, conditions, interpreting results | SHOULD
# Chi-square tests: goodness of fit, independence and homogeneity

## 1. What it covers
- **Goodness of fit:** does one categorical variable follow a claimed distribution?
- **Independence:** are two categorical variables related (one sample)?
- **Homogeneity:** do several groups have the same distribution?
- Expected counts, the $\chi^2$ statistic, degrees of freedom, conditions.

## 2. Where it is used
- **Product analytics:** is plan choice related to country? Is the device mix the same across marketing channels?
- **Quality:** are defects equally likely on each shift?
- **Genetics:** do observed offspring ratios match Mendel's predictions?
- **Fairness and randomness checks:** is a die or a random number generator fair? Is a model's error rate independent of a demographic group?

## 3. What it means
Compare **observed** counts with the counts **expected** if $H_0$ were true. Big differences → evidence against $H_0$.

$$
\chi^2 = \sum\frac{(\text{observed} - \text{expected})^2}{\text{expected}}
$$
Each cell adds its squared difference, scaled by the expected count. The larger $\chi^2$, the stronger the evidence.

- **Goodness of fit:** expected count $= n \times$ claimed proportion; $df$ = number of categories − 1.
- **Two-way tables (independence/homogeneity):** expected $= \frac{\text{row total} \times \text{column total}}{\text{grand total}}$; $df = (r - 1)(c - 1)$.

**Condition:** all expected counts at least 5.

## 4. Formula & worked example
$$
\chi^2 = \sum\frac{(O - E)^2}{E} \qquad E_{ij} = \frac{(\text{row } i \text{ total})(\text{column } j \text{ total})}{\text{grand total}}
$$

**Example 1 (goodness of fit).** A die is rolled 120 times: counts 25, 17, 15, 23, 24, 16. Fair?
- Expected 20 each. $\chi^2 = \frac{25 + 9 + 25 + 9 + 16 + 16}{20} = 5.0$, $df = 5$ → p ≈ 0.42 → no evidence the die is unfair.

**Example 2 (independence).** Plan choice by device (300 users):
| | Basic | Pro | Total |
|---|---|---|---|
| Mobile | 120 | 30 | 150 |
| Desktop | 90 | 60 | 150 |
| Total | 210 | 90 | 300 |
- Expected: mobile-basic $= \frac{150 \times 210}{300} = 105$; mobile-pro 45; desktop-basic 105; desktop-pro 45.
- $\chi^2 = \frac{15^2}{105} + \frac{15^2}{45} + \frac{15^2}{105} + \frac{15^2}{45} \approx 2.14 + 5 + 2.14 + 5 = 14.29$, $df = 1$ → p ≈ 0.0002 → plan choice is associated with device (desktop users choose Pro more often).

**Code.**
```python
from scipy.stats import chisquare, chi2_contingency
print(chisquare([25, 17, 15, 23, 24, 16]))                 # statistic 5.0, p ≈ 0.416
chi2, p, dof, expected = chi2_contingency([[120, 30], [90, 60]], correction=False)
print(round(chi2, 2), p, dof)                               # 14.29  0.00016  1
```
`chisquare` does goodness of fit (equal expected counts by default); `chi2_contingency` computes expected counts for a two-way table. `correction=False` turns off the small-sample Yates correction to match the hand calculation.

## 5. Common mistakes
- Using percentages instead of counts.
- Expected counts below 5 (combine categories or use an exact test).
- Concluding causation from a significant association.

## 6. Practice
Q: Expected count formula in a two-way table?
A: Row total × column total ÷ grand total.

Q: Degrees of freedom for a 3 × 4 table?
A: $(3 - 1)(4 - 1) = 6$.

Q: Degrees of freedom for goodness of fit with 5 categories?
A: $4$.

Q: Condition for chi-square tests?
A: All expected counts at least 5.

Q: Difference between independence and homogeneity tests?
A: Independence: one sample, two variables. Homogeneity: several samples, one variable. The calculation is the same.

@@ 3 | TOPIC | Inference for regression and multiple regression | t-test and CI for the slope, conditions (LINE), reading regression output, several predictors | SHOULD
# Inference for regression and multiple regression

## 1. What it covers
- Testing whether the true slope $\beta$ is 0; confidence interval for $\beta$.
- Conditions: **L**inear, **I**ndependent, **N**ormal residuals, **E**qual variance (LINE).
- Reading regression output (coefficients, SEs, t, p, $R^2$).
- **Multiple regression**: several predictors; interpreting coefficients "holding others constant".

## 2. Where it is used
- **Economics and business:** which factors drive sales or prices, and by how much.
- **Data science:** linear and logistic regression are the most interpretable models.
- **Science:** quantifying relationships with uncertainty.
- **Real estate:** price models with area, location, age.

## 3. What it means
A fitted slope $b$ from a sample estimates the true slope $\beta$. If the true slope were 0, $x$ would be useless for predicting $y$. The test asks whether $b$ is far enough from 0 relative to its SE: $t = \frac{b}{SE_b}$ with $df = n - 2$.

**Multiple regression:** $\hat{y} = b_0 + b_1x_1 + b_2x_2 + \cdots$. Each $b_j$ is the predicted change in $y$ for a one-unit increase in $x_j$, **holding the other predictors fixed**. A variable can look important on its own but not after controlling for others (or vice versa).

**Warnings:** check residual plots (LINE); highly correlated predictors (**multicollinearity**) make coefficients unstable; adding predictors always raises $R^2$, so use adjusted $R^2$ or validation data.

## 4. Formula & worked example
$$
t = \frac{b - 0}{SE_b},\ df = n - 2 \qquad \text{CI: } b \pm t^*SE_b
$$

**Example 1 (output).** Regressing exam score on study hours ($n = 30$):
| | coef | std err | t | p |
|---|---|---|---|---|
| const | 41.0 | 3.2 | 12.8 | <0.001 |
| hours | 4.8 | 0.65 | 7.38 | <0.001 |
- Strong evidence of a positive slope. 95% CI ($t^* = 2.048$): $4.8 \pm 1.33$ → $(3.47, 6.13)$ points per hour.

**Example 2 (multiple regression).** House price (lakh taka) $= 10 + 0.08 \times \text{area (sq ft)} - 0.5 \times \text{age (years)} + 15 \times \text{near\_metro}$.
- Holding age and metro access fixed, each extra square foot adds 0.08 lakh (8000 taka); each year of age reduces price by 0.5 lakh; being near a metro station adds 15 lakh.

**Example 3 (code).**
```python
import numpy as np, statsmodels.api as sm
rng = np.random.default_rng(1)
hours = rng.uniform(0, 10, 30)
score = 41 + 4.8 * hours + rng.normal(0, 7, 30)
model = sm.OLS(score, sm.add_constant(hours)).fit()
print(model.summary().tables[1])     # coefficients, SEs, t, p, 95% CI
```
`add_constant` adds the intercept column; `summary()` prints the regression table used in reports and papers, including the slope test and confidence interval.

## 5. Common mistakes
- Concluding causation from a significant slope in observational data.
- Interpreting a multiple-regression coefficient without "holding the others constant".
- Skipping residual checks.

## 6. Practice
Q: Null hypothesis for the regression slope test?
A: $H_0: \beta = 0$ (no linear relationship).

Q: $b = 2.5$, $SE_b = 0.5$. $t$?
A: $5$.

Q: Degrees of freedom for simple regression with $n = 40$?
A: $38$.

Q: What does LINE stand for?
A: Linear, Independent, Normal residuals, Equal variance.

Q: Interpret $b_2 = -0.5$ for "age" in a price model.
A: Holding the other predictors fixed, each extra year of age is associated with a 0.5 lower predicted price.

@@ 3 | TOPIC | Analysis of variance (ANOVA) | Comparing several means, between vs within variation, the F-statistic, post-hoc comparisons | NICE
# Analysis of variance (ANOVA)

## 1. What it covers
- Why not run many t-tests (multiple comparisons).
- **One-way ANOVA**: are all group means equal?
- **Between-group** vs **within-group** variation and the **F-statistic**.
- **Post-hoc** tests (Tukey) to find which groups differ.

## 2. Where it is used
- **A/B/n tests:** comparing several page variants at once.
- **Manufacturing:** comparing output from several machines or suppliers.
- **Agriculture:** comparing crop yields under several fertilisers.
- **Education and psychology:** comparing several teaching methods or conditions.

## 3. What it means
With 4 groups there are 6 pairwise t-tests, inflating the false-positive rate. ANOVA asks one question: $H_0$: all means are equal; $H_a$: at least one differs.

The idea: compare **how far apart the group means are** (between-group variation) with **how spread out the data are within each group** (within-group variation).
$$
F = \frac{\text{variation between groups}}{\text{variation within groups}}
$$
If the group means differ a lot relative to the noise inside groups, $F$ is large and the p-value small.

A significant ANOVA doesn't say **which** groups differ; follow up with a post-hoc test like Tukey's HSD, which controls the overall error rate.

**Conditions:** independent random samples; roughly normal groups; similar SDs (largest SD less than about twice the smallest).

## 4. Formula & worked example
$$
F = \frac{MSG}{MSE} = \frac{SSG/(k - 1)}{SSE/(N - k)}
$$

**Example 1 (idea).** Three machines' mean output: 50, 52, 51 units with within-group SDs of about 10 → $F$ small → differences look like noise. Same means with SDs about 0.5 → $F$ large → real differences.

**Example 2 (by hand, small).** Groups: A = 4, 5, 6; B = 6, 7, 8; C = 9, 10, 11. Grand mean 7.33.
- $SSG = 3[(5 - 7.33)^2 + (7 - 7.33)^2 + (10 - 7.33)^2] = 3[5.44 + 0.11 + 7.11] = 38$
- $SSE$ = within-group squared deviations $= 2 + 2 + 2 = 6$
- $F = \frac{38/2}{6/6} = 19$ → p ≈ 0.0025 → the means are not all equal.

**Example 3 (code).**
```python
from scipy.stats import f_oneway
from statsmodels.stats.multicomp import pairwise_tukeyhsd
A, B, C = [4, 5, 6], [6, 7, 8], [9, 10, 11]
print(f_oneway(A, B, C))                                   # F = 19.0, p ≈ 0.0025
print(pairwise_tukeyhsd(A + B + C, ["A"]*3 + ["B"]*3 + ["C"]*3))
```
`f_oneway` runs the ANOVA; `pairwise_tukeyhsd` then shows which pairs differ (here C differs from A and B; A vs B is not significant with so little data).

## 5. Common mistakes
- Running many t-tests instead of ANOVA.
- Concluding all means differ after a significant ANOVA (only at least one does).
- Ignoring very unequal spreads.

## 6. Practice
Q: Null hypothesis of one-way ANOVA?
A: All group means are equal.

Q: What does a large F-statistic indicate?
A: Between-group differences are large relative to within-group variation.

Q: Why not just run all pairwise t-tests?
A: It inflates the overall Type I error rate.

Q: After a significant ANOVA, how do you find which groups differ?
A: Use a post-hoc test such as Tukey's HSD.

Q: Degrees of freedom for $k = 4$ groups and $N = 40$ observations?
A: $3$ and $36$.

@@ 3 | TOPIC | Statistical tests on real data in Python | A complete workflow: load, explore, check conditions, test, report effect sizes; choosing the right test | SHOULD
# Statistical tests on real data in Python

## 1. What it covers
- A complete, reproducible analysis workflow.
- A guide for **choosing the right test**.
- Reporting results properly (effect size, CI, p-value, conditions).

## 2. Where it is used
- **Data analyst and data scientist work:** every experiment report.
- **Engineering:** deciding whether a performance change is real.
- **Product teams:** A/B test readouts.

## 3. What it means
**Workflow:**
1. **Question:** write it in words, then as $H_0$/$H_a$, before looking at results.
2. **Load and explore:** summary statistics, plots, missing values, outliers.
3. **Check conditions** (randomness, independence, sample size, shape).
4. **Run the test** and compute a confidence interval.
5. **Report:** effect size + CI + p-value + caveats, in plain language.

**Choosing a test:**
| Question | Data | Test |
|---|---|---|
| One proportion vs a value | counts | one-sample z (proportion) |
| Two proportions | counts in 2 groups | two-sample z / chi-square |
| One mean vs a value | numbers | one-sample t |
| Two independent means | numbers in 2 groups | Welch t-test |
| Before/after on same units | paired numbers | paired t-test |
| 3+ means | numbers in 3+ groups | ANOVA (+ Tukey) |
| Two categorical variables | two-way table | chi-square independence |
| Relationship between numbers | x, y pairs | regression slope test |
| Small/skewed samples | numbers | nonparametric (Mann–Whitney, Wilcoxon) |

## 4. Formula & worked example
**Example (full workflow): does a new onboarding flow increase 7-day retention?**
```python
import numpy as np, pandas as pd
from statsmodels.stats.proportion import proportions_ztest, confint_proportions_2indep

rng = np.random.default_rng(42)
df = pd.DataFrame({
    "group": np.repeat(["old", "new"], 3000),
    "retained": np.concatenate([rng.binomial(1, 0.30, 3000), rng.binomial(1, 0.33, 3000)]),
})
summary = df.groupby("group")["retained"].agg(["sum", "count", "mean"])
print(summary)                                            # explore

x = summary.loc[["new", "old"], "sum"].values
n = summary.loc[["new", "old"], "count"].values
z, p = proportions_ztest(x, n)                            # test
lo, hi = confint_proportions_2indep(x[0], n[0], x[1], n[1])   # effect size CI
print(f"lift = {summary.loc['new','mean'] - summary.loc['old','mean']:.3f}, "
      f"95% CI ({lo:.3f}, {hi:.3f}), p = {p:.4f}")
```
The script simulates a randomised experiment, summarises each group, runs the two-proportion test and reports the lift with its confidence interval. A good write-up: "The new flow increased 7-day retention by about 3 percentage points (95% CI roughly 0.7 to 5.4 points, p ≈ 0.01) in a randomised test of 6000 users." (Exact numbers depend on the random seed.)

**Nonparametric alternative.**
```python
from scipy.stats import mannwhitneyu
print(mannwhitneyu([12, 15, 11, 30, 14], [18, 22, 19, 25, 40]))
```
The Mann–Whitney test compares two groups using ranks, so it doesn't need normality, which is useful for small, skewed samples like revenue per user.

## 5. Common mistakes
- Choosing the test after seeing which gives the smallest p-value.
- Reporting only p-values.
- Ignoring how the data were collected (no randomisation → no causal claim).

## 6. Practice
Q: Test for comparing mean spend between two independent groups?
A: Welch's two-sample t-test.

Q: Test for whether churn is related to subscription plan (categorical)?
A: Chi-square test of independence.

Q: Test for comparing the same servers' latency before and after a patch?
A: Paired t-test.

Q: Five things a good test report includes?
A: The question, effect size, confidence interval, p-value, and conditions/caveats.

Q: When might you use a Mann–Whitney test?
A: For small or strongly skewed samples where t-test conditions fail.

@@ 3 | TOPIC | Practice & review: Inference | Mixed problem set with worked answers | MUST
# Practice & review: Inference

**How to use this:** 60 minutes; calculator or Python allowed. State hypotheses and check conditions every time.

## Problems
1. 312 of 600 surveyed users want a dark mode. Build a 95% CI for the proportion.
2. How many users are needed to estimate a proportion within ±2% at 95% (no prior estimate)?
3. 16 cereal boxes: $\bar{x} = 495$ g, $s = 8$ g. Test whether the mean is less than the labelled 500 g ($t^*$ one-sided 5%, $df = 15$: 1.753).
4. A/B test: A 210/3000 converted, B 255/3000. Test at $\alpha = 0.05$ (two-sided).
5. A die rolled 60 times gives 4, 10, 8, 12, 9, 17. Chi-square statistic and conclusion ($\chi^2_{0.05,5} = 11.07$).
6. A team runs 12 A/B tests at $\alpha = 0.05$ with no real effects. Probability of at least one false win?
7. Why is a paired design better for comparing two algorithms on the same 20 datasets?
8. A regression of sales on ad spend gives slope 3.2 with SE 1.1 ($n = 25$). Is the slope significant? 95% CI ($t^* = 2.069$)?

## Answers
1. $\hat{p} = 0.52$; $SE \approx 0.0204$; $0.52 \pm 0.040$ → $(0.48, 0.56)$.
2. $\left(\frac{1.96}{0.02}\right)^2(0.25) = 2401$.
3. $t = \frac{-5}{8/4} = -2.5 < -1.753$ → reject $H_0$; evidence the mean is below 500 g.
4. $\hat{p}_A = 0.07$, $\hat{p}_B = 0.085$; pooled $0.0775$; $SE = \sqrt{0.0775 \times 0.9225 \times \frac{2}{3000}} \approx 0.0069$; $z \approx 2.17$ → p ≈ 0.03 → reject; B converts better.
5. Expected 10 each: $\frac{36 + 0 + 4 + 4 + 1 + 49}{10} = 9.4 < 11.07$ → fail to reject; no convincing evidence of unfairness.
6. $1 - 0.95^{12} \approx 0.46$.
7. Pairing removes dataset-to-dataset variation, so differences between algorithms are measured more precisely.
8. $t = \frac{3.2}{1.1} \approx 2.91$, $df = 23$ → p ≈ 0.008 → significant; CI $3.2 \pm 2.28$ → $(0.92, 5.48)$.

## Flashcards
Q: General form of a confidence interval?
A: Estimate ± critical value × standard error.

Q: p-value meaning?
A: The probability of data at least as extreme as observed if $H_0$ is true.

Q: Type I vs Type II error?
A: Type I: false positive (reject a true $H_0$). Type II: false negative (miss a real effect).

Q: Chi-square statistic?
A: $\sum\frac{(O - E)^2}{E}$.

@@ 2 | MODULE | Final review | Cumulative mixed set covering the whole roadmap | MUST
Mix everything. After this, continue with Linear Algebra (the language of machine learning) or apply these methods to your own data.

@@ 3 | TOPIC | Final review: Statistics and probability | Cumulative mixed problems from all phases with answers | MUST
# Final review: Statistics and probability

**How to use this:** 75 minutes; calculator or Python allowed. Re-study the topic for every miss.

## Problems
1. Salaries (thousand taka): 28, 30, 31, 33, 35, 36, 38, 120. Which centre and spread measures should you report, and why? Compute them.
2. A test has mean 60 and SD 12; scores are roughly normal. What percent score above 84? What score marks the top 5%?
3. For data with $\bar{x} = 10$, $s_x = 2$, $\bar{y} = 50$, $s_y = 8$, $r = -0.6$: find the regression line and $r^2$.
4. A researcher compares sleep in students who drink coffee and those who don't, using a survey. Can she conclude coffee causes poor sleep? What design would allow that?
5. 5% of a population has a condition. A test has 90% sensitivity and a 10% false positive rate. $P(\text{condition} \mid \text{positive})$?
6. A password has 4 lowercase letters followed by 2 digits. How many passwords are possible?
7. A call centre averages 5 calls per 10 minutes. $P(\text{no calls in 10 minutes})$? Expected wait between calls?
8. Average basket value: $\mu = 900$, $\sigma = 600$ (skewed). For 144 random baskets, $P(\bar{x} > 1000)$?
9. 230 of 500 surveyed customers are satisfied. 95% CI for the satisfaction rate. Is "a majority are satisfied" supported?
10. An A/B test: A 300/5000, B 360/5000. Test for a difference at 5% and give a 95% CI for the lift.

## Answers
1. Right-skewed with an outlier (120) → median and IQR. Median $= \frac{33 + 35}{2} = 34$; $Q_1 = 30.5$, $Q_3 = 37$, IQR $= 6.5$ (mean ≈ 43.9 is misleading).
2. 84 is $z = 2$ → about 2.5%; top 5%: $60 + 1.645 \times 12 \approx 79.7$.
3. $b = -0.6 \times \frac{8}{2} = -2.4$; $a = 50 + 24 = 74$ → $\hat{y} = 74 - 2.4x$; $r^2 = 0.36$.
4. No: observational study with possible confounders (stress, workload). A randomised experiment assigning coffee/no coffee would allow causal conclusions.
5. $\frac{0.9 \times 0.05}{0.9 \times 0.05 + 0.1 \times 0.95} = \frac{0.045}{0.14} \approx 0.32$.
6. $26^4 \times 10^2 = 45\,697\,600$.
7. $e^{-5} \approx 0.0067$; expected wait 2 minutes.
8. SE $= \frac{600}{12} = 50$; $z = 2$ → $\approx 0.023$.
9. $\hat{p} = 0.46$, $SE \approx 0.0223$, CI $0.46 \pm 0.044$ → $(0.416, 0.504)$; the claim isn't supported (the interval is mostly below 0.5).
10. Pooled $\hat{p} = 0.066$; $SE = \sqrt{0.066 \times 0.934 \times \frac{2}{5000}} \approx 0.00497$; $z = \frac{0.012}{0.00497} \approx 2.42$ → p ≈ 0.016 → significant. CI: $SE \approx 0.00496$ → $0.012 \pm 0.0097$ → $(0.2\%, 2.2\%)$.
