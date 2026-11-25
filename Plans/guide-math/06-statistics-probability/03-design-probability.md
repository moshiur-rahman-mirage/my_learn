@@ 1 | MODULE | Phase 3 — Collecting Data: Sampling and Experiments (Core) | Sampling methods, bias, observational studies vs experiments, confounding, experimental design and A/B tests | SHOULD
# Phase 3: Collecting data: sampling and experiments

No amount of clever analysis can rescue badly collected data ("garbage in, garbage out"). This phase covers how to choose a sample that represents a population, how bias creeps in, and how to design experiments that can actually show cause and effect, including the A/B tests run by every major website.

@@ 2 | MODULE | Sampling and study design | Sampling methods, bias, observational studies, experiments | SHOULD
How the data were collected decides what conclusions are allowed.

@@ 3 | TOPIC | Sampling methods | Simple random, stratified, cluster, systematic and convenience samples | SHOULD
# Sampling methods

## 1. What it covers
- Why we sample instead of measuring everyone (a census).
- **Simple random sampling (SRS)**, **stratified**, **cluster**, **systematic** sampling.
- **Convenience** and **voluntary response** samples (and why they're bad).
- Random sampling in code.

## 2. Where it is used
- **Polling and market research:** surveying 1000 people to estimate national opinion.
- **Quality control:** testing a sample of items from a production line.
- **Data science:** sampling huge tables (`df.sample()`), stratified train/test splits (`train_test_split(stratify=y)`).
- **Auditing:** checking a random sample of transactions.

## 3. What it means
Measuring a whole population is often impossible or too expensive, so we measure a **sample**. To trust it, the sample must represent the population, and **randomness** is the best tool for that.
- **SRS:** every group of $n$ individuals is equally likely to be chosen (names from a hat).
- **Stratified:** split the population into groups (strata) that differ in an important way (e.g. urban/rural), then take an SRS from **each**. Guarantees every group is represented and often gives more precise estimates.
- **Cluster:** split into groups that are each like a mini-population (e.g. city blocks), randomly pick **whole clusters**. Cheaper, less precise.
- **Systematic:** pick every $k$th individual from a list after a random start.
- **Convenience** (whoever is easy to reach) and **voluntary response** (people choose to respond, like online polls) are usually **biased**.

Think of tasting soup: stir it well (randomise) and one spoonful tells you about the whole pot. A spoonful from the unstirred top doesn't.

## 4. Formula & worked example
**Example 1.** A university with 60% science and 40% arts students wants a sample of 200, guaranteeing both faculties are represented proportionally.
- Stratified: random 120 science + 80 arts students.

**Example 2.** An NGO surveys households in 500 villages. It randomly picks 25 villages and interviews every household in them → cluster sampling.

**Example 3.** A factory tests every 50th item after a random start between 1 and 50 → systematic sampling.

**Example 4.** A news website asks readers to vote on a policy → voluntary response; the results reflect only people motivated to respond.

**Example 5 (code).**
```python
import pandas as pd
from sklearn.model_selection import train_test_split
df = pd.DataFrame({"faculty": ["sci"]*600 + ["arts"]*400, "score": range(1000)})
srs = df.sample(n=200, random_state=1)                         # simple random sample
strat = df.groupby("faculty", group_keys=False).sample(frac=0.2, random_state=1)
print(srs.faculty.value_counts(), strat.faculty.value_counts())
train, test = train_test_split(df, test_size=0.2, stratify=df.faculty, random_state=1)
```
`df.sample` draws an SRS; sampling 20% within each faculty gives a stratified sample (exactly 120 and 80). `stratify=` in `train_test_split` keeps class proportions equal in the training and test sets, which matters for imbalanced data.

## 5. Common mistakes
- Confusing stratified (sample **within** every group) with cluster (sample **whole** groups).
- Thinking a large sample fixes a biased method (a biased sample of 1 million is still biased).
- Using convenience samples for conclusions about a population.

## 6. Practice
Q: Difference between stratified and cluster sampling?
A: Stratified samples individuals from every group; cluster samples entire randomly chosen groups.

Q: Why is a voluntary online poll unreliable?
A: People who choose to respond often have strong opinions (voluntary response bias).

Q: Every 10th customer through the door is surveyed. Method?
A: Systematic sampling (random if the start is random).

Q: Does a bigger sample fix sampling bias?
A: No.

Q: Which sklearn option keeps class proportions in a split?
A: `stratify=`.

@@ 3 | TOPIC | Bias in sampling and surveys | Undercoverage, nonresponse, response bias, question wording, selection and survivorship bias | SHOULD
# Bias in sampling and surveys

## 1. What it covers
- **Bias**: systematic error in one direction.
- **Undercoverage**, **nonresponse**, **response bias**, **question wording** bias.
- **Selection** and **survivorship** bias in data analysis.

## 2. Where it is used
- **Polling:** famous failures (the 1936 Literary Digest poll predicted the wrong US president from a sample of 2.4 million biased responses).
- **Machine learning:** biased training data produces biased models (e.g. face recognition trained mostly on one group).
- **Product analytics:** surveying only active users misses why others left.
- **Investing:** "average fund returns" ignore funds that closed (survivorship bias).

## 3. What it means
**Bias** means the method tends to give answers that are wrong in the same direction every time, unlike random error, which averages out.
- **Undercoverage:** some groups can't be selected (a phone survey misses people without phones).
- **Nonresponse:** chosen people don't respond, and non-responders differ from responders.
- **Response bias:** people answer untruthfully (about income, voting, health habits) or to please the interviewer.
- **Wording:** "Don't you agree that ...?" leads respondents.
- **Survivorship bias:** analysing only the "survivors". In WWII, planes returning with bullet holes in the wings led some to suggest armouring the wings; the statistician Abraham Wald pointed out that planes hit elsewhere (engines) didn't return, so those areas needed armour.

## 4. Formula & worked example
**Example 1.** A survey about internet use is done online → undercoverage of people without internet; it overestimates usage.

**Example 2.** "Do you support the reckless plan to raise taxes?" → loaded wording. Neutral: "Do you support or oppose the plan to raise taxes?"

**Example 3.** Only 15% of mailed surveys come back → nonresponse bias likely (busy or uninterested people are missing).

**Example 4 (survivorship).** "Successful startup founders dropped out of university, so dropping out leads to success" → ignores the many dropouts whose startups failed.

**Example 5 (ML).** A loan-approval model is trained only on past **approved** applicants (we never observed whether rejected ones would have repaid) → selection bias in the training data.

## 5. Common mistakes
- Treating bias like random error that a bigger sample will cancel.
- Ignoring who didn't respond.
- Drawing conclusions from only the cases that "made it".

## 6. Practice
Q: What is bias in statistics?
A: Systematic error that pushes results consistently in one direction.

Q: A landline phone poll misses younger people. Type of bias?
A: Undercoverage.

Q: People under-report how much junk food they eat. Type of bias?
A: Response bias.

Q: Give an example of survivorship bias.
A: Judging investment funds by the returns of funds still operating, ignoring closed ones.

Q: Does a larger sample remove bias?
A: No; it only reduces random variation.

@@ 3 | TOPIC | Observational studies vs experiments | Imposing treatments, confounding variables, why only experiments show causation | SHOULD
# Observational studies vs experiments

## 1. What it covers
- **Observational studies** (observe without intervening) vs **experiments** (impose a treatment).
- **Confounding variables**.
- Why randomised experiments can show **causation** and observational studies usually can't.

## 2. Where it is used
- **Medicine:** randomised controlled trials (RCTs) decide which drugs are approved.
- **Tech companies:** A/B tests decide which features ship.
- **Economics and public policy:** natural experiments and observational data (with caution).
- **Reading the news critically:** "Coffee drinkers live longer" is usually from observational data.

## 3. What it means
- **Observational study:** researchers measure what already happens (people who choose to drink coffee vs those who don't).
- **Experiment:** researchers **assign** treatments (randomly decide who drinks coffee).

**Confounding:** in observational studies, the groups may differ in other ways. Coffee drinkers might also be wealthier or exercise more; any of these could explain the difference. Such variables are **confounders**: their effects are mixed up with the treatment's.

**Random assignment** solves this: with randomisation, other factors are spread roughly evenly across groups, so the only systematic difference is the treatment. That's why well-designed experiments can establish cause and effect.

## 4. Formula & worked example
**Example 1.** A study finds people who use a fitness app lose more weight. Observational or experiment? → Observational (people chose the app). Possible confounder: motivation.

**Example 2.** Researchers randomly assign 200 volunteers to use the app or not for 3 months → experiment; a difference in weight loss can be attributed to the app.

**Example 3.** Students who sit at the front get better grades. Can you conclude sitting at the front causes better grades? No: more motivated students may choose the front seats.

**Example 4 (ethics).** You can't randomly assign people to smoke, so the smoking–cancer link was established through many careful observational studies plus biological evidence.

## 5. Common mistakes
- Concluding causation from observational data.
- Calling a study an experiment because it collected data carefully (the key is **assigning** treatments).
- Ignoring possible confounders.

## 6. Practice
Q: What makes a study an experiment?
A: Researchers deliberately assign treatments to subjects.

Q: Define a confounding variable.
A: A variable associated with both the treatment and the outcome, so their effects can't be separated.

Q: Why does random assignment allow causal conclusions?
A: It balances other variables across groups, so the treatment is the only systematic difference.

Q: "Children who eat breakfast score higher." Study type?
A: Observational.

Q: When must observational studies be used?
A: When an experiment would be unethical or impossible.

@@ 3 | TOPIC | Designing experiments and A/B tests | Control groups, randomisation, replication, blinding, placebos, blocking, matched pairs, A/B testing | SHOULD
# Designing experiments and A/B tests

## 1. What it covers
- Principles: **control**, **randomisation**, **replication**.
- Control groups, **placebos**, **blinding** (single and double).
- **Blocking** and **matched-pairs** designs.
- **A/B testing** in software products.

## 2. Where it is used
- **Tech:** A/B testing button colours, prices, recommendation algorithms, email subject lines.
- **Medicine:** double-blind randomised controlled trials.
- **Agriculture:** comparing fertilisers on blocks of similar soil.
- **Manufacturing:** testing process changes.

## 3. What it means
**Three principles:**
- **Control:** keep other variables the same and include a **control group** (no treatment or the current version) for comparison.
- **Randomisation:** assign subjects to groups by chance.
- **Replication:** use enough subjects so that chance differences average out.

**Placebo:** a fake treatment, because people often improve just from believing they're treated. **Blinding:** subjects (single-blind) and also evaluators (double-blind) don't know who got what.

**Blocking:** group similar subjects first (e.g. by age, or by mobile vs desktop users), then randomise **within** each block. This removes known variation and makes the comparison more precise.

**Matched pairs:** each subject gets both treatments in random order (or pairs of similar subjects are split).

**A/B testing:** randomly show users version A (control) or B (variant), measure a metric (conversion rate), and use a hypothesis test (Phase 6) to decide whether the difference is real.

## 4. Formula & worked example
**Example 1 (design).** Test whether a new checkout page increases purchases.
- Randomly assign each visitor to the old (A) or new (B) page; same time period; measure the purchase rate in each group; decide in advance how many visitors to collect (sample size).

**Example 2 (blocking).** Mobile and desktop users behave differently. Block by device and randomise within each block, then compare A vs B within devices.

**Example 3 (double-blind).** A headache drug trial: neither patients nor doctors know who gets the drug or the placebo; a third party holds the code.

**Example 4 (matched pairs).** Comparing two keyboards' typing speed: each person types on both, in random order; analyse the differences.

**Example 5 (code: random assignment).**
```python
import hashlib
def bucket(user_id: str, experiment: str = "checkout_v2") -> str:
    h = int(hashlib.md5(f"{experiment}:{user_id}".encode()).hexdigest(), 16)
    return "B" if h % 2 else "A"          # stable 50/50 split
print([bucket(u) for u in ["u1", "u2", "u3", "u4"]])
```
Hashing the user ID gives each user a random-looking but **consistent** assignment: the same user always sees the same version, which real A/B testing systems need.

## 5. Common mistakes
- No control group (you can't tell whether the change or something else caused the effect).
- Letting users choose their version (self-selection bias).
- Stopping an A/B test as soon as it "looks significant" (inflates false positives; see p-hacking in Phase 6).

## 6. Practice
Q: Three principles of experimental design?
A: Control, randomisation, replication.

Q: What is a double-blind experiment?
A: Neither the subjects nor those measuring outcomes know who received which treatment.

Q: Why block by device type in an A/B test?
A: To remove variation due to device and compare versions more precisely.

Q: What is a placebo for?
A: To separate the treatment's real effect from the effect of believing you're treated.

Q: Why use hashing for A/B assignment?
A: It gives a random but consistent split, so each user always sees the same version.

@@ 3 | TOPIC | Practice & review: Sampling and experiments | Mixed problem set with worked answers | SHOULD
# Practice & review: Sampling and experiments

**How to use this:** 30 minutes. Write full sentences.

## Problems
1. A city council wants residents' opinions on a new bus route. Describe a stratified sample and say why it may be better than an SRS.
2. Identify the sampling method: (a) choose 5 random schools and survey all their teachers; (b) survey shoppers at a mall entrance.
3. A survey asks "Wouldn't you agree that the excellent new park is a good use of money?" Identify the problem and rewrite it.
4. An article says "People who own dogs have lower blood pressure." Is this from an experiment? Name a possible confounder.
5. Design an experiment to test whether a new study app improves exam scores.
6. Why might a company's survey of current customers overestimate satisfaction?

## Answers
1. Divide residents into strata (e.g. by neighbourhood along/away from the route, or by age group) and take an SRS within each; it guarantees each group's view is represented and can give more precise estimates.
2. (a) Cluster sampling; (b) convenience sampling (likely biased).
3. Leading/loaded wording. "Do you support or oppose spending on the new park?"
4. Likely observational; confounder: dog owners may walk more (exercise) or have different lifestyles.
5. Recruit students, randomly assign half to use the app and half to the usual method (control) for a fixed period, keep other conditions similar, possibly block by prior grades, then compare mean exam scores.
6. Dissatisfied customers have already left (survivorship bias), so current customers are the satisfied ones.

## Flashcards
Q: Stratified sampling?
A: Divide into similar groups (strata) and take an SRS from each.

Q: Confounding?
A: When another variable's effect is mixed up with the treatment's effect.

Q: Only design that can show causation?
A: A randomised controlled experiment.

Q: Survivorship bias?
A: Drawing conclusions only from cases that "survived" a selection process.

@@ 1 | MODULE | Phase 4 — Probability (Core) | Probability rules, counting, conditional probability, independence, Bayes' theorem, simulation | MUST
# Phase 4: Probability

Probability measures uncertainty with a number from 0 (impossible) to 1 (certain). It underlies statistics, games, insurance, finance, machine learning (classifiers output probabilities; Naive Bayes, language models), cryptography and reliability engineering.

@@ 2 | MODULE | Probability rules and counting | Basic rules, complement and addition, counting principle, permutations, combinations | MUST
Most probability questions reduce to counting outcomes or combining simple rules.

@@ 3 | TOPIC | Probability basics: sample spaces, theoretical and experimental probability | Outcomes, events, equally likely outcomes, relative frequency, law of large numbers | MUST
# Probability basics: sample spaces, theoretical and experimental probability

## 1. What it covers
- **Sample space** $S$, **outcomes** and **events**.
- **Theoretical** probability for equally likely outcomes.
- **Experimental** probability (relative frequency).
- The **law of large numbers**.

## 2. Where it is used
- **Games and gambling:** dice, cards, lotteries (and why the house always wins in the long run).
- **Insurance:** premiums are based on long-run frequencies of claims.
- **Weather forecasting:** "70% chance of rain".
- **Software:** randomised algorithms, load balancing, simulations.

## 3. What it means
- **Sample space:** the list of all possible outcomes. Rolling a die: $\{1, 2, 3, 4, 5, 6\}$.
- **Event:** a set of outcomes, e.g. "even" $= \{2, 4, 6\}$.
- **Theoretical probability** (equally likely outcomes): $P(A) = \frac{\text{favourable outcomes}}{\text{total outcomes}}$.
- **Experimental probability:** $\frac{\text{times A happened}}{\text{number of trials}}$.

**Law of large numbers:** as the number of trials grows, the experimental probability gets closer to the true probability. Flip a coin 10 times and you might get 7 heads; flip it 10 000 times and you'll get very close to 50%. It says nothing about short runs: after 5 heads in a row, tails is **not** "due" (the **gambler's fallacy**).

## 4. Formula & worked example
$$
P(A) = \frac{\lvert A\rvert}{\lvert S\rvert} \ (\text{equally likely}) \qquad 0 \le P(A) \le 1 \qquad P(S) = 1
$$

**Example 1.** Probability of rolling a number greater than 4: $\frac{2}{6} = \frac{1}{3}$.

**Example 2 (two dice).** Sample space has 36 outcomes. $P(\text{sum} = 7)$: (1,6), (2,5), (3,4), (4,3), (5,2), (6,1) → $\frac{6}{36} = \frac{1}{6}$.

**Example 3.** A card from a 52-card deck: $P(\text{heart}) = \frac{13}{52} = \frac{1}{4}$; $P(\text{king}) = \frac{4}{52} = \frac{1}{13}$.

**Example 4 (experimental).** A machine produced 12 defective parts out of 800 → estimated defect probability $0.015$.

**Example 5 (code: law of large numbers).**
```python
import random
for n in (10, 100, 10_000, 1_000_000):
    heads = sum(random.random() < 0.5 for _ in range(n))
    print(n, heads / n)
# e.g. 10 0.7 / 100 0.46 / 10000 0.4987 / 1000000 0.50021
```
Each `random.random() < 0.5` is a simulated fair coin flip. The proportion of heads wanders for small $n$ and settles near 0.5 as $n$ grows.

## 5. Common mistakes
- Assuming outcomes are equally likely when they aren't (sums of two dice: 7 is more likely than 2).
- The gambler's fallacy ("tails is due").
- Probabilities above 1 or below 0 (a sign of an error).

## 6. Practice
Q: $P(\text{rolling a prime on a die})$?
A: $\{2, 3, 5\}$ → $\frac{1}{2}$.

Q: Two coins: $P(\text{at least one head})$?
A: $\frac{3}{4}$.

Q: Two dice: $P(\text{sum} = 2)$?
A: $\frac{1}{36}$.

Q: State the law of large numbers.
A: As trials increase, the relative frequency approaches the true probability.

Q: A coin landed heads 6 times in a row. Probability of heads next?
A: $\frac{1}{2}$ (independent flips).

@@ 3 | TOPIC | Complement and addition rules | P(not A) = 1 − P(A), mutually exclusive events, P(A or B) = P(A) + P(B) − P(A and B), Venn diagrams | MUST
# Complement and addition rules

## 1. What it covers
- The **complement rule**: $P(A^c) = 1 - P(A)$.
- **Mutually exclusive** (disjoint) events.
- The **general addition rule** for $P(A \text{ or } B)$.
- **Venn diagrams**.

## 2. Where it is used
- **Reliability:** $P(\text{at least one failure}) = 1 - P(\text{no failures})$.
- **Security:** probability a password is cracked by at least one of several attacks.
- **Marketing:** customers who use product A or B (avoid double counting).
- **Databases:** `COUNT` of rows matching A OR B uses inclusion–exclusion.

## 3. What it means
- **Complement:** "not A" happens exactly when A doesn't. Often it's easier to compute the complement, especially for "at least one" questions.
- **Mutually exclusive:** A and B can't both happen (rolling a 2 and a 5 on one die). Then $P(A \text{ or } B) = P(A) + P(B)$.
- **General addition rule:** if they can overlap, adding counts the overlap twice, so subtract it once. A Venn diagram shows why.

## 4. Formula & worked example
$$
P(A^c) = 1 - P(A) \qquad P(A \cup B) = P(A) + P(B) - P(A \cap B)
$$

**Example 1.** $P(\text{rain}) = 0.3$ → $P(\text{no rain}) = 0.7$.

**Example 2 (at least one).** Three independent servers each fail with probability 0.1 per day. $P(\text{at least one fails})$?
- $1 - 0.9^3 = 1 - 0.729 = 0.271$

**Example 3 (overlap).** In a class, 60% study maths, 45% study physics, 25% study both. $P(\text{maths or physics})$?
- $0.60 + 0.45 - 0.25 = 0.80$

**Example 4 (cards).** $P(\text{heart or king}) = \frac{13}{52} + \frac{4}{52} - \frac{1}{52} = \frac{16}{52} = \frac{4}{13}$

**Example 5 (birthday problem).** In a room of 23 people, $P(\text{at least two share a birthday})$?
- $1 - \frac{365}{365}\cdot\frac{364}{365}\cdots\frac{343}{365} \approx 1 - 0.493 = 0.507$ (surprisingly over 50%; the same maths explains hash collisions in computing).

## 5. Common mistakes
- Adding probabilities of overlapping events without subtracting the overlap.
- Computing "at least one" directly instead of using the complement.
- Confusing mutually exclusive with independent (they're different ideas).

## 6. Practice
Q: $P(A) = 0.35$. $P(\text{not } A)$?
A: $0.65$.

Q: $P(A) = 0.5$, $P(B) = 0.4$, $P(A \cap B) = 0.2$. $P(A \cup B)$?
A: $0.7$.

Q: Are "roll a 1" and "roll an even number" mutually exclusive?
A: Yes.

Q: $P(\text{at least one six in 4 rolls})$?
A: $1 - \left(\frac{5}{6}\right)^4 \approx 0.518$.

Q: Why subtract $P(A \cap B)$ in the addition rule?
A: The overlap is counted twice when adding $P(A)$ and $P(B)$.

@@ 3 | TOPIC | Counting principle and permutations | Multiplication rule, factorials, arrangements, permutations nPr | MUST
# Counting principle and permutations

## 1. What it covers
- The **multiplication (counting) principle**.
- **Factorials** $n!$.
- **Permutations**: ordered arrangements, $_nP_r = \frac{n!}{(n - r)!}$.
- Arrangements with repetition and with identical items.

## 2. Where it is used
- **Security:** number of possible passwords or PINs (and how long brute force takes).
- **Scheduling and logistics:** orderings of tasks or delivery routes (the travelling salesman problem has $n!$ routes).
- **Computer science:** permutations in algorithms, testing all orderings.
- **Probability:** counting outcomes for equally likely events.

## 3. What it means
**Multiplication principle:** if one choice can be made in $a$ ways and a second in $b$ ways, together there are $a \times b$ ways. 3 shirts and 4 trousers give $3 \times 4 = 12$ outfits.

**Permutations** count **ordered** arrangements (order matters: ABC ≠ BAC).
- All $n$ items: $n!$ ($5! = 120$).
- Choose $r$ of $n$ in order: $n \times (n - 1) \times \cdots$ ($r$ factors) $= \frac{n!}{(n - r)!}$.
- With identical items: divide by the factorials of the repeats (arrangements of "BANANA" $= \frac{6!}{3!\,2!}$).

Factorials grow incredibly fast: $20! \approx 2.4 \times 10^{18}$, which is why brute-forcing all orderings is hopeless for large $n$.

## 4. Formula & worked example
$$
n! = n(n - 1)\cdots 1,\ 0! = 1 \qquad {}_nP_r = \frac{n!}{(n - r)!}
$$

**Example 1 (PIN).** 4-digit PIN (digits can repeat): $10^4 = 10\,000$. No repeats: $10 \times 9 \times 8 \times 7 = 5040$.

**Example 2 (password).** 8 characters from 62 (a–z, A–Z, 0–9): $62^8 \approx 2.2 \times 10^{14}$. At $10^{10}$ guesses per second: about 6 hours. With 12 characters: $62^{12} \approx 3.2 \times 10^{21}$ → about 10 000 years.

**Example 3.** 10 runners: number of ways to award gold, silver and bronze? → $_{10}P_3 = 720$.

**Example 4.** Arrangements of the letters of "LEVEL"? → $\frac{5!}{2!\,2!} = 30$ (two L's, two E's).

**Example 5 (routes).** A delivery to 8 stops can be ordered in $8! = 40\,320$ ways; 15 stops in $1.3 \times 10^{12}$ ways (so route optimisers use heuristics).

**Example 6 (code).**
```python
import math, itertools
print(math.perm(10, 3), math.factorial(8))            # 720 40320
print(list(itertools.permutations("ABC")))           # all 6 orderings
```
`math.perm(n, r)` computes $_nP_r$; `itertools.permutations` lists the actual arrangements (only feasible for small inputs).

## 5. Common mistakes
- Using permutations when order doesn't matter (use combinations; next topic).
- Forgetting that $0! = 1$.
- Allowing repeats when the problem doesn't (or vice versa).

## 6. Practice
Q: $5!$?
A: $120$.

Q: $_7P_2$?
A: $42$.

Q: Number of 3-letter codes from 26 letters with repetition?
A: $26^3 = 17\,576$.

Q: Ways to seat 6 people in a row?
A: $720$.

Q: Arrangements of "MISSISSIPPI"?
A: $\frac{11!}{4!\,4!\,2!} = 34\,650$.

@@ 3 | TOPIC | Combinations and probability with counting | nCr, choosing without order, committees, card hands, lotteries | MUST
# Combinations and probability with counting

## 1. What it covers
- **Combinations**: unordered selections, $\binom{n}{r} = \frac{n!}{r!(n - r)!}$.
- Permutations vs combinations: does order matter?
- Probability = favourable selections ÷ total selections (card hands, committees, lotteries, defective items).

## 2. Where it is used
- **Lotteries:** odds of winning.
- **Quality control:** probability a sample of items contains defects (hypergeometric).
- **Machine learning:** number of feature subsets ($2^n$) or pairs ($\binom{n}{2}$); cross-validation splits.
- **Networks:** number of possible connections between $n$ nodes: $\binom{n}{2}$.

## 3. What it means
A **combination** counts selections where **order doesn't matter**: choosing a committee of 3 from 10 people. Every group of 3 can be ordered in $3! = 6$ ways, and the permutation count lists each group 6 times, so divide:
$$
\binom{10}{3} = \frac{_{10}P_3}{3!} = \frac{720}{6} = 120
$$

**Order matters?** Race positions, passwords, seating → permutations. Teams, committees, card hands, lottery numbers → combinations.

**Counting probability:** $P = \frac{\text{number of favourable selections}}{\text{number of possible selections}}$, often using combinations for both.

## 4. Formula & worked example
$$
\binom{n}{r} = \frac{n!}{r!\,(n - r)!} \qquad \binom{n}{r} = \binom{n}{n - r}
$$

**Example 1.** Choose 4 books from 9: $\binom{9}{4} = 126$.

**Example 2 (lottery).** Pick 6 numbers from 49: $\binom{49}{6} = 13\,983\,816$ → $P(\text{jackpot}) \approx 7.2 \times 10^{-8}$.

**Example 3 (committee).** A committee of 3 from 6 women and 4 men. $P(\text{exactly 2 women})$?
- $\frac{\binom{6}{2}\binom{4}{1}}{\binom{10}{3}} = \frac{15 \times 4}{120} = 0.5$

**Example 4 (cards).** $P(\text{a 5-card hand has all hearts})$: $\frac{\binom{13}{5}}{\binom{52}{5}} = \frac{1287}{2\,598\,960} \approx 0.000495$.

**Example 5 (defects).** A box of 20 chips has 3 defective. You test 4 at random. $P(\text{none defective})$?
- $\frac{\binom{17}{4}}{\binom{20}{4}} = \frac{2380}{4845} \approx 0.491$

**Example 6 (network).** Possible direct links between 50 servers: $\binom{50}{2} = 1225$.

## 5. Common mistakes
- Using combinations when order matters (or the reverse).
- Forgetting to multiply when choosing from several groups (Example 3).
- Arithmetic errors with big factorials; cancel before multiplying.

## 6. Practice
Q: $\binom{8}{2}$?
A: $28$.

Q: Ways to choose a team of 5 from 12 players?
A: $\binom{12}{5} = 792$.

Q: Order matters or not: choosing a president and a secretary?
A: Order matters (permutation).

Q: $P(\text{two random cards are both aces})$?
A: $\frac{\binom{4}{2}}{\binom{52}{2}} = \frac{6}{1326} \approx 0.0045$.

Q: Why does $\binom{n}{r} = \binom{n}{n - r}$?
A: Choosing $r$ items to include is the same as choosing $n - r$ to leave out.

@@ 2 | MODULE | Conditional probability, independence and Bayes | Conditional probability, trees, independence, multiplication rule, Bayes' theorem, simulation | MUST
How should the probability of an event change when you learn new information?

@@ 3 | TOPIC | Conditional probability and tree diagrams | P(A given B) = P(A and B)/P(B), two-way tables, probability trees | MUST
# Conditional probability and tree diagrams

## 1. What it covers
- **Conditional probability** $P(A \mid B)$: the probability of A **given** that B happened.
- Computing it from two-way tables and from the formula.
- **Tree diagrams** for multi-stage processes.
- The general multiplication rule $P(A \cap B) = P(A)P(B \mid A)$.

## 2. Where it is used
- **Medicine:** probability of disease given a positive test.
- **Spam filters and classifiers:** $P(\text{spam} \mid \text{words in the email})$.
- **Product analytics:** conversion rate **given** the user came from an ad.
- **Reliability:** probability a system fails given a component has failed.
- **Insurance:** risk given age, region, history.

## 3. What it means
$P(A \mid B)$ restricts attention to the cases where B happened: B becomes the new "whole". Of those, what fraction also have A?
$$
P(A \mid B) = \frac{P(A \cap B)}{P(B)}
$$

**Order matters:** $P(A \mid B) \ne P(B \mid A)$ in general. $P(\text{has a fever} \mid \text{has flu})$ is high, but $P(\text{has flu} \mid \text{has a fever})$ may be much lower (many things cause fevers).

**Tree diagrams:** each branch shows a conditional probability. Multiply along a path to get the probability of that path; add paths for an event that can happen in several ways.

## 4. Formula & worked example
$$
P(A \mid B) = \frac{P(A \cap B)}{P(B)} \qquad P(A \cap B) = P(A)\,P(B \mid A)
$$

**Example 1 (table).** Using the device table from Phase 1: $P(\text{bought} \mid \text{desktop}) = \frac{40}{400} = 0.10$, but $P(\text{desktop} \mid \text{bought}) = \frac{40}{76} \approx 0.53$.

**Example 2 (cards, without replacement).** Two cards drawn. $P(\text{both aces}) = \frac{4}{52} \times \frac{3}{51} = \frac{1}{221}$.

**Example 3 (tree).** 60% of a shop's laptops come from supplier X (2% defective) and 40% from Y (5% defective). $P(\text{defective})$?
- Path X-defective: $0.6 \times 0.02 = 0.012$; path Y-defective: $0.4 \times 0.05 = 0.020$ → total $0.032$.

**Example 4 (reverse).** Given a laptop is defective, $P(\text{from Y})$? → $\frac{0.020}{0.032} = 0.625$ (that's Bayes' theorem, next-but-one topic).

**Example 5.** $P(A) = 0.5$, $P(B) = 0.4$, $P(A \cap B) = 0.1$ → $P(A \mid B) = 0.25$, $P(B \mid A) = 0.2$.

## 5. Common mistakes
- Confusing $P(A \mid B)$ with $P(B \mid A)$ (the "prosecutor's fallacy").
- Dividing by the wrong total in a two-way table.
- Forgetting that drawing **without replacement** changes the second probability.

## 6. Practice
Q: $P(A \cap B) = 0.12$, $P(B) = 0.3$. $P(A \mid B)$?
A: $0.4$.

Q: Two cards without replacement: $P(\text{both hearts})$?
A: $\frac{13}{52} \times \frac{12}{51} = \frac{1}{17}$.

Q: In a tree diagram, how do you get the probability of a path?
A: Multiply the probabilities along its branches.

Q: Is $P(\text{rain} \mid \text{clouds})$ the same as $P(\text{clouds} \mid \text{rain})$?
A: No.

Q: A bag has 3 red and 2 blue balls; draw two without replacement. $P(\text{red then blue})$?
A: $\frac{3}{5} \times \frac{2}{4} = \frac{3}{10}$.

@@ 3 | TOPIC | Independence and the multiplication rule | Independent events, P(A and B) = P(A)P(B), testing independence, independent vs mutually exclusive | MUST
# Independence and the multiplication rule

## 1. What it covers
- **Independent** events: knowing one doesn't change the probability of the other.
- The multiplication rule $P(A \cap B) = P(A)P(B)$ for independent events.
- **Testing** independence from data.
- Independent vs mutually exclusive.

## 2. Where it is used
- **Reliability engineering:** systems with redundant parts (RAID, backup servers) assume independent failures.
- **Security:** combining independent factors (two-factor authentication).
- **Machine learning:** Naive Bayes assumes features are independent given the class.
- **Risk:** the 2008 financial crisis was partly caused by wrongly assuming mortgage defaults were independent.

## 3. What it means
A and B are **independent** if $P(A \mid B) = P(A)$: learning B happened gives no information about A. Coin flips, separate dice and (ideally) separate machines are independent.

Then $P(A \text{ and } B) = P(A) \times P(B)$.

**Testing:** check whether $P(A \cap B) = P(A)P(B)$ (or whether $P(A \mid B) = P(A)$) in the data.

**Independent ≠ mutually exclusive.** Mutually exclusive events (with positive probabilities) are actually **dependent**: if one happens, the other **can't**, which is very informative.

**Series and parallel systems:**
- **Series** (all parts must work): $P(\text{works}) = p_1p_2\cdots$.
- **Parallel** (at least one must work): $P(\text{works}) = 1 - (1 - p_1)(1 - p_2)\cdots$.

## 4. Formula & worked example
$$
A, B \text{ independent} \iff P(A \cap B) = P(A)\,P(B) \iff P(A \mid B) = P(A)
$$

**Example 1.** Two dice: $P(\text{both sixes}) = \frac{1}{6} \times \frac{1}{6} = \frac{1}{36}$.

**Example 2 (test).** $P(A) = 0.4$, $P(B) = 0.5$, $P(A \cap B) = 0.2$ → $0.4 \times 0.5 = 0.2$ → independent.

**Example 3 (test with data).** Of 1000 users, 300 use dark mode, 400 are on mobile, and 150 do both. $P(\text{dark}) \times P(\text{mobile}) = 0.3 \times 0.4 = 0.12 \ne 0.15$ → not independent (mobile users favour dark mode slightly more).

**Example 4 (series vs parallel).** Each server is up 99% of the time, independently.
- Two in series: $0.99^2 = 0.9801$ (98.01%).
- Two in parallel (redundant): $1 - 0.01^2 = 0.9999$ (99.99%).

**Example 5 (correlated failures).** If both servers share one power supply, failures aren't independent and the 99.99% figure is wrong. Real systems spread replicas across separate zones for this reason.

## 5. Common mistakes
- Multiplying probabilities of dependent events as if they were independent.
- Thinking mutually exclusive events are independent.
- Assuming independence in real systems without checking (shared causes).

## 6. Practice
Q: $P(A) = 0.3$, $P(B) = 0.6$, independent. $P(A \cap B)$?
A: $0.18$.

Q: $P(A) = 0.2$, $P(B) = 0.5$, $P(A \cap B) = 0.15$. Independent?
A: No ($0.1 \ne 0.15$).

Q: Three components in series, each 95% reliable. System reliability?
A: $0.95^3 \approx 0.857$.

Q: Two backups in parallel, each 90% reliable. System reliability?
A: $1 - 0.1^2 = 0.99$.

Q: Can two events with positive probability be both mutually exclusive and independent?
A: No.

@@ 3 | TOPIC | Bayes' theorem | Updating probabilities with evidence, base rates, false positives, spam filtering | MUST
# Bayes' theorem

## 1. What it covers
- **Bayes' theorem**: reversing a conditional probability.
- **Prior**, **likelihood**, **posterior**.
- The **base rate fallacy** and false positives in medical tests.
- Naive Bayes spam filtering.

## 2. Where it is used
- **Medicine:** interpreting test results (most positives for a rare disease can be false).
- **Spam filters:** classic email filters used Naive Bayes.
- **Machine learning:** Bayesian inference, Naive Bayes classifiers, probabilistic models.
- **Fraud detection and security alerts:** most alerts for rare events are false alarms.
- **Search and rescue, A/B testing (Bayesian methods), self-driving perception.**

## 3. What it means
You often know $P(\text{evidence} \mid \text{cause})$ (a test's accuracy) but want $P(\text{cause} \mid \text{evidence})$ (do I have the disease, given a positive test?). Bayes' theorem converts one into the other:
$$
P(H \mid E) = \frac{P(E \mid H)\,P(H)}{P(E)}
$$
- **Prior** $P(H)$: belief before the evidence (the base rate).
- **Likelihood** $P(E \mid H)$: how probable the evidence is if $H$ is true.
- **Posterior** $P(H \mid E)$: updated belief.

**The base rate matters enormously.** If a disease is rare, even an accurate test produces mostly false positives, because there are so many more healthy people to produce false alarms. Imagine 10 000 people to make it concrete.

## 4. Formula & worked example
$$
P(H \mid E) = \frac{P(E \mid H)P(H)}{P(E \mid H)P(H) + P(E \mid H^c)P(H^c)}
$$

**Example 1 (medical test).** A disease affects 1% of people. The test detects 99% of cases (sensitivity) and has a 5% false positive rate. You test positive. $P(\text{disease})$?
- $P(D \mid +) = \frac{0.99 \times 0.01}{0.99 \times 0.01 + 0.05 \times 0.99} = \frac{0.0099}{0.0099 + 0.0495} \approx 0.167$
- Only about **17%**! In 10 000 people: 100 sick → 99 test positive; 9900 healthy → 495 false positives. Of 594 positives, only 99 are sick.

**Example 2 (second test).** If you test positive again (independent test), use 0.167 as the new prior:
- $\frac{0.99 \times 0.167}{0.99 \times 0.167 + 0.05 \times 0.833} \approx 0.80$

**Example 3 (laptop suppliers from the previous topic).** $P(Y \mid \text{defective}) = \frac{0.05 \times 0.4}{0.032} = 0.625$.

**Example 4 (spam).** 40% of email is spam. The word "free" appears in 30% of spam and 2% of normal email. $P(\text{spam} \mid \text{"free"})$?
- $\frac{0.3 \times 0.4}{0.3 \times 0.4 + 0.02 \times 0.6} = \frac{0.12}{0.132} \approx 0.91$

**Example 5 (code).**
```python
def bayes(prior, sensitivity, false_pos):
    evidence = sensitivity * prior + false_pos * (1 - prior)
    return sensitivity * prior / evidence
p = bayes(0.01, 0.99, 0.05); print(round(p, 3))   # 0.167
print(round(bayes(p, 0.99, 0.05), 3))             # 0.798  after a second positive test
```
The posterior from the first test becomes the prior for the second: that's how Bayesian updating works.

## 5. Common mistakes
- Ignoring the base rate (assuming a positive test means 99% chance of disease).
- Confusing $P(E \mid H)$ with $P(H \mid E)$.
- Forgetting the false-positive term in the denominator.

## 6. Practice
Q: State Bayes' theorem.
A: $P(H \mid E) = \frac{P(E \mid H)P(H)}{P(E)}$.

Q: What is the prior?
A: The probability of the hypothesis before seeing the evidence.

Q: Disease rate 2%, sensitivity 90%, false positive rate 10%. $P(\text{disease} \mid +)$?
A: $\frac{0.018}{0.018 + 0.098} \approx 0.155$.

Q: Why do rare-event alarms produce many false positives?
A: Far more negatives exist, so even a small false-positive rate creates many false alarms.

Q: In Naive Bayes, what does "naive" refer to?
A: Assuming the features are independent given the class.

@@ 3 | TOPIC | Simulation and Monte Carlo methods | Random number generation, estimating probabilities and π by simulation, expected values | MUST
# Simulation and Monte Carlo methods

## 1. What it covers
- Using random numbers to **simulate** chance processes.
- Estimating probabilities by repeating a simulation many times.
- **Monte Carlo** estimation (e.g. of $\pi$ or of an area).
- How accuracy grows with the number of simulations.

## 2. Where it is used
- **Finance:** pricing options and estimating portfolio risk (Value at Risk).
- **Engineering:** reliability of complex systems; tolerance analysis.
- **Games and AI:** Monte Carlo tree search (used in AlphaGo).
- **Physics and graphics:** path tracing for photorealistic rendering, particle simulations.
- **Project management:** probability of finishing by a deadline.

## 3. What it means
When a probability is hard to calculate exactly, **simulate the process many times** and count how often the event happens. By the law of large numbers, the relative frequency approaches the true probability.

**Estimating $\pi$:** throw random points into a 1 × 1 square. The fraction that lands inside the quarter circle of radius 1 approximates its area $\frac{\pi}{4}$.

**Accuracy:** the error shrinks like $\frac{1}{\sqrt{n}}$: 100 times more simulations for one extra digit of accuracy.

## 4. Formula & worked example
$$
\hat{p} = \frac{\text{number of simulated successes}}{n} \qquad \text{typical error} \approx \sqrt{\frac{p(1 - p)}{n}}
$$

**Example 1 (π).**
```python
import random
n = 1_000_000
inside = sum(random.random()**2 + random.random()**2 <= 1 for _ in range(n))
print(4 * inside / n)      # ≈ 3.141...
```
Each point $(x, y)$ with $x^2 + y^2 \le 1$ lies inside the quarter circle; multiplying the fraction by 4 estimates $\pi$. With a million points the error is about 0.002.

**Example 2 (birthday problem).**
```python
import random
def shared(k):
    days = [random.randrange(365) for _ in range(k)]
    return len(set(days)) < k            # a repeat means two people share a birthday
print(sum(shared(23) for _ in range(100_000)) / 100_000)   # ≈ 0.507
```
The simulation agrees with the exact calculation from the addition-rule topic.

**Example 3 (project deadline).** Three tasks take uniformly 2–5, 3–6 and 1–4 days. $P(\text{total} \le 12)$?
```python
import random
n = 100_000
ok = sum(random.uniform(2,5) + random.uniform(3,6) + random.uniform(1,4) <= 12 for _ in range(n))
print(ok / n)              # ≈ 0.83
```
Summing random task times and counting how often the total fits the deadline answers a question that's awkward to solve with formulas.

## 5. Common mistakes
- Too few simulations (results vary wildly).
- Not setting a random seed when results must be reproducible (`random.seed(42)`).
- Modelling the process wrongly (e.g. sampling with replacement when it should be without).

## 6. Practice
Q: What principle makes Monte Carlo estimates work?
A: The law of large numbers.

Q: How does the error of a Monte Carlo estimate shrink with $n$?
A: Proportional to $\frac{1}{\sqrt{n}}$.

Q: How do you estimate $\pi$ with random points?
A: The fraction in a quarter circle ≈ $\frac{\pi}{4}$; multiply by 4.

Q: Why set a random seed?
A: To make the simulation reproducible.

Q: To gain one more decimal digit of accuracy, how many more simulations?
A: About 100 times more.

@@ 3 | TOPIC | Practice & review: Probability | Mixed problem set with worked answers | MUST
# Practice & review: Probability

**How to use this:** 50 minutes; calculator allowed.

## Problems
1. Two dice: $P(\text{sum} \ge 10)$.
2. $P(A) = 0.45$, $P(B) = 0.35$, $P(A \cup B) = 0.6$. Find $P(A \cap B)$ and $P(A \mid B)$. Independent?
3. How many 6-character passwords using lowercase letters and digits (repeats allowed)? Without repeats?
4. A committee of 4 is chosen from 7 engineers and 5 designers. $P(\text{2 of each})$?
5. A box has 5 red, 3 green and 2 blue balls; draw 2 without replacement. $P(\text{same colour})$?
6. A component works with probability 0.9. How many in parallel are needed for at least 99.9% reliability?
7. 3% of transactions are fraudulent. A detector flags 95% of fraud and 4% of legitimate transactions. $P(\text{fraud} \mid \text{flagged})$?
8. Explain how you would estimate by simulation the probability of getting at least 3 heads in a row in 10 coin flips.

## Answers
1. (4,6), (5,5), (6,4), (5,6), (6,5), (6,6) → $\frac{6}{36} = \frac{1}{6}$.
2. $P(A \cap B) = 0.45 + 0.35 - 0.6 = 0.2$; $P(A \mid B) = \frac{0.2}{0.35} \approx 0.571$; $P(A)P(B) = 0.1575 \ne 0.2$ → not independent.
3. $36^6 = 2\,176\,782\,336$; without repeats $_{36}P_6 = 1\,402\,410\,240$.
4. $\frac{\binom{7}{2}\binom{5}{2}}{\binom{12}{4}} = \frac{21 \times 10}{495} \approx 0.424$.
5. $\frac{\binom{5}{2} + \binom{3}{2} + \binom{2}{2}}{\binom{10}{2}} = \frac{10 + 3 + 1}{45} = \frac{14}{45} \approx 0.311$.
6. $1 - 0.1^n \ge 0.999$ → $0.1^n \le 0.001$ → $n = 3$.
7. $\frac{0.95 \times 0.03}{0.95 \times 0.03 + 0.04 \times 0.97} = \frac{0.0285}{0.0673} \approx 0.42$.
8. Simulate 10 random flips, check for a run of 3 heads, repeat 100 000 times, and report the fraction of runs with such a streak (≈ 0.508).

## Flashcards
Q: General addition rule?
A: $P(A \cup B) = P(A) + P(B) - P(A \cap B)$.

Q: Conditional probability formula?
A: $P(A \mid B) = \frac{P(A \cap B)}{P(B)}$.

Q: Permutations vs combinations?
A: Permutations: order matters; combinations: order doesn't.

Q: Why can a positive result for a rare disease be unreliable?
A: The base rate is low, so false positives outnumber true positives.
