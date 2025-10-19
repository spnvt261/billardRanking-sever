package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.TournamentRequest;
import com.billard.BillardRankings.dto.TournamentResponse;

import java.util.List;
import java.util.Map;

public interface TournamentService extends CrudService<Long, TournamentRequest, TournamentResponse> {
    Map<String, Object> getAllTournamentsGroupedByQuarter(Long workspaceId);

}
