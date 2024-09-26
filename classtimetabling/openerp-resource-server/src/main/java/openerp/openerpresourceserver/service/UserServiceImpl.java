package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.UserEntity;
import openerp.openerpresourceserver.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;

    @Override
    public List<UserEntity> getAllUsers() {

        List<UserEntity> users = userRepo.findAll();
        return users;
    }

    @Override
    public UserEntity getUserById(String id) {

        Optional<UserEntity> userEntity = userRepo.findById(id);

        if (userEntity.isEmpty()) throw new NoSuchElementException("Not exist user with id " + id);

        return userEntity.get();
    }
}
