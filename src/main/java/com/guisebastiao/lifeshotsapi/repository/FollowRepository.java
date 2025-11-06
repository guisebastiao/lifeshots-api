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

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f WHERE f.following = :following AND f.follower = :follower")
    boolean alreadyFollowingAccount(@Param("following") Profile following, @Param("follower")Profile follower);

    Page<Follow> findByFollower(Profile follower, Pageable pageable);
    Page<Follow> findByFollowing(Profile following, Pageable pageable);
    Optional<Follow> findByFollowingAndFollower(Profile following, Profile follower);
    boolean existsByFollowerAndFollowing(Profile follower, Profile following);
    boolean existsByFollowingAndFollower(Profile following, Profile follower);
}
