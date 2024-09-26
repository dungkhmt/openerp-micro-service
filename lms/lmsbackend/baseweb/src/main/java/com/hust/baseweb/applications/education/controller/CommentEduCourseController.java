package com.hust.baseweb.applications.education.controller;

import com.hust.baseweb.applications.education.entity.CommentsEduCourseMaterial;
import com.hust.baseweb.applications.education.model.CommentEduCourseDetailOM;
import com.hust.baseweb.applications.education.service.CommentsEduCourseMaterialService;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentEduCourseController {

    public CommentsEduCourseMaterialService commentsEduCourseMaterialService;
    public UserService userService;

    @PostMapping("/edu/class/comment")
    public ResponseEntity<?> createComment(
        Principal principal,
        @RequestBody CommentsEduCourseMaterial input
    ) {
        UserLogin u = userService.findById(principal.getName());
        CommentsEduCourseMaterial commentsEduCourseMaterial = commentsEduCourseMaterialService.createComment(
            input.getEduCourseMaterialId(),
            input.getReplyToCommentId(),
            input.getCommentMessage(),
            u
        );
        return ResponseEntity.ok().body(commentsEduCourseMaterial);
    }

    @GetMapping("/edu/class/comment/{eduCourseMaterialId}")
    public ResponseEntity<?> getListCommentsOnEduCourse(
        Principal principal,
        @PathVariable UUID eduCourseMaterialId
    ) {
        List<CommentEduCourseDetailOM> lst = commentsEduCourseMaterialService.findByEduCourseMaterialId(
            eduCourseMaterialId);
        return ResponseEntity.ok().body(lst);
    }

    @GetMapping("/edu/class/main-comment/{eduCourseMaterialId}")
    public ResponseEntity<?> getListMainCommentsOnEduCourse(
        Principal principal,
        @PathVariable UUID eduCourseMaterialId
    ) {
        List<CommentEduCourseDetailOM> lst = commentsEduCourseMaterialService.findByEduCourseMaterialIdWithoutReplyComment(
            eduCourseMaterialId);
        return ResponseEntity.ok().body(lst);
    }

    @GetMapping("/edu/class/reply-comment/{commentId}")
    public ResponseEntity<?> getListReplyCommentByCommentId(
        Principal principal,
        @PathVariable UUID commentId
    ) {
        List<CommentEduCourseDetailOM> lst = commentsEduCourseMaterialService.findByReplyCommentId(commentId);
        return ResponseEntity.ok().body(lst);
    }

    @PutMapping("/edu/class/comment/{commentId}")
    public ResponseEntity<?> editCommentOnEduCourse(
        Principal principal,
        @RequestBody CommentsEduCourseMaterial input,
        @PathVariable UUID commentId
    ) {
        CommentsEduCourseMaterial commentsEduCourseMaterial = commentsEduCourseMaterialService.editCommentEduCourse(
            commentId,
            input.getCommentMessage(),
            input.getCreatedStamp()
        );
        return ResponseEntity.ok().body(commentsEduCourseMaterial);
    }

    @DeleteMapping("/edu/class/comment/{commentId}")
    public ResponseEntity<?> deleteCommentOnEduCourse(
        Principal principal,
        @PathVariable UUID commentId
    ) {
        commentsEduCourseMaterialService.deleteCommentEduCourse(commentId);
        return ResponseEntity.ok().body(commentId);
    }
}
