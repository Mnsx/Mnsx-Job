package top.mnsx.job.properties;

import lombok.Data;

@Data
public class JobProperties {
    // 远程任务控制中心Ip地址
    private String adminIp;

    // 远程任务调用控制中心端口
    private String adminPort;

    // 应用名称
    private String appName;

    // 应用简介
    private String appDesc;

    // Ip地址
    private String ip;

    // 端口
    private String port;
}
