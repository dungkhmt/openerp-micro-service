package wms.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wms.dto.ReturnPaginationDTO;
import wms.dto.category.ProductUnitDTO;
import wms.entity.ProductUnit;
import wms.repo.ProductUnitRepo;
import wms.service.BaseService;


@Service
public class CategoryServiceImpl extends BaseService implements ICategoryService {
    @Autowired
    private ProductUnitRepo productUnitRepo;
    @Override
    public ProductUnit createProductUnit(ProductUnitDTO productUnit) {
        ProductUnit newUnit = new ProductUnit();
        newUnit.setCode(productUnit.getCode());
        newUnit.setName(productUnit.getName());
        return productUnitRepo.save(newUnit);
    }

    @Override
    public ReturnPaginationDTO<ProductUnit> getAllProductUnit(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ProductUnit> listUnits = productUnitRepo.search(pageable);
        return getPaginationResult(listUnits.getContent(), page, listUnits.getTotalPages(), listUnits.getTotalElements());
    }

    @Override
    public ProductUnit getProductUnitById(long id) {
        return productUnitRepo.getProductUnitById(id);
    }

    @Override
    public ProductUnit getProductUnitByCode(String code) {
        return productUnitRepo.getProductUnitByCode(code);
    }

}
