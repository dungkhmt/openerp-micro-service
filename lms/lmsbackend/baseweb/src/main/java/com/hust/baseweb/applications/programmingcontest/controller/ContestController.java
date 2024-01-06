package com.hust.baseweb.applications.programmingcontest.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.repo.ContestProblemRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ContestRepo;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepo;
import com.hust.baseweb.applications.programmingcontest.service.ContestService;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ContestController {

    ProblemTestCaseService problemTestCaseService;
    ContestRepo contestRepo;
    ContestSubmissionRepo contestSubmissionRepo;
    ContestProblemRepo contestProblemRepo;
    UserService userService;
    ContestService contestService;

    @Secured("ROLE_TEACHER")
    @PostMapping("/contests")
    public ResponseEntity<?> createContest(
        @RequestBody @Valid ModelCreateContest modelCreateContest,
        Principal principal
    )
        throws Exception {
        log.info("createContest {}", modelCreateContest);
        ContestEntity contest = problemTestCaseService.createContest(modelCreateContest, principal.getName());
        //ContestEntity contest = problemTestCaseService.createContest(modelCreateContest, principal.getName());
        return ResponseEntity.status(200).body(contest);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/contests/import-problems")
    public ResponseEntity<?> importProblemsFromAContest(@RequestBody ModelImportProblemsFromAContestInput input) {

        try {
            List<ModelImportProblemFromContestResponse> res = problemTestCaseService.importProblemsFromAContest(input);
            return ResponseEntity.ok().body(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Secured("ROLE_TEACHER")
    @PutMapping("/contests/{contestId}")
    public ResponseEntity<?> editContest(
        @RequestBody ModelUpdateContest modelUpdateContest, Principal principal,
        @PathVariable("contestId") String contestId
    ) throws Exception {
        log.info("edit contest modelUpdateContest {}", modelUpdateContest);

        problemTestCaseService.updateContest(modelUpdateContest, principal.getName(), contestId);

        return ResponseEntity.status(200).body(null);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/contest-problem")
    public ResponseEntity<?> saveProblemToContest(
        @RequestBody ModelProblemInfoInContest modelProblemInfoInContest,
        Principal principal
    ) throws Exception {

        problemTestCaseService.saveProblemInfoInContest(modelProblemInfoInContest, principal.getName());

        return ResponseEntity.status(200).body("ok");
    }

    @Secured("ROLE_TEACHER")
    @DeleteMapping("/contest-problem")
    public ResponseEntity<?> removeProblemFromContest(
        @RequestParam String contestId,
        @RequestParam String problemId,
        Principal principal
    ) {

        problemTestCaseService.removeProblemFromContest(contestId, problemId, principal.getName());

        return ResponseEntity.status(200).body("ok");
    }

    @GetMapping("/contests/roles")
    public ResponseEntity<?> getListRolesContest() {
        List<String> L = UserRegistrationContestEntity.getListRoles();
        return ResponseEntity.ok().body(L);
    }

    @GetMapping("/contests/{contestId}")
    public ResponseEntity<?> getContestDetail(@PathVariable("contestId") String contestId, Principal principal) {
        log.info("getContestDetail constestid {}", contestId);
        ModelGetContestDetailResponse response = problemTestCaseService.getContestDetailByContestIdAndTeacher(
            contestId,
            principal.getName());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/contests/{contestId}/problems/{problemId}")
    public ResponseEntity<?> getProblemDetailInContestViewByStudent(
        @PathVariable("problemId") String problemId, @PathVariable("contestId") String contestId
    ) {
        try {
            ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
            ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);
            if (cp == null) {
                return ResponseEntity.ok().body("NOTFOUND");
            }
            ModelCreateContestProblemResponse problemEntity = problemTestCaseService.getContestProblem(problemId);
            ModelStudentViewProblemDetail model = new ModelStudentViewProblemDetail();
            if (contestEntity.getProblemDescriptionViewType() != null &&
                contestEntity.getProblemDescriptionViewType()
                             .equals(ContestEntity.CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_HIDDEN)) {
                model.setProblemStatement(" ");
            } else {
                model.setProblemStatement(problemEntity.getProblemDescription());
            }

            model.setSubmissionMode(cp.getSubmissionMode());
            model.setProblemName(cp.getProblemRename());
            model.setProblemCode(cp.getProblemRecode());
            model.setIsPreloadCode(problemEntity.getIsPreloadCode());
            model.setPreloadCode(problemEntity.getPreloadCode());
            model.setAttachment(problemEntity.getAttachment());
            model.setAttachmentNames(problemEntity.getAttachmentNames());
            model.setListLanguagesAllowed(contestEntity.getListLanguagesAllowed());
            return ResponseEntity.ok().body(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("NOTFOUND");
    }


    @GetMapping("/contests/{contestId}/problems")
    public ResponseEntity<?> getListContestProblemViewedByStudent(@PathVariable("contestId") String contestId) {
        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> listProblem = contestEntity.getProblems();
        return ResponseEntity.status(200).body(listProblem);
    }

    @GetMapping("/contests/{contestId}/problems/v2")
    public ResponseEntity<?> getListContestProblemViewedByStudentV2(
        @PathVariable("contestId") String contestId,
        Principal principal
    ) {
        String userId = principal.getName();
        ContestEntity contestEntity = contestService.findContest(contestId);

        List<ProblemEntity> listProblem = contestEntity.getProblems();
        List<String> listAcceptedProblem = contestSubmissionRepo.findAcceptedProblemsOfUser(userId, contestId);
        List<ModelProblemMaxSubmissionPoint> listTriedProblem = contestSubmissionRepo.findSubmittedProblemsOfUser(
            userId,
            contestId);

        Map<String, Long> mapProblemToMaxSubmissionPoint = new HashMap<>();
        for (ModelProblemMaxSubmissionPoint problem : listTriedProblem) {
            mapProblemToMaxSubmissionPoint.put(problem.getProblemId(), problem.getMaxPoint());
        }

        List<ModelStudentOverviewProblem> responses = new ArrayList<>();

        if (contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            for (ProblemEntity problem : listProblem) {
                String problemId = problem.getProblemId();

                ContestProblem contestProblem = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);

                ModelStudentOverviewProblem response = new ModelStudentOverviewProblem();
                response.setProblemId(problemId);
                response.setProblemName(contestProblem.getProblemRename());
                response.setProblemCode(contestProblem.getProblemRecode());
                response.setLevelId(problem.getLevelId());

                List<String> tags = problem.getTags().stream().map(TagEntity::getName).collect(Collectors.toList());
                response.setTags(tags);

                if (mapProblemToMaxSubmissionPoint.containsKey(problemId)) {
                    response.setSubmitted(true);
                    response.setMaxSubmittedPoint(mapProblemToMaxSubmissionPoint.get(problemId));
                }

                if (listAcceptedProblem.contains(problemId)) {
                    response.setAccepted(true);
                }

                responses.add(response);
            }
        }
        return ResponseEntity.status(200).body(responses);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/admin/contests")
    public ResponseEntity<?> getAllContestPagingByAdmin(
        Principal principal, Pageable pageable,
        @Param("sortBy") String sortBy
    ) {
        if (sortBy != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));
        } else {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                      Sort.by("createdAt").ascending());
        }
        ModelGetContestPageResponse resp = problemTestCaseService.getAllContestsPagingByAdmin(
            principal.getName(),
            pageable);
        return ResponseEntity.status(200).body(resp);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/contests")
    public ResponseEntity<?> getManagedContestOfTeacher(Principal principal) {
        List<ModelGetContestResponse> resp = problemTestCaseService
            .getManagedContestOfTeacher(principal.getName());
        return ResponseEntity.status(200).body(resp);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/all-contests")
    public ResponseEntity<?> getAllContest(Principal principal) {
        List<ModelGetContestResponse> resp = problemTestCaseService
            .getAllContests(principal.getName());
        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping("contests/{contestId}/register-student")
    public ResponseEntity<?> studentRegisterContest(@PathVariable("contestId") String contestId, Principal principal)
        throws MiniLeetCodeException {
        log.info("studentRegisterContest {}", contestId);
        ModelStudentRegisterContestResponse resp = problemTestCaseService.studentRegisterContest(
            contestId,
            principal.getName());
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/contests/{contestId}/registered-users")
    public ResponseEntity<?> getUserRegisterSuccessfulContest(
        @PathVariable("contestId") String contestId,
        Pageable pageable
    ) {
        log.info("get User Register Successful Contest ");
        ListModelUserRegisteredContestInfo resp = problemTestCaseService
            .getListUserRegisterContestSuccessfulPaging(pageable, contestId);
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/contests/{contestId}/members")
    public ResponseEntity<?> getMembersOfContest(@PathVariable String contestId) {
        List<ModelMemberOfContestResponse> res = problemTestCaseService.getListMemberOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/contests/{contestId}/group/members")
    public ResponseEntity<?> getMembersOfContestGroup(Principal principal, @PathVariable String contestId) {
        String userId = principal.getName();
        List<ModelMemberOfContestResponse> res = problemTestCaseService.getListMemberOfContestGroup(contestId, userId);
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_TEACHER")
    @DeleteMapping("/contests/members")
    public ResponseEntity<?> removeMemberFromContest(
        @RequestBody ModelRemoveMemberFromContestInput input
    ) {
        boolean res = problemTestCaseService.removeMemberFromContest(input.getId());
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_TEACHER")
    @DeleteMapping("/contests/group/members")
    public ResponseEntity<?> removeMemberFromContestGroup(
        Principal principal,
        @RequestBody ModelRemoveMemberFromContestGroupInput input
    ) {
        String userId = principal.getName();
        boolean res = problemTestCaseService.removeMemberFromContestGroup(
            input.getContestId(),
            userId,
            input.getParticipantId());
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_TEACHER")
    @PutMapping("/contests/permissions")
    public ResponseEntity<?> updatePermissionOfMemberToContest(
        Principal principal,
        @RequestBody ModelUpdatePermissionMemberToContestInput input
    ) {
        boolean res = problemTestCaseService.updatePermissionMemberToContest(principal.getName(), input);
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/contests/{contestId}/pending-users")
    public ResponseEntity<?> getUserRegisterPendingContest(
        @PathVariable("contestId") String contestId,
        Pageable pageable, @Param("size") String size, @Param("page") String page
    ) {
        log.info("get User Register Pending Contest pageable {} size {} page {} contest id {}", pageable, size, page,
                 contestId);
        ListModelUserRegisteredContestInfo resp = problemTestCaseService
            .getListUserRegisterContestPendingPaging(pageable, contestId);
        return ResponseEntity.status(200).body(resp);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/contests/{contestId}/pending-users-v2")
    public ResponseEntity<?> getPendingRegisteredUserOfContest(@PathVariable String contestId) {
        List<ModelMemberOfContestResponse> res = problemTestCaseService.getPendingRegisteredUsersOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/contests/registers/approval-management")
    public ResponseEntity<?> teacherManagerStudentRegisterContest(
        Principal principal,
        @RequestBody ModelTeacherManageStudentRegisterContest request
    ) throws MiniLeetCodeException {
        log.info("teacherManagerStudentRegisterContest");
        problemTestCaseService.teacherManageStudentRegisterContest(principal.getName(), request);
        return ResponseEntity.status(200).body(null);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/contests/registers/approval")
    public ResponseEntity<?> approveRegisteredUser2Contest(
        Principal principal,
        @RequestBody ModelApproveRegisterUser2ContestInput input
    ) {
        try {
            problemTestCaseService.approveRegisteredUser2Contest(principal.getName(), input);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body("");
        }
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/students/contests")
    public ResponseEntity<?> getContestRegisteredStudent(Principal principal) {
        ModelGetContestPageResponse res = problemTestCaseService.getRegisteredContestsByUser(principal.getName());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/students/not-registered-contests")
    public ResponseEntity<?> getContestNotRegisteredByStudentPaging(
        Pageable pageable, @Param("sortBy") String sortBy,
        Principal principal
    ) {
        log.info("getContestRegisteredByStudentPaging sortBy {} pageable {}", sortBy, pageable);
        if (sortBy != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));
        } else {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                      Sort.by("startedAt").descending());
        }
        ModelGetContestPageResponse modelGetContestPageResponse = problemTestCaseService
            .getNotRegisteredContestByUser(pageable, principal.getName());
        return ResponseEntity.status(200).body(modelGetContestPageResponse);
    }

    @Deprecated
    @PostMapping("/contests/users")
    public ResponseEntity<?> addUserContest(@RequestBody ModelAddUserToContest modelAddUserToContest) {
        problemTestCaseService.addUserToContest(modelAddUserToContest);
        return ResponseEntity.status(200).body(null);
    }

    @PostMapping("/contests/{id}/users")
    public ResponseEntity<?> addUsers2Contest(
        @PathVariable(name = "id") String contestId,
        @RequestBody AddUsers2Contest addUsers2Contest
    ) {
        problemTestCaseService.addUsers2ToContest(contestId, addUsers2Contest);
        return ResponseEntity.status(200).body(null);
    }

    @DeleteMapping("/contests/users")
    public ResponseEntity<?> deleteUserFromContest(@RequestBody ModelAddUserToContest modelAddUserToContest)
        throws MiniLeetCodeException {
        problemTestCaseService.deleteUserContest(modelAddUserToContest);
        return ResponseEntity.status(200).body(null);
    }

    //    @GetMapping("/public/ranking-programming-contest/{contestId}")
//    public ResponseEntity<?> getRankingContestPublic(@PathVariable("contestId") String contestId, Pageable pageable) {
//        pageable = Pageable.unpaged();
//        List<ContestSubmissionsByUser> page = problemTestCaseService.getRankingByContestIdNew(pageable, contestId,
//                                                                                              Constants.GetPointForRankingType.HIGHEST);
//        // log.info("ranking page {}", page);
//        return ResponseEntity.status(200).body(page);
//    }

    @GetMapping("/contests/public-ranking/{contestId}")
    public ResponseEntity<?> getRankingContestPublic(
        @PathVariable("contestId") String contestId,
        @RequestParam Constants.GetPointForRankingType getPointForRankingType
    ) {
        // if (contestSubmissionRepo.countAllByContestId(contestId) > 500) {
        //     return ResponseEntity
        //         .status(400)
        //         .body("This contest size is too big. Contact the contest manager for ranking table");
        // }

        List<ContestSubmissionsByUser> res = problemTestCaseService.getRankingByContestIdNew(
            contestId,
            getPointForRankingType);

        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/contests/ranking/{contestId}")
    public ResponseEntity<?> getRankingContestNewVersion(
        @PathVariable("contestId") String contestId,
        @RequestParam Constants.GetPointForRankingType getPointForRankingType
    ) {
        List<ContestSubmissionsByUser> res = problemTestCaseService.getRankingByContestIdNew(
            contestId,
            getPointForRankingType);
        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/contests/group/ranking/{contestId}")
    public ResponseEntity<?> getRankingContestGroupNewVersion(
        Principal principal,
        @PathVariable("contestId") String contestId,
        @RequestParam Constants.GetPointForRankingType getPointForRankingType
    ) {
        String userId = principal.getName();
        List<ContestSubmissionsByUser> res = problemTestCaseService.getRankingGroupByContestIdNew(
            userId,
            contestId,
            getPointForRankingType);
        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/contests/{contestId}/users/submissions")
    public ResponseEntity<?> getContestSubmissionPagingOfCurrentUser(
        Principal principal,
        @PathVariable String contestId
    ) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService
            .findContestSubmissionByUserLoginIdAndContestIdPaging(pageable, principal.getName(), contestId);
        return ResponseEntity.status(200).body(page);
    }

    @GetMapping("/contests/users/submissions")
    public ResponseEntity<?> getContestSubmissionInProblemPagingOfCurrentUser(
        Principal principal,
        @RequestParam("contestid") String contestId, @RequestParam("problemid") String problemId
    ) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService
            .findContestSubmissionByUserLoginIdAndContestIdAndProblemIdPaging(
                pageable,
                principal.getName(),
                contestId,
                problemId);
        return ResponseEntity.status(200).body(page);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/contests/{contestId}/submissions")
    public ResponseEntity<?> getContestSubmissionPaging(
        @PathVariable("contestId") String contestId,
        @RequestParam String search,
        @RequestParam int page,
        @RequestParam int size
    ) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ContestSubmission> res = problemTestCaseService.findContestSubmissionByContestIdPaging(
            pageRequest,
            contestId,
            search);
        return ResponseEntity.status(200).body(res);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/contests/{contestId}/group/submissions")
    public ResponseEntity<?> getContestGroupSubmissionPaging(
        Principal principal,
        @PathVariable("contestId") String contestId,
        @RequestParam String search,
        @RequestParam int page,
        @RequestParam int size
    ) {
        String userId = principal.getName();
        log.info("getContestGroupSubmissionPaging, userId = " + userId);
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ContestSubmission> res = problemTestCaseService.findContestGroupSubmissionByContestIdPaging(
            pageRequest,
            contestId,
            userId,
            search);
        return ResponseEntity.status(200).body(res);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/teacher/contests/{contestId}/users/{userId}/submissions")
    public ResponseEntity<?> getContestSubmissionOfAUserPaging(
        @PathVariable("contestId") String contestId,
        @PathVariable String userId, Pageable pageable
    ) {
        log.info("getContestSubmissionPaging, contestId = " + contestId);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService
            .findContestSubmissionByUserLoginIdAndContestIdPaging(pageable, userId, contestId);
        log.info("page {}", page);
        return ResponseEntity.status(200).body(page);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/contests/{contestId}/judged-submissions")
    public ResponseEntity<?> getUserJudgedProblemSubmission(@PathVariable String contestId) {
        List<ModelUserJudgedProblemSubmissionResponse> res = problemTestCaseService
            .getUserJudgedProblemSubmissions(contestId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/contests/{contestId}/users/{userId}/roles")
    public ResponseEntity<?> getRolesUserNotApprovedInContest(
        @PathVariable String userId,
        @PathVariable String contestId
    ) {
        ModelGetRolesOfUserInContestResponse res = problemTestCaseService.getRolesOfUserInContest(userId, contestId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/contests/permissions")
    public ResponseEntity<?> getPermissionsOfMemberOfContest() {
        List<String> perms = UserRegistrationContestEntity.getListPermissions();
        return ResponseEntity.ok().body(perms);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/contests/switch-judge-mode")
    public ResponseEntity<?> switchAllContestJudgeMode(@RequestParam("mode") String judgeMode) {

        problemTestCaseService.switchAllContestJudgeMode(judgeMode);

        return ResponseEntity.status(200).body("ok");
    }

    @PostMapping("/contests/students/upload-list")
    public ResponseEntity<?> uploadExcelStudentListOfContest(
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelUploadExcelParticipantToContestInput modelUpload = gson.fromJson(
            inputJson, ModelUploadExcelParticipantToContestInput.class);
        List<ModelAddUserToContestResponse> uploadedUsers = new ArrayList<>();
        String contestId = modelUpload.getContestId();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            //System.out.println("uploadExcelStudentListOfQuizTest, lastRowNum = " + lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(0);

                if (c == null || c.getStringCellValue().equals("")) {
                    continue;
                }
                String userId = c.getStringCellValue();
                ModelAddUserToContest m = new ModelAddUserToContest();
                m.setContestId(contestId);
                m.setUserId(userId);
                m.setRole(UserRegistrationContestEntity.ROLE_PARTICIPANT);
                ModelAddUserToContestResponse response = problemTestCaseService.addUserToContest(m);
                uploadedUsers.add(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(uploadedUsers);
    }

    @PostMapping("/contests/students/upload-group-list")
    public ResponseEntity<?> uploadExcelStudentGroupListOfContest(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        String userLoginId = principal.getName();
        Gson gson = new Gson();
        ModelUploadExcelParticipantToContestInput modelUpload = gson.fromJson(
            inputJson, ModelUploadExcelParticipantToContestInput.class);
        List<ModelAddUserToContestGroupResponse> uploadedUsers = new ArrayList<>();
        String contestId = modelUpload.getContestId();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            //System.out.println("uploadExcelStudentListOfQuizTest, lastRowNum = " + lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(0);

                if (c == null || c.getStringCellValue().equals("")) {
                    continue;
                }
                String participantId = c.getStringCellValue();
                ModelAddUserToContestGroup m = new ModelAddUserToContestGroup();
                m.setContestId(contestId);
                m.setUserId(userLoginId);
                m.setParticipantId(participantId);

                ModelAddUserToContestGroupResponse response = problemTestCaseService.addUserToContestGroup(m);
                uploadedUsers.add(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(uploadedUsers);
    }

    @GetMapping("/contests/users/{userLoginId}/contest-result")
    public ResponseEntity<?> getContestResultOnProblemOfAUser(
        @PathVariable("userLoginId") String userLoginId
    ) {
        log.info("getContestResultOnProblemOfAUser, user = " + userLoginId);
        List<ContestSubmission> lst = problemTestCaseService.getNewestSubmissionResults(userLoginId);

        return ResponseEntity.status(200).body(lst);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/contest/get-participant-view-submission-modes")
    public ResponseEntity<?> getParticipantViewSubmissionModes() {
        List<String> res = ContestEntity.getListParticipantViewSubmissionModes();
        return ResponseEntity.ok().body(res);
    }
}
