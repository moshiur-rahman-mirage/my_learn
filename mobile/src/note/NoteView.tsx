import { useEffect, useImperativeHandle, useRef, useState, type ComponentType, type Ref } from 'react'
import { ActivityIndicator, Linking, PixelRatio, View, useColorScheme } from 'react-native'
import { Asset } from 'expo-asset'
import { WebView as RNWebView } from 'react-native-webview'
import type { AndroidWebViewProps, WebViewMessageEvent } from 'react-native-webview/lib/WebViewTypes'
import type { NoteFormat } from '@/domain/types'

/**
 * Shows a note exactly as the desktop does: the desktop's own Markdown renderer (marked,
 * highlight.js, KaTeX, Mermaid) bundled into assets/renderer/note-viewer.html and run in a
 * WebView, fully offline. Sizes itself to the note, so it sits inside a ScrollView.
 */
export interface NoteViewHandle {
  /** Converts rich-text HTML to Markdown with the desktop's converter. */
  toMarkdown: (html: string) => Promise<string>
}

/** What we call on the WebView instance. */
interface WebViewRef {
  injectJavaScript: (script: string) => void
}

/**
 * The package types WebView with iOS & Android & Windows props intersected, which TypeScript 6
 * reduces to `never`. This app is Android-only, so type it with the Android props (same runtime
 * component: Metro resolves WebView.android.js).
 */
const WebView = RNWebView as unknown as ComponentType<AndroidWebViewProps & { ref?: Ref<WebViewRef> }>

let viewerUri: Promise<string> | null = null

/** The viewer's local file:// address (the asset is copied out of the app bundle once). */
function loadViewer(): Promise<string> {
  viewerUri ??= (async () => {
    const asset = Asset.fromModule(require('../../assets/renderer/note-viewer.html'))
    await asset.downloadAsync()
    if (!asset.localUri) throw new Error('Note viewer asset is missing')
    return asset.localUri
  })()
  viewerUri.catch(() => {
    viewerUri = null
  })
  return viewerUri
}

export function NoteView({
  content, format, names, onWikiLink, ref, minHeight = 40,
}: {
  content: string
  format: NoteFormat
  /** wikiKey()s of the plan's item names, so [[links]] to missing items look different */
  names: string[]
  onWikiLink?: (target: string) => void
  ref?: Ref<NoteViewHandle>
  minHeight?: number
}) {
  const dark = useColorScheme() === 'dark'
  const web = useRef<WebViewRef>(null)
  const [uri, setUri] = useState<string | null>(null)
  const [ready, setReady] = useState(false)
  const [height, setHeight] = useState(minHeight)
  const pending = useRef<((md: string) => void) | null>(null)

  useEffect(() => {
    let alive = true
    loadViewer().then((u) => alive && setUri(u))
    return () => {
      alive = false
    }
  }, [])

  // (re)render whenever the note or theme changes
  useEffect(() => {
    if (!ready) return
    const payload = JSON.stringify({ content, format, dark, names, fontScale: PixelRatio.getFontScale() })
    web.current?.injectJavaScript(`window.renderNote(${payload}); true;`)
  }, [ready, content, format, dark, names])

  useImperativeHandle(ref, () => ({
    toMarkdown: (html: string) =>
      new Promise<string>((resolve) => {
        pending.current = resolve
        web.current?.injectJavaScript(`window.convertToMarkdown(${JSON.stringify(html)}); true;`)
      }),
  }), [])

  const onMessage = (e: WebViewMessageEvent) => {
    let msg: { type: string; value?: unknown; target?: string; href?: string }
    try {
      msg = JSON.parse(e.nativeEvent.data)
    } catch {
      return
    }
    if (msg.type === 'ready') setReady(true)
    else if (msg.type === 'height' && typeof msg.value === 'number') setHeight(Math.max(minHeight, msg.value))
    else if (msg.type === 'wiki' && msg.target) onWikiLink?.(msg.target)
    else if (msg.type === 'link' && msg.href && /^https?:|^mailto:/i.test(msg.href)) void Linking.openURL(msg.href)
    else if (msg.type === 'markdown' && typeof msg.value === 'string') {
      pending.current?.(msg.value)
      pending.current = null
    }
  }

  if (!uri) return <ActivityIndicator style={{ height: minHeight }} />
  return (
    <View style={{ height }}>
      <WebView
        ref={web}
        source={{ uri }}
        originWhitelist={['*']}
        allowFileAccess
        javaScriptEnabled
        nestedScrollEnabled
        onMessage={onMessage}
        // only the viewer itself may load; links go through onMessage
        onShouldStartLoadWithRequest={(req) => req.url === uri}
        style={{ backgroundColor: 'transparent' }}
        containerStyle={{ backgroundColor: 'transparent' }}
        setSupportMultipleWindows={false}
        textZoom={100}
      />
    </View>
  )
}
