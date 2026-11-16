@@ 1 | MODULE | Phase 8 — Frontend with React | HTML/CSS, JavaScript, TypeScript, React core, ecosystem | MUST
# Phase 8: Frontend with React

React is **mandatory** in the TOR. Know JavaScript fundamentals (closures, promises, the event loop), TypeScript basics, React hooks and how to call a secured API.

@@ 2 | TOPIC | HTML and CSS Essentials | Semantic HTML, accessibility, box model, flexbox, grid, responsive | SHOULD
# HTML and CSS essentials

## Semantic HTML
Use tags that **describe meaning**, not just looks.

```html
<header><nav><a href="/loans">Loans</a></nav></header>
<main>
  <h1>Member list</h1>
  <form>
    <label for="phone">Phone</label>
    <input id="phone" name="phone" type="tel" required pattern="01[3-9][0-9]{8}" />
    <button type="submit">Search</button>
  </form>
  <table>
    <thead><tr><th scope="col">Name</th><th scope="col">Outstanding</th></tr></thead>
    <tbody><tr><td>Rahim</td><td>5,000</td></tr></tbody>
  </table>
</main>
<footer>© PKSF</footer>
```
**Line by line**
- `header`, `nav`, `main`, `footer`: screen readers and search engines understand the page structure. Better than `<div>` everywhere.
- `<label for="phone">` linked to `id="phone"`: clicking the label focuses the input, and screen readers read the label. **Essential for accessibility.**
- `type="tel"`, `required`, `pattern`: built-in browser validation (still validate on the server!).
- `<th scope="col">`: tells assistive technology which cells are headers.

**Analogy:** semantic tags are like labelled boxes when moving house ("Kitchen", "Books") instead of 50 boxes all labelled "Stuff".

## The box model
Every element is a box: **content → padding → border → margin**.
```css
* { box-sizing: border-box; }   /* width includes padding + border (much easier) */
.card { width: 300px; padding: 16px; border: 1px solid #ddd; margin: 8px; }
```
- Without `border-box`, the card would be 300 + 32 + 2 = 334px wide.

## Flexbox (one dimension: a row OR a column)
```css
.toolbar { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
```
- `justify-content`: alignment along the main axis (horizontal for a row).
- `align-items`: alignment on the cross axis (vertical).
- `gap`: space between the items.

## Grid (two dimensions: rows AND columns)
```css
.dashboard { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px; }
```
- `repeat(auto-fit, minmax(240px, 1fr))`: as many columns as fit, each at least 240px. **Responsive without media queries.**

## Responsive design
```css
.sidebar { display: none; }
@media (min-width: 768px) { .sidebar { display: block; } }
```
- **Mobile-first:** write the small-screen styles first, then add `min-width` media queries for bigger screens. Many PO field officers use phones.

**Specificity (which CSS rule wins):** inline style > `#id` > `.class` / `[attr]` / `:hover` > `tag`. For equal specificity, the later rule wins. Avoid `!important`.

@@ 2 | MODULE | JavaScript | Core language, async, event loop, HTTP calls | MUST
JavaScript is the language of the browser. React is "just JavaScript", so weak JS means weak React.

@@ 3 | TOPIC | JavaScript Core Concepts | let/const, types, ==/===, closures, this, destructuring, spread, array methods | MUST
# JavaScript core concepts

## var vs let vs const
```js
var a = 1;      // function-scoped, hoisted, can be redeclared: avoid
let b = 2;      // block-scoped, can be reassigned
const c = 3;    // block-scoped, can't be reassigned
const arr = [1]; arr.push(2);   // OK: const freezes the BINDING, not the object
```
- Use `const` by default, and `let` when the value must change. Never `var`.

## == vs ===
```js
0 == "0"        // true  (type coercion)
0 === "0"       // false (different types)
null == undefined   // true
null === undefined  // false
```
- **Always use `===`.** `==` converts types in surprising ways.

**Falsy values:** `false, 0, "", null, undefined, NaN`. Everything else is truthy (even `"0"` and `[]`).

## Closures
A function **remembers the variables of the place where it was created**, even after that outer function has finished.

```js
function makeCounter() {
  let count = 0;                 // private variable
  return function () {
    count++;                     // still accessible later
    return count;
  };
}
const next = makeCounter();
next(); // 1
next(); // 2
```
**Line by line**
- `makeCounter` returns an inner function. Normally `count` would disappear when `makeCounter` ends.
- The inner function **closes over** `count`, keeping it alive. Nobody else can touch `count`, so we get privacy.
- **Analogy:** a backpack. The function carries its birthplace's variables in a backpack wherever it goes.
- React hooks rely on closures. **Stale closures** (a callback seeing an old state value) are a common bug.

## this
- In a normal function, `this` depends on **how the function is called** (`obj.method()` → obj).
- **Arrow functions have no own `this`**: they use the `this` of the surrounding code. That's why arrows are handy in callbacks.

