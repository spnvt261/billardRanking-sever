package com.billard.BillardRankings.dto;

import com.billard.BillardRankings.entity.Tournament;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TournamentResponse {
    private Long id;
    private Long workspaceId;
    private String name;
    private Tournament.TournamentType tournamentType;
    private Integer round1PlayersAfter;
    private Tournament.TournamentRoundStatus round1Status;
    private Tournament.TournamentType tournamentType2;
    private Integer round2PlayersAfter;
    private Tournament.TournamentRoundStatus round2Status;
    private Tournament.TournamentType tournamentType3;
    private Tournament.TournamentRoundStatus round3Status;
    private String startDate;
    private String endDate;
    private String location;
    private Integer prize;

    // Các khóa ngoại (giữ ID để dễ debug hoặc dùng khi cần)
    private Long winnerId;
    private Long runnerUpId;
    private Long thirdPlaceId;

    // Các object chi tiết
    private PlayerResponse winner;
    private PlayerResponse runnerUp;
    private PlayerResponse thirdPlace;

    private String description;
    private String rules;
    private String banner;
    private Tournament.TournamentStatus status;
    private Tournament.TournamentFormat format;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer numberAttend;
    private Integer numberTeams;
    // ✅ Thêm danh sách người chơi tham dự
//    private List<PlayerResponse> listPlayer;
//    private List<TeamResponse> listTeam; // ✅ thay cho listPlayer
    private Map<Integer, List<TeamResponse>> listTeamByRound;

}
