package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.PrizeHistoryRequest;
import com.billard.BillardRankings.dto.PrizeHistoryResponse;
import com.billard.BillardRankings.service.PrizeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prize-histories")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class PrizeHistoryController {

    private final PrizeHistoryService prizeHistoryService;

    @GetMapping
    public ResponseEntity<ListResponse<PrizeHistoryResponse>> getAllPrizeHistories(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(prizeHistoryService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrizeHistoryResponse> getPrizeHistory(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(prizeHistoryService.findById(id, workspaceId));
    }

    @PostMapping
    public ResponseEntity<PrizeHistoryResponse> createPrizeHistory(@RequestBody PrizeHistoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prizeHistoryService.save(request));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<PrizeHistoryResponse> updatePrizeHistory(
//            @PathVariable("id") Long id,
//            @RequestBody PrizeHistoryRequest request
//    ) {
//        return ResponseEntity.status(HttpStatus.OK).body(prizeHistoryService.save(id, request));
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePrizeHistory(
//            @PathVariable("id") Long id,
//            @RequestParam(name = "workspaceId") Long workspaceId
//    ) {
//        prizeHistoryService.delete(id, workspaceId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

//    @DeleteMapping
//    public ResponseEntity<Void> deletePrizeHistories(
//            @RequestBody List<Long> ids,
//            @RequestParam(name = "workspaceId") Long workspaceId
//    ) {
//        prizeHistoryService.delete(ids, workspaceId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
