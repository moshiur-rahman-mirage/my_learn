/**
 * SHA-256 as lower-case hex of a UTF-8 string. The app plugs in expo-crypto at start-up
 * (`src/platform/crypto.ts`); tests plug in Node's crypto. Kept injectable so domain code stays
 * free of native modules and runs in plain Node.
 */
export type Sha256Hex = (text: string) => Promise<string>

let impl: Sha256Hex | null = null

export function configureSha256(fn: Sha256Hex): void {
  impl = fn
}

export function sha256Hex(text: string): Promise<string> {
  if (!impl) throw new Error('SHA-256 not configured: call configureSha256() at start-up')
  return impl(text)
}
