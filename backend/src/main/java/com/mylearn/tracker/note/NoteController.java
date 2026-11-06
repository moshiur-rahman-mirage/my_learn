package com.mylearn.tracker.note;

import com.mylearn.tracker.note.NoteService.NoteResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items/{id}/notes")
public class NoteController {

    private final NoteService notes;

    public NoteController(NoteService notes) {
        this.notes = notes;
    }

    @GetMapping
    public NoteResponse get(@PathVariable Long id) {
        return notes.get(id);
    }

    @PutMapping
    public NoteResponse save(@PathVariable Long id, @Valid @RequestBody SaveNoteRequest request) {
        return notes.save(id, request.content());
    }

    /** About 100 pages of text; images aren't embedded, so real notes stay far below this. */
    public record SaveNoteRequest(@Size(max = 500_000, message = "Note is too long") String content) {
    }
}
