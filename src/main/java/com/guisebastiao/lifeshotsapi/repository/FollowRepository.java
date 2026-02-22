package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Follow;
import com.guisebastiao.lifeshotsapi.entity.FollowId;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    boolean existsByFollowerAndFollowing(Profile follower, Profile following);

    Page<Follow> findByFollower(Profile follower, Pageable pageable);

    Page<Follow> findByFollowing(Profile following, Pageable pageable);

    Optional<Follow> findByFollowingAndFollower(Profile following, Profile follower);

    @Query("""
        SELECT DISTINCT y2.following
        FROM Follow y1
             JOIN Follow y2 ON y1.following.id = y2.follower.id
        WHERE y1.follower.id = :profileId
          AND y2.following.id <> :profileId
          AND y2.following.id NOT IN (
                SELECT f.following.id
                FROM Follow f
                WHERE f.follower.id = :profileId
          )
    """)
    Page<Profile> findFriendRecommendations(@Param("profileId") UUID profileId, Pageable pageable);
}
