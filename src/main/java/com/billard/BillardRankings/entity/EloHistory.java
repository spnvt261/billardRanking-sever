package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(name = "elo_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EloHistory extends BaseEntity {
    
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;
    
    @Column(name = "player_id", nullable = false)
    private Long playerId;
    
    @Column(name = "match_id", nullable = true)
    private Long matchId;

    @Column(name = "tournament_id")
    private Long tounamentId;
    
    @Column(name = "old_elo", nullable = false)
    private Integer oldElo;
    
    @Column(name = "elo_change", nullable = false)
    private Integer eloChange;
    
    @Column(name = "new_elo", nullable = false)
    private Integer newElo;

}
