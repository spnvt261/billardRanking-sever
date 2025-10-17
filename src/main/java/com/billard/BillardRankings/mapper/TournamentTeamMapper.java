package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.TournamentTeamRequest;
import com.billard.BillardRankings.dto.TournamentTeamResponse;
import com.billard.BillardRankings.entity.TournamentTeam;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TournamentTeamMapper extends GenericMapper<TournamentTeam, TournamentTeamRequest, TournamentTeamResponse> {
    
    @Override
    TournamentTeamResponse entityToResponse(TournamentTeam entity);
    
    @Override
    TournamentTeam requestToEntity(TournamentTeamRequest request);
    
    @Override
    TournamentTeam partialUpdate(@MappingTarget TournamentTeam entity, TournamentTeamRequest request);
    
    @Override
    List<TournamentTeamResponse> entityToResponse(List<TournamentTeam> entities);
}
