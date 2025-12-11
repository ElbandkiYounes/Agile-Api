package com.miniprojetspring.controller;

import com.miniprojetspring.model.SprintBacklog;
import com.miniprojetspring.Service.SprintBacklogService;
import com.miniprojetspring.payload.SprintBacklogPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sprint-backlogs")
public class SprintBacklogController {

    private final SprintBacklogService sprintBacklogService;

    @Autowired
    public SprintBacklogController(SprintBacklogService sprintBacklogService) {
        this.sprintBacklogService = sprintBacklogService;
    }

    @PostMapping()
    public ResponseEntity<SprintBacklog> createSprintBacklog(@Valid @RequestBody SprintBacklogPayload payload) {
        SprintBacklog sprintBacklog = sprintBacklogService.createSprintBacklog(payload);
        return ResponseEntity.ok(sprintBacklog);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintBacklog> getSprintBacklogById(@PathVariable String id) {
        SprintBacklog sprintBacklog = sprintBacklogService.getSprintBacklogById(id);
        return ResponseEntity.ok(sprintBacklog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintBacklog> updateSprintBacklog(@PathVariable String id, @Valid @RequestBody SprintBacklogPayload payload) {
        SprintBacklog sprintBacklog = sprintBacklogService.updateSprintBacklog(id, payload);
        return ResponseEntity.ok(sprintBacklog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprintBacklog(@PathVariable String id) {
        sprintBacklogService.deleteSprintBacklog(id);
        return ResponseEntity.noContent().build();
    }
}