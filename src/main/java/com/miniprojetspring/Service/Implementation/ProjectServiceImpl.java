package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProjectRepository;
import com.miniprojetspring.Service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project getProjectById(String uuid) {
        return projectRepository.findById(UUID.fromString(uuid)).orElseThrow(() -> new NotFoundException("Project not found"));
    }
}
