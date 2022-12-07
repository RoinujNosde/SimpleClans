package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.wrappers;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class SCMember {

    public Member getMember() {
        return member;
    }

    private final Member member;

    public SCMember(@NotNull Member member) {
        this.member = member;
    }
}
