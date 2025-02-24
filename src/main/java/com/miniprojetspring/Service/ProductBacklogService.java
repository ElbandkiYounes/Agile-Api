package com.miniprojetspring.Service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProductBacklogRepositoryImp;
import com.miniprojetspring.payload.CreateProductBacklogPayload;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductBacklogService {

    private final ProductBacklogRepositoryImp productBacklogRepositoryImp;
    private final ProjectService projectService ;

    public ProductBacklogService(ProductBacklogRepositoryImp productBacklogRepositoryImp, ProjectService projectService) {
        this.productBacklogRepositoryImp = productBacklogRepositoryImp;
        this.projectService = projectService;
    }

    public ProductBacklog createProductBacklog(CreateProductBacklogPayload payload) {
        Optional<Project> project = projectService.getProjectById(UUID.fromString(payload.getProjectId()));
        if (project.isEmpty()) {
            throw new NotFoundException("Project not found");
        }
        return productBacklogRepositoryImp.save(payload.toEntity(project.get()));
    }
}
