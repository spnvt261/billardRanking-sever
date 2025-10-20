package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.PlayerResponse;
import com.billard.BillardRankings.dto.TournamentRequest;
import com.billard.BillardRankings.dto.TournamentResponse;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.entity.Tournament;
import com.billard.BillardRankings.repository.PlayerRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class TournamentMapper implements GenericMapper<Tournament, TournamentRequest, TournamentResponse> {

    @Autowired
    protected PlayerRepository playerRepository;

    @Autowired
    protected PlayerMapper playerMapper;

    @Override
    public abstract Tournament requestToEntity(TournamentRequest request);

    @Override
    public abstract Tournament partialUpdate(@MappingTarget Tournament entity, TournamentRequest request);

    @Override
    public abstract List<TournamentResponse> entityToResponse(List<Tournament> entities);



    // ⚡️👉 Thêm hàm này để dùng cho update trong service
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromRequest(TournamentRequest request, @MappingTarget Tournament entity);



    @Override
    public TournamentResponse entityToResponse(Tournament entity) {
        if (entity == null) {
            return null;
        }

        TournamentResponse response = new TournamentResponse()
                .setId(entity.getId())
                .setWorkspaceId(entity.getWorkspaceId())
                .setName(entity.getName())
                .setTournamentType(entity.getTournamentType())
                .setStartDate(entity.getStartDate())
                .setEndDate(entity.getEndDate())
                .setLocation(entity.getLocation())
                .setPrize(entity.getPrize())
                .setWinnerId(entity.getWinnerId())
                .setRunnerUpId(entity.getRunnerUpId())
                .setThirdPlaceId(entity.getThirdPlaceId())
                .setDescription(entity.getDescription())
                .setRules(entity.getRules())
                .setBanner(entity.getBanner())
                .setStatus(entity.getStatus())
                .setFormat(entity.getFormat())
                .setCreatedAt(entity.getCreatedAt())
                .setUpdatedAt(entity.getUpdatedAt());

        // Map các Player tương ứng theo ID
        if (entity.getWinnerId() != null) {
            playerRepository.findById(entity.getWinnerId())
                    .map(playerMapper::entityToResponse)
                    .ifPresent(response::setWinner);
        }
        if (entity.getRunnerUpId() != null) {
            playerRepository.findById(entity.getRunnerUpId())
                    .map(playerMapper::entityToResponse)
                    .ifPresent(response::setRunnerUp);
        }
        if (entity.getThirdPlaceId() != null) {
            playerRepository.findById(entity.getThirdPlaceId())
                    .map(playerMapper::entityToResponse)
                    .ifPresent(response::setThirdPlace);
        }

        return response;
    }
}
