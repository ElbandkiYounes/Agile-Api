package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.SprintBacklog;
import com.miniprojetspring.Repository.SprintBacklogRepository;
import com.miniprojetspring.Service.Implementation.ProjectServiceImpl;
import com.miniprojetspring.Service.Implementation.SprintBacklogServiceImpl;
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
public class SprintBacklogServiceTest {

    @Mock
    private SprintBacklogRepository sprintBacklogRepository;

    @Mock
    private ProjectServiceImpl projectServiceImpl;

    @InjectMocks
    private SprintBacklogServiceImpl sprintBacklogServiceImpl;

    private SprintBacklogPayload sprintBacklogPayload;
    private Project project;
    private SprintBacklog sprintBacklog;
    private UUID sprintBacklogId;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        sprintBacklogId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        sprintBacklogPayload = new SprintBacklogPayload();
        sprintBacklogPayload.setName("Test Sprint Backlog");
        sprintBacklogPayload.setDescription("Test Description");
        sprintBacklogPayload.setProjectId(projectId.toString());

        project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .description("Test Project Description")
                .build();

        sprintBacklog = SprintBacklog.builder()
                .id(sprintBacklogId)
                .name(sprintBacklogPayload.getName())
                .description(sprintBacklogPayload.getDescription())
                .project(project)
                .build();
    }

    @Test
    public void testCreateSprintBacklog_Success() {
        when(projectServiceImpl.getProjectById(projectId.toString())).thenReturn(project);
        when(sprintBacklogRepository.save(any(SprintBacklog.class))).thenReturn(sprintBacklog);

        SprintBacklog createdSprintBacklog = sprintBacklogServiceImpl.createSprintBacklog(projectId.toString(), sprintBacklogPayload);

        assertNotNull(createdSprintBacklog);
        assertEquals(sprintBacklog.getName(), createdSprintBacklog.getName());
        assertEquals(sprintBacklog.getDescription(), createdSprintBacklog.getDescription());

        verify(projectServiceImpl, times(1)).getProjectById(projectId.toString());
        verify(sprintBacklogRepository, times(1)).save(any(SprintBacklog.class));
    }

    @Test
    public void testCreateSprintBacklog_ProjectNotFound() {
        when(projectServiceImpl.getProjectById(projectId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.createSprintBacklog(projectId.toString(), sprintBacklogPayload));

        verify(projectServiceImpl, times(1)).getProjectById(projectId.toString());
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }

    @Test
    public void testGetSprintBacklogById_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));

        SprintBacklog retrievedSprintBacklog = sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId.toString());

        assertNotNull(retrievedSprintBacklog);
        assertEquals(sprintBacklog.getId(), retrievedSprintBacklog.getId());

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
    }

    @Test
    public void testGetSprintBacklogById_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.getSprintBacklogById(sprintBacklogId.toString()));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
    }

    @Test
    public void testDeleteSprintBacklog_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        doNothing().when(sprintBacklogRepository).deleteById(sprintBacklogId);

        assertDoesNotThrow(() -> sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogId.toString()));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(sprintBacklogRepository, times(1)).deleteById(sprintBacklogId);
    }

    @Test
    public void testDeleteSprintBacklog_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.deleteSprintBacklog(sprintBacklogId.toString()));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(sprintBacklogRepository, never()).deleteById(sprintBacklogId);
    }

    @Test
    public void testUpdateSprintBacklog_Success() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.of(sprintBacklog));
        when(sprintBacklogRepository.save(any(SprintBacklog.class))).thenReturn(sprintBacklog);

        SprintBacklog updatedSprintBacklog = sprintBacklogServiceImpl.updateSprintBacklog(sprintBacklogId.toString(), sprintBacklogPayload);

        assertNotNull(updatedSprintBacklog);
        assertEquals(sprintBacklogPayload.getName(), updatedSprintBacklog.getName());
        assertEquals(sprintBacklogPayload.getDescription(), updatedSprintBacklog.getDescription());

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(sprintBacklogRepository, times(1)).save(any(SprintBacklog.class));
    }

    @Test
    public void testUpdateSprintBacklog_NotFound() {
        when(sprintBacklogRepository.findById(sprintBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sprintBacklogServiceImpl.updateSprintBacklog(sprintBacklogId.toString(), sprintBacklogPayload));

        verify(sprintBacklogRepository, times(1)).findById(sprintBacklogId);
        verify(sprintBacklogRepository, never()).save(any(SprintBacklog.class));
    }
}