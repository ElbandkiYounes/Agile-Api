package com.miniprojetspring.Controller;

import com.miniprojetspring.Model.Role;
import com.miniprojetspring.Service.RoleService;
import com.miniprojetspring.payload.RolePayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/projects/{projectId}/roles")
    public ResponseEntity<Role> createRole(@PathVariable String projectId, @Valid @RequestBody RolePayload payload) {
        Role role = roleService.createRole(projectId, payload);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/projects/{projectId}/roles/{roleId}")
    public ResponseEntity<Role> updateRole(@PathVariable String projectId, @PathVariable String roleId, @Valid @RequestBody RolePayload payload) {
        Role role = roleService.updateRole(projectId, roleId, payload);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}/roles")
    public ResponseEntity<List<Role>> getRolesByProjectId(@PathVariable String projectId) {
        List<Role> roles = roleService.getRolesByProjectId(projectId);
        return ResponseEntity.ok(roles);
    }
}