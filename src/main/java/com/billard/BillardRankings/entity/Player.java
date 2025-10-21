package com.billard.BillardRankings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Player extends BaseEntity {
    
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Column(name = "nickname", length = 100)
    private String nickname;
    
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "start_elo", nullable = false)
    private Integer startElo = 0;

    @Column(name = "start_money", nullable = false)
    private Integer startMoney = 0;

    @Column(name = "is_friend")
    private Boolean isFriend = true;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "joined_date", nullable = true)
    private LocalDate joinedDate;


}
