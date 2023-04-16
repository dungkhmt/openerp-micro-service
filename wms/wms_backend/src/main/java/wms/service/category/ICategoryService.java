package wms.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.category.ProductUnitDTO;
import wms.entity.ProductUnit;

import java.util.List;

public interface ICategoryService {
    ProductUnit createProductUnit(ProductUnitDTO productUnit);
    ReturnPaginationDTO<ProductUnit> getAllProductUnit(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    ProductUnit getProductUnitById(long id);
    ProductUnit getProductUnitByCode(String code);
}
