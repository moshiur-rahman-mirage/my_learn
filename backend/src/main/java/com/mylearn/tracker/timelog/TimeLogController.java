package com.mylearn.tracker.timelog;

import com.mylearn.tracker.timelog.TimeLogService.ItemTime;
import com.mylearn.tracker.timelog.TimeLogService.TimeEntry;
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

@RestController
@RequestMapping("/api")
public class TimeLogController {

    private final TimeLogService time;

    public TimeLogController(TimeLogService time) {
        this.time = time;
    }

    @GetMapping("/items/{itemId}/time")
    public ItemTime forItem(@PathVariable Long itemId) {
        return time.forItem(itemId);
    }

    @PostMapping("/items/{itemId}/time")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeEntry log(@PathVariable Long itemId, @Valid @RequestBody LogTimeRequest request) {
        return time.log(itemId, request.minutes());
    }

    @DeleteMapping("/time/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        time.delete(id);
    }

    public record LogTimeRequest(@NotNull Integer minutes) {
    }
}
