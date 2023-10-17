<template>
    <div id="aCoursesList" class="bg-fa of">
        <!-- 讲师列表 开始 -->
        <section class="container">
            <header class="comm-title all-teacher-title">
            <h2 class="fl tac">
                <span class="c-333">全部讲师</span>
            </h2>
            <section class="c-tab-title">
                <a id="subjectAll" title="全部" href="#">全部</a>
                <!-- <c:forEach var="subject" items="${subjectList }">
                    <a id="${subject.subjectId}" title="${subject.subjectName }" href="javascript:void(0)" onclick="submitForm(${subject.subjectId})">${subject.subjectName }</a>
                </c:forEach>-->
            </section>
            </header>
            <section class="c-sort-box unBr">
                <div>
                    <!-- /无数据提示 开始,使用v-if判断teacherData中没有数据就显示找不到相关数据-->
                    <section class="no-data-wrap" v-if="teacherData.total==0">
                        <em class="icon30 no-data-ico">&nbsp;</em>
                        <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
                    </section>
                    <!-- /无数据提示 结束-->

                    <!--有数据记录就显示下列组件-->
                    <article class="i-teacher-list" v-if="teacherData.total>0">
                        <ul class="of">
                            <li v-for="teacher in teacherData.curPageTeachers" :key="teacher.id">
                                <section class="i-teach-wrap">
                                    <div class="i-teach-pic">
                                        <a href="/teacher/1" :title="teacher.name" target="_blank">
                                            <img :src="teacher.avatar" alt>
                                        </a>
                                    </div>
                                    <div class="mt10 hLh30 txtOf tac">
                                        <a :href="'/teacher/'+teacher.id" :title="teacher.name" target="_blank" class="fsize18 c-666">{{teacher.name}}</a>
                                    </div>
                                    <div class="hLh30 txtOf tac">
                                        <span class="fsize14 c-999">{{teacher.intro}}</span>
                                    </div>
                                    <div class="mt15 i-q-txt">
                                        <p class="c-999 f-fA">{{teacher.career}}</p>
                                    </div>
                                </section>
                            </li>
                        </ul>
                    <div class="clear"></div>
                    </article>
                </div>

                <!-- 公共分页 开始 -->
                <div>
                    <div class="paging">
                        <!-- undisable这个class是否存在，取决于数据属性hasPrevious -->
                        <!--@click.prevent阻止超链接的跳转行为转而执行gotoPage方法-->
                        <!--:class="{undisable: !data.hasPrevious}"是让图标被选中时的样式发生变化，没有上一页就不能点击，用css样式控制能否点击-->
                        <a :class="{undisable: !teacherData.hasPrevious}" href="#" title="首页" @click.prevent="gotoPage(1)">首</a>
                        <!--点击前一页跳转到‘当前页-1’，前一页没有时无法点击-->
                        <a :class="{undisable: !teacherData.hasPrevious}" href="#" title="前一页" @click.prevent="gotoPage(teacherData.current-1)">&lt;</a>
                        <!--循环页码组件，把总页数取到，点击当前页无法点击-->
                        <a v-for="page in teacherData.pages" :key="page" :class="{current: teacherData.current == page, undisable: teacherData.current ==page}" :title="'第'+page+'页'" href="#" @click.prevent="gotoPage(page)">{{ page }}</a>
                        <!--后一页是’当前页+1‘，没有下一页不能点击-->
                        <a :class="{undisable: !teacherData.hasNext}" href="#" title="后一页" @click.prevent="gotoPage(teacherData.current+1)">&gt;</a>
                        <a :class="{undisable: !teacherData.hasNext}" href="#" title="末页" @click.prevent="gotoPage(teacherData.pages)">末</a>
                        <div class="clear"/>
                    </div>
                </div>
                <!-- 公共分页 结束 -->
            </section>
        </section>
        <!-- /讲师列表 结束 -->
    </div>
</template>
<script>
    import teacher from '@/api/teacher'
    export default {
        //异步调用
        //方法名固定，两个参数params：相当于params=this.$route.params、error:得到当前调用中后端返回的错误信息
        //return也是固定的，
        asyncData({ params, error }) {
            //这个return后面不能直接加回车，加了回车会出问题
            return teacher.pageTeacherList(1, 8).then(response => {
                //这个也可以用之前的方法获取值，即赋值给data中的变量，也可以直接写成下面这种形式
                //this.data=response.data.data
                //第一个data不需要在data(){}中定义，会自动帮忙定义data，还会自动把值赋值到data上，和上面的写法效果是一样的
                return { teacherData: response.data.data }
            });
        },
        methods:{
            gotoPage(page){
                if(page<=this.teacherData.pages){
                    teacher.pageTeacherList(page, 8).then(response => {
                        this.teacherData=response.data.data
                    });
                }
            }
        }
    };
</script>