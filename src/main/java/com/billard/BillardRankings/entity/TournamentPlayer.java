package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_players", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "player_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TournamentPlayer extends BaseEntity {
    
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;
    
    @Column(name = "player_id", nullable = false)
    private Long playerId;
    
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
    
    @Column(name = "seed_number")
    private Integer seedNumber;

    @Column(name = "rank_current")
    private Integer rankCurrent;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
