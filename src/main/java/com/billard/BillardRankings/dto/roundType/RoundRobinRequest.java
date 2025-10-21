package com.billard.BillardRankings.dto.roundType;

import com.billard.BillardRankings.entity.Tournament;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RoundRobinRequest {
    @NotNull( message = "Tournament ID not null")
    private Long tournamentId;

    @NotNull( message = "numgroup not null")
    private int numGroups;

    @NotNull( message = "roundnumber not null")
    private int roundNumber;

//    @NotNull( message = "tournamentRoundType not null")
//    private Tournament.TournamentType tournamentRoundType;

    @NotNull( message = "gamenumber not null")
    private int gameNumberPlayed;

    @NotNull( message = "round player after not null")
    private int roundPlayersAfter;

    @NotNull( message = "list data not null")
    private List<List<Long>> groupSelections;
}
