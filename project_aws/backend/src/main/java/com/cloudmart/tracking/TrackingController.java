package com.cloudmart.tracking;

import com.cloudmart.tracking.ClickTracker.ClickEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class TrackingController {

    private final ClickTracker tracker;

    public TrackingController(ClickTracker tracker) {
        this.tracker = tracker;
    }

    @PostMapping("/api/track")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void track(@RequestBody ClickEvent e) {
        String ts = e.ts() != null ? e.ts() : Instant.now().toString();
        tracker.track(new ClickEvent(e.event(), e.productId(), e.sessionId(), ts));
    }
}
