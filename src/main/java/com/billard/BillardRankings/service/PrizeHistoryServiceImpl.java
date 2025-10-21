package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.PrizeHistoryRequest;
import com.billard.BillardRankings.dto.PrizeHistoryResponse;
import com.billard.BillardRankings.entity.PrizeHistory;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.exception.ResourceNotFoundException;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.PrizeHistoryMapper;
import com.billard.BillardRankings.repository.PrizeHistoryRepository;
import com.billard.BillardRankings.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrizeHistoryServiceImpl
        extends BaseCrudServiceImpl<PrizeHistory, PrizeHistoryRequest, PrizeHistoryResponse, Long>
        implements PrizeHistoryService {

    private final PrizeHistoryRepository prizeHistoryRepository;
    private final PrizeHistoryMapper prizeHistoryMapper;
    private final PlayerRepository playerRepository;

    @Override
    public PrizeHistoryResponse save(PrizeHistoryRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, request.getPlayerId()));

        if (!player.getWorkspaceId().equals(request.getWorkspaceId())) {
            throw new IllegalArgumentException("Player (id=" + player.getId() + ") does not belong to workspace " + request.getWorkspaceId());
        }

        Optional<PrizeHistory> latestHistory = prizeHistoryRepository
                .findTopByWorkspaceIdAndPlayerIdOrderByIdDesc(request.getWorkspaceId(), request.getPlayerId());

        int oldPrize = latestHistory.map(PrizeHistory::getNewPrize).orElse(0); // mặc định 0 nếu chưa có lịch sử
        int newPrize = oldPrize + request.getPrizeChange();

        PrizeHistory entity = new PrizeHistory()
                .setWorkspaceId(request.getWorkspaceId())
                .setPlayerId(request.getPlayerId())
                .setMatchId(request.getMatchId())
                .setTournamentId(request.getTournamentId())
                .setOldPrize(oldPrize)
                .setPrizeChange(request.getPrizeChange())
                .setNewPrize(newPrize);

        entity = prizeHistoryRepository.save(entity);
        return prizeHistoryMapper.entityToResponse(entity);
    }

    @Override
    public PrizeHistoryResponse save(Long id, PrizeHistoryRequest request) {
        PrizeHistory existing = prizeHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PRIZE_HISTORY, id));

        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, request.getPlayerId()));

        if (!player.getWorkspaceId().equals(request.getWorkspaceId())) {
            throw new IllegalArgumentException("Player (id=" + player.getId() + ") does not belong to workspace " + request.getWorkspaceId());
        }

        Optional<PrizeHistory> latestHistory = prizeHistoryRepository
                .findTopByWorkspaceIdAndPlayerIdOrderByIdDesc(request.getWorkspaceId(), request.getPlayerId());

        int oldPrize = latestHistory.map(PrizeHistory::getNewPrize).orElse(0);
        int newPrize = oldPrize + request.getPrizeChange();

        existing
                .setWorkspaceId(request.getWorkspaceId())
                .setPlayerId(request.getPlayerId())
                .setMatchId(request.getMatchId())
                .setTournamentId(request.getTournamentId())
                .setOldPrize(oldPrize)
                .setPrizeChange(request.getPrizeChange())
                .setNewPrize(newPrize);

        existing = prizeHistoryRepository.save(existing);
        return prizeHistoryMapper.entityToResponse(existing);
    }

    @Override
    protected JpaRepository<PrizeHistory, Long> getRepository() {
        return prizeHistoryRepository;
    }

    @Override
    protected JpaSpecificationExecutor<PrizeHistory> getSpecificationRepository() {
        return prizeHistoryRepository;
    }

    @Override
    protected GenericMapper<PrizeHistory, PrizeHistoryRequest, PrizeHistoryResponse> getMapper() {
        return prizeHistoryMapper;
    }

    @Override
    protected String getResourceName() {
        return ResourceName.PRIZE_HISTORY;
    }

    @Override
    protected List<String> getSearchFields() {
        return Arrays.asList();
    }

    @Override
    protected Long getWorkspaceIdFromEntity(PrizeHistory entity) {
        return entity.getWorkspaceId();
    }

    @Override
    protected Long getWorkspaceIdFromRequest(PrizeHistoryRequest request) {
        return request.getWorkspaceId();
    }

    @Override
    protected Long getIdFromEntity(PrizeHistory entity) {
        return entity.getId();
    }
}
