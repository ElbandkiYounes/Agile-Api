package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.ConflictException;
import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.Role;
import com.miniprojetspring.Repository.RoleRepository;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.Service.RoleService;
import com.miniprojetspring.payload.RolePayload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ProjectService projectService;

    public RoleServiceImpl(RoleRepository roleRepository, ProjectService projectService) {
        this.roleRepository = roleRepository;
        this.projectService = projectService;
    }

    @Override
    public Role getRoleByNameAndProjectId(String name, String projectId) {
        Optional<Role> role =  roleRepository.findByNameAndProject_Id(name, UUID.fromString(projectId));
        return role.orElse(null);
    }

    @Override
    public Role getRoleById(String id) {
        return roleRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    @Override
    public Role createRole(String projectId,RolePayload rolePayload) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        Role role = getRoleByNameAndProjectId(rolePayload.getName(), projectId);
        if (role != null) {
            throw new ConflictException("Role already exists");
        }
        return roleRepository.save(rolePayload.toEntity(project));
    }

    @Override
    public Role updateRole(String projectId, String roleId, RolePayload payload) {
        Role existingRole = roleRepository.findById(UUID.fromString(roleId))
                .orElseThrow(() -> new NotFoundException("Role not found"));
        Role role = getRoleByNameAndProjectId(payload.getName(), projectId);
        if (role != null && !role.getId().equals(existingRole.getId())) {
            throw new ConflictException("Role already exists");
        }
        return roleRepository.save(payload.toEntity(existingRole));
    }

    @Override
    public List<Role> getRolesByProjectId(String projectId) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found");
        }
        return roleRepository.findAllByProject_Id(UUID.fromString(projectId));
    }

    @Override
    public void deleteRole(String id) {
        Role role = roleRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Role not found"));
        roleRepository.delete(role);
    }
}