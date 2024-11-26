package com.hust.baseweb.repo;

import com.hust.baseweb.entity.StatusItem;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusItemRepo extends JpaRepository<StatusItem, String> {

    StatusItem findByStatusId(String statusId);

    List<StatusItem> findAllByStatusIdStartsWith(String statusId);

    @Query(value = "SELECT e.* FROM status_item e WHERE e.status_type_id = :statusTypeId", nativeQuery = true)
    List<StatusItem> findAllByStatusTypeId(@Param("statusTypeId") String statusTypeId);
}
