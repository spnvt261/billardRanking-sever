package com.billard.BillardRankings.dto.roundType;

import lombok.Data;
import java.util.List;

@Data
public class RoundRobinRequest {
    private Long tournamentId;
    private int numGroups;
    private int roundNumber;
    private int roundPlayersAfter;
    private List<List<Long>> groupSelections;
}
