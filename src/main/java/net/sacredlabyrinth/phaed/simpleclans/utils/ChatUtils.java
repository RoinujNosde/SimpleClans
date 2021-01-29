package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ChatUtils {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(%([A-Za-z]+)%)");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#(\\w{5}[0-9A-Fa-f])");
    private static boolean supportsHexColors;

    static {
        try {
            ChatColor.class.getDeclaredMethod("of", String.class);
            supportsHexColors = true;
        } catch (NoSuchMethodException e) {
            supportsHexColors = false;
        }
    }

    private ChatUtils() {
    }

    public static String parseColors(@NotNull String text) {
        // Special thanks to the Spigot community!
        // https://www.spigotmc.org/threads/hex-color-code-translate.449748/#post-3867795
        if (supportsHexColors) {
            Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
            }
            text = matcher.appendTail(buffer).toString();
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String stripColors(String text) {
        if (supportsHexColors) {
            Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buffer, "");
            }
            text = matcher.appendTail(buffer).toString();
        }
        return oldStripColors(text);
    }

    private static String oldStripColors(String text) {
        return text.replaceAll("[&][0-9A-Za-z]", "")
                .replaceAll(String.valueOf((char) 194), "") //don't know why
                .replaceAll("[\u00a7][0-9A-Za-z]", "");
    }

    public static BaseComponent[] toBaseComponents(@Nullable CommandSender receiver, @NotNull String text) {
        ComponentBuilder builder = new ComponentBuilder("");
        ArrayList<String> placeholders = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            placeholders.add(matcher.group(0));
        }
        String[] split = PLACEHOLDER_PATTERN.split(text);
        for (int i = 0; i < split.length; i++) {
            builder.append(split[i]);
            if (i >= placeholders.size()) {
                continue;
            }
            appendPlaceholder(receiver, builder, placeholders.get(i));
        }

        return builder.create();
    }

    @SuppressWarnings("deprecation")
    private static void appendPlaceholder(@Nullable CommandSender receiver, ComponentBuilder builder, String placeholder) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(placeholder);
        if (!matcher.find()) {
            return;
        }
        placeholder = matcher.group(2);
        builder.retain(FormatRetention.FORMATTING).append(lang("clickable." + placeholder, receiver))
                .event(new ClickEvent(RUN_COMMAND, "/" + placeholder))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                        (lang("hover.click.to." + placeholder, receiver)))));
    }

    /**
     * Breaks a raw string up into a series of lines. Words are wrapped using
     * spaces as decimeters and the newline character is respected.
     *
     * @param rawString  The raw string to break.
     * @param lineLength The length of a line of text.
     * @return An array of word-wrapped lines.
     */
    @NotNull
    public static String[] wordWrap(@Nullable String rawString, int lineLength) {
        // A null string is a single line
        if (rawString == null) {
            return new String[]{""};
        }

        // A string shorter than the lineWidth is a single line
        if (rawString.length() <= lineLength && !rawString.contains("\n")) {
            return new String[]{rawString};
        }

        Pattern splitPattern = Pattern.compile("(?<=\\G.{" + lineLength + "})");
        char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        int lineColorChars = 0;

        for (int i = 0; i < rawChars.length; i++) {
            char c = rawChars[i];

            // skip chat color modifiers
            if (c == ChatColor.COLOR_CHAR) {
                word.append(ChatColor.COLOR_CHAR);
                word.append(rawChars[i + 1]);
                lineColorChars += 2;
                i++; // Eat the next character as we have already processed it
                continue;
            }

            if (c == ' ' || c == '\n') {

                if (line.length() == 0 && word.length() - lineColorChars > lineLength) { // special case: extremely long word begins a line
                    lines.addAll(Arrays.asList(splitPattern.split(word.toString())));
                } else if (line.length() + 1 + word.length() - lineColorChars == lineLength) { // Line exactly the correct length...newline
                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                    lines.add(line.toString());
                    line = new StringBuilder();
                    lineColorChars = 0;
                } else if (line.length() + 1 + word.length() - lineColorChars > lineLength) { // Line too long...break the line
                    for (String partialWord : splitPattern.split(word.toString())) {
                        lines.add(line.toString());
                        line = new StringBuilder(partialWord);
                    }
                    lineColorChars = 0;
                } else {
                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                }
                word = new StringBuilder();

                if (c == '\n') { // Newline forces the line to flush
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
            } else {
                word.append(c);
            }
        }

        if (line.length() > 0) { // Only add the last line if there is anything to add
            lines.add(line.toString());
        }

        // Iterate over the wrapped lines, applying the last color from one line to the beginning of the next
        // TODO Fix me
        try {
            applyLastColorToFollowingLines(lines);
        } catch (StringIndexOutOfBoundsException ex) {
            Logger.getLogger("SimpleClans").warning(String.format("Error wrapping string: %s", rawString));
            return new String[] {rawString};
        }
        return lines.toArray(new String[0]);
    }

    private static void applyLastColorToFollowingLines(List<String> lines) {
        if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != ChatColor.COLOR_CHAR) {
            lines.set(0, ChatColor.WHITE + lines.get(0));
        }
        for (int i = 1; i < lines.size(); i++) {
            final String pLine = lines.get(i - 1);
            final String subLine = lines.get(i);

            char color = pLine.charAt(pLine.lastIndexOf(ChatColor.COLOR_CHAR) + 1);
            if (subLine.length() == 0 || subLine.charAt(0) != ChatColor.COLOR_CHAR) {
                lines.set(i, ChatColor.getByChar(color) + subLine);
            }
        }
    }
}
