package com.miniprojetspring.service;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.Role;
import com.miniprojetspring.model.User;
import com.miniprojetspring.repository.ProjectRepository;
import com.miniprojetspring.repository.RoleRepository;
import com.miniprojetspring.service.implementation.RoleServiceImpl;
import com.miniprojetspring.service.implementation.ProjectSecurityService;
import com.miniprojetspring.payload.RolePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectSecurityService projectSecurityService;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RolePayload createPayload;
    private RolePayload updatePayload;
    private Project project;
    private Role role;
    private UUID roleId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        roleId = UUID.randomUUID();

        createPayload = new RolePayload();
        createPayload.setName("Test Role");

        updatePayload = new RolePayload();
        updatePayload.setName("Updated Role");

        project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .description("Test Project Description")
                .build();

        role = Role.builder()
                .id(roleId)
                .name(createPayload.getName())
                .project(project)
                .build();
    }

    @Test
    void testGetRoleByNameAndProjectId_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());
        when(roleRepository.findByNameAndProject_Id(createPayload.getName(), projectId)).thenReturn(Optional.of(role));

        Role actualRole = roleService.getRoleByNameAndProjectId(createPayload.getName());

        assertNotNull(actualRole);
        assertEquals(role.getId(), actualRole.getId());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(roleRepository, times(1)).findByNameAndProject_Id(createPayload.getName(), projectId);
    }

    @Test
    void testGetRoleByNameAndProjectId_NotFound() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());
        when(roleRepository.findByNameAndProject_Id(createPayload.getName(), projectId)).thenReturn(Optional.empty());

        Role actualRole = roleService.getRoleByNameAndProjectId(createPayload.getName());

        assertNull(actualRole);

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(roleRepository, times(1)).findByNameAndProject_Id(createPayload.getName(), projectId);
    }

    @Test
    void testGetRoleById_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(projectSecurityService.isProjectMember(projectId.toString())).thenReturn(true);

        Role actualRole = roleService.getRoleById(roleId.toString());

        assertNotNull(actualRole);
        assertEquals(role.getId(), actualRole.getId());

        verify(roleRepository, times(1)).findById(roleId);
        verify(projectSecurityService, times(1)).isProjectMember(projectId.toString());
    }

    @Test
    void testGetRoleById_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        String roleIdString = roleId.toString();
        assertThrows(NotFoundException.class, () -> roleService.getRoleById(roleIdString));

        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testCreateRole_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role actualRole = roleService.createRole(createPayload);

        assertNotNull(actualRole);
        assertEquals(role.getName(), actualRole.getName());

        verify(projectSecurityService, times(2)).getCurrentUser();
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testCreateRole_ProjectNotFound() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(null).build());

        assertThrows(NotFoundException.class, () -> roleService.createRole(createPayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(roleRepository, never()).save(any(Role.class));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testCreateRole_RoleAlreadyExists() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());
        when(roleRepository.findByNameAndProject_Id(createPayload.getName(), projectId)).thenReturn(Optional.of(role));

        assertThrows(ConflictException.class, () -> roleService.createRole(createPayload));

        verify(projectSecurityService, times(2)).getCurrentUser();
        verify(roleRepository, times(1)).findByNameAndProject_Id(createPayload.getName(), projectId);
        verify(roleRepository, never()).save(any(Role.class));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testUpdateRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());

        Role actualRole = roleService.updateRole(roleId.toString(), updatePayload);

        assertNotNull(actualRole);
        assertEquals(updatePayload.getName(), actualRole.getName());

        verify(roleRepository, times(1)).findById(roleId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testUpdateRole_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        String roleIdString = roleId.toString();
        assertThrows(NotFoundException.class, () -> roleService.updateRole(roleIdString, updatePayload));

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdateRole_RoleAlreadyExists() {
        Role anotherRole = Role.builder().id(UUID.randomUUID()).name(updatePayload.getName()).project(project).build();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);
        when(roleRepository.findByNameAndProject_Id(updatePayload.getName(), projectId)).thenReturn(Optional.of(anotherRole));
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());

        String roleIdString = roleId.toString();
        assertThrows(ConflictException.class, () -> roleService.updateRole(roleIdString, updatePayload));

        verify(roleRepository, times(1)).findById(roleId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(roleRepository, times(1)).findByNameAndProject_Id(updatePayload.getName(), projectId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDeleteRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);

        roleService.deleteRole(roleId.toString());

        verify(roleRepository, times(1)).findById(roleId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void testDeleteRole_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        String roleIdString = roleId.toString();
        assertThrows(NotFoundException.class, () -> roleService.deleteRole(roleIdString));

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).delete(any(Role.class));
    }
}