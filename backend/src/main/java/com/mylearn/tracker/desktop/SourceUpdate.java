package com.mylearn.tracker.desktop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * "Update from source code" in the tray menu: runs {@code deploy\windows\install-app.ps1} from the
 * project folder the app was built from (recorded by the build as {@code -Dapp.source-dir}).
 * The script rebuilds the app, replaces this installation and starts the new version; it shows
 * its progress in a console window and stops for Enter at the end, so a failed build can be read.
 * The running app keeps working until the new build is ready.
 */
final class SourceUpdate {

    private SourceUpdate() {
    }

    /** The install script, or null when the app wasn't built from a source folder on this computer. */
    static Path script() {
        String dir = System.getProperty("app.source-dir");
        if (dir == null || dir.isBlank() || !System.getProperty("os.name", "").toLowerCase().contains("win")) {
            return null;
        }
        Path script = Path.of(dir).resolve("deploy").resolve("windows").resolve("install-app.ps1");
        return Files.isRegularFile(script) ? script : null;
    }

    static void start(Path script) {
        try {
            // "start" detaches the console, so it survives the script stopping this app
            new ProcessBuilder("cmd.exe", "/c", "start", "\"Learning Tracker update\"",
                    "powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass",
                    "-File", script.toString(), "-PauseAtEnd")
                    .directory(script.getParent().getParent().getParent().toFile())
                    .start();
        } catch (IOException ignored) {
            // nothing to show it on; the user can run the script by hand
        }
    }
}
