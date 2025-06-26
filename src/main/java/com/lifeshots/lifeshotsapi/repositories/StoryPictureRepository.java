package com.lifeshots.lifeshotsapi.repositories;

import com.lifeshots.lifeshotsapi.models.StoryPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoryPictureRepository extends JpaRepository<StoryPicture, UUID> {
}
