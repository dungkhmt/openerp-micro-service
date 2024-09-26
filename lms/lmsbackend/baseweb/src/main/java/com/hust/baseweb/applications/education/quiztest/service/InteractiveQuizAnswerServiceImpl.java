package com.hust.baseweb.applications.education.quiztest.service;

import java.util.UUID;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizAnswer;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizAnswerRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizQuestionRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class InteractiveQuizAnswerServiceImpl implements InteractiveQuizAnswerService {
    private InteractiveQuizAnswerRepo interactiveQuizAnswerRepo;
    // private InteractiveQuizQuestionRepo interactiveQuizQuestionRepo;
    private InteractiveQuizRepo interactiveQuizRepo;

    @Override
    public int submitAnswer(List<InteractiveQuizAnswer> interactiveQuizAnswer){
        InteractiveQuiz interactiveQuiz = interactiveQuizRepo.findById(interactiveQuizAnswer.get(0).getInteractiveQuizId()).orElse(null);
        if (interactiveQuiz == null) {
            return 0;
        }
        // if (interactiveQuiz.getStatusId() != InteractiveQuiz.STATUS_OPENED) {
        //     return 0;
        // }
        //Lay list cau tra loi cu
        List<InteractiveQuizAnswer> interactiveQuizAnswers = interactiveQuizAnswerRepo.findAllByInteractiveQuizIdAndQuestionIdAndUserId(interactiveQuizAnswer.get(0).getInteractiveQuizId(), interactiveQuizAnswer.get(0).getQuestionId(), interactiveQuizAnswer.get(0).getUserId());
        for (InteractiveQuizAnswer item : interactiveQuizAnswers) {   
            interactiveQuizAnswerRepo.delete(item);
        }
        for (InteractiveQuizAnswer item : interactiveQuizAnswer) {   
            interactiveQuizAnswerRepo.save(item);
        }
        // CompositeInteractiveQuizAnswerId id;
        // id.setUserId(interactiveQuizAnswer.getUserId());
        // id.setChoiceAnswerId(interactiveQuizAnswer.getChoiceAnswerId());
        // id.setInteractiveQuizId(interactiveQuizAnswer.getInteractiveQuizId());
        // id.setQuestionId(interactiveQuizAnswer.getQuestionId());
        // InteractiveQuizAnswer answer = interactiveQuizAnswerRepo.findById()

        return 1;
    };
}
