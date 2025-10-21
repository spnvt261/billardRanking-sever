package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.PrizeHistoryRequest;
import com.billard.BillardRankings.dto.PrizeHistoryResponse;
import com.billard.BillardRankings.entity.PrizeHistory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrizeHistoryMapper extends GenericMapper<PrizeHistory, PrizeHistoryRequest, PrizeHistoryResponse> {

    @Override
    PrizeHistoryResponse entityToResponse(PrizeHistory entity);

    @Override
    PrizeHistory requestToEntity(PrizeHistoryRequest request);

    @Override
    PrizeHistory partialUpdate(@MappingTarget PrizeHistory entity, PrizeHistoryRequest request);

    @Override
    List<PrizeHistoryResponse> entityToResponse(List<PrizeHistory> entities);
}
