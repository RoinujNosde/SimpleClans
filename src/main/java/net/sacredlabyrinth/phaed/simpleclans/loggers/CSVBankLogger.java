package net.sacredlabyrinth.phaed.simpleclans.loggers;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class CSVBankLogger implements BankLogger {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private final SimpleClans plugin;

    public CSVBankLogger(SimpleClans plugin) {
        this.plugin = plugin;

        if (!plugin.getSettingsManager().isBankLogEnabled()) {
            return;
        }

        try {
            FileHandler fh = new FileHandler(getFilePath(), (int) (100 * Math.pow(10, 6)), 1, true);
            fh.setFormatter(new CSVFormatter());
            fh.setLevel(Level.INFO);
            fh.setEncoding("UTF-8");
            logger.addHandler(fh);
            if (!plugin.getSettingsManager().isDebugging()) {
                logger.setUseParentHandlers(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(BankLog log) {
        if (!plugin.getSettingsManager().isBankLogEnabled()) {
            return;
        }

        logger.log(Level.INFO, toCSV(log));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NotNull
    String getFilePath() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String folderPath = plugin.getDataFolder().getPath() + File.separator + "logs" + File.separator + "bank";
        String filePath = folderPath + File.separator + dateFormat.format(new Date()) + ".csv";

        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                makeHeader(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }

    private void makeHeader(File file) throws FileNotFoundException {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println(String.join(",", BankLog.getHeader()));
        }
    }

    private String toCSV(BankLog log) {
        return String.join(",", log.getValues());
    }

    @NotNull
    public Logger getLogger() {
        return logger;
    }

    static class CSVFormatter extends Formatter {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");

        @Override
        public String format(LogRecord record) {
            return dateFormat.format(record.getMillis()) + "," + record.getMessage() + "\n";
        }
    }
}
