package com.atlisheng.canal.constants;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 这种配置在配置文件在CanalClient中使用的方式不能用，会导致项目启动不起来，报空指针异常
 * @创建日期 2023/10/15
 * @since 1.0.0
 */
@Component
public class ConstantsProperties implements InitializingBean {
    @Value("${remote.canal.ip}")
    private String canalIP;
    @Value("${remote.canal.port}")
    private Integer canalPort;
    @Value("${remote.canal.password}")
    private String canalPassword;
    @Value("${remote.canal.username}")
    private String canalUsername;
    @Value("${remote.canal.destination}")
    private String canalDestination;

    public static String CANAL_IP;
    public static Integer CANAL_PORT;
    public static String CANAL_PASSWORD;
    public static String CANAL_USERNAME;
    public static String CANAL_DESTINATION;

    @Override
    public void afterPropertiesSet() throws Exception {
        canalIP=CANAL_IP;
        canalPort=CANAL_PORT;
        canalUsername=CANAL_USERNAME;
        canalPassword=CANAL_PASSWORD;
        canalDestination=CANAL_DESTINATION;
    }
}