## Modern syntax you must read fluently
```js
const member = { name: "Rahim", phone: "017...", po: { code: "PO-07" } };

const { name, po: { code } } = member;        // destructuring
const [first, ...rest] = [10, 20, 30];        // first=10, rest=[20,30]
const copy = { ...member, name: "Karim" };    // spread: shallow copy + override
const merged = [...[1, 2], ...[3]];           // [1,2,3]
const city = member.address?.city ?? "N/A";   // optional chaining + nullish coalescing
const greet = (n = "Guest") => `Hello ${n}`;  // default param + template literal
```
- Destructuring pulls fields out into variables.
- Spread `...` copies or merges. It's essential for **immutable state updates** in React.
- `?.` stops and returns `undefined` instead of throwing when something is null.
- `??` uses the right side only when the left is `null`/`undefined` (unlike `||`, which also replaces `0` and `""`).

## Array methods (used constantly in React)
```js
const loans = [ { id: 1, amt: 5000, active: true }, { id: 2, amt: 8000, active: false } ];

loans.map(l => l.amt);                          // [5000, 8000]   transform each
loans.filter(l => l.active);                    // [{id:1...}]    keep some
loans.reduce((sum, l) => sum + l.amt, 0);       // 13000          combine into one
loans.find(l => l.id === 2);                    // {id:2...}      first match
loans.some(l => !l.active);                     // true           any match?
loans.every(l => l.amt > 1000);                 // true           all match?
[...loans].sort((a, b) => b.amt - a.amt);       // sort a COPY (sort mutates!)
```
- `map`, `filter` and `reduce` return **new** arrays/values and don't mutate the original. React state must not be mutated.
- `sort` **mutates** in place, so copy first with `[...arr]` (or use `toSorted()` in modern JS).

@@ 3 | TOPIC | Async JavaScript and the Event Loop | Callbacks, promises, async/await, microtasks | MUST
# Async JavaScript and the event loop

JavaScript runs on **one thread**. Slow work (network, timers) is handed off, and its callback runs later. The **event loop** decides when.

**Analogy:** a single waiter in a restaurant. He takes your order (starts the request), gives it to the kitchen, and serves other tables meanwhile. When the kitchen rings the bell, he brings your food. He never stands frozen at the kitchen door.

## Promises and async/await

```js
// Promise chain
fetch("/api/v1/loans/7")
  .then(res => {
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
  })
  .then(loan => console.log(loan.outstanding))
  .catch(err => console.error("Failed:", err.message));

// Same with async/await (easier to read)
async function loadLoan(id) {
  try {
    const res = await fetch(`/api/v1/loans/${id}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const loan = await res.json();
    return loan;
  } catch (err) {
    console.error("Failed:", err.message);
    throw err;
  }
}

// Run in parallel
const [member, loans] = await Promise.all([getMember(42), getLoans(42)]);
```
**Line by line**
- A **Promise** is a placeholder for a future value: *pending → fulfilled or rejected*.
- `fetch` does **not** reject on 404/500. It only rejects on network failure. **Always check `res.ok`.**
- `res.json()` also returns a promise, because the body is read asynchronously.
- `await` pauses **this function** (not the whole page) until the promise settles. It can only be used inside `async` functions (or at module top level).
- `try/catch` handles rejected promises with `await`.
- `Promise.all`: start both requests together and wait for both. If any one fails, the whole thing rejects (`Promise.allSettled` gives every result).

## The event loop order (classic MCQ)

```js
console.log("1");
setTimeout(() => console.log("2"), 0);
Promise.resolve().then(() => console.log("3"));
console.log("4");
// Output: 1, 4, 3, 2
```
**Why**
1. Synchronous code runs first: `1`, `4`.
2. Then **all microtasks** (promise callbacks): `3`.
3. Then the next **macrotask** (setTimeout, I/O): `2`.
- So even `setTimeout(..., 0)` waits until the current code and every pending promise callback have finished.

**Analogy:** the waiter finishes the current table (sync code), then handles the "quick requests" queue (microtasks: promises), then picks up the next big order from the "normal" queue (macrotasks: timers).

@@ 3 | TOPIC | Axios Instance with Interceptors | Central API client, auth header, error handling | MUST
# Axios instance with interceptors

Instead of repeating the base URL, token and error handling in every component, create **one API client**.

```ts
// src/api/client.ts
import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,     // e.g. https://api.pksf.org.bd/api/v1
  timeout: 15000,
});

// Before every request: attach the token
api.interceptors.request.use((config) => {
  const token = authStore.getToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// After every response: handle errors in one place
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      authStore.logout();                    // token expired or invalid
      window.location.href = "/login";
    }
    if (error.response?.status === 403) {
      toast.error("You do not have permission");
    }
    return Promise.reject(error);            // let the caller handle it too
  }
);

