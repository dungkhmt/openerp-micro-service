package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    @Query("Select u from User u inner join Task t on u.id = t.creatorId where t.assigneeId = :userId")
    List<User> getAllUserCreateTaskAssignMe(String userId);

    @Query("Select u from User u inner join Task t on u.id = t.assigneeId where t.creatorId = :userId")
    List<User> getAllUserAssignTaskAssignMe(String userId);

    @Query(
        value = "SELECT * FROM user_login WHERE email = :email",
        nativeQuery = true
    )
    Optional<User> findByEmail(String email);
}
