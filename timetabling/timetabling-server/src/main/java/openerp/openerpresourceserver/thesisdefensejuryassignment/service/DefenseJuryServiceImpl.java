package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded.DefenseJuryTeacherRole;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public String createNewDefenseJury(DefenseJuryIM defenseJury) {
        DefenseJury newDefenseJury = new DefenseJury();

        if ((defenseJury.getName().isEmpty()) || (defenseJury.getDefenseDate() == null)
                || (defenseJury.getThesisPlanName() == null)) {
            return null;
        }

        String thesisDefensePlanId = defenseJury.getThesisPlanName();
        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(thesisDefensePlanId).orElse(null);
        if (thesisDefensePlan == null) return null;
        //compare date
        Date planStartDate = thesisDefensePlan.getStartDate();
        Date planEndDate = thesisDefensePlan.getEndDate();
        if (defenseJury.getDefenseDate().after(planEndDate))
            return "Ngày tổ chức phải trước ngày " + planEndDate.toString();
        if (defenseJury.getDefenseDate().before(planStartDate))
            return "Ngày tổ chức phải sau ngày " + planStartDate.toString();
        //
//        List<AcademicKeyword> academicKeywordList = new LinkedList<>();
//        for (int i = 0; i < defenseJury.getAcademicKeywordList().size(); i++) {
//            AcademicKeyword foundAcademicKeyword = academicKeywordRepo.findById(defenseJury.getAcademicKeywordList().get(i)).orElse(null);
//            if (foundAcademicKeyword == null) {
//                return null;
//            }
//            academicKeywordList.add(foundAcademicKeyword);
//        }
        JuryTopic juryTopic = juryTopicRepo.findById(defenseJury.getJuryTopicId()).orElse(null);
        int defenseRoomId = defenseJury.getDefenseRoomId();
        DefenseRoom defenseRoom = defenseRoomRepo.findById(defenseRoomId).orElse(null);
        if (defenseRoom == null) {
            return null;
        }

        int defenseSessionId = defenseJury.getDefenseSessionId();
        DefenseSession defenseSession = defenseSessionRepo.findById(defenseSessionId).orElse(null);

        if (defenseSession == null) {
            return null;
        }
        List<DefenseJury> duplicateDefenseJury = defenseJuryRepo.findByThesisDefensePlanAndDefenseDateAndDefenseSessionAndDefenseRoom(thesisDefensePlan,
                defenseJury.getDefenseDate(),
                defenseSession,
                defenseRoom);

        if (!duplicateDefenseJury.isEmpty()) {
            return "Hội đồng " + duplicateDefenseJury.get(0).getName() + " đã được tổ chức vào phòng " + defenseRoom.getName() + " cùng buổi";
        }
        newDefenseJury.setDefenseRoom(defenseRoom);
        newDefenseJury.setDefenseSession(defenseSession);
        newDefenseJury.setThesisDefensePlan(thesisDefensePlan);
//        newDefenseJury.setAcademicKeywordList(academicKeywordList);
        newDefenseJury.setJuryTopic(juryTopic);
        newDefenseJury.setName(defenseJury.getName());
        newDefenseJury.setDefenseDate(defenseJury.getDefenseDate());
        newDefenseJury.setCreatedTime(new Date());
        newDefenseJury.setMaxThesis(defenseJury.getMaxThesis());

        DefenseJury saveJury = defenseJuryRepo.save(newDefenseJury);
        return "Success";
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepo.findAll();
    }

    @Override
    public DefenseJury getDefenseJuryByID(UUID id) {

        DefenseJury defenseJury = defenseJuryRepo.findById(id).orElse(null);
        assert defenseJury != null;
        logger.info(defenseJury.getName());
        return defenseJury;
    }

    @Override
    public List<Thesis> getAllAvailableThesiss(String thesisDefensePlanId) {
        return thesisRepo.findByThesisDefensePlanIdAndDefenseJury(thesisDefensePlanId, null).orElse(null);
    }

    @Override
    public String assignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList) {
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
            List<DefenseJury> defenseJuryAtTheSameDate = defenseJuryRepo.findByThesisDefensePlanAndDefenseDateAndDefenseSessionAndDefenseJuryTeacherRolesTeacherId(
                    defenseJury.getThesisDefensePlan(),
                    defenseJury.getDefenseDate(),
                    defenseJury.getDefenseSession(),
                    teacherName
            );
            boolean removed = defenseJuryAtTheSameDate.remove(defenseJury);
            if (!defenseJuryAtTheSameDate.isEmpty()) {
                return "Giáo viên " + teacher.getTeacherName() + " đã được phân vào hội đồng "
                        + defenseJury.getName() + " cùng ngày";
            }
            DefenseJuryTeacherRole defenseJuryTeacherRole1 = new DefenseJuryTeacherRole(teacher, teacherRole, defenseJury);
            DefenseJuryTeacherRole newDefenseJuryTeacherRole = defenseJuryTeacherRoleRepo.save(defenseJuryTeacherRole1);
            defenseJury.getDefenseJuryTeacherRoles().add(newDefenseJuryTeacherRole);
        }
        List<String> thesisList = teacherAndThesisList.getThesisIdList();
        for (String thesisId : thesisList) {
            System.out.println(UUID.fromString(thesisId));
            Thesis thesis = thesisRepo.findById(UUID.fromString(thesisId)).orElse(null);
            if (thesis == null) return null;
            thesis.setDefenseJury(defenseJury);
            defenseJury.getThesisList().add(thesisRepo.save(thesis));
        }
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
    public List<Teacher> assignTeacherAutomatically(String thesisDefensePlanId, String defenseJuryId) {
        int MAX_TEACHER_NUM = 5;
        DefenseJury defenseJury = defenseJuryRepo.findById(UUID.fromString(defenseJuryId)).orElse(null);
        if (defenseJury == null) return null;
        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(thesisDefensePlanId).orElse(null);
        if (thesisDefensePlan == null) return null;
        List<Teacher> teacherList = teacherRepo.findAll();
        List<Integer> teacherAndDefenseJuryMatchingScore = new ArrayList<>();
        int maxScore = 0;
        for (Teacher teacher : teacherList) {
            int score = calculateTeacherAndDefenseJuryMatchingScore(defenseJury, teacher);
            if (score > maxScore) {
                maxScore = score;
            }
            teacherAndDefenseJuryMatchingScore.add(score);
        }
        List<DefenseJury> defenseJuryListAtTheSameTime = defenseJuryRepo.findByThesisDefensePlanAndDefenseDateAndDefenseSession(
                thesisDefensePlan,
                defenseJury.getDefenseDate(),
                defenseJury.getDefenseSession()
        );
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
        DefenseSession defenseSession = defenseSessionRepo.findById(updateDefenseJuryIM.getDefenseSessionId()).orElse(null);
        if (defenseSession == null) return null;
        Date defenseDate = updateDefenseJuryIM.getDefenseDate();
        Date planStartDate = defenseJury.getThesisDefensePlan().getStartDate();
        Date planEndDate = defenseJury.getThesisDefensePlan().getEndDate();
        if (defenseDate.after(planEndDate)) return "Ngày tổ chức phải trước ngày " + planEndDate.toString();
        if (defenseDate.before(planStartDate)) return "Ngày tổ chức phải sau ngày " + planStartDate.toString();
        JuryTopic juryTopic = juryTopicRepo.findById(updateDefenseJuryIM.getJuryTopicId()).orElse(null);
//        List<AcademicKeyword> academicKeywordList = new ArrayList<>();
//        for (String keyword : updateDefenseJuryIM.getAcademicKeywordList()) {
//            AcademicKeyword academicKeyword = academicKeywordRepo.findById(keyword).orElse(null);
//            if (academicKeyword == null) return null;
//            academicKeywordList.add(academicKeyword);
//        }
        List<DefenseJury> duplicateDefenseJury = defenseJuryRepo.findByThesisDefensePlanAndDefenseDateAndDefenseSessionAndDefenseRoom(defenseJury.getThesisDefensePlan(),
                defenseDate,
                defenseSession,
                defenseRoom);
        boolean isRemoved = duplicateDefenseJury.remove(defenseJury);
        if (!duplicateDefenseJury.isEmpty()) {
            return "Hội đồng " + duplicateDefenseJury.get(0).getName() + " đã được tổ chức vào phòng " + defenseRoom.getName() + " cùng buổi";
        }
        defenseJury.setName(updateDefenseJuryIM.getName());
        defenseJury.setMaxThesis(updateDefenseJuryIM.getMaxThesis());
        defenseJury.setDefenseSession(defenseSession);
        defenseJury.setDefenseRoom(defenseRoom);
//        defenseJury.setAcademicKeywordList(academicKeywordList);
        defenseJury.setJuryTopic(juryTopic);
        defenseJury.setDefenseDate(defenseDate);
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
        int i = 0;
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
                if (foundTeacher.getRoleId() == role) {
                    i++;
                } else {
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
        i = 0;
        while (i < thesisList.size()) {
            Thesis thesis = thesisList.get(i);
            String id = thesis.getId().toString();
            List<String> foundThesisId = thesisIdList.stream().filter(thesisId -> thesisId.equals(id)).toList();
            if (foundThesisId.isEmpty()) {
                thesis.setDefenseJury(null);
                thesisRepo.save(thesis);
                thesisList.remove(i);
            } else {
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

    @Override
    public DefenseJury deleteDefenseJuryByID(UUID id) {
        DefenseJury defenseJury = defenseJuryRepo.findById(id).orElse(null);
        if (defenseJury == null) return null;
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
        int juryTopic = defenseJury.getJuryTopic().getId();
        return thesisRepo.findByThesisDefensePlanIdAndDefenseJuryAndJuryTopicId(thesisDefensePlanId, null, juryTopic);
    }

    public int calculateTeacherAndDefenseJuryMatchingScore(DefenseJury defenseJury, Teacher teacher) {
        int score = 0;
        HashMap<String, Integer> teacherKeyword = new HashMap<>();
        for (AcademicKeyword academicKeyword : teacher.getAcademicKeywordList()) {
            teacherKeyword.put(academicKeyword.getKeyword(), 1);
        }
        for (AcademicKeyword academicKeyword : defenseJury.getJuryTopic().getAcademicKeywordList()) {
            if (teacherKeyword.containsKey(academicKeyword.getKeyword())) {
                score += 1;
            }
        }
        return score;
    }

}
