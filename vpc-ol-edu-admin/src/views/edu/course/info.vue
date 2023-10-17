<template>
    <div class="app-container">
        <h2 style="text-align: center;">发布新课程</h2>
        <el-steps :active="1" process-status="wait" align-center style="margin-bottom:40px;">
            <el-step title="填写课程基本信息"/>
            <el-step title="创建课程大纲"/>
            <el-step title="最终发布"/>
        </el-steps>
        <el-form label-width="120px">
            <el-form-item label="课程标题">
                <el-input v-model="courseInfo.title" placeholder=" 示例：高放废液接收系统设备概述 [ 课程名称注意大小写 ]"/>
            </el-form-item>
            <!-- 所属分类  -->
            <el-form-item label="课程分类">
                <!--这里的v-model是默认值的意思吗-->
                <el-select v-model="courseInfo.subjectParentId" placeholder="请选择一级分类" @change="firstLevelSubjectChange">
                    <el-option v-for="firstLevelSubject in firstLevelSubjects" :key="firstLevelSubject.id" :label="firstLevelSubject.title" :value="firstLevelSubject.id"/>
                </el-select>
                <el-select v-model="courseInfo.subjectId" placeholder="请选择二级分类">
                    <el-option v-for="secondLevelSubject in secondLevelSubjects" :key="secondLevelSubject.id" :label="secondLevelSubject.title" :value="secondLevelSubject.id"/>
                </el-select>
            </el-form-item>
            <!-- 课程讲师  -->
            <!-- 所属分类：级联下拉列表 -->
                <!-- 一级分类 -->
            <el-form-item label="课程讲师">
                <!--查询所有讲师的接口不能分页，需要重新定义查所有讲师的接口方法，注意讲师最终提交的是讲师的id -->
                <el-select v-model="courseInfo.teacherId" placeholder="请选择">
                    <el-option v-for="teacher in teacherList" :key="teacher.id" :label="teacher.name" :value="teacher.id"/>
                </el-select>
            </el-form-item>
            <!-- 课程总时长 -->
            <el-form-item label="总课时">
                <el-input-number :min="0" v-model="courseInfo.courseTotalTime" controls-position="right" placeholder="单位：分钟"/>
            </el-form-item>
            <!-- 课程简介 -->
            <el-form-item label="课程简介">
                <tinymce :height="300" v-model="courseInfo.description"/>
            </el-form-item>
            <!-- 课程封面-->
            <!--
                :show-file-list="false" 显示文件上传列表，true为显示，false为不显示
                :on-success="handleAvatarSuccess"  上传成功执行的方法
                :before-upload="beforeAvatarUpload"  上传之前执行的方法
                :action="BASE_API+'/admin/oss/file/upload?host=cover'"  上传的接口地址
                class="avatar-uploader"  上传组件样式
                这里auto-upload  自动上传省略了，省略的效果是选择文件后会自动上传
            -->
            <el-form-item label="课程封面">
                <el-upload 
                :show-file-list="false" 
                :on-success="handleAvatarSuccess" 
                :before-upload="beforeAvatarUpload" 
                :action="BASE_API+'/eduoss/fileoss'" 
                class="avatar-uploader">
                    <!--一般为了效果好，会默认一个静态资源来提示该处可以添加更改封面图片，这个资源一般存放在静态资源文件夹static下-->
                    <img :src="courseInfo.cover">
                </el-upload>
            </el-form-item>
            <!--课程价格-->
            <el-form-item label="课程价格">
                <el-input-number :min="0" v-model="courseInfo.price" controls-position="right" placeholder="免费课程请设置为0元"/> 元
            </el-form-item>
            <el-form-item>
                <el-button :disabled="saveBtnDisabled" type="primary" @click="next">保存并下一步</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>
