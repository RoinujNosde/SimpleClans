package net.sacredlabyrinth.phaed.simpleclans.storage;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author cc_madelg
 */
public class MySQLCore implements DBCore {

    private final Logger log;
    private Connection connection;
    private final String host;
    private final String username;
    private final String password;
    private final String database;
    private final int port;

    /**
     * @param host     The host
     * @param database The database
     * @param username The username
     * @param password The password
     */
    public MySQLCore(String host, String database, int port, String username, String password) {
        this.database = database;
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
        this.log = SimpleClans.getInstance().getLogger();
        initialize();
    }

    private void initialize() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&autoReconnect=true", username, password);
        } catch (ClassNotFoundException e) {
            log.severe("ClassNotFoundException! " + e.getMessage());
        } catch (SQLException e) {
            log.severe("SQLException! " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(0)) {
                initialize();
            }
        } catch (SQLException e) {
            initialize();
        }
        return connection;
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            log.severe("Failed to close database connection! " + e.getMessage());
        }
    }

}
