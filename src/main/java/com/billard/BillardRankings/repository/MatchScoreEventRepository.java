package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.MatchScoreEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchScoreEventRepository extends JpaRepository<MatchScoreEvent, Long>, JpaSpecificationExecutor<MatchScoreEvent> {
    List<MatchScoreEvent> findByWorkspaceId(Long workspaceId);
}
