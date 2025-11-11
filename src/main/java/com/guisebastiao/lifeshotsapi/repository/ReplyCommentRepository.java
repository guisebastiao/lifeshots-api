package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Comment;
import com.guisebastiao.lifeshotsapi.entity.ReplyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReplyCommentRepository extends JpaRepository<ReplyComment, UUID> {
    @Query("SELECT rc FROM ReplyComment rc WHERE rc.comment = :comment AND rc.isDeleted = false AND rc.isRemoved = false")
    Page<ReplyComment> findAllByComment(@Param("comment") Comment comment, Pageable pageable);

    @Query("SELECT rc FROM ReplyComment rc WHERE rc.id = :id AND rc.isDeleted = false AND rc.isRemoved = false")
    Optional<ReplyComment> findByIdAndNotDeletedAndNotRemoved(@Param("id") UUID id);
}
