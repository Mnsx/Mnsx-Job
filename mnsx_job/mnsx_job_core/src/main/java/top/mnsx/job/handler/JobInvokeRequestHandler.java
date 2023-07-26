package top.mnsx.job.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.mnsx.job.executor.JobExecutor;
import top.mnsx.job.message.JobInvokeRequest;
import top.mnsx.job.message.JobInvokeResponse;
import top.mnsx.job.message.JobResponse;

public class JobInvokeRequestHandler extends SimpleChannelInboundHandler<JobInvokeRequest> {

    private JobExecutor jobExecutor;

    public JobInvokeRequestHandler(JobExecutor jobExecutor) {
        super();
        this.jobExecutor = jobExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JobInvokeRequest msg) throws Exception {
        String jobHandler = msg.getJobHandler();
        String params = msg.getParams();
        JobInvokeResponse jobInvokeResponse = jobExecutor.jobInvoke(jobHandler, params);
        jobInvokeResponse.setSequenceId(msg.getSequenceId());
        ctx.channel().writeAndFlush(jobInvokeResponse);
    }
}
