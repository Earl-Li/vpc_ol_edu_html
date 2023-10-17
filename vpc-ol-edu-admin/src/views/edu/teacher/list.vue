<template>
    <div class="app-container">
        <h2>讲师列表</h2>
        <!--:inline表示所有的内容在一行内展示-->
        <el-form :inline="true" class="demo-form-inline">
            <el-form-item>
                <!--input输入框,v-model需要绑定teacherQuery的值-->
                <el-input v-model="teacherQuery.name" placeholder="讲师名"/>
            </el-form-item>
            <el-form-item>
                <!--select-option下拉列表-->
                <el-select v-model="teacherQuery.level" clearable placeholder="讲师头衔">
                    <el-option :value="1" label="高级讲师"/>
                    <el-option :value="2" label="首席讲师"/>
                </el-select>
            </el-form-item>
            <el-form-item label="添加时间">
                <el-date-picker
                    v-model="teacherQuery.beginTime"
                    type="datetime"
                    placeholder="选择开始时间"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    default-time="00:00:00"
                />
            </el-form-item>
            <el-form-item>
                <!--这个是时间选择框-->
                <el-date-picker
                    v-model="teacherQuery.endTime"
                    type="datetime"
                    placeholder="选择截止时间"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    default-time="00:00:00"
                />
            </el-form-item>
            <!--button按钮，@click="fetchData()"是点击执行查询方法，修妖修改成查询方法-->
            <el-button type="primary" icon="el-icon-search" @click="getTeacherList()">查询</el-button>
            <!--注意此时这个清空按钮的resetData方法是没有的，点了也没有用，需要自己编写resetData方法实现
                查询条件的清空和查询一次所有，需要在methods中编写
            -->
            <el-button type="default" @click="resetData()">清空</el-button>
        </el-form>

        <!-- 表格 -->
        <!--:data得到对应变量名的数据，
            v-loading="listLoading"和element-loading-text="数据加载中"在数据加载时会显示加载中信息
            border、fit、highlight-current-row都是样式
        -->
        <!--查询表单-->
        <el-table :data="list" border fit highlight-current-row>
        
        <el-table-column
            label="序号"
            width="70"
            align="center">
            <template slot-scope="scope">
                {{ (page - 1) * limit + scope.$index + 1 }}
            </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="80" />
        <el-table-column label="头衔" width="80">
            <!--scope表示整个表格，scope.row表示表格的某一行，level为1则表示是高级讲师，否则为首席讲师；===表示不仅要值等，数据类型也要相等-->
            <template slot-scope="scope">
                {{ scope.row.level===1?'高级讲师':'首席讲师' }}
            </template>
        </el-table-column>
        <el-table-column prop="intro" label="资历" />
        <el-table-column prop="gmtCreate" label="添加时间" width="160"/>
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column label="操作" width="200" align="center">
            <template slot-scope="scope">
                <router-link :to="'/teacher/edit/'+scope.row.id">
                    <el-button type="primary" size="mini" icon="el-icon-edit">修改</el-button>
                </router-link>
                <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeDataById(scope.row.id)">删除</el-button>
            </template>
        </el-table-column>
        </el-table>
        
        <!-- 分页 ,这些冒号都是v-bind,style是分页条的样式、layout是显示数据的布局，这里面封装了上一页下一页的判断，还有值的传递，
            不需要再自己写逻辑进行判断了
            这里的@表示v-on的简写，current-change这个事件绑定的是分页的切换，对应的方法是自己通过接口查询数据的方法即getTeacherList，
            调用的时候会自动传参当前页，已经由element-ui封装好了，但是并不会修改data中page的数据，需要手动进行更改
        -->
        <el-pagination
            :current-page="page"
            :page-size="limit"
            :total="total"
            style="padding: 30px 0; text-align: center;"
            layout="total, prev, pager, next, jumper"
            @current-change="getTeacherList"
        />
    </div>
</template>
<script>
    import teacher from '@/api/edu/teacher.js'
    export default{
        //这里面写vue的核心代码
        //data有两种写法
        //data的第一种写法
        /*data:{

        },*/
        //data的第二种写法
        data(){//data中定义变量和初始值，方法会使用到的数据
            return {
                //listLoading:true,//是否显示loading信息
                list:null,//list接收查询完接口后返回的集合
                total:0,//总记录数，默认为0条记录
                page:1,//page保存当前页信息，默认就是第一页
                limit:10,//limit保存每页记录数，默认每页十条记录
                teacherQuery:{}//用来封装查询条件对象
            }
        },
        created(){//created方法在页面渲染前执行，一般用methods定义的方法
            this.getTeacherList()//对methods中的方法进行调用，注意这里面无法直接调用teacher.findAllTeacherPaging方法
        },
        methods:{//methods中创建定义具体的方法，在这里面会调用teacher.js中定义的方法
            //定义请求讲师列表的方法，page =1表示page的默认值是1，当值不为1时不会变化，page该是多少就是多少
            getTeacherList(page=1){
                this.page=page
                console.log(this.page)
                //按照axios的要求是axios.post("").then().catch()//由于request已经将这个过程进行了封装，
                //teacher.js已经进行了处理，这里只需要调用teacher.js中的对应方法即可,注意request方法仅相当于axios.post(""),
                //后面的.then().catch()还是需要自己在这个方法中进行处理
                teacher.findAllTeacherPaging(this.page,this.limit,this.teacherQuery)
                    .then(response=>{
                        //response是接口返回的数据
                        //console.log(response)
                        this.list=response.data.rows
                        this.total=response.data.total
                        //console.log(this.list)
                        //console.log(this.total)
                    })//请求成功处理方法
                    .catch(error=>{
                        console.log(error)
                    })//请求失败处理方法
            },
            resetData(){//清空条件查询框并查询所有一次
                this.teacherQuery={}
                this.getTeacherList()
            },
            removeDataById(id){//删除需要调用接口，teacher.js准备写方法去执行接口中的方法
                //alert(id)
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
                })//catch表示确认删除弹框点击取消后执行的方法，此处不需要显示任何信息，可以不写.catch方法
                /*.catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });          
                });*/
            }
        }
    }
</script>
