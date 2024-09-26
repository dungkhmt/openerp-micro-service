package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EduQuizTestGroupRepo extends JpaRepository<EduTestQuizGroup, UUID> {

    public List<EduTestQuizGroup> findAllByTestIdAndGroupCode(String testId, String groupCode);

    List<EduTestQuizGroup> findByTestId(String testId);

    List<EduTestQuizGroup> findByQuizGroupIdIn(Set<UUID> quizGroupIds);

    EduTestQuizGroup findEduTestQuizGroupByTestIdAndQuizGroupId(
        String testId,
        UUID quizGroupId
    );

    public interface QuizTestGroupInfo {

        String getQuiz_group_id();

        String getGroup_code();

        String getNote();

        int getNum_question();

        int getNum_student();
    }

    @Query(
        nativeQuery = true,
        value = "select cast(ORI.quiz_group_id as varchar(60)), ORI.group_code, ORI.note, \n" +
                //"NUMQUEST.num_question, NUMSTUDENT.num_student \n" +
                "CASE \n" +
                "WHEN NUMQUEST.num_question IS NULL THEN 0 \n" +
                "ELSE NUMQUEST.num_question \n" +
                "END AS num_question, \n" +
                "CASE \n" +
                "WHEN NUMSTUDENT.num_student IS NULL THEN 0 \n" +
                "ELSE NUMSTUDENT.num_student \n" +
                "END AS num_student \n" +
                "from edu_test_quiz_group ORI \n" +
                "left join (\n" +
                "select  \n" +
                "S1.quiz_group_id, \n" +
                "S1.group_code, \n" +
                "count(S2.question_id) as num_question \n" +
                "from edu_test_quiz_group S1 \n" +
                "inner join quiz_group_question_assignment S2 \n" +
                "on S1.quiz_group_id = S2.quiz_group_id \n" +
                "--inner join edu_test_quiz_group_participation_assignment S3 \n" +
                "--on S1.quiz_group_id = S3.quiz_group_id \n" +
                "group by S1.quiz_group_id  \n" +
                ") NUMQUEST \n" +
                "on NUMQUEST.quiz_group_id = ORI.quiz_group_id \n" +
                "left join ( \n" +
                "select  \n" +
                "S1.quiz_group_id, \n" +
                "S1.group_code, \n" +
                "count(S3.participation_user_login_id) as num_student \n" +
                "from edu_test_quiz_group S1 \n" +
                "inner join edu_test_quiz_group_participation_assignment S3 \n" +
                "on S1.quiz_group_id = S3.quiz_group_id \n" +
                "group by S1.quiz_group_id  \n" +
                ") NUMSTUDENT \n" +
                "on NUMSTUDENT.quiz_group_id = ORI.quiz_group_id \n" +
                "where ORI.test_id = ?1"
    )
    List<QuizTestGroupInfo> findQuizTestGroupsInfo(String testId);

    @Transactional
    @Modifying
    @Query(
        nativeQuery = true,
        value =
            "delete from edu_test_quiz_group \n" +
            "where test_id = ?1 and quiz_group_id = ?2"
    )
    Integer deleteQuizTestGroup(String testId, UUID quizTestGroupId);

}