<script>
    import course from '@/api/edu/course'
    import subject from '@/api/edu/subject'
    import Tinymce from '@/components/Tinymce'
    const defaultForm = {
        id: '',
        title: '',
        subjectId: '',
        teacherId: '',
        courseTotalTime: 0,
        description: '',
        cover: 'https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/03/364822ae6edd429db705a06be39a660c高放废液玻璃固化.jpg',
        price: 0,
        subjectParentId:''
    }
    export default {
        components: { Tinymce },
        data() {
            return {
                courseInfo: defaultForm,
                saveBtnDisabled: false, // 保存按钮是否禁用
                teacherList: [],
                firstLevelSubjects: [],//课程一级分类
                secondLevelSubjects: [],//课程二级分类
                BASE_API:process.env.BASE_API,
                cover: 'https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/03/364822ae6edd429db705a06be39a660c高放废液玻璃固化.jpg'
            }
        },
        watch: {
            $route(to, from) {
                this.init()
            }
        },
        created() {
            this.init()
        },
        methods: {
            //课程基本信息回显的功能
            echoCourseInfo(courseId){
                course.echoCourseInfo(courseId)
                .then(response=>{
                    this.courseInfo=response.data.courseInfo
                    this.handleSecondLevelSubject(this.courseInfo.subjectParentId)
                })
            },
            //封面上传成功的方法，一般是得到封面上传后访问的地址，把封面地址赋值给课程信息的cover
            handleAvatarSuccess(response,file){
                this.courseInfo.cover=response.data.url
            },
            //封面上传之前执行的方法,一般用于检查文件类型和文件大小
            beforeAvatarUpload(file){
                //上传文件类型是'image/jpeg'时可以通过
                const isJPG = file.type === 'image/jpeg'
                //上传文件大小小于2MB不会报错
                const isLt2M = file.size / 1024 / 1024 < 2
                if (!isJPG) {
                    this.$message.error('上传头像图片只能是 JPG 格式!')
                }
                if (!isLt2M) {
                    this.$message.error('上传头像图片大小不能超过 2MB!')
                }
                return isJPG && isLt2M
            },
            //一级课程分类变化事件触发方法
            firstLevelSubjectChange(value){//框架封装了事件自动传参当前标签的值
                this.handleSecondLevelSubject(value)
                this.courseInfo.subjectId = ''//一级下拉列表变化，二级下拉列表绑定的变量先初始化，一级没变，二级不变，没有这行代码一级变了二级不会变，会给编辑者造成歧义
            },
            //二级课程的级联行为
            handleSecondLevelSubject(value){
                for(var i=0;i<this.firstLevelSubjects.length;i++){
                    var curSubject= this.firstLevelSubjects[i]
                    if(value===curSubject.id){
                        this.secondLevelSubjects=curSubject.children
                    }
                }
            },
            //获取所有课程分类的列表
            getAllSubjectList(){
                subject.findAllSubject()
                .then(response=>{
                    this.firstLevelSubjects=response.data.subjects
                }).catch(error=>{
                    console.log(error)
                })
            },
            //获取所有讲师列表
            selectedTeacher(){
                course.getAllTeacher()
                .then(response=>{
                    this.teacherList=response.data.items
                })
                .catch((response) => {
                    this.$message({
                        type: 'error',
                        message: response.message
                    })
                })
            },
            init() {
                // 初始化分类列表
                this.getAllSubjectList()
                // 获取讲师列表
                this.selectedTeacher()
                if (this.$route.params && this.$route.params.id) {
                    this.courseInfo.id = this.$route.params.id
                    // 根据id获取课程基本信息
                    this.echoCourseInfo(this.courseInfo.id)
                } else {
                    defaultForm.id=''
                    this.courseInfo = { ...defaultForm }
                    //手动清空富文本编辑器的内容
                    tinymce.activeEditor.setContent("");
                }
            },
            next() {
                this.saveBtnDisabled = true
                console.log(this.courseInfo)
                if (!this.courseInfo.id) {
                    this.saveData()
                } else {
                    this.updateData()
                }
            },
            // 保存
            saveData() {
                course.saveCourseInfo(this.courseInfo)
                .then(response => {
                    this.$message({
                        type: 'success',
                        message: '添加成功!'
                    })
                    //console.log(response.data.courseId)
                    this.$router.push({ path: '/course/chapter/' + response.data.courseId})
                })
                .catch((response) => {
                    this.$message({
                        type: 'error',
                        message: response.message
                    })
                })
            },
            updateData() {
                course.updateCourseInfo(this.courseInfo)
                .then(response => {
                    this.$message({
                        type: 'success',
                        message: '修改成功!'
                    })
                    this.$router.push({ path: '/course/chapter/' + this.courseInfo.id})
                })
                .catch((response) => {
                    this.$message({
                        type: 'error',
                        message: response.message
                    })
                })
            }
        }
    }
</script>
<!--scoped表示该样式只有当前页面可用-->
<!--<style scoped>
    .tinymce-container {
        line-height: 29px;
    }
</style>-->
