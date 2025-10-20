package com.billard.BillardRankings.dto;

import com.billard.BillardRankings.entity.Tournament;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TournamentRequest {
    
    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;
    
    @NotNull(message = "Tournament type is required")
    private Tournament.TournamentType tournamentType;

    private Integer round1PlayersAfter;

    private Tournament.TournamentType tournamentType2;

    private Integer round2PlayersAfter;

    private Tournament.TournamentType tournamentType3;
    
    @NotNull(message = "Start date is required")
    private String startDate;
    
    private String endDate;
    
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
//
    @NotBlank(message = "Prize is required")
    @Size(max = 100, message = "Prize must not exceed 100 characters")
    private Integer prize;
    
    private Long winnerId;
    private Long runnerUpId;
    private Long thirdPlaceId;
    
    private String description;
    private String rules;
    
    @Size(max = 200, message = "Banner must not exceed 200 characters")
    private String banner;
    
    private Tournament.TournamentStatus status;

    private Tournament.TournamentFormat format;

    // ✅ Thêm list playerIds
    private List<Long> playerIds;
}
