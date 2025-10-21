package com.billard.BillardRankings.dto;

import com.billard.BillardRankings.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MatchResponse {
    private Long id;
    private Long workspaceId;
    private Long tournamentId;
    private Long team1Id;
    private Long team2Id;
    private TeamResponse team1;
    private TeamResponse team2;
    private Integer scoreTeam1;
    private Integer scoreTeam2;
    private Match.MatchType matchType;
    private Match.MatchCategory matchCategory;
    private BigDecimal betAmount;
    private String matchDate;
    private String note;
    private Long winnerId;
    private Match.MatchStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
