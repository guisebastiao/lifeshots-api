package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    @Query("""
        SELECT CASE WHEN 
            COUNT(f1) > 0 AND COUNT(f2) > 0 
        THEN true ELSE false END
        FROM Follow f1, Follow f2
        WHERE f1.follower = :profileA AND f1.following = :profileB
          AND f2.follower = :profileB AND f2.following = :profileA
    """)
    boolean profilesFollowEachOther(@Param("profileA") Profile profileA, @Param("profileB") Profile profileB);
}
