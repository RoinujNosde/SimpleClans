package net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.Components;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.RosterFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ClanDetailsFrame extends SCFrame {
    private final Clan clan;

    public ClanDetailsFrame(@Nullable SCFrame parent, @NotNull Player viewer, @NotNull Clan clan) {
        super(parent, viewer);
        this.clan = clan;
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
        addHome();
        addBank();
        addVerify();
        addDisband();
    }

    private void addDisband() {
        SCComponent disband = new SCComponentImpl(lang("gui.clandetails.disband.title", getViewer()),
                Collections.singletonList(lang("gui.staffclandetails.disband.lore", getViewer())),
                XMaterial.BARRIER, 34);
        disband.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
                "mod disband", false, clan.getTag()));
        disband.setConfirmationRequired(ClickType.LEFT);
        disband.setPermission(ClickType.LEFT, "simpleclans.mod.disband");
        add(disband);
    }

    private void addVerify() {
        boolean verified = clan.isVerified();

        XMaterial material = verified ? XMaterial.REDSTONE_TORCH : XMaterial.LEVER;
        String title = verified ? lang("gui.clandetails.verified.title", getViewer())
                : lang("gui.clandetails.not.verified.title", getViewer());
        List<String> lore = verified ? null : new ArrayList<>();
        if (!verified) {
            lore.add(lang("gui.staffclandetails.not.verified.lore", getViewer()));
        }
        SCComponent verify = new SCComponentImpl(title, lore, material, 32);
        if (!verified) {
            verify.setPermission(ClickType.LEFT, "simpleclans.mod.verify");
            verify.setConfirmationRequired(ClickType.LEFT);
            verify.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
                    "mod verify", false, clan.getTag()));
        }
        add(verify);
    }

    private void addHome() {
        List<String> lore = new ArrayList<>();
        lore.add(lang("gui.staffclandetails.home.lore.teleport", getViewer()));
        lore.add(lang("gui.staffclandetails.home.lore.set", getViewer()));

        SCComponent home = new SCComponentImpl(lang("gui.clandetails.home.title", getViewer()), lore,
                XMaterial.MAGENTA_BED, 30);
        home.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
                "mod home tp", false, clan.getTag()));
        home.setPermission(ClickType.LEFT, "simpleclans.mod.hometp");
        home.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(),
                "mod home set", false, clan.getTag()));
        home.setPermission(ClickType.RIGHT, "simpleclans.mod.home");
        home.setConfirmationRequired(ClickType.RIGHT);
        add(home);
    }

    private void addRoster() {
        SCComponent roster = new SCComponentImpl(lang("gui.clandetails.roster.title", getViewer()),
                Collections.singletonList(lang("gui.staffclandetails.roster.lore", getViewer())),
                XMaterial.PLAYER_HEAD, 28);

        List<ClanPlayer> members = clan.getMembers();
        if (members.size() != 0) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(
                    members.get((int) (Math.random() * members.size())).getUniqueId());
            Components.setOwningPlayer(roster.getItem(), offlinePlayer);
        }

        roster.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RosterFrame(getViewer(), this, clan, true)));
        add(roster);
    }

    private void addBank() {
        List<String> lore = Collections.singletonList(lang("gui.clandetails.bank.balance.lore", getViewer(), clan.getBalance()));

        SCComponent bank = new SCComponentImpl(lang("gui.clandetails.bank.title", getViewer()), lore, XMaterial.GOLD_INGOT, 40);
        add(bank);
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.clandetails.title", getViewer(), ChatUtils.stripColors(clan.getColorTag()),
                clan.getName());
    }

    @Override
    public int getSize() {
        return 5 * 9;
    }

}
