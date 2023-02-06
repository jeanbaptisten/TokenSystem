package me.jb.tokensystem.sql;

import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDbConnection {

    private static final String DATABASE_URL = "jdbc:sqlite";
    private static final String DATABASE_FILE = "storage.db";

    private final Plugin plugin;
    private Connection connection;

    public SQLDbConnection(Plugin plugin) {
        this.plugin = plugin;
    }

    public void open() throws SQLException, ClassNotFoundException {

        if (this.isOpened()) throw new SQLException("Connection already opened.");

        Class.forName("org.sqlite.JDBC");

        Path path = Paths.get(this.plugin.getDataFolder() + "/" + DATABASE_FILE);

        this.connection = DriverManager.getConnection(DATABASE_URL + ":" + path.toString());
    }

    public void close() throws SQLException {

        if (!this.isOpened()) throw new SQLException("Connection already closed.");

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
