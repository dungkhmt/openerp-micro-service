package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EduQuizTestRepo extends JpaRepository<EduQuizTest, String> {

    List<EduQuizTest> findAllBySessionId(UUID sessionId);


    @Query(
        nativeQuery = true,
        value = "select * from edu_quiz_test where created_by_user_login_id = ?1"
    )
    public List<EduQuizTest> findByCreateUser(String userLoginId);


    public static interface StudentInfo {

        String getTest_id();

        String getUser_login_id();

        String getFull_name();

        String getGender();

        String getBirth_date();

        String getEmail();

        String getStatus_id();

    }

    @Query(
        nativeQuery = true,
        value =
            "select \n" +
            "S1.test_id, \n" +
            "S1.participant_user_login_id as user_login_id, \n" +
            "person.first_name || ' ' || person.middle_name || ' ' || person.last_name as full_name, \n" +
            "person.gender, \n" +
            "person.birth_date, \n" +
            "user_register.email, \n" +
            "S1.status_id \n" +
            "from edu_test_quiz_participant S1 \n" +
            "inner join user_login \n" +
            "on S1.participant_user_login_id = user_login.user_login_id \n" +
            "inner join person \n" +
            "on person.party_id = user_login.party_id \n" +
            "left join user_register \n" +
            "on user_register.user_login_id = user_login.user_login_id \n" +
            "where S1.test_id = ?1"
    )
    public List<StudentInfo> findAllStudentInTest(String testId);

    @Transactional
    @Modifying
    @Query(
        nativeQuery = true,
        value =
            "delete from edu_test_quiz_participant " +
            "where test_id = ?1 and participant_user_login_id = ?2"
    )
    public Integer rejectStudentInTest(String testId, String userLoginId);

    @Transactional
    @Modifying
    @Query(
        nativeQuery = true,
        value =
            "update edu_test_quiz_participant \n" +
            "set status_id = 'STATUS_APPROVED' \n" +
            "where test_id = ?1 and participant_user_login_id = ?2 and status_id = 'STATUS_REGISTERED'"
    )
    public Integer acceptStudentInTest(String testId, String userLoginId);

}
