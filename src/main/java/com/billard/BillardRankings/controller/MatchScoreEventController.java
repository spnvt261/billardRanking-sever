package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.dto.MatchScoreEventRequest;
import com.billard.BillardRankings.dto.MatchScoreEventResponse;
import com.billard.BillardRankings.entity.Match;
import com.billard.BillardRankings.repository.MatchRepository;
import com.billard.BillardRankings.service.MatchScoreEventService;
import com.billard.BillardRankings.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/match-score-events")
@RequiredArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class MatchScoreEventController {

    private final MatchScoreEventService matchScoreEventService;
    private final MatchRepository matchRepository;

    @GetMapping
    public ResponseEntity<ListResponse<MatchScoreEventResponse>> getAllMatchScoreEvents(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(matchScoreEventService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchScoreEventResponse> getMatchScoreEvent(
            @PathVariable("id") Long id,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(matchScoreEventService.findById(id, workspaceId));
    }

    @PostMapping
    public ResponseEntity<MatchScoreEventResponse> createMatchScoreEvent(
            @RequestBody MatchScoreEventRequest request,
            @RequestParam("token") String scoreCounterLockToken
    ) {
        // Kiểm tra token
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        if (!scoreCounterLockToken.equals(match.getScoreCounterLockToken())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid score counter lock token");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(matchScoreEventService.save(request));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<MatchScoreEventResponse> updateMatchScoreEvent(
//            @PathVariable("id") Long id,
//            @RequestBody MatchScoreEventRequest request
//    ) {
//        return ResponseEntity.status(HttpStatus.OK).body(matchScoreEventService.save(id, request));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteMatchScoreEvent(
//            @PathVariable("id") Long id,
//            @RequestParam(name = "workspaceId") Long workspaceId
//    ) {
//        matchScoreEventService.delete(id, workspaceId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//
//    @DeleteMapping
//    public ResponseEntity<Void> deleteMatchScoreEvents(
//            @RequestBody List<Long> ids,
//            @RequestParam(name = "workspaceId") Long workspaceId
//    ) {
//        matchScoreEventService.delete(ids, workspaceId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}
