package com.example.demo;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.logging.Logger;

@Component
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

    @Inject
    DbServer dbServer;

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

}
