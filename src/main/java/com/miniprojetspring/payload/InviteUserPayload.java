package com.miniprojetspring.payload;

import com.miniprojetspring.model.Previlige;
import com.miniprojetspring.model.Project;
import com.miniprojetspring.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InviteUserPayload {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @Email(message = "Email is not valid")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Previlige is required")
    private Previlige previlige;

    public User toUser(Project project) {
        return User.builder()
                .fullName(fullName)
                .email(email)
                .project(project)
                .previlige(previlige)
                .password(password)
                .build();
    }
}
