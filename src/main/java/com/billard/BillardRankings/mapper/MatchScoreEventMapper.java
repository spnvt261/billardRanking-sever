package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.MatchScoreEventRequest;
import com.billard.BillardRankings.dto.MatchScoreEventResponse;
import com.billard.BillardRankings.entity.MatchScoreEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MatchScoreEventMapper extends GenericMapper<MatchScoreEvent, MatchScoreEventRequest, MatchScoreEventResponse> {
    
//    @Override
//    MatchScoreEventResponse entityToResponse(MatchScoreEvent entity);
    
    @Override
    MatchScoreEvent requestToEntity(MatchScoreEventRequest request);
    
    @Override
    MatchScoreEvent partialUpdate(@MappingTarget MatchScoreEvent entity, MatchScoreEventRequest request);
    
    @Override
    List<MatchScoreEventResponse> entityToResponse(List<MatchScoreEvent> entities);

    @Override
    @Mapping(target = "team", ignore = true) // Bỏ qua trường team, sẽ xử lý trong service
    MatchScoreEventResponse entityToResponse(MatchScoreEvent entity);
}
