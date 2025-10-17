package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.entity.Workspace;
import com.billard.BillardRankings.repository.WorkspaceRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final WorkspaceRepository workspaceRepository;

    public TestController(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "BillardRankings API is running");
        return response;
    }

    @PostMapping("/create-workspace")
    public Map<String, Object> createWorkspace(@RequestBody WorkspaceCreateRequest request) {
        try {
            Workspace workspace = Workspace.builder()
                    .name(request.getName())
                    .passwordHash(com.billard.BillardRankings.util.PasswordUtils.encode(request.getPassword()))
                    .shareKey(request.getShareKey().toString())
                    .build();

            workspace = workspaceRepository.save(workspace);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("workspaceId", workspace.getId());
            response.put("message", "Workspace created successfully");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create workspace: " + e.getMessage());
            return response;
        }
    }

    public static class WorkspaceCreateRequest {
        private String name;
        private String password;
        private Long shareKey;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Long getShareKey() {
            return shareKey;
        }

        public void setShareKey(Long shareKey) {
            this.shareKey = shareKey;
        }
    }

    @PostMapping("/check-share-key")
    public Map<String, Object> checkShareKey(@RequestBody ShareKeyCheckRequest request) {
        boolean exists = workspaceRepository.existsByShareKey(request.getShareKey().toString());
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        
        if (exists) {
            workspaceRepository.findByShareKey(request.getShareKey().toString()).ifPresent(workspace -> {
                Map<String, Object> workspaceInfo = new HashMap<>();
                workspaceInfo.put("id", workspace.getId());
                workspaceInfo.put("name", workspace.getName());
                workspaceInfo.put("shareKey", workspace.getShareKey());
                response.put("workspace", workspaceInfo);
            });
        }
        
        return response;
    }

    @PostMapping("/login-workspace")
    public Map<String, Object> loginWorkspace(@RequestBody WorkspaceLoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Workspace workspace = workspaceRepository.findByShareKey(request.getShareKey().toString())
                    .orElse(null);
            
            if (workspace == null) {
                response.put("success", false);
                response.put("message", "Workspace not found");
                return response;
            }
            
            // Verify password
            boolean passwordMatches = com.billard.BillardRankings.util.PasswordUtils.matches(request.getPassword(), workspace.getPasswordHash());
            
            if (passwordMatches) {
                response.put("success", true);
                response.put("message", "Login successful");
                Map<String, Object> workspaceInfo = new HashMap<>();
                workspaceInfo.put("id", workspace.getId());
                workspaceInfo.put("name", workspace.getName());
                workspaceInfo.put("shareKey", workspace.getShareKey());
                response.put("workspace", workspaceInfo);
            } else {
                response.put("success", false);
                response.put("message", "Invalid password");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
        }
        
        return response;
    }

    public static class ShareKeyCheckRequest {
        private Long shareKey;

        public Long getShareKey() {
            return shareKey;
        }

        public void setShareKey(Long shareKey) {
            this.shareKey = shareKey;
        }
    }

    public static class WorkspaceLoginRequest {
        private Long shareKey;
        private String password;

        public Long getShareKey() {
            return shareKey;
        }

        public void setShareKey(Long shareKey) {
            this.shareKey = shareKey;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
