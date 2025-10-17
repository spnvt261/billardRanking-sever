package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.TeamPlayerRequest;
import com.billard.BillardRankings.dto.TeamPlayerResponse;
import com.billard.BillardRankings.entity.TeamPlayer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeamPlayerMapper extends GenericMapper<TeamPlayer, TeamPlayerRequest, TeamPlayerResponse> {
    
    @Override
    TeamPlayerResponse entityToResponse(TeamPlayer entity);
    
    @Override
    TeamPlayer requestToEntity(TeamPlayerRequest request);
    
    @Override
    TeamPlayer partialUpdate(@MappingTarget TeamPlayer entity, TeamPlayerRequest request);
    
    @Override
    List<TeamPlayerResponse> entityToResponse(List<TeamPlayer> entities);
}
