package com.hust.openerp.taskmanagement.hr_management.application.port.out;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.User;

import java.util.List;

public interface UserUseCase {

    List<User> getAllUsers();

    User getUserById(String id);

    void synchronizeUser(String userId, String email, String firstName, String lastName);

}
