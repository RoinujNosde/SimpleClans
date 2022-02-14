package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class DiscordHookException extends Exception {

    private String translateKey;

    public DiscordHookException(String message) {
        super(message);
    }

    public DiscordHookException(String message, String translateKey) {
        super(message);
        this.translateKey = translateKey;
    }

    public String getTranslateKey() {
        return translateKey;
    }
}
