package com.example.demo.config;

import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.security.UserProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.ebean.DatabaseFactory;
import io.ebean.config.CurrentUserProvider;
import io.ebean.config.DatabaseConfig;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.logging.Logger;

@Configuration
@EnableScheduling
public class AppConfig {
    @Value("${threads.poolSize}")
    private int poolSize;
    @Value("${threads.queueCapacity}")
    private int queueCapacity;
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

}
