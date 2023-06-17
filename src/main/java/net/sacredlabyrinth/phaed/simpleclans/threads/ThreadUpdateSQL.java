package net.sacredlabyrinth.phaed.simpleclans.threads;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author NeT32
 */
public class ThreadUpdateSQL extends Thread {

    Connection connection;
    String query;
    String sqlType;

    public ThreadUpdateSQL(Connection connection, String query, String sqlType)
    {
        this.query = query;
        this.connection = connection;
        this.sqlType = sqlType;
    }

    @Override
    public void run()
    {
        try
        {
        	if (!connection.isClosed()) {
        		this.connection.createStatement().executeUpdate(this.query);
        	}
        }
        catch (SQLException ex)
        {
            if (!ex.toString().contains("not return ResultSet"))
            {
                SimpleClans.getInstance().getLogger().severe("[Thread] Error at SQL " + this.sqlType + " Query: " + ex);
                SimpleClans.getInstance().getLogger().severe("[Thread] Query: " + this.query);
            }
        }
    }
}
