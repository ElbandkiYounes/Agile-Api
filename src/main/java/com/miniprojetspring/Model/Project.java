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
public class Project {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    @OneToOne
    private ProductBacklog productBacklog;
    @CreatedDate
    private Date CreatedAt;
}
