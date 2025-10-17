package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.PlayerRequest;
import com.billard.BillardRankings.dto.PlayerResponse;
import com.billard.BillardRankings.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlayerMapper extends GenericMapper<Player, PlayerRequest, PlayerResponse> {
    
    @Override
    PlayerResponse entityToResponse(Player entity);
    
    @Override
    Player requestToEntity(PlayerRequest request);
    
    @Override
    Player partialUpdate(@MappingTarget Player entity, PlayerRequest request);
    
    @Override
    List<PlayerResponse> entityToResponse(List<Player> entities);
}
