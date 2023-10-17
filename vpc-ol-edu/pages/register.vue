<template>
    <div class="main">
        <div class="title">
            <a href="/login">登录</a>
            <span>·</span>
            <a class="active" href="/register">注册</a>
        </div>
        <div class="sign-up-container">
            <el-form ref="userForm" :model="registerVo">
                <!--:rules是输入框的前端校验规则，required: true表示该输入框必须输入值，不输入值表单提交会有问题，message是不输入值后输入框的提示信息，
                trigger是触发判断的事件，这里是blur失去焦点
                -->
                <el-form-item class="input-prepend restyle" prop="nickname" :rules="[{required: true, message: '请输入你的昵称', trigger: 'blur' }]">
                    <div>
                        <el-input type="text" placeholder="你的昵称" v-model="registerVo.nickname"/>
                        <i class="iconfont icon-user"/>
                    </div>
                </el-form-item>
                <!--validator是自定义校验规则，校验规则是自己写的方法checkPhone。即在执行组件校验规则的同时执行自定义校验规则checkPhone-->
                <el-form-item class="input-prepend restyle no-radius" prop="phoneNumber" :rules="[{ required: true, message: '请输入手机号码', trigger: 'blur' },{validator:checkPhone, trigger: 'blur'}]">
                    <div>
                        <el-input type="text" placeholder="手机号" v-model="registerVo.phoneNumber"/>
                        <i class="iconfont icon-phone"/>
                    </div>
                </el-form-item>
                <el-form-item class="input-prepend restyle no-radius" prop="code" :rules="[{ required: true, message: '请输入验证码', trigger: 'blur' },{validator:checkSmsCode, trigger: 'blur'}]">
                    <div style="width: 100%; display: block; float: left; position: relative">
                        <el-input type="text" placeholder="验证码" v-model="registerVo.code"/>
                        <i class="iconfont icon-phone"/>
                    </div>
                    <div class="btn" style=" position:absolute ; right: 0 ; top: 6px ; width:40% ;">
                        <a href="javascript:" type="button" @click="sendSmsCode()" :value="codeTest" style="border: none;background-color: none">{{codeTest}}</a>
                    </div>
                </el-form-item>
                <el-form-item class="input-prepend restyle no-radius" prop="password" :rules="[{ required:true, message: '请输入密码', trigger: 'blur' }]">
                    <div>
                        <el-input type="password" placeholder="设置密码" v-model="registerVo.password"/>
                        <i class="iconfont icon-password"/>
                    </div>
                </el-form-item>
                <!--这个prop属性必须和v-model的保持一致，否则value绑定不上去，甚至发生自定义回调函数无法调用的情况-->
                <el-form-item class="input-prepend" prop="verifyPassword" :rules="[{ required:true, message: '请再次输入密码', trigger: 'blur' },{validator:checkPassword, trigger: 'blur'}]">
                    <div>
                        <el-input type="password" placeholder="确认密码" v-model="registerVo.verifyPassword"/>
                        <i class="iconfont icon-password"/>
                    </div>
                </el-form-item>
                <div class="btn">
                    <input type="button" class="sign-up-button" value="注册" @click="submitRegister()">
                </div>
                <br>
                <p class="sign-up-msg">点击 “注册” 即表示您同意并愿意遵守<br>
                    <a target="_blank" href="http://www.jianshu.com/p/c44d171298ce">用户协议</a>
                    和 
                    <a target="_blank" href="http://www.jianshu.com/p/2ov8x3">隐私政策</a> 。
                </p>
            </el-form>
            <!-- 更多注册方式 -->
            <div class="more-sign">
                <h6>社交帐号注册</h6>
                <ul>
                    <li>
                        <a id="weixin" class="weixin" target="_blank" href="http://localhost:9001/api/ucenter/wx/login">
                            <i class="iconfont icon-weixin"/>
                        </a>
                    </li>
                    <li>
                        <a id="qq" class="qq" target="_blank" href="#"><i class="iconfont icon-qq"/></a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>
