package com.mylearn.tracker.desktop;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * "Start with Windows": a value under HKCU\...\Run (per user, no admin rights needed).
 * Written through a .reg file so the quoted exe path survives intact.
 */
final class WindowsAutostart {

    private static final String RUN_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String VALUE_NAME = "LearningTracker";

    private WindowsAutostart() {
    }

    /** Set by jpackage launchers; empty when started with plain {@code java}. */
    static Optional<String> exePath() {
        return Optional.ofNullable(System.getProperty("jpackage.app-path")).filter(p -> !p.isBlank());
    }

    static boolean isAvailable() {
        return exePath().isPresent() && System.getProperty("os.name", "").startsWith("Windows");
    }

    static boolean isEnabled() {
        return isAvailable() && run("reg", "query", RUN_KEY, "/v", VALUE_NAME);
    }

    static boolean setEnabled(boolean enabled) {
        if (!isAvailable()) {
            return false;
        }
        return enabled ? writeValue(exePath().get()) : run("reg", "delete", RUN_KEY, "/v", VALUE_NAME, "/f");
    }

    /** After an update or reinstall in another folder, point the entry at the current exe. */
    static void refreshPathIfEnabled() {
        if (isEnabled()) {
            setEnabled(true);
        }
    }

    private static boolean writeValue(String exe) {
        String data = "\"" + exe + "\" " + DesktopLauncher.BACKGROUND_ARG;
        String reg = "Windows Registry Editor Version 5.00\r\n\r\n"
                + "[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run]\r\n"
                + "\"" + VALUE_NAME + "\"=\"" + data.replace("\\", "\\\\").replace("\"", "\\\"") + "\"\r\n";
        Path file = null;
        try {
            file = Files.createTempFile("learning-tracker-autostart", ".reg");
            byte[] bom = {(byte) 0xFF, (byte) 0xFE};
            byte[] body = reg.getBytes(StandardCharsets.UTF_16LE);
            byte[] all = new byte[bom.length + body.length];
            System.arraycopy(bom, 0, all, 0, bom.length);
            System.arraycopy(body, 0, all, bom.length, body.length);
            Files.write(file, all);
            return run("reg", "import", file.toString());
        } catch (IOException e) {
            return false;
        } finally {
            if (file != null) {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException ignored) {
                    // temp file; harmless
                }
            }
        }
    }

    private static boolean run(String... command) {
        try {
            Process p = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
            return p.waitFor() == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
