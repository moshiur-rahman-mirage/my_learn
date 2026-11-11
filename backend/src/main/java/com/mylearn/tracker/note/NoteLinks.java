package com.mylearn.tracker.note;

import com.mylearn.tracker.common.HtmlText;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code [[Item name]]} links in notes (also {@code [[Item name|shown text]]}). A link points to an
 * item of the same plan by name, ignoring case and spacing, so it survives the target being
 * recreated or the plan being re-imported. The references are stored per note so that an item
 * can show the notes that link to it.
 */
@Service
@Transactional(readOnly = true)
public class NoteLinks {

    private static final Pattern LINK = Pattern.compile("\\[\\[([^\\[\\]\\n|]{1,200})(?:\\|[^\\[\\]\\n]{0,200})?]]");
    static final int MAX_LINKS_PER_NOTE = 500;

    private final JdbcTemplate jdbc;
    private final EntityManager em;
    private final LearningItemRepository items;

    public NoteLinks(JdbcTemplate jdbc, EntityManager em, LearningItemRepository items) {
        this.jdbc = jdbc;
        this.em = em;
        this.items = items;
    }

    /** Re-reads the references of one note; a null note removes them. */
    @Transactional
    public void update(Long itemId, String content, NoteFormat format) {
        em.flush();
        jdbc.update("DELETE FROM note_ref WHERE source_item_id = ?", itemId);
        for (String target : targets(content, format)) {
            jdbc.update("INSERT OR IGNORE INTO note_ref (source_item_id, target_name) VALUES (?, ?)", itemId, target);
        }
    }

    /** Items of the same plan whose notes link to this item, in plan order. */
    public List<Backlink> backlinks(Long itemId) {
        LearningItem item = items.findById(itemId).orElseThrow(() -> new NotFoundException("Item", itemId));
        em.flush();
        return jdbc.query("""
                        SELECT i.id, i.name, i.item_type FROM note_ref r
                        JOIN learning_item i ON i.id = r.source_item_id
                        WHERE r.target_name = ? AND i.plan_id = ? AND i.id <> ?
                        ORDER BY i.display_order, i.id""",
                (rs, n) -> new Backlink(rs.getLong("id"), rs.getString("name"), ItemType.valueOf(rs.getString("item_type"))),
                normalize(item.getName()), item.getPlanId(), itemId);
    }

    static Set<String> targets(String content, NoteFormat format) {
        Set<String> found = new LinkedHashSet<>();
        if (content == null || content.isBlank()) {
            return found;
        }
        String text = format == NoteFormat.HTML ? HtmlText.toLines(content) : content;
        Matcher m = LINK.matcher(text);
        while (m.find() && found.size() < MAX_LINKS_PER_NOTE) {
            String name = normalize(m.group(1));
            if (!name.isEmpty()) {
                found.add(name);
            }
        }
        return found;
    }

    /** Lower case with single spaces; the same rule the UI uses to resolve a link. */
    public static String normalize(String name) {
        return name == null ? "" : name.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    public record Backlink(Long itemId, String name, ItemType type) {
    }
}
