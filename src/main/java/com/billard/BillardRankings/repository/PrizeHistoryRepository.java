package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.PrizeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrizeHistoryRepository extends JpaRepository<PrizeHistory, Long>, JpaSpecificationExecutor<PrizeHistory> {
    List<PrizeHistory> findByWorkspaceId(Long workspaceId);

    Optional<PrizeHistory> findTopByWorkspaceIdAndPlayerIdOrderByIdDesc(Long workspaceId, Long playerId);
}
