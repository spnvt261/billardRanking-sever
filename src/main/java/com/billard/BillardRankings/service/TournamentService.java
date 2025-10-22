package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.MatchResponse;
import com.billard.BillardRankings.dto.TournamentRequest;
import com.billard.BillardRankings.dto.TournamentResponse;
import com.billard.BillardRankings.dto.roundType.RoundRobinRankingResponse;
import com.billard.BillardRankings.dto.roundType.RoundRobinRequest;

import java.util.List;
import java.util.Map;

public interface TournamentService extends CrudService<Long, TournamentRequest, TournamentResponse> {
    Map<String, Object> getAllTournamentsGroupedByQuarter(Long workspaceId);
    List<MatchResponse>  createRoundRobin(RoundRobinRequest request, Long workspaceId);
    RoundRobinRankingResponse getRoundRobinRankings(Long tournamentId, Long workspaceId, int roundNumber);


}