// Usage
export const getLoan = (id: number) => api.get<Loan>(`/loans/${id}`).then(r => r.data);
export const createMember = (m: MemberRequest) => api.post<Member>("/members", m).then(r => r.data);
```
**Line by line**
- `axios.create`: a pre-configured instance. Every call uses the base URL and timeout.
- `import.meta.env.VITE_API_URL`: an environment variable (Vite), so dev and prod use different URLs.
- **Request interceptor:** runs before each request and adds the `Authorization` header automatically.
- **Response interceptor:** the first function handles success, the second handles errors. A 401 sends the user to login; a 403 shows a message.
- `return Promise.reject(error)`: keeps the error flowing so that the component can show its own message.
- `api.get<Loan>`: a TypeScript generic, so `r.data` is typed as `Loan`.
- Unlike `fetch`, axios **rejects on 4xx/5xx** and parses JSON automatically.

**Analogy:** interceptors are a security desk in the lobby. Every outgoing visitor gets a badge (the token) and every returning visitor is checked, so each office doesn't need its own desk.

@@ 2 | TOPIC | TypeScript Essentials | Types, interfaces, unions, generics, utility types | MUST
# TypeScript essentials

**TypeScript** = JavaScript + **types**. The compiler catches mistakes before the code runs.
**Analogy:** a spell-checker for your code. It underlines `loan.outstnding` before your users ever see the bug.

```ts
// Basic types
let count: number = 5;
let name: string = "Rahim";
let active: boolean = true;
let ids: number[] = [1, 2, 3];

// Interface / type: the shape of an object
interface Member {
  id: number;
  name: string;
  phone?: string;                    // optional
  readonly nationalId: string;       // can't be changed after creation
}

// Union + literal types
type LoanStatus = "PENDING" | "ACTIVE" | "CLOSED";
let s: LoanStatus = "ACTIVE";
// s = "DONE";                       // compile error

// Function types
function totalOutstanding(loans: Loan[]): number {
  return loans.reduce((sum, l) => sum + l.outstanding, 0);
}

// Generics: reusable and type-safe
interface Page<T> { content: T[]; totalElements: number; page: number; }
async function fetchPage<T>(url: string): Promise<Page<T>> {
  const res = await api.get<Page<T>>(url);
  return res.data;
}
const members = await fetchPage<Member>("/members");   // members.content is Member[]

// Narrowing
function show(v: string | number) {
  if (typeof v === "string") return v.toUpperCase();   // TS knows v is a string here
  return v.toFixed(2);                                   // and a number here
}

// Utility types
type MemberForm = Omit<Member, "id">;          // everything except id
type MemberPatch = Partial<Member>;            // all fields optional
type MemberCard = Pick<Member, "name" | "phone">;
type ById = Record<number, Member>;            // { [id]: Member }
```
**Line by line**
- `phone?`: may be missing (`string | undefined`).
- `readonly`: assignment after creation is an error.
- **Literal union** `"PENDING" | "ACTIVE" | "CLOSED"`: only these exact strings are allowed. It works like a Java enum.
- `Page<T>`: one interface reused for members, loans, anything.
- **Narrowing:** after a `typeof` check, TS knows the exact type in each branch.
- `Omit`, `Partial`, `Pick`, `Record`: build new types from existing ones instead of copying fields.

**interface vs type:** both describe object shapes. `interface` can be extended or merged, while `type` can also express unions/intersections. Teams usually pick one style; either is fine.

**`any` vs `unknown`:** `any` turns type checking off (avoid it). `unknown` forces you to check the type before using the value (safer).

@@ 2 | MODULE | React Core | Components, props, state, effects, hooks, context | MUST
React builds UIs from **components**: reusable functions that return what the screen should look like. When **state** changes, React re-renders and updates only the parts of the DOM that changed.

@@ 3 | TOPIC | Components, Props and State | JSX, props, useState, lists and keys, conditional rendering | MUST
# Components, props and state

**Analogy:** components are LEGO bricks. **Props** are the settings you give a brick from outside (its colour). **State** is the brick's own memory (is the light on?).

```tsx
type LoanCardProps = { loanNo: string; outstanding: number; onRepay: (amount: number) => void };

function LoanCard({ loanNo, outstanding, onRepay }: LoanCardProps) {
  const [amount, setAmount] = useState<number>(0);          // local state

  return (
    <div className="card">
      <h3>{loanNo}</h3>
      <p>Outstanding: {outstanding.toLocaleString("en-BD")}</p>
      {outstanding === 0 ? <span>Closed</span> : (
        <>
          <input type="number" value={amount}
                 onChange={e => setAmount(Number(e.target.value))} />
          <button disabled={amount <= 0} onClick={() => onRepay(amount)}>Repay</button>
        </>
      )}
    </div>
  );
}

