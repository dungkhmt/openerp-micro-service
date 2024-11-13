package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.QuizTag;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizTagRepo extends JpaRepository<QuizTag, UUID> {
        public List<QuizTag> findAllByCourseId(String courseId);

        @Query(nativeQuery = true, value = "SELECT * FROM quiz_tags WHERE tag_id IN ?1")
        public List<QuizTag> findAllByTagIdsIn(List<UUID> tagIds);

        @Query(nativeQuery = true, value = "SELECT CAST(tag_id as varchar) tag_id FROM quiz_tags WHERE tag_name IN ?2 AND course_id = ?1")
        public List<UUID> findAllTagIdByCourseIdAndTagName(String courseId, List<String> tagName);

}
