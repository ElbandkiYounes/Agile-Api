package com.miniprojetspring.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "name"})
})
public class Role {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @JsonProperty("projectId")
    public UUID getProjectId() {
        return project.getId();
    }

    @PreRemove
    private void removeRoleFromProject() {
        project.getRoles().remove(this);
    }

}
