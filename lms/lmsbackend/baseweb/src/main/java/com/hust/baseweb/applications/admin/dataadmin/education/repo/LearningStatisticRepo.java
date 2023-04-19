package com.hust.baseweb.applications.admin.dataadmin.education.repo;

import com.hust.baseweb.applications.admin.dataadmin.education.entity.LearningStatisticEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface LearningStatisticRepo extends JpaRepository<LearningStatisticEntity, String> {

    @Query("SELECT MAX (ls.lastModifiedAt) FROM LearningStatisticEntity ls")
    Optional<Date> findLatestStatisticTime();

    Page<LearningStatisticEntity> findByLoginIdContainsIgnoreCase(String partOfLoginId, Pageable pageable);

}
