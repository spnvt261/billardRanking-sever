package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.PlayerRequest;
import com.billard.BillardRankings.dto.PlayerResponse;

public interface PlayerService extends CrudService<Long, PlayerRequest, PlayerResponse> {
}
