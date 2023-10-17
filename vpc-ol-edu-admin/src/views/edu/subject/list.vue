<template>
    <div class="app-container">
        <!--el-input是一个检索功能，输入关键字能检索树形结构的课程-->
        <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />
        <!--el-tree中显示课程分类信息
            ref="tree2"理解为el-tree的唯一标识
            :data="data2"表示要显示的数据，即Data中data2的数据，并自动对数据进行了遍历显示
            :props="defaultProps"表示取到节点和子节点的名称，讲的不是很清楚
            :filter-node-method="filterNode"是检索框相关的功能
            
            class="filter-tree"
            default-expand-all是相关的样式功能，讲的非常草率

            目前的工作是写一个接口，把查询到的课程信息封装成data2给前端自动遍历即可，数据的格式必须和data2中的格式要一样
        -->
        <el-tree
            ref="tree2"
            :data="subjects"
            :props="defaultProps"
            :filter-node-method="filterNode"
            class="filter-tree"
            default-expand-all
        />
    </div>
</template>

<script>
    import subject from '@/api/edu/subject.js'
    export default {
        data() {
            return {
                filterText: '',
                //展示信息的基本结构是id为分类信息的id，label是要展示的分类信息，如果有子分类将子分类信息放在children中，以这种形式进行嵌套
                subjects: [],
                defaultProps: {
                    children: 'children',
                    label: 'title'
                }
            }
        },
        created(){
            this.getAllSubjectList()
        },
        watch: {
            filterText(val) {
                this.$refs.tree2.filter(val)
            }
        },
        methods: {
            getAllSubjectList(){
                subject.findAllSubject()
                .then(response=>{
                    this.subjects=response.data.subjects
                }).catch(error=>{
                    console.log(error)
                })
            },
            filterNode(value, data) {
                if (!value) return true
                return data.title.toLowerCase().indexOf(value.toLowerCase()) !== -1
            }
        }
    }
</script>