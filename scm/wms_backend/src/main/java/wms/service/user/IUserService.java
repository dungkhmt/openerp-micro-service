package wms.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.entity.UserRegister;
import wms.exception.CustomException;

import java.util.List;

public interface IUserService {
    ReturnPaginationDTO<UserRegister> getAllUsersPaging(int page, int pageSize, String sortField, boolean isSortAsc, String role, String textSearch) throws JsonProcessingException;
    List<UserRegister> getAllUsers();
    List<UserRegister> getAllUsersByRole(String roleName);
    UserRegister getUserById(long id);
    UserRegister getUserByCode(String code);
    UserRegister updateUserLogin(ProductDTO productDTO, long id) throws CustomException;
    void deleteDeliveryTripById(long id);
}
