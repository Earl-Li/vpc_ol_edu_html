import request from '@/utils/request'
export default {
    //分页查询课程评论
    getCommentByCourseId(courseId , curPage, limit) {
        return request({
            url: `/eduservice/frontcomment/getCommentByCourseId/${courseId}/${curPage}/${limit}`,
            method: 'get'
        })
    },
    //添加评论
    addComment(comment) {
        return request({
            url: `/eduservice/frontcomment/addComment`,
            method: 'post',
            data: comment
        })
    }
}