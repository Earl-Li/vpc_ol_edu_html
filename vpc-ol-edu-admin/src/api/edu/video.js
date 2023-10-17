import request from '@/utils/request'
export default {
    queryVideo(videoId){
        return request({
            url: `/eduservice/video/queryVideo/${videoId}`,
            method: 'get'
        })
    },
    addVideo(video){
        return request({
            url: '/eduservice/video/addVideo',
            method: 'post',
            data: video
        })
    },
    updateVideo(video){
        return request({
            url: `/eduservice/video/updateVideo`,
            method: 'put',
            data: video
        })
    },
    deleteVideo(videoId){
        return request({
            url: `/eduservice/video/deleteVideo/${videoId}`,
            method: 'delete',
        })
    }
}