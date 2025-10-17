package com.billard.BillardRankings.controller;

import com.billard.BillardRankings.constant.AppConstants;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.service.CrudService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@Setter
@Scope("prototype")
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class GenericController<I, O> {

    private CrudService<Long, I, O> crudService;
    private Class<I> requestType;

    public ResponseEntity<ListResponse<O>> getAllResources(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all,
            @RequestParam(name = "workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(crudService.findAll(page, size, sort, filter, search, all, workspaceId));
    }

    public ResponseEntity<O> getResource(@PathVariable("id") Long id, @RequestParam(name = "workspaceId") Long workspaceId) {
        return ResponseEntity.status(HttpStatus.OK).body(crudService.findById(id, workspaceId));
    }

    public ResponseEntity<O> createResource(@RequestBody JsonNode request, @RequestParam(name = "workspaceId") Long workspaceId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(crudService.save(request, requestType));
    }

    public ResponseEntity<O> updateResource(@PathVariable("id") Long id, @RequestBody JsonNode request, @RequestParam(name = "workspaceId") Long workspaceId) {
        return ResponseEntity.status(HttpStatus.OK).body(crudService.save(id, request, requestType));
    }

    public ResponseEntity<Void> deleteResource(@PathVariable("id") Long id, @RequestParam(name = "workspaceId") Long workspaceId) {
        crudService.delete(id, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<Void> deleteResources(@RequestBody List<Long> ids, @RequestParam(name = "workspaceId") Long workspaceId) {
        crudService.delete(ids, workspaceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
