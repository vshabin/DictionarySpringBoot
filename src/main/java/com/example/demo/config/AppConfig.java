package com.example.demo.config;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;


@Configuration
@EnableScheduling
@Log4j2
public class AppConfig {
    @Value("${threads.poolSize}")
    private int poolSize;
    @Value("${threads.queueCapacity}")
    private int queueCapacity;

    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;

    @Bean
    @Qualifier("jobs")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(poolSize);
        pool.setMaxPoolSize(poolSize);
        pool.setQueueCapacity(queueCapacity);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }

    @Bean
    public Session mailSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.mail.ru");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
}
