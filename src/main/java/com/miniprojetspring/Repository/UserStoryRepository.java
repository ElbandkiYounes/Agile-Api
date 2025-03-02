package com.miniprojetspring.Repository;

import com.miniprojetspring.Model.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {
    List<UserStory> findByEpicId(UUID epicId);
    List<UserStory> findUserStoriesByProductBacklogId(UUID epicId);
}
