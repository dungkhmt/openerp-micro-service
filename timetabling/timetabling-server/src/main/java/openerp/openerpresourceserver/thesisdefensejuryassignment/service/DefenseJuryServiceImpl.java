package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.DefenseJuryDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.JuryTopicDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded.DefenseJuryTeacherRole;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@AllArgsConstructor
@Service


public class DefenseJuryServiceImpl implements DefenseJuryService {

    private static Logger logger = LogManager.getLogger(DefenseJuryServiceImpl.class);

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

    @Autowired
    private JuryTopicRepo juryTopicRepo;

    @Autowired
    private DefenseJurySessionRepo defenseJurySessionRepo;
    @Override
    @Transactional
    public String createNewDefenseJury(DefenseJuryIM defenseJury) {
        DefenseJury newDefenseJury = new DefenseJury();

        if ((defenseJury.getName().isEmpty()) || (defenseJury.getDefenseDate() == null)) {
            return null;
        }
        JuryTopic defensePlanTopic = juryTopicRepo.findById(defenseJury.getJuryTopicId()).orElse(null);
        System.out.println(defenseJury.getJuryTopicId());
        if (defensePlanTopic == null) return null;
        ThesisDefensePlan thesisDefensePlan = defensePlanTopic.getThesisDefensePlan();
        if (thesisDefensePlan == null) return null;
        System.out.println(thesisDefensePlan.getName());
        //compare date
        Date planStartDate = thesisDefensePlan.getStartDate();
        Date planEndDate = thesisDefensePlan.getEndDate();
        if (defenseJury.getDefenseDate().after(planEndDate))
            return "Ngày tổ chức phải trước ngày " + planEndDate.toString();
        if (defenseJury.getDefenseDate().before(planStartDate))
            return "Ngày tổ chức phải sau ngày " + planStartDate.toString();
        //
        int defenseRoomId = defenseJury.getDefenseRoomId();
        DefenseRoom defenseRoom = defenseRoomRepo.findById(defenseRoomId).orElse(null);
        if (defenseRoom == null) {
            return null;
        }

        List<Integer> defenseSessionIdList = defenseJury.getDefenseSessionId();
        List<DefenseSession> defenseSessionList = new ArrayList<>();
        for (int defenseSessionId: defenseSessionIdList) {
            DefenseSession defenseSession = defenseSessionRepo.findById(defenseSessionId).orElse(null);
            if (defenseSession == null) {
                return null;
            }
            List<DefenseJury> duplicateDefenseJury = defenseJuryRepo.findByPlanTopicThesisDefensePlanAndDefenseDateAndDefenseJurySessionListDefenseSessionAndDefenseRoom(thesisDefensePlan,
                    defenseJury.getDefenseDate(),
                    defenseSession,
                    defenseRoom);

            if (!duplicateDefenseJury.isEmpty()) {
                return "Hội đồng " + duplicateDefenseJury.get(0).getName() + " đã được tổ chức vào phòng " + defenseRoom.getName() + " cùng buổi";
            }
            defenseSessionList.add(defenseSession);
        }
        newDefenseJury.setDefenseRoom(defenseRoom);
        newDefenseJury.setName(defenseJury.getName());
        newDefenseJury.setDefenseDate(defenseJury.getDefenseDate());
        newDefenseJury.setCreatedTime(new Date());
        newDefenseJury.setMaxThesis(defenseJury.getMaxThesis());
        newDefenseJury.setPlanTopic(defensePlanTopic);
        newDefenseJury.setId(UUID.randomUUID());
        List<DefenseJurySession> defenseJurySessionList = new LinkedList<>();
        for (DefenseSession defenseSession: defenseSessionList){
            DefenseJurySession defenseJurySession = new DefenseJurySession(defenseSession,newDefenseJury);
            defenseJurySessionList.add(defenseJurySession);
        }
        newDefenseJury.setDefenseJurySessionList(defenseJurySessionList);
        DefenseJury saveJury = defenseJuryRepo.save(newDefenseJury);
        return "Success";

    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepo.findAll();
    }

