商业模式

B2C

管理员【增删改，使用系统后台】和普通用户【查，使用系统前台】

B2B2C模式

京东【普通用户可以买自营也可以买普通商家】



在线教育平台

使用B2C商业模式，

系统后台包括"讲师管理","课程分类管理","课程管理","统计分析","订单管理","banner管理","权限管理"模块

系统前台包括"首页数据显示","讲师列表详情","课程列表详情【包括视频在线播放】","注册登录","微信扫描登录","微信扫描支付"功能



涉及技术

后端：springBoot、SpringCloud、MyBatisPlus、SpringSecurity、redis、Maven、easyExcel、jwt、OAuth2

前端：vue、element-ui、框架【TODO】、axios、node.js

其他技术：

阿里云oss、阿里云视频点播服务、阿里云短信服务、

微信支付和登录、docker、git、Jenkins



前后端分离开发

前端负责数据显示，用到html、css、js、jq

后端返回数据或者操作数据、结构为controller、service、mapper，java中开发接口指的就是开发上述3个结构的过程

前后端的联系是前端发送ajax请求调用后端接口将json数据返回给前端的过程



> 代码细节看文档，文档后端前端代码步骤很详细，这篇文档只讲开发要点



# 后台系统

# 讲师管理模块开发

1. 创建数据库表

   + 表名edu_teacher，文件edu_teacher.sql
   + 数据库表设计规约，
     + 核心库名与应用名一致，
     + 表名字段名必须使用小写字母或数字，进制数字开头，表名不使用复数名词
     + 表命名用"业务名_表的作用"
     + 表必备三字段："id"【类型bigint unsigned，单表时自增；分库分表集群部署是id为varchar，非自增，业务中使用分布式id生成器】、"gmt_create"【类型为datetime类型，记录创建时间】、"gmt_modified"【同前，记录更新时间】
     + 单表行数超500万行或单表容量超2GB才进行分库分表，预计三年后数据量达不到这个水平，建表时不靠分库分表
     + 表达是与否概念字段使用is_xxx格式命名，数据类型是unsigned tinyint【1表示是，0表示否】
     + 非负数字段必须为unsigned
     + 小数类型为decimal，进制使用float和double，这俩存储时存在精度损失问题，如果数据范围超限，整数和小数分开存储
     + 存储字符串长度几乎相等时，用char

2. 创建项目结构

   > 其中父工程创建springboot工程，子模块和子子模块都创建maven工程

   + 创建父工程【管理依赖版本以及存放公共依赖 】

     + 子模块1
       + 子子模块1
       + 子子模块2
     + 子模块2
     + ...

   + 总的模块目录

     ![](C:\Users\Earl\Desktop\项目模块结构.png)

   + 创建流程

     + 太多了，看文档【主要流程是创子模块；引入依赖；配置mp相关的spring配置项，注意乐观锁、逻辑删除相关功能没有涉及，还设置了返回json数据的时间格式为东八区；用mp代码生成器生成实体类和所有的目录结构实体类、mapper、controller、service；写控制器方法；创建启动类；创建配置类配置mapper扫描和其他；使用设定的模块端口号8001启动项目】

       + application.properties的配置项

         ```properties
         # 服务端口,这是整个模块对应的服务器端口，不写这个会默认使用tomcat的8080端口
         server.port=8001
         # 服务名
         spring.application.name=service-edu
         # 环境设置： dev、 test、 prod,用来配置mybatis-plus的sql执行性能的
         spring.profiles.active=dev
         # mysql数据库连接
         spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
         spring.datasource.url=jdbc:mysql://localhost:3306/ol_education?serverTimezone=GMT%2B8
         spring.datasource.username=root
         spring.datasource.password=Haworthia0715
         #mybatis日志
         mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
         
         #控制器返回json的全局时间格式设置为东八区并设置json中时间的格式
         spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
         spring.jackson.time-zone=GMT+8
         ```

         

     + 特点：

       + 父工程和子模块中都不写代码

       + 子子模块service_edu用mp提供的代码生成器生成相关代码

         ```xml
         <!-- velocity模板引擎,Mybatis Plus代码生成器需要,mybatis-Plus在3.0.3之后移除了代码生成器与模板引擎间的默认依赖，需要手动添加代码生成的依赖mybatis-plus-generator -->
         <dependency>
             <groupId>org.apache.velocity</groupId>
             <artifactId>velocity-engine-core</artifactId>
         </dependency>
         ```

         + 生成器代码

           ```java
           public class CodeGenerator {
               @Test
               public void main1() {
                   // 1、创建代码生成器
                   AutoGenerator mpg = new AutoGenerator();
                   // 2、全局配置
                   GlobalConfig gc = new GlobalConfig();
                   String projectPath = System.getProperty("user.dir");
                   System.out.println(projectPath);
                   gc.setOutputDir(projectPath + "/src/main/java");
                   gc.setAuthor("atguigu");
                   gc.setOpen(false); //生成后是否打开资源管理器
                   gc.setFileOverride(false); //重新生成时文件是否覆盖
                   /*
                    * mp生成service层代码，默认接口名称第一个字母有 I
                    * UcenterService
                    * */
                   gc.setServiceName("%sService"); //去掉Service接口的首字母I
                   gc.setIdType(IdType.ID_WORKER); //主键策略
                   gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
                   gc.setSwagger2(true);//开启Swagger2模式
                   mpg.setGlobalConfig(gc);
                   // 3、数据源配置
                   DataSourceConfig dsc = new DataSourceConfig();
                   dsc.setUrl("jdbc:mysql://localhost:3306/guli?serverTimezone=GMT%2B8");
                   dsc.setDriverName("com.mysql.cj.jdbc.Driver");
                   dsc.setUsername("root");
                   dsc.setPassword("root");
                   dsc.setDbType(DbType.MYSQL);
                   mpg.setDataSource(dsc);
                   // 4、包配置
                   PackageConfig pc = new PackageConfig();
                   pc.setModuleName("serviceedu"); //模块名
                   pc.setParent("com.atguigu");
                   pc.setController("controller");
                   pc.setEntity("entity");
                   pc.setService("service");
                   pc.setMapper("mapper");
                   mpg.setPackageInfo(pc);
                   // 5、策略配置
                   StrategyConfig strategy = new StrategyConfig();
                   strategy.setInclude("edu_teacher");
                   strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
                   strategy.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
                   strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
                   strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain =true) setter链式操作
                   strategy.setRestControllerStyle(true); //restful api风格控制器
                   strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
                   mpg.setStrategy(strategy);
                   // 6、执行
                   mpg.execute();
               }
           }
           ```

         + 作用是生成实体类并创建好后端结构目录

           + 接口中的内容需要自己写，mapper接口自动实现了BaseMapper<EduTeacher>接口，无需自己建mapper



4. 讲师逻辑删除功能

   + 控制器写删除方法【controller调用service的removeById，即调用mapper的deleteById】，请求路径需要传递讲师id，用@PathVariable注解读取请求路径的路径变量

   + 配置逻辑删除插件LogicSqlInjector，在表示逻辑删除字段上添加@TableLogic注解

   + 用swagger进行接口测试

     + swagger用于生成在线接口文档、方便接口测试、描述、调用、可视化Restful风格的web服务，是一个规范完整的框架，具有及时性、规范性、一致性、可测性的特点

     + 配置swagger2

       + 创建子模块common、在common下创建service_base子子模块，创建配置类向IoC容器中注入Docket组件，docket组件的类型是SWAGGER_2

         ```java
         @Configuration
         @EnableSwagger2//这个注解的作用不明白，是swagger的注解，估计是使swagger生效的注解
         public class Swagger2Config {
             @Bean
             public Docket webApiConfig() {
                 return new Docket(DocumentationType.SWAGGER_2)
                         .groupName("webApi")//这个名字表示组名，可以随便取
                         .apiInfo(webApiInfo())//apiInfo调用webApiInfo()方法设置在线文档的一些信息
                         .select()
                         .paths(Predicates.not(PathSelectors.regex("/admin/.*")))//这两行表示如果请求路径中包含这两种格式就不适用swagger对其进行显示
                         .paths(Predicates.not(PathSelectors.regex("/error.*")))
                         .build();
             }
             private ApiInfo webApiInfo(){
                 return new ApiInfoBuilder()
                         .title("网站-课程中心API文档")
                         .description("本文档描述了课程中心微服务接口定义")
                         .version("1.0")
                         .contact(new Contact("java", "http://atguigu.com", "55317332@qq.com"))
                         .build();
             }
         }
         ```

       + 在service模块下引入service_base模块

         > 注意此时配置类swagger的包名为com.atlisheng.servicebase，而service模块下组件扫描的包默认是com.atlisheng.eduservice，两个包名不一样，swagger无法扫描到，需要用@ComponentScan将包扫描范围增大到com.atlisheng

         ```xml
         <!--在service模块下引入service_base模块的依赖来使用swagger服务-->
         <dependency>
             <groupId>com.atlisheng</groupId>
             <artifactId>service_base</artifactId>
             <version>0.0.1-SNAPSHOT</version>
         </dependency>
         ```

         扩大组件扫描范围

         ```java
         @SpringBootApplication
         @ComponentScan(basePackages = "com.atlisheng")
         public class EduServiceApplication {
             public static void main(String[] args) {
                 SpringApplication.run(EduServiceApplication.class,args);
             }
         }
         ```

       + 访问swagger的网址，固定为http://localhost:8001/swagger-ui.html

       + Swagger定义接口说明和参数说明的注解

         + @Api
           + 定义在类上，description属性会展示在swagger默认显示控制器类名的位置
         + @ApiOperation
           + 定义在方法上，value属性会展示在方法的说明上
         + @ApiParam
           + 定义在控制器方法的参数前，会展示在参数的说明上【其中required属性表示该参数是必须传参的】



6. 统一返回数据格式

   + 统一返回数据格式，将响应封装成json返回，能够使前端（IOS、ANDROID、Web）对数据操作更轻松

   + 返回数据格式一般包括响应信息、状态码、处理信息、返回数据，本项目返回数据格式如下：

     + 列表返回数据【注意列表数据的key用items】

     ```json
     {
         "success": true,
         "code": 20000,
         "message": "成功",
         "data": {
             "items": [
                 {
                 "id": "1",
                 "name": "刘德华",
                 "intro": "毕业于师范大学数学系，热爱教育事业，执教数学思维6年有余"
                 }
             ]
         }
     }
     ```

     + 分页数据【key用total，rows】

     ```json
     {
     "success": true,
     "code": 20000,
     "message": "成功",
     "data": {
     "total": 17,
     "rows": [
     {
     "id": "1",
     "name": "刘德华",
     "intro": "毕业于师范大学数学系，热爱教育事业，执教数学思维6年有余"
         }
     ]
     }
     }
     ```

     + 没有返回数据

     ```json
     {
         "success": true,
         "code": 20000,
         "message": "成功",
         "data": {}
     }
     ```

     + 响应失败

     ```json
     {
         "success": false,
         "code": 20001,
         "message": "失败",
         "data": {}
     }
     ```

     + 从而可以定义统一结果

     ```json
     {
         "success": 布尔, //响应是否成功
         "code": 数字, //响应码
         "message": 字符串, //返回消息
         "data": HashMap //返回数据，放在键值对中
     }
     ```

     > 用HashMap自动将键值对转成json对象来满足不同情景响应数据的需求

   + 创建统一结果返回类

     + 创建响应状态码接口【在common_utils下创建ResponseCode接口存放常量响应状态码、创建ResponseData作为统一响应结果类】

     + 创建响应结果类

       ```java
       @Data
       public class ResponseData {
           @ApiModelProperty(value = "响应是否成功")//这个注解的信息会展示在swagger中
           private Boolean success;
       
           @ApiModelProperty(value = "服务器响应状态码")
           private Integer code;
       
           @ApiModelProperty(value = "响应信息")
           private String message;
       
           @ApiModelProperty(value = "服务器响应数据")
           private Map<String,Object> data=new HashMap<>();
       
           private ResponseData(){
       
           }
       
           /**
            * @return {@link ResponseData }
            * @描述 成功响应返回响应数据的静态方法
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/27
            * @since 1.0.0
            */
           public static ResponseData responseCall(){
               ResponseData responseData = new ResponseData();
               responseData.setSuccess(true);
               responseData.setCode(ResponseCode.SUCCESS);
               responseData.setMessage("响应成功");
               return responseData;
           }
       
           /**
            * @return {@link ResponseData }
            * @描述 异常响应调用方法
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/27
            * @since 1.0.0
            */
           public static ResponseData responseErrorCall(){
               ResponseData responseData = new ResponseData();
               responseData.setSuccess(false);
               responseData.setCode(ResponseCode.ERROR);
               responseData.setMessage("响应异常");
               return responseData;
           }
       
           /**
            * @param success 成功
            * @return {@link ResponseData }
            * @描述 以下方法是为了方便链式编程，即对一个对象一顿.，就可以设置其中的属性，如responseData.success(true).message("响应成功").code("20000").data()
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/27
            * @since 1.0.0
            */
           public ResponseData success(Boolean success){
               this.setSuccess(success);
               return this;
           }
           public ResponseData message(String message){
               this.setMessage(message);
               return this;
           }
           public ResponseData code(Integer code){
               this.setCode(code);
               return this;
           }
           public ResponseData data(String key, Object value){
               this.data.put(key, value);
               return this;
           }
           public ResponseData data(Map<String, Object> map) {
               this.setData(map);
               return this;
           }
       }
       ```

     + 在service模块中添加统一返回结果的依赖

       ```xml
       <!--在service模块下引入common_utils模块的依赖来使用统一返回结果类-->
       <dependency>
           <groupId>com.atlisheng</groupId>
           <artifactId>common_utils</artifactId>
           <version>0.0.1-SNAPSHOT</version>
       </dependency>
       ```

       

7. 讲师查询结果分页

   + 配置类中配置分页插件

     ```java
     @Bean
     public PaginationInterceptor paginationInterceptor(){
         return new PaginationInterceptor();
     }
     ```

   + 分页查询方法

     ```java
     @GetMapping("pageTeacher/{current}/{limit}")
     @ApiOperation(value = "讲师分页查询")
     public ResponseData findAllTeacherPaging(@ApiParam(name = "current",value = "当前页",required = true) @PathVariable Integer current,
                                              @ApiParam(name = "limit",value = "每页记录条数",required = true) @PathVariable Integer limit){
         Page<EduTeacher> teacherPage = new Page<>(current, limit);
         teacherService.page(teacherPage,null);
         return ResponseData.responseCall().data("total",teacherPage.getTotal()).data("rows",teacherPage.getRecords());//getRecords是获取当前页的所有记录集合
     }
     ```

     

8. 条件查询

   > 根据讲师的名字，头衔和入驻时间gmt_create查询讲师记录，实现多条件组合查询带分页

   + 在entity的vo包下创建条件查询对象TeacherQueryFactor，把条件值封装到对象，将对象传递到接口中

     ```java
     @ApiModel(value = "Teacher查询对象", description = "讲师查询对象封装")
     @Data
     public class TeacherQueryFactor implements Serializable {
         private static final long serialVersionUID = 1L;
         @ApiModelProperty(value = "教师名称,模糊查询")//这个@ApiModelProperty的example属性表示在swagger中传入参数举例，String默认是String，Integer默认是0
         private String name;
         @ApiModelProperty(value = "头衔 1高级讲师 2首席讲师")
         private Integer level;
         @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
         private String beginTime;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换
         @ApiModelProperty(value = "查询结束时间", example = "2019-12-01 10:10:10")
         private String endTime;
     }
     ```

   + 条件查询带分页的控制器方法

     ```java
     /**
      * @param current            当前页
      * @param limit              每页记录数
      * @param teacherQueryFactor 讲师查询条件
      * @return {@link ResponseData }
      * @描述 讲师多条件组合带分页查询
      * @author Earl
      * @version 1.0.0
      * @创建日期 2023/08/27
      * @since 1.0.0
      */
     @PostMapping("pageFactorTeacher/{current}/{limit}")
     @ApiOperation(value = "讲师多条件组合带分页查询")
     public ResponseData findFactorTeacherPaging(@ApiParam(name = "current",value = "当前页",required = true) @PathVariable Integer current,
                                                 @ApiParam(name = "limit",value = "每页记录条数",required = true) @PathVariable Integer limit,
                                                 @ApiParam(name = "teacherQueryFactor",value = "讲师筛选条件") @RequestBody(required = false) TeacherQueryFactor teacherQueryFactor){//@RequestBody将json数据封装到对应的对象中
         Page<EduTeacher> teacherPage = new Page<>();
         QueryWrapper<EduTeacher> queryWrapper = new QueryWrapper<>();
         String teacherName = teacherQueryFactor.getName();
         Integer teacherLevel = teacherQueryFactor.getLevel();
         String beginTime = teacherQueryFactor.getBeginTime();
         String endTime = teacherQueryFactor.getEndTime();
         if (!StringUtils.isEmpty(teacherName)){
             queryWrapper.like("name",teacherName);
         }
         if (!StringUtils.isEmpty(teacherLevel)){
             queryWrapper.eq("level",teacherLevel);
         }
         if (!StringUtils.isEmpty(beginTime)){
             queryWrapper.ge("gmt_create",beginTime);
         }
         if (!StringUtils.isEmpty(endTime)){
             queryWrapper.le("gmt_create",endTime);
         }
         teacherService.page(teacherPage,queryWrapper);
         return ResponseData.responseCall().data("total",teacherPage.getTotal()).data("rows",teacherPage.getRecords());
     }
     ```

9. 实现gmt_Create和gmt_Modified字段的自动填充功能

   + 在实体类中添加自动填充注解@TableField
   + 编写自定义源对象处理器MyMetaObjectHandler并用@Component注解纳入Spring容器管理

   

10. 讲师添加到列表功能

    + 控制器方法，@RequestBody注解接收post提交的参数封装成EduTeacher对象并调用service的save(entity)方法实现数据存入，版本号和逻辑删除默认值是由数据库设置的

    

11. 讲师信息更新功能

    + 第一步在控制器方法中根据讲师id查询讲师信息
    + 第二步在另一个控制器方法中路径传递讲师id，@RequestBody封装讲师信息，使用put提交方式

    

12. 统一异常处理

    + 在common_base中创建统一异常处理类GlobalExceptionHandler.java,作用是捕捉所有异常打印异常堆栈信息然后响应ResponseData.responseErrorCall().message("服务器异常,请联系管理员")

      > 这上面的@ControllerAdvice注解是干什么的

      ```java
      @ControllerAdvice
      public class GlobalExceptionHandler {
          @ExceptionHandler(Exception.class)//指定出现哪种异常的情况下执行该方法
          @ResponseBody
          public ResponseData handleException(Exception e){
              e.printStackTrace();
              return ResponseData.responseErrorCall().message("服务器异常,请联系管理员");
          }
      }
      ```

    + 发生@ExceptionHandler(Exception.class)这种指定的异常会去执行该方法

      > 还有特定异常处理和自定义异常处理见文档，核心还是捕捉不同类型的异常执行不同的方法

    + 特定异常处理

      > 这种异常的优先级高于全局异常处理，发生对应异常会优先去调用特定异常的处理方法

      ```java
      @ExceptionHandler(ArithmeticException.class)//特定异常执行该方法
      @ResponseBody
      public ResponseData handleException(ArithmeticException e){
          e.printStackTrace();
          log.error(e.getMessage());
          return ResponseData.responseErrorCall().message("数学运算异常,请联系管理员");
      }
      ```

    + 自定义异常处理

      > 就是自己自定义异常，然后用特定异常处理进行处理

      + 第一步：创建自定义异常类继承RuntimeException，确定异常属性，状态码和异常信息

        ```java
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class CustomException extends RuntimeException{
            /**
             * 状态码
             */
            private Integer code;
        
            /**
             * 一张信息
             */
            private String msg;
        }
        ```

      + 第二步：对自定义异常进行特定异常处理

        > 自定义异常是在try语句块发生异常捕捉后在catch语句块中手动抛出的

        ```java
        @ExceptionHandler(CustomException.class)//自定义异常执行方法
        @ResponseBody
        public ResponseData handleException(CustomException e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseData.responseErrorCall().code(e.getCode()).message(e.getMessage());
        }
        ```

      + 第三步：自定义异常使用举例

        ```java
        @GetMapping("pageTeacher/{current}/{limit}")
        @ApiOperation(value = "分页查询全部讲师")
        public ResponseData findAllTeacherPaging(@ApiParam(name = "current",value = "当前页",required = true) @PathVariable Integer current,
                                                 @ApiParam(name = "limit",value = "每页记录条数",required = true) @PathVariable Integer limit){
            Page<EduTeacher> teacherPage = new Page<>(current, limit);
            try{
                int i=10/0;
            }catch (Exception e){
                throw new CustomException(20001,"执行了自定义异常处理...");
            }
            //int i=10/0;//这种会由系统抛出算术异常
            teacherService.page(teacherPage,null);
            return ResponseData.responseCall().data("total",teacherPage.getTotal()).data("rows",teacherPage.getRecords());//getRecords是获取当前页的所有记录集合
        }
        ```

      

    

13. 日志

    + 日志记录器的行为级别OFF、 FATAL、 ERROR、 WARN、 INFO、 DEBUG、 ALL  ，默认情况下SpringBoot在控制台打印的日志级别为INFO及以上的日志级别

    + 通过Spring的配置文件可以设置在控制台打印的日志级别，如

      ```properties
      #设置SpringBoot的控制台打印日志的级别为WARN
      logging.level.root=WARN
      ```

      > 这种方式只能将日志打印在控制台上

    + 使用logback日志工具能将日志输出到控制台也能输出到文件

      > 常见日志工具有log4j，logback

      + 使用logback需要删除application.properties的日志配置，包括mybatis的日志配置

      + 配置logback日志

        + idea安装日志插件：grep-console

          > 这个插件是一个彩色日志插件，但是我没装以前日志也是彩色的，感觉装没装没影响

        + 在resource中创建logback-spring.xml，并复制拷贝下列内容

          > 其中就规定了日志文件的输出地址

          ```xml
          <?xml version="1.0" encoding="UTF-8"?>
          <configuration scan="true" scanPeriod="10 seconds">
              <!-- 日志级别从低到高分为TRACE < DEBUG < INFO < WARN < ERROR < FATAL，如果设置为WARN，则低于WARN的信息都不会输出 -->
              <!-- scan:当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true -->
              <!-- scanPeriod:设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。 -->
              <!-- debug:当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。 -->
              <contextName>logback</contextName>
          
              <!-- name的值是变量的名称， value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义变量后，可以使“${}”来使用变量。 -->
              <property name="log.path" value="D:/E:/JavaStudy/project/ol_edu/edu" />
          
              <!-- 彩色日志 -->
              <!-- 配置格式变量： CONSOLE_LOG_PATTERN 彩色日志格式 -->
              <!-- magenta:洋红 -->
              <!-- boldMagenta:粗红-->
              <!-- cyan:青色 -->
              <!-- white:白色 -->
              <!-- magenta:洋红 -->
              <property name="CONSOLE_LOG_PATTERN"
                        value="%yellow(%date{yyyy-MM-dd HH:mm:ss}) |%highlight(%-5level)|%blue(%thread) |%blue(%file:%line) |%green(%logger) |%cyan(%msg%n)"/>
              <!--输出到控制台-->
              <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
                  <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
                  <!-- 例如：如果此处配置了INFO级别，则后面其他位置即使配置了DEBUG级别的日志，也不会被输出 -->
                  <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
                      <level>INFO</level>
                  </filter>
                  <encoder>
                      <Pattern>${CONSOLE_LOG_PATTERN}</Pattern>
                      <!-- 设置字符集 -->
                      <charset>UTF-8</charset>
                  </encoder>
              </appender>
              <!--输出到文件-->
              <!-- 时间滚动输出 level为 INFO 日志 -->
              <appender name="INFO_FILE"
                        class="ch.qos.logback.core.rolling.RollingFileAppender">
                  <!-- 正在记录的日志文件的路径及文件名 -->
                  <file>${log.path}/log_info.log</file>
                  <!--日志文件输出格式-->
                  <encoder>
                      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level
                          %logger{50} - %msg%n</pattern>
                      <charset>UTF-8</charset>
                  </encoder>
                  <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
                  <rollingPolicy
                          class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                      <!-- 每天日志归档路径以及格式 -->
                      <fileNamePattern>${log.path}/info/log-info-%d{yyyy-MMdd}.%i.log</fileNamePattern>
                      <timeBasedFileNamingAndTriggeringPolicy
                              class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                          <maxFileSize>100MB</maxFileSize>
                      </timeBasedFileNamingAndTriggeringPolicy>
                      <!--日志文件保留天数-->
                      <maxHistory>15</maxHistory>
                  </rollingPolicy>
                  <!-- 此日志文件只记录info级别的 -->
                  <filter class="ch.qos.logback.classic.filter.LevelFilter">
                      <level>INFO</level>
                      <onMatch>ACCEPT</onMatch>
                      <onMismatch>DENY</onMismatch>
                  </filter>
              </appender>
              <!-- 时间滚动输出 level为 WARN 日志 -->
              <appender name="WARN_FILE"
                        class="ch.qos.logback.core.rolling.RollingFileAppender">
                  <!-- 正在记录的日志文件的路径及文件名 -->
                  <file>${log.path}/log_warn.log</file>
                  <!--日志文件输出格式-->
                  <encoder>
                      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level
                          %logger{50} - %msg%n</pattern>
                      <charset>UTF-8</charset> <!-- 此处设置字符集 -->
                  </encoder>
                  <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
                  <rollingPolicy
                          class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                      <fileNamePattern>${log.path}/warn/log-warn-%d{yyyy-MMdd}.%i.log</fileNamePattern>
                      <timeBasedFileNamingAndTriggeringPolicy
                              class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                          <maxFileSize>100MB</maxFileSize>
                      </timeBasedFileNamingAndTriggeringPolicy>
                      <!--日志文件保留天数-->
                      <maxHistory>15</maxHistory>
                  </rollingPolicy>
                  <!-- 此日志文件只记录warn级别的 -->
                  <filter class="ch.qos.logback.classic.filter.LevelFilter">
                      <level>warn</level>
                      <onMatch>ACCEPT</onMatch>
                      <onMismatch>DENY</onMismatch>
                  </filter>
              </appender>
              <!-- 时间滚动输出 level为 ERROR 日志 -->
              <appender name="ERROR_FILE"
                        class="ch.qos.logback.core.rolling.RollingFileAppender">
                  <!-- 正在记录的日志文件的路径及文件名 -->
                  <file>${log.path}/log_error.log</file>
                  <!--日志文件输出格式-->
                  <encoder>
                      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level
                          %logger{50} - %msg%n</pattern>
                      <charset>UTF-8</charset> <!-- 此处设置字符集 -->
                  </encoder>
                  <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
                  <rollingPolicy
                          class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                      <fileNamePattern>${log.path}/error/log-error-%d{yyyy-MMdd}.%i.log</fileNamePattern>
                      <timeBasedFileNamingAndTriggeringPolicy
                              class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                          <maxFileSize>100MB</maxFileSize>
                      </timeBasedFileNamingAndTriggeringPolicy>
                      <!--日志文件保留天数-->
                      <maxHistory>15</maxHistory>
                  </rollingPolicy>
                  <!-- 此日志文件只记录ERROR级别的 -->
                  <filter class="ch.qos.logback.classic.filter.LevelFilter">
                      <level>ERROR</level>
                      <onMatch>ACCEPT</onMatch>
                      <onMismatch>DENY</onMismatch>
                  </filter>
              </appender>
              <!--
                  <logger>用来设置某一个包或者具体的某一个类的日志打印级别、以及指定<appender>。
                  <logger>仅有一个name属性，一个可选的level和一个可选的addtivity属性。
                      name:用来指定受此logger约束的某一个包或者具体的某一个类。
                      level:用来设置打印级别，大小写无关： TRACE, DEBUG, INFO, WARN, ERROR, ALL和 OFF，如果未设置此属性，那么当前logger将会继承上级的级别。
              -->
              <!--
              使用mybatis的时候， sql语句是debug下才会打印，而这里我们只配置了info，所以想要查看sql语句的话，有以下两种操作：
                  第一种把<root level="INFO">改成<root level="DEBUG">这样就会打印sql，不过这样日志那边会出现很多其他消息
                  第二种就是单独给mapper下目录配置DEBUG模式，代码如下，这样配置sql语句会打印，其他还是正常DEBUG级别：
              -->
              <!--开发环境:打印控制台-->
              <springProfile name="dev">
                  <!--可以输出项目中的debug日志，包括mybatis的sql日志-->
                  <logger name="com.guli" level="INFO" />
                  <!--
                  root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
                      level:用来设置打印级别，大小写无关： TRACE, DEBUG, INFO, WARN, ERROR,ALL 和 OFF，默认是DEBUG,可以包含零个或多个appender元素。
                  -->
                  <root level="INFO">
                      <appender-ref ref="CONSOLE" />
                      <appender-ref ref="INFO_FILE" />
                      <appender-ref ref="WARN_FILE" />
                      <appender-ref ref="ERROR_FILE" />
                  </root>
              </springProfile>
              <!--生产环境:输出到文件-->
              <springProfile name="pro">
                  <root level="INFO">
                      <appender-ref ref="CONSOLE" />
                      <appender-ref ref="DEBUG_FILE" />
                      <appender-ref ref="INFO_FILE" />
                      <appender-ref ref="ERROR_FILE" />
                      <appender-ref ref="WARN_FILE" />
                  </root>
              </springProfile>
          </configuration>
          ```

    + 将程序运行异常的信息输出到文件中

      + 第一步：在统一异常处理类上添加@Slf4j注解

      + 第二步：使用异常输出语句log.error(e.getMessage)，错误信息就会输出到error.log中

        ```java
        @ControllerAdvice
        @Slf4j
        public class GlobalExceptionHandler {
            @ExceptionHandler(Exception.class)//指定出现哪种异常的情况下执行该方法
            @ResponseBody
            public ResponseData handleException(Exception e){
                e.printStackTrace();
                log.error(e.getMessage());
                return ResponseData.responseErrorCall().message("服务器异常,请联系管理员");
            }
        }
        ```

      + 默认异常信息写入文件只写一行信息，想要写的更详细可以参考文档ExceptionUtil.java工具类  ，可以将详细信息输出到日志中

    



# 前端框架搭建

## VS code前端框架搭建

+ 安装插件ch【汉化】、Live Server【类似于tomcat的服务器，有了这个没有tomcat也可以模拟出服务器的效果，能通过端口号进行访问，用法是页面直接右键用live Server打开】、Vetur、Vue-helper【这两个插件方便vue的开发、比如不同代码会有颜色的变化】

+ 创建工作区

  > 前端的代码都写在工作区中

  + 第一步在本地创建空文件夹并使用vscode打开该文件夹
  + 第二步把文件夹另存为工作区，见文档

+ ECMAScript6

  + ES6是JS的一种标准，是JS请国际标准化组织制定的希望通过标准使JS成为浏览器脚本语言的国际标准，ES6是JS的规格，JS是ES6的实现，ES6是2015年开始发布的，泛指ES5.1之后的版本
  + ES6代码简洁，但浏览器的兼容性很差，ES5代码复杂，但是浏览器兼容性很好

+ ES6基本语法

  + 变量声明
    + let声明的变量有局部作用域，即大括号外无法访问大括号内let声明的变量；同一个变量只能被let声明一次
    + var声明的变量没有局部作用域，到处都可以访问；且同一个变量可以被var声明多次
  + 太多了，见E:\JavaStudy\project\ol_edu\vpc_ol_js\es6_std的笔记

+ Vue.js简介

  + 用于构建用户界面的渐进式框架，只关注视图层且便于与第三方库或既有项目整合
  + vue的js文件`vue.min.js`
  + 步骤
    + 第一步：引入vue的js文件
    + 第二步：搭建前端页面的架子，感叹号生成html页面，引入vue的js文件，在脚本块中搭建vue对象的架子
    + 第三步：使用插值表达式获取vue对象中data中的数据
    + 第四步：抽取vue代码片段
      + 文件 => 首选项 => 用户代码片段 => 新建全局代码片段/或文件夹代码片段：  
      + 使用的时候用代码片段开头的文字选中创建出来，具体看视频【文档写的不好】

+ Vue基本语法

  + 太多，看E:\JavaStudy\project\ol_edu\vpc_ol_js\vue_std的笔记
  + 组件：组件可以扩展HTML元素，封装可重用的代码，实现使用可复用的小组件来构建大型应用，几乎所有类型的应用界面都可以抽象为一个组件树
  
+ vue路由

  + 路由用于设定访问路径，并将路径和组件映射起来。传统的页面应用，是用一些超链接来实现页面切换和跳转的。在vue-router单页面应用中，则是路径之间的切换，实际上就是组件的切换。路由就是SPA（单页应用）的路径管理器。再通俗的说，vue-router就是我们WebApp的链接路径管理系统。因为我们一般用Vue做的都是单页应用，只有一个主页面index.html，所以你写的<a></a>标签是不起作用的，要使用vue-router来进行管理

+ vue通过axios发送ajax请求

  + axios是独立的项目，不属于vue，但是常和vue一起使用实现ajax操作
  + axios的使用
    + 引入axios和vue的依赖，用data.json模拟服务器返回的数据
    + 使用axios发送ajax请求，请求文件得到数据并在页面展示【在vue的method中用*axios.提交方式("请求接口路径[文件写文件路径]").then(箭头函数).catch(箭头函数)*放松ajax请求并对数据进行处理】

+ 使用element-ui

  + element-ui是饿了么基于vue的后台组件库，方便页面的快速布局和构建，官网[Element - 网站快速成型工具](https://element.eleme.cn/#/zh-CN)
  + 可以选取前端组件直接生成代码，然后自己改改就能得到一样的效果

+ node.js

  + Node.js是一个时间驱动I/O服务端js运行环境，类比于java的jdk，基于google的v8引擎，理解成不需要浏览器，可以在服务端执行javaScript的代码环境，本质就是浏览器底层那一套东西

  + 此外，Node.js还可以模拟服务器效果，如tomcat，

    > js文件写代码模拟服务器行为，nodejs运行后可以在浏览器对指定的端口号的请求路径进行访问

    > node.js安装最好默认安装到c盘，`node -v`查看nodejs版本信息【本机使用v10.24.1】，LTS是长期支持版本

  + 使用node执行js代码

    + 编写js文件，从文件位置进入DOS命令窗口
    + `node 文件名.js`：运行js文件，console.log之类的命令会在DOS窗口显示,还可以模拟服务器的效果
    + 关闭模拟服务器的nodejs服务直接按ctrl+c

  + vscode打开cmd窗口终端，可以在vscode中使用命令用nodejs执行js代码

    + powershell有管理员权限，cmd没有管理员权限
    
  + NPM

    + NPM全称Node Package Manager，是Node.js包管理工具，全球最大的免费开源模块生态系统，也是Node.js的包管理工具，相当于前端的maven，和maven一样下载js依赖如jquery需要联网

    + 通过npm可以方便的管理前端工程，下载js库，Node.js默认将npm包安装到node.js\node_modules目录下，即Node.js已经集成了npm工具

    + `npm -v`可以查看当前npm版本

    + npm操作

      + `npm init`npm项目初始化操作,在文件工程路径下使用，项目初始化完成之后，生成package.json，类似于后端的pom.xml

        + `npm init -y`是项目默认初始化，少了一些询问而采用默认设置

      + `npm install 依赖名称`npm下载js依赖，下载好的依赖会自动放入当前项目的node_modules目录下，package.json的依赖信息中会自动添加对应依赖信息，这里面的依赖信息可能变化;安装特定版本在依赖名称后加`@具体版本`,可以在package-lock.json中锁定依赖的版本

      + `npm config set registry https://registry.npm.taobao.org`:npm官方管理的包都从http:..npmjs.com下载，网速慢，推荐使用淘宝的NPM镜像，该命令就是修改npm镜像

        + `npm config list`:查看npm配置信息

      + 有package.json但没下载依赖，使用`npm install`可以实现所有依赖下载

      + 以下命令表示在项目中安装一些插件

        + \#devDependencies节点：开发时的依赖包，项目打包到生产环境的时候不包含的依赖
          \#使用 -D参数将依赖添加到devDependencies节点
          npm install --save-dev eslint
          \#或
          npm install -D eslint

          > 当前项目中安装，换个项目就不能用了

        + \#全局安装
          \#Node.js全局安装的npm包和工具的位置：用户目录\AppData\Roaming\npm\node_modules
          \#一些命令行工具常使用全局安装的方式
          npm install -g webpack  

          > 只要在nodejs环境中的项目都可以使用

      + 其他命令

        + \#更新包（更新到最新版本）
          npm update 包名
        + \#全局更新
          npm update -g 包名
        + \#卸载包
          npm uninstall 包名
        + #全局卸载
          npm uninstall -g 包名  

+ babel

  + babel是转码器，能够将ES6代码转换成ES5代码，一般都是编写ES6代码，转换成ES5代码运行，因为ES6代码的浏览器兼容性很差

  + `npm install --global babel-cli`npm安装babel

  + `babel --version`可以查看babel是否安装成功

  + 转码流程

    + 安装babel

    + 编写es6的js文件

    + 创建babel配置文件.babelrc，编写转码配置【类似于写明待转码文件的规范】

    + `npm install --save-dev babel-preset-es2015`安装es2015转码器

    + 使用命令进行转码【这一步涉及的文件夹必须创建好，文件不用创建，会自动创建】

      ```shell
      # 转码结果写入一个文件
      mkdir dist1
      # --out-file 或 -o 参数指定输出文件，文件名字可以自定义
      babel src/example.js --out-file dist1/compiled.js
      # 或者
      babel src/example.js -o dist1/compiled.js
      # 整个目录转码
      mkdir dist2
      # --out-dir 或 -d 参数指定输出目录,文件名字不会改变
      babel src --out-dir dist2
      # 或者
      babel src -d dist2
      ```

+ 模块化

  + 模块化：后端开发接口时，controller注入service、service注入mapper，后端中类与类的调用就是后端的模块化操作

  + 而前端中的js和js建的调用就是前端的模块化操作

  + 注意：使用es6写法实现的模块化操作，node.js环境中不能直接运行，需要使用babel将es6代码转成es5代码，才能在node.js上进行运行

  + 模块化操作

    + 第一种方式

      + *用common-js即exports和required来导出引入模块*

        ```js
        //1. 用common-js即exports和required来导出引入模块
        //创建js方法
        //定义成员
        const sum = function(a,b){
            return parseInt(a) + parseInt(b)
        }
        const subtract = function(a,b){
            return parseInt(a) - parseInt(b)
        }
        const multiply = function(a,b){
            return parseInt(a) * parseInt(b)
        }
        const divide = function(a,b){
            return parseInt(a) / parseInt(b)
        }
        
        //导出模块中的成员，即设置哪些方法可以被其他js调用,以下是简写，非简写就是在每个方法名前加"方法名:",在需要使用的文件中引入该模块即可使用
        module.exports = {
            sum,
            subtract,
            //multiply,
            divide
        }
        ```

      + 调用

        ```javascript
        //用required指令引入calculate.js文件，就可以调用其中模块化的方法
        //指令格式const cal=required('./文件路径'),cal就像创建一个对象一样可以通过该对象调用文件中的模块化方法
        const cal = require('./calculate')
        
        //调用cal的模块化方法,没有加入模块化的方法就不会被提示出来
        console.log(cal.sum(1,2))
        console.log(cal.divide(10,3))
        console.log(cal.subtract(10,7))
        ```

    + 第二种方式

      + *es6的export和import来导出、导入模块*

        ```javascript
        //ES6模块化规范，使用export和import来导出、导入模块
        export function getList(){
            console.log('获取用户数据列表')
        }
        export function save(){
            console.log('保存用户数据')
        }
        ```

      + 调用

        ```javascript
        //使用import来取出需要的方法，方法间用逗号分隔
        import {getList,save} from "./userApi.js"//js可以省略，但是./不能省，注意可以使用@符号代替.
        
        getList()
        save()
        //注意此时无法在nodejs中通过node命令运行es6的模块化，必须先转换成es5的代码
        ```

      + 将上述模块文件和调用文件全部转成es5再执行

    + 第三种方式

      + es6的export default和import

        ```javascript
        //使用export default把模块中的方法包含进去
        export default{
            getList(){
                console.log('获取用户列表2')
            },
            save(){
                console.log('保存用户数据2')
            }
        }
        ```

      + import像引入对象一样引入模块化方法

        ```javascript
        //import像导入对象一样从模块化文件导入
        import userApi from './userApi2'
        userApi.getList()
        userApi.save()
        ```

        > 这是es6的第二种方式引入模块化的方式，仍然需要使用babel转成es5才能用nodejs运行

+ webpack

  + webpack是一个前端资源加载/打包工具，会根据模块的依赖关系按指定的规则生成静态资源，作用是将多种静态资源js、css、less转换成一个静态文件，即把多个静态资源文件打包成一个文件，减少页面的请求次数

  + `npm install -g webpack webpack-cli`全局安装webpack

  + `webpack -v`安装后查看版本号

  + webpack打包js文件

    + 在webpack_std下创建三个js文件common.js、utils.js和main.js

    ```javascript
    //创建3个js文件，在common和utils中分别定义了info和add方法，在main.js中引入了common和utils，使用webpack打包这三个文件
    const common=require('./common')
    const utils=require('./utils')
    common.info('Hello World!'+utils.add(100,200))
    ```

    + 在webpack即项目根目录下创建配置文件webpack.config.js,在其中写入webpack的配置文件

    > 有新目录的需要创建对应目录

    ```javascript
    const path=require("path")//Node.js的内置模块
    module.exports={
        entry:'./src/main.js',//配置js文件的入口条件
        output:{
            path:path.resolve(__dirname,'./dist'),//指定打包js文件放置目录
            filename:'bundle.js'//输出文件
        }
    }
    ```

    + 执行打包命令

    > 以下两个命令二选一，注意这里的nodejs的版本要高一点，否则会webpack报错，国内的论坛根本找不到解决方案，只能在stackOverflow上找到

    ```shell
    webpack #有黄色警告,原因是没有设置模式为开发者模式，实际上默认生产模式
    webpack --mode=development #没有警告
    #执行后查看bundle.js 里面包含了上面两个js文件的内容并惊醒了代码压缩
    ```

    ```shell
    PS E:\JavaStudy\project\ol_edu\vpc_ol_js\webpack_std> webpack
    #nodejs版本太低报错
    [webpack-cli] TypeError: ["webpack.config",".webpack/webpack.config",".webpack/webpackfile"].flatMap is not a function
        at WebpackCLI.loadConfig (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\lib\webpack-cli.js:1505:118)
        at WebpackCLI.createCompiler (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\lib\webpack-cli.js:1781:33)
        at WebpackCLI.runWebpack (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\lib\webpack-cli.js:1877:31)
        at Command.makeCommand (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\lib\webpack-cli.js:944:32)
        at Command.listener [as _actionHandler] (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\node_modules\commander\lib\command.js:482:17)     
        at actionResult._chainOrCall (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\node_modules\commander\lib\command.js:1283:65)
        at Command._chainOrCall (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\node_modules\commander\lib\command.js:1177:12)
        at Command._parseCommand (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\node_modules\commander\lib\command.js:1283:27)
        at hookResult._chainOrCall (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\node_modules\commander\lib\command.js:1081:27)
        at Command._chainOrCall (C:\Users\Earl\AppData\Roaming\npm\node_modules\webpack-cli\node_modules\commander\lib\command.js:1177:12)
    PS E:\JavaStudy\project\ol_edu\vpc_ol_js\webpack_std> node -v
    v10.24.1
    #更换了nodejs的版本
    PS E:\JavaStudy\project\ol_edu\vpc_ol_js\webpack_std> node -v
    v18.17.1
    #打包成功
    PS E:\JavaStudy\project\ol_edu\vpc_ol_js\webpack_std> webpack
    asset bundle.js 322 bytes [emitted] [minimized] (name: main)
    ./src/main.js 266 bytes [built] [code generated]
    ./src/common.js 138 bytes [built] [code generated]
    ./src/utils.js 46 bytes [built] [code generated]
    
    WARNING in configuration
    The 'mode' option has not been set, webpack will fallback to 'production' for this value.
    Set 'mode' option to 'development' or 'production' to enable defaults for each environment.
    You can also set it to 'none' to disable any default behavior. Learn more: https://webpack.js.org/configuration/mode/
    
    webpack 5.88.2 compiled with 1 warning in 161 ms
    ```

    

    + 创建html文件，引入打包后的js文件，用浏览器查看效果

    ```html
    <script src="./dist/bundle.js"></script>
    ```

  + webpack打包css文件

    + 编写css文件，用required引入css文件

    + webpack本身只能处理javaScript模块，如果要处理其他类型的文件，需要使用模块和资源的转换器loader进行转换，首先需要安装loader插件。css-loader是将css装载到javascript，style-loader可以让javascript认识css

      + `npm install --save-dev style-loader css-loader`

        > 这个好像就下不好一样，但是进package.json一看就是下好的，实际也能用，很费解

    + 修改webpack.config.js

      ```javascript
      const path=require("path")//Node.js的内置模块
      module.exports={
          entry:'./src/main.js',//配置js文件的入口条件
          output:{
              path:path.resolve(__dirname,'./dist'),//指定打包js文件放置目录
              filename:'bundle.js'//输出文件
          },
          module:{
              rules:[
                  {
                      test:/\.css$/,//打包规则应用到以css结尾的文件上
                      use:['style-loader','css-loader']
                  }
              ]
          }
      }
      ```

    + 在main.js中引入css文件`require('./style.css')`，重新打包并打开html页面查看效果

    

## 搭建项目前端页面环境

> 选取模板vue-admin-template框架进行前端页面环境搭建

1. 模板安装

   + 使用171kb的小压缩包作为前端框架，将其解压放到工作区中
   + `npm init`初始化该项目,`npm install`安装所有依赖，由于nodejs版本过高也会导致依赖下载失败，这里又改成了v14，overStackflow上说14的版本兼容性更好
   + `npm run dev`启动项目即可通过浏览器访问该项目，访问地址:http://localhost:9528

2. vue-admin-template前端框架环境说明

   > 该模板主要基于vue和element-ui两种技术实现的，这两个文件都能在node_module中找到

   + index.html和main.js是前端框架入口，在index.html中有一个id为app的div，在main.js中new了一个vue对象，其中的el就是index.html中的app【@一般是指src的根目录即就是src】

   + build目录放的是项目进行构建和进行编译的脚本文件，就像java编译class文件的一些工具文件

   + config目录，里面放着项目最基本的设置，如index.js,里面就放着port:9528,host:'localhost',都可以进行更改；index.js中有个useEslint:true,把这个值改成false，ESLint是vscode的插件，可以帮助自动整理代码格式并做代码检查，不建议装，检查太严格，多了一个空格或者换行都算错，这不好；另外两个文件分别对应开发环境或者生产环境去分别执行，启动时`npm run dev`就是开发环境启动，文件中的BASE_API规定了浏览器要访问接口的默认位置，后面要改成本地8001服务器端口

   + node_modules是下载好的依赖

   + src主要的代码都在src中

     > 重要的有api、router和views，改就主要改访问地址和这三个地方，其他地方基本不怎么动；开发流程是写接口，写路由、在页面中调用方法并用element-ui进行显示

     + api中定义了需要调用的方法
     + assets目录主要放一些静态资源，js文件，css文件，一些项目中的图片
     + components主要放一些当前框架没有的额外的组件
     + icons放的是项目中用的各种图标
     + router表示项目中用到的路由部分
     + store中主要放的是项目中用到的脚本文件，没啥用
     + styles中放的是一些样式文件
     + utils中放的是项目中用到的一些工具类，如权限、请求等
     + views目录中放的是项目中具体的页面，这里面用的页面都是vue的后缀名

   + static没啥用，主要都是一些不咋使用的静态资源

3. 解决登录问题

   + 登录默认地址是https://easy-mock.com/mock/5950a2419adc231f356a6636/vue-admin/user/login

     + 由config/dev.ens.js的BASE_API+src/api/login.js中的login方法的url两个拼接而成，login方法的request来自src/utils/request文件，该文件又包含axios文件，在这个文件中对ajax请求进行了封装，axios文件中的service常量规定了baseURL为配置文件dev.env的BASE_API
   
       ```javascript
       import axios from 'axios'
       import { Message, MessageBox } from 'element-ui'
       import store from '../store'
       import { getToken } from '@/utils/auth'
       
       // 创建axios实例
       const service = axios.create({
         baseURL: process.env.BASE_API, // api 的 base_url
         timeout: 5000 // 请求超时时间
       })
       
       // request拦截器
       service.interceptors.request.use(
         config => {
           if (store.getters.token) {
             config.headers['X-Token'] = getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
           }
           return config
         },
         error => {
           // Do something with request error
           console.log(error) // for debug
           Promise.reject(error)
         }
       )
       
       // response 拦截器
       service.interceptors.response.use(
         response => {
           /**
            * code为非20000是抛错 可结合自己业务进行修改
            */
           const res = response.data//得到服务器响应数据
           if (res.code !== 20000) {//如果值不是20000，就报错并输出失败信息
             Message({
               message: res.message,
               type: 'error',
               duration: 5 * 1000
             })
             // 50008:非法的token; 50012:其他客户端登录了;  50014:Token 过期了;
             if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
               MessageBox.confirm(
                 '你已被登出，可以取消继续留在该页面，或者重新登录',
                 '确定登出',
                 {
                   confirmButtonText: '重新登录',
                   cancelButtonText: '取消',
                   type: 'warning'
                 }
               ).then(() => {
                 store.dispatch('FedLogOut').then(() => {
                   location.reload() // 为了重新实例化vue-router对象 避免bug
                 })
               })
             }
             return Promise.reject('error')
           } else {
             return response.data//如果响应数据是20000，则直接将返回数据返回
           }
         },
         error => {
           console.log('err' + error) // for debug
           Message({
             message: error.message,
             type: 'error',
             duration: 5 * 1000
           })
           return Promise.reject(error)
         }
       )
       
       export default service
       ```
   
       
   
     + 将BASE_API路径设置为本机服务器所在的地址http://localhost:8001
   
     + 用户登录时调用啦login和info两个方法。login方法负责登录操作，info方法登录后获取用户信息
       + 其中login方法返回token值，info方法返回roles、name、avatar[头像]，在服务器中写出对应的接口，注意前端要求服务端返回的数据一般都有commit方法，第二个参数就是要返回的值【token实际是用户名】
   
   + 后端登录接口编写
   
     + 在前端对响应的状态码是否等于两万进行了判断，如果不等于20000就会抛错，所以后端的状态码不要随便瞎定义
   
     + login方法返回token，info方法返回roles、name、avatar
   
       ```java
       /**
        * @author Earl
        * @version 1.0.0
        * @描述 用于用户登录的接口入口
        * @创建日期 2023/08/30
        * @since 1.0.0
        */
       @RestController
       @RequestMapping("/eduservice/user")
       public class UserLoginController {
           /**
            * @return {@link ResponseData }
            * @描述 用户登录操作服务器端方法
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/30
            * @since 1.0.0
            */
           @PostMapping("login")
           public ResponseData userLogin(){
               return ResponseData.responseCall().data("token","admin");//admin是用户名，后面涉及查表再改
           }
       
           /**
            * @return {@link ResponseData }
            * @描述 获取用户信息
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/30
            * @since 1.0.0
            */
           @GetMapping("info")
           public ResponseData getUserInfo(){
               HashMap<String, Object> userInfo = new HashMap<>();
               userInfo.put("roles","[admin]");//这是啥意思
               userInfo.put("name","admin");
               userInfo.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");//这个是拷贝的课程资料
               return ResponseData.responseCall().data(userInfo);
           }
       }
       ```
   
   + 根据后端接口信息更改前端请求信息
   
     + 在src/api/login.js中的request对象中修改请求路径和请求方式与登录接口对应
   
   + 解决跨域问题，由于前端页面的端口号和服务器的端口号不是同一个，所以请求存在跨域问题，提示Access-Control-Allow-Origin，
   
     + 通过在后端接口的controller上添加@CrossOrigin注解来允许跨域访问
     + 也可以使用nginx网关解决【后面说】
   
   + 此时登录即可进入
   
     + 每个相同的请求会发送两次，是浏览器的机制，第一次是测试请求是否能成功连通接口【预检】，请求方式是options，并不会返回数据；第二次是真正的访问服务器获取响应数据

## 实现讲师列表功能

> 二次开发，一般都是在现有项目基础上开发新的功能

+ 第一步，添加路由，main.js中有个import router from './router',router会被加载到下方的vue对象中，对应src/router/下的index.js【注意模块化的对象可以引入文件夹】，路由就是在src/router/index.js中添加，路由在constantRouterMap中以数组的形式存在，以下是页面中Example菜单及其子菜单table和tree的示例，添加路由复制一份改就行

  ```javascript
  {
      //这是一层目录
  path: '/example',
  component: Layout,//layout是一种布局，表示路由的采用的一种表现形式
  redirect: '/example/table',//访问/example会重定向到example中的path为table的路由
  name: 'Example',//菜单名
  meta: { title: 'Example', icon: 'example' },//上面名字改下标的title也要一起改，icon就是这个标题用的图标
  children: [//这里面表示二层目录
    {
      path: 'table',
      name: 'Table',//这个name属性是菜单名
      component: () => import('@/views/table/index'),//这个component属性就是点击路由跳转的页面，import是页面的位置，即路由对应页面的url，框架不认识./只认识@/，就是src；本行代码的意思是table路由对应的页面在src/views/table/index.vue
      meta: { title: 'Table', icon: 'table' }
    },
    {
      path: 'tree',
      name: 'Tree',
      component: () => import('@/views/tree/index'),
      meta: { title: 'Tree', icon: 'tree' }
    }
  ]
  },
  ```

+ 模仿前端页面开发，增加修改路由，在views中创建对应的vue页面

+ 开发vue页面，页面顶上的template标签是element-ui的部分，引入了api文件对应后端接口的方法，自己写也需要在api中创建文件定义方法建立与后端接口的联系并在vue页面中进行引入，在vue页面中的后半部分就是vue的结构，export default{这里面是原来要写在vue中的内容，有filters、data、created、methods}，在methods中定义方法，在created中调用，在data中做初始化，最后使用上述的element-ui对数据进行展示

+ 讲师列表开发步骤

  + 第一步：创建讲师管理路由
  
    ```javascript
    {
        path: '/teacher',
        component: Layout,
        redirect: '/teacher/list',
        name: '讲师管理',
        meta: { title: '讲师管理', icon: 'example' },
        children: [
          {
            path: 'list',
            name: '讲师列表',
            component: () => import('@/views/edu/teacher/list'),//.vue可以省略不写
            meta: { title: '讲师列表', icon: 'table' }
          },
          {
            path: 'save',
            name: '添加讲师',
            component: () => import('@/views/edu/teacher/save'),
            meta: { title: '添加讲师', icon: 'tree' }
          }
        ]
      },
    ```
  
  + 第二步：创建对应的vue页面list.vue和save.vue，页面的<template><div class="app-container">部分进行了封装，头两个标签必须为这个
  
  + 第三步：在api文件创建teacher.js定义出对应接口路径的方法，在request.js中对axios进行了封装，超过5000ms没有详情就报错，方法的写法直接照抄已经有的文件
  
    ```javascript
    import request from '@/utils/request'
    
    export default {
        //讲师列表，讲师条件分页查询，current为当前页，limit为每页记录数，teacherQuery为条件对象
        findAllTeacherPaging(current,limit,teacherQuery){
            return request({
                //url的两种写法，推荐第二种
                //url: '/eduservice/teacher/pageTeacher/'+current+'/'+limit,
                url: `/eduservice/teacher/pageFactorTeacher/${current}/${limit}`,//带条件查询和不带条件查询一定要区分清楚，两者请求方式都不同，即使加了跨域请求注解还是会报错没有跨域请求权限
                method: 'post',
                //teacherQuery是查询条件对象，后端使用@RequestBody注解获取数据需要前端传入json数据，data属性对应对象会自动将对象转成json格式传入接口
                data: teacherQuery
            })
        }
    }
    ```
  
  + 第四步：在views目录下编写讲师列表页面
  
    > 前端的东西要想读懂要专门去学vue和element-ui
  
    ```html
    <template>
        <div class="app-container">
                讲师列表
            <!-- 表格 -->
            <!--:data得到对应变量名的数据，
                v-loading="listLoading"和element-loading-text="数据加载中"在数据加载时会显示加载中信息
                border、fit、highlight-current-row都是样式
            -->
            <el-table
                :data="list"
                border
                fit
                highlight-current-row>
            
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
                    <router-link :to="'/edu/teacher/edit/'+scope.row.id">
                        <el-button type="primary" size="mini" icon="el-icon-edit">修改</el-button>
                    </router-link>
                    <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeDataById(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
            </el-table>
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
                //定义请求讲师列表的方法
                getTeacherList(){
                    //按照axios的要求是axios.post("").then().catch()//由于request已经将这个过程进行了封装，
                    //teacher.js已经进行了处理，这里只需要调用teacher.js中的对应方法即可,注意request方法仅相当于axios.post(""),
                    //后面的.then().catch()还是需要自己在这个方法中进行处理
                    teacher.findAllTeacherPaging(this.page,this.limit,this.teacherQuery)
                        .then(response=>{
                            //response是接口返回的数据
                            //console.log(response)
                            this.list=response.data.rows
                            this.total=response.data.total
                            console.log(this.list)
                            console.log(this.total)
                        })//请求成功处理方法
                        .catch(error=>{
                            console.log(error)
                        })//请求失败处理方法
                }
            }
        }
    </script>
    ```
  
  + 给讲师列表加上分页条
  
    > 直接从element-ui上找一个好看的分页条加上，实际上还需要改，没学直接抄课件的
  
    ```html
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
    ```
  
    + 由于element-ui封装了调用getTeacherList方法自动传参当前页，但是data中的page没有更新，所以还需要再getTeacherList方法中添加page更新的代码，那个形参page=1没看懂，学vue的时候注意一下
  
  + 给讲师列表添加条件查询功能
  
    + element-ui封装的，后面自己学了改进一下时间条件的选择效果
  
      ```html
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
          <el-button type="default" @click="resetData()">清空</el-button>
      </el-form>
      ```
  
    + 实现清空按钮的功能
  
      + 请求所有条件，调用一次查询所有的方法
  
      ```javascript
      resetData(){//清空条件查询框并查询所有一次
          this.teacherQuery={}
          this.getTeacherList()
      }
      ```
  
    + 实现讲师删除功能
  
      + 删除按钮绑定的是removeDataById(scope.row.id)方法，id也是传过来封装在对象中的，只是没有展示，但是仍然能使用scope.row.id获取
  
      + 对应接口的逻辑删除Api方法
  
      + 在页面中调用逻辑删除api方法实现删除，注意还要有友好性提示，确认删除弹框和删除成功弹窗【这俩在element-ui中是一体的】，删除后还要在进行一次讲师查询
  
        ```javascript
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
        ```
  
        > 本身methods中的方法的.catch就可以不写，因为在request.js中已经对错误信息进行了默认封装，不写也行，建议不写，因为有些浏览器会执行两次catch【框架一次，自己写的一次】会发生错误
  
    + 实现添加讲师功能
  
      + 编写保存页面【element-ui,然后自己改，没学直接抄】
  
      + 创建对应接口的Api方法
  
        ```javascript
        //添加讲师
        addTeacher(teacher){
            return request({
                url: `/eduservice/teacher/addTeacher`,
                method: 'post',
                data: teacher
            })
        }
        ```
  
      + 引入Api方法，编写保存数据的方法
  
        【该页面显示查询列表调用了路由跳转】
  
        ```html
        <template>
            <div class="app-container">
                添加讲师
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
                    <el-form-item>
                        <el-button :disabled="saveBtnDisabled" type="primary" @click="saveOrUpdate">保存</el-button>
                    </el-form-item>
                </el-form>
        </div>
        </template>
            </div>
        </template>
        
        <script>
            import teacher from '@/api/edu/teacher'
            export default {
                data() {
                    return {
                        teacher: {//这个要和后端entity实体对应才能实现自动封装
                            name: '',
                            sort: 0,
                            level: 1,
                            career: '',
                            intro: '',
                            avatar: ''
                        },
                        saveBtnDisabled: false // 保存按钮是否禁用,
                    }
                },
                methods: {
                    saveOrUpdate() {
                        this.saveBtnDisabled = true// 一次保存后保存按钮禁用避免多次返回提交
                        this.saveTeacher()
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
                    }
                }
            }
        </script>
        ```
  
    + 讲师条件分页查询带排序
  
      + 后端代码【在条件类中添加排序的条件,根据讲师添加时间降序】
  
        ```java
        queryWrapper.orderByDesc("gmtCreate");
        ```
  
    + 讲师修改功能
  
      > 整体逻辑，根据隐藏路由id调用查询接口查询出讲师信息绑定到讲师对象在页面进行回显，修改结束后点击保存按钮由讲师对象是否含有讲师id调用保存或者修改接口修改讲师数据
      
      + 点击修改按钮进入添加页面进行数据回显，根据讲师id查询数据进行显示
      
      + 通过隐藏路由跳转到添加讲师页面，请求参数edit/:id相当于路由中的占位符，里面要传参
      
      + 修改讲师列表页面修改按钮的链接路径
      
        > 逻辑是超链接被路由分配静态页面，静态页面执行ajax请求在页面几个不同时机的方法中向接口要参数并对参数进行展示
      
      + 定义查询讲师的接口，在添加讲师页面定义出查询讲师并将查询结果赋值给显示对象teacher
      
      + 用路径是否有id参数判断是否需要执行查询方法，因为添加讲师不需要对讲师进行查询，vue中this.$route.params.id表示获取路由中的参数，this.$route.params是获取路由中的参数
      
      + 修改的实现，定义修改接口的ajax请求，定义修改讲师的方法，修改后和添加方法一致，显示提示信息并回到讲师列表页面，保存按钮既要能调用保存接口又要能调用修改接口，根据teacher中是否有id来进行判断，因为添加讲师由系统生成id，而修改讲师会由数据库传参id
      
      + 现存问题
      
        + 点击修改但没提交，再次点击添加讲师页面，还是显示修改讲师的信息，原因是讲师对象的信息没有清空,解决方法是做添加讲师页面数据渲染前，teacher数据先进行一次清空【vue的导航切换问题：多个路由渲染同一个组件，组件会重用，组件的生命周期钩子不会再被调用，使得组件的一些数据无法根据路径发生数据的更新，多次路由跳转同一个页面，页面的created方法只会在第一次路由跳转执行，后面不会执行，但是我这儿没有问题，出现这个问题再用vue监听器进行解决，监听器的作用是路由变化时去执行一段代码】
      
          > 监听器代码
      
          ```javascript
          created(){
              this.init()
          },
          watch: {
              $route(to,from){//vue监听路由变化方式，路由发生变化，其中的方法就会执行
                  this.init()
              }
          },
          methods: {
              init(){
                  if(this.$route.params && this.$route.params.id){
                      const id=this.$route.params.id
                      this.getInfoById(id)
                  }else{
                      this.teacher={}
                  }
              },
          }
          ```
      
          
      
        + 点击保存按钮，保存按钮被禁用，但是服务器响应出了问题，页面会一直卡在保存页面且无法再次保存，填写的数据会直接消失，用户体验很不好，如何解决?



## 添加讲师头像上传功能

1. 使用阿里云oss存储服务

   + 网站阿里云，冲几毛钱，搜索阿里云oss，产品分类的云计算中也可以找到，点击管理控制台，创建并管理bucket，勾选低频访问和公共读，文件管理中能看到文件信息，能手动上传文件，也可以用java代码操作阿里云oss

   + Java代码操作阿里云oss

     + 创建阿里云oss许可证【阿里云颁发的id和密钥：bucket管理的access key】

     + 创建service_oss模块，引入阿里云oss依赖aliyun-sdk-oss和时间工具依赖joda-time

     + 对阿里云oss服务在application.properties中进行配置

     + 创建启动类并排除数据源自动加载配置功能

     + 创建常量类读取配置文件内容

     + 创建controller和service，controller去调用service中的文件上传方法，service中使用阿里云简单文件上传模板编写文件上传方法

       ```java
       @Override
       public String uploadFileAvatar(MultipartFile file) {
           // 填写阿里云四大oss信息
           String endpoint = ConstantProperties.END_POINT;
           String bucketName = ConstantProperties.BUCKET_NAME;
           String accessKeyId = ConstantProperties.KEY_ID;
           String accessKeySecret=ConstantProperties.ACCESS_KEY_SECRET;
       
       
           // 创建OSSClient实例。
           OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);
       
           try {
               //上传文件流
               InputStream inputStream = file.getInputStream();
               //获取上传文件名称
               String originalFilename = file.getOriginalFilename();
               //调用oss方法实现上传（需要拼参数bucket名称、上传到oss的文件路径和名称、上传文件输入流）
               ossClient.putObject(bucketName, originalFilename, inputStream);
               //上传后需要把上传到阿里云oss的路径手动拼接出来存放在数据库,访问地址的规则是"https://"+bucketName+"."+endpoint+"/"+originalFilename
               String avatarUrl="https://"+bucketName+"."+endpoint+"/"+originalFilename;
               return avatarUrl;
           } catch (OSSException oe) {
               System.out.println("Caught an OSSException, which means your request made it to OSS, "
                       + "but was rejected with an error response for some reason.");
               System.out.println("Error Message:" + oe.getErrorMessage());
               System.out.println("Error Code:" + oe.getErrorCode());
               System.out.println("Request ID:" + oe.getRequestId());
               System.out.println("Host ID:" + oe.getHostId());
               return null;
           } catch (ClientException ce) {
               System.out.println("Caught an ClientException, which means the client encountered "
                       + "a serious internal problem while trying to communicate with OSS, "
                       + "such as not being able to access the network.");
               System.out.println("Error Message:" + ce.getMessage());
               return null;
           } catch (IOException e) {
               e.printStackTrace();
               return null;
           } finally {
               // 关闭OSSClient
               if (ossClient != null) {
                   ossClient.shutdown();
               }
           }
       }
       ```

     + swagger测试
     
       + 两个问题：
         + 问题一，同名文件会造成阿里云oss同名文件内容覆盖，解决办法是获取文件原始名称，给每个文件拼接一个uuid避免同名
         + 问题二，对文件进行分类管理，根据年月日期分类或者根据用户名进行分类，办法是在文件名中用XX/XX/1.jpg表示文件目录【这里使用开始和oss一起引入的joda的DateTime的toString方法来将系统时间格式改成带斜杠的时间格式】
     
   + nginx反向代理服务器
   
     > 先用nginx做请求转发，后续用网关代替
   
     + nginx的功能
   
       + 请求转发：浏览器发送一个请求给nginx，nginx根据路径匹配把请求发送给另一个具体的服务器，nginx单独占用一个端口
   
       + 负载均衡：负载均衡将请求平均分摊到多个服务器中，负载均衡的策略有轮询【服务器按顺序一个一个依次对请求进行处理】、请求时间【】、权重、哈希等
   
       + 动静分离
   
     + 前端页面的端口号BASE_API为8001、实际图片上传是8002端口、访问的后台管理系统是8001端口，如何使用nginx实现图片上传访问8002端口
   
       + 让前端页面访问nginx，使用nginx分配访问的服务器
   
       + nginx启动，直接解压windows安装包，在解压目录中通过DOS命令窗口使用nginx.exe启动nginx，注意关闭CMD窗口nginx是不会停止运行的，改了配置nginx需要重启，此时不能通过关闭cmd窗口来关闭DOS窗口，nginx停止命令：nginx.exe -s stop；也可以通过nginx的nginx.exe -s reload直接进行重启
   
       + 在nginx解压包下的confg目录下的nginx.conf文件中对nginx进行配置
   
         + 配置nginx实现请求转发的功能
   
           + 配置文件中的worker相关是用作多路复用的
   
           + nginx的默认端口是80端口、最好改成81，避免不必要的端口冲突
   
           + 也可以设置nginx端口为9001，在nginx中配置当请求路径含eduoss就进入8002端口，地址中有eduservice就进入8001端口，添加配置的方法是在server中添加，listen表示nginx对外的监听端口设置为9001，server_name是主机名称，location是匹配路径，后面跟~ /请求路径，proxy_pass是匹配转发服务器的地址【这个81和9001有啥区别？】,改完以后重启nginx
   
             > 注意用作区分的eduservice和eduoss在两中请求路径中不能相互包含
   
             ```java
             server {
                 listen       9001;
                 server_name  localhost;
             
                 location ~/eduservice/ {
                     proxy_pass http://localhost:8001;
                 }
             location ~/eduoss/ {
                     proxy_pass http://localhost:8002;
                 }
             }
             ```
   
           + 在前端dev.env.js中将前端请求端口改成本机的nginx监听端口9001，注意启动前端页面前一定要启动nginx
   
     + 在添加讲师页面提供讲师头像上传功能
   
       + 使用element-ui创建头像上传组件，为了效果更美观，自己找一个vue-element-admin-master框架中的功能，在体积更大的框架压缩包中找出ImageCropper组件和PanThumb组件并复制到当前的前端文件夹的component目录下
       
       + 添加讲师页面使用这两个组件，用import进行导入，并在export default中的components对两个组件进行声明
       
       + 使用element-ui导入头像上传组件【具体不懂，学element-ui再说，还对涉及变量进行了初始化，对close方法和cropSuccess方法进行了重写】
       
         ```html
         saveBtnDisabled: false, // 保存按钮是否禁用,
         imagecropperShow: false, //上传弹框组件是否显示
         imagecropperKey:0,//上传组件的key值
         BASE_API: ProcessingInstruction.env.BASE_API
         ```
       
         ```html
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
                 :url="BASE_API+'/admin/oss/file/upload'"
                 field="file"
                 @close="close"
             @crop-upload-success="cropSuccess"/>
         </el-form-item>
         ```
       
         ```html
         close(){//关闭头像上传弹框的方法,点击上传头像的叉号就会调用close方法,即关闭头像上传弹框
             this.imagecropperShow=false
         },
         cropSuccess(data){//头像上传成功的方法，头像点击保存成功就会调用cropSuccess方法，注意在点击上传时就已经调用了上传接口方法，返回的结果会自动封装到这个方法的参数中
             this.imagecropperShow=false//关闭弹窗
             this.teacher.avatar=data.url
         },
         ```
       
       + 修改上传接口的方法【把请求发送给对应的后端接口，后端接口把图片上传到阿里云oss，并把图片访问地址存入数据库】，注意前端框架会自动把文件名基础名改成file.png,以防止文件名出现中文的情况，不上传头像可以把头像设置成默认的头像，即图像的地址给前端的avatar属性，有修改再变更，注意这个默认avatar如何设置成可以在添加头像页面显示的头像
       
       + 头像上传的bug，第一次上传成功后再次点击头像上传，第一次头像上传成功后再次点击头像上传会显示上传成功无法再次上传，解决办法是把上传组件的imagecropperKey自动加1，原因不清楚，有机会学习该框架再说
       
         + 还有个问题，头像上传后即使不注册讲师，阿里云上仍然会保存数据，如何让保存教师记录的时候再对头像数据进行云存储



## 添加课程分类功能

1. 使用EasyExcel读取excel内容添加数据，把课程分类编辑在excel表格中，在表格中数据通过技术手段如EasyExcel读取到数据库表格中

2. EasyExcel是JAVA解析Excel表格的工具，由阿里巴巴提供；早期处理excel由apache的工具poi和jxl，都存在内存消耗严重的问题，EasyExcel的原理是从磁盘上一行行读取数据，逐行进行解析，而其他方式大多都是一次性读取数据加载到内存中，效率很低，以下对Easyexcel进行演示

   + eaxyExcel需要引入对应的依赖，同时由于EasyExcel对poi进行了封装，同时还需要引入poi的依赖，注意EasyExcel和poi的版本有对应，版本不正确可能会出现问题，2.1.1版本的EasyExcel对应poi的3.1.7的依赖

   + 写的操作比较简单，读的操作比较繁杂

     + 建立与表格对应的实体类，属性要对应表的不同列，加上@Data注解，注意对属性需要添加@ExcelProperty注解并设置属性与表头名称的对应关系
     + 创建对excel表格的写操作
       + 第一步确定并定义对应表格文件位置的字符串,注意这个文件会自动创建
       + 第二步调用easyexcel的write方法实现写操作，传参参数文件的路径名称，第二个是对应表格实体类的class文件
       + sheet中的参数是对应表格的sheet名
       + dowrite方法传入一个list集合，list集合中每个元素的对象是对应表格的实体类

     ```java
     @Data
     @AllArgsConstructor
     public class DemoExcelData {
         //设置实体类与表格表头信息的对应关系，没有表创建表格时会自动写成表头
         @ExcelProperty("学生编号")
         private Integer studentNo;
         @ExcelProperty("学生姓名")
         private String studentName;
     }
     ```

     ```java
     public class TestEasyExcelWrite {
         public static void main(String[] args) {
             //定义写入文件夹的地址，注意没有对应文件会自动创建
             String fileName="E:\\JavaStudy\\project\\ol_edu\\student.xlsx";
             //调用EasyExcel的write方法实现写操作,这种方式会自动关流
             EasyExcel.write(fileName,DemoExcelData.class).sheet("学生列表").doWrite(getData());
         }
     
         private static List<DemoExcelData> getData() {
             List<DemoExcelData> excelData = new ArrayList<>();
             for (int i = 0; i < 10; i++) {
                 excelData.add(new DemoExcelData(i, "lucy" + i));
             }
             return excelData;
         }
     }
     ```

   + 读操作需要用index对实体类的属性用@ExcelProperty属性指明对应的序号【可以直接加在读取数据类的同一个注解中，属性名是index】，创建实现AnalysisEventListener接口的监听器分别重写读取表头的invokeHeadMap方法，读取表格内容的invoke方法【读取数据会自动封装到data中】，读取完毕后的doAfterAllAnalysed方法【目前用不上】，然后对监听器进行调用

     【注意必须对实体类创建无参数构造方法，否则读取表格内容会报异常】

     ```java
     @Data
     @AllArgsConstructor
     @NoArgsConstructor
     public class DemoExcelData {
         //设置实体类与表格表头信息的对应关系，没有表创建表格时会自动写成表头
         @ExcelProperty(value = "学生编号",index = 0)
         private Integer studentNo;
         @ExcelProperty(value = "学生姓名",index=1)
         private String studentName;
     }
     ```

     ```java
     public class ExcelListener extends AnalysisEventListener<DemoExcelData> {
         @Override
         public void invoke(DemoExcelData demoExcelData, AnalysisContext analysisContext) {
             System.out.println("表格内容:"+demoExcelData);//表格每行被自动封装到对象demoExcelData中
         }
     
         @Override
         public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
             System.out.println("表头内容:"+headMap);//表头内容一行记录会以index,表头内容的形式封装到headMap中
         }
     
         @Override
         public void doAfterAllAnalysed(AnalysisContext analysisContext) {
         }
     }
     ```

     ```java
     public static void main(String[] args) {
         //定义写入文件夹的地址，注意没有对应文件会自动创建
         //String fileName="E:\\JavaStudy\\project\\ol_edu\\student.xlsx";
         //调用EasyExcel的write方法实现写操作,这种方式会自动关流
         //EasyExcel.write(fileName,DemoExcelData.class).sheet("学生列表").doWrite(getData());
     
         //实现excel的读操作
         String fileName="E:\\JavaStudy\\project\\ol_edu\\student.xlsx";
         EasyExcel.read(fileName,DemoExcelData.class,new ExcelListener()).sheet().doRead();
     }
     ```

     

3. 使用Easyexcel实现从上传的excel表格中导入一级目录和二级目录，并判断数据库中是否存在对应的课程目录，由于这里的数据量比较小，且easyexcel每次只处理一条记录，优化可以考虑把查询课程添加到缓存中或者使用ThreadLocal实现数据库连接池【这个不太会，因为本身数据源是使用的mp】

   【创建edu_subject表，使用mp的代码生成器生成框架结构】

   【写controller中文件上传解析的方法】

   ```java
   @RestController
   @RequestMapping("/eduservice/edu-subject")
   @CrossOrigin
   @Api(description = "读取上传文件")
   public class EduSubjectController {
   
       @Autowired
       private EduSubjectService eduSubjectService;//controller注入eduSubjectService，这个对象一直传入EduSubjectServiceImpl，疑问spring的IoC组件能否通过this直接传入
   
       //添加课程分类，获取上传过来的表格文件，把文件内容读取出来
       @PostMapping("addSubject")
       @ApiOperation("读取上传课程分类文件")
       public ResponseData addSubject(MultipartFile file){
           //调用eduSubjectServiceImpl中的saveSubject方法来读取表格内容并存入数据库
           eduSubjectService.saveSubject(file,eduSubjectService);
           return ResponseData.responseCall();
       }
   
   }
   ```

   【写eduSubjectServiceImpl中对应的解析方法】

   ```java
   @Override
   public void saveSubject(MultipartFile file,EduSubjectService eduSubjectService) {
       try {
           InputStream inputStream = file.getInputStream();
           EasyExcel.read(inputStream, SubjectData.class,new SubjectExcelListener(eduSubjectService)).sheet().doRead();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
   ```

   【写对应表格的实体类】

   ```java
   @Data
   public class SubjectData {
   
       @ExcelProperty(index=0)
       private String firstSubjectName;
   
       @ExcelProperty(index=1)
       private String secondSubjectName;
   }
   ```

   【写对应读取数据对数据判断是否重复以及存储到数据库的监听器】

   ```java
   /**
    * @param subjectData
    * @param analysisContext
    * @描述 读取Excel中的内容，一行一行进行读取，注意如果一级目录存在相同内容需要判断不能重复存入数据库，如果读取的数据为null，
    * 表名数据库中已经没有数据了，这儿有问题，始终都会有读到最后没有数据的情况，或者中间某行数据没有记录的情况，这儿直接抛异常没问题吗？
    * 讲的不清楚，EasyExcel以后自己学了再说，包括返回对象为null的情况对应表格的何种情况
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/09/10
    * @since 1.0.0
    */
   @Override
   public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
       if (subjectData==null){
           //这里一定要搞清楚subjectData为null到底是某行数据为null还是表格压根就没有数据
           throw new CustomException(20001,"文件没有数据");
       }
       //一级数据重复的现象很普遍，需要判断一级数据不能重复添加
       EduSubject firstSubject = exitFirstSubject(subjectData.getFirstSubjectName());
       if (firstSubject==null){//如果一级分类不存在则存入数据库
           firstSubject = new EduSubject();
           firstSubject.setParentId("0");
           firstSubject.setTitle(subjectData.getFirstSubjectName());
           subjectService.save(firstSubject);
       }
       String pid = firstSubject.getId();//不管一级分类有没有id值都会保存一级分类的id值
       if (exitSecondSubject(subjectData.getSecondSubjectName(),pid)==null){
           EduSubject secondEduSubject = new EduSubject();
           secondEduSubject.setParentId(pid);
           secondEduSubject.setTitle(subjectData.getSecondSubjectName());
           subjectService.save(secondEduSubject);
       }
   
   }
   
   /**
    * @param
    * @param name
    * @return {@link EduSubject }
    * @描述 判断一级分类不能重复添加，这里每行都要调用很浪费资源，不如读一次以后直接把title字段加入缓存
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/09/10
    * @since 1.0.0
    */
   private EduSubject exitFirstSubject(String name){
       QueryWrapper<EduSubject> eduSubjectQueryWrapper = new QueryWrapper<>();
       eduSubjectQueryWrapper.eq("title",name).eq("parent_id","0");
       EduSubject subject = subjectService.getOne(eduSubjectQueryWrapper);
       return subject;
   }
   
   /**
    * @param name
    * @param pid
    * @return {@link EduSubject }
    * @描述  判断二级分类不能重复添加，这里每行都要调用很浪费资源，不如读一次以后直接把title字段加入缓存
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/09/10
    * @since 1.0.0
    */
   private EduSubject exitSecondSubject(String name,String pid){
       QueryWrapper<EduSubject> eduSubjectQueryWrapper = new QueryWrapper<>();
       eduSubjectQueryWrapper.eq("title",name).eq("parent_id",pid);
       EduSubject subject = subjectService.getOne(eduSubjectQueryWrapper);
       return subject;
   }
   ```

   



## 课程分类功能

1. 树形结构显示
   + 用表来存储数据库结构，一级分类的pid为0，二级分类的记录的pid为一级分类的id，三级分类的pid为二级分类的id，注意一级分类的pid是人为设置的，二三级的pid是由mp自动生成的
   + 创建数据库表edu_subject，使用mp的代码生成器生成框架的基本结构，在controller中添加上传文件读取表格内容存储数据库的saveSubject方法，创建对应表格的实体类，在service中编写对应的Easyexcel读取表格的方法，编写监听器实现具体的数据处理逻辑，包括判断一级分类、二级分类是否是否在数据库中重复，以及使用传入的学科service类存入数据库的操作

2. 课程分类的前端实现

   + 在index.js中完成路由对应页面的设置，在views中添加subject目录，该目录下创建list.vue和save.vue,把路由指向对应的页面

     ```java
     {
         path: '/subject',
         component: Layout,
         redirect: '/subject/list',
         name: '课程管理',
         meta: { title: '课程管理', icon: 'example' },
         children: [
           {
             path: 'list',
             name: '课程列表',
             component: () => import('@/views/edu/subject/list'),
             meta: { title: '课程列表', icon: 'table' }
           },
           {
             path: 'save',
             name: '添加课程分类',
             component: () => import('@/views/edu/subject/save'),
             meta: { title: '添加课程分类', icon: 'tree' }
           }
         ]
       },
     ```

   + 对课程列表页面和添加课程分类页面进行实现

     + 使用element-ui组件实现页面上传效果，在script的methods中对点击按钮上传文件到接口的submitUpload方法，上传成功的fileUploadSuccess方法，上传失败的fileUploadError方法进行实现；并对组件涉及的变量进行定义初始化，注意文件上传一般都不是ajax提交，一般都是普通提交

       > 注意路由跳转的代码this.$router.push({path:'/subject/list'})路径借鉴路由中index.js中的写法，/subject是大路由，/list是对应的小路由

     ```html
     <template>
         <div class="app-container">
             <el-form label-width="120px">
                 <el-form-item label="信息描述">
                     <el-tag type="info">excel模版说明</el-tag>
                     <el-tag>
                         <i class="el-icon-download"/>
                         <a :href="OSS_PATH +'/excel/%E8%AF%BE%E7%A8%8B%E5%88%86%E7%B1%BB%E5%88%97%E8%A1%A8%E6%A8%A1%E6%9D%BF.xls'">点击下载模版</a>
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
     ```

   + 课程分类树形列表显示功能

     + 前端页面：直接借用模板的树形列表代码并分析，主要两个功能，一个是对课程信息进行检索的功能，一个是自动对data2数据遍历以树形结构显示的功能

       ```html
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
                   :data="data2"
                   :props="defaultProps"
                   :filter-node-method="filterNode"
                   class="filter-tree"
                   default-expand-all
               />
           </div>
       </template>
       
       <script>
           export default {
               data() {
                   return {
                       filterText: '',
                       //展示信息的基本结构是id为分类信息的id，label是要展示的分类信息，如果有子分类将子分类信息放在children中，以这种形式进行嵌套
                       data2: [{
                           id: 1,
                           label: 'Level one 1',//这个就是一级分类展示的信息
                           children: [{
                               id: 4,
                               label: 'Level two 1-1',//一级分类下的children中的label是二级分类中展示的信息
                               children: [
                                   {
                                   id: 9,
                                   label: 'Level three 1-1-1'//二级分类下的children中的label是三级分类中展示的信息
                                   }, 
                                   {
                                   id: 10,
                                   label: 'Level three 1-1-2'
                                   }
                               ]
                           }]
                       }, 
                       {
                           id: 2,
                           label: 'Level one 2',
                           children: [
                               {
                               id: 5,
                               label: 'Level two 2-1'
                               }, 
                               {
                                   id: 6,
                                   label: 'Level two 2-2'
                               }
                           ]
                       }, 
                       {
                           id: 3,
                           label: 'Level one 3',
                           children: [
                               {
                                   id: 7,
                                   label: 'Level two 3-1'
                               }, 
                               {
                                   id: 8,
                                   label: 'Level two 3-2'
                               }
                           ]
                       }
                       ],
                       defaultProps: {
                           children: 'children',
                           label: 'label'
                       }
                   }
               },
               watch: {
                   filterText(val) {
                       this.$refs.tree2.filter(val)
                   }
               },
               methods: {
                   filterNode(value, data) {
                       if (!value) return true
                       return data.label.indexOf(value) !== -1
                   }
               }
           }
       </script>
       ```

       

     + 后端接口：创建接口返回课程信息并封装成前端模板要求的data2的格式供前端树形结构自动遍历

       + 参考树形结构的属性，创建一级分类和二级分类两个实体类，用list集合作为一级分类的属性表示一级分类下有多个二级分类

         ```java
         @Data
         public class FirstLevelSubject {
             private String id;
             private String title;
         
             //一级分类中的二级分类
             private List<SecondLevelSubject> children=new ArrayList<>();
         }
         ```

         ```java
         @Data
         public class SecondLevelSubject {
             private String id;
             private String title;
         }
         ```

         ```java
         /**
          * @return {@link ResponseData }
          * @描述 查询数据库所有课程并按一级目录二级目录整理成list集合返回给前端
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/09/11
          * @since 1.0.0
          */
         @GetMapping("getAllSubject")
         public ResponseData getAllSubject(){
             List<FirstLevelSubject> subjects=eduSubjectService.getAllSubject();
             return ResponseData.responseCall().data("subjects",subjects);
         }
         ```

         ```java
         //这里示例用的是两层for循环嵌套，使用HashMap对
         @Override
         public List<FirstLevelSubject> getAllSubject() {
             //查询一级课程分类，wrapper能清除条件反复使用吗？
             QueryWrapper<EduSubject> firstLevelSubjectQueryWrapper = new QueryWrapper<>();
             firstLevelSubjectQueryWrapper.eq("parent_id","0");
             List<EduSubject> firstLevelSubjects = list(firstLevelSubjectQueryWrapper);
             //也可以使用自动注入的baseMapper直接调用selectList方法查询所有，实际ServiceImpl对selectList方法进行了封装
         
             //查询二级课程分类，考虑到根据一级课程分类的id对二级课程分类多次查询效率较低，直接一次性查询所有的二级分类后再进行统一封装
             QueryWrapper<EduSubject> secondLevelSubjectQueryWrapper = new QueryWrapper<>();
             secondLevelSubjectQueryWrapper.ne("parent_id","0");
             List<EduSubject> secondLevelSubjects = list(secondLevelSubjectQueryWrapper);
         
             //创建list集合，用于最终封装数据
             List<FirstLevelSubject> finalSubjectList=new ArrayList<>();
             //创建HashMap，便于封装二级分类
             Map<String,List<SecondLevelSubject>> classificationMap=new HashMap<>();
             //封装一级分类
             //把查询出来的一级分类遍历并读取id和title信息进行封装，spring框架提供一个工具类BeanUtils，其中的copyProperties方法会将第一个参数
             // 对象的属性值搞出来放在第二个参数对象属性上，避免属性过多代码繁琐
             firstLevelSubjects.forEach(eduSubject -> {
                 FirstLevelSubject firstLevelSubject = new FirstLevelSubject();
                 //BeanUtils的copyProperties方法作用是把eduSubject的属性值复制到firstLevelSubject中去，第二个对象中没有的值就不进行封装
                 BeanUtils.copyProperties(eduSubject,firstLevelSubject);
                 finalSubjectList.add(firstLevelSubject);
                 classificationMap.put(eduSubject.getId(),new ArrayList<>());
             });
             secondLevelSubjects.forEach(eduSubject -> {
                 SecondLevelSubject secondLevelSubject = new SecondLevelSubject();
                 BeanUtils.copyProperties(eduSubject,secondLevelSubject);
                 classificationMap.get(eduSubject.getParentId()).add(secondLevelSubject);
             });
             //将二级分类拷贝到一级分类的children属性中
             finalSubjectList.forEach(firstLevelSubject -> {
                 firstLevelSubject.setChildren(classificationMap.get(firstLevelSubject.getId()));
             });
             return finalSubjectList;
         }
         ```

       + 使用swagger测试没毛病

     + 前后端整合

       + 编写subject.js定义课程列表查询接口

         ```javascript
         import request from '@/utils/request'
         
         export default {
             findAllSubject(){
                 return request({
                     url: `/eduservice/subject/getAllSubject`,
                     method: 'get'
                 })
             }
         }
         ```

       + 课程列表显示页面修改

         > data2定义成空数据，准备接收后端返回数据，引入查询接口，在methods中定义查询所有课程方法getAllSubjectList，在created方法在页面加载时就对查询所有课程的方法进行调用，修改defaultProps的label为title，filterNode方法中的label也改成title，children也改成对应后端的属性名

         ```html
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
                 watch: {
                     filterText(val) {
                         this.$refs.tree2.filter(val)
                     }
                 },
                 methods: {
                     getAllSubjectList(){
                         subject.findAllSubject().then(response=>{
                             this.subjects=response.data.subjects
                         }).catch(error=>{
                             console.log(error)
                         })
                     },
                     filterNode(value, data) {
                         if (!value) return true
                         return data.title.indexOf(value) !== -1
                     }
                 }
             }
         </script>
         ```

         【前端效果】

         ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/09/11/1bc8f65207ae424d84ca6049f30e965a课程分类前端页面效果.png)

     + 将检索功能修改为不区分大小写

       ```html
       filterNode(value, data) {
           if (!value) return true
           return data.title.toLowerCase().indexOf(value.toLowerCase()) !== -1
       }
       ```

     + 设置添加课程分类成功后路由跳转到课程列表页面

       ```HTML
       this.$router.push({path:'/subject/list'})
       ```

       

## 添加课程

+ 实现一个课程发布流程

  + 编辑课程基本信息【包括课程名称、课程价格、课时数、课程简介、所属讲师、所属分类，填写完点击保存并下一步跳转编辑课程大纲】
  + 编辑课程大纲【做章节和小节的列表功能，在小节中有添加视频的功能，注意在中间环节还要添加上一步和下一步按钮回到上一步修改或者直接去下一步跳转课程的最终发布】
  + 课程最终发布【课程信息确认，没有问题再提交，提示信息包括课程名称、课程价格、课程分类，提供两个按钮上一步和最终发布,没有点击课程发布前台是看不到发布的视频和信息的】

+ 课程添加需要使用到的数据库表

  【新表】

  + edu_course：【课程表】主要存储课程的基本信息，包括课程名称、课程价格、课时数、课程封面cover、购买数、观看数、版本、状态等，当然还有id，逻辑删除、创建时间，修改时间等
  + edu_course_description：【课程简介表】主要用于存储课程的简介信息
  + edu_chapter：【课程章节表】主要存储课程的章节信息
  + edu_video：【课程小结表】主要存储课程章节的小结信息，同样小节中涉及到视频，用到阿里云的视频点播，在阿里视频点播中左视频的存储操作

  【已经有的表】

  + edu_teacher：【讲师表】
  + edu_subject：【课程分类表】

+ 课程表之间的关系【一对一、一对多、多对多】【4个一对多，一个一对一】

  + 一个课程分类中有多个课程，一个课程只可能属于一个课程分类
  + 一个课程有多个章节，一个章节只可能属于一个课程
  + 一个章节对应多个小节，一个小节只可能属于一个章节

  edu_subject与edu_course是一对多关系，edu_course与edu_chapter是一对多关系，edu_chapter与edu_video是一对多关系

  + 一个课程对应一个课程简介，一个简介也只属于一个课程

  edu_course与edu_course_description是一对一关系

  + 一个讲师与课程之间可能是一对一关系，也可能是多对多关系，一个讲师讲多门课，当然存在一门课的内容多个讲师进行讲解，但是都可以看成把这种直接看成两门课，统一成一对多关系

  edu_teacher与edu_course是一对多关系

  ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/09/11/f32bab0e7f1842b5b9c7a8da2e147177courseRelationship.png)

+ 后端框架搭建

  + 使用mp的代码生成器直接生成对应四张数据库表的代码结构，注意课程简介的controller不会单独使用，可以删掉，简介的修改等操作都在可成的controller中完成，注意不同的表需要使用不同的service继承的Mapper来操作对应的数据库表，其实可以理解，因为继承的Mapper都添加了泛型
  
  + 添加课程细节问题列举
    + 在添加课程基本信息时就会添加课程简介，课程基本信息和课程简介属于两张表，以前是用一个对应一张表的实体类对上传信息进行封装再使用mp直接将对象进行数据库存储，解决办法是专门创建一个vo【View Object显示层对象，一般是web向模板渲染引擎传输的对象】实体类，用于表单提交数据的封装，后续在提取出来对不同的数据库表进行操作，好消息是不同的表中可以添加各种spring管理的service对象，
    + 一个表单提交信息向两张表中添加信息
    + 课程信息填写涉及到的讲师信息需要下拉列表查询所有讲师并提供选择讲师下拉列表，课程所属分类同理查询并从下拉列表进行选择【课程分类涉及到一级分类和二级分类，需要做成二级联动的效果】
    
  + 添加课程具体实现
  
    + 添加课程信息接口实现
  
      + 依靠vo类封装前端信息，使用BeanUtils将信息分开拷贝到实体类添加到数据库表，其中会有个问题，添加到两个表中的数据应该是一对一关系，实际添加信息不经过特殊处理各自的id都不同，没有一对一关系【解决办法：将课程表的id直接赋值给课程简介表的id】
  
      + 创建vo类CourseInfoForm，编写控制器方法addCourseInfo封装请求数据调用saveCourseInfo方法执行数据分包数据库存储并返回处理结果，在EduCourseServiceImpl中的saveCourseInfo方法中编写数据分包存储逻辑【存在问题：没有添加事务处理,涉及到两个表不同mapper的事务处理@transaction是否生效】
  
        ```java
        @ApiModel(value = "课程基本信息", description = "编辑课程基本信息的表单对象")
        @Data
        public class CourseInfoForm implements Serializable {
            private static final long serialVersionUID = 1L;
            @ApiModelProperty(value = "课程ID")
            private String id;
            @ApiModelProperty(value = "课程讲师ID")
            private String teacherId;
            @ApiModelProperty(value = "课程专业ID")
            private String subjectId;
            @ApiModelProperty(value = "课程标题")
            private String title;
            @ApiModelProperty(value = "课程销售价格，设置为0则可免费观看")
            private BigDecimal price;
            @ApiModelProperty(value = "总课时")
            private Integer courseTotalTime;
            @ApiModelProperty(value = "课程封面图片路径")
            private String cover;
            @ApiModelProperty(value = "课程简介")
            private String description;
        }
        ```
  
        ```java
        @PostMapping("addCourseInfo")
        @ApiOperation("添加课程信息")
        public ResponseData addCourseInfo(@RequestBody CourseInfoForm courseInfoForm){
            eduCourseService.saveCourseInfo(courseInfoForm);
            return ResponseData.responseCall();
        }
        ```
  
        ```java
        @Service
        public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {
        
            @Autowired
            private EduCourseDescriptionService eduCourseDescriptionService;
        
            /**
             * @param courseInfoForm
             * @描述 由于课程表和课程信息表是一对一的关系，直接拿课程表的id作为课程信息表的id
             * @author Earl
             * @version 1.0.0
             * @创建日期 2023/09/12
             * @since 1.0.0
             */
            @Override
            public void saveCourseInfo(CourseInfoForm courseInfoForm) {
                //向课程表添加课程基本信息
                EduCourse eduCourse = new EduCourse();
                BeanUtils.copyProperties(courseInfoForm,eduCourse);
                //注意一下baseMapper.insert返回的是插入记录条数，但是service中封装的返回值是当插入记录条数大于等于1且不为null就返回true
                //课程表添加信息不成功就抛出异常，这儿似乎不需要事务，因为添加失败了抛异常后续也不会执行了,但是后续课程简介仍然可能添加失败，最好还是加上事务
                if (!save(eduCourse)){
                    throw new CustomException(20001,"添加课程信息失败");
                }
                //向课程简介表添加课程简介，同时注意将课程表的id设置为可成信息表对应的id
                EduCourseDescription eduCourseDescription = new EduCourseDescription();
                eduCourseDescription.setId(eduCourse.getId()).setDescription(courseInfoForm.getDescription());
                eduCourseDescriptionService.save(eduCourseDescription);
            }
        }
        ```
  
    + 添加课程信息前端实现
  
      + 添加路由，在views/course目录下创建3个页面，分别对应课程信息的info页面、章节chapter页面、课程最终发布的publish页面、课程列表list页面；添加子路由把子路由对应到相应的前端页面【这一步注意逻辑：页面跳转不是自由的，必须先经历添加课程信息--才能跳转---课程大纲分类---才能跳转---课程最终发布，办法就是用hidden：true把路由隐藏起来，用path/:id使用路由跳转的方式对这些路由进行访问】
  
      + 课程信息添加页面
  
        + element-ui中的步骤条选取样式，点击下一步会跳到下一个步骤的样式，和页面无关，就是一个样式条
  
          > 一下代码在三个页面复用，不同页面其中active的属性值分别为1,2,3就能实现步骤条的效果，点击下一步上一步使用路由跳转实现
  
          ```html
          <el-steps :active="1" process-status="wait" align-center style="marginbottom: 40px;">
              <el-step title="填写课程基本信息"/>
              <el-step title="创建课程大纲"/>
              <el-step title="提交审核"/>
          </el-steps>
          <el-form label-width="120px">
              <el-form-item>
                  <el-button :disabled="saveBtnDisabled" type="primary" @click="next">保存并下一步</el-button>
              </el-form-item>
          </el-form>
          ```
  
          > 隐藏路由跳转
  
          ```html
          methods: {
              previous() {
                  console.log('previous')
                  this.$router.push({ path: '/course/info/1' })
              },
              next() {
                  console.log('next')
                  this.$router.push({ path: '/course/publish/1' })
              }
          }
          ```
  
        + 这里主要是隐藏路由不能主动访问页面，必须由路由跳转实现步骤条功能；点击上一步下一步绑定事件路由跳转方法访问隐藏路由实现路由跳转，每个页面都添加同一个element-ui步骤条组件，控制其中的active参数值达到不同步骤的视觉效果
  
          > 路由页面
  
          ```html
          {
              path: '/course',
              component: Layout,
              redirect: '/course/list',
              name: '课程管理',
              meta: { title: '课程管理', icon: 'example' },
              children: [
                {
                  path: 'list',
                  name: '课程列表',
                  component: () => import('@/views/edu/course/list'),
                  meta: { title: '课程列表', icon: 'table' }
                },
                {
                  path: 'info',//匹配路由路径
                  name: 'addEduCourse',
                  component: () => import('@/views/edu/course/info'),//路由匹配页面
                  meta: { title: '添加课程', icon: 'tree' }//注意这个名字对应路由的左边栏的名字，也对应路由上方的名字
                },
                {
                  path: 'info/:id',
                  name: 'EduCourseInfoEdit',
                  component: () => import('@/views/edu/course/info'),
                  meta: { title: '编辑课程基本信息', noCache: true },
                  hidden: true
                },
                {
                  path: 'chapter/:id',
                  name: 'EduCourseChapterEdit',
                  component: () => import('@/views/edu/course/chapter'),
                  meta: { title: '编辑课程大纲', noCache: true },
                  hidden: true
                },
                {
                  path: 'publish/:id',
                  name: 'EduCoursePublishEdit',
                  component: () => import('@/views/edu/course/publish'),
                  meta: { title: '发布课程', noCache: true },
                  hidden: true
                }
              ]
            },
          ```
  
          > 编辑课程信息页面
  
          ```html
          <template>
              <div class="app-container">
                  <h2 style="text-align: center;">发布新课程</h2>
                  <el-steps :active="1" process-status="wait" align-center style="marginbottom: 40px;">
                      <el-step title="填写课程基本信息"/>
                      <el-step title="创建课程大纲"/>
                      <el-step title="提交审核"/>
                  </el-steps>
                  <el-form label-width="120px">
                      <el-form-item>
                          <el-button :disabled="saveBtnDisabled" type="primary" @click="next">保存并下一步</el-button>
                      </el-form-item>
                  </el-form>
              </div>
          </template>
          <script>
              export default {
                  data() {
                      return {
                          saveBtnDisabled: false // 保存按钮是否禁用
                      }
                  },
                  created() {
                      console.log('info created')
                  },
                  methods: {
                      next() {
                          console.log('next')
                          this.$router.push({ path: '/course/chapter/1' })
                      }
                  }
              }
          </script>
          ```
  
          > 编辑课程大纲页面
  
          ```html
          <template>
              <div class="app-container">
                  <h2 style="text-align: center;">发布新课程</h2>
                  <el-steps :active="2" process-status="wait" align-center style="marginbottom: 40px;">
                      <el-step title="填写课程基本信息"/>
                      <el-step title="创建课程大纲"/>
                      <el-step title="提交审核"/>
                  </el-steps>
                  <el-form label-width="120px">
                      <el-form-item>
                          <el-button @click="previous">上一步</el-button>
                          <el-button :disabled="saveBtnDisabled" type="primary" @click="next">下一步</el-button>
                      </el-form-item>
                  </el-form>
              </div>
          </template>
          <script>
              export default {
                  data() {
                      return {
                          saveBtnDisabled: false // 保存按钮是否禁用
                      }
                  },
                  created() {
                      console.log('chapter created')
                  },
                  methods: {
                      previous() {
                          console.log('previous')
                          this.$router.push({ path: '/course/info/1' })
                      },
                      next() {
                          console.log('next')
                          this.$router.push({ path: '/course/publish/1' })
                      }
                  }
              }
          </script>
          ```
  
          > 发布课程页面
  
          ```html
          <template>
              <div class="app-container">
                  <h2 style="text-align: center;">发布新课程</h2>
                      <el-steps :active="3" process-status="wait" align-center style="marginbottom: 40px;">
                          <el-step title="填写课程基本信息"/>
                          <el-step title="创建课程大纲"/>
                          <el-step title="提交审核"/>
                      </el-steps>
                  <el-form label-width="120px">
                      <el-form-item>
                          <el-button @click="previous">返回修改</el-button>
                          <el-button :disabled="saveBtnDisabled" type="primary" @click="publish">发布课程</el-button>
                      </el-form-item>
                  </el-form>
              </div>
          </template>
          <script>
              export default {
                  data() {
                      return {
                          saveBtnDisabled: false // 保存按钮是否禁用
                      }
                  },
                  created() {
                      console.log('publish created')
                  },
                  methods: {
                      previous() {
                          console.log('previous')
                          this.$router.push({ path: '/course/chapter/1' })
                      },
                      publish() {
                          console.log('publish')
                          this.$router.push({ path: '/course/list' })
                      }
                  }
              }
          </script>
          ```
  
        + 实现编辑课程基本信息页面
  
          > 定义前端接口函数
  
          ```html
          import request from '@/utils/request'
          export default {
              saveCourseInfo(courseInfo) {
                  return request({
                      url: `/eduservice/course/addCourseInfo`,
                      method: 'post',
                      data: courseInfo
                  })
              }
          }
          ```
  
          > 编写前端编辑课程基本信息页面代码
          >
          > element-ui文本居中的样式style="text-align: center;",不写就是靠右
          >
          > style="marginbottom: 40px;"是当前标签底部像素40px
          >
          > ...是扩展运算符，可以将...后面对象的属性合并到某个对象中
  
          ```html
          <template>
              <div class="app-container">
                  <h2 style="text-align: center;">发布新课程</h2>
                  <el-steps :active="1" process-status="wait" align-center style="marginbottom: 40px;">
                      <el-step title="填写课程基本信息"/>
                      <el-step title="创建课程大纲"/>
                      <el-step title="最终发布"/>
                  </el-steps>
                  <el-form label-width="120px">
                      <el-form-item label="课程标题">
                          <el-input v-model="courseInfo.title" placeholder=" 示例：机器学习项目课：从基础到搭建项目视频课程。专业名称注意大小写"/>
                      </el-form-item>
                      <!-- 所属分类 TODO -->
                      <!-- 课程讲师 TODO -->
                      <el-form-item label="总课时">
                          <el-input-number :min="0" v-model="courseInfo.courseTotalTime" controls-position="right" placeholder="请填写课程的总课时数"/>
                      </el-form-item>
                      <!-- 课程简介 -->
                      <el-form-item label="课程简介">
                          <el-input v-model="courseInfo.description" :rows="10" type="textarea"/>
                      </el-form-item>
                      <!-- 课程封面 TODO -->
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
              const defaultForm = {
                  title: '',
                  subjectId: '',
                  teacherId: '',
                  courseTotalTime: 0,
                  description: '',
                  cover: '',
                  price: 0
              }
              export default {
                  data() {
                      return {
                          courseInfo: defaultForm,
                          saveBtnDisabled: false // 保存按钮是否禁用
                      }
                  },
                  watch: {
                      $route(to, from) {
                          console.log('watch $route')
                          this.init()
                      }
                  },
                  created() {
                      console.log('info created')
                      this.init()
                  },
                  methods: {
                      init() {
                          if (this.$route.params && this.$route.params.id) {
                              const id = this.$route.params.id
                              console.log(id)
                          } else {
                              this.courseInfo = { ...defaultForm }
                          }
                      },
                      next() {
                          console.log('next')
                          this.saveBtnDisabled = true
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
                          this.$router.push({ path: '/course/chapter/1' })
                      }
                  }
              }
          </script>
          ```
  
          > 优化：第一是把讲师和课程分类用下拉列表显示，第二个上传课程封面，第三个是做课程简介内容更加丰富，让字体有样式，可以插入图片，可以加一些特殊的图标，类似于QQ的文本框
          >
          > 问题：第一个课程数据保存太早了，而且整个流程没有添加事务；同时点击上一步课程数据没有回显，没法修改

### 讲师下拉列表

1. element-ui表单中选择下拉列表的样式

   > el-select
   >
   > el-option标签：其中label属性就是显示在下拉列表的内容，value是数据提交的内容，label标签用v-for遍历出所有讲师,下拉列表最终提交的数据value是讲师的id

   ```html
   <el-form-item label="课程讲师">
       <!--查询所有讲师的接口不能分页，需要重新定义查所有讲师的接口方法，注意讲师最终提交的是讲师的id，:value是单向绑定v-bind的缩写 -->
       <el-select v-model="courseInfo.teacherId" placeholder="请选择">
           <el-option v-for="teacher in teacherList" :key="teacher.id" :label="teacher.name" :value="teacher.id"/>
       </el-select>
   </el-form-item>
   ```

   > 定义所有讲师查询接口

   ```html
   getAllTeacher(){
       return request({
           url: `/eduservice/teacher/findAll`,
           method: 'get'
       })
   },
   ```

   > 将查询讲师接口数据赋值给teacherList

   ```html
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
   ```


### 课程分类下拉列表

1. 基本逻辑：第一次进入页面显示所有一级分类，选择某个一级分类后用change事件根据选中的id遍历一级分类数据将对应的children赋值给二级分类并显示对应一级分类中对应的二级分类，一级分类是edu_subject表中二级分类的subjectParentId，二级分类是subjectId

   > 注意在显示所有课程分类的列表中对应的subject.js中已经定义了查询课程分类的接口并且封装好了数据，可以直接拿来用
   >
   > 二级联动下拉列表element-ui样式

   ```html
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
   ```

   > 二级联动下拉列表参数处理

   ```html
   firstLevelSubjectChange(value){//框架封装了事件自动传参当前标签的值
       for(var i=0;i<this.firstLevelSubjects.length;i++){
           var curSubject= this.firstLevelSubjects[i]
           if(value===curSubject.id){
               this.secondLevelSubjects=curSubject.children
               this.courseInfo.subjectId = ''//一级下拉列表变化，二级下拉列表绑定的变量先初始化，一级没变，二级不变，没有这行代码一级变了二级不会变，会给编辑者造成歧义
           }
       }
   },
   getAllSubjectList(){
       subject.findAllSubject()
       .then(response=>{
           this.firstLevelSubjects=response.data.subjects
       }).catch(error=>{
           console.log(error)
       })
   },
   ```

   ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/09/13/f8be213338cf45699a2e21996e90491e课程分类二级联动效果.png)

### 课程封面上传

1. 图片上传还是调用讲师头像上传的接口

2. 课程封面上传前端element-ui组件

   ```html
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
           <!--一般为了效果好，会默认一个静态资源来提示该处可以添加更改封面图片，这个资源一般存放在静态资源文件夹static下,data中cover: '/static/高放废液玻璃固化.jpg'-->
           <img :src="courseInfo.cover">
       </el-upload>
   </el-form-item>
   ```

3. 封面上传前后的图片格式大小校验和上传后的地址处理

   ```html
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
   ```

   ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/09/13/b76230e81da04d45b9962af0cd5ec4e5添加默认封面效果.png)

### 课程简介整合文本编辑器

> 注意数据库中用于存储富文本编辑内容的内省是text类型，加粗效果是用strong标签标记的，图片内容作了base64编码，将编码内容直接存储在数据库中，注意text类型是有大小限制的，不能上传太大的图片【注意一下有没有longtext类型】

1. 富文本编辑器

   + Tinymce可视化编辑器

     + 参考
       https://panjiachen.gitee.io/vue-element-admin/#/components/tinymce
       https://panjiachen.gitee.io/vue-element-admin/#/example/create  
     + 效果：字体可以多种个性化设计，可以加线条，图片，表情等等

   + 整合步骤

     + 将富文本编辑器组件的components和static文件夹的内容复制到项目的components和static目录下

     + 在build/webpack.dev.conf.js中添加配置

       > 作用是使html页面中可以使用这里定义的BASE_URL变量

       ```json
       new HtmlWebpackPlugin({//这个是该配置文件中本身就有的，这是安装前端开发插件用的
           ......,
           templateParameters: {
           	BASE_URL: config.dev.assetsPublicPath + config.dev.assetsSubDirectory
           }
       })
       ```

     + 找到index.html，在文件中引入两个JS脚本文件

       ```html
       <script src=<%= BASE_URL %>/tinymce4.7.5/tinymce.min.js></script>
       <!--下面的js是中文的一个软件包,引入这个软件包可以让富文本编辑器界面变成中文的-->
       <script src=<%= BASE_URL %>/tinymce4.7.5/langs/zh_CN.js></script>
       ```

     + 在views具体页面中从组件包引入Tinymce，并在export default中声明组件Tinymce

       ```html
       import Tinymce from '@/components/Tinymce'
       export default {
       	components: { Tinymce },//声明
       	......
       }
       ```

     + 使用tinymce标签就可以直接使用富文本编辑器了

       ```html
       <!-- 课程简介-->
       <el-form-item label="课程简介">
       	<tinymce :height="300" v-model="courseInfo.description"/>
       </el-form-item>
       ```

     + 给富文本编辑器添加如下样式调整上传图片按钮的高度

       ```html
       <style scoped>
       .tinymce-container {
       line-height: 29px;
       }
       </style>
       ```




## 课程大纲管理

### 课程大纲列表功能

1. 实现课程大纲展示列表功能

   > 还是将章节小节信息封装成二级目录的形式

   + 后端接口

     > 创建对应二级目录的两个实体类，章节类和小节类，在章节中用list集合封装小节【表示一对多】，使用单独的controller即EduChapterController来控制章节的对应功能，但是要注意章节表中的课程id确保着章节和课程的对应关系，查询课程列表的时候要作为条件进行传入

     + 创建封装章节、小节的二级查询实体类

     + 在控制器方法中定义查询课程大纲列表的控制器方法，使用@PathVariable注解封装查询课程id，确定返回结果的类型为章节的list集合【前端能展示所有的数据，直接返回所有章节的list集合，每个章节中都有各自小节的list集合】

     + 在课程service中实现对应的查询封装过程

       > + 根据课程id查询所有的章节
       > + 根据课程id查询课程所有的小节，小节表中的chapterId对应上级章节目录，小节表中的courseId字段对应小节的上级章节目录，courseId用于查询所有的小节，chapterId用于所有小节的章节封装
       > + 遍历查询到的所有章节信息进行封装，遍历所有查询到的小节封装成list集合封装到对应chapterId的章节的小节属性

     ```java
     @Service
     public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {
     
         @Autowired
         private EduVideoService eduVideoService;
     
         @Override
         public List<Chapter> getChaptersByCourseId(String courseId) {
             //通过课程ID查询所有章节信息
             QueryWrapper<EduChapter> chapterQueryWrapper = new QueryWrapper<>();
             chapterQueryWrapper.eq("course_id",courseId);
             List<EduChapter> eduChapterList = list(chapterQueryWrapper);
             //也可以使用自动注入的baseMapper直接调用selectList方法查询所有，实际ServiceImpl的list方法对selectList方法进行了封装
     
             //查询课程对应的小节信息，考虑到根据章节id对小节分类多次查询效率较低，直接一次性查询所有的小节后再进行统一封装
             QueryWrapper<EduVideo> sectionQueryWrapper = new QueryWrapper<>();
             sectionQueryWrapper.eq("course_id",courseId);
             List<EduVideo> eduVideoList = eduVideoService.list(sectionQueryWrapper);
     
             //创建list集合，用于最终封装数据
             List<Chapter> finalChapterList=new ArrayList<>();
             //创建HashMap，便于封装小节信息
             Map<String,List<Section>> classificationMap=new HashMap<>();
             //封装章节信息
             //把查询出来的章节遍历并读取id和title信息进行封装，spring框架提供一个工具类BeanUtils，其中的copyProperties方法会将第一个参数对象的属性值搞出来放在第二个参数对象属性上，避免属性过多代码繁琐
             eduChapterList.forEach(eduChapter -> {
                 Chapter chapter = new Chapter();
                 BeanUtils.copyProperties(eduChapter,chapter);
                 finalChapterList.add(chapter);
                 classificationMap.put(eduChapter.getId(),new ArrayList<>());
             });
             eduVideoList.forEach(eduVideo -> {
                 Section section = new Section();
                 BeanUtils.copyProperties(eduVideo,section);
                 classificationMap.get(eduVideo.getChapterId()).add(section);
             });
             //将二级分类拷贝到一级分类的children属性中
             finalChapterList.forEach(chapter -> {
                 chapter.setChildren(classificationMap.get(chapter.getId()));
             });
             return finalChapterList;
         }
     }
     ```

   + 前端页面

     + 在chapter.js中定义章节查询接口

     + 在chapter.vue中编写前端列表组件，并调用后端接口获取课程数据，课程id在第一步编辑课程基本信息时已经添加到路由末端，可以通过this.$route.params.id获取，太丑，后面改一下

       ```html
       <template>
           <div class="app-container">
               <h2 style="text-align: center;">发布新课程</h2>
               <el-steps :active="2" process-status="wait" align-center style="margin-bottom:40px;">
                   <el-step title="填写课程基本信息"/>
                   <el-step title="创建课程大纲"/>
                   <el-step title="最终发布"/>
               </el-steps>
               <el-button type="text">添加章节</el-button>
       <!-- 章节 -->
       <ul class="chanpterList">
       <li
       v-for="chapter in chapters"
       :key="chapter.id">
       <p>{{ chapter.title }}</p>
       <!-- 视频 -->
       <ul class="chanpterList videoList">
       <li
       v-for="video in chapter.children"
       :key="video.id">
       <p>{{ video.title }}</p>
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
           export default {
               data() {
                   return {
                       saveBtnDisabled: false ,// 保存按钮是否禁用
                       courseId: '',
                       chapters: [],
                   }
               },
               created() {
                   this.init()
               },
               methods: {
                   init(){
                       if(this.$route.params && this.$route.params.id){
                           this.courseId=this.$route.params.id
                           this.getChaptersByCourseId(this.courseId)
                       }
                   },
                   getChaptersByCourseId(courseId){
                       chapter.queryChaptersByCourseId(courseId)
                       .then(response=>{
                           console.log(response)
                           this.chapters=response.data.chapters
                           console.log(this.chapters)
                       })
                   },
                   previous() {
                       console.log('previous')
                       this.$router.push({ path: '/course/info/1' })
                   },
                   next() {
                       console.log('next')
                       this.$router.push({ path: '/course/publish/1' })
                   }
               }
           }
       </script>
       <style scoped>
           .chanpterList{
               position: relative;
               list-style: none;
               margin: 0;
               padding: 0;
           }
           .chanpterList li{
               position: relative;
           }
           .chanpterList p{
               float: left;
               font-size: 20px;
               margin: 10px 0;
               padding: 10px;
               height: 70px;
               line-height: 50px;
               width: 100%;
               border: 1px solid #DDD;
           }
           .chanpterList .acts {
               float: right;
               font-size: 14px;
           }
           .videoList{
               padding-left: 50px;
           }
           .videoList p{
               float: left;
               font-size: 14px;
               margin: 10px 0;
               padding: 10px;
               height: 50px;
               line-height: 30px;
               width: 100%;
               border: 1px dotted #DDD;
           }
       </style>
       ```

       

2. 点击上一步修改课程基本信息

   > 点击上一步要实现数据的回显以供修改
   >
   > 在数据回显页面点击修改内容并保存，会修改数据库的内容

   + 后端接口

     + 根据课程id查询课程基本信息

       > 在course的控制器方法中调用service的getCourseInfo方法通过课程id获取课程信息

       【web层】
     
       ```java
       @GetMapping("addCourseInfo/{courseId}")
       @ApiOperation("回显课程信息")
       public ResponseData echoCourseInfoById(@ApiParam(name="id",value = "课程ID",required = true) @PathVariable String courseId ){
           CourseInfoForm courseInfo=eduCourseService.getCourseInfoById(courseId);
           return ResponseData.responseCall().data("courseInfo",courseInfo);
       }
       ```
     
       【业务层】
     
       ```java
       @Override
       public CourseInfoForm getCourseInfoById(String id) {
           //准备封装返回查询数据的对象
           CourseInfoForm courseInfoForm=new CourseInfoForm();
           //根据课程id查询课程表,String类实现了可序列化接口，传参的id被封装成课序列化多态，这里仍然可以直接使用String类型的id
           EduCourse eduCourse = getById(id);
           BeanUtils.copyProperties(eduCourse,courseInfoForm);
           EduCourseDescription eduCourseDescription = eduCourseDescriptionService.getById(id);
           courseInfoForm.setDescription(eduCourseDescription.getDescription());
           return courseInfoForm;
       }
       ```
     
     + 修改课程信息接口
     
       【web层】
     
       ```java
       @PutMapping("addCourseInfo")
       @ApiOperation("更新课程信息")
       public ResponseData updateCourseInfo(@ApiParam(name="CourseInfoForm",value = "课程更新信息",required = true) @RequestBody CourseInfoForm courseInfoForm){
           eduCourseService.updateCourseInfo(courseInfoForm);
           return ResponseData.responseCall().data("courseId",courseInfoForm.getId());
       }
       ```
     
       【业务层】
     
       ```java
       @Override
       public void updateCourseInfo(CourseInfoForm courseInfoForm) {
           EduCourse eduCourse = new EduCourse();
           BeanUtils.copyProperties(courseInfoForm,eduCourse);
           if (!updateById(eduCourse)){
               throw new CustomException(20001,"课程信息保存失败");
           }
           EduCourseDescription eduCourseDescription = new EduCourseDescription();
           eduCourseDescription.setDescription(courseInfoForm.getDescription());
           if(!eduCourseDescriptionService.updateById(eduCourseDescription)){
               throw new CustomException(20001,"课程详情信息保存失败");
           }
       }
       ```
     
   + 前端实现
   
     + 在api的course中定义后端接口
   
       ```javascript
       import request from '@/utils/request'
       export default {
           echoCourseInfo(courseId){
               return request({
                   url: `/eduservice/course/addCourseInfo/`+{courseId},
                   method: 'get'
               })
           },
           updateCourseInfo(courseInfo){
               return request({
                   url: `/eduservice/course/addCourseInfo/`,
                   method: 'put',
                   data: courseInfo
               })
           }
       }
       ```
   
     + 在chapter页面，向前跳转的路由添加id
   
       ```vue
       previous() {
           this.$router.push({ path: '/course/info/'+this.courseId })
       },
       ```
   
     + 在info页面调用接口方法实现数据回显，如果路由有id就调用回显接口，如果路由没有id就直接准备调用填写课程基本信息接口
   
       ```javascript
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
                   const id = this.$route.params.id
                   // 根据id获取课程基本信息
                   this.echoCourseInfo(id)
               } else {
                   this.courseInfo = { ...defaultForm }
                   //手动清空富文本编辑器的内容
                   tinymce.activeEditor.setContent("");
               }
           }
       }
       ```
   
     + 在update方法中调用修改数据库信息接口修改数据库信息，在点击下一步按钮绑定有id就调用更新接口，没有id就调用保存课程信息接口
   
       ```javascript
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
       ```
   
       > 测试：填写课程基本信息的下拉列表，二级联动下拉列表、点击下一步的数据库保存功能；点击上一步的数据库回显功能；再次点击添加课程的内容清空功能，特别是富文本编辑器的内容清空功能；修改课程基本信息后的更新功能

### 章节增删改

1. 添加章节的功能

   + 功能需求：有一个添加章节的按钮，点击该按钮会弹出窗口，在弹出窗口中添加课程，点击保存进行添加，弹出窗口可以选择element-ui的对话框

   + 前端实现

     【定义对应后端的接口】

     ```javascript
     addChapter(chapter){
         return request({
             url: '/eduservice/chapter/addChapter',
             method: 'post',
             data: chapter
         })
     },
     queryChapter(chapterId){
         return request({
             url: `/eduservice/chapter/queryChapter/${chapterId}`,
             method: 'get',
         })
     },
     updateChapter(chapter){
         return request({
             url: `/eduservice/chapter/updateChapter`,
             method: 'put',
             data: chapter
         })
     },
     deleteChapter(chapterId){
         return request({
             url: `/eduservice/chapter/deleteChapter/${chapterId}`,
             method: 'delete',
         })
     }
     ```

     【添加章节对话框element-ui组件代码】

     ```html
     <!-- Table:dialogTableVisible = true绑定了dialog的:visible.sync属性，表示是否显示对应的对话框，点击事件发生后就是默认true属性 -->
     <!-- 添加和修改章节表单 -->
     <el-dialog :visible.sync="dialogChapterFormVisible" title="添加章节">
         <el-form :model="chapter" label-width="120px">
             <el-form-item label="章节标题">
                 <el-input v-model="chapter.title"/>
             </el-form-item>
         <el-form-item label="章节排序">
             <el-input-number v-model="chapter.sort" :min="0" controlsposition="right"/>
         </el-form-item>
         </el-form>
         <div slot="footer" class="dialog-footer">
             <el-button @click="dialogChapterFormVisible = false">取 消</el-button>
             <el-button type="primary" @click="saveOrUpdate">确 定</el-button>
         </div>
     </el-dialog>
     <el-row>
         <el-button type="primary" @click="dialogChapterFormVisible = true">添加新章节</el-button>
     </el-row>
     ```

     【弹窗的保存功能】

     ```java
     saveOrUpdate(){
         this.saveChapter()
     },
     saveChapter(){
         chapter.addChapter(this.chapter)
         .then(response => {
             this.$message({
                 type: 'success',
                 message: '章节添加成功!'
             })
             this.handleDialog()
         })
         .catch((response) => {
             this.$message({
                 type: 'error',
                 message: response.message
             })
         })
     },
     handleDialog(){
         this.dialogChapterFormVisible=false
         this.getChaptersByCourseId()
         //重置章节标题
         this.chapter.title=''
         //重置章节排序
         this.chapter.sort=0
     }
     ```

     【修改章节信息的实现】

     > 在章节列表中添加编辑和删除按钮，编辑按钮绑定事件，打开弹框，调用接口通过chapterId调用接口获取章节id，将返回数据赋值给chapter并显示在添加对话框中，chapter.id是初始化章节列表时带过来的，点击事件可以直接获取对应的章节的id，此时弹框的事件要进行判断对应的弹框是修改状态还是添加状态，文档是判定id是否存在判定的，因为回显会绑定章节对象的id，没有id就做添加工作，有id就做修改操作，修改成功后的操作和添加是一样的

     > 【编辑和修改按钮】
     > 注意：p标签的样式图层会浮动在span的上面，导致span的按钮无法被点击，也就没有办法触发单击事件，这时候的解决办法是通过样式设置p和span标签的图层位置为相对，将span图层的优先级z-index设置为1，让span图层置于所有图层的最上方,直接注释掉float也是可以的，但是这样会导致页面布局混乱，细节看[关于float属性导致button按钮无法点击问题的解决思路_明天天明~的博客-CSDN博客](https://blog.csdn.net/qq_41950447/article/details/116261878)

     ```java
     <!-- 章节 -->
     <ul class="chanpterList">
         <li
         v-for="chapter in chapters"
         :key="chapter.id">
             <p>{{ chapter.title }}
                 <span class="acts">
                     <el-button @click="editChapter(chapter.id)" type="success" plain>编辑</el-button>
                     <el-button type="danger" plain >删除</el-button>
                 </span>
             </p>
     
             <!-- 视频 -->
             <ul class="chanpterList videoList">
                 <li
                 v-for="video in chapter.children"
                 :key="video.id">
                     <p>{{ video.title }}</p>
                 </li>
             </ul>
         </li>
     </ul>
     ```

     ```css
     .chanpterList p{
         float: left;/**这个属性会导致内部的标签被p标签覆盖，导致其中的按钮不能被点击，解决办法是设置position属性为relative，并提升内部标签span的优先级z-index=1，将sapn标签置于顶层，这样按钮就可以点击了 */
         font-size: 20px;
         margin: 10px 0;
         padding: 10px;
         height: 70px;
         line-height: 50px;
         width: 100%;
         border: 1px solid #DDD;
         position: relative;
     }
     .chanpterList .acts {
         float: right;
         font-size: 14px;
         position: relative;
         z-index: 1;
     }
     ```

     > 【单击编辑的绑定事件】

     ```javascript
     //定义编辑章节信息的方法，主要是数据的回显，由于原先的所有章节列表是单独封装成二级联动效果的对象，排序属性并没有涉及，这里直接查表，不使用之前的数据
     editChapter(chapterId){
         this.dialogChapterFormVisible=true
         chapter.queryChapter(chapterId)
         .then(response=>{
             this.chapter=response.data.chapter
         })
     },
     ```

     > 【单击确定的绑定事件】

     ```javascript
     saveOrUpdate(){
         this.chapterSaveBtnDisabled=true
         if(!this.chapter.id){
             this.saveChapter()
         }else{
             this.updateChapter()
         }
     },
      //定义更新章节信息的方法
     updateChapter(){
         chapter.updateChapter(this.chapter)
         .then(response=>{
             this.$message({
                 type: 'success',
                 message: '章节更新成功!'
             })
             this.handleDialog()
         })
     }
     ```

     【删除章节的前端实现】

     > 需求是先提示是否确认删除，点击确定后没有小节会直接删除
     >
     > 存在问题，删除按钮不会主动失去焦点，使用JavaScript手动失去焦点不起作用
   
     ```java
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
     ```
   
     
   
   + 后端实现
   
     > 在章节前端控制器中开发章节的添加，查询、修改，删除接口
     >
     > 添加和修改传参章节对象，查询和删除传参章节id
     >
     > 删除章节要特别注意，如果章节下面没有小节可以直接删除，但是章节下面有小节常见的开发处理方式有两种，第一种方式删除章节时将章节中的所有小节全部删除；第二种方式是章节下面有小节，不允许删除章节【目前采用的方式是章节中有小节就不允许删掉章节：逻辑是根据章节id查小节，能查出小节就不删，查不出就直接删章节，不需要查出小节具体的对象，只需要判断是否有小节，可以使用service的count方法查出对应条件的记录数；baseMapper的deleteById传参id可以删除对应记录】
   
     【前端控制器方法】
   
     ```java
     @PostMapping("addChapter")
     @ApiOperation("新增章节")
     public ResponseData addChapter(@ApiParam(name="chapter",value="课程章节",required = true) @RequestBody EduChapter eduChapter){
         return eduChapterService.save(eduChapter)?ResponseData.responseCall():ResponseData.responseErrorCall();
     }
     
     @GetMapping("queryChapter/{chapterId}")
     @ApiOperation("查询章节")
     public ResponseData queryChapter(@ApiParam(name="chapterId",value="课程章节ID",required = true) @PathVariable String chapterId){
         EduChapter chapter = eduChapterService.getById(chapterId);
         return ResponseData.responseCall().data("chapter",chapter);
     }
     
     @PutMapping("updateChapter")
     @ApiOperation("修改章节")
     public ResponseData updateChapter(@ApiParam(name="chapter",value="课程章节",required = true) @RequestBody EduChapter eduChapter){
         return eduChapterService.updateById(eduChapter)?ResponseData.responseCall():ResponseData.responseErrorCall();
     }
     
     @DeleteMapping("deleteChapter/{chapterId}")
     @ApiOperation("删除章节")
     public ResponseData deleteChapter(@ApiParam(name = "chapterId",value = "课程ID",required = true) @PathVariable String chapterId){
         return eduChapterService.deleteChapter(chapterId)?ResponseData.responseCall():ResponseData.responseErrorCall();
     }
     ```
   
     【删除的逻辑】
   
     ```java
     @Override
     public boolean deleteChapter(String chapterId) {
         QueryWrapper<EduVideo> eduVideoQueryWrapper = new QueryWrapper<>();
         eduVideoQueryWrapper.eq("chapter_id",chapterId);
         if (eduVideoService.count(eduVideoQueryWrapper)>0){
             throw new CustomException(20001,"该章节下存在小节，无法删除该章节");
         }
         return removeById(chapterId);
     }
     ```
   
     

### 小节增删改

> 需求：在章节标签上有一个添加小节的按钮，点击按钮弹出对话框添加小节信息，小节信息除了id和title外还有课程id、章节id、以及视频的id，这个id需要阿里云视频点播返回，暂时先标记成可以为空，以及视频名称同样如此，讲到再说
>
> 删除小节时需要完善，需要将视频一起进行删除
>
> 查询接口暂时不用写，因为已经在课程章节信息中查过了，当时在chapterservice中注入的videoService

1. 后端接口实现

   【小节前端控制器增删改接口】

   ```java
   @RestController
   @RequestMapping("/eduservice/video")
   @Api(description = "课程小节管理")
   @CrossOrigin
   public class EduVideoController {
       @Autowired
       EduVideoService eduVideoService;
       
       @PostMapping("addVideo")
       @ApiOperation("添加课程小节")
       public ResponseData addVideo(@ApiParam(name="video",value = "课程小节",required = true) @RequestBody EduVideo eduVideo){
           return eduVideoService.save(eduVideo)?ResponseData.responseCall():ResponseData.responseErrorCall();
       }
   
       @PutMapping("updateVideo")
       @ApiOperation("更新课程小节")
       public ResponseData updateVideo(@ApiParam(name="video",value = "课程小节",required = true)@RequestBody EduVideo eduVideo){
           return eduVideoService.updateById(eduVideo)?ResponseData.responseCall():ResponseData.responseErrorCall();
       }
       
       //TODO:这个方法后期需要完善阿里云点播小节视频的删除功能
       @DeleteMapping("deleteVideo/{id}")
       @ApiOperation("删除课程小节")
       public ResponseData deleteVideo(@ApiParam(name="videoId",value = "课程小节ID",required = true)@PathVariable String id){
           return eduVideoService.removeById(id)?ResponseData.responseCall():ResponseData.responseErrorCall();
       }
   }
   ```

2. 前端实现

   【前端定义后端对应接口】

   ```javascript
   import request from '@/utils/request'
   export default {
       addVideo(video){
           return request({
               url: '/eduservice/video/addVideo',
               method: 'post',
               data: video
           })
       },
       updateVideo(video){
           return request({
               url: `/eduservice/video/updateVideo`,
               method: 'put',
               data: video
           })
       },
       deleteVideo(videoId){
           return request({
               url: `/eduservice/video/deleteVideo/${videoId}`,
               method: 'delete',
           })
       }
   }
   ```

   【添加小节按钮】

   ```javascript
   <el-button type="primary" plain @click="addVideo(chapter.id)">添加课时</el-button>
   ```

   【添加小节对话框组件】

   ```html
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
           <!-- TODO -->
           </el-form-item>
       </el-form>
       <div slot="footer" class="dialog-footer">
           <el-button @click="handleVideoDialog">取 消</el-button>
           <el-button :disabled="videoSaveBtnDisabled" type="primary" @click="saveOrUpdateVideo">确 定</el-button>
       </div>
   </el-dialog>
   ```

   【编辑和修改小节按钮】

   ```html
   <p>{{ video.title }}
       <span class="acts">
           <el-button @click="editVideo(video.id)" type="success" size="small">编辑</el-button>
           <el-button type="danger" @click="deleteVideo(video.id)" size="small">删除</el-button>
       </span>
   </p>
   ```

   【相关处理函数】

   > 实现了小节的添加、修改和删除功能，以及小节数据的回显功能，组件和方法涉及的变量都在data中进行了定义

   ```javascript
   openVideoDialog(chapterId){ 
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
   ```

   





## 课程信息确认

### 添加课程信息展示和确认发布

> 编写SQL语句的方式完成，涉及多表操作。展示内容包括课程封面、课程名称、价格、课程分类、课程简介、课程讲师；涉及到4张表，可以创建一个vo类查四次表封装数据，但是不建议，建议手写sql实现；涉及到多表连接，常用方式为内连接和外连接
>
> 内连接：查两张表有关联得数据，没有关联查不出来，关联是指有个字段为两张表共有且有对应关系【外键】
>
> 左外连接：左边的表作为主表，其中的数据全部查，右边的表作为副表，只查关联数据
>
> 右外连接：右边表作为主表查所有，左边的表只查关联部分
>
> 常用的是内连接和左外连接，由于本课程中可能没有简介或者部分信息，用内连接可能查不出来需要的数据，选择左外连接，课程都查出来，其他的只查关联数据
>
> 查询的数据包括课程id、课程标题、课程价格、课程时长、课程简介、课程讲师名字、课程一级分类、课程二级分类

1. 使用左外连接查询需要的信息的SQL语句

   > 这个sql可以通过减连接次数提升查询效率，在复习sql以后优化,这个sql写在mapper.xml中，且只能通过service中自动注入的对应的baseMapper才能调用，不能通过service调用
   >
   > 取的别名需要和属性名一致
   >
   > xml中的SQL写对了但是找不到对应的方法，显示BindingException，方法not found；这是maven的文件加载机制造成的，maven只会在java目录下去加载java后缀的文件，不会去加载xml后缀和其他后缀的文件，解决方式：
   >
   > + 方式1：将xml文件夹整个复制到对应的mapper包下
   > + 方式2：将xml文件夹放在resources目录下
   > + 方式3：通过配置项进行引入【常用的就是第三种】
   >   + pom.xml中做配置【在pom.xml中规定java目录的xml文件也进行打包】
   >   + 项目application.properties中做配置【配置mp的mapper-locations属性为mapper.xml文件的路径正则表达式】

   ```sql
   <select id="getPublishConfirmCourseInfo" resultType="com.atlisheng.eduservice.entity.bo.course.PublishConfirmCourseInfo">
       SELECT
           ec.id,ec.title,ec.price,ec.course_total_time,ec.cover,
           et.name teacherName,
           es1.title firstLevelSubject,
           es2.title secondLevelSubject
       FROM
           edu_course ec
               LEFT OUTER JOIN
           edu_course_description ecd
           ON
               ec.id=ecd.id
               LEFT OUTER JOIN
           edu_teacher et
           ON
               ec.teacher_id=et.id
               LEFT OUTER JOIN
           edu_subject es1
           ON
               ec.subject_parent_id=es1.id
               LEFT OUTER JOIN
           edu_subject es2
           ON
               ec.subject_id=es2.id
       WHERE
           ec.id=#{}
   </select>
   ```

   【在对应的eduCourseMapper.java中定义出对应的方法】

   > 由动态代理机制自动态实现

   ```java
   public interface EduCourseMapper extends BaseMapper<EduCourse> {
       /**
        * @param courseId
        * @return {@link PublishConfirmCourseInfo }
        * @描述 多表联查课程发布确认信息封装成PublishConfirmCourseInfo响应前端
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/09/23
        * @since 1.0.0
        */
       public PublishConfirmCourseInfo getPublishConfirmCourseInfo(String courseId);
   }
   ```

   【定义封装查询数据的对象】

   ```java
   @Data
   public class PublishConfirmCourseInfo {
       private String id;
       private String title;
       private String cover;
       private Integer courseTotalTime;
       private String firstLevelSubject;
       private String secondLevelSubject;
       private String teacherName;
       private String price;
   }
   ```

   【编写控制器方法】

   ```java
   @GetMapping("getPublishConfirmCourseInfo/{courseId}")
   @ApiOperation("查询课程发布确认信息")
   public ResponseData getPublishConfirmCourseInfo(@ApiParam(name="CourseId",value = "课程ID",required = true) @PathVariable String courseId){
       PublishConfirmCourseInfo publishConfirmCourseInfo=eduCourseService.getPublishConfirmCourseInfo(courseId);
       return ResponseData.responseCall().data("publishConfirmCourseInfo",publishConfirmCourseInfo);
   }
   ```

   【在service中对getPublishConfirmCourseInfo进行实现】

   ```java
   @Override
   public PublishConfirmCourseInfo getPublishConfirmCourseInfo(String courseId) {
       return baseMapper.getPublishConfirmCourseInfo(courseId);
   }
   ```

   【存在mapper.xml无法被maven默认加载机制扫描的问题，解决办法是在模块的pom.xml加上build标签把需要打包的.xml类型文件包含进来，然后在application.properties】
   
   > 【pom.xml配置】
   
   ```xml
   <!-- 项目打包时会将java目录中的*.xml文件也进行打包 -->
   <!--没有这个配置我的xml文件也能被打包编译，但是仍然报错-->
   <build>
       <resources>
           <resource>
               <!--&lt;!&ndash;这个文件夹的内容要进行包含加载，包含的内容为**/*.xml，**表示多层目录，*表示一层目录&ndash;&gt;-->
               <directory>src/main/java</directory>
               <includes>
                   <include>**/*.xml</include>
               </includes>
               <filtering>false</filtering>
           </resource>
       </resources>
   </build>
   ```
   
   > 【在application.properties中对属性进行配置】
   
   ```properties
   #配置mapper.xml文件的路径,classpath为类路径，src目录下，target包下的classes目录
   mybatis-plus.mapper-locations=classpath:com/atlisheng/eduservice/mapper/xml/*.xml
   ```
   
   【前端页面展示】
   
   > 前端组件，从chapter中跳转的路由得到课程id，在页面构建的时候就调用接口获取对应的可成确认信息
   >
   > 发布逻辑是点击发布课程，弹框提示是否发布课程，然后调用接口改状态，跳转课程列表页面显示所有课程列表，课程列表根据normal字段显示已经发布的课程
   >
   > 点击发布以后根据课程id去修改数据库中course的status属性为normal来标记课程的发布状态，draft为未发布状态
   
   ```html
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
   ```
   
   



### 阿里云视频点播服务

1. 阿里云视频点播服务

   + 产品--企业应用--视频点播
   + 开通服务，按流量进行收费，最多花个几毛钱

   + 视频点播VoD：集音视频采集【视频音频录制】、编辑【视频剪辑】、上传【视频存储，本质上是oss存储，用视频点播进行管理】、自动转码【视频分辨率，这个功能要收费】、媒体资源管理【视频资源的分类、增删改】、分发加速【让视频少出现正在缓冲的提示，播放更顺畅，该功能要收费】于一体的一站式音视频点播解决方案【就是视频相关的功能阿里云视频点播都做好了】
   + 和对象存储一样，实际生产都是对资源用java代码对阿里云视频点播进行管理，用代码上传，播放和删除视频

2. 阿里云视频点播管理控制台的使用

   + 媒资库的音/视频可以上传视频，视频会自动生成id和视频资源地址
   + 本质上还是oss进行存储，视频点播不负责存储，可以对视频进行分类管理，即放在不同的文件夹
   + 转码模版组：视频转成高清、超清、或者加密
     + 视频参数讲的太水【hls视频格式是一种加密方式，加密后即使拿到视频地址也不能播放，只有自己知道怎么播放】

3. 用java代码实现视频的阿里云点播视频上传、删除和播放

   + 文档：视频点播--文档&SDK--服务端API、服务端SDK、上传SDK【上传、删除和播放在这里面】
   + 服务端：后端接口
     + 服务端API和服务端SDK是指在java在后端接口中的操作
     + API：阿里云提供一个url地址，只需要调用该固定地址并提供需要的参数【类似于url后问号拼接参数的方式】，就能实现相应的功能，用httpclient技术可以不通过浏览器调用api地址、安卓和ios不使用浏览器而经常使用httpclient技术
     + SDK：通过SDK【软件开发工具包来调用api，也强烈推荐使用这种方式来调用api】，对api的调用方式进行了封装，直接调用其中的类或者接口中的方法来实现功能
   + 客户端：浏览器、安卓、ios

   + 使用javaSDK的流程

     + 安装SDK，即引入依赖

       > 所有的版本号都在顶级父工程中做了约束

       ```xml
       <dependencies>
           <dependency>
               <groupId>com.aliyun</groupId>
               <artifactId>aliyun-java-sdk-core</artifactId>
           </dependency>
           <dependency>
               <groupId>com.aliyun.oss</groupId>
               <artifactId>aliyun-sdk-oss</artifactId>
           </dependency>
           <dependency>
               <groupId>com.aliyun</groupId>
               <artifactId>aliyun-java-sdk-vod</artifactId>
           </dependency>
           <dependency>
               <groupId>com.aliyun</groupId>
               <artifactId>aliyun-sdk-vod-upload</artifactId>
           </dependency>
           <dependency>
               <groupId>com.alibaba</groupId>
               <artifactId>fastjson</artifactId>
           </dependency>
           <dependency>
               <groupId>org.json</groupId>
               <artifactId>json</artifactId>
           </dependency>
           <dependency>
               <groupId>com.google.code.gson</groupId>
               <artifactId>gson</artifactId>
           </dependency>
           <dependency>
               <groupId>joda-time</groupId>
               <artifactId>joda-time</artifactId>
           </dependency>
       </dependencies>
       ```

     + 初始化，创建DefaultAcsClient对象，设置几个参数值【点播服务接入区域regionId=“cn-shanghai”,这个值不能改，因为目前的服务都部署在上海，传参点播服务接入区域、accessKeyId、accessKeySecret【这俩是oss对象存储的id和密钥】获取DefaultAcsClient对象并作为初始化的返回值】

     + 通过得到视频地址对视频进行播放【根据视频id获取，id是存视频返回存在数据库中的】

       > 加密视频获取的地址无法直接播放、实际生产视频是要加密的，避免视频白嫖，数据库中不存储视频地址，存储每个视频的唯一id值，根据视频id可以同时获取视频地址和视频凭证，拿着凭证相当于许可证，既可以播放加密视频，也可以播放非加密视频，id对应数据库的video_source_id
       >
       > 注意手动阿里云视频点播需要启用存储管理，在存储管理中点击启用就可以手动上传视频了
       >
       > 播放加密视频必须要域名，没有域名播放不了，非加密视频阿里云改成不需要域名也可以播放

       ```java
       @Test
       public void testGetVideoPlayInfoList() throws ClientException {
           //初始化客户端、请求对象和相应对象
           DefaultAcsClient client = AliYunVodSDKUtils.initVodClient(accessKeyId,
                   accessKeySecret);
           GetPlayInfoRequest request = new GetPlayInfoRequest();
           GetPlayInfoResponse response = new GetPlayInfoResponse();
           try {
               //设置请求参数
               request.setVideoId("98d5be205c3971eebfbd0675a0ec0102");
               //获取请求响应
               response = client.getAcsResponse(request);
               //输出请求结果
               List<GetPlayInfoResponse.PlayInfo> playInfoList = response.getPlayInfoList();
               for (GetPlayInfoResponse.PlayInfo playInfo: playInfoList) {
                   //获取视频的地址
                   System.out.println("PlayInfo.PlayURL =" + playInfo.getPlayURL()+"\n");
               }
               //获取视频的名称
               System.out.println("VideoBase.Title =" + response.getVideoBase().getTitle()+"\n");
           } catch (Exception e) {
               System.out.print("ErrorMessage = " + e.getLocalizedMessage());
           }
           System.out.print("RequestId = " + response.getRequestId() + "\n");
       }
       ```

     + 获取视频播放凭证【根据视频id获取】

       ```java
       @Test
       public void testGetVideoPlayAuth() throws ClientException {
           //初始化客户端、请求对象和相应对象
           DefaultAcsClient client = AliYunVodSDKUtils.initVodClient(accessKeyId,
                   accessKeySecret);
           GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
           GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
           try {
               //设置请求参数
               request.setVideoId("98d5be205c3971eebfbd0675a0ec0102");
               //获取请求响应
               response = client.getAcsResponse(request);
               //输出请求结果
               //播放凭证
               System.out.print("PlayAuth = " + response.getPlayAuth() + "\n");
               //VideoMeta信息
               System.out.print("VideoMeta.Title = " + response.getVideoMeta().getTitle() + "\n");
           } catch (Exception e) {
               System.out.print("ErrorMessage = " + e.getLocalizedMessage());
           }
           System.out.print("RequestId = " + response.getRequestId() + "\n");
       }
       ```

     + 上传视频到阿里云视频点播服务

       > aliyun-java-vod-upload-1.4.9.jar没有正式开源，maven中下载不到，需要手动将jar包添加到本地仓库中
       >
       > 从阿里云下载对应jar包，在含有该jar包的目录下使用maven命令进行安装`mvn install:install-file -DgroupId=com.aliyun -DartifactId=aliyun-sdk-vod-upload -Dversion=1.4.11 -Dpackaging=jar -Dfile=aliyun-java-vod-upload-1.4.11.jar  `
       >
       > 下载对应的sdk下除了包含jar包的lib目录外，在sample目录下的VODUploadDemo.java中还有视频上传的7种代码示例，比文档讲的更详细，这里使用其中的文件流上传，注释内容暂时都用不着
       >
       > 手动添加这个jar包在第一集引入父工程依赖报错时就手动查资料解决了，并且进行了手动引入，这里并没有报错而且对应的jar包已经存在了

       ```java
       @Test
       public void testUploadVideo() {
           String title="6 - What If I Want to Move Faster-LocalUpload by sdk";//文件上传后阿里云上对应的名字
           String fileName="E:\\JavaStudy\\project\\ol_edu\\resources\\6 - What If I Want to Move Faster.mp4";
           UploadVideoRequest request = new UploadVideoRequest(accessKeyId, accessKeySecret, title, fileName);
       
           /* 可指定分片上传时每个分片的大小，默认为2M字节,将视频分片存储，每个片2M大小，最终组成一个完整视频 */
           request.setPartSize(2 * 1024 * 1024L);
           /* 可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定）*/
           request.setTaskNum(1);
       
           UploadVideoImpl uploader = new UploadVideoImpl();
           UploadVideoResponse response = uploader.uploadVideo(request);
       
           System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
           if (response.isSuccess()) {//这个是判断回调函数是否有返回值
               System.out.print("VideoId=" + response.getVideoId() + "\n");//获取视频的id
           } else {
               /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
               System.out.print("VideoId=" + response.getVideoId() + "\n");
               System.out.print("ErrorCode=" + response.getCode() + "\n");
               System.out.print("ErrorMessage=" + response.getMessage() + "\n");
           }
       }
       ```

       



​		

#### 完善小节添加上传视频功能

1. 后端实现

   + 第一步：引入VOD依赖

     ```xml
     <dependencies>
         <dependency>
             <groupId>com.aliyun</groupId>
             <artifactId>aliyun-java-sdk-core</artifactId>
         </dependency>
         <dependency>
             <groupId>com.aliyun.oss</groupId>
             <artifactId>aliyun-sdk-oss</artifactId>
         </dependency>
         <dependency>
             <groupId>com.aliyun</groupId>
             <artifactId>aliyun-java-sdk-vod</artifactId>
         </dependency>
         <dependency>
             <groupId>com.aliyun</groupId>
             <artifactId>aliyun-sdk-vod-upload</artifactId>
         </dependency>
         <dependency>
             <groupId>com.alibaba</groupId>
             <artifactId>fastjson</artifactId>
         </dependency>
         <dependency>
             <groupId>org.json</groupId>
             <artifactId>json</artifactId>
         </dependency>
         <dependency>
             <groupId>com.google.code.gson</groupId>
             <artifactId>gson</artifactId>
         </dependency>
         <dependency>
             <groupId>joda-time</groupId>
             <artifactId>joda-time</artifactId>
         </dependency>
     </dependencies>
     ```

   + 第二步：创建application.properties设置必要的参数

     ```properties
     # 服务端口
     server.port=8003
     # 服务名
     spring.application.name=service-vod
     # 环境设置： dev、 test、 prod
     spring.profiles.active=dev
     #阿里云 vod
     #不同的服务器，地址不同
     aliyun.vod.file.keyid=LTAI5t8W1kDjtkhA399nYGAh
     aliyun.vod.file.keysecret=yAKCPaOaUtr5ruK4tJpOpfbhld69oy
     # 最大上传单个文件大小：默认1M，这个是tomcat默认大小限制，由tomcat抛出异常
     spring.servlet.multipart.max-file-size=1024MB
     # 最大置总上传的数据大小 ：默认10M
     spring.servlet.multipart.max-request-size=1024MB
     ```

   + 第三步：创建启动类【没有涉及数据源，直接排除掉数据源的自动配置，避免报错】

     ```java
     @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
     @ComponentScan(basePackages = "com.atlisheng")//一定别写成mapperScan了
     public class VodApplication {
         public static void main(String[] args) {
             SpringApplication.run(VodApplication.class,args);
         }
     }
     ```

   + 第四步：创建后端接口【使用流式上传接口】

     【controller】

     ```java
     @RestController
     @CrossOrigin
     @RequestMapping("eduvod/filevod")
     @Api(description = "阿里云视频点播视频管理")
     public class VodController {
         @Autowired
         VodService vodService;
     
         @PostMapping("uploadVideo")
         @ApiOperation("视频文件上传至阿里云VOD")
         public ResponseData uploadVideo(@ApiParam(name="file",value = "视频文件",required = true) MultipartFile file){
             String videoId = vodService.uploadVideoByFile(file);
             return ResponseData.responseCall().data("videoId",videoId);
         }
     }
     ```

     【service】

     ```java
     public interface VodService {
         /**
          * @param file
          * @return {@link String }
          * @描述 以文件的方式上传视频到阿里云视频点播
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/09/27
          * @since 1.0.0
          */
         String uploadVideoByFile(MultipartFile file);
     }
     @Service
     public class VodServiceImpl implements VodService {
         /**
          * @param file
          * @return {@link String }
          * @描述 以文件的方式上传视频到阿里云视频点播
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/09/27
          * @since 1.0.0
          */
         @Override
         public String uploadVideoByFile(MultipartFile file) {
             try {
                 String fileName=file.getOriginalFilename();
                 String title=fileName.substring(0,fileName.lastIndexOf('.'))+" upload by fileAndInputStream";
                 InputStream inputStream=file.getInputStream();
                 UploadStreamRequest request = new UploadStreamRequest(ConstantProperties.ACCESS_KEY_ID, ConstantProperties.ACCESS_KEY_SECRET, title, fileName, inputStream);
                 UploadVideoImpl uploader = new UploadVideoImpl();
                 UploadStreamResponse response = uploader.uploadStream(request);
                 String videoId;
                 if (response.isSuccess()) {
                     videoId = response.getVideoId();
                 } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                     videoId = response.getVideoId();
                 }
                 return videoId;
             } catch (IOException e) {
                 e.printStackTrace();
                 return null;
             }
         }
     }
     ```

     【绑定配置数据，有更好的绑定方式，复习了springboot再改进】

     ```java
     @Component
     public class ConstantProperties implements InitializingBean {
         @Value("${aliyun.vod.file.keyid}")
         private String accessKeyId;
         @Value("${aliyun.vod.file.keysecret}")
         private String accessKeySecret;
     
         //定义公开静态常量,这样可以避免获取上述赋值的注解而进行变更，不安全
         public static String ACCESS_KEY_ID;
         public static String ACCESS_KEY_SECRET;
     
         @Override
         public void afterPropertiesSet() throws Exception {
             ACCESS_KEY_ID=accessKeyId;
             ACCESS_KEY_SECRET=accessKeySecret;
         }
     }
     ```

2. 前端实现

   + 前端视频上传组件

     ```html
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
         :action="BASE_API+'eduvod/filevod/uploadVideo'"
         :limit="1"
         class="upload-demo">
             <el-button size="small" type="primary">上传视频</el-button>
             <!--el-tooltip是给用户的一个友好提示信息，会在上传按钮后面跟一个问号，鼠标悬停在问号上会提示对应信息-->
             <el-tooltip placement="right-end">
                 <div slot="content">
                     最大支持1G， <br>
                     支持3GP、 ASF、 AVI、 DAT、 DV、 FLV、 F4V、 <br>
                     GIF
                     、 M2T、 M4V、 MJ2、 MJPEG、 MKV、 MOV、 MP4、 <br>
                     MPE
                     、 MPG、 MPEG、 MTS、 OGG、 QT、 RM、 RMVB、 <br>
                     SWF
                     、 TS、 VOB、 WMV、 WEBM 等视频格式上传
                 </div>
             <i class="el-icon-question"/>
             </el-tooltip>
         </el-upload>
     </el-form-item>
     ```

   + 涉及方法定义

     ```javascript
     handleVodUploadSuccess(response, file, fileList) {
         this.video.videoSourceId = response.data.videoId
     },
     //视图上传多于一个视频
     handleUploadExceed(files, fileList) {
         this.$message.warning('想要重新上传视频，请先删除已上传的视频')
     },
     ```

   + 添加参数定义

     ```javascript
     fileList: [],//上传文件列表
     BASE_API: process.env.BASE_API // 接口API地址
     ```

3. 配置nginx地址转发规则并设置nginx的上传大小限制修改

   > 配置完nginx后nginx重启nginx -s reload，这个命令偶尔会不好使，用stop和nginx停启最保险，注意配置文件保存后才能生效

   + 配置nginx地址转发规则

     ```json
     location ~ /vod/ {
     	proxy_pass http://localhost:8003;
     }
     ```

   + 配置nginx上传文件大小，否则上传时会有 413 (Request Entity Too Large) 异常，打开nginx主配置文件nginx.conf，找到http{}，添加  

     ```json
     client_max_body_size 1024m;
     ```

4. 添加视频文件原始名称

   + 前端的file.name就可以直接得到视频文件原始名称，不需要后端进行处理返回

     ```javascript
     //成功回调，目前只用到response，主要为了获取videoId，并存入数据库
     handleVodUploadSuccess(response, file, fileList) {
         //阿里云视频点播上返回的videoId赋值
         this.video.videoSourceId = response.data.videoId
         //视频文件原始名称获取和入库
         this.video.videoOriginalName = file.name
     },
     ```

5. 点击删除视频的同时把阿里云视频点播上的视频也删掉【还有bug，不能直接直观看到每个小节下的视频信息】

   > 小节的视频回显功能，必须要把fileList设置成数组，否则框架会不认的，将fileList赋值为如下形式，就能回显数据库中视频的名字
   >
   > ```javascript
   > this.fileList=[{name:this.video.videoOriginalName}]
   > ```

   + 对添加的视频不满意，点击视频文件后面的叉号会直接删除阿里云视频的功能实现

   + 后端service删除阿里云视频点播的实现

     ```java
     public class ALiYunVodUtil {
     
         /**
          * @param accessKeyId
          * @param accessKeySecret
          * @return {@link DefaultAcsClient }
          * @描述 视频点播操作对象的初始化操作，获取DefaultAcsClient对象
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/09/26
          * @since 1.0.0
          */
         public static DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) {
             String regionId = "cn-shanghai"; // 点播服务接入区域
             DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId,
                     accessKeySecret);
             DefaultAcsClient client = new DefaultAcsClient(profile);
             return client;
         }
     }
     /**
      * @param videoId
      * @描述 根据视频ID删除阿里云VOD上的视频
      * @author Earl
      * @version 1.0.0
      * @创建日期 2023/09/28
      * @since 1.0.0
      */
     @Override
     public void removeVodVideo(String videoId) {
         try {
             DefaultAcsClient client = AliyunVodUtil.initVodClient(
                     ConstantProperties.ACCESS_KEY_ID,
                     ConstantProperties.ACCESS_KEY_SECRET);
             DeleteVideoRequest request = new DeleteVideoRequest();
             request.setVideoIds(videoId);
             DeleteVideoResponse response = client.getAcsResponse(request);
             System.out.print("RequestId = " + response.getRequestId() + "\n");
         } catch (ClientException e) {
             e.printStackTrace();
             throw new CustomException(20001,"视频删除失败");
         }
     }
     ```

   + 前端删除方法

     > 有个很严重的bug，用户上传视频到完成这段时间，保存小节信息的按钮处于可以点击的状态，没有时间点来判断视频开始上传和结束，会导致还没获取到videoId就对小节数据更新，导致视频成功上传但是数据库中找不到视频信息，解决办法可以在视频上传成功后单独执行一次小节数据更新【这个解决办法不行，上传过程直接关闭窗口会直接导致上传成功的后续代码不会执行而报执行异常，暂时找不到好的解决办法】

     ```javascript
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
         //点击叉号fileList还没删除确认直接清空了，这里补上,不用补，出现这种情况是因为没有加return，不加return还会将handleVodRemove方法一起执行，加了return一切问题都不会出现
         //this.fileList=[{name:this.video.videoOriginalName}]
         return this.$confirm(`是否永久删除视频【${file.name}】`, '提示', {
             confirmButtonText: '确定',
             cancelButtonText: '取消',
             type: 'warning'
         })
     },
     ```

     

6. 用springCloud实现删除小节删除视频、删除课程删除视频



## 课程列表

### 课程列表显示

> 需求：课程列表上方有课程查询框，课程列表像讲师列表一样，有三个选项，分别是编辑课程基本信息、编辑课程大纲、删除课程，课程列表具有分页展示效果，编辑课程信息跳转课程信息编辑info页面、编辑大纲信息跳转chapter页面

1. 后端实现

   【vo类封装查询条件】

   ```java
   @ApiModel(value="课程多条件带分页查询对象",description = "课程查询条件封装")
   @Data
   public class CourseQueryFactor {
       @ApiModelProperty(value = "课程名称")
       private String title;
       @ApiModelProperty(value = "讲师id")
       private String teacherId;
       @ApiModelProperty(value = "一级类别id")
       private String subjectParentId;
       @ApiModelProperty(value = "二级类别id")
       private String subjectId;
       @ApiModelProperty(value = "课程发布状态")
       private String status;
   }
   ```

   【后端控制器多条件分页查询方法】

   ```java
   @PostMapping("pageFactorCourse/{current}/{limit}")
   @ApiOperation(value = "课程多条件组合带分页查询")
   public ResponseData findFactorCoursePaging(@ApiParam(name = "current",value = "当前页",required = true) @PathVariable Integer current,
                                               @ApiParam(name = "limit",value = "每页记录条数",required = true) @PathVariable Integer limit,
                                               @ApiParam(name = "courseQueryFactor",value = "讲师筛选条件") @RequestBody(required = false) CourseQueryFactor courseQueryFactor){//@RequestBody将json数据封装到对应的对象中
       Page<EduCourse> coursePage = new Page<>(current,limit);
       QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
       String title = courseQueryFactor.getTitle();
       String teacherId = courseQueryFactor.getTeacherId();
       String subjectParentId = courseQueryFactor.getSubjectParentId();
       String subjectId = courseQueryFactor.getSubjectId();
       String status = courseQueryFactor.getStatus();
       queryWrapper.orderByDesc("gmt_Create");
       if (!StringUtils.isEmpty(title)){
           queryWrapper.like("title",title);
       }
       if (!StringUtils.isEmpty(teacherId)){
           queryWrapper.eq("teacher_id",teacherId);
       }
       if (!StringUtils.isEmpty(subjectParentId)){
           queryWrapper.eq("subject_parent_id",subjectParentId);
       }
       if (!StringUtils.isEmpty(subjectId)){
           queryWrapper.le("subject_id",subjectId);
       }
       if (!StringUtils.isEmpty(status)){
           queryWrapper.le("status",status);
       }
       eduCourseService.page(coursePage,queryWrapper);
       return ResponseData.responseCall().data("total",coursePage.getTotal()).data("course",coursePage.getRecords());
   }
   ```

2. 前端实现

   【前端接口定义】

   ```javascript
   //课程列表，课程条件分页查询，current为当前页，limit为每页记录数，teacherQuery为条件对象
   findAllCoursePaging(current,limit,courseQuery){
       return request({
           url: `/eduservice/course/pageFactorCourse/${current}/${limit}`,//带条件查询和不带条件查询一定要区分清楚，两者请求方式都不同，即使加了跨域请求注解还是会报错没有跨域请求权限
           method: 'post',
           //courseQuery是查询条件对象，后端使用@RequestBody注解获取数据需要前端传入json数据，data属性对应对象会自动将对象转成json格式传入接口
           data: courseQuery
       })
   }
   ```

   【前端页面组件】

   ```html
   <template>
       <div class="app-container">
           <h2>课程列表</h2>
           <!--:inline表示所有的内容在一行内展示-->
           <el-form :inline="true" class="demo-form-inline">
               <!--课程标题查询条件-->
               <el-form-item>
                   <el-input v-model="courseQuery.title" placeholder="课程标题"/>
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
           <el-table :data="courses" border fit highlight-current-row >
               <el-table-column label="序号" width="70" align="center">
                   <template slot-scope="scope">
                       {{ (page - 1) * limit + scope.$index + 1 }}
                   </template>
               </el-table-column>
               <el-table-column label="课程信息" width="240" align="center">
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
               <el-table-column label="课时" width="125" align="center">
                   <template slot-scope="scope">
                       {{ scope.row.courseTotalTime }}
                   </template>
               </el-table-column>
               <el-table-column label="浏览量" width="125" align="center">
                   <template slot-scope="scope">
                       {{ scope.row.viewCount }}
                   </template>
               </el-table-column>
               <el-table-column label="课程讲师" width="125" align="center">
                   <template slot-scope="scope">
                       {{ scope.row.teacherId }}
                   </template>
               </el-table-column>
               <el-table-column label="创建时间" width="150" align="center">
                   <template slot-scope="scope">
                       {{ scope.row.gmtCreate.substr(0, 10) }}
                   </template>
               </el-table-column>
               <el-table-column label="更新时间" width="150" align="center">
                   <template slot-scope="scope">
                       {{ scope.row.gmtModified.substr(0, 10) }}
                   </template>
               </el-table-column>
               <el-table-column label="课程价格" width="125" align="center" >
                   <template slot-scope="scope">
                       {{ Number(scope.row.price) === 0 ? '免费' : '¥' + scope.row.price.toFixed(2) }}
                   </template>
               </el-table-column>
               <el-table-column prop="buyCount" label="购买数量" width="100" align="center">
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
                       <el-button type="text" size="mini" icon="el-icon-delete">删除</el-button>
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
                       console.log(this.courses)
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
   ```
   

> 需要对课程列表进行优化，手动写sql语句解析查询条件多表连接查询并显示课程简介和讲师姓名

### 课程删除

> 课程删除户把视频小节、章节、描述信息和课程本身都删除掉
>
> 外键：一对多多的哪一方创建字段作为外键指向一的哪一方的主键，开发中不建议把外键生命出来，原因：1.外键必须要保持数据一致性，如果外键存在那么对应主键的记录是删不掉的，有外键要按顺序先删小节、章节、描述，再删课程
>
> 有很多操作都应该加事务的，但是都没有加

1. 后端接口

   【控制器方法：根据课程id删除课程】

   ```java
   @DeleteMapping("deleteCourse/{courseId}")
   @ApiOperation("根据课程id删除课程")
   public ResponseData deleteCourse(@ApiParam(name="CourseId",value = "课程ID",required = true) @PathVariable String courseId){
       eduCourseService.removeCourseByCourseId(courseId);
       return ResponseData.responseCall();
   }
   ```

   【service的实现】

   > 根据课程id删除课程小节【还要删除对应视频文件、后面再讲】
   >
   > 根据课程id删除课程描述，具体封装很简单，用queryWrapper封装一下就行，没什么好说的
   >
   > 根据课程id删除课程描述
   >
   > 根据课程id删除课程本身
   >
   > 需要把小节、章节、描述的service注入进课程service

   ```java
   @Override
   public void removeCourseByCourseId(String courseId) {
       if(eduVideoService.removeByCourseId(courseId) && eduChapterService.removeByCourseId(courseId) && eduCourseDescriptionService.removeById(courseId) && removeById(courseId)){
           return;
       }
       throw new CustomException(20001,"课程删除失败");
   }
   ```

   



### 

# 微服务架构

## Nacos服务注册中心

> 使用feign对服务进行调用

父工程，子模块。子模块下有多个子子模块，每个子子模块的占用端口又各不相同，

+ 微服务是一种架构风格，每个服务都运行在独立的进程中，互不影响干扰，使用轻量级机制通信，通常为HTTP API，每个服务都有自己特定的功能，每个服务都可以单独进行部署在某台服务器上，为了扩展性更强、负载更合理、部署方便，代码量少，而且解决问题更方便，直接看哪个功能出了问题
  + 微服务每个模块都能使用独立的数据存储服务，比如一个用redis、一个用mysql
  + 单体服务只能写成一种语言、微服务的各个模块可以使用不同的语言进行实现【项目外包，把一些非核心模块外包给使用另一种语言的团队】
  + 结构上松耦合、功能上为统一的整体
  + 不适合使用微服务的项目：非常底层的业务、如操作系统内核、存储系统、网络系统、数据库系统
+ 常用的微服务框架
  + SpringCloud、Dubbo【出现比较早，很多公司还在用】、Dropwizard、Consul、etcd、
+ 早期所有的东西放在一起【一个进程？】，是单体应用，早期的web开发就是这样独立软件工具的堆砌，扩展性差、可靠性低、维护成本高

1. SpringCloud

   + 不是一种一种技术、是一系列框架的集合，使用这些框架就能实现微服务架构，利用Spring Boot的开发便利性简化了分布式系统基础设施的开发，选取成熟经得起考验的服务框架如服务发现、服务注册、配置中心、消息总线、负载均衡、 熔断器、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。  

   + 使用springCloud需要依赖于springboot技术，springboot实质上就是快速构建spring的脚手架工具，springCloud的开发代号【都是地铁站名】必须和springBoot的版本号严格对应

     + springCloud有些小版本：

       > 有稳定版本优先用稳定版本，没有GA的情况下用SR版本，SR版本没有用M版本，不要用快照版

       + SNAPSHOT： 快照版本，是不稳定的，随时可能被修改
       + M： MileStone， M1表示第1个里程碑版本【实现预定目标的版本】，一般同时标注PRE，表示预览版版。
       + SR： Service Release， SR1表示第1个正式版本，一般同时标注GA： (GenerallyAvailable),表示稳定版
         本。

   + Spring Cloud相关基础服务组件

     + 服务发现——Netflix Eureka （Nacos）

       > Eureka出现了一些瓶颈，换成了nacos。服务发现就是注册中心

     + 服务调用——Netflix Feign

     + 熔断器——Netflix Hystrix

     + 服务网关——Spring Cloud GateWay

     + 分布式配置——Spring Cloud Config （Nacos）

     + 消息总线 —— Spring Cloud Bus （Nacos） 

     

     

2. Nacos

   > edu模块对vod模块中的方法进行调用实现对小节视频的删除功能，区分于引入项目依赖和通过HTTP API传递数据，即服务间的相互调用
   >
   > Nacos 是阿里巴巴构建云原生应用的动态服务发现、配置管理和服务管理平台。 Nacos 致力于帮助您发现、配置和管理微服务。 Nacos 能快速实现动态服务发现、服务配置、服务元数据及流量管理。 快捷构建、交付和管理微服务平台。Nacos同时相当于服务发现和分布式配置，除此外还可以实现消息总线
   >
   > 早期springCloud使用的Eureka，后来遇到性能瓶颈，更新代价高，就被替换了
   >
   > zookeeper也是常见的注册中心，GO是Consul
   >
   > 注意如果服务中添加了Nacos依赖但是没有配置nacos地址，服务就无法启动起来

   + 在删除小节的edu服务中的方法中调用vod服务中删除视频的方法

     + 第一步：将edu和vod两个服务在注册中心进行注册
       + 注册中心：类比于房产中介，将房产在注册中心做登记，再想租户介绍

   + Nacos的执行流程

     + Nacos由三个部分构成、Nacos注册中心、消费者【调用方法】、生产者【提供方法】
       + 被调用的服务的都是生产者，调用服务就是消费者，两个组件都要在注册中心进行注册，注册的基本逻辑是使用ip和端口号进行注册
       + 消费者在注册中心中得到消费者的ip和端口号，使用这俩对生产者进行调用

   + 安装nacos

     + 使用的是nacos1.1.4版本，不要选用beta版本，beta版本是公测版本，就是让大家帮他测试，里面有问题再完善，下载windows版本，下载地址：https://github.com/alibaba/nacos/releases  
     + 运行就是解压压缩包，运行windows版本下的startup.cmd文件即可，最好使用命令`startup -m standalone`设置成standalone模式【单击版本启动，表示非集群的方式启动】
     + nacos的访问：使用http://localhost:8848/nacos直接进行访问，默认端口就是8848，默认用户名密码都是nacos，进入后服务管理下的服务列表就会显示当前已经注册了的服务

   + 使用nacos对服务进行注册

     > Nacos注册中心会显示服务在配置文件中的name属性配置的服务名称，注意服务名称可以用短横杠，但是不要使用下划线

     + 在service模块中引入nacos客户端的pom依赖，因为service中的模块都需要进行注册，所以直接在service的pom文件引入
     + 在要进行注册的服务的application.properties文件中配置Nacos地址对应的属性server-addr【即ip地址和端口号，不用加http】
     + 在微服务启动类上添加@EnableDiscoveryClient注解，添加Nacos客户端注解，表示注册该springBoot应用

   + 对服务进行调用

     + 对服务进行调用需要使用Feign【由Netflix网飞开发，springCloud很多组件都由该公司开发】、Feign是一个声明式、模板化的HTTP客户端、可以快捷优雅的调用HTTP API，Spring Cloud Feign对Feign进行了增强，使Feign支持了Spring MVC注解，并整合了Ribbon和Eureka，从而让Feign的使用更加方便。整合了Spring Cloud Ribbon和Spring Cloud Hystrix，
       除了提供这两者的强大功能外，还提供了一种声明式的Web服务客户端定义的方式。能够帮助我们定义和实现依赖服务接口的定义。在Spring Cloud feign的实现下，只需要创建一个接口并用注解方式配置它，即可完成服务提供方的接口绑定，简化了使用Spring Cloud Ribbon时自行封装服务调用客户端的开发量  

   + 使用Feign对服务进行调用

     + 第一步：引入Feign的依赖openFeign

     + 第二步：在调用端edu中创建client包和XXXClient接口写服务调用代码

       @FeignClient注解用于指定从哪个服务中调用功能 ，名称【value属性】与被调用的服务名保持一致。【配置文件中配置的服务名】
       @GetMapping注解用于对被调用的微服务进行地址映射【类似于前端定义接口的地址写法】
       @PathVariable注解一定要指定参数名称，否则出错【也是value属性】
       @Component注解防止，在其他位置注入CodClient时idea报错  

       ```java
       /**
        * @author Earl
        * @version 1.0.0
        * @描述 远程调用vod服务中的方法
        * @创建日期 2023/09/29
        * @since 1.0.0
        */
       @FeignClient("service-vod")
       @Component
       public interface VodClient {
           /**
            * @param videoId
            * @描述 edu通过Feign远程调用vod服务中的删除小节视频方法
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/09/29
            * @since 1.0.0
            */
           @DeleteMapping("/eduvod/filevod/removeVodVideo/{videoId}")
           public void removeVodVideo(@PathVariable("videoId") String videoId);
       }
       ```

     + 第三步：在调用端的启动类上加上@EnableFeignClients注解

     + 第四步：在调用端对应的方法中对服务中的方法进行调用

       + 向方法所在类注入对应的XXXClient接口，通过该对象调用对应的方法即可，底层细节相当于是向被调用端发起请求远程调用对应的方法

         ```java
         /**
          * @param id
          * @return {@link ResponseData }
          * @描述 删除课程小节，如果存在视频则远程调用vod服务删除对应的视频
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/09/29
          * @since 1.0.0
          */
         @DeleteMapping("deleteVideo/{id}")
         @ApiOperation("删除课程小节")
         public ResponseData deleteVideo(@ApiParam(name="videoId",value = "课程小节ID",required = true)@PathVariable String id){
             //根据小节id查询出小节视频的ID，判断非空串远程调用vod的删除视频方法并传参视频ID
             String videoSourceId = eduVideoService.getById(id).getVideoSourceId();
             if (!StringUtils.isEmpty(videoSourceId)) {
                 vodClient.removeVodVideo(videoSourceId);
             }
             return eduVideoService.removeById(id)?ResponseData.responseCall():ResponseData.responseErrorCall();
         }
         ```

3. 使用微服务架构的实现删除课程删除多个视频

   + 删除多个视频可以给request传入多个id【需要传递用逗号分割的多个id的字符串】，可以以list集合的方式传入一个数组，StringUtils.join(list.toArray,","),是apache.common.lang包下的方法,返回值是String，相当于将list集合转成数组然后遍历用“,”分割拼接成字符串；Java8中的方法String.join(",",list)也能直接达到同样的效果

   + 第一步：用list集合封装前端传入的多个id

     【controller层】

     > 确定一下@RequestParam("videoIdList")的用法

     ```java
     @DeleteMapping("removeVodVideoByIds")
     @ApiOperation("根据多个视频id批量删除视频")
     public ResponseData removeVodVideoByIds(@ApiParam(name="videoIds",value = "批量视频ID",required = true)@RequestParam("videoIdList") List<String> videoIdList){
         vodService.removeVodVideoByIds(videoIdList);
         return ResponseData.responseCall().message("视频删除成功");
     }
     ```

     【service层】

     ```java
     /**
      * @param videoIdList
      * @描述 根据多个视频id批量删除视频
      * @author Earl
      * @version 1.0.0
      * @创建日期 2023/09/30
      * @since 1.0.0
      */
     @Override
     public void removeVodVideoByIds(List<String> videoIdList) {
         try {
             DefaultAcsClient client = ALiYunVodUtil.initVodClient(
                     ConstantProperties.ACCESS_KEY_ID,
                     ConstantProperties.ACCESS_KEY_SECRET);
             DeleteVideoRequest request = new DeleteVideoRequest();
             String videoIdsString = String.join(",", videoIdList);
             request.setVideoIds(videoIdsString);
             DeleteVideoResponse response = client.getAcsResponse(request);
             System.out.print("RequestId = " + response.getRequestId() + "\n");
         } catch (ClientException e) {
             e.printStackTrace();
             throw new CustomException(20001,"视频删除失败");
         }
     }
     ```

   + 第二步：给VOD的对应api的delete请求的request传参多个id，删除方法和删除单个的方法是一样的，id可以由服务调用方查数据库查出来，queryWrapper中查询指定字段的selecrt(“字段名”)，查出来还是会封装成对应对象的list集合，而不是对应字段的list集合【注意视频id可能为null，要加非空判断，空的记录没必要放入list集合】

     ```java
     /**
      * @param courseId
      * @描述 根据课程id删除课程信息，包括小节、章节、课程描述、课程本身
      * @author Earl
      * @version 1.0.0
      * @创建日期 2023/09/25
      * @since 1.0.0
      */
     @Override
     public void removeCourseByCourseId(String courseId) {
         //获取该课程下的所有视频ID
         QueryWrapper<EduVideo> queryWrapper=new QueryWrapper<>();
         queryWrapper.eq("course_id",courseId);
         queryWrapper.select("video_source_id");
         List<EduVideo> eduVideoList = eduVideoService.list(queryWrapper);
         List<String> videoIdList=new ArrayList<>();
         eduVideoList.forEach(eduVideo -> {
             String videoSourceId = eduVideo.getVideoSourceId();
             if(!StringUtils.isEmpty(videoSourceId)){
                 videoIdList.add(videoSourceId);
             }
         });
         if (videoIdList.size()>0){
             vodClient.removeVodVideoByIds(videoIdList);
         }
         if(eduVideoService.removeByCourseId(courseId) && eduChapterService.removeByCourseId(courseId) && eduCourseDescriptionService.removeById(courseId) && removeById(courseId)){
             return;
         }
         throw new CustomException(20001,"课程删除失败");
     }
     ```

   + 第三步：在VodClient接口中定义vod服务中删除多个视频的方法，注意也要list集合非空判断，如果list集合为空`list.size()`就不用调用对应的删除视频方法了

     ```java
     @FeignClient("service-vod")
     @Component
     public interface VodClient {
         /**
          * @param videoId
          * @描述 edu通过Feign远程调用vod服务中的删除小节视频方法
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/09/29
          * @since 1.0.0
          */
         @DeleteMapping("/eduvod/filevod/removeVodVideo/{videoId}")
         public void removeVodVideo(@PathVariable("videoId") String videoId);
     
         /**
          * @param videoIds
          * @描述 根据多个视频id批量删除视频
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/09/30
          * @since 1.0.0
          */
         @DeleteMapping("/eduvod/filevod/removeVodVideoByIds")
         public void removeVodVideoByIds(@RequestParam("videoIdList") List<String> videoIdList);
     }
     ```

     > 已测试，删除功能完全没问题



## Hystrix熔断器

1. SpringCloud执行过程中组件的调用流程

   > 消费者：edu、生产者：vod

   + Feign-->Hystrix-->Ribbon-->Http Client
     + Feign:

   + 第一步：定义接口化请求调用：编写接口【如VodClient】，指定调用服务的名字和调用方法的路径，抽象方法
   + 第二步：服务开始调用，执行Feign组件，找到服务的名字和方法地址，根据服务名字和地址对方法进行调用
   + 第三步：Hystrix：断路器、熔断器；调用方法的过程去检验对应服务是否能调用，能调用继续执行，调用不了服务挂掉了就执行熔断机制，目的是保护系统
   + 第四步：Ribbon，做负载均衡，把请求均衡分担到多个服务器中
   + 第五步：Http Client，真正根据服务和方法路径真正去调用对应的服务，做真实的http通信请求

2. Hystrix

   > 供分布式系统使用，提供延迟和容错功能，保证分布式系统错误情况下的弹性
   >
   > 分布式：把项目的不同服务部署在不同的服务器上，不同的服务加在一起构成完整的项目就构成了分布式系统
   >
   > 应用场景是系统中某些服务不稳定，使用这些服务的用户线程可能会发生阻塞、如果没有隔离机制，整个系统可能会挂掉

   熔断机制【有几种方式】：

   + 服务器宕机情况下的处理：某个服务的服务器宕机，当该服务再被其他服务调用时Hystrix不会再调用宕机的服务器，hystrix执行fallback把对应服务器从系统中踢出去
   + 响应过慢情况下的处理：服务调用请求本身有等待返回结果的时间，超时就认为请求失败；被调用者有时存在服务器没有宕机，但是相应时间很慢的情况，熔断器可以设置当遇到请求很慢的情况下允许调用者在响应很慢的情况下的延迟等待

3. 在项目中整合springCloud Hystrix熔断器

   + 引入Hystrix依赖
   + 在配置文件中添加Hystrix配置【开启熔断机制【默认是false】和设置hystrix超时时间，默认时间是1000ms，可以自定义设置，该时间内的响应都不会提示超时】
   + 编写一个远程调用接口的实现类【vodClient的实现类】，在远程调用失败后会自动执行实现类中的方法，比如抛出错误信息；同时需要再VodClient的@FeignClient注解的fallback属性上指定实现类的.class类型对象，实现类也需要加@Component注解





# 前台系统

## 项目前台系统搭建

### NUXT

对比于vue-admin-template框架，前台用这个框架，这是一个服务端渲染技术

SEO：网站中出现关键词的数量更多在页面展示的次序更靠前，由于ajax是异步请求，在搜索引擎爬虫抓取工具扫描完网站关键词之前异步请求的响应还没有展示出来，导致搜索的网站排序和关键字的匹配度降低【即异步请求的ajax不利于SEO】

NUXT服务端渲染技术在服务端将以上问题解决，客户端只做数据的显示，不进行其他处理；客户端发送请求给服务器，服务器中包含了tomcat和多出一个Nodejs，tomcat得到数据然后被tomcat处理封装，然后发给客户端进行展示；NUXT是nodejs的一个框架，在服务端对数据进行渲染然后将数据返回给客户端

### NUXT框架的安装运行

1. 获取NEXT框架的压缩文件starter-template-master，解压将template的内容复制到前台目录中，将后台系统的.eslintrc.js配置文件复制到前台目录根路径下

   > 【这不行的，课件里面写的有问题，直接拷贝NUST框架template中的.eslintrc.js，eslint的检查规则很严格，有没有空行空格，id属性在class属性前面都会检查，不对就过不了编译，这里有错误改代码就酸爽了，后台系统是禁用eslint格式检查所以编译没报错，前台教程是直接用NUXT框架中的eslint配置文件，改了以后就没有报错了】

2. 修改package.json的name、description、author

   ```json
   "name": "guli",
   "version": "1.0.0",
   "description": "谷粒学院前台网站",
   "author": "Helen <55317332@qq.com>",
   ```

3. 修改nuxt.config.js  

   > 这里的设置最后会显示在页面标题栏和meta数据中  

   ```json
   head: {
       title: '谷粒学院 - Java视频|HTML5视频|前端视频|Python视频|大数据视频-自学拿1万+月薪的IT在线视频课程，谷粉力挺，老学员为你推荐',
       meta: [
       { charset: 'utf-8' },
       { name: 'viewport', content: 'width=device-width, initial-scale=1' },
       { hid: 'keywords', name: 'keywords', content: '谷粒学院,IT在线视频教程,Java视频,HTML5视频,前端视频,Python视频,大数据视频' },
       { hid: 'description', name: 'description', content: '谷粒学院是国内领先的IT在线视频学习平台、职业教育平台。截止目前,谷粒学院线上、线下学习人次数以万计！会同上百个知名开发团队联合制定的Java、 HTML5前端、大数据、 Python等视频课程，被广大学习者及IT工程师誉为：业界最适合自学、代码量最大、案例最多、实战性最强、技术最前沿的IT系列视频课程！ ' }
       ],
       link: [
       	{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
       ]
   },
   ```

4. 在根目录使用`npm install`安装依赖，在根目录下使用`npm run dex`测试运行

   > 出现占用多少内存的提示就表示应用启动成功

   

#### NUXT目录结构

> NUXT框架本身只是基于VUE，并没有基于element-ui；而后台管理系统的vue-admin是同时基于VUE和Element-ui实现的

+ .nuxt目录

  > 前端中代码编译的文件，该文件是在项目运行后自动生成的文件

+ 资源目录 assets

  > 用于组织未编译的静态资源如CSS、JS、img、 LESS、 SASS 或 JavaScript。

+ 组件目录 components

  > 放项目中用到的相关组件，比如富文本编辑器
  >
  > 用于组织应用的 Vue.js 组件。 Nuxt.js 不会扩展增强该目录下 Vue.js 组件，即这些组件不会像页面组件那样有 asyncData 方法的特性。

+ 布局目录 layouts

  > 其中的default.vue设置了网页怎么布局，布局比如网站的头、中、尾；头有点像路由、中是头部下的内容展示、尾是一些联系方式、版权信息、友情连接等；在页面中的头和尾就放在这个文件中，中间的信息放在pages目录的index.vue文件中【名师、热门课程和幻灯片在index页面】
  >
  > 加载页面时先去加载default、再去引入index，是用nuxt中的<nuxt/>标签和html中的iframe标签引入的

+ middleware

  > 该目录下放一些相关组件

+ node_module

  > 放下载的依赖

+ 页面目录 pages

  > 这里面放项目中的具体页面，其中的index.vue就是首页面
  >
  > 用于组织应用的路由及视图。 Nuxt.js 框架读取该目录下所有的 .vue 文件并自动生成对应的路由配置。

+ 插件目录 plugins

  > 用于组织那些需要在 根vue.js应用 实例化之前需要运行的 Javascript 插件。

+  nuxt.config.js 文件

  > nuxt框架的核心文件
  >
  > nuxt.config.js 文件用于组织Nuxt.js 应用的个性化配置，以便覆盖默认配置。  







### 首页

#### 轮播图【幻灯片】

> 也叫banner，使用一个幻灯片插件vue-awesome-swiper，使用npm install vue-awesome-swiper@3.1.3

1. 安装幻灯片插件

2. 配置幻灯片插件

   > 在 plugins 文件夹下新建文件 nuxt-swiper-plugin.js，内容是  

   ```js
   import Vue from 'vue'//引入vue
   import VueAwesomeSwiper from 'vue-awesome-swiper/dist/ssr'//引入幻灯片插件
   Vue.use(VueAwesomeSwiper)//vue使用幻灯片插件
   ```

   > 在 nuxt.config.js 文件中配置插件
   > 将 plugins 和 css节点 复制到 module.exports节点下  

   ```js
   module.exports = {
       // some nuxt config...
       plugins: [
       	{ src: '~/plugins/nuxt-swiper-plugin.js', ssr: false }
       ],
       css: [
       	'swiper/dist/css/swiper.css'
       ]
   }
   ```

3. 复制项目中使用的静态资源到asset目录

   > 资料中页面原型下的asset目录复制到nuxt项目的asset目录下，包括css样式，项目中用到的图片，js等内容；实际生产中这些静态资源都是由美工制作好的

4. 从课件复制代码到default.vue文件下

   > 文件中代码有头信息和尾信息，中间的nuxt是引入其他的页面

5. 从课件复制首页面到pages中的index.vue页面

   > 后续将这个页面的数据更改为静态页面的效果

6. 整合幻灯片

   > 幻灯片放在index页面中，目前幻灯片只有手动切换功能，没有自动切换功能

   复制幻灯片代码到index.vue，复制幻灯片的切换组件到index.vue，幻灯片的原文件在photo的banner中



#### 首页数据banner显示

> 幻灯片或者轮播图，新建banner微服务cms【content management system】，注意如果在mp的mapper.xml文件中写sql需要在pom.xml中配置builder设置xml可以被打包

1. 后端构建
   1. 创建cms项目
   2. 配置application.properties
   3. 创建轮播图对应的数据库表，使用mp的代码生成器生成后端框架代码

2. 编写后端轮播图操作接口，
   + controller设置成后台和前台使用的控制器，不使用默认设置
   + 后台banner控制器方法
     + banner不带条件分页查询
     + 增加banner
     + 根据修改banner
     + 根据id删除banner
     + 根据id查询banner的方法
   + 前台banner控制器方法
     + 查询所有banner【幻灯片显示数据不需要分页，这个自己封装一个方法，为了后续加redis方便】

3. 用户前台轮播图数据的展示操作

   + NUXT框架本身没有带axios组件，需要使用命令`npm install axios@0.19.2`先下载axios组件

   + 参考后台管理系统utils/request.js对ajax请求进行封装,axios的baseURL要写成nginx的地址

     > 由于之前的框架对axios进行了封装，返回的是response.data；所以只需要写一个response.data.XXX就能获取数据，这里需要两个data才能获取数据

     ```js
     methods:{
         init(){
           this.getBannerList()
         },
         getBannerList(){
           banner.getBannerList()
           .then(response=>{
             this.bannerList=response.data.data.bannerList
             console.log(this.bannerList)
           })
         },
     }
     ```

     【轮播图显示组件】

     ```html
     <div>
         <!-- 幻灯片 开始 -->
         <div v-swiper:mySwiper="swiperOption">
         <div class="swiper-wrapper">
             <div v-for="banner in bannerList" :key="banner.id" class="swiper-slide" style="background: #040B1B;">
                 <a target="_blank" :href="banner.linkUrl">
                     <img width="100%" :src="banner.imageUrl" :alt="banner.title">
                 </a>
             </div>
         </div>
         <div class="swiper-pagination swiper-pagination-white"></div>
         <div class="swiper-button-prev swiper-button-white" slot="button-prev"></div>
         <div class="swiper-button-next swiper-button-white" slot="button-next"></div>
     </div>
     <!-- 幻灯片 结束 -->
     ```

     > 同理搞出首页热门课程和热门讲师数据的遍历
     >
     > 1. 根据课程的浏览量进行查询，前台显示前8个热门课程，可以查询按浏览量排序的前八个
     > 2. Sql语句：根据id进行降序排列，显示排序之后的前八条记录【可以根据浏览量排序，不要根据视频讲的id排序】
     >    + 核心一个orderByDesc和一个last方法拼接sql，由于课程和讲师都在EDU模块中，在edu模块写代码，在cms模块中进行调用

   + ：key是对数据遍历过程中每个组件的key标识，常用id进行标识，alt属性有两种情况，第一种情况是将鼠标移至图片上显示alt中的信息，第二种情况是src地址的图片没有了就会显示alt的内容，两种情况需要看是哪一种浏览器，点击图片跳转超链接，连接地址是banner属性的linkURL

4. 实现后台管理员对轮播图的操作







### NUXT中的路由操作

> 路由：类似与菜单，可以跳转页面

1. 固定路由

   + 路由路径是固定的，不发生变化的，

     > 该项目固定路由的位置在default.vue中

     ```html
     <!--router-link 的to属性设置路由跳转地址，Nuxt的路由跳转规则是跳转路径为/course，会在pages中找course文件夹，在course文件夹中去找index.vue；同样会拼接default.vue和course/index.vue的内容-->
     <router-link to="/" tag="li" active-class="current" exact>
       <a>首页</a>
     </router-link>
     ```

   

2. 动态路由

   + 路由路径是动态变化的，路由后面比如跟一个/id属性，这个属性值是动态变化的，比如课程详情

     > NUXT的动态路由是以下划线开头的vue文件，参数名为下划线后边的文件名【实际不是必要，只是一种规范】，如course/id是参数名，pages/course/_id.vue就是对应的页面详情

3. 整合课程列表、课程详情、讲师列表、讲师详情

   + 整合课程列表

     > pages/course/index.vue

     ```vue
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
                       <a title="全部" href="#">全部</a>
                     </li>
                     <li>
                       <a title="数据库" href="#">数据库</a>
                     </li>
                     <li class="current">
                       <a title="外语考试" href="#">外语考试</a>
                     </li>
                     <li>
                       <a title="教师资格证" href="#">教师资格证</a>
                     </li>
                     <li>
                       <a title="公务员" href="#">公务员</a>
                     </li>
                     <li>
                       <a title="移动开发" href="#">移动开发</a>
                     </li>
                     <li>
                       <a title="操作系统" href="#">操作系统</a>
                     </li>
                   </ul>
                 </dd>
               </dl>
               <dl>
                 <dt>
                   <span class="c-999 fsize14"></span>
                 </dt>
                 <dd class="c-s-dl-li">
                   <ul class="clearfix">
                     <li>
                       <a title="职称英语" href="#">职称英语</a>
                     </li>
                     <li>
                       <a title="英语四级" href="#">英语四级</a>
                     </li>
                     <li>
                       <a title="英语六级" href="#">英语六级</a>
                     </li>
                   </ul>
                 </dd>
               </dl>
               <div class="clear"></div>
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
                   <li>
                     <a title="关注度" href="#">关注度</a>
                   </li>
                   <li>
                     <a title="最新" href="#">最新</a>
                   </li>
                   <li class="current bg-orange">
                     <a title="价格" href="#">价格&nbsp;
                       <span>↓</span>
                     </a>
                   </li>
                 </ol>
               </section>
             </div>
             <div class="mt40">
               <!-- /无数据提示 开始-->
               <section class="no-data-wrap">
                 <em class="icon30 no-data-ico">&nbsp;</em>
                 <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
               </section>
               <!-- /无数据提示 结束-->
               <article class="comm-course-list">
                 <ul class="of" id="bna">
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img src="~/assets/photo/course/1442295592705.jpg" class="img-responsive" alt="听力口语">
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a href="/course/1" title="听力口语" class="course-title fsize18 c-333">听力口语</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">9634人学习</i>
                           |
                           <i class="c-999 f-fA">9634评论</i>
                         </span>
                       </section>
                     </div>
                   </li>
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img src="~/assets/photo/course/1442295581911.jpg" class="img-responsive" alt="Java精品课程">
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a href="/course/1" title="Java精品课程" class="course-title fsize18 c-333">Java精品课程</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">501人学习</i>
                           |
                           <i class="c-999 f-fA">501评论</i>
                         </span>
                       </section>
                     </div>
                   </li>
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img src="~/assets/photo/course/1442295604295.jpg" class="img-responsive" alt="C4D零基础">
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a href="/course/1" title="C4D零基础" class="course-title fsize18 c-333">C4D零基础</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">300人学习</i>
                           |
                           <i class="c-999 f-fA">300评论</i>
                         </span>
                       </section>
                     </div>
                   </li>
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img
                           src="~/assets/photo/course/1442302831779.jpg"
                           class="img-responsive"
                           alt="数学给宝宝带来的兴趣"
                         >
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a href="/course/1" title="数学给宝宝带来的兴趣" class="course-title fsize18 c-333">数学给宝宝带来的兴趣</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">256人学习</i>
                           |
                           <i class="c-999 f-fA">256评论</i>
                         </span>
                       </section>
                     </div>
                   </li>
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img
                           src="~/assets/photo/course/1442295455437.jpg"
                           class="img-responsive"
                           alt="零基础入门学习Python课程学习"
                         >
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a
                           href="/course/1"
                           title="零基础入门学习Python课程学习"
                           class="course-title fsize18 c-333"
                         >零基础入门学习Python课程学习</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">137人学习</i>
                           |
                           <i class="c-999 f-fA">137评论</i>
                         </span>
                       </section>
                     </div>
                   </li>
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img
                           src="~/assets/photo/course/1442295570359.jpg"
                           class="img-responsive"
                           alt="MySql从入门到精通"
                         >
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a href="/course/1" title="MySql从入门到精通" class="course-title fsize18 c-333">MySql从入门到精通</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">125人学习</i>
                           |
                           <i class="c-999 f-fA">125评论</i>
                         </span>
                       </section>
                     </div>
                   </li>
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img src="~/assets/photo/course/1442302852837.jpg" class="img-responsive" alt="搜索引擎优化技术">
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a href="/course/1" title="搜索引擎优化技术" class="course-title fsize18 c-333">搜索引擎优化技术</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">123人学习</i>
                           |
                           <i class="c-999 f-fA">123评论</i>
                         </span>
                       </section>
                     </div>
                   </li>
                   <li>
                     <div class="cc-l-wrap">
                       <section class="course-img">
                         <img src="~/assets/photo/course/1442295379715.jpg" class="img-responsive" alt="20世纪西方音乐">
                         <div class="cc-mask">
                           <a href="/course/1" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                       </section>
                       <h3 class="hLh30 txtOf mt10">
                         <a href="/course/1" title="20世纪西方音乐" class="course-title fsize18 c-333">20世纪西方音乐</a>
                       </h3>
                       <section class="mt10 hLh20 of">
                         <span class="fr jgTag bg-green">
                           <i class="c-fff fsize12 f-fA">免费</i>
                         </span>
                         <span class="fl jgAttr c-ccc f-fA">
                           <i class="c-999 f-fA">34人学习</i>
                           |
                           <i class="c-999 f-fA">34评论</i>
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
                 <a class="undisable" title>首</a>
                 <a id="backpage" class="undisable" href="#" title>&lt;</a>
                 <a href="#" title class="current undisable">1</a>
                 <a href="#" title>2</a>
                 <a id="nextpage" href="#" title>&gt;</a>
                 <a href="#" title>末</a>
                 <div class="clear"></div>
               </div>
             </div>
             <!-- 公共分页 结束 -->
           </section>
         </section>
         <!-- /课程列表 结束 -->
       </div>
     </template>
     <script>
     	export default {};
     </script>
     ```

     > pages/course/_id.vue

     ```vue
     <template>
         <div id="aCoursesList" class="bg-fa of">
         <!-- /课程详情 开始 -->
         <section class="container">
             <section class="path-wrap txtOf hLh30">
             <a href="#" title class="c-999 fsize14">首页</a>
             \
             <a href="#" title class="c-999 fsize14">课程列表</a>
             \
             <span class="c-333 fsize14">Java精品课程</span>
             </section>
             <div>
             <article class="c-v-pic-wrap" style="height: 357px;">
                 <section class="p-h-video-box" id="videoPlay">
                 <img src="~/assets/photo/course/1442295581911.jpg" alt="Java精品课程" class="dis c-v-pic">
                 </section>
             </article>
             <aside class="c-attr-wrap">
                 <section class="ml20 mr15">
                 <h2 class="hLh30 txtOf mt15">
                     <span class="c-fff fsize24">Java精品课程</span>
                 </h2>
                 <section class="c-attr-jg">
                     <span class="c-fff">价格：</span>
                     <b class="c-yellow" style="font-size:24px;">￥0.00</b>
                 </section>
                 <section class="c-attr-mt c-attr-undis">
                     <span class="c-fff fsize14">主讲： 唐嫣&nbsp;&nbsp;&nbsp;</span>
                 </section>
                 <section class="c-attr-mt of">
                     <span class="ml10 vam">
                     <em class="icon18 scIcon"></em>
                     <a class="c-fff vam" title="收藏" href="#" >收藏</a>
                     </span>
                 </section>
                 <section class="c-attr-mt">
                     <a href="#" title="立即观看" class="comm-btn c-btn-3">立即观看</a>
                 </section>
                 </section>
             </aside>
             <aside class="thr-attr-box">
                 <ol class="thr-attr-ol clearfix">
                 <li>
                     <p>&nbsp;</p>
                     <aside>
                     <span class="c-fff f-fM">购买数</span>
                     <br>
                     <h6 class="c-fff f-fM mt10">150</h6>
                     </aside>
                 </li>
                 <li>
                     <p>&nbsp;</p>
                     <aside>
                     <span class="c-fff f-fM">课时数</span>
                     <br>
                     <h6 class="c-fff f-fM mt10">20</h6>
                     </aside>
                 </li>
                 <li>
                     <p>&nbsp;</p>
                     <aside>
                     <span class="c-fff f-fM">浏览数</span>
                     <br>
                     <h6 class="c-fff f-fM mt10">501</h6>
                     </aside>
                 </li>
                 </ol>
             </aside>
             <div class="clear"></div>
             </div>
             <!-- /课程封面介绍 -->
             <div class="mt20 c-infor-box">
             <article class="fl col-7">
                 <section class="mr30">
                 <div class="i-box">
                     <div>
                     <section id="c-i-tabTitle" class="c-infor-tabTitle c-tab-title">
                         <a name="c-i" class="current" title="课程详情">课程详情</a>
                     </section>
                     </div>
                     <article class="ml10 mr10 pt20">
                     <div>
                         <h6 class="c-i-content c-infor-title">
                         <span>课程介绍</span>
                         </h6>
                         <div class="course-txt-body-wrap">
                         <section class="course-txt-body">
                             <p>
                             Java的发展历史，可追溯到1990年。当时Sun&nbsp;Microsystem公司为了发展消费性电子产品而进行了一个名为Green的项目计划。该计划
                             负责人是James&nbsp;Gosling。起初他以C++来写一种内嵌式软件，可以放在烤面包机或PAD等小型电子消费设备里，使得机器更聪明，具有人工智
                             能。但他发现C++并不适合完成这类任务！因为C++常会有使系统失效的程序错误，尤其是内存管理，需要程序设计师记录并管理内存资源。这给设计师们造成
                             极大的负担，并可能产生许多bugs。&nbsp;
                             <br>为了解决所遇到的问题，Gosling决定要发展一种新的语言，来解决C++的潜在性危险问题，这个语言名叫Oak。Oak是一种可移植性语言，也就是一种平台独立语言，能够在各种芯片上运行。
                             <br>1994年，Oak技术日趋成熟，这时网络正开始蓬勃发展。Oak研发小组发现Oak很适合作为一种网络程序语言。因此发展了一个能与Oak配合的浏
                             览器--WebRunner，后更名为HotJava，它证明了Oak是一种能在网络上发展的程序语言。由于Oak商标已被注册，工程师们便想到以自己常
                             享用的咖啡(Java)来重新命名，并于Sun&nbsp;World&nbsp;95中被发表出来。
                             </p>
                         </section>
                         </div>
                     </div>
                     <!-- /课程介绍 -->
                     <div class="mt50">
                         <h6 class="c-g-content c-infor-title">
                         <span>课程大纲</span>
                         </h6>
                         <section class="mt20">
                         <div class="lh-menu-wrap">
                             <menu id="lh-menu" class="lh-menu mt10 mr10">
                             <ul>
                                 <!-- 文件目录 -->
                                 <li class="lh-menu-stair">
                                 <a href="javascript: void(0)" title="第一章" class="current-1">
                                     <em class="lh-menu-i-1 icon18 mr10"></em>第一章
                                 </a>
                                 <ol class="lh-menu-ol" style="display: block;">
                                     <li class="lh-menu-second ml30">
                                     <a href="#" title>
                                         <span class="fr">
                                         <i class="free-icon vam mr10">免费试听</i>
                                         </span>
                                         <em class="lh-menu-i-2 icon16 mr5">&nbsp;</em>第一节
                                     </a>
                                     </li>
                                     <li class="lh-menu-second ml30">
                                     <a href="#" title class="current-2">
                                         <em class="lh-menu-i-2 icon16 mr5">&nbsp;</em>第二节
                                     </a>
                                     </li>
                                 </ol>
                                 </li>
                             </ul>
                             </menu>
                         </div>
                         </section>
                     </div>
                     <!-- /课程大纲 -->
                     </article>
                 </div>
                 </section>
             </article>
             <aside class="fl col-3">
                 <div class="i-box">
                 <div>
                     <section class="c-infor-tabTitle c-tab-title">
                     <a title href="javascript:void(0)">主讲讲师</a>
                     </section>
                     <section class="stud-act-list">
                     <ul style="height: auto;">
                         <li>
                         <div class="u-face">
                             <a href="#">
                             <img src="~/assets/photo/teacher/1442297969808.jpg" width="50" height="50" alt>
                             </a>
                         </div>
                         <section class="hLh30 txtOf">
                             <a class="c-333 fsize16 fl" href="#">周杰伦</a>
                         </section>
                         <section class="hLh20 txtOf">
                             <span class="c-999">毕业于北京大学数学系</span>
                         </section>
                         </li>
                     </ul>
                     </section>
                 </div>
                 </div>
             </aside>
             <div class="clear"></div>
             </div>
         </section>
         <!-- /课程详情 结束 -->
         </div>
     </template>
     
     <script>
         export default {};
     </script>
     ```

     > pages/teacher/index.vue

     ```vue
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
                 <!-- /无数据提示 开始-->
                 <section class="no-data-wrap">
                 <em class="icon30 no-data-ico">&nbsp;</em>
                 <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
                 </section>
                 <!-- /无数据提示 结束-->
                 <article class="i-teacher-list">
                 <ul class="of">
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="姚晨" target="_blank">
                             <img src="~/assets/photo/teacher/1442297885942.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="姚晨" target="_blank" class="fsize18 c-666">姚晨</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span class="fsize14 c-999">北京师范大学法学院副教授、清华大学法学博士。自2004年至今已有9年的司法考试培训经验。长期从事司法考试辅导，深知命题规律，了解解题技巧。内容把握准确，授课重点明确，层次分明，调理清晰，将法条法理与案例有机融合，强调综合，深入浅出。</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">北京师范大学法学院副教授</p>
                         </div>
                     </section>
                     </li>
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="谢娜" target="_blank">
                             <img src="~/assets/photo/teacher/1442297919077.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="谢娜" target="_blank" class="fsize18 c-666">谢娜</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span class="fsize14 c-999">十年课程研发和培训咨询经验，曾任国企人力资源经理、大型外企培训经理，负责企业大学和培训体系搭建；曾任专业培训机构高级顾问、研发部总监，为包括广东移动、东莞移动、深圳移动、南方电网、工商银行、农业银行、民生银行、邮储银行、TCL集团、清华大学继续教育学院、中天路桥、广西扬翔股份等超过200家企业提供过培训与咨询服务，并担任近50个大型项目的总负责人。</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">资深课程设计专家，专注10年AACTP美国培训协会认证导师</p>
                         </div>
                     </section>
                     </li>
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="刘德华" target="_blank">
                             <img src="~/assets/photo/teacher/1442297927029.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="刘德华" target="_blank" class="fsize18 c-666">刘德华</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span class="fsize14 c-999">上海师范大学法学院副教授、清华大学法学博士。自2004年至今已有9年的司法考试培训经验。长期从事司法考试辅导，深知命题规律，了解解题技巧。内容把握准确，授课重点明确，层次分明，调理清晰，将法条法理与案例有机融合，强调综合，深入浅出。</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">上海师范大学法学院副教授</p>
                         </div>
                     </section>
                     </li>
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="周润发" target="_blank">
                             <img src="~/assets/photo/teacher/1442297935589.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="周润发" target="_blank" class="fsize18 c-666">周润发</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span class="fsize14 c-999">法学博士，北京师范大学马克思主义学院副教授，专攻毛泽东思想概论、邓小平理论，长期从事考研辅导。出版著作两部，发表学术论文30余篇，主持国家社会科学基金项目和教育部重大课题子课题各一项，参与中央实施马克思主义理论研究和建设工程项目。</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">考研政治辅导实战派专家，全国考研政治命题研究组核心成员。</p>
                         </div>
                     </section>
                     </li>
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="钟汉良" target="_blank">
                             <img src="~/assets/photo/teacher/1442298121626.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="钟汉良" target="_blank" class="fsize18 c-666">钟汉良</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span class="fsize14 c-999">具备深厚的数学思维功底、丰富的小学教育经验，授课风格生动活泼，擅长用形象生动的比喻帮助理解、简单易懂的语言讲解难题，深受学生喜欢</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">毕业于师范大学数学系，热爱教育事业，执教数学思维6年有余</p>
                         </div>
                     </section>
                     </li>
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="唐嫣" target="_blank">
                             <img src="~/assets/photo/teacher/1442297957332.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="唐嫣" target="_blank" class="fsize18 c-666">唐嫣</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span class="fsize14 c-999">中国科学院数学与系统科学研究院应用数学专业博士，研究方向为数字图像处理，中国工业与应用数学学会会员。参与全国教育科学“十五”规划重点课题“信息化进程中的教育技术发展研究”的子课题“基与课程改革的资源开发与应用”，以及全国“十五”科研规划全国重点项目“掌上型信息技术产品在教学中的运用和开发研究”的子课题“用技术学数学”。</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">中国人民大学附属中学数学一级教师</p>
                         </div>
                     </section>
                     </li>
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="周杰伦" target="_blank">
                             <img src="~/assets/photo/teacher/1442297969808.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="周杰伦" target="_blank" class="fsize18 c-666">周杰伦</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span class="fsize14 c-999">中教一级职称。讲课极具亲和力。</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">毕业于北京大学数学系</p>
                         </div>
                     </section>
                     </li>
                     <li>
                     <section class="i-teach-wrap">
                         <div class="i-teach-pic">
                         <a href="/teacher/1" title="陈伟霆" target="_blank">
                             <img src="~/assets/photo/teacher/1442297977255.jpg" alt>
                         </a>
                         </div>
                         <div class="mt10 hLh30 txtOf tac">
                         <a href="/teacher/1" title="陈伟霆" target="_blank" class="fsize18 c-666">陈伟霆</a>
                         </div>
                         <div class="hLh30 txtOf tac">
                         <span
                             class="fsize14 c-999"
                         >政治学博士、管理学博士后，北京师范大学马克思主义学院副教授。多年来总结出了一套行之有效的应试技巧与答题方法，针对性和实用性极强，能帮助考生在轻松中应考，在激励的竞争中取得高分，脱颖而出。</span>
                         </div>
                         <div class="mt15 i-q-txt">
                         <p class="c-999 f-fA">长期从事考研政治课讲授和考研命题趋势与应试对策研究。考研辅导新锐派的代表。</p>
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
                 <a href="#" title="首页">首</a>
                 <a href="#" title="前一页">&lt;</a>
                 <a href="#" title="第1页" class="current undisable">1</a>
                 <a href="#" title="第2页">2</a>
                 <a href="#" title="后一页">&gt;</a>
                 <a href="#" title="末页">末</a>
                 <div class="clear"></div>
                 </div>
             </div>
             <!-- 公共分页 结束 -->
             </section>
         </section>
         <!-- /讲师列表 结束 -->
         </div>
     </template>
     <script>
         export default {};
     </script>
     ```

     > pages/teacher/_id.vue

     ```vue
     <template>
         <div id="aCoursesList" class="bg-fa of">
         <!-- 讲师介绍 开始 -->
         <section class="container">
             <header class="comm-title">
             <h2 class="fl tac">
                 <span class="c-333">讲师介绍</span>
             </h2>
             </header>
             <div class="t-infor-wrap">
             <!-- 讲师基本信息 -->
             <section class="fl t-infor-box c-desc-content">
                 <div class="mt20 ml20">
                 <section class="t-infor-pic">
                     <img src="~/assets/photo/teacher/1442297885942.jpg">
                 </section>
                 <h3 class="hLh30">
                     <span class="fsize24 c-333">姚晨&nbsp;高级讲师</span>
                 </h3>
                 <section class="mt10">
                     <span class="t-tag-bg">北京师范大学法学院副教授</span>
                 </section>
                 <section class="t-infor-txt">
                     <p
                     class="mt20"
                     >北京师范大学法学院副教授、清华大学法学博士。自2004年至今已有9年的司法考试培训经验。长期从事司法考试辅导，深知命题规律，了解解题技巧。内容把握准确，授课重点明确，层次分明，调理清晰，将法条法理与案例有机融合，强调综合，深入浅出。</p>
                 </section>
                 <div class="clear"></div>
                 </div>
             </section>
             <div class="clear"></div>
             </div>
             <section class="mt30">
             <div>
                 <header class="comm-title all-teacher-title c-course-content">
                 <h2 class="fl tac">
                     <span class="c-333">主讲课程</span>
                 </h2>
                 <section class="c-tab-title">
                     <a href="javascript: void(0)">&nbsp;</a>
                 </section>
                 </header>
                 <!-- /无数据提示 开始-->
                 <section class="no-data-wrap">
                 <em class="icon30 no-data-ico">&nbsp;</em>
                 <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
                 </section>
                 <!-- /无数据提示 结束-->
                 <article class="comm-course-list">
                 <ul class="of">
                     <li>
                     <div class="cc-l-wrap">
                         <section class="course-img">
                         <img src="~/assets/photo/course/1442295455437.jpg" class="img-responsive" >
                         <div class="cc-mask">
                             <a href="#" title="开始学习" target="_blank" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                         </section>
                         <h3 class="hLh30 txtOf mt10">
                         <a href="#" title="零基础入门学习Python课程学习" target="_blank" class="course-title fsize18 c-333">零基础入门学习Python课程学习</a>
                         </h3>
                     </div>
                     </li>
                     <li>
                     <div class="cc-l-wrap">
                         <section class="course-img">
                         <img src="~/assets/photo/course/1442295472860.jpg" class="img-responsive" >
                         <div class="cc-mask">
                             <a href="#" title="开始学习" target="_blank" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                         </section>
                         <h3 class="hLh30 txtOf mt10">
                         <a href="#" title="影想力摄影小课堂" target="_blank" class="course-title fsize18 c-333">影想力摄影小课堂</a>
                         </h3>
                     </div>
                     </li>
                     <li>
                     <div class="cc-l-wrap">
                         <section class="course-img">
                         <img src="~/assets/photo/course/1442302831779.jpg" class="img-responsive" >
                         <div class="cc-mask">
                             <a href="#" title="开始学习" target="_blank" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                         </section>
                         <h3 class="hLh30 txtOf mt10">
                         <a href="#" title="数学给宝宝带来的兴趣" target="_blank" class="course-title fsize18 c-333">数学给宝宝带来的兴趣</a>
                         </h3>
                     </div>
                     </li>
                     <li>
                     <div class="cc-l-wrap">
                         <section class="course-img">
                         <img src="~/assets/photo/course/1442295506745.jpg" class="img-responsive" >
                         <div class="cc-mask">
                             <a href="#" title="开始学习" target="_blank" class="comm-btn c-btn-1">开始学习</a>
                         </div>
                         </section>
                         <h3 class="hLh30 txtOf mt10">
                         <a href="#" title="国家教师资格考试专用" target="_blank" class="course-title fsize18 c-333">国家教师资格考试专用</a>
                         </h3>
                     </div>
                     </li>
                 </ul>
                 <div class="clear"></div>
                 </article>
             </div>
             </section>
         </section>
         <!-- /讲师介绍 结束 -->
         </div>
     </template>
     <script>
     export default {};
     </script>
     ```

     

### Redis缓存首页数据

> 能够提升数据查询的效率

1. redis回顾

   + 基于key-value的方式进行存储，读和写的速度可观【内存读取速度快】，支持多种数据结构【看笔记】

   + 支持持久化【数据可以存入硬盘】

   + 支持过期时间【设置数据的过期时间】和事务

   + 支持消息订阅

   + 做内存和缓存数据库，一般把高频访问不频繁修改且不重要【如钱】的数据放入redis 

     > 面试题：redis集群搭建
     >
     > redis和memcache的区别：memcache不支持持久化

2. SpringBoot整合redis

   + 第一步

   > 在common包下整合redis依赖starter-data-redis和commons-pool2【这个是redis的连接池】

   ```xml
   <!-- redis -->
   <dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   <!-- spring2.X集成redis所需common-pool2-->
   <dependency>
   <groupId>org.apache.commons</groupId>
   <artifactId>commons-pool2</artifactId>
   <version>2.6.0</version>
   </dependency>
   ```

   + 第二步

     > 在service_base模块下创建RedisConfig，即redis配置类，里面两个插件，一个做缓存，一个做缓存管理，写法都是固定的
     >
     > redisTemplate做redis缓存操作
     >
     > CacheManager主要做一些类型转换，数据过期时间等

     ```java
     @Configuration
     @EnableCaching
     public class RedisConfig extends CachingConfigurerSupport {
         @Bean
         public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
             RedisTemplate<String, Object> template = new RedisTemplate<>();
             RedisSerializer<String> redisSerializer = new StringRedisSerializer();
             Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
             ObjectMapper om = new ObjectMapper();
             om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
             om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
             jackson2JsonRedisSerializer.setObjectMapper(om);
             template.setConnectionFactory(factory);
             //key序列化方式
             template.setKeySerializer(redisSerializer);
             //value序列化
             template.setValueSerializer(jackson2JsonRedisSerializer);
             //value hashmap序列化
             template.setHashValueSerializer(jackson2JsonRedisSerializer);
             return template;
         }
         @Bean
         public CacheManager cacheManager(RedisConnectionFactory factory) {
             RedisSerializer<String> redisSerializer = new StringRedisSerializer();
             Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
             //解决查询缓存转换异常的问题
             ObjectMapper om = new ObjectMapper();
             om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
             om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
             jackson2JsonRedisSerializer.setObjectMapper(om);
             // 配置序列化（解决乱码的问题） ,过期时间600秒
             RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                             .entryTtl(Duration.ofSeconds(600))
                             .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                             .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                             .disableCachingNullValues();
             RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                     .cacheDefaults(config)
                     .build();
             return cacheManager;
         }
     }
     ```

   + 第三步

     > 基于SpringBoot缓存注解使用redis进行数据的redis缓存，这个用RedisConfig中的redisTemplate插件也能做到
     >
     > SpringBoot三种常用缓存注解：【注解可以加在service中对应的方法上】
     >
     > + @Cacheable【一般用于查询方法，对方法返回结果进行缓存，下次请求如果缓存中有就查询缓存，如果缓存没有就执行方法并把结果存入缓存】
     >   + key、value属性字面意思，key和value都是自定义的，共同构成数据的名字，这里key用的首页数据，value用的bannerList，key在双引号间还要就加单引号，否则可能会有问题，都显示绿色则没问题
     >   + 这个注解还可以加在控制器方法上，一样没有毛病
     > + @CachePut【该注解标注方法每次执行都会查数据库，并将数据存入数据库，其他方法可以直接从缓存中拿该数据，一般用在新增记录方法上】,这个key和value中间加两个冒号共同构成redis中的key。value在前，key在后
     >   + key，value属性
     > + @CacheEvict【该注解标注的方法会清空对应指定的缓存，一般用在更新和删除的方法上】
     >   + allEntries属性设置为true，方法执行完会立即清空对应value名的缓存

   + 第四步

     > 改造首页banner接口，把数据加入redis缓存

     在方法上添加@Cacheable注解并设置key和value即可

     > 控制器方法上加也没问题，而且是远程调用的

     ```java
     /**
      * @return {@link ResponseData }
      * @描述 获取最受欢迎的八门课程
      * @author Earl
      * @version 1.0.0
      * @创建日期 2023/10/03
      * @since 1.0.0
      */
     @GetMapping("getPopularCourse")
     @ApiOperation("获取最受欢迎的八门课程")
     @Cacheable(value = "indexData",key="'hotCourseList'")
     public ResponseData getPopularCourse(){
         return eduServiceClient.queryPopularCourse();
     }
     
     /**
      * @return {@link ResponseData }
      * @描述 获取最高资历的四位讲师
      * @author Earl
      * @version 1.0.0
      * @创建日期 2023/10/03
      * @since 1.0.0
      */
     @GetMapping("getPopularTeacher")
     @ApiOperation("获取最高资历的四位讲师")
     @Cacheable(value = "indexData",key="'TeacherList'")
     public ResponseData getPopularTeacher(){
         return eduServiceClient.queryPopularTeacher();
     }
     ```

     > SERVICE中加也没毛病

     ```java
     @Override
     @Cacheable(key="'bannerList'",value = "indexData")
     public List<EduBanner> queryBannerList() {
         List<EduBanner> bannerList = list(null);
         return bannerList;
     }
     ```

     

   + 第五步

     > 启动redis

     + 虚拟机装redis并启动redis
     + xshell连接虚拟机，找到redis.conf配置文件，在该目录下用`./redis-server /etc/redis.confg `启动redis服务，使用`./redis-cli`对redis进行本地链接，出现端口号就是启动成功了
       + redis必记命令
         + keys *【查询所有key】
         + get `key`【通过key获取值】

     + 用windows访问linux上的redis服务需要

       + 关闭linux防火墙或者开放redis对应的端口

       + 修改redis配置文件

         > 注意修改完redis配置文件需要重启，重启命令`ps -ef | grep redis `，查看redis命令进程；`kill -9 3259`杀进程，然后启动命令启动，把redis的密码和端口改掉

         + 注释掉`bind 127.0.0.1`【如果不注释掉这句话只能通过本地访问，windows是访问不了的】
         + 如果IDEA报错redis是protected-mode，需要修改配置文件`protected-mode yes`为`protected-mode no`【保护模式不允许远程访问】

   + 第六步：在service-cms模块配置文件添加redis配置

     ```properties
     #配置redis相关信息
     #Redis服务器地址，写虚拟机的ip地址
     spring.redis.host=192.168.200.132
     #Redis服务器连接端口
     spring.redis.port=6173
     #Redis数据库索引（默认为0）
     spring.redis.database= 0
     #连接超时时间（毫秒）
     spring.redis.timeout=1800000
     #连接池最大连接数（使用负值表示没有限制）
     spring.redis.lettuce.pool.max-active=20
     #最大阻塞等待时间(负数表示没限制)
     spring.redis.lettuce.pool.max-wait=-1
     #连接池中的最大空闲连接
     spring.redis.lettuce.pool.max-idle=5
     #连接池中的最小空闲连接
     spring.redis.lettuce.pool.min-idle=0
     ```

     

     

     

## 登录注册功能

> 单一服务器模式登录，所有的程序部署在一台tomcat中，使用session存储用户登录成功后的数据，session中可以获取用户数据说明已经登录，一台服务器这种方式很适合，session.setAttribute("key",value)，session.getAttribute("key")；
>
> + session默认过期时间：默认是30分钟不做任何操作
>
> 分布式服务器集群部署分摊访问压力，扩展方便
>
> 单点登录：SSO【single sign on】模式在任何一个服务模块登录后，其他所有模块登录后都不需要登录，可以直接进行访问，比如百度在贴吧登录后，在图片、文库等都不需要再次登录，可以直接访问，分布式必用登录方式
>
> 单点登录的三种常见方式：
>
> 第二种和第三种用的最多，有时选择使用，有时混合使用
>
> + session的广播机制实现【session复制，单个服务器模块登录后session存入用户信息，然后将session对象复制到各个模块中；致命缺点：项目中模块太多，session复制很耗时间、空间，极其浪费算力和存储空间；是一种互联网早期的机制】
> + cookie+redis实现
>   + cookie是客户端技术，存在浏览器中，每次请求都会带cookie；redis读取速度快，基于k-v做存储；
>   + 实现过程：在项目任何一个模块做登录，登录后将数据放入cookie和redis，在redis中的value放用户数据，在key中放入生成的唯一值【一般是用户ip或者用户的id或者uuid】，将redis中生成的key放入cookie中
>   + 每次访问携带cookie，在服务中获取cookie，拿着cookie到redis根据key查询，查询到有数据就是已经登录，再严谨一点，拿着数据到数据库进行验证，验证成功就是已登录，不成功就是未登录
> + token实现
>   + token是按一定规则生成字符串，token也叫令牌，字符串可以包含用户信息，这种字符串叫自包含令牌，如ip#username#职位#头像#...，将该字符串做一个base64编码，然后做一个加密
>   + 实现过程：
>     + 第一步：单点登录后生成一个包含用户信息特定规则的字符串，将字符串通过cookie或者地址栏返回
>     + 第二步：每次访问模块，地址栏带该字符串，访问模块对地址栏中的字符串解码获取用户信息，可以获取到就是已登录，获取不到就是未登录

### 后端接口

1. 注册接口
   + 整合jwt【json web token】

     + JWT：是一种通用的自包含令牌，规定好了生成字符串的规则，里面可以包含用户信息，JWT的规则比较完善，用的比较广

       > token是按一定规则生成的字符串，包含用户信息，但是规则每个公司都不一定，一般采用通用的，如JWT
       >
       > JWT生成的字符串包含3个部分，用'.'进行划分
       >
       > + 第一部分：JWT头信息【编码方式alg:'HS256'；token类型'typ':'JWT'】，一般是json对象，只是经过base64编码后变成字符串
       >
       > + 第二部分：有效载荷，JWT主体内容部分，一个json对象，七个默认字段
       >
       >   > 除了默认字段外，还可以自定义私有字段，用户名，是否管理员等等，但是注意默认情况下JWT是未加密的，不要放隐私保密信息，防止信息泄露，用户信息就可以放在这部分
       >
       >   + iss：发行人
       >   + exp：到期时间
       >   + sub：主题
       >   + aud：用户
       >   + nbf：在此之前不可用
       >   + iat：发布时间
       >   + jti： JWT ID用于标识该JWT  
       >
       > + 第三部分：签名哈希
       >
       >   + 自定义一个密码，该密码仅保存在服务器中且不向用户公开，使用HS256算法按以下公式计算签名哈希【HS256算法是加密算法】
       >
       >     > 通过签名哈希可以验证该字符串是否由我方服务器生成，可以作为一种防伪标志，claims就是有效载荷
       >
       >     $$
       >     HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(claims), secret)
       >     $$
       >
       > 三个部分JWT头、有效载荷、签名哈希和"."共同组成整个JWT对象【注意头和有效载荷都是base64编码】

     + jwt的优缺点：

       + 减少服务器请求数据库的次数、可包含用户头像、id、昵称等信息且存储在客户端、减少查库和服务器内存消耗
       + 默认不加密，不加密情况下无法存储私密数据、但是可以对原始令牌进行加密
       + 最大的缺点是服务器不保存会话状态，所以在使用期间不可能取消令牌或更改令牌的权限。也就是说，一旦JWT签发，在有效期内将会一直有效 
       + 为了减少敏感信息盗用和窃取，第一令牌有效期不能设置过长、第二重要操作前还是要对身份进行验证、第三不建议使用HTTP协议进行传输、建议使用加密的HTTPS协议进行传输

     + 整合流程

       > 在common模块引入主要为了其他模块都能用到

       + 在common模块引入JWT依赖jjwt

         ```xml
         <dependencies>
             <!-- JWT-->
             <dependency>
                 <groupId>io.jsonwebtoken</groupId>
                 <artifactId>jjwt</artifactId>
             </dependency>
         </dependencies>
         ```

       + 在common模块创建JWT工具类：直接复制代码

         ```java
         /**
          * @author Earl
          * @version 1.0.0
          * @描述 这里面的静态方法都会用在用户登录验证中
          * @创建日期 2023/10/04
          * @since 1.0.0
          */
         public class JwtUtils {
         
             //定义两个常量
             public static final long EXPIRE = 1000 * 60 * 60 * 24;//EXPIRE是token过期时间，这里是1天
             public static final String APP_SECRET = "zAhmndMQBR3769PQISABsPy30XPHKG";//服务器存储做签名哈希的密码
         
             /**
              * @param id 用户id
              * @param nickname 用户昵称
              * @return {@link String }
              * @描述 生成token字符串的方案，传入用户id和昵称生成JWT令牌，还可以传入其他的信息
              * @author Earl
              * @version 1.0.0
              * @创建日期 2023/10/04
              * @since 1.0.0
              */
             public static String getJwtToken(String id, String nickname){
                 //生成JWT令牌
                 String JwtToken = Jwts.builder()
                         //设置JWT头信息
                         .setHeaderParam("typ", "JWT")
                         .setHeaderParam("alg", "HS256")
         
                         //设置分类，名字随便起
                         .setSubject("vpc-ol-user")
                         //设置当前时间
                         .setIssuedAt(new Date())
                         //设置过期时间为当前时间加上期望值
                         .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
         
                         //设置token的主体部分，用户信息，多个可以加多行
                         .claim("id", id)
                         .claim("nickname", nickname)
         
                         //签名哈希，加密方式和密钥
                         .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                         .compact();
                 return JwtToken;
             }
         
             /**
              * @param jwtToken
              * @return boolean
              * @描述 判断token是否存在与有效，伪造的也会返回false，这个是直接传入token字符串
              * @author Earl
              * @version 1.0.0
              * @创建日期 2023/10/04
              * @since 1.0.0
              */
             public static boolean checkToken(String jwtToken) {
                 if(StringUtils.isEmpty(jwtToken)) return false;//如果token为空直接返回false
                 try {
                     Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);//使用密钥判断token是否是有效的，有异常就不是有效的
                 } catch (Exception e) {
                     e.printStackTrace();
                     return false;
                 }
                 return true;//没有异常是有效的就返回true
             }
         
             /**
              * @param request
              * @return boolean
              * @描述 判断token是否存在与有效，这个和上面的方法的区别是传递的是请求参数
              * @author Earl
              * @version 1.0.0
              * @创建日期 2023/10/04
              * @since 1.0.0
              */
             public static boolean checkToken(HttpServletRequest request) {
                 try {
                     String jwtToken = request.getHeader("token");//从请求中获取头信息token
                     if(StringUtils.isEmpty(jwtToken)) return false;
                     Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
                 } catch (Exception e) {
                     e.printStackTrace();
                     return false;
                 }
                 return true;
             }
         
             /**
              * @param request
              * @return {@link String }
              * @描述 根据request对象得到token，通过token获取用户id以及其他信息
              * @author Earl
              * @version 1.0.0
              * @创建日期 2023/10/04
              * @since 1.0.0
              */
             public static String getMemberIdByJwtToken(HttpServletRequest request) {
                 String jwtToken = request.getHeader("token");
                 if(StringUtils.isEmpty(jwtToken)) return "";
                 Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);//解析token
                 Claims claims = claimsJws.getBody();//获取主体部分
                 return (String)claims.get("id");//获取用户id
             }
         }
         ```

   + 阿里云短信服务

     + 创建service_msm模块
     
     + 搭建SpringBoot应用基本结构
     
     + 开通阿里云短信服务--进入管理控制台--国内短信--申请签名管理和模板管理【模板管理是发送短信的模板，点击添加模板、模板名字要有实际意义，申请说明也要有实际意义，等待审核；签名管理是为了发送信息，只有签名通过才能发送验证码，模板通过模板code进行使用】
     
       > 注意阿里云发送验证码的码值还是由自家服务器生成的，把验证码传递给阿里云进行发送，验证码用自定义随机数工具类RandomUtil生成，这个工具类直接复制
     
       ```java
       public class RandomUtil {
           private static final Random random = new Random();
       
           private static final DecimalFormat fourdf = new DecimalFormat("0000");
       
           private static final DecimalFormat sixdf = new DecimalFormat("000000");
       
           /**
            * @return {@link String }
            * @描述 随机生成4位验证码
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/10/04
            * @since 1.0.0
            */
           public static String getFourBitRandom() {
               return fourdf.format(random.nextInt(10000));
           }
       
           /**
            * @return {@link String }
            * @描述 随机生成6位验证码
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/10/04
            * @since 1.0.0
            */
           public static String getSixBitRandom() {
               return sixdf.format(random.nextInt(1000000));
           }
       
           /**
            * 给定数组，抽取n个数据
            * @param list
            * @param n
            * @return
            */
           public static ArrayList getRandom(List list, int n) {
       
               Random random = new Random();
       
               HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
       
               // 生成随机数字并存入HashMap
               for (int i = 0; i < list.size(); i++) {
       
                   int number = random.nextInt(100) + 1;
       
                   hashMap.put(number, i);
               }
       
               // 从HashMap导入数组
               Object[] robjs = hashMap.values().toArray();
       
               ArrayList r = new ArrayList();
       
               // 遍历数组并打印数据
               for (int i = 0; i < n; i++) {
                   r.add(list.get((int) robjs[i]));
                   System.out.print(list.get((int) robjs[i]) + "\t");
               }
               System.out.print("\n");
               return r;
           }
       }
       
       ```
     
     + 编写发送阿里云短信服务的service方法
     
       【controller方法】
     
       > 通过设置redis中手机号对应的验证码的有效时间来实现五分钟验证码有效，超过五分钟就重新发短信
     
       ```java
        @GetMapping(value = "/send/{phoneNumber}")
       @ApiOperation("发送短信验证码并存入redis中")
       public ResponseData code(@ApiParam(name="phoneNumber",value = "手机号码",required = true,defaultValue = "18794830715")
                                    @PathVariable String phoneNumber) {
           //通过手机号从redis中获取验证码,opsForValue().get()就是从redis获取参数的方法
           String code = redisTemplate.opsForValue().get(phoneNumber);
           //redisTemplate.getExpire(phoneNumber,TimeUnit.MINUTES)是获取有效时间
           if(!StringUtils.isEmpty(code)) return ResponseData.responseCall()
                   .message("请您在"+redisTemplate.getExpire(phoneNumber,TimeUnit.MINUTES)+"分钟后进行尝试!");
           code = RandomUtil.getSixBitRandom();
           Map<String,Object> param = new HashMap<>();
           param.put("code", code);
           boolean isSend = msmService.sendMessage(phoneNumber, param);
           if(isSend) {
               //设置的5分钟的有效时长，TimeUnit.MINUTES是一分钟，5是5个一分钟
               redisTemplate.opsForValue().set(phoneNumber, code,5, TimeUnit.MINUTES);
               return ResponseData.responseCall();
           } else {
               return ResponseData.responseErrorCall().message("发送短信失败");
           }
       }
       ```
     
       【service方法】
     
       ```java
       @Override
       public boolean sendMessage(String phoneNumber, Map<String, Object> param) {
           if(StringUtils.isEmpty(phoneNumber)) return false;
           //default是地域节点，直接default
           DefaultProfile profile = DefaultProfile.getProfile(
                   "default",
                   ConstantProperties.KEY_ID,
                   ConstantProperties.ACCESS_KEY_SECRET);
           IAcsClient client = new DefaultAcsClient(profile);
       
           //这部分参数是固定写法
           CommonRequest request = new CommonRequest();
           //request.setProtocol(ProtocolType.HTTPS);
           //请求提交方式
           request.setMethod(MethodType.POST);
           //请求访问的服务器
           request.setDomain("dysmsapi.aliyuncs.com");
           //版本号
           request.setVersion("2017-05-25");
           //行为为发送短信
           request.setAction("SendSms");
       
           //设置参数
           request.putQueryParameter("PhoneNumbers", phoneNumber);//设置手机号，这个key是固定的
           request.putQueryParameter("SignName", "阿里云短信测试");//设置阿里云的签名名称
           request.putQueryParameter("TemplateCode", ConstantProperties.TEMPLATE_CODE);//模板code
           request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));//这里面有code，这里需要传递的也是json格式，Map可以直接变成json格式
           try {
               CommonResponse response = client.getCommonResponse(request);//这个就是发送请求的方法
               System.out.println(response.getData());
               return response.getHttpResponse().isSuccess();
           } catch (ServerException e) {
               e.printStackTrace();
           } catch (ClientException e) {
               e.printStackTrace();
           }
           return false;//捕获异常就返回false
       }
       ```

2. 登录接口

   + 登录用户信息验证

     + 密码不能明文，存储密码是加密的，验证采用的办法是输入密码加密与数据库密码进行对比，实际中用MD5加密，特点是只能加密，不能加密，工具类为MD5.java，其中的encrypt方法就是加密，MD5在java.security包下就有

   + 核心：

     + 第一步：建表edu_user，使用mp代码生成器生成后端框架，创建启动类，设置Spring的配置文件，设置服务端口号、服务名、配置测试mp执行效率的环境、数据源、mp日志、设置json的时间格式和时区、配置mp的逻辑删除定义、mapper.xml的路径、Hystrix熔断机制和超时时间、redis相关配置、

     + 第二步：创建登录和注册的vo类、封装用户信息的bo类

     + 第三步：在common模块下添加MD5Util类生成MD5加密密文并将密文转成32位

       > MD5加密大致流程：字符串转成字节数组，字节数组补足64的倍数个字节，补足规则：原字节数组后一位补16进制80，最后八个字节补足原字节数组的bit总数并展示为小端序，在此基础上将总字节数补0x00至64字节的倍数，将处理后的字节数组按64个字节一组分成若干整数组，设置A、B、C、D四个4字节16进制数，分别赋值给a、b、c、d；对a、b、c、d按照MD5算法的规则循环计算每一组数据后得到a、b、c、d，这四个数与A、B、C、D相加每个数中的字节按小端序的顺序排列输出一个16字节数，即md.digest()中的运算规则，分别用b >>> 4 & 0xf和b & 0xf将对应的一个字节的两个16进制取到并查表转换成对应的16进制字符，将得到的字符数组转换成字符串即MD5不加盐算法的最终实现
       >
       > MD5算法原理没有深究

       ```java
       public class MD5Util {
           public static String encrypt(String strSrc) {
               try {
                   char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
                   byte[] bytes = strSrc.getBytes();//这个还是密码的长度
                   MessageDigest md = MessageDigest.getInstance("MD5");
                   md.update(bytes);
                   bytes = md.digest();//这一步自动扩容到16长度，这个就是算密文的方法，这个得到的是小端序16进制的一个字节的字符数组，
                   // 需要转换成16进制的除去0x的32位表示方式，每4个比特就是一个16进制的一个位表示
                   int j = bytes.length;
                   char[] chars = new char[j * 2];//准备输出
                   int k = 0;
                   for (int i = 0; i < bytes.length; i++) {
                       byte b = bytes[i];
                       chars[k++] = hexChars[b >>> 4 & 0xf];//前半个字节右移4位，左边补0，然后与00001111求与得到16进制的取得对应的16进制字符
                       chars[k++] = hexChars[b & 0xf];//后半个字节得到对应16进制字符
                   }
                   return new String(chars);
               } catch (NoSuchAlgorithmException e) {
                   e.printStackTrace();
                   throw new RuntimeException("MD5加密出错！！+" + e);
               }
           }
       }
       ```

     + 第四步：后端接口

       【controller层】

       ```java
       /**
        * <p>
        * 用户表 前端控制器
        * </p>
        *
        * @author Earl
        * @since 2023-10-05
        */
       @RestController
       @RequestMapping("/eduuser/ucenter")
       @CrossOrigin
       public class EduUserController {
           @Autowired
           private EduUserService userService;
       
           @ApiOperation(value = "用户登录")
           @PostMapping("login")
           public ResponseData login(@ApiParam(name = "loginInfo",value = "用户登录信息",required = true) @RequestBody LoginVo loginVo) {
               String token = userService.login(loginVo);
               return ResponseData.responseCall().data("token", token);
           }
       
           @ApiOperation(value = "用户注册")
           @PostMapping("register")
           public ResponseData register(@ApiParam(name = "registerInfo",value = "用户注册信息",required = true)@RequestBody RegisterVo registerVo){
               userService.register(registerVo);
               return ResponseData.responseCall().message("注册成功");
           }
       
           /**
            * @param request
            * @return {@link ResponseData }
            * @描述 根据token获取用户id,通过用户id查询用户数据
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/10/05
            * @since 1.0.0
            */
           @ApiOperation(value = "根据token获取登录信息")
           @GetMapping("auth/getLoginInfo")
           public ResponseData getLoginInfo(@ApiParam(name = "request",value = "用户请求",required = true) HttpServletRequest request){
               try {
                   //从请求中获取token，并将token解析成用户id进行返回，如果没有token就返回空串
                   String userId = JwtUtils.getMemberIdByJwtToken(request);
                   LoginUserInfoBo loginUserInfo = userService.getLoginInfo(userId);
                   return ResponseData.responseCall().data("loginInfo", loginUserInfo);
               }catch (Exception e){
                   e.printStackTrace();
                   throw new CustomException(20001,"请先登录");
               }
           }
       }
       ```

       【service层】

       ```java
       /**
        * <p>
        * 用户表 服务实现类
        * </p>
        *
        * @author Earl
        * @since 2023-10-05
        */
       @Service
       public class EduUserServiceImpl extends ServiceImpl<EduUserMapper, EduUser> implements EduUserService {
       
           @Autowired
           private RedisTemplate<String,String> redisTemplate;
       
           /**
            * @param loginVo
            * @return {@link String }
            * @描述 用户登录验证,校验用户手机号和密码是否为空串，验证用户手机号是否注册、密码是否正确，账号是否被禁用
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/10/05
            * @since 1.0.0
            */
           @Override
           public String login(LoginVo loginVo) {
               String phoneNumber = loginVo.getPhoneNumber();
               String password = loginVo.getPassword();
               //校验参数,格式由前端校验
               if(StringUtils.isEmpty(phoneNumber)) {
                   throw new CustomException(20001,"请输入您的手机号码");
               }
               if(StringUtils.isEmpty(password)){
                   throw new CustomException(20001,"请输入您的登录密码");
               }
               //通过手机号码获取会员
               EduUser eduUser = baseMapper.selectOne(new QueryWrapper<EduUser>().eq("phone_number", phoneNumber));
               if(null == eduUser) {
                   throw new CustomException(20001,"该手机号码尚未进行注册");
               }
               //校验密码
               if(!MD5Util.encrypt(password).equals(eduUser.getPassword())) {
                   throw new CustomException(20001,"用户密码错误");
               }
               //校验是否被禁用
               if(eduUser.getIsDisabled()) {
                   throw new CustomException(20001,"该账户已被禁用");
               }
               //到此用户登录信息校验成功，使用JWT生成token字符串
               String token = JwtUtils.getJwtToken(eduUser.getId(), eduUser.getNickname());
               return token;
           }
       
           /**
            * @param registerVo
            * @描述 用户注册，校验用户输入信息是否为空串、校验用户短信验证是否正确、校验手机号是否已经注册，校验通过将用户信息添加到数据库
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/10/05
            * @since 1.0.0
            */
           @Override
           public void register(RegisterVo registerVo) {
               //获取注册信息，进行校验
               String nickname = registerVo.getNickname();
               String phoneNumber = registerVo.getPhoneNumber();
               String password = registerVo.getPassword();
               String code = registerVo.getCode();
               //校验参数是否为空，实际前端会对格式进行校验，这里只是确认
               if(StringUtils.isEmpty(phoneNumber) ||
                       StringUtils.isEmpty(phoneNumber) ||
                       StringUtils.isEmpty(password) ||
                       StringUtils.isEmpty(code)) {
                   throw new CustomException(20001,"用户注册异常，请联系客服");
               }
               //从redis获取发送的验证码校验验证码
               String msmCode = redisTemplate.opsForValue().get(phoneNumber);
               if(!code.equals(msmCode)) {
                   throw new CustomException(20001,"验证码错误");
               }
               //查询数据库中是否存在相同的手机号码
               Integer count = baseMapper.selectCount(new QueryWrapper<EduUser>().eq("phone_number", phoneNumber));
               //Integer的intValue方法判断对应的值，应该右自动类型转换吧
               if(count.intValue() > 0) {
                   throw new CustomException(20001,"该手机号已被注册");
               }
               //添加注册信息到数据库
               EduUser eduUser = new EduUser();
               eduUser.setNickname(nickname);
               eduUser.setPhoneNumber(registerVo.getPhoneNumber());
               eduUser.setPassword(MD5Util.encrypt(password));
               eduUser.setIsDisabled(false);
               //设置用户默认头像
               eduUser.setAvatar(ConstantProperties.DEFAULT_AVATAR);
               save(eduUser);
           }
       
           /**
            * @param memberId
            * @return {@link LoginVo }
            * @描述 通过用户id获取用户信息并封装到登录信息中
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/10/05
            * @since 1.0.0
            */
           @Override
           public LoginUserInfoBo getLoginInfo(String memberId) {
               EduUser eduUser = baseMapper.selectById(memberId);
               LoginUserInfoBo loginUserInfo = new LoginUserInfoBo();
               BeanUtils.copyProperties(eduUser, loginUserInfo);
               return loginUserInfo;
           }
       }
       ```

     + 第五步：注入mp逻辑删除组件ISqlInjector，并在逻辑删除字段上标注@TableLogic注解

       ```java
       @Configuration
       public class EduUserConfig {
       
           /**
            * @return {@link ISqlInjector }
            * @描述 配置mp逻辑删除插件
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/27
            * @since 1.0.0
            */
           @Bean
           public ISqlInjector iSqlInjector(){
               return new LogicSqlInjector();
           }
       }
       ```

       

### 前端实现

1. 安装element-ui和vue-qriously，安装后需要像轮播图一样在plugins目录下进行引用  

   + vue-qriously是为了后续下载微信支付码使用的

2. 整合注册页面

   + assets/css/sign.css是注册相关的样式
   + 在layout目录下创建注册布局页面sign.vue，

   + 在pages目录下创建注册页面register.vue，default中注册、登录的超链接路径与该注册页面的名字要一致且只取"/页面名字"，复制页面内容

     > 倒计时效果基于js中的定时器方法setInterval("js代码"，1000)，每隔一秒让参数中的js代码执行一次，只需要设置每秒让显示参数自减1即可实现倒计时效果
     >
     > SpringBoot应用改了模块名字以后一定要检查配置文件是否在类路径下被打包，否则会出现绑定数据错误导致项目起不来和其他问题，用了redis的服务运行时redis一定要启动起来，否则运行会出问题
     >
     > 有很多bug，比如后端返回号码已经被注册，也会显示注册成功;验证码不正确也会显示注册成功，没注册上也会显示注册成功，验证码不是立即验证，而是点击注册再验证，逻辑应该改成验证码不正确无法注册，手机的验证也要在输入手机后立即验证，避免失败后重新输入，拉低用户体验；一分钟的验证码只是防刷短信的，只要重发了验证码，redis中的数据就更新了，这里的bug大部分都修好了，解决办法是提交注册前校验

   + 整合步骤

     + 定义前端接口

       ```js
       import request from '@/utils/request'
       export default {
           //根据手机号码发送短信
           sendSmsCode(phoneNumber) {
               return request({
                   url: `edusms/confirm//send/${phoneNumber}`,
                   method: 'get'
               })
           },
           //用户注册
           submitRegister(registerVo) {
               return request({
                   url: `/eduuser/ucenter/register`,
                   method: 'post',
                   data: registerVo
               })
           }
       }
       ```

     + 创建登录页面

       ```vue
       <template>
           <div class="main">
               <div class="title">
                   <a href="/login">登录</a>
                   <span>·</span>
                   <a class="active" href="/register">注册</a>
               </div>
               <div class="sign-up-container">
                   <el-form ref="userForm" :model="registerVo">
                       <!--:rules是输入框的前端校验规则，required: true表示该输入框必须输入值，不输入值表单提交会有问题，message是不输入值后输入框的提示信息，
                       trigger是触发判断的事件，这里是blur失去焦点
                       -->
                       <el-form-item class="input-prepend restyle" prop="nickname" :rules="[{required: true, message: '请输入你的昵称', trigger: 'blur' }]">
                           <div>
                               <el-input type="text" placeholder="你的昵称" v-model="registerVo.nickname"/>
                               <i class="iconfont icon-user"/>
                           </div>
                       </el-form-item>
                       <!--validator是自定义校验规则，校验规则是自己写的方法checkPhone。即在执行组件校验规则的同时执行自定义校验规则checkPhone-->
                       <el-form-item class="input-prepend restyle no-radius" prop="phoneNumber" :rules="[{ required: true, message: '请输入手机号码', trigger: 'blur' },{validator:checkPhone, trigger: 'blur'}]">
                           <div>
                               <el-input type="text" placeholder="手机号" v-model="registerVo.phoneNumber"/>
                               <i class="iconfont icon-phone"/>
                           </div>
                       </el-form-item>
                       <el-form-item class="input-prepend restyle no-radius" prop="code" :rules="[{ required: true, message: '请输入验证码', trigger: 'blur' }]">
                           <div style="width: 100%; display: block; float: left; position: relative">
                               <el-input type="text" placeholder="验证码" v-model="registerVo.code"/>
                               <i class="iconfont icon-phone"/>
                           </div>
                           <div class="btn" style="position:absolute;right: 0;top: 6px;width:40%;">
                               <a href="javascript:" type="button" @click="sendSmsCode()" :value="codeTest" style="border: none;background-color: none">{{codeTest}}</a>
                           </div>
                       </el-form-item>
                       <el-form-item class="input-prepend" prop="password" :rules="[{ required:true, message: '请输入密码', trigger: 'blur' }]">
                           <div>
                               <el-input type="password" placeholder="设置密码" v-model="registerVo.password"/>
                               <i class="iconfont icon-password"/>
                           </div>
                       </el-form-item>
                       <div class="btn">
                           <input type="button" class="sign-up-button" value="注册" @click="submitRegister()">
                       </div>
                       <p class="sign-up-msg">点击 “注册” 即表示您同意并愿意遵守<br>
                           <a target="_blank" href="http://www.jianshu.com/p/c44d171298ce">用户协议</a>
                           和 
                           <a target="_blank" href="http://www.jianshu.com/p/2ov8x3">隐私政策</a> 。
                       </p>
                   </el-form>
                   <!-- 更多注册方式 -->
                   <div class="more-sign">
                       <h6>社交帐号直接注册</h6>
                       <ul>
                           <li>
                               <a id="weixin" class="weixin" target="_blank" href="http://huaan.free.idcfengye.com/api/ucenter/wx/login">
                                   <i class="iconfont icon-weixin"/>
                               </a>
                           </li>
                           <li>
                               <a id="qq" class="qq" target="_blank" href="#"><i class="iconfont icon-qq"/></a>
                           </li>
                       </ul>
                   </div>
               </div>
           </div>
       </template>
       ```

     + 定义数据和其中的方法

       ```js
       <script>
           //这个是引入页面中用到的样式文件
           import '~/assets/css/sign.css'
           import '~/assets/css/iconfont.css'
           import register from '@/api/register'
           export default {
               //这个是定义页面样式的文件为assets/css/sign.css，错了这句话的意思是使用layout中的sign.vue布局，引用sign.css是import的效果
               layout: 'sign',
               data() {
                   return {
                       registerVo: {
                           phoneNumber: '',
                           code: '',
                           nickname: '',
                           password: ''
                       },
                       sending: true, //是否发送验证码，true表示可发送，false表示发送按钮禁用
                       //验证码再次发送倒计时效果，倒计时效果基于html中的定时器方法，每隔一段时间执行一次js方法：setInterval("alert('test')",1000)
                       //效果是每隔一秒让前一个参数表示的js代码执行一次，倒计时只需要每隔1s让second值自减1
                       second: 60, //倒计时初始值为60s
                       codeTest: '获取验证码'
                   }
               },
               methods: {
                   //点击获取验证码调用接口发送手机验证码
                   sendSmsCode() {
                       //sending = false
                       //his.sending原为true,请求成功， !this.sending == true，主要是防止有人把disabled属性去掉，多次点击；
                       if (!this.sending) return;
                       //debugger
                       // prop 换成你想监听的prop字段,发送验证码要确认手机号码格式没问题
                       this.$refs.userForm.validateField('phoneNumber', (errMsg) => {
                       if (errMsg == '') {//手机号码格式信息为空则可以继续执行发送验证码操作
                           register.sendSmsCode(this.registerVo.phoneNumber).then(res => {
                               this.sending = false;
                               this.timeDown();
                           });
                       }
                       })
                   },
                   //验证码可再次发送倒计时效果
                   timeDown() {
                       let result = setInterval(() => {
                           --this.second;//每秒时间自减1
                           this.codeTest = this.second//显示剩余秒值
                           if (this.second < 1) {
                               clearInterval(result);//用于停止 setInterval() 方法执行的函数代码,参数是必须的，为setInterval的返回值
                               this.sending = true;//倒计时结束可发送验证码
                               this.second = 60;//初始化倒计时秒值
                               this.codeTest = "获取验证码"//倒计时结束让显示值恢复初始值
                           }
                       }, 1000);
                   },
                   //请求注册接口，提交注册数据
                   submitRegister() {
                       this.$refs['userForm'].validate((valid) => {
                           if (valid) {
                               register.submitRegister(this.registerVo).then(response => {
                                   //提示注册成功
                                   this.$message({
                                       type: 'success',
                                       message: "注册成功"
                                   })
                                   //跳转登录页面
                                   this.$router.push({path: '/login'})
                               })
                           }
                       })
                   },
                   checkPhone (rule, value, callback) {
                       //正则表达式校验手机号码，自定义校验方法自动传参value为输入框的值，callback是返回参数，校验成功直接调用callback，校验失败传参创建一个error对象并输入提示信息
                       //字符串以1开始，/是正则表达式开始结束的标志，[34578]表示第二位匹配34578，然后匹配9次数字，$表示到此该字符串结束，整体可以看做一个对象用test和值做校验
                       if (!(/^1[34578]\d{9}$/.test(value))) {
                           return callback(new Error('手机号码格式不正确'))
                       }
                       return callback()
                   }
               }
           }
       </script>
       ```

3. 整合登录页面

   + 登录前端页面

   + 整合微信扫描登录

     + 这是腾讯提供的一个流程
     + OAuth2

   + 整合步骤

     + 第一步：调用接口返回用户的token

     + 第二步：将返回的token字符串放在cookie中【必须安装js-cookie才能用cookie】

     + 第三步：写一个前端拦截器，判断cookie中有没有token字符串，如果有将token字符串放入请求头中

     + 第四步：根据token值调用接口获取用户信息，以便展示在首页面，将用户信息放入cookie，请求接口前会先调用前端拦截器

     + 第五步：在首页从cookie获取用户信息，在首页面对用户信息进行展示，cookie.set是设置值，cookie.get是获取值，同样需要引入js-cookie

     + 第六步：通过用户信息对象或者其属性是否有值判断是否在注册登录位置显示用户信息

     + 第七步：退出直接把cookie中的数据清空就回到最初的状态了，cookie的清空是向同名cookie中设置一个空串，cookie中的数据就消失了，清空后回到首页面

     + 第八步：定义前端请求接口：

       > 太多了，按照文档更改组件参数，主要是前端页面组件的校验规则，cookie设置值和取值的视线，请求拦截器的token放入header供后端获取，cookie数据对应的值取空串就能清空cookie数据，登出就是清空cookie数据，必须安装js-cookie才能对cookie放值和取值操作，用一个标志参数让注册表单无法多次请求
       >
       > 这里主要优化了手机号码注册前验证，短信验证码注册前验证，密码二次确认的功能
       >
       > 注意配置文件中用value引入的字符串不需要加""，加了会让数据库中对应的字符串也出现""，如头像连接，会导致前端 无法识别连接
       >
       > 存在问题，以弹窗的形式提示成功校验信息，点击注册会全部进行一次校验，仍然会弹出所有弹窗，不好看，怎么进行处理

       【短信验证码校验】

       > 注意提示框不要写在then函数的外面，否则一定会发生数据还没返回赋值就进行了判断并提示信息的情况

       ```js
       checkSmsCode(rule, value, callback){
           //验证码格式判断
           if (!(/^\d{6}$/.test(value))) {
               return callback(new Error('验证码格式不正确'))
           }
           if(!this.registerVo.phoneNumber){
               return callback(new Error('请填写手机号码'))
           }
           //验证短信验证码是否匹配
           register.verifySmsCode(this.registerVo.phoneNumber,value).then(response=>{
               this.smsCodeVerifyMsg=response.data.message
               if(this.smsCodeVerifyMsg){
                   //new Error('该手机号码已经被注册')不能传变量，只能手写字符串，否则提示信息无法消失
                   return callback(new Error('验证码错误'))
               }
               this.$message({
                   type: 'success',
                   message: "验证码验证成功"
               })
               return callback()
           })
       },
       ```

       【电话号码校验】

       ```js
       checkPhone (rule, value, callback) {
           //正则表达式校验手机号码，自定义校验方法自动传参value为输入框的值，callback是返回参数，校验成功直接调用callback，校验失败传参创建一个error对象并输入提示信息
           //字符串以1开始，/是正则表达式开始结束的标志，[34578]表示第二位匹配34578，然后匹配9次数字，$表示到此该字符串结束，整体可以看做一个对象用test和值做校验
           if (!(/^1[34578]\d{9}$/.test(value))) {
               return callback(new Error('手机号码格式不正确'))
           }
           //验证手机号码是否已经注册
           register.verifyPhoneNumber(value).then(response=>{
               this.phoneVerifyMsg=response.data.message
               if(this.phoneVerifyMsg){
                   //new Error('该手机号码已经被注册')不能传变量，只能手写字符串，否则提示信息无法消失
                   return callback(new Error('该手机号码已经被注册'))
               }
               return callback()
           })
       },
       ```

       【密码二次验证】

       ```js
       checkPassword(rule, value, callback){
           if(!this.registerVo.password){
               return callback(new Error('请先填写用户密码'))
           }
           if(!(this.registerVo.password==value)){
               return callback(new Error('前后密码不匹配'))
           }
           this.$message({
               type: 'success',
               message: "密码匹配成功"
           })
           return callback()
       }
       ```

       

4. 微信扫码登录

   > 自动注册、自动登录

   + OAuth2

     + 针对特定问题的一种解决方案，能解决一些特定问题，主要解决两个问题

       + 解决开放系统间的授权

         > 线上的打印服务无法访问用户在线上的第三方图片存储服务，图片存储服务只能由用户提供身份证明主动去操作，打印服务要访问特定用户的照片需要用户对其进行授权，Otuth2解决的第一个问题，如何授权让第三方服务去访问用户资源
         >
         > 授权方式：
         >
         > 1. 方式1：用户名密码复制【适用于同一个公司内部多个系统，不适用于不授信的第三方应用】
         >    + 客户应用复制资源拥有者的用户密码，并将其传递给受保护的资源
         > 2. 方式2：通用开发者key【适用于合作商或者授信的不同业务部门】
         >    + 万能钥匙，两个客户应用商量好了开发某种认证，使两个应用可以互相访问【缺点是两个应用之间实力对等才能促进这种方式的达成】
         > 3. 方式3：办法令牌
         >    + 用户资源提供商向第三方提供一个令牌【字符串】，令牌的内容包含给谁进行颁发，有效时间，通过网络给别的服务颁发令牌、令牌管理、颁发、吊销需要统一的协议，在此基础上形成了OAuth2解决方案，类似于token的感觉

       + 分布式系统的访问问题

         > 指的就是单点登录，生成token，请求携带token【OAuth2令牌机制：按照一定的规则生成字符串，字符串包含用户信息，OAuth2没有规定token生成规则，如何生成是用户自己决定的，可以用JWT，也可以自定义】
         >
         > 单个服务登录成功，封装用户信息，按照一定规则生成token字符串，token响应给应用并通过路径或者cookie进行传递给各个服务，服务获取字符串，判断token中是否有用户对应信息来判断用户登录状态
         >
         > 犯了事去美国是一种解决方案，怎么去是技术细节【该技术细节类比于如何生成token】

     + Oauth2是一种解决方案，不是一种协议，只支持HTTP协议，没有定义技术细节，没有定义token生成方式，没有定义加密方式，仅仅是一种用于REST/APIs的代理授权框架，仅用于授权代理，通过这种方式解耦认证和授权，解耦资源服务器和授权服务器，基于令牌的方式，在不暴露用户密码的情况下让应用获取对用户数据的有限访问权限，让应用代表用户去访问用户数据，是标准安全框架，适用于服务端WEBApp、浏览器单页SPA、无线原生APP、服务器对服务器之间，HTTP/JSON友好，易于请求传递token

       + 缺点是框架太宽泛，各种实现、互操作性和兼容性太差，和OAuth1不兼容，各种安全场景需要去定制
       + 一般来说授获取访问令牌【Acess Token】是客户应用【一般是Web或者无线应用】在用户许可下在单独的授权服务器【AS】中完成的，拿着令牌去访问资源服务器【RS】，访问令牌具有作用域【由资源拥有者额外指定的有限权限】

   + 腾迅相关的准备工作

     + 到网站https://open.weixin.qq.com  注册用户，完善开发者资质认证【300元，1到2天审批】，创建网站名字提交审核【一般七个工作日审批，名字不要太个性，名字通俗平庸，这个名字用于用户扫码后的跳转】，需要域名地址【扫码后的请求跳转，包括服务器地址】

     + 老师提供了，在微信登录的wx id。txt中，不能用了，测试用看评论前几条有

     + 微信登录的基本流程

       ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/07/2f91082b145b4e248c8ca504ceec82fb微信登录流程.png)

     + 在edu_user模块中配置微信id、密钥、域名地址，创建类读取配置文件内容；这几个值暂时使用固定的，实际生产中都是公司申请好的

     + 生成微信扫描的二维码

       + 微信文档中写的

         + 访问固定地址传固定参数就能生成微信二维码

           > "https://open.weixin.qq.com/connect/qrconnect" +
           > "?appid=%s" +
           > "&redirect_uri=%s" +
           > "&response_type=code" +
           > "&scope=snsapi_login" +
           > "&state=%s" +
           > "#wechat_redirect";  
           >
           > appid是应用唯一标识，就是微信申请的应用id，在配置文件配好了
           >
           > redirect_uri是自己使用urlEncode编码方式对重定向连接进行处理，注意只需要对请求重定向地址编码，此前和后续参数不用编码，并用传递给微信编码后的重定向地址
           >
           > response_type是填code，这个是固定的
           >
           > scope是应用授权作用域，拥有多个作用域用逗号（,）分隔，网页应用对应snsapi_login
           >
           > 以上四个参数是必须的
           >
           > 可选参数state是state用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻 击），建议第三方带上该参数，可设置为简单的随机数加session进行校验【就是给该地址传递什么state值就返回什么state值，防止跨站请求伪造攻击】
           >
           > 实际非固定的就两个appid和redirect_uri

         + 直接拼接连接字符串参数很容易出错，实际开发中常用%s来占位，用String.format来传递参数，第一个参数为拼接字符串，后续参数对应要拼接的变量或者字符串常量

         + 编码用到URLEncoder.encode方法

         + 微信二维码用到了重定向不要加@RestController，@RestController中的@ReponseBody是返回对象，直接用@Conroller直接重定向，只是重定向，不需要返回地址

         【微信二维码生成】

         ```java
         /**
          * @param session
          * @return {@link String }
          * @描述 获取微信登录二维码
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/07
          * @since 1.0.0
          */
         @GetMapping("login")
         public String genQrConnect(HttpSession session) {
             // 微信开放平台授权baseUrl
             String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                     "?appid=%s" +
                     "&redirect_uri=%s" +
                     "&response_type=code" +
                     "&scope=snsapi_login" +
                     "&state=%s" +
                     "#wechat_redirect";
             //获取业务服务器重定向地址
             String redirectUrl = ConstantProperties.WX_OPEN_REDIRECT_URL;
             try {
                 redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
             } catch (UnsupportedEncodingException e) {
                 throw new CustomException(20001, e.getMessage());
             }
             // 防止csrf攻击（跨站请求伪造攻击）
             //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
             String state = "imhelen";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
             //System.out.println("state = " + state);
             // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
             //键： "wechar-open-state-" + httpServletRequest.getSession().getId()
             //值： satte
             //过期时间： 30分钟
             //生成qrcodeUrl
             String qrcodeUrl = String.format(
                     baseUrl,
                     ConstantProperties.WX_OPEN_APP_ID,
                     redirectUrl,
                     state);
             return "redirect:" + qrcodeUrl;
             //扫描后跳转的地址栏：http://localhost:8160/api/ucenter/wx/callback?code=071mkwFa1x2W9G0gz0Ia1va8Y02mkwF4&state=imhelen
         }
         ```

         + 扫描二维码后的跳转流程

           + 扫描后会去跳转配置的域名wx.open.redirect_url，即编码前的redirectUrl=http://localhost:8160/api/ucenter/wx/callback，//这个扫描是跳转到尚硅谷的服务器，然后转发回来的地址，方便测试用，只需要把本地的地址改成返回的连接形式就能端口8160和路径api/ucenter/wx/callback与尚硅谷服务器返回结果一致就能回到本地接口，实际生产中不需要中间服务器，因为会跳转到对应公司的服务器地址接口上

           + 返回了code和state两个参数，直接用形参就能接收

             + code类似于手机验证码，随机唯一值
             + state自定义的，防止网站攻击设置的【防止csrf攻击（跨站请求伪造攻击），设置为ngrok的前置域名】

           + 拿着获取到的code，携带code发送请求到固定地址，传参网站id和secret；获取到access_token访问凭证和openid【微信昵称可以相同，但是都有唯一标识的openid，相当于主键】

           + 拿着access_token和openid再去请求微信固定地址，最终获取微信扫码人的信息，比如头像、昵称等等【多个步骤多次验证得到数据】

             > 完整流程：用户要登录一个系统，用户是第一方，系统是第二方；用微信登录，微信就是第三方；用户请求第三方应用，第二方向第三方发起OAuth2授权登录，微信请求用户确认，用户确认后微信重定向到第二方带上临时授权票据【code相当于手机验证码】，第二方通过code和第二方注册的应用信息请求微信获取授权令牌access_key和用户id，第二方应用拿着access_key和用户id去请求用户数据
             >
             > 用到的技术点：
             >
             > 1. 用httpClient去使用程序在服务端发送请求，可以自己写个工具类去操作httpClient发送请求，还有类似的服务okhttp
             >
             >    > 返回的响应体是json格式的字符串，包含以下信息，主要使用的就是access_token和微信id
             >    >
             >    > + access_token
             >    > + expires_in：凭证有效时间
             >    > + refresh_token
             >    > + open_id：微信id
             >    > + scope：作用范围
             >    > + unionid：作用单元
             >
             > 2. json转换工具，常用的有fastjson和gson【这次用gson】
             >
             >    + 还有jackson，@RestController返回json数据用的就是jackson

           + httpClient发送get请求的工具类方法

             ```java
             /**
              * 发送一个 GET 请求
              *
              * @param url
              * @param charset
              * @param connTimeout  建立链接超时时间,毫秒.
              * @param readTimeout  响应超时时间,毫秒.
              * @return
              * @throws ConnectTimeoutException   建立链接超时
              * @throws SocketTimeoutException   响应超时
              * @throws Exception
              */
             public static String get(String url, String charset, Integer connTimeout,Integer readTimeout)
                     throws ConnectTimeoutException,SocketTimeoutException, Exception {
             
                 HttpClient client = null;
                 //创建HttpGet对象，传参要请求的地址
                 HttpGet get = new HttpGet(url);
                 String result = "";
                 try {
                     // 设置参数，连接超时时间、响应超时时间
                     Builder customReqConf = RequestConfig.custom();
                     if (connTimeout != null) {
                         customReqConf.setConnectTimeout(connTimeout);
                     }
                     if (readTimeout != null) {
                         customReqConf.setSocketTimeout(readTimeout);
                     }
                     get.setConfig(customReqConf.build());
             
                     HttpResponse res = null;
             
                     //不同前缀执行不同的请求方法
                     if (url.startsWith("https")) {
                         // 执行 Https 请求.
                         client = createSSLInsecureClient();
                         //发送请求是execute方法，发送的是封装了路径的HttpGet请求，返回HttpResponse对象
                         res = client.execute(get);
                     } else {
                         // 执行 Http 请求.
                         client = HttpClientUtil.client;
                         res = client.execute(get);
                     }
                     //通过返回对象获取最终的结果，            //res.getEntity()是获取对应的响应体
                     result = IOUtils.toString(res.getEntity().getContent(), charset);
                 } finally {
                     get.releaseConnection();
                     if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                         ((CloseableHttpClient) client).close();
                     }
                 }
                 return result;
             }
             ```

             

         + 返回的微信用户信息格式

           > 一般用到的就是openid、nickname、sex、headimgurl

           + openid
           + nickname
           + sex：男性是1，女性是0
           + language
           + city
           + country
           + headimgurl：这个就是微信头像链接，这个返回的双斜杠\\\会用\/转义，存数据库的时候需要去掉
           + privilige
           + unionid

           > 验证数据库中是否有该微信id，没有取出信息然后存入数据库，
           >
           > 并用用户id和昵称生成token字符串，通过路径传递到首页面，不能放在cookie中，因为分布式项目中cookie不能跨域传递

           ```java
           import com.atlisheng.commonutils.jwt.JwtUtils;
           import com.atlisheng.eduuser.entity.EduUser;
           import com.atlisheng.eduuser.service.EduUserService;
           import com.atlisheng.eduuser.utils.ConstantProperties;
           import com.atlisheng.eduuser.utils.HttpClientUtil;
           import com.atlisheng.servicebase.exceptions.CustomException;
           import com.google.gson.Gson;
           import io.swagger.annotations.Api;
           import org.springframework.beans.factory.annotation.Autowired;
           import org.springframework.stereotype.Controller;
           import org.springframework.web.bind.annotation.CrossOrigin;
           import org.springframework.web.bind.annotation.GetMapping;
           import org.springframework.web.bind.annotation.RequestMapping;
           
           import javax.servlet.http.HttpSession;
           import java.io.UnsupportedEncodingException;
           import java.net.URLEncoder;
           import java.util.HashMap;
           
           @Api(description = "微信扫码登录接口")
           @Controller
           @CrossOrigin
           @RequestMapping("/api/ucenter/wx")
           public class WxApiController {
               @Autowired
               EduUserService eduUserService;
           
               @GetMapping("callback")
               public String callback(String code,String state){
                   //获取扫码人的信息，调用注册方法
                   //得到授权临时票据code,类似于短信验证码
                   //从redis中将state获取出来，和当前传入的state作比较，做网站防攻击校验，这个好像要自己实现
                   //如果一致则放行，如果不一致则抛出异常：非法访问
                   //向认证服务器https://api.weixin.qq.com/sns/oauth2/access_token发送请求携带
                   // appid[网站id]、secret[网站密钥]、code[授权临时票据]换取access_token
                   String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                                   "?appid=%s" +
                                   "&secret=%s" +
                                   "&code=%s" +
                                   "&grant_type=authorization_code";
                   //拼接对应的三个参数
                   String accessTokenUrl = String.format(baseAccessTokenUrl,
                           ConstantProperties.WX_OPEN_APP_ID,
                           ConstantProperties.WX_OPEN_APP_SECRET,
                           code);
           
                   //用httpClient发送请求，这个技术很古老，但是一直在使用，得到对应返回的token和微信用户id，不用浏览器也能实现浏览器发送请求的效果
                   String result = null;
                   try {
                       //自定义Util包的get方法，是获取token和用户微信id的方法，这玩意儿会直接返回响应体，并转换成json格式的字符串
                       result = HttpClientUtil.get(accessTokenUrl);
                       System.out.println("accessToken=============" + result);
                   } catch (Exception e) {
                       throw new CustomException(20001, "获取access_token失败");
                   }
           
                   //解析json字符串，获取access_token和openid，老办法是分割逗号，取第一个和第四个，这种方式不好；也可以根据一段长度的字符串进行匹配
                   //方便直接用谷歌提供的gson，直接把json格式字符串转换成Map的key-value形式，直接通过key从map中取值
                   Gson gson = new Gson();
                   //Gson的fromJson方法，传参json格式的字符串，和要转成类型的class对象，key是String、value是Object
                   HashMap map = gson.fromJson(result, HashMap.class);
                   String accessToken = (String)map.get("access_token");
                   String openid = (String)map.get("openid");
           
                   //查询数据库当前用用户是否曾经使用过微信登录，数据库中有会存储openid字段，没有才去查询对应的用户信息，有对应的openid直接提示登录
                   EduUser eduUser=eduUserService.getByOpenid(openid);
                   if(eduUser==null){
                       //访问微信的资源服务器，获取用户信息,
                       String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                               "?access_token=%s" +
                               "&openid=%s";
                       String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
                       String resultUserInfo = null;
                       try {
                           resultUserInfo = HttpClientUtil.get(userInfoUrl);
                           System.out.println("resultUserInfo==========" + resultUserInfo);
                       } catch (Exception e) {
                           throw new CustomException(20001, "拉取用户微信信息失败");
                       }
                       //解析json
                       HashMap<String, Object> mapUserInfo = gson.fromJson(resultUserInfo, HashMap.class);
                       String nickname = (String)mapUserInfo.get("nickname");//微信昵称
                       String headimgurl = (String)mapUserInfo.get("headimgurl");//微信头像
                       //向数据库中插入一条记录
                       eduUser = new EduUser();
                       eduUser.setNickname(nickname);
                       eduUser.setOpenid(openid);//这个早就有了，做用户已经注册校验的
                       eduUser.setAvatar(headimgurl);
                       eduUserService.save(eduUser);
                   }
                   //如果已经注册过直接跳转登录页面即可，不需要提示信息
                   //TODO 用户的手机号码是否需要绑定，不绑定完全可以再创建一个新的号码
           
           
                   //登录，这儿需要完善用户已经登录的效果，而不是未登录前的页面，解决办法是在路径中传递token给前端
                   //首页面created方法初始化的时候会尝试从cookie中获取数据，可以在服务端生成token传递给cookie，但是cookie无法做到跨域传递，域名不同传递不了，这种方式在微服务中是不行的
                   // 生成token的数据只有用户id和昵称，一定注意token不带密码,插入数据id会回显
                   String jwtToken = JwtUtils.getJwtToken(eduUser.getId(), eduUser.getNickname());
           
                   return "redirect:http://localhost:3000?user_token="+jwtToken;
               }
           
               /**
                * @param session
                * @return {@link String }
                * @描述 获取微信登录二维码
                * @author Earl
                * @version 1.0.0
                * @创建日期 2023/10/07
                * @since 1.0.0
                */
               @GetMapping("login")
               public String genQrConnect(HttpSession session) {
                   // 微信开放平台授权baseUrl
                   String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                           "?appid=%s" +
                           "&redirect_uri=%s" +
                           "&response_type=code" +
                           "&scope=snsapi_login" +
                           "&state=%s" +
                           "#wechat_redirect";
                   //获取业务服务器重定向地址
                   String redirectUrl = ConstantProperties.WX_OPEN_REDIRECT_URL;
                   try {
                       redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
                   } catch (UnsupportedEncodingException e) {
                       throw new CustomException(20001, e.getMessage());
                   }
                   // 防止csrf攻击（跨站请求伪造攻击）
                   //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
                   String state = "imhelen";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
                   //System.out.println("state = " + state);
                   // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
                   //键： "wechar-open-state-" + httpServletRequest.getSession().getId()
                   //值： satte
                   //过期时间： 30分钟
                   //生成qrcodeUrl
                   String qrcodeUrl = String.format(
                           baseUrl,
                           ConstantProperties.WX_OPEN_APP_ID,
                           redirectUrl,
                           state);
                   return "redirect:" + qrcodeUrl;
                   //这个扫描是跳转到尚硅谷的服务器，然后转发回来的地址，方便测试用，只需要把本地的地址改成返回的连接形式就能端口和路径与尚硅谷服务器返回结果一致就能回到本地接口
                   //扫描后跳转的地址栏：http://localhost:8160/api/ucenter/wx/callback?code=071mkwFa1x2W9G0gz0Ia1va8Y02mkwF4&state=imhelen
               }
           
           }
           ```

         + 前端获取地址栏的token，加入cookie中，方便页面初始化时前端请求拦截器将cookie中的token放入请求头中去

           > 注意以？token=""的形式传递的地址栏参数不能通过this.$route.params.id的方式获取参数值，这种取值方式仅限于参数矩阵的方式
           > 路径带参数名称的参数可以通过`this.$route.query.参数名`取到
           >
           > 进入default页面，会存在token不能放入cookie的情况，原因很不理解，而且检验了两次地址栏有没有token参数的情况
           
           ```js
           <script>
           import "~/assets/css/reset.css";
           import "~/assets/css/theme.css";
           import "~/assets/css/global.css";
           import "~/assets/css/web.css";
           
           import cookie from 'js-cookie'
           import login from '@/api/login'
           
           export default {
             data() {
               return {
                 token: '',
                 userInfo: {
                   id: '',
                   avatar: '',
                   phoneNumber: '',
                   nickname: '',
                 }
               }
             },
             created() {
               this.token = this.$route.query.user_token
               if (this.token) {
                 this.wxLogin()
               }
               this.init()
             },
             methods: {
               init(){
                 this.showInfo()
               },
               showInfo() {
                 //从cookie中获取用户信息，这个信息是json字符串，不是json对象，需要使用JSON.parse(jsonStr)将其转换成json对象
                 //因为cookie中存放json对象是字符串的形式，以前直接响应数据不需要做json对象的转换，Js中的JSON就是来干这个的
                 var userInfoJsonStr = cookie.get("user_info");
                 console.log('=============='+userInfoJsonStr)
                 if (userInfoJsonStr) {
                   this.userInfo = JSON.parse(userInfoJsonStr)
                 }
               },
               logout() {
                 cookie.set('user_info', "", { domain: 'localhost' })
                 cookie.set('user_token', "", { domain: 'localhost' })
                 //跳转页面
                 window.location.href = "/"
               },
               wxLogin() {
                 if (this.token == '') return
                 //把token存在cookie中、也可以放在localStorage中
                 cookie.set('user_token', this.token, { domain:'localhost' })
                 cookie.set('user_info', '', { domain: 'localhost' })
                 var curToken=cookie.get("user_token")
                 console.log('*************'+curToken)
                 //登录成功根据token获取用户信息
                 login.getLoginInfo().then(response => {
                   this.userInfo = response.data.data.loginInfo
                   //将用户信息记录在cookie中，每次访问domain的ip都会发送，JSON.stringify(this.loginInfo)
                   cookie.set('user_info', this.userInfo, { domain: 'localhost'})
                 })
               }
             }
           }
           </script>
           ```
           
           ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/08/ddd08ef0e24243259e45285aef7ce7a3第一次没读取到cookie中设置的token.png)
           
           ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/08/7879380aad804a4d8f2a91150a7b79c7token被读取两次，且有一次设置值失败.png)
         
   
   
   

## 讲师列表

分页查询讲师功能

> 固定每页显示八个讲师

1. 后端分页查询讲师接口

   ```java
   @Override
   public Map<String, Object> pageFrontTeacherList(Page<EduTeacher> pageParam) {
       QueryWrapper<EduTeacher> queryWrapper = new QueryWrapper<>();
       queryWrapper.orderByAsc("sort");
       baseMapper.selectPage(pageParam, queryWrapper);
       List<EduTeacher> records = pageParam.getRecords();
       long current = pageParam.getCurrent();
       long pages = pageParam.getPages();
       long size = pageParam.getSize();
       long total = pageParam.getTotal();
       boolean hasNext = pageParam.hasNext();
       boolean hasPrevious = pageParam.hasPrevious();
       Map<String, Object> map = new HashMap<String, Object>();
       map.put("curPageTeachers", records);//这个是每页的记录
       map.put("current", current);//这个是当前页码
       map.put("pages", pages);//这个是总页数
       map.put("size", size);//这个是每页的记录条数
       map.put("total", total);//总记录数
       map.put("hasNext", hasNext);//这个是翻页组件的前一页
       map.put("hasPrevious", hasPrevious);//这个是翻页组件的后一页
       return map;
   }
   ```

   

2. 前端调用接口获得数据

   > 异步调用这能调用一次完成初始化，最后获取当前页数据需要调用其他方法

```js
asyncData({ params, error }) {
    //这个return后面不能直接加回车，加了回车会出问题
    return teacher.getPageList(1, 8).then(response => {
        console.log(response.data.data);
        //这个也可以用之前的方法获取值，即赋值给data中的变量，也可以直接写成下面这种形式
        //this.data=response.data.data
        //第一个data不需要在data(){}中定义，会自动帮忙定义data，还会自动把值赋值到data上，和上面的写法效果是一样的，而且其他方法可以直接调用，也不用再次声明定义
        return { teacherData: response.data.data }
    });
},
```

3. 讲师列表没有数据提示信息

   ```html
   <!-- /无数据提示 开始,使用v-if判断teacherData中没有数据就显示找不到相关数据-->
   <section class="no-data-wrap" v-if="teacherData.total==0">
       <em class="icon30 no-data-ico">&nbsp;</em>
       <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
   </section>
   <!-- /无数据提示 结束-->
   ```

4. 讲师页面

   ```html
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
   ```

5. 分页组件

   > 写一个方法做分页切换，之前的分页数据只有当前页，盲猜还要再次请求接口，就是定义一个方法传参当前选择页码和每页记录数再调用一次分页查询接口

   ```js
   methods:{
       gotoPage(page){
           if(page<=this.teacherData.pages){
               teacher.pageTeacherList(page, 8).then(response => {
                   this.teacherData=response.data.data
               });
           }
       }
   }
   ```
   
   【组件】
   
   ```html
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
   ```
   
   



## 讲师详情

1. 后端接口【一个接口查询讲师基本信息和相关课程】

   + 根据讲师id查询讲师基本信息

   + 根据讲师id查询讲师所讲课程

     ```java
     @GetMapping("getTeacherInfoById/{teacherId}")
     @ApiOperation("根据讲师id查询讲师详情和课程列表")
     public ResponseData getTeacherInfoById(
             @ApiParam(name = "teacherId", value = "讲师ID", required = true)
             @PathVariable String teacherId){
         EduTeacher eduTeacher = eduTeacherService.getById(teacherId);
         List<EduCourse> courseList=eduCourseService.getCourseInfoByTeacherId(teacherId);
         return ResponseData.responseCall().data("teacher",eduTeacher).data("courses",courseList);
     }
     ```

     【查关联课程的service层】

     ```java
     @Override
     public List<EduCourse> getCourseInfoByTeacherId(String teacherId) {
         QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
         queryWrapper.eq("teacher_id", teacherId);
         //按照最后更新时间倒序排列
         queryWrapper.orderByDesc("gmt_modified");
         List<EduCourse> courses = baseMapper.selectList(queryWrapper);
         return courses;
     }
     ```

     

2. 前端页面

   > NUXT框架获取动态路由params.id的id要和页面_id保持一样
   >
   > 给多个变量定义赋值用逗号隔开

   ```html
   <section class="container">
   <header class="comm-title">
       <h2 class="fl tac">
           <span class="c-333">讲师介绍</span>
       </h2>
   </header>
   <div class="t-infor-wrap">
       <!-- 讲师基本信息 -->
       <section class="fl t-infor-box c-desc-content">
           <div class="mt20 ml20">
               <section class="t-infor-pic">
                   <img :src="teacher.avatar">
               </section>
               <h3 class="hLh30">
                   <span class="fsize24 c-333">{{teacher.name}}&nbsp;{{teacher.level == 1?'高级讲师':'首席讲师'}}</span>
               </h3>
               <section class="mt10">
                   <span class="t-tag-bg">{{teacher.career}}</span>
               </section>
               <section class="t-infor-txt">
                   <p class="mt20">{{teacher.intro}}</p>
               </section>
               <div class="clear">
               </div>
           </div>
       </section>
       <div class="clear"></div>
       </div>
       <section class="mt30">
           <div>
               <header class="comm-title all-teacher-title c-course-content">
                   <h2 class="fl tac">
                       <span class="c-333">主讲课程</span>
                   </h2>
                   <section class="c-tab-title">
                       <a href="javascript: void(0)">&nbsp;</a>
                   </section>
               </header>
               <!-- /无数据提示 开始-->
               <section class="no-data-wrap" v-if="courses.length==0">
                   <em class="icon30 no-data-ico">&nbsp;</em>
                   <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
               </section>
               <!-- /无数据提示 结束-->
               <article class="comm-course-list">
                   <ul class="of">
                       <li v-for="course in courses" :key="course.id">
                           <div class="cc-l-wrap">
                               <section class="course-img">
                                   <img :src="course.cover" class="img-responsive" >
                                   <div class="cc-mask">
                                       <a :href="'/course/'+course.id" title="开始学习" target="_blank" class="comm-btn c-btn-1">开始学习</a>
                                   </div>
                               </section>
                               <h3 class="hLh30 txtOf mt10">
                                   <a :href="'/course/'+course.id" :title="course.title" target="_blank" class="course-title fsize18 c-333">{{course.title}}</a>
                               </h3>
                           </div>
                       </li>
                   </ul>
                   <div class="clear"></div>
               </article>
           </div>
       </section>
   </section>
   ```

   







## 课程列表

> 一级分类下有二级分类，可以筛选课程列表，即带条件分页查询，可以自主选择关注度、最新、价格做排序

1. 后端接口

   【Vo对象封装查询条件】

   ```java
   @ApiModel(value = "课程查询对象", description = "课程查询对象封装")
   @Data
   public class FrontCourseQueryFactor implements Serializable {
       private static final long serialVersionUID = 1L;
       @ApiModelProperty(value = "课程名称")
       private String title;
       @ApiModelProperty(value = "讲师id")
       private String teacherId;
       @ApiModelProperty(value = "一级类别id")
       private String subjectParentId;
       @ApiModelProperty(value = "二级类别id")
       private String subjectId;
       @ApiModelProperty(value = "销量排序[1表示按销量查询，空串表示不按销量进行查询]",example = "1")
       private String buyCountSort;
       @ApiModelProperty(value = "最新时间排序")
       private String gmtCreateSort;
       @ApiModelProperty(value = "价格排序")
       private String priceSort;
   }
   ```

   【多条件分页查询课程列表控制器方法】

   ```java
   @PostMapping("pageFactorCourse/{curPage}/{limit}")
   public ResponseData pageFactorCourse(
           @ApiParam(name = "curPage", value = "当前页码", required = true)
           @PathVariable Long curPage,
           @ApiParam(name = "limit", value = "每页记录数", required = true)
           @PathVariable Long limit,
           @ApiParam(name = "courseQuery", value = "查询对象", required = false)
           @RequestBody(required = false) FrontCourseQueryFactor courseQueryFactor){
       Page<EduCourse> pageParam = new Page<EduCourse>(curPage, limit);
       Map<String, Object> map = eduCourseService.pageFactorCourse(pageParam, courseQueryFactor);
       return ResponseData.responseCall().data(map);
   }
   ```

   【service方法】

   ```java
   @Override
   public Map<String, Object> pageFactorCourse(Page<EduCourse> pageParam, FrontCourseQueryFactor courseQueryFactor) {
       QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
       //课程一级分类id
       if (!StringUtils.isEmpty(courseQueryFactor.getSubjectParentId())) {
           queryWrapper.eq("subject_parent_id",
                   courseQueryFactor.getSubjectParentId());
       }
       //课程二级分类id
       if (!StringUtils.isEmpty(courseQueryFactor.getSubjectId())) {
           queryWrapper.eq("subject_id", courseQueryFactor.getSubjectId());
       }
       //按照字段排序，前端传的字符串"1",为1就设置对应的字段排序，前端每次点击排序都会将其他排序初始化
       //售货量
       if (!StringUtils.isEmpty(courseQueryFactor.getBuyCountSort())) {
           queryWrapper.orderByDesc("buy_count");
       }
       //课程创建时间
       if (!StringUtils.isEmpty(courseQueryFactor.getGmtCreateSort())) {
           queryWrapper.orderByDesc("gmt_create");
       }
       //课程价格
       if (!StringUtils.isEmpty(courseQueryFactor.getPriceSort())) {
           queryWrapper.orderByDesc("price");
       }
       baseMapper.selectPage(pageParam, queryWrapper);
       List<EduCourse> courseList = pageParam.getRecords();
       long curPage = pageParam.getCurrent();
       long pages = pageParam.getPages();
       long size = pageParam.getSize();
       long total = pageParam.getTotal();
       boolean hasNext = pageParam.hasNext();
       boolean hasPrevious = pageParam.hasPrevious();
       Map<String, Object> map = new HashMap<String, Object>();
       map.put("courses", courseList);
       map.put("curPage", curPage);
       map.put("pages", pages);
       map.put("size", size);
       map.put("total", total);
       map.put("hasNext", hasNext);
       map.put("hasPrevious", hasPrevious);
       return map;
   }
   ```

   

2. 前端实现

   ```html
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
   ```

   

## 课程详情

> 点击课程详情界面跳到新页面，显示课程介绍，并显示课程章节信息，显示讲师头像和简介，需要写SQL查出来，并且在章节信息中整合阿里云的播放器，把视频播放功能整合进去
>
> 编写SQL查询课程基本信息、课程分类、课程描述、所属讲师
>
> 调用之前的方法根据课程id查询章节和小节
>
> 写SQL要注意build目录是否包含mapper的打包目录，配置文件设置mapper.xml的路径【精确到后缀】
>
> 注意课程详情中课程介绍内容有标签，是富文本编辑器自动生成的，如果直接输出到浏览器不会翻译标签，标签会被原样输出，vue中的v-html属性可以把富文本编辑器中的标签翻译成输入富文本编辑器的效果，和v-if、v-for一样的使用方法

1. 前端代码

   ```html
   <template>
       <div id="aCoursesList" class="bg-fa of">
           <!-- /课程详情 开始 -->
           <section class="container">
               <!-- 课程所属分类 开始 -->
               <section class="path-wrap txtOf hLh30">
                   <a href="/" title class="c-999 fsize14">首页</a>
                   \
                   <a href="/course" title class="c-999 fsize14">课程列表</a>
                   \
                   <span class="c-333 fsize14">{{ course.firstLevelSubjectTitle }}</span>
                   \
                   <span class="c-333 fsize14">{{ course.secondLevelSubjectTitle }}</span>
               </section>
               <!-- 课程所属分类 开始 -->
               <!-- 课程基本信息 开始 -->
               <div>
                   <article class="c-v-pic-wrap" style="height: 357px;">
                       <section id="videoPlay" class="p-h-video-box">
                           <img :src="course.cover" :alt="course.title" class="dis c-v-pic">
                       </section>
                   </article>
                   <aside class="c-attr-wrap">
                       <section class="ml20 mr15">
                           <h2 class="hLh30 txtOf mt15">
                               <span class="c-fff fsize24">{{ course.title }}</span>
                           </h2>
                           <section class="c-attr-jg">
                               <span class="c-fff">价格： </span>
                               <b class="c-yellow" style="font-size:24px;">￥{{ course.price}}</b>
                           </section>
                           <section class="c-attr-mt c-attr-undis">
                               <span class="c-fff fsize14">主讲： {{ course.teacherName}}&nbsp;&nbsp;&nbsp;</span>
                           </section>
                           <section class="c-attr-mt of">
                               <span class="ml10 vam">
                                   <em class="icon18 scIcon"/>
                                   <a class="c-fff vam" title="收藏" href="#" >收藏</a>
                               </span>
                           </section>
                           <section class="c-attr-mt">
                               <a href="#" title="立即观看" class="comm-btn c-btn-3">立即观看</a>
                           </section>
                       </section>
                   </aside>
                   <aside class="thr-attr-box">
                       <ol class="thr-attr-ol clearfix">
                           <li>
                               <p>&nbsp;</p>
                               <aside>
                                   <span class="c-fff f-fM">购买数</span>
                                   <br>
                                   <h6 class="c-fff f-fM mt10">{{ course.buyCount }}</h6>
                               </aside>
                           </li>
                           <li>
                               <p>&nbsp;</p>
                               <aside>
                                   <span class="c-fff f-fM">课时数</span>
                                   <br>
                                   <h6 class="c-fff f-fM mt10">{{ course.courseTotalTime }}</h6>
                               </aside>
                           </li>
                           <li>
                               <p>&nbsp;</p>
                               <aside>
                                   <span class="c-fff f-fM">浏览数</span>
                                   <br>
                                   <h6 class="c-fff f-fM mt10">{{ course.viewCount }}</h6>
                               </aside>
                           </li>
                       </ol>
                   </aside>
                   <div class="clear"/>
               </div>
               <!-- /课程基本信息 结束 -->
               <!-- /课程封面介绍 -->
               <div class="mt20 c-infor-box">
                   <article class="fl col-7">
                       <section class="mr30">
                           <div class="i-box">
                               <div>
                                   <section id="c-i-tabTitle" class="c-infor-tabTitle c-tab-title">
                                       <a name="c-i" class="current" title="课程详情">课程详情</a>
                                   </section>
                               </div>
                               <article class="ml10 mr10 pt20">
                               <!-- 课程详情介绍 开始 -->
                                   <div>
                                       <h6 class="c-i-content c-infor-title">
                                           <span>课程介绍</span>
                                       </h6>
                                       <div class="course-txt-body-wrap">
                                           <section class="course-txt-body">
                                               <!-- 将内容中的html翻译过来 -->
                                               <p v-html="course.description"></p>
                                           </section>
                                       </div>
                                   </div>
                               <!-- /课程详情介绍 结束 -->
                               <!-- 课程大纲 开始-->
                                   <div class="mt50">
                                       <h6 class="c-g-content c-infor-title">
                                           <span>课程大纲</span>
                                       </h6>
                                       <section class="mt20">
                                           <div class="lh-menu-wrap">
                                               <menu id="lh-menu" class="lh-menu mt10 mr10">
                                                   <ul>
                                                   <!-- 课程章节目录 -->
                                                       <li v-for="chapter in chapters" :key="chapter.id" class="lh-menu-stair">
                                                           <a :title="chapter.title" href="javascript: void(0)" class="current-1">
                                                               <em class="lh-menu-i-1 icon18 mr10"/>{{ chapter.title}}
                                                           </a>
                                                           <ol class="lh-menu-ol" style="display: block;">
                                                               <li v-for="video in chapter.children" :key="video.id" class="lh-menu-second ml30">
                                                                   <a href="#" title>
                                                                       <span v-if="video.free === true" class="fr">
                                                                           <i class="free-icon vam mr10">免费试听</i>
                                                                       </span>
                                                                       <em class="lh-menu-i-2 icon16 mr5">&nbsp;</em>{{ video.title }}
                                                                   </a>
                                                               </li>
                                                           </ol>
                                                       </li>
                                                   </ul>
                                               </menu>
                                           </div>
                                       </section>
                                   </div>
                                   <!-- /课程大纲 结束 -->
                               </article>
                           </div>
                       </section>
                   </article>
                   <aside class="fl col-3">
                       <div class="i-box">
                       <!-- 主讲讲师 开始-->
                           <div>
                               <section class="c-infor-tabTitle c-tab-title">
                                   <a title href="javascript:void(0)">主讲讲师</a>
                               </section>
                               <section class="stud-act-list">
                                   <ul style="height: auto;">
                                       <li>
                                           <div class="u-face">
                                               <a :href="'/teacher/'+course.teacherId" target="_blank">
                                                   <img :src="course.avatar" width="50" height="50" alt>
                                               </a>
                                           </div>
                                           <section class="hLh30 txtOf">
                                               <a :href="'/teacher/'+course.teacherId" class="c-333 fsize16 fl" target="_blank">{{ course.teacherName }}</a>
                                           </section>
                                           <section class="hLh20 txtOf">
                                               <span class="c-999">{{ course.intro }}</span>
                                           </section>
                                       </li>
                                   </ul>
                               </section>
                           </div>
                       <!-- /主讲讲师 结束 -->
                       </div>
                   </aside>
                   <div class="clear"></div>
               </div>
           </section>
       <!-- /课程详情 结束 -->
       </div>
   </template>
   
   <script>
       import course from "@/api/course"
       export default {
           asyncData({ params, error }) {
               return course.getCourseInfoByCourseId(params.id).then(response => {
                   return {
                       course: response.data.data.course,
                       chapters: response.data.data.chapters
                   }
               })
           }
       }
   </script>
   ```

2. 后端接口

   【controller】

   ```java
   @Api(description = "前台课程管理")
   @RestController
   @CrossOrigin
   @RequestMapping("/eduservice/front")
   public class FrontCourseController {
       @Autowired
       EduCourseService eduCourseService;
       @Autowired
       EduChapterService eduChapterService;
   
       @ApiOperation("根据课程id查询前台课程信息详情")
       @GetMapping("getFrontCourseInfo/{courseId}")
       public ResponseData getFrontCourseInfo(
               @ApiParam(name = "courseId", value = "课程ID", required = true)
               @PathVariable String courseId){
           //查询课程信息和讲师信息
           FrontCourseInfo course = eduCourseService.getFrontCourseInfoByCourseId(courseId);
           //查询当前课程的章节信息
           List<Chapter> chapters = eduChapterService.getChaptersByCourseId(courseId);
           return ResponseData.responseCall().data("course", course).data("chapters", chapters);
       }
   }
   ```

   【service】

   ```java
   /**
    * @param courseId
    * @描述 每次获取课程信息展示在前台都会增加课程浏览数,返回true表示更新成功，返回false表示更新失败
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/10
    * @since 1.0.0
    */
   @Override
   public boolean updateCourseViewCount(String courseId) {
       EduCourse eduCourse = getById(courseId);
       eduCourse.setViewCount(eduCourse.getViewCount()+1);
       return updateById(eduCourse);
   }
   
   /**
    * @param courseId
    * @return {@link FrontCourseInfo }
    * @描述 根据课程id查询前台课程信息【包含讲师信息，多表连接查询，需要手写sql】
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/10
    * @since 1.0.0
    */
   @Override
   public FrontCourseInfo getFrontCourseInfoByCourseId(String courseId) {
       //更新课程浏览数
       if (! updateCourseViewCount(courseId)){
           throw new CustomException(20001,"课程浏览数更新失败!");
       }
       return baseMapper.getFrontCourseInfoByCourseId(courseId);
   }
   ```

   【mapper.xml】

   > 注意：mapper.xml中写的sql需要再mapper.java中声明对应的方法，就是可以通过mapper调用，会在mapper中动态生成方法，注意该方法必须在service中进行调用才能生效

   ```xml
   <select id="getFrontCourseInfoByCourseId" resultType="com.atlisheng.eduservice.entity.bo.course.FrontCourseInfo">
       SELECT
           cou.id,
           cou.title,
           cou.cover,
           CONVERT(cou.price, DECIMAL(8,2)) AS price,
           cou.course_total_time AS courseTotalTime,
           cou.cover,
           cou.buy_count AS buyCount,
           cou.view_count AS viewCount,
           coudes.description,
           tea.id AS teacherId,
           tea.name AS teacherName,
           tea.intro,
           tea.avatar,
           subj1.id AS firstLevelSubjectId,
           subj1.title AS firstLevelSubjectTitle,
           subj2.id AS secondLevelSubjectId,
           subj2.title AS secondLevelSubjectTitle
       FROM
           edu_course cou
               LEFT JOIN edu_course_description coudes ON cou.id = coudes.id
               LEFT JOIN edu_teacher tea ON cou.teacher_id = tea.id
               LEFT JOIN edu_subject subj1 ON cou.subject_parent_id = subj1.id
               LEFT JOIN edu_subject subj2 ON cou.subject_id = subj2.id
       WHERE
           cou.id = #{courseId}
   </select>
   ```

   



## 整合阿里云播放器视频播放功能

> 视频播放有两种方式，一种是地址播放，一种是凭证播放；推荐用凭证播放，用地址播放加密后的视频是无法播放的，因为视频存在阿里云上，视频需要使用阿里云播放器才能播放，别的播放器理论上是播放不了的

1. 无加密视频的阿里云播放器整合

   ```html
   <link rel="stylesheet" href="https://g.alicdn.com/de/prismplayer/2.8.1/skins/default/aliplayer-min.css" />
   <script charset="utf-8" type="text/javascript" src="https://g.alicdn.com/de/prismplayer/2.8.1/aliplayer-min.js">
   </script>
   <body>
       <div class="prism-player" id="J_prismPlayer">
   
       </div>
       <script>
           var player = new Aliplayer({
               id: 'J_prismPlayer',
               width: '100%',//这个是设置播放器的宽度
               autoplay: false,//这个设置为true表示浏览器一打开就开始播放
               cover: 'http://liveroom-img.oss-cn-qingdao.aliyuncs.com/logo.png',
               //播放配置
               //1.根据地址进行播放配置，
               //直接在source属性后加播放地址，但是这种方式只适合不加密视频的播放
               //source: '播放地址'
               source: 'https://outin-854fba4f5c3911eebcce00163e0edab2.oss-cn-shanghai.aliyuncs.com/sv/622427d8-18b040c097e/622427d8-18b040c097e.mp4?Expires=1696941043&OSSAccessKeyId=LTAIxSaOfEzCnBOj&Signature=lUQ2LE7U%2BzB6TU%2Bj8IRg2b4wSxA%3D'
               //播放凭证进行播放
           },function(player){
               console.log('播放器创建好了。 ')
           });
       </script>
   </body>
   ```

2. 有加密的视频播放器整合

   ```html
   <link rel="stylesheet" href="https://g.alicdn.com/de/prismplayer/2.8.1/skins/default/aliplayer-min.css" />
   <script charset="utf-8" type="text/javascript" src="https://g.alicdn.com/de/prismplayer/2.8.1/aliplayer-min.js">
   </script>
   <body>
       <div class="prism-player" id="J_prismPlayer">
   
       </div>
       <script>
           var player = new Aliplayer({
               id: 'J_prismPlayer',
               width: '100%',//这个是设置播放器的宽度
               autoplay: false,//这个设置为true表示浏览器一打开就开始播放
               cover: 'http://liveroom-img.oss-cn-qingdao.aliyuncs.com/logo.png',
               //播放配置
               //2.根据凭证进行播放配置，
               //如果播放加密视频，则配置encryptType=1，非加密视频无需设置此项，非加密视频不用加
               encryptType : '1',
               //视频id
               vid : '98d5be205c3971eebfbd0675a0ec0102',
               //视频播放凭证
               playauth : 'eyJTZWN1cml0eVRva2VuIjoiQ0FJU2lBTjFxNkZ0NUIyeWZTaklyNWJ2QVBEZDJwSkkxcW1mUkVEWmxtNWdWZXQwbjdUdm16ejJJSGhNZUhkaEFPMFl0Lzgxbm10UTZ2b2NscklxRmNNZUhCMmZQWkV1dHM4TXJGbjRKb0hidk5ldTBic0hoWnY5K2UxYnJzZWlqcUhvZU96Y1lJNzMwWjdQQWdtMlEwWVJySkwrY1RLOUphYk1VL21nZ29KbWFkSTZSeFN4YVNFOGF2NWRPZ3BscnIwSVZ4elBNdnIvSFJQMnVtN1pIV3R1dEEwZTgzMTQ1ZmFRejlHaTZ4YlRpM2I5ek9FVXFPYVhKNFMvUGZGb05ZWnlTZjZvd093VUVxL2R5M3hvN3hGYjFhRjRpODRpL0N2YzdQMlFDRU5BK3dtbFB2dTJpOE5vSUYxV2E3UVdJWXRncmZQeGsrWjEySmJOa0lpbDVCdFJFZHR3ZUNuRldLR216c3krYjRIUEROc2ljcXZoTUhuZ3k4MkdNb0tQMHprcGVuVUdMZ2hIQ2JGRFF6MVNjVVZ5Rm1xRGUvVDNvZzZTT1Z6K0VmRy92ZnRvZ2NZdi9UTEI1c0dYSWxXRGViS2QzQnNETjRVMEIwRlNiRVpPZ1RLd0svSllLbGNXS1FnL1crckxWL2xhYUJGUHRLWFdtaUgrV3lOcDAzVkxoZnI2YXVOcGJnUHIxVVFwTlJxQUFSSzlBKzN2azV2MldGRWJjSCtSUGJ1KzAybm1MMExPK3A1R3hDN3Jidk5hb1UveElCK3htWkhFN0RJMEFvL0lEQlRzN29uZHZFUGJVa2JwSFZuK1QxTnk1eUpQY2o2UjhpLzBsanhuZ3hMOWQ3d2JHNVhqVnB4NEdXSzRqL3Fxc256S3RnQWhHejdjRkZMUy9mM0c0QWZkUElucVpiZkpvSXdZRUZWVVlhNzdJQUE9IiwiQXV0aEluZm8iOiJ7XCJDSVwiOlwid1A1cFZoRFZQQlYwM2ZwQm94WTdzWkUyaG83Y2RBRzk1RzBYZjhpdk5tcHRidGg0SUczM2xpQkRiaEtjb3FwTVwiLFwiQ2FsbGVyXCI6XCJuOFpZblI3OEtRTGhRM2g4SlNHUzIyR0JVM2tFU1BkNXMxWnYycEZTeEdFPVwiLFwiRXhwaXJlVGltZVwiOlwiMjAyMy0xMC0xMFQxMTo0NzoxNlpcIixcIk1lZGlhSWRcIjpcIjk4ZDViZTIwNWMzOTcxZWViZmJkMDY3NWEwZWMwMTAyXCIsXCJTaWduYXR1cmVcIjpcIjVYR1lGZXZBTm50SDRlZ1BqZFdBakFwK2Qvaz1cIn0iLCJWaWRlb01ldGEiOnsiU3RhdHVzIjoiTm9ybWFsIiwiVmlkZW9JZCI6Ijk4ZDViZTIwNWMzOTcxZWViZmJkMDY3NWEwZWMwMTAyIiwiVGl0bGUiOiI2IC0gV2hhdCBJZiBJIFdhbnQgdG8gTW92ZSBGYXN0ZXIubXA0IiwiQ292ZXJVUkwiOiJodHRwOi8vb3V0aW4tODU0ZmJhNGY1YzM5MTFlZWJjY2UwMDE2M2UwZWRhYjIub3NzLWNuLXNoYW5naGFpLmFsaXl1bmNzLmNvbS85OGQ1YmUyMDVjMzk3MWVlYmZiZDA2NzVhMGVjMDEwMi9zbmFwc2hvdHMvZjk5Y2FlZTVmNzk2NDlhMzhmYTllYWNmMDhhNjcwMGItMDAwMDEuanBnP0V4cGlyZXM9MTY5Njk0MTkzNiZPU1NBY2Nlc3NLZXlJZD1MVEFJeFNhT2ZFekNuQk9qJlNpZ25hdHVyZT1rZHBJRnc4RFlhQng3Z0o2UnVVOXdmbFhFTmslM0QiLCJEdXJhdGlvbiI6MTYuMjc2N30sIkFjY2Vzc0tleUlkIjoiU1RTLk5VWktKaTdNaWFrdEZmaHJuNVlkWHBxRHkiLCJBY2Nlc3NLZXlTZWNyZXQiOiI3WFNhUnFUZm9MWFk5VWtFS0dIVW1IaXVYWmhVb0NqeHppeGEzalltS01qeiIsIlJlZ2lvbiI6ImNuLXNoYW5naGFpIiwiQ3VzdG9tZXJJZCI6MTY0MzE5MzgyNjAzMzUyMX0'
           },function(player){
               console.log('播放器创建好了。 ')
           });
       </script>
   </body>
   ```

### 后端接口

> 根据视频id获取视频播放凭证

1. 【通过视频id获取播放凭证】

   ```java
   /**
    * @param videoId
    * @return {@link String }
    * @描述 根据视频id获取视频播放凭证
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/10
    * @since 1.0.0
    */
   @Override
   public String getPlayAuth(String videoId) {
       //初始化客户端、请求对象和相应对象
       DefaultAcsClient client = ALiYunVodUtil.initVodClient(ConstantProperties.ACCESS_KEY_ID, ConstantProperties.ACCESS_KEY_SECRET);
       GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
       GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
       try {
           //设置请求参数
           request.setVideoId(videoId);
           //获取请求响应
           response = client.getAcsResponse(request);
           //返回播放凭证
           return response.getPlayAuth();
       } catch (Exception e) {
           throw new CustomException(20001,e.getLocalizedMessage());
       }
   }
   ```



### 前端页面

> 谷粒学院是点击小节，会弹框然后播放视频
>
> 51cto会弹出新页面，在该页面做视频播放
>
> 这里采取51cto的方式，在新页面做视频播放
>
> 用异步的请求方式页面渲染创建好以后数据不一定有，要在mouted方法中当页面数据获取渲染之后再对mounted进行调用
>
> 点击超链接打开新页面是在超链接中添加target属性并设定值为"_blank"
>
> 视频播放器中插入广告，开通vip、视频列表、弹幕、倍速播放、字幕等组件在阿里云播放器中的功能展示都有相关的示例等，有直接的代码可以拷贝

【layout】

```html
<template>
    <div class="guli-player">
        <div class="head">
            <a href="/" title="谷粒学院">
                <img class="logo" src="~/assets/img/logo.png" lt="谷粒学院">
            </a>
        </div>
        <div class="body">
            <div class="content">
                <nuxt/>
            </div>
        </div>
    </div>
</template>
<script>
    export default {}
</script>
<style>
html,body{
    height:100%;
}
</style>
<style scoped>
.head {
    height: 50px;
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
}
.head .logo{
    height: 50px;
    margin-left: 10px;
}
.body {
    position: absolute;
    top: 50px;
    left: 0;
    right: 0;
    bottom: 0;
    overflow: hidden;
}
</style>
```

【阿里云播放器组件】

```html
<template>
    <div>
        <!-- 阿里云视频播放器样式 -->
        <link rel="stylesheet" href="https://g.alicdn.com/de/prismplayer/2.8.1/skins/default/aliplayer-min.css" >
        <!-- 阿里云视频播放器脚本 -->
        <script charset="utf-8" type="text/javascript" src="https://g.alicdn.com/de/prismplayer/2.8.1/aliplayer-min.js" >
        </script>
        <!--定义播放器dom-->
        <div id="J_prismPlayer" class="prism-player" />
    </div>
</template>
<script>
    import vod from '@/api/vod'
    export default {
        layout: 'video',//应用video布局
        asyncData({ params, error }) {
            return vod.getPlayAuth(params.vid).then(response => {
                // console.log(response.data.data)
                return {
                    vid: params.vid,
                    videoAuth: response.data.data.videoAuth
                }
            })
        },
        /**
        * 页面渲染完成时：此时js脚本已加载， Aliplayer已定义，可以使用
        * 如果在created生命周期函数中使用， Aliplayer is not defined错误
        */
        mounted() {
            new Aliplayer({
                id: 'J_prismPlayer',
                vid: this.vid, // 视频id
                playauth: this.videoAuth, // 播放凭证
                encryptType: '1', // 如果播放加密视频，则需设置encryptType=1，非加密视频无需设置此项
                width: '100%',
                height: '500px',
                // 以下可选设置
                cover: 'https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/03/364822ae6edd429db705a06be39a660c高放废液玻璃固化.jpg', // 封面
                qualitySort: 'asc', // 清晰度排序
                mediaType: 'video', // 返回音频还是视频
                autoplay: false, // 自动播放
                isLive: false, // 直播
                rePlay: false, // 循环播放
                preload: true,
                controlBarVisibility: 'hover', // 控制条的显示方式：鼠标悬停
                useH5Prism: true // 播放器类型： html5
            }, function(player) {
                console.log('播放器创建成功')
            })
        }      
    }
</script>
```



## 课程评论功能

> 需求：以下需求一般是项目经理负责
>
> 课程下方有一个输入评论的框，有回复两个字，在评论框输入评论后回车评论可以被展示
>
> 评论展示为 人名  评论内容
>
> 最下方是评论分页条
>
> 评论表包括课程id，讲师id，用户id，头像、昵称【可以优化多表查询连接查询，这个评论量一大占用空间很大】
>
> 评论之前要提示先登录，不支持匿名评论
>
> 控制器方法：
>
> + 分页查询课程评论
>
> + 添加评论【要添加的数据包括课程评论内容[由用户输入]、课程和讲师id[由课程详情页面可以查到]、用户信息可以由当前登录用户的token获取用户id，根据用户id查询用户信息】
>
>   > 在service中实现定义添加评论的内容，在user中实现获取用户信息的功能

1. 前端构建

   ```html
   <!--课程评论开始-->
           <div class="mt50 commentHtml">
               <div>
                   <h6 class="c-c-content c-infor-title" id="i-art-comment">
                       <span class="commentTitle">课程评论</span>
                   </h6>
                   <section class="lh-bj-list pr mt20 replyhtml">
                       <ul>
                           <li class="unBr">
                               <aside class="noter-pic">
                                   <img width="50" height="50" class="picImg" src="~/assets/img/avatar-boy.gif">
                               </aside>
                               <div class="of">
                                   <section class="n-reply-wrap">
                                       <fieldset>
                                           <textarea name="" v-model="comment.content" placeholder="输入您要评论的文字" id="commentContent"></textarea>
                                       </fieldset>
                                       <p class="of mt5 tar pl10 pr10">
                                           <span class="fl ">
                                               <tt class="c-red commentContentmeg" style="display: none;"></tt>
                                           </span>
                                           <input type="button" @click="addComment" value="发布" class="lh-reply-btn">
                                       </p>
                                   </section>
                               </div>
                           </li>
                       </ul>
                   </section>
                   <section class="">
                       <section class="question-list lh-bj-list pr">
                           <ul class="pr10">
                               <li v-for="(comment,index) in data.comments" v-bind:key="index">
                                   <aside class="noter-pic">
                                       <img width="50" height="50" class="picImg" :src="comment.avatar">
                                   </aside>
                                   <div class="of">
                                       <span class="fl">
                                           <font class="fsize12 c-blue">{{comment.nickname}}</font>
                                           <font class="fsize12 c-999 ml5">评论： </font>
                                       </span>
                                   </div>
                                   <div class="noter-txt mt5">
                                       <p>{{comment.content}}</p>
                                   </div>
                                   <div class="of mt5">
                                       <span class="f2">
                                           <font class="fsize12 c-999 ml5">第{{index}}层</font>
                                       </span>
                                       <span class="fr">
                                           <font class="fsize12 c-999ml5">{{comment.gmtCreate}}</font>
                                       </span>
                                   </div>
                               </li>
                           </ul>
                       </section>
                   </section>
                   <!-- 公共分页 开始 -->
                   <div class="paging">
                       <!-- undisable这个class是否存在，取决于数据属性hasPrevious -->
                       <a :class="{undisable: !data.hasPrevious}" href="#" title="首页" @click.prevent="gotoPage(1)">首</a>
                       <a :class="{undisable: !data.hasPrevious}" href="#" title="前一页" @click.prevent="gotoPage(data.curPage-1)">&lt;</a>
                       <a v-for="page in data.pages" :key="page" :class="{current: data.curPage == page, undisable: data.curPage == page}"
                       :title="'第'+page+'页'" href="#" @click.prevent="gotoPage(page)">{{ page }}</a>
                       <a :class="{undisable: !data.hasNext}" href="#" title="后一页" @click.prevent="gotoPage(data.curPage+1)">&gt;</a>
                       <a :class="{undisable: !data.hasNext}" href="#" title="末页" @click.prevent="gotoPage(data.pages)">末</a>
                       <div class="clear"/>
                   </div>
                   <!-- 公共分页 结束 -->
               </div>
           </div>
       </article>
   </div>
   ```

   【对应的方法】

   ```javascript
   <script>
       import course from "@/api/course"
       import comment from "@/api/comment"
       export default {
           asyncData({ params, error }) {
               return course.getCourseInfoByCourseId(params.id).then(response => {
                   return {
                       courseId: params.id,
                       course: response.data.data.course,
                       chapters: response.data.data.chapters
                   }
               })
           },
           data() {
               return {
                   data:{},
                   curPage:1,
                   limit:20,
                   total:10,
                   comment:{
                       content: '',
                       courseId: '',
                       teacherId: ''
                   },
                   chapterVideoList:[],
                   isbuyCourse:false
               }
           },
           mounted() {
               this.initComment()
           },
           methods:{
               initComment(){
                   comment.getCommentByCourseId(this.courseId, this.curPage, this.limit,).then(response =>{
                       //获取评论和封装的数据
                       this.data = response.data.data
                   })
               },
               addComment(){
                   this.comment.courseId = this.courseId
                   this.comment.teacherId = this.course.teacherId
                   console.log(this.comment)
                   comment.addComment(this.comment).then(response => {
                       this.comment.content = ''
                       this.initComment()
                   }).catch(()=>{
                       console.log(error)
                       //this.$message({
                           //type: 'error',
                           //message: '评论失败，请联系管理员'
                       //})
                   })
               },
               gotoPage(page){
                   comment.getCommentByCourseId(this.courseId, page, this.limit).then(response =>{
                       this.data = response.data.data
                   })
               }
           }
       };
   </script>
   ```

2. 后端实现

   > 前端控制器，没有区分服务层，这不是一个好习惯

   ```java
   @Api(description = "前台评论管理")
   @RestController
   @CrossOrigin
   @RequestMapping("/eduservice/frontcomment")
   public class FrontCommentController {
       @Autowired
       private EduCommentService commentService;
       @Autowired
       private UserClient userClient;
   
       /**
        * @param curPage
        * @param limit
        * @param courseId
        * @return {@link ResponseData }
        * @描述 根据课程id分页查询评论，没有实现评论嵌套，即回复评论的评论
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/10/11
        * @since 1.0.0
        *///根据课程id查询评论列表
       @ApiOperation(value = "评论分页列表")
       @GetMapping("getCommentByCourseId/{courseId}/{curPage}/{limit}")
       public ResponseData getCommentByCourseId(
               @ApiParam(name = "curPage", value = "当前页码", required = true)
               @PathVariable("curPage") Long curPage,
               @ApiParam(name = "limit", value = "每页记录数", required = true)
               @PathVariable("limit") Long limit,
               @ApiParam(name = "courseId", value = "查询对象", required = true)
               @PathVariable("courseId") String courseId) {
           Page<EduComment> pageParam = new Page<>(curPage, limit);
           QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
           wrapper.eq("course_id",courseId);
           commentService.page(pageParam,wrapper);
           List<EduComment> commentList = pageParam.getRecords();
           Map<String, Object> map = new HashMap<>();
           map.put("comments", commentList);
           map.put("curPage", pageParam.getCurrent());
           map.put("pages", pageParam.getPages());
           map.put("size", pageParam.getSize());
           map.put("total", pageParam.getTotal());
           map.put("hasNext", pageParam.hasNext());
           map.put("hasPrevious", pageParam.hasPrevious());
           return ResponseData.responseCall().data(map);
       }
   
       /**
        * @param comment
        * @param request
        * @return {@link ResponseData }
        * @描述 添加评论，前端需要传入评论内容，课程id，
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/10/11
        * @since 1.0.0
        */
       @ApiOperation(value = "添加评论")
       @PostMapping("addComment")
       public ResponseData addComment(@RequestBody EduComment comment, HttpServletRequest request) {
           String userId = JwtUtils.getMemberIdByJwtToken(request);
           if(StringUtils.isEmpty(userId)) {
               return ResponseData.responseCall().code(28004).message("请先登录");
           }
           comment.setUserId(userId);
           ResponseData response = userClient.getLoginInfo(userId);
           Map<String, Object> userInfo =  response.getData();
           comment.setNickname((String)userInfo.get("nickname"));
           comment.setAvatar((String)userInfo.get("avatar"));
           commentService.save(comment);
           return ResponseData.responseCall();
       }
   }
   ```

   【服务调用】

   ```java
   @Component
   @FeignClient(name="service-user",fallback = UserClientImpl.class)
   public interface UserClient {
       //根据用户id获取用户信息
       @GetMapping("/eduuser/ucenter/getUserByUserId/{userId}")
       public ResponseData getLoginInfo(@PathVariable("userId") String userId);
   }
   ```

   【服务调用熔断执行】

   ```java
   @Component
   public class UserClientImpl implements UserClient {
       @Override
       public ResponseData getLoginInfo(String userId) {
           return ResponseData.responseErrorCall().message("获取用户信息 time out...");
       }
   }
   ```

   





## 微信支付功能

> 如果课程免费，可以直接观看，如果课程不免费，点击立即购买跳转订单生成页面，订单页面注意物品只能选择购买一个，点击去支付跳转支付流程，微信支付完成后原来点击购买的按钮变成了立即观看
>
> 点击立即购买，生成一个订单，向订单中添加一条记录，为了方便支付测试，把价格都设置成0.01,支付了100没地方退款
>
> 手机扫完二维码就会在表pay_log中生成一个支付日志记录【交易状态，微信后台可能存在延迟，判断支付是否成功的标志，必须等待成功再做其他事情】
>
> 涉及接口：
>
> + 生成订单接口【远程调用edu服务获取课程信息和讲师信息，远程调用user服务从token获取用户id获取用户信息添加到订单表】
> + 根据订单号查询订单信息的接口
> + 生成微信支付二维码的接口
> + 查询订单微信支付状态是否成功的接口，因为微信支付存在延迟
>
> 流程：
>
> + 点击立即购买--创建订单，生成返回订单号-->前端跳转商品去支付页面，调用后端查询课程信息接口将课程信息查询出来-->点击去支付调用后端生成微信支付二维码接口跳转支付页面-->支付成功前端隔3秒调用后端接口查询一次订单状态，查询支付成功后生成支付记录并修改订单的支付状态为1跳转立刻学习页面-->根据课程id和用户id判断课程用户是否已经购买，已经购买或者免费的课程用v-if和v-else实现选项切换
>
> 微信支付二维码生成
>
> + 这个和登录注册的二维码不同，哪个是直接输入地址就能返回二维码；这个支付二维码需要使用vue的组件进行下载
>
> + 但是还是要注册微信开发者，只支持企业用户
>
>   > 测试信息如下，生产中公司已经准备好了，主要使用微信id、商户号和商户key
>
>   ```yaml
>   weixin:
>     pay:
>       #关联的公众号appid
>       appid: wx74862e0dfcf69954
>       #商户号
>       partner: 1558950191
>       #商户key
>       partnerkey: T6m9iK73b0kn9g5v426MKfHQH7X8rKwb
>       #回调地址
>       notifyurl: http://guli.shop/api/order/weixinPay/weixinNotify
>   ```
>
> 扫描二维码支付以后需要
>
> + 查询是否支付成功，第一要在支付记录表中添加一条记录，第二要将订单表中的记录的status字段改成1，并在订单支付状态验证方法中远程调用将课程的购买数量加1
>
> bug：
>
> + 支付以后还没返回结果但是把浏览器页面关闭了，后面创建支付记录的方法无法执行了，为什么微信支付成功后不直接发一次请求告诉服务器成功了
> + 多次点击购买会生成多条订单记录，可能被攻击
> + 小节列表的状态是否免费定义的是是否可以试看课程，不免费表示不能试看，不能跳转视频播放页面，是否有bug直接拷贝id到播放器地址就能观看，如果有bug在获取视频凭证的时候就要判断在未支付情况下且不免费的情况下不能获取视频凭证，返回请购买课程的提示信息
> + 没登录点击购买应该跳转登录页面
> + 购买以后还要把课程的购买数加1
> + 此外QQ登录注册和支付宝支付功能没有实现

1. 后端接口

   > 太多了，懒得写，流程看文档，注意以下几点

   + 服务调用最好不要用统一返回格式封装vo和bo类，存在数据传输过程类型统一转换成Map类型，导致数据类型如BigDecimal失真变成Double，又转换成decimal可能导致数据出错，直接把服务之间调用的vo类和bo类抽象成公共类，服务调用直接返回对应数据类型即可

   + 多学习一下实际支付的代码和处理流程，这个支付判断很大程度依赖支付过程前端页面的定时器任务，如果页面丢失，设备没电，支付结果就会丢失，这是很严重的bug，实际生产是怎么写的，学习一下

   + 涉及到的工具类标记一下

     【发送微信支付请求的工具类】

     ```java
     package com.atlisheng.eduorder.utils;
     import org.apache.http.Consts;
     import org.apache.http.HttpEntity;
     import org.apache.http.NameValuePair;
     import org.apache.http.client.ClientProtocolException;
     import org.apache.http.client.entity.UrlEncodedFormEntity;
     import org.apache.http.client.methods.*;
     import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
     import org.apache.http.conn.ssl.SSLContextBuilder;
     import org.apache.http.conn.ssl.TrustStrategy;
     import org.apache.http.entity.StringEntity;
     import org.apache.http.impl.client.CloseableHttpClient;
     import org.apache.http.impl.client.HttpClients;
     import org.apache.http.message.BasicNameValuePair;
     import org.apache.http.util.EntityUtils;
     
     import javax.net.ssl.SSLContext;
     import java.io.IOException;
     import java.security.cert.CertificateException;
     import java.security.cert.X509Certificate;
     import java.text.ParseException;
     import java.util.HashMap;
     import java.util.LinkedList;
     import java.util.List;
     import java.util.Map;
     
     /**
      * @author Earl
      * @version 1.0.0
      * @描述 http请求客户端,封装了HttpClient请求发送和Map转成xml格式，以及xml格式向Map集合的转换
      * @创建日期 2023/10/12
      * @since 1.0.0
      */
     public class HttpClient {
         private String url;
         private Map<String, String> param;
         private int statusCode;
         private String content;
         private String xmlParam;
         private boolean isHttps;
     
         public boolean isHttps() {
             return isHttps;
         }
     
         public void setHttps(boolean isHttps) {
             this.isHttps = isHttps;
         }
     
         public String getXmlParam() {
             return xmlParam;
         }
     
         public void setXmlParam(String xmlParam) {
             this.xmlParam = xmlParam;
         }
     
         public HttpClient(String url, Map<String, String> param) {
             this.url = url;
             this.param = param;
         }
     
         public HttpClient(String url) {
             this.url = url;
         }
     
         public void setParameter(Map<String, String> map) {
             param = map;
         }
     
         public void addParameter(String key, String value) {
             if (param == null)
                 param = new HashMap<String, String>();
             param.put(key, value);
         }
     
         public void post() throws ClientProtocolException, IOException {
             HttpPost http = new HttpPost(url);
             setEntity(http);
             execute(http);
         }
     
         public void put() throws ClientProtocolException, IOException {
             HttpPut http = new HttpPut(url);
             setEntity(http);
             execute(http);
         }
     
         public void get() throws ClientProtocolException, IOException {
             if (param != null) {
                 StringBuilder url = new StringBuilder(this.url);
                 boolean isFirst = true;
                 for (String key : param.keySet()) {
                     if (isFirst)
                         url.append("?");
                     else
                         url.append("&");
                     url.append(key).append("=").append(param.get(key));
                 }
                 this.url = url.toString();
             }
             HttpGet http = new HttpGet(url);
             execute(http);
         }
     
         /**
          * set http post,put param
          */
         private void setEntity(HttpEntityEnclosingRequestBase http) {
             if (param != null) {
                 List<NameValuePair> nvps = new LinkedList<NameValuePair>();
                 for (String key : param.keySet())
                     nvps.add(new BasicNameValuePair(key, param.get(key))); // 参数
                 http.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8)); // 设置参数
             }
             if (xmlParam != null) {
                 http.setEntity(new StringEntity(xmlParam, Consts.UTF_8));
             }
         }
     
         private void execute(HttpUriRequest http) throws ClientProtocolException,
                 IOException {
             CloseableHttpClient httpClient = null;
             try {
                 if (isHttps) {
                     SSLContext sslContext = new SSLContextBuilder()
                             .loadTrustMaterial(null, new TrustStrategy() {
                                 // 信任所有
                                 public boolean isTrusted(X509Certificate[] chain,
                                                          String authType)
                                         throws CertificateException {
                                     return true;
                                 }
                             }).build();
                     SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                             sslContext);
                     httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                             .build();
                 } else {
                     httpClient = HttpClients.createDefault();
                 }
                 CloseableHttpResponse response = httpClient.execute(http);
                 try {
                     if (response != null) {
                         if (response.getStatusLine() != null)
                             statusCode = response.getStatusLine().getStatusCode();
                         HttpEntity entity = response.getEntity();
                         // 响应内容
                         content = EntityUtils.toString(entity, Consts.UTF_8);
                     }
                 } finally {
                     response.close();
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             } finally {
                 httpClient.close();
             }
         }
     
         public int getStatusCode() {
             return statusCode;
         }
     
         public String getContent() throws ParseException, IOException {
             return content;
         }
     }
     ```

     【生成订单号的工具类】

     > 实际没用这个工具类生成订单号，实际使用的是mp的IdWorker.getIdStr()方法生成的订单号

     ```java
     public class OrderUtil {
         /**
          * @return {@link String }
          * @描述 生成订单号
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/12
          * @since 1.0.0
          */
         public static String generateOrderNo() {
             SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
             String newDate = sdf.format(new Date());
             String result = "";
             Random random = new Random();
             for (int i = 0; i < 3; i++) {
                 result += random.nextInt(10);
             }
             return newDate + result;
         }
     }
     ```

   + 该模块和edu模块存在多次相互调用，留意一下，此外还有mp逻辑删除插件也需要引入，这点课堂没说

   + 购买数量加1的服务调用也在订单支付确认中实现了，课程没有涉及

   + 一定要留意支付二维码的下载前端插件和element-ui一起下载过了





2. 前端页面

   + 项目源码中的静态资源文件覆盖项目前端的assets文件，因为之前没有添加支付相关的静态资源
   + 前端页面主要在course/id，order目录下、pay目录下
   + 比较重要的就是定时任务验证支付状态、异步调用的变量获取值较慢，可能存在页面渲染完还没获取到数据，此时涉及到页面渲染的数据不要放在异步调用中获取，避免出现错误

   

# 后台完善



## 统计分析模块

### 需求

> 以一个数据为例，其他都是类似的，把统计出来的注册人数，使用图表的形式进行展示【柱状图、折线图、饼状图等】

### 生成统计数据

> 创建表存储统计数据，字段包括统计日期、注册人数、登录人数、每日视频播放时、没惹你新增课程数、创建日期、更新时间
>
> 如：查某天有多注册人数，对应差当前生成了多少条记录，注意创建时间中带时分秒，如何忽略时分秒，可以使用like，但是不建议这么做，like一般用在昵称，名字等，日期不建议用like；mysql中有一个函数Date，可以把带时分秒的日期转换成不带时分秒的日期部分【确认一下用了函数是否不能使用索引】
>
> 实现的数据库基础就是把用户表查询出的注册人数存储到统计分析数据表中，手写sql，多个参数要给方法中的参数用@Param注解起名，在mapper.xml中使用该名字
>
> 当天的人数还可以统计到redis中，一天结束时设置定时任务进行处理，把数据放入缓存,有三个标准:  1.数据量不大  2.访问频率高  3.数据更改频率低
>
> 图表显示统计数据：用图表插件进行显示
>
> 流程：统计数据模块调用user模块中的接口统计注册人数，添加到统计表中，统计模块去调用服务的原因一般是统计的数据库和用户服务的数据库不是同一个数据库，统计数据库一般也不能直接访问用户数据库，所以一般通过服务调用去获取数据来生成统计数据
>
> 每次生成当天统计数据或者生成某天统计数据先查是否有对应数据，有就删除后添加，没有就直接添加，可以优化，非当天数据直接查，因为有定时任务查，查不到再生成



1. 定时任务

   > 纳入Spring容器管理，@Scheduled(cron = "0/5 * * * * ?")表示定时任务方法，corn规定定时规则，看着和linux的有点像，这个corn表达式可以在线生成Scheduled(cron = "0/5 * * * * ?")
   >
   > 流程：
   >
   > + 在定时任务启动类上添加@EnableScheduling注解
   > + 创建定时任务类，类交给Spring容器管理，用@Scheduled注解标注定时方法，cron属性为对应corn表达式【也称七子表达式、七域表达式【年、周、月、日、小时、分、秒】】，表达式可以借助网站http://cron.qqe2.com/  生成，SpringBoot的七子表达式默认只支持六位，年默认是当前年，写上第七位运行会报错
   
   ```java
   @Component
   public class ScheduledTask {
       @Autowired
       private EduStatisticsService eduStatisticsService;
   
       /**
        * @描述 测试定时任务用 每天七点到二十三点每五秒执行一次
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/10/13
        * @since 1.0.0
        */
       @Scheduled(cron = "0/5 * * * * ?")
       public void task1() {
           System.out.println("*********++++++++++++*****执行了");
       }
   
       /**
        * @描述 每天凌晨1点执行定时任务，得到昨天的满足数据库日期部分格式的昨天日期
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/10/13
        * @since 1.0.0
        */
       @Scheduled(cron = "0 0 1 * * ?")
       public void task2() {
           //获取上一天的日期
           String day = DateUtil.formatDate(DateUtil.addDays(new Date(), -1));
           eduStatisticsService.generateDataByDate(day);
       }
   }
   ```
   
   【日期工具类】
   
   ```java
   package com.atlisheng.edustatistics.utils;
   
   import java.text.SimpleDateFormat;
   import java.util.Calendar;
   import java.util.Date;
   
   /**
    * @author Earl
    * @version 1.0.0
    * @描述 日期操作工具类
    * @创建日期 2023/10/13
    * @since 1.0.0
    */
   public class DateUtil {
   
       private static final String dateFormat = "yyyy-MM-dd";
   
       /**
        * @param date
        * @return {@link String }
        * @描述 格式化日期
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/10/13
        * @since 1.0.0
        */
       public static String formatDate(Date date) {
           SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
           return sdf.format(date);
   
       }
   
       /**
        * @param date   处理的日期，非null
        * @param amount 要加的天数，可能为负数
        * @return {@link Date }
        * @描述 在日期date上增加amount天 。
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/10/13
        * @since 1.0.0
        */
       public static Date addDays(Date date, int amount) {
           Calendar now =Calendar.getInstance();
           now.setTime(date);
           now.set(Calendar.DATE,now.get(Calendar.DATE)+amount);
           return now.getTime();
       }
   
       public static void main(String[] args) {
           System.out.println(DateUtil.formatDate(new Date()));
           System.out.println(DateUtil.formatDate(DateUtil.addDays(new Date(), -1)));
       }
   }
   ```
   
   



### 图表数据统计【echarts】

> 图表显示需要使用的ECharts组件，ECharts是百度的一个项目，后来百度把Echart捐给apache，用于前端图表展示，提供了常规的折线图、 柱状图、 散点图、 饼图、 K线图，用于统计的盒形图，用于地理数据可视化的地图、 热力图、 线图，用于关系数据可视化的关系图、 treemap、 旭日图，多维数据可视化的平行坐标，还有用于 BI 的漏斗图， 仪表盘，并且支持图与图之间的混搭。
> 官方网站： https://echarts.baidu.com/  ，官网有文档，介绍用法；实例介绍图标类型，点击具体的图可以直接获得图的代码，k线图都有
>
> Echarts本身是一个js文件，需要对该文件进行引入，可以在官网或者github下载，也可以通过npm install echarts --save下载--->下载后通过script标签对js文件进行引入-->在body中为Echarts准备一个具备高宽的DOM容器【div】，通过echarts.init方法初始化一个echarts实例并通过setOption(option)方法生成图表，图表在option中进行定义，通过后端返回的数据赋值给data就能实现数据在途中的展示，
>
> 因为data数据是一个json数组格式，后端响应数据给前端也要响应list格式的数据，被自动转换成json数组，注意可以在map中放入list集合，解析成json对象中的json数组
>
> vue中在response中对数据赋值，在response同一个方法的接下来的方法中使用了该赋值，该方法需要放在response的括号中，否则取不到该数据，
>
> Echarts很牛逼，x轴不够长会自动隐藏部分x坐标值，但是会保留数据点

【前端页面】

```html
<template>
    <div class="app-container">
        <!--表单-->
        <el-form :inline="true" class="demo-form-inline">
            <el-form-item>
                <el-select v-model="queryInfo.type" clearable placeholder="请选择">
                    <el-option label="学员登录数统计" value="login_num"/>
                    <el-option label="学员注册数统计" value="register_num"/>
                    <el-option label="课程播放数统计" value="video_view_num"/>
                    <el-option label="每日课程数统计" value="course_num"/>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-date-picker v-model="queryInfo.begin" type="date" placeholder="选择开始日期" value-format="yyyy-MM-dd" />
            </el-form-item>
            <el-form-item>
                <el-date-picker v-model="queryInfo.end" type="date" placeholder="选择截止日期" value-format="yyyy-MM-dd" />
            </el-form-item>
            <el-button :disabled="btnDisabled" type="primary" icon="el-icon-search" @click="showChart()">查询</el-button>
        </el-form>
        <div class="chart-container">
            <div id="chart" class="chart" style="height:500px;width:100%" />
        </div>
    </div>
</template>
<script>
import echarts from 'echarts'
import statistics from '@/api/edu/statistics'
export default {
    data() {
        return {
            //查询图标数据接口请求参数封装对象
            queryInfo: {
                type: '',
                begin: '',
                end: ''
            },
            btnDisabled: false,
            chart: null,
            title: '',//图表名称
            xData: [],
            yData: []
        }
    },
    methods: {
        showChart() {
            this.initChartData()
        },
        // 准备图表数据
        initChartData() {
            statistics.dataDisplayByChart(this.queryInfo).then(response => {
                // 数据
                this.yData = response.data.dataList
                // 横轴时间
                this.xData = response.data.dateList
                // 当前统计类别
                switch (this.queryInfo.type) {
                    case 'register_num':
                        this.title = '学员注册数统计'
                        break
                    case 'login_num':
                        this.title = '学员登录数统计'
                        break
                    case 'video_view_num':
                        this.title = '课程播放数统计'
                        break
                    case 'course_num':
                        this.title = '每日课程数统计'
                        break
                }
                this.setChart()
            })
        },
        // 设置图标参数
        setChart() {
            // 基于准备好的dom，初始化echarts实例
            this.chart = echarts.init(document.getElementById('chart'))
            // console.log(this.chart)
            // 指定图表的配置项和数据
            var option = {
                // x轴是类目轴（离散数据） ,必须通过data设置类目数据
                xAxis: {
                    type: 'category',
                    data: this.xData
                },
                // y轴是数据轴（连续数据）
                yAxis: {
                    type: 'value'
                },
                // 系列列表。每个系列通过 type 决定自己的图表类型
                series: [{
                    // 系列中的数据内容数组
                    data: this.yData,
                    // 折线图
                    type: 'line'
                }],
                //标题显示
                title: {
                    text: this.title
                },
                //x坐标轴触发提示
                tooltip: {
                    trigger: 'axis'
                },
                //区域缩放
                dataZoom: [{
                        show: true,
                        height: 30,
                        xAxisIndex: [0],
                        bottom: 30,
                        start: 10,
                        end: 80,
                        handleIcon: 'path://M306.1,413c0,2.2-1.8,4-4,4h-59.8c-2.2,0-4-1.8-4-4V200.8c0-2.2,1.8-4,4-4h59.8c2.2,0,4,1.8,4,4V413z',
                        handleSize: '110%',
                        handleStyle: {
                            color: '#d3dee5'
                        },
                        textStyle: {
                            color: '#fff'
                        },
                        borderColor: '#90979c'
                    },
                    {
                        type: 'inside',
                        show: true,
                        height: 15,
                        start: 1,
                        end: 35
                }]
            }
            this.chart.setOption(option)
        }
    }
}
</script>
```

【后端接口--只写service】

```java
/**
 * @param begin begin是开始日期【不带时分秒】
 * @param end end是结束日期【不带时分秒】
 * @param type type是前端需要的数据类型
 * @return {@link Map }<{@link String }, {@link Object }>
 * @描述 查询统计图表的数据，根据前端请求数据的种类，将数据和日期分别封装成两个list放入Map传给前端
 * @author Earl
 * @version 1.0.0
 * @创建日期 2023/10/14
 * @since 1.0.0
 */
public Map<String, Object> queryChartData(String begin, String end, String type) {
    QueryWrapper<EduStatistics> dayQueryWrapper = new QueryWrapper<>();
    //选择要查询的字段
    dayQueryWrapper.select(type, "date_calculated");
    dayQueryWrapper.between("date_calculated", begin, end);
    List<EduStatistics> eduStatisticsList = baseMapper.selectList(dayQueryWrapper);
    Map<String, Object> map = new HashMap<>();
    List<Integer> dataList = new ArrayList<Integer>();
    List<String> dateList = new ArrayList<String>();
    map.put("dataList", dataList);
    map.put("dateList", dateList);
    //返回list会自动封装成数组形式，即json的数组形式，json有两种形式，对象到json对象，list集合会变成json数组形式
    for (int i = 0; i < eduStatisticsList.size(); i++) {
        EduStatistics eduStatistics = eduStatisticsList.get(i);
        dateList.add(eduStatistics.getDateCalculated());//把日期放入dateList
        switch (type) {
            case "register_num":
                dataList.add(eduStatistics.getRegisterNum());
                break;
            case "login_num":
                dataList.add(eduStatistics.getLoginNum());
                break;
            case "video_view_num":
                dataList.add(eduStatistics.getVideoViewNum());
                break;
            case "course_num":
                dataList.add(eduStatistics.getCourseNum());
                break;
            case "sell_num":
                dataList.add(eduStatistics.getSellNum());
                break;
            default:
                break;
        }
    }
    return map;
}
```





## canal数据同步工具

1. 应用场景

   + 分库：一般建数据库，统计数据专门存在统计数据库中，用户信息专门存放在用户数据库中，

     + 早期是通过对应的服务去调用相应的数据库，缺点是服务间耦合度高，远程调用效率比较低

     + 目前可以采取实时同步数据库表的方式进行实现，把会员表同步到统计数据库中，就可以实现本地统计，效率更高、耦合更低，canal就是实现数据库同步的工具【由阿里纯java开发，基于数据库增量日志解析，提供增量数据订阅&消费，目前主要支持MySQL  】【把远程库中的内容同步到本地库中】

   + 实现过程，把用户数据库中的表创建一份相同表结构的数据到统计数据库中，用户数据库变化，统计数据库中的表会相应的同步变化，然后直接在本地做数据统计工作

2. 准备工作

   + 准备Linux系统安装MySQL数据库，创建数据库和数据库表；

   + 准备windows系统MySQL，创建数据库和数据库表【名称和表结构要和linux中的相同】；

   + 在linux中使用cannal数据同步工具

     + 第一步：修改linux中即远程Mysql数据库的配置【mysql高级中的读写分离集群配置】

       + canal基于mysql的binlog技术，要开启mysql的binlog写入功能

         + 开启mysql，使用命令`show variables like 'log_bin';`检查binlog功能是否开启【OFF表示未开启，on表示已开启】

         + 如果没有开启，按以下配置开启binlog功能

           ```mysql
           1，修改 mysql 的配置文件 my.cnf
           vi /etc/my.cnf
           追加内容：
           log-bin=mysql-bin #binlog文件名
           binlog_format=ROW #选择row模式，表示一行一行进行同步
           server_id=1 #mysql实例id,不能和canal的slaveId重复
           2，重启 mysql：[有时服务名是mysqld，有时是mysql]
           service mysqld restart
           3，登录 mysql 客户端，查看 log_bin 变量
           mysql> show variables like 'log_bin';
           +---------------+-------+
           | Variable_name | Value |
           +---------------+-------+
           | log_bin | ON|
           +---------------+-------+
           1 row in set (0.00 sec)
           ————————————————
           如果显示状态为ON表示该功能已开启
           ```

       + 在mysql中添加一下相关用户和权限

         > mysql本身有一个user表，第一个用户是root、localhost；表示该用户只能通过本地访问，远程访问不到；添加一条用户，把localhost改成%表示可以远程访问，用户改成canal，即开放canal的远程访问权限；用以下语句在linux中添加一下相关用户和权限
         >
         > 连接完用数据库工具远程连接一下linux数据库

         ```mysql
         CREATE USER 'canal'@'%' IDENTIFIED BY 'canal';#这个密码canal要修改匹配安全策略，否则会报错
         GRANT SHOW VIEW, SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO
         'canal'@'%';
         FLUSH PRIVILEGES;
         ```

     + 第二步：下载安装linux的Canal服务

       + github下载地址：https://github.com/alibaba/canal/releases  ，本项目用的1.1.4.tar.gz

       + 把压缩文件上传到linux中，可以直接上传到/usr/local/canal目录，也可以上传到opt/canal目录下使用`cp canal.deployer-1.1.4.tar.gz /usr/local/canal`将压缩包拷贝到/usr/local/canal目录下

       + `tar zxvf canal.deployer-1.1.4.tar.gz  `解压压缩文件，解压就能用，可以直接考虑在usr/local/canal目录下解压压缩包

       + `vi conf/example/instance.properties  `修改canal配置文件instance.properties

         > 修改成自己的数据库信息

         ```yml
         #需要改成自己的数据库信息,linux数据库的ip和端口号，本机可以写127.0.0.1
         canal.instance.master.address=192.168.44.132:3306
         #需要改成自己的数据库用户名与密码
         canal.instance.dbUsername=canal
         canal.instance.dbPassword=canal
         #需要改成同步的数据库表的规则，例如只是同步一下表，比如指定哪个表进行匹配，使用perl正则表达式进行正则匹配
         #canal.instance.filter.regex=.*\\..*
         canal.instance.filter.regex=guli_ucenter.ucenter_member
         #多个正则之间以逗号(,)分隔，转义符需要双斜杠(\\)
         #常见例子：
         #1. 匹配所有数据库的所有表： .* or .*\\..*
         #2. 匹配canal数据库下所有表： canal\\..*
         #3. canal数据库下的以canal打头的表： canal\\.canal.*
         #4. canal数据库下的一张表【具体库具体表】： canal.test1
         #5. 多个规则组合使用： canal\\..*,mysql.test1,mysql.test2 (逗号分隔)
         #注意：此过滤条件只针对row模式的数据有效(ps. mixed/statement因为不解析sql，所以无法准确提取tableName进行过滤)
         ```

       + 进入bin目录下`sh bin/startup.sh`启动【canal的bin目录下有启动脚本startup.sh】

         + 关闭有`stop.sh`脚本
         + `grep canal`查看canal的进程

     + 第三步：打开对应的防火墙端口【开放安全组】
     + 第四步：安装jdk

     

     

3. 项目整合canal

   + 创建新模块，在模块中引入canal相关的依赖

     ```xml
     <dependencies>
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-web</artifactId>
         </dependency>
         <!--mysql-->
         <dependency>
             <groupId>mysql</groupId>
             <artifactId>mysql-connector-java</artifactId>
         </dependency>
         <!--一个操作数据库的工具-->
         <dependency>
             <groupId>commons-dbutils</groupId>
             <artifactId>commons-dbutils</artifactId>
         </dependency>
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-jdbc</artifactId>
         </dependency>
         <!--canal的依赖，canal的客户端工具-->
         <dependency>
             <groupId>com.alibaba.otter</groupId>
             <artifactId>canal.client</artifactId>
         </dependency>
     </dependencies>
     ```

   + 在数据库中引入对数据源的相关配置，设置服务名和端口号，以及开发环境

     ```properties
     # 服务端口,这是整个模块对应的服务器端口，不写这个会默认使用tomcat的8080端口
     server.port=10000
     # 服务名
     spring.application.name=canal-client
     # 环境设置： dev、 test、 prod,用来配置mybatis-plus的sql执行性能的
     spring.profiles.active=dev
     # mysql数据库连接
     spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
     spring.datasource.url=jdbc:mysql://localhost:3306/ol_education?serverTimezone=GMT%2B8
     spring.datasource.username=root
     spring.datasource.password=Haworthia0715
     ```

   + 创建canal工具类

     > 真拉，还要自己写工具类

     ```java
     package com.atlisheng.canal.client;
     import com.alibaba.otter.canal.client.CanalConnector;
     import com.alibaba.otter.canal.client.CanalConnectors;
     import com.alibaba.otter.canal.protocol.CanalEntry.*;
     import com.alibaba.otter.canal.protocol.Message;
     import com.atlisheng.canal.constants.ConstantsProperties;
     import com.google.protobuf.InvalidProtocolBufferException;
     import org.apache.commons.dbutils.DbUtils;
     import org.apache.commons.dbutils.QueryRunner;
     import org.springframework.stereotype.Component;
     import javax.annotation.Resource;
     import javax.sql.DataSource;
     import java.net.InetSocketAddress;
     import java.sql.Connection;
     import java.sql.SQLException;
     import java.util.Iterator;
     import java.util.List;
     import java.util.Queue;
     import java.util.concurrent.ConcurrentLinkedQueue;
     
     /**
      * @author Earl
      * @version 1.0.0
      * @描述 canal客户端类
      * @创建日期 2023/10/15
      * @since 1.0.0
      */
     @Component
     public class CanalClient {
         //sql队列
         private Queue<String> SQL_QUEUE = new ConcurrentLinkedQueue<>();
         //数据源
         @Resource
         private DataSource dataSource;
     
         /**
          * @描述 canal入库方法
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/15
          * @since 1.0.0
          */
         public void run() {
             CanalConnector connector = CanalConnectors.newSingleConnector(
                     new InetSocketAddress("192.168.200.132", 11111),
                     "example", "", "");
             //这里似乎不能用常量来设置参数，会导致实例无法创建服务启动不起来，经过测试确实只要有一个自动配置的常量都启动不起来，创建不了实例
             // 注意从这里访问canal不需要用户名和密码，似乎是直接访问的那边的Canal
             /*CanalConnector connector = CanalConnectors.newSingleConnector(new
                     InetSocketAddress("192.168.200.132",
                     11111), "example", "", "");*/
             int batchSize = 1000;
             try {
                 connector.connect();
                 connector.subscribe(".*\\..*");
                 connector.rollback();
                 try {
                     while (true) {
                         //尝试从master那边拉去数据batchSize条记录，有多少取多少
                         Message message = connector.getWithoutAck(batchSize);
                         long batchId = message.getId();
                         //1. size的值为0，线程睡1s，否则size大于0表示数据库变化了，执行dataHandle方法
                         int size = message.getEntries().size();
                         if (batchId == -1 || size == 0) {
                             Thread.sleep(1000);
                         } else {
                             //2. 在dataHandle中判断数据库更改类型，根据远程数据的变化获取对应数据拼接sql语句存入sql队列中
                             dataHandle(message.getEntries());
                         }
     
                         connector.ack(batchId);
                         //3. 当队列里面堆积的sql大于一定数值的时候就模拟执行，在executeQueueSql方法中用数据源把语句进行执行，一直在执行，一秒一次
                         // 没有变化，就休眠。有变化就拼接sql放入队列执行？一次更新多条是放在一起拼接吗，对是多条，message.getEntries()
                         //是一个list集合，遍历完才会跳出判断类型拼接sql，加入队列的操作
                         if (SQL_QUEUE.size() >= 1) {
                             executeQueueSql();
                         }
                     }
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 } catch (InvalidProtocolBufferException e) {
                     e.printStackTrace();
                 }
             } finally {
                 connector.disconnect();
             }
         }
     
         /**
          * @描述 模拟执行队列里面的sql语句
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/15
          * @since 1.0.0
          */
         public void executeQueueSql() {
             int size = SQL_QUEUE.size();
             for (int i = 0; i < size; i++) {
                 String sql = SQL_QUEUE.poll();
                 System.out.println("[sql]----> " + sql);
                 this.execute(sql.toString());
             }
         }
     
         /**
          * @param entrys
          * @描述 数据处理
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/15
          * @since 1.0.0
          */
         private void dataHandle(List<Entry> entrys) throws InvalidProtocolBufferException {
             for (Entry entry : entrys) {
                 if (EntryType.ROWDATA == entry.getEntryType()) {
                     RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                     EventType eventType = rowChange.getEventType();
                     //如果是XX操作执行对应的saveXXSql(entry)方法
                     if (eventType == EventType.DELETE) {
                         saveDeleteSql(entry);
                     } else if (eventType == EventType.UPDATE) {
                         saveUpdateSql(entry);
                     } else if (eventType == EventType.INSERT) {
                         saveInsertSql(entry);
                     }
                 }
             }
         }
     
         /**
          * @param entry
          * @描述 保存更新语句
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/15
          * @since 1.0.0
          */
         private void saveUpdateSql(Entry entry) {
             try {
                 RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                 List<RowData> rowDatasList = rowChange.getRowDatasList();
                 for (RowData rowData : rowDatasList) {
                     List<Column> newColumnList = rowData.getAfterColumnsList();
                     StringBuffer sql = new StringBuffer("update " +
                             entry.getHeader().getTableName() + " set ");
                     for (int i = 0; i < newColumnList.size(); i++) {
                         sql.append(" " + newColumnList.get(i).getName()
                                 + " = '" + newColumnList.get(i).getValue() + "'");
                         if (i != newColumnList.size() - 1) {
                             sql.append(",");
                         }
                     }
                     sql.append(" where ");
                     List<Column> oldColumnList = rowData.getBeforeColumnsList();
                     for (Column column : oldColumnList) {
                         if (column.getIsKey()) {
                             //暂时只支持单一主键
                             sql.append(column.getName() + "=" + column.getValue());
                             break;
                         }
                     }
                     SQL_QUEUE.add(sql.toString());
                 }
             } catch (InvalidProtocolBufferException e) {
                 e.printStackTrace();
             }
         }
     
         /**
          * @param entry
          * @描述 保存删除语句
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/15
          * @since 1.0.0
          */
         private void saveDeleteSql(Entry entry) {
             try {
                 RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                 List<RowData> rowDatasList = rowChange.getRowDatasList();
                 for (RowData rowData : rowDatasList) {
                     List<Column> columnList = rowData.getBeforeColumnsList();
                     StringBuffer sql = new StringBuffer("delete from " +
                             entry.getHeader().getTableName() + " where ");
                     for (Column column : columnList) {
                         if (column.getIsKey()) {
                             //暂时只支持单一主键
                             sql.append(column.getName() + "=" + column.getValue());
                             break;
                         }
                     }
                     SQL_QUEUE.add(sql.toString());
                 }
             } catch (InvalidProtocolBufferException e) {
                 e.printStackTrace();
             }
         }
     
         /**
          * @param entry
          * @描述 保存插入语句，获取到远程库中改了那个表或者字段，加了什么值，将这些信息提取出来拼接成sql语句，然后将语句放在队列
          * SQL_QUEUE中
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/15
          * @since 1.0.0
          */
         private void saveInsertSql(Entry entry) {
             try {
                 RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                 List<RowData> rowDatasList = rowChange.getRowDatasList();
                 for (RowData rowData : rowDatasList) {
                     List<Column> columnList = rowData.getAfterColumnsList();
                     StringBuffer sql = new StringBuffer("insert into " +
                             entry.getHeader().getTableName() + " (");
                     for (int i = 0; i < columnList.size(); i++) {
                         sql.append(columnList.get(i).getName());
                         if (i != columnList.size() - 1) {
                             sql.append(",");
                         }
                     }
                     sql.append(") VALUES (");
                     for (int i = 0; i < columnList.size(); i++) {
                         sql.append("'" + columnList.get(i).getValue() + "'");
                         if (i != columnList.size() - 1) {
                             sql.append(",");
                         }
                     }
                     sql.append(")");
                     SQL_QUEUE.add(sql.toString());
                 }
             } catch (InvalidProtocolBufferException e) {
                 e.printStackTrace();
             }
         }
     
         /**
          * @param sql
          * @描述 入库
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/15
          * @since 1.0.0
          */
         public void execute(String sql) {
             Connection con = null;
             try {
                 if (null == sql) return;
                 con = dataSource.getConnection();
                 QueryRunner qr = new QueryRunner();
                 int row = qr.execute(con, sql);
                 System.out.println("update: " + row);
             } catch (SQLException e) {
                 e.printStackTrace();
             } finally {
                 DbUtils.closeQuietly(con);
             }
         }
     }
     ```

   + 创建启动类

     ```java
     @SpringBootApplication
     public class CanalApplication implements CommandLineRunner {
         @Resource
         private CanalClient canalClient;
         
         public static void main(String[] args) {
         	SpringApplication.run(CanalApplication.class, args);
         }
     }
     ```

   + 注意事项

     + 这里的canal貌似是利用ip和端口远程访问远程库上面的canal，而且不需要用户名和密码，且固定端口号是11111，那为什么看不到linux系统上canal对应的端口号，
     + 操作的本地库是在本地服务上进行规定的，必须确保数据库名和表名与远程库的一致性【经过测试，数据库不同名也可以，只要表名和表结构相同即可】







## GateWay网关

> SpringCloud组件，可以实现nginx的相关功能
>
> nginx的作用就是网关的作用，请求先到网关，又网关根据请求统一分配服务器地址【请求转发功能，还可以做负载均衡，权限控制，提供统一的路由方式、基于Filter链提供网关基本的安全、埋点、监控、限流等功能】【nginx又专门的端口】
>
> gateway功能更强大，可以做权限控制【限制访问ip、控制器跨域也可以放在网关中实现】
>
> gatway操作一般要继承nacos进行操作，要通过服务注册中心用网关访问系统中的服务
>
> 早期网关用的是网飞的Zuul，后来被功能更强的gateway替代了
>
> 客户端直接请求微服务的缺点：
>
> + 客户端多次请求服务，增加客户端的复杂性
>
> + 跨域问题再一定场景下很复杂
>
> + 每个服务都要独立认证
>
> + 重构复杂【如微服务的拆分和合并】
>
> + 微服务可能使用了防火墙或者对浏览器不友好的协议，直接访问会有困难
>
>   这些问题都可以使用API网关解决，API 网关是介于客户端和服务器端之间的中间层，所有的外部请
>   求都会先经过 API 网关这一层，安全、性能、监控可以交由 API 网关来做  
>
> 网关能辅助企业管理大规模的API，以降低管理成本和安全风险，包括协议适配、协议转发、安全策略、防刷、流量、监控日志等功能。一般来说网关对外暴露的URL或者接口信息，我们统称为路由信息。
>
> 网关的核心是Filter以及Filter Chain（Filter责任链）

1. + 用户通过客户端进行访问-->进入网关首先来到GateWay Handler Mapping【即匹配路径和服务的地址的映射器】-->然后到GateWay Web Handler【处理器将请求分配指定的过滤器链然后发送到指定的服务执行业务逻辑】-->然后到GateWay的一系列过滤器【做权限、跨域等处理】-->真正的服务器
   + 涉及概念
     + 路由：路由是网关最基础的部分，路由信息有一个ID、一个目的URL、一组断言和一组Filter组成。如果断言路由为真，则说明请求的URL和配置匹配
     + 断言： Java8中的断言函数。 Spring Cloud Gateway中的断言函数输入类型是Spring5.0框架中的ServerWebExchange。 Spring Cloud Gateway中的断言函数允许开发者去定义匹配来自于httprequest中的任何信息，比如请求头和参数等。 【简单来说就是路径的端口的匹配规则】 
     + 过滤器：Spring cloud gateway中的filter分为两种类型的Filter，分别是Gateway Filter和Global Filter。过滤器Filter将会对请求和响应进行修改处理  

2. 网关整合流程

   + 引入相关依赖

     ```xml
     <!--API网关的相关依赖-->
     <dependencies>
         <!--公共工具：包括md5加密、服务返回数据的封装类、JWT令牌、响应的数据统一格式-->
         <dependency>
             <groupId>com.atlisheng</groupId>
             <artifactId>common_utils</artifactId>
             <version>0.0.1-SNAPSHOT</version>
         </dependency>
         <!--用于服务注册中心的nacos-->
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
         </dependency>
         <!--API网关Gateway-->
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-gateway</artifactId>
         </dependency>
         <!--gson:HttpClient等将json数据格式的字符串转换成Map集合的工具-->
         <dependency>
             <groupId>com.google.code.gson</groupId>
             <artifactId>gson</artifactId>
         </dependency>
         <!--服务间相互调用的Fegin-->
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-openfeign</artifactId>
         </dependency>
     </dependencies>
     ```

   + 创建application.properties

     > 设置完成edu服务可以直接通过网关的端口号8222进行访问

     ```properties
     # 服务端口
     server.port=8222
     # 服务名
     spring.application.name=service-gateway
     # nacos服务地址
     spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
     #使用服务发现路由，开启服务发现，让gateway能使用feign找到各个服务，默认是false
     spring.cloud.gateway.discovery.locator.enabled=true
     #服务路由名小写
     #spring.cloud.gateway.discovery.locator.lower-case-service-id=true
     #0表示第一个路由匹配，每个路由匹配都需要设置id【id可以随便写，但是一般建议还是写服务的名字】,url【固定写法：lb://是路由规则，后面跟的必须是nacos中注册的服务名字】,和predicates【断言，这个就是路径匹配规则】，路径匹配上了就去到对应的服务，path的匹配规则不考虑uri部分，即控制器方法第一个//之间字符串之前的都不考虑，即/eduservice/**不能写成*/eduservice/**,两个**表示匹配任意层目录
     #设置路由id
     spring.cloud.gateway.routes[0].id=service-acl
     #设置路由的uri
     spring.cloud.gateway.routes[0].uri=lb://service-acl
     #设置路由断言,代理servicerId为auth-service的/auth/路径
     spring.cloud.gateway.routes[0].predicates= Path=/*/acl/**
     #配置service-edu服务
     spring.cloud.gateway.routes[1].id=service-edu
     spring.cloud.gateway.routes[1].uri=lb://service-edu
     spring.cloud.gateway.routes[1].predicates= Path=/eduservice/**
     #配置service-ucenter服务
     spring.cloud.gateway.routes[2].id=service-ucenter
     spring.cloud.gateway.routes[2].uri=lb://service-ucenter
     spring.cloud.gateway.routes[2].predicates= Path=/ucenterservice/**
     #配置service-ucenter服务
     spring.cloud.gateway.routes[3].id=service-cms
     spring.cloud.gateway.routes[3].uri=lb://service-cms
     spring.cloud.gateway.routes[3].predicates= Path=/cmsservice/**
     spring.cloud.gateway.routes[4].id=service-msm
     spring.cloud.gateway.routes[4].uri=lb://service-msm
     spring.cloud.gateway.routes[4].predicates= Path=/edumsm/**
     spring.cloud.gateway.routes[5].id=service-order
     spring.cloud.gateway.routes[5].uri=lb://service-order
     spring.cloud.gateway.routes[5].predicates= Path=/orderservice/**
     spring.cloud.gateway.routes[6].id=service-order
     spring.cloud.gateway.routes[6].uri=lb://service-order
     spring.cloud.gateway.routes[6].predicates= Path=/orderservice/**
     spring.cloud.gateway.routes[7].id=service-oss
     spring.cloud.gateway.routes[7].uri=lb://service-oss
     spring.cloud.gateway.routes[7].predicates= Path=/eduoss/**
     spring.cloud.gateway.routes[8].id=service-statistic
     spring.cloud.gateway.routes[8].uri=lb://service-statistic
     spring.cloud.gateway.routes[8].predicates= Path=/staservice/**
     spring.cloud.gateway.routes[9].id=service-vod
     spring.cloud.gateway.routes[9].uri=lb://service-vod
     spring.cloud.gateway.routes[9].predicates= Path=/eduvod/**
     spring.cloud.gateway.routes[10].id=service-edu
     spring.cloud.gateway.routes[10].uri=lb://service-edu
     spring.cloud.gateway.routes[10].predicates= Path=/eduuser/**
     ```

     【yaml】

     ```yml
     server:
       port: 8222
     spring:
       application:
         cloud:
           gateway:
             discovery:
               locator:
                 enabled: true
             routes:
             - id: SERVICE-ACL
               uri: lb://SERVICE-ACL
               predicates:
               - Path=/*/acl/** # 路径匹配
             - id: SERVICE-EDU
               uri: lb://SERVICE-EDU
               predicates:
               - Path=/eduservice/** # 路径匹配
             - id: SERVICE-UCENTER
               uri: lb://SERVICE-UCENTER
               predicates:
               - Path=/ucenter/** # 路径匹配
     nacos:
       discovery:
         server-addr: 127.0.0.1:8848
     ```

     

   + 创建启动类

     > 需要在服务器中进行注册，要加上@EnableDiscoveryClient注解实现服务注册

     ```java
     @SpringBootApplication
     @EnableDiscoveryClient
     public class ApiGatewayApplication {
         public static void main(String[] args) {
         	SpringApplication.run(ApiGatewayApplication.class, args);
         }
     }
     ```

3. GateWay实现负载均衡

   + GateWay自动封装了负载均衡功能，比如两台服务器部署了edu服务，客户端发送请求到网关，网关通过请求路径和nacos注册中心发现了edu服务集群，会自动将请求平均分配到集群的服务器上，如果服务器性能不一样呢？
   + 负载均衡策略【dubbo会讲这个东西】
     + 轮询：第一个第二个第一个第二个...
     + 权重：哪个服务器的权重高先访问
     + 最少请求时间：谁的响应时间最短就先去访问哪个服务器

4. api网关的各种配置和工具类

   > 包名可以随便起，但是类名必须保持一致

   + config.CorsConfig

     > 解决跨域问题，注意网关和控制器中的跨域二者只能出现一个，否则会出问题，可以理解为第一次网关跨域成功，第二次跨域由跨回去了，前端的端口也必须改成Gateway的端口，不要再使用nginx的端口
     >
     > 前端后台改dev.env.js；前台改request.js；把端口号改成gateway的端口号

     ```java
     /**
      * @author Earl
      * @version 1.0.0
      * @描述 description:
      * @创建日期 2023/10/16
      * @since 1.0.0
      */
     @Configuration
     public class CorsConfig {
         /**
          * @return {@link CorsWebFilter }
          * @描述 CorsWebFilter可以统一解决跨域问题，有了这个所有的控制器方法上就不用写@CrossOrigin注解了
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/10/16
          * @since 1.0.0
          */
         @Bean
         public CorsWebFilter corsFilter() {
             CorsConfiguration config = new CorsConfiguration();
             config.addAllowedMethod("*");
             config.addAllowedOrigin("*");
             config.addAllowedHeader("*");
     
             UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
             source.registerCorsConfiguration("/**", config);
     
             return new CorsWebFilter(source);
         }
     }
     ```

   + filter.AuthGlobalFilter

     > 限定哪些请求可以访问，哪些请求不能访问，请求失败会输出什么结果

     ```java
     package com.atlisheng.gateway.filter;
     
     import com.google.gson.JsonObject;
     import org.springframework.cloud.gateway.filter.GatewayFilterChain;
     import org.springframework.cloud.gateway.filter.GlobalFilter;
     import org.springframework.core.Ordered;
     import org.springframework.core.io.buffer.DataBuffer;
     import org.springframework.http.server.reactive.ServerHttpRequest;
     import org.springframework.http.server.reactive.ServerHttpResponse;
     import org.springframework.stereotype.Component;
     import org.springframework.util.AntPathMatcher;
     import org.springframework.web.server.ServerWebExchange;
     import reactor.core.publisher.Mono;
     
     import java.nio.charset.StandardCharsets;
     import java.util.List;
     
     /**
      * @author Earl
      * @version 1.0.0
      * @描述 <p>
      * 全局Filter，统一处理会员登录与外部不允许访问的服务
      * 限定哪些请求可以访问，哪些请求不能访问，请求失败会输出什么结果
      * </p>
      * @创建日期 2023/10/16
      * @since 1.0.0
      */
     @Component
     public class AuthGlobalFilter implements GlobalFilter, Ordered {
     
         private AntPathMatcher antPathMatcher = new AntPathMatcher();
     
         @Override
         public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
             ServerHttpRequest request = exchange.getRequest();
             String path = request.getURI().getPath();
             //访问改地址，用户必须登录
             if(antPathMatcher.match("/api/**/auth/**", path)) {
                 List<String> tokenList = request.getHeaders().get("token");
                 if(null == tokenList) {
                     ServerHttpResponse response = exchange.getResponse();
                     return out(response);
                 } else {
     //                Boolean isCheck = JwtUtils.checkToken(tokenList.get(0));
     //                if(!isCheck) {
                         ServerHttpResponse response = exchange.getResponse();
                         return out(response);
     //                }
                 }
             }
             //内部服务接口，不允许外部访问
             if(antPathMatcher.match("/**/inner/**", path)) {
                 ServerHttpResponse response = exchange.getResponse();
                 return out(response);
             }
             return chain.filter(exchange);
         }
     
         @Override
         public int getOrder() {
             return 0;
         }
     
         private Mono<Void> out(ServerHttpResponse response) {
             JsonObject message = new JsonObject();
             message.addProperty("success", false);
             message.addProperty("code", 28004);
             message.addProperty("data", "鉴权失败");
             byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
             DataBuffer buffer = response.bufferFactory().wrap(bits);
             //response.setStatusCode(HttpStatus.UNAUTHORIZED);
             //指定编码，否则在浏览器中会中文乱码
             response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
             return response.writeWith(Mono.just(buffer));
         }
     }
     
     ```

   + handler.ErrorHandlerConfig

     > 覆盖默认的异常处理

     ```java
     package com.atlisheng.gateway.handler;
     
     import org.springframework.beans.factory.ObjectProvider;
     import org.springframework.boot.autoconfigure.web.ResourceProperties;
     import org.springframework.boot.autoconfigure.web.ServerProperties;
     import org.springframework.boot.context.properties.EnableConfigurationProperties;
     import org.springframework.boot.web.reactive.error.ErrorAttributes;
     import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
     import org.springframework.context.ApplicationContext;
     import org.springframework.context.annotation.Bean;
     import org.springframework.context.annotation.Configuration;
     import org.springframework.core.Ordered;
     import org.springframework.core.annotation.Order;
     import org.springframework.http.codec.ServerCodecConfigurer;
     import org.springframework.web.reactive.result.view.ViewResolver;
     
     import java.util.Collections;
     import java.util.List;
     
     /**
      * @author Earl
      * @version 1.0.0
      * @描述 覆盖默认的异常处理
      * @创建日期 2023/10/16
      * @since 1.0.0
      */
     @Configuration
     @EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
     public class ErrorHandlerConfig {
     
         private final ServerProperties serverProperties;
     
         private final ApplicationContext applicationContext;
     
         private final ResourceProperties resourceProperties;
     
         private final List<ViewResolver> viewResolvers;
     
         private final ServerCodecConfigurer serverCodecConfigurer;
     
         public ErrorHandlerConfig(ServerProperties serverProperties,
                                          ResourceProperties resourceProperties,
                                          ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                             ServerCodecConfigurer serverCodecConfigurer,
                                          ApplicationContext applicationContext) {
             this.serverProperties = serverProperties;
             this.applicationContext = applicationContext;
             this.resourceProperties = resourceProperties;
             this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
             this.serverCodecConfigurer = serverCodecConfigurer;
         }
     
         @Bean
         @Order(Ordered.HIGHEST_PRECEDENCE)
         public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
             JsonExceptionHandler exceptionHandler = new JsonExceptionHandler(
                     errorAttributes,
                     this.resourceProperties,
                     this.serverProperties.getError(),
                     this.applicationContext);
             exceptionHandler.setViewResolvers(this.viewResolvers);
             exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
             exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
             return exceptionHandler;
         }
     }
     
     ```

   + handler.JsonExceptionHandler

     ```java
     package com.atlisheng.gateway.handler;
     
     import org.springframework.boot.autoconfigure.web.ErrorProperties;
     import org.springframework.boot.autoconfigure.web.ResourceProperties;
     import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
     import org.springframework.boot.web.reactive.error.ErrorAttributes;
     import org.springframework.context.ApplicationContext;
     import org.springframework.http.HttpStatus;
     import org.springframework.web.reactive.function.server.*;
     
     import java.util.HashMap;
     import java.util.Map;
     
     /**
      * @author Earl
      * @version 1.0.0
      * @描述 自定义异常处理
      *
      * <p>异常时用JSON代替HTML异常信息<p>
      * @创建日期 2023/10/16
      * @since 1.0.0
      */
     public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {
     
         public JsonExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                     ErrorProperties errorProperties, ApplicationContext applicationContext) {
             super(errorAttributes, resourceProperties, errorProperties, applicationContext);
         }
     
         /**
          * 获取异常属性
          */
         @Override
         protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
             Map<String, Object> map = new HashMap<>();
             map.put("success", false);
             map.put("code", 20005);
             map.put("message", "网关失败");
             map.put("data", null);
             return map;
         }
     
         /**
          * 指定响应处理方法为JSON处理的方法
          * @param errorAttributes
          */
         @Override
         protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
             return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
         }
     
         /**
          * 根据code获取对应的HttpStatus
          * @param errorAttributes
          */
         @Override
         protected int getHttpStatus(Map<String, Object> errorAttributes) {
             return 200;
         }
     }
     ```

     



## 权限管理模块

### 需求

> 管理员只能管理部分模块，如只做讲师管理，或者只做课程管理等等；整体是为用户分配角色，通过角色去操作有操作权限的菜单；为角色分配菜单，为用户分配角色
>
> 用户管理：
>
> + 用户增删改查
> + 用户的角色分配
>
> 角色管理：
>
> + 角色的增删改查
>
> + 角色权限管理
>
>   + 系统管理员：可以操作所有菜单
>
>   + 讲师管理员：只能操作讲师
>
> 菜单管理：
>
> + 菜单列表：路由存入数据库，从数据库中读取并做显示，把数据封装成路由的数据格式
> + 菜单添加、修改功能
> + 菜单删除功能：删除菜单的同时删除子菜单，递归删除

1. 权限管理表的关系

   > acl开头的表，至少五张表才能把功能做完善

   + acl_permission：菜单表，树形结构存储菜单信息
   + acl_role：角色表，用户管理员，测试
   + acl_user：用户表，
   + acl_role_permission：角色和菜单的关系表
   + acl_user_role：用户和角色的关系表

   > 菜单表和角色表是多对多的关系：一个菜单可以供多个角色访问，一个角色也可以访问多个菜单
   >
   > 角色表和用户表也是多对多的关系：一个角色可以由多个用户担任，一个用户也可以担任多个角色
   >
   > + 一对多的处理方法是在多的表建一个外键，和1形成对照关系
   > + 多对赌的处理方法是在多对多的表之间创建一个中间表，在中间表建立两个表的多对多关系
   >   + 就是将两个表对应有关系的记录的id存入中间表的同一条记录，比如marry既可以担任讲师管理员又可以担任课程管理员，就将marry和讲师管理员单独生成1条记录，marry和课程管理员又单独生成一条疾苦



### 权限管理典型接口

1. 查询所有菜单

   > dfs查询所有菜单，封装成无限层树形结构，注意在swagger中禁用了admin请求路径，必须注释掉或者修改路径才能使用swagger访问acl下的接口

   ```java
   /**
    * @return {@link ResponseData }
    * @描述 查询出所有的菜单，封装成Permission的List集合返回，woc这个Permission对应数据库的children字段竟然还是一个
    * List集合？而且还是Permission的List集合，这和数据库插入值不会冲突吗？查表知没有children这个字段，这个children属性
    * 单纯就是给前端用的
    * 这种形式可以封装成无限级树形结构，使用递归来实现查询所有菜单的功能：一级菜单下面查二级菜单、二级菜单下面查三级菜单，
    * 直到查不到下级菜单为止
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/16
    * @since 1.0.0
    *///获取全部菜单
   @ApiOperation(value = "查询所有菜单")
   @GetMapping
   public ResponseData indexAllPermission() {
       List<Permission> list =  permissionService.queryAllMenu();
       return ResponseData.responseCall().data("children",list);
   }
   
   /**
    * @return {@link List }<{@link Permission }>
    * @描述 获取全部菜单，先查询菜单中的所有数据，注意这里面写了两套方法，第二套是课程演示，实际运行也是第二套，这里笔记做在第一套上面
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/16
    * @since 1.0.0
    */
   @Override
   public List<Permission> queryAllMenu() {
   
       //1.查出所有的菜单
       QueryWrapper<Permission> wrapper = new QueryWrapper<>();
       wrapper.orderByDesc("id");
       List<Permission> permissionList = baseMapper.selectList(wrapper);
   
       //2.将查出的菜单封装成list集合返回给前端
       List<Permission> result = bulid(permissionList);
   
       return result;
   }
   
   /**
    * @param treeNodes
    * @return {@link List }<{@link Permission }>
    * @描述 使用递归方法封装菜单数据成树形结构，先从一级查询
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/16
    * @since 1.0.0
    */
   private static List<Permission> bulid(List<Permission> treeNodes) {
       List<Permission> trees = new ArrayList<>();
       //对每条菜单记录进行遍历，当遇到一级目录时，设置对应一级菜单的层级为1，查出对应一级菜单的全部子菜单封装成list集合存入一级菜单的children
       // 属性并将一级菜单加入封装数据的list集合
       for (Permission treeNode : treeNodes) {
           if ("0".equals(treeNode.getPid())) {
               treeNode.setLevel(1);
               //将子菜单的所有数据全部查出来放入一级菜单的children属性，将一级菜单加入list集合
               trees.add(findChildren(treeNode,treeNodes));
           }
       }
       return trees;
   }
   
   /**
    * @param treeNode 顶层一级菜单对象
    * @param treeNodes 所有菜单记录的list集合
    * @return {@link Permission }
    * @描述 递归查找一级菜单的所有子菜单，并封装成list集合传递给上级菜单的children属性
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/16
    * @since 1.0.0
    */
   private static Permission findChildren(Permission treeNode,List<Permission> treeNodes) {
       //由于是循环调用，这一步实际是在各层菜单的children属性创建一个新的list集合，准备接收子菜单数据
       treeNode.setChildren(new ArrayList<Permission>());
   
       for (Permission it : treeNodes) {
           //这个效率有点感人，每次遍历所有菜单，将pid和上层菜单的id进行比较，值相同则查出该菜单的下的所有子菜单，
           // 封装到其children属性，并将该菜单设置到上层菜单的children属性的list集合中，实际上是深度优先遍历
           if(treeNode.getId().equals(it.getPid())) {
               //定义菜单的层级
               int level = treeNode.getLevel() + 1;
               //设置当前菜单层级
               it.setLevel(level);
               //这特么会执行？逗我呢，上面不是执行了嘛，这里最后测试一下会不会进来,经过测试，压根就不会执行这行代码
               if (treeNode.getChildren() == null) {
                   treeNode.setChildren(new ArrayList<>());
               }
               //在这里面再次对所有菜单进行了遍历
               treeNode.getChildren().add(findChildren(it,treeNodes));
           }
       }
       return treeNode;
   }
   ```

2. 递归删除菜单

   ```java
   /**
    * @param id
    * @return {@link ResponseData }
    * @描述 根据菜单id递归删除菜单和子菜单
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/16
    * @since 1.0.0
    */
   @ApiOperation(value = "递归删除菜单")
   @DeleteMapping("remove/{id}")
   public ResponseData remove(@PathVariable String id) {
       permissionService.removeChildById(id);
       return ResponseData.responseCall();
   }
   /**
    * @param id
    * @描述 递归删除菜单
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/17
    * @since 1.0.0
    */
   @Override
   public void removeChildById(String id) {
   
       //将id传入list集合，用于封装所有删除菜单的id值
       List<String> idList = new ArrayList<>();
       //根据id递归查询出当前菜单所有的子菜单id并存入List集合
       this.selectChildListById(id, idList);
   
       //将当前菜单的id加入list集合
       idList.add(id);
       //根据id批量删除集合中id对应的记录
       baseMapper.deleteBatchIds(idList);
   }
   /**
    *	根据当前菜单的id，递归获取子菜单的id，将子菜单id封装到list集合中，相当于将所有pid的记录查出来，然后遍历所有的子菜单记录，将
    *其id放入list集合，并继续对这些记录查询子菜单记录，将id放入同一个list集合，并遍历每个菜单下的子菜单，直到获取不到子菜单向上返回，有点
    * 像深度优先遍历和宽度优先遍历的结合版
    * @param id
    * @param idList
    */
   private void selectChildListById(String id, List<String> idList) {
       List<Permission> childList = baseMapper.selectList(new QueryWrapper<Permission>().eq("pid", id).select("id"));
       childList.stream().forEach(item -> {
           idList.add(item.getId());
           this.selectChildListById(item.getId(), idList);
       });
   }
   ```

3. 添加角色权限

   ```java
   /**
    * @param roleId 角色id
    * @param permissionId 菜单id，角色单次选择只能一个角色，菜单可以选择多个，swagger的数组形式的数据通过每个新元素起新行传递
    * @return {@link ResponseData }
    * @描述
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/16
    * @since 1.0.0
    */
   @ApiOperation(value = "给角色分配权限")
   @PostMapping("/doAssign")
   public ResponseData doAssign(String roleId,String[] permissionId) {
       permissionService.saveRolePermissionRelationShip(roleId,permissionId);
       return ResponseData.responseCall();
   }
    /**
    * @param roleId
    * @param permissionIds
    * @描述 给角色分配权限
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/17
    * @since 1.0.0
    */
   @Override
   public void saveRolePermissionRelationShip(String roleId, String[] permissionIds) {
   
       rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id", roleId));
       //1.对应role_permission表，根据输入的角色id和菜单id集合，生成多条角色菜单关系表，加入list集合，最后批次添加记录
       List<RolePermission> rolePermissionList = new ArrayList<>();
       //遍历菜单id集合，因为角色和菜单关系表，需要填写的只有角色id和菜单id，所以根本用不着查表，直接设置对应的id即可
       for(String permissionId : permissionIds) {
           if(StringUtils.isEmpty(permissionId)) continue;
   
           RolePermission rolePermission = new RolePermission();
           rolePermission.setRoleId(roleId);
           rolePermission.setPermissionId(permissionId);
           rolePermissionList.add(rolePermission);
       }
       //批次添加记录
       rolePermissionService.saveBatch(rolePermissionList);
   }
   
   ```

4. 根据角色id查询出所有菜单并将角色对应菜单的isSelected属性设置为true

   ```java
   /**
    * @param roleId
    * @return {@link ResponseData }
    * @描述 根据角色id查询出对应的菜单id
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/16
    * @since 1.0.0
    */
   @ApiOperation(value = "根据角色获取菜单")
   @GetMapping("toAssign/{roleId}")
   public ResponseData toAssign(@PathVariable String roleId) {
       List<Permission> list = permissionService.selectAllMenu(roleId);
       return ResponseData.responseCall().data("children", list);
   }
   /**
    * @param roleId
    * @return {@link List }<{@link Permission }>
    * @描述 根据角色id查询出所有的菜单，将和角色有关系的菜单的isSelect属性设置为true
    * @author Earl
    * @version 1.0.0
    * @创建日期 2023/10/17
    * @since 1.0.0
    */
   @Override
   public List<Permission> selectAllMenu(String roleId) {
       List<Permission> allPermissionList = baseMapper.selectList(new QueryWrapper<Permission>().orderByAsc("CAST(id AS SIGNED)"));
   
       //根据角色id获取角色权限
       List<RolePermission> rolePermissionList = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("role_id",roleId));
   
       for (int i = 0; i < allPermissionList.size(); i++) {
           //根据每个菜单对象对每个查询出的角色菜单遍历，如果菜单的id和角色菜单记录的菜单id相等，将菜单的select属性设置为true
           Permission permission = allPermissionList.get(i);
           for (int m = 0; m < rolePermissionList.size(); m++) {
               RolePermission rolePermission = rolePermissionList.get(m);
               if(rolePermission.getPermissionId().equals(permission.getId())) {
                   //lombok的isSelect属性，@Data生成的是setSelect方法，会去掉is
                   //这个属性是应该是用来给前端展示菜单用的？
                   permission.setSelect(true);
               }
           }
       }
   
       List<Permission> permissionList = bulid(allPermissionList);
       return permissionList;
   }
   
   ```



### 权限管理系统整合SpringSecurity

> Spring Security 基于 Spring 框架，提供了一套Web 应用安全性的解决方案。一般来说， Web 应用的安全性包括用户认证（Authentication）【用户登录时输入账号密码查询数据库验证登录】和用户授权（Authorization）【根据用户角色授权用户的操作权限】 两个部分  
>
> SpringSecurity本质上就是过滤器，对请求的路径进行过滤
>
> + 如果项目基于Session，那么Spring-security会对cookie里的sessionid进行解析，找到服务器存储的sesion信息，然后判断当前用户是否符合请求的要求。【SpringSecurity封装了从session中取数据的过程，如session.getAttribute()】
> + 如果项目基于token，则是解析出token，然后获取用户信息和用户权限信息将当前请求加入到Spring-security管理的权限信息中去  

1. 实现思路
   + 如果系统的模块众多，每个模块都需要就行授权与认证，
     + 所以选择基于token的形式进行授权与认证，用户根据用户名密码认证成功，然后获取当前用户角色的一系列权限值，
     + 用户登录后，以用户名为key，权限列表为value的形式存入redis缓存中，根据用户名相关信息生成token返回，浏览器将token记录到cookie中，
     + 每次调用api接口都默认将token携带到header请求头中， Spring-security解析header头获取token信息，解析token获取当前用户名，根据用户名就可以从redis中获取权限列表，这样Spring-security就能够判断当前请求是否有权限访问  
   + 流程
     + 用户登录，输入用户名和密码进行验证同时查询登录用户权限列表
     + 将用户名作为key，用户权限列表作为value存入redis
     + 生成jwttoken，将token传递到前端，放入cookie，cookie在发起请求时通过请求拦截器放入请求头
     + 每次发起请求，SpringSecurity都是获取token，解析token获取用户名，根据用户名从redis中获取用户权限，SpringSecurity判断当前请求是否有访问权限，即访问权限是SpringSecurity进行判断的



### 整合权限管理模块到edu-acl服务

1. 整合流程

   + common下创建SpringSecurity模块

   + 引入相关依赖

     ```xml
     <dependencies>
         <!--有用到其中的工具，比如MD5加密，数据响应等-->
         <dependency>
             <groupId>com.atlisheng</groupId>
             <artifactId>common_utils</artifactId>
             <version>0.0.1-SNAPSHOT</version>
         </dependency>
         <!-- Spring Security权限框架依赖 -->
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-security</artifactId>
         </dependency>
         <!--jwt相关依赖-->
         <dependency>
             <groupId>io.jsonwebtoken</groupId>
             <artifactId>jjwt</artifactId>
         </dependency>
     </dependencies>
     ```

2. 代码结构

   > 代码结构基本是固定的，直接照猫画虎整合，以后在学习SpringSecurity的相关内容
   >
   > 很多工作都被SpringSecurity封装了，用户需要决定token如何加密，redis中存储什么信息，打印什么信息；细节得看SpringSecurity的课程，这里讲的很浅显

   + 搭建SpringSecurity模块

     + 一个核心配置类

     + 两个用户授权和认证的实体类

     + 两个授权和认证过滤器

     + 四个工具类：包括密码处理器【密码加密】、退出处理器、token操作工具类、未授权统一处理类【没有权限返回什么值】

   + 在权限管理中整合SpringSecurity模块

     + 在service_acl中引入SpringSecurity模块的依赖

     + 在service_acl模块中创建UserDetailsService的实现类【这个类的作用是查询登录和用户权限的类，实现了在security模块中SpringSecurity的UserDetailsService接口】

       ```java
       package com.atlisheng.eduacl.service.impl;
       
       import com.atlisheng.eduacl.entity.User;
       import com.atlisheng.eduacl.service.PermissionService;
       import com.atlisheng.eduacl.service.UserService;
       import com.atlisheng.security.entity.SecurityUser;
       import org.springframework.beans.BeanUtils;
       import org.springframework.beans.factory.annotation.Autowired;
       import org.springframework.security.core.userdetails.UserDetails;
       import org.springframework.security.core.userdetails.UserDetailsService;
       import org.springframework.security.core.userdetails.UsernameNotFoundException;
       import org.springframework.stereotype.Service;
       
       import java.util.List;
       
       /**
        * <p>
        * 自定义userDetailsService - 认证用户详情
        * 自定义查查询用户类
        * </p>
        *
        * @author qy
        * @since 2019-11-08
        */
       @Service("userDetailsService")
       public class UserDetailsServiceImpl implements UserDetailsService {
       
           @Autowired
           private UserService userService;
       
           @Autowired
           private PermissionService permissionService;
       
           /***
            * 根据账号获取用户信息
            * @param username:
            * @return: org.springframework.security.core.userdetails.UserDetails
            */
           @Override
           public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
               // 从数据库中取出用户信息
               User user = userService.selectByUsername(username);
       
               // 判断用户是否存在
               if (null == user){
                   //throw new UsernameNotFoundException("用户名不存在！");
               }
               // 返回UserDetails实现类
               com.atlisheng.security.entity.User curUser = new com.atlisheng.security.entity.User();
               BeanUtils.copyProperties(user,curUser);
       
               List<String> authorities = permissionService.selectPermissionValueByUserId(user.getId());
               SecurityUser securityUser = new SecurityUser(curUser);
               securityUser.setPermissionValueList(authorities);
               return securityUser;
           }
       
       }
       ```

       

### 整合前端权限管理部分

> 系统包括SpringSecurity的代码中也设置了admin是超级管理员，拥有所有权限，在数据库角色表中也做了设定，项目初始时用admin进行登录，加用户，加角色，加权限

1. 在后台管理系统重的node_modules中把element-ui删掉，再复制过去

   > 使用课上提供的element-ui依赖会导致菜单管理列表出问题，这里使用以下代码更换element-ui版本，换完以后确实好了
   >
   > 执行下面的代码，更换其版本。先卸载：npm uninstall element-ui -S  再安装 npm install element-ui -S

2. 替换后台管理系统代码

   > 拷贝18天中的前端代码，拷贝的都是没写过的页面，问题不大

   + 替换api中的login.js并拷贝acl文件【login.js中的api改成了security下的文件】，之前的login是edu中做的临时登录
   + 路由改成查数据库并显示的新路由写法
   + store目录加了一些js文件
   + utils
   + main.js和permission.js文件需要替换
   + views中添加一些页面

3. `npm install --save vuex-persistedstate  `安装vuex-persistedstate，该依赖是为了后台系统做登录持久化存储数据使用

4. 修改文件两个地方

   + router的index.js的页面跳转地址改成自己项目中的地址【path和import属性】
   + 修改数据库菜单表路径和页面地址【从讲师管理开始更改菜单相关的信息以符合前端页面，path不需要加@/views，已经被封装好了】

5. 确认后台系统的地址是网关的地址

6. 后台系统登录后运行流程

   + 首先进入到SpringSecurity的TokenLoginFilter【登录过滤器】的attemptAuthentication方法，得到用户输入的用户名和密码，然后执行acl模块下的UserDetailsServiceImpl中的loadUsername方法，用用户名查询用户信息和用户权限，封装用户权限到SecurityUser类并返回用户权限，查询到用户信息后会进入DefaultPasswordEncoder的matches方法比对密码， 密码比对成功后进入下面的successfulAuthentication方法，然后执行登录成功的方法，在该方法中根据用户名生成token字符串，然后把用户名和用户权限放入redis中，通过responseData的data属性返回token；往后的操作都是SpringSecurity做到的，从redis中根据用户名获取用户的权限，然后给用户赋值权限做到的，登录一次后，每次请求都走上诉过程并到TokenAuthentixationFilter【授权过滤器中】类中的getAuthentication方法获取请求头中的token，获取用户名，从redis中获取用户权限并赋值给用户

     【用户权限数据】

     ```json
     "[\"java.util.ArrayList\",[\"user.list\",\"user.add\",\"user.update\",\"user.remove\",\"role.update\",\"role.list\",\"role.add\",\"role.remove\",\"role.acl\",\"permission.list\",\"permission.add\",\"permission.update\",\"permission.remove\",\"teacher.add\",\"teacher.list\",\"teacher.update\",\"teacher.remove\",\"subject.list\",\"subject.import\",\"course.publish\",\"course.update\",\"chapter.update\",\"daily.list\",\"daily.add\",\"banner.list\",\"banner.add\",\"banner.update\",\"banner.remove\",\"order.list\",\"user.assgin\"]]"
     127.0.0.1:6173> 
     ```

   + 没有对应后台权限的用户的后台系统是没有相应路由菜单的

   + 有很严重的bug

     > bug1，对角色修改权限时会导致角色已经有的权限再次在数据库中被添加，导致使用该角色登录时无法查看菜单栏【我确实出现了修改看数据有没有回显的情况，也确实出现了菜单无法显示的情况，还有登录后会直接访问退出登录的页面，导致用户没有相应权限前端页面会直接无法响应，其实就是被SpringSecurity拦截了】









# Nacos分布式配置中心

> 服务器集群部署，如果一个服务有多态服务器，配置文件发生变更，所有相同服务的配置文件都需要进行修改，很麻烦
>
> SpringCloud提供配置中新SpringCloudConfig，但是不好用，后来替换成了nacos

1. 配置中心SpringCloudConfig

   + Spring Cloud Config 为分布式系统的外部配置提供了服务端和客户端的支持方案。在配置的服务端您可以在所有环境中为应用程序管理外部属性的中心位置。客户端和服务端概念上的Spring Environment和 PropertySource 抽象保持同步, 它们非常适合Spring应用程序，但是可以与任何语言中运行的应用程序一起使用。  

     + 通俗的讲，一个服务集群式部署，当涉及到的数据库地址发生变化，所有的配置文件都需要改，还可能涉及改错忘了改的问题

     + 整个专门作为配置中心的服务，专门存放服务的配置文件，让各个服务都去读取配置中心的对应文件，修改配置文件只需要改配置中心中的文件【nacos也可以有集群】

     + Spring Cloud Config 包含了Client和Server两个部分， server提供配置文件的存储、以接口的形式将配置文件的内容提供出去， client通过接口获取数据、并依据此数据初始化自己的应用。 Springcloud使用git或svn【也是一个版本控制工具】存放配置文件，默认情况下使用git。  

     + Nacos 可以与 Spring, Spring Boot, Spring Cloud 集成，并能代替 Spring Cloud Eureka, SpringCloud Config。 通过 Nacos Server 和 spring-cloud-starter-alibaba-nacos-config 实现配置的动态变更。  

       + 实现该功能需要以下几项依赖

         ```xml
         <!--nacos服务注册中心-->
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
         </dependency>
         <!--openfeign服务调用-->
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-openfeign</artifactId>
         </dependency>
         <!--做分布式配置中心的nacos-confign组件-->
         
         ```

       + nacos配置中心的功能

         + 调整系统运行时更改配置文件是有效手段。如果微服务架构中没有使用统一配置中心时，所存在的问题：
           \- 配置文件分散在各个项目里，不方便维护
           \- 配置内容安全与权限
           \- 更新配置后，项目需要重启
         + nacos配置中心：系统配置的集中管理（编辑、存储、分发）、动态更新不重启、回滚配置（变更管理、
           历史版本管理、变更审计）等所有与配置相关的活动。  

2. nacos配置中心配置流程

   + SpringBoot配置文件加载顺序

     + 优先加载bootstrap.properties文件，该文件配置的数据一般是系统级别的配置，这些参数一般是不会变动的。 

       > bootstrap.properties 用于应用程序上下文的引导阶段。bootstrap.properties 由父Spring ApplicationContext加载。父ApplicationContext 被加载到使用 application.properties 的之前 

     + 然后加载application.properties，用来定义应用级别的。  

   + nacos添加配置

     + Data ID的命名格式：`${prefix}-${spring.profiles.active}.${file-extension}  `
       + ${prefix}就是bootstrap文件中配置的服务名
       + ${spring.profile.active}是bootstrap文件中配置的spring.profiles.active  ，可以为dev、prod、test
         + 当spring.profiles.active属性没有配置时`-${spring.profile.active}`省略不写
         + 注意当bootstrap加了spring.profiles.active=dev，原来的配置文件就不能用了，会自动取nacos上匹配完整命名格式的文件即`${prefix}-dev.${file-extension}`
       + ${file-extension}是配置内容的数据格式，nacos支持properties 和 yaml 类型。

     + 配置流程

       + 点击配置列表，点击添加，根据命名规范命好名，把原配置文件拷贝进来，把原来配置文件改成bootstrap.properties，

         > 配置列表添加好了名字就不能改了

         【bootstrap配置】

         ```properties
         #配置中心地址
         spring.cloud.nacos.config.server-addr=127.0.0.1:8848
         
         #当前环境对应的 profile,放开下一行注释，SpringBoot去nacos中找的配置文件的名字会变成完整名字的配置文件，原来的配置文件无法再继续使用
         spring.profiles.active=dev
         
         # 该配置影响统一配置中心中的dataId
         spring.application.name=service-statistics
         ```

   + 在项目中读取nacos配置中心的文件

     + 注释掉原来的Application.properties配置，但是可以有该文件，创建bootstrap.properties，在该文件中配置配置中心地址【ip：端口】，服务名字【该配置会统一影响配置中心的dataId，spring.profile.active=dev可以不写】

     + 在调用服务中引入nacos-config依赖

       ```xml
       <dependencies>
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
           </dependency>
       </dependencies>
       ```

       > 高版本SpringBoot还需要额外引入bootstrap
       >
       > 可以通过查看端口号来判断那个配置文件被采纳了

     

3. Nacos名称空间切换环境

   + 在配置列表上有个public，这就是一个名称空间，可以简单理解为一个包

   + 项目中的几种环境

     > 不同环境的配置可能不同，比如开发就用自己的计算机，测试会上测试环境，有各种的压力测试，测试没问题才会上线运行，涉及到很多环境的配置；生产环境是用户真正使用的环境
     >
     > 这些环境在nacos中用命名空间统一描述，nacos中的默认命名空间为public

     + dev：开发环境
     + test：测试环境
     + prod：生产环境

   + 创建其他三种命名空间

     + 在命名空间新建其他三种命名空间

       > 创建好以后配置列表会显示刚创建的几个命名空间

     + 在命名空间中可以点击加号创建配置文件，也可以使用克隆的方式创建配置文件，选中配置文件点击克隆后弹窗选择要克隆到的命名空间，然后点击开始克隆，克隆不会导致原始文件消失

     + 需要使用自定义命名空间的配置文件需要再bootstrap中配置属性`spring.cloud.nacos.config.namespace=命名空间的nacespace值`【表示根据名称空间做切换】

   

4. Nacos配置中心加载多个配置文件

   > 端口号可以写在一个文件中，数据库配置可以写在一个单独文件中，redis也可以写在一个单独的配置文件中，通过Nacos读取多个配置文件中的内容

   + 新添port.properties和redis.properties到dev命名空间

   + 在bootstrap中对上述两个配置文件进行追加，其他不要动，配置文件的值一变化，后端就会有反应，如改了端口，不需要重新启动

     ```properties
     #额外配置文件的名称
     spring.cloud.nacos.config.ext-config[0].data-id=port.properties
     # 开启动态刷新配置，否则配置文件修改，工程无法感知
     spring.cloud.nacos.config.ext-config[0].refresh=true
     spring.cloud.nacos.config.ext-config[1].data-id=jdbc.properties
     spring.cloud.nacos.config.ext-config[1].refresh=true
     ```

     

     



# 项目提交到Git

> 码云：公有仓库不限制人数，私有仓库免费限制五人

1. 创建一个项目仓库
2. 管理中仓库名字可以改，管理中成员管理可以添加项目管理人员
3. 本地代码提交到git
   + 电脑上必须安装git，idea上必须配置git【即把path设置成git.exe的路径】
   + 第二步：在idea上点击VCS创建本地仓库，选择本地仓库的位置，一般选择当前项目为本地仓库，也可以选择别的地方作为本地仓库，此时项目中的文件都变成红色的
   + 第三步：右键项目选择git进行add，添加代码到本地库，没有创建本地库右键项目是不会有git选项的
   + 第四步：在idea上点击git下的repository下的remote设置远程仓库的地址并点击ok
   + 第五步：本地库内容提交到远程库点击git然后commit【提交信息必须填写，不填会提交失败】
4. git远程库的密码更改以后可以到windows凭据中修改本地存储的远程库的密码
5. 让本地代码和远程库断开连接
   + setting中VC移除项目目录【相当于从本地库移除项目】
   + 找到项目仓库文件夹，删掉.git文件



# 附录

1. mysql5的url不需要带服务器时区，驱动中无需加cj；mysql8的url需要带时区且url中需要带时区，且springBoot需要2.1以上

   + serverTimezone=GMT%2B8作用是设置服务器时区为东八区，用于服务器处理时间相关的操作，如记录日志、生成时间戳等

   ```prop
   spring.datasource.url=jdbc:mysql://localhost:3306/mybatis_study?serverTimezone=GMT%2B8
   spring.datasource.url=jdbc:mysql://localhost:3306/mybatis_study?serverTimezone=Asia/Shanghai
   ```

   

2. mybatis-plus怎么更改默认访问user表为访问t_user表

3. springboot开启mybatis日志

   + 能在控制台显示sql语句和查询结果

   ```pro
   #mybatis日志
   mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
   ```

4. mybatis-plus操作

   + 查询所有

     ```java
     userMapper.selectList(null);
     ```

   + 添加一条记录

     + mp会自动生成19位id主键值，不需要手动设置
     + 主键策略，详细见谷粒学院文档mp简介，包扩
       + 默认主键策略是ID_WORKER、生成全局唯一ID
       + 自增主键【表要设置自增主键，且实体类对应字段需要配置@TableId(type = IdType.AUTO)  注解】
       + UUID生成唯一的随机值，但是排序不方便
       + 用redis生成主键，用redis原子操作incr和incrby实现，每天从0点开始的流水号也可以用“日期+当日自增长号”实现，优点灵活数字天然排序，对分页和需要排序的情况友好，缺点是没有redis需要引入redis，增加系统复杂度和编码量
       + mp自带策略是推特的雪花算法【snowflake：分布式ID生成算法，生成long类型的ID，使用41bit作毫秒数，10bit作机器ID，包括5位表示数据中心，五位表示机器码，12bit作为毫秒内的流水号，每毫秒可以产生4096个ID，1个永远为0的符号位？这不是和19位冲突】
         + 自增主键就是上述情况设置为AUTO、ID_WORKER生成19位值，为数字类型专用；ID_WORKER_STR生成19位值，为字符串类型专用、INPUT表示自定义主键值，通过自己注册自动填充插件进行填充，NONE表示跟随全局，约等于input、UUID表示生成随机唯一值
         + 不设置type值就会使用雪花算法自动生成，雪花算法生成的是64位主键
       + 注意ID_WORKER、UUID、ID_WORKER_STR自由当插入对象的ID为空时才会自动填充

     ```java
     userMapper.insert(user)
     ```

   + 根据id更新数据

     + 需要设置user的id值，只会更新user中和表中相同id数据不同的字段

     ```java
     userMapper.updateById(user);
     ```

   + 自动填充

     + 一般数据表中会有记录创建时间和记录更新时间，正常情况下需要自己设置创建时间和修改时间，比较麻烦，而且创建时间还需要对数据库记录进行逻辑判断，很不方便；借助mp的源对象处理器接口MetaObjectHandler和@TableField注解实现自动填充功能，注解的fill属性为FieldFill.insert表示第一次插入时自动填充；fill属性为FieldFill.insert_update表示第一次插入和修改都自动填充，update表示仅当更新填充字段，default表示默认不进行处理

     + 第一步在需要自动填充的字段上加注解

       ```java
       @TableField(fill= FieldFill.INSERT)
       或者
       @TableField(fill=FieldFill.INSERT_UPDATE)
       ```

     + 第二步编写自定义源对象处理器实现源对象处理器接口

       + 在insertfill方法中通过setFieldValByName方法指定添加记录时对应的填充字段，填充内容
       + 在updatefill方法中通过setFieldValByName方法指定更新记录时对应的填充字段，填充内容

       ```java
       @Component
       public class TimeMetaObjectHandler implements MetaObjectHandler {
           /**
            * @param metaObject 元对象
            * @描述 使用mp实现第一次添加操作时该方法执行
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/26
            * @since 1.0.0
            */
           @Override
           public void insertFill(MetaObject metaObject) {
               setFieldValByName("createTime",new Date(),metaObject);
               setFieldValByName("updateTime",new Date(),metaObject);
           }
       
           /**
            * @param metaObject 元对象
            * @描述 使用mp实现修改操作时该方法执行
            * @author Earl
            * @version 1.0.0
            * @创建日期 2023/08/26
            * @since 1.0.0
            */
           @Override
           public void updateFill(MetaObject metaObject) {
               setFieldValByName("updateTime",new Date(),metaObject);
           }
       }
       ```

   + mp简单查询

     + 根据id进行批量查询
     
       + 多个id组织成list
     
       ```java
       List<User> users = userMapper.selectBatchIds(Arrays.asList(1l, 2l, 3l, 4l));
       ```
     
     + 用Map封装查询条件
     
       + map中存放的是不同的条件，最终效果是返回满足所有条件的交集的所有记录
     
       + 注意map中的key对应的是数据库中的字段名，user_id在实体类中对应userId，但在map中仍然填写字段名user_id
     
         ```java
         List<User> users = userMapper.selectByMap(conditionMap);
         ```
     
     + 使用mp自带分页插件分页查询
     
   + 根据id删除记录

     + 物理删除

       ```java
       //物理删除，直接从数据库删除对应记录
       int count = userMapper.deleteById(1695443169719640066l);
       ```

     + 批量物理删除

       ```java
       int count = userMapper.deleteBatchIds(Arrays.asList(1695441613733539842l, 1695442467597361154l));
       ```

     + mp实现逻辑删除[标记为删除]

       第一步：添加deleted字段：ALTER TABLE 'user' ADD COLUMN 'deleted' boolean

       第二步：配置逻辑删除插件ISqlInjector

       ```java
       /**
        * @return {@link ISqlInjector }
        * @描述 mp逻辑删除插件
        * @author Earl
        * @version 1.0.0
        * @创建日期 2023/08/27
        * @since 1.0.0
        */
       @Bean
       public ISqlInjector iSqlInjector(){
           return new LogicSqlInjector();
       }
       ```

       第三步：实体类的deleted字段上添加@TableLogic注解以及@TableField(fill=FieldFill.INSERT注解【？这需要设置，数据库表为什么不能设置默认值？讲师模块就没有设置】)

       第四步：插入时设置添加自动填充初始值

       第五步：在配置文件中添加逻辑删除deleted的值的含义

       ```properties
       #mp逻辑删除配置，这也是默认配置，设置值和mp默认值一样可以不配置
       mybatis-plus.global-config.db-config.logic-delete-value=1
       mybatis-plus.global-config.db-config.logic-not-delete-value=0
       ```

       注意：逻辑删除会导致物理删除的方法失效，转而执行更新语句将deleted值设置为1，查询操作也会忽略这些deleted被更新为1的字段

   + 条件查询

5. 乐观锁

   + 解决丢失更新的问题，多人同时修改同一条记录，最后提交更新的数据把之前提交更新的数据全部覆盖了，导致之前的数据库操作丢失失效，解决方式可以采用串行的悲观锁，即拿到锁才能读；或者乐观锁方式，更新数据的版本与读取数据的版本一致才能提交事务，更新后数据版本加1

   + mp实现字段乐观锁

     + 在实体类中添加@Version注解并用自动填充功能设置版本的默认值为1

     + 配置乐观锁插件，即向IoC容器中注入OptimisticLockerInterceptor组件

       > 注意乐观锁只支持int、Integer、long、Long、Date、Timestamp、LocalDateTime类型数据，整形下新版本等于旧版本号+1，newVersion会回写到实体类对象的对应version属性中，更新版本号只支持updateById和update（entity,wrapper）两个方法
       >
       > 这种方式必须要先查记录再更改记录对应的版本才会自加1，否则只有更新操作没有查询动作版本号是不会变化的

6. 用指令向表中添加version字段

   ```sql
   ALTER TABLE 'user' ADD COLUMN 'version' INT
   ```

7. list集合遍历的另一种写法

   ```java
   users.forEach(System.out::println);
   ```

8. Mp中的分页插件

   + 在MyBatis-plus配置类中注入PaginationInterceptor【页面拦截器】，作用与pageHelper类似

   ```java
   @Bean
   public PaginationInterceptor paginationInterceptor(){
       return new PaginationInterceptor();
   }
   ```

   + 编写分页代码

     ```java
     @Test
     public void testPage(){
         Page<User> page = new Page<>(1, 3);//创建page对象，其中第一个参数为当前页，3为每页显示记录数
         userMapper.selectPage(page, null);//第二个参数是查询条件，为null表示不限定条件，page为对应的分页对象,查询的数据会被封装到page对象中
         //通过page对象获取分页数据，疑问怎么变成下一页
         System.out.println(page.getCurrent());//获取当前页
         System.out.println(page.getRecords());//获取当前页数据的list集合
         page.setCurrent(page.getCurrent()+1);//可以通过setCurrent设置当前页
         System.out.println(page.getCurrent());
         System.out.println(page.getSize());//获取每页显示记录数
         System.out.println(page.getTotal());//获取总记录数
         System.out.println(page.getPages());//获取总页数
     
         System.out.println(page.hasNext());//当前页有下一页
         System.out.println(page.hasPrevious());//当前页有上一页
     }
     ```

9. mp的性能分析插件PerformanceInterceptor

   > 开发环境使用，线上不推荐，一共有三种环境，dev【开发】、test【测试】、prod【生产】用@Profile({"dev","test"})注解设置dev test环境开启

   + 输出每条SQL和其执行时间，超过指定时间停止SQL的执行，便于发现问题,执行超时会直接抛异常

   + 参数：maxTime：设置SQL执行的是最大时长，单位毫秒

     ​			format：SQL是否格式化，默认为false

   第一步：向IoC容器中注入性能分析插件

   ```java
   @Bean
   @Profile({"dev","test"})//设置dev test环境开启,有了这个插件还必须设置springBoot配置文件设置为dev才能显示执行信息，单有这个不行
   public PerformanceInterceptor performanceInterceptor(){
       PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
       performanceInterceptor.setMaxTime(100);
       performanceInterceptor.setFormat(true);
       return performanceInterceptor;
   }
   ```

   第二步：配置springBoot配置文件

   ```properties
   #springBoot配置mp性能分析插件配置
   spring.profiles.active=dev
   ```

10. wapper条件构造抽象类，顶级父类

   + AbstractWrapper：子类，用于查询条件封装以生成sql的where条件

   + QueryWrapper是AbstractWrapper的子类，一般创建QueryWrapper封装条件

     ```java
     @Test
     public void testDelete(){
         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
         queryWrapper.isNull("name").ge("age",12).isNotNull("email");//isNull:name字段为null的，ge表示大于等于，isNotNull表示不为null的
         int count = userMapper.delete(queryWrapper);
         System.out.println(count);
     }
     ```

     + 对应条件ge【大于等于】、gt【大于】、le【小于等于】、lt【小于】、isNull、isNotNull、eq【等于】、nq【不等于】、between【在什么之间，需要三个参数，字段，下限值和上限值包含边界】、notBetween【不在什么中间】、like【相当于mysql中的like，需要传参字段和like后面的字面值】、orderByDesc【查询结果降序】、orderBy【】、orderByAsc【查询结果升序】、last【传入的字符串会直接拼接到sql的最后】、allEq【传入的map，map中所有条件的并集】、wrapper.select("id","name")会查询所有记录指定的列id和name，返回list集合
     + 代码示例

     ```java
     @Test
     public void testSelectQuery(){
         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
         //ge、gt、le、lt、
         queryWrapper.ge("age",30);//age大于等于30的记录
         //eq、ne
         queryWrapper.eq("age",30);//age等于30的记录
         //between
         queryWrapper.between("age",20,30);
         //like
         queryWrapper.like("name","岳");
         //orderByDesc
         queryWrapper.orderByDesc("id");
         //last
         queryWrapper.last("limit 1");
         //指定要查询的列
         queryWrapper.select("id","name");
         List<User> users = userMapper.selectList(queryWrapper);
         users.forEach(System.out::println);
     }
     ```

     

11. java注释

    + # TODO、FIXME、XXX

      + `//TODO `： 表示待实现的功能
      + `//FIXME`： 代码存在Bug，不能Run或运行结果不正确，需要修复
      + `//XXX  `： 勉强可以工作，但是实现的方法不一定很好

12. jsonView浏览器插件可以很好的看返回浏览器的json数据

13. 403错误一般发生在跨域和路径写错的情况下

14. el-table不要把所有字段的宽度限定死，所有字段都限定死，会导致表格右边出现白框

15. 一定一定要注意，当修改了模块名称后以及包路径后，一定要将.idea中的workspace文件中的包路径和 [service_sms.iml](E:\JavaStudy\project\ol_edu\vpc-ol-edu\service\service_sms\service_sms.iml) 文件中的包路径修改正确，否则后续代码的任何编译都不会更新到当前项目下，还是更新在老包路径下，且直到现在springBoot配置文件仍然不会自动打包，需要手动复制

16. sms：short message service

17. 留意RFC、IETF、Flickr、Dropbox

18. @PathVariable注解無法將前台传入的String类型自动转换成Long类型，只能自动转换成long类型

19. VUE的异步调用

    > 在页面加载完成后调用，且只会被调用一次，这是调用了一次前台讲师列表分页查询

    ```vue
    <script>
    	import teacher from "@/api/teacher"
        //异步调用
        //方法名固定，两个参数params：相当于params=this.$route.params、error:得到当前调用中后端返回的错误信息
        //return也是固定的，
        export default {
            asyncData({ params, error }) {
                //这个return后面不能直接加回车，加了回车会出问题
                return teacher.getPageList(1, 8).then(response => {
                    console.log(response.data.data);
                    //这个也可以用之前的方法获取值，即赋值给data中的变量，也可以直接写成下面这种形式
                    //this.data=response.data.data
                    //第一个data不需要在data(){}中定义，会自动帮忙定义data，还会自动把值赋值到data上，和上面的写法效果是一样的
                    return { data: response.data.data }
                });
    		},
    	};
    </script>
    ```

20. javascript:void(0);取消超链接的跳转行为，转而可以执行点击事件的方法

21. 图片样式中的height和图层高度的height可以设置成一样就能填满图层

22. 设置linux环境变量命令`vim /etc/profile`;shift+g切换至文本最后一行



# 优化

1. 通过excel表格添加一级目录和二级目录过程中需要检查数据是否存在于数据库中，不必每条记录都去查数据库，使用HashMap对查询记录缓存，没有再去查询数据库并对记录进行缓存【查询到不存数据并对查询数据进行缓存，查不到则保存记录并对当前保存记录进行缓存】

2. 在对课程一二级分类封装的过程中，不要对每个一级分类进行数据库查询，而采取一级分类一次查询，二级分类一次查询；用两层for循环嵌套对一二级信息进行封装，但是考虑到时间复杂度比较高，特别是含有三级分类的过程，采用HashMap缓存一级分类课程的id和二级分类列表，根据二级分类的父id一次遍历存入不同的列表，最后统一对HashMap遍历并赋值给一级分类的children属性

3. web提交的数据涉及多个数据库表，创建一个vo类统一封装web提交信息，后续处理成对应数据库表的entity类并进行数据库操作

4. 在课程信息内容编辑到一半时重新点击添加课程，执行初始化方法init()清空数据对象内容，富文本编辑器的内容并没有被清空，在社区查询之后了解到要手动使用代码来对编辑器的内容进行清空

   【社区链接：[javascript - Tinymce content clear mceCleanup - Stack Overflow](https://stackoverflow.com/questions/22066678/tinymce-content-clear-mcecleanup)】

   ```vue
   init() {
       // 初始化分类列表
       this.getAllSubjectList()
       // 获取讲师列表
       this.selectedTeacher()
       if (this.$route.params && this.$route.params.id) {
           const id = this.$route.params.id
           // 根据id获取课程基本信息
           this.echoCourseInfo(id)
       } else {
           this.courseInfo = { ...defaultForm }
           //手动清空富文本编辑器的内容
           tinymce.activeEditor.setContent("");
       }
   },
   ```

5. p标签的样式图层会浮动在span的上面，导致span的按钮无法被点击，也就没有办法触发单击事件，这时候的解决办法是通过样式设置p和span标签的图层位置为相对，将span图层的优先级z-index设置为1，让span图层置于所有图层的最上方,直接注释掉float也是可以的，但是这样会导致页面布局混乱，细节看[关于float属性导致button按钮无法点击问题的解决思路_明天天明~的博客-CSDN博客](https://blog.csdn.net/qq_41950447/article/details/116261878)

6. *//添加新章节或者修改新章节弹出对话框并将确认按钮重置，在处理完就重置会出现连点两次导致章节信息清空的bug*

   ```javascript
   initDialogBeforeAddOrEdit(){
       dialogChapterFormVisible = true
       this.chapterSaveBtnDisabled=false
   },
   ```

7. 章节页面点击编辑后点击取消原文档没有做chapter的清除工作，只是简单的关闭了对话框，给取消绑定对话框处理完成后的收尾方法

   ```javascript
   <el-button @click="handleDialog">取 消</el-button>
   handleDialog(){
       this.dialogChapterFormVisible=false
       this.getChaptersByCourseId()
       //重置章节标题
       this.chapter.title=''
       //充值章节排序
       this.chapter.sort=0
   },
   ```

8. 需要对课程列表进行优化，手动写sql语句解析查询条件多表连接查询并显示课程简介和讲师姓名

9. 课程分类列表没有修改功能

10. 章节管理需要一个一个excel表格批量导入功能

11. 编辑章节信息点击上一步到课程基本信息，此时再次点击添加课程添加新课程仍然会修改编辑章节信息的课程

    > 已经解决，常量的id单独设置为空串

12. 有个很严重的bug，用户上传视频到完成这段时间，保存小节信息的按钮处于可以点击的状态，没有时间点来判断视频开始上传和结束，会导致还没获取到videoId就对小节数据更新，导致视频成功上传但是数据库中找不到视频信息，解决办法可以在视频上传成功后单独执行一次小节数据更新【这个解决办法不行，上传过程直接关闭窗口会直接导致上传成功的后续代码不会执行而报执行异常，暂时找不到好的解决办法】

13. [ESLint](https://so.csdn.net/so/search?q=ESLint&spm=1001.2101.3001.7020)校验Vue项目最后一行有个空行校验，可以加个空行或者直接在.eslintrc.js取消最后空行校验`'eol-last': 0`

14. @ApiParam可能会导致@PathVariable注解获取变量时不能获取变量名，而直接获取变量名，导致id获取不到，可以直接把@ApiParam删掉，也可以尝试把@PathVariable的value属性设置成和@ApiParam的name属性一致

    



问题

1. 有一个小问题，点击同一个路由，界面内容不会清空，这个问题暂时没有提及，讲师列表界面还有这个问题
2. 没有章节信息，仍然可以创建成功还可以在数据库存入数据，也需要优化进行判定，比如没有课程名称或讲师就不能添加，使用js中的事件对部分信息进行限制
3. 留意一下，跳转到新页面时条件判断【即不满足跳转条件的时候怎么办】















