package com.mylearn.tracker.tag;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.plan.PlanService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Labels shared by all plans. A tag exists while at least one item carries it: tags are created
 * by typing a new name and disappear when the last item loses them.
 */
@Service
@Transactional(readOnly = true)
public class TagService {

    static final int MAX_TAGS_PER_ITEM = 20;
    static final int MAX_TAG_LENGTH = 40;

    private final JdbcTemplate jdbc;
    private final LearningItemRepository items;
    private final PlanService plans;

    public TagService(JdbcTemplate jdbc, LearningItemRepository items, PlanService plans) {
        this.jdbc = jdbc;
        this.items = items;
        this.plans = plans;
    }

    /** Every tag with the number of items carrying it, alphabetical. */
    public List<TagCount> all() {
        return jdbc.query("""
                        SELECT t.name, COUNT(it.item_id) AS n FROM tag t
                        JOIN item_tag it ON it.tag_id = t.id
                        GROUP BY t.id ORDER BY lower(t.name)""",
                (rs, n) -> new TagCount(rs.getString("name"), rs.getInt("n")));
    }

    /** Replaces the item's tags; new names create tags, an existing name is matched ignoring case. */
    @Transactional
    public List<String> setTags(Long itemId, Collection<String> names) {
        LearningItem item = items.findById(itemId).orElseThrow(() -> new NotFoundException("Item", itemId));
        List<String> clean = normalize(names);
        jdbc.update("DELETE FROM item_tag WHERE item_id = ?", itemId);
        List<String> stored = new ArrayList<>();
        for (String name : clean) {
            List<Map<String, Object>> found = jdbc.queryForList("SELECT id, name FROM tag WHERE name = ? COLLATE NOCASE", name);
            long tagId;
            if (found.isEmpty()) {
                jdbc.update("INSERT INTO tag (name) VALUES (?)", name);
                tagId = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
                stored.add(name);
            } else {
                tagId = ((Number) found.getFirst().get("id")).longValue();
                stored.add((String) found.getFirst().get("name")); // keep the existing spelling
            }
            jdbc.update("INSERT OR IGNORE INTO item_tag (item_id, tag_id) VALUES (?, ?)", itemId, tagId);
        }
        jdbc.update("DELETE FROM tag WHERE id NOT IN (SELECT tag_id FROM item_tag)");
        plans.touch(item.getPlanId());
        stored.sort(String.CASE_INSENSITIVE_ORDER);
        return stored;
    }

    /** Trims, collapses inner spaces, drops a leading '#', removes duplicates (ignoring case). */
    static List<String> normalize(Collection<String> names) {
        Map<String, String> unique = new LinkedHashMap<>();
        if (names != null) {
            for (String raw : names) {
                if (raw == null) {
                    continue;
                }
                String name = raw.strip().replaceAll("\\s+", " ");
                if (name.startsWith("#")) {
                    name = name.substring(1).strip();
                }
                if (name.isEmpty()) {
                    continue;
                }
                if (name.length() > MAX_TAG_LENGTH) {
                    throw new BusinessRuleException("Tag '%s…' is longer than %d characters"
                            .formatted(name.substring(0, 20), MAX_TAG_LENGTH));
                }
                unique.putIfAbsent(name.toLowerCase(Locale.ROOT), name);
            }
        }
        if (unique.size() > MAX_TAGS_PER_ITEM) {
            throw new BusinessRuleException("An item can have at most %d tags".formatted(MAX_TAGS_PER_ITEM));
        }
        return List.copyOf(unique.values());
    }

    public record TagCount(String name, int items) {
    }
}
