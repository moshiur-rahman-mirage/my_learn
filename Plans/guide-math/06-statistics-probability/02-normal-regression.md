@@ 1 | MODULE | Phase 2 — Normal Distribution and Bivariate Data (Core) | Density curves, the normal model, scatterplots, correlation, least-squares regression, residuals, influential points, transformations | MUST
# Phase 2: Normal distribution and bivariate data

The **normal distribution** (the bell curve) describes heights, measurement errors, test scores and, thanks to the central limit theorem, averages of almost anything. **Bivariate data** asks how two quantitative variables relate: does study time predict marks, does price predict sales? The tools here, correlation and regression, are the foundation of machine learning.

@@ 2 | MODULE | The normal distribution | Density curves, empirical rule, normal probability calculations | MUST
Many natural measurements pile up in a bell shape around the mean.

@@ 3 | TOPIC | Density curves | Smooth models of distributions, area as proportion, mean and median of a density curve | MUST
# Density curves

## 1. What it covers
- A **density curve** as an idealised smooth histogram.
- **Area under the curve = proportion** of values.
- Total area = 1; probabilities are areas, not heights.
- Mean (balance point) vs median (equal-areas point) of a density curve; the uniform distribution.

## 2. Where it is used
- **Probability models** for continuous variables (heights, times, errors).
- **Machine learning:** probability density functions (PDFs) in generative models and anomaly detection.
- **Simulation:** generating random numbers from a chosen distribution.
- **Quality control:** what fraction of products fall within tolerance.

## 3. What it means
Draw a histogram of a huge data set with very thin bins and scale it so the bars' total area is 1: it becomes a smooth curve. That's a **density curve**.

- The **area** between two values is the **proportion** of observations in that range.
- The height is **not** a probability (it can exceed 1); only areas are.
- The **median** splits the area in half; the **mean** is the balance point. For right-skewed curves the mean is to the right of the median.

**Uniform distribution:** a flat rectangle, e.g. a random number generator `random()` producing values equally likely anywhere in $[0, 1]$.

## 4. Formula & worked example
$$
P(a \le X \le b) = \text{area under the curve from } a \text{ to } b \qquad \text{total area} = 1
$$

**Example 1 (uniform).** A bus arrives uniformly at random within a 20-minute window. Probability you wait between 5 and 12 minutes?
- Height $\frac{1}{20}$; area $= 7 \times \frac{1}{20} = 0.35$

**Example 2 (triangle).** A density is a triangle from 0 to 2 with its peak at 0. Height at 0 must be 1 (area $\frac{1}{2} \cdot 2 \cdot h = 1$). $P(X < 1)$? The remaining triangle from 1 to 2 has area $\frac{1}{2} \cdot 1 \cdot 0.5 = 0.25$ → $P(X < 1) = 0.75$.

**Example 3.** Is $f(x) = 2$ on $[0, 1]$ a valid density? Area $= 2 \ne 1$ → no.

**Example 4 (code).**
```python
import numpy as np
x = np.random.uniform(0, 20, 1_000_000)
print(((x > 5) & (x < 12)).mean())   # ≈ 0.35
```
Simulating a million uniform waiting times and counting the fraction between 5 and 12 confirms the area calculation.

## 5. Common mistakes
- Reading the curve's height as a probability.
- Forgetting the total area must be 1.
- Thinking $P(X = a) > 0$ for a continuous variable (a single point has zero area).

## 6. Practice
Q: Total area under any density curve?
A: $1$.

Q: Uniform on $[0, 10]$. $P(2 \le X \le 5)$?
A: $0.3$.

Q: For a continuous variable, $P(X = 3)$?
A: $0$.

Q: In a right-skewed density curve, which is larger: mean or median?
A: Mean.

Q: What does the area between two values represent?
A: The proportion of observations in that range.

@@ 3 | TOPIC | The normal distribution and the empirical rule | Bell curve, parameters μ and σ, 68–95–99.7 rule, standard normal | MUST
# The normal distribution and the empirical rule

