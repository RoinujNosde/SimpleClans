package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestFinishedEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.WarEndEvent;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.RED;

/**
 * @author phaed
 */
public final class RequestManager {
    private final SimpleClans plugin;
    private final HashMap<String, Request> requests = new HashMap<>();

    /**
     *
     */
    public RequestManager() {
        plugin = SimpleClans.getInstance();
        askerTask();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasRequest(String tag) {
        return requests.containsKey(tag);
    }

    public void addDemoteRequest(ClanPlayer requester, String demotedName, Clan clan) {
        if (requests.containsKey(clan.getTag())) {
            return;
        }
        String msg = MessageFormat.format(lang("asking.for.the.demotion"), requester.getName(), demotedName);

        ClanPlayer demotedTp = plugin.getClanManager().getAnyClanPlayer(demotedName);

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(demotedTp);

        Request req = new Request(ClanRequest.DEMOTE, acceptors, requester, demotedName, clan, msg);
        req.vote(requester.getName(), VoteResult.ACCEPT);
        requests.put(req.getClan().getTag(), req);
        ask(req);
    }

    /**
     * This method asks <i>all</i> leaders about
     * some action <i>inside</i> their clan.
     * <p>
     * Example of possible requests:
     * </p>
     * <ul>
     *     <li>Disband request can be asked from all leaders</li>
     *     <li>Rename request can be asked from all leaders</li>
     *     <li>Promote request can be asked from all leaders</li>
     * </ul>
     *
     * <p>
     * Examples of incompatible requests:
     * </p>
     * <ul>
     *      <li>Demote request can be asked from all leaders,
     *      <b>except the demoted one.</b></li>
     *      <li>Invite request has to ask someone <b>outside</b> of leaders clan</li>
     * </ul>
     *
     * @param requester the clan player, who sent the request
     * @param request   the type of request, see: {@link ClanRequest}
     * @param target    the target which will be used in request processing
     * @param key       the language key that would be translated and send the message to all leaders
     * @param args      the language objects, requires in some language strings.
     * @throws IllegalArgumentException if passed incompatible request
     */
    public void requestAllLeaders(@NotNull ClanPlayer requester, @NotNull ClanRequest request,
                                  @NotNull String target, @NotNull String key, @Nullable Object... args) {
        if (request.equals(ClanRequest.INVITE) || request.equals(ClanRequest.DEMOTE)) {
            throw new IllegalArgumentException("Unsupported request: " + request.name());
        }

        Clan clan = requester.getClan();
        if (clan == null || requests.containsKey(clan.getTag())) {
            return;
        }

        String msg = lang(key, args);
        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());

        Request req = new Request(request, acceptors, requester, target, clan, msg);
        requests.put(clan.getTag(), req);
        req.vote(requester.getName(), VoteResult.ACCEPT);

        ask(req);
    }

    /**
     * Add a member invite request
     *
     * @param requester   the requester
     * @param invitedName the invited Player
     * @param clan        the Clan
     */
    public void addInviteRequest(ClanPlayer requester, String invitedName, Clan clan) {
        if (requests.containsKey(invitedName.toLowerCase())) {
            return;
        }
        Player player = Bukkit.getPlayer(invitedName);
        if (player == null) {
            return;
        }

        String msg = lang("inviting.you.to.join", player, requester.getName(), clan.getName());
        Request req = new Request(ClanRequest.INVITE, null, requester, invitedName, clan, msg);
        requests.put(invitedName.toLowerCase(), req);
        ask(req);
    }

