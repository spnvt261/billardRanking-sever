package com.billard.BillardRankings.service.impl;

import com.billard.BillardRankings.constant.ResourceName;
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
    @Transactional
    public MatchResponse save(MatchRequest request) {
        Long workspaceId = request.getWorkspaceId();

        // ✅ Kiểm tra team1
        if (request.getTeam1Id() != null) {
            Team team1 = teamRepository.findById(request.getTeam1Id())
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceName.TEAM, request.getTeam1Id()));
            if (!team1.getWorkspaceId().equals(workspaceId)) {
                throw new IllegalArgumentException("Team 1 does not belong to the specified workspace.");
            }
        }

        // ✅ Kiểm tra team2
        if (request.getTeam2Id() != null) {
            Team team2 = teamRepository.findById(request.getTeam2Id())
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceName.TEAM, request.getTeam2Id()));
            if (!team2.getWorkspaceId().equals(workspaceId)) {
                throw new IllegalArgumentException("Team 2 does not belong to the specified workspace.");
            }
        }

        // ✅ Nếu cả hai đều tồn tại, kiểm tra cùng workspace với nhau (đề phòng lỗi logic)
        if (request.getTeam1Id() != null && request.getTeam2Id() != null) {
            Team team1 = teamRepository.findById(request.getTeam1Id()).get();
            Team team2 = teamRepository.findById(request.getTeam2Id()).get();

            if (!team1.getWorkspaceId().equals(team2.getWorkspaceId())) {
                throw new IllegalArgumentException("Team 1 and Team 2 must belong to the same workspace.");
            }
        }

        // ✅ Nếu qua hết -> map & lưu như cũ
        Match match = matchMapper.requestToEntity(request);
        match = matchRepository.save(match);
        return matchMapper.entityToResponse(match);
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
