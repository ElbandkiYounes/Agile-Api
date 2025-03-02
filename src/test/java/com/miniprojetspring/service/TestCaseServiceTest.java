package com.miniprojetspring.service;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.TestCase;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Repository.TestCaseRepository;
import com.miniprojetspring.Service.Implementation.TestCaseServiceImpl;
import com.miniprojetspring.Service.UserStoryService;
import com.miniprojetspring.payload.TestCasePayload;
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
public class TestCaseServiceTest {

    @Mock
    private UserStoryService userStoryService;

    @Mock
    private TestCaseRepository testCaseRepository;

    @InjectMocks
    private TestCaseServiceImpl testCaseServiceImpl;

    private TestCasePayload testCasePayload;
    private UserStory userStory;
    private TestCase testCase;
    private UUID userStoryId;
    private UUID testCaseId;

    @BeforeEach
    public void setUp() {
        userStoryId = UUID.randomUUID();
        testCaseId = UUID.randomUUID();

        testCasePayload = new TestCasePayload();
        testCasePayload.setTitle("Test Case Title");
        testCasePayload.setDescription("Test Case Description");
        testCasePayload.setUserStoryId(userStoryId.toString());

        userStory = UserStory.builder()
                .id(userStoryId)
                .title("Test User Story")
                .description("Test User Story Description")
                .build();

        testCase = TestCase.builder()
                .id(testCaseId)
                .title(testCasePayload.getTitle())
                .description(testCasePayload.getDescription())
                .userStory(userStory)
                .build();
    }

    @Test
    public void testCreateTestCase_Success() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

        TestCase createdTestCase = testCaseServiceImpl.createTestCase(testCasePayload, userStoryId.toString());

        assertNotNull(createdTestCase);
        assertEquals(testCase.getTitle(), createdTestCase.getTitle());
        assertEquals(testCase.getDescription(), createdTestCase.getDescription());

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
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
    public void testGetTestCaseById_Success() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));

        TestCase retrievedTestCase = testCaseServiceImpl.getTestCaseById(testCaseId.toString());

        assertNotNull(retrievedTestCase);
        assertEquals(testCase.getId(), retrievedTestCase.getId());

        verify(testCaseRepository, times(1)).findById(testCaseId);
    }

    @Test
    public void testGetTestCaseById_NotFound() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.getTestCaseById(testCaseId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
    }

    @Test
    public void testUpdateTestCase_Success() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

        TestCase updatedTestCase = testCaseServiceImpl.updateTestCase(testCasePayload, testCaseId.toString(), userStoryId.toString());

        assertNotNull(updatedTestCase);
        assertEquals(testCasePayload.getTitle(), updatedTestCase.getTitle());
        assertEquals(testCasePayload.getDescription(), updatedTestCase.getDescription());

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, times(1)).save(any(TestCase.class));
    }

    @Test
    public void testUpdateTestCase_TestCaseNotFound() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.updateTestCase(testCasePayload, testCaseId.toString(), userStoryId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(userStoryService, never()).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, never()).save(any(TestCase.class));
    }

    @Test
    public void testUpdateTestCase_UserStoryNotFound() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.updateTestCase(testCasePayload, testCaseId.toString(), userStoryId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, never()).save(any(TestCase.class));
    }

    @Test
    public void testDeleteTestCase_Success() {
        when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));
        doNothing().when(testCaseRepository).delete(testCase);

        assertDoesNotThrow(() -> testCaseServiceImpl.deleteTestCase(testCaseId.toString()));

        verify(testCaseRepository, times(1)).findById(testCaseId);
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
    public void testGetTestCasesByUserStoryId_Success() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(userStory);
        when(testCaseRepository.findTestCasesByUserStoryId(userStoryId)).thenReturn(List.of(testCase));

        List<TestCase> testCases = testCaseServiceImpl.getTestCasesByUserStoryId(userStoryId.toString());

        assertNotNull(testCases);
        assertFalse(testCases.isEmpty());
        assertEquals(testCase.getId(), testCases.get(0).getId());

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, times(1)).findTestCasesByUserStoryId(userStoryId);
    }

    @Test
    public void testGetTestCasesByUserStoryId_UserStoryNotFound() {
        when(userStoryService.getUserStoryById(userStoryId.toString())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> testCaseServiceImpl.getTestCasesByUserStoryId(userStoryId.toString()));

        verify(userStoryService, times(1)).getUserStoryById(userStoryId.toString());
        verify(testCaseRepository, never()).findTestCasesByUserStoryId(userStoryId);
    }
}