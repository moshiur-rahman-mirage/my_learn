package com.mylearn.tracker.importer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.flashcard.FlashcardService;
import com.mylearn.tracker.importer.ImportDtos.ImportResult;
import com.mylearn.tracker.importer.ImportDtos.ImportedPlan;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.item.Priority;
import com.mylearn.tracker.link.LinkService;
import com.mylearn.tracker.note.ItemNote;
import com.mylearn.tracker.note.ItemNoteRepository;
import com.mylearn.tracker.note.NoteFormat;
import com.mylearn.tracker.note.NoteService;
import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.LearningPlanRepository;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.PlanStatus;
import com.mylearn.tracker.tag.TagService;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Full backup of one plan as JSON: everything the plan holds (items, notes, tags, links, time
 * log, review schedules, confidence ratings and flashcard schedules, with their dates), unlike
 * the CSV export which only carries what makes sense to edit in a spreadsheet.
 *
 * <p>Restoring always creates a <b>new</b> plan. Item ids from the file are kept when they are
 * not in use, so a restored plan still matches its CSV exports for sync.
 */
@Service
@Transactional(readOnly = true)
public class PlanBackupService {

    static final String FORMAT = "learning-tracker-plan";
    static final int VERSION = 1;
    static final int MAX_ITEMS = 50_000;
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    private final PlanService planService;
    private final LearningPlanRepository plans;
    private final LearningItemRepository items;
    private final ItemNoteRepository noteRepository;
    private final NoteService notes;
    private final TagService tags;
    private final LinkService links;
    private final FlashcardService flashcards;
    private final JdbcTemplate jdbc;
    private final EntityManager em;
    private final ObjectMapper json;

