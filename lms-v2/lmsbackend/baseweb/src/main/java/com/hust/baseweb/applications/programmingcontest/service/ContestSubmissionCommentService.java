package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionComment;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.CommentDTO;
import com.hust.baseweb.applications.programmingcontest.model.ModelContestSubmissionComment;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionCommentRepository;
import com.hust.baseweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContestSubmissionCommentService {

    private final ContestSubmissionCommentRepository contestSubmissionCommentRepo;

    @Autowired
    public ContestSubmissionCommentService(ContestSubmissionCommentRepository contestSubmissionCommentRepo) {
        this.contestSubmissionCommentRepo = contestSubmissionCommentRepo;
    }

    public ContestSubmissionComment postComment(UUID submissionId, ModelContestSubmissionComment modelComment, String userId) throws MiniLeetCodeException {
        if (modelComment == null || modelComment.getComment() == null || modelComment.getComment().isEmpty()) {
            throw new MiniLeetCodeException("Comment cannot be null or empty.");
        }

        ContestSubmissionComment comment = new ContestSubmissionComment();
        comment.setId(UUID.randomUUID());
        comment.setSubmissionId(submissionId);
        comment.setUserId(userId);
        comment.setComment(modelComment.getComment());
        comment.setCreatedStamp(new Date());
        comment.setLastUpdatedStamp(new Date());

        return contestSubmissionCommentRepo.save(comment);
    }

    @Autowired
    private UserService userService;

    public List<CommentDTO> getAllCommentsBySubmissionId(UUID submissionId) {
        List<ContestSubmissionComment> comments = contestSubmissionCommentRepo.findBySubmissionId(submissionId);

        return comments.stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getComment(),
                        comment.getCreatedStamp(), userService.getUserFullName(comment.getUserId())))
                .collect(Collectors.toList());
    }

}
