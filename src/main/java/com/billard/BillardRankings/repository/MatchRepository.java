package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match> {
    List<Match> findByWorkspaceId(Long workspaceId);
    Page<Match> findByWorkspaceId(Long workspaceId, Pageable pageable);
}
