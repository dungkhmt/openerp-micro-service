package com.hust.openerp.taskmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    @Query("Select u from User u inner join Task t on u.id = t.creatorId where t.assigneeId = :userId")
    List<User> getAllUserCreateTaskAssignMe(String userId);
}
