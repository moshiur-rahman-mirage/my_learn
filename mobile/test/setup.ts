import { createHash } from 'node:crypto'
import { configureSha256 } from '../src/domain/hash'

// The app uses expo-crypto (src/platform/crypto.ts); tests use Node's.
configureSha256(async (text) => createHash('sha256').update(text, 'utf8').digest('hex'))
