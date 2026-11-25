<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { Copy, Eye, EyeOff, KeyRound, LoaderCircle, RefreshCw, Smartphone } from 'lucide-vue-next'
import QRCode from 'qrcode'
import { phoneSyncApi } from '../api'
import { errorMessage } from '../api/client'
import type { SyncPairing } from '../types/api'
import { relativeTime } from '../lib/format'
import { useToast } from '../composables/useToast'
import { useConfirm } from '../composables/useConfirm'

const toast = useToast()
const { confirm } = useConfirm()

const pairing = ref<SyncPairing | null>(null)
const loadError = ref<string | null>(null)
const address = ref<string>('')
const showToken = ref(false)
const qr = ref<string | null>(null)
const resetting = ref(false)

/** The URL the phone connects to; the user picks the address if the PC has several networks. */
const url = computed(() => (pairing.value && address.value ? `http://${address.value}:${pairing.value.port}` : ''))
/** Scanned by the phone app's pairing screen. */
const qrPayload = computed(() => (pairing.value && url.value ? JSON.stringify({ url: url.value, token: pairing.value.token }) : ''))

watch(qrPayload, async (payload) => {
  qr.value = payload ? await QRCode.toDataURL(payload, { margin: 1, width: 220, errorCorrectionLevel: 'M' }) : null
})

async function load() {
  try {
    pairing.value = await phoneSyncApi.pairing()
    loadError.value = null
    if (!pairing.value.addresses.includes(address.value)) address.value = pairing.value.addresses[0] ?? ''
  } catch (e) {
    loadError.value = errorMessage(e)
  }
}

async function copy(text: string, what: string) {
  try {
    await navigator.clipboard.writeText(text)
    toast.success(`${what} copied`)
  } catch {
    toast.error(`Couldn't copy; select the text and copy it yourself.`)
  }
}

async function resetToken() {
  const ok = await confirm({
    title: 'Unpair all phones?',
    message: 'A new sync token is created. Every paired phone stops syncing until you pair it again with the new code.',
    confirmLabel: 'Create new token',
    danger: true,
  })
  if (!ok) return
  resetting.value = true
  try {
    pairing.value = await phoneSyncApi.newToken()
    toast.success('New token created. Pair your phone again.')
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    resetting.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="card space-y-3 p-4">
    <div class="flex flex-wrap items-center justify-between gap-2">
      <h2 class="flex items-center gap-2 font-semibold"><Smartphone class="size-4" /> Phone sync</h2>
      <button class="btn-secondary" title="Refresh" @click="load"><RefreshCw class="size-4" /> Refresh</button>
    </div>
    <p class="muted text-sm">
      The Android app keeps its own offline copy of your plans and notes. When the phone is on the same Wi-Fi as this computer, tap
      <strong>Sync</strong> in the app: changes go both ways, and a note edited on both sides keeps both texts.
    </p>

    <p v-if="loadError" class="text-sm text-red-600">{{ loadError }}</p>

    <template v-else-if="pairing">
      <div class="flex flex-wrap gap-6">
        <div class="space-y-3">
          <ol class="list-decimal space-y-1 pl-5 text-sm">
            <li>In the phone app, open <strong>Sync → Pair with computer</strong>.</li>
            <li>Scan this code, or type the address and token below.</li>
            <li>If Windows asks whether Java may use the network, allow <strong>private networks</strong>.</li>
          </ol>

          <div>
            <label class="label" for="sync-address">Address</label>
            <div v-if="pairing.addresses.length" class="flex flex-wrap items-center gap-2">
              <select v-if="pairing.addresses.length > 1" id="sync-address" v-model="address" class="input w-auto font-mono text-xs">
                <option v-for="a in pairing.addresses" :key="a" :value="a">{{ a }}</option>
              </select>
              <code class="text-sm">{{ url }}</code>
              <button class="btn-ghost p-1" title="Copy address" @click="copy(url, 'Address')"><Copy class="size-4" /></button>
            </div>
            <p v-else class="text-sm text-amber-600">
              This computer has no local network address right now. Connect it to Wi-Fi or a network cable, then refresh.
            </p>
          </div>

          <div>
            <span class="label">Token</span>
            <div class="flex flex-wrap items-center gap-2">
              <code class="break-all text-xs">{{ showToken ? pairing.token : '•'.repeat(24) }}</code>
              <button class="btn-ghost p-1" :title="showToken ? 'Hide token' : 'Show token'" @click="showToken = !showToken">
                <EyeOff v-if="showToken" class="size-4" /><Eye v-else class="size-4" />
              </button>
              <button class="btn-ghost p-1" title="Copy token" @click="copy(pairing.token, 'Token')"><Copy class="size-4" /></button>
            </div>
            <p class="muted mt-1 text-xs">Anyone with this token on your network can read and change your data through sync. Keep it private.</p>
          </div>

          <button class="btn-secondary" :disabled="resetting" @click="resetToken">
            <LoaderCircle v-if="resetting" class="size-4 animate-spin" /><KeyRound v-else class="size-4" /> Unpair all phones…
          </button>
        </div>

        <img v-if="qr" :src="qr" alt="Pairing code for the phone app" class="size-[220px] rounded border bg-white p-1" />
      </div>

      <div>
        <h3 class="mb-1 text-sm font-medium">Phones that have synced</h3>
        <p v-if="!pairing.devices.length" class="muted text-sm">None yet.</p>
        <ul v-else class="space-y-1 text-sm">
          <li v-for="d in pairing.devices" :key="d.deviceId" class="flex justify-between gap-4">
            <span>{{ d.name || 'Phone' }}</span>
            <span class="muted" :title="d.lastSyncAt">last sync {{ relativeTime(d.lastSyncAt) }}</span>
          </li>
        </ul>
      </div>
    </template>

    <p v-else class="muted flex items-center gap-2 text-sm"><LoaderCircle class="size-4 animate-spin" /> Loading…</p>
  </section>
</template>
