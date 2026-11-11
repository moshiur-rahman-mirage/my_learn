package com.mylearn.tracker.confidence;

import com.mylearn.tracker.confidence.ConfidenceService.Rated;
import com.mylearn.tracker.confidence.ConfidenceService.Shaky;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConfidenceController {

    private final ConfidenceService confidence;

    public ConfidenceController(ConfidenceService confidence) {
        this.confidence = confidence;
    }

    /** {@code {"level": 1..5}}, or {@code {"level": null}} to clear. */
    @PutMapping("/api/items/{itemId}/confidence")
    public Rated rate(@PathVariable Long itemId, @RequestBody RateRequest request) {
        return confidence.rate(itemId, request.level());
    }

    @GetMapping("/api/confidence/shaky")
    public List<Shaky> shaky() {
        return confidence.shaky();
    }

    public record RateRequest(Integer level) {
    }
}
