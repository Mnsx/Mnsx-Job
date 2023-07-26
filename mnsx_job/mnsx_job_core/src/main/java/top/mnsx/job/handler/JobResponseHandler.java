package top.mnsx.job.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mnsx.job.message.JobResponse;
@Slf4j
@ChannelHandler.Sharable
public class JobResponseHandler extends SimpleChannelInboundHandler<JobResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JobResponse msg) throws Exception {
        if (msg.isOk()) {
            log.info("[Mnsx-Job] 任务执行成功，code={}", msg.getCode());
        } else {
            log.warn("[Mnsx-Job] 任务执行失败，code={}， msg={}", msg.getCode(), msg.getMsg());
        }
    }
}
