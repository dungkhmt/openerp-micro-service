package com.example.shared.db.repo;


import com.example.shared.db.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
