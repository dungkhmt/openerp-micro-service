package com.hust.baseweb.applications.examclassandaccount.repo;

import com.hust.baseweb.applications.examclassandaccount.entity.RandomGeneratedUserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomGeneratedUserLoginRepo extends JpaRepository<RandomGeneratedUserLogin, String> {

}
