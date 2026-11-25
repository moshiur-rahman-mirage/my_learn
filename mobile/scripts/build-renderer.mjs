// Builds the offline note viewer (renderer/main.ts) into ONE self-contained HTML file:
// assets/renderer/note-viewer.html. It bundles the desktop's own markdown renderer with marked,
// highlight.js, KaTeX (woff2 fonts inlined) and Mermaid, so notes render with no network.
// Run after changing renderer/ or frontend/src/lib/markdown.ts: npm run renderer
import { mkdirSync, readFileSync, writeFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { build } from 'esbuild'

const here = dirname(fileURLToPath(import.meta.url))
const root = join(here, '..')
const frontendModules = join(root, '..', 'frontend', 'node_modules')

/** KaTeX's CSS lists woff2, woff and ttf for each font; keep only woff2 (every WebView reads it). */
const katexWoff2Only = {
  name: 'katex-woff2-only',
  setup(b) {
    b.onLoad({ filter: /katex(\.min)?\.css$/ }, (args) => ({
      contents: readFileSync(args.path, 'utf8').replace(/,\s*url\([^)]+\.(woff|ttf)\)\s*format\("(woff|truetype)"\)/g, ''),
      loader: 'css',
      resolveDir: dirname(args.path),
    }))
  },
}

const result = await build({
  entryPoints: [join(root, 'renderer', 'main.ts')],
  bundle: true,
  format: 'iife',
  platform: 'browser',
  target: ['chrome100'],
  minify: true,
  write: false,
  outdir: join(root, 'renderer', 'out'),
  nodePaths: [frontendModules, join(root, 'node_modules')],
  loader: { '.woff2': 'dataurl', '.woff': 'empty', '.ttf': 'empty' },
  plugins: [katexWoff2Only],
  legalComments: 'none',
  logLevel: 'warning',
})

const js = result.outputFiles.find((f) => f.path.endsWith('.js')).text
const css = result.outputFiles.find((f) => f.path.endsWith('.css')).text
// keep "</script>" inside the bundle from closing the tag
const safeJs = js.replace(/<\/script/gi, '<\\/script')
const html = `<!doctype html>
<html><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=5">
<style>${css}</style></head>
<body><div id="note"></div><script>${safeJs}</script></body></html>
`
const out = join(root, 'assets', 'renderer', 'note-viewer.html')
mkdirSync(dirname(out), { recursive: true })
writeFileSync(out, html)
console.log(`Wrote ${out} (${(html.length / 1024 / 1024).toFixed(2)} MB)`)
