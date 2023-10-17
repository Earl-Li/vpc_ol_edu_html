<template>
    <div class="app-container">
        <h2 style="text-align: center;">发布新课程</h2>
        <el-steps :active="2" process-status="wait" align-center style="margin-bottom:40px;">
            <el-step title="填写课程基本信息"/>
            <el-step title="创建课程大纲"/>
            <el-step title="最终发布"/>
        </el-steps>
        <!-- 添加和修改章节表单 -->
        <el-dialog :visible.sync="dialogChapterFormVisible" :title="dialogChapterInfo">
            <el-form :model="chapter" label-width="120px">
                <el-form-item label="章节标题">
                    <el-input v-model="chapter.title"/>
                </el-form-item>
                <el-form-item label="章节排序">
                    <el-input-number v-model="chapter.sort" :min="0" controlsposition="right"/>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleChapterDialog">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateChapter" :disabled="chapterSaveBtnDisabled">确 定</el-button>
            </div>
        </el-dialog>
        <!-- 添加和修改课时表单 -->
        <el-dialog :visible.sync="dialogVideoFormVisible" :title="dialogVideoInfo">
            <el-form :model="video" label-width="120px">
                <el-form-item label="课时标题">
                    <el-input v-model="video.title"/>
                </el-form-item>
                <el-form-item label="课时排序">
                    <el-input-number v-model="video.sort" :min="0" controlsposition="right"/>
                </el-form-item>
                <el-form-item label="是否免费">
                    <el-radio-group v-model="video.isFree">
                        <el-radio :label="1">免费</el-radio>
                        <el-radio :label="0">默认</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="上传视频">
                    <!--
                        on-success上传成功后调用的方法，
                        fileList在选择文件后会在这个对象中列举上传的文件的列表
                        on-remove点击文件删除的叉号后弹框并点击确定删除会调用对应的方法
                        before-remove是点击删除文件列表文件后面的叉号调用对应的方法
                        action是后端上传接口的请求地址
                        limit是允许上传的文件数量，当前是1
                        upload-demo是准备给组件一个样式
                        on-exceed上传视频多于一个会执行对应的方法
                        这个组件上传文件也是即使上传，点完上传视频就会直接上传，没做验证，没做取消小节添加的取消删除视频工作，需要优化的地方很多
                        说白了就是上传视频文件到阿里云视频点播，返回视频id，赋值给前端的video对象，然后一起存入数据库
                    -->
                    <el-upload
                    :on-success="handleVodUploadSuccess"
                    :on-remove="handleVodRemove"
                    :before-remove="beforeVodRemove"
                    :on-exceed="handleUploadExceed"
                    :file-list="fileList"
                    :action="BASE_API+'/eduvod/filevod/uploadVideo'"
                    :limit="1"
                    class="upload-demo">
                        <el-button size="small" type="primary" >上传视频</el-button>
                        <!--el-tooltip是给用户的一个友好提示信息，会在上传按钮后面跟一个问号，鼠标悬停在问号上会提示对应信息-->
                        <el-tooltip placement="right-end">
                            <div slot="content">
                                最大支持1G, <br>
                                支持3GP、 ASF、 AVI、 DAT、 DV、 FLV、 F4V、 <br>
                                GIF、 M2T、 M4V、 MJ2、 MJPEG、 MKV、 MOV、 MP4、 <br>
                                MPE、 MPG、 MPEG、 MTS、 OGG、 QT、 RM、 RMVB、 <br>
                                SWF、 TS、 VOB、 WMV、 WEBM 等视频格式上传
                            </div>
                        <i class="el-icon-question"/>
                        </el-tooltip>
                    </el-upload>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleVideoDialog">取 消</el-button>
                <el-button :disabled="videoSaveBtnDisabled" type="primary" @click="saveOrUpdateVideo">确 定</el-button>
            </div>
        </el-dialog>
        <el-row>
            <el-button type="primary" @click="openChapterDialogBeforeAddOrEdit">添加新章节</el-button>
        </el-row>
        <!-- 章节 -->
        <ul class="chanpterList">
            <li
                v-for="chapter in chapters"
                :key="chapter.id"
            >
                <p>{{ chapter.title }}
                    <span class="acts">
                        <el-button type="primary" plain @click="openVideoDialog(chapter.id)">添加课时</el-button>
                        <el-button @click="editChapter(chapter.id)" type="success" plain>编辑</el-button>
                        <el-button type="danger" plain @click="deleteChapter(chapter.id)">删除</el-button>
                    </span>
                </p>
                
                <!-- 视频 -->
                <ul class="chanpterList videoList">
                    <li
                    v-for="video in chapter.children"
                    :key="video.id">
                        <p>{{ video.title }}
                            <span class="acts">
                                <el-button @click="editVideo(video.id)" type="success" size="small">编辑</el-button>
                                <el-button type="danger" @click="deleteVideo(video.id)" size="small">删除</el-button>
                            </span>
                        </p>
                    </li>
                </ul>
            </li>
        </ul>
        <el-form label-width="120px">
            <el-form-item>
                <el-button @click="previous">上一步</el-button>
                <el-button :disabled="saveBtnDisabled" type="primary" @click="next">下一步</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    import chapter from '@/api/edu/chapter'
    import video from '@/api/edu/video'
    import vod from '@/api/edu/vod'
    const defaultVideoForm={
        id: '',
        courseId:'',
        title: '',
        sort: 0,
        chapterId: '',
        isFree: 0,
        videoSourceId: '',
        videoOriginalName: ''
    }
    export default {
        data() {
            return {
                saveBtnDisabled: false ,// 下一步按钮是否禁用
                courseId: '',
                chapters: [],
                chapter: {
                    courseId: '',
                    title: '',
                    sort: 0
                },
                dialogChapterFormVisible: false,
                dialogVideoFormVisible: false,
                chapterSaveBtnDisabled: false,
                videoSaveBtnDisabled: false,
                dialogChapterInfo: '添加章节',
                dialogVideoInfo: '添加课时',
                video: {...defaultVideoForm},
                fileList: [],//上传文件列表
                BASE_API: process.env.BASE_API, // 接口API地址
            }
        },
        created() {
            this.init()
        },
        methods: {
            //================================课时操作相关方法==================================
            //成功回调，目前只用到response，主要为了获取videoId，并存入数据库
            handleVodUploadSuccess(response, file, fileList) {
                //阿里云视频点播上返回的videoId赋值
                this.video.videoSourceId = response.data.videoId
                //视频文件原始名称获取和入库
                this.video.videoOriginalName = file.name
                //单独执行一次小节数据更新，确保视频数据存入数据库,不行，会报响应异常错误，后面的方法不会执行，很难办
                //video.updateVideo(this.video).then(response=>{})
            },
            //视图上传多于一个视频
            handleUploadExceed(files, fileList) {
                this.$message.warning('想要重新上传视频，请先删除已上传的视频')
            },
            //执行完beforeVodRemove方法点击确认会直接执行这个方法，调用接口删除阿里云视频点播中的视频文件
            handleVodRemove(file,fileList){
                this.videoSaveBtnDisabled=true
                vod.removeAliYunVideo(this.video.videoSourceId)
                .then(response=>{
                    this.$message({
                        type:'success',
                        message:response.message
                    })
                })
                this.video.videoSourceId = ''
                this.video.videoOriginalName = ''
                //this.fileList=[]
                this.videoSaveBtnDisabled=false
            },
            //点击视频后面的叉号，会直接执行这个方法
            beforeVodRemove(file,fileList){
                //点击叉号fileList还没删除确认直接清空了，这里补上
                //this.fileList=[{name:this.video.videoOriginalName}]
                return this.$confirm(`是否永久删除视频【${file.name}】`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
            },
            openVideoDialog(chapterId){ 
                this.video={...defaultVideoForm}
                this.dialogVideoInfo='添加课时'
                this.fileList=[]
                this.video.courseId=this.courseId  
                this.video.chapterId=chapterId
                this.initVideoDialogBeforeAddOrEdit()
            },
            initVideoDialogBeforeAddOrEdit(){
                this.dialogVideoFormVisible = true
                this.videoSaveBtnDisabled=false
            },
            saveOrUpdateVideo(){
                this.videoSaveBtnDisabled=true
                if(!this.video.id){
                    this.saveVideo()
                }else{
                    this.updateVideo()
                }
            },
            saveVideo(){
                video.addVideo(this.video)
                .then(response => {
                    this.$message({
                        type: 'success',
                        message: '课时添加成功!'
                    })
                    this.handleVideoDialog()
                })
                .catch((response) => {
                    this.$message({
                        type: 'error',
                        message: response.message
                    })
                })
            },
            handleVideoDialog(){
                this.dialogVideoFormVisible=false
                this.getChaptersByCourseId()
                //重置video
                this.video={...defaultVideoForm}
                this.dialogVideoInfo='添加课时'
            },
            editVideo(videoId){
                this.dialogVideoInfo="修改课时"
                this.initVideoDialogBeforeAddOrEdit()
                video.queryVideo(videoId)
                .then(response=>{
                    this.video=response.data.video
                    this.fileList=this.video.videoOriginalName===''?[]:[{name:this.video.videoOriginalName}]
                    console.log(this.video)
                })
            },
            updateVideo(){
                video.updateVideo(this.video)
                .then(response=>{
                    this.$message({
                        type: 'success',
                        message: '课时更新成功!'
                    })
                    this.handleVideoDialog()
                })
            },
            deleteVideo(videoId){
                this.$confirm('此操作将永久删除该课时, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    video.deleteVideo(videoId).then(response=>{
                        this.$message({
                        type: 'success',
                        message: '删除成功!'
                        })
                        this.getChaptersByCourseId()
                    })
                })
            },
            //================================章节操作相关方法==================================
            init(){
                if(this.$route.params && this.$route.params.id){
                    this.courseId=this.$route.params.id
                    this.chapter.courseId=this.$route.params.id
                    this.getChaptersByCourseId()
                }
            },
            getChaptersByCourseId(){
                chapter.queryChaptersByCourseId(this.courseId)
                .then(response=>{
                    this.chapters=response.data.chapters
                })
            },
            previous() {
                this.$router.push({ path: '/course/info/'+this.courseId })
            },
            next() {
                this.$router.push({ path: '/course/publish/'+this.courseId })
            },
            saveOrUpdateChapter(){
                this.chapterSaveBtnDisabled=true
                if(!this.chapter.id){
                    this.saveChapter()
                }else{
                    this.updateChapter()
                }
            },
            //在点击添加新章节时再进行一次章节初始化是为了避免对话框点击叉号没有事件可以触发清空对象信息，添加课时同理
            openChapterDialogBeforeAddOrEdit(){
                this.initChapterDialogBeforeAddOrEdit()
                //重置章节标题
                this.chapter.title=''
                //充值章节排序
                this.chapter.sort=0
                this.dialogChapterInfo='添加章节'
            },
            //添加新章节或者修改新章节弹出对话框并将确认按钮重置，在处理完就重置会出现连点两次导致章节信息清空的bug
            initChapterDialogBeforeAddOrEdit(){
                this.dialogChapterFormVisible = true
                this.chapterSaveBtnDisabled=false
            },
            saveChapter(){
                chapter.addChapter(this.chapter)
                .then(response => {
                    this.$message({
                        type: 'success',
                        message: '章节添加成功!'
                    })
                    this.handleChapterDialog()
                })
                .catch((response) => {
                    this.$message({
                        type: 'error',
                        message: response.message
                    })
                })
            },
            handleChapterDialog(){
                this.dialogChapterFormVisible=false
                this.getChaptersByCourseId()
                //重置章节标题
                this.chapter.title=''
                //充值章节排序
                this.chapter.sort=0
                this.dialogChapterInfo='添加章节'
            },
            //定义编辑章节信息的方法，主要是数据的回显，由于原先的所有章节列表是单独封装成二级联动效果的对象，排序属性并没有涉及，这里直接查表，不使用之前的数据
            editChapter(chapterId){
                this.dialogChapterInfo="修改章节"
                this.initChapterDialogBeforeAddOrEdit()
                chapter.queryChapter(chapterId)
                .then(response=>{
                    this.chapter=response.data.chapter
                })
            },
            //定义更新章节信息的方法
            updateChapter(){
                chapter.updateChapter(this.chapter)
                .then(response=>{
                    this.$message({
                        type: 'success',
                        message: '章节更新成功!'
                    })
                    this.handleChapterDialog()
                })
            },
            deleteChapter(chapterId){
                this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    chapter.deleteChapter(chapterId).then(response=>{
                        this.$message({
                        type: 'success',
                        message: '删除成功!'
                        })
                        this.getChaptersByCourseId()
                    })
                })
            }
        }
    }
