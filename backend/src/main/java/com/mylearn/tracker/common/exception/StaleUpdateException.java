package com.mylearn.tracker.common.exception;

/**
 * The client edited an outdated copy: the version it sent no longer matches the stored one
 * (HTTP 409). The client should reload and re-apply its change.
 */
public class StaleUpdateException extends ConflictException {

    private final long currentVersion;

    public StaleUpdateException(String resource, Object id, long sentVersion, long currentVersion) {
        super("%s %s was changed elsewhere (you edited version %d, current is %d). Reload and try again."
                .formatted(resource, id, sentVersion, currentVersion));
        this.currentVersion = currentVersion;
    }

    public long getCurrentVersion() {
        return currentVersion;
    }
}
