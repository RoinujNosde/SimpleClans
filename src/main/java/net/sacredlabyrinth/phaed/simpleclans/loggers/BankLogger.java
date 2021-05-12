package net.sacredlabyrinth.phaed.simpleclans.loggers;

public interface BankLogger {
    void log(BankLog log);

    enum Operation {
        DEPOSIT, WITHDRAW, SET
    }
}
