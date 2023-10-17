<template>
    <div class="cart py-container">
        <!--主内容-->
        <div class="checkout py-container pay">
            <div class="checkout-tit">
                <h4 class="fl tit-txt">
                    <span class="success-icon">
                    </span>
                    <span class="success-info">
                        <!--out_trade_no是订单号-->
                        订单提交成功，请您及时付款！订单号： {{payObj.out_trade_no}}
                    </span>
                </h4>
                <span class="fr">
                    <!--total_fee课程总价-->
                    <em class="sui-lead">
                        应付金额： </em><em class="orange money">￥{{payObj.total_fee}}
                    </em>
                </span>
                <div class="clearfix">

                </div>
            </div>
            <div class="checkout-steps">
                <div class="fl weixin">
                    微信支付
                </div>
                <div class="fl sao">
                    <p class="red">
                        请使用微信扫一扫。 
                    </p>
                    <div class="fl code">
                        <!--下载微信支付二维码需要使用qriously组件，之前下过了，code_url是二维码地址-->
                        <!-- <img id="qrious" src="~/assets/img/erweima.png" alt=""> -->
                        <!-- <qriously value="weixin://wxpay/bizpayurl?pr=R7tnDpZ":size="338"/> -->
                        <qriously :value="payObj.code_url" :size="338"/>
                        <div class="saosao">
                            <p>请使用微信扫一扫</p>
                            <p>扫描二维码支付</p>
                        </div>
                    </div>
                </div>
                <div class="clearfix">
                </div>
                <!-- <p><a href="pay.html" target="_blank">> 其他支付方式</a></p> -->
            </div>
        </div>
    </div>
</template>
<script>
import order from '@/api/order'
export default {
    //根据订单id生成微信支付二维码,后端返回的是一个Map集合,data就是这个集合，里面存有二维码地址
    asyncData({params, error}) {
        return order.generateQRCode(params.no).then(response => {
            //不加return也会读取不到
            return {
                payObj: response.data.data
            }
        })
    },
    data() {
        return {
            timer: null, // 定时器名称
            initQCode: '',
            timerStatus:''//定时器的返回值，当清除该返回值定时器调用失效
        }
    },
    mounted() {
        //在页面渲染之后执行，订单号是异步调用获取，这里需要使用订单号，在mounted方法时开始执行
        //每隔三秒，去查询一次支付状态，定时器中第一个参数调用方法发送后端接口查询订单状态请求
        this.timerStatus = setInterval(() => {this.queryPayStatus(this.payObj.out_trade_no)}, 3000);
    },
    methods: {
        //查询支付状态的方法
        queryPayStatus(out_trade_no) {
            order.queryPayStatus(out_trade_no).then(response => {
                if (response.data.success) {
                    //如果支付成功，清除定时器，不清楚定时器这个页面会一直调用服务器接口
                    clearInterval(this.timerStatus)
                    this.$message({
                        type: 'success',
                        message: '支付成功!'
                    })
                    //跳转到课程详情页面观看视频
                    this.$router.push({path: '/course/' + this.payObj.course_id})
                }
            })
        }
    }
}
</script>