    public void addWarStartRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
        if (requests.containsKey(warClan.getTag())) {
            return;
        }
        String msg = MessageFormat.format(lang("proposing.war"), requestingClan.getName(), ChatUtils.stripColors(warClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(ClanRequest.START_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(req.getTarget(), req);
        ask(req);
    }

    public void addWarEndRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
        if (requests.containsKey(warClan.getTag())) {
            return;
        }
        String msg = MessageFormat.format(lang("proposing.to.end.the.war"), requestingClan.getName(), ChatUtils.stripColors(warClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(ClanRequest.END_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(req.getTarget(), req);
        ask(req);
    }

    public void addAllyRequest(ClanPlayer requester, Clan allyClan, Clan requestingClan) {
        if (requests.containsKey(allyClan.getTag())) {
            return;
        }
        String msg = MessageFormat.format(lang("proposing.an.alliance"), requestingClan.getName(), ChatUtils.stripColors(allyClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(allyClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(ClanRequest.CREATE_ALLY, acceptors, requester, allyClan.getTag(), requestingClan, msg);
        requests.put(req.getTarget(), req);
        ask(req);
    }

    public void addRivalryBreakRequest(ClanPlayer requester, Clan rivalClan, Clan requestingClan) {
        if (requests.containsKey(rivalClan.getTag())) {
            return;
        }
        String msg = MessageFormat.format(lang("proposing.to.end.the.rivalry"), requestingClan.getName(), ChatUtils.stripColors(rivalClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(rivalClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(ClanRequest.BREAK_RIVALRY, acceptors, requester, rivalClan.getTag(), requestingClan, msg);
        requests.put(req.getTarget(), req);
        ask(req);
    }

    public void accept(ClanPlayer cp) {
        Request req = requests.get(cp.getTag());

        if (req != null) {
            req.vote(cp.getName(), VoteResult.ACCEPT);
            processResults(req);
        } else {
            req = requests.get(cp.getCleanName());

            if (req != null) {
                processInvite(req, VoteResult.ACCEPT);
            }
        }
    }

    public void deny(ClanPlayer cp) {
        Request req = requests.get(cp.getTag());

        if (req != null) {
            req.vote(cp.getName(), VoteResult.DENY);
            processResults(req);
        } else {
            req = requests.get(cp.getCleanName());

            if (req != null) {
                processInvite(req, VoteResult.DENY);
            }
        }
    }

    public void processInvite(Request req, VoteResult vote) {
        requests.remove(req.getTarget().toLowerCase());

        Clan clan = req.getClan();
        Player invited = Bukkit.getPlayerExact(req.getTarget());
        if (invited == null) {
            return;
        }

        if (vote.equals(VoteResult.ACCEPT)) {
            ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(invited.getUniqueId());
            int maxMembers = !clan.isVerified() ? plugin.getSettingsManager().getInt(CLAN_UNVERIFIED_MAX_MEMBERS) : plugin.getSettingsManager().getInt(CLAN_MAX_MEMBERS);

            if (maxMembers > 0 && maxMembers > clan.getSize()) {
                ChatBlock.sendMessageKey(invited, "accepted.invitation", clan.getName());
                clan.addBb(lang("joined.the.clan", invited.getName()));
                plugin.getClanManager().serverAnnounce(lang("has.joined", invited.getName(), clan.getName()));
                clan.addPlayerToClan(cp);
            } else {
                ChatBlock.sendMessageKey(invited, "this.clan.has.reached.the.member.limit");
            }
        } else {
            ChatBlock.sendMessageKey(invited, "denied.invitation", clan.getName());
            clan.leaderAnnounce(RED + lang("membership.invitation", invited.getName()));
        }
    }


    public void processResults(Request req) {
        Clan requestClan = req.getClan();
        ClanPlayer requester = req.getRequester();

        String target = req.getTarget();

        @Nullable
        Clan targetClan = plugin.getClanManager().getClan(target);

        ClanPlayer targetCp = plugin.getClanManager().getAnyClanPlayer(target);
        @Nullable
        UUID targetUuid = targetCp != null ? targetCp.getUniqueId() : null;

        List<String> accepts = req.getAccepts();
        List<String> denies = req.getDenies();

        switch (req.getType()) {
            case START_WAR:
                processStartWar(requester, requestClan, targetClan, accepts, denies);
                break;
            case END_WAR:
                processEndWar(requester, requestClan, targetClan, accepts, denies);
                break;
            case CREATE_ALLY:
                processCreateAlly(requester, requestClan, targetClan, accepts, denies);
                break;
            case BREAK_RIVALRY:
                processBreakRivalry(requester, requestClan, targetClan, accepts, denies);
                break;
            case DEMOTE:
            case PROMOTE:
                if (!req.votingFinished() || targetUuid == null) {
                    return;
                }
                target = requestClan.getTag();

                if (req.getType() == ClanRequest.DEMOTE) {
                    processDemote(req, requestClan, targetUuid, denies);
                }
                if (req.getType() == ClanRequest.PROMOTE) {
                    processPromote(req, requestClan, targetUuid, denies);
                }
                break;
            case DISBAND:
                if (!req.votingFinished()) {
                    return;
                }
                processDisband(requester, requestClan, denies);
                break;
            case RENAME:
                if (!req.votingFinished()) {
                    return;
                }

                processRename(req);
                break;
            default:
                return;
        }

        requests.remove(target);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new RequestFinishedEvent(req));
        req.cleanVotes();
    }

    private void processRename(Request request) {
        if (request.getDenies().isEmpty()) {
            request.getClan().setName(request.getTarget());
        } else {
            String deniers = String.join(", ", request.getDenies());
            request.getClan().leaderAnnounce(RED + lang("rename.refused", deniers));
        }
    }

    private void processDisband(ClanPlayer requester, Clan requestClan, List<String> denies) {
        if (denies.isEmpty()) {
            requestClan.disband(requester.toPlayer(), true, false);
        } else {
            String deniers = String.join(", ", denies);
            requestClan.leaderAnnounce(RED + lang("clan.deletion", deniers));
        }
    }

    private void processPromote(Request req, Clan requestClan, UUID targetPlayer, List<String> denies) {
        String promotedName = req.getTarget();
        if (denies.isEmpty()) {
            requestClan.addBb(lang("leaders"), lang("promoted.to.leader", promotedName));
            requestClan.promote(targetPlayer);
        } else {
            String deniers = String.join(", ", denies);
            requestClan.leaderAnnounce(RED + lang("denied.the.promotion", deniers, promotedName));
        }
    }

    private void processDemote(Request req, Clan requestClan, UUID targetPlayer, List<String> denies) {
        String demotedName = req.getTarget();
        if (denies.isEmpty()) {
            requestClan.addBb(lang("leaders"), lang("demoted.back.to.member", demotedName));
            requestClan.demote(targetPlayer);
        } else {
            String deniers = String.join(", ", denies);
            requestClan.leaderAnnounce(
                    RED + lang("denied.demotion", deniers, demotedName));
        }
    }

    private void processBreakRivalry(ClanPlayer requester, Clan requestClan, @Nullable Clan targetClan,
                                     List<String> accepts, List<String> denies) {
        if (targetClan != null && requestClan != null) {
            if (!accepts.isEmpty()) {
                requestClan.removeRival(targetClan);
                targetClan.addBb(requester.getName(), lang("broken.the.rivalry", accepts.get(0), requestClan.getName()));
                requestClan.addBb(requester.getName(), lang("broken.the.rivalry.with", requester.getName(), targetClan.getName()));
            } else {
                targetClan.addBb(requester.getName(), lang("denied.to.make.peace", denies.get(0), requestClan.getName()));
                requestClan.addBb(requester.getName(), lang("peace.agreement.denied", targetClan.getName()));
            }
        }
    }

    private void processCreateAlly(ClanPlayer requester, Clan requestClan, @Nullable Clan targetClan,
                                   List<String> accepts, List<String> denies) {
        if (targetClan != null && requestClan != null) {
            if (!accepts.isEmpty()) {
                requestClan.addAlly(targetClan);

                targetClan.addBb(requester.getName(), lang("accepted.an.alliance", accepts.get(0), requestClan.getName()));
                requestClan.addBb(requester.getName(), lang("created.an.alliance", requester.getName(), targetClan.getName()));
            } else {
                targetClan.addBb(requester.getName(), lang("denied.an.alliance", denies.get(0), requestClan.getName()));
                requestClan.addBb(requester.getName(), lang("the.alliance.was.denied", targetClan.getName()));
            }
        }
    }

    private void processEndWar(ClanPlayer requester, Clan requestClan, @Nullable Clan targetClan, List<String> accepts,
                               List<String> denies) {
        if (requestClan != null && targetClan != null) {
            if (!accepts.isEmpty()) {
                War war = plugin.getProtectionManager().getWar(requestClan, targetClan);
                plugin.getProtectionManager().removeWar(war, WarEndEvent.Reason.REQUEST);
                requestClan.removeWarringClan(targetClan);
                targetClan.removeWarringClan(requestClan);

                targetClan.addBb(requester.getName(), lang("you.are.no.longer.at.war", accepts.get(0), requestClan.getColorTag()));
                requestClan.addBb(requester.getName(), lang("you.are.no.longer.at.war", requestClan.getName(), targetClan.getColorTag()));
            } else {
                targetClan.addBb(requester.getName(), lang("denied.war.end", denies.get(0), requestClan.getName()));
                requestClan.addBb(requester.getName(), lang("end.war.denied", targetClan.getName()));
            }
        }
    }

    private void processStartWar(ClanPlayer requester, Clan requestClan, @Nullable Clan targetClan,
                                 List<String> accepts, List<String> denies) {
        if (requestClan != null && targetClan != null) {
            if (!accepts.isEmpty()) {
                plugin.getProtectionManager().addWar(requester, requestClan, targetClan);
            } else {
                targetClan.addBb(requester.getName(), lang("denied.war.req", denies.get(0),
                        requestClan.getName()));
                requestClan.addBb(requester.getName(), lang("end.war.denied",
                        targetClan.getName()));
            }
        }
    }

    /**
     * End a pending request prematurely
     *
     * @param playerName the Player signing off
     */
    public void endPendingRequest(String playerName) {
        for (Request req : new LinkedList<>(requests.values())) {
            for (ClanPlayer cp : req.getAcceptors()) {
                if (cp.getName().equalsIgnoreCase(playerName)) {
                    req.getClan().leaderAnnounce(lang("signed.off.request.cancelled", RED + playerName, req.getType()));
                    requests.remove(req.getClan().getTag());
                    break;
                }
            }
        }

    }

    public void removeRequest(@NotNull String keyOrTarget) {
        Iterator<Map.Entry<String, Request>> iterator = requests.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Request> entry = iterator.next();
            final String requester = entry.getKey();
            final String target = entry.getValue().getTarget();
            if (keyOrTarget.equals(requester) || keyOrTarget.equals(target)) {
                entry.getValue().cleanVotes();
                iterator.remove();
            }
        }
    }

    /**
     * Starts the task that asks for the votes of all requests
     */
    public void askerTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Iterator<Map.Entry<String, Request>> iter = requests.entrySet().iterator(); iter.hasNext(); ) {
                    Request req = iter.next().getValue();

                    if (req == null) {
                        continue;
                    }

                    if (req.reachedRequestLimit()) {
                        iter.remove();
                    }

                    ask(req);
                    req.incrementAskCount();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, plugin.getSettingsManager().getSeconds(REQUEST_FREQUENCY));
    }

    /**
     * Asks a request to players for votes
     *
     * @param req the Request
     */
    public void ask(final Request req) {
        String message = lang("request.message", req.getClan().getColorTag(), req.getMsg());
        ArrayList<Player> recipients = new ArrayList<>();
        if (req.getType() == ClanRequest.INVITE) {
            recipients.add(Bukkit.getPlayerExact(req.getTarget()));
        } else {
            for (ClanPlayer cp : req.getAcceptors()) {
                if (cp.getVote() == null) {
                    recipients.add(cp.toPlayer());
                }
            }
        }

        for (Player recipient : recipients) {
            if (recipient != null) {
                recipient.spigot().sendMessage(ChatUtils.toBaseComponents(recipient, message));
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new RequestEvent(req)));
    }
}
