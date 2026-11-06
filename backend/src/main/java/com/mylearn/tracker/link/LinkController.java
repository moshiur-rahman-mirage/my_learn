package com.mylearn.tracker.link;

import com.mylearn.tracker.item.dto.LinkResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/api")
public class LinkController {

    private final LinkService links;

    public LinkController(LinkService links) {
        this.links = links;
    }

    @GetMapping("/items/{itemId}/links")
    public List<LinkResponse> list(@PathVariable Long itemId) {
        return links.list(itemId);
    }

    @PostMapping("/items/{itemId}/links")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkResponse add(@PathVariable Long itemId, @Valid @RequestBody LinkRequest request) {
        return links.add(itemId, request.url(), request.title());
    }

    @DeleteMapping("/links/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        links.delete(id);
    }

    public record LinkRequest(@Size(max = 2000) String url, @Size(max = 300) String title) {
    }
}
