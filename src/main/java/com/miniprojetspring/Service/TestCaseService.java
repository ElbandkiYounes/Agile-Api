package com.miniprojetspring.Service;

import com.miniprojetspring.Model.TestCase;
import com.miniprojetspring.payload.TestCasePayload;

import java.util.List;

public interface TestCaseService {

    TestCase createTestCase(TestCasePayload testCasePayload, String userStoryId);

    TestCase getTestCaseById(String id);
    TestCase updateTestCase(TestCasePayload testCasePayload,String testCaseId, String userStoryId);
    void deleteTestCase(String id);
    List<TestCase> getTestCasesByUserStoryId(String userStoryId);
}
