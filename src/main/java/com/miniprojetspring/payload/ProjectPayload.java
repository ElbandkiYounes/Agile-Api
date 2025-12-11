package com.miniprojetspring.payload;

import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
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

    public Project toEntity(User owner) {
        return Project.builder()
                .name(name)
                .description(description)
                .owner(owner)
                .sprintBacklogs(Collections.emptyList())
                .build();
    }

    public Project toEntity(Project project) {
        project.setName(name);
        project.setDescription(description);
        return project;
    }
}
