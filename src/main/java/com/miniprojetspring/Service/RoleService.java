package com.miniprojetspring.Service;

import com.miniprojetspring.Model.Role;
import com.miniprojetspring.payload.RolePayload;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role getRoleByNameAndProjectId(String name, String projectId);
    Role createRole(String projectId, RolePayload rolePayload);
    Role updateRole(String projectId, String roleId, RolePayload payload);
    List<Role> getRolesByProjectId(String projectId);
    Role getRoleById(String id);
    void deleteRole(String id);
}
