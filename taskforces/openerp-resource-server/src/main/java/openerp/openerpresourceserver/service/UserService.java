package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> getAllUsers();

    UserEntity getUserById(String id);

}
