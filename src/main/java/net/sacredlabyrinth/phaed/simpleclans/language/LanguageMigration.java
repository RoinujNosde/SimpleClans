package net.sacredlabyrinth.phaed.simpleclans.language;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class that migrates old language files (yaml) to the new format (properties)
 * 
 * @author RoinujNosde
 *
 */
public class LanguageMigration {
	
	private SimpleClans plugin;

	public LanguageMigration(SimpleClans plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Migrates all language files in the plugin's data folder
	 */
	public void migrate() {
		File dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists()) {
			return;
		}
		File[] languageFiles = dataFolder.listFiles(f -> {
			return f.getName().startsWith("language") && f.getName().endsWith(".yml");
		});
		
		for (File file : languageFiles) {
			convert(file);
			backup(file);
			file.delete();
		}
	}

	/**
	 * Copies the file to the backup folder
	 * 
	 * @param file the file to backup
	 */
	private void backup(File file) {
		File backupFolder = new File(plugin.getDataFolder(), "language_backup");
		if (!backupFolder.exists()) {
			backupFolder.mkdir();
		}
		
		file.renameTo(new File(backupFolder, file.getName()));		
	}

	/**
	 * Converts the yaml file to properties
	 * 
	 * @param file
	 */
	private void convert(File file) {
		List<String> lines = readLines(file);

		if (lines == null || lines.isEmpty()) {
			return;
		}

		List<String> convertedLines = convertLines(lines);

		saveFile(file, convertedLines);
	}

	private void saveFile(File file, List<String> convertedLines) {
		String fileName = convertFileName(file);
		File converted = new File(file.getParentFile(), fileName);
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(converted), StandardCharsets.UTF_8))) {
			for (String line : convertedLines) {
				writer.append(line);
				writer.append(System.lineSeparator());
			}

			writer.flush();
		} catch (IOException e) {
			plugin.getLogger().severe("Error converting language file " + file.getName());
			e.printStackTrace();
		}
	}

	@NotNull
	private List<String> convertLines(List<String> lines) {
		List<String> convertedLines = new ArrayList<>();
		for (String line : lines) {
			line = line.replaceFirst(":", "=")
					.replaceAll("''''", "''")
					.replaceAll("^'", "")
					.replaceAll("= \'", "=")
					.replaceAll("'$", "")
					.replaceAll("^\"", "")
					.replaceAll("\"$", "")
					.replaceAll("= \"", "=");
			convertedLines.add(line);
		}
		return convertedLines;
	}

	@Nullable
	private List<String> readLines(File file) {
		List<String> lines = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			lines = reader.lines().collect(Collectors.toList());
		} catch (IOException e) {
			plugin.getLogger().severe("Error converting language file " + file.getName());
			e.printStackTrace();
		}
		return lines;
	}

	@NotNull
	private String convertFileName(File file) {
		String fileName;
		if (file.getName().equals("language.yml")) {
			fileName = "messages_en.properties";
		} else {
			fileName = file.getName().replace("language", "messages")
					.replace("yml", "properties").replace("-", "_");
		}
		return fileName;
	}
}
