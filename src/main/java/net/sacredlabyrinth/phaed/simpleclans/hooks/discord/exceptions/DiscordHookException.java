package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class DiscordHookException extends Exception {

    private String translateKey;
    private String clanTag;

    public DiscordHookException() {
    }

    public DiscordHookException(String translateKey) {
        this.translateKey = translateKey;
    }

    public DiscordHookException(String translateKey, String clanTag) {
        this(translateKey);
        this.clanTag = clanTag;
    }

    public String getTranslateKey() {
        return translateKey;
    }

    public String getClanTag() {
        return clanTag;
    }
}
