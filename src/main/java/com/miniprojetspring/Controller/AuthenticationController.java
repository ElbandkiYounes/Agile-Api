package com.miniprojetspring.Controller;

import com.miniprojetspring.Model.User;
import com.miniprojetspring.Service.Implementation.AuthenticationService;
import com.miniprojetspring.Service.Implementation.JwtService;
import com.miniprojetspring.payload.LoginResponse;
import com.miniprojetspring.payload.LoginUserPayload;
import com.miniprojetspring.payload.RegisterUserPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserPayload payload) {
        User registeredUser = authenticationService.signup(payload);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserPayload payload) {
        User authenticatedUser = authenticationService.authenticate(payload);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
