package com.lifeshots.lifeshotsapi.repositories;

import com.lifeshots.lifeshotsapi.models.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StoryRepository extends JpaRepository<Story, UUID> {
    @Query("SELECT s FROM Story s WHERE s.expiresAt <= :time")
    List<Story> findAllStoriesOlderThan24Hours(@Param("time") LocalDateTime time);

    @Query("SELECT s FROM Story s JOIN Follow f ON f.following = s.user WHERE f.follower.id = :userId")
    Page<Story> findAllStoriesByBelongsFollowers(@Param("userId") UUID userId, Pageable pageable);
}
