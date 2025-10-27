package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.MatchScoreEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MatchScoreEventRepository extends JpaRepository<MatchScoreEvent, Long>, JpaSpecificationExecutor<MatchScoreEvent> {
    List<MatchScoreEvent> findByWorkspaceId(Long workspaceId);
    @Query("SELECT DISTINCT m.matchId FROM MatchScoreEvent m WHERE m.matchId IN :matchIds")
    Set<Long> findDistinctMatchIdByMatchIdIn(@Param("matchIds") Set<Long> matchIds);

}
