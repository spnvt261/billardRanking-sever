package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.MatchRequest;
import com.billard.BillardRankings.dto.MatchResponse;
import com.billard.BillardRankings.dto.TeamResponse;
import com.billard.BillardRankings.entity.Match;
import com.billard.BillardRankings.repository.TeamRepository;
import com.billard.BillardRankings.repository.PlayerRepository;
import com.billard.BillardRankings.mapper.PlayerMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@RequiredArgsConstructor
public abstract class MatchMapper implements GenericMapper<Match, MatchRequest, MatchResponse> {

    @Autowired
    protected TeamRepository teamRepository;

    @Autowired
    protected PlayerRepository playerRepository;

    @Autowired
    protected PlayerMapper playerMapper;

    @Override
    public abstract MatchResponse entityToResponse(Match entity);

    @Override
    public abstract Match requestToEntity(MatchRequest request);

    @Override
    public abstract Match partialUpdate(@MappingTarget Match entity, MatchRequest request);

    /**
     * ⚙️ Thêm hàm này để dùng trong MatchServiceImpl
     * Dùng để cập nhật entity từ request (chỉ ghi đè field có giá trị)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromRequest(MatchRequest request, @MappingTarget Match match);
    /**
     * Tự động gắn thông tin team1, team2 vào MatchResponse
     */
    @AfterMapping
    protected void afterMapping(Match entity, @MappingTarget MatchResponse response) {
        // Team 1
        if (entity.getTeam1Id() != null) {
            teamRepository.findById(entity.getTeam1Id()).ifPresent(team -> {
                var players = playerRepository.findPlayersByTeamId(team.getId());
                var playerDtos = players.stream()
                        .map(playerMapper::entityToResponse)
                        .toList();
                var teamResponse = new TeamResponse()
                        .setId(team.getId())
                        .setWorkspaceId(team.getWorkspaceId())
                        .setTeamName(team.getTeamName())
                        .setPlayers(playerDtos);
                response.setTeam1(teamResponse);
            });
        }

        // Team 2
        if (entity.getTeam2Id() != null) {
            teamRepository.findById(entity.getTeam2Id()).ifPresent(team -> {
                var players = playerRepository.findPlayersByTeamId(team.getId());
                var playerDtos = players.stream()
                        .map(playerMapper::entityToResponse)
                        .toList();
                var teamResponse = new TeamResponse()
                        .setId(team.getId())
                        .setWorkspaceId(team.getWorkspaceId())
                        .setTeamName(team.getTeamName())
                        .setPlayers(playerDtos);
                response.setTeam2(teamResponse);
            });
        }
    }
}
