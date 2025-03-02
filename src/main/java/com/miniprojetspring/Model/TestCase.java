package com.miniprojetspring.Model;

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
    private UserStory userStory;

}