## 1. What it covers
- The **normal distribution** $N(\mu, \sigma)$: symmetric, bell-shaped.
- The **empirical (68–95–99.7) rule**.
- The **standard normal** $Z \sim N(0, 1)$.
- Checking whether data are approximately normal.

## 2. Where it is used
- **Natural measurements:** heights, blood pressure, measurement errors.
- **Quality control:** Six Sigma: defects beyond $\pm 6\sigma$ are extremely rare.
- **Finance:** (approximate) daily returns; risk models (with caution: real returns have fatter tails).
- **Inference:** sample means are approximately normal (central limit theorem), which is why normal tables appear everywhere.
- **ML:** weight initialisation, Gaussian noise, Gaussian Naive Bayes.

## 3. What it means
The normal curve is a symmetric bell centred at the mean $\mu$; the SD $\sigma$ controls its width. Mean = median = mode.

**Empirical rule:**
- About **68%** of values lie within $1\sigma$ of the mean.
- About **95%** within $2\sigma$.
- About **99.7%** within $3\sigma$.

So a value more than 3 SDs from the mean is very rare (3 in 1000).

**Checking normality:** a roughly symmetric, single-peaked histogram; or a **normal probability (Q–Q) plot** where points fall near a straight line.

## 4. Formula & worked example
$$
f(x) = \frac{1}{\sigma\sqrt{2\pi}}\,e^{-\frac{(x - \mu)^2}{2\sigma^2}} \qquad Z = \frac{X - \mu}{\sigma} \sim N(0, 1)
$$

**Example 1.** Adult heights $\sim N(165, 7)$ cm.
- 68% between 158 and 172; 95% between 151 and 179; 99.7% between 144 and 186.

**Example 2.** What percent of heights are above 179 cm (= $\mu + 2\sigma$)?
- 95% within → 5% outside → split equally: **2.5%** above.

**Example 3.** What percent are between 158 and 179 ($-1\sigma$ to $+2\sigma$)?
- $34\% + 47.5\% = 81.5\%$

**Example 4 (manufacturing).** Bolt lengths $\sim N(50, 0.1)$ mm; tolerance 49.8–50.2 mm ($\pm 2\sigma$) → about 5% are rejected.

**Example 5 (code).**
```python
from scipy.stats import norm
print(norm.cdf(1) - norm.cdf(-1))   # 0.6827
print(norm.cdf(2) - norm.cdf(-2))   # 0.9545
print(norm.cdf(3) - norm.cdf(-3))   # 0.9973
```
`norm.cdf(z)` is the area to the left of $z$ under the standard normal curve, so subtracting gives the area between, confirming the 68–95–99.7 rule.

## 5. Common mistakes
- Applying the empirical rule to skewed data.
- Forgetting to split the outside area between both tails.
- Assuming every data set is normal.

## 6. Practice
Q: Percent of a normal distribution within 2 SDs of the mean?
A: About 95%.

Q: $N(100, 15)$: percent above 130?
A: About 2.5%.

Q: $N(100, 15)$: percent between 85 and 115?
A: About 68%.

Q: Mean, median and mode of a normal distribution?
A: All equal.

Q: What plot checks for normality?
A: A normal probability (Q–Q) plot.

@@ 3 | TOPIC | Normal probability calculations | Using z-tables and software for areas, finding cut-off values (inverse normal) | MUST
# Normal probability calculations

## 1. What it covers
- Finding proportions/probabilities: $P(X < a)$, $P(X > a)$, $P(a < X < b)$.
- Standardising to $z$ and using a **z-table** or software.
- **Inverse** problems: finding the value for a given percentile.

## 2. Where it is used
- **Admissions:** the score needed for the top 10%.
- **Manufacturing:** defect rates and setting tolerances.
- **Service levels:** the time within which 95% of deliveries arrive.
- **Inference:** p-values and critical values (Phase 6).

