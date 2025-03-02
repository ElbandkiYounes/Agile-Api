package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.SprintBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.SprintBacklogRepository;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.Service.SprintBacklogService;
import com.miniprojetspring.payload.SprintBacklogPayload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SprintBacklogServiceImpl implements SprintBacklogService {

    private final SprintBacklogRepository sprintBacklogRepository;
    private final ProjectService projectServiceImpl;

    public SprintBacklogServiceImpl(SprintBacklogRepository sprintBacklogRepository, ProjectService projectServiceImpl) {
        this.sprintBacklogRepository = sprintBacklogRepository;
        this.projectServiceImpl = projectServiceImpl;
    }

    public SprintBacklog createSprintBacklog(String projectId, SprintBacklogPayload payload) {
        Project project = projectServiceImpl.getProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        return sprintBacklogRepository.save(payload.toEntity(project));
    }

    public SprintBacklog getSprintBacklogById(String id) {
        return sprintBacklogRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Sprint Backlog not found"));
    }

    public void deleteSprintBacklog(String id) {
        getSprintBacklogById(id);
        sprintBacklogRepository.deleteById(UUID.fromString(id));
    }

    public SprintBacklog updateSprintBacklog(String id, SprintBacklogPayload payload) {
        SprintBacklog sprintBacklog = getSprintBacklogById(id);
        return sprintBacklogRepository.save(payload.toEntity(sprintBacklog));
    }
}