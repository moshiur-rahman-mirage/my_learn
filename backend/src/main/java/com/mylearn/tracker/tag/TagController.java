package com.mylearn.tracker.tag;

import com.mylearn.tracker.tag.TagService.TagCount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TagController {

    private final TagService tags;

    public TagController(TagService tags) {
        this.tags = tags;
    }

    @GetMapping("/tags")
    public List<TagCount> all() {
        return tags.all();
    }

    /** Replaces the item's tags; returns the stored names. */
    @PutMapping("/items/{itemId}/tags")
    public List<String> set(@PathVariable Long itemId, @Valid @RequestBody SetTagsRequest request) {
        return tags.setTags(itemId, request.tags());
    }

    public record SetTagsRequest(@NotNull List<String> tags) {
    }
}
