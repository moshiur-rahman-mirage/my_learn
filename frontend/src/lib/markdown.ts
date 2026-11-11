import { Marked, type TokenizerAndRendererExtension } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js/lib/common'
import katex from 'katex'
import 'katex/dist/katex.min.css'
import DOMPurify from 'dompurify'
import TurndownService from 'turndown'

function escapeHtml(s: string): string {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')
}

/** How [[links]] are matched to items: ignoring case and spacing (the server uses the same rule). */
export function wikiKey(name: string): string {
  return name.trim().replace(/\s+/g, ' ').toLowerCase()
}

// --- math: $inline$ and $$display$$ (KaTeX) ---------------------------------------------------

function tex(source: string, displayMode: boolean): string {
  return katex.renderToString(source, { displayMode, throwOnError: false, output: 'html', strict: false, trust: false })
}

const mathBlock: TokenizerAndRendererExtension = {
  name: 'mathBlock',
  level: 'block',
  start: (src) => src.match(/^\$\$/m)?.index,
  tokenizer(src) {
    const m = /^\$\$([\s\S]+?)\$\$[ \t]*(?:\n|$)/.exec(src)
    if (m) return { type: 'mathBlock', raw: m[0], text: m[1].trim() }
  },
  renderer: (t) => `<div class="math-display">${tex(t.text, true)}</div>\n`,
}

const mathInline: TokenizerAndRendererExtension = {
  name: 'mathInline',
  level: 'inline',
  start: (src) => {
    const i = src.indexOf('$')
    return i < 0 ? undefined : i
  },
  tokenizer(src) {
    // $$…$$ inside a paragraph is shown as a display formula
    let m = /^\$\$([^$]+?)\$\$/.exec(src)
    if (m) return { type: 'mathInline', raw: m[0], text: m[1].trim(), display: true }
    // $x$: no space just inside the dollars and no digit right after ("$5 and $10" stays text)
    m = /^\$(?![\s$])((?:\\\$|[^$\n])+?)(?<![\s\\])\$(?!\d)/.exec(src)
    if (m) return { type: 'mathInline', raw: m[0], text: m[1], display: false }
  },
  renderer: (t) => tex(t.text, Boolean(t.display)),
}

// --- [[links]] between notes -----------------------------------------------------------------

/** Set for the duration of one render: does a [[name]] point to an existing item? */
let wikiExists: ((name: string) => boolean) | null = null

const wikiLink: TokenizerAndRendererExtension = {
  name: 'wikiLink',
  level: 'inline',
  start: (src) => {
    const i = src.indexOf('[[')
    return i < 0 ? undefined : i
  },
  tokenizer(src) {
    const m = /^\[\[([^[\]\n|]{1,200})(?:\|([^[\]\n]{0,200}))?\]\]/.exec(src)
    if (m) return { type: 'wikiLink', raw: m[0], target: m[1].trim(), label: (m[2] || m[1]).trim() }
  },
  renderer(t) {
    const exists = wikiExists ? wikiExists(t.target) : true
    const title = exists ? `Open “${t.target}”` : `No item named “${t.target}” in this plan`
    return `<a class="wikilink${exists ? '' : ' wikilink-missing'}" data-wiki="${escapeHtml(t.target)}" role="link" tabindex="0" title="${escapeHtml(title)}">${escapeHtml(t.label)}</a>`
  },
}

/**
 * GitHub-flavoured Markdown (tables, task lists, ~~strike~~, autolinks); single newlines become <br>.
 * Plus: highlighted code blocks, $math$, [[links]]; ```mermaid blocks are left for renderDiagrams().
 */
const marked = new Marked(
  { gfm: true, breaks: true },
  markedHighlight({
    emptyLangClass: 'hljs',
    langPrefix: 'hljs language-',
    highlight(code, lang) {
      if (lang && lang !== 'mermaid' && hljs.getLanguage(lang)) {
        return hljs.highlight(code, { language: lang, ignoreIllegals: true }).value
      }
      return escapeHtml(code)
    },
  }),
)
marked.use({ extensions: [mathBlock, mathInline, wikiLink] })

