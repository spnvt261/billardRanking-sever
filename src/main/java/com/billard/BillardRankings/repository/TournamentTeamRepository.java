package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, Long>, JpaSpecificationExecutor<TournamentTeam> {
    List<TournamentTeam> findByTournamentId(Long tournamentId);
    List<TournamentTeam> findByTeamId(Long teamId);
    int countByTournamentId(Long tournamentId);
    List<TournamentTeam> findByTournamentIdIn(List<Long> tournamentIds);

}
