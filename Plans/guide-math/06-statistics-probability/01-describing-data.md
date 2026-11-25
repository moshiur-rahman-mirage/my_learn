@@ 1 | MODULE | Phase 1 — Describing Data (Foundation) | Data types, charts, centre, spread, box plots, standard deviation, comparing distributions, transformations, z-scores | MUST
# Phase 1: Describing data

**Statistics** is the science of learning from data in the presence of variability. **Probability** is the mathematics of uncertainty. Together they power data science, machine learning, A/B testing, medicine, polling, quality control, finance and every dashboard you've ever seen.

This roadmap goes from describing a data set, to probability, to drawing conclusions about a whole population from a sample (inference). Examples use Python (pandas, NumPy, SciPy) where it helps.

**What you need first:** the Numbers & Arithmetic roadmap (fractions, percentages) and basic algebra. Calculus is helpful for continuous distributions but not required.

Each topic has six parts: what it covers, where it is used, what it means, formulas with worked examples, common mistakes, and practice flashcards.

@@ 2 | MODULE | Data, displays and summaries | Variables, categorical data, plots, centre, spread, box plots, standard deviation | MUST
Before any analysis: look at the data and summarise its centre, spread and shape.

@@ 3 | TOPIC | Data, variables and statistical questions | Individuals and variables, categorical vs quantitative, discrete vs continuous, statistical questions and variability | SHOULD
# Data, variables and statistical questions

## 1. What it covers
- **Individuals** (rows) and **variables** (columns).
- **Categorical** vs **quantitative** variables; **discrete** vs **continuous**.
- What makes a question **statistical** (it expects variability).
- Population vs sample.

## 2. Where it is used
- **Databases and pandas:** each column has a type; choosing the right analysis depends on it (a mean of postal codes is meaningless).
- **Machine learning:** categorical features need encoding (one-hot); quantitative ones may need scaling.
- **Surveys and analytics:** designing questions and dashboards.
- **Data quality:** checking that values match their variable type.

## 3. What it means
A data set is a table: each **row** is an individual (a person, transaction, day), each **column** is a variable.
- **Categorical** variables place individuals into groups: blood type, city, payment method. Numbers can be categorical too (postal code, phone number), if arithmetic on them makes no sense.
- **Quantitative** variables are measured numbers where arithmetic makes sense: income, height, response time.
  - **Discrete:** countable values (number of children, number of clicks).
  - **Continuous:** any value in a range (weight, time).

A **statistical question** expects answers that vary: "How many hours do students in my class sleep?" is statistical; "How many hours did I sleep last night?" is not.

**Population:** everyone you want to know about. **Sample:** the part you actually measure.

## 4. Formula & worked example
**Example 1.** Classify the variables in an e-commerce orders table: `order_id`, `city`, `items`, `total_taka`, `payment_method`, `delivery_minutes`.
- `order_id`: identifier (not analysed); `city`, `payment_method`: categorical; `items`: quantitative discrete; `total_taka`, `delivery_minutes`: quantitative continuous.

**Example 2.** Statistical or not? "What is the average delivery time in Dhaka this month?" → statistical. "When did order #1234 arrive?" → not.

**Example 3 (population vs sample).** A company surveys 500 of its 80 000 customers. Population: all 80 000 customers; sample: the 500.

**Example 4 (pandas).**
```python
import pandas as pd
df = pd.DataFrame({"city": ["Dhaka", "Khulna", "Dhaka"], "total": [1200.0, 850.5, 430.0]})
print(df.dtypes)          # city: object (categorical text), total: float64 (quantitative)
print(df["city"].value_counts())
```
`dtypes` shows how pandas stores each column. `value_counts()` is the natural summary for a categorical variable (counts per group), while numeric columns get means and standard deviations.

## 5. Common mistakes
- Treating numeric codes (postal codes, IDs) as quantitative.
- Confusing the sample with the population.
- Asking non-statistical questions in a survey.

## 6. Practice
Q: Is "number of siblings" categorical or quantitative? Discrete or continuous?
A: Quantitative, discrete.

Q: Is "favourite programming language" categorical?
A: Yes.

Q: Is "Do people in Dhaka prefer tea or coffee?" a statistical question?
A: Yes, the answers vary.

