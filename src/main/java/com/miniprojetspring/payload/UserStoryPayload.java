package com.miniprojetspring.payload;

import com.miniprojetspring.Model.UserStoryPriority;
import com.miniprojetspring.Model.UserStoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStoryPayload {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotBlank(message = "Product Backlog ID is required")
    private String productBacklogId;

    @NotBlank(message = "Role Name is required")
    private String roleName;

    @NotBlank(message = "Goal cannot be blank")
    private String goal;

    @NotBlank(message = "Desire cannot be blank")
    private String desire;

    @NotNull(message = "Priority is required")
    private UserStoryPriority userStoryPriority;

    @NotNull(message = "Status is required")
    private UserStoryStatus userStoryStatus;
}
