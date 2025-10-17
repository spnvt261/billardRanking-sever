package com.billard.BillardRankings.service;

import com.billard.BillardRankings.dto.WorkspaceDTO;
import com.billard.BillardRankings.entity.Workspace;
import com.billard.BillardRankings.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final PasswordEncoder passwordEncoder;

    public WorkspaceDTO validateWorkspaceAccess(String shareKey, String password) {
        return workspaceRepository.findByShareKey(shareKey)
            .filter(workspace -> passwordEncoder.matches(password, workspace.getPasswordHash())
            )
            .map(this::convertToDTO)
            .orElse(null);
    }

    public WorkspaceDTO getWorkspaceByShareKey(String shareKey) {
        return workspaceRepository.findByShareKey(shareKey)
            .map(this::convertToDTO)
            .orElse(null);
    }

    private WorkspaceDTO convertToDTO(Workspace workspace) {
        if (workspace == null) {
            return null;
        }
        return WorkspaceDTO.builder()
            .id(workspace.getId())
            .name(workspace.getName())
            .shareKey(workspace.getShareKey())
            .build();
    }
}