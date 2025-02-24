package com.miniprojetspring.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBacklog {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    @OneToOne
    private Project project;
    @OneToMany
    @Builder.Default
    private List<Epic> epics = Collections.emptyList();
}
