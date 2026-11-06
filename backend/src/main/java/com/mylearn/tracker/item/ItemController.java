package com.mylearn.tracker.item;

import com.mylearn.tracker.item.dto.CompletionRequest;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemDetailResponse;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.MoveItemRequest;
import com.mylearn.tracker.item.dto.ReorderItemRequest;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.item.dto.UpdateItemRequest;
import com.mylearn.tracker.progress.Progress;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final ItemService items;

    public ItemController(ItemService items) {
        this.items = items;
    }

    @PostMapping("/plans/{planId}/items")
    public ResponseEntity<ItemResponse> create(@PathVariable Long planId, @Valid @RequestBody CreateItemRequest request) {
        ItemResponse created = items.create(planId, request);
        return ResponseEntity.created(URI.create("/api/items/" + created.id())).body(created);
    }

    /** The plan's whole roadmap as a tree with rolled-up progress. */
    @GetMapping("/plans/{planId}/items")
    public List<TreeNode> tree(@PathVariable Long planId) {
        return items.tree(planId);
    }

    @GetMapping("/items/{id}")
    public ItemDetailResponse get(@PathVariable Long id) {
        return items.detail(id);
    }

    @PutMapping("/items/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest request) {
        return items.update(id, request);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        items.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/items/{id}/completion")
    public ItemResponse completion(@PathVariable Long id, @Valid @RequestBody CompletionRequest request) {
        return items.setCompletion(id, request.completed());
    }

    @PatchMapping("/items/{id}/move")
    public ItemResponse move(@PathVariable Long id, @Valid @RequestBody MoveItemRequest request) {
        return items.move(id, request);
    }

    @PatchMapping("/items/{id}/reorder")
    public ItemResponse reorder(@PathVariable Long id, @Valid @RequestBody ReorderItemRequest request) {
        return items.reorder(id, request.position());
    }

    @GetMapping("/items/{id}/progress")
    public Progress progress(@PathVariable Long id) {
        return items.progressOf(id);
    }
}
