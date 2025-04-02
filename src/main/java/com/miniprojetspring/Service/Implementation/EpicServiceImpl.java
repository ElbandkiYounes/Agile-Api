package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.BadRequestException;
import com.miniprojetspring.Exception.ConflictException;
import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.SprintBacklog;
import com.miniprojetspring.Repository.EpicRepository;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.SprintBacklogService;
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

    public Epic createEpic(EpicPayload payload) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklog();
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog not found");
        }
        return epicRepository.save(payload.toEntity(productBacklog));
    }

    public Epic getEpicById(String id) {
        Epic epic = epicRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Epic not found"));
        if (!projectSecurityService.isProjectMember(epic.getProductBacklog().getProject().getId().toString())
        && !projectSecurityService.isProjectOwner(epic.getProductBacklog().getProject().getId().toString())) {
            throw new AccessDeniedException("Epic not found");
        }
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
        getEpicById(id);
        if(!projectSecurityService.isProjectOwner(getEpicById(id).getProductBacklog().getProject().getId().toString())){
            throw new AccessDeniedException("Epic not found");
        }
        epicRepository.deleteById(UUID.fromString(id));
    }

    public Epic updateEpic(String id, EpicPayload payload) {
        Epic epic = getEpicById(id);
        if(!projectSecurityService.isProjectOwner(epic.getProductBacklog().getProject().getId().toString())){
            throw new AccessDeniedException("Epic not found");
        }
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
        if (!projectSecurityService.isProjectOwner(sprintBacklog.getProject().getId().toString())){
            throw new AccessDeniedException("User doesnt have access to this Sprint Backlog");
        }
        if(!projectSecurityService.isProjectOwner(epic.getProductBacklog().getProject().getId().toString())){
            throw new AccessDeniedException("User doesnt have access to this Epic");
        }
        if (epic.getUserStories().isEmpty()){
            throw new BadRequestException("Epic must have at least one User Story to be linked to a Sprint Backlog");
        }
        epic.setSprintBacklog(sprintBacklog);
        return epicRepository.save(epic);
    }

    public Epic unlinkEpicToSprintBacklog(String epicId) {
        Epic epic = getEpicById(epicId);
        if(!projectSecurityService.isProjectOwner(epic.getProductBacklog().getProject().getId().toString())){
            throw new AccessDeniedException("User does not have access to this Epic");
        }
        epic.setSprintBacklog(null);
        return epicRepository.save(epic);
    }
}
