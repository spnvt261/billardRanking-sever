package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.PlayerListResponse;
import com.billard.BillardRankings.dto.PlayerRequest;
import com.billard.BillardRankings.dto.PlayerResponse;
import com.billard.BillardRankings.entity.Player;
import com.billard.BillardRankings.exception.ResourceNotFoundException;
import com.billard.BillardRankings.mapper.PlayerMapper;
import com.billard.BillardRankings.repository.EloHistoryRepository;
import com.billard.BillardRankings.repository.PlayerRepository;
import com.billard.BillardRankings.repository.PrizeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final EloHistoryRepository eloHistoryRepository;
    private final PrizeHistoryRepository prizeHistoryRepository;


    @Override
    public ListResponse<PlayerResponse> findAll(int page, int size, String sort, String filter, String search, boolean all, Long workspaceId) {
        // 1️⃣ Lấy toàn bộ player trong workspace
        List<Player> allPlayers = playerRepository.findByWorkspaceId(workspaceId);

        // 2️⃣ Map entity -> DTO và tính Elo
        List<PlayerResponse> allResponses = allPlayers.stream()
                .map(this::mapPlayerWithEloAndPrize)
                .toList();

        // 3️⃣ Sắp xếp toàn bộ theo Elo giảm dần
        List<PlayerResponse> sortedResponses = allResponses.stream()
                .sorted((p1, p2) -> {
                    int cmp = Integer.compare(p2.getElo(), p1.getElo());
                    if (cmp == 0) {
                        // Nếu Elo bằng nhau thì sắp theo tên để ổn định
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                    return cmp;
                })
                .toList();

        // 4️⃣ Gán rank cho tất cả player
        for (int i = 0; i < sortedResponses.size(); i++) {
            sortedResponses.get(i).setRank(i + 1);
        }

        // 5️⃣ Nếu all == true, trả về toàn bộ
        if (all) {
            return new ListResponse<>(
                    sortedResponses,
                    1,
                    sortedResponses.size(),
                    sortedResponses.size(),
                    1,
                    true
            );
        }

        // 6️⃣ Phân trang thủ công
        int totalElements = sortedResponses.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.max(0, (page - 1) * size);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<PlayerResponse> pagedResponses = sortedResponses.subList(fromIndex, toIndex);

        boolean last = page >= totalPages;

        // 7️⃣ Trả về đúng format yêu cầu
        return new ListResponse<>(
                pagedResponses,
                page,
                size,
                totalElements,
                totalPages,
                last
        );
    }

    @Override
    public ListResponse<PlayerResponse> findAllSortedByPrize(int page, int size, String sort, String filter, String search, boolean all, Long workspaceId) {
        // 1️⃣ Lấy toàn bộ player trong workspace
        List<Player> allPlayers = playerRepository.findByWorkspaceId(workspaceId);

        // 2️⃣ Map entity -> DTO và tính Elo + Prize
        List<PlayerResponse> allResponses = allPlayers.stream()
                .map(this::mapPlayerWithEloAndPrize) // helper đã tính Elo + Prize
                .toList();

        // 3️⃣ Sắp xếp theo prize giảm dần
        List<PlayerResponse> sortedResponses = allResponses.stream()
                .sorted((p1, p2) -> {
                    int cmp = Integer.compare(p2.getPrize(), p1.getPrize());
                    if (cmp == 0) {
                        // Nếu prize bằng nhau, sort theo Elo giảm dần để ổn định
                        return Integer.compare(p2.getElo(), p1.getElo());
                    }
                    return cmp;
                })
                .toList();

        // 4️⃣ Gán rank (dựa theo prize)
        for (int i = 0; i < sortedResponses.size(); i++) {
            sortedResponses.get(i).setRank(i + 1);
        }

        // 5️⃣ Trả toàn bộ nếu all == true
        if (all) {
            return new ListResponse<>(
                    sortedResponses,
                    1,
                    sortedResponses.size(),
                    sortedResponses.size(),
                    1,
                    true
            );
        }

        // 6️⃣ Phân trang thủ công
        int totalElements = sortedResponses.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.max(0, (page - 1) * size);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<PlayerResponse> pagedResponses = sortedResponses.subList(fromIndex, toIndex);

        boolean last = page >= totalPages;

        return new ListResponse<>(
                pagedResponses,
                page,
                size,
                totalElements,
                totalPages,
                last
        );
    }


    @Override
    public PlayerResponse findById(Long id, Long workspaceId) {
        // 1️⃣ Lấy toàn bộ player trong workspace
        List<Player> allPlayers = playerRepository.findByWorkspaceId(workspaceId);

        // 2️⃣ Map entity -> DTO và tính Elo
        List<PlayerResponse> allResponses = allPlayers.stream()
                .map(this::mapPlayerWithEloAndPrize)
                .toList();

        // 3️⃣ Sắp xếp toàn bộ theo Elo giảm dần
        List<PlayerResponse> sortedResponses = allResponses.stream()
                .sorted((p1, p2) -> {
                    int cmp = Integer.compare(p2.getElo(), p1.getElo());
                    if (cmp == 0) {
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                    return cmp;
                })
                .toList();

        // 4️⃣ Gán rank cho tất cả player
        for (int i = 0; i < sortedResponses.size(); i++) {
            sortedResponses.get(i).setRank(i + 1);
        }

        // 5️⃣ Tìm player theo ID
        return sortedResponses.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, id));
    }


    @Override
    public PlayerResponse save(PlayerRequest request) {
        if (request.getWorkspaceId() == null || request.getWorkspaceId() <= 0) {
            throw new IllegalArgumentException("Workspace ID is required");
        }


        Player player = playerMapper.requestToEntity(request);
        // Trong cả hai phương thức save:
        if (player.getAvatarUrl() == null || player.getAvatarUrl().isEmpty()) {
            player.setAvatarUrl("https://res.cloudinary.com/djeohgclg/image/upload/v1760831936/qg3rplthuila4qdajd19.png");
        }
        player = playerRepository.save(player);
        return mapPlayerWithEloAndPrize(player);
    }

    @Override
    public PlayerResponse save(Long id, PlayerRequest request) {
        Player existingPlayer = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, id));

        if (!existingPlayer.getWorkspaceId().equals(request.getWorkspaceId())) {
            throw new ResourceNotFoundException(ResourceName.PLAYER, id);
        }

        Player updatedPlayer = playerMapper.partialUpdate(existingPlayer, request);
        if (updatedPlayer.getAvatarUrl() == null || updatedPlayer.getAvatarUrl().isEmpty()) {
            updatedPlayer.setAvatarUrl("https://res.cloudinary.com/djeohgclg/image/upload/v1760831936/qg3rplthuila4qdajd19.png");
        }
        updatedPlayer = playerRepository.save(updatedPlayer);
        return mapPlayerWithEloAndPrize(updatedPlayer);
    }

    @Override
    public void delete(Long id, Long workspaceId) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PLAYER, id));

        if (!player.getWorkspaceId().equals(workspaceId)) {
            throw new ResourceNotFoundException(ResourceName.PLAYER, id);
        }

        playerRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids, Long workspaceId) {
        List<Player> players = playerRepository.findAllById(ids);
        List<Player> validPlayers = players.stream()
                .filter(player -> player.getWorkspaceId().equals(workspaceId))
                .toList();
        playerRepository.deleteAll(validPlayers);
    }

    // ---------------- Helper ----------------
