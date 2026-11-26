import { useRef, useState } from 'react'
import { Alert, KeyboardAvoidingView, Platform, ScrollView, Text, TextInput, View } from 'react-native'
import { CameraView, useCameraPermissions } from 'expo-camera'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { fetchTransport, pair, parsePairingCode, SyncError } from '@/sync/client'
import { useSync } from '@/sync/useSync'
import { useDataVersion } from '@/state/stores'
import { Button, Card, ErrorText } from '@/ui/components'
import { radius, space, useColors } from '@/ui/theme'

/**
 * Pairs with the desktop: scan the QR code from its Settings → Phone sync, or type the address
 * and token shown there. On success the first sync starts right away.
 */
export default function PairScreen() {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const runSync = useSync((s) => s.run)
  const [permission, requestPermission] = useCameraPermissions()
  const [scanning, setScanning] = useState(false)
  const [url, setUrl] = useState('')
  const [token, setToken] = useState('')
  const [label, setLabel] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const handled = useRef(false)

  const connect = async (u: string, t: string, discardUnsent = false) => {
    setBusy(true)
    setError(null)
    try {
      await pair(db, fetchTransport(15_000), u, t, discardUnsent, label)
      bump()
      router.back()
      void runSync(db) // first sync: brings the plans over
    } catch (e) {
      if (e instanceof SyncError && e.kind === 'unsent-changes') {
        Alert.alert('Changes not sent yet', `${e.message}\n\nSync with the previous computer first, or continue and discard them.`, [
          { text: 'Cancel', style: 'cancel' },
          { text: 'Discard and pair', style: 'destructive', onPress: () => void connect(u, t, true) },
        ])
      } else {
        setError(e instanceof Error ? e.message : String(e))
      }
      handled.current = false
    } finally {
      setBusy(false)
    }
  }

  const startScan = async () => {
    setError(null)
    if (!permission?.granted) {
      const p = await requestPermission()
      if (!p.granted) {
        setError('Camera access was denied. Type the address and token instead.')
        return
      }
    }
    handled.current = false
    setScanning(true)
  }

  const inputStyle = {
    borderWidth: 1, borderColor: c.border, borderRadius: radius.md, padding: space.md, color: c.text, backgroundColor: c.card, fontSize: 15,
  }

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg }} keyboardShouldPersistTaps="handled">
        <Text style={{ color: c.muted }}>
          On your computer, open the Learning Tracker → Settings → Phone sync. Both devices must be on the same Wi-Fi.
        </Text>

        {scanning ? (
          <View style={{ gap: space.sm }}>
            <View style={{ height: 300, borderRadius: radius.card, overflow: 'hidden' }}>
              <CameraView
                style={{ flex: 1 }}
                facing="back"
                barcodeScannerSettings={{ barcodeTypes: ['qr'] }}
                onBarcodeScanned={({ data }) => {
                  if (handled.current) return
                  const code = parsePairingCode(data)
                  if (!code) {
                    setError('That QR code isn’t a Learning Tracker pairing code.')
                    return
                  }
                  handled.current = true
                  setScanning(false)
                  setUrl(code.url)
                  setToken(code.token)
                  void connect(code.url, code.token)
                }}
              />
            </View>
            <Button kind="secondary" title="Cancel scanning" onPress={() => setScanning(false)} />
          </View>
        ) : (
          <Button title="Scan the code on your computer" onPress={startScan} busy={busy} />
        )}

        <Card style={{ gap: space.md }}>
          <Text style={{ color: c.text, fontWeight: '600' }}>Or type it in</Text>
          <View style={{ gap: space.xs }}>
            <Text style={{ color: c.muted, fontSize: 13 }}>Network name (optional)</Text>
            <TextInput
              value={label}
              onChangeText={setLabel}
              placeholder="Home or Office"
              placeholderTextColor={c.faint}
              autoCapitalize="words"
              style={inputStyle}
              accessibilityLabel="Network name"
            />
          </View>
          <View style={{ gap: space.xs }}>
            <Text style={{ color: c.muted, fontSize: 13 }}>Address</Text>
            <TextInput
              value={url}
              onChangeText={setUrl}
              placeholder="192.168.1.20:38427"
              placeholderTextColor={c.faint}
              autoCapitalize="none"
              autoCorrect={false}
              keyboardType="url"
              style={inputStyle}
              accessibilityLabel="Computer address"
            />
          </View>
          <View style={{ gap: space.xs }}>
            <Text style={{ color: c.muted, fontSize: 13 }}>Token</Text>
            <TextInput
              value={token}
              onChangeText={setToken}
              placeholder="Copied from Settings → Phone sync"
              placeholderTextColor={c.faint}
              autoCapitalize="none"
              autoCorrect={false}
              secureTextEntry
              style={inputStyle}
              accessibilityLabel="Sync token"
            />
          </View>
          <Button title="Connect" onPress={() => connect(url, token.trim())} disabled={!url.trim() || !token.trim()} busy={busy} />
        </Card>

        <ErrorText message={error} />
      </ScrollView>
    </KeyboardAvoidingView>
  )
}
