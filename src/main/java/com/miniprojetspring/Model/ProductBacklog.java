package com.miniprojetspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBacklog {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Column(nullable = false)
    private String name;
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "project_id")
    private Project project;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Epic> epics = Collections.emptyList();
    @JsonProperty("projectId")
    public UUID getProjectId() {
        return project.getId();
    }
    @PreRemove
    private void preRemove() {
        if (this.project != null) {
            this.project.setProductBacklog(null);
        }
    }
}
