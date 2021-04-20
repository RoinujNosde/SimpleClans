package net.sacredlabyrinth.phaed.simpleclans;

import java.time.LocalDateTime;

public class Kill {

	private final ClanPlayer killer;
	private final ClanPlayer victim;
	private final LocalDateTime time;
	
	public Kill(ClanPlayer killer, ClanPlayer victim, LocalDateTime time) {
		this.killer = killer;
		this.victim = victim;
		this.time = time;
	}

	public ClanPlayer getKiller() {
		return killer;
	}

	public ClanPlayer getVictim() {
		return victim;
	}

	public LocalDateTime getTime() {
		return time;
	}
	
	public enum Type {
		CIVILIAN("c"), RIVAL("r"), NEUTRAL("n"), ALLY("a");
		
		Type(String shortName) {
			this.shortName = shortName;
		}
		
		private final String shortName;
		
		public String getShortname() {
			return shortName;
		}
	}
}
