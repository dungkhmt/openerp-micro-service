package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(String id);

    void synchronizeUser(String userId, String email, String firstName, String lastName);

}
