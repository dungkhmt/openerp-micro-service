package com.hust.baseweb.repo;

import com.hust.baseweb.entity.UserRegister;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public interface UserRegisterRepo extends JpaRepository<UserRegister, String> {

}
