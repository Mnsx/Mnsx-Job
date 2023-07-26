package top.mnsx.job.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import top.mnsx.job.message.JobInvokeResponse;

import java.util.concurrent.ConcurrentHashMap;

public class JobInvokeResponseHandler extends SimpleChannelInboundHandler<JobInvokeResponse> {

    public final static ConcurrentHashMap<Integer, Promise<String>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JobInvokeResponse msg) throws Exception {
        Promise<String> promise = PROMISE_MAP.remove(msg.getSequenceId());
        if (promise != null) {
            String returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }
}