Q: A poll asks 1200 voters. What is the population?
A: All eligible voters (the group the poll wants to describe).

Q: Why shouldn't you average postal codes?
A: They are labels (categorical), not measured quantities.

@@ 3 | TOPIC | Categorical data: bar charts, two-way tables and association | Frequency and relative frequency, bar and pie charts, joint, marginal and conditional distributions | SHOULD
# Categorical data: bar charts, two-way tables and association

## 1. What it covers
- Frequency and **relative frequency** tables; bar and pie charts.
- **Two-way (contingency) tables**: joint, marginal and **conditional** distributions.
- Detecting **association** between two categorical variables.

## 2. Where it is used
- **Product analytics:** conversion rate by device (mobile vs desktop).
- **Medicine:** treatment vs outcome tables in clinical trials.
- **Marketing:** customer segment vs product preference.
- **pandas:** `pd.crosstab` builds two-way tables; chi-square tests (Phase 6) test association.

## 3. What it means
**Relative frequency** = count ÷ total, so groups of different sizes can be compared.

A **two-way table** counts individuals by two categorical variables at once.
- **Joint** distribution: each cell ÷ grand total.
- **Marginal** distribution: row or column totals ÷ grand total (one variable alone).
- **Conditional** distribution: each cell ÷ its **row (or column) total**, the distribution of one variable **within** a group of the other.

**Association:** if the conditional distributions differ between groups, the variables are associated. Compare **percentages**, never raw counts, when groups have different sizes.

## 4. Formula & worked example
A website test: 1000 visitors, by device and whether they bought.
| | Bought | Didn't | Total |
|---|---|---|---|
| Mobile | 36 | 564 | 600 |
| Desktop | 40 | 360 | 400 |
| Total | 76 | 924 | 1000 |

**Example 1 (marginal).** Overall purchase rate $= \frac{76}{1000} = 7.6\%$; mobile share of visitors $= 60\%$.

**Example 2 (conditional).** Purchase rate given mobile $= \frac{36}{600} = 6\%$; given desktop $= \frac{40}{400} = 10\%$.

**Example 3 (association).** 6% vs 10% → device and buying are associated (desktop users convert better). Raw counts (36 vs 40) would hide how big the difference is.

**Example 4 (joint).** $P(\text{mobile and bought}) = \frac{36}{1000} = 3.6\%$.

**Example 5 (code).**
```python
import pandas as pd
df = pd.DataFrame({"device": ["mobile"]*600 + ["desktop"]*400,
                   "bought": [1]*36 + [0]*564 + [1]*40 + [0]*360})
print(pd.crosstab(df.device, df.bought, normalize="index"))   # conditional % by row
```
`normalize="index"` divides each row by its row total, giving the conditional distribution within each device.

## 5. Common mistakes
- Comparing counts instead of percentages across groups of different sizes.
- Dividing by the grand total when a conditional distribution (row total) is needed.
- Using a pie chart with many tiny slices (a bar chart is clearer).

## 6. Practice
Q: Relative frequency formula?
A: Category count ÷ total count.

Q: In the table, what is $P(\text{bought} \mid \text{desktop})$?
A: $10\%$.

Q: What is a marginal distribution?
A: The distribution of one variable alone, from the table's totals.

Q: How do you check for association in a two-way table?
A: Compare conditional distributions (percentages) across groups.

Q: What fraction of buyers used mobile?
A: $\frac{36}{76} \approx 47\%$.

@@ 3 | TOPIC | Displaying quantitative data: dot plots, stem plots and histograms | Building plots, choosing bins, describing shape (symmetric, skewed, modes), outliers | SHOULD
# Displaying quantitative data: dot plots, stem plots and histograms

## 1. What it covers
- **Dot plots**, **stem-and-leaf plots** and **histograms**.
- Choosing bin widths.
- Describing a distribution: **shape, centre, spread, unusual values** (SOCS).
- Shapes: symmetric, **right-skewed**, left-skewed, uniform, bimodal.

## 2. Where it is used
- **Exploratory data analysis:** the first step in any data project (`df.hist()`).
- **Performance engineering:** latency histograms show long tails (a few very slow requests) that averages hide.
- **Finance:** return distributions; income distributions are right-skewed.
- **Quality control:** spotting two production processes mixed together (bimodal).

