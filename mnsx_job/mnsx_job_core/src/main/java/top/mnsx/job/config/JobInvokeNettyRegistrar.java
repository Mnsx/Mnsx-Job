package top.mnsx.job.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.mnsx.job.executor.JobExecutor;
import top.mnsx.job.handler.JobInvokeRequestHandler;
import top.mnsx.job.handler.JobResponseHandler;
import top.mnsx.job.message.JobInvokeRequest;
import top.mnsx.job.message.PingMessage;
import top.mnsx.job.properties.JobProperties;
import top.mnsx.job.protocol.MessageCodecSharable;
import top.mnsx.job.protocol.ProtocolFrameDecoder;
import top.mnsx.job.utils.IdUtil;
import top.mnsx.job.utils.ThrowableUtil;

@Slf4j
public class JobInvokeNettyRegistrar {
    @Setter
    private JobProperties jobProperties;
    @Setter
    private JobExecutor jobExecutor;

    public void init() {
        // 需要为Netty新起一个线程，否则会因为Tomcat阻塞
        new Thread(() -> {
            // 获取连接参数
            String adminIp = jobProperties.getAdminIp();
            String adminPort = jobProperties.getAdminPort();
            // 工作组
            NioEventLoopGroup group = new NioEventLoopGroup();
            // 日志处理器
            final LoggingHandler loggingHandler = new LoggingHandler();
            // 消息编解码器
            final MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
            // 响应处理器
            final JobResponseHandler jobResponseHandler = new JobResponseHandler();

            try {
                log.info("[Mnsx-Job] 准备创建Netty客户端...");
                Channel channel = new Bootstrap()
                        .channel(NioSocketChannel.class)
                        .group(group)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ProtocolFrameDecoder());
                                ch.pipeline().addLast(loggingHandler);
                                ch.pipeline().addLast(messageCodecSharable);
                                ch.pipeline().addLast(jobResponseHandler);
                                ch.pipeline().addLast(new JobInvokeRequestHandler(jobExecutor));
                                // 心跳检测
                                // 如果3s没有进行写操作，修改状态为WRITE_IDLE
                                ch.pipeline().addLast(new IdleStateHandler(0, 10, 0));
                                // 如果状态为WRITE_IDLE
                                ch.pipeline().addLast(new ChannelDuplexHandler() {
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        IdleStateEvent event = (IdleStateEvent) evt;
                                        if (event.state() == IdleState.WRITER_IDLE) {
                                            // 发送心跳包
                                            ctx.channel().writeAndFlush(new PingMessage(IdUtil.generateAtomicId()));
                                        }
                                    }
                                });
                            }
                        })
                        .connect(adminIp, Integer.parseInt(adminPort))
                        .sync()
                        .channel();
                log.info("[Mnsx-Job] Netty客户端创建成功，并连接到服务器, ip={}, port={}", adminIp, adminPort);

                // jobExecutor需要通过channel发送注册请求到服务端
                this.jobExecutor.setChannel(channel);

                // 阻塞通道，等待关闭后执行
                channel.closeFuture().sync();
                log.info("[Mnsx-Job] 当前通道已经关闭");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[Mnsx-Job] Netty客户端不能连接到服务端, ip={}, port={}, error={}", adminIp, adminPort, ThrowableUtil.getThrowableStackTrace(e));
            } finally {
                group.shutdownGracefully();
            }
        }).start();
    }
}
