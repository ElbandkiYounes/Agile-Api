package com.miniprojetspring.payload;

import com.miniprojetspring.Model.TestCase;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Model.TestCaseResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCasePayload {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Test case result is required")
    private TestCaseResult result;

    @NotNull(message = "UserStory ID is required")
    private String userStoryId;

    public TestCase toEntity(UserStory userStory) {
        return TestCase.builder()
                .title(title)
                .description(description)
                .result(result)
                .userStory(userStory)
                .build();
    }

    public TestCase toEntity(TestCase testCase, UserStory userStory) {
        testCase.setTitle(this.title);
        testCase.setDescription(this.description);
        testCase.setResult(this.result);
        testCase.setUserStory(userStory);
        return testCase;
    }
}
