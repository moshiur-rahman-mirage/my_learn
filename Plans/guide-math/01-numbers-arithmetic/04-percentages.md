@@ 1 | MODULE | Phase 4 — Percentages (Core) | Converting forms, percent of a quantity, finding the whole, percent change, discounts, tax, interest, error, successive changes | MUST
# Phase 4: Percentages

"Per cent" means "per hundred". A percentage is just a fraction with denominator 100, which makes different quantities easy to compare. Percentages are everywhere in money, news, statistics and business dashboards, and they are also where people are most easily misled (a 50% rise followed by a 50% fall is **not** back to the start).

**One idea solves almost every problem in this phase:** a percentage change is a **multiplier**. Up 15% means ×1.15; down 15% means ×0.85.

@@ 2 | MODULE | Percent basics and applications | Conversions, part–percent–whole, change, money applications, interest, error, successive changes | MUST
Turn every percentage into a decimal multiplier and these problems become one-line calculations.

@@ 3 | TOPIC | Fractions, decimals and percents | Converting between the three forms; benchmark percents | MUST
# Fractions, decimals and percents

## 1. What it covers
- What "percent" means.
- Converting between fractions, decimals and percents in every direction.
- Benchmark values to know by heart ($\frac{1}{8} = 12.5\%$, etc.).

## 2. Where it is used
- **News and reports:** "inflation 9.5%", "turnout 41%".
- **Dashboards and analytics:** conversion rate, bounce rate, CPU usage, test coverage.
- **Probability:** "70% chance of rain" = 0.7.
- **Programming:** progress bars compute `done / total * 100`; CSS widths like `width: 33.33%`.
- **Exams and grading.**

## 3. What it means
$\%$ means "out of 100": $37\% = \frac{37}{100} = 0.37$.
- **Percent → decimal:** divide by 100 (move the point 2 left).
- **Decimal → percent:** multiply by 100 (move the point 2 right).
- **Fraction → percent:** divide top by bottom, then ×100.
- **Percent → fraction:** put over 100 and simplify.

Percentages above 100% are fine: $150\% = 1.5$, one and a half times the original.

## 4. Formula & worked example
$$
p\% = \frac{p}{100} \qquad \text{fraction} \to \% : \frac{a}{b} \times 100\%
$$
Benchmarks: $\frac{1}{2} = 50\%$, $\frac{1}{4} = 25\%$, $\frac{3}{4} = 75\%$, $\frac{1}{5} = 20\%$, $\frac{1}{8} = 12.5\%$, $\frac{1}{3} = 33.\overline{3}\%$, $\frac{2}{3} = 66.\overline{6}\%$, $\frac{1}{10} = 10\%$.

**Example 1.** $\frac{7}{20}$ as a percent: $7 \div 20 = 0.35 = 35\%$

**Example 2.** $0.045 = 4.5\%$

**Example 3.** $64\%$ as a fraction: $\frac{64}{100} = \frac{16}{25}$

**Example 4.** $2.5\%$ as a decimal and fraction: $0.025 = \frac{25}{1000} = \frac{1}{40}$

**Example 5.** $\frac{5}{6}$ as a percent: $0.8\overline{3} \approx 83.3\%$

**Example 6 (code).** A progress bar.
```python
done, total = 37, 120
pct = done / total * 100
print(f"{pct:.1f}%")      # 30.8%
```
`done / total` gives the fraction as a decimal (0.3083...); multiplying by 100 turns it into a percent, and `:.1f` rounds to one decimal place for display.

## 5. Common mistakes
- $0.5\% = 0.5$. No: $0.5\% = 0.005$.
- Moving the decimal point the wrong way.
- Writing $\frac{1}{3} = 33\%$ exactly. It is $33.\overline{3}\%$.

## 6. Practice
Q: $\frac{3}{8}$ as a percent?
A: $37.5\%$.

Q: $0.6\%$ as a decimal?
A: $0.006$.

Q: $125\%$ as a decimal?
A: $1.25$.

