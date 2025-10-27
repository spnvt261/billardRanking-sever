package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchRequest;
import com.billard.BillardRankings.dto.MatchResponse;
import com.billard.BillardRankings.dto.MatchUpdateRequest;
import com.billard.BillardRankings.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<ListResponse<MatchResponse>> getAllMatches(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        // Mặc định sắp xếp theo id giảm dần
        String sort = "id,desc";
        return ResponseEntity.status(HttpStatus.OK)
                .body(matchService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(matchService.findById(id, workspaceId));
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<MatchResponse> getMatchByUuid(
            @PathVariable("uuid") String uuid,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(matchService.findByUuid(uuid, workspaceId));
    }

    @PostMapping("/create-score-counter")
    public ResponseEntity<MatchResponse> createScoreCounter(@RequestBody MatchRequest request) {
        MatchResponse response = matchService.createScoreCounter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@RequestBody MatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchResponse> updateMatch(
            @PathVariable("id") Long id,
            @RequestBody MatchRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(matchService.save(id, request));
    }



    // 🔒 LOCK: Khi người dùng mở trang score counter
    @PutMapping("/uuid/{uuid}/lock-score-counter")
    public ResponseEntity<String> lockScoreCounter(
            @PathVariable("uuid") String uuid,
            @RequestParam("workspaceId") Long workspaceId,
            @RequestParam("raceTo") int raceTo // 👈 thêm tham số mới
    ) {
        String token = matchService.lockScoreCounterByUuid(uuid, workspaceId,raceTo);
        return ResponseEntity.ok(token);
    }

    // 🔁 REFRESH: Gia hạn lock khi người dùng vẫn đang mở trang
    @PutMapping("/uuid/{uuid}/refresh-score-counter-lock")
    public ResponseEntity<Void> refreshScoreCounterLock(
            @PathVariable("uuid") String uuid,
            @RequestParam("workspaceId") Long workspaceId,
            @RequestParam("token") String token
    ) {
        matchService.refreshScoreCounterLockByUuid(uuid, workspaceId, token);
        return ResponseEntity.ok().build();
    }

    // 🔓 UNLOCK: Khi người dùng rời trang hoặc đóng tab
    @PutMapping("/uuid/{uuid}/unlock-score-counter")
    public ResponseEntity<Void> unlockScoreCounter(
            @PathVariable("uuid") String uuid,
            @RequestParam("workspaceId") Long workspaceId,
            @RequestParam("token") String token
    ) {
        matchService.unlockScoreCounterByUuid(uuid, workspaceId, token);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/uuid/{uuid}/verify-score-counter-token")
    public ResponseEntity<Boolean> verifyScoreCounterToken(
            @PathVariable String uuid,
            @RequestParam Long workspaceId,
            @RequestParam String token
    ) {
        boolean valid = matchService.verifyScoreCounterToken(uuid, token);
        return ResponseEntity.ok(valid);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        matchService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMatches(
            @RequestBody List<Long> ids,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        matchService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/by-round")
    public ResponseEntity<List<MatchResponse>> getMatchesByRound(
            @RequestParam("tournamentId") Long tournamentId,
            @RequestParam("roundNumber") int roundNumber,
            @RequestParam("workspaceId") Long workspaceId
    ) {
        List<MatchResponse> matches = matchService.findByTournamentAndRound(tournamentId, roundNumber, workspaceId);
        return ResponseEntity.ok(matches);
    }

}
