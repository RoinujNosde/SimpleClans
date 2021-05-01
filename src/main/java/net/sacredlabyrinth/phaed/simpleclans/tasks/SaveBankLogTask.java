package net.sacredlabyrinth.phaed.simpleclans.tasks;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveBankLogTask extends BukkitRunnable {

    private final SimpleClans plugin;
    private final SettingsManager sm;

    private File logFile;

    public SaveBankLogTask() {
        plugin = SimpleClans.getInstance();
        sm = plugin.getSettingsManager();
    }

    /**
     * Starts the repetitive task
     */
    public void start() {
        int day = 86400 * 20;
        runTaskTimerAsynchronously(plugin, 0, day);
    }

    @Override
    public void run() {
        if (sm.getBankLogType().equalsIgnoreCase("csv")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
            Date now = new Date();

            logFile = new File(plugin.getDataFolder() + File.separator + "bankLogs", formatter.format(now) + "." + sm.getBankLogType());
            if (logFile.exists()) return;

            logFile.getParentFile().mkdirs();

            try {
                logFile.createNewFile();
                SimpleClans.debug("Saving bank log...");
                writeDataToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeDataToFile() throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(logFile);

        if (SimpleClans.getBankLogs() != null) {
            printWriter.println(SimpleClans.getBankLogs());
            SimpleClans.getBankLogs().clear();
        }
    }
}
