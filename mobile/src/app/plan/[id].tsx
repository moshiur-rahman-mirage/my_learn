import { useMemo, useState } from 'react'
import { Alert, FlatList, Pressable, Switch, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router, Stack, useLocalSearchParams } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { getPlan } from '@/data/plans'
import { isShortReadPlanIncluded, setShortReadPlanIncluded } from '@/data/shortRead'
import { loadTree } from '@/data/tree'
import { RuleError, setCompletion } from '@/data/items'
import { exportPlanCsv } from '@/data/exportCsv'
import { StaleError, updatePlan } from '@/data/structure'
import { shareTextFile } from '@/platform/share'
import { percentLabel } from '@/domain/progress'
import type { Plan, TreeNode } from '@/domain/types'
import { isDone, parentIds, visibleRows } from '@/domain/visibleRows'
import { useDataVersion, useExpanded } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { ActionSheet, type SheetAction } from '@/ui/ActionSheet'
import { Chip, EmptyState, ErrorText, Loading, PriorityBadge, ProgressBar } from '@/ui/components'
import { itemActions } from '@/ui/itemActions'
import { PaceBadge } from '@/ui/PaceBadge'
import { PlanCover } from '@/ui/PlanCover'
import { space, useColors } from '@/ui/theme'

export default function PlanScreen() {
  const { id } = useLocalSearchParams<{ id: string }>()
  const planId = Number(id)
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const [hideDone, setHideDone] = useState(false)
  const [sheet, setSheet] = useState<{ title: string; actions: SheetAction[] } | null>(null)
  const { data, error } = useQuery(async (d) => ({
    plan: await getPlan(d, planId),
    tree: await loadTree(d, planId),
    shortReadIncluded: await isShortReadPlanIncluded(d, planId),
  }), `plan:${planId}`)

  const expandedIds = useExpanded((s) => s.byPlan[planId])
  const setExpanded = useExpanded((s) => s.set)
  const toggle = useExpanded((s) => s.toggle)
  // first visit: open the top level, like a table of contents
  const expanded = useMemo(
    () => new Set(expandedIds ?? data?.tree.filter((n) => !n.leaf).map((n) => n.id) ?? []),
    [expandedIds, data],
  )
  const rows = useMemo(() => (data ? visibleRows(data.tree, expanded, hideDone) : []), [data, expanded, hideDone])

  const onToggleExpand = (node: TreeNode) => {
    if (!expandedIds) setExpanded(planId, [...expanded])
    toggle(planId, node.id)
  }

  const onTick = async (node: TreeNode) => {
    try {
      await setCompletion(db, node.id, !node.completed)
      bump()
    } catch (e) {
      Alert.alert('Can’t tick this', e instanceof RuleError ? e.message : String(e))
    }
  }

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  if (!data.plan) return <EmptyState title="This plan is gone" message="It was deleted, maybe on your computer." />
  const plan: Plan = data.plan
  const allIds = parentIds(data.tree)

  const addTopLevel = () => router.push({ pathname: '/item-form', params: { planId: String(planId), parentId: '' } })
  const toggleArchived = async () => {
    try {
      await updatePlan(db, plan.id, {
        name: plan.name, description: plan.description, targetDate: plan.targetDate,
        status: plan.status === 'ACTIVE' ? 'ARCHIVED' : 'ACTIVE', version: plan.version,
      })
      bump()
    } catch (e) {
      Alert.alert(e instanceof StaleError ? 'Changed meanwhile' : 'Can’t change it', e instanceof Error ? e.message : String(e))
    }
  }
  const toggleShortReadIncluded = async (included: boolean) => {
    try {
      await setShortReadPlanIncluded(db, plan.id, included)
      bump()
    } catch (e) {
      Alert.alert('Can’t update Short Read', e instanceof Error ? e.message : String(e))
    }
  }
  const planMenu = () =>
    setSheet({
      title: plan.name,
      actions: [
        { label: 'Add a top-level item', icon: 'add-circle-outline', onPress: addTopLevel },
        { label: 'Edit plan (name, target date)', icon: 'create-outline', onPress: () => router.push({ pathname: '/plan-form', params: { id: String(plan.id) } }) },
        plan.status === 'ACTIVE'
          ? { label: 'Archive plan', icon: 'archive-outline', onPress: () => void toggleArchived() }
          : { label: 'Make active again', icon: 'refresh-outline', onPress: () => void toggleArchived() },
        { label: 'Export as CSV (for the desktop or Excel)', icon: 'share-outline', onPress: () => void exportCsv() },
        { label: 'Delete plan…', icon: 'trash-outline', destructive: true, onPress: () => router.push({ pathname: '/plan-delete/[id]', params: { id: String(plan.id) } }) },
      ],
    })
  const exportCsv = async () => {
    try {
      const { fileName, content } = await exportPlanCsv(db, plan.id)
      await shareTextFile(fileName, content, 'text/csv')
    } catch (e) {
      Alert.alert('Couldn’t export', e instanceof Error ? e.message : String(e))
    }
  }
  const rowMenu = (node: TreeNode) =>
    setSheet({ title: node.name, actions: itemActions(db, { id: node.id, name: node.name, planId, parentId: node.parentId }, bump) })

  return (
    <>
      <Stack.Screen
        options={{
          title: plan.name,
          headerRight: () => (
            <View style={{ flexDirection: 'row', gap: space.lg }}>
              <Pressable onPress={addTopLevel} hitSlop={12} accessibilityRole="button" accessibilityLabel="Add a top-level item">
                <Ionicons name="add" size={26} color={c.text} />
              </Pressable>
              <Pressable onPress={planMenu} hitSlop={12} accessibilityRole="button" accessibilityLabel="Plan actions">
                <Ionicons name="ellipsis-vertical" size={22} color={c.text} />
              </Pressable>
            </View>
          ),
        }}
      />
      <ActionSheet visible={sheet !== null} onClose={() => setSheet(null)} title={sheet?.title} actions={sheet?.actions ?? []} />
      <FlatList
        data={rows}
        keyExtractor={(r) => String(r.node.id)}
        initialNumToRender={30}
        windowSize={11}
        ListHeaderComponent={
          <View style={{ padding: space.lg, gap: space.md, borderBottomColor: c.border, borderBottomWidth: 1 }}>
            <View style={{ flexDirection: 'row', gap: space.lg, alignItems: 'center' }}>
              <PlanCover name={plan.name} width={78} height={104} />
              <View style={{ flex: 1, gap: 6 }}>
                <Text style={{ color: c.text, fontSize: 20, fontWeight: '800', fontFamily: 'serif' }} numberOfLines={3}>{plan.name}</Text>
                {plan.description ? <Text style={{ color: c.muted, fontSize: 13 }} numberOfLines={2}>{plan.description}</Text> : null}
                <Text style={{ color: c.muted, fontSize: 12 }}>
                  <Text style={{ color: c.primary, fontWeight: '900', fontSize: 16 }}>{percentLabel(plan.progress)}</Text>
                  {`  ${plan.progress.completed} of ${plan.progress.total} topics`}{plan.status === 'ARCHIVED' ? ' · archived' : ''}
                </Text>
              </View>
            </View>
            <ProgressBar progress={plan.progress} height={8} />
            <PaceBadge pace={plan.pace} detailed />
            <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', gap: space.md }}>
              <View style={{ flex: 1 }}>
                <Text style={{ color: c.text, fontWeight: '700' }}>Include in Short Read</Text>
                <Text style={{ color: c.muted, fontSize: 12 }}>Allow topics from this plan in home recommendations</Text>
              </View>
              <Text style={{ color: c.muted, fontWeight: '700' }}>{data.shortReadIncluded ? 'Yes' : 'No'}</Text>
              <Switch
                value={data.shortReadIncluded}
                onValueChange={(included) => void toggleShortReadIncluded(included)}
                trackColor={{ false: c.border, true: c.primary }}
                accessibilityLabel="Include this plan in Short Read recommendations"
              />
            </View>
            <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.sm }}>
              <Chip label="Hide done" on={hideDone} onPress={() => setHideDone((h) => !h)} />
              <Chip label="Expand all" on={false} onPress={() => setExpanded(planId, allIds)} />
              <Chip label="Collapse all" on={false} onPress={() => setExpanded(planId, [])} />
            </View>
          </View>
        }
        ListEmptyComponent={
          <EmptyState title={hideDone ? 'Everything here is done' : 'This plan is empty'} message={hideDone ? 'Nice work.' : undefined} />
        }
        renderItem={({ item }) => (
          <TreeRow node={item.node} depth={item.depth} expanded={expanded.has(item.node.id)} onToggle={onToggleExpand} onTick={onTick} onMenu={rowMenu} />
        )}
      />
    </>
  )
}

