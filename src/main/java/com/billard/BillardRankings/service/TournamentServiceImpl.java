package com.billard.BillardRankings.service.impl;

import com.billard.BillardRankings.dto.PlayerResponse;
import com.billard.BillardRankings.entity.EloHistory;
import com.billard.BillardRankings.entity.TournamentPlayer;
import com.billard.BillardRankings.mapper.PlayerMapper;
import com.billard.BillardRankings.repository.EloHistoryRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;


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
    private final EloHistoryRepository eloHistoryRepository;
    private static final String[] DEFAULT_BANNERS = {
            "https://res.cloudinary.com/djeohgclg/image/upload/v1760831796/kpyjblssthmy01sxrpwc.jpg",
            "https://res.cloudinary.com/djeohgclg/image/upload/v1760831891/bkqwndeuxax9d4icepaw.jpg"
    };
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
        if (tournament == null) return null;

        // 1️⃣ Map thông tin cơ bản của tournament
        TournamentResponse response = tournamentMapper.entityToResponse(tournament);

        // 2️⃣ Lấy danh sách playerId tham gia tournament
        List<Long> playerIds = tournamentPlayerRepository.findByTournamentId(tournament.getId()).stream()
                .map(tp -> tp.getPlayerId())
                .toList();

        if (playerIds.isEmpty()) {
            response.setListPlayer(List.of());
            return response;
        }

        // 3️⃣ Lấy player entity từ DB
        List<Player> players = playerRepository.findAllById(playerIds);

        // 4️⃣ Map Player -> PlayerResponse và tính Elo mới nhất
        List<PlayerResponse> listPlayer = new ArrayList<>();
        for (Player player : players) {
            PlayerResponse pr = playerMapper.entityToResponse(player);

            // Lấy EloHistory mới nhất
            int elo = eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(player.getId())
                    .map(EloHistory::getNewElo)
                    .orElse(player.getStartElo() != null ? player.getStartElo() : 0);
            pr.setElo(elo);

            listPlayer.add(pr);
        }

        // 5️⃣ Sắp xếp giảm dần theo Elo, nếu bằng thì theo tên
        listPlayer.sort((p1, p2) -> {
            int elo1 = p1.getElo() != null ? p1.getElo() : 0;
            int elo2 = p2.getElo() != null ? p2.getElo() : 0;
            int cmp = Integer.compare(elo2, elo1);
            if (cmp == 0) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
            return cmp;
        });

        // 6️⃣ Gán rank
        for (int i = 0; i < listPlayer.size(); i++) {
            listPlayer.get(i).setRank(i + 1);
        }

        // 7️⃣ Gán danh sách player vào response
        response.setListPlayer(listPlayer);

        // 8️⃣ Map winner/runner-up/third-place với Elo mới nhất
        if (response.getWinnerId() != null) {
            playerRepository.findById(response.getWinnerId())
                    .ifPresent(player -> {
                        PlayerResponse pr = playerMapper.entityToResponse(player);
                        pr.setElo(eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(player.getId())
                                .map(EloHistory::getNewElo)
                                .orElse(player.getStartElo() != null ? player.getStartElo() : 0));
                        response.setWinner(pr);
                    });
        }
        if (response.getRunnerUpId() != null) {
            playerRepository.findById(response.getRunnerUpId())
                    .ifPresent(player -> {
                        PlayerResponse pr = playerMapper.entityToResponse(player);
                        pr.setElo(eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(player.getId())
                                .map(EloHistory::getNewElo)
                                .orElse(player.getStartElo() != null ? player.getStartElo() : 0));
                        response.setRunnerUp(pr);
                    });
        }
        if (response.getThirdPlaceId() != null) {
            playerRepository.findById(response.getThirdPlaceId())
                    .ifPresent(player -> {
                        PlayerResponse pr = playerMapper.entityToResponse(player);
                        pr.setElo(eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(player.getId())
                                .map(EloHistory::getNewElo)
                                .orElse(player.getStartElo() != null ? player.getStartElo() : 0));
                        response.setThirdPlace(pr);
                    });
        }

        return response;
    }


    // ✅ Ghi đè lại phương thức save() để thêm validate workspace player
    @Override
    @Transactional
    public TournamentResponse save(TournamentRequest request) {
        // 0️⃣ Gán banner mặc định nếu banner null hoặc trống
        if (request.getBanner() == null || request.getBanner().isBlank()) {
            int randomIndex = (int) (Math.random() * DEFAULT_BANNERS.length);
            request.setBanner(DEFAULT_BANNERS[randomIndex]);
        }

        // 1️⃣ Validate winner/runner-up/third-place
        validatePlayersWorkspace(request);

        // 2️⃣ Tạo Tournament
        Tournament tournament = tournamentMapper.requestToEntity(request);
        Tournament savedTournament = tournamentRepository.save(tournament);

        // 3️⃣ Thêm player vào tournament_player nếu có
        if (request.getPlayerIds() != null && !request.getPlayerIds().isEmpty()) {
            for (Long playerId : request.getPlayerIds()) {
                Player player = playerRepository.findById(playerId)
                        .orElseThrow(() -> new IllegalArgumentException("Player not found (id=" + playerId + ")"));

                if (!player.getWorkspaceId().equals(savedTournament.getWorkspaceId())) {
                    throw new IllegalArgumentException(
                            "Player " + playerId + " không thuộc workspace của tournament");
                }

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
    @Override
    public Map<String, Object> getAllTournamentsGroupedByQuarter(Long workspaceId) {
        List<com.billard.BillardRankings.entity.Tournament> entities = tournamentRepository.findByWorkspaceId(workspaceId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<String, List<TournamentResponse>> normalGrouped = new LinkedHashMap<>();
        List<TournamentResponse> specialDenList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // 1️⃣ Mapping an toàn
        List<TournamentResponse> responses = new ArrayList<>();
        for (com.billard.BillardRankings.entity.Tournament ent : entities) {
            try {
                TournamentResponse resp = buildTournamentResponse(ent);
                responses.add(resp);
            } catch (Exception e) {
                String msg = String.format("Error mapping tournament id=%s: %s",
                        ent == null ? "null" : String.valueOf(ent.getId()), e.toString());
                System.err.println(msg);
                errors.add(msg);
            }
        }

        // 2️⃣ Sắp xếp giảm dần theo startDate
        responses.sort((a, b) -> {
            LocalDate da = parseDateSafe(a.getStartDate(), formatter);
            LocalDate db = parseDateSafe(b.getStartDate(), formatter);
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        // 3️⃣ Phân nhóm
        for (TournamentResponse t : responses) {
            if (t == null) continue;

            String typeStr = "";
            try {
                Object typeObj = t.getTournamentType();
                if (typeObj != null) {
                    typeStr = (typeObj instanceof Enum) ? ((Enum<?>) typeObj).name() : typeObj.toString();
                }
            } catch (Exception e) {
                System.err.println("Error reading tournamentType for id=" + t.getId() + ": " + e.toString());
            }

            if ("SPECIAL_DEN".equalsIgnoreCase(typeStr)) {
                specialDenList.add(t);
                continue;
            }

            LocalDate date = parseDateSafe(t.getStartDate(), formatter);
            String key;
            if (date == null) {
                key = "INVALID_DATE";
            } else {
                int month = date.getMonthValue();
                int year = date.getYear();
                String quarter = (month <= 3) ? "Q1" : (month <= 6) ? "Q2" : (month <= 9) ? "Q3" : "Q4";
                key = quarter + "/" + year;
            }

            normalGrouped.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        // ✅ 4️⃣ Sắp xếp từng list trong mỗi quý giảm dần theo id
        for (List<TournamentResponse> list : normalGrouped.values()) {
            list.sort((a, b) -> Long.compare(b.getId(), a.getId())); // ↓ giảm dần theo id
        }

        // ✅ 5️⃣ Sắp xếp danh sách SPECIAL_DEN giảm dần theo id
        specialDenList.sort((a, b) -> Long.compare(b.getId(), a.getId()));

        // 6️⃣ Trả kết quả cuối cùng
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("NormalTournament", normalGrouped);
        result.put("SpecialDen", specialDenList);
        if (!errors.isEmpty()) result.put("errors", errors);

        return result;
    }



    /** Hàm phụ an toàn parse */
    private LocalDate parseDateSafe(String dateStr, DateTimeFormatter formatter) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception ex1) {
            try {
                return LocalDate.parse(dateStr); // fallback ISO yyyy-MM-dd
            } catch (Exception ex2) {
                // không log quá nhiều ở đây (đã có log khi mapping từng entity nếu cần)
                return null;
            }
        }
    }



}
