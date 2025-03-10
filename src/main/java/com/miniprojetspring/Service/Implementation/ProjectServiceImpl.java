package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProjectRepository;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.payload.ProjectPayload;
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

    public Project createProject(ProjectPayload payload) {
        return projectRepository.save(payload.toEntity());
    }

    public Project updateProject(String uuid, ProjectPayload payload) {
        Project existingProject = projectRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Project not found"));
        return projectRepository.save(payload.toEntity(existingProject));
    }

    public void deleteProject(String uuid) {
        Project project = projectRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Project not found"));
        projectRepository.delete(project);
    }
}
