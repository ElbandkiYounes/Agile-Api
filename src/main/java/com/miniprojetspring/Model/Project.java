package com.miniprojetspring.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ProductBacklog productBacklog;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SprintBacklog> sprintBacklogs;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Role> roles = Collections.emptyList();
    @CreationTimestamp
    private LocalDateTime CreatedAt;

    @JsonProperty("productBacklogId")
    public UUID getProductBacklogId() {
        return productBacklog != null ? productBacklog.getId() : null;
    }

    @JsonProperty("sprintBacklogIds")
    public List<UUID> getSprintBacklogIds() {
        return sprintBacklogs != null ? sprintBacklogs.stream()
                .map(SprintBacklog::getId)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

}
