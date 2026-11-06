package com.mylearn.tracker.item.dto;

/** A web link of an item. {@code title} is null when none was given (the UI shows the host). */
public record LinkResponse(Long id, String url, String title) {
}
