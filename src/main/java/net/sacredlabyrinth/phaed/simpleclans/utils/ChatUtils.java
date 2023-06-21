package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;
import static net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ChatUtils {

    private static final SimpleClans plugin = SimpleClans.getInstance();
    public static boolean HEX_COLOR_SUPPORT;

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(%([A-Za-z]+)%)");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("[&§][0-9a-fA-Fk-orK-OR]");
    private static final Pattern HEX_STRIP_COLOR_PATTERN = Pattern.compile("([&§]#[0-9A-Fa-f]{6})|([&§][0-9a-fA-Fk-orK-OR])|([&§]x([&§][a-fA-F0-9]){6})");

    static {
        try {
            ChatColor.class.getDeclaredMethod("of", String.class);
            HEX_COLOR_SUPPORT = true;
        } catch (NoSuchMethodException e) {
            HEX_COLOR_SUPPORT = false;
        }
    }

    private ChatUtils() {
    }

    public static String getColorByChar(char character) {
        ChatColor color = ChatColor.getByChar(character);
        return color != null ? color.toString() : Character.toString(character);
    }

    public static String parseColors(@NotNull String text) {
        // Special thanks to the Spigot community!
        // https://www.spigotmc.org/threads/hex-color-code-translate.449748/#post-3867795
        if (HEX_COLOR_SUPPORT) {
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
        Pattern pattern = HEX_COLOR_SUPPORT ? HEX_STRIP_COLOR_PATTERN : STRIP_COLOR_PATTERN;
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, "");
        }
        return matcher.appendTail(buffer).toString();
    }

    public static String getLastColorCode(String msg) {
        if (msg.length() < 2) {
            return "";
        }

        String one = msg.substring(msg.length() - 2, msg.length() - 1);
        String two = msg.substring(msg.length() - 1);

        if (one.equals("§")) {
            return one + two;
        }

        if (one.equals("&")) {
            return getColorByChar(two.charAt(0));
        }

        return "";
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

    public static String formatCurrency(double value) {
        return plugin.getSettingsManager().getDecimalFormat().format(value);
    }

    public static String formatDate(long time) {
        return plugin.getSettingsManager().getDateTimeFormatter().format(Instant.ofEpochMilli(time));
    }

}
