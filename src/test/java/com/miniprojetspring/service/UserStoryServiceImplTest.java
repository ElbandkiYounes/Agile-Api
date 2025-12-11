package com.miniprojetspring.service;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.*;
import com.miniprojetspring.Repository.TestCaseRepository;
import com.miniprojetspring.Repository.UserStoryRepository;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.Service.Implementation.ProjectSecurityService;
import com.miniprojetspring.Service.Implementation.UserStoryServiceImpl;
import com.miniprojetspring.Service.ProductBacklogService;
import com.miniprojetspring.Service.RoleService;
import com.miniprojetspring.payload.UserStoryPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStoryServiceImplTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProductBacklogService productBacklogService;

    @Mock
    private EpicService epicService;

    @Mock
    private RoleService roleService;

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private ProjectSecurityService projectSecurityService;

    @InjectMocks
    private UserStoryServiceImpl userStoryService;

    private UUID testUuid;
    private User testUser;
    private Project testProject;
    private ProductBacklog testProductBacklog;
    private Role testRole;
    private Epic testEpic;
    private UserStory testUserStory;
    private UserStoryPayload testUserStoryPayload;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();

        // Create test objects
        testUser = new User();
        testUser.setId(UUID.randomUUID());

        testProject = new Project();
        testProject.setId(UUID.randomUUID());
        testProject.setOwner(testUser);

        testProductBacklog = new ProductBacklog();
        testProductBacklog.setId(UUID.randomUUID());
        testProductBacklog.setProject(testProject);
        testProject.setProductBacklog(testProductBacklog);

        testRole = new Role();
        testRole.setId(UUID.randomUUID());
        testRole.setProject(testProject);
        testRole.setName("Developer");

        testEpic = new Epic();
        testEpic.setId(UUID.randomUUID());
        testEpic.setProductBacklog(testProductBacklog);

        testUserStory = new UserStory();
        testUserStory.setId(testUuid);
        testUserStory.setTitle("Test User Story");
        testUserStory.setDescription("Test Description");
        testUserStory.setRole(testRole);
        testUserStory.setGoal("Test Goal");
        testUserStory.setDesire("Test Desire");
        testUserStory.setPriority(UserStoryPriority.MUST_HAVE);
        testUserStory.setStatus(UserStoryStatus.NOT_STARTED);
        testUserStory.setProductBacklog(testProductBacklog);

        testUserStoryPayload = new UserStoryPayload();
        testUserStoryPayload.setTitle("Test User Story");
        testUserStoryPayload.setDescription("Test Description");
        testUserStoryPayload.setRoleId(testRole.getId().toString());
        testUserStoryPayload.setGoal("Test Goal");
        testUserStoryPayload.setDesire("Test Desire");
        testUserStoryPayload.setUserStoryPriority(UserStoryPriority.MUST_HAVE);
        testUserStoryPayload.setUserStoryStatus(UserStoryStatus.NOT_STARTED);
    }

    @Test
    void getUserStoriesByRoleId_ShouldReturnUserStories_WhenRoleExists() {
        // Arrange
        String roleId = testRole.getId().toString();
        List<UserStory> expectedUserStories = Collections.singletonList(testUserStory);

        when(roleService.getRoleById(roleId)).thenReturn(testRole);
        when(projectSecurityService.isProjectMember(testProject.getId().toString())).thenReturn(true);
        when(userStoryRepository.findByRole_Id(testRole.getId())).thenReturn(expectedUserStories);

        // Act
        List<UserStory> actualUserStories = userStoryService.getUserStoriesByRoleId(roleId);

        // Assert
        assertEquals(expectedUserStories, actualUserStories);
        verify(roleService).getRoleById(roleId);
        verify(userStoryRepository).findByRole_Id(testRole.getId());
    }

    @Test
    void getUserStoriesByRoleId_ShouldThrowNotFoundException_WhenRoleIsNull() {
        // Arrange
        String roleId = UUID.randomUUID().toString();
        when(roleService.getRoleById(roleId)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.getUserStoriesByRoleId(roleId));
        verify(roleService).getRoleById(roleId);
        verify(userStoryRepository, never()).findByRole_Id(any(UUID.class));
    }

    @Test
    void getUserStoriesByRoleId_ShouldThrowNotFoundException_WhenUserNotProjectMemberOrOwner() {
        // Arrange
        String roleId = testRole.getId().toString();
        when(roleService.getRoleById(roleId)).thenReturn(testRole);
        when(projectSecurityService.isProjectMember(testProject.getId().toString())).thenReturn(false);
        when(projectSecurityService.isProjectOwner(testProject.getId().toString())).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.getUserStoriesByRoleId(roleId));
        verify(roleService).getRoleById(roleId);
        verify(userStoryRepository, never()).findByRole_Id(any(UUID.class));
    }

    @Test
    void getUserStoriesByEpicId_ShouldReturnUserStories_WhenEpicExists() {
        // Arrange
        String epicId = testEpic.getId().toString();
        List<UserStory> expectedUserStories = Collections.singletonList(testUserStory);

        when(epicService.getEpicById(epicId)).thenReturn(testEpic);
        when(projectSecurityService.isProjectMember(testProject.getId().toString())).thenReturn(true);
        when(userStoryRepository.findByEpic_Id(testEpic.getId())).thenReturn(expectedUserStories);

        // Act
        List<UserStory> actualUserStories = userStoryService.getUserStoriesByEpicId(epicId);

        // Assert
        assertEquals(expectedUserStories, actualUserStories);
        verify(epicService).getEpicById(epicId);
        verify(userStoryRepository).findByEpic_Id(testEpic.getId());
    }

    @Test
    void getUserStoriesByEpicId_ShouldThrowNotFoundException_WhenEpicIsNull() {
        // Arrange
        String epicId = UUID.randomUUID().toString();
        when(epicService.getEpicById(epicId)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.getUserStoriesByEpicId(epicId));
        verify(epicService).getEpicById(epicId);
        verify(userStoryRepository, never()).findByEpic_Id(any(UUID.class));
    }

    @Test
    void getUserStoriesByEpicId_ShouldThrowAccessDeniedException_WhenUserNotProjectMemberOrOwner() {
        // Arrange
        String epicId = testEpic.getId().toString();
        when(epicService.getEpicById(epicId)).thenReturn(testEpic);
        when(projectSecurityService.isProjectMember(testProject.getId().toString())).thenReturn(false);
        when(projectSecurityService.isProjectOwner(testProject.getId().toString())).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userStoryService.getUserStoriesByEpicId(epicId));
        verify(epicService).getEpicById(epicId);
        verify(userStoryRepository, never()).findByEpic_Id(any(UUID.class));
    }

    @Test
    void getUserStories_ShouldReturnUserStories_WhenProductBacklogExists() {
        // Arrange
        List<UserStory> expectedUserStories = Collections.singletonList(testUserStory);

        when(productBacklogService.getProductBacklog()).thenReturn(testProductBacklog);
        when(userStoryRepository.findUserStoriesByProductBacklog_Id(testProductBacklog.getId())).thenReturn(expectedUserStories);

        // Act
        List<UserStory> actualUserStories = userStoryService.getUserStories();

        // Assert
        assertEquals(expectedUserStories, actualUserStories);
        verify(productBacklogService).getProductBacklog();
        verify(userStoryRepository).findUserStoriesByProductBacklog_Id(testProductBacklog.getId());
    }

    @Test
    void getUserStories_ShouldThrowNotFoundException_WhenProductBacklogIsNull() {
        // Arrange
        when(productBacklogService.getProductBacklog()).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.getUserStories());
        verify(productBacklogService).getProductBacklog();
        verify(userStoryRepository, never()).findUserStoriesByProductBacklog_Id(any(UUID.class));
    }

    @Test
    void createUserStory_ShouldReturnCreatedUserStory_WhenAllInputsAreValid() {
        // Arrange
        when(productBacklogService.getProductBacklog()).thenReturn(testProductBacklog);
        when(roleService.getRoleById(testUserStoryPayload.getRoleId())).thenReturn(testRole);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(testUserStory);

        // Act
        UserStory createdUserStory = userStoryService.createUserStory(testUserStoryPayload);

        // Assert
        assertEquals(testUserStory, createdUserStory);
        verify(productBacklogService).getProductBacklog();
        verify(roleService).getRoleById(testUserStoryPayload.getRoleId());
        verify(userStoryRepository).save(any(UserStory.class));
    }

    @Test
    void createUserStory_ShouldThrowNotFoundException_WhenProductBacklogIsNull() {
        // Arrange
        when(productBacklogService.getProductBacklog()).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.createUserStory(testUserStoryPayload));
        verify(productBacklogService).getProductBacklog();
        verify(roleService, never()).getRoleById(any());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void createUserStory_ShouldThrowNotFoundException_WhenRoleIsNull() {
        // Arrange
        when(productBacklogService.getProductBacklog()).thenReturn(testProductBacklog);
        when(roleService.getRoleById(testUserStoryPayload.getRoleId())).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.createUserStory(testUserStoryPayload));
        verify(productBacklogService).getProductBacklog();
        verify(roleService).getRoleById(testUserStoryPayload.getRoleId());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void createUserStory_ShouldThrowAccessDeniedException_WhenRoleAndProductBacklogNotOnSameProject() {
        // Arrange
        Project anotherProject = new Project();
        anotherProject.setId(UUID.randomUUID());

        Role roleFromAnotherProject = new Role();
        roleFromAnotherProject.setId(UUID.randomUUID());
        roleFromAnotherProject.setProject(anotherProject);

        when(productBacklogService.getProductBacklog()).thenReturn(testProductBacklog);
        when(roleService.getRoleById(testUserStoryPayload.getRoleId())).thenReturn(roleFromAnotherProject);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userStoryService.createUserStory(testUserStoryPayload));
        verify(productBacklogService).getProductBacklog();
        verify(roleService).getRoleById(testUserStoryPayload.getRoleId());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void linkUserStoryToEpic_ShouldReturnLinkedUserStory_WhenInputsAreValid() {
        // Arrange
        String epicId = testEpic.getId().toString();
        String userStoryId = testUserStory.getId().toString();

        getUserStoryByIdMock(userStoryId);
        when(epicService.getEpicById(epicId)).thenReturn(testEpic);
        when(userStoryRepository.save(testUserStory)).thenReturn(testUserStory);

        // Act
        UserStory linkedUserStory = userStoryService.linkUserStoryToEpic(epicId, userStoryId);

        // Assert
        assertEquals(testEpic, linkedUserStory.getEpic());
        verify(epicService).getEpicById(epicId);
        verify(userStoryRepository).save(testUserStory);
    }

    @Test
    void linkUserStoryToEpic_ShouldThrowNotFoundException_WhenEpicIsNull() {
        // Arrange
        String epicId = UUID.randomUUID().toString();
        String userStoryId = testUserStory.getId().toString();

        getUserStoryByIdMock(userStoryId);
        when(epicService.getEpicById(epicId)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.linkUserStoryToEpic(epicId, userStoryId));
        verify(epicService).getEpicById(epicId);
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void unlinkUserStoryFromEpic_ShouldReturnUnlinkedUserStory() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();
        testUserStory.setEpic(testEpic);

        getUserStoryByIdMock(userStoryId);

        // Act
        UserStory unlinkedUserStory = userStoryService.unlinkUserStoryFromEpic(userStoryId);

        // Assert
        assertNull(unlinkedUserStory.getEpic());
    }

    @Test
    void getUserStoryById_ShouldReturnUserStory_WhenUserStoryExists() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        when(userStoryRepository.findById(testUserStory.getId())).thenReturn(Optional.of(testUserStory));
        when(projectSecurityService.isProjectMember(testProject.getId().toString())).thenReturn(true);

        // Act
        UserStory retrievedUserStory = userStoryService.getUserStoryById(userStoryId);

        // Assert
        assertEquals(testUserStory, retrievedUserStory);
        verify(userStoryRepository).findById(testUserStory.getId());
    }

    @Test
    void getUserStoryById_ShouldThrowNotFoundException_WhenUserStoryDoesNotExist() {
        // Arrange
        String userStoryId = UUID.randomUUID().toString();

        when(userStoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.getUserStoryById(userStoryId));
        verify(userStoryRepository).findById(any(UUID.class));
    }

    @Test
    void getUserStoryById_ShouldThrowAccessDeniedException_WhenUserNotProjectMemberOrOwner() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        when(userStoryRepository.findById(testUserStory.getId())).thenReturn(Optional.of(testUserStory));
        when(projectSecurityService.isProjectMember(testProject.getId().toString())).thenReturn(false);
        when(projectSecurityService.isProjectOwner(testProject.getId().toString())).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userStoryService.getUserStoryById(userStoryId));
        verify(userStoryRepository).findById(testUserStory.getId());
    }

    @Test
    void updateUserStory_ShouldReturnUpdatedUserStory_WhenInputsAreValid() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        getUserStoryByIdMock(userStoryId);
        when(roleService.getRoleById(testUserStoryPayload.getRoleId())).thenReturn(testRole);
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(testUserStory);

        // Act
        UserStory updatedUserStory = userStoryService.updateUserStory(testUserStoryPayload, userStoryId);

        // Assert
        assertEquals(testUserStory, updatedUserStory);
        verify(roleService).getRoleById(testUserStoryPayload.getRoleId());
        verify(userStoryRepository).save(any(UserStory.class));
    }

    @Test
    void updateUserStory_ShouldThrowNotFoundException_WhenRoleIsNull() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        getUserStoryByIdMock(userStoryId);
        when(roleService.getRoleById(testUserStoryPayload.getRoleId())).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.updateUserStory(testUserStoryPayload, userStoryId));
        verify(roleService).getRoleById(testUserStoryPayload.getRoleId());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void updateUserStory_ShouldThrowNotFoundException_WhenRoleAndProductBacklogNotOnSameProject() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        Project anotherProject = new Project();
        anotherProject.setId(UUID.randomUUID());

        Role roleFromAnotherProject = new Role();
        roleFromAnotherProject.setId(UUID.randomUUID());
        roleFromAnotherProject.setProject(anotherProject);

        getUserStoryByIdMock(userStoryId);
        when(roleService.getRoleById(testUserStoryPayload.getRoleId())).thenReturn(roleFromAnotherProject);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userStoryService.updateUserStory(testUserStoryPayload, userStoryId));
        verify(roleService).getRoleById(testUserStoryPayload.getRoleId());
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void deleteUserStory_ShouldDeleteUserStory_WhenUserStoryExists() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        getUserStoryByIdMock(userStoryId);
        doNothing().when(userStoryRepository).delete(testUserStory);

        // Act
        userStoryService.deleteUserStory(userStoryId);

        // Assert
        verify(userStoryRepository).delete(testUserStory);
    }

    @Test
    void checkUserStoryStatus_ShouldSetStatusToNotStarted_WhenNoTestCases() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();
        getUserStoryByIdMock(userStoryId);
        when(testCaseRepository.findTestCasesByUserStoryId(testUserStory.getId())).thenReturn(Collections.emptyList());

        // Act
        userStoryService.checkUserStoryStatus(userStoryId);

        // Assert
        assertEquals(UserStoryStatus.NOT_STARTED, testUserStory.getStatus());
        verify(userStoryRepository).save(testUserStory);
    }

    @Test
    void checkUserStoryStatus_ShouldSetStatusToDone_WhenAllTestCasesPassed() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        TestCase passedTestCase = new TestCase();
        passedTestCase.setResult(TestCaseResult.PASS);

        List<TestCase> testCases = Collections.singletonList(passedTestCase);

        getUserStoryByIdMock(userStoryId);
        when(testCaseRepository.findTestCasesByUserStoryId(testUserStory.getId())).thenReturn(testCases);

        // Act
        userStoryService.checkUserStoryStatus(userStoryId);

        // Assert
        assertEquals(UserStoryStatus.DONE, testUserStory.getStatus());
        verify(userStoryRepository).save(testUserStory);
    }

    @Test
    void checkUserStoryStatus_ShouldSetStatusToInProgress_WhenSomeTestCasesNotPassed() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        TestCase passedTestCase = new TestCase();
        passedTestCase.setResult(TestCaseResult.PASS);

        TestCase failedTestCase = new TestCase();
        failedTestCase.setResult(TestCaseResult.FAIL);

        List<TestCase> testCases = Arrays.asList(passedTestCase, failedTestCase);

        getUserStoryByIdMock(userStoryId);
        when(testCaseRepository.findTestCasesByUserStoryId(testUserStory.getId())).thenReturn(testCases);

        // Act
        userStoryService.checkUserStoryStatus(userStoryId);

        // Assert
        assertEquals(UserStoryStatus.IN_PROGRESS, testUserStory.getStatus());
        verify(userStoryRepository).save(testUserStory);
    }

    @Test
    void checkUserStoryStatus_ShouldSetStatusToInProgress_WhenSomeTestCasesNullResult() {
        // Arrange
        String userStoryId = testUserStory.getId().toString();

        TestCase passedTestCase = new TestCase();
        passedTestCase.setResult(TestCaseResult.PASS);

        TestCase nullResultTestCase = new TestCase();
        nullResultTestCase.setResult(null);

        List<TestCase> testCases = Arrays.asList(passedTestCase, nullResultTestCase);

        getUserStoryByIdMock(userStoryId);
        when(testCaseRepository.findTestCasesByUserStoryId(testUserStory.getId())).thenReturn(testCases);

        // Act
        userStoryService.checkUserStoryStatus(userStoryId);

        // Assert
        assertEquals(UserStoryStatus.IN_PROGRESS, testUserStory.getStatus());
        verify(userStoryRepository).save(testUserStory);
    }

    // Helper method to mock getUserStoryById calls
    private UserStory getUserStoryByIdMock(String id) {
        when(userStoryRepository.findById(UUID.fromString(id))).thenReturn(Optional.of(testUserStory));
        when(projectSecurityService.isProjectMember(testProject.getId().toString())).thenReturn(true);
        return testUserStory;
    }
}