function LoanList({ loans }: { loans: Loan[] }) {
  const [paid, setPaid] = useState<Record<string, number>>({});

  const handleRepay = (loanNo: string, amount: number) =>
    setPaid(prev => ({ ...prev, [loanNo]: (prev[loanNo] ?? 0) + amount }));

  if (loans.length === 0) return <p>No loans found.</p>;

  return (
    <ul>
      {loans.map(l => (
        <li key={l.loanNo}>
          <LoanCard loanNo={l.loanNo} outstanding={l.outstanding}
                    onRepay={amt => handleRepay(l.loanNo, amt)} />
        </li>
      ))}
    </ul>
  );
}
```

**Line by line**
- A component is a **function returning JSX**. JSX looks like HTML but compiles to `React.createElement(...)` calls.
- `{ loanNo, outstanding, onRepay }`: props are destructured. **Props are read-only.** A child never changes its props.
- `useState(0)`: returns `[currentValue, setter]`. Calling the setter **schedules a re-render** with the new value.
- `{expression}`: put JavaScript inside JSX.
- `cond ? <A/> : <B/>`: conditional rendering (or `cond && <A/>`).
- `<>...</>`: a **Fragment** groups elements without adding an extra `<div>`.
- `value={amount}` + `onChange`: a **controlled input**, where React state is the single source of truth.
- `onRepay`: a **callback prop**. The child tells the parent "something happened", which is how data flows **up**.
- `setPaid(prev => ({...prev, ...}))`: **functional update** + **new object**. Never mutate state (`paid[x] = 1` won't re-render). The `prev =>` form guarantees you build on the latest value.
- `key={l.loanNo}`: a stable unique key lets React match list items between renders. **Don't use the array index** if items can be reordered or removed.

**Data flow:** props go **down**, events go **up** (one-way data flow). When two siblings need the same data, **lift the state up** to their common parent.

@@ 3 | TOPIC | useEffect | Side effects, dependency array, cleanup, fetching data | MUST
# useEffect

`useEffect` runs **side effects** after render: fetching data, subscriptions, timers, changing the document title.

**Analogy:** after you rearrange your room (render), you might need to do follow-up chores (effects), like plugging the lamp back in. And before you rearrange again, you unplug it (cleanup).

```tsx
function MemberDetails({ memberId }: { memberId: number }) {
  const [member, setMember] = useState<Member | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const controller = new AbortController();          // lets us cancel the request
    setLoading(true);

    api.get<Member>(`/members/${memberId}`, { signal: controller.signal })
       .then(r => { setMember(r.data); setError(null); })
       .catch(e => { if (!axios.isCancel(e)) setError("Could not load member"); })
       .finally(() => setLoading(false));

    return () => controller.abort();                   // CLEANUP: cancel if memberId changes or we unmount
  }, [memberId]);                                      // re-run only when memberId changes

  if (loading) return <p>Loading…</p>;
  if (error) return <p role="alert">{error}</p>;
  return <h2>{member?.name}</h2>;
}
```

**Line by line**
- The effect function runs **after** React paints the screen.
- **Dependency array `[memberId]`**: re-run the effect only when `memberId` changes.
  - `[]`: run once after the first render (mount).
  - No array at all: run after **every** render (usually a bug).
- The **cleanup** function (`return () => ...`) runs before the next effect run and on unmount. Here it cancels the old request, so a slow old response can't overwrite newer data (a **race condition**).
- Three states (`loading`, `error`, `data`): always handle all three in the UI.
- In development, React **StrictMode** runs effects twice on mount, deliberately, to expose missing cleanups.

**Common mistakes**
- Missing dependencies: the effect uses stale values. Follow the ESLint `react-hooks/exhaustive-deps` rule.
- Objects or functions in the dependency array that are recreated every render: an infinite loop.
- Using an effect to compute derived data: just compute it during render instead (`const total = items.reduce(...)`).
- In real apps, use **TanStack Query** for server data instead of hand-written fetch effects (see the React Ecosystem topics).

@@ 3 | TOPIC | useRef, useMemo, useCallback, Context and useReducer | The other essential hooks | MUST
# useRef, useMemo, useCallback, Context and useReducer

## useRef: a box that survives re-renders without causing them
```tsx
function SearchBox() {
  const inputRef = useRef<HTMLInputElement>(null);
  const renders = useRef(0);
  renders.current++;                                   // changing .current does NOT re-render

  return <>
    <input ref={inputRef} />
    <button onClick={() => inputRef.current?.focus()}>Focus</button>
  </>;
}
```
- `ref={inputRef}`: React puts the real DOM element in `inputRef.current`, for focusing, scrolling or measuring.
- It also stores mutable values (timer IDs, previous values) that shouldn't trigger a render.

## useMemo and useCallback: remember results between renders
```tsx
function Portfolio({ loans, onSelect }: { loans: Loan[]; onSelect: (id: number) => void }) {
  const total = useMemo(
    () => loans.reduce((s, l) => s + l.outstanding, 0),   // recalculated only when loans changes
    [loans]);

  const handleClick = useCallback((id: number) => onSelect(id), [onSelect]);  // same function each render

  return <LoanTable loans={loans} onRowClick={handleClick} total={total} />;
}
const LoanTable = React.memo(LoanTableImpl);   // skip re-render if the props are the same
```
- `useMemo(fn, deps)`: caches a **computed value**.
- `useCallback(fn, deps)`: caches a **function** so that its identity stays the same, which matters when it is passed to a `React.memo` child or used as an effect dependency.
- `React.memo`: re-renders the child only if its props changed (shallow comparison).
- **Don't overuse them.** They cost memory and complexity. Use them for expensive calculations or to stop real re-render problems you have measured.
- **Analogy:** useMemo is writing a long calculation's answer on a sticky note, and only redoing it when the inputs change.

## Context: share data without "prop drilling"
```tsx
type Auth = { user: string | null; roles: string[]; logout: () => void };
const AuthContext = createContext<Auth | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<string | null>(null);
  const value = useMemo(() => ({ user, roles: [], logout: () => setUser(null) }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {                       // a custom hook for easy, safe access
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}

// Anywhere below the provider:
const { user, logout } = useAuth();
```
- **Prop drilling:** passing a prop through 5 components that don't need it, just to reach the 6th. Context solves this.
- `Provider value={...}`: every component inside can read it with `useContext`.
- `useMemo` on the value: otherwise a new object every render re-renders every consumer.
- Use it for app-wide, rarely changing data: the logged-in user, theme, language. For frequently changing global state, use a state library (Zustand/Redux).
- **Analogy:** context is the building's PA system. Instead of passing a note person to person, you announce it once and anyone listening hears it.

## useReducer: complex state with clear actions
```tsx
type State = { items: CartItem[]; total: number };
type Action = { type: "add"; item: CartItem } | { type: "remove"; id: number } | { type: "clear" };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "add":    return { items: [...state.items, action.item], total: state.total + action.item.amount };
    case "remove": {
      const items = state.items.filter(i => i.id !== action.id);
      return { items, total: items.reduce((s, i) => s + i.amount, 0) };
    }
    case "clear":  return { items: [], total: 0 };
  }
}

