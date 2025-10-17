package com.billard.BillardRankings.dto;

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
public class TeamPlayerResponse {
    private Long id;
    private Long workspaceId;
    private Long teamId;
    private Long playerId;
    private LocalDateTime joinedAt;
    private Boolean isCaptain;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
