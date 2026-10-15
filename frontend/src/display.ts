import { currentUser } from './api'

/**
 * 负责人显示名。
 *
 * 后端 `/api/users` 仅管理员可读（见 AdminAccessInterceptor），因此普通账号无法把
 * 其他用户的 ID 解析为姓名；此处只解析当前登录用户，其余显示为“用户#ID”。
 * 待后端在业务返回体中补充 ownerName 后，改为直接使用该字段。
 */
export function ownerLabel(id: unknown): string {
  if (id === null || id === undefined || id === '') return '—'
  const me = currentUser()
  if (me && Number(me.id) === Number(id)) return me.nickname || me.username || `用户#${id}`
  return `用户#${id}`
}

/**
 * 把后端返回的时间字符串显示为“MM-DD HH:mm”。
 *
 * 只做字符串截取，不做时区换算：后端统一返回无时区的字面量时间，
 * 前端若按本地时区解析反而会引入新的偏移。无法识别时原样返回。
 */
export function formatDateTime(value: unknown): string {
  if (value === null || value === undefined || value === '') return '—'
  const text = String(value)
  const matched = /^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})/.exec(text)
  return matched ? `${matched[2]}-${matched[3]} ${matched[4]}:${matched[5]}` : text
}

/** 导出 CSV。带 UTF-8 BOM，便于 Excel 直接识别中文。 */
export function exportCsv(filename: string, headers: string[], rows: Array<Array<unknown>>): void {
  const escape = (value: unknown) => {
    const text = value === null || value === undefined ? '' : String(value)
    return /[",\n\r]/.test(text) ? `"${text.replace(/"/g, '""')}"` : text
  }
  const lines = [headers.map(escape).join(','), ...rows.map((row) => row.map(escape).join(','))]
  const blob = new Blob([`\uFEFF${lines.join('\r\n')}`], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}
