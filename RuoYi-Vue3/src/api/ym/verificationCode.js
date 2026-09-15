import request from '@/utils/request'

/** 查询码接口挂在证书 Controller 下，路径 /ym/certificate/verificationCode/* */

export function listVerificationCode(query) {
  return request({
    url: '/ym/certificate/verificationCode/list',
    method: 'get',
    params: query
  })
}

export function listUnusedVerificationCode(params) {
  return request({
    url: '/ym/certificate/verificationCode/unusedList',
    method: 'get',
    params
  })
}

export function batchGenerateVerificationCode(data) {
  return request({
    url: '/ym/certificate/verificationCode/batchGenerate',
    method: 'post',
    data
  })
}

export function delVerificationCode(id) {
  return request({
    url: '/ym/certificate/verificationCode/' + id,
    method: 'delete'
  })
}
