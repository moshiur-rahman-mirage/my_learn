@@ 1 | MODULE | Phase 3 — Ratios, Rates and Proportions (Core) | Ratios, ratio tables, sharing, unit rates, speed, proportional relationships, scale, unit conversion | MUST
# Phase 3: Ratios, rates and proportions

Ratios compare quantities. Rates compare quantities with **different units** (km per hour, taka per kg). Proportions say two ratios are equal. Together they are the most used maths in daily life and the bridge to linear functions in algebra: a proportional relationship $y = kx$ is the simplest straight line.

@@ 2 | MODULE | Ratios and rates | Ratio notation, equivalent ratios, tape diagrams, unit rates, speed–distance–time | MUST
Learn to see every "for every", "per" and "out of" as a ratio.

@@ 3 | TOPIC | Ratios: concept and notation | Part-to-part vs part-to-whole, a:b, a to b, a/b, simplifying ratios | MUST
# Ratios: concept and notation

## 1. What it covers
- What a ratio is and the three ways to write one: $a : b$, "a to b", $\frac{a}{b}$.
- **Part-to-part** vs **part-to-whole** ratios.
- Simplifying ratios, including ones with fractions, decimals or different units.

## 2. Where it is used
- **Screens and images:** aspect ratio $16 : 9$; resizing an image without distortion keeps the ratio.
- **Cooking and chemistry:** rice to water $1 : 2$; mixing concrete $1 : 2 : 3$ (cement : sand : gravel).
- **Finance:** debt-to-income ratio, price-to-earnings ratio.
- **Data science:** class imbalance (e.g. 1 fraud : 99 normal), train/test split $80 : 20$.

## 3. What it means
A ratio compares two (or more) quantities by **division**: how many of one for each of the other.

In a class of 12 boys and 18 girls:
- **Part-to-part:** boys : girls $= 12 : 18 = 2 : 3$ ("2 boys for every 3 girls").
- **Part-to-whole:** boys : total $= 12 : 30 = 2 : 5$, so $\frac{2}{5}$ of the class are boys.

A ratio is simplified like a fraction: divide every part by the GCF. Both quantities must be in the **same unit** first.

## 4. Formula & worked example
$$
a : b = \frac{a}{b} = (ka) : (kb) \quad (k \ne 0)
$$

**Example 1.** Simplify $45 : 60$.
- GCF 15 → $3 : 4$

**Example 2.** Simplify $1.2 : 0.8$.
- ×10: $12 : 8 = 3 : 2$

**Example 3.** Simplify $\frac{2}{3} : \frac{1}{4}$.
- Multiply by 12 (LCM of 3 and 4): $8 : 3$

**Example 4 (units).** 50 cm to 2 m.
- Same unit: $50 : 200 = 1 : 4$

**Example 5.** A bag has red and blue marbles in the ratio $3 : 5$. What fraction is red?
- Total parts $= 8$ → $\frac{3}{8}$

## 5. Common mistakes
- Mixing part-to-part and part-to-whole: $3 : 5$ red to blue does **not** mean $\frac{3}{5}$ are red.
- Simplifying with different units ($50$ cm : $2$ m is not $25 : 1$).
- Reversing the order: "boys to girls" must be written boys first.

## 6. Practice
Q: Simplify $24 : 36 : 60$.
A: $2 : 3 : 5$.

Q: 15 cats and 25 dogs. Ratio of cats to all animals?
A: $15 : 40 = 3 : 8$.

Q: Simplify $0.75 : 2.25$.
A: $1 : 3$.

Q: Simplify 45 minutes : 2 hours.
A: $45 : 120 = 3 : 8$.

Q: A 1920 × 1080 screen. Simplest aspect ratio?
A: $16 : 9$.

@@ 3 | TOPIC | Equivalent ratios and ratio tables | Scaling ratios up and down, ratio tables, ratios on a graph | MUST
# Equivalent ratios and ratio tables

