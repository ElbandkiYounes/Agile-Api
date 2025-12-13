package com.miniprojetspring.service.implementation;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.model.TestCase;
import com.miniprojetspring.model.UserStory;
import com.miniprojetspring.repository.TestCaseRepository;
import com.miniprojetspring.service.TestCaseService;
import com.miniprojetspring.service.UserStoryService;
import com.miniprojetspring.payload.TestCasePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    private final UserStoryService userStoryService;
    private final TestCaseRepository testCaseRepository;
    private final ProjectSecurityService projectSecurityService;

    @Autowired
    public TestCaseServiceImpl(
            UserStoryService userStoryService,
            TestCaseRepository testCaseRepository,
            ProjectSecurityService projectSecurityService) {
        this.userStoryService = userStoryService;
        this.testCaseRepository = testCaseRepository;
        this.projectSecurityService = projectSecurityService;
    }

    private void validateProjectAccess(String projectId) {
        if (!projectSecurityService.isProjectMember(projectId)
                && !projectSecurityService.isProjectOwner(projectId)) {
            throw new AccessDeniedException("Access denied to project");
        }
    }

    private void validateProjectOwner(String projectId) {
        if (!projectSecurityService.isProjectOwner(projectId)) {
            throw new AccessDeniedException("Only project owner can perform this action");
        }
    }

    @Override
    public TestCase createTestCase(TestCasePayload testCasePayload, String userStoryId) {
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);
        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }

        validateProjectAccess(userStory.getProductBacklog().getProject().getId().toString());

        TestCase testCase = testCasePayload.toEntity(userStory);
        return testCaseRepository.save(testCase);
    }

    @Override
    public TestCase getTestCaseById(String id) {
        TestCase testCase = testCaseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Test case not found"));

        validateProjectAccess(testCase.getUserStory().getProductBacklog().getProject().getId().toString());

        return testCase;
    }

    @Override
    public TestCase updateTestCase(TestCasePayload testCasePayload, String testCaseId, String userStoryId) {
        TestCase existingTestCase = getTestCaseById(testCaseId);
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);

        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }

        if (!existingTestCase.getUserStory().equals(userStory)) {
            throw new NotFoundException("Test case does not belong to user story");
        }

        validateProjectAccess(userStory.getProductBacklog().getProject().getId().toString());

        userStoryService.checkUserStoryStatus(userStoryId);
        return testCaseRepository.save(testCasePayload.toEntity(existingTestCase));
    }

    @Override
    public void deleteTestCase(String id) {
        TestCase testCase = getTestCaseById(id);

        validateProjectOwner(testCase.getUserStory().getProductBacklog().getProject().getId().toString());

        testCaseRepository.delete(testCase);
    }

    @Override
    public List<TestCase> getTestCasesByUserStoryId(String userStoryId) {
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);
        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }

        validateProjectAccess(userStory.getProductBacklog().getProject().getId().toString());

        return testCaseRepository.findTestCasesByUserStoryId(UUID.fromString(userStoryId));
    }
}