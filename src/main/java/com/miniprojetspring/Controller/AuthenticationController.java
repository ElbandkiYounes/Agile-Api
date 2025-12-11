package com.miniprojetspring.controller;

import com.miniprojetspring.model.User;
import com.miniprojetspring.Service.Implementation.AuthenticationService;
import com.miniprojetspring.Service.Implementation.JwtService;
import com.miniprojetspring.Service.Implementation.ProjectSecurityService;
import com.miniprojetspring.payload.LoginResponse;
import com.miniprojetspring.payload.LoginUserPayload;
import com.miniprojetspring.payload.RegisterUserPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping()
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final ProjectSecurityService  projectSecurityService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, ProjectSecurityService projectSecurityService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.projectSecurityService = projectSecurityService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserPayload payload) {
        User registeredUser = authenticationService.signup(payload);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserPayload payload) {
        User authenticatedUser = authenticationService.authenticate(payload);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/api/me")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = projectSecurityService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
}
