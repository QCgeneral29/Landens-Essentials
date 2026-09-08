package com.landen.essentials.teleport;

import java.util.UUID;

public class TeleportRequest {

    public enum Type {
        TPA,
        TPA_HERE
    }

    private final UUID requesterUuid;
    private final UUID targetUuid;
    private final Type type;
    private final long timestamp;

    public TeleportRequest(UUID requesterUuid, UUID targetUuid, Type type) {
        this.requesterUuid = requesterUuid;
        this.targetUuid = targetUuid;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getRequesterUuid() {
        return requesterUuid;
    }

    public UUID getTargetUuid() {
        return targetUuid;
    }

    public Type getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired(long timeoutMillis) {
        return System.currentTimeMillis() - timestamp > timeoutMillis;
    }
}
