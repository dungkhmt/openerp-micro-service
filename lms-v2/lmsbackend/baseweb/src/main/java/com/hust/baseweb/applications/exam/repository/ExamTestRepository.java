package com.hust.baseweb.applications.exam.repository;

import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import com.hust.baseweb.applications.exam.model.response.ExamTestQuestionDetailsRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamTestRepository extends JpaRepository<ExamTestEntity, String> {

    @Query(value = "select\n" +
                   "    etq.id as examTestQuestionId,\n" +
                   "    eq.id as questionId,\n" +
                   "    eq.code as questionCode,\n" +
                   "    eq.type as questionType,\n" +
                   "    eq.content as questionContent,\n" +
                   "    eq.file_path as questionFile,\n" +
                   "    eq.number_answer as questionNumberAnswer,\n" +
                   "    eq.content_answer1 as questionContentAnswer1,\n" +
                   "    eq.content_answer2 as questionContentAnswer2,\n" +
                   "    eq.content_answer3 as questionContentAnswer3,\n" +
                   "    eq.content_answer4 as questionContentAnswer4,\n" +
                   "    eq.content_answer5 as questionContentAnswer5,\n" +
                   "    eq.multichoice as questionMultichoice,\n" +
                   "    eq.answer as questionAnswer,\n" +
                   "    eq.explain as questionExplain,\n" +
                   "    etq.order as questionOrder\n" +
                   "from\n" +
                   "    exam_test et\n" +
                   "left join exam_test_question etq on\n" +
                   "    et.id = etq.exam_test_id\n" +
                   "left join exam_question eq on\n" +
                   "    etq.exam_question_id = eq.id\n" +
                   "where\n" +
                   "    et.created_by = :userLogin\n" +
                   "    and et.id = :examTestId\n" +
                   "order by\n" +
                   "    etq.order", nativeQuery = true)
    List<ExamTestQuestionDetailsRes> details(@Param("userLogin") String userLogin,
                                             @Param("examTestId") String examTestId);

    Optional<ExamTestEntity> findByCode(String code);
}