    public PlanBackupService(PlanService planService, LearningPlanRepository plans, LearningItemRepository items,
                             ItemNoteRepository noteRepository, NoteService notes, TagService tags, LinkService links,
                             FlashcardService flashcards, JdbcTemplate jdbc, EntityManager em, ObjectMapper json) {
        this.planService = planService;
        this.plans = plans;
        this.items = items;
        this.noteRepository = noteRepository;
        this.notes = notes;
        this.tags = tags;
        this.links = links;
        this.flashcards = flashcards;
        this.jdbc = jdbc;
        this.em = em;
        this.json = json.copy().enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // --- file format --------------------------------------------------------------------------

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record PlanBackup(String format, int version, Instant exportedAt, PlanData plan, List<ItemData> items) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record PlanData(String name, String description, PlanStatus status, LocalDate targetDate, Instant createdAt) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ItemData(String id, ItemType type, String name, String description, Priority priority,
                           boolean completed, Instant completedAt, Instant createdAt, NoteData note, List<String> tags,
                           List<LinkData> links, List<TimeData> timeLog, ReviewData review, ConfidenceData confidence,
                           List<CardData> flashcards, List<ItemData> children) {
    }

    public record NoteData(NoteFormat format, String content, Instant updatedAt) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record LinkData(String url, String title) {
    }

    public record TimeData(int minutes, Instant loggedAt) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ReviewData(int stage, LocalDate dueOn, Instant lastReviewedAt) {
    }

    public record ConfidenceData(int level, Instant ratedAt) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CardData(String question, int stage, LocalDate dueOn, Instant lastReviewedAt) {
    }

    // --- export -------------------------------------------------------------------------------

    public ExportService.Export export(Long planId) {
        LearningPlan plan = planService.findPlan(planId);
        em.flush();
        List<LearningItem> all = items.findAllByPlan(planId);
        Map<Long, List<LearningItem>> children = new HashMap<>();
        for (LearningItem i : all) {
            children.computeIfAbsent(i.getParentId(), k -> new ArrayList<>()).add(i);
        }
        children.values().forEach(l -> l.sort(Comparator.comparingInt(LearningItem::getDisplayOrder).thenComparing(LearningItem::getId)));
        Extras x = loadExtras(planId);
        PlanBackup backup = new PlanBackup(FORMAT, VERSION, Instant.now(),
                new PlanData(plan.getName(), plan.getDescription(), plan.getStatus(), plan.getTargetDate(), plan.getCreatedAt()),
                toData(children.getOrDefault(null, List.of()), children, x));
        try {
            return new ExportService.Export(slug(plan.getName()) + ".json", json.writeValueAsBytes(backup));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<ItemData> toData(List<LearningItem> level, Map<Long, List<LearningItem>> children, Extras x) {
        List<ItemData> out = new ArrayList<>();
        for (LearningItem i : level) {
            ItemNote note = x.notes.get(i.getId());
            out.add(new ItemData(i.getExternalId(), i.getType(), i.getName(), i.getDescription(), i.getPriority(),
                    i.isCompleted(), i.getCompletedAt(), i.getCreatedAt(),
                    note == null ? null : new NoteData(note.getFormat(), note.getContent(), note.getUpdatedAt()),
                    x.tags.getOrDefault(i.getId(), List.of()), x.links.getOrDefault(i.getId(), List.of()),
                    x.time.getOrDefault(i.getId(), List.of()), x.reviews.get(i.getId()), x.confidence.get(i.getId()),
                    x.cards.getOrDefault(i.getId(), List.of()),
                    toData(children.getOrDefault(i.getId(), List.of()), children, x)));
        }
        return out;
    }

    private record Extras(Map<Long, ItemNote> notes, Map<Long, List<String>> tags, Map<Long, List<LinkData>> links,
                          Map<Long, List<TimeData>> time, Map<Long, ReviewData> reviews,
                          Map<Long, ConfidenceData> confidence, Map<Long, List<CardData>> cards) {
    }

    private Extras loadExtras(Long planId) {
        Map<Long, ItemNote> noteMap = noteRepository.findAllByPlan(planId).stream()
                .collect(Collectors.toMap(ItemNote::getItemId, Function.identity()));
        String inPlan = " JOIN learning_item i ON i.id = x.item_id WHERE i.plan_id = ? ";
        Map<Long, List<String>> tagMap = new HashMap<>();
        jdbc.query("SELECT x.item_id, t.name FROM item_tag x JOIN tag t ON t.id = x.tag_id" + inPlan + "ORDER BY lower(t.name)",
                rs -> {
                    tagMap.computeIfAbsent(rs.getLong(1), k -> new ArrayList<>()).add(rs.getString(2));
                }, planId);
        Map<Long, List<LinkData>> linkMap = new HashMap<>();
        jdbc.query("SELECT x.item_id, x.url, x.title FROM item_link x" + inPlan + "ORDER BY x.display_order, x.id",
                rs -> {
                    linkMap.computeIfAbsent(rs.getLong(1), k -> new ArrayList<>()).add(new LinkData(rs.getString(2), rs.getString(3)));
                }, planId);
        Map<Long, List<TimeData>> timeMap = new HashMap<>();
        jdbc.query("SELECT x.item_id, x.minutes, x.logged_at FROM time_log x" + inPlan + "ORDER BY x.logged_at, x.id",
                rs -> {
                    timeMap.computeIfAbsent(rs.getLong(1), k -> new ArrayList<>())
                            .add(new TimeData(rs.getInt(2), Instant.parse(rs.getString(3))));
                }, planId);
        Map<Long, ReviewData> reviewMap = new HashMap<>();
        jdbc.query("SELECT x.item_id, x.stage, x.due_on, x.last_reviewed_at FROM item_review x" + inPlan,
                rs -> {
                    reviewMap.put(rs.getLong(1), new ReviewData(rs.getInt(2), LocalDate.parse(rs.getString(3)), instant(rs.getString(4))));
                }, planId);
        Map<Long, ConfidenceData> confidenceMap = new HashMap<>();
        jdbc.query("SELECT x.item_id, x.level, x.rated_at FROM item_confidence x" + inPlan,
                rs -> {
                    confidenceMap.put(rs.getLong(1), new ConfidenceData(rs.getInt(2), Instant.parse(rs.getString(3))));
                }, planId);
        Map<Long, List<CardData>> cardMap = new HashMap<>();
        jdbc.query("SELECT x.item_id, x.question, x.stage, x.due_on, x.last_reviewed_at FROM flashcard x" + inPlan + "ORDER BY x.position",
                rs -> {
                    cardMap.computeIfAbsent(rs.getLong(1), k -> new ArrayList<>()).add(new CardData(rs.getString(2), rs.getInt(3),
                            LocalDate.parse(rs.getString(4)), instant(rs.getString(5))));
                }, planId);
        return new Extras(noteMap, tagMap, linkMap, timeMap, reviewMap, confidenceMap, cardMap);
    }

    // --- restore ------------------------------------------------------------------------------

    /**
     * What restoring the file would create.
     *
     * @param nameTaken     a plan with the file's name exists; {@code suggestedName} is free
     */
    public record BackupPreview(String fileName, String planName, String suggestedName, boolean nameTaken, Instant exportedAt,
                                int modules, int topics, int completed, int notes, int tags, int links, int timeEntries,
                                int minutes, int reviews, int ratings, int flashcards) {
    }

    public BackupPreview preview(String fileName, byte[] content) {
        PlanBackup b = read(content);
        int[] c = new int[12];
        count(b.items(), c);
        String name = b.plan().name().strip();
        boolean taken = plans.existsByNameIgnoreCase(name);
        return new BackupPreview(fileName, name, taken ? freeName(name) : name, taken, b.exportedAt(),
                c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8], c[9], c[10]);
    }

    private static void count(List<ItemData> list, int[] c) {
        if (list == null) {
            return;
        }
        for (ItemData i : list) {
            c[i.type() == ItemType.MODULE ? 0 : 1]++;
            c[2] += i.completed() ? 1 : 0;
            c[3] += i.note() != null ? 1 : 0;
            c[4] += size(i.tags());
            c[5] += size(i.links());
            c[6] += size(i.timeLog());
            c[7] += i.timeLog() == null ? 0 : i.timeLog().stream().mapToInt(TimeData::minutes).sum();
            c[8] += i.review() != null ? 1 : 0;
            c[9] += i.confidence() != null ? 1 : 0;
            c[10] += size(i.flashcards());
            count(i.children(), c);
        }
    }

    /** Restores the file as a new plan, named {@code newPlanName} (default: the name in the file). */
    @Transactional
    public ImportResult restore(String fileName, byte[] content, String newPlanName) {
        PlanBackup b = read(content);
        String name = newPlanName == null || newPlanName.isBlank() ? b.plan().name().strip() : newPlanName.strip();
        if (plans.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A plan named '%s' already exists; choose another name".formatted(name));
        }
        LearningPlan plan = planService.createEntity(name, b.plan().description());
        if (b.plan().status() != null) {
            plan.setStatus(b.plan().status());
        }
        plan.setTargetDate(b.plan().targetDate());

        Set<String> ids = new HashSet<>();
        collectIds(b.items(), ids);
        Set<String> taken = ids.isEmpty() ? Set.of() : new HashSet<>(items.findExistingExternalIds(ids));
        List<Runnable> afterFlush = new ArrayList<>();
        int[] count = {0};
        create(plan, null, b.items(), taken, afterFlush, count);

        em.flush();
        afterFlush.forEach(Runnable::run);
        if (b.plan().createdAt() != null) {
            em.flush();
            jdbc.update("UPDATE learning_plan SET created_at = ? WHERE id = ?", InstantText.format(b.plan().createdAt()), plan.getId());
        }
        return new ImportResult(List.of(new ImportedPlan(plan.getId(), plan.getName(), fileName, count[0])));
    }

    private void create(LearningPlan plan, LearningItem parent, List<ItemData> list, Set<String> taken,
                        List<Runnable> afterFlush, int[] count) {
        if (list == null) {
            return;
        }
        for (int pos = 0; pos < list.size(); pos++) {
            ItemData d = list.get(pos);
            boolean leaf = d.children() == null || d.children().isEmpty();
            ItemType type = d.type() != null ? d.type() : leaf ? ItemType.TOPIC : ItemType.MODULE;
            LearningItem item = new LearningItem(plan, parent, type, d.name().strip(),
                    d.description() == null || d.description().isBlank() ? null : d.description(), d.priority());
            String id = d.id() == null ? null : d.id().toLowerCase(Locale.ROOT);
            if (id != null && UUID_PATTERN.matcher(id).matches() && taken.add(id)) {
                item.restoreExternalId(id);
            }
            item.setDisplayOrder(pos);
            boolean checkable = leaf && type == ItemType.TOPIC;
            if (d.completed() && checkable) {
                item.markCompleted(d.completedAt() != null ? d.completedAt() : Timestamps.now());
            }
            items.save(item);
            count[0]++;
            Long itemId = item.getId();

            if (d.note() != null && d.note().content() != null) {
                notes.save(itemId, d.note().content(), d.note().format());
                if (d.flashcards() != null) {
                    for (CardData card : d.flashcards()) {
                        if (card.question() != null && card.dueOn() != null) {
                            flashcards.restoreSchedule(itemId, card.question(), card.stage(), card.dueOn(),
                                    card.lastReviewedAt() == null ? null : InstantText.format(card.lastReviewedAt()));
                        }
                    }
                }
            }
            if (d.tags() != null && !d.tags().isEmpty()) {
                tags.setTags(itemId, d.tags());
            }
            if (d.links() != null) {
                for (LinkData link : d.links()) {
                    try {
                        links.add(itemId, link.url(), link.title());
                    } catch (BusinessRuleException ignored) {
                        // an invalid link in a hand-edited file is skipped
                    }
                }
            }
            if (d.timeLog() != null) {
                for (TimeData t : d.timeLog()) {
                    if (t.minutes() >= 1 && t.minutes() <= 1440 && t.loggedAt() != null) {
                        jdbc.update("INSERT INTO time_log (item_id, minutes, logged_at) VALUES (?, ?, ?)",
                                itemId, t.minutes(), InstantText.format(t.loggedAt()));
                    }
                }
            }
            if (d.confidence() != null && d.confidence().level() >= 1 && d.confidence().level() <= 5) {
                Instant at = d.confidence().ratedAt() != null ? d.confidence().ratedAt() : Timestamps.now();
                jdbc.update("INSERT INTO item_confidence (item_id, level, rated_at) VALUES (?, ?, ?)",
                        itemId, d.confidence().level(), InstantText.format(at));
            }
            if (d.review() != null && d.review().dueOn() != null && d.completed() && checkable) {
                jdbc.update("INSERT OR REPLACE INTO item_review (item_id, stage, due_on, last_reviewed_at) VALUES (?, ?, ?, ?)",
                        itemId, Math.max(0, d.review().stage()), d.review().dueOn().toString(),
                        d.review().lastReviewedAt() == null ? null : InstantText.format(d.review().lastReviewedAt()));
            }
            if (d.createdAt() != null) {
                Instant createdAt = d.createdAt();
                afterFlush.add(() -> jdbc.update("UPDATE learning_item SET created_at = ? WHERE id = ?",
                        InstantText.format(createdAt), itemId));
            }
            create(plan, item, d.children(), taken, afterFlush, count);
        }
    }

    private PlanBackup read(byte[] content) {
        PlanBackup b;
        try {
            b = json.readValue(content, PlanBackup.class);
        } catch (IOException e) {
            throw new BusinessRuleException("This isn't a readable plan backup (.json): " + (e instanceof com.fasterxml.jackson.core.JsonProcessingException j ? j.getOriginalMessage() : e.getMessage()));
        }
        if (b == null || !FORMAT.equals(b.format())) {
            throw new BusinessRuleException("This JSON file isn't a Learning Tracker plan backup.");
        }
        if (b.version() > VERSION) {
            throw new BusinessRuleException("This backup was made by a newer version of Learning Tracker. Update the app first.");
        }
        if (b.plan() == null || b.plan().name() == null || b.plan().name().isBlank()) {
            throw new BusinessRuleException("The backup has no plan name.");
        }
        int[] total = {0};
        validate(b.items(), total);
        return b;
    }

    private static void validate(List<ItemData> list, int[] total) {
        if (list == null) {
            return;
        }
        for (ItemData i : list) {
            if (i == null || i.name() == null || i.name().isBlank()) {
                throw new BusinessRuleException("The backup contains an item without a name.");
            }
            if (++total[0] > MAX_ITEMS) {
                throw new BusinessRuleException("The backup has more than %,d items.".formatted(MAX_ITEMS));
            }
            validate(i.children(), total);
        }
    }

    private String freeName(String name) {
        String candidate = name + " (restored)";
        for (int n = 2; plans.existsByNameIgnoreCase(candidate); n++) {
            candidate = name + " (restored " + n + ")";
        }
        return candidate;
    }

    private static void collectIds(List<ItemData> list, Set<String> ids) {
        if (list == null) {
            return;
        }
        for (ItemData i : list) {
            if (i.id() != null) {
                ids.add(i.id().toLowerCase(Locale.ROOT));
            }
            collectIds(i.children(), ids);
        }
    }

    private static int size(List<?> l) {
        return l == null ? 0 : l.size();
    }

    private static Instant instant(String text) {
        return text == null || text.isBlank() ? null : Instant.parse(text);
    }

    private static String slug(String name) {
        String s = name.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "-").replaceAll("(^-|-$)", "");
        return (s.isEmpty() ? "plan" : s) + "-backup";
    }
}
