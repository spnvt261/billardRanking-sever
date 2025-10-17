package com.billard.BillardRankings.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}