Q: $85\%$ as a simplified fraction?
A: $\frac{17}{20}$.

Q: $0.125$ as a percent?
A: $12.5\%$.

@@ 3 | TOPIC | Percent of a quantity and finding the whole | part = percent × whole, finding any one of the three | MUST
# Percent of a quantity and finding the whole

## 1. What it covers
- Finding a percent **of** a number.
- Finding **what percent** one number is of another.
- Finding the **whole** when you know a part and its percent.
- Mental shortcuts (10%, 1%, 5%).

## 2. Where it is used
- **Tips, VAT and service charges.**
- **Survey results:** 342 of 900 people said yes. What percent?
- **Exams:** "you need 40% to pass a 75-mark paper".
- **Business metrics:** if 3% of visitors buy and you want 600 sales, how many visitors do you need?

## 3. What it means
Every percent problem uses one equation with three quantities:
$$
\text{part} = \text{percent (as a decimal)} \times \text{whole}
$$
Two are given, and you find the third. "**Of**" means multiply; "**is**" means equals.

**Mental shortcut:** 10% = move the point one place left; 1% = two places; 5% = half of 10%. Build others: 15% = 10% + 5%.

## 4. Formula & worked example
$$
\text{part} = p \times W \qquad p = \frac{\text{part}}{W} \qquad W = \frac{\text{part}}{p}
$$

**Example 1.** 35% of 240 = $0.35 \times 240 = 84$

**Example 2 (mental).** 15% of 860 = $86 + 43 = 129$

**Example 3.** 54 is what percent of 72?
- $\frac{54}{72} = 0.75 = 75\%$

