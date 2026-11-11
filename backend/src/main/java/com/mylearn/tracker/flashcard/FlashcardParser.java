package com.mylearn.tracker.flashcard;

import com.mylearn.tracker.common.HtmlText;
import com.mylearn.tracker.note.NoteFormat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds flashcards in a note. A card is a {@code Q:} line followed by an {@code A:} line:
 * <pre>
 * Q: What does DI stand for?
 * A: Dependency injection.
 *    The answer may continue on the next lines, up to a blank line.
 * </pre>
 * The markers may be bold ({@code **Q:**}) or list items ({@code - Q:}). The question may also
 * span several lines until the {@code A:} line. Code fences inside an answer are kept whole, and
 * markers inside code blocks are ignored. Pure logic, no database.
 */
public final class FlashcardParser {

    static final int MAX_CARDS = 300;
    static final int MAX_QUESTION = 2_000;
    static final int MAX_ANSWER = 20_000;

    private static final Pattern MARKER = Pattern.compile(
            "^\\s*(?:[-*+]\\s+|\\d+[.)]\\s+)?(?:\\*\\*|__)?([QqAa])(?:\\*\\*|__)?\\s*[:：](?:\\*\\*|__)?\\s?(.*)$");
    private static final Pattern FENCE = Pattern.compile("^\\s*(```|~~~)");
    private static final Pattern HEADING = Pattern.compile("^\\s{0,3}#{1,6}\\s");

    private FlashcardParser() {
    }

    public record Card(String key, String question, String answer) {
    }

    public static List<Card> parse(String content, NoteFormat format) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        String text = format == NoteFormat.HTML ? HtmlText.toLines(content) : content;
        String[] lines = text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);

        List<Card> cards = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        StringBuilder question = null;
        StringBuilder answer = null;
        boolean inFence = false;

        for (String line : lines) {
            if (FENCE.matcher(line).find()) {
                inFence = !inFence;
                if (answer != null) {
                    answer.append(line).append('\n');
                }
                continue;
            }
            if (inFence) {
                if (answer != null) {
                    answer.append(line).append('\n');
                }
                continue;
            }
            Matcher m = MARKER.matcher(line);
            boolean isQ = m.matches() && m.group(1).equalsIgnoreCase("q");
            boolean isA = m.matches() && m.group(1).equalsIgnoreCase("a");

            if (isQ) {
                add(cards, keys, question, answer);
                question = new StringBuilder(m.group(2));
                answer = null;
            } else if (isA && question != null && answer == null) {
                answer = new StringBuilder(m.group(2)).append('\n');
            } else if (line.isBlank() || HEADING.matcher(line).find()) {
                if (answer != null) {
                    add(cards, keys, question, answer);
                    question = null;
                    answer = null;
                } else if (question != null && HEADING.matcher(line).find()) {
                    question = null; // a question without an answer
                }
            } else if (answer != null) {
                answer.append(line).append('\n');
            } else if (question != null) {
                question.append('\n').append(line);
            }
            if (cards.size() >= MAX_CARDS) {
                return cards;
            }
        }
        add(cards, keys, question, answer);
        return cards;
    }

    private static void add(List<Card> cards, Set<String> keys, StringBuilder question, StringBuilder answer) {
        if (question == null || answer == null || cards.size() >= MAX_CARDS) {
            return;
        }
        String q = question.toString().strip();
        String a = answer.toString().strip();
        if (q.isEmpty() || a.isEmpty()) {
            return;
        }
        q = q.length() > MAX_QUESTION ? q.substring(0, MAX_QUESTION) : q;
        a = a.length() > MAX_ANSWER ? a.substring(0, MAX_ANSWER) : a;
        String key = key(q);
        if (keys.add(key)) { // the same question twice in one note is one card
            cards.add(new Card(key, q, a));
        }
    }

    /** Identity of a card: its question, ignoring case and spacing. */
    static String key(String question) {
        String normal = question.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").strip();
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(normal.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash, 0, 12);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
