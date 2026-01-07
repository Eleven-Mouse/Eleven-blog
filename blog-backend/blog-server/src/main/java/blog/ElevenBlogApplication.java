package blog;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Eleven博客系统启动类
 *
 * 这是一个基于Spring Boot的博客管理系统，提供文章管理、用户认证等功能
 *
 * @author Eleven
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("blog.mapper")
@Slf4j
@EnableScheduling
public class ElevenBlogApplication
{

    public static void main(String[] args)
    {
        // 启动Spring Boot应用程序
        SpringApplication.run(ElevenBlogApplication.class, args);
        log.info("Eleven博客系统启动成功！");
    }
}
