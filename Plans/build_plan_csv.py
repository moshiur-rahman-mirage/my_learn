"""Build plan.csv from the Markdown sources in guide/.

Each guide/*.md file holds rows. A row starts with a marker line:

    @@ level | TYPE | name | description | PRIORITY

and everything after it (until the next marker) is that row's Markdown content.
level: 1 = phase, 2 = section, 3 = topic.  TYPE: MODULE or TOPIC.  PRIORITY: MUST / SHOULD / NICE or empty.

Run:  python build_plan_csv.py [guide_dir] [out.csv]   (defaults: guide plan.csv)
"""
import csv
import pathlib
import re
import sys

HERE = pathlib.Path(__file__).parent
GUIDE = HERE / (sys.argv[1] if len(sys.argv) > 1 else "guide")
OUT = HERE / (sys.argv[2] if len(sys.argv) > 2 else "plan.csv")
FIELDS = ["level", "type", "name", "description", "priority", "completed", "content"]
MARKER = re.compile(r"^@@\s*(\d)\s*\|\s*(MODULE|TOPIC)\s*\|(.*)$")

rows, problems = [], []

for path in sorted(GUIDE.glob("*.md")):
    current = None
    for lineno, line in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
        m = MARKER.match(line)
        if m:
            parts = [p.strip() for p in m.group(3).split("|")]
            parts += [""] * (3 - len(parts))
            current = {
                "level": m.group(1), "type": m.group(2), "name": parts[0],
                "description": parts[1], "priority": parts[2].upper(),
                "completed": "FALSE" if m.group(2) == "TOPIC" else "",
                "content": [], "src": f"{path.name}:{lineno}",
            }
            rows.append(current)
        elif current is not None:
            current["content"].append(line)
        elif line.strip():
            problems.append(f"{path.name}:{lineno}: text before first @@ marker")

for r in rows:
    r["content"] = "\n".join(r["content"]).strip()
    body = r["content"]
    if body.count("```") % 2:
        problems.append(f"{r['src']} '{r['name']}': unbalanced ``` code fence")
    if r["priority"] not in ("", "MUST", "SHOULD", "NICE"):
        problems.append(f"{r['src']} '{r['name']}': bad priority {r['priority']}")
    # Every topic that contains code must also contain explanation text.
    prose = re.sub(r"```.*?```", "", body, flags=re.S)
    if "```" in body and len(prose.strip()) < 200:
        problems.append(f"{r['src']} '{r['name']}': code without enough explanation")

# A tick only makes sense on a leaf topic: the next row must not be deeper.
for i, r in enumerate(rows):
    nxt = rows[i + 1] if i + 1 < len(rows) else None
    if r["type"] == "TOPIC" and nxt and int(nxt["level"]) > int(r["level"]):
        problems.append(f"{r['src']} '{r['name']}': TOPIC has children")

if problems:
    print("\n".join(problems))
    sys.exit(1)

with OUT.open("w", encoding="utf-8-sig", newline="") as f:
    w = csv.DictWriter(f, fieldnames=FIELDS, extrasaction="ignore")
    w.writeheader()
    w.writerows(rows)

topics = sum(r["type"] == "TOPIC" for r in rows)
print(f"wrote {len(rows)} rows ({topics} topics) to {OUT}")
