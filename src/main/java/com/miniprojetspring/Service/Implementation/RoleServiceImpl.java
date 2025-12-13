package com.miniprojetspring.service.implementation;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.Role;
import com.miniprojetspring.repository.ProjectRepository;
import com.miniprojetspring.repository.RoleRepository;
import com.miniprojetspring.service.ProjectService;
import com.miniprojetspring.service.RoleService;
import com.miniprojetspring.payload.RolePayload;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ProjectSecurityService projectSecurityService;

    public RoleServiceImpl(RoleRepository roleRepository, ProjectService projectService, ProjectRepository projectRepository, ProjectSecurityService projectSecurityService) {
        this.roleRepository = roleRepository;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.projectSecurityService = projectSecurityService;
    }

    private void validateProjectAccess(String projectId) {
        if (!projectSecurityService.isProjectMember(projectId)
                && !projectSecurityService.isProjectOwner(projectId)) {
            throw new AccessDeniedException("Access denied to project");
        }
    }

    private void validateProjectOwner(String projectId) {
        if (!projectSecurityService.isProjectOwner(projectId)) {
            throw new AccessDeniedException("Only project owner can perform this action");
        }
    }

    @Override
    public Role getRoleByNameAndProjectId(String name) {
        Project project = projectSecurityService.getCurrentUser().getProject();
        if(project == null) {
            throw new NotFoundException("User doesnt have a project");
        }
        Optional<Role> role =  roleRepository.findByNameAndProject_Id(name, project.getId());
        return role.orElse(null);
    }

    @Override
    public Role getRoleById(String id) {
        Optional<Role> role =  roleRepository.findById(UUID.fromString(id));
        if(role.isEmpty()) {
            throw new NotFoundException("Role not found");
        }
        validateProjectAccess(role.get().getProjectId().toString());
        return role.get();
    }

    public Role createRole(RolePayload rolePayload) {
        Project project = projectSecurityService.getCurrentUser().getProject();
        if (project == null) {
            throw new NotFoundException("User doesnt have a project");
        }
        Role role = getRoleByNameAndProjectId(rolePayload.getName());
        if (role != null) {
            throw new ConflictException("Role already exists");
        }

        role =  roleRepository.save(rolePayload.toEntity(project));
        project.getRoles().add(role);
        projectRepository.save(project);
        return role;

    }

    @Override
    public Role updateRole(String roleId, RolePayload payload) {
        Role existingRole = roleRepository.findById(UUID.fromString(roleId))
                .orElseThrow(() -> new NotFoundException("Role not found"));
        validateProjectOwner(existingRole.getProjectId().toString());
        Role role = getRoleByNameAndProjectId(payload.getName());
        if (role != null && !role.getId().equals(existingRole.getId())) {
            throw new ConflictException("Role already exists");
        }
        return roleRepository.save(payload.toEntity(existingRole));
    }

    public List<Role> getRoles() {
        Project project = projectService.getProject();
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        return roleRepository.findAllByProject_Id(project.getId());
    }

    @Override
    public void deleteRole(String id) {
        Role role = roleRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Role not found"));
        validateProjectOwner(role.getProjectId().toString());
        roleRepository.delete(role);
    }
}