</script>
<style scoped>
    .chanpterList{
        position: relative;
        list-style: none;
        margin: 0 ;
        padding: 0;
    }
    .chanpterList li{
        position: relative;
    }
    .chanpterList p{
        float: left;/**这个属性会导致内部的标签被p标签覆盖，导致其中的按钮不能被点击，解决办法是设置position属性为relative，并提升内部标签span的优先级z-index=1，将sapn标签置于顶层，这样按钮就可以点击了 */
        font-size: 20px;/**设置p标签的字体大小 */
        font-family:"lucida grande", "lucida sans unicode", lucida, helvetica, "Hiragino Sans GB", "Microsoft YaHei", "WenQuanYi Micro Hei", sans-serif;
        margin: 10px 0;
        padding: 10px;/*设置文字到边框的距离*/
        height: 60px;
        line-height: 40px;/*设置文字行高*/
        width: 100%;/*设置p标签的宽度*/
        border: 1px solid lightskyblue;
        border-radius: 8px;/*设置边框弧度*/
        position: relative;
    }
    .chanpterList .acts {
        float: right;
        font-size: 14px;
        position: relative;
        z-index: 1;
    }
    .videoList{
        padding-left: 50px;
    }
    .videoList p{
        float: left;/**这个属性会导致内部的标签被p标签覆盖，导致其中的按钮不能被点击，解决办法是设置position属性为relative，并提升内部标签span的优先级z-index=1，将sapn标签置于顶层，这样按钮就可以点击了 */
        font-size: 16px;/**设置p标签的字体大小 */
        font-family: "lucida grande", "lucida sans unicode", lucida, helvetica, "Hiragino Sans GB", "Microsoft YaHei", "WenQuanYi Micro Hei", sans-serif;
        margin: 10px 0;
        padding: 3px 27px 3px 10px;/*设置文字到边框的距离*/
        height: 40px;
        line-height: 27px;/*设置文字行高*/
        width: 100%;/*设置p标签的宽度*/
        border: 1px solid lightskyblue;
        border-radius: 5px;/*设置边框弧度*/
        position: relative;
    }
    .videoList .acts {
        float: right;
        font-size: 14px;
        position: relative;
        z-index: 1;
    }
</style>