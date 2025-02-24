package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProductBacklogRepositoryImp;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.payload.CreateProductBacklogPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductBacklogServiceTest {

    @Mock
    private ProductBacklogRepositoryImp productBacklogRepositoryImp;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProductBacklogService productBacklogService;

    private CreateProductBacklogPayload payload;
    private Project project;

    @BeforeEach
    public void setUp() {
        payload = new CreateProductBacklogPayload();
        payload.setName("Test Backlog");
        payload.setProjectId(UUID.randomUUID().toString());

        project = new Project();
        project.setId(UUID.fromString(payload.getProjectId()));
    }

    @Test
    public void testCreateProductBacklog_Success() {
        when(projectService.getProjectById(any(UUID.class))).thenReturn(Optional.of(project));
        ProductBacklog expectedBacklog = ProductBacklog.builder()
                .name(payload.getName())
                .project(project)
                .build();
        when(productBacklogRepositoryImp.save(any(ProductBacklog.class))).thenReturn(expectedBacklog);

        ProductBacklog actualBacklog = productBacklogService.createProductBacklog(payload);

        assertNotNull(actualBacklog);
        assertEquals(expectedBacklog.getName(), actualBacklog.getName());
        assertEquals(expectedBacklog.getProject(), actualBacklog.getProject());

        verify(projectService, times(1)).getProjectById(any(UUID.class));
        verify(productBacklogRepositoryImp, times(1)).save(any(ProductBacklog.class));
    }

    @Test
    public void testCreateProductBacklog_ProjectNotFound() {
        when(projectService.getProjectById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogService.createProductBacklog(payload));

        verify(projectService, times(1)).getProjectById(any(UUID.class));
        verify(productBacklogRepositoryImp, never()).save(any(ProductBacklog.class));
    }
}