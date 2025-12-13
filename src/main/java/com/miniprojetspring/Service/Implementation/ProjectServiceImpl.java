package com.miniprojetspring.service.implementation;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.ProductBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.repository.UserRepository;
import com.miniprojetspring.payload.InviteUserPayload;
import com.miniprojetspring.repository.ProjectRepository;
import com.miniprojetspring.service.ProjectService;
import com.miniprojetspring.payload.ProjectPayload;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectSecurityService projectSecurityService;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              ProjectSecurityService projectSecurityService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectSecurityService = projectSecurityService;

    }

    private Project getCurrentUserProject() {
        User currentUser = projectSecurityService.getCurrentUser();
        if (currentUser.getProject() == null) {
            throw new NotFoundException("User does not have a project");
        }
        return currentUser.getProject();
    }

    public Project getProject() {
        return getCurrentUserProject();
    }

    public Project createProject(ProjectPayload payload) {
        User currentUser = projectSecurityService.getCurrentUser();
        if(currentUser.getProject() != null) {
            throw new ConflictException("User already has a project");
        }
        Project project =  projectRepository.save(payload.toEntity(currentUser));
        currentUser.setProject(project);
        userRepository.save(currentUser);
        return project;
    }

    public Project linkProductBacklogToProject(ProductBacklog productBacklog) {
        Project project = getCurrentUserProject();
        project.setProductBacklog(productBacklog);
        return projectRepository.save(project);
    }

    public Project updateProject(ProjectPayload payload) {
        Project existingProject = getCurrentUserProject();
        return projectRepository.save(payload.toEntity(existingProject));
    }

    public void deleteProject() {
        Project project = getCurrentUserProject();
        projectRepository.delete(project);
    }

    @Transactional
    public User inviteUser(InviteUserPayload userPayload) {
        Project project = getCurrentUserProject();

        if (project.getUsers().stream().anyMatch(user -> user.getEmail().equals(userPayload.getEmail()))) {
            throw new IllegalArgumentException("User already invited");
        }
        User user = userPayload.toUser(project);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        project.getUsers().add(user);
        projectRepository.save(project);

        return userRepository.save(user);
    }

}
