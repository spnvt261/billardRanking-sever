package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.TournamentPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentPlayerRepository extends JpaRepository<TournamentPlayer, Long>, JpaSpecificationExecutor<TournamentPlayer> {
    List<TournamentPlayer> findByTournamentId(Long tournamentId);
    List<TournamentPlayer> findByPlayerId(Long playerId);
    void deleteByTournamentId(Long tournamentId);
    // ✅ Thêm dòng này để tìm 1 player cụ thể trong 1 giải
    Optional<TournamentPlayer> findByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
    int countByTournamentId(Long tournamentId);
    List<TournamentPlayer> findByTournamentIdIn(List<Long> tournamentIds);

}
