package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_teams", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "team_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TournamentTeam extends BaseEntity {
    
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;
    
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "tournament_round")
    private Integer tournamentRound = 1;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
