package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
            //builder.appendLegacy(split[i]);
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
}
