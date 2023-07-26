package top.mnsx.job.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class NetUtil {

    public NetUtil() {

    }

    public static String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("[Mnsx-Job] 获取IP地址失败，异常={}", ThrowableUtil.getThrowableStackTrace(e));
            throw new RuntimeException("获取IP地址失败");
        }
    }
}
