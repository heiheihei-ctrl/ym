import request from '@/utils/request'

export function listLawyer(query) {
  return request({
    url: '/ym/lawyer/list',
    method: 'get',
    params: query
  })
}

export function getLawyer(id) {
  return request({
    url: '/ym/lawyer/' + id,
    method: 'get'
  })
}

export function addLawyer(data) {
  return request({
    url: '/ym/lawyer',
    method: 'post',
    data: data
  })
}

export function updateLawyer(data) {
  return request({
    url: '/ym/lawyer',
    method: 'put',
    data: data
  })
}

export function delLawyer(id) {
  return request({
    url: '/ym/lawyer/' + id,
    method: 'delete'
  })
}
