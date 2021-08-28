
package net.sacredlabyrinth.phaed.simpleclans;

import java.util.Date;

/**
 * @author phaed
 * @deprecated No usage
 */
@Deprecated
public class DamageRecord {
    private String victim;
    private String attacker;
    private Date time;

    /**
     * @param victim
     * @param attacker
     */
    public DamageRecord(String victim, String attacker) {
        this.victim = victim;
        this.attacker = attacker;
        this.time = new Date();
    }

    /**
     * @return the victim
     */
    public String getVictim() {
        return victim;
    }

    /**
     * @param victim the victim to set
     */
    public void setVictim(String victim) {
        this.victim = victim;
    }

    /**
     * @return the attacker
     */
    public String getAttacker() {
        return attacker;
    }

    /**
     * @param attacker the attacker to set
     */
    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return new Date(time.getTime());
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = new Date(time.getTime());
    }
}
