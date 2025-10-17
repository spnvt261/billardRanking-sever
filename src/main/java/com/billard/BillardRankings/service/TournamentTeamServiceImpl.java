package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.TournamentTeamRequest;
import com.billard.BillardRankings.dto.TournamentTeamResponse;
import com.billard.BillardRankings.entity.Team;
import com.billard.BillardRankings.entity.Tournament;
import com.billard.BillardRankings.entity.TournamentTeam;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.TournamentTeamMapper;
import com.billard.BillardRankings.repository.TeamRepository;
import com.billard.BillardRankings.repository.TournamentRepository;
import com.billard.BillardRankings.repository.TournamentTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TournamentTeamServiceImpl
        extends BaseCrudServiceImpl<TournamentTeam, TournamentTeamRequest, TournamentTeamResponse, Long>
        implements TournamentTeamService {

    private final TournamentTeamRepository tournamentTeamRepository;
    private final TournamentTeamMapper tournamentTeamMapper;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    @Override
    protected JpaRepository<TournamentTeam, Long> getRepository() {
        return tournamentTeamRepository;
    }

    @Override
    protected JpaSpecificationExecutor<TournamentTeam> getSpecificationRepository() {
        return tournamentTeamRepository;
    }

    @Override
    protected GenericMapper<TournamentTeam, TournamentTeamRequest, TournamentTeamResponse> getMapper() {
        return tournamentTeamMapper;
    }

    @Override
    protected String getResourceName() {
        return ResourceName.TOURNAMENT_TEAM;
    }

    @Override
    protected List<String> getSearchFields() {
        return Arrays.asList("note");
    }

    @Override
    protected Long getWorkspaceIdFromEntity(TournamentTeam entity) {
        return null;
    }

    @Override
    protected Long getWorkspaceIdFromRequest(TournamentTeamRequest request) {
        return null;
    }

    @Override
    protected Long getIdFromEntity(TournamentTeam entity) {
        return entity.getId();
    }

    // ------------------ Override save để validate workspace ------------------

    @Override
    public TournamentTeamResponse save(TournamentTeamRequest request) {
        validateWorkspace(request);
        return super.save(request);
    }

    @Override
    public TournamentTeamResponse save(Long id, TournamentTeamRequest request) {
        validateWorkspace(request);
        return super.save(id, request);
    }

    // ------------------ Kiểm tra workspace ------------------

    private void validateWorkspace(TournamentTeamRequest request) {
        Long tournamentId = request.getTournamentId();
        Long teamId = request.getTeamId();

        if (tournamentId == null || teamId == null) {
            throw new IllegalArgumentException("TournamentId and TeamId are required");
        }

        Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
        if (tournamentOpt.isEmpty()) {
            throw new IllegalArgumentException("Tournament not found (id=" + tournamentId + ")");
        }

        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            throw new IllegalArgumentException("Team not found (id=" + teamId + ")");
        }

        Tournament tournament = tournamentOpt.get();
        Team team = teamOpt.get();

        if (!tournament.getWorkspaceId().equals(team.getWorkspaceId())) {
            throw new IllegalArgumentException("Team and Tournament must belong to the same workspace");
        }
    }
}
