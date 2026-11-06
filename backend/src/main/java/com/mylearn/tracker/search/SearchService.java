package com.mylearn.tracker.search;

import com.mylearn.tracker.item.HierarchyQueries;
import com.mylearn.tracker.item.HierarchyQueries.PathEntry;
import com.mylearn.tracker.item.ItemType;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Search across all plans: item names, descriptions and note text (SQLite FTS5, kept up to date
 * by triggers), optionally limited to items with a tag. Each word matches as a prefix, so
 * "sprin boo" finds "Spring Boot"; all words must match.
 */
@Service
@Transactional(readOnly = true)
public class SearchService {

    static final int LIMIT = 50;
    private static final char HIT_START = '\u0002';
    private static final char HIT_END = '\u0003';
    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}\\p{M}_]+");

    private final JdbcTemplate jdbc;
    private final EntityManager em;
    private final HierarchyQueries hierarchy;

    public SearchService(JdbcTemplate jdbc, EntityManager em, HierarchyQueries hierarchy) {
        this.jdbc = jdbc;
        this.em = em;
        this.hierarchy = hierarchy;
    }

    /**
     * @param text words to find (blank = only filter by tag)
     * @param tag  only items with this tag (blank = any)
     */
    public SearchResult search(String text, String tag) {
        String match = toMatchQuery(text);
        boolean byTag = tag != null && !tag.isBlank();
        if (match == null && !byTag) {
            return new SearchResult(List.of(), false);
        }
        em.flush();
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT i.id, i.name, i.description, i.item_type, i.is_completed, "
                + "i.plan_id, p.name AS plan_name, p.status AS plan_status, "
                + "NOT EXISTS (SELECT 1 FROM learning_item c WHERE c.parent_id = i.id) AS leaf");
        if (match != null) {
            sql.append(", snippet(search_index, 0, char(2), char(3), '…', 12) AS s_name")
                    .append(", snippet(search_index, 1, char(2), char(3), '…', 12) AS s_desc")
                    .append(", snippet(search_index, 2, char(2), char(3), '…', 16) AS s_note")
                    .append(" FROM search_index s JOIN learning_item i ON i.id = s.item_id");
        } else {
            sql.append(" FROM learning_item i");
        }
        sql.append(" JOIN learning_plan p ON p.id = i.plan_id WHERE 1 = 1");
        if (match != null) {
            sql.append(" AND search_index MATCH ?");
            args.add(match);
        }
        if (byTag) {
            sql.append(" AND i.id IN (SELECT it.item_id FROM item_tag it JOIN tag t ON t.id = it.tag_id "
                    + "WHERE t.name = ? COLLATE NOCASE)");
            args.add(tag.strip());
        }
        // active plans first; then relevance (name hits weigh most) or, for a tag, plan and name
        sql.append(" ORDER BY (p.status = 'ACTIVE') DESC, ")
                .append(match != null ? "bm25(search_index, 10.0, 3.0, 1.0)" : "lower(p.name), lower(i.name)")
                .append(" LIMIT ").append(LIMIT + 1);

        List<SearchHit> hits = jdbc.query(sql.toString(), (rs, n) -> toHit(rs, match != null), args.toArray());
        boolean more = hits.size() > LIMIT;
        List<SearchHit> shown = more ? hits.subList(0, LIMIT) : hits;
        return new SearchResult(shown.stream().map(this::withPath).toList(), more);
    }

    private SearchHit withPath(SearchHit hit) {
        List<PathEntry> path = hierarchy.pathTo(hit.itemId());
        List<String> names = path.subList(0, Math.max(0, path.size() - 1)).stream().map(PathEntry::name).toList();
        return new SearchHit(hit.itemId(), hit.name(), hit.type(), hit.checkable(), hit.completed(), hit.planId(),
                hit.planName(), hit.planArchived(), names, hit.matchedIn(), hit.snippet());
    }

    private static SearchHit toHit(ResultSet rs, boolean withSnippets) throws SQLException {
        ItemType type = ItemType.valueOf(rs.getString("item_type"));
        boolean checkable = type == ItemType.TOPIC && rs.getBoolean("leaf");
        String matchedIn = null;
        List<Segment> snippet = List.of();
        if (withSnippets) {
            if (hasHit(rs.getString("s_name"))) {
                matchedIn = "name";
            } else if (hasHit(rs.getString("s_desc"))) {
                matchedIn = "description";
                snippet = segments(rs.getString("s_desc"));
            } else if (hasHit(rs.getString("s_note"))) {
                matchedIn = "note";
                snippet = segments(rs.getString("s_note"));
            }
        }
        if (snippet.isEmpty() && rs.getString("description") != null) {
            snippet = List.of(new Segment(shorten(rs.getString("description")), false));
        }
        return new SearchHit(rs.getLong("id"), rs.getString("name"), type, checkable,
                checkable && rs.getBoolean("is_completed"), rs.getLong("plan_id"), rs.getString("plan_name"),
                !"ACTIVE".equals(rs.getString("plan_status")), List.of(), matchedIn, snippet);
    }

    /**
     * Turns user input into a safe FTS5 query: every word becomes a quoted prefix term, so
     * operators and punctuation typed by the user can never cause a syntax error.
     *
     * @return null when the input has no words
     */
    static String toMatchQuery(String text) {
        if (text == null) {
            return null;
        }
        List<String> terms = new ArrayList<>();
        Matcher m = WORD.matcher(text);
        while (m.find() && terms.size() < 10) {
            terms.add("\"" + m.group() + "\"*");
        }
        return terms.isEmpty() ? null : String.join(" ", terms);
    }

    private static boolean hasHit(String s) {
        return s != null && s.indexOf(HIT_START) >= 0;
    }

    /** Splits snippet text at the hit markers. */
    static List<Segment> segments(String s) {
        List<Segment> out = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        boolean inHit = false;
        for (char c : s.toCharArray()) {
            if (c == HIT_START || c == HIT_END) {
                if (!buf.isEmpty()) {
                    out.add(new Segment(buf.toString(), inHit));
                    buf.setLength(0);
                }
                inHit = c == HIT_START;
            } else {
                buf.append(c);
            }
        }
        if (!buf.isEmpty()) {
            out.add(new Segment(buf.toString(), inHit));
        }
        return out;
    }

    private static String shorten(String s) {
        return s.length() <= 160 ? s : s.substring(0, 157) + "…";
    }

    /** A piece of a snippet; {@code hit} pieces are the matched words. */
    public record Segment(String text, boolean hit) {
    }

    /**
     * @param path      ancestor names, root first
     * @param matchedIn "name", "description" or "note" (null when searching by tag only)
     * @param snippet   context around the match (or the description)
     */
    public record SearchHit(Long itemId, String name, ItemType type, boolean checkable, boolean completed,
                            Long planId, String planName, boolean planArchived, List<String> path,
                            String matchedIn, List<Segment> snippet) {
    }

    /** @param more there are more hits than shown: refine the search */
    public record SearchResult(List<SearchHit> hits, boolean more) {
    }
}
