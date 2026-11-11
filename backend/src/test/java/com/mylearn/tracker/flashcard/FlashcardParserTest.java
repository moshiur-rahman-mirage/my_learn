package com.mylearn.tracker.flashcard;

import com.mylearn.tracker.flashcard.FlashcardParser.Card;
import com.mylearn.tracker.note.NoteFormat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlashcardParserTest {

    @Test
    void markdownCardsWithMultiLineAnswersAndCode() {
        String md = """
                # Spring

                Some intro text. A: not a card, no question before it.

                Q: What does DI stand for?
                A: Dependency injection.
                It continues here.

                - **Q:** Show a bean
                - **A:** Like this:
                ```java
                @Bean

                Foo foo() { return new Foo(); }
                ```

                Q: question without answer

                ## Next
                q: lower case works
                a: yes
                """;
        List<Card> cards = FlashcardParser.parse(md, NoteFormat.MARKDOWN);
        assertThat(cards).extracting(Card::question)
                .containsExactly("What does DI stand for?", "Show a bean", "lower case works");
        assertThat(cards.get(0).answer()).isEqualTo("Dependency injection.\nIt continues here.");
        assertThat(cards.get(1).answer()).contains("```java").contains("Foo foo()").endsWith("```");
    }

    @Test
    void htmlNotesUseTheirParagraphs() {
        String html = "<h2>Cards</h2><p>Q: Capital of France?</p><p>A: Paris</p><p>other text</p>";
        List<Card> cards = FlashcardParser.parse(html, NoteFormat.HTML);
        assertThat(cards).containsExactly(new Card(FlashcardParser.key("Capital of France?"), "Capital of France?", "Paris"));
    }

    @Test
    void sameQuestionTwiceIsOneCard_keyIgnoresCaseAndSpacing() {
        List<Card> cards = FlashcardParser.parse("Q: What  is X?\nA: one\n\nQ: what is x?\nA: two\n", NoteFormat.MARKDOWN);
        assertThat(cards).hasSize(1);
        assertThat(FlashcardParser.key("What  is X?")).isEqualTo(FlashcardParser.key("what is x?"));
    }
}
