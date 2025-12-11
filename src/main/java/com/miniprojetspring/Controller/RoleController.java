package com.miniprojetspring.controller;

import com.miniprojetspring.model.Role;
import com.miniprojetspring.service.RoleService;
import com.miniprojetspring.payload.RolePayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping()
    public ResponseEntity<Role> createRole(@Valid @RequestBody RolePayload payload) {
        Role role = roleService.createRole(payload);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable String id, @Valid @RequestBody RolePayload payload) {
        Role role = roleService.updateRole(id, payload);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<Role>> getRolesByProjectId() {
        List<Role> roles = roleService.getRoles();
        return ResponseEntity.ok(roles);
    }
}