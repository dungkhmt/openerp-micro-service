package com.hust.baseweb.applications.education.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Le Anh Tuan
 */
public interface VideoRepository extends JpaRepository<Video, UUID> {

    Optional<Video> findByIdAndDeletedFalse(UUID id);

    @Modifying
    @Query("update Video v set v.deleted = ?2 where v.id = ?1")
    void updateDeleted(UUID id, boolean deleted);
}