    @Override
    public DefenseJuryDTO getDefenseJuryByID(UUID id) {
        DefenseJury defenseJury = defenseJuryRepo.findById(id).orElse(null);
        assert defenseJury != null;
        logger.info(defenseJury.getName());
        DefenseJuryDTO defenseJuryDTO = new DefenseJuryDTO();
        defenseJuryDTO.setId(defenseJury.getId().toString());
        defenseJuryDTO.setName(defenseJury.getName());
        defenseJuryDTO.setMaxThesis(defenseJury.getMaxThesis());
        defenseJuryDTO.setDefenseDate(defenseJury.getDefenseDate());
        defenseJuryDTO.setDefenseRoom(defenseJury.getDefenseRoom());
        defenseJuryDTO.setDefenseSession(new LinkedList<>());
        for (DefenseJurySession defenseJurySession : defenseJury.getDefenseJurySessionList()){
            defenseJuryDTO.getDefenseSession().add(defenseJurySession.getDefenseSession());
        }
        List<ThesisDTO> thesisDTOList = new ArrayList<>();
        for (Thesis thesis : defenseJury.getThesisList()){
            ThesisDTO thesisDTO = new ThesisDTO();
            thesisDTO.setId(thesis.getId());
            thesisDTO.setThesisName(thesis.getThesisName());
            thesisDTO.setThesisAbstract(thesis.getThesisAbstract());
            thesisDTO.setThesisDefensePlanId(thesis.getThesisDefensePlan().getId());
            thesisDTO.setDefenseJuryId(thesis.getDefenseJury().getId());
            thesisDTO.setDefenseJuryName(thesis.getDefenseJury().getName());
            thesisDTO.setSupervisor(thesis.getSupervisor().getTeacherName());
            thesisDTO.setStudentName(thesis.getStudentName());
            thesisDTO.setJuryTopicName(thesis.getJuryTopic().getName());
            if (thesis.getScheduledReviewer() != null){
                thesisDTO.setScheduledReviewer(thesis.getScheduledReviewer().getTeacherName());
            }
            if (thesis.getSecondaryJuryTopic() != null){
                thesisDTO.setSecondJuryTopicName(thesis.getSecondaryJuryTopic().getName());
            }
            thesisDTO.setAcademicKeywordList(thesis.getAcademicKeywordList());
            thesisDTOList.add(thesisDTO);
        }
        defenseJuryDTO.setThesisList(thesisDTOList);
        defenseJuryDTO.setDefenseJuryTeacherRoles(defenseJury.getDefenseJuryTeacherRoles());
        defenseJuryDTO.setAssigned(defenseJury.isAssigned());
        JuryTopicDTO juryTopicDTO = new JuryTopicDTO(defenseJury.getPlanTopic().getName(),
                defenseJury.getPlanTopic().getAcademicKeywordList());
        defenseJuryDTO.setJuryTopicDTO(juryTopicDTO);
        return defenseJuryDTO;
    }

    @Override
    public List<ThesisDTO> getAllAvailableThesiss(String thesisDefensePlanId) {
        List<Thesis> thesisList = thesisRepo.findByThesisDefensePlanIdAndDefenseJury(thesisDefensePlanId, null).orElse(null);
        if (thesisList == null) return  null;
        List<ThesisDTO> thesisDTOList = new ArrayList<>();
        for (Thesis thesis: thesisList){
            ThesisDTO thesisDTO = new ThesisDTO();
            thesisDTO.setId(thesis.getId());
            thesisDTO.setThesisName(thesis.getThesisName());
            thesisDTO.setThesisAbstract(thesis.getThesisAbstract());
            thesisDTO.setThesisDefensePlanId(thesis.getThesisDefensePlan().getId());
            thesisDTO.setSupervisor(thesis.getSupervisor().getTeacherName());
            thesisDTO.setStudentName(thesis.getStudentName());
            thesisDTO.setJuryTopicName(thesis.getJuryTopic().getName());
            if (thesis.getScheduledReviewer() != null){
                thesisDTO.setScheduledReviewer(thesis.getScheduledReviewer().getTeacherName());
            }
            if (thesis.getSecondaryJuryTopic() != null){
                thesisDTO.setSecondJuryTopicName(thesis.getSecondaryJuryTopic().getName());
            }
            thesisDTO.setAcademicKeywordList(thesis.getAcademicKeywordList());
            thesisDTOList.add(thesisDTO);
        }
        return thesisDTOList;
    }

