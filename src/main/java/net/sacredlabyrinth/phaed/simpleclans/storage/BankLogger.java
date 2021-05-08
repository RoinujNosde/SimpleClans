package net.sacredlabyrinth.phaed.simpleclans.storage;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankLogger {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final SimpleClans plugin;

    public BankLogger(SimpleClans plugin) {
        this.plugin = plugin;

        if (!plugin.getSettingsManager().isBankLogEnabled()) {
            return;
        }

        String filePathWithoutFile = plugin.getDataFolder().getPath() + File.separator + "bankLogs";
        String filePath = filePathWithoutFile + File.separator + DateFormat.getDateInstance().format(new Date(System.currentTimeMillis())) + "_%u_%g.csv";

        File file = new File(filePathWithoutFile);
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            FileHandler fh = new FileHandler(filePath, (int) (10 * Math.pow(10, 6)), 10, false);
            fh.setFormatter(new CSVFormatter());
            fh.setLevel(Level.INFO);
            fh.setEncoding("UTF-8");
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(CommandSender sender, Clan clan, EconomyResponse response, Operation operation, double amount) {
        if (!plugin.getSettingsManager().isBankLogEnabled()) {
            return;
        }

        List<String> logList = new ArrayList<>();
        logList.add(sender.getName());
        logList.add(clan.getName());
        logList.add(response.name());
        logList.add(operation.name());
        logList.add(String.valueOf(amount));
        if (sender instanceof Player) {
            logList.add(String.valueOf(plugin.getPermissionsManager().playerGetMoney((Player) sender)));
        }
        logList.add(String.valueOf((int) clan.getBalance()));

        logger.log(Level.INFO, String.join(",", logList));
    }

    public enum Operation {
        DEPOSIT, WITHDRAW, SET
    }
}
