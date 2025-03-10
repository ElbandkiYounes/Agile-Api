package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProjectRepository;
import com.miniprojetspring.Service.Implementation.ProjectServiceImpl;
import com.miniprojetspring.payload.ProjectPayload;
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
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private ProjectPayload createPayload;
    private ProjectPayload updatePayload;
    private Project project;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        projectId = UUID.randomUUID();

        createPayload = ProjectPayload.builder()
                .name("Test Project")
                .description("Test Project Description")
                .build();

        updatePayload = ProjectPayload.builder()
                .name("Updated Project")
                .description("Updated Project Description")
                .build();

        project = Project.builder()
                .id(projectId)
                .name(createPayload.getName())
                .description(createPayload.getDescription())
                .build();
    }

    @Test
    public void testGetProjectById_NotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.getProjectById(projectId.toString()));

        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    public void testGetProjectById_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project actualProject = projectService.getProjectById(projectId.toString());

        assertNotNull(actualProject);
        assertEquals(project.getId(), actualProject.getId());
        assertEquals(project.getName(), actualProject.getName());

        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    public void testCreateProject_Success() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project actualProject = projectService.createProject(createPayload);

        assertNotNull(actualProject);
        assertEquals(project.getName(), actualProject.getName());
        assertEquals(project.getDescription(), actualProject.getDescription());

        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_NotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.updateProject(projectId.toString(), updatePayload));

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project actualProject = projectService.updateProject(projectId.toString(), updatePayload);

        assertNotNull(actualProject);
        assertEquals(updatePayload.getName(), actualProject.getName());
        assertEquals(updatePayload.getDescription(), actualProject.getDescription());

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testDeleteProject_NotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.deleteProject(projectId.toString()));

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    public void testDeleteProject_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.deleteProject(projectId.toString());

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).delete(project);
    }
}