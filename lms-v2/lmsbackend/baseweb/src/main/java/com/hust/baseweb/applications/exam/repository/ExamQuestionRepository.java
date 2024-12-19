package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamQuestionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestionEntity, String> {

    @Query(value = "select\n" +
                   "   *\n" +
                   "from\n" +
                   "   exam_question eq\n" +
                   "where\n" +
                   "   eq.created_by = :userLogin\n" +
                   "   and\n" +
                   "   (:code is null or lower(eq.code) like CONCAT('%', LOWER(:code),'%'))\n" +
                   "   and (:content is null or lower(eq.content) like CONCAT('%', LOWER(:content),'%'))\n" +
                   "   and (eq.type in :types)", nativeQuery = true)
    Page<ExamQuestionEntity> filter(Pageable pageable, @Param("userLogin") String userLogin, @Param("code") String code,
                                    @Param("content") String content, @Param("types") List<Integer> types);

    Optional<ExamQuestionEntity> findByCode(String code);
}
