package openerp.openerpresourceserver.infrastructure.output.persistence.utils;

import openerp.openerpresourceserver.domain.model.PageInfo;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    public static Pageable getPageable(IPageableRequest request) {
        return getPageable(request, "id");
    }

    public static Pageable getPageable(IPageableRequest request, String sortBy) {
        return PageRequest.of(Math.toIntExact(request.getPage()), Math.toIntExact(request.getPageSize()),
                Sort.by(sortBy).descending());
    }

    public static <T> PageInfo getPageInfo(Page<T> result) {
        var pageInfo = new PageInfo();
        pageInfo.setTotalPage( (long) result.getTotalPages());
        pageInfo.setTotalRecords(result.getTotalElements());
        pageInfo.setPageSize((long) result.getPageable().getPageSize());
        pageInfo.setPage((long) result.getPageable().getPageNumber());
        if(result.hasNext()){
            pageInfo.setNextPage((long) result.nextPageable().getPageNumber());
        }
        if(result.hasPrevious()){
            pageInfo.setPreviousPage((long) result.previousPageable().getPageNumber());
        }
        return pageInfo;
    }
}
