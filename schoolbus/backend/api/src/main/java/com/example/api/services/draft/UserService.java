package com.example.api.services.draft;

import com.example.shared.db.entities.User;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(String id);

    void synchronizeUser(String userId, String email, String firstName, String lastName);

}
