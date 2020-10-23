package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RequestFinishedEvent;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;
import org.bukkit.scheduler.BukkitRunnable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

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

    public boolean hasRequest(String tag) {
        return requests.containsKey(tag);
    }

    public void addDemoteRequest(ClanPlayer requester, String demotedName, Clan clan) {
    	if (requests.containsKey(clan.getTag())) {
    		return;
    	}
    	String msg = MessageFormat.format(lang("asking.for.the.demotion"), requester.getName(), demotedName);

        ClanPlayer demotedTp = plugin.getClanManager().getClanPlayer(UUIDMigration.getForcedPlayerUUID(demotedName));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());
        acceptors.remove(demotedTp);

        Request req = new Request(plugin, ClanRequest.DEMOTE, acceptors, requester, demotedName, clan, msg);
        req.vote(requester.getName(), VoteResult.ACCEPT);
        requests.put(req.getClan().getTag(), req);
        ask(req);
    }

    public void addPromoteRequest(ClanPlayer requester, String promotedName, Clan clan) {
    	if (requests.containsKey(clan.getTag())) {
    		return;
    	}
		String msg = MessageFormat.format(lang("asking.for.the.promotion"), requester.getName(), promotedName);

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());

        Request req = new Request(plugin, ClanRequest.PROMOTE, acceptors, requester, promotedName, clan, msg);
        requests.put(req.getClan().getTag(), req);
        req.vote(requester.getName(), VoteResult.ACCEPT);
        ask(req);
    }

    public void addDisbandRequest(ClanPlayer requester, Clan clan) {
    	if (requests.containsKey(clan.getTag())) {
    		return;
    	}
		String msg = MessageFormat.format(lang("asking.for.the.deletion"), requester.getName());

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(clan.getLeaders());

        Request req = new Request(plugin, ClanRequest.DISBAND, acceptors, requester, clan.getTag(), clan, msg);
        requests.put(req.getTarget(), req);
        req.vote(requester.getName(), VoteResult.ACCEPT);
        ask(req);
    }

    /**
     * Add a member invite request
     *
     * @param requester the requester
     * @param invitedName the invited Player
     * @param clan the Clan
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
        Request req = new Request(plugin, ClanRequest.INVITE, null, requester, invitedName, clan, msg);
        requests.put(invitedName.toLowerCase(), req);
        ask(req);
    }

    public void addWarStartRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
    	if (requests.containsKey(warClan.getTag())) {
    		return;
    	}
		String msg = MessageFormat.format(lang("proposing.war"), requestingClan.getName(), Helper.stripColors(warClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.START_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(req.getTarget(), req);
        ask(req);
    }

    public void addWarEndRequest(ClanPlayer requester, Clan warClan, Clan requestingClan) {
    	if (requests.containsKey(warClan.getTag())) {
    		return;
    	}
		String msg = MessageFormat.format(lang("proposing.to.end.the.war"), requestingClan.getName(), Helper.stripColors(warClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(warClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.END_WAR, acceptors, requester, warClan.getTag(), requestingClan, msg);
        requests.put(req.getTarget(), req);
        ask(req);
    }

    public void addAllyRequest(ClanPlayer requester, Clan allyClan, Clan requestingClan) {
    	if (requests.containsKey(allyClan.getTag())) {
    		return;
    	}
		String msg = MessageFormat.format(lang("proposing.an.alliance"), requestingClan.getName(), Helper.stripColors(allyClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(allyClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.CREATE_ALLY, acceptors, requester, allyClan.getTag(), requestingClan, msg);
        requests.put(req.getTarget(), req);
        ask(req);
    }

    public void addRivalryBreakRequest(ClanPlayer requester, Clan rivalClan, Clan requestingClan) {
       	if (requests.containsKey(rivalClan.getTag())) {
    		return;
    	}
		String msg = MessageFormat.format(lang("proposing.to.end.the.rivalry"), requestingClan.getName(), Helper.stripColors(rivalClan.getColorTag()));

        List<ClanPlayer> acceptors = Helper.stripOffLinePlayers(rivalClan.getLeaders());
        acceptors.remove(requester);

        Request req = new Request(plugin, ClanRequest.BREAK_RIVALRY, acceptors, requester, rivalClan.getTag(), requestingClan, msg);
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
        Clan clan = req.getClan();
        String invited = req.getTarget();

        if (vote.equals(VoteResult.ACCEPT)) {
            ClanPlayer cp = plugin.getClanManager().getCreateClanPlayerUUID(invited);
            if (cp == null) {
                return;
            }

            clan.addBb(ChatColor.AQUA + lang("joined.the.clan", invited));
            plugin.getClanManager().serverAnnounce(lang("has.joined", invited, clan.getName()));
            clan.addPlayerToClan(cp);
        } else {
            clan.leaderAnnounce(ChatColor.RED + lang("membership.invitation", invited));
        }

        requests.remove(req.getTarget().toLowerCase());
    }

    public void processResults(Request req) {
    	Clan requesterClan = req.getClan();
    	ClanPlayer requesterCp = req.getRequester();
    	
    	String target = req.getTarget();
    	//may be null
    	Clan targetClan = plugin.getClanManager().getClan(target);
    	//may be null
    	UUID targetPlayer = UUIDMigration.getForcedPlayerUUID(target);
    	
    	List<String> accepts = req.getAccepts();
    	List<String> denies = req.getDenies();
    	
    	switch (req.getType()) {
    		case START_WAR:
    			processStartWar(requesterClan, requesterCp, targetClan, accepts, denies);
    			break;
    		case END_WAR:
    			processEndWar(requesterClan, requesterCp, targetClan, accepts, denies);
    			break;
    		case CREATE_ALLY:
				processCreateAlly(requesterClan, requesterCp, targetClan, accepts, denies);
                break;
    		case BREAK_RIVALRY:
    			processBreakRivalry(requesterClan, requesterCp, targetClan, accepts, denies);
                break;
    		case DEMOTE: case PROMOTE:
    			if (!req.votingFinished() || targetPlayer == null) {
    				return;
    			}
    			target = requesterClan.getTag();
    			
    			if (req.getType() == ClanRequest.DEMOTE) {
    				processDemote(req, requesterClan, targetPlayer, denies);
    			}
    			if (req.getType() == ClanRequest.PROMOTE) {
    				processPromote(req, requesterClan, targetPlayer, denies);
    			}
    			break;
    		case DISBAND:
    			if (!req.votingFinished()) {
    				return;
    			}
				processDisband(requesterClan, denies);
                break;
    		default:
    			return;
    	}
    	
        requests.remove(target);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new RequestFinishedEvent(req));
        req.cleanVotes();
    }

	private void processDisband(Clan requesterClan, List<String> denies) {
		if (denies.isEmpty()) {
		    requesterClan.addBb(lang("leaders"), ChatColor.AQUA + lang("has.been.disbanded", requesterClan.getName()));
		    requesterClan.disband();
		} else {
		    String deniers = Helper.toMessage(Helper.toArray(denies), ", ");
		    requesterClan.leaderAnnounce(ChatColor.RED + lang("clan.deletion", deniers));
		}
	}

	private void processPromote(Request req, Clan requesterClan, UUID targetPlayer, List<String> denies) {
		String promotedName = req.getTarget();
		if (denies.isEmpty()) {
		    requesterClan.addBb(lang("leaders"), ChatColor.AQUA + lang("promoted.to.leader", promotedName));
		    requesterClan.promote(targetPlayer);
		} else {
		    String deniers = Helper.toMessage(Helper.toArray(denies), ", ");
		    requesterClan.leaderAnnounce(ChatColor.RED + lang("denied.the.promotion", deniers, promotedName));
		}
	}

	private void processDemote(Request req, Clan requesterClan, UUID targetPlayer, List<String> denies) {
		String demotedName = req.getTarget();

		if (denies.isEmpty()) {
			requesterClan.addBb(lang("leaders"), ChatColor.AQUA
					+ lang("demoted.back.to.member", demotedName));
			requesterClan.demote(targetPlayer);
		} else {
			String deniers = Helper.toMessage(Helper.toArray(denies), ", ");
			requesterClan.leaderAnnounce(
					ChatColor.RED + lang("denied.demotion", deniers, demotedName));
		}
	}

	private void processBreakRivalry(Clan requesterClan, ClanPlayer requesterCp, Clan targetClan, List<String> accepts,
			List<String> denies) {
		if (targetClan != null && requesterClan != null) {
		    if (!accepts.isEmpty()) {
		    	requesterClan.removeRival(targetClan);
		    	targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("broken.the.rivalry", accepts.get(0), requesterClan.getName()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("broken.the.rivalry.with", requesterCp.getName(), targetClan.getName()));
		    } else {
		    	targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("denied.to.make.peace", denies.get(0), requesterClan.getName()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("peace.agreement.denied", targetClan.getName()));
		    }
		}
	}

	private void processCreateAlly(Clan requesterClan, ClanPlayer requesterCp, Clan targetClan, List<String> accepts,
			List<String> denies) {
		if (targetClan != null && requesterClan != null) {
		    if (!accepts.isEmpty()) {
		        requesterClan.addAlly(targetClan);

		        targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("accepted.an.alliance", accepts.get(0), requesterClan.getName()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("created.an.alliance", requesterCp.getName(), targetClan.getName()));
		    } else {
		    	targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("denied.an.alliance", denies.get(0), requesterClan.getName()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("the.alliance.was.denied", targetClan.getName()));
		    }
		}
	}

	private void processEndWar(Clan requesterClan, ClanPlayer requesterCp, Clan targetClan, List<String> accepts,
			List<String> denies) {
		if (requesterClan != null && targetClan != null) {
		    if (!accepts.isEmpty()) {
		    	requesterClan.removeWarringClan(targetClan);
		    	targetClan.removeWarringClan(requesterClan);

		    	targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("you.are.no.longer.at.war", accepts.get(0), requesterClan.getColorTag()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("you.are.no.longer.at.war", requesterClan.getName(), targetClan.getColorTag()));
		    } else {
		    	targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("denied.war.end", denies.get(0), requesterClan.getName()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("end.war.denied", targetClan.getName()));
		    }
		}
	}

	private void processStartWar(Clan requesterClan, ClanPlayer requesterCp, Clan targetClan, List<String> accepts,
			List<String> denies) {
		if (requesterClan != null && targetClan != null) {
		    if (!accepts.isEmpty()) {
		        requesterClan.addWarringClan(targetClan);
		        targetClan.addWarringClan(requesterClan);

		        targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("you.are.at.war", targetClan.getName(), requesterClan.getColorTag()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("you.are.at.war", requesterClan.getName(), targetClan.getColorTag()));
		    } else {
		    	targetClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("denied.war.req", denies.get(0), requesterClan.getName()));
		        requesterClan.addBb(requesterCp.getName(), ChatColor.AQUA + lang("end.war.denied", targetClan.getName()));
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
                    req.getClan().leaderAnnounce(lang("signed.off.request.cancelled", ChatColor.RED + playerName, req.getType()));
                    requests.remove(req.getClan().getTag());
                    break;
                }
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
		}}.runTaskTimerAsynchronously(plugin, 0, plugin.getSettingsManager().getRequestFreqencySecs() * 20L);
    }

    /**
     * Asks a request to players for votes
     *
     * @param req the Request
     */
    public void ask(final Request req) {
        final String tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + req.getClan().getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight();
        final String message = tag + " " + plugin.getSettingsManager().getRequestMessageColor() + req.getMsg();

        if (req.getType().equals(ClanRequest.INVITE)) {
            Player player = Bukkit.getPlayerExact(req.getTarget());

            if (player != null) {
                String options = lang("accept.or.deny", player, ChatBlock.makeEmpty(Helper.stripColors(tag)) + " " + ChatColor.DARK_GREEN + "/" + plugin.getSettingsManager().getCommandAccept() + plugin.getSettingsManager().getPageHeadingsColor(), ChatColor.DARK_RED + "/" + plugin.getSettingsManager().getCommandDeny());

                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, message);
                ChatBlock.sendMessage(player, options);
                ChatBlock.sendBlank(player);
            }
        } else {
            for (ClanPlayer cp : req.getAcceptors()) {
                if (cp.getVote() == null) {
                    Player player = cp.toPlayer();

                    if (player != null) {
                        String options = lang("accept.or.deny", player, ChatBlock.makeEmpty(Helper.stripColors(tag)) + " " + ChatColor.DARK_GREEN + "/" + plugin.getSettingsManager().getCommandAccept() + plugin.getSettingsManager().getPageHeadingsColor(), ChatColor.DARK_RED + "/" + plugin.getSettingsManager().getCommandDeny());

                        ChatBlock.sendBlank(player);
                        ChatBlock.sendMessage(player, message);
                        ChatBlock.sendMessage(player, options);
                        ChatBlock.sendBlank(player);
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                SimpleClans.getInstance().getServer().getPluginManager().callEvent(new RequestEvent(req));
            }
        }.runTask(plugin);
    }
}
