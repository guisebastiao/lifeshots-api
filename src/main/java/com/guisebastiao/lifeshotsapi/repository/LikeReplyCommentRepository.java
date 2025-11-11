package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.LikeReplyComment;
import com.guisebastiao.lifeshotsapi.entity.LikeReplyCommentId;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.ReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeReplyCommentRepository extends JpaRepository<LikeReplyComment, LikeReplyCommentId> {
    boolean existsByReplyCommentAndProfile(ReplyComment comment, Profile profile);
}
