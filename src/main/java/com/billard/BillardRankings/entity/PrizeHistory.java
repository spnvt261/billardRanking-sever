package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "prize_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PrizeHistory extends BaseEntity {

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "tournament_id")
    private Long tournamentId;

    @Column(name = "old_prize", nullable = false)
    private Integer oldPrize;

    @Column(name = "prize_change", nullable = false)
    private Integer prizeChange;

    @Column(name = "new_prize", nullable = false)
    private Integer newPrize;

}