## 3. What it means
- **Dot plot:** one dot per value along a number line. Good for small data sets.
- **Stem-and-leaf:** split each number into a stem (leading digits) and a leaf (last digit). Shows the shape and keeps the actual values.
- **Histogram:** group values into equal-width **bins** and draw bars for the count in each bin. Good for large data sets.

**Shape:**
- **Symmetric:** both sides roughly mirror images (heights).
- **Right-skewed:** a long tail to the right (incomes, house prices, response times). Most values are small; a few are very large.
- **Left-skewed:** a long tail to the left (exam scores on an easy test).
- **Bimodal:** two peaks, often meaning two different groups mixed together.

Bin width matters: too wide hides detail, too narrow looks noisy.

## 4. Formula & worked example
**Example 1 (stem plot).** Test scores: 52, 61, 64, 68, 70, 71, 73, 75, 75, 78, 82, 85, 91.
```text
5 | 2
6 | 1 4 8
7 | 0 1 3 5 5 8
8 | 2 5
9 | 1
```
Stems are the tens digit and leaves the ones digit. The shape is roughly symmetric, centred in the 70s.

**Example 2 (histogram bins).** API response times (ms) with bins 0–100, 100–200, ...: counts 520, 300, 90, 40, 20, 10, 5, ... → strongly right-skewed (a long tail of slow requests).

**Example 3 (bimodal).** Heights of a mixed group of adults show peaks near 155 cm and 170 cm → probably two groups (e.g. by sex); analyse them separately.

**Example 4 (code).**
```python
import numpy as np
import matplotlib.pyplot as plt
times = np.random.lognormal(mean=4.5, sigma=0.6, size=5000)   # right-skewed, like latencies
plt.hist(times, bins=50)
plt.xlabel("response time (ms)"); plt.ylabel("count"); plt.show()
```
A log-normal sample mimics real latency data: most requests are fast, with a long right tail. `bins=50` sets how many bars the histogram uses.

## 5. Common mistakes
- Leaving gaps between histogram bars for continuous data (or using a bar chart for quantitative data).
- Choosing bins so wide that the shape disappears.
- Describing skew by the side where the data piles up (skew is named after the **tail**).

## 6. Practice
Q: A long tail to the right is called?
A: Right-skewed (positively skewed).

Q: What might a bimodal histogram indicate?
A: Two different groups mixed in one data set.

Q: Which plot keeps the actual data values?
A: Stem-and-leaf plot (and dot plot).

Q: What four things describe a distribution?
A: Shape, centre, spread and unusual values (outliers).

Q: Typical shape of income data?
A: Right-skewed.

@@ 3 | TOPIC | Measures of centre: mean, median and mode | Computing each, mean as balance point, effect of outliers and skew, choosing the right measure | MUST
# Measures of centre: mean, median and mode

## 1. What it covers
- **Mean** (average), **median** (middle value), **mode** (most frequent).
- The mean as a **fair share** and a **balance point**.
- How skew and outliers affect mean vs median.
- Choosing the right measure; weighted means.

## 2. Where it is used
- **Economics:** median income is reported because the mean is pulled up by a few billionaires.
- **Performance engineering:** median (p50) response time, alongside p95/p99.
- **Real estate:** median house price.
- **Grades:** weighted averages (GPA, CGPA).
- **Machine learning:** the mean minimises squared error; the median minimises absolute error.

## 3. What it means
- **Mean** $\bar{x}$: add all values and divide by how many. It's the "fair share" if the total were split equally, and the **balance point** of the data on a number line.
- **Median:** the middle value when sorted (the average of the two middle values if $n$ is even). Half the data is below, half above.
- **Mode:** the most common value (useful for categories too).

**Outliers:** one huge value drags the mean a lot but barely moves the median. The median is **resistant**; the mean is not.

**Skew:** in right-skewed data, mean > median (the tail pulls the mean right). In left-skewed data, mean < median. Symmetric: mean ≈ median.

## 4. Formula & worked example
$$
\bar{x} = \frac{1}{n}\sum_{i=1}^{n} x_i \qquad \text{weighted mean} = \frac{\sum w_ix_i}{\sum w_i}
$$

