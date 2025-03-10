package com.miniprojetspring.Controller;

import com.miniprojetspring.Model.TestCase;
import com.miniprojetspring.Service.TestCaseService;
import com.miniprojetspring.payload.TestCasePayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestCaseController {

    private final TestCaseService testCaseService;

    @Autowired
    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @PostMapping("/user-stories/{userStoryId}/test-cases")
    public ResponseEntity<TestCase> createTestCase(@PathVariable String userStoryId, @Valid @RequestBody TestCasePayload payload) {
        TestCase testCase = testCaseService.createTestCase(payload, userStoryId);
        return ResponseEntity.ok(testCase);
    }

    @GetMapping("/test-cases/{id}")
    public ResponseEntity<TestCase> getTestCaseById(@PathVariable String id) {
        TestCase testCase = testCaseService.getTestCaseById(id);
        return ResponseEntity.ok(testCase);
    }

    @PutMapping("/test-cases/{testCaseId}/user-stories/{userStoryId}")
    public ResponseEntity<TestCase> updateTestCase(@PathVariable String testCaseId, @PathVariable String userStoryId, @Valid @RequestBody TestCasePayload payload) {
        TestCase testCase = testCaseService.updateTestCase(payload, testCaseId, userStoryId);
        return ResponseEntity.ok(testCase);
    }

    @DeleteMapping("/test-cases/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable String id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user-stories/{userStoryId}/test-cases")
    public ResponseEntity<List<TestCase>> getTestCasesByUserStoryId(@PathVariable String userStoryId) {
        List<TestCase> testCases = testCaseService.getTestCasesByUserStoryId(userStoryId);
        return ResponseEntity.ok(testCases);
    }
}