import request from '@/utils/request'

export default {
    //讲师列表，讲师条件分页查询，current为当前页，limit为每页记录数，teacherQuery为条件对象
    findAllTeacherPaging(current,limit,teacherQuery){
        return request({
            //url的两种写法，推荐第二种
            //url: '/eduservice/teacher/pageTeacher/'+current+'/'+limit,
            url: `/eduservice/teacher/pageFactorTeacher/${current}/${limit}`,//带条件查询和不带条件查询一定要区分清楚，两者请求方式都不同，即使加了跨域请求注解还是会报错没有跨域请求权限
            method: 'post',
            //teacherQuery是查询条件对象，后端使用@RequestBody注解获取数据需要前端传入json数据，data属性对应对象会自动将对象转成json格式传入接口
            data: teacherQuery
        })
    },
    //通过讲师id逻辑删除讲师
    deleteTeacherId(id){
        return request({
            url: `/eduservice/teacher/${id}`,
            method: 'delete'
        })
    },
    //添加讲师
    addTeacher(teacher){
        return request({
            url: `/eduservice/teacher/addTeacher`,
            method: 'post',
            data: teacher
        })
    },
    //修改页面根据讲师id对讲述信息在添加页面进行回显
    getTeacherInfo(id){
        return request({
            url: `/eduservice/teacher/queryTeacher/${id}`,
            method: 'get'
        })
    },
    //修改讲师接口
    updateTeacherInfo(teacher){
        return request({
            url: `/eduservice/teacher/updateTeacher/${teacher.id}`,
            method: 'put',
            data: teacher
        })
    }
}