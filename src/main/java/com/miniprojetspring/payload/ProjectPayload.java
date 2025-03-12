package com.miniprojetspring.payload;

import com.miniprojetspring.Model.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;

@Getter
@Setter
@Builder
public class ProjectPayload {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;

    public Project toEntity() {
        return Project.builder()
                .name(name)
                .description(description)
                .sprintBacklogs(Collections.emptyList())
                .build();
    }

    public Project toEntity(Project project) {
        project.setName(name);
        project.setDescription(description);
        return project;
    }
}
