package com.billard.BillardRankings.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Workspace key is required")
    private String workspaceKey;

    @NotBlank(message = "Password is required")
    private String password;
}