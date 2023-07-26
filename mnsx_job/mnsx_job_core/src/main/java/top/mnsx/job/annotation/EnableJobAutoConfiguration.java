package top.mnsx.job.annotation;

import org.springframework.context.annotation.Import;
import top.mnsx.job.config.JobAutoConfigurationRegistrar;

import java.lang.annotation.*;

/**
 * 开启远程任务调用自动配置，主要是引入JobAutoConfigurationRegistrar，JobConfiguration
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({JobAutoConfigurationRegistrar.class})
public @interface EnableJobAutoConfiguration {

    // Job控制中心IP
    String adminIp() default "127.0.0.1";

    // Job控制中心端口号
    String adminPort() default "7777";

    // 应用名称
    String appName();

    // 应用介绍
    String appDesc() default "";
}