## 3. What it means
**Steps for "what proportion?":**
1. Draw the bell curve and shade the region.
2. Standardise: $z = \frac{x - \mu}{\sigma}$.
3. Look up the area to the **left** of $z$ (table or `norm.cdf`).
4. For "greater than", use $1 -$ left area; for "between", subtract two left areas.

**Steps for "what value?" (inverse):**
1. Find the $z$ with the required area to its left (`norm.ppf`).
2. Convert back: $x = \mu + z\sigma$.

Key $z$-values: $z = 1.645$ (95th percentile), $1.96$ (97.5th), $2.326$ (99th).

## 4. Formula & worked example
$$
P(X < a) = \Phi\!\left(\frac{a - \mu}{\sigma}\right) \qquad x_p = \mu + z_p\sigma
$$
($\Phi$ = standard normal CDF.)

**Example 1.** Exam scores $\sim N(70, 8)$. Proportion scoring below 82?
- $z = 1.5$ → $\Phi(1.5) \approx 0.9332$ → 93.3%

**Example 2.** Proportion above 60? $z = -1.25$ → $1 - 0.1056 = 0.8944$ → 89.4%

**Example 3.** Between 66 and 78? $z = -0.5$ and $1$ → $0.8413 - 0.3085 = 0.5328$ → 53.3%

**Example 4 (inverse).** Score needed for the top 10%? → 90th percentile, $z = 1.2816$ → $70 + 1.2816 \times 8 \approx 80.3$

**Example 5 (service level).** Delivery times $\sim N(35, 6)$ min. Promise a time that 95% of deliveries meet?
- $35 + 1.645 \times 6 \approx 44.9$ → promise 45 minutes.

**Example 6 (code).**
```python
from scipy.stats import norm
print(norm.cdf(82, loc=70, scale=8))   # 0.9332  P(X < 82)
print(norm.sf(60, loc=70, scale=8))    # 0.8944  P(X > 60)  (sf = 1 - cdf)
print(norm.ppf(0.90, loc=70, scale=8)) # 80.25   90th percentile
```
`cdf` gives a left-tail area, `sf` a right-tail area, and `ppf` (percent point function) goes backwards from an area to a value.

## 5. Common mistakes
- Using the table's left area when the question asks for "greater than".
- Using $\sigma^2$ instead of $\sigma$ in the z formula.
- Mixing up `cdf` (value → area) and `ppf` (area → value).

## 6. Practice
Q: $N(50, 10)$: $P(X < 65)$?
A: $\Phi(1.5) \approx 0.933$.

Q: $N(50, 10)$: $P(X > 40)$?
A: $1 - \Phi(-1) \approx 0.841$.

Q: $z$ for the 95th percentile?
A: $1.645$.

Q: $N(200, 25)$: value at the 2.5th percentile?
A: $200 - 1.96 \times 25 = 151$.

Q: Which scipy function finds a value from a percentile?
A: `norm.ppf`.

@@ 2 | MODULE | Bivariate data: correlation and regression | Scatterplots, correlation, least-squares lines, residuals, influential points, transformations | MUST
How does one quantitative variable relate to another?

@@ 3 | TOPIC | Scatterplots and association | Explanatory and response variables, direction, form, strength, outliers; correlation is not causation | MUST
# Scatterplots and association

## 1. What it covers
- **Explanatory** (x) and **response** (y) variables.
- Describing a scatterplot: **direction, form, strength, unusual points**.
- Positive, negative and no association; linear vs nonlinear.
- **Correlation is not causation**; lurking variables.

## 2. Where it is used
- **Exploratory data analysis:** scatterplot matrices (`pd.plotting.scatter_matrix`, seaborn `pairplot`) to spot relationships before modelling.
- **Business:** ad spend vs sales; price vs demand.
- **Science:** dose vs response.
- **ML feature selection:** finding which inputs relate to the target.

## 3. What it means
Plot each individual as a point $(x, y)$.
- **Direction:** positive (up to the right), negative (down), or none.
- **Form:** linear (a straight band), curved, clusters.
- **Strength:** how tightly the points follow the form.
- **Unusual points:** outliers far from the pattern.

