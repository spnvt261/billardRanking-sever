package com.billard.BillardRankings.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PrizeHistoryRequest {

    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;

    @NotNull(message = "Player ID is required")
    private Long playerId;

    private Long matchId;

    private Long tournamentId;

    @NotNull(message = "Prize change is required")
    private Integer prizeChange;

}
