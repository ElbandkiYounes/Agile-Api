package com.miniprojetspring.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserStory {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    private UserStoryPriority priority;
    @Enumerated(EnumType.STRING)
    private UserStoryStatus status;

    @CreatedDate
    private Date createdAt;
    private Date dueDate;


    @ManyToOne
    private Epic epic ;

    @ManyToOne
    private ProductBacklog productBacklog;
}