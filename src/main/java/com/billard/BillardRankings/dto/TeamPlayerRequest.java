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
public class TeamPlayerRequest {
    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;
    @NotNull(message = "Team ID is required")
    private Long teamId;
    
    @NotNull(message = "Player ID is required")
    private Long playerId;
    
    private LocalDateTime joinedAt;
    private Boolean isCaptain = false;
}
