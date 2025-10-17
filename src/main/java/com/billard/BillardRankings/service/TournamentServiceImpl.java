package com.billard.BillardRankings.service.impl;
import com.billard.BillardRankings.dto.PlayerResponse;
import com.billard.BillardRankings.entity.TournamentPlayer;
import com.billard.BillardRankings.mapper.PlayerMapper;
import com.billard.BillardRankings.repository.TournamentPlayerRepository;
import com.billard.BillardRankings.service.BaseCrudServiceImpl;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.TournamentRequest;
import com.billard.BillardRankings.dto.TournamentResponse;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.entity.Tournament;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.TournamentMapper;
import com.billard.BillardRankings.repository.PlayerRepository;
import com.billard.BillardRankings.repository.TournamentRepository;
import com.billard.BillardRankings.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl
        extends BaseCrudServiceImpl<Tournament, TournamentRequest, TournamentResponse, Long>
        implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final PlayerRepository playerRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository; // ✅ thêm
    private final PlayerMapper playerMapper; // map entity -> PlayerResponse

    @Override
    protected JpaRepository<Tournament, Long> getRepository() {
        return tournamentRepository;
    }

    @Override
    protected JpaSpecificationExecutor<Tournament> getSpecificationRepository() {
        return tournamentRepository;
    }

    @Override
    protected GenericMapper<Tournament, TournamentRequest, TournamentResponse> getMapper() {
        return tournamentMapper;
    }

    @Override
    protected String getResourceName() {
        return "Tournament";
    }

    @Override
    protected List<String> getSearchFields() {
        return List.of("name", "location", "description");
    }

    @Override
    protected Long getWorkspaceIdFromEntity(Tournament entity) {
        return entity.getWorkspaceId();
    }

    @Override
    protected Long getWorkspaceIdFromRequest(TournamentRequest request) {
        return request.getWorkspaceId();
    }

    @Override
    protected Long getIdFromEntity(Tournament entity) {
        return entity.getId();
    }


    @Override
    public ListResponse<TournamentResponse> findAll(int page, int size, String sort, String filter, String search, boolean all, Long workspaceId) {
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);

        Page<Tournament> tournaments = tournamentRepository.findByWorkspaceId(workspaceId, pageable);

        List<TournamentResponse> responses = tournaments.getContent().stream()
                .map(this::buildTournamentResponse)
                .toList();

        return new ListResponse<>(responses, tournaments);
    }

    @Override
    public TournamentResponse findById(Long id, Long workspaceId) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found (id=" + id + ")"));

        if (!tournament.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("Tournament (id=" + id + ") does not belong to workspace " + workspaceId);
        }

        return buildTournamentResponse(tournament);
    }

    // ------------------ Build TournamentResponse kèm listPlayer ------------------
    private TournamentResponse buildTournamentResponse(Tournament tournament) {
        TournamentResponse response = tournamentMapper.entityToResponse(tournament);

        List<Long> playerIds = tournamentPlayerRepository.findByTournamentId(tournament.getId()).stream()
                .map(tp -> tp.getPlayerId())
                .toList();

        List<PlayerResponse> listPlayer = playerRepository.findAllById(playerIds).stream()
                .map(playerMapper::entityToResponse)
                .toList();

        response.setListPlayer(listPlayer);

        return response;
    }

    // ✅ Ghi đè lại phương thức save() để thêm validate workspace player
    @Override
    @Transactional
    public TournamentResponse save(TournamentRequest request) {
        // 1️⃣ Validate winner/runner-up/third-place
        validatePlayersWorkspace(request);

        // 2️⃣ Tạo Tournament
        Tournament tournament = tournamentMapper.requestToEntity(request);
        Tournament savedTournament = tournamentRepository.save(tournament);

        // 3️⃣ Thêm player vào tournament_player nếu có
        if (request.getPlayerIds() != null && !request.getPlayerIds().isEmpty()) {
            for (Long playerId : request.getPlayerIds()) {
                // ✅ Kiểm tra player tồn tại
                Player player = playerRepository.findById(playerId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Player not found (id=" + playerId + ")"));

                // ✅ Kiểm tra workspace
                if (!player.getWorkspaceId().equals(savedTournament.getWorkspaceId())) {
                    throw new IllegalArgumentException(
                            "Player " + playerId + " không thuộc workspace của tournament");
                }

                // ✅ Tạo TournamentPlayer
                TournamentPlayer tp = new TournamentPlayer();
                tp.setTournamentId(savedTournament.getId());
                tp.setPlayerId(player.getId());

                tournamentPlayerRepository.save(tp);
            }
        }

        // 4️⃣ Build response kèm list player
        return buildTournamentResponse(savedTournament);
    }

    @Override
    public TournamentResponse save(Long id, TournamentRequest request) {
        validatePlayersWorkspace(request);
        return super.save(id, request);
    }

    @Override
    public void delete(Long aLong, Long workspaceId) {

    }

    @Override
    public void delete(List<Long> longs, Long workspaceId) {

    }

    // ✅ Kiểm tra các player (winner, runner-up, third-place) thuộc cùng workspace
    private void validatePlayersWorkspace(TournamentRequest request) {
        Long workspaceId = request.getWorkspaceId();
        if (workspaceId == null) return;

        validatePlayerInWorkspace(request.getWinnerId(), workspaceId, "Winner");
        validatePlayerInWorkspace(request.getRunnerUpId(), workspaceId, "Runner-up");
        validatePlayerInWorkspace(request.getThirdPlaceId(), workspaceId, "Third-place");
    }

    // ✅ Hàm phụ: kiểm tra player có thuộc workspace hay không
    private void validatePlayerInWorkspace(Long playerId, Long workspaceId, String role) {
        if (playerId == null) return;
        Optional<Player> playerOpt = playerRepository.findById(playerId);

        if (playerOpt.isEmpty()) {
            throw new IllegalArgumentException(role + " player not found (id=" + playerId + ")");
        }

        Player player = playerOpt.get();
        if (!player.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException(role + " player (id=" + playerId + ") does not belong to workspace " + workspaceId);
        }
    }
}
