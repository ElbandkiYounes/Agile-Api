package com.miniprojetspring.service;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.Model.Previlige;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.User;
import com.miniprojetspring.Repository.ProjectRepository;
import com.miniprojetspring.Repository.UserRepository;
import com.miniprojetspring.Service.Implementation.ProjectServiceImpl;
import com.miniprojetspring.Service.Implementation.ProjectSecurityService;
import com.miniprojetspring.payload.InviteUserPayload;
import com.miniprojetspring.payload.ProjectPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProjectSecurityService projectSecurityService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private ProjectPayload createPayload;
    private ProjectPayload updatePayload;
    private InviteUserPayload inviteUserPayload;
    private Project project;
    private User currentUser;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        projectId = UUID.randomUUID();

        createPayload = ProjectPayload
                .builder()
                .description("Project Description")
                .name("Project")
                .build();

        updatePayload = ProjectPayload
                .builder()
                .description("Updated Project Description")
                .name("Updated Project")
                .build();

        inviteUserPayload = InviteUserPayload.builder()
                .email("test@gmail.com")
                .fullName("Test User")
                .password("password")
                .previlige(Previlige.DEVELOPER)
                .build();

        currentUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("testuser@example.com")
                .password("password")
                .previlige(Previlige.PRODUCT_OWNER)
                .build();

        project = Project.builder()
                .id(projectId)
                .name(createPayload.getName())
                .description(createPayload.getDescription())
                .owner(currentUser)
                .build();

        currentUser.setProject(project);
        project.getUsers().add(currentUser);
    }

    @Test
    public void testGetProject_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        Project actualProject = projectService.getProject();

        assertNotNull(actualProject);
        assertEquals(project.getId(), actualProject.getId());

        verify(projectSecurityService, times(1)).getCurrentUser();
    }

    @Test
    public void testGetProject_NotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> projectService.getProject());

        verify(projectSecurityService, times(1)).getCurrentUser();
    }

    @Test
    public void testCreateProject_Success() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(projectId);
            return p;
        });
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        Project actualProject = projectService.createProject(createPayload);

        assertNotNull(actualProject);
        assertEquals(project.getName(), actualProject.getName());
        assertEquals(project.getDescription(), actualProject.getDescription());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateProject_Conflict() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(ConflictException.class, () -> projectService.createProject(createPayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(projectRepository, never()).save(any(Project.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateProject_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project actualProject = projectService.updateProject(updatePayload);

        assertNotNull(actualProject);
        assertEquals(updatePayload.getName(), actualProject.getName());
        assertEquals(updatePayload.getDescription(), actualProject.getDescription());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_NotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> projectService.updateProject(updatePayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testDeleteProject_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        doNothing().when(projectRepository).delete(project);

        projectService.deleteProject();

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    public void testDeleteProject_NotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> projectService.deleteProject());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    public void testInviteUser_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        User invitedUser = projectService.inviteUser(inviteUserPayload);

        assertNotNull(invitedUser);
        assertEquals(inviteUserPayload.getEmail(), invitedUser.getEmail());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }


    @Test
    public void testInviteUser_ProjectNotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> projectService.inviteUser(inviteUserPayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testInviteUser_AlreadyInvited() {
        User existingUser = User.builder()
                .email(inviteUserPayload.getEmail())
                .build();
        project.getUsers().add(existingUser);

        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(IllegalArgumentException.class, () -> projectService.inviteUser(inviteUserPayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(projectRepository, never()).save(any(Project.class));
    }
}