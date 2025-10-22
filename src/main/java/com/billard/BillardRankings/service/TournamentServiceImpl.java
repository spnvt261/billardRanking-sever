package com.billard.BillardRankings.service.impl;

import com.billard.BillardRankings.dto.*;
import com.billard.BillardRankings.dto.roundType.RoundRobinRankingResponse;
import com.billard.BillardRankings.dto.roundType.RoundRobinRequest;
import com.billard.BillardRankings.dto.roundType.RoundRobinTeamResponse;
import com.billard.BillardRankings.entity.*;
import com.billard.BillardRankings.mapper.PlayerMapper;
import com.billard.BillardRankings.repository.*;
import com.billard.BillardRankings.service.BaseCrudServiceImpl;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.TournamentMapper;
import com.billard.BillardRankings.service.TournamentService;
import com.billard.BillardRankings.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class TournamentServiceImpl
        extends BaseCrudServiceImpl<Tournament, TournamentRequest, TournamentResponse, Long>
        implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository; // ✅ thêm
    private final PlayerMapper playerMapper; // map entity -> PlayerResponse
    private final EloHistoryRepository eloHistoryRepository;
    private final TeamRepository teamRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final TournamentTeamRepository tournamentTeamRepository;

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
                .map(tournament -> buildTournamentResponse(tournament, true))
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

        return buildTournamentResponse(tournament,true);
    }


    private TournamentResponse buildTournamentResponse(Tournament tournament, boolean loadTeams) {
        TournamentResponse response = new TournamentResponse();
        response.setId(tournament.getId());
        response.setWorkspaceId(tournament.getWorkspaceId());
        response.setName(tournament.getName());
        response.setTournamentType(tournament.getTournamentType());
        response.setRound1PlayersAfter(tournament.getRound1PlayersAfter());
        response.setRound1Status(tournament.getRound1Status());
        response.setTournamentType2(tournament.getTournamentType2());
        response.setRound2PlayersAfter(tournament.getRound2PlayersAfter());
        response.setRound2Status(tournament.getRound2Status());
        response.setTournamentType3(tournament.getTournamentType3());
        response.setRound3Status(tournament.getRound3Status());
        response.setStartDate(tournament.getStartDate());
        response.setEndDate(tournament.getEndDate());
        response.setLocation(tournament.getLocation());
        response.setPrize(tournament.getPrize());
        response.setDescription(tournament.getDescription());
        response.setRules(tournament.getRules());
        response.setBanner(tournament.getBanner());
        response.setStatus(tournament.getStatus());
        response.setFormat(tournament.getFormat());
        response.setCreatedAt(tournament.getCreatedAt());
        response.setUpdatedAt(tournament.getUpdatedAt());

        response.setWinnerId(tournament.getWinnerId());
        response.setRunnerUpId(tournament.getRunnerUpId());
        response.setThirdPlaceId(tournament.getThirdPlaceId());

        if (tournament.getWinnerId() != null) {
            playerRepository.findById(tournament.getWinnerId())
                    .map(playerMapper::entityToResponse)
                    .ifPresent(response::setWinner);
        }

        if (tournament.getRunnerUpId() != null) {
            playerRepository.findById(tournament.getRunnerUpId())
                    .map(playerMapper::entityToResponse)
                    .ifPresent(response::setRunnerUp);
        }

        if (tournament.getThirdPlaceId() != null) {
            playerRepository.findById(tournament.getThirdPlaceId())
                    .map(playerMapper::entityToResponse)
                    .ifPresent(response::setThirdPlace);
        }

        // ✅ Load team theo từng round (1, 2, 3)
        if (loadTeams) {
            List<TournamentTeam> allTournamentTeams = tournamentTeamRepository.findByTournamentId(tournament.getId());
            Map<Integer, List<TournamentTeam>> teamsByRound = allTournamentTeams.stream()
                    .collect(Collectors.groupingBy(TournamentTeam::getTournamentRound));

            Map<Integer, List<TeamResponse>> mapRoundToTeams = new TreeMap<>();

            // ✅ Duyệt cố định 3 round
            for (int round = 1; round <= 3; round++) {
                List<TournamentTeam> roundTeams = teamsByRound.getOrDefault(round, List.of());

                if (roundTeams.isEmpty()) {
                    // Không có đội nào ở round này → mảng rỗng
                    mapRoundToTeams.put(round, List.of());
                    continue;
                }

                List<Long> teamIds = roundTeams.stream()
                        .map(TournamentTeam::getTeamId)
                        .toList();

                List<Team> teams = teamRepository.findAllById(teamIds);
                List<TeamPlayer> teamPlayers = teamPlayerRepository.findByTeamIdIn(teamIds);
                Map<Long, List<Long>> teamToPlayerIds = teamPlayers.stream()
                        .collect(Collectors.groupingBy(
                                TeamPlayer::getTeamId,
                                Collectors.mapping(TeamPlayer::getPlayerId, Collectors.toList())
                        ));

                List<TournamentPlayer> tournamentPlayers = tournamentPlayerRepository.findByTournamentId(tournament.getId());
                Map<Long, TournamentPlayer> playerIdToTournamentPlayer = tournamentPlayers.stream()
                        .collect(Collectors.toMap(TournamentPlayer::getPlayerId, tp -> tp));

                List<TeamResponse> listTeam = new ArrayList<>();

                for (Team team : teams) {
                    TeamResponse teamResponse = new TeamResponse();
                    teamResponse.setId(team.getId());
                    teamResponse.setWorkspaceId(team.getWorkspaceId());
                    teamResponse.setTeamName(team.getTeamName());
                    teamResponse.setCreatedAt(team.getCreatedAt());
                    teamResponse.setUpdatedAt(team.getUpdatedAt());

                    List<Long> pids = teamToPlayerIds.getOrDefault(team.getId(), List.of());
                    List<PlayerResponse> players = new ArrayList<>();

                    if (!pids.isEmpty()) {
                        List<Player> playerEntities = playerRepository.findAllById(pids);
                        for (Player p : playerEntities) {
                            PlayerResponse pr = playerMapper.entityToResponse(p);

                            int elo = eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(p.getId())
                                    .map(EloHistory::getNewElo)
                                    .orElse(p.getStartElo() != null ? p.getStartElo() : 0);
                            pr.setElo(elo);

                            TournamentPlayer tp = playerIdToTournamentPlayer.get(p.getId());
                            if (tp != null) {
                                pr.setRank(tp.getRankCurrent());
                                pr.setSeedNumber(tp.getSeedNumber());
                            }
                            players.add(pr);
                        }
                    }

                    teamResponse.setPlayers(players);
                    listTeam.add(teamResponse);
                }

                // ✅ Sắp xếp listTeam theo seed
                listTeam.sort((t1, t2) -> {
                    int minSeed1 = t1.getPlayers().stream()
                            .mapToInt(p -> p.getSeedNumber() != null ? p.getSeedNumber() : Integer.MAX_VALUE)
                            .min().orElse(Integer.MAX_VALUE);

                    int minSeed2 = t2.getPlayers().stream()
                            .mapToInt(p -> p.getSeedNumber() != null ? p.getSeedNumber() : Integer.MAX_VALUE)
                            .min().orElse(Integer.MAX_VALUE);

                    return Integer.compare(minSeed1, minSeed2);
                });

                mapRoundToTeams.put(round, listTeam);
            }

            response.setListTeamByRound(mapRoundToTeams);
        }

        return response;
    }


    private TournamentResponse buildTournamentSummary(Tournament tournament) {
        TournamentResponse response = new TournamentResponse();

        response.setId(tournament.getId());
        response.setWorkspaceId(tournament.getWorkspaceId());
        response.setName(tournament.getName());
        response.setTournamentType(tournament.getTournamentType());
        response.setTournamentType2(tournament.getTournamentType2());
        response.setTournamentType3(tournament.getTournamentType3());
        response.setRound1PlayersAfter(tournament.getRound1PlayersAfter());
        response.setRound2PlayersAfter(tournament.getRound2PlayersAfter());
        response.setStartDate(tournament.getStartDate());
        response.setEndDate(tournament.getEndDate());
        response.setLocation(tournament.getLocation());
        response.setPrize(tournament.getPrize());
        response.setBanner(tournament.getBanner());
        response.setDescription(tournament.getDescription());
        response.setRules(tournament.getRules());
        response.setStatus(tournament.getStatus());
        response.setFormat(tournament.getFormat());
        response.setCreatedAt(tournament.getCreatedAt());
        response.setUpdatedAt(tournament.getUpdatedAt());
        response.setWinnerId(tournament.getWinnerId());
        response.setRunnerUpId(tournament.getRunnerUpId());
        response.setThirdPlaceId(tournament.getThirdPlaceId());

        // ✅ Đếm số người tham dự trong bảng tournament_players
        int numberAttend = tournamentPlayerRepository.countByTournamentId(tournament.getId());
        response.setNumberAttend(numberAttend);

        // ✅ Đếm số đội trong bảng tournament_teams
        int numberTeams = tournamentTeamRepository.countByTournamentId(tournament.getId());
        response.setNumberTeams(numberTeams);

        return response;
    }


    private void handleTournamentPlayers(Tournament tournament, List<Long> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) return;

        Long workspaceId = tournament.getWorkspaceId();

        // --- A) Tính Elo & rank tổng thể cho toàn workspace ---
        List<Player> allPlayers = playerRepository.findByWorkspaceId(workspaceId);
        List<PlayerResponse> allResponses = new ArrayList<>();

        for (Player p : allPlayers) {
            PlayerResponse pr = playerMapper.entityToResponse(p);
            int elo = eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(p.getId())
                    .map(EloHistory::getNewElo)
                    .orElse(p.getStartElo() != null ? p.getStartElo() : 0);
            pr.setElo(elo);
            allResponses.add(pr);
        }

        // sắp xếp toàn workspace theo Elo ↓
        allResponses.sort((p1, p2) -> {
            int e1 = p1.getElo() != null ? p1.getElo() : 0;
            int e2 = p2.getElo() != null ? p2.getElo() : 0;
            int cmp = Integer.compare(e2, e1);
            if (cmp == 0) {
                String n1 = p1.getName() != null ? p1.getName() : "";
                String n2 = p2.getName() != null ? p2.getName() : "";
                return n1.compareToIgnoreCase(n2);
            }
            return cmp;
        });

        Map<Long, Integer> playerIdToOverallRank = new LinkedHashMap<>();
        for (int i = 0; i < allResponses.size(); i++) {
            playerIdToOverallRank.put(allResponses.get(i).getId(), i + 1);
        }

        // --- B) Tính Elo & seedNumber trong tập participants ---
        List<PlayerResponse> participants = new ArrayList<>();
        for (Long pid : playerIds) {
            Player player = playerRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException("Player not found (id=" + pid + ")"));

            if (!player.getWorkspaceId().equals(workspaceId)) {
                throw new IllegalArgumentException("Player " + pid + " không thuộc workspace của tournament");
            }

            PlayerResponse pr = playerMapper.entityToResponse(player);
            int elo = eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(player.getId())
                    .map(EloHistory::getNewElo)
                    .orElse(player.getStartElo() != null ? player.getStartElo() : 0);
            pr.setElo(elo);
            participants.add(pr);
        }

        // Sắp xếp participants theo Elo ↓
        participants.sort((p1, p2) -> {
            int e1 = p1.getElo() != null ? p1.getElo() : 0;
            int e2 = p2.getElo() != null ? p2.getElo() : 0;
            int cmp = Integer.compare(e2, e1);
            if (cmp == 0) {
                String n1 = p1.getName() != null ? p1.getName() : "";
                String n2 = p2.getName() != null ? p2.getName() : "";
                return n1.compareToIgnoreCase(n2);
            }
            return cmp;
        });

        Map<Long, Integer> playerIdToSeed = new LinkedHashMap<>();
        for (int i = 0; i < participants.size(); i++) {
            playerIdToSeed.put(participants.get(i).getId(), i + 1);
        }

        // --- C) Lưu toàn bộ tournament_players ---
        List<TournamentPlayer> tpList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long pid : playerIds) {
            Player player = playerRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException("Player not found (id=" + pid + ")"));

            TournamentPlayer tp = new TournamentPlayer();
            tp.setTournamentId(tournament.getId());
            tp.setPlayerId(player.getId());
            tp.setRankCurrent(playerIdToOverallRank.get(player.getId())); // rank tổng thể
            tp.setSeedNumber(playerIdToSeed.get(player.getId()));          // seed trong giải
            tp.setJoinedAt(now);
            tp.setIsActive(true);
            tpList.add(tp);
        }

        tournamentPlayerRepository.saveAll(tpList);
    }


    @Override
    @Transactional
    public TournamentResponse save(TournamentRequest request) {
        // 0️⃣ Gán banner mặc định nếu banner null hoặc trống
        if (request.getBanner() == null || request.getBanner().isBlank()) {
            int randomIndex = (int) (Math.random());
            request.setBanner(DEFAULT_BANNERS[randomIndex]);
        }
        // ✅ Gán giá trị mặc định cho format nếu null
        if (request.getFormat() == null) {
            request.setFormat(Tournament.TournamentFormat.SINGLE);
        }

        // 1️⃣ Validate winner/runner-up/third-place
        validatePlayersWorkspace(request);

        // 2️⃣ Tạo Tournament (chưa có id)
        Tournament tournament = tournamentMapper.requestToEntity(request);
        Tournament savedTournament = tournamentRepository.save(tournament);

        // ⚡ Nếu format = SINGLE → tạo team cho từng player
        if (savedTournament.getFormat() == Tournament.TournamentFormat.SINGLE) {
            createTeamsForSingleTournament(savedTournament, request.getPlayerIds());
        }

        // 3️⃣ Tính rankCurrent + seedNumber và thêm vào tournament_players
        handleTournamentPlayers(savedTournament, request.getPlayerIds());

        // 4️⃣ Build response kèm list player
        return buildTournamentResponse(savedTournament,true);
    }

    private void createTeamsForSingleTournament(Tournament tournament, List<Long> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) return;

        Long workspaceId = tournament.getWorkspaceId();
        LocalDateTime now = LocalDateTime.now();

        List<Team> newTeams = new ArrayList<>();
        List<TeamPlayer> newTeamPlayers = new ArrayList<>();
        List<TournamentTeam> newTournamentTeams = new ArrayList<>();

        for (Long playerId : playerIds) {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new IllegalArgumentException("Player not found (id=" + playerId + ")"));

            // 1️⃣ Tạo team mới cho player
            Team team = new Team()
                    .setWorkspaceId(workspaceId)
                    .setTeamName(player.getName() != null
                            ? player.getName() + " Team"
                            : "Player " + playerId + " Team");
            teamRepository.save(team);

            // 2️⃣ Tạo team_player (liên kết player với team)
            TeamPlayer tp = new TeamPlayer()
                    .setWorkspaceId(workspaceId)
                    .setTeamId(team.getId())
                    .setPlayerId(playerId)
                    .setJoinedAt(now)
                    .setIsCaptain(true); // mỗi team 1 người, là captain luôn
            newTeamPlayers.add(tp);

            // 3️⃣ Tạo tournament_team
            TournamentTeam tt = new TournamentTeam()
                    .setTournamentId(tournament.getId())
                    .setTeamId(team.getId())
                    .setTournamentRound(1)
                    .setIsActive(true);
            newTournamentTeams.add(tt);
        }

        // ✅ Lưu tất cả vào DB
        teamPlayerRepository.saveAll(newTeamPlayers);
        tournamentTeamRepository.saveAll(newTournamentTeams);
    }


    @Override
    @Transactional
    public TournamentResponse save(Long id, TournamentRequest request) {
        // 1️⃣ Kiểm tra tournament tồn tại
        validatePlayersWorkspace(request);

        Tournament existing = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found (id=" + id + ")"));

        // 2️⃣ Cập nhật các thông tin cơ bản (tên, ngày, mô tả, v.v.)
        tournamentMapper.updateEntityFromRequest(request, existing);
        Tournament saved = tournamentRepository.save(existing);
        // ✅ Gán giá trị mặc định cho format nếu null
        if (request.getFormat() == null) {
            request.setFormat(Tournament.TournamentFormat.SINGLE);
        }

        // 3️⃣ Xử lý playerIds nếu có
        if (request.getPlayerIds() != null && !request.getPlayerIds().isEmpty()) {

            // Lấy danh sách player hiện có trong tournament
            List<TournamentPlayer> existingPlayers = tournamentPlayerRepository.findByTournamentId(saved.getId());
            Set<Long> existingPlayerIds = existingPlayers.stream()
                    .map(TournamentPlayer::getPlayerId)
                    .collect(Collectors.toSet());

            // Lọc ra các player mới
            List<Long> newPlayerIds = request.getPlayerIds().stream()
                    .filter(idPlayer -> !existingPlayerIds.contains(idPlayer))
                    .toList();

            if (!newPlayerIds.isEmpty()) {
                // ⚡ Nếu format = SINGLE → tạo team cho các player mới
                if (saved.getFormat() == Tournament.TournamentFormat.SINGLE) {
                    createTeamsForSingleTournament(saved, newPlayerIds);
                }
                // ✅ Lấy toàn bộ player trong workspace để tính rank hiện tại
                List<Player> allPlayers = playerRepository.findByWorkspaceId(saved.getWorkspaceId());
                Map<Long, Integer> playerRankMap = calculatePlayerRanks(allPlayers);

                // ✅ Lấy toàn bộ player hiện có trong giải + player mới → để tính seed
                List<Player> allJoinedPlayers = playerRepository.findAllById(
                        Stream.concat(existingPlayerIds.stream(), newPlayerIds.stream())
                                .toList()
                );

                // Sắp xếp giảm dần theo Elo → seed 1, 2, 3...
                allJoinedPlayers.sort((p1, p2) -> {
                    int elo1 = getPlayerElo(p1);
                    int elo2 = getPlayerElo(p2);
                    return Integer.compare(elo2, elo1);
                });

                int seed = 1;
                for (Player p : allJoinedPlayers) {
                    TournamentPlayer tp = tournamentPlayerRepository
                            .findByTournamentIdAndPlayerId(saved.getId(), p.getId())
                            .orElse(null);

                    if (tp == null) {
                        // Player mới → thêm mới vào bảng
                        tp = new TournamentPlayer()
                                .setTournamentId(saved.getId())
                                .setPlayerId(p.getId())
                                .setJoinedAt(LocalDateTime.now())
                                .setIsActive(true);
                    }

                    // Cập nhật rank & seed
                    tp.setRankCurrent(playerRankMap.getOrDefault(p.getId(), 0));
                    tp.setSeedNumber(seed++);
                    tournamentPlayerRepository.save(tp);
                }
            }
        }

        // 4️⃣ Trả về response
        return buildTournamentResponse(saved,true);
    }

    private int getPlayerElo(Player player) {
        return eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(player.getId())
                .map(EloHistory::getNewElo)
                .orElse(player.getStartElo() != null ? player.getStartElo() : 0);
    }

    private Map<Long, Integer> calculatePlayerRanks(List<Player> allPlayers) {
        List<Player> sorted = new ArrayList<>(allPlayers);
        sorted.sort((p1, p2) -> {
            int elo1 = getPlayerElo(p1);
            int elo2 = getPlayerElo(p2);
            int cmp = Integer.compare(elo2, elo1);
            if (cmp == 0) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
            return cmp;
        });

        Map<Long, Integer> rankMap = new LinkedHashMap<>();
        for (int i = 0; i < sorted.size(); i++) {
            rankMap.put(sorted.get(i).getId(), i + 1);
        }
        return rankMap;
    }




