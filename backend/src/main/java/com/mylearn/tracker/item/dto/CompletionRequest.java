package com.mylearn.tracker.item.dto;

import jakarta.validation.constraints.NotNull;

public record CompletionRequest(@NotNull Boolean completed) {
}
