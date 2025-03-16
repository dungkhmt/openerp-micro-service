package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;


import com.hust.openerp.taskmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
