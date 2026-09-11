import { pcaTextArr } from 'element-china-area-data'

/** 省市区级联（value 为中文名称，与 origins 表 province/city/district 一致） */
export const regionData = pcaTextArr

/** 拼接完整产地地址（省+市+区+详细地址） */
export function buildFullAddress(province, city, district, address) {
  return [province, city, district, address].filter(v => v && String(v).trim()).join('')
}

/** 根据已存省市区名称还原级联选中值 */
export function namesToRegionCodes(province, city, district) {
  if (!province || !city || !district) {
    return []
  }
  return [province, city, district]
}
