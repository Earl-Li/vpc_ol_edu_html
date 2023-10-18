package com.atlisheng.utils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.Test;

public class CodeGenerator {
    @Test
    public void CodeGenerate() {
        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        //String projectPath = System.getProperty("user.dir");//这个是获取用户的当前模块service_edu的绝对路径，但是经常出错，建议在下方使用自己的绝对路径

        //XXX
        //注意找办法把绝对路径搞成相对路径
        gc.setOutputDir("E:\\JavaStudy\\project\\ol_edu\\vpc-ol-edu\\service\\service_statistics" + "/src/main/java");

        gc.setAuthor("Earl");//代码生成后注释的作者
        gc.setOpen(false); //生成后是否打开资源管理器，写false生成代码的目录不打开
        gc.setFileOverride(false); //重新生成时文件是否覆盖，false表示再次调用该方法不会覆盖掉之前的代码，true会把之前的代码全部覆盖掉
        gc.setServiceName("%sService"); //去掉Service接口的首字母I，即原本mp生成的service层代码，默认接口名称第一个字母有I
        gc.setIdType(IdType.ID_WORKER_STR); //主键策略
        gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型,即表中datetime类型会生成实体类中的Date类型
        gc.setSwagger2(true);//开启Swagger2模式

        mpg.setGlobalConfig(gc);

        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/ol_education?serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("Haworthia0715");
        dsc.setDbType(DbType.MYSQL);//数据库类型
        mpg.setDataSource(dsc);

        // 4、包配置
        PackageConfig pc = new PackageConfig();

        //两个结合生成包名"com.atlisheng.edustatistics"
        pc.setParent("com.atlisheng");
        pc.setModuleName("edustatistics"); //模块名

        //大包"com.atlisheng.edustatistics"下的子包com.atlisheng.edustatistics.controller
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        // 5、策略配置,根据表逆向工程建接口
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude("edu_statistics");//有多张表用可变长度参数囊括
        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        strategy.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀，这里要去掉前缀就把pc.getModuleName()改成表的前缀名edu_

        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain =true) setter链式操作，还有@Data注解
        strategy.setRestControllerStyle(true); //restful api风格控制器
        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
        mpg.setStrategy(strategy);

        // 6、执行
        mpg.execute();
    }
}
