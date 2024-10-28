package com.hust.baseweb.applications.education.service;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.QuizTag;
import com.hust.baseweb.applications.education.repo.QuizTagRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class QuizTagServiceImpl implements QuizTagService{
    private QuizTagRepo quizTagRepo;
    @Override
    public QuizTag createQuizTag(String courseId, String tagName){
        QuizTag quizTag = new QuizTag();
        quizTag.setCourseId(courseId);
        quizTag.setTagName(tagName);
        quizTag.setCreatedStamp(new Date());
        quizTag.setLastUpdated(new Date());
        return quizTagRepo.save(quizTag);
    }
}
