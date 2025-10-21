package com.billard.BillardRankings.service.impl;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchRequest;
import com.billard.BillardRankings.dto.MatchResponse;
import com.billard.BillardRankings.dto.TeamResponse;
import com.billard.BillardRankings.entity.Match;
import com.billard.BillardRankings.entity.Team;
import com.billard.BillardRankings.entity.TeamPlayer;
import com.billard.BillardRankings.exception.ResourceNotFoundException;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.MatchMapper;
import com.billard.BillardRankings.mapper.PlayerMapper;
import com.billard.BillardRankings.repository.MatchRepository;
import com.billard.BillardRankings.repository.PlayerRepository;
import com.billard.BillardRankings.repository.TeamPlayerRepository;
import com.billard.BillardRankings.repository.TeamRepository;
import com.billard.BillardRankings.service.BaseCrudServiceImpl;
import com.billard.BillardRankings.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl extends BaseCrudServiceImpl<Match, MatchRequest, MatchResponse, Long>
        implements MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final MatchMapper matchMapper;
    private final PlayerMapper playerMapper;

    // ------------------- Validation Logic -------------------
    @Override
    public ListResponse<MatchResponse> findAll(int page, int size, String sort, String filter, String search, boolean all, Long workspaceId) {
        // Gọi logic mặc định từ BaseCrudServiceImpl
        ListResponse<MatchResponse> response = super.findAll(page, size, sort, filter, search, all, workspaceId);

        // Nội dung gốc trên trang hiện tại
        List<MatchResponse> originalContent = response.getContent();

        // Lọc các trận đấu có status khác NOT_STARTED
        List<MatchResponse> filteredContent = originalContent.stream()
                .filter(match -> match.getStatus() != Match.MatchStatus.NOT_STARTED)
                .toList();

        // Tính số phần tử bị loại trên trang hiện tại
        int removedOnPage = originalContent.size() - filteredContent.size();

        // Tính totalElements mới (không âm)
        long originalTotalElements = response.getTotalElements();
        long newTotalElements = Math.max(0L, originalTotalElements - removedOnPage);

        // Tính totalPages mới (cẩn trọng khi size == 0)
        int pageSize = response.getSize() <= 0 ? size : response.getSize();
        int newTotalPages = pageSize > 0 ? (int) Math.ceil((double) newTotalElements / pageSize) : 0;

        return new ListResponse<>(
                filteredContent,
                response.getPage(),
                pageSize,
                newTotalElements,
                newTotalPages,
                response.isLast()
        );
    }

    @Override
    @Transactional
    public MatchResponse save(MatchRequest request) {
        Long workspaceId = request.getWorkspaceId();

        // ✅ Tạo team1 nếu có danh sách player
        Long team1Id = request.getTeam1Id();
        if (team1Id == null && request.getTeam1Players() != null && !request.getTeam1Players().isEmpty()) {
            team1Id = createTeamWithPlayers(request.getTeam1Players(), workspaceId, "Team 1");
        }

        // ✅ Tạo team2 nếu có danh sách player
        Long team2Id = request.getTeam2Id();
        if (team2Id == null && request.getTeam2Players() != null && !request.getTeam2Players().isEmpty()) {
            team2Id = createTeamWithPlayers(request.getTeam2Players(), workspaceId, "Team 2");
        }

        // ✅ Map sang entity & gán teamId
        Match match = matchMapper.requestToEntity(request);
        match.setTeam1Id(team1Id);
        match.setTeam2Id(team2Id);

        // ✅ Xác định winnerId dựa vào điểm số
        Integer score1 = request.getScoreTeam1() != null ? request.getScoreTeam1() : 0;
        Integer score2 = request.getScoreTeam2() != null ? request.getScoreTeam2() : 0;

        if (score1 > score2) {
            match.setWinnerId(team1Id);
        } else if (score2 > score1) {
            match.setWinnerId(team2Id);
        } else {
            match.setWinnerId(null); // Hòa
        }

        // ✅ Lưu match
        match = matchRepository.save(match);
        return matchMapper.entityToResponse(match);
    }


    private Long createTeamWithPlayers(List<Long> playerIds, Long workspaceId, String teamNamePrefix) {
        // ✅ Tạo team
        Team team = new Team();
        team.setTeamName(teamNamePrefix + " - " + System.currentTimeMillis());
        team.setWorkspaceId(workspaceId);
        team = teamRepository.save(team);

        // ✅ Lấy danh sách player từ DB
        var players = playerRepository.findAllById(playerIds);
        if (players.size() != playerIds.size()) {
            throw new IllegalArgumentException("Some player IDs do not exist.");
        }

        // ✅ Kiểm tra workspace hợp lệ
        boolean hasInvalidPlayer = players.stream()
                .anyMatch(p -> !p.getWorkspaceId().equals(workspaceId));
        if (hasInvalidPlayer) {
            throw new IllegalArgumentException("All players must belong to the same workspace as the match.");
        }

        // ✅ Gắn player cho team
        for (var player : players) {
            TeamPlayer teamPlayer = new TeamPlayer();
            teamPlayer.setTeamId(team.getId());
            teamPlayer.setPlayerId(player.getId());
            teamPlayer.setWorkspaceId(workspaceId);
            teamPlayerRepository.save(teamPlayer);
        }

        return team.getId();
    }



    // ------------------- Xây dựng dữ liệu chi tiết -------------------

    public List<MatchResponse> getAllMatchesWithTeams() {
        var matches = matchRepository.findAll();
        var responses = new ArrayList<MatchResponse>();

        for (var match : matches) {
            var response = matchMapper.entityToResponse(match);
            response.setTeam1(buildTeamResponse(match.getTeam1Id()));
            response.setTeam2(buildTeamResponse(match.getTeam2Id()));
            responses.add(response);
        }
        return responses;
    }

    private TeamResponse buildTeamResponse(Long teamId) {
        if (teamId == null) return null;
        var teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) return null;

        var team = teamOpt.get();
        List<TeamPlayer> teamPlayers = teamPlayerRepository.findByTeamId(teamId);
        List<Long> playerIds = teamPlayers.stream().map(TeamPlayer::getPlayerId).toList();

        var players = playerRepository.findAllById(playerIds);
        var playerResponses = players.stream()
                .map(playerMapper::entityToResponse)
                .toList();

        return new TeamResponse()
                .setId(team.getId())
                .setTeamName(team.getTeamName())
                .setPlayers(playerResponses);
    }

    // ------------------- Các hàm abstract -------------------

    @Override
    protected JpaRepository<Match, Long> getRepository() {
        return matchRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Match> getSpecificationRepository() {
        return matchRepository;
    }

    @Override
    protected GenericMapper<Match, MatchRequest, MatchResponse> getMapper() {
        return matchMapper;
    }

    @Override
    protected String getResourceName() {
        return "Match";
    }

    @Override
    protected List<String> getSearchFields() {
        return List.of("note", "matchType", "matchCategory");
    }

    @Override
    protected Long getWorkspaceIdFromEntity(Match entity) {
        return entity.getWorkspaceId();
    }

    @Override
    protected Long getWorkspaceIdFromRequest(MatchRequest request) {
        return request.getWorkspaceId();
    }

    @Override
    protected Long getIdFromEntity(Match entity) {
        return entity.getId();
    }
}
