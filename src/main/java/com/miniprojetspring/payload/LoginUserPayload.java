package com.miniprojetspring.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginUserPayload {
    @Email(message = "Email is not valid")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;

}