**Example 1.** Data: 4, 8, 6, 5, 3, 8, 9. Sorted: 3, 4, 5, 6, 8, 8, 9.
- Mean $= \frac{43}{7} \approx 6.14$; median $= 6$; mode $= 8$.

**Example 2 (even n).** 12, 15, 18, 20, 25, 30 → median $= \frac{18 + 20}{2} = 19$.

**Example 3 (outlier).** Salaries (thousand taka): 30, 32, 35, 38, 40, 400.
- Mean $\approx 95.8$; median $= 36.5$. The median describes a typical employee far better.

**Example 4 (weighted GPA).** Grades 4.0 (3 credits), 3.5 (4 credits), 3.0 (2 credits).
- $\frac{12 + 14 + 6}{9} = \frac{32}{9} \approx 3.56$

**Example 5 (code).**
```python
import numpy as np
from statistics import mode
x = np.array([30, 32, 35, 38, 40, 400])
print(x.mean(), np.median(x), mode([1, 2, 2, 3]))   # 95.83 36.5 2
```

## 5. Common mistakes
- Forgetting to sort before finding the median.
- Using the mean for strongly skewed data or data with outliers.
- Averaging averages from groups of different sizes without weighting.

## 6. Practice
Q: Median of 7, 2, 9, 4, 5?
A: Sorted 2, 4, 5, 7, 9 → $5$.

Q: Which is resistant to outliers: mean or median?
A: The median.

Q: In right-skewed data, how do mean and median compare?
A: Mean > median.

Q: Mean of 10, 20, 30, 40?
A: $25$.

Q: Class A (20 students) averages 70; class B (30 students) averages 80. Combined mean?
A: $\frac{1400 + 2400}{50} = 76$.

@@ 3 | TOPIC | Measures of spread: range, quartiles, IQR, box plots and outliers | Five-number summary, interquartile range, box plots, the 1.5×IQR rule, mean absolute deviation | MUST
# Measures of spread: range, quartiles, IQR, box plots and outliers

## 1. What it covers
- **Range** and its weakness.
- **Quartiles** $Q_1$, $Q_3$ and the **interquartile range** (IQR).
- The **five-number summary** and **box plots**.
- The **1.5 × IQR rule** for outliers.
- **Mean absolute deviation** (MAD).

## 2. Where it is used
- **Data cleaning:** flagging outliers before training a model (e.g. sensor glitches, data-entry errors).
- **Performance monitoring:** spread of response times; box plots per server.
- **Finance:** spread of returns as a measure of risk.
- **Comparing groups:** side-by-side box plots in reports (`df.boxplot()`).

## 3. What it means
Two data sets can have the same centre but very different spread: a class where everyone scores 70 vs one with scores from 30 to 100.

- **Range** = max − min. Simple, but one outlier changes it completely.
- **Quartiles** split sorted data into four equal parts: $Q_1$ (25th percentile), median, $Q_3$ (75th percentile).
- **IQR** $= Q_3 - Q_1$: the spread of the **middle half**. Resistant to outliers.
- **Five-number summary:** min, $Q_1$, median, $Q_3$, max. A **box plot** draws it: a box from $Q_1$ to $Q_3$ with a line at the median, and whiskers out to the data.
- **Outlier rule:** below $Q_1 - 1.5 \times \text{IQR}$ or above $Q_3 + 1.5 \times \text{IQR}$.
- **MAD:** the average distance of values from the mean.

## 4. Formula & worked example
$$
\text{IQR} = Q_3 - Q_1 \qquad \text{fences: } Q_1 - 1.5\,\text{IQR},\ Q_3 + 1.5\,\text{IQR} \qquad \text{MAD} = \frac{1}{n}\sum\lvert x_i - \bar{x}\rvert
$$

**Example 1.** Data (sorted): 2, 4, 5, 7, 8, 9, 11, 12, 14, 30.
- Median $= \frac{8 + 9}{2} = 8.5$; $Q_1$ = median of the lower half (2, 4, 5, 7, 8) $= 5$; $Q_3$ = median of the upper half (9, 11, 12, 14, 30) $= 12$.
- IQR $= 7$; fences: $5 - 10.5 = -5.5$ and $12 + 10.5 = 22.5$ → **30 is an outlier**.
- Five-number summary: 2, 5, 8.5, 12, 30.

