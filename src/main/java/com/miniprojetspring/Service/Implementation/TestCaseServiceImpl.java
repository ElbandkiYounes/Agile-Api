package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.Exception.NotFoundException;
import com.miniprojetspring.Model.TestCase;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Repository.TestCaseRepository;
import com.miniprojetspring.Service.TestCaseService;
import com.miniprojetspring.Service.UserStoryService;
import com.miniprojetspring.payload.TestCasePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    private final UserStoryService userStoryService;
    private final TestCaseRepository testCaseRepository;

    @Autowired
    public TestCaseServiceImpl(UserStoryService userStoryService, TestCaseRepository testCaseRepository) {
        this.userStoryService = userStoryService;
        this.testCaseRepository = testCaseRepository;
    }

    @Override
    public TestCase createTestCase(TestCasePayload testCasePayload, String userStoryId) {
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);
        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }
        TestCase testCase = testCasePayload.toEntity(userStory);
        return testCaseRepository.save(testCase);
    }

    @Override
    public TestCase getTestCaseById(String id) {
        return testCaseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Test case not found"));
    }

    @Override
    public TestCase updateTestCase(TestCasePayload testCasePayload,String testCaseId, String userStoryId) {
        TestCase existingTestCase = getTestCaseById(testCaseId);
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);
        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }
        if (!existingTestCase.getUserStory().equals(userStory)) {
            throw new NotFoundException("Test case does not belong to user story");
        }
        userStoryService.checkUserStoryStatus(userStoryId);
        return testCaseRepository.save(testCasePayload.toEntity(existingTestCase));
    }

    @Override
    public void deleteTestCase(String id) {
        TestCase testCase = getTestCaseById(id);
        testCaseRepository.delete(testCase);
    }

    @Override
    public List<TestCase> getTestCasesByUserStoryId(String userStoryId) {
        UserStory userStory = userStoryService.getUserStoryById(userStoryId);
        if (userStory == null) {
            throw new NotFoundException("User story not found");
        }
        return testCaseRepository.findTestCasesByUserStoryId(UUID.fromString(userStoryId));
    }
}
