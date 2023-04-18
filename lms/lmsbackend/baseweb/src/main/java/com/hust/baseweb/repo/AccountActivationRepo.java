package com.hust.baseweb.repo;

import com.hust.baseweb.entity.AccountActivation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountActivationRepo extends JpaRepository<AccountActivation, UUID> {

}
