package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchScoreEventRequest;
import com.billard.BillardRankings.dto.MatchScoreEventResponse;
import com.billard.BillardRankings.dto.TeamResponse;
import com.billard.BillardRankings.entity.*;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.MatchScoreEventMapper;
import com.billard.BillardRankings.repository.MatchRepository;
import com.billard.BillardRankings.repository.MatchScoreEventRepository;
import com.billard.BillardRankings.repository.PlayerRepository;
import com.billard.BillardRankings.repository.TeamRepository;
import com.billard.BillardRankings.repository.TeamPlayerRepository;
import com.billard.BillardRankings.mapper.PlayerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchScoreEventServiceImpl
        extends BaseCrudServiceImpl<MatchScoreEvent, MatchScoreEventRequest, MatchScoreEventResponse, Long>
        implements MatchScoreEventService {

    private final MatchScoreEventRepository matchScoreEventRepository;
    private final MatchScoreEventMapper matchScoreEventMapper;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final PlayerMapper playerMapper;

    @Override
    @Transactional
    public MatchScoreEventResponse save(MatchScoreEventRequest request) {
        Long workspaceId = request.getWorkspaceId();

        // Kiểm tra Match
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        if (!match.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("Match does not belong to the same workspace");
        }

        // ⚠️ Thêm kiểm tra status
        if (match.getStatus() != Match.MatchStatus.ONGOING) {
            throw new IllegalArgumentException("Cannot add match score event to a finished match");
        }

        if(request.getTeamId().equals(match.getTeam1Id())){
            match.setScoreTeam1(match.getScoreTeam1()+1);
        }
        if(request.getTeamId().equals(match.getTeam2Id())){
            match.setScoreTeam2(match.getScoreTeam2()+1);
        }

        // ⚠️ Thêm kiểm tra status
//        if (match.getStatus() == Match.MatchStatus.FINISHED) {
//            throw new IllegalArgumentException("Cannot add match score event to a finished match");
//        }

        // Kiểm tra Team
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        if (!team.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("Team does not belong to the same workspace");
        }

        // Kiểm tra Player (nếu có)
        if (request.getPlayerId() != null) {
            Player player = playerRepository.findById(request.getPlayerId())
                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
            if (!player.getWorkspaceId().equals(workspaceId)) {
                throw new IllegalArgumentException("Player does not belong to the same workspace");
            }
        }

        // Sau khi hợp lệ → lưu
        MatchScoreEvent entity = matchScoreEventMapper.requestToEntity(request);
        entity = matchScoreEventRepository.save(entity);
        MatchScoreEventResponse response = matchScoreEventMapper.entityToResponse(entity);

        // Gán thông tin team
        response.setTeam(buildTeamResponse(entity.getTeamId()));
        return response;
    }


    @Override
    public MatchScoreEventResponse findById(Long id, Long workspaceId) {
        MatchScoreEvent entity = matchScoreEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MatchScoreEvent not found"));
        if (!entity.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("MatchScoreEvent does not belong to the same workspace");
        }
        MatchScoreEventResponse response = matchScoreEventMapper.entityToResponse(entity);
        // Gán thông tin team
        response.setTeam(buildTeamResponse(entity.getTeamId()));
        return response;
    }

    @Override
    public ListResponse<MatchScoreEventResponse> findAll(int page, int size, String sort, String filter, String search, boolean all, Long workspaceId) {
        // Gọi phương thức findAll từ lớp cha
        ListResponse<MatchScoreEventResponse> response = super.findAll(page, size, sort, filter, search, all, workspaceId);

        // Cập nhật danh sách response để thêm thông tin team
        List<MatchScoreEventResponse> updatedResponses = response.getContent().stream()
                .map(resp -> {
                    resp.setTeam(buildTeamResponse(resp.getTeamId()));
                    return resp;
                })
                .collect(Collectors.toList());

        // Tạo instance mới của ListResponse với danh sách đã cập nhật
        return new ListResponse<>(
                updatedResponses,        // Danh sách đã cập nhật
                response.getPage(),      // Trang hiện tại
                response.getSize(),      // Kích thước trang
                response.getTotalElements(), // Tổng số phần tử
                response.getTotalPages(), // Tổng số trang
                response.isLast()        // Cờ trang cuối
        );
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

    @Override
    protected JpaRepository<MatchScoreEvent, Long> getRepository() {
        return matchScoreEventRepository;
    }

    @Override
    protected JpaSpecificationExecutor<MatchScoreEvent> getSpecificationRepository() {
        return matchScoreEventRepository;
    }

    @Override
    protected GenericMapper<MatchScoreEvent, MatchScoreEventRequest, MatchScoreEventResponse> getMapper() {
        return matchScoreEventMapper;
    }

    @Override
    protected String getResourceName() {
        return ResourceName.MATCH_SCORE_EVENT;
    }

    @Override
    protected List<String> getSearchFields() {
        return Arrays.asList("note");
    }

    @Override
    protected Long getWorkspaceIdFromEntity(MatchScoreEvent entity) {
        return entity.getWorkspaceId();
    }

    @Override
    protected Long getWorkspaceIdFromRequest(MatchScoreEventRequest request) {
        return request.getWorkspaceId();
    }

    @Override
    protected Long getIdFromEntity(MatchScoreEvent entity) {
        return entity.getId();
    }
}