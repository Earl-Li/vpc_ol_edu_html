<template>
    <div class="app-container">
        <h2 style="text-align: center;">发布新课程</h2>
        <el-steps :active="3" process-status="wait" align-center style="margin-bottom:40px;">
            <el-step title="填写课程基本信息"/>
            <el-step title="创建课程大纲"/>
            <el-step title="发布课程"/>
        </el-steps>
        <div class="ccInfo">
            <img :src="publishConfirmCourseInfo.cover">
            <div class="main">
                <h2>{{ publishConfirmCourseInfo.title }}</h2>
                <p class="gray">
                    <span>共{{ publishConfirmCourseInfo.courseTotalTime }}课时</span>
                </p>
                <p>
                    <span>所属分类：{{ publishConfirmCourseInfo.firstLevelSubject }} — {{publishConfirmCourseInfo.secondLevelSubject }}</span>
                </p>
                <p>
                    课程讲师：{{ publishConfirmCourseInfo.teacherName }}
                </p>
                <h3 class="red">￥{{ publishConfirmCourseInfo.price }}</h3>
            </div>
        </div>
        <div>
            <el-button @click="previous">返回修改</el-button>
            <el-button :disabled="saveBtnDisabled" type="primary" @click="publish">发布课程</el-button>
        </div>
    </div>
</template>
<script>
    import course from '@/api/edu/course'
    export default {
        data() {
            return {
                saveBtnDisabled: false, // 保存按钮是否禁用
                courseId: '', // 所属课程
                publishConfirmCourseInfo: {}
            }
        },
        created() {
            this.init()
        },
        methods: {
            init() {
                if (this.$route.params && this.$route.params.id) {
                    this.courseId = this.$route.params.id
                    // 根据id获取课程基本信息
                    this.queryPublishConfirmCourseInfo(this.courseId)
                }
            },
            queryPublishConfirmCourseInfo(courseId) {
                course.getPublishConfirmCourseInfoByCourseId(courseId)
                .then(response => {
                    this.publishConfirmCourseInfo = response.data.publishConfirmCourseInfo
                })
            },
            previous() {
                this.$router.push({ path: '/course/chapter/' + this.courseId })
            },
            publish() {
                this.$confirm('是否确认发布该课程?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.saveBtnDisabled=true
                    course.publishCourse(this.courseId)
                    .then(response => {
                        this.$message({
                            type: 'success',
                            message: '课程发布成功!'
                        })
                        this.$router.push({ path: '/course/list' })
                    })
                })
                
            }
        }
    }
</script>
<style scoped>
    .ccInfo {
        background: #f5f5f5;
        padding: 20px;
        overflow: hidden;
        border: 1px dashed #DDD;
        margin-bottom: 40px;
        position: relative;
    } 
    .ccInfo img {
        background: #d6d6d6;
        width: 500px;
        height: 278px;
        display: block;
        float: left;
        border: none;
    } 
    .ccInfo .main{
        margin-left: 520px;
    }
    .ccInfo .main h2 {
        font-size: 28px;
        margin-bottom: 30px;
        line-height: 1;
        font-weight: normal;
    }
    .ccInfo .main p {
        margin-bottom: 10px;
        word-wrap: break-word;
        line-height: 24px;
        max-height: 48px;
        overflow: hidden;
    } 
    .ccInfo .main p {
        margin-bottom: 10px;
        word-wrap: break-word;
        line-height: 24px;
        max-height: 48px;
        overflow: hidden;
    }
    .ccInfo .main h3 {
        left: 540px;
        bottom: 20px;
        line-height: 1;
        font-size: 28px;
        color: #d32f24;
        font-weight: normal;
        position: absolute;
    }
</style>