    @Override
    public String assignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList) {
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(teacherAndThesisList.getDefenseJuryId())).orElse(null);
        if (defenseJury == null) return null;
        List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRoleId = teacherAndThesisList.getDefenseJuryTeacherRole();
        String president = "president";
        String secretary = "secretary";
        List<DefenseJuryTeacherRole> presidentAndSecretary = new ArrayList<>();
        List<String> thesisIdList = teacherAndThesisList.getThesisIdList();
        List<Thesis> thesisList = new ArrayList<>();
        List<DefenseJuryTeacherRole> defenseJuryTeacherRoleList = new ArrayList<>();
        HashMap<String, String> supervisorList = new  HashMap<>();
        for (String thesisId : thesisIdList) {
            Thesis thesis = thesisRepo.findById(UUID.fromString(thesisId)).orElse(null);
            if (thesis == null) return null;
            if (!supervisorList.containsKey(thesis.getSupervisor().getId())) {
                supervisorList.put(thesis.getSupervisor().getId(), thesis.getThesisName());
            }
            thesis.setDefenseJury(defenseJury);
            thesisList.add(thesis);
        }
        for (DefenseJuryTeacherRoleIM defenseJuryTeacherRoleIM : defenseJuryTeacherRoleId) {
            String teacherName = defenseJuryTeacherRoleIM.getTeacherName();
            int roleId = defenseJuryTeacherRoleIM.getRoleId();
            Teacher teacher = teacherRepo.findById(teacherName).orElse(null);
            Role teacherRole = roleRepo.findById(roleId).orElse(null);
            if (teacher == null) return null;
            if (teacherRole == null) return "Hãy lựa chọn nhiệm vụ cho giáo viên " + teacher.getTeacherName() ;
            for (DefenseJurySession defenseJurySession : defenseJury.getDefenseJurySessionList()){
                List<DefenseJury> defenseJuryAtTheSameDate = defenseJuryRepo.findByPlanTopicThesisDefensePlanAndDefenseDateAndDefenseJurySessionListDefenseSessionAndDefenseJuryTeacherRolesTeacherId(
                        defenseJury.getPlanTopic().getThesisDefensePlan(),
                        defenseJury.getDefenseDate(),
                        defenseJurySession.getDefenseSession(),
                        teacherName
                );
                boolean removed = defenseJuryAtTheSameDate.remove(defenseJury);
                if (!defenseJuryAtTheSameDate.isEmpty()) {
                    return "Giáo viên " + teacher.getTeacherName() + " đã được phân vào hội đồng "
                            + defenseJury.getName() + " cùng ngày";
                }
            }
            DefenseJuryTeacherRole defenseJuryTeacherRole1 = new DefenseJuryTeacherRole(teacher, teacherRole, defenseJury);
            if (teacherRole.getRole().equals(president) || teacherRole.getRole().equals(secretary)){
                if (supervisorList.containsKey(teacher.getId())){
                    return teacherRole.getName() + " " +teacher.getTeacherName() + " đang hướng dẫn đồ án "
                            + supervisorList.get(teacher.getId()) + " trong hội đồng.";
                }
            }
            defenseJuryTeacherRoleList.add(defenseJuryTeacherRole1);
        }
        for (Thesis thesis : thesisList) {
            Thesis thesis1 = thesisRepo.save(thesis);
        }
        for (DefenseJuryTeacherRole teacherRole: defenseJuryTeacherRoleList){
            DefenseJuryTeacherRole saved = defenseJuryTeacherRoleRepo.save(teacherRole);
        }
        defenseJury.setThesisList(thesisList);
        defenseJury.setDefenseJuryTeacherRoles(defenseJuryTeacherRoleList);
        DefenseJury savedJury = defenseJuryRepo.save(defenseJury);
        return "Successed";
    }

    @Override
    public DefenseJury assignReviewerToThesis(AssignReviewerToThesisIM teacherAndThesisList) {
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(teacherAndThesisList.getDefenseJuryId())).orElse(null);
        if (defenseJury == null) return null;
        for (ReviewerThesisIM reviewerThesis : teacherAndThesisList.getReviewerThesisList()) {
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
    public List<Teacher> assignTeacherAutomatically(String thesisDefensePlanId, String defenseJuryId, AssignTeacherToDefenseJuryAutomaticallyIM thesisList) {
        int MAX_TEACHER_NUM = 5;
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(defenseJuryId)).orElse(null);
        if (defenseJury == null) return null;
        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(thesisDefensePlanId).orElse(null);
        if (thesisDefensePlan == null) return null;
        List<Thesis> choseThesisList = new LinkedList<>();
        HashMap<String, Integer> keyword = new HashMap<>();
        for (AcademicKeyword academicKeyword: defenseJury.getPlanTopic().getAcademicKeywordList()){
            if (!keyword.containsKey(academicKeyword.getKeyword())){
                keyword.put(academicKeyword.getKeyword(), 1);
            }
        }
        for (String thesisId: thesisList.getThesisIdList()){
            Thesis thesis = thesisRepo.findById(UUID.fromString(thesisId)).orElse(null);
            if (thesis == null) return null;
            for (AcademicKeyword academicKeyword: thesis.getAcademicKeywordList()){
                if (!keyword.containsKey(academicKeyword.getKeyword())){
                    keyword.put(academicKeyword.getKeyword(), 1);
                }
            }
        }
        List<Teacher> teacherList = teacherRepo.findAll();
        List<Integer> teacherAndDefenseJuryMatchingScore = new ArrayList<>();
        int maxScore = 0;
        for (Teacher teacher : teacherList) {
            int score = calculateTeacherAndDefenseJuryMatchingScore(keyword, teacher);
            if (score > maxScore) {
                maxScore = score;
            }
            teacherAndDefenseJuryMatchingScore.add(score);
        }
        List<DefenseJury> defenseJuryListAtTheSameTime = new LinkedList<>();
        for (DefenseJurySession defenseJurySession: defenseJury.getDefenseJurySessionList()) {
            List<DefenseJury> defenseJurys = defenseJuryRepo.findByPlanTopicThesisDefensePlanAndDefenseDateAndDefenseJurySessionListDefenseSession(
                    thesisDefensePlan,
                    defenseJury.getDefenseDate(),
                    defenseJurySession.getDefenseSession()
            );
            defenseJuryListAtTheSameTime.addAll(defenseJurys);
        }
        List<Teacher> mostMatchingTeacherList = new ArrayList<>();
        if (defenseJuryListAtTheSameTime.size() == 1) { // only this jury at this time
            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherAndDefenseJuryMatchingScore.get(i) == maxScore){
                    mostMatchingTeacherList.add(teacherList.get(i));
                    if (mostMatchingTeacherList.size() == MAX_TEACHER_NUM){
                        break;
                    }
                }
            }
            if (mostMatchingTeacherList.size() < MAX_TEACHER_NUM){
                for (int i = 0; i < teacherList.size(); i++) {
                    if (teacherAndDefenseJuryMatchingScore.get(i) != 0) {
                        mostMatchingTeacherList.add(teacherList.get(i));
                        if (mostMatchingTeacherList.size() == MAX_TEACHER_NUM){
                            break;
                        }
                    }
                }

            }
            return mostMatchingTeacherList;
        }
        List <Teacher> teacherListInOtherJury = new LinkedList<>();
        for (DefenseJury jury : defenseJuryListAtTheSameTime){
            List<Teacher> teacherListInJury = jury.getDefenseJuryTeacherRoles().stream().map(DefenseJuryTeacherRole::getTeacher).toList();
            teacherListInOtherJury.addAll(teacherListInJury);
        }
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherAndDefenseJuryMatchingScore.get(i) == maxScore && !teacherListInOtherJury.contains(teacherList.get(i))){
                mostMatchingTeacherList.add(teacherList.get(i));
                if (mostMatchingTeacherList.size() == MAX_TEACHER_NUM){
                    break;
                }
            }
        }
        if (mostMatchingTeacherList.size() < MAX_TEACHER_NUM){
            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherAndDefenseJuryMatchingScore.get(i) != 0) {
                    mostMatchingTeacherList.add(teacherList.get(i));
                    if (mostMatchingTeacherList.size() == MAX_TEACHER_NUM){
                        break;
                    }
                }
            }

        }
        return mostMatchingTeacherList;
    }

    @Override
    public String updateDefenseJury(UpdateDefenseJuryIM updateDefenseJuryIM) {
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(updateDefenseJuryIM.getId())).orElse(null);
        if (defenseJury == null) return null;
        DefenseRoom defenseRoom = defenseRoomRepo.findById(updateDefenseJuryIM.getDefenseRoomId()).orElse(null);
        if (defenseRoom == null) return null;
        Date defenseDate = updateDefenseJuryIM.getDefenseDate();
        Date planStartDate = defenseJury.getPlanTopic().getThesisDefensePlan().getStartDate();
        Date planEndDate = defenseJury.getPlanTopic().getThesisDefensePlan().getEndDate();
        if (defenseDate.after(planEndDate)) return "Ngày tổ chức phải trước ngày " + planEndDate.toString();
        if (defenseDate.before(planStartDate)) return "Ngày tổ chức phải sau ngày " + planStartDate.toString();
        List<DefenseSession> defenseSessionList = new LinkedList<>();
        for (int defenseSessionId: updateDefenseJuryIM.getDefenseSessionId()) {
            DefenseSession defenseSession = defenseSessionRepo.findById(defenseSessionId).orElse(null);
            if (defenseSession == null) return null;
            List<DefenseJury> duplicateDefenseJury = defenseJuryRepo.findByPlanTopicThesisDefensePlanAndDefenseDateAndDefenseJurySessionListDefenseSessionAndDefenseRoom(defenseJury.getPlanTopic().getThesisDefensePlan(),
                    defenseDate,
                    defenseSession,
                    defenseRoom);
            boolean isRemoved = duplicateDefenseJury.remove(defenseJury);
            if (!duplicateDefenseJury.isEmpty()) {
                return "Hội đồng " + duplicateDefenseJury.get(0).getName() + " đã được tổ chức vào phòng " + defenseRoom.getName() + " cùng buổi";
            }

            defenseSessionList.add(defenseSession);

        }
        for (DefenseJurySession defenseJurySession: defenseJury.getDefenseJurySessionList()){
            defenseJurySessionRepo.delete(defenseJurySession);

        }
        List<DefenseJurySession> defenseJurySessionList = new ArrayList<>();
        for (DefenseSession defenseSession: defenseSessionList){
            DefenseJurySession defenseJurySession = new DefenseJurySession(defenseSession,defenseJury);
            defenseJurySessionList.add(defenseJurySession);
        }
        defenseJury.setName(updateDefenseJuryIM.getName());
        defenseJury.setMaxThesis(updateDefenseJuryIM.getMaxThesis());
        defenseJury.setDefenseRoom(defenseRoom);
        defenseJury.setDefenseDate(defenseDate);
        defenseJury.setDefenseJurySessionList(defenseJurySessionList);
        DefenseJury updatedDefenseJury = defenseJuryRepo.save(defenseJury);
        return "SUCCESS";
    }

    @Override
    public DefenseJury reassignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList) {
        String defenseJuryId = teacherAndThesisList.getDefenseJuryId();
        List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRoleIm = teacherAndThesisList.getDefenseJuryTeacherRole();
        List<String> thesisIdList = teacherAndThesisList.getThesisIdList();
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(defenseJuryId)).orElse(null);
        if (defenseJury == null) return null;
        List<DefenseJuryTeacherRole> defenseJuryTeacherRoles = defenseJury.getDefenseJuryTeacherRoles();
        List<Thesis> thesisList = defenseJury.getThesisList();
        HashMap<String, String> supervisorList = new HashMap<>();
        int i = 0;
        while (i < thesisList.size()) {
            Thesis thesis = thesisList.get(i);
            String id = thesis.getId().toString();
            List<String> foundThesisId = thesisIdList.stream().filter(thesisId -> thesisId.equals(id)).toList();
            if (foundThesisId.isEmpty()) {
                thesis.setDefenseJury(null);
                thesisRepo.save(thesis);
                thesisList.remove(i);
            } else {
                if (!supervisorList.containsKey(thesis.getSupervisor().getId())) {
                    supervisorList.put(thesis.getSupervisor().getId(), thesis.getThesisName());
                }
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
                if (!supervisorList.containsKey(thesis.getSupervisor().getId())) {
                    supervisorList.put(thesis.getSupervisor().getId(), thesis.getThesisName());
                }
                thesis.setDefenseJury(defenseJury);
                thesisList.add(thesis);
            }
        }
        i = 0;
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
            } else {
                DefenseJuryTeacherRoleIM foundTeacher = foundDefenseJuryTeacherRoleList.get(0);
                if (foundTeacher.getRoleId() != role) {
                    Role teacherRole = roleRepo.findById(foundTeacher.getRoleId()).orElse(null);
                    defenseJuryTeacherRole.setRole(teacherRole);
                    defenseJuryTeacherRoles.set(i, defenseJuryTeacherRole);
                }
                i++;
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
                defenseJuryTeacherRoles.add(defenseJuryTeacherRole);
            }
        }
        defenseJury.setThesisList(thesisList);
        defenseJury.setDefenseJuryTeacherRoles(defenseJuryTeacherRoles);
        return defenseJuryRepo.save(defenseJury);
    }
    @Override
    public DefenseJury deleteDefenseJuryByID(UUID id) {
        DefenseJury defenseJury = defenseJuryRepo.findById(id).orElse(null);
        if (defenseJury == null) return null;
        for (DefenseJurySession defenseJurySession: defenseJury.getDefenseJurySessionList()){
            defenseJurySessionRepo.delete(defenseJurySession);
        }
        for (DefenseJuryTeacherRole role : defenseJury.getDefenseJuryTeacherRoles()) {
            defenseJuryTeacherRoleRepo.delete(role);
        }
        for (Thesis thesis : defenseJury.getThesisList()) {
            thesis.setDefenseJury(null);
            thesis.setScheduledReviewer(null);
            thesisRepo.save(thesis);
        }
        defenseJuryRepo.delete(defenseJury);
        return defenseJury;
    }
    @Override
    public List<Thesis> getAvailableThesisByJuryTopic(String thesisDefensePlanId, String defenseJuryId) {
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(defenseJuryId)).orElse(null);
        if (defenseJury == null) return null;
        int juryTopic = defenseJury.getPlanTopic().getId();
        return thesisRepo.findByThesisDefensePlanIdAndDefenseJuryAndJuryTopicId(thesisDefensePlanId, null, juryTopic);
    }
    public int calculateTeacherAndDefenseJuryMatchingScore(HashMap<String, Integer> keyword, Teacher teacher) {
        int score = 0;
        for (AcademicKeyword academicKeyword : teacher.getAcademicKeywordList()) {
            if (keyword.containsKey(academicKeyword.getKeyword())) {
                score += 1;
            }
        }
        return score;
    }
}
