package com.billard.BillardRankings.dto;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
public class ListResponse<T> {
    List<T> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean last;

    // ✅ Constructor gốc (dành cho Page)
    public <E> ListResponse(List<T> content, Page<E> page) {
        this.content = content;
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }

    // ✅ Constructor mới (dành cho phân trang thủ công)
    public ListResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public static <T, E> ListResponse<T> of(List<T> content, Page<E> page) {
        return new ListResponse<>(content, page);
    }
}
