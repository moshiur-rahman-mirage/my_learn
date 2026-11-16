import csv
import pathlib
import re

src_path = pathlib.Path(r'D:\mylearn\Plans\PKSF_FullStack_Study_Material.md')
text = src_path.read_text(encoding='utf-8')
text = text.replace('\ufeff', '')
text = text.replace('PKSF Full-stack Software Engineer — Study Material', '')
text = text.replace('Page 1 of 63', '')
text = text.replace('Page 2 of 63', '')
text = text.replace('Page 5 of 63', '')
text = text.replace('Page 6 of 63', '')
text = text.replace('Page 7 of 63', '')
text = text.replace('Page 14 of 63', '')
text = text.replace('Page 15 of 63', '')
text = text.replace('Page 27 of 63', '')
text = text.replace('Page 28 of 63', '')
text = text.replace('Page 29 of 63', '')
text = text.replace('Page 31 of 63', '')
text = text.replace('Page 33 of 63', '')
text = text.replace('Page 36 of 63', '')
text = text.replace('Page 38 of 63', '')
text = text.replace('Page 39 of 63', '')
text = text.replace('Page 41 of 63', '')
text = text.replace('Page 42 of 63', '')
text = text.replace('Page 44 of 63', '')
text = text.replace('Page 48 of 63', '')
text = text.replace('Page 50 of 63', '')
text = text.replace('Page 52 of 63', '')
text = text.replace('Page 56 of 63', '')
text = text.replace('Page 57 of 63', '')
text = text.replace('Page 58 of 63', '')
text = text.replace('Page 59 of 63', '')
text = text.replace('Page 60 of 63', '')
text = text.replace('Page 61 of 63', '')
text = text.replace('Page 63 of 63', '')

sections = []
current = None
for raw in text.splitlines():
    line = raw.rstrip()
    m = re.match(r'^(#{1,6})\s+(.*)$', line.strip())
    if m:
        if current is not None:
            sections.append(current)
        title = re.sub(r'\*\*', '', m.group(2)).strip()
        if title in {'Software Engineer (Full-stack) Study Material — PKSF Recruitment Preparation', 'Contents', 'How to Use This Material'}:
            current = None
            continue
        current = {'level': len(m.group(1)), 'name': title, 'body': []}
        continue
    if current is None:
        continue
    s = line.strip()
    if not s:
        current['body'].append('')
        continue
    if s.startswith('Page ') or s == 'PKSF Full-stack Software Engineer — Study Material':
        continue
    if re.fullmatch(r'[-*_]{3,}', s):
        continue
    if re.fullmatch(r'\|?\s*---+\s*\|?', s):
        continue
    current['body'].append(line)
if current is not None:
    sections.append(current)


def convert_code(match):
    code = match.group(1)
    code = re.sub(r'\s+', ' ', code)
    code = code.strip()
    if re.search(r'(?i)\bSELECT\b|\bINSERT\b|\bUPDATE\b|\bDELETE\b|\bCREATE\b|\bWITH\b', code):
        lang = 'sql'
    elif re.search(r'(?i)\bpublic\s+class\b|\bSystem\.out\b|\b@RestController\b|\bListNode\b|\bStringBuilder\b|\bnew\s+BigDecimal\b', code):
        lang = 'java'
    elif re.search(r'(?i)\bconst\s+|\blet\s+|\bfunction\s+|=>|useState|Promise', code):
        lang = 'javascript'
    elif re.search(r'(?i)\bGET\b|\bPOST\b|\bPUT\b|\bDELETE\b|/api/', code):
        lang = 'http'
    else:
        lang = 'text'
    return '\n```' + lang + '\n' + code + '\n```\n'

rows = []
for sec in sections:
    level = sec['level']
    row_level = 1 if level == 1 else (2 if level == 2 else 3)
    row_type = 'MODULE' if row_level <= 2 else 'TOPIC'
    body = '\n'.join(sec['body'])
    body = re.sub(r'<mark>(.*?)</mark>', convert_code, body, flags=re.S)
    body = re.sub(r'\n{3,}', '\n\n', body)
    body = body.strip()
    rows.append({
        'level': str(row_level),
        'type': row_type,
        'name': re.sub(r'\*\*', '', sec['name']).strip(),
        'description': '',
        'priority': '',
        'completed': 'FALSE',
        'content': body,
    })

out_path = pathlib.Path(r'D:\mylearn\Plans\plan.csv')
with out_path.open('w', encoding='utf-8-sig', newline='') as f:
    writer = csv.DictWriter(f, fieldnames=['level', 'type', 'name', 'description', 'priority', 'completed', 'content'])
    writer.writeheader()
    writer.writerows(rows)

print(f'wrote {len(rows)} rows to {out_path}')
