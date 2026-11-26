import { Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import Svg, { Defs, LinearGradient, Rect, Stop } from 'react-native-svg'

/** Pairs of gradient colours; a plan always gets the same one (hash of its name). */
const GRADIENTS: [string, string][] = [
  ['#3f7f78', '#2c5c57'], // deep teal
  ['#4f6d9a', '#384f73'], // slate blue
  ['#6f7f4a', '#525f35'], // olive
  ['#b0694a', '#8a4f36'], // terracotta
  ['#7a5a83', '#5b4262'], // plum
  ['#3d7a96', '#2b5a6f'], // ocean
  ['#a8874f', '#7f6538'], // ochre
  ['#5c6670', '#434b53'], // graphite
  ['#4f8a66', '#3a684c'], // forest
  ['#9a5f6f', '#764654'], // rosewood
]

/** A fitting icon from words in the plan name; a book otherwise. */
const ICONS: [RegExp, keyof typeof Ionicons.glyphMap][] = [
  [/java|spring|kotlin/i, 'cafe'],
  [/aws|cloud|azure|gcp|devops|docker|kubernetes/i, 'cloud'],
  [/math|calculus|algebra|geometry|trigonometry|statistic|probability|linear/i, 'calculator'],
  [/dsa|algorithm|data struct|leetcode/i, 'git-network'],
  [/node|javascript|typescript|react|vue|web/i, 'logo-nodejs'],
  [/python|django|flask/i, 'logo-python'],
  [/design|architect|system/i, 'construct'],
  [/account|finance|mba|business|odoo|economics/i, 'briefcase'],
  [/analyst|data|sql|bi\b/i, 'stats-chart'],
  [/manager|leader|engineering-manager/i, 'people'],
  [/language|english|french|spanish|german/i, 'language'],
]

export function hashOf(s: string): number {
  let h = 0
  for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) | 0
  return Math.abs(h)
}

export function coverFor(name: string): { from: string; to: string; icon: keyof typeof Ionicons.glyphMap } {
  const [from, to] = GRADIENTS[hashOf(name) % GRADIENTS.length]
  const icon = ICONS.find(([re]) => re.test(name))?.[1] ?? 'book'
  return { from, to, icon }
}

function initials(name: string): string {
  const words = name.replace(/[-_]/g, ' ').split(/\s+/).filter((w) => /[A-Za-z0-9]/.test(w))
  return (words.length >= 2 ? words[0][0] + words[1][0] : (words[0] ?? '?').slice(0, 2)).toUpperCase()
}

/** A generated "book cover" for a plan: gradient, icon and initials. */
export function PlanCover({ name, width = 64, height = 86 }: { name: string; width?: number; height?: number }) {
  const { from, to, icon } = coverFor(name)
  const id = `g${hashOf(name)}`
  return (
    <View style={{ width, height, borderRadius: 12, overflow: 'hidden', elevation: 3 }} accessibilityElementsHidden importantForAccessibility="no-hide-descendants">
      <Svg width={width} height={height} style={{ position: 'absolute' }}>
        <Defs>
          <LinearGradient id={id} x1="0" y1="0" x2="1" y2="1">
            <Stop offset="0" stopColor={from} />
            <Stop offset="1" stopColor={to} />
          </LinearGradient>
        </Defs>
        <Rect x="0" y="0" width={width} height={height} fill={`url(#${id})`} />
        <Rect x="0" y="0" width={5} height={height} fill="#00000022" />
      </Svg>
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', gap: 4 }}>
        <Ionicons name={icon} size={Math.round(width * 0.38)} color="#ffffffee" />
        <Text style={{ color: '#ffffffcc', fontWeight: '900', fontSize: Math.round(width * 0.17), letterSpacing: 1 }}>{initials(name)}</Text>
      </View>
    </View>
  )
}
