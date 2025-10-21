package com.billard.BillardRankings.repository;

import com.billard.BillardRankings.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {

    // ✅ Dành cho MatchServiceImpl
    @Query("SELECT p FROM Player p WHERE p.id IN :playerIds")
    List<Player> findByIds(@Param("playerIds") List<Long> playerIds);
    @Query("""
    SELECT p FROM Player p
    JOIN TeamPlayer tp ON tp.playerId = p.id
    WHERE tp.teamId = :teamId
""")
    List<Player> findPlayersByTeamId(@Param("teamId") Long teamId);
    // ✅ Dành cho PlayerServiceImpl
    Page<Player> findByWorkspaceId(Long workspaceId, Pageable pageable);
    List<Player> findByWorkspaceId(Long workspaceId);
    List<Player> findAllByWorkspaceId(Long workspaceId);

}
