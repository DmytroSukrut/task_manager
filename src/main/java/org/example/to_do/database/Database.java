package org.example.to_do.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class Database {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.user}")
    private String user;

    @Value("${jdbc.password}")
    private String password;

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
