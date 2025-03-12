package com.miniprojetspring.Repository;

import com.miniprojetspring.Model.Epic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EpicRepository extends JpaRepository<Epic, UUID> {
    @EntityGraph(attributePaths = "userStory")
    List<Epic> findByProductBacklog_Id(UUID productBacklogId);
}