**Correlation ≠ causation:** ice-cream sales and drownings rise together, but both are driven by hot weather (a **lurking variable**). Only a randomised experiment can establish cause and effect.

## 4. Formula & worked example
**Example 1.** Study hours vs exam score for 30 students: points rise from lower-left to upper-right in a fairly tight straight band → "a strong, positive, linear association; one student who studied 2 hours but scored 90 is an outlier."

**Example 2.** Car speed vs fuel efficiency: efficiency rises up to about 60–80 km/h then falls → a curved (nonlinear) association.

**Example 3 (lurking variable).** Cities with more hospitals have more deaths. Why? Population size drives both.

**Example 4 (code).**
```python
import numpy as np, matplotlib.pyplot as plt
hours = np.random.uniform(0, 10, 50)
score = 40 + 5 * hours + np.random.normal(0, 6, 50)
plt.scatter(hours, score); plt.xlabel("study hours"); plt.ylabel("score"); plt.show()
```
The simulated data have a true linear relationship (40 + 5 × hours) plus random noise, which appears as a positive, linear, moderately strong scatter.

## 5. Common mistakes
- Concluding causation from an observational scatterplot.
- Calling a strong curved relationship "weak" because it isn't linear.
- Swapping explanatory and response variables.

## 6. Practice
Q: Four features to describe in a scatterplot?
A: Direction, form, strength, unusual points.

Q: Explanatory variable in "fertiliser amount vs crop yield"?
A: Fertiliser amount.

Q: What is a lurking variable?
A: A variable not studied that affects both variables, creating an apparent association.

Q: Can a scatterplot alone prove causation?
A: No.

Q: Describe points falling from upper left to lower right in a tight curve.
A: Strong, negative, nonlinear association.

@@ 3 | TOPIC | The correlation coefficient r | Computing and interpreting r, properties, what r does not measure | MUST
# The correlation coefficient r

## 1. What it covers
- The **correlation coefficient** $r$: strength and direction of a **linear** relationship.
- Its range $-1 \le r \le 1$ and properties.
- How to compute it, and what it doesn't capture.

## 2. Where it is used
- **Data science:** correlation matrices (`df.corr()`) and heatmaps to find related features (and redundant ones).
- **Finance:** correlation between assets for diversification.
- **Science:** quantifying relationships.
- **Recommendation systems:** Pearson correlation between users' ratings.

## 3. What it means
$r$ measures how close the points are to a **straight line**:
- $r = 1$: perfect positive line; $r = -1$: perfect negative line; $r = 0$: no **linear** relationship.
- Rough guide: $\lvert r\rvert > 0.8$ strong, $0.5$–$0.8$ moderate, $< 0.5$ weak.