<script>
    //这个是引入页面中用到的样式文件
    import '~/assets/css/sign.css'
    import '~/assets/css/iconfont.css'
    import register from '@/api/register'
    export default {
        //这个是定义页面样式的文件为assets/css/sign.css，错了这句话的意思是使用layout中的sign.vue布局，引用sign.css是import的效果
        layout: 'sign',
        data() {
            return {
                registerVo: {
                    phoneNumber: '',
                    code: '',
                    nickname: '',
                    password: '',
                    verifyPassword: ''
                },
                sending: true, //是否发送验证码，true表示可发送，false表示发送按钮禁用
                //验证码再次发送倒计时效果，倒计时效果基于html中的定时器方法，每隔一段时间执行一次js方法：setInterval("alert('test')",1000)
                //效果是每隔一秒让前一个参数表示的js代码执行一次，倒计时只需要每隔1s让second值自减1
                second: 60, //倒计时初始值为60s
                codeTest: '获取验证码',
                phoneVerifyMsg: '',
                smsCodeVerifyMsg: ''
            }
        },
        methods: {
            //点击获取验证码调用接口发送手机验证码
            sendSmsCode() {
                //sending = false
                //his.sending原为true,请求成功， !this.sending == true，主要是防止有人把disabled属性去掉，多次点击；
                if (!this.sending) return;
                //debugger
                // prop 换成你想监听的prop字段
                this.$refs.userForm.validateField('phoneNumber', (errMsg) => {
                if (errMsg == '') {
                    register.sendSmsCode(this.registerVo.phoneNumber).then(res => {
                        this.sending = false;
                        this.timeDown();
                    });
                }
                })
            },
            //验证码可再次发送倒计时效果
            timeDown() {
                let result = setInterval(() => {
                    --this.second;//每秒时间自减1
                    this.codeTest = this.second//显示剩余秒值
                    if (this.second < 1) {
                        clearInterval(result);//用于停止 setInterval() 方法执行的函数代码,参数是必须的，为setInterval的返回值
                        this.sending = true;//倒计时结束可发送验证码
                        this.second = 60;//初始化倒计时秒值
                        this.codeTest = "获取验证码"//倒计时结束让显示值恢复初始值
                    }
                }, 1000);
            },
            //请求注册接口，提交注册数据
            submitRegister() {
                this.$refs['userForm'].validate((valid) => {
                    if (valid) {
                        register.submitRegister(this.registerVo).then(response => {
                            if(response.data.code=20000){
                                //提示注册成功
                                this.$message({
                                    type: 'success',
                                    message: "注册成功"
                                })
                                //跳转登录页面
                                this.$router.push({path: '/login'})
                            }else{
                                this.$message({
                                    type: 'error',
                                    message: response.message
                                })
                            }
                        })
                    }
                })
            },
            checkPhone (rule, value, callback) {
                //正则表达式校验手机号码，自定义校验方法自动传参value为输入框的值，callback是返回参数，校验成功直接调用callback，校验失败传参创建一个error对象并输入提示信息
                //字符串以1开始，/是正则表达式开始结束的标志，[34578]表示第二位匹配34578，然后匹配9次数字，$表示到此该字符串结束，整体可以看做一个对象用test和值做校验
                if (!(/^1[34578]\d{9}$/.test(value))) {
                    return callback(new Error('手机号码格式不正确'))
                }
                //验证手机号码是否已经注册
                register.verifyPhoneNumber(value).then(response=>{
                    this.phoneVerifyMsg=response.data.message
                    if(this.phoneVerifyMsg){
                        //new Error('该手机号码已经被注册')不能传变量，只能手写字符串，否则提示信息无法消失
                        return callback(new Error('该手机号码已经被注册'))
                    }
                    return callback()
                })
            },
            checkSmsCode(rule, value, callback){
                //验证码格式判断
                if (!(/^\d{6}$/.test(value))) {
                    return callback(new Error('验证码格式不正确'))
                }
                if(!this.registerVo.phoneNumber){
                    return callback(new Error('请填写手机号码'))
                }
                //验证短信验证码是否匹配
                register.verifySmsCode(this.registerVo.phoneNumber,value).then(response=>{
                    this.smsCodeVerifyMsg=response.data.message
                    if(this.smsCodeVerifyMsg){
                        //new Error('该手机号码已经被注册')不能传变量，只能手写字符串，否则提示信息无法消失
                        return callback(new Error('验证码错误'))
                    }
                    this.$message({
                        type: 'success',
                        message: "验证码验证成功"
                    })
                    return callback()
                })
            },
            checkPassword(rule, value, callback){
                if(!this.registerVo.password){
                    return callback(new Error('请先填写用户密码'))
                }
                if(!(this.registerVo.password==value)){
                    return callback(new Error('前后密码不匹配'))
                }
                this.$message({
                    type: 'success',
                    message: "密码匹配成功"
                })
                return callback()
            }
        }
    }
</script>