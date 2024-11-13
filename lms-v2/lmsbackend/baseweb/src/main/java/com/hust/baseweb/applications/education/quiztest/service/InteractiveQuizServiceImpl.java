package com.hust.baseweb.applications.education.quiztest.service;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.QuizChoiceAnswer;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizAnswer;
import com.hust.baseweb.applications.education.quiztest.model.InteractiveQuizTestResultOutputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo.StudentInfo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizAnswerRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo.StudentResult;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class InteractiveQuizServiceImpl implements InteractiveQuizService {
    
    private InteractiveQuizRepo interactiveQuizRepo;
    private InteractiveQuizAnswerRepo interactiveQuizAnswerRepo;
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

    public List<StudentResult> getQuizResults(UUID interactiveQuizId){
        // List<InteractiveQuizAnswer> answers = interactiveQuizAnswerRepo.findByInteractiveQuizId(interactiveQuizId);
        List<InteractiveQuizAnswer> answers = interactiveQuizAnswerRepo.findByInteractiveQuizId(interactiveQuizId);

        if (answers == null || answers.isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu không có câu trả lời nào
        }

        // Danh sách để lưu trữ kết quả cuối cùng
        List<StudentResult> results = new ArrayList<>();

        // Lấy danh sách tất cả các userId duy nhất
        List<String> userIds = answers.stream()
            .map(InteractiveQuizAnswer::getUserId)
            .distinct()
            .collect(Collectors.toList());

        // Lặp qua từng userId để tính điểm
        for (String userId : userIds) {
            // Lấy tất cả các câu trả lời của người dùng hiện tại
            List<InteractiveQuizAnswer> userAnswers = answers.stream()
                .filter(answer -> answer.getUserId().equals(userId))
                .collect(Collectors.toList());

            int score = 0;

            // Lấy danh sách tất cả các questionId mà người dùng đã trả lời
            List<UUID> questionIds = userAnswers.stream()
                .map(InteractiveQuizAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestionId)
                .distinct()
                .collect(Collectors.toList());

            // Lặp qua từng questionId để kiểm tra câu trả lời đúng
            for (UUID questionId : questionIds) {
                // Lấy tất cả các câu trả lời đúng của câu hỏi hiện tại
                List<QuizChoiceAnswer> correctAnswers = userAnswers.stream()
                    .filter(answer -> answer.getQuizQuestion().getQuestionId().equals(questionId))
                    .map(InteractiveQuizAnswer::getQuizChoiceAnswer)
                    .filter(Objects::nonNull)
                    .filter(QuizChoiceAnswer::isCorrectAnswer)
                    .distinct()
                    .collect(Collectors.toList());

                // Lấy tất cả các câu trả lời của người dùng cho câu hỏi hiện tại
                List<QuizChoiceAnswer> userChoices = userAnswers.stream()
                    .filter(answer -> answer.getQuizQuestion().getQuestionId().equals(questionId))
                    .map(InteractiveQuizAnswer::getQuizChoiceAnswer)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

                // Kiểm tra nếu tất cả các câu trả lời đúng đều được chọn
                boolean allCorrect = correctAnswers.size() == userChoices.size() && correctAnswers.containsAll(userChoices);

                // Nếu tất cả các câu trả lời đúng đều được chọn, tăng điểm số lên 1
                if (allCorrect) {
                    score++;
                }
            }

            // Thêm kết quả cho người dùng hiện tại vào danh sách kết quả
            results.add(new StudentResult(userId, score));
        }

        return results;
    }
}
