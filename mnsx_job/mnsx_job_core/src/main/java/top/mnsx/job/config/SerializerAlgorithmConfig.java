package top.mnsx.job.config;

import org.springframework.context.annotation.Configuration;
import top.mnsx.job.serializer.SerializerAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class  SerializerAlgorithmConfig {
    static Properties properties;

    static {
        // 读取Properties中的数据
        try (
                InputStream in = SerializerAlgorithmConfig.class.getResourceAsStream("/application.properties")
        ) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前的序列化方式（默认为Jdk）
     * @return
     */
    public static SerializerAlgorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if (value == null) {
            return SerializerAlgorithm.jdk;
        } else {
            return SerializerAlgorithm.valueOf(value.toLowerCase());
        }
    }
}