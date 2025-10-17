package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.ResourceName;
import com.billard.BillardRankings.dto.TeamRequest;
import com.billard.BillardRankings.dto.TeamResponse;
import com.billard.BillardRankings.entity.Team;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.mapper.TeamMapper;
import com.billard.BillardRankings.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends BaseCrudServiceImpl<Team, TeamRequest, TeamResponse, Long> implements TeamService {
    
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    
    @Override
    protected JpaRepository<Team, Long> getRepository() {
        return teamRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Team> getSpecificationRepository() {
        return teamRepository;
    }
    
    @Override
    protected GenericMapper<Team, TeamRequest, TeamResponse> getMapper() {
        return teamMapper;
    }
    
    @Override
    protected String getResourceName() {
        return ResourceName.TEAM;
    }
    
    @Override
    protected List<String> getSearchFields() {
        return Arrays.asList("teamName");
    }
    
    @Override
    protected Long getWorkspaceIdFromEntity(Team entity) {
        return entity.getWorkspaceId();
    }
    
    @Override
    protected Long getWorkspaceIdFromRequest(TeamRequest request) {
        return request.getWorkspaceId();
    }
    
    @Override
    protected Long getIdFromEntity(Team entity) {
        return entity.getId();
    }
}
