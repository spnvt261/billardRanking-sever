package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.TournamentPlayerRequest;
import com.billard.BillardRankings.dto.TournamentPlayerResponse;
import com.billard.BillardRankings.entity.TournamentPlayer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TournamentPlayerMapper extends GenericMapper<TournamentPlayer, TournamentPlayerRequest, TournamentPlayerResponse> {
    
    @Override
    TournamentPlayerResponse entityToResponse(TournamentPlayer entity);
    
    @Override
    TournamentPlayer requestToEntity(TournamentPlayerRequest request);
    
    @Override
    TournamentPlayer partialUpdate(@MappingTarget TournamentPlayer entity, TournamentPlayerRequest request);
    
    @Override
    List<TournamentPlayerResponse> entityToResponse(List<TournamentPlayer> entities);
}
