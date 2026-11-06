package com.mylearn.tracker.review;

import com.mylearn.tracker.review.ReviewService.DueReview;
import com.mylearn.tracker.review.ReviewService.ReviewResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviews;

    public ReviewController(ReviewService reviews) {
        this.reviews = reviews;
    }

    @GetMapping("/due")
    public List<DueReview> due() {
        return reviews.due();
    }

    @PostMapping("/{itemId}")
    public ReviewResult review(@PathVariable Long itemId, @Valid @RequestBody ReviewRequest request) {
        return reviews.review(itemId, request.remembered());
    }

    /** Stop reviewing a topic (it stays completed). */
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stop(@PathVariable Long itemId) {
        reviews.cancel(itemId);
    }

    public record ReviewRequest(@NotNull Boolean remembered) {
    }
}
