package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchResponse;
import com.billard.BillardRankings.dto.MatchScoreEventRequest;
import com.billard.BillardRankings.dto.MatchScoreEventResponse;

public interface MatchScoreEventService extends CrudService<Long, MatchScoreEventRequest, MatchScoreEventResponse> {
    ListResponse<MatchScoreEventResponse> findAll(int page,
    int size,
    String sort,
    String filter,
    String search,
    boolean all,
    Long workspaceId,
    Long matchId
    );
    void endMatch(Long id,String token);
    void pauseMatch(Long id,String token);
}
