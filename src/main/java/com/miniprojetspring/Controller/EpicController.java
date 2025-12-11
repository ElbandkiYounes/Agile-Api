package com.miniprojetspring.controller;

import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Service.EpicService;
import com.miniprojetspring.payload.EpicPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/epics")
public class EpicController {

    private final EpicService epicService;

    @Autowired
    public EpicController(EpicService epicService) {
        this.epicService = epicService;
    }

    @PostMapping()
    public ResponseEntity<Epic> createEpic(@Valid @RequestBody EpicPayload payload) {
        Epic epic = epicService.createEpic(payload);
        return ResponseEntity.ok(epic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Epic> getEpicById(@PathVariable String id) {
        Epic epic = epicService.getEpicById(id);
        return ResponseEntity.ok(epic);
    }

    @GetMapping()
    public ResponseEntity<List<Epic>> getEpicsByProductBacklogId() {
        List<Epic> epics = epicService.getEpics();
        return ResponseEntity.ok(epics);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpic(@PathVariable String id) {
        epicService.deleteEpic(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Epic> updateEpic(@PathVariable String id, @Valid @RequestBody EpicPayload payload) {
        Epic epic = epicService.updateEpic(id, payload);
        return ResponseEntity.ok(epic);
    }

    @PostMapping("/{epicId}/link/sprint-backlog/{sprintBacklogId}")
    public ResponseEntity<Epic> linkEpicToSprintBacklog(@PathVariable String sprintBacklogId, @PathVariable String epicId) {
        Epic epic = epicService.linkEpicToSprintBacklog(sprintBacklogId, epicId);
        return ResponseEntity.ok(epic);
    }

    @PostMapping("/{epicId}/unlink")
    public ResponseEntity<Epic> unlinkEpicToSprintBacklog(@PathVariable String epicId) {
        Epic epic = epicService.unlinkEpicToSprintBacklog(epicId);
        return ResponseEntity.ok(epic);
    }
}