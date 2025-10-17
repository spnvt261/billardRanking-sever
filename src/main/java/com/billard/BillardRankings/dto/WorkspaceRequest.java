package com.billard.BillardRankings.dto;

import jakarta.validation.constraints.NotBlank;
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
public class WorkspaceRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;
    
    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;
    
    @NotNull(message = "Share key is required")
    private Long shareKey;
}
