package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.CommentOnQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CommentOnQuizQuestionRepo extends JpaRepository<CommentOnQuizQuestion, UUID> {

    List<CommentOnQuizQuestion> findAllByQuestionId(UUID questionId);

    List<CommentOnQuizQuestion> findAllByReplyToCommentId(UUID commentId);

    CommentOnQuizQuestion findByCommentId(UUID commentId);

    @Transactional
    @Modifying
    @Query(value = "select * from comment_on_quiz_question where reply_to_comment_id is null and question_id = :questionId",
           nativeQuery = true)
    List<CommentOnQuizQuestion> findAllByQuestionIdWithoutReplyComment(UUID questionId);


    @Transactional
    @Modifying
    void deleteByCommentId(UUID commentId);

    @Transactional
    @Modifying
    @Query(value = "delete from comment_on_quiz_question where reply_to_comment_id = :commentId", nativeQuery = true)
    void deleteReplyCommentByCommentId(UUID commentId);

    @Query(value = "select count(*) from comment_on_quiz_question where question_id = ?1", nativeQuery = true)
    int getNumberCommentsOnQuiz(UUID questionId);

    @Query(value = "select distinct created_by_user_login_id from comment_on_quiz_question where question_id = :questionId",
           nativeQuery = true)
    List<String> getListUserIdHadComment(UUID questionId);
}
