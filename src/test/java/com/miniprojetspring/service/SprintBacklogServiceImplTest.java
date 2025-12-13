package com.miniprojetspring.service;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.SprintBacklog;
import com.miniprojetspring.repository.SprintBacklogRepository;
import com.miniprojetspring.service.implementation.ProjectSecurityService;
import com.miniprojetspring.service.implementation.ProjectServiceImpl;
import com.miniprojetspring.service.implementation.SprintBacklogServiceImpl;
import com.miniprojetspring.payload.SprintBacklogPayload;
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
class SprintBacklogServiceImplTest {

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
    void setUp() {
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
    void testCreateSprintBacklog_Success() {
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
    void testCreateSprintBacklog_ProjectNotFound() {
        when(projectServiceImpl.getProject()).thenReturn(null);

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.createSprintBacklog(sprintBacklogPayload));

        verify(projectServiceImpl, times(1)).getProject();
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }

    @Test
    void testGetSprintBacklogById_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectMember(projectId.toString())).thenReturn(true);

        SprintBacklog actualSprintBacklog = sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId.toString());

        assertNotNull(actualSprintBacklog);
        assertEquals(sprintBacklog.getId(), actualSprintBacklog.getId());

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectMember(projectId.toString());
    }

    @Test
    void testGetSprintBacklogById_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        String sprintBacklogIdString = sprintBacklogId.toString();
        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogIdString));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, never()).isProjectMember(anyString());
    }

    @Test
    void testGetSprintBacklogById_AccessDenied() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectMember(projectId.toString())).thenReturn(false);

        String sprintBacklogIdString = sprintBacklogId.toString();
        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogIdString));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectMember(projectId.toString());
    }

    @Test
    void testDeleteSprintBacklog_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(true);
        doNothing().when(sprintBacklogRepository).deleteById(sprintBacklogId);

        sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogId.toString());

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(2)).isProjectOwner(projectId.toString());
        verify(sprintBacklogRepository, times(1)).deleteById(sprintBacklogId);
    }

    @Test
    void testDeleteSprintBacklog_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        String sprintBacklogIdString = sprintBacklogId.toString();
        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogIdString));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, never()).isProjectOwner(anyString());
        verify(sprintBacklogRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testDeleteSprintBacklog_AccessDenied() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(false);

        String sprintBacklogIdString = sprintBacklogId.toString();
        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogIdString));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(sprintBacklogRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testUpdateSprintBacklog_Success() {
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
    void testUpdateSprintBacklog_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        String sprintBacklogIdString = sprintBacklogId.toString();
        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.updateSprintBacklog(sprintBacklogIdString, sprintBacklogPayload));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, never()).isProjectOwner(anyString());
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }

    @Test
    void testUpdateSprintBacklog_AccessDenied() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(projectSecurityService.isProjectOwner(projectId.toString())).thenReturn(false);

        String sprintBacklogIdString = sprintBacklogId.toString();
        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.updateSprintBacklog(sprintBacklogIdString, sprintBacklogPayload));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(projectSecurityService, times(1)).isProjectOwner(projectId.toString());
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }
}