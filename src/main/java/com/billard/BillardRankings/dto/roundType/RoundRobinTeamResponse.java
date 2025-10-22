package com.billard.BillardRankings.dto.roundType;

import com.billard.BillardRankings.dto.TeamResponse;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoundRobinTeamResponse {
    private TeamResponse team;
    private int wins;
    private int losses;
    private int ties;
    private List<String> recentResults;
    private int matchesPlayed; // số trận đã đấu
    private int matchesTotal;  // tổng số trận phải đấu
}
