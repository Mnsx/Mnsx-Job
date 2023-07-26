package top.mnsx.job.config;

import io.netty.util.internal.ObjectUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import top.mnsx.job.annotation.EnableJobAutoConfiguration;
import top.mnsx.job.executor.JobExecutor;
import top.mnsx.job.properties.JobProperties;
import top.mnsx.job.utils.NetUtil;

import java.lang.annotation.Annotation;
import java.util.Objects;

@Slf4j
public class JobAutoConfigurationRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    @Setter
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取EnableJobAutoConfiguration注解参数
        AnnotationAttributes annotationAttributes
                = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
                        EnableJobAutoConfiguration.class.getName()));

        if (Objects.isNull(annotationAttributes)) {
            log.error("[Mnsx-Job] EnableJobAutoConfiguration注解属性为空");
            throw new RuntimeException("EnableJobAutoConfiguration注解属性为空");
        }

        // 获取Job平台配置信息，并注册bean
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JobProperties.class)
                .addPropertyValue("adminIp", annotationAttributes.getString("adminIp"))
                .addPropertyValue("adminPort", annotationAttributes.getString("adminPort"))
                .addPropertyValue("appName", annotationAttributes.getString("appName"))
                .addPropertyValue("appDesc", annotationAttributes.getString("appDesc"))
                .addPropertyValue("ip", NetUtil.getIp())
                .addPropertyValue("port", environment.getProperty(
                        "server.port", String.class, "8080"));
        registry.registerBeanDefinition("jobProperties", beanDefinitionBuilder.getBeanDefinition());

        // 注册JobExecutor
        registerJobExecutor(registry);

        // 2.0通过netty执行方法调用
        // 注册JobInvokeNettyRegistrar
        registerJobInvokeNettyRegistrar(registry);
    }

    private void registerJobInvokeNettyRegistrar(BeanDefinitionRegistry registry) {
        log.info("[Mnsx-Job] JobInvokeNettyRegistrar开始注册...");

        // 创建JobInvokeNettyRegistrar类的BeanDefinition
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(JobInvokeNettyRegistrar.class)
                .setInitMethodName("init") // 初始化方法
                .addPropertyReference("jobExecutor", "jobExecutor") // 参数引用
                .addPropertyReference("jobProperties", "jobProperties") // 配置信息
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO) // 不允许直接注入
                .getBeanDefinition();

        // 注册bean到spring容器
        registry.registerBeanDefinition("JobInvokeNetty", beanDefinition);

        log.info("[Mnsx-Job] JobInvokeNettyRegistrar注册成功");
    }

    /**
     * 注册JobExecutor
     * @param registry 注册中心
     */
    private void registerJobExecutor(BeanDefinitionRegistry registry) {
        log.info("[Mnsx-Job] JobExecutor开始注册...");

        // 创建JobExecutor类BeanDefinition
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(JobExecutor.class)
                .setInitMethodName("init") // 初始化方法
                .setDestroyMethodName("destroy") // 销毁方法
                .addPropertyReference("jobProperties", "jobProperties") // 配置信息
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE) // 通过Type进行AutoWired注入
                .getBeanDefinition();

        // 注册bean到spring容器
        registry.registerBeanDefinition("jobExecutor", beanDefinition);

        log.info("[Mnsx-Job] JobExecutor注册成功");
    }
}
