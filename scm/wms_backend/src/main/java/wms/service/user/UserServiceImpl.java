package wms.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.entity.UserRegister;
import wms.exception.CustomException;
import wms.repo.UserRepo;
import wms.service.BaseService;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends BaseService implements IUserService {
    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public ReturnPaginationDTO<UserRegister> getAllUsersPaging(int page, int pageSize, String sortField, boolean isSortAsc, String role, String textSearch) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? PageRequest.of(page - 1, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<UserRegister> users = userRepo.search(pageable, role, textSearch);
        return getPaginationResult(users.getContent(), page, users.getTotalPages(), users.getTotalElements());
    }

    @Override
    public List<UserRegister> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public List<UserRegister> getAllUsersByRole(String roleName) {
        return userRepo.getUsersByRole(roleName);
    }

    public UserRegister getUserById(long id) {
        return null;
    }

    public UserRegister getUserByCode(String code) {
        return null;
    }

    @Override
    public UserRegister updateUserLogin(ProductDTO productDTO, long id) throws CustomException {
        return null;
    }

    @Override
    public void deleteDeliveryTripById(long id) {

    }
}
