package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class PlayerDetailsFrame extends SCFrame {

	private final SimpleClans plugin = SimpleClans.getInstance();
	private final OfflinePlayer subject;
	private final Clan clan;

	public PlayerDetailsFrame(Player viewer, SCFrame parent, OfflinePlayer subject) {
		super(parent, viewer);
		this.subject = subject;
		clan = plugin.getClanManager().getAnyClanPlayer(viewer.getUniqueId()).getClan();
	}

	@Override
	public void createComponents() {
		String subjectName = subject.getName();
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 4)
				continue;
			add(Components.getPanelComponent(slot));
		}

		add(Components.getBackComponent(getParent(), 4, getViewer()));
		add(Components.getPlayerComponent(this, getViewer(), subject, 13, false));

		if (!isSameClan()) {
			return;
		}
		
		SCComponent kick = new SCComponentImpl(lang("gui.playerdetails.kick.title",getViewer()), null, Material.RED_WOOL,
				28);
		kick.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "kick " + subjectName, true));
		kick.setPermission(ClickType.LEFT, RankPermission.KICK);
		add(kick);

		SCComponent promoteDemote = new SCComponentImpl(lang("gui.playerdetails.promote.demote.title",getViewer()),
				Arrays.asList(lang("gui.playerdetails.promote.lore.left.click",getViewer()),
						lang("gui.playerdetails.demote.lore.right.click",getViewer())),
				Material.GUNPOWDER, 30);
		promoteDemote.setListener(ClickType.LEFT,
				() -> InventoryController.runSubcommand(getViewer(), "promote " + subjectName, !plugin.getSettingsManager().isConfirmationForPromote()));
		promoteDemote.setPermission(ClickType.LEFT, "simpleclans.leader.promote");
		promoteDemote.setListener(ClickType.RIGHT,
				() -> InventoryController.runSubcommand(getViewer(), "demote " + subjectName, !plugin.getSettingsManager().isConfirmationForDemote()));
		add(promoteDemote);
		promoteDemote.setPermission(ClickType.RIGHT, "simpleclans.leader.demote");

		SCComponentImpl assignUnassign = new SCComponentImpl(lang("gui.playerdetails.assign.unassign.title",getViewer()),
				Arrays.asList(lang("gui.playerdetails.assign.lore.left.click",getViewer()),
						lang("gui.playerdetails.unassign.lore.right.click",getViewer())),
				Material.FEATHER, 32);
		assignUnassign.setListener(ClickType.RIGHT,
				() -> InventoryController.runSubcommand(getViewer(), "rank unassign " + subjectName, true));
		assignUnassign.setPermission(ClickType.RIGHT, "simpleclans.leader.rank.unassign");
		assignUnassign.setListener(ClickType.LEFT,
				() -> InventoryDrawer.open(new RanksFrame(this, getViewer(), clan, subject)));
		add(assignUnassign);
		assignUnassign.setPermission(ClickType.LEFT, "simpleclans.leader.rank.assign");

		SCComponent trustUntrust = new SCComponentImpl(lang("gui.playerdetails.trust.untrust.title",getViewer()),
				Arrays.asList(lang("gui.playerdetails.trust.lore.left.click",getViewer()),
						lang("gui.playerdetails.untrust.lore.right.click",getViewer())),
				Material.CYAN_DYE, 34);
		trustUntrust.setListener(ClickType.LEFT,
				() -> InventoryController.runSubcommand(getViewer(), "trust " + subjectName, true));
		trustUntrust.setPermission(ClickType.LEFT, "simpleclans.leader.settrust");
		trustUntrust.setListener(ClickType.RIGHT,
				() -> InventoryController.runSubcommand(getViewer(), "untrust " + subjectName, true));
		trustUntrust.setPermission(ClickType.RIGHT, "simpleclans.leader.settrust");
		add(trustUntrust);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.playerdetails.title",getViewer(), subject.getName());
	}

	@Override
	public int getSize() {
		int size = 3;
		if (isSameClan()) {
			size = 5;
		}
		return size * 9;
	}

	private boolean isSameClan() {
		return clan != null && clan.isMember(subject.getUniqueId());
	}

}
