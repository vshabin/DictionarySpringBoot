package com.example.demo.infrastructure.repositories;

import io.ebean.Database;
import org.springframework.stereotype.Component;

@Component
public class DbServer {
    private Database DB;

    public Database getDB() {
        return DB;
    }

    public void setDB(Database DB) {
        this.DB = DB;
    }
}
