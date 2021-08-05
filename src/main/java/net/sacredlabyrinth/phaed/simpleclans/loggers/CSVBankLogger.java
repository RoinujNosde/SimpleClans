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

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DEBUG;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.ECONOMY_BANK_LOG_ENABLED;

/**
 * Logs all bank actions to one CSV file per day.
 *
 * It accepts {@link BankLog} as a record
 * and uses the format from {@link CSVFormatter}
 *
 * Typical usage:
 * <pre>{@code
 *     CSVBankLogger bankLogger = new CSVBankLogger(plugin);
 *     bankLogger.log(new BankLog(sender, clan, economyResponse, operation, cause, amount));
 * }</pre>
 */
public class CSVBankLogger implements BankLogger {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private final SimpleClans plugin;

    static final int FILE_SIZE = (int) (100 * Math.pow(10, 6));
    static final int FILE_COUNT = 1;

    public CSVBankLogger(SimpleClans plugin) {
        this.plugin = plugin;

        if (!plugin.getSettingsManager().is(ECONOMY_BANK_LOG_ENABLED)) {
            return;
        }

        try {
            FileHandler fh = new FileHandler(getFilePath(), FILE_SIZE, FILE_COUNT, true);
            fh.setFormatter(new CSVFormatter());
            fh.setLevel(Level.INFO);
            fh.setEncoding("UTF-8");
            logger.addHandler(fh);
            if (!plugin.getSettingsManager().is(DEBUG)) {
                logger.setUseParentHandlers(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(BankLog log) {
        if (!plugin.getSettingsManager().is(ECONOMY_BANK_LOG_ENABLED)) {
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

        @Override
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    }
}
