package com.miniprojetspring.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    @ManyToOne
    private Project project;

}
