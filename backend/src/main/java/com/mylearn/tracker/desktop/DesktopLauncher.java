package com.mylearn.tracker.desktop;

import com.mylearn.tracker.LearningTrackerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;

/**
 * Start-up for the installed Windows app (no console window):
 * <ul>
 *   <li>already running → just open the browser (double-clicking the shortcut again is harmless)</li>
 *   <li>otherwise start the server with the {@code desktop} profile, add a tray icon, open the browser</li>
 *   <li>{@code --background} (used by "Start with Windows") skips opening the browser</li>
 * </ul>
 * Plain {@code java -jar} and systemd never come here.
 */
public final class DesktopLauncher {

    public static final int DEFAULT_PORT = 38427;
    static final String BACKGROUND_ARG = "--background";

    private DesktopLauncher() {
    }

    public static void launch(String[] rawArgs) {
        boolean background = Arrays.asList(rawArgs).contains(BACKGROUND_ARG);
        String[] args = Arrays.stream(rawArgs).filter(a -> !BACKGROUND_ARG.equals(a)).toArray(String[]::new);
        int port = Integer.getInteger("server.port", DEFAULT_PORT);
        URI home = URI.create("http://127.0.0.1:" + port + "/");

        if (isOurAppRunning(home)) {
            if (!background) {
                Browser.open(home);
            }
            return;
        }
        if (isPortTaken(port)) {
            fail("Port " + port + " is already used by another program.\n\n"
                    + "Close that program, or change the port: edit\n"
                    + "  <install folder>\\app\\Learning Tracker.cfg\n"
                    + "and replace -Dserver.port=" + port + " with another number.");
            return;
        }

        SpringApplication app = new SpringApplication(LearningTrackerApplication.class);
        app.setHeadless(false); // needed for the tray icon and dialogs
        app.setAdditionalProfiles("desktop");
        try {
            ConfigurableApplicationContext context = app.run(args);
            Path dataDir = Path.of(context.getEnvironment().getRequiredProperty("app.data-dir")).toAbsolutePath();
            Tray.install(context, home, dataDir);
            WindowsAutostart.refreshPathIfEnabled();
            if (!background) {
                Browser.open(home);
            }
        } catch (Throwable e) {
            fail("Learning Tracker could not start:\n" + rootCause(e).getMessage()
                    + "\n\nDetails are in %LOCALAPPDATA%\\LearningTracker\\data\\logs\\learning-tracker.log");
        }
    }

    /** Our health endpoint answering means a previous instance is already serving. */
    static boolean isOurAppRunning(URI home) {
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build()) {
            HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder(home.resolve("actuator/health")).timeout(Duration.ofSeconds(2)).build(),
                    HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body().contains("UP");
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    static boolean isPortTaken(int port) {
        try (ServerSocket ignored = new ServerSocket(port, 1, InetAddress.getLoopbackAddress())) {
            return false;
        } catch (BindException e) {
            return true;
        } catch (IOException e) {
            return false; // let Spring report anything unusual
        }
    }

    private static Throwable rootCause(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t;
    }

    private static void fail(String message) {
        try {
            JOptionPane.showMessageDialog(null, message, "Learning Tracker", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable ignored) {
            System.err.println(message); // headless environment
        }
        System.exit(1);
    }
}
