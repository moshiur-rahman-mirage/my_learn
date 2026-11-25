/**
 * The note viewer that runs inside the app's WebView. It uses the DESKTOP's own renderer
 * (frontend/src/lib/markdown.ts and diagrams.ts), so a note looks the same on both devices:
 * GitHub Markdown, highlighted code, KaTeX math, Mermaid diagrams and [[links]].
 * Built into assets/renderer/note-viewer.html by scripts/build-renderer.mjs: no network needed.
 *
 * Messages: the app calls window.renderNote(payload); the page posts
 * {type: 'ready' | 'height' | 'wiki' | 'link' | 'markdown', ...} back through ReactNativeWebView.postMessage.
 */
import DOMPurify from 'dompurify'
import hljs from 'highlight.js/lib/common'
import { htmlToMarkdown, renderMarkdown } from '../../frontend/src/lib/markdown'
import { renderDiagrams } from '../../frontend/src/lib/diagrams'
import './note.css'

interface Payload {
  content: string
  format: 'HTML' | 'MARKDOWN'
  dark: boolean
  /** wikiKey()s of item names in the plan, to mark [[links]] to missing items */
  names: string[]
  fontScale?: number
}

declare global {
  interface Window {
    ReactNativeWebView?: { postMessage(message: string): void }
    renderNote: (p: Payload) => Promise<void>
    convertToMarkdown: (html: string) => void
  }
}

function post(message: object) {
  window.ReactNativeWebView?.postMessage(JSON.stringify(message))
}

/** Same rule as the app and the server: trimmed, single spaces, lower case. */
function wikiKey(name: string): string {
  return name.trim().replace(/[ \t\n\x0B\f\r]+/g, ' ').toLowerCase()
}

const root = document.getElementById('note')!

window.renderNote = async (p: Payload) => {
  document.documentElement.classList.toggle('dark', p.dark)
  document.documentElement.style.fontSize = `${16 * (p.fontScale ?? 1)}px`
  const names = new Set(p.names)
  if (p.format === 'MARKDOWN') {
    root.innerHTML = renderMarkdown(p.content, { wikiExists: (n) => names.has(wikiKey(n)) })
  } else {
    // rich-text notes from the desktop editor: sanitized, code blocks highlighted like Markdown ones
    root.innerHTML = DOMPurify.sanitize(p.content)
    root.querySelectorAll<HTMLElement>('pre code').forEach((code) => {
      const lang = /language-(\S+)/.exec(code.className)?.[1]
      if (lang === 'mermaid') return
      if (lang && hljs.getLanguage(lang)) code.innerHTML = hljs.highlight(code.textContent ?? '', { language: lang }).value
      code.classList.add('hljs')
    })
  }
  reportHeight()
  await renderDiagrams(root)
  reportHeight()
}

/** Rich-text note → Markdown with the desktop's converter (its "switch to Markdown"). */
window.convertToMarkdown = (html: string) => {
  post({ type: 'markdown', value: htmlToMarkdown(html) })
}

// taps: [[links]] open the item in the app, web links open in the browser
document.addEventListener('click', (e) => {
  const a = (e.target as HTMLElement).closest('a')
  if (!a) return
  e.preventDefault()
  const wiki = a.getAttribute('data-wiki')
  if (wiki) post({ type: 'wiki', target: wiki })
  else if (a.href) post({ type: 'link', href: a.href })
})

// task-list checkboxes are display-only here
document.addEventListener('change', (e) => {
  const t = e.target as HTMLInputElement
  if (t.type === 'checkbox') t.checked = !t.checked
})

let lastHeight = 0
function reportHeight() {
  const h = Math.ceil(document.documentElement.scrollHeight)
  if (h !== lastHeight) {
    lastHeight = h
    post({ type: 'height', value: h })
  }
}
new ResizeObserver(reportHeight).observe(document.body)

post({ type: 'ready' })
