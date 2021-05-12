package net.sacredlabyrinth.phaed.simpleclans.loggers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

public class CSVBankLogger implements BankLogger {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private final SimpleClans plugin;
    private final DecimalFormat decimalFormat = new DecimalFormat("##.##");

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
    public void log(@Nullable CommandSender sender, @NotNull Clan clan, @NotNull EconomyResponse economyResponse, @NotNull Operation operation, ClanBalanceUpdateEvent.Cause cause, double amount) {
        if (!plugin.getSettingsManager().isBankLogEnabled()) {
            return;
        }

        List<String> logList = new ArrayList<>();
        logList.add(sender != null ? sender.getName() : "API");
        logList.add(clan.getName());
        logList.add(economyResponse.name());
        logList.add(operation.name());
        logList.add(cause.name());
        if (sender instanceof Player) {
            logList.add(String.valueOf(plugin.getPermissionsManager().playerGetMoney((Player) sender)));
        } else {
            logList.add(decimalFormat.format(0));
        }
        logList.add(decimalFormat.format(amount));
        logList.add(decimalFormat.format(clan.getBalance()));

        logger.log(Level.INFO, String.join(",", logList));
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
            String header = "Date,Sender,Clan Name,Response,Operation,Cause,Sender Balance,Amount,Clan Balance";
            pw.println(header);
        }
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
