package com.billard.BillardRankings.mapper;

import com.billard.BillardRankings.dto.WorkspaceRequest;
import com.billard.BillardRankings.dto.WorkspaceResponse;
import com.billard.BillardRankings.entity.Workspace;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkspaceMapper extends GenericMapper<Workspace, WorkspaceRequest, WorkspaceResponse> {

    default LocalDateTime map(Instant value) {
        return value != null ? LocalDateTime.ofInstant(value, ZoneId.systemDefault()) : null;
    }

    @Override
    WorkspaceResponse entityToResponse(Workspace entity);

    @Override
    @Mapping(
            target = "passwordHash",
            expression = "java(com.billard.BillardRankings.util.PasswordUtils.encode(request.getPassword()))"
    )
    Workspace requestToEntity(WorkspaceRequest request);

    @Override
    Workspace partialUpdate(@MappingTarget Workspace entity, WorkspaceRequest request);

    @AfterMapping
    default void afterPartialUpdate(@MappingTarget Workspace entity, WorkspaceRequest request) {
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            entity.setPasswordHash(
                    com.billard.BillardRankings.util.PasswordUtils.encode(request.getPassword())
            );
        }
    }

    @Override
    List<WorkspaceResponse> entityToResponse(List<Workspace> entities);
}

