package com.miniprojetspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SprintBacklog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String description ;

    @ManyToOne
    @JsonIgnore
    private Project project;

    @OneToMany
    @Builder.Default
    @JsonIgnore
    private List<UserStory> userStories = Collections.emptyList();

    @OneToMany
    @Builder.Default
    @JsonIgnore
    private List<Epic> epics =  Collections.emptyList();

    @JsonProperty("projectId")
    public UUID getProjectId() {
        return project.getId();
    }

    @JsonProperty("userStorieIds")
    public List<UUID> getUserStoriesId() {
        return userStories.stream().map(UserStory::getId).toList();
    }

    @JsonProperty("epicIds")
    public List<UUID> getEpicsId() {
        return epics.stream().map(Epic::getId).toList();
    }
}
