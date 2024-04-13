package com.hust.baseweb.applications.education.quiztest.service;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.model.InteractiveQuizTestResultOutputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo.StudentInfo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class InteractiveQuizServiceImpl implements InteractiveQuizService {
    
    private InteractiveQuizRepo interactiveQuizRepo;

    @Override
    public List<InteractiveQuiz> getAllInteractiveQuiz(){
        return interactiveQuizRepo.findAll();
    } 

    @Override
    public InteractiveQuiz getInteractiveQuizById(String testId){
        return interactiveQuizRepo.getOne(UUID.fromString(testId));
    }

    @Override
    public InteractiveQuiz createInteractiveQuiz(String interactiveQuizName, UUID sessionId, String status){
        InteractiveQuiz interactiveQuiz = new InteractiveQuiz();
        interactiveQuiz.setInteractive_quiz_name(interactiveQuizName);
        interactiveQuiz.setSessionId(sessionId);
        interactiveQuiz.setStatusId(status);
        interactiveQuiz.setCreatedStamp(new Date());
        interactiveQuiz.setLastUpdated(new Date());
        return interactiveQuizRepo.save(interactiveQuiz);
    }
}
