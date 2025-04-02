package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.Previlige;
import com.miniprojetspring.Model.ProductBacklog;
import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.User;
import com.miniprojetspring.Repository.ProductBacklogRepository;
import com.miniprojetspring.Service.Implementation.ProductBacklogServiceImpl;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.Service.Implementation.ProjectSecurityService;
import com.miniprojetspring.payload.ProductBacklogPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductBacklogServiceImplTest {

    @Mock
    private ProductBacklogRepository productBacklogRepository;

    @Mock
    private ProjectService projectServiceImpl;

    @Mock
    private ProjectSecurityService projectSecurityService;

    @InjectMocks
    private ProductBacklogServiceImpl productBacklogServiceImpl;

    private ProductBacklogPayload createPayload;
    private ProductBacklogPayload updatePayload;
    private Project project;
    private ProductBacklog productBacklog;
    private User currentUser;
    private UUID productBacklogId;
    private UUID projectId;

    @BeforeEach
    public void setUp() {
        projectId = UUID.randomUUID();
        productBacklogId = UUID.randomUUID();

        createPayload = new ProductBacklogPayload();
        createPayload.setName("Test Backlog");

        updatePayload = new ProductBacklogPayload();
        updatePayload.setName("Updated Backlog");

        productBacklog = ProductBacklog.builder()
                .id(productBacklogId)
                .name(createPayload.getName())
                .build();

        project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .description("Test Project Description")
                .productBacklog(productBacklog)
                .build();

        productBacklog.setProject(project);

        currentUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("testuser@example.com")
                .password("password")
                .previlige(Previlige.PRODUCT_OWNER)
                .project(project)
                .build();
    }

    @Test
    public void testCreateProductBacklog_Success() {
        // Clear existing product backlog for this test
        project.setProductBacklog(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        when(productBacklogRepository.save(any(ProductBacklog.class))).thenReturn(productBacklog);
        doAnswer(invocation -> {
            ProductBacklog pb = invocation.getArgument(0);
            pb.setProject(project);
            return null;
        }).when(projectServiceImpl).linkProductBacklogToProject(any(ProductBacklog.class));

        ProductBacklog actualBacklog = productBacklogServiceImpl.createProductBacklog(createPayload);

        assertNotNull(actualBacklog);
        assertEquals(productBacklog.getName(), actualBacklog.getName());
        assertEquals(project, actualBacklog.getProject());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(projectServiceImpl, times(1)).linkProductBacklogToProject(any(ProductBacklog.class));
        verify(productBacklogRepository, times(1)).save(any(ProductBacklog.class));
    }

    @Test
    public void testCreateProductBacklog_ProjectNotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.createProductBacklog(createPayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }

    @Test
    public void testCreateProductBacklog_AlreadyExists() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.createProductBacklog(createPayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }

    @Test
    public void testGetProductBacklog_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        ProductBacklog actualBacklog = productBacklogServiceImpl.getProductBacklog();

        assertNotNull(actualBacklog);
        assertEquals(productBacklog.getName(), actualBacklog.getName());
        assertEquals(project, actualBacklog.getProject());

        verify(projectSecurityService, times(1)).getCurrentUser();
    }

    @Test
    public void testGetProductBacklog_ProjectNotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.getProductBacklog());

        verify(projectSecurityService, times(1)).getCurrentUser();
    }

    @Test
    public void testGetProductBacklog_NotFound() {
        project.setProductBacklog(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.getProductBacklog());

        verify(projectSecurityService, times(1)).getCurrentUser();
    }

    @Test
    public void testDeleteProductBacklog_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        productBacklogServiceImpl.deleteProductBacklog();

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, times(1)).delete(productBacklog);
    }

    @Test
    public void testDeleteProductBacklog_ProjectNotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.deleteProductBacklog());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, never()).delete(any(ProductBacklog.class));
    }

    @Test
    public void testDeleteProductBacklog_NotFound() {
        project.setProductBacklog(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.deleteProductBacklog());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, never()).delete(any(ProductBacklog.class));
    }

    @Test
    public void testUpdateProductBacklog_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        when(productBacklogRepository.save(any(ProductBacklog.class))).thenReturn(productBacklog);

        ProductBacklog actualBacklog = productBacklogServiceImpl.updateProductBacklog(updatePayload);

        assertNotNull(actualBacklog);
        assertEquals(updatePayload.getName(), actualBacklog.getName());
        assertEquals(project, actualBacklog.getProject());

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, times(1)).save(any(ProductBacklog.class));
    }

    @Test
    public void testUpdateProductBacklog_ProjectNotFound() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.updateProductBacklog(updatePayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }

    @Test
    public void testUpdateProductBacklog_NotFound() {
        project.setProductBacklog(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> productBacklogServiceImpl.updateProductBacklog(updatePayload));

        verify(projectSecurityService, times(1)).getCurrentUser();
        verify(productBacklogRepository, never()).save(any(ProductBacklog.class));
    }
}