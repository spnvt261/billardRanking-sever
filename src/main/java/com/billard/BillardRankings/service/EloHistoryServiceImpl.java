package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.EloHistoryRequest;
import com.billard.BillardRankings.dto.EloHistoryResponse;
import com.billard.BillardRankings.entity.EloHistory;
import com.billard.BillardRankings.entity.Match;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.exception.ResourceNotFoundException;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.EloHistoryMapper;
import com.billard.BillardRankings.repository.EloHistoryRepository;
import com.billard.BillardRankings.repository.MatchRepository;
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
public class EloHistoryServiceImpl
        extends BaseCrudServiceImpl<EloHistory, EloHistoryRequest, EloHistoryResponse, Long>
        implements EloHistoryService {

    private final EloHistoryRepository eloHistoryRepository;
    private final EloHistoryMapper eloHistoryMapper;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Override
    public EloHistoryResponse save(EloHistoryRequest request) {
        // Kiểm tra workspaceId của player
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, request.getPlayerId()));

        if (!player.getWorkspaceId().equals(request.getWorkspaceId())) {
            throw new IllegalArgumentException("Player (id=" + player.getId() + ") does not belong to workspace " + request.getWorkspaceId());
        }

        // Kiểm tra workspaceId của match nếu matchId không null
        if (request.getMatchId() != null) {
            Match match = matchRepository.findById(request.getMatchId())
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceName.MATCH, request.getMatchId()));

            if (!match.getWorkspaceId().equals(request.getWorkspaceId())) {
                throw new IllegalArgumentException("Match (id=" + match.getId() + ") does not belong to workspace " + request.getWorkspaceId());
            }
        }

        // Lưu entity
        EloHistory entity = eloHistoryMapper.requestToEntity(request);
        entity = eloHistoryRepository.save(entity);
        return eloHistoryMapper.entityToResponse(entity);
    }

    @Override
    public EloHistoryResponse save(Long id, EloHistoryRequest request) {
        // Lấy entity hiện tại
        EloHistory existing = eloHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ELO_HISTORY, id));

        // Kiểm tra workspaceId của player
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, request.getPlayerId()));

        if (!player.getWorkspaceId().equals(request.getWorkspaceId())) {
            throw new IllegalArgumentException("Player (id=" + player.getId() + ") does not belong to workspace " + request.getWorkspaceId());
        }

        // Kiểm tra workspaceId của match nếu matchId không null
        if (request.getMatchId() != null) {
            Match match = matchRepository.findById(request.getMatchId())
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceName.MATCH, request.getMatchId()));

            if (!match.getWorkspaceId().equals(request.getWorkspaceId())) {
                throw new IllegalArgumentException("Match (id=" + match.getId() + ") does not belong to workspace " + request.getWorkspaceId());
            }
        }

        // Cập nhật entity
        existing = eloHistoryMapper.partialUpdate(existing, request);
        existing = eloHistoryRepository.save(existing);

        return eloHistoryMapper.entityToResponse(existing);
    }

    // ---------------- override các phương thức của BaseCrudServiceImpl ----------------
    @Override
    protected JpaRepository<EloHistory, Long> getRepository() {
        return eloHistoryRepository;
    }

    @Override
    protected JpaSpecificationExecutor<EloHistory> getSpecificationRepository() {
        return eloHistoryRepository;
    }

    @Override
    protected GenericMapper<EloHistory, EloHistoryRequest, EloHistoryResponse> getMapper() {
        return eloHistoryMapper;
    }

    @Override
    protected String getResourceName() {
        return ResourceName.ELO_HISTORY;
    }

    @Override
    protected List<String> getSearchFields() {
        return Arrays.asList();
    }

    @Override
    protected Long getWorkspaceIdFromEntity(EloHistory entity) {
        return entity.getWorkspaceId();
    }

    @Override
    protected Long getWorkspaceIdFromRequest(EloHistoryRequest request) {
        return request.getWorkspaceId();
    }

    @Override
    protected Long getIdFromEntity(EloHistory entity) {
        return entity.getId();
    }
}
