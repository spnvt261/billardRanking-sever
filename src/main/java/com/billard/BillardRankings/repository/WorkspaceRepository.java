package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findByShareKey(String shareKey);
    void deleteByShareKey(String shareKey);
    boolean existsByShareKey(String shareKey);
}
