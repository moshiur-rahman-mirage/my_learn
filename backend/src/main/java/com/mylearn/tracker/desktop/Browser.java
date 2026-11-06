package com.mylearn.tracker.desktop;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

final class Browser {

    private Browser() {
    }

    static void open(URI uri) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
                return;
            }
        } catch (IOException | UnsupportedOperationException ignored) {
            // fall through to the Windows shell
        }
        try {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", uri.toString()).start();
        } catch (IOException ignored) {
            // nothing else to try; the tray menu still offers "Open"
        }
    }
}
