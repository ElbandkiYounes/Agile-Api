package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Service.Implementation.ProductBacklogServiceImpl;
import com.miniprojetspring.Service.Implementation.ProjectServiceImpl;
import com.miniprojetspring.payload.ProductBacklogPayload;
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
public class ProductBacklogServiceImplTest {

    @Mock
    private ProductBacklogRepository productBacklogRepository;

    @Mock
    private ProjectServiceImpl projectServiceImpl;

    @InjectMocks
    private ProductBacklogServiceImpl productBacklogServiceImpl;

    private ProductBacklogPayload createPayload;
    private ProductBacklogPayload updatePayload;
    private Project project;
    private ProductBacklog productBacklog;
    private UUID productBacklogId;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        projectId = UUID.randomUUID();
        productBacklogId = UUID.randomUUID();

        createPayload = new ProductBacklogPayload();
        createPayload.setName("Test Backlog");
        createPayload.setProjectId(projectId.toString());

        updatePayload = new ProductBacklogPayload();
        updatePayload.setName("Updated Backlog");

        project = new Project();
        project.setId(projectId);

        productBacklog = ProductBacklog.builder()
                .id(productBacklogId)
                .name(createPayload.getName())
                .project(project)
                .build();
    }

    @Test
    public void testCreateProductBacklog_Success() {
        when(projectServiceImpl.getProjectById(String.valueOf(projectId))).thenReturn(project);
        when(productBacklogRepository.save(any(ProductBacklog.class))).thenReturn(productBacklog);

        ProductBacklog actualBacklog = productBacklogServiceImpl.createProductBacklog(projectId.toString(),createPayload);

        assertNotNull(actualBacklog);
        assertEquals(productBacklog.getName(), actualBacklog.getName());
        assertEquals(productBacklog.getProject(), actualBacklog.getProject());

        verify(projectServiceImpl, times(1)).getProjectById(String.valueOf(projectId));
        verify(productBacklogRepository, times(1)).save(any(ProductBacklog.class));
    }

    @Test
    public void testCreateProductBacklog_ProjectNotFound() {
        when(projectServiceImpl.getProjectById(String.valueOf(projectId))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.createProductBacklog(String.valueOf(projectId),createPayload));

        verify(projectServiceImpl, times(1)).getProjectById(String.valueOf(projectId));
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }

    @Test
    public void testGetProductBacklogById_Success() {
        when(productBacklogRepository.findById(productBacklogId)).thenReturn(Optional.of(productBacklog));

        ProductBacklog actualBacklog = productBacklogServiceImpl.getProductBacklogById(String.valueOf(productBacklogId));

        assertNotNull(actualBacklog);
        assertEquals(productBacklog.getId(), actualBacklog.getId());
        assertEquals(productBacklog.getName(), actualBacklog.getName());

        verify(productBacklogRepository, times(1)).findById(productBacklogId);
    }

    @Test
    public void testGetProductBacklogById_NotFound() {
        when(productBacklogRepository.findById(productBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.getProductBacklogById(String.valueOf(productBacklogId)));

        verify(productBacklogRepository, times(1)).findById(productBacklogId);
    }

    @Test
    public void testDeleteProductBacklog_Success() {
        when(productBacklogRepository.findById(productBacklogId)).thenReturn(Optional.of(productBacklog));

        productBacklogServiceImpl.deleteProductBacklog(String.valueOf(productBacklogId));

        verify(productBacklogRepository, times(1)).findById(productBacklogId);
        verify(productBacklogRepository, times(1)).deleteById(productBacklogId);
    }

    @Test
    public void testDeleteProductBacklog_NotFound() {
        when(productBacklogRepository.findById(productBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.deleteProductBacklog(String.valueOf(productBacklogId)));

        verify(productBacklogRepository, times(1)).findById(productBacklogId);
        verify(productBacklogRepository, never()).deleteById(productBacklogId);
    }

    @Test
    public void testUpdateProductBacklog_Success() {
        when(productBacklogRepository.findById(productBacklogId)).thenReturn(Optional.of(productBacklog));
        when(productBacklogRepository.save(any(ProductBacklog.class))).thenReturn(productBacklog);

        ProductBacklog actualBacklog = productBacklogServiceImpl.updateProductBacklog(String.valueOf(productBacklogId), updatePayload);

        assertNotNull(actualBacklog);
        assertEquals(updatePayload.getName(), actualBacklog.getName());

        verify(productBacklogRepository, times(1)).findById(productBacklogId);
        verify(productBacklogRepository, times(1)).save(any(ProductBacklog.class));
    }

    @Test
    public void testUpdateProductBacklog_NotFound() {
        when(productBacklogRepository.findById(productBacklogId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.updateProductBacklog(String.valueOf(productBacklogId), updatePayload));

        verify(productBacklogRepository, times(1)).findById(productBacklogId);
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }
}