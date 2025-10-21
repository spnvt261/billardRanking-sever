package com.billard.BillardRankings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PrizeHistoryResponse {
    private Long id;
    private Long workspaceId;
    private Long playerId;
    private Long matchId;
    private Long tournamentId;
    private Integer oldPrize;
    private Integer prizeChange;
    private Integer newPrize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
