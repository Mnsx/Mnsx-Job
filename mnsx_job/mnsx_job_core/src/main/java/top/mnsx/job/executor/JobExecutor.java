package top.mnsx.job.executor;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import top.mnsx.job.handler.IJobHandler;
import top.mnsx.job.message.AppToAdminRequest;
import top.mnsx.job.message.JobInvokeResponse;
import top.mnsx.job.message.JobResponse;
import top.mnsx.job.properties.JobProperties;
import top.mnsx.job.utils.IdUtil;
import top.mnsx.job.utils.ThrowableUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JobExecutor {
    // 连接控制中心的通道
    @Getter
    @Setter
    private Channel channel;
    @Setter
    private JobProperties jobProperties;
    @Autowired
    private ApplicationContext applicationContext;

    // 存放所有IJobHandler的map集合
    private ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();

    /**
     * 注册JobHandler
     * @param name Handler名称
     * @param jobHandler 执行器
     * @return IJobHandler
     */
    public IJobHandler registerJobHandler(String name, IJobHandler jobHandler) {
        IJobHandler result = jobHandlerRepository.putIfAbsent(name, jobHandler);
        log.info("[Mnsx-Job] 注册handler成功，名称={}，handler={}", name, jobHandler);
        return result;
    }

    /**
     * 获取对应JobHandler
     * @param name handler名称
     * @return IJobHandler
     */
    public IJobHandler loadJobHandler(String name) {
        if (jobHandlerRepository.containsKey(name)) {
            return jobHandlerRepository.get(name);
        } else {
            log.error("[Mnsx-Job] 容器中不存在{}对应handler", name);
            throw new RuntimeException("容器中不存在" + name + "对应handler");
        }
    }

    /**
     * JobExecutor初始化方法
     */
    public void init() {
        log.info("[Mnsx-Job] JobExecutor初始化中...");

        initJobHandler();

        new RegisterAppToAdminThread().start();
    }

    /**
     * JobExecutor销毁方法
     */
    public void destroy() {
        log.info("[Mnsx-Job] JobExecutor销毁中...");
    }

    /**
     * 任务执行方法
     * @param name 执行器名称
     * @param params 执行参数
     * @return JobResponse
     */
    public JobInvokeResponse jobInvoke(String name, String params) {
        IJobHandler jobHandler = jobHandlerRepository.get(name);

        if (Objects.isNull(jobHandler)) {
            return JobInvokeResponse.error("任务不存在!");
        }

        try {
            return jobHandler.execute(params);
        } catch (Exception e) {
            log.error("[Mnsx-Job] 任务={} 调用异常={}", name, ThrowableUtil.getThrowableStackTrace(e));
            return JobInvokeResponse.error("任务调度异常!");
        }
    }

    /**
     * 初始化所有的JobHandler
     */
    private void initJobHandler() {
        String[] beanNames = applicationContext.getBeanNamesForType(IJobHandler.class);
        if (beanNames.length == 0) {
            return;
        }

        Arrays.stream(beanNames).forEach(beanName ->
                registerJobHandler(beanName, (IJobHandler) applicationContext.getBean(beanName)));
    }

    private class RegisterAppToAdminThread extends Thread {

        private RegisterAppToAdminThread() {
            super("Mnsx-AppToAdmin");
        }

        @Override
        public void run() {
            // 等待通道创建成功
            do {
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                    log.info("[Mnsx-Job] 等待Netty客户端创建创建...");
                } catch (InterruptedException e) {
                    log.error("[Mnsx-Job] Netty客户端调用产生异常, exception={}", ThrowableUtil.getThrowableStackTrace(e));
                    throw new RuntimeException(e);
                }
            } while (Objects.isNull(channel));

            log.info("[Mnsx-Job] 开始将应用注册到客户端...");

            // 准备AppToAdminRequest
            AppToAdminRequest appToAdminRequest = new AppToAdminRequest();
            appToAdminRequest.setAppName(jobProperties.getAppName());
            appToAdminRequest.setAppDesc(jobProperties.getAppDesc());
            appToAdminRequest.setAddress(jobProperties.getIp() + ":" + jobProperties.getPort());
            appToAdminRequest.setSequenceId(IdUtil.generateAtomicId());

            // 发送请求AppToAdminRequest
            try {
                // 发送请求
                channel.writeAndFlush(appToAdminRequest);
                log.info("[Mnsx-Job] 应用通过通道={}注册到服务端成功", channel);
            } catch (Throwable t) {
                log.error("[Mnsx-Job] 应用注册到服务端失败, exception={}", ThrowableUtil.getThrowableStackTrace(t));
            }
        }
    }
}
