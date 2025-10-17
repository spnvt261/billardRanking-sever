package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.TournamentPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentPlayerRepository extends JpaRepository<TournamentPlayer, Long>, JpaSpecificationExecutor<TournamentPlayer> {
    List<TournamentPlayer> findByTournamentId(Long tournamentId);
    List<TournamentPlayer> findByPlayerId(Long playerId);
    void deleteByTournamentId(Long tournamentId);
}
