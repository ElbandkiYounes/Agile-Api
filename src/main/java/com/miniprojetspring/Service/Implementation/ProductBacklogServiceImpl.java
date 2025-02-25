package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.payload.CreateProductBacklogPayload;
import com.miniprojetspring.payload.UpdateProductBacklogPayload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductBacklogServiceImpl implements ProductBacklogService {

    private final ProductBacklogRepository productBacklogRepository;
    private final ProjectServiceImpl projectServiceImpl;

    public ProductBacklogServiceImpl(ProductBacklogRepository productBacklogRepository, ProjectServiceImpl projectServiceImpl) {
        this.productBacklogRepository = productBacklogRepository;
        this.projectServiceImpl = projectServiceImpl;
    }

    public ProductBacklog createProductBacklog(CreateProductBacklogPayload payload) {
        Project project = projectServiceImpl.getProjectById(UUID.fromString(payload.getProjectId()));
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        return productBacklogRepository.save(payload.toEntity(project));
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
