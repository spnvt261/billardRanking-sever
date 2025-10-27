package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Match extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false, updatable = false, length = 36)
    private String uuid = UUID.randomUUID().toString();
    
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;
    
    @Column(name = "tournament_id")
    private Long tournamentId;
    
    @Column(name = "team1_id", nullable = false)
    private Long team1Id;
    
    @Column(name = "team2_id", nullable = false)
    private Long team2Id;
    
    @Column(name = "score_team1")
    private Integer scoreTeam1 = 0;
    
    @Column(name = "score_team2")
    private Integer scoreTeam2 = 0;

    @Column(name = "race_to")
    private Integer raceTo = null;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "match_type")
    private MatchType matchType = MatchType.GROUP;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "match_category")
    private MatchCategory matchCategory = MatchCategory.FUN;
    
    @Column(name = "bet_amount", precision = 12, scale = 2)
    private BigDecimal betAmount;
    
    @Column(name = "match_date")
    private String matchDate;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "round")
    private Integer round;

    @Column(name = "game_number")
    private Integer gameNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status = MatchStatus.FINISHED;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_round_type")
    private Tournament.TournamentType tournamentRoundType;

    @Column(name = "tournament_round")
    private Integer tournamentRound;
    
    @Column(name = "winner_id")
    private Long winnerId;



    // 🆕 Liên kết các trận kế tiếp
    @Column(name = "next_match_if_win")
    private Long nextMatchIfWin;

    @Column(name = "next_match_if_losses")
    private Long nextMatchIfLosses;

    @Column(name = "score_counter_lock_token")
    private String scoreCounterLockToken;

    @Column(name = "score_counter_locked_at")
    private LocalDateTime scoreCounterLockedAt;


    public enum MatchType {
        GROUP, QUARTERFINAL, SEMIFINAL, FINAL, THIRD_PLACE, LAST16, LAST32
    }
    
    public enum MatchCategory {
         FUN, TOURNAMENT, BETTING
    }

    public enum MatchStatus {
        ONGOING, FINISHED,NOT_STARTED,UPCOMING,PAUSED
    }
}
