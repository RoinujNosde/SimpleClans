package net.sacredlabyrinth.phaed.simpleclans.chest;

import java.util.UUID;

public class LockResult {
    private final LockStatus status;
    private final UUID lockedBy;
    private final String serverName;

    public LockResult(LockStatus status) {
        this.status = status;
        this.lockedBy = null;
        this.serverName = null;
    }

    public LockResult(LockStatus status, UUID lockedBy, String serverName) {
        this.status = status;
        this.lockedBy = lockedBy;
        this.serverName = serverName;
    }

    public LockStatus getStatus() {
        return status;
    }

    public UUID getLockedBy() {
        return lockedBy;
    }

    public String getServerName() {
        return serverName;
    }
}
