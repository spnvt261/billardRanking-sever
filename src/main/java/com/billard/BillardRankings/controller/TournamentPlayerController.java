package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.TournamentPlayerRequest;
import com.billard.BillardRankings.dto.TournamentPlayerResponse;
import com.billard.BillardRankings.service.TournamentPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament-players")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class TournamentPlayerController {

    private final TournamentPlayerService tournamentPlayerService;

    @GetMapping
    public ResponseEntity<ListResponse<TournamentPlayerResponse>> getAllTournamentPlayers(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentPlayerService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentPlayerResponse> getTournamentPlayer(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentPlayerService.findById(id, workspaceId));
    }

    @PostMapping
    public ResponseEntity<TournamentPlayerResponse> createTournamentPlayer(@RequestBody TournamentPlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentPlayerService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentPlayerResponse> updateTournamentPlayer(
            @PathVariable("id") Long id,
            @RequestBody TournamentPlayerRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentPlayerService.save(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournamentPlayer(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        tournamentPlayerService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTournamentPlayers(
            @RequestBody List<Long> ids,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        tournamentPlayerService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
