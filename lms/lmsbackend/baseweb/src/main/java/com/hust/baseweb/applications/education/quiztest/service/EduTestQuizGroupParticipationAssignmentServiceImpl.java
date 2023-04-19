package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroupParticipationAssignment;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.QuizTestGroupParticipantAssignmentOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.AddParticipantToQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.ModelResponseGetQuizTestGroup;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.RemoveParticipantToQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestGroupRepo;
import com.hust.baseweb.applications.education.quiztest.repo.EduTestQuizGroupParticipationAssignmentRepo;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EduTestQuizGroupParticipationAssignmentServiceImpl
    implements EduTestQuizGroupParticipationAssignmentService {

    private EduTestQuizGroupParticipationAssignmentRepo eduTestQuizGroupParticipationAssignmentRepo;
    private EduQuizTestGroupRepo eduQuizTestGroupRepo;

    private UserService userService;

    @Override
    public List<QuizTestGroupParticipantAssignmentOutputModel> getQuizTestGroupParticipant(String testId) {

        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(testId);
        List<UUID> groupIds = eduTestQuizGroups.stream().map(
            eduTestQuizGroup -> eduTestQuizGroup.getQuizGroupId()
        ).collect(Collectors.toList());
        Map<UUID, EduTestQuizGroup> mId2Group = new HashMap();
        for (EduTestQuizGroup e : eduTestQuizGroups) {
            mId2Group.put(e.getQuizGroupId(), e);
        }
        List<EduTestQuizGroupParticipationAssignment> eduTestQuizGroupParticipationAssignments
            = eduTestQuizGroupParticipationAssignmentRepo.findAllByQuizGroupIdIn(groupIds);
        List<QuizTestGroupParticipantAssignmentOutputModel> quizTestGroupParticipantAssignmentOutputModels = new ArrayList<>();
        for (EduTestQuizGroupParticipationAssignment e : eduTestQuizGroupParticipationAssignments) {
            String userLoginId = e.getParticipationUserLoginId();
            PersonModel personModel = userService.findPersonByUserLoginId(userLoginId);
            EduTestQuizGroup g = mId2Group.get(e.getQuizGroupId());
            UUID groupId = null;
            String groupCode = "";
            if (g != null) {
                groupId = g.getQuizGroupId();
                groupCode = g.getGroupCode();
            }
            String fullName = "";
            if (personModel != null) {
                fullName = personModel.getFirstName() +
                           " " +
                           personModel.getMiddleName() +
                           " " +
                           personModel.getLastName();
            }
            quizTestGroupParticipantAssignmentOutputModels.add(
                new QuizTestGroupParticipantAssignmentOutputModel(groupId, groupCode, userLoginId, fullName));
        }
        return quizTestGroupParticipantAssignmentOutputModels;
    }

    @Override
    public EduTestQuizGroupParticipationAssignment assignParticipant2QuizTestGroup(
        AddParticipantToQuizTestGroupInputModel input
    ) {
        EduTestQuizGroup eduTestQuizGroup = eduQuizTestGroupRepo.findById(input.getQuizTestGroupId()).orElse(null);
        if (eduTestQuizGroup == null) {
            log.info("assignParticipant2QuizTestGroup, cannot find eduTestQuizGroup " +
                     input.getQuizTestGroupId() +
                     "BUG???");
            return null;
        }
        String testId = eduTestQuizGroup.getTestId();

        // get list of test-group of the current testId
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(testId);
        List<UUID> groupIds = new ArrayList<>();
        for (EduTestQuizGroup g : eduTestQuizGroups) {
            groupIds.add(g.getQuizGroupId());
        }
        // get list of group-participant assignment related to input participant userLoginId
        List<EduTestQuizGroupParticipationAssignment> eduTestQuizGroupParticipationAssignments
            = eduTestQuizGroupParticipationAssignmentRepo.findAllByQuizGroupIdInAndParticipationUserLoginId(
            groupIds,
            input.getParticipantUserLoginId());

        if (eduTestQuizGroupParticipationAssignments == null || eduTestQuizGroupParticipationAssignments.size() == 0) {
            EduTestQuizGroupParticipationAssignment eduTestQuizGroupParticipationAssignment =
                new EduTestQuizGroupParticipationAssignment();
            eduTestQuizGroupParticipationAssignment.setParticipationUserLoginId(input.getParticipantUserLoginId());
            eduTestQuizGroupParticipationAssignment.setQuizGroupId(input.getQuizTestGroupId());
            eduTestQuizGroupParticipationAssignment = eduTestQuizGroupParticipationAssignmentRepo.save(
                eduTestQuizGroupParticipationAssignment);
            return eduTestQuizGroupParticipationAssignment;
        } else {
            if (eduTestQuizGroupParticipationAssignments.size() > 1) {
                log.info(
                    "assignParticipant2QuizTestGroup FOUND more than quiz-test-group of a given testId associated with the input participant " +
                    input.getParticipantUserLoginId());
                return null;
            } else {
                // lay ra assignment participant-2-group hien tai and update with new group
                EduTestQuizGroupParticipationAssignment a = eduTestQuizGroupParticipationAssignments.get(0);
                if (a.getQuizGroupId() == input.getQuizTestGroupId()) {
                    log.info("assignParticipant2QuizTestGroup records group " +
                             input.getQuizTestGroupId() +
                             ", participant " +
                             input.getParticipantUserLoginId() +
                             " EXISTS!!");
                    return a;
                }

                //a.setQuizGroupId(input.getQuizTestGroupId());
                //a = eduTestQuizGroupParticipationAssignmentRepo.save(a);
                //return a;
                // delete current record
                eduTestQuizGroupParticipationAssignmentRepo.delete(a);

                EduTestQuizGroupParticipationAssignment eduTestQuizGroupParticipationAssignment =
                    new EduTestQuizGroupParticipationAssignment();
                eduTestQuizGroupParticipationAssignment.setParticipationUserLoginId(input.getParticipantUserLoginId());
                eduTestQuizGroupParticipationAssignment.setQuizGroupId(input.getQuizTestGroupId());
                eduTestQuizGroupParticipationAssignment = eduTestQuizGroupParticipationAssignmentRepo.save(
                    eduTestQuizGroupParticipationAssignment);
                return eduTestQuizGroupParticipationAssignment;
            }

        }
    }

    @Override
    public boolean removeParticipantFromQuizTestGroup(RemoveParticipantToQuizTestGroupInputModel input) {
        log.info("removeParticipantFromQuizTestGroup, groupId = " +
                 input.getQuizTestGroupId() +
                 " participantId = " +
                 input.getParticipantUserLoginId());
        EduTestQuizGroupParticipationAssignment a = eduTestQuizGroupParticipationAssignmentRepo
            .findByQuizGroupIdAndParticipationUserLoginId(
                input.getQuizTestGroupId(),
                input.getParticipantUserLoginId());

        if (a != null) {
            eduTestQuizGroupParticipationAssignmentRepo.delete(a);
            return true;
        }
        log.info("removeParticipantFromQuizTestGroup, assign NOT EXISTS!!");
        return false;
    }

    @Override
    public ModelResponseGetQuizTestGroup getQuizTestGroupOfUser(String userId, String testId){
        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findByTestId(testId);
        ModelResponseGetQuizTestGroup res = new ModelResponseGetQuizTestGroup();
        res.setStatusId("NULL");
        for(EduTestQuizGroup g: groups){
            List<EduTestQuizGroupParticipationAssignment> L = eduTestQuizGroupParticipationAssignmentRepo.findAllByQuizGroupIdAndParticipationUserLoginId(
                g.getQuizGroupId(),
                userId);
            if(L != null && L.size() > 0){
                //EduTestQuizGroupParticipationAssignment e = L.get(0);
                //EduTestQuizGroup g = eduQuizTestGroupRepo.findById(e.getQuizGroupId()).orElse(null);
                //if(g != null) {
                    res.setGroupId(g.getQuizGroupId());
                    res.setGroupCode(g.getGroupCode());
                    res.setStatusId("OK");
                //}
            }
        }
        /*
        List<EduTestQuizGroupParticipationAssignment> L = eduTestQuizGroupParticipationAssignmentRepo
            .findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(userId);
        ModelResponseGetQuizTestGroup res = new ModelResponseGetQuizTestGroup();
        res.setStatusId("NULL");

        if(L != null && L.size() > 0){
            EduTestQuizGroupParticipationAssignment e = L.get(0);
            EduTestQuizGroup g = eduQuizTestGroupRepo.findById(e.getQuizGroupId()).orElse(null);
            if(g != null) {
                res.setGroupId(e.getQuizGroupId());
                res.setGroupCode(g.getGroupCode());
                res.setStatusId("OK");
            }
        }

         */
        return res;
    }
}
