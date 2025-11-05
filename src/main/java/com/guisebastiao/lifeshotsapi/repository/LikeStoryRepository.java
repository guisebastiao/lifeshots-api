package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.LikeStory;
import com.guisebastiao.lifeshotsapi.entity.LikeStoryId;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeStoryRepository extends JpaRepository<LikeStory, LikeStoryId> {

    @Query("SELECT COUNT(ls) > 0 FROM LikeStory ls WHERE ls.story = :story AND ls.profile = :profile")
    boolean alreadyLikedStory(@Param("story") Story story, @Param("profile") Profile profile);

    Page<LikeStory> findAllByStory(Story story, Pageable pageable);
}
