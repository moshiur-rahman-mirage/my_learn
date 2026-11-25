package com.mylearn.tracker.sync;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.sync.SyncApplier.Run;
import com.mylearn.tracker.sync.SyncApplier.Winner;
import com.mylearn.tracker.sync.SyncDtos.Change;
import com.mylearn.tracker.sync.SyncDtos.Device;
import com.mylearn.tracker.sync.SyncDtos.SyncRequest;
import com.mylearn.tracker.sync.SyncDtos.SyncResponse;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Two-way sync with the phone app: applies the phone's changes, then returns this database's
 * changes since the phone's cursor (docs/SYNC_PROTOCOL.md §3). One transaction: a failure
 * applies nothing.
 */
@Service
public class PhoneSyncService {

    private static final Logger log = LoggerFactory.getLogger(PhoneSyncService.class);
    static final int PAGE_SIZE = 2000;
    static final int MAX_INCOMING = 50_000;
    /** Clock differences beyond this are treated as a wrong clock, not skew. */
    static final Duration MAX_SKEW = Duration.ofDays(2);

    private final JdbcTemplate jdbc;
    private final EntityManager em;
    private final SyncApplier applier;
    private final SyncSnapshots snapshots;

    public PhoneSyncService(JdbcTemplate jdbc, EntityManager em, SyncApplier applier, SyncSnapshots snapshots) {
        this.jdbc = jdbc;
        this.em = em;
        this.applier = applier;
        this.snapshots = snapshots;
    }

    @Transactional
    public SyncResponse sync(SyncRequest req) {
        if (req.protocol() == null || req.protocol() != SyncDtos.PROTOCOL) {
            throw new ConflictException("PROTOCOL_MISMATCH: this computer speaks sync protocol " + SyncDtos.PROTOCOL
                    + "; update the app that is older.");
        }
        if (req.deviceId() == null || req.deviceId().isBlank() || req.deviceId().length() > 100) {
            throw new BusinessRuleException("deviceId is required");
        }
        List<Change> incoming = req.changes() == null ? List.of() : req.changes();
        if (incoming.size() > MAX_INCOMING) {
            throw new BusinessRuleException("Too many changes in one sync (" + incoming.size() + "); the limit is " + MAX_INCOMING);
        }
        em.flush();
        em.clear(); // we write with JDBC; nothing cached may go stale

        Instant now = Timestamps.now();
        String epoch = epoch();
        boolean reset = req.epoch() != null && !req.epoch().equals(epoch);
        long since = reset || req.since() == null ? 0 : Math.max(0, req.since());

        long before = maxSeq();
        Run run = new Run(since, skew(req.clientNow(), now), req.deviceId(), req.deviceName());
        applier.apply(incoming, run);

        // what the phone sent is not echoed back to it ...
        jdbc.update("UPDATE sync_change SET origin = ? WHERE seq > ?", req.deviceId(), before);
        // ... and a row it won keeps the phone's time, so a later edit here is still compared fairly
        for (Winner w : run.winners) {
            jdbc.update("UPDATE sync_change SET changed_at = ? WHERE entity = ? AND key = ? AND origin = ?",
                    w.changedAt(), w.entity(), w.key(), req.deviceId());
        }
        // merged notes and repairs (renumbering, cleared completions) are new changes the phone must pull
        for (String key : run.mergedNotes) {
            jdbc.update("UPDATE sync_change SET origin = NULL WHERE entity = 'note' AND key = ?", key);
        }
        applier.repair(run);

        jdbc.update("""
                        INSERT INTO sync_device (device_id, name, last_sync_at) VALUES (?, ?, ?)
                        ON CONFLICT (device_id) DO UPDATE SET name = excluded.name, last_sync_at = excluded.last_sync_at""",
                req.deviceId(), req.deviceName(), InstantText.format(now));

        // After a reset the phone wipes its data, so "already came from this phone" no longer holds:
        // send everything, including what it pushed in this very request.
        Page page = pull(since, reset ? null : req.deviceId());
        if (!incoming.isEmpty() || !run.skipped.isEmpty()) {
            log.info("Sync with {}: applied {}, skipped {}, conflicts {}, sending {}{}", deviceLabel(req), run.applied,
                    run.skipped.size(), run.conflicts.size(), page.changes.size(), page.more ? " (more pages)" : "");
        }
        return new SyncResponse(SyncDtos.PROTOCOL, epoch, reset, page.cursor, page.more, InstantText.format(now),
                run.applied, run.skipped, run.conflicts, page.changes);
    }

    private record Page(List<Change> changes, long cursor, boolean more) {
    }

    /** @param excludeOrigin skip lines that came from this device (null = send everything) */
    private Page pull(long since, String excludeOrigin) {
        List<Map<String, Object>> lines = jdbc.queryForList("""
                SELECT seq, entity, key, changed_at, deleted FROM sync_change
                WHERE seq > ? AND (? IS NULL OR origin IS NULL OR origin <> ?)
                ORDER BY seq LIMIT ?""", since, excludeOrigin, excludeOrigin, PAGE_SIZE + 1);
        boolean more = lines.size() > PAGE_SIZE;
        if (more) {
            lines = lines.subList(0, PAGE_SIZE);
        }
        List<Change> changes = new ArrayList<>(lines.size());
        for (Map<String, Object> line : lines) {
            String entity = (String) line.get("entity");
            String key = (String) line.get("key");
            String changedAt = (String) line.get("changed_at");
            Map<String, Object> data = ((Number) line.get("deleted")).intValue() == 1 ? null : snapshots.read(entity, key);
            changes.add(new Change(entity, key, changedAt, data == null, data));
        }
        long cursor = more ? ((Number) lines.getLast().get("seq")).longValue() : Math.max(since, maxSeq());
        return new Page(changes, cursor, more);
    }

    /** Server time minus phone time, so phone timestamps compare in this computer's clock. */
    private static long skew(String clientNow, Instant now) {
        if (clientNow == null) {
            return 0;
        }
        try {
            Duration d = Duration.between(Instant.parse(clientNow), now);
            if (d.abs().compareTo(MAX_SKEW) > 0) {
                throw new BusinessRuleException("The phone's clock is more than 2 days off; fix its date and time, then sync again.");
            }
            return d.toMillis();
        } catch (DateTimeParseException e) {
            throw new BusinessRuleException("clientNow is not a timestamp: " + clientNow);
        }
    }

    public String epoch() {
        return jdbc.queryForObject("SELECT value FROM app_meta WHERE key = 'sync_epoch'", String.class);
    }

    private long maxSeq() {
        Long seq = jdbc.queryForObject("SELECT COALESCE(MAX(seq), 0) FROM sync_change", Long.class);
        return seq == null ? 0 : seq;
    }

    @Transactional(readOnly = true)
    public List<Device> devices() {
        return jdbc.query("SELECT device_id, name, last_sync_at FROM sync_device ORDER BY last_sync_at DESC",
                (rs, n) -> new Device(rs.getString("device_id"), rs.getString("name"), rs.getString("last_sync_at")));
    }

    private static String deviceLabel(SyncRequest req) {
        return req.deviceName() == null ? req.deviceId() : req.deviceName();
    }
}
