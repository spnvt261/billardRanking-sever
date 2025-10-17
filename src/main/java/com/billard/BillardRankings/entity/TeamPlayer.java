package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TeamPlayer extends BaseEntity {
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;
    @Column(name = "team_id", nullable = false)
    private Long teamId;
    
    @Column(name = "player_id", nullable = false)
    private Long playerId;
    
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
    
    @Column(name = "is_captain")
    private Boolean isCaptain = false;
}
