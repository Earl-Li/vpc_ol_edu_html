'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  //BASE_API: '"https://easy-mock.com/mock/5950a2419adc231f356a6636/vue-admin"',//https是超文本传输协议
  BASE_API: '"http://127.0.0.1:8222"',//个人是没有权限使用https，http需要花钱去找部门认证才能用
})
