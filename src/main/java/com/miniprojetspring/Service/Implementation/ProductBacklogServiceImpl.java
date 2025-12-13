package com.miniprojetspring.service.implementation;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.ProductBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.repository.ProductBacklogRepository;
import com.miniprojetspring.service.ProductBacklogService;
import com.miniprojetspring.service.ProjectService;
import com.miniprojetspring.payload.ProductBacklogPayload;
import org.springframework.stereotype.Service;

@Service
public class ProductBacklogServiceImpl implements ProductBacklogService {

    private final ProductBacklogRepository productBacklogRepository;
    private final ProjectService projectServiceImpl;
    private final ProjectSecurityService projectSecurityService;

    public ProductBacklogServiceImpl(ProductBacklogRepository productBacklogRepository, ProjectService projectServiceImpl, ProjectSecurityService projectSecurityService) {
        this.productBacklogRepository = productBacklogRepository;
        this.projectServiceImpl = projectServiceImpl;
        this.projectSecurityService = projectSecurityService;
    }

    private Project getUserProject() {
        User user = projectSecurityService.getCurrentUser();
        Project project = user.getProject();
        if (project == null) {
            throw new NotFoundException("Owner does not have a project yet");
        }
        return project;
    }

    private ProductBacklog getExistingProductBacklog(Project project) {
        ProductBacklog productBacklog = project.getProductBacklog();
        if (productBacklog == null) {
            throw new NotFoundException("Product Backlog does not exist for this user's project");
        }
        return productBacklog;
    }

    public ProductBacklog createProductBacklog(ProductBacklogPayload payload) {
        Project project = getUserProject();
        if(project.getProductBacklog() != null) {
            throw new NotFoundException("Product Backlog already exists for this project");
        }
        ProductBacklog productBacklog = payload.toEntity();
        projectServiceImpl.linkProductBacklogToProject(productBacklog);
        productBacklog.setProject(project);
        return productBacklogRepository.save(productBacklog);
    }

    public ProductBacklog getProductBacklog() {
        Project project = getUserProject();
        return getExistingProductBacklog(project);
    }

    public void deleteProductBacklog() {
        Project project = getUserProject();
        ProductBacklog productBacklog = getExistingProductBacklog(project);
        productBacklogRepository.delete(productBacklog);
    }

    public ProductBacklog updateProductBacklog(ProductBacklogPayload payload) {
        Project project = getUserProject();
        ProductBacklog productBacklog = getExistingProductBacklog(project);
        return productBacklogRepository.save(payload.toEntity(productBacklog));
    }
}
