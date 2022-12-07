package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.wrappers;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

public class SCRole {

    private final Role role;

    public SCRole(@NotNull Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
