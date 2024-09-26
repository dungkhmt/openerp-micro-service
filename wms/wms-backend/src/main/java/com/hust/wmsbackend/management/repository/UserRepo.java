package com.hust.wmsbackend.management.repository;


import com.hust.wmsbackend.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
