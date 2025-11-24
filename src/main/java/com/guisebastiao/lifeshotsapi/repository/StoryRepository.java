package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    int countStoriesByProfile(Profile profile);

    @Query("SELECT s FROM Story s WHERE s.id = :storyId AND s.isDeleted = FALSE")
    Optional<Story> findByIdAndNotDeleted(@Param("storyId") UUID storyId);

    @Query("SELECT s FROM Story s WHERE s.expiresAt <= :expiresAt AND s.isExpired = false")
    List<Story> findAllStoriesExpired(@Param("expiresAt") Instant expiresAt);

    @Query("SELECT s FROM Story s WHERE s.profile = :profile AND s.isDeleted = false AND s.isExpired = false")
    List<Story> findAllStoriesByProfile(@Param("profile") Profile profile);

    @Query("""
    SELECT s
    FROM Story s
    WHERE s.profile IN (
        SELECT f.following
        FROM Follow f
        WHERE f.follower = :profile
    )
    AND (
        s.profile.isPrivate = false
        OR EXISTS (
            SELECT 1
            FROM Follow f2
            WHERE f2.follower = s.profile
              AND f2.following = :profile
        )
    )
    AND s.isDeleted = false
    AND s.isExpired = false
    ORDER BY s.createdAt DESC
    """)
    Page<Story> findAllStoriesFromFriends(@Param("profile") Profile profile, Pageable pageable);

    @Query("""
      SELECT COUNT(DISTINCT s.profile)
      FROM Story s
      WHERE s.profile IN (
        SELECT f.following FROM Follow f WHERE f.follower = :profile
      )
      AND (
        s.profile.isPrivate = false
        OR EXISTS (
          SELECT 1 FROM Follow f2 WHERE f2.follower = s.profile AND f2.following = :profile
        )
      )
      AND s.isDeleted = false
      AND s.isExpired = false
    """)
    long countDistinctProfilesFromFriends(@Param("profile") Profile profile);
}
