package com.billard.BillardRankings.dto;

import jakarta.validation.constraints.NotNull;
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
public class MatchScoreEventRequest {
    
    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;
    
    private Long tournamentId;
    
    @NotNull(message = "Match ID is required")
    private Long matchId;
    
    @NotNull(message = "Team ID is required")
    private Long teamId;
    
    private Long playerId;
    
    @NotNull(message = "Rack number is required")
    private Integer rackNumber;
    
    @NotNull(message = "Points received is required")
    private Integer pointsReceived;
    
//    private LocalDateTime recordedAt;
    private String note;
}
