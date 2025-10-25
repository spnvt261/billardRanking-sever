package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_score_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MatchScoreEvent extends BaseEntity {
    
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;
    
    @Column(name = "tournament_id")
    private Long tournamentId;
    
    @Column(name = "match_id")
    private Long matchId;
    
    @Column(name = "team_id", nullable = false)
    private Long teamId;
    
    @Column(name = "player_id")
    private Long playerId;
    
    @Column(name = "rack_number", nullable = false)
    private Integer rackNumber;
    
    @Column(name = "points_received", nullable = false)
    private Integer pointsReceived;
    
//    @Column(name = "recorded_at")
//    private LocalDateTime recordedAt;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
