package com.billard.BillardRankings.utils;

import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SearchUtils {
    
    public static <T> Specification<T> parse(String search, List<String> searchFields) {
        if (search == null || search.trim().isEmpty()) {
            return Specification.where(null);
        }
        
        StringBuilder rsqlQuery = new StringBuilder();
        for (int i = 0; i < searchFields.size(); i++) {
            if (i > 0) {
                rsqlQuery.append(" or ");
            }
            rsqlQuery.append(searchFields.get(i)).append("=ilike=").append(search);
        }
        
        return RSQLJPASupport.toSpecification(rsqlQuery.toString());
    }
}
