package net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.Components;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff.ClanListFrame.Type;

public class PlayerDetailsFrame extends SCFrame {

	private final OfflinePlayer subject;
	private final String subjectName;

	public PlayerDetailsFrame(@NotNull Player viewer, SCFrame parent, @NotNull OfflinePlayer subject) {
		super(parent, viewer);
		this.subject = subject;
		subjectName = subject.getName();
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 4)
				continue;
			add(Components.getPanelComponent(slot));
		}

		add(Components.getBackComponent(getParent(), 4, getViewer()));
		add(Components.getPlayerComponent(this, getViewer(), subject, 13, false));

		addBanUnban();
		addPlace();
		addResetKDR();
		addPurge();
		addPromoteDemote();
	}

	private void addPromoteDemote() {
		SCComponent promoteDemote = new SCComponentImpl(lang("gui.playerdetails.promote.demote.title",getViewer()),
				Arrays.asList(lang("gui.playerdetails.promote.lore.left.click", getViewer()),
						lang("gui.playerdetails.demote.lore.right.click", getViewer())),
				XMaterial.GUNPOWDER, 28);
		promoteDemote.setConfirmationRequired(ClickType.LEFT);
		promoteDemote.setListener(ClickType.LEFT,
				() -> InventoryController.runSubcommand(getViewer(), "admin promote", true, subjectName));
		promoteDemote.setPermission(ClickType.LEFT, "simpleclans.admin.promote");
		promoteDemote.setListener(ClickType.RIGHT,
				() -> InventoryController.runSubcommand(getViewer(), "admin demote", true, subjectName));
		promoteDemote.setConfirmationRequired(ClickType.RIGHT);
		add(promoteDemote);
		promoteDemote.setPermission(ClickType.RIGHT, "simpleclans.admin.demote");
	}

	private void addPurge() {
		SCComponent purge = new SCComponentImpl.Builder(XMaterial.LAVA_BUCKET).withSlot(34).withDisplayName(
				lang("gui.playerdetails.purge.title", getViewer())).withLoreLine(
				lang("gui.playerdetails.purge.lore", getViewer())).build();
		purge.setConfirmationRequired(ClickType.LEFT);
		purge.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
				"purge", false, subjectName));
		purge.setPermission(ClickType.LEFT, "simpleclans.admin.purge");
		add(purge);
	}

	private void addResetKDR() {
		SCComponent resetKdr = new SCComponentImpl.Builder(XMaterial.ANVIL)
				.withSlot(30).withDisplayName(lang("gui.main.reset.kdr.title", getViewer()))
				.withLoreLine(lang("gui.playerdetails.resetkdr.lore", getViewer())).build();
		resetKdr.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
				"resetkdr", false, subjectName));
		resetKdr.setConfirmationRequired(ClickType.LEFT);
		resetKdr.setPermission(ClickType.LEFT, "simpleclans.admin.resetkdr");
		add(resetKdr);
	}

	private void addPlace() {
		SCComponent place = new SCComponentImpl.Builder(XMaterial.MINECART).withSlot(32)
				.withDisplayName(lang("gui.playerdetails.place.title", getViewer()))
				.withLoreLine(lang("gui.playerdetails.place.lore", getViewer())).build();
		place.setPermission(ClickType.LEFT, "simpleclans.mod.place");
		place.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer(),
				Type.PLACE, subject)));
		add(place);
	}

	private void addBanUnban() {
		SCComponent banUnban = new SCComponentImpl.Builder(XMaterial.BARRIER).withSlot(40)
				.withDisplayName(lang("gui.playerdetails.ban.unban.title", getViewer()))
				.withLore(Arrays.asList(lang("gui.playerdetails.ban.left.click", getViewer()),
						lang("gui.playerdetails.unban.right.click", getViewer()))).build();
		banUnban.setConfirmationRequired(ClickType.LEFT);
		banUnban.setConfirmationRequired(ClickType.RIGHT);
		banUnban.setPermission(ClickType.LEFT, "simpleclans.mod.ban");
		banUnban.setPermission(ClickType.RIGHT, "simpleclans.mod.ban");
		banUnban.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
				"ban", false, subjectName));
		banUnban.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(),
				"unban", false, subjectName));
		add(banUnban);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.playerdetails.title", getViewer(), subject.getName());
	}

	@Override
	public int getSize() {
		return 5 * 9;
	}

}
