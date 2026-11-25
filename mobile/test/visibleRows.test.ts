import { describe, expect, it } from 'vitest'
import { buildTree } from '../src/domain/tree'
import { isDone, parentIds, visibleRows } from '../src/domain/visibleRows'
import { done, row } from './fixtures'

function sample() {
  const m = row('Module', 'MODULE', null, 0)
  const a = done(row('A', 'TOPIC', m, 0))
  const b = row('B', 'TOPIC', m, 1)
  const finished = row('Finished', 'MODULE', null, 1)
  const f1 = done(row('F1', 'TOPIC', finished, 0))
  const loose = row('Loose', 'TOPIC', null, 2)
  return { tree: buildTree([m, a, b, finished, f1, loose]), m, finished }
}

describe('visibleRows', () => {
  it('shows only roots when nothing is expanded', () => {
    const { tree } = sample()
    expect(visibleRows(tree, new Set()).map((r) => r.node.name)).toEqual(['Module', 'Finished', 'Loose'])
  })

  it('descends into expanded nodes with depth', () => {
    const { tree, m } = sample()
    expect(visibleRows(tree, new Set([m.id])).map((r) => `${r.depth}:${r.node.name}`)).toEqual(['0:Module', '1:A', '1:B', '0:Finished', '0:Loose'])
  })

  it('hides finished topics and finished branches', () => {
    const { tree, m, finished } = sample()
    expect(visibleRows(tree, new Set([m.id, finished.id]), true).map((r) => r.node.name)).toEqual(['Module', 'B', 'Loose'])
  })

  it('knows what is done and which nodes can expand', () => {
    const { tree, m, finished } = sample()
    expect(tree.map(isDone)).toEqual([false, true, false])
    expect(parentIds(tree)).toEqual([m.id, finished.id])
  })
})
