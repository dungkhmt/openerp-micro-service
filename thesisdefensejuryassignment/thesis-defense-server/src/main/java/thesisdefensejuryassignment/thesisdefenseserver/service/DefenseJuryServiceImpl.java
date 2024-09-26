package thesisdefensejuryassignment.thesisdefenseserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesisdefensejuryassignment.thesisdefenseserver.dto.UpdateDefenseJuryDTO;
import thesisdefensejuryassignment.thesisdefenseserver.entity.*;
import thesisdefensejuryassignment.thesisdefenseserver.entity.embedded.DefenseJuryTeacherRole;
import thesisdefensejuryassignment.thesisdefenseserver.models.*;
import thesisdefensejuryassignment.thesisdefenseserver.or_tools.AssignTeacherAndThesisToDefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.repo.*;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@AllArgsConstructor
@Service


public class DefenseJuryServiceImpl implements DefenseJuryService {

    @Autowired
    private DefenseJuryRepo defenseJuryRepo;
    @Autowired
    private ThesisDefensePlanRepo thesisDefensePlanRepo;
    @Autowired
    private AcademicKeywordRepo academicKeywordRepo;
    @Autowired
    private DefenseRoomRepo defenseRoomRepo;
    @Autowired
    private DefenseSessionRepo defenseSessionRepo;

    @Autowired
    private TeacherRepo teacherRepo;
    @Autowired
    private ThesisRepo thesisRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private DefenseJuryTeacherRoleRepo defenseJuryTeacherRoleRepo;
    @Override
    public DefenseJury createNewDefenseJury(DefenseJuryIM defenseJury) {
        DefenseJury newDefenseJury = new DefenseJury();

        if ((defenseJury.getName().isEmpty()) || (defenseJury.getDefenseDate() == null)
                || (defenseJury.getThesisPlanName() == null)) {
            return null;
        }

        String thesisDefensePlanId = defenseJury.getThesisPlanName();
        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(thesisDefensePlanId).orElse(null);

        List<AcademicKeyword> academicKeywordList = new LinkedList<>();
        for (int i=0;i<defenseJury.getAcademicKeywordList().size();i++){
            AcademicKeyword foundAcademicKeyword = academicKeywordRepo.findById(defenseJury.getAcademicKeywordList().get(i)).orElse(null);
            if (foundAcademicKeyword == null){
                return null;
            }
            academicKeywordList.add(foundAcademicKeyword);
        }
        int defenseRoomId = defenseJury.getDefenseRoomId();
        DefenseRoom defenseRoom = defenseRoomRepo.findById(defenseRoomId).orElse(null);
        if (defenseRoom == null){
            return  null;
        }

        int defenseSessionId = defenseJury.getDefenseSessionId();
        DefenseSession defenseSession = defenseSessionRepo.findById(defenseSessionId).orElse(null);

        if (defenseSession == null){
            return null;
        }
        newDefenseJury.setDefenseRoom(defenseRoom);
        newDefenseJury.setDefenseSession(defenseSession);
        newDefenseJury.setThesisDefensePlan(thesisDefensePlan);
        newDefenseJury.setAcademicKeywordList(academicKeywordList);
        newDefenseJury.setName(defenseJury.getName());
        newDefenseJury.setDefenseDate(defenseJury.getDefenseDate());
        newDefenseJury.setCreatedTime(new Date());
        newDefenseJury.setMaxThesis(defenseJury.getMaxThesis());

        return defenseJuryRepo.save(newDefenseJury);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepo.findAll();
    }

    @Override
    public DefenseJury getDefenseJuryByID(UUID id) {
        return defenseJuryRepo.findById(id).orElse(null);
    }

    @Override
    public List<Thesis> getAllAvailableThesiss(String thesisDefensePlanId) {
        return thesisRepo.findAllAvailableThesis(thesisDefensePlanId);
    }

