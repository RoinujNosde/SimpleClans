package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

		add(Components.getBackComponent(getParent(), 4));
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
		String joined = lang("chat.joined");
		String notJoined = lang("chat.not.joined");

		String clanStatus = clanEnabled ? joined : notJoined;
		String allyStatus = allyEnabled ? joined : notJoined;

		SettingsManager sm = plugin.getSettingsManager();
		String chatCommand = sm.isTagBasedClanChat() ? clan.getTag() : sm.getCommandClanChat();
		String joinArg = lang("join");
		String leaveArg = lang("leave");
		return new SCComponentImpl(lang("gui.clandetails.chat.title"),
				Arrays.asList(
						lang("gui.clandetails.chat.clan.chat.lore", chatCommand),
						lang("gui.clandetails.chat.clan.join.leave.lore", chatCommand, joinArg, leaveArg),
						lang("gui.clandetails.chat.ally.chat.lore", sm.getCommandAlly()),
						lang("gui.clandetails.chat.ally.join.leave.lore", sm.getCommandAlly(), joinArg, leaveArg),
						lang("gui.clandetails.chat.clan.status.lore", clanStatus),
						lang("gui.clandetails.chat.ally.status.lore", allyStatus),
						lang("gui.clandetails.chat.clan.toggle.lore"),
						lang("gui.clandetails.chat.ally.toggle.lore")),
				Material.KNOWLEDGE_BOOK, 43);
	}

	private void addRank() {
		SCComponent rank = new SCComponentImpl(lang("gui.clandetails.rank.title"),
				Collections.singletonList(lang("gui.clandetails.rank.lore")), Material.IRON_HELMET, 37);
		rank.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RanksFrame(this, getViewer(), clan, null)));
		rank.setPermission(ClickType.LEFT, "simpleclans.leader.rank.list");
		add(rank);
	}

	private void addFee() {
		String status = clan.isMemberFeeEnabled() ? lang("fee.enabled") : lang("fee.disabled");
		SCComponent fee = new SCComponentImpl(lang("gui.clandetails.fee.title"),
				Arrays.asList(lang("gui.clandetails.fee.value.lore", clan.getMemberFee()),
						lang("gui.clandetails.fee.status.lore", status),
						lang("gui.clandetails.fee.toggle.lore")),
				Material.GOLD_NUGGET, 41);
		fee.setVerifiedOnly(ClickType.LEFT);
		fee.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "toggle fee", true));
		fee.setPermission(ClickType.LEFT, RankPermission.FEE_ENABLE);
		add(fee);
	}

	private void addDisband() {
		SCComponent disband = new SCComponentImpl(lang("gui.clandetails.disband.title"),
				Collections.singletonList(lang("gui.clandetails.disband.lore")), Material.BARRIER, 50);
		disband.setListener(ClickType.MIDDLE, () -> InventoryController.runSubcommand(getViewer(), "disband", false));
		disband.setPermission(ClickType.MIDDLE, "simpleclans.leader.disband");
		add(disband);
	}

	private void addResign() {
		SCComponent resign = new SCComponentImpl(lang("gui.clandetails.resign.title"),
				Collections.singletonList(lang("gui.clandetails.resign.lore")), Material.IRON_DOOR, 48);
		resign.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "resign", false));
		resign.setPermission(ClickType.LEFT, "simpleclans.member.resign");
		add(resign);
	}

	private void addVerify() {
		boolean verified = clan.isVerified();
		Material material = verified ? Material.REDSTONE_TORCH : Material.LEVER;
		String title = verified ? lang("gui.clandetails.verified.title")
				: lang("gui.clandetails.not.verified.title");
		List<String> lore = verified ? null : Collections.singletonList(lang("gui.clandetails.not.verified.lore"));
		SCComponent verify = new SCComponentImpl(title, lore, material, 39);
		if (!verified) {
			verify.setPermission(ClickType.LEFT, "simpleclans.leader.verify");
			verify.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "verify", false));
		}
		add(verify);
	}

	private void addBank() {
		String withdrawStatus = clan.isAllowWithdraw() ? lang("allowed") : lang("blocked");
		String depositStatus = clan.isAllowDeposit() ? lang("allowed") : lang("blocked");
		SCComponent bank = new SCComponentImpl(lang("gui.clandetails.bank.title"),
				Arrays.asList(lang("gui.clandetails.bank.balance.lore", clan.getBalance()),
						lang("gui.clandetails.bank.withdraw.status.lore", withdrawStatus),
						lang("gui.clandetails.bank.deposit.status.lore", depositStatus),
						lang("gui.clandetails.bank.withdraw.toggle.lore"),
						lang("gui.clandetails.bank.deposit.toggle.lore")),
				Material.GOLD_INGOT, 34);
		bank.setLorePermission(RankPermission.BANK_BALANCE);
		bank.setVerifiedOnly(ClickType.MIDDLE);
		bank.setListener(ClickType.MIDDLE, () -> InventoryController.runSubcommand(getViewer(), "toggle withdraw", true));
		bank.setPermission(ClickType.MIDDLE, "simpleclans.leader.withdraw-toggle");
		bank.setVerifiedOnly(ClickType.RIGHT);
		bank.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(), "toggle deposit", true));
		bank.setPermission(ClickType.RIGHT, "simpleclans.leader.deposit-toggle");

		add(bank);
	}

	private void addFf() {
		String personalFf = cp.isFriendlyFire() ? lang("allowed") : lang("auto");
		String clanFf = clan.isFriendlyFire() ? lang("allowed") : lang("blocked");
		SCComponent ff = new SCComponentImpl(lang("gui.clandetails.ff.title"),
				Arrays.asList(lang("gui.clandetails.ff.personal.lore", personalFf),
						lang("gui.clandetails.ff.clan.lore", clanFf),
						lang("gui.clandetails.ff.personal.toggle.lore"),
						lang("gui.clandetails.ff.clan.toggle.lore")),
				Material.GOLDEN_SWORD, 32);

		ff.setListener(ClickType.LEFT, this::togglePersonalFf);
		ff.setPermission(ClickType.LEFT, "simpleclans.member.ff");
		ff.setListener(ClickType.RIGHT, this::toggleClanFf);
		ff.setPermission(ClickType.RIGHT, RankPermission.FRIENDLYFIRE);
		add(ff);
	}

	private void toggleClanFf() {
		String arg;
		if (clan.isFriendlyFire()) {
			arg = lang("block");
		} else {
			arg = lang("allow");
		}
		InventoryController.runSubcommand(getViewer(), String.format("clanff %s", arg), true);
	}

	private void togglePersonalFf() {
		String arg;
		if (cp.isFriendlyFire()) {
			arg = lang("auto");
		} else {
			arg = lang("allow");
		}
		InventoryController.runSubcommand(getViewer(), String.format("ff %s", arg), true);
	}

	private void addRegroup() {
		SCComponent regroup = new SCComponentImpl(lang("gui.clandetails.regroup.title"),
				Arrays.asList(lang("gui.clandetails.regroup.lore.home"),
						lang("gui.clandetails.regroup.lore.me")),
				Material.BEACON, 30);
		regroup.setVerifiedOnly(ClickType.LEFT);
		regroup.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "home regroup", false));
		regroup.setPermission(ClickType.LEFT, RankPermission.HOME_REGROUP);
		regroup.setVerifiedOnly(ClickType.RIGHT);
		regroup.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(), "home regroup me", false));
		regroup.setPermission(ClickType.RIGHT, RankPermission.HOME_REGROUP);
		add(regroup);
	}

	private void addHome() {
		SCComponent home = new SCComponentImpl(lang("gui.clandetails.home.title"),
				Arrays.asList(lang("gui.clandetails.home.lore.teleport"),
						lang("gui.clandetails.home.lore.set"),
						lang("gui.clandetails.home.lore.clear")),
				Material.MAGENTA_BED, 28);
		home.setVerifiedOnly(ClickType.LEFT);
		home.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "home", false));
		home.setPermission(ClickType.LEFT, RankPermission.HOME_TP);
		home.setVerifiedOnly(ClickType.RIGHT);
		home.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(), "home set", false));
		home.setPermission(ClickType.RIGHT, RankPermission.HOME_SET);
		home.setVerifiedOnly(ClickType.MIDDLE);
		home.setListener(ClickType.MIDDLE, () -> InventoryController.runSubcommand(getViewer(), "home clear", false));
		home.setPermission(ClickType.MIDDLE, RankPermission.HOME_SET);
		add(home);
	}

	private void addRoster() {
		SCComponent roster = new SCComponentImpl(lang("gui.clandetails.roster.title"),
				Collections.singletonList(lang("gui.clandetails.roster.lore")), Material.PLAYER_HEAD, 19);
		if (roster.getItemMeta() != null) {
			SkullMeta itemMeta = (SkullMeta) roster.getItemMeta();
			List<ClanPlayer> members = clan.getMembers();
			itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(
					members.get((int) (Math.random() * members.size())).getUniqueId()));
			roster.setItemMeta(itemMeta);
		}
		roster.setVerifiedOnly(ClickType.LEFT);
		roster.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RosterFrame(getViewer(), this, clan)));
		roster.setPermission(ClickType.LEFT, "simpleclans.member.roster");
		add(roster);
	}

	private void addCoords() {
		SCComponent coords = new SCComponentImpl(lang("gui.clandetails.coords.title"),
				Collections.singletonList(lang("gui.clandetails.coords.lore")), Material.COMPASS, 21);
		coords.setVerifiedOnly(ClickType.LEFT);
		coords.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new CoordsFrame(getViewer(), this, clan)));
		coords.setPermission(ClickType.LEFT, RankPermission.COORDS);
		add(coords);
	}

	private void addAllies() {
		SCComponent allies = new SCComponentImpl(lang("gui.clandetails.allies.title"),
				Collections.singletonList(lang("gui.clandetails.allies.lore")), Material.CYAN_BANNER, 23);
		allies.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new AlliesFrame(getViewer(), this, clan)));
		allies.setPermission(ClickType.LEFT, "simpleclans.anyone.alliances");
		add(allies);
	}

	private void addRivals() {
		SCComponent rivals = new SCComponentImpl(lang("gui.clandetails.rivals.title"),
				Collections.singletonList(lang("gui.clandetails.rivals.lore")), Material.RED_BANNER, 25);
		rivals.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RivalsFrame(getViewer(), this, clan)));
		rivals.setPermission(ClickType.LEFT, "simpleclans.anyone.rivalries");
		add(rivals);
	}

	private void updateFrame() {
		InventoryDrawer.update(this);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.clandetails.title", Helper.stripColors(clan.getColorTag()),
				clan.getName());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

}
