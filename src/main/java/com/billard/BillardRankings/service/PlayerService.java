package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.PlayerListResponse;
import com.billard.BillardRankings.dto.PlayerRequest;
import com.billard.BillardRankings.dto.PlayerResponse;

import java.util.List;

public interface PlayerService extends CrudService<Long, PlayerRequest, PlayerResponse> {
    List<PlayerListResponse> findAllSimple(Long workspaceId);
}
