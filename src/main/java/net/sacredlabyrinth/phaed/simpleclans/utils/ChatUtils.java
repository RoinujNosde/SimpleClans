package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;
import static net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ChatUtils {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(%([A-Za-z]+)%)");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");
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

    public static String getLastColorCode(String msg) {
        if (msg.length() < 2) {
            return "";
        }

        String one = msg.substring(msg.length() - 2, msg.length() - 1);
        String two = msg.substring(msg.length() - 1);

        if (one.equals("\u00a7")) {
            return one + two;
        }

        if (one.equals("&")) {
            return parseColors(two);
        }

        return "";
    }

    private static String oldStripColors(String text) {
        return text.replaceAll("[&][0-9A-Fa-fk-orx]", "")
                .replaceAll(String.valueOf((char) 194), "") //don't know why
                .replaceAll("[\u00a7][0-9A-Fa-fk-orx]", "");
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
     * Loops through the input and returns the last color codes
     *
     * @param input the input
     * @return the last color codes
     */
    @NotNull
    public static String getLastColors(@NotNull String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();

        for (int index = length - 1; index > -1; index--) {
            boolean found = false;
            String color = String.valueOf(input.charAt(index));
            if (ChatColor.ALL_CODES.contains(color)) {
                if (index - 1 >= 0) {
                    char section = input.charAt(index - 1);
                    if (section == COLOR_CHAR) {
                        index--;
                        result.insert(0, section + color);
                        found = true;
                    }
                }
            }
            if (!found && result.length() != 0) {
                break;
            }
        }

        return result.toString();
    }

    public static void applyLastColorToFollowingLines(@NotNull List<String> lines) {
        if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != COLOR_CHAR) {
            lines.set(0, ChatColor.WHITE + lines.get(0));
        }
        for (int i = 1; i < lines.size(); i++) {
            final String pLine = lines.get(i - 1);
            final String subLine = lines.get(i);

            if (subLine.length() == 0 || subLine.charAt(0) != COLOR_CHAR) {
                lines.set(i, getLastColors(pLine) + subLine);
            }
        }
    }
}
