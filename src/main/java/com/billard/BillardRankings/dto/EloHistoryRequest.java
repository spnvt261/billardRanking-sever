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
public class EloHistoryRequest {
    
    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;
    
    @NotNull(message = "Player ID is required")
    private Long playerId;
    
//    @NotNull(message = "Match ID is required")
    private Long matchId;

    private Long tournamentId;
    
    @NotNull(message = "Old ELO is required")
    private Integer oldElo;
    
    @NotNull(message = "ELO change is required")
    private Integer eloChange;
    
    @NotNull(message = "New ELO is required")
    private Integer newElo;

}
