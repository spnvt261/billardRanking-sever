package com.billard.BillardRankings.dto.roundType;

import com.billard.BillardRankings.entity.Tournament;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtherRoundTypeRequest {

    @NotNull(message = "Tournament ID cannot be null")
    private Long tournamentId;

    @NotNull(message = "Round number cannot be null")
    private Integer roundNumber;

    @NotNull(message = "Tournament round type cannot be null")
    private Tournament.TournamentType roundType;

    @NotNull(message = "Game number played cannot be null")
    private Integer gameNumberPlayed;

    @NotNull(message = "Round players after cannot be null")
    private Integer roundPlayersAfter;

    @NotNull(message = "List of player IDs cannot be null")
    private List<Long> listPlayerIds;
}
