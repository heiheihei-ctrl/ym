/**
 * 解析见证业务动态路由 path。
 * 重整后多为 /ym/batch、/ym/certificate；兼容历史 /batch 等顶级路径。
 */
export function findYmMenuPath(router, keyword, exclude = []) {
  const key = (keyword || '').toLowerCase()
  const excludes = (exclude || []).map(e => String(e).toLowerCase())
  const matched = router.getRoutes().filter(r => {
    if (!r.path || r.path === '/' || r.path.includes(':')) return false
    const p = r.path.toLowerCase()
    if (excludes.some(ex => p.includes(ex))) return false
    return p.includes(key)
  })
  if (!matched.length) return null
  // 优先见证业务目录下的路由，再取最长匹配
  matched.sort((a, b) => {
    const aYm = a.path.toLowerCase().includes('/ym/') ? 1 : 0
    const bYm = b.path.toLowerCase().includes('/ym/') ? 1 : 0
    if (aYm !== bYm) return bYm - aYm
    return b.path.length - a.path.length
  })
  return matched[0].path
}
