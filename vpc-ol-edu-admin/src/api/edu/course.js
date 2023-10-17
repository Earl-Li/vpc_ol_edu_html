import request from '@/utils/request'
export default {
    getAllTeacher(){
        return request({
            url: `/eduservice/teacher/findAll`,
            method: 'get'
        })
    },
    saveCourseInfo(courseInfo) {
        return request({
            url: `/eduservice/course/addCourseInfo`,
            method: 'post',
            data: courseInfo
        })
    },
    echoCourseInfo(courseId){
        return request({
            url: `/eduservice/course/addCourseInfo/${courseId}`,
            method: 'get'
        })
    },
    updateCourseInfo(courseInfo){
        return request({
            url: `/eduservice/course/addCourseInfo/`,
            method: 'put',
            data: courseInfo
        })
    },
    getPublishConfirmCourseInfoByCourseId(courseId){
        return request({
            url: `/eduservice/course/getPublishConfirmCourseInfo/${courseId}`,
            method: 'get'
        })
    },
    publishCourse(courseId){
        return request({
            url: `/eduservice/course/publishCourse/${courseId}`,
            method: 'put'
        })
    },
    //课程列表，课程条件分页查询，current为当前页，limit为每页记录数，teacherQuery为条件对象
    findAllCoursePaging(current,limit,courseQuery){
        return request({
            url: `/eduservice/course/pageFactorCourse/${current}/${limit}`,//带条件查询和不带条件查询一定要区分清楚，两者请求方式都不同，即使加了跨域请求注解还是会报错没有跨域请求权限
            method: 'post',
            //courseQuery是查询条件对象，后端使用@RequestBody注解获取数据需要前端传入json数据，data属性对应对象会自动将对象转成json格式传入接口
            data: courseQuery
        })
    },
    //根据课程id移除课程的小节、章节、描述、课程本身
    removeCourseByCourseId(courseId){
        return request({
            url: `/eduservice/course/deleteCourse/${courseId}`,//带条件查询和不带条件查询一定要区分清楚，两者请求方式都不同，即使加了跨域请求注解还是会报错没有跨域请求权限
            method: 'delete'
        })
    }
}