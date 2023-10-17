import request from '@/utils/request'
export default {
    //分页查询讲师列表
    pageTeacherList(curPage,limit) {
        return request({
            url: `/eduservice/front/pageTeacherList/${curPage}/${limit}`,
            method: 'get'
        })
    },
    getTeacherInfoById(teacherId){
        return request({
            url: `/eduservice/front/getTeacherInfoById/${teacherId}`,
            method: 'get'
        })
    }
}