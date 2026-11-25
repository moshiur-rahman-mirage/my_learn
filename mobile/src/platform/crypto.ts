import * as Crypto from 'expo-crypto'
import { configureSha256 } from '@/domain/hash'

/** Plugs expo-crypto into the domain code (flashcard keys). Imported once by the root layout. */
configureSha256((text) =>
  Crypto.digestStringAsync(Crypto.CryptoDigestAlgorithm.SHA256, text, { encoding: Crypto.CryptoEncoding.HEX }),
)
