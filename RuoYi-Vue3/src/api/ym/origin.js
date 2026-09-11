import request from '@/utils/request'

export function listOrigin(query) {
  return request({
    url: '/ym/origin/list',
    method: 'get',
    params: query
  })
}

export function getOrigin(id) {
  return request({
    url: '/ym/origin/' + id,
    method: 'get'
  })
}

export function addOrigin(data) {
  return request({
    url: '/ym/origin',
    method: 'post',
    data: data
  })
}

export function updateOrigin(data) {
  return request({
    url: '/ym/origin',
    method: 'put',
    data: data
  })
}

export function delOrigin(id) {
  return request({
    url: '/ym/origin/' + id,
    method: 'delete'
  })
}
