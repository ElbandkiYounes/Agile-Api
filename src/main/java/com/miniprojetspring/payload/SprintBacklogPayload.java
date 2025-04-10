package com.miniprojetspring.payload;

import com.miniprojetspring.Model.Project;
import com.miniprojetspring.Model.SprintBacklog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SprintBacklogPayload {
    @NotBlank(message = "name is required (Cannot be blank)")
    private String name;

    @NotBlank(message = "description is required (Cannot be blank)")
    private String description;

    public SprintBacklog toEntity(Project project) {
        return SprintBacklog.builder()
                .name(name)
                .description(description)
                .project(project)
                .build();
    }

    public SprintBacklog toEntity(SprintBacklog sprintBacklog) {
        sprintBacklog.setName(this.getName());
        sprintBacklog.setDescription(this.getDescription());
        return sprintBacklog;
    }
}
