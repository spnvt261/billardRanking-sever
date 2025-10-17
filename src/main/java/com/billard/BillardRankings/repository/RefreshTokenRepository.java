package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.RefreshToken;
import com.billard.BillardRankings.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByWorkspace(Workspace workspace);
}
