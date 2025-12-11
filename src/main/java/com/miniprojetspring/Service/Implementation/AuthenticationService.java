package com.miniprojetspring.Service.Implementation;

import com.miniprojetspring.exception.ConflictException;
import com.miniprojetspring.Model.User;
import com.miniprojetspring.Repository.UserRepository;
import com.miniprojetspring.payload.LoginUserPayload;
import com.miniprojetspring.payload.RegisterUserPayload;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForLocalDate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final PastValidatorForLocalDate pastValidatorForLocalDate;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            PastValidatorForLocalDate pastValidatorForLocalDate) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.pastValidatorForLocalDate = pastValidatorForLocalDate;
    }

    public User signup(RegisterUserPayload payload) {
        if(userRepository.existsByEmail(payload.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        User user = payload.toUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserPayload payload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        payload.getEmail(),
                        payload.getPassword()
                )
        );

        return userRepository.findByEmail(payload.getEmail())
                .orElseThrow();
    }
}
