import request from '@/utils/request'
export default {
    //根据手机号码发送短信
    sendSmsCode(phoneNumber) {
        return request({
            url: `edusms/confirm//send/${phoneNumber}`,
            method: 'get'
        })
    },
    //用户注册
    submitRegister(registerVo) {
        return request({
            url: `/eduuser/ucenter/register`,
            method: 'post',
            data: registerVo
        })
    },
    //验证手机号码是否已经注册
    verifyPhoneNumber(phoneNumber){
        return request({
            url: `/eduuser/ucenter/verifyPhoneNumber/${phoneNumber}`,
            method: 'get'
        })
    },
    //验证短信验证码是否匹配
    verifySmsCode(smsCode,phoneNumber){
        return request({
            url: `/eduuser/ucenter/verifySmsCode/${smsCode}/${phoneNumber}`,
            method: 'get'
        })
    }
}