**Example 2 (MAD).** 2, 4, 6, 8: mean 5; distances 3, 1, 1, 3 → MAD $= 2$.

**Example 3 (comparing).** Two delivery services both have median 30 min; A has IQR 5 min, B has IQR 20 min → A is more consistent.

**Example 4 (code).**
```python
import numpy as np
x = np.array([2, 4, 5, 7, 8, 9, 11, 12, 14, 30])
q1, q3 = np.percentile(x, [25, 75])
iqr = q3 - q1
print(q1, q3, x[(x < q1 - 1.5*iqr) | (x > q3 + 1.5*iqr)])   # 5.5 11.75 [30]
```
NumPy uses a slightly different quartile method (interpolation), so it gives $Q_1 = 5.5$, $Q_3 = 11.75$ instead of 5 and 12. Different software can differ a little; the conclusion (30 is an outlier) is the same.

## 5. Common mistakes
- Using the range as the main measure of spread.
- Including the median in both halves when computing quartiles (methods vary; be consistent).
- Deleting outliers automatically; first find out **why** they're there (an error, or a real extreme?).

## 6. Practice
Q: IQR of a data set with $Q_1 = 20$ and $Q_3 = 35$?
A: $15$.

Q: Upper outlier fence for $Q_1 = 20$, $Q_3 = 35$?
A: $35 + 22.5 = 57.5$.

Q: What does a box plot show?
A: The five-number summary (min, $Q_1$, median, $Q_3$, max) and outliers.

Q: Why is the IQR better than the range for skewed data?
A: It ignores the extreme quarters, so outliers don't affect it.

Q: MAD of 10, 10, 10, 10?
A: $0$.

@@ 3 | TOPIC | Variance and standard deviation | Deviations from the mean, population vs sample formulas (n vs n − 1), interpretation, code | MUST
# Variance and standard deviation

## 1. What it covers
- **Variance** and **standard deviation** (SD): the standard measures of spread.
- Population ($\sigma$, divide by $N$) vs sample ($s$, divide by $n - 1$).
- Computing by hand and with code; interpreting the SD.

## 2. Where it is used
- **Finance:** volatility (risk) of an investment is the SD of its returns.
- **Manufacturing:** Six Sigma quality control aims for processes with tiny SD.
- **Machine learning:** feature standardisation divides by the SD; the loss MSE is a variance-like quantity.
- **Science:** error bars and measurement uncertainty.
- **Inference:** confidence intervals and tests all use the SD.

## 3. What it means
**Deviation** = how far a value is from the mean: $x_i - \bar{x}$. We want the "typical deviation", but deviations add up to zero (positives and negatives cancel). So square them first:
- **Variance** = average squared deviation.
- **Standard deviation** = square root of the variance, which brings the units back (cm, not cm²).

Roughly: **the SD is the typical distance of a value from the mean.** A small SD means data are clustered; a large SD means spread out.

**Why $n - 1$ for a sample?** A sample's values are slightly closer to their own mean than to the true population mean, so dividing by $n$ underestimates the spread. Dividing by $n - 1$ corrects this (Bessel's correction).

## 4. Formula & worked example
$$
\sigma^2 = \frac{1}{N}\sum(x_i - \mu)^2 \qquad s^2 = \frac{1}{n - 1}\sum(x_i - \bar{x})^2 \qquad s = \sqrt{s^2}
$$

**Example 1 (by hand).** Sample: 4, 6, 8, 10, 12. Mean 8.
- Deviations: $-4, -2, 0, 2, 4$; squares: 16, 4, 0, 4, 16; sum 40
- $s^2 = \frac{40}{4} = 10$, $s = \sqrt{10} \approx 3.16$
- (As a population: $\sigma^2 = 8$, $\sigma \approx 2.83$.)

**Example 2 (compare).** Two investments with average return 8%: A's SD 2%, B's SD 15% → B is much riskier.

**Example 3 (shortcut).** $\sum(x - \bar{x})^2 = \sum x^2 - n\bar{x}^2$. For the data above: $\sum x^2 = 360$, $n\bar{x}^2 = 320$ → 40 ✓

