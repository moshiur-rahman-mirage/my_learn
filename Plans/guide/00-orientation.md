@@ 1 | MODULE | Phase 0 — Orientation & Strategy | Understand the job, the domain and how to study | MUST
# Phase 0: Orientation & Strategy

Before learning any technology, know **what job you are preparing for** and **how you will be tested**.
Think of this phase as reading the map before a journey. Without it you may walk fast but in the wrong direction.

**Focus order:** Java + Spring Boot + React + PostgreSQL first (these are mandatory). Then Security (JWT/OAuth2/Keycloak), Microservices, Redis, Docker/Kubernetes/CI-CD (preferred). Cloud is optional.

@@ 2 | TOPIC | How to Use This Guide | Study method, note format and 8-week plan | MUST
# How to use this guide

Every topic in this guide follows the same pattern:

1. **Idea:** what it is, in plain words, often with an everyday analogy.
2. **Code:** a small example you should be able to write on paper.
3. **Line by line:** what each important line does and *why*.
4. **Remember:** the key points and common traps.
5. **Q&A:** questions you may be asked in the written test or viva.

> Rule: never memorize code you cannot explain. In a viva, the examiner will point at a line and ask "why is this here?"

## 8-week plan

| Week | Focus | Output |
|---|---|---|
| 1 | CS fundamentals + Core Java | 50 MCQs/day, 2 coding problems/day |
| 2 | Collections, Streams, Concurrency, JVM | Handwritten Java solutions |
| 3 | Spring Core, Boot, REST, JPA | CRUD API with validation and tests |
| 4 | SQL + PostgreSQL design, indexing, tuning | 20 SQL problems, EXPLAIN practice |
| 5 | Spring Security, JWT, OAuth2, Keycloak | Secure API with Keycloak |
| 6 | React + TypeScript | Frontend for the API |
| 7 | Microservices, SOAP/XML, Redis, MongoDB | Split into services, add cache |
| 8 | Docker, Kubernetes, CI/CD, mock interviews | Deployed portfolio repo |

## Daily routine (about 3–4 hours)

- **30 min:** revise yesterday's topics. Close the note and explain them out loud.
- **90 min:** study new topics from this guide.
- **60 min:** write code by hand (paper), then type it and run it.
- **30 min:** MCQs / Q&A from the "Interview Questions" topics.

**Why revise every day?** Memory is like a footpath through grass. Walk it once and it disappears. Walk it again and again and it becomes a road. This is called *spaced repetition*.

@@ 2 | TOPIC | What the TOR Asks For | Mandatory, preferred and optional skills | MUST
# What the TOR (Terms of Reference) asks for

The TOR is the job description. Every line in it is a hint about what they will ask.

- **Mandatory:** at least 2 years of full-stack experience, with hands-on Java (Spring Boot) and React.
- **Preferred:** a public GitHub with clean structure, tests, a README and docker-compose. Also microservices; REST/JSON/XML/SOAP; OAuth2, JWT, Keycloak; PostgreSQL schema design, indexing and query tuning; MongoDB; Redis caching and sessions; Docker, Kubernetes, Git, CI/CD.
- **Optional:** AWS/GCP/Azure familiarity or an associate-level certificate.

## Context: why this matters at PKSF

PKSF (Palli Karma-Sahayak Foundation) funds 200+ **Partner Organizations (POs)**, which lend money to poor people.
Its software handles **money and personal data**. So expect questions on:

- **Security:** who can see or change what?
- **Data integrity:** a loan balance must never be wrong.
- **Auditing:** who changed what, and when?
- **Performance:** reports over millions of installments must stay fast.

**Analogy:** building software for PKSF is like building a bank vault, not a garden shed. Every answer you give should show that you care about correctness and safety.

@@ 2 | TOPIC | Microfinance Vocabulary | PO, member, disbursement, installment, PAR | MUST
# Microfinance vocabulary

You will look much stronger in a viva if you use the domain's own words.

| Term | Simple meaning |
|---|---|
| **PO (Partner Organization)** | An NGO/MFI that borrows from PKSF and lends to members |
| **Member / Borrower** | A person in a PO group (called a *samity*) |
| **Samity** | A small group of members who meet weekly |
| **Disbursement** | Money given out to a borrower |
| **Installment** | One scheduled repayment (often weekly) |
| **Outstanding** | Principal not yet repaid |
| **Overdue** | Installments past their due date and not paid |
| **PAR (Portfolio at Risk)** | Outstanding of loans that have overdue installments ÷ total outstanding |
| **Savings** | Money members deposit |
| **Service charge** | An interest-like fee on a loan |

## Worked example: PAR

A PO has 3 loans:

- Loan A: outstanding 10,000, no overdue
- Loan B: outstanding 5,000, **has an overdue installment**
- Loan C: outstanding 5,000, no overdue

PAR = 5,000 / 20,000 = **25%**. That means a quarter of the money lent out is at risk.

**Analogy:** PAR is like a teacher asking, "Out of all the homework I handed out, how much is late?"

## Design tip

In design questions, model **Loans**, **Installments** and **Transactions** with:

- `NUMERIC(14,2)` money columns (never float: floats round wrongly, like a ruler with blurry marks).
- An **immutable ledger**: never update or delete a payment. Add a reversing entry instead, just like an accountant who never erases ink.
- An **audit trail**: who did what, and when.

@@ 2 | TOPIC | Selection Process | Written test, viva and how to prepare for each | MUST
# Selection process

## 1. Written test
- MCQs: Java, OOP, DBMS, networking, OS, web.
- Short code **by hand**: reverse a string, find duplicates, binary search, a SQL query.
- Short theory answers: "Explain ACID", "Difference between 401 and 403".

## 2. Viva (interview)
- Your past projects: be ready to draw the architecture.
- Spring Boot internals, database design, security.
- "Why PKSF?" Prepare an honest answer that links your skills to their mission (financial inclusion, secure systems for POs).

## How to prepare
- **Write Java on paper.** With no IDE, a missing semicolon or bracket costs marks. Practice until your handwriting compiles in your head.
- **Explain out loud.** If you cannot explain it to a friend in 2 minutes, you do not know it yet.
- **Use the STAR format** for project questions: Situation → Task → Action → Result.

Q: Why do they test handwritten code?
A: To check that you understand logic and syntax without autocomplete. They are testing your thinking, not your IDE.