const [state, dispatch] = useReducer(reducer, { items: [], total: 0 });
dispatch({ type: "add", item: { id: 1, amount: 500 } });
```
- The **reducer** is a pure function: (old state, action) → new state. All the update logic is in one place and easy to test.
- `dispatch(action)`: describe **what happened**, and the reducer decides how the state changes.
- Use it when the state has many related fields or the next state depends on complex rules. (Redux works on the same idea.)

## Rules of hooks
1. Call hooks only at the **top level** (not inside if/loops/nested functions). React relies on call order.
2. Call them only from React components or custom hooks (functions named `useSomething`).

@@ 3 | TOPIC | Custom Hooks | Reusing stateful logic across components | SHOULD
# Custom hooks

A **custom hook** is a function starting with `use` that uses other hooks. It lets you **reuse logic** (not UI) across components.

**Analogy:** a recipe card you can hand to any cook. Every component that uses the hook gets its **own** copy of the state, just as each cook makes their own dish from the same recipe.

```tsx
// Debounce: wait until the user stops typing
function useDebounce<T>(value: T, delay = 400): T {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const t = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(t);              // the user typed again: cancel the old timer
  }, [value, delay]);
  return debounced;
}

// Local storage-backed state
function useLocalStorage<T>(key: string, initial: T) {
  const [value, setValue] = useState<T>(() => {
    try { const raw = localStorage.getItem(key); return raw ? JSON.parse(raw) as T : initial; }
    catch { return initial; }
  });
  useEffect(() => {
    try { localStorage.setItem(key, JSON.stringify(value)); } catch { /* storage unavailable */ }
  }, [key, value]);
  return [value, setValue] as const;
}

// Usage
function MemberSearch() {
  const [text, setText] = useState("");
  const query = useDebounce(text, 400);
  const [pageSize, setPageSize] = useLocalStorage("pageSize", 20);
  const { data } = useQuery({ queryKey: ["members", query, pageSize],
                              queryFn: () => searchMembers(query, pageSize),
                              enabled: query.length >= 2 });
  return <input value={text} onChange={e => setText(e.target.value)} placeholder="Search name/phone" />;
}
```
**Line by line**
- `useDebounce`: each keystroke resets a 400 ms timer. Only when the typing pauses does `debounced` update, so the API is called once instead of 10 times.
- The cleanup `clearTimeout(t)`: cancels the pending update when the value changes again.
- `useState(() => ...)`: **lazy initial state**. The function runs only on the first render, so we don't read localStorage on every render.
- `try/catch` around localStorage: it can throw in private mode or when blocked.
- `as const`: returns a typed tuple `[T, setter]`, like `useState`.
- `enabled: query.length >= 2`: don't search for one character.

@@ 2 | MODULE | React Ecosystem | Routing, server state, forms, UI libraries | MUST
The libraries real React projects use: **React Router**, **TanStack Query**, **React Hook Form + Zod** and a **UI kit**.

@@ 3 | TOPIC | Routing and Protected Routes | React Router v6 routes, params, navigation, role guards | MUST
# Routing and protected routes

**React Router** maps URLs to components without full page reloads, which makes the app an SPA (Single Page Application).

```tsx
// main.tsx
const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  {
    element: <RequireAuth />,                       // guard for everything below
    children: [
      { path: "/", element: <Dashboard /> },
      { path: "/members", element: <MemberList /> },
      { path: "/members/:id", element: <MemberDetails /> },
      {
        element: <RequireRole role="MANAGER" />,
        children: [{ path: "/loans/approvals", element: <Approvals /> }],
      },
    ],
  },
  { path: "*", element: <NotFound /> },
]);
createRoot(document.getElementById("root")!).render(<RouterProvider router={router} />);

