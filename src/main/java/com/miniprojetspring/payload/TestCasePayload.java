package com.miniprojetspring.payload;

import com.miniprojetspring.Model.TestCase;
import com.miniprojetspring.Model.UserStory;
import com.miniprojetspring.Model.TestCaseResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestCasePayload {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Test case result is required")
    private TestCaseResult result;

    public TestCase toEntity(UserStory userStory) {
        return TestCase.builder()
                .title(title)
                .description(description)
                .result(result)
                .userStory(userStory)
                .build();
    }

    public TestCase toEntity(TestCase testCase) {
        testCase.setTitle(this.title);
        testCase.setDescription(this.description);
        testCase.setResult(this.result);
        return testCase;
    }
}
