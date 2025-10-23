package com.billard.BillardRankings.dto.roundType;

import com.billard.BillardRankings.dto.TeamResponse;
import com.billard.BillardRankings.entity.Tournament;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundRobinRankingResponse {
    // Mỗi bảng (A,B,C...) là 1 nhóm
    private Map<Integer, List<RoundRobinTeamResponse>> rankings;

        private Map<Tournament.TournamentType, List<TeamResponse>> otherTypesTeams;
}
