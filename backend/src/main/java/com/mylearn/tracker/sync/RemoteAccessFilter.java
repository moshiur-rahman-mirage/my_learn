package com.mylearn.tracker.sync;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * The app has no login, so only this computer may use it. Other devices (the phone on the same
 * Wi-Fi) may reach the token-protected sync endpoints and nothing else.
 *
 * <p>{@code app.remote-access=all} turns this off, for setups where every request arrives through
 * a proxy on another address (Docker's nginx), which then decides who may connect.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RemoteAccessFilter extends OncePerRequestFilter {

    static final Set<String> REMOTE_PATHS = Set.of("/api/sync", "/api/sync/ping");

    private final boolean restrict;

    public RemoteAccessFilter(@Value("${app.remote-access:sync-only}") String mode) {
        this.restrict = !"all".equalsIgnoreCase(mode.strip());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!restrict || isLocal(request.getRemoteAddr()) || REMOTE_PATHS.contains(path(request))) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":"
                + "\"Only phone sync is available from other devices. Open the app on the computer it runs on.\"}");
    }

    /** The decoded, normalized path (getRequestURI is raw and could hide "/../"). */
    private static String path(HttpServletRequest request) {
        String path = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
        return path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    static boolean isLocal(String address) {
        if (address == null) {
            return false;
        }
        try {
            return InetAddress.getByName(address).isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
