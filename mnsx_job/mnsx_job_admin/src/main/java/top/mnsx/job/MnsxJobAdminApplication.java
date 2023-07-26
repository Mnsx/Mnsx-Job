package top.mnsx.job;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.mnsx.job.mapper")
public class MnsxJobAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MnsxJobAdminApplication.class);
    }
}
