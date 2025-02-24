package com.miniprojetspring.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String name;
    private String description;
    @CreatedDate
    private Date CreatedAt;

}
