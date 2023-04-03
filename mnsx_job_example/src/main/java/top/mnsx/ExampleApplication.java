package top.mnsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.mnsx.annotation.EnableJobAutoConfiguration;

/**
 * @Author Mnsx_x xx1527030652@gmail.com
 */
@SpringBootApplication
@EnableJobAutoConfiguration(adminIp = "192.168.32.134", adminPort = 7777, appName = "test", appDesc = "test")
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class);
    }
}
