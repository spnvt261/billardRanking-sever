package com.billard.BillardRankings.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Filter để kiểm tra quyền workspace: so sánh workspaceId được request (query/body) với workspaceId từ token.
 * Nếu mismatch → trả 403.
 */
public class WorkspaceAuthorizationFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // Chỉ áp dụng cho non-GET
        if ("GET".equalsIgnoreCase(method)) return true;

        // Bỏ qua auth endpoints
        return path.startsWith("/api/auth/") || path.startsWith("/api/test/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Wrap request so we can safely read the body without consuming it for downstream
        HttpServletRequest requestToUse = request;
        if (isJsonContent(request)) {
            try {
                requestToUse = new CachedBodyHttpServletRequest(request);
            } catch (Exception ignored) {
                // fallback to original request
                requestToUse = request;
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            // Nếu chưa có authentication, JwtAuthenticationFilter sẽ trả 401 trước đó. Chỉ để an toàn.
            filterChain.doFilter(requestToUse, response);
            return;
        }

        Object details = auth.getDetails();
        Long authWorkspaceId = details instanceof Long ? (Long) details : null;

        // Lấy workspaceId từ query param
        String workspaceIdParam = requestToUse.getParameter("workspaceId");
        Long requestedWorkspaceId = null;

        if (workspaceIdParam != null) {
            try {
                requestedWorkspaceId = Long.parseLong(workspaceIdParam);
            } catch (NumberFormatException ignored) {
            }
        }

        // Nếu không có ở query, thử đọc body JSON (thường POST/PUT) để tìm workspaceId
        if (requestedWorkspaceId == null && isJsonContent(requestToUse)) {
            String body = readBody(requestToUse);
            if (body != null && !body.isBlank()) {
                try {
                    JsonNode root = mapper.readTree(body);
                    if (root.has("workspaceId")) {
                        requestedWorkspaceId = root.get("workspaceId").asLong();
                    }
                } catch (Exception ignored) {
                }
            }
        }

        // Nếu requestedWorkspaceId tồn tại và authWorkspaceId khác nhau → 403
        if (requestedWorkspaceId != null && authWorkspaceId != null && !authWorkspaceId.equals(requestedWorkspaceId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Forbidden: token does not belong to requested workspace\"}");
            return;
        }

        filterChain.doFilter(requestToUse, response);
    }

    private boolean isJsonContent(HttpServletRequest request) {
        String ct = request.getContentType();
        return ct != null && (ct.contains(MediaType.APPLICATION_JSON_VALUE) || ct.contains("+json"));
    }

    private String readBody(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            return null;
        }
    }
}
