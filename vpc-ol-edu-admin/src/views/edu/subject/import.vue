<template>
    <div class="app-container">
        <el-form label-width="120px">
            <el-form-item label="信息描述">
                <el-tag type="info">excel模版说明</el-tag>
                <el-tag>
                    <i class="el-icon-download"/>
                    <a :href="OSS_PATH+'2023/09/22/91d1253d50fa459da2b9b08fee016deesubject.xlsx'">点击下载模版</a>
                </el-tag>
            </el-form-item>
            <el-form-item label="选择Excel">
                <!--ref="upload"是组件的唯一标识，实际上这个就是把课程分类写在excel表格中点击上传解析excel表格把课程信息存入数据库
                    auto-upload="false"表示是否自动上传，自动上传是选择完文件能够自动上传，手动上传是选择完文件后点击上传再上传，false表示禁用自动上传
                    on-success="fileUploadSuccess"表示上传成功调用fileUploadSuccess方法
                    on-error="fileUploadError"表示上传失败调用fileUploadError方法
                    disabled="importBtnDisabled"表示点完按钮以后按钮是否能被点第二次
                    limit="1"表示限制每次只能传一个文件
                    action="BASE_API+'/eduservice/edu-subject/addSubject'"表示上传接口地址
                    name="file"后端的MultipartFile file即变量名必须和这个相同
                    accept="application/vnd.ms-excel"表示只能上传excel文件，传其他格式的文件不支持
                -->
                <el-upload
                    ref="upload"
                    :auto-upload="false"
                    :on-success="fileUploadSuccess"
                    :on-error="fileUploadError"
                    :disabled="importBtnDisabled"
                    :limit="1"
                    :action="BASE_API+'/eduservice/subject/addSubject'"
                    name="file"
                    accept="application/.xlsx">
                    <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
                    <el-button
                        :loading="loading"
                        style="margin-left: 10px;"
                        size="small"
                        type="success"
                    @click="submitUpload">{{ fileUploadBtnText }}</el-button>
                </el-upload>
            </el-form-item>
        </el-form>
    </div>
</template>
<script>
    export default {
        data() {
            return {
                BASE_API: process.env.BASE_API, // 接口API地址
                OSS_PATH: process.env.OSS_PATH, // 阿里云OSS地址
                fileUploadBtnText: '上传到服务器', // 按钮文字
                importBtnDisabled: false, // 按钮是否禁用,
                loading: false
            }
        },
        create(){

        },
        methods:{
            //点击按钮上传文件到接口中
            submitUpload(){
                this.importBtnDisabled=true//上传文件按钮禁用
                this.loading=true
                //js:document.getElementById("upload").submit(),原生JS的写法，下面是框架的写法，upload是上传组件的身份，整个表示提交文件的方法标识
                this.$refs.upload.submit()
            },
            //上传成功
            fileUploadSuccess(response){//response可以获取后端接口的返回数据
                //提示上传成功并返回课程列表页面
                this.loading=false
                this.$message({
                    type: 'success',
                    message: '成功添加课程'
                })
                //路由跳转课程分类列表
                this.$router.push({path:'/subject/list'})
            },
            //上传失败
            fileUploadError(){
                this.loading=false
                this.$message({
                    type: 'error',
                    message: '导入课程失败'
                })
            }
        }
    }
</script>