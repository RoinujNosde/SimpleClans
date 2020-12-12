package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.md_5.bungee.api.chat.*;
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

    private ChatUtils() {
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
            builder.appendLegacy(split[i]);
            if (placeholders.size() == i) {
                continue;
            }
            appendPlaceholder(receiver, builder, placeholders.get(i));
        }

        return builder.create();
    }

    private static void appendPlaceholder(@Nullable CommandSender receiver, ComponentBuilder builder, String placeholder) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(placeholder);
        if (!matcher.find()) {
            return;
        }
        placeholder = matcher.group(2);
        builder.appendLegacy(lang("clickable." + placeholder, receiver))
                .event(new ClickEvent(RUN_COMMAND, "/" + placeholder))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]
                        {new TextComponent(lang("hover.click.to." + placeholder, receiver))}));

    }
}
