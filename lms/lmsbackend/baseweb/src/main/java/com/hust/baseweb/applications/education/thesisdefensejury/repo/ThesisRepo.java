package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResource;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.Thesis;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThesisRepo extends  PagingAndSortingRepository<Thesis, UUID>{
    Page<Thesis> findAll(Pageable pageable);
    Optional<Thesis> findByThesisName(String thesisName);


    @Query(value = "select * from thesis t where t.thesis_name like %:thesis_name%", nativeQuery = true)
    List<Thesis> findAllByThesisName(String thesis_name);

    @Modifying
    @Query(value = "DELETE FROM thesis t WHERE t.id = :id and t.created_by_user_login_id=:UserLoginId", nativeQuery = true)
    void deleteByIdAndUserLogin(UUID id,String UserLoginId);

    @Query(value = "select * from thesis t where t.scheduled_jury_id = :juryID", nativeQuery = true)
    List<Thesis> findAllByJuryID(UUID juryID);

    @Query(value = "select * from thesis t where t.scheduled_jury_id = :juryID and t.thesis_name like %:key%", nativeQuery = true)
    List<Thesis> findAllByJuryIDAAndThesisName(UUID juryID,String key);

    @Query(value = "select * from thesis t where t.id = :thesisId and t.scheduled_jury_id = :defenseJuryId", nativeQuery = true)
    Optional<Thesis> findByIdAndDefenseJury(UUID thesisId, UUID defenseJuryId);

    @Modifying
    @Query(value = "Update thesis set scheduled_jury_id = null where id = :thesisId ", nativeQuery = true)
    void updateDeleteThesisByDefenJuryId(UUID thesisId);

    @Modifying
    @Query(value = "Update thesis set scheduled_jury_id = :juryId where id = :thesisId ", nativeQuery = true)
    void updateThesisByDefenJuryId(UUID thesisId,UUID juryId);


    @Query(value = "select count(t.scheduled_jury_id) from  thesis t WHERE t.scheduled_jury_id = :juryId", nativeQuery = true)
    Long getCountThesisByJuryId(UUID juryId);

    @Query(value = "select * from thesis t where t.thesis_defense_plan_id = :planId", nativeQuery = true)
    List<Thesis> findAllByPlanId(String planId);

    @Query(value = "select * from thesis t where t.thesis_defense_plan_id = :planId and t.thesis_name like %:key%", nativeQuery = true)
    List<Thesis> findAllByPlanIdAndThesisName(String planId,String key);

    @Modifying
    @Query(value = "select * from thesis t where t.thesis_defense_plan_id = :planId and t.scheduled_jury_id = :juryId and t.thesis_name like %:key%", nativeQuery = true)
    List<Thesis> findByDefensePlanIdAndAndDefenseJuryAndThesisName(String planId,UUID juryId,String key);



//    @Modifying
//    @Query(value = "select * from thesis t where t.id = :id and t.scheduled_jury_id = :defenseJuryId", nativeQuery = true)
//    Optional<Thesis> findByIdAndDefenseJury(UUID Id,UUID defenseJuryId );
}
