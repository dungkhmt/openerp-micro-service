package com.hust.baseweb.applications.education.classmanagement.service;

import com.hust.baseweb.applications.education.classmanagement.enumeration.RegistStatus;
import com.hust.baseweb.applications.education.classmanagement.model.ModelResponseEduClassDetail;
import com.hust.baseweb.applications.education.entity.AssignmentSubmission;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduClassUserLoginRole;
import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.applications.education.model.*;
import com.hust.baseweb.applications.education.model.educlassuserloginrole.AddEduClassUserLoginRoleIM;
import com.hust.baseweb.applications.education.model.educlassuserloginrole.ClassOfUserOM;
import com.hust.baseweb.applications.education.model.getclasslist.GetClassListOM;
import com.hust.baseweb.entity.UserLogin;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ClassService {

    EduClass findById(UUID id);

    EduClass save(UserLogin userLogin, AddClassModel addClassModel);

    GetClassListOM getClassesOfCurrentSemester(String studentId, GetClassesIM filterParams, Pageable pageable);

    SimpleResponse register(UUID classId, String studentId);

    Map<String, SimpleResponse> updateRegistStatus(UUID classId, Set<String> studentIds, RegistStatus status);

    List<ModelResponseEduClassDetail> getAllClass();

    List<GetClassesOfTeacherOM> getClassesOfTeacher(String teacherId);

    List<GetClassesOfStudentOM> getClassesOfStudent(String studentId);

    GetClassDetailOM getClassDetail(UUID id);

    List<GetAssigns4TeacherOM> getAssign4Teacher(UUID classId);

    List<GetAllStuAssignDetail4Teacher> getAllStuAssign4Teacher(UUID classId);

    List<AssignmentSubmission> getAssignSubmit4Teacher(UUID classId);

    List<GetAssigns4StudentOM> getAssign4Student(UUID classId);

    List<GetStudentsOfClassOM> getStudentsOfClass(UUID id);

    List<GetStudentsOfClassOM> getRegistStudentsOfClass(UUID id);

    EduClassUserLoginRole addEduClassUserLoginRole(AddEduClassUserLoginRoleIM input);

    List<EduClassUserLoginRole> getUserLoginRolesOfClass(UUID classId);

    List<ClassOfUserOM> getClassOfUser(String userLoginId);

    EduCourse getCourseOfClassCode(String classCode);

    int addAllUser2Class(String classCode);

    void deleteEduClassUserLoginRole(AddEduClassUserLoginRoleIM deletedPermission);
}
