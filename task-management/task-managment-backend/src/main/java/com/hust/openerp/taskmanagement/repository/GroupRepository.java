package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.Group;
import com.hust.openerp.taskmanagement.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    @Query("SELECT g FROM GroupUser gu JOIN gu.group g WHERE gu.userId = :userId AND gu.thrsDate IS NULL")
    List<Group> findActiveByUserId(@Param("userId") String userId);
}
