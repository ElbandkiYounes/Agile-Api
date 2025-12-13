package com.miniprojetspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestCase implements Serializable {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String title;
    private String description ;

    @Enumerated(EnumType.STRING)
    private TestCaseResult result;

    @ManyToOne
    @JsonIgnore
    private UserStory userStory;
}
