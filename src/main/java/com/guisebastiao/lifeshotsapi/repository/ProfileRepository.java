package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    @Query("""
        SELECT CASE WHEN COUNT(f1) > 0 AND COUNT(f2) > 0 THEN true ELSE false END
            FROM Follow f1, Follow f2
        WHERE f1.follower = :profileA AND f1.following = :profileB
            AND f2.follower = :profileB AND f2.following = :profileA
    """)
    boolean profilesFollowEachOther(@Param("profileA") Profile profileA, @Param("profileB") Profile profileB);

    @Query("""
        SELECT p FROM Profile p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.user.handle) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Profile> searchProfiles(@Param("search") String search, Pageable pageable);

    @Query(
            value = """
                SELECT s.profile_id FROM stories s WHERE s.profile_id IN (
                    SELECT f.following_id FROM follows f WHERE f.follower_id = :profileId
                ) AND s.is_deleted = false AND s.is_expired = false GROUP BY s.profile_id ORDER BY MAX(s.created_at) DESC
            """, countQuery = """
                SELECT COUNT(DISTINCT s.profile_id) FROM stories s WHERE s.profile_id IN (
                    SELECT f.following_id FROM follows f WHERE f.follower_id = :profileId
                ) AND s.is_deleted = false AND s.is_expired = false
            """, nativeQuery = true)
    Page<UUID> findProfileIdsWithStories(@Param("profileId") UUID profileId, Pageable pageable);
}
