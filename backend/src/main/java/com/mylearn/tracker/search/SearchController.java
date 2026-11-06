package com.mylearn.tracker.search;

import com.mylearn.tracker.search.SearchService.SearchResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService search;

    public SearchController(SearchService search) {
        this.search = search;
    }

    /**
     * @param q   words to find in names, descriptions and notes
     * @param tag only items with this tag
     */
    @GetMapping
    public SearchResult search(@RequestParam(required = false) String q, @RequestParam(required = false) String tag) {
        return search.search(q, tag);
    }
}