## 1. What it covers
- Making **equivalent ratios** by multiplying or dividing both parts.
- Testing whether two ratios are equivalent.
- **Ratio tables** to find missing values.
- Why equivalent ratio pairs lie on a straight line through the origin.

## 2. Where it is used
- **Recipe scaling:** a recipe for 4 people scaled to 10.
- **Responsive images:** keep width : height fixed when resizing (CSS `aspect-ratio`).
- **Maps:** a scale such as 1 : 50 000.
- **Currency exchange tables** and price lists.

## 3. What it means
Equivalent ratios describe the **same relationship** at different sizes. A drink mixed 2 cups juice : 3 cups water tastes the same as 4 : 6 or 10 : 15.

A **ratio table** lists equivalent ratios in columns. You can move along it by multiplying, dividing or **adding columns** (adding two columns of the same ratio gives another equivalent ratio).

**Graph:** plot each pair $(x, y)$. All equivalent pairs lie on one straight line through $(0, 0)$, because $y$ is always the same multiple of $x$.

## 4. Formula & worked example
$$
\frac{a}{b} = \frac{c}{d} \iff ad = bc
$$

**Example 1 (ratio table).** A recipe uses 3 eggs per 2 cups of flour. How many eggs for 7 cups?
| flour | 2 | 1 | 7 |
|---|---|---|---|
| eggs | 3 | 1.5 | 10.5 |
- Divide by 2 to get 1 cup, then multiply by 7 → $10.5$ eggs.

**Example 2.** Are $6 : 15$ and $10 : 25$ equivalent?
- Simplify both: $2 : 5$ and $2 : 5$ → yes. (Or cross-multiply: $6 \times 25 = 150 = 15 \times 10$ ✓)

**Example 3.** Resize a 1200 × 800 image to width 450 keeping the ratio.
- Ratio $3 : 2$ → height $= 450 \times \frac{2}{3} = 300$

**Example 4 (graph).** Pairs from the ratio $1 : 4$: $(1, 4), (2, 8), (3, 12)$. They lie on the line $y = 4x$.

## 5. Common mistakes
- **Adding** the same number to both parts: $2 : 3 \ne 4 : 5$. Multiply instead.
- Scaling only one part.
- Assuming a line that doesn't pass through $(0, 0)$ shows equivalent ratios.

## 6. Practice
Q: Fill in: $5 : 8 = 20 : ?$
A: $32$.

Q: Are $4 : 7$ and $12 : 20$ equivalent?
A: No ($4 \times 20 = 80 \ne 7 \times 12 = 84$).

Q: 3 notebooks cost 240 taka. Cost of 8?
A: $80$ each → $640$ taka.

Q: Resize 1600 × 900 to height 360.
A: Width $= 1600 \times \frac{360}{900} = 640$.

Q: What shape do equivalent ratio pairs make on a graph?
A: A straight line through the origin.

@@ 3 | TOPIC | Tape diagrams and sharing in a ratio | Dividing a total in a given ratio, part-whole and difference problems | MUST
# Tape diagrams and sharing in a ratio

## 1. What it covers
- Drawing a **tape diagram** (bar model) for ratio problems.
- **Sharing** a total in a given ratio.
- Problems given the **difference** between parts, or one part.

## 2. Where it is used
- **Business:** splitting profit between partners who invested 3 lakh and 5 lakh (ratio $3 : 5$).
- **Inheritance and budgets:** dividing money by rules.
- **Mixing:** how much of each ingredient for a 10 kg batch with ratio $2 : 3$.
- **Data:** splitting a dataset 70 : 15 : 15 into train/validation/test sets.

## 3. What it means
A tape diagram draws each part of the ratio as equal-sized boxes. For $3 : 5$, draw 3 boxes for A and 5 boxes for B: 8 equal boxes in total.

**Method:**
1. Total number of parts (boxes) $=$ sum of the ratio numbers.
2. Value of one part $=$ known amount ÷ number of parts it covers.
3. Multiply to get each share.

```text
A: [  ][  ][  ]
B: [  ][  ][  ][  ][  ]      8 equal boxes in total
```

