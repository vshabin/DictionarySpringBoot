package com.example.demo.config;

import com.example.demo.domainservices.TelegramClient;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.*;
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

import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Value("${telegram.api-id}")
    private int apiId;
    @Value("${telegram.api-hash}")
    private String apiHash;

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

    @Bean
    @Qualifier("telegramClient")
    public TelegramClient telegramClient() {
        try {
            Init.init();

            try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
                var apiToken = new APIToken(apiId, apiHash);


                // Configure the client
                TDLibSettings settings = TDLibSettings.create(apiToken);

                // Configure the session directory.
                // After you authenticate into a session, the authentication will be skipped from the next restart!
                // If you want to ensure to match the authentication supplier user/bot with your session user/bot,
                //   you can name your session directory after your user id, for example: "tdlib-session-id12345"
                Path sessionPath = Paths.get("example-tdlight-session");
                settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
                settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

                // Prepare a new client builder
                SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);

                // Configure the authentication info
                // Replace with AuthenticationSupplier.consoleLogin(), or .user(xxx), or .bot(xxx);
                SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.testUser(7381);
                // This is an example, remove this line to use the real telegram datacenters!
                settings.setUseTestDatacenter(true);
                return new TelegramClient(clientBuilder, authenticationData);

            }} catch (Exception e) {
            log.error(e);
            }
        return null;
    }

}
