package com.miniprojetspring.Service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.payload.CreateProductBacklogPayload;
import com.miniprojetspring.payload.UpdateProductBacklogPayload;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductBacklogService {

    private final ProductBacklogRepository productBacklogRepository;
    private final ProjectService projectService ;

    public ProductBacklogService(ProductBacklogRepository productBacklogRepository, ProjectService projectService) {
        this.productBacklogRepository = productBacklogRepository;
        this.projectService = projectService;
    }

    public ProductBacklog createProductBacklog(CreateProductBacklogPayload payload) {
        Optional<Project> project = projectService.getProjectById(UUID.fromString(payload.getProjectId()));
        if (project.isEmpty()) {
            throw new NotFoundException("Project not found");
        }
        return productBacklogRepository.save(payload.toEntity(project.get()));
    }

    public ProductBacklog getProductBacklogById(UUID id) {
        return productBacklogRepository.findById(id).orElseThrow(() -> new NotFoundException("Product Backlog not found"));
    }

    public void deleteProductBacklog(UUID id) {
        getProductBacklogById(id);
        productBacklogRepository.deleteById(id);
    }

    public ProductBacklog updateProductBacklog(UUID id, UpdateProductBacklogPayload payload) {
        ProductBacklog productBacklog = getProductBacklogById(id);
        return productBacklogRepository.save(payload.ToEntity(productBacklog));
    }
}
