package com.miniprojetspring.Service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Repository.EpicRepository;
import com.miniprojetspring.payload.CreateEpicPayload;
import com.miniprojetspring.payload.UpdateEpicPayload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EpicService {

    private final EpicRepository epicRepository;
    private final ProductBacklogService productBacklogService;

    public EpicService(EpicRepository epicRepository, ProductBacklogService productBacklogService) {
        this.epicRepository = epicRepository;
        this.productBacklogService = productBacklogService;
    }

    public Epic createEpic(CreateEpicPayload payload) {
        ProductBacklog productBacklog = productBacklogService.getProductBacklogById(UUID.fromString(payload.getProductBacklogId()));
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog not found");
        }
        return epicRepository.save(payload.toEntity(productBacklog));
    }

    public Epic getEpicById(UUID id) {
        return epicRepository.findById(id).orElseThrow(() -> new NotFoundException("Epic not found"));
    }

    public List<Epic> getEpicsByProductBacklogId(UUID productBacklogId) {
        ProductBacklog productBacklog = productBacklogService.getProductBacklogById(productBacklogId);
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog not found");
        }
        return epicRepository.findByProductBacklogId(productBacklogId);
    }

    public void deleteEpic(UUID id) {
        getEpicById(id);
        epicRepository.deleteById(id);
    }

    public Epic updateEpic(UUID id, UpdateEpicPayload payload) {
        Epic epic = getEpicById(id);
        return epicRepository.save(payload.toEntity(epic));
    }
}
