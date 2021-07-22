package net.sacredlabyrinth.phaed.simpleclans;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.REQUEST_MAX;

/**
 * @author phaed
 */
public final class Request {
    private List<ClanPlayer> acceptors = new ArrayList<>();
    private Clan clan;
    private String msg;
    private String target;
    private ClanRequest type;
    private ClanPlayer requester;
    private int askCount;

    public Request(SimpleClans plugin,
                   ClanRequest type,
                   @Nullable List<ClanPlayer> acceptors,
                   ClanPlayer requester,
                   String target,
                   Clan clan,
                   String msg) {
        this.type = type;
        this.target = target;
        this.clan = clan;
        this.msg = msg;
        if (acceptors != null) {
            this.acceptors = acceptors;
        }
        this.requester = requester;

        cleanVotes();
    }

    /**
     * @return the type
     */
    public ClanRequest getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ClanRequest type) {
        this.type = type;
    }

    /**
     * @return the acceptors
     */
    public List<ClanPlayer> getAcceptors() {
        return Collections.unmodifiableList(acceptors);
    }

    /**
     * @param acceptors the acceptors to set
     */
    public void setAcceptors(List<ClanPlayer> acceptors) {
        this.acceptors = acceptors;
    }

    /**
     * @return the clan
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * @param clan the clan to set
     */
    public void setClan(Clan clan) {
        this.clan = clan;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    public void vote(String playerName, VoteResult vote) {
        for (ClanPlayer cp : acceptors) {
            if (cp.getName().equalsIgnoreCase(playerName)) {
                cp.setVote(vote);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean votingFinished() {
        for (ClanPlayer cp : acceptors) {
            if (cp.getVote() == null) {
                return false;
            }
        }

        return true;
    }

    public List<String> getDenies() {
        List<String> out = new ArrayList<>();

        for (ClanPlayer cp : acceptors) {
            if (cp.getVote() != null && cp.getVote().equals(VoteResult.DENY)) {
                out.add(cp.getName());
            }
        }

        return out;
    }

    public List<String> getAccepts() {
        List<String> out = new ArrayList<>();

        for (ClanPlayer cp : acceptors) {
            if (cp.getVote() != null && cp.getVote().equals(VoteResult.ACCEPT)) {
                out.add(cp.getName());
            }
        }

        return out;
    }

    /**
     * Cleans votes
     */
    public void cleanVotes() {
        for (ClanPlayer cp : acceptors) {
            cp.setVote(null);
        }
    }

    /**
     * @return the requester
     */
    public ClanPlayer getRequester() {
        return requester;
    }

    /**
     * @param requester the requester to set
     */
    public void setRequester(ClanPlayer requester) {
        this.requester = requester;
    }

    public void incrementAskCount() {
        askCount += 1;
    }

    public boolean reachedRequestLimit() {
        return askCount > SimpleClans.getInstance().getSettingsManager().getInt(REQUEST_MAX);
    }
}
