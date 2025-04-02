package com.miniprojetspring.service;

import com.miniprojetspring.Exception.ConflictException;
import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.Role;
import com.miniprojetspring.Model.User;
import com.miniprojetspring.Repository.ProjectRepository;
import com.miniprojetspring.Repository.RoleRepository;
import com.miniprojetspring.Service.Implementation.RoleServiceImpl;
import com.miniprojetspring.Service.Implementation.ProjectSecurityService;
import com.miniprojetspring.payload.RolePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

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
    public void setUp() {
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
    public void testGetRoleByNameAndProjectId_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());
        when(roleRepository.findByNameAndProject_Id(createPayload.getName(), projectId)).thenReturn(Optional.of(role));

        Role actualRole = roleService.getRoleByNameAndProjectId(createPayload.getName());

        assertNotNull(actualRole);
        assertEquals(role.getId(), actualRole.getId());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(roleRepository, times(1)).findByNameAndProject_Id(createPayload.getName(), projectId);
    }

    @Test
    public void testGetRoleByNameAndProjectId_NotFound() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());
        when(roleRepository.findByNameAndProject_Id(createPayload.getName(), projectId)).thenReturn(Optional.empty());

        Role actualRole = roleService.getRoleByNameAndProjectId(createPayload.getName());

        assertNull(actualRole);

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(roleRepository, times(1)).findByNameAndProject_Id(createPayload.getName(), projectId);
    }

    @Test
    public void testGetRoleById_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(projectSecurityService.isProjectMember(projectId.toString())).thenReturn(true);

        Role actualRole = roleService.getRoleById(roleId.toString());

        assertNotNull(actualRole);
        assertEquals(role.getId(), actualRole.getId());

        verify(roleRepository, times(1)).findById(roleId);
        verify(projectSecurityService, times(1)).isProjectMember(projectId.toString());
    }

    @Test
    public void testGetRoleById_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.getRoleById(roleId.toString()));

        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    public void testCreateRole_Success() {
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
    public void testCreateRole_ProjectNotFound() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(null).build());

        assertThrows(NotFoundException.class, () -> roleService.createRole(createPayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(roleRepository, never()).save(any(Role.class));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testCreateRole_RoleAlreadyExists() {
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());
        when(roleRepository.findByNameAndProject_Id(createPayload.getName(), projectId)).thenReturn(Optional.of(role));

        assertThrows(ConflictException.class, () -> roleService.createRole(createPayload));

        verify(projectSecurityService, times(2)).getCurrentUser();
        verify(roleRepository, times(1)).findByNameAndProject_Id(createPayload.getName(), projectId);
        verify(roleRepository, never()).save(any(Role.class));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testUpdateRole_Success() {
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
    public void testUpdateRole_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.updateRole(roleId.toString(), updatePayload));

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_RoleAlreadyExists() {
        Role anotherRole = Role.builder().id(UUID.randomUUID()).name(updatePayload.getName()).project(project).build();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);
        when(roleRepository.findByNameAndProject_Id(updatePayload.getName(), projectId)).thenReturn(Optional.of(anotherRole));
        when(projectSecurityService.getCurrentUser()).thenReturn(User.builder().project(project).build());

        assertThrows(ConflictException.class, () -> roleService.updateRole(roleId.toString(), updatePayload));

        verify(roleRepository, times(1)).findById(roleId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(roleRepository, times(1)).findByNameAndProject_Id(updatePayload.getName(), projectId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testDeleteRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);

        roleService.deleteRole(roleId.toString());

        verify(roleRepository, times(1)).findById(roleId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    public void testDeleteRole_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.deleteRole(roleId.toString()));

        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).delete(any(Role.class));
    }
}