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
public class UpdateEpicPayload {

    @NotBlank(message = "name is required (Cannot be blank)")
    @NotNull(message = "name is required (Cannot be null)")
    private String name;

    private String description;

    @NotNull(message = "Priority is required")
    private EpicPriority epicPriority;

    @NotNull(message = "Status is required")
    private EpicStatus epicStatus;

    private Date dueDate;

    public Epic toEntity(Epic epic) {
        epic.setName(this.getName());
        epic.setDescription(this.getDescription());
        epic.setPriority(this.getEpicPriority());
        epic.setStatus(this.getEpicStatus());
        epic.setDueDate(this.getDueDate());
        return epic;
    }
}
