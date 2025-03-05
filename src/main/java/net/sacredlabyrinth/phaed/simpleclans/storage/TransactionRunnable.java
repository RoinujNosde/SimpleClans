package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.SQLException;

@FunctionalInterface
public interface TransactionRunnable {
    void run() throws SQLException;
}