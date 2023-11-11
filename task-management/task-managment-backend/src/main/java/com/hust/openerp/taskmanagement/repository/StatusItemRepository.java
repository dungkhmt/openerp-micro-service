package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.StatusItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusItemRepository extends JpaRepository<StatusItem, String> {

    StatusItem findByStatusId(String statusId);

    List<StatusItem> findAllByStatusIdStartsWith(String statusId);

    @Query(value = "SELECT e.* FROM status_item e WHERE e.status_type_id = :statusTypeId", nativeQuery = true)
    List<StatusItem> findAllByStatusTypeId(@Param("statusTypeId") String statusTypeId);
}
