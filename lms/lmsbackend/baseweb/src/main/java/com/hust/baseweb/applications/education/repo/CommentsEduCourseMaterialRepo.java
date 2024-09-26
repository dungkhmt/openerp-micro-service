package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.CommentsEduCourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CommentsEduCourseMaterialRepo extends JpaRepository<CommentsEduCourseMaterial, UUID> {

    @Transactional
    @Modifying
    @Query(value = "select * from comments_edu_course_material WHERE edu_course_material_id = :eduCourseMaterialId ORDER BY created_stamp ASC",
           nativeQuery = true)
    List<CommentsEduCourseMaterial> findAllByEduCourseMaterialId(UUID eduCourseMaterialId);

    CommentsEduCourseMaterial findByCommentId(UUID commentId);

    CommentsEduCourseMaterial deleteByCommentId(UUID commentId);

    @Transactional
    @Modifying
    @Query(value = "delete from comments_edu_course_material where reply_to_comment_id = :commentId",
           nativeQuery = true)
    void deleteAllByReplyToCommentId(UUID commentId);

    @Transactional
    @Modifying
    @Query(value = "select * from comments_edu_course_material where edu_course_material_id = :eduCourseMaterialId and reply_to_comment_id is null ORDER BY created_stamp ASC",
           nativeQuery = true)
    List<CommentsEduCourseMaterial> findByEduCourseMaterialIdWithoutReplyComment(UUID eduCourseMaterialId);

    @Transactional
    @Modifying
    @Query(value = "select * from comments_edu_course_material WHERE reply_to_comment_id = :replyToCommentId ORDER BY created_stamp ASC",
           nativeQuery = true)
    List<CommentsEduCourseMaterial> findByReplyToCommentId(UUID replyToCommentId);

    @Query(value = "select distinct posted_by_user_login_id from comments_edu_course_material where edu_course_material_id = :eduCourseMaterialId",
           nativeQuery = true)
    List<String> postedByUserLoginId(UUID eduCourseMaterialId);
}
