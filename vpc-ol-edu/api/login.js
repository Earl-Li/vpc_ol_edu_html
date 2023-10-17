import request from '@/utils/request'
export default {
    //用户提交登录信息
    submitLogin(loginVo) {
        return request({
            url: `/eduuser/ucenter/login`,
            method: 'post',
            data: loginVo
        })
    },
    //根据token获取用户信息
    getLoginInfo() {
        return request({
            url: `/eduuser/ucenter/getLoginInfo`,
            method: 'get'
        })
    }
}