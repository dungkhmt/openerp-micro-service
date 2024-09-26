package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.QuizQuestionTag;
import com.hust.baseweb.applications.education.entity.QuizTag;
import com.hust.baseweb.applications.education.entity.compositeid.CompositeQuizQuestionTagId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.transaction.annotation.Transactional;

public interface QuizQuestionTagRepo extends JpaRepository<QuizQuestionTag, CompositeQuizQuestionTagId> {

    public List<QuizQuestionTag> findAllByQuestionId(UUID questionId);

    @Transactional
    public void deleteByQuestionId(UUID questionId);
    
    @Query(nativeQuery = true, value = "SELECT CAST(question_id as varchar) question_id from quiz_question_tag WHERE tag_id IN :tagIds GROUP BY question_id HAVING COUNT(tag_id) = :count")
    public List<UUID> findByTagIdIn(@Param("tagIds") List<UUID> tagIds, @Param("count") int count);
}
