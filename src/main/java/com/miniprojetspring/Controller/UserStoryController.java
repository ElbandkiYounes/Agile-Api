package com.miniprojetspring.controller;

import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Service.UserStoryService;
import com.miniprojetspring.payload.UserStoryPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-stories")
public class UserStoryController {

    private final UserStoryService userStoryService;

    @Autowired
    public UserStoryController(UserStoryService userStoryService) {
        this.userStoryService = userStoryService;
    }

    @PostMapping()
    public ResponseEntity<UserStory> createUserStory(@Valid @RequestBody UserStoryPayload payload) {
        UserStory userStory = userStoryService.createUserStory(payload);
        return ResponseEntity.ok(userStory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStory> getUserStoryById(@PathVariable String id) {
        UserStory userStory = userStoryService.getUserStoryById(id);
        return ResponseEntity.ok(userStory);
    }

    @GetMapping()
    public ResponseEntity<List<UserStory>> getUserStoriesByBacklogId() {
        List<UserStory> userStories = userStoryService.getUserStories();
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/roles/{roleId}")
    public ResponseEntity<List<UserStory>> getUserStoriesByRoleId(@PathVariable String roleId) {
        List<UserStory> userStories = userStoryService.getUserStoriesByRoleId(roleId);
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/epics/{epicId}")
    public ResponseEntity<List<UserStory>> getUserStoriesByEpicId(@PathVariable String epicId) {
        List<UserStory> userStories = userStoryService.getUserStoriesByEpicId(epicId);
        return ResponseEntity.ok(userStories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserStory> updateUserStory(@PathVariable String id, @Valid @RequestBody UserStoryPayload payload) {
        UserStory userStory = userStoryService.updateUserStory(payload, id);
        return ResponseEntity.ok(userStory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserStory(@PathVariable String id) {
        userStoryService.deleteUserStory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userStoryId}/link/epic/{epicId}")
    public ResponseEntity<UserStory> linkUserStoryToEpic(@PathVariable String epicId, @PathVariable String userStoryId) {
        UserStory userStory = userStoryService.linkUserStoryToEpic(epicId, userStoryId);
        return ResponseEntity.ok(userStory);
    }

    @PostMapping("/{userStoryId}/unlink")
    public ResponseEntity<UserStory> unlinkUserStoryFromEpic(@PathVariable String userStoryId) {
        UserStory userStory = userStoryService.unlinkUserStoryFromEpic(userStoryId);
        return ResponseEntity.ok(userStory);
    }
}