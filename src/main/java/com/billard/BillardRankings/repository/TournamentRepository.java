package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>, JpaSpecificationExecutor<Tournament> {
    List<Tournament> findByWorkspaceId(Long workspaceId);
    // ✅ Thêm method có pageable
    Page<Tournament> findByWorkspaceId(Long workspaceId, Pageable pageable);
}
