package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.SprintBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.Repository.SprintBacklogRepository;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.Service.SprintBacklogService;
import com.miniprojetspring.payload.SprintBacklogPayload;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SprintBacklogServiceImpl implements SprintBacklogService {

    private final SprintBacklogRepository sprintBacklogRepository;
    private final ProjectService projectServiceImpl;
    private final ProjectSecurityService projectSecurityService;

    public SprintBacklogServiceImpl(SprintBacklogRepository sprintBacklogRepository, ProjectService projectServiceImpl, ProjectSecurityService projectSecurityService) {
        this.sprintBacklogRepository = sprintBacklogRepository;
        this.projectServiceImpl = projectServiceImpl;
        this.projectSecurityService = projectSecurityService;
    }

    public SprintBacklog createSprintBacklog(SprintBacklogPayload payload) {
        Project project = projectServiceImpl.getProject();
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        return sprintBacklogRepository.save(payload.toEntity(project));
    }

    public SprintBacklog getSprintBacklogById(String id) {
        SprintBacklog sprintBacklog =  sprintBacklogRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Sprint Backlog not found"));
        if(!projectSecurityService.isProjectMember(sprintBacklog.getProject().getId().toString())
        && !projectSecurityService.isProjectOwner(sprintBacklog.getProject().getId().toString())) {
            throw new AccessDeniedException("Sprint Backlog not found");
        }
        return sprintBacklog;
    }

    public void deleteSprintBacklog(String id) {
        SprintBacklog sprintBacklog = getSprintBacklogById(id);
        if (sprintBacklog == null) {
            throw new NotFoundException("Sprint Backlog not found");
        }
        if(!projectSecurityService.isProjectOwner(sprintBacklog.getProject().getId().toString())) {
            throw new AccessDeniedException("Sprint Backlog not found");
        }
        sprintBacklogRepository.deleteById(UUID.fromString(id));
    }

    public SprintBacklog updateSprintBacklog(String id, SprintBacklogPayload payload) {
        SprintBacklog sprintBacklog = getSprintBacklogById(id);
        if (sprintBacklog == null) {
            throw new NotFoundException("Sprint Backlog not found");
        }
        if(!projectSecurityService.isProjectOwner(sprintBacklog.getProject().getId().toString())) {
            throw new AccessDeniedException("Sprint Backlog not found");
        }
        return sprintBacklogRepository.save(payload.toEntity(sprintBacklog));
    }
}