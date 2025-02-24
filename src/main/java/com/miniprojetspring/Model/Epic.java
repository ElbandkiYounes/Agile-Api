package com.miniprojetspring.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Epic {
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
    @CreatedDate
    private Date createdAt;
    private Date dueDate;

    @ManyToOne
    private ProductBacklog productBacklog;

}
