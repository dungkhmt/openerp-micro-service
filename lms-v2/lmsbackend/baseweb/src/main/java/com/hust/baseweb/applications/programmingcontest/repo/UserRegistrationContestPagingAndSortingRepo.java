package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import com.hust.baseweb.applications.programmingcontest.entity.UserRegistrationContestEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelUserRegisteredClassInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface UserRegistrationContestPagingAndSortingRepo
    extends PagingAndSortingRepository<UserRegistrationContestEntity, UUID> {

    @Query(
        "select new com.hust.baseweb.applications.programmingcontest.model.ModelUserRegisteredClassInfo(ul.userLoginId, '', ul.firstName, ul.lastName, urce.status) from UserRegistrationContestEntity urce " +
        "inner join ContestEntity ce on urce.status =:status and urce.contestId = :contestId and urce.contestId = ce.contestId " +
        "inner join UserLogin ul on urce.userId = ul.userLoginId ")
    Page<ModelUserRegisteredClassInfo> getAllUserRegisteredByContestIdAndStatusInfo(
        Pageable pageable,
        @Param("contestId")
        String contestId,
        @Param("status") String status
    );

    @Query(
        "select ce from ContestEntity ce where ce.contestId not in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and (urce.status = 'SUCCESSFUL' or urce.status = 'PENDING') )" +
        "and (ce.statusId = 'OPEN' or ce.statusId = 'RUNNING')")
    Page<ContestEntity> getNotRegisteredContestByUserLogin(Pageable pageable, @Param("userId") String userId);

    @Query(
        "select count(ce.contestId) from ContestEntity ce where ce.contestId not in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and (urce.status = 'SUCCESSFUL' or urce.status = 'PENDING') )" +
        "and (ce.statusId = 'OPEN' or ce.statusId = 'RUNNING')")
    long getNumberOfNotRegisteredContestByUserLogin(@Param("userId") String userId);

    @Query(
        "select ce from ContestEntity ce where ce.contestId in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and urce.status = 'SUCCESSFUL') and (ce.statusId = 'RUNNING' or ce.statusId = 'OPEN' or ce.statusId = 'COMPLETED')")
    Page<ContestEntity> getContestByUser(Pageable pageable, @Param("userId") String userId);

}
