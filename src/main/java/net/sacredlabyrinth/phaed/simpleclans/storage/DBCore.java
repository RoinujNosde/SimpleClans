package net.sacredlabyrinth.phaed.simpleclans.storage;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author phaed
 */
public interface DBCore {

    SimpleClans plugin = SimpleClans.getInstance();
    Logger log = plugin.getLogger();

    /**
     * @return connection
     */
    Connection getConnection();

    /**
     * @return whether connection can be established
     */
    default boolean checkConnection() {
        return getConnection() != null;
    }

    /**
     * Close connection
     */
    void close();

    /**
     * Execute a select statement
     *
     * @param query the query
     * @return the result set or null if the query failed
     */
    default @Nullable ResultSet select(String query) {
        try {
            return getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("Error executing query: %s", query), ex);
        }
        return null;
    }

    /**
     * Execute a statement
     *
     * @param query the query
     * @return true if the statement was executed
     */
    default boolean execute(String query) {
        try {
            getConnection().createStatement().execute(query);
            return true;
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("Error executing query: %s", query), ex);
            return false;
        }
    }

    /**
     * Check whether a table exists
     *
     * @param table the table
     * @return true if the table exists
     */
    default boolean existsTable(String table) {
        try {
            ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null);
            return tables.next();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, String.format("Error checking if table %s exists", table), ex);
            return false;
        }
    }

    /**
     * Check whether a column exists
     *
     * @param table  the table
     * @param column the column
     * @return true if the column exists
     */
    default boolean existsColumn(String table, String column) {
        try {
            ResultSet col = getConnection().getMetaData().getColumns(null, null, table, column);
            return col.next();
        } catch (Exception ex) {
            log.log(Level.SEVERE, String.format("Error checking if column %s exists in table %s", column, table), ex);
            return false;
        }
    }

    default void executeUpdate(String query) {
        final Exception exception = new Exception(); // Stores a reference to the caller's stack trace for async tasks
        Runnable executeUpdate = () -> {
            if (getConnection() != null) {
                try {
                    getConnection().createStatement().executeUpdate(query);
                } catch (SQLException ex) {
                    log.log(Level.SEVERE, String.format("Error executing query: %s", query), ex);
                    if (!Bukkit.isPrimaryThread()) {
                        log.log(Level.SEVERE, "Caller's stack trace:", exception);
                    }
                }
            }
        };
        if (plugin.getSettingsManager().is(ConfigField.PERFORMANCE_USE_THREADS)) {
            plugin.getScheduler().runLaterAsync(executeUpdate, 0);
        } else {
            executeUpdate.run();
        }
    }
}