**Example 4 (code).**
```python
import numpy as np
x = np.array([4, 6, 8, 10, 12])
print(x.std(), x.std(ddof=1))      # 2.828 (population)  3.162 (sample)
import pandas as pd
print(pd.Series(x).std())          # 3.162  (pandas defaults to the sample SD)
```
NumPy's `std` divides by $n$ unless you pass `ddof=1`; pandas divides by $n - 1$ by default. This difference is a common source of small discrepancies between tools.

## 5. Common mistakes
- Forgetting to square the deviations (they sum to 0).
- Reporting the variance when the SD is wanted (wrong units).
- Mixing up $n$ and $n - 1$ (and NumPy vs pandas defaults).

## 6. Practice
Q: SD of 5, 5, 5, 5?
A: $0$.

Q: Sample variance of 1, 2, 3?
A: Mean 2; squares 1, 0, 1 → $\frac{2}{2} = 1$.

Q: Why take the square root of the variance?
A: To return to the original units.

Q: Why divide by $n - 1$ for a sample?
A: To correct the underestimate of spread from using the sample's own mean.

Q: Which numpy argument gives the sample SD?
A: `ddof=1`.

@@ 3 | TOPIC | Comparing distributions | Comparing centre, spread, shape and outliers across groups; side-by-side box plots and dot plots | SHOULD
# Comparing distributions

## 1. What it covers
- Comparing two or more groups by **shape, centre, spread and outliers**.
- Side-by-side box plots, back-to-back stem plots, overlaid histograms.
- Measuring the size of a difference relative to the spread.

## 2. Where it is used
- **A/B testing:** comparing a metric between control and variant.
- **Education:** scores of two classes or two teaching methods.
- **Operations:** delivery times across warehouses or couriers.
- **Healthcare:** outcomes in treatment vs control groups.

## 3. What it means
A good comparison mentions **all four** features, **in context**, with numbers:
- **Centre:** which group is typically higher (use medians for skewed data, means for symmetric).
- **Spread:** which is more variable (IQR or SD).
- **Shape:** skewed or symmetric?
- **Outliers:** any unusual values?

**Is the difference big?** A difference in centres matters more when the spreads are small. A rough measure: difference in means ÷ typical SD (an "effect size"). If the boxes barely overlap, the difference is large relative to the variability.

