/**
 * 律师执业证号：仅校验 17 位数字
 */
export function isValidLawyerNo(value) {
  const no = String(value || '').trim()
  if (!no) {
    return '执业证号不能为空'
  }
  if (!/^\d{17}$/.test(no)) {
    return '执业证号须为17位数字'
  }
  return true
}

export function validateLawyerNoRule(rule, value, callback) {
  const result = isValidLawyerNo(value)
  if (result === true) {
    callback()
  } else {
    callback(new Error(result))
  }
}
