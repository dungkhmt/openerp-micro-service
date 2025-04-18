package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.entity.HomestayUser;

public interface HomestayUserService {
    HomestayUser getUserById(Long userId);

    List<HomestayUser> getAllUsers();

    List<HomestayUser> getUsersByType(String userType);

    HomestayUser getUserByEmail(String email);

    HomestayUser createUser(HomestayUser user);

    HomestayUser updateUser(Long userId, HomestayUser user);

    void deleteUser(Long userId);

    HomestayUser registerUser(HomestayUser user);

    boolean authenticate(String email, String password);
    
}
