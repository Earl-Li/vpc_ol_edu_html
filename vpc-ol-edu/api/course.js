import request from '@/utils/request'
export default {
    //分页多条件查询课程列表
    pageFactorCourse(curPage, limit, courseQueryFactor) {
        return request({
            url: `/eduservice/front/pageFactorCourse/${curPage}/${limit}`,
            method: 'post',
            data: courseQueryFactor
        })
    },
    //调用后台管理系统的接口获取课程全部一二级分类
    findAllSubject(){
        return request({
            url: `/eduservice/subject/getAllSubject`,
            method: 'get'
        })
    },
    //查询课程详情接口
    getCourseInfoByCourseId(courseId){
        return request({
            url: `/eduservice/front/getFrontCourseInfo/${courseId}`,
            method: 'get'
        })
    }
}