// RequireAuth.tsx
function RequireAuth() {
  const { user } = useAuth();
  const location = useLocation();
  if (!user) return <Navigate to="/login" replace state={{ from: location }} />;
  return <Outlet />;                                // render the matched child route
}

function RequireRole({ role }: { role: string }) {
  const { roles } = useAuth();
  return roles.includes(role) ? <Outlet /> : <p>403: Not allowed</p>;
}

// Reading params and navigating
function MemberDetails() {
  const { id } = useParams();                       // "/members/42" -> id = "42"
  const navigate = useNavigate();
  return <button onClick={() => navigate("/members")}>Back</button>;
}
```
**Line by line**
- `createBrowserRouter([...])`: the route table.
- A route with only `element` and `children` (no path) is a **layout/guard route**. Its element wraps all its children.
- `RequireAuth`: if nobody is logged in, `<Navigate>` redirects to /login. `state={{from}}` remembers where the user wanted to go, so we can send them back after login.
- `replace`: replaces the history entry, so "Back" doesn't return to the protected page.
- `<Outlet />`: "render the matching child route here".
- `:id` in the path → `useParams()` gives `{ id: "42" }` (always a **string**, so convert with `Number(id)`).
- `path: "*"`: catch-all for 404.
- **Important:** frontend guards are for **user experience only**. Anyone can edit browser code, so **the backend must enforce security** on every API call.

@@ 3 | TOPIC | Server State with TanStack Query | Queries, mutations, caching, invalidation | MUST
# Server state with TanStack Query

**Server state** (data from the API) is different from UI state: it is shared, can go stale, and needs loading/error handling, caching and refetching. **TanStack Query** (React Query) handles all of this.

**Analogy:** a smart personal assistant. You ask "get me the member list". They remember the answer, give it to you instantly next time, quietly check for updates in the background, and throw out the old copy when you tell them something changed.

```tsx
// main.tsx
const queryClient = new QueryClient({ defaultOptions: { queries: { staleTime: 30_000, retry: 1 } } });
<QueryClientProvider client={queryClient}><App /></QueryClientProvider>

// Reading data
function MemberList({ page }: { page: number }) {
  const { data, isPending, isError, error } = useQuery({
    queryKey: ["members", page],                       // cache key: changes -> new fetch
    queryFn: () => api.get<Page<Member>>(`/members?page=${page}`).then(r => r.data),
    placeholderData: keepPreviousData,                 // keep the old page visible while the next loads
  });

  if (isPending) return <Spinner />;
  if (isError) return <p role="alert">{(error as Error).message}</p>;
  return <MemberTable rows={data.content} />;
}

// Changing data
function NewMemberForm() {
  const qc = useQueryClient();
  const mutation = useMutation({
    mutationFn: (m: MemberRequest) => api.post<Member>("/members", m).then(r => r.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["members"] }); // mark the list stale -> refetch
      toast.success("Member created");
    },
    onError: (e) => toast.error("Could not save"),
  });

  return <button disabled={mutation.isPending}
                 onClick={() => mutation.mutate({ name: "Rahim", phone: "01712345678" })}>
           {mutation.isPending ? "Saving…" : "Save"}
         </button>;
}
```
**Line by line**
- `QueryClient`: holds the cache. `staleTime: 30_000` means the data counts as fresh for 30 s (no refetch during that time).
- `queryKey: ["members", page]`: identifies the data. Different pages are cached separately. When the key changes, the data is fetched automatically.
- `queryFn`: how to fetch it. It must return a promise or throw on error.
- `isPending` / `isError` / `data`: you get the loading and error states for free.
- `keepPreviousData`: no flicker to a spinner when paging.
- `useMutation`: for POST/PUT/DELETE. `mutate(variables)` runs it.
- `invalidateQueries(["members"])`: every query whose key starts with "members" is marked stale and refetched, so the list shows the new member.
- `mutation.isPending`: disable the button to prevent double submits.

Also free: deduplication (10 components asking for the same key means 1 request), refetch on window focus, retries, and pagination/infinite queries.

@@ 3 | TOPIC | Forms with React Hook Form and Zod | Performant forms with schema validation | MUST
# Forms with React Hook Form + Zod

- **React Hook Form (RHF):** manages form state with minimal re-renders.
- **Zod:** defines a **validation schema** once, and TypeScript types are generated from it.

**Analogy:** Zod is the official form rulebook ("phone must be 11 digits"). RHF is the clerk who collects the form, checks it against the rulebook and points out each mistake.

```tsx
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

