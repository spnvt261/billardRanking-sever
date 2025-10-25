package com.billard.BillardRankings.config;

import com.billard.BillardRankings.service.JwtService;
import com.billard.BillardRankings.repository.WorkspaceRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final WorkspaceRepository workspaceRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // GET thì không filter
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }

        // POST / PUT / DELETE → bỏ qua auth và test
        // Các endpoint public khác
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/test/")
                || path.startsWith("/api/images/")
                || path.startsWith("/api/match-score-events")
                || path.matches("^/api/matches/(uuid/[^/]+/)?(lock-score-counter|refresh-score-counter-lock|unlock-score-counter|verify-score-counter-token|create-score-counter)$");

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtService.validateToken(token)) {
                    String workspaceKey = jwtService.getWorkspaceKeyFromToken(token);

                    // Resolve workspace id from workspaceKey and attach to Authentication details
                    Long authWorkspaceId = null;
                    try {
                        workspaceRepository.findByShareKey(workspaceKey).ifPresent(ws -> {
                            // set id in outer variable via array trick
                        });
                        // use a direct lookup
                        authWorkspaceId = workspaceRepository.findByShareKey(workspaceKey).map(w -> w.getId()).orElse(null);
                    } catch (Exception ignored) {
                        // If workspace can't be resolved, leave authWorkspaceId null. Authorization filter will handle it.
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            workspaceKey,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                    // Attach the resolved workspace id (DB id) into details for downstream checks
                    authentication.setDetails(authWorkspaceId);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                // JWT thiếu → trả 401 JSON
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"JWT token is missing or invalid\"}");
                return;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"JWT validation failed\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
