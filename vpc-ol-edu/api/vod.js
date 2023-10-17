import request from '@/utils/request'
export default {
    //根据视频id获取视频播放凭证
    getPlayAuth(videoId) {
        return request({
            url: `/eduvod/filevod/getPlayAuth/${videoId}`,
            method: 'get'
        })
    }
}