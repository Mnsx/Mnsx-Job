package top.mnsx.job.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mnsx.job.domain.po.JobApp;
import top.mnsx.job.message.AppToAdminRequest;
import top.mnsx.job.message.JobResponse;
import top.mnsx.job.server.AdminServer;
import top.mnsx.job.service.JobAppService;
import top.mnsx.job.utils.Result;

@ChannelHandler.Sharable
@Component
@Slf4j
public class AppToAdminRequestHandler extends SimpleChannelInboundHandler<AppToAdminRequest> {

    @Autowired
    private JobAppService jobAppService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AppToAdminRequest msg) throws Exception {
        JobApp jobApp = new JobApp();
        BeanUtils.copyProperties(msg, jobApp);
        jobApp.setAddressList(msg.getAddress());
        Result<JobApp> result = jobAppService.insert(jobApp);
        log.info("result" + result.isErr());
        if (result.isErr()) {
            log.warn("[Mnsx-Job] 客户端={}注册失败", jobApp.getAddressList());
        } else {
            log.info("[Mnsx-Job] 客户端={}注册成功", jobApp.getAddressList());
            for (String s : jobApp.getAddressList().split(",")) {
                AdminServer.getChannelMap().putIfAbsent(s, ctx.channel());
            }
            ctx.channel().writeAndFlush(JobResponse.success(JSON.toJSONString(jobApp)));
        }
    }
}