## 4. Formula & worked example
$$
\text{share of A} = \frac{a}{a + b} \times \text{total}
$$

**Example 1.** Share 24 000 taka in the ratio $3 : 5$.
- 8 parts → one part $= 3000$ → A $= 9000$, B $= 15\,000$

**Example 2 (difference).** Ali and Bina share sweets in the ratio $4 : 7$. Bina gets 15 more than Ali. How many sweets in total?
- Difference $= 7 - 4 = 3$ parts $= 15$ → one part $= 5$ → total $= 11 \times 5 = 55$

**Example 3 (one part known).** Cement : sand $= 1 : 3$. If you have 12 bags of sand, how much cement?
- 3 parts $= 12$ → 1 part $= 4$ bags of cement.

**Example 4 (three parts).** Split a dataset of 20 000 rows as $70 : 15 : 15$.
- 100 parts → 1 part $= 200$ → $14\,000$ train, $3000$ validation, $3000$ test.

## 5. Common mistakes
- Dividing the total by one ratio number instead of the **sum**.
- In difference problems, dividing the difference by the total parts instead of the difference in parts.
- Forgetting to answer the actual question (total vs one share).

## 6. Practice
Q: Share 450 in the ratio $2 : 7$.
A: $100$ and $350$.

Q: Boys : girls $= 5 : 4$, and there are 20 girls. How many students?
A: One part $= 5$; total $= 9 \times 5 = 45$.

Q: Ratio $3 : 8$, difference 35. Find both parts.
A: 5 parts $= 35$ → $21$ and $56$.

Q: Split 1.2 kg in the ratio $1 : 2 : 3$.
A: $0.2$, $0.4$, $0.6$ kg.

Q: Partners invest 2 lakh and 3 lakh. Profit is 1.5 lakh. Each share?
A: $0.6$ lakh and $0.9$ lakh.

@@ 3 | TOPIC | Unit rates and comparing rates | Rates per one unit, unit prices, best-buy comparisons | MUST
# Unit rates and comparing rates

## 1. What it covers
- What a **rate** is (a ratio of different units).
- The **unit rate**: the amount per **one** unit.
- Comparing deals with unit prices.

## 2. Where it is used
- **Shopping:** taka per kg, taka per litre: is the big pack really cheaper?
- **Mobile data:** taka per GB across plans.
- **Cloud computing:** cost per hour or per GB-month (AWS pricing is all unit rates).
- **Performance:** requests per second, frames per second, words per minute.
- **Fuel efficiency:** km per litre.

## 3. What it means
A **rate** compares two quantities with **different units**: 300 km in 4 hours. The **unit rate** answers "how much for **one**?": $\frac{300}{4} = 75$ km per **1** hour.

To compare offers, convert each to the same unit rate. A unit rate can be flipped: 50 taka per kg also means $\frac{1}{50}$ kg per taka. Choose the version that answers the question.

## 4. Formula & worked example
$$
\text{unit rate} = \frac{\text{quantity A}}{\text{quantity B}} \text{ (per 1 unit of B)}
$$

**Example 1.** 12 eggs cost 150 taka. Unit price?
- $150 \div 12 = 12.5$ taka per egg

**Example 2 (best buy).** 2 kg rice for 170 taka, or 5 kg for 410 taka?
- $85$ taka/kg vs $82$ taka/kg → the 5 kg bag is cheaper per kg.

**Example 3.** Plan A: 30 GB for 499 taka. Plan B: 50 GB for 799 taka.
- A: $\approx 16.6$ taka/GB; B: $\approx 16.0$ taka/GB → B is slightly cheaper per GB (but only if you use the data).

**Example 4.** A server handles 9000 requests in 2.5 minutes. Requests per second?
- $2.5$ min $= 150$ s → $9000 \div 150 = 60$ requests/s

## 5. Common mistakes
- Dividing the wrong way (kg per taka instead of taka per kg) and then picking the "biggest" as the best deal.
- Mixing units (minutes vs seconds) before dividing.
- Ignoring whether you actually need the larger quantity.

