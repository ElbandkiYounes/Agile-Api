package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.payload.ProductBacklogPayload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductBacklogServiceImpl implements ProductBacklogService {

    private final ProductBacklogRepository productBacklogRepository;
    private final ProjectService projectServiceImpl;

    public ProductBacklogServiceImpl(ProductBacklogRepository productBacklogRepository, ProjectService projectServiceImpl) {
        this.productBacklogRepository = productBacklogRepository;
        this.projectServiceImpl = projectServiceImpl;
    }

    public ProductBacklog createProductBacklog(String projectId,ProductBacklogPayload payload) {
        Project project = projectServiceImpl.getProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        return productBacklogRepository.save(payload.toEntity(project));
    }

    public ProductBacklog getProductBacklogById(String id) {
        return productBacklogRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Product Backlog not found"));
    }

    public void deleteProductBacklog(String id) {
        getProductBacklogById(id);
        productBacklogRepository.deleteById(UUID.fromString(id));
    }

    public ProductBacklog updateProductBacklog(String id, ProductBacklogPayload payload) {
        ProductBacklog productBacklog = getProductBacklogById(id);
        return productBacklogRepository.save(payload.ToEntity(productBacklog));
    }
}
