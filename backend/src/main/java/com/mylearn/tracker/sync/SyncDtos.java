package com.mylearn.tracker.sync;

import java.util.List;
import java.util.Map;

/** Request and response bodies of {@code POST /api/sync} (docs/SYNC_PROTOCOL.md §2). */
public final class SyncDtos {

    public static final int PROTOCOL = 1;

    private SyncDtos() {
    }

    public record SyncRequest(
            Integer protocol,
            String deviceId,
            String deviceName,
            String epoch,
            Long since,
            String clientNow,
            List<Change> changes) {
    }

    /** One changed row; {@code data} is null when {@code deleted}. */
    public record Change(String entity, String key, String changedAt, boolean deleted, Map<String, Object> data) {
    }

    public record SyncResponse(
            int protocol,
            String epoch,
            boolean reset,
            long cursor,
            boolean more,
            String serverNow,
            int applied,
            List<Skipped> skipped,
            List<Conflict> conflicts,
            List<Change> changes) {
    }

    public record Skipped(String entity, String key, String reason) {
    }

    public record Conflict(String entity, String key, String resolution) {
    }

    public record Ping(String app, int protocol, String epoch) {
    }

    public record Device(String deviceId, String name, String lastSyncAt) {
    }

    /** What the desktop Settings page shows to pair a phone; only served to this computer. */
    public record Pairing(String token, int port, List<String> addresses, List<Device> devices) {
    }
}
