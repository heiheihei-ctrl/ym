import request from '@/utils/request'

export function getSiteConfig() {
  return request({
    url: '/ym/siteConfig',
    method: 'get'
  })
}

export function saveSiteConfig(data) {
  return request({
    url: '/ym/siteConfig',
    method: 'put',
    data
  })
}
