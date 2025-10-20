package com.billard.BillardRankings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PlayerResponse {
    private Long id;
    private Long workspaceId;
    private String name;
    private String nickname;
    private String avatarUrl;
    private Integer elo;
    private String description;
    private LocalDate joinedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer rank;
    private Integer seedNumber;
}
