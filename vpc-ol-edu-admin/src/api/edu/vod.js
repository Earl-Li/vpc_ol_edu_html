import request from '@/utils/request'
export default{
    removeAliYunVideo(videoId){
        return request({
            url: `eduvod/filevod/removeVodVideo/${videoId}`,
            method: 'delete',
        })
    }
}
