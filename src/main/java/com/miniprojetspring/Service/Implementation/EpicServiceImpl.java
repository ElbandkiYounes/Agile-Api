package com.miniprojetspring.service.implementation;

import com.miniprojetspring.exception.BadRequestException;
import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.Epic;
import com.miniprojetspring.model.ProductBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.SprintBacklog;
import com.miniprojetspring.repository.EpicRepository;
import com.miniprojetspring.service.EpicService;
import com.miniprojetspring.service.ProductBacklogService;
import com.miniprojetspring.service.SprintBacklogService;
import com.miniprojetspring.payload.EpicPayload;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EpicServiceImpl implements EpicService {

    private final EpicRepository epicRepository;
    private final ProductBacklogService productBacklogServiceImpl;
    private final SprintBacklogService sprintBacklogServiceImpl;
    private final ProjectSecurityService projectSecurityService;

    public EpicServiceImpl(EpicRepository epicRepository,
                           ProductBacklogService productBacklogServiceImpl,
                           SprintBacklogService sprintBacklogServiceImpl, ProjectSecurityService projectSecurityService) {
        this.epicRepository = epicRepository;
        this.productBacklogServiceImpl = productBacklogServiceImpl;
        this.sprintBacklogServiceImpl = sprintBacklogServiceImpl;
        this.projectSecurityService = projectSecurityService;
    }

    private void validateProjectAccess(String projectId) {
        if (!projectSecurityService.isProjectMember(projectId)
                && !projectSecurityService.isProjectOwner(projectId)) {
            throw new AccessDeniedException("Access denied to project");
        }
    }

    private void validateProjectOwner(String projectId) {
        if (!projectSecurityService.isProjectOwner(projectId)) {
            throw new AccessDeniedException("Only project owner can perform this action");
        }
    }

    public Epic createEpic(EpicPayload payload) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklog();
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog not found");
        }
        return epicRepository.save(payload.toEntity(productBacklog));
    }

    public Epic getEpicById(String id) {
        Epic epic = epicRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Epic not found"));
        validateProjectAccess(epic.getProductBacklog().getProject().getId().toString());
        return epic;
    }

    public List<Epic> getEpics() {
        Project project = projectSecurityService.getCurrentUser().getProject();
        if (project == null) {
            throw new NotFoundException("User doesnt belong to a Project");
        }
        ProductBacklog productBacklog = project.getProductBacklog();
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog not found");
        }
        return epicRepository.findByProductBacklog_Id(productBacklog.getId());

    }

    public void deleteEpic(String id) {
        Epic epic = getEpicById(id);
        validateProjectOwner(epic.getProductBacklog().getProject().getId().toString());
        epicRepository.deleteById(UUID.fromString(id));
    }

    public Epic updateEpic(String id, EpicPayload payload) {
        Epic epic = getEpicById(id);
        validateProjectOwner(epic.getProductBacklog().getProject().getId().toString());
        return epicRepository.save(payload.toEntity(epic));
    }

    public Epic linkEpicToSprintBacklog(String sprintBacklogId, String epicId) {
        Epic epic = getEpicById(epicId);
        SprintBacklog sprintBacklog = sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId);
        if(epic.getSprintBacklog() != null){
            throw new ConflictException("Epic already linked to a Sprint Backlog");
        }
        if (sprintBacklog == null) {
            throw new NotFoundException("Sprint Backlog not found.");
        }
        validateProjectOwner(sprintBacklog.getProject().getId().toString());
        validateProjectOwner(epic.getProductBacklog().getProject().getId().toString());
        if (epic.getUserStories().isEmpty()){
            throw new BadRequestException("Epic must have at least one User Story to be linked to a Sprint Backlog");
        }
        epic.setSprintBacklog(sprintBacklog);
        return epicRepository.save(epic);
    }

    public Epic unlinkEpicToSprintBacklog(String epicId) {
        Epic epic = getEpicById(epicId);
        validateProjectOwner(epic.getProductBacklog().getProject().getId().toString());
        epic.setSprintBacklog(null);
        return epicRepository.save(epic);
    }
}
