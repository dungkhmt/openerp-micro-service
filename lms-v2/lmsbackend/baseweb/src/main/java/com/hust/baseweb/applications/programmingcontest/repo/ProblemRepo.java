package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelProblemGeneralInfo;
import com.hust.baseweb.model.ProblemProjection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepo extends JpaRepository<ProblemEntity, String>, JpaSpecificationExecutor<ProblemEntity> {

    ProblemEntity findByProblemId(String problemId);

    @Query("SELECT p FROM ProblemEntity p LEFT JOIN FETCH p.tags WHERE p.problemId = (:problemId)")
    ProblemEntity findByProblemIdWithTagFetched(@Param("problemId") String problemId);

    @Query("select p from ProblemEntity p where p.problemId in :problemIds")
    List<ProblemEntity> getAllProblemWithArray(@Param("problemIds") List<String> problemIds);

    // @Query(
    //     nativeQuery = true,
    //     value = 
    //     "select p.problem_id as problemId, p.problem_name as problemName, p.problem_description as problemDescription from contest_problem_new p"
    // )
    @Query("select new com.hust.baseweb.applications.programmingcontest.model.ModelProblemGeneralInfo("
           + "p.problemId, p.problemName, p.levelId, p.problemDescription"
           + ") from ProblemEntity p")
    List<ModelProblemGeneralInfo> getAllProblemGeneralInformation();
    @Query("select new com.hust.baseweb.applications.programmingcontest.model.ModelProblemGeneralInfo("
           + "p.problemId, p.problemName, p.levelId, p.problemDescription"
           + ") from ProblemEntity p where p.statusId = 'OPEN'")
    List<ModelProblemGeneralInfo> getAllOpenProblemGeneralInformation();

    boolean existsByProblemId(String problemId);

    boolean existsByProblemName(String problemName);

    @Query(value = "with filteredProblemIds as (select   " +
                   "    distinct p.problem_id  " +
                   "from   " +
                   "    contest_problem_new p   " +
                   "left join problem_tag pt on   " +
                   "    p.problem_id = pt.problem_id   " +
                   "left join tag t on   " +
                   "    pt.tag_id = t.tag_id   " +
                   "where   " +
                   "    (:isPublic is null or cast(p.is_public as text) = cast(:isPublic as text)) " +
                   "    and (:userId is null or p.created_by_user_login_id = cast(:userId as text)) " +
                   "    and (:name is null or upper(p.problem_name) like upper(concat('%', :name, '%')))   " +
                   "    and (:levelIds is null OR p.level_id = ANY (string_to_array(cast(:levelIds as text), ',')))   " +
                   "    and (:statusIds is null or p.status_id = ANY (string_to_array(cast(:statusIds as text), ',')))   " +
                   "    and (:tagIds is null or cast(t.tag_id as text) = ANY (string_to_array(cast(:tagIds as text), ',')))  " +
                   ")   " +
                   "select   " +
                   "    p.problem_id as problemId,   " +
                   "    p.problem_name as problemName,   " +
                   "    p.created_by_user_login_id as userId,   " +
                   "    p.level_id as levelId,   " +
                   "    p.created_stamp as createdAt,   " +
                   "    p.appearances as appearances,   " +
                   "    p.status_id as statusId,   " +
                   "    cast(coalesce(JSON_AGG(JSON_BUILD_OBJECT('tagId', t.tag_id, 'name', t.name)) filter (where t.tag_id is not null), '[]') as text) as jsonTags   " +
                   "from   " +
                   "    contest_problem_new p  " +
                   "inner join filteredProblemIds on p.problem_id = filteredProblemIds.problem_id  " +
                   "left join problem_tag pt on   " +
                   "    p.problem_id = pt.problem_id   " +
                   "left join tag t on   " +
                   "    pt.tag_id = t.tag_id  " +
                   "group by   " +
                   "    p.problem_id   " +
                   "order by p.created_stamp desc",
           countQuery = "with filteredProblemIds as (select  " +
                        "    distinct p.problem_id " +
                        "from  " +
                        "    contest_problem_new p  " +
                        "left join problem_tag pt on  " +
                        "    p.problem_id = pt.problem_id  " +
                        "left join tag t on  " +
                        "    pt.tag_id = t.tag_id  " +
                        "where  " +
                        "    (:isPublic is null or cast(p.is_public as text) = cast(:isPublic as text)) " +
                        "    and (:userId is null or p.created_by_user_login_id = cast(:userId as text)) " +
                        "    and (:name is null or upper(p.problem_name) like upper(concat('%', :name, '%')))   " +
                        "    and (:levelIds is null OR p.level_id = ANY (string_to_array(cast(:levelIds as text), ',')))   " +
                        "    and (:statusIds is null or p.status_id = ANY (string_to_array(cast(:statusIds as text), ',')))   " +
                        "    and (:tagIds is null or cast(t.tag_id as text) = ANY (string_to_array(cast(:tagIds as text), ',')))  " +
                        ")  " +
                        "select  " +
                        "    count(problem_id)  " +
                        "from  " +
                        "    filteredProblemIds",
           nativeQuery = true)
    Page<ProblemProjection> findAllBy(
        @Param("userId") String userId,
        @Param("name") String name,
        @Param("levelIds") String levelIds,
        @Param("tagIds") String tagIds,
        @Param("statusIds") String statusIds,
        @Param("isPublic") Boolean isPublic,
        Pageable pageable
    );

    @Query(value = "with createdByOthers as ( " +
                   "select " +
                   "    distinct p.problem_id " +
                   "from    " +
                   "    contest_problem_new p    " +
                   "left join problem_tag pt on    " +
                   "    p.problem_id = pt.problem_id    " +
                   "left join tag t on    " +
                   "    pt.tag_id = t.tag_id    " +
                   "where    " +
                   "    p.created_by_user_login_id <> :userId " +
                   "    and (:name is null or upper(p.problem_name) like upper(concat('%', :name, '%')))    " +
                   "    and (:levelIds is null OR p.level_id = ANY (string_to_array(cast(:levelIds as text), ',')))    " +
                   "    and (:statusIds is null or p.status_id = ANY (string_to_array(cast(:statusIds as text), ',')))    " +
                   "    and (:tagIds is null or cast(t.tag_id as text) = ANY (string_to_array(cast(:tagIds as text), ',')))   " +
                   "), " +
                   "filteredProblemIds as ( " +
                   "select " +
                   "    distinct createdByOthers.problem_id " +
                   "from " +
                   "    createdByOthers " +
                   "inner join user_contest_problem_role ucpr on " +
                   "    createdByOthers.problem_id = ucpr.problem_id " +
                   "    and ucpr.user_id = :userId " +
                   ")   " +
                   "select    " +
                   "    p.problem_id as problemId,    " +
                   "    p.problem_name as problemName,    " +
                   "    p.created_by_user_login_id as userId,    " +
                   "    p.level_id as levelId,    " +
                   "    p.created_stamp as createdAt,    " +
                   "    p.appearances as appearances,    " +
                   "    p.status_id as statusId,    " +
                   "    cast(coalesce(JSON_AGG(JSON_BUILD_OBJECT('tagId', t.tag_id, 'name', t.name)) filter (where t.tag_id is not null), '[]') as text) as jsonTags    " +
                   "from    " +
                   "    contest_problem_new p   " +
                   "inner join filteredProblemIds on p.problem_id = filteredProblemIds.problem_id   " +
                   "left join problem_tag pt on    " +
                   "    p.problem_id = pt.problem_id    " +
                   "left join tag t on    " +
                   "    pt.tag_id = t.tag_id   " +
                   "group by    " +
                   "    p.problem_id    " +
                   "order by p.created_stamp desc",
           countQuery = "with createdByOthers as ( " +
                        "select " +
                        "    distinct p.problem_id " +
                        "from    " +
                        "    contest_problem_new p    " +
                        "left join problem_tag pt on    " +
                        "    p.problem_id = pt.problem_id    " +
                        "left join tag t on    " +
                        "    pt.tag_id = t.tag_id    " +
                        "where    " +
                        "    p.created_by_user_login_id <> :userId " +
                        "    and (:name is null or upper(p.problem_name) like upper(concat('%', :name, '%')))    " +
                        "    and (:levelIds is null OR p.level_id = ANY (string_to_array(cast(:levelIds as text), ',')))    " +
                        "    and (:statusIds is null or p.status_id = ANY (string_to_array(cast(:statusIds as text), ',')))    " +
                        "    and (:tagIds is null or cast(t.tag_id as text) = ANY (string_to_array(cast(:tagIds as text), ',')))   " +
                        ") " +
                        "select " +
                        "    count(distinct createdByOthers.problem_id) " +
                        "from " +
                        "    createdByOthers " +
                        "inner join user_contest_problem_role ucpr on " +
                        "    createdByOthers.problem_id = ucpr.problem_id " +
                        "    and ucpr.user_id = :userId ",
           nativeQuery = true)
    Page<ProblemProjection> findAllSharedProblemsBy(
        @Param("userId") String userId,
        @Param("name") String name,
        @Param("levelIds") String levelIds,
        @Param("tagIds") String tagIds,
        @Param("statusIds") String statusIds,
        Pageable pageable
    );
}
