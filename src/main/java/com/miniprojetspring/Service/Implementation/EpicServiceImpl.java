package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Repository.EpicRepository;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.payload.EpicPayload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EpicServiceImpl implements EpicService {

    private final EpicRepository epicRepository;
    private final ProductBacklogServiceImpl productBacklogServiceImpl;

    public EpicServiceImpl(EpicRepository epicRepository, ProductBacklogServiceImpl productBacklogServiceImpl) {
        this.epicRepository = epicRepository;
        this.productBacklogServiceImpl = productBacklogServiceImpl;
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
}
