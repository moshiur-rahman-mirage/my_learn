package com.mylearn.tracker.desktop;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

/** Notification-area icon: the only visible part of the running server. */
final class Tray {

    private Tray() {
    }

    static void install(ConfigurableApplicationContext context, URI home, Path dataDir) {
        if (!SystemTray.isSupported()) {
            return;
        }
        EventQueue.invokeLater(() -> {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                TrayIcon icon = new TrayIcon(loadImage(), "Learning Tracker - " + home);
                icon.setImageAutoSize(true);

                PopupMenu menu = new PopupMenu();
                MenuItem open = new MenuItem("Open Learning Tracker");
                open.addActionListener(e -> Browser.open(home));
                menu.add(open);

                MenuItem folder = new MenuItem("Open data folder (database, backups)");
                folder.addActionListener(e -> openFolder(dataDir));
                menu.add(folder);

                if (WindowsAutostart.isAvailable()) {
                    CheckboxMenuItem autostart = new CheckboxMenuItem("Start with Windows", WindowsAutostart.isEnabled());
                    autostart.addItemListener(e -> {
                        boolean wanted = autostart.getState();
                        if (!WindowsAutostart.setEnabled(wanted)) {
                            autostart.setState(!wanted);
                        }
                    });
                    menu.add(autostart);
                }

                menu.addSeparator();
                MenuItem quit = new MenuItem("Quit");
                quit.addActionListener(e -> {
                    tray.remove(icon);
                    // off the AWT thread: closing Spring waits for requests and the database
                    Thread stopper = new Thread(() -> System.exit(SpringApplication.exit(context)), "quit");
                    stopper.start();
                });
                menu.add(quit);

                icon.setPopupMenu(menu);
                icon.addActionListener(e -> Browser.open(home)); // double-click
                tray.add(icon);
            } catch (AWTException | IOException e) {
                // no tray (e.g. some remote sessions): the app still runs; quit via Task Manager
            }
        });
    }

    private static Image loadImage() throws IOException {
        try (InputStream in = Tray.class.getResourceAsStream("/desktop/tray-icon.png")) {
            if (in == null) {
                throw new IOException("tray icon resource missing");
            }
            return ImageIO.read(in);
        }
    }

    private static void openFolder(Path dir) {
        try {
            Desktop.getDesktop().open(dir.toFile());
        } catch (IOException | UnsupportedOperationException | IllegalArgumentException ignored) {
            // folder missing or no shell; nothing useful to do
        }
    }
}
