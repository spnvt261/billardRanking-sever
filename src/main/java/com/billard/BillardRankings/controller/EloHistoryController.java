package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.EloHistoryRequest;
import com.billard.BillardRankings.dto.EloHistoryResponse;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.service.EloHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elo-histories")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class EloHistoryController {

    private final EloHistoryService eloHistoryService;

    @GetMapping
    public ResponseEntity<ListResponse<EloHistoryResponse>> getAllEloHistories(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(eloHistoryService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EloHistoryResponse> getEloHistory(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(eloHistoryService.findById(id, workspaceId));
    }

    @PostMapping
    public ResponseEntity<EloHistoryResponse> createEloHistory(@RequestBody EloHistoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eloHistoryService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EloHistoryResponse> updateEloHistory(
            @PathVariable("id") Long id,
            @RequestBody EloHistoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(eloHistoryService.save(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEloHistory(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        eloHistoryService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEloHistories(
            @RequestBody List<Long> ids,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        eloHistoryService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