// links in rendered notes open in a new tab
DOMPurify.addHook('afterSanitizeAttributes', (node) => {
  if (node.tagName === 'A' && node.getAttribute('href')) {
    node.setAttribute('target', '_blank')
    node.setAttribute('rel', 'noopener noreferrer')
  }
})

export interface RenderOptions {
  /** Marks [[links]] to names that don't exist as missing. */
  wikiExists?: (name: string) => boolean
}

/** Markdown → sanitized HTML, safe to put in v-html (raw HTML in the Markdown is cleaned, scripts dropped). */
export function renderMarkdown(md: string, options: RenderOptions = {}): string {
  wikiExists = options.wikiExists ?? null
  try {
    const html = marked.parse(md ?? '', { async: false }) as string
    return DOMPurify.sanitize(html, { ADD_ATTR: ['target'] })
  } finally {
    wikiExists = null
  }
}

/** Markdown → HTML the rich-text editor understands (its task lists use their own markup). */
export function markdownToEditorHtml(md: string): string {
  const doc = new DOMParser().parseFromString(renderMarkdown(md), 'text/html')
  doc.querySelectorAll('ul').forEach((ul) => {
    const items = Array.from(ul.children).filter((c) => c.tagName === 'LI')
    const isTaskList = items.length > 0 && items.every((li) => li.querySelector(':scope > input[type=checkbox], :scope > p > input[type=checkbox]'))
    if (!isTaskList) return
    ul.setAttribute('data-type', 'taskList')
    items.forEach((li) => {
      const box = li.querySelector('input[type=checkbox]') as HTMLInputElement
      li.setAttribute('data-type', 'taskItem')
      li.setAttribute('data-checked', String(box.checked || box.hasAttribute('checked')))
      box.remove()
    })
  })
  return doc.body.innerHTML
}

const turndown = new TurndownService({
  headingStyle: 'atx',
  codeBlockStyle: 'fenced',
  bulletListMarker: '-',
  emDelimiter: '*',
  hr: '---',
})
turndown.keep(['mark', 'u'])
turndown.addRule('strike', {
  filter: ['s', 'del'],
  replacement: (content) => `~~${content}~~`,
})
// rich-text editor checklist item: <li data-type="taskItem" data-checked="true"><label>…</label><div>text</div></li>
turndown.addRule('taskItem', {
  filter: (node) => node.nodeName === 'LI' && node.getAttribute('data-type') === 'taskItem',
  replacement: (content, node) => {
    const checked = (node as HTMLElement).getAttribute('data-checked') === 'true'
    const text = content.replace(/^\n+/, '').replace(/\n+$/, '').replace(/\n/gm, '\n  ')
    return `- [${checked ? 'x' : ' '}] ${text}\n`
  },
})
turndown.remove((node) => node.nodeName === 'LABEL' && node.parentElement?.getAttribute('data-type') === 'taskItem')
// code blocks: keep the language class TipTap writes (language-java → ```java)
turndown.addRule('fencedCode', {
  filter: (node) => node.nodeName === 'PRE' && node.firstChild?.nodeName === 'CODE',
  replacement: (_content, node) => {
    const code = node.firstChild as HTMLElement
    const lang = (code.getAttribute('class') ?? '').match(/language-(\S+)/)?.[1] ?? ''
    const text = code.textContent ?? ''
    const fence = text.includes('```') ? '~~~' : '```'
    return `\n\n${fence}${lang}\n${text.replace(/\n$/, '')}\n${fence}\n\n`
  },
})

/** Rich-text HTML → Markdown (for switching an existing note to Markdown). */
export function htmlToMarkdown(html: string): string {
  return turndown.turndown(html ?? '').trim()
}
