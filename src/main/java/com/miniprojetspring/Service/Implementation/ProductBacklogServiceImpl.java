package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.ProductBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.ProjectService;
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

    public ProductBacklog createProductBacklog(ProductBacklogPayload payload) {
        User owner = projectSecurityService.getCurrentUser();
        Project project = owner.getProject();
        if(project == null) {
            throw new NotFoundException("Owner does not have a project yet");
        }
        if(project.getProductBacklog() != null) {
            throw new NotFoundException("Product Backlog already exists for this project");
        }
        ProductBacklog productBacklog = payload.toEntity();
        projectServiceImpl.linkProductBacklogToProject(productBacklog);
        productBacklog.setProject(project);
        return productBacklogRepository.save(productBacklog);
    }

    public ProductBacklog getProductBacklog() {
        User user = projectSecurityService.getCurrentUser();
        Project project = user.getProject();
        if(project == null) {
            throw new NotFoundException("Owner does not have a project yet");
        }
        if(project.getProductBacklog() == null) {
            throw new NotFoundException("Product Backlog does not exist for this user's project");
        }
        return project.getProductBacklog();
    }

    public void deleteProductBacklog() {
        User user = projectSecurityService.getCurrentUser();
        Project project = user.getProject();
        if(project == null) {
            throw new NotFoundException("Owner does not have a project yet");
        }
        if(project.getProductBacklog() == null) {
            throw new NotFoundException("Product Backlog does not exist for this user's project");
        }
        productBacklogRepository.delete(project.getProductBacklog());

    }

    public ProductBacklog updateProductBacklog(ProductBacklogPayload payload) {
        User owner = projectSecurityService.getCurrentUser();
        Project project = owner.getProject();
        if(project == null) {
            throw new NotFoundException("Owner does not have a project yet");
        }
        if(project.getProductBacklog() == null) {
            throw new NotFoundException("Product Backlog does not exist for this user's project");
        }
        ProductBacklog productBacklog = project.getProductBacklog();
        return productBacklogRepository.save(payload.ToEntity(productBacklog));
    }
}
