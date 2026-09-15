import { listProductOptions } from '@/api/ym/product'

/** 将产品列表转为下拉 { label, value }，value 为 productCode */
export function mapProductOptions(rows) {
  return (rows || []).map(item => ({
    label: item.productName,
    value: item.productCode,
    raw: item
  }))
}

export function loadEnabledProductOptions() {
  return listProductOptions().then(res => mapProductOptions(res.data))
}

export function productLabel(options, code) {
  const item = (options || []).find(o => o.value === code)
  return item ? item.label : code || '-'
}
