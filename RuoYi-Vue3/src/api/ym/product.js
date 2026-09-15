import request from '@/utils/request'

export function listProduct(query) {
  return request({
    url: '/ym/product/list',
    method: 'get',
    params: query
  })
}

export function listProductOptions() {
  return request({
    url: '/ym/product/options',
    method: 'get'
  })
}

export function getProduct(id) {
  return request({
    url: '/ym/product/' + id,
    method: 'get'
  })
}

export function addProduct(data) {
  return request({
    url: '/ym/product',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/ym/product',
    method: 'put',
    data
  })
}

export function delProduct(id) {
  return request({
    url: '/ym/product/' + id,
    method: 'delete'
  })
}
