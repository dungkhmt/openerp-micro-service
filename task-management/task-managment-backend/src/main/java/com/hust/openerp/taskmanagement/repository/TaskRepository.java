package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    @Modifying
    @Query(value = """
            UPDATE Task t SET t.rgt = t.rgt + 2 WHERE t.rgt >= :parentRgt AND t.ancestorId = :ancestorId
            """)
    void updateNestedSetsRgt(@Param("parentRgt") Integer parentRgt, @Param("ancestorId") UUID ancestorId);

    @Modifying
    @Query(value = """
            UPDATE Task t SET t.lft = t.lft + 2 WHERE t.lft > :parentRgt AND t.ancestorId = :ancestorId
            """)
    void updateNestedSetsLft(@Param("parentRgt") Integer parentRgt, @Param("ancestorId") UUID ancestorId);

    @Query("SELECT t FROM Task t WHERE t.ancestorId = :ancestorId AND t.lft >= :lft AND t.rgt <= :rgt ORDER BY t.lft")
    List<Task> getTaskByAncestorIdAndLftAndRgt(@Param("ancestorId") UUID ancestorId, @Param("lft") Integer lft,
            @Param("rgt") Integer rgt);
}
