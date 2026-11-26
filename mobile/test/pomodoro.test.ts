import { describe, expect, it } from 'vitest'
import { timeEntries } from '../src/data/extras'
import { createItem, createPlan } from '../src/data/structure'
import { usePomodoro } from '../src/state/pomodoro'
import { migratedDb } from './nodeDb'

describe('pomodoro', () => {
  it('survives a restart (end time stored) and logs its minutes once it is over', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: null })
    await usePomodoro.getState().start(db, a, 'A', 25)
    const endsAt = usePomodoro.getState().running!.endsAt

    usePomodoro.setState({ running: null }) // "app restarted"
    await usePomodoro.getState().load(db)
    expect(usePomodoro.getState().running?.itemId).toBe(a)

    expect(await usePomodoro.getState().finishIfDue(db, endsAt - 1)).toBeNull()
    expect((await usePomodoro.getState().finishIfDue(db, endsAt))?.minutes).toBe(25)
    expect((await timeEntries(db, a)).map((e) => e.minutes)).toEqual([25])
    expect(usePomodoro.getState().running).toBeNull()
    expect(await db.first("SELECT 1 FROM app_meta WHERE key = 'pomodoro'")).toBeNull()
  })

  it('cancel logs nothing', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: null })
    await usePomodoro.getState().start(db, a, 'A', 1)
    await usePomodoro.getState().cancel(db)
    expect(await timeEntries(db, a)).toEqual([])
  })
})
