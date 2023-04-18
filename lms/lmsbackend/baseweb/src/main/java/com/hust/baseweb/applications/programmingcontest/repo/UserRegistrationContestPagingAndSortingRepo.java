package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import com.hust.baseweb.applications.programmingcontest.entity.UserRegistrationContestEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelUserRegisteredClassInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.UUID;

public interface UserRegistrationContestPagingAndSortingRepo extends PagingAndSortingRepository<UserRegistrationContestEntity, UUID> {
//    @Query("select new com.hust.minileetcode.model.ModelUserRegisteredClassInfo(ul.email, ul.userLoginId, p.middleName, p.firstName, p.lastName, urce.status) from UserRegistrationContestEntity urce " +
//            "inner join ContestEntity ce on urce.status =:status and urce.contest = :contest and urce.contest = ce " +
//            "inner join UserLogin ul on urce.userLogin = ul " +
//            "inner join Person p on ul.person = p")
//    Page<ModelUserRegisteredClassInfo> getAllUserRegisteredByContestAndStatusInfo(Pageable pageable, @Param("contest") ContestEntity contest, @Param("status") String status);



    @Query("select new com.hust.baseweb.applications.programmingcontest.model.ModelUserRegisteredClassInfo(ul.userLoginId, pe.middleName, pe.firstName, pe.lastName, urce.status) from UserRegistrationContestEntity urce " +
            "inner join ContestEntity ce on urce.status =:status and urce.contestId = :contestId and urce.contestId = ce.contestId " +
            "inner join UserLogin ul on urce.userId = ul.userLoginId " +
            "inner join Party p on ul.party = p inner join Person pe on pe.partyId  = p.partyId")
    Page<ModelUserRegisteredClassInfo> getAllUserRegisteredByContestIdAndStatusInfo(Pageable pageable, @Param("contestId") String contestId, @Param("status") String status);






//    @Query("select new com.hust.minileetcode.model.ModelUserRegisteredClassInfo(ul.email, ul.userLoginId, p.middleName, p.firstName, p.lastName, urce.status) from UserLogin ul " +
//            "inner join Person p on ul.person = p and  (ul.userLoginId like %:keyword% or ul.email like %:keyword%) left outer join UserRegistrationContestEntity urce on ul = urce.userLogin and urce.contest = :contest")
//    Page<ModelUserRegisteredClassInfo> searchUser(Pageable pageable, @Param("contest") ContestEntity contest, @Param("keyword") String keyword);


    @Query("select new com.hust.baseweb.applications.programmingcontest.model.ModelUserRegisteredClassInfo( ul.userLoginId, p.middleName, p.firstName, p.lastName, urce.status) from UserLogin ul " +
            "inner join Party pa on ul.party = pa inner join Person p on p.partyId = ul.party and  (ul.userLoginId like %:keyword% ) left outer join UserRegistrationContestEntity urce on ul.userLoginId = urce.userId and urce.contestId = :contestId")
    Page<ModelUserRegisteredClassInfo> searchUser(Pageable pageable, @Param("contestId") String contestId, @Param("keyword") String keyword);


//    @Query("select ce from ContestEntity ce where ce in (select urce.contest from UserRegistrationContestEntity urce where urce.userLogin = :userLogin and urce.status = 'SUCCESSFUL')")
//    Page<ContestEntity> getContestByUserAndStatusSuccessful(Pageable pageable, @Param("userLogin") UserLogin userLogin);


    @Query("select ce from ContestEntity ce where ce.contestId in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and urce.status = 'SUCCESSFUL')")
    Page<ContestEntity> getContestByUserAndStatusSuccessful(Pageable pageable, @Param("userId") String userId);

//    @Query("select ce from ContestEntity ce where ce not in (select urce.contest from UserRegistrationContestEntity urce where urce.userLogin = :userLogin and urce.status = 'SUCCESSFUL')")
//    Page<ContestEntity> getNotRegisteredContestByUserLogin(Pageable pageable, @Param("userLogin") UserLogin userLogin);


    //@Query("select ce from ContestEntity ce where ce.contestId not in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and urce.status = 'SUCCESSFUL')")
    @Query("select ce from ContestEntity ce where ce.contestId not in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and (urce.status = 'SUCCESSFUL' or urce.status = 'PENDING') )" +
           "and (ce.statusId = 'OPEN' or ce.statusId = 'RUNNING')")
    Page<ContestEntity> getNotRegisteredContestByUserLogin(Pageable pageable, @Param("userId") String userId);

    @Query("select count(ce.contestId) from ContestEntity ce where ce.contestId not in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and (urce.status = 'SUCCESSFUL' or urce.status = 'PENDING') )" +
           "and (ce.statusId = 'OPEN' or ce.statusId = 'RUNNING')")
    long getNumberOfNotRegisteredContestByUserLogin(@Param("userId") String userId);

    @Query("select ce from ContestEntity ce where ce.contestId in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and urce.status = 'SUCCESSFUL') and ((ce.endTime >= :now and ce.startedAt < :now) or ce.isPublic = true )")
    //@Query("select ce from ContestEntity ce where ce.contestId in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and urce.status = 'SUCCESSFUL') and (ce.statusId = 'RUNNING' or ce.statusId = 'OPEN')")
    Page<ContestEntity> getContestByUserAndStatusSuccessfulInSolvingTime(Pageable pageable, @Param("userId") String userId, @Param("now") Date now);

    @Query("select ce from ContestEntity ce where ce.contestId in (select urce.contestId from UserRegistrationContestEntity urce where urce.userId = :userId and urce.status = 'SUCCESSFUL') and (ce.statusId = 'RUNNING' or ce.statusId = 'OPEN' or ce.statusId = 'COMPLETED')")
    Page<ContestEntity> getContestByUser(Pageable pageable, @Param("userId") String userId);

}
