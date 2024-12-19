package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamTestRepository extends JpaRepository<ExamTestEntity, String> {

    @Query(value = "select\n" +
                   "    *\n" +
                   "from\n" +
                   "    exam_test et\n" +
                   "where\n" +
                   "    et.created_by = :userLogin\n" +
                   "    and\n" +
                   "    (:keyword is null or \n" +
                   "        (lower(et.code) like CONCAT('%', LOWER(:keyword),'%')) or \n" +
                   "        (lower(et.name) like CONCAT('%', LOWER(:keyword),'%')))\n" +
                   "    and (\n" +
                   "        (:createdFrom is null and :createdTo is null) or \n" +
                   "        (:createdFrom is not null and :createdTo is null and et.created_at >= :createdFrom) or \n" +
                   "        (:createdFrom is null and :createdTo is not null and et.created_at <= :createdTo) or \n" +
                   "        (:createdFrom is not null and :createdTo is not null and et.created_at between :createdFrom and :createdTo)\n" +
                   "    )\n" +
                   "order by created_at desc", nativeQuery = true)
    Page<ExamTestEntity> filter(Pageable pageable, @Param("userLogin") String userLogin,
                                @Param("keyword") String keyword,
                                @Param("createdFrom") LocalDateTime createdFrom,
                                @Param("createdTo") LocalDateTime createdTo);

    Optional<ExamTestEntity> findByCode(String code);
}
