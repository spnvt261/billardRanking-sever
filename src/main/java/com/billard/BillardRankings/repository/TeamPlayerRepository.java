package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Long>, JpaSpecificationExecutor<TeamPlayer> {
    List<TeamPlayer> findByTeamId(Long teamId);
    List<TeamPlayer> findByPlayerId(Long playerId);
}
