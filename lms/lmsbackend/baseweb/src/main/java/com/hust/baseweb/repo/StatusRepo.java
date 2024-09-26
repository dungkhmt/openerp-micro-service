package com.hust.baseweb.repo;

import com.hust.baseweb.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepo extends JpaRepository<Status, String> {
    //Status findByStatusId(String statusId);
}
