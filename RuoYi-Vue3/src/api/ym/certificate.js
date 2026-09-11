import request from '@/utils/request'

export function listCertificate(query) {
  return request({
    url: '/ym/certificate/list',
    method: 'get',
    params: query
  })
}

export function getCertificate(id) {
  return request({
    url: '/ym/certificate/' + id,
    method: 'get'
  })
}

export function addCertificate(data) {
  return request({
    url: '/ym/certificate',
    method: 'post',
    data: data
  })
}

export function updateCertificate(data) {
  return request({
    url: '/ym/certificate',
    method: 'put',
    data: data
  })
}

export function delCertificate(id) {
  return request({
    url: '/ym/certificate/' + id,
    method: 'delete'
  })
}
