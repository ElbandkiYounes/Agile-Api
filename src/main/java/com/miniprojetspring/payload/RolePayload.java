package com.miniprojetspring.payload;

import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.Role;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePayload {
    @NotBlank(message = "name is required (Cannot be blank)")
    private String name;
    @NotBlank(message = "name is required (Cannot be blank)")
    private String description;

    public Role toEntity(Project project){
        return Role.builder()
                .name(name)
                .description(description)
                .project(project)
                .build();
    }

    public  Role toEntity(Role role){
        role.setName(this.getName());
        role.setDescription(this.getDescription());
        return role;
    }

}
