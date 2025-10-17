package com.billard.BillardRankings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MatchDetailResponse {
    
    private Long matchId;
    private Long tournamentId;
    private String tournamentName;
    private String matchType;
    private String matchDate;
    private Integer scoreTeam1;
    private Integer scoreTeam2;
    private TeamDetail team1;
    private TeamDetail team2;
    private String winnerSide; // "team1", "team2", or null
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class TeamDetail {
        private Long id;
        private Long workspaceId;
        private String name;
        private List<PlayerInfo> players;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class PlayerInfo {
        private Long id;
        private String name;
    }
}
