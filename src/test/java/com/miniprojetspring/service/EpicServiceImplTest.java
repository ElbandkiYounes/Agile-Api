package com.miniprojetspring.service;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.*;
import com.miniprojetspring.repository.EpicRepository;
import com.miniprojetspring.service.implementation.EpicServiceImpl;
import com.miniprojetspring.service.implementation.ProjectSecurityService;
import com.miniprojetspring.payload.EpicPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EpicServiceImplTest {

    @Mock
    private EpicRepository epicRepository;

    @Mock
    private ProductBacklogService productBacklogService;

    @Mock
    private SprintBacklogService sprintBacklogService;

    @Mock
    private ProjectSecurityService projectSecurityService;

    @InjectMocks
    private EpicServiceImpl epicService;

    private EpicPayload createPayload;
    private EpicPayload updatePayload;
    private ProductBacklog productBacklog;
    private Epic epic;
    private UUID epicId;
    private UUID productBacklogId;
    private UUID sprintBacklogId;
    private SprintBacklog sprintBacklog;
    private User currentUser;
    private Project project;

    @BeforeEach
    public void setUp() {
        productBacklogId = UUID.randomUUID();
        epicId = UUID.randomUUID();
        sprintBacklogId = UUID.randomUUID();

        createPayload = new EpicPayload();
        createPayload.setName("Test Epic");

        updatePayload = new EpicPayload();
        updatePayload.setName("Updated Epic");

        productBacklog = new ProductBacklog();
        productBacklog.setId(productBacklogId);

        project = Project.builder()
                .id(UUID.randomUUID())
                .name("Test Project")
                .description("Test Project Description")
                .productBacklog(productBacklog)
                .build();

        productBacklog.setProject(project);

        epic = Epic.builder()
                .id(epicId)
                .name(createPayload.getName())
                .productBacklog(productBacklog)
                .userStories(List.of(new UserStory())) // Add empty user stories list
                .build();

        sprintBacklog = SprintBacklog.builder()
                .id(sprintBacklogId)
                .name("Test Sprint Backlog")
                .project(project)
                .build();

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
    public void testCreateEpic_Success() {
        when(productBacklogService.getProductBacklog()).thenReturn(productBacklog);
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);

        Epic actualEpic = epicService.createEpic(createPayload);

        assertNotNull(actualEpic);
        assertEquals(epic.getName(), actualEpic.getName());
        assertEquals(epic.getProductBacklog(), actualEpic.getProductBacklog());

        verify(productBacklogService, times(1)).getProductBacklog();
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testCreateEpic_ProductBacklogNotFound() {
        when(productBacklogService.getProductBacklog()).thenReturn(null);

        assertThrows(NotFoundException.class, () -> epicService.createEpic(createPayload));

        verify(productBacklogService, times(1)).getProductBacklog();
        verify(epicRepository, never()).save(any(Epic.class));
    }

    @Test
    public void testGetEpicById_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(projectSecurityService.isProjectMember(project.getId().toString())).thenReturn(true);

        Epic actualEpic = epicService.getEpicById(epicId.toString());

        assertNotNull(actualEpic);
        assertEquals(epic.getId(), actualEpic.getId());
        assertEquals(epic.getName(), actualEpic.getName());

        verify(epicRepository, times(1)).findById(epicId);
    }

    @Test
    public void testGetEpicById_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.getEpicById(epicId.toString()));

        verify(epicRepository, times(1)).findById(epicId);
    }

    @Test
    public void testGetEpics_Success() {
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);
        when(epicRepository.findByProductBacklog_Id(productBacklogId)).thenReturn(List.of(epic));

        List<Epic> epics = epicService.getEpics();

        assertNotNull(epics);
        assertFalse(epics.isEmpty());
        assertEquals(epic.getId(), epics.get(0).getId());

        verify(epicRepository, times(1)).findByProductBacklog_Id(productBacklogId);
    }

    @Test
    public void testGetEpics_UserNotInProject() {
        currentUser.setProject(null);
        when(projectSecurityService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(NotFoundException.class, () -> epicService.getEpics());

        verify(epicRepository, never()).findByProductBacklog_Id(any(UUID.class));
    }

    @Test
    public void testDeleteEpic_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(projectSecurityService.isProjectOwner(project.getId().toString())).thenReturn(true);

        doNothing().when(epicRepository).deleteById(epicId);

        epicService.deleteEpic(epicId.toString());

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(epicRepository, times(1)).deleteById(epicId);
    }

    @Test
    public void testDeleteEpic_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.deleteEpic(epicId.toString()));

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, never()).deleteById(epicId);
    }

    @Test
    public void testUpdateEpic_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);
        when(projectSecurityService.isProjectOwner(project.getId().toString())).thenReturn(true);

        Epic actualEpic = epicService.updateEpic(epicId.toString(), updatePayload);

        assertNotNull(actualEpic);
        assertEquals(updatePayload.getName(), actualEpic.getName());

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testUpdateEpic_NotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> epicService.updateEpic(epicId.toString(), updatePayload));

        verify(epicRepository, times(1)).findById(epicId);
        verify(epicRepository, never()).save(any(Epic.class));
    }

    @Test
    public void testLinkEpicToSprintBacklog_Success() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(projectSecurityService.isProjectOwner(project.getId().toString())).thenReturn(true);
        when(sprintBacklogService.getSprintBacklogById(sprintBacklogId.toString())).thenReturn(sprintBacklog);
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);

        Epic linkedEpic = epicService.linkEpicToSprintBacklog(sprintBacklogId.toString(), epicId.toString());

        assertNotNull(linkedEpic);
        assertEquals(sprintBacklog, linkedEpic.getSprintBacklog());

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(sprintBacklogService, times(1)).getSprintBacklogById(sprintBacklogId.toString());
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testLinkEpicToSprintBacklog_SprintBacklogNotFound() {
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(sprintBacklogService.getSprintBacklogById(sprintBacklogId.toString())).thenReturn(null);
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> epicService.linkEpicToSprintBacklog(sprintBacklogId.toString(), epicId.toString()));

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(sprintBacklogService, times(1)).getSprintBacklogById(sprintBacklogId.toString());
        verify(epicRepository, never()).save(any(Epic.class));
    }

    @Test
    public void testLinkEpicToSprintBacklog_Conflict() {
        epic.setSprintBacklog(sprintBacklog);
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> epicService.linkEpicToSprintBacklog(sprintBacklogId.toString(), epicId.toString()));

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(epicRepository, never()).save(any(Epic.class));
    }

    @Test
    public void testLinkEpicToSprintBacklog_DifferentProjects() {
        // Create a different project
        Project differentProject = Project.builder()
                .id(UUID.randomUUID())
                .name("Different Project")
                .description("Different Project Description")
                .build();

        // Create a sprint backlog from the different project
        SprintBacklog differentSprintBacklog = SprintBacklog.builder()
                .id(UUID.randomUUID())
                .name("Different Sprint Backlog")
                .project(differentProject)
                .build();

        // Mock repository responses
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(sprintBacklogService.getSprintBacklogById(sprintBacklogId.toString())).thenReturn(differentSprintBacklog);

        // Mock security checks
        // Return true for the epic's project (current project)
        when(projectSecurityService.isProjectOwner(project.getId().toString())).thenReturn(true);
        // Return false for the different project
        when(projectSecurityService.isProjectOwner(differentProject.getId().toString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                epicService.linkEpicToSprintBacklog(sprintBacklogId.toString(), epicId.toString()));

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(sprintBacklogService, times(1)).getSprintBacklogById(sprintBacklogId.toString());
        verify(epicRepository, never()).save(any(Epic.class));
    }

    @Test
    public void testUnlinkEpicToSprintBacklog_Success() {
        epic.setSprintBacklog(sprintBacklog);
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(epicRepository.save(any(Epic.class))).thenReturn(epic);
        when(projectSecurityService.isProjectOwner(project.getId().toString())).thenReturn(true);

        Epic unlinkedEpic = epicService.unlinkEpicToSprintBacklog(epicId.toString());

        assertNotNull(unlinkedEpic);
        assertNull(unlinkedEpic.getSprintBacklog());

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    public void testLinkEpicToSprintBacklog_EpicWithoutUserStories() {
        epic.setUserStories(List.of());
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> epicService.linkEpicToSprintBacklog(sprintBacklogId.toString(), epicId.toString()));

        verify(epicRepository, atLeastOnce()).findById(epicId);
        verify(epicRepository, never()).save(any(Epic.class));
    }
}