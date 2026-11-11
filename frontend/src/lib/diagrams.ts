import type { Mermaid } from 'mermaid'

/**
 * Turns ```mermaid code blocks inside `root` into diagrams. Mermaid is large, so it is only
 * downloaded the first time a note actually contains a diagram. Rendered SVG is cached per
 * source text and theme, so re-rendering while typing is cheap.
 */
let mermaidPromise: Promise<Mermaid> | null = null
let currentTheme: 'dark' | 'default' | null = null
const cache = new Map<string, string>()
let seq = 0

async function load(theme: 'dark' | 'default'): Promise<Mermaid> {
  mermaidPromise ??= import('mermaid').then((m) => m.default)
  const mermaid = await mermaidPromise
  if (currentTheme !== theme) {
    mermaid.initialize({ startOnLoad: false, securityLevel: 'strict', theme, fontFamily: 'inherit' })
    currentTheme = theme
  }
  return mermaid
}

function escapeHtml(s: string): string {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

export async function renderDiagrams(root: HTMLElement | null): Promise<void> {
  if (!root) return
  const blocks = Array.from(root.querySelectorAll<HTMLElement>('pre > code.language-mermaid'))
  if (!blocks.length) return
  const theme = document.documentElement.classList.contains('dark') ? 'dark' : 'default'
  const mermaid = await load(theme)
  for (const code of blocks) {
    const source = code.textContent ?? ''
    const key = `${theme}\u0000${source}`
    let svg = cache.get(key)
    if (svg === undefined) {
      const id = `mmd-${++seq}`
      try {
        svg = (await mermaid.render(id, source)).svg
        if (cache.size > 200) cache.clear()
        cache.set(key, svg)
      } catch (e) {
        document.getElementById(`d${id}`)?.remove() // mermaid's leftover scratch element
        const message = e instanceof Error ? e.message : String(e)
        svg = `<p class="diagram-error">Diagram error: ${escapeHtml(message.split('\n')[0] ?? '')}</p>`
      }
    }
    const pre = code.parentElement
    if (!pre?.isConnected) continue // re-rendered meanwhile
    const box = document.createElement('div')
    box.className = 'mermaid-diagram'
    box.innerHTML = svg
    pre.replaceWith(box)
  }
}
