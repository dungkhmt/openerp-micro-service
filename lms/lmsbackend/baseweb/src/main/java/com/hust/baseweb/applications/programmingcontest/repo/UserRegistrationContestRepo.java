package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.UserRegistrationContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRegistrationContestRepo extends JpaRepository<UserRegistrationContestEntity, UUID> {
//    UserRegistrationContestEntity findUserRegistrationContestByContestAndUserLogin(ContestEntity contest, UserLogin userLogin);

    UserRegistrationContestEntity findUserRegistrationContestEntityByContestIdAndUserId(
        String contestId,
        String userId
    );

    UserRegistrationContestEntity findUserRegistrationContestEntityByContestIdAndUserIdAndRoleId(
        String contestId,
        String userId,
        String roleId
    );

//    UserRegistrationContestEntity findUserRegistrationContestEntityByContestAndUserLoginAndStatus(ContestEntity contest, UserLogin userLogin, String status);

    List<UserRegistrationContestEntity> findUserRegistrationContestEntityByContestIdAndUserIdAndStatus(
        String contestId,
        String userId,
        String status
    );

    List<UserRegistrationContestEntity> findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(
        String contestId,
        String userId,
        String status,
        String roleId
    );
//    List<ModelUserRegisteredClassInfo> getAllUserRegisteredContestInfo()

    List<UserRegistrationContestEntity> findAllByUserIdAndRoleId(String userId, String roleId);

    List<UserRegistrationContestEntity> findAllByUserIdAndRoleIdAndStatus(String userId, String roleId, String status);

    List<UserRegistrationContestEntity> findAllByUserId(String userId);

    List<UserRegistrationContestEntity> findAllByUserIdAndRoleIdIn(String userId, List<String> roles);

    List<UserRegistrationContestEntity> findAllByRoleIdAndContestIdAndStatus(
        String roleId,
        String contestId,
        String status
    );

    List<UserRegistrationContestEntity> findAllByContestIdAndStatus(String contestId, String status);

    @Query(value = "select * from user_registration_contest_new u " +
                   "where u.contest_id = ?1 " +
                   "and u.status = ?3 " +
                   " and u.user_id in (select participant_id from contest_user_participant_group where contest_id = ?1 and user_id = ?2) " ,
           nativeQuery = true)
    List<UserRegistrationContestEntity> findAllInGroupByContestIdAndStatus(String contestId, String userId, String status);

    @Query(value = "select user_id from user_registration_contest_new " +
                   "where contest_id = ?1 " +
                   "and status = ?2",
           nativeQuery = true)
    List<String> getAllUserIdsInContest(String contestId, String status);

    void deleteAllByContestId(String contestId);
}
