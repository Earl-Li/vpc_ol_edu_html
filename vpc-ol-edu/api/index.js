import request from '@/utils/request'
export default {
    getBannerList(){
        return request({
            url: '/educms/front/getAllBanner',
            method: 'get'
        })
    },
    getTeacherList(){
        return request({
            url: '/educms/front/getPopularTeacher',
            method: 'get'
        })
    },
    getCourseList(){
        return request({
            url: '/educms/front/getPopularCourse',
            method: 'get'
        })
    }
}