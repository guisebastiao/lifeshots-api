package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Comment;
import com.guisebastiao.lifeshotsapi.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.isDeleted = false")
    Page<Comment> findAllByPost(@Param("post") Post post, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.isDeleted = false AND c.isRemoved = false")
    Optional<Comment> findByIdAndNotDeletedAndNotRemoved(@Param("id") UUID id);
}
