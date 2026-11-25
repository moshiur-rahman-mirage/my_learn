/** "motorola" + "motorola edge 60 fusion" → "Motorola edge 60 fusion" (no doubled brand). */
export function phoneName(brand: string | undefined, model: string | undefined): string {
  const b = (brand ?? '').trim()
  const m = (model ?? '').trim()
  const name = b && m.toLowerCase().startsWith(b.toLowerCase()) ? m : [b, m].filter(Boolean).join(' ')
  return name ? name.charAt(0).toUpperCase() + name.slice(1) : ''
}
