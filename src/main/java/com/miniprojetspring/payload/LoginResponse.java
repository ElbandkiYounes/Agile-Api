package com.miniprojetspring.payload;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String token;

    private long expiresIn;

}
