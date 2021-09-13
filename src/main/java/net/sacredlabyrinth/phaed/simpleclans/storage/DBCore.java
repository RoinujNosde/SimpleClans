package net.sacredlabyrinth.phaed.simpleclans.storage;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phaed
 */
public interface DBCore
{

    /**
     * @return connection
     */
    Connection getConnection();

    /**
     * @return whether connection can be established
     */
    Boolean checkConnection();

    /**
     * Close connection
     */
    void close();

    /**
     * Execute a select statement
     * @param query
     * @return
     */
    ResultSet select(String query);

    /**
     * Execute an insert statement
     * @param query
     */
    void insert(String query);

    /**
     * Execute an update statement
     * @param query
     */
    void update(String query);

    /**
     * Execute a delete statement
     * @param query
     */
    void delete(String query);

    /**
     * Execute a statement
     * @param query
     * @return
     */
    Boolean execute(String query);

    /**
     * Check whether a table exists
     * @param table
     * @return
     */
    Boolean existsTable(String table);
    
    /**
     * Check whether a colum exists
     *
     * @param tabell
     * @param colum
     * @return
     */
    Boolean existsColumn(String tabell, String colum);

    default void executeAsync(String query, String sqlType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (getConnection() != null) {
                        getConnection().createStatement().executeUpdate(query);
                    }
                }
                catch (SQLException ex) {
                    if (!ex.toString().contains("not return ResultSet")) {
                        Logger.getLogger("SimpleClans")
                                .log(Level.SEVERE, String.format("Error at SQL %s query: %s", sqlType, query), ex);
                    }
                }
            }
        }.runTaskAsynchronously(SimpleClans.getInstance());

    }
}
