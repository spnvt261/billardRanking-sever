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
public class EloHistoryResponse {
    private Long id;
    private Long workspaceId;
    private Long playerId;
    private Long matchId;
    private Long tournamentId;
    private Integer oldElo;
    private Integer eloChange;
    private Integer newElo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
