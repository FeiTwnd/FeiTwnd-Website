package cc.feitwnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching // 开启缓存
@EnableTransactionManagement // 开启事务管理
@EnableScheduling // 开启定时任务
@EnableAsync // 开启异步方法执行
public class FeiTwndBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeiTwndBackendApplication.class, args);
    }
}
