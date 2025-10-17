package com.billard.BillardRankings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class WorkspaceResponse {
    private Long id;
    private String name;
    private String passwordHash;
    private Long shareKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
