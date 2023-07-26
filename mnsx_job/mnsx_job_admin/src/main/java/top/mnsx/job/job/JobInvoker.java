package top.mnsx.job.job;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import org.springframework.stereotype.Component;
import top.mnsx.job.handler.JobInvokeResponseHandler;
import top.mnsx.job.message.JobInvokeRequest;
import top.mnsx.job.message.JobResponse;
import top.mnsx.job.server.AdminServer;

@Component
public class JobInvoker {
    public JobResponse invoke(String url, String jobHandler, String params) throws InterruptedException {
        Channel channel = AdminServer.getChannelMap().get(url);
        DefaultPromise<String> promise = new DefaultPromise<>(channel.eventLoop());
        JobInvokeRequest jobInvokeRequest = new JobInvokeRequest(jobHandler, params);
        channel.writeAndFlush(jobInvokeRequest);
        JobInvokeResponseHandler.PROMISE_MAP.put(jobInvokeRequest.getSequenceId(), promise);
        // 等待接受结果
        promise.await();
        if (promise.isSuccess()) {
            return JobResponse.success(promise.getNow());
        } else {
            throw new RuntimeException(promise.cause());
        }
    }
}
