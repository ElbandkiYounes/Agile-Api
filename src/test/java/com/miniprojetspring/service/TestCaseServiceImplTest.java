package com.miniprojetspring.service;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.ProductBacklog;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.TestCase;
import com.miniprojetspring.model.TestCaseResult;
import com.miniprojetspring.model.UserStory;
import com.miniprojetspring.repository.TestCaseRepository;
import com.miniprojetspring.service.implementation.ProjectSecurityService;
import com.miniprojetspring.service.implementation.TestCaseServiceImpl;
import com.miniprojetspring.payload.TestCasePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestCaseServiceImplTest {

    @Mock
    private UserStoryService userStoryService;

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private ProjectSecurityService projectSecurityService;

    @InjectMocks
    private TestCaseServiceImpl testCaseServiceImpl;

    private TestCasePayload testCasePayload;
    private UserStory userStory;
    private TestCase testCase;
    private UUID testCaseId;
    private UUID userStoryId;
    private UUID projectId;
    private Project project;
    private ProductBacklog productBacklog;

    @BeforeEach
    public void setUp() {
        projectId = UUID.randomUUID();
        userStoryId = UUID.randomUUID();
        testCaseId = UUID.randomUUID();

        project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .build();

        productBacklog = ProductBacklog.builder()
                .id(UUID.randomUUID())
                .project(project)
                .build();

        userStory = UserStory.builder()
                .id(userStoryId)
                .title("Test User Story")
                .productBacklog(productBacklog)
                .build();

        testCasePayload = TestCasePayload.builder()
                .title("Test Case")
                .description("Test Description")
                .result(TestCaseResult.FAIL)
                .build();

        testCase = TestCase.builder()
                .id(testCaseId)
                .title(testCasePayload.getTitle())
                .description(testCasePayload.getDescription())
                .result(testCasePayload.getResult())
                .userStory(userStory)
                .build();
    }

    // Create TestCase Tests
    @Test
    public void testCreateTestCase_Success() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(true);
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

        TestCase actualTestCase = testCaseServiceImpl.createTestCase(testCasePayload, userStoryId.toString());

        assertNotNull(actualTestCase);
        assertEquals(testCase.getTitle(), actualTestCase.getTitle());
        assertEquals(testCase.getDescription(), actualTestCase.getDescription());
        assertEquals(testCase.getResult(), actualTestCase.getResult());

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
        verify(testCaseRepository, times(1)).save(any(TestCase.class));
    }

    @Test
    public void testCreateTestCase_UserStoryNotFound() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.createTestCase(testCasePayload, userStoryId.toString()));

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, never()).save(any(TestCase.class));
    }

    @Test
    public void testCreateTestCase_NoAccessRights() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(false);
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> testCaseServiceImpl.createTestCase(testCasePayload, userStoryId.toString()));

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
        verify(projectSecurityService, times(1)).isProjectOwner(anyString());
        verify(testCaseRepository, never()).save(any(TestCase.class));
    }

    @Test
    public void testCreateTestCase_ProjectOwnerSuccess() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(false);
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(true);
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

        TestCase actualTestCase = testCaseServiceImpl.createTestCase(testCasePayload, userStoryId.toString());

        assertNotNull(actualTestCase);
        assertEquals(testCase.getTitle(), actualTestCase.getTitle());

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
        verify(projectSecurityService, times(1)).isProjectOwner(anyString());
        verify(testCaseRepository, times(1)).save(any(TestCase.class));
    }

    // Get TestCase Tests
    @Test
    public void testGetTestCaseById_Success() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(true);

        TestCase actualTestCase = testCaseServiceImpl.getTestCaseById(testCaseId.toString());

        assertNotNull(actualTestCase);
        assertEquals(testCase.getId(), actualTestCase.getId());
        assertEquals(testCase.getTitle(), actualTestCase.getTitle());

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
    }

    @Test
    public void testGetTestCaseById_NotFound() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.getTestCaseById(testCaseId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
    }

    @Test
    public void testGetTestCaseById_NoAccessRights() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(false);
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> testCaseServiceImpl.getTestCaseById(testCaseId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
        verify(projectSecurityService, times(1)).isProjectOwner(anyString());
    }

    // Update TestCase Tests
    @Test
    public void testUpdateTestCase_Success() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(true);
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

        TestCase actualTestCase = testCaseServiceImpl.updateTestCase(testCasePayload, testCaseId.toString(), userStoryId.toString());

        assertNotNull(actualTestCase);
        assertEquals(testCasePayload.getTitle(), actualTestCase.getTitle());
        assertEquals(testCasePayload.getDescription(), actualTestCase.getDescription());
        assertEquals(testCasePayload.getResult(), actualTestCase.getResult());

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(projectSecurityService, times(2)).isProjectMember(anyString());
        verify(userStoryService, times(1)).checkUserStoryStatus(userStoryId.toString());
        verify(testCaseRepository, times(1)).save(any(TestCase.class));
    }

    @Test
    public void testUpdateTestCase_TestCaseNotFound() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.updateTestCase(testCasePayload, testCaseId.toString(), userStoryId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(userStoryService, never()).getUserStoryById(anyString());
        verify(testCaseRepository, never()).save(any(TestCase.class));
    }

    @Test
    public void testUpdateTestCase_TestCaseDoesNotBelongToUserStory() {
        // Create a different user story
        UserStory differentUserStory = UserStory.builder()
                .id(UUID.randomUUID())
                .title("Different User Story")
                .productBacklog(productBacklog)
                .build();

        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(differentUserStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.updateTestCase(testCasePayload, testCaseId.toString(), userStoryId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, never()).save(any(TestCase.class));
    }

    // Delete TestCase Tests
    @Test
    public void testDeleteTestCase_Success() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(true);

        testCaseServiceImpl.deleteTestCase(testCaseId.toString());

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(projectSecurityService, times(2)).isProjectOwner(anyString());
        verify(testCaseRepository, times(1)).delete(testCase);
    }

    @Test
    public void testDeleteTestCase_NotFound() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.deleteTestCase(testCaseId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(testCaseRepository, never()).delete(any(TestCase.class));
    }

    @Test
    public void testDeleteTestCase_NoAccessRights() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> testCaseServiceImpl.deleteTestCase(testCaseId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(projectSecurityService, times(1)).isProjectOwner(anyString());
        verify(testCaseRepository, never()).delete(any(TestCase.class));
    }

    // Get TestCases by UserStory Tests
    @Test
    public void testGetTestCasesByUserStoryId_Success() {
        List<TestCase> testCases = Arrays.asList(
                TestCase.builder().id(UUID.randomUUID()).title("Test Case 1").userStory(userStory).build(),
                TestCase.builder().id(UUID.randomUUID()).title("Test Case 2").userStory(userStory).build()
        );

        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(true);
        when(testCaseRepository.findTestCasesByUserStoryId(userStoryId)).thenReturn(testCases);

        List<TestCase> actualTestCases = testCaseServiceImpl.getTestCasesByUserStoryId(userStoryId.toString());

        assertNotNull(actualTestCases);
        assertEquals(2, actualTestCases.size());
        assertEquals("Test Case 1", actualTestCases.get(0).getTitle());
        assertEquals("Test Case 2", actualTestCases.get(1).getTitle());

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
        verify(testCaseRepository, times(1)).findTestCasesByUserStoryId(userStoryId);
    }

    @Test
    public void testGetTestCasesByUserStoryId_UserStoryNotFound() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.getTestCasesByUserStoryId(userStoryId.toString()));

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, never()).findTestCasesByUserStoryId(any(UUID.class));
    }

    @Test
    public void testGetTestCasesByUserStoryId_NoAccessRights() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(false);
        when(projectSecurityService.isProjectOwner(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> testCaseServiceImpl.getTestCasesByUserStoryId(userStoryId.toString()));

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
        verify(projectSecurityService, times(1)).isProjectOwner(anyString());
        verify(testCaseRepository, never()).findTestCasesByUserStoryId(any(UUID.class));
    }

    @Test
    public void testGetTestCasesByUserStoryId_EmptyList() {
        List<TestCase> emptyList = List.of();

        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(projectSecurityService.isProjectMember(anyString())).thenReturn(true);
        when(testCaseRepository.findTestCasesByUserStoryId(userStoryId)).thenReturn(emptyList);

        List<TestCase> actualTestCases = testCaseServiceImpl.getTestCasesByUserStoryId(userStoryId.toString());

        assertNotNull(actualTestCases);
        assertTrue(actualTestCases.isEmpty());

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(projectSecurityService, times(1)).isProjectMember(anyString());
        verify(testCaseRepository, times(1)).findTestCasesByUserStoryId(userStoryId);
    }
}