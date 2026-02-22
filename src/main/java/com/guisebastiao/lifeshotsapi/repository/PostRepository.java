package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findByIdAndNotDeleted(@Param("id") UUID id);

    @Query("SELECT p FROM Post p WHERE p.createdAt >= :limit AND p.profile.isPrivate = false AND p.isDeleted = false ORDER BY p.likeCount DESC")
    Page<Post> findAllTrendingPosts(@Param("limit") Instant limit, Pageable pageable);

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.profile IN (
            SELECT f.following
            FROM Follow f
            WHERE f.follower = :profile
        )
          AND (
                p.profile.isPrivate = false
                OR EXISTS (
                    SELECT 1
                    FROM Follow f2
                    WHERE f2.follower = p.profile
                      AND f2.following = :profile
                )
          )
          AND p.isDeleted = false
        ORDER BY p.createdAt DESC
    """)
    Page<Post> findAllPostsFromFriends(@Param("profile") Profile profile, Pageable pageable);
}
