package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.config.EducationConfigProperties;
import com.hust.baseweb.applications.education.entity.CommentOnQuizQuestion;
import com.hust.baseweb.applications.education.model.quiz.CommentOnQuizQuestionDetailOM;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.repo.CommentOnQuizQuestionRepo;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class CommentOnQuizQuestionServiceImpl implements CommentOnQuizQuestionService {

    @Autowired
    private CommentOnQuizQuestionRepo commentOnQuizQuestionRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private QuizQuestionService quizQuestionService;

    @Autowired
    private EducationConfigProperties properties;

    @Override
    public CommentOnQuizQuestion createComment(UUID questionId, String comment, UUID replyToCommentId, UserLogin u) {
        CommentOnQuizQuestion commentOnQuizQuestion = new CommentOnQuizQuestion();
        commentOnQuizQuestion.setCommentText(comment);
        commentOnQuizQuestion.setQuestionId(questionId);
        commentOnQuizQuestion.setReplyToCommentId(replyToCommentId);
        commentOnQuizQuestion.setCreatedByUserLoginId(u.getUserLoginId());
        commentOnQuizQuestion.setCreatedStamp(new Date());
        commentOnQuizQuestion.setStatusId(CommentOnQuizQuestion.STATUS_CREATED);
        commentOnQuizQuestion = commentOnQuizQuestionRepo.save(commentOnQuizQuestion);


        // push notification to admin
        QuizQuestionDetailModel q = quizQuestionService.findById(questionId);
        String fromUserLoginId = u.getUserLoginId();
        String toUserLoginId = q.getCreatedByUserLoginId();
        String msg = "user " +
                     fromUserLoginId +
                     " comments on quiz " +
                     q.getQuizCourseTopic().getQuizCourseTopicName() +
                     " of course " +
                     q.getQuizCourseTopic().getEduCourse().getName();

        //String url = properties.getUrl_root() + "/edu/teacher/course/quiz/view/detail/" + questionId + "/" + q.getQuizCourseTopic().getEduCourse().getId();
        String url = "/edu/teacher/course/quiz/view/detail/" +
                     questionId +
                     "/" +
                     q.getQuizCourseTopic().getEduCourse().getId();
        List<String> listUsers = commentOnQuizQuestionRepo.getListUserIdHadComment(questionId);
        listUsers.forEach((userId) -> {
            notificationsService.create(fromUserLoginId, userId, msg, url);
        });

        return commentOnQuizQuestion;
    }

    @Override
    public List<CommentOnQuizQuestionDetailOM> findByQuestionId(UUID questionId) {
        List<CommentOnQuizQuestion> lst = commentOnQuizQuestionRepo.findAllByQuestionIdWithoutReplyComment(questionId);
        List<CommentOnQuizQuestionDetailOM> list = new ArrayList();
        for (CommentOnQuizQuestion c : lst) {
            CommentOnQuizQuestionDetailOM cd = new CommentOnQuizQuestionDetailOM();
            cd.setCommentId(c.getCommentId());
            cd.setCommentText(c.getCommentText());
            cd.setQuestionId(c.getQuestionId());
            cd.setCreatedByUserLoginId(c.getCreatedByUserLoginId());
            cd.setCreatedStamp(c.getCreatedStamp());
            cd.setReplyToCommentId(c.getReplyToCommentId());

            PersonModel person = userService.findPersonByUserLoginId(c.getCreatedByUserLoginId());
            if (person != null) {
                cd.setFullNameOfCreator(person.getLastName() + " " + person.getMiddleName()
                                        + " " + person.getFirstName());
            }
            list.add(cd);
        }
        return list;
    }

    @Override
    public List<CommentOnQuizQuestionDetailOM> findByReplyToCommentId(UUID questionId) {
        List<CommentOnQuizQuestion> lst = commentOnQuizQuestionRepo.findAllByReplyToCommentId(questionId);
        List<CommentOnQuizQuestionDetailOM> list = new ArrayList();
        for (CommentOnQuizQuestion c : lst) {
            CommentOnQuizQuestionDetailOM cd = new CommentOnQuizQuestionDetailOM();
            cd.setCommentId(c.getCommentId());
            cd.setCommentText(c.getCommentText());
            cd.setCreatedByUserLoginId(c.getCreatedByUserLoginId());
            cd.setCreatedStamp(c.getCreatedStamp());
            cd.setReplyToCommentId(c.getReplyToCommentId());

            PersonModel person = userService.findPersonByUserLoginId(c.getCreatedByUserLoginId());
            if (person != null) {
                cd.setFullNameOfCreator(person.getLastName() + " " + person.getMiddleName()
                                        + " " + person.getFirstName());
            }
            list.add(cd);
        }
        return list;
    }

    @Override
    public int getNumberCommentsOnQuiz(UUID questionId) {
        int nbr = commentOnQuizQuestionRepo.getNumberCommentsOnQuiz(questionId);
        return nbr;
    }

    @Override
    public CommentOnQuizQuestion deleteCommentOnQuiz(UUID commentId) {
        CommentOnQuizQuestion deleteComment = commentOnQuizQuestionRepo.findByCommentId(commentId);

        commentOnQuizQuestionRepo.deleteReplyCommentByCommentId(commentId);
        commentOnQuizQuestionRepo.deleteByCommentId(commentId);

        return deleteComment;
    }

    @Override
    public CommentOnQuizQuestion updateComment(UUID commentId, String commentText) {
        CommentOnQuizQuestion deleteComment = commentOnQuizQuestionRepo.findByCommentId(commentId);
        deleteComment.setCommentText(commentText);

        deleteComment = commentOnQuizQuestionRepo.save(deleteComment);

        return deleteComment;
    }

    public List<String> listUserIdHadComment(UUID questionId) {
        List<String> listUserId = commentOnQuizQuestionRepo.getListUserIdHadComment(questionId);

        return listUserId;
    }
}
