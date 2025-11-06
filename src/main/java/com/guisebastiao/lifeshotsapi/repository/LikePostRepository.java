package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, LikePostId> {

    @Query("SELECT COUNT(lp) > 0 FROM LikePost lp WHERE lp.post = :post AND lp.profile = :profile")
    boolean alreadyLikedPost(@Param("post") Post post, @Param("profile") Profile profile);

    Page<LikePost> findAllByPost(Post post, Pageable pageable);
    boolean existsByPostAndProfile(Post post, Profile profile);
}
