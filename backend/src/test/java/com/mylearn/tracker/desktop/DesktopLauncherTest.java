package com.mylearn.tracker.desktop;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class DesktopLauncherTest {

    @Test
    void detectsAPortHeldByAnotherProgram_butDoesNotMistakeItForOurApp() throws Exception {
        try (ServerSocket other = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            int port = other.getLocalPort();
            assertThat(DesktopLauncher.isPortTaken(port)).isTrue();
            // something listens, but it never answers our health check
            assertThat(DesktopLauncher.isOurAppRunning(URI.create("http://127.0.0.1:" + port + "/"))).isFalse();
        }
    }

    @Test
    void freePortIsNotTaken() throws Exception {
        int port;
        try (ServerSocket s = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            port = s.getLocalPort();
        }
        assertThat(DesktopLauncher.isPortTaken(port)).isFalse();
        assertThat(DesktopLauncher.isOurAppRunning(URI.create("http://127.0.0.1:" + port + "/"))).isFalse();
    }
}
