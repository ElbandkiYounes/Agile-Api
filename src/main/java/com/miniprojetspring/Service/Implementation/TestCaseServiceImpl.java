package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.exception.NotFoundException;
import com.miniprojetspring.Model.TestCase;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Repository.TestCaseRepository;
import com.miniprojetspring.Service.TestCaseService;
import com.miniprojetspring.Service.UserStoryService;
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

    @Override
    public TestCase createTestCase(TestCasePayload testCasePayload, String userStoryId) {
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);
        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }

        // Check if the user has access to the project
        if (!projectSecurityService.isProjectMember(userStory.getProductBacklog().getProject().getId().toString())
                && !projectSecurityService.isProjectOwner(userStory.getProductBacklog().getProject().getId().toString())) {
            throw new AccessDeniedException("User story not found");
        }

        TestCase testCase = testCasePayload.toEntity(userStory);
        return testCaseRepository.save(testCase);
    }

    @Override
    public TestCase getTestCaseById(String id) {
        TestCase testCase = testCaseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Test case not found"));

        // Check if the user has access to the project
        if (!projectSecurityService.isProjectMember(testCase.getUserStory().getProductBacklog().getProject().getId().toString())
                && !projectSecurityService.isProjectOwner(testCase.getUserStory().getProductBacklog().getProject().getId().toString())) {
            throw new AccessDeniedException("Test case not found");
        }

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

        // Check if the user has proper access rights for updating
        if (!projectSecurityService.isProjectMember(userStory.getProductBacklog().getProject().getId().toString())
                && !projectSecurityService.isProjectOwner(userStory.getProductBacklog().getProject().getId().toString())) {
            throw new AccessDeniedException("Insufficient privileges for this operation");
        }

        userStoryService.checkUserStoryStatus(userStoryId);
        return testCaseRepository.save(testCasePayload.toEntity(existingTestCase));
    }

    @Override
    public void deleteTestCase(String id) {
        TestCase testCase = getTestCaseById(id);

        // Check if the user has proper access rights for deletion
        // Only project owners should be able to delete (following pattern from other services)
        if (!projectSecurityService.isProjectOwner(testCase.getUserStory().getProductBacklog().getProject().getId().toString())) {
            throw new AccessDeniedException("Test case not found");
        }

        testCaseRepository.delete(testCase);
    }

    @Override
    public List<TestCase> getTestCasesByUserStoryId(String userStoryId) {
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);
        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }

        // Check if the user has access to the project
        if (!projectSecurityService.isProjectMember(userStory.getProductBacklog().getProject().getId().toString())
                && !projectSecurityService.isProjectOwner(userStory.getProductBacklog().getProject().getId().toString())) {
            throw new AccessDeniedException("User story not found");
        }

        return testCaseRepository.findTestCasesByUserStoryId(UUID.fromString(userStoryId));
    }
}