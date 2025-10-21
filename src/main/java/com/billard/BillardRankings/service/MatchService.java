package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.MatchRequest;
import com.billard.BillardRankings.dto.MatchResponse;

import java.util.List;

public interface MatchService extends CrudService<Long, MatchRequest, MatchResponse> {
    List<MatchResponse> findByTournamentAndRound(Long tournamentId, int roundNumber, Long workspaceId);
}
