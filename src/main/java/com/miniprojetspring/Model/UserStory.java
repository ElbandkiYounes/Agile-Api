package com.miniprojetspring.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    @ManyToOne
    private Role role;
    @Column(nullable = false)
    private String goal;
    @Column(nullable = false)
    private String desire;

    @CreatedDate
    private Date createdAt;
    private Date dueDate;

    @OneToMany
    @Builder.Default
    private List<TestCase> testCases = Collections.emptyList();

    @ManyToOne
    private Epic epic;

    @ManyToOne
    private ProductBacklog productBacklog;
}