package top.mnsx.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.mnsx.job.annotation.EnableJobAutoConfiguration;

@SpringBootApplication
@EnableJobAutoConfiguration(adminIp = "localhost", appName = "test", appDesc = "test")
public class TemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class);
    }
}
