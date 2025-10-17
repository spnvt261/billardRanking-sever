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
public class TournamentTeamRequest {
    
    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;
    
    @NotNull(message = "Team ID is required")
    private Long teamId;
    
    private LocalDateTime joinedAt;
    private Integer seedNumber;
    private Boolean isActive = true;
    private String note;
}
