package com.mylearn.tracker.sync;

import com.mylearn.tracker.admin.AppSettings;
import com.mylearn.tracker.common.web.ErrorResponse;
import com.mylearn.tracker.sync.SyncDtos.Pairing;
import com.mylearn.tracker.sync.SyncDtos.Ping;
import com.mylearn.tracker.sync.SyncDtos.SyncRequest;
import com.mylearn.tracker.sync.SyncDtos.SyncResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phone sync. {@code /api/sync} and {@code /api/sync/ping} need the pairing token and are the
 * only endpoints reachable from other devices ({@link RemoteAccessFilter}); the pairing details
 * are served to this computer only.
 */
@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final PhoneSyncService sync;
    private final AppSettings settings;
    private final Environment env;

    public SyncController(PhoneSyncService sync, AppSettings settings, Environment env) {
        this.sync = sync;
        this.settings = settings;
        this.env = env;
    }

    @PostMapping
    public SyncResponse sync(@RequestHeader(value = "Authorization", required = false) String auth,
                             @RequestBody SyncRequest request) {
        requireToken(auth);
        return sync.sync(request);
    }

    @GetMapping("/ping")
    public Ping ping(@RequestHeader(value = "Authorization", required = false) String auth) {
        requireToken(auth);
        return new Ping("learning-tracker", SyncDtos.PROTOCOL, sync.epoch());
    }

    @GetMapping("/pairing")
    public Pairing pairing() {
        return new Pairing(settings.syncToken(), port(), lanAddresses(), sync.devices());
    }

    /** A new token unpairs every phone; each must pair again. */
    @PostMapping("/pairing/token")
    public Pairing newToken() {
        settings.newSyncToken();
        return pairing();
    }

    private void requireToken(String auth) {
        String expected = "Bearer " + settings.syncToken();
        if (auth == null || !MessageDigest.isEqual(auth.getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException();
        }
    }

    static final class UnauthorizedException extends RuntimeException {
        UnauthorizedException() {
            super("Not paired: the sync token is missing or wrong. Pair the phone again from Settings → Phone sync.");
        }
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<ErrorResponse> unauthorized(UnauthorizedException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, "Unauthorized", e.getMessage(),
                List.of(), req.getRequestURI(), Instant.now()));
    }

    private int port() {
        String p = env.getProperty("local.server.port", env.getProperty("server.port", "8080"));
        return Integer.parseInt(p);
    }

    /** IPv4 addresses of this computer on its local networks (what the phone connects to). */
    static List<String> lanAddresses() {
        List<String> out = new ArrayList<>();
        try {
            for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!nic.isUp() || nic.isLoopback() || nic.isVirtual() || isVirtualAdapter(nic)) {
                    continue;
                }
                for (InetAddress a : Collections.list(nic.getInetAddresses())) {
                    if (a instanceof Inet4Address && a.isSiteLocalAddress()) {
                        out.add(a.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            // no interfaces readable: the page shows none and the user types the address
        }
        Collections.sort(out);
        return out;
    }

    /**
     * Hyper-V/WSL, Docker and VM adapters look like ordinary interfaces to Java but a phone can't
     * reach them; listing them would offer the wrong address in the pairing code.
     */
    static boolean isVirtualAdapter(NetworkInterface nic) {
        String names = (nic.getName() + " " + nic.getDisplayName()).toLowerCase(java.util.Locale.ROOT);
        for (String marker : List.of("vethernet", "hyper-v", "wsl", "docker", "virtualbox", "vmware", "vbox", "virtual")) {
            if (names.contains(marker)) {
                return true;
            }
        }
        return false;
    }
}
