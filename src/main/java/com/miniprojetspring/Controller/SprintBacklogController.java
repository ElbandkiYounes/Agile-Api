package com.miniprojetspring.Controller;

import com.miniprojetspring.Model.SprintBacklog;
import com.miniprojetspring.Service.SprintBacklogService;
import com.miniprojetspring.payload.SprintBacklogPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SprintBacklogController {

    private final SprintBacklogService sprintBacklogService;

    @Autowired
    public SprintBacklogController(SprintBacklogService sprintBacklogService) {
        this.sprintBacklogService = sprintBacklogService;
    }

    @PostMapping("/projects/{projectId}/sprint-backlogs")
    public ResponseEntity<SprintBacklog> createSprintBacklog(@PathVariable String projectId, @Valid @RequestBody SprintBacklogPayload payload) {
        SprintBacklog sprintBacklog = sprintBacklogService.createSprintBacklog(projectId, payload);
        return ResponseEntity.ok(sprintBacklog);
    }

    @GetMapping("/sprint-backlogs/{id}")
    public ResponseEntity<SprintBacklog> getSprintBacklogById(@PathVariable String id) {
        SprintBacklog sprintBacklog = sprintBacklogService.getSprintBacklogById(id);
        return ResponseEntity.ok(sprintBacklog);
    }

    @PutMapping("/sprint-backlogs/{id}")
    public ResponseEntity<SprintBacklog> updateSprintBacklog(@PathVariable String id, @Valid @RequestBody SprintBacklogPayload payload) {
        SprintBacklog sprintBacklog = sprintBacklogService.updateSprintBacklog(id, payload);
        return ResponseEntity.ok(sprintBacklog);
    }

    @DeleteMapping("/sprint-backlogs/{id}")
    public ResponseEntity<Void> deleteSprintBacklog(@PathVariable String id) {
        sprintBacklogService.deleteSprintBacklog(id);
        return ResponseEntity.noContent().build();
    }
}