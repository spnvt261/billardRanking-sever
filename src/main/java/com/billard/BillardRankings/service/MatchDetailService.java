package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchDetailResponse;

public interface MatchDetailService {
    
    ListResponse<MatchDetailResponse> getMatchDetails(Long workspaceId, int page, int size, String sort, String filter, String search, boolean all);
    
    MatchDetailResponse getMatchDetailById(Long matchId, Long workspaceId);
}
