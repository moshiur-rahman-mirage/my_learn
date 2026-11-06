package com.mylearn.tracker.link;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.item.dto.LinkResponse;
import com.mylearn.tracker.plan.PlanService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

import static com.mylearn.tracker.common.Text.blankToNull;

/** Web links attached to an item (a video, a docs page, a course lesson). */
@Service
@Transactional(readOnly = true)
public class LinkService {

    static final int MAX_LINKS_PER_ITEM = 50;

    private final JdbcTemplate jdbc;
    private final LearningItemRepository items;
    private final PlanService plans;

    public LinkService(JdbcTemplate jdbc, LearningItemRepository items, PlanService plans) {
        this.jdbc = jdbc;
        this.items = items;
        this.plans = plans;
    }

    public List<LinkResponse> list(Long itemId) {
        requireItem(itemId);
        return jdbc.query("SELECT id, url, title FROM item_link WHERE item_id = ? ORDER BY display_order, id",
                (rs, n) -> new LinkResponse(rs.getLong("id"), rs.getString("url"), rs.getString("title")), itemId);
    }

    /** @param url "example.com/x" is accepted and stored as "https://example.com/x" */
    @Transactional
    public LinkResponse add(Long itemId, String url, String title) {
        LearningItem item = requireItem(itemId);
        String clean = normalizeUrl(url);
        String cleanTitle = blankToNull(title);
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM item_link WHERE item_id = ?", Integer.class, itemId);
        if (count != null && count >= MAX_LINKS_PER_ITEM) {
            throw new BusinessRuleException("An item can have at most %d links".formatted(MAX_LINKS_PER_ITEM));
        }
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.update(c -> {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO item_link (item_id, url, title, display_order, created_at) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, itemId);
            ps.setString(2, clean);
            ps.setString(3, cleanTitle);
            ps.setInt(4, count == null ? 0 : count);
            ps.setString(5, InstantText.format(Timestamps.now()));
            return ps;
        }, key);
        plans.touch(item.getPlanId());
        return new LinkResponse(key.getKey().longValue(), clean, cleanTitle);
    }

    @Transactional
    public void delete(Long linkId) {
        List<Long> itemIds = jdbc.queryForList("SELECT item_id FROM item_link WHERE id = ?", Long.class, linkId);
        if (itemIds.isEmpty()) {
            throw new NotFoundException("Link", linkId);
        }
        jdbc.update("DELETE FROM item_link WHERE id = ?", linkId);
        items.findById(itemIds.getFirst()).ifPresent(i -> plans.touch(i.getPlanId()));
    }

    /** Only web links: a link opens in the browser with one click, so no javascript: or file: URLs. */
    static String normalizeUrl(String raw) {
        String url = raw == null ? "" : raw.strip();
        if (url.isEmpty()) {
            throw new BusinessRuleException("Enter a link");
        }
        if (!url.contains("://")) {
            url = "https://" + url;
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!scheme.equals("http") && !scheme.equals("https")) {
                throw new BusinessRuleException("Only http:// and https:// links are supported");
            }
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new BusinessRuleException("Not a valid link: " + raw);
            }
        } catch (URISyntaxException e) {
            throw new BusinessRuleException("Not a valid link: " + raw);
        }
        if (url.length() > 2000) {
            throw new BusinessRuleException("Link is too long");
        }
        return url;
    }

    private LearningItem requireItem(Long itemId) {
        return items.findById(itemId).orElseThrow(() -> new NotFoundException("Item", itemId));
    }
}