const memberSchema = z.object({
  name: z.string().trim().min(2, "Name is too short").max(100),
  phone: z.string().regex(/^01[3-9]\d{8}$/, "Invalid BD mobile number"),
  nationalId: z.string().regex(/^(\d{10}|\d{13}|\d{17})$/, "NID must be 10, 13 or 17 digits"),
  loanAmount: z.coerce.number().min(1000, "Minimum 1,000").max(500000),
});
type MemberForm = z.infer<typeof memberSchema>;            // the TS type comes FROM the schema

function MemberFormPage() {
  const { register, handleSubmit, formState: { errors, isSubmitting }, reset } =
    useForm<MemberForm>({ resolver: zodResolver(memberSchema) });

  const onSubmit = async (values: MemberForm) => {
    await api.post("/members", values);
    reset();                                               // clear the form after success
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <label htmlFor="name">Name</label>
      <input id="name" {...register("name")} aria-invalid={!!errors.name} />
      {errors.name && <span role="alert">{errors.name.message}</span>}

      <label htmlFor="phone">Phone</label>
      <input id="phone" {...register("phone")} />
      {errors.phone && <span role="alert">{errors.phone.message}</span>}

      <label htmlFor="amt">Loan amount</label>
      <input id="amt" type="number" {...register("loanAmount")} />
      {errors.loanAmount && <span role="alert">{errors.loanAmount.message}</span>}

      <button type="submit" disabled={isSubmitting}>{isSubmitting ? "Saving…" : "Save"}</button>
    </form>
  );
}
```
**Line by line**
- `z.object({...})`: the rules. Each field has its own validators and error messages.
- `z.coerce.number()`: HTML inputs always give strings, so coerce converts "5000" to 5000.
- `z.infer<typeof memberSchema>`: **one source of truth**. You never write the type separately, so it can't drift from the rules.
- `zodResolver(memberSchema)`: connects Zod to RHF.
- `{...register("name")}`: spreads `name`, `onChange`, `onBlur` and `ref` onto the input. RHF tracks the input **without** a React state per keystroke (uncontrolled), so it is fast.
- `handleSubmit(onSubmit)`: validates first and calls `onSubmit` only if everything is valid.
- `errors.name?.message`: the message from the schema.
- `aria-invalid` and `role="alert"`: screen readers announce the errors.
- `isSubmitting`: true while the async `onSubmit` runs, so the button is disabled (no double submits).
- **The server must validate again.** Client validation is for convenience, not security.

@@ 3 | TOPIC | UI Libraries, Tables and Charts | MUI/Ant Design, data tables, charts, styling options | NICE
# UI libraries, tables and charts

Admin/MIS apps (like PKSF's) are mostly **forms, tables and dashboards**. Don't build everything from scratch.

| Need | Popular choices |
|---|---|
| Component kit | **MUI**, **Ant Design** (great for admin panels), Chakra, shadcn/ui |
| Styling | Tailwind CSS, CSS Modules, styled-components |
| Data tables | **TanStack Table**, MUI DataGrid, AG Grid |
| Charts | Recharts, Chart.js, ECharts |
| Dates | date-fns, Day.js |
| Global UI state | Zustand, Redux Toolkit |

```tsx
// Ant Design table with server-side pagination
import { Table } from "antd";

function LoanTable() {
  const [page, setPage] = useState(1);
  const { data, isFetching } = useQuery({
    queryKey: ["loans", page],
    queryFn: () => api.get<Page<Loan>>(`/loans?page=${page - 1}&size=20`).then(r => r.data),
  });

  return (
    <Table<Loan>
      rowKey="id"
      loading={isFetching}
      dataSource={data?.content}
      columns={[
        { title: "Loan No", dataIndex: "loanNo" },
        { title: "Member", dataIndex: "memberName" },
        { title: "Outstanding", dataIndex: "outstanding", align: "right",
          render: (v: number) => v.toLocaleString("en-BD", { minimumFractionDigits: 2 }) },
      ]}
      pagination={{ current: page, pageSize: 20, total: data?.totalElements, onChange: setPage }}
    />
  );
}
```
**Line by line**
- `rowKey="id"`: a unique key per row (the same idea as React `key`).
- `columns`: what to show. `render` formats the cell (money with 2 decimals, right-aligned, which is easier to compare).
- `page - 1`: Ant Design pages start at 1, while Spring pages start at 0. A classic off-by-one bug.
- **Server-side pagination**: only 20 rows travel over the network, never 100,000.
- `total` from the API lets the table show the right number of pages.

**Charts tip:** for a PAR trend, use a line chart over months. To compare POs, use a bar chart. Always label units (BDT, %).

@@ 2 | TOPIC | React Advanced and Performance | Rendering, code splitting, error boundaries, security, build | SHOULD
# React advanced and performance

## How rendering works
1. State or props change → React **re-runs** the component function (render).
2. It builds a new **virtual DOM** tree and **diffs** it with the previous one (reconciliation).
3. Only the **real DOM changes** that are actually needed are applied (commit).

**Analogy:** a spot-the-difference puzzle. React compares the old and new pictures and repaints only the differences instead of the whole wall.

A re-render happens when: the component's state changes, its parent re-renders, or a context it uses changes.

## Code splitting with lazy loading
```tsx
const Reports = lazy(() => import("./pages/Reports"));    // separate JS chunk

