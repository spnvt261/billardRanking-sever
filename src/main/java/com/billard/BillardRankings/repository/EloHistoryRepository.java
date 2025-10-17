package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.EloHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EloHistoryRepository extends JpaRepository<EloHistory, Long>, JpaSpecificationExecutor<EloHistory> {
    List<EloHistory> findByWorkspaceId(Long workspaceId);

    // ✅ Lấy lịch sử mới nhất của một player
    Optional<EloHistory> findFirstByPlayerIdOrderByIdDesc(Long playerId);
}
