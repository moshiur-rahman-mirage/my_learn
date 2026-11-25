package com.mylearn.tracker.flashcard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mylearn.tracker.common.HtmlText;
import com.mylearn.tracker.flashcard.FlashcardParser.Card;
import com.mylearn.tracker.note.NoteFormat;
import com.mylearn.tracker.note.NoteLinks;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins what the flashcard parser, HtmlText and [[link]] normalization produce for tricky notes.
 * The phone app checks the same file (mobile/test/parity.test.ts), so the two ports can't drift:
 * card keys must match on both devices or flashcard schedules won't sync.
 *
 * <p>After adding inputs: {@code mvn test -Dtest=FlashcardParityFixtureTest -Dparity.regenerate=true}
 */
class FlashcardParityFixtureTest {

    static final Path FIXTURE = Path.of("src", "test", "resources", "parity", "flashcard-fixtures.json");
    static final ObjectMapper JSON = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @SuppressWarnings("unchecked")
    void fixtureMatchesTheJavaImplementation() throws Exception {
        Map<String, Object> file = JSON.readValue(FIXTURE.toFile(), Map.class);
        List<Map<String, Object>> cases = (List<Map<String, Object>>) file.get("cases");
        List<Map<String, Object>> computed = new ArrayList<>();
        for (Map<String, Object> c : cases) {
            String content = (String) c.get("content");
            NoteFormat format = NoteFormat.valueOf((String) c.get("format"));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("name", c.get("name"));
            out.put("format", format.name());
            out.put("content", content);
            List<Map<String, String>> cards = new ArrayList<>();
            for (Card card : FlashcardParser.parse(content, format)) {
                cards.add(Map.of("key", card.key(), "question", card.question(), "answer", card.answer()));
            }
            out.put("cards", cards);
            if (format == NoteFormat.HTML) {
                out.put("lines", HtmlText.toLines(content));
                out.put("plainText", HtmlText.toPlainText(content));
            }
            out.put("wikiKey", NoteLinks.normalize(content));
            computed.add(out);
        }
        if (Boolean.getBoolean("parity.regenerate")) {
            Files.writeString(FIXTURE, JSON.writeValueAsString(Map.of("cases", computed)) + "\n");
            return;
        }
        assertThat(computed).as("re-run with -Dparity.regenerate=true after changing inputs").isEqualTo(cases);
    }
}
