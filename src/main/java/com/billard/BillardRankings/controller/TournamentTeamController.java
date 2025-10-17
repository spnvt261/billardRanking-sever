package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.TournamentTeamRequest;
import com.billard.BillardRankings.dto.TournamentTeamResponse;
import com.billard.BillardRankings.service.TournamentTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament-teams")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class TournamentTeamController {

    private final TournamentTeamService tournamentTeamService;

    @GetMapping
    public ResponseEntity<ListResponse<TournamentTeamResponse>> getAllTournamentTeams(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentTeamService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentTeamResponse> getTournamentTeam(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentTeamService.findById(id, workspaceId));
    }

    @PostMapping
    public ResponseEntity<TournamentTeamResponse> createTournamentTeam(@RequestBody TournamentTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentTeamService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentTeamResponse> updateTournamentTeam(
            @PathVariable("id") Long id,
            @RequestBody TournamentTeamRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(tournamentTeamService.save(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournamentTeam(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        tournamentTeamService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTournamentTeams(
            @RequestBody List<Long> ids,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        tournamentTeamService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
