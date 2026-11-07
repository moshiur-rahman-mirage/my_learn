import { Marked } from 'marked'
import DOMPurify from 'dompurify'
import TurndownService from 'turndown'

/** GitHub-flavoured Markdown (tables, task lists, ~~strike~~, autolinks); single newlines become <br>. */
const marked = new Marked({ gfm: true, breaks: true })

// links in rendered notes open in a new tab
DOMPurify.addHook('afterSanitizeAttributes', (node) => {
  if (node.tagName === 'A' && node.getAttribute('href')) {
    node.setAttribute('target', '_blank')
    node.setAttribute('rel', 'noopener noreferrer')
  }
})

/** Markdown → sanitized HTML, safe to put in v-html (raw HTML in the Markdown is cleaned, scripts dropped). */
export function renderMarkdown(md: string): string {
  const html = marked.parse(md ?? '', { async: false }) as string
  return DOMPurify.sanitize(html, { ADD_ATTR: ['target'] })
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
