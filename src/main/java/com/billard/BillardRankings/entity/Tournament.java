package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Tournament extends BaseEntity {
    
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type", nullable = false)
    private TournamentType tournamentType;

    @Column(name = "round1_players_after")
    private Integer round1PlayersAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type_2")
    private TournamentType tournamentType2;

    @Column(name = "round2_players_after")
    private Integer round2PlayersAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type_3")
    private TournamentType tournamentType3;
    
    @Column(name = "start_date", nullable = false)
    private String startDate;
    
    @Column(name = "end_date")
    private String endDate;
    
    @Column(name = "location", length = 255)
    private String location;
    
    @Column(name = "prize", nullable = false, length = 100)
    private Integer prize;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "runner_up_id")
    private Long runnerUpId;

    @Column(name = "third_place_id")
    private Long thirdPlaceId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;
    
    @Column(name = "banner", length = 200)
    private String banner;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private TournamentFormat format = TournamentFormat.SINGLE;
    
    public enum TournamentType {
        ROUND_ROBIN, SINGLE_ELIMINATION, DOUBLE_ELIMINATION, CUSTOM, SWEDISH, SPECIAL_DEN
    }
    
    public enum TournamentStatus {
        ONGOING, UPCOMING, FINISHED, PAUSED
    }

    public enum TournamentFormat {
        SINGLE, DOUBLES
    }
}