<Suspense fallback={<Spinner />}>
  <Reports />
</Suspense>
```
- The Reports code downloads **only when the user opens it**, so the first page loads faster.
- `Suspense fallback`: what to show while the chunk loads.

## Error boundaries
```tsx
import { ErrorBoundary } from "react-error-boundary";

<ErrorBoundary fallback={<p>Something went wrong in this panel.</p>}>
  <PortfolioChart />
</ErrorBoundary>
```
- If `PortfolioChart` throws during render, only that panel shows the fallback, and the whole app doesn't go blank. (Error boundaries catch **render** errors, not async errors in event handlers.)

## Performance checklist
- Keep state **as close as possible** to where it is used.
- Virtualize long lists (`@tanstack/react-virtual`): render only the visible rows.
- `React.memo` / `useMemo` / `useCallback` only for measured problems.
- Debounce search inputs.
- Use the React DevTools **Profiler** to see what re-renders and why.

## Frontend security
- React escapes `{text}` automatically. **Avoid `dangerouslySetInnerHTML`** (sanitize with DOMPurify if you must use it).
- Never store secrets in frontend code: everything in the bundle is public.
- Prefer HttpOnly cookies / in-memory tokens over localStorage for tokens.
- The backend must always re-check permissions.

## Build and deploy (Vite)
```bash
npm create vite@latest loan-web -- --template react-ts
npm run dev          # dev server with hot reload
npm run build        # optimized static files in dist/
```
- `dist/` is just static HTML/JS/CSS. Serve it with Nginx (in Docker) and proxy `/api` to the backend. Configure Nginx to fall back to `index.html` for client-side routes (`try_files $uri /index.html;`).

@@ 2 | TOPIC | Phase 8 — Interview Questions | Quick Q&A revision for frontend | MUST
# Phase 8 interview questions

**Q. let vs const vs var?**
A. var is function-scoped and hoisted. let and const are block-scoped. const can't be reassigned (but objects stay mutable).

**Q. == vs ===?**
A. `==` converts types before comparing. `===` compares type and value. Always use `===`.

**Q. What is a closure?**
A. A function that remembers the variables of the scope where it was created, even after that scope has finished.

**Q. Explain the event loop output of: log 1, setTimeout 2, promise 3, log 4.**
A. 1, 4, 3, 2. Sync code first, then microtasks (promises), then macrotasks (timers).

**Q. Props vs state?**
A. Props are inputs passed from the parent and are read-only. State is the component's own data, and changing it with the setter triggers a re-render.

**Q. Why do lists need keys?**
A. So that React can match items between renders and update, reorder or remove the right DOM nodes efficiently. Use stable unique IDs, not indexes.

**Q. What does the useEffect dependency array do?**
A. It controls when the effect re-runs: `[]` once after mount, `[x]` when x changes, none after every render. The cleanup runs before the next run and on unmount.

**Q. useMemo vs useCallback?**
A. useMemo caches a computed value. useCallback caches a function reference. Both recompute when their dependencies change.

**Q. What is prop drilling and how do you avoid it?**
A. Passing props through many layers that don't need them. Avoid it with Context, composition or a state library.

**Q. Controlled vs uncontrolled inputs?**
A. Controlled: the value comes from React state with onChange. Uncontrolled: the DOM holds the value and you read it through a ref (React Hook Form uses this for performance).

**Q. Why use TanStack Query instead of useEffect + fetch?**
A. It provides caching, deduplication, background refetching, retries, loading/error states and invalidation after mutations, with far less hand-written code.

**Q. How do you protect routes in React?**
A. A guard component that checks the auth state and redirects with `<Navigate>`. The backend must still enforce authorization, because frontend checks are only UX.

**Q. What is the virtual DOM?**
A. An in-memory tree of UI elements. React diffs the new tree with the old one and applies only the needed changes to the real DOM.

**Q. interface vs type in TypeScript?**
A. Both describe shapes. Interfaces can be extended or merged. Types can also express unions, intersections and mapped types.
