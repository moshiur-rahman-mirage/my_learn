// Runs the end-to-end sync test against the real backend jar (build it first:
// cd backend && mvn -q package -DskipTests). Sets LT_E2E=1 portably, then runs vitest.
import { spawnSync } from 'node:child_process'

const r = spawnSync('npx', ['vitest', 'run', 'test/e2e'], { stdio: 'inherit', shell: true, env: { ...process.env, LT_E2E: '1' } })
process.exit(r.status ?? 1)
