package com.miniprojetspring.controller;

import com.miniprojetspring.model.TestCase;
import com.miniprojetspring.service.TestCaseService;
import com.miniprojetspring.payload.TestCasePayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-cases")
public class TestCaseController {

    private final TestCaseService testCaseService;

    @Autowired
    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @PostMapping("/user-stories/{userStoryId}/")
    public ResponseEntity<TestCase> createTestCase(@PathVariable String userStoryId, @Valid @RequestBody TestCasePayload payload) {
        TestCase testCase = testCaseService.createTestCase(payload, userStoryId);
        return ResponseEntity.ok(testCase);
    }

    @GetMapping("{id}")
    public ResponseEntity<TestCase> getTestCaseById(@PathVariable String id) {
        TestCase testCase = testCaseService.getTestCaseById(id);
        return ResponseEntity.ok(testCase);
    }

    @PutMapping("{id}/user-stories/{userStoryId}")
    public ResponseEntity<TestCase> updateTestCase(@PathVariable String id, @PathVariable String userStoryId, @Valid @RequestBody TestCasePayload payload) {
        TestCase testCase = testCaseService.updateTestCase(payload, id, userStoryId);
        return ResponseEntity.ok(testCase);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable String id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user-stories/{userStoryId}")
    public ResponseEntity<List<TestCase>> getTestCasesByUserStoryId(@PathVariable String userStoryId) {
        List<TestCase> testCases = testCaseService.getTestCasesByUserStoryId(userStoryId);
        return ResponseEntity.ok(testCases);
    }
}