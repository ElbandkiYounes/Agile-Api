package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.ProductBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.Repository.UserRepository;
import com.miniprojetspring.payload.InviteUserPayload;
import com.miniprojetspring.Repository.ProjectRepository;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.payload.ProjectPayload;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private ProjectSecurityService projectSecurityService;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              ProjectSecurityService projectSecurityService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectSecurityService = projectSecurityService;

    }

    public Project getProject() {
        User currentUser = projectSecurityService.getCurrentUser();
        if(currentUser.getProject() == null) {
            throw new NotFoundException("User does not have a project");
        }
        return currentUser.getProject();
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
        Project project = getProject();
        System.out.println(project.getId());
        project.setProductBacklog(productBacklog);
        return projectRepository.save(project);
    }

    public Project updateProject(ProjectPayload payload) {
        Project existingProject = getProject();
        return projectRepository.save(payload.toEntity(existingProject));
    }

    public void deleteProject() {
        Project project = getProject();
        projectRepository.delete(project);
    }

    @Transactional
    public User inviteUser(InviteUserPayload userPayload) {
        User currentUser = projectSecurityService.getCurrentUser();
        Project project = currentUser.getProject();

        if(project == null) {
            throw new NotFoundException("User does not have a project");
        }

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
