package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.CommentOnQuizQuestion;
import com.hust.baseweb.applications.education.model.quiz.CommentOnQuizQuestionDetailOM;
import com.hust.baseweb.entity.UserLogin;

import java.util.List;
import java.util.UUID;

public interface CommentOnQuizQuestionService {

    CommentOnQuizQuestion createComment(UUID questionId, String comment, UUID getReplyToCommentId, UserLogin u);

    List<CommentOnQuizQuestionDetailOM> findByQuestionId(UUID questionId);

    List<CommentOnQuizQuestionDetailOM> findByReplyToCommentId(UUID commentId);

    CommentOnQuizQuestion deleteCommentOnQuiz(UUID commentId);

    CommentOnQuizQuestion updateComment(UUID commentId, String commentText);

    int getNumberCommentsOnQuiz(UUID questionId);

    List<String> listUserIdHadComment(UUID questionId);
}