//    @Override
//    public TournamentResponse save(Long id, TournamentRequest request) {
//        validatePlayersWorkspace(request);
//        return super.save(id, request);
//    }

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
    // ✅ Lấy sẵn toàn bộ tournamentId
        List<Long> tournamentIds = entities.stream().map(Tournament::getId).toList();

//        // ✅ Truy vấn để đếm người & team một lần
        List<TournamentTeam> allTeams = tournamentTeamRepository.findByTournamentIdIn(tournamentIds);
        List<TournamentPlayer> allPlayers = tournamentPlayerRepository.findByTournamentIdIn(tournamentIds);
    // Gom nhóm đếm
        Map<Long, Long> teamCountMap = allTeams.stream()
                .collect(Collectors.groupingBy(TournamentTeam::getTournamentId, Collectors.counting()));

        Map<Long, Long> playerCountMap = allPlayers.stream()
                .collect(Collectors.groupingBy(TournamentPlayer::getTournamentId, Collectors.counting()));
    // 1️⃣ Mapping an toàn, không load listTeam
    List<TournamentResponse> responses = new ArrayList<>();
    for (com.billard.BillardRankings.entity.Tournament ent : entities) {
        try {
            TournamentResponse resp = buildTournamentResponse(ent, false); // ❌ bỏ listTeam
            responses.add(resp);
            resp.setNumberTeams(teamCountMap.getOrDefault(ent.getId(), 0L).intValue());
            resp.setNumberAttend(playerCountMap.getOrDefault(ent.getId(), 0L).intValue());
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

    // 4️⃣ Sắp xếp từng list trong mỗi quý giảm dần theo id
    for (List<TournamentResponse> list : normalGrouped.values()) {
        list.sort((a, b) -> Long.compare(b.getId(), a.getId()));
    }

    // 5️⃣ Sắp xếp danh sách SPECIAL_DEN giảm dần theo id
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


//@Override
//@Transactional
//public List<MatchResponse> createRoundRobin(RoundRobinRequest request, Long workspaceId) {
//    Tournament tournament = tournamentRepository.findById(request.getTournamentId())
//            .orElseThrow(() -> new RuntimeException("Tournament not found"));
//
//    if (!tournament.getWorkspaceId().equals(workspaceId)) {
//        throw new IllegalArgumentException("Tournament does not belong to the specified workspaceId");
//    }
//
//    // Cập nhật thông tin theo roundNumber
//    switch (request.getRoundNumber()) {
//        case 1 -> {
//            tournament.setRound1PlayersAfter(request.getRoundPlayersAfter());
//            tournament.setRound1Status(Tournament.TournamentRoundStatus.ONGOING);
//        }
//        case 2 -> {
//            tournament.setRound2PlayersAfter(request.getRoundPlayersAfter());
//            tournament.setRound2Status(Tournament.TournamentRoundStatus.ONGOING);
//        }
//        case 3 -> tournament.setRound3Status(Tournament.TournamentRoundStatus.ONGOING);
//        default -> throw new IllegalArgumentException("Invalid round number: must be 1, 2, or 3");
//    }
//    tournamentRepository.save(tournament);
//
//    List<Long> validPlayerIds = playerRepository.findAllByWorkspaceId(workspaceId)
//            .stream()
//            .map(Player::getId)
//            .toList();
//
//    int gameNumberPlayed = request.getGameNumberPlayed();
//    char[] groupNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
//
//    List<List<Long>> groupPlayersCopy = new ArrayList<>();
//    List<Integer> groupNumRounds = new ArrayList<>();
//    for (List<Long> group : request.getGroupSelections()) {
//        List<Long> players = new ArrayList<>(group);
//        if (players.size() % 2 != 0) players.add(null);
//        groupPlayersCopy.add(players);
//        groupNumRounds.add(players.size() - 1);
//    }
//
//    List<Map<Long, Long>> groupPlayerToTeam = new ArrayList<>();
//    for (int groupIndex = 0; groupIndex < request.getGroupSelections().size(); groupIndex++) {
//        List<Long> players = groupPlayersCopy.get(groupIndex);
//        Map<Long, Long> playerToTeam = new HashMap<>();
//        for (Long playerId : players) {
//            if (playerId == null) continue;
//            if (!validPlayerIds.contains(playerId)) {
//                throw new IllegalArgumentException("Player " + playerId + " không thuộc workspaceId " + workspaceId);
//            }
//
//            Team team = new Team()
//                    .setWorkspaceId(workspaceId)
//                    .setTeamName("Team_" + playerId + "_R" + request.getRoundNumber());
//            teamRepository.save(team);
//
//            TeamPlayer teamPlayer = new TeamPlayer()
//                    .setWorkspaceId(workspaceId)
//                    .setTeamId(team.getId())
//                    .setPlayerId(playerId)
//                    .setIsCaptain(true)
//                    .setJoinedAt(LocalDateTime.now());
//            teamPlayerRepository.save(teamPlayer);
//
//            playerToTeam.put(playerId, team.getId());
//        }
//        groupPlayerToTeam.add(playerToTeam);
//    }
//
//    int maxRounds = groupNumRounds.stream().max(Integer::compareTo).orElse(0);
//
//    List<MatchResponse> createdMatches = new ArrayList<>();
//
//    for (int round = 0; round < maxRounds; round++) {
//        for (int groupIndex = 0; groupIndex < request.getGroupSelections().size(); groupIndex++) {
//            List<Long> players = groupPlayersCopy.get(groupIndex);
//            Map<Long, Long> playerToTeam = groupPlayerToTeam.get(groupIndex);
//            int numRounds = groupNumRounds.get(groupIndex);
//
//            if (round >= numRounds) continue;
//
//            int numPlayers = players.size();
//            int numMatchesPerRound = numPlayers / 2;
//            String groupLabel = groupNames[groupIndex] + "";
//
//            for (int i = 0; i < numMatchesPerRound; i++) {
//                Long player1 = players.get(i);
//                Long player2 = players.get(numPlayers - 1 - i);
//                if (player1 == null || player2 == null) continue;
//
//                gameNumberPlayed += 1;
//                Match match = new Match()
//                        .setWorkspaceId(workspaceId)
//                        .setTournamentId(tournament.getId())
//                        .setTeam1Id(playerToTeam.get(player1))
//                        .setTeam2Id(playerToTeam.get(player2))
//                        .setScoreTeam1(0)
//                        .setScoreTeam2(0)
//                        .setMatchCategory(Match.MatchCategory.TOURNAMENT)
//                        .setMatchType(Match.MatchType.GROUP)
//                        .setStatus(Match.MatchStatus.UPCOMING)
//                        .setMatchDate(DateUtils.getCurrentDateString())
//                        .setNote(groupLabel)
//                        .setRound(round + 1)
//                        .setTournamentRoundType(Tournament.TournamentType.ROUND_ROBIN)
//                        .setTournamentRound(request.getRoundNumber())
//                        .setGameNumber(gameNumberPlayed);
//
//                matchRepository.save(match);
//
//                // Chuyển sang MatchResponse
//                MatchResponse response = new MatchResponse()
//                        .setId(match.getId())
//                        .setWorkspaceId(match.getWorkspaceId())
//                        .setTournamentId(match.getTournamentId())
//                        .setTournamentRoundType(match.getTournamentRoundType())
//                        .setTeam1Id(match.getTeam1Id())
//                        .setTeam2Id(match.getTeam2Id())
//                        .setScoreTeam1(match.getScoreTeam1())
//                        .setScoreTeam2(match.getScoreTeam2())
//                        .setMatchType(match.getMatchType())
//                        .setMatchCategory(match.getMatchCategory())
//                        .setMatchDate(match.getMatchDate())
//                        .setNote(match.getNote())
//                        .setRound(match.getRound())
//                        .setGameNumber(match.getGameNumber())
//                        .setStatus(match.getStatus())
//                        .setWinnerId(match.getWinnerId())
//                        .setCreatedAt(match.getCreatedAt())
//                        .setUpdatedAt(match.getUpdatedAt());
//
//                createdMatches.add(response);
//            }
//
//            // Xoay vòng (circle method)
//            Long first = players.get(0);
//            List<Long> rotated = new ArrayList<>();
//            rotated.add(first);
//            rotated.add(players.get(numPlayers - 1));
//            rotated.addAll(players.subList(1, numPlayers - 1));
//            groupPlayersCopy.set(groupIndex, rotated);
//        }
//    }
//
//    return createdMatches;
//}
public List<MatchResponse> createRoundRobin(RoundRobinRequest request, Long workspaceId) {
    // 1. Lấy tournament và kiểm tra workspace
    Tournament tournament = tournamentRepository.findById(request.getTournamentId())
            .orElseThrow(() -> new RuntimeException("Tournament not found"));

    if (!tournament.getWorkspaceId().equals(workspaceId)) {
        throw new IllegalArgumentException("Tournament does not belong to the specified workspaceId");
    }

    // 2. Cập nhật trạng thái round tương ứng
    switch (request.getRoundNumber()) {
        case 1 -> {
            tournament.setRound1PlayersAfter(request.getRoundPlayersAfter());
            tournament.setRound1Status(Tournament.TournamentRoundStatus.ONGOING);
        }
        case 2 -> {
            tournament.setRound2PlayersAfter(request.getRoundPlayersAfter());
            tournament.setRound2Status(Tournament.TournamentRoundStatus.ONGOING);
        }
        case 3 -> tournament.setRound3Status(Tournament.TournamentRoundStatus.ONGOING);
        default -> throw new IllegalArgumentException("Invalid round number: must be 1, 2, or 3");
    }
    tournamentRepository.save(tournament);

    // 3. Lấy tất cả TournamentTeam cho tournament và round được truyền vào
    List<TournamentTeam> tournamentTeams = tournamentTeamRepository
            .findByTournamentIdAndTournamentRound(tournament.getId(), request.getRoundNumber());

    if (tournamentTeams.isEmpty()) {
        throw new IllegalArgumentException("No teams found in tournament " + tournament.getId() + " for round " + request.getRoundNumber());
    }

    // 4. Lấy tất cả teamIds thuộc tournamentTeams
    List<Long> tournamentTeamIds = tournamentTeams.stream()
            .map(TournamentTeam::getTeamId)
            .toList();

    // 5. Lấy mapping teamId -> list(playerId) từ team_player (chỉ cho các team thuộc tournamentTeamIds)
    List<TeamPlayer> teamPlayers = teamPlayerRepository.findByTeamIdIn(tournamentTeamIds);
    Map<Long, List<Long>> teamIdToPlayerIds = teamPlayers.stream()
            .collect(Collectors.groupingBy(
                    TeamPlayer::getTeamId,
                    Collectors.mapping(TeamPlayer::getPlayerId, Collectors.toList())
            ));

    // 6. Xây map playerId -> teamId (nếu player nằm trong nhiều team thuộc tournamentTeamIds, map đến first match)
    Map<Long, Long> playerIdToTeamId = new HashMap<>();
    for (Map.Entry<Long, List<Long>> e : teamIdToPlayerIds.entrySet()) {
        Long teamId = e.getKey();
        for (Long playerId : e.getValue()) {
            // nếu player đã map sang team khác trước đó, giữ team đầu tiên (hoặc bạn có thể ném lỗi tuỳ nhu cầu)
            playerIdToTeamId.putIfAbsent(playerId, teamId);
        }
    }

    // 7. Lấy groupSelections từ request (mảng 2 chiều playerId groups)
    List<List<Long>> groupSelections = request.getGroupSelections();
    if (groupSelections == null || groupSelections.isEmpty()) {
        throw new IllegalArgumentException("groupSelections must not be empty");
    }

    int gameNumberPlayed = request.getGameNumberPlayed();
    char[] groupNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    List<MatchResponse> createdMatches = new ArrayList<>();

    // 8. Với mỗi group (group là danh sách playerId), chuyển sang danh sách teamId tương ứng
    for (int groupIndex = 0; groupIndex < groupSelections.size(); groupIndex++) {
        List<Long> playerGroup = groupSelections.get(groupIndex);
        if (playerGroup == null || playerGroup.isEmpty()) {
            continue; // bỏ qua nhóm rỗng
        }

        List<Long> groupTeamIds = new ArrayList<>();
        Set<Long> seenTeamIds = new HashSet<>();

        for (Long playerId : playerGroup) {
            if (playerId == null) continue;
            Long teamId = playerIdToTeamId.get(playerId);
            if (teamId == null) {
                // player trong payload không thuộc bất kỳ team nào của tournament này ở round này
                throw new IllegalArgumentException("Player " + playerId + " không thuộc bất kỳ team nào của tournament " + tournament.getId() + " ở round " + request.getRoundNumber());
            }
            // tránh trùng team (nếu một team có nhiều player)
            if (!seenTeamIds.contains(teamId)) {
                groupTeamIds.add(teamId);
                seenTeamIds.add(teamId);
            }
        }

        if (groupTeamIds.isEmpty()) continue; // không có team hợp lệ trong group này

        // 9. Đảm bảo số team chẵn (thêm null làm bye nếu cần)
        if (groupTeamIds.size() % 2 != 0) {
            groupTeamIds.add(null);
        }

        int numRounds = groupTeamIds.size() - 1;
        String groupLabel = groupNames.length > groupIndex ? String.valueOf(groupNames[groupIndex]) : ("G" + (groupIndex + 1));

        // 10. Sinh match cho từng vòng trong group (circle method)
        List<Long> rotating = new ArrayList<>(groupTeamIds); // sẽ xoay vòng
        for (int r = 0; r < numRounds; r++) {
            int numMatchesPerRound = rotating.size() / 2;
            for (int i = 0; i < numMatchesPerRound; i++) {
                Long team1 = rotating.get(i);
                Long team2 = rotating.get(rotating.size() - 1 - i);

                if (team1 == null || team2 == null) continue; // bye

                gameNumberPlayed += 1;

                Match match = new Match()
                        .setWorkspaceId(workspaceId)
                        .setTournamentId(tournament.getId())
                        .setTeam1Id(team1)
                        .setTeam2Id(team2)
                        .setScoreTeam1(0)
                        .setScoreTeam2(0)
                        .setMatchCategory(Match.MatchCategory.TOURNAMENT)
                        .setMatchType(Match.MatchType.GROUP)
                        .setStatus(Match.MatchStatus.UPCOMING)
                        .setMatchDate(DateUtils.getCurrentDateString())
                        .setNote(groupLabel)
                        .setRound(r + 1) // vòng trong group
                        .setTournamentRoundType(Tournament.TournamentType.ROUND_ROBIN)
                        .setTournamentRound(request.getRoundNumber())
                        .setGameNumber(gameNumberPlayed);

                matchRepository.save(match);

                MatchResponse response = new MatchResponse()
                        .setId(match.getId())
                        .setWorkspaceId(match.getWorkspaceId())
                        .setTournamentId(match.getTournamentId())
                        .setTournamentRoundType(match.getTournamentRoundType())
                        .setTeam1Id(match.getTeam1Id())
                        .setTeam2Id(match.getTeam2Id())
                        .setScoreTeam1(match.getScoreTeam1())
                        .setScoreTeam2(match.getScoreTeam2())
                        .setMatchType(match.getMatchType())
                        .setMatchCategory(match.getMatchCategory())
                        .setMatchDate(match.getMatchDate())
                        .setNote(match.getNote())
                        .setRound(match.getRound())
                        .setGameNumber(match.getGameNumber())
                        .setStatus(match.getStatus())
                        .setWinnerId(match.getWinnerId())
                        .setCreatedAt(match.getCreatedAt())
                        .setUpdatedAt(match.getUpdatedAt());

                createdMatches.add(response);
            }

            // Xoay: giữ phần tử đầu, xoay phần còn lại sang phải 1
            Long first = rotating.get(0);
            List<Long> rest = new ArrayList<>(rotating.subList(1, rotating.size()));
            Collections.rotate(rest, 1); // xoay phải 1
            List<Long> newRot = new ArrayList<>();
            newRot.add(first);
            newRot.addAll(rest);
            rotating = newRot;
        }
    }

    return createdMatches;
}

    @Override
    public RoundRobinRankingResponse getRoundRobinRankings(Long tournamentId, Long workspaceId, int roundNumber) {
        // 1️⃣ Kiểm tra tournament có thuộc workspace không
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found: " + tournamentId));

        if (!tournament.getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("Tournament does not belong to workspace " + workspaceId);
        }

        // 2️⃣ Lấy danh sách trận đấu (lọc theo round + loại ROUND_ROBIN)
        List<Match> matches = matchRepository.findByTournamentIdAndWorkspaceIdAndTournamentRound(
                tournamentId, workspaceId, roundNumber
        );

        // Nếu repository chưa có hàm này, thêm vào:
        // List<Match> findByTournamentIdAndWorkspaceIdAndTournamentRound(Long tournamentId, Long workspaceId, Integer tournamentRound);

        // 3️⃣ Nhóm theo bảng (note = "A","B","C"...)
        Map<String, List<Match>> matchesByGroup = matches.stream()
                .filter(m -> m.getTournamentRoundType() == Tournament.TournamentType.ROUND_ROBIN)
                .filter(m -> m.getNote() != null && !m.getNote().isEmpty())
                .collect(Collectors.groupingBy(Match::getNote));

        // 4️⃣ Tạo kết quả cho từng bảng
        Map<Integer, List<RoundRobinTeamResponse>> rankingsMap = new HashMap<>();
        int groupIndex = 1; // A=1, B=2,...

        for (Map.Entry<String, List<Match>> entry : matchesByGroup.entrySet()) {
            List<Match> groupMatches = entry.getValue();

            class TeamStats {
                Long teamId;
                int wins = 0;
                int losses = 0;
                int ties = 0;
                List<String> recentResults = new ArrayList<>();
                TeamStats(Long teamId) { this.teamId = teamId; }
            }

            Map<Long, TeamStats> statsMap = new HashMap<>();

            for (Match m : groupMatches) {
                Long team1Id = m.getTeam1Id();
                Long team2Id = m.getTeam2Id();
                Integer score1 = m.getScoreTeam1();
                Integer score2 = m.getScoreTeam2();

                if (team1Id == null || team2Id == null || score1 == null || score2 == null) continue;

                statsMap.putIfAbsent(team1Id, new TeamStats(team1Id));
                statsMap.putIfAbsent(team2Id, new TeamStats(team2Id));

                TeamStats t1 = statsMap.get(team1Id);
                TeamStats t2 = statsMap.get(team2Id);

                if (score1 > score2) {
                    t1.wins++; t2.losses++;
                    t1.recentResults.add("W"); t2.recentResults.add("L");
                } else if (score1 < score2) {
                    t1.losses++; t2.wins++;
                    t1.recentResults.add("L"); t2.recentResults.add("W");
                } else {
                    t1.ties++; t2.ties++;
                    t1.recentResults.add("T"); t2.recentResults.add("T");
                }
            }

            // 5️⃣ Lấy danh sách team từ repository
            List<Long> teamIds = new ArrayList<>(statsMap.keySet());
            List<TeamPlayer> teamPlayers = teamPlayerRepository.findByTeamIdIn(teamIds);
            // 3️⃣ Tạo map teamId -> list playerId
            Map<Long, List<Long>> teamToPlayerIds = teamPlayers.stream()
                    .collect(Collectors.groupingBy(
                            TeamPlayer::getTeamId,
                            Collectors.mapping(TeamPlayer::getPlayerId, Collectors.toList())
                    ));
            Map<Long, Team> teamMap = teamRepository.findAllById(teamIds)
                    .stream().collect(Collectors.toMap(Team::getId, t -> t));

            // 6️⃣ Map sang DTO RoundRobinTeamResponse

            List<RoundRobinTeamResponse> rankingList = statsMap.values().stream()
                    .map(ts -> {
                        Team team = teamMap.get(ts.teamId);
                        if (team == null) return null;
                        // Lấy playerIds của team
                        List<Long> playerIds = teamToPlayerIds.getOrDefault(team.getId(), List.of());

                        // Map sang PlayerResponse
                        List<PlayerResponse> playerResponses = playerIds.isEmpty() ? List.of() :
                                playerRepository.findAllById(playerIds).stream()
                                        .map(playerMapper::entityToResponse)
                                        .toList();
                        TeamResponse teamResp = new TeamResponse(
                                team.getId(),
                                team.getWorkspaceId(),
                                team.getTeamName(),
                                playerResponses,
                                team.getCreatedAt(),
                                team.getUpdatedAt()
                        );
                        return new RoundRobinTeamResponse(teamResp, ts.wins, ts.losses, ts.ties, ts.recentResults);
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt((RoundRobinTeamResponse r) -> r.getWins()).reversed())
                    .toList();

            // 7️⃣ Lưu vào rankingsMap (theo chỉ số bảng)
            rankingsMap.put(groupIndex++, rankingList);
        }

        return new RoundRobinRankingResponse(rankingsMap);
    }





}
