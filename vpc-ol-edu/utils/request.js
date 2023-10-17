import axios from 'axios'
import { MessageBox, Message } from 'element-ui'//这是element-ui消息提示的组件
import cookie from 'js-cookie'//凡是对cookie的操作都要引入这个组价

// 创建axios实例
const service = axios.create({
    baseURL: 'http://127.0.0.1:8222', // api 的 base_url
    timeout: 5000 // 请求超时时间
})
// http request 拦截器，拦截器的作用是只要cookie中有token，就将token放入header中，否则什么也不做
//service.interceptors.request.use表示每次发送请求都会使用request的拦截器interceptors
service.interceptors.request.use(
    config => {
    //debugger，判断cookie中有没有token，有就放入header中
    if (cookie.get('user_token')) {
        //config.headers['token'] = window.localStorage.getItem('user_token');
        config.headers['token'] = cookie.get('user_token');//这个token必须与后端的保持一致
    }
    return config
    },
    err => {
    return Promise.reject(err);
})
// http response 拦截器，针对response中支付的拦截器
service.interceptors.response.use(response => {
    //debugger
    if (response.data.code == 28004) {
        console.log("response.data.resultCode是28004")
        // 返回 错误代码-1 清除ticket信息并跳转到登录页面
        //debugger
        window.location.href="/login"
        return
    }else{
        if (response.data.code !== 20000) {
            //25000：订单支付中，不做任何提示
            if(response.data.code != 25000) {
                Message({
                    message: response.data.message || 'error',
                    type: 'error',
                    duration: 5 * 1000
                })
            }
        } else {
            return response;
        }
    }
},error => {
    return Promise.reject(error.response) // 返回接口返回的错误信息
});
//这一行要写在最后，否则会出问题
export default service
