package com.hust.baseweb.applications.education.quiztest.controller;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroupParticipationAssignment;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.AddParticipantToQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.AddParticipantToQuizTestGroupOfAQuizTestInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.RemoveParticipantToQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestGroupRepo;
import com.hust.baseweb.applications.education.quiztest.service.EduTestQuizGroupParticipationAssignmentService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Log4j2
@RestController
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EduTestQuizGroupParticipationAssignmentController {

    private EduTestQuizGroupParticipationAssignmentService eduTestQuizGroupParticipationAssignmentService;
    private EduQuizTestGroupRepo eduQuizTestGroupRepo;

    @PostMapping("/add-participant-to-quiz-test-group")
    public ResponseEntity<?> addParticipantToQuizTestGroup(
        Principal principal, @RequestBody
        AddParticipantToQuizTestGroupInputModel input
    ) {
        log.info("addParticipantToQuizTestGroup, groupId = " +
                 input.getQuizTestGroupId() +
                 " participantId = " +
                 input.getParticipantUserLoginId());

        // them ban ghi vao bang EduTestQuizGroupParticipationAssignment
        EduTestQuizGroupParticipationAssignment eduTestQuizGroupParticipationAssignment
            = eduTestQuizGroupParticipationAssignmentService.assignParticipant2QuizTestGroup(input);
        return ResponseEntity.ok().body(eduTestQuizGroupParticipationAssignment);
    }
    @PostMapping("/add-participant-to-test-group-of-a-quiztest")
    public ResponseEntity<?> addParticipantToTestGroupOfAQuizTest(Principal principal, @RequestBody
        AddParticipantToQuizTestGroupOfAQuizTestInputModel input
                                                                  ){
        log.info("addParticipantToTestGroupOfAQuizTest, user = " + input.getUserLoginId() +
                 " test " + input.getTestId() + " groupCode " + input.getGroupCode());

        List<EduTestQuizGroup> lst = eduQuizTestGroupRepo.findAllByTestIdAndGroupCode(input.getTestId(), input.getGroupCode());
        if(lst == null || lst.size() == 0){
            log.info("addParticipantToTestGroupOfAQuizTest, user = " + input.getUserLoginId() +
                     " test " + input.getTestId() + " groupCode " + input.getGroupCode()
            + " NOT FOUND test group???");
            return ResponseEntity.ok().body("NOT FOUND TEST GROUP");
        }
        EduTestQuizGroup g = lst.get(0);
        AddParticipantToQuizTestGroupInputModel I = new AddParticipantToQuizTestGroupInputModel(
            input.getUserLoginId(), g.getQuizGroupId());

        // them ban ghi vao bang EduTestQuizGroupParticipationAssignment
        EduTestQuizGroupParticipationAssignment eduTestQuizGroupParticipationAssignment
            = eduTestQuizGroupParticipationAssignmentService.assignParticipant2QuizTestGroup(I);
        return ResponseEntity.ok().body(eduTestQuizGroupParticipationAssignment);

    }

    @PostMapping("/remove-participant-from-quiz-test-group")
    public ResponseEntity<?> removeParticipantFromQuizTestGroup(
        Principal principal, @RequestBody
        RemoveParticipantToQuizTestGroupInputModel input
    ) {
        log.info("removeParticipantFromQuizTestGroup, groupId = " +
                 input.getQuizTestGroupId() +
                 " participantId = " +
                 input.getParticipantUserLoginId());

        // them ban ghi vao bang EduTestQuizGroupParticipationAssignment
        boolean ok
            = eduTestQuizGroupParticipationAssignmentService.removeParticipantFromQuizTestGroup(input);

        return ResponseEntity.ok().body(ok);
    }

}