//    private PlayerResponse mapPlayerWithElo(Player player) {
//        PlayerResponse response = playerMapper.entityToResponse(player);
//
//        // Lấy EloHistory mới nhất
//        eloHistoryRepository.findFirstByPlayerIdOrderByIdDesc(player.getId())
//                .ifPresentOrElse(
//                        latestHistory -> response.setElo(latestHistory.getNewElo()),
//                        () -> response.setElo(player.getStartElo())
//                );
//
//        return response;
//    }
    private PlayerResponse mapPlayerWithEloAndPrize(Player player) {
        PlayerResponse response = playerMapper.entityToResponse(player);

        // ✅ Lấy EloHistory mới nhất
        eloHistoryRepository.findTopByWorkspaceIdAndPlayerIdOrderByIdDesc(player.getWorkspaceId(), player.getId())
                .ifPresentOrElse(
                        latestHistory -> response.setElo(latestHistory.getNewElo()),
                        () -> response.setElo(player.getStartElo())
                );

        // ✅ Lấy PrizeHistory mới nhất
        prizeHistoryRepository.findTopByWorkspaceIdAndPlayerIdOrderByIdDesc(player.getWorkspaceId(), player.getId())
                .ifPresentOrElse(
                        latestPrize -> response.setPrize(latestPrize.getNewPrize()),
                        () -> response.setPrize(player.getStartMoney())
                );

        return response;
    }


    @Override
    public List<PlayerListResponse> findAllSimple(Long workspaceId) {
        List<Player> players = playerRepository.findByWorkspaceId(workspaceId);
        return playerMapper.entityToSimpleResponse(players);
    }
}
