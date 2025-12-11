package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.model.Previlige;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.Repository.ProjectRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;

    public ProjectSecurityService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Checks if the current user is the owner of the specified project
     */
    public boolean isProjectOwner(String projectId) {
        User currentUser = getCurrentUser();
        Project project = projectRepository.findById(UUID.fromString(projectId))
                .orElseThrow(() -> new AccessDeniedException("Project not found"));

        return project.getOwner().getId().equals(currentUser.getId());
    }

    /**
     * Checks if the current user has a specific privilege
     */
    public boolean hasPrivilege(Previlige privilege) {
        User currentUser = getCurrentUser();
        return currentUser.getPrevilige() == privilege;
    }

    /**
     * Checks if the current user is part of the project
     */
    public boolean isProjectMember(String projectId) {
        User currentUser = getCurrentUser();
        Project project = projectRepository.findById(UUID.fromString(projectId))
                .orElseThrow(() -> new AccessDeniedException("Project not found"));

        return project.getUsers().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));
    }

    /**
     * Get the current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        return (User) authentication.getPrincipal();
    }

    /**
     * Verify that a user can perform specific actions on a project
     * - PRODUCT_OWNER can do anything with their own projects
     * - Other roles have specific permissions
     */
    public void checkProjectAccess(String projectId, Previlige minimumPrivilege) {
        User currentUser = getCurrentUser();

        // PRODUCT_OWNER can do anything to their own projects
        if (currentUser.getPrevilige() == Previlige.PRODUCT_OWNER && isProjectOwner(projectId)) {
            return;
        }

        // Otherwise, check if user has at least the minimum required privilege
        // and is a member of the project
        if (currentUser.getPrevilige().ordinal() <= minimumPrivilege.ordinal() && isProjectMember(projectId)) {
            return;
        }

        throw new AccessDeniedException("Insufficient privileges for this operation");
    }
}