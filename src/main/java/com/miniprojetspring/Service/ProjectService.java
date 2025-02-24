package com.miniprojetspring.Service;

import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> getProjectById(UUID uuid) {
        return projectRepository.findById(uuid);
    }
}
