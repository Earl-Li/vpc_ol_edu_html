<template>
    <div id="aCoursesList" class="bg-fa of">
    <!-- /课程列表 开始 -->
    <section class="container">
        <header class="comm-title">
            <h2 class="fl tac">
                <span class="c-333">全部课程</span>
            </h2>
        </header>
        <section class="c-sort-box">
            <section class="c-s-dl">
                <dl>
                    <dt>
                        <span class="c-999 fsize14">课程类别</span>
                    </dt>
                    <dd class="c-s-dl-li">
                        <ul class="clearfix">
                            <li>
                                <!--不传参？搜索全部？-->
                                <a title="全部" href="javascript:void(0);" @click="searchOne(null)">全部</a>
                            </li>
                            <li v-for="(subject,index) in firstLevelSubjectList" v-bind:key="index" :class="{active:firstLevelIndex==index}">
                                <a :title="subject.title" href="javascript:void(0);" @click="searchOne(subject.id, index)">{{subject.title}}</a>
                            </li>
                        </ul>
                    </dd>
                </dl>
                <dl>
                    <dt>
                        <span class="c-999 fsize14"/>
                    </dt>
                    <dd class="c-s-dl-li">
                        <ul class="clearfix">
                            <li v-for="(subject,index) in secondLevelSubjectList" v-bind:key="index" :class="{hit:secondLevelIndex==index}">
                                <a :title="subject.title" href="javascript:void(0);" @click="searchTwo(subject.id, index)">{{subject.title}}</a>
                            </li>
                        </ul>
                    </dd>
                </dl>
                <div class="clear"/>
            </section>
            <div class="js-wrap">
                <section class="fr">
                    <span class="c-ccc">
                        <i class="c-master f-fM">1</i>/
                        <i class="c-666 f-fM">1</i>
                    </span>
                </section>
                <section class="fl">
                    <ol class="js-tap clearfix">
                        <li :class="{'current bg-orange':buyCountSort!=''}">
                            <!--点击按销量排序执行searchBuyCount方法-->
                            <a title="销量" href="javascript:void(0);" @click="searchBuyCount()">销量
                                <!--向下箭头表示降序排列-->
                                <span :class="{hide:buyCountSort==''}">↓</span>
                            </a>
                        </li>
                        <li :class="{'current bg-orange':gmtCreateSort!=''}">
                            <a title="最新" href="javascript:void(0);" @click="searchGmtCreate()">最新
                                <span :class="{hide:gmtCreateSort==''}">↓</span>
                            </a>
                        </li>
                        <li :class="{'current bg-orange':priceSort!=''}">
                            <a title="价格" href="javascript:void(0);" @click="searchPrice()">价格&nbsp;
                                <!--display: none;是隐藏样式，priceSort==''满足该条件会应用hide样式，对该图层进行隐藏-->
                                <span :class="{hide:priceSort==''}">↓</span>
                            </a>
                        </li>
                    </ol>
                </section>
            </div>
            <div class="mt40">
                <!-- /无数据提示 开始-->
                <section class="no-data-wrap" v-if="Number(data.total)===0">
                    <em class="icon30 no-data-ico">&nbsp;</em>
                    <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
                </section>
                <!-- /无数据提示 结束-->
                <article class="comm-course-list" v-if="data.total>0">
                    <ul class="of" id="bna">
                        <li v-for="course in data.courses" :key="course.id" >
                            <div class="cc-l-wrap">
                                <section class="course-img">
                                    <img :src="course.cover" class="img-responsive" :alt="course.title">
                                    <div class="cc-mask">
                                        <a :href="'/course/'+course.id" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                                    </div>
                                </section>
                                <h3 class="hLh30 txtOf mt10">
                                    <a href="'/course/'+course.id" :title="course.title" class="course-title fsize18 c-333">{{course.title}}</a>
                                </h3>
                                <section class="mt10 hLh20 of">
                                    <span class="fr jgTag bg-green">
                                        <i class="c-fff fsize12 f-fA">{{Number(course.price)===0?免费:'¥ '+course.price}}</i>
                                    </span>
                                    <span class="fl jgAttr c-ccc f-fA">
                                        <i class="c-999 f-fA">{{course.viewCount}}人学习</i>
                                        |
                                        <i class="c-999 f-fA">{{course.buyCount}}评论</i>
                                    </span>
                                </section>
                            </div>
                        </li>
                    </ul>
                    <div class="clear"></div>
                </article>
            </div>
            <!-- 公共分页 开始 -->
            <div>
                <div class="paging">
                    <!-- undisable这个class是否存在，取决于数据属性hasPrevious -->
                    <a
                    :class="{undisable: !data.hasPrevious}"
                    href="#"
                    title="首页"
                    @click.prevent="gotoPage(1)">首</a>
                    
                    <a
                    :class="{undisable: !data.hasPrevious}"
                    href="#"
                    title="前一页"
                    @click.prevent="gotoPage(data.curPage-1)">&lt;</a>
                    
                    <a
                    v-for="page in data.pages"
                    :key="page"
                    :class="{curPage: data.curPage == page, undisable: data.curPage == page}"
                    :title="'第'+page+'页'"
                    href="#"
                    @click.prevent="gotoPage(page)">{{ page }}</a>
                    
                    <a
                    :class="{undisable: !data.hasNext}"
                    href="#"
                    title="后一页"
                    @click.prevent="gotoPage(data.curPage+1)">&gt;</a>
                    
                    <a
                    :class="{undisable: !data.hasNext}"
                    href="#"
                    title="末页"
                    @click.prevent="gotoPage(data.pages)">末</a>
                    <div class="clear"/>
                </div>
            </div>
            <!-- 公共分页 结束 -->
        </section>
    </section>
    <!-- /课程列表 结束 -->
    </div>
