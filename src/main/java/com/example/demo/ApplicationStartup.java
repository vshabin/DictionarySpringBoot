package com.example.demo;

import com.example.demo.domainservices.TelegramBot;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.inject.Inject;
import java.util.logging.Logger;

@Component
@Log4j2
public class ApplicationStartup implements ApplicationRunner {
    private static final Logger LOG= Logger.getLogger(String.valueOf(ApplicationStartup.class));

    @Value("${dbConnectionString}")
    private String dbConnectionString;

    @Value("${dbUsername}")
    private String dbUsername;

    @Value("${dbPassword}")
    private String dbPassword;

    private String serverName="dictionary";

    private static HikariConfig config= new HikariConfig();
    private static HikariDataSource ds= new HikariDataSource();
    private static CurrentUserProvider userProvider= new UserProvider();

    @Autowired
    DbServer dbServer;
    @Autowired
    TelegramBot bot;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Application startup");

        config.setJdbcUrl(dbConnectionString);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);

        ds=new HikariDataSource(config);

        var cfg= new DatabaseConfig();
        cfg.setAllQuotedIdentifiers(true);
        cfg.setExpressionNativeIlike(true);
        cfg.addPackage("com.example.demo.infrastructure.repositories");
        cfg.setName(serverName);
        cfg.setDataSource(ds);
        cfg.setDefaultServer(true);
        cfg.setCurrentUserProvider(userProvider);
        dbServer.setDB(DatabaseFactory.create(cfg));

        DatabaseConnection databaseConnection= new JdbcConnection(ds.getConnection());
        var database=liquibase.database.DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection);
        Liquibase liquibase= new Liquibase("db/changelog/db.changelog-master.xml", (ResourceAccessor) new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());

    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot((LongPollingBot) bot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
