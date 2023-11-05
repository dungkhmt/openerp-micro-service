package com.hust.openerp.taskmanagement.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.repository.UserRepository;
import com.hust.openerp.taskmanagement.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@jakarta.transaction.Transactional
public class UserServiceImplement implements UserService {
  private final UserRepository userLoginRepo;

  @Override
  public User findById(String userLoginId) {
    return userLoginRepo.findById(userLoginId).orElse(null);
  }

  @Override
  public java.util.List<User> getAllUsers() {
    return userLoginRepo.findAll();
  }
}
