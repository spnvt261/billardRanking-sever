package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchDetailResponse;
import com.billard.BillardRankings.service.MatchDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match-details")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class MatchDetailController {
    
    private final MatchDetailService matchDetailService;
    
    @GetMapping
    public ResponseEntity<ListResponse<MatchDetailResponse>> getMatchDetails(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        ListResponse<MatchDetailResponse> response = matchDetailService.getMatchDetails(
                workspaceId, page, size, sort, filter, search, all);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchDetailResponse> getMatchDetailById(
            @PathVariable("matchId") Long matchId,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        MatchDetailResponse response = matchDetailService.getMatchDetailById(matchId, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
