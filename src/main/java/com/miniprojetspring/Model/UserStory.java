package com.miniprojetspring.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
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

    @CreationTimestamp
    private LocalDateTime createdAt;
    private Date dueDate;

    @OneToMany(mappedBy = "userStory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TestCase> testCases = Collections.emptyList();

    @ManyToOne
    @Builder.Default
    @JsonIgnore
    private Epic epic = null;

    @ManyToOne
    @JsonIgnore
    private ProductBacklog productBacklog;

    @JsonProperty("productBacklogId")
    public UUID getProductBacklogId() {
        return productBacklog != null ? productBacklog.getId() : null;
    }

    @JsonProperty("epicId")
    public UUID getEpicId() {
        return epic != null ? epic.getId() : null;
    }

}