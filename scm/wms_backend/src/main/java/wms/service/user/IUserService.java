package wms.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.entity.UserLogin;
import wms.exception.CustomException;

import java.util.List;

public interface IUserService {
//    UserLogin createUserLogin(UserLoginDTO deliveryTripDTO, JwtAuthenticationToken token) throws CustomException;
    ReturnPaginationDTO<UserLogin> getAllUserLogins(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    List<UserLogin> getAllUsers();
    UserLogin getUserLoginById(long id);
    UserLogin getUserLoginByCode(String code);
    UserLogin updateUserLogin(ProductDTO productDTO, long id) throws CustomException;
    void deleteDeliveryTripById(long id);
}
