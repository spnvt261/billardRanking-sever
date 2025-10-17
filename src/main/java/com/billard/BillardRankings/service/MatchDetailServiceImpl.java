package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchDetailResponse;
import com.billard.BillardRankings.entity.*;
import com.billard.BillardRankings.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchDetailServiceImpl implements MatchDetailService {
    
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final PlayerRepository playerRepository;
    
    @Override
    public ListResponse<MatchDetailResponse> getMatchDetails(Long workspaceId, int page, int size, String sort, String filter, String search, boolean all) {
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<Match> matches = matchRepository.findByWorkspaceId(workspaceId, pageable);
        
        List<MatchDetailResponse> matchDetails = matches.getContent().stream()
                .map(this::convertToMatchDetailResponse)
                .collect(Collectors.toList());
        
        return new ListResponse<>(matchDetails, matches);
    }
    
    @Override
    public MatchDetailResponse getMatchDetailById(Long matchId, Long workspaceId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        
        if (!match.getWorkspaceId().equals(workspaceId)) {
            throw new RuntimeException("Match not found in workspace");
        }
        
        return convertToMatchDetailResponse(match);
    }
    
    private MatchDetailResponse convertToMatchDetailResponse(Match match) {
        MatchDetailResponse response = new MatchDetailResponse()
                .setMatchId(match.getId())
                .setTournamentId(match.getTournamentId())
                .setMatchType(match.getMatchType().name())
                .setMatchDate(match.getMatchDate())
                .setScoreTeam1(match.getScoreTeam1())
                .setScoreTeam2(match.getScoreTeam2());
        
        // Get tournament name
        if (match.getTournamentId() != null) {
            tournamentRepository.findById(match.getTournamentId())
                    .ifPresent(tournament -> response.setTournamentName(tournament.getName()));
        }
        
        // Get team1 details
        if (match.getTeam1Id() != null) {
            teamRepository.findById(match.getTeam1Id())
                    .ifPresent(team -> {
                        MatchDetailResponse.TeamDetail teamDetail = new MatchDetailResponse.TeamDetail()
                                .setId(team.getId())
                                .setName(team.getTeamName())
                                .setWorkspaceId(team.getWorkspaceId());
                        // Get players for team1
                        List<TeamPlayer> teamPlayers = teamPlayerRepository.findByTeamId(team.getId());
                        List<MatchDetailResponse.PlayerInfo> players = teamPlayers.stream()
                                .map(tp -> playerRepository.findById(tp.getPlayerId())
                                        .map(player -> new MatchDetailResponse.PlayerInfo()
                                                .setId(player.getId())
                                                .setName(player.getName()))
                                        .orElse(null))
                                .filter(player -> player != null)
                                .collect(Collectors.toList());
                        
                        teamDetail.setPlayers(players);
                        response.setTeam1(teamDetail);
                    });
        }
        
        // Get team2 details
        if (match.getTeam2Id() != null) {
            teamRepository.findById(match.getTeam2Id())
                    .ifPresent(team -> {
                        MatchDetailResponse.TeamDetail teamDetail = new MatchDetailResponse.TeamDetail()
                                .setId(team.getId())
                                .setName(team.getTeamName())
                                .setWorkspaceId(team.getWorkspaceId());
                        
                        // Get players for team2
                        List<TeamPlayer> teamPlayers = teamPlayerRepository.findByTeamId(team.getId());
                        List<MatchDetailResponse.PlayerInfo> players = teamPlayers.stream()
                                .map(tp -> playerRepository.findById(tp.getPlayerId())
                                        .map(player -> new MatchDetailResponse.PlayerInfo()
                                                .setId(player.getId())
                                                .setName(player.getName()))
                                        .orElse(null))
                                .filter(player -> player != null)
                                .collect(Collectors.toList());
                        
                        teamDetail.setPlayers(players);
                        response.setTeam2(teamDetail);
                    });
        }
        
        // Determine winner side
        if (match.getWinnerId() != null) {
            if (match.getWinnerId().equals(match.getTeam1Id())) {
                response.setWinnerSide("team1");
            } else if (match.getWinnerId().equals(match.getTeam2Id())) {
                response.setWinnerSide("team2");
            }
        }
        
        return response;
    }
}
