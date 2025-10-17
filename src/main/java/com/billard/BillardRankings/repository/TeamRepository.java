package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    List<Team> findByWorkspaceId(Long workspaceId);
}
