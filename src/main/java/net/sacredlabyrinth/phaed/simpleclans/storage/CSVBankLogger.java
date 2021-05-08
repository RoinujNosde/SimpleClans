package net.sacredlabyrinth.phaed.simpleclans.storage;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

public class CSVBankLogger implements BankLogger {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final SimpleClans plugin;

    public CSVBankLogger(SimpleClans plugin) {
        this.plugin = plugin;

        if (!plugin.getSettingsManager().isBankLogEnabled()) {
            return;
        }

        try {
            FileHandler fh = new FileHandler(getFilePath(), (int) (100 * Math.pow(10, 6)), 10, false);
            fh.setFormatter(new CSVFormatter());
            fh.setLevel(Level.INFO);
            fh.setEncoding("UTF-8");
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(CommandSender sender, Clan clan, EconomyResponse economyResponse, Operation operation, double amount) {
        if (!plugin.getSettingsManager().isBankLogEnabled()) {
            return;
        }

        List<String> logList = new ArrayList<>();
        logList.add(sender.getName());
        logList.add(clan.getName());
        logList.add(economyResponse.name());
        logList.add(operation.name());
        logList.add(String.valueOf(amount));
        if (sender instanceof Player) {
            logList.add(String.valueOf(plugin.getPermissionsManager().playerGetMoney((Player) sender)));
        }
        logList.add(String.valueOf((int) clan.getBalance()));

        logger.log(Level.INFO, String.join(",", logList));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NotNull
    String getFilePath() {
        String folderPath = plugin.getDataFolder().getPath() + File.separator + "bankLogs";
        String filePath = folderPath + File.separator + DateFormat.getDateInstance().format(new Date()) + "_%u_%g.csv";

        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return filePath;
    }

    @NotNull
    public Logger getLogger() {
        return logger;
    }

    static class CSVFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            Date now = new Date(record.getMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
            return dateFormat.format(now) + "," + record.getMessage() + "\n";
        }
    }
}
