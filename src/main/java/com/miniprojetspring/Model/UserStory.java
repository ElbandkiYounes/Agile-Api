package com.miniprojetspring.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    UUID id;
    @Column(nullable = false)
    String title;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    UserStoryPriority priority;
    @Enumerated(EnumType.STRING)
    UserStoryStatus status;

    @CreatedDate
    private Date createdAt;

    @ManyToOne
    private Epic epic;

    @ManyToOne
    private ProductBacklog productBacklog;


}
