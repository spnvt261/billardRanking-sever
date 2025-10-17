package com.billard.BillardRankings.service;

import com.billard.BillardRankings.entity.RefreshToken;
import com.billard.BillardRankings.entity.Workspace;
import com.billard.BillardRankings.exception.TokenRefreshException;
import com.billard.BillardRankings.repository.RefreshTokenRepository;
import com.billard.BillardRankings.repository.WorkspaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public RefreshToken createRefreshToken(String workspaceKey) {
        Workspace workspace = workspaceRepository.findByShareKey(workspaceKey)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        // 🔹 Xóa token cũ (nếu có)
        refreshTokenRepository.deleteByWorkspace(workspace);

        // 🔹 Tạo token mới
        RefreshToken refreshToken = RefreshToken.builder()
                .workspace(workspace)
                .workspaceKey(workspaceKey)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token expired. Please sign in again.");
        }
        return token;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found"));
    }
}
