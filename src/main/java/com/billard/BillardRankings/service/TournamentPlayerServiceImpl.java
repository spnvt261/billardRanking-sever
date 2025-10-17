package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.TournamentPlayerRequest;
import com.billard.BillardRankings.dto.TournamentPlayerResponse;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.entity.Tournament;
import com.billard.BillardRankings.entity.TournamentPlayer;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.TournamentPlayerMapper;
import com.billard.BillardRankings.repository.PlayerRepository;
import com.billard.BillardRankings.repository.TournamentPlayerRepository;
import com.billard.BillardRankings.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentPlayerServiceImpl
        extends BaseCrudServiceImpl<TournamentPlayer, TournamentPlayerRequest, TournamentPlayerResponse, Long>
        implements TournamentPlayerService {

    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final TournamentPlayerMapper tournamentPlayerMapper;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    @Override
    public TournamentPlayerResponse save(TournamentPlayerRequest request) {
        Long tournamentId = request.getTournamentId();
        Long playerId = request.getPlayerId();

        if (tournamentId == null || playerId == null) {
            throw new IllegalArgumentException("tournamentId and playerId are required");
        }

        // ✅ Lấy tournament & player từ DB
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        // ✅ Kiểm tra workspaceId
        if (!tournament.getWorkspaceId().equals(player.getWorkspaceId())) {
            throw new IllegalArgumentException("Tournament and Player must belong to the same workspace");
        }

        // ✅ Lưu nếu hợp lệ
        TournamentPlayer entity = tournamentPlayerMapper.requestToEntity(request);
        entity = tournamentPlayerRepository.save(entity);
        return tournamentPlayerMapper.entityToResponse(entity);
    }

    // -------------------------------------------------
    @Override
    protected JpaRepository<TournamentPlayer, Long> getRepository() {
        return tournamentPlayerRepository;
    }

    @Override
    protected JpaSpecificationExecutor<TournamentPlayer> getSpecificationRepository() {
        return tournamentPlayerRepository;
    }

    @Override
    protected GenericMapper<TournamentPlayer, TournamentPlayerRequest, TournamentPlayerResponse> getMapper() {
        return tournamentPlayerMapper;
    }

    @Override
    protected String getResourceName() {
        return ResourceName.TOURNAMENT_PLAYER;
    }

    @Override
    protected List<String> getSearchFields() {
        return Arrays.asList("note");
    }

    @Override
    protected Long getWorkspaceIdFromEntity(TournamentPlayer entity) {
        // Vì bảng trung gian không có workspaceId, lấy từ tournament hoặc player nếu cần
        return null;
    }

    @Override
    protected Long getWorkspaceIdFromRequest(TournamentPlayerRequest request) {
        return null;
    }

    @Override
    protected Long getIdFromEntity(TournamentPlayer entity) {
        return entity.getId();
    }
}
