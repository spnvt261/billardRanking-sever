package com.billard.BillardRankings.dto;

import com.billard.BillardRankings.entity.Match;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MatchRequest {
    
    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;
    
    private Long tournamentId;
    
//    @NotNull(message = "Team 1 ID is required")
    private Long team1Id;
    
//    @NotNull(message = "Team 2 ID is required")
    private Long team2Id;

    // ✅ Thay vào đó truyền list player ID
    @NotNull(message = "Team 1 is required")
    private List<Long> team1Players;

    @NotNull(message = "Team 2 is required")
    private List<Long> team2Players;

    @NotNull(message = "Score is required")
    private Integer scoreTeam1 = 0;

    @NotNull(message = "Score is required")
    private Integer scoreTeam2 = 0;
    
    private Match.MatchType matchType = Match.MatchType.GROUP;
    private Match.MatchCategory matchCategory = Match.MatchCategory.TOURNAMENT;
    
    private BigDecimal betAmount;
    
//    @NotNull(message = "Match date is required")
    private String matchDate;
    
    private String note;
    private Long winnerId;
}
