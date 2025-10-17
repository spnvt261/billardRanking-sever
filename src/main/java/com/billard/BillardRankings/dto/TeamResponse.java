package com.billard.BillardRankings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TeamResponse {
    private Long id;
    private Long workspaceId;
    private String teamName;
    private List<PlayerResponse> players;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
