package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.TeamRequest;
import com.billard.BillardRankings.dto.TeamResponse;
import com.billard.BillardRankings.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeamMapper extends GenericMapper<Team, TeamRequest, TeamResponse> {
    
    @Override
    TeamResponse entityToResponse(Team entity);
    
    @Override
    Team requestToEntity(TeamRequest request);
    
    @Override
    Team partialUpdate(@MappingTarget Team entity, TeamRequest request);
    
    @Override
    List<TeamResponse> entityToResponse(List<Team> entities);
}
