import request from '@/utils/request'

export function listBatch(query) {
  return request({
    url: '/ym/batch/list',
    method: 'get',
    params: query
  })
}

export function getBatch(id) {
  return request({
    url: '/ym/batch/' + id,
    method: 'get'
  })
}

export function addBatch(data) {
  return request({
    url: '/ym/batch',
    method: 'post',
    data: data
  })
}

export function updateBatch(data) {
  return request({
    url: '/ym/batch',
    method: 'put',
    data: data
  })
}

export function delBatch(id) {
  return request({
    url: '/ym/batch/' + id,
    method: 'delete'
  })
}
