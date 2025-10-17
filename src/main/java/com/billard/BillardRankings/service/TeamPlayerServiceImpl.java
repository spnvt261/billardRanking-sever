package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.TeamPlayerRequest;
import com.billard.BillardRankings.dto.TeamPlayerResponse;
import com.billard.BillardRankings.entity.Team;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.entity.TeamPlayer;
import com.billard.BillardRankings.exception.ResourceNotFoundException;
import com.billard.BillardRankings.mapper.TeamPlayerMapper;
import com.billard.BillardRankings.repository.TeamPlayerRepository;
import com.billard.BillardRankings.repository.TeamRepository;
import com.billard.BillardRankings.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamPlayerServiceImpl implements TeamPlayerService {

    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamPlayerMapper teamPlayerMapper;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    @Override
    public ListResponse<TeamPlayerResponse> findAll(int page, int size, String sort, String filter, String search, boolean all, Long workspaceId) {
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<TeamPlayer> teamPlayers = teamPlayerRepository.findAll(pageable);
        List<TeamPlayerResponse> responses = teamPlayerMapper.entityToResponse(teamPlayers.getContent());
        return new ListResponse<>(responses, teamPlayers);
    }

    @Override
    public TeamPlayerResponse findById(Long id, Long workspaceId) {
        TeamPlayer teamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.TEAM_PLAYER, id));
        return teamPlayerMapper.entityToResponse(teamPlayer);
    }

    @Override
    @Transactional
    public TeamPlayerResponse save(TeamPlayerRequest request) {
        // --- Check team ---
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.TEAM, request.getTeamId()));

        // --- Check player ---
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, request.getPlayerId()));

        // --- Validate workspace ---
        if (!team.getWorkspaceId().equals(request.getWorkspaceId()) ||
                !player.getWorkspaceId().equals(request.getWorkspaceId())) {
            throw new IllegalArgumentException("Team and Player must belong to the same workspace!");
        }

        // --- Save after validation ---
        TeamPlayer teamPlayer = teamPlayerMapper.requestToEntity(request);
        teamPlayer = teamPlayerRepository.save(teamPlayer);
        return teamPlayerMapper.entityToResponse(teamPlayer);
    }

    @Override
    @Transactional
    public TeamPlayerResponse save(Long id, TeamPlayerRequest request) {
        TeamPlayer existingTeamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.TEAM_PLAYER, id));

        TeamPlayer updatedTeamPlayer = teamPlayerMapper.partialUpdate(existingTeamPlayer, request);
        updatedTeamPlayer = teamPlayerRepository.save(updatedTeamPlayer);
        return teamPlayerMapper.entityToResponse(updatedTeamPlayer);
    }

    @Override
    public void delete(Long id, Long workspaceId) {
        TeamPlayer teamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.TEAM_PLAYER, id));
        teamPlayerRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids, Long workspaceId) {
        List<TeamPlayer> teamPlayers = teamPlayerRepository.findAllById(ids);
        teamPlayerRepository.deleteAll(teamPlayers);
    }
}
