<template>
    <div class="main">
        <div class="title">
            <a class="active" href="/login">登录</a>
            <span>·</span>
            <a href="/register">注册</a>
        </div>
        <div class="sign-up-container">
            <el-form ref="loginVoForm" :model="loginVo">
                <el-form-item class="input-prepend restyle" prop="phoneNumber" 
                :rules="[{required: true, message: '请输入手机号码', trigger: 'blur' },{validator:checkPhone, trigger: 'blur'}]">
                    <div >
                        <el-input type="text" placeholder="手机号" v-model="loginVo.phoneNumber"/>
                        <i class="iconfont icon-phone" />
                    </div>
                </el-form-item>
                <el-form-item class="input-prepend" prop="password" :rules="[{ required:true, message: '请输入密码', trigger: 'blur' }]">
                    <div>
                        <el-input type="password" placeholder="密码" v-model="loginVo.password"/>
                        <i class="iconfont icon-password"/>
                    </div>
                </el-form-item>
                <div class="btn">
                    <input type="button" class="sign-in-button" value="登录" @click="submitLogin()">
                </div>
            </el-form>
            <!-- 更多登录方式 -->
            <div class="more-sign">
                <h6>社交帐号登录</h6>
                <ul>
                    <li>
                        <a id="weixin" class="weixin" target="_blank" href="http://localhost:9001/api/ucenter/wx/login">
                            <i class="iconfont icon-weixin"/>
                        </a>
                    </li>
                    <li>
                        <a id="qq" class="qq" target="_blank" href="#">
                            <i class="iconfont icon-qq"/>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>
<script>
    import '~/assets/css/sign.css'
    import '~/assets/css/iconfont.css'
    //要用cookie就要安装这个插件，比如向cookie中放token
    import cookie from 'js-cookie'
    import login from '@/api/login'
    export default {
        layout: 'sign',
        data () {
            return {
                loginVo:{
                    phoneNumber:'',
                    password:''
                },
                loginInfo:{}
            }
        },
        methods: {
            submitLogin(){
                console.log(this.loginVo)
                login.submitLogin(this.loginVo).then(response => {
                    if(response.data.success){
                        //把token存在cookie中、也可以放在localStorage中,domian是指定可以访问该cookie的域名，实现对cookie的操作必须下载js-cookie
                        //第一个参数是cookie中token的name属性
                        //第三个属性是domain，这里表示ip地址只要是localhost，cookie都会传递，表示cookie的传递范围
                        cookie.set('user_token', response.data.data.token, { domain:'localhost' })
                        //登录成功发送请求根据token获取用户信息，这个请求发送过程中会被请求拦截器拦截并将token设置在请求头中
                        login.getLoginInfo().then(response => {
                            this.loginInfo = response.data.data.loginInfo
                            //将用户信息记录在cookie中，每次访问domain的ip都会发送，JSON.stringify(this.loginInfo)
                            cookie.set('user_info', JSON.stringify(this.loginInfo), { domain: 'localhost' })
                            //跳转页面到首页,这是js的路径跳转，去地址栏端口后输入一个斜杠
                            window.location.href = "/";
                        })
                    }
                })
            },
            checkPhone (rule, value, callback) {
                //debugger
                if (!(/^1[34578]\d{9}$/.test(value))) {
                    return callback(new Error('手机号码格式不正确'))
                }
                return callback()
            }
        }
    }
</script>
<style>
.el-form-item__error{
    z-index: 9999999;
}
</style>