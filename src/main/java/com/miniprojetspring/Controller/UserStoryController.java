package com.miniprojetspring.Controller;

import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Service.UserStoryService;
import com.miniprojetspring.payload.UserStoryPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserStoryController {

    private final UserStoryService userStoryService;

    @Autowired
    public UserStoryController(UserStoryService userStoryService) {
        this.userStoryService = userStoryService;
    }

    @PostMapping("/product-backlog/{productBacklogId}/user-stories")
    public ResponseEntity<UserStory> createUserStory(@PathVariable String productBacklogId, @Valid @RequestBody UserStoryPayload payload) {
        UserStory userStory = userStoryService.createUserStory(productBacklogId, payload);
        return ResponseEntity.ok(userStory);
    }

    @GetMapping("/user-stories/{id}")
    public ResponseEntity<UserStory> getUserStoryById(@PathVariable String id) {
        UserStory userStory = userStoryService.getUserStoryById(id);
        return ResponseEntity.ok(userStory);
    }

    @GetMapping("/roles/{roleId}/user-stories")
    public ResponseEntity<List<UserStory>> getUserStoriesByRoleId(@PathVariable String roleId) {
        List<UserStory> userStories = userStoryService.getUserStoriesByRoleId(roleId);
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/epics/{epicId}/user-stories")
    public ResponseEntity<List<UserStory>> getUserStoriesByEpicId(@PathVariable String epicId) {
        List<UserStory> userStories = userStoryService.getUserStoriesByEpicId(epicId);
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/product-backlog/{productBacklogId}/user-stories")
    public ResponseEntity<List<UserStory>> getUserStoriesByBacklogId(@PathVariable String productBacklogId) {
        List<UserStory> userStories = userStoryService.getUserStoriesByBacklogId(productBacklogId);
        return ResponseEntity.ok(userStories);
    }

    @PutMapping("/user-stories/{id}")
    public ResponseEntity<UserStory> updateUserStory(@PathVariable String id, @Valid @RequestBody UserStoryPayload payload) {
        UserStory userStory = userStoryService.updateUserStory(payload, id);
        return ResponseEntity.ok(userStory);
    }

    @DeleteMapping("/user-stories/{id}")
    public ResponseEntity<Void> deleteUserStory(@PathVariable String id) {
        userStoryService.deleteUserStory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/user-stories/{userStoryId}/link/epic/{epicId}")
    public ResponseEntity<UserStory> linkUserStoryToEpic(@PathVariable String epicId, @PathVariable String userStoryId) {
        UserStory userStory = userStoryService.linkUserStoryToEpic(epicId, userStoryId);
        return ResponseEntity.ok(userStory);
    }

    @PostMapping("/user-stories/{userStoryId}/unlink")
    public ResponseEntity<UserStory> unlinkUserStoryFromEpic(@PathVariable String userStoryId) {
        UserStory userStory = userStoryService.unlinkUserStoryFromEpic(userStoryId);
        return ResponseEntity.ok(userStory);
    }
}