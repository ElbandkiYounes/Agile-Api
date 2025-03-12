package com.miniprojetspring.service;

import com.miniprojetspring.Exception.ConflictException;
import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.Role;
import com.miniprojetspring.Repository.RoleRepository;
import com.miniprojetspring.Service.Implementation.ProjectServiceImpl;
import com.miniprojetspring.Service.Implementation.RoleServiceImpl;
import com.miniprojetspring.payload.RolePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProjectServiceImpl projectService;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RolePayload createPayload;
    private RolePayload updatePayload;
    private Project project;
    private Role role;
    private UUID roleId;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        projectId = UUID.randomUUID();
        roleId = UUID.randomUUID();

        createPayload = new RolePayload();
        createPayload.setName("Test Role");
        createPayload.setDescription("Test Description");

        updatePayload = new RolePayload();
        updatePayload.setName("Updated Role");
        updatePayload.setDescription("Updated Description");

        project = new Project();
        project.setId(projectId);

        role = Role.builder()
                .id(roleId)
                .name(createPayload.getName())
                .description(createPayload.getDescription())
                .project(project)
                .build();
    }

    @Test
    public void testCreateRole_Success() {
        when(projectService.getProjectById(String.valueOf(projectId))).thenReturn(project);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role actualRole = roleService.createRole(projectId.toString(), createPayload);

        assertNotNull(actualRole);
        assertEquals(role.getName(), actualRole.getName());
        assertEquals(role.getDescription(), actualRole.getDescription());
        assertEquals(role.getProject(), actualRole.getProject());

        verify(projectService, times(1)).getProjectById(String.valueOf(projectId));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testCreateRole_ProjectNotFound() {
        UUID notFoundProjectId = UUID.randomUUID();
        when(projectService.getProjectById(String.valueOf(notFoundProjectId))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> roleService.createRole(notFoundProjectId.toString(), createPayload));

        verify(projectService, times(1)).getProjectById(String.valueOf(notFoundProjectId));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testCreateRole_RoleAlreadyExists() {
        when(projectService.getProjectById(String.valueOf(projectId))).thenReturn(project);
        when(roleRepository.findByNameAndProject_Id(createPayload.getName(), projectId)).thenReturn(Optional.of(role));

        assertThrows(ConflictException.class, () -> roleService.createRole(projectId.toString(), createPayload));

        verify(projectService, times(1)).getProjectById(String.valueOf(projectId));
        verify(roleRepository, times(1)).findByNameAndProject_Id(createPayload.getName(), projectId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testGetRoleByNameAndProjectId_Success() {
        when(roleRepository.findByNameAndProject_Id(role.getName(), projectId)).thenReturn(Optional.of(role));

        Role actualRole = roleService.getRoleByNameAndProjectId(role.getName(), projectId.toString());

        assertNotNull(actualRole);
        assertEquals(role.getId(), actualRole.getId());
        assertEquals(role.getName(), actualRole.getName());

        verify(roleRepository, times(1)).findByNameAndProject_Id(role.getName(), projectId);
    }

    @Test
    public void testGetRoleByNameAndProjectId_NotFound() {
        when(roleRepository.findByNameAndProject_Id(role.getName(), projectId)).thenReturn(Optional.empty());

        Role actualRole = roleService.getRoleByNameAndProjectId(role.getName(), projectId.toString());

        assertNull(actualRole);

        verify(roleRepository, times(1)).findByNameAndProject_Id(role.getName(), projectId);
    }

    @Test
    public void testUpdateRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role actualRole = roleService.updateRole(projectId.toString(), roleId.toString(), updatePayload);

        assertNotNull(actualRole);
        assertEquals(updatePayload.getName(), actualRole.getName());
        assertEquals(updatePayload.getDescription(), actualRole.getDescription());

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.updateRole(projectId.toString(), roleId.toString(), updatePayload));

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_RoleAlreadyExists() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Role anotherRole = Role.builder()
                .id(UUID.randomUUID())
                .name(updatePayload.getName())
                .description(updatePayload.getDescription())
                .project(project)
                .build();
        when(roleRepository.findByNameAndProject_Id(updatePayload.getName(), projectId)).thenReturn(Optional.of(anotherRole));

        assertThrows(ConflictException.class, () -> roleService.updateRole(projectId.toString(), roleId.toString(), updatePayload));

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).findByNameAndProject_Id(updatePayload.getName(), projectId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testGetRolesByProjectId_Success() {
        when(projectService.getProjectById(String.valueOf(projectId))).thenReturn(project);
        when(roleRepository.findAllByProject_Id(projectId)).thenReturn(List.of(role));

        List<Role> roles = roleService.getRolesByProjectId(projectId.toString());

        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals(role.getId(), roles.get(0).getId());

        verify(projectService, times(1)).getProjectById(String.valueOf(projectId));
        verify(roleRepository, times(1)).findAllByProject_Id(projectId);
    }

    @Test
    public void testGetRolesByProjectId_ProjectNotFound() {
        when(projectService.getProjectById(String.valueOf(projectId))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> roleService.getRolesByProjectId(projectId.toString()));

        verify(projectService, times(1)).getProjectById(String.valueOf(projectId));
        verify(roleRepository, never()).findAllByProject_Id(projectId);
    }

    @Test
    public void testDeleteRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        roleService.deleteRole(roleId.toString());

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    public void testDeleteRole_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.deleteRole(roleId.toString()));

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).delete(role);
    }

    @Test
    public void testGetRoleById_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Role actualRole = roleService.getRoleById(roleId.toString());

        assertNotNull(actualRole);
        assertEquals(role.getId(), actualRole.getId());
        assertEquals(role.getName(), actualRole.getName());

        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    public void testGetRoleById_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.getRoleById(roleId.toString()));

        verify(roleRepository, times(1)).findById(roleId);
    }
}