package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ClanDetailsFrame extends SCFrame {
	private final Clan clan;
	private final ClanPlayer cp;
	private final SimpleClans plugin;

	public ClanDetailsFrame(@Nullable SCFrame parent, @NotNull Player viewer, @NotNull Clan clan) {
		super(parent, viewer);
		this.clan = clan;
		plugin = SimpleClans.getInstance();
		cp = plugin.getClanManager().getClanPlayer(getViewer());
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 4)
				continue;
			add(Components.getPanelComponent(slot));
		}

		add(Components.getBackComponent(getParent(), 4, getViewer()));
		add(Components.getClanComponent(this, getViewer(), clan, 13, false));

		addRoster();
		addCoords();
		addAllies();
		addRivals();
		addHome();
		addRegroup();
		addFf();
		addBank();
		addFee();
		addRank();
		addVerify();
		addResign();
		addDisband();
		addChat();
	}

	private void addChat() {
		Channel cpChannel = cp.getChannel();
		boolean clanEnabled = Channel.CLAN.equals(cpChannel);
		boolean allyEnabled = Channel.ALLY.equals(cpChannel);

		SCComponent chat = createChatComponent(clanEnabled, allyEnabled);
		chat.setListener(ClickType.LEFT, () -> {
			if (clanEnabled) {
				cp.setChannel(Channel.NONE);
			} else {
				cp.setChannel(Channel.CLAN);
			}
			updateFrame();
		});
		chat.setPermission(ClickType.LEFT, "simpleclans.member.chat");
		chat.setListener(ClickType.RIGHT, () -> {
			if (allyEnabled) {
				cp.setChannel(Channel.NONE);
			} else {
				cp.setChannel(Channel.ALLY);
			}
			updateFrame();
		});
		chat.setPermission(ClickType.RIGHT, RankPermission.ALLY_CHAT);
		add(chat);
	}

	@NotNull
	private SCComponent createChatComponent(boolean clanEnabled, boolean allyEnabled) {
		String joined = lang("chat.joined", getViewer());
		String notJoined = lang("chat.not.joined", getViewer());

		String clanStatus = clanEnabled ? joined : notJoined;
		String allyStatus = allyEnabled ? joined : notJoined;

		SettingsManager sm = plugin.getSettingsManager();
		String chatCommand = sm.isTagBasedClanChat() ? clan.getTag() : sm.getCommandClanChat();
		String joinArg = lang("join", getViewer());
		String leaveArg = lang("leave", getViewer());
		return new SCComponentImpl(lang("gui.clandetails.chat.title", getViewer()),
				Arrays.asList(
						lang("gui.clandetails.chat.clan.chat.lore", getViewer(), chatCommand),
						lang("gui.clandetails.chat.clan.join.leave.lore", getViewer(), chatCommand, joinArg, leaveArg),
						lang("gui.clandetails.chat.ally.chat.lore", getViewer(), sm.getCommandAlly()),
						lang("gui.clandetails.chat.ally.join.leave.lore", getViewer(), sm.getCommandAlly(), joinArg, leaveArg),
						lang("gui.clandetails.chat.clan.status.lore", getViewer(), clanStatus),
						lang("gui.clandetails.chat.ally.status.lore", getViewer(), allyStatus),
						lang("gui.clandetails.chat.clan.toggle.lore", getViewer()),
						lang("gui.clandetails.chat.ally.toggle.lore", getViewer())),
				XMaterial.KNOWLEDGE_BOOK, 43);
	}

	private void addRank() {
		SCComponent rank = new SCComponentImpl(lang("gui.clandetails.rank.title", getViewer()),
				Collections.singletonList(lang("gui.clandetails.rank.lore", getViewer())), XMaterial.IRON_HELMET,
				37);
		rank.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RanksFrame(this, getViewer(), clan, null)));
		rank.setPermission(ClickType.LEFT, "simpleclans.leader.rank.list");
		add(rank);
	}

	private void addFee() {
		String status = clan.isMemberFeeEnabled() ? lang("fee.enabled", getViewer()) : lang("fee.disabled", getViewer());
		SCComponent fee = new SCComponentImpl(lang("gui.clandetails.fee.title", getViewer()),
				Arrays.asList(lang("gui.clandetails.fee.value.lore", getViewer(), clan.getMemberFee()),
						lang("gui.clandetails.fee.status.lore", getViewer(), status),
						lang("gui.clandetails.fee.toggle.lore", getViewer())),
				XMaterial.GOLD_NUGGET, 41);
		fee.setVerifiedOnly(ClickType.LEFT);
		fee.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "toggle fee", true));
		fee.setPermission(ClickType.LEFT, RankPermission.FEE_ENABLE);
		add(fee);
	}

	private void addDisband() {
		SCComponent disband = new SCComponentImpl(lang("gui.clandetails.disband.title", getViewer()),
				Collections.singletonList(lang("gui.clandetails.disband.lore", getViewer())), XMaterial.BARRIER,
				50);
		disband.setListener(ClickType.MIDDLE, () -> InventoryController.runSubcommand(getViewer(), "disband", false));
		disband.setConfirmationRequired(ClickType.MIDDLE);
		disband.setPermission(ClickType.MIDDLE, "simpleclans.leader.disband");
		add(disband);
	}

	private void addResign() {
		SCComponent resign = new SCComponentImpl(lang("gui.clandetails.resign.title", getViewer()),
				Collections.singletonList(lang("gui.clandetails.resign.lore", getViewer())), XMaterial.IRON_DOOR, 48);
		resign.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "resign", false));
		resign.setConfirmationRequired(ClickType.LEFT);
		resign.setPermission(ClickType.LEFT, "simpleclans.member.resign");
		add(resign);
	}

	private void addVerify() {
		boolean verified = clan.isVerified();
		boolean purchaseVerification = plugin.getSettingsManager().isRequireVerification()
				&& plugin.getSettingsManager().isePurchaseVerification();

		XMaterial material = verified ? XMaterial.REDSTONE_TORCH : XMaterial.LEVER;
		String title = verified ? lang("gui.clandetails.verified.title", getViewer())
				: lang("gui.clandetails.not.verified.title", getViewer());
		List<String> lore = verified ? null : new ArrayList<>();
		if (!verified) {
			if (purchaseVerification) {
				lore.add(lang("gui.clandetails.verify.price.lore", getViewer(), plugin.getSettingsManager().getVerificationPrice()));
			}
			lore.add(lang("gui.clandetails.not.verified.lore", getViewer()));
		}
		SCComponent verify = new SCComponentImpl(title, lore, material, 39);
		if (!verified) {
			verify.setPermission(ClickType.LEFT, "simpleclans.leader.verify");
			verify.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "verify", false));
		}
		add(verify);
	}

	private void addBank() {
		String withdrawStatus = clan.isAllowWithdraw() ? lang("allowed",getViewer()) : lang("blocked",getViewer());
		String depositStatus = clan.isAllowDeposit() ? lang("allowed", getViewer()) : lang("blocked", getViewer());
		SCComponent bank = new SCComponentImpl(lang("gui.clandetails.bank.title", getViewer()),
				Arrays.asList(lang("gui.clandetails.bank.balance.lore", getViewer(), clan.getBalance()),
						lang("gui.clandetails.bank.withdraw.status.lore", getViewer(), withdrawStatus),
						lang("gui.clandetails.bank.deposit.status.lore", getViewer(), depositStatus),
						lang("gui.clandetails.bank.withdraw.toggle.lore", getViewer()),
						lang("gui.clandetails.bank.deposit.toggle.lore", getViewer())),
				XMaterial.GOLD_INGOT, 34);
		bank.setLorePermission(RankPermission.BANK_BALANCE);
		bank.setVerifiedOnly(ClickType.MIDDLE);
		bank.setListener(ClickType.MIDDLE, () -> InventoryController.runSubcommand(getViewer(), "toggle withdraw", true));
		bank.setConfirmationRequired(ClickType.MIDDLE);
		bank.setPermission(ClickType.MIDDLE, "simpleclans.leader.withdraw-toggle");
		bank.setVerifiedOnly(ClickType.RIGHT);
		bank.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(), "toggle deposit", true));
		bank.setPermission(ClickType.RIGHT, "simpleclans.leader.deposit-toggle");

		add(bank);
	}

	private void addFf() {
		String personalFf = cp.isFriendlyFire() ? lang("allowed",getViewer()) : lang("auto",getViewer());
		String clanFf = clan.isFriendlyFire() ? lang("allowed", getViewer()) : lang("blocked", getViewer());
		SCComponent ff = new SCComponentImpl(lang("gui.clandetails.ff.title", getViewer()),
				Arrays.asList(lang("gui.clandetails.ff.personal.lore", getViewer(), personalFf),
						lang("gui.clandetails.ff.clan.lore", getViewer(), clanFf),
						lang("gui.clandetails.ff.personal.toggle.lore", getViewer()),
						lang("gui.clandetails.ff.clan.toggle.lore", getViewer())),
				XMaterial.GOLDEN_SWORD, 32);

		ff.setListener(ClickType.LEFT, this::togglePersonalFf);
		ff.setPermission(ClickType.LEFT, "simpleclans.member.ff");
		ff.setListener(ClickType.RIGHT, this::toggleClanFf);
		ff.setPermission(ClickType.RIGHT, RankPermission.FRIENDLYFIRE);
		add(ff);
	}

	private void toggleClanFf() {
		String arg;
		if (clan.isFriendlyFire()) {
			arg = lang("block",getViewer());
		} else {
			arg = lang("allow",getViewer());
		}
		InventoryController.runSubcommand(getViewer(), String.format("clanff %s", arg), true);
	}

	private void togglePersonalFf() {
		String arg;
		if (cp.isFriendlyFire()) {
			arg = lang("auto",getViewer());
		} else {
			arg = lang("allow",getViewer());
		}
		InventoryController.runSubcommand(getViewer(), String.format("ff %s", arg), true);
	}

	private void addRegroup() {
		double price = 0;
		if (plugin.getSettingsManager().isePurchaseHomeRegroup()) {
			price = plugin.getSettingsManager().getHomeRegroupPrice();
			if (!plugin.getSettingsManager().iseUniqueTaxOnRegroup()) {
				price = price * clan.getOnlineMembers().size();
			}
		}

		List<String> lore = new ArrayList<>();
		if (price != 0) lore.add(lang("gui.clandetails.regroup.lore.price", getViewer(), price));
		lore.add(lang("gui.clandetails.regroup.lore.home", getViewer()));
		lore.add(lang("gui.clandetails.regroup.lore.me", getViewer()));

		SCComponent regroup = new SCComponentImpl(lang("gui.clandetails.regroup.title", getViewer()), lore,
				XMaterial.BEACON, 30);
		regroup.setVerifiedOnly(ClickType.LEFT);
		regroup.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "home regroup", false));
		regroup.setConfirmationRequired(ClickType.LEFT);
		regroup.setPermission(ClickType.LEFT, RankPermission.HOME_REGROUP);
		regroup.setVerifiedOnly(ClickType.RIGHT);
		regroup.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(), "home regroup me", false));
		regroup.setConfirmationRequired(ClickType.RIGHT);
		regroup.setPermission(ClickType.RIGHT, RankPermission.HOME_REGROUP);
		add(regroup);
	}

	private void addHome() {
		SettingsManager sm = plugin.getSettingsManager();
		double homePrice = sm.isePurchaseHomeTeleport() ? sm.getHomeTeleportPrice() : 0;
		double setPrice = sm.isePurchaseHomeTeleportSet() ? sm.getHomeTeleportPriceSet() : 0;

		List<String> lore = new ArrayList<>();
		if (homePrice != 0) lore.add(lang("gui.clandetails.home.lore.teleport.price", getViewer(), homePrice));
		lore.add(lang("gui.clandetails.home.lore.teleport", getViewer()));
		if (setPrice != 0) lore.add(lang("gui.clandetails.home.lore.set.price", getViewer(), setPrice));
		lore.add(lang("gui.clandetails.home.lore.set", getViewer()));
		lore.add(lang("gui.clandetails.home.lore.clear", getViewer()));

		SCComponent home = new SCComponentImpl(lang("gui.clandetails.home.title", getViewer()), lore,
				Objects.requireNonNull(XMaterial.MAGENTA_BED.parseMaterial()), 28);
		home.setVerifiedOnly(ClickType.LEFT);
		home.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "home", false));
		home.setPermission(ClickType.LEFT, RankPermission.HOME_TP);
		home.setVerifiedOnly(ClickType.RIGHT);
		home.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(), "home set", false));
		home.setPermission(ClickType.RIGHT, RankPermission.HOME_SET);
		home.setConfirmationRequired(ClickType.RIGHT);
		home.setVerifiedOnly(ClickType.MIDDLE);
		home.setListener(ClickType.MIDDLE, () -> InventoryController.runSubcommand(getViewer(), "home clear", false));
		home.setPermission(ClickType.MIDDLE, RankPermission.HOME_SET);
		home.setConfirmationRequired(ClickType.MIDDLE);
		add(home);
	}

	private void addRoster() {
		SCComponent roster = new SCComponentImpl(lang("gui.clandetails.roster.title", getViewer()),
				Collections.singletonList(lang("gui.clandetails.roster.lore", getViewer())), XMaterial.PLAYER_HEAD,
				19);
		List<ClanPlayer> members = clan.getMembers();
		if (members.size() != 0) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(
					members.get((int) (Math.random() * members.size())).getUniqueId());
			Components.setOwningPlayer(roster.getItem(), offlinePlayer);
		}
		roster.setVerifiedOnly(ClickType.LEFT);
		roster.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RosterFrame(getViewer(), this, clan)));
		roster.setPermission(ClickType.LEFT, "simpleclans.member.roster");
		add(roster);
	}

	private void addCoords() {
		SCComponent coords = new SCComponentImpl(lang("gui.clandetails.coords.title", getViewer()),
				Collections.singletonList(lang("gui.clandetails.coords.lore", getViewer())), XMaterial.COMPASS,
				21);
		coords.setVerifiedOnly(ClickType.LEFT);
		coords.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new CoordsFrame(getViewer(), this, clan)));
		coords.setPermission(ClickType.LEFT, RankPermission.COORDS);
		add(coords);
	}

	private void addAllies() {
		SCComponent allies = new SCComponentImpl(lang("gui.clandetails.allies.title", getViewer()),
				Collections.singletonList(lang("gui.clandetails.allies.lore", getViewer())), XMaterial.CYAN_BANNER,
				23);
		allies.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new AlliesFrame(getViewer(), this, clan)));
		allies.setPermission(ClickType.LEFT, "simpleclans.anyone.alliances");
		add(allies);
	}

	private void addRivals() {
		SCComponent rivals = new SCComponentImpl(lang("gui.clandetails.rivals.title", getViewer()),
				Collections.singletonList(lang("gui.clandetails.rivals.lore", getViewer())), XMaterial.RED_BANNER,
				25);
		rivals.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RivalsFrame(getViewer(), this, clan)));
		rivals.setPermission(ClickType.LEFT, "simpleclans.anyone.rivalries");
		add(rivals);
	}

	private void updateFrame() {
		InventoryDrawer.open(this);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.clandetails.title",getViewer(), Helper.stripColors(clan.getColorTag()),
				clan.getName());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

}
