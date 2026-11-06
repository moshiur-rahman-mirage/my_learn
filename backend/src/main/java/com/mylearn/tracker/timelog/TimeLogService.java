package com.mylearn.tracker.timelog;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.plan.PlanService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

/** Time spent learning, logged per item ("+25 min" buttons and finished Pomodoros). */
@Service
@Transactional(readOnly = true)
public class TimeLogService {

    static final int MAX_MINUTES = 1440;

    private final JdbcTemplate jdbc;
    private final LearningItemRepository items;
    private final PlanService plans;

    public TimeLogService(JdbcTemplate jdbc, LearningItemRepository items, PlanService plans) {
        this.jdbc = jdbc;
        this.items = items;
        this.plans = plans;
    }

    public ItemTime forItem(Long itemId) {
        requireItem(itemId);
        List<TimeEntry> entries = jdbc.query(
                "SELECT id, minutes, logged_at FROM time_log WHERE item_id = ? ORDER BY logged_at DESC, id DESC",
                (rs, n) -> new TimeEntry(rs.getLong("id"), rs.getInt("minutes"), Instant.parse(rs.getString("logged_at"))),
                itemId);
        return new ItemTime(itemId, entries.stream().mapToInt(TimeEntry::minutes).sum(), entries);
    }

    @Transactional
    public TimeEntry log(Long itemId, int minutes) {
        if (minutes < 1 || minutes > MAX_MINUTES) {
            throw new BusinessRuleException("Minutes must be between 1 and " + MAX_MINUTES);
        }
        LearningItem item = requireItem(itemId);
        Instant now = Timestamps.now();
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.update(c -> {
            PreparedStatement ps = c.prepareStatement("INSERT INTO time_log (item_id, minutes, logged_at) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, itemId);
            ps.setInt(2, minutes);
            ps.setString(3, InstantText.format(now));
            return ps;
        }, key);
        plans.touch(item.getPlanId());
        return new TimeEntry(key.getKey().longValue(), minutes, now);
    }

    @Transactional
    public void delete(Long entryId) {
        if (jdbc.update("DELETE FROM time_log WHERE id = ?", entryId) == 0) {
            throw new NotFoundException("Time entry", entryId);
        }
    }

    /** Minutes logged since {@code since}, in all plans. */
    public int minutesSince(Instant since) {
        Integer total = jdbc.queryForObject("SELECT COALESCE(SUM(minutes), 0) FROM time_log WHERE logged_at >= ?",
                Integer.class, InstantText.format(since));
        return total == null ? 0 : total;
    }

    private LearningItem requireItem(Long itemId) {
        return items.findById(itemId).orElseThrow(() -> new NotFoundException("Item", itemId));
    }

    public record TimeEntry(Long id, int minutes, Instant loggedAt) {
    }

    /** @param totalMinutes time logged on this item itself (not its sub-items) */
    public record ItemTime(Long itemId, int totalMinutes, List<TimeEntry> entries) {
    }
}
