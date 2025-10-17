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
public class MatchScoreEventResponse {
    private Long id;
    private Long workspaceId;
    private Long tournamentId;
    private Long matchId;
    private Long teamId;
    private Long playerId;
    private Integer rackNumber;
    private Integer pointsReceived;
//    private LocalDateTime recordedAt;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