## 6. Practice
Q: 420 km on 35 litres. km per litre?
A: $12$ km/L.

Q: 6 pens for 90 taka or 10 pens for 140 taka. Better deal?
A: $15$ vs $14$ taka each → 10 pens.

Q: Typing 1350 words in 30 minutes. Words per minute?
A: $45$.

Q: A cloud VM costs USD 0.096 per hour. Cost for a 30-day month?
A: $0.096 \times 720 \approx 69.12$ USD.

Q: 2.4 kg of apples cost 360 taka. Price per kg?
A: $150$ taka.

@@ 3 | TOPIC | Speed, distance and time | d = st, average speed, unit changes, meeting and overtaking problems | MUST
# Speed, distance and time

## 1. What it covers
- The formula $d = s \times t$ and its rearrangements.
- **Average speed** for a journey with several parts.
- Converting between km/h and m/s.
- Simple meeting and overtaking problems.

## 2. Where it is used
- **Travel planning:** arrival times, GPS estimates.
- **Physics:** the starting point for motion and velocity.
- **Networking:** download time = file size ÷ bandwidth (a 2 GB file at 100 Mbit/s).
- **Sports:** pace (minutes per km) for runners.
- **Logistics:** delivery route timing.

## 3. What it means
**Speed** is a rate: distance per unit of time. If you go 60 km every hour, then in 3 hours you go 180 km.

**Average speed** = **total** distance ÷ **total** time. It is **not** the average of the speeds, unless you spend equal **times** at each speed.

**Meeting problems:** two objects moving **towards** each other close the gap at the **sum** of their speeds. Moving in the **same** direction, the gap changes at the **difference** of their speeds.

## 4. Formula & worked example
$$
d = st \qquad s = \frac{d}{t} \qquad t = \frac{d}{s} \qquad 1 \text{ m/s} = 3.6 \text{ km/h}
$$

**Example 1.** A bus travels 225 km at 50 km/h. How long?
- $t = \frac{225}{50} = 4.5$ h $= 4$ h 30 min

**Example 2 (average speed).** 120 km at 60 km/h, then 120 km at 40 km/h. Average speed?
- Times: 2 h and 3 h → total 240 km in 5 h → $48$ km/h (**not** 50).

**Example 3 (units).** 90 km/h in m/s?
- $90 \div 3.6 = 25$ m/s

**Example 4 (meeting).** Two trains 300 km apart head towards each other at 70 and 80 km/h. When do they meet?
- Closing speed $150$ km/h → $t = \frac{300}{150} = 2$ h

**Example 5 (download).** 2 GB file at 100 Mbit/s.
- 2 GB $= 16\,000$ Mbit (using 1 GB = 8000 Mbit) → $t = 160$ s $\approx 2.7$ min

## 5. Common mistakes
- Averaging speeds directly when the distances (not the times) are equal.
- Mixing minutes and hours: 20 minutes $= \frac{1}{3}$ h, **not** 0.2 h.
- Confusing bits and bytes in data rates (1 byte = 8 bits).

## 6. Practice
Q: How far in 2 h 15 min at 80 km/h?
A: $80 \times 2.25 = 180$ km.

Q: 15 m/s in km/h?
A: $54$ km/h.

Q: Go 30 km at 60 km/h, return at 30 km/h. Average speed?
A: 60 km in 1.5 h → $40$ km/h.

Q: A runner covers 10 km in 50 minutes. Pace and speed?
A: 5 min/km; 12 km/h.

Q: A car at 60 km/h leaves 1 h before a car at 90 km/h on the same road. When does the second catch up?
A: Gap 60 km, closing at 30 km/h → 2 h after it starts.

@@ 2 | MODULE | Proportional relationships and conversion | y = kx, solving proportions, scale drawings, unit conversion | MUST
Proportional relationships are where arithmetic turns into algebra.