## 4. Formula & worked example
$$
\text{effect size (Cohen's } d) \approx \frac{\bar{x}_1 - \bar{x}_2}{s}
$$

**Example 1.** Delivery times (minutes):
| | Median | IQR | Shape | Outliers |
|---|---|---|---|---|
| Courier A | 32 | 8 | roughly symmetric | none |
| Courier B | 28 | 18 | right-skewed | two over 90 |
- "B is faster typically (median 28 vs 32 min), but much less consistent (IQR 18 vs 8), with a right skew and two very late deliveries over 90 min. A is more predictable."

**Example 2 (effect size).** Variant page time-on-site: mean 5.2 min vs control 4.8 min; SD about 2 min → $d = 0.2$: a small effect relative to the variation.

**Example 3 (code).**
```python
import pandas as pd
df = pd.DataFrame({"courier": ["A"]*5 + ["B"]*5,
                   "minutes": [30, 32, 33, 35, 31, 22, 25, 28, 40, 95]})
print(df.groupby("courier")["minutes"].describe())
df.boxplot(column="minutes", by="courier")
```
`groupby(...).describe()` gives count, mean, SD and quartiles per group in one table; the box plot shows the same comparison visually.

## 5. Common mistakes
- Comparing only centres and ignoring spread.
- Not writing the comparison in context (units and meaning).
- Using means when one group has extreme outliers.

## 6. Practice
Q: Four features to compare between distributions?
A: Shape, centre, spread and outliers.

Q: Group A median 50 (IQR 5), group B median 55 (IQR 30). Which difference is clearer: centre or spread?
A: Spread (B is far more variable; the centre difference is small relative to B's spread).

Q: Which centre measure for strongly skewed groups?
A: Median.

Q: What does it mean if two box plots don't overlap at all?
A: The difference between groups is large relative to their spread.

Q: Means 72 and 68, SD 8. Effect size?
A: $0.5$ (a medium effect).

@@ 3 | TOPIC | Effects of transformations on summary statistics | Adding a constant, multiplying by a constant, unit conversions and their effect on centre and spread | MUST
# Effects of transformations on summary statistics

## 1. What it covers
- What happens to the mean, median, SD and IQR when you **add** a constant to every value.
- What happens when you **multiply** every value by a constant.
- Unit conversions (°C → °F, taka → USD) and **standardisation**.

## 2. Where it is used
- **Unit conversions** in reports and pipelines.
- **Feature scaling in ML:** standardising ($\frac{x - \mu}{\sigma}$) and min-max scaling.
- **Payroll:** a flat raise vs a percentage raise affects the spread differently.
- **Grading:** curving scores.

## 3. What it means
- **Adding** $c$ to every value shifts the whole distribution: centre measures (mean, median) shift by $c$; spread measures (SD, IQR, range) **don't change**, because everything moves together.
- **Multiplying** by $k$ stretches it: centres **and** spreads are multiplied by $\lvert k\rvert$ (variance by $k^2$).
- **Shape** doesn't change under these linear transformations.

For $y = a + bx$: $\bar{y} = a + b\bar{x}$ and $s_y = \lvert b\rvert s_x$.

## 4. Formula & worked example
$$
y = a + bx:\quad \bar{y} = a + b\bar{x},\quad \text{median}_y = a + b\,\text{median}_x,\quad s_y = \lvert b\rvert s_x,\quad s_y^2 = b^2s_x^2
$$

**Example 1 (flat raise).** Salaries: mean 40 000, SD 6000. Everyone gets +3000.
- New mean 43 000; SD still 6000.

**Example 2 (percentage raise).** Same salaries, everyone gets +10% (×1.1).
- New mean 44 000; new SD 6600 (spread grows).

**Example 3 (temperature).** Daily highs: mean 30°C, SD 4°C. In °F ($F = 32 + 1.8C$):
- Mean $= 32 + 54 = 86$°F; SD $= 1.8 \times 4 = 7.2$°F.

**Example 4 (standardise).** Scores with mean 60 and SD 12; $z = \frac{x - 60}{12}$ → the z-scores have mean 0 and SD 1.

## 5. Common mistakes
- Adding the constant to the SD.
- Multiplying the variance by $k$ instead of $k^2$.
- Thinking the shape changes under a linear transformation.

## 6. Practice
Q: Add 5 to every value. What happens to the IQR?
A: Unchanged.

Q: Multiply every value by 3. What happens to the SD?
A: It triples.

Q: Mean 20 cm, SD 2 cm. Convert to mm. New mean and SD?
A: 200 mm and 20 mm.

Q: Variance 9, every value doubled. New variance?
A: $36$.

Q: Mean and SD of standardised scores?
A: 0 and 1.

@@ 3 | TOPIC | Percentiles and z-scores | Percentile rank, standardising values, comparing across different scales | MUST
# Percentiles and z-scores

## 1. What it covers
- **Percentiles**: the value below which a given percent of data falls.
- **Percentile rank** of a value.
- **z-scores**: how many SDs a value is from the mean.
- Comparing values from different distributions.

## 2. Where it is used
- **Admissions and exams:** percentile ranks on standardised tests.
- **Child growth charts:** height and weight percentiles.
- **Performance monitoring:** p95 and p99 latency (the time under which 95% or 99% of requests complete).
- **Anomaly detection:** flagging values with $\lvert z\rvert > 3$.
- **ML preprocessing:** standardising features to z-scores.

## 3. What it means
- The **$p$th percentile** is the value with $p\%$ of the data at or below it. The median is the 50th percentile; $Q_1$ the 25th; $Q_3$ the 75th.
- A **z-score** standardises a value: $z = \frac{x - \text{mean}}{\text{SD}}$. $z = 2$ means 2 SDs above the mean; $z = -1$ means 1 SD below.

z-scores let you compare apples and oranges: a score of 80 on a hard test may be more impressive than 90 on an easy one.

## 4. Formula & worked example
$$
z = \frac{x - \mu}{\sigma} \qquad x = \mu + z\sigma
$$

**Example 1.** Maths test: mean 65, SD 10; you scored 85 → $z = 2$. English test: mean 78, SD 5; you scored 88 → $z = 2$. Equally impressive relative to classmates.

**Example 2.** Physics: mean 50, SD 8, score 66 → $z = 2$; Chemistry: mean 70, SD 4, score 76 → $z = 1.5$. Physics was relatively better.

**Example 3 (back to a value).** Heights: mean 165 cm, SD 7 cm. What height has $z = -1.5$? → $165 - 10.5 = 154.5$ cm.

**Example 4 (latency percentiles, code).**
```python
import numpy as np
lat = np.random.lognormal(4.5, 0.6, 100_000)
p50, p95, p99 = np.percentile(lat, [50, 95, 99])
print(round(p50), round(p95), round(p99))   # e.g. 90 241 365 (ms)
```
For skewed latency data, p95 and p99 are far above the median: 1 in 100 users waits about 4 times longer than the typical user, which the mean alone wouldn't reveal.

**Example 5 (anomaly).** Daily sales: mean 500, SD 40. A day with 650 sales has $z = 3.75$ → investigate.

## 5. Common mistakes
- Confusing a percentile (a value) with a percentage (a proportion).
- Subtracting in the wrong order ($\mu - x$).
- Using z-scores for strongly skewed data and expecting normal-table percentages to apply.

## 6. Practice
Q: Mean 100, SD 15, value 130. z-score?
A: $2$.

Q: z-score of a value equal to the mean?
A: $0$.

Q: What does p99 latency of 800 ms mean?
A: 99% of requests complete in 800 ms or less.

Q: Mean 40, SD 5. Value with $z = -2$?
A: $30$.

Q: Score 75 (mean 70, SD 2.5) vs score 80 (mean 72, SD 8). Which is relatively better?
A: The first ($z = 2$ vs $z = 1$).

@@ 3 | TOPIC | Practice & review: Describing data | Mixed problem set with worked answers | MUST
# Practice & review: Describing data

**How to use this:** 45 minutes; calculator allowed.

## Problems
1. Classify: (a) blood group, (b) number of app logins per day, (c) battery life in hours.
2. In a survey, 120 of 300 students and 90 of 200 teachers prefer online classes. Compare conditional percentages.
3. Data: 3, 7, 7, 8, 10, 12, 13, 15, 40. Find the mean, median, mode, $Q_1$, $Q_3$, IQR, and check for outliers.
4. Sample: 2, 4, 4, 5, 7, 8. Find the sample SD.
5. A histogram of house prices has a long right tail. Which is larger, mean or median? Which should a news report use?
6. Test scores: mean 72, SD 9. Each score is curved by $\times 1.1 + 2$. New mean and SD?
7. Ali scored 82 (mean 74, SD 4) and Bina scored 88 (mean 80, SD 6) on different tests. Who did relatively better?
8. Two gyms' membership fees: Gym A median 2000, IQR 300; Gym B median 1800, IQR 900 with one outlier at 6000. Write a comparison.

## Answers
1. (a) categorical; (b) quantitative discrete; (c) quantitative continuous.
2. Students 40%, teachers 45% → teachers slightly more likely to prefer online.
3. Mean $\frac{115}{9} \approx 12.8$; median 10; mode 7; $Q_1 = 7$ (median of 3, 7, 7, 8), $Q_3 = 14$ (median of 12, 13, 15, 40); IQR 7; upper fence $24.5$ → 40 is an outlier.
4. Mean 5; squared deviations 9, 1, 1, 0, 4, 9 → 24; $s^2 = 4.8$; $s \approx 2.19$.
5. Mean > median; report the median.
6. Mean $= 72 \times 1.1 + 2 = 81.2$; SD $= 9.9$.
7. Ali $z = 2$, Bina $z = 1.33$ → Ali.
8. Gym B is cheaper typically (median 1800 vs 2000) but far more variable (IQR 900 vs 300) and has an extreme outlier at 6000; Gym A's fees are more consistent.

## Flashcards
Q: Which centre and spread measures are resistant to outliers?
A: Median and IQR.

Q: 1.5 × IQR rule?
A: Values below $Q_1 - 1.5\,\text{IQR}$ or above $Q_3 + 1.5\,\text{IQR}$ are outliers.

Q: Sample SD formula?
A: $s = \sqrt{\frac{\sum(x - \bar{x})^2}{n - 1}}$.

Q: z-score formula?
A: $z = \frac{x - \mu}{\sigma}$.
