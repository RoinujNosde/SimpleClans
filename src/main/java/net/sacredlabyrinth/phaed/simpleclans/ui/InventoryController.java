package net.sacredlabyrinth.phaed.simpleclans.ui;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.ConfirmationFrame;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.WarningFrame;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * 
 * @author RoinujNosde
 *
 */
public class InventoryController implements Listener {
	private static final Map<UUID, SCFrame> frames = new HashMap<>();

	@EventHandler(ignoreCancelled = true)
	public void onClose(InventoryCloseEvent event) {
		HumanEntity entity = event.getPlayer();
		if (!(entity instanceof Player)) {
			return;
		}
		
		frames.remove(entity.getUniqueId());
	}

	@EventHandler(ignoreCancelled = true)
	public void onInteract(InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();
		if (!(entity instanceof Player)) {
			return;
		}

		SCFrame frame = frames.get(entity.getUniqueId());
		if (frame == null) {
			return;
		}
		
		event.setCancelled(true);
		
		if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
			return;
		}
		
		SCComponent component = frame.getComponent(event.getSlot());
		if (component == null) {
			return;
		}

		ClickType click = event.getClick();
		Runnable listener = component.getListener(click);
		if (listener == null) {
			return;
		}

		if (component.isVerifiedOnly(click) && !isClanVerified((Player) entity)) {
			InventoryDrawer.open(new WarningFrame(frame, (Player) entity, null));
			return;
		}
		
		Object permission = component.getPermission(click);
		if (permission != null) {
			if (!hasPermission((Player) entity, permission)) {
				InventoryDrawer.open(new WarningFrame(frame, (Player) entity, permission));
				return;
			}
		}

		if (component.isConfirmationRequired(click)) {
			listener = () -> InventoryDrawer.open(new ConfirmationFrame(frame, frame.getViewer(), component.getListener(click)));
		}

		Runnable finalListener = listener;
		Bukkit.getScheduler().runTask(SimpleClans.getInstance(), () -> {
			ItemStack currentItem = event.getCurrentItem();
			if (currentItem == null) return;

			ItemMeta itemMeta = currentItem.getItemMeta();
			Objects.requireNonNull(itemMeta).setLore(Collections.singletonList(lang("gui.loading", (Player) entity)));
			currentItem.setItemMeta(itemMeta);

			finalListener.run();
		});
	}


	/**
	 * Checks if the Player's Clan is verified
	 * @param player the Player
	 * @return if the Clan is verified
	 */
	private boolean isClanVerified(@NotNull Player player) {
		SimpleClans plugin = SimpleClans.getInstance();
		ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());

		return cp != null && cp.getClan() != null && cp.getClan().isVerified();
	}

	/**
	 * Checks if the player has the permission
	 * 
	 * @param player the Player
	 * @param permission the permission
	 * @return true if they have permission
	 *
	 * @author RoinujNosde
	 */
	private boolean hasPermission(@NotNull Player player, @NotNull Object permission) {
		SimpleClans plugin = SimpleClans.getInstance();
		PermissionsManager pm = plugin.getPermissionsManager();
		if (permission instanceof String) {
			String permS = (String) permission;
			boolean leaderPerm = permS.contains("simpleclans.leader") && !permS.equalsIgnoreCase("simpleclans.leader.create");
			ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());

			return pm.has(player, permS) && (!leaderPerm || (cp != null && cp.isLeader()));
		}
		return pm.has(player, (RankPermission) permission, false);
	}

	/**
	 * Registers the frame in the InventoryController
	 * @param frame the frame
	 *
	 * @author RoinujNosde
	 */
	public static void register(@NotNull SCFrame frame) {
		frames.put(frame.getViewer().getUniqueId(), frame);
	}

	/**
	 * Checks if the Player is registered
	 *
	 * @param player the Player
	 * @return if they are registered
	 */
	public static boolean isRegistered(@NotNull Player player) {
		return frames.containsKey(player.getUniqueId());
	}

	/**
	 * Runs a subcommand for the Player
	 * @param player the Player
	 * @param subcommand the subcommand
	 * @param update whether to update the inventory instead of closing
	 *
	 * @author RoinujNosde
	 */
	public static void runSubcommand(@NotNull Player player, @NotNull String subcommand, boolean update, String... args) {
		SimpleClans plugin = SimpleClans.getInstance();
		String baseCommand = plugin.getSettingsManager().getCommandClan();
		subcommand = plugin.getCommandManager().getCommandReplacements().replace(subcommand);
		String finalCommand = String.format("%s %s ", baseCommand, subcommand) + String.join(" ", args);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.performCommand(finalCommand);
				if (!update) {
					player.closeInventory();
				} else {
					SCFrame currentFrame = frames.get(player.getUniqueId());
					if (currentFrame instanceof ConfirmationFrame) {
						currentFrame = currentFrame.getParent();
					}
					InventoryDrawer.open(currentFrame);
				}
			}
		}.runTask(plugin);
	}
}
