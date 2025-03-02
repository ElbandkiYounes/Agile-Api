package com.miniprojetspring.payload;

import com.miniprojetspring.Model.*;
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

    public UserStory toEntity(ProductBacklog productBacklog,Role role) {
        return UserStory.builder()
                .title(title)
                .description(description)
                .role(role)
                .goal(goal)
                .desire(desire)
                .priority(userStoryPriority)
                .status(userStoryStatus)
                .productBacklog(productBacklog)
                .build();
    }

    public UserStory toEntity(UserStory userStory,Role role) {
        userStory.setTitle(this.getTitle());
        userStory.setDescription(this.getDescription());
        userStory.setRole(role);
        userStory.setGoal(this.getGoal());
        userStory.setDesire(this.getDesire());
        userStory.setPriority(this.getUserStoryPriority());
        userStory.setStatus(this.getUserStoryStatus());
        return userStory;
    }
}
