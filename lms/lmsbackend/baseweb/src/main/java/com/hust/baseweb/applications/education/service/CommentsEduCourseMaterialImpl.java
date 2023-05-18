package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.CommentsEduCourseMaterial;
import com.hust.baseweb.applications.education.entity.EduCourseChapterMaterial;
import com.hust.baseweb.applications.education.model.CommentEduCourseDetailOM;
import com.hust.baseweb.applications.education.repo.CommentsEduCourseMaterialRepo;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentsEduCourseMaterialImpl implements CommentsEduCourseMaterialService {

    @Autowired
    private CommentsEduCourseMaterialRepo commentsEduCourseMaterialRepo;
    public UserService userService;
    private NotificationsService notificationsService;
    private EduCourseChapterMaterialService eduCourseChapterMaterialService;

    @Override
    public CommentsEduCourseMaterial createComment(
        UUID eduCourseMaterialId,
        UUID replyToCommentId,
        String comment,
        UserLogin u
    ) {
        CommentsEduCourseMaterial commentsEduCourseMaterial = new CommentsEduCourseMaterial();
        commentsEduCourseMaterial.setCommentMessage(comment);
        commentsEduCourseMaterial.setEduCourseMaterialId(eduCourseMaterialId);
        commentsEduCourseMaterial.setReplyToCommentId(replyToCommentId);
        commentsEduCourseMaterial.setPostedByUserLoginId(u.getUserLoginId());
        commentsEduCourseMaterial.setCreatedStamp(new Date());
        commentsEduCourseMaterial.setStatusId(CommentsEduCourseMaterial.STATUS_CREATED);
        commentsEduCourseMaterial = commentsEduCourseMaterialRepo.save(commentsEduCourseMaterial);

        EduCourseChapterMaterial material = eduCourseChapterMaterialService.findById(eduCourseMaterialId);

        List<String> listUsers = commentsEduCourseMaterialRepo.postedByUserLoginId(eduCourseMaterialId);
        String url = "/edu/student/course/chapter/material/detail/" + eduCourseMaterialId;
        String message = "User " + u.getUserLoginId() + " comments on material " + material.getEduCourseMaterialName();
        listUsers.forEach((user) -> {
            notificationsService.create(u.getUserLoginId(), user, message, url);
        });

        return commentsEduCourseMaterial;

    }

    @Override
    public List<CommentEduCourseDetailOM> findByEduCourseMaterialId(UUID eduCourseMaterialId) {
        List<CommentsEduCourseMaterial> lst = commentsEduCourseMaterialRepo.findAllByEduCourseMaterialId(
            eduCourseMaterialId);
        List<CommentEduCourseDetailOM> list = new ArrayList();
        for (CommentsEduCourseMaterial cmt : lst) {
            // get info of comment detail
            CommentEduCourseDetailOM cmtDetail = new CommentEduCourseDetailOM();
            cmtDetail.setCommentId(cmt.getCommentId());
            cmtDetail.setReplyToCommentId(cmt.getReplyToCommentId());
            cmtDetail.setCommentMessage(cmt.getCommentMessage());
            cmtDetail.setPostedByUserLoginId(cmt.getPostedByUserLoginId());
            cmtDetail.setCreatedStamp(cmt.getCreatedStamp());

            //get name of comment' person
            PersonModel person = userService.findPersonByUserLoginId(cmtDetail.getPostedByUserLoginId());
            if (person != null) {
                cmtDetail.setFullNameOfCreator(person.getLastName() + " " + person.getMiddleName()
                                               + " " + person.getFirstName());
            }
            list.add(cmtDetail);
        }
        return list;
    }

    @Override
    public List<CommentEduCourseDetailOM> findByEduCourseMaterialIdWithoutReplyComment(UUID eduCourseMaterialId) {
        List<CommentsEduCourseMaterial> lst = commentsEduCourseMaterialRepo.findByEduCourseMaterialIdWithoutReplyComment(
            eduCourseMaterialId);
        List<CommentEduCourseDetailOM> list = new ArrayList();
        for (CommentsEduCourseMaterial cmt : lst) {
            // get info of comment detail
            CommentEduCourseDetailOM cmtDetail = new CommentEduCourseDetailOM();
            cmtDetail.setCommentId(cmt.getCommentId());
            cmtDetail.setReplyToCommentId(cmt.getReplyToCommentId());
            cmtDetail.setCommentMessage(cmt.getCommentMessage());
            cmtDetail.setPostedByUserLoginId(cmt.getPostedByUserLoginId());
            cmtDetail.setCreatedStamp(cmt.getCreatedStamp());

            //get name of comment' person
            PersonModel person = userService.findPersonByUserLoginId(cmtDetail.getPostedByUserLoginId());
            if (person != null) {
                cmtDetail.setFullNameOfCreator(person.getLastName() + " " + person.getMiddleName()
                                               + " " + person.getFirstName());
            }
            list.add(cmtDetail);
        }
        return list;
    }

    @Override
    public List<CommentEduCourseDetailOM> findByReplyCommentId(UUID commentId) {
        List<CommentsEduCourseMaterial> lst = commentsEduCourseMaterialRepo.findByReplyToCommentId(commentId);
        List<CommentEduCourseDetailOM> list = new ArrayList();
        for (CommentsEduCourseMaterial cmt : lst) {
            // get info of comment detail
            CommentEduCourseDetailOM cmtDetail = new CommentEduCourseDetailOM();
            cmtDetail.setCommentId(cmt.getCommentId());
            cmtDetail.setReplyToCommentId(cmt.getReplyToCommentId());
            cmtDetail.setCommentMessage(cmt.getCommentMessage());
            cmtDetail.setPostedByUserLoginId(cmt.getPostedByUserLoginId());
            cmtDetail.setCreatedStamp(cmt.getCreatedStamp());

            //get name of comment' person
            PersonModel person = userService.findPersonByUserLoginId(cmtDetail.getPostedByUserLoginId());
            if (person != null) {
                cmtDetail.setFullNameOfCreator(person.getLastName() + " " + person.getMiddleName()
                                               + " " + person.getFirstName());
            }
            list.add(cmtDetail);
        }
        return list;
    }

    @Override
    public CommentsEduCourseMaterial editCommentEduCourse(UUID commentId, String comment, Date createdStamp) {

        CommentsEduCourseMaterial newComment = commentsEduCourseMaterialRepo.findByCommentId(commentId);
        newComment.setCommentMessage(comment);
//        newComment.setCreatedStamp(createdStamp);
        newComment = commentsEduCourseMaterialRepo.save(newComment);
        return newComment;
    }

    @Override
    public CommentsEduCourseMaterial deleteCommentEduCourse(UUID commentId) {
        CommentsEduCourseMaterial deleteCmt = commentsEduCourseMaterialRepo.findByCommentId(commentId);
        commentsEduCourseMaterialRepo.deleteAllByReplyToCommentId(commentId);
        commentsEduCourseMaterialRepo.deleteById(commentId);

        return deleteCmt;
    }
}
