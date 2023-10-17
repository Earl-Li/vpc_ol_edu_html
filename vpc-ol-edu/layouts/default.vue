<template>
  <div class="in-wrap">
    <!-- 公共头引入 -->
    <header id="header">
      <section class="container">
        <h1 id="logo">
          <a href="#" title="谷粒学院">
            <img src="~/assets/img/logo.png" width="100%" alt="谷粒学院">
          </a>
        </h1>
        <div class="h-r-nsl">
          <ul class="nav">
            <!--router-link 的to属性设置路由跳转地址，Nuxt的路由跳转规则是跳转路径为/course，会在pages中找course文件夹
            ，在course文件夹中去找index.vue；同样会拼接default.vue和course/index.vue的内容-->
            <router-link to="/" tag="li" active-class="current" exact>
              <a>首页</a>
            </router-link>
            <router-link to="/course" tag="li" active-class="current">
              <a>课程</a>
            </router-link>
            <router-link to="/teacher" tag="li" active-class="current">
              <a>名师</a>
            </router-link>
            <router-link to="/article" tag="li" active-class="current">
              <a>文章</a>
            </router-link>
            <router-link to="/qa" tag="li" active-class="current">
              <a>问答</a>
            </router-link>
          </ul>
          <!-- / nav -->
          <ul class="h-r-login">
            <!--用户id没有值就显示登录注册-->
            <li v-if="!userInfo.id" id="no-login">
              <a href="/login" title="登录">
                <em class="icon18 login-icon">&nbsp;</em>
                <span class="vam ml5">登录</span>
              </a>
              |
              <a href="/register" title="注册">
                <span class="vam ml5">注册</span>
              </a>
            </li>
            <!--用户id有值就显示以下两个li标签-->
            <li v-if="userInfo.id" id="is-login-one" class="mr10">
              <a id="headerMsgCountId" href="#" title="消息">
                <em class="icon18 news-icon">&nbsp;</em>
              </a>
              <q class="red-point" style="display: none">&nbsp;</q>
            </li>
            <li v-if="userInfo.id" id="is-login-two" class="h-r-user">
              <a href="/ucenter" title>
                <img :src="userInfo.avatar" width="30" height="30" class="vam picImg" alt>
                <span id="userName" class="vam disIb">{{ userInfo.nickname }}</span>
              </a>
              &nbsp;
              <a href="javascript:void(0);" title="退出" @click="logout()" class="ml5">退出</a>
            </li>
          </ul>
          <aside class="h-r-search">
            <form action="#" method="post">
              <label class="h-r-s-box">
                <input type="text" placeholder="输入你想学的课程" name="queryCourse.courseName" value>
                <button type="submit" class="s-btn">
                  <em class="icon18">&nbsp;</em>
                </button>
              </label>
            </form>
          </aside>
        </div>
        <aside class="mw-nav-btn">
          <div class="mw-nav-icon"></div>
        </aside>
        <div class="clear"></div>
      </section>
    </header>
    
    <nuxt/>

    <!-- 公共底引入 -->
    <footer id="footer">
      <section class="container">
        <div class>
          <h4 class="hLh30">
            <span class="fsize18 f-fM c-999">友情链接</span>
          </h4>
          <ul class="of flink-list">
            <li>
              <a href="http://www.atguigu.com/" title="尚硅谷" target="_blank">尚硅谷</a>
            </li>
          </ul>
          <div class="clear"></div>
        </div>
        <div class="b-foot">
          <section class="fl col-7">
            <section class="mr20">
              <section class="b-f-link">
                <a href="#" title="关于我们" target="_blank">关于我们</a>|
                <a href="#" title="联系我们" target="_blank">联系我们</a>|
                <a href="#" title="帮助中心" target="_blank">帮助中心</a>|
                <a href="#" title="资源下载" target="_blank">资源下载</a>|
                <span>服务热线：010-56253825(北京) 0755-85293825(深圳)</span>
                <span>Email：info@atguigu.com</span>
              </section>
              <section class="b-f-link mt10">
                <span>©2018课程版权均归谷粒学院所有 京ICP备17055252号</span>
              </section>
            </section>
          </section>
          <aside class="fl col-3 tac mt15">
            <section class="gf-tx">
              <span>
                <img src="~/assets/img/wx-icon.png" alt>
              </span>
            </section>
            <section class="gf-tx">
              <span>
                <img src="~/assets/img/wb-icon.png" alt>
              </span>
            </section>
          </aside>
          <div class="clear"></div>
        </div>
      </section>
    </footer>
  </div>
</template>
<script>
import '~/assets/css/reset.css'
import '~/assets/css/theme.css'
import '~/assets/css/global.css'
import '~/assets/css/web.css'
import '~/assets/css/base.css'
import '~/assets/css/activity_tab.css'
import '~/assets/css/bottom_rec.css'
import '~/assets/css/nice_select.css'
import '~/assets/css/order.css'
import '~/assets/css/swiper-3.3.1.min.css'
import "~/assets/css/pages-weixinpay.css"
import cookie from 'js-cookie'
import login from '@/api/login'

export default {
  data() {
    return {
      token: '',
      userInfo: {
        id: '',
        avatar: '',
        phoneNumber: '',
        nickname: '',
      }
    }
  },
  created() {
    this.token = this.$route.query.user_token
    if (this.token) {
      this.wxLogin()
    }
    this.init()
  },
  methods: {
    init(){
      this.showInfo()
    },
    showInfo() {
      //从cookie中获取用户信息，这个信息是json字符串，不是json对象，需要使用JSON.parse(jsonStr)将其转换成json对象
      //因为cookie中存放json对象是字符串的形式，以前直接响应数据不需要做json对象的转换，Js中的JSON就是来干这个的
      var userInfoJsonStr = cookie.get("user_info");
      if (userInfoJsonStr) {
        this.userInfo = JSON.parse(userInfoJsonStr)
      }
    },
    logout() {
      cookie.set('user_info', "", { domain: 'localhost' })
      cookie.set('user_token', "", { domain: 'localhost' })
      //跳转页面
      window.location.href = "/"
    },
    wxLogin() {
      if (this.token == '') return
      //把token存在cookie中、也可以放在localStorage中
      //window.localStorage.setItem("user_token",this.token)
      cookie.set('user_token', this.token, { domain:'localhost' })
      cookie.set('user_info', '', { domain: 'localhost' })
      //var curToken=cookie.get("user_token",this.token)
      //console.log('*************'+curToken)
      //登录成功根据token获取用户信息
      login.getLoginInfo().then(response => {
        this.userInfo = response.data.data.loginInfo
        //将用户信息记录在cookie中，每次访问domain的ip都会发送，JSON.stringify(this.loginInfo)
        cookie.set('user_info', JSON.stringify(this.userInfo), { domain: 'localhost'})
      })
    }
  }
}

</script>