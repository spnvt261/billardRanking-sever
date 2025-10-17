package com.billard.BillardRankings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PlayerRequest {
    
    @NotNull(message = "Workspace ID is required")
    private Long workspaceId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;
    
    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;
    
    @Size(max = 255, message = "Avatar URL must not exceed 255 characters")
    private String avatarUrl;

    @NotNull(message = "Start ELO is required")
    private Integer startElo;

    private String description;
    
//    @NotNull(message = "Joined date is required")
    private LocalDate joinedDate;
}
