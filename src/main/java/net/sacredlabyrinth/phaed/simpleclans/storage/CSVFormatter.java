package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CSVFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        Date date = new Date(record.getMillis());
        return date + "," + record.getMessage() + "\n";
    }
}
