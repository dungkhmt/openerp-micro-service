package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils;

import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageInfo;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    public static Pageable getPageable(IPageableRequest request) {
        if (request == null || request.getPage() == null || request.getPageSize() == null) {
            return Pageable.unpaged();
        }
        Sort sort = request.getOrder() == null || request.getSortBy() == null ?
            Sort.unsorted() : Sort.by(from(request.getOrder()), request.getSortBy());

        return PageRequest.of(
            Math.toIntExact(request.getPage()),
            Math.toIntExact(request.getPageSize()),
            sort
        );
    }

    public static Sort.Direction from(SortDirection sortDirection) {
        if (sortDirection == null) {
            return Sort.Direction.DESC;
        }
        return switch (sortDirection) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
            default -> Sort.Direction.DESC;
        };
    }

    public static <T> PageInfo getPageInfo(Page<T> result) {
        var pageInfo = new PageInfo();
        if (result.getPageable().isUnpaged()) {
            pageInfo.setTotalPage(1L);
            pageInfo.setTotalRecords(result.getTotalElements());
            pageInfo.setPageSize(result.getTotalElements());
            pageInfo.setPage(0L);
            pageInfo.setNextPage(null);
            pageInfo.setPreviousPage(null);
            return pageInfo;
        }

        pageInfo.setTotalPage((long) result.getTotalPages());
        pageInfo.setTotalRecords(result.getTotalElements());
        pageInfo.setPageSize((long) result.getPageable().getPageSize());
        pageInfo.setPage((long) result.getPageable().getPageNumber());
        if (result.hasNext()) {
            pageInfo.setNextPage((long) result.nextPageable().getPageNumber());
        }
        if (result.hasPrevious()) {
            pageInfo.setPreviousPage((long) result.previousPageable().getPageNumber());
        }
        return pageInfo;
    }
}
