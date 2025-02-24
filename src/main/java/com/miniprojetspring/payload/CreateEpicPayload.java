package com.miniprojetspring.payload;

import com.miniprojetspring.Model.Epic;
import com.miniprojetspring.Model.EpicPriority;
import com.miniprojetspring.Model.EpicStatus;
import com.miniprojetspring.Model.ProductBacklog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateEpicPayload {

    @NotBlank(message = "name is required (Cannot be blank)")
    @NotNull(message = "name is required (Cannot be null)")
    private String name;

    private String description;

    @NotBlank(message = "productBacklogId is required (Cannot be blank)")
    @NotNull(message = "productBacklogId is required (Cannot be null)")
    private String productBacklogId;

    @NotNull(message = "Priority is required")
    private EpicPriority epicPriority;

    @NotNull(message = "Status is required")
    private EpicStatus epicStatus;

    private Date dueDate;

    public Epic toEntity(ProductBacklog productBacklog) {
        return Epic.builder()
                .name(name)
                .description(description)
                .productBacklog(productBacklog)
                .priority(epicPriority)
                .status(epicStatus)
                .dueDate(dueDate)
                .build();
    }
}
