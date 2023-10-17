import request from '@/utils/request'
export default {
    //1、根据课程id创建订单
    createOrder(courseId) {
        return request({
            url: `/eduorder/order/createOrder/${courseId}`,
            method: 'get'
        })
    },
    //2、根据订单号获取订单
    getOrderByOrderNo(orderNo) {
        return request({
            url: `/eduorder/order/queryOrder/${orderNo}`,
            method: 'get'
        })
    },
    //3、根据订单号生成微信支付二维码
    generateQRCode(orderNo) {
        return request({
            url: `/eduorder/paylog/generateQRCode/${orderNo}`,
            method: 'get'
        })
    },
    //4、根据订单号查询订单支付状态
    queryPayStatus(orderNo) {
        return request({
            url: `/eduorder/paylog/queryPayStatus/${orderNo}`,
            method: 'get'
        })
    }
}