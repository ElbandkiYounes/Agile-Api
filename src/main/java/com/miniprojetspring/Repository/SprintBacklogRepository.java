package com.miniprojetspring.Repository;

import com.miniprojetspring.model.SprintBacklog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SprintBacklogRepository extends JpaRepository<SprintBacklog, UUID> {
}
