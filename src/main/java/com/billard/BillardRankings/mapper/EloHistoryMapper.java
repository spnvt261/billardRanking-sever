package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.EloHistoryRequest;
import com.billard.BillardRankings.dto.EloHistoryResponse;
import com.billard.BillardRankings.entity.EloHistory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EloHistoryMapper extends GenericMapper<EloHistory, EloHistoryRequest, EloHistoryResponse> {
    
    @Override
    EloHistoryResponse entityToResponse(EloHistory entity);
    
    @Override
    EloHistory requestToEntity(EloHistoryRequest request);
    
    @Override
    EloHistory partialUpdate(@MappingTarget EloHistory entity, EloHistoryRequest request);
    
    @Override
    List<EloHistoryResponse> entityToResponse(List<EloHistory> entities);
}
