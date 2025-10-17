package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.dto.LoginRequest;
import com.billard.BillardRankings.dto.LoginResponse;
import com.billard.BillardRankings.dto.TokenRefreshRequest;
import com.billard.BillardRankings.dto.WorkspaceDTO;
import com.billard.BillardRankings.entity.RefreshToken;
import com.billard.BillardRankings.service.JwtService;
import com.billard.BillardRankings.service.RefreshTokenService;
import com.billard.BillardRankings.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceService workspaceService; // Thêm WorkspaceService

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // Validate workspace and credentials
        WorkspaceDTO workspace = workspaceService.validateWorkspaceAccess(
            request.getWorkspaceKey(), 
            request.getPassword()
        );
        
        if (workspace == null) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(request.getWorkspaceKey());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getWorkspaceKey());

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .workspace(workspace)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        
        String accessToken = jwtService.generateToken(refreshToken.getWorkspaceKey());
        WorkspaceDTO workspace = workspaceService.getWorkspaceByShareKey(refreshToken.getWorkspaceKey());

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .workspace(workspace)
                .build());
    }
}