@@ 3 | TOPIC | Proportional relationships and the constant of proportionality | Recognising proportionality in tables, graphs and equations; y = kx | MUST
# Proportional relationships and the constant of proportionality

## 1. What it covers
- When two quantities are **proportional**.
- The **constant of proportionality** $k = \frac{y}{x}$.
- Recognising proportionality in tables, graphs and equations, and writing $y = kx$.
- What the points $(0, 0)$ and $(1, k)$ mean.

## 2. Where it is used
- **Pricing:** cost = price per item × number of items.
- **Physics:** Hooke's law (spring stretch ∝ force), Ohm's law ($V = IR$).
- **Pay:** wages = hourly rate × hours.
- **Machine learning:** the simplest model is a straight line through the origin; linear regression generalises it.
- **Cooking and currency conversion.**

## 3. What it means
$y$ is proportional to $x$ if $y$ is always the **same multiple** of $x$: $y = kx$. Double $x$ and $y$ doubles; halve $x$ and $y$ halves.

**How to recognise it:**
- **Table:** $\frac{y}{x}$ is the same for every row.
- **Graph:** a straight line **through the origin**.
- **Equation:** $y = kx$ (no "+ something").

$k$ is the unit rate: the value of $y$ when $x = 1$. So the graph passes through $(1, k)$. It passes through $(0, 0)$ because zero items cost zero.

**Not proportional:** a taxi fare with a 50 taka starting charge plus 20 taka per km ($y = 20x + 50$) is linear but **not** proportional: 0 km still costs 50.

## 4. Formula & worked example
$$
y = kx \qquad k = \frac{y}{x}
$$

**Example 1.** Is this table proportional?
| x | 2 | 5 | 8 |
|---|---|---|---|
| y | 7 | 17.5 | 28 |
- $\frac{7}{2} = 3.5$, $\frac{17.5}{5} = 3.5$, $\frac{28}{8} = 3.5$ → yes, $y = 3.5x$.

