package com.mylearn.tracker.flashcard;

import com.mylearn.tracker.flashcard.FlashcardService.CardCounts;
import com.mylearn.tracker.flashcard.FlashcardService.DueCard;
import com.mylearn.tracker.flashcard.FlashcardService.ItemCard;
import com.mylearn.tracker.flashcard.FlashcardService.ReviewOutcome;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlashcardController {

    private final FlashcardService cards;

    public FlashcardController(FlashcardService cards) {
        this.cards = cards;
    }

    @GetMapping("/api/flashcards/due")
    public List<DueCard> due(@RequestParam(required = false) Long planId) {
        return cards.due(planId);
    }

    @PostMapping("/api/flashcards/{id}/review")
    public ReviewOutcome review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return cards.review(id, request.remembered());
    }

    @GetMapping("/api/items/{itemId}/flashcards")
    public List<ItemCard> forItem(@PathVariable Long itemId) {
        return cards.forItem(itemId);
    }

    @GetMapping("/api/plans/{planId}/flashcards/counts")
    public CardCounts counts(@PathVariable Long planId) {
        return cards.counts(planId);
    }

    public record ReviewRequest(@NotNull Boolean remembered) {
    }
}