**How it works:** standardise both variables to z-scores and average their products. Points in the upper-right and lower-left quadrants (both z's same sign) push $r$ up; the other quadrants push it down.

**Properties:** no units; unchanged by changing units; same if you swap $x$ and $y$; very sensitive to outliers.

**$r = 0$ doesn't mean no relationship:** $y = x^2$ on symmetric data has $r = 0$ despite a perfect curve.

## 4. Formula & worked example
$$
r = \frac{1}{n - 1}\sum\left(\frac{x_i - \bar{x}}{s_x}\right)\left(\frac{y_i - \bar{y}}{s_y}\right)
$$

**Example 1 (small data).** $x$: 1, 2, 3, 4, 5; $y$: 2, 4, 5, 4, 5.
- $\bar{x} = 3$, $\bar{y} = 4$; deviations $x$: $-2, -1, 0, 1, 2$; $y$: $-2, 0, 1, 0, 1$
- $\sum(x - \bar{x})(y - \bar{y}) = 4 + 0 + 0 + 0 + 2 = 6$; $\sum(x - \bar{x})^2 = 10$; $\sum(y - \bar{y})^2 = 6$
- $r = \frac{6}{\sqrt{10 \cdot 6}} \approx 0.775$ (moderately strong positive)

**Example 2 (curve).** $x$: $-2, -1, 0, 1, 2$; $y = x^2$: 4, 1, 0, 1, 4 → $r = 0$, yet $y$ is completely determined by $x$.

**Example 3 (outlier).** Adding one far-off point can turn $r = 0.1$ into $r = 0.8$; always look at the scatterplot.

**Example 4 (code).**
```python
import numpy as np, pandas as pd
x = [1, 2, 3, 4, 5]; y = [2, 4, 5, 4, 5]
print(np.corrcoef(x, y)[0, 1])            # 0.7746
df = pd.DataFrame({"x": x, "y": y, "z": [5, 3, 4, 2, 1]})
print(df.corr().round(2))                  # correlation matrix of all columns
```
`np.corrcoef` returns a 2×2 matrix; the off-diagonal entry is $r$. `df.corr()` computes $r$ for every pair of columns, the usual first step in feature analysis.

## 5. Common mistakes
- Using $r$ for curved relationships.
- Thinking $r = 0.6$ means "60% related" ($r^2$ has a percentage meaning; $r$ doesn't).
- Concluding causation from a high $r$.

## 6. Practice
Q: Range of $r$?
A: $-1$ to $1$.

Q: Does changing units from cm to m change $r$?
A: No.

Q: $r = -0.9$ means?
A: A strong negative linear relationship.

Q: Can $r = 0$ when there's a strong relationship?
A: Yes, if the relationship is nonlinear.

Q: Is $r$ resistant to outliers?
A: No, it's very sensitive.

@@ 3 | TOPIC | Least-squares regression line | ŷ = a + bx, slope b = r·s_y/s_x, prediction, interpreting slope and intercept, extrapolation | MUST
# Least-squares regression line

## 1. What it covers
- The **least-squares regression line** $\hat{y} = a + bx$.
- Formulas: $b = r\frac{s_y}{s_x}$, $a = \bar{y} - b\bar{x}$.
- Predicting with the line; **interpreting** slope and intercept in context.
- The danger of **extrapolation**.

## 2. Where it is used
- **Machine learning:** linear regression is the most basic supervised model (`sklearn.linear_model.LinearRegression`).
- **Business forecasting:** sales vs advertising; cost vs volume.
- **Science:** calibration curves (instrument reading → true concentration).
- **Economics:** estimating trends and elasticities.

## 3. What it means
Many lines could pass through a scatterplot. The **least-squares** line is the one that makes the sum of **squared vertical distances** (residuals) from the points to the line as small as possible.

- **Slope $b$:** for each 1-unit increase in $x$, the **predicted** $y$ changes by $b$ (on average).
- **Intercept $a$:** the predicted $y$ when $x = 0$ (only meaningful if $x = 0$ makes sense and is near the data).
- The line always passes through $(\bar{x}, \bar{y})$.

**Extrapolation:** predicting far outside the range of the data is risky, because the relationship may change.

## 4. Formula & worked example
$$
\hat{y} = a + bx \qquad b = r\,\frac{s_y}{s_x} = \frac{\sum(x - \bar{x})(y - \bar{y})}{\sum(x - \bar{x})^2} \qquad a = \bar{y} - b\bar{x}
$$

**Example 1 (from the previous topic's data).** $\sum(x - \bar{x})(y - \bar{y}) = 6$, $\sum(x - \bar{x})^2 = 10$ → $b = 0.6$; $a = 4 - 0.6(3) = 2.2$ → $\hat{y} = 2.2 + 0.6x$.

**Example 2 (from summaries).** Study hours: $\bar{x} = 5$, $s_x = 2$; scores: $\bar{y} = 65$, $s_y = 12$; $r = 0.8$.
- $b = 0.8 \times \frac{12}{2} = 4.8$; $a = 65 - 4.8(5) = 41$ → $\hat{y} = 41 + 4.8x$
- Interpretation: each extra hour of study is associated with 4.8 more points, on average. Predicted score for 7 hours: $41 + 33.6 = 74.6$.

**Example 3 (extrapolation).** Predicting the score for 30 hours gives 185 → impossible (max 100): the line doesn't hold outside the data range (0–10 hours).

**Example 4 (code).**
```python
import numpy as np
from sklearn.linear_model import LinearRegression
x = np.array([1, 2, 3, 4, 5]).reshape(-1, 1)   # sklearn wants a 2D array of features
y = np.array([2, 4, 5, 4, 5])
model = LinearRegression().fit(x, y)
print(model.intercept_, model.coef_[0])        # 2.2 0.6
print(model.predict([[6]]))                    # [5.8]
```
`fit` computes the least-squares slope and intercept exactly as the formulas do. `reshape(-1, 1)` turns the list into one column, because sklearn models expect a table of features (one row per individual).

## 5. Common mistakes
- Interpreting the slope as a guaranteed change ("will increase") instead of a predicted average change.
- Interpreting the intercept when $x = 0$ is meaningless.
- Swapping $x$ and $y$: the regression of $y$ on $x$ is different from $x$ on $y$.

## 6. Practice
Q: Formula for the slope from $r$?
A: $b = r\frac{s_y}{s_x}$.

Q: Which point does the regression line always pass through?
A: $(\bar{x}, \bar{y})$.

Q: $\hat{y} = 12 + 3.5x$. Predict $y$ at $x = 4$.
A: $26$.

Q: Interpret slope $-2.1$ in "price (taka) vs units sold".
A: Each 1 taka increase in price is associated with 2.1 fewer units sold, on average.

Q: Why is extrapolation risky?
A: The relationship may not hold outside the observed range.

@@ 3 | TOPIC | Residuals, residual plots and r² | Residual = y − ŷ, residual plots for checking linearity, coefficient of determination, standard error | MUST
# Residuals, residual plots and r²

## 1. What it covers
- **Residuals** $e = y - \hat{y}$.
- **Residual plots** for checking whether a linear model fits.
- The **coefficient of determination** $r^2$.
- The standard deviation of the residuals $s$ (typical prediction error).

## 2. Where it is used
- **ML model evaluation:** $R^2$ score and RMSE (`sklearn.metrics.r2_score`, `mean_squared_error`).
- **Diagnosing models:** residual patterns reveal missing curvature, missing variables or changing spread.
- **Forecasting:** reporting typical error ("predictions are usually within ± 5 units").
- **Science:** checking calibration curves.

## 3. What it means
**Residual** = actual − predicted: how far the point is above (positive) or below (negative) the line.

**Residual plot:** residuals vs $x$ (or vs $\hat{y}$).
- Random scatter around 0 with no pattern → the linear model is appropriate.
- A **curve** → the relationship is nonlinear.
- A **fan shape** (spread growing) → the variability changes with $x$; predictions are less reliable for large $x$.

**$r^2$:** the fraction of the variation in $y$ that is **explained** by the linear relationship with $x$. $r^2 = 0.64$ means 64% of the variation in scores is explained by study hours; the other 36% is due to other factors.

**$s$ (residual SD):** the typical size of a prediction error, in $y$'s units.

## 4. Formula & worked example
$$
e_i = y_i - \hat{y}_i \qquad r^2 = 1 - \frac{\sum e_i^2}{\sum(y_i - \bar{y})^2} \qquad s = \sqrt{\frac{\sum e_i^2}{n - 2}}
$$

**Example 1.** $\hat{y} = 2.2 + 0.6x$ and data $(1,2), (2,4), (3,5), (4,4), (5,5)$.
- Predictions: 2.8, 3.4, 4.0, 4.6, 5.2; residuals: $-0.8, 0.6, 1.0, -0.6, -0.2$ (sum 0 ✓)
- $\sum e^2 = 0.64 + 0.36 + 1 + 0.36 + 0.04 = 2.4$; $\sum(y - \bar{y})^2 = 6$
- $r^2 = 1 - \frac{2.4}{6} = 0.6$ (and $r = 0.775$, $r^2 = 0.6$ ✓); $s = \sqrt{2.4/3} \approx 0.89$

**Example 2 (interpretation).** A model of house price on area has $r^2 = 0.72$ and $s = 8$ lakh: "72% of the variation in price is explained by area; predictions are typically off by about 8 lakh taka."

**Example 3 (residual pattern).** Fitting a line to $y = x^2$-shaped data gives residuals positive at both ends and negative in the middle (a U shape) → use a curved model.

**Example 4 (code).**
```python
from sklearn.metrics import r2_score, mean_squared_error
y = [2, 4, 5, 4, 5]
pred = [2.8, 3.4, 4.0, 4.6, 5.2]
print(r2_score(y, pred), mean_squared_error(y, pred) ** 0.5)   # 0.6 0.693
```
`r2_score` computes $1 - \frac{\sum e^2}{\sum(y - \bar{y})^2}$. The RMSE divides by $n$ rather than $n - 2$, so it's a little smaller than $s$.

## 5. Common mistakes
- Computing residuals as predicted − actual (the sign convention is actual − predicted).
- Thinking a high $r^2$ proves the model is right (always check the residual plot).
- Interpreting $r^2$ as "the percent of points on the line".

## 6. Practice
Q: Residual formula?
A: $y - \hat{y}$.

Q: Actual 50, predicted 46. Residual?
A: $4$ (the point is above the line).

Q: $r = -0.7$. What is $r^2$, and what does it mean?
A: $0.49$: 49% of the variation in $y$ is explained by the linear relationship with $x$.

Q: A residual plot shows a clear curve. Conclusion?
A: A linear model is not appropriate.

Q: What does a fan-shaped residual plot indicate?
A: The spread of $y$ changes with $x$ (non-constant variance).

@@ 3 | TOPIC | Outliers, influential points and transformations | Leverage, influential points, log and power transformations to straighten curved data | SHOULD
# Outliers, influential points and transformations

## 1. What it covers
- **Outliers** in regression (large residuals), **high-leverage** points (extreme $x$), **influential** points (change the line a lot).
- **Transformations** to straighten curved relationships: log, square root, power.
- Interpreting models fitted to transformed data.

## 2. Where it is used
- **Data cleaning:** one bad sensor reading can wreck a fitted model.
- **Economics and biology:** log-transforming income, population or prices.
- **Science:** power laws (metabolism vs body mass) become straight lines on log–log axes.
- **ML:** log-transforming skewed targets (house prices) often improves linear models.

## 3. What it means
- **Outlier:** a point with a large residual (far above or below the line).
- **High leverage:** a point with an $x$-value far from the others; like a long lever, it can pull the line towards itself.
- **Influential:** removing it changes the slope or intercept noticeably. High-leverage points are often influential.

Always fit the model with and without suspicious points and report both.

**Transformations:**
- **Exponential** growth $y = ab^x$: plot $\log y$ vs $x$ → straight line.
- **Power** law $y = ax^p$: plot $\log y$ vs $\log x$ → straight line with slope $p$.
- Then fit a line to the transformed data and transform back to predict.

## 4. Formula & worked example
$$
y = ab^x \Rightarrow \log y = \log a + x\log b \qquad y = ax^p \Rightarrow \log y = \log a + p\log x
$$

**Example 1 (influence).** Five points near $\hat{y} = 2 + x$ for $x$ from 1 to 5, plus one point at $(20, 5)$. Fitting with it, the slope drops from 1 to about 0.04: the point at $x = 20$ is high-leverage and influential.

**Example 2 (exponential).** Bacteria counts: $t$ = 0, 1, 2, 3, 4; $N$ = 100, 210, 390, 820, 1600. $\ln N$: 4.61, 5.35, 5.97, 6.71, 7.38 → nearly linear with slope ≈ 0.70 → $N \approx 100e^{0.70t}$ (doubling about every hour).

**Example 3 (power law).** Animal mass $M$ vs metabolic rate $B$: log–log plot has slope ≈ 0.75 → $B \propto M^{0.75}$ (Kleiber's law).

**Example 4 (code).**
```python
import numpy as np
t = np.array([0, 1, 2, 3, 4]); N = np.array([100, 210, 390, 820, 1600])
slope, intercept = np.polyfit(t, np.log(N), 1)
print(round(slope, 3), round(np.exp(intercept), 1))   # 0.691 (close to ln 2 = 0.693), 101.5
```
Fitting a straight line to $(t, \ln N)$ gives the growth rate (slope) and $\ln a$ (intercept); exponentiating the intercept recovers $a$.

## 5. Common mistakes
- Deleting outliers without justification.
- Forgetting to transform predictions back to the original scale.
- Interpreting slopes of log models as ordinary unit changes (a slope in $\ln y$ is roughly a **percentage** change in $y$).

## 6. Practice
Q: What makes a point high-leverage?
A: Its $x$-value is far from the other $x$-values.

Q: What is an influential point?
A: One whose removal noticeably changes the regression line.

Q: Which transformation straightens exponential data?
A: Take the log of $y$.

Q: A log–log plot is a straight line with slope 2. Model?
A: $y = ax^2$ (a power law).

Q: In $\ln y = 1.2 + 0.05x$, what does 0.05 roughly mean?
A: Each unit increase in $x$ multiplies $y$ by about $e^{0.05} \approx 1.05$ (≈5% increase).

@@ 3 | TOPIC | Practice & review: Normal distribution and regression | Mixed problem set with worked answers | MUST
# Practice & review: Normal distribution and regression

**How to use this:** 50 minutes; calculator or Python allowed.

## Problems
1. IQ scores $\sim N(100, 15)$. Use the empirical rule for the percent between 70 and 115.
2. Same distribution: proportion above 120? IQ at the 99th percentile?
3. A uniform density on $[2, 12]$: $P(4 < X < 7)$?
4. Describe: points rise steeply then level off, tightly clustered, no outliers.
5. Data: $x$ = 2, 4, 6, 8; $y$ = 3, 7, 8, 12. Find $r$, the regression line and $r^2$.
6. Interpret: $\widehat{\text{rent}} = 4000 + 650 \times \text{rooms}$ (taka), $r^2 = 0.58$.
7. A residual plot shows a fan shape. What does it mean?
8. Population: 1000, 1500, 2250, 3375 at years 0–3. Which transformation makes it linear? Model?

## Answers
1. 70 is $-2\sigma$, 115 is $+1\sigma$ → $47.5\% + 34\% = 81.5\%$.
2. $z = 1.33$ → $1 - 0.9088 \approx 0.091$; 99th percentile $= 100 + 2.326 \times 15 \approx 134.9$.
3. $\frac{3}{10} = 0.3$.
4. Strong, positive, nonlinear (curved) association with diminishing increases.
5. $\bar{x} = 5$, $\bar{y} = 7.5$; $S_{xy} = (-3)(-4.5) + (-1)(-0.5) + (1)(0.5) + (3)(4.5) = 28$; $S_{xx} = 20$; $S_{yy} = 41$. $r = \frac{28}{\sqrt{820}} \approx 0.978$; $b = 1.4$, $a = 0.5$ → $\hat{y} = 0.5 + 1.4x$; $r^2 \approx 0.956$.
6. Each extra room is associated with 650 taka more rent on average; 58% of the variation in rent is explained by the number of rooms. (The 4000 for 0 rooms isn't meaningful.)
7. The variability of $y$ grows with $x$; predictions are less precise for larger $x$.
8. Ratios are all 1.5 → log $y$ vs year is linear; $P = 1000(1.5)^t$.

## Flashcards
Q: Empirical rule?
A: 68%, 95% and 99.7% within 1, 2 and 3 SDs.

Q: Regression slope from $r$?
A: $b = r\frac{s_y}{s_x}$.

Q: What does $r^2$ measure?
A: The fraction of variation in $y$ explained by the linear model.

Q: What should a good residual plot look like?
A: Random scatter around zero with no pattern.
