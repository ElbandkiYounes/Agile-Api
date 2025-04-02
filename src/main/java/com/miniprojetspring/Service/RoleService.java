package com.miniprojetspring.Service;

import com.miniprojetspring.Model.Role;
import com.miniprojetspring.payload.RolePayload;

import java.util.List;

public interface RoleService {
    Role getRoleByNameAndProjectId(String name);
    Role createRole(RolePayload rolePayload);
    Role updateRole(String roleId, RolePayload payload);
    List<Role> getRoles();
    Role getRoleById(String id);
    void deleteRole(String id);
}
