<template>
    <div class="app-container">
        <h2>添加讲师</h2>
        <el-form label-width="120px">
            <el-form-item label="讲师名称">
                <el-input v-model="teacher.name"/>
            </el-form-item>
            <el-form-item label="讲师排序">
                <el-input-number v-model="teacher.sort" controls-position="right" min="0"/>
            </el-form-item>
            <el-form-item label="讲师头衔">
                <el-select v-model="teacher.level" clearable placeholder="请选择">
                <!--
                数据类型一定要和取出的json中的一致，否则没法回填
                因此，这里value使用动态绑定的值，保证其数据类型是number
                -->
                    <el-option :value="1" label="高级讲师"/>
                    <el-option :value="2" label="首席讲师"/>
                </el-select>
            </el-form-item>
            <el-form-item label="讲师资历">
                <el-input v-model="teacher.career"/>
            </el-form-item>
            <el-form-item label="讲师简介">
                <el-input v-model="teacher.intro" :rows="10" type="textarea"/>
            </el-form-item>
            <!-- 讲师头像： TODO -->
            <!--讲师头像-->
            <!-- 讲师头像 -->
            <el-form-item label="讲师头像">
                <!-- 头衔缩略图 -->
                <pan-thumb :image="teacher.avatar"/>
                <!-- 文件上传按钮 -->
                <el-button type="primary" icon="el-icon-upload"
                    @click="imagecropperShow=true">更换头像
                </el-button>
                <!--
                v-show：是否显示上传组件
                :key：类似于id，如果一个页面多个图片上传控件，可以做区分
                :url：后台上传的url地址
                @close：关闭上传组件
                @crop-upload-success：上传成功后的回调 -->
                <image-cropper
                    v-show="imagecropperShow"
                    :width="300"
                    :height="300"
                    :key="imagecropperKey"
                    :url="BASE_API+'/eduoss/fileoss'"
                    field="file"
                    @close="close"
                    @crop-upload-success="cropSuccess"/>
            </el-form-item>
            <el-form-item>
                <el-button :disabled="saveBtnDisabled" type="primary" @click="saveOrUpdate">保存</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    import teacher from '@/api/edu/teacher'
    import ImageCropper from '@/components/ImageCropper'
    import PanThumb from '@/components/PanThumb'
    export default {
        components: {ImageCropper,PanThumb},
        data() {
            return {
                teacher: {//这个的属性要和后端entity实体对应才能实现自动封装
                    name: '',
                    sort: 0,
                    level: 1,
                    career: '',
                    intro: '',
                    avatar: ''
                },
                saveBtnDisabled: false, // 保存按钮是否禁用,
                imagecropperShow: false, //上传弹框组件是否显示，点击保存头像,这个属性为false就表示上传弹框关闭了
                imagecropperKey:0, //上传组件的key值
                BASE_API: process.env.BASE_API 
            }
        },
        //页面渲染前判断路由中是否有参数id,有就通过getInfoById方法查询讲师信息并封装到讲师对象中用于数据渲染
        created(){
            this.init()
        },
        watch: {
            $route(to,from){//vue监听路由变化方式，路由发生变化，其中的方法就会执行
                this.init()
            }
        },
        methods: {
            close(){//关闭头像上传弹框的方法,点击上传头像的叉号就会调用close方法,即关闭头像上传弹框
                this.imagecropperShow=false
                this.imagecropperKey=this.imagecropperKey+1
            },
            cropSuccess(data){//头像上传成功的方法，头像点击保存成功就会调用cropSuccess方法，注意在点击上传时就已经调用了上传接口方法，返回的结果会自动封装到这个方法的参数中
                this.close()
                this.teacher.avatar=data.url
            },
            init(){
                if(this.$route.params && this.$route.params.id){
                    const id=this.$route.params.id
                    this.getInfoById(id)
                }else{
                    this.teacher={}
                }
            },
            saveOrUpdate() {
                this.saveBtnDisabled = true// 一次保存后保存按钮禁用避免多次返回提交,这里应该还要设置提交超时后的处理，否则失败则会一直卡在这个页面
                console.log(this.teacher.avatar)
                if(!this.teacher.avatar){
                    console.log(this.teacher.avatar)
                    this.teacher.avatar='https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/03/b91ef0c2e9704873b7605f4af287d4d3default.jpg'
                }
                if(!this.teacher.id){
                    this.saveTeacher()
                }else{
                    //添加讲师此前的讲师数据清空
                    this.updateTeacher()
                }
            },
            // 保存
            saveTeacher() {
                teacher.addTeacher(this.teacher)
                .then(response=>{
                    //提示添加成功信息
                    this.$message({
                        type: 'success',
                        message: '添加成功!'
                    });
                    //回到列表页面:该方法中不能直接调用另一个页面定义的getTeacherList方法,使用路由跳转的方法实现
                    this.$router.push({path:'/teacher/list'})
                })
            },
            //根据讲师id调用讲师id查询接口查询讲师信息
            getInfoById(id){
                teacher.getTeacherInfo(id)
                    .then(response=>{
                        this.teacher=response.data.items
                        console.log(this.teacher)
                    })
            },
            //定义修改讲师的方法，修改完和保存讲师方法一样需要提示信息和路由跳转讲师列表页面
            updateTeacher(){
                teacher.updateTeacherInfo(this.teacher)
                    .then(response=>{
                        //提示添加成功信息
                        this.$message({
                            type: 'success',
                            message: '修改成功!'
                    });
                        //回到列表页面:该方法中不能直接调用另一个页面定义的getTeacherList方法,使用路由跳转的方法实现
                        this.$router.push({path:'/teacher/list'})
                    })
            }
        }
    }
</script>