import request from '@/utils/request'

/** 公开读取查询页系统配置（无需登录） */
export function getPublicSiteConfig() {
  return request({
    url: '/ym/siteConfig/public',
    method: 'get',
    headers: { isToken: false }
  })
}

/** 公开查验见证书（无需登录） */
export function verifyCertificate(code) {
  return request({
    url: '/ym/certificate/public/verify',
    method: 'get',
    params: { code },
    headers: { isToken: false }
  })
}
