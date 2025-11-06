package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoryRepository extends JpaRepository<Story, UUID> {
    @Query("SELECT s FROM Story s WHERE s.id = :storyId AND s.isDeleted = FALSE")
    Optional<Story> findByIdAndNotDeleted(@Param("storyId") UUID storyId);

    @Query("SELECT s FROM Story s WHERE s.expiresAt <= :expiresAt AND s.isExpired = false")
    List<Story> findAllStoriesExpired(@Param("expiresAt") Instant expiresAt);
}