</template>
<script>
    import course from '@/api/course'
    export default {
        data () {
            return {
                page:1,//表示当前页
                data:{},//课程列表
                firstLevelSubjectList: [], // 一级分类列表
                secondLevelSubjectList: [], // 二级分类列表
                courseQueryFactor: {}, // 查询表单条件封装对象
                firstLevelIndex:-1,//一级课程分类索引
                secondLevelIndex:-1,//二级课程分类索引
                buyCountSort:"",//按购买量排序
                gmtCreateSort:"",//按课程创建时间排序
                priceSort:""//按照价格进行排序
            }
        },
        //加载完渲染时
        created () {
            //获取课程列表
            this.initCourse()
            //获取分类
            this.initSubject()
        },
        methods: {
            //查询课程列表，不带条件查询全部课程
            initCourse(){
                course.pageFactorCourse(1, 8,this.courseQueryFactor).then(response => {
                    this.data = response.data.data
                })
            },
            //查询所有一级课程分类，其中数据中包含了一级分类下的二级分类
            initSubject(){
                course.findAllSubject().then(response => {
                    this.firstLevelSubjectList = response.data.data.subjects
                })
            },
            //点击一级分类，显示对应的二级分类，查询数据
            searchOne(subjectParentId, index) {
                this.firstLevelIndex = index//这个是为了让课程分类样式生效，active属性
                //初始化各种信息，防止出现bug
                this.secondLevelIndex = -1
                this.courseQueryFactor.subjectId = "";
                this.secondLevelSubjectList = [];
                this.courseQueryFactor.subjectParentId = subjectParentId;
                this.gotoPage(this.page)//按照当前一级分类查询课程信息
                //对一级点击的分类做一次匹配，显示对应的二级分类列表，让一级课程分类的id和对应课程的一级分类id对比，相同把对应的子目录给二级分类列表
                for (let i = 0; i < this.firstLevelSubjectList.length; i++) {
                    if (this.firstLevelSubjectList[i].id === subjectParentId) {
                        this.secondLevelSubjectList = this.firstLevelSubjectList[i].children
                    }
                }
            },
            //点击二级分类，直接将二级分类的id赋值给查询条件封装对象然后查询
            searchTwo(subjectId, index) {
                this.secondLevelIndex = index
                this.courseQueryFactor.subjectId = subjectId;
                this.gotoPage(this.page)
            },
            //按销量进行查询，三种排序均只实现了一种排序，还可以设置值正序或者倒序排序
            searchBuyCount() {
                //将销量参数赋值1，供后端判断按照销量进行排序，其他查询条件置为空
                this.buyCountSort = "1";
                this.gmtCreateSort = "";
                this.priceSort = "";
                //把查询要求设置到查询条件中
                this.courseQueryFactor.buyCountSort = this.buyCountSort;
                this.courseQueryFactor.gmtCreateSort = this.gmtCreateSort;
                this.courseQueryFactor.priceSort = this.priceSort;
                //带着当前页进行课程分页数据查询
                this.gotoPage(this.page)
            },
            //按课程创建时间排序查询
            searchGmtCreate() {
                this.buyCountSort = "";
                this.gmtCreateSort = "1";
                this.priceSort = "";
                this.courseQueryFactor.buyCountSort = this.buyCountSort;
                this.courseQueryFactor.gmtCreateSort = this.gmtCreateSort;
                this.courseQueryFactor.priceSort = this.priceSort;
                this.gotoPage(this.page)
            },
            //按照课程价格排序查询
            searchPrice() {
                this.buyCountSort = "";
                this.gmtCreateSort = "";
                this.priceSort = "1";
                this.courseQueryFactor.buyCountSort = this.buyCountSort;
                this.courseQueryFactor.gmtCreateSort = this.gmtCreateSort;
                this.courseQueryFactor.priceSort = this.priceSort;
                this.gotoPage(this.page)
            },
            //分页查询
            gotoPage(page) {
                //console.log(page)
                if(((page<=this.data.pages & page>0) || this.data.pages==0)){
                    this.page = page
                    course.pageFactorCourse(page, 8, this.courseQueryFactor).then(response => {
                        this.data = response.data.data
                    })
                }
            }
        }
    }
</script>
<style scoped>
.active {
    background: rgb(21, 189, 166);
}
.hit{
    background: rgb(163, 223, 241);
}
.hide {
    display: none;
}
.show {
    display: block;
}
</style>