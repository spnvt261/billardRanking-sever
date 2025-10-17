package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.MatchScoreEventRequest;
import com.billard.BillardRankings.dto.MatchScoreEventResponse;
import com.billard.BillardRankings.entity.Match;
import com.billard.BillardRankings.entity.MatchScoreEvent;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.entity.Team;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.MatchScoreEventMapper;
import com.billard.BillardRankings.repository.MatchRepository;
import com.billard.BillardRankings.repository.MatchScoreEventRepository;
import com.billard.BillardRankings.repository.PlayerRepository;
import com.billard.BillardRankings.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public MatchScoreEventResponse save(MatchScoreEventRequest request) {
        Long workspaceId = request.getWorkspaceId();

        // ✅ Kiểm tra Match
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        if (!match.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("Match does not belong to the same workspace");
        }

        // ✅ Kiểm tra Team
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        if (!team.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("Team does not belong to the same workspace");
        }

        // ✅ Kiểm tra Player (nếu có)
        if (request.getPlayerId() != null) {
            Player player = playerRepository.findById(request.getPlayerId())
                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
            if (!player.getWorkspaceId().equals(workspaceId)) {
                throw new IllegalArgumentException("Player does not belong to the same workspace");
            }
        }

        // ✅ Sau khi hợp lệ → lưu
        MatchScoreEvent entity = matchScoreEventMapper.requestToEntity(request);
        entity = matchScoreEventRepository.save(entity);
        return matchScoreEventMapper.entityToResponse(entity);
    }

    // -------------------------------------------------
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
