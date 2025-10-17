package com.billard.BillardRankings.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TeamRequest {
    
    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;
    
    @Size(max = 150, message = "Team name must not exceed 150 characters")
    private String teamName;
}
