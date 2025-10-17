package com.billard.BillardRankings.service;

import com.billard.BillardRankings.constant.FieldName;
import com.billard.BillardRankings.dto.ListResponse;
import com.billard.BillardRankings.exception.ResourceNotFoundException;
import com.billard.BillardRankings.mapper.GenericMapper;
import com.billard.BillardRankings.utils.SearchUtils;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Arrays;
import java.util.List;

public abstract class BaseCrudServiceImpl<E, I, O, ID> implements CrudService<ID, I, O> {
    
    protected abstract JpaRepository<E, ID> getRepository();
    protected abstract JpaSpecificationExecutor<E> getSpecificationRepository();
    protected abstract GenericMapper<E, I, O> getMapper();
    protected abstract String getResourceName();
    protected abstract List<String> getSearchFields();
    protected abstract Long getWorkspaceIdFromEntity(E entity);
    protected abstract Long getWorkspaceIdFromRequest(I request);
    
    @Override
    public ListResponse<O> findAll(int page, int size, String sort, String filter, String search, boolean all, Long workspaceId) {
        Specification<E> sortable = RSQLJPASupport.toSort(sort);
        Specification<E> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<E> searchable = SearchUtils.parse(search, getSearchFields());
        Specification<E> workspaceFilter = (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("workspaceId"), workspaceId);
        
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<E> entities = getSpecificationRepository().findAll(sortable.and(filterable).and(searchable).and(workspaceFilter), pageable);
        List<O> entityResponses = getMapper().entityToResponse(entities.getContent());
        return new ListResponse<>(entityResponses, entities);
    }
    
    @Override
    public O findById(ID id, Long workspaceId) {
        return getRepository().findById(id)
                .map(entity -> {
                    if (getWorkspaceIdFromEntity(entity).equals(workspaceId)) {
                        return getMapper().entityToResponse(entity);
                    }
                    throw new ResourceNotFoundException(getResourceName(), FieldName.ID, id);
                })
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), FieldName.ID, id));
    }
    
    @Override
    public O save(I request) {
        E entity = getMapper().requestToEntity(request);
        entity = getRepository().save(entity);
        return getMapper().entityToResponse(entity);
    }
    
    @Override
    public O save(ID id, I request) {
        return getRepository().findById(id)
                .map(existingEntity -> {
                    if (getWorkspaceIdFromEntity(existingEntity).equals(getWorkspaceIdFromRequest(request))) {
                        E updatedEntity = getMapper().partialUpdate(existingEntity, request);
                        return getMapper().entityToResponse(getRepository().save(updatedEntity));
                    }
                    throw new ResourceNotFoundException(getResourceName(), FieldName.ID, id);
                })
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), FieldName.ID, id));
    }
    
    @Override
    public void delete(ID id, Long workspaceId) {
        getRepository().findById(id)
                .ifPresentOrElse(
                    entity -> {
                        if (getWorkspaceIdFromEntity(entity).equals(workspaceId)) {
                            getRepository().deleteById(id);
                        } else {
                            throw new ResourceNotFoundException(getResourceName(), FieldName.ID, id);
                        }
                    },
                    () -> {
                        throw new ResourceNotFoundException(getResourceName(), FieldName.ID, id);
                    }
                );
    }
    
    @Override
    public void delete(List<ID> ids, Long workspaceId) {
        List<ID> validIds = getRepository().findAllById(ids).stream()
                .filter(entity -> getWorkspaceIdFromEntity(entity).equals(workspaceId))
                .map(entity -> getIdFromEntity(entity))
                .toList();
        getRepository().deleteAllById(validIds);
    }
    
    protected abstract ID getIdFromEntity(E entity);
}