function TreeRow({ node, depth, expanded, onToggle, onTick, onMenu }: {
  node: TreeNode
  depth: number
  expanded: boolean
  onToggle: (n: TreeNode) => void
  onTick: (n: TreeNode) => void
  onMenu: (n: TreeNode) => void
}) {
  const c = useColors()
  const done = isDone(node)
  return (
    <View
      style={{
        flexDirection: 'row', alignItems: 'center', minHeight: 48, paddingRight: space.lg,
        paddingLeft: space.sm + depth * 18, borderBottomColor: c.border, borderBottomWidth: 0.5, gap: space.xs,
      }}
    >
      {node.leaf ? (
        <View style={{ width: 32 }} />
      ) : (
        <Pressable
          onPress={() => onToggle(node)}
          hitSlop={8}
          accessibilityRole="button"
          accessibilityLabel={`${expanded ? 'Collapse' : 'Expand'} ${node.name}`}
          style={{ width: 32, height: 40, alignItems: 'center', justifyContent: 'center' }}
        >
          <Ionicons name={expanded ? 'chevron-down' : 'chevron-forward'} size={18} color={c.muted} />
        </Pressable>
      )}
      {node.checkable ? (
        <Pressable
          onPress={() => onTick(node)}
          hitSlop={8}
          accessibilityRole="checkbox"
          accessibilityState={{ checked: node.completed }}
          accessibilityLabel={node.name}
          style={{ width: 36, height: 40, alignItems: 'center', justifyContent: 'center' }}
        >
          <Ionicons name={node.completed ? 'checkbox' : 'square-outline'} size={24} color={node.completed ? c.done : c.faint} />
        </Pressable>
      ) : null}
      <Pressable
        style={{ flex: 1, paddingVertical: space.sm, gap: 2 }}
        onPress={() => router.push({ pathname: '/item/[id]', params: { id: String(node.id) } })}
        onLongPress={() => onMenu(node)}
        accessibilityActions={[{ name: 'longpress', label: 'Edit, move or delete' }]}
        onAccessibilityAction={(e) => e.nativeEvent.actionName === 'longpress' && onMenu(node)}
        accessibilityRole="button"
        accessibilityHint="Opens the details and notes; long-press to edit, move or delete"
      >
        <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.xs, flexWrap: 'wrap' }}>
          <Text
            style={{
              color: done && node.checkable ? c.muted : c.text, fontWeight: node.type === 'MODULE' ? '600' : '400', fontSize: 15,
              textDecorationLine: node.checkable && node.completed ? 'line-through' : 'none', flexShrink: 1,
            }}
          >
            {node.name}
          </Text>
          <PriorityBadge priority={node.priority} />
          {node.hasNotes ? <Ionicons name="document-text-outline" size={14} color={c.muted} accessibilityLabel="has notes" /> : null}
          {node.flashcards > 0 ? <Text style={{ color: c.muted, fontSize: 11 }}>{node.flashcards} cards</Text> : null}
        </View>
        {!node.leaf ? (
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm }}>
            <View style={{ flex: 1, maxWidth: 160 }}>
              <ProgressBar progress={node.progress} height={4} />
            </View>
            <Text style={{ color: c.muted, fontSize: 11 }}>
              {percentLabel(node.progress)} · {node.progress.completed}/{node.progress.total}
            </Text>
          </View>
        ) : node.type === 'MODULE' ? (
          <Text style={{ color: c.faint, fontSize: 11 }}>Empty module · N/A</Text>
        ) : null}
      </Pressable>
      <Pressable onPress={() => onMenu(node)} hitSlop={8} accessibilityRole="button"
        accessibilityLabel={`Actions for ${node.name}`}
        style={{ width: 40, height: 44, alignItems: 'center', justifyContent: 'center' }}>
        <Ionicons name="ellipsis-vertical" size={18} color={c.muted} />
      </Pressable>
    </View>
  )
}
