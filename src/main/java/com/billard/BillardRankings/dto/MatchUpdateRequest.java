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
public class MatchUpdateRequest {

    private Integer scoreTeam1 = 0;

    private Integer scoreTeam2 = 0;

    private Integer raceTo;

    private Match.MatchType matchType;
    private Match.MatchCategory matchCategory;

    private Match.MatchStatus status;

    private BigDecimal betAmount;

    private String note;
    private Long winnerId;
}
