package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchRequest;
import com.billard.BillardRankings.dto.MatchResponse;
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
