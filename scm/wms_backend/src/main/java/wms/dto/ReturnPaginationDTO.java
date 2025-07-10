package wms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ReturnPaginationDTO<T>{

    int totalElements;

    int totalPages;

    int pageNumber;

    List<T> content = new ArrayList<>();
}
