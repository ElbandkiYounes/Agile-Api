package com.miniprojetspring.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.springframework.context.annotation.EnableMBeanExport;

import java.util.Collection;
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
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String name;
    private String description ;

    @OneToOne
    private Project project;

    @OneToMany
    @Builder.Default
    private List<UserStory> userStories = Collections.emptyList();

    @OneToMany
    @Builder.Default
    private List<Epic> epics =  Collections.emptyList();

}
