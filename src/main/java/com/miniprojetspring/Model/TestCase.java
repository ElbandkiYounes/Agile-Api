package com.miniprojetspring.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestCase {
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
