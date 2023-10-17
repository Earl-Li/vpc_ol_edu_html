<template>
    <div class="app-container">
        <h2>课程列表</h2>
        <!--:inline表示所有的内容在一行内展示-->
        <el-form :inline="true" class="demo-form-inline">
            <!--课程标题查询条件-->
            <el-form-item>
                <el-input v-model="courseQuery.title" placeholder="课程标题" />
            </el-form-item>
            <!--课程分类查询条件-->
            <el-form-item>
                <el-select v-model="courseQuery.subjectParentId" placeholder="课程一级分类" @change="firstLevelSubjectChange">
                    <el-option v-for="firstLevelSubject in firstLevelSubjects" :key="firstLevelSubject.id" :label="firstLevelSubject.title" :value="firstLevelSubject.id"/>
                </el-select>
                <el-select v-model="courseQuery.subjectId" placeholder="课程二级分类">
                    <el-option v-for="secondLevelSubject in secondLevelSubjects" :key="secondLevelSubject.id" :label="secondLevelSubject.title" :value="secondLevelSubject.id"/>
                </el-select>
            </el-form-item>
            <!--课程讲师查询条件-->
            <el-form-item label="课程讲师">
                <!--查询所有讲师的接口不能分页，需要重新定义查所有讲师的接口方法，注意讲师最终提交的是讲师的id -->
                <el-select v-model="courseQuery.teacherId" placeholder="讲师">
                    <el-option v-for="teacher in teacherList" :key="teacher.id" :label="teacher.name" :value="teacher.id"/>
                </el-select>
            </el-form-item>
            <el-form-item label="发布状态">
                <el-select v-model="courseQuery.status" placeholder="发布状态">
                    <el-option key="Normal" label='已发布' value="Normal"/>
                    <el-option key="Draft" label='未发布' value="Draft"/>
                </el-select>
            </el-form-item>
            <!--button按钮，@click="fetchData()"是点击执行查询方法，修妖修改成查询方法-->
            <el-button type="primary" icon="el-icon-search" @click="getCourseList()">查询</el-button>
            <el-button type="default" @click="resetData()">清空</el-button>
        </el-form>
        <!-- 表格 -->
        <el-table :data="courses" border fit highlight-current-row>
            <el-table-column label="序号" width="70" align="center">
                <template slot-scope="scope">
                    {{ (page - 1) * limit + scope.$index + 1 }}
                </template>
            </el-table-column>
            <el-table-column label="课程信息" width="300" align="center">
                <template slot-scope="scope">
                    <div class="info">
                        <div class="pic">
                            <img :src="scope.row.cover" alt="scope.row.title" width="150px">
                        </div>
                        <div class="title">
                            <a href="">{{ scope.row.title }}</a>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="课时" align="center">
                <template slot-scope="scope">
                    {{ scope.row.courseTotalTime }}
                </template>
            </el-table-column>
            <el-table-column label="浏览量" align="center">
                <template slot-scope="scope">
                    {{ scope.row.viewCount }}
                </template>
            </el-table-column>
            <el-table-column label="课程讲师" align="center">
                <template slot-scope="scope">
                    {{ scope.row.teacherId }}
                </template>
            </el-table-column>
            <el-table-column label="发布状态" align="center" >
                <template slot-scope="scope">
                    {{ scope.row.status === 'Normal' ? '已发布' : '未发布' }}
                </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center">
                <template slot-scope="scope">
                    {{ scope.row.gmtCreate.substr(0, 10) }}
                </template>
            </el-table-column>
            <el-table-column label="更新时间" align="center">
                <template slot-scope="scope">
                    {{ scope.row.gmtModified.substr(0, 10) }}
                </template>
            </el-table-column>
            <el-table-column label="课程价格" align="center" >
                <template slot-scope="scope">
                    {{ Number(scope.row.price) === 0 ? '免费' : '¥' + scope.row.price.toFixed(2) }}
                </template>
            </el-table-column>
            <el-table-column prop="buyCount" label="购买数量" align="center">
                <template slot-scope="scope">
                    {{ scope.row.buyCount }}人
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center">
                <template slot-scope="scope">
                    <router-link :to="'/course/info/'+scope.row.id">
                        <el-button type="text" size="mini" icon="el-icon-edit">编辑课程信息</el-button>
                    </router-link>
                    <router-link :to="'/course/chapter/'+scope.row.id">
                        <el-button type="text" size="mini" icon="el-icon-edit">编辑课程大纲</el-button>
                    </router-link>
                    <el-button type="text" size="mini" icon="el-icon-delete" @click="removeCourseByCourseId(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            :current-page="page"
            :page-size="limit"
            :total="total"
            style="padding: 30px 0; text-align: center;"
            layout="total, prev, pager, next, jumper"
            @current-change="getCourseList"
        />
    </div>
</template>
<script>
    import course from '@/api/edu/course'
    import subject from '@/api/edu/subject'
    import teacher from '@/api/edu/teacher'
    export default{
        data(){
            return {
                courses:null,//list接收查询完接口后返回的集合
                total:0,//总记录数，默认为0条记录
                page:1,//page保存当前页信息，默认就是第一页
                limit:10,//limit保存每页记录数，默认每页十条记录
                courseQuery:{},//用来封装查询条件对象
                firstLevelSubjects: [],//课程一级分类
                secondLevelSubjects: [],//课程二级分类
                teacherList: []
            }
        },
        created(){
            this.init()
        },
        methods:{
            //定义请求讲师列表的方法，page =1表示page的默认值是1，当值不为1时不会变化，page该是多少就是多少
            getCourseList(page=1){
                this.page=page
                course.findAllCoursePaging(this.page,this.limit,this.courseQuery)
                .then(response=>{
                    this.courses=response.data.courses
                    this.total=response.data.total
                })
                .catch(error=>{
                    console.log(error)
                })
            },
            resetData(){//清空条件查询框并查询所有一次
                this.courseQuery={}
                this.getCourseList()
            },
            removeDataById(id){//删除需要调用接口，teacher.js准备写方法去执行接口中的方法
                this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })//点击确认会自动调用then中的方法
                .then(() => {
                    teacher.deleteTeacherId(id)
                    .then(response=>{
                        //提示信息
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        //回到列表页面
                        this.getTeacherList()
                    })
                })
            },
            init(){
                // 初始化分类列表
                this.getAllSubjectList()
                // 获取讲师列表
                this.selectedTeacher()
                this.getCourseList()
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
            //根据课程id移除课程，这个是物理删除
            removeCourseByCourseId(courseId){
                this.$confirm('此操作将永久删除该课程, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                .then(() => {
                    course.removeCourseByCourseId(courseId)
                    .then(response=>{
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.getCourseList()
                    })
                })
            }
        }
    }
</script>
<style scoped>
.myClassList .info {
width: 450px;
overflow: hidden;
}
.myClassList .info .pic {
width: 150px;
height: 90px;
overflow: hidden;
float: left;
}
.myClassList .info .pic a {
display: block;
width: 100%;
height: 100%;
margin: 0;
padding: 0;
}
.myClassList .info .pic img {
display: block;
width: 100%;
}
.myClassList td .info .title {
width: 280px;
float: right;
height: 90px;
}
.myClassList td .info .title a {
display: block;
height: 48px;
line-height: 24px;
overflow: hidden;
color: #00baf2;
margin-bottom: 12px;
}
.myClassList td .info .title p {
line-height: 20px;
margin-top: 5px;
color: #818181;
}
</style>