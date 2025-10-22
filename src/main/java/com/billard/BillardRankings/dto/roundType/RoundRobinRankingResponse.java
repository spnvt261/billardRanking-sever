package com.billard.BillardRankings.dto.roundType;

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
}
