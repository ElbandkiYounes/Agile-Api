package com.miniprojetspring.payload;

import com.miniprojetspring.Model.Previlige;
import com.miniprojetspring.Model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterUserPayload {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @Email(message = "Email is not valid")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;

    public User toUser() {
        return User.builder()
                .fullName(fullName)
                .email(email)
                .password(password)
                .previlige(Previlige.PRODUCT_OWNER)
                .build();
    }

}