    @Override
    public DefenseJury assignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList) {
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(teacherAndThesisList.getDefenseJuryId())).orElse(null);
        if (defenseJury == null) return null;
        List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRole = teacherAndThesisList.getDefenseJuryTeacherRole();
        for (DefenseJuryTeacherRoleIM defenseJuryTeacherRoleIM : defenseJuryTeacherRole) {
            String teacherName = defenseJuryTeacherRoleIM.getTeacherName();
            int roleId = defenseJuryTeacherRoleIM.getRoleId();
            Teacher teacher = teacherRepo.findById(teacherName).orElse(null);
            Role teacherRole = roleRepo.findById(roleId).orElse(null);
            if (teacherRole == null) return null;
            if (teacher == null) return null;
            DefenseJuryTeacherRole defenseJuryTeacherRole1 = new DefenseJuryTeacherRole(teacher,teacherRole, defenseJury);
            DefenseJuryTeacherRole newDefenseJuryTeacherRole= defenseJuryTeacherRoleRepo.save(defenseJuryTeacherRole1);
            defenseJury.getDefenseJuryTeacherRoles().add(newDefenseJuryTeacherRole);
        }
        List<String> thesisList = teacherAndThesisList.getThesisIdList();
        for (String thesisId : thesisList){
            System.out.println(UUID.fromString(thesisId));
            Thesis thesis = thesisRepo.findById(UUID.fromString(thesisId)).orElse(null);
            if (thesis == null)return null;
            thesis.setDefenseJury(defenseJury);
            defenseJury.getThesisList().add(thesisRepo.save(thesis));
        }
        return defenseJuryRepo.save(defenseJury);
    }

    @Override
    public DefenseJury assignReviewerToThesis(AssignReviewerToThesisIM teacherAndThesisList) {
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(teacherAndThesisList.getDefenseJuryId())).orElse(null);
        if (defenseJury == null) return null;
        for (ReviewerThesisIM reviewerThesis : teacherAndThesisList.getReviewerThesisList()){
            Thesis thesis = thesisRepo.findById(UUID.fromString(reviewerThesis.getThesisId())).orElse(null);
            if (thesis == null) return null;
            Teacher reviewer = teacherRepo.findById(reviewerThesis.getScheduledReviewerId()).orElse(null);
            if (reviewer == null) return null;
            thesis.setScheduledReviewer(reviewer);
            defenseJury.getThesisList().add(thesisRepo.save(thesis));
        }
        defenseJury.setAssigned(true);
        return defenseJuryRepo.save(defenseJury);
    }

    @Override
    public String assignTeacherAndThesisAutomatically(AssignTeacherToDefenseJuryAutomaticallyIM teacherIdList) {
        String id = teacherIdList.getThesisDefensePlanId();
        System.out.println(id);
        List<Thesis> thesisList = getAllAvailableThesiss(id);
        List<Teacher> teacherList = new ArrayList<>();
        for (DefenseJuryTeacherRoleIM teacher: teacherIdList.getDefenseJuryTeacherRole()){
            teacherList.add(teacherRepo.findById(teacher.getTeacherName()).orElse(null));
        }

        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(id).orElse(null);
        long dateStart = thesisDefensePlan.getStartDate().getTime();
        long dateEnd = thesisDefensePlan.getEndDate().getTime();
        long timeDiff = Math.abs(dateEnd - dateStart);
        int sessionNumber = (int) TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS) * 2;
        AssignTeacherAndThesisToDefenseJury assignTeacherAndThesisToDefenseJury = new AssignTeacherAndThesisToDefenseJury(
                thesisList,
                teacherList,
                14,
                5
        );
        assignTeacherAndThesisToDefenseJury.assignThesisAndTeacherToDefenseJury();
        return "Check the console";
    }

    @Override
    public DefenseJury updateDefenseJury(UpdateDefenseJuryIM updateDefenseJuryIM) {
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(updateDefenseJuryIM.getId())).orElse(null);
        if (defenseJury == null) return null;
        DefenseRoom defenseRoom = defenseRoomRepo.findById(updateDefenseJuryIM.getDefenseRoomId()).orElse(null);
        if (defenseRoom == null) return null;
        DefenseSession defenseSession = defenseSessionRepo.findById(updateDefenseJuryIM.getDefenseSessionId()).orElse(null);

        if (defenseSession == null) return null;
        List<AcademicKeyword> academicKeywordList = new ArrayList<>();
        for (String keyword : updateDefenseJuryIM.getAcademicKeywordList()) {
            AcademicKeyword academicKeyword = academicKeywordRepo.findById(keyword).orElse(null);
            if (academicKeyword == null) return null;
            academicKeywordList.add(academicKeyword);
        }
        defenseJury.setName(updateDefenseJuryIM.getName());
        defenseJury.setMaxThesis(updateDefenseJuryIM.getMaxThesis());
        defenseJury.setDefenseSession(defenseSession);
        defenseJury.setDefenseRoom(defenseRoom);
        defenseJury.setAcademicKeywordList(academicKeywordList);
        defenseJury.setDefenseDate(updateDefenseJuryIM.getDefenseDate());
        DefenseJury updatedDefenseJury = defenseJuryRepo.save(defenseJury);
        return updatedDefenseJury;
    }

    @Override
    public DefenseJury reassignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList) {
        String defenseJuryId = teacherAndThesisList.getDefenseJuryId();
        List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRoleIm = teacherAndThesisList.getDefenseJuryTeacherRole();
        List<String> thesisIdList = teacherAndThesisList.getThesisIdList();
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(defenseJuryId)).orElse(null);
        if (defenseJury == null) return null;
        List<DefenseJuryTeacherRole> defenseJuryTeacherRoles = defenseJury.getDefenseJuryTeacherRoles();
        int i=0;
        while (i < defenseJuryTeacherRoles.size()) { // them tat ca giao vien moi
            DefenseJuryTeacherRole defenseJuryTeacherRole = defenseJuryTeacherRoles.get(i);
            String teacher = defenseJuryTeacherRole.getTeacher().getTeacherName();
            int role = defenseJuryTeacherRole.getRole().getId();
            List<DefenseJuryTeacherRoleIM> foundDefenseJuryTeacherRoleList = defenseJuryTeacherRoleIm.stream().filter(
                    teacherRole -> teacherRole.getTeacherName().equals(teacher)
            ).toList();
            if (foundDefenseJuryTeacherRoleList.isEmpty()) { // K co gvien do trg ds moi
                defenseJuryTeacherRoleRepo.delete(defenseJuryTeacherRole);
                defenseJuryTeacherRoles.remove(defenseJuryTeacherRole);
            }
            else {
                DefenseJuryTeacherRoleIM foundTeacher = foundDefenseJuryTeacherRoleList.get(0);
                if (foundTeacher.getRoleId() == role){
                    i++;
                }
                else {
                    Role teacherRole = roleRepo.findById(foundTeacher.getRoleId()).orElse(null);
                    defenseJuryTeacherRole.setRole(teacherRole);
                    defenseJuryTeacherRoles.set(i, defenseJuryTeacherRoleRepo.save(defenseJuryTeacherRole));
                    i++;
                }
            }
        }


        for (DefenseJuryTeacherRoleIM defenseJuryTeacherRoleIM : defenseJuryTeacherRoleIm) { // them tat ca giao vien moi
            String teacherName = defenseJuryTeacherRoleIM.getTeacherName();
            int roleId = defenseJuryTeacherRoleIM.getRoleId();
            List<DefenseJuryTeacherRole> foundDefenseJuryTeacherRole = defenseJuryTeacherRoles.stream().filter(
                    teacherRole -> teacherRole.getTeacher().getTeacherName().equals(teacherName)
            ).toList();
            if (foundDefenseJuryTeacherRole.isEmpty()) { // chua co giao vien moi do
                Teacher teacher = teacherRepo.findById(teacherName).orElse(null);
                Role teacherRole = roleRepo.findById(roleId).orElse(null);
                if (teacherRole == null) return null;
                if (teacher == null) return null;
                DefenseJuryTeacherRole defenseJuryTeacherRole = new DefenseJuryTeacherRole(teacher, teacherRole, defenseJury);
                defenseJuryTeacherRoles.add(defenseJuryTeacherRoleRepo.save(defenseJuryTeacherRole));
            }
        }

        defenseJury.setDefenseJuryTeacherRoles(defenseJuryTeacherRoles);
        List<Thesis> thesisList = defenseJury.getThesisList();
        i=0;
        while (i < thesisList.size()) {
            Thesis thesis = thesisList.get(i);
            String id = thesis.getId().toString();
            List<String> foundThesisId = thesisIdList.stream().filter(thesisId -> thesisId.equals(id)).toList();
            if (foundThesisId.isEmpty()) {
                thesis.setDefenseJury(null);
                thesisRepo.save(thesis);
                thesisList.remove(i);
            }
            else{
                i++;
            }
        }

        for (String thesisId : thesisIdList) {
            List<Thesis> foundThesis = thesisList
                    .stream()
                    .filter(thesis -> thesis.getId().toString().equals(thesisId))
                    .toList();
            if (foundThesis.isEmpty()) {
                Thesis thesis = thesisRepo.findById(UUID.fromString(thesisId)).orElse(null);
                if (thesis == null) return null;
                thesis.setDefenseJury(defenseJury);
                thesisList.add(thesisRepo.save(thesis));
            }
        }
        defenseJury.setThesisList(thesisList);
        return defenseJuryRepo.save(defenseJury);
    }



}
