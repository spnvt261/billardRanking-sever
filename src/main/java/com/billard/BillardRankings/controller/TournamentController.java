package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchResponse;
import com.billard.BillardRankings.dto.TournamentRequest;
import com.billard.BillardRankings.dto.TournamentResponse;
import com.billard.BillardRankings.dto.roundType.OtherRoundTypeRequest;
import com.billard.BillardRankings.dto.roundType.RoundRobinRankingResponse;
import com.billard.BillardRankings.dto.roundType.RoundRobinRequest;
import com.billard.BillardRankings.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<ListResponse<TournamentResponse>> getAllTournaments(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournament(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        try {
            TournamentResponse resp = tournamentService.findById(id, workspaceId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace(); // Log stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(@RequestBody TournamentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable("id") Long id,
            @RequestBody TournamentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentService.save(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        tournamentService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTournaments(
            @RequestBody List<Long> ids,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        tournamentService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<Map<String, Object>> getAllTournamentsGroupedByQuarter(
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.ok(tournamentService.getAllTournamentsGroupedByQuarter(workspaceId));
    }

    @GetMapping("/roundrobin-rankings")
    public ResponseEntity<RoundRobinRankingResponse> getRoundRobinRankings(
            @RequestParam(name = "tournamentId") Long tournamentId,
            @RequestParam(name = "workspaceId") Long workspaceId,
            @RequestParam(name = "roundNumber") int roundNumber
    ) {
        RoundRobinRankingResponse response = tournamentService.getRoundRobinRankings(tournamentId, workspaceId, roundNumber);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/create-round-robin")
    public ResponseEntity<?> createRoundRobin(
            @RequestBody RoundRobinRequest request,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        try {
            List<MatchResponse> matches = tournamentService.createRoundRobin(request, workspaceId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create round robin: " + e.getMessage());
        }
    }

    // TournamentController.java
    @PostMapping("/other-round-type")
    public ResponseEntity<?> createOtherRoundType(
            @RequestBody OtherRoundTypeRequest request,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        try {
            // Gọi service, hiện tại chưa có logic
            tournamentService.createOtherRoundType(request, workspaceId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Other round type created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create other round type: " + e.getMessage());
        }
    }


}
