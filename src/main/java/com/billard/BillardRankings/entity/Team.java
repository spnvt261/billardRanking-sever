package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Team extends BaseEntity {
    
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;
    
    @Column(name = "team_name", length = 150)
    private String teamName;
}
