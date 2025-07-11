package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.form.UpdateProfileForm;
import com.hust.openerp.taskmanagement.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User findById(String userLoginId);

    List<User> getAllUsers();

    void synchronizeUser(String userId, String email, String firstName, String lastName);

    List<User> searchUser(String q);

    List<User> getUserCreateTaskAssignMe(String userId);

    List<User> getUserAssignTaskAssignMe(String userId);
    
    void updateProfile(String userId, UpdateProfileForm updateProfileForm);
}
