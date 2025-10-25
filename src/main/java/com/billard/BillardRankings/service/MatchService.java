package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.MatchRequest;
import com.billard.BillardRankings.dto.MatchResponse;

import java.util.List;

public interface MatchService extends CrudService<Long, MatchRequest, MatchResponse> {
    List<MatchResponse> findByTournamentAndRound(Long tournamentId, int roundNumber, Long workspaceId);
    MatchResponse findByUuid(String uuid, Long workspaceId);
    String lockScoreCounterByUuid(String uuid, Long workspaceId,int raceTo);

    void refreshScoreCounterLockByUuid(String uuid, Long workspaceId, String token);

    void unlockScoreCounterByUuid(String uuid, Long workspaceId, String token);
    boolean verifyScoreCounterToken(String uuid, String token);
    MatchResponse createScoreCounter(MatchRequest request);

}
