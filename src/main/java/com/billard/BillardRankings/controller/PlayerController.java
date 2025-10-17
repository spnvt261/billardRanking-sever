package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.PlayerRequest;
import com.billard.BillardRankings.dto.PlayerResponse;
import com.billard.BillardRankings.service.PlayerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<ListResponse<PlayerResponse>> getAllPlayers(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.findById(id, workspaceId));
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@RequestBody @Valid PlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable("id") Long id,
            @RequestBody PlayerRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.save(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        playerService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePlayers(
            @RequestBody List<Long> ids,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        playerService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
