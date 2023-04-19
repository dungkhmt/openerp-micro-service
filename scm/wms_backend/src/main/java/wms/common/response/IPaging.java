package wms.common.response;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface IPaging extends ISort {
    default Pageable getPaging(int page, int size, Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
    default Pageable getDefaultPage(int page, int size) {
        return getPaging(page, size, getSortDESC());
    }
    default Pageable getDefaultPageNonNativeQuery(int page, int size) {
        return getPaging(page, size, getSortDESC2());
    }
    default Pageable getDefaultPage3(int page, int size, String column, Sort.Direction type) {
        return getPaging(page, size, getSortDESC3(column, type));
    }
}
