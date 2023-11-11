package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.entity.User;

import java.util.List;

public interface UserService {

    User findById(String userLoginId);

    List<User> getAllUsers();
}
