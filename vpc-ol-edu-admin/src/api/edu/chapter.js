import request from '@/utils/request'
export default {
    queryChaptersByCourseId(courseId){
        return request({
            url: '/eduservice/chapter/chapterList/'+courseId,
            method: 'get'
        })
    },
    addChapter(chapter){
        return request({
            url: '/eduservice/chapter/addChapter',
            method: 'post',
            data: chapter
        })
    },
    queryChapter(chapterId){
        return request({
            url: `/eduservice/chapter/queryChapter/${chapterId}`,
            method: 'get',
        })
    },
    updateChapter(chapter){
        return request({
            url: `/eduservice/chapter/updateChapter`,
            method: 'put',
            data: chapter
        })
    },
    deleteChapter(chapterId){
        return request({
            url: `/eduservice/chapter/deleteChapter/${chapterId}`,
            method: 'delete',
        })
    }
}