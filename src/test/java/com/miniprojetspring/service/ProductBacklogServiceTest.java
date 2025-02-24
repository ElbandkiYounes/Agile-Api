package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.payload.CreateProductBacklogPayload;
import com.miniprojetspring.payload.UpdateProductBacklogPayload;
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
    private ProductBacklogRepository productBacklogRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProductBacklogService productBacklogService;

    private CreateProductBacklogPayload createPayload;
    private UpdateProductBacklogPayload updatePayload;
    private Project project;
    private ProductBacklog productBacklog;

    @BeforeEach
    public void setUp() {
        createPayload = new CreateProductBacklogPayload();
        createPayload.setName("Test Backlog");
        createPayload.setProjectId(UUID.randomUUID().toString());

        updatePayload = new UpdateProductBacklogPayload();
        updatePayload.setName("Updated Backlog");

        project = new Project();
        project.setId(UUID.fromString(createPayload.getProjectId()));

        productBacklog = ProductBacklog.builder()
                .id(UUID.randomUUID())
                .name(createPayload.getName())
                .project(project)
                .build();
    }

    @Test
    public void testCreateProductBacklog_Success() {
        when(projectService.getProjectById(any(UUID.class))).thenReturn(Optional.of(project));
        when(productBacklogRepository.save(any(ProductBacklog.class))).thenReturn(productBacklog);

        ProductBacklog actualBacklog = productBacklogService.createProductBacklog(createPayload);

        assertNotNull(actualBacklog);
        assertEquals(productBacklog.getName(), actualBacklog.getName());
        assertEquals(productBacklog.getProject(), actualBacklog.getProject());

        verify(projectService, times(1)).getProjectById(any(UUID.class));
        verify(productBacklogRepository, times(1)).save(any(ProductBacklog.class));
    }

    @Test
    public void testCreateProductBacklog_ProjectNotFound() {
        when(projectService.getProjectById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogService.createProductBacklog(createPayload));

        verify(projectService, times(1)).getProjectById(any(UUID.class));
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }

    @Test
    public void testGetProductBacklogById_Success() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.of(productBacklog));

        ProductBacklog actualBacklog = productBacklogService.getProductBacklogById(productBacklog.getId());

        assertNotNull(actualBacklog);
        assertEquals(productBacklog.getId(), actualBacklog.getId());
        assertEquals(productBacklog.getName(), actualBacklog.getName());

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testGetProductBacklogById_NotFound() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogService.getProductBacklogById(productBacklog.getId()));

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testDeleteProductBacklog_Success() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.of(productBacklog));

        productBacklogService.deleteProductBacklog(productBacklog.getId());

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(productBacklogRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void testDeleteProductBacklog_NotFound() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogService.deleteProductBacklog(productBacklog.getId()));

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(productBacklogRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testUpdateProductBacklog_Success() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.of(productBacklog));
        when(productBacklogRepository.save(any(ProductBacklog.class))).thenReturn(productBacklog);

        ProductBacklog actualBacklog = productBacklogService.updateProductBacklog(productBacklog.getId(), updatePayload);

        assertNotNull(actualBacklog);
        assertEquals(updatePayload.getName(), actualBacklog.getName());

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(productBacklogRepository, times(1)).save(any(ProductBacklog.class));
    }

    @Test
    public void testUpdateProductBacklog_NotFound() {
        when(productBacklogRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogService.updateProductBacklog(productBacklog.getId(), updatePayload));

        verify(productBacklogRepository, times(1)).findById(any(UUID.class));
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }
}