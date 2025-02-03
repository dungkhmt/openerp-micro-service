package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.DashboardPostgresEntity;
import com.real_estate.post.utils.TypeProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<DashboardPostgresEntity, Long> {
    @Query("select d.endTime from DashboardPostgresEntity d order by d.endTime desc limit 1")
    public Optional<Long> findLastTimeTrigger();

    @Query("select d from DashboardPostgresEntity d " +
            "where d.startTime >= :fromTime and d.endTime <= :toTime and d.typeProperty = :typeProperty and d.districtId = :districtId " +
            "order by d.startTime asc")
    List<DashboardPostgresEntity> findBy(Long fromTime, Long toTime, TypeProperty typeProperty, String districtId);
}
