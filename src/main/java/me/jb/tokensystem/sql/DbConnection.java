package me.jb.tokensystem.sql;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String DATABASE_URL = "jdbc:mysql";

    private final Plugin plugin;
    private Connection connection;

    public DbConnection(Plugin plugin) {
        this.plugin = plugin;
    }

    public void open(FileConfiguration config) throws SQLException {

        if (this.isOpened())
            throw new SQLException("Connection already opened.");

        String host = config.getString("mysql.host");
        String database = config.getString("mysql.database");
        String user = config.getString("mysql.username");
        String password = config.getString("mysql.password");
        String url = String.format("%s://%s/%s?autoReconnect=true", DATABASE_URL, host, database);

        this.connection = DriverManager.getConnection(url, user, password);
    }

    public void close() throws SQLException {

        if (!this.isOpened())
            throw new SQLException("Connection already closed.");

        this.connection.close();
        this.connection = null;
    }

    public boolean isOpened() throws SQLException {
        return this.connection != null && !this.connection.isClosed();
    }

    public Connection getConnection() {
        return this.connection;
    }
}
