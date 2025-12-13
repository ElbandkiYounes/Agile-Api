package com.miniprojetspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Epic implements Serializable {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    private EpicPriority priority;
    @Enumerated(EnumType.STRING)
    private EpicStatus status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Date dueDate;

    @ManyToOne
    @JsonIgnore
    private ProductBacklog productBacklog;

    @ManyToOne
    @Builder.Default
    @JsonIgnore
    private SprintBacklog sprintBacklog = null;

    @OneToMany(mappedBy = "epic", fetch = FetchType.EAGER)
    @Builder.Default
    @JsonIgnore
    private List<UserStory> userStories = new ArrayList<>();


    @JsonProperty("productBacklogId")
    public UUID getProductBacklogId() {
        return productBacklog.getId();
    }

    @JsonProperty("sprintBacklogId")
    public UUID getSprintBacklogId() {
        return sprintBacklog != null ? sprintBacklog.getId() : null;
    }

    @JsonProperty("userStoryIds")
    public List<UUID> getUserStoryIds() {
        return userStories.stream()
                .map(UserStory::getId)
                .toList();
    }

    @PreRemove
    private void preRemove() {
        if (sprintBacklog != null) {
            sprintBacklog.getEpics().remove(this);
        }
        if(productBacklog != null) {
            productBacklog.getEpics().remove(this);
        }

    }
}
