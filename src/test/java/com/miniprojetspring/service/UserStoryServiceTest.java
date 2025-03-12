package com.miniprojetspring.service;

import com.miniprojetspring.Exception.ConflictException;
import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.*;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.Repository.TestCaseRepository;
import com.miniprojetspring.Service.Implementation.EpicServiceImpl;
import com.miniprojetspring.Service.Implementation.ProductBacklogServiceImpl;
import com.miniprojetspring.Service.Implementation.RoleServiceImpl;
import com.miniprojetspring.Service.Implementation.UserStoryServiceImpl;
import com.miniprojetspring.payload.UserStoryPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProductBacklogServiceImpl productBacklogServiceImpl;

    @Mock
    private EpicServiceImpl epicService;

    @Mock
    private RoleServiceImpl roleService;

    @Mock
    private TestCaseRepository testCaseRepository;

    @InjectMocks
    private UserStoryServiceImpl userStoryServiceImpl;

    private UserStoryPayload userStoryPayload;
    private ProductBacklog productBacklog;
    private Role role;
    private Epic epic;
    private UserStory userStory;
    private UUID userStoryId;
    private UUID productBacklogId;
    private UUID roleId;
    private UUID epicId;

    @BeforeEach
    public void setUp() {
        userStoryId = UUID.randomUUID();
        productBacklogId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        epicId = UUID.randomUUID();

        userStoryPayload = new UserStoryPayload();
        userStoryPayload.setTitle("Test User Story");
        userStoryPayload.setDescription("Test Description");
        userStoryPayload.setRoleId(roleId.toString());

        Project project = Project.builder()
                .id(UUID.randomUUID())
                .name("Test Project")
                .description("Test Project Description")
                .build();

        productBacklog = ProductBacklog.builder()
                .id(productBacklogId)
                .name("Test Product Backlog")
                .project(project)
                .build();

        project.setProductBacklog(productBacklog);

        role = Role.builder()
                .id(roleId)
                .name("Test Role")
                .description("Test Role Description")
                .project(project)
                .build();

        epic = Epic.builder()
                .id(epicId)
                .name("Test Epic")
                .productBacklog(productBacklog)
                .build();

        userStory = UserStory.builder()
                .id(userStoryId)
                .title(userStoryPayload.getTitle())
                .description(userStoryPayload.getDescription())
                .productBacklog(productBacklog)
                .role(role)
                .status(UserStoryStatus.IN_PROGRESS)
                .build();
    }

    @Test
    public void testGetUserStoriesByRoleId_Success() {
        when(roleService.getRoleById(roleId.toString())).thenReturn(role);
        when(userStoryRepository.findByRole_Id(roleId)).thenReturn(List.of(userStory));

        List<UserStory> userStories = userStoryServiceImpl.getUserStoriesByRoleId(roleId.toString());

        assertNotNull(userStories);
        assertFalse(userStories.isEmpty());
        assertEquals(userStory.getId(), userStories.get(0).getId());

        verify(roleService, times(1)).getRoleById(roleId.toString());
        verify(userStoryRepository, times(1)).findByRole_Id(roleId);
    }

    @Test
    public void testGetUserStoriesByRoleId_RoleNotFound() {
        when(roleService.getRoleById(roleId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.getUserStoriesByRoleId(roleId.toString()));

        verify(roleService, times(1)).getRoleById(roleId.toString());
        verify(userStoryRepository, never()).findByRole_Id(roleId);
    }

    @Test
    public void testGetUserStoriesByEpicId_Success() {
        when(epicService.getEpicById(epicId.toString())).thenReturn(epic);
        when(userStoryRepository.findByEpic_Id(epicId)).thenReturn(List.of(userStory));

        List<UserStory> userStories = userStoryServiceImpl.getUserStoriesByEpicId(epicId.toString());

        assertNotNull(userStories);
        assertFalse(userStories.isEmpty());
        assertEquals(userStory.getId(), userStories.get(0).getId());

        verify(epicService, times(1)).getEpicById(epicId.toString());
        verify(userStoryRepository, times(1)).findByEpic_Id(epicId);
    }

    @Test
    public void testGetUserStoriesByEpicId_EpicNotFound() {
        when(epicService.getEpicById(epicId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.getUserStoriesByEpicId(epicId.toString()));

        verify(epicService, times(1)).getEpicById(epicId.toString());
        verify(userStoryRepository, never()).findByEpic_Id(epicId);
    }

    @Test
    public void testGetUserStoriesByBacklogId_Success() {
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId.toString())).thenReturn(productBacklog);
        when(userStoryRepository.findUserStoriesByProductBacklog_Id(productBacklogId)).thenReturn(List.of(userStory));

        List<UserStory> userStories = userStoryServiceImpl.getUserStoriesByBacklogId(productBacklogId.toString());

        assertNotNull(userStories);
        assertFalse(userStories.isEmpty());
        assertEquals(userStory.getId(), userStories.get(0).getId());

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId.toString());
        verify(userStoryRepository, times(1)).findUserStoriesByProductBacklog_Id(productBacklogId);
    }

    @Test
    public void testGetUserStoriesByBacklogId_ProductBacklogNotFound() {
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.getUserStoriesByBacklogId(productBacklogId.toString()));

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId.toString());
        verify(userStoryRepository, never()).findUserStoriesByProductBacklog_Id(productBacklogId);
    }

    @Test
    public void testCreateUserStory_Success() {
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId.toString())).thenReturn(productBacklog);
        when(roleService.getRoleById(roleId.toString())).thenReturn(role);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(userStory);

        UserStory createdUserStory = userStoryServiceImpl.createUserStory(productBacklogId.toString(), userStoryPayload);

        assertNotNull(createdUserStory);
        assertEquals(userStory.getTitle(), createdUserStory.getTitle());
        assertEquals(userStory.getDescription(), createdUserStory.getDescription());

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId.toString());
        verify(roleService, times(1)).getRoleById(roleId.toString());
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testCreateUserStory_ProductBacklogNotFound() {
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.createUserStory(productBacklogId.toString(), userStoryPayload));

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId.toString());
        verify(roleService, never()).getRoleById(roleId.toString());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testCreateUserStory_RoleNotFound() {
        when(productBacklogServiceImpl.getProductBacklogById(productBacklogId.toString())).thenReturn(productBacklog);
        when(roleService.getRoleById(roleId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.createUserStory(productBacklogId.toString(), userStoryPayload));

        verify(productBacklogServiceImpl, times(1)).getProductBacklogById(productBacklogId.toString());
        verify(roleService, times(1)).getRoleById(roleId.toString());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testLinkUserStoryToEpic_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(epicService.getEpicById(epicId.toString())).thenReturn(epic);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(userStory);

        UserStory linkedUserStory = userStoryServiceImpl.linkUserStoryToEpic(epicId.toString(), userStoryId.toString());

        assertNotNull(linkedUserStory);
        assertEquals(epic, linkedUserStory.getEpic());

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(epicService, times(1)).getEpicById(epicId.toString());
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testLinkUserStoryToEpic_EpicNotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(epicService.getEpicById(epicId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.linkUserStoryToEpic(epicId.toString(), userStoryId.toString()));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(epicService, times(1)).getEpicById(epicId.toString());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }
    @Test
    public void testLinkUserStoryToEpic_Conflict() {
        UUID userStoryId = UUID.randomUUID();
        UUID epicId = UUID.randomUUID();
        UserStory userStory = new UserStory();
        userStory.setId(userStoryId);
        Epic epic = new Epic();
        epic.setId(epicId);
        userStory.setEpic(epic);

        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(epicService.getEpicById(epicId.toString())).thenReturn(epic);

        assertThrows(ConflictException.class, () -> userStoryServiceImpl.linkUserStoryToEpic(epicId.toString(), userStoryId.toString()));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(epicService, times(1)).getEpicById(epicId.toString());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testUnlinkUserStoryFromEpic_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));

        UserStory unlinkedUserStory = userStoryServiceImpl.unlinkUserStoryFromEpic(userStoryId.toString());

        assertNotNull(unlinkedUserStory);
        assertNull(unlinkedUserStory.getEpic());

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testGetUserStoryById_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));

        UserStory retrievedUserStory = userStoryServiceImpl.getUserStoryById(userStoryId.toString());

        assertNotNull(retrievedUserStory);
        assertEquals(userStory.getId(), retrievedUserStory.getId());

        verify(userStoryRepository, times(1)).findById(userStoryId);
    }

    @Test
    public void testGetUserStoryById_NotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.getUserStoryById(userStoryId.toString()));

        verify(userStoryRepository, times(1)).findById(userStoryId);
    }

    @Test
    public void testUpdateUserStory_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(roleService.getRoleById(roleId.toString())).thenReturn(role);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(userStory);

        UserStory updatedUserStory = userStoryServiceImpl.updateUserStory(userStoryPayload, userStoryId.toString());

        assertNotNull(updatedUserStory);
        assertEquals(userStoryPayload.getTitle(), updatedUserStory.getTitle());
        assertEquals(userStoryPayload.getDescription(), updatedUserStory.getDescription());

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(roleService, times(1)).getRoleById(roleId.toString());
        verify(userStoryRepository, times(1)).save(any(UserStory.class));
    }

    @Test
    public void testUpdateUserStory_NotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.updateUserStory(userStoryPayload, userStoryId.toString()));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(roleService, never()).getRoleById(roleId.toString());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    public void testDeleteUserStory_Success() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        doNothing().when(userStoryRepository).delete(userStory);

        assertDoesNotThrow(() -> userStoryServiceImpl.deleteUserStory(userStoryId.toString()));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(userStoryRepository, times(1)).delete(userStory);
    }

    @Test
    public void testDeleteUserStory_NotFound() {
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.deleteUserStory(userStoryId.toString()));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(userStoryRepository, never()).delete(any(UserStory.class));
    }

    @Test
    public void testCheckUserStoryStatus_AllTestCasesPassed() {
        // Arrange
        TestCase testCase1 = TestCase.builder()
                .id(UUID.randomUUID())
                .result(TestCaseResult.PASS)
                .build();
        TestCase testCase2 = TestCase.builder()
                .id(UUID.randomUUID())
                .result(TestCaseResult.PASS)
                .build();
        List<TestCase> testCases = List.of(testCase1, testCase2);

        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(testCaseRepository.findTestCasesByUserStoryId(userStoryId)).thenReturn(testCases);

        // Act
        userStoryServiceImpl.checkUserStoryStatus(userStoryId.toString());

        // Assert
        assertEquals(UserStoryStatus.DONE, userStory.getStatus());
        verify(userStoryRepository, times(1)).save(userStory);
    }

    @Test
    public void testCheckUserStoryStatus_SomeTestCasesFailed() {
        // Arrange
        TestCase testCase1 = TestCase.builder()
                .id(UUID.randomUUID())
                .result(TestCaseResult.PASS)
                .build();
        TestCase testCase2 = TestCase.builder()
                .id(UUID.randomUUID())
                .result(TestCaseResult.FAIL)
                .build();
        List<TestCase> testCases = List.of(testCase1, testCase2);

        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(testCaseRepository.findTestCasesByUserStoryId(userStoryId)).thenReturn(testCases);

        // Act
        userStoryServiceImpl.checkUserStoryStatus(userStoryId.toString());

        // Assert
        assertEquals(UserStoryStatus.IN_PROGRESS, userStory.getStatus());
        verify(userStoryRepository, times(1)).save(userStory);
    }

    @Test
    public void testCheckUserStoryStatus_NoTestCases() {
        // Arrange
        List<TestCase> testCases = List.of();

        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(testCaseRepository.findTestCasesByUserStoryId(userStoryId)).thenReturn(testCases);

        // Act
        userStoryServiceImpl.checkUserStoryStatus(userStoryId.toString());

        // Assert
        assertEquals(UserStoryStatus.NOT_STARTED, userStory.getStatus());
        verify(userStoryRepository, times(1)).save(userStory);
    }

    @Test
    public void testCheckUserStoryStatus_NullResultInTestCases() {
        // Arrange
        TestCase testCase1 = TestCase.builder()
                .id(UUID.randomUUID())
                .result(TestCaseResult.PASS)
                .build();
        TestCase testCase2 = TestCase.builder()
                .id(UUID.randomUUID())
                .result(null) // Null result
                .build();
        List<TestCase> testCases = List.of(testCase1, testCase2);

        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.of(userStory));
        when(testCaseRepository.findTestCasesByUserStoryId(userStoryId)).thenReturn(testCases);

        // Act
        userStoryServiceImpl.checkUserStoryStatus(userStoryId.toString());

        // Assert
        assertEquals(UserStoryStatus.IN_PROGRESS, userStory.getStatus());
        verify(userStoryRepository, times(1)).save(userStory);
    }

    @Test
    public void testCheckUserStoryStatus_UserStoryNotFound() {
        // Arrange
        when(userStoryRepository.findById(userStoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryServiceImpl.checkUserStoryStatus(userStoryId.toString()));

        verify(userStoryRepository, times(1)).findById(userStoryId);
        verify(testCaseRepository, never()).findTestCasesByUserStoryId(any());
        verify(userStoryRepository, never()).save(any());
    }
}