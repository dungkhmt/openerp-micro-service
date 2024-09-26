package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.entity.QuizGroupQuestionAssignment;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.AddQuizGroupQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.QuizGroupQuestionDetailOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.RemoveQuizGroupQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestGroupRepo;
import com.hust.baseweb.applications.education.quiztest.repo.QuizGroupQuestionAssignmentRepo;
import com.hust.baseweb.applications.education.repo.QuizQuestionRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class QuizGroupQuestionAssignmentServiceImpl implements QuizGroupQuestionAssignmentService {

    private QuizGroupQuestionAssignmentRepo quizGroupQuestionAssignmentRepo;
    private EduQuizTestGroupRepo eduQuizTestGroupRepo;
    private QuizQuestionRepo quizQuestionRepo;

    @Override
    public List<QuizGroupQuestionDetailOutputModel> findAllQuizGroupQuestionAssignmentOfTest(String testId) {
        List<EduTestQuizGroup> eduQuizTestGroups = eduQuizTestGroupRepo.findByTestId(testId);
        // TO BE IMPROVED
        List<QuizQuestion> quizQuestions = quizQuestionRepo.findAll();
        HashMap<UUID, QuizQuestion> mId2QuizQuestion = new HashMap();
        for (QuizQuestion q : quizQuestions) {
            mId2QuizQuestion.put(q.getQuestionId(), q);
        }

        HashMap<UUID, EduTestQuizGroup> mId2QuizTestGroup = new HashMap();
        for (EduTestQuizGroup g : eduQuizTestGroups) {
            mId2QuizTestGroup.put(g.getQuizGroupId(), g);
        }

        log.info("findAllQuizGroupQuestionAssignmentOfTest, groups.sz = " + eduQuizTestGroups.size());
        List<UUID> quizGroupIds = eduQuizTestGroups
            .stream()
            .map(eduQuizTestGroup -> eduQuizTestGroup.getQuizGroupId())
            .collect(
                Collectors.toList());
        for (UUID id : quizGroupIds) {
            log.info("findAllQuizGroupQuestionAssignmentOfTest, groupId " + id);
        }
        List<QuizGroupQuestionAssignment> quizGroupQuestionAssignment = quizGroupQuestionAssignmentRepo.findAllByQuizGroupIdIn(
            quizGroupIds);
        log.info("findAllQuizGroupQuestionAssignmentOfTest, return sz = " + quizGroupQuestionAssignment.size());
        List<QuizGroupQuestionDetailOutputModel> quizGroupQuestionDetailOutputModels = new ArrayList();
        for (QuizGroupQuestionAssignment qgq : quizGroupQuestionAssignment) {
            QuizQuestion q = mId2QuizQuestion.get(qgq.getQuestionId());
            EduTestQuizGroup g = mId2QuizTestGroup.get(qgq.getQuizGroupId());
            QuizGroupQuestionDetailOutputModel quizGroupQuestionDetailOutputModel = new QuizGroupQuestionDetailOutputModel();
            quizGroupQuestionDetailOutputModel.setGroupCode(g.getGroupCode());
            quizGroupQuestionDetailOutputModel.setQuestionId(qgq.getQuestionId());
            quizGroupQuestionDetailOutputModel.setQuizGroupId(qgq.getQuizGroupId());
            quizGroupQuestionDetailOutputModel.setQuestionStatement(q.getQuestionContent());
            quizGroupQuestionDetailOutputModels.add(quizGroupQuestionDetailOutputModel);
        }
        Collections.sort(quizGroupQuestionDetailOutputModels, new Comparator<QuizGroupQuestionDetailOutputModel>() {
            @Override
            public int compare(QuizGroupQuestionDetailOutputModel o1, QuizGroupQuestionDetailOutputModel o2) {
                return o1.getGroupCode().compareTo(o2.getGroupCode());
            }
        });
        return quizGroupQuestionDetailOutputModels;
    }

    @Transactional
    @Override
    public boolean removeQuizGroupQuestionAssignment(RemoveQuizGroupQuestionInputModel input) {
        log.info("removeQuizGroupQuestionAssignment, quizGroup " +
                 input.getQuizGroupId() +
                 " questionId " +
                 input.getQuestionId());
        QuizGroupQuestionAssignment gq = quizGroupQuestionAssignmentRepo.findByQuestionIdAndQuizGroupId(
            input.getQuestionId(),
            input.getQuizGroupId());
        if (gq == null) {
            log.info("removeQuizGroupQuestionAssignment, quizGroup " +
                     input.getQuizGroupId() +
                     " questionId " +
                     input.getQuestionId()
                     +
                     " NOT EXIST!!");
            return false;
        }
        quizGroupQuestionAssignmentRepo.delete(gq);
        return true;
    }

    @Override
    public QuizGroupQuestionAssignment addQuizGroupQuestionAssignment(AddQuizGroupQuestionInputModel input) {
        log.info("addQuizGroupQuestionAssignment, quizGroup " +
                 input.getQuizGroupId() +
                 " questionId " +
                 input.getQuestionId());
        QuizGroupQuestionAssignment gq = quizGroupQuestionAssignmentRepo.findByQuestionIdAndQuizGroupId(
            input.getQuestionId(),
            input.getQuizGroupId());
        if (gq != null) {
            log.info("addQuizGroupQuestionAssignment, quizGroup " +
                     input.getQuizGroupId() +
                     " questionId " +
                     input.getQuestionId()
                     +
                     " ALREADY EXIST!!");

            return gq;
        }
        gq = new QuizGroupQuestionAssignment();
        gq.setQuestionId(input.getQuestionId());
        gq.setQuizGroupId(input.getQuizGroupId());
        gq = quizGroupQuestionAssignmentRepo.save(gq);
        return gq;
    }
}