**Example 2.** Is this proportional? $(1, 4), (2, 7), (3, 10)$
- $\frac{4}{1} = 4$, $\frac{7}{2} = 3.5$ → no (it's $y = 3x + 1$).

**Example 3.** A worker earns 1350 taka for 9 hours. Write the equation and find the pay for 14 hours.
- $k = \frac{1350}{9} = 150$ → $y = 150x$ → $y = 150 \times 14 = 2100$ taka

**Example 4.** Ohm's law: 12 V across a resistor gives 0.5 A. What current at 30 V?
- $I = \frac{V}{R}$ with $R = \frac{12}{0.5} = 24$ Ω → $I = \frac{30}{24} = 1.25$ A

## 5. Common mistakes
- Calling every straight line proportional. It must pass through the origin.
- Computing $\frac{x}{y}$ instead of $\frac{y}{x}$ for $k$ (both are constant, but $y = kx$ needs $\frac{y}{x}$).
- Checking only one row of a table.

## 6. Practice
Q: $y$ is proportional to $x$, and $y = 18$ when $x = 4$. Find $k$ and $y$ when $x = 10$.
A: $k = 4.5$; $y = 45$.

Q: Is $y = 5x - 2$ proportional?
A: No, it doesn't pass through the origin.

Q: What does the point $(1, k)$ on a proportional graph tell you?
A: The unit rate: $y$ for one unit of $x$.

Q: 5 kg of potatoes cost 175 taka. Equation?
A: $y = 35x$.

Q: A spring stretches 3 cm under 6 N. Stretch under 10 N?
A: $k = 0.5$ cm/N → 5 cm.

@@ 3 | TOPIC | Solving proportions | Cross-multiplication, the unit-rate method, inverse proportion | MUST
# Solving proportions

## 1. What it covers
- Solving $\frac{a}{b} = \frac{x}{d}$ by cross-multiplying.
- The unit-rate method as an alternative.
- **Inverse proportion**: when one quantity goes up, the other goes down.

## 2. Where it is used
- **Medicine:** dosage by body weight (5 mg per kg).
- **Map reading** and scale models.
- **Project planning:** workers and time (more workers → less time: inverse).
- **Estimation:** if 50 out of 200 sampled users clicked, how many of 10 000 will?

## 3. What it means
A **proportion** is an equation saying two ratios are equal. Cross-multiplying works because multiplying both sides by both denominators clears the fractions:
$$
\frac{a}{b} = \frac{c}{d} \Rightarrow ad = bc
$$

**Inverse proportion:** $xy = k$ (the product is constant). If 4 workers take 6 days, then $4 \times 6 = 24$ worker-days of work; 8 workers take $24 \div 8 = 3$ days. Think of sharing a pizza: more people means a smaller slice each.

**Check which kind** before solving: does doubling one quantity double the other (direct) or halve it (inverse)?

## 4. Formula & worked example
$$
\text{direct: } \frac{y_1}{x_1} = \frac{y_2}{x_2} \qquad \text{inverse: } x_1y_1 = x_2y_2
$$

**Example 1.** Solve $\frac{4}{9} = \frac{x}{27}$.
- $9x = 108$ → $x = 12$

**Example 2.** Solve $\frac{3}{x + 2} = \frac{5}{10}$.
- $5(x + 2) = 30$ → $x + 2 = 6$ → $x = 4$

**Example 3 (dosage).** 5 mg per kg. Dose for a 38 kg child?
- $\frac{5}{1} = \frac{x}{38}$ → $x = 190$ mg

**Example 4 (inverse).** 6 taps fill a tank in 40 minutes. How long with 10 taps?
- $6 \times 40 = 240$ → $\frac{240}{10} = 24$ min

**Example 5 (sampling).** 18 out of 240 sampled phones were faulty. Estimate faulty phones in 5000.
- $\frac{18}{240} = \frac{x}{5000}$ → $x = 375$

## 5. Common mistakes
- Using direct proportion for an inverse situation (more workers → more days ✗).
- Setting up the proportion with mismatched units or order ($\frac{\text{kg}}{\text{taka}} = \frac{\text{taka}}{\text{kg}}$ ✗).
- Cross-multiplying across an addition: $\frac{a}{b} + c$ is not a proportion.

## 6. Practice
Q: Solve $\frac{7}{x} = \frac{21}{36}$.
A: $21x = 252$ → $x = 12$.

Q: 3 cm on a map is 12 km. What is 7.5 cm?
A: $30$ km.

Q: 8 workers build a wall in 15 days. How long for 12 workers?
A: $120 \div 12 = 10$ days.

Q: Direct or inverse: speed and travel time for a fixed distance?
A: Inverse.

Q: Solve $\frac{x - 1}{4} = \frac{9}{12}$.
A: $12(x - 1) = 36$ → $x = 4$.

@@ 3 | TOPIC | Scale drawings, maps and scale factors | Scale factors, map scales, enlarging and reducing, how area and volume scale | SHOULD
# Scale drawings, maps and scale factors

## 1. What it covers
- **Scale factors** for enlarging and reducing shapes.
- Map and blueprint scales such as $1 : 50\,000$.
- Why **area** scales by $k^2$ and **volume** by $k^3$.

## 2. Where it is used
- **Maps and GPS:** converting map distance to real distance.
- **Architecture:** floor plans at $1 : 100$.
- **Graphic design and games:** zooming an image by 200% makes its area 4 times bigger (and needs 4 times the pixels).
- **3D printing and models:** a model car at $1 : 18$.
- **Biology:** why big animals need thicker legs (weight grows by $k^3$ but bone strength only by $k^2$).

## 3. What it means
A **scale factor** $k$ multiplies every length. $k > 1$ enlarges, $0 < k < 1$ reduces. Angles don't change.

A map scale $1 : 50\,000$ means 1 cm on the map is 50 000 cm (= 500 m) in real life.

**Area and volume:** if every length is multiplied by $k$, a rectangle's area $\text{length} \times \text{width}$ becomes $k \cdot \text{length} \times k \cdot \text{width} = k^2 \times$ area. A box's volume gets three factors: $k^3$. Doubling a photo's size (k = 2) needs 4 times the paper.

## 4. Formula & worked example
$$
\text{new length} = k \times \text{old} \qquad \text{new area} = k^2 \times \text{old} \qquad \text{new volume} = k^3 \times \text{old}
$$

**Example 1.** Map scale $1 : 25\,000$; distance on the map 6.4 cm. Real distance?
- $6.4 \times 25\,000 = 160\,000$ cm $= 1.6$ km

**Example 2.** A room is 4.5 m long. How long on a $1 : 50$ plan?
- $450$ cm $\div 50 = 9$ cm

**Example 3.** A triangle with sides 3, 4, 5 is enlarged by a factor of 2.5. New sides?
- $7.5$, $10$, $12.5$

**Example 4.** A photo is enlarged from 10 × 15 cm to 30 × 45 cm. How many times more ink (area)?
- $k = 3$ → area ×$9$

**Example 5.** A model ship is $1 : 100$. The model holds 0.5 litres. How much would the real ship hold (same shape)?
- Volume ×$100^3 = 1\,000\,000$ → $500\,000$ litres

## 5. Common mistakes
- Scaling area by $k$ instead of $k^2$.
- Unit mix-ups in map scales: convert cm to m or km at the end.
- Using the scale backwards (multiplying when you should divide).

## 6. Practice
Q: Scale $1 : 200$; a wall is 3 cm on the plan. Real length?
A: $600$ cm $= 6$ m.

Q: A square of side 4 is scaled by $k = 1.5$. New area?
A: Side 6, area 36 (= $16 \times 2.25$).

Q: Lengths are halved. What happens to volume?
A: It becomes $\frac{1}{8}$.

Q: Real distance 12 km, map distance 24 cm. Scale?
A: $1 : 50\,000$.

Q: Zooming an image to 300% multiplies the pixel count by?
A: $9$.

@@ 3 | TOPIC | Unit conversion and dimensional analysis | Conversion factors, chaining conversions, area/volume units, units in formulas | MUST
# Unit conversion and dimensional analysis

## 1. What it covers
- Converting units by multiplying by **conversion factors** (fractions equal to 1).
- Chaining several conversions.
- Converting area ($\text{m}^2$) and volume ($\text{m}^3$) units.
- Checking formulas by their units.

## 2. Where it is used
- **Engineering and science:** NASA lost the USD 125 million Mars Climate Orbiter in 1999 because one team used pound-seconds and another newton-seconds.
- **Cooking and medicine:** mL vs teaspoons, mg vs g.
- **Computing:** bytes ↔ bits, KB ↔ MB ↔ GB, ms ↔ s, and cloud pricing per GB-hour.
- **Travel and trade:** km ↔ miles, currencies.

## 3. What it means
A conversion factor is a fraction that equals 1, like $\frac{1000 \text{ m}}{1 \text{ km}}$. Multiplying by 1 doesn't change the quantity, only its units. Choose the fraction so the unit you **don't** want **cancels** (it appears once on top and once on the bottom).

**Area and volume:** $1 \text{ m} = 100 \text{ cm}$, so $1 \text{ m}^2 = 100 \times 100 = 10\,000 \text{ cm}^2$ and $1 \text{ m}^3 = 1\,000\,000 \text{ cm}^3$. Square or cube the factor too.

**Dimensional analysis:** units behave like algebra. If a formula for speed gives $\frac{\text{km}}{\text{h}}$, it's plausible; if it gives $\text{km} \cdot \text{h}$, something is wrong.

## 4. Formula & worked example
$$
\text{quantity} \times \frac{\text{new unit}}{\text{old unit}} \quad (\text{factor} = 1)
$$
Useful: 1 km = 1000 m, 1 m = 100 cm, 1 kg = 1000 g, 1 L = 1000 mL = 1000 cm³, 1 h = 3600 s, 1 inch = 2.54 cm, 1 mile ≈ 1.609 km.

**Example 1.** 72 km/h to m/s.
$$
72 \frac{\text{km}}{\text{h}} \times \frac{1000 \text{ m}}{1 \text{ km}} \times \frac{1 \text{ h}}{3600 \text{ s}} = 20 \frac{\text{m}}{\text{s}}
$$

**Example 2.** 3.5 m² to cm².
- $3.5 \times 10\,000 = 35\,000$ cm²

**Example 3.** A tank is 2 m × 1.5 m × 0.8 m. How many litres?
- $2.4$ m³; $1 \text{ m}^3 = 1000$ L → $2400$ L

**Example 4 (chain).** A tap drips 2 mL every 5 s. Litres per day?
- $\frac{2 \text{ mL}}{5 \text{ s}} \times \frac{86\,400 \text{ s}}{1 \text{ day}} \times \frac{1 \text{ L}}{1000 \text{ mL}} = 34.56$ L/day

**Example 5 (computing).** 500 MB per day for 30 days in GB? (1 GB = 1000 MB)
- $15\,000$ MB $= 15$ GB

## 5. Common mistakes
- Converting area or volume with the length factor ($1 \text{ m}^2 = 100 \text{ cm}^2$ ✗).
- Putting the conversion fraction upside down, so the units don't cancel.
- Mixing 1000 and 1024 for KB/MB/GB without saying which (storage vendors use 1000; operating systems often 1024).

## 6. Practice
Q: 5 hours in seconds?
A: $18\,000$ s.

Q: 25 m/s in km/h?
A: $90$ km/h.

Q: 0.6 m³ in litres?
A: $600$ L.

Q: 12 inches in cm?
A: $30.48$ cm.

Q: 2 km² in m²?
A: $2\,000\,000$ m².

@@ 3 | TOPIC | Practice & review: Ratios, rates and proportions | Mixed problem set with worked answers | MUST
# Practice & review: Ratios, rates and proportions

**How to use this:** 45 minutes; a calculator is allowed for 6–9.

## Problems
1. Simplify $2.5 : 7.5 : 10$.
2. Divide 7200 taka among three people in the ratio $2 : 3 : 4$.
3. Red : blue $= 5 : 3$. There are 18 more red than blue. How many in total?
4. Which is cheaper per litre: 1.5 L for 95 taka or 2.25 L for 140 taka?
5. Drive 90 km at 60 km/h and 90 km at 90 km/h. Average speed?
6. $y$ is proportional to $x$; $y = 52$ when $x = 8$. Find $y$ when $x = 13$.
7. 5 machines make an order in 12 hours. How long would 3 machines take?
8. On a $1 : 40\,000$ map, a lake has area 3 cm². Real area in km²?
9. Convert 1.2 g/cm³ to kg/m³.

## Answers
1. $1 : 3 : 4$.
2. 9 parts → 800 each → 1600, 2400, 3200.
3. 2 parts $= 18$ → 1 part $= 9$ → total $8 \times 9 = 72$.
4. $63.3$ vs $62.2$ taka/L → the 2.25 L bottle.
5. Times 1.5 h and 1 h → 180 km in 2.5 h → $72$ km/h.
6. $k = 6.5$ → $y = 84.5$.
7. Inverse: $60$ machine-hours ÷ 3 = $20$ h.
8. 1 cm = 400 m, so 1 cm² = 160 000 m²; area $= 480\,000$ m² $= 0.48$ km².
9. $1.2 \frac{\text{g}}{\text{cm}^3} \times \frac{1 \text{ kg}}{1000 \text{ g}} \times \frac{1\,000\,000 \text{ cm}^3}{1 \text{ m}^3} = 1200$ kg/m³.

## Flashcards
Q: Part-to-part $3 : 5$. Fraction that is the first part?
A: $\frac{3}{8}$.

Q: Average speed formula?
A: Total distance ÷ total time.

Q: Graph of a proportional relationship?
A: A straight line through the origin, $y = kx$.

Q: Lengths scale by $k$. Areas and volumes scale by?
A: $k^2$ and $k^3$.
