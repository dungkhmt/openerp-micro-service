package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUser.GroupUserId> {
    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user WHERE gu.groupId = :groupId AND gu.thrsDate IS NULL")
    List<GroupUser> findActiveByGroupId(@Param("groupId") UUID groupId);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user WHERE gu.groupId IN :groupIds AND gu.thrsDate IS NULL")
    List<GroupUser> findActiveByGroupIds(@Param("groupIds") List<UUID> groupIds);

    boolean existsByGroupIdAndUserIdAndThrsDateIsNull(UUID groupId, String userId);

    Optional<GroupUser> findByGroupIdAndUserId(UUID groupId, String userId);
}
