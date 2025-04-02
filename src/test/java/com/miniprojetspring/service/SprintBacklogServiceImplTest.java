package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.SprintBacklog;
import com.miniprojetspring.Repository.SprintBacklogRepository;
import com.miniprojetspring.Service.Implementation.ProjectSecurityService;
import com.miniprojetspring.Service.Implementation.ProjectServiceImpl;
import com.miniprojetspring.Service.Implementation.SprintBacklogServiceImpl;
import com.miniprojetspring.payload.SprintBacklogPayload;
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
public class SprintBacklogServiceImplTest {

    @Mock
    private SprintBacklogRepository sprintBacklogRepository;

    @Mock
    private ProjectServiceImpl projectServiceImpl;

    @Mock
    private ProjectSecurityService projectSecurityService;

    @InjectMocks
    private SprintBacklogServiceImpl sprintBacklogServiceImpl;

    private SprintBacklogPayload sprintBacklogPayload;
    private Project project;
    private SprintBacklog sprintBacklog;
    private UUID sprintBacklogId;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        projectId = UUID.randomUUID();
        sprintBacklogId = UUID.randomUUID();

        sprintBacklogPayload = new SprintBacklogPayload();
        sprintBacklogPayload.setName("Test Sprint Backlog");

        project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .description("Test Project Description")
                .build();

        sprintBacklog = SprintBacklog.builder()
                .id(sprintBacklogId)
                .name(sprintBacklogPayload.getName())
                .project(project)
                .build();
    }

    @Test
    public void testCreateSprintBacklog_Success() {
        when(projectServiceImpl.getProject()).thenReturn(project);
        when(sprintBacklogRepository.save(any(SprintBacklog.class))).thenReturn(sprintBacklog);

        SprintBacklog actualSprintBacklog = sprintBacklogServiceImpl.createSprintBacklog(sprintBacklogPayload);

        assertNotNull(actualSprintBacklog);
        assertEquals(sprintBacklog.getName(), actualSprintBacklog.getName());
        assertEquals(project, actualSprintBacklog.getProject());

        verify(projectServiceImpl, times(1)).getProject();
        verify(sprintBacklogRepository, times(1)).save(any(SprintBacklog.class));
    }

    @Test
    public void testCreateSprintBacklog_ProjectNotFound() {
        when(projectServiceImpl.getProject()).thenReturn(null);

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.createSprintBacklog(sprintBacklogPayload));

        verify(projectServiceImpl, times(1)).getProject();
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }

    @Test
    public void testGetSprintBacklogById_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectMember(projectId.toString())).thenReturn(true);

        SprintBacklog actualSprintBacklog = sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId.toString());

        assertNotNull(actualSprintBacklog);
        assertEquals(sprintBacklog.getId(), actualSprintBacklog.getId());

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectMember(projectId.toString());
    }

    @Test
    public void testGetSprintBacklogById_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId.toString()));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, never()).isProjectMember(anyString());
    }

    @Test
    public void testGetSprintBacklogById_AccessDenied() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectMember(projectId.toString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId.toString()));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectMember(projectId.toString());
    }

    @Test
    public void testDeleteSprintBacklog_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);
        doNothing().when(sprintBacklogRepository).deleteById(sprintBacklogId);

        sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogId.toString());

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(2)).isProjectOwner(projectId.toString());
        verify(sprintBacklogRepository, times(1)).deleteById(sprintBacklogId);
    }

    @Test
    public void testDeleteSprintBacklog_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogId.toString()));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, never()).isProjectOwner(anyString());
        verify(sprintBacklogRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testDeleteSprintBacklog_AccessDenied() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogId.toString()));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(sprintBacklogRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testUpdateSprintBacklog_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);
        when(sprintBacklogRepository.save(any(SprintBacklog.class))).thenReturn(sprintBacklog);

        SprintBacklog actualSprintBacklog = sprintBacklogServiceImpl.updateSprintBacklog(sprintBacklogId.toString(), sprintBacklogPayload);

        assertNotNull(actualSprintBacklog);
        assertEquals(sprintBacklogPayload.getName(), actualSprintBacklog.getName());

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(2)).isProjectOwner(projectId.toString());
        verify(sprintBacklogRepository, times(1)).save(any(SprintBacklog.class));
    }

    @Test
    public void testUpdateSprintBacklog_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.updateSprintBacklog(sprintBacklogId.toString(), sprintBacklogPayload));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, never()).isProjectOwner(anyString());
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }

    @Test
    public void testUpdateSprintBacklog_AccessDenied() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> sprintBacklogServiceImpl.updateSprintBacklog(sprintBacklogId.toString(), sprintBacklogPayload));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }
}