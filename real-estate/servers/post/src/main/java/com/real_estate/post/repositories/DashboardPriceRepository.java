package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.DashboardPricePostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardPriceRepository extends JpaRepository<DashboardPricePostgresEntity, Long> {
    @Query("select d.endTime from DashboardPricePostgresEntity d order by d.endTime desc limit 1")
    public Optional<Long> findLastTimeTrigger();

    @Query("select d from DashboardPricePostgresEntity d " +
            "where d.startTime >= :fromTime and d.endTime <= :toTime and d.typeProperty = :typeProperty and d.districtId = :districtId " +
            "order by d.startTime asc")
    List<DashboardPricePostgresEntity> findBy(Long fromTime, Long toTime, String typeProperty, String districtId);
}
