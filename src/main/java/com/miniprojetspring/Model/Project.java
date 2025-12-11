package com.miniprojetspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ProductBacklog productBacklog;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<SprintBacklog> sprintBacklogs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonProperty("productBacklogId")
    public UUID getProductBacklogId() {
        return productBacklog != null ? productBacklog.getId() : null;
    }

    @JsonProperty("sprintBacklogIds")
    public List<UUID> getSprintBacklogIds() {
        return sprintBacklogs != null ? sprintBacklogs.stream()
                .map(SprintBacklog::getId)
                .toList() : Collections.emptyList();
    }

    @PreRemove
    private void preRemove() {
        if (owner != null) {
            owner.setProject(null);
        }
    }

}