**Example 4 (find the whole).** 18 students (that's 12% of the school) wear glasses. How many students in total?
- $W = \frac{18}{0.12} = 150$

**Example 5.** A conversion rate is 2.5%. How many visitors are needed for 600 sales?
- $W = \frac{600}{0.025} = 24\,000$

**Example 6.** 4% of a number is 7. What is 100%?
- $\frac{7}{0.04} = 175$

## 5. Common mistakes
- Dividing the wrong way in "what percent": always **part ÷ whole**.
- Multiplying by 35 instead of 0.35.
- In "find the whole" problems, multiplying instead of dividing.

## 6. Practice
Q: 8% of 450?
A: $36$.

Q: 27 out of 45 as a percent?
A: $60\%$.

Q: 30 is 40% of what?
A: $75$.

Q: 12.5% of 640?
A: $80$ (it's $\frac{1}{8}$).

Q: You scored 52 out of 80. Percentage?
A: $65\%$.

@@ 3 | TOPIC | Percent increase and decrease | Percent change formula, multipliers, finding the original value | MUST
# Percent increase and decrease

## 1. What it covers
- Increasing or decreasing a value by a percentage, using a **multiplier**.
- The **percent change** formula.
- **Reverse percentages**: finding the original price before a change.

## 2. Where it is used
- **Prices and inflation:** "rice up 12% this year".
- **Salaries:** an 8% raise.
- **Business dashboards:** month-over-month growth; stock price changes.
- **Performance engineering:** "the new query is 35% faster".
- **Population and data growth.**

## 3. What it means
**Multiplier method:** increasing by $p\%$ means keeping 100% and adding $p\%$, so you multiply by $1 + \frac{p}{100}$. Decreasing by $p\%$: multiply by $1 - \frac{p}{100}$.
- +20% → ×1.20; −20% → ×0.80; +5% → ×1.05; −2.5% → ×0.975.

**Percent change** compares the change with the **original** value:
$$
\text{percent change} = \frac{\text{new} - \text{original}}{\text{original}} \times 100\%
$$

**Reverse:** if the new value is known, **divide** by the multiplier. A shirt costs 1150 taka after a 15% VAT? Original $= 1150 \div 1.15 = 1000$. (Subtracting 15% of 1150 gives the wrong answer.)

## 4. Formula & worked example
$$
\text{new} = \text{original} \times \left(1 \pm \frac{p}{100}\right) \qquad \text{original} = \frac{\text{new}}{1 \pm p/100}
$$

**Example 1.** Increase 2400 by 15%: $2400 \times 1.15 = 2760$

**Example 2.** Decrease 850 by 8%: $850 \times 0.92 = 782$

**Example 3 (percent change).** A price rose from 60 to 75 taka.
- $\frac{75 - 60}{60} = 0.25 = 25\%$ increase

**Example 4 (decrease).** Page load time fell from 3.2 s to 2.0 s.
- $\frac{2.0 - 3.2}{3.2} = -0.375$ → 37.5% decrease

**Example 5 (reverse).** After a 20% discount a phone costs 18 400 taka. Original price?
- $18\,400 \div 0.80 = 23\,000$ taka

## 5. Common mistakes
- Dividing by the **new** value in the percent change formula. Always divide by the **original**.
- Reverse percentages by subtracting: $18\,400 - 20\% \text{ of } 18\,400$ ✗. Divide by the multiplier.
- Saying "from 4% to 6% is a 2% increase". It is a **2 percentage point** increase, which is a **50%** relative increase.

## 6. Practice
Q: Multiplier for a 7% decrease?
A: $0.93$.

Q: 480 increased by 12.5%?
A: $540$.

Q: From 250 to 210. Percent change?
A: $-16\%$ (a 16% decrease).

Q: After a 10% raise, a salary is 33 000. What was it before?
A: $30\,000$.

Q: Interest rates went from 5% to 6%. Percentage point change and percent change?
A: 1 percentage point; a 20% increase.

@@ 3 | TOPIC | Discounts, tax, tips, markup and commission | Multi-step money problems with percentages | MUST
# Discounts, tax, tips, markup and commission

## 1. What it covers
- **Discount** (price goes down) and **sales tax / VAT** (price goes up).
- **Tips** and service charges.
- **Markup** (shop price above cost) and **profit margin**.
- **Commission** (pay as a percent of sales).

## 2. Where it is used
- **Shopping and restaurants:** sale prices, VAT, service charge.
- **Retail and e-commerce:** pricing products from cost; e-commerce platforms take a commission per sale.
- **Freelancing:** platforms like Upwork or Fiverr take a 10–20% fee.
- **Sales jobs:** salary + commission.
- **Billing software:** computing invoices with discounts and VAT.

## 3. What it means
All of these are percent changes, so use multipliers:
- Discount $d\%$: price × $(1 - d/100)$.
- Tax, tip or markup $t\%$: amount × $(1 + t/100)$.
- Commission $c\%$: earnings $= c/100 \times$ sales.

**Markup vs margin:** both compare profit with something different.
- **Markup** = profit ÷ **cost**.
- **Margin** = profit ÷ **selling price**.
A product costing 80 sold at 100 has a 25% markup but a 20% margin.

**Order of discount and tax doesn't matter** for the final price, because multiplication is commutative: $P \times 0.8 \times 1.15 = P \times 1.15 \times 0.8$.

## 4. Formula & worked example
$$
\text{markup} = \frac{\text{price} - \text{cost}}{\text{cost}} \qquad \text{margin} = \frac{\text{price} - \text{cost}}{\text{price}}
$$

**Example 1.** A 3500 taka jacket, 30% off, then 7.5% VAT.
- $3500 \times 0.70 \times 1.075 = 2633.75$ taka

**Example 2 (tip).** A 1840 taka bill with a 10% service charge and 5% VAT on the bill.
- Both on the original bill: $1840 \times 1.15 = 2116$ taka (if VAT is charged on bill + service: $1840 \times 1.10 \times 1.05 = 2125.20$; read the rules!)

**Example 3 (markup).** A shop buys a kettle for 1200 taka and marks it up 35%. Selling price and margin?
- Price $= 1200 \times 1.35 = 1620$; margin $= \frac{420}{1620} \approx 25.9\%$

**Example 4 (commission).** A salesperson earns 15 000 taka basic plus 4% commission on sales of 380 000 taka.
- $15\,000 + 0.04 \times 380\,000 = 15\,000 + 15\,200 = 30\,200$ taka

**Example 5 (platform fee).** A freelancer bills USD 500; the platform takes 10%. Then a 2% transfer fee applies. Amount received?
- $500 \times 0.90 \times 0.98 = 441$ USD

## 5. Common mistakes
- Adding percentages from different bases: 30% off and then 20% off is **not** 50% off ($0.7 \times 0.8 = 0.56$, i.e. 44% off).
- Confusing markup with margin.
- Applying tax to the original price when the rule says after the discount (or vice versa when it matters, e.g. fixed fees).

## 6. Practice
Q: 25% off 2400, then 15% VAT. Final price?
A: $2400 \times 0.75 \times 1.15 = 2070$.

Q: Cost 500, price 650. Markup and margin?
A: Markup 30%; margin $\approx 23.1\%$.

Q: 6% commission on 250 000. Earnings?
A: $15\,000$.

Q: Is "40% off then 10% off" the same as 50% off?
A: No: $0.6 \times 0.9 = 0.54$, i.e. 46% off.

Q: A meal costs 960 taka; tip 15%. Total?
A: $1104$ taka.

@@ 3 | TOPIC | Simple and compound interest | I = Prt, total amount, comparing with compound interest | MUST
# Simple and compound interest

## 1. What it covers
- **Simple interest:** $I = Prt$.
- The total amount $A = P + I$.
- Finding the rate, time or principal.
- A first look at **compound interest** $A = P(1 + r)^t$ (covered fully in the Algebra roadmap).

## 2. Where it is used
- **Banking:** savings accounts, fixed deposits (FDR), savings certificates (Sanchayapatra).
- **Loans:** personal and car loans, credit cards.
- **Investing:** comparing returns.
- **Business finance:** working out the cost of borrowing.

## 3. What it means
**Interest** is the price of using money. The **principal** $P$ is the amount borrowed or saved, $r$ is the yearly rate (as a decimal) and $t$ is the time in years.

**Simple interest:** interest is charged on the **original principal only**, the same amount every year.

**Compound interest:** each year's interest is added to the balance, and the next year's interest is calculated on the new, larger balance: "interest on interest". Over long periods this makes a huge difference. Compounding is like a snowball rolling downhill: it picks up more snow because it's already bigger.

## 4. Formula & worked example
$$
I = Prt \qquad A = P + I = P(1 + rt) \qquad \text{compound: } A = P(1 + r)^t
$$

**Example 1.** 50 000 taka at 8% simple interest for 3 years.
- $I = 50\,000 \times 0.08 \times 3 = 12\,000$; $A = 62\,000$ taka

**Example 2 (months).** 20 000 at 9% for 8 months.
- $t = \frac{8}{12}$ → $I = 20\,000 \times 0.09 \times \frac{2}{3} = 1200$

**Example 3 (find the rate).** 15 000 grows to 18 600 in 4 years (simple). Rate?
- $I = 3600 = 15\,000 \times r \times 4$ → $r = 0.06 = 6\%$

**Example 4 (compare).** 100 000 at 10% for 10 years.
- Simple: $A = 100\,000(1 + 0.1 \times 10) = 200\,000$
- Compound: $A = 100\,000 \times 1.1^{10} \approx 259\,374$

**Example 5 (code).** Compound growth year by year.
```python
balance = 100_000
for year in range(1, 11):
    balance *= 1.10          # add 10% of the current balance
print(round(balance))        # 259374
```
Each loop multiplies the **current** balance by 1.10, which is exactly what compounding means. Ten loops give $100\,000 \times 1.1^{10}$.

## 5. Common mistakes
- Using 8 instead of 0.08 for $r$.
- Using months directly as $t$; convert to years.
- Forgetting to add the principal when asked for the total amount.

## 6. Practice
Q: Simple interest on 40 000 at 7.5% for 2 years?
A: $6000$.

Q: Total after 5 years: 10 000 at 6% simple?
A: $13\,000$.

Q: How long for 25 000 at 8% simple to earn 6000?
A: $t = \frac{6000}{2000} = 3$ years.

Q: 10 000 at 5% compounded yearly for 2 years?
A: $10\,000 \times 1.05^2 = 11\,025$.

Q: Why does compound interest beat simple interest over time?
A: Interest is earned on previous interest, so the base keeps growing.

@@ 3 | TOPIC | Percent error | Comparing measured and true values, relative error | SHOULD
# Percent error

## 1. What it covers
- **Absolute error** $\lvert \text{measured} - \text{true}\rvert$.
- **Percent (relative) error**: the error compared with the true value.
- Why relative error is more meaningful than absolute error.

## 2. Where it is used
- **Science labs:** comparing experimental results with accepted values.
- **Machine learning:** MAPE (mean absolute percentage error) for forecasting.
- **Engineering tolerances:** a resistor rated 100 Ω ± 5%.
- **Estimation:** how good was the project time estimate?

## 3. What it means
An error of 1 cm is huge when measuring a 5 cm screw, but tiny when measuring a 100 m field. Percent error puts the error **in proportion** to the true size.

**Steps:** 1. find the difference, 2. take its absolute value, 3. divide by the **true** value, 4. ×100.

## 4. Formula & worked example
$$
\text{percent error} = \frac{\lvert \text{measured} - \text{true}\rvert}{\lvert\text{true}\rvert} \times 100\%
$$

**Example 1.** You measured $g = 9.5$ m/s²; the true value is 9.81.
- $\frac{0.31}{9.81} \approx 3.2\%$

**Example 2.** You estimated a task at 6 hours; it took 8.
- $\frac{\lvert 6 - 8\rvert}{8} = 25\%$

**Example 3 (tolerance).** A 220 Ω resistor with ±5% tolerance. Range of possible values?
- $5\%$ of $220 = 11$ → $209$ to $231$ Ω

**Example 4.** Two errors: 2 g on a 50 g sample vs 5 g on a 1 kg sample. Which is worse?
- $4\%$ vs $0.5\%$ → the first is worse, despite the smaller absolute error.

## 5. Common mistakes
- Dividing by the measured value instead of the true value.
- Forgetting the absolute value (percent error is non-negative).
- Leaving it as a decimal when a percent is asked for.

## 6. Practice
Q: Measured 48 cm, true 50 cm. Percent error?
A: $4\%$.

Q: Forecast 1200 sales, actual 1000. Percent error?
A: $20\%$.

Q: A 1 kg bag weighs 985 g. Percent error?
A: $1.5\%$.

Q: Which is worse: 0.5 s error on a 10 s race, or 3 s on a 400 s race?
A: $5\%$ vs $0.75\%$ → the 10 s race.

Q: Range of a 1000 Ω resistor with ±2% tolerance?
A: $980$ to $1020$ Ω.

@@ 3 | TOPIC | Successive percent changes | Chaining multipliers, why +50% then −50% isn't zero, average growth | MUST
# Successive percent changes

## 1. What it covers
- Applying several percent changes one after another.
- Why increases and decreases don't cancel.
- Finding the overall percent change of a chain.
- A first look at average growth rate.

## 2. Where it is used
- **Investing:** a stock falls 20% and then rises 20%: you've still lost money.
- **Pricing:** stacked discounts ("extra 10% off sale prices").
- **Business reporting:** quarterly growth that compounds into yearly growth.
- **Inflation:** prices rising several percent a year for many years.

## 3. What it means
Each change applies to the **new** value, not the original. So multiply the multipliers:
- +50% then −50%: $1.5 \times 0.5 = 0.75$ → overall **−25%**.
- −20% then +20%: $0.8 \times 1.2 = 0.96$ → overall **−4%**.

The decrease is applied to a bigger number (or the increase to a smaller one), so the two don't cancel. If you lose half your money, you need a **100%** gain to get back.

## 4. Formula & worked example
$$
\text{overall multiplier} = m_1 \times m_2 \times \cdots \times m_n \qquad \text{overall change} = (\text{multiplier} - 1) \times 100\%
$$

**Example 1.** Price up 10%, then up 10% again.
- $1.1 \times 1.1 = 1.21$ → +21% (not 20%)

**Example 2.** A 25% discount, then an extra 10% off.
- $0.75 \times 0.9 = 0.675$ → 32.5% off overall

**Example 3.** An investment of 80 000: −30% in year 1, +40% in year 2.
- $80\,000 \times 0.7 \times 1.4 = 78\,400$ → still below the start (−2%)

**Example 4 (needed recovery).** After a 40% drop, what rise gets you back?
- $0.6 \times m = 1$ → $m = 1.\overline{6}$ → a 66.7% rise

**Example 5 (yearly from monthly).** 2% growth per month for 12 months.
- $1.02^{12} \approx 1.268$ → about 26.8% per year (not 24%)

## 5. Common mistakes
- Adding the percentages: +10% and +10% ≠ +20%.
- Assuming −x% then +x% returns to the start.
- Applying all changes to the original value instead of the running value.

## 6. Practice
Q: +20% then −20%. Overall?
A: $1.2 \times 0.8 = 0.96$ → −4%.

Q: Three discounts of 10% each. Overall discount?
A: $0.9^3 = 0.729$ → 27.1% off.

Q: A 50% loss needs what gain to recover?
A: 100%.

Q: 1000 grows 5% then 8%. Final value?
A: $1000 \times 1.05 \times 1.08 = 1134$.

Q: 3% growth each quarter. Yearly growth?
A: $1.03^4 \approx 1.1255$ → about 12.6%.

@@ 3 | TOPIC | Practice & review: Percentages | Mixed problem set with worked answers | MUST
# Practice & review: Percentages

**How to use this:** 45 minutes with a calculator. Write the multiplier for every change before calculating.

## Problems
1. Write $\frac{9}{40}$ as a percent and $0.35\%$ as a decimal.
2. 18% of 1250? And 75 is what percent of 300?
3. 42 is 35% of what number?
4. A laptop's price falls from 85 000 to 72 250 taka. Percent change?
5. After 15% VAT a bill is 2760 taka. Bill before VAT?
6. A shop buys at 640 and sells at 880. Markup and margin?
7. 120 000 at 9% simple interest for 3 years 6 months. Interest?
8. 50 000 at 8% compound interest for 3 years. Final amount?
9. A share price rises 25%, then falls 20%, then rises 10%. Overall change?
10. A measurement of 3.2 m should have been 3.25 m. Percent error?

## Answers
1. $22.5\%$; $0.0035$.
2. $225$; $25\%$.
3. $42 \div 0.35 = 120$.
4. $\frac{72\,250 - 85\,000}{85\,000} = -15\%$.
5. $2760 \div 1.15 = 2400$ taka.
6. Markup $= \frac{240}{640} = 37.5\%$; margin $= \frac{240}{880} \approx 27.3\%$.
7. $120\,000 \times 0.09 \times 3.5 = 37\,800$.
8. $50\,000 \times 1.08^3 = 62\,985.60$.
9. $1.25 \times 0.8 \times 1.1 = 1.1$ → +10%.
10. $\frac{0.05}{3.25} \approx 1.54\%$.

## Flashcards
Q: Multiplier for a $p\%$ increase? Decrease?
A: $1 + \frac{p}{100}$; $1 - \frac{p}{100}$.

Q: Percent change formula?
A: $\frac{\text{new} - \text{original}}{\text{original}} \times 100\%$.

Q: How do you find the original price after a known percent change?
A: Divide the new price by the multiplier.

Q: Simple interest formula?
A: $I = Prt$.
