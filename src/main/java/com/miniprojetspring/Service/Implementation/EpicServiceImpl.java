package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.BadRequestException;
import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.SprintBacklog;
import com.miniprojetspring.Repository.EpicRepository;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.SprintBacklogService;
import com.miniprojetspring.payload.EpicPayload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EpicServiceImpl implements EpicService {

    private final EpicRepository epicRepository;
    private final ProductBacklogService productBacklogServiceImpl;
    private final SprintBacklogService sprintBacklogServiceImpl;

    public EpicServiceImpl(EpicRepository epicRepository,
                           ProductBacklogService productBacklogServiceImpl,
                           SprintBacklogService sprintBacklogServiceImpl) {
        this.epicRepository = epicRepository;
        this.productBacklogServiceImpl = productBacklogServiceImpl;
        this.sprintBacklogServiceImpl = sprintBacklogServiceImpl;
    }

    public Epic createEpic(String productBacklogId, EpicPayload payload) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklogById(productBacklogId);
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog not found");
        }
        return epicRepository.save(payload.toEntity(productBacklog));
    }

    public Epic getEpicById(String id) {
        return epicRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Epic not found"));
    }

    public List<Epic> getEpicsByProductBacklogId(String productBacklogId) {
        ProductBacklog productBacklog = productBacklogServiceImpl.getProductBacklogById(productBacklogId);
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog not found");
        }
        return epicRepository.findByProductBacklogId(UUID.fromString(productBacklogId));
    }

    public void deleteEpic(String id) {
        getEpicById(id);
        epicRepository.deleteById(UUID.fromString(id));
    }

    public Epic updateEpic(String id, EpicPayload payload) {
        Epic epic = getEpicById(id);
        return epicRepository.save(payload.toEntity(epic));
    }

    public Epic linkEpicToSprintBacklog(String sprintBacklogId, String epicId) {
        Epic epic = getEpicById(epicId);
        SprintBacklog sprintBacklog = sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId);

        if (sprintBacklog == null) {
            throw new NotFoundException("Sprint Backlog not found.");
        }
        if (sprintBacklog.getProject().getId() != epic.getProductBacklog().getProject().getId()) {
            throw new NotFoundException("Sprint Backlog and Epic not on the same Project");
        }
        if (epic.getUserStory().isEmpty()){
            throw new BadRequestException("Epic must have at least one User Story to be linked to a Sprint Backlog");
        }
        epic.setSprintBacklog(sprintBacklog);
        return epicRepository.save(epic);
    }

    public Epic unlinkEpicToSprintBacklog(String epicId) {
        Epic epic = getEpicById(epicId);
        epic.setSprintBacklog(null);
        return epicRepository.save(epic);
    }
}
