package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.wrappers;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SCGuild {

    private final Guild guild;

    public SCGuild(@NotNull Guild guild) {
        this.guild = guild;
    }

    @Nullable
    public Guild getGuild() {
        return guild;
    }

    public Member getMember(User user) {
        return Objects.requireNonNull(getGuild()).getMember(user);
    }
}
