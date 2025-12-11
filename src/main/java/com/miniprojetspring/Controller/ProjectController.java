package com.miniprojetspring.controller;

import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import com.miniprojetspring.Service.ProjectService;
import com.miniprojetspring.payload.InviteUserPayload;
import com.miniprojetspring.payload.ProjectPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody  ProjectPayload payload) {
        Project project = projectService.createProject(payload);
        return ResponseEntity.ok(project);
    }

    @GetMapping()
    public ResponseEntity<Project> getProjectById() {
        Project project = projectService.getProject();
        return ResponseEntity.ok(project);
    }

    @PutMapping()
    public ResponseEntity<Project> updateProject(@Valid @RequestBody ProjectPayload payload) {
        Project project = projectService.updateProject(payload);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteProject() {
        projectService.deleteProject();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invite")
    public ResponseEntity<User> inviteUser(@RequestBody InviteUserPayload userPayload) {
        User invitedUser = projectService.inviteUser(userPayload);
        return ResponseEntity.ok(invitedUser);
    }

}