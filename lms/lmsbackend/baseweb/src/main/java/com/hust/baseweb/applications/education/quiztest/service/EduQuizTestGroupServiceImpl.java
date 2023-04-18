package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.entity.QuizChoiceAnswer;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.*;
import com.hust.baseweb.applications.education.quiztest.model.QuizChoiceAnswerHideCorrectAnswer;
import com.hust.baseweb.applications.education.quiztest.model.QuizGroupTestDetailModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.GenerateQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.*;
import com.hust.baseweb.applications.education.quiztest.utils.Utils;
import com.hust.baseweb.applications.education.repo.EduCourseRepo;
import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.service.QuizQuestionService;
import com.hust.baseweb.service.UserService;
import com.hust.baseweb.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EduQuizTestGroupServiceImpl implements EduQuizTestGroupService {

    private EduQuizTestGroupRepo eduQuizTestGroupRepo;
    private UserService userService;
    private QuizQuestionService quizQuestionService;
    private EduQuizTestRepo eduQuizTestRepo;
    private EduCourseRepo eduCourseRepo;
    private QuizGroupQuestionAssignmentRepo quizGroupQuestionAssignmentRepo;
    private EduTestQuizGroupParticipationAssignmentRepo eduTestQuizGroupParticipationAssignmentRepo;
    private QuizGroupQuestionParticipationExecutionChoiceRepo quizGroupQuestionParticipationExecutionChoiceRepo;
    private EduTestQuizParticipantService eduTestQuizParticipantService;
    private QuizTestExecutionSubmissionRepo quizTestExecutionSubmissionRepo;

    private static Random R = new Random();

    @Override
    public List<EduTestQuizGroup> generateQuizTestGroups(GenerateQuizTestGroupInputModel input) {
        List<EduTestQuizGroup> eduTestQuizGroups = eduQuizTestGroupRepo.findByTestId(input.getQuizTestId());
        List<EduTestQuizGroup> eduTestQuizGroupList = new ArrayList<EduTestQuizGroup>();
        String[] codes = new String[eduTestQuizGroups.size()];
        for (int i = 0; i < eduTestQuizGroups.size(); i++) {
            codes[i] = eduTestQuizGroups.get(i).getGroupCode();
        }
        String[] newCodes = CommonUtils.generateNextSeqId(codes, input.getNumberOfQuizTestGroups());
        log.info("generateQuizTestGroups, number of groups = " + input.getNumberOfQuizTestGroups() + " newCodes.length = "+ newCodes.length);
        for (int i = 0; i < newCodes.length; i++) {
            log.info("generateQuizTestGroups, gen newCode " + newCodes[i]);
            EduTestQuizGroup eduTestQuizGroup = new EduTestQuizGroup();
            eduTestQuizGroup.setTestId(input.getQuizTestId());
            eduTestQuizGroup.setGroupCode(newCodes[i]);

            eduTestQuizGroup = eduQuizTestGroupRepo.save(eduTestQuizGroup);

            eduTestQuizGroupList.add(eduTestQuizGroup);
        }
        return eduTestQuizGroupList;
    }

    public QuizGroupTestDetailModel getTestGroupQuestionDetail(Principal principal, String testID) {
        EduTestQuizParticipant participant = eduTestQuizParticipantService
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(principal.getName(), testID);


        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();
        List<QuizQuestionDetailModel> listQuestions = new ArrayList<>();

        QuizGroupTestDetailModel testDetail = new QuizGroupTestDetailModel();
        testDetail.setTestId(testID);
        testDetail.setTestName(test.getTestName());
        testDetail.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        testDetail.setScheduleDatetime(strDate);
        testDetail.setCourseName(courseName);
        testDetail.setViewTypeId(test.getViewTypeId());

        List<EduTestQuizGroupParticipationAssignment> listGroupAsignment = eduTestQuizGroupParticipationAssignmentRepo.findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(
            principal.getName());
        //System.out.println(listGroupAsignment.size());
        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        for (EduTestQuizGroupParticipationAssignment ele : listGroupAsignment) {
            eduTestQuizGroup = eduQuizTestGroupRepo.findEduTestQuizGroupByTestIdAndQuizGroupId(
                testID,
                ele.getQuizGroupId());
            if (eduTestQuizGroup != null) {
                break;
            }
        }
        if (eduTestQuizGroup == null) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(null);
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(groupId.toString());
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        String permutation = "0123456789";
        if(participant != null){
            if(participant.getPermutation() != null && !participant.getPermutation().equals(""))
                permutation = participant.getPermutation();
        }
        /*
        ArrayList<Integer> indices = new ArrayList();
        int m = 0;
        for(int i = 0; i < permutation.length(); i++) {
            int idx =Integer.valueOf(permutation.charAt(i));
            indices.add(idx);
            m = Math.max(idx,m);
        }
        */
        int len = tmpl.size();
        //log.info("getTestGroupQuestionDetail, permutation = " + permutation + " len = tmpl.sz = " + len);
        for(QuizGroupQuestionAssignment asign: tmpl){
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            if(len < quizQuestion.getQuizChoiceAnswerList().size()){
                len  = quizQuestion.getQuizChoiceAnswerList().size();
            }
        }
        if(len < permutation.length()) len = permutation.length();

        // randome indices sequence based on permutation
        int[] indices = Utils.genSequence(permutation,len);
        //log.info("getTestGroupQuestionDetail, update len = " + len + " indices = " + indices.toString());

        //log.info("getTestGroupQuestionDetail, random indices = ");
        //for(int i = 0; i < indices.length; i++) log.info(indices[i] + " ");

        tmpl.forEach(asign -> {
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            //System.out.println("ok ");

            // random order choices based on permutation
            //List<QuizChoiceAnswer> answers = new ArrayList();
            List<QuizChoiceAnswerHideCorrectAnswer> answers = new ArrayList();
            for(int i = 0;i < indices.length; i++){
                if(indices[i] >= quizQuestion.getQuizChoiceAnswerList().size()){
                    //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + indices[i] + " > answers.size -> continue");
                    continue;
                }
                answers.add(quizQuestion.getQuizChoiceAnswerList().get(indices[i]));
                //QuizChoiceAnswer ans = quizQuestion.getQuizChoiceAnswerList().get(indices[i]);


                if(answers.size() == quizQuestion.getQuizChoiceAnswerList().size()) break;
            }
            //for(int i = m+1; i < quizQuestion.getQuizChoiceAnswerList().size();i++){
            //    answers.add(quizQuestion.getQuizChoiceAnswerList().get(i));
            //}
            quizQuestion.setQuizChoiceAnswerList(answers);

            listQuestions.add(quizQuestion);
        });


        List<QuizGroupQuestionParticipationExecutionChoice> listChoice = quizGroupQuestionParticipationExecutionChoiceRepo
            .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupId(
                principal.getName(), groupId);
        Map<String, List<UUID>> participationExecutionChoice = new HashMap<>();
        listChoice.forEach(ele -> {
            String quesId = ele.getQuestionId().toString();
            if (participationExecutionChoice.containsKey(quesId)) {
                participationExecutionChoice.get(quesId).add(ele.getChoiceAnswerId());
            } else {
                List<UUID> tmp = new ArrayList<>();
                tmp.add(ele.getChoiceAnswerId());
                participationExecutionChoice.put(quesId, tmp);
            }
        });


        ArrayList<QuizQuestionDetailModel> sorted_lst = new ArrayList();
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            if(idx >= listQuestions.size()){
                //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + idx + " > listQuesions.sz -> continue");
                continue;
            }
            sorted_lst.add(listQuestions.get(idx));
            //log.info("getTestGroupQuestionDetail ADD sorted_lst.sz = " + sorted_lst.size() + "/" + listQuestions.size() );
        }

        /*
        // swap randomly listQUestions
        QuizQuestionDetailModel[] arr = new QuizQuestionDetailModel[listQuestions.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = listQuestions.get(i);
        }
        for (int k = 0; k < arr.length; k++) {
            int i = R.nextInt(arr.length);
            int j = R.nextInt(arr.length);
            QuizQuestionDetailModel tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        listQuestions.clear();
        for (int i = 0; i < arr.length; i++) {
            listQuestions.add(arr[i]);
            for (QuizChoiceAnswer a : arr[i].getQuizChoiceAnswerList()) {
                a.setIsCorrectAnswer('-');
            }
        }
        */


        //testDetail.setListQuestion(listQuestions);
        testDetail.setListQuestion(sorted_lst);

        testDetail.setQuizGroupId(groupId.toString());
        testDetail.setGroupCode(groupCode);
        testDetail.setParticipationExecutionChoice(participationExecutionChoice);
        return testDetail;
    }
    private Map<String, List<UUID>> getChoiceAnswers(List<QuizGroupQuestionAssignment> questionAssignment, String userId, UUID groupId){
        Map<String, List<UUID>> participationExecutionChoice = new HashMap<>();
        for(QuizGroupQuestionAssignment asign: questionAssignment) {
            //System.out.println("here ");
            //QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            UUID questionId = asign.getQuestionId();
            List<QuizTestExecutionSubmission> choices = quizTestExecutionSubmissionRepo
            .findAllByQuestionIdAndQuizGroupIdAndParticipationUserLoginIdOrderByCreatedStampDesc(questionId, groupId, userId);
            if(choices != null && choices.size() > 0){
                QuizTestExecutionSubmission mostRecentlyChoice = choices.get(0);
                String[] listChoices = mostRecentlyChoice.getChoiceAnswerIds().split(",");
                if(listChoices != null && listChoices.length > 0){
                    for(int i = 0; i < listChoices.length; i++){
                        log.debug("getChoiceAnswers, listChoices[" + i + "] = " + listChoices[i]);
                        if(listChoices[i] != null && !listChoices[i].equals("")) {
                            UUID choiceId = UUID.fromString(listChoices[i].trim());
                            if (participationExecutionChoice.containsKey(questionId.toString())) {
                                participationExecutionChoice.get(questionId.toString()).add(choiceId);
                            } else {
                                List<UUID> tmp = new ArrayList<>();
                                tmp.add(choiceId);
                                participationExecutionChoice.put(questionId.toString(), tmp);
                            }
                        }
                }
            }
        }
    }
    return participationExecutionChoice;
    }
    @Override
    public QuizGroupTestDetailModel getTestGroupQuestionDetailHeavyReload(Principal principal, String testID) {
        EduTestQuizParticipant participant = eduTestQuizParticipantService
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(principal.getName(), testID);


        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();
        List<QuizQuestionDetailModel> listQuestions = new ArrayList<>();
        String userId = principal.getName();

        QuizGroupTestDetailModel testDetail = new QuizGroupTestDetailModel();
        testDetail.setTestId(testID);
        testDetail.setTestName(test.getTestName());
        testDetail.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        testDetail.setScheduleDatetime(strDate);
        testDetail.setCourseName(courseName);
        testDetail.setViewTypeId(test.getViewTypeId());

        List<EduTestQuizGroupParticipationAssignment> listGroupAsignment = eduTestQuizGroupParticipationAssignmentRepo.findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(
            principal.getName());
        //System.out.println(listGroupAsignment.size());
        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        for (EduTestQuizGroupParticipationAssignment ele : listGroupAsignment) {
            eduTestQuizGroup = eduQuizTestGroupRepo.findEduTestQuizGroupByTestIdAndQuizGroupId(
                testID,
                ele.getQuizGroupId());
            if (eduTestQuizGroup != null) {
                break;
            }
        }
        if (eduTestQuizGroup == null) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(null);
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(groupId.toString());
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        String permutation = "0123456789";
        if(participant != null){
            if(participant.getPermutation() != null && !participant.getPermutation().equals(""))
                permutation = participant.getPermutation();
        }
        /*
        ArrayList<Integer> indices = new ArrayList();
        int m = 0;
        for(int i = 0; i < permutation.length(); i++) {
            int idx =Integer.valueOf(permutation.charAt(i));
            indices.add(idx);
            m = Math.max(idx,m);
        }
        */
        int len = tmpl.size();
        //log.info("getTestGroupQuestionDetail, permutation = " + permutation + " len = tmpl.sz = " + len);
        for(QuizGroupQuestionAssignment asign: tmpl){
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            if(len < quizQuestion.getQuizChoiceAnswerList().size()){
                len  = quizQuestion.getQuizChoiceAnswerList().size();
            }
        }
        if(len < permutation.length()) len = permutation.length();

        // randome indices sequence based on permutation
        int[] indices = Utils.genSequence(permutation,len);
        //log.info("getTestGroupQuestionDetail, update len = " + len + " indices = " + indices.toString());

        //log.info("getTestGroupQuestionDetail, random indices = ");
        //for(int i = 0; i < indices.length; i++) log.info(indices[i] + " ");

        tmpl.forEach(asign -> {
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            //System.out.println("ok ");

            // random order choices based on permutation
            //List<QuizChoiceAnswer> answers = new ArrayList();
            List<QuizChoiceAnswerHideCorrectAnswer> answers = new ArrayList();
            for(int i = 0;i < indices.length; i++){
                if(indices[i] >= quizQuestion.getQuizChoiceAnswerList().size()){
                    //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + indices[i] + " > answers.size -> continue");
                    continue;
                }
                answers.add(quizQuestion.getQuizChoiceAnswerList().get(indices[i]));
                //QuizChoiceAnswer ans = quizQuestion.getQuizChoiceAnswerList().get(indices[i]);


                if(answers.size() == quizQuestion.getQuizChoiceAnswerList().size()) break;
            }
            //for(int i = m+1; i < quizQuestion.getQuizChoiceAnswerList().size();i++){
            //    answers.add(quizQuestion.getQuizChoiceAnswerList().get(i));
            //}
            quizQuestion.setQuizChoiceAnswerList(answers);

            listQuestions.add(quizQuestion);
        });


        /*
        List<QuizGroupQuestionParticipationExecutionChoice> listChoice = quizGroupQuestionParticipationExecutionChoiceRepo
            .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupId(
                principal.getName(), groupId);
        Map<String, List<UUID>> participationExecutionChoice = new HashMap<>();
        listChoice.forEach(ele -> {
            String quesId = ele.getQuestionId().toString();
            if (participationExecutionChoice.containsKey(quesId)) {
                participationExecutionChoice.get(quesId).add(ele.getChoiceAnswerId());
            } else {
                List<UUID> tmp = new ArrayList<>();
                tmp.add(ele.getChoiceAnswerId());
                participationExecutionChoice.put(quesId, tmp);
            }
        });
        */

        Map<String, List<UUID>> participationExecutionChoice = getChoiceAnswers(tmpl,userId, groupId);


        ArrayList<QuizQuestionDetailModel> sorted_lst = new ArrayList();
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            if(idx >= listQuestions.size()){
                //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + idx + " > listQuesions.sz -> continue");
                continue;
            }
            sorted_lst.add(listQuestions.get(idx));
            //log.info("getTestGroupQuestionDetail ADD sorted_lst.sz = " + sorted_lst.size() + "/" + listQuestions.size() );
        }

        /*
        // swap randomly listQUestions
        QuizQuestionDetailModel[] arr = new QuizQuestionDetailModel[listQuestions.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = listQuestions.get(i);
        }
        for (int k = 0; k < arr.length; k++) {
            int i = R.nextInt(arr.length);
            int j = R.nextInt(arr.length);
            QuizQuestionDetailModel tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        listQuestions.clear();
        for (int i = 0; i < arr.length; i++) {
            listQuestions.add(arr[i]);
            for (QuizChoiceAnswer a : arr[i].getQuizChoiceAnswerList()) {
                a.setIsCorrectAnswer('-');
            }
        }
        */


        //testDetail.setListQuestion(listQuestions);
        testDetail.setListQuestion(sorted_lst);

        testDetail.setQuizGroupId(groupId.toString());
        testDetail.setGroupCode(groupCode);
        testDetail.setParticipationExecutionChoice(participationExecutionChoice);
        return testDetail;
    }

    public QuizGroupTestDetailModel getTestGroupQuestionDetail(String userLoginId, String testID) {
        EduTestQuizParticipant participant = eduTestQuizParticipantService
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(userLoginId, testID);


        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();
        List<QuizQuestionDetailModel> listQuestions = new ArrayList<>();

        QuizGroupTestDetailModel testDetail = new QuizGroupTestDetailModel();
        testDetail.setTestId(testID);
        testDetail.setTestName(test.getTestName());
        testDetail.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        testDetail.setScheduleDatetime(strDate);
        testDetail.setCourseName(courseName);
        testDetail.setViewTypeId(test.getViewTypeId());

        List<EduTestQuizGroupParticipationAssignment> listGroupAsignment = eduTestQuizGroupParticipationAssignmentRepo.findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(
            userLoginId);
        //System.out.println(listGroupAsignment.size());
        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        for (EduTestQuizGroupParticipationAssignment ele : listGroupAsignment) {
            eduTestQuizGroup = eduQuizTestGroupRepo.findEduTestQuizGroupByTestIdAndQuizGroupId(
                testID,
                ele.getQuizGroupId());
            if (eduTestQuizGroup != null) {
                break;
            }
        }
        if (eduTestQuizGroup == null) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(null);
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(groupId.toString());
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        String permutation = "0123456789";
        if(participant != null){
            if(participant.getPermutation() != null && !participant.getPermutation().equals(""))
                permutation = participant.getPermutation();
        }
        /*
        ArrayList<Integer> indices = new ArrayList();
        int m = 0;
        for(int i = 0; i < permutation.length(); i++) {
            int idx =Integer.valueOf(permutation.charAt(i));
            indices.add(idx);
            m = Math.max(idx,m);
        }
        */
        int len = tmpl.size();
        //log.info("getTestGroupQuestionDetail, permutation = " + permutation + " len = tmpl.sz = " + len);
        for(QuizGroupQuestionAssignment asign: tmpl){
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            if(len < quizQuestion.getQuizChoiceAnswerList().size()){
                len  = quizQuestion.getQuizChoiceAnswerList().size();
            }
        }
        if(len < permutation.length()) len = permutation.length();

        // randome indices sequence based on permutation
        int[] indices = Utils.genSequence(permutation,len);
        //log.info("getTestGroupQuestionDetail, update len = " + len + " indices = " + indices.toString());

        //log.info("getTestGroupQuestionDetail, random indices = ");
        //for(int i = 0; i < indices.length; i++) log.info(indices[i] + " ");

        tmpl.forEach(asign -> {
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            //System.out.println("ok ");

            // random order choices based on permutation
            //List<QuizChoiceAnswer> answers = new ArrayList();
            List<QuizChoiceAnswerHideCorrectAnswer> answers = new ArrayList();
            for(int i = 0;i < indices.length; i++){
                if(indices[i] >= quizQuestion.getQuizChoiceAnswerList().size()){
                    //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + indices[i] + " > answers.size -> continue");
                    continue;
                }
                answers.add(quizQuestion.getQuizChoiceAnswerList().get(indices[i]));
                //QuizChoiceAnswer ans = quizQuestion.getQuizChoiceAnswerList().get(indices[i]);


                if(answers.size() == quizQuestion.getQuizChoiceAnswerList().size()) break;
            }
            //for(int i = m+1; i < quizQuestion.getQuizChoiceAnswerList().size();i++){
            //    answers.add(quizQuestion.getQuizChoiceAnswerList().get(i));
            //}
            quizQuestion.setQuizChoiceAnswerList(answers);

            listQuestions.add(quizQuestion);
        });


        List<QuizGroupQuestionParticipationExecutionChoice> listChoice = quizGroupQuestionParticipationExecutionChoiceRepo
            .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupId(
                userLoginId, groupId);
        Map<String, List<UUID>> participationExecutionChoice = new HashMap<>();
        listChoice.forEach(ele -> {
            String quesId = ele.getQuestionId().toString();
            if (participationExecutionChoice.containsKey(quesId)) {
                participationExecutionChoice.get(quesId).add(ele.getChoiceAnswerId());
            } else {
                List<UUID> tmp = new ArrayList<>();
                tmp.add(ele.getChoiceAnswerId());
                participationExecutionChoice.put(quesId, tmp);
            }
        });

        ArrayList<QuizQuestionDetailModel> sorted_lst = new ArrayList();
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            if(idx >= listQuestions.size()){
                //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + idx + " > listQuesions.sz -> continue");
                continue;
            }
            sorted_lst.add(listQuestions.get(idx));
            //log.info("getTestGroupQuestionDetail ADD sorted_lst.sz = " + sorted_lst.size() + "/" + listQuestions.size() );
        }

        /*
        // swap randomly listQUestions
        QuizQuestionDetailModel[] arr = new QuizQuestionDetailModel[listQuestions.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = listQuestions.get(i);
        }
        for (int k = 0; k < arr.length; k++) {
            int i = R.nextInt(arr.length);
            int j = R.nextInt(arr.length);
            QuizQuestionDetailModel tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        listQuestions.clear();
        for (int i = 0; i < arr.length; i++) {
            listQuestions.add(arr[i]);
            for (QuizChoiceAnswer a : arr[i].getQuizChoiceAnswerList()) {
                a.setIsCorrectAnswer('-');
            }
        }
        */


        //testDetail.setListQuestion(listQuestions);
        testDetail.setListQuestion(sorted_lst);

        testDetail.setQuizGroupId(groupId.toString());
        testDetail.setGroupCode(groupCode);
        testDetail.setParticipationExecutionChoice(participationExecutionChoice);
        return testDetail;
    }


    @Override
    public QuizGroupTestDetailModel getTestGroupQuestionDetailOfGroupCode(String userLoginId, String groupCode, String testID) {
        EduTestQuizParticipant participant = eduTestQuizParticipantService
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(userLoginId, testID);


        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();
        List<QuizQuestionDetailModel> listQuestions = new ArrayList<>();

        QuizGroupTestDetailModel testDetail = new QuizGroupTestDetailModel();
        testDetail.setTestId(testID);
        testDetail.setTestName(test.getTestName());
        testDetail.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        testDetail.setScheduleDatetime(strDate);
        testDetail.setCourseName(courseName);
        testDetail.setViewTypeId(test.getViewTypeId());

        //List<EduTestQuizGroupParticipationAssignment> listGroupAsignment = eduTestQuizGroupParticipationAssignmentRepo.findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(
        //    userLoginId);
        //System.out.println(listGroupAsignment.size());
        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findAllByTestIdAndGroupCode(testID, groupCode);
        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        if(groups != null && groups.size() > 0){
            eduTestQuizGroup = groups.get(0);
        }
        if (eduTestQuizGroup == null) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(null);
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        //String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            testDetail.setListQuestion(null);
            testDetail.setQuizGroupId(groupId.toString());
            testDetail.setGroupCode(null);
            testDetail.setParticipationExecutionChoice(null);
            return testDetail;
        }
        String permutation = "0123456789";
        if(participant != null){
            if(participant.getPermutation() != null && !participant.getPermutation().equals(""))
                permutation = participant.getPermutation();
        }
        /*
        ArrayList<Integer> indices = new ArrayList();
        int m = 0;
        for(int i = 0; i < permutation.length(); i++) {
            int idx =Integer.valueOf(permutation.charAt(i));
            indices.add(idx);
            m = Math.max(idx,m);
        }
        */
        int len = tmpl.size();
        //log.info("getTestGroupQuestionDetail, permutation = " + permutation + " len = tmpl.sz = " + len);
        for(QuizGroupQuestionAssignment asign: tmpl){
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            if(len < quizQuestion.getQuizChoiceAnswerList().size()){
                len  = quizQuestion.getQuizChoiceAnswerList().size();
            }
        }
        if(len < permutation.length()) len = permutation.length();

        // randome indices sequence based on permutation
        int[] indices = Utils.genSequence(permutation,len);
        //log.info("getTestGroupQuestionDetail, update len = " + len + " indices = " + indices.toString());

        //log.info("getTestGroupQuestionDetail, random indices = ");
        //for(int i = 0; i < indices.length; i++) log.info(indices[i] + " ");

        tmpl.forEach(asign -> {
            //System.out.println("here ");
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            //System.out.println("ok ");

            // random order choices based on permutation
            //List<QuizChoiceAnswer> answers = new ArrayList();
            List<QuizChoiceAnswerHideCorrectAnswer> answers = new ArrayList();
            for(int i = 0;i < indices.length; i++){
                if(indices[i] >= quizQuestion.getQuizChoiceAnswerList().size()){
                    //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + indices[i] + " > answers.size -> continue");
                    continue;
                }
                answers.add(quizQuestion.getQuizChoiceAnswerList().get(indices[i]));
                //QuizChoiceAnswer ans = quizQuestion.getQuizChoiceAnswerList().get(indices[i]);


                if(answers.size() == quizQuestion.getQuizChoiceAnswerList().size()) break;
            }
            //for(int i = m+1; i < quizQuestion.getQuizChoiceAnswerList().size();i++){
            //    answers.add(quizQuestion.getQuizChoiceAnswerList().get(i));
            //}
            quizQuestion.setQuizChoiceAnswerList(answers);

            listQuestions.add(quizQuestion);
        });


        List<QuizGroupQuestionParticipationExecutionChoice> listChoice = quizGroupQuestionParticipationExecutionChoiceRepo
            .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupId(
                userLoginId, groupId);
        Map<String, List<UUID>> participationExecutionChoice = new HashMap<>();
        listChoice.forEach(ele -> {
            String quesId = ele.getQuestionId().toString();
            if (participationExecutionChoice.containsKey(quesId)) {
                participationExecutionChoice.get(quesId).add(ele.getChoiceAnswerId());
            } else {
                List<UUID> tmp = new ArrayList<>();
                tmp.add(ele.getChoiceAnswerId());
                participationExecutionChoice.put(quesId, tmp);
            }
        });

        ArrayList<QuizQuestionDetailModel> sorted_lst = new ArrayList();
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            if(idx >= listQuestions.size()){
                //log.info("getTestGroupQuestionDetail, indices[" + i + "] = " + idx + " > listQuesions.sz -> continue");
                continue;
            }
            sorted_lst.add(listQuestions.get(idx));
            //log.info("getTestGroupQuestionDetail ADD sorted_lst.sz = " + sorted_lst.size() + "/" + listQuestions.size() );
        }

        /*
        // swap randomly listQUestions
        QuizQuestionDetailModel[] arr = new QuizQuestionDetailModel[listQuestions.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = listQuestions.get(i);
        }
        for (int k = 0; k < arr.length; k++) {
            int i = R.nextInt(arr.length);
            int j = R.nextInt(arr.length);
            QuizQuestionDetailModel tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        listQuestions.clear();
        for (int i = 0; i < arr.length; i++) {
            listQuestions.add(arr[i]);
            for (QuizChoiceAnswer a : arr[i].getQuizChoiceAnswerList()) {
                a.setIsCorrectAnswer('-');
            }
        }
        */


        //testDetail.setListQuestion(listQuestions);
        testDetail.setListQuestion(sorted_lst);

        testDetail.setQuizGroupId(groupId.toString());
        testDetail.setGroupCode(groupCode);
        testDetail.setParticipationExecutionChoice(participationExecutionChoice);
        return testDetail;
    }

    public List<QuizGroupTestDetailModel> getQuizTestGroupWithQuestionsDetail(String testId){
        EduQuizTest test = eduQuizTestRepo.findById(testId).get();
        String courseName = "";
        EduCourse course = eduCourseRepo.findById(test.getCourseId()).orElse(null);
        if(course != null) courseName = course.getName();

        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findByTestId(testId);

        List<QuizGroupTestDetailModel> res = new ArrayList();
        for(EduTestQuizGroup g: groups){
            // get list of questions of this group g
            QuizGroupTestDetailModel quizGroupTestDetailModel = new QuizGroupTestDetailModel();
            List<QuizGroupQuestionAssignment> questions = quizGroupQuestionAssignmentRepo
            .findQuizGroupQuestionAssignmentsByQuizGroupId(g.getQuizGroupId());
            List<QuizQuestionDetailModel> listQuestions = new ArrayList();
            for(QuizGroupQuestionAssignment asign: questions) {
                QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
                int seq = asign.getSeq();
                List<QuizChoiceAnswerHideCorrectAnswer> choices = quizQuestion.getQuizChoiceAnswerList();
                // order the choiceAnswers based on seq
                List<QuizChoiceAnswerHideCorrectAnswer> choiceAnswers = new ArrayList();
                int[] idx = Utils.getPermutationBasedOnSeq(seq,choices.size());
                if(idx != null) {
                    for(int i = 0; i < choices.size(); i++) {
                        choiceAnswers.add(quizQuestion.getQuizChoiceAnswerList().get(idx[i]));
                    }
                }
                quizQuestion.setQuizChoiceAnswerList(choiceAnswers);

                listQuestions.add(quizQuestion);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = formatter.format(test.getScheduleDatetime());
            quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
            quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
            quizGroupTestDetailModel.setScheduleDatetime(strDate);
            quizGroupTestDetailModel.setDuration(test.getDuration());
            quizGroupTestDetailModel.setCourseName(courseName);
            quizGroupTestDetailModel.setTestName(test.getTestName());
            quizGroupTestDetailModel.setTestId(testId);
            quizGroupTestDetailModel.setGroupCode(g.getGroupCode());
            quizGroupTestDetailModel.setListQuestion(listQuestions);
            res.add(quizGroupTestDetailModel);
        }

        return res;
    }

    @Override
    public QuizGroupTestDetailModel getTestGroupQuestionDetailNotUsePermutationConfig(String userLoginId, String testID){
        EduTestQuizParticipant participant = eduTestQuizParticipantService
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(userLoginId, testID);

        QuizGroupTestDetailModel quizGroupTestDetailModel = new QuizGroupTestDetailModel();

        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();
        //List<QuizQuestionDetailModel> listQuestions = new ArrayList<>();

        //QuizGroupTestDetailModel testDetail = new QuizGroupTestDetailModel();
        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());

        List<EduTestQuizGroupParticipationAssignment> listGroupAsignment = eduTestQuizGroupParticipationAssignmentRepo
            .findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(userLoginId);
        //System.out.println(listGroupAsignment.size());
        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        for (EduTestQuizGroupParticipationAssignment ele : listGroupAsignment) {
            eduTestQuizGroup = eduQuizTestGroupRepo.findEduTestQuizGroupByTestIdAndQuizGroupId(
                testID,
                ele.getQuizGroupId());
            if (eduTestQuizGroup != null) {
                break;
            }
        }
        if (eduTestQuizGroup == null) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(null);
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }

        List<QuizGroupQuestionAssignment> questions = quizGroupQuestionAssignmentRepo
            .findQuizGroupQuestionAssignmentsByQuizGroupId(groupId);


        List<QuizQuestionDetailModel> listQuestions = new ArrayList();
        for(QuizGroupQuestionAssignment asign: questions) {
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            int seq = asign.getSeq();
            List<QuizChoiceAnswerHideCorrectAnswer> choices = quizQuestion.getQuizChoiceAnswerList();
            // order the choiceAnswers based on seq
            List<QuizChoiceAnswerHideCorrectAnswer> choiceAnswers = new ArrayList();
            int[] idx = Utils.getPermutationBasedOnSeq(seq,choices.size());
            if(idx != null) {
                for(int i = 0; i < choices.size(); i++) {
                    choiceAnswers.add(quizQuestion.getQuizChoiceAnswerList().get(idx[i]));
                }
            }
            quizQuestion.setQuizChoiceAnswerList(choiceAnswers);

            listQuestions.add(quizQuestion);
        }

        List<QuizGroupQuestionParticipationExecutionChoice> listChoice = quizGroupQuestionParticipationExecutionChoiceRepo
            .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupId(
                userLoginId, groupId);
        Map<String, List<UUID>> participationExecutionChoice = new HashMap<>();
        listChoice.forEach(ele -> {
            String quesId = ele.getQuestionId().toString();
            if (participationExecutionChoice.containsKey(quesId)) {
                participationExecutionChoice.get(quesId).add(ele.getChoiceAnswerId());
            } else {
                List<UUID> tmp = new ArrayList<>();
                tmp.add(ele.getChoiceAnswerId());
                participationExecutionChoice.put(quesId, tmp);
            }
        });

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setDuration(test.getDuration());
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setGroupCode(groupCode);
        quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
        quizGroupTestDetailModel.setListQuestion(listQuestions);
        quizGroupTestDetailModel.setParticipationExecutionChoice(participationExecutionChoice);
        return quizGroupTestDetailModel;
    }

    @Override
    public QuizGroupTestDetailModel getTestGroupQuestionDetailNotUsePermutationConfigHeavyReload(
        String userLoginId,
        String testID
    ) {
        EduTestQuizParticipant participant = eduTestQuizParticipantService
            .findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(userLoginId, testID);

        QuizGroupTestDetailModel quizGroupTestDetailModel = new QuizGroupTestDetailModel();

        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();
        //List<QuizQuestionDetailModel> listQuestions = new ArrayList<>();

        //QuizGroupTestDetailModel testDetail = new QuizGroupTestDetailModel();
        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());

        List<EduTestQuizGroupParticipationAssignment> listGroupAsignment = eduTestQuizGroupParticipationAssignmentRepo
            .findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(userLoginId);
        //System.out.println(listGroupAsignment.size());
        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        for (EduTestQuizGroupParticipationAssignment ele : listGroupAsignment) {
            eduTestQuizGroup = eduQuizTestGroupRepo.findEduTestQuizGroupByTestIdAndQuizGroupId(
                testID,
                ele.getQuizGroupId());
            if (eduTestQuizGroup != null) {
                break;
            }
        }
        if (eduTestQuizGroup == null) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(null);
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }

        List<QuizGroupQuestionAssignment> questions = quizGroupQuestionAssignmentRepo
            .findQuizGroupQuestionAssignmentsByQuizGroupId(groupId);


        List<QuizQuestionDetailModel> listQuestions = new ArrayList();
        for(QuizGroupQuestionAssignment asign: questions) {
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            int seq = asign.getSeq();
            List<QuizChoiceAnswerHideCorrectAnswer> choices = quizQuestion.getQuizChoiceAnswerList();
            // order the choiceAnswers based on seq
            List<QuizChoiceAnswerHideCorrectAnswer> choiceAnswers = new ArrayList();
            int[] idx = Utils.getPermutationBasedOnSeq(seq,choices.size());
            if(idx != null) {
                for(int i = 0; i < choices.size(); i++) {
                    choiceAnswers.add(quizQuestion.getQuizChoiceAnswerList().get(idx[i]));
                }
            }
            quizQuestion.setQuizChoiceAnswerList(choiceAnswers);

            listQuestions.add(quizQuestion);
        }

        Map<String, List<UUID>> participationExecutionChoice = getChoiceAnswers(questions,userLoginId, groupId);

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setDuration(test.getDuration());
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setGroupCode(groupCode);
        quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
        quizGroupTestDetailModel.setListQuestion(listQuestions);
        quizGroupTestDetailModel.setParticipationExecutionChoice(participationExecutionChoice);
        return quizGroupTestDetailModel;
    }

    @Override
    public QuizGroupTestDetailModel getQuestionsDetailOfQuizGroup(String groupCode, String testID){
        QuizGroupTestDetailModel quizGroupTestDetailModel = new QuizGroupTestDetailModel();
        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();

        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());

        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findByTestId(testID);

        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        for (EduTestQuizGroup g : groups) {
            if (g.getGroupCode() != null && g.getGroupCode().equals(groupCode)) {
                eduTestQuizGroup = g;
                break;
            }
        }
        if (eduTestQuizGroup == null) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(null);
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        //String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }

        //QuizGroupTestDetailModel quizGroupTestDetailModel = new QuizGroupTestDetailModel();
        List<QuizGroupQuestionAssignment> questions = quizGroupQuestionAssignmentRepo
            .findQuizGroupQuestionAssignmentsByQuizGroupId(groupId);


        List<QuizQuestionDetailModel> listQuestions = new ArrayList();
        for(QuizGroupQuestionAssignment asign: questions) {
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            int seq = asign.getSeq();
            List<QuizChoiceAnswerHideCorrectAnswer> choices = quizQuestion.getQuizChoiceAnswerList();
            // order the choiceAnswers based on seq
            List<QuizChoiceAnswerHideCorrectAnswer> choiceAnswers = new ArrayList();
            int[] idx = Utils.getPermutationBasedOnSeq(seq,choices.size());
            if(idx != null) {
                for(int i = 0; i < choices.size(); i++) {
                    choiceAnswers.add(quizQuestion.getQuizChoiceAnswerList().get(idx[i]));
                }
            }
            quizQuestion.setQuizChoiceAnswerList(choiceAnswers);

            listQuestions.add(quizQuestion);
        }

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setDuration(test.getDuration());
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setGroupCode(groupCode);
        quizGroupTestDetailModel.setListQuestion(listQuestions);
        quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
        return quizGroupTestDetailModel;

    }

    @Override
    public QuizGroupTestDetailModel getQuestionsDetailWithUserExecutionChoideOfQuizGroupNotUsePermutationConfig(
        String userLoginId, String groupCode, String testID){
        QuizGroupTestDetailModel quizGroupTestDetailModel = new QuizGroupTestDetailModel();
        EduQuizTest test = eduQuizTestRepo.findById(testID).get();
        String courseName = eduCourseRepo.findById(test.getCourseId()).get().getName();

        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setDuration(test.getDuration());
        //SimpleDateFormat  formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());

        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findByTestId(testID);

        EduTestQuizGroup eduTestQuizGroup = null;//new EduTestQuizGroup();
        for (EduTestQuizGroup g : groups) {
            if (g.getGroupCode() != null && g.getGroupCode().equals(groupCode)) {
                eduTestQuizGroup = g;
                break;
            }
        }
        if (eduTestQuizGroup == null) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(null);
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }
        UUID groupId = eduTestQuizGroup.getQuizGroupId();
        //String groupCode = eduTestQuizGroup.getGroupCode();
        List<QuizGroupQuestionAssignment> tmpl = quizGroupQuestionAssignmentRepo.findQuizGroupQuestionAssignmentsByQuizGroupId(
            groupId);
        //System.out.println(tmpl.size());
        if (tmpl.size() == 0) {
            quizGroupTestDetailModel.setListQuestion(null);
            quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
            quizGroupTestDetailModel.setGroupCode(null);
            quizGroupTestDetailModel.setParticipationExecutionChoice(null);
            return quizGroupTestDetailModel;
        }

        //QuizGroupTestDetailModel quizGroupTestDetailModel = new QuizGroupTestDetailModel();
        List<QuizGroupQuestionAssignment> questions = quizGroupQuestionAssignmentRepo
            .findQuizGroupQuestionAssignmentsByQuizGroupId(groupId);


        List<QuizQuestionDetailModel> listQuestions = new ArrayList();
        for(QuizGroupQuestionAssignment asign: questions) {
            QuizQuestionDetailModel quizQuestion = quizQuestionService.findQuizDetail(asign.getQuestionId());
            int seq = asign.getSeq();
            List<QuizChoiceAnswerHideCorrectAnswer> choices = quizQuestion.getQuizChoiceAnswerList();
            // order the choiceAnswers based on seq
            List<QuizChoiceAnswerHideCorrectAnswer> choiceAnswers = new ArrayList();
            int[] idx = Utils.getPermutationBasedOnSeq(seq,choices.size());
            if(idx != null) {
                for(int i = 0; i < choices.size(); i++) {
                    choiceAnswers.add(quizQuestion.getQuizChoiceAnswerList().get(idx[i]));
                }
            }
            quizQuestion.setQuizChoiceAnswerList(choiceAnswers);

            listQuestions.add(quizQuestion);
        }
        List<QuizGroupQuestionParticipationExecutionChoice> listChoice = quizGroupQuestionParticipationExecutionChoiceRepo
            .findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupId(
                userLoginId, groupId);
        Map<String, List<UUID>> participationExecutionChoice = new HashMap<>();
        listChoice.forEach(ele -> {
            String quesId = ele.getQuestionId().toString();
            if (participationExecutionChoice.containsKey(quesId)) {
                participationExecutionChoice.get(quesId).add(ele.getChoiceAnswerId());
            } else {
                List<UUID> tmp = new ArrayList<>();
                tmp.add(ele.getChoiceAnswerId());
                participationExecutionChoice.put(quesId, tmp);
            }
        });

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //String strDate = formatter.format(test.getScheduleDatetime());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setViewTypeId(test.getViewTypeId());
        quizGroupTestDetailModel.setScheduleDatetime(strDate);
        quizGroupTestDetailModel.setDuration(test.getDuration());
        quizGroupTestDetailModel.setCourseName(courseName);
        quizGroupTestDetailModel.setTestName(test.getTestName());
        quizGroupTestDetailModel.setTestId(testID);
        quizGroupTestDetailModel.setGroupCode(groupCode);
        quizGroupTestDetailModel.setListQuestion(listQuestions);
        quizGroupTestDetailModel.setQuizGroupId(groupId.toString());
        quizGroupTestDetailModel.setParticipationExecutionChoice(participationExecutionChoice);
        return quizGroupTestDetailModel;

    }

    @Override
    public EduTestQuizGroup getQuizTestGroupFrom(String groupCode, String testId) {
        List<EduTestQuizGroup> groups = eduQuizTestGroupRepo.findAllByTestIdAndGroupCode(testId, groupCode);
        log.debug("getQuizTestGroupFrom, testId = " + testId + " groupCode = " + groupCode + " len = " + groupCode.length() + " ret.sz = " + groups.size());
        if(groups != null && groups.size() > 0){
            return groups.get(0);
        }
        return null;
    }
}
