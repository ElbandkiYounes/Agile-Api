package com.miniprojetspring.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

    private String name;
    private String description ;

    @OneToOne
    private UserStory userStory;

}
