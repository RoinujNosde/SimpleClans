package net.sacredlabyrinth.phaed.simpleclans.loggers;

/**
 * The core interface for all bank loggers,
 * which may log all economy actions,
 * with its defining ({@link Operation})
 */
public interface BankLogger {
    void log(BankLog log);

    enum Operation {
        DEPOSIT, WITHDRAW, SET
    }
}
