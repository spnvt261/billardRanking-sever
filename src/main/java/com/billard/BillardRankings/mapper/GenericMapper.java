package com.billard.BillardRankings.mapper;

import java.util.List;

public interface GenericMapper<E, I, O> {
    O entityToResponse(E entity);
    E requestToEntity(I request);
    E partialUpdate(E entity, I request);
    List<O> entityToResponse(List<E> entities);
}
