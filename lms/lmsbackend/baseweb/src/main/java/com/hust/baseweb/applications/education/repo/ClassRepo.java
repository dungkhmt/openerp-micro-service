package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.AssignmentSubmission;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.model.*;
import com.hust.baseweb.applications.education.model.getclasslist.ClassOM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ClassRepo extends JpaRepository<EduClass, UUID> {

    List<EduClass> findAll();

    List<EduClass> findByClassCode(String classCode);

    /*Class save(Class aClass);

    Class findByClassId(String classId);

    List<Class> findBySemesterId(String semesterId);

    @Query(value = "from edu_class ec where ec.semester_id=?1 \n" +
                   "and not exists (select class_id \n" +
                   "\t\t\t\tfrom edu_class_teacher_asignment ecta \n" +
                   "\t\t\t\twhere ec.class_id = ecta.class_id) \n" +
                   "\tor exists (select * \n" +
                   "\t\t\t\tfrom edu_class_teacher_asignment ecta \n" +
                   "\t\t\t\twhere ec.class_id = ecta.class_id and ecta.teacher_id = 'NULL')",
           nativeQuery = true)
    List<Class> findNotAssignedBySemesterId(String semesterId);*/

    // Class Management
    @Query(value = "select\n" +
                   "\tcast(cl.id as varchar) id,\n" +
                   "\tcode,\n" +
                   "\tclass_code classCode,\n" +
                   "\tco.id courseId,\n" +
                   "\tco.course_name courseName,\n" +
                   "\tcl.class_type classType,\n" +
                   "\td.id departmentId\n" +
                   "from\n" +
                   "\tedu_class as cl\n" +
                   "inner join edu_course as co on\n" +
                   "\tcl.course_id = co.id\n" +
                   "inner join edu_department as d on\n" +
                   "\tcl.department_id = d.id\n" +
                   "where\n" +
                   "\tcl.semester_id = ?1\n" +
                   "\tand cl.status_id = ?2\n" +
                   "order by\n" +
                   "\tco.id",
           countQuery = "select\n" +
                        "\tcount(cl.id)\n" +
                        "from\n" +
                        "\tedu_class as cl\n" +
                        "inner join edu_course as co on\n" +
                        "\tcl.course_id = co.id\n" +
                        "inner join edu_department as d on\n" +
                        "\tcl.department_id = d.id\n" +
                        "where\n" +
                        "\tcl.semester_id = ?1 and cl.status_id = ?2",
           nativeQuery = true)
    Page<ClassOM> findBySemester(short semesterId, String status, Pageable pageable);

    @Query(value = "select\n" +
                   "\tcast(cl.id as varchar) id,\n" +
                   "\tcode,\n" +
                   "\tclass_code classCode,\n" +
                   "\tco.id courseId,\n" +
                   "\tco.course_name courseName,\n" +
                   "\tcl.class_type classType,\n" +
                   "\td.id departmentId\n" +
                   "from\n" +
                   "\tedu_class as cl\n" +
                   "inner join edu_course as co on\n" +
                   "\tcl.course_id = co.id\n" +
                   "inner join edu_department as d on\n" +
                   "\tcl.department_id = d.id\n" +
                   "where\n" +
                   "\t cl.status_id = ?1\n" +
                   "order by\n" +
                   "\tco.id",
           countQuery = "select\n" +
                        "\tcount(cl.id)\n" +
                        "from\n" +
                        "\tedu_class as cl\n" +
                        "inner join edu_course as co on\n" +
                        "\tcl.course_id = co.id\n" +
                        "inner join edu_department as d on\n" +
                        "\tcl.department_id = d.id\n" +
                        "where\n" +
                        "\t cl.status_id = ?1",
           nativeQuery = true)
    Page<ClassOM> findByStatus(String status, Pageable pageable);

    @Query(value = "select\n" +
                   "\tcast(cl.id as varchar) id,\n" +
                   "\tcode,\n" +
                   "\tclass_code classCode,\n" +
                   "\tco.id courseId,\n" +
                   "\tco.course_name courseName,\n" +
                   "\tcl.class_type classType,\n" +
                   "\td.id departmentId\n" +
                   "from\n" +
                   "\tedu_class as cl\n" +
                   "inner join edu_course as co on\n" +
                   "\tcl.course_id = co.id\n" +
                   "inner join edu_department as d on\n" +
                   "\tcl.department_id = d.id\n" +
                   "where\n" +
                   "\tcl.semester_id = ?1\n" +
                   "\tand cast(code as varchar) like concat('%', lower(unaccent(?2)), '%')\n" +
                   "\tand cast(class_code as varchar) like concat('%', lower(unaccent(?3)), '%')\n" +
                   "\tand lower(unaccent(co.id)) like concat('%', lower(unaccent(?4)), '%')\n" +
                   "\tand lower(unaccent(co.course_name)) like concat('%', lower(unaccent(?5)), '%')\n" +
                   "\tand lower(unaccent(cl.class_type)) like concat('%', lower(unaccent(?6)), '%')\n" +
                   "\tand lower(unaccent(d.id)) like concat('%', lower(unaccent(?7)), '%')",
           countQuery = "select\n" +
                        "\tcount(cl.id)\n" +
                        "from\n" +
                        "\tedu_class as cl\n" +
                        "inner join edu_course as co on\n" +
                        "\tcl.course_id = co.id\n" +
                        "inner join edu_department as d on\n" +
                        "\tcl.department_id = d.id\n" +
                        "where\n" +
                        "\tcl.semester_id = ?1\n" +
                        "\tand cast(code as varchar) like concat('%', lower(unaccent(?2)), '%')\n" +
                        "\tand cast(class_code as varchar) like concat('%', lower(unaccent(?3)), '%')\n" +
                        "\tand lower(unaccent(co.id)) like concat('%', lower(unaccent(?4)), '%')\n" +
                        "\tand lower(unaccent(co.course_name)) like concat('%', lower(unaccent(?5)), '%')\n" +
                        "\tand lower(unaccent(cl.class_type)) like concat('%', lower(unaccent(?6)), '%')\n" +
                        "\tand lower(unaccent(d.id)) like concat('%', lower(unaccent(?7)), '%')",
           nativeQuery = true)
    Page<ClassOM> findBySemesterWithFilters(
        short semesterId,
        String code,
        String classCode,
        String courseId,
        String name,
        String type,
        String dept,
        Pageable pageable
    );


    @Query(value = "select count(student_id)\n" +
                   "from edu_class_registration ecr \n" +
                   "where class_id = ?1 and status = 'APPROVED'",
           nativeQuery = true)
    int getNoStudentsOf(UUID classId);

    @Query(value = "select cast(ecl.id as varchar) id,\n" +
                   "\tecl.code code,\n" +
                   "\tecl.class_code classCode,\n" +
                   "\tecl.status_id statusId,\n" +
                   "\tec.id courseId,\n" +
                   "\tec.course_name \"name\",\n" +
                   "\tecl.class_type classType,\n" +
                   "\tecl.department_id department,\n" +
                   "\tecl.semester_id semester\n" +
                   "from edu_class ecl \n" +
                   "\tinner join edu_course ec on ecl.course_id = ec.id \n" +
                   "where ecl.teacher_id = ?1 \n" +
                   "order by ecl.semester_id ",
           nativeQuery = true)
    List<GetClassesOfTeacherOM> getClassesOfTeacher(String teacherId);

    /*@Query(value = "select\n" +
                   "\tcast(class_id as varchar)\n" +
                   "from\n" +
                   "\tedu_class_registration ecr\n" +
                   "where\n" +
                   "\tstudent_id = ? 1\n" +
                   "\tand status in ('APPROVED', 'WAITING_FOR_APPROVAL')",
           nativeQuery = true)
    List<String> getClassIdsOfStudent(String studentId);*/

    @Query(value = "select\n" +
                   "\tcast(ecl.id as varchar) id,\n" +
                   "\tecl.code code,\n" +
                   "\tecl.class_code classCode,\n" +
                   "\tec.id courseId,\n" +
                   "\tec.course_name \"name\",\n" +
                   "\tecl.class_type classType,\n" +
                   "\tecl.semester_id semester,\n" +
                   "\tecr.status status\n" +
                   "from\n" +
                   "\tedu_class_registration ecr\n" +
                   "inner join edu_class ecl on\n" +
                   "\tecr.class_id = ecl.id\n" +
                   "inner join edu_course ec on\n" +
                   "\tecl.course_id = ec.id\n" +
                   "where\n" +
                   "\tecr.student_id = ?1 and ecr.status in ?2\n" +
                   "order by\n" +
                   "\tecl.semester_id",
           nativeQuery = true)
    List<GetClassesOfStudentOM> getClassesDetailOf(String studentId, List<String> status);

    @Query(value = "select\n" +
                   "\tcast(ecl.id as varchar) id,\n" +
                   "\tecl.code code,\n" +
                   "\tec.id courseId,\n" +
                   "\tec.course_name \"name\",\n" +
                   "\tecl.class_type classType,\n" +
                   "\tecl.semester_id semester,\n" +
                   "\tconcat(p.first_name , ' ', p.middle_name , ' ', p.last_name ) teacherName,\n" +
                   "\tur.email email\n" +
                   "from\n" +
                   "\tedu_class ecl\n" +
                   "inner join edu_course ec on\n" +
                   "\tecl.course_id = ec.id\n" +
                   "inner join user_login ul on\n" +
                   "\tecl.teacher_id = ul.user_login_id\n" +
                   "inner join person p on\n" +
                   "\tul.party_id = p.party_id\n" +
                   "left outer join user_register ur on\n" +
                   "\tul.user_login_id = ur.user_login_id\n" +
                   "where\n" +
                   "\tecl.id = ?1",
           nativeQuery = true)
    GetClassDetailOM getDetailOf(UUID classId);

    @Query(value = "select\n" +
                   "\tcast(id as varchar) id,\n" +
                   "\tassignment_name \"name\",\n" +
                   "\topen_time openTime,\n" +
                   "\tclose_time closeTime,\n" +
                   "\tdeleted\n" +
                   "from\n" +
                   "\tedu_assignment ea\n" +
                   "where\n" +
                   "\tclass_id = ?1\n" +
                   "order by\n" +
                   "\topen_time desc",
           nativeQuery = true)
    List<GetAssigns4TeacherOM> getAssignments4Teacher(UUID classId);

    @Query(value = "select\n" +
                   "\tul.user_login_id id,\n" +
                   "\tconcat(p.first_name , ' ', p.middle_name , ' ', p.last_name ) \"name\",\n" +
                   "\tcast(ea.id as varchar) assignmentId,\n" +
                   "\tea.assignment_name assignmentName,\n" +
                   "\tcast(eas.id as varchar) assignmentSubmissionId\n" +
                   "from\n" +
                   "\tedu_class_registration ecr\n" +
                   "inner join user_login ul on\n" +
                   "\tecr.student_id = ul.user_login_id\n" +
                   "inner join person p on\n" +
                   "\tul.party_id = p.party_id\n" +
                   "left outer join user_register ur on\n" +
                   "\tul.user_login_id = ur.user_login_id\n" +
                   "inner join edu_assignment ea on\n" +
                   "\tecr.class_id = ea.class_id\n" +
                   "left outer join edu_assignment_submission eas on\n" +
                   "\tea.id = eas.assignment_id\n" +
                   "\tand ecr.student_id = eas.student_id\n" +
                   "where\n" +
                   "\tecr.class_id = ?1\n" +
                   "\tand ecr.status = \'APPROVED\'\n" +
                   "order by\n" +
                   "\tp.last_name asc, ea.assignment_name asc",
           nativeQuery = true)
    List<GetAllStuAssigns4TeacherOM> getAllStudentAssignments4Teacher(UUID classId);

    @Query(value = "select\n" +
                   "\tcast(id as varchar) id,\n" +
                   "\tassignment_name \"name\",\n" +
                   "\topen_time openTime,\n" +
                   "\tclose_time closeTime,\n" +
                   "\tdeleted\n" +
                   "from\n" +
                   "\tedu_assignment ea\n" +
                   "where\n" +
                   "\tclass_id = ?1\n" +
                   "order by\n" +
                   "\topen_time desc",
           nativeQuery = true)
    List<AssignmentSubmission> getAssignmentsSubmission4Teacher(UUID classId);


    @Query(value = "select\n" +
                   "\tcast(id as varchar) id,\n" +
                   "\tassignment_name \"name\",\n" +
                   "\tclose_time closeTime\n" +
                   "from\n" +
                   "\tedu_assignment\n" +
                   "where\n" +
                   "\tclass_id = ?1\n" +
                   "\tand deleted = false\n" +
                   "\tand open_time <= now()\n" +
                   "order by\n" +
                   "\topen_time",
           nativeQuery = true)
    List<GetAssigns4StudentOM> getAssignments4Student(UUID classId);

    @Query(value = "select\n" +
                   "\tul.user_login_id id,\n" +
                   "\tconcat(p.first_name , ' ', p.middle_name , ' ', p.last_name ) \"name\",\n" +
                   "\tur.email email\n" +
                   "from\n" +
                   "\tedu_class_registration ecr\n" +
                   "inner join user_login ul on\n" +
                   "\tecr.student_id = ul.user_login_id\n" +
                   "inner join person p on\n" +
                   "\tul.party_id = p.party_id\n" +
                   "left outer join user_register ur on\n" +
                   "\tul.user_login_id = ur.user_login_id\n" +
                   "where\n" +
                   "\tecr.class_id = ?1\n" +
                   "\tand ecr.status = ?2\n" +
                   "order by\n" +
                   "\tp.last_name",
           nativeQuery = true)
    List<GetStudentsOfClassOM> getStudentsOfClass(UUID id, String status);

    @Query(value = "select\n" +
                   "\tcast(class_id as varchar)\n" +
                   "from\n" +
                   "\tedu_class_registration ecr\n" +
                   "where\n" +
                   "\tstudent_id = ?1\n" +
                   "\tand status in ('WAITING_FOR_APPROVAL', 'APPROVED')\n" +
                   "\tand class_id in ?2", nativeQuery = true)
    Set<String> getRegisteredClassesIn(String studentId, List<UUID> classIds);

    @Query(value = "select\n" +
                   "\tcount(1)\n" +
                   "from\n" +
                   "\tedu_class ec\n" +
                   "where\n" +
                   "\tid = ?1",
           nativeQuery = true)
    int isClassExist(UUID id);

    Optional<EduClass> findFirstByCode(Integer code);
}
