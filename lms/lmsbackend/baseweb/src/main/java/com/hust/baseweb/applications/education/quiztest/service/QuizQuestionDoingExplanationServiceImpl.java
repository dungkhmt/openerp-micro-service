package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.contentmanager.model.ContentModel;
import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.applications.education.quiztest.entity.QuizQuestionDoingExplanation;
import com.hust.baseweb.applications.education.quiztest.model.quizdoingexplanation.QuizDoingExplanationInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.QuizQuestionDoingExplanationRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class QuizQuestionDoingExplanationServiceImpl implements QuizQuestionDoingExplanationService {

    private final MongoContentService mongoContentService;

    private final QuizQuestionDoingExplanationRepo quizDoingExplanationRepo;

    @Override
    public Collection<QuizQuestionDoingExplanation> findExplanationByParticipantIdAndQuestionId(
        String participantLoginId,
        UUID questionId
    ) {
        Sort sortDescByCreatedTime = Sort.by(Sort.Direction.DESC, "createdStamp");
        return quizDoingExplanationRepo.findByParticipantUserIdAndQuestionId(participantLoginId,
                                                                             questionId,
                                                                             sortDescByCreatedTime);
    }

    @Override
    public QuizQuestionDoingExplanation createExplanation(QuizDoingExplanationInputModel quizDoingExplanationIM,
                                                          MultipartFile attachment) {
        QuizQuestionDoingExplanation newQuizDoingExplanation = new QuizQuestionDoingExplanation();
        newQuizDoingExplanation.setQuestionId(quizDoingExplanationIM.getQuestionId());
        newQuizDoingExplanation.setParticipantUserId(quizDoingExplanationIM.getParticipantUserId());
        newQuizDoingExplanation.setTestId(quizDoingExplanationIM.getTestId());
        newQuizDoingExplanation.setSolutionExplanation(quizDoingExplanationIM.getSolutionExplanation());
        setAttachment(newQuizDoingExplanation, attachment);
        return quizDoingExplanationRepo.save(newQuizDoingExplanation);
    }

    @Override
    public QuizQuestionDoingExplanation updateExplanation(
        UUID explanationId,
        String newSolutionExplanation,
        MultipartFile attachment
    ) {
        QuizQuestionDoingExplanation updatedQuizDoingExplanation = quizDoingExplanationRepo.findById(explanationId)
            .orElseThrow(() -> new ResourceNotFoundException("Doesn't exist quiz doing explanation with id " + explanationId));
        updatedQuizDoingExplanation.setSolutionExplanation(newSolutionExplanation);
        setAttachment(updatedQuizDoingExplanation, attachment);
        return quizDoingExplanationRepo.save(updatedQuizDoingExplanation);
    }

    @Override
    public void deleteExplanation(UUID explanationId) {
        QuizQuestionDoingExplanation updatedQuizDoingExplanation = quizDoingExplanationRepo.findById(explanationId)
            .orElseThrow(() -> new ResourceNotFoundException("Doesn't exist quiz doing explanation with id " + explanationId));
        String attachmentStorageId = updatedQuizDoingExplanation.getAttachment();
        quizDoingExplanationRepo.deleteById(explanationId);
        if (attachmentStorageId != null) {
            mongoContentService.deleteFilesById(attachmentStorageId);
        }
    }

    @Override
    public QuizQuestionDoingExplanation setAttachment(QuizQuestionDoingExplanation savedSolution,
                                                      MultipartFile attachment) {
        if (attachment == null) {
            return savedSolution;
        }

        try {
            String uniqueFileName = new StringBuilder(UUID.randomUUID().toString())
                .append("_").append(attachment.getOriginalFilename())
                .toString();
            ContentModel contentModel = new ContentModel(uniqueFileName, attachment);
            ObjectId newAttachmentStorageId = mongoContentService.storeFileToGridFs(contentModel);
            String oldAttachmentStorageId = savedSolution.getAttachment();
            savedSolution.setAttachment(newAttachmentStorageId.toString());
            mongoContentService.deleteFilesById(oldAttachmentStorageId);
        } catch (IOException e) {
            log.error("An error occur when set attachment for quiz doing explanation with id {}. Error detal: {}",
                      savedSolution.getId(), e.getMessage());
            throw new RuntimeException(e);
        }
        return savedSolution;
    }
}
