package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.TeamRequest;
import com.billard.BillardRankings.dto.TeamResponse;
import com.billard.BillardRankings.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<ListResponse<TeamResponse>> getAllTeams(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.findById(id, workspaceId));
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable("id") Long id,
            @RequestBody TeamRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.save(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        teamService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTeams(
            @RequestBody List<Long> ids,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        teamService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
