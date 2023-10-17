import request from '@/utils/request'

export default {
    findAllSubject(){
        return request({
            url: `/eduservice/subject/getAllSubject`,
            method: 'get